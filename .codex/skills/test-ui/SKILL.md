---
name: test-ui
description: Run and verify console UI test cases defined in this project's test/ui-test-plan.md. Use when executing command-line interface tests with specified input and expected output.
---

# Test UI

Run the console UI tests recorded in [`test/ui-test-plan.md`](../../../test/ui-test-plan.md). The plan is the source of truth for test commands, input, and expected output.

## Test plan format

The plan is a list of test cases. Each case represents one command that runs the program and must contain all of these fields:

- **Aim:** what behavior the test checks.
- **Command:** the command that starts the program for this test.
- **Input:** the exact text to send to standard input. Use `(none)` when no input is required.
- **Expected output:** the exact console output expected from the program.

Put commands, input, and output in fenced text blocks. Keep the expected output free of terminal prompts, shell commands, and explanatory text.

## Run the tests

1. Read the plan before running anything. If it has no concrete test cases, ask the user to supply the cases; do not invent expected output.
2. Ensure Java 25 is active before compiling or running the project. On this macOS project, use `sdk use java 25.0.3.fx-zulu` when needed.
3. Execute test cases (commands) in plan order. For each case, run its command, supply the **Input** block exactly, and capture the program's console output. Perform any necessary build step before the first test, but do not treat build output as UI output unless the plan explicitly makes it a test case.
4. Compare actual output with **Expected output** exactly. You may normalize only line endings (`CRLF` versus `LF`); do not trim whitespace or otherwise change either output.
5. After every passing case, report its captured input and output in the test-session record. In the final response, show the same console transcript so the test run can be reviewed.

## Failure handling

On the first failing case, immediately stop: do not run later cases. Record and report:

- the test case name and aim;
- the exact input supplied;
- expected output; and
- actual output.

Clearly mark the test session as failed. If every case matches, state that the session passed and include the transcript for every test case.
