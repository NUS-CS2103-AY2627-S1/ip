#!/usr/bin/env python3
"""Run console UI cases defined in a Markdown test plan."""

from __future__ import annotations

import argparse
import re
import subprocess
import sys
from dataclasses import dataclass
from datetime import datetime
from pathlib import Path


CASE_PATTERN = re.compile(r"^## Test case: (.+)$", re.MULTILINE)
FIELD_PATTERN = r"\*\*{label}:\*\*[ \t]*\n```text\n(.*?)```"


@dataclass
class TestCase:
    """One console UI test parsed from the Markdown plan."""

    name: str
    aim: str
    command: str
    inputs: str
    expected_output: str


def get_field(case_text: str, label: str) -> str:
    """Return the content of a required fenced test-plan field."""
    match = re.search(FIELD_PATTERN.format(label=re.escape(label)), case_text, re.DOTALL)
    if not match:
        raise ValueError(f"missing **{label}:** text block")
    return match.group(1)


def parse_plan(plan_path: Path) -> list[TestCase]:
    """Read and validate all test cases from the configured Markdown plan."""
    plan = plan_path.read_text(encoding="utf-8")
    matches = list(CASE_PATTERN.finditer(plan))
    if not matches:
        raise ValueError("no '## Test case:' sections found")

    cases = []
    for index, match in enumerate(matches):
        section_end = matches[index + 1].start() if index + 1 < len(matches) else len(plan)
        section = plan[match.end():section_end]
        aim_match = re.search(r"\*\*Aim:\*\*\s*(.+)", section)
        command_match = re.search(r"\*\*Command:\*\*\s*`(.+)`", section)
        if not aim_match:
            raise ValueError(f"{match.group(1)!r}: missing **Aim:**")
        if not command_match:
            raise ValueError(f"{match.group(1)!r}: missing **Command:**")
        cases.append(TestCase(
            name=match.group(1).strip(),
            aim=aim_match.group(1).strip(),
            command=command_match.group(1).strip(),
            inputs=get_field(section, "Inputs"),
            expected_output=get_field(section, "Expected output"),
        ))
    return cases


def display(value: str) -> str:
    """Make empty console input or output unambiguous in a transcript."""
    return value if value else "<empty>"


def run_case(case: TestCase, root: Path) -> tuple[bool, str]:
    """Run one case and return whether its actual output matches exactly."""
    result = subprocess.run(
        case.command,
        shell=True,
        cwd=root,
        input=case.inputs,
        text=True,
        stdout=subprocess.PIPE,
        stderr=subprocess.STDOUT,
    )
    actual = result.stdout
    passed = result.returncode == 0 and actual == case.expected_output
    record = (
        f"## {case.name}\n\n"
        f"Aim: {case.aim}\n\n"
        f"Command: {case.command}\n\n"
        f"Console input:\n```text\n{display(case.inputs)}\n```\n\n"
        f"Expected output:\n```text\n{display(case.expected_output)}\n```\n\n"
        f"Actual output (exit code {result.returncode}):\n```text\n{display(actual)}\n```\n\n"
        f"Result: {'PASS' if passed else 'FAIL'}\n"
    )
    return passed, record


def main() -> int:
    """Run plan cases in order and stop after the first failure."""
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("plan", type=Path, help="path to test/ui-test-plan.md")
    args = parser.parse_args()
    plan_path = args.plan.resolve()
    root = plan_path.parent.parent
    try:
        cases = parse_plan(plan_path)
    except (OSError, ValueError) as error:
        print(f"Invalid UI test plan: {error}", file=sys.stderr)
        return 2

    transcript = [f"# UI test session — {datetime.now().isoformat(timespec='seconds')}\n"]
    all_passed = True
    for case in cases:
        passed, record = run_case(case, root)
        transcript.append(record)
        if not passed:
            all_passed = False
            transcript.append("\nSession terminated after the first failing test case.\n")
            break

    session_dir = plan_path.parent / "ui-test-sessions"
    session_dir.mkdir(exist_ok=True)
    session_path = session_dir / f"session-{datetime.now():%Y%m%d-%H%M%S}.md"
    session_path.write_text("\n".join(transcript), encoding="utf-8")
    print("\n".join(transcript))
    print(f"Transcript: {session_path}")
    return 0 if all_passed else 1


if __name__ == "__main__":
    raise SystemExit(main())
