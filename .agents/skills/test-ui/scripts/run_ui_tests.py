#!/usr/bin/env python3
"""Run fail-fast console UI tests described in a Markdown test plan."""

from __future__ import annotations

import argparse
import re
import shlex
import subprocess
import sys
from dataclasses import dataclass
from pathlib import Path


@dataclass(frozen=True)
class UiTestCase:
    """One independent console session from the UI test plan."""

    name: str
    aim: str
    inputs: str
    expected_output: str


@dataclass(frozen=True)
class UiTestPlan:
    """Commands and comparison settings read from the Markdown test plan."""

    build_command: str | None
    program_command: str
    output_starts_after: str | None
    test_cases: list[UiTestCase]


class PlanError(ValueError):
    """Indicate that the Markdown test plan is missing required data."""


def normalize_newlines(text: str) -> str:
    """Normalize line endings while preserving meaningful whitespace."""

    return text.replace("\r\n", "\n").replace("\r", "\n")


def comparable(text: str) -> str:
    """Return output in the form used for exact comparisons."""

    return normalize_newlines(text).rstrip("\n")


def parse_command(plan: str, label: str, required: bool) -> str | None:
    """Read a backtick-delimited command from the configuration section."""

    pattern = rf"(?m)^{re.escape(label)}:\s*`([^`]+)`\s*$"
    match = re.search(pattern, plan)
    if match:
        return match.group(1).strip()
    if required:
        raise PlanError(f"Missing configuration line: {label}: `<command>`")
    return None


def parse_optional_text(plan: str, label: str) -> str | None:
    """Read an optional backtick-delimited text value from the configuration section."""

    pattern = rf"(?m)^{re.escape(label)}:\s*`([^`]+)`\s*$"
    match = re.search(pattern, plan)
    if match:
        return match.group(1)
    return None


def extract_section(case_body: str, heading: str) -> str:
    """Extract text below a level-three heading up to the next heading."""

    pattern = rf"(?ms)^### {re.escape(heading)}\s*\n(.*?)(?=^### |\Z)"
    match = re.search(pattern, case_body)
    if not match:
        raise PlanError(f"Missing section: ### {heading}")
    return match.group(1).strip("\n")


def extract_fenced_text(section: str, heading: str) -> str:
    """Extract the single text code block contained in a plan section."""

    match = re.fullmatch(r"\s*```(?:text)?\s*\n(.*?)\n```\s*", section, re.DOTALL)
    if not match:
        raise PlanError(f"Section '### {heading}' must contain one fenced text block")
    return match.group(1)


def parse_plan(plan_path: Path) -> UiTestPlan:
    """Parse commands and test cases from a UI test plan."""

    plan = normalize_newlines(plan_path.read_text(encoding="utf-8"))
    program_command = parse_command(plan, "Program command", required=True)
    build_command = parse_command(plan, "Build command", required=False)
    output_starts_after = parse_optional_text(plan, "Output starts after line")

    headings = list(re.finditer(r"(?m)^## Test Case:\s*(.+?)\s*$", plan))
    if not headings:
        raise PlanError("No test cases found; add a '## Test Case: <name>' section")

    test_cases: list[UiTestCase] = []
    for index, heading_match in enumerate(headings):
        end = headings[index + 1].start() if index + 1 < len(headings) else len(plan)
        body = plan[heading_match.end():end]
        name = heading_match.group(1).strip()
        try:
            aim = extract_section(body, "Aim").strip()
            inputs = extract_fenced_text(extract_section(body, "Inputs"), "Inputs")
            expected = extract_fenced_text(
                extract_section(body, "Expected output"), "Expected output"
            )
        except PlanError as error:
            raise PlanError(f"Test case '{name}': {error}") from error

        if not aim:
            raise PlanError(f"Test case '{name}': aim must not be empty")
        if not inputs:
            raise PlanError(f"Test case '{name}': inputs must not be empty")
        test_cases.append(UiTestCase(name, aim, inputs, expected))

    assert program_command is not None
    return UiTestPlan(build_command, program_command, output_starts_after, test_cases)


def command_tokens(command: str, label: str) -> list[str]:
    """Tokenize a configured command without invoking a shell."""

    try:
        tokens = shlex.split(command)
    except ValueError as error:
        raise PlanError(f"Invalid {label}: {error}") from error
    if not tokens:
        raise PlanError(f"{label} must not be empty")
    return tokens


def print_block(label: str, content: str, input_block: bool = False) -> None:
    """Print a clearly delimited transcript or comparison block."""

    print(f"--- {label} ---")
    if not content:
        print("(empty)")
        return
    if input_block:
        for line in normalize_newlines(content).splitlines():
            print(f"> {line}")
    else:
        print(content, end="" if content.endswith("\n") else "\n")


def output_for_comparison(output: str, starts_after: str | None) -> str:
    """Trim ignored startup output before comparing, if the plan requests it."""

    if starts_after is None:
        return output

    output = normalize_newlines(output)
    marker = starts_after + "\n"
    marker_position = output.find(marker)
    if marker_position == -1:
        return output
    return output[marker_position + len(marker):]


def run_build(command: str, repo: Path) -> bool:
    """Run the optional build command and report a failure immediately."""

    print(f"=== Build: {command} ===")
    try:
        result = subprocess.run(
            command_tokens(command, "build command"),
            cwd=repo,
            capture_output=True,
            text=True,
            check=False,
        )
    except OSError as error:
        print(f"RESULT: FAIL\nCould not start build command: {error}")
        return False

    if result.stdout:
        print_block("BUILD OUTPUT", result.stdout)
    if result.stderr:
        print_block("BUILD ERROR OUTPUT", result.stderr)
    if result.returncode != 0:
        print(f"RESULT: FAIL (build exited with status {result.returncode})")
        return False
    print("RESULT: PASS\n")
    return True


def run_test_case(
    test_case: UiTestCase,
    number: int,
    program_tokens: list[str],
    repo: Path,
    timeout: float,
    output_starts_after: str | None,
) -> bool:
    """Run and report one test case, returning false on its first failure."""

    supplied_input = test_case.inputs
    if not supplied_input.endswith("\n"):
        supplied_input += "\n"

    print(f"=== Test Case {number}: {test_case.name} ===")
    print(f"Aim: {test_case.aim}")
    print_block("CONSOLE INPUT", test_case.inputs, input_block=True)

    try:
        result = subprocess.run(
            program_tokens,
            cwd=repo,
            input=supplied_input,
            capture_output=True,
            text=True,
            timeout=timeout,
            check=False,
        )
    except subprocess.TimeoutExpired as error:
        actual = error.stdout or ""
        if isinstance(actual, bytes):
            actual = actual.decode(errors="replace")
        print_block("CONSOLE OUTPUT", actual)
        print("RESULT: FAIL (program timed out)")
        print_block("EXPECTED OUTPUT", test_case.expected_output)
        print_block("ACTUAL OUTPUT", actual)
        return False
    except OSError as error:
        print_block("CONSOLE OUTPUT", "")
        print(f"RESULT: FAIL (could not start program: {error})")
        print_block("EXPECTED OUTPUT", test_case.expected_output)
        print_block("ACTUAL OUTPUT", "")
        return False

    print_block("CONSOLE OUTPUT", result.stdout)
    if result.stderr:
        print_block("CONSOLE ERROR OUTPUT", result.stderr)

    actual_for_comparison = output_for_comparison(result.stdout, output_starts_after)
    output_matches = comparable(actual_for_comparison) == comparable(
        test_case.expected_output
    )
    exited_cleanly = result.returncode == 0
    if output_matches and exited_cleanly:
        print("RESULT: PASS\n")
        return True

    if not exited_cleanly:
        print(f"RESULT: FAIL (program exited with status {result.returncode})")
    else:
        print("RESULT: FAIL (output did not match)")
    print_block("EXPECTED OUTPUT", test_case.expected_output)
    print_block("ACTUAL OUTPUT", actual_for_comparison)
    return False


def main() -> int:
    """Parse arguments, execute the plan, and stop at the first failure."""

    parser = argparse.ArgumentParser(
        description="Run fail-fast console UI tests from test/ui-test-plan.md."
    )
    parser.add_argument("--plan", default="test/ui-test-plan.md", help="test plan path")
    parser.add_argument("--repo", default=".", help="repository root")
    parser.add_argument(
        "--timeout",
        type=float,
        default=10.0,
        help="timeout in seconds for each test case (default: 10)",
    )
    args = parser.parse_args()

    repo = Path(args.repo).resolve()
    plan_path = Path(args.plan)
    if not plan_path.is_absolute():
        plan_path = repo / plan_path

    try:
        test_plan = parse_plan(plan_path)
        program_tokens = command_tokens(test_plan.program_command, "program command")
    except (OSError, PlanError) as error:
        print(f"TEST PLAN ERROR: {error}", file=sys.stderr)
        return 2

    if test_plan.build_command and not run_build(test_plan.build_command, repo):
        print("Stopped before the first test case because the build failed.")
        return 1

    for number, test_case in enumerate(test_plan.test_cases, start=1):
        if not run_test_case(
            test_case,
            number,
            program_tokens,
            repo,
            args.timeout,
            test_plan.output_starts_after,
        ):
            print("Stopped after the first failed test case; later cases were not run.")
            return 1

    print(f"All {len(test_plan.test_cases)} test case(s) passed.")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
