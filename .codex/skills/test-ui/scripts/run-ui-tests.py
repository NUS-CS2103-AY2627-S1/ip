#!/usr/bin/env python3
"""Run command-line UI tests described in a small Markdown test plan."""
from __future__ import annotations

import argparse
import re
import shlex
import subprocess
import sys
from dataclasses import dataclass
from pathlib import Path


@dataclass
class TestCommand:
    name: str
    aim: str
    command: str
    expected: str


CASE_RE = re.compile(
    r"^## Test case:\s*(?P<name>.+?)\s*\n(?P<body>.*?)(?=^## Test case:|\Z)",
    re.MULTILINE | re.DOTALL,
)
PAIR_RE = re.compile(
    r"### Command\s*\n```(?:text)?\n(?P<command>.*?)\n```\s*\n"
    r"### Expected output\s*\n```(?:text)?\n(?P<expected>.*?)\n```",
    re.MULTILINE | re.DOTALL,
)


def parse_plan(path: Path) -> list[TestCommand]:
    text = path.read_text(encoding="utf-8")
    cases = list(CASE_RE.finditer(text))
    if not cases:
        raise ValueError("no '## Test case:' sections found")

    tests: list[TestCommand] = []
    for case in cases:
        body = case.group("body")
        aim_match = re.search(r"^Aim:\s*(.+)$", body, re.MULTILINE)
        if not aim_match:
            raise ValueError(f"test case {case.group('name')!r} is missing an Aim line")
        pairs = list(PAIR_RE.finditer(body))
        if not pairs:
            raise ValueError(f"test case {case.group('name')!r} has no command/output pair")
        for pair in pairs:
            command = pair.group("command").strip("\n")
            if not command:
                raise ValueError(f"test case {case.group('name')!r} has an empty command")
            expected = pair.group("expected")
            # The newline before a Markdown closing fence belongs to the code block.
            tests.append(TestCommand(case.group("name"), aim_match.group(1).strip(), command,
                                     expected + "\n"))
    return tests


def visible(value: str) -> str:
    return value.replace("\\", "\\\\").replace("\n", "\\n\n")


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--command", required=True, help="program command, as a shell-like string")
    parser.add_argument("--plan", default="test/ui-test-plan.md")
    parser.add_argument("--timeout", type=float, default=10.0)
    args = parser.parse_args()

    try:
        tests = parse_plan(Path(args.plan))
        program = shlex.split(args.command)
    except (OSError, ValueError, ValueError) as exc:
        print(f"ERROR: {exc}", file=sys.stderr)
        return 2

    print(f"UI test session: {len(tests)} command(s)")
    for number, test in enumerate(tests, 1):
        input_text = test.command + "\n"
        print(f"\n--- Test {number}: {test.name} ---")
        print(f"Aim: {test.aim}")
        print(f"Console input:\n{test.command}")
        try:
            result = subprocess.run(program, input=input_text, text=True,
                                    capture_output=True, timeout=args.timeout)
        except subprocess.TimeoutExpired as exc:
            actual = (exc.stdout or "")
            print(f"Console output:\n{actual}")
            print(f"FAIL: timed out after {args.timeout:g}s", file=sys.stderr)
            return 1
        actual = result.stdout
        print(f"Console output:\n{actual}", end="" if actual.endswith("\n") else "\n")
        if result.stderr:
            print(f"Console stderr:\n{result.stderr}", end="" if result.stderr.endswith("\n") else "\n")
        if result.returncode != 0:
            print(f"FAIL: program exited with status {result.returncode}", file=sys.stderr)
            print(f"Expected output:\n{test.expected}", file=sys.stderr)
            return 1
        if actual != test.expected:
            print("FAIL: output mismatch", file=sys.stderr)
            print(f"Expected output:\n{test.expected}", file=sys.stderr)
            print(f"Actual output:\n{actual}", file=sys.stderr)
            return 1
        print("PASS")
    print("\nAll UI tests passed.")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
