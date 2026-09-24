#!/usr/bin/env python3
"""Run planned console UI tests and record the complete test session."""

from __future__ import annotations

import re
import subprocess
import sys
from dataclasses import dataclass
from pathlib import Path


ROOT = Path(__file__).resolve().parents[4]
PLAN_PATH = ROOT / "test" / "ui-test-plan.md"
SESSION_PATH = ROOT / "test" / "ui-test-session.md"
BLOCK_FIELD_PATTERN = re.compile(
    r"\*\*(Command|Input|Expected output):\*\*\s*\n```text\n(.*?)\n```",
    re.DOTALL,
)
AIM_PATTERN = re.compile(r"\*\*Aim:\*\*\s*(.+)")


@dataclass
class UiTestCase:
    """One planned console interaction and the output it should produce."""

    name: str
    aim: str
    command: str
    user_input: str
    expected_output: str


def normalize(text: str) -> str:
    """Normalize line endings and ignore only one trailing newline."""
    return text.replace("\r\n", "\n").replace("\r", "\n").rstrip("\n")


def parse_plan(plan: str) -> list[UiTestCase]:
    """Parse the documented Markdown test-plan format into test cases."""
    sections = re.split(r"^##\s+", plan, flags=re.MULTILINE)[1:]
    cases: list[UiTestCase] = []
    for section in sections:
        name, _, body = section.partition("\n")
        fields = {field: value for field, value in BLOCK_FIELD_PATTERN.findall(body)}
        aim_match = AIM_PATTERN.search(body)
        if aim_match:
            fields["Aim"] = aim_match.group(1).strip()
        required = {"Aim", "Command", "Input", "Expected output"}
        missing = required - fields.keys()
        if missing:
            raise ValueError(f"Test case '{name.strip()}' is missing: {', '.join(sorted(missing))}")
        cases.append(
            UiTestCase(
                name=name.strip(),
                aim=fields["Aim"].strip(),
                command=fields["Command"].strip(),
                user_input=fields["Input"],
                expected_output=fields["Expected output"],
            )
        )
    if not cases:
        raise ValueError("No test cases found. Add level-two test case headings to test/ui-test-plan.md.")
    return cases


def block(label: str, value: str) -> str:
    """Format one command, input, or output value for the session record."""
    return f"**{label}:**\n```text\n{value}\n```\n"


def run_case(case: UiTestCase) -> tuple[bool, str]:
    """Run a case once and return whether its captured output matched."""
    completed = subprocess.run(
        case.command,
        input=case.user_input,
        text=True,
        stdout=subprocess.PIPE,
        stderr=subprocess.STDOUT,
        shell=True,
        cwd=ROOT,
    )
    actual = completed.stdout
    passed = completed.returncode == 0 and normalize(actual) == normalize(case.expected_output)
    return passed, actual


def main() -> int:
    """Execute tests in plan order, stopping at the first failure."""
    try:
        cases = parse_plan(PLAN_PATH.read_text(encoding="utf-8"))
    except (FileNotFoundError, ValueError) as error:
        print(f"Cannot run UI tests: {error}", file=sys.stderr)
        return 2

    record = ["# UI test session", ""]
    for index, case in enumerate(cases, start=1):
        passed, actual = run_case(case)
        record.extend(
            [
                f"## {index}. {case.name} — {'PASSED' if passed else 'FAILED'}",
                "",
                f"**Aim:** {case.aim}",
                "",
                block("Command", case.command),
                block("Console input", case.user_input),
                block("Expected output", case.expected_output),
                block("Actual output", actual),
            ]
        )
        if not passed:
            record.append("Testing stopped after this failure; later cases were not run.")
            break

    SESSION_PATH.write_text("\n".join(record).rstrip() + "\n", encoding="utf-8")
    print(SESSION_PATH.read_text(encoding="utf-8"), end="")
    return 0 if all("— PASSED" in line for line in record if line.startswith("## ")) else 1


if __name__ == "__main__":
    raise SystemExit(main())
