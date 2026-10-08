#!/usr/bin/env python3
"""Run UI test cases from test/ui-test-plan.md against the Rudra console app."""

from __future__ import annotations

import re
import subprocess
import sys
from dataclasses import dataclass
from pathlib import Path


ROOT = Path(__file__).resolve().parents[4]
PLAN_PATH = ROOT / "test" / "ui-test-plan.md"
LOG_PATH = ROOT / "test" / "ui-test-session.log"
DATA_PATH = ROOT / "data" / "rudra.txt"
SRC_DIR = ROOT / "src" / "main" / "java"
MAIN_CLASS = "rudra.Rudra"


@dataclass
class TestCase:
    name: str
    aim: str
    preloaded_save_file: str
    save_path_mode: str
    inputs: str
    expected_output: str


def parse_cases(plan_text: str) -> list[TestCase]:
    pattern = re.compile(
        r"^## (?P<name>.+?)\n"
        r"Aim:\s*(?P<aim>.+?)\n+"
        r"(?:### Preloaded Save File\n```(?:text)?\n(?P<preload>.*?)\n```\n+)?"
        r"(?:Save Path Mode:\s*(?P<save_path_mode>.+?)\n+)?"
        r"### Inputs\n```(?:text)?\n(?P<inputs>.*?)\n```\n+"
        r"### Expected Output\n```(?:text)?\n(?P<expected>.*?)\n```",
        re.MULTILINE | re.DOTALL,
    )
    cases = []
    for match in pattern.finditer(plan_text):
        cases.append(
            TestCase(
                name=match.group("name").strip(),
                aim=match.group("aim").strip(),
                preloaded_save_file=(match.group("preload") or "").rstrip(),
                save_path_mode=(match.group("save_path_mode") or "file").strip(),
                inputs=match.group("inputs").rstrip(),
                expected_output=match.group("expected").rstrip(),
            )
        )
    return cases


def normalize(text: str) -> str:
    return text.replace("\r\n", "\n").rstrip()


def compile_project() -> None:
    java_files = sorted(str(path) for path in SRC_DIR.rglob("*.java"))
    if not java_files:
        raise SystemExit("No Java source files found under src/main/java.")

    result = subprocess.run(
        ["javac", *java_files],
        cwd=ROOT,
        capture_output=True,
        text=True,
        check=False,
    )
    if result.returncode != 0:
        sys.stderr.write("Compilation failed.\n")
        if result.stdout:
            sys.stderr.write(result.stdout)
        if result.stderr:
            sys.stderr.write(result.stderr)
        raise SystemExit(result.returncode)


def run_case(test_case: TestCase) -> tuple[str, str]:
    DATA_PATH.parent.mkdir(parents=True, exist_ok=True)
    if DATA_PATH.exists():
        if DATA_PATH.is_dir():
            DATA_PATH.rmdir()
        else:
            DATA_PATH.unlink()

    if test_case.save_path_mode == "directory":
        DATA_PATH.mkdir(parents=True, exist_ok=True)
    elif test_case.preloaded_save_file:
        DATA_PATH.write_text(test_case.preloaded_save_file + "\n")

    process = subprocess.run(
        ["java", "-cp", str(SRC_DIR), MAIN_CLASS],
        cwd=ROOT,
        input=test_case.inputs + "\n",
        capture_output=True,
        text=True,
        check=False,
    )
    return normalize(process.stdout), normalize(test_case.expected_output)


def format_transcript(test_case: TestCase, actual_output: str) -> str:
    return (
        f"## {test_case.name}\n"
        f"Aim: {test_case.aim}\n\n"
        "### Console Input\n"
        "```text\n"
        f"{test_case.inputs}\n"
        "```\n\n"
        "### Console Output\n"
        "```text\n"
        f"{actual_output}\n"
        "```\n"
    )


def main() -> int:
    if not PLAN_PATH.exists():
        sys.stderr.write(f"Missing test plan: {PLAN_PATH}\n")
        return 1

    cases = parse_cases(PLAN_PATH.read_text())
    if not cases:
        sys.stderr.write(
            "No valid test cases found in test/ui-test-plan.md. "
            "Use the documented ## / Aim / Inputs / Expected Output format.\n"
        )
        return 1

    compile_project()

    transcripts: list[str] = []
    for index, test_case in enumerate(cases, start=1):
        actual_output, expected_output = run_case(test_case)
        transcripts.append(format_transcript(test_case, actual_output))
        LOG_PATH.write_text("\n\n".join(transcripts) + "\n")

        print(f"[{index}/{len(cases)}] {test_case.name}")
        if actual_output != expected_output:
            print("FAIL")
            print(f"Transcript saved to: {LOG_PATH}")
            print("Expected output:")
            print(expected_output)
            print("Actual output:")
            print(actual_output)
            return 1

        print("PASS")

    print(f"All {len(cases)} test(s) passed.")
    print(f"Transcript saved to: {LOG_PATH}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
