---
name: test-ui
description: Run project UI command tests for the Kong chatbot using cases recorded in test/ui-test-plan.md.
---

# Test UI

Use this skill when the user asks to run or update UI-style command tests for this Kong chatbot project.

The test cases live in `test/ui-test-plan.md`. Each test case must include:

- an aim
- console input commands
- expected console output snippets

When adding or changing tests, update `test/ui-test-plan.md` first. Keep the expected output focused on the behavior being tested instead of copying the whole banner unless the banner itself is under test.

## Running Tests

Run the helper from the project root:

```bash
python3 .codex/skills/test-ui/scripts/run_ui_tests.py
```

The helper compiles the Java files into a temporary directory, runs each test case as a fresh program session, and checks that the expected output snippets appear in order.

After testing, report the console transcript shown by the helper so the user can see the test session.

If a test fails:

- stop immediately
- report the failed test case name
- show the console input
- show the actual output
- show the missing or mismatched expected output

Do not continue to later test cases after the first failure.
