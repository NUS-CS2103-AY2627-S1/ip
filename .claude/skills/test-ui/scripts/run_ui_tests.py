#!/usr/bin/env python3
"""
Compile the program and run every test case in test/ui-test-plan.md against
it, feeding each case's Input to stdin and comparing the program's full
stdout against its Expected output.

    python3 .claude/skills/test-ui/scripts/run_ui_tests.py

The data/ folder (where the program persists its task list, see Storage.java)
is deleted before each test case's first run, so every test case starts from
a clean slate regardless of what an earlier test case saved.

A test case may optionally include a "Second input"/"Second expected output"
pair. When present, it is run as a second, separate process invocation
immediately after the first, WITHOUT resetting data/ in between -- this is
how a test case can verify that data saved by one run is loaded by the next.

Stops at the first failing run and reports the actual vs. expected output.
Exits 0 if every test case (in file order) passes, 1 otherwise.
"""
from __future__ import annotations

import argparse
import os
import re
import shutil
import subprocess
import sys
from pathlib import Path

# Adjust here if the project's entry class, source layout, or test plan
# location ever change.
SRC_DIR = "src/main/java"
MAIN_CLASS = "eve.Eve"
CLASSES_DIR = "_temp/test-ui-classes"
PLAN_FILE = "test/ui-test-plan.md"

# Must match where Storage.java persists tasks.
DATA_DIR = "data"
DATA_FILE = "eve.txt"

HEADER_RE = re.compile(r"^## Test \d+: (?P<name>.+?)\s*$", re.MULTILINE)
AIM_RE = re.compile(r"\*\*Aim:\*\*\s*(?P<aim>.+?)\s*\n\n", re.DOTALL)
INPUT_RE = re.compile(r"\*\*Input:\*\*\s*```(?:\w*)\n(?P<input>.*?)```", re.DOTALL)
EXPECTED_RE = re.compile(r"\*\*Expected output:\*\*\s*```(?:\w*)\n(?P<expected>.*?)```", re.DOTALL)
SECOND_INPUT_RE = re.compile(r"\*\*Second input:\*\*\s*```(?:\w*)\n(?P<input>.*?)```", re.DOTALL)
SECOND_EXPECTED_RE = re.compile(
    r"\*\*Second expected output:\*\*\s*```(?:\w*)\n(?P<expected>.*?)```", re.DOTALL)
SEED_RE = re.compile(r"\*\*Data file before run:\*\*\s*```(?:\w*)\n(?P<seed>.*?)```", re.DOTALL)


class TestCase:
    def __init__(self, name: str, aim: str, stdin: str, expected: str,
                 second_stdin: str | None = None, second_expected: str | None = None,
                 seed: str | None = None):
        self.name = name
        self.aim = aim
        self.stdin = stdin
        self.expected = expected
        self.second_stdin = second_stdin
        self.second_expected = second_expected
        self.seed = seed

    @property
    def runs(self) -> list[tuple[str, str]]:
        """This case's (stdin, expected) pairs, in the order they should run."""
        runs = [(self.stdin, self.expected)]
        if self.second_stdin is not None:
            runs.append((self.second_stdin, self.second_expected))
        return runs


def parse_plan(text: str) -> list[TestCase]:
    headers = list(HEADER_RE.finditer(text))
    cases = []
    for i, header in enumerate(headers):
        start = header.end()
        end = headers[i + 1].start() if i + 1 < len(headers) else len(text)
        block = text[start:end]

        aim_match = AIM_RE.search(block)
        input_match = INPUT_RE.search(block)
        expected_match = EXPECTED_RE.search(block)
        if not (aim_match and input_match and expected_match):
            continue

        second_input_match = SECOND_INPUT_RE.search(block)
        second_expected_match = SECOND_EXPECTED_RE.search(block)
        seed_match = SEED_RE.search(block)

        cases.append(TestCase(
            name=header.group("name"),
            aim=aim_match.group("aim"),
            stdin=input_match.group("input"),
            expected=expected_match.group("expected"),
            second_stdin=second_input_match.group("input") if second_input_match else None,
            second_expected=second_expected_match.group("expected") if second_expected_match else None,
            seed=seed_match.group("seed") if seed_match else None,
        ))
    return cases


def compile_program(repo: Path) -> None:
    # eve/gui/** needs JavaFX on the classpath (see build.gradle), which this
    # script deliberately doesn't set up -- it only tests the CLI entry
    # point (eve.Eve), which has no GUI dependency.
    sources = sorted(
        s for s in (repo / SRC_DIR).glob("**/*.java")
        if "eve" + os.sep + "gui" not in str(s)
    )
    if not sources:
        raise RuntimeError(f"no .java files found under {SRC_DIR}")
    classes_dir = repo / CLASSES_DIR
    classes_dir.mkdir(parents=True, exist_ok=True)
    result = subprocess.run(
        ["javac", "-d", str(classes_dir), *[str(s) for s in sources]],
        capture_output=True, text=True,
    )
    if result.returncode != 0:
        raise RuntimeError(f"compilation failed:\n{result.stdout}{result.stderr}")


def reset_data(repo: Path) -> None:
    """Deletes the persisted-tasks folder so the next run starts with no saved data."""
    shutil.rmtree(repo / DATA_DIR, ignore_errors=True)


def seed_data(repo: Path, content: str) -> None:
    """Writes content verbatim to the data file, e.g. to test corrupted-file handling."""
    data_dir = repo / DATA_DIR
    data_dir.mkdir(parents=True, exist_ok=True)
    (data_dir / DATA_FILE).write_text(content, encoding="utf-8")


def run_program(repo: Path, stdin: str) -> tuple[str, str, int]:
    """Returns (stdout, stderr, returncode)."""
    result = subprocess.run(
        # -ea: run with assertions enabled (see A-Assertions), so a violated
        # assumption fails the test loudly instead of silently passing.
        ["java", "-ea", "-cp", str(repo / CLASSES_DIR), MAIN_CLASS],
        input=stdin, capture_output=True, text=True, timeout=10, cwd=str(repo),
    )
    return result.stdout, result.stderr, result.returncode


def print_session(stdin: str, stdout: str, label: str = "") -> None:
    print(f"--- Input (stdin){label} ---")
    for line in stdin.splitlines():
        print(f"> {line}")
    print(f"--- Actual output{label} ---")
    print(stdout, end="" if stdout.endswith("\n") else "\n")


def main(argv: list[str]) -> int:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--repo", default=".", help="repository root (default: cwd)")
    parser.add_argument("--plan", default=PLAN_FILE, help="path to the test plan, relative to --repo")
    args = parser.parse_args(argv)

    repo = Path(args.repo).resolve()
    plan_path = repo / args.plan
    if not plan_path.exists():
        print(f"error: test plan not found at {plan_path}", file=sys.stderr)
        return 1

    cases = parse_plan(plan_path.read_text(encoding="utf-8"))
    if not cases:
        print(f"error: no test cases found in {plan_path}", file=sys.stderr)
        return 1

    print(f"Compiling {SRC_DIR} ...")
    try:
        compile_program(repo)
    except RuntimeError as exc:
        print(f"error: {exc}", file=sys.stderr)
        return 1
    print(f"Found {len(cases)} test case(s) in {args.plan}\n")

    for index, case in enumerate(cases, start=1):
        print(f"=== Test {index}: {case.name} ===")
        print(f"Aim: {case.aim}")
        reset_data(repo)
        if case.seed is not None:
            seed_data(repo, case.seed)

        runs = case.runs
        for run_index, (stdin, expected) in enumerate(runs, start=1):
            run_label = f" (run {run_index} of {len(runs)})" if len(runs) > 1 else ""
            try:
                stdout, stderr, returncode = run_program(repo, stdin)
            except subprocess.TimeoutExpired:
                print(f"Result: FAIL{run_label} (program did not exit within 10s -- "
                      "does the Input end with 'bye'?)")
                return 1

            print_session(stdin, stdout, run_label)
            if stderr:
                print(f"--- Actual stderr{run_label} ---")
                print(stderr, end="" if stderr.endswith("\n") else "\n")

            actual_normalized = stdout.rstrip("\n")
            expected_normalized = expected.rstrip("\n")
            if returncode == 0 and actual_normalized == expected_normalized:
                print(f"Result: PASS{run_label}\n")
                continue

            print(f"Result: FAIL{run_label}")
            print("--- Expected output ---")
            print(expected, end="" if expected.endswith("\n") else "\n")
            print("--- Actual output ---")
            print(stdout, end="" if stdout.endswith("\n") else "\n")
            print(f"\nStopped at Test {index} of {len(cases)}: {case.name}{run_label}")
            return 1

    print(f"All {len(cases)} test case(s) passed.")
    return 0


if __name__ == "__main__":
    raise SystemExit(main(sys.argv[1:]))
