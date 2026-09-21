#!/usr/bin/env python3
"""Run the command-line UI cases recorded in a Markdown test plan."""

from __future__ import annotations

import argparse
import re
import subprocess
import sys
import textwrap
from pathlib import Path


CASE_HEADING = re.compile(r"^##\s+Test Case\b\s*(.*)$", re.IGNORECASE)
FIELD = re.compile(r"^\s*(?:[-*]\s*)?(Aim|Inputs?|Commands?|Expected output)\s*:\s*(.*)$", re.IGNORECASE)


def command_from_line(line: str) -> str:
    """Extract a command from inline code or from the text after a label."""
    match = re.search(r"`([^`]+)`", line)
    return match.group(1).strip() if match else line.strip()


def parse_plan(plan_path: Path) -> tuple[dict[str, str], list[dict[str, str]]]:
    """Parse global commands and test cases from the project's Markdown plan."""
    config: dict[str, str] = {}
    cases: list[dict[str, str]] = []
    current: dict[str, str] | None = None
    active_field: str | None = None
    in_fence = False
    fenced_lines: list[str] = []

    def finish_case() -> None:
        if current is not None:
            cases.append(current.copy())

    for raw_line in plan_path.read_text(encoding="utf-8").splitlines():
        heading = CASE_HEADING.match(raw_line)
        if heading and active_field is None:
            finish_case()
            current = {
                "name": heading.group(1).strip() or f"Test case {len(cases) + 1}",
                "aim": "",
                "inputs": "",
                "expected": "",
                "program": "",
            }
            continue

        if active_field is not None:
            if not in_fence:
                if raw_line.strip().startswith("```"):
                    in_fence = True
                    continue
                continue
            if raw_line.strip() == "```":
                target = "expected" if active_field == "expected" else active_field
                if current is not None:
                    current[target] = textwrap.dedent("\n".join(fenced_lines)).rstrip("\n")
                active_field = None
                in_fence = False
                fenced_lines = []
            else:
                fenced_lines.append(raw_line)
            continue

        field_match = FIELD.match(raw_line)
        if field_match:
            label = field_match.group(1).lower()
            value = field_match.group(2).strip()
            if label == "aim":
                if current is not None:
                    current["aim"] = value
            elif label in {"input", "inputs", "command", "commands"}:
                active_field = "inputs"
                fenced_lines = []
            else:
                active_field = "expected"
                fenced_lines = []
            continue

        if current is None:
            lowered = raw_line.lower()
            if "compile command:" in lowered:
                config["compile"] = command_from_line(raw_line.split(":", 1)[1])
            elif "program command:" in lowered:
                config["program"] = command_from_line(raw_line.split(":", 1)[1])
        elif raw_line.lower().strip().startswith("program command:"):
            current["program"] = command_from_line(raw_line.split(":", 1)[1])

    if active_field is not None and current is not None:
        target = "expected" if active_field == "expected" else active_field
        current[target] = textwrap.dedent("\n".join(fenced_lines)).rstrip("\n")
    finish_case()
    return config, cases


def display_block(text: str, stream=sys.stdout) -> None:
    """Print a block without adding an unwanted blank line."""
    print(text, end="" if text.endswith("\n") else "\n", file=stream)


def run_command(command: str, input_text: str, cwd: Path, timeout: float) -> tuple[int, str]:
    """Run one trusted project command and return its exit code and console output."""
    try:
        result = subprocess.run(
            command,
            input=input_text,
            capture_output=True,
            text=True,
            shell=True,
            cwd=cwd,
            timeout=timeout,
        )
    except subprocess.TimeoutExpired as error:
        output = (error.stdout or "") + (error.stderr or "")
        return -1, output + f"\n[Timed out after {timeout:g} seconds]"
    return result.returncode, result.stdout + result.stderr


def validate_cases(config: dict[str, str], cases: list[dict[str, str]]) -> list[str]:
    """Return plan-format errors before any command is executed."""
    errors: list[str] = []
    if not config.get("program"):
        errors.append("The plan must define a Program command.")
    if not cases:
        errors.append("The plan must contain at least one Test Case heading.")
    for number, case in enumerate(cases, start=1):
        if not case.get("aim"):
            errors.append(f"Test case {number} is missing an Aim.")
        if not case.get("inputs"):
            errors.append(f"Test case {number} is missing an Inputs code block.")
        if not case.get("expected"):
            errors.append(f"Test case {number} is missing an Expected output code block.")
    return errors


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("plan", nargs="?", default="test/ui-test-plan.md")
    parser.add_argument("--repo", default=".", help="Repository root (default: current directory)")
    parser.add_argument("--timeout", type=float, default=30.0, help="Timeout per command in seconds")
    args = parser.parse_args()

    repo = Path(args.repo).resolve()
    plan_path = (repo / args.plan).resolve() if not Path(args.plan).is_absolute() else Path(args.plan)
    config, cases = parse_plan(plan_path)
    errors = validate_cases(config, cases)
    if errors:
        for error in errors:
            print(f"Plan error: {error}", file=sys.stderr)
        return 2

    compile_command = config.get("compile")
    if compile_command:
        print(f"=== Compile: {compile_command} ===")
        return_code, compile_output = run_command(compile_command, "", repo, args.timeout)
        if compile_output:
            display_block(compile_output)
        if return_code != 0:
            print("Compilation failed; test session terminated.", file=sys.stderr)
            return 1

    for number, case in enumerate(cases, start=1):
        program = case.get("program") or config["program"]
        input_text = case["inputs"] + "\n"
        expected = case["expected"]
        print(f"\n=== Test Case {number}: {case['name']} ===")
        print(f"Aim: {case['aim']}")
        print("--- Console input ---")
        display_block(input_text)
        return_code, actual = run_command(program, input_text, repo, args.timeout)
        print("--- Console output ---")
        display_block(actual)

        actual_normalized = actual.rstrip("\n")
        expected_normalized = expected.rstrip("\n")
        if return_code != 0 or actual_normalized != expected_normalized:
            print("FAIL: test session terminated immediately.", file=sys.stderr)
            print("--- Expected output ---", file=sys.stderr)
            display_block(expected, sys.stderr)
            print("--- Actual output ---", file=sys.stderr)
            display_block(actual, sys.stderr)
            if return_code != 0:
                print(f"Process exit code: {return_code}", file=sys.stderr)
            return 1
        print("PASS")

    print(f"\nAll {len(cases)} UI test case(s) passed.")
    return 0


if __name__ == "__main__":
    sys.exit(main())
