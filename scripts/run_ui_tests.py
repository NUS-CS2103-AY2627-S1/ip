"""Runs every console transcript defined in test/ui-test-plan.md."""

from __future__ import annotations

import os
from pathlib import Path
import re
import shutil
import subprocess
import sys
import tempfile


PROJECT_ROOT = Path(__file__).resolve().parents[1]
TEST_PLAN = PROJECT_ROOT / "test" / "ui-test-plan.md"
CLASSES_DIRECTORY = PROJECT_ROOT / "build" / "classes" / "java" / "main"


def normalized(text: str) -> str:
    """Returns text with consistent newlines and no final newline."""
    return text.replace("\r\n", "\n").rstrip("\n")


def read_code_block(case_text: str, label: str, required: bool = True) -> str | None:
    """Returns the fenced code block following a section label."""
    pattern = rf"{re.escape(label)}:\s*\n\s*```\n(.*?)\n```"
    match = re.search(pattern, case_text, re.DOTALL)
    if match is None:
        if required:
            raise ValueError(f"Missing '{label}' block")
        return None
    return match.group(1)


def compile_application() -> None:
    """Compiles the application through its Gradle wrapper."""
    wrapper = PROJECT_ROOT / ("gradlew.bat" if os.name == "nt" else "gradlew")
    result = subprocess.run(
        [str(wrapper), "classes"],
        cwd=PROJECT_ROOT,
        text=True,
        capture_output=True,
        check=False,
    )
    if result.returncode != 0:
        print(result.stdout)
        print(result.stderr, file=sys.stderr)
        raise RuntimeError("Compilation failed")


def run_kaykay(working_directory: Path, console_input: str) -> str:
    """Runs one isolated Kaykay console session."""
    java_home = os.environ.get("JAVA_HOME")
    java = Path(java_home) / "bin" / "java" if java_home else shutil.which("java")
    if java is None:
        raise RuntimeError("Java is not available")
    result = subprocess.run(
        [str(java), "-cp", str(CLASSES_DIRECTORY), "kaykay.Kaykay"],
        cwd=working_directory,
        input=console_input + "\n",
        text=True,
        capture_output=True,
        check=False,
    )
    if result.returncode != 0:
        raise RuntimeError(result.stderr or f"Kaykay exited with code {result.returncode}")
    return normalized(result.stdout)


def assert_equal(case_name: str, result_name: str, actual: str, expected: str) -> None:
    """Stops the suite with a complete comparison when values differ."""
    if normalized(actual) == normalized(expected):
        return
    print(f"FAILED: {case_name} ({result_name})")
    print("--- Actual ---")
    print(actual)
    print("--- Expected ---")
    print(expected)
    raise AssertionError(f"{case_name} failed")


def run_case(case_text: str) -> None:
    """Runs one test-plan case, including optional persistence checks."""
    case_name = case_text.splitlines()[0].removeprefix("## ")
    console_input = read_code_block(case_text, "Commands / console input")
    expected_output = read_code_block(case_text, "Expected output")
    initial_file = read_code_block(case_text, "Initial file contents", required=False)
    expected_file = read_code_block(case_text, "Expected file contents", required=False)
    restart_input = read_code_block(case_text, "Restart console input", required=False)
    expected_restart = read_code_block(case_text, "Expected restart output", required=False)

    with tempfile.TemporaryDirectory(prefix="kaykay-ui-test-") as directory:
        working_directory = Path(directory)
        data_file = working_directory / "data" / "kaykay.txt"
        if initial_file is not None:
            data_file.parent.mkdir(parents=True, exist_ok=True)
            data_file.write_text(initial_file + "\n", encoding="utf-8")

        actual_output = run_kaykay(working_directory, console_input)
        print(f"\n{case_name}")
        print("--- Console input ---")
        print(console_input)
        print("--- Console output ---")
        print(actual_output)
        assert_equal(case_name, "console output", actual_output, expected_output)

        if expected_file is not None:
            actual_file = data_file.read_text(encoding="utf-8")
            assert_equal(case_name, "data file", actual_file, expected_file)

        if restart_input is not None and expected_restart is not None:
            actual_restart = run_kaykay(working_directory, restart_input)
            print("--- Restart console input ---")
            print(restart_input)
            print("--- Restart console output ---")
            print(actual_restart)
            assert_equal(case_name, "restart output", actual_restart, expected_restart)

    print(f"PASSED: {case_name}")


def main() -> int:
    """Compiles Kaykay and runs test-plan cases in document order."""
    compile_application()
    plan_text = TEST_PLAN.read_text(encoding="utf-8")
    cases = re.findall(r"(?ms)^## Case .*?(?=^## Case |\Z)", plan_text)
    if not cases:
        raise ValueError("No UI test cases found")
    for case_text in cases:
        run_case(case_text)
    print(f"\nAll {len(cases)} UI test cases passed.")
    return 0


if __name__ == "__main__":
    try:
        raise SystemExit(main())
    except (AssertionError, OSError, RuntimeError, ValueError) as error:
        print(f"UI test run stopped: {error}", file=sys.stderr)
        raise SystemExit(1) from error
