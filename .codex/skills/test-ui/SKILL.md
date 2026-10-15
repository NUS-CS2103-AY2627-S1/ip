---
name: test-ui
description: Run project-specific console UI tests from test/ui-test-plan.md. Use when validating an interactive or command-line program against planned commands, inputs, and exact expected output, including when a test session transcript and immediate failure reporting are required.
---

# Test UI

Use `scripts/run_ui_tests.py` to execute the test cases in `test/ui-test-plan.md`.

## Test-plan format

Record each test case in this exact structure. Keep the `Command` block to one shell command; use `&&` if compilation is required before starting the program.

````markdown
## Test Case: A short name

### Aim
Describe the behavior being checked.

### Command
```sh
java -cp build/classes/java/main ubis.Ubis
```

### Input
```text
list
bye
```

### Expected Output
```text
Expected complete console output
```
````

Use an empty `Input` block when the program needs no input. Exact whitespace matters in `Expected Output`; use a code block so all line breaks are preserved.

## Run tests

1. Update `test/ui-test-plan.md` with every scenario to test.
2. From the project root, run:

   ```sh
   python3 .codex/skills/test-ui/scripts/run_ui_tests.py
   ```

3. Read the session transcript printed by the runner. It prints each command, supplied input, and received output.

The runner stops at the first failed test. Its failure report displays both expected and actual output, so do not continue with later cases.

## Rules

Run commands only from the project root. Treat test-plan commands as project-maintained, trusted commands. Update the expected output deliberately whenever the intended UI changes.
