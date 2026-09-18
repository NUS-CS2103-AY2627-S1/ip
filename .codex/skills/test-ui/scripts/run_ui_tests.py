#!/usr/bin/env python3
"""Run the command-line test cases in test/ui-test-plan.md."""

from __future__ import annotations

import argparse
import re
import subprocess
import sys
from pathlib import Path


CASE_RE = re.compile(r"^## Test case:\s*(.+?)\s*$", re.MULTILINE)


def extract_cases(text: str) -> list[dict[str, str]]:
    matches = list(CASE_RE.finditer(text))
    cases = []
    for index, match in enumerate(matches):
        section = text[match.end(): matches[index + 1].start() if index + 1 < len(matches) else len(text)]

        def value(label: str) -> str:
            found = re.search(rf"^\*\*{label}:\*\*\s*(.*?)(?=^\*\*|\Z)", section, re.MULTILINE | re.DOTALL)
            return found.group(1).strip() if found else ""

        def code_block(label: str) -> str:
            found = re.search(rf"^\*\*{label}:\*\*\s*\n```[^\n]*\n(.*?)\n```", section, re.MULTILINE | re.DOTALL)
            return found.group(1) if found else ""

        cases.append({"name": match.group(1), "aim": value("Aim"), "command": value("Command").strip("`") , "inputs": code_block("Inputs"), "expected": code_block("Expected output")})
    return cases


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("plan", nargs="?", default="test/ui-test-plan.md")
    parser.add_argument("--cwd", default=".")
    args = parser.parse_args()
    plan = Path(args.plan)
    if not plan.is_file():
        print(f"Cannot run UI tests: missing plan {plan}")
        return 2
    cases = extract_cases(plan.read_text())
    if not cases:
        print(f"Cannot run UI tests: no test cases found in {plan}")
        return 2

    print("=== UI TEST SESSION ===")
    for number, case in enumerate(cases, 1):
        print(f"\n--- Test case {number}: {case['name']} ---")
        print(f"Aim: {case['aim']}")
        print(f"$ {case['command']}")
        print(f"[console input]\n{case['inputs'] or '(none)'}")
        result = subprocess.run(case["command"], input=case["inputs"], text=True, capture_output=True, shell=True, cwd=args.cwd)
        actual = result.stdout + result.stderr
        print(f"[console output]\n{actual}")
        if actual != case["expected"]:
            print("FAIL")
            print(f"Expected output:\n{case['expected']}")
            print("Test session terminated after the first failure.")
            return 1
        print("PASS")
    print("\nAll UI test cases passed.")
    return 0


if __name__ == "__main__":
    sys.exit(main())
