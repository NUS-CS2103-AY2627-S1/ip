---
name: test-ui
description: Run the chatbot's text UI against the test cases recorded in test/ui-test-plan.md, checking the console output of each list of commands against the expected output. Use when asked to test the text UI, run the UI/text-ui tests, check the chatbot's console output, add a text UI test case, or verify that the program still behaves as expected after a change.
---

# Text UI testing

Run the chatbot with lists of commands and check what it prints against the
expected output recorded in the test plan.

## Where the test cases live

All test cases live in `test/ui-test-plan.md`, together with the settings the
runner needs (main class and source directory) and the rules used to compare
output. That file is the single source of truth — never keep test cases in
this skill, in a scratch file, or only in the conversation.

Each test case in the plan states:

- an **aim** — what the test case is checking and why,
- an **input** — the lines the user types, one command per line,
- an **expected output** — everything the program should print for those lines.

Because the chatbot saves its task list to a file, a test case may also state:

- a **data file before** — a save file put in place before the program starts,
  standing for tasks saved in an earlier session,
- a **data file after** — what the save file should hold when the program has
  finished. The single line `(no file)` says no save file should exist at all.

A test case with neither block starts from no save file and has nothing checked
about what it saves.

## Running the tests

1. Make sure Java 25 is active; if `java -version` reports anything else, run
   `sdk use java 25.0.3.fx-zulu` first (this project requires Java 25).
2. From the repository root, run:

   ```bash
   python3 .claude/skills/test-ui/scripts/run-ui-tests.py
   ```

   Useful flags: `--only TC3` runs a single test case, `--plan PATH` uses a
   different plan file.

The runner compiles the sources into a temporary directory, then for each test
case starts the program again and feeds it that test case's input lines. It
prints a record of the console session — the input typed and the output
printed — for every test case it runs.

Each test case runs in a working directory of its own, so the save file one
test case writes is never found by the next, and a test run leaves nothing
behind in the repository.

## Reporting the result

Show the user the runner's output, so they can see the test session itself,
then state the outcome:

- **All test cases passed:** say so and give the number of test cases run.
- **A test case failed:** the runner stops there by design. Report which test
  case failed, its aim, and the expected and actual output the runner printed,
  with the difference between them. Do not run the remaining test cases and do
  not edit anything to make the test pass unless the user asks — a failure is
  a finding to report first.

When a test case fails, say plainly which side looks wrong: the program (a
real bug) or the plan (an expected output that was never updated after an
intended change). Do not assume the plan is right.

## Adding a test case

Append a new `###` heading to the `## Test cases` section of
`test/ui-test-plan.md`, following the format of the test cases already there:
an id and title (`### TC7 - Short title`), an `**Aim:**` line, then `**Input**`
and `**Expected output**` fenced blocks, and optionally `**Data file before**`
and `**Data file after**` blocks. The runner picks it up with no other change.

Write the expected output by hand from the requirements where possible. If you
instead capture it by running the current program, say so when reporting —
output captured that way records what the program does today, which is only a
useful test once someone has confirmed it is also what the program should do.
