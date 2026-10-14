#!/usr/bin/env python3
"""Runs the UI test cases recorded in a test plan against the compiled program.

Test plan format (Markdown), one block per test case:

    ## TC<id>: <title>
    **Aim:** <what this test case verifies>

    | Input | Expected Output |
    |---|---|
    | `command1` | `text that must appear in the output produced after command1` |
    | `command2` | `more text\nsecond line` |

Each row is one line of input sent to the program's console, paired with a
snippet of text that must appear in the program's console output at or after
that point in the session (a substring / "contains" check, not an exact
match, so cosmetic changes like divider lines don't break tests). Use the
literal characters "\\n" inside a cell to require a newline in the expected
text. All rows in one test case are sent to a single fresh run of the
program, in order, so later rows can check the effect of earlier commands
(e.g. "add a task" then "list").

The whole test session stops at the first failing test case: no further test
cases are run, and the actual vs. expected output is reported.
"""
import argparse
import glob
import os
import re
import shutil
import subprocess
import sys

# On Windows, stdout/stderr default to the console's codepage (e.g. cp1252),
# which can't represent every character the program under test might print
# (or that a test plan's Markdown prose might contain). Reconfigure to UTF-8
# with substitution instead of crashing mid-report.
for _stream in (sys.stdout, sys.stderr):
    if hasattr(_stream, "reconfigure"):
        _stream.reconfigure(encoding="utf-8", errors="replace")


class TestCase:
    def __init__(self, tc_id, title):
        self.tc_id = tc_id
        self.title = title
        self.aim = ""
        self.steps = []  # list of (input, expected_output)


def parse_plan(plan_path):
    with open(plan_path, "r", encoding="utf-8") as f:
        lines = f.read().splitlines()

    test_cases = []
    current = None
    in_table = False
    header_seen = False
    separator_seen = False
    in_fence = False

    def unescape(cell):
        cell = cell.strip()
        if cell.startswith("`") and cell.endswith("`") and len(cell) >= 2:
            cell = cell[1:-1]
        return cell.replace("\\n", "\n")

    for raw_line in lines:
        line = raw_line.rstrip("\n")

        if line.strip().startswith("```"):
            in_fence = not in_fence
            continue
        if in_fence:
            continue

        heading = re.match(r"^##\s+TC(\S+):\s*(.+)$", line.strip())
        if heading:
            if current is not None:
                test_cases.append(current)
            current = TestCase(heading.group(1), heading.group(2).strip())
            in_table = False
            header_seen = False
            separator_seen = False
            continue

        if current is None:
            continue

        aim_match = re.match(r"^\*\*Aim:\*\*\s*(.+)$", line.strip())
        if aim_match:
            current.aim = aim_match.group(1).strip()
            continue

        stripped = line.strip()
        if stripped.startswith("|"):
            if not header_seen:
                header_seen = True
                in_table = True
                continue
            if not separator_seen:
                separator_seen = True
                continue
            cells = stripped.strip("|").split("|")
            if len(cells) >= 2:
                cmd = unescape(cells[0])
                expected = unescape(cells[1])
                if cmd != "":
                    current.steps.append((cmd, expected))
        else:
            in_table = False
            header_seen = False
            separator_seen = False

    if current is not None:
        test_cases.append(current)

    return test_cases


def compile_project(javac, source_dir, classes_dir):
    os.makedirs(classes_dir, exist_ok=True)
    sources = sorted(glob.glob(os.path.join(source_dir, "**", "*.java"), recursive=True))
    if not sources:
        print(f"No .java files found under {source_dir}", file=sys.stderr)
        return False
    result = subprocess.run(
        [javac, "-d", classes_dir] + sources,
        capture_output=True, text=True, encoding="utf-8", errors="replace",
    )
    if result.returncode != 0:
        print("Compilation failed:", file=sys.stderr)
        print(result.stderr, file=sys.stderr)
        return False
    return True


def run_session(java, classes_dir, main_class, inputs, timeout):
    stdin_lines = list(inputs)
    if not stdin_lines or stdin_lines[-1].strip().lower() != "bye":
        stdin_lines.append("bye")
    stdin_text = "\n".join(stdin_lines) + "\n"
    try:
        result = subprocess.run(
            [java,
             "-Dstdout.encoding=UTF-8", "-Dstderr.encoding=UTF-8", "-Dstdin.encoding=UTF-8",
             "-cp", classes_dir, main_class],
            input=stdin_text,
            capture_output=True, text=True, encoding="utf-8", errors="replace",
            timeout=timeout,
        )
        return stdin_lines, result.stdout, result.stderr, False
    except subprocess.TimeoutExpired as e:
        stdout = e.stdout or ""
        stderr = e.stderr or ""
        return stdin_lines, stdout, stderr, True


def main():
    parser = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
    parser.add_argument("plan", nargs="?", default="test/ui-test-plan.md")
    parser.add_argument("--main-class", default="verity.Verity")
    parser.add_argument("--source-dir", default="src/main/java")
    parser.add_argument("--classes-dir", default="out/production/ip")
    parser.add_argument("--data-dir", default="data",
                         help="Directory the app saves task data to; wiped before each "
                              "test case so saved state from one case can't leak into the next.")
    parser.add_argument("--java", default="java")
    parser.add_argument("--javac", default="javac")
    parser.add_argument("--skip-compile", action="store_true")
    parser.add_argument("--timeout", type=float, default=15.0)
    args = parser.parse_args()

    if not os.path.isfile(args.plan):
        print(f"Test plan not found: {args.plan}", file=sys.stderr)
        sys.exit(2)

    test_cases = parse_plan(args.plan)
    if not test_cases:
        print(f"No test cases found in {args.plan}", file=sys.stderr)
        sys.exit(2)

    if not args.skip_compile:
        print(f"Compiling {args.source_dir} -> {args.classes_dir} ...")
        if not compile_project(args.javac, args.source_dir, args.classes_dir):
            sys.exit(1)
        print("Compilation succeeded.\n")

    passed = 0
    for tc in test_cases:
        bar = "-" * 60
        print(bar)
        print(f"TC{tc.tc_id}: {tc.title}")
        if tc.aim:
            print(f"Aim: {tc.aim}")
        print(bar)

        if not tc.steps:
            print("  (no input/expected-output rows found for this test case; skipped)\n")
            continue

        inputs = [cmd for cmd, _ in tc.steps]
        print(">> Console session (stdin sent, in order):")
        for line in inputs:
            print(f"    {line}")

        # Each test case must start from a blank slate. The app now persists tasks to
        # disk (Level 7), so without this a test case would silently inherit tasks
        # saved by an earlier one, even though it runs in its own fresh OS process.
        if os.path.isdir(args.data_dir):
            shutil.rmtree(args.data_dir)

        stdin_lines, stdout, stderr, timed_out = run_session(
            args.java, args.classes_dir, args.main_class, inputs, args.timeout,
        )

        print("\n>> Captured console output:")
        print(stdout if stdout else "(empty)")
        if stderr.strip():
            print(">> Captured console error output:")
            print(stderr)
        if timed_out:
            print(f"\n[FAIL] Program did not finish within {args.timeout}s.")
            sys.exit(1)

        search_from = 0
        for cmd, expected in tc.steps:
            idx = stdout.find(expected, search_from)
            if idx == -1:
                print(f"\n[FAIL] TC{tc.tc_id} ({tc.title}) - input `{cmd}`")
                print("Expected output (must appear in console output at/after this point):")
                print(f'"""\n{expected}\n"""')
                print("Actual console output for this test case:")
                print(f'"""\n{stdout}\n"""')
                print(bar)
                print("Test session terminated: a test case failed.")
                sys.exit(1)
            search_from = idx + len(expected)
            print(f"  [PASS] `{cmd}` -> found expected output.")

        passed += 1
        print()

    print(f"All {passed} test case(s) passed.")


if __name__ == "__main__":
    main()
