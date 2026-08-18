#!/usr/bin/env python3
"""Run exact-output console UI tests documented in a Markdown test plan."""

from __future__ import annotations

import re
import subprocess
import sys
from dataclasses import dataclass
from pathlib import Path


@dataclass
class TestCase:
    """Store one console UI test case parsed from the Markdown plan."""

    name: str
    aim: str
    command: str
    user_input: str
    expected_output: str


def normalize(text: str) -> str:
    """Return text with platform-independent line endings."""
    return text.replace("\r\n", "\n").replace("\r", "\n")


def parse_test_cases(plan_text: str) -> list[TestCase]:
    """Parse test cases that follow the format documented by the test-ui skill."""
    pattern = re.compile(
        r"^## Test \d+: (?P<name>.+?)\n\n"
        r"\*\*Aim:\*\* (?P<aim>.+?)\n\n"
        r"\*\*Run:\*\* `(?P<command>.+?)`\n\n"
        r"\*\*Input:\*\*\n```text\n(?P<input>.*?)\n```\n\n"
        r"\*\*Expected output:\*\*\n```text\n(?P<expected>.*?)\n```",
        re.MULTILINE | re.DOTALL,
    )
    matches = list(pattern.finditer(normalize(plan_text)))
    if not matches:
        raise ValueError("No test cases match the required format.")

    return [
        TestCase(
            match.group("name"),
            match.group("aim"),
            match.group("command"),
            match.group("input"),
            match.group("expected"),
        )
        for match in matches
    ]


def print_session(case: TestCase, actual_output: str) -> None:
    """Print the command, input, and output observed in one test session."""
    print(f"\n=== Test: {case.name} ===")
    print(f"Aim: {case.aim}")
    print(f"Run: {case.command}")
    print("Console input:")
    print(case.user_input)
    print("Console output:")
    print(actual_output, end="" if actual_output.endswith("\n") else "\n")


def run_test_case(case: TestCase, working_directory: Path) -> tuple[bool, str]:
    """Run one case and return whether its output equals the expected output."""
    result = subprocess.run(
        case.command,
        input=case.user_input + "\n",
        text=True,
        shell=True,
        capture_output=True,
        cwd=working_directory,
        timeout=15,
    )
    actual_output = normalize(result.stdout + result.stderr)
    return result.returncode == 0 and actual_output == normalize(case.expected_output), actual_output


def main() -> int:
    """Run the requested plan and stop after its first failure."""
    plan_path = Path(sys.argv[1] if len(sys.argv) == 2 else "test/ui-test-plan.md")
    try:
        cases = parse_test_cases(plan_path.read_text(encoding="utf-8"))
    except (OSError, ValueError) as error:
        print(f"Cannot read test plan: {error}", file=sys.stderr)
        return 2

    for index, case in enumerate(cases, start=1):
        try:
            passed, actual_output = run_test_case(case, plan_path.parent.parent)
        except subprocess.TimeoutExpired:
            print(f"\nTest {index} failed: {case.name} timed out after 15 seconds.")
            return 1

        print_session(case, actual_output)
        if not passed:
            print("Test failed. Expected output:")
            print(case.expected_output)
            print("Actual output:")
            print(actual_output)
            return 1

    print(f"\nAll {len(cases)} UI test case(s) passed.")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
