#!/usr/bin/env python3
"""Compile Labubu and run the console UI test cases in test/ui-test-plan.md."""

from __future__ import annotations

import os
import re
import shutil
import subprocess
import sys
import tempfile
from dataclasses import dataclass
from pathlib import Path


@dataclass
class TestCase:
    """A console UI test case read from the Markdown test plan."""

    name: str
    aim: str
    inputs: str
    expected_output: str


def normalize(text: str) -> str:
    """Normalize line endings while preserving all meaningful console output."""
    return text.replace("\r\n", "\n").replace("\r", "\n").rstrip("\n")


def read_code_block(section: str, label: str) -> str:
    """Return the text block belonging to a required Markdown test-case field."""
    match = re.search(
        rf"^\*\*{re.escape(label)}:\*\*\s*\n```text\n(.*?)\n```",
        section,
        re.MULTILINE | re.DOTALL,
    )
    if match is None:
        raise ValueError(f"Missing {label} text block.")
    return match.group(1)


def load_test_cases(plan_path: Path) -> list[TestCase]:
    """Read the required test-case format from the project's Markdown test plan."""
    content = plan_path.read_text(encoding="utf-8")
    matches = list(re.finditer(r"^## Test case: (.+)$", content, re.MULTILINE))
    if not matches:
        raise ValueError("The test plan has no test cases.")

    test_cases = []
    for index, match in enumerate(matches):
        section_end = matches[index + 1].start() if index + 1 < len(matches) else len(content)
        section = content[match.end():section_end]
        aim_match = re.search(r"^\*\*Aim:\*\*\s*(.+)$", section, re.MULTILINE)
        if aim_match is None:
            raise ValueError(f"Test case '{match.group(1)}' is missing an aim.")
        test_cases.append(TestCase(
            name=match.group(1).strip(),
            aim=aim_match.group(1).strip(),
            inputs=read_code_block(section, "Inputs"),
            expected_output=read_code_block(section, "Expected output"),
        ))
    return test_cases


def find_java_executable(name: str) -> str:
    """Find a Java executable, preferring the configured Java 25 home."""
    suffix = ".exe" if os.name == "nt" else ""
    java_home = os.environ.get("JAVA_HOME")
    if java_home:
        candidate = Path(java_home) / "bin" / f"{name}{suffix}"
        if candidate.is_file():
            return str(candidate)
    executable = shutil.which(name)
    if executable:
        return executable
    raise FileNotFoundError(f"Cannot find {name}. Set JAVA_HOME to a JDK 25 installation.")


def write_session(session_path: Path, records: list[tuple[TestCase, str, bool]]) -> None:
    """Save the inputs and observed output for every completed test case."""
    lines = ["# UI Test Session", ""]
    for test_case, actual_output, passed in records:
        lines.extend([
            f"## {test_case.name} — {'PASSED' if passed else 'FAILED'}",
            "",
            f"**Aim:** {test_case.aim}",
            "",
            "**Console input:**",
            "```text",
            test_case.inputs,
            "```",
            "",
            "**Console output:**",
            "```text",
            actual_output,
            "```",
            "",
        ])
        if not passed:
            lines.extend([
                "**Expected output:**",
                "```text",
                test_case.expected_output,
                "```",
                "",
            ])
    session_path.write_text("\n".join(lines), encoding="utf-8")


def main() -> int:
    """Compile the application and run UI test cases until one fails."""
    if hasattr(sys.stdout, "reconfigure"):
        sys.stdout.reconfigure(encoding="utf-8", errors="backslashreplace")
    root = Path.cwd()
    plan_path = root / "test" / "ui-test-plan.md"
    session_path = root / "_temp" / "ui-test-session.md"
    class_path = root / "_temp" / "ui-test-classes"

    try:
        test_cases = load_test_cases(plan_path)
        javac = find_java_executable("javac")
        java = find_java_executable("java")
    except (FileNotFoundError, ValueError) as error:
        print(f"Test setup failed: {error}")
        return 2

    source_files = sorted((root / "src" / "main" / "java").rglob("*.java"))
    if not source_files:
        print("Test setup failed: no Java source files were found.")
        return 2
    class_path.mkdir(parents=True, exist_ok=True)
    session_path.parent.mkdir(parents=True, exist_ok=True)

    compile_result = subprocess.run(
        [javac, "-encoding", "UTF-8", "-d", str(class_path), *(str(path) for path in source_files)],
        cwd=root,
        capture_output=True,
        text=True,
        encoding="utf-8",
        errors="replace",
    )
    if compile_result.returncode != 0:
        print("Compilation failed:")
        print(compile_result.stderr)
        return 2

    records: list[tuple[TestCase, str, bool]] = []
    for test_case in test_cases:
        with tempfile.TemporaryDirectory() as test_directory:
            result = subprocess.run(
                [java, "-Dfile.encoding=UTF-8", "-Dstdout.encoding=UTF-8", "-Dstderr.encoding=UTF-8",
                 "-cp", str(class_path), "labubu.Labubu"],
                cwd=test_directory,
                input=test_case.inputs + "\n",
                capture_output=True,
                text=True,
                encoding="utf-8",
                errors="replace",
            )
        actual_output = normalize(result.stdout + result.stderr)
        passed = result.returncode == 0 and actual_output == normalize(test_case.expected_output)
        records.append((test_case, actual_output, passed))
        write_session(session_path, records)

        if not passed:
            print(f"FAILED: {test_case.name}")
            print(f"Aim: {test_case.aim}")
            print("Expected output:")
            print(normalize(test_case.expected_output))
            print("Actual output:")
            print(actual_output)
            print(f"Session record: {session_path}")
            return 1
        print(f"PASSED: {test_case.name}")

    print(f"All {len(test_cases)} test case(s) passed.")
    print(f"Session record: {session_path}")
    return 0


if __name__ == "__main__":
    sys.exit(main())
