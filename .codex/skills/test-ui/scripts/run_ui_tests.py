#!/usr/bin/env python3
"""Compile Chocolate and run the console tests in test/ui-test-plan.md."""

from __future__ import annotations

import re
import subprocess
import sys
import tempfile
from pathlib import Path


CASE_PATTERN = re.compile(r"^## Test: (?P<name>.+?)\n(?P<body>.*?)(?=^## Test:|\Z)",
                          re.MULTILINE | re.DOTALL)
INPUT_PATTERN = re.compile(r"### Input\n```text\n(.*?)\n```", re.DOTALL)
FIRST_INPUT_PATTERN = re.compile(r"### First-session input\n```text\n(.*?)\n```", re.DOTALL)
SECOND_INPUT_PATTERN = re.compile(r"### Second-session input\n```text\n(.*?)\n```", re.DOTALL)
EXPECTED_PATTERN = re.compile(r"### Expected output\n```text\n(.*?)\n```", re.DOTALL)
SECOND_EXPECTED_PATTERN = re.compile(r"### Expected second-session output\n```text\n(.*?)\n```", re.DOTALL)


def normalise(text: str) -> str:
    """Make platform line endings comparable without hiding output differences."""
    return text.replace("\r\n", "\n").rstrip("\n")


def load_cases(plan_path: Path) -> list[dict[str, object]]:
    """Read the Markdown test-plan format into runnable test cases."""
    plan = plan_path.read_text(encoding="utf-8")
    cases = []
    for match in CASE_PATTERN.finditer(plan):
        body = match.group("body")
        aim_match = re.search(r"^Aim: (.+)$", body, re.MULTILINE)
        first_input_match = FIRST_INPUT_PATTERN.search(body)
        if first_input_match:
            second_input_match = SECOND_INPUT_PATTERN.search(body)
            expected_match = SECOND_EXPECTED_PATTERN.search(body)
            if not second_input_match or not expected_match:
                raise ValueError(f"Incomplete two-session test: {match.group('name')}")
            inputs = [first_input_match.group(1), second_input_match.group(1)]
        else:
            input_match = INPUT_PATTERN.search(body)
            expected_match = EXPECTED_PATTERN.search(body)
            if not input_match or not expected_match:
                raise ValueError(f"Incomplete test: {match.group('name')}")
            inputs = [input_match.group(1)]

        if not aim_match:
            raise ValueError(f"Missing aim: {match.group('name')}")
        cases.append({
            "name": match.group("name"),
            "aim": aim_match.group(1),
            "inputs": inputs,
            "expected": expected_match.group(1),
        })
    if not cases:
        raise ValueError("No test cases found. Follow the format in test/ui-test-plan.md.")
    return cases


def compile_program(repo: Path, output_dir: Path) -> None:
    """Compile Chocolate's non-JavaFX console application into a temporary directory."""
    source_root = repo / "src/main/java"
    source_files = sorted(
        source_file for source_file in source_root.rglob("*.java")
        if "chocolate/gui" not in source_file.as_posix()
        and source_file.name != "Launcher.java"
    )
    if not source_files:
        raise FileNotFoundError("No Java source files found in src/main/java.")
    result = subprocess.run(
        ["javac", "--release", "25", "-d", str(output_dir), *map(str, source_files)],
        capture_output=True,
        text=True,
        cwd=repo,
    )
    if result.returncode != 0:
        raise RuntimeError("Compilation failed:\n" + result.stderr)


def find_main_class(repo: Path) -> str:
    """Return the console entry point, which remains available alongside the GUI."""
    return "chocolate.Chocolate"


def run_session(output_dir: Path, session_input: str, working_dir: Path, main_class: str) -> str:
    """Run one new Chocolate process and return its standard output."""
    result = subprocess.run(
        ["java", "-cp", str(output_dir), main_class],
        input=session_input + "\n",
        capture_output=True,
        text=True,
        cwd=working_dir,
    )
    if result.returncode != 0:
        raise RuntimeError("Chocolate stopped with an error:\n" + result.stderr)
    return result.stdout


def print_transcript(case: dict[str, object], actuals: list[str]) -> None:
    """Display the input and output from a completed console test."""
    print(f"\nTest: {case['name']}")
    print(f"Aim: {case['aim']}")
    for number, (case_input, actual) in enumerate(zip(case["inputs"], actuals), start=1):
        label = "" if len(actuals) == 1 else f" (session {number})"
        print(f"Console input{label}:")
        print(case_input)
        print(f"Console output{label}:")
        print(actual, end="" if actual.endswith("\n") else "\n")


def main() -> int:
    repo = Path(__file__).resolve().parents[4]
    plan_path = repo / "test/ui-test-plan.md"

    try:
        cases = load_cases(plan_path)
        with tempfile.TemporaryDirectory(prefix="chocolate-ui-test-") as temporary_path:
            output_dir = Path(temporary_path)
            compile_program(repo, output_dir)
            main_class = find_main_class(repo)

            for number, case in enumerate(cases, start=1):
                case_directory = output_dir / f"case-{number}"
                case_directory.mkdir()
                actuals = [run_session(output_dir, case_input, case_directory, main_class)
                           for case_input in case["inputs"]]
                actual = actuals[-1]
                print_transcript(case, actuals)
                if normalise(actual) != normalise(case["expected"]):
                    print("FAIL: Actual output did not match expected output.")
                    print("Expected output:")
                    print(case["expected"])
                    print("Actual output:")
                    print(actual, end="" if actual.endswith("\n") else "\n")
                    return 1

        print(f"\nPASS: {len(cases)} UI test case(s) passed.")
        return 0
    except (FileNotFoundError, RuntimeError, ValueError) as error:
        print(f"ERROR: {error}", file=sys.stderr)
        return 2


if __name__ == "__main__":
    raise SystemExit(main())
