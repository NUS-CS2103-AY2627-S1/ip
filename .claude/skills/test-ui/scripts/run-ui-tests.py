#!/usr/bin/env python3
"""Run the text-UI test cases described in test/ui-test-plan.md.

Each test case supplies a list of input lines and the console output expected
from the program. This script compiles the program once, then feeds each test
case's input to a fresh run and compares the captured output with the expected
output. The session transcript is printed as it goes, so a reader can see the
whole conversation. The first failing test case stops the run and its expected
and actual outputs are reported.

Usage:
    python3 run-ui-tests.py [--plan test/ui-test-plan.md] [--only TC2 TC5]
"""

import argparse
import difflib
import re
import shutil
import subprocess
import sys
import tempfile
from pathlib import Path

# A test case starts at a level-3 heading such as "### TC1: Greets the user".
CASE_HEADING = re.compile(r"^###\s+(?P<id>\S+?):\s*(?P<title>.+?)\s*$")
# A reusable output snippet starts at a level-4 heading such as "#### GREETING".
SNIPPET_HEADING = re.compile(r"^####\s+(?P<name>[A-Za-z][A-Za-z0-9_]*)\s*$")
# Field labels inside a test case, e.g. "**Aim:** ..." or "**Input:**".
FIELD_LABEL = re.compile(
    r"^\*\*(?P<name>Aim|Input|Expected output|Saved file|Expected saved file):?\*\*:?"
    r"\s*(?P<inline>.*)$",
    re.IGNORECASE)
FENCE = re.compile(r"^```")
# A snippet reference inside an expected output, e.g. "{{GREETING}}".
PLACEHOLDER = re.compile(r"\{\{\s*(?P<name>[A-Za-z][A-Za-z0-9_]*)\s*\}\}")

FIELD_NAMES = {"aim": "Aim", "input": "Input", "expected output": "Expected output",
               "saved file": "Saved file", "expected saved file": "Expected saved file"}

# Where the program keeps its saved task list, relative to the working directory.
SAVE_FILE = Path("data") / "tasks.txt"


class TestPlanError(Exception):
    """Raised when the test plan cannot be understood."""


class TestCase:
    def __init__(self, case_id, title, aim, input_lines, expected,
                 saved_file=None, expected_saved_file=None):
        self.case_id = case_id
        self.title = title
        self.aim = aim
        self.input_lines = input_lines
        self.expected = expected
        # The save file to put in place before the run, or None to start with no
        # save file at all (which is what a first run on a new machine looks like).
        self.saved_file = saved_file
        # The save file the run should leave behind, or None not to check it.
        self.expected_saved_file = expected_saved_file


def expand_snippets(text, snippets, case_id):
    """Replaces every {{NAME}} in an expected output with the named snippet.

    Blocks such as the greeting banner appear in the output of every run. Writing
    them once as a snippet keeps the test plan readable, and means a change to the
    banner is made in one place rather than in every test case.
    """
    def substitute(match):
        name = match.group("name")
        if name not in snippets:
            raise TestPlanError(
                f"test case {case_id} refers to unknown snippet {{{{{name}}}}}; "
                f"known snippets: {', '.join(sorted(snippets)) or '(none)'}")
        return snippets[name]

    # A snippet may itself refer to another one, so expand until nothing is left.
    for _ in range(10):
        expanded = PLACEHOLDER.sub(substitute, text)
        if expanded == text:
            return expanded
        text = expanded
    raise TestPlanError(f"test case {case_id} has snippets nested more than 10 deep")


def parse_plan(path):
    """Reads the test plan and returns the list of test cases it describes.

    The parser only looks at the headings and the labelled fenced blocks it knows
    about, so the rest of the file is free-form prose that documents the plan for
    human readers.
    """
    if not path.exists():
        raise TestPlanError(f"test plan not found: {path}")

    lines = path.read_text(encoding="utf-8").splitlines()
    cases = []
    snippets = {}
    current = None       # fields collected for the test case we are inside
    pending = None       # where the next fenced block's contents should be stored
    fence_body = None    # lines gathered inside the current fenced block

    def finish(case):
        if case is None:
            return
        missing = [f for f in ("Aim", "Input", "Expected output") if f not in case["fields"]]
        if missing:
            raise TestPlanError(
                f"test case {case['id']} is missing: {', '.join(missing)}")
        cases.append(TestCase(
            case_id=case["id"],
            title=case["title"],
            aim=case["fields"]["Aim"].strip(),
            input_lines=case["fields"]["Input"].splitlines(),
            expected=case["fields"]["Expected output"],
            saved_file=case["fields"].get("Saved file"),
            expected_saved_file=case["fields"].get("Expected saved file"),
        ))

    for line in lines:
        if fence_body is not None:
            # Inside a fenced block: the next fence closes it.
            if FENCE.match(line):
                kind, key = pending
                if kind == "snippet":
                    snippets[key] = "\n".join(fence_body)
                else:
                    current["fields"][key] = "\n".join(fence_body)
                fence_body = None
                pending = None
            else:
                fence_body.append(line)
            continue

        snippet = SNIPPET_HEADING.match(line)
        if snippet:
            finish(current)
            current = None
            pending = ("snippet", snippet.group("name"))
            continue

        heading = CASE_HEADING.match(line)
        if heading:
            finish(current)
            current = {"id": heading.group("id"), "title": heading.group("title"), "fields": {}}
            pending = None
            continue

        if current is not None:
            label = FIELD_LABEL.match(line.strip())
            if label:
                field = FIELD_NAMES[label.group("name").lower()]
                inline = label.group("inline").strip()
                if inline:
                    # Short fields such as the aim are written on the label's own line.
                    current["fields"][field] = inline
                    pending = None
                else:
                    pending = ("case", field)
                continue

        if pending is not None and FENCE.match(line.strip()):
            fence_body = []

    finish(current)
    if not cases:
        raise TestPlanError(f"no test cases found in {path}")

    for case in cases:
        case.expected = expand_snippets(case.expected, snippets, case.case_id)
    return cases


def compile_program(repo_root, classes_dir):
    """Compiles every source file into classes_dir and returns the javac output."""
    sources = sorted(str(p) for p in (repo_root / "src" / "main" / "java").rglob("*.java"))
    if not sources:
        raise TestPlanError("no Java sources found under src/main/java")
    classes_dir.mkdir(parents=True, exist_ok=True)
    result = subprocess.run(
        ["javac", "-d", str(classes_dir), *sources],
        capture_output=True, text=True,
    )
    if result.returncode != 0:
        raise TestPlanError("compilation failed:\n" + (result.stderr or result.stdout))
    return sources


def normalise(text):
    """Makes output comparable across platforms and editors.

    Windows line endings, trailing spaces on a line, and trailing blank lines at
    the end of the output are all invisible on screen, so they should not decide
    whether a test passes.
    """
    text = text.replace("\r\n", "\n").replace("\r", "\n")
    return "\n".join(line.rstrip() for line in text.split("\n")).rstrip("\n")


def run_case(case, classes_dir, main_class, timeout):
    """Runs one test case and returns (passed, actual_output, file_problem).

    Each case runs in its own empty working directory. The program saves its task
    list to ./data/tasks.txt, so cases sharing a directory would inherit each
    other's tasks and the order the cases were run in would change their results.
    """
    stdin_data = "\n".join(case.input_lines)
    if stdin_data:
        stdin_data += "\n"

    work_dir = Path(tempfile.mkdtemp(prefix=f"ui-test-{case.case_id}-"))
    try:
        if case.saved_file is not None:
            save_path = work_dir / SAVE_FILE
            save_path.parent.mkdir(parents=True, exist_ok=True)
            body = case.saved_file
            save_path.write_text(body + "\n" if body else "", encoding="utf-8")
        try:
            result = subprocess.run(
                ["java", "-cp", str(classes_dir), main_class],
                input=stdin_data, capture_output=True, text=True, timeout=timeout,
                cwd=work_dir,
            )
        except subprocess.TimeoutExpired:
            return False, f"<test case timed out after {timeout}s>", None

        # stderr is folded in so an unexpected stack trace shows up as a failure
        # rather than disappearing.
        actual = result.stdout + (("\n" + result.stderr) if result.stderr.strip() else "")
        passed = normalise(actual) == normalise(case.expected)

        file_problem = check_saved_file(case, work_dir)
        return passed and file_problem is None, actual, file_problem
    finally:
        shutil.rmtree(work_dir, ignore_errors=True)


def check_saved_file(case, work_dir):
    """Returns a description of how the saved file differs from what was expected.

    Returns None when the case does not check the saved file, or when it matches.
    """
    if case.expected_saved_file is None:
        return None

    save_path = work_dir / SAVE_FILE
    expected = normalise(case.expected_saved_file)
    if not save_path.exists():
        if expected == "":
            return None
        return f"expected {SAVE_FILE} to be written, but no such file exists"

    actual = normalise(save_path.read_text(encoding="utf-8"))
    if actual == expected:
        return None
    diff = "\n".join(difflib.unified_diff(
        expected.split("\n"), actual.split("\n"),
        fromfile="expected " + str(SAVE_FILE), tofile="actual " + str(SAVE_FILE),
        lineterm=""))
    return f"the saved file does not match:\n{diff}"


def print_transcript(case, actual):
    """Prints the console session for one test case: what went in, what came out."""
    print(f"--- {case.case_id}: {case.title} ---")
    print(f"    Aim: {case.aim}")
    print("    Input:")
    for line in case.input_lines:
        print(f"      > {line}")
    print("    Console output:")
    for line in normalise(actual).split("\n"):
        print(f"      | {line}")
    print()


def report_failure(case, actual, file_problem=None):
    print("=" * 70)
    print(f"FAILED: {case.case_id}: {case.title}")
    print(f"Aim: {case.aim}")
    print("=" * 70)
    print("\n--- EXPECTED ---")
    print(normalise(case.expected))
    print("\n--- ACTUAL ---")
    print(normalise(actual))
    print("\n--- DIFF (expected vs actual) ---")
    diff = difflib.unified_diff(
        normalise(case.expected).split("\n"),
        normalise(actual).split("\n"),
        fromfile="expected", tofile="actual", lineterm="",
    )
    for line in diff:
        print(line)
    if file_problem is not None:
        print("\n--- SAVED FILE ---")
        print(file_problem)
    print()
    print("Test session terminated at the first failure; "
          "later test cases were not run.")


def main():
    parser = argparse.ArgumentParser(description=__doc__,
                                     formatter_class=argparse.RawDescriptionHelpFormatter)
    parser.add_argument("--plan", default="test/ui-test-plan.md",
                        help="path to the test plan (default: test/ui-test-plan.md)")
    parser.add_argument("--repo-root", default=".", help="repository root (default: .)")
    parser.add_argument("--main-class", default="davidgoggins.DavidGoggins",
                        help="class whose main() starts the program")
    parser.add_argument("--only", nargs="*", metavar="ID",
                        help="run only the test cases with these ids")
    parser.add_argument("--timeout", type=float, default=20.0,
                        help="seconds to allow each test case (default: 20)")
    args = parser.parse_args()

    repo_root = Path(args.repo_root).resolve()
    plan_path = (repo_root / args.plan) if not Path(args.plan).is_absolute() else Path(args.plan)

    try:
        cases = parse_plan(plan_path)
        classes_dir = Path(tempfile.mkdtemp(prefix="ui-test-classes-"))
        try:
            sources = compile_program(repo_root, classes_dir)
            print(f"Compiled {len(sources)} source file(s).")
            print(f"Test plan: {plan_path}")

            selected = cases
            if args.only:
                wanted = set(args.only)
                selected = [c for c in cases if c.case_id in wanted]
                unknown = wanted - {c.case_id for c in cases}
                if unknown:
                    raise TestPlanError(f"unknown test case id(s): {', '.join(sorted(unknown))}")

            print(f"Running {len(selected)} test case(s).\n")
            print("=" * 70)
            print("TEST SESSION TRANSCRIPT")
            print("=" * 70 + "\n")

            for index, case in enumerate(selected, start=1):
                passed, actual, file_problem = run_case(
                    case, classes_dir, args.main_class, args.timeout)
                print_transcript(case, actual)
                if not passed:
                    report_failure(case, actual, file_problem)
                    print(f"\nRESULT: {index - 1} passed, 1 failed, "
                          f"{len(selected) - index} not run.")
                    return 1
                print(f"    PASS\n")

            print("=" * 70)
            print(f"RESULT: all {len(selected)} test case(s) passed.")
            print("=" * 70)
            return 0
        finally:
            shutil.rmtree(classes_dir, ignore_errors=True)
    except TestPlanError as e:
        print(f"error: {e}", file=sys.stderr)
        return 2


if __name__ == "__main__":
    sys.exit(main())
