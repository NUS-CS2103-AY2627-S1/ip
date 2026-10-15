#!/usr/bin/env python3
"""
Run the console UI test cases in test/ui-test-plan.md against the compiled
Java program, one program session per test case.

    python3 .claude/skills/test-ui/scripts/run_ui_tests.py [test-plan-path]

Run from the repository root. See test/ui-test-plan.md for the test case
format (fenced ```session blocks of ">>> input" / expected-output lines).

For each test case, inputs are sent to the program in order, and the output
produced after each input is compared, line for line, against the expected
output for that step. The full session transcript (as actually observed) is
always printed. On the first mismatch anywhere, the whole run stops
immediately and the expected vs actual output for the failing step is
reported.
"""
from __future__ import annotations

import queue
import re
import subprocess
import sys
import tempfile
import threading
from pathlib import Path

DEFAULT_TEST_PLAN = Path("test/ui-test-plan.md")
SRC_DIR = Path("src/main/java")
BUILD_DIR = Path("_temp/test-ui-build")
READ_TIMEOUT_SECONDS = 5.0

_EOF = object()
_TIMEOUT = object()


def find_main_class(src_dir: Path) -> str:
    """Returns the fully qualified name of the class holding the main method.

    The name is built from the file's path below the source root, so a class in
    a package is named the way java expects it on the command line: a file at
    rex/Rex.java is reported as "rex.Rex", while one sitting directly in the
    source root is reported as just "Rex".
    """
    for java_file in sorted(src_dir.rglob("*.java")):
        if "public static void main" in java_file.read_text():
            return ".".join(java_file.relative_to(src_dir).with_suffix("").parts)
    raise RuntimeError(f"No file with a main method found in {src_dir}")


def compile_project(src_dir: Path, build_dir: Path) -> None:
    build_dir.mkdir(parents=True, exist_ok=True)
    java_files = [str(p) for p in sorted(src_dir.rglob("*.java"))]
    if not java_files:
        raise RuntimeError(f"No .java files found in {src_dir}")
    result = subprocess.run(
        ["javac", *java_files, "-d", str(build_dir)],
        capture_output=True, text=True,
    )
    if result.returncode != 0:
        raise RuntimeError(f"Compilation failed:\n{result.stdout}\n{result.stderr}")


def parse_test_plan(md_text: str) -> list[tuple[str, list[tuple[str, list[str]]]]]:
    heading_re = re.compile(r"^###\s+(.*)$", re.MULTILINE)
    block_re = re.compile(r"```session\n(.*?)\n```", re.DOTALL)

    test_cases = []
    for block_match in block_re.finditer(md_text):
        start = block_match.start()
        headings_before = list(heading_re.finditer(md_text[:start]))
        name = headings_before[-1].group(1).strip() if headings_before else f"(unnamed, offset {start})"
        test_cases.append((name, parse_block(block_match.group(1))))
    return test_cases


def parse_block(block: str) -> list[tuple[str, list[str]]]:
    steps: list[tuple[str, list[str]]] = []
    current_input = None
    current_expected: list[str] = []
    for line in block.split("\n"):
        if line.startswith(">>> "):
            if current_input is not None:
                steps.append((current_input, current_expected))
            current_input = line[len(">>> "):]
            current_expected = []
        else:
            current_expected.append(line)
    if current_input is not None:
        steps.append((current_input, current_expected))
    return steps


def _reader_worker(proc: subprocess.Popen, q: "queue.Queue") -> None:
    for line in iter(proc.stdout.readline, ""):
        q.put(line.rstrip("\n"))
    q.put(_EOF)


def _read_line(q: "queue.Queue", timeout: float):
    try:
        return q.get(timeout=timeout)
    except queue.Empty:
        return _TIMEOUT


def _start_process(build_dir: Path, main_class: str, work_dir: Path):
    """Launches the program in work_dir, returning (process, output_queue).

    The program is run with work_dir as its working directory so that any
    files it creates (e.g. its save file) land in a throwaway directory
    instead of the repository, keeping test cases isolated from each other
    and from real data. build_dir must therefore be an absolute path.

    Assertions are enabled with -ea so that a broken internal assumption
    fails these tests instead of passing unnoticed; Java ignores every
    assert statement without that flag.
    """
    proc = subprocess.Popen(
        ["java", "-ea", "-cp", str(build_dir), main_class],
        stdin=subprocess.PIPE, stdout=subprocess.PIPE, stderr=subprocess.STDOUT,
        text=True, bufsize=1, cwd=str(work_dir),
    )
    q: "queue.Queue" = queue.Queue()
    threading.Thread(target=_reader_worker, args=(proc, q), daemon=True).start()
    return proc, q


def _stop_process(proc: subprocess.Popen) -> None:
    proc.terminate()
    try:
        proc.wait(timeout=2)
    except subprocess.TimeoutExpired:
        proc.kill()


def run_test_case(build_dir: Path, main_class: str, steps: list[tuple[str, list[str]]],
                  work_dir: Path):
    """Returns (passed, transcript_lines, failure_or_None)."""
    proc, q = _start_process(build_dir, main_class, work_dir)

    transcript: list[str] = []
    failure = None

    for step_input, expected_lines in steps:
        if step_input == "(startup)":
            transcript.append(">>> (startup)")
        elif step_input == "(restart)":
            # Quit and relaunch in the same working directory, so that whatever
            # the previous session wrote to disk is still there for this one.
            transcript.append(">>> (restart)")
            _stop_process(proc)
            proc, q = _start_process(build_dir, main_class, work_dir)
        elif step_input.startswith("(write ") and step_input.endswith(")"):
            # Put a file on disk for the program to find. Its "expected output"
            # lines are the file's contents, and nothing is read from the
            # program, so this step is complete once the file is written.
            relative_path = step_input[len("(write "):-1].strip()
            target = work_dir / relative_path
            target.parent.mkdir(parents=True, exist_ok=True)
            target.write_text("\n".join(expected_lines) + "\n")
            transcript.append(f">>> {step_input}")
            transcript.extend(expected_lines)
            continue
        else:
            transcript.append(f">>> {step_input}")
            proc.stdin.write(step_input + "\n")
            proc.stdin.flush()

        for expected_line in expected_lines:
            item = _read_line(q, READ_TIMEOUT_SECONDS)
            if item is _TIMEOUT:
                transcript.append("<no output — timed out waiting for a line>")
                failure = (step_input, expected_line, "<timed out>")
                break
            if item is _EOF:
                transcript.append("<program exited early>")
                failure = (step_input, expected_line, "<program exited>")
                break
            transcript.append(item)
            if item.rstrip() != expected_line.rstrip():
                failure = (step_input, expected_line, item)
                break
        if failure is not None:
            break

    _stop_process(proc)

    return failure is None, transcript, failure


def main() -> int:
    test_plan_path = Path(sys.argv[1]) if len(sys.argv) > 1 else DEFAULT_TEST_PLAN
    if not test_plan_path.exists():
        print(f"Test plan not found: {test_plan_path}", file=sys.stderr)
        return 2

    test_cases = parse_test_plan(test_plan_path.read_text())
    if not test_cases:
        print(f"No ```session test case blocks found in {test_plan_path}", file=sys.stderr)
        return 2

    main_class = find_main_class(SRC_DIR)
    print(f"Compiling {SRC_DIR} (entry point: {main_class})...")
    compile_project(SRC_DIR, BUILD_DIR)
    build_dir = BUILD_DIR.resolve()

    for name, steps in test_cases:
        print(f"\n=== {name} ===")
        # Each test case gets its own empty directory to run in, so that files
        # written by one case cannot be seen by the next one.
        with tempfile.TemporaryDirectory(prefix="rex-ui-test-") as work_dir:
            passed, transcript, failure = run_test_case(
                build_dir, main_class, steps, Path(work_dir)
            )
        print("\n".join(transcript))

        if not passed:
            step_input, expected_line, actual_line = failure
            print(f"\nFAILED at input: {step_input!r}")
            print(f"  expected: {expected_line!r}")
            print(f"  actual:   {actual_line!r}")
            print(f"\nStopped after first failure. Remaining test cases were not run.")
            return 1

        print(f"--- PASSED: {name} ---")

    print(f"\nAll {len(test_cases)} test case(s) passed.")
    return 0


if __name__ == "__main__":
    sys.exit(main())
