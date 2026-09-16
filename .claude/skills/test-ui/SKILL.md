---
name: test-ui
description: Run the CLI UI test cases recorded in test/ui-test-plan.md against the current build of this project, and report a pass/fail transcript. Use after any code change that could affect the chatbot's console behavior (new/changed commands, output formatting, task types), or whenever the user asks to test the UI / run the test plan.
---

# test-ui

Runs the black-box console test cases defined in `test/ui-test-plan.md`
against the compiled program, in order, stopping at the first failure.

## Steps

1. Read `test/ui-test-plan.md`. It has a "How to run the program" section
   (build/run commands) followed by a numbered list of test cases. Each
   test case has an **Aim**, **Inputs** (stdin lines, one command per
   line), and an **Expected output** block.

2. Build the program using the build command in the plan, e.g.:
   ```
   javac -d out src/main/java/*.java
   ```
   If the build fails, stop immediately and report the compiler output —
   do not attempt to run any test cases.

3. For each test case, in the order listed in the plan:
   a. Run the program fresh (a new process — state must not leak between
      test cases) using the run command in the plan, piping the test
      case's **Inputs** to stdin, one line per command.
   b. Capture stdout only (ignore stderr — JVM/launcher noise such as
      `JAVA_TOOL_OPTIONS` messages is expected there and is not part of
      the program's output).
   c. Compare the captured stdout against the **Expected output** block:
      - If the expected block is labelled "(... section only)" or is
        otherwise clearly a partial excerpt, check that those lines
        appear as a contiguous, in-order substring of the actual output
        (trailing whitespace per line ignored).
      - Otherwise, treat the expected block as the full expected stdout
        and compare it verbatim (trailing whitespace per line ignored).
   d. Print the console session transcript for this test case: the aim,
      the inputs as typed, and the actual output produced, so there is a
      visible record of what ran.
   e. If the comparison fails, stop testing immediately (do not run
      remaining test cases) and report:
      - which test case failed (its number and aim),
      - the exact expected output,
      - the exact actual output,
      - a short diff/description of where they differ.

4. If every test case passes, report a summary (e.g. "9/9 test cases
   passed") after the full transcript.

## Notes for whoever edits the plan or the code

- Keep test cases independent: each one starts a fresh program instance
  and ends with `bye` so the process exits on its own.
- When behavior changes intentionally (new command, changed wording,
  changed formatting), update the relevant expected output in
  `test/ui-test-plan.md` in the same change — this skill only checks
  the plan is honored, it does not decide what "correct" should be.
- When adding a new command/feature, add a new test case to the plan
  rather than only editing an existing one, unless the existing test
  case is being superseded.
