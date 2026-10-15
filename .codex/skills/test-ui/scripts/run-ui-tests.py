#!/usr/bin/env python3
"""Run console UI tests described by a Markdown test plan."""

from __future__ import annotations

import argparse
import re
import subprocess
import sys
from dataclasses import dataclass
from pathlib import Path


class PlanError(ValueError):
    """Raised when a test plan does not follow the required structure."""


@dataclass
class TestCase:
    """A single console UI test case parsed from the test plan."""

    identifier: str
    title: str
    aim: str
    inputs: str
    expected_output: str


def normalize_output(text: str) -> str:
    """Normalizes line endings and terminal newlines for output comparison."""
    return text.replace("\r\n", "\n").replace("\r", "\n").rstrip("\n")


def fenced_block(text: str, heading: str) -> str:
    """Returns the text code block directly below a Markdown heading."""
    pattern = rf"(?ms)^{re.escape(heading)}\s*\n\s*```text\n(.*?)\n```"
    match = re.search(pattern, text)
    if not match:
        raise PlanError(f"missing a text code block below '{heading}'")
    return match.group(1)


def shell_block(text: str, heading: str) -> str:
    """Returns the shell code block directly below a Markdown heading."""
    pattern = rf"(?ms)^{re.escape(heading)}\s*\n\s*```sh\n(.*?)\n```"
    match = re.search(pattern, text)
    if not match:
        raise PlanError(f"missing a shell code block below '{heading}'")
    return match.group(1)


def parse_test_cases(plan: str) -> list[TestCase]:
    """Parses all required test-case fields from the Markdown plan."""
    cases: list[TestCase] = []
    sections = re.split(r"(?m)^### ", plan)[1:]
    for section in sections:
        heading, _, body = section.partition("\n")
        match = re.fullmatch(r"(TC-[\w-]+):\s*(.+)", heading.strip())
        if not match:
            continue
        aim_match = re.search(r"(?m)^\*\*Aim:\*\*\s*(.+)$", body)
        if not aim_match:
            raise PlanError(f"{match.group(1)} is missing an **Aim:** field")
        cases.append(TestCase(
            identifier=match.group(1),
            title=match.group(2),
            aim=aim_match.group(1).strip(),
            inputs=fenced_block(body, "#### Inputs"),
            expected_output=fenced_block(body, "#### Expected output"),
        ))
    if not cases:
        raise PlanError("no test cases were found")
    return cases


def print_block(label: str, content: str) -> None:
    """Prints one labelled console transcript block."""
    print(f"--- {label} ---")
    print(content if content else "<empty>")


def run_shell(command: str, working_directory: Path, inputs: str | None = None) -> subprocess.CompletedProcess[str]:
    """Runs a configured shell command using zsh for SDKMAN compatibility."""
    return subprocess.run(
        command,
        shell=True,
        executable="/bin/zsh",
        cwd=working_directory,
        input=None if inputs is None else inputs + "\n",
        capture_output=True,
        text=True,
    )


def main() -> int:
    """Builds the program and runs each planned UI test in order."""
    parser = argparse.ArgumentParser(description="Run console UI tests from a Markdown plan.")
    parser.add_argument("plan", type=Path, help="path to the Markdown UI test plan")
    args = parser.parse_args()

    plan_path = args.plan.resolve()
    try:
        plan = plan_path.read_text(encoding="utf-8")
        working_directory_text = re.search(
            r"(?m)^\*\*Working directory:\*\*\s*`([^`]+)`\s*$", plan
        )
        if not working_directory_text:
            raise PlanError("missing **Working directory:** field")
        working_directory = (plan_path.parent.parent / working_directory_text.group(1)).resolve()
        build_command = shell_block(plan, "### Build command")
        run_command = shell_block(plan, "### Run command")
        test_cases = parse_test_cases(plan)
    except (OSError, PlanError) as error:
        print(f"Plan error: {error}", file=sys.stderr)
        return 2

    print("=== Building program ===")
    build = run_shell(build_command, working_directory)
    if build.returncode != 0:
        print_block("Build output", build.stdout)
        print_block("Build errors", build.stderr)
        return 1

    for test_case in test_cases:
        print(f"\n=== {test_case.identifier}: {test_case.title} ===")
        result = run_shell(run_command, working_directory, test_case.inputs)
        actual_output = normalize_output(result.stdout)
        expected_output = normalize_output(test_case.expected_output)

        print_block("Console input", test_case.inputs)
        print_block("Console output", actual_output)
        if result.returncode != 0 or actual_output != expected_output:
            print("RESULT: FAILED")
            print(f"Aim: {test_case.aim}")
            print_block("Expected output", expected_output)
            print_block("Actual output", actual_output)
            if result.stderr:
                print_block("Program errors", result.stderr)
            return 1
        print("RESULT: PASSED")

    print("\nAll UI test cases passed.")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
