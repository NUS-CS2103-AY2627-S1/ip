#!/usr/bin/env python3
"""Run the command-line UI test cases described in a Markdown test plan."""

from __future__ import annotations

import argparse
import difflib
import re
import shlex
import subprocess
import sys
from dataclasses import dataclass
from pathlib import Path


@dataclass(frozen=True)
class TestCase:
    """One fresh-process UI session and the output it must produce."""

    name: str
    aim: str
    inputs: list[str]
    expected_output: str


def _read_fenced_section(case_body: str, title: str) -> str:
    """Return the contents of a named Markdown fenced section."""
    pattern = rf"###\s+{re.escape(title)}\s*\r?\n\s*```[^\r\n]*\r?\n(.*?)\r?\n```"
    match = re.search(pattern, case_body, flags=re.IGNORECASE | re.DOTALL)
    if match is None:
        raise ValueError(f"Missing fenced '{title}' section")
    return match.group(1)


def parse_plan(plan_path: Path) -> tuple[str, Path, list[TestCase]]:
    """Parse the program command, working directory, and test cases from Markdown."""
    text = plan_path.read_text(encoding="utf-8")

    command_match = re.search(r"^[-*]\s*Program command:\s*`([^`]+)`\s*$", text, re.MULTILINE)
    if command_match is None:
        raise ValueError("Missing '- Program command: `...`' entry")
    program_command = command_match.group(1)

    working_dir_match = re.search(r"^[-*]\s*Working directory:\s*`([^`]+)`\s*$", text, re.MULTILINE)
    repository_root = plan_path.parent.parent
    working_dir = Path(working_dir_match.group(1)) if working_dir_match else repository_root
    if not working_dir.is_absolute():
        working_dir = (repository_root / working_dir).resolve()

    case_matches = list(re.finditer(r"^##\s+Test case:\s*(.+?)\s*$", text, re.MULTILINE))
    if not case_matches:
        raise ValueError("The plan must contain at least one '## Test case:' section")

    cases: list[TestCase] = []
    for index, match in enumerate(case_matches):
        body_end = case_matches[index + 1].start() if index + 1 < len(case_matches) else len(text)
        body = text[match.end():body_end]
        aim_match = re.search(r"^[-*]\s*Aim:\s*(.+?)\s*$", body, re.MULTILINE)
        if aim_match is None:
            raise ValueError(f"Test case '{match.group(1)}' is missing an Aim entry")
        input_text = _read_fenced_section(body, "Inputs")
        expected_output = _read_fenced_section(body, "Expected output")
        cases.append(TestCase(match.group(1), aim_match.group(1), input_text.splitlines(), expected_output))

    return program_command, working_dir, cases


def _normalise_output(output: str) -> str:
    """Normalise platform line endings and ignore only trailing line endings."""
    return output.replace("\r\n", "\n").replace("\r", "\n").rstrip("\n")


def _format_diff(expected: str, actual: str) -> str:
    """Create a readable unified diff for a failed output comparison."""
    diff = difflib.unified_diff(
        expected.splitlines(),
        actual.splitlines(),
        fromfile="expected",
        tofile="actual",
        lineterm="",
    )
    return "\n".join(diff)


def _run_case(command: str, working_dir: Path, case: TestCase, timeout: float) -> tuple[str, str | None]:
    """Run one case and return combined output plus an optional process error."""
    input_text = "\n".join(case.inputs) + "\n"
    try:
        completed = subprocess.run(
            shlex.split(command, posix=False),
            cwd=working_dir,
            input=input_text,
            capture_output=True,
            text=True,
            timeout=timeout,
            check=False,
        )
    except subprocess.TimeoutExpired as error:
        partial = error.stdout or ""
        if isinstance(partial, bytes):
            partial = partial.decode(errors="replace")
        return partial, f"process exceeded the {timeout:g}-second timeout"

    output = completed.stdout + completed.stderr
    if completed.returncode != 0:
        return output, f"process exited with status {completed.returncode}"
    return output, None


def _transcript_entry(case: TestCase, actual: str) -> str:
    """Format one test session for both the console and the session log."""
    inputs = "\n".join(case.inputs)
    return (
        f"=== {case.name} ===\n"
        f"Aim: {case.aim}\n"
        f"--- console input ---\n{inputs}\n"
        f"--- console output ---\n{actual}"
    )


def main() -> int:
    """Run all plan cases, stopping at the first failure."""
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("plan", type=Path, help="Markdown UI test plan")
    parser.add_argument("--session-log", type=Path, help="Transcript output path")
    parser.add_argument("--timeout", type=float, default=30.0, help="Seconds allowed per case")
    args = parser.parse_args()

    plan_path = args.plan.resolve()
    log_path = (args.session_log or plan_path.parent / "ui-test-session.log").resolve()
    try:
        command, working_dir, cases = parse_plan(plan_path)
    except (OSError, ValueError) as error:
        print(f"ERROR: {error}", file=sys.stderr)
        return 2

    transcript: list[str] = []
    print(f"Program command: {command}")
    print(f"Working directory: {working_dir}")
    print(f"Test cases: {len(cases)}")

    for number, case in enumerate(cases, start=1):
        actual, process_error = _run_case(command, working_dir, case, args.timeout)
        entry = _transcript_entry(case, actual)
        transcript.append(entry)
        expected = case.expected_output
        failed = process_error is not None or _normalise_output(actual) != _normalise_output(expected)

        if failed:
            log_path.parent.mkdir(parents=True, exist_ok=True)
            log_path.write_text("\n\n".join(transcript) + "\n", encoding="utf-8")
            print(f"\nFAIL {number}/{len(cases)}: {case.name}")
            if process_error:
                print(f"Process error: {process_error}")
            print("\nExpected output:\n" + expected)
            print("\nActual output:\n" + actual)
            if process_error is None:
                print("\nDiff:\n" + (_format_diff(expected, actual) or "(no line diff)"))
            print(f"\nSession transcript: {log_path}")
            return 1

        print(f"PASS {number}/{len(cases)}: {case.name}")

    log_path.parent.mkdir(parents=True, exist_ok=True)
    log_path.write_text("\n\n".join(transcript) + "\n", encoding="utf-8")
    print("\nAll UI tests passed.")
    print("\nConsole input/output transcript:\n" + "\n\n".join(transcript))
    print(f"\nSession transcript: {log_path}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
