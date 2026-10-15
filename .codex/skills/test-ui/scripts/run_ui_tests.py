#!/usr/bin/env python3
"""Run the console UI test cases recorded in a Markdown test plan."""

from __future__ import annotations

import argparse
import re
import shutil
import subprocess
import sys
import tempfile
from pathlib import Path

ROOT = Path(__file__).resolve().parents[4]
DEFAULT_PLAN = ROOT / "test" / "ui-test-plan.md"
BUILD_DIRECTORY = ROOT / "_temp" / "ui-test-classes"
DATA_FILE = ROOT / "data" / "CBT.txt"


def normalize(text: str) -> str:
    """Make platform line endings comparable while preserving all content."""
    return text.replace("\r\n", "\n").replace("\r", "\n")


def parse_test_cases(plan: Path) -> list[tuple[str, str, str, str, str]]:
    """Return each case's name, console IO, and optional data-file expectations."""
    content = plan.read_text(encoding="utf-8")
    sections = re.split(r"(?m)^## Test case: (.+)$", content)
    if len(sections) == 1:
        raise ValueError("No test cases found; use headings such as '## Test case: Add task'.")
    cases = []
    pattern = r"(?ms)^### (Inputs|Expected output|Setup data|Expected saved data)\s*\n```text\n(.*?)\n```\s*$"
    for index in range(1, len(sections), 2):
        name, section = sections[index].strip(), sections[index + 1]
        blocks = {kind: body for kind, body in re.findall(pattern, section)}
        missing = {"Inputs", "Expected output"} - blocks.keys()
        if missing:
            raise ValueError(f"{name}: missing {', '.join(sorted(missing))} text block(s).")
        cases.append((name, normalize(blocks["Inputs"]), normalize(blocks["Expected output"]) + "\n",
                      normalize(blocks.get("Setup data", "")),
                      normalize(blocks.get("Expected saved data", ""))))
    return cases


def compile_program() -> None:
    """Compile console sources into an isolated temporary test-class directory."""
    source_root = ROOT / "src" / "main" / "java"
    source_files = sorted(path for path in source_root.rglob("*.java")
                          if "javafx" not in path.relative_to(source_root).parts)
    if not source_files:
        raise RuntimeError("No Java source files found in src/main/java.")
    shutil.rmtree(BUILD_DIRECTORY, ignore_errors=True)
    BUILD_DIRECTORY.mkdir(parents=True)
    result = subprocess.run(["javac", "-d", str(BUILD_DIRECTORY), *map(str, source_files)], cwd=ROOT,
                            text=True, capture_output=True, check=False)
    if result.returncode:
        raise RuntimeError(f"Compilation failed:\n{result.stdout}{result.stderr}")


def run_case(inputs: str) -> str:
    """Run one clean application session and return its console output."""
    result = subprocess.run(["java", "-cp", str(BUILD_DIRECTORY), "cbt.Cbt"], input=inputs + "\n", cwd=ROOT,
                            text=True, capture_output=True, check=False)
    if result.returncode:
        raise RuntimeError(f"Program exited with code {result.returncode}:\n{result.stderr}")
    return normalize(result.stdout)


def prepare_data_file(contents: str) -> None:
    """Give a test case an isolated starting data file."""
    if DATA_FILE.exists():
        DATA_FILE.unlink()
    if contents:
        DATA_FILE.parent.mkdir(parents=True, exist_ok=True)
        DATA_FILE.write_text(contents + "\n", encoding="utf-8")


def show_block(label: str, value: str) -> None:
    """Print a clearly delimited transcript block."""
    print(f"--- {label} ---")
    print(value, end="" if value.endswith("\n") else "\n")


def main() -> int:
    """Compile the program and run every documented UI test until one fails."""
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--plan", type=Path, default=DEFAULT_PLAN)
    plan = parser.parse_args().plan.resolve()
    try:
        cases = parse_test_cases(plan)
        compile_program()
    except (OSError, RuntimeError, ValueError) as error:
        print(f"TEST SETUP FAILED: {error}", file=sys.stderr)
        return 2
    with tempfile.TemporaryDirectory() as temporary_directory:
        backup_path = Path(temporary_directory) / "CBT.txt"
        had_data_file = DATA_FILE.exists()
        if had_data_file:
            shutil.copy2(DATA_FILE, backup_path)
        try:
            for position, (name, inputs, expected, setup_data, expected_data) in enumerate(cases, start=1):
                print(f"\n=== Test {position}: {name} ===")
                prepare_data_file(setup_data)
                show_block("Console input", inputs)
                try:
                    actual = run_case(inputs)
                except RuntimeError as error:
                    print(f"TEST FAILED: {error}", file=sys.stderr)
                    return 1
                show_block("Console output", actual)
                if actual != expected:
                    show_block("EXPECTED OUTPUT", expected)
                    show_block("ACTUAL OUTPUT", actual)
                    print(f"TEST FAILED: {name}", file=sys.stderr)
                    return 1
                if expected_data:
                    actual_data = normalize(DATA_FILE.read_text(encoding="utf-8")) if DATA_FILE.exists() else ""
                    expected_data += "\n"
                    if actual_data != expected_data:
                        show_block("EXPECTED SAVED DATA", expected_data)
                        show_block("ACTUAL SAVED DATA", actual_data)
                        print(f"TEST FAILED: {name}", file=sys.stderr)
                        return 1
                print("RESULT: PASS")
        finally:
            if DATA_FILE.exists():
                DATA_FILE.unlink()
            if had_data_file:
                DATA_FILE.parent.mkdir(parents=True, exist_ok=True)
                shutil.copy2(backup_path, DATA_FILE)
    print(f"\nAll {len(cases)} UI test(s) passed.")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
