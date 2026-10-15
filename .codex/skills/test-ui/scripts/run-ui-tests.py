#!/usr/bin/env python3
"""Run console UI tests described in a Markdown test plan."""

from __future__ import annotations

import re
import subprocess
import sys
from dataclasses import dataclass
from pathlib import Path


TEST_HEADING = re.compile(r"^## Test case:\s*(.+?)\s*$", re.MULTILINE)
SECTION_HEADING = re.compile(r"^### (Aim|Setup|Input|Expected output)\s*$", re.MULTILINE)
CONFIG_LINE = re.compile(
    r"^(run-command|setup-command|before-each-command|working-directory|timeout-seconds):\s*(.*?)\s*$",
    re.MULTILINE,
)


@dataclass
class TestCase:
    """Represent one console session and its expected result."""

    name: str
    aim: str
    setup_command: str | None
    console_input: str
    expected_output: str


def normalise_output(text: str) -> str:
    """Normalise line endings while ignoring a final terminal newline."""
    return text.replace("\r\n", "\n").replace("\r", "\n").rstrip("\n")


def read_code_block(text: str, section_name: str, case_name: str) -> str:
    """Return the fenced code block following a named section heading."""
    heading = re.search(rf"^### {re.escape(section_name)}\s*$", text, re.MULTILINE)
    if heading is None:
        raise ValueError(f"{case_name}: missing '### {section_name}' section")

    after_heading = text[heading.end():]
    opening = re.search(r"^```[^\n]*\n", after_heading, re.MULTILINE)
    if opening is None:
        raise ValueError(f"{case_name}: {section_name} must contain a fenced code block")

    content_start = opening.end()
    closing = re.search(r"^```\s*$", after_heading[content_start:], re.MULTILINE)
    if closing is None:
        raise ValueError(f"{case_name}: unterminated {section_name} code block")
    return after_heading[content_start:content_start + closing.start()].rstrip("\n")


def read_aim(text: str, case_name: str) -> str:
    """Return the prose immediately following the Aim heading."""
    heading = re.search(r"^### Aim\s*$", text, re.MULTILINE)
    if heading is None:
        raise ValueError(f"{case_name}: missing '### Aim' section")
    following = text[heading.end():]
    next_heading = SECTION_HEADING.search(following)
    aim = following[:next_heading.start() if next_heading else len(following)].strip()
    if not aim:
        raise ValueError(f"{case_name}: aim must not be empty")
    return aim


def read_optional_code_block(text: str, section_name: str) -> str | None:
    """Return an optional fenced code block following a named section heading."""
    heading = re.search(rf"^### {re.escape(section_name)}\s*$", text, re.MULTILINE)
    if heading is None:
        return None
    after_heading = text[heading.end():]
    opening = re.search(r"^```[^\n]*\n", after_heading, re.MULTILINE)
    if opening is None:
        raise ValueError(f"{section_name} must contain a fenced code block")
    content_start = opening.end()
    closing = re.search(r"^```\s*$", after_heading[content_start:], re.MULTILINE)
    if closing is None:
        raise ValueError(f"{section_name} has an unterminated code block")
    return after_heading[content_start:content_start + closing.start()].rstrip("\n")


def parse_test_cases(plan_text: str) -> list[TestCase]:
    """Parse all test case sections from the plan."""
    visible_plan = re.sub(r"<!--.*?-->", "", plan_text, flags=re.DOTALL)
    headings = list(TEST_HEADING.finditer(visible_plan))
    cases = []
    for index, heading in enumerate(headings):
        end = headings[index + 1].start() if index + 1 < len(headings) else len(visible_plan)
        case_text = visible_plan[heading.end():end]
        name = heading.group(1)
        cases.append(TestCase(
            name=name,
            aim=read_aim(case_text, name),
            setup_command=read_optional_code_block(case_text, "Setup"),
            console_input=read_code_block(case_text, "Input", name),
            expected_output=read_code_block(case_text, "Expected output", name),
        ))
    if not cases:
        raise ValueError("No active '## Test case:' sections were found in the plan.")
    return cases


def parse_config(plan_text: str, plan_path: Path) -> tuple[str, str | None, str | None, Path, float]:
    """Read runner settings from top-level plan metadata."""
    config = {match.group(1): match.group(2) for match in CONFIG_LINE.finditer(plan_text)}
    run_command = config.get("run-command")
    if not run_command:
        raise ValueError("Missing required 'run-command:' in the test plan.")

    relative_directory = config.get("working-directory", ".")
    working_directory = (plan_path.parent / relative_directory).resolve()
    if not working_directory.is_dir():
        raise ValueError(f"Working directory does not exist: {working_directory}")

    try:
        timeout = float(config.get("timeout-seconds", "10"))
    except ValueError as error:
        raise ValueError("'timeout-seconds' must be a number.") from error
    if timeout <= 0:
        raise ValueError("'timeout-seconds' must be greater than zero.")
    return run_command, config.get("setup-command"), config.get("before-each-command"), working_directory, timeout


def format_block(title: str, content: str) -> str:
    """Format a transcript field as a Markdown code block."""
    return f"### {title}\n\n```text\n{content}\n```\n"


def write_record(record_path: Path, entries: list[str]) -> None:
    """Write the complete test-session transcript."""
    record_path.write_text("# UI Test Record\n\n" + "\n".join(entries), encoding="utf-8")


def run_command(command: str, working_directory: Path, console_input: str,
                timeout: float) -> subprocess.CompletedProcess[str]:
    """Run one shell command with captured console input and output."""
    return subprocess.run(
        command,
        shell=True,
        cwd=working_directory,
        input=console_input + "\n",
        capture_output=True,
        text=True,
        timeout=timeout,
        check=False,
    )


def main() -> int:
    """Run the plan and stop at the first mismatch."""
    if len(sys.argv) != 2:
        print("Usage: run-ui-tests.py PATH_TO_UI_TEST_PLAN.md", file=sys.stderr)
        return 2

    plan_path = Path(sys.argv[1]).resolve()
    record_path = plan_path.with_name("ui-test-record.md")
    try:
        plan_text = plan_path.read_text(encoding="utf-8")
        run_target, setup_command, before_each_command, working_directory, timeout = parse_config(plan_text, plan_path)
        cases = parse_test_cases(plan_text)
    except (OSError, ValueError) as error:
        print(f"Test plan error: {error}", file=sys.stderr)
        return 2

    entries = [f"Plan: `{plan_path}`", f"Run command: `{run_target}`"]
    if setup_command:
        try:
            setup = run_command(setup_command, working_directory, "", timeout)
        except subprocess.TimeoutExpired:
            entries.extend(["## Setup", "**Result:** FAIL (timed out)"])
            write_record(record_path, entries)
            print(f"Setup timed out. Record: {record_path}", file=sys.stderr)
            return 1
        if setup.returncode != 0:
            entries.extend(["## Setup", "**Result:** FAIL", format_block("Output", setup.stdout + setup.stderr)])
            write_record(record_path, entries)
            print(f"Setup failed. Record: {record_path}", file=sys.stderr)
            return 1

    for number, case in enumerate(cases, start=1):
        try:
            if before_each_command:
                cleanup = run_command(before_each_command, working_directory, "", timeout)
                if cleanup.returncode != 0:
                    raise RuntimeError(cleanup.stdout + cleanup.stderr)
            if case.setup_command:
                preparation = run_command(case.setup_command, working_directory, "", timeout)
                if preparation.returncode != 0:
                    raise RuntimeError(preparation.stdout + preparation.stderr)
            result = run_command(run_target, working_directory, case.console_input, timeout)
            actual_output = result.stdout
            if result.stderr:
                actual_output += result.stderr
            passed = result.returncode == 0 and normalise_output(actual_output) == normalise_output(case.expected_output)
        except subprocess.TimeoutExpired as error:
            actual_output = (error.stdout or "") + (error.stderr or "")
            result = None
            passed = False
        except RuntimeError as error:
            actual_output = str(error)
            result = None
            passed = False

        entry = [f"## Test {number}: {case.name}", f"**Aim:** {case.aim}"]
        entry.append(format_block("Console input", case.console_input))
        entry.append(format_block("Expected output", case.expected_output))
        entry.append(format_block("Actual output", actual_output))
        if result is None:
            entry.append("**Exit code:** timed out\n")
        else:
            entry.append(f"**Exit code:** {result.returncode}\n")
        entry.append("**Result:** PASS\n" if passed else "**Result:** FAIL\n")
        entries.extend(entry)
        write_record(record_path, entries)

        if not passed:
            print(f"FAIL: Test {number} - {case.name}", file=sys.stderr)
            print("Expected output:", file=sys.stderr)
            print(case.expected_output, file=sys.stderr)
            print("Actual output:", file=sys.stderr)
            print(actual_output, file=sys.stderr)
            print(f"Record: {record_path}", file=sys.stderr)
            return 1
        print(f"PASS: Test {number} - {case.name}")

    print(f"All {len(cases)} UI test(s) passed. Record: {record_path}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
