#!/usr/bin/env python3
"""Run console UI tests recorded in test/ui-test-plan.md."""

from __future__ import annotations

import re
import shutil
import subprocess
import sys
import tempfile
from pathlib import Path


ROOT = Path(__file__).resolve().parents[4]
PLAN_PATH = ROOT / "test" / "ui-test-plan.md"
CASE_PATTERN = re.compile(r"^## Test case: (.+)$", re.MULTILINE)
BLOCK_PATTERN = re.compile(r"^### (Inputs|Expected output)\s*```text\n(.*?)\n```$", re.MULTILINE | re.DOTALL)
JAVA_UTF8_OPTIONS = [
    "-Dfile.encoding=UTF-8",
    "-Dstdin.encoding=UTF-8",
    "-Dstdout.encoding=UTF-8",
    "-Dstderr.encoding=UTF-8",
]


def parse_cases(plan: str) -> list[tuple[str, str, str, str]]:
    """Return each test case's name, aim, input, and expected output."""
    matches = list(CASE_PATTERN.finditer(plan))
    cases = []
    for index, match in enumerate(matches):
        section = plan[match.end():matches[index + 1].start() if index + 1 < len(matches) else None]
        aim = re.search(r"^Aim: (.+)$", section, re.MULTILINE)
        blocks = dict(BLOCK_PATTERN.findall(section))
        if aim is None or set(blocks) != {"Inputs", "Expected output"}:
            raise ValueError(f"Test case '{match.group(1)}' must have an aim, inputs, and expected output.")
        cases.append((match.group(1), aim.group(1), blocks["Inputs"], blocks["Expected output"]))
    if not cases:
        raise ValueError("No test cases found in the UI test plan.")
    return cases


def normalize(text: str) -> str:
    """Make line endings and trailing whitespace irrelevant to comparisons."""
    return "\n".join(line.rstrip() for line in text.replace("\r\n", "\n").splitlines()).strip()


def response_only(output: str) -> str:
    """Remove the greeting and goodbye sections from a complete program session."""
    divider = "    ____________________________________________________________"
    lines = (line.rstrip() for line in output.replace("\r\n", "\n").splitlines())
    sections = "\n".join(lines).split(divider)
    response_sections = (section.strip() for section in sections[3:-2])
    response_lines = "\n".join(section for section in response_sections if section)
    return "\n".join(line.removeprefix("     ") for line in response_lines.splitlines())


def main() -> int:
    """Compile the application and run every documented UI test case."""
    try:
        cases = parse_cases(PLAN_PATH.read_text(encoding="utf-8"))
    except (OSError, ValueError) as error:
        print(f"Test plan error: {error}", file=sys.stderr)
        return 2

    build_dir = Path(tempfile.mkdtemp(prefix="bkxss-ui-tests-"))
    try:
        compile_result = subprocess.run(
            ["javac", "-d", str(build_dir), *map(str, (ROOT / "src/main/java").rglob("*.java"))],
            cwd=ROOT, text=True, encoding="utf-8", capture_output=True, check=False,
        )
        if compile_result.returncode:
            print("Compilation failed:\n" + compile_result.stderr, file=sys.stderr)
            return 2

        for name, aim, inputs, expected in cases:
            session_input = inputs + "\nbye\n"
            session_dir = Path(tempfile.mkdtemp(prefix="bkxss-ui-session-"))
            try:
                result = subprocess.run(
                    ["java", *JAVA_UTF8_OPTIONS, "-cp", str(build_dir), "bkxss.Bkxss"], cwd=session_dir,
                    input=session_input, text=True, encoding="utf-8", capture_output=True, check=False,
                )
            finally:
                shutil.rmtree(session_dir)
            actual = response_only(result.stdout)
            print(f"\nTest case: {name}\nAim: {aim}\nConsole input:\n{inputs}\nConsole output:\n{actual}")
            if result.returncode or normalize(actual) != normalize(expected):
                print("\nFAILED — stopping the test session.")
                print("Expected output:\n" + expected)
                print("Actual output:\n" + actual)
                if result.stderr:
                    print("Program error:\n" + result.stderr)
                return 1
        print(f"\nPassed {len(cases)} UI test case(s).")
        return 0
    finally:
        shutil.rmtree(build_dir)


if __name__ == "__main__":
    raise SystemExit(main())
