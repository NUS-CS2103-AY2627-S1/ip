---
name: test-ui
description: "Run this project's command-line UI test cases from test/ui-test-plan.md, compare exact outputs, and stop at the first failure."
---

# Test UI

Use this skill when the user asks to run or update the project's console UI tests.

## Workflow

1. Read `test/ui-test-plan.md`. Each test case must include an aim, a fenced input block containing one command per line, and a fenced expected-output block containing the program's stdout. The expected output must not include terminal input echo.
2. If the user supplies test cases in the request, add or update them in `test/ui-test-plan.md` before running the tests. Preserve existing cases unless the user asks to replace them.
3. Run the test runner from the project root:

   ```text
   python3 .codex/skills/test-ui/scripts/run_ui_tests.py --plan test/ui-test-plan.md
   ```

   The runner compiles the Java sources with Java 25, starts a fresh `Panda` process for each test case, sends that case's commands on stdin, and compares stdout exactly after normalizing only line endings.
4. Show the console input and actual console output for every completed test case. Do not summarize a passing session without showing this record.
5. Stop immediately when compilation fails, a process exits unsuccessfully, or output differs. Report the test case, aim, input, expected output, and actual output. Do not run later cases after a failure.

Do not modify application source code merely to make a test pass. If a test fails, report the mismatch and ask whether the user wants the implementation or the test plan corrected.

The test plan is the maintained record of UI coverage. Keep expected output synchronized with intentional user-visible changes.

Maintain the project's JUnit coverage target of roughly 50% across the highest-value methods. After every code change, review and update the JUnit tests in `src/test/java` so that changing logic remains covered and the target is still met.
