#!/usr/bin/env python3
"""Compile Bibi and run UI cases recorded in a Markdown test plan."""

from __future__ import annotations

import argparse
import re
import subprocess
import sys
from pathlib import Path


def read_cases(plan_path: Path) -> list[tuple[str, str, str]]:
    """Return the name, input, and expected output for every plan case."""
    text = plan_path.read_text(encoding="utf-8")
    sections = re.split(r"^## Test ", text, flags=re.MULTILINE)[1:]
    cases = []

    for section in sections:
        title = section.splitlines()[0].strip()
        name = title.split(": ", 1)[1] if ": " in title else title
        input_match = re.search(r"^### Input\s*\n```text\n(.*?)\n```", section,
                                flags=re.MULTILINE | re.DOTALL)
        expected_match = re.search(r"^### Expected output\s*\n```text\n(.*?)\n```", section,
                                   flags=re.MULTILINE | re.DOTALL)
        if input_match is None or expected_match is None:
            raise ValueError(f"Test '{name}' needs Input and Expected output text blocks.")
        cases.append((name, input_match.group(1), expected_match.group(1)))

    if not cases:
        raise ValueError("The test plan does not contain any '## Test' sections.")
    return cases


def ensure_java_25(command: str) -> None:
    """Stop before tests unless the selected Java command reports version 25."""
    version = subprocess.run([command, "--version"], capture_output=True, text=True, check=False)
    version_text = (version.stdout + version.stderr).strip()
    if version.returncode != 0 or not re.search(r"(?:java(?:c)? )?25(?:[.\s]|$)", version_text):
        raise RuntimeError(f"JDK 25 is required, but command reports: {version_text}")


def compile_application(project_root: Path, javac: str) -> None:
    """Compile all Java source files into the project's out directory."""
    source_dir = project_root / "src" / "main" / "java"
    source_files = sorted(source_dir.glob("*.java"))
    if not source_files:
        raise RuntimeError(f"No Java source files found in {source_dir}")

    result = subprocess.run(
        [javac, "-d", str(project_root / "out"), *(str(path) for path in source_files)],
        cwd=project_root,
        capture_output=True,
        text=True,
        check=False,
    )
    if result.returncode != 0:
        raise RuntimeError("Compilation failed:\n" + result.stdout + result.stderr)


def expected_lines_in_order(expected: str, actual: str) -> bool:
    """Check that each non-empty expected line occurs after the previous one."""
    position = 0
    for line in (line for line in expected.splitlines() if line.strip()):
        found_at = actual.find(line, position)
        if found_at == -1:
            return False
        position = found_at + len(line)
    return True


def run_case(project_root: Path, java: str, case_input: str) -> str:
    """Run Bibi with a test case's console input and return its output."""
    result = subprocess.run(
        [java, "-cp", str(project_root / "out"), "Bibi"],
        cwd=project_root,
        input=case_input + "\n",
        capture_output=True,
        text=True,
        check=False,
    )
    return result.stdout + result.stderr


def main() -> int:
    """Run every test case and stop at the first mismatch."""
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--project-root", type=Path, required=True)
    parser.add_argument("--plan", type=Path, required=True)
    parser.add_argument("--javac", default="javac")
    parser.add_argument("--java", default="java")
    args = parser.parse_args()

    project_root = args.project_root.resolve()
    plan_path = args.plan.resolve()
    try:
        cases = read_cases(plan_path)
        ensure_java_25(args.javac)
        ensure_java_25(args.java)
        compile_application(project_root, args.javac)
    except (OSError, RuntimeError, ValueError) as error:
        print(f"TEST SETUP FAILED: {error}", file=sys.stderr)
        return 1

    for number, (name, case_input, expected) in enumerate(cases, start=1):
        actual = run_case(project_root, args.java, case_input)
        print(f"\n=== Test {number}: {name} ===")
        print("Console input:")
        print(case_input)
        print("Console output:")
        print(actual.rstrip())

        if not expected_lines_in_order(expected, actual):
            print("\nTEST FAILED")
            print("Expected output fragments:")
            print(expected)
            print("Actual output:")
            print(actual)
            return 1

        print("TEST PASSED")

    print(f"\nAll {len(cases)} UI tests passed.")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
