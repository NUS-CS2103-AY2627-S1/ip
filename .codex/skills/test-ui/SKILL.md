---
name: test-ui
description: Run fail-fast console UI acceptance tests from command lists and exact expected outputs, especially test cases recorded in test/ui-test-plan.md.
---

# Test the console UI

Use this skill when the user asks to execute or extend command-driven UI acceptance tests.

1. Read `test/ui-test-plan.md`. Treat it as the human-readable source of the test aims, inputs, expected outputs, execution settings, and comparison rules.
2. Accept test cases supplied by the user as lists of console commands and expected output lines. If tests are being retained for this project, record each case's aim, inputs, and exact expected output in the plan and keep `test/ui-test-cases.json` synchronized as the runner's machine-readable form.
3. Compile and run with Java 25. Do not silently use a different Java version. Use the main class and class path documented by the plan.
4. Run `scripts/run_ui_tests.ps1` with the JSON case file. It starts a fresh program process for each case, sends its command list, and compares the complete normalized stdout with the expected lines.
5. Stop at the first failed case. Do not execute remaining cases after a mismatch or process error.
6. Show the runner's console-session record after testing. On failure, report the case, actual output, and expected output; on success, report the number of cases passed.

Exact comparison ignores only line-ending differences and a single final newline. Do not loosen matching unless the plan explicitly specifies a different rule.
