#!/usr/bin/env python3
"""Run the text-UI test cases recorded in a test plan against the chatbot.

The test plan (by default ``test/ui-test-plan.md``) is the single source of
truth: it names the program under test and lists the test cases, each with an
aim, the lines to type, and the console output those lines should produce.

For every test case this script feeds the input lines to a freshly started
program and compares the console output with the expected output.  It prints a
record of the console session as it goes, and stops at the first failing test
case so the failure is the last thing on screen.

Each test case runs in a working directory of its own.  The program keeps the
task list in a file below that directory, so this is what stops one test case
from finding the tasks another one saved -- and it keeps the repository clean
of files written by a test run.  A test case may put a save file there before
the program starts, and may state what the save file should hold afterwards.

Usage:
    python3 .claude/skills/test-ui/scripts/run-ui-tests.py [--plan PATH] [--only ID]

Exit codes: 0 all test cases passed, 1 a test case failed, 2 setup failed
(plan unreadable, or the program did not compile).
"""

import argparse
import difflib
import re
import shutil
import subprocess
import sys
import tempfile
from pathlib import Path

# Width of the rules printed between test cases, chosen to match the width of
# the divider the chatbot itself prints so the transcript lines up on screen.
RULE = "=" * 72
THIN_RULE = "-" * 72

# How long a single test case may run before it is treated as a failure.  A
# text UI that is waiting for input it will never get would otherwise hang the
# whole test session.
TIMEOUT_SECONDS = 10

# Where the program keeps its tasks, relative to the working directory it is
# started in, unless the plan says otherwise.
DEFAULT_DATA_FILE = "data/bob.txt"

# What a "Data file after" block holds when the test case expects the program
# to have left no save file at all.  An empty block cannot say this: it would
# be indistinguishable from expecting an empty file.
NO_FILE_MARKER = "(no file)"


class PlanError(Exception):
    """Raised when the test plan cannot be understood."""


class TestCase:
    """One test case read from the test plan.

    ``data_before`` is the save file to put in place before the program starts,
    and ``data_after`` is what the save file should hold when it has finished.
    Either is ``None`` when the test case says nothing about it: no save file
    before, and no check on the save file afterwards.
    """

    def __init__(self, case_id, title, aim, input_text, expected_text,
                 data_before=None, data_after=None):
        self.case_id = case_id
        self.title = title
        self.aim = aim
        self.input_text = input_text
        self.expected_text = expected_text
        self.data_before = data_before
        self.data_after = data_after


def parse_settings(text):
    """Reads the program-under-test settings from the plan.

    The settings are written as ``- **Key:** `value`` lines so that they stay
    readable as ordinary Markdown while still being easy to pick out here.
    """
    settings = {}
    for key in ("Main class", "Source directory", "Data file"):
        match = re.search(r"\*\*" + key + r":\*\*\s*`?([^`\n]+)`?", text)
        if match:
            settings[key] = match.group(1).strip()
    missing = {"Main class", "Source directory"} - settings.keys()
    if missing:
        raise PlanError(
            "the plan does not state: " + ", ".join(sorted(missing))
            + " (expected lines such as: - **Main class:** `Bob`)"
        )
    # The data file is optional: a program that saves nothing needs no such
    # setting, and a plan that does not name one gets the usual location.
    settings.setdefault("Data file", DEFAULT_DATA_FILE)
    return settings


def parse_cases(text):
    """Reads the test cases from the ``## Test cases`` section of the plan.

    Each test case is a ``###`` heading holding an ``**Aim:**`` line, an
    ``**Input**`` fenced block, and an ``**Expected output**`` fenced block.
    It may also hold a ``**Data file before**`` block, put in place as the save
    file before the program starts, and a ``**Data file after**`` block, checked
    against the save file once the program has finished.
    """
    section = re.search(r"^##\s+Test cases\s*$(.*?)(?=^##\s+|\Z)", text,
                        re.MULTILINE | re.DOTALL)
    if section is None:
        raise PlanError("the plan has no '## Test cases' section")

    # Split the section into one chunk per '###' heading, keeping the heading
    # text alongside the body it introduces.
    chunks = re.split(r"^###\s+(.*)$", section.group(1), flags=re.MULTILINE)[1:]
    if not chunks:
        raise PlanError("the '## Test cases' section lists no test cases")

    cases = []
    for heading, body in zip(chunks[0::2], chunks[1::2]):
        heading = heading.strip()
        # "TC3 - Mark and unmark" splits into an id and a readable title.
        parts = re.split(r"\s+[-—]\s+", heading, maxsplit=1)
        case_id = parts[0].strip()
        title = parts[1].strip() if len(parts) > 1 else ""

        # The aim may be wrapped over several lines, so it runs from the
        # "**Aim:**" marker up to the next blank line and is joined into one.
        aim_match = re.search(r"\*\*Aim:\*\*\s*(.+?)(?=\n[ \t]*\n)", body, re.DOTALL)
        aim = " ".join(aim_match.group(1).split()) if aim_match else ""

        blocks = extract_labelled_blocks(body)
        for label in ("input", "expected output"):
            if label not in blocks:
                raise PlanError(
                    f"test case '{case_id}' has no **{label.title()}** block")
        if not aim:
            raise PlanError(f"test case '{case_id}' has no **Aim:** line")

        cases.append(TestCase(case_id, title, aim,
                              blocks["input"], blocks["expected output"],
                              blocks.get("data file before"),
                              blocks.get("data file after")))
    return cases


def extract_labelled_blocks(body):
    """Returns the fenced code blocks of a test case, keyed by the bold label
    that introduces them (for example ``input`` or ``expected output``)."""
    blocks = {}
    label = None
    fence = None
    collected = []
    for line in body.splitlines():
        if fence is None:
            heading = re.fullmatch(r"\*\*([A-Za-z ]+)\*\*\s*", line.strip())
            if heading:
                label = heading.group(1).strip().lower()
                continue
            opening = re.match(r"\s*(```+|~~~+)", line)
            if opening and label is not None:
                fence = opening.group(1)
                collected = []
            continue
        if line.strip().startswith(fence):
            blocks[label] = "\n".join(collected)
            label = None
            fence = None
            continue
        collected.append(line)
    return blocks


def normalise(text):
    """Puts output into the form used for comparison.

    Trailing spaces on a line and blank lines at the very end are invisible on
    screen, so they are ignored rather than reported as differences; every
    other character must match exactly.
    """
    lines = [line.rstrip() for line in text.replace("\r\n", "\n").split("\n")]
    while lines and lines[-1] == "":
        lines.pop()
    return lines


def compile_program(source_dir, classes_dir):
    """Compiles the program under test, returning javac's output on failure."""
    sources = sorted(str(path) for path in Path(source_dir).rglob("*.java"))
    if not sources:
        raise PlanError(f"no .java files found under '{source_dir}'")
    result = subprocess.run(
        ["javac", "-d", str(classes_dir), *sources],
        capture_output=True, text=True)
    return result.returncode, (result.stdout + result.stderr).strip()


def run_case(case, classes_dir, main_class, run_dir, data_file):
    """Runs one test case and returns the program's stdout, stderr and status.

    The program is started in ``run_dir``, a directory of its own, holding
    nothing but the save file the test case asked for.  Both halves of that
    matter: the program cannot see what another test case saved, and what this
    one saves goes nowhere near the repository.
    """
    if case.data_before is not None:
        data_path = run_dir / data_file
        data_path.parent.mkdir(parents=True, exist_ok=True)
        text = case.data_before
        if text and not text.endswith("\n"):
            text += "\n"
        data_path.write_text(text, encoding="utf-8")

    # Each test case starts the program again, so no test case can be affected
    # by tasks an earlier one left in memory either.
    stdin_text = case.input_text
    if stdin_text and not stdin_text.endswith("\n"):
        stdin_text += "\n"
    try:
        result = subprocess.run(
            ["java", "-cp", str(classes_dir), main_class],
            input=stdin_text, capture_output=True, text=True,
            cwd=str(run_dir), timeout=TIMEOUT_SECONDS)
        return result.stdout, result.stderr.strip(), result.returncode, False
    except subprocess.TimeoutExpired as expired:
        partial = expired.stdout or ""
        if isinstance(partial, bytes):
            partial = partial.decode(errors="replace")
        return partial, "", None, True


def read_data_file(run_dir, data_file):
    """Returns what the program left in the save file, or ``None`` if it left none."""
    data_path = run_dir / data_file
    if not data_path.is_file():
        return None
    return data_path.read_text(encoding="utf-8")


def describe_data_file(text):
    """Returns the save file's contents as they are shown in the session record."""
    return NO_FILE_MARKER if text is None else text


def data_file_matches(expected_text, actual_text):
    """Returns whether the save file holds what the test case said it should.

    The expected block may be the marker ``(no file)``, which asks for the
    program to have left no save file behind at all.
    """
    if expected_text.strip() == NO_FILE_MARKER:
        return actual_text is None
    if actual_text is None:
        return False
    return normalise(expected_text) == normalise(actual_text)


def print_block(title, text):
    print(f"{THIN_RULE}\n{title}\n{THIN_RULE}")
    print(text if text.strip() else "(no output)")


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--plan", default="test/ui-test-plan.md",
                        help="path to the test plan (default: test/ui-test-plan.md)")
    parser.add_argument("--only", metavar="ID",
                        help="run just the test case with this id, e.g. TC3")
    args = parser.parse_args()

    plan_path = Path(args.plan)
    try:
        plan_text = plan_path.read_text()
        settings = parse_settings(plan_text)
        cases = parse_cases(plan_text)
    except OSError as error:
        print(f"Cannot read the test plan: {error}", file=sys.stderr)
        return 2
    except PlanError as error:
        print(f"Cannot use the test plan '{plan_path}': {error}", file=sys.stderr)
        return 2

    if args.only:
        cases = [case for case in cases if case.case_id.lower() == args.only.lower()]
        if not cases:
            print(f"No test case with id '{args.only}' in {plan_path}", file=sys.stderr)
            return 2

    if shutil.which("javac") is None or shutil.which("java") is None:
        print("javac/java not found on PATH. On macOS run: sdk use java 25.0.3.fx-zulu",
              file=sys.stderr)
        return 2

    work_dir = Path(tempfile.mkdtemp(prefix="ui-test-"))
    classes_dir = work_dir / "classes"
    try:
        print(f"{RULE}\nText UI test session\n{RULE}")
        print(f"Plan          : {plan_path}")
        print(f"Program       : {settings['Main class']} "
              f"(sources in {settings['Source directory']})")
        print(f"Data file     : {settings['Data file']} "
              f"(below each test case's own working directory)")
        print(f"Test cases    : {len(cases)}")

        status, javac_output = compile_program(settings["Source directory"], classes_dir)
        if status != 0:
            print("\nThe program did not compile, so no test case was run:\n")
            print(javac_output)
            return 2
        print("Compilation   : OK\n")

        data_file = settings["Data file"]
        for number, case in enumerate(cases, start=1):
            label = f"{case.case_id}: {case.title}" if case.title else case.case_id
            print(f"{RULE}\nTest case {number} of {len(cases)} - {label}\n{RULE}")
            print(f"Aim: {case.aim}\n")
            # A directory of its own per test case, thrown away with the rest of
            # the work directory at the end of the session.
            run_dir = work_dir / f"run-{case.case_id}"
            run_dir.mkdir(parents=True, exist_ok=True)
            if case.data_before is not None:
                print_block(f"Save file before ({data_file})", case.data_before)
            print(f"$ java -cp classes {settings['Main class']}")
            print_block("Console input (typed by the user)", case.input_text)
            actual, stderr_text, exit_code, timed_out = run_case(
                case, classes_dir, settings["Main class"], run_dir, data_file)
            print_block("Console output (printed by the program)", actual)
            if stderr_text:
                print_block("Error output", stderr_text)
            saved_text = read_data_file(run_dir, data_file)
            if case.data_after is not None:
                print_block(f"Save file after ({data_file})",
                            describe_data_file(saved_text))

            if timed_out:
                print(f"\nRESULT: FAIL - the program was still running after "
                      f"{TIMEOUT_SECONDS} seconds (it may be waiting for more input).")
                print("\nTest session terminated at the first failure.")
                return 1

            expected_lines = normalise(case.expected_text)
            actual_lines = normalise(actual)
            if expected_lines != actual_lines:
                print("\nRESULT: FAIL - the console output does not match the plan.\n")
                print_block("Expected output", case.expected_text)
                print_block("Actual output", actual)
                diff = difflib.unified_diff(expected_lines, actual_lines,
                                            fromfile="expected", tofile="actual",
                                            lineterm="")
                print_block("Difference (- expected, + actual)", "\n".join(diff))
                if exit_code not in (0, None):
                    print(f"\nThe program exited with status {exit_code}.")
                print("\nTest session terminated at the first failure. "
                      f"Later test cases in {plan_path} were not run.")
                return 1

            if case.data_after is not None and not data_file_matches(case.data_after,
                                                                     saved_text):
                print(f"\nRESULT: FAIL - {data_file} does not hold what the plan says.\n")
                print_block("Expected save file", case.data_after)
                print_block("Actual save file", describe_data_file(saved_text))
                diff = difflib.unified_diff(
                    normalise(case.data_after),
                    normalise(describe_data_file(saved_text)),
                    fromfile="expected", tofile="actual", lineterm="")
                print_block("Difference (- expected, + actual)", "\n".join(diff))
                print("\nTest session terminated at the first failure. "
                      f"Later test cases in {plan_path} were not run.")
                return 1

            print("\nRESULT: PASS\n")

        print(f"{RULE}")
        print(f"All {len(cases)} test case(s) passed.")
        print(RULE)
        return 0
    finally:
        shutil.rmtree(work_dir, ignore_errors=True)


if __name__ == "__main__":
    sys.exit(main())
