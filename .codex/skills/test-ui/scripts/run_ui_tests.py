#!/usr/bin/env python3
"""Run fail-fast console UI tests described in a Markdown test plan."""

from __future__ import annotations

import argparse
import json
import re
import subprocess
import sys
import tempfile
from pathlib import Path
from typing import Any


def parse_args() -> argparse.Namespace:
    """Parse command-line arguments."""
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument(
        "--plan",
        type=Path,
        default=Path("test/ui-test-plan.md"),
        help="Markdown plan containing one fenced JSON object",
    )
    return parser.parse_args()


def load_plan(plan_path: Path) -> dict[str, Any]:
    """Load and validate the machine-readable JSON block in the plan."""
    plan_text = plan_path.read_text(encoding="utf-8")
    match = re.search(r"```json\s*(\{.*?\})\s*```", plan_text, re.DOTALL)
    if match is None:
        raise ValueError("the plan must contain one fenced JSON object")

    plan = json.loads(match.group(1))
    if not isinstance(plan.get("main_class"), str):
        raise ValueError("main_class must be a string")
    if not isinstance(plan.get("test_cases"), list) or not plan["test_cases"]:
        raise ValueError("test_cases must be a non-empty list")

    seen_ids: set[str] = set()
    for case in plan["test_cases"]:
        if not isinstance(case, dict):
            raise ValueError("each test case must be an object")
        case_id = case.get("id")
        if not isinstance(case_id, str) or not case_id:
            raise ValueError("each test case must have a non-empty id")
        if case_id in seen_ids:
            raise ValueError(f"duplicate test case id: {case_id}")
        seen_ids.add(case_id)
        if not isinstance(case.get("aim"), str) or not case["aim"]:
            raise ValueError(f"{case_id}: aim must be a non-empty string")
        if not isinstance(case.get("inputs"), list) or not all(
            isinstance(item, str) for item in case["inputs"]
        ):
            raise ValueError(f"{case_id}: inputs must be a list of strings")
        if not isinstance(case.get("expected_output"), list) or not all(
            isinstance(item, str) for item in case["expected_output"]
        ):
            raise ValueError(
                f"{case_id}: expected_output must be a list of strings"
            )
    return plan


def require_java_25() -> None:
    """Fail clearly unless the active Java compiler is version 25."""
    result = subprocess.run(
        ["javac", "-version"], capture_output=True, text=True, check=False
    )
    version_text = (result.stdout + result.stderr).strip()
    if result.returncode != 0 or re.search(r"\bjavac 25(?:\.|\b)", version_text) is None:
        raise RuntimeError(
            "Java 25 is required; activate it with "
            "`sdk use java 25.0.3.fx-zulu` and retry "
            f"(active compiler: {version_text or 'unavailable'})"
        )


def compile_program(source_dir: Path, output_dir: Path) -> None:
    """Compile every Java source file under the source root."""
    sources = sorted(source_dir.rglob("*.java"))
    if not sources:
        raise RuntimeError(f"no Java sources found in {source_dir}")
    result = subprocess.run(
        ["javac", "-d", str(output_dir), *(str(path) for path in sources)],
        capture_output=True,
        text=True,
        check=False,
    )
    if result.returncode != 0:
        raise RuntimeError("compilation failed:\n" + result.stdout + result.stderr)


def normalize_newlines(text: str | bytes) -> str:
    """Normalize platform line endings without changing other whitespace."""
    if isinstance(text, bytes):
        text = text.decode("utf-8", errors="replace")
    return text.replace("\r\n", "\n").replace("\r", "\n")


def lines_to_output(lines: list[str]) -> str:
    """Convert expected lines to the exact output string printed by Java."""
    return "\n".join(lines) + ("\n" if lines else "")


def print_session(case: dict[str, Any], actual_output: str) -> None:
    """Print a reproducible record of one console test session."""
    print(f"=== {case['id']} ===")
    print(f"Aim: {case['aim']}")
    print("--- Console input ---")
    for command in case["inputs"]:
        print(command)
    print("--- Console output ---")
    print(actual_output, end="" if actual_output.endswith("\n") else "\n")


def run_case(
    case: dict[str, Any], main_class: str, class_dir: Path, timeout_seconds: float
) -> bool:
    """Run one complete console session and compare its output exactly."""
    console_input = "\n".join(case["inputs"]) + "\n"
    with tempfile.TemporaryDirectory(prefix="test-ui-case-") as case_dir:
        try:
            result = subprocess.run(
                ["java", "-cp", str(class_dir), main_class],
                input=console_input,
                capture_output=True,
                text=True,
                timeout=timeout_seconds,
                check=False,
                cwd=case_dir,
            )
        except subprocess.TimeoutExpired as error:
            actual_output = normalize_newlines(error.stdout or "")
            print_session(case, actual_output)
            print(f"FAIL: timed out after {timeout_seconds:g} seconds")
            print("--- Expected output ---")
            print(lines_to_output(case["expected_output"]), end="")
            return False

    actual_output = normalize_newlines(result.stdout)
    expected_output = lines_to_output(case["expected_output"])
    print_session(case, actual_output)

    failure_reasons: list[str] = []
    if result.returncode != 0:
        failure_reasons.append(f"process exited with code {result.returncode}")
    if result.stderr:
        failure_reasons.append("program wrote to standard error")
    if actual_output != expected_output:
        failure_reasons.append("standard output did not match")

    if failure_reasons:
        print("FAIL: " + "; ".join(failure_reasons))
        if result.stderr:
            print("--- Standard error ---")
            print(normalize_newlines(result.stderr), end="")
        print("--- Actual output ---")
        print(actual_output, end="" if actual_output.endswith("\n") else "\n")
        print("--- Expected output ---")
        print(expected_output, end="" if expected_output.endswith("\n") else "\n")
        return False

    print("PASS")
    return True


def main() -> int:
    """Compile the program and run test cases until the first failure."""
    args = parse_args()
    try:
        plan = load_plan(args.plan)
        require_java_25()
        source_dir = Path("src/main/java")
        with tempfile.TemporaryDirectory(prefix="test-ui-") as temp_dir:
            class_dir = Path(temp_dir)
            compile_program(source_dir, class_dir)
            timeout_seconds = float(plan.get("timeout_seconds", 10))
            for case in plan["test_cases"]:
                if not run_case(
                    case, plan["main_class"], class_dir, timeout_seconds
                ):
                    return 1
    except (OSError, ValueError, RuntimeError, json.JSONDecodeError) as error:
        print(f"TEST SETUP FAILED: {error}", file=sys.stderr)
        return 2

    print(f"All {len(plan['test_cases'])} test cases passed.")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
