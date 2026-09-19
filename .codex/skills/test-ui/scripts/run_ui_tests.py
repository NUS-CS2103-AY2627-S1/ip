#!/usr/bin/env python3
"""Run Markdown-defined console UI tests and print their transcripts."""

from __future__ import annotations

import re
import subprocess
import sys
import textwrap
from dataclasses import dataclass
from pathlib import Path


CASE_HEADING = re.compile(r"^###\s+(.+?)\s*$", re.MULTILINE)
AIM = re.compile(r"^- Aim:\s*(?P<value>.+?)\s*$", re.MULTILINE)
FIELD = re.compile(
    r"^- (?P<name>Command|Inputs|Expected output):\s*\n"
    r"^[ \t]*```[^\n]*\n(?P<value>.*?)\n^[ \t]*```\s*$",
    re.MULTILINE | re.DOTALL,
)


@dataclass(frozen=True)
class TestCase:
    """Represent one executable console UI test case."""

    name: str
    aim: str
    command: str
    inputs: str
    expected_output: str


def normalize(text: str) -> str:
    """Normalize line endings without weakening exact-output comparisons."""
    return text.replace("\r\n", "\n").replace("\r", "\n")


def block_text(value: str) -> str:
    """Remove Markdown indentation and restore the code block's final newline."""
    return textwrap.dedent(value) + "\n"


def parse_cases(plan_path: Path) -> list[TestCase]:
    """Parse the required test-case structure from a Markdown plan."""
    plan = plan_path.read_text(encoding="utf-8")
    headings = list(CASE_HEADING.finditer(plan))
    cases: list[TestCase] = []
    for index, heading in enumerate(headings):
        section_end = headings[index + 1].start() if index + 1 < len(headings) else len(plan)
        section = plan[heading.end():section_end]
        aim_match = AIM.search(section)
        values = {match.group("name"): block_text(match.group("value")) for match in FIELD.finditer(section)}
        required = {"Command", "Inputs", "Expected output"}
        missing = required - values.keys()
        if not aim_match:
            missing.add("Aim")
        if missing:
            missing_text = ", ".join(sorted(missing))
            raise ValueError(f"{heading.group(1)!r} is missing: {missing_text}")
        cases.append(
            TestCase(
                name=heading.group(1),
                aim=aim_match.group("value"),
                command=values["Command"],
                inputs=values["Inputs"],
                expected_output=values["Expected output"],
            )
        )
    if not cases:
        raise ValueError("No test cases found. Add headings beginning with '### '.")
    return cases


def show_block(label: str, content: str) -> None:
    """Print a labeled transcript block while preserving its exact contents."""
    print(f"--- {label} ---")
    print(content, end="" if content.endswith("\n") or not content else "\n")


def main() -> int:
    """Execute test cases in order and stop on the first failure."""
    if len(sys.argv) != 2:
        print("Usage: run_ui_tests.py <test-plan.md>", file=sys.stderr)
        return 2

    plan_path = Path(sys.argv[1])
    try:
        cases = parse_cases(plan_path)
    except (OSError, ValueError) as error:
        print(f"Cannot read test plan: {error}", file=sys.stderr)
        return 2

    for case_number, case in enumerate(cases, start=1):
        print(f"\n=== Test {case_number}: {case.name} ===")
        print(f"Aim: {case.aim}")
        show_block("Console input", case.inputs)
        try:
            result = subprocess.run(
                case.command,
                shell=True,
                executable="/bin/zsh",
                input=case.inputs,
                text=True,
                stdout=subprocess.PIPE,
                stderr=subprocess.STDOUT,
                cwd=plan_path.parent.parent,
                timeout=30,
            )
        except subprocess.TimeoutExpired as error:
            actual = normalize(error.stdout or "")
            show_block("Console output", actual)
            print("RESULT: FAILED (program did not finish within 30 seconds)")
            return 1

        actual = normalize(result.stdout)
        expected = normalize(case.expected_output)
        show_block("Console output", actual)
        if result.returncode != 0 or actual != expected:
            print("RESULT: FAILED")
            print(f"Exit code: {result.returncode}")
            show_block("Expected output", expected)
            show_block("Actual output", actual)
            return 1
        print("RESULT: PASSED")

    print(f"\nAll {len(cases)} UI test case(s) passed.")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
