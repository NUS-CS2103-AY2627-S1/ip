#!/usr/bin/env python3
"""Run UI tests defined in test/ui-test-plan.md."""

from __future__ import annotations

import re
import shutil
import subprocess
import sys
from dataclasses import dataclass
from pathlib import Path


TEST_DIR = Path(__file__).resolve().parent
PROJECT_ROOT = TEST_DIR.parent
PLAN_FILE = TEST_DIR / "ui-test-plan.md"
DEFAULT_DATA_FILE = PROJECT_ROOT / "data" / "axiom.txt"


@dataclass
class TestCase:
    number: int
    name: str
    aim: str
    inputs: str
    expected_output: str
    initial_file_path: str | None = None
    initial_file_content: str | None = None
    expected_file_path: str | None = None
    expected_file_content: str | None = None


def parse_plan(content: str) -> tuple[list[str], list[TestCase]]:
    run_command_match = re.search(r"\*\*Run command:\*\*\s*`([^`]+)`", content)
    if not run_command_match:
        raise ValueError("ui-test-plan.md must define **Run command:** `...`")

    run_command = run_command_match.group(1).strip()
    run_parts = run_command.split()

    case_pattern = re.compile(
        r"^## Test Case (\d+):\s*(.+?)\s*\n\n"
        r"\*\*Aim:\*\*\s*(.+?)\n\n"
        r"(?:\*\*Initial file:\*\*\s*`([^`]+)`\n```\n(.*?)\n```\n\n)?"
        r"\*\*Inputs:\*\*\n```\n(.*?)\n```\n\n"
        r"\*\*Expected output:\*\*\n```\n(.*?)\n```"
        r"(?:\n\n\*\*Expected file:\*\*\s*`([^`]+)`\n```\n(.*?)\n```)?",
        re.MULTILINE | re.DOTALL,
    )

    cases: list[TestCase] = []
    for match in case_pattern.finditer(content):
        cases.append(
            TestCase(
                number=int(match.group(1)),
                name=match.group(2).strip(),
                aim=match.group(3).strip(),
                initial_file_path=match.group(4),
                initial_file_content=match.group(5),
                inputs=match.group(6),
                expected_output=match.group(7),
                expected_file_path=match.group(8),
                expected_file_content=match.group(9),
            )
        )

    if not cases:
        raise ValueError("No test cases found in ui-test-plan.md")

    return run_parts, cases


def run_program(run_parts: list[str], inputs: str) -> str:
    result = subprocess.run(
        run_parts,
        input=inputs,
        capture_output=True,
        text=True,
        cwd=PROJECT_ROOT,
    )
    if result.returncode != 0:
        stderr = result.stderr.strip()
        raise RuntimeError(
            f"Program exited with code {result.returncode}"
            + (f"\n{stderr}" if stderr else "")
        )
    return result.stdout


def remove_data_directory() -> None:
    data_dir = PROJECT_ROOT / "data"
    if data_dir.exists():
        shutil.rmtree(data_dir)


def write_data_file(path: Path, content: str) -> None:
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_text(content, encoding="utf-8")


def read_data_file(path: Path) -> str:
    if not path.is_file():
        return ""
    return path.read_text(encoding="utf-8")


def print_session_header(case: TestCase) -> None:
    print(f"=== Test Case {case.number}: {case.name} ===")
    print(f"Aim: {case.aim}")
    print()
    if case.initial_file_path is not None:
        print("--- Initial file ---")
        print(case.initial_file_path)
        print(case.initial_file_content, end="" if (case.initial_file_content or "").endswith("\n") else "\n")
        print()
    print("--- Input ---")
    print(case.inputs)
    print()
    print("--- Output ---")


def main() -> int:
    if not PLAN_FILE.is_file():
        print(f"Test plan not found: {PLAN_FILE}", file=sys.stderr)
        return 1

    content = PLAN_FILE.read_text(encoding="utf-8")
    try:
        run_parts, cases = parse_plan(content)
    except ValueError as error:
        print(f"Failed to parse test plan: {error}", file=sys.stderr)
        return 1

    print("UI Test Session")
    print(f"Plan: {PLAN_FILE.relative_to(PROJECT_ROOT)}")
    print(f"Run command: {' '.join(run_parts)}")
    print(f"Test cases: {len(cases)}")
    print()

    passed = 0
    for case in cases:
        print_session_header(case)
        remove_data_directory()
        if case.initial_file_path is not None:
            write_data_file(PROJECT_ROOT / case.initial_file_path, case.initial_file_content or "")
        try:
            actual_output = run_program(run_parts, case.inputs)
        except RuntimeError as error:
            print(error)
            print()
            print("--- Result ---")
            print("FAIL (program error)")
            return 1

        print(actual_output, end="" if actual_output.endswith("\n") else "\n")
        print()
        print("--- Result ---")

        if actual_output != case.expected_output:
            print("FAIL (stdout mismatch)")
            print()
            print("Expected output:")
            print("```")
            print(case.expected_output, end="" if case.expected_output.endswith("\n") else "\n")
            print("```")
            print()
            print("Actual output:")
            print("```")
            print(actual_output, end="" if actual_output.endswith("\n") else "\n")
            print("```")
            print()
            print(f"Test session terminated after Test Case {case.number}: {case.name}")
            return 1

        if case.expected_file_path is not None:
            file_path = PROJECT_ROOT / case.expected_file_path
            actual_file_content = read_data_file(file_path)
            expected_file_content = case.expected_file_content or ""
            print("--- File check ---")
            print(f"File: {case.expected_file_path}")
            if actual_file_content != expected_file_content:
                print("FAIL (file mismatch)")
                print()
                print("Expected file content:")
                print("```")
                print(expected_file_content, end="" if expected_file_content.endswith("\n") else "\n")
                print("```")
                print()
                print("Actual file content:")
                print("```")
                print(actual_file_content, end="" if actual_file_content.endswith("\n") else "\n")
                print("```")
                print()
                print(f"Test session terminated after Test Case {case.number}: {case.name}")
                return 1
            print("File PASS")

        print("PASS")
        passed += 1
        print()

    print(f"All {passed} test case(s) passed.")
    return 0


if __name__ == "__main__":
    sys.exit(main())
