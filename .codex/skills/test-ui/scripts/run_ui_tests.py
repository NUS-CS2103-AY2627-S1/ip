#!/usr/bin/env python3
"""Run scripted command-line UI tests described in a Markdown test plan."""

from __future__ import annotations

import argparse
import difflib
import re
import subprocess
import sys
from dataclasses import dataclass
from pathlib import Path


SECTION_NAMES = {
    "aim": "aim",
    "command": "command",
    "inputs": "inputs",
    "expected output": "expected_output",
}
CASE_HEADING = re.compile(r"^##\s+(?:Test Case\s*:?\s*|\d+[.)]\s+)(?P<name>.+?)\s*$", re.IGNORECASE)
SECTION_HEADING = re.compile(r"^###\s+(?P<section>.+?)\s*$", re.IGNORECASE | re.MULTILINE)
FENCE = re.compile(r"^```[^\r\n]*\r?\n(?P<content>.*?)^```\s*$", re.MULTILINE | re.DOTALL)


@dataclass
class TestCase:
    """One command/input/expected-output entry from the test plan."""

    name: str
    aim: str
    command: str
    inputs: str
    expected_output: str


def repository_root() -> Path:
    """Return the project root based on this script's location."""

    return Path(__file__).resolve().parents[4]


def normalize_output(output: str) -> str:
    """Normalize platform line endings and ignore only final newline characters."""

    return output.replace("\r\n", "\n").replace("\r", "\n").rstrip("\n")


def fenced_content(text: str, start: int) -> str:
    """Return the first fenced block after a section heading."""

    match = FENCE.search(text, start)
    if match is None:
        raise ValueError("section is missing a fenced code block")
    return match.group("content")


def parse_plan(plan_path: Path) -> list[TestCase]:
    """Parse the supported test-case structure from a Markdown plan."""

    text = plan_path.read_text(encoding="utf-8")
    headings = list(re.finditer(r"^##\s+.+?$", text, re.MULTILINE))
    if not headings:
        raise ValueError("the test plan contains no level-two test-case headings")

    cases: list[TestCase] = []
    for index, heading_match in enumerate(headings):
        case_match = CASE_HEADING.match(heading_match.group(0))
        if case_match is None:
            continue
        name = case_match.group("name").strip()
        case_end = headings[index + 1].start() if index + 1 < len(headings) else len(text)
        case_text = text[heading_match.end() : case_end]
        sections = list(SECTION_HEADING.finditer(case_text))
        values: dict[str, str] = {}
        for section_index, section_match in enumerate(sections):
            section_name = SECTION_NAMES.get(section_match.group("section").strip().lower())
            if section_name is None:
                continue
            section_end = (
                sections[section_index + 1].start()
                if section_index + 1 < len(sections)
                else len(case_text)
            )
            section_body_start = section_match.end()
            section_body = case_text[section_body_start:section_end]
            if section_name == "aim":
                values[section_name] = section_body.strip()
            else:
                values[section_name] = fenced_content(section_body, 0)

        missing = [field for field in SECTION_NAMES.values() if field not in values]
        if missing:
            raise ValueError(f"test case '{name}' is missing: {', '.join(missing)}")
        if not values["command"].strip():
            raise ValueError(f"test case '{name}' has an empty command")
        cases.append(TestCase(name=name, **values))

    if not cases:
        raise ValueError("the test plan contains no valid test cases")
    return cases


def format_block(value: str) -> str:
    """Make an empty transcript visible while preserving all non-empty output."""

    return value if value else "<empty>"


def show_session(case: TestCase, actual: str, exit_code: int | None, status: str) -> None:
    """Print the console input/output record for one test case."""

    print(f"\n=== {case.name} ===")
    print(f"Aim: {case.aim}")
    print(f"$ {case.command}")
    print("--- Console input ---")
    print(format_block(case.inputs), end="" if case.inputs.endswith("\n") else "\n")
    print("--- Console output ---")
    print(format_block(actual), end="" if actual.endswith("\n") else "\n")
    exit_text = "unknown" if exit_code is None else str(exit_code)
    print(f"--- Result: {status} (exit code {exit_text}) ---")


def run_case(case: TestCase, root: Path, timeout: float) -> tuple[str, int | None, str | None]:
    """Run one test case and return its output, exit code, and diagnostic."""

    try:
        completed = subprocess.run(
            case.command,
            cwd=root,
            input=case.inputs,
            text=True,
            shell=True,
            stdout=subprocess.PIPE,
            stderr=subprocess.STDOUT,
            timeout=timeout,
            check=False,
        )
        return completed.stdout, completed.returncode, None
    except subprocess.TimeoutExpired as error:
        partial = error.stdout or ""
        if isinstance(partial, bytes):
            partial = partial.decode(errors="replace")
        return partial, None, f"command timed out after {timeout:g} seconds"
    except OSError as error:
        return "", None, f"could not start command: {error}"


def report_failure(case: TestCase, actual: str, expected: str, exit_code: int | None, diagnostic: str | None) -> None:
    """Print actual/expected output and stop the session after a failed case."""

    print("\n--- Expected output ---")
    print(format_block(expected))
    if diagnostic:
        print(f"--- Diagnostic ---\n{diagnostic}")
    if normalize_output(actual) != normalize_output(expected):
        diff = difflib.unified_diff(
            normalize_output(expected).splitlines(),
            normalize_output(actual).splitlines(),
            fromfile="expected",
            tofile="actual",
            lineterm="",
        )
        print("--- Output diff ---")
        print("\n".join(diff))
    if exit_code not in (None, 0):
        print(f"--- Non-zero exit code: {exit_code} ---")


def main() -> int:
    """Parse arguments, run cases in order, and fail fast."""

    root = repository_root()
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--plan", type=Path, default=Path("test/ui-test-plan.md"))
    parser.add_argument("--timeout", type=float, default=30.0)
    args = parser.parse_args()
    plan_path = args.plan if args.plan.is_absolute() else root / args.plan

    try:
        cases = parse_plan(plan_path)
    except (OSError, ValueError) as error:
        print(f"Test session could not start: {error}", file=sys.stderr)
        return 1

    print(f"UI test plan: {plan_path}")
    print(f"Cases: {len(cases)} (fail-fast)")
    for case in cases:
        actual, exit_code, diagnostic = run_case(case, root, args.timeout)
        show_session(case, actual, exit_code, "PASS" if not diagnostic and exit_code == 0 and normalize_output(actual) == normalize_output(case.expected_output) else "FAIL")
        if diagnostic or exit_code != 0 or normalize_output(actual) != normalize_output(case.expected_output):
            report_failure(case, actual, case.expected_output, exit_code, diagnostic)
            return 1

    print(f"\nTest session passed: {len(cases)} case(s).")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
