#!/usr/bin/env python3
"""Run exact transcript tests defined in test/ui-test-plan.md."""

from __future__ import annotations

import argparse
import re
import shutil
import subprocess
import sys
import tempfile
from dataclasses import dataclass
from pathlib import Path


CASE_PATTERN = re.compile(
    r"^## (?P<name>.+?)\n\n"
    r"\*\*Aim:\*\* (?P<aim>.+?)\n\n"
    r"(?:### Initial data file\n\n```text\n(?P<initial_data>.*?)```\n\n)?"
    r"### Input\n\n```text\n(?P<input>.*?)```\n\n"
    r"### Expected output\n\n```text\n(?P<expected>.*?)```"
    r"(?:\n\n### Restart input\n\n```text\n(?P<restart_input>.*?)```\n\n"
    r"### Expected restart output\n\n```text\n(?P<restart_expected>.*?)```)?"
    r"(?=\n\n## |\n?\Z)",
    re.MULTILINE | re.DOTALL,
)
JAVA_25_PATTERN = re.compile(r'(?:version "|javac )25(?:\.|\s|\")')
COMPILE_TIMEOUT_SECONDS = 30
CASE_TIMEOUT_SECONDS = 10


@dataclass(frozen=True)
class TestCase:
    """Contains one input transcript and its expected console output."""

    name: str
    aim: str
    initial_data: str | None
    program_input: str
    expected_output: str
    restart_input: str | None
    expected_restart_output: str | None


def load_test_cases(plan_path: Path) -> list[TestCase]:
    """Parse all test cases from the Markdown test plan."""
    plan = plan_path.read_text(encoding="utf-8")
    headings = re.findall(r"^## (.+)$", plan, re.MULTILINE)
    cases = [
        TestCase(
            name=match.group("name"),
            aim=match.group("aim"),
            initial_data=match.group("initial_data"),
            program_input=match.group("input"),
            expected_output=match.group("expected"),
            restart_input=match.group("restart_input"),
            expected_restart_output=match.group("restart_expected"),
        )
        for match in CASE_PATTERN.finditer(plan)
    ]
    if not cases:
        raise ValueError(f"No valid test cases found in {plan_path}")
    if len(cases) != len(headings):
        raise ValueError(
            f"Parsed {len(cases)} of {len(headings)} test cases in {plan_path}; "
            "check each case's Aim, Input, and Expected output sections"
        )
    case_names = [case.name for case in cases]
    if len(case_names) != len(set(case_names)):
        raise ValueError(f"Duplicate test case names found in {plan_path}")
    return cases


def find_java_25() -> tuple[Path, Path]:
    """Locate the project SDK, falling back to Java 25 on PATH."""
    sdk_dir = Path.home() / ".sdkman/candidates/java/25.0.3.fx-zulu/bin"
    java = sdk_dir / "java"
    javac = sdk_dir / "javac"
    if not java.is_file() or not javac.is_file():
        java_path = shutil.which("java")
        javac_path = shutil.which("javac")
        if java_path is None or javac_path is None:
            raise RuntimeError("Java and javac were not found")
        java = Path(java_path)
        javac = Path(javac_path)

    for executable in (java, javac):
        version = subprocess.run(
            [str(executable), "-version"],
            capture_output=True,
            text=True,
            check=False,
            timeout=5,
        )
        version_text = version.stdout + version.stderr
        if version.returncode != 0 or not JAVA_25_PATTERN.search(version_text):
            raise RuntimeError(f"Java 25 is required, but {executable} reported: {version_text.strip()}")
    return java, javac


def print_transcript(label: str, content: str) -> None:
    """Print a transcript while preserving its final newline clearly."""
    print(f"--- {label} ---")
    print(content, end="" if content.endswith("\n") else "\n")


def captured_text(content: str | bytes | None) -> str:
    """Normalize subprocess output, including bytes returned after a timeout."""
    if isinstance(content, bytes):
        return content.decode(errors="replace")
    return content or ""


def main() -> int:
    repository_root = Path(__file__).resolve().parents[4]
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument(
        "plan",
        nargs="?",
        default="test/ui-test-plan.md",
        type=Path,
        help="Markdown test plan (default: test/ui-test-plan.md)",
    )
    args = parser.parse_args()
    plan_path = args.plan if args.plan.is_absolute() else repository_root / args.plan

    try:
        cases = load_test_cases(plan_path)
        java, javac = find_java_25()
    except (OSError, RuntimeError, subprocess.TimeoutExpired, ValueError) as error:
        print(f"ERROR: {error}", file=sys.stderr)
        return 2

    source_files = sorted(
        source_file
        for source_file in (repository_root / "src/main/java").rglob("*.java")
        if "gui" not in source_file.parts and source_file.name != "Launcher.java"
    )
    if not source_files:
        print("ERROR: No Java source files found", file=sys.stderr)
        return 2

    with tempfile.TemporaryDirectory(prefix="snoopy-ui-test-") as build_dir:
        try:
            compilation = subprocess.run(
                [str(javac), "-d", build_dir, *map(str, source_files)],
                capture_output=True,
                text=True,
                check=False,
                cwd=repository_root,
                timeout=COMPILE_TIMEOUT_SECONDS,
            )
        except subprocess.TimeoutExpired:
            print(
                f"ERROR: Compilation exceeded {COMPILE_TIMEOUT_SECONDS} seconds",
                file=sys.stderr,
            )
            return 2
        if compilation.returncode != 0:
            print("ERROR: Compilation failed", file=sys.stderr)
            print(compilation.stdout + compilation.stderr, end="", file=sys.stderr)
            return 2

        for case in cases:
            case_directory = Path(build_dir) / case.name.split(":", 1)[0].lower()
            case_directory.mkdir()
            if case.initial_data is not None:
                data_file = case_directory / "data/snoopy.txt"
                data_file.parent.mkdir()
                data_file.write_text(case.initial_data, encoding="utf-8")

            print(f"=== {case.name} ===")
            print(f"Aim: {case.aim}")
            print_transcript("Console input", case.program_input)
            try:
                result = subprocess.run(
                    [str(java), "-cp", build_dir, "snoopy.Snoopy"],
                    input=case.program_input,
                    capture_output=True,
                    text=True,
                    check=False,
                    cwd=case_directory,
                    timeout=CASE_TIMEOUT_SECONDS,
                )
            except subprocess.TimeoutExpired as error:
                actual_output = captured_text(error.stdout) + captured_text(error.stderr)
                print_transcript("Console output", actual_output)
                print(f"FAIL: {case.name} exceeded {CASE_TIMEOUT_SECONDS} seconds")
                print_transcript("Expected output", case.expected_output)
                print_transcript("Actual output", actual_output)
                return 1
            actual_output = result.stdout + result.stderr
            print_transcript("Console output", actual_output)

            if result.returncode != 0 or actual_output != case.expected_output:
                print(f"FAIL: {case.name}")
                print_transcript("Expected output", case.expected_output)
                print_transcript("Actual output", actual_output)
                return 1

            if case.restart_input is not None:
                print_transcript("Console input after restart", case.restart_input)
                try:
                    restart_result = subprocess.run(
                        [str(java), "-cp", build_dir, "snoopy.Snoopy"],
                        input=case.restart_input,
                        capture_output=True,
                        text=True,
                        check=False,
                        cwd=case_directory,
                        timeout=CASE_TIMEOUT_SECONDS,
                    )
                except subprocess.TimeoutExpired as error:
                    restart_output = captured_text(error.stdout) + captured_text(error.stderr)
                    print_transcript("Console output after restart", restart_output)
                    print(f"FAIL: {case.name} restart exceeded {CASE_TIMEOUT_SECONDS} seconds")
                    print_transcript("Expected restart output", case.expected_restart_output or "")
                    print_transcript("Actual restart output", restart_output)
                    return 1
                restart_output = restart_result.stdout + restart_result.stderr
                print_transcript("Console output after restart", restart_output)
                if (restart_result.returncode != 0
                        or restart_output != case.expected_restart_output):
                    print(f"FAIL: {case.name} restart")
                    print_transcript("Expected restart output", case.expected_restart_output or "")
                    print_transcript("Actual restart output", restart_output)
                    return 1
            print(f"PASS: {case.name}\n")

    print(f"All {len(cases)} UI test case(s) passed.")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
