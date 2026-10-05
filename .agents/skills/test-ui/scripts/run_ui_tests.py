#!/usr/bin/env python3
"""Run the project's scripted console UI tests."""

from __future__ import annotations

import re
import shutil
import subprocess
import sys
import tempfile
from dataclasses import dataclass
from pathlib import Path


@dataclass
class TestCase:
    name: str
    aim: str
    input_text: str
    expected_lines: list[str]


def find_project_root() -> Path:
    for candidate in Path(__file__).resolve().parents:
        if (candidate / "src" / "main" / "java").is_dir():
            return candidate
    raise RuntimeError("Could not locate the project root")


ROOT = find_project_root()
PLAN = ROOT / "test" / "ui-test-plan.md"


def parse_plan(text: str) -> list[TestCase]:
    sections = re.split(r"(?m)^##\s+", text)[1:]
    cases: list[TestCase] = []

    for section in sections:
        lines = section.splitlines()
        name = lines[0].strip()
        aim_match = re.search(r"(?m)^Aim:\s*(.+)$", section)
        input_match = re.search(r"(?ms)^Input:\s*```(?:text)?\s*\n(.*?)```", section)
        expected_match = re.search(
            r"(?ms)^Expected output:\s*```(?:text)?\s*\n(.*?)```", section
        )

        if not aim_match or not input_match or not expected_match:
            raise ValueError(
                f"Test section '{name}' must contain Aim, Input, and Expected output blocks"
            )

        expected_lines = [
            line.strip()
            for line in expected_match.group(1).splitlines()
            if line.strip()
        ]
        if not expected_lines:
            raise ValueError(f"Test section '{name}' has no expected output lines")

        cases.append(
            TestCase(
                name=name,
                aim=aim_match.group(1).strip(),
                input_text=input_match.group(1).strip("\n") + "\n",
                expected_lines=expected_lines,
            )
        )

    if not cases:
        raise ValueError(f"No test cases found in {PLAN}")
    return cases


def version_number(command: str) -> str:
    result = subprocess.run([command, "-version"], capture_output=True, text=True)
    output = result.stdout + result.stderr
    match = re.search(r'version\s+"(\d+)', output)
    if match:
        return match.group(1)
    match = re.search(r"(?:javac|java)\s+(\d+)", output)
    return match.group(1) if match else "unknown"


def print_transcript(case: TestCase, output: str) -> None:
    print(f"\n=== {case.name} ===")
    print(f"Aim: {case.aim}")
    print("--- Console input ---")
    print(case.input_text, end="")
    print("--- Console output ---")
    print(output, end="" if output.endswith("\n") else "\n")


def check_expected(output: str, expected_lines: list[str]) -> str | None:
    actual_lines = [line.strip() for line in output.splitlines()]
    cursor = 0
    for expected in expected_lines:
        try:
            cursor = actual_lines.index(expected, cursor) + 1
        except ValueError:
            return expected
    return None


def main() -> int:
    try:
        cases = parse_plan(PLAN.read_text())
    except (OSError, ValueError) as error:
        print(f"ERROR: {error}", file=sys.stderr)
        return 1

    java_version = version_number("java")
    javac_version = version_number("javac")
    if java_version != "25" or javac_version != "25":
        print(
            "ERROR: Java 25 is required. "
            f"Found java {java_version} and javac {javac_version}. "
            "Run: sdk use java 25.0.3.fx-zulu",
            file=sys.stderr,
        )
        return 1

    # Packages place source files in subdirectories below the source root.
    sources = sorted((ROOT / "src" / "main" / "java").rglob("*.java"))
    if not sources:
        print("ERROR: No Java source files found", file=sys.stderr)
        return 1

    with tempfile.TemporaryDirectory(prefix="test-ui-") as temp_dir:
        classes = Path(temp_dir)
        compile_result = subprocess.run(
            ["javac", "-d", str(classes), *(str(source) for source in sources)],
            cwd=ROOT,
            capture_output=True,
            text=True,
        )
        if compile_result.returncode != 0:
            print("COMPILATION FAILED")
            print(compile_result.stdout, end="")
            print(compile_result.stderr, end="", file=sys.stderr)
            return 1

        for case in cases:
            # Each case starts with no data folder at all, so cases stay
            # independent and every case also exercises the first-run state.
            data_dir = ROOT / "data"
            if data_dir.exists():
                shutil.rmtree(data_dir)
            try:
                run_result = subprocess.run(
                    ["java", "-cp", str(classes), "gatsby.Gatsby"],
                    cwd=ROOT,
                    input=case.input_text,
                    capture_output=True,
                    text=True,
                    timeout=10,
                )
                output = run_result.stdout + run_result.stderr
            except subprocess.TimeoutExpired as error:
                output = (error.stdout or "") + (error.stderr or "")
                print_transcript(case, output)
                print(f"FAIL: test timed out after 10 seconds: {case.name}")
                return 1

            print_transcript(case, output)

            if run_result.returncode != 0:
                print(f"FAIL: process exited with status {run_result.returncode}")
                print("Expected output lines:")
                for line in case.expected_lines:
                    print(f"  {line}")
                return 1

            missing = check_expected(output, case.expected_lines)
            if missing is not None:
                print(f"FAIL: expected output line not found in order: {missing}")
                print("Expected output lines:")
                for line in case.expected_lines:
                    print(f"  {line}")
                print("Actual output was shown above.")
                return 1

            print(f"PASS: {case.name}")

    print(f"\nAll {len(cases)} UI test(s) passed.")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
