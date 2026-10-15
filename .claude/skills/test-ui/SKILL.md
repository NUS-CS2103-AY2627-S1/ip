---
name: test-ui
description: Run the chatbot's text-UI test cases from test/ui-test-plan.md, feeding each case's commands to the program and comparing the console output against the expected output. Use when asked to test the chatbot, run the UI tests, check that commands still behave correctly, or verify a change did not break existing behaviour.
---

# Test UI

Run the text-based UI test cases recorded in `test/ui-test-plan.md`. Each case
feeds a list of commands to the program on standard input and compares the whole
console output against the expected output recorded in the plan.

This is only one of the project's two suites, and running it is not on its own
enough after a code change. The other is the JUnit unit tests in `src/test/java`,
run with `./gradlew test`, which cover the top ~50% highest-value methods and
must be kept up to that target whenever `src/main/java` changes. `AGENTS.md` is
the authority on both; follow it rather than this file alone.

## Run the tests

From the repository root:

```bash
python .claude/skills/test-ui/scripts/run-ui-tests.py
```

Useful options:

- `--case TC-3` runs only cases whose heading contains that text.
- `--plan PATH` uses a different test plan.
- `--repo PATH` runs against a different repository root.

This project is on Windows, where the launcher is `python` (or `py`); `python3`
is not available. On macOS or Linux use `python3`.

## Report the results

1. Show the transcript the runner prints. It records, for every case that ran,
   the aim, the commands typed (`>` lines), the console output (`|` lines), any
   standard error (`!` lines), and the exit code. This is the record of the test
   session and should be shown to the user, not just summarised.
2. On success, state how many cases passed.
3. On failure, the runner stops at that case and prints the expected output, the
   actual output, and a line-by-line difference. Report all three. Do not run the
   remaining cases and do not fix the code without being asked.

The runner exits 0 when every case passes, and 1 as soon as one fails.

## Add or change test cases

All test cases live in `test/ui-test-plan.md`. A case is a `###` heading, an
`**Aim:**` line saying why the case exists, an ` ```input ` block of commands,
and an ` ```expected ` block of console output:

    ### TC-6 - Short title

    **Aim:** what this case is checking, and why it matters.

    ```input
    read book
    bye
    ```

    ```expected
    ...the exact console output...
    ```

Cases run top to bottom. Every case needs a `bye` at the end unless it is
deliberately testing what happens when input ends without one.

A case may also carry a ` ```data ` block before its input block:

    ```data
    T | 1 | read book
    D | 0 | return book | June 6th
    ```

Those lines are written to `data/elsa.txt` before the program starts, so the
case begins with tasks an earlier run is meant to have saved. A case without a
` ```data ` block starts with no data file at all. The runner sets this up for
every case, so cases never inherit tasks from each other or from an earlier
run.

The command used to start the program is the ` ```run ` block near the top of
the plan, so a change to how the program is launched is made there, not here.

The ` ```build ` block beside it is run once before the first case. It builds
the jar the cases then run, so that no case can pass against a stale build. The
runner writes the wrapper as `gradlew.bat` on Windows and `./gradlew` elsewhere,
so the block itself stays the same on every platform. If the build fails, its
output is shown and no case is run.

## Writing expected output

Derive expected output from the increment's requirements, not by copying
whatever the program currently prints. Expected output copied from actual output
records present behaviour as correct and cannot detect a bug that already exists.

Comparison ignores trailing whitespace on each line and blank lines at the very
end of the output; everything else, including leading indentation, must match
exactly.

## Resource

`scripts/run-ui-tests.py` is the bundled runner. It uses only Python's standard
library, so there is nothing to install.
