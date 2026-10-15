---
name: test-ui
description: Run project-specific UI transcript tests for the Java chatbot after code updates, using test/ui-test-plan.md as the source of test cases and expected outputs.
---

# test-ui

Use this skill after changing the chatbot code or command behavior.

Workflow:

1. Update `test/ui-test-plan.md` if the expected UI behavior changed.
2. Run `python3 test/run-ui-tests.py` from the project root.
3. If a test fails, stop and report the failed case, expected output, and actual output.
4. If all tests pass, report the console transcript summary from `test/ui-test-session.log`.

The test plan is the source of truth. Each test case must include:

- Aim
- Inputs
- Expected Output

