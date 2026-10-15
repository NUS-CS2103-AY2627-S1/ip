---
name: test-ui
description: "Run ET console UI tests recorded in test/ui-test-plan.md and report the full test session."
---

# Test UI

Run this skill when changing or verifying ET's console behaviour.

## Test plan

Read [test/ui-test-plan.md](../../../test/ui-test-plan.md) before testing. It
is the source of truth for the Java version, compile command, run command, and
test cases. Each test case must contain all of the following:

- a `###` heading naming the case;
- an `Aim:` line;
- an `Inputs:` fenced `text` block containing the console commands; and
- an `Expected output:` fenced `text` block containing the complete console output.

Add or update cases in this plan when the requested UI behaviour changes. Do
not alter expected output merely to hide an unexpected result; first confirm
that the observed behaviour matches the intended requirement.

## Run tests

From the repository root, run:

```bash
python3 .codex/skills/test-ui/scripts/run_ui_tests.py
```

The runner compiles the project using the command in the plan, runs each case
in order, and prints a transcript containing the console input and output.
Output comparison is exact except for line-ending style and one trailing final
newline. If a case fails, it stops immediately, reports the test input, and
shows the expected and actual output. Do not run later cases after a failure.
