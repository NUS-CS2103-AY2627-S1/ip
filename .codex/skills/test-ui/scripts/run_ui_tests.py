#!/usr/bin/env python3
"""Run UI test cases declared in a Markdown test plan."""

from __future__ import annotations

import argparse
import re
import subprocess
import sys
from dataclasses import dataclass
from pathlib import Path


@dataclass
class TestCase:
    """One console test case parsed from the test plan."""

    title: str
    aim: str
    user_input: str
    expected_output: str


def fenced_block(text: str, heading: str) -> str:
    """Return the fenced code block that follows a second-level heading."""
    match = re.search(
        rf"^## {re.escape(heading)}\s*\n+```[^\n]*\n(.*?)\n```",
        text,
        re.MULTILINE | re.DOTALL,
    )
    if not match:
        raise ValueError(f"Missing a fenced code block below '## {heading}'.")
    return match.group(1)


def parse_cases(text: str) -> list[TestCase]:
    """Parse all third-level Markdown test cases from a test plan."""
    pattern = re.compile(
        r"^### (?P<title>.+?)\s*\n+"
        r"\*\*Aim:\*\*\s*(?P<aim>.+?)\s*\n+"
        r"\*\*Input:\*\*\s*\n+```[^\n]*\n(?P<input>.*?)\n```\s*\n+"
        r"\*\*Expected output:\*\*\s*\n+```[^\n]*\n(?P<expected>.*?)\n```",
        re.MULTILINE | re.DOTALL,
    )
    return [
        TestCase(
            title=match.group("title").strip(),
            aim=match.group("aim").strip(),
            user_input=match.group("input"),
            expected_output=match.group("expected"),
        )
        for match in pattern.finditer(text)
    ]


def normalize(text: str) -> str:
    """Use Unix line endings and require one final newline for comparisons."""
    return text.replace("\r\n", "\n").replace("\r", "\n").rstrip("\n") + "\n"


def code_block(text: str) -> str:
    """Format text as a Markdown text code block."""
    return f"```text\n{text.rstrip()}\n```\n"


def session_heading(case: TestCase, number: int) -> str:
    """Return the common report heading for one test case."""
    return f"## {number}. {case.title}\n\n**Aim:** {case.aim}\n\n### Console input\n\n{code_block(case.user_input)}"


def run_case(command: str, case: TestCase) -> tuple[bool, str]:
    """Run one case and return whether its actual output matched exactly."""
    result = subprocess.run(
        command,
        shell=True,
        input=normalize(case.user_input),
        stdout=subprocess.PIPE,
        stderr=subprocess.STDOUT,
        text=True,
    )
    actual = normalize(result.stdout)
    expected = normalize(case.expected_output)
    return result.returncode == 0 and actual == expected, actual


def main() -> int:
    """Run the plan and create a session record, stopping at the first failure."""
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--plan", default="test/ui-test-plan.md")
    parser.add_argument("--session", default="test/ui-test-session.md")
    args = parser.parse_args()

    plan_path = Path(args.plan)
    session_path = Path(args.session)
    try:
        plan_text = plan_path.read_text(encoding="utf-8")
        command = fenced_block(plan_text, "Test command")
        cases = parse_cases(plan_text)
    except (OSError, ValueError) as error:
        print(f"Cannot read test plan: {error}", file=sys.stderr)
        return 2

    if not cases:
        print("No test cases were found in the test plan.", file=sys.stderr)
        return 2

    report = ["# UI Test Session\n", f"**Test command:** `{command}`\n"]
    for number, case in enumerate(cases, start=1):
        passed, actual = run_case(command, case)
        report.append(session_heading(case, number))
        report.append("### Console output\n\n")
        report.append(code_block(actual))

        if passed:
            report.append("**Result:** PASS\n")
            continue

        report.append("**Result:** FAIL — testing stopped at this case.\n\n")
        report.append("### Expected output\n\n")
        report.append(code_block(case.expected_output))
        session_path.parent.mkdir(parents=True, exist_ok=True)
        session_path.write_text("\n".join(report), encoding="utf-8")
        print(f"FAIL: {case.title}. See {session_path}.", file=sys.stderr)
        return 1

    session_path.parent.mkdir(parents=True, exist_ok=True)
    session_path.write_text("\n".join(report), encoding="utf-8")
    print(f"PASS: {len(cases)} case(s). See {session_path}.")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
