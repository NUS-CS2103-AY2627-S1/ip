#!/usr/bin/env python3
"""Compile Benji and compare its console output with Markdown UI test cases."""

from __future__ import annotations

import re
import shutil
import subprocess
import sys
from pathlib import Path


ROOT = Path(__file__).resolve().parents[4]
PLAN = ROOT / "test" / "ui-test-plan.md"
CLASSES = ROOT / "_temp" / "test-ui-classes"
CASE_PATTERN = re.compile(
    r"## (?P<title>.+?)\n\nAim: (?P<aim>.+?)\n\n### Input\n\n```text\n"
    r"(?P<input>.*?)\n```\n\n### Expected output\n\n```text\n"
    r"(?P<expected>.*?)\n```",
    re.DOTALL,
)


def show_session(title: str, commands: str, output: str) -> None:
    """Print a readable record of the input and resulting console output."""
    print(f"\n=== {title} ===")
    print("Console input:")
    print(commands)
    print("Console output:")
    print(output, end="" if output.endswith("\n") else "\n")


def first_difference(expected: str, actual: str) -> str:
    """Return the first line at which two output strings differ."""
    expected_lines = expected.splitlines()
    actual_lines = actual.splitlines()
    for number, (expected_line, actual_line) in enumerate(
            zip(expected_lines, actual_lines), start=1):
        if expected_line != actual_line:
            return (f"First difference at output line {number}:\n"
                    f"  expected: {expected_line!r}\n"
                    f"  actual:   {actual_line!r}")
    return (f"Different number of output lines: expected {len(expected_lines)}, "
            f"actual {len(actual_lines)}.")


def normalize_output(output: str) -> str:
    """Ignore trailing spaces, which do not change visible console output."""
    return "\n".join(line.rstrip() for line in output.splitlines()) + "\n"


def main() -> int:
    """Compile the application and run every test case until a failure occurs."""
    cases = list(CASE_PATTERN.finditer(PLAN.read_text(encoding="utf-8")))
    if not cases:
        print(f"No valid test cases found in {PLAN}", file=sys.stderr)
        return 2

    shutil.rmtree(CLASSES, ignore_errors=True)
    CLASSES.mkdir(parents=True)
    sources = sorted((ROOT / "src" / "main" / "java").glob("*.java"))
    compilation = subprocess.run(
        ["javac", "-d", str(CLASSES), *map(str, sources)],
        text=True,
        capture_output=True,
    )
    if compilation.returncode != 0:
        print("Compilation failed:\n" + compilation.stderr, file=sys.stderr)
        return 1

    for case in cases:
        title = case["title"]
        commands = case["input"] + "\n"
        expected = case["expected"] + "\n"
        run = subprocess.run(
            ["java", "-cp", str(CLASSES), "Benji"],
            input=commands,
            text=True,
            capture_output=True,
        )
        show_session(title, commands, run.stdout)
        if run.returncode != 0:
            print("Test failed: the application exited with an error.", file=sys.stderr)
            print("\nExpected output:")
            print(expected, end="")
            print("\nActual output:")
            print(run.stdout, end="")
            print(run.stderr, file=sys.stderr)
            return 1
        if normalize_output(run.stdout) != normalize_output(expected):
            print("\nTest failed.")
            print(first_difference(normalize_output(expected), normalize_output(run.stdout)))
            print("\nExpected output:")
            print(expected, end="")
            print("\nActual output:")
            print(run.stdout, end="")
            return 1
        print("Test passed.")

    print("\nAll UI tests passed.")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
