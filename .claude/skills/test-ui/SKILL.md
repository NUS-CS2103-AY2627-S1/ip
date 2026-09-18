---
name: test-ui
description: Run the console UI test cases recorded in test/ui-test-plan.md against the compiled Java program, verifying each test case's commands produce the exact expected console output. Use when asked to test the UI/CLI, run UI tests, verify console output, or check that the chatbot's behavior still matches the test plan.
---

# Test UI

Run the project's recorded UI test cases end to end: compile the program, feed each test case's commands to it as if typed at the prompt, and check the resulting console session against the expected transcript recorded in `test/ui-test-plan.md`.

## Run the tests

From the repository root:

```bash
python3 .claude/skills/test-ui/scripts/run_ui_tests.py test/ui-test-plan.md
```

The script:

1. Reads `## Setup` from the test plan for the source directory and main class, and compiles all `.java` files there.
2. Runs each `## Test Case: <name>` in the order it appears in the file, feeding its `**Input:**` commands to the program's stdin (the last command must be the one that exits the program, e.g. `bye`, so the full session can be captured).
3. Reassembles the program's stdout into a console transcript that interleaves each typed command with the block of output that followed it — the same shape as the `**Expected Output:**` in the test plan.
4. Prints that transcript for every test case as it runs, so the console session is visible.
5. Compares the transcript to the test case's `**Expected Output:**`. On the first mismatch, prints both the expected and actual transcripts in full and stops immediately — later test cases are not run. Report this failure (test case name, aim, and the expected/actual diff) to the user.
6. If every test case passes, reports the total count.

Report the outcome to the user: which test cases passed, and — if the run stopped early — which one failed and why, quoting the actual vs. expected output the script printed.

## Adding or updating test cases

Add a new `## Test Case: <name>` section to `test/ui-test-plan.md` with `**Aim:**`, `**Input:**`, and `**Expected Output:**` (see existing entries for the exact format). To get the exact expected transcript text without hand-computing indentation, generate it from a real run instead of guessing:

```bash
python3 .claude/skills/test-ui/scripts/run_ui_tests.py test/ui-test-plan.md --record "<name>"
```

This runs just that one test case's `**Input:**` and prints its actual transcript with no comparison — paste that output verbatim as the `**Expected Output:**` block. Only do this after confirming the actual behavior shown is correct, since `--record` reports reality, not correctness.

## Resource

`scripts/run_ui_tests.py` is the bundled runner. It only depends on `javac`/`java` being on `PATH` and Python's standard library.
