---
name: test-ui
description: Run command line UI tests for the Auto program from test/ui-test-plan.md, feeding commands to a fresh instance and checking the console output against the expected output. Use when asked to test the UI, run or add UI/text test cases, check that commands produce the expected console output, or verify the program still behaves correctly after a change.
---

# Test the command line UI

Drive the `Auto` program through its console interface and check what it prints.
Every test case lives in `test/ui-test-plan.md`; the bundled runner executes all
of them and reports the failures together at the end.

Expected output in the plan states **desired** behaviour, not whatever the
program currently prints. A case that crashes the program is a failing case,
never a passing one.

## Run the tests

From the repository root:

```bash
python .claude/skills/test-ui/scripts/run-ui-tests.py
```

Use `python`, not `python3` — on this Windows machine `python3` resolves to a
Microsoft Store alias stub that is not a working interpreter. `py` also works.

The runner compiles every `.java` file under `src/main/java` into
`_temp/ui-test-classes` first, so no separate build step is needed. `_temp/` is
already in `.gitignore`.

Options:

- `--only TC-07` runs only the test cases whose heading starts with that text.
  Use it when iterating on one failure; run the whole plan again afterwards.
- `--plan <path>` points at a different test plan file.

## Add test cases the user supplies

When the user gives a list of commands and the output they expect, record each
one in `test/ui-test-plan.md` **before** running anything, so the plan stays the
single source of truth. Append a new `###` heading under `## Test cases` using
this shape:

````markdown
### TC-10 Short title

**Aim:** What behaviour this proves, in one sentence.

**Input**

```text
todo read book
bye
```

**Expected output**

```text
{{GREETING}}
=======================================================
Got it. I've added this task:
  [T][ ] read book
Now you have 1 tasks in the list.
=======================================================
{{FAREWELL}}
```
````

Requirements the runner enforces:

- The heading, the `**Aim:**` line, and both fenced blocks are all required.
- **Input must end with `bye`.** The program only leaves its input loop on that
  command; otherwise the run hangs until the 15 second timeout and the case is
  reported as a failure.
- **Expected output is the entire console session.** Reference the fixed
  greeting and farewell with the `{{GREETING}}` and `{{FAREWELL}}` placeholders
  defined under `## Common blocks` rather than pasting them into each case.
- **Test cases share no state.** The task list is in memory only and each case
  gets a fresh instance, so a case that needs existing tasks must add them
  itself as part of its input.

If the user gives commands without expected output, do not invent it. Ask what
the output should be, or run the commands once and offer the observed output as
the baseline to confirm — never record unverified output as expected.

## Report the results

The runner prints a session record for every test case it ran, showing the aim,
the input fed to the program, and the console output produced. Show that record
to the user so the test session is visible, and also mention that it is saved to
`_temp/ui-test-session.txt`.

For each failure the runner prints the expected output, the actual output, and a
line diff of the two, plus any stack trace the program wrote to stderr, followed
by a summary listing every failing case. Relay all of that, then say which
behaviour is wrong and where in the source it comes from. Do not edit the
expected output to match the actual output unless the user confirms the new
behaviour is correct — a failure is a result to report, not something to tune
away.

## Comparison rules

Output is compared after normalising line endings, stripping trailing spaces on
each line, and trimming blank lines at the start and end of the session.
Everything else, including blank lines inside the session, must match exactly.
This tolerance exists because the program mixes embedded `\n` with the platform
line separator from `println`, and its ASCII banner has trailing spaces.

## Resource

`scripts/run-ui-tests.py` is the runner and uses only the Python standard
library. It parses the plan, expands the placeholders, runs every case, compares
the output, writes the session record, and exits non-zero if any case failed.
