---
name: test-ui
description: Run planned console UI tests for this Java project and compare each session's output exactly. Use when asked to test Moon's command-line interface or verify its user-facing output.
---

# Test UI

Run the console test cases defined in [`test/ui-test-plan.md`](../../../test/ui-test-plan.md). Each test case has an aim, input, and expected output.

## Test plan format

Each test case must follow this structure:

````markdown
## Test case: Short name

**Aim:** What this interaction verifies.

### Input
```text
command one
command two
bye
```

### Expected output
```text
program output here
```
````

The expected output contains only text printed by the program; do not include terminal-echoed input.

## Run tests

1. Read `test/ui-test-plan.md` before running tests. Add or update cases when the requested behavior is not covered; every case needs an aim, input, and expected output.
2. Run the bundled runner from the repository root using Python:

   ```bash
   python .codex/skills/test-ui/scripts/run_ui_tests.py
   ```

   In this project, compile and run with Java 25. If `python` is unavailable, use the bundled workspace Python runtime.
3. Show the runner's full session record, which includes the console input and actual console output for every completed case.
4. On the first failure, stop. Report that case's aim plus its expected and actual outputs. Do not run later test cases.

The runner compiles all files in `src/main/java` before testing and writes class files only to the ignored `out/ui-tests` folder.
