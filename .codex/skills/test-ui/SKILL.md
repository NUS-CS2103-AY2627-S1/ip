---
name: test-ui
description: "Run repeatable console UI tests from test/ui-test-plan.md, compare exact output, and stop at the first failure. Use when testing this project's command-line interface."
---

# Test UI

Use this skill to run the console UI test cases documented in
[`test/ui-test-plan.md`](../../../test/ui-test-plan.md). Keep that file as the
source of truth for test inputs, expected output, and project-specific run
instructions.

## Test-plan format

Record each test case with all three headings below:

````markdown
### TC-01: Short descriptive name

**Aim:** What behavior this session verifies.

**Inputs:**
```text
command one
command two
```

**Expected output:**
```text
The complete console output, exactly as expected.
```
````

The input block is one program session: send its lines to standard input in the
listed order. The expected-output block must contain the complete output from
that session, including the greeting and goodbye messages when applicable.
Update the plan whenever UI behavior changes.

## Running tests

1. Read the test plan and follow its **How to run** instructions. Use Java 25,
   as required by this project.
2. Run each test case in its own fresh program process. Capture standard output
   and standard error together, and compare the captured output to the expected
   output exactly. Do not silently trim whitespace or omit blank lines.
3. After each passing test, show a transcript containing the console input and
   the captured console output.
4. At the first failing test, stop immediately. Show its input, actual output,
   and expected output, state that subsequent test cases were not run, and do
   not change application code unless the user separately asks for a fix.

When the test plan lacks expected output for a changed UI, report that the plan
needs updating instead of treating newly observed output as the expected result.

## Expectation-alignment mode

Use this mode only when the user explicitly asks to keep the application code
unchanged and to make the test cases match its current UI. On a failure, first
record the original input, expected output, and actual output. Then replace only
that test case's expected-output block in `test/ui-test-plan.md` with the
captured actual output, rerun the case, and report that the test plan—not the
application—was updated. Continue to stop at the first mismatch until it has
been aligned and verified.
