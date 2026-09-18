#!/usr/bin/env python3
"""
Run console UI tests from test/ui-test-plan.md.

The plan provides a compile command, a run command, and test cases with input
and expected-output fenced blocks. The runner stops at the first failing test and
prints both expected and actual output for inspection.
"""
from __future__ import annotations

import argparse
import re
import subprocess
import sys
import tempfile
from dataclasses import dataclass
from pathlib import Path


DEFAULT_PLAN = Path("test/ui-test-plan.md")
DEFAULT_COMPILE = "javac -d /tmp/duck-ui-test src/main/java/*.java"
DEFAULT_RUN = "java -cp /tmp/duck-ui-test Duck"


@dataclass
class TestCase:
    name: str
    aim: str
    input_text: str
    expected_output: str
    initial_files: list[tuple[str, str]]
    expected_files: list[tuple[str, str]]


def normalize(text: str) -> str:
    """Normalize line endings and trailing spaces while preserving content."""
    text = text.replace("\r\n", "\n").replace("\r", "\n")
    lines = [line.rstrip() for line in text.split("\n")]
    return "\n".join(lines).strip()


def command_from_plan(plan_text: str, label: str, default: str) -> str:
    pattern = rf"^{re.escape(label)}:\s*`([^`]+)`\s*$"
    match = re.search(pattern, plan_text, flags=re.MULTILINE)
    return match.group(1).strip() if match else default


def fenced_block(body: str, heading: str) -> str:
    pattern = rf"^### {re.escape(heading)}\s*\n```[^\n]*\n(.*?)\n```"
    match = re.search(pattern, body, flags=re.MULTILINE | re.DOTALL)
    if not match:
        raise ValueError(f"Missing fenced block for '### {heading}'")
    return match.group(1)


def expected_file_blocks(body: str) -> list[tuple[str, str]]:
    """Return the expected path and content for each file assertion."""
    pattern = r"^### Expected File\s+(.+?)\s*\n```[^\n]*\n(.*?)\n```"
    return [
        (match.group(1).strip(), match.group(2))
        for match in re.finditer(pattern, body, flags=re.MULTILINE | re.DOTALL)
    ]


def initial_file_blocks(body: str) -> list[tuple[str, str]]:
    """Return the path and content for each file created before a test."""
    pattern = r"^### Initial File\s+(.+?)\s*\n```[^\n]*\n(.*?)\n```"
    return [
        (match.group(1).strip(), match.group(2))
        for match in re.finditer(pattern, body, flags=re.MULTILINE | re.DOTALL)
    ]


def parse_test_cases(plan_text: str) -> list[TestCase]:
    pattern = r"^## Test Case\s+\d*:?\s*(.*?)\s*\n(.*?)(?=^## Test Case\s+\d*:|\Z)"
    matches = re.finditer(pattern, plan_text, flags=re.MULTILINE | re.DOTALL)
    cases: list[TestCase] = []
    for match in matches:
        name = match.group(1).strip() or f"Test Case {len(cases) + 1}"
        body = match.group(2)
        aim_match = re.search(r"^Aim:\s*(.*?)\s*$", body, flags=re.MULTILINE)
        if not aim_match:
            raise ValueError(f"Missing Aim for test case '{name}'")
        cases.append(
            TestCase(
                name=name,
                aim=aim_match.group(1).strip(),
                input_text=fenced_block(body, "Input"),
                expected_output=fenced_block(body, "Expected Output"),
                initial_files=initial_file_blocks(body),
                expected_files=expected_file_blocks(body),
            )
        )
    if not cases:
        raise ValueError("No test cases found in the UI test plan")
    return cases


def run_shell(command: str, repo: Path, stdin: str | None = None) -> subprocess.CompletedProcess[str]:
    return subprocess.run(
        command,
        cwd=repo,
        input=stdin,
        shell=True,
        text=True,
        capture_output=True,
    )


def print_block(title: str, content: str) -> None:
    print(title)
    print("```text")
    print(content.rstrip())
    print("```")


def ensure_final_newline(text: str) -> str:
    return text if text.endswith("\n") else text + "\n"


def write_initial_files(session_path: Path, initial_files: list[tuple[str, str]]) -> None:
    """Create files needed by a test inside its isolated session directory."""
    for initial_path, initial_content in initial_files:
        file_path = session_path / initial_path
        file_path.parent.mkdir(parents=True, exist_ok=True)
        file_path.write_text(ensure_final_newline(initial_content), encoding="utf-8")


def main() -> int:
    parser = argparse.ArgumentParser(description="Run console UI tests from a Markdown test plan.")
    parser.add_argument("--plan", default=str(DEFAULT_PLAN), help="Path to the UI test plan")
    parser.add_argument("--repo", default=".", help="Repository root")
    args = parser.parse_args()

    repo = Path(args.repo).resolve()
    plan_path = (repo / args.plan).resolve()
    if not plan_path.exists():
        print(f"UI test plan not found: {plan_path}", file=sys.stderr)
        return 2

    plan_text = plan_path.read_text(encoding="utf-8")
    compile_command = command_from_plan(plan_text, "Compile command", DEFAULT_COMPILE)
    run_command = command_from_plan(plan_text, "Run command", DEFAULT_RUN)

    try:
        test_cases = parse_test_cases(plan_text)
    except ValueError as error:
        print(f"Invalid UI test plan: {error}", file=sys.stderr)
        return 2

    print(f"Using plan: {plan_path}")
    print(f"Compile command: {compile_command}")
    compile_result = run_shell(compile_command, repo)
    if compile_result.returncode != 0:
        print("Compilation failed.")
        print_block("Compiler stdout:", compile_result.stdout)
        print_block("Compiler stderr:", compile_result.stderr)
        return 1

    print(f"Run command: {run_command}")
    print(f"Loaded {len(test_cases)} test case(s).")

    for index, test_case in enumerate(test_cases, start=1):
        print()
        print(f"== Test {index}: {test_case.name} ==")
        print(f"Aim: {test_case.aim}")
        session_input = ensure_final_newline(test_case.input_text)
        with tempfile.TemporaryDirectory(prefix="duck-ui-test-") as session_directory:
            session_path = Path(session_directory)
            write_initial_files(session_path, test_case.initial_files)
            result = run_shell(run_command, session_path, stdin=session_input)
            actual_output = result.stdout

            print_block("Console input:", test_case.input_text)
            print_block("Console output:", actual_output)

            expected = normalize(test_case.expected_output)
            actual = normalize(actual_output)
            if result.returncode != 0 or actual != expected:
                print(f"FAIL: {test_case.name}")
                if result.returncode != 0:
                    print(f"Program exited with status {result.returncode}.")
                    print_block("Program stderr:", result.stderr)
                print_block("Expected output:", test_case.expected_output)
                print_block("Actual output:", actual_output)
                return 1

            for expected_path, expected_content in test_case.expected_files:
                file_path = session_path / expected_path
                actual_content = file_path.read_text(encoding="utf-8") if file_path.exists() else ""
                print_block(f"File output ({expected_path}):", actual_content)
                if normalize(actual_content) != normalize(expected_content):
                    print(f"FAIL: {test_case.name}")
                    print_block(f"Expected file ({expected_path}):", expected_content)
                    print_block(f"Actual file ({expected_path}):", actual_content)
                    return 1

            print(f"PASS: {test_case.name}")

    print()
    print(f"All {len(test_cases)} UI test case(s) passed.")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
