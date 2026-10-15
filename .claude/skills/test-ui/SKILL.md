---
name: test-ui
description: Run the console UI test cases recorded in test/ui-test-plan.md against the Ace Java program (src/main/java/ace/Ace.java), feeding each case's inputs as stdin and diffing the actual output against the expected output. Use this whenever the user asks to test the UI, run UI tests, check console output, verify the program's output against expected/sample transcripts, or mentions test/ui-test-plan.md or "test-ui" by name. Stops at the first failing test case and reports the diff — does not run remaining cases past a failure.
---

# test-ui

Runs this project's console-output test cases and reports whether the program's actual output matches what's recorded as expected. This is a simple compile-run-diff workflow — there's no test framework involved, because the program's entire observable behavior is what it prints to stdout in response to lines of stdin.

## Why fail-fast

Test cases run in order and the session stops at the **first** failure. Once one case fails, later cases were likely run against a program state you can no longer trust to interpret cleanly (and in a course project, one bug reported clearly is more useful than five bugs reported vaguely). Report the one failure well rather than rushing through the rest.

## Workflow

1. **Compile fresh.** Stale `.class` files are a classic source of "but I already fixed that" confusion, so always recompile before testing. The source lives under packages now (`src/main/java/ace/...`), so the compile command must recurse rather than glob just the top-level folder:
   ```bash
   javac -d out/production/ip $(find src/main/java -name "*.java")
   ```
   If this fails, stop and report the compiler errors — there's no point running tests against code that doesn't build.

2. **Read `test/ui-test-plan.md`** and parse out the test cases in the order they appear (see format below). If the file doesn't exist yet, say so and stop — don't invent test cases.

3. **For each test case, in order:**
   - Write its `Inputs` block to a temp file (one command per line, exactly as listed — this becomes the program's stdin).
   - Write its `Expected Output` block to a temp file (exactly as listed, this is compared byte-for-byte via string equality).
   - Run:
     ```bash
     .claude/skills/test-ui/scripts/run_ui_test.sh <input-tmp-file> <expected-tmp-file>
     ```
   - The script prints the input transcript and the actual output itself (that satisfies the "show a record of the console session" requirement — you don't need to re-print it yourself), then reports `RESULT: PASS` or `RESULT: FAIL`.
   - Before running each case, print its **Aim** and case number so the user can follow along as the session progresses — the script's transcript alone doesn't say *why* a case exists.
   - If the script exits non-zero (FAIL), **stop immediately.** Show the diff block the script printed (expected vs. actual), state clearly which test case failed and why, and do not run any subsequent test cases.
   - If it exits 0 (PASS), move on to the next case.

4. **When the session ends** (either all cases passed, or one failed and you stopped), give a one-line summary: how many cases ran, how many passed, and — if you stopped early — which case and line-by-line diff caused the stop.

Comparison is exact string equality (including whitespace, spacing inside `[T][ ]`-style brackets, and the `____...` separator lines) — the whole point of this project's UI tests is that the console output format itself is the spec, so don't normalize or trim anything before comparing.

## test/ui-test-plan.md format

Each test case is a level-3 heading followed by three parts: what the case is checking, the exact lines to feed the program as stdin (in order, ending in `bye` unless the case is deliberately about not exiting), and the exact expected stdout for that whole session.

```markdown
### Test Case N: <short name>

**Aim:** <one sentence: what behavior this case is checking and why>

**Inputs:**
\```
<command 1>
<command 2>
...
bye
\```

**Expected Output:**
\```
<the exact, full stdout the program should produce for the above inputs,
run fresh from an empty task list>
\```
```

Keep each case self-contained (fresh program start, empty task list) so cases can be understood — and re-run — independently.

## Adding new test cases

When the user asks to add a case (e.g. after fixing a bug or adding a command), append a new `### Test Case N` section to `test/ui-test-plan.md` following the format above. Prefer deriving the exact expected output from a real run of the *fixed* program rather than typing it from memory — it's easy to get a whitespace character wrong by hand, which would make the test fail for the wrong reason.
