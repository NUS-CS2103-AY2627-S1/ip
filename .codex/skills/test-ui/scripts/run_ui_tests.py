#!/usr/bin/env python3
"""Runs console UI test cases recorded in test/ui-test-plan.md."""

from __future__ import annotations

import argparse
import re
import subprocess
import sys
import tempfile
from dataclasses import dataclass
from pathlib import Path


@dataclass(frozen=True)
class TestCase:
    """Stores one console test session from the UI test plan."""

    name: str
    aim: str
    inputs: str
    expected_output: str
    initial_data: str | None


def parse_args() -> argparse.Namespace:
    """Returns command-line arguments for the test runner."""
    parser = argparse.ArgumentParser(
        description="Run UI tests from test/ui-test-plan.md."
    )
    parser.add_argument(
        "--repo",
        type=Path,
        default=Path.cwd(),
        help="repository root (default: current directory)",
    )
    parser.add_argument(
        "--plan",
        type=Path,
        help="test plan path (default: <repo>/test/ui-test-plan.md)",
    )
    parser.add_argument(
        "--timeout",
        type=float,
        default=10.0,
        help="seconds allowed for each test case (default: 10)",
    )
    return parser.parse_args()


def extract_section(body: str, heading: str) -> str:
    """Returns the Markdown content under a level-three heading."""
    match = re.search(
        rf"^### {re.escape(heading)}\s*$\n(.*?)(?=^### |\Z)",
        body,
        flags=re.MULTILINE | re.DOTALL | re.IGNORECASE,
    )
    if not match:
        raise ValueError(f"missing '### {heading}' section")
    return match.group(1).strip()


def extract_optional_section(body: str, heading: str) -> str | None:
    """Returns an optional Markdown level-three section."""
    match = re.search(
        rf"^### {re.escape(heading)}\s*$\n(.*?)(?=^### |\Z)",
        body,
        flags=re.MULTILINE | re.DOTALL | re.IGNORECASE,
    )
    return match.group(1).strip() if match else None


def extract_text_block(section: str, heading: str) -> str:
    """Returns the contents of the first fenced text block in a section."""
    match = re.search(r"```(?:text)?[ \t]*\n(.*?)\n```", section, re.DOTALL)
    if not match:
        raise ValueError(f"'{heading}' must contain a fenced text block")
    return match.group(1)


def load_test_cases(plan_path: Path) -> list[TestCase]:
    """Returns all test cases parsed from the specified Markdown plan."""
    plan = plan_path.read_text(encoding="utf-8")
    matches = list(
        re.finditer(
            r"^## Test Case:\s*(.+?)\s*$\n(.*?)(?=^## Test Case:|\Z)",
            plan,
            flags=re.MULTILINE | re.DOTALL,
        )
    )
    if not matches:
        raise ValueError("the test plan does not contain any test cases")

    test_cases = []
    for match in matches:
        name = match.group(1).strip()
        body = match.group(2)
        try:
            aim = extract_section(body, "Aim")
            inputs = extract_text_block(extract_section(body, "Inputs"), "Inputs")
            expected = extract_text_block(
                extract_section(body, "Expected Output"), "Expected Output"
            )
            initial_data_section = extract_optional_section(body, "Initial Data")
            initial_data = (
                extract_text_block(initial_data_section, "Initial Data")
                if initial_data_section is not None
                else None
            )
        except ValueError as error:
            raise ValueError(f"test case '{name}': {error}") from error
        test_cases.append(TestCase(name, aim, inputs, expected, initial_data))
    return test_cases


def normalize_output(output: str) -> str:
    """Returns output normalized for cross-platform console comparison."""
    normalized = output.replace("\r\n", "\n").replace("\r", "\n")
    normalized_lines = [line.rstrip(" \t") for line in normalized.split("\n")]
    while normalized_lines and normalized_lines[-1] == "":
        normalized_lines.pop()
    return "\n".join(normalized_lines)


def require_java_25() -> None:
    """Raises an error unless javac and java both report major version 25."""
    for executable in ("javac", "java"):
        result = subprocess.run(
            [executable, "-version"],
            check=False,
            capture_output=True,
            text=True,
        )
        version_text = f"{result.stdout}\n{result.stderr}".strip()
        if result.returncode != 0 or not re.search(r'\b(?:javac |version ")25(?:\.|\b)', version_text):
            raise RuntimeError(
                f"{executable} must use Java 25; reported: {version_text or 'unknown'}"
            )


def compile_program(repo: Path, output_dir: Path) -> None:
    """Compiles the Java sources needed by the console application."""
    source_root = repo / "src" / "main" / "java"
    javafx_sources = {
        source_root / "yuno" / "ui" / "DialogBox.java",
        source_root / "yuno" / "ui" / "Launcher.java",
        source_root / "yuno" / "ui" / "Main.java",
        source_root / "yuno" / "ui" / "MainWindow.java",
    }
    sources = sorted(path for path in source_root.rglob("*.java") if path not in javafx_sources)
    if not sources:
        raise RuntimeError(f"no Java sources found under {source_root}")
    result = subprocess.run(
        ["javac", "-d", str(output_dir), *map(str, sources)],
        check=False,
        capture_output=True,
        text=True,
    )
    if result.returncode != 0:
        raise RuntimeError(f"compilation failed:\n{result.stdout}{result.stderr}")


def print_transcript(test_case: TestCase, actual_output: str) -> None:
    """Prints the input and output record for an attempted test case."""
    print(f"=== {test_case.name} ===")
    print(f"Aim: {test_case.aim}")
    print("--- Console input ---")
    for command in test_case.inputs.splitlines():
        print(f"> {command}")
    print("--- Console output ---")
    print(actual_output)


def run_test_case(
    test_case: TestCase,
    class_directory: Path,
    working_directory: Path,
    timeout: float,
) -> tuple[bool, str, str]:
    """Runs one test case and returns its result, output, and error text."""
    if test_case.initial_data is not None:
        data_directory = working_directory / "data"
        data_directory.mkdir()
        (data_directory / "yuno.txt").write_text(
            f"{test_case.initial_data}\n", encoding="utf-8"
        )

    console_input = f"{test_case.inputs}\n"
    try:
        result = subprocess.run(
            ["java", "-cp", str(class_directory), "yuno.Yuno"],
            input=console_input,
            check=False,
            capture_output=True,
            text=True,
            timeout=timeout,
            cwd=working_directory,
        )
    except subprocess.TimeoutExpired as error:
        actual = error.stdout or ""
        if isinstance(actual, bytes):
            actual = actual.decode(errors="replace")
        return False, actual, f"process exceeded the {timeout:g}-second timeout"

    error_text = result.stderr.strip()
    passed = (
        result.returncode == 0
        and not error_text
        and normalize_output(result.stdout)
        == normalize_output(test_case.expected_output)
    )
    if result.returncode != 0:
        error_text = f"process exited with code {result.returncode}\n{error_text}".strip()
    return passed, result.stdout, error_text


def main() -> int:
    """Runs the test plan and stops immediately after the first failure."""
    args = parse_args()
    repo = args.repo.resolve()
    plan_path = (args.plan or repo / "test" / "ui-test-plan.md").resolve()

    try:
        test_cases = load_test_cases(plan_path)
        require_java_25()
        with tempfile.TemporaryDirectory(prefix="yuno-ui-test-") as temp_dir:
            temporary_root = Path(temp_dir)
            class_directory = temporary_root / "classes"
            class_directory.mkdir()
            compile_program(repo, class_directory)

            for number, test_case in enumerate(test_cases, start=1):
                working_directory = temporary_root / f"case-{number}"
                working_directory.mkdir()
                passed, actual_output, error_text = run_test_case(
                    test_case,
                    class_directory,
                    working_directory,
                    args.timeout,
                )
                print_transcript(test_case, actual_output)
                if not passed:
                    print("--- Expected output ---")
                    print(test_case.expected_output)
                    print("--- Actual output ---")
                    print(actual_output)
                    if error_text:
                        print("--- Process error ---")
                        print(error_text)
                    print(f"FAIL: test case {number} of {len(test_cases)}")
                    return 1
                print("PASS\n")
    except (OSError, RuntimeError, ValueError) as error:
        print(f"ERROR: {error}", file=sys.stderr)
        return 2

    print(f"PASS: all {len(test_cases)} test cases passed")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
