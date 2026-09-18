#!/usr/bin/env python3
"""Run command-line UI test cases described in test/ui-test-plan.md."""

from __future__ import annotations

import difflib
import re
import subprocess
import sys
from dataclasses import dataclass
from pathlib import Path

PLAN_PATH = Path("test/ui-test-plan.md")
SESSION_PATH = Path("test/ui-test-session.md")
FIELD_PATTERN = re.compile(
    r"\*\*(Command|Input|Expected output):\*\*\s*\n```[^\n]*\n(.*?)\n```",
    re.DOTALL,
)


@dataclass
class TestCase:
    """One UI test parsed from the Markdown plan."""

    name: str
    aim: str
    command: str
    user_input: str
    expected_output: str


def parse_cases(plan: str) -> list[TestCase]:
    """Parse test cases and validate the required fields in the plan."""
    parts = re.split(r"^### Test:\s*(.+?)\s*$", plan, flags=re.MULTILINE)
    cases: list[TestCase] = []
    for index in range(1, len(parts), 2):
        name, body = parts[index].strip(), parts[index + 1]
        aim_match = re.search(r"\*\*Aim:\*\*\s*(.+)", body)
        fields = dict(FIELD_PATTERN.findall(body))
        required = {"Command", "Input", "Expected output"}
        if not aim_match or set(fields) != required:
            raise ValueError(
                f"Test '{name}' must include Aim, Command, Input, and Expected output."
            )
        # Markdown fences cannot represent the final line terminator directly:
        # the newline before ``` closes the fence. Restore it for exact console
        # comparisons, since a normal console line is newline-terminated.
        expected_output = fields["Expected output"] + "\n"
        cases.append(TestCase(name, aim_match.group(1).strip(), fields["Command"],
                              fields["Input"], expected_output))
    if not cases:
        raise ValueError("No test cases found. Add a '### Test: ...' section to the plan.")
    return cases


def fenced(label: str, content: str, language: str = "text") -> str:
    """Format transcript content as a Markdown fenced block."""
    return f"**{label}:**\n```{language}\n{content}\n```\n"


def main() -> int:
    """Run each planned case, recording the session and stopping at the first failure."""
    if not PLAN_PATH.is_file():
        print(f"Test plan not found: {PLAN_PATH}", file=sys.stderr)
        return 2
    try:
        cases = parse_cases(PLAN_PATH.read_text(encoding="utf-8"))
    except ValueError as error:
        print(f"Invalid test plan: {error}", file=sys.stderr)
        return 2

    record = ["# UI test session", ""]
    for number, case in enumerate(cases, start=1):
        result = subprocess.run(
            case.command,
            shell=True,
            cwd=Path.cwd(),
            input=case.user_input + "\n",
            text=True,
            capture_output=True,
        )
        actual = result.stdout
        status = result.returncode == 0 and actual == case.expected_output
        record.extend([
            f"## {number}. {case.name}", "", f"**Aim:** {case.aim}", "",
            fenced("Command", case.command, "sh"),
            fenced("Console input", case.user_input),
            fenced("Expected output", case.expected_output),
            fenced("Actual output", actual),
            f"**Exit status:** `{result.returncode}`", "",
            f"**Result:** {'PASS' if status else 'FAIL'}", "",
        ])
        if not status:
            diff = "".join(difflib.unified_diff(
                case.expected_output.splitlines(keepends=True), actual.splitlines(keepends=True),
                fromfile="expected output", tofile="actual output",
            )) or "Output matched, but the command exited unsuccessfully.\n"
            record.extend([fenced("Difference", diff, "diff"),
                           "Testing stopped after this failed case.", ""])
            SESSION_PATH.write_text("\n".join(record), encoding="utf-8")
            print(
                f"FAIL: {case.name}\n\nExpected output:\n{case.expected_output}"
                f"\nActual output:\n{actual}\nDifference:\n{diff}"
                f"Session saved to {SESSION_PATH}"
            )
            return 1

    record.extend([f"All {len(cases)} test case(s) passed.", ""])
    SESSION_PATH.write_text("\n".join(record), encoding="utf-8")
    print(f"PASS: all {len(cases)} test case(s). Session saved to {SESSION_PATH}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
