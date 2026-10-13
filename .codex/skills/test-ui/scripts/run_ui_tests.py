"""Run the console UI test cases described in a Markdown test plan.

The script intentionally keeps the test-plan format small and dependency-free so
students can inspect or adapt it easily. It runs cases in order and exits on the
first process or comparison failure.
"""

from __future__ import annotations

import argparse
from dataclasses import dataclass
from datetime import datetime, timezone
from pathlib import Path
import re
import subprocess
import sys
from typing import Iterable


CASE_HEADING = re.compile(r"^###\s+(TC-[^:]+):\s*(.+?)\s*$")
LEVEL_TWO_HEADING = re.compile(r"^##\s+.*$")
FENCE = re.compile(r"^\s*```(?:[^`]*)\s*$")


@dataclass(frozen=True)
class TestCase:
    """A single command, stdin payload, and expected console transcript."""

    case_id: str
    title: str
    aim: str
    command: str
    inputs: str
    expected_output: str


@dataclass
class TestRecord:
    """The observable result of one executed or skipped test case."""

    case: TestCase
    status: str
    actual_output: str = ""
    expected_output: str = ""
    return_code: int | None = None
    detail: str = ""


def normalize_output(value: str) -> str:
    """Apply the documented comparison normalization without hiding other differences."""

    normalized = value.replace("\r\n", "\n").replace("\r", "\n")
    return normalized[:-1] if normalized.endswith("\n") else normalized


def remove_inline_code(value: str) -> str:
    """Remove one pair of Markdown inline-code delimiters from a field value."""

    stripped = value.strip()
    if len(stripped) >= 2 and stripped.startswith("`") and stripped.endswith("`"):
        return stripped[1:-1]
    return stripped


def line_without_newline(value: str) -> str:
    """Return a line without its platform-specific line ending."""

    return value.rstrip("\r\n")


def find_bullet(lines: list[str], start: int, end: int, field: str) -> tuple[str, int]:
    """Find a required `- Field:` bullet and return its value and line number."""

    pattern = re.compile(rf"^\s*-\s*{re.escape(field)}:\s*(.*?)\s*$", re.IGNORECASE)
    for index in range(start, end):
        match = pattern.match(line_without_newline(lines[index]))
        if match:
            return match.group(1), index
    raise ValueError(f"missing `{field}` field")


def find_fenced_block(lines: list[str], label_line: int, end: int, field: str) -> str:
    """Read the fenced block immediately following a field bullet."""

    index = label_line + 1
    while index < end and not line_without_newline(lines[index]).strip():
        index += 1
    if index >= end or not FENCE.match(line_without_newline(lines[index])):
        raise ValueError(f"`{field}` must be followed by a fenced code block")

    index += 1
    content: list[str] = []
    while index < end:
        current = line_without_newline(lines[index])
        if current.strip() == "```":
            return "".join(content)
        content.append(lines[index])
        index += 1
    raise ValueError(f"unterminated `{field}` code block")


def parse_test_cases(plan_text: str) -> list[TestCase]:
    """Parse and validate all `### TC-...` sections before running anything."""

    lines = plan_text.splitlines(keepends=True)
    starts = [
        index
        for index, line in enumerate(lines)
        if CASE_HEADING.match(line_without_newline(line))
    ]
    if not starts:
        raise ValueError("the test plan contains no `### TC-...` test cases")

    cases: list[TestCase] = []
    for position, start in enumerate(starts):
        next_case = starts[position + 1] if position + 1 < len(starts) else len(lines)
        section_end = next_case
        for index in range(start + 1, next_case):
            if LEVEL_TWO_HEADING.match(line_without_newline(lines[index])):
                section_end = index
                break

        heading = CASE_HEADING.match(line_without_newline(lines[start]))
        assert heading is not None
        case_id, title = heading.groups()
        try:
            aim, _ = find_bullet(lines, start + 1, section_end, "Aim")
            command, _ = find_bullet(lines, start + 1, section_end, "Command")
            _, inputs_label_line = find_bullet(lines, start + 1, section_end, "Inputs")
            _, expected_label_line = find_bullet(
                lines, start + 1, section_end, "Expected output"
            )
            inputs = find_fenced_block(lines, inputs_label_line, section_end, "Inputs")
            expected = find_fenced_block(
                lines, expected_label_line, section_end, "Expected output"
            )
        except ValueError as error:
            raise ValueError(f"{case_id} is invalid: {error}") from error

        if not aim:
            raise ValueError(f"{case_id} is invalid: `Aim` must not be empty")
        if not command:
            raise ValueError(f"{case_id} is invalid: `Command` must not be empty")
        cases.append(
            TestCase(
                case_id=case_id,
                title=title,
                aim=aim,
                command=remove_inline_code(command),
                inputs=inputs,
                expected_output=expected,
            )
        )
    return cases


def read_java_version() -> tuple[bool, str]:
    """Check that the Java executable available to the session is Java 25."""

    try:
        result = subprocess.run(
            ["java", "-version"],
            stdout=subprocess.PIPE,
            stderr=subprocess.STDOUT,
            text=True,
            encoding="utf-8",
            errors="replace",
            check=False,
        )
    except OSError as error:
        return False, f"Java is not available: {error}"

    first_line = result.stdout.splitlines()[0] if result.stdout.splitlines() else ""
    version_match = re.search(r'version\s+["\'](\d+)', result.stdout)
    if result.returncode != 0 or version_match is None:
        return False, f"could not determine Java version from: {first_line or '<no output>'}"
    major = version_match.group(1)
    if major != "25":
        return False, f"Java 25 is required, but the active runtime is Java {major}"
    return True, first_line


def run_case(case: TestCase, cwd: Path, timeout: float) -> TestRecord:
    """Execute one case with combined console output and compare its transcript."""

    try:
        result = subprocess.run(
            case.command,
            cwd=cwd,
            shell=True,
            input=case.inputs,
            stdout=subprocess.PIPE,
            stderr=subprocess.STDOUT,
            text=True,
            encoding="utf-8",
            errors="replace",
            timeout=timeout,
            check=False,
        )
    except subprocess.TimeoutExpired as error:
        actual = error.stdout or ""
        if isinstance(actual, bytes):
            actual = actual.decode("utf-8", errors="replace")
        return TestRecord(
            case=case,
            status="FAIL",
            actual_output=actual,
            expected_output=case.expected_output,
            detail=f"timed out after {timeout:g} seconds",
        )
    except OSError as error:
        return TestRecord(
            case=case,
            status="FAIL",
            actual_output="",
            expected_output=case.expected_output,
            detail=f"could not start command: {error}",
        )

    actual = result.stdout or ""
    output_matches = normalize_output(actual) == normalize_output(case.expected_output)
    passed = result.returncode == 0 and output_matches
    if passed:
        detail = ""
    elif result.returncode != 0:
        detail = f"process exited with code {result.returncode}"
    else:
        detail = "console output did not match"
    return TestRecord(
        case=case,
        status="PASS" if passed else "FAIL",
        actual_output=actual,
        expected_output=case.expected_output,
        return_code=result.returncode,
        detail=detail,
    )


def display_text(value: str) -> str:
    """Make an empty console stream explicit while preserving non-empty text."""

    return value if value else "<empty>"


def format_record(record: TestRecord) -> str:
    """Format the input/output transcript for both terminal and Markdown recording."""

    case = record.case
    parts = [
        f"=== {case.case_id}: {case.title} ===",
        f"Command: {case.command}",
        "Console input:",
        display_text(case.inputs),
        "Console output:",
        display_text(record.actual_output),
        f"Status: {record.status}",
    ]
    if record.detail:
        parts.append(f"Detail: {record.detail}")
    if record.status == "FAIL":
        parts.extend(
            [
                "Expected output:",
                display_text(record.expected_output),
                "Actual output:",
                display_text(record.actual_output),
            ]
        )
    if record.status == "SKIPPED":
        parts.append("Console input/output: not run")
    return "\n".join(parts)


def format_transcript(records: Iterable[TestRecord]) -> str:
    """Join per-case records in the same order in which they were considered."""

    return "\n\n".join(format_record(record) for record in records)


def update_session_record(plan_text: str, session_text: str, summary: str) -> str:
    """Replace the latest-session section while preserving the test plan above it."""

    heading = "## Latest test session"
    section = (
        f"{heading}\n\n"
        f"- Recorded: {datetime.now(timezone.utc).astimezone().isoformat(timespec='seconds')}\n"
        f"- Result: {summary}\n\n"
        "````text\n"
        f"{session_text}\n"
        "````\n"
    )
    heading_match = re.search(r"^##\s+Latest test session\s*$", plan_text, re.MULTILINE)
    if heading_match is None:
        separator = "\n" if plan_text.endswith("\n") else "\n\n"
        return f"{plan_text}{separator}{section}"

    next_heading = re.search(
        r"^##\s+(?!#).*$", plan_text[heading_match.end() :], re.MULTILINE
    )
    section_end = heading_match.end() + next_heading.start() if next_heading else len(plan_text)
    prefix = plan_text[: heading_match.start()]
    suffix = plan_text[section_end:]
    if suffix and not suffix.startswith("\n"):
        suffix = "\n" + suffix
    return f"{prefix}{section}{suffix}"


def parse_args() -> argparse.Namespace:
    """Parse command-line options for the test runner."""

    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--plan", type=Path, required=True, help="Markdown UI test plan")
    parser.add_argument(
        "--cwd",
        type=Path,
        default=Path.cwd(),
        help="directory from which test commands are run (default: current directory)",
    )
    parser.add_argument(
        "--timeout",
        type=float,
        default=30.0,
        help="maximum seconds allowed for each test case (default: 30)",
    )
    return parser.parse_args()


def main() -> int:
    """Validate the plan, execute cases fail-fast, print, and record the transcript."""

    arguments = parse_args()
    plan_path = arguments.plan.resolve()
    cwd = arguments.cwd.resolve()
    if not plan_path.is_file():
        print(f"Test plan not found: {plan_path}", file=sys.stderr)
        return 2
    if not cwd.is_dir():
        print(f"Working directory not found: {cwd}", file=sys.stderr)
        return 2

    plan_text = plan_path.read_text(encoding="utf-8")
    try:
        cases = parse_test_cases(plan_text)
    except ValueError as error:
        print(f"Invalid UI test plan: {error}", file=sys.stderr)
        return 2

    java_ok, java_detail = read_java_version()
    if not java_ok:
        print(f"UI tests not run: {java_detail}", file=sys.stderr)
        return 2

    records: list[TestRecord] = []
    for case in cases:
        record = run_case(case, cwd, arguments.timeout)
        records.append(record)
        if record.status == "FAIL":
            break

    if len(records) < len(cases):
        for case in cases[len(records) :]:
            records.append(
                TestRecord(case=case, status="SKIPPED", detail="not run after the first failure")
            )

    failed = next((record for record in records if record.status == "FAIL"), None)
    overall = "FAIL" if failed else "PASS"
    transcript = format_transcript(records)
    passed_count = sum(record.status == "PASS" for record in records)
    failed_count = sum(record.status == "FAIL" for record in records)
    skipped_count = sum(record.status == "SKIPPED" for record in records)
    summary = (
        f"{overall} ({passed_count} passed, {failed_count} failed, "
        f"{skipped_count} skipped; {java_detail})"
    )
    print(f"Java runtime: {java_detail}")
    print(transcript)
    print(f"\nTest session result: {summary}")

    plan_path.write_text(
        update_session_record(plan_text, transcript, summary), encoding="utf-8"
    )
    return 1 if failed else 0


if __name__ == "__main__":
    raise SystemExit(main())
