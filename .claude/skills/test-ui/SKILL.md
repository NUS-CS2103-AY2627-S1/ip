---
name: test-ui
description: Run the text-UI test cases recorded in test/ui-test-plan.md against the chatbot. Each case gives an aim, a list of input lines, and the expected console output; the runner compiles the program, feeds each case's input to a fresh run, prints the full session transcript, and stops at the first failure with the expected and actual output. Use when asked to test the UI, run the text-UI tests, check the program's output against expected output, or add a UI test case.
---

# Text UI testing

Run the program once per test case, compare its console output with the expected
output, and show the session so a reader can see what was typed and what came back.

## Where the test cases live

All test cases live in `test/ui-test-plan.md`. That file is the single source of
truth: it documents the plan for human readers *and* is parsed by the runner, so
there is no second copy of the expected output to keep in step.

Each test case is a level-3 heading `### <id>: <title>` followed by three fields:

* `**Aim:**` — one line on what the case checks and why.
* `**Input:**` — a fenced block, one input line per line, exactly as typed.
* `**Expected output:**` — a fenced block holding the whole console output.

`{{NAME}}` inside an expected output expands to the reusable snippet of that name,
defined under a `#### NAME` heading in the plan. The greeting banner and the
sign-off appear in every run, so they are written once as snippets instead of
being repeated in every test case.

The plan file itself contains a fuller description of the format; read it before
editing.

## Running the tests

Use Java 25. On macOS, switch to it first if needed:

```bash
sdk use java 25.0.3.fx-zulu
```

Then run the whole plan from the repository root:

```bash
python3 .claude/skills/test-ui/scripts/run-ui-tests.py
```

Useful options:

| Option | Purpose |
| --- | --- |
| `--only TC3 TC5` | Run just those test cases, by id. |
| `--plan <path>` | Use a different test plan file. |
| `--main-class <name>` | Start a class other than `davidgoggins.DavidGoggins`. |
| `--timeout <seconds>` | Change the per-case time limit (default 20). |

The runner compiles every file under `src/main/java` into a temporary directory,
so it never leaves build output in the repository.

## What the output looks like

The runner prints a transcript as it goes. For each test case it shows the aim,
the input lines prefixed with `>`, and the program's console output prefixed with
`|`, then `PASS`.

Comparison ignores differences that are invisible on screen — line-ending style,
trailing spaces at the end of a line, and blank lines at the very end of the
output. Everything else must match character for character. Anything the program
writes to standard error is appended to its output, so an unexpected stack trace
shows up as a failure rather than passing unnoticed.

## When a test case fails

The runner stops immediately: later test cases are not run. It reports the failing
case's id, title and aim, then the full expected output, the full actual output,
and a unified diff of the two. It exits with status 1 (2 if the plan itself could
not be read).

When reporting a failure to the user:

1. Quote the failing test case's id, title and aim.
2. Show the expected and actual output, and the diff line that differs.
3. Say plainly that the run stopped there and which cases were not reached.
4. Diagnose before changing anything: work out whether the program is wrong or the
   expected output in the plan is out of date, and say which you think it is.
   Do not "fix" a failure by pasting the actual output into the plan unless the
   change in behaviour was intended — that would make the test case prove nothing.

## Adding a test case

Add a new `### <id>: <title>` section to `test/ui-test-plan.md`, keeping ids in
sequence. Write the expected output by reasoning about what the program *should*
print, then run the case to confirm. If you generate the expected output by
running the program first, read it line by line against the source before
recording it, so a bug is not frozen into the plan as the expected result.
