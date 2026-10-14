---
name: test-ui
description: Run console (UI) tests for this project's Java app - feed each test case's commands to the compiled program and check the output against recorded expected output. Use when asked to test the UI/CLI, add or run UI test cases, verify console output/behavior, or check that the app still behaves correctly after a change.
---

# Test UI

Drive the app's console (`System.in`/`System.out`) with recorded command
sequences and verify the output, stopping at the first failure. Test cases
live in [`test/ui-test-plan.md`](../../../test/ui-test-plan.md) so they
accumulate as a readable record of what's been verified.

## 1. Update the test plan (only if new commands/expected outputs were given)

If the user supplied new commands and expected outputs to test (rather than
just asking to (re-)run the existing plan), add them to
`test/ui-test-plan.md` as one or more new test case blocks, following the
format documented at the top of that file:

```
## TC<id>: <short title>
**Aim:** <what this test case checks, and why>

| Input | Expected Output |
|---|---|
| `command` | `text that must appear in the console output after this command` |
```

* Give each test case a clear aim: what behavior it verifies and why it
  matters (not just a restatement of the command).
* A test case can be a whole scenario: multiple input/expected-output rows
  are sent in order to a single fresh run of the program, so later rows can
  build on earlier ones (e.g. add a task, then list, then mark it done, then
  list again).
* Keep expected-output snippets to the distinguishing text for that step,
  not a re-transcription of the whole console output — the check is a
  substring search, not an exact match, so it's tolerant of formatting
  details like divider lines or banners.
* Pick a short, unique TC id (numbers are fine, e.g. `TC6`).

Only add test cases the user actually asked for or that directly cover a
change just made; don't invent a large speculative test suite.

## 2. Run the tests

Run the bundled runner from the repository root. It compiles the sources
under `src/main/java` (skip with `--skip-compile` if already built), then
runs each test case in the plan against a fresh instance of the program,
newest-first order preserved from the file:

```bash
python .claude/skills/test-ui/scripts/run_ui_tests.py test/ui-test-plan.md
```

If the app's main class, source directory, or output directory differs from
the defaults (`verity.Verity`, `src/main/java`, `out/production/ip`), pass
`--main-class`, `--source-dir`, or `--classes-dir` accordingly — check
`src/main/java` for the class containing `public static void main`.

Only run test cases the user asked about with `--skip-compile` if the code
hasn't changed since the last successful run.

## 3. Report the results

The script itself prints, for every test case it runs, the exact console
session: every input line sent (in order) and the full captured console
output. Show this transcript to the user (don't summarize it away) so they
can see the actual test session, not just a pass/fail verdict.

* **All passed:** report the count of test cases that passed.
* **A test case failed:** the script stops at the first failure (exit code
  1) and prints the failing test case's id/title, the expected output that
  was never found, and the actual console output produced. Report exactly
  that — which test case failed, expected vs. actual — and do not continue
  on to later test cases; the run has already stopped. Do not attempt to
  "fix" the test case's expected output yourself to make it pass — the
  mismatch means either the code has a bug or the expected output is wrong,
  and that's a judgment call for the user.
