#!/usr/bin/env python3
"""Run the console UI test cases described in a Markdown test plan."""

from __future__ import annotations

import argparse
import os
import re
import shlex
import subprocess
import sys
from dataclasses import dataclass
from pathlib import Path


@dataclass
class TestCase:
    identifier: str
    title: str
    aim: str
    inputs: str
    expected: str


def clean_config_value(value: str) -> str:
    value = value.strip()
    if value.startswith("`") and value.endswith("`"):
        return value[1:-1]
    return value


def section_text(block: str, heading: str) -> str:
    pattern = rf"^###\s+{re.escape(heading)}\s*$([\s\S]*?)(?=^###\s|\Z)"
    match = re.search(pattern, block, re.IGNORECASE | re.MULTILINE)
    if not match:
        raise ValueError(f"missing '### {heading}' section")
    return match.group(1).strip()


def fenced_text(section: str, name: str) -> str:
    match = re.search(r"```[^\n]*\n([\s\S]*?)```", section)
    if not match:
        raise ValueError(f"the {name} section must contain a fenced text block")
    return match.group(1).rstrip("\r\n")


def parse_plan(path: Path) -> tuple[dict[str, str], list[TestCase]]:
    text = path.read_text(encoding="utf-8")
    config: dict[str, str] = {}
    for key in ("Program", "Working directory", "Setup"):
        match = re.search(rf"^\s*-\s*{re.escape(key)}:\s*(.+)$", text,
                          re.IGNORECASE | re.MULTILINE)
        if match:
            config[key.lower()] = clean_config_value(match.group(1))

    matches = list(re.finditer(
        r"^##\s+Test Case\s+([^:\s]+):\s*(.+)$", text, re.IGNORECASE | re.MULTILINE
    ))
    if not matches:
        raise ValueError("the plan contains no test cases")

    cases = []
    for index, match in enumerate(matches):
        block_end = matches[index + 1].start() if index + 1 < len(matches) else len(text)
        block = text[match.end():block_end]
        cases.append(TestCase(
            identifier=match.group(1),
            title=match.group(2).strip(),
            aim=section_text(block, "Aim"),
            inputs=fenced_text(section_text(block, "Inputs"), "Inputs"),
            expected=fenced_text(section_text(block, "Expected output"), "Expected output"),
        ))
    return config, cases


def command_args(command: str) -> list[str]:
    try:
        return shlex.split(command, posix=True)
    except ValueError as error:
        raise ValueError(f"cannot parse command {command!r}: {error}") from error


def run_command(command: str, cwd: Path, input_text: str, timeout: float) -> tuple[str, int | None, str | None]:
    try:
        completed = subprocess.run(
            command_args(command),
            cwd=cwd,
            input=input_text,
            text=True,
            capture_output=True,
            timeout=timeout,
            check=False,
        )
    except FileNotFoundError as error:
        return str(error), None, "command not found"
    except subprocess.TimeoutExpired as error:
        output = error.stdout or ""
        if isinstance(output, bytes):
            output = output.decode(errors="replace")
        return output, None, f"timed out after {timeout:g} seconds"

    output = completed.stdout
    if completed.stderr:
        output += "\n[stderr]\n" + completed.stderr
    return output, completed.returncode, None


def expected_lines(expected: str) -> list[str]:
    return [line.strip() for line in expected.replace("\r\n", "\n").split("\n") if line.strip()]


def missing_expected_lines(actual: str, expected: str) -> list[str]:
    actual_lines = actual.replace("\r\n", "\n").split("\n")
    cursor = 0
    missing = []
    for wanted in expected_lines(expected):
        found = False
        while cursor < len(actual_lines):
            if wanted in actual_lines[cursor].strip():
                found = True
                cursor += 1
                break
            cursor += 1
        if not found:
            missing.append(wanted)
            break
    return missing


def print_transcript(case: TestCase, actual: str) -> None:
    print(f"\n{'=' * 72}\n{case.identifier}: {case.title}")
    print(f"Aim: {case.aim}")
    print("\nCONSOLE INPUT")
    print(case.inputs if case.inputs else "(no input)")
    print("\nCONSOLE OUTPUT")
    print(actual if actual else "(no output)")


def main() -> int:
    parser = argparse.ArgumentParser(description="Run a Markdown console UI test plan.")
    parser.add_argument("--plan", default="test/ui-test-plan.md")
    parser.add_argument("--program", help="override the Program entry in the plan")
    parser.add_argument("--cwd", help="override the plan's working directory")
    parser.add_argument("--timeout", type=float, default=30.0)
    args = parser.parse_args()

    plan_path = Path(args.plan).resolve()
    try:
        config, cases = parse_plan(plan_path)
        program = args.program or config.get("program")
        if not program:
            raise ValueError("the plan must define a Program command")
        base_dir = plan_path.parent.parent
        cwd = Path(args.cwd or config.get("working directory", "."))
        if not cwd.is_absolute():
            cwd = (base_dir / cwd).resolve()
    except (OSError, ValueError) as error:
        print(f"ERROR: {error}", file=sys.stderr)
        return 2

    setup = config.get("setup")
    if setup:
        print(f"Running setup: {setup}")
        setup_output, setup_code, setup_error = run_command(setup, cwd, "", args.timeout)
        if setup_error or setup_code != 0:
            print("SETUP FAILED")
            print(setup_output)
            if setup_error:
                print(setup_error)
            return 1

    for case in cases:
        input_text = case.inputs
        if input_text and not input_text.endswith("\n"):
            input_text += "\n"
        actual, return_code, error = run_command(program, cwd, input_text, args.timeout)
        print_transcript(case, actual)

        missing = missing_expected_lines(actual, case.expected)
        if error or return_code != 0 or missing:
            print("\nFAIL")
            print("EXPECTED OUTPUT")
            print(case.expected)
            if missing:
                print("\nMISSING EXPECTED LINE(S)")
                print("\n".join(missing))
            if error:
                print(f"\nERROR: {error}")
            if return_code not in (None, 0):
                print(f"\nPROCESS EXIT CODE: {return_code}")
            return 1
        print("\nPASS")

    print(f"\nAll {len(cases)} UI test case(s) passed.")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
