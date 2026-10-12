#!/usr/bin/env python3
"""Run console UI test cases defined in a project Markdown test plan."""

from __future__ import annotations

import argparse
import re
import subprocess
import sys
from dataclasses import dataclass
from pathlib import Path


@dataclass
class TestCase:
    """One planned console session and the output it must produce."""

    name: str
    aim: str
    inputs: str
    expected_output: str


def fenced_section(body: str, heading: str) -> str:
    """Return the text inside the fenced block immediately after a heading."""
    pattern = rf"^### {re.escape(heading)}\s*\n```text\n(.*?)^```\s*$"
    match = re.search(pattern, body, flags=re.MULTILINE | re.DOTALL)
    if not match:
        raise ValueError(f"Missing a fenced text block for '{heading}'.")
    return match.group(1)


def parse_plan(plan_path: Path) -> tuple[str, str, list[TestCase]]:
    """Read execution commands and test cases from the Markdown test plan."""
    plan = plan_path.read_text(encoding="utf-8")
    compile_match = re.search(r"^- Compile command: `(.+)`$", plan, re.MULTILINE)
    run_match = re.search(r"^- Run command: `(.+)`$", plan, re.MULTILINE)
    if not compile_match or not run_match:
        raise ValueError("Test environment needs Compile command and Run command entries.")

    sections = re.split(r"^## Test case: (.+?)\s*$", plan, flags=re.MULTILINE)
    cases: list[TestCase] = []
    for index in range(1, len(sections), 2):
        name, body = sections[index], sections[index + 1]
        aim_match = re.search(r"^### Aim\s*\n(.+?)(?=^### |\Z)", body, re.MULTILINE | re.DOTALL)
        if not aim_match:
            raise ValueError(f"Test case '{name}' is missing an Aim section.")
        cases.append(TestCase(
            name=name.strip(),
            aim=aim_match.group(1).strip(),
            inputs=fenced_section(body, "Inputs"),
            expected_output=fenced_section(body, "Expected output"),
        ))
    if not cases:
        raise ValueError("The plan does not contain any test cases.")
    return compile_match.group(1), run_match.group(1), cases


def normalise(text: str) -> str:
    """Make comparisons independent of the operating system line ending."""
    return text.replace("\r\n", "\n").replace("\r", "\n")


def show_session(case: TestCase, actual: str) -> None:
    """Print an auditable record of the inputs and standard output for a case."""
    print(f"\n=== Test case: {case.name} ===")
    print(f"Aim: {case.aim}")
    print("Console input:")
    print(case.inputs, end="" if case.inputs.endswith("\n") else "\n")
    print("Console output:")
    print(actual, end="" if actual.endswith("\n") else "\n")


def run_shell(command: str, *, input_text: str | None = None) -> subprocess.CompletedProcess[str]:
    """Run a plan command from the repository root and capture its text streams."""
    return subprocess.run(command, shell=True, text=True, input=input_text,
                          capture_output=True, check=False)


def main() -> int:
    """Execute requested cases, stopping as soon as a mismatch is found."""
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--plan", type=Path, default=Path("test/ui-test-plan.md"))
    parser.add_argument("--case", help="Run only the test case with this exact name.")
    args = parser.parse_args()

    try:
        compile_command, run_command, cases = parse_plan(args.plan)
    except (OSError, ValueError) as error:
        print(f"Cannot read test plan: {error}", file=sys.stderr)
        return 2

    if args.case:
        cases = [case for case in cases if case.name == args.case]
        if not cases:
            print(f"No test case named '{args.case}'.", file=sys.stderr)
            return 2

    print(f"Compiling with: {compile_command}")
    compilation = run_shell(compile_command)
    if compilation.returncode != 0 or compilation.stderr:
        print("Compilation failed.", file=sys.stderr)
        print(compilation.stdout, end="", file=sys.stderr)
        print(compilation.stderr, end="", file=sys.stderr)
        return 1

    for number, case in enumerate(cases, start=1):
        result = run_shell(run_command, input_text=case.inputs)
        actual = normalise(result.stdout)
        expected = normalise(case.expected_output)
        show_session(case, actual)
        if result.returncode != 0 or result.stderr or actual != expected:
            print("FAIL: test session terminated immediately.")
            if result.returncode != 0:
                print(f"Program exit status: {result.returncode}")
            if result.stderr:
                print("Standard error:")
                print(result.stderr, end="" if result.stderr.endswith("\n") else "\n")
            print("Expected output:")
            print(expected, end="" if expected.endswith("\n") else "\n")
            print("Actual output:")
            print(actual, end="" if actual.endswith("\n") else "\n")
            return 1
        print(f"PASS ({number}/{len(cases)})")

    print(f"\nPASS: {len(cases)} test case(s) completed.")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
