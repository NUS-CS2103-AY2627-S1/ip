---
name: test-ui
description: Runs Bott's console UI test cases from test/ui-test-plan.md and checks the program's actual output against each test case's expected output. Use this after any change to Bott.java or the Task/Todo/Deadline/Event classes, or whenever the user asks to "test the UI", "run UI tests", "run test-ui", or wants to verify Bott's console behavior.
---

# test-ui

Runs the test cases recorded in `test/ui-test-plan.md` against the compiled `Bott` program and checks
the program's console output against each test case's expected output, character for character.

## Steps

1. **Compile.** Make sure Java 25 is active (`sdk use java 25.0.3.fx-zulu` if needed), then build a clean
   set of classes. Classes live under packages (e.g. `bott`, `bott.ui`, `bott.task`), so compile with a
   recursive file list rather than a flat wildcard. The `bott.gui` package (the JavaFX GUI) is excluded:
   it needs the JavaFX libraries on the classpath — supplied by Gradle, not a plain `javac` — and none of
   its classes are reachable from `bott.Bott`, so the console tests never exercise them:
   ```
   rm -rf bin && mkdir bin && find src/main/java -name "*.java" -not -path "*/gui/*" | xargs javac -d bin
   ```
   If compilation fails, stop here and report the compiler error — do not run tests against a stale or
   missing build.

2. **Read the test plan.** Read `test/ui-test-plan.md` in full. It contains an ordered list of test
   cases; each has an **Aim** (what it checks), an **Input** block (the exact lines to send to the
   program's stdin, in order, ending with `bye`), and an **Expected output** block (the exact text the
   program must print to stdout for that whole run — from the first character of the startup banner to
   the final blank line after the farewell message).

3. **Run each test case, in order.** Each test case is one independent, fresh run of the program (its
   Input lines all go to a single invocation), unless its Aim says it spans multiple runs (used for
   testing that saved tasks persist across restarts). Bott persists tasks to `data/bott.txt`, so:
   - Before a normal (single-run) test case, delete `data/bott.txt` if it exists, so no leftover state
     from an earlier test case leaks in.
   - Before the *first* run of a multi-run test case, also delete `data/bott.txt`. Do NOT delete it
     between that test case's own runs — the shared file is what's being tested.

   For each test case:
   - Write its Input lines to a temp file (e.g. in your scratchpad directory, or `/tmp` if you don't have
     one).
   - Run `java -cp bin bott.Bott < <input-file>` and capture stdout.
   - Compare the captured output to the test case's Expected output **exactly** — every character,
     including leading spaces before divider lines and message text, and trailing blank lines. Do not
     trim, normalize, or otherwise treat whitespace as insignificant; Bott's output format is the product
     under test.
   - If it matches: keep the input and actual output for the session record (step 5), then move on to the
     next test case.
   - If it does not match: **stop immediately.** Do not send further input for this test case, and do not
     run any remaining test cases. Report:
     - The test case's name and aim.
     - The full actual output.
     - The full expected output.
     - A line-level diff between the two, to help pinpoint exactly where they diverge.

4. **Clean up** any temp input/output files you created once you're done comparing.

5. **Show the session record.** Whether the run stopped early on a failure or all test cases passed,
   show a transcript of every test case that was actually executed: its aim, the input lines sent, and
   the actual console output produced. This is a record of the test session, not just a pass/fail count —
   the user should be able to see what actually happened.

## If the test plan is out of date

If the change you're testing altered Bott's commands or output format, `test/ui-test-plan.md` should
already have been updated to match (see `AGENTS.md`) before this skill runs. If you find a test case here
that no longer reflects the intended behavior, don't silently edit its expected output to make it pass —
flag the mismatch to the user; it may be a genuine regression rather than a stale test case.
