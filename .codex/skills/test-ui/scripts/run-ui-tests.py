"""Run command-line UI test cases recorded in a Markdown test plan."""

from __future__ import annotations

import argparse
import re
import shlex
import subprocess
import sys
from dataclasses import dataclass
from pathlib import Path


@dataclass
class TestCase:
    """One UI test case parsed from the test plan."""

    name: str
    aim: str
    user_input: str
    expected_output: str


def code_block(section: str, label: str) -> str:
    """Return the text code block following a Markdown label in a section."""
    pattern = rf"\*\*{re.escape(label)}:\*\*\s*\n```text\n(.*?)\n```"
    match = re.search(pattern, section, flags=re.DOTALL)
    if match is None:
        raise ValueError(f"Missing {label.lower()} code block")
    return match.group(1)


def parse_plan(plan_path: Path) -> tuple[str, list[TestCase]]:
    """Parse a program command and UI test cases from a Markdown plan."""
    plan = plan_path.read_text(encoding="utf-8").replace("\r\n", "\n")
    command_match = re.search(
        r"## Program command\s*\n```text\n(.*?)\n```", plan, flags=re.DOTALL
    )
    if command_match is None:
        raise ValueError("Missing ## Program command text code block")

    headings = list(re.finditer(r"^## Test case: (.+)$", plan, flags=re.MULTILINE))
    if not headings:
        raise ValueError("The plan does not define any test cases")

    cases: list[TestCase] = []
    for index, heading in enumerate(headings):
        end = headings[index + 1].start() if index + 1 < len(headings) else len(plan)
        section = plan[heading.end():end]
        aim_match = re.search(r"\*\*Aim:\*\*\s*(.+)", section)
        if aim_match is None:
            raise ValueError(f"Missing aim for test case '{heading.group(1)}'")
        cases.append(
            TestCase(
                name=heading.group(1).strip(),
                aim=aim_match.group(1).strip(),
                user_input=code_block(section, "Input"),
                expected_output=code_block(section, "Expected output"),
            )
        )
    return command_match.group(1).strip(), cases


def normalise(text: str) -> str:
    """Normalise line endings while preserving all meaningful output lines."""
    return text.replace("\r\n", "\n").rstrip("\n")


def output_matches(actual: str, expected: str) -> bool:
    """Compare output exactly, optionally ignoring a dynamic leading prefix."""
    actual = normalise(actual)
    expected = normalise(expected)
    prefix = "{{ANY_PREFIX}}"
    if expected == prefix:
        return True
    if expected.startswith(prefix + "\n"):
        return actual.endswith(expected[len(prefix) + 1:])
    return actual == expected


def show_transcript(case: TestCase, actual: str) -> None:
    """Print a readable record of one executed console session."""
    print(f"\n=== {case.name} ===")
    print(f"Aim: {case.aim}")
    print("Console input:")
    print(case.user_input)
    print("Console output:")
    print(actual, end="" if actual.endswith("\n") else "\n")


def main() -> int:
    """Run all planned UI tests until one fails or every case passes."""
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("plan", type=Path, help="Markdown UI test plan")
    parser.add_argument("--command", help="Override the program command in the plan")
    arguments = parser.parse_args()

    try:
        command, cases = parse_plan(arguments.plan)
    except (OSError, ValueError) as error:
        print(f"Invalid test plan: {error}", file=sys.stderr)
        return 2

    command = arguments.command or command
    try:
        command_parts = shlex.split(command, posix=False)
    except ValueError as error:
        print(f"Invalid program command: {error}", file=sys.stderr)
        return 2

    for case in cases:
        result = subprocess.run(
            command_parts,
            input=case.user_input + "\n",
            capture_output=True,
            text=True,
            check=False,
        )
        actual = result.stdout
        show_transcript(case, actual)
        if result.returncode != 0 or not output_matches(actual, case.expected_output):
            print("RESULT: FAILED")
            if result.returncode != 0:
                print(f"Program exit code: {result.returncode}")
                if result.stderr:
                    print("Program error output:")
                    print(result.stderr, end="" if result.stderr.endswith("\n") else "\n")
            print("Expected output:")
            print(case.expected_output)
            print("Actual output:")
            print(actual, end="" if actual.endswith("\n") else "\n")
            return 1
        print("RESULT: PASSED")

    print(f"\nAll {len(cases)} UI test case(s) passed.")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
