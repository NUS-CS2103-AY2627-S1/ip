#!/usr/bin/env python3
"""Runs the console UI test cases recorded in test/ui-test-plan.md."""

from __future__ import annotations

import re
import subprocess
import sys
import tempfile
from dataclasses import dataclass
from pathlib import Path


@dataclass(frozen=True)
class TestCase:
    """A single isolated console UI test session."""

    identifier: str
    name: str
    aim: str
    inputs: str
    expected_output: str


def normalize_line_endings(text: str) -> str:
    """Converts platform-specific line endings to Unix line endings."""
    return text.replace("\r\n", "\n").replace("\r", "\n")


def extract_section(section: str, heading: str, case_id: str) -> str:
    """Extracts a fenced text block under a level-three heading."""
    pattern = (
        rf"^### {re.escape(heading)}[ \t]*\n"
        rf"(?:[ \t]*\n)*```(?:text)?[ \t]*\n(.*?)\n```[ \t]*$"
    )
    match = re.search(pattern, section, flags=re.MULTILINE | re.DOTALL)
    if not match:
        raise ValueError(f"{case_id} is missing a valid '{heading}' fenced text block")
    return normalize_line_endings(match.group(1)) + "\n"


def parse_test_plan(plan_path: Path) -> list[TestCase]:
    """Parses ordered test cases from the Markdown test plan."""
    plan = normalize_line_endings(plan_path.read_text(encoding="utf-8"))
    case_matches = list(
        re.finditer(
            r"^## (TC-[A-Za-z0-9-]+): ([^\n]+)\n(.*?)(?=^## TC-|\Z)",
            plan,
            flags=re.MULTILINE | re.DOTALL,
        )
    )
    if not case_matches:
        raise ValueError("the test plan contains no test cases")

    cases = []
    identifiers = set()
    for match in case_matches:
        identifier, name, section = match.groups()
        if identifier in identifiers:
            raise ValueError(f"duplicate test case ID: {identifier}")
        identifiers.add(identifier)

        aim_match = re.search(
            r"^### Aim\s*\n(.+?)(?=\n### )",
            section,
            flags=re.MULTILINE | re.DOTALL,
        )
        if not aim_match:
            raise ValueError(f"{identifier} is missing an Aim section")

        cases.append(
            TestCase(
                identifier=identifier,
                name=name.strip(),
                aim=aim_match.group(1).strip(),
                inputs=extract_section(section, "Inputs", identifier),
                expected_output=extract_section(section, "Expected output", identifier),
            )
        )
    return cases


def print_block(label: str, content: str) -> None:
    """Prints a labeled transcript block without altering its content."""
    print(f"--- {label} ---")
    print(content, end="" if content.endswith("\n") else "\n")


def require_java_25(project_root: Path) -> None:
    """Stops the run unless both Java tools report major version 25."""
    for command in (["java", "-version"], ["javac", "-version"]):
        result = subprocess.run(
            command,
            cwd=project_root,
            capture_output=True,
            text=True,
            check=False,
        )
        version_text = normalize_line_endings(result.stdout + result.stderr)
        if result.returncode != 0 or not re.search(r'\b(?:java|javac)(?: version)? "?25(?:[.\s])', version_text):
            raise RuntimeError(
                f"{' '.join(command)} must use Java 25; reported:\n{version_text.rstrip()}"
            )


def main() -> int:
    """Compiles Koara and runs test cases sequentially, stopping on failure."""
    project_root = Path(__file__).resolve().parents[4]
    plan_path = project_root / "test" / "ui-test-plan.md"
    source_directory = project_root / "src" / "main" / "java"

    try:
        require_java_25(project_root)
        cases = parse_test_plan(plan_path)
    except (OSError, RuntimeError, ValueError) as error:
        print(f"TEST SETUP FAILED: {error}", file=sys.stderr)
        return 2

    with tempfile.TemporaryDirectory(prefix="koara-ui-test-") as class_directory:
        source_paths = sorted(source_directory.rglob("*.java"))
        if not source_paths:
            print(f"TEST COMPILATION FAILED: no Java sources found in {source_directory}", file=sys.stderr)
            return 2
        compilation = subprocess.run(
            ["javac", "-d", class_directory, *(str(path) for path in source_paths)],
            cwd=project_root,
            capture_output=True,
            text=True,
            check=False,
        )
        if compilation.returncode != 0:
            print("TEST COMPILATION FAILED", file=sys.stderr)
            print_block("Compiler output", compilation.stdout + compilation.stderr)
            return 2

        passed = 0
        for test_case in cases:
            with tempfile.TemporaryDirectory(prefix="koara-ui-case-") as case_directory:
                result = subprocess.run(
                    ["java", "-cp", class_directory, "koara.Koara"],
                    cwd=case_directory,
                    input=test_case.inputs,
                    capture_output=True,
                    text=True,
                    check=False,
                )
            actual_output = normalize_line_endings(result.stdout)
            expected_output = normalize_line_endings(test_case.expected_output)

            print(f"\n{test_case.identifier}: {test_case.name}")
            print(f"Aim: {test_case.aim}")
            print_block("Console input", test_case.inputs)
            print_block("Console output", actual_output)

            failure_reason = None
            if result.returncode != 0:
                failure_reason = f"program exited with code {result.returncode}"
            elif result.stderr:
                failure_reason = "program wrote to stderr"
            elif actual_output != expected_output:
                failure_reason = "stdout did not match the expected output"

            if failure_reason:
                print(f"RESULT: FAILED ({failure_reason})")
                if result.stderr:
                    print_block("Standard error", normalize_line_endings(result.stderr))
                print_block("Actual output", actual_output)
                print_block("Expected output", expected_output)
                print(f"Stopped after the first failure. {len(cases) - passed - 1} case(s) were not run.")
                return 1

            passed += 1
            print("RESULT: PASSED")

    print(f"\nTEST SESSION PASSED: {passed}/{len(cases)} case(s).")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
