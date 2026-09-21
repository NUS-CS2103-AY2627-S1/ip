#!/usr/bin/env python3
"""Run the command-line UI tests described in test/ui-test-plan.md."""

from __future__ import annotations

import argparse
import re
import shutil
import subprocess
import sys
import tempfile
from dataclasses import dataclass
from pathlib import Path


@dataclass(frozen=True)
class TestCase:
    """One UI test case parsed from the Markdown test plan."""

    name: str
    input_text: str
    expected_lines: list[str]
    initial_file_lines: list[str] | None = None
    expected_file_lines: list[str] | None = None


CASE_PATTERN = re.compile(r"^## Test case \d+:\s*(.+)$", re.MULTILINE)
BLOCK_PATTERN = re.compile(
    r"^Input:\s*\n```[^\n]*\n(?P<input>.*?)\n```\s*\n"
    r"Expected output:\s*\n```[^\n]*\n(?P<expected>.*?)\n```",
    re.MULTILINE | re.DOTALL,
)
INITIAL_FILE_PATTERN = re.compile(
    r"^Initial data/duke\.txt contents:\s*\n```[^\n]*\n(?P<data>.*?)\n```",
    re.MULTILINE | re.DOTALL,
)
EXPECTED_FILE_PATTERN = re.compile(
    r"^Expected `data/duke\.txt` contents after the case:\s*\n```[^\n]*\n(?P<data>.*?)\n```",
    re.MULTILINE | re.DOTALL,
)


def parse_plan(plan_path: Path) -> list[TestCase]:
    """Parse test cases from the project's Markdown test plan."""
    plan_text = plan_path.read_text(encoding="utf-8")
    case_matches = list(CASE_PATTERN.finditer(plan_text))
    if not case_matches:
        raise ValueError(f"No test cases found in {plan_path}")

    cases: list[TestCase] = []
    for index, case_match in enumerate(case_matches):
        section_end = (
            case_matches[index + 1].start()
            if index + 1 < len(case_matches)
            else len(plan_text)
        )
        section = plan_text[case_match.end() : section_end]
        block_match = BLOCK_PATTERN.search(section)
        if block_match is None:
            raise ValueError(
                f"Test case '{case_match.group(1)}' must contain Input and Expected output blocks"
            )

        expected_lines = [
            line for line in block_match.group("expected").splitlines() if line.strip()
        ]
        if not expected_lines:
            raise ValueError(f"Test case '{case_match.group(1)}' has no expected output lines")

        initial_file_match = INITIAL_FILE_PATTERN.search(section)
        expected_file_match = EXPECTED_FILE_PATTERN.search(section)

        cases.append(
            TestCase(
                name=case_match.group(1).strip(),
                input_text=block_match.group("input"),
                expected_lines=expected_lines,
                initial_file_lines=(
                    initial_file_match.group("data").splitlines()
                    if initial_file_match is not None
                    else None
                ),
                expected_file_lines=(
                    expected_file_match.group("data").splitlines()
                    if expected_file_match is not None
                    else None
                ),
            )
        )
    return cases


def command_version(command: str) -> str:
    """Return the first version line from a Java command."""
    completed = subprocess.run(
        [command, "-version"], capture_output=True, text=True, check=False
    )
    version_output = (completed.stderr or completed.stdout).splitlines()
    if completed.returncode != 0 or not version_output:
        raise RuntimeError(f"Could not determine the version of {command}")
    return version_output[0]


def compile_sources(project_root: Path, javac: str, classes_dir: Path) -> None:
    """Compile all application sources into a temporary classes directory."""
    sources = sorted((project_root / "src/main/java").rglob("*.java"))
    if not sources:
        raise RuntimeError("No Java sources found in src/main/java")
    completed = subprocess.run(
        [javac, "-Xlint:all", "-d", str(classes_dir), *(str(source) for source in sources)],
        cwd=project_root,
        capture_output=True,
        text=True,
        check=False,
    )
    if completed.returncode != 0:
        raise RuntimeError("Compilation failed:\n" + completed.stdout + completed.stderr)


def check_expected_output(actual: str, expected_lines: list[str]) -> str | None:
    """Find the first expected line that is missing or out of order."""
    search_start = 0
    for expected in expected_lines:
        found_at = actual.find(expected, search_start)
        if found_at == -1:
            return expected
        search_start = found_at + len(expected)
    return None


def run_case(project_root: Path, java: str, classes_dir: Path, case: TestCase) -> str:
    """Run one case and return its console output, or raise on failure."""
    with tempfile.TemporaryDirectory(prefix="caitlyn-case-") as case_dir:
        case_path = Path(case_dir)
        data_path = case_path / "data" / "duke.txt"
        if case.initial_file_lines is not None:
            data_path.parent.mkdir()
            data_path.write_text("\n".join(case.initial_file_lines) + "\n", encoding="utf-8")

        completed = subprocess.run(
            [java, "-cp", str(classes_dir), "caitlyn.Caitlyn"],
            cwd=case_path,
            input=case.input_text,
            capture_output=True,
            text=True,
            timeout=10,
            check=False,
        )
        output = completed.stdout + completed.stderr
        if completed.returncode != 0:
            raise RuntimeError(f"Caitlyn exited with status {completed.returncode}")
        missing = check_expected_output(output, case.expected_lines)
        if missing is not None:
            expected = "\n".join(case.expected_lines)
            raise AssertionError(
                f"Missing or out-of-order expected output: {missing!r}\n"
                f"Expected lines:\n{expected}\n"
                f"Actual console output:\n{output}"
            )

        if case.expected_file_lines is not None:
            if not data_path.exists():
                raise AssertionError("Expected data/duke.txt to be created")
            actual_file_lines = data_path.read_text(encoding="utf-8").splitlines()
            if actual_file_lines != case.expected_file_lines:
                raise AssertionError(
                    "Saved data/duke.txt did not match the expected contents:\n"
                    f"Expected:\n{chr(10).join(case.expected_file_lines)}\n"
                    f"Actual:\n{chr(10).join(actual_file_lines)}"
                )
        return output


def main() -> int:
    """Compile Caitlyn, execute every plan case, and print transcripts."""
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--plan", default="test/ui-test-plan.md", type=Path)
    parser.add_argument("--java", default=shutil.which("java") or "java")
    parser.add_argument("--javac", default=shutil.which("javac") or "javac")
    args = parser.parse_args()

    project_root = Path(__file__).resolve().parents[4]
    plan_path = (project_root / args.plan).resolve() if not args.plan.is_absolute() else args.plan

    try:
        java_version = command_version(args.java)
        javac_version = command_version(args.javac)
        if "25" not in java_version or "25" not in javac_version:
            raise RuntimeError(
                f"Java 25 is required; found {java_version!r} and {javac_version!r}"
            )
        cases = parse_plan(plan_path)
        with tempfile.TemporaryDirectory(prefix="caitlyn-ui-") as temp_dir:
            classes_dir = Path(temp_dir) / "classes"
            classes_dir.mkdir()
            compile_sources(project_root, args.javac, classes_dir)
            print(f"Java toolchain: {java_version}; {javac_version}")
            for number, case in enumerate(cases, start=1):
                print(f"\n=== Test case {number}: {case.name} ===")
                print("--- console input ---")
                print(case.input_text, end="" if case.input_text.endswith("\n") else "\n")
                output = run_case(project_root, args.java, classes_dir, case)
                print("--- console output ---")
                print(output, end="" if output.endswith("\n") else "\n")
                print("PASS")
    except (AssertionError, OSError, RuntimeError, subprocess.TimeoutExpired, ValueError) as error:
        print(f"\nFAIL: {error}", file=sys.stderr)
        return 1
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
