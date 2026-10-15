#!/usr/bin/env python3
"""
Run the scripted console sessions described in test/ui-test-plan.md against
the compiled chatbot, and check each command's output against its expected
output.

    python3 run_ui_tests.py [--plan test/ui-test-plan.md] [--src src/main/java]
                             [--main-class samantha.Samantha] [--timeout 10]

Test plan format (see test/ui-test-plan.md for the authoritative examples):

    ## Test <n>: <name>

    **Aim:** <what this test case checks>

    ```input
    <one command>
    ```
    ```expected
    <the text printed between the two divider lines for that command>
    ```

    (repeat the ```input``` / ```expected``` pair for each command in the
    session)

Each test case is run as its own fresh `java <main-class>` process. All of a
test case's inputs are fed to its process in order; the resulting stdout is
split on divider lines (any line of 10+ underscores) into response blocks.
The first block -- the startup banner/greeting -- is not checked against
anything, since it is not a response to a command. Every block after that is
compared, in order, against the test case's expected outputs.

Comparison ignores trailing whitespace on each line and leading/trailing
blank lines around a block, but is otherwise an exact match.

On the first mismatch anywhere in the plan, this script prints the actual vs
expected output for that step and stops immediately -- it does not run any
later test cases.
"""
from __future__ import annotations

import argparse
import re
import subprocess
import sys
import tempfile
from dataclasses import dataclass
from pathlib import Path

DIVIDER_RE = re.compile(r"^_{10,}$")
TEST_HEADER_RE = re.compile(r"^##\s*Test\s+\d+:\s*(.+?)\s*$", re.MULTILINE)
AIM_RE = re.compile(r"\*\*Aim:\*\*\s*(.+?)\s*\n\s*\n", re.DOTALL)
BLOCK_RE = re.compile(r"```(input|expected)\n(.*?)\n```", re.DOTALL)


@dataclass
class TestCase:
    name: str
    aim: str
    steps: list[tuple[str, str]]  # (input, expected) pairs, in order


def parse_test_plan(text: str) -> list[TestCase]:
    headers = list(TEST_HEADER_RE.finditer(text))
    cases = []
    for i, header in enumerate(headers):
        name = header.group(1)
        section_end = headers[i + 1].start() if i + 1 < len(headers) else len(text)
        section = text[header.end():section_end]

        aim_match = AIM_RE.search(section)
        aim = " ".join(aim_match.group(1).split()) if aim_match else "(no aim given)"

        blocks = BLOCK_RE.findall(section)
        steps = []
        pending_input = None
        for tag, content in blocks:
            content = content.strip("\n")
            if tag == "input":
                pending_input = content
            elif tag == "expected" and pending_input is not None:
                steps.append((pending_input, content))
                pending_input = None
        cases.append(TestCase(name=name, aim=aim, steps=steps))
    return cases


def normalize(block: str) -> str:
    lines = [line.rstrip() for line in block.strip("\n").splitlines()]
    return "\n".join(lines).strip()


def compile_sources(src_dir: Path) -> Path:
    classes_dir = Path(tempfile.mkdtemp(prefix="ui-test-classes-"))
    sources = sorted(str(p) for p in src_dir.rglob("*.java"))
    result = subprocess.run(
        ["javac", "-d", str(classes_dir), *sources],
        capture_output=True, text=True,
    )
    if result.returncode != 0:
        print("Compilation failed:\n" + result.stderr, file=sys.stderr)
        sys.exit(1)
    return classes_dir


def run_session(
    classes_dir: Path,
    main_class: str,
    inputs: list[str],
    timeout: int,
    working_directory: Path,
):
    stdin_text = "".join(line + "\n" for line in inputs)
    try:
        result = subprocess.run(
            ["java", "-cp", str(classes_dir), main_class],
            input=stdin_text,
            capture_output=True,
            text=True,
            timeout=timeout,
            cwd=working_directory,
        )
        return result.stdout, result.stderr
    except subprocess.TimeoutExpired as e:
        return (e.stdout or ""), (e.stderr or "") + "\n[timed out after {}s]".format(timeout)


def split_response_blocks(stdout: str) -> list[str]:
    blocks = []
    current: list[str] = []
    for line in stdout.splitlines():
        if DIVIDER_RE.match(line):
            if current:
                blocks.append("\n".join(current))
                current = []
        else:
            current.append(line)
    if current:
        blocks.append("\n".join(current))
    return blocks


def print_transcript(case: TestCase, blocks: list[str]) -> None:
    print(f"\n--- console session: {case.name} ---")
    if blocks:
        print(blocks[0].strip("\n"))
    for i, (command, _) in enumerate(case.steps):
        print(f"\n{command}")
        response = blocks[i + 1] if i + 1 < len(blocks) else "(no output)"
        print(response.strip("\n"))
    print("--- end of session ---")


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
    parser.add_argument("--plan", default="test/ui-test-plan.md", type=Path)
    parser.add_argument("--src", default="src/main/java", type=Path)
    parser.add_argument("--main-class", default="samantha.Samantha")
    parser.add_argument("--timeout", type=int, default=10)
    args = parser.parse_args()

    if not args.plan.exists():
        print(f"Test plan not found: {args.plan}", file=sys.stderr)
        return 1

    cases = parse_test_plan(args.plan.read_text())
    if not cases:
        print(f"No test cases found in {args.plan}", file=sys.stderr)
        return 1

    classes_dir = compile_sources(args.src)

    passed = 0
    for case in cases:
        inputs = [command for command, _ in case.steps]
        with tempfile.TemporaryDirectory(prefix="ui-test-workdir-") as working_directory:
            stdout, stderr = run_session(
                classes_dir,
                args.main_class,
                inputs,
                args.timeout,
                Path(working_directory),
            )
        blocks = split_response_blocks(stdout)

        print_transcript(case, blocks)

        failure = None
        for i, (command, expected) in enumerate(case.steps):
            actual = blocks[i + 1] if i + 1 < len(blocks) else None
            if actual is None or normalize(actual) != normalize(expected):
                failure = (i, command, expected, actual)
                break

        if failure:
            i, command, expected, actual = failure
            print(f"\nFAIL: {case.name}  (aim: {case.aim})")
            print(f"  step {i + 1}, input: {command!r}")
            print("  --- expected ---")
            print(normalize(expected))
            print("  --- actual ---")
            print(normalize(actual) if actual is not None else "(no output -- process produced no more response blocks)")
            if stderr.strip():
                print("  --- stderr ---")
                print(stderr.strip())
            print(f"\n{passed}/{len(cases)} test case(s) passed before this failure. Stopping.")
            return 1

        print(f"PASS: {case.name}")
        passed += 1

    print(f"\n{passed}/{len(cases)} test case(s) passed.")
    return 0


if __name__ == "__main__":
    sys.exit(main())
