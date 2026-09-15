#!/usr/bin/env python3
"""Run LuigiBot console test cases defined in a Markdown test plan."""

from __future__ import annotations

import re
import subprocess
import sys
import tempfile
from dataclasses import dataclass
from pathlib import Path


@dataclass(frozen=True)
class TestCase:
    """One isolated console session and its exact expected output."""

    name: str
    aim: str
    input_text: str
    expected_output: str
    initial_saved_data: str | None
    initial_save_path: str | None
    expected_saved_data: str | None


def extract_block(body: str, heading: str, case_name: str) -> str:
    """Extract one text-fenced block following the requested heading."""
    pattern = re.compile(
        rf"^### {re.escape(heading)}\s*\n\s*```text\n(.*?)\n```",
        re.MULTILINE | re.DOTALL,
    )
    match = pattern.search(body)
    if match is None:
        raise ValueError(f"Test case '{case_name}' has no {heading!r} text block")
    return match.group(1)


def extract_optional_block(body: str, heading: str) -> str | None:
    """Extract an optional text-fenced block following the requested heading."""
    pattern = re.compile(
        rf"^### {re.escape(heading)}\s*\n\s*```text\n(.*?)\n```",
        re.MULTILINE | re.DOTALL,
    )
    match = pattern.search(body)
    return None if match is None else match.group(1)


def parse_test_plan(plan_path: Path) -> list[TestCase]:
    """Parse test cases from the project's Markdown UI test plan."""
    content = plan_path.read_text(encoding="utf-8").replace("\r\n", "\n")
    case_pattern = re.compile(
        r"^## Test case: (?P<name>[^\n]+)\n(?P<body>.*?)(?=^## Test case: |\Z)",
        re.MULTILINE | re.DOTALL,
    )
    cases: list[TestCase] = []

    for match in case_pattern.finditer(content):
        name = match.group("name").strip()
        body = match.group("body")
        aim_match = re.search(r"^\*\*Aim:\*\*\s*(.+)$", body, re.MULTILINE)
        if aim_match is None:
            raise ValueError(f"Test case '{name}' has no aim")
        cases.append(
            TestCase(
                name=name,
                aim=aim_match.group(1).strip(),
                input_text=extract_block(body, "Input", name),
                expected_output=extract_block(body, "Expected output", name),
                initial_saved_data=extract_optional_block(body, "Initial saved data"),
                initial_save_path=extract_optional_block(body, "Initial save path"),
                expected_saved_data=extract_optional_block(body, "Expected saved data"),
            )
        )

    if not cases:
        raise ValueError(f"No test cases found in {plan_path}")
    return cases


def compile_program(repo_root: Path) -> Path:
    """Compile the project with Gradle and return its main classes directory."""
    gradle_wrapper = repo_root / ("gradlew.bat" if sys.platform == "win32" else "gradlew")
    result = subprocess.run(
        [str(gradle_wrapper), "classes"],
        cwd=repo_root,
        capture_output=True,
        text=True,
        check=False,
    )
    if result.returncode != 0:
        raise RuntimeError(f"Compilation failed:\n{result.stdout}{result.stderr}")
    classes_dir = repo_root / "build" / "classes" / "java" / "main"
    if not classes_dir.is_dir():
        raise RuntimeError(f"Gradle did not create the classes directory: {classes_dir}")
    return classes_dir


def run_case(working_dir: Path, classes_dir: Path, case: TestCase) -> str:
    """Run one fresh LuigiBot process and return normalized console output."""
    session_input = case.input_text + "\n"
    result = subprocess.run(
        ["java", "-cp", str(classes_dir), "luigibot.LuigiBot"],
        cwd=working_dir,
        input=session_input,
        capture_output=True,
        text=True,
        timeout=15,
        check=False,
    )
    actual = (result.stdout + result.stderr).replace("\r\n", "\n").rstrip("\n")
    if result.returncode != 0:
        actual += f"\n[process exited with code {result.returncode}]"
    return actual


def read_saved_data(working_dir: Path) -> str | None:
    """Return normalized LuigiBot save data, or None when no file exists."""
    save_path = working_dir / "data" / "luigibot.txt"
    if not save_path.exists():
        return None
    return save_path.read_text(encoding="utf-8").replace("\r\n", "\n").rstrip("\n")


def write_initial_saved_data(working_dir: Path, saved_data: str) -> None:
    """Create a LuigiBot save file for a persistence test case."""
    save_path = working_dir / "data" / "luigibot.txt"
    save_path.parent.mkdir()
    save_path.write_text(saved_data + "\n", encoding="utf-8")


def create_initial_save_path(working_dir: Path, path_type: str) -> None:
    """Create a special save path used to exercise filesystem failures."""
    if path_type != "directory":
        raise ValueError(f"Unsupported initial save path type: {path_type}")
    save_path = working_dir / "data" / "luigibot.txt"
    save_path.mkdir(parents=True)


def print_transcript(case: TestCase, actual: str) -> None:
    """Print a readable record of console input and output."""
    print(f"\n=== {case.name} ===")
    print(f"Aim: {case.aim}")
    print("--- Console input ---")
    for command in case.input_text.splitlines():
        print(f"> {command}")
    print("--- Console output ---")
    print(actual)


def main() -> int:
    """Compile LuigiBot and execute test cases, stopping at first failure."""
    repo_root = Path(__file__).resolve().parents[4]
    plan_path = (
        Path(sys.argv[1]).resolve()
        if len(sys.argv) > 1
        else repo_root / "test" / "ui-test-plan.md"
    )

    try:
        cases = parse_test_plan(plan_path)
        with tempfile.TemporaryDirectory(prefix="luigibot-ui-") as temp_dir:
            classes_dir = compile_program(repo_root)
            print(f"Running {len(cases)} UI test case(s) from {plan_path}")
            for index, case in enumerate(cases, start=1):
                working_dir = Path(temp_dir) / f"case-{index}"
                working_dir.mkdir()
                if case.initial_saved_data is not None:
                    write_initial_saved_data(working_dir, case.initial_saved_data)
                if case.initial_save_path is not None:
                    create_initial_save_path(working_dir, case.initial_save_path)
                actual = run_case(working_dir, classes_dir, case)
                print_transcript(case, actual)
                if actual != case.expected_output:
                    print(f"\nFAIL: test case {index} - {case.name}")
                    print("--- Expected output ---")
                    print(case.expected_output)
                    print("--- Actual output ---")
                    print(actual)
                    print("Testing terminated; later test cases were not run.")
                    return 1
                if case.expected_saved_data is not None:
                    actual_saved_data = read_saved_data(working_dir)
                    print("--- Saved data ---")
                    print("[file missing]" if actual_saved_data is None else actual_saved_data)
                    if actual_saved_data != case.expected_saved_data:
                        print(f"\nFAIL: test case {index} - {case.name}")
                        print("--- Expected saved data ---")
                        print(case.expected_saved_data)
                        print("--- Actual saved data ---")
                        print("[file missing]" if actual_saved_data is None else actual_saved_data)
                        print("Testing terminated; later test cases were not run.")
                        return 1
                print(f"PASS: test case {index} - {case.name}")
    except (OSError, RuntimeError, ValueError, subprocess.TimeoutExpired) as error:
        print(f"ERROR: {error}", file=sys.stderr)
        return 1

    print(f"\nPASS: all {len(cases)} UI test case(s) passed.")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
