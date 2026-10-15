#!/usr/bin/env python3
"""Run fail-fast console UI tests described in test/ui-test-plan.md."""

from __future__ import annotations

import re
import shlex
import subprocess
import sys
from pathlib import Path

PLAN_PATH = Path("test/ui-test-plan.md")
SECTION = re.compile(r"^## (?P<title>.+?)\s*$\n(?P<body>.*?)(?=^## |\Z)", re.MULTILINE | re.DOTALL)
CASE_TITLE = re.compile(r"^Test case:\s*(?P<name>.+)$", re.IGNORECASE)
SUBSECTION = re.compile(r"^### (?P<title>Aim|Inputs|Expected output)\s*$\n+```[^\n]*\n(?P<content>.*?)\n```", re.MULTILINE | re.DOTALL | re.IGNORECASE)
AIM = re.compile(r"^### Aim\s*$\n+(?P<content>.*?)(?=^### |\Z)", re.MULTILINE | re.DOTALL | re.IGNORECASE)

def normalize(text: str) -> str:
    """Normalize platform line endings while retaining meaningful output."""
    return text.replace("\r\n", "\n").replace("\r", "\n").rstrip("\n")

def parse_plan(plan: str) -> tuple[str, int, list[dict[str, str]]]:
    """Read the runner configuration and cases from the Markdown plan."""
    sections = list(SECTION.finditer(plan))
    command = next((section.group("body").strip() for section in sections if section.group("title").strip().lower() == "program command"), None)
    if not command:
        raise ValueError("Missing a non-empty '## Program command' section.")
    timeout_text = next((section.group("body").strip() for section in sections if section.group("title").strip().lower() == "timeout seconds"), "5")
    try:
        timeout = int(timeout_text)
    except ValueError as error:
        raise ValueError("'## Timeout seconds' must contain a whole number.") from error
    tests = []
    for section in sections:
        match = CASE_TITLE.match(section.group("title").strip())
        if not match:
            continue
        fields = {field.group("title").lower(): field.group("content") for field in SUBSECTION.finditer(section.group("body"))}
        aim = AIM.search(section.group("body"))
        if aim:
            fields["aim"] = aim.group("content").strip()
        missing = {"aim", "inputs", "expected output"} - fields.keys()
        if missing:
            raise ValueError(f"Test case '{match.group('name')}' is missing: {', '.join(sorted(missing))}.")
        tests.append({"name": match.group("name"), **fields})
    if not tests:
        raise ValueError("No '## Test case: ...' sections found.")
    return command, timeout, tests

def print_session(name: str, inputs: str, output: str) -> None:
    """Print a readable record of one test's console session."""
    print(f"\n=== {name} ===\nConsole input:")
    print(inputs or "<no input>")
    print("Console output:")
    print(output or "<no output>")

def main() -> int:
    """Execute cases in order and stop immediately on a mismatch or error."""
    if not PLAN_PATH.is_file():
        print(f"Test plan not found: {PLAN_PATH}", file=sys.stderr)
        return 2
    try:
        command, timeout, tests = parse_plan(PLAN_PATH.read_text(encoding="utf-8"))
    except ValueError as error:
        print(f"Invalid test plan: {error}", file=sys.stderr)
        return 2
    for index, test in enumerate(tests, start=1):
        inputs = test["inputs"]
        stdin = f"{inputs}\n" if inputs and not inputs.endswith("\n") else inputs
        try:
            command_parts = shlex.split(command, posix=False)
            result = subprocess.run(command_parts, input=stdin, text=True, stdout=subprocess.PIPE, stderr=subprocess.STDOUT, timeout=timeout, cwd=Path.cwd())
        except subprocess.TimeoutExpired as error:
            print_session(test["name"], inputs, error.stdout or "")
            print(f"FAIL: Test {index} timed out after {timeout} seconds.")
            return 1
        actual = result.stdout
        print_session(test["name"], inputs, actual)
        expected = test["expected output"]
        if result.returncode != 0 or normalize(actual) != normalize(expected):
            print(f"FAIL: Test {index} — {test['name']}")
            if result.returncode != 0:
                print(f"Program exit code: {result.returncode}")
            print("Expected output:")
            print(expected or "<no output>")
            print("Actual output:")
            print(actual or "<no output>")
            return 1
        print(f"PASS: Test {index} — {test['name']}")
    print(f"\nAll {len(tests)} UI test case(s) passed.")
    return 0

if __name__ == "__main__":
    raise SystemExit(main())
