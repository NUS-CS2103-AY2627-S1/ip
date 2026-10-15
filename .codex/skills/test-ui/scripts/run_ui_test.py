#!/usr/bin/env python3
"""Run exact-output console UI tests and stop after the first failure."""

import argparse
import json
import subprocess
import sys
from pathlib import Path


def normalized(text: str) -> str:
    """Normalize platform line endings while preserving other characters."""
    return text.replace("\r\n", "\n").replace("\r", "\n")


def show_block(label: str, value: str) -> None:
    """Print a clearly delimited transcript section."""
    print(f"--- {label} ---")
    print(value, end="" if not value or value.endswith("\n") else "\n")


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("plan_json", type=Path, help="JSON runner input")
    args = parser.parse_args()
    data = json.loads(args.plan_json.read_text(encoding="utf-8"))
    program = data["program"]
    timeout = data.get("timeout_seconds", 10)

    for case in data["tests"]:
        input_text = "".join(f"{line}\n" for line in case["inputs"])
        expected = normalized(case["expected_output"])
        print(f"=== {case['id']}: {case['aim']} ===")
        print("Launch:", " ".join(program))
        show_block("CONSOLE INPUT", input_text)
        try:
            result = subprocess.run(program, input=input_text, text=True,
                                    capture_output=True, timeout=timeout,
                                    check=False)
        except (OSError, subprocess.TimeoutExpired) as error:
            print(f"RESULT: FAIL ({error})")
            print("Remaining test cases were not run.")
            return 1

        actual = normalized(result.stdout)
        show_block("CONSOLE OUTPUT", actual)
        if result.stderr:
            show_block("STDERR", normalized(result.stderr))
        print(f"Exit status: {result.returncode}")

        if result.returncode != 0 or actual != expected:
            print("RESULT: FAIL")
            show_block("ACTUAL OUTPUT", actual)
            show_block("EXPECTED OUTPUT", expected)
            print("Remaining test cases were not run.")
            return 1
        print("RESULT: PASS")

    print(f"All {len(data['tests'])} test case(s) passed.")
    return 0


if __name__ == "__main__":
    sys.exit(main())
