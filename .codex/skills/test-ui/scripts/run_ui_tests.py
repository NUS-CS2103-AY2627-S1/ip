#!/usr/bin/env python3
"""Run the console UI cases in test/ui-test-plan.md and record their transcript."""
from __future__ import annotations
import re
import subprocess
import sys
from dataclasses import dataclass
from pathlib import Path

PLAN_PATH = Path("test/ui-test-plan.md")
SESSION_PATH = Path("test/ui-test-session.md")
CASE_HEADING = re.compile(r"^##\s+(.+?)\s*$", re.MULTILINE)

@dataclass
class TestCase:
    """A command-line test case parsed from the Markdown plan."""
    name: str
    aim: str
    command: str
    user_input: str
    expected_output: str

def normalize(text: str) -> str:
    """Make line-ending differences irrelevant while retaining all other output."""
    return text.replace("\r\n", "\n").replace("\r", "\n")

def field_block(case_text: str, label: str, keep_final_newline: bool = False) -> str:
    """Return the fenced-code content following one required plan field."""
    pattern = re.compile(rf"\*\*{re.escape(label)}:\*\*\s*\r?\n```[^\r\n]*\r?\n(.*?)```", re.DOTALL)
    match = pattern.search(case_text)
    if not match:
        raise ValueError(f"Missing fenced **{label}:** block")
    content = match.group(1)
    return content if keep_final_newline else content.rstrip("\r\n")

def parse_cases(plan: str) -> list[TestCase]:
    """Parse all level-two test-case sections from the test plan."""
    headings = list(CASE_HEADING.finditer(plan))
    if not headings:
        raise ValueError("No test cases found; add a level-two (##) case heading.")
    cases = []
    for index, heading in enumerate(headings):
        end = headings[index + 1].start() if index + 1 < len(headings) else len(plan)
        case_text = plan[heading.end():end]
        aim_match = re.search(r"\*\*Aim:\*\*\s*(.+)", case_text)
        if not aim_match:
            raise ValueError(f"{heading.group(1)}: Missing one-line **Aim:** field")
        cases.append(TestCase(heading.group(1), aim_match.group(1).strip(), field_block(case_text, "Command"), field_block(case_text, "Input"), field_block(case_text, "Expected output", keep_final_newline=True)))
    return cases

def fenced(label: str, value: str) -> str:
    """Format captured text so that it is readable in a Markdown transcript."""
    return f"**{label}:**\n```text\n{value}\n```\n"

def main() -> int:
    """Run cases sequentially, saving all executed interactions before returning."""
    if not PLAN_PATH.is_file():
        print(f"Test plan not found: {PLAN_PATH}", file=sys.stderr)
        return 2
    try:
        cases = parse_cases(PLAN_PATH.read_text(encoding="utf-8"))
    except ValueError as error:
        print(f"Invalid test plan: {error}", file=sys.stderr)
        return 2
    transcript = ["# UI test session\n"]
    for number, case in enumerate(cases, start=1):
        result = subprocess.run(case.command, shell=True, input=case.user_input + "\n" if case.user_input else "", text=True, stdout=subprocess.PIPE, stderr=subprocess.STDOUT, cwd=Path.cwd())
        actual = normalize(result.stdout)
        expected = normalize(case.expected_output)
        passed = result.returncode == 0 and actual == expected
        status = "PASS" if passed else "FAIL"
        record = [f"## {number}. {case.name} — {status}\n", f"Aim: {case.aim}\n", fenced("Command", case.command), fenced("Console input", case.user_input), fenced("Expected output", expected), fenced("Actual output", actual), f"Exit code: {result.returncode}\n"]
        transcript.extend(record)
        print("\n".join(record))
        SESSION_PATH.parent.mkdir(parents=True, exist_ok=True)
        SESSION_PATH.write_text("\n".join(transcript), encoding="utf-8")
        if not passed:
            print(f"Test failed: {case.name}. Stopping immediately.", file=sys.stderr)
            return 1
    print(f"All {len(cases)} UI test case(s) passed. Transcript: {SESSION_PATH}")
    return 0

if __name__ == "__main__":
    raise SystemExit(main())