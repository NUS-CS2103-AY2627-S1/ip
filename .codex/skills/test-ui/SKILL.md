---
name: test-ui
description: Run and verify planned console UI test sessions from test/ui-test-plan.md. Use for interactive command-line UI behavior, not unit tests or graphical UI testing.
---

# Test UI

Use this skill to execute the console UI test cases recorded in `test/ui-test-plan.md` and compare each complete program session with its expected output.

## Test-plan format

Keep all test cases and execution details in `test/ui-test-plan.md`. The plan must contain:

- one `Compile command` and one `Run command` under **Test environment**;
- a clearly named test case for each scenario;
- an **Aim**, **Inputs**, and **Expected output** section in every test case;
- fenced `text` blocks for the inputs and exact expected standard output.

The runner reads this format. Each inputs block is a list of console commands, one command per line. Include the program's exit command when the program is interactive, so the session can finish normally.

## Run the tests

1. Confirm that `java -version` reports Java 25. Switch or configure the JDK first if needed.
2. Review or update `test/ui-test-plan.md` before changing the program. Preserve meaningful scenarios and make expected output match the intended UI, including whitespace where it is user-visible.
3. From the repository root, run:

   ```powershell
   python .codex/skills/test-ui/scripts/run_ui_tests.py
   ```

   To run one case while developing, use `--case <test-case-name>`. If `python`
   is not on PATH in Codex Desktop, load the workspace dependencies and use the
   bundled Python executable it reports.

4. The runner compiles once, starts a fresh program session for each test case, supplies its inputs, and compares the complete standard output after normalizing only Windows versus Unix line endings.

## Results and failures

The runner prints a record of every test session's console input and output.
In the final response, reproduce the runner's complete session record in a
fenced `text` block, including every `Console input:` and `Console output:`
section. Do not replace it with a pass/fail summary, place it only in an
intermediate update, or abbreviate it.

At the first failure, stop the test run immediately. Report the failed case's aim along with clearly labelled actual and expected outputs; do not continue to later cases. Treat a non-zero program exit status or any standard-error output as a failure unless the test plan is intentionally changed to cover it.

After a successful run, report the number of passing cases and the command used. Do not edit source code solely to make a pre-existing expected output pass without confirming that the expectation reflects the intended behavior.
