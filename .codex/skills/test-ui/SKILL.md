---
name: test-ui
description: Run scripted console UI tests for this Java chatbot using test/ui-test-plan.md. Use when asked to verify command/output transcripts, replay a list of chatbot inputs, or stop on the first mismatched console response.
---

# Test UI

Run console UI test cases defined in `test/ui-test-plan.md`. Each case records its aim, the user inputs to send, and the exact expected console output. The runner compiles the Java sources, executes the chatbot once per test case, captures the full console session, and stops immediately on the first failed case.

## Test plan source

Read `test/ui-test-plan.md` before running tests. Each test case must contain:

- `Aim`
- `Inputs`
- `Expected Output`

Use this exact structure:

```md
## Test Case: short-name
Aim:
One sentence describing the purpose.

Inputs:
```text
first command
second command
bye
```

Expected Output:
```text
full expected transcript here
```
```

The expected output must be the full console transcript for that case, including the greeting banner and separators, because the runner compares exact normalized output.

For a test that needs tasks to exist before the chatbot starts, add an optional
`Initial Data` block after `Expected Output`:

````md
Initial Data:
```text
T | 1 | read book
```
````

The runner isolates the data file for every case and restores any file that
existed before the test run.

## Run tests

From the repository root, run:

```bash
python3 .codex/skills/test-ui/scripts/run_ui_tests.py
```

The runner:

1. Reads `test/ui-test-plan.md`
2. Compiles `src/main/java/*.java`
3. Runs one chatbot session per test case
4. Prepares isolated initial task data when a case provides it
5. Saves the console input/output record to `_temp/ui-test-record.md`
6. Stops on the first failure and prints the expected and actual outputs

## Reporting

After running the tests:

- Report whether all test cases passed
- Point the user to `_temp/ui-test-record.md`
- If a case failed, report the failing test-case name and include the expected and actual outputs from the runner

## Resource

`scripts/run_ui_tests.py` is the standard-library-only test runner for this repository's console chatbot.
