#!/usr/bin/env python3
"""Compile Jaku and run exact-output UI cases from the Markdown test plan."""

from __future__ import annotations

import argparse
import re
import subprocess
import sys
import tempfile
from dataclasses import dataclass
from pathlib import Path


CASE_PATTERN = re.compile(
    r"^## (?P<name>[^\n]+)\n\n"
    r"Aim: (?P<aim>[^\n]+)\n\n"
    r"### Input\n\n```text\n(?P<input>.*?)\n```\n\n"
    r"### Expected output\n\n```text\n(?P<expected>.*?)\n```"
    r"(?:\n\n### Initial data\n\n```text\n(?P<initial_data>.*?)\n```)?"
    r"(?:\n\n### Expected saved data\n\n```text\n(?P<expected_data>.*?)\n```)?"
    r"(?:\n\n### Data path kind\n\n(?P<data_path_kind>file|directory))?"
    r"(?=\n\n## |\s*\Z)",
    re.MULTILINE | re.DOTALL,
)


@dataclass(frozen=True)
class TestCase:
    """One chatbot session and the exact console output it should produce."""

    name: str
    aim: str
    user_input: str
    expected_output: str
    initial_data: str | None
    expected_data: str | None
    data_path_kind: str | None


def find_project_root() -> Path:
    """Return the nearest parent directory containing Jaku's Java sources."""
    candidates = (Path.cwd(), *Path.cwd().parents, *Path(__file__).resolve().parents)
    for candidate in candidates:
        if (candidate / "src/main/java/jaku/Jaku.java").is_file():
            return candidate
    raise RuntimeError("Could not find a project root containing src/main/java/jaku/Jaku.java")


def parse_test_plan(plan_path: Path) -> list[TestCase]:
    """Parse test cases from the plan's documented Markdown structure."""
    plan_text = plan_path.read_text(encoding="utf-8")
    cases = [
        TestCase(
            match.group("name"),
            match.group("aim"),
            match.group("input"),
            match.group("expected"),
            match.group("initial_data"),
            match.group("expected_data"),
            match.group("data_path_kind"),
        )
        for match in CASE_PATTERN.finditer(plan_text)
    ]
    if not cases:
        raise ValueError(f"No test cases found in {plan_path}")
    return cases


def normalize_output(output: str) -> str:
    """Normalize platform line endings without hiding whitespace differences."""
    return output.replace("\r\n", "\n").rstrip("\n")


def decode_expected_output(output: str) -> str:
    """Restore visible markers used for trailing spaces in Markdown fixtures."""
    return normalize_output(output.replace("␠", " "))


def verify_java_25(project_root: Path) -> None:
    """Fail with a useful message unless javac reports Java 25."""
    result = subprocess.run(
        ["javac", "-version"],
        cwd=project_root,
        capture_output=True,
        text=True,
        check=False,
    )
    version_output = (result.stdout + result.stderr).strip()
    if result.returncode != 0 or not version_output.startswith("javac 25"):
        raise RuntimeError(f"Java 25 is required; found: {version_output or 'javac unavailable'}")


def compile_sources(project_root: Path, classes_directory: Path) -> None:
    """Compile production sources through Gradle, including declared JavaFX dependencies."""
    result = subprocess.run(
        ["./gradlew", "classes", "--no-daemon"],
        cwd=project_root,
        capture_output=True,
        text=True,
        check=False,
    )
    if result.returncode != 0:
        details = (result.stdout + result.stderr).strip()
        raise RuntimeError(f"Gradle compilation failed:\n{details}")


def run_case(project_root: Path, classes_directory: Path, data_file: Path, case: TestCase) -> str:
    """Run one fresh Jaku session and return its standard output."""
    result = subprocess.run(
        ["java", f"-Djaku.dataFile={data_file}", "-cp", str(classes_directory), "jaku.Jaku"],
        cwd=project_root,
        input=case.user_input + "\n",
        capture_output=True,
        text=True,
        timeout=10,
        check=False,
    )
    if result.returncode != 0:
        raise RuntimeError(
            f"{case.name} exited with status {result.returncode}:\n{result.stderr.strip()}"
        )
    if result.stderr:
        raise RuntimeError(f"{case.name} wrote to standard error:\n{result.stderr.rstrip()}")
    return normalize_output(result.stdout)


def print_transcript(case: TestCase, actual_output: str) -> None:
    """Print the commands and application output for a completed test case."""
    print(f"[{case.name}] {case.aim}")
    print("Input:")
    print(case.user_input)
    print("Output:")
    print(actual_output)


def prepare_data_file(data_file: Path, case: TestCase) -> None:
    """Create the optional initial saved-task fixture for one test case."""
    if case.data_path_kind == "directory":
        data_file.mkdir(parents=True)
    elif case.initial_data is not None:
        data_file.parent.mkdir(parents=True)
        data_file.write_text(case.initial_data + "\n", encoding="utf-8")


def verify_saved_data(data_file: Path, case: TestCase) -> None:
    """Compare saved task data when the test case specifies an expectation."""
    if case.expected_data is None:
        return
    actual_data = normalize_output(data_file.read_text(encoding="utf-8"))
    expected_data = normalize_output(case.expected_data)
    if actual_data != expected_data:
        raise RuntimeError(
            f"{case.name} saved data did not match.\nExpected:\n{expected_data}\n"
            f"Actual:\n{actual_data}"
        )


def main() -> int:
    """Run all planned UI cases, stopping immediately on the first failure."""
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument(
        "--plan",
        type=Path,
        default=Path("test/ui-test-plan.md"),
        help="test plan path relative to the project root",
    )
    args = parser.parse_args()

    try:
        project_root = find_project_root()
        plan_path = args.plan if args.plan.is_absolute() else project_root / args.plan
        test_cases = parse_test_plan(plan_path)
        verify_java_25(project_root)
        with tempfile.TemporaryDirectory(prefix="jaku-ui-tests-") as temp_directory:
            classes_directory = project_root / "build/classes/java/main"
            compile_sources(project_root, classes_directory)
            for case_number, case in enumerate(test_cases, start=1):
                data_file = Path(temp_directory) / f"case-{case_number}" / "jaku.txt"
                prepare_data_file(data_file, case)
                actual_output = run_case(project_root, classes_directory, data_file, case)
                print_transcript(case, actual_output)
                expected_output = decode_expected_output(case.expected_output)
                if actual_output != expected_output:
                    print("Expected:", file=sys.stderr)
                    print(expected_output, file=sys.stderr)
                    print("Actual:", file=sys.stderr)
                    print(actual_output, file=sys.stderr)
                    print(f"FAIL: {case.name}", file=sys.stderr)
                    return 1
                verify_saved_data(data_file, case)
                print(f"PASS: {case.name}\n")
    except (OSError, RuntimeError, ValueError, subprocess.TimeoutExpired) as error:
        print(f"ERROR: {error}", file=sys.stderr)
        return 1

    print(f"PASS: {len(test_cases)} UI test case(s)")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
