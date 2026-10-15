#!/usr/bin/env python3
"""Run exact-output Nova console tests from the Markdown UI test plan."""
import re
import subprocess
import sys
import tempfile
from pathlib import Path

PATTERN = re.compile(
    r"^## (.+?)\n+Aim:\s*(.+?)\n+(?:### Initial data file\s*\n+```(?:text)?\n(.*?)\n```\s*\n+)?"
    r"### Input\s*\n+```(?:text)?\n(.*?)\n```\s*\n+"
    r"### Expected output\s*\n+```(?:text)?\n(.*?)\n```(?=\s*(?:\n## |\Z))",
    re.MULTILINE | re.DOTALL,
)


def normalized(value):
    """Normalize platform line endings and trailing newlines."""
    return value.replace("\r\n", "\n").replace("\r", "\n").rstrip("\n")


def execute(command, **kwargs):
    """Execute a process while capturing all console output."""
    return subprocess.run(command, text=True, capture_output=True, check=False, **kwargs)


def main():
    """Compile Nova and stop immediately when a UI test fails."""
    root = Path.cwd()
    plan = root / "test" / "ui-test-plan.md"
    if not plan.is_file():
        print(f"TEST SESSION FAILED: missing {plan}")
        return 1
    cases = PATTERN.findall(normalized(plan.read_text(encoding="utf-8")))
    if not cases:
        print("TEST SESSION FAILED: no valid test cases found")
        return 1
    version = execute(["javac", "-version"])
    version_text = normalized(version.stdout + version.stderr)
    if version.returncode or not version_text.startswith("javac 25"):
        print(f"TEST SESSION FAILED: Java 25 required; found {version_text or 'no javac'}")
        return 1
    sources = sorted(
        source for source in (root / "src" / "main" / "java").rglob("*.java")
        if "gui" not in source.parts and source.name != "Launcher.java"
    )
    with tempfile.TemporaryDirectory(prefix="nova-ui-test-") as classes:
        compiled = execute(["javac", "-d", classes, *map(str, sources)])
        if compiled.returncode:
            print("TEST SESSION FAILED: compilation failed")
            print(normalized(compiled.stdout + compiled.stderr))
            return 1
        print(f"UI TEST SESSION ({len(cases)} cases, {version_text})")
        for number, (name, aim, initial_data, commands, expected) in enumerate(cases, 1):
            commands, expected = normalized(commands), normalized(expected)
            with tempfile.TemporaryDirectory(prefix="nova-ui-case-") as run_directory:
                if initial_data:
                    data_file = Path(run_directory) / "data" / "nova.txt"
                    data_file.parent.mkdir(parents=True)
                    data_file.write_text(normalized(initial_data) + "\n", encoding="utf-8")
                result = execute(
                    ["java", "-cp", classes, "nova.Nova"],
                    input=commands + "\n",
                    cwd=run_directory,
                )
                actual = normalized(result.stdout)
                print(f"\n[{number}/{len(cases)}] {name}\nAim: {aim.strip()}")
                if initial_data:
                    print(f"--- INITIAL DATA FILE ---\n{normalized(initial_data)}")
                print(f"--- CONSOLE INPUT ---\n{commands}")
                print(f"--- CONSOLE OUTPUT ---\n{actual}")
                if result.returncode or actual != expected:
                    print(f"--- RESULT: FAILED ---\n--- EXPECTED OUTPUT ---\n{expected}")
                    print(f"--- ACTUAL OUTPUT ---\n{actual}")
                    print("TEST SESSION TERMINATED AFTER FIRST FAILURE")
                    return 1
                print("--- RESULT: PASSED ---")
    print(f"\nTEST SESSION PASSED: {len(cases)}/{len(cases)} cases")
    return 0


if __name__ == "__main__":
    sys.exit(main())
