#!/usr/bin/env python3
"""Run exact-output console UI tests described in a Markdown test plan."""

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
    """A single isolated console UI test case."""

    identifier: str
    title: str
    aim: str
    inputs: str
    expected_output: str


@dataclass(frozen=True)
class TestPlan:
    """Commands and cases parsed from the Markdown test plan."""

    setup_command: str
    run_command: str
    timeout_seconds: float
    cases: list[TestCase]


def _read_setting(markdown: str, name: str) -> str:
    pattern = rf"^- {re.escape(name)}: `([^`]+)`\s*$"
    match = re.search(pattern, markdown, flags=re.MULTILINE)
    if not match:
        raise ValueError(f"Missing '- {name}: `...`' setting")
    return match.group(1)


def _read_code_block(section: str, heading: str) -> str:
    pattern = rf"^### {re.escape(heading)}\s*\n\s*```text\n(.*?)^```\s*$"
    match = re.search(pattern, section, flags=re.MULTILINE | re.DOTALL)
    if not match:
        raise ValueError(f"Missing '### {heading}' text block")
    return match.group(1)


def parse_plan(plan_path: Path) -> TestPlan:
    """Parse a test plan that follows the format documented by the skill."""
    markdown = plan_path.read_text(encoding="utf-8")
    setup_command = _read_setting(markdown, "Setup command")
    run_command = _read_setting(markdown, "Run command")

    timeout_text = _read_setting(markdown, "Timeout seconds")
    try:
        timeout_seconds = float(timeout_text)
    except ValueError as error:
        raise ValueError("Timeout seconds must be a number") from error
    if timeout_seconds <= 0:
        raise ValueError("Timeout seconds must be greater than zero")

    headings = list(re.finditer(r"^## (TC-[A-Za-z0-9_-]+): (.+)\s*$", markdown, re.MULTILINE))
    if not headings:
        raise ValueError("The plan contains no '## TC-...: ...' test cases")

    cases: list[TestCase] = []
    identifiers: set[str] = set()
    for index, heading in enumerate(headings):
        section_end = headings[index + 1].start() if index + 1 < len(headings) else len(markdown)
        section = markdown[heading.end():section_end]
        identifier = heading.group(1)
        if identifier in identifiers:
            raise ValueError(f"Duplicate test case identifier: {identifier}")
        identifiers.add(identifier)

        aim_match = re.search(r"^- Aim: (.+)\s*$", section, re.MULTILINE)
        if not aim_match:
            raise ValueError(f"{identifier} is missing '- Aim: ...'")

        cases.append(
            TestCase(
                identifier=identifier,
                title=heading.group(2),
                aim=aim_match.group(1),
                inputs=_read_code_block(section, "Inputs"),
                expected_output=_read_code_block(section, "Expected output"),
            )
        )

    return TestPlan(setup_command, run_command, timeout_seconds, cases)


def _print_block(label: str, content: str) -> None:
    print(f"--- {label} ---")
    if content:
        sys.stdout.write(content)
        if not content.endswith("\n"):
            print()
    else:
        print("<empty>")
    print(f"--- end {label} ---")


def _run_shell(command: str, *, cwd: Path, timeout: float, inputs: str | None = None) -> subprocess.CompletedProcess[str]:
    return subprocess.run(
        command,
        cwd=cwd,
        input=inputs,
        text=True,
        capture_output=True,
        shell=True,
        timeout=timeout,
        check=False,
    )


def run_plan(plan: TestPlan, *, cwd: Path, quiet: bool = False) -> int:
    """Run a plan, returning immediately when a setup or test failure occurs."""
    if not quiet:
        print("===== UI test setup =====")
        print(f"Working directory: {cwd}")
        print(f"Setup command: {plan.setup_command}")
    try:
        setup = _run_shell(plan.setup_command, cwd=cwd, timeout=plan.timeout_seconds)
    except subprocess.TimeoutExpired:
        print(f"FAIL: setup timed out after {plan.timeout_seconds:g} seconds")
        return 1

    if setup.stdout and (not quiet or setup.returncode != 0):
        _print_block("setup standard output", setup.stdout)
    if setup.stderr and (not quiet or setup.returncode != 0):
        _print_block("setup standard error", setup.stderr)
    if setup.returncode != 0:
        print(f"FAIL: setup exited with status {setup.returncode}")
        return 1
    if not quiet:
        print("Setup passed.")

    for test_case in plan.cases:
        if not quiet:
            print()
            print(f"===== {test_case.identifier}: {test_case.title} =====")
            print(f"Aim: {test_case.aim}")
            _print_block("console input", test_case.inputs)

        try:
            completed = _run_shell(
                plan.run_command,
                cwd=cwd,
                timeout=plan.timeout_seconds,
                inputs=test_case.inputs,
            )
        except subprocess.TimeoutExpired as error:
            actual_output = error.stdout or ""
            if isinstance(actual_output, bytes):
                actual_output = actual_output.decode(errors="replace")
            if quiet:
                print(f"===== {test_case.identifier}: {test_case.title} =====")
                print(f"Aim: {test_case.aim}")
                _print_block("console input", test_case.inputs)
            _print_block("actual console output", actual_output)
            print(f"FAIL: test timed out after {plan.timeout_seconds:g} seconds")
            return 1

        if not quiet:
            _print_block("actual console output", completed.stdout)
        if completed.stderr and not quiet:
            _print_block("standard error", completed.stderr)

        failure_reasons: list[str] = []
        if completed.returncode != 0:
            failure_reasons.append(f"process exited with status {completed.returncode}")
        if completed.stderr:
            failure_reasons.append("process wrote to standard error")
        if completed.stdout != test_case.expected_output:
            failure_reasons.append("actual output did not exactly match expected output")

        if failure_reasons:
            if quiet:
                print(f"===== {test_case.identifier}: {test_case.title} =====")
                print(f"Aim: {test_case.aim}")
                _print_block("console input", test_case.inputs)
                _print_block("actual console output", completed.stdout)
                if completed.stderr:
                    _print_block("standard error", completed.stderr)
            print(f"FAIL: {test_case.identifier}: {'; '.join(failure_reasons)}")
            _print_block("expected console output", test_case.expected_output)
            print("--- unified diff (expected -> actual) ---")
            diff = difflib.unified_diff(
                test_case.expected_output.splitlines(keepends=True),
                completed.stdout.splitlines(keepends=True),
                fromfile="expected",
                tofile="actual",
            )
            sys.stdout.writelines(diff)
            print("--- end unified diff ---")
            print("Test session terminated immediately; later cases were not run.")
            return 1

        if not quiet:
            print(f"PASS: {test_case.identifier}")

    if not quiet:
        print()
    print(f"PASS: all {len(plan.cases)} UI test cases passed.")
    return 0


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("plan", type=Path, help="path to the Markdown UI test plan")
    parser.add_argument(
        "--cwd",
        type=Path,
        default=Path.cwd(),
        help="working directory for setup and application commands (default: current directory)",
    )
    parser.add_argument(
        "--quiet",
        action="store_true",
        help="suppress successful setup and test transcripts",
    )
    args = parser.parse_args()

    try:
        plan = parse_plan(args.plan)
    except (OSError, ValueError) as error:
        print(f"FAIL: could not read test plan: {error}", file=sys.stderr)
        return 2

    return run_plan(plan, cwd=args.cwd.resolve(), quiet=args.quiet)


if __name__ == "__main__":
    raise SystemExit(main())
