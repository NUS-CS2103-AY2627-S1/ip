---
name: test-ui
description: Run fail-fast, transcript-backed console UI tests defined in test/ui-test-plan.md. Use when validating command-line input and output against an explicit test plan.
---

# Test Console UI

Run the console UI test cases in `test/ui-test-plan.md`. This skill is for deterministic terminal interactions, not unit tests or GUI testing.

## Test-plan contract

Use `test/ui-test-plan.md` as the source of truth. Keep the document under version control. It must include:

- the command that builds and starts the program, including any required Java version;
- one numbered test case per fresh program session;
- for every test case: its aim, its ordered command inputs, and the exact expected output after each input;
- an exact expected startup-output block when the program prints one before accepting input.

Use fenced plain-text blocks for inputs and expected output. Match spaces, blank lines, punctuation, and line endings exactly unless the plan explicitly marks a value as variable and defines how to check it.

## Run the tests

1. Read the complete test plan and verify that every test case follows the contract. If it is incomplete or ambiguous, report that before running the program.
2. Build the application with the command from the plan. For this project, use Java 25 and write generated class files only to a temporary or ignored output directory.
3. Run every test case in its own fresh process. First compare the program's startup output, then supply each listed input in order and compare the response produced after that input with its corresponding expected-output block.
4. Keep a transcript as you test. It must show both console input and console output in their original order. Label user-entered lines with `INPUT>` and program lines with `OUTPUT>`; do not alter whitespace in the recorded output.
5. On the first mismatch, immediately stop the current process and do not run further cases. Report the test-case identifier, the command input, and clearly separated **Expected output** and **Actual output** blocks. Include the transcript collected so far.
6. If every comparison succeeds, report that all cases passed and show the complete transcript in the response. Do not claim a pass merely because the program exited successfully.

## Maintaining the plan

Update `test/ui-test-plan.md` when console behavior intentionally changes. Add a focused case for each new command, error path, or state change. Keep expected output exact enough that a regression is observable.

