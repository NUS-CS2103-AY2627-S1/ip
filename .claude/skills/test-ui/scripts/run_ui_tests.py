#!/usr/bin/env python3
"""
Run the UI test cases recorded in a test plan (default: test/ui-test-plan.md)
against a compiled Java console program, and report a console transcript for
each test case.

    python3 run_ui_tests.py [test-plan] [--record NAME]

The test plan is a Markdown file with:

  * a "## Setup" section giving the source directory, main class, and
    (optionally) working directory to run the program from, each as a
    backtick-quoted value after a bold label, e.g.:

        - **Source directory:** `src/main/java`
        - **Main class:** `Chud`

  * one or more "## Test Case: <name>" sections, each with:

        **Aim:** <one-line description>

        **Input:**
        ```
        <one command per line, the program's stdin -- last line should be
        the command that makes the program exit, e.g. `bye`>
        ```

        **Expected Output:**
        ```
        <the exact console session transcript expected to result>
        ```

For each test case (in order), the program is compiled once and then run
once per test case, fed the test case's input. The raw stdout is split on
the program's own horizontal-rule separator (its first line of output) into
blocks, and reassembled into a transcript that interleaves each typed
command with the block of output that followed it -- the same shape as the
Expected Output in the test plan.

The transcript for every test case is printed as it runs. The first test
case whose actual transcript does not exactly match its expected transcript
ends the run immediately: the actual and expected transcripts are both
printed in full, and the script exits with a non-zero status. Test cases
after a failure are not run.

Pass `--record NAME` to skip comparison for the named test case and just
print its actual transcript -- useful for generating the Expected Output
text to paste into the test plan when authoring or updating a test case.
"""
from __future__ import annotations

import argparse
import re
import shutil
import subprocess
import sys
import tempfile
from pathlib import Path

TEST_CASE_HEADING = re.compile(r"^## Test Case:\s*(.+)$", re.MULTILINE)
SETUP_FIELD = re.compile(r"\*\*{label}:\*\*\s*`([^`]+)`")


def fenced_block_after(text: str, label: str) -> str:
    """Return the contents of the first ```-fenced block that follows **label:** in text."""
    label_match = re.search(rf"\*\*{re.escape(label)}:\*\*", text)
    if not label_match:
        raise ValueError(f"missing **{label}:** section")
    rest = text[label_match.end():]
    fence_match = re.search(r"```[^\n]*\n(.*?)```", rest, re.DOTALL)
    if not fence_match:
        raise ValueError(f"missing fenced code block after **{label}:**")
    return fence_match.group(1).rstrip("\n")


def parse_setup(setup_text: str) -> dict:
    def field(label: str) -> str:
        match = re.search(SETUP_FIELD.pattern.format(label=re.escape(label)), setup_text)
        if not match:
            raise ValueError(f"Setup section is missing **{label}:** `...`")
        return match.group(1)

    return {
        "source_dir": field("Source directory"),
        "main_class": field("Main class"),
    }


def parse_test_plan(plan_path: Path):
    text = plan_path.read_text()

    setup_match = re.search(r"^## Setup\s*$(.*?)(?=^## Test Case:|\Z)", text, re.MULTILINE | re.DOTALL)
    if not setup_match:
        raise ValueError("test plan is missing a '## Setup' section")
    setup = parse_setup(setup_match.group(1))

    headings = list(TEST_CASE_HEADING.finditer(text))
    if not headings:
        raise ValueError("test plan has no '## Test Case: <name>' sections")

    test_cases = []
    for i, heading in enumerate(headings):
        name = heading.group(1).strip()
        section_end = headings[i + 1].start() if i + 1 < len(headings) else len(text)
        section = text[heading.end():section_end]

        aim_match = re.search(r"\*\*Aim:\*\*\s*(.+)", section)
        aim = aim_match.group(1).strip() if aim_match else "(no aim given)"

        input_text = fenced_block_after(section, "Input")
        expected_text = fenced_block_after(section, "Expected Output")

        inputs = input_text.split("\n") if input_text else []
        test_cases.append({
            "name": name,
            "aim": aim,
            "inputs": inputs,
            "expected": expected_text,
        })

    return setup, test_cases


def compile_program(repo_root: Path, source_dir: str, build_dir: Path) -> Path:
    classes_dir = build_dir / "classes"
    classes_dir.mkdir(parents=True, exist_ok=True)
    # The GUI package depends on JavaFX, which isn't on plain javac's classpath here and isn't
    # needed for console testing anyway -- the console entry point (chud.Chud) never touches it.
    sources = sorted(
        p for p in (repo_root / source_dir).glob("**/*.java")
        if "gui" not in p.relative_to(repo_root / source_dir).parts
    )
    if not sources:
        raise ValueError(f"no .java files found in {source_dir}")

    result = subprocess.run(
        ["javac", "-d", str(classes_dir), *[str(s) for s in sources]],
        cwd=repo_root, capture_output=True, text=True,
    )
    if result.returncode != 0:
        print("Compilation failed:\n" + result.stdout + result.stderr, file=sys.stderr)
        sys.exit(1)
    return classes_dir


def run_program(repo_root: Path, classes_dir: Path, main_class: str, inputs: list[str]) -> str:
    # Each test case must start from a clean slate: the program persists its task list to
    # ./data (relative to repo_root) between runs, so stale data from an earlier test case
    # would otherwise leak into this one.
    shutil.rmtree(repo_root / "data", ignore_errors=True)

    stdin_text = "\n".join(inputs) + "\n"
    result = subprocess.run(
        ["java", "-cp", str(classes_dir), main_class],
        cwd=repo_root, input=stdin_text, capture_output=True, text=True, timeout=15,
    )
    return result.stdout


def build_transcript(inputs: list[str], stdout: str) -> str:
    """Interleave each typed command with the block of output that followed it."""
    lines = stdout.split("\n")
    if lines and lines[-1] == "":
        lines = lines[:-1]
    if not lines:
        raise ValueError("program produced no output")

    horizontal_line = lines[0]
    blocks: list[list[str]] = []
    current: list[str] = []
    for line in lines:
        if line == horizontal_line:
            if current:
                blocks.append(current)
                current = []
        else:
            current.append(line)
    if current:
        blocks.append(current)

    parts = [horizontal_line]
    parts.extend(blocks[0] if blocks else [])
    parts.append(horizontal_line)

    for i, command in enumerate(inputs):
        parts.append("")
        parts.append(command)
        parts.append(horizontal_line)
        if i + 1 < len(blocks):
            parts.extend(blocks[i + 1])
        parts.append(horizontal_line)

    return "\n".join(parts)


def run_test_case(repo_root: Path, classes_dir: Path, main_class: str, test_case: dict) -> str:
    stdout = run_program(repo_root, classes_dir, main_class, test_case["inputs"])
    return build_transcript(test_case["inputs"], stdout)


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
    parser.add_argument("test_plan", nargs="?", default="test/ui-test-plan.md")
    parser.add_argument("--record", metavar="NAME", help="print the actual transcript for one test case without comparing")
    args = parser.parse_args()

    repo_root = Path(subprocess.run(
        ["git", "rev-parse", "--show-toplevel"], capture_output=True, text=True, check=True,
    ).stdout.strip())
    plan_path = (repo_root / args.test_plan) if not Path(args.test_plan).is_absolute() else Path(args.test_plan)

    setup, test_cases = parse_test_plan(plan_path)

    with tempfile.TemporaryDirectory(prefix="test-ui-") as build_dir:
        classes_dir = compile_program(repo_root, setup["source_dir"], Path(build_dir))

        if args.record:
            matches = [tc for tc in test_cases if tc["name"] == args.record]
            if not matches:
                print(f"No test case named {args.record!r} in {plan_path}", file=sys.stderr)
                return 1
            transcript = run_test_case(repo_root, classes_dir, setup["main_class"], matches[0])
            print(transcript)
            return 0

        for i, test_case in enumerate(test_cases, start=1):
            print(f"=== Test Case {i}/{len(test_cases)}: {test_case['name']} ===")
            print(f"Aim: {test_case['aim']}")
            transcript = run_test_case(repo_root, classes_dir, setup["main_class"], test_case)
            print(transcript)

            if transcript.strip("\n") == test_case["expected"].strip("\n"):
                print(f"--- PASS: {test_case['name']} ---\n")
            else:
                print(f"--- FAIL: {test_case['name']} ---")
                print("\nExpected:\n" + test_case["expected"])
                print("\nActual:\n" + transcript)
                print(f"\nStopped after test case {i}/{len(test_cases)} due to failure.")
                return 1

        print(f"All {len(test_cases)} test case(s) passed.")
        return 0


if __name__ == "__main__":
    sys.exit(main())
