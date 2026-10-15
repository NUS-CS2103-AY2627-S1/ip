---
name: test-ui
description: Run console UI tests for this Java chatbot project from a Markdown test plan. Use when asked to test command-line chatbot behavior, compare typed command sequences against expected console output, update or run test/ui-test-plan.md, show a console transcript, or stop immediately on the first UI test failure.
---

# Test UI

Run command-line UI tests from `test/ui-test-plan.md`. Each test case records its aim, input commands, and expected full console output. After running tests, show the generated console-session record so the user can inspect what input was sent and what output came back.

## Test Plan Format

Keep test cases in `test/ui-test-plan.md` using this shape:

````markdown
# UI Test Plan

Program command: `java -cp src/main/java Dukey`
Compile command: `javac src/main/java/*.java`
Data file: `src/main/data/dukey.txt`

## Test Case: Greets and exits

Aim: Verify that `bye` exits the chatbot.

Initial data:
```text
T | 1 | read book
```

Input:
```text
bye
```

Expected output:
```text
____________________________________________________________
...
____________________________________________________________
```
````

The compile command and data file are optional. The program command is required. Use `Initial data` to reset the data file before a test. Use `Expected data` to check the saved data file after a test. If the user provides test cases in the prompt, record or update them in `test/ui-test-plan.md` before running the tests.

## Run Tests

From the repository root, run:

```bash
python3 .codex/skills/test-ui/scripts/run-ui-tests.py --plan test/ui-test-plan.md
```

The runner compiles first when `Compile command:` is present, writes `Initial data` to `Data file` when present, then runs the program once per test case with that test case's `Input` block as standard input.

## Report Results

- If every test passes, report the pass count and show the transcript saved at `_temp/ui-test-session.md`.
- If a test fails, stop immediately. Report the failing test name, then show expected output and actual output from the runner. Do not continue to later test cases.
- Preserve exact output checks except for final trailing newlines, which the runner normalizes.

## Resource

`scripts/run-ui-tests.py` parses the Markdown plan, runs the configured program, compares actual output to expected output, and writes `_temp/ui-test-session.md`.
