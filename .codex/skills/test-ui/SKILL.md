---
name: test-ui
description: Run the complete scripted console UI test plan from test/ui-test-plan.md, compare each program output exactly with its expected output, and report a complete test-session transcript.
---

# Test UI

Use this skill to execute the console UI test cases defined in `test/ui-test-plan.md`.

## Test-plan source

Treat `test/ui-test-plan.md` as the source of truth. Each test case must state:

- its aim;
- the command used to start the program;
- its console inputs; and
- its expected console output.

Use the headings and fenced-block format in that file so multi-line inputs and output remain unambiguous. Update the plan before testing whenever a requested test case is missing or its command, input, or expected output is incomplete.

## Run the tests

1. Read the complete test plan and run its test cases in order. Use Java 25 for Java build or run commands.
2. For each case, execute its command and provide the listed input exactly, preserving line breaks. Capture both standard output and standard error in the session record. Do not alter, normalize, trim, reorder, or omit output when comparing it to the expected output.
3. If the actual output matches the expected output, record that the case passed and continue.
4. If a case mismatches or its command fails, record that case's aim, expected output, actual output, exit status, and error output, then continue to the next case. Never stop the suite because of a failure.
5. After all cases have run, show a test-session record for every case, including passed and failed cases. For each case, include the command, console input, captured console output, and pass/fail result, followed by a concise summary of all failures.

Do not change application code merely to make a test pass unless the user separately asks for a fix. Keep the plan and the displayed transcript focused on user-visible console behavior.
