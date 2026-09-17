---
name: test-ui
description: Run and maintain PotatoBot console UI tests that compare ordered command inputs with exact expected replies. Use when Codex needs to test command handling, verify user-visible console output, reproduce a UI regression, or add cases to test/ui-test-plan.md.
---

# Test UI

Use the deterministic runner to test PotatoBot's console interaction against the project test plan.

## Workflow

1. Read `test/ui-test-plan.md` completely before changing or running tests.
2. When given new commands and expected outputs, add a test case to the JSON block. Include a unique ID, aim, and ordered steps containing `input` and `expected_output`.
3. Preserve existing test cases unless the user explicitly changes the expected behavior.
4. From the repository root, run:

   ```shell
   python skills/test-ui/scripts/run_ui_tests.py
   ```

5. Show the emitted console-session record in the result. If a test fails, stop at that failure and report its input, actual output, and expected output.

## Testing rules

- Require Java 25 and build with the command recorded in the test plan.
- Run each scenario from a clean PotatoBot process.
- Replay earlier steps in the same scenario before checking the next step so task-list state is preserved.
- Compare replies exactly after normalizing line endings and removing only surrounding whitespace.
- Do not weaken or silently update an expectation merely to make a failing test pass.
- Diagnose a failure before modifying application code; testing alone does not authorize a fix.
