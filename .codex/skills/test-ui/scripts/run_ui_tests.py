#!/usr/bin/env python3
"""Run the console UI test cases declared in test/ui-test-plan.md."""

from __future__ import annotations

import re
import shutil
import subprocess
import sys
from dataclasses import dataclass
from pathlib import Path

PROJECT_ROOT = Path(__file__).resolve().parents[4]
PLAN_PATH = PROJECT_ROOT / "test/ui-test-plan.md"
SESSION_PATH = PROJECT_ROOT / "test/ui-test-session.md"
CLASS_DIRECTORY = PROJECT_ROOT / "test/.ui-test-classes"
DATA_DIRECTORY = PROJECT_ROOT / "data"
STARTUP_PROMPT = "What can I do for you?\n"
MAIN_CLASS = "bambolino.Bambolino"


@dataclass
class TestCase:
    """One test case read from the Markdown test plan."""

    name: str
    aim: str
    inputs: str
    expected_output: str


def read_test_cases(plan: str) -> list[TestCase]:
    """Parse test cases that follow the documented Markdown structure."""
    pattern = re.compile(
        r"^## Test: (?P<name>.+?)\n+"
        r"\*\*Aim:\*\* (?P<aim>.+?)\n+\n"
        r"\*\*Inputs:\*\*\n```text\n(?P<inputs>.*?)\n```\n+\n"
        r"\*\*Expected output:\*\*\n```text\n(?P<expected>.*?)\n```",
        re.MULTILINE | re.DOTALL,
    )
    cases = [
        TestCase(match["name"], match["aim"], match["inputs"], match["expected"])
        for match in pattern.finditer(plan)
    ]
    if not cases:
        raise ValueError("No valid test cases found. Follow the structure in the test-ui skill.")
    return cases


def compile_program() -> None:
    """Compile application sources with Java 25 into a temporary test directory."""
    compiler_version = subprocess.run(
        ["javac", "--version"], capture_output=True, text=True, check=True
    ).stdout.strip()
    if not compiler_version.startswith("javac 25"):
        raise RuntimeError(f"Java 25 is required, but found {compiler_version}.")

    source_files = sorted((PROJECT_ROOT / "src/main/java").rglob("*.java"))
    if not source_files:
        raise RuntimeError("No Java source files were found in src/main/java.")
    shutil.rmtree(CLASS_DIRECTORY, ignore_errors=True)
    CLASS_DIRECTORY.mkdir(parents=True)
    subprocess.run(["javac", "-d", str(CLASS_DIRECTORY), *map(str, source_files)], check=True)


def run_case(case: TestCase) -> tuple[str, str]:
    """Run one case and return output for comparison and the full console output."""
    shutil.rmtree(DATA_DIRECTORY, ignore_errors=True)
    result = subprocess.run(
        ["java", "-cp", str(CLASS_DIRECTORY), MAIN_CLASS],
        input=case.inputs + "\n",
        capture_output=True,
        text=True,
        cwd=PROJECT_ROOT,
    )
    if result.returncode != 0:
        raise RuntimeError(f"The program ended with exit code {result.returncode}:\n{result.stderr}")
    if STARTUP_PROMPT not in result.stdout:
        raise RuntimeError("Could not find Bambolino's startup prompt in the program output.")
    return result.stdout.split(STARTUP_PROMPT, maxsplit=1)[1], result.stdout


def write_session(records: list[tuple[TestCase, str, str, bool]]) -> None:
    """Write a readable record of all console input and output observed so far."""
    lines = ["# UI Test Session", ""]
    for case, actual, full_output, passed in records:
        status = "PASSED" if passed else "FAILED"
        lines.extend([
            f"## {status}: {case.name}", "", f"**Aim:** {case.aim}", "",
            "**Console input:**", "```text", case.inputs, "```", "",
            "**Console output:**", "```text", full_output.rstrip("\n"), "```", "",
        ])
    SESSION_PATH.parent.mkdir(exist_ok=True)
    SESSION_PATH.write_text("\n".join(lines), encoding="utf-8")


def main() -> int:
    """Compile the program, execute cases in order, and stop at the first failure."""
    try:
        cases = read_test_cases(PLAN_PATH.read_text(encoding="utf-8"))
        compile_program()
    except (OSError, subprocess.CalledProcessError, RuntimeError, ValueError) as error:
        print(f"Test setup failed: {error}", file=sys.stderr)
        return 2

    records: list[tuple[TestCase, str, str, bool]] = []
    for case in cases:
        try:
            actual, full_output = run_case(case)
        except (OSError, RuntimeError) as error:
            print(f"Test '{case.name}' could not run: {error}", file=sys.stderr)
            return 2
        passed = actual == case.expected_output + "\n"
        records.append((case, actual, full_output, passed))
        write_session(records)
        if not passed:
            print(f"FAILED: {case.name}")
            print("Expected output:")
            print(case.expected_output)
            print("Actual output:")
            print(actual.rstrip("\n"))
            print(f"Session record: {SESSION_PATH}")
            return 1
        print(f"PASSED: {case.name}")

    print(f"All {len(cases)} UI test(s) passed.")
    print(f"Session record: {SESSION_PATH}")
    return 0


if __name__ == "__main__":
    sys.exit(main())
