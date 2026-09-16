---
name: test-ui
description: Run the project's UI test plan against the compiled program, comparing each command's output to what test/ui-test-plan.md expects. Use after any change to Eve.java, Task.java, or other source files, and whenever the user asks to test, verify, or check the chatbot's behavior.
---

# Test UI

Run every test case recorded in `test/ui-test-plan.md` against the actual
program: compile the current source, feed each case's input to the program's
stdin, and compare its full console output against that case's expected
output.

## Run the tests

From the repository root:

```bash
python3 .claude/skills/test-ui/scripts/run_ui_tests.py
```

The script:
1. Compiles every `.java` file under `src/main/java` (no external
   dependencies -- standard `javac`/`java` only).
2. Parses `test/ui-test-plan.md` into an ordered list of test cases, each
   with an aim, an input (stdin lines), an expected output, and optionally
   a second input/expected-output pair (see below).
3. Before each test case's first run, deletes the `data/` folder the
   program persists its task list under (see `Storage.java`), so every test
   case starts from a clean slate regardless of what an earlier test case
   saved. If a case has a second run, `data/` is *not* reset between the
   two runs within that case -- that's what lets a case verify that data
   saved by run 1 is loaded by run 2.
4. Runs each test case (one or two process invocations) in order, printing
   a session record (the input lines and the actual output) so the console
   transcript is visible.
5. Stops immediately at the first failing run, printing both the expected
   and actual output for that run, and exits with a non-zero status. It
   does not run the remaining test cases.
6. If every test case passes, prints a one-line summary and exits 0.

Report the printed session record and final result back to the user; do not
summarize away the actual/expected output on failure.

## Maintain the test plan

`test/ui-test-plan.md` is the single source of truth for these tests. Each
test case follows this exact structure so the script can parse it:

````markdown
## Test N: <short name>

**Aim:** <what this case verifies>

**Input:**
```text
<one command per line, must end with "bye" so the program exits>
```

**Expected output:**
```text
<the program's exact expected stdout for this input>
```
````

For a test case that needs to check behavior across two separate runs of
the program (e.g. that saved data is loaded back on the next run), add an
optional second pair after the first, using the same structure:

````markdown
**Second input:**
```text
<one command per line for the second, separate process invocation>
```

**Second expected output:**
```text
<the program's exact expected stdout for the second run>
```
````

The `data/` folder is reset before the first input runs, but is left alone
between the first and second runs of the same test case -- do not add a
second run to a case unless it specifically depends on state left behind
by the first.

For a test case that needs to check how the program handles a pre-existing
data file (e.g. a corrupted one), add an optional block that gets written
verbatim to the data file before the case's first run:

````markdown
**Data file before run:**
```text
<exact content to write to data/eve.txt before the first run>
```
````

When a code change adds, removes, or changes the behavior of a command,
update or add test cases here first (verify the new expected output by
running the program manually), then run this skill to confirm.
