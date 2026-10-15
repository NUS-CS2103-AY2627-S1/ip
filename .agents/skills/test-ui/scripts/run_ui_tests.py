#!/usr/bin/env python3
"""Run exact console UI tests described in test/ui-test-plan.md."""

from __future__ import annotations

import argparse
import difflib
import re
import shlex
import shutil
import subprocess
import sys
import tempfile
from dataclasses import dataclass
from pathlib import Path


@dataclass(frozen=True)
class TestCase:
    """One independent console session and its expected standard output."""

    name: str
    aim: str
    inputs: str
    expected_output: str


def find_repo_root(start: Path) -> Path:
    """Find the nearest parent containing the repository's .git directory."""
    for candidate in (start, *start.parents):
        if (candidate / ".git").exists():
            return candidate
    raise ValueError("Run this script from inside the project repository.")


def read_config(plan_text: str, label: str) -> str:
    """Read a backtick-delimited configuration value from the plan."""
    pattern = rf"^- \*\*{re.escape(label)}:\*\* `([^`]*)`\s*$"
    match = re.search(pattern, plan_text, flags=re.MULTILINE)
    if not match:
        raise ValueError(f"Missing plan configuration: {label}")
    return match.group(1)


def read_block(case_text: str, label: str) -> str:
    """Read a text code block belonging to a test-case field."""
    pattern = rf"\*\*{re.escape(label)}:\*\*\s*\n```text\n(.*?)\n```"
    match = re.search(pattern, case_text, flags=re.DOTALL)
    if not match:
        raise ValueError(f"Missing {label} block")
    return match.group(1)


def parse_test_cases(plan_text: str) -> list[TestCase]:
    """Parse all level-three Markdown headings as test cases."""
    headings = list(re.finditer(r"^### (.+)$", plan_text, flags=re.MULTILINE))
    cases: list[TestCase] = []

    for index, heading in enumerate(headings):
        end = headings[index + 1].start() if index + 1 < len(headings) else len(plan_text)
        case_text = plan_text[heading.end():end]
        aim_match = re.search(r"^\*\*Aim:\*\* (.+)$", case_text, flags=re.MULTILINE)
        if not aim_match:
            raise ValueError(f"{heading.group(1)} is missing its Aim")
        try:
            inputs = read_block(case_text, "Inputs")
            expected = read_block(case_text, "Expected output")
        except ValueError as error:
            raise ValueError(f"{heading.group(1)}: {error}") from error
        cases.append(TestCase(heading.group(1), aim_match.group(1), inputs, expected))

    if not cases:
        raise ValueError("The plan contains no test cases.")
    return cases


def normalize_output(output: str) -> str:
    """Normalize platform newlines and ignore one final newline."""
    normalized = output.replace("\r\n", "\n").replace("\r", "\n")
    return normalized[:-1] if normalized.endswith("\n") else normalized


def print_text_block(label: str, content: str) -> None:
    """Print a transcript block without changing its contents."""
    print(f"--- {label} ---")
    print(content, end="" if content.endswith("\n") else "\n")


def check_java_version(required_major: str) -> None:
    """Fail before compilation when the active Java major version is wrong."""
    result = subprocess.run(
        ["java", "-version"], capture_output=True, text=True, check=False
    )
    version_text = result.stderr + result.stdout
    match = re.search(r'version "(\d+)', version_text)
    actual_major = match.group(1) if match else "unknown"
    if result.returncode != 0 or actual_major != required_major:
        raise RuntimeError(
            f"Java {required_major} is required, but the active Java major version is "
            f"{actual_major}.\n{version_text}"
        )


def reset_persisted_state(working_directory: Path) -> None:
    """Delete Clearblue's saved-task data folder so the next run starts empty.

    The plan guarantees each test case sees a fresh process with an empty
    task list, but Clearblue now persists tasks to data/clearblue.txt across
    runs. Removing that folder before every case keeps the guarantee true.
    """
    data_directory = working_directory / "data"
    if data_directory.exists():
        shutil.rmtree(data_directory)


def run_shell(command: str, working_directory: Path, **kwargs: object) -> subprocess.CompletedProcess[str]:
    """Run a configured plan command through zsh."""
    return subprocess.run(
        command,
        cwd=working_directory,
        shell=True,
        executable="/bin/zsh",
        text=True,
        check=False,
        **kwargs,
    )


def run_tests(plan_path: Path) -> int:
    """Compile the application, execute cases in order, and stop on failure."""
    repo_root = find_repo_root(Path.cwd().resolve())
    resolved_plan = plan_path if plan_path.is_absolute() else repo_root / plan_path
    plan_text = resolved_plan.read_text(encoding="utf-8")

    compile_command = read_config(plan_text, "Compile command")
    run_command = read_config(plan_text, "Run command")
    working_directory = repo_root / read_config(plan_text, "Working directory")
    required_java = read_config(plan_text, "Required Java major version")
    timeout_seconds = float(read_config(plan_text, "Timeout seconds"))
    test_cases = parse_test_cases(plan_text)

    try:
        displayed_plan = resolved_plan.relative_to(repo_root)
    except ValueError:
        displayed_plan = resolved_plan
    print(f"UI test plan: {displayed_plan}")
    print(f"Test cases: {len(test_cases)}")

    try:
        check_java_version(required_java)
    except RuntimeError as error:
        print("\nSETUP FAILED")
        print(error)
        return 1

    with tempfile.TemporaryDirectory(prefix="clearblue-ui-test-") as classes_directory:
        quoted_classes_directory = shlex.quote(classes_directory)
        compile_command = compile_command.replace("{classes_dir}", quoted_classes_directory)
        run_command = run_command.replace("{classes_dir}", quoted_classes_directory)

        compile_result = run_shell(
            compile_command,
            working_directory,
            capture_output=True,
            timeout=timeout_seconds,
        )
        if compile_result.returncode != 0:
            print("\nCOMPILATION FAILED")
            print_text_block("Compiler standard output", compile_result.stdout)
            print_text_block("Compiler standard error", compile_result.stderr)
            return 1

        for number, test_case in enumerate(test_cases, start=1):
            print(f"\n=== {test_case.name} ===")
            print(f"Aim: {test_case.aim}")
            console_input = test_case.inputs + "\n"
            print_text_block("Console input", console_input)

            reset_persisted_state(working_directory)

            try:
                result = run_shell(
                    run_command,
                    working_directory,
                    input=console_input,
                    capture_output=True,
                    timeout=timeout_seconds,
                )
            except subprocess.TimeoutExpired as error:
                actual_output = error.stdout or ""
                print_text_block("Actual console output", actual_output)
                print(f"RESULT: FAIL (timed out after {timeout_seconds:g} seconds)")
                print_text_block("Expected output", test_case.expected_output)
                print(f"Stopped immediately. {len(test_cases) - number} later case(s) were not run.")
                return 1

            print_text_block("Actual console output", result.stdout)
            actual = normalize_output(result.stdout)
            expected = normalize_output(test_case.expected_output)

            failure_reason = None
            if result.returncode != 0:
                failure_reason = f"program exited with status {result.returncode}"
            elif result.stderr:
                failure_reason = "program wrote to standard error"
            elif actual != expected:
                failure_reason = "standard output did not match"

            if failure_reason:
                print(f"RESULT: FAIL ({failure_reason})")
                if result.stderr:
                    print_text_block("Actual standard error", result.stderr)
                print_text_block("Expected output", test_case.expected_output)
                print("--- Output diff (expected -> actual) ---")
                print(
                    "".join(
                        difflib.unified_diff(
                            expected.splitlines(keepends=True),
                            actual.splitlines(keepends=True),
                            fromfile="expected",
                            tofile="actual",
                        )
                    ),
                    end="",
                )
                print(f"Stopped immediately. {len(test_cases) - number} later case(s) were not run.")
                return 1

            print("RESULT: PASS")

        reset_persisted_state(working_directory)

    print(f"\nTEST SESSION PASSED: {len(test_cases)} of {len(test_cases)} cases passed.")
    return 0


def main() -> int:
    """Parse command-line arguments and run the selected UI test plan."""
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument(
        "--plan",
        type=Path,
        default=Path("test/ui-test-plan.md"),
        help="test-plan path relative to the repository root",
    )
    arguments = parser.parse_args()

    try:
        return run_tests(arguments.plan)
    except (OSError, ValueError) as error:
        print(f"TEST PLAN ERROR: {error}", file=sys.stderr)
        return 2


if __name__ == "__main__":
    raise SystemExit(main())
