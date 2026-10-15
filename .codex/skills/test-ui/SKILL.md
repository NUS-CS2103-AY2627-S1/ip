---
name: test-ui
description: Run and maintain exact-output acceptance tests for this project's command-line UI using test cases in test/ui-test-plan.md. Use when asked to test Sine's console commands, add UI test cases, or show a console test session.
---

# Test UI

Use `test/ui-test-plan.md` as the source of truth. Each test case must contain an aim, an `Inputs` text block, and an `Expected output` text block. When the user supplies commands and expected outputs, add or update cases in that file before running them.

Run all cases from the repository root:

```bash
python3 .codex/skills/test-ui/scripts/run_ui_tests.py \
  --java-home /Users/iansin/.sdkman/candidates/java/25.0.3.fx-zulu
```

The runner compiles every file in `src/main/java` with Java 25, starts a fresh `Sine` process for each case, and compares its complete standard output with the expected output after normalizing line endings and the final newline. It prints the console input and actual output for every executed case.

Stop after the first failure. Report the failed case's aim, input, complete expected output, and complete actual output; do not run later cases. Do not change application code or expected output merely to make a failing test pass unless the user asks for that change.

When adding cases, keep them independent because every case runs in a new process. Include `bye` when the expected session should exercise normal shutdown.
