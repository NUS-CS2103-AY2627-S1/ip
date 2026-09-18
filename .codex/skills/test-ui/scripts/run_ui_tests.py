#!/usr/bin/env python3
"""
Run console UI tests defined in test/ui-test-plan.md.
"""

from __future__ import annotations

import re
import subprocess
import sys
from dataclasses import dataclass
from pathlib import Path


ROOT = Path(__file__).resolve().parents[4]
PLAN_PATH = ROOT / "test" / "ui-test-plan.md"
OUTPUT_PATH = ROOT / "_temp" / "ui-test-record.md"
JAVA_SRC_DIR = ROOT / "src" / "main" / "java"
DATA_FILE_PATH = ROOT / "data" / "friday.txt"
ARCHIVE_FILE_PATH = ROOT / "data" / "archive.txt"
MAIN_CLASS = "friday.Friday"


@dataclass
class TestCase:
    name: str
    aim: str
    inputs: str
    expected_output: str
    initial_data: str | None


def normalize_output(text: str) -> str:
    return text.replace("\r\n", "\n").strip()


def parse_test_cases(plan_text: str) -> list[TestCase]:
    pattern = re.compile(
        r"^## Test Case: (?P<name>[^\n]+)\n"
        r"Aim:\n(?P<aim>.*?)(?:\n\n|\n)"
        r"Inputs:\n```text\n(?P<inputs>.*?)\n```\n\n"
        r"Expected Output:\n```text\n(?P<expected>.*?)\n```"
        r"(?:\n\nInitial Data:\n```text\n(?P<initial_data>.*?)\n```)?",
        re.MULTILINE | re.DOTALL,
    )
    cases = []
    for match in pattern.finditer(plan_text):
        cases.append(
            TestCase(
                name=match.group("name").strip(),
                aim=match.group("aim").strip(),
                inputs=match.group("inputs").strip(),
                expected_output=match.group("expected").strip(),
                initial_data=(
                    match.group("initial_data").strip()
                    if match.group("initial_data") is not None
                    else None
                ),
            )
        )
    return cases


def compile_sources() -> None:
    java_files = sorted(str(path) for path in JAVA_SRC_DIR.rglob("*.java"))
    if not java_files:
        raise RuntimeError("No Java source files found in src/main/java.")

    result = subprocess.run(
        ["javac", *java_files],
        cwd=ROOT,
        capture_output=True,
        text=True,
        check=False,
    )
    if result.returncode != 0:
        raise RuntimeError(
            "Compilation failed.\n"
            f"stdout:\n{result.stdout}\n"
            f"stderr:\n{result.stderr}"
        )


def run_case(test_case: TestCase) -> str:
    case_input = test_case.inputs + "\n"
    result = subprocess.run(
        ["java", "-cp", str(JAVA_SRC_DIR), MAIN_CLASS],
        cwd=ROOT,
        input=case_input,
        capture_output=True,
        text=True,
        check=False,
    )
    if result.returncode != 0:
        raise RuntimeError(
            f"Program exited with code {result.returncode} for test case '{test_case.name}'.\n"
            f"stdout:\n{result.stdout}\n"
            f"stderr:\n{result.stderr}"
        )
    return result.stdout


def prepare_data_file(test_case: TestCase) -> None:
    """Give each test case an isolated task data file."""
    DATA_FILE_PATH.unlink(missing_ok=True)
    ARCHIVE_FILE_PATH.unlink(missing_ok=True)
    try:
        DATA_FILE_PATH.parent.rmdir()
    except OSError:
        pass
    if test_case.initial_data is not None:
        DATA_FILE_PATH.parent.mkdir(parents=True, exist_ok=True)
        DATA_FILE_PATH.write_text(test_case.initial_data + "\n", encoding="utf-8")


def write_record(records: list[tuple[TestCase, str]]) -> None:
    OUTPUT_PATH.parent.mkdir(parents=True, exist_ok=True)
    lines = ["# UI Test Record", ""]
    for case, actual_output in records:
        lines.extend(
            [
                f"## Test Case: {case.name}",
                f"Aim: {case.aim}",
                "",
                "Input:",
                "```text",
                case.inputs,
                "```",
                "",
                "Output:",
                "```text",
                actual_output.strip(),
                "```",
                "",
            ]
        )
    OUTPUT_PATH.write_text("\n".join(lines) + "\n")


def main() -> int:
    if not PLAN_PATH.exists():
        print(f"Test plan not found: {PLAN_PATH}", file=sys.stderr)
        return 1

    plan_text = PLAN_PATH.read_text()
    test_cases = parse_test_cases(plan_text)
    if not test_cases:
        print("No valid test cases found in test/ui-test-plan.md.", file=sys.stderr)
        return 1

    try:
        compile_sources()
    except RuntimeError as error:
        print(str(error), file=sys.stderr)
        return 1

    original_data = DATA_FILE_PATH.read_bytes() if DATA_FILE_PATH.exists() else None
    original_archive_data = ARCHIVE_FILE_PATH.read_bytes() if ARCHIVE_FILE_PATH.exists() else None
    records: list[tuple[TestCase, str]] = []
    try:
        for test_case in test_cases:
            prepare_data_file(test_case)
            try:
                actual_output = run_case(test_case)
            except RuntimeError as error:
                print(str(error), file=sys.stderr)
                write_record(records)
                return 1

            records.append((test_case, actual_output))

            if normalize_output(actual_output) != normalize_output(test_case.expected_output):
                write_record(records)
                print(f"FAILED: {test_case.name}", file=sys.stderr)
                print("Expected output:", file=sys.stderr)
                print(test_case.expected_output, file=sys.stderr)
                print("Actual output:", file=sys.stderr)
                print(actual_output.strip(), file=sys.stderr)
                return 1
    finally:
        if original_data is None:
            DATA_FILE_PATH.unlink(missing_ok=True)
        else:
            DATA_FILE_PATH.parent.mkdir(parents=True, exist_ok=True)
            DATA_FILE_PATH.write_bytes(original_data)
        if original_archive_data is None:
            ARCHIVE_FILE_PATH.unlink(missing_ok=True)
        else:
            ARCHIVE_FILE_PATH.parent.mkdir(parents=True, exist_ok=True)
            ARCHIVE_FILE_PATH.write_bytes(original_archive_data)

    write_record(records)
    print(f"PASSED: {len(test_cases)} test case(s)")
    print(f"Record written to {OUTPUT_PATH}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
