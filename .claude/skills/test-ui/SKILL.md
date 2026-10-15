---
name: test-ui
description: Run the Thomas chatbot's text-UI test cases from test/ui-test-plan.md, feeding each case's commands to the program and comparing the console output against the expected output. Use when asked to test the UI, run the text-UI tests, check the chatbot's output, verify a command still behaves correctly, or add a new UI test case.
---

# Text-UI testing for Thomas

Runs the chatbot end to end against the test cases recorded in
[test/ui-test-plan.md](../../../test/ui-test-plan.md), then shows the console
session so the input and output can be read directly.

## Running the tests

```bash
python3 .claude/skills/test-ui/scripts/run-ui-tests.py
```

To run a single case while working on it, filter by heading text:

```bash
python3 .claude/skills/test-ui/scripts/run-ui-tests.py --filter TC4
```

The script compiles `src/main/java/*.java` into `bin/` with `javac` and then
runs `java -cp bin Thomas` once per test case. It needs Java 25 on the PATH; if
`javac` is missing or the compile fails it stops with a setup error, and on
macOS the fix is `sdk use java 25.0.3.fx-zulu`.

## What to do with the result

Report the run, do not just summarise it:

1. **Show the transcript.** Paste the script's output, or the relevant part of
   it. Each case prints its aim, the lines typed in (`>`), and the lines the
   program printed back (`|`). That transcript is the record of the session and
   is the point of the exercise -- do not replace it with "all tests passed".
2. **On a failure, stop.** The script already stops at the first failing case
   and exits 1. Do not re-run the remaining cases, and do not start fixing code
   before reporting. Show the failing case's aim, its expected output, its
   actual output, and the diff between them, then say which of the two looks
   wrong: a genuine bug in the chatbot, or an expected output in the plan that
   has gone stale because the program's wording changed on purpose.
3. **On success**, say how many cases ran and that the plan is unchanged.

Exit status: 0 all passed, 1 a case failed, 2 a setup problem (missing plan,
unparseable plan, compile error).

## Adding or updating cases

Cases live in `test/ui-test-plan.md` under `## Test cases`, one `###` heading
each, with three parts: `**Aim:**` on one line, an `**Input:**` code block, and
an `**Expected output:**` code block. The plan file explains the format and how
cases are isolated from one another; read it before editing.

Two rules matter when writing a case:

* **Derive the expected output from the source, not from a run.** Read
  `Thomas.java` and work out what it should print. Pasting in whatever the
  program happened to print makes the test agree with the code by construction,
  including its bugs, and it will then pass forever without checking anything.
* **Give each case one aim.** The aim is what makes a failure diagnosable: it
  says what behaviour broke, not merely that some bytes differ. A case whose aim
  needs the word "and" is usually two cases.

When a case fails because the chatbot's wording changed deliberately, update the
plan's expected output and say so explicitly in the report -- an expected output
edited quietly is a test silently deleted.
