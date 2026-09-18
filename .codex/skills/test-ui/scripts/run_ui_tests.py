#!/usr/bin/env python3
"""Compile HABI and check the console sessions recorded in a Markdown plan."""

import argparse
import re
import shutil
import subprocess
import sys
import tempfile
from pathlib import Path


TEST_CASE_PATTERN = re.compile(
    r"^## Test case: (?P<name>.+?)\n\n"
    r"\*\*Aim:\*\* (?P<aim>.+?)\n\n"
    r"\*\*Input\*\*\n```text\n(?P<input>.*?)\n```\n\n"
    r"\*\*Expected output\*\*\n```text\n(?P<expected>.*?)\n```",
    re.MULTILINE | re.DOTALL,
)


def normalise(text):
    """Use Unix line endings for deterministic comparisons."""
    return text.replace("\r\n", "\n").replace("\r", "\n")


def java_major_version():
    """Return the installed Java major version, or None when Java is unavailable."""
    java_path = shutil.which("java")
    if java_path is None:
        return None
    result = subprocess.run([java_path, "-version"], capture_output=True, text=True, check=False)
    version_match = re.search(r'version "(\d+)', result.stderr + result.stdout)
    return int(version_match.group(1)) if version_match else None


def read_test_cases(plan_path):
    """Read test cases from the required Markdown format."""
    plan = normalise(plan_path.read_text(encoding="utf-8"))
    test_cases = list(TEST_CASE_PATTERN.finditer(plan))
    if not test_cases:
        raise ValueError("No test cases matched the required Markdown format.")
    return test_cases


def print_block(title, content):
    """Print a labelled block while preserving the session text."""
    print(f"--- {title} ---")
    print(content, end="" if content.endswith("\n") else "\n")


def main():
    """Run every planned UI case until one fails."""
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--plan", default="test/ui-test-plan.md", type=Path)
    parser.add_argument("--allow-non-25", action="store_true",
                        help="run with a non-Java-25 runtime when Java 25 is unavailable")
    args = parser.parse_args()

    major_version = java_major_version()
    if major_version is None:
        print("ERROR: Java is not available on PATH.", file=sys.stderr)
        return 2
    if major_version != 25 and not args.allow_non_25:
        print(f"ERROR: Java 25 is required; found Java {major_version}.", file=sys.stderr)
        return 2
    if major_version != 25:
        print(f"WARNING: Running with Java {major_version}; Java 25 was not available.")

    try:
        test_cases = read_test_cases(args.plan)
    except (OSError, ValueError) as error:
        print(f"ERROR: {error}", file=sys.stderr)
        return 2

    source_files = sorted(Path("src/main/java").rglob("*.java"))
    if not source_files:
        print("ERROR: No Java source files found in src/main/java.", file=sys.stderr)
        return 2

    with tempfile.TemporaryDirectory(prefix="habi-ui-test-") as class_directory:
        absolute_class_directory = str(Path(class_directory).resolve())
        compilation = subprocess.run(
            ["javac", "-d", class_directory, *map(str, source_files)],
            capture_output=True, text=True, check=False,
        )
        if compilation.returncode != 0:
            print("ERROR: Compilation failed.", file=sys.stderr)
            print(compilation.stderr, file=sys.stderr, end="")
            return compilation.returncode

        for number, test_case in enumerate(test_cases, start=1):
            input_text = test_case.group("input") + "\n"
            expected_output = test_case.group("expected") + "\n"
            with tempfile.TemporaryDirectory(prefix="habi-ui-case-") as case_directory:
                execution = subprocess.run(
                    ["java", "-cp", absolute_class_directory, "habi.Habi"], input=input_text,
                    capture_output=True, text=True, check=False, cwd=case_directory,
                )
            actual_output = normalise(execution.stdout)

            print(f"\n=== Test {number}: {test_case.group('name')} ===")
            print(f"Aim: {test_case.group('aim')}")
            print_block("Console input", input_text)
            print_block("Console output", actual_output)

            if execution.returncode != 0 or actual_output != expected_output:
                print("RESULT: FAIL")
                if execution.returncode != 0:
                    print(f"Program exit code: {execution.returncode}")
                    print_block("Standard error", normalise(execution.stderr))
                print_block("Expected output", expected_output)
                print_block("Actual output", actual_output)
                return 1
            print("RESULT: PASS")

    print("\nAll UI test cases passed.")
    return 0


if __name__ == "__main__":
    sys.exit(main())
