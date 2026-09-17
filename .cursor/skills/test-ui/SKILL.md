---
name: test-ui
description: Run Mouse chatbot UI tests from test/ui-test-plan.md. Accepts lists of commands and expected outputs, runs the program, fail-fast compares output, and prints the console session. Use when asked to run UI tests, test-ui, text UI tests, or verify chatbot input/output.
---

# Test UI

Run the Mouse chatbot against the test cases in `test/ui-test-plan.md`. The skill accepts lists of commands and expected outputs. For each command list (test case), it runs the program and checks the output against the expected output.

The list of test cases (and other relevant information) is recorded in the `test/ui-test-plan.md` file. Each test case specifies the aim of the test case, inputs, and the expected output.

After testing, show a record of the console input and output so we can see the test session. If a test case failed, terminate the test session immediately, and report the actual and expected outputs.

## Run tests

1. Work from the `ip` repository root (the folder that contains `src/main/java` and `test/ui-test-plan.md`).
2. Use Java 25 (`25.0.3.fx-zulu`). If needed: `sdk use java 25.0.3.fx-zulu`.
3. If the user added or changed cases, update `test/ui-test-plan.md` first. Do not hard-code cases in this skill.
4. Run the bundled runner:

   ```bash
   python3 .cursor/skills/test-ui/scripts/run-ui-tests.py
   ```

5. Print the runner's full session record in your reply.
6. If a test case failed, stop. Do not run later cases or "fix forward". Report the actual and expected outputs from the runner, including stderr if the program crashed. Only continue if the user asks to update the plan or the program.
7. If the user supplies new commands and expected outputs, append a new test case to `test/ui-test-plan.md` (aim, inputs, expected output) and re-run.

## Test case rules

- One new `mouse.Mouse` process per test case.
- Inputs are sent in order, one line each. Include `bye` when the case should exit cleanly.
- Expected output is exact console text (spacing and horizontal lines included).
- The first failing test case ends the session.

## Resource

`scripts/run-ui-tests.py` compiles every `src/main/java/**/*.java` file, reads `test/ui-test-plan.md`, fail-fast compares output, and prints stderr on crashes.
