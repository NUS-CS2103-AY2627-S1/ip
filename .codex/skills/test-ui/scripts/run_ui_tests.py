#!/usr/bin/env python3
"""Run command-line UI tests described in a Markdown test plan."""

from __future__ import annotations

import argparse
import difflib
import shlex
import subprocess
import sys
from dataclasses import dataclass
from pathlib import Path


@dataclass
class TestCase:
    """One UI test case parsed from the Markdown plan."""

    name: str
    aim: str
    input_text: str
    expected_output: str


def extract_code_block(lines: list[str], start: int) -> tuple[str, int]:
    """Extract the first text code block after ``start``."""
    index = start
    while index < len(lines) and lines[index].strip() != "```text":
        index += 1

    if index == len(lines):
        raise ValueError("Expected a ```text code block.")

    index += 1
    block_start = index
    while index < len(lines) and lines[index].strip() != "```":
        index += 1

    if index == len(lines):
        raise ValueError("Unterminated ```text code block.")

    return "\n".join(lines[block_start:index]), index + 1


def parse_plan(plan_path: Path) -> list[TestCase]:
    """Parse test cases from the project's Markdown test plan."""
    lines = plan_path.read_text(encoding="utf-8").splitlines()
    cases: list[TestCase] = []
    index = 0

    while index < len(lines):
        line = lines[index]
        if not line.startswith("## Test Case: "):
            index += 1
            continue

        name = line.removeprefix("## Test Case: ").strip()
        aim = ""
        input_text = None
        expected_output = None
        index += 1

        while index < len(lines) and not lines[index].startswith("## Test Case: "):
            line = lines[index]
            if line.startswith("- Aim:"):
                aim = line.removeprefix("- Aim:").strip()
            elif line.strip() == "### Input":
                input_text, index = extract_code_block(lines, index + 1)
                continue
            elif line.strip() == "### Expected output":
                expected_output, index = extract_code_block(lines, index + 1)
                continue
            index += 1

        if not aim or input_text is None or expected_output is None:
            raise ValueError(
                f"Test case '{name}' must contain an aim, input, and expected output."
            )

        cases.append(TestCase(name, aim, input_text, expected_output))

    if not cases:
        raise ValueError(f"No test cases found in {plan_path}.")

    return cases


def normalize_output(value: str) -> str:
    """Normalize line endings, trailing spaces, and one final newline."""
    normalized = value.replace("\r\n", "\n")
    normalized = "\n".join(line.rstrip() for line in normalized.split("\n"))
    return normalized[:-1] if normalized.endswith("\n") else normalized


def print_record(label: str, value: str) -> None:
    """Print a labelled console record without hiding whitespace or blank lines."""
    print(label)
    print(value, end="" if value.endswith("\n") else "\n")


def run_test_case(test_case: TestCase, command: list[str], cwd: Path,
                  timeout: float, reset_file: Path | None) -> bool:
    """Run one case, print its session, and return whether it passed."""
    if reset_file is not None:
        try:
            reset_file.unlink(missing_ok=True)
        except OSError as exception:
            print(f"Unable to reset UI test data file: {exception}", file=sys.stderr)
            return False

    input_for_process = test_case.input_text
    if not input_for_process.endswith("\n"):
        input_for_process += "\n"

    try:
        result = subprocess.run(
            command,
            cwd=cwd,
            input=input_for_process,
            text=True,
            capture_output=True,
            timeout=timeout,
            check=False,
        )
    except subprocess.TimeoutExpired as exception:
        print(f"\n=== {test_case.name} ===")
        print(f"Aim: {test_case.aim}")
        print_record("--- Console input ---", test_case.input_text)
        print("--- Result ---")
        print(f"FAIL: process exceeded the {timeout:g}-second timeout.")
        if exception.stdout:
            print_record("--- Partial console output ---", exception.stdout)
        return False
    except OSError as exception:
        print(f"\n=== {test_case.name} ===")
        print(f"Aim: {test_case.aim}")
        print_record("--- Console input ---", test_case.input_text)
        print("--- Result ---")
        print(f"FAIL: could not start program: {exception}")
        return False

    actual_output = result.stdout
    if result.stderr:
        actual_output += "\n[stderr]\n" + result.stderr

    actual_normalized = normalize_output(actual_output)
    expected_normalized = normalize_output(test_case.expected_output)
    passed = result.returncode == 0 and actual_normalized == expected_normalized

    print(f"\n=== {test_case.name} ===")
    print(f"Aim: {test_case.aim}")
    print_record("--- Console input ---", test_case.input_text)
    print_record("--- Console output ---", actual_output)

    if passed:
        print("--- Result ---")
        print("PASS")
        return True

    print("--- Result ---")
    if result.returncode != 0:
        print(f"FAIL: program exited with status {result.returncode}.")
    else:
        print("FAIL: actual output differs from expected output.")
    print_record("--- Expected output ---", test_case.expected_output)
    print("--- Output diff ---")
    diff = difflib.unified_diff(
        expected_normalized.splitlines(keepends=True),
        actual_normalized.splitlines(keepends=True),
        fromfile="expected",
        tofile="actual",
    )
    sys.stdout.writelines(diff)
    return False


def main() -> int:
    """Parse arguments, run cases in order, and stop at the first failure."""
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--plan", default="test/ui-test-plan.md",
                        help="Markdown test plan path")
    parser.add_argument(
        "--program",
        default="java -cp build/classes/java/main LuckyNoSlacky",
        help="Program command, quoted as one string",
    )
    parser.add_argument("--cwd", default=".", help="Program working directory")
    parser.add_argument(
        "--reset-file",
        help="Delete this file before each test case to isolate persisted state",
    )
    parser.add_argument("--timeout", type=float, default=10.0,
                        help="Per-test timeout in seconds")
    args = parser.parse_args()

    plan_path = Path(args.plan)
    cwd = Path(args.cwd).resolve()
    reset_file = None
    if args.reset_file:
        reset_file = Path(args.reset_file)
        if not reset_file.is_absolute():
            reset_file = cwd / reset_file
    try:
        test_cases = parse_plan(plan_path)
    except (OSError, ValueError) as exception:
        print(f"Unable to read test plan: {exception}", file=sys.stderr)
        return 2

    command = shlex.split(args.program)
    print(f"Running {len(test_cases)} UI test case(s) with: {' '.join(command)}")

    for index, test_case in enumerate(test_cases, start=1):
        print(f"\nStarting test case {index} of {len(test_cases)}.")
        if not run_test_case(test_case, command, cwd, args.timeout, reset_file):
            print("\nTest session terminated after the first failure.")
            return 1

    print(f"\nAll {len(test_cases)} UI test case(s) passed.")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
