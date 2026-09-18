---
name: test-ui
description: Run console UI test cases from test/ui-test-plan.md, compare each actual output with its expected output, and stop at the first failure.
---

# Test UI

Use this skill for deterministic, interactive console UI tests for the Java project.

## Test plan

Keep the test cases in [test/ui-test-plan.md](../../../test/ui-test-plan.md). Each test case must contain:

- an aim explaining what behavior is being checked;
- one command that starts the program;
- the console input, with one input line per command sent to the program; and
- the exact expected console output.

Represent each command as a separate test-case section. Keep prompts, separators, spaces, and blank lines in the expected-output code block when they are part of the UI contract.

## Run the tests

1. Read the complete test plan before running anything. Resolve relative program paths from the repository root.
2. Ensure the Java program is compiled with Java 25 and that the command in the plan launches the intended entry point.
3. Run the bundled runner:

   ```text
   python .codex/skills/test-ui/scripts/run_ui_tests.py test/ui-test-plan.md
   ```

   If `python` is unavailable, use the workspace's bundled Python executable or an equivalent Python 3 interpreter.
4. The runner executes test cases in document order. For each case it prints the command, console input, console output, and pass/fail result, then compares the captured output with the expected output.
5. If a case fails, stop immediately. Report the case title, aim, actual output, and expected output; do not run later cases.
6. Preserve the complete console session shown by the runner in the response. If an output file is requested, pass `--transcript <path>` and report its absolute path.

The comparison is exact apart from normalizing Windows and Unix line endings. The runner returns a nonzero exit status on a failed test, malformed test plan, or timeout.

