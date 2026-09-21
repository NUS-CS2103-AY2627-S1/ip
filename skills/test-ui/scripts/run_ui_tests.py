#!/usr/bin/env python3
"""Run the Markdown-defined Janet UI tests and stop at the first failure."""

from pathlib import Path
import re
import subprocess
import sys
import tempfile

ROOT = Path(__file__).resolve().parents[3]
PLAN = ROOT / "test" / "ui-test-plan.md"
GUI_ONLY_SOURCES = {"Launcher.java", "Main.java", "DialogBox.java", "MainWindow.java"}


def read_cases(text):
    cases = []
    sections = re.split(r"(?=^## Test case: )", text, flags=re.MULTILINE)
    for section in sections:
        heading = re.search(r"^## Test case: (.+)$", section, re.MULTILINE)
        if not heading:
            continue
        aim = re.search(r"^Aim:\s*(.+)$", section, re.MULTILINE)
        inputs = re.search(r"^### Inputs\n\n```(?:text|console)?\n(.*?)```", section,
                           re.MULTILINE | re.DOTALL)
        expected = re.search(r"^### Expected output\n\n```(?:text|console)?\n(.*?)```", section,
                             re.MULTILINE | re.DOTALL)
        initial_data = re.search(r"^### Initial saved data\n\n```text\n(.*?)```", section,
                                 re.MULTILINE | re.DOTALL)
        expected_data = re.search(r"^### Expected saved data\n\n```text\n(.*?)```", section,
                                  re.MULTILINE | re.DOTALL)
        if not aim or not inputs or not expected:
            raise ValueError(f"Malformed test case: {heading.group(1)}")
        cases.append((heading.group(1), aim.group(1), inputs.group(1), expected.group(1),
                      initial_data.group(1) if initial_data else None,
                      expected_data.group(1) if expected_data else None))
    return cases


def run():
    if not PLAN.exists():
        raise SystemExit(f"Missing test plan: {PLAN}")
    cases = read_cases(PLAN.read_text())
    if not cases:
        raise SystemExit("No test cases found in the UI test plan.")

    with tempfile.TemporaryDirectory(prefix="janet-ui-") as build_dir:
        sources = [
            str(path)
            for path in (ROOT / "src" / "main" / "java").rglob("*.java")
            if path.name not in GUI_ONLY_SOURCES
        ]
        subprocess.run(["javac", "--release", "25", "-d", build_dir, *sources], cwd=ROOT, check=True)
        for index, (name, aim, inputs, expected, initial_data, expected_data) in enumerate(cases, 1):
            with tempfile.TemporaryDirectory(prefix="janet-ui-case-") as run_dir:
                data_path = Path(run_dir) / "data" / "janet.txt"
                if initial_data is not None:
                    data_path.parent.mkdir()
                    data_path.write_text(initial_data)
                result = subprocess.run(
                    ["java", "-cp", build_dir, "janet.Janet"], cwd=run_dir,
                    input=inputs, text=True, capture_output=True, check=False
                )
                actual_data = data_path.read_text() if data_path.exists() else None
            actual = result.stdout.replace("\r\n", "\n")
            expected = expected.replace("\r\n", "\n")
            data_matches = expected_data is None or actual_data == expected_data
            print(f"=== Test {index}: {name} ===")
            print(f"Aim: {aim}")
            print("--- Console input ---")
            print(inputs, end="" if inputs.endswith("\n") else "\n")
            print("--- Console output ---")
            print(actual, end="" if actual.endswith("\n") else "\n")
            if result.returncode != 0 or actual != expected or not data_matches:
                print("--- Expected output ---")
                print(expected, end="" if expected.endswith("\n") else "\n")
                if not data_matches:
                    print("--- Expected saved data ---")
                    print(expected_data or "<no data file>")
                    print("--- Actual saved data ---")
                    print(actual_data or "<no data file>")
                print(f"FAILED: test {index} ({name})")
                return 1
            print("PASSED\n")
    print(f"All {len(cases)} UI tests passed.")
    return 0


if __name__ == "__main__":
    sys.exit(run())
