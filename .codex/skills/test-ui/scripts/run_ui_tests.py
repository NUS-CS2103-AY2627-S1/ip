#!/usr/bin/env python3
"""Run the Bo UI test cases declared in test/ui-test-plan.md."""

from __future__ import annotations

import re
import shutil
import subprocess
import sys
import tempfile
from pathlib import Path


ROOT = Path(__file__).resolve().parents[4]
PLAN = ROOT / "test" / "ui-test-plan.md"
JAVA_FILES = sorted((ROOT / "src" / "main" / "java").rglob("*.java"))


def read_cases() -> list[dict[str, str]]:
    """Parse test cases from the project's Markdown UI test plan."""
    plan_text = PLAN.read_text(encoding="utf-8")
    case_blocks = re.findall(
        r"^## Test case \d+: (.+?)\n(.*?)(?=^## Test case \d+: |\Z)",
        plan_text,
        flags=re.MULTILINE | re.DOTALL,
    )
    if not case_blocks:
        raise ValueError(f"No test cases found in {PLAN}")

    cases = []
    for title, block in case_blocks:
        aim_match = re.search(r"^Aim:\s*(.+)$", block, flags=re.MULTILINE)
        input_match = re.search(
            r"^### Input\s*\n```(?:text)?\n(.*?)```", block, flags=re.MULTILINE | re.DOTALL
        )
        expected_match = re.search(
            r"^### Expected output\s*\n```(?:text)?\n(.*?)```",
            block,
            flags=re.MULTILINE | re.DOTALL,
        )
        saved_file_match = re.search(
            r"^### Expected saved file\s*\n```(?:text)?\n(.*?)```",
            block,
            flags=re.MULTILINE | re.DOTALL,
        )
        initial_file_match = re.search(
            r"^### Initial saved file\s*\n```(?:text)?\n(.*?)```",
            block,
            flags=re.MULTILINE | re.DOTALL,
        )
        if not aim_match or not input_match or not expected_match:
            raise ValueError(
                f"Test case '{title}' must contain Aim, Input, and Expected output sections"
            )

        cases.append(
            {
                "title": title.strip(),
                "aim": aim_match.group(1).strip(),
                "input": input_match.group(1),
                "expected": expected_match.group(1),
                "saved_file": saved_file_match.group(1) if saved_file_match else "",
                "initial_file": initial_file_match.group(1) if initial_file_match else "",
            }
        )
    return cases


def expected_lines(expected: str) -> list[str]:
    """Return non-empty expected lines, normalized for indentation."""
    return [line.strip() for line in expected.splitlines() if line.strip()]


def assert_expected(actual: str, expected: list[str]) -> str | None:
    """Check expected lines occur in order and return a failure explanation."""
    actual_lines = [line.strip() for line in actual.splitlines()]
    cursor = 0
    for expected_line in expected:
        while cursor < len(actual_lines) and actual_lines[cursor] != expected_line:
            cursor += 1
        if cursor == len(actual_lines):
            return f"Expected line not found after the previous match:\n  {expected_line}"
        cursor += 1
    return None


def compile_project(build_dir: Path) -> None:
    """Compile the Bo sources into a temporary directory."""
    if not JAVA_FILES:
        raise RuntimeError("No Java source files found in src/main/java")
    result = subprocess.run(
        ["javac", "-Xlint:all", "-d", str(build_dir), *(str(path) for path in JAVA_FILES)],
        cwd=ROOT,
        text=True,
        capture_output=True,
    )
    if result.returncode != 0:
        raise RuntimeError(f"Compilation failed:\n{result.stdout}{result.stderr}")


def run_case(case: dict[str, str], build_dir: Path, number: int) -> bool:
    """Run one test case and print its complete console session."""
    saved_path = ROOT / "data" / "duke.txt"
    saved_path.unlink(missing_ok=True)
    if case["initial_file"]:
        saved_path.parent.mkdir(parents=True, exist_ok=True)
        saved_path.write_text(case["initial_file"], encoding="utf-8")

    run_directory = ROOT / "src" / "main" / "java" if number == 8 else ROOT
    result = subprocess.run(
        ["java", "-cp", str(build_dir), "bo.Bo"],
        cwd=run_directory,
        input=case["input"],
        text=True,
        capture_output=True,
        timeout=10,
    )
    actual = result.stdout + result.stderr
    expected = expected_lines(case["expected"])
    failure = assert_expected(actual, expected)

    print(f"\n=== Test case {number}: {case['title']} ===")
    print(f"Aim: {case['aim']}")
    print("--- Console input ---")
    print(case["input"], end="" if case["input"].endswith("\n") else "\n")
    print("--- Console output ---")
    print(actual, end="" if actual.endswith("\n") else "\n")

    if result.returncode != 0:
        print(f"FAIL: Bo exited with status {result.returncode}.")
        print("Expected output lines:")
        print("\n".join(expected))
        return False
    if failure:
        print(f"FAIL: {failure}")
        print("Expected output lines:")
        print("\n".join(expected))
        print("Actual output:")
        print(actual)
        return False

    if case["saved_file"]:
        try:
            actual_saved_file = saved_path.read_text(encoding="utf-8")
        except FileNotFoundError:
            print(f"FAIL: Expected saved file was not created: {saved_path}")
            return False
        if actual_saved_file != case["saved_file"]:
            print("FAIL: Saved file contents do not match the expected format.")
            print("Expected saved file:")
            print(case["saved_file"], end="" if case["saved_file"].endswith("\n") else "\n")
            print("Actual saved file:")
            print(actual_saved_file, end="" if actual_saved_file.endswith("\n") else "\n")
            return False

    print("PASS")
    return True


def main() -> int:
    """Compile Bo, run all planned cases, and stop at the first failure."""
    if shutil.which("javac") is None or shutil.which("java") is None:
        print("FAIL: Java 25 tools javac and java must be available.", file=sys.stderr)
        return 1

    saved_path = ROOT / "data" / "duke.txt"
    saved_file_existed = saved_path.exists()
    saved_file_contents = saved_path.read_bytes() if saved_file_existed else None
    try:
        cases = read_cases()
        with tempfile.TemporaryDirectory(prefix="bo-ui-build-") as build_path:
            build_dir = Path(build_path)
            compile_project(build_dir)
            for number, case in enumerate(cases, start=1):
                if not run_case(case, build_dir, number):
                    return 1
    except (OSError, RuntimeError, ValueError, subprocess.TimeoutExpired) as error:
        print(f"FAIL: {error}", file=sys.stderr)
        return 1
    finally:
        if saved_file_existed:
            saved_path.parent.mkdir(parents=True, exist_ok=True)
            saved_path.write_bytes(saved_file_contents)
        else:
            saved_path.unlink(missing_ok=True)

    print(f"\nAll {len(cases)} UI test cases passed.")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
