#!/usr/bin/env python3
"""Run console UI test cases defined in a Markdown test plan."""

from __future__ import annotations

import re
import subprocess
import sys
import tempfile
from pathlib import Path


TEST_CASE_PATTERN = re.compile(
    r"^## Test Case \d+: (?P<title>.+?)\r?\n(?P<preamble>.*?)^### Inputs\r?\n\s*```(?:text)?\r?\n"
    r"(?P<inputs>.*?)\r?\n```\r?\n.*?^### Expected Output\r?\n\s*```(?:text)?\r?\n"
    r"(?P<expected>.*?)\r?\n```",
    re.MULTILINE | re.DOTALL,
)


def normalize(output: str) -> str:
    """Normalize line endings and insignificant trailing whitespace for comparison."""
    return "\n".join(line.rstrip() for line in output.replace("\r\n", "\n").split("\n")).strip()


def load_test_cases(plan_path: Path) -> list[tuple[str, str, str, str]]:
    """Read test-case titles, initial data, inputs, and expected outputs from the Markdown plan."""
    matches = TEST_CASE_PATTERN.finditer(plan_path.read_text(encoding="utf-8"))
    cases = []
    for match in matches:
        initial_data_match = re.search(
            r"^### Initial Data\r?\n\s*```(?:text)?\r?\n(?P<data>.*?)\r?\n```",
            match["preamble"],
            re.MULTILINE | re.DOTALL,
        )
        initial_data = initial_data_match["data"] if initial_data_match else ""
        cases.append((match["title"], initial_data, match["inputs"], match["expected"]))
    if not cases:
        raise ValueError("No test cases found. Follow the test plan's Test Case template.")
    return cases


def main() -> int:
    """Compile the program, run every planned UI session, and stop at the first failure."""
    plan_path = Path(sys.argv[1]) if len(sys.argv) > 1 else Path("test/ui-test-plan.md")
    source_files = sorted(Path("src/main/java").rglob("*.java"))
    if not source_files:
        print("No Java source files found in src/main/java.")
        return 1

    try:
        cases = load_test_cases(plan_path)
    except (OSError, ValueError) as error:
        print(f"Could not load UI test plan: {error}")
        return 1

    with tempfile.TemporaryDirectory(prefix="duke-ui-tests-") as output_directory:
        compilation = subprocess.run(
            ["javac", "-d", output_directory, *map(str, source_files)],
            text=True,
            capture_output=True,
        )
        if compilation.returncode != 0:
            print("Compilation failed:\n" + compilation.stderr)
            return 1

        for number, (title, initial_data, inputs, expected) in enumerate(cases, start=1):
            session_directory = Path(output_directory) / f"session-{number}"
            session_directory.mkdir()
            if initial_data:
                data_file = session_directory / "data" / "duke.txt"
                data_file.parent.mkdir()
                data_file.write_text(initial_data + "\n", encoding="utf-8")
            session = subprocess.run(
                ["java", "-cp", str(Path(output_directory).resolve()), "bloop.Duke"],
                input=inputs + "\n",
                text=True,
                capture_output=True,
                cwd=session_directory,
            )
            actual = session.stdout
            print(f"\n=== Test Case {number}: {title} ===")
            print("Console input:")
            print(inputs)
            print("Console output:")
            print(actual, end="" if actual.endswith("\n") else "\n")

            if session.returncode != 0 or normalize(actual) != normalize(expected):
                print("FAILED: expected and actual outputs differ.")
                print("Expected output:\n" + expected)
                print("Actual output:\n" + actual)
                return 1

    print("\nAll UI test cases passed.")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
