#!/usr/bin/env python3
"""Run command-line UI test cases recorded in a Markdown test plan."""

from __future__ import annotations

import argparse
import re
import subprocess
import sys
from pathlib import Path


def section(text: str, heading: str) -> str:
    match = re.search(rf"^### {re.escape(heading)}\s*$", text, re.MULTILINE)
    if not match:
        return ""
    remainder = text[match.end():]
    next_heading = re.search(r"^###?\s+", remainder, re.MULTILINE)
    return remainder[:next_heading.start()] if next_heading else remainder


def block(text: str) -> str:
    match = re.search(r"```(?:text|console|shell)?\s*\n(.*?)```", text, re.DOTALL)
    return match.group(1) if match else text.strip() + ("\n" if text.strip() else "")


def parse_plan(path: Path) -> tuple[list[dict], float]:
    text = path.read_text(encoding="utf-8")
    timeout_match = re.search(r"^Timeout seconds:\s*(\d+(?:\.\d+)?)\s*$", text, re.MULTILINE)
    timeout = float(timeout_match.group(1)) if timeout_match else 10.0
    headings = list(re.finditer(r"^## Test case:\s*(.+?)\s*$", text, re.MULTILINE))
    cases = []
    for index, heading in enumerate(headings):
        end = headings[index + 1].start() if index + 1 < len(headings) else len(text)
        case_text = text[heading.end():end]
        command = block(section(case_text, "Command")).strip()
        expected = block(section(case_text, "Expected output"))
        if not command or not expected:
            raise ValueError(f"Test case {index + 1} is missing Command or Expected output")
        cases.append({
            "name": heading.group(1).strip(),
            "aim": section(case_text, "Aim").strip(),
            "command": command,
            "input": block(section(case_text, "Input")),
            "expected": expected,
        })
    if not cases:
        raise ValueError("The test plan contains no '## Test case:' sections")
    return cases, timeout


def display(value: str) -> str:
    return value if value else "(no output)"


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("plan", nargs="?", default="test/ui-test-plan.md")
    parser.add_argument("--timeout", type=float, default=None)
    args = parser.parse_args()
    try:
        cases, plan_timeout = parse_plan(Path(args.plan))
    except (OSError, ValueError) as error:
        print(f"Unable to read test plan: {error}", file=sys.stderr)
        return 2

    for number, case in enumerate(cases, start=1):
        timeout = args.timeout if args.timeout is not None else plan_timeout
        print(f"\n=== Test case {number}: {case['name']} ===")
        if case["aim"]:
            print(f"Aim: {case['aim']}")
        print(f"$ {case['command']}")
        input_text = case["input"].rstrip("\n")
        print("> " + (input_text.replace("\n", "\n> ") if input_text else "(no input)"))
        try:
            result = subprocess.run(case["command"], input=case["input"], text=True,
                                    capture_output=True, shell=True, timeout=timeout)
        except subprocess.TimeoutExpired:
            print(f"FAIL: timed out after {timeout:g} seconds")
            print("Expected output:\n" + display(case["expected"].rstrip("\n")))
            print("Actual output:\n(process timed out)")
            return 1

        actual = result.stdout.replace("\r\n", "\n").rstrip("\n")
        expected = case["expected"].replace("\r\n", "\n").rstrip("\n")
        print("Output:\n" + display(actual))
        if result.returncode != 0 or actual != expected:
            print(f"FAIL: process exited with status {result.returncode}")
            print("Expected output:\n" + display(expected))
            print("Actual output:\n" + display(actual))
            if result.stderr:
                print("Stderr:\n" + result.stderr.rstrip("\n"))
            return 1
        print("PASS")
    print(f"\nAll {len(cases)} UI test case(s) passed.")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
