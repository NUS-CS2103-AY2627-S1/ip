---
name: test-ui
description: 'Run command-line UI tests from test/ui-test-plan.md. Use when testing Alfred or another interactive console program with command and expected-output cases, recording the full console session, and stopping immediately on the first failure.'
argument-hint: 'Optional: provide a test case name or command list to run'
user-invocable: true
disable-model-invocation: false
---

# Test UI

Run the console UI test cases defined in `test/ui-test-plan.md`.

## Test Plan Format

Each test case in the plan must contain:

- `Aim`: the behavior being verified.
- `Inputs`: the exact lines to send to the program, in order.
- `Expected output`: the exact output or required output fragments, in order.

The plan must also identify:

- the project root;
- the command used to compile the program;
- the command used to run it;
- any required runtime version or setup;
- how output is normalized, if whitespace is intentionally ignored.

Prefer exact output comparisons. If the plan explicitly allows normalization or output fragments, apply only that stated rule.

## Procedure

1. Read `test/ui-test-plan.md` and preserve the test case order.
2. Check the required runtime and build command. For this project, use Java `25.0.3.fx-zulu`.
3. Compile the program from the project root before starting the test session. Include every source file needed by the program. Treat compilation failure as a test-session failure and report the compiler output.
4. Start one fresh program process for each test case unless the plan explicitly says that state must be shared. Feed the listed inputs exactly as standard input.
5. Capture the complete console input and output for each case. Do not hide the banner, prompts, separators, error output, or exit output.
6. Compare actual output with the expected output according to the plan's comparison rule.
7. If a case fails, stop immediately. Do not run later cases. Report:
   - the failed test case and aim;
   - the exact console input;
   - the actual console output;
   - the expected output;
   - the first mismatch, if it can be identified.
8. If all cases pass, report a summary followed by the complete console transcript for every case.
9. Remove only temporary build artifacts created by the test run, unless the user asks to retain them.

## Required Report Format

For every completed case, show:

```text
Test case: <name>
Aim: <aim>
Input:
<input lines>
Output:
<complete captured output>
Result: PASS | FAIL
```

On failure, include no later test cases in the report and terminate the test session immediately.

## Alfred Command Notes

For the current Alfred UI, useful test inputs include:

- `todo <description>`
- `deadline <description> /by <date or time string>`
- `event <description> /from <start string> /to <end string>`
- `mark <one-based task number>`
- `unmark <one-based task number>`
- `list`
- `find <keyword>`
- `bye`

Date and time values are strings; do not parse or reinterpret them during comparison.
