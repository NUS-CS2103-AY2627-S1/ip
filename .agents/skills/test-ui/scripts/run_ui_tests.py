#!/usr/bin/env python3
"""Run fail-fast console UI tests declared in test/ui-test-plan.md."""

from __future__ import annotations

import argparse
import json
import queue
import re
import subprocess
import sys
import tempfile
import threading
from pathlib import Path
from typing import TextIO


PLAN_PATTERN = re.compile(
    r"<!--\s*test-ui-plan\s*-->\s*```json\s*(.*?)\s*```",
    re.DOTALL,
)


class UiTestError(RuntimeError):
    """Raised when the test plan or program protocol is invalid."""


def load_plan(path: Path) -> dict:
    """Load and validate the JSON test-plan block from a Markdown file."""
    match = PLAN_PATTERN.search(path.read_text(encoding="utf-8"))
    if not match:
        raise UiTestError(
            f"{path} must contain a JSON block after '<!-- test-ui-plan -->'"
        )

    try:
        plan = json.loads(match.group(1))
    except json.JSONDecodeError as error:
        raise UiTestError(f"invalid JSON in {path}: {error}") from error

    required = ("source_directory", "main_class", "separator", "test_cases")
    missing = [key for key in required if key not in plan]
    if missing:
        raise UiTestError(f"test plan is missing: {', '.join(missing)}")
    if not isinstance(plan["test_cases"], list) or not plan["test_cases"]:
        raise UiTestError("test_cases must be a non-empty list")

    seen_ids: set[str] = set()
    for case in plan["test_cases"]:
        for key in ("id", "aim", "inputs", "expected_outputs"):
            if key not in case:
                raise UiTestError(f"a test case is missing '{key}'")
        if case["id"] in seen_ids:
            raise UiTestError(f"duplicate test case id: {case['id']}")
        seen_ids.add(case["id"])
        if len(case["inputs"]) != len(case["expected_outputs"]):
            raise UiTestError(
                f"{case['id']}: inputs and expected_outputs must have equal lengths"
            )
        for output in case["expected_outputs"]:
            if not isinstance(output, list) or not all(
                isinstance(line, str) for line in output
            ):
                raise UiTestError(
                    f"{case['id']}: each expected output must be a list of lines"
                )
    return plan


def compile_project(repo: Path, source_directory: str, classes: Path) -> None:
    """Compile every Java source file into the temporary classes directory."""
    sources = sorted((repo / source_directory).rglob("*.java"))
    if not sources:
        raise UiTestError(f"no Java files found under {source_directory}")

    version = subprocess.run(
        ["javac", "-version"], cwd=repo, capture_output=True, text=True
    )
    version_text = (version.stdout or version.stderr).strip()
    if version.returncode != 0:
        raise UiTestError(version_text or "could not run javac")
    if not version_text.startswith("javac 25"):
        raise UiTestError(f"Java 25 is required, but found: {version_text}")

    result = subprocess.run(
        ["javac", "-d", str(classes), *map(str, sources)],
        cwd=repo,
        capture_output=True,
        text=True,
    )
    if result.returncode != 0:
        details = "\n".join(part for part in (result.stdout, result.stderr) if part)
        raise UiTestError(f"compilation failed:\n{details.rstrip()}")
    print(f"Compiled {len(sources)} Java source file(s) with {version_text}.")


def pump_output(stream: TextIO, output_queue: queue.Queue[str | None]) -> None:
    """Copy process output lines into a queue without blocking the main thread."""
    for line in stream:
        output_queue.put(line.rstrip("\r\n"))
    output_queue.put(None)


def read_block(
    output_queue: queue.Queue[str | None],
    separator: str,
    timeout_seconds: float,
) -> list[str]:
    """Read one program output block bounded by two separator lines."""
    lines: list[str] = []
    separators_seen = 0
    while separators_seen < 2:
        try:
            line = output_queue.get(timeout=timeout_seconds)
        except queue.Empty as error:
            raise UiTestError(
                f"timed out after {timeout_seconds:g}s waiting for program output"
            ) from error
        if line is None:
            raise UiTestError("program exited before completing its output block")
        lines.append(line)
        if line == separator:
            separators_seen += 1
    return lines


def print_transcript(case: dict, transcript: list[tuple[str, list[str] | str]]) -> None:
    """Print the console inputs and outputs recorded for one test session."""
    print(f"\n=== Console session: {case['id']} ===")
    print(f"Aim: {case['aim']}")
    for kind, value in transcript:
        if kind == "input":
            print(f"> {value}")
        else:
            for line in value:
                print(line)
    print("=== End console session ===")


def stop_process(process: subprocess.Popen[str]) -> None:
    """Terminate a process that is still running."""
    if process.poll() is None:
        process.terminate()
        try:
            process.wait(timeout=2)
        except subprocess.TimeoutExpired:
            process.kill()
            process.wait()


def run_case(
    run_directory: Path,
    classes: Path,
    main_class: str,
    separator: str,
    timeout_seconds: float,
    case: dict,
) -> tuple[bool, int]:
    """Run one test case and stop at its first mismatched command response."""
    process = subprocess.Popen(
        ["java", "-cp", str(classes), main_class],
        cwd=run_directory,
        stdin=subprocess.PIPE,
        stdout=subprocess.PIPE,
        stderr=subprocess.STDOUT,
        text=True,
        bufsize=1,
    )
    assert process.stdin is not None
    assert process.stdout is not None

    output_queue: queue.Queue[str | None] = queue.Queue()
    reader = threading.Thread(
        target=pump_output, args=(process.stdout, output_queue), daemon=True
    )
    reader.start()

    transcript: list[tuple[str, list[str] | str]] = []
    commands_checked = 0
    try:
        startup = read_block(output_queue, separator, timeout_seconds)
        transcript.append(("output", startup))

        for index, command in enumerate(case["inputs"]):
            transcript.append(("input", command))
            process.stdin.write(command + "\n")
            process.stdin.flush()

            block = read_block(output_queue, separator, timeout_seconds)
            transcript.append(("output", block))
            actual = block[1:-1]
            expected = case["expected_outputs"][index]
            if actual != expected:
                stop_process(process)
                print(f"\nFAIL: {case['id']} command {index + 1}: {command}")
                print("\nExpected output:")
                print("\n".join(expected) if expected else "<no output>")
                print("\nActual output:")
                print("\n".join(actual) if actual else "<no output>")
                print_transcript(case, transcript)
                return False, commands_checked
            commands_checked += 1
    except (BrokenPipeError, UiTestError) as error:
        stop_process(process)
        print(f"\nFAIL: {case['id']}: {error}")
        print_transcript(case, transcript)
        return False, commands_checked
    finally:
        if process.stdin and not process.stdin.closed:
            process.stdin.close()

    stop_process(process)
    print_transcript(case, transcript)
    print(f"PASS: {case['id']} ({commands_checked} command(s))")
    return True, commands_checked


def main(argv: list[str]) -> int:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument(
        "plan", nargs="?", default="test/ui-test-plan.md", help="test-plan path"
    )
    args = parser.parse_args(argv)

    repo = Path.cwd()
    plan_path = (repo / args.plan).resolve()
    try:
        plan = load_plan(plan_path)
        timeout_seconds = float(plan.get("response_timeout_seconds", 5))
        with tempfile.TemporaryDirectory(prefix="test-ui-") as temp_directory:
            classes = Path(temp_directory) / "classes"
            classes.mkdir()
            compile_project(repo, plan["source_directory"], classes)

            total_commands = 0
            sessions = Path(temp_directory) / "sessions"
            sessions.mkdir()
            for index, case in enumerate(plan["test_cases"]):
                run_directory = sessions / str(index)
                run_directory.mkdir()
                passed, checked = run_case(
                    run_directory,
                    classes,
                    plan["main_class"],
                    plan["separator"],
                    timeout_seconds,
                    case,
                )
                total_commands += checked
                if not passed:
                    return 1
    except (OSError, ValueError, UiTestError) as error:
        print(f"ERROR: {error}", file=sys.stderr)
        return 2

    print(
        f"\nPASS: {len(plan['test_cases'])} test case(s), "
        f"{total_commands} command(s)."
    )
    return 0


if __name__ == "__main__":
    raise SystemExit(main(sys.argv[1:]))
