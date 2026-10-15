---
name: test-ui
description: Run Labubu console UI tests from test/ui-test-plan.md, compare each command sequence with its expected output, and save a console transcript. Use when asked to test or verify interactive command-line behaviour.
---

# Test UI

Use this skill to run the project's Labubu command-line interface tests. The test plan at `test/ui-test-plan.md` is the source of truth for each test's aim, console inputs, and exact expected console output.

## Add or update tests

Add a `## Test case: <name>` section for each test. Include one concise `**Aim:**` line and `text` code blocks for `**Inputs:**` and `**Expected output:**`. Inputs are entered one per line. Include `bye` as the final input so the application exits normally.

Keep expected output exact, including prompts and task formatting. The runner normalizes Windows and Unix line endings and ignores only final trailing line breaks.

## Run the plan

From the repository root, run:

```powershell
py .codex/skills/test-ui/scripts/run-ui-tests.py
```

The runner compiles every `src/main/java/*.java` source file using JDK 25. It uses `JAVA_HOME` when set, otherwise `javac` and `java` from `PATH`. It records every completed case in `_temp/ui-test-session.md`.

Stop after the first failed test. Report its aim, expected output, actual output, and the transcript path. Do not continue with later cases after a failure.
