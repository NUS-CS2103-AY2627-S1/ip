---
name: test-ui
description: Run transcript-based UI tests for the Snoopy Java chatbot. Use when asked to add, update, or execute command-and-output test cases for this project.
---

# Test UI

Use [`test/ui-test-plan.md`](../../../test/ui-test-plan.md) as the source of truth. Each test case must contain an aim, a fenced `Input` block containing the commands sent to the program, and a fenced `Expected output` block containing the complete expected console output.

When the user supplies new test cases, record or update them in the test plan before running them. Preserve exact spaces, punctuation, and line breaks because the comparison is exact.

Design state-sensitive cases, not only isolated command checks. Interleave valid commands that mutate the task list with invalid commands, then use `list` or a later valid `mark`/`unmark` command to verify that rejected input did not add, reorder, remove, or change any task. In particular, cover empty arguments, missing delimiters, non-numeric and out-of-range indexes, unknown commands, and meaningful whitespace boundaries when those behaviors exist in the current version.

Keep each case focused on one state invariant and explain that invariant in its aim. Include the complete follow-up output that proves the internal state remained correct; an error message alone is insufficient evidence for a state-mutating command.

Run all cases from the repository root:

```bash
python3 .agents/skills/test-ui/scripts/run_ui_tests.py
```

The runner compiles with Java 25, starts a fresh program for each test case, and prints the case aim plus the complete console input and actual output. If a comparison fails, stop immediately and report the expected and actual outputs printed by the runner. Do not continue to later cases after a failure.
