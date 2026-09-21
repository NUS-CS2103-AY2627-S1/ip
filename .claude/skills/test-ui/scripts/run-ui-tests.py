#!/usr/bin/env python3
"""Run the UI test cases recorded in test/ui-test-plan.md against the Auto program.

Each test case starts a fresh instance of the program, feeds its input lines to
stdin, and compares the captured console output with the expected output. Every
test case runs, whether or not earlier ones failed; the expected and actual
output for each failure is reported at the end.

Uses only the Python standard library.
"""

from __future__ import annotations

import argparse
import difflib
import re
import subprocess
import sys
from pathlib import Path

# <repo>/.claude/skills/test-ui/scripts/run-ui-tests.py -> parents[4] is <repo>
REPO_ROOT = Path(__file__).resolve().parents[4]
DEFAULT_PLAN = REPO_ROOT / "test" / "ui-test-plan.md"
SOURCE_DIR = REPO_ROOT / "src" / "main" / "java"
CLASSES_DIR = REPO_ROOT / "_temp" / "ui-test-classes"
TRANSCRIPT_PATH = REPO_ROOT / "_temp" / "ui-test-session.txt"
MAIN_CLASS = "auto.Auto"
RUN_TIMEOUT_SECONDS = 15
DATA_FILE = REPO_ROOT / "data" / "test_auto.txt"

RULE = "=" * 68
THIN_RULE = "-" * 68
PLACEHOLDER_PATTERN = re.compile(r"\{\{[A-Z0-9_]+\}\}")


class PlanError(Exception):
    """Raised when test/ui-test-plan.md cannot be parsed."""


def read_fenced_block(lines: list[str], start: int) -> tuple[str, int]:
    """Return the next fenced code block at or after `start`.

    Returns the block's text and the index of the line after the closing fence.
    Stops with an error if a heading appears before the opening fence, which
    means the expected block is missing.
    """
    i = start
    while i < len(lines) and not lines[i].lstrip().startswith("```"):
        if lines[i].startswith("#"):
            raise PlanError(f"expected a fenced code block before line {i + 1}")
        i += 1
    if i >= len(lines):
        raise PlanError("expected a fenced code block but reached end of plan")
    i += 1
    body: list[str] = []
    while i < len(lines) and not lines[i].lstrip().startswith("```"):
        body.append(lines[i])
        i += 1
    if i >= len(lines):
        raise PlanError("unterminated fenced code block")
    return "\n".join(body), i + 1


def parse_plan(text: str) -> tuple[dict[str, str], list[dict]]:
    """Parse the plan into reusable common blocks and an ordered list of cases.

    `## Common blocks` holds `### {{NAME}}` headings, each followed by a fenced
    block. Every other `###` heading is a test case, described by an `**Aim:**`
    line plus `**Input**` and `**Expected output**` fenced blocks.
    """
    lines = text.splitlines()
    common: dict[str, str] = {}
    cases: list[dict] = []
    section: str | None = None
    case: dict | None = None
    i = 0

    while i < len(lines):
        line = lines[i]

        if line.startswith("## "):
            section = line[3:].strip().lower()
            case = None
            i += 1
            continue

        if line.startswith("### "):
            title = line[4:].strip()
            if section is not None and section.startswith("common"):
                common[title], i = read_fenced_block(lines, i + 1)
                case = None
                continue
            case = {"title": title, "aim": "", "initial_data": None,
                    "data_path": "data/test_auto.txt", "input": None,
                    "expected": None}
            cases.append(case)
            i += 1
            continue

        stripped = line.strip()
        if case is not None:
            if stripped.startswith("**Aim:**"):
                case["aim"] = stripped[len("**Aim:**"):].strip()
            elif stripped == "**Initial data**":
                case["initial_data"], i = read_fenced_block(lines, i + 1)
                continue
            elif stripped.startswith("**Data path:**"):
                case["data_path"] = stripped[len("**Data path:**"):].strip().strip("`")
            elif stripped == "**Input**":
                case["input"], i = read_fenced_block(lines, i + 1)
                continue
            elif stripped == "**Expected output**":
                case["expected"], i = read_fenced_block(lines, i + 1)
                continue
        i += 1

    for entry in cases:
        if entry["input"] is None:
            raise PlanError(f"test case '{entry['title']}' has no **Input** block")
        if entry["expected"] is None:
            raise PlanError(f"test case '{entry['title']}' has no **Expected output** block")
    return common, cases


def expand_placeholders(expected: str, common: dict[str, str]) -> str:
    """Replace {{NAME}} tokens with the matching common block."""

    def substitute(match: re.Match[str]) -> str:
        key = match.group(0)
        if key not in common:
            raise PlanError(f"unknown placeholder {key}; define it under '## Common blocks'")
        return common[key]

    return PLACEHOLDER_PATTERN.sub(substitute, expected)


def normalize(text: str) -> str:
    """Make output comparable across platforms.

    Collapses CRLF and CR to LF, drops trailing whitespace on each line, and
    trims leading and trailing blank lines. The program mixes embedded '\\n'
    with platform line separators from println, and its banner has trailing
    spaces, so comparing raw text would fail for cosmetic reasons.
    """
    text = text.replace("\r\n", "\n").replace("\r", "\n")
    return "\n".join(line.rstrip() for line in text.split("\n")).strip("\n")


def compile_sources() -> None:
    """Compile every .java file under src/main/java into the temp classes dir."""
    sources = sorted(str(path) for path in SOURCE_DIR.rglob("*.java"))
    if not sources:
        raise SystemExit(f"No Java sources found under {SOURCE_DIR}")
    CLASSES_DIR.mkdir(parents=True, exist_ok=True)
    result = subprocess.run(
        ["javac", "-encoding", "UTF-8", "-d", str(CLASSES_DIR), *sources],
        capture_output=True,
        text=True,
    )
    if result.returncode != 0:
        print("Compilation failed; no test cases were run.\n")
        print(result.stdout + result.stderr)
        raise SystemExit(1)


def run_program(stdin_text: str, initial_data: str | None,
                data_path: str) -> subprocess.CompletedProcess[str]:
    """Start a fresh program instance and feed it the given input lines."""
    DATA_FILE.unlink(missing_ok=True)
    if initial_data is not None:
        DATA_FILE.parent.mkdir(parents=True, exist_ok=True)
        DATA_FILE.write_text(initial_data.rstrip("\n") + "\n", encoding="utf-8")
    return subprocess.run(
        ["java", "-Dfile.encoding=UTF-8", f"-Dauto.data.file={data_path}",
         "-cp", str(CLASSES_DIR), MAIN_CLASS],
        input=stdin_text.rstrip("\n") + "\n",
        capture_output=True,
        text=True,
        encoding="utf-8",
        errors="replace",
        timeout=RUN_TIMEOUT_SECONDS,
    )


def format_session_entry(case: dict, stdout: str, stderr: str, verdict: str) -> str:
    """Render one test case as a readable console session record."""
    parts = [RULE, case["title"]]
    if case["aim"]:
        parts.append(f"Aim: {case['aim']}")
    parts += [
        THIN_RULE,
        "input fed to the program:",
        THIN_RULE,
        normalize(case["input"]),
        THIN_RULE,
        "console output:",
        THIN_RULE,
        normalize(stdout),
    ]
    if stderr.strip():
        parts += [THIN_RULE, "stderr:", THIN_RULE, normalize(stderr)]
    parts += [THIN_RULE, f"result: {verdict}", ""]
    return "\n".join(parts)


def report_failure(case: dict, expected: str, actual: str,
                   process: subprocess.CompletedProcess[str] | None) -> None:
    """Print the expected and actual output for one failing case.

    `process` is None when the case timed out, in which case there is no exit
    code or stderr to report.
    """
    print(RULE)
    print(f"FAILED: {case['title']}")
    print(RULE)
    if case["aim"]:
        print(f"Aim: {case['aim']}\n")
    print("--- expected output " + "-" * 48)
    print(expected)
    print("--- actual output " + "-" * 50)
    print(actual)
    print("--- diff (- expected, + actual) " + "-" * 36)
    diff = difflib.unified_diff(
        expected.split("\n"), actual.split("\n"),
        fromfile="expected", tofile="actual", lineterm="",
    )
    print("\n".join(diff))
    if process is None:
        return
    if process.stderr.strip():
        print("--- stderr from the program " + "-" * 40)
        print(process.stderr.strip())
    if process.returncode != 0:
        print(f"\nProgram exited with code {process.returncode}.")


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--plan", type=Path, default=DEFAULT_PLAN,
                        help="path to the test plan (default: test/ui-test-plan.md)")
    parser.add_argument("--only", default=None,
                        help="run only test cases whose heading starts with this text")
    args = parser.parse_args()

    if not args.plan.is_file():
        raise SystemExit(f"Test plan not found: {args.plan}")

    try:
        common, cases = parse_plan(args.plan.read_text(encoding="utf-8"))
    except PlanError as error:
        raise SystemExit(f"Could not parse {args.plan}: {error}")

    if args.only:
        cases = [case for case in cases if case["title"].startswith(args.only)]
    if not cases:
        raise SystemExit("No test cases to run.")

    compile_sources()

    session: list[str] = []
    failures: list[tuple[dict, str, str, subprocess.CompletedProcess[str] | None]] = []

    # Every case runs even after a failure, so one broken behaviour cannot hide
    # the state of the rest of the plan.
    for case in cases:
        expected = normalize(expand_placeholders(case["expected"], common))
        try:
            process = run_program(case["input"], case["initial_data"], case["data_path"])
        except subprocess.TimeoutExpired:
            session.append(format_session_entry(case, "", "", "FAIL (timed out)"))
            failures.append((case, expected,
                             f"(no output: the program did not exit within "
                             f"{RUN_TIMEOUT_SECONDS}s; every test case must end "
                             f"with a 'bye' command)", None))
            continue

        actual = normalize(process.stdout)
        passed = expected == actual
        session.append(format_session_entry(case, process.stdout, process.stderr,
                                            "PASS" if passed else "FAIL"))
        if not passed:
            failures.append((case, expected, actual, process))

    TRANSCRIPT_PATH.parent.mkdir(parents=True, exist_ok=True)
    TRANSCRIPT_PATH.write_text("\n".join(session), encoding="utf-8")

    print("\n".join(session))

    for failure in failures:
        report_failure(*failure)

    if failures:
        print(RULE)
        print(f"{len(cases) - len(failures)} of {len(cases)} test case(s) passed; "
              f"{len(failures)} failed:")
        for case, *_ in failures:
            print(f"  FAILED  {case['title']}")
        print(f"\nSession record saved to {TRANSCRIPT_PATH}")
        return 1

    print(f"All {len(cases)} test case(s) passed.")
    print(f"Session record saved to {TRANSCRIPT_PATH}")
    return 0


if __name__ == "__main__":
    sys.exit(main())
