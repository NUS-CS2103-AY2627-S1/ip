#!/usr/bin/env python3
"""Run command-line UI sessions described in test/ui-test-plan.md."""

from __future__ import annotations

import argparse
import re
import subprocess
import sys
from pathlib import Path


def read_section(text: str, heading_pattern: str) -> str:
    """Return the body of the first matching level-three Markdown section."""
    match = re.search(
        rf"^###\s+{heading_pattern}\s*$\n(.*?)(?=^###\s|\Z)",
        text,
        re.MULTILINE | re.DOTALL,
    )
    if not match:
        raise ValueError(f"missing section matching '### {heading_pattern}'")
    return match.group(1).strip("\n")


def read_code_block(section: str, label: str) -> str:
    """Return the contents of the first fenced code block in a section."""
    match = re.search(r"```[^\n]*\n(.*?)```", section, re.DOTALL)
    if not match:
        raise ValueError(f"missing fenced code block for {label}")
    return match.group(1).rstrip("\n")


def parse_plan(plan_text: str) -> tuple[str, str, list[dict[str, str]]]:
    """Parse commands and test cases from the project UI test plan."""
    compile_match = re.search(r"^-\s*Compile command:\s*(.+)$", plan_text, re.MULTILINE)
    run_match = re.search(r"^-\s*Run command:\s*(.+)$", plan_text, re.MULTILINE)
    if not compile_match or not run_match:
        raise ValueError("plan must define compile and run commands")

    cases = []
    case_pattern = re.compile(
        r"^##\s+Test case:\s*(.+?)\s*$\n(.*?)(?=^##\s|\Z)",
        re.MULTILINE | re.DOTALL,
    )
    for match in case_pattern.finditer(plan_text):
        body = match.group(2)
        cases.append(
            {
                "name": match.group(1),
                "aim": read_section(body, r"Aim"),
                "inputs": read_code_block(read_section(body, r"Inputs?"), "inputs"),
                "expected": read_code_block(
                    read_section(body, r"Expected output(?:\s*\(.*\))?"),
                    "expected output",
                ),
            }
        )
    if not cases:
        raise ValueError("plan must contain at least one test case")
    compile_command = compile_match.group(1).strip().strip("`").strip()
    run_command_text = run_match.group(1).strip().strip("`").strip()
    return compile_command, run_command_text, cases


def run_command(command: str, repo: Path, input_text: str | None = None) -> subprocess.CompletedProcess[str]:
    """Run a configured shell command in the repository root."""
    return subprocess.run(
        command,
        cwd=repo,
        input=input_text,
        text=True,
        capture_output=True,
        shell=True,
        timeout=60,
        check=False,
    )


def expected_lines_are_in_order(actual: str, expected: str) -> bool:
    """Check every non-empty expected line against complete actual output lines."""
    actual_lines = actual.splitlines()
    cursor = 0
    for expected_line in expected.splitlines():
        if not expected_line:
            continue
        try:
            cursor = actual_lines.index(expected_line, cursor) + 1
        except ValueError:
            return False
    return True


def combined_output(result: subprocess.CompletedProcess[str]) -> str:
    """Combine stdout and stderr for a useful session record."""
    output = result.stdout
    if result.stderr:
        output += ("\n" if output else "") + result.stderr
    return output


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("repo", nargs="?", default=".", help="repository root")
    parser.add_argument(
        "plan",
        nargs="?",
        default="test/ui-test-plan.md",
        help="path to the Markdown UI test plan, relative to the repository",
    )
    args = parser.parse_args()

    repo = Path(args.repo).expanduser().resolve()
    plan_path = (repo / args.plan).resolve()
    try:
        compile_command, run_command_text, cases = parse_plan(plan_path.read_text(encoding="utf-8"))
    except (OSError, ValueError) as error:
        print(f"UI test plan error: {error}", file=sys.stderr)
        return 2

    print(f"Compiling with: {compile_command}")
    compile_result = run_command(compile_command, repo)
    if compile_result.returncode != 0:
        print("\n********** BUILD FAILURE **********")
        print(combined_output(compile_result), end="")
        return 1

    print(f"Running {len(cases)} UI test case(s) from {plan_path}")
    for number, case in enumerate(cases, start=1):
        session_input = case["inputs"] + "\n"
        result = run_command(run_command_text, repo, session_input)
        actual_output = combined_output(result)
        passed = result.returncode == 0 and expected_lines_are_in_order(
            actual_output, case["expected"]
        )

        print(f"\n=== Test case {number}: {case['name']} ===")
        print(f"Aim: {case['aim']}")
        print("--- Console input ---")
        print(session_input, end="")
        print("--- Console output ---")
        print(actual_output, end="" if actual_output.endswith("\n") else "\n")

        if not passed:
            print("--- Expected output ---")
            print(case["expected"])
            print(f"FAIL: stopping after test case {number}.")
            return 1
        print("PASS")

    print(f"\nAll {len(cases)} UI test case(s) passed.")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
