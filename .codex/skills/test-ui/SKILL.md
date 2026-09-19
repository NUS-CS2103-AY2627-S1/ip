---
name: test-ui
description: Run the Benji console UI test plan and compare each command session with its expected output. Use after changing console behavior or when asked to verify the chatbot's user interface.
---

# Test UI

Run the UI regression tests defined in `test/ui-test-plan.md`.

1. Read the test plan before testing. Each test must include an aim, input, and complete expected output. Trailing spaces at the end of a line are ignored because they are not visible in the console.
2. From the repository root, run:

   ```bash
   python3 .codex/skills/test-ui/scripts/run_ui_tests.py
   ```

3. Report the test session printed by the script. The session includes the console input and actual output.
4. If a test fails, stop immediately. Report the actual and expected output shown by the script; do not continue to later tests.
5. Update the test plan when an intentional UI change changes expected output. Do not change expected output merely to hide an unexpected regression.

The runner compiles the application with the active Java 25 toolchain and uses only the Python standard library.
