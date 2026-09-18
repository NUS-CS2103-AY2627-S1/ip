#!/usr/bin/env python3
"""Run the Markdown-defined command-line UI test plan.

The plan intentionally uses a small Markdown subset instead of a dependency on
YAML or another parser. Each step has Command, Inputs, and Expected output
fenced blocks. Output is compared exactly and execution stops on the first
failure.
"""

from __future__ import annotations

import argparse
import re
import shlex
import subprocess
import sys
from dataclasses import dataclass
from pathlib import Path


CASE_HEADER = re.compile(r"^##\s+Test case\s+\d+\s*:\s*(.+?)\s*$")
STEP_HEADER = re.compile(r"^###\s+Step\s+\d+\s*:\s*(.+?)\s*$")
FIELD_HEADER = re.compile(r"^(Command|Inputs|Expected output):\s*$", re.IGNORECASE)


@dataclass
class Step:
    case_name: str
    case_aim: str
    step_name: str
    command: str
    inputs: str
    expected: str


def _fenced_value(lines: list[str], start: int, field: str, source: Path) -> tuple[str, int]:
    """Read the text fence immediately following a field label."""
    while start < len(lines) and not lines[start].strip():
        start += 1
    if start >= len(lines) or not lines[start].strip().startswith("```"):
        raise ValueError(f"{source}: {field} must be followed by a fenced code block")

    end = start + 1
    while end < len(lines) and not lines[end].strip().startswith("```"):
        end += 1
    if end == len(lines):
        raise ValueError(f"{source}: unterminated {field} code block")

    value = "\n".join(lines[start + 1 : end])
    if value:
        value += "\n"
    return value, end + 1


def parse_plan(path: Path) -> list[Step]:
    """Parse all test steps from the project's Markdown test plan."""
    lines = path.read_text(encoding="utf-8").splitlines()
    steps: list[Step] = []
    case_name: str | None = None
    case_aim: str | None = None
    step_name: str | None = None
    fields: dict[str, str] = {}
    i = 0

    def finish_step() -> None:
        nonlocal step_name, fields
        if step_name is None:
            return
        missing = [key for key in ("command", "inputs", "expected") if key not in fields]
        if case_name is None or case_aim is None:
            raise ValueError(f"{path}: step {step_name!r} is missing its test-case aim")
        if missing:
            raise ValueError(f"{path}: step {step_name!r} is missing: {', '.join(missing)}")
        steps.append(Step(case_name, case_aim, step_name, fields["command"], fields["inputs"], fields["expected"]))
        step_name = None
        fields = {}

    while i < len(lines):
        line = lines[i]
        case_match = CASE_HEADER.match(line)
        if case_match:
            finish_step()
            case_name = case_match.group(1)
            case_aim = None
            i += 1
            continue

        step_match = STEP_HEADER.match(line)
        if step_match:
            finish_step()
            if case_name is None:
                raise ValueError(f"{path}: step appears before a test-case heading")
            step_name = step_match.group(1)
            i += 1
            continue

        if case_name is not None and step_name is None and line.strip().lower().startswith("aim:"):
            case_aim = line.split(":", 1)[1].strip()
            i += 1
            continue

        field_match = FIELD_HEADER.match(line.strip())
        if field_match and step_name is not None:
            field = field_match.group(1).lower()
            key = {"command": "command", "inputs": "inputs", "expected output": "expected"}[field]
            value, i = _fenced_value(lines, i + 1, field_match.group(1), path)
            fields[key] = value
            continue

        i += 1

    finish_step()
    if not steps:
        raise ValueError(f"{path}: no test steps found")
    return steps


def display(value: str) -> str:
    """Make blank input/output and missing final newlines visible in the transcript."""
    return value if value else "<empty>\n"


def run_step(step: Step, root: Path, timeout: float) -> tuple[bool, str, str]:
    """Run one step and return (passed, actual, failure_reason)."""
    command_text = step.command.strip()
    try:
        command = shlex.split(command_text)
    except ValueError as exc:
        return False, "", f"invalid command syntax: {exc}"
    if not command:
        return False, "", "command is empty"

    try:
        result = subprocess.run(
            command,
            cwd=root,
            input=step.inputs,
            stdout=subprocess.PIPE,
            stderr=subprocess.STDOUT,
            text=True,
            timeout=timeout,
            check=False,
        )
    except FileNotFoundError:
        return False, "", f"command not found: {command[0]}"
    except subprocess.TimeoutExpired as exc:
        actual = exc.stdout or ""
        if isinstance(actual, bytes):
            actual = actual.decode("utf-8", errors="replace")
        return False, actual, f"command timed out after {timeout:g} seconds"
    if result.returncode != 0:
        return False, result.stdout, f"command exited with status {result.returncode}"
    if result.stdout != step.expected:
        return False, result.stdout, "output differs from expected output"
    return True, result.stdout, ""


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--plan", type=Path, default=Path("test/ui-test-plan.md"))
    parser.add_argument("--project-root", type=Path, default=Path("."))
    parser.add_argument("--timeout", type=float, default=30.0)
    args = parser.parse_args()

    plan = args.plan.resolve()
    root = args.project_root.resolve()
    try:
        steps = parse_plan(plan)
    except (OSError, ValueError) as exc:
        print(f"PLAN ERROR: {exc}", file=sys.stderr)
        return 2

    print(f"UI TEST SESSION: {plan}")
    print(f"Steps: {len(steps)}")
    print()
    for index, step in enumerate(steps, start=1):
        print(f"TEST {index}/{len(steps)}: {step.case_name} — {step.step_name}")
        print(f"Aim: {step.case_aim}")
        print(f"$ {step.command.strip()}")
        print("--- console input ---")
        print(display(step.inputs), end="")
        print("--- console output ---")
        passed, actual, reason = run_step(step, root, args.timeout)
        print(display(actual), end="")
        print("--- end console output ---")
        if not passed:
            print("RESULT: FAIL")
            print(f"Reason: {reason}")
            print("--- expected output ---")
            print(display(step.expected), end="")
            print("--- end expected output ---")
            print("SESSION TERMINATED: first failure")
            return 1
        print("RESULT: PASS")
        print()

    print(f"SESSION PASSED: {len(steps)} step(s)")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
