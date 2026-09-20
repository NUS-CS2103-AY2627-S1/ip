---
name: test-ui
description: Run repeatable console UI tests defined in test/ui-test-plan.md, checking expected output and printing each test session.
---

# Test UI

Use this skill when changing or verifying this project's command-line interface.

The test plan is `test/ui-test-plan.md`. It contains one or more `## Test case:`
sections. Every section must contain:

- `Aim:` explaining the behaviour under test.
- an `### Inputs` block with the commands to send to the program, one command per line.
- an `### Expected output` block with the complete expected console output, excluding the
  startup greeting and the final `bye` response.

Run the plan from the repository root:

```sh
python3 .codex/skills/test-ui/scripts/run_ui_tests.py
```

The runner compiles the Java sources with Java 25, starts a fresh program session
for each test case, and compares the normalized output to the expected output.
It prints the console input and output for every completed test. On the first
failure, it stops immediately and reports both the expected and actual output.

When adding a UI behaviour, add or update a test case in the plan before running
the tests. Keep expected output limited to the response to the listed commands;
the runner removes the greeting and final farewell automatically.
