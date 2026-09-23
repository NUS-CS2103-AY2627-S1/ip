"""Runs console UI test cases recorded in test/ui-test-plan.md."""

from __future__ import annotations

import argparse
import os
import re
import subprocess
import sys
import tempfile
from dataclasses import dataclass
from pathlib import Path


ANSI_ESCAPE = re.compile(r"\x1B\[[0-?]*[ -/]*[@-~]")
CASE_PATTERN = re.compile(
    r"^## Test case: (?P<name>.+?)\r?\n"
    r"Aim: (?P<aim>.+?)\r?\n\r?\n"
    r"Input:\r?\n```text\r?\n(?P<input>.*?)\r?\n```\r?\n\r?\n"
    r"Expected output:\r?\n```text\r?\n(?P<expected>.*?)\r?\n```",
    re.MULTILINE | re.DOTALL,
)


@dataclass
class UiTestCase:
    """A command session and its expected console output."""

    name: str
    aim: str
    commands: str
    expected_output: str


def normalise_output(output: str) -> str:
    """Removes terminal colour codes and makes line endings comparable."""
    without_colours = ANSI_ESCAPE.sub("", output)
    return without_colours.replace("\r\n", "\n").rstrip()


def load_test_cases(plan_path: Path) -> list[UiTestCase]:
    """Loads UI test cases from the documented Markdown format."""
    plan = plan_path.read_text(encoding="utf-8")
    cases = [
        UiTestCase(
            name=match.group("name"),
            aim=match.group("aim"),
            commands=match.group("input"),
            expected_output=normalise_output(match.group("expected")),
        )
        for match in CASE_PATTERN.finditer(plan)
    ]
    if not cases:
        raise ValueError("No valid test cases found in " + str(plan_path))
    return cases


def find_java_tool(tool_name: str) -> str:
    """Finds a Java 25 tool, preferring JAVA25_HOME when it is set."""
    candidate_homes = []
    configured_home = os.environ.get("JAVA25_HOME")
    if configured_home:
        candidate_homes.append(Path(configured_home))
    program_files = Path(os.environ.get("ProgramFiles", r"C:\\Program Files"))
    candidate_homes.extend(sorted((program_files / "Eclipse Adoptium").glob("jdk-25*"), reverse=True))

    executable_name = tool_name + (".exe" if os.name == "nt" else "")
    for java_home in candidate_homes:
        executable = java_home / "bin" / executable_name
        if executable.is_file():
            return str(executable)
    return tool_name


def require_java_25(javac_command: str) -> None:
    """Checks that the selected compiler is Java 25."""
    result = subprocess.run(
        [javac_command, "-version"], text=True, capture_output=True, check=False
    )
    version_output = (result.stdout + result.stderr).strip()
    if result.returncode != 0 or not re.search(r"\b25(?:\.|\b)", version_output):
        raise RuntimeError(
            "Java 25 is required, but the selected compiler reports: " + version_output
        )


def compile_program(project_root: Path, output_directory: Path, javac_command: str) -> None:
    """Compiles all Java source files so tests never use stale class files."""
    source_files = sorted((project_root / "src" / "main" / "java").rglob("*.java"))
    if not source_files:
        raise ValueError("No Java source files found in src/main/java")
    output_directory.mkdir(parents=True, exist_ok=True)
    result = subprocess.run(
        [javac_command, "-d", str(output_directory), *map(str, source_files)],
        cwd=project_root,
        text=True,
        capture_output=True,
        check=False,
    )
    if result.returncode != 0:
        print("Compilation failed:")
        print(result.stdout + result.stderr)
        raise RuntimeError("UI tests were not run.")


def print_transcript(label: str, content: str) -> None:
    """Prints a labelled console transcript section."""
    print(label)
    print("-" * len(label))
    print(content)


def run_test_case(
    project_root: Path, class_path: Path, test_case: UiTestCase, main_class: str, java_command: str
) -> bool:
    """Runs one isolated UI test case and compares its output exactly."""
    # Give each UI case its own working directory so relative application data
    # cannot leak between otherwise independent command sessions.
    with tempfile.TemporaryDirectory(prefix="bob-ui-test-") as test_directory:
        working_directory = Path(test_directory) / "working-directory"
        working_directory.mkdir()
        result = subprocess.run(
            [java_command, "-cp", str(class_path), main_class],
            cwd=working_directory,
            input=test_case.commands + "\n",
            text=True,
            capture_output=True,
            check=False,
        )
    actual_output = normalise_output(result.stdout + result.stderr)

    print("\nTest case: " + test_case.name)
    print("Aim: " + test_case.aim)
    print_transcript("Console input", test_case.commands)
    print_transcript("Console output", actual_output)

    if result.returncode != 0:
        print("FAILED: The program exited with code " + str(result.returncode) + ".")
        print_transcript("Expected output", test_case.expected_output)
        return False
    if actual_output != test_case.expected_output:
        print("FAILED: Actual output does not match expected output.")
        print_transcript("Expected output", test_case.expected_output)
        print_transcript("Actual output", actual_output)
        return False

    print("PASSED")
    return True


def main() -> int:
    """Compiles Bob and runs every recorded UI test case in sequence."""
    parser = argparse.ArgumentParser(description="Run console UI tests for Bob.")
    parser.add_argument("--plan", default="test/ui-test-plan.md", help="path to the Markdown test plan")
    parser.add_argument("--main", default="bob.Bob", help="fully qualified Java main class")
    arguments = parser.parse_args()

    project_root = Path.cwd()
    plan_path = project_root / arguments.plan
    try:
        test_cases = load_test_cases(plan_path)
        class_path = project_root / "build" / "ui-test-classes"
        javac_command = find_java_tool("javac")
        java_command = find_java_tool("java")
        require_java_25(javac_command)
        compile_program(project_root, class_path, javac_command)
    except (OSError, ValueError, RuntimeError) as error:
        print("ERROR: " + str(error))
        return 1

    for test_case in test_cases:
        if not run_test_case(project_root, class_path, test_case, arguments.main, java_command):
            print("Stopping after the first failed test case.")
            return 1

    print("\nAll " + str(len(test_cases)) + " UI test case(s) passed.")
    return 0


if __name__ == "__main__":
    sys.exit(main())
