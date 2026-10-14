#!/usr/bin/env python3
"""Run CLI UI tests recorded in test/ui-test-plan.md."""

from __future__ import annotations

import json
import subprocess
import sys
from pathlib import Path


START_MARKER = "```json ui-tests"
END_MARKER = "```"


def load_cases(plan_path: Path) -> list[dict]:
    text = plan_path.read_text(encoding="utf-8")
    start = text.find(START_MARKER)
    if start == -1:
        raise ValueError(f"Could not find fenced block starting with {START_MARKER!r}")

    json_start = text.find("\n", start)
    if json_start == -1:
        raise ValueError("The ui-tests block is missing its JSON content")

    end = text.find(END_MARKER, json_start + 1)
    if end == -1:
        raise ValueError("The ui-tests block is missing its closing fence")

    cases = json.loads(text[json_start + 1:end].strip())
    if not isinstance(cases, list):
        raise ValueError("The ui-tests JSON block must contain an array")
    return cases


def input_text(raw_input: object) -> str:
    if isinstance(raw_input, list):
        return "".join(f"{line}\n" for line in raw_input)
    if isinstance(raw_input, str):
        return raw_input
    raise ValueError("Each test case input must be a string or an array of strings")


def require_string(case: dict, field: str) -> str:
    value = case.get(field)
    if not isinstance(value, str):
        raise ValueError(f"Each test case must include string field {field!r}")
    return value


def run_case(case: dict) -> tuple[str, str, str, int]:
    command = require_string(case, "command")
    stdin = input_text(case.get("input", ""))
    completed = subprocess.run(
        command,
        input=stdin,
        text=True,
        capture_output=True,
        shell=True,
        check=False,
    )
    actual = completed.stdout
    if completed.stderr:
        actual += completed.stderr
    return command, stdin, actual, completed.returncode


def print_record(name: str, command: str, stdin: str, actual: str, returncode: int) -> None:
    print(f"## {name}")
    print(f"$ {command}")
    print("[console input]")
    print(stdin, end="" if stdin.endswith("\n") or not stdin else "\n")
    print("[console output]")
    print(actual, end="" if actual.endswith("\n") or not actual else "\n")
    print(f"[exit code] {returncode}")


def main() -> int:
    if len(sys.argv) != 2:
        print("Usage: run-ui-tests.py test/ui-test-plan.md", file=sys.stderr)
        return 2

    plan_path = Path(sys.argv[1])
    cases = load_cases(plan_path)
    print("# UI Test Session")

    for index, case in enumerate(cases, start=1):
        name = require_string(case, "name")
        require_string(case, "aim")
        expected = require_string(case, "expected_output")
        command, stdin, actual, returncode = run_case(case)
        print_record(f"{index}. {name}", command, stdin, actual, returncode)

        if actual != expected or returncode != 0:
            print("## FAILED")
            print(f"Test case: {name}")
            print("[expected output]")
            print(expected, end="" if expected.endswith("\n") or not expected else "\n")
            print("[actual output]")
            print(actual, end="" if actual.endswith("\n") or not actual else "\n")
            print(f"[actual exit code] {returncode}")
            return 1

    print("## PASSED")
    print(f"Ran {len(cases)} UI test case(s).")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
