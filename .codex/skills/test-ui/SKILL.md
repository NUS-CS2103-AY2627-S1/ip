---
name: test-ui
description: "Run and report the project's command-line UI tests from test/ui-test-plan.md after behavior changes."
---

# Test UI

Use this skill to verify observable command-line behavior after a code update.

1. Read `test/ui-test-plan.md`. Keep it current when behavior, commands, or expected output changes.
2. Run each test case in plan order. Compile into an isolated temporary directory so generated `.class` files do not enter the repository.
3. Supply the listed console input exactly, capture standard output and standard error, and compare the actual output with the expected output exactly (apart from the final line ending).
4. After each passing case, show a test transcript containing the command, console input, and console output.
5. On the first failure, stop immediately. Report the test case aim and both the expected and actual outputs; do not continue to later cases.

Do not commit generated files. The test plan is the source of truth for the test cases.
