---
name: test-ui
description: Run the repository's console UI test cases from test/ui-test-plan.md, compare each actual transcript with its expected output, and stop at the first failure.
---

# Console UI testing

Use this skill after every code update that changes user-visible console behavior, and whenever the project instructions require UI verification.

1. Read `test/ui-test-plan.md`. Each test case must include an aim, inputs, a command, and expected output.
2. Run `python test-ui/scripts/run_ui_tests.py test/ui-test-plan.md` from the repository root, using Java 25 when the plan invokes Java.
3. Treat stdout and stderr as one console transcript. The runner compares output after converting CRLF to LF and ignoring final line endings.
4. The runner executes cases in document order, prints console input and output, and terminates immediately on the first mismatch. Report the case name plus both actual and expected output exactly as printed.
5. Do not edit expected output merely to make a failing test pass. If behavior intentionally changed, update the plan before rerunning.

When adding or changing UI behavior, update the test plan with a regression case when needed, then invoke this skill.
