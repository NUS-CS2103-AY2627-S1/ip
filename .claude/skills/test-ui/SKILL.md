---
name: test-ui
description: Run this project's console UI regression tests against the compiled chatbot, checking real program output against the expected output recorded in test/ui-test-plan.md. Use after any code change to the chatbot's behavior, or whenever asked to test, verify, or check the UI/console output.
---

# Test UI

Runs the test cases recorded in `test/ui-test-plan.md` against the compiled
program, one full console session per test case, and reports pass/fail with
the actual session transcript.

## Run the tests

From the repository root:

```bash
python3 .claude/skills/test-ui/scripts/run_ui_tests.py
```

This compiles every `.java` file in `src/main/java` (auto-detecting the entry
point as the file containing `public static void main`), then for each test
case in `test/ui-test-plan.md`, starts one fresh program session and feeds it
the test case's inputs in order, checking the output after each input against
the expected output for that step.

## Behavior

- Prints the full observed console transcript for every test case that runs, so the session can be reviewed either way.
- On the first mismatch anywhere (wrong output, program exits early, or times out waiting for output), the whole run stops immediately — no further test cases run — and the expected vs. actual output for the failing step is printed.
- Exits with status 0 if every test case passed, non-zero otherwise.

## Keeping the test plan current

`test/ui-test-plan.md` is the source of truth for these tests, and needs
updating whenever the chatbot's commands or output format change:

1. Add a new `### TC<n>: <name>` section with an **Aim** line and a fenced ` ```session ` block for any newly added command or behavior.
2. Update the expected output in existing test cases whose output format changed (e.g. a new field appended to `list` output).
3. See the "Format" section at the top of `test/ui-test-plan.md` for the exact `>>> input` / expected-output block syntax.

After updating the file, re-run this skill to confirm the new/updated
expectations actually match real program output before treating the test
plan as correct — don't assume the written expectation is right without
having seen the program actually produce it.

## Resource

`scripts/run_ui_tests.py` is the bundled standard-library-only test runner (no pip installs needed).
