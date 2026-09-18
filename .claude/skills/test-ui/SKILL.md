---
name: test-ui
description: Run the console UI test cases in test/ui-test-plan.md against the compiled Bot program and report pass/fail with a full transcript. Use after any change to src/main/java to check for regressions.
---

# test-ui

Runs the black-box console test cases recorded in `test/ui-test-plan.md`
against the current `Bot` program, in order, stopping at the first
failure.

Bot saves tasks to `./data/bot.txt` and reloads them on startup, so unlike
a purely in-memory program, state can carry over between separate `java
-cp out bot.Bot` runs via that file. Each test case's **Setup** note (see
`test/ui-test-plan.md`'s "How to run" section for the default when a test
case has none) says exactly how to handle `./data` before that test case;
follow it precisely; do not delete `./data` on your own initiative when a
test case's Setup says not to, and do not skip deleting it when a test
case (or the default) says to.

## Steps

1. Read `test/ui-test-plan.md` in full. For each test case it lists an
   aim, a **Setup** note (or relies on the file's stated default of
   deleting `./data` first), and either:
   - a single **Input** block and **Expected Output** block (one program
     run), or
   - multiple numbered **Session** blocks, each with its own **Input** and
     **Expected Output** (multiple program runs sharing one `./data`,
     used to test persistence itself).

2. Build the program fresh (the source is split across packages under
   `src/main/java/bot/...`, so a flat `*.java` glob won't find every
   file — discover them recursively instead). Exclude `bot/gui` (the
   JavaFX GUI, see Level-10): `Bot` never depends on it, and it needs
   JavaFX on the classpath, which plain `javac` doesn't have:
   ```
   find src/main/java -path "*/bot/gui" -prune -o -name "*.java" -print | xargs javac -d out
   ```
   If the build fails, stop and report the compiler error — do not run any
   test cases.

3. For each test case, in the order listed in the file:
   a. Carry out its **Setup** note (deleting `./data`, or writing specific
      content to `./data/bot.txt`, as it directs).
   b. For each run in the test case (the one Input/Expected Output pair,
      or each Session in order), run `java -cp out bot.Bot`, feeding that
      run's **Input** lines to standard input in order (e.g. via `printf
      'line1\nline2\n... | java -cp out bot.Bot`), and capture the full
      standard output produced. For a multi-session test case, do not
      touch `./data` between sessions.
   c. Compare each run's actual output verbatim against its **Expected
      Output** block. Ignore a difference in only the trailing newline at
      the very end; treat any other difference (including
      whitespace/indentation) as a failure.
   d. Print a transcript for this test case: its name/aim, and for each
      run the input lines exactly as sent and the actual output produced
      — so the console session is visible.
   e. If any run's actual output does not match its expected output, stop
      immediately (do not run the remaining test cases) and report:
      - which test case (and, if multi-session, which session) failed,
      - the full expected output,
      - the full actual output,
      - if easy to identify, the first line at which they diverge.

4. If every test case passes, print a short summary (e.g. "N/N test cases
   passed") after the transcripts.

## Notes

- Single-run test cases are independent of each other via their Setup
  note (normally: delete `./data` first) — task state should never carry
  over between them. A multi-session test case deliberately shares
  `./data` across its own sessions only; still delete/reset `./data`
  before that test case's first session per its Setup note, and treat
  `./data` as reset again (per the next test case's own Setup note) once
  you move on to the next test case.
- If `test/ui-test-plan.md` is missing or has no test cases, report that
  instead of silently doing nothing.
- Do not modify `src/main/java` or `test/ui-test-plan.md` as part of
  running this skill; it only builds, runs, and reports.
