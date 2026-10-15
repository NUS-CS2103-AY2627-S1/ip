---
name: test-ui
description: Run documented console UI test cases, compare each program output with its expected output, and report a complete test-session transcript. Use for validating this project's command-line interface; do not use for unit tests or graphical UI testing.
---

# Test UI

Run the console-interface test cases documented in [test/ui-test-plan.md](../../../test/ui-test-plan.md). The plan is the source of truth for the command, aim, console input, and expected output of every case.

## Test-session workflow

1. Read the complete test plan before starting. Run its test cases in the documented order, launching a fresh program process for each one.
2. Confirm the active Java runtime is version 25. If it is not, switch to `java 25.0.3.fx-zulu` with SDKMAN before running any case.
3. For each case, run the `Run` command exactly as recorded. Capture its combined standard output and standard error. Treat the separate `Console input` block as the input supplied to that command; it is not expected to be echoed by a piped program.
4. Compare the captured output with the `Expected output` block exactly, after only normalizing CRLF line endings to LF. Do not ignore blank lines, spacing, punctuation, or final output lines.
5. On the first mismatch, stop immediately. Do not run later cases. Report the test case name, its aim, the console input, and clearly labelled expected and actual output.
6. If all cases pass, report success and show a session record for every case. For each one, include its name and aim, followed by labelled `Console input` and `Console output` fenced blocks. The displayed output must be the captured actual output, not a copied expectation.

If a case cannot be launched (for example, compilation fails), treat that as a failed case: stop the session and show the command, its input, the expected output, and the captured error/output.

## Maintaining the plan

Keep all UI test cases and execution details in `test/ui-test-plan.md`. When the console interface changes, update the relevant command, inputs, expected output, and aim together. Every test case must include all four fields: `Aim`, `Console input`, `Run`, and `Expected output`.
