---
name: test-ui
description: Use when asked to test, re-test, or verify Duncan's text UI behaviour from the console - running the UI test cases, adding or updating a UI test case, or checking that a change to the program did not alter what it prints. Also use after changing anything the program prints.
---

# Test the text UI

Run every test case in `test/ui-test-plan.md` against the real program and
compare what it prints with what the plan says it should print.

The plan is the source of truth. It holds the setup, the greeting and farewell
that every session prints, and one entry per test case giving that case's aim,
input, and expected output. `scripts/run-ui-tests.py` reads it and does the
running and comparing.

## Run the tests

From the repository root:

```bash
python3 .claude/skills/test-ui/scripts/run-ui-tests.py
```

Useful options:

| Option | Effect |
|---|---|
| `--only TC04` | Run just that case (repeatable). Use while fixing one case. |
| `--plan <path>` | Use a different plan file. Default: `test/ui-test-plan.md`. |
| `--timeout 30` | Seconds allowed per case. Default: 15. |

Exit codes: `0` all passed, `1` a case failed, `2` the setup or plan is broken
(no Java 25, sources do not compile, plan cannot be parsed).

The script compiles the sources itself and runs each case in its own fresh
temporary directory, so it never touches the `data/` save file in the working
copy. Nothing needs to be built or cleaned up first.

## Report the result

**Always show the session transcript.** The script prints, for every case that
ran, the aim, the exact console input it typed, and the exact console output it
got back. Show that transcript to the user — it is the record of the test
session, and it is the point of running the tests rather than just the
pass/fail line.

If the transcript is long, show it in full anyway unless the user asked for a
summary; do not replace it with a description of what happened.

## When a case fails

The script stops at the first failing case and prints its aim, its expected
output, its actual output, and a diff. It also says how many cases were left
unrun.

Report that to the user and **stop there.** A failing case means one of two
things, and only the user can say which:

- the program has a bug, and the plan is right; or
- the program is right, and the plan's expected output is out of date.

**Never edit the plan's expected output to match what the program printed**
unless the user has said the new output is the correct one. Doing that turns a
failing test into a silent pass and destroys the only record of what the
program was supposed to do. The same goes for deleting or skipping the case.

If the error is `the program's greeting does not match the plan's "### Greeting"
block`, the program's banner or greeting changed. Tell the user; update the
plan's Greeting or Farewell block only once they confirm the change was
intended.

## Add or change a test case

Test cases live under `## Test cases` in `test/ui-test-plan.md`. Copy the shape
of an existing one:

````markdown
### TC14: Short title

**Aim:** What this case checks, and why it is worth checking.

**Input:**

```text
todo read book
mark 1
```

**Expected output:**

```text
____________________________________________________________
Got it. I've added this task:
  [T][ ] read book
Now you have 1 tasks in the list.
____________________________________________________________
```
````

Rules the script relies on:

- The heading must read `### <id>: <title>`. Ids are free-form; keep them
  unique and in order.
- All three parts — **Aim:**, **Input:**, **Expected output:** — are required.
  A case missing one is a setup error, not a test failure.
- Do not put the exit command (`bye`) in the input; it is appended
  automatically.
- Do not put the greeting or farewell in the expected output; they are removed
  before comparison.
- The program prints a blank line after each command's response, so a case with
  several commands must include those blank lines between the response blocks.
  Blank lines at the very start and end of a block, and trailing whitespace on
  a line, are ignored.

**Write the expected output from what the program is supposed to do** — read
the code, or the requirement being implemented. Do not run the program first
and paste its output in as the expectation: that only records what the program
does today, so the case can never catch the behaviour being wrong.

Each case starts from an empty task list, so a case cannot test that tasks
survive a restart. `## Not covered by this plan` in the plan lists what is out
of scope.
