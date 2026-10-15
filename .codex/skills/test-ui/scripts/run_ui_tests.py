#!/usr/bin/env python3
"""Run the project's scripted text UI tests and print each console session.

The test-plan format is intentionally small and Markdown-friendly. Each test
case contains an input block and an expected-output block. A fresh program
process is started for every case so that tests do not depend on execution
order or state left by a previous case.
"""
from __future__ import annotations

import argparse
import re
import shlex
import subprocess
import sys
from dataclasses import dataclass
from pathlib import Path


class PlanError(ValueError):
    """Raised when the Markdown test plan is missing required information."""


@dataclass(frozen=True)
class TestCase:
    """One self-contained UI test case from the Markdown test plan."""

    name: str
    aim: str
    inputs: str
    expected: str


@dataclass(frozen=True)
class TestPlan:
    """Parsed commands and test cases needed to run the UI test session."""

    program_command: list[str]
    setup_command: list[str] | None
    timeout_seconds: float
    cases: list[TestCase]


SETTING_RE = re.compile(r"^\s*-\s*([^:]+?)\s*:\s*(.*?)\s*$")
CASE_RE = re.compile(r"^\s*##\s+Test case\s+\d+\s*:\s*(.+?)\s*$", re.IGNORECASE)
AIM_RE = re.compile(r"^\s*-\s*Aim\s*:\s*(.*?)\s*$", re.IGNORECASE)


def inline_value(value: str) -> str:
    """Remove optional Markdown inline-code markers from a setting value."""
    value = value.strip()
    if len(value) >= 2 and value[0] == "`" and value[-1] == "`":
        return value[1:-1]
    return value


def command_parts(command: str, label: str) -> list[str]:
    """Split a plan command into executable arguments without invoking a shell."""
    try:
        parts = shlex.split(command, posix=True)
    except ValueError as exc:
        raise PlanError(f"{label} is not a valid command: {exc}") from exc
    if not parts:
        raise PlanError(f"{label} must not be empty")
    return parts


def setting(lines: list[str], wanted: str) -> str | None:
    """Find a top-level bullet setting by its case-insensitive label."""
    for line in lines:
        match = SETTING_RE.match(line)
        if match and match.group(1).strip().lower() == wanted.lower():
            return inline_value(match.group(2))
    return None


def parse_plan(path: Path) -> TestPlan:
    """Parse the required metadata and fenced test cases from a Markdown plan."""
    try:
        lines = path.read_text(encoding="utf-8").splitlines()
    except OSError as exc:
        raise PlanError(f"cannot read test plan {path}: {exc}") from exc

    program = setting(lines, "Program command")
    if program is None:
        raise PlanError("test plan must define a `Program command`")
    setup = setting(lines, "Setup command")
    timeout_text = setting(lines, "Timeout seconds")
    try:
        timeout_seconds = float(timeout_text) if timeout_text else 30.0
    except ValueError as exc:
        raise PlanError("`Timeout seconds` must be a positive number") from exc
    if timeout_seconds <= 0:
        raise PlanError("`Timeout seconds` must be a positive number")

    cases: list[TestCase] = []
    current: dict[str, object] | None = None
    section: str | None = None
    in_fence = False

    def finish_case() -> None:
        if current is None:
            return
        name = str(current["name"])
        aim = str(current.get("aim", "")).strip()
        inputs = "\n".join(current.get("inputs", []))  # type: ignore[arg-type]
        expected = "\n".join(current.get("expected", []))  # type: ignore[arg-type]
        if not aim:
            raise PlanError(f"{name} is missing an `Aim` line")
        if "inputs" not in current:
            raise PlanError(f"{name} is missing an `Inputs` section")
        if "expected" not in current:
            raise PlanError(f"{name} is missing an `Expected output` section")
        cases.append(TestCase(name, aim, inputs, expected))

    for line in lines:
        case_match = CASE_RE.match(line)
        if case_match:
            finish_case()
            current = {"name": f"Test case {len(cases) + 1}: {case_match.group(1)}"}
            section = None
            in_fence = False
            continue
        if current is None:
            continue

        aim_match = AIM_RE.match(line)
        if aim_match:
            current["aim"] = aim_match.group(1)
            continue

        heading = line.strip().lower()
        if heading.startswith("### inputs") or heading.startswith("### input commands"):
            section = "inputs"
            current.setdefault(section, [])
            in_fence = False
            continue
        if heading.startswith("### expected output"):
            section = "expected"
            current.setdefault(section, [])
            in_fence = False
            continue
        if section is not None and line.strip().startswith("```"):
            in_fence = not in_fence
            continue
        if section is not None and in_fence:
            current[section].append(line)  # type: ignore[union-attr]

    finish_case()
    if not cases:
        raise PlanError("test plan must contain at least one test case")
    return TestPlan(
        program_command=command_parts(program, "Program command"),
        setup_command=command_parts(setup, "Setup command") if setup else None,
        timeout_seconds=timeout_seconds,
        cases=cases,
    )


def comparable(text: str) -> str:
    """Normalize platform line endings while ignoring one final line ending."""
    normalized = text.replace("\r\n", "\n").replace("\r", "\n")
    return normalized[:-1] if normalized.endswith("\n") else normalized


def display(text: str) -> None:
    """Print captured console text without adding a confusing extra blank line."""
    if text:
        print(text, end="" if text.endswith(("\n", "\r")) else "\n")
    else:
        print("(no console output)")


def run_setup(plan: TestPlan, repo: Path) -> int:
    """Run the optional compile/setup command and print its console output."""
    if plan.setup_command is None:
        return 0
    print(f"\n=== Setup ===\n$ {shlex.join(plan.setup_command)}")
    try:
        result = subprocess.run(
            plan.setup_command,
            cwd=repo,
            capture_output=True,
            text=True,
            encoding="utf-8",
            errors="replace",
            timeout=plan.timeout_seconds,
        )
    except (OSError, subprocess.TimeoutExpired) as exc:
        print(f"Setup failed: {exc}", file=sys.stderr)
        return 1
    display(result.stdout + result.stderr)
    if result.returncode != 0:
        print(f"Setup failed with exit code {result.returncode}", file=sys.stderr)
        return result.returncode or 1
    return 0


def run_case(index: int, case: TestCase, plan: TestPlan, repo: Path) -> bool:
    """Run one case, print its transcript, and return whether it passed."""
    input_text = case.inputs
    if input_text and not input_text.endswith("\n"):
        input_text += "\n"
    print(f"\n=== {case.name} ===")
    print(f"Aim: {case.aim}")
    print("--- Console input ---")
    display(input_text)

    try:
        result = subprocess.run(
            plan.program_command,
            cwd=repo,
            input=input_text,
            capture_output=True,
            text=True,
            encoding="utf-8",
            errors="replace",
            timeout=plan.timeout_seconds,
        )
        actual = result.stdout + result.stderr
        return_code = result.returncode
    except subprocess.TimeoutExpired as exc:
        partial = exc.stdout or ""
        if isinstance(partial, bytes):
            partial = partial.decode("utf-8", errors="replace")
        actual = str(partial)
        return_code = None
        print(f"Program timed out after {plan.timeout_seconds:g} seconds.")
    except OSError as exc:
        actual = f"Could not start program: {exc}"
        return_code = None

    print("--- Console output ---")
    display(actual)
    passed = comparable(actual) == comparable(case.expected) and return_code == 0
    if passed:
        print(f"PASS: {case.name}")
        return True

    print(f"FAIL: {case.name} (stopping immediately)")
    if return_code not in (None, 0):
        print(f"Actual exit code: {return_code}")
    print("--- Actual output ---")
    display(actual)
    print("--- Expected output ---")
    display(case.expected)
    return False


def main(argv: list[str]) -> int:
    """Parse arguments, run setup, and stop at the first failed case."""
    parser = argparse.ArgumentParser(description="Run the project's text UI test plan.")
    parser.add_argument("--plan", default="test/ui-test-plan.md", help="Markdown test plan")
    parser.add_argument("--repo", default=".", help="repository root (default: current directory)")
    args = parser.parse_args(argv)
    repo = Path(args.repo).expanduser().resolve()
    plan_path = (repo / args.plan).resolve() if not Path(args.plan).is_absolute() else Path(args.plan)

    try:
        plan = parse_plan(plan_path)
    except PlanError as exc:
        print(f"Test-plan error: {exc}", file=sys.stderr)
        return 2

    print(f"Running {len(plan.cases)} UI test case(s) from {plan_path}")
    setup_status = run_setup(plan, repo)
    if setup_status != 0:
        return 2
    for index, case in enumerate(plan.cases, start=1):
        if not run_case(index, case, plan, repo):
            return 1
    print(f"\nAll {len(plan.cases)} UI test case(s) passed.")
    return 0


if __name__ == "__main__":
    raise SystemExit(main(sys.argv[1:]))
