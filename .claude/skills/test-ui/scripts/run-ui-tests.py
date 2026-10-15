#!/usr/bin/env python3
"""Run the text UI test cases recorded in a Markdown test plan.

Compiles the project, then for each test case feeds the case's input lines to a
fresh run of the program and compares the console output against the expected
output. Prints a transcript of every session. Stops at the first failure and
reports the expected and actual output.

Each case runs in a working directory of its own, so the data file one case
leaves behind can never reach the next one. Within a case, a `--- restart ---`
line in the input ends the run and starts another one in the same directory,
which is how saving and loading are tested.

Only the Python standard library is used, so there is no install step.
"""

from __future__ import annotations

import argparse
import difflib
import glob
import re
import shutil
import subprocess
import sys
import tempfile
from dataclasses import dataclass, field
from pathlib import Path

DEFAULT_PLAN = "test/ui-test-plan.md"
DEFAULT_MAIN_CLASS = "billy.Billy"
DEFAULT_SOURCE_GLOB = "src/main/java/**/*.java"
DEFAULT_TIMEOUT_SECONDS = 10
DEFAULT_DATA_FILE = "data/billy.txt"

# An input line of exactly this ends one run of the program and starts another in
# the same working directory, so a case can check what survives a restart.
RESTART_MARKER = "--- restart ---"

RULE = "=" * 72
THIN_RULE = "-" * 72


@dataclass
class TestCase:
    """One test case parsed from the plan."""

    title: str
    aim: str = ""
    inputs: list[str] = field(default_factory=list)
    expected: list[str] = field(default_factory=list)
    line_number: int = 0
    # Contents to plant in the data file before the case runs, or None to start
    # with no data file at all.
    data_file: list[str] | None = None


class PlanError(Exception):
    """Raised when the test plan cannot be understood."""


def parse_front_matter(lines: list[str]) -> tuple[dict[str, str], int]:
    """Reads optional `key: value` front matter delimited by `---` lines.

    Returns the settings and the index of the first line after the front matter.
    """
    if not lines or lines[0].strip() != "---":
        return {}, 0

    settings: dict[str, str] = {}
    for index in range(1, len(lines)):
        stripped = lines[index].strip()
        if stripped == "---":
            return settings, index + 1
        if not stripped or stripped.startswith("#"):
            continue
        if ":" not in stripped:
            raise PlanError(f"line {index + 1}: front matter needs `key: value`, got {stripped!r}")
        key, value = stripped.split(":", 1)
        settings[key.strip()] = value.strip().strip("`")
    raise PlanError("front matter opened with `---` but was never closed")


def read_fenced_block(lines: list[str], start: int) -> tuple[list[str], int]:
    """Reads the next ``` fenced block at or after `start`.

    Returns the block's lines and the index of the line after its closing fence.
    """
    index = start
    while index < len(lines) and not lines[index].lstrip().startswith("```"):
        if lines[index].strip().startswith("## "):
            raise PlanError(f"line {start + 1}: expected a fenced block before the next heading")
        index += 1
    if index >= len(lines):
        raise PlanError(f"line {start + 1}: expected a fenced ``` block but reached end of file")

    block: list[str] = []
    index += 1  # step past the opening fence
    while index < len(lines):
        if lines[index].lstrip().startswith("```"):
            return block, index + 1
        block.append(lines[index])
        index += 1
    raise PlanError(f"line {start + 1}: fenced block was never closed")


def expand_snippets(
    block: list[str], snippets: dict[str, list[str]], where: str
) -> list[str]:
    """Replaces any line that is just `{{NAME}}` with the named snippet's lines.

    Lets shared output such as the greeting be written once rather than repeated
    in every case, so rewording it is a one-place change.
    """
    expanded: list[str] = []
    for line in block:
        marker = re.match(r"^\s*\{\{([A-Za-z0-9_]+)\}\}\s*$", line)
        if not marker:
            expanded.append(line)
            continue
        name = marker.group(1)
        if name not in snippets:
            known = ", ".join(sorted(snippets)) or "none defined"
            raise PlanError(f"{where}: unknown snippet {{{{{name}}}}} (known: {known})")
        expanded.extend(snippets[name])
    return expanded


def parse_plan(path: Path) -> tuple[dict[str, str], list[TestCase]]:
    """Parses the test plan into settings and an ordered list of test cases."""
    if not path.is_file():
        raise PlanError(f"test plan not found: {path}")

    lines = path.read_text(encoding="utf-8").splitlines()
    settings, index = parse_front_matter(lines)

    cases: list[TestCase] = []
    snippets: dict[str, list[str]] = {}
    current: TestCase | None = None

    while index < len(lines):
        line = lines[index]
        stripped = line.strip()

        if stripped.startswith("## "):
            heading = stripped[3:].strip()

            # A snippet section defines reusable output rather than a test case.
            snippet = re.match(r"^Snippet:\s*([A-Za-z0-9_]+)$", heading)
            if snippet:
                snippets[snippet.group(1)], index = read_fenced_block(lines, index + 1)
                current = None
                continue

            # Only headings that look like test cases start a case.
            if re.match(r"^TC[-_ ]?\d+", heading, re.IGNORECASE):
                current = TestCase(title=heading, line_number=index + 1)
                cases.append(current)
            else:
                current = None
            index += 1
            continue

        if current is not None:
            label = re.match(
                r"^\*\*(Aim|Input|Expected output|Data file)[:s]*\*\*:?\s*(.*)$", stripped
            )
            if label:
                name = label.group(1).lower()
                if name == "aim":
                    current.aim = label.group(2).strip()
                    index += 1
                    continue
                block, index = read_fenced_block(lines, index + 1)
                if name == "input":
                    current.inputs = block
                elif name == "data file":
                    current.data_file = block
                else:
                    current.expected = block
                continue

        index += 1

    for case in cases:
        case.expected = expand_snippets(case.expected, snippets, case.title)
        if not case.inputs:
            raise PlanError(f"{case.title} (line {case.line_number}): missing an **Input:** block")
        if not case.expected:
            raise PlanError(
                f"{case.title} (line {case.line_number}): missing an **Expected output:** block"
            )

    if not cases:
        raise PlanError(f"no test cases found in {path} (headings must look like `## TC-01 ...`)")
    return settings, cases


def compile_sources(
    repo: Path, source_glob: str, classes_dir: Path, exclude_glob: str | None = None
) -> None:
    """Compiles the project's sources, raising SystemExit on any compile error."""
    # recursive=True is what makes a `**` in the glob span nested folders rather
    # than a single level, so sources organised into packages are all found.
    sources = sorted(glob.glob(str(repo / source_glob), recursive=True))

    # The GUI sources need JavaFX on the classpath, which plain javac does not
    # have. They are left out rather than compiled, since the text UI never uses
    # them: nothing the console conversation touches refers to the window.
    if exclude_glob:
        excluded = set(glob.glob(str(repo / exclude_glob), recursive=True))
        sources = [s for s in sources if s not in excluded]
        if excluded:
            print(f"Excluded {len(excluded)} source file(s) matching {exclude_glob!r}.")

    if not sources:
        fail(f"no source files matched {source_glob!r} under {repo}")

    classes_dir.mkdir(parents=True, exist_ok=True)
    result = subprocess.run(
        ["javac", "-d", str(classes_dir), *sources],
        capture_output=True,
        text=True,
    )
    if result.returncode != 0:
        print(f"COMPILATION FAILED\n\n{result.stdout}{result.stderr}", file=sys.stderr)
        raise SystemExit(1)
    print(f"Compiled {len(sources)} source file(s) with javac.\n")


def normalise(lines: list[str]) -> list[str]:
    """Removes trailing whitespace per line and any trailing blank lines.

    Keeps comparisons from failing over invisible differences that no user
    would ever notice on screen.
    """
    cleaned = [line.rstrip() for line in lines]
    while cleaned and not cleaned[-1]:
        cleaned.pop()
    return cleaned


def split_sessions(inputs: list[str]) -> list[list[str]]:
    """Splits a case's input into one list of lines per run of the program."""
    sessions: list[list[str]] = [[]]
    for line in inputs:
        if line.strip() == RESTART_MARKER:
            sessions.append([])
        else:
            sessions[-1].append(line)
    return sessions


def prepare_work_dir(case: TestCase, work_dir: Path, data_file: str) -> None:
    """Creates the case's own working directory and plants its data file, if any.

    A directory per case keeps the file one case saves from being loaded by the
    next, which is what lets every case still claim to start from scratch.
    """
    work_dir.mkdir(parents=True, exist_ok=True)
    if case.data_file is None:
        return
    target = work_dir / data_file
    target.parent.mkdir(parents=True, exist_ok=True)
    target.write_text("".join(line + "\n" for line in case.data_file), encoding="utf-8")


def run_case(
    case: TestCase,
    classes_dir: Path,
    main_class: str,
    timeout: int,
    work_dir: Path,
    data_file: str,
) -> list[str]:
    """Runs the program once per session in the case, all in the same directory."""
    prepare_work_dir(case, work_dir, data_file)

    output: list[str] = []
    for session in split_sessions(case.inputs):
        stdin_text = "".join(line + "\n" for line in session)
        try:
            result = subprocess.run(
                ["java", "-ea", "-cp", str(classes_dir), main_class],
                input=stdin_text,
                capture_output=True,
                text=True,
                timeout=timeout,
                cwd=work_dir,
            )
        except subprocess.TimeoutExpired:
            fail(
                f"{case.title}: the program did not exit within {timeout}s.\n"
                f"Does the input end with a command that terminates the program?"
            )
        if result.stderr.strip():
            print(f"stderr from the program:\n{result.stderr}", file=sys.stderr)
        output.extend(result.stdout.splitlines())
    return output


def print_session(case: TestCase, actual: list[str]) -> None:
    """Prints a readable record of what was typed and what came back."""
    print(THIN_RULE)
    print(f"{case.title}")
    if case.aim:
        print(f"Aim: {case.aim}")
    print(THIN_RULE)
    print("--- console input ---")
    for line in case.inputs:
        print(f"> {line}")
    print("--- console output ---")
    for line in actual:
        print(line)
    print()


def report_failure(case: TestCase, expected: list[str], actual: list[str]) -> None:
    """Prints the expected and actual output, plus a line-by-line diff."""
    # Push out the transcript first, so the report cannot appear above it when
    # the two streams are piped somewhere together.
    sys.stdout.flush()
    print(RULE, file=sys.stderr)
    print(f"TEST FAILED: {case.title}  (test plan line {case.line_number})", file=sys.stderr)
    if case.aim:
        print(f"Aim: {case.aim}", file=sys.stderr)
    print(RULE, file=sys.stderr)

    print("\n--- EXPECTED output ---", file=sys.stderr)
    print("\n".join(expected) or "(nothing)", file=sys.stderr)
    print("\n--- ACTUAL output ---", file=sys.stderr)
    print("\n".join(actual) or "(nothing)", file=sys.stderr)

    print("\n--- DIFF (- expected, + actual) ---", file=sys.stderr)
    diff = difflib.unified_diff(expected, actual, fromfile="expected", tofile="actual", lineterm="")
    print("\n".join(diff), file=sys.stderr)
    print("\nTest session terminated at the first failure.", file=sys.stderr)


def fail(message: str) -> None:
    """Reports a problem with the test run itself and exits."""
    sys.stdout.flush()
    print(f"ERROR: {message}", file=sys.stderr)
    raise SystemExit(1)


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--repo", default=".", help="repository root (default: current directory)")
    parser.add_argument("--plan", default=None, help=f"test plan path (default: {DEFAULT_PLAN})")
    parser.add_argument("--main", default=None, help="entry point class name")
    parser.add_argument("--sources", default=None, help="glob of source files to compile")
    parser.add_argument("--only", default=None, help="run only cases whose title contains this text")
    parser.add_argument("--timeout", type=int, default=DEFAULT_TIMEOUT_SECONDS)
    args = parser.parse_args()

    repo = Path(args.repo).resolve()
    plan_path = Path(args.plan) if args.plan else repo / DEFAULT_PLAN
    if not plan_path.is_absolute():
        plan_path = repo / plan_path

    if shutil.which("javac") is None or shutil.which("java") is None:
        fail("javac and java must be on PATH. On macOS run: sdk use java 25.0.3.fx-zulu")

    try:
        settings, cases = parse_plan(plan_path)
    except PlanError as error:
        fail(str(error))

    main_class = args.main or settings.get("main_class", DEFAULT_MAIN_CLASS)
    source_glob = args.sources or settings.get("source_glob", DEFAULT_SOURCE_GLOB)
    exclude_glob = settings.get("exclude_glob")
    data_file = settings.get("data_file", DEFAULT_DATA_FILE)

    if args.only:
        cases = [case for case in cases if args.only.lower() in case.title.lower()]
        if not cases:
            fail(f"no test case title contains {args.only!r}")

    print(RULE)
    print(f"Text UI test session: {plan_path.relative_to(repo) if plan_path.is_relative_to(repo) else plan_path}")
    print(f"Entry point: {main_class}   Test cases: {len(cases)}")
    print(RULE + "\n")

    with tempfile.TemporaryDirectory(prefix="ui-test-") as workspace:
        classes_dir = Path(workspace) / "classes"
        compile_sources(repo, source_glob, classes_dir, exclude_glob)

        for number, case in enumerate(cases, start=1):
            work_dir = Path(workspace) / f"case-{number:02d}"
            actual = run_case(
                case, classes_dir, main_class, args.timeout, work_dir, data_file
            )
            print_session(case, actual)

            expected_clean = normalise(case.expected)
            actual_clean = normalise(actual)
            if expected_clean != actual_clean:
                report_failure(case, expected_clean, actual_clean)
                print(f"\n{number - 1} of {len(cases)} test case(s) passed before this failure.",
                      file=sys.stderr)
                return 1
            print(f"PASSED: {case.title}\n")

    print(RULE)
    print(f"All {len(cases)} test case(s) passed.")
    print(RULE)
    return 0


if __name__ == "__main__":
    sys.exit(main())
