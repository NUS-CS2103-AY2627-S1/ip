#!/usr/bin/env python3
"""Run command-line UI tests defined in test/ui-test-plan.md."""

from __future__ import annotations

import argparse
import re
import subprocess
import sys
from dataclasses import dataclass
from pathlib import Path


CASE_PATTERN = re.compile(
    r"^## Test Case:\s*(?P<title>[^\n]+)\n+"
    r"Aim:\s*(?P<aim>[^\n]+)\n+"
    r"(?:Run command:\s*`(?P<run_command>[^`\n]+)`\n+)?"
    r"Input:\s*\n```(?:text)?\n(?P<input>.*?)\n```\n+"
    r"Expected output:\s*\n```(?:text)?\n(?P<expected>.*?)\n```",
    re.MULTILINE | re.DOTALL,
)


@dataclass
class TestCase:
    """One executable UI test and its expected terminal output."""

    title: str
    aim: str
    run_command: str | None
    input_text: str
    expected_output: str


def field_value(plan_text: str, field_name: str, required: bool = True) -> str | None:
    """Return a backtick-delimited configuration value from the plan."""
    match = re.search(
        rf"^-\s*{re.escape(field_name)}:\s*`([^`]+)`\s*$",
        plan_text,
        re.MULTILINE,
    )
    if match:
        return match.group(1)
    if required:
        raise ValueError(f"Missing `{field_name}` in the Configuration section.")
    return None


def parse_plan(plan_path: Path) -> tuple[str | None, str, int, list[TestCase]]:
    """Parse the documented UI test-plan format."""
    plan_text = plan_path.read_text(encoding="utf-8")
    setup_command = field_value(plan_text, "Setup command", required=False)
    run_command = field_value(plan_text, "Run command")
    timeout_text = field_value(plan_text, "Timeout seconds")
    try:
        timeout_seconds = int(timeout_text)
    except ValueError as error:
        raise ValueError("Timeout seconds must be a whole number.") from error

    cases = [
        TestCase(
            title=match.group("title").strip(),
            aim=match.group("aim").strip(),
            run_command=(match.group("run_command") or "").strip() or None,
            input_text=match.group("input"),
            expected_output=match.group("expected"),
        )
        for match in CASE_PATTERN.finditer(plan_text)
    ]
    if not cases:
        raise ValueError("No test cases found. Use the documented Test Case format.")
    return setup_command, run_command, timeout_seconds, cases


def normalized(text: str) -> str:
    """Normalize platform line endings while retaining all meaningful whitespace."""
    return text.replace("\r\n", "\n").replace("\r", "\n").rstrip("\n")


def print_block(title: str, text: str) -> None:
    """Print a labelled transcript block without hiding blank lines."""
    print(f"--- {title} ---")
    print(text, end="" if text.endswith("\n") or not text else "\n")


def run_setup(command: str | None, repo: Path) -> bool:
    """Run the optional build/setup command once before the test session."""
    if command is None:
        return True
    print("=== Setup ===")
    print(f"Command: {command}")
    result = subprocess.run(
        command,
        shell=True,
        cwd=repo,
        text=True,
        capture_output=True,
    )
    setup_output = result.stdout + result.stderr
    if setup_output:
        print_block("Setup output", setup_output)
    if result.returncode != 0:
        print(f"Setup failed with exit code {result.returncode}.")
        return False
    print("Setup passed.\n")
    return True


def run_case(
    case: TestCase,
    command: str,
    timeout_seconds: int,
    repo: Path,
) -> tuple[bool, str, str]:
    """Run one fresh program process and return its result and captured output."""
    try:
        result = subprocess.run(
            command,
            shell=True,
            cwd=repo,
            input=case.input_text + "\n",
            text=True,
            capture_output=True,
            timeout=timeout_seconds,
        )
    except subprocess.TimeoutExpired as error:
        partial_output = (error.stdout or "") + (error.stderr or "")
        return False, partial_output, f"Timed out after {timeout_seconds} seconds."

    actual_output = result.stdout + result.stderr
    if result.returncode != 0:
        return False, actual_output, f"Program exited with code {result.returncode}."
    if normalized(actual_output) != normalized(case.expected_output):
        return False, actual_output, "Actual output did not match expected output."
    return True, actual_output, ""


def main() -> int:
    """Run test cases in order and stop as soon as one fails."""
    parser = argparse.ArgumentParser(
        description="Run command-line UI tests from a Markdown plan."
    )
    parser.add_argument(
        "--plan",
        default="test/ui-test-plan.md",
        help="path to the Markdown test plan",
    )
    args = parser.parse_args()

    repo = Path.cwd()
    plan_path = (repo / args.plan).resolve()
    if not plan_path.is_file():
        print(f"Test plan not found: {plan_path}", file=sys.stderr)
        return 2

    try:
        setup_command, run_command, timeout_seconds, cases = parse_plan(plan_path)
    except ValueError as error:
        print(f"Invalid test plan: {error}", file=sys.stderr)
        return 2

    if not run_setup(setup_command, repo):
        return 1

    for number, case in enumerate(cases, start=1):
        print(f"=== Test {number}: {case.title} ===")
        print(f"Aim: {case.aim}")
        case_command = case.run_command or run_command
        if case.run_command:
            print(f"Run command: {case_command}")
        print_block("Console input", case.input_text)
        passed, actual_output, failure_reason = run_case(
            case,
            case_command,
            timeout_seconds,
            repo,
        )
        print_block("Console output", actual_output)
        if passed:
            print("Result: PASS\n")
            continue

        print(f"Result: FAIL - {failure_reason}")
        print_block("Expected output", case.expected_output)
        print("Test session terminated after the first failure.")
        return 1

    print(f"All {len(cases)} UI test case(s) passed.")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
