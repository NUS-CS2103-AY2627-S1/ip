"""Run the console UI test cases recorded in test/ui-test-plan.md."""

from __future__ import annotations

import re
import subprocess
import sys
import tempfile
from dataclasses import dataclass
from pathlib import Path


@dataclass
class TestCase:
    """One UI interaction and the output it should produce."""

    name: str
    aim: str
    input_text: str
    expected_output: str


def normalise(text: str) -> str:
    """Make line-ending differences irrelevant while retaining all displayed text."""
    return text.replace("\r\n", "\n").rstrip("\n")


def read_test_cases(plan_path: Path) -> list[TestCase]:
    """Read test cases from the documented Markdown test-plan format."""
    plan = plan_path.read_text(encoding="utf-8")
    pattern = re.compile(
        r"^## Test case: (?P<name>.+?)\r?\n\r?\n"
        r"\*\*Aim:\*\* (?P<aim>.+?)\r?\n\r?\n"
        r"### Input\r?\n```text\r?\n(?P<input>.*?)\r?\n```\r?\n\r?\n"
        r"### Expected output\r?\n```text\r?\n(?P<expected>.*?)\r?\n```",
        re.MULTILINE | re.DOTALL,
    )
    cases = [
        TestCase(
            match.group("name"),
            match.group("aim"),
            normalise(match.group("input")),
            normalise(match.group("expected")),
        )
        for match in pattern.finditer(plan)
    ]
    if not cases:
        raise ValueError("No valid test cases found in test/ui-test-plan.md.")
    return cases


def compile_program(repo: Path, output_dir: Path) -> None:
    """Compile all Java source files with Java 25 before testing the UI."""
    sources = sorted((repo / "src" / "main" / "java").rglob("*.java"))
    result = subprocess.run(
        ["javac", "--release", "25", "-d", str(output_dir), *(str(source) for source in sources)],
        cwd=repo,
        text=True,
        capture_output=True,
    )
    if result.returncode != 0:
        print("Compilation failed:")
        print(result.stderr or result.stdout)
        raise SystemExit(1)


def print_session(case: TestCase, actual_output: str) -> None:
    """Display the recorded input and program output for one test session."""
    print(f"\n=== {case.name} ===")
    print(f"Aim: {case.aim}")
    print("Console input:")
    print(case.input_text)
    print("Console output:")
    print(actual_output)


def main() -> None:
    """Compile Moon and run planned UI tests until the first failure."""
    repo = Path(__file__).resolve().parents[4]
    plan_path = repo / "test" / "ui-test-plan.md"
    output_dir = repo / "out" / "ui-tests"
    cases = read_test_cases(plan_path)
    output_dir.mkdir(parents=True, exist_ok=True)
    compile_program(repo, output_dir)

    for case in cases:
        with tempfile.TemporaryDirectory() as session_dir:
            result = subprocess.run(
                ["java", "-cp", str(output_dir), "moon.Moon"],
                cwd=session_dir,
                input=f"{case.input_text}\n",
                text=True,
                capture_output=True,
            )
        actual_output = normalise(result.stdout)
        print_session(case, actual_output)

        if result.returncode != 0 or actual_output != case.expected_output:
            print("\nRESULT: FAILED -- testing stopped at the first failing case.")
            print("Expected output:")
            print(case.expected_output)
            print("Actual output:")
            print(actual_output)
            raise SystemExit(1)

        print("RESULT: PASSED")

    print("\nAll UI test cases passed.")


if __name__ == "__main__":
    main()
