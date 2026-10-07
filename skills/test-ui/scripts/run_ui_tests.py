#!/usr/bin/env python3
"""Run the Markdown-defined console UI test plan and record its session."""

from __future__ import annotations

import re
import subprocess
from pathlib import Path


ROOT = Path(__file__).resolve().parents[3]
PLAN = ROOT / "test" / "ui-test-plan.md"
SESSION = ROOT / "test" / "ui-test-session.md"


def read_cases() -> list[dict[str, str]]:
    text = PLAN.read_text(encoding="utf-8")
    pattern = re.compile(
        r"### Test case \d+: (?P<name>.+?)\n"
        r"\n\*Aim\*\n(?P<aim>.+?)\n"
        r"\n\*Command\*\n```(?:bash|sh)?\n(?P<command>.*?)\n```\n"
        r"\n\*Input\*\n```(?:text|console)?\n(?P<input>.*?)\n```\n"
        r"\n\*Expected output\*\n```(?:text|console)?\n(?P<expected>.*?)\n```",
        re.DOTALL,
    )
    cases = [m.groupdict() for m in pattern.finditer(text)]
    if not cases:
        raise SystemExit(f"No test cases found in {PLAN}")
    return cases


def normalise(value: str) -> str:
    return value.replace("\r\n", "\n")


def main() -> int:
    cases = read_cases()
    records: list[str] = ["# UI test session", ""]
    for number, case in enumerate(cases, 1):
        command = case["command"].strip()
        stdin = case["input"]
        expected = normalise(case["expected"])
        if expected and not expected.endswith("\n"):
            expected += "\n"
        result = subprocess.run(
            ["bash", "-c", command],
            input=stdin,
            text=True,
            capture_output=True,
            cwd=ROOT,
        )
        actual = normalise(result.stdout + result.stderr)
        passed = result.returncode == 0 and actual == expected
        records.extend(
            [
                f"## Test case {number}: {case['name'].strip()}",
                "",
                f"**Aim:** {case['aim'].strip()}",
                "",
                "**Command**",
                "```bash",
                command,
                "```",
                "",
                "**Console input**",
                "```text",
                stdin,
                "```",
                "",
                "**Console output**",
                "```text",
                actual,
                "```",
                "",
                f"**Result:** {'PASS' if passed else 'FAIL'} (exit status {result.returncode})",
                "",
            ]
        )
        if not passed:
            records.extend(["**Expected output**", "```text", expected, "```", ""])
            SESSION.parent.mkdir(parents=True, exist_ok=True)
            SESSION.write_text("\n".join(records), encoding="utf-8")
            print("\n".join(records))
            return 1

    SESSION.parent.mkdir(parents=True, exist_ok=True)
    SESSION.write_text("\n".join(records), encoding="utf-8")
    print("\n".join(records))
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
