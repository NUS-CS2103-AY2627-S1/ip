#!/usr/bin/env python3
"""Compile and run the console UI cases documented in a Markdown test plan."""

from __future__ import annotations

import re
import shutil
import subprocess
import sys
import tempfile
from dataclasses import dataclass
from pathlib import Path


@dataclass
class TestCase:
    """One independently executed command session from the UI test plan."""

    identifier: str
    aim: str
    user_input: str
    expected_output: str
    saved_tasks: str | None


def normalise_newlines(text: str) -> str:
    """Make platform line endings comparable without changing meaningful spaces."""
    return text.replace("\r\n", "\n").replace("\r", "\n")


def code_block(section: str, heading: str, case_id: str) -> str:
    """Return the text-code block that follows a required test-case heading."""
    match = re.search(
        rf"^### {re.escape(heading)}\s*\n+```text\n(.*?)\n```",
        section,
        flags=re.MULTILINE | re.DOTALL,
    )
    if match is None:
        raise ValueError(f"Test case '{case_id}' is missing a '{heading}' text block.")
    return normalise_newlines(match.group(1)) + "\n"


def read_plan(plan_path: Path) -> tuple[Path, str, list[TestCase]]:
    """Parse the Java settings and test cases from the Markdown plan."""
    plan = normalise_newlines(plan_path.read_text(encoding="utf-8"))
    setup_match = re.search(r"^## Program setup\s*$([\s\S]*?)(?=^## |\Z)", plan, re.MULTILINE)
    if setup_match is None:
        raise ValueError("The plan is missing its '## Program setup' section.")

    setup = setup_match.group(1)
    source_match = re.search(r"^- Source directory: `([^`]+)`\s*$", setup, re.MULTILINE)
    main_match = re.search(r"^- Main class: `([^`]+)`\s*$", setup, re.MULTILINE)
    java_match = re.search(r"^- Java version: (\d+)\s*$", setup, re.MULTILINE)
    if source_match is None or main_match is None or java_match is None:
        raise ValueError("Program setup needs Java version, source directory, and main class entries.")
    if java_match.group(1) != "25":
        raise ValueError("This project must be tested with Java version 25.")

    source_directory = (plan_path.parent.parent / source_match.group(1)).resolve()
    cases: list[TestCase] = []
    sections = list(re.finditer(r"^## Test case: ([^\n]+)\s*$", plan, re.MULTILINE))
    for index, match in enumerate(sections):
        end = sections[index + 1].start() if index + 1 < len(sections) else len(plan)
        section = plan[match.end() : end]
        identifier = match.group(1).strip()
        aim_match = re.search(r"^### Aim\s*\n+(.+?)(?=^### |\Z)", section, re.MULTILINE | re.DOTALL)
        if aim_match is None:
            raise ValueError(f"Test case '{identifier}' is missing an Aim.")
        cases.append(
            TestCase(
                identifier=identifier,
                aim=aim_match.group(1).strip(),
                user_input=code_block(section, "Input", identifier),
                expected_output=code_block(section, "Expected output", identifier),
                saved_tasks=(code_block(section, "Saved tasks", identifier)
                             if "### Saved tasks" in section else None),
            )
        )
    if not cases:
        raise ValueError("The plan contains no test cases.")
    return source_directory, main_match.group(1), cases


def require_java_25(executable: str) -> None:
    """Confirm that the selected Java tool is version 25 before compiling."""
    result = subprocess.run([executable, "-version"], capture_output=True, text=True, check=False)
    version_text = normalise_newlines(result.stdout + result.stderr)
    if result.returncode != 0 or re.search(r"(?:version [\"]?|javac )25(?:[.\s\"]|$)", version_text) is None:
        raise RuntimeError(f"{executable} must be Java 25. Found:\n{version_text.strip()}")


def compile_program(source_directory: Path, build_directory: Path, main_class: str) -> None:
    """Compile the console entry point and its dependencies without requiring JavaFX."""
    main_source = source_directory / (main_class.replace(".", "/") + ".java")
    if not main_source.is_file():
        raise RuntimeError(f"Main source not found: {main_source}")
    build_directory.mkdir(parents=True, exist_ok=True)
    result = subprocess.run(
        ["javac", "-sourcepath", str(source_directory), "-d", str(build_directory), str(main_source)],
        capture_output=True,
        text=True,
        check=False,
    )
    if result.returncode != 0:
        raise RuntimeError("Compilation failed:\n" + normalise_newlines(result.stdout + result.stderr))


def display_session(case: TestCase, actual_output: str) -> None:
    """Print a readable record of one test's terminal interaction."""
    print(f"\nTEST {case.identifier}: {case.aim}")
    print("CONSOLE INPUT:")
    print(case.user_input, end="")
    print("CONSOLE OUTPUT:")
    print(actual_output, end="" if actual_output.endswith("\n") else "\n")


def main() -> int:
    """Run every documented UI test, stopping immediately at the first mismatch."""
    if len(sys.argv) != 2:
        print("Usage: run_ui_tests.py test/ui-test-plan.md", file=sys.stderr)
        return 2

    try:
        plan_path = Path(sys.argv[1]).resolve()
        source_directory, main_class, cases = read_plan(plan_path)
        for executable in ("java", "javac"):
            if shutil.which(executable) is None:
                raise RuntimeError(f"'{executable}' is not on PATH. Configure a Java 25 JDK first.")
            require_java_25(executable)

        build_directory = plan_path.parent / ".ui-test-build"
        compile_program(source_directory, build_directory, main_class)
        for case in cases:
            with tempfile.TemporaryDirectory(prefix="tungtung-ui-") as session_directory:
                if case.saved_tasks is not None:
                    data_file = Path(session_directory) / "data" / "tungtung.txt"
                    data_file.parent.mkdir()
                    data_file.write_text(case.saved_tasks, encoding="utf-8")
                result = subprocess.run(
                    ["java", "-cp", str(build_directory), main_class],
                    input=case.user_input,
                    cwd=session_directory,
                    capture_output=True,
                    text=True,
                    check=False,
                    timeout=30,
                )
            actual_output = normalise_newlines(result.stdout)
            display_session(case, actual_output)
            if result.returncode != 0 or actual_output != case.expected_output:
                print(f"FAIL: {case.identifier}. Test session terminated.")
                print("EXPECTED OUTPUT:")
                print(case.expected_output, end="")
                print("ACTUAL OUTPUT:")
                print(actual_output, end="" if actual_output.endswith("\n") else "\n")
                if result.returncode != 0:
                    print("PROGRAM ERROR:")
                    print(normalise_newlines(result.stderr), end="")
                return 1
            print("PASS")
    except (OSError, RuntimeError, ValueError, subprocess.TimeoutExpired) as error:
        print(f"UI tests could not run: {error}", file=sys.stderr)
        return 2

    print("\nAll UI tests passed.")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
