---
name: test-ui
description: Run Larper console UI tests from test/ui-test-plan.md after code updates, when validating command input/output behavior, or when a user asks to check UI/test transcripts. Use this skill to compile the Java program, run each listed input session, compare actual output against expected output, stop on the first failure, and show console input/output records.
---

# Test UI

Use this skill for console UI regression testing of the Larper Java program.

## Workflow

1. Open `test/ui-test-plan.md`.
2. Update it if the code change affects commands, messages, formatting, or expected behavior.
3. Run:

```bash
python3 .codex/skills/test-ui/scripts/run_ui_tests.py
```

4. Read the printed test session records.
5. If a test fails, stop further testing, report the failing test case, and include the expected and actual outputs printed by the script.
6. If all tests pass, summarize the covered cases and mention that `test-ui` passed.

## Test Plan Format

Each test case in `test/ui-test-plan.md` must use this structure:

~~~markdown
## Test Case: short name

Aim: What this test proves.

Inputs:
```text
command one
command two
exit
```

Expected output:
```text
full expected stdout
```

Initial data file:
```text
task file contents to write before Larper starts
```

Expected data file:
```text
full expected contents of the saved data file
```
~~~

The runner treats each test case as a fresh program session. Include `exit` in the inputs unless the test is specifically checking end-of-input behavior.
The `Initial data file` block is optional. Use it when a test needs Larper to start with saved tasks.
The `Expected data file` block is optional. Use it only when a test needs to verify Larper's saved task file.

## Notes

- Keep expected output exact, including separator lines and blank lines.
- The runner normalizes line endings to Unix newlines and ignores only extra trailing whitespace at the very end of the full output.
- Do not continue after a failure; fix the code or expected output first.
