#!/usr/bin/env python3
"""
Run CLI-style UI tests defined in `test/ui-test-plan.md`.

Each test case is a fenced code block tagged `ui-test` with keys:
  - id: string
  - aim: string
  - cmd: string
  - stdin: | (optional multi-line)
  - expected: | (multi-line)

The runner executes tests in order, stops on the first failure, and writes:
  - _temp/ui-test-session.txt      transcript of commands + output
  - _temp/ui-test-actual.txt       actual output of failing test (on failure)
  - _temp/ui-test-expected.txt     expected output of failing test (on failure)
"""

from __future__ import annotations

import os
import re
import subprocess
import sys
import argparse
from dataclasses import dataclass
from pathlib import Path


REPO_ROOT = Path(__file__).resolve().parents[1]
PLAN_PATH = REPO_ROOT / "test" / "ui-test-plan.md"
TEMP_DIR = REPO_ROOT / "_temp"
SESSION_PATH = TEMP_DIR / "ui-test-session.txt"
FAIL_ACTUAL_PATH = TEMP_DIR / "ui-test-actual.txt"
FAIL_EXPECTED_PATH = TEMP_DIR / "ui-test-expected.txt"


@dataclass(frozen=True)
class TestCase:
    test_id: str
    aim: str
    cmd: str
    stdin: str
    expected: str


def normalize_output(text: str) -> str:
    text = text.replace("\r\n", "\n").replace("\r", "\n")
    lines = [line.rstrip() for line in text.split("\n")]
    text = "\n".join(lines)
    return text.rstrip("\n")


def _dedent_block(text: str) -> str:
    lines = text.splitlines()
    while lines and lines[0].strip() == "":
        lines.pop(0)
    while lines and lines[-1].strip() == "":
        lines.pop()
    if not lines:
        return ""
    indents = [
        len(re.match(r"[ \t]*", line).group(0))
        for line in lines
        if line.strip() != ""
    ]
    margin = min(indents) if indents else 0
    return "\n".join(line[margin:] for line in lines)


def parse_ui_test_blocks(plan_text: str) -> list[TestCase]:
    blocks = re.findall(r"```ui-test\s*\n(.*?)\n```", plan_text, flags=re.DOTALL)
    cases: list[TestCase] = []
    for block in blocks:
        lines = block.splitlines()
        data: dict[str, str] = {}
        i = 0
        while i < len(lines):
            line = lines[i]
            if not line.strip() or line.lstrip().startswith("#"):
                i += 1
                continue
            m = re.match(r"^([A-Za-z0-9_-]+)\s*:\s*(.*)$", line)
            if not m:
                raise ValueError(f"Invalid line in ui-test block: {line!r}")
            key, rest = m.group(1), m.group(2)
            if rest == "|":
                i += 1
                collected: list[str] = []
                while i < len(lines):
                    next_line = lines[i]
                    if re.match(r"^[A-Za-z0-9_-]+\s*:\s*", next_line):
                        break
                    collected.append(next_line)
                    i += 1
                data[key] = _dedent_block("\n".join(collected))
                continue
            data[key] = rest.strip()
            i += 1

        test_id = data.get("id", "").strip()
        aim = data.get("aim", "").strip()
        cmd = data.get("cmd", "").strip()
        stdin = data.get("stdin", "")
        expected = data.get("expected", "")
        if not test_id or not aim or not cmd:
            raise ValueError("Each ui-test must include non-empty: id, aim, cmd")
        cases.append(TestCase(test_id=test_id, aim=aim, cmd=cmd, stdin=stdin, expected=expected))
    return cases


def run_command(cmd: str, stdin_text: str) -> tuple[int, str]:
    proc = subprocess.run(
        cmd if isinstance(cmd, list) else cmd,
        shell=True,
        cwd=str(REPO_ROOT),
        input=(stdin_text + ("\n" if stdin_text and not stdin_text.endswith("\n") else "")),
        text=True,
        stdout=subprocess.PIPE,
        stderr=subprocess.STDOUT,
        env={**os.environ},
    )
    return proc.returncode, proc.stdout


def append_session(text: str) -> None:
    SESSION_PATH.parent.mkdir(parents=True, exist_ok=True)
    with SESSION_PATH.open("a", encoding="utf-8") as f:
        f.write(text)


def main(argv: list[str]) -> int:
    parser = argparse.ArgumentParser(description="Run UI tests from a markdown plan.")
    parser.add_argument("--plan", default=str(PLAN_PATH), help="Path to ui-test plan markdown file.")
    parser.add_argument("--id", dest="ids", action="append", default=[],
                        help="Run only a specific test id (repeatable).")
    args = parser.parse_args(argv)

    plan_path = Path(args.plan)
    if not plan_path.exists():
        print(f"ERROR: missing test plan at {plan_path}", file=sys.stderr)
        return 2

    plan_text = plan_path.read_text(encoding="utf-8")
    cases = parse_ui_test_blocks(plan_text)
    if not cases:
        print(f"ERROR: no ```ui-test``` blocks found in {plan_path}", file=sys.stderr)
        return 2

    if args.ids:
        wanted = set(args.ids)
        cases = [c for c in cases if c.test_id in wanted]
        if not cases:
            print(f"ERROR: no matching test ids found: {sorted(wanted)}", file=sys.stderr)
            return 2

    TEMP_DIR.mkdir(parents=True, exist_ok=True)
    SESSION_PATH.write_text("", encoding="utf-8")

    for index, case in enumerate(cases, start=1):
        header = (
            f"=== {case.test_id} ({index}/{len(cases)}) ===\n"
            f"AIM: {case.aim}\n"
            f"$ {case.cmd}\n"
        )
        append_session(header)

        if case.stdin.strip():
            append_session("STDIN:\n" + case.stdin + "\n")

        returncode, output = run_command(case.cmd, case.stdin)
        append_session("OUTPUT:\n" + output + ("\n" if not output.endswith("\n") else ""))
        append_session(f"EXIT CODE: {returncode}\n\n")

        actual_norm = normalize_output(output)
        expected_norm = normalize_output(case.expected)

        if actual_norm != expected_norm:
            FAIL_ACTUAL_PATH.write_text(actual_norm + "\n", encoding="utf-8")
            FAIL_EXPECTED_PATH.write_text(expected_norm + "\n", encoding="utf-8")
            print(f"FAIL: {case.test_id} - output mismatch")
            print(f"Session transcript: {SESSION_PATH}")
            print(f"Actual output:      {FAIL_ACTUAL_PATH}")
            print(f"Expected output:    {FAIL_EXPECTED_PATH}")
            return 1

    print(f"PASS: {len(cases)} test case(s)")
    print(f"Session transcript: {SESSION_PATH}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main(sys.argv[1:]))
