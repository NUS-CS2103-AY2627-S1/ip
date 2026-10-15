"""Run the command/response cases defined in test/ui-test-plan.md."""

from __future__ import annotations

import re
import subprocess
import sys
import tempfile
from dataclasses import dataclass
from pathlib import Path

ROOT = Path(__file__).resolve().parents[4]
PLAN = ROOT / "test" / "ui-test-plan.md"
CLASSES = ROOT / "build" / "classes" / "java" / "main"
SEPARATOR = "_" * 60


@dataclass
class TestCase:
    """A UI test case parsed from the Markdown plan."""
    name: str
    aim: str
    commands: list[str]
    expected_outputs: list[str]


def code_blocks(section: str) -> list[str]:
    """Return the contents of fenced `text` blocks."""
    return [block.rstrip("\n") for block in re.findall(r"```text\r?\n(.*?)```", section, re.DOTALL)]


def parse_plan(plan: str) -> list[TestCase]:
    """Parse the small Markdown format documented by this skill."""
    cases = []
    for section in re.split(r"^## Test: ", plan, flags=re.MULTILINE)[1:]:
        name, body = section.split("\n", 1)
        aim = re.search(r"^Aim: (.+)$", body, re.MULTILINE)
        commands = re.search(r"^### Commands\r?\n(.*?)(?=^### Expected outputs\r?$)", body, re.MULTILINE | re.DOTALL)
        expected = re.search(r"^### Expected outputs\r?\n(.*)", body, re.MULTILINE | re.DOTALL)
        if not aim or not commands or not expected:
            raise ValueError(f"Test '{name}' does not use the required plan format.")
        command_blocks = code_blocks(commands.group(1))
        command_lines = command_blocks[0].splitlines() if len(command_blocks) == 1 else []
        expected_blocks = code_blocks(expected.group(1))
        if not command_lines or len(command_lines) != len(expected_blocks):
            raise ValueError(f"Test '{name}' must have one expected block per command.")
        cases.append(TestCase(name, aim.group(1), command_lines, expected_blocks))
    if not cases:
        raise ValueError("The test plan contains no test cases.")
    return cases


def compile_program() -> None:
    """Compile the application with Gradle so third-party dependencies are available."""
    wrapper = ROOT / ("gradlew.bat" if sys.platform == "win32" else "gradlew")
    result = subprocess.run(
        [str(wrapper), "classes", "--no-daemon"],
        cwd=ROOT,
        text=True,
        capture_output=True,
    )
    if result.returncode:
        details = "\n".join(part for part in (result.stdout, result.stderr) if part)
        raise RuntimeError("Gradle compilation failed:\n" + details)


def responses(output: str) -> list[str]:
    """Extract the application response following each submitted command."""
    sections = output.replace("\r\n", "\n").split(SEPARATOR + "\n")
    # Consecutive separators create empty sections. The remaining first and
    # last sections are respectively the greeting and automatic `bye` reply.
    nonempty_sections = [section.rstrip("\n") for section in sections if section.strip()]
    return nonempty_sections[1:-1]


def transcript(commands: list[str], output: str) -> None:
    """Print a test session's input and complete console output."""
    print("Console input:")
    print("\n".join(commands + ["bye"]))
    print("Console output:")
    print(output.rstrip())


def main() -> int:
    """Run all planned cases and stop immediately when one command fails."""
    try:
        cases = parse_plan(PLAN.read_text(encoding="utf-8"))
        compile_program()
    except (OSError, ValueError, RuntimeError) as error:
        print(f"Cannot start UI tests: {error}", file=sys.stderr)
        return 2
    for case in cases:
        with tempfile.TemporaryDirectory(prefix="quackers-ui-test-") as test_directory:
            result = subprocess.run(["java", "-cp", str(CLASSES), "quackers.Quackers"],
                                    input="\n".join(case.commands + ["bye"]) + "\n",
                                    text=True, capture_output=True, cwd=test_directory)
        actual = responses(result.stdout)
        for command, expected, response in zip(case.commands, case.expected_outputs, actual):
            if expected.replace("\r\n", "\n") != response:
                print(f"FAIL: {case.name} - {case.aim}\nCommand: {command}")
                print("Expected output:\n" + expected)
                print("Actual output:\n" + response)
                transcript(case.commands, result.stdout)
                return 1
        if result.returncode or len(actual) != len(case.commands):
            print(f"FAIL: {case.name} - {case.aim}")
            print(result.stderr or "Could not isolate command responses.")
            transcript(case.commands, result.stdout)
            return 1
        print(f"PASS: {case.name} - {case.aim}")
        transcript(case.commands, result.stdout)
    print("All UI tests passed.")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
