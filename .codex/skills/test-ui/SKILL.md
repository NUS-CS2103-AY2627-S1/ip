---
name: test-ui
description: Run ordered command-and-response UI tests for this Java chatbot, record test definitions in test/ui-test-plan.md, preserve a console transcript, and stop at the first output mismatch. Use when adding, reviewing, or executing text-based chatbot UI test cases.
---

# Test UI

Use `test/ui-test-plan.md` as the source of truth for UI test definitions and test configuration.

## Prepare the plan

1. Read `AGENTS.md` and `test/ui-test-plan.md` before compiling or running the program.
2. When the user supplies commands and expected outputs, record them in the plan before testing. Preserve expected output verbatim; never derive or update it from actual output.
3. Give every test case a unique ID and specify its aim, ordered inputs, and an ordered expected-output block for every input.
4. Require the input and expected-output lists to have the same length. If they do not, stop before running the program and ask the user to correct the missing entry.
5. Infer a concise aim when the commands make it unambiguous. Ask the user only when choosing an aim would change what is being tested.

Follow the test case schema already documented in the plan. Each test case is one stateful chatbot session; start a fresh process for the next test case.

## Build and run

1. Use Java 25 as required by `AGENTS.md`. Verify both `java -version` and `javac -version` before testing.
2. Use the compile and run commands from the plan. If they are stale, inspect the project and update the configuration without changing any test's expected output.
3. Compile into a new directory created with `mktemp -d`; do not emit `.class` files into `src/`.
4. If compilation fails, report the compiler output and do not start a test session.
5. Run the program in a PTY so the transcript includes both console input and output.

## Execute and compare

For each test case, in document order:

1. Start a fresh program process and record its startup output.
2. Send each input exactly as written, followed by one newline. Wait until the program reaches its next input prompt or exits before comparing.
3. Record all terminal text in order for the final transcript.
4. For comparison only, remove the PTY's exact echo of the submitted input and normalize CRLF to LF in both actual and expected text. Do not trim or otherwise normalize spaces, blank lines, prompts, punctuation, or trailing newlines.
5. Compare the program output caused by that input with its paired expected-output block.
6. If they differ, immediately terminate the running process, skip every remaining input and test case, and report the failing test ID and input with separate fenced `Expected output` and `Actual output` blocks.
7. If an input causes the program to exit, confirm that it exits normally and ensure it is the final input in that test case.

Do not edit application code while running this testing workflow unless the user separately asks for a fix.

## Report

After a successful run, report how many test cases and command-response checks passed. After either success or failure, show a fenced `text` transcript containing the complete console input and output captured up to that point. Clearly mark any inputs or test cases not run because of a failure.
