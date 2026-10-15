---
name: test-ui
description: Run the chatbot's text UI test cases from test/ui-test-plan.md, feeding each case's commands to a fresh run of the program and comparing the console output against the expected output. Use when asked to test the UI, run the text UI tests, check the chatbot's output against expected output, verify console behaviour, or add a UI test case.
---

# Test the text UI

Each test case in `test/ui-test-plan.md` records an aim, the exact commands to
type, and the exact console output expected back. The bundled runner compiles the
project, replays every case against a fresh run of the program, prints a
transcript of each session, and stops at the first failure.

Every case runs in a working directory of its own, so the file the program saves
its tasks to never leaks from one case into the next.

## Run the tests

1. Run the bundled runner from the repository root:

   ```bash
   python3 .claude/skills/test-ui/scripts/run-ui-tests.py
   ```

   Useful flags:

   * `--only TC-03` runs just the cases whose title contains that text.
   * `--plan <path>` uses a different test plan (default: `test/ui-test-plan.md`).
   * `--main <class>` and `--sources '<glob>'` override the entry point and the
     files to compile. Both default to the settings at the top of the test plan.
   * `--timeout <seconds>` changes how long a single case may run (default: 10).

2. Show the user the transcript the runner prints. It records the input typed and
   the output returned for every case, which is the record of the test session.
   Do not summarise it away — the point is to let the user see the session.

3. Report the outcome plainly: how many cases passed, and whether the run was cut
   short by a failure. Never report a pass without having run the tests.

## When a test fails

The runner stops immediately and prints the failing case's aim, the full expected
output, the full actual output, and a line-by-line diff. Remaining cases are not
run, so their status is unknown — say so rather than implying they passed.

Then work out which side is wrong before changing anything:

* If the program is wrong, fix the program.
* If the expected output in the plan is out of date — for example, the greeting
  or a message was deliberately reworded — update the plan.

Never edit the expected output merely to make a failing test pass. Confirm the new
behaviour is actually intended first, and say which side you changed and why.

## Java version

The project requires Java 25. If `javac` or `java` is missing, or the version is
wrong, switch with `sdk use java 25.0.3.fx-zulu` on macOS.

## Add a test case

Append a section to `test/ui-test-plan.md` in this shape:

````markdown
## TC-10 Short title of the case

**Aim:** What behaviour this case pins down, and why it is worth testing.

**Input:**
```text
todo borrow book
bye
```

**Expected output:**
```text
(the whole console session, banner and farewell included)
```
````

Requirements the runner enforces:

* The heading must start with `TC-` and a number, or the section is ignored.
* Both an `**Input:**` and an `**Expected output:**` fenced block are required.
* Ending the input with `bye` tests the normal exit. Omitting it is also valid and
  tests the end-of-input exit, the same path as pressing Ctrl+D.
* An input line of exactly `--- restart ---` ends that run of the program and
  starts another one in the same working directory. The output of every run in the
  case is compared as one session, which is how behaviour that has to survive a
  restart — such as saving and loading the task list — is tested.
* An optional `**Data file:**` fenced block is written to the saved-list file
  (`data_file` in the plan's settings, default `data/billy.txt`) inside the case's
  working directory before the program starts. Use it to test loading, including
  loading a file that has been damaged. Without such a block the case starts with
  no data file at all, as on a first run.
* The expected output is the whole session, since each case runs the program from
  scratch. Task numbering therefore restarts at 1 in every case.
* Write the shared startup and sign-off as `{{GREETING}}` and `{{FAREWELL}}` on a
  line of their own rather than repeating them. The runner replaces such a line
  with the matching `## Snippet: NAME` section before comparing, so rewording the
  greeting is a one-place change. A name with no matching section is an error.
* Avoid trailing spaces on input lines: they are invisible and editors often strip
  them on save. Leading and repeated inner spaces are safe to test with.
* Trailing spaces at the end of a line are ignored, as they are invisible on
  screen. Everything else must match exactly, including blank lines and the
  divider lines.

To write the expected output, reason about what the program should print. If you
instead copy what the program currently prints, the case proves nothing beyond
"the code does what it does" — and would happily lock in a bug.

## Resource

`scripts/run-ui-tests.py` is the bundled runner. It uses only the Python standard
library, so there is no install step. It compiles to a temporary directory that is
deleted afterwards, so it never writes into the repository.
