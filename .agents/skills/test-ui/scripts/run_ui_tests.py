#!/usr/bin/env python3
"""Compile CHOO and compare console sessions with a Markdown test plan."""

from __future__ import annotations

import subprocess
import sys
import tempfile
from dataclasses import dataclass
from pathlib import Path


@dataclass(frozen=True)
class TestCase:
    """One complete console session from the UI test plan."""

    name: str
    aim: str
    initial_data: str | None
    input_text: str
    expected_output: str
    expected_data: str | None


def read_fenced_block(lines: list[str], index: int, label: str) -> tuple[str, int]:
    """Read a text fenced block that follows a section label."""
    if index >= len(lines) or lines[index].strip() != label:
        raise ValueError(f"Expected '{label}'")
    index += 1
    if index >= len(lines) or lines[index].strip() != "```text":
        raise ValueError(f"Expected a text block after '{label}'")
    index += 1
    content: list[str] = []
    while index < len(lines) and lines[index].strip() != "```":
        content.append(lines[index])
        index += 1
    if index >= len(lines):
        raise ValueError(f"Unclosed text block after '{label}'")
    return "".join(content), index + 1


def parse_plan(plan_path: Path) -> list[TestCase]:
    """Parse ordered UI test cases from the Markdown plan."""
    lines = plan_path.read_text(encoding="utf-8").splitlines(keepends=True)
    cases: list[TestCase] = []
    index = 0

    while index < len(lines):
        if not lines[index].startswith("## Test case: "):
            index += 1
            continue

        name = lines[index].removeprefix("## Test case: ").strip()
        index += 1
        while index < len(lines) and not lines[index].strip():
            index += 1
        if index >= len(lines) or not lines[index].startswith("Aim: "):
            raise ValueError(f"Test case '{name}' has no Aim")
        aim = lines[index].removeprefix("Aim: ").strip()
        index += 1
        while index < len(lines) and not lines[index].strip():
            index += 1
        initial_data = None
        if index < len(lines) and lines[index].strip() == "Initial data:":
            initial_data, index = read_fenced_block(lines, index, "Initial data:")
            while index < len(lines) and not lines[index].strip():
                index += 1
        input_text, index = read_fenced_block(lines, index, "Input:")
        while index < len(lines) and not lines[index].strip():
            index += 1
        expected_output, index = read_fenced_block(lines, index, "Expected output:")
        while index < len(lines) and not lines[index].strip():
            index += 1
        expected_data = None
        if index < len(lines) and lines[index].strip() == "Expected data:":
            expected_data, index = read_fenced_block(lines, index, "Expected data:")
        cases.append(TestCase(
            name, aim, initial_data, input_text, expected_output, expected_data
        ))

    if not cases:
        raise ValueError("The UI test plan contains no test cases")
    return cases


def print_block(label: str, content: str) -> None:
    """Print a labeled transcript block without altering its content."""
    print(f"{label}:")
    print("```text")
    print(content, end="" if content.endswith("\n") else "\n")
    print("```")


def compile_application(repo_root: Path, classes_dir: Path) -> None:
    """Compile all production Java sources into a temporary directory."""
    sources = sorted((repo_root / "src/main/java").rglob("*.java"))
    if not sources:
        raise ValueError("No Java sources found under src/main/java")
    result = subprocess.run(
        ["javac", "-d", str(classes_dir), *(str(source) for source in sources)],
        cwd=repo_root,
        capture_output=True,
        text=True,
        check=False,
    )
    if result.returncode != 0:
        print("Compilation failed.", file=sys.stderr)
        print(result.stdout, end="", file=sys.stderr)
        print(result.stderr, end="", file=sys.stderr)
        raise SystemExit(result.returncode)


def run_case(repo_root: Path, classes_dir: Path, case: TestCase) -> bool:
    """Run one isolated console session and report whether it matches."""
    with tempfile.TemporaryDirectory(prefix="choo-ui-case-") as case_dir:
        working_directory = Path(case_dir)
        data_file = working_directory / "data" / "choo.txt"
        if case.initial_data is not None:
            data_file.parent.mkdir(parents=True)
            data_file.write_text(case.initial_data, encoding="utf-8")

        result = subprocess.run(
            ["java", "-cp", str(classes_dir), "choo.Choo"],
            cwd=working_directory,
            input=case.input_text,
            capture_output=True,
            text=True,
            timeout=10,
            check=False,
        )
        actual_data = data_file.read_text(encoding="utf-8") if data_file.exists() else None

        print(f"Test case: {case.name}")
        print(f"Aim: {case.aim}")
        if case.initial_data is not None:
            print_block("Initial data", case.initial_data)
        print_block("Console input", case.input_text)
        print_block("Console output", result.stdout)
        if case.expected_data is not None and actual_data is not None:
            print_block("Saved data", actual_data)

        output_matches = result.stdout == case.expected_output
        data_matches = case.expected_data is None or actual_data == case.expected_data
        if result.returncode == 0 and output_matches and data_matches:
            print("Result: PASS")
            return True

        print("Result: FAIL")
        if not output_matches:
            print_block("Expected output", case.expected_output)
            print_block("Actual output", result.stdout)
        if not data_matches:
            print_block("Expected data", case.expected_data or "")
            print_block("Actual data", actual_data or "<missing>\n")
        if result.stderr:
            print_block("Standard error", result.stderr)
        return False


def main() -> int:
    """Run all plan cases in order, stopping at the first failure."""
    repo_root = Path(__file__).resolve().parents[4]
    plan_path = Path(sys.argv[1]) if len(sys.argv) > 1 else repo_root / "test/ui-test-plan.md"
    if not plan_path.is_absolute():
        plan_path = repo_root / plan_path

    try:
        cases = parse_plan(plan_path)
        with tempfile.TemporaryDirectory(prefix="choo-ui-test-") as temp_dir:
            classes_dir = Path(temp_dir)
            compile_application(repo_root, classes_dir)
            for case in cases:
                if not run_case(repo_root, classes_dir, case):
                    return 1
    except (OSError, ValueError, subprocess.TimeoutExpired) as error:
        print(f"UI test error: {error}", file=sys.stderr)
        return 2

    print(f"All {len(cases)} UI test case(s) passed.")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
