#!/usr/bin/env python3
"""Run the Markdown-recorded console UI tests and print their transcripts."""

from __future__ import annotations

from dataclasses import dataclass
from pathlib import Path
import re
import subprocess
import sys


@dataclass
class TestCase:
    """One console UI test read from the project's Markdown test plan."""

    name: str
    aim: str
    command: str
    inputs: str
    expected_output: str


def read_fenced_text(section: str, heading: str, test_name: str) -> str:
    """Read a required ``text`` code block below a test-case heading."""
    pattern = rf"^### {re.escape(heading)}\s*\n```text\n(.*?)\n```\s*$"
    match = re.search(pattern, section, flags=re.MULTILINE | re.DOTALL)
    if match is None:
        raise ValueError(f"{test_name}: missing a '{heading}' text block")

    contents = match.group(1)
    return contents + ("\n" if contents else "")


def read_test_cases(plan_path: Path) -> list[TestCase]:
    """Parse test cases in the documented format from ``plan_path``."""
    plan = plan_path.read_text(encoding="utf-8")
    matches = list(re.finditer(r"^## Test case: (.+?)\s*$", plan, flags=re.MULTILINE))
    if not matches:
        raise ValueError("The plan does not contain any '## Test case:' headings.")

    test_cases: list[TestCase] = []
    for index, match in enumerate(matches):
        name = match.group(1)
        section_end = matches[index + 1].start() if index + 1 < len(matches) else len(plan)
        section = plan[match.end():section_end]

        aim_match = re.search(r"^- Aim: (.+?)\s*$", section, flags=re.MULTILINE)
        command_match = re.search(
            r"^- Run command: `([^`]+)`\s*$", section, flags=re.MULTILINE
        )
        if aim_match is None:
            raise ValueError(f"{name}: missing an Aim")
        if command_match is None:
            raise ValueError(f"{name}: missing a Run command in inline code")

        test_cases.append(
            TestCase(
                name=name,
                aim=aim_match.group(1),
                command=command_match.group(1),
                inputs=read_fenced_text(section, "Inputs", name),
                expected_output=read_fenced_text(section, "Expected output", name),
            )
        )

    return test_cases


def display_block(label: str, contents: str) -> None:
    """Print a clearly bounded console record, including an empty record."""
    print(f"{label}:")
    print("---")
    if contents:
        print(contents, end="" if contents.endswith("\n") else "\n")
    else:
        print("<no output>")
    print("---")


def run_test(test_case: TestCase) -> bool:
    """Run one test case, show its transcript, and return whether it passed."""
    completed = subprocess.run(
        test_case.command,
        shell=True,
        input=test_case.inputs,
        text=True,
        stdout=subprocess.PIPE,
        stderr=subprocess.PIPE,
        check=False,
    )

    print(f"\nTest case: {test_case.name}")
    print(f"Aim: {test_case.aim}")
    print(f"Run command: {test_case.command}")
    display_block("Console input", test_case.inputs)
    display_block("Console output", completed.stdout)
    if completed.stderr:
        display_block("Standard error", completed.stderr)

    passed = completed.returncode == 0 and completed.stdout == test_case.expected_output
    if passed:
        print("Result: PASS")
        return True

    print("Result: FAIL")
    print(f"Exit code: {completed.returncode}")
    display_block("Expected output", test_case.expected_output)
    display_block("Actual output", completed.stdout)
    return False


def main() -> int:
    """Read the requested plan and stop at the first failed test case."""
    if len(sys.argv) != 2:
        print("Usage: run_ui_tests.py <test-plan.md>", file=sys.stderr)
        return 2

    try:
        test_cases = read_test_cases(Path(sys.argv[1]))
    except (OSError, ValueError) as error:
        print(f"Could not read UI test plan: {error}", file=sys.stderr)
        return 2

    print(f"Running {len(test_cases)} console UI test case(s).")
    for test_case in test_cases:
        if not run_test(test_case):
            print("\nTest session stopped after the first failure.")
            return 1

    print("\nAll console UI tests passed.")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
