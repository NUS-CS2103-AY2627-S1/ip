#!/usr/bin/env python3
"""Run the UI test cases recorded in test/ui-test-plan.md against the chatbot.

Each test case feeds a list of commands to the program on stdin and compares
the console output against the expected output. The run stops at the first
failure and reports the actual and expected output for that case.
"""

import argparse
import re
import shlex
import subprocess
import sys
from pathlib import Path

FENCE = re.compile(r"```(?:[\w-]+)?\n(.*?)```", re.S)


def sections(text, level):
    """Splits markdown into (heading, body) pairs at the given heading level."""
    heading = re.compile(rf"^{'#' * level} +(.*)$", re.M)
    found = list(heading.finditer(text))
    result = []
    for i, match in enumerate(found):
        end = found[i + 1].start() if i + 1 < len(found) else len(text)
        result.append((match.group(1).strip(), text[match.end():end]))
    return result


def labeled_block(body, label):
    """Returns the first fenced code block appearing after `label` in `body`."""
    start = body.find(label)
    if start == -1:
        return None
    match = FENCE.search(body, start)
    return match.group(1) if match else None


def normalize(text):
    """Splits into lines, dropping trailing whitespace and trailing blank lines.

    Trailing spaces are invisible in a terminal, so treating them as
    differences would only produce confusing failures.
    """
    lines = [line.rstrip() for line in text.replace("\r\n", "\n").split("\n")]
    while lines and not lines[-1]:
        lines.pop()
    return lines


def parse_plan(path):
    text = path.read_text(encoding="utf-8")

    preamble = ""
    for heading, body in sections(text, 2):
        if heading.lower().startswith("session preamble"):
            block = FENCE.search(body)
            preamble = block.group(1) if block else ""
            break

    cases = []
    for heading, body in sections(text, 3):
        commands = labeled_block(body, "**Input:**")
        expected = labeled_block(body, "**Expected output:**")
        if commands is None or expected is None:
            continue
        aim = ""
        aim_match = re.search(r"\*\*Aim:\*\*\s*(.+)", body)
        if aim_match:
            aim = aim_match.group(1).strip()
        cases.append({
            "name": heading,
            "aim": aim,
            "setup": labeled_block(body, "**Setup input:**"),
            "setup_data": labeled_block(body, "**Setup data file:**"),
            "input": commands,
            "expected": preamble + expected,
        })
    return cases


def report_failure(case, expected, actual):
    print(f"\nFAILED: {case['name']}")
    if case["aim"]:
        print(f"Aim: {case['aim']}")

    for i in range(max(len(expected), len(actual))):
        want = expected[i] if i < len(expected) else "<no more output>"
        got = actual[i] if i < len(actual) else "<no more output>"
        if want != got:
            print(f"\nFirst difference at line {i + 1}:")
            print(f"  expected: {want!r}")
            print(f"  actual  : {got!r}")
            break

    print("\n--- expected output ---")
    print("\n".join(expected))
    print("\n--- actual output ---")
    print("\n".join(actual))


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--plan", default="test/ui-test-plan.md",
                        help="path to the test plan (default: test/ui-test-plan.md)")
    parser.add_argument("--cmd", default="java src/main/java/sallman/Sallman.java",
                        help="command that starts the chatbot")
    parser.add_argument("--filter", default=None,
                        help="only run test cases whose heading contains this text")
    parser.add_argument("--data", default="_temp/ui-test-data.txt",
                        help="scratch data file passed to the app, deleted before each "
                             "case so cases cannot leak saved tasks into each other "
                             "(default: _temp/ui-test-data.txt)")
    args = parser.parse_args()

    plan_path = Path(args.plan)
    if not plan_path.exists():
        print(f"Test plan not found: {plan_path}", file=sys.stderr)
        return 2

    cases = parse_plan(plan_path)
    if args.filter:
        cases = [c for c in cases if args.filter.lower() in c["name"].lower()]
    if not cases:
        print("No test cases found.", file=sys.stderr)
        return 2

    # The app saves its task list, so each case starts from a clean scratch
    # file. Never point --data at the real task list: it gets deleted.
    data_file = Path(args.data)
    data_file.parent.mkdir(parents=True, exist_ok=True)
    command = shlex.split(args.cmd) + [str(data_file)]
    print(f"Running {len(cases)} test case(s) with: {args.cmd} {data_file}")
    print()

    for case in cases:
        data_file.unlink(missing_ok=True)
        if case["setup_data"] is not None:
            # Raw file content, for cases that need data the app itself could
            # not produce, such as a hand-corrupted save file.
            data_file.write_text(case["setup_data"], encoding="utf-8")
        if case["setup"]:
            # An earlier session that seeds the data file. Its output is not
            # checked; it exists so the case under test starts from saved data.
            subprocess.run(command, input=case["setup"], text=True,
                           capture_output=True, timeout=60)
        try:
            process = subprocess.run(command, input=case["input"], text=True,
                                     capture_output=True, timeout=60)
        except subprocess.TimeoutExpired:
            print(f"\nFAILED: {case['name']}\nThe program did not exit within 60s. "
                  f"Does the test input end with 'bye'?")
            return 1

        actual_text = process.stdout
        if process.returncode != 0:
            actual_text += process.stderr

        expected = normalize(case["expected"])
        actual = normalize(actual_text)

        print(f"=== {case['name']} ===")
        if case["aim"]:
            print(f"Aim: {case['aim']}")
        print("--- console session ---")
        print(f"[stdin]\n{case['input'].rstrip()}")
        print(f"[stdout]\n{actual_text.rstrip()}")

        if expected != actual:
            report_failure(case, expected, actual)
            print(f"\nStopped at the first failure. "
                  f"{len(cases)} case(s) in the plan.")
            return 1

        print("PASSED\n")

    print(f"All {len(cases)} test case(s) passed.")
    return 0


if __name__ == "__main__":
    sys.exit(main())
