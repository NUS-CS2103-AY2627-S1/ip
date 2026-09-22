#!/usr/bin/env python3
"""Run console UI tests described in test/ui-test-plan.md."""

from __future__ import annotations

import argparse
import os
import queue
import re
import subprocess
import sys
import threading
import time
import uuid
from dataclasses import dataclass
from pathlib import Path


DEFAULT_PLAN = Path("test/ui-test-plan.md")
DEFAULT_PROGRAM = "java -cp src/main/java nico.Nico"
TEST_RUNS_DIRECTORY = Path("_temp") / "ui-test-runs"


@dataclass
class TestCase:
    name: str
    aim: str
    inputs: list[str]
    expected_outputs: list[str]


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser()
    parser.add_argument("--plan", default=str(DEFAULT_PLAN))
    parser.add_argument("--program", default=None)
    parser.add_argument("--build", default=None)
    parser.add_argument("--timeout", type=float, default=4.0)
    parser.add_argument("--quiet", type=float, default=0.25)
    return parser.parse_args()


def normalize(text: str) -> str:
    lines = [line.strip() for line in text.replace("\r\n", "\n").replace("\r", "\n").split("\n")]
    return "\n".join(lines).strip()


def split_expected(text: str) -> list[str]:
    parts: list[list[str]] = [[]]
    for line in text.replace("\r\n", "\n").replace("\r", "\n").split("\n"):
        if line.strip() == "---":
            parts.append([])
        else:
            parts[-1].append(line)
    return ["\n".join(part).strip() for part in parts]


def extract_fence(body: str, fence_name: str) -> str | None:
    pattern = re.compile(rf"```{re.escape(fence_name)}\s*\n(.*?)\n```", re.DOTALL)
    match = pattern.search(body)
    return match.group(1) if match else None


def parse_plan(path: Path) -> tuple[str | None, str | None, list[TestCase]]:
    if not path.exists():
        raise SystemExit(f"Missing test plan: {path}")

    content = path.read_text(encoding="utf-8")
    global_section = content.split("## Test Case:", 1)[0]
    build = extract_fence(global_section, "build")
    program = extract_fence(global_section, "program")

    cases: list[TestCase] = []
    blocks = re.split(r"^## Test Case:\s*", content, flags=re.MULTILINE)[1:]
    for block in blocks:
        lines = block.splitlines()
        name = lines[0].strip() if lines else "Unnamed"
        body = "\n".join(lines[1:])

        aim_match = re.search(r"^Aim:\s*(.+)$", body, flags=re.MULTILINE)
        aim = aim_match.group(1).strip() if aim_match else ""
        inputs_block = extract_fence(body, "input")
        expected_block = extract_fence(body, "expected")

        if not aim:
            raise SystemExit(f"Test case '{name}' is missing Aim.")
        if inputs_block is None:
            raise SystemExit(f"Test case '{name}' is missing an input block.")
        if expected_block is None:
            raise SystemExit(f"Test case '{name}' is missing an expected block.")

        inputs = [line for line in inputs_block.splitlines() if line.strip()]
        expected_outputs = split_expected(expected_block)
        if len(inputs) != len(expected_outputs):
            raise SystemExit(
                f"Test case '{name}' has {len(inputs)} input(s) but "
                f"{len(expected_outputs)} expected output chunk(s)."
            )
        cases.append(TestCase(name, aim, inputs, expected_outputs))

    if not cases:
        raise SystemExit(f"No test cases found in {path}.")
    return build.strip() if build else None, program.strip() if program else None, cases


def start_reader(stream, out_queue: queue.Queue[str]) -> threading.Thread:
    def read_stream() -> None:
        while True:
            chunk = stream.read(1)
            if chunk == "":
                break
            out_queue.put(chunk)

    thread = threading.Thread(target=read_stream, daemon=True)
    thread.start()
    return thread


def drain_output(out_queue: queue.Queue[str], process: subprocess.Popen, timeout: float, quiet: float) -> str:
    collected: list[str] = []
    deadline = time.monotonic() + timeout
    last_output = time.monotonic()

    while time.monotonic() < deadline:
        try:
            chunk = out_queue.get(timeout=0.05)
            collected.append(chunk)
            last_output = time.monotonic()
        except queue.Empty:
            if process.poll() is not None:
                while True:
                    try:
                        collected.append(out_queue.get_nowait())
                    except queue.Empty:
                        return "".join(collected)
            if time.monotonic() - last_output >= quiet:
                return "".join(collected)

    return "".join(collected)


def run_command(command: str, label: str) -> None:
    print(f"Running {label}: {command}")
    result = subprocess.run(command, shell=True, text=True, capture_output=True)
    if result.stdout:
        print(result.stdout, end="")
    if result.stderr:
        print(result.stderr, end="", file=sys.stderr)
    if result.returncode != 0:
        raise SystemExit(f"{label} failed with exit code {result.returncode}.")


def terminate(process: subprocess.Popen) -> None:
    if process.poll() is None:
        process.terminate()
        try:
            process.wait(timeout=1)
        except subprocess.TimeoutExpired:
            process.kill()


def run_test_case(test: TestCase, program: str, timeout: float, quiet: float) -> bool:
    print(f"\n=== Test Case: {test.name} ===")
    print(f"Aim: {test.aim}")

    env = os.environ.copy()
    env["PYTHONUNBUFFERED"] = "1"
    env["REPO_ROOT"] = str(Path.cwd())

    run_directory = TEST_RUNS_DIRECTORY / uuid.uuid4().hex
    run_directory.mkdir(parents=True, exist_ok=False)

    process = subprocess.Popen(
        program,
        shell=True,
        stdin=subprocess.PIPE,
        stdout=subprocess.PIPE,
        stderr=subprocess.STDOUT,
        text=True,
        bufsize=0,
        env=env,
        cwd=run_directory,
    )
    assert process.stdin is not None
    assert process.stdout is not None

    out_queue: queue.Queue[str] = queue.Queue()
    start_reader(process.stdout, out_queue)
    transcript: list[tuple[str, str]] = []

    try:
        initial_output = drain_output(out_queue, process, timeout, quiet)
        if initial_output.strip():
            transcript.append(("<program start>", initial_output))

        for index, user_input in enumerate(test.inputs):
            print(f"\n> {user_input}")
            process.stdin.write(user_input + "\n")
            process.stdin.flush()

            actual = drain_output(out_queue, process, timeout, quiet)
            transcript.append((user_input, actual))
            print(actual, end="" if actual.endswith("\n") else "\n")

            expected = test.expected_outputs[index]
            if normalize(expected) not in normalize(actual):
                print("\nFAILED")
                print(f"Test case: {test.name}")
                print(f"nico.Command: {user_input}")
                print("\nExpected output:")
                print(expected)
                print("\nActual output:")
                print(actual)
                print("\nTranscript so far:")
                for command, output in transcript:
                    print(f"\n> {command}")
                    print(output, end="" if output.endswith("\n") else "\n")
                return False

        print("PASS")
        return True
    finally:
        terminate(process)


def main() -> int:
    args = parse_args()
    build_from_plan, program_from_plan, cases = parse_plan(Path(args.plan))

    build = args.build if args.build is not None else build_from_plan
    program = args.program or program_from_plan or DEFAULT_PROGRAM

    if build:
        run_command(build, "build command")

    for test in cases:
        if not run_test_case(test, program, args.timeout, args.quiet):
            return 1

    print(f"\nAll {len(cases)} test case(s) passed.")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
