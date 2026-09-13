#!/usr/bin/env python3
"""Run and verify Alfred console UI test cases from the project test plan."""

import re
import subprocess
import sys
import tempfile
from pathlib import Path


PROJECT_ROOT = Path(__file__).resolve().parents[4]
PLAN_PATH = PROJECT_ROOT / "test" / "ui-test-plan.md"
SOURCE_DIRECTORY = PROJECT_ROOT / "src" / "main" / "java"
CASE_PATTERN = re.compile(
    r"^## (?P<name>.+?)\n\n\*\*Aim:\*\* (?P<aim>.+?)\n\n"
    r"(?:### Setup saved data\n```text\n(?P<saved_data>.*?)\n```\n\n)?"
    r"(?:### Setup input\n```text\n(?P<setup>.*?)\n```\n\n)?"
    r"### Input\n```text\n(?P<input>.*?)\n```\n\n"
    r"### Expected output\n```text\n(?P<expected>.*?)\n```",
    re.MULTILINE | re.DOTALL,
)


def load_cases():
    """Return test cases parsed from the test plan, or terminate on an invalid plan."""
    plan = PLAN_PATH.read_text(encoding="utf-8")
    cases = list(CASE_PATTERN.finditer(plan))
    if not cases:
        sys.exit("No valid UI test cases found in test/ui-test-plan.md.")
    return cases


def compile_program(output_directory):
    """Compile all Java source files into the supplied temporary directory."""
    source_files = sorted(SOURCE_DIRECTORY.rglob("*.java"))
    result = subprocess.run(
        ["javac", "-d", str(output_directory), *map(str, source_files)],
        cwd=PROJECT_ROOT,
        text=True,
        capture_output=True,
        check=False,
    )
    if result.returncode != 0:
        sys.exit("Compilation failed:\n" + result.stderr)


def print_session(name, aim, console_input, console_output):
    """Print a readable record of one console test session."""
    print(f"\n=== {name} ===")
    print(f"Aim: {aim}")
    print("Console input:")
    print(console_input)
    print("Console output:")
    print(console_output)


def main():
    """Compile Alfred, run its UI cases, and stop at the first mismatch."""
    cases = load_cases()
    with tempfile.TemporaryDirectory(prefix="alfred-ui-") as temporary_directory:
        compile_program(Path(temporary_directory))
        for case_number, case in enumerate(cases):
            name = case.group("name")
            aim = case.group("aim")
            console_input = case.group("input") + "\n"
            expected = case.group("expected") + "\n"
            case_directory = Path(temporary_directory) / f"case-data-{case_number}"
            case_directory.mkdir()
            saved_data = case.group("saved_data")
            if saved_data is not None:
                saved_data_path = case_directory / "data" / "alfred.txt"
                saved_data_path.parent.mkdir()
                saved_data_path.write_text(saved_data, encoding="utf-8")
            setup = case.group("setup")
            if setup is not None:
                setup_input = setup + "\n"
                setup_result = subprocess.run(
                    ["java", "-cp", temporary_directory, "alfred.Alfred"],
                    cwd=case_directory,
                    input=setup_input,
                    text=True,
                    capture_output=True,
                    check=False,
                )
                print_session(name + " setup", aim, setup_input.rstrip(), setup_result.stdout.rstrip())
                if setup_result.returncode != 0:
                    print("FAIL: setup session did not exit successfully.")
                    sys.exit(1)
            result = subprocess.run(
                ["java", "-cp", temporary_directory, "alfred.Alfred"],
                cwd=case_directory,
                input=console_input,
                text=True,
                capture_output=True,
                check=False,
            )
            actual = result.stdout
            print_session(name, aim, console_input.rstrip(), actual.rstrip())
            if result.returncode != 0 or actual != expected:
                print("FAIL: output did not match the expected output.")
                print("Expected output:")
                print(expected.rstrip())
                print("Actual output:")
                print(actual.rstrip())
                sys.exit(1)
            print("PASS")

    print("\nAll UI test cases passed.")


if __name__ == "__main__":
    main()
