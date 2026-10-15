#!/usr/bin/env python3
"""Run console UI cases recorded in test/ui-test-plan.md."""
from __future__ import annotations
import argparse
import re
import subprocess
import sys
from dataclasses import dataclass
from pathlib import Path

CASE_RE = re.compile(r"^## Test case:\s*(?P<name>.+?)\s*$", re.MULTILINE)
FIELD_RE = re.compile(r"^(?P<label>Aim|Command|Input|Expected output):\s*\n```[^\n]*\n(?P<value>.*?)\n```\s*$", re.MULTILINE | re.DOTALL)

@dataclass
class TestCase:
    name: str
    aim: str
    command: str
    input_text: str
    expected: str

def normalise(text: str) -> str:
    return text.replace("\r\n", "\n").replace("\r", "\n")

def parse_cases(plan: Path) -> list[TestCase]:
    text = normalise(plan.read_text(encoding="utf-8"))
    starts = list(CASE_RE.finditer(text))
    if not starts:
        raise ValueError("No '## Test case: ...' headings found.")
    cases = []
    for index, start in enumerate(starts):
        end = starts[index + 1].start() if index + 1 < len(starts) else len(text)
        fields = {match.group("label"): match.group("value") for match in FIELD_RE.finditer(text[start.end():end])}
        missing = {"Aim", "Command", "Input", "Expected output"} - fields.keys()
        if missing:
            raise ValueError(f"Test case '{start.group('name')}' is missing: {', '.join(sorted(missing))}.")
        cases.append(TestCase(start.group("name"), fields["Aim"], fields["Command"], fields["Input"] + "\n", fields["Expected output"] + "\n"))
    return cases

def fenced(title: str, value: str) -> str:
    return f"### {title}\n\n```text\n{value}```\n"

def main() -> int:
    parser = argparse.ArgumentParser(description="Run UI cases from a Markdown test plan.")
    parser.add_argument("--plan", type=Path, default=Path("test/ui-test-plan.md"))
    parser.add_argument("--session", type=Path, default=Path("test/ui-test-session.md"))
    args = parser.parse_args()
    root = Path.cwd()
    try:
        cases = parse_cases((root / args.plan).resolve())
    except (OSError, ValueError) as error:
        print(f"Cannot run UI tests: {error}", file=sys.stderr)
        return 2
    session = (root / args.session).resolve()
    record = ["# UI test session\n"]
    for number, case in enumerate(cases, 1):
        completed = subprocess.run(case.command, shell=True, cwd=root, input=case.input_text, text=True, stdout=subprocess.PIPE, stderr=subprocess.STDOUT, check=False)
        actual = normalise(completed.stdout)
        expected = normalise(case.expected)
        passed = actual == expected and completed.returncode == 0
        record += [f"## {number}. {case.name} - {'PASS' if passed else 'FAIL'}\n", f"Aim: {case.aim}\n", fenced("Command", case.command + "\n"), fenced("Input sent to stdin", case.input_text), fenced("Program output", actual), f"Exit code: `{completed.returncode}`\n"]
        if not passed:
            record += [fenced("Expected output", expected), fenced("Actual output", actual), f"Stopped after first failure: {'output differs' if actual != expected else 'non-zero exit code'}.\n"]
            session.parent.mkdir(parents=True, exist_ok=True)
            session.write_text("\n".join(record), encoding="utf-8")
            print("\n".join(record), end="")
            return 1
    record.append(f"All {len(cases)} test case(s) passed.\n")
    session.parent.mkdir(parents=True, exist_ok=True)
    session.write_text("\n".join(record), encoding="utf-8")
    print("\n".join(record), end="")
    return 0

if __name__ == "__main__":
    raise SystemExit(main())