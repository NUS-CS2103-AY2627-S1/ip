---
name: test-ui
description: Run planned console UI tests and compare each session's output with expected output. Use when asked to test command-line interactions or verify console output against a test plan.
---

# Test UI

Run the console UI test cases defined in `../../../src/test/ui-test-plan.md`. Each case
contains its aim, console inputs, and complete expected output.

## Run tests

1. Update `../../../src/test/ui-test-plan.md` before testing so its build command, run
   command, and test cases describe the program's current interface.
2. Run the project-local test runner from the repository root:

   ```bash
   python3 .codex/skills/test-ui/scripts/run-ui-tests.py src/test/ui-test-plan.md
   ```

3. Read the printed transcript for every passing case. Each transcript records
   the exact console input and output.
4. Stop after the first failure. The runner prints the failed case's aim and
   its expected and actual output; report those details without running later
   cases.

## Test-plan format

Keep the configuration headings and fields in the existing plan. Add each new
case under `## Test cases` with:

- an `### TC-...` heading;
- a one-line `**Aim:**` statement;
- an `#### Inputs` `text` code block; and
- an `#### Expected output` `text` code block.

The output block must contain the entire program output for that test session,
including welcome and farewell messages. The runner compares output exactly
apart from line-ending style and trailing newlines.

## Resource

`scripts/run-ui-tests.py` reads the plan, builds once, runs the application
once per test case, prints transcripts, and exits on the first failure.
