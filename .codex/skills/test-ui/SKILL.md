---
name: test-ui
description: Run project UI test cases from test/ui-test-plan.md by feeding command-line inputs to the program and comparing exact console output. Use for CLI UI testing requests in this repo.
---

# Test UI

Use this skill to run end-to-end command-line UI tests for this Java project.

## Test Plan

Keep the authoritative test cases in `test/ui-test-plan.md`. Each test case must state:

- Aim: what behavior the test is checking.
- Command: the shell command that starts the program under test.
- Inputs: the console input lines to send to the program.
- Expected output: the exact console output expected from the program.

The plan should also contain a fenced JSON block marked `ui-tests`. The helper script reads this block and expects an array of test case objects with `name`, `aim`, `command`, `input`, and `expected_output` fields. `input` can be either a string or an array of strings. Include final newline characters in `expected_output` when the program prints a trailing newline.

## Running Tests

1. Read `test/ui-test-plan.md` before running tests. If the user's requested cases are not recorded there yet, update the file first.
2. If the terminal is not using Java 25, switch to the installed Java 25 before compiling or running the project.
3. Run:

   ```bash
   python3 .codex/skills/test-ui/scripts/run-ui-tests.py test/ui-test-plan.md
   ```

4. The helper runs test cases in the order listed. It stops immediately at the first failure.
5. After testing, report the console session record printed by the helper so the user can see the command, inputs, and outputs for each completed test.
6. If a test fails, report the failed test name, the actual output, and the expected output. Do not continue to later test cases after a failure.

Use exact output comparison by default. Only normalize output when the user explicitly asks for looser matching.
