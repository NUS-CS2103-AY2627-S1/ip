#!/usr/bin/env python3
"""Run fail-fast console UI tests recorded in test/ui-test-plan.md."""

from __future__ import annotations

import difflib
import re
import shutil
import subprocess
import sys
from pathlib import Path


ROOT = Path(__file__).resolve().parents[4]
PLAN = ROOT / "test" / "ui-test-plan.md"
TRANSCRIPT = ROOT / "test" / "ui-test-session.txt"
CLASSES = ROOT / "build" / "ui-test-classes"
DATA_FILE = ROOT / "data" / "minty.txt"
FIXTURES = ROOT / "test" / "data"
CASE_PATTERN = re.compile(
    r"^## (?P<id>TC\d+): (?P<name>.+?)\n\n"
    r"Aim: (?P<aim>.+?)\n\n"
    r"### Input\n\n```text\n(?P<input>.*?)```\n\n"
    r"### Expected output\n\n```text\n(?P<expected>.*?)```(?=\n+(?:## TC\d+:|\Z)|\Z)",
    re.MULTILINE | re.DOTALL,
)


def parse_cases(plan_text: str) -> list[dict[str, str]]:
    """Extract ordered test cases from the documented Markdown format."""
    cases = [match.groupdict() for match in CASE_PATTERN.finditer(plan_text)]
    if not cases:
        raise ValueError(f"No valid test cases found in {PLAN}")
    return cases


def compile_program() -> None:
    """Compile all production Java sources into an isolated test directory."""
    CLASSES.mkdir(parents=True, exist_ok=True)
    sources = sorted((ROOT / "src" / "main" / "java").rglob("*.java"))
    subprocess.run(
        ["javac", "-d", str(CLASSES), *map(str, sources)],
        cwd=ROOT,
        check=True,
    )


def main() -> int:
    """Run each documented case and stop immediately on the first mismatch."""
    cases = parse_cases(PLAN.read_text(encoding="utf-8"))
    compile_program()
    records: list[str] = []

    for case in cases:
        DATA_FILE.unlink(missing_ok=True)
        try:
            DATA_FILE.parent.rmdir()
        except OSError:
            pass
        fixture = FIXTURES / f"{case['id']}.txt"
        if fixture.exists():
            DATA_FILE.parent.mkdir(parents=True, exist_ok=True)
            shutil.copyfile(fixture, DATA_FILE)

        command_input = case["input"]
        expected = case["expected"]
        result = subprocess.run(
            ["java", "-cp", str(CLASSES), "minty.Minty"],
            cwd=ROOT,
            input=command_input,
            text=True,
            capture_output=True,
        )
        actual = result.stdout
        records.append(
            f"=== {case['id']}: {case['name']} ===\n"
            f"Aim: {case['aim']}\n"
            f"--- Console input ---\n{command_input}"
            f"--- Console output ---\n{actual}"
        )
        TRANSCRIPT.parent.mkdir(parents=True, exist_ok=True)
        TRANSCRIPT.write_text("\n".join(records), encoding="utf-8")

        if result.returncode != 0 or actual != expected:
            print(f"FAIL: {case['id']} - {case['name']}", file=sys.stderr)
            print("--- Actual output ---", file=sys.stderr)
            print(actual, end="", file=sys.stderr)
            print("--- Expected output ---", file=sys.stderr)
            print(expected, end="", file=sys.stderr)
            print("--- Difference ---", file=sys.stderr)
            print(
                "".join(
                    difflib.unified_diff(
                        expected.splitlines(keepends=True),
                        actual.splitlines(keepends=True),
                        fromfile="expected",
                        tofile="actual",
                    )
                ),
                end="",
                file=sys.stderr,
            )
            return 1

        print(f"PASS: {case['id']} - {case['name']}")

    print(f"All {len(cases)} UI test(s) passed. Transcript: {TRANSCRIPT}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
