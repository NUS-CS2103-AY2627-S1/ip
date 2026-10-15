---
name: test-ui
description: Run this project's command-line UI test plan, compare every command response with its expected output, stop at the first mismatch, and show the complete console transcript. Use when asked to run, add, update, or review Jonk UI tests.
---

# Test UI

Use `test/ui-test-plan.md` as the source of truth for the test session.

## Prepare the plan

1. Read `AGENTS.md` and `test/ui-test-plan.md` from the repository root.
2. If the user supplies commands, expected outputs, or other test details, record them in `test/ui-test-plan.md` before running the tests. Preserve unrelated existing cases.
3. Ensure every test case has an aim, inputs, and expected output. Ask for missing expected behavior only when it cannot be inferred safely from the project requirements or current program.

## Run the tests

1. Follow the Java version and build instructions in `AGENTS.md`. Compile the program before starting the test session; treat a compilation failure as a failed session and report the compiler output.
2. Start one fresh instance of `Jonk` in an interactive terminal. Run the test cases once, in plan order, so application state carries from one command to the next.
3. Keep a complete transcript containing the startup output, every input command, and all console output.
4. For each test case, send its input and wait until the program requests another input or exits. Compare that command's response with the case's expected output before sending the next command.
5. Apply only the comparison normalization stated in the plan. Do not silently ignore other text or whitespace differences.

## Failure behavior

At the first mismatch or unexpected process exit:

1. Stop immediately. Do not run any later test case.
2. Terminate the running program if necessary.
3. Report the failed test-case ID and aim, its input, the expected output, and the actual output.
4. Show the complete console transcript up to the failure.

## Successful completion

After all cases pass, report the number of passed cases and show the complete console input/output transcript. Clearly label inputs in the transcript if the terminal did not echo them.
