---
name: test-ui
description: Run project-specific console UI tests for this Java chatbot from test/ui-test-plan.md. Use when the user asks to test UI behavior, verify command/output examples, run acceptance tests, or check console input and output against expected transcripts.
---

# Test UI

Use this skill to run transcript-style UI tests for the Java chatbot in this repository.

## Test Plan

Read test cases from `test/ui-test-plan.md`. Each test case must include:

- Aim: what behavior the test verifies.
- Inputs: one command per line, including `bye` if the session should exit.
- Expected output: the complete expected console output for the program run.

Use fenced code blocks labeled `text` under `Inputs` and `Expected output`.

## Workflow

1. Inspect `test/ui-test-plan.md`. If it does not exist, create it with the test cases needed for the requested behavior.
2. Ensure each test case includes an aim, inputs, and expected output.
3. Run:

   ```bash
   python3 .codex/skills/test-ui/scripts/run-ui-tests.py
   ```

4. If the script reports a failure, stop immediately. Report the failed test name, aim, inputs, expected output, and actual output.
5. If all tests pass, report the test count and show the console transcript recorded by the script.

## Notes

- The script compiles files from `src/main/java` into `/tmp/habpyduck-ui-test-classes`.
- The script feeds each test case input to `HabpyDuck` through standard input.
- The comparison ignores trailing whitespace at line ends, but otherwise expects the output to match.
- Use Java 25 for compilation and execution. On macOS, if needed, run `sdk use java 25.0.3.fx-zulu` before testing.
