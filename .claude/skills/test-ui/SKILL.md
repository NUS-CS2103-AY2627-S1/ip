---
name: test-ui
description: Run this project's console UI test plan (test/ui-test-plan.md) against the Steph program -- compiles the code, feeds each test case's commands into the running program, and checks the actual console output against the expected output recorded in the plan. Use this whenever the user wants to test Steph's console output, run the UI tests, verify a command's output, add a new UI test case, or check that a change to Steph.java (or a Task subclass) didn't break existing behavior. Trigger even if they just say "test this" or "did I break anything" after editing the program's command handling.
---

# test-ui

Verifies Steph's console behavior against recorded expected output, so a
change to command parsing or output formatting either matches what's already
been agreed on, or is caught immediately instead of silently drifting.

## Running the existing test plan

```
python3 .claude/skills/test-ui/scripts/run_ui_tests.py
```

This compiles everything under `src/main/java`, then runs each test case in
`test/ui-test-plan.md` as its own Steph session, checking every command's
output against what's recorded there.

- **All pass**: the script prints a full transcript of every command sent and
  every response received across all test cases. Show this transcript to the
  user (or a representative slice of it if it's long) -- that's the "record
  of the console input and output" the test session produces.
- **A test case fails**: the script stops immediately -- it does not run the
  remaining commands in that test case, or any later test cases. It prints
  the failing test case's aim, the command, the expected output, the actual
  output, and a diff. Report this to the user plainly; don't try to silently
  "fix" the test plan to match new output, or dismiss the failure, without
  checking with the user first -- a mismatch might mean the code regressed,
  or it might mean the expected output is stale and needs updating. Let the
  user decide which.

## Adding or updating test cases

Test cases live in `test/ui-test-plan.md`, whose header documents the exact
format (a `## Test case:` heading, an `**Aim:**` line, and one or more
`### Command` / `### Expected output` fenced-block pairs). Read that header
before adding a case, since the parser depends on the format matching
exactly.

Never hand-type the expected output block. It's easy to get indentation or a
blank line wrong, and a hand-typed "expected" block just encodes a guess --
if it's wrong, the test will either falsely fail on correct output or falsely
pass on broken output. Instead, generate it from a real run:

```
python3 .claude/skills/test-ui/scripts/run_ui_tests.py record "todo buy milk" "list"
```

This compiles, runs the given commands as one session, and prints
ready-to-paste `### Command` / `### Expected output` blocks. Read over the
printed output yourself (or have the user confirm it) before pasting it into
the plan -- the script only records what the program actually did, which is
only correct if the program's current behavior is actually correct. Recording
a bug's output as "expected" just locks the bug in.

A few things worth knowing about how test cases run:

- Each test case starts a fresh Steph process, so tasks added in one test
  case don't leak into the next one. Within a single test case, though, state
  carries across its commands (e.g. `mark 1` after `todo ...` refers to the
  task just added).
- Don't put `bye` in a test case's command list -- it ends the session
  without printing a response block of its own, and every test case's
  session already ends on its own once its commands run out.
- Put exactly one command per `### Command` block. The comparison is
  per-command, so a block with several commands in it can't be checked
  correctly.
