#!/usr/bin/env python3
"""Runs UI transcript tests from test/ui-test-plan.md."""

from __future__ import annotations

import re
import subprocess
import sys
import tempfile
from dataclasses import dataclass
from pathlib import Path


PROJECT_ROOT = Path(__file__).resolve().parents[1]
PLAN_PATH = PROJECT_ROOT / "test" / "ui-test-plan.md"
BUILD_DIR = Path("/private/tmp/codex-ip-ui-tests")
SESSION_LOG_PATH = PROJECT_ROOT / "test" / "ui-test-session.log"


@dataclass
class TestCase:
    name: str
    aim: str
    setup_data_file: str | None
    inputs: str
    expected_output: str
    expected_data_file: str | None


def parse_test_plan() -> list[TestCase]:
    plan_text = PLAN_PATH.read_text()
    sections = re.split(r"^## Test Case: ", plan_text, flags=re.MULTILINE)[1:]
    test_cases: list[TestCase] = []

    for section in sections:
        name, body = section.split("\n", 1)
        aim_match = re.search(r"^Aim: (.+)$", body, flags=re.MULTILINE)
        inputs_match = re.search(
            r"^### Inputs\n```text\n(.*?)\n```", body, flags=re.MULTILINE | re.DOTALL
        )
        expected_match = re.search(
            r"^### Expected Output\n```text\n(.*?)\n```", body, flags=re.MULTILINE | re.DOTALL
        )
        setup_data_match = re.search(
            r"^### Setup Data File\n```text\n(.*?)\n```", body, flags=re.MULTILINE | re.DOTALL
        )
        expected_data_match = re.search(
            r"^### Expected Data File\n```text\n(.*?)\n```", body, flags=re.MULTILINE | re.DOTALL
        )

        if not aim_match or not inputs_match or not expected_match:
            raise ValueError(f"Test case '{name.strip()}' is missing aim, inputs, or expected output.")

        test_cases.append(
            TestCase(
                name=name.strip(),
                aim=aim_match.group(1).strip(),
                setup_data_file=setup_data_match.group(1) if setup_data_match else None,
                inputs=inputs_match.group(1),
                expected_output=expected_match.group(1),
                expected_data_file=expected_data_match.group(1) if expected_data_match else None,
            )
        )

    return test_cases


def compile_program() -> None:
    source_files = sorted(
        str(path)
        for path in (PROJECT_ROOT / "src" / "main" / "java").rglob("*.java")
        if path.name not in {"Launcher.java", "Main.java", "MainWindow.java"}
    )
    subprocess.run(["javac", "-d", str(BUILD_DIR), *source_files], check=True, cwd=PROJECT_ROOT)


def write_setup_data_file(test_case: TestCase, data_file: Path) -> None:
    if test_case.setup_data_file is None:
        return

    data_file.parent.mkdir(parents=True, exist_ok=True)
    data_file.write_text(test_case.setup_data_file + "\n")


def read_data_file(data_file: Path) -> str:
    if not data_file.exists():
        return ""
    return data_file.read_text().rstrip("\n")


def run_case(test_case: TestCase, working_dir: Path) -> str:
    result = subprocess.run(
        ["java", "-cp", str(BUILD_DIR), "duke.Duke"],
        input=test_case.inputs + "\n",
        text=True,
        capture_output=True,
        check=False,
        cwd=working_dir,
    )

    if result.returncode != 0:
        raise RuntimeError(
            f"Program exited with code {result.returncode}.\nSTDOUT:\n{result.stdout}\nSTDERR:\n{result.stderr}"
        )

    return result.stdout.rstrip("\n")


def append_transcript(lines: list[str], test_case: TestCase, actual_output: str, data_file: Path) -> None:
    lines.extend(
        [
            f"## {test_case.name}",
            f"Aim: {test_case.aim}",
            "",
        ]
    )
    if test_case.setup_data_file is not None:
        lines.extend(
            [
                "Setup data file:",
                test_case.setup_data_file,
                "",
            ]
        )
    lines.extend(
        [
            "Input:",
            test_case.inputs,
            "",
            "Output:",
            actual_output,
            "",
        ]
    )
    if test_case.expected_data_file is not None:
        lines.extend(
            [
                "Data file:",
                read_data_file(data_file),
                "",
            ]
        )


def main() -> int:
    test_cases = parse_test_plan()
    compile_program()

    transcript_lines = ["# UI Test Session", ""]

    for test_case in test_cases:
        # Never replace or delete the user's real data/duke.txt when testing.
        with tempfile.TemporaryDirectory(prefix="geen-ui-test-") as case_dir:
            working_dir = Path(case_dir)
            data_file = working_dir / "data" / "duke.txt"
            write_setup_data_file(test_case, data_file)
            actual_output = run_case(test_case, working_dir)
            append_transcript(transcript_lines, test_case, actual_output, data_file)
            actual_data_file = read_data_file(data_file)

        if actual_output != test_case.expected_output:
            SESSION_LOG_PATH.write_text("\n".join(transcript_lines))
            print(f"FAILED: {test_case.name}")
            print("\nExpected:")
            print(test_case.expected_output)
            print("\nActual:")
            print(actual_output)
            return 1

        if test_case.expected_data_file is not None:
            if actual_data_file != test_case.expected_data_file:
                SESSION_LOG_PATH.write_text("\n".join(transcript_lines))
                print(f"FAILED: {test_case.name}")
                print("\nExpected data file:")
                print(test_case.expected_data_file)
                print("\nActual data file:")
                print(actual_data_file)
                return 1

    SESSION_LOG_PATH.write_text("\n".join(transcript_lines))
    print(f"Passed {len(test_cases)} UI test case(s).")
    print(f"Session log: {SESSION_LOG_PATH}")
    return 0


if __name__ == "__main__":
    sys.exit(main())
