---
name: test-ui
description: Run command-line Ui test cases from explicit commands and expected outputs, recording the plan and console transcript for this Java project.
---

# Test Ui

Use this skill when the user provides, requests, or wants to run command-line Ui test cases for this project. The test cases are described as commands to run and expected console output.

## Test Plan

Record the test cases and relevant setup details in `test/ui-test-plan.md` before running tests. If the file does not exist, create it. Keep it readable for beginner Java students.

Each test case must include:

- Aim: what behavior the test is checking.
- Command: the shell command used to start the program.
- Inputs: the console input sent to the program, in order.
- Expected output: the output that should appear for that test.

Also record any relevant setup, such as the Java version used, compilation command, working directory, or assumptions about saved data.

## Running Tests

For each test case, run the program exactly as specified, feed the listed inputs, and compare the actual console output with the expected output.

- Run test cases in the order listed unless the user explicitly asks otherwise.
- Use Java 25 for Java build or run commands. On macOS, switch with `sdk use java 25.0.3.fx-zulu` if needed.
- Preserve line breaks and meaningful spacing when comparing output. Ignore only incidental shell prompts or wrapper text that was not produced by the program.
- If a test case passes, continue to the next test case.
- If a test case fails, stop immediately. Report the failing test case, actual output, and expected output. Do not continue to later tests.

## Reporting

After testing, show a record of the console input and output so the test session can be inspected. Include the command that was run and the transcript for each executed test case.

For passing tests, summarize which test cases passed and point to the updated `test/ui-test-plan.md`.

For a failing test, include:

- The test case name or number.
- The command and inputs used.
- The expected output.
- The actual output.
- The first meaningful difference, when it is easy to identify.
