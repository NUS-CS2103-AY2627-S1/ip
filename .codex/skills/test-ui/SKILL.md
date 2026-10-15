---
name: test-ui
description: Run command-line UI test cases from test/ui-test-plan.md after code updates, compare each program session with its expected output, print the console input and output record, and stop immediately on the first failure. Use when testing a CLI application's end-to-end text interaction or when a user provides commands and expected console output.
---

# Test UI

Run deterministic end-to-end tests for a command-line application.

The test plan records each test case's aim, input commands, and exact expected
output. Run the bundled script so each test case is isolated, output is shown
as a console session record, and the first mismatch terminates the run.

## Test-plan format

Keep the test cases in `test/ui-test-plan.md`. Use one `## Test Case: ...`
section per case, with these required subsections:

````markdown
## Test Case: Short descriptive name

- Aim: Explain what behavior this test verifies.

### Input

```text
command one
command two
```

### Expected output

```text
the exact console output, excluding only the final newline
```
````

Keep the expected output exact. The runner normalizes CRLF to LF, removes
incidental trailing spaces, and ignores only a final newline; it preserves
leading spaces, blank lines, and all other output.

## Add or update test cases

When the user supplies a list of commands and expected outputs, record each
case in `test/ui-test-plan.md` before running it. Include a concise aim, keep
the commands in the input block in execution order, and copy the complete
expected console output into the expected-output block. Do not silently omit
the banner, dividers, or other output produced by the program.

## Run the UI tests

1. Read `test/ui-test-plan.md` and confirm that the expected output matches the
   current product behavior.
2. Build the application if needed.
3. Run the runner from the repository root:

   ```bash
   python3 .codex/skills/test-ui/scripts/run_ui_tests.py \
       --plan test/ui-test-plan.md \
       --program "java -cp build/classes/java/main LuckyNoSlacky"
   ```

Use `--cwd` when the program must run from another directory and `--timeout`
to change the per-test timeout. The default program command is
`java -cp build/classes/java/main LuckyNoSlacky`.

## Failure behavior

- Run test cases in the order listed in the plan.
- Start a new program process for each test case.
- Print the input and actual console output for every completed test case.
- Stop immediately at the first non-zero exit, timeout, or output mismatch.
- On failure, print the expected output, actual output, and a unified diff.
- Do not continue to later test cases after a failure.

## Resources

Use `scripts/run_ui_tests.py` for deterministic plan parsing, process execution,
comparison, session recording, and fail-fast behavior.
