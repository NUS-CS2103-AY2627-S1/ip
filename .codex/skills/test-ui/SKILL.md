---
name: test-ui
description: Run planned console UI tests from test/ui-test-plan.md, compare each session's output exactly, and stop at the first failure.
---

# Console UI testing

Use this skill when testing the project's command-line user interface against planned input/output sessions.

## Test plan

Keep the test cases in `test/ui-test-plan.md`. Each case must have an aim, an `Inputs` fenced `text` block, and an `Expected output` fenced `text` block. The plan's `test-ui-run` comment supplies the command that launches the application. Inputs are supplied to one fresh program session in their listed order.

Add or revise cases before running them when the requested behavior changes. Expected output must be the complete console output, including banners, prompts, dividers, and blank lines. Do not weaken comparisons with partial matching unless the user explicitly requests it.

## Run the tests

1. Compile the project using Java 25, if the plan's run command needs compilation.
2. Read each test case's `Inputs` and `Expected output` blocks. Invoke the command from `test-ui-run` directly in a temporary working directory, sending the complete Inputs block through standard input. Use one new process per test case; retain that temporary directory between cases only when the plan explicitly requires persistence.
3. Compare the captured output with the Expected output exactly after normalizing line endings. On the first mismatch, write `test/ui-test-session.md` with the test's input, expected output, and actual output; report the mismatch and do not run later cases.
4. If every case passes, write `test/ui-test-session.md` with every test case's input and its captured actual output. Present that file in the response.

The transcript must reflect actual captured program output, not copied expectations. Do not use a project runner script; execute the program command directly.

## Plan format

Use the structure in the existing plan. The run command is declared once, for example:

`<!-- test-ui-run: java -cp build/classes Edith -->`

Use Markdown headings beginning with `## Test case` so the runner can identify cases.
