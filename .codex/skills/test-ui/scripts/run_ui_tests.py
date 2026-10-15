#!/usr/bin/env python3
"""Run console UI tests from a Markdown plan."""

import argparse
import re
import subprocess
import sys
from pathlib import Path


def normalize(text):
    """Normalize line endings before comparing output."""
    return text.replace("\r\n", "\n").rstrip("\n") + "\n"


def unindent(block):
    """Remove the four-space Markdown indentation from a text block."""
    return "\n".join(line[4:] for line in block.rstrip("\n").splitlines())


def parse_cases(plan_path):
    """Parse cases with Aim, Inputs, and Expected output fields."""
    pattern = re.compile(
        r"^## (?P<title>.+?)\n"
        r"Aim: (?P<aim>.+?)\n"
        r"Inputs:\n(?P<input>(?:    .*\n)+)"
        r"Expected output:\n(?P<expected>(?:    .*\n?)+)",
        re.MULTILINE,
    )
    text = plan_path.read_text(encoding="utf-8").replace("\r\n", "\n")
    cases = [
        {
            "title": match.group("title"),
            "aim": match.group("aim"),
            "input": normalize(unindent(match.group("input"))),
            "expected": normalize(unindent(match.group("expected"))),
        }
        for match in pattern.finditer(text)
    ]
    if not cases:
        raise ValueError("No test cases found in the plan.")
    return cases


def compile_program(project_root, classes_dir):
    """Compile all Nova source files."""
    java_files = sorted((project_root / "src/main/java").rglob("*.java"))
    result = subprocess.run(
        ["javac", "-d", str(classes_dir), *(str(path) for path in java_files)],
        text=True,
        capture_output=True,
        check=False,
    )
    if result.returncode:
        print("Compilation failed.", file=sys.stderr)
        print(result.stderr, end="", file=sys.stderr)
        return False
    return True


def main():
    """Run each case, printing its transcript and stopping at the first failure."""
    parser = argparse.ArgumentParser()
    parser.add_argument("plan", type=Path)
    args = parser.parse_args()

    try:
        cases = parse_cases(args.plan)
    except (OSError, ValueError) as error:
        print(error, file=sys.stderr)
        return 2

    classes_dir = Path.cwd() / "out" / "ui-test-classes"
    classes_dir.mkdir(parents=True, exist_ok=True)
    if not compile_program(Path.cwd(), classes_dir):
        return 2

    for case in cases:
        project_root = Path.cwd()
        task_file = project_root / "data" / "nova.txt"
        temporary_task_file = project_root / "data" / "nova.txt.tmp"
        task_file.unlink(missing_ok=True)
        temporary_task_file.unlink(missing_ok=True)
        result = subprocess.run(
            ["java", "-cp", str(classes_dir), "nova.Nova"],
            input=case["input"],
            text=True,
            capture_output=True,
            check=False,
            cwd=project_root,
        )
        actual = normalize(result.stdout)
        if result.returncode:
            actual += "\nProgram error:\n" + result.stderr

        print("=== " + case["title"] + " ===")
        print("Aim: " + case["aim"])
        print("Console input:")
        print(case["input"], end="")
        print("Console output:")
        print(actual, end="")

        if actual != case["expected"]:
            print("FAIL: Expected output:")
            print(case["expected"], end="")
            print("Actual output:")
            print(actual, end="")
            print("Terminating test session after the first failure.")
            return 1
        print("PASS")

    print("All UI test cases passed.")
    return 0


if __name__ == "__main__":
    sys.exit(main())
