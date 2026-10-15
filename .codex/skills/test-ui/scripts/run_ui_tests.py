#!/usr/bin/env python3
"""Run console UI tests declared in test/ui-test-plan.md."""

from __future__ import annotations

import re
import subprocess
import sys
from dataclasses import dataclass
from pathlib import Path

PLAN_PATH = Path("test/ui-test-plan.md")
CASE_PATTERN = re.compile(
    r"^## Test Case: (?P<name>.+?)\n+"
    r"### Aim\n+(?P<aim>.*?)\n+"
    r"### Command\n+```(?:sh|bash|text)?\n(?P<command>.*?)\n```\n+"
    r"### Input\n+```(?:text)?\n(?P<input>.*?)\n```\n+"
    r"### Expected Output\n```(?:text)?\n(?P<expected>.*?)\n```",
    re.MULTILINE | re.DOTALL,
)


@dataclass
class TestCase:
    """Represent one console test from the Markdown test plan."""

    name: str
    aim: str
    command: str
    input_text: str
    expected: str


def normalize(text: str) -> str:
    """Normalize line endings without hiding meaningful whitespace differences."""
    return text.replace("\r\n", "\n").replace("\r", "\n")


def read_cases(plan_path: Path) -> list[TestCase]:
    """Parse all test cases and reject a plan that does not follow the required format."""
    if not plan_path.is_file():
        raise ValueError(f"Test plan not found: {plan_path}")
    matches = list(CASE_PATTERN.finditer(plan_path.read_text(encoding="utf-8")))
    if not matches:
        raise ValueError("No valid test cases found. Follow the format in test-ui/SKILL.md.")
    return [
        TestCase(
            name=match["name"].strip(),
            aim=match["aim"].strip(),
            command=match["command"].strip(),
            input_text=match["input"],
            expected=match["expected"] + "\n",
        )
        for match in matches
    ]


def show_block(label: str, content: str) -> None:
    """Print a labelled transcript block, including an explicit marker for empty input."""
    print(f"--- {label} ---")
    print(content if content else "<empty>")


def main() -> int:
    """Run cases in order and stop immediately when a command or assertion fails."""
    try:
        cases = read_cases(PLAN_PATH)
    except ValueError as error:
        print(f"TEST PLAN ERROR: {error}", file=sys.stderr)
        return 2

    print(f"UI test session: {PLAN_PATH}")
    for number, case in enumerate(cases, start=1):
        print(f"\n=== Test {number}: {case.name} ===")
        print(f"Aim: {case.aim}")
        show_block("Command", f"$ {case.command}")
        show_block("Console input", case.input_text)
        completed = subprocess.run(case.command, shell=True, input=case.input_text,
                                   text=True, capture_output=True)
        actual = normalize(completed.stdout)
        show_block("Console output", actual)
        if completed.returncode != 0:
            print(f"FAIL: command exited with status {completed.returncode}")
            show_block("Standard error", normalize(completed.stderr))
            return 1
        if actual != normalize(case.expected):
            print("FAIL: output did not match expected output")
            show_block("Expected output", normalize(case.expected))
            show_block("Actual output", actual)
            return 1
        print("PASS")
    print("\nAll UI tests passed.")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
