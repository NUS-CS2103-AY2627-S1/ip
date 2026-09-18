#!/usr/bin/env python
# -*- coding: utf-8 -*-
"""Run Aether UI tests from test/ui-test-plan.md (Python 3.6 compatible)."""

from __future__ import print_function

import os
import re
import subprocess
import sys

LINE = "____________________________________________________________"
PLAN_PATH = os.path.join("test", "ui-test-plan.md")
SRC_GLOB = os.path.join("src", "main", "java", "**", "*.java")


def repo_root():
    test_dir = os.path.dirname(os.path.abspath(__file__))
    # test/run_ui_tests.py -> repository root is one level up
    return os.path.abspath(os.path.join(test_dir, ".."))


def normalize(text):
    text = text.replace("\r\n", "\n").replace("\r", "\n")
    lines = [line.rstrip() for line in text.split("\n")]
    while lines and lines[-1] == "":
        lines.pop()
    return "\n".join(lines)


def split_messages(transcript):
    """Split a full printMessage transcript into bodies between LINE markers."""
    text = normalize(transcript)
    parts = text.split(LINE)
    bodies = []
    for part in parts:
        body = part.strip("\n")
        if body != "":
            bodies.append(body)
    return bodies


def parse_plan(path):
    with open(path, "r") as handle:
        content = handle.read()

    # Split on test case headings while keeping the heading line.
    chunks = re.split(r"(?m)^## Test case:\s*", content)
    cases = []
    for chunk in chunks[1:]:
        lines = chunk.splitlines()
        name = lines[0].strip()
        rest = "\n".join(lines[1:])

        aim_match = re.search(r"\*\*Aim:\*\*\s*(.+?)(?:\n\s*\n|\n\*\*)", rest, re.S)
        aim = aim_match.group(1).strip() if aim_match else ""

        inputs_match = re.search(r"\*\*Inputs:\*\*\s*```(?:[^\n]*)\n(.*?)```", rest, re.S)
        expected_match = re.search(
            r"\*\*Expected output:\*\*\s*```(?:[^\n]*)\n(.*?)```", rest, re.S
        )
        if inputs_match is None or expected_match is None:
            raise SystemExit("Could not parse inputs/expected output for: " + name)

        inputs = [line for line in normalize(inputs_match.group(1)).split("\n") if line != ""]
        expected = normalize(expected_match.group(1))
        cases.append({"name": name, "aim": aim, "inputs": inputs, "expected": expected})
    if not cases:
        raise SystemExit("No test cases found in " + path)
    return cases


def compile_sources(root):
    from glob import glob

    os.chdir(root)
    out_dir = os.path.join(root, "out")
    if not os.path.isdir(out_dir):
        os.makedirs(out_dir)
    java_files = glob(os.path.join(root, SRC_GLOB), recursive=True)
    gui_dir = os.path.normpath(os.path.join(root, "src", "main", "java", "aether", "gui"))
    java_files = [
        path for path in java_files
        if not os.path.normpath(path).startswith(gui_dir + os.sep)
    ]
    if not java_files:
        raise SystemExit("No Java sources found under src/main/java")
    command = ["javac", "-d", out_dir] + java_files
    print("Compiling:", " ".join(command))
    result = subprocess.Popen(
        command, stdout=subprocess.PIPE, stderr=subprocess.PIPE, universal_newlines=True
    )
    stdout, stderr = result.communicate()
    if result.returncode != 0:
        sys.stderr.write(stdout)
        sys.stderr.write(stderr)
        raise SystemExit("Compilation failed")


def run_session(root, inputs):
    commands = list(inputs)
    stdin_text = "\n".join(commands)
    if commands:
        stdin_text += "\n"
    process = subprocess.Popen(
        ["java", "-cp", os.path.join(root, "out"), "aether.Aether"],
        stdin=subprocess.PIPE,
        stdout=subprocess.PIPE,
        stderr=subprocess.PIPE,
        universal_newlines=True,
        cwd=root,
    )
    stdout, stderr = process.communicate(stdin_text)
    if process.returncode != 0:
        raise SystemExit("Aether exited with code %s\n%s" % (process.returncode, stderr))
    return commands, normalize(stdout)


def reset_saved_tasks(root):
    """Remove the test data file so every independent UI case starts empty."""
    data_file = os.path.join(root, "data", "aether.txt")
    if os.path.isfile(data_file):
        os.remove(data_file)


def format_session(commands, actual):
    messages = split_messages(actual)
    record = []
    record.append("(greeting)")
    if messages:
        record.append(LINE)
        record.append(messages[0])
        record.append(LINE)
    for index, command in enumerate(commands):
        record.append(command)
        message_index = index + 1
        if message_index < len(messages):
            record.append(LINE)
            record.append(messages[message_index])
            record.append(LINE)
    for message_index in range(len(commands) + 1, len(messages)):
        record.append("(end of input)")
        record.append(LINE)
        record.append(messages[message_index])
        record.append(LINE)
    return "\n".join(record)


def first_mismatch(expected_messages, actual_messages, commands):
    count = min(len(expected_messages), len(actual_messages))
    for index in range(count):
        if expected_messages[index] != actual_messages[index]:
            if index == 0:
                label = "greeting"
            elif index <= len(commands):
                label = commands[index - 1]
            else:
                label = "end of input"
            return label, expected_messages[index], actual_messages[index]
    if len(expected_messages) != len(actual_messages):
        return (
            "message count",
            "%s messages" % len(expected_messages),
            "%s messages" % len(actual_messages),
        )
    return None


def main():
    root = repo_root()
    os.chdir(root)
    plan = os.path.join(root, PLAN_PATH)
    cases = parse_plan(plan)
    compile_sources(root)

    for case in cases:
        print("=" * 60)
        print("Test case: " + case["name"])
        print("Aim: " + case["aim"])
        reset_saved_tasks(root)
        commands, actual = run_session(root, case["inputs"])
        expected = case["expected"]
        session_record = format_session(commands, actual)
        print("--- Console session ---")
        print(session_record)
        print("-----------------------")

        if actual != expected:
            mismatch = first_mismatch(
                split_messages(expected), split_messages(actual), commands
            )
            print("FAILED: " + case["name"])
            if mismatch:
                label, expected_part, actual_part = mismatch
                print("First mismatch at: " + label)
                print("--- Expected ---")
                print(expected_part)
                print("--- Actual ---")
                print(actual_part)
            else:
                print("--- Expected ---")
                print(expected)
                print("--- Actual ---")
                print(actual)
            return 1

        print("PASSED: " + case["name"])

    print("=" * 60)
    print("All %s test case(s) passed." % len(cases))
    return 0


if __name__ == "__main__":
    sys.exit(main())
