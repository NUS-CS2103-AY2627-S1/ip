#!/usr/bin/env python3
"""Run fail-fast console UI tests recorded in test/ui-test-plan.md."""

import argparse
import os
import re
import shutil
import subprocess
import sys
import tempfile
from dataclasses import dataclass
from pathlib import Path


@dataclass
class TestCase:
    """A console test consisting of input commands and expected response lines."""

    name: str
    aim: str
    inputs: list[str]
    expected: list[str]
    initial_data: list[str] | None
    expected_data: list[str] | None


def read_fenced_block(body: str, heading: str) -> str:
    """Return the text fenced block beneath a third-level heading."""
    pattern = rf"^### {re.escape(heading)}\s*\n```(?:text)?\s*\n(.*?)^```\s*$"
    match = re.search(pattern, body, re.MULTILINE | re.DOTALL)
    if not match:
        raise ValueError(f"Missing '### {heading}' fenced text block")
    return match.group(1).strip("\n")


def load_test_cases(plan_path: Path) -> list[TestCase]:
    """Parse test cases from the Markdown test plan."""
    content = plan_path.read_text(encoding="utf-8")
    sections = re.finditer(
        r"^## (.+?)\s*$\n(.*?)(?=^## |\Z)",
        content,
        re.MULTILINE | re.DOTALL,
    )
    cases = []
    for section in sections:
        name = section.group(1).strip()
        body = section.group(2)
        aim_match = re.search(r"^\*\*Aim:\*\*\s*(.+)$", body, re.MULTILINE)
        if not aim_match:
            raise ValueError(f"{name}: missing '**Aim:**' field")
        inputs = read_fenced_block(body, "Inputs").splitlines()
        expected = read_fenced_block(body, "Expected output").splitlines()
        try:
            initial_data = read_fenced_block(body, "Initial data file").splitlines()
        except ValueError:
            initial_data = None
        try:
            expected_data = read_fenced_block(body, "Expected data file").splitlines()
        except ValueError:
            expected_data = None
        cases.append(
            TestCase(name, aim_match.group(1).strip(), inputs, expected, initial_data, expected_data)
        )
    if not cases:
        raise ValueError("No test cases found in the test plan")
    return cases


def get_java_major(javac: Path) -> int:
    """Return the major version reported by a javac executable."""
    result = subprocess.run(
        [str(javac), "-version"],
        capture_output=True,
        text=True,
        check=False,
    )
    match = re.search(r"javac\s+(\d+)", result.stdout + result.stderr)
    return int(match.group(1)) if match else 0


def find_java_home(explicit_home: str | None) -> tuple[Path, int]:
    """Find Java 25 when possible, otherwise return the newest local JDK."""
    candidate_homes = []
    for raw_home in (
        explicit_home,
        os.environ.get("TEST_UI_JAVA_HOME"),
        str(Path.home() / ".sdkman/candidates/java/25.0.3.fx-zulu"),
        "/Applications/IntelliJ IDEA.app/Contents/jbr/Contents/Home",
        os.environ.get("JAVA_HOME"),
    ):
        if raw_home:
            candidate_homes.append(Path(raw_home))

    system_javac = shutil.which("javac")
    if system_javac:
        candidate_homes.append(Path(system_javac).resolve().parent.parent)

    available = []
    seen = set()
    for java_home in candidate_homes:
        resolved_home = java_home.expanduser().resolve()
        if resolved_home in seen:
            continue
        seen.add(resolved_home)
        javac = resolved_home / "bin/javac"
        java = resolved_home / "bin/java"
        if javac.is_file() and java.is_file():
            available.append((get_java_major(javac), resolved_home))

    if not available:
        raise RuntimeError("No Java compiler and runtime were found")
    return max(available, key=lambda item: (item[0] == 25, item[0]))[::-1]


def is_separator(line: str) -> bool:
    """Return whether a line contains only the chatbot's underscore separator."""
    stripped = line.strip()
    return bool(stripped) and set(stripped) == {"_"}


def normalize_actual(output: str) -> list[str]:
    """Remove startup and optional console formatting from actual output."""
    normalized = []
    startup_finished = False
    for line in output.splitlines():
        if not startup_finished:
            if is_separator(line):
                startup_finished = True
            continue
        stripped = line.strip()
        if stripped and not is_separator(stripped):
            normalized.append(stripped)
    return normalized


def normalize_expected(lines: list[str]) -> list[str]:
    """Remove optional whitespace and separators from expected output."""
    return [line.strip() for line in lines if line.strip() and not is_separator(line)]


def print_lines(lines: list[str]) -> None:
    """Print lines or a marker when a list is empty."""
    if lines:
        print("\n".join(lines))
    else:
        print("<no output>")


def run_tests(project_root: Path, plan_path: Path, java_home: Path, java_major: int) -> int:
    """Compile the chatbot and run every test case until one fails."""
    cases = load_test_cases(plan_path)
    javac = java_home / "bin/javac"
    java = java_home / "bin/java"
    sources = sorted((project_root / "src/main/java").rglob("*.java"))
    if not sources:
        print("FAIL: no Java source files found", file=sys.stderr)
        return 1

    print(f"Java: {java_home} (major version {java_major})")
    if java_major != 25:
        print("WARNING: Java 25 is unavailable; using the newest compatible local JDK.")

    with tempfile.TemporaryDirectory(prefix="test-ui-") as build_dir:
        compile_result = subprocess.run(
            [str(javac), "-d", build_dir, *map(str, sources)],
            capture_output=True,
            text=True,
            check=False,
        )
        if compile_result.returncode != 0:
            print("FAIL: compilation failed")
            print(compile_result.stdout + compile_result.stderr)
            return 1

        for case_number, case in enumerate(cases, start=1):
            case_directory = Path(build_dir) / f"case-{case_number}"
            case_directory.mkdir()
            if case.initial_data is not None:
                data_path = case_directory / "data/bot.txt"
                data_path.parent.mkdir()
                data_path.write_text("\n".join(case.initial_data) + "\n", encoding="utf-8")
            console_input = "\n".join(case.inputs) + "\n"
            print(f"\n=== {case.name} ===")
            print(f"Aim: {case.aim}")
            print("--- Console input ---")
            print_lines(case.inputs)
            try:
                result = subprocess.run(
                    [str(java), "-cp", build_dir, "bot.Bot"],
                    input=console_input,
                    capture_output=True,
                    text=True,
                    timeout=10,
                    check=False,
                    cwd=case_directory,
                )
            except subprocess.TimeoutExpired:
                print("--- Console output ---")
                print("<process timed out after 10 seconds>")
                print("RESULT: FAIL")
                return 1

            console_output = result.stdout + result.stderr
            print("--- Console output ---")
            print(console_output.rstrip() or "<no output>")
            actual = normalize_actual(console_output)
            expected = normalize_expected(case.expected)
            output_failed = result.returncode != 0 or actual != expected

            data_failed = False
            actual_data = []
            if case.expected_data is not None:
                data_path = case_directory / "data/bot.txt"
                if data_path.is_file():
                    actual_data = data_path.read_text(encoding="utf-8").splitlines()
                print("--- Data file output ---")
                print_lines(actual_data)
                data_failed = actual_data != case.expected_data

            if output_failed or data_failed:
                if output_failed:
                    print("--- Expected normalized output ---")
                    print_lines(expected)
                    print("--- Actual normalized output ---")
                    print_lines(actual)
                if data_failed:
                    print("--- Expected data file ---")
                    print_lines(case.expected_data)
                    print("--- Actual data file ---")
                    print_lines(actual_data)
                print(f"Exit code: {result.returncode}")
                print("RESULT: FAIL")
                return 1
            print("RESULT: PASS")

    print(f"\nAll {len(cases)} UI test case(s) passed.")
    return 0


def main() -> int:
    """Parse arguments and execute the UI test plan."""
    script_path = Path(__file__).resolve()
    project_root = script_path.parents[4]
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument(
        "--plan",
        default=str(project_root / "test/ui-test-plan.md"),
        help="path to the Markdown UI test plan",
    )
    parser.add_argument("--java-home", help="JDK home to use for testing")
    args = parser.parse_args()

    try:
        java_home, java_major = find_java_home(args.java_home)
        return run_tests(project_root, Path(args.plan).resolve(), java_home, java_major)
    except (OSError, RuntimeError, ValueError) as error:
        print(f"FAIL: {error}", file=sys.stderr)
        return 1


if __name__ == "__main__":
    sys.exit(main())
