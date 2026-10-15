---
name: test-ui
description: Run this project’s command-line UI tests from test/ui-test-plan.md, comparing each actual output with its expected output and stopping at the first failure.
---

# Test UI

Use this skill when the user provides, requests, or asks to update lists of commands and expected console output for the Java application.

## Workflow

1. Read `test/ui-test-plan.md`. Each test case must include an aim, console inputs, and expected output.
2. Treat the commands in one test case as one console session. Build the application using the project’s configured build tool when available; otherwise compile the Java sources into a temporary directory. Use Java 25 when running or compiling.
3. Run test cases in document order. Supply the listed inputs exactly, capture stdout and stderr, and compare the captured output with the expected output. Preserve meaningful whitespace and line breaks; normalize only the line-ending convention.
4. After each test case, show a console record containing the input and actual output, and report PASS or FAIL.
5. If a test case fails, stop immediately. Report the first failing case, its aim, the exact actual output, and the exact expected output. Do not continue with later cases.
6. Do not change application code merely to make a test pass. If the test plan is missing, malformed, or ambiguous, explain the issue before testing.

## Updating the plan

When asked to add or change tests, edit `test/ui-test-plan.md`. Keep each case self-contained and specify the full expected output relevant to the behavior under test, including prompts, separators, and termination output when applicable.
