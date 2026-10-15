#!/usr/bin/env python3
"""Run fail-fast console UI tests described in test/ui-test-plan.md."""

from __future__ import annotations

import argparse
import difflib
import os
import re
import subprocess
import sys
import tempfile
from dataclasses import dataclass
from pathlib import Path


SEPARATOR = "_" * 60
BANNER = "\n".join(
    [
        " _____ _ _    _       _",
        "|__  /(_) | _(_) __ _(_)",
        "  / / | | |/ / |/ _` | |",
        " / /_ | |   <| | (_| | |",
        "/____||_|_|\\_\\_|\\__,_|_|",
    ]
)


@dataclass(frozen=True)
class TestCase:
    """One console test loaded from the Markdown plan."""

    name: str
    aim: str
    commands: str
    expected: str
    initial_data: str | None
    expected_data: str | None


def normalize(text: str) -> str:
    """Normalize platform newlines and ignore only final newline characters."""
    return text.replace("\r\n", "\n").replace("\r", "\n").rstrip("\n")


def expand_placeholders(text: str) -> str:
    """Expand stable UI elements used by expected-output blocks."""
    return text.replace("{{SEPARATOR}}", SEPARATOR).replace("{{BANNER}}", BANNER)


def fenced_block(section: str, heading: str, case_name: str) -> str:
    """Extract a fenced block beneath the requested level-three heading."""
    pattern = rf"### {re.escape(heading)}\s*\n```[^\n]*\n(.*?)\n```"
    match = re.search(pattern, section, flags=re.DOTALL)
    if not match:
        raise ValueError(f"{case_name}: missing fenced '{heading}' block")
    return match.group(1)


def optional_fenced_block(section: str, heading: str) -> str | None:
    """Extract an optional fenced block beneath a level-three heading."""
    pattern = rf"### {re.escape(heading)}\s*\n```[^\n]*\n(.*?)\n```"
    match = re.search(pattern, section, flags=re.DOTALL)
    return match.group(1) if match else None


def load_cases(plan_path: Path) -> list[TestCase]:
    """Parse all test cases from the Markdown test plan."""
    plan = plan_path.read_text(encoding="utf-8")
    headings = list(re.finditer(r"^## (.+)$", plan, flags=re.MULTILINE))
    cases: list[TestCase] = []

    for index, heading in enumerate(headings):
        name = heading.group(1).strip()
        start = heading.end()
        end = headings[index + 1].start() if index + 1 < len(headings) else len(plan)
        section = plan[start:end]
        aim_match = re.search(r"^\*\*Aim:\*\*\s*(.+)$", section, flags=re.MULTILINE)
        if not aim_match:
            raise ValueError(f"{name}: missing '**Aim:**' statement")
        cases.append(
            TestCase(
                name=name,
                aim=aim_match.group(1).strip(),
                commands=fenced_block(section, "Input", name),
                expected=expand_placeholders(fenced_block(section, "Expected output", name)),
                initial_data=optional_fenced_block(section, "Initial data file"),
                expected_data=optional_fenced_block(section, "Expected data file"),
            )
        )

    if not cases:
        raise ValueError("No test cases found in the plan")
    return cases


def compile_project(repo_root: Path, output_dir: Path) -> None:
    """Compile every Java source file into the temporary output directory."""
    sources = sorted((repo_root / "src/main/java").rglob("*.java"))
    if not sources:
        raise RuntimeError("No Java source files found in src/main/java")
    wrapper = "gradlew.bat" if os.name == "nt" else "./gradlew"
    dependencies = subprocess.run(
        [wrapper, "-q", "printCompileClasspath", "--console=plain"],
        cwd=repo_root,
        capture_output=True,
        text=True,
        timeout=60,
        check=False,
    )
    if dependencies.returncode != 0:
        raise RuntimeError(f"Dependency resolution failed:\n{dependencies.stdout}{dependencies.stderr}")
    result = subprocess.run(
        ["javac", "-Xlint:all", "-cp", dependencies.stdout.strip(),
         "-d", str(output_dir), *map(str, sources)],
        cwd=repo_root,
        capture_output=True,
        text=True,
        timeout=30,
        check=False,
    )
    if result.returncode != 0:
        raise RuntimeError(f"Compilation failed:\n{result.stdout}{result.stderr}")


def print_block(label: str, contents: str) -> None:
    """Print a labeled transcript block."""
    print(f"--- {label} ---")
    print(contents)


def run_case(case: TestCase, run_dir: Path, class_dir: Path, main_class: str) -> bool:
    """Run and report one case, returning whether its output matched."""
    if case.initial_data is not None:
        data_path = run_dir / "data/zikiai.txt"
        data_path.parent.mkdir(parents=True)
        data_path.write_text(case.initial_data + "\n", encoding="utf-8")

    stdin = case.commands + "\n"
    try:
        result = subprocess.run(
            ["java", "-cp", str(class_dir), main_class],
            cwd=run_dir,
            input=stdin,
            capture_output=True,
            text=True,
            timeout=10,
            check=False,
        )
    except subprocess.TimeoutExpired as error:
        print(f"FAIL: {case.name} (program timed out)")
        print_block("Console input", case.commands)
        print_block("Actual output before timeout", error.stdout or "")
        return False

    actual = normalize(result.stdout)
    expected = normalize(case.expected)
    print(f"\n=== {case.name} ===")
    print(f"Aim: {case.aim}")
    print_block("Console input", case.commands)
    print_block("Console output", actual)

    if result.returncode != 0:
        print(f"FAIL: program exited with status {result.returncode}")
        if result.stderr:
            print_block("Standard error", result.stderr.rstrip("\n"))
        return False

    if actual != expected:
        print("FAIL: actual output did not match expected output")
        print_block("Expected output", expected)
        print_block("Actual output", actual)
        diff = "\n".join(
            difflib.unified_diff(
                expected.splitlines(),
                actual.splitlines(),
                fromfile="expected",
                tofile="actual",
                lineterm="",
            )
        )
        print_block("Difference", diff)
        return False

    if case.expected_data is not None:
        data_path = run_dir / "data/zikiai.txt"
        actual_data = normalize(
            data_path.read_text(encoding="utf-8") if data_path.exists() else "<missing>"
        )
        expected_data = normalize(case.expected_data)
        print_block("Saved data file", actual_data)
        if actual_data != expected_data:
            print("FAIL: saved data did not match expected data")
            print_block("Expected data file", expected_data)
            print_block("Actual data file", actual_data)
            diff = "\n".join(
                difflib.unified_diff(
                    expected_data.splitlines(),
                    actual_data.splitlines(),
                    fromfile="expected-data",
                    tofile="actual-data",
                    lineterm="",
                )
            )
            print_block("Difference", diff)
            return False

    print("PASS")
    return True


def main() -> int:
    """Compile the project and run each planned case until one fails."""
    repo_root = Path(__file__).resolve().parents[4]
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument(
        "--plan",
        type=Path,
        default=repo_root / "test/ui-test-plan.md",
        help="Markdown test plan to run",
    )
    parser.add_argument("--main-class", default="zikiai.Zikiai", help="Java main class")
    args = parser.parse_args()

    try:
        cases = load_cases(args.plan)
        with tempfile.TemporaryDirectory(prefix="test-ui-") as directory:
            temporary_root = Path(directory)
            class_dir = temporary_root / "classes"
            class_dir.mkdir()
            compile_project(repo_root, class_dir)
            for completed, case in enumerate(cases):
                run_dir = temporary_root / f"case-{completed + 1}"
                run_dir.mkdir()
                if not run_case(case, run_dir, class_dir, args.main_class):
                    print(f"\nStopped after {completed} passing test case(s).")
                    return 1
    except (OSError, RuntimeError, ValueError, subprocess.TimeoutExpired) as error:
        print(f"FAIL: {error}", file=sys.stderr)
        return 1

    print(f"\nAll {len(cases)} test case(s) passed.")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
