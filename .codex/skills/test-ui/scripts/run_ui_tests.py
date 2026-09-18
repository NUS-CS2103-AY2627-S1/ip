#!/usr/bin/env python3
"""Run the Markdown-defined console UI tests for the Chausistant project."""

from __future__ import annotations

import argparse
import difflib
import re
import subprocess
import sys
import tempfile
from dataclasses import dataclass
from pathlib import Path


@dataclass
class TestCase:
    """One named input session and its expected console output."""

    name: str
    aim: str
    commands: list[str]
    expected: str
    match_mode: str
    initial_files: list["FileFixture"]
    initial_directories: list[Path]
    expected_files: list["FileFixture"]


@dataclass
class FileFixture:
    """A file that is set up or checked as part of a test case."""

    path: Path
    content: str


class ProgramRunError(RuntimeError):
    """Indicates that a program session ended unsuccessfully."""

    def __init__(self, message: str, output: str) -> None:
        super().__init__(message)
        self.output = output


TEST_CASE_PATTERN = re.compile(
    r"^## Test case:\s*(?P<name>.+?)\s*$"
    r"(?P<body>.*?)(?=^## Test case:|\Z)",
    re.MULTILINE | re.DOTALL,
)
BLOCK_PATTERN = re.compile(
    r"^### (?P<title>Inputs|Expected output)\s*\n"
    r"```(?:text)?\s*\n(?P<content>.*?)(?:\n)?^```$",
    re.MULTILINE | re.DOTALL,
)
FILE_BLOCK_PATTERN = re.compile(
    r"^### (?P<kind>Initial|Expected) file:\s*(?P<path>.+?)\s*\n"
    r"```(?:text)?\s*\n(?P<content>.*?)(?:\n)?^```$",
    re.MULTILINE | re.DOTALL,
)
DIRECTORY_PATTERN = re.compile(
    r"^### Initial directory:\s*(?P<path>.+?)\s*$",
    re.MULTILINE,
)


def parse_plan(plan_path: Path) -> list[TestCase]:
    """Parse all test cases from the project's Markdown test plan."""
    plan = plan_path.read_text(encoding="utf-8")
    cases: list[TestCase] = []

    for match in TEST_CASE_PATTERN.finditer(plan):
        body = match.group("body")
        aim_match = re.search(r"^Aim:\s*(.+?)\s*$", body, re.MULTILINE)
        match_match = re.search(r"^Match:\s*(contains|exact)\s*$", body, re.MULTILINE)
        blocks = {
            block.group("title"): block.group("content")
            for block in BLOCK_PATTERN.finditer(body)
        }

        missing = [title for title in ("Inputs", "Expected output") if title not in blocks]
        if aim_match is None or missing:
            missing_parts = ", ".join(missing) or "Aim"
            raise ValueError(
                f"Test case '{match.group('name').strip()}' is missing: {missing_parts}."
            )

        commands = blocks["Inputs"].splitlines()
        if not commands or all(not command.strip() for command in commands):
            raise ValueError(f"Test case '{match.group('name').strip()}' has no inputs.")

        file_fixtures = {"Initial": [], "Expected": []}
        for file_block in FILE_BLOCK_PATTERN.finditer(body):
            path = Path(file_block.group("path").strip())
            if path.is_absolute() or ".." in path.parts:
                raise ValueError("File fixture paths must be relative and stay within the test directory.")
            file_fixtures[file_block.group("kind")].append(
                FileFixture(path, file_block.group("content"))
            )

        initial_directories = []
        for directory_match in DIRECTORY_PATTERN.finditer(body):
            path = Path(directory_match.group("path").strip())
            if path.is_absolute() or ".." in path.parts:
                raise ValueError("Directory fixture paths must be relative and stay within the test directory.")
            initial_directories.append(path)

        cases.append(
            TestCase(
                name=match.group("name").strip(),
                aim=aim_match.group(1).strip(),
                commands=commands,
                expected=blocks["Expected output"],
                match_mode=match_match.group(1) if match_match else "contains",
                initial_files=file_fixtures["Initial"],
                initial_directories=initial_directories,
                expected_files=file_fixtures["Expected"],
            )
        )

    if not cases:
        raise ValueError(f"No test cases found in {plan_path}.")
    return cases


def normalise_output(output: str) -> str:
    """Make line endings and the final newline deterministic for comparison."""
    output = output.replace("\r\n", "\n")
    return output.rstrip("\n") + "\n" if output else ""


def run_case(case: TestCase, classes_dir: Path, working_directory: Path) -> str:
    """Run one fresh Chausistant session and return its combined console output."""
    session_input = "\n".join(case.commands) + "\n"
    result = subprocess.run(
        ["java", "-cp", str(classes_dir), "chausistant.Chausistant"],
        cwd=working_directory,
        input=session_input,
        text=True,
        stdout=subprocess.PIPE,
        stderr=subprocess.STDOUT,
        timeout=30,
        check=False,
    )
    if result.returncode != 0:
        raise ProgramRunError(
            f"program exited with status {result.returncode}", result.stdout
        )
    return result.stdout


def expected_matches(case: TestCase, actual: str) -> bool:
    """Compare actual output using the case's declared matching mode."""
    actual = normalise_output(actual)
    expected = normalise_output(case.expected)
    if case.match_mode == "exact":
        return actual == expected
    return expected in actual


def write_initial_fixtures(case: TestCase, working_directory: Path) -> None:
    """Create the test case's declared initial files and directories."""
    for directory in case.initial_directories:
        (working_directory / directory).mkdir(parents=True, exist_ok=True)
    for fixture in case.initial_files:
        file_path = working_directory / fixture.path
        file_path.parent.mkdir(parents=True, exist_ok=True)
        file_path.write_text(normalise_output(fixture.content), encoding="utf-8")


def find_file_failures(case: TestCase, working_directory: Path) -> list[str]:
    """Return explanations for saved files that differ from their expected contents."""
    failures = []
    for fixture in case.expected_files:
        file_path = working_directory / fixture.path
        if not file_path.is_file():
            failures.append(f"Expected file was not created: {fixture.path}")
            continue

        actual = file_path.read_text(encoding="utf-8")
        if normalise_output(actual) != normalise_output(fixture.content):
            failures.append(
                f"Unexpected contents in {fixture.path}:\n"
                f"Expected:\n{normalise_output(fixture.content)}"
                f"Actual:\n{normalise_output(actual)}"
            )
    return failures


def print_transcript(case: TestCase, actual: str) -> None:
    """Print the console input and output for one test session."""
    print(f"\n=== {case.name} ===")
    print(f"Aim: {case.aim}")
    print("Console input:")
    print("\n".join(case.commands))
    print("Console output:")
    print(actual, end="" if actual.endswith("\n") else "\n")


def main() -> int:
    """Compile the project and run test cases until the first failure."""
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("plan", type=Path, help="Markdown UI test plan")
    args = parser.parse_args()

    project_root = Path.cwd()
    plan_path = args.plan.resolve()

    try:
        cases = parse_plan(plan_path)
    except (OSError, ValueError) as error:
        print(f"TEST PLAN ERROR: {error}", file=sys.stderr)
        return 2

    with tempfile.TemporaryDirectory(prefix="chausistant-ui-test-") as temp_dir:
        classes_dir = Path(temp_dir)
        source_files = sorted((project_root / "src/main/java").rglob("*.java"))
        if not source_files:
            print("TEST SESSION FAILED: no Java source files were found.")
            return 1
        compile_result = subprocess.run(
            [
                "javac",
                "--release",
                "25",
                "-d",
                str(classes_dir),
                *[str(source_file.relative_to(project_root)) for source_file in source_files],
            ],
            cwd=project_root,
            text=True,
            stdout=subprocess.PIPE,
            stderr=subprocess.STDOUT,
            check=False,
        )
        if compile_result.returncode != 0:
            print("TEST SESSION FAILED: compilation failed.")
            print(compile_result.stdout, end="")
            return 1

        for index, case in enumerate(cases, start=1):
            working_directory = classes_dir / f"case-{index}"
            working_directory.mkdir()
            try:
                write_initial_fixtures(case, working_directory)
                actual = run_case(case, classes_dir, working_directory)
            except ProgramRunError as error:
                actual = error.output
                print_transcript(case, actual)
                print(f"TEST SESSION FAILED: {error}")
                print("Expected output:")
                print(normalise_output(case.expected), end="")
                print("Actual output:")
                print(normalise_output(actual), end="")
                print("Testing terminated immediately; later cases were not run.")
                return 1
            except subprocess.TimeoutExpired as error:
                actual = error.stdout or ""
                print_transcript(case, actual)
                print("TEST SESSION FAILED: program timed out.")
                print("Expected output:")
                print(normalise_output(case.expected), end="")
                print("Actual output:")
                print(normalise_output(actual), end="")
                print("Testing terminated immediately; later cases were not run.")
                return 1
            except OSError as error:
                print(f"\n=== {case.name} ===")
                print(f"Aim: {case.aim}")
                print("Console input:")
                print("\n".join(case.commands))
                print(f"TEST SESSION FAILED: {error}")
                print("Expected output:")
                print(normalise_output(case.expected), end="")
                print("Actual output:")
                print("(no console output)")
                print("Testing terminated immediately; later cases were not run.")
                return 1

            print_transcript(case, actual)
            if not expected_matches(case, actual):
                expected = normalise_output(case.expected)
                actual_normalised = normalise_output(actual)
                print("Result: FAIL")
                print("Expected output:")
                print(expected, end="")
                print("Actual output:")
                print(actual_normalised, end="")
                print("Diff:")
                print(
                    "".join(
                        difflib.unified_diff(
                            expected.splitlines(keepends=True),
                            actual_normalised.splitlines(keepends=True),
                            fromfile="expected",
                            tofile="actual",
                        )
                    ),
                    end="",
                )
                print("Testing terminated immediately; later cases were not run.")
                return 1

            file_failures = find_file_failures(case, working_directory)
            if file_failures:
                print("Result: FAIL")
                print("File verification:")
                print("\n".join(file_failures))
                print("Testing terminated immediately; later cases were not run.")
                return 1

            print("Result: PASS")

    print("\nAll UI test cases passed.")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
