#!/usr/bin/env python3
"""Run exact-output console UI tests recorded in a Markdown test plan."""

from __future__ import annotations

import argparse
import difflib
import re
import subprocess
import sys
from dataclasses import dataclass
from pathlib import Path


@dataclass(frozen=True)
class TestCase:
    """A single fresh-process console UI test case."""

    name: str
    aim: str
    inputs: str
    expected_output: str


@dataclass(frozen=True)
class TestPlan:
    """The executable configuration and ordered cases from a UI test plan."""

    program_command: str
    timeout_seconds: float
    test_cases: list[TestCase]


class PlanError(ValueError):
    """Raised when the Markdown test plan does not match the required format."""


def normalize_line_endings(text: str) -> str:
    """Return text with Windows and legacy Mac line endings normalized to LF."""
    return text.replace("\r\n", "\n").replace("\r", "\n")


def extract_fenced_block(section: str, heading: str, case_name: str) -> str:
    """Extract the text fence following a level-four heading."""
    pattern = re.compile(
        rf"^####[ \t]+{re.escape(heading)}[ \t]*\n"
        rf"[ \t]*\n?```text[ \t]*\n(.*?)^```[ \t]*$",
        re.MULTILINE | re.DOTALL,
    )
    match = pattern.search(section)
    if not match:
        raise PlanError(f"{case_name} is missing a valid '{heading}' text block")
    return normalize_line_endings(match.group(1))


def parse_plan(plan_path: Path) -> TestPlan:
    """Parse a UI test plan using the format documented by the skill."""
    try:
        document = normalize_line_endings(plan_path.read_text(encoding="utf-8"))
    except OSError as exception:
        raise PlanError(f"cannot read {plan_path}: {exception}") from exception

    command_match = re.search(
        r"^### Program command[ \t]*\n[ \t]*\n?```(?:shell|sh|bash|zsh)[ \t]*\n"
        r"(.*?)^```[ \t]*$",
        document,
        re.MULTILINE | re.DOTALL,
    )
    if not command_match or not command_match.group(1).strip():
        raise PlanError("missing a non-empty '### Program command' shell block")
    program_command = command_match.group(1).strip()

    timeout_match = re.search(
        r"^- Timeout \(seconds\):[ \t]*([0-9]+(?:\.[0-9]+)?)[ \t]*$",
        document,
        re.MULTILINE,
    )
    timeout_seconds = float(timeout_match.group(1)) if timeout_match else 10.0
    if timeout_seconds <= 0:
        raise PlanError("timeout must be greater than zero")

    test_cases_heading = re.search(r"^## Test cases[ \t]*$", document, re.MULTILINE)
    if not test_cases_heading:
        raise PlanError("missing '## Test cases' section")
    cases_document = document[test_cases_heading.end() :]
    headings = list(re.finditer(r"^###[ \t]+(.+?)[ \t]*$", cases_document, re.MULTILINE))
    if not headings:
        raise PlanError("the plan contains no test cases")

    test_cases: list[TestCase] = []
    for index, heading in enumerate(headings):
        case_name = heading.group(1).strip()
        section_end = headings[index + 1].start() if index + 1 < len(headings) else len(cases_document)
        section = cases_document[heading.end() : section_end]
        aim_match = re.search(r"^\*\*Aim:\*\*[ \t]*(.+?)[ \t]*$", section, re.MULTILINE)
        if not aim_match:
            raise PlanError(f"{case_name} is missing a non-empty '**Aim:**' line")
        test_cases.append(
            TestCase(
                name=case_name,
                aim=aim_match.group(1),
                inputs=extract_fenced_block(section, "Inputs", case_name),
                expected_output=extract_fenced_block(section, "Expected output", case_name),
            )
        )

    return TestPlan(program_command, timeout_seconds, test_cases)


def show_block(label: str, content: str) -> None:
    """Print a labelled block while making a missing final newline visible."""
    print(f"--- {label} ---")
    sys.stdout.write(content)
    if not content.endswith("\n"):
        print("\n[no final newline]")
    print(f"--- End {label.lower()} ---")


def run_test_case(test_case: TestCase, plan: TestPlan, project_root: Path) -> bool:
    """Run one case, print its transcript, and return whether it passed."""
    print(f"=== {test_case.name} ===")
    print(f"Aim: {test_case.aim}")
    show_block("Console input", test_case.inputs)

    try:
        completed = subprocess.run(
            plan.program_command,
            cwd=project_root,
            input=test_case.inputs,
            stdout=subprocess.PIPE,
            stderr=subprocess.STDOUT,
            text=True,
            encoding="utf-8",
            errors="replace",
            shell=True,
            timeout=plan.timeout_seconds,
            check=False,
        )
        actual_output = normalize_line_endings(completed.stdout)
    except subprocess.TimeoutExpired as exception:
        partial_output = exception.stdout or ""
        if isinstance(partial_output, bytes):
            partial_output = partial_output.decode("utf-8", errors="replace")
        actual_output = normalize_line_endings(partial_output)
        show_block("Console output", actual_output)
        print(f"FAIL: timed out after {plan.timeout_seconds:g} seconds")
        show_block("Actual output", actual_output)
        show_block("Expected output", test_case.expected_output)
        return False

    show_block("Console output", actual_output)
    output_matches = actual_output == test_case.expected_output
    exit_succeeded = completed.returncode == 0
    if output_matches and exit_succeeded:
        print("PASS\n")
        return True

    if not exit_succeeded:
        print(f"FAIL: program exited with status {completed.returncode}")
    else:
        print("FAIL: actual output differs from expected output")
    show_block("Actual output", actual_output)
    show_block("Expected output", test_case.expected_output)
    print("--- Unified diff (expected -> actual) ---")
    sys.stdout.writelines(
        difflib.unified_diff(
            test_case.expected_output.splitlines(keepends=True),
            actual_output.splitlines(keepends=True),
            fromfile="expected",
            tofile="actual",
        )
    )
    print("--- End diff ---")
    return False


def main() -> int:
    """Run all planned cases in order, stopping at the first failure."""
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument(
        "plan",
        nargs="?",
        default="test/ui-test-plan.md",
        type=Path,
        help="Markdown test plan path (default: test/ui-test-plan.md)",
    )
    arguments = parser.parse_args()
    project_root = Path.cwd()
    plan_path = arguments.plan if arguments.plan.is_absolute() else project_root / arguments.plan

    try:
        plan = parse_plan(plan_path)
    except PlanError as exception:
        print(f"TEST PLAN ERROR: {exception}", file=sys.stderr)
        return 2

    print(f"UI test plan: {plan_path}")
    print(f"Program command: {plan.program_command}")
    print(f"Cases: {len(plan.test_cases)}\n")
    for index, test_case in enumerate(plan.test_cases, start=1):
        if not run_test_case(test_case, plan, project_root):
            print(f"Test session terminated after case {index}; later cases were not run.")
            return 1

    print(f"All {len(plan.test_cases)} UI test cases passed.")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
