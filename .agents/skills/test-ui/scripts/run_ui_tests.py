#!/usr/bin/env python3
"""Run fail-fast console UI tests defined in test/ui-test-plan.md."""

from __future__ import annotations

import argparse
import os
from pathlib import Path
import re
import shutil
import subprocess
import sys
import tempfile


REPO_ROOT = Path(__file__).resolve().parents[4]
DEFAULT_PLAN = REPO_ROOT / "test" / "ui-test-plan.md"
REQUIRED_SDKMAN_JAVA = Path.home() / ".sdkman" / "candidates" / "java" / "25.0.3.fx-zulu"


class PlanError(ValueError):
    """Indicates that the UI test plan does not follow the required format."""


def parse_fenced_block(lines: list[str], start: int, section: str) -> tuple[str, int]:
    """Extract a fenced text block starting at or after the given line."""
    index = start
    while index < len(lines) and not lines[index].startswith("```"):
        if lines[index].startswith("## ") or lines[index].startswith("### "):
            raise PlanError(f"Missing fenced block for {section}")
        index += 1

    if index == len(lines):
        raise PlanError(f"Missing fenced block for {section}")

    closing_index = index + 1
    while closing_index < len(lines) and not lines[closing_index].startswith("```"):
        closing_index += 1

    if closing_index == len(lines):
        raise PlanError(f"Unclosed fenced block for {section}")

    return "\n".join(lines[index + 1 : closing_index]), closing_index + 1


def parse_plan(plan_path: Path) -> list[dict[str, str]]:
    """Parse test cases from the Markdown test plan."""
    lines = plan_path.read_text(encoding="utf-8").splitlines()
    cases: list[dict[str, str]] = []
    index = 0

    while index < len(lines):
        if not re.match(r"^## TC-[A-Za-z0-9_-]+:", lines[index]):
            index += 1
            continue

        title = lines[index][3:].strip()
        case: dict[str, str] = {"title": title}
        index += 1

        while index < len(lines) and not lines[index].startswith("## "):
            if lines[index] == "### Aim":
                index += 1
                aim_lines: list[str] = []
                while index < len(lines) and not lines[index].startswith("### "):
                    if lines[index].strip():
                        aim_lines.append(lines[index].strip())
                    index += 1
                case["aim"] = " ".join(aim_lines)
                continue

            if lines[index] == "### Input":
                case["input"], index = parse_fenced_block(lines, index + 1, "Input")
                continue

            if lines[index] == "### Expected output":
                case["expected"], index = parse_fenced_block(
                    lines, index + 1, "Expected output"
                )
                continue

            if lines[index] == "### Expected data file":
                case["expected_data"], index = parse_fenced_block(
                    lines, index + 1, "Expected data file"
                )
                continue

            if lines[index] == "### Initial data file":
                case["initial_data"], index = parse_fenced_block(
                    lines, index + 1, "Initial data file"
                )
                continue

            if lines[index] == "### Initial data path":
                case["initial_data_path"], index = parse_fenced_block(
                    lines, index + 1, "Initial data path"
                )
                continue

            index += 1

        missing = [key for key in ("aim", "input", "expected") if key not in case]
        if missing:
            raise PlanError(f"{title} is missing: {', '.join(missing)}")
        cases.append(case)

    if not cases:
        raise PlanError("No test cases found")
    return cases


def resolve_java_home(requested_home: str | None) -> Path:
    """Find a Java 25 installation, preferring the project-required SDKMAN version."""
    candidates = []
    if requested_home:
        candidates.append(Path(requested_home).expanduser())
    if REQUIRED_SDKMAN_JAVA.is_dir():
        candidates.append(REQUIRED_SDKMAN_JAVA)
    if os.environ.get("JAVA_HOME"):
        candidates.append(Path(os.environ["JAVA_HOME"]))

    javac_on_path = shutil.which("javac")
    if javac_on_path:
        candidates.append(Path(javac_on_path).resolve().parents[1])

    for candidate in candidates:
        javac = candidate / "bin" / "javac"
        java = candidate / "bin" / "java"
        if not javac.is_file() or not java.is_file():
            continue
        version = subprocess.run(
            [str(javac), "-version"], capture_output=True, text=True, check=False
        )
        version_text = (version.stdout + version.stderr).strip()
        if re.search(r"\bjavac 25(?:\.|\b)", version_text):
            return candidate

    raise RuntimeError("Java 25 was not found; pass its installation path with --java-home")


def normalize_output(output: str) -> str:
    """Normalize platform line endings and optional final line breaks."""
    return output.replace("\r\n", "\n").replace("\r", "\n").rstrip("\n")


def show_block(label: str, content: str) -> None:
    """Print a clearly delimited transcript or comparison block."""
    print(f"--- {label} ---")
    print(content)
    print(f"--- end {label} ---")


def run_tests(plan_path: Path, java_home: Path) -> int:
    """Compile Nova and run plan cases, stopping at the first failure."""
    cases = parse_plan(plan_path)
    source_files = sorted((REPO_ROOT / "src" / "main" / "java").rglob("*.java"))
    if not source_files:
        raise RuntimeError("No Java source files found in src/main/java")

    with tempfile.TemporaryDirectory(prefix="nova-ui-test-") as build_directory:
        compilation = subprocess.run(
            [
                str(java_home / "bin" / "javac"),
                "-d",
                build_directory,
                *map(str, source_files),
            ],
            cwd=REPO_ROOT,
            capture_output=True,
            text=True,
            check=False,
        )
        if compilation.returncode != 0:
            print("Compilation failed; no UI tests were run.", file=sys.stderr)
            print(compilation.stdout, end="", file=sys.stderr)
            print(compilation.stderr, end="", file=sys.stderr)
            return 1

        for case in cases:
            entered_input = case["input"]
            process_input = entered_input + ("\n" if entered_input else "")
            with tempfile.TemporaryDirectory(prefix="nova-ui-case-") as case_directory:
                case_root = Path(case_directory)
                data_file = case_root / "data" / "nova.txt"
                if "initial_data" in case:
                    data_file.parent.mkdir(parents=True)
                    data_file.write_text(case["initial_data"] + "\n", encoding="utf-8")
                elif case.get("initial_data_path") == "directory":
                    data_file.mkdir(parents=True)

                is_read_only = case.get("initial_data_path") == "read-only-directory"
                if is_read_only:
                    data_file.parent.chmod(0o555)
                try:
                    result = subprocess.run(
                        [
                            str(java_home / "bin" / "java"),
                            "-cp",
                            build_directory,
                            "nova.Nova",
                        ],
                        cwd=case_root,
                        input=process_input,
                        capture_output=True,
                        text=True,
                        timeout=10,
                        check=False,
                    )
                finally:
                    if is_read_only:
                        data_file.parent.chmod(0o755)

                print(f"\n=== {case['title']} ===")
                print(f"Aim: {case['aim']}")
                show_block("console input", entered_input)
                show_block("console output", result.stdout.rstrip("\n"))

                expected = normalize_output(case["expected"])
                actual = normalize_output(result.stdout)
                if result.returncode != 0 or actual != expected:
                    print("RESULT: FAIL")
                    show_block("expected output", expected)
                    show_block("actual output", actual)
                    if result.stderr:
                        show_block("runtime error", result.stderr.rstrip("\n"))
                    print("Test session terminated after the first failure.")
                    return 1

                if "expected_data" in case:
                    actual_data = data_file.read_text(encoding="utf-8")
                    expected_data = normalize_output(case["expected_data"])
                    normalized_data = normalize_output(actual_data)
                    show_block("saved data file", normalized_data)
                    if normalized_data != expected_data:
                        print("RESULT: FAIL")
                        show_block("expected data file", expected_data)
                        show_block("actual data file", normalized_data)
                        print("Test session terminated after the first failure.")
                        return 1

                print("RESULT: PASS")

    print(f"\nAll {len(cases)} UI test cases passed.")
    return 0


def main() -> int:
    """Parse command-line options and run the UI test session."""
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--plan", type=Path, default=DEFAULT_PLAN)
    parser.add_argument("--java-home")
    arguments = parser.parse_args()

    try:
        java_home = resolve_java_home(arguments.java_home)
        return run_tests(arguments.plan.resolve(), java_home)
    except (OSError, PlanError, RuntimeError, subprocess.TimeoutExpired) as error:
        print(f"UI test session could not complete: {error}", file=sys.stderr)
        return 1


if __name__ == "__main__":
    raise SystemExit(main())
