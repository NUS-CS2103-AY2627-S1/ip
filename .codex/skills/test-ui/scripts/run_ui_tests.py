#!/usr/bin/env python3
"""Run exact, command-by-command console UI tests for the Ruby chatbot."""

from __future__ import annotations

import argparse
import queue
import subprocess
import sys
import tempfile
import threading
import time
from dataclasses import dataclass
from pathlib import Path


DIVIDER = "____________________________________________________________"
DEFAULT_TIMEOUT_SECONDS = 5.0
MAIN_CLASS = "ruby.Ruby"


@dataclass
class TestStep:
    command: str
    expected_output: str


@dataclass
class TestCase:
    name: str
    aim: str
    startup_output: str
    steps: list[TestStep]
    data_file: str = ""
    saved_data: str = ""


def read_fenced_block(lines: list[str], start: int) -> tuple[str, int]:
    """Read the next fenced block and return its contents and next position."""
    index = start
    while index < len(lines) and not lines[index].startswith("```"):
        if lines[index].strip():
            raise ValueError(f"Expected a fenced block near line {index + 1}")
        index += 1

    if index == len(lines):
        raise ValueError("Expected a fenced block before the end of the plan")

    index += 1
    content: list[str] = []
    while index < len(lines) and lines[index] != "```":
        content.append(lines[index])
        index += 1

    if index == len(lines):
        raise ValueError("Unclosed fenced block in the test plan")

    return "\n".join(content), index + 1


def parse_plan(plan_path: Path) -> list[TestCase]:
    """Parse test cases from the documented Markdown test-plan format."""
    lines = plan_path.read_text(encoding="utf-8").splitlines()
    cases: list[TestCase] = []
    current: TestCase | None = None
    pending_command: str | None = None
    index = 0

    while index < len(lines):
        line = lines[index]
        if line.startswith("## "):
            if current is not None:
                validate_case(current, pending_command)
                cases.append(current)
            current = TestCase(line[3:].strip(), "", "", [])
            pending_command = None
            index += 1
            continue

        if current is not None and line.startswith("Aim: "):
            current.aim = line[len("Aim: "):].strip()
            index += 1
            continue

        if current is not None and line == "### Startup output":
            current.startup_output, index = read_fenced_block(lines, index + 1)
            continue

        if current is not None and line == "### Data file":
            current.data_file, index = read_fenced_block(lines, index + 1)
            continue

        if current is not None and line == "### Saved data":
            current.saved_data, index = read_fenced_block(lines, index + 1)
            continue

        if current is not None and line == "### Input":
            pending_command, index = read_fenced_block(lines, index + 1)
            if "\n" in pending_command:
                raise ValueError(
                    f"Each input must be one command in test case '{current.name}'"
                )
            continue

        if current is not None and line == "### Expected output":
            if pending_command is None:
                raise ValueError(
                    f"Expected output has no preceding input in '{current.name}'"
                )
            expected, index = read_fenced_block(lines, index + 1)
            current.steps.append(TestStep(pending_command, expected))
            pending_command = None
            continue

        index += 1

    if current is not None:
        validate_case(current, pending_command)
        cases.append(current)

    if not cases:
        raise ValueError("The test plan contains no test cases")
    return cases


def validate_case(test_case: TestCase, pending_command: str | None) -> None:
    """Check that one parsed test case contains all required information."""
    if not test_case.aim:
        raise ValueError(f"Test case '{test_case.name}' has no aim")
    if not test_case.startup_output:
        raise ValueError(f"Test case '{test_case.name}' has no startup output")
    if pending_command is not None:
        raise ValueError(f"Input '{pending_command}' has no expected output")
    if not test_case.steps:
        raise ValueError(f"Test case '{test_case.name}' has no command steps")


def output_reader(stream, output_queue: queue.Queue[str | None]) -> None:
    """Forward process output lines to a queue without blocking the test loop."""
    for line in stream:
        output_queue.put(line.rstrip("\n").rstrip("\r"))
    output_queue.put(None)


def read_ui_block(
    output_queue: queue.Queue[str | None], timeout_seconds: float
) -> str:
    """Read one UI message enclosed by two divider lines."""
    lines: list[str] = []
    divider_count = 0
    deadline = time.monotonic() + timeout_seconds

    while divider_count < 2:
        remaining = deadline - time.monotonic()
        if remaining <= 0:
            raise TimeoutError("Timed out while waiting for chatbot output")
        try:
            line = output_queue.get(timeout=remaining)
        except queue.Empty as error:
            raise TimeoutError("Timed out while waiting for chatbot output") from error

        if line is None:
            raise RuntimeError("The chatbot exited before completing its response")
        lines.append(line)
        if line == DIVIDER:
            divider_count += 1

    return "\n".join(lines)


def stop_process(process: subprocess.Popen[str]) -> None:
    """Stop a chatbot process without leaving it running after a test."""
    if process.poll() is None:
        process.terminate()
        try:
            process.wait(timeout=1)
        except subprocess.TimeoutExpired:
            process.kill()
            process.wait(timeout=1)


def print_failure(
    test_case: TestCase, command: str, expected: str, actual: str
) -> None:
    """Print an actionable first-failure report."""
    print(f"\nFAIL: {test_case.name}")
    print(f"Aim: {test_case.aim}")
    print(f"Input: {command}")
    print("\nExpected output:")
    print(expected)
    print("\nActual output:")
    print(actual)


def run_case(
    test_case: TestCase,
    class_directory: Path,
    case_directory: Path,
    timeout_seconds: float,
) -> bool:
    """Run one test case, stopping immediately on its first mismatch."""
    case_directory.mkdir(parents=True, exist_ok=True)
    if test_case.data_file:
        data_dir = case_directory / "data"
        data_dir.mkdir(parents=True, exist_ok=True)
        (data_dir / "ruby.txt").write_text(test_case.data_file, encoding="utf-8")

    process = subprocess.Popen(
        ["java", "-cp", str(class_directory), MAIN_CLASS],
        stdin=subprocess.PIPE,
        stdout=subprocess.PIPE,
        stderr=subprocess.STDOUT,
        text=True,
        bufsize=1,
        cwd=case_directory,
    )
    assert process.stdin is not None
    assert process.stdout is not None

    output_queue: queue.Queue[str | None] = queue.Queue()
    reader = threading.Thread(
        target=output_reader,
        args=(process.stdout, output_queue),
        daemon=True,
    )
    reader.start()

    print(f"\n=== {test_case.name} ===")
    print(f"Aim: {test_case.aim}")
    print(f"$ java {MAIN_CLASS}")

    try:
        actual_startup = read_ui_block(output_queue, timeout_seconds)
        print(actual_startup)
        if actual_startup != test_case.startup_output:
            stop_process(process)
            print_failure(
                test_case,
                "<startup>",
                test_case.startup_output,
                actual_startup,
            )
            return False

        for step in test_case.steps:
            print(f"> {step.command}")
            process.stdin.write(step.command + "\n")
            process.stdin.flush()
            actual = read_ui_block(output_queue, timeout_seconds)
            print(actual)
            if actual != step.expected_output:
                stop_process(process)
                print_failure(test_case, step.command, step.expected_output, actual)
                return False

        stop_process(process)
        if test_case.saved_data:
            data_path = case_directory / "data" / "ruby.txt"
            if not data_path.exists():
                print_failure(
                    test_case,
                    "<saved data>",
                    test_case.saved_data,
                    "<no data file was written>",
                )
                return False
            actual_data = data_path.read_text(encoding="utf-8").rstrip("\n")
            if actual_data != test_case.saved_data.rstrip("\n"):
                print_failure(test_case, "<saved data>", test_case.saved_data, actual_data)
                return False
        print(f"PASS: {test_case.name}")
        return True
    except (BrokenPipeError, RuntimeError, TimeoutError) as error:
        stop_process(process)
        print_failure(test_case, "<runtime>", "A complete UI response", str(error))
        return False


def verify_java_25() -> None:
    """Fail clearly if the active Java runtime is not version 25."""
    result = subprocess.run(
        ["java", "-version"],
        capture_output=True,
        text=True,
        check=False,
    )
    version_output = result.stderr + result.stdout
    first_line = version_output.splitlines()[0] if version_output else ""
    if result.returncode != 0 or 'version "25.' not in first_line:
        raise RuntimeError(
            "Java 25 is required. Activate it with "
            "`sdk use java 25.0.3.fx-zulu` before running the UI tests. "
            f"Detected: {first_line or 'no Java runtime'}"
        )


def compile_project(repository: Path, class_directory: Path) -> None:
    """Compile all project Java sources into a temporary directory."""
    sources = sorted((repository / "src" / "main" / "java").rglob("*.java"))
    if not sources:
        raise RuntimeError("No Java sources found in src/main/java")
    result = subprocess.run(
        ["javac", "-d", str(class_directory), *map(str, sources)],
        capture_output=True,
        text=True,
        check=False,
    )
    if result.returncode != 0:
        raise RuntimeError("Compilation failed:\n" + result.stderr)


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument(
        "--plan",
        type=Path,
        help="Markdown test plan (defaults to test/ui-test-plan.md)",
    )
    parser.add_argument(
        "--timeout",
        type=float,
        default=DEFAULT_TIMEOUT_SECONDS,
        help="Seconds to wait for each UI response",
    )
    args = parser.parse_args()

    repository = Path(__file__).resolve().parents[4]
    plan_path = args.plan or repository / "test" / "ui-test-plan.md"

    try:
        verify_java_25()
        test_cases = parse_plan(plan_path)
        with tempfile.TemporaryDirectory(prefix="ruby-ui-tests-") as temp_directory:
            class_directory = Path(temp_directory)
            compile_project(repository, class_directory)
            for index, test_case in enumerate(test_cases):
                case_directory = class_directory / f"case-{index + 1}"
                if not run_case(test_case, class_directory, case_directory, args.timeout):
                    return 1
    except (OSError, RuntimeError, ValueError) as error:
        print(f"ERROR: {error}", file=sys.stderr)
        return 1

    print(f"\nAll {len(test_cases)} UI test case(s) passed.")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
