#!/usr/bin/env python3
"""Run console UI tests defined in test/ui-test-plan.md."""

from __future__ import annotations

import argparse
import re
import shlex
import subprocess
import sys
from dataclasses import dataclass
from pathlib import Path


if hasattr(sys.stdout, "reconfigure"):
    sys.stdout.reconfigure(encoding="utf-8", errors="replace")
if hasattr(sys.stderr, "reconfigure"):
    sys.stderr.reconfigure(encoding="utf-8", errors="replace")


@dataclass
class TestCase:
    """One UI test case parsed from the Markdown test plan."""

    name: str
    aim: str
    inputs: str
    expected_output: str


def extract_block(section: str, heading: str) -> str:
    """Return the text inside a required text code block under a heading."""
    pattern = rf"^### {re.escape(heading)}\r?\n\s*```text\r?\n(.*?)\r?\n```"
    match = re.search(pattern, section, flags=re.MULTILINE | re.DOTALL)
    if match is None:
        raise ValueError(f"missing '{heading}' text block")
    return match.group(1)


def parse_test_plan(plan_path: Path) -> tuple[str, list[TestCase]]:
    """Parse the program command and test cases from a Markdown test plan."""
    text = plan_path.read_text(encoding="utf-8")
    command_match = re.search(
        r"^## Program command\r?\n\s*```text\r?\n(.+?)\r?\n```", text,
        flags=re.MULTILINE | re.DOTALL,
    )
    if command_match is None:
        raise ValueError("missing 'Program command' text block")

    sections = re.split(r"^## Test case: ", text, flags=re.MULTILINE)[1:]
    test_cases = []
    for section in sections:
        name, separator, body = section.partition("\n")
        if not separator:
            raise ValueError("test case name must be followed by its contents")
        aim_match = re.search(r"^- \*\*Aim:\*\* (.+)$", body, flags=re.MULTILINE)
        if aim_match is None:
            raise ValueError(f"test case '{name}' is missing its aim")
        test_cases.append(TestCase(
            name=name.strip(),
            aim=aim_match.group(1).strip(),
            inputs=extract_block(body, "Inputs"),
            expected_output=extract_block(body, "Expected output"),
        ))

    if not test_cases:
        raise ValueError("the test plan contains no test cases")
    return command_match.group(1).strip(), test_cases


def normalise_newlines(text: str) -> str:
    """Normalise line endings and an optional final newline for comparison."""
    normalised = text.replace("\r\n", "\n").replace("\r", "\n")
    return normalised.removesuffix("\n")


def command_arguments(command: str) -> list[str]:
    """Split a command-line string while preserving quoted arguments."""
    return shlex.split(command, posix=True)


def display_session(test_case: TestCase, actual_output: str) -> None:
    """Print the inputs and captured console output for one test case."""
    print(f"\n=== {test_case.name} ===")
    print(f"Aim: {test_case.aim}")
    print("--- Console input ---")
    print(test_case.inputs)
    print("--- Console output ---")
    print(actual_output, end="" if actual_output.endswith("\n") else "\n")


def run_test_case(test_case: TestCase, command: str, repository: Path) -> tuple[bool, str, str]:
    """Run one test case and return its pass state, output, and error output."""
    console_input = test_case.inputs
    if console_input and not console_input.endswith("\n"):
        console_input += "\n"
    result = subprocess.run(
        command_arguments(command),
        input=console_input,
        capture_output=True,
        cwd=repository,
        encoding="utf-8",
        errors="replace",
        timeout=15,
    )
    actual = normalise_newlines(result.stdout)
    expected = normalise_newlines(test_case.expected_output)
    passed = result.returncode == 0 and actual == expected
    error_output = normalise_newlines(result.stderr)
    if result.returncode != 0:
        error_output = f"Process exited with code {result.returncode}.\n{error_output}"
    return passed, actual, error_output


def main() -> int:
    """Run every planned UI test until the first failure."""
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--plan", type=Path, default=Path("test/ui-test-plan.md"))
    parser.add_argument("--command", help="override the program command in the test plan")
    args = parser.parse_args()

    plan_path = args.plan.resolve()
    try:
        planned_command, test_cases = parse_test_plan(plan_path)
    except (OSError, ValueError) as error:
        print(f"Could not read UI test plan: {error}", file=sys.stderr)
        return 2

    command = args.command or planned_command
    repository = plan_path.parent.parent
    print(f"Running {len(test_cases)} UI test case(s) with: {command}")
    for test_case in test_cases:
        try:
            passed, actual, error_output = run_test_case(test_case, command, repository)
        except (OSError, subprocess.TimeoutExpired, ValueError) as error:
            print(f"\n=== {test_case.name} ===")
            print(f"Test could not run: {error}")
            return 1

        display_session(test_case, actual)
        if not passed:
            print("--- Expected output ---")
            print(test_case.expected_output)
            if error_output:
                print("--- Standard error ---")
                print(error_output, end="" if error_output.endswith("\n") else "\n")
            print(f"FAIL: {test_case.name}. Test session terminated.")
            return 1
        print(f"PASS: {test_case.name}")

    print("\nAll UI test cases passed.")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
