#!/usr/bin/env python3
"""Compile Harold and run the transcript cases in test/ui-test-plan.md."""

from __future__ import annotations

import re
import subprocess
import sys
import tempfile
from dataclasses import dataclass
from pathlib import Path


SEPARATOR = "____________________________________________________________"
PROJECT_ROOT = Path(__file__).resolve().parents[4]
DEFAULT_PLAN = PROJECT_ROOT / "test" / "ui-test-plan.md"


@dataclass
class TestCase:
    """A sequence of commands and their expected responses."""

    name: str
    aim: str
    commands: list[str]
    expected_outputs: list[str]


def normalize(output: str) -> str:
    """Normalize platform line endings and insignificant outer blank lines."""
    return output.replace("\r\n", "\n").replace("\r", "\n").strip("\n")


def parse_plan(plan_path: Path) -> list[TestCase]:
    """Parse test cases from the documented Markdown test-plan format."""
    text = plan_path.read_text(encoding="utf-8")
    matches = list(re.finditer(r"^## (TC\d+:[^\n]+)$", text, re.MULTILINE))
    if not matches:
        raise ValueError("No test cases matching '## TC<number>: <name>' were found.")

    cases = []
    for position, match in enumerate(matches):
        end = matches[position + 1].start() if position + 1 < len(matches) else len(text)
        section = text[match.end():end]

        aim_match = re.search(r"\*\*Aim:\*\*\s*(.+)", section)
        inputs_match = re.search(
            r"### Inputs\s*```text\s*\n(.*?)```", section, re.DOTALL
        )
        outputs_heading = section.find("### Expected outputs")
        if not aim_match or not inputs_match or outputs_heading < 0:
            raise ValueError(f"{match.group(1)} is missing its aim, inputs, or outputs.")

        commands = [line for line in normalize(inputs_match.group(1)).split("\n") if line]
        outputs_section = section[outputs_heading:]
        expected_outputs = [
            normalize(output)
            for output in re.findall(
                r"#### Output \d+\s*```text\s*\n(.*?)```",
                outputs_section,
                re.DOTALL,
            )
        ]
        if len(commands) != len(expected_outputs):
            raise ValueError(
                f"{match.group(1)} has {len(commands)} inputs but "
                f"{len(expected_outputs)} expected outputs."
            )
        cases.append(
            TestCase(match.group(1), aim_match.group(1).strip(), commands, expected_outputs)
        )
    return cases


def extract_responses(stdout: str) -> list[str]:
    """Remove separators and the startup banner, returning command responses."""
    sections = [normalize(section) for section in stdout.split(SEPARATOR)]
    nonempty_sections = [section for section in sections if section]
    return nonempty_sections[1:]


def print_transcript(case: TestCase, commands_run: int, responses: list[str]) -> None:
    """Print the visible command/response record for a test session."""
    print(f"\n=== {case.name} ===")
    print(f"Aim: {case.aim}")
    for index in range(commands_run):
        print(f"\n> {case.commands[index]}")
        if index < len(responses):
            print(responses[index])


def main() -> int:
    """Compile the program and stop at the first mismatched command response."""
    plan_path = Path(sys.argv[1]).resolve() if len(sys.argv) > 1 else DEFAULT_PLAN
    try:
        cases = parse_plan(plan_path)
    except (OSError, ValueError) as error:
        print(f"TEST PLAN ERROR: {error}", file=sys.stderr)
        return 2

    source_root = PROJECT_ROOT / "src" / "main" / "java"
    sources = sorted(
        source
        for source in source_root.rglob("*.java")
        if "gui" not in source.relative_to(source_root).parts
    )
    if not sources:
        print("COMPILE ERROR: No Java source files found.", file=sys.stderr)
        return 2

    with tempfile.TemporaryDirectory(prefix="harold-ui-test-") as build_directory:
        compile_result = subprocess.run(
            ["javac", "-d", build_directory, *map(str, sources)],
            cwd=PROJECT_ROOT,
            capture_output=True,
            text=True,
            check=False,
        )
        if compile_result.returncode != 0:
            print("COMPILE FAILED", file=sys.stderr)
            print(compile_result.stdout, end="", file=sys.stderr)
            print(compile_result.stderr, end="", file=sys.stderr)
            return 2

        for case_number, case in enumerate(cases, start=1):
            case_directory = Path(build_directory) / f"case-{case_number}"
            case_directory.mkdir()
            try:
                result = subprocess.run(
                    ["java", "-cp", build_directory, "harold.Harold"],
                    cwd=case_directory,
                    input="\n".join(case.commands) + "\n",
                    capture_output=True,
                    text=True,
                    timeout=10,
                    check=False,
                )
            except subprocess.TimeoutExpired:
                print_transcript(case, 0, [])
                print("\nFAILED: Program did not terminate within 10 seconds.")
                return 1

            responses = extract_responses(result.stdout)
            for index, expected in enumerate(case.expected_outputs):
                actual = responses[index] if index < len(responses) else "<no output>"
                if normalize(actual) != normalize(expected):
                    print_transcript(case, index + 1, responses)
                    print(f"\nFAILED at command {index + 1}: {case.commands[index]}")
                    print("\n--- Expected output ---")
                    print(expected)
                    print("\n--- Actual output ---")
                    print(actual)
                    return 1

            if result.returncode != 0 or len(responses) != len(case.commands):
                print_transcript(case, len(case.commands), responses)
                print(f"\nFAILED: Program exited with status {result.returncode}.")
                if result.stderr:
                    print(result.stderr, end="")
                return 1

            print_transcript(case, len(case.commands), responses)
            print("\nPASSED")

    print(f"\nAll {len(cases)} test cases passed.")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
