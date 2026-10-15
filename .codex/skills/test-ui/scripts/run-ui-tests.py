#!/usr/bin/env python3
"""Run Markdown-defined console tests and stop at the first failure."""
from __future__ import annotations

import argparse
import re
import subprocess
import sys
from pathlib import Path

CASE_RE = re.compile(
    r"### Test Case:\s*(?P<name>[^\n]+)\n"
    r"Aim:\s*(?P<aim>[^\n]*)\n\s*\n"
    r"Command:\s*\n```(?:text|powershell|bash)?\n(?P<command>.*?)\n```\n\s*\n"
    r"Input:\s*\n```(?:text)?\n(?P<input>.*?)\n```\n\s*\n"
    r"Expected output:\s*\n```(?:text)?\n(?P<expected>.*?)\n```",
    re.DOTALL,
)


def display(value: str) -> str:
    return value if value else "<empty>"


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("plan", type=Path)
    args = parser.parse_args()
    plan = args.plan.resolve()
    cases = list(CASE_RE.finditer(plan.read_text(encoding="utf-8")))
    if not cases:
        print(f"No test cases found in {plan}", file=sys.stderr)
        return 2

    repo = plan.parent.parent
    for number, match in enumerate(cases, 1):
        name = match.group("name").strip()
        command = match.group("command").strip()
        supplied = match.group("input")
        expected = match.group("expected")
        if supplied == "<empty>":
            supplied = ""
        print(f"\n=== Test {number}: {name} ===")
        print("--- console input ---")
        print(display(supplied))
        result = subprocess.run(
            command, cwd=repo, input=supplied, text=True,
            capture_output=True, shell=True,
        )
        actual = result.stdout.rstrip("\n")
        print("--- console output ---")
        print(display(actual))
        print("--- expected output ---")
        print(display(expected))
        if result.returncode != 0 or actual != expected:
            print(f"FAIL: {name} (exit code {result.returncode})")
            if result.stderr:
                print("--- stderr ---")
                print(result.stderr.rstrip("\n"))
            print("Test session terminated at the first failure.")
            return 1
        print(f"PASS: {name}")
    print(f"\nAll {len(cases)} test case(s) passed.")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
