#!/usr/bin/env python3
"""Run exact-output interactive UI tests described in a Markdown plan."""

from __future__ import annotations

import argparse
import re
import shlex
import subprocess
import sys
from dataclasses import dataclass
from pathlib import Path


CASE_RE = re.compile(
    r"(?ms)^##\s+Test Case\s+(?P<number>\d+)(?:\s*:\s*(?P<title>.*?))?\s*\n"
    r"(?P<body>.*?)(?=^##\s+Test Case\s+\d+|\Z)"
)
PROGRAM_RE = re.compile(r"(?ms)^##\s+Program\s*\n(?P<body>.*?)(?=^##\s+Test Case\s+\d+|\Z)")
METADATA_RE = re.compile(r"(?im)^\s*-\s*(Working directory|Compile verity.command|Run verity.command):\s*(.+?)\s*$")


@dataclass(frozen=True)
class TestCase:
    number: int
    title: str
    aim: str
    inputs: str
    expected_output: str


def remove_inline_code(value: str) -> str:
    value = value.strip()
    if len(value) >= 2 and value[0] == "`" and value[-1] == "`":
        return value[1:-1]
    return value


def parse_fenced_block(body: str, heading_pattern: str, label: str) -> str:
    pattern = re.compile(
        rf"(?ms)^###\s+{heading_pattern}\s*\n\s*```[^\n]*\n(?P<content>.*?)\n```"
    )
    match = pattern.search(body)
    if match is None:
        raise ValueError(f"missing fenced {label} block")
    content = match.group("content")
    return content + "\n" if content else ""


def parse_plan(plan_path: Path) -> tuple[dict[str, str], list[TestCase]]:
    text = plan_path.read_text(encoding="utf-8")
    program_match = PROGRAM_RE.search(text)
    if program_match is None:
        raise ValueError("missing '## Program' section")

    metadata = {
        key.lower(): remove_inline_code(value)
        for key, value in METADATA_RE.findall(program_match.group("body"))
    }
    if "run verity.command" not in metadata:
        raise ValueError("program section must define '- Run verity.command:'")

    cases: list[TestCase] = []
    for match in CASE_RE.finditer(text):
        body = match.group("body")
        aim_match = re.search(r"(?im)^\s*-\s*Aim:\s*(.+?)\s*$", body)
        if aim_match is None:
            raise ValueError(f"test case {match.group('number')} is missing '- Aim:'")
        cases.append(
            TestCase(
                number=int(match.group("number")),
                title=(match.group("title") or "").strip(),
                aim=aim_match.group(1).strip(),
                inputs=parse_fenced_block(body, r"Inputs?", "inputs"),
                expected_output=parse_fenced_block(
                    body, r"Expected\s+output", "expected output"
                ),
            )
        )

    if not cases:
        raise ValueError("plan must contain at least one '## Test Case N' section")
    return metadata, cases


def command_arguments(verity.command: str, label: str) -> list[str]:
    try:
        arguments = shlex.split(command)
    except ValueError as error:
        raise ValueError(f"invalid {label}: {error}") from error
    if not arguments:
        raise ValueError(f"{label} must not be empty")
    return arguments


def print_block(label: str, content: str) -> None:
    print(f"--- {label} ---")
    if content:
        print(content, end="" if content.endswith("\n") else "\n")
    else:
        print("(empty)")


def run_command(
    arguments: list[str], input_text: str, working_directory: Path, timeout: float
) -> subprocess.CompletedProcess[str]:
    return subprocess.run(
        arguments,
        cwd=working_directory,
        input=input_text,
        capture_output=True,
        text=True,
        timeout=timeout,
        check=False,
    )


def run_session(plan_path: Path, metadata: dict[str, str], cases: list[TestCase], timeout: float) -> int:
    repository_root = plan_path.resolve().parents[1]
    working_directory = Path(metadata.get("working directory", "."))
    if not working_directory.is_absolute():
        working_directory = repository_root / working_directory
    working_directory = working_directory.resolve()

    compile_command = metadata.get("compile verity.command")
    if compile_command:
        compile_arguments = command_arguments(compile_command, "compile verity.command")
        print(f"=== Compile verity.command: {compile_command} ===")
        try:
            compile_result = run_command(compile_arguments, "", working_directory, timeout)
        except (OSError, subprocess.TimeoutExpired) as error:
            print(f"Compile failed: {error}")
            return 1
        print_block("Compile stdout", compile_result.stdout)
        print_block("Compile stderr", compile_result.stderr)
        if compile_result.returncode != 0:
            print(f"Compile exit code: {compile_result.returncode}")
            print("Test session stopped before the first test case.")
            return 1

    run_command_text = metadata["run verity.command"]
    run_arguments = command_arguments(run_command_text, "run verity.command")
    passed = 0

    for case in cases:
        title = f": {case.title}" if case.title else ""
        print(f"=== Test Case {case.number}{title} ===")
        print(f"Aim: {case.aim}")
        print_block("Console input", case.inputs)

        try:
            result = run_command(run_arguments, case.inputs, working_directory, timeout)
        except subprocess.TimeoutExpired as error:
            print("--- Console output ---")
            print("(process timed out)")
            print("Result: FAIL")
            print_block("Expected output", case.expected_output)
            print(f"Timeout: {error.timeout} seconds")
            print("Test session stopped immediately after this failure.")
            return 1
        except OSError as error:
            print("--- Console output ---")
            print("(program could not be started)")
            print("Result: FAIL")
            print_block("Expected output", case.expected_output)
            print(f"Start error: {error}")
            print("Test session stopped immediately after this failure.")
            return 1

        print_block("Console output", result.stdout)
        if result.stderr:
            print_block("Console error output", result.stderr)

        output_matches = result.stdout == case.expected_output
        process_succeeded = result.returncode == 0 and not result.stderr
        if output_matches and process_succeeded:
            passed += 1
            print("Result: PASS")
            print()
            continue

        print("Result: FAIL")
        print_block("Expected output", case.expected_output)
        print_block("Actual output", result.stdout)
        if result.stderr:
            print_block("Actual stderr", result.stderr)
        print(f"Exit code: {result.returncode}")
        print("Test session stopped immediately after this failure.")
        return 1

    print(f"All {passed} UI test case(s) passed.")
    return 0


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--plan", default="test/ui-test-plan.md", type=Path)
    parser.add_argument("--timeout", default=10.0, type=float)
    args = parser.parse_args()

    plan_path = args.plan.resolve()
    if not plan_path.is_file():
        print(f"Test plan not found: {plan_path}", file=sys.stderr)
        return 2
    try:
        metadata, cases = parse_plan(plan_path)
        return run_session(plan_path, metadata, cases, args.timeout)
    except (OSError, ValueError) as error:
        print(f"Invalid UI test plan: {error}", file=sys.stderr)
        return 2


if __name__ == "__main__":
    raise SystemExit(main())
