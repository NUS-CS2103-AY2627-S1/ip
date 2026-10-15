---
name: test-ui
description: Run fail-fast console UI tests for this Java chatbot using cases recorded in test/ui-test-plan.md. Use after chatbot code changes or when given command and expected-output lists to verify.
---

# Test UI

Use `test/ui-test-plan.md` as the source of truth for console test cases. If the
user supplies new commands and expected outputs, add or update a test case in
that file before running the tests. Every case must contain:

- an aim;
- an `Inputs` fenced text block containing commands in entry order; and
- an `Expected output` fenced text block containing the response lines in order.

When persistence is part of the behavior under test, a case may also contain an
`Expected data file` fenced text block. The runner compares it exactly with the
generated `data/bot.txt` file. An `Initial data file` fenced text block seeds
that file before startup when loading behavior needs to be tested.

Each test case starts a fresh chatbot process in an isolated temporary working
directory, so storage tests do not overwrite project data. The runner ignores
indentation, blank lines, separator-only lines, and the startup banner through
the first separator. All remaining output must exactly match the expected
response lines.

From the repository root, run:

```bash
python3 .codex/skills/test-ui/scripts/run_ui_tests.py --plan test/ui-test-plan.md
```

The runner compiles the Java sources, prints the complete console input and
output for every executed case, and stops immediately on a compilation error,
nonzero program exit, timeout, or output mismatch. On failure, report the test
name plus the normalized expected and actual output printed by the runner. Do
not continue to later test cases after a failure.

Prefer Java 25. The runner checks common Java installations and honors a
`TEST_UI_JAVA_HOME` environment variable. If Java 25 is unavailable, it uses the
newest compatible local JDK and prints a visible warning in the test record.
