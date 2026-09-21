#!/usr/bin/env python3
"""Compile Twizzy and run exact console tests from a Markdown plan."""

from __future__ import annotations

import difflib
import re
import subprocess
import sys
import tempfile
from dataclasses import dataclass
from pathlib import Path


DIVIDER = "_" * 60
BANNER = r""" _______        _                     
|__   __|      (_)                    
   | |_      ___ __________   _       
   | \ \ /\ / / |_  /_  / | | |      
   | |\ V  V /| |/ / / /| |_| |      
   |_| \_/\_/ |_/___/___| \__, |      
                           __/ |      
                          |___/"""
STARTUP = (f"{DIVIDER}\n{BANNER}\nWsg, I'm Twizzy — your task-list twin.\n"
           f"Type help to see what I can do.\n{DIVIDER}")


@dataclass(frozen=True)
class TestCase:
    """A named console interaction and its exact expected output."""

    name: str
    aim: str
    commands: str
    expected: str


def parse_plan(plan_path: Path) -> list[TestCase]:
    """Parse UI test cases from the project's Markdown test plan."""
    plan = plan_path.read_text(encoding="utf-8")
    sections = re.finditer(r"^## (.+?)\n(.*?)(?=^## |\Z)", plan, re.MULTILINE | re.DOTALL)
    cases = []

    for section in sections:
        name, body = section.groups()
        aim = re.search(r"\*\*Aim:\*\*\s*(.+)", body)
        inputs = re.search(r"\*\*Inputs:\*\*\s*```text\n(.*?)\n```", body, re.DOTALL)
        expected = re.search(r"\*\*Expected output:\*\*\s*```text\n(.*?)\n```", body, re.DOTALL)
        if not (aim and inputs and expected):
            raise ValueError(f"{name} must specify Aim, Inputs, and Expected output")

        expanded = expected.group(1).replace("{{STARTUP}}", STARTUP).replace("{{DIVIDER}}", DIVIDER)
        cases.append(TestCase(name, aim.group(1).strip(), inputs.group(1), expanded))

    if not cases:
        raise ValueError("the test plan contains no test cases")
    return cases


def print_transcript(case: TestCase, actual: str) -> None:
    """Print the commands and captured output for one test case."""
    print(f"=== {case.name} ===")
    print(f"Aim: {case.aim}")
    print("--- Console input ---")
    print(case.commands)
    print("--- Console output ---")
    print(actual)


def split_sessions(commands: str) -> list[str]:
    """Split commands at restart markers without discarding intentional blank input."""
    sessions = []
    current_lines = []
    for line in commands.splitlines(keepends=True):
        if line.strip() == "{{RESTART}}":
            sessions.append("".join(current_lines))
            current_lines = []
        else:
            current_lines.append(line)
    sessions.append("".join(current_lines))
    return sessions


def fail(case: TestCase, actual: str, reason: str) -> int:
    """Report one failed case with expected and actual output."""
    print_transcript(case, actual)
    print(f"FAIL: {reason}")
    print("--- Expected output ---")
    print(case.expected)
    print("--- Actual output ---")
    print(actual)
    print("--- Difference (expected -> actual) ---")
    print("".join(difflib.unified_diff(
        case.expected.splitlines(keepends=True),
        actual.splitlines(keepends=True),
        fromfile="expected",
        tofile="actual",
    )), end="")
    return 1


def main() -> int:
    """Compile the project and run each case, stopping on failure."""
    if len(sys.argv) != 2:
        print("Usage: run_ui_tests.py test/ui-test-plan.md", file=sys.stderr)
        return 2

    try:
        cases = parse_plan(Path(sys.argv[1]))
    except (OSError, ValueError) as error:
        print(f"Test plan error: {error}", file=sys.stderr)
        return 2

    version = subprocess.run(["javac", "-version"], capture_output=True, text=True, check=False)
    version_text = (version.stdout + version.stderr).strip()
    if version.returncode != 0 or not re.match(r"javac 25(?:\.|$)", version_text):
        print(f"Java 25 is required; found: {version_text or 'javac unavailable'}", file=sys.stderr)
        return 2

    sources = sorted(
        source for source in Path("src/main/java").rglob("*.java")
        if "gui" not in source.relative_to("src/main/java").parts
    )
    if not sources:
        print("No Java source files found in src/main/java", file=sys.stderr)
        return 2

    with tempfile.TemporaryDirectory(prefix="twizzy-ui-test-") as build_directory:
        compilation = subprocess.run(
            ["javac", "-Xlint:all", "-d", build_directory, *map(str, sources)],
            capture_output=True,
            text=True,
            check=False,
        )
        if compilation.returncode != 0:
            print("Compilation failed:", file=sys.stderr)
            print(compilation.stdout + compilation.stderr, file=sys.stderr)
            return 2

        for case in cases:
            actual_parts = []
            with tempfile.TemporaryDirectory(prefix="twizzy-ui-case-") as case_directory:
                for commands in split_sessions(case.commands):
                    try:
                        result = subprocess.run(
                            ["java", "-cp", build_directory, "twizzy.Twizzy"],
                            input=commands if commands.endswith("\n") else commands + "\n",
                            capture_output=True,
                            text=True,
                            timeout=10,
                            check=False,
                            cwd=case_directory,
                        )
                    except subprocess.TimeoutExpired as error:
                        return fail(case, error.stdout or "", "program timed out after 10 seconds")

                    actual_parts.append(result.stdout.rstrip("\n"))
                    if result.returncode != 0:
                        actual = "\n".join(actual_parts)
                        return fail(case, actual, f"program exited with status {result.returncode}; stderr: {result.stderr}")
                    if result.stderr:
                        actual = "\n".join(actual_parts)
                        return fail(case, actual, f"program wrote to stderr: {result.stderr}")

            actual = "\n".join(actual_parts)
            if actual != case.expected:
                return fail(case, actual, "actual output did not match expected output")

            print_transcript(case, actual)
            print("PASS\n")

    print(f"All {len(cases)} UI test case(s) passed.")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
