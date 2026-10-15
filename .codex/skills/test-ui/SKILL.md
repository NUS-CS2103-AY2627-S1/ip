---
name: test-ui
description: "Run scripted console UI tests from a Markdown plan containing commands and expected output. Use when testing a command-line application, recording UI test cases, showing a console session transcript, or stopping at the first output mismatch."
---

# Test UI

Run planned console-user-interface tests for this Java project. Use the test plan at test/ui-test-plan.md unless the user supplies another plan.

## Test-plan format

Record each case as a level-two heading followed by Aim, Inputs, and Expected output fields. Indent every input and output line by four spaces. The expected output must include the complete console session, including the banner and dividers.

## Run tests

1. Update the plan with the requested commands and exact expected output.
2. Run the bundled test runner from the repository root:

   python .codex/skills/test-ui/scripts/run_ui_tests.py test/ui-test-plan.md

3. Show the runner's Console input and Console output transcript for every executed case.
4. If a case fails, stop immediately. Report that case's expected and actual output, and do not run later cases.

The runner compiles src/main/java with javac and runs the Nova entry point separately for each test case.
