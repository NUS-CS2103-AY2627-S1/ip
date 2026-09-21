---
name: test-ui
description: Run Janet command-line UI tests defined in this project's test/ui-test-plan.md, comparing each actual output with its expected output and stopping at the first failure.
---

# Janet UI testing

Use this project-specific skill when testing Janet’s console interaction.

1. Read `test/ui-test-plan.md`. Each test case must contain an aim, an `Inputs` fenced block, and an `Expected output` fenced block.
2. Run `scripts/run_ui_tests.py` from the repository root. It compiles the Java sources with Java 25, runs each case, and compares output exactly after normalizing only platform line endings.
3. Stop immediately at the first failed case. Report the case aim, console input, expected output, and actual output. Do not run later cases.
4. For a successful session, show every case’s console input and output, then report that all cases passed.

When adding coverage, record the test case in `test/ui-test-plan.md` before running the skill. Keep expected output complete, including Janet’s banner and separators.
