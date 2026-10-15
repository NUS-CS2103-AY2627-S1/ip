#!/usr/bin/env python3
"""Run the console UI cases defined in test/ui-test-plan.md."""

import argparse
import re
import subprocess
import sys
from pathlib import Path


CONFIG_PATTERN = re.compile(
    r"(?m)^- (?P<name>Required Java version|Compile command|Run command): `(?P<value>[^`]+)`$")
CASE_PATTERN = re.compile(
    r"(?ms)^### (?P<name>.+?)\n\n"
    r"Aim: (?P<aim>.+?)\n\n"
    r"Inputs:\n```text\n(?P<input>.*?)\n```\n\n"
    r"Expected output:\n```text\n(?P<expected>.*?)\n```(?=\n+(?:### |\Z))")


def normalize_output(output: str) -> str:
    """Normalise platform line endings and an optional final newline."""
    return output.replace("\r\n", "\n").rstrip("\n")


def print_block(title: str, content: str) -> None:
    """Print one clearly labelled section of the test transcript."""
    print(f"{title}:")
    print(content if content else "<empty>")


def run_command(command: str, root: Path, input_text: str = "") -> subprocess.CompletedProcess[str]:
    """Run a plan command from the repository root and capture its output."""
    return subprocess.run(
        command,
        cwd=root,
        shell=True,
        input=input_text,
        text=True,
        capture_output=True,
        check=False,
    )


def load_plan(plan_path: Path) -> tuple[dict[str, str], list[dict[str, str]]]:
    """Extract configuration and test cases from a UI test plan."""
    plan_text = plan_path.read_text(encoding="utf-8")
    configuration = {match["name"]: match["value"] for match in CONFIG_PATTERN.finditer(plan_text)}
    required = {"Required Java version", "Compile command", "Run command"}
    missing_configuration = required.difference(configuration)
    if missing_configuration:
        missing = ", ".join(sorted(missing_configuration))
        raise ValueError(f"The test plan is missing configuration: {missing}.")

    cases = [match.groupdict() for match in CASE_PATTERN.finditer(plan_text)]
    if not cases:
        raise ValueError("The test plan contains no valid test cases.")
    return configuration, cases


def verify_java_version(required_version: str) -> bool:
    """Return whether the active compiler uses the Java major version in the plan."""
    version_result = subprocess.run(
        ["javac", "-version"], text=True, capture_output=True, check=False)
    version_text = (version_result.stdout + version_result.stderr).strip()
    print(f"Compiler: {version_text}")
    if version_result.returncode != 0 or not re.search(rf"\b{re.escape(required_version)}(?:\.|\b)", version_text):
        print(f"Expected Java version: {required_version}")
        return False
    return True


def reset_storage(root: Path) -> None:
    """Remove ET's generated storage file so UI cases remain independent."""
    storage_file = root / "data" / "tasks.txt"
    storage_file.unlink(missing_ok=True)
    try:
        storage_file.parent.rmdir()
    except OSError:
        pass


def main() -> int:
    """Run every documented UI test until one fails."""
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument(
        "--plan",
        default="test/ui-test-plan.md",
        help="path to the Markdown UI test plan, relative to the repository root",
    )
    arguments = parser.parse_args()
    root = Path.cwd()
    plan_path = root / arguments.plan

    try:
        configuration, cases = load_plan(plan_path)
    except (OSError, ValueError) as error:
        print(f"Unable to load UI test plan: {error}")
        return 2

    print("=== UI Test Session ===")
    print(f"Plan: {arguments.plan}")
    if not verify_java_version(configuration["Required Java version"]):
        return 1

    print(f"Compile command: {configuration['Compile command']}")
    compilation = run_command(configuration["Compile command"], root)
    if compilation.returncode != 0:
        print("Compilation failed; test session terminated.")
        print_block("Compiler output", compilation.stdout + compilation.stderr)
        return 1

    for number, case in enumerate(cases, start=1):
        print(f"\n=== Test {number}: {case['name']} ===")
        print(f"Aim: {case['aim']}")
        print_block("Console input", case["input"])

        reset_storage(root)
        result = run_command(configuration["Run command"], root, case["input"] + "\n")
        actual_output = normalize_output(result.stdout)
        expected_output = normalize_output(case["expected"])
        print_block("Console output", actual_output)

        if result.returncode != 0 or actual_output != expected_output:
            print("Result: FAIL — test session terminated.")
            if result.returncode != 0:
                print(f"Program exit code: {result.returncode}")
                print_block("Program error output", result.stderr)
            print_block("Expected output", expected_output)
            print_block("Actual output", actual_output)
            return 1

        print("Result: PASS")

    print(f"\nAll {len(cases)} UI test case(s) passed.")
    return 0


if __name__ == "__main__":
    sys.exit(main())
