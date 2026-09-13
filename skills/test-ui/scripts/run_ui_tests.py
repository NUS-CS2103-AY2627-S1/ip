#!/usr/bin/env python3
"""Compile the project and run markdown-defined console UI tests."""
import argparse
import os
import re
import subprocess
import sys
import tempfile
from pathlib import Path

CASE = re.compile(
    r"^## Test case: (?P<name>.+?)\r?\n"
    r"\*\*Aim:\*\* (?P<aim>.+?)\r?\n"
    r"\*\*Input:\*\*\r?\n```text\r?\n(?P<input>.*?)\r?\n```\r?\n"
    r"\*\*Expected output:\*\*\r?\n```text\r?\n(?P<expected>.*?)\r?\n```",
    re.MULTILINE | re.DOTALL,
)


def normalise(text):
    return text.replace("\r\n", "\n").rstrip("\n")


def executable(name):
    home = os.environ.get("JAVA_HOME")
    candidate = Path(home) / "bin" / f"{name}.exe" if home else None
    return str(candidate) if candidate and candidate.exists() else name


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("--plan", type=Path, default=Path("test/ui-test-plan.md"))
    parser.add_argument("--allow-non-25", action="store_true")
    args = parser.parse_args()
    root = Path.cwd()
    plan = args.plan if args.plan.is_absolute() else root / args.plan
    matches = list(CASE.finditer(plan.read_text(encoding="utf-8")))
    if not matches:
        print("UI test runner error: no test cases found.", file=sys.stderr)
        return 2
    javac = executable("javac")
    version = subprocess.run([javac, "--version"], text=True, capture_output=True)
    if version.returncode or (not args.allow_non_25 and not version.stdout.startswith("javac 25")):
        print(f"UI test runner error: Java 25 is required; found {version.stdout.strip()}.", file=sys.stderr)
        return 2
    java_root = root / "src" / "main" / "java"
    sources = sorted(source for source in java_root.rglob("*.java")
                     if source.name not in {"NelsonGui.java", "Launcher.java", "DialogBox.java"})
    with tempfile.TemporaryDirectory(prefix="nelson-ui-tests-") as temporary:
        compiled = subprocess.run([javac, "-d", temporary, *(str(source) for source in sources)],
                                  cwd=root, text=True, capture_output=True)
        if compiled.returncode:
            print(f"UI test runner error: compilation failed:\n{compiled.stderr}", file=sys.stderr)
            return 2
        for match in matches:
            name, aim = match["name"], match["aim"]
            # Each UI case is isolated; persistence is verified separately with a preloaded file.
            (root / "data" / "nelson.txt").unlink(missing_ok=True)
            user_input = normalise(match["input"])
            expected = normalise(match["expected"])
            result = subprocess.run([executable("java"), "-cp", temporary, "nelson.Nelson"],
                                    cwd=root, input=user_input + "\n", text=True, capture_output=True)
            actual = normalise(result.stdout)
            print(f"\n=== {name} ===\nAim: {aim}\n--- Console input ---\n{user_input}")
            print(f"--- Console output ---\n{actual}")
            if result.returncode or actual != expected:
                print("RESULT: FAIL\n--- Expected output ---")
                print(expected)
                print("--- Actual output ---")
                print(actual)
                return 1
            print("RESULT: PASS")
    print("\nAll UI test cases passed.")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
