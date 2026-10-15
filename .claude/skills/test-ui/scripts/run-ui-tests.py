#!/usr/bin/env python
"""
Run the UI test cases recorded in test/ui-test-plan.md against the chatbot.

    python .claude/skills/test-ui/scripts/run-ui-tests.py [--plan PATH] [--case NAME]

For each test case the runner starts the program, feeds it the case's input
commands on standard input, and compares the console output against the
expected output recorded in the plan. It prints a transcript of every case it
runs, and stops at the first failure, reporting expected against actual.

Exit code is 0 when every case passes and 1 as soon as one fails.

Only Python's standard library is used.

The plan is read as Markdown with this structure:

    ```build
    gradlew shadowJar
    ```

    ```run
    java -ea -cp build/libs/elsa.jar elsa.Elsa
    ```

    ### TC-1 - Greet and exit

    **Aim:** why this case exists

    ```input
    bye
    ```

    ```expected
    ...the exact console output...
    ```

The optional ```build``` block is run once, before the first case, so that no
case can be run against a stale build. If it fails, its output is shown and no
case is run at all.

The chatbot saves its tasks to a data file and reads them back at startup, so
the runner controls that file before each case: a case with a ```data``` block
starts with those lines in the file, and a case without one starts with no file
at all. Cases are therefore independent of each other and of any earlier run.

Comparison ignores trailing whitespace on each line and any blank lines at the
very end, so an editor that trims trailing spaces cannot cause a false failure.
Everything else, including leading indentation, must match exactly.
"""
from __future__ import annotations

import argparse
import difflib
import re
import shlex
import subprocess
import sys
from pathlib import Path

HEADING_RE = re.compile(r"^###\s+(.+?)\s*$")
AIM_RE = re.compile(r"^\*\*Aim:\*\*\s*(.+?)\s*$")
FENCE_RE = re.compile(r"^```(\w*)\s*$")

DEFAULT_PLAN = Path("test/ui-test-plan.md")
# Where the chatbot keeps its saved tasks, relative to the repository root.
DATA_FILE = Path("data") / "elsa.txt"
DEFAULT_COMMAND = "java -ea -cp build/libs/elsa.jar elsa.Elsa"
RULE = "-" * 70


def resolve_wrapper(command: list[str], repo: Path) -> list[str]:
    """Names the Gradle wrapper script the way the current platform needs it.

    The test plan writes the build command as plain `gradlew ...` so that it reads
    the same everywhere. The wrapper is really two files: `gradlew.bat` on Windows
    and the extensionless shell script `gradlew` elsewhere. Neither is found by
    name alone, because the repository root is not on PATH, so the full path is
    used rather than relying on the working directory.
    """
    if not command or command[0] != "gradlew":
        return command
    script = "gradlew.bat" if sys.platform == "win32" else "gradlew"
    return [str(repo / script)] + command[1:]


def run_build(build: str, repo: Path) -> bool:
    """Runs the plan's build command once. Returns True when the build succeeded."""
    command = resolve_wrapper(shlex.split(build), repo)
    print(f"Build:   {' '.join(command)}")
    result = subprocess.run(command, cwd=str(repo), capture_output=True, text=True, timeout=600)
    if result.returncode == 0:
        print("Build:   OK")
        return True
    print("Build:   FAILED")
    print(RULE)
    for stream in (result.stdout, result.stderr):
        for line in stream.rstrip().splitlines():
            print(f"  ! {line}")
    print(RULE)
    print("No test case was run, because they would have tested a stale build.")
    return False


class PlanError(Exception):
    """Raised when the test plan cannot be understood."""


def parse_plan(text: str) -> tuple[str, str, list[dict]]:
    """Splits the plan into the build command, the run command, and the test cases."""
    command = ""
    build = ""
    cases: list[dict] = []
    current: dict | None = None
    fence: str | None = None
    buffer: list[str] = []

    for number, line in enumerate(text.splitlines(), start=1):
        fence_match = FENCE_RE.match(line)

        if fence is not None:
            # Inside a fenced block: a closing fence ends it, anything else is content.
            if fence_match and not fence_match.group(1):
                block = "\n".join(buffer)
                if fence == "run":
                    command = block.strip()
                elif fence == "build":
                    build = block.strip()
                elif fence in ("input", "expected", "data"):
                    if current is None:
                        raise PlanError(f"line {number}: '{fence}' block before any '### ' heading")
                    current[fence] = block
                fence = None
                buffer = []
            else:
                buffer.append(line)
            continue

        if fence_match:
            fence = fence_match.group(1) or "plain"
            buffer = []
            continue

        heading = HEADING_RE.match(line)
        if heading:
            current = {"name": heading.group(1), "aim": "", "input": None,
                       "expected": None, "data": None}
            cases.append(current)
            continue

        aim = AIM_RE.match(line)
        if aim and current is not None:
            current["aim"] = aim.group(1)

    for case in cases:
        if case["input"] is None or case["expected"] is None:
            raise PlanError(f"test case '{case['name']}' is missing an input or expected block")

    return command or DEFAULT_COMMAND, build, cases


def normalise(text: str) -> list[str]:
    """Reduces output to the form used for comparison."""
    lines = [line.rstrip() for line in text.replace("\r\n", "\n").split("\n")]
    while lines and not lines[-1]:
        lines.pop()
    return lines


def prepare_data_file(repo: Path, case: dict) -> None:
    """Puts the chatbot's data file into the state this case expects.

    A case with a ```data``` block starts with exactly those lines saved; a case
    without one starts with no data file, as on a first ever run. Either way the
    case cannot be affected by what an earlier case or an earlier run left behind.
    """
    path = repo / DATA_FILE
    if case["data"] is None:
        path.unlink(missing_ok=True)
        return
    path.parent.mkdir(parents=True, exist_ok=True)
    text = case["data"]
    if text and not text.endswith("\n"):
        text += "\n"
    path.write_text(text, encoding="utf-8")


def run_case(command: str, repo: Path, case: dict) -> tuple[list[str], str, int]:
    """Runs one case and returns its normalised output, raw stderr, and exit code."""
    stdin_text = case["input"]
    if stdin_text and not stdin_text.endswith("\n"):
        stdin_text += "\n"
    result = subprocess.run(
        shlex.split(command),
        cwd=str(repo),
        input=stdin_text,
        capture_output=True,
        text=True,
        timeout=60,
    )
    return normalise(result.stdout), result.stderr, result.returncode


def show_transcript(case: dict, actual: list[str], stderr: str, exit_code: int) -> None:
    """Prints the console session for one case so the run can be reviewed."""
    print(RULE)
    print(case["name"])
    if case["aim"]:
        print(f"Aim: {case['aim']}")
    print(RULE)
    if case["data"] is None:
        print(f"Saved tasks before the run: none ({DATA_FILE.as_posix()} absent)")
    else:
        print(f"Saved tasks before the run ({DATA_FILE.as_posix()}):")
        for line in case["data"].splitlines() or ["(empty file)"]:
            print(f"  # {line}")
    print("Input typed by the user:")
    for line in case["input"].splitlines():
        print(f"  > {line}")
    print("Console output:")
    for line in actual:
        print(f"  | {line}")
    if stderr.strip():
        print("Standard error:")
        for line in stderr.rstrip().splitlines():
            print(f"  ! {line}")
    print(f"Exit code: {exit_code}")


def report_failure(case: dict, expected: list[str], actual: list[str]) -> None:
    """Prints expected against actual for a failed case."""
    print()
    print(RULE)
    print(f"FAILED: {case['name']}")
    print(RULE)
    print("Expected output:")
    for line in expected:
        print(f"  | {line}")
    print()
    print("Actual output:")
    for line in actual:
        print(f"  | {line}")
    print()
    print("Difference (- expected, + actual):")
    diff = difflib.unified_diff(expected, actual, fromfile="expected", tofile="actual", lineterm="")
    for line in diff:
        print(f"  {line}")
    print()
    print("Test session terminated at the first failure.")


def main(argv: list[str]) -> int:
    parser = argparse.ArgumentParser(description="Run the chatbot UI test cases.")
    parser.add_argument("--plan", default=str(DEFAULT_PLAN),
                        help=f"path to the test plan (default: {DEFAULT_PLAN})")
    parser.add_argument("--repo", default=".", help="repository root to run the program from")
    parser.add_argument("--case", help="run only cases whose heading contains this text")
    args = parser.parse_args(argv)

    repo = Path(args.repo).resolve()
    plan_path = Path(args.plan)
    if not plan_path.is_absolute():
        plan_path = repo / plan_path
    if not plan_path.exists():
        print(f"error: test plan not found at {plan_path}", file=sys.stderr)
        return 1

    try:
        command, build, cases = parse_plan(plan_path.read_text(encoding="utf-8"))
    except PlanError as exc:
        print(f"error: {exc}", file=sys.stderr)
        return 1

    if args.case:
        cases = [c for c in cases if args.case.lower() in c["name"].lower()]
    if not cases:
        print("error: no test cases to run", file=sys.stderr)
        return 1

    print(f"Plan:    {plan_path}")
    print(f"Command: {command}")
    print(f"Cases:   {len(cases)}")

    # Build before any case runs, so that a case can never pass against a stale build.
    if build:
        try:
            if not run_build(build, repo):
                return 1
        except FileNotFoundError:
            print(f"error: cannot run the build command '{build}' from {repo}", file=sys.stderr)
            return 1
        except subprocess.TimeoutExpired:
            print("error: the build did not finish within 600 seconds", file=sys.stderr)
            return 1
    print()

    for position, case in enumerate(cases, start=1):
        try:
            prepare_data_file(repo, case)
            actual, stderr, exit_code = run_case(command, repo, case)
        except FileNotFoundError:
            print(f"error: cannot run '{command}' from {repo}", file=sys.stderr)
            return 1
        except subprocess.TimeoutExpired:
            print(f"error: '{case['name']}' did not finish within 60 seconds", file=sys.stderr)
            return 1

        expected = normalise(case["expected"])
        show_transcript(case, actual, stderr, exit_code)

        if actual != expected:
            print("Result: FAIL")
            report_failure(case, expected, actual)
            print(f"\n{position - 1} of {len(cases)} case(s) passed before the failure.")
            return 1

        print("Result: PASS")
        print()

    print(RULE)
    print(f"All {len(cases)} case(s) passed.")
    return 0


if __name__ == "__main__":
    raise SystemExit(main(sys.argv[1:]))
