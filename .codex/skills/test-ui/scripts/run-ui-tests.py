#!/usr/bin/env python3
"""Run transcript-style UI tests for the HabpyDuck console program."""

from __future__ import annotations

import difflib
import re
import shutil
import subprocess
import sys
from dataclasses import dataclass
from pathlib import Path


ROOT = Path(__file__).resolve().parents[4]
TEST_PLAN = ROOT / "test" / "ui-test-plan.md"
CLASS_DIR = Path("/tmp/habpyduck-ui-test-classes")
MAIN_CLASS = "habpyduck.HabpyDuck"
SAVE_DIR = ROOT / "data"
SAVE_FILE = ROOT / "data" / "habpyduck.txt"


@dataclass
class TestCase:
    """One console UI test case from the Markdown test plan."""

    name: str
    aim: str
    inputs: str
    expected_output: str
    initial_file_content: str | None = None
    expected_file_content: str | None = None


def normalize(output: str) -> str:
    """Normalize output before comparison while preserving meaningful lines."""
    lines = output.replace("\r\n", "\n").replace("\r", "\n").split("\n")
    return "\n".join(line.rstrip() for line in lines).strip()


def extract_section(body: str, heading: str) -> str:
    """Extract text under a level-3 heading from a test-case body."""
    pattern = rf"(?ms)^### {re.escape(heading)}\s*\n(.*?)(?=^### |\Z)"
    match = re.search(pattern, body)
    if not match:
        raise ValueError(f"Missing '### {heading}' section")
    return match.group(1).strip()


def extract_code_block(section: str, heading: str) -> str:
    """Extract the first fenced code block from a section."""
    match = re.search(r"(?ms)```(?:text)?[ \t]*\n(.*?)\n```", section)
    if not match:
        raise ValueError(f"Missing fenced code block in '{heading}' section")
    return match.group(1)


def extract_optional_code_block(body: str, heading: str) -> str | None:
    """Extract an optional fenced code block from a test-case body."""
    try:
        section = extract_section(body, heading)
    except ValueError:
        return None
    return extract_code_block(section, heading)


def read_test_plan() -> list[TestCase]:
    """Read test cases from test/ui-test-plan.md."""
    if not TEST_PLAN.exists():
        raise FileNotFoundError(f"Missing test plan: {TEST_PLAN}")

    content = TEST_PLAN.read_text(encoding="utf-8")
    chunks = re.split(r"(?m)^## Test Case: ", content)
    cases: list[TestCase] = []
    for chunk in chunks[1:]:
        name_line, _, body = chunk.partition("\n")
        name = name_line.strip()
        aim = extract_section(body, "Aim")
        inputs = extract_code_block(extract_section(body, "Inputs"), "Inputs")
        expected_output = extract_code_block(
            extract_section(body, "Expected output"), "Expected output"
        )
        initial_file_content = extract_optional_code_block(body, "Initial saved file content")
        expected_file_content = extract_optional_code_block(body, "Expected saved file content")
        cases.append(
            TestCase(name, aim, inputs, expected_output, initial_file_content, expected_file_content)
        )

    if not cases:
        raise ValueError("No test cases found. Use '## Test Case: <name>' headings.")
    return cases


def compile_program() -> None:
    """Compile all Java source files used by the console program."""
    CLASS_DIR.mkdir(parents=True, exist_ok=True)
    sources = sorted((ROOT / "src" / "main" / "java").rglob("*.java"))
    command = ["javac", "-d", str(CLASS_DIR), *[str(source) for source in sources]]
    subprocess.run(command, cwd=ROOT, check=True)


def run_program(case: TestCase) -> str:
    """Run the console program once with the given input transcript."""
    if SAVE_DIR.exists():
        shutil.rmtree(SAVE_DIR)
    if case.initial_file_content is not None:
        SAVE_FILE.parent.mkdir(parents=True, exist_ok=True)
        SAVE_FILE.write_text(case.initial_file_content + "\n", encoding="utf-8")
    process = subprocess.run(
        ["java", "-cp", str(CLASS_DIR), MAIN_CLASS],
        cwd=ROOT,
        input=case.inputs,
        text=True,
        capture_output=True,
        check=False,
    )
    if process.returncode != 0:
        return process.stdout + process.stderr
    return process.stdout


def get_saved_file_content() -> str:
    """Read the saved task file, or return an empty string if it was not created."""
    if not SAVE_FILE.exists():
        return ""
    return SAVE_FILE.read_text(encoding="utf-8")


def print_transcript(case: TestCase, actual_output: str) -> None:
    """Print the input and output for one test session."""
    print(f"## Test Case: {case.name}")
    print(f"Aim: {case.aim}")
    print("\nConsole input:")
    print(case.inputs)
    print("\nConsole output:")
    print(actual_output)


def main() -> int:
    """Run UI tests, stopping immediately on the first failed test case."""
    try:
        cases = read_test_plan()
        compile_program()
    except Exception as error:
        print(f"ERROR: {error}", file=sys.stderr)
        return 1

    for index, case in enumerate(cases, start=1):
        actual_output = run_program(case)
        if normalize(actual_output) != normalize(case.expected_output):
            print(f"FAILED test {index}: {case.name}")
            print(f"Aim: {case.aim}")
            print("\nConsole input:")
            print(case.inputs)
            print("\nExpected output:")
            print(case.expected_output)
            print("\nActual output:")
            print(actual_output)
            print("\nUnified diff:")
            expected_lines = normalize(case.expected_output).splitlines()
            actual_lines = normalize(actual_output).splitlines()
            for line in difflib.unified_diff(
                expected_lines,
                actual_lines,
                fromfile="expected",
                tofile="actual",
                lineterm="",
            ):
                print(line)
            return 1

        if case.expected_file_content is not None:
            actual_file_content = get_saved_file_content()
            if normalize(actual_file_content) != normalize(case.expected_file_content):
                print(f"FAILED test {index}: {case.name}")
                print(f"Aim: {case.aim}")
                print("\nExpected saved file content:")
                print(case.expected_file_content)
                print("\nActual saved file content:")
                print(actual_file_content)
                print("\nUnified diff:")
                expected_lines = normalize(case.expected_file_content).splitlines()
                actual_lines = normalize(actual_file_content).splitlines()
                for line in difflib.unified_diff(
                    expected_lines,
                    actual_lines,
                    fromfile="expected",
                    tofile="actual",
                    lineterm="",
                ):
                    print(line)
                return 1

        print(f"PASSED test {index}: {case.name}")
        print_transcript(case, actual_output)

    print(f"All {len(cases)} UI test case(s) passed.")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
