#!/usr/bin/env python3
"""Run exact-output console UI tests described by a Markdown test plan."""

from __future__ import annotations

import re
import subprocess
import sys
from dataclasses import dataclass
from pathlib import Path


@dataclass
class TestCase:
    """A single console invocation and its expected observable result."""

    name: str
    aim: str
    command: str
    input_text: str
    expected_output: str
    expected_exit_code: int = 0


FIELD_BLOCK = re.compile(
    r"(?ms)^Command:\s*\n+```[^\n]*\n(.*?)^```\s*\n+"
    r"Input:\s*\n+```[^\n]*\n(.*?)^```\s*\n+"
    r"Expected output:\s*\n+```[^\n]*\n(.*?)^```"
)


def block_contents(text: str) -> str:
    """Remove the structural newline immediately before a closing fence."""
    return text[:-1] if text.endswith("\n") else text


def parse_plan(plan_text: str) -> list[TestCase]:
    """Parse test cases from the skill's intentionally strict Markdown format."""
    plan_text = re.sub(r"(?s)<!--.*?-->", "", plan_text)
    headings = list(re.finditer(r"(?m)^## (.+)$", plan_text))
    cases: list[TestCase] = []
    for index, heading in enumerate(headings):
        end = headings[index + 1].start() if index + 1 < len(headings) else len(plan_text)
        body = plan_text[heading.end():end]
        aim_match = re.search(r"(?m)^Aim:\s*(.+)$", body)
        blocks_match = FIELD_BLOCK.search(body)
        if not aim_match or not blocks_match:
            raise ValueError(
                f"{heading.group(1)!r} must contain Aim, Command, Input, and Expected output"
            )
        exit_match = re.search(r"(?m)^Expected exit code:\s*(-?\d+)\s*$", body)
        cases.append(
            TestCase(
                name=heading.group(1).strip(),
                aim=aim_match.group(1).strip(),
                command=block_contents(blocks_match.group(1)),
                input_text=block_contents(blocks_match.group(2)),
                expected_output=block_contents(blocks_match.group(3)),
                expected_exit_code=int(exit_match.group(1)) if exit_match else 0,
            )
        )
    if not cases:
        raise ValueError("No test cases found (expected at least one '##' heading)")
    return cases


def comparable(text: str) -> str:
    """Normalize platform newlines and ignore at most one trailing newline."""
    normalized = text.replace("\r\n", "\n").replace("\r", "\n")
    return normalized[:-1] if normalized.endswith("\n") else normalized


def fenced(text: str) -> str:
    """Render literal console content safely in a Markdown fence."""
    fence = "```" if "```" not in text else "````"
    return f"{fence}text\n{text}\n{fence}"


def case_record(case: TestCase, actual: str, exit_code: int, passed: bool) -> str:
    """Create an auditable record for one executed case."""
    supplied = case.input_text
    return (
        f"## {case.name} — {'PASS' if passed else 'FAIL'}\n\n"
        f"Aim: {case.aim}\n\n"
        f"Command:\n\n{fenced(case.command)}\n\n"
        f"Console input:\n\n{fenced(supplied)}\n\n"
        f"Actual console output:\n\n{fenced(actual)}\n\n"
        f"Expected output:\n\n{fenced(case.expected_output)}\n\n"
        f"Exit code: `{exit_code}` (expected `{case.expected_exit_code}`)\n"
    )


def main() -> int:
    """Execute cases in order, write the transcript, and stop at first failure."""
    plan_path = Path(sys.argv[1] if len(sys.argv) > 1 else "test/ui-test-plan.md")
    session_path = plan_path.with_name("ui-test-session.md")
    try:
        cases = parse_plan(plan_path.read_text(encoding="utf-8"))
    except (OSError, ValueError) as error:
        print(f"Cannot run UI tests: {error}", file=sys.stderr)
        return 2

    records = ["# UI Test Session\n"]
    for case in cases:
        process = subprocess.run(
            case.command,
            input=case.input_text + ("\n" if case.input_text else ""),
            text=True,
            shell=True,
            stdout=subprocess.PIPE,
            stderr=subprocess.STDOUT,
            cwd=Path.cwd(),
            check=False,
        )
        passed = (
            comparable(process.stdout) == comparable(case.expected_output)
            and process.returncode == case.expected_exit_code
        )
        records.append(case_record(case, process.stdout, process.returncode, passed))
        session_path.parent.mkdir(parents=True, exist_ok=True)
        session_path.write_text("\n".join(records), encoding="utf-8")
        print(f"{'PASS' if passed else 'FAIL'}: {case.name}")
        if not passed:
            print("\nACTUAL OUTPUT\n" + process.stdout)
            print("EXPECTED OUTPUT\n" + case.expected_output)
            print(f"Session stopped; record written to {session_path}")
            return 1

    print(f"All {len(cases)} test case(s) passed; record written to {session_path}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
