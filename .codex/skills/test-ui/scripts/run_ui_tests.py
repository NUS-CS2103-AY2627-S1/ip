#!/usr/bin/env python3
"""Run the project's command-line UI test cases from a markdown plan."""

import argparse
import json
import re
import shutil
import subprocess
import sys
import tempfile
from pathlib import Path


PLAN_BLOCK = re.compile(
    r"```json\s+test-ui\s*\r?\n(.*?)\r?\n```", re.IGNORECASE | re.DOTALL
)


def read_plan(plan_path: Path) -> list[dict]:
    text = plan_path.read_text(encoding="utf-8")
    match = PLAN_BLOCK.search(text)
    if not match:
        raise ValueError(f"No ```json test-ui``` block found in {plan_path}")

    data = json.loads(match.group(1))
    cases = data.get("test_cases")
    if not isinstance(cases, list) or not cases:
        raise ValueError("The test-ui JSON block must contain a non-empty test_cases list")
    return cases


def java_major_version() -> str:
    result = subprocess.run(
        ["java", "-version"], capture_output=True, text=True, check=False
    )
    version_text = result.stderr + result.stdout
    match = re.search(r'version "(\d+)', version_text)
    if not match:
        raise RuntimeError("Could not determine the active Java version")
    return match.group(1)


def compile_project(project_root: Path, classes_dir: Path) -> None:
    """Compile all production sources using the project's dependency-aware build."""
    gradle_wrapper = project_root / ("gradlew.bat" if sys.platform.startswith("win") else "gradlew")
    gradle_command = (
        [str(gradle_wrapper)]
        if sys.platform.startswith("win")
        else ["bash", str(gradle_wrapper)]
    )
    result = subprocess.run(
        [*gradle_command, "compileJava", "--quiet"],
        cwd=project_root,
        capture_output=True,
        text=True,
        check=False,
    )
    if result.returncode != 0:
        raise RuntimeError("Compilation failed:\n" + result.stdout + result.stderr)

    compiled_dir = project_root / "build" / "classes" / "java" / "main"
    if not compiled_dir.is_dir():
        raise RuntimeError(f"Compilation output directory not found: {compiled_dir}")
    shutil.copytree(compiled_dir, classes_dir, dirs_exist_ok=True)


def validate_case(case: dict, index: int) -> tuple[str, list[str], list[str]]:
    try:
        name = case["name"]
        inputs = case["inputs"]
        expected = case["expected_outputs"]
    except KeyError as error:
        raise ValueError(f"Test case {index} is missing {error.args[0]}") from error

    if not isinstance(name, str) or not name:
        raise ValueError(f"Test case {index} has an invalid name")
    if not isinstance(inputs, list) or not all(isinstance(item, str) for item in inputs):
        raise ValueError(f"Test case {name!r} inputs must be a list of strings")
    if not isinstance(expected, list) or not all(isinstance(item, str) for item in expected):
        raise ValueError(f"Test case {name!r} expected_outputs must be a list of strings")
    if len(inputs) != len(expected):
        raise ValueError(
            f"Test case {name!r} has {len(inputs)} inputs but "
            f"{len(expected)} expected_outputs"
        )
    return name, inputs, expected


def run_case(classes_dir: Path, runtime_dir: Path, case: dict, index: int) -> bool:
    name, inputs, expected = validate_case(case, index)
    session_input = "\n".join(inputs) + "\n"
    process = subprocess.Popen(
        ["java", "-cp", str(classes_dir), "turtley.Turtley"],
        cwd=runtime_dir,
        stdin=subprocess.PIPE,
        stdout=subprocess.PIPE,
        stderr=subprocess.STDOUT,
        text=True,
    )

    try:
        actual = process.communicate(input=session_input, timeout=30)[0]
    except subprocess.TimeoutExpired:
        process.kill()
        actual = process.communicate()[0]
        print(f"\nFAIL: {name} timed out after 30 seconds")
        print("--- Console input ---")
        print(session_input, end="")
        print("--- Console output ---")
        print(actual, end="")
        return False

    cursor = 0
    failure = None
    for command, expected_output in zip(inputs, expected):
        match_index = actual.find(expected_output, cursor)
        if match_index < 0:
            failure = (command, expected_output)
            break
        cursor = match_index + len(expected_output)

    print(f"\n=== {name} ===")
    print("--- Console input ---")
    print(session_input, end="")
    print("--- Console output ---")
    print(actual, end="")

    if failure is not None or process.returncode != 0:
        if failure is not None:
            command, expected_output = failure
            print(f"\nFAIL: command {command!r}")
            print("--- Expected output ---")
            print(expected_output)
        if process.returncode != 0:
            print(f"\nFAIL: program exited with status {process.returncode}")
        print("--- Actual console output ---")
        print(actual, end="")
        return False

    print("RESULT: PASS")
    return True


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument(
        "--plan", type=Path, default=Path("test/ui-test-plan.md"), help="Markdown test plan"
    )
    parser.add_argument(
        "--project-root", type=Path, default=Path.cwd(), help="Project root directory"
    )
    args = parser.parse_args()
    project_root = args.project_root.resolve()
    plan_path = args.plan if args.plan.is_absolute() else project_root / args.plan

    try:
        version = java_major_version()
        if version != "25":
            raise RuntimeError(f"Java 25 is required, but Java {version} is active")
        cases = read_plan(plan_path)
        with tempfile.TemporaryDirectory(prefix="test-ui-") as temp_dir:
            classes_dir = Path(temp_dir)
            runtime_dir = Path(temp_dir) / "runtime"
            runtime_dir.mkdir()
            compile_project(project_root, classes_dir)
            for index, case in enumerate(cases, start=1):
                if not run_case(classes_dir, runtime_dir, case, index):
                    print(f"\nStopped after test case {index}; later cases were not run.")
                    return 1
    except (OSError, RuntimeError, ValueError, json.JSONDecodeError) as error:
        print(f"ERROR: {error}", file=sys.stderr)
        return 1

    print(f"\nAll {len(cases)} UI test case(s) passed.")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
