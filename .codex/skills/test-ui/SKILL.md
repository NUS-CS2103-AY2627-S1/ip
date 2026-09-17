---
name: test-ui
description: Run the project's console jonathan.ui.UI regression tests from test/ui-test-plan.md after chatbot behavior changes.
---

# jonathan.ui.UI Test

Use this skill after changing chatbot code that can affect console input or output.

1. Read `test/ui-test-plan.md`. Update it when the supported commands or their expected output changes.
2. From the project root, run:

   ```bash
   python3 .codex/skills/test-ui/scripts/run_ui_tests.py
   ```

3. Review the printed console-input and console-output record for every passing test.
4. If a test fails, stop immediately. Report the test aim and the script's expected-versus-actual output. Do not describe the code update as verified.

The test plan is the source of truth for test cases. Each case must include an aim, input lines, and the complete expected program output in fenced code blocks. For persistence behavior, add an `Expected saved data` fenced block too.
