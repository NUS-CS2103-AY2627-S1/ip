#!/usr/bin/env python3
"""Run exact, fail-fast console UI tests defined in a Markdown test plan."""

from __future__ import annotations

import argparse
import re
import subprocess
import sys
import tempfile
from dataclasses import dataclass
from pathlib import Path


CONFIG_PATTERN = re.compile(
    r"^## Configuration\s*$.*?"
    r"^- Source directory:\s*`([^`]+)`\s*$.*?"
    r"^- Classes directory:\s*`([^`]+)`\s*$.*?"
    r"^- Main class:\s*`([^`]+)`\s*$",
    re.MULTILINE | re.DOTALL,
)
CASE_PATTERN = re.compile(
    r"^## Test case:\s*(.+?)\s*$\s*"
    r"^### Aim\s*$\s*(.+?)\s*"
    r"^### Commands\s*$\s*```text[ \t]*\r?\n(.*?)^```\s*$\s*"
    r"^### Expected output\s*$\s*```text[ \t]*\r?\n(.*?)^```\s*$",
    re.MULTILINE | re.DOTALL,
)


@dataclass(frozen=True)
class TestCase:
    """One independent application session and its expected stdout."""

    name: str
    aim: str
    commands: str
    expected: str


def normalized_output(text: str) -> str:
    """Normalize platform line endings while retaining all other output."""
    return text.replace("\r\n", "\n").replace("\r", "\n")


def parse_plan(plan_path: Path) -> tuple[Path, Path, str, list[TestCase]]:
    """Read configuration and ordered test cases from the Markdown plan."""
    text = plan_path.read_text(encoding="utf-8")
    config = CONFIG_PATTERN.search(text)
    if config is None:
        raise ValueError("missing or invalid Configuration section")

    cases = [
        TestCase(
            name=match.group(1).strip(),
            aim=match.group(2).strip(),
            commands=match.group(3),
            expected=match.group(4),
        )
        for match in CASE_PATTERN.finditer(text)
    ]
    if not cases:
        raise ValueError("the plan contains no valid test cases")
    if len({case.name for case in cases}) != len(cases):
        raise ValueError("test case names must be unique")

    repository = plan_path.parent.parent.resolve()
    return repository / config.group(1), repository / config.group(2), config.group(3), cases


def require_java_25() -> None:
    """Fail clearly if the active compiler is not the project's Java version."""
    try:
        result = subprocess.run(
            ["javac", "-version"], capture_output=True, text=True, check=False
        )
    except FileNotFoundError as error:
        raise RuntimeError("javac was not found; activate Java 25") from error
    version_text = (result.stdout + result.stderr).strip()
    if result.returncode != 0 or not re.search(r"\b25(?:\.|\b)", version_text):
        raise RuntimeError(
            f"Java 25 is required, but the active compiler reports: {version_text}"
        )


def compile_sources(source_directory: Path, classes_directory: Path) -> None:
    """Compile all project Java sources before starting the UI sessions."""
    sources = sorted(source_directory.rglob("*.java"))
    if not sources:
        raise RuntimeError(f"no Java sources found below {source_directory}")
    classes_directory.mkdir(parents=True, exist_ok=True)
    command = ["javac", "-d", str(classes_directory), *map(str, sources)]
    result = subprocess.run(command, capture_output=True, text=True, check=False)
    if result.returncode != 0:
        details = normalized_output(result.stdout + result.stderr).rstrip()
        raise RuntimeError(f"compilation failed:\n{details}")


def show_transcript(case: TestCase, actual: str) -> None:
    """Print a readable record of the input and captured program output."""
    print(f"=== Test case: {case.name} ===")
    print(f"Aim: {case.aim}")
    print("--- Console input ---")
    for command in case.commands.splitlines():
        print(f"> {command}")
    print("--- Console output ---")
    print(actual, end="" if actual.endswith("\n") else "\n")


def run_case(classes_directory: Path, main_class: str, case: TestCase) -> bool:
    """Run one fresh process and compare its complete stdout exactly."""
    try:
        with tempfile.TemporaryDirectory() as working_directory:
            result = subprocess.run(
                ["java", "-cp", str(classes_directory), main_class],
                cwd=working_directory,
                input=case.commands,
                capture_output=True,
                text=True,
                check=False,
                timeout=30,
            )
    except subprocess.TimeoutExpired as error:
        actual = normalized_output(error.stdout or "")
        expected = normalized_output(case.expected)
        show_transcript(case, actual)
        print(f"FAIL: {case.name} timed out after 30 seconds")
        print("--- Actual output ---")
        print(actual, end="" if actual.endswith("\n") else "\n")
        print("--- Expected output ---")
        print(expected, end="" if expected.endswith("\n") else "\n")
        return False
    except FileNotFoundError:
        expected = normalized_output(case.expected)
        show_transcript(case, "")
        print("FAIL: java was not found")
        print("--- Actual output ---")
        print("<no output: process could not be launched>")
        print("--- Expected output ---")
        print(expected, end="" if expected.endswith("\n") else "\n")
        return False

    actual = normalized_output(result.stdout)
    expected = normalized_output(case.expected)
    show_transcript(case, actual)
    if result.returncode != 0:
        print(f"FAIL: process exited with status {result.returncode}")
        if result.stderr:
            print("--- Standard error ---")
            print(normalized_output(result.stderr), end="")
        print("--- Expected output ---")
        print(expected, end="" if expected.endswith("\n") else "\n")
        return False
    if actual != expected:
        print("FAIL: actual output did not match expected output")
        print("--- Actual output ---")
        print(actual, end="" if actual.endswith("\n") else "\n")
        print("--- Expected output ---")
        print(expected, end="" if expected.endswith("\n") else "\n")
        return False

    print("PASS")
    return True


def main() -> int:
    """Compile the application and run planned cases until one fails."""
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("plan", type=Path, help="path to ui-test-plan.md")
    args = parser.parse_args()

    try:
        source_directory, classes_directory, main_class, cases = parse_plan(
            args.plan.resolve()
        )
        require_java_25()
        compile_sources(source_directory, classes_directory)
    except (OSError, ValueError, RuntimeError) as error:
        print(f"TEST SESSION FAILED: {error}", file=sys.stderr)
        return 1

    print(f"Compiled with Java 25. Running {len(cases)} UI test case(s).")
    for case in cases:
        if not run_case(classes_directory, main_class, case):
            print("Test session terminated at the first failure.")
            return 1
    print(f"All {len(cases)} UI test case(s) passed.")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
