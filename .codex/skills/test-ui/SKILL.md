---
name: test-ui
description: Run and verify planned console UI tests for this project. Use when asked to test command inputs and expected program output, or to run the UI test plan.
---

# Test UI

Run the console UI test cases recorded in `test/ui-test-plan.md`. Each test
case describes its aim, the complete console input, and the complete expected
standard output.

## Test plan format

Use one `## Test Case N: <title>` section for each case. Every test case must
contain these sections in this order:

1. `### Aim` — one or more lines describing the behavior being tested.
2. `### Input` — a fenced `text` block containing all console input lines.
3. `### Expected Output` — a fenced `text` block containing the expected
   standard output. Include all messages produced by the program.

## Run the tests

From the repository root, run:

```bash
python .codex/skills/test-ui/scripts/run_ui_tests.py
```

The runner compiles the console Java source files under `src/main/java`,
skipping files that import JavaFX, then runs each test case in plan order. It
prints the aim plus a record of the console input and actual output for every
completed test session.

On the first mismatch, stop immediately. Report the failing test's aim, input,
expected output, and actual output; do not run later test cases. Treat a
non-zero program exit or compilation failure as a failed test session.

Update `test/ui-test-plan.md` before running the skill whenever commands or
their output change. Keep test cases focused on one behavior where practical.
