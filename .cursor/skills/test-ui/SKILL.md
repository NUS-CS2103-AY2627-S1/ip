---
name: test-ui
description: >-
  Run console UI tests for this Java chatbot from test/ui-test-plan.md.
  Accepts lists of commands and expected outputs, runs the program for each
  test case, compares actual vs expected output, shows the console I/O
  transcript, and stops immediately on the first failure. Use when asked to
  run UI tests, text-UI tests, /test-ui, or verify chatbot console behaviour;
  also invoke after each application code update per AGENTS.md.
---

# test-ui

Run text-UI tests for the project chatbot using the plan in `test/ui-test-plan.md`.

Project agents must invoke this skill after each application code update, and
must update `test/ui-test-plan.md` first when console behaviour changed.

## Requirements (do not weaken)

* The skill should accept lists of commands and expected outputs. For each command, it should run the program and check the output against the expected output.
* The list of test cases (and other relevant information) should be recorded in the `test/ui-test-plan.md` file.
* Each test case should specify the aim of the test case, inputs, and the expected output.
* After testing, show a record of the console input and output so we can see the test session.
* If a test case failed, terminate the test session immediately, and report the actual and expected outputs.

## Workflow

1. Read `test/ui-test-plan.md` for how to launch the program and for every test case.
2. Prefer the bundled runner (exact compare, fail-fast, I/O transcript):

   ```bash
   python3 .cursor/skills/test-ui/scripts/run-ui-tests.py
   ```

3. If the plan or sources changed and expected outputs need updating, edit `test/ui-test-plan.md` first, then re-run.
4. Report the runner summary to the user. Always include the console input/output record from the run.
5. On failure: stop (do not run later cases). Show the failing case aim/id, then actual vs expected output.

## Manual fallback (only if the script cannot run)

For each test case in order:

1. Compile and start the program as documented in the plan (Java 25).
2. Feed the case **Inputs** lines to stdin (one command per line; end with `bye` when required).
3. Capture full stdout.
4. Compare to **Expected output** (normalize `\r\n` → `\n`; ignore a single trailing newline difference only if both sides are otherwise identical).
5. Print a session transcript: each input line and the program output.
6. On mismatch: print actual and expected, then **stop immediately**.

## Plan format

Keep `test/ui-test-plan.md` as the source of truth. Each case must include:

- **Aim** — what behaviour is being checked
- **Inputs** — the list of commands (stdin lines)
- **Expected output** — full console output for that session

Use the heading/fenced-block structure the runner already understands (see existing cases in that file).
