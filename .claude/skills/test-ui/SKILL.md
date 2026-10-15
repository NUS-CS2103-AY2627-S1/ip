---
name: test-ui
description: Run the project's console UI test plan (test/ui-test-plan.md) against the compiled program, one command at a time, stopping at the first mismatch. Use when asked to test the UI/console output, run UI tests, or verify the chatbot's output against expected output.
---

# Test UI

Drive the compiled program with the commands listed in `test/ui-test-plan.md`
and check its printed output against the expected output recorded there, one
test case (one command) at a time, in a single continuous program session.

## Run the tests

From the repository root:

```bash
py .claude/skills/test-ui/scripts/run-ui-tests.py
```

Pass a different plan file as the sole argument if the user names one other
than `test/ui-test-plan.md`. The script:

1. Finds the class with `public static void main` under `src/main/java` and
   compiles it with `javac` into a throwaway temp directory (never into the
   source tree). Classes that import `javafx.*` or `dev.langchain4j.*` are
   left out of both steps: a console test plan cannot drive a window or reach
   an AI, and compiling either would need the JavaFX or LangChain4j jars,
   which this script deliberately does without. So the console program must
   never refer to such a class directly; only the GUI wires the AI in.
2. Launches that program once and, for each test case in order, sends its
   input line (if any), reads the program's response, and compares it
   line-for-line against the case's expected output. The startup case (no
   input) checks the greeting printed before anything is typed.
3. Stops immediately at the first mismatch — it does not send any further
   commands or check any later test cases. On a mismatch it reports which
   test case failed plus the expected and actual output for that case.
4. Prints a reconstructed console session (inputs interleaved with the
   program's actual output) so the transcript can be read like a real
   terminal session, followed by a PASSED/FAILED summary. On failure the
   transcript ends at the failing case; later cases are not shown because
   they were never run.

Report the PASSED/FAILED result and the transcript back to the user. On
FAILED, lead with which test case failed and the expected-vs-actual diff.

## Maintaining the test plan

`test/ui-test-plan.md` is a sequence of `## Test Case: <name>` sections, each
with:

- `**Aim:**` — one line describing what the case checks.
- `**Input:**` — a fenced code block with the single line typed at this
  step, or an empty fenced block for the startup case. At most one input
  line per case — if a scenario needs several commands, add one test case
  per command; later cases can rely on state left behind by earlier ones
  (e.g. `mark 1` after a case that added a task), since the whole plan runs
  as one continuous session.
- `**Expected Output:**` — a fenced code block with the exact lines the
  program must print in response, with no divider line and no leading or
  trailing blank line (the runner adds and checks the divider framing
  itself). Interior blank lines (e.g. inside the startup banner) do belong
  in the block.

When adding a test case for new behavior, get the exact expected text by
running the program with that input first and copying its real output —
don't hand-type a guess, since whitespace (e.g. list indentation) is part of
what the test checks.
