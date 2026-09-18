"""Run automated UI tests described in test/ui-test-plan.md."""

from __future__ import annotations

import re
import subprocess
import sys
from dataclasses import dataclass
from pathlib import Path


PLAN_PATH = Path("test/ui-test-plan.md")

if hasattr(sys.stdout, "reconfigure"):
    sys.stdout.reconfigure(encoding="utf-8")
if hasattr(sys.stderr, "reconfigure"):
    sys.stderr.reconfigure(encoding="utf-8")


@dataclass
class TestCase:
    case_id: str
    title: str
    aim: str
    command: str
    inputs: str
    expected_output: str


def normalize_output(text: str) -> str:
    """Normalize output for stable console comparison."""
    text = text.replace("\r\n", "\n").replace("\r", "\n")
    lines = [line.rstrip() for line in text.split("\n")]
    while lines and lines[-1] == "":
        lines.pop()
    return "\n".join(lines)


def extract_setup_command(plan_text: str) -> str | None:
    match = re.search(r"^- Setup command:\s*`([^`]+)`\s*$", plan_text, re.MULTILINE)
    return match.group(1) if match else None


def extract_automated_command(plan_text: str) -> str | None:
    """Return the GUI test command, if the plan declares one."""
    match = re.search(r"^- Automated command:\s*`([^`]+)`\s*$", plan_text, re.MULTILINE)
    return match.group(1) if match else None


def extract_field(block: str, label: str) -> str:
    pattern = rf"^{re.escape(label)}:\s*\n```[a-zA-Z0-9_-]*\n(.*?)\n```"
    match = re.search(pattern, block, re.MULTILINE | re.DOTALL)
    if not match:
        raise ValueError(f"Missing fenced '{label}:' block")
    return match.group(1)


def parse_cases(plan_text: str) -> list[TestCase]:
    heading_pattern = re.compile(r"^###\s+((?:TC|Test)[-\w]*):\s*(.+?)\s*$", re.MULTILINE)
    matches = list(heading_pattern.finditer(plan_text))
    cases: list[TestCase] = []

    for index, match in enumerate(matches):
        start = match.end()
        end = matches[index + 1].start() if index + 1 < len(matches) else len(plan_text)
        block = plan_text[start:end]

        aim_match = re.search(r"^Aim:\s*(.+?)\s*$", block, re.MULTILINE)
        if not aim_match:
            raise ValueError(f"Missing Aim for {match.group(1)}")

        cases.append(
            TestCase(
                case_id=match.group(1),
                title=match.group(2),
                aim=aim_match.group(1),
                command=extract_field(block, "Command").strip(),
                inputs=extract_field(block, "Inputs"),
                expected_output=extract_field(block, "Expected output"),
            )
        )

    if not cases:
        raise ValueError("No test cases found. Add headings like '### TC-01: Exit'.")

    return cases


def run_command(command: str, stdin_text: str = "") -> subprocess.CompletedProcess[str]:
    if stdin_text and not stdin_text.endswith("\n"):
        stdin_text += "\n"

    return subprocess.run(
        command,
        input=stdin_text,
        encoding="utf-8",
        errors="replace",
        text=True,
        stdout=subprocess.PIPE,
        stderr=subprocess.STDOUT,
        shell=True,
    )


def print_transcript(case: TestCase, actual_output: str) -> None:
    print(f"## {case.case_id}: {case.title}")
    print(f"Aim: {case.aim}")
    print()
    print("$ " + case.command)
    if case.inputs:
        print(case.inputs, end="" if case.inputs.endswith("\n") else "\n")
    print(actual_output, end="" if actual_output.endswith("\n") else "\n")


def main() -> int:
    if not PLAN_PATH.exists():
        print(f"Missing test plan: {PLAN_PATH}", file=sys.stderr)
        return 2

    try:
        plan_text = PLAN_PATH.read_text(encoding="utf-8")
        automated_command = extract_automated_command(plan_text)
        setup_command = extract_setup_command(plan_text)
        cases = [] if automated_command else parse_cases(plan_text)
    except ValueError as error:
        print(f"Could not parse {PLAN_PATH}: {error}", file=sys.stderr)
        return 2

    if automated_command:
        print("## Automated GUI tests")
        print("$ " + automated_command)
        result = run_command(automated_command)
        if result.stdout:
            print(result.stdout, end="" if result.stdout.endswith("\n") else "\n")
        if result.returncode != 0:
            print(f"GUI tests failed with exit code {result.returncode}", file=sys.stderr)
            return result.returncode
        print("Automated GUI tests passed.")
        return 0

    if setup_command:
        print("## Setup")
        print("$ " + setup_command)
        setup_result = run_command(setup_command)
        if setup_result.stdout:
            print(setup_result.stdout, end="" if setup_result.stdout.endswith("\n") else "\n")
        if setup_result.returncode != 0:
            print(f"Setup failed with exit code {setup_result.returncode}", file=sys.stderr)
            return setup_result.returncode
        print()

    for case in cases:
        result = run_command(case.command, case.inputs)
        actual = result.stdout
        print_transcript(case, actual)

        normalized_actual = normalize_output(actual)
        normalized_expected = normalize_output(case.expected_output)

        if result.returncode != 0 or normalized_actual != normalized_expected:
            print()
            print(f"FAILED: {case.case_id}: {case.title}")
            if result.returncode != 0:
                print(f"Process exited with code {result.returncode}")
            print()
            print("Expected output:")
            print(case.expected_output)
            print()
            print("Actual output:")
            print(actual)
            return 1

        print("PASS")
        print()

    print(f"All {len(cases)} UI test case(s) passed.")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
