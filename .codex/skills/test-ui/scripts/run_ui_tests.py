#!/usr/bin/env python3
"""Run console UI test sessions defined in a Markdown test plan."""

from __future__ import annotations

import argparse
import difflib
import re
import shlex
import subprocess
import sys
import tempfile
from dataclasses import dataclass
from pathlib import Path


DEFAULT_PLAN = Path("test/ui-test-plan.md")
TEST_CASE_HEADING = re.compile(r"^##\s+Test case:\s*(?P<name>.+?)\s*$", re.IGNORECASE | re.MULTILINE)


class PlanError(ValueError):
    """Describe a test-plan format problem that prevents safe test execution."""


@dataclass(frozen=True)
class TestCase:
    """Represent one test case with an initial and optional restarted console session."""

    name: str
    aim: str
    command: str
    inputs: str
    restart_inputs: str | None
    expected_output: str


@dataclass(frozen=True)
class TestPlan:
    """Represent the setup instructions and test sessions from a test plan."""

    setup_command: str | None
    timeout_seconds: float
    cases: list[TestCase]


@dataclass(frozen=True)
class CommandResult:
    """Store the observable result of launching one program command."""

    output: str
    return_code: int | None
    timed_out: bool


def normalize_newlines(text: str) -> str:
    """Make comparison portable without hiding whitespace differences."""
    return text.replace("\r\n", "\n").replace("\r", "\n")


def inline_value(section: str, label: str) -> str:
    """Return a required backtick-delimited field from one Markdown section."""
    pattern = re.compile(
        rf"^\s*(?:[-*]\s+)?\*\*{re.escape(label)}:\*\*\s*`(?P<value>[^`]+)`\s*$",
        re.IGNORECASE | re.MULTILINE,
    )
    match = pattern.search(section)
    if not match:
        raise PlanError(f"Missing `{label}` field.")
    return match.group("value").strip()


def optional_inline_value(section: str, label: str) -> str | None:
    """Return an optional backtick-delimited field from a Markdown section."""
    pattern = re.compile(
        rf"^\s*(?:[-*]\s+)?\*\*{re.escape(label)}:\*\*\s*`(?P<value>[^`]+)`\s*$",
        re.IGNORECASE | re.MULTILINE,
    )
    match = pattern.search(section)
    return match.group("value").strip() if match else None


def single_line_value(section: str, label: str) -> str:
    """Return a required plain-text field from one Markdown section."""
    pattern = re.compile(
        rf"^\s*\*\*{re.escape(label)}:\*\*\s*(?P<value>\S.*?)\s*$",
        re.IGNORECASE | re.MULTILINE,
    )
    match = pattern.search(section)
    if not match:
        raise PlanError(f"Missing `{label}` field.")
    return match.group("value").strip()


def fenced_value(section: str, label: str) -> str:
    """Return the content of a required fenced-code field from a section."""
    pattern = re.compile(
        rf"^\s*\*\*{re.escape(label)}:\*\*\s*\n```[^\n]*\n(?P<value>.*?)^```\s*$",
        re.IGNORECASE | re.MULTILINE | re.DOTALL,
    )
    match = pattern.search(section)
    if not match:
        raise PlanError(f"Missing fenced `{label}` block.")
    return normalize_newlines(match.group("value"))


def optional_fenced_value(section: str, label: str) -> str | None:
    """Return an optional fenced-code field from one test-plan section."""
    pattern = re.compile(
        rf"^\s*\*\*{re.escape(label)}:\*\*\s*\n```[^\n]*\n(?P<value>.*?)^```\s*$",
        re.IGNORECASE | re.MULTILINE | re.DOTALL,
    )
    match = pattern.search(section)
    return normalize_newlines(match.group("value")) if match else None


def parse_plan(plan_path: Path) -> TestPlan:
    """Parse the supported Markdown test-plan format into executable sessions."""
    try:
        content = normalize_newlines(plan_path.read_text(encoding="utf-8"))
    except OSError as error:
        raise PlanError(f"Cannot read {plan_path}: {error}") from error

    headings = list(TEST_CASE_HEADING.finditer(content))
    if not headings:
        raise PlanError("Add at least one `## Test case: <id>` section.")

    environment = content[:headings[0].start()]
    setup_command = optional_inline_value(environment, "Setup command")
    timeout_text = optional_inline_value(environment, "Timeout seconds") or "5"
    try:
        timeout_seconds = float(timeout_text)
    except ValueError as error:
        raise PlanError("`Timeout seconds` must be a positive number.") from error
    if timeout_seconds <= 0:
        raise PlanError("`Timeout seconds` must be greater than zero.")

    cases: list[TestCase] = []
    for index, heading in enumerate(headings):
        end = headings[index + 1].start() if index + 1 < len(headings) else len(content)
        section = content[heading.end() : end]
        name = heading.group("name").strip()
        if not name:
            raise PlanError("Every test case needs an id after `Test case:`.")
        cases.append(
            TestCase(
                name=name,
                aim=single_line_value(section, "Aim"),
                command=inline_value(section, "Run command"),
                inputs=fenced_value(section, "Inputs"),
                restart_inputs=optional_fenced_value(section, "Restart inputs"),
                expected_output=fenced_value(section, "Expected output"),
            )
        )

    return TestPlan(setup_command, timeout_seconds, cases)


def run_command(command: str, inputs: str, timeout_seconds: float, workspace: Path) -> CommandResult:
    """Run a shell command and collect the combined console output."""
    try:
        completed = subprocess.run(
            command,
            cwd=workspace,
            input=inputs,
            text=True,
            shell=True,
            stdout=subprocess.PIPE,
            stderr=subprocess.STDOUT,
            timeout=timeout_seconds,
            check=False,
        )
    except subprocess.TimeoutExpired as error:
        output = error.stdout or ""
        if isinstance(output, bytes):
            output = output.decode(errors="replace")
        return CommandResult(normalize_newlines(output), None, True)

    return CommandResult(normalize_newlines(completed.stdout), completed.returncode, False)


def quote_path(path: Path) -> str:
    """Quote a path for the shell that runs the test command on the current platform."""
    path_text = str(path)
    return subprocess.list2cmdline([path_text]) if sys.platform == "win32" else shlex.quote(path_text)


def expand_command(command: str, workspace: Path, test_directory: Path) -> str:
    """Replace supported path placeholders with shell-safe absolute paths."""
    return (
        command.replace("{workspace}", quote_path(workspace))
        .replace("{test_dir}", quote_path(test_directory))
    )


def show_text(text: str) -> None:
    """Print text exactly once, including a final newline when needed."""
    if not text:
        print("[no output]")
    else:
        print(text, end="" if text.endswith("\n") else "\n")


def show_input(command: str, inputs: str) -> None:
    """Print the launch command and each line sent to the program."""
    print("Console input:")
    print(f"$ {command}")
    if not inputs:
        print("[no input]")
        return
    for line in inputs.splitlines():
        print(f"> {line}")


def show_difference(expected: str, actual: str) -> None:
    """Print a compact unified diff for unequal output."""
    print("Difference (expected -> actual):")
    diff = difflib.unified_diff(
        expected.splitlines(keepends=True),
        actual.splitlines(keepends=True),
        fromfile="expected",
        tofile="actual",
    )
    diff_text = "".join(diff)
    show_text(diff_text or "[output text matched; another failure condition occurred]")


def run_setup(plan: TestPlan, workspace: Path) -> bool:
    """Run the optional setup command and stop before UI tests if it fails."""
    if not plan.setup_command:
        return True

    print("=== Setup ===")
    print(f"$ {plan.setup_command}")
    result = run_command(plan.setup_command, "", plan.timeout_seconds, workspace)
    show_text(result.output)
    if result.timed_out:
        print(f"Setup timed out after {plan.timeout_seconds:g} seconds. No UI test was run.")
        return False
    if result.return_code != 0:
        print(f"Setup exited with status {result.return_code}. No UI test was run.")
        return False
    return True


def run_case(case: TestCase, timeout_seconds: float, workspace: Path) -> bool:
    """Run one test case and display a full transcript and failure diagnostics."""
    with tempfile.TemporaryDirectory(prefix="console-ui-test-") as temporary_directory:
        command = expand_command(case.command, workspace, Path(temporary_directory))
        sessions = [("Initial session", case.inputs)]
        if case.restart_inputs is not None:
            sessions.append(("Restarted session", case.restart_inputs))

        print(f"=== Test case: {case.name} ===")
        print(f"Aim: {case.aim}")
        results: list[CommandResult] = []
        for session_name, session_inputs in sessions:
            if len(sessions) > 1:
                print(f"{session_name}:")
            show_input(command, session_inputs)
            result = run_command(command, session_inputs, timeout_seconds, workspace)
            results.append(result)
            print("Console output:")
            show_text(result.output)
            if result.timed_out or result.return_code != 0:
                break

        actual_output = "".join(result.output for result in results)
        output_matches = actual_output == case.expected_output
        all_sessions_succeeded = len(results) == len(sessions) and all(
            not result.timed_out and result.return_code == 0 for result in results
        )
        if all_sessions_succeeded and output_matches:
            print("Result: PASS")
            return True

        print("Result: FAIL — stopping the test session immediately.")
        timed_out_result = next((result for result in results if result.timed_out), None)
        failed_result = next((result for result in results if result.return_code not in (0, None)), None)
        if timed_out_result:
            print(f"The program timed out after {timeout_seconds:g} seconds.")
        elif failed_result:
            print(f"Program exit status: {failed_result.return_code}")
        print("Expected console output:")
        show_text(case.expected_output)
        print("Actual console output:")
        show_text(actual_output)
        if not output_matches:
            show_difference(case.expected_output, actual_output)
        return False


def main() -> int:
    """Load a plan, run its setup, and fail fast through its test cases."""
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--plan", type=Path, default=DEFAULT_PLAN, help="Markdown test plan to run")
    parser.add_argument(
        "--workspace",
        type=Path,
        default=Path.cwd(),
        help="Directory in which plan commands run (default: current directory)",
    )
    arguments = parser.parse_args()
    plan_path = arguments.plan.resolve()
    workspace = arguments.workspace.resolve()

    try:
        plan = parse_plan(plan_path)
    except PlanError as error:
        print(f"Test plan error: {error}", file=sys.stderr)
        return 2

    if not workspace.is_dir():
        print(f"Workspace does not exist: {workspace}", file=sys.stderr)
        return 2
    if not run_setup(plan, workspace):
        return 1

    for case in plan.cases:
        if not run_case(case, plan.timeout_seconds, workspace):
            return 1

    print(f"All {len(plan.cases)} UI test case(s) passed.")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
