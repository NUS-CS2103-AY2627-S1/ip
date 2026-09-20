#!/usr/bin/env python3
"""Run console UI cases declared in a Markdown test plan."""

from __future__ import annotations

import argparse
import re
import subprocess
import sys
from dataclasses import dataclass
from pathlib import Path


@dataclass
class TestCase:
    """One isolated program session described by the test plan."""

    name: str
    aim: str
    input_text: str
    expected_output: str


def block_after(label: str, text: str, required: bool = True) -> str:
    """Return the fenced-text block following a Markdown label."""
    match = re.search(
        rf"^{re.escape(label)}:\s*\n```(?:text|sh)?\n(.*?)\n```",
        text,
        flags=re.MULTILINE | re.DOTALL,
    )
    if match:
        return match.group(1)
    if required:
        raise ValueError(f"Missing a fenced block after '{label}:'")
    return ""


def parse_plan(path: Path) -> tuple[str, str, list[TestCase]]:
    """Parse compile/run commands and test cases from the supported plan format."""
    plan = path.read_text(encoding="utf-8")
    compile_command = block_after("Compile", plan)
    run_command = block_after("Run", plan)
    sections = re.split(r"^### Test case: ", plan, flags=re.MULTILINE)[1:]
    cases: list[TestCase] = []
    for section in sections:
        name, _, body = section.partition("\n")
        aim_match = re.search(r"^Aim: (.+)$", body, flags=re.MULTILINE)
        if not aim_match:
            raise ValueError(f"Test case '{name}' is missing 'Aim:'.")
        cases.append(
            TestCase(
                name=name.strip(),
                aim=aim_match.group(1).strip(),
                input_text=block_after("Input", body),
                expected_output=block_after("Expected output", body),
            )
        )
    if not cases:
        raise ValueError("The plan contains no '### Test case:' sections.")
    return compile_command, run_command, cases


def normalise(text: str) -> str:
    """Normalise line endings while retaining all meaningful console whitespace."""
    return text.replace("\r\n", "\n").rstrip("\n")


def run_shell(command: str, *, input_text: str | None = None) -> subprocess.CompletedProcess[str]:
    """Run a plan command through zsh so its setup commands and redirects work."""
    return subprocess.run(
        command,
        shell=True,
        executable="/bin/zsh",
        input=input_text,
        text=True,
        capture_output=True,
        check=False,
    )


def print_transcript(case: TestCase, actual: str) -> None:
    """Print an auditable record of the console session."""
    print(f"\n=== {case.name} ===")
    print(f"Aim: {case.aim}")
    print("Console input:")
    print(case.input_text)
    print("Console output:")
    print(actual, end="" if actual.endswith("\n") else "\n")


def main() -> int:
    """Compile the program, run cases in order, and stop after the first failure."""
    parser = argparse.ArgumentParser()
    parser.add_argument("plan", type=Path, nargs="?", default=Path("test/ui-test-plan.md"))
    arguments = parser.parse_args()

    try:
        compile_command, run_command, cases = parse_plan(arguments.plan)
    except (OSError, ValueError) as error:
        print(f"Cannot read test plan: {error}", file=sys.stderr)
        return 2

    compilation = run_shell(compile_command)
    if compilation.returncode != 0:
        print("Compilation failed; no UI test was run.", file=sys.stderr)
        print(compilation.stdout + compilation.stderr, file=sys.stderr)
        return compilation.returncode or 1

    for case in cases:
        result = run_shell(run_command, input_text=case.input_text + "\n")
        actual = result.stdout + result.stderr
        print_transcript(case, actual)
        if result.returncode != 0 or normalise(actual) != normalise(case.expected_output):
            print("RESULT: FAILED — stopping the test session.")
            print("Expected output:")
            print(case.expected_output)
            print("Actual output:")
            print(actual, end="" if actual.endswith("\n") else "\n")
            return result.returncode or 1
        print("RESULT: PASSED")

    print(f"\nAll {len(cases)} UI test case(s) passed.")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
