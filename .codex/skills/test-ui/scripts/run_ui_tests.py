#!/usr/bin/env python3
"""Run exact-output console UI tests described in a Markdown test plan."""

from __future__ import annotations

import argparse
import os
import re
import shlex
import subprocess
import sys
import tempfile
from dataclasses import dataclass
from pathlib import Path


CASE_PATTERN = re.compile(r"^## Test case[^\n]*\n(.*?)(?=^## Test case|\Z)", re.MULTILINE | re.DOTALL)


@dataclass
class TestCase:
    """One console test case parsed from the Markdown plan."""

    title: str
    aim: str
    inputs: str
    expected_output: str
    initial_data: str | None


def normalize_output(output: str) -> str:
    """Normalize platform line endings and one final newline for comparison."""
    return output.replace("\r\n", "\n").replace("\r", "\n").rstrip("\n")


def extract_fenced_block(section: str, label: str) -> str:
    """Extract the text fence immediately following a Markdown label."""
    pattern = rf"^{re.escape(label)}\s*\n```[^\n]*\n(.*?)^```\s*$"
    match = re.search(pattern, section, re.MULTILINE | re.DOTALL)
    if match is None:
        raise ValueError(f"Missing fenced block after '{label}'")
    return match.group(1).rstrip("\n")


def extract_optional_fenced_block(section: str, label: str) -> str | None:
    """Extract an optional text fence, returning None when it is absent."""
    pattern = rf"^{re.escape(label)}\s*\n```[^\n]*\n(.*?)^```\s*$"
    match = re.search(pattern, section, re.MULTILINE | re.DOTALL)
    return match.group(1).rstrip("\n") if match is not None else None


def parse_plan(plan_path: Path) -> tuple[str, list[TestCase]]:
    """Parse the run command and test cases from the Markdown plan."""
    plan = plan_path.read_text(encoding="utf-8")
    run_match = re.search(r"^Run command:\s*`([^`]+)`\s*$", plan, re.MULTILINE)
    run_command = run_match.group(1) if run_match else "java -cp {classes} Duchess"

    cases: list[TestCase] = []
    for section_match in CASE_PATTERN.finditer(plan):
        heading = plan[section_match.start() : plan.find("\n", section_match.start())]
        section = section_match.group(1)
        aim_match = re.search(r"^Aim:\s*(.+)$", section, re.MULTILINE)
        if aim_match is None:
            raise ValueError(f"Missing Aim in {heading}")
        cases.append(
            TestCase(
                title=heading[3:] if heading.startswith("## ") else heading,
                aim=aim_match.group(1).strip(),
                inputs=extract_fenced_block(section, "Inputs:"),
                expected_output=extract_fenced_block(section, "Expected output:"),
                initial_data=extract_optional_fenced_block(section, "Initial data file:"),
            )
        )

    if not cases:
        raise ValueError(f"No test cases found in {plan_path}")
    return run_command, cases


def java_executable(name: str) -> str:
    """Prefer an explicitly configured Java home while allowing command overrides."""
    override = os.environ.get(name.upper())
    if override:
        return override
    java_home = os.environ.get("JAVA_HOME")
    binary = "javac" if name == "javac" else "java"
    if java_home:
        candidate = Path(java_home) / "bin" / binary
        if candidate.exists():
            return str(candidate)
    return binary


def compile_sources(classes_dir: Path) -> None:
    """Compile all project Java sources into the supplied temporary directory."""
    sources = sorted(Path("src/main/java").rglob("*.java"))
    if not sources:
        raise RuntimeError("No Java sources found in src/main/java")
    command = [java_executable("javac"), "-d", str(classes_dir), *(str(source) for source in sources)]
    result = subprocess.run(command, text=True, capture_output=True)
    if result.returncode != 0:
        raise RuntimeError(
            "Compilation failed.\n"
            f"Command: {' '.join(command)}\n"
            f"stdout:\n{result.stdout}\n"
            f"stderr:\n{result.stderr}"
        )


def display_session(test_case: TestCase, actual_output: str) -> None:
    """Print the console input and output for one test case."""
    print(f"\n=== {test_case.title} ===")
    print(f"Aim: {test_case.aim}")
    print("--- Console input ---")
    print(test_case.inputs)
    print("--- Console output ---")
    print(actual_output)


def main() -> int:
    """Run all cases, stopping immediately after the first failure."""
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("plan", nargs="?", default="test/ui-test-plan.md")
    args = parser.parse_args()
    plan_path = Path(args.plan)

    try:
        run_command, cases = parse_plan(plan_path)
        with tempfile.TemporaryDirectory(prefix="duchess-ui-") as temporary_dir:
            classes_dir = Path(temporary_dir) / "classes"
            classes_dir.mkdir()
            compile_sources(classes_dir)

            command_parts = [
                part.replace("{classes}", str(classes_dir))
                for part in shlex.split(run_command)
            ]
            if command_parts and command_parts[0] == "java":
                command_parts[0] = java_executable("java")

            for test_case in cases:
                with tempfile.TemporaryDirectory(prefix="duchess-ui-case-") as case_directory:
                    if test_case.initial_data is not None:
                        data_directory = Path(case_directory) / "data"
                        data_directory.mkdir()
                        (data_directory / "duchess.txt").write_text(
                            test_case.initial_data + "\n", encoding="utf-8"
                        )
                    try:
                        result = subprocess.run(
                            command_parts,
                            input=test_case.inputs + "\n",
                            text=True,
                            capture_output=True,
                            timeout=30,
                            cwd=case_directory,
                        )
                    except subprocess.TimeoutExpired as exception:
                        actual_output = exception.stdout or ""
                        display_session(test_case, actual_output)
                        print("TEST FAILED: the program timed out after 30 seconds.")
                        print("Expected output:")
                        print(test_case.expected_output)
                        return 1

                actual_output = result.stdout
                if result.stderr:
                    actual_output += f"\n[stderr]\n{result.stderr}"
                display_session(test_case, actual_output)

                if result.returncode != 0 or normalize_output(actual_output) != normalize_output(
                    test_case.expected_output
                ):
                    print("TEST FAILED: actual and expected outputs differ.")
                    print("--- Expected output ---")
                    print(test_case.expected_output)
                    print("--- Actual output ---")
                    print(actual_output)
                    return 1

                print("TEST PASSED")
    except (OSError, ValueError, RuntimeError) as exception:
        print(f"TEST SESSION COULD NOT START: {exception}", file=sys.stderr)
        return 1

    print(f"\nAll {len(cases)} UI test case(s) passed.")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
