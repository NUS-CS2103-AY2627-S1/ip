#!/usr/bin/env python3
"""Run markdown-defined console UI tests with exact, fail-fast comparisons."""
import re
import subprocess
import sys
from pathlib import Path


def parse_plan(path: Path):
    text = path.read_text(encoding="utf-8")
    sections = re.split(r"(?m)^##\s+", text)[1:]
    cases = []
    for section in sections:
        lines = section.splitlines()
        name = lines[0].strip()
        def field(label):
            match = re.search(rf"(?ms)^\*\*{label}:\*\*\s*\n```[^\n]*\n(.*?)\n```", section)
            return match.group(1) if match else None
        aim = re.search(r"(?m)^\*\*Aim:\*\*\s*(.+)$", section)
        command = field("Command")
        inputs = field("Inputs") or ""
        expected = field("Expected output")
        if expected is not None:
            expected += "\n"
        if not aim or command is None or expected is None:
            raise ValueError(f"{name}: requires Aim, Command, and Expected output")
        cases.append((name, aim.group(1).strip(), command.strip(), inputs, expected))
    if not cases:
        raise ValueError("no test cases found")
    return cases


def normalize(value):
    return value.replace("\r\n", "\n").replace("\r", "\n")


def main():
    plan = Path(sys.argv[1] if len(sys.argv) > 1 else "test/ui-test-plan.md")
    try:
        cases = parse_plan(plan)
    except (OSError, ValueError) as exc:
        print(f"TEST PLAN ERROR: {exc}", file=sys.stderr)
        return 2
    for index, (name, aim, command, inputs, expected) in enumerate(cases, 1):
        result = subprocess.run(command, shell=True, input=inputs, text=True,
                                stdout=subprocess.PIPE, stderr=subprocess.STDOUT)
        actual = result.stdout
        print(f"\n=== Test {index}: {name} ===\nAim: {aim}\nCommand: {command}")
        print(f"Console input:\n{inputs or '(none)'}")
        print(f"Console output:\n{actual}")
        if normalize(actual) != normalize(expected) or result.returncode != 0:
            print("RESULT: FAIL")
            print(f"Expected output:\n{expected}")
            print(f"Exit code: {result.returncode}")
            print(f"Stopped after test {index}; later tests were not run.")
            return 1
        print("RESULT: PASS")
    print(f"\nTest session complete: {len(cases)} passed.")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
