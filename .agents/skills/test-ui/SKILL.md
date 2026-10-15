---
name: test-ui
description: Run the scripted console-input/output test cases in test/ui-test-plan.md against the compiled chatbot and check each command's output against its expected output. Use when asked to test the UI, run UI tests, verify the program's console output, or check that a change didn't break existing command behavior.
---

# Test UI

Run every test case in `test/ui-test-plan.md` as a fresh console session
against the compiled program, comparing each command's printed response
against the expected output recorded for it. Stop at the first mismatch.

## Run the tests

From the repository root:

```bash
python3 .agents/skills/test-ui/scripts/run_ui_tests.py
```

This recompiles all Java files under `src/main/java` into a temporary directory first, so
it always tests the current source, not a stale build. Override
`--plan`, `--src`, `--main-class`, or `--timeout` if the user asks for a
non-default location or class name; defaults are `test/ui-test-plan.md`,
`src/main/java`, `samantha.Samantha`, and 10 seconds.

## Behavior to expect

- Each test case in the plan runs as its own process: all of that test
  case's `input` lines are fed to stdin in order, and the output is split on
  divider lines into response blocks, one per command.
- The script always prints a console transcript for the test case it just
  ran -- the startup banner/greeting followed by each command interleaved
  with what it printed -- so the actual session is visible either way.
- If every step in a test case matches, it's reported as `PASS` and the next
  test case runs.
- On the first step that doesn't match (or a step that produced no output
  at all, e.g. because the process crashed early), the script prints the
  failing test case's name and aim, the input that failed, the expected
  output, the actual output, any stderr from the process, and then **stops
  immediately** -- it does not run the remaining test cases. Report this
  actual-vs-expected diff back to the user rather than summarizing it away.
- Exit code is 0 if every test case passed, 1 otherwise (compile failure,
  missing plan file, or a failing test case).

## Adding or editing test cases

Test cases live in `test/ui-test-plan.md`, which documents its own format at
the top. Each test case is a `## Test <n>: <name>` section with a
`**Aim:**` line and one or more `input`/`expected` fenced-code-block pairs,
one pair per command in that session. When asked to add coverage for a
behavior, add a new test case there rather than writing a one-off script --
that keeps the whole suite in one place and re-runnable.

A test case should end with a `bye` step so the process exits cleanly;
without it the process hits end-of-input mid-loop and crashes instead of
terminating normally.
