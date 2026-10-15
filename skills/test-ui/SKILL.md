---
name: test-ui
description: Run the command-line UI test cases recorded in test/ui-test-plan.md and compare each session with its expected output.
---

# UI testing

Use this project-specific skill after changing application code or when the user asks to verify the command-line UI.

1. Read `test/ui-test-plan.md`. Each test case must contain an `Aim`, an `Inputs` fenced block, and an `Expected output` fenced block. The plan also defines the build and program commands.
2. Run the repository runner:

   ```text
   python3 skills/test-ui/scripts/run_ui_tests.py
   ```

   The runner builds the program, executes test cases in document order, and compares every visible stdout line with its expected output. Trailing box-padding spaces are ignored.
3. If a test fails, stop immediately. Report the test case, actual output, and expected output; do not continue to later cases.
4. After the run, inspect the `Latest test session` section appended to `test/ui-test-plan.md`. It records the console input and output for every case reached, including a failing case.

Keep the plan synchronized with the current UI contract. If a deliberate UI change makes an expected output obsolete, update the plan before running the tests. Do not hide regressions by weakening comparisons to substring checks.
