#!/usr/bin/env python3
"""Run the text-UI test cases described in test/ui-test-plan.md.

Each test case is a fresh run of the chatbot: the case's input lines are fed to
the program on standard input, and the program's console output is compared
against the case's expected output.

Running each case in its own process and its own throwaway working directory
keeps cases independent -- a case never inherits tasks added by an earlier one,
whether through memory or through the save file the chatbot writes to ./data, so
cases can be reordered or run singly (--filter) without changing their results.
A case that needs existing tasks adds them itself as part of its own input.

A case may give more than one **Input:** block. Each is a separate run of the
chatbot sharing that one directory, which is how a case shows that tasks saved
by one run are still there for the next.

Exit status is 0 when every case passes, 1 on the first failure (the run stops
there, so the reported failure is always the first thing that went wrong), and
2 for a setup problem such as a compile error.
"""

import argparse
import difflib
import re
import shutil
import subprocess
import sys
import tempfile
from pathlib import Path

# Root of the repository: this file lives at
# <root>/.claude/skills/test-ui/scripts/run-ui-tests.py
REPO_ROOT = Path(__file__).resolve().parents[4]

DEFAULT_PLAN = REPO_ROOT / "test" / "ui-test-plan.md"
SRC_DIR = REPO_ROOT / "src" / "main" / "java"
BIN_DIR = REPO_ROOT / "bin"
MAIN_CLASS = "thomas.Thomas"

# How long a single run of the chatbot may take before it is considered hung.
TIMEOUT_SECONDS = 20

# A fenced code block, e.g. ```text ... ```. DOTALL so the body may span lines.
FENCE_RE = re.compile(r"```[a-zA-Z]*\n(.*?)```", re.DOTALL)

# A {{NAME}} placeholder occupying a whole line of an expected-output block.
MACRO_RE = re.compile(r"^\{\{([A-Z0-9_]+)\}\}$")


class PlanError(Exception):
    """Raised when the test plan cannot be understood."""


def split_sections(markdown, level):
    """Splits markdown into (heading_text, body) pairs at the given ATX level.

    Only headings of exactly this level split the text, so a '###' case heading
    does not break up the '##' section that contains it.
    """
    pattern = re.compile(r"^#{%d} +(.*)$" % level, re.MULTILINE)
    matches = list(pattern.finditer(markdown))
    sections = []
    for i, match in enumerate(matches):
        end = matches[i + 1].start() if i + 1 < len(matches) else len(markdown)
        sections.append((match.group(1).strip(), markdown[match.end():end]))
    return sections


def first_fence_after(body, label):
    """Returns the first fenced code block appearing after `label` in `body`.

    Returns None when the label is absent, so the caller can report which part
    of a test case is missing.
    """
    position = body.find(label)
    if position == -1:
        return None
    match = FENCE_RE.search(body, position)
    return match.group(1) if match else None


def all_fences_after(body, label):
    """Returns the fenced block following every occurrence of `label`.

    A case with more than one **Input:** block is run once per block, in order,
    against the same data directory. That is what makes saving and loading
    testable: the chatbot has to be stopped and started again for a reload to
    happen at all, and a single run can never show it.
    """
    fences = []
    position = 0
    while True:
        found = body.find(label, position)
        if found == -1:
            return fences
        match = FENCE_RE.search(body, found)
        if match is None:
            return fences
        fences.append(match.group(1))
        position = match.end()


def display_path(path):
    """Shows a path relative to the repository when it is inside it.

    A plan given with --plan may sit anywhere, so falling back to the absolute
    path keeps that case from raising.
    """
    resolved = path.resolve()
    try:
        return str(resolved.relative_to(REPO_ROOT))
    except ValueError:
        return str(resolved)


def parse_aim(body):
    """Returns the prose following '**Aim:**', up to the next blank line.

    The aim is prose rather than a code block, so it is read line by line: an
    aim may wrap onto following lines, and the blank line before '**Input:**'
    ends it.
    """
    match = re.search(r"\*\*Aim:\*\*[ \t]*(.*)", body)
    if match is None:
        return ""

    lines = [match.group(1).strip()]
    for line in body[match.end():].splitlines()[1:]:
        if not line.strip():
            break
        lines.append(line.strip())
    return " ".join(part for part in lines if part)


def parse_plan(plan_path):
    """Parses the test plan into (macros, cases).

    macros maps a name such as GREETING to the lines it expands to. cases is a
    list of dicts with keys: name, aim, inputs (one entry per run), expected.
    """
    markdown = plan_path.read_text(encoding="utf-8")

    macros = {}
    cases = []

    for heading, body in split_sections(markdown, level=2):
        if heading.lower().startswith("shared output blocks"):
            for name, macro_body in split_sections(body, level=3):
                fence = FENCE_RE.search(macro_body)
                if fence is None:
                    raise PlanError(
                        f"Shared output block '{name}' has no fenced code block."
                    )
                macros[name.strip()] = fence.group(1)
        elif heading.lower().startswith("test cases"):
            for name, case_body in split_sections(body, level=3):
                aim = parse_aim(case_body)

                case_inputs = all_fences_after(case_body, "**Input:**")
                expected = first_fence_after(case_body, "**Expected output:**")
                if not case_inputs:
                    raise PlanError(f"Test case '{name}' has no **Input:** block.")
                if expected is None:
                    raise PlanError(
                        f"Test case '{name}' has no **Expected output:** block."
                    )
                cases.append(
                    {
                        "name": name,
                        "aim": aim,
                        "inputs": case_inputs,
                        "expected": expected,
                    }
                )

    if not cases:
        raise PlanError(
            f"No test cases found in {plan_path}. Cases live under a '## Test cases' "
            "heading, one '### ' heading each."
        )
    return macros, cases


def expand_macros(expected, macros, case_name):
    """Replaces whole-line {{NAME}} placeholders with the shared block's lines."""
    out = []
    for line in expected.splitlines():
        match = MACRO_RE.match(line.strip())
        if match:
            name = match.group(1)
            if name not in macros:
                raise PlanError(
                    f"Test case '{case_name}' uses {{{{{name}}}}}, which is not "
                    "defined under '## Shared output blocks'."
                )
            out.extend(macros[name].splitlines())
        else:
            out.append(line)
    return "\n".join(out)


def normalise(text):
    """Trims trailing spaces per line and trailing blank lines.

    Leading whitespace is preserved -- the chatbot's indentation is part of what
    these tests check.
    """
    lines = [line.rstrip() for line in text.splitlines()]
    while lines and lines[-1] == "":
        lines.pop()
    return lines


def compile_sources():
    """Compiles the Java sources into bin/, replacing any previous output."""
    javac = shutil.which("javac")
    if javac is None:
        print("SETUP ERROR: javac not found on PATH.", file=sys.stderr)
        print("Run 'sdk use java 25.0.3.fx-zulu' first.", file=sys.stderr)
        return False

    sources = sorted(str(p) for p in SRC_DIR.rglob("*.java"))
    if not sources:
        print(f"SETUP ERROR: no .java files under {SRC_DIR}.", file=sys.stderr)
        return False

    # Start from a clean bin/ so a deleted source file cannot linger as a class.
    if BIN_DIR.exists():
        shutil.rmtree(BIN_DIR)
    BIN_DIR.mkdir(parents=True)

    result = subprocess.run(
        [javac, "-d", str(BIN_DIR), *sources],
        capture_output=True,
        text=True,
    )
    if result.returncode != 0:
        print("SETUP ERROR: compilation failed.\n", file=sys.stderr)
        print(result.stdout + result.stderr, file=sys.stderr)
        return False
    if result.stderr.strip():
        # Warnings are worth seeing but are not a failure.
        print("javac warnings:\n" + result.stderr.rstrip() + "\n")
    return True


def run_case(case_inputs):
    """Runs the chatbot once per input block, and returns (output, exit_code).

    Every run happens in a throwaway working directory, so the save file the
    chatbot writes to ./data belongs to this case alone: cases stay independent
    now that tasks outlive a run, and the repository is not left holding a data
    file produced by a test.

    The runs of one case share that directory, which is what lets a later run
    see what an earlier one saved. Their outputs are concatenated, and the first
    non-zero exit status is the one reported.

    stderr is folded into the output so a stack trace shows up in the transcript
    where it happened.
    """
    outputs = []
    with tempfile.TemporaryDirectory(prefix="thomas-ui-test-") as workdir:
        for case_input in case_inputs:
            stdin_text = (
                case_input if case_input.endswith("\n") else case_input + "\n"
            )
            try:
                result = subprocess.run(
                    # -ea: the assertions in the source are off by default, so
                    # without it these cases exercise the code with every
                    # stated assumption unchecked.
                    ["java", "-ea", "-cp", str(BIN_DIR), MAIN_CLASS],
                    input=stdin_text,
                    capture_output=True,
                    text=True,
                    timeout=TIMEOUT_SECONDS,
                    cwd=workdir,
                )
            except subprocess.TimeoutExpired:
                outputs.append(
                    f"<<< the program did not exit within {TIMEOUT_SECONDS}s >>>"
                )
                return "".join(outputs), None
            outputs.append(result.stdout + result.stderr)
            if result.returncode != 0:
                return "".join(outputs), result.returncode
    return "".join(outputs), 0


def print_transcript(case, actual):
    """Prints one case's console session: what was typed, and what came back."""
    print(f"--- {case['name']} ---")
    if case["aim"]:
        print(f"Aim: {case['aim']}")
    print()
    for number, case_input in enumerate(case["inputs"], start=1):
        # Numbered only when there is more than one, so the ordinary
        # single-run case reads exactly as it did before.
        if len(case["inputs"]) > 1:
            print(f"Console input (run {number} of {len(case['inputs'])}):")
        else:
            print("Console input:")
        for line in case_input.splitlines():
            print(f"  > {line}")
        print()
    print("Console output:")
    for line in actual.splitlines():
        print(f"  | {line}")
    print()


def print_failure(case, expected_lines, actual_lines, exit_code):
    """Reports a failed case: expected, actual, and a line-by-line diff."""
    print("=" * 72)
    print(f"FAILED: {case['name']}")
    if case["aim"]:
        print(f"Aim: {case['aim']}")
    print("=" * 72)

    print("\nExpected output:")
    for line in expected_lines:
        print(f"  | {line}")

    print("\nActual output:")
    for line in actual_lines:
        print(f"  | {line}")

    print("\nDifference (- expected, + actual):")
    diff = difflib.unified_diff(
        expected_lines, actual_lines, "expected", "actual", lineterm="", n=3
    )
    for line in diff:
        print(f"  {line}")

    if exit_code is None:
        print("\nThe program had to be killed: it never exited.")
    elif exit_code != 0:
        print(f"\nThe program exited with status {exit_code}.")
    print()


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument(
        "--plan",
        type=Path,
        default=DEFAULT_PLAN,
        help="path to the test plan (default: test/ui-test-plan.md)",
    )
    parser.add_argument(
        "--filter",
        default=None,
        help="run only cases whose heading contains this text (case-insensitive)",
    )
    args = parser.parse_args()

    if not args.plan.exists():
        print(f"SETUP ERROR: test plan not found at {args.plan}.", file=sys.stderr)
        return 2

    try:
        macros, cases = parse_plan(args.plan)
    except PlanError as error:
        print(f"SETUP ERROR: {error}", file=sys.stderr)
        return 2

    if args.filter:
        needle = args.filter.lower()
        cases = [c for c in cases if needle in c["name"].lower()]
        if not cases:
            print(f"SETUP ERROR: no test case matches '{args.filter}'.", file=sys.stderr)
            return 2

    if not compile_sources():
        return 2

    print(f"Test plan: {display_path(args.plan)}")
    print(f"Running {len(cases)} test case(s) against {MAIN_CLASS}.\n")

    for index, case in enumerate(cases, start=1):
        try:
            expected = expand_macros(case["expected"], macros, case["name"])
        except PlanError as error:
            print(f"SETUP ERROR: {error}", file=sys.stderr)
            return 2

        actual, exit_code = run_case(case["inputs"])
        print_transcript(case, actual)

        expected_lines = normalise(expected)
        actual_lines = normalise(actual)

        if expected_lines != actual_lines or exit_code not in (0, None):
            print_failure(case, expected_lines, actual_lines, exit_code)
            remaining = len(cases) - index
            print(
                f"Session stopped at {case['name']}. "
                f"{index - 1} passed, 1 failed, {remaining} not run."
            )
            return 1

        print(f"PASSED: {case['name']}\n")

    print("=" * 72)
    print(f"All {len(cases)} test case(s) passed.")
    return 0


if __name__ == "__main__":
    sys.exit(main())
