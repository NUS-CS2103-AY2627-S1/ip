#!/usr/bin/env python3
"""Run fail-fast console UI tests recorded in a Markdown test plan."""

from __future__ import annotations

import argparse
import re
import subprocess
import sys
from dataclasses import dataclass
from pathlib import Path
from typing import List, Optional, Union


@dataclass(frozen=True)
class TestCase:
    """One independently executed console UI scenario."""

    case_id: str
    title: str
    aim: str
    inputs: str
    expected_output: str


@dataclass(frozen=True)
class TestPlan:
    """Execution configuration and ordered test cases parsed from Markdown."""

    build_command: Optional[str]
    run_command: str
    working_directory: Path
    timeout_seconds: float
    cases: List[TestCase]


class PlanError(ValueError):
    """Indicate that a test plan is missing required or valid content."""


def normalize_newlines(text: str) -> str:
    """Normalize Windows and legacy Mac line endings to LF."""

    return text.replace("\r\n", "\n").replace("\r", "\n")


def captured_text(value: Union[str, bytes, None]) -> str:
    """Convert subprocess output to normalized text."""

    if value is None:
        return ""
    if isinstance(value, bytes):
        value = value.decode("utf-8", errors="replace")
    return normalize_newlines(value)


def configuration_value(markdown: str, name: str) -> str:
    """Return a backtick-delimited value from the Configuration section."""

    pattern = rf"(?mi)^-\s*{re.escape(name)}:\s*`([^`]*)`\s*$"
    match = re.search(pattern, markdown)
    if not match:
        raise PlanError(f"Missing configuration item: {name}")
    return match.group(1)


def fenced_block(section: str, heading: str) -> str:
    """Return literal text from the fenced block following a level-four heading."""

    pattern = (
        rf"(?ms)^####\s+{re.escape(heading)}\s*$\n"
        rf"\s*^```(?:text|console)?[ \t]*$\n(.*?)^```[ \t]*$"
    )
    match = re.search(pattern, section)
    if not match:
        raise PlanError(f"Missing or malformed '{heading}' fenced block")
    return normalize_newlines(match.group(1))


def parse_plan(plan_path: Path) -> TestPlan:
    """Parse configuration and test cases from a UI test plan."""

    markdown = normalize_newlines(plan_path.read_text(encoding="utf-8"))
    build_value = configuration_value(markdown, "Build command").strip()
    run_command_value = configuration_value(markdown, "Run command").strip()
    working_value = configuration_value(markdown, "Working directory").strip()
    timeout_value = configuration_value(markdown, "Timeout seconds").strip()

    if not run_command_value:
        raise PlanError("Run command must not be empty")
    if not working_value:
        raise PlanError("Working directory must not be empty")

    try:
        timeout_seconds = float(timeout_value)
    except ValueError as error:
        raise PlanError("Timeout seconds must be a number") from error
    if timeout_seconds <= 0:
        raise PlanError("Timeout seconds must be greater than zero")

    heading_pattern = re.compile(
        r"(?m)^###\s+(TC-[A-Za-z0-9_-]+)\s+(?:—|-)\s+(.+?)\s*$"
    )
    headings = list(heading_pattern.finditer(markdown))
    if not headings:
        raise PlanError("No test cases found")

    cases: List[TestCase] = []
    seen_ids = set()
    for index, heading_match in enumerate(headings):
        start = heading_match.end()
        end = headings[index + 1].start() if index + 1 < len(headings) else len(markdown)
        section = markdown[start:end]
        case_id = heading_match.group(1)
        if case_id in seen_ids:
            raise PlanError(f"Duplicate test case ID: {case_id}")
        seen_ids.add(case_id)

        aim_match = re.search(r"(?m)^-\s*Aim:\s*(.+?)\s*$", section)
        if not aim_match:
            raise PlanError(f"{case_id}: missing Aim")

        try:
            inputs = fenced_block(section, "Inputs")
            expected = fenced_block(section, "Expected output")
        except PlanError as error:
            raise PlanError(f"{case_id}: {error}") from error

        cases.append(
            TestCase(
                case_id=case_id,
                title=heading_match.group(2),
                aim=aim_match.group(1),
                inputs=inputs,
                expected_output=expected,
            )
        )

    repository_root = plan_path.resolve().parent.parent
    configured_working_directory = Path(working_value)
    if configured_working_directory.is_absolute():
        working_directory = configured_working_directory.resolve()
    else:
        working_directory = (repository_root / configured_working_directory).resolve()
    if not working_directory.is_dir():
        raise PlanError(f"Working directory does not exist: {working_directory}")

    return TestPlan(
        build_command=None if build_value.lower() == "none" else build_value,
        run_command=run_command_value,
        working_directory=working_directory,
        timeout_seconds=timeout_seconds,
        cases=cases,
    )


def run_command(
    command: str, cwd: Path, timeout: float, inputs: str = ""
) -> subprocess.CompletedProcess:
    """Execute a configured shell command with merged stdout and stderr."""

    return subprocess.run(
        command,
        cwd=str(cwd),
        input=inputs,
        stdout=subprocess.PIPE,
        stderr=subprocess.STDOUT,
        text=True,
        encoding="utf-8",
        errors="replace",
        shell=True,
        timeout=timeout,
        check=False,
    )


def print_block(label: str, content: str) -> None:
    """Print a labeled literal block while making an absent final newline visible."""

    print(f"--- {label} ---")
    if content:
        sys.stdout.write(content)
        if not content.endswith("\n"):
            print("\n[no final newline]")
    else:
        print("[empty]")


def execute(plan: TestPlan) -> int:
    """Build once, execute cases in order, and stop on the first failure."""

    if plan.build_command:
        print("=== Build ===")
        print(f"$ {plan.build_command}")
        try:
            build = run_command(
                plan.build_command,
                plan.working_directory,
                plan.timeout_seconds,
            )
        except subprocess.TimeoutExpired as error:
            print(f"[FAIL] Build timed out after {plan.timeout_seconds:g} seconds")
            if error.stdout:
                print_block("BUILD OUTPUT", captured_text(error.stdout))
            return 1
        build_output = captured_text(build.stdout)
        if build_output:
            print_block("BUILD OUTPUT", build_output)
        if build.returncode != 0:
            print(f"[FAIL] Build exited with code {build.returncode}")
            return 1
        print("[PASS] Build completed")

    print("=== Console test session ===")
    passed = 0
    for case in plan.cases:
        print(f"=== {case.case_id} - {case.title} ===")
        print(f"Aim: {case.aim}")
        print_block("CONSOLE INPUT", case.inputs)

        try:
            result = run_command(
                plan.run_command,
                plan.working_directory,
                plan.timeout_seconds,
                case.inputs,
            )
        except subprocess.TimeoutExpired as error:
            actual = captured_text(error.stdout)
            print_block("CONSOLE OUTPUT (ACTUAL)", actual)
            print(f"[FAIL] {case.case_id} timed out after {plan.timeout_seconds:g} seconds")
            print_block("EXPECTED OUTPUT", case.expected_output)
            return 1

        actual = captured_text(result.stdout)
        print_block("CONSOLE OUTPUT (ACTUAL)", actual)
        if result.returncode != 0:
            print(f"[FAIL] {case.case_id} exited with code {result.returncode}")
            print_block("EXPECTED OUTPUT", case.expected_output)
            return 1
        if actual != case.expected_output:
            print(f"[FAIL] {case.case_id} output did not match")
            print_block("EXPECTED OUTPUT", case.expected_output)
            return 1

        passed += 1
        print(f"[PASS] {case.case_id}")

    print(f"[PASS] {passed}/{len(plan.cases)} test cases passed")
    return 0


def main() -> int:
    """Parse command-line arguments and run the requested test plan."""

    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("plan", type=Path, help="path to test/ui-test-plan.md")
    arguments = parser.parse_args()
    try:
        plan = parse_plan(arguments.plan)
    except (OSError, PlanError) as error:
        print(f"[ERROR] Invalid UI test plan: {error}", file=sys.stderr)
        return 2
    return execute(plan)


if __name__ == "__main__":
    raise SystemExit(main())
