---
name: test-ui
description: Run command-line UI test cases for this Java project by sending commands to the program, comparing each response with its expected output, stopping at the first mismatch, and reporting the console transcript. Use when asked to test the text UI or execute cases from test/ui-test-plan.md.
---

# Test UI

Use `test/ui-test-plan.md` as the persistent source of UI test cases and session setup.

## Preparing the test plan

- Accept test cases supplied as lists of commands and expected outputs.
- Before testing, record every supplied case in `test/ui-test-plan.md`. Give each case a unique ID and state its aim/rationale, input, and exact expected output.
- Preserve spaces, punctuation, capitalization, blank lines, and task order in expected output.
- If the user supplies no cases, run the cases already recorded in the plan.
- Do not change application code while performing a test-only request.

## Running a session

1. Read the whole test plan, including its setup and comparison rules.
2. Confirm that Java 25 is active, then compile the application using the commands recorded in the plan. Treat a compilation failure as a failed test session.
3. Start one fresh instance of the program. Keep it running while executing cases in plan order so state created by earlier commands is retained.
4. Capture the startup output. Then submit one test command at a time and capture its complete response before submitting another command.
5. Compare actual and expected output exactly. Normalize only the platform newline difference between CRLF and LF. Do not ignore indentation, repeated spaces, punctuation, blank lines, or horizontal separators.
6. If output matches, continue to the next case.
7. At the first mismatch, terminate the running program immediately. Do not execute later cases.
8. After the final passing case, send `bye` only when the plan includes it as a case; otherwise terminate the process after capturing the required output.

## Reporting

Always show a readable console transcript containing the input commands and all output captured during the session.

For a passing session, report how many cases passed. For a failure, identify the failed case and show its actual and expected output in separate fenced blocks. State that the remaining cases were not run. If compilation or startup fails, show the relevant console output and stop.
