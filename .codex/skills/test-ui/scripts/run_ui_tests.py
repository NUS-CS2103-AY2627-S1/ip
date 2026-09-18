#!/usr/bin/env python3
"""
Run the text-UI test cases recorded in a Markdown test plan.

    python3 run_ui_tests.py [--plan test/ui-test-plan.md] [--case TC3]

For every test case the script starts a *fresh* run of the program, feeds the
case's input lines to standard input, and compares everything the program
printed against the expected output recorded in the plan. The console session
is printed as it goes, so the reader can see exactly what was typed and what
came back.

The first failing test case stops the whole session: the script prints the
expected output, the actual output, and a line-by-line difference, then exits
with status 1. Later test cases are not run, because once the program is
misbehaving their results cannot be trusted.

Only Python's standard library is used, so there is nothing to install.
"""
from __future__ import annotations

import argparse
import difflib
import re
import shutil
import subprocess
import sys
import tempfile
from pathlib import Path

# A fenced code block opens and closes with three backticks, optionally
# followed by a language name such as ```text.
FENCE = re.compile(r"^```[A-Za-z0-9_+-]*\s*$")

RULE = "=" * 72
THIN = "-" * 72


# --- reading the test plan ---------------------------------------------------


def parse_plan(path: Path) -> tuple[str, list[dict]]:
    """
    Read the Markdown plan and return its greeting block and its test cases.

    A section is any `## ` heading. The section titled `Greeting` holds the
    fixed banner the program prints on every run; it is prepended to each
    case's expected output so the plan does not have to repeat it. Every other
    section that contains an `**Input:**` block is treated as a test case.
    """
    lines = path.read_text(encoding="utf-8").splitlines()
    sections: list[dict] = []
    current: dict | None = None
    pending_label: str | None = None
    collecting_aim = False

    index = 0
    while index < len(lines):
        line = lines[index]

        if line.startswith("## "):
            current = {"title": line[3:].strip(), "aim": None, "input": None,
                       "expected": None, "first_block": None}
            sections.append(current)
            pending_label = None
            collecting_aim = False

        elif current is not None:
            stripped = line.strip()
            lowered = stripped.lower()

            # An aim may be wrapped over several lines; it ends at the first
            # blank line or at the next bold label.
            if collecting_aim and stripped and not stripped.startswith("**"):
                current["aim"] = f"{current['aim']} {stripped}".strip()
                index += 1
                continue
            collecting_aim = False

            if lowered.startswith("**aim:**"):
                current["aim"] = stripped[len("**Aim:**"):].strip()
                collecting_aim = True
            elif lowered.startswith("**input"):
                pending_label = "input"
            elif lowered.startswith("**expected"):
                pending_label = "expected"
            elif FENCE.match(line):
                block, index = read_fenced_block(lines, index)
                if current["first_block"] is None:
                    current["first_block"] = block
                if pending_label is not None:
                    current[pending_label] = block
                pending_label = None

        index += 1

    greeting = ""
    for section in sections:
        if section["title"].lower().startswith("greeting"):
            greeting = section["first_block"] or ""
            break

    cases = [section for section in sections if section["input"] is not None]
    for case in cases:
        # The identifier is the first word of the heading, e.g. "TC3" in
        # "## TC3 - Mark a task as done".
        case["id"] = case["title"].split()[0].rstrip(":-")
        if case["expected"] is None:
            fail(f"Test case {case['id']} has no '**Expected output:**' block.")

    return greeting, cases


def read_fenced_block(lines: list[str], fence_index: int) -> tuple[str, int]:
    """Return the text inside the fence starting at `fence_index`, and its end."""
    body: list[str] = []
    index = fence_index + 1
    while index < len(lines) and not FENCE.match(lines[index]):
        body.append(lines[index])
        index += 1
    return "\n".join(body), index


# --- compiling and running the program ---------------------------------------


def compile_sources(source_dir: Path, class_dir: Path) -> None:
    """Compile every Java file under `source_dir` into `class_dir`."""
    sources = sorted(str(p) for p in source_dir.rglob("*.java"))
    if not sources:
        fail(f"No .java files found under {source_dir}.")

    print(f"Compiling {len(sources)} source file(s) from {source_dir} ...")
    result = subprocess.run(["javac", "-d", str(class_dir), *sources],
                            capture_output=True, text=True)
    if result.returncode != 0:
        print(THIN)
        print(result.stderr.rstrip())
        print(THIN)
        fail("Compilation failed, so no test case could be run.")
    print("Compilation succeeded.\n")


def run_case(class_dir: Path, main_class: str, stdin_text: str,
             timeout: int, work_dir: Path) -> subprocess.CompletedProcess:
    """
    Start the program, feed it `stdin_text`, and capture what it prints.

    The program runs in `work_dir` so that any file it saves lands in a
    throwaway folder, rather than in the repository's own data folder.
    """
    return subprocess.run(
        # -ea so the assertions in the code are live during these tests too;
        # the JVM disables them by default.
        ["java", "-ea", "-cp", str(class_dir), main_class],
        input=stdin_text + "\n" if stdin_text else "\n",
        capture_output=True, text=True, timeout=timeout,
        cwd=str(work_dir),
    )


# --- comparing and reporting -------------------------------------------------


def normalise(text: str) -> str:
    """
    Ignore differences that a reader could not see: trailing spaces at the end
    of a line, and blank lines at the very end of the output.
    """
    return "\n".join(line.rstrip() for line in text.splitlines()).rstrip("\n")


def print_transcript(case: dict, stdin_text: str, result) -> None:
    """Show the console session for one test case."""
    print(RULE)
    print(case["title"])
    if case["aim"]:
        print(f"Aim: {case['aim']}")
    print(RULE)
    print("--- console input (typed into the program) ---")
    for line in stdin_text.splitlines():
        print(f"> {line}")
    print("--- console output (printed by the program) ---")
    print(result.stdout.rstrip("\n"))
    if result.stderr.strip():
        print("--- standard error (the program printed a problem) ---")
        print(result.stderr.rstrip("\n"))
    print()


def report_failure(case: dict, expected: str, actual: str, result) -> None:
    """Explain a mismatch, then stop the whole session."""
    print(RULE)
    print(f"FAILED: {case['title']}")
    if case["aim"]:
        print(f"Aim: {case['aim']}")
    print(RULE)
    print("--- expected output ---")
    print(expected)
    print("--- actual output ---")
    print(actual)
    print("--- difference (- expected, + actual) ---")
    diff = difflib.unified_diff(expected.splitlines(), actual.splitlines(),
                                fromfile="expected", tofile="actual", lineterm="")
    for line in diff:
        print(line)
    if result is not None and result.stderr.strip():
        print("--- standard error ---")
        print(result.stderr.rstrip("\n"))
    print(RULE)
    print("Test session terminated at the first failure. "
          "Remaining test cases were not run.")
    sys.exit(1)


def fail(message: str) -> None:
    print(f"ERROR: {message}", file=sys.stderr)
    sys.exit(1)


# --- entry point -------------------------------------------------------------


def main() -> None:
    parser = argparse.ArgumentParser(description="Run the text-UI test plan.")
    parser.add_argument("--plan", default="test/ui-test-plan.md",
                        help="Markdown file holding the test cases.")
    parser.add_argument("--src", default="src/main/java",
                        help="Folder containing the Java sources.")
    parser.add_argument("--main-class", default="kenbot.Kenbot",
                        help="Class whose main method starts the program.")
    parser.add_argument("--case", action="append", default=None, metavar="ID",
                        help="Run only this test case; repeat to run several.")
    parser.add_argument("--timeout", type=int, default=10,
                        help="Seconds to wait before treating a run as hung.")
    args = parser.parse_args()

    plan_path = Path(args.plan)
    if not plan_path.is_file():
        fail(f"Test plan not found: {plan_path}")

    greeting, cases = parse_plan(plan_path)
    if args.case:
        wanted = {identifier.upper() for identifier in args.case}
        cases = [case for case in cases if case["id"].upper() in wanted]
        if not cases:
            fail(f"No test case in {plan_path} matched {sorted(wanted)}.")
    if not cases:
        fail(f"No test cases found in {plan_path}.")

    print(RULE)
    print(f"Text-UI test session - {plan_path}")
    print(f"{len(cases)} test case(s) to run")
    print(RULE + "\n")

    class_dir = Path(tempfile.mkdtemp(prefix="ui-test-classes-"))
    sandbox = Path(tempfile.mkdtemp(prefix="ui-test-sandbox-"))
    try:
        compile_sources(Path(args.src), class_dir)

        for case in cases:
            # Each case runs in an empty folder, so a file saved by one case can
            # never be seen by the next, and the repository's own data folder is
            # left untouched.
            shutil.rmtree(sandbox, ignore_errors=True)
            sandbox.mkdir(parents=True, exist_ok=True)

            stdin_text = case["input"]
            try:
                result = run_case(class_dir, args.main_class, stdin_text,
                                  args.timeout, sandbox)
            except subprocess.TimeoutExpired:
                print_transcript(case, stdin_text,
                                 subprocess.CompletedProcess([], 0, "", ""))
                report_failure(case, normalise(join_expected(greeting, case)),
                               f"(no output: the program was still running after "
                               f"{args.timeout} seconds)", None)
                return

            print_transcript(case, stdin_text, result)

            expected = normalise(join_expected(greeting, case))
            actual = normalise(result.stdout)
            if expected != actual:
                report_failure(case, expected, actual, result)
            print(f"PASSED: {case['id']}\n")

        print(RULE)
        print(f"All {len(cases)} test case(s) passed.")
        print(RULE)
    finally:
        shutil.rmtree(class_dir, ignore_errors=True)
        shutil.rmtree(sandbox, ignore_errors=True)


def join_expected(greeting: str, case: dict) -> str:
    """Put the shared greeting in front of a case's own expected output."""
    if not greeting:
        return case["expected"]
    return greeting + "\n" + case["expected"]


if __name__ == "__main__":
    main()
