#!/usr/bin/env python3
"""Compile and run console UI test cases described in a Markdown test plan."""

from __future__ import annotations

import re
import shutil
import subprocess
import sys
import tempfile
from pathlib import Path


ROOT = Path.cwd()
PLAN_PATH = ROOT / "test" / "ui-test-plan.md"
SOURCE_DIR = ROOT / "src" / "main" / "java"
MAIN_CLASS = "marcus.Marcus"
SAVE_FILE = Path("data") / "results.txt"
CASE_PATTERN = re.compile(
    r"^## Test Case: (?P<name>.+?)\n(?P<setup>.*?)^### Input\s*\n```text\n(?P<input>.*?)\n```"
    r"\s*\n.*?^### Expected Output\s*\n```text\n(?P<expected>.*?)\n```"
    r"(?:\n\n### Expected Saved Tasks\s*\n```text\n(?P<saved>.*?)\n```)?",
    re.MULTILINE | re.DOTALL,
)


def normalise(text: str) -> str:
    """Make line-ending differences irrelevant while retaining visible spacing."""
    return text.replace("\r\n", "\n").rstrip("\n")


def print_block(label: str, content: str) -> None:
    """Print a labelled console transcript block."""
    print(f"{label}:")
    print(content if content else "<empty>")


def load_cases() -> list[tuple[str, str, str, str | None, str | None]]:
    """Read named test cases from the project's Markdown test plan."""
    if not PLAN_PATH.is_file():
        raise FileNotFoundError(f"Test plan not found: {PLAN_PATH}")
    cases = []
    for match in CASE_PATTERN.finditer(PLAN_PATH.read_text()):
        initial_match = re.search(
            r"^### Initial Saved Tasks\s*\n```text\n(?P<initial>.*?)\n```",
            match.group("setup"),
            re.MULTILINE | re.DOTALL,
        )
        cases.append((
            match.group("name").strip(), match.group("input"), match.group("expected"), match.group("saved"),
            initial_match.group("initial") if initial_match else None,
        ))
    if not cases:
        raise ValueError("No test cases found. Follow the format in the test-ui skill.")
    return cases


def compile_program(output_dir: Path) -> None:
    """Compile all project Java source files into an isolated directory."""
    sources = sorted(SOURCE_DIR.rglob("*.java"))
    if not sources:
        raise FileNotFoundError(f"No Java files found under {SOURCE_DIR}")
    result = subprocess.run(
        ["javac", "-d", str(output_dir), *map(str, sources)],
        capture_output=True,
        text=True,
    )
    if result.returncode != 0:
        print_block("Compilation error", result.stderr)
        raise RuntimeError("Compilation failed")


def main() -> int:
    """Run all planned cases, stopping immediately after a failed comparison."""
    try:
        cases = load_cases()
    except (FileNotFoundError, ValueError) as error:
        print(error, file=sys.stderr)
        return 2

    build_dir = Path(tempfile.mkdtemp(prefix="ui-test-"))
    try:
        compile_program(build_dir)
        for number, (name, commands, expected, expected_saved_tasks, initial_saved_tasks) in enumerate(cases, start=1):
            case_dir = build_dir / f"case-{number}"
            case_dir.mkdir()
            if initial_saved_tasks is not None:
                saved_file = case_dir / SAVE_FILE
                saved_file.parent.mkdir()
                saved_file.write_text(initial_saved_tasks + "\n")
            result = subprocess.run(
                ["java", "-cp", str(build_dir), MAIN_CLASS],
                input=commands + "\n",
                capture_output=True,
                text=True,
                cwd=case_dir,
            )
            print(f"\nTest {number}: {name}")
            print_block("Console input", commands)
            print_block("Console output", result.stdout)

            actual = normalise(result.stdout)
            wanted = normalise(expected)
            if result.returncode != 0 or actual != wanted:
                print("RESULT: FAIL")
                print_block("Expected output", expected)
                print_block("Actual output", result.stdout)
                if result.stderr:
                    print_block("Program error", result.stderr)
                return 1

            if expected_saved_tasks is not None:
                saved_file = case_dir / SAVE_FILE
                actual_saved_tasks = saved_file.read_text() if saved_file.is_file() else ""
                print_block("Saved tasks file", actual_saved_tasks)
                if normalise(actual_saved_tasks) != normalise(expected_saved_tasks):
                    print("RESULT: FAIL")
                    print_block("Expected saved tasks", expected_saved_tasks)
                    print_block("Actual saved tasks", actual_saved_tasks)
                    return 1
            print("RESULT: PASS")
    except RuntimeError:
        return 1
    finally:
        shutil.rmtree(build_dir, ignore_errors=True)

    print(f"\nAll {len(cases)} UI test case(s) passed.")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
