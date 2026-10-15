#!/usr/bin/env python3
"""
Run the console test cases described in a UI test plan against the compiled
Java chatbot, feeding each test case's input to a fresh run of the program
and comparing the captured stdout against the expected output verbatim.

    python3 run_ui_tests.py [repo_root] [test_plan_path] [main_class]

Defaults: repo_root=".", test_plan_path="test/ui-test-plan.md", main_class="coco.Coco"

Each test case in the plan file looks like:

    ## Test 1: Some name

    Aim: what this test case is checking.

    ### Input

    ```
    line one
    line two
    ```

    ### Expected Output

    ```
    ...
    ```

On the first failing test case, the run stops immediately (non-zero exit)
and prints the actual output next to the expected output. Only Python's
standard library is used.
"""
from __future__ import annotations

import re
import shutil
import subprocess
import sys
from pathlib import Path

TEST_CASE_RE = re.compile(
    r"^##\s+Test\s+\d+:\s*(?P<name>.+?)\s*$"
    r"\n+Aim:\s*(?P<aim>.+?)\s*(?=\n\n|\n###)"
    r"\n+###\s+Input\s*"
    r"\n+```[^\n]*\n(?P<input>.*?)\n?```"
    r"\n+###\s+Expected Output\s*"
    r"\n+```[^\n]*\n(?P<expected>.*?)\n?```",
    re.MULTILINE | re.DOTALL,
)


def parse_test_plan(text: str) -> list[dict]:
    cases = []
    for match in TEST_CASE_RE.finditer(text):
        cases.append(
            {
                "name": match.group("name").strip(),
                "aim": " ".join(match.group("aim").split()),
                "input": match.group("input"),
                "expected": match.group("expected"),
            }
        )
    return cases


def compile_program(repo_root: Path, build_dir: Path) -> None:
    src_files = sorted((repo_root / "src" / "main" / "java").rglob("*.java"))
    if not src_files:
        raise SystemExit("No Java source files found under src/main/java")
    build_dir.mkdir(parents=True, exist_ok=True)
    result = subprocess.run(
        ["javac", "-d", str(build_dir), *[str(f) for f in src_files]],
        capture_output=True,
        text=True,
    )
    if result.returncode != 0:
        print(result.stdout)
        print(result.stderr, file=sys.stderr)
        raise SystemExit("Compilation failed")


def run_case(
    repo_root: Path, build_dir: Path, main_class: str, stdin_text: str
) -> subprocess.CompletedProcess:
    # The chatbot persists tasks to ./data relative to its working directory.
    # Wipe it before every case so each one still runs against a fresh
    # instance, as the test plan requires, instead of inheriting tasks
    # saved by a previous case.
    shutil.rmtree(repo_root / "data", ignore_errors=True)
    return subprocess.run(
        ["java", "-cp", str(build_dir), main_class],
        input=stdin_text,
        capture_output=True,
        text=True,
        timeout=15,
        cwd=repo_root,
    )


def main() -> None:
    repo_root = Path(sys.argv[1]) if len(sys.argv) > 1 else Path(".")
    plan_path = (
        Path(sys.argv[2]) if len(sys.argv) > 2 else repo_root / "test" / "ui-test-plan.md"
    )
    main_class = sys.argv[3] if len(sys.argv) > 3 else "coco.Coco"

    if not plan_path.exists():
        raise SystemExit(f"Test plan not found: {plan_path}")

    cases = parse_test_plan(plan_path.read_text())
    if not cases:
        raise SystemExit(f"No test cases found in {plan_path}")

    build_dir = repo_root / "out" / "test-ui"
    compile_program(repo_root, build_dir)

    print(f"Running {len(cases)} test case(s) from {plan_path}\n")

    for i, case in enumerate(cases, start=1):
        print(f"=== Test {i}: {case['name']} ===")
        print(f"Aim: {case['aim']}\n")
        print("--- console input ---")
        print(case["input"])

        result = run_case(repo_root, build_dir, main_class, case["input"])
        actual = result.stdout
        print("--- console output ---")
        print(actual, end="" if actual.endswith("\n") else "\n")

        expected_normalized = case["expected"].rstrip("\n")
        actual_normalized = actual.rstrip("\n")

        if actual_normalized != expected_normalized:
            print(f"\nFAILED: Test {i} ({case['name']})")
            if result.returncode != 0 or result.stderr.strip():
                print(
                    f"\n--- program crashed (exit code {result.returncode}) ---"
                )
                print(result.stderr.strip())
            print("\n--- expected output ---")
            print(expected_normalized)
            print("\n--- actual output ---")
            print(actual_normalized)
            sys.exit(1)

        print(f"PASSED: Test {i} ({case['name']})\n")

    shutil.rmtree(repo_root / "data", ignore_errors=True)
    print(f"All {len(cases)} test case(s) passed.")


if __name__ == "__main__":
    main()
