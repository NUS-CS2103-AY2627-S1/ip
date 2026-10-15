---
name: test-ui
description: Run and verify this project's planned console UI sessions after user-facing Java changes. Use for interactive-command behavior, prompts, formatting, and regressions; not for unit-only changes with no UI effect.
---

# Console UI testing

Use the test cases in `test/ui-test-plan.md` as the source of truth. Each case states its aim, the lines supplied to the program, and the exact expected console output.

1. Update the plan when a user-visible command, message, or workflow changes. Keep every case's aim, input, and expected output current.
2. Run `powershell -ExecutionPolicy Bypass -File .codex/skills/test-ui/scripts/run-ui-tests.ps1` from the repository root. The runner compiles the application with Java 25, executes every planned console session, and compares normalized full output exactly.
3. Report the runner's session transcript. It prints the console input and actual output for every passing case.
4. Stop at the first failure. Report that case's actual and expected output, correct the relevant code or plan, then rerun from the beginning. Do not treat a failed UI test as passing.

The runner intentionally does not edit the test plan or application sources.
