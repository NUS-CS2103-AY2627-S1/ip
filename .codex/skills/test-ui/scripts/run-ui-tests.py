#!/usr/bin/env python3
"""Run the console UI cases recorded in test/ui-test-plan.md."""

from __future__ import annotations

import argparse
import difflib
import re
import subprocess
import sys
import tempfile
from dataclasses import dataclass
from pathlib import Path


CASE_PATTERN = re.compile(
    r"^## Test case: (?P<name>[^\n]+)\n\n"
    r"\*\*Aim:\*\* (?P<aim>[^\n]+)\n\n"
    r"### Input\n\n```text\n(?P<input>.*?)\n```\n\n"
    r"### Expected output\n\n```text\n(?P<expected>.*?)\n```",
    re.MULTILINE | re.DOTALL,
)


@dataclass(frozen=True)
class TestCase:
    name: str
    aim: str
    console_input: str
    expected_output: str


def parse_plan(plan_path: Path) -> list[TestCase]:
    """Parse test cases from the project's Markdown test plan."""
    plan = plan_path.read_text(encoding="utf-8")
    cases = [
        TestCase(
            match.group("name"),
            match.group("aim"),
            match.group("input") + "\n",
            match.group("expected") + "\n",
        )
        for match in CASE_PATTERN.finditer(plan)
    ]
    if not cases:
        raise ValueError(f"No test cases found in {plan_path}")
    return cases


def compile_program(repo: Path, classes_dir: Path) -> None:
    """Compile all Java source files into the temporary classes directory."""
    sources = sorted((repo / "src/main/java").rglob("*.java"))
    if not sources:
        raise ValueError("No Java source files found in src/main/java")
    classes_dir.mkdir(parents=True, exist_ok=True)
    result = subprocess.run(
        ["javac", "-d", str(classes_dir), *map(str, sources)],
        cwd=repo,
        capture_output=True,
        text=True,
    )
    if result.returncode != 0:
        raise RuntimeError(result.stderr or result.stdout or "Compilation failed")


def format_record(case: TestCase, actual_output: str, result: str) -> str:
    """Create a readable record of one console test session."""
    return (
        f"=== {case.name}: {result} ===\n"
        f"Aim: {case.aim}\n"
        "--- Console input ---\n"
        f"{case.console_input}"
        "--- Console output ---\n"
        f"{actual_output}"
    )


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--repo", type=Path, default=Path.cwd())
    parser.add_argument("--plan", type=Path, default=Path("test/ui-test-plan.md"))
    parser.add_argument("--main-class", default="beemo.Beemo")
    args = parser.parse_args()

    repo = args.repo.resolve()
    plan_path = args.plan if args.plan.is_absolute() else repo / args.plan
    classes_dir = repo / "_temp/ui-test-classes"
    log_path = repo / "_temp/ui-test-session.log"
    log_path.parent.mkdir(parents=True, exist_ok=True)
    test_runs_dir = repo / "_temp/ui-test-runs"
    test_runs_dir.mkdir(parents=True, exist_ok=True)

    try:
        cases = parse_plan(plan_path)
        compile_program(repo, classes_dir)
    except (OSError, ValueError, RuntimeError) as error:
        print(f"UI test setup failed: {error}", file=sys.stderr)
        return 1

    records: list[str] = []
    for case in cases:
        with tempfile.TemporaryDirectory(dir=test_runs_dir) as working_directory:
            result = subprocess.run(
                ["java", "-cp", str(classes_dir), args.main_class],
                cwd=working_directory,
                input=case.console_input,
                capture_output=True,
                text=True,
            )
        actual = result.stdout
        passed = result.returncode == 0 and actual == case.expected_output
        record = format_record(case, actual, "PASS" if passed else "FAIL")
        records.append(record)
        print(record)

        if not passed:
            print("--- Expected output ---")
            print(case.expected_output, end="")
            print("--- Actual output ---")
            print(actual, end="")
            print("--- Difference ---")
            print(
                "".join(
                    difflib.unified_diff(
                        case.expected_output.splitlines(keepends=True),
                        actual.splitlines(keepends=True),
                        fromfile="expected",
                        tofile="actual",
                    )
                ),
                end="",
            )
            if result.stderr:
                print("--- Standard error ---")
                print(result.stderr, end="")
            log_path.write_text("\n".join(records), encoding="utf-8")
            print(f"Stopped after first failure. Session log: {log_path}")
            return 1

    log_path.write_text("\n".join(records), encoding="utf-8")
    print(f"All {len(cases)} UI test case(s) passed. Session log: {log_path}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
