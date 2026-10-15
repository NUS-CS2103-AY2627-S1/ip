---
name: test-ui
description: Run Bo command-line UI test cases recorded in test/ui-test-plan.md, compare console output with expected output, print each test session, and stop at the first failure.
---

# Bo UI testing

Use this project-specific skill when validating Bo through its console interface.

## Required workflow

1. Read `test/ui-test-plan.md`. Each test case must include an aim, an `Input` code block containing one command per line, and an `Expected output` code block.
2. Run the bundled runner from the repository root:

   ```bash
   python3 .codex/skills/test-ui/scripts/run_ui_tests.py
   ```

3. The runner compiles all files in `src/main/java` with Java 25, starts a fresh Bo process for each test case, and sends that case's commands through standard input.
4. For every case, show the complete console input and output. Expected output lines are matched in order after surrounding whitespace is ignored; this permits the plan to focus on observable responses without duplicating decorative separators.
5. If a case fails, print the actual output and the expected output for that case, stop immediately, and return a non-zero exit status. Do not run later cases.

## Updating tests

Keep test cases in `test/ui-test-plan.md`. Add a case when a user-facing command or error path changes. Expected lines should be specific enough to catch regressions, including task type/status, task numbering, and task counts where relevant. Keep `bye` at the end of a normal session so the process terminates cleanly.
