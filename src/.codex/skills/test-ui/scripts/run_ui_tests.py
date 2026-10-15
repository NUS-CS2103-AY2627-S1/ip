#!/usr/bin/env python3
"""Compile Rocky and run the console UI cases recorded in a Markdown test plan."""

from __future__ import annotations

import argparse
from dataclasses import dataclass
from pathlib import Path
import re
import subprocess
import sys
import tempfile


CASE_PATTERN = re.compile(
    r"^## (?P<name>.+?)\n"
    r"\*\*Aim:\*\* (?P<aim>.+?)\n\n"
    r"\*\*Input:\*\*\n```(?:text)?\n(?P<input>.*?)\n```\n\n"
    r"\*\*Expected output:\*\*\n```(?:text)?\n(?P<expected>.*?)\n```",
    re.MULTILINE | re.DOTALL,
)


@dataclass
class UiTestCase:
    """A console test case loaded from the Markdown test plan."""

    name: str
    aim: str
    console_input: str
    expected_output: str


def normalize_output(text: str) -> str:
    """Make the final newline consistent for reliable console output comparison."""
    return text.rstrip("\n") + "\n"


def load_test_cases(plan_path: Path) -> list[UiTestCase]:
    """Load UI test cases from the required Markdown structure."""
    plan_text = plan_path.read_text(encoding="utf-8")
    cases = [
        UiTestCase(
            name=match.group("name").strip(),
            aim=match.group("aim").strip(),
            console_input=normalize_output(match.group("input")),
            expected_output=normalize_output(match.group("expected")),
        )
        for match in CASE_PATTERN.finditer(plan_text)
    ]
    if not cases:
        raise ValueError(
            "No test cases found. Follow the case structure documented in "
            "the test-ui skill."
        )
    return cases


def compile_program(source_dir: Path, build_dir: Path) -> None:
    """Compile all Java files from the supplied source directory."""
    source_files = sorted(source_dir.rglob("*.java"))
    if not source_files:
        raise ValueError(f"No Java files found in {source_dir}")

    result = subprocess.run(
        ["javac", "-d", str(build_dir), *(str(path) for path in source_files)],
        capture_output=True,
        text=True,
    )
    if result.returncode != 0:
        raise RuntimeError(result.stderr.strip() or "Compilation failed.")


def print_session(case: UiTestCase, actual_output: str) -> None:
    """Print the captured input and output for one executed test case."""
    print(f"\n=== {case.name} ===")
    print(f"Aim: {case.aim}")
    print("Console input:")
    print(case.console_input, end="")
    print("Console output:")
    print(actual_output, end="" if actual_output.endswith("\n") else "\n")


def run_test_cases(cases: list[UiTestCase], build_dir: Path, main_class: str,
                   timeout_seconds: float) -> int:
    """Run cases in order, stopping at the first failed output comparison."""
    for case in cases:
        try:
            result = subprocess.run(
                ["java", "-cp", str(build_dir), main_class],
                input=case.console_input,
                capture_output=True,
                text=True,
                timeout=timeout_seconds,
            )
        except subprocess.TimeoutExpired:
            print(f"\n=== {case.name} ===")
            print(f"Aim: {case.aim}")
            print("FAILED: the program did not finish before the timeout.")
            return 1

        actual_output = normalize_output(result.stdout)
        if result.returncode != 0:
            actual_output += f"[Program error]\n{result.stderr}"

        print_session(case, actual_output)
        if result.returncode != 0 or actual_output != case.expected_output:
            print("FAILED: actual output did not match expected output.")
            print("Expected output:")
            print(case.expected_output, end="")
            print("Actual output:")
            print(actual_output, end="" if actual_output.endswith("\n") else "\n")
            return 1

        print("PASSED")

    print("\nAll UI tests passed.")
    return 0


def main() -> int:
    """Parse command-line options and run the configured UI test plan."""
    parser = argparse.ArgumentParser(
        description="Compile a Java console program and run Markdown-defined UI tests."
    )
    parser.add_argument("--plan", type=Path, default=Path("test/ui-test-plan.md"),
                        help="Markdown file containing UI test cases")
    parser.add_argument("--source-dir", type=Path, default=Path("main/java"),
                        help="directory containing Java source files")
    parser.add_argument("--main-class", default="Rocky", help="main class to execute")
    parser.add_argument("--timeout", type=float, default=5.0,
                        help="maximum seconds per test case")
    args = parser.parse_args()

    try:
        cases = load_test_cases(args.plan)
        with tempfile.TemporaryDirectory(prefix="rocky-ui-tests-") as temporary_directory:
            build_dir = Path(temporary_directory)
            compile_program(args.source_dir, build_dir)
            return run_test_cases(cases, build_dir, args.main_class, args.timeout)
    except (OSError, RuntimeError, ValueError) as error:
        print(f"Unable to run UI tests: {error}", file=sys.stderr)
        return 1


if __name__ == "__main__":
    raise SystemExit(main())
