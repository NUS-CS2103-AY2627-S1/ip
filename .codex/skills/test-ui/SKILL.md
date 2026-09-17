---
name: test-ui
description: Run scripted command-line UI test cases from test/ui-test-plan.md, compare each program output with its expected output, and record the console session. Stop immediately on the first failure.
---

# Test UI

Use this skill to execute black-box command-line UI tests for this project.

## Test plan

Record the test cases and relevant execution information in `test/ui-test-plan.md`. Create the file and its parent directory if needed. Each test case must include:

- Aim
- Inputs: the exact command and any interactive input
- Expected output: the exact output or an explicitly documented comparison rule

Keep commands reproducible and state prerequisites, working directory, environment variables, and the Java/runtime command when relevant.

## Execute tests

1. Read `test/ui-test-plan.md` and execute test cases in listed order.
2. For each case, run the program with the specified command and inputs, capturing stdout, stderr, exit status, and any prompts. Preserve output line breaks and ordering.
3. Compare actual output with expected output according to the test plan. Use exact comparison by default; allow normalization only when the plan explicitly says so (for example, a timestamp or platform-dependent path).
4. After each case, append or produce a test-session record showing the case name, console input, console output, exit status, and pass/fail result. Do not hide output behind a summary.
5. If any case fails, stop immediately. Report the first failing case with the complete actual and expected outputs and do not run later cases.
6. At completion, report the pass/fail status of every case reached and the location of the test plan and session record. Do not modify production code to make a test pass unless separately requested.

## Session record

Use a clearly marked transcript format, such as:

```text
=== Test Case: <name> ===
Input:
<commands and interactive input>
Output:
<stdout and stderr>
Exit status: <status>
Result: PASS
```

For failures, include both `Expected output:` and `Actual output:` sections, then terminate the session.
