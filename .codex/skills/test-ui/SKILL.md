---
name: test-ui
description: Test this project's Yappy console UI by running command lists from test/ui-test-plan.md and checking expected output fragments.
---

# Test UI

Use this skill after code changes that affect the Yappy console app.

## Workflow

1. Read `test/ui-test-plan.md` and update it when the expected console behavior changes.
   Include negative cases for invalid inputs when command parsing or error handling changes. Interleave valid and invalid commands when useful so the test can detect accidental state changes after an error.
2. Run `test/run-ui-tests.ps1` from the project root. The script compiles the Java sources, runs each command list in the test plan, and checks that the expected output fragments appear in order.
3. If a test fails, stop immediately and report the test case name, the missing or out-of-order expected output, and the actual console output.
4. If all tests pass, summarize the test session and mention that `test/ui-test-session.md` contains the recorded console input/output.

The test plan records expected fragments instead of the full banner and divider text so tests stay focused on user-visible task behavior.
