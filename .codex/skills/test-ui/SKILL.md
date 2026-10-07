---
name: test-ui
description: Run command-line UI test cases for this Java chatbot from a command and expected-output list, stopping at the first failure and recording the session.
---

# Test the chatbot UI

Use this project-specific skill when the user provides UI test cases consisting of commands and expected output for the chatbot.

## Test plan

Keep the test cases in [`test/ui-test-plan.md`](../../../test/ui-test-plan.md). Each case must include:

- an aim;
- the exact input commands, one command per line;
- the expected output, including relevant separators, prompts, status markers, and farewell text.

When the user supplies new or changed cases, update the plan before running the tests. Do not silently change expected output to match the program.

## Running tests

1. Read the test plan and identify the commands and expected output for each case.
2. Compile the Java program using the repository's configured Java version. The repository requires Java 25; if Java 25 is unavailable, report that limitation before testing.
3. Run each test case as a separate process, feeding its commands through standard input. Capture the complete console output, including the initial banner and final farewell.
4. Compare actual output with expected output exactly, apart from line-ending differences. Preserve spaces, blank lines, separators, and Unicode characters when comparing.
5. Stop immediately after the first failed test case. Do not run later cases after a failure.
6. After a successful test session, add a session record to `test/ui-test-plan.md` containing the date, commands entered, and complete console input/output. If a case fails, record the failure and include both the actual and expected output, clearly labeled.
7. Remove generated `.class` files after testing unless the project already tracks build output.

## Report

In the final response, state which test cases passed. Include the recorded console session, or point to its location in `test/ui-test-plan.md`. For a failure, state the first failed case and show the actual and expected outputs; do not describe later cases as tested.
