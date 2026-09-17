#!/usr/bin/env python3
"""Compile the chatbot and run the console sessions documented in the jonathan.ui.UI test plan."""

from __future__ import annotations

import re
import shutil
import subprocess
import sys
import tempfile
from pathlib import Path


PROJECT_ROOT = Path(__file__).resolve().parents[4]
PLAN_PATH = PROJECT_ROOT / "test" / "ui-test-plan.md"
SOURCE_PATH = PROJECT_ROOT / "src" / "main" / "java"
SAVE_PATH = PROJECT_ROOT / "data" / "jonathan.txt"


def extract_fenced_block(section: str, heading: str) -> str:
    """Return the content of the fenced block directly below a level-three heading."""
    pattern = rf"^### {re.escape(heading)}\n```(?:text)?\n(.*?)\n```"
    match = re.search(pattern, section, flags=re.MULTILINE | re.DOTALL)
    if match is None:
        raise ValueError(f"Missing a fenced block headed '### {heading}'.")
    return match.group(1) + "\n"


def extract_optional_fenced_block(section: str, heading: str) -> str | None:
    """Return an optional fenced block, or None when the test does not define one."""
    pattern = rf"^### {re.escape(heading)}\n```(?:text)?\n(.*?)\n```"
    match = re.search(pattern, section, flags=re.MULTILINE | re.DOTALL)
    return None if match is None else match.group(1) + "\n"


def load_cases() -> list[tuple[str, str, str, str, str | None]]:
    """Load test inputs and expected output/data from the Markdown test plan."""
    plan = PLAN_PATH.read_text(encoding="utf-8")
    sections = re.split(r"^## Test: ", plan, flags=re.MULTILINE)[1:]
    if not sections:
        raise ValueError("The test plan contains no sections headed '## Test:'.")

    cases = []
    for section in sections:
        name, body = section.split("\n", 1)
        aim_match = re.search(r"^### Aim\n(.+)$", body, flags=re.MULTILINE)
        if aim_match is None:
            raise ValueError(f"Test '{name}' has no one-line aim.")
        cases.append((
            name,
            aim_match.group(1),
            extract_fenced_block(body, "Input"),
            extract_fenced_block(body, "Expected output"),
            extract_optional_fenced_block(body, "Expected saved data"),
        ))
    return cases


def compile_program(output_dir: Path) -> None:
    """Compile all Java source files into the supplied temporary directory."""
    sources = sorted(SOURCE_PATH.rglob("*.java"))
    result = subprocess.run(
        ["javac", "-d", str(output_dir), *map(str, sources)],
        text=True,
        capture_output=True,
        check=False,
    )
    if result.returncode != 0:
        print("Compilation failed:\n" + result.stderr, file=sys.stderr)
        raise SystemExit(1)


def run_case(class_dir: Path, input_text: str) -> tuple[int, str, str]:
    """Run jonathan.Jonathan with one scripted console session."""
    result = subprocess.run(
        ["java", "-cp", str(class_dir), "jonathan.Jonathan"],
        cwd=PROJECT_ROOT,
        input=input_text,
        text=True,
        capture_output=True,
        check=False,
    )
    return result.returncode, result.stdout.replace("\r\n", "\n"), result.stderr


def main() -> None:
    """Run every planned jonathan.ui.UI test and stop at the first mismatch."""
    try:
        cases = load_cases()
    except (OSError, ValueError) as error:
        print(f"Cannot read jonathan.ui.UI test plan: {error}", file=sys.stderr)
        raise SystemExit(1)

    class_dir = Path(tempfile.mkdtemp(prefix="chatbot-ui-tests-"))
    try:
        compile_program(class_dir)
        for name, aim, input_text, expected_text, expected_data in cases:
            SAVE_PATH.unlink(missing_ok=True)
            return_code, actual_text, stderr = run_case(class_dir, input_text)
            actual_data = SAVE_PATH.read_text(encoding="utf-8") if SAVE_PATH.exists() else ""
            print(f"\n=== {name} ===")
            print(f"Aim: {aim}")
            print("--- Console input ---")
            print(input_text, end="")
            print("--- Console output ---")
            print(actual_text, end="")
            if expected_data is not None:
                print("--- Saved data ---")
                print(actual_data, end="")

            if (return_code != 0 or actual_text != expected_text
                    or (expected_data is not None and actual_data != expected_data)):
                print("--- Expected output ---")
                print(expected_text, end="")
                if expected_data is not None:
                    print("--- Expected saved data ---")
                    print(expected_data, end="")
                if stderr:
                    print("--- Standard error ---", file=sys.stderr)
                    print(stderr, end="", file=sys.stderr)
                print(f"FAILED: {name}", file=sys.stderr)
                raise SystemExit(1)

            print(f"PASSED: {name}")
    finally:
        shutil.rmtree(class_dir, ignore_errors=True)


if __name__ == "__main__":
    main()
