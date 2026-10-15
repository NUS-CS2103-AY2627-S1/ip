#!/usr/bin/env python3
"""Run fail-fast console UI tests recorded in test/ui-test-plan.md."""

from __future__ import annotations

import argparse
import difflib
import re
import subprocess
import sys
import tempfile
from dataclasses import dataclass
from pathlib import Path


CASE_HEADING = re.compile(r"^##\s+(?P<case_id>[^:\s]+)\s*:\s*(?P<title>.+)$", re.MULTILINE)


@dataclass(frozen=True)
class TestCase:
    """A console UI test parsed from the Markdown test plan."""

    case_id: str
    title: str
    aim: str
    commands: tuple[str, ...]
    expected_output: str


def parse_arguments() -> argparse.Namespace:
    """Parse command-line arguments for the UI test runner."""
    project_root = Path(__file__).resolve().parents[4]
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--project-root", type=Path, default=project_root)
    parser.add_argument("--plan", type=Path, default=Path("test/ui-test-plan.md"))
    return parser.parse_args()


def extract_text_block(section: str, heading: str, case_id: str) -> str:
    """Extract a text code block beneath a level-three heading."""
    pattern = re.compile(
        rf"^###\s+{re.escape(heading)}\s*$\s*^```text\s*$\n(.*?)\n^```\s*$",
        re.MULTILINE | re.DOTALL,
    )
    match = pattern.search(section)
    if match is None:
        raise ValueError(f"{case_id} is missing the '{heading}' text block.")
    return match.group(1)


def parse_test_plan(plan_path: Path) -> list[TestCase]:
    """Parse all test cases from the Markdown test plan."""
    content = plan_path.read_text(encoding="utf-8")
    headings = list(CASE_HEADING.finditer(content))
    if not headings:
        raise ValueError("The test plan contains no test cases.")

    cases = []
    seen_ids = set()
    for index, heading in enumerate(headings):
        section_end = headings[index + 1].start() if index + 1 < len(headings) else len(content)
        section = content[heading.end():section_end]
        case_id = heading.group("case_id")
        if case_id in seen_ids:
            raise ValueError(f"Duplicate test case ID: {case_id}")
        seen_ids.add(case_id)

        aim_match = re.search(r"^Aim:\s*(.+)$", section, re.MULTILINE)
        if aim_match is None:
            raise ValueError(f"{case_id} is missing its aim.")

        input_block = extract_text_block(section, "Input", case_id)
        commands = tuple(input_block.splitlines())
        if not commands or any(not command.strip() for command in commands):
            raise ValueError(f"{case_id} must contain only non-empty input commands.")

        expected_output = extract_text_block(section, "Expected output", case_id)
        cases.append(TestCase(
            case_id=case_id,
            title=heading.group("title").strip(),
            aim=aim_match.group(1).strip(),
            commands=commands,
            expected_output=expected_output,
        ))
    return cases


def require_java_25(project_root: Path) -> None:
    """Fail unless the active Java compiler is Java 25."""
    result = subprocess.run(
        ["javac", "-version"],
        cwd=project_root,
        text=True,
        stdout=subprocess.PIPE,
        stderr=subprocess.STDOUT,
        check=False,
    )
    version = result.stdout.strip()
    if result.returncode != 0 or not version.startswith("javac 25"):
        raise RuntimeError(f"Java 25 is required; active compiler reports: {version or 'unavailable'}")


def compile_project(project_root: Path, classes_dir: Path) -> Path:
    """Compile all production Java sources with the project's Gradle dependencies."""
    sources = sorted((project_root / "src/main/java").rglob("*.java"))
    if not sources:
        raise RuntimeError("No Java source files were found under src/main/java.")
    result = subprocess.run(
        [str(project_root / "gradlew"), "classes"],
        cwd=project_root,
        text=True,
        stdout=subprocess.PIPE,
        stderr=subprocess.STDOUT,
        check=False,
    )
    if result.returncode != 0:
        raise RuntimeError("Compilation failed:\n" + result.stdout.rstrip())
    return project_root / "build/classes/java/main"


def normalize_output(output: str) -> str:
    """Normalize platform line endings and ignore only final newlines."""
    return output.replace("\r\n", "\n").replace("\r", "\n").rstrip("\n")


def run_test_case(project_root: Path, classes_dir: Path, test_case: TestCase) -> str:
    """Run one test case in a fresh Avon process and return its console output."""
    console_input = "\n".join(test_case.commands) + "\n"
    with tempfile.TemporaryDirectory(prefix="avon-ui-case-") as case_directory:
        result = subprocess.run(
            ["java", "-cp", str(classes_dir), "avon.Avon"],
            cwd=case_directory,
            input=console_input,
            text=True,
            stdout=subprocess.PIPE,
            stderr=subprocess.STDOUT,
            check=False,
        )
    if result.returncode != 0:
        return result.stdout + f"\n[process exited with status {result.returncode}]"
    return result.stdout


def print_transcript(test_case: TestCase, actual_output: str) -> None:
    """Print the commands and output exactly as observed for one session."""
    print(f"\n=== {test_case.case_id}: {test_case.title} ===")
    print(f"Aim: {test_case.aim}")
    print("Console input:")
    for command in test_case.commands:
        print(f"> {command}")
    print("Console output:")
    print(normalize_output(actual_output))


def report_failure(test_case: TestCase, actual_output: str) -> None:
    """Report expected and actual output plus a unified diff."""
    expected = normalize_output(test_case.expected_output)
    actual = normalize_output(actual_output)
    print(f"\nFAILED: {test_case.case_id}")
    print("Expected output:")
    print(expected)
    print("Actual output:")
    print(actual)
    print("Difference (expected -> actual):")
    print("\n".join(difflib.unified_diff(
        expected.splitlines(),
        actual.splitlines(),
        fromfile="expected",
        tofile="actual",
        lineterm="",
    )))


def main() -> int:
    """Compile Avon and run all planned UI tests, stopping on first failure."""
    arguments = parse_arguments()
    project_root = arguments.project_root.resolve()
    plan_path = arguments.plan
    if not plan_path.is_absolute():
        plan_path = project_root / plan_path

    try:
        test_cases = parse_test_plan(plan_path)
        require_java_25(project_root)
        with tempfile.TemporaryDirectory(prefix="avon-ui-test-") as temporary_directory:
            classes_dir = Path(temporary_directory)
            classes_dir = compile_project(project_root, classes_dir)
            for test_case in test_cases:
                actual_output = run_test_case(project_root, classes_dir, test_case)
                print_transcript(test_case, actual_output)
                if normalize_output(actual_output) != normalize_output(test_case.expected_output):
                    report_failure(test_case, actual_output)
                    return 1
    except (OSError, RuntimeError, ValueError) as error:
        print(f"UI test session could not run: {error}", file=sys.stderr)
        return 2

    print(f"\nPASS: {len(test_cases)} UI test case(s).")
    return 0


if __name__ == "__main__":
    sys.exit(main())
