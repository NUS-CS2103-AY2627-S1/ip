"""Run console UI test cases defined in test/ui-test-plan.md."""
#Used AI to generate test file
import json
import re
import shutil
import subprocess
import sys
from pathlib import Path


PROJECT_ROOT = Path(__file__).resolve().parents[4]
PLAN_PATH = PROJECT_ROOT / "test" / "ui-test-plan.md"


def normalize(text):
    """Use one line-ending style so tests work on every platform."""
    return text.replace("\r\n", "\n").replace("\r", "\n")


def load_plan():
    """Extract the JSON test plan from its Markdown document."""
    plan_text = PLAN_PATH.read_text(encoding="utf-8")
    match = re.search(r"```json\s*(\{.*?\})\s*```", plan_text, re.DOTALL)
    if match is None:
        raise ValueError("No JSON test plan was found in " + str(PLAN_PATH))
    return json.loads(match.group(1))


def run(command, inputs=""):
    """Run a process and capture its text input and output."""
    return subprocess.run(command, cwd=PROJECT_ROOT, input=inputs, text=True,
                          capture_output=True, check=False)


def expected_output(test_case):
    """Convert the plan's literal output lines into console text."""
    return "\n".join(test_case["expected_output"]) + "\n"


def print_transcript(test_case, output):
    """Show the console session for a passing test case."""
    print("\n=== " + test_case["id"] + ": " + test_case["aim"] + " ===")
    print("Console input:")
    print(test_case["inputs"], end="" if test_case["inputs"].endswith("\n") else "\n")
    print("Console output:")
    print(output, end="" if output.endswith("\n") else "\n")


def main():
    """Build the project and stop at the first UI mismatch."""
    plan = load_plan()
    build = run(plan["build_command"])
    if build.returncode != 0:
        print("Build failed.\n" + build.stderr, file=sys.stderr)
        return 1

    for test_case in plan["tests"]:
        result = run(test_case["command"], test_case["inputs"])
        actual = normalize(result.stdout)
        expected = normalize(expected_output(test_case))
        if result.returncode != 0 or actual != expected:
            print("\nFAIL: " + test_case["id"] + " — " + test_case["aim"])
            print("Console input:\n" + test_case["inputs"])
            print("Expected output:\n" + expected)
            print("Actual output:\n" + actual)
            if result.stderr:
                print("Standard error:\n" + result.stderr)
            print("Exit code: " + str(result.returncode))
            return 1
        print_transcript(test_case, actual)

    print("\nAll UI test cases passed.")
    return 0


if __name__ == "__main__":
    try:
        raise SystemExit(main())
    finally:
        shutil.rmtree(PROJECT_ROOT / ".test-ui-build", ignore_errors=True)
