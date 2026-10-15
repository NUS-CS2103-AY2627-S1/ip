#!/usr/bin/env python3
"""Compile TBlade and run the UI test cases documented in the test plan."""

from pathlib import Path
import re
import shutil
import subprocess
import sys
import tempfile


PROJECT_ROOT = Path(__file__).resolve().parents[4]
DEFAULT_PLAN = PROJECT_ROOT / "test" / "ui-test-plan.md"
CASE_PATTERN = re.compile(
    r"^## Test Case: (?P<name>.+?)\n"
    r"### Aim\n(?P<aim>.+?)\n"
    r"### Inputs\n```text\n(?P<inputs>.*?)\n```\n+"
    r"### Expected output\n```text\n(?P<expected>.*?)\n```",
    re.MULTILINE | re.DOTALL,
)


def load_cases(plan_path: Path) -> list[dict[str, str]]:
    """Return the documented test cases, rejecting an incomplete test plan."""
    matches = list(CASE_PATTERN.finditer(plan_path.read_text()))
    if not matches:
        raise ValueError("No test cases matched the required test-plan format.")
    return [match.groupdict() for match in matches]


def compile_program(classes_directory: Path) -> None:
    """Compile every Java source file using the configured Java 25 compiler."""
    sources = sorted((PROJECT_ROOT / "src" / "main" / "java").glob("**/*.java"))
    result = subprocess.run(
        ["javac", "-d", str(classes_directory), *map(str, sources)],
        text=True,
        capture_output=True,
        check=False,
    )
    if result.returncode != 0:
        print("Compilation failed:\n" + result.stdout + result.stderr, file=sys.stderr)
        sys.exit(1)


def display_session(case: dict[str, str], actual: str) -> None:
    """Print the input and output record requested for this test session."""
    print(f"=== Test Case: {case['name']} ===")
    print(f"Aim: {case['aim']}")
    print("--- Console input ---")
    print(case["inputs"])
    print("--- Console output ---")
    print(actual, end="" if actual.endswith("\n") else "\n")


def main() -> None:
    """Compile the application and execute test cases until one fails."""
    plan_path = Path(sys.argv[1]) if len(sys.argv) == 2 else DEFAULT_PLAN
    try:
        cases = load_cases(plan_path)
    except (OSError, ValueError) as error:
        print(f"Could not read UI test plan: {error}", file=sys.stderr)
        sys.exit(1)

    classes_directory = Path(tempfile.mkdtemp(prefix="tblade-ui-tests-"))
    try:
        compile_program(classes_directory)
        for case in cases:
            with tempfile.TemporaryDirectory(prefix="tblade-ui-session-") as session_directory:
                result = subprocess.run(
                    ["java", "-cp", str(classes_directory), "tblade.TBlade"],
                    input=case["inputs"] + "\n",
                    text=True,
                    capture_output=True,
                    check=False,
                    cwd=session_directory,
                )
            actual = result.stdout
            display_session(case, actual)
            if result.returncode != 0 or actual != case["expected"] + "\n":
                print("FAILED: output does not match the expected output.")
                print("--- Expected output ---")
                print(case["expected"])
                print("--- Actual output ---")
                print(actual, end="" if actual.endswith("\n") else "\n")
                sys.exit(1)
            print("PASSED\n")
    finally:
        shutil.rmtree(classes_directory)


if __name__ == "__main__":
    main()
