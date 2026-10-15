#!/usr/bin/env python3
"""Run the console UI sessions documented in test/ui-test-plan.md."""

from __future__ import annotations

import re
import subprocess
import sys
from dataclasses import dataclass
from pathlib import Path


PROJECT_ROOT = Path(__file__).resolve().parents[4]
PLAN_PATH = PROJECT_ROOT / "test" / "ui-test-plan.md"
TIMEOUT_SECONDS = 10


@dataclass
class TestCase:
    """A named console session and its expected standard output."""

    name: str
    aim: str
    inputs: str
    expected_output: str


def normalise_newlines(text: str) -> str:
    """Make output comparisons independent of the operating system's line ending."""
    return text.replace("\r\n", "\n").replace("\r", "\n")


def fenced_section(section: str, heading: str, case_name: str) -> str:
    """Return a required fenced block below a level-three test-case heading."""
    pattern = rf"^### {re.escape(heading)}\s*\n```[^\n]*\n(.*?)(?=\n```)"
    match = re.search(pattern, section, re.MULTILINE | re.DOTALL)
    if match is None:
        raise ValueError(f"{case_name!r} is missing a fenced '{heading}' block.")
    content = normalise_newlines(match.group(1))
    return f"{content}\n" if content else ""


def parse_plan(plan: str) -> tuple[str, list[TestCase]]:
    """Parse the small Markdown schema used by this project's UI test plan."""
    command_match = re.search(
        r"^## Program command\s*\n```(?:sh|shell)?\n(.*?)(?=\n```)",
        plan,
        re.MULTILINE | re.DOTALL,
    )
    if command_match is None:
        raise ValueError("The plan is missing a fenced '## Program command' shell block.")
    command = command_match.group(1).strip()
    if not command:
        raise ValueError("The program command cannot be empty.")

    matches = list(re.finditer(r"^## Test case:\s*(.+?)\s*$", plan, re.MULTILINE))
    if not matches:
        raise ValueError("The plan must contain at least one '## Test case:' section.")

    cases: list[TestCase] = []
    for index, match in enumerate(matches):
        end = matches[index + 1].start() if index + 1 < len(matches) else len(plan)
        section = plan[match.end():end]
        name = match.group(1).strip()
        aim_match = re.search(r"^### Aim\s*\n(.+?)(?=\n###|\Z)", section, re.MULTILINE | re.DOTALL)
        if aim_match is None:
            raise ValueError(f"{name!r} is missing an Aim.")
        cases.append(TestCase(
            name=name,
            aim=aim_match.group(1).strip(),
            inputs=fenced_section(section, "Inputs", name),
            expected_output=fenced_section(section, "Expected output", name),
        ))
    return command, cases


def print_transcript(case: TestCase, output: str) -> None:
    """Show the exact console exchange that was just tested."""
    print(f"\n=== {case.name} ===")
    print(f"Aim: {case.aim}")
    print("Console input:")
    print(case.inputs, end="" if case.inputs else "<no input>\n")
    print("Console output:")
    print(output, end="" if output else "<no output>\n")


def require_java_25() -> None:
    """Fail early when the project's required Java version is not selected."""
    result = subprocess.run(["java", "-version"], capture_output=True, text=True, check=False)
    version_text = result.stderr + result.stdout
    if result.returncode != 0 or not re.search(r'(?:version )?"?25(?:\.|\s|$)', version_text):
        raise RuntimeError("Java 25 is required. Select it with: sdk use java 25.0.3.fx-zulu")


def main() -> int:
    """Execute cases in order and stop immediately after the first failure."""
    try:
        require_java_25()
        command, cases = parse_plan(PLAN_PATH.read_text(encoding="utf-8"))
    except (OSError, RuntimeError, ValueError) as error:
        print(f"Test setup error: {error}", file=sys.stderr)
        return 2

    for case in cases:
        try:
            result = subprocess.run(
                command,
                shell=True,
                cwd=PROJECT_ROOT,
                input=case.inputs,
                capture_output=True,
                text=True,
                timeout=TIMEOUT_SECONDS,
                check=False,
            )
        except subprocess.TimeoutExpired as error:
            actual = normalise_newlines(error.stdout or "")
            print_transcript(case, actual)
            print(f"FAIL: timed out after {TIMEOUT_SECONDS} seconds.", file=sys.stderr)
            print("Expected output:\n" + case.expected_output, file=sys.stderr, end="")
            print("Actual output:\n" + actual, file=sys.stderr, end="")
            return 1

        actual = normalise_newlines(result.stdout)
        print_transcript(case, actual)
        if result.returncode != 0 or actual != case.expected_output:
            print("FAIL: output did not match the expected output.", file=sys.stderr)
            if result.returncode != 0:
                print(f"Program exit status: {result.returncode}", file=sys.stderr)
            if result.stderr:
                print("Program standard error:\n" + result.stderr, file=sys.stderr, end="" if result.stderr.endswith("\n") else "\n")
            print("Expected output:\n" + case.expected_output, file=sys.stderr, end="")
            print("Actual output:\n" + actual, file=sys.stderr, end="")
            return 1
        print("PASS")

    print(f"\nAll {len(cases)} UI test case(s) passed.")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
