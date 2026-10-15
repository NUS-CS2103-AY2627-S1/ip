#!/usr/bin/env python3
"""Runs the UI test cases defined in test/ui-test-plan.md against the
compiled XiaoZhi chatbot, feeding each test's input to the program's stdin
and comparing the actual console output to the expected output.

Used by the `test-ui` skill (see .claude/skills/test-ui/SKILL.md).

Usage:
    python3 test/run_ui_tests.py

Behavior:
    - Compiles all files under src/main/java into test/_build.
    - Parses every "## Test N: ..." section out of test/ui-test-plan.md.
    - Runs each test case in order, printing the input and actual output
      for a full session transcript as it goes. Each test case runs in its
      own fresh temporary directory, so XiaoZhi's save file (created at
      "./data/xiaozhi.txt", relative to wherever it is run from) never
      leaks tasks from one test case into the next; every test case's
      expected output can assume it starts from an empty task list.
    - Stops immediately at the first failing test case and reports both
      the expected and actual output for it (exit code 1).
    - Exits 0 and prints a summary if every test case passes.
"""

import re
import subprocess
import sys
import tempfile
from pathlib import Path

ROOT = Path(__file__).resolve().parent.parent
PLAN_PATH = ROOT / "test" / "ui-test-plan.md"
SRC_DIR = ROOT / "src" / "main" / "java"
BUILD_DIR = ROOT / "test" / "_build"

TEST_PATTERN = re.compile(
    r"^## (Test \d+: .+?)\s*\n"
    r"\s*\n\*\*Aim:\*\*\s*(.+?)\n\s*\n"
    r"(?:.*?\n)*?"
    r"\*\*Input:\*\*\s*\n```\n(.*?)\n```\s*\n"
    r"(?:.*?\n)*?"
    r"\*\*Expected output:\*\*\s*\n```\n(.*?)\n```",
    re.MULTILINE | re.DOTALL,
)


def compile_program():
    BUILD_DIR.mkdir(parents=True, exist_ok=True)
    java_files = sorted(str(p) for p in SRC_DIR.glob("**/*.java"))
    if not java_files:
        print(f"No .java files found under {SRC_DIR}")
        sys.exit(1)
    result = subprocess.run(
        ["javac", "-d", str(BUILD_DIR), *java_files],
        capture_output=True,
        text=True,
    )
    if result.returncode != 0:
        print("Compilation failed:")
        print(result.stdout)
        print(result.stderr)
        sys.exit(1)


def parse_test_plan():
    if not PLAN_PATH.exists():
        print(f"Test plan not found at {PLAN_PATH}")
        sys.exit(1)
    text = PLAN_PATH.read_text()
    tests = []
    for match in TEST_PATTERN.finditer(text):
        name, aim, input_block, expected_block = match.groups()
        tests.append(
            {
                "name": name.strip(),
                "aim": " ".join(aim.split()),
                "input": input_block,
                "expected": expected_block.rstrip("\n"),
            }
        )
    if not tests:
        print(f"No test cases found in {PLAN_PATH}")
        sys.exit(1)
    return tests


def run_test(test):
    # Run in a fresh temp directory each time: XiaoZhi always saves to
    # "./data/xiaozhi.txt" relative to its working directory, so reusing one
    # working directory across test cases would let an earlier test case's
    # tasks leak into a later one (e.g. an add in one test case silently
    # bumping "Now you have N tasks" in the next).
    with tempfile.TemporaryDirectory() as run_dir:
        proc = subprocess.run(
            ["java", "-cp", str(BUILD_DIR), "xiaozhi.XiaoZhi"],
            input=test["input"] + "\n",
            capture_output=True,
            text=True,
            timeout=10,
            cwd=run_dir,
        )
    return proc.stdout.rstrip("\n")


def main():
    compile_program()
    tests = parse_test_plan()

    print(f"Running {len(tests)} UI test case(s) from "
          f"{PLAN_PATH.relative_to(ROOT)}\n")

    for test in tests:
        print(f"===== {test['name']} =====")
        print(f"Aim: {test['aim']}")
        print("--- input ---")
        print(test["input"])
        actual = run_test(test)
        print("--- actual output ---")
        print(actual)
        print()

        if actual == test["expected"]:
            print(f"PASS: {test['name']}\n")
        else:
            print(f"FAIL: {test['name']}")
            print("\n--- expected output ---")
            print(test["expected"])
            print("\n--- actual output ---")
            print(actual)
            sys.exit(1)

    print(f"All {len(tests)} test case(s) passed.")


if __name__ == "__main__":
    main()
