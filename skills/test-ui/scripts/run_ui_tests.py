#!/usr/bin/env python3
"""Run console UI tests described in a Markdown test plan.

Each Test Case section must contain fenced Aim, Command, Input, and Expected
Output sections. Test cases run in document order and the process stops at the
first command failure or output mismatch.
"""

from __future__ import annotations

import re
import subprocess
import sys
from dataclasses import dataclass
from pathlib import Path


DEFAULT_PLAN = Path("test/ui-test-plan.md")
TEST_CASE_PATTERN = re.compile(
    r"^##\s+Test Case:\s*(?P<name>.+?)\s*$"
    r"(?P<body>.*?)(?=^##\s+Test Case:|\Z)",
    re.MULTILINE | re.DOTALL,
)
FIELD_PATTERN = re.compile(
    r"^###\s+(?P<field>Aim|Command|Input|Expected Output)\s*$"
    r"(?P<value>.*?)(?=^###\s+|\Z)",
    re.MULTILINE | re.DOTALL | re.IGNORECASE,
)
FENCE = chr(96) * 3
FENCED_VALUE_PATTERN = re.compile(
    r"^\s*" + FENCE + r"[^\n]*\n(?P<value>.*?)^\s*" + FENCE + r"\s*$",
    re.MULTILINE | re.DOTALL,
)


@dataclass(frozen=True)
class TestCase:
    """A single command, its console input, and the output it must produce."""

    name: str
    aim: str
    command: str
    input_text: str
    expected_output: str


def extract_value(raw_value: str, field: str, test_name: str) -> str:
    """Extract a field's content and validate its presence."""

    if field == "Aim":
        aim = raw_value.strip()
        if not aim:
            raise ValueError(f"Test case '{test_name}' has an empty Aim section")
        return aim

    match = FENCED_VALUE_PATTERN.fullmatch(raw_value.strip())
    if match is None:
        raise ValueError(
            f"Test case '{test_name}' has a {field} section without one fenced code block"
        )
    return match.group("value")


def parse_plan(plan_path: Path) -> list[TestCase]:
    """Parse and validate all test cases from a Markdown plan."""

    plan_text = plan_path.read_text(encoding="utf-8")
    matches = list(TEST_CASE_PATTERN.finditer(plan_text))
    if not matches:
        raise ValueError(f"No Test Case sections found in {plan_path}")

    cases: list[TestCase] = []
    for case_match in matches:
        name = case_match.group("name").strip()
        fields = {
            field_match.group("field").lower(): field_match.group("value")
            for field_match in FIELD_PATTERN.finditer(case_match.group("body"))
        }
        required_fields = {"aim", "command", "input", "expected output"}
        missing = sorted(required_fields - fields.keys())
        if missing:
            raise ValueError(
                f"Test case '{name}' is missing section(s): {', '.join(missing)}"
            )

        cases.append(
            TestCase(
                name=name,
                aim=extract_value(fields["aim"], "Aim", name),
                command=extract_value(fields["command"], "Command", name),
                input_text=extract_value(fields["input"], "Input", name),
                expected_output=extract_value(
                    fields["expected output"], "Expected Output", name
                ),
            )
        )
    return cases


def comparable_output(output: str) -> str:
    """Normalize line endings and ignore only trailing whitespace at EOF."""

    return output.replace("\r\n", "\n").replace("\r", "\n").rstrip()


def print_block(label: str, value: str) -> None:
    """Print a labelled transcript block while preserving the value's content."""

    print(f"--- {label} ---")
    if value:
        print(value, end="" if value.endswith("\n") else "\n")
    else:
        print("<empty>")


def run_test_case(case_number: int, case: TestCase, project_root: Path) -> bool:
    """Run one test, print its transcript, and return whether it passed."""

    print(f"=== Test {case_number}: {case.name} ===")
    print(f"Aim: {case.aim}")
    print(f"$ {case.command}")
    print_block("console input", case.input_text)

    completed = subprocess.run(
        case.command,
        cwd=project_root,
        input=case.input_text,
        stdout=subprocess.PIPE,
        stderr=subprocess.STDOUT,
        text=True,
        shell=True,
        executable="/bin/zsh",
        check=False,
    )
    actual_output = completed.stdout
    print_block("console output", actual_output)

    output_matches = comparable_output(actual_output) == comparable_output(
        case.expected_output
    )
    process_succeeded = completed.returncode == 0
    if process_succeeded and output_matches:
        print("Result: PASS")
        print()
        return True

    print(f"Result: FAIL (exit status {completed.returncode})")
    if not process_succeeded:
        print("The command exited unsuccessfully.")
    if not output_matches:
        print_block("expected output", case.expected_output)
        print_block("actual output", actual_output)
    print()
    return False


def main(argv: list[str]) -> int:
    """Run all plan entries, stopping immediately after the first failure."""

    plan_path = Path(argv[1]) if len(argv) > 1 else DEFAULT_PLAN
    project_root = Path.cwd()
    try:
        cases = parse_plan(plan_path)
    except (OSError, ValueError) as error:
        print(f"UI test plan error: {error}", file=sys.stderr)
        return 1

    print(f"UI test plan: {plan_path}")
    print(f"Working directory: {project_root}")
    print(f"Test cases: {len(cases)}")
    print()

    for case_number, case in enumerate(cases, start=1):
        if not run_test_case(case_number, case, project_root):
            print(f"Stopped after test {case_number}.")
            return 1

    print(f"All {len(cases)} UI test case(s) passed.")
    return 0


if __name__ == "__main__":
    raise SystemExit(main(sys.argv))
