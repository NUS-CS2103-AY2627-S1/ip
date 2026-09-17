#!/usr/bin/env python3
"""Run Mouse UI tests from test/ui-test-plan.md. Fail fast on the first mismatch."""

from __future__ import annotations

import os
import re
import subprocess
import sys
from difflib import unified_diff
from pathlib import Path

def find_repo() -> Path:
    here = Path(__file__).resolve()
    for path in [here, *here.parents]:
        if (path / "test" / "ui-test-plan.md").exists() and (path / "src" / "main" / "java").exists():
            return path
    raise SystemExit("error: could not find the ip repository root")


REPO = find_repo()
PLAN = REPO / "test" / "ui-test-plan.md"
SRC = REPO / "src" / "main" / "java"
OUT = REPO / "bin"
MAIN_CLASS = "mouse.Mouse"
JDK_HINT = Path.home() / ".sdkman" / "candidates" / "java" / "25.0.3.fx-zulu"


def java_home() -> Path | None:
    env = os.environ.get("JAVA_HOME")
    if env:
        return Path(env)
    if JDK_HINT.exists():
        return JDK_HINT
    return None


def java_bin(name: str) -> str:
    home = java_home()
    if home:
        return str(home / "bin" / name)
    return name


def compile_sources() -> None:
    sources = sorted(str(path) for path in SRC.rglob("*.java"))
    if not sources:
        raise SystemExit(f"error: no Java files in {SRC}")
    OUT.mkdir(parents=True, exist_ok=True)
    result = subprocess.run(
        [java_bin("javac"), "-d", str(OUT), *sources],
        capture_output=True,
        text=True,
    )
    if result.returncode != 0:
        sys.stderr.write(result.stderr)
        raise SystemExit("error: compilation failed")


def parse_plan(text: str) -> list[dict[str, str]]:
    cases: list[dict[str, str]] = []
    parts = re.split(r"^## Test case:\s*", text, flags=re.MULTILINE)
    for part in parts[1:]:
        name, _, body = part.partition("\n")
        aim_match = re.search(r"\*\*Aim:\*\*\s*(.+)", body)
        inputs_match = re.search(r"\*\*Inputs:\*\*\s*```(?:text)?\n(.*?)```", body, re.DOTALL)
        expected_match = re.search(
            r"\*\*Expected output:\*\*\s*```(?:text)?\n(.*?)```", body, re.DOTALL
        )
        if not aim_match or not inputs_match or not expected_match:
            raise SystemExit(f"error: test case '{name.strip()}' is missing aim, inputs, or expected output")
        cases.append(
            {
                "name": name.strip(),
                "aim": aim_match.group(1).strip(),
                "inputs": inputs_match.group(1),
                "expected": expected_match.group(1),
            }
        )
    if not cases:
        raise SystemExit(f"error: no test cases found in {PLAN}")
    return cases


def normalize(text: str) -> str:
    return text.replace("\r\n", "\n").replace("\r", "\n")


def command_lines(inputs: str) -> list[str]:
    lines = normalize(inputs).split("\n")
    if lines and lines[-1] == "":
        lines = lines[:-1]
    return lines


def run_case(inputs: str) -> tuple[str, str, int]:
    stdin = normalize(inputs)
    if not stdin.endswith("\n"):
        stdin += "\n"
    result = subprocess.run(
        [java_bin("java"), "-cp", str(OUT), MAIN_CLASS],
        input=stdin,
        capture_output=True,
        text=True,
        cwd=REPO,
    )
    return normalize(result.stdout), normalize(result.stderr), result.returncode


def session_record(commands: list[str], output: str) -> str:
    typed = "\n".join(f"> {command}" for command in commands)
    return f"Console input:\n{typed}\n\nConsole output:\n{output}"


def report_failure(case: dict[str, str], actual: str, expected: str, stderr: str = "") -> None:
    diff = "".join(
        unified_diff(
            expected.splitlines(keepends=True),
            actual.splitlines(keepends=True),
            fromfile="expected",
            tofile="actual",
        )
    )
    print("TEST CASE FAILED")
    print(f"Name: {case['name']}")
    print(f"Aim: {case['aim']}")
    print()
    print("Expected output:")
    print(expected, end="" if expected.endswith("\n") else "\n")
    print()
    print("Actual output:")
    print(actual, end="" if actual.endswith("\n") else "\n")
    if stderr.strip():
        print()
        print("Stderr:")
        print(stderr, end="" if stderr.endswith("\n") else "\n")
    if diff:
        print()
        print("Diff:")
        print(diff, end="" if diff.endswith("\n") else "\n")
    print()
    print("Session terminated.")


def main() -> int:
    if not PLAN.exists():
        print(f"error: missing {PLAN}", file=sys.stderr)
        return 1

    print("======== UI test session ========")
    home = java_home()
    print(f"Repo: {REPO}")
    print(f"Plan: {PLAN}")
    print(f"Java: {java_bin('java')}" + (f" ({home})" if home else ""))
    compile_sources()
    print("Compile: ok")
    print()

    cases = parse_plan(PLAN.read_text(encoding="utf-8"))
    for index, case in enumerate(cases, start=1):
        commands = command_lines(case["inputs"])
        print(f"[{index}/{len(cases)}] {case['name']}")
        print(f"Aim: {case['aim']}")
        actual, stderr, code = run_case(case["inputs"])
        expected = normalize(case["expected"])
        print()
        print(session_record(commands, actual))
        print()
        if code != 0:
            print(f"Program exited with status {code}")
            report_failure(case, actual, expected, stderr)
            return 1
        if actual != expected:
            report_failure(case, actual, expected, stderr)
            return 1
        print("PASS")
        print()

    print(f"All {len(cases)} test case(s) passed.")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
