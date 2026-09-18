#!/usr/bin/env python3
"""Runs the console UI test cases defined in a test plan against a compiled
Java program, stopping at the first failure.

Test plan format (see test/ui-test-plan.md for real examples):

    ## Test: <name>
    **Aim:** <one-line description of what this test case checks>

    ```input
    <one console command per line, sent to the program's stdin in order>
    ```

    ```output
    <the exact expected stdout for that input, byte for byte>
    ```

Usage:
    python3 .claude/skills/test-ui/scripts/run_ui_tests.py \\
        [--plan test/ui-test-plan.md] [--classpath out/production/ip] [--main Meowmeow]
"""
import argparse
import re
import shutil
import subprocess
import sys
from pathlib import Path


def parse_test_plan(text: str):
    """Splits the test plan into test cases and extracts each one's name,
    aim, input lines, and expected output. Raises ValueError with a helpful
    message if a test case is missing its input or output block, since a
    silently-skipped test case would be worse than a loud parse error.
    """
    tests = []
    blocks = re.split(r"(?m)^## Test: ", text)[1:]
    for block in blocks:
        name_line, _, rest = block.partition("\n")
        name = name_line.strip()

        # Aim text may be soft-wrapped across multiple source lines; take
        # everything up to the next blank line and collapse it to one line.
        aim_match = re.search(r"\*\*Aim:\*\*\s*(.+?)\n\s*\n", rest, re.DOTALL)
        aim = re.sub(r"\s+", " ", aim_match.group(1)).strip() if aim_match else ""

        input_match = re.search(r"```input\r?\n(.*?)```", rest, re.DOTALL)
        output_match = re.search(r"```output\r?\n(.*?)```", rest, re.DOTALL)
        if not input_match or not output_match:
            raise ValueError(
                f"Test case '{name}' is missing an ```input or ```output code block"
            )

        tests.append({
            "name": name,
            "aim": aim,
            "input": input_match.group(1),
            "output": output_match.group(1),
        })
    return tests


def run_case(java_cmd, stdin_text):
    result = subprocess.run(
        java_cmd,
        input=stdin_text,
        capture_output=True,
        text=True,
        encoding="utf-8",
        timeout=10,
    )
    return result.stdout


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--plan", default="test/ui-test-plan.md",
                         help="Path to the test plan markdown file")
    parser.add_argument("--classpath", default="out/production/ip",
                         help="Directory of compiled .class files")
    parser.add_argument("--main", default="meowmeow.Meowmeow",
                         help="Fully-qualified name of the main class to run")
    args = parser.parse_args()

    plan_path = Path(args.plan)
    if not plan_path.exists():
        print(f"Test plan not found: {plan_path}", file=sys.stderr)
        return 2

    try:
        tests = parse_test_plan(plan_path.read_text(encoding="utf-8"))
    except ValueError as e:
        print(f"Could not parse test plan: {e}", file=sys.stderr)
        return 2

    if not tests:
        print(f"No test cases found in {plan_path}", file=sys.stderr)
        return 2

    java_cmd = ["java", "-cp", args.classpath, args.main]
    passed = 0

    for i, test in enumerate(tests, start=1):
        # The program saves tasks to ./data/ and loads them on startup, so
        # a file left by one test case would leak into the next. Clear it
        # first so every test case starts from an empty task list.
        shutil.rmtree("data", ignore_errors=True)

        print(f"\n{'=' * 70}")
        print(f"Test {i}/{len(tests)}: {test['name']}")
        if test["aim"]:
            print(f"Aim: {test['aim']}")
        print("=" * 70)

        print("--- Console input ---")
        print(test["input"].rstrip("\n"))

        actual = run_case(java_cmd, test["input"])
        print("--- Console output (actual) ---")
        print(actual.rstrip("\n"))

        expected = test["output"]
        if actual.rstrip("\n") != expected.rstrip("\n"):
            print(f"\n{'!' * 70}")
            print(f"FAIL: {test['name']}")
            print("!" * 70)
            print("--- Expected output ---")
            print(expected.rstrip("\n"))
            print("--- Actual output ---")
            print(actual.rstrip("\n"))
            print(
                f"\nStopped after {passed} passing test case(s); "
                f"test {i} ('{test['name']}') failed."
            )
            return 1

        print(f"PASS: {test['name']}")
        passed += 1

    print(f"\nAll {passed} test case(s) passed.")
    return 0


if __name__ == "__main__":
    sys.exit(main())
