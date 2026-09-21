#!/usr/bin/env python3
"""Compile the chatbot and run exact console UI tests from a Markdown plan."""

from __future__ import annotations

import argparse
import re
import subprocess
import sys
import tempfile
from dataclasses import dataclass
from pathlib import Path


CASE_PATTERN = re.compile(
    r"^## (?P<identifier>[^\n]+)\n\n"
    r"\*\*Aim:\*\* (?P<aim>[^\n]+)\n\n"
    r"### Input\n\n```text\n(?P<input_text>.*?)```\n\n"
    r"### Expected output\n\n```text\n(?P<expected_output>.*?)```",
    re.MULTILINE | re.DOTALL,
)


@dataclass(frozen=True)
class TestCase:
    """One console test containing its purpose, input, and expected output."""

    identifier: str
    aim: str
    input_text: str
    expected_output: str


def parse_cases(plan_path: Path) -> list[TestCase]:
    """Read all test cases from the Markdown test plan."""
    plan = plan_path.read_text(encoding="utf-8")
    cases = [TestCase(**match.groupdict()) for match in CASE_PATTERN.finditer(plan)]
    if not cases:
        raise ValueError(f"No test cases found in {plan_path}")
    return cases


def compile_project(repo: Path, output_dir: Path) -> None:
    """Compile every Java source file and fail with compiler output on error."""
    sources = sorted((repo / "src/main/java").glob("*.java"))
    if not sources:
        raise ValueError("No Java source files found in src/main/java")

    result = subprocess.run(
        ["javac", "-Xlint:all", "-Werror", "-d", str(output_dir), *map(str, sources)],
        capture_output=True,
        text=True,
    )
    if result.returncode != 0:
        print("Compilation failed:", file=sys.stderr)
        print(result.stdout, end="", file=sys.stderr)
        print(result.stderr, end="", file=sys.stderr)
        raise SystemExit(1)


def run_case(output_dir: Path, case: TestCase) -> None:
    """Run one test and stop immediately if its output differs."""
    result = subprocess.run(
        ["java", "-cp", str(output_dir), "Chris"],
        input=case.input_text,
        capture_output=True,
        text=True,
        timeout=10,
    )

    print(f"\n=== {case.identifier}: {case.aim} ===")
    print("INPUT:")
    print(case.input_text, end="")
    print("OUTPUT:")
    print(result.stdout, end="")

    if result.returncode != 0 or result.stdout != case.expected_output:
        print("TEST FAILED", file=sys.stderr)
        print("EXPECTED:", file=sys.stderr)
        print(case.expected_output, end="", file=sys.stderr)
        print("ACTUAL:", file=sys.stderr)
        print(result.stdout, end="", file=sys.stderr)
        if result.stderr:
            print("STDERR:", file=sys.stderr)
            print(result.stderr, end="", file=sys.stderr)
        raise SystemExit(1)

    print("RESULT: PASS")


def main() -> None:
    """Parse arguments, compile the project, and execute the test plan."""
    parser = argparse.ArgumentParser()
    parser.add_argument("repo", type=Path)
    parser.add_argument("plan", type=Path)
    args = parser.parse_args()

    repo = args.repo.resolve()
    plan = args.plan if args.plan.is_absolute() else repo / args.plan
    cases = parse_cases(plan)

    with tempfile.TemporaryDirectory(prefix="chris-ui-test-") as temp_dir:
        output_dir = Path(temp_dir)
        compile_project(repo, output_dir)
        for case in cases:
            run_case(output_dir, case)

    print(f"\nAll {len(cases)} UI test cases passed.")


if __name__ == "__main__":
    main()
