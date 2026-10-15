#!/usr/bin/env python3
"""Run Markdown-defined command-line UI tests and stop at the first failure."""

from __future__ import annotations

import argparse
import difflib
import re
import subprocess
import sys
from pathlib import Path
import shutil


CASE_PATTERN = re.compile(r"^##\s+Test case\b.*?(?=^##\s+Test case\b|\Z)",
                          re.MULTILINE | re.DOTALL)
BLOCK_PATTERN = re.compile(
    r"\*\*(Inputs|Expected output):\*\*\s*\n\x60{3}[^\n]*\n(.*?)\x60{3}",
    re.DOTALL,
)


def parse_cases(plan_path: Path) -> list[tuple[str, str, str]]:
    """Parse (name, inputs, expected output) records from a Markdown plan."""
    plan = plan_path.read_text(encoding="utf-8")
    cases = []
    for section in CASE_PATTERN.findall(plan):
        heading = section.splitlines()[0].removeprefix("## ").strip()
        blocks = {kind: content for kind, content in BLOCK_PATTERN.findall(section)}
        if "Inputs" not in blocks or "Expected output" not in blocks:
            raise ValueError(
                f"{heading} must contain fenced Inputs and Expected output blocks"
            )
        cases.append((heading, blocks["Inputs"], blocks["Expected output"]))
    if not cases:
        raise ValueError(f"No test cases found in {plan_path}")
    return cases


def comparable(text: str) -> str:
    """Normalize line endings and the final Markdown newline for comparison."""
    return text.replace("\r\n", "\n").rstrip("\n")


def format_diff(expected: str, actual: str) -> str:
    """Return a readable expected-versus-actual unified diff."""
    return "".join(
        difflib.unified_diff(
            expected.splitlines(keepends=True),
            actual.splitlines(keepends=True),
            fromfile="expected",
            tofile="actual",
        )
    )


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("plan", type=Path)
    parser.add_argument(
        "--program",
        nargs="+",
        default=["java", "src/main/java/Amy.java"],
        help="program command and arguments (default: java src/main/java/Amy.java)",
    )
    parser.add_argument(
        "--session-log",
        type=Path,
        default=Path("test/ui-test-session.log"),
    )
    args = parser.parse_args()

    try:
        cases = parse_cases(args.plan)
    except (OSError, ValueError) as error:
        print(f"ERROR: {error}", file=sys.stderr)
        return 2

    args.session_log.parent.mkdir(parents=True, exist_ok=True)
    with args.session_log.open("w", encoding="utf-8") as log:
        for number, (name, inputs, expected) in enumerate(cases, start=1):
            shutil.rmtree("data", ignore_errors=True)  # reset state before each test case

            result = subprocess.run(
                args.program,
                input=inputs,
                text=True,
                capture_output=True,
            )
            actual = result.stdout
            if result.stderr:
                actual += f"\n[stderr]\n{result.stderr}"

            log.write(f"=== {name} ===\n")
            log.write("--- console input ---\n")
            log.write(inputs)
            if not inputs.endswith("\n"):
                log.write("\n")
            log.write("--- console output ---\n")
            log.write(actual)
            if not actual.endswith("\n"):
                log.write("\n")
            log.write("\n")
            log.flush()

            if result.returncode != 0 or comparable(actual) != comparable(expected):
                print(f"FAILED test case {number}: {name}")
                print("\nExpected output:\n" + expected.rstrip("\n"))
                print("\nActual output:\n" + actual.rstrip("\n"))
                diff = format_diff(comparable(expected), comparable(actual))
                if diff:
                    print("\nDiff:\n" + diff)
                print(f"\nSession record: {args.session_log.resolve()}")
                return 1

            print(f"PASSED test case {number}: {name}")

    print(f"All {len(cases)} test case(s) passed.")
    print(f"Session record: {args.session_log.resolve()}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
