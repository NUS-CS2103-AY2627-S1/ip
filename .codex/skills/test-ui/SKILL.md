---
name: test-ui
description: Run console UI regression tests for this Java iP project from test/ui-test-plan.md. Use when asked to test chatbot commands, compare console output against expected output, record UI test cases, show a console input/output transcript, or stop immediately on the first UI test failure.
---

# Test UI

Run scripted console sessions for the chatbot and compare actual program output with expected output recorded in `test/ui-test-plan.md`.

## Workflow

1. Treat the current repository as the target unless the user identifies another repository.
2. Read `test/ui-test-plan.md`. If the user supplies new commands and expected output, add or update test cases in that file before running tests.
3. Ensure every test case has:
   - `Aim:`
   - `### Input` fenced block containing the console commands to send to the program
   - `### Expected Output` fenced block containing the expected program output
   - Optional `### Initial File path/to/file` fenced blocks for files to create before the test
   - Optional `### Expected File path/to/file` fenced blocks for files that should be produced
4. Run:

   ```bash
   python3 .codex/skills/test-ui/scripts/run-ui-tests.py
   ```

5. If a test fails, stop immediately. Report the failed test name, actual output, and expected output.
6. If tests pass, report the pass summary and include the console input/output transcript printed by the runner.

## Test Plan Format

Keep project-specific test cases in `test/ui-test-plan.md`.

Use this structure:

````markdown
# UI Test Plan

Compile command: `javac -d /tmp/duck-ui-test src/main/java/*.java`
Run command: `java -cp /tmp/duck-ui-test Duck`

## Test Case 1: Short name
Aim: What this test verifies.

### Input
```text
command one
command two
bye
```

### Expected Output
```text
program output only
```

### Initial File data/duck.txt
```text
file content before the session
```

### Expected File data/duck.txt
```text
saved file content
```
````

The input block records what the user types. The expected output block records only what the program prints. Each program session runs in an isolated temporary directory so runtime data cannot leak between tests or overwrite the user's files.

## Runner

`scripts/run-ui-tests.py` parses the plan, compiles once, and runs each test case in a fresh program session. It normalizes line endings and trailing spaces before comparison so platform differences do not cause noisy failures.
