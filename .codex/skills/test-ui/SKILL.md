---
name: test-ui
description: Run and verify this project's command-line UI test cases from test/ui-test-plan.md after code changes that affect behaviour.
---

# Test UI

Use this skill to regression-test the TBlade console interface.

1. Update `test/ui-test-plan.md` when a behaviour, command, or user-facing message has changed. Each test case must state its aim, console inputs, and the complete expected console output.
2. Run `python3 .codex/skills/test-ui/scripts/run_ui_tests.py` from the project root.
3. Review the printed console input and output for every passing test. The runner terminates at the first failed test and prints the expected and actual outputs; fix the issue or update the plan only when the intended UI has changed.

The runner compares output exactly, including whitespace and the pig emoji. Keep date and time values in test cases as plain strings.
