#!/usr/bin/env python3
"""Run the text UI test cases described in test/ui-test-plan.md.

Each test case is run as its own program session: the sources are compiled
once, then for every case the program is started in a fresh empty working
directory, the case's input lines are fed to its standard input, and the
captured standard output is compared against the case's expected output.

A fresh working directory per case matters because the program saves its task
list to a path relative to the working directory, so without it one case would
inherit the tasks left behind by an earlier one.

Testing stops at the first failing case, and the failure is reported with the
expected and actual output side by side.
"""

import argparse
import difflib
import os
import re
import shutil
import subprocess
import sys
import tempfile

# Exit codes, so a caller can tell a failing test from a broken setup.
EXIT_PASS = 0
EXIT_TEST_FAILED = 1
EXIT_SETUP_ERROR = 2

FENCE_RE = re.compile(r"^```[a-zA-Z0-9_-]*\s*$")
SETTING_RE = re.compile(r"^\s*[-*]\s*\*\*(?P<key>[^*]+?):\*\*\s*`(?P<value>[^`]*)`")
CASE_HEADING_RE = re.compile(r"^###\s+(?P<id>[^:]+?)\s*:\s*(?P<title>.+?)\s*$")
AIM_RE = re.compile(r"^\s*\*\*Aim:\*\*\s*(?P<aim>.+?)\s*$")
LABEL_RE = re.compile(r"^\s*\*\*(?P<label>Input|Expected output):\*\*\s*$")


class PlanError(Exception):
    """Raised when the test plan cannot be understood or the setup is wrong."""


class TestCase:
    """One test case read from the plan."""

    def __init__(self, case_id, title, aim, input_lines, expected):
        self.case_id = case_id
        self.title = title
        self.aim = aim
        self.input_lines = input_lines
        self.expected = expected


def normalise(text):
    """Split output into lines for comparison.

    Trailing whitespace on a line, and blank lines at either end of the block,
    are not things a test case should have to match exactly, so they are
    removed. Blank lines *inside* a block are significant and are kept.
    """
    lines = [line.rstrip() for line in text.replace("\r\n", "\n").split("\n")]
    while lines and lines[0] == "":
        lines.pop(0)
    while lines and lines[-1] == "":
        lines.pop()
    return lines


def read_fenced_block(lines, start):
    """Return (block_text, index_after_block) for the next fenced code block.

    Scanning starts at `start`; any blank lines before the opening fence are
    skipped.
    """
    i = start
    while i < len(lines) and lines[i].strip() == "":
        i += 1
    if i >= len(lines) or not FENCE_RE.match(lines[i].strip()):
        raise PlanError(f"expected a fenced code block at line {i + 1}")
    i += 1
    body = []
    while i < len(lines) and not FENCE_RE.match(lines[i].strip()):
        body.append(lines[i])
        i += 1
    if i >= len(lines):
        raise PlanError("a fenced code block was never closed")
    return "\n".join(body), i + 1


def finish_case(case):
    """Validate a collected test case and turn it into a TestCase."""
    labels = (("aim", "**Aim:**"), ("input", "**Input:**"),
              ("expected", "**Expected output:**"))
    for key, label in labels:
        if case[key] is None:
            raise PlanError(
                f'test case "{case["id"]}" (line {case["line"]}) is missing its '
                f"{label} entry"
            )
    input_lines = [ln for ln in case["input"].split("\n") if ln.strip() != ""]
    if not input_lines:
        raise PlanError(f'test case "{case["id"]}" has an empty Input block')
    return TestCase(case["id"], case["title"], case["aim"], input_lines,
                    case["expected"])


def parse_plan(path):
    """Parse the test plan into (settings, greeting, farewell, cases)."""
    try:
        with open(path, encoding="utf-8") as f:
            lines = f.read().split("\n")
    except OSError as e:
        raise PlanError(f"cannot read the test plan: {e}")

    settings = {}
    greeting = None
    farewell = None
    cases = []

    section = None       # current "## " heading, lower-cased
    subsection = None    # current "### " heading, lower-cased
    current = None       # test case being collected
    i = 0
    while i < len(lines):
        line = lines[i]

        if line.startswith("## ") and not line.startswith("### "):
            if current is not None:
                cases.append(finish_case(current))
                current = None
            section = line[3:].strip().lower()
            subsection = None
            i += 1
            continue

        if line.startswith("### "):
            if current is not None:
                cases.append(finish_case(current))
                current = None
            subsection = line[4:].strip().lower()
            if section == "test cases":
                m = CASE_HEADING_RE.match(line)
                if not m:
                    raise PlanError(
                        f"line {i + 1}: a test case heading must read "
                        f'"### <id>: <title>", got: {line}'
                    )
                current = {
                    "id": m.group("id").strip(),
                    "title": m.group("title").strip(),
                    "aim": None,
                    "input": None,
                    "expected": None,
                    "line": i + 1,
                }
            i += 1
            continue

        if section == "setup":
            m = SETTING_RE.match(line)
            if m:
                settings[m.group("key").strip().lower()] = m.group("value").strip()
                i += 1
                continue
            if subsection in ("greeting", "farewell") and FENCE_RE.match(line.strip()):
                block, i = read_fenced_block(lines, i)
                if subsection == "greeting":
                    greeting = block
                else:
                    farewell = block
                continue
            i += 1
            continue

        if current is not None:
            m = AIM_RE.match(line)
            if m:
                # An aim may wrap over several lines; keep reading until a
                # blank line or the next labelled part of the test case.
                aim_parts = [m.group("aim").strip()]
                i += 1
                while i < len(lines):
                    nxt = lines[i]
                    if (nxt.strip() == "" or nxt.startswith("#")
                            or nxt.lstrip().startswith("**")
                            or FENCE_RE.match(nxt.strip())):
                        break
                    aim_parts.append(nxt.strip())
                    i += 1
                current["aim"] = " ".join(aim_parts)
                continue
            m = LABEL_RE.match(line)
            if m:
                key = "input" if m.group("label") == "Input" else "expected"
                if current[key] is not None:
                    raise PlanError(
                        f'test case "{current["id"]}" has more than one '
                        f"**{m.group('label')}:** block"
                    )
                current[key], i = read_fenced_block(lines, i + 1)
                continue

        i += 1

    if current is not None:
        cases.append(finish_case(current))

    if greeting is None:
        raise PlanError('the plan has no "### Greeting" block under "## Setup"')
    if farewell is None:
        raise PlanError('the plan has no "### Farewell" block under "## Setup"')
    if not cases:
        raise PlanError('the plan has no test cases under "## Test cases"')

    return settings, greeting, farewell, cases


def check_java_version():
    """Return the javac version, stopping if it is older than the project needs."""
    try:
        result = subprocess.run(["javac", "-version"], capture_output=True,
                                text=True)
    except FileNotFoundError:
        raise PlanError("javac was not found on PATH; install Java 25 first")
    reported = (result.stdout + result.stderr).strip()
    m = re.search(r"(\d+)", reported)
    if m and int(m.group(1)) < 25:
        raise PlanError(
            f"this project needs Java 25 but javac reports {reported}.\n"
            "Switch to it with:  sdk use java 25.0.3.fx-zulu"
        )
    return reported


def compile_sources(source_dir, classes_dir):
    """Compile every .java file under source_dir; return how many there were."""
    sources = []
    for root, _dirs, files in os.walk(source_dir):
        sources.extend(os.path.join(root, f) for f in files if f.endswith(".java"))
    if not sources:
        raise PlanError(f"no .java files found under {source_dir}")
    result = subprocess.run(["javac", "-d", classes_dir] + sorted(sources),
                            capture_output=True, text=True)
    if result.returncode != 0:
        raise PlanError("the program did not compile:\n"
                        + (result.stdout + result.stderr).rstrip())
    return len(sources)


def run_case(case, classes_dir, main_class, exit_command, timeout):
    """Run one test case in its own working directory.

    Returns (stdin_text, output) so the caller can show the session.
    """
    work_dir = tempfile.mkdtemp(prefix="ui-test-")
    stdin_lines = list(case.input_lines)
    if exit_command and stdin_lines[-1].strip() != exit_command:
        stdin_lines.append(exit_command)
    stdin_text = "\n".join(stdin_lines) + "\n"
    try:
        result = subprocess.run(
            ["java", "-cp", os.path.abspath(classes_dir), main_class],
            input=stdin_text, capture_output=True, text=True,
            cwd=work_dir, timeout=timeout,
        )
    except subprocess.TimeoutExpired:
        raise PlanError(
            f'test case "{case.case_id}" did not finish within {timeout}s. '
            "The program is probably still waiting for input - check that the "
            f'plan\'s exit command ("{exit_command}") ends the program.'
        )
    finally:
        shutil.rmtree(work_dir, ignore_errors=True)
    output = result.stdout
    if result.stderr.strip():
        output += "\n[stderr]\n" + result.stderr
    return stdin_text, output


def strip_session_frame(actual_lines, greeting_lines, farewell_lines, case_id):
    """Remove the greeting and farewell that every session prints.

    Both are checked rather than blindly dropped, so a change to either is
    reported instead of being silently folded into a test case's output.
    """
    if actual_lines[:len(greeting_lines)] != greeting_lines:
        raise PlanError(
            f'test case "{case_id}": the program\'s greeting does not match the '
            'plan\'s "### Greeting" block. Update that block if the greeting '
            "changed on purpose."
        )
    body = actual_lines[len(greeting_lines):]
    if farewell_lines:
        if body[-len(farewell_lines):] != farewell_lines:
            raise PlanError(
                f'test case "{case_id}": the program\'s farewell does not match '
                'the plan\'s "### Farewell" block. Update that block if the '
                "farewell changed on purpose."
            )
        body = body[:-len(farewell_lines)]
    while body and body[0] == "":
        body.pop(0)
    while body and body[-1] == "":
        body.pop()
    return body


def print_transcript(case, stdin_text, output):
    """Print one case's console input and output as a readable session record."""
    print(f"+- {case.case_id}: {case.title}")
    print(f"|  Aim: {case.aim}")
    print("+- console input --------------------------------------------")
    for line in stdin_text.rstrip("\n").split("\n"):
        print(f"|  > {line}")
    print("+- console output -------------------------------------------")
    for line in output.rstrip("\n").split("\n"):
        print(f"|  {line}")


def report_failure(case, index, total, expected, body):
    """Report the failing case, then stop the session."""
    print("+- FAIL\n")
    print("=" * 64)
    print(f'TEST SESSION TERMINATED at "{case.case_id}: {case.title}"')
    print(f"  Aim: {case.aim}")
    print(f"  Ran {index} of {total} case(s); {total - index} not run.")
    print("=" * 64)
    print("\nEXPECTED OUTPUT")
    print("-" * 64)
    print("\n".join(expected) if expected else "(nothing)")
    print("\nACTUAL OUTPUT (greeting and farewell removed)")
    print("-" * 64)
    print("\n".join(body) if body else "(nothing)")
    print("\nDIFFERENCE (- expected, + actual)")
    print("-" * 64)
    diff = difflib.unified_diff(expected, body, fromfile="expected",
                                tofile="actual", lineterm="", n=2)
    print("\n".join(diff))


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--plan", default="test/ui-test-plan.md",
                        help="path to the test plan (default: %(default)s)")
    parser.add_argument("--only", action="append", metavar="ID",
                        help="run only the test case with this id "
                             "(repeatable; default: run every case)")
    parser.add_argument("--timeout", type=float, default=15.0,
                        help="seconds to allow each case (default: %(default)s)")
    args = parser.parse_args()

    classes_dir = None
    try:
        settings, greeting, farewell, cases = parse_plan(args.plan)
        source_dir = settings.get("source directory", "src/main/java")
        main_class = settings.get("main class", "Duncan")
        exit_command = settings.get("exit command", "bye")

        if args.only:
            wanted = {c.lower() for c in args.only}
            cases = [c for c in cases if c.case_id.lower() in wanted]
            if not cases:
                raise PlanError(f"no test case matches {args.only}")

        javac_version = check_java_version()
        classes_dir = tempfile.mkdtemp(prefix="ui-test-classes-")
        file_count = compile_sources(source_dir, classes_dir)

        print(f"Test plan:  {args.plan}")
        print(f"Toolchain:  {javac_version}")
        print(f"Compiled:   {file_count} source file(s) from {source_dir}")
        print(f"Test cases: {len(cases)}")
        print()

        greeting_lines = normalise(greeting)
        farewell_lines = normalise(farewell)

        for index, case in enumerate(cases, start=1):
            stdin_text, output = run_case(case, classes_dir, main_class,
                                          exit_command, args.timeout)
            print_transcript(case, stdin_text, output)
            body = strip_session_frame(normalise(output), greeting_lines,
                                       farewell_lines, case.case_id)
            expected = normalise(case.expected)
            if body != expected:
                report_failure(case, index, len(cases), expected, body)
                return EXIT_TEST_FAILED
            print("+- PASS\n")
    except PlanError as e:
        print(f"ERROR: {e}", file=sys.stderr)
        return EXIT_SETUP_ERROR
    finally:
        if classes_dir:
            shutil.rmtree(classes_dir, ignore_errors=True)

    print("=" * 64)
    print(f"ALL {len(cases)} TEST CASE(S) PASSED")
    print("=" * 64)
    return EXIT_PASS


if __name__ == "__main__":
    sys.exit(main())
