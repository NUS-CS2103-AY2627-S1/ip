#!/usr/bin/env python3
"""Run PotatoBot console UI tests recorded in test/ui-test-plan.md."""

import json
import os
import re
import shutil
import subprocess
import sys
import tempfile
from pathlib import Path


PROJECT_ROOT = Path(__file__).resolve().parents[3]
TEST_PLAN = PROJECT_ROOT / "test" / "ui-test-plan.md"


def normalize(output):
    """Normalize platform line endings and insignificant surrounding whitespace."""
    return output.replace("\r\n", "\n").strip()


def load_test_plan():
    """Load the JSON test definition embedded in the Markdown test plan."""
    plan_text = TEST_PLAN.read_text(encoding="utf-8")
    match = re.search(r"```json\s*\n(.*?)\n```", plan_text, re.DOTALL)
    if match is None:
        raise ValueError(f"No JSON test definition found in {TEST_PLAN}")
    return json.loads(match.group(1))


def run_process(command, console_input=None, environment=None):
    """Run a project command and capture its console streams."""
    return subprocess.run(
        command,
        cwd=PROJECT_ROOT,
        env=environment,
        input=console_input,
        text=True,
        capture_output=True,
        check=False,
    )


def extract_reply(console_output, command_count):
    """Extract the reply corresponding to the last submitted command."""
    sections = console_output.replace("\r\n", "\n").split("Me: ")
    if len(sections) <= command_count:
        raise ValueError("Could not find PotatoBot's reply in the console output")
    return normalize(sections[command_count])


def fail(test_id, step_number, command_input, expected, actual):
    """Report the first failed step and terminate the test session."""
    print(f"FAIL {test_id}, step {step_number}")
    print("INPUT")
    print(f"> {command_input}")
    print("EXPECTED OUTPUT")
    print(expected)
    print("ACTUAL OUTPUT")
    print(actual)
    sys.exit(1)


def main():
    """Build PotatoBot and execute every UI test in fail-fast order."""
    try:
        plan = load_test_plan()
    except (OSError, ValueError, json.JSONDecodeError) as exception:
        print(f"Test-plan error: {exception}", file=sys.stderr)
        return 1

    version_result = run_process(["java", "-version"])
    version_output = version_result.stdout + version_result.stderr
    if version_result.returncode != 0 or not re.search(r'version "25(?:\.|\")', version_output):
        print("FAIL: test-ui requires Java 25.", file=sys.stderr)
        print(version_output.strip(), file=sys.stderr)
        return 1

    build_command = plan["build_command"]
    print("BUILD")
    print("$ " + " ".join(build_command))
    build_result = run_process(build_command)
    if build_result.returncode != 0:
        print("BUILD FAILED")
        print((build_result.stdout + build_result.stderr).strip())
        return 1
    print("Build passed.\n")

    run_command = plan["run_command"]
    test_data_directory = Path(tempfile.mkdtemp(prefix="potatobot-ui-", dir=PROJECT_ROOT / "build"))
    test_save_file = test_data_directory / "potatobot.txt"
    test_environment = os.environ.copy()
    test_environment["POTATOBOT_SAVE_FILE"] = str(test_save_file)
    passed_steps = 0

    try:
        for test_case in plan["test_cases"]:
            test_id = test_case["id"]
            print(f"TEST {test_id}: {test_case['aim']}")
            command_history = []

            for step_number, step in enumerate(test_case["steps"], start=1):
                command_input = step["input"]
                expected = normalize(step["expected_output"])
                command_history.append(command_input)
                session_input = "\n".join(command_history + ["bye"]) + "\n"

                test_save_file.unlink(missing_ok=True)
                initial_save_file = test_case.get("initial_save_file")
                if initial_save_file is not None:
                    test_save_file.write_text(initial_save_file, encoding="utf-8")

                run_result = run_process(run_command, session_input, test_environment)

                if run_result.returncode != 0:
                    actual = normalize(run_result.stdout + run_result.stderr)
                    fail(test_id, step_number, command_input, expected, actual)

                try:
                    actual = extract_reply(run_result.stdout, len(command_history))
                except ValueError as exception:
                    fail(test_id, step_number, command_input, expected, str(exception))

                print(f"> {command_input}")
                print(actual)

                if actual != expected:
                    fail(test_id, step_number, command_input, expected, actual)

                print("PASS\n")
                passed_steps += 1
    finally:
        shutil.rmtree(test_data_directory, ignore_errors=True)

    print(f"ALL UI TESTS PASSED ({passed_steps} command checks)")
    return 0


if __name__ == "__main__":
    sys.exit(main())
