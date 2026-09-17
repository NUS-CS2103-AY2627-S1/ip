#!/usr/bin/env python3
"""Run fail-fast, command-by-command tests for a console application."""

from __future__ import annotations

import argparse
import json
import queue
import re
import shutil
import subprocess
import sys
import threading
import time
from dataclasses import dataclass
from pathlib import Path
from typing import TextIO


@dataclass
class TestFailure(Exception):
    """Details of the first UI test failure."""

    case_id: str
    step_number: int
    command: str
    expected: str
    actual: str
    reason: str
    transcript: str


class StreamReader:
    """Read a process stream asynchronously so prompts can be timed out."""

    def __init__(self, stream: TextIO) -> None:
        self.items: queue.Queue[str | None] = queue.Queue()
        self.thread = threading.Thread(target=self._read, args=(stream,), daemon=True)
        self.thread.start()

    def _read(self, stream: TextIO) -> None:
        while True:
            character = stream.read(1)
            if character == "":
                break
            self.items.put(character)
        self.items.put(None)

    def read_until(self, marker: str | None, timeout_seconds: float) -> tuple[str, bool]:
        """Return text before a marker, or all text through end-of-stream."""
        deadline = time.monotonic() + timeout_seconds
        output = ""
        while True:
            remaining = deadline - time.monotonic()
            if remaining <= 0:
                raise TimeoutError("timed out while waiting for console output")
            try:
                item = self.items.get(timeout=remaining)
            except queue.Empty as error:
                raise TimeoutError("timed out while waiting for console output") from error
            if item is None:
                return output, False
            output += item
            if marker is not None and output.endswith(marker):
                return output[:-len(marker)], True


def normalize(text: str) -> str:
    """Normalize platform line endings and ignore only final newlines."""
    return text.replace("\r\n", "\n").replace("\r", "\n").rstrip("\n")


def load_plan(path: Path) -> dict:
    """Load and validate the JSON test definition embedded in a Markdown plan."""
    markdown = path.read_text(encoding="utf-8")
    match = re.search(r"```json\s*(\{.*?\})\s*```", markdown, re.DOTALL)
    if match is None:
        raise ValueError(f"No JSON test definition found in {path}")
    plan = json.loads(match.group(1))
    if not isinstance(plan.get("test_cases"), list) or not plan["test_cases"]:
        raise ValueError("The test plan must contain a non-empty test_cases list")
    for case in plan["test_cases"]:
        if not all(key in case for key in ("id", "aim", "exchanges")):
            raise ValueError("Every test case must specify id, aim, and exchanges")
        if not isinstance(case["exchanges"], list) or not case["exchanges"]:
            raise ValueError(f"{case['id']} must contain at least one exchange")
        for exchange in case["exchanges"]:
            if "input" not in exchange or "expected_output" not in exchange:
                raise ValueError(f"Every exchange in {case['id']} needs input and expected_output")
    return plan


def expand_expected(lines: list[str] | str, variables: dict[str, str]) -> str:
    """Join expected lines and expand declared {{VARIABLE}} placeholders."""
    expected = "\n".join(lines) if isinstance(lines, list) else lines
    for name, value in variables.items():
        expected = expected.replace("{{" + name + "}}", value)
    return normalize(expected)


def expand_file_content(lines: list[str] | str, variables: dict[str, str]) -> str:
    """Build exact fixture or expected file content from plan lines."""
    content = "\n".join(lines) + ("\n" if lines else "") if isinstance(lines, list) else lines
    for name, value in variables.items():
        content = content.replace("{{" + name + "}}", value)
    return content


def prepare_case_directory(project_root: Path, plan: dict, case: dict,
                           variables: dict[str, str]) -> Path:
    """Create an isolated working directory and its fixtures for one test case."""
    working_root = project_root / plan["program"]["working_dir"]
    case_directory = working_root / case["id"]
    if case_directory.exists():
        shutil.rmtree(case_directory)
    case_directory.mkdir(parents=True)
    for relative_path, content in case.get("initial_files", {}).items():
        path = case_directory / relative_path
        path.parent.mkdir(parents=True, exist_ok=True)
        path.write_text(expand_file_content(content, variables), encoding="utf-8")
    return case_directory


def verify_expected_files(project_root: Path, case: dict, variables: dict[str, str],
                          transcript: str) -> None:
    """Compare files produced by a test case with their exact expected content."""
    for relative_path, content in case.get("expected_files", {}).items():
        path = project_root / relative_path
        expected = expand_file_content(content, variables)
        actual = path.read_text(encoding="utf-8") if path.exists() else "<missing file>"
        if actual != expected:
            raise TestFailure(
                case["id"], len(case["exchanges"]) + 1,
                f"<saved file: {relative_path}>", expected, actual,
                "saved file did not match expected content", transcript,
            )


def ensure_java_25() -> None:
    """Fail clearly when the active compiler is not Java 25."""
    result = subprocess.run(["javac", "-version"], capture_output=True, text=True)
    version_output = normalize(result.stdout + result.stderr)
    if result.returncode != 0 or re.search(r"\bjavac 25(?:\.|\b)", version_output) is None:
        raise RuntimeError(
            "Java 25 is required. Run `sdk use java 25.0.3.fx-zulu` before this script. "
            f"Active compiler: {version_output or 'unavailable'}"
        )


def compile_program(project_root: Path, program: dict) -> Path:
    """Compile all configured Java sources into a clean generated directory."""
    source_dir = project_root / program["source_dir"]
    classes_dir = project_root / program["classes_dir"]
    sources = sorted(source_dir.rglob("*.java"))
    if not sources:
        raise RuntimeError(f"No Java source files found under {source_dir}")
    if classes_dir.exists():
        shutil.rmtree(classes_dir)
    classes_dir.mkdir(parents=True)
    result = subprocess.run(
        ["javac", "-d", str(classes_dir), *map(str, sources)],
        capture_output=True,
        text=True,
    )
    if result.returncode != 0:
        details = normalize(result.stdout + result.stderr)
        raise RuntimeError(f"Compilation failed:\n{details}")
    return classes_dir


def stop_process(process: subprocess.Popen[str]) -> None:
    """Stop a test process promptly after a failure or incomplete case."""
    if process.poll() is not None:
        return
    process.terminate()
    try:
        process.wait(timeout=1)
    except subprocess.TimeoutExpired:
        process.kill()
        process.wait()


def run_case(case: dict, prompt: str, variables: dict[str, str],
             java_command: list[str], timeout_seconds: float,
             working_directory: Path) -> str:
    """Run one stateful test case and compare each response before continuing."""
    process = subprocess.Popen(
        java_command,
        stdin=subprocess.PIPE,
        stdout=subprocess.PIPE,
        stderr=subprocess.PIPE,
        text=True,
        bufsize=1,
        cwd=working_directory,
    )
    assert process.stdin is not None
    assert process.stdout is not None
    assert process.stderr is not None
    stdout_reader = StreamReader(process.stdout)
    stderr_chunks: list[str] = []
    stderr_thread = threading.Thread(
        target=lambda: stderr_chunks.append(process.stderr.read()), daemon=True
    )
    stderr_thread.start()

    transcript = ""
    try:
        startup, found_prompt = stdout_reader.read_until(prompt, timeout_seconds)
        transcript = normalize(startup) + "\n" + prompt
        if not found_prompt:
            raise TestFailure(
                case["id"], 0, "<startup>", prompt, normalize(startup),
                "program exited before displaying its input prompt", transcript,
            )
        if "expected_startup_output" in case:
            expected_startup = expand_expected(case["expected_startup_output"], variables)
            actual_startup = normalize(startup)
            if actual_startup != expected_startup:
                raise TestFailure(
                    case["id"], 0, "<startup>", expected_startup, actual_startup,
                    "startup output did not match expected output", transcript,
                )

        for step_number, exchange in enumerate(case["exchanges"], start=1):
            command = exchange["input"]
            transcript += command + "\n"
            process.stdin.write(command + "\n")
            process.stdin.flush()

            expect_exit = bool(exchange.get("expect_exit", False))
            actual, next_prompt_found = stdout_reader.read_until(
                None if expect_exit else prompt, timeout_seconds
            )
            actual = normalize(actual)
            transcript += actual + ("\n" if actual else "")
            if next_prompt_found:
                transcript += prompt

            expected = expand_expected(exchange["expected_output"], variables)
            if actual != expected:
                raise TestFailure(
                    case["id"], step_number, command, expected, actual,
                    "actual output did not match expected output", transcript,
                )
            if expect_exit:
                process.wait(timeout=timeout_seconds)
                if process.returncode != 0:
                    stderr_thread.join(timeout=0.2)
                    raise TestFailure(
                        case["id"], step_number, command, expected, actual,
                        f"program exited with code {process.returncode}: "
                        + normalize("".join(stderr_chunks)), transcript,
                    )
            elif not next_prompt_found:
                stderr_thread.join(timeout=0.2)
                raise TestFailure(
                    case["id"], step_number, command, expected, actual,
                    "program exited instead of requesting the next command: "
                    + normalize("".join(stderr_chunks)), transcript,
                )
    except TimeoutError as error:
        raise TestFailure(
            case["id"], 0, "<timeout>", "timely console response", "",
            str(error), transcript,
        ) from error
    finally:
        stop_process(process)

    return transcript.rstrip()


def session_markdown(records: list[tuple[dict, str]], failure: TestFailure | None) -> str:
    """Build a readable record of all console interaction completed so far."""
    lines = ["# UI Test Session", ""]
    for case, transcript in records:
        lines.extend([
            f"## {case['id']}: {case['aim']}", "", "```text", transcript, "```", "",
        ])
    if failure is not None:
        lines.extend([
            f"## FAILED: {failure.case_id}, step {failure.step_number}", "",
            f"Input: `{failure.command}`", "", f"Reason: {failure.reason}", "",
            "### Expected output", "", "```text", failure.expected, "```", "",
            "### Actual output", "", "```text", failure.actual, "```", "",
        ])
    else:
        lines.extend([f"Result: PASS ({len(records)} test cases)", ""])
    return "\n".join(lines)


def main(argv: list[str]) -> int:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--plan", default="test/ui-test-plan.md")
    parser.add_argument("--transcript", default="test/ui-test-session.md")
    args = parser.parse_args(argv)

    project_root = Path.cwd()
    plan_path = project_root / args.plan
    transcript_path = project_root / args.transcript

    try:
        plan = load_plan(plan_path)
        ensure_java_25()
        classes_dir = compile_program(project_root, plan["program"])
    except (OSError, ValueError, RuntimeError, json.JSONDecodeError) as error:
        print(f"ERROR: {error}", file=sys.stderr)
        return 2

    prompt = plan.get("prompt", "You: ")
    variables = plan.get("variables", {})
    timeout_seconds = float(plan.get("timeout_seconds", 5))
    java_command = ["java", "-cp", str(classes_dir), plan["program"]["main_class"]]
    records: list[tuple[dict, str]] = []

    for case in plan["test_cases"]:
        try:
            case_directory = prepare_case_directory(project_root, plan, case, variables)
            transcript = run_case(
                case, prompt, variables, java_command, timeout_seconds, case_directory
            )
            verify_expected_files(case_directory, case, variables, transcript)
            records.append((case, transcript))
        except TestFailure as failure:
            records.append((case, failure.transcript.rstrip()))
            session = session_markdown(records, failure)
            transcript_path.parent.mkdir(parents=True, exist_ok=True)
            transcript_path.write_text(session, encoding="utf-8")
            print(session)
            print(
                f"FAIL: {failure.case_id}, step {failure.step_number}, input {failure.command!r}",
                file=sys.stderr,
            )
            print(f"EXPECTED:\n{failure.expected}", file=sys.stderr)
            print(f"ACTUAL:\n{failure.actual}", file=sys.stderr)
            return 1

    session = session_markdown(records, None)
    transcript_path.parent.mkdir(parents=True, exist_ok=True)
    transcript_path.write_text(session, encoding="utf-8")
    print(session)
    return 0


if __name__ == "__main__":
    raise SystemExit(main(sys.argv[1:]))
