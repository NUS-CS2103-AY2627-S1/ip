---
name: test-ui
description: Run scripted console UI tests for this Java project from command and expected-output lists recorded in test/ui-test-plan.md. Use when Codex needs to test the interactive Herta command-line interface, compare actual output with expected output, capture a console transcript, or stop immediately and report a failed test.
---

# Test UI

Run the project's interactive console UI from the test cases in `test/ui-test-plan.md`.

## Workflow

1. Read `AGENTS.md` and `test/ui-test-plan.md` before testing. Treat the plan as the source of truth for the program command, inputs, expected output, and relevant setup information.
2. Check that Java 25 is active. If it is not, stop and report the version problem rather than silently using another JDK.
3. Compile the application using the setup command in the plan when one is provided. Keep generated class files in the location specified by the plan.
4. Run the bundled runner from the repository root:

   ```powershell
   python .codex/skills/test-ui/scripts/run_ui_tests.py test/ui-test-plan.md
   ```

5. For every test case, start a fresh process, send its listed input commands in order, and compare the complete combined console output with that case's expected output. Preserve command order and whitespace; only a final line-ending difference is ignored.
6. Stop immediately on the first failed test. Report the test name, actual output, and expected output, including a useful line-by-line diff when possible. Do not run later cases.
7. After a successful or failed run, show the console input and output transcript printed by the runner and point to `test/ui-test-session.log`.

## Test-plan format

Keep all test cases in `test/ui-test-plan.md`. Include the program command, working directory, Java requirement, optional setup command, and one section per test case. Each case must contain an aim, an `Inputs` fenced block containing one UI command per line, and an `Expected output` fenced block containing the exact output for that complete session.

Use this shape when adding cases:

```markdown
## Test case: Short name

- Aim: What behavior this case verifies.

### Inputs

```text
first command
second command
bye
```

### Expected output

```text
exact program output, including prompts and indentation
```
```

Use an empty line in the inputs block to test an empty command. Ensure each session exits, normally by including `bye`, so the runner can collect complete output.

## Failure handling

The runner writes the transcript for all cases reached to `test/ui-test-session.log`. On failure it exits with a non-zero status after recording the failed case. Use the actual/expected sections and diff in the console output to diagnose the mismatch, update the plan only when the expected behavior is intentionally changing, and rerun the skill.
