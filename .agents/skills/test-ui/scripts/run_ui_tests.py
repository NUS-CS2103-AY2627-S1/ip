#!/usr/bin/env python3
"""Run fail-fast interactive console tests stored in a Markdown test plan."""

from __future__ import annotations

import json
import os
import re
import selectors
import signal
import subprocess
import sys
import time
from pathlib import Path
from typing import Any


class TestFailure(Exception):
    """Indicate that a test cannot continue safely."""


def load_plan(path: Path) -> dict[str, Any]:
    """Extract and validate the machine-readable JSON plan from Markdown."""
    text = path.read_text(encoding="utf-8")
    heading = text.find("## Machine-readable plan")
    if heading < 0:
        raise TestFailure("Missing '## Machine-readable plan' heading.")
    match = re.search(r"```json\s*\n(.*?)\n```", text[heading:], re.DOTALL)
    if not match:
        raise TestFailure("Missing JSON code block after the machine-readable plan heading.")
    try:
        plan = json.loads(match.group(1))
    except json.JSONDecodeError as error:
        raise TestFailure(f"Invalid plan JSON: {error}") from error

    missing = sorted({"run_command", "test_cases"} - plan.keys())
    if missing:
        raise TestFailure(f"Missing plan field(s): {', '.join(missing)}")
    if not isinstance(plan["test_cases"], list) or not plan["test_cases"]:
        raise TestFailure("The plan must contain at least one test case.")
    return plan


def normalized(value: str, comparison: str) -> str:
    """Normalize line endings and apply the requested comparison mode."""
    value = value.replace("\r\n", "\n").replace("\r", "\n")
    if comparison == "trim-final-newline":
        return value.rstrip("\n")
    if comparison != "exact":
        raise TestFailure(f"Unsupported comparison mode: {comparison}")
    return value


def read_response(
    process: subprocess.Popen[bytes],
    selector: selectors.BaseSelector,
    timeout_seconds: float,
    idle_seconds: float,
) -> str:
    """Read combined process output until it becomes idle or exits."""
    chunks: list[bytes] = []
    deadline = time.monotonic() + timeout_seconds
    last_data = time.monotonic()

    while True:
        now = time.monotonic()
        if now >= deadline:
            raise TestFailure(f"Timed out after {timeout_seconds:g} seconds while waiting for output.")

        events = selector.select(min(idle_seconds, deadline - now))
        if events:
            try:
                chunk = os.read(process.stdout.fileno(), 65536)  # type: ignore[union-attr]
            except BlockingIOError:
                chunk = b""
            if chunk:
                chunks.append(chunk)
                last_data = time.monotonic()
                continue

        if process.poll() is not None:
            while True:
                try:
                    chunk = os.read(process.stdout.fileno(), 65536)  # type: ignore[union-attr]
                except BlockingIOError:
                    break
                if not chunk:
                    break
                chunks.append(chunk)
            break
        if time.monotonic() - last_data >= idle_seconds:
            break

    return b"".join(chunks).decode("utf-8", errors="replace")


def show_block(label: str, value: str) -> None:
    """Print an output block while keeping empty output visible."""
    print(f"--- {label} ---")
    if value:
        print(value, end="" if value.endswith("\n") else "\n")
    else:
        print("<empty>")
    print(f"--- end {label} ---")


def assert_output(actual: str, expected: Any, comparison: str, context: str) -> None:
    """Fail with both values when actual output differs from expected output."""
    if not isinstance(expected, str):
        raise TestFailure(f"{context}: expected output must be a string.")
    if normalized(actual, comparison) != normalized(expected, comparison):
        print(f"FAIL: {context}")
        show_block("actual output", actual)
        show_block("expected output", expected)
        raise TestFailure("Output mismatch; remaining tests were not run.")


def run_case(case: dict[str, Any], plan: dict[str, Any], plan_dir: Path) -> None:
    """Run one test case in a fresh process and stop at its first failed step."""
    case_id = case.get("id", "<missing id>")
    aim = case.get("aim", "<missing aim>")
    steps = case.get("steps")
    if not isinstance(case.get("id"), str) or not case["id"].strip():
        raise TestFailure("Every test case must have a non-empty string 'id'.")
    if not isinstance(case.get("aim"), str) or not case["aim"].strip():
        raise TestFailure(f"{case_id}: 'aim' must be a non-empty string.")
    if not isinstance(steps, list) or not steps:
        raise TestFailure(f"{case_id}: 'steps' must be a non-empty list.")

    cwd = (plan_dir / plan.get("working_directory", ".")).resolve()
    timeout = float(plan.get("timeout_seconds", 10))
    idle = float(plan.get("idle_milliseconds", 250)) / 1000
    comparison = plan.get("comparison", "exact")

    print(f"\n=== {case_id}: {aim} ===")
    process = subprocess.Popen(
        ["/bin/zsh", "-lc", plan["run_command"]],
        cwd=cwd,
        stdin=subprocess.PIPE,
        stdout=subprocess.PIPE,
        stderr=subprocess.STDOUT,
        start_new_session=True,
    )
    assert process.stdout is not None and process.stdin is not None
    os.set_blocking(process.stdout.fileno(), False)
    selector = selectors.DefaultSelector()
    selector.register(process.stdout, selectors.EVENT_READ)

    try:
        startup = read_response(process, selector, timeout, idle)
        print(startup, end="" if not startup or startup.endswith("\n") else "\n")
        assert_output(startup, case.get("expected_startup_output", ""), comparison, f"{case_id} startup")

        for index, step in enumerate(steps, start=1):
            command = step.get("input")
            if not isinstance(command, str):
                raise TestFailure(f"{case_id} step {index}: input must be a string.")
            if process.poll() is not None:
                raise TestFailure(f"{case_id} step {index}: program exited before input '{command}'.")

            print(f"> {command}")
            process.stdin.write((command + "\n").encode())
            process.stdin.flush()
            actual = read_response(process, selector, timeout, idle)
            print(actual, end="" if not actual or actual.endswith("\n") else "\n")
            assert_output(actual, step.get("expected_output"), comparison, f"{case_id} step {index} ({command})")
        print(f"PASS: {case_id}")
    finally:
        selector.close()
        if process.poll() is None:
            try:
                os.killpg(process.pid, signal.SIGTERM)
            except ProcessLookupError:
                pass
            else:
                try:
                    process.wait(timeout=1)
                except subprocess.TimeoutExpired:
                    try:
                        os.killpg(process.pid, signal.SIGKILL)
                    except ProcessLookupError:
                        pass
                    process.wait()


def main() -> int:
    """Load a plan, run cases in order, and return a useful process status."""
    if len(sys.argv) != 2:
        print(f"Usage: {Path(sys.argv[0]).name} <test-plan.md>", file=sys.stderr)
        return 2
    plan_path = Path(sys.argv[1]).resolve()
    try:
        plan = load_plan(plan_path)
        seen_ids: set[str] = set()
        for case in plan["test_cases"]:
            if not isinstance(case, dict):
                raise TestFailure("Every test case must be a JSON object.")
            case_id = case.get("id")
            if case_id in seen_ids:
                raise TestFailure(f"Duplicate test case ID: {case_id}")
            if isinstance(case_id, str):
                seen_ids.add(case_id)
            run_case(case, plan, plan_path.parent)
    except (OSError, ValueError, TestFailure) as error:
        print(f"\nTEST SESSION TERMINATED: {error}", file=sys.stderr)
        return 1
    print(f"\nALL TESTS PASSED ({len(plan['test_cases'])} test case(s)).")
    return 0


if __name__ == "__main__":
    sys.stdout.reconfigure(line_buffering=True)
    raise SystemExit(main())
