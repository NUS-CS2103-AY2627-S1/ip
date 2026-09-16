#!/usr/bin/env python3
"""Run the project's Markdown-defined console UI tests."""

from __future__ import annotations

import argparse
import re
import subprocess
import sys
import tempfile
from dataclasses import dataclass
from pathlib import Path


@dataclass
class TestCase:
    """One input/output comparison from the UI test plan."""

    name: str
    aim: str
    input_text: str
    expected_output: str
    initial_data: str | None = None


def normalize_line_endings(value: str) -> str:
    """Normalize platform-specific line endings for deterministic comparisons."""

    return value.replace("\r\n", "\n").replace("\r", "\n")


def fenced_block(section: str, heading: str) -> str:
    """Extract the first fenced text block following a section heading."""

    pattern = rf"###\s+{re.escape(heading)}\s*\n\s*```[^\n]*\n(.*?)\n\s*```"
    match = re.search(pattern, section, re.DOTALL | re.IGNORECASE)
    if match is None:
        raise ValueError(f"missing fenced {heading.lower()} block")

    block = match.group(1)
    return block + "\n" if block else ""


def optional_fenced_block(section: str, heading: str) -> str | None:
    """Extract an optional fenced text block following a section heading."""

    pattern = rf"###\s+{re.escape(heading)}\s*\n\s*```[^\n]*\n(.*?)\n\s*```"
    match = re.search(pattern, section, re.DOTALL | re.IGNORECASE)
    if match is None:
        return None

    return match.group(1) + "\n" if match.group(1) else ""


def read_plan(plan_path: Path) -> list[TestCase]:
    """Read all test cases from the project's Markdown test plan."""

    content = plan_path.read_text(encoding="utf-8")
    matches = list(re.finditer(r"^##\s+(Test Case[^\n]*)$", content, re.MULTILINE))
    if not matches:
        raise ValueError("the plan contains no '## Test Case ...' sections")

    test_cases: list[TestCase] = []
    for index, match in enumerate(matches):
        section_end = matches[index + 1].start() if index + 1 < len(matches) else len(content)
        section = content[match.end():section_end]
        aim_match = re.search(r"^-\s*Aim:\s*(.+)$", section, re.MULTILINE | re.IGNORECASE)
        if aim_match is None:
            raise ValueError(f"{match.group(1)} is missing an Aim line")

        test_cases.append(
            TestCase(
                name=match.group(1).strip(),
                aim=aim_match.group(1).strip(),
                input_text=fenced_block(section, "Input"),
                expected_output=fenced_block(section, "Expected output"),
                initial_data=optional_fenced_block(section, "Initial data"),
            )
        )

    return test_cases


def print_block(label: str, value: str) -> None:
    """Print a labeled block while making empty output visible."""

    print(f"{label}:")
    print(value if value else "<empty>", end="" if value.endswith("\n") else "\n")


def check_java_version() -> None:
    """Require the Java 25 toolchain specified by the project instructions."""

    result = subprocess.run(["javac", "-version"], capture_output=True, text=True)
    version_output = result.stdout + result.stderr
    match = re.search(r"(?:javac|openjdk)\s+(\d+)", version_output)
    if result.returncode != 0 or match is None or match.group(1) != "25":
        raise RuntimeError(
            "Java 25 is required. Switch to Java 25, for example with "
            "`sdk use java 25.0.3.fx-zulu`, then rerun the UI tests."
        )


def compile_project(project_root: Path, build_dir: Path) -> None:
    """Compile all Java source files into a temporary build directory."""

    source_files = sorted((project_root / "src/main/java").rglob("*.java"))
    if not source_files:
        raise RuntimeError("no Java source files found in src/main/java")

    result = subprocess.run(
        ["javac", "-d", str(build_dir), *(str(path) for path in source_files)],
        cwd=project_root,
        capture_output=True,
        text=True,
    )
    if result.returncode != 0:
        print("Compilation failed.", file=sys.stderr)
        print(result.stdout, end="", file=sys.stderr)
        print(result.stderr, end="", file=sys.stderr)
        raise RuntimeError("cannot run UI tests after compilation failure")


def run_test_case(test_case: TestCase, project_root: Path, build_dir: Path) -> bool:
    """Run one test case, print its session, and return whether it passed."""

    data_file = project_root / "data" / "panda.txt"
    data_file.unlink(missing_ok=True)
    if test_case.initial_data is not None:
        data_file.parent.mkdir(parents=True, exist_ok=True)
        data_file.write_text(normalize_line_endings(test_case.initial_data), encoding="utf-8")

    input_text = normalize_line_endings(test_case.input_text)
    expected_output = normalize_line_endings(test_case.expected_output)
    result = subprocess.run(
        ["java", "-cp", str(build_dir), "panda.app.Panda"],
        cwd=project_root,
        input=input_text,
        capture_output=True,
        text=True,
    )
    actual_output = normalize_line_endings(result.stdout)

    print(f"\n=== {test_case.name} ===")
    print(f"Aim: {test_case.aim}")
    print_block("Console input", input_text)
    print_block("Console output", actual_output)

    if result.returncode != 0:
        print(f"FAIL: program exited with status {result.returncode}.")
        if result.stderr:
            print_block("Process error output", result.stderr)
        print_block("Expected output", expected_output)
        return False

    if actual_output != expected_output:
        print("FAIL: actual output does not match expected output.")
        print_block("Expected output", expected_output)
        print_block("Actual output", actual_output)
        return False

    print("PASS")
    return True


def main() -> int:
    """Run every test case until the first failure."""

    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--plan", type=Path, default=Path("test/ui-test-plan.md"))
    args = parser.parse_args()

    plan_path = args.plan.resolve()
    project_root = plan_path.parent.parent
    try:
        test_cases = read_plan(plan_path)
        check_java_version()
        with tempfile.TemporaryDirectory(prefix="panda-ui-tests-") as build_path:
            compile_project(project_root, Path(build_path))
            for test_case in test_cases:
                if not run_test_case(test_case, project_root, Path(build_path)):
                    return 1
    except (OSError, ValueError, RuntimeError) as error:
        print(f"ERROR: {error}", file=sys.stderr)
        return 1
    finally:
        (project_root / "data" / "panda.txt").unlink(missing_ok=True)

    print(f"\nAll {len(test_cases)} UI test case(s) passed.")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
