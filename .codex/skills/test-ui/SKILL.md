---
name: test-ui
description: Run planned command-line UI test cases, compare console output with expected output, and report a fail-fast test transcript. Use for executing or reviewing the project's interactive UI tests.
---

# Test UI

Run the test cases defined in `test/ui-test-plan.md`. Treat the plan as the source of truth for how to launch the program, its console inputs, and its expected output.

## Test-plan format

Each test case must contain:

- **Aim** — the behavior being checked.
- **Inputs** — the ordered commands or other console input to send to the program.
- **Expected output** — the complete expected console output, including prompts and line breaks where relevant.

Use a fenced text block for both Inputs and Expected output so whitespace and command order are unambiguous. Add launch/build instructions under **Test environment** when they are needed to run the program.

## Run tests

1. Read `test/ui-test-plan.md`. If it has no test cases, ask the user to add them; do not invent expected behavior.
2. Follow its Test environment instructions. Run the program in a fresh session for every test case unless the plan explicitly says a case continues a prior session.
3. Send the listed Inputs in order and capture all console output exactly as displayed. Include the program-launch command and every entered input in the transcript.
4. Compare the actual output with Expected output exactly, unless that test case explicitly defines a narrower comparison rule.
5. On the first failure, stop immediately. Report the test-case name and aim, then show the expected output, actual output, and the complete console transcript. Do not run later test cases.
6. If all tests pass, report each result and show the complete input/output transcript for every session.

Keep transcripts in the response for the current test run; do not alter the test plan with actual results unless the user asks.
