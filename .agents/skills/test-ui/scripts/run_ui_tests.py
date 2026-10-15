#!/usr/bin/env python3
"""Run exact-output console UI tests recorded in test/ui-test-plan.md."""

from __future__ import annotations

import argparse
import difflib
import re
import subprocess
import sys
from dataclasses import dataclass
from pathlib import Path


@dataclass(frozen=True)
class UiTestCase:
    """A single console session and its exact expected standard output."""

    identifier: str
    title: str
    aim: str
    inputs: str
    expected_output: str


def normalize_line_endings(text: str) -> str:
    """Treat Windows and Unix line endings as equivalent."""
    return text.replace("\r\n", "\n").replace("\r", "\n")


def read_setting(plan: str, label: str) -> str:
    """Read an inline-code setting from the plan's configuration list."""
    pattern = re.compile(
        rf"^\s*-\s*{re.escape(label)}:\s*`([^`\r\n]+)`\s*$",
        re.IGNORECASE | re.MULTILINE,
    )
    match = pattern.search(plan)
    if not match:
        raise ValueError(f"missing configuration setting: {label}")
    return match.group(1)


def read_fenced_block(section: str, label: str, case_id: str) -> str:
    """Read a text fence belonging to one test-case field."""
    pattern = re.compile(
        rf"^\*\*{re.escape(label)}:\*\*\s*\n"
        rf"```text\s*\n(.*?)^```\s*$",
        re.IGNORECASE | re.MULTILINE | re.DOTALL,
    )
    match = pattern.search(section)
    if not match:
        raise ValueError(f"{case_id} is missing a {label!r} text block")
    return match.group(1)


def parse_test_cases(plan: str) -> list[UiTestCase]:
    """Parse ordered test cases from the Markdown plan."""
    heading_pattern = re.compile(r"^###\s+([^:\r\n]+):\s*(.+?)\s*$", re.MULTILINE)
    headings = list(heading_pattern.finditer(plan))
    if not headings:
        raise ValueError("the plan contains no test cases")

    cases: list[UiTestCase] = []
    identifiers: set[str] = set()
    for index, heading in enumerate(headings):
        case_id = heading.group(1).strip()
        title = heading.group(2).strip()
        section_end = headings[index + 1].start() if index + 1 < len(headings) else len(plan)
        section = plan[heading.end():section_end]

        if case_id in identifiers:
            raise ValueError(f"duplicate test-case ID: {case_id}")
        identifiers.add(case_id)

        aim_match = re.search(r"^\*\*Aim:\*\*\s*(.+?)\s*$", section, re.MULTILINE)
        if not aim_match:
            raise ValueError(f"{case_id} is missing a single-line aim")

        cases.append(
            UiTestCase(
                identifier=case_id,
                title=title,
                aim=aim_match.group(1),
                inputs=read_fenced_block(section, "Inputs", case_id),
                expected_output=read_fenced_block(section, "Expected output", case_id),
            )
        )
    return cases


def command_version(command: str) -> str:
    """Return combined version output for a Java command."""
    try:
        result = subprocess.run(
            [command, "-version"],
            capture_output=True,
            text=True,
            encoding="utf-8",
            errors="replace",
            check=False,
        )
    except FileNotFoundError as error:
        raise RuntimeError(f"{command} was not found on PATH") from error
    return normalize_line_endings(result.stdout + result.stderr)


def require_java_version(expected_major: int) -> None:
    """Ensure both the Java compiler and runtime use the required major version."""
    for command in ("javac", "java"):
        version_text = command_version(command)
        match = re.search(r'(?:javac\s+|version\s+")(\d+)', version_text)
        if not match:
            raise RuntimeError(f"could not determine {command} version from: {version_text.strip()}")
        actual_major = int(match.group(1))
        if actual_major != expected_major:
            raise RuntimeError(
                f"{command} {expected_major} is required, but version {actual_major} is active"
            )


def ensure_within_repository(path: Path, repository: Path, label: str) -> None:
    """Reject plan paths that escape the repository."""
    if path != repository and repository not in path.parents:
        raise ValueError(f"{label} must be inside the repository: {path}")


def print_block(label: str, text: str) -> None:
    """Print a transcript block without changing its contents."""
    print(f"--- {label} ---")
    sys.stdout.write(text)
    if text and not text.endswith("\n"):
        print()
    print(f"--- End {label.lower()} ---")


def print_failure(case: UiTestCase, actual: str, stderr: str, reason: str) -> None:
    """Print all evidence for the first failing test case."""
    print(f"=== FAILED {case.identifier}: {case.title} ===")
    print(f"Aim: {case.aim}")
    print(f"Reason: {reason}")
    print_block("Console input", normalize_line_endings(case.inputs))
    print_block("Actual output", actual)
    print_block("Expected output", normalize_line_endings(case.expected_output))
    if stderr:
        print_block("Standard error", stderr)

    difference = "".join(
        difflib.unified_diff(
            normalize_line_endings(case.expected_output).splitlines(keepends=True),
            actual.splitlines(keepends=True),
            fromfile="expected",
            tofile="actual",
        )
    )
    if difference:
        print_block("Unified diff", difference)


def run_tests(plan_path: Path) -> int:
    """Build the project with Gradle, then execute test cases until completion or failure."""
    repository = Path.cwd().resolve()
    plan_path = plan_path.resolve()
    ensure_within_repository(plan_path, repository, "test plan")
    plan = normalize_line_endings(plan_path.read_text(encoding="utf-8"))

    source_directory = (repository / read_setting(plan, "Source directory")).resolve()
    ensure_within_repository(source_directory, repository, "source directory")
    main_class = read_setting(plan, "Main class")
    java_version = int(read_setting(plan, "Java version"))
    timeout_seconds = float(read_setting(plan, "Timeout seconds"))
    test_cases = parse_test_cases(plan)

    if timeout_seconds <= 0:
        raise ValueError("Timeout seconds must be positive")
    if not any(source_directory.rglob("*.java")):
        raise ValueError(f"no Java source files found under {source_directory}")

    require_java_version(java_version)
    gradle_wrapper = repository / ("gradlew.bat" if sys.platform == "win32" else "gradlew")
    if not gradle_wrapper.is_file():
        raise RuntimeError(f"Gradle wrapper was not found: {gradle_wrapper}")

    compile_result = subprocess.run(
            [str(gradle_wrapper), "--console=plain", "classes"],
            cwd=repository,
            capture_output=True,
            text=True,
            encoding="utf-8",
            errors="replace",
            check=False,
        )
    if compile_result.returncode != 0:
        print("UI test session terminated: Gradle compilation failed.")
        print_block(
            "Compiler output",
            normalize_line_endings(compile_result.stdout + compile_result.stderr),
        )
        return 2

    build_directory = repository / "build" / "classes" / "java" / "main"
    if not build_directory.is_dir():
        raise RuntimeError(f"Gradle did not produce application classes: {build_directory}")

    for case in test_cases:
        standard_input = normalize_line_endings(case.inputs)
        if standard_input and not standard_input.endswith("\n"):
            standard_input += "\n"
        try:
            result = subprocess.run(
                ["java", "-cp", str(build_directory), main_class],
                cwd=repository,
                input=standard_input,
                capture_output=True,
                text=True,
                encoding="utf-8",
                errors="replace",
                timeout=timeout_seconds,
                check=False,
            )
        except subprocess.TimeoutExpired as error:
            actual = normalize_line_endings(error.stdout or "")
            stderr = normalize_line_endings(error.stderr or "")
            print_failure(
                case,
                actual,
                stderr,
                f"timed out after {timeout_seconds:g} seconds",
            )
            print("UI test session terminated after the first failure.")
            return 1

        actual = normalize_line_endings(result.stdout)
        expected = normalize_line_endings(case.expected_output)
        stderr = normalize_line_endings(result.stderr)
        if result.returncode != 0 or actual != expected:
            reason = (
                f"program exited with status {result.returncode}"
                if result.returncode != 0
                else "actual output did not match expected output"
            )
            print_failure(case, actual, stderr, reason)
            print("UI test session terminated after the first failure.")
            return 1

        print(f"=== PASSED {case.identifier}: {case.title} ===")
        print(f"Aim: {case.aim}")
        print_block("Console input", standard_input)
        print_block("Console output", actual)

    print(f"UI test session passed: {len(test_cases)} case(s).")
    return 0


def main(argv: list[str]) -> int:
    """Parse arguments and report plan or environment errors clearly."""
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("plan", type=Path, help="Markdown UI test plan")
    args = parser.parse_args(argv)
    try:
        return run_tests(args.plan)
    except (OSError, RuntimeError, ValueError) as error:
        print(f"UI test session could not start: {error}", file=sys.stderr)
        return 2


if __name__ == "__main__":
    raise SystemExit(main(sys.argv[1:]))
