#!/usr/bin/env python3
"""Run console UI tests specified in a Markdown test plan."""

from __future__ import annotations

import argparse
import re
import subprocess
import sys
from dataclasses import dataclass
from pathlib import Path


@dataclass
class TestCase:
    """One console interaction and its expected complete output."""

    name: str
    aim: str
    command: str
    inputs: str
    expected_output: str


SECTION_PATTERN = re.compile(r"^## Test case: (?P<name>.+?)\s*$", re.MULTILINE)
FIELD_PATTERN = r"^### {heading}\s*\n+```(?:{language})?\n(?P<value>.*?)\n```\s*$"


def normalise_output(text: str) -> str:
    """Normalise line endings and a final newline for platform-independent comparison."""
    return text.replace("\r\n", "\n").rstrip("\n")


def read_field(case_text: str, heading: str, language: str = "") -> str:
    """Return one fenced field from a test case or raise a useful format error."""
    pattern = re.compile(FIELD_PATTERN.format(heading=re.escape(heading), language=language), re.MULTILINE | re.DOTALL)
    match = pattern.search(case_text)
    if not match:
        raise ValueError(f"Missing a fenced '{heading}' section")
    return match.group("value")


def parse_plan(plan_path: Path) -> list[TestCase]:
    """Parse test cases that follow the documented Markdown structure."""
    plan = plan_path.read_text(encoding="utf-8")
    matches = list(SECTION_PATTERN.finditer(plan))
    if not matches:
        raise ValueError("No '## Test case: <name>' headings found")

    cases = []
    for index, match in enumerate(matches):
        next_start = matches[index + 1].start() if index + 1 < len(matches) else len(plan)
        case_text = plan[match.end():next_start]
        aim_match = re.search(r"^### Aim\s*\n+(?P<value>.*?)(?=^### |\Z)", case_text, re.MULTILINE | re.DOTALL)
        if not aim_match:
            raise ValueError(f"Test case '{match.group('name')}' is missing an Aim section")
        cases.append(TestCase(
            name=match.group("name"),
            aim=aim_match.group("value").strip(),
            command=read_field(case_text, "Run command", "sh"),
            inputs=read_field(case_text, "Inputs", "text"),
            expected_output=read_field(case_text, "Expected output", "text"),
        ))
    return cases


def print_transcript(case: TestCase, actual: str) -> None:
    """Print the recorded user input and program output for one test case."""
    print(f"\n=== {case.name} ===")
    print(f"Aim: {case.aim}")
    print("Console input:")
    print(case.inputs or "<no input>")
    print("Console output:")
    print(actual or "<no output>")


def main() -> int:
    """Run every planned test, stopping at the first mismatch."""
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("plan", type=Path, help="Markdown test plan to execute")
    args = parser.parse_args()

    try:
        cases = parse_plan(args.plan)
    except (OSError, ValueError) as error:
        print(f"Cannot read test plan: {error}", file=sys.stderr)
        return 2

    for case in cases:
        result = subprocess.run(
            case.command,
            shell=True,
            executable="/bin/zsh",
            input=case.inputs + "\n",
            text=True,
            stdout=subprocess.PIPE,
            stderr=subprocess.STDOUT,
            cwd=Path.cwd(),
            check=False,
        )
        actual = normalise_output(result.stdout)
        expected = normalise_output(case.expected_output)
        print_transcript(case, actual)
        if result.returncode != 0 or actual != expected:
            print("RESULT: FAILED")
            print(f"Exit status: {result.returncode}")
            print("Expected output:")
            print(expected or "<no output>")
            print("Actual output:")
            print(actual or "<no output>")
            return 1
        print("RESULT: PASSED")

    print(f"\nAll {len(cases)} UI test case(s) passed.")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
