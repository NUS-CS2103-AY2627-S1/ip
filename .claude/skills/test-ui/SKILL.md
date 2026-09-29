---
name: test-ui
description: Run Emma's UI test cases from test/ui-test-plan.md, feeding each case's commands to the chatbot and comparing the console output against the expected output. Use when asked to test the UI, run the UI tests, check the chatbot's output, or verify a change did not break existing commands.
---

# Emma UI testing

Runs the chatbot end-to-end, one test case at a time, and checks what it prints
against what the test plan says it should print.

## Where the test cases live

`test/ui-test-plan.md`. Each case is a `##` section containing:

- **Aim** — one line saying what the case is checking
- **Input** — a fenced block, one command per line, exactly as a user would type
- **Expected output** — a fenced block holding Emma's responses only

Read that file's "Conventions" section before writing new cases; it explains
what the expected-output block must and must not contain.

## Procedure

1. Read `test/ui-test-plan.md` and collect the cases to run.
   - If the user named specific cases, run only those.
   - If the user supplied commands and expected output directly in their
     message, run those as an extra ad-hoc case and offer to add it to the plan.
   - Otherwise run every case, in file order.

2. For each case, in order:

   a. Write the case's Input block verbatim to a temporary file.

   b. Run the case:

      ```bash
      .claude/skills/test-ui/scripts/run-case.sh <inputs> <raw-out> <normalised-out>
      ```

      The script compiles the sources under `src/main/java` fresh each time, so it always
      tests current source. It writes the full transcript to `<raw-out>` and
      Emma's responses alone to `<normalised-out>`.

   c. Compare `<normalised-out>` against the case's Expected output block with
      `diff`. Do not eyeball it — run the diff.

   d. **If the case fails, stop the whole session immediately.** Do not run any
      remaining case. Report:
      - which case failed, and its aim
      - the input that was fed in
      - the expected output
      - the actual output
      - the `diff` between them
      Then stop and let the user decide what to do.

3. If every case passes, show the session record: for each case, its name, aim,
   and the raw console transcript from `<raw-out>`, so the user can see the
   actual input and output of the run. Finish with a one-line summary of how
   many cases passed.

Use the scratchpad directory for all temporary files.

## Notes

- Every case's input must end with `bye` so the program terminates. Without it
  the run still ends at end-of-input, but the exit message goes untested.
- Each case runs in its own empty working directory. Emma saves to
  `./data/emma.json` relative to that directory, so cases never inherit each
  other's saved tasks and the repository's real data file is never touched.
- A line reading exactly `--- restart ---` in the input ends the session and
  starts a new one in the same directory, which is how a case tests that saved
  tasks are reloaded. The runner drops the banner and greeting of every session,
  so expected output holds only Emma's responses either side of the restart.
- If `javac` fails, report the compile error and stop — that is a failure of the
  session, not of a test case.
