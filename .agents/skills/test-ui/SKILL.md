---
name: test-ui
description: Run this project's command-line UI test cases from test/ui-test-plan.md, compare their console output with expected output, and report the complete test session. Use when testing interactive console behavior.
---

# Test UI

Use this skill to execute the manual command-line UI test cases defined in
[`test/ui-test-plan.md`](../../../test/ui-test-plan.md).

## Test plan format

Each test case must have all four fields below. Use fenced text blocks for
`Inputs` and `Expected output` so whitespace and blank lines are unambiguous.

````markdown
## Test case: <short name>

**Aim:** <what this test verifies>

**Command:**
```text
<command that starts the program>
```

**Inputs:**
```text
<one console input line per line; leave the block empty for no input>
```

**Expected output:**
```text
<the complete expected console output>
```
````

`Command` can contain a sequence of shell commands when setup is needed; the
last command must start the interactive program. Keep the command independent
so each test case starts with a fresh program process.

## Workflow

1. Read `test/ui-test-plan.md`. If a case is missing its aim, command, inputs,
   or expected output - or contains a template placeholder such as
   `<replace ...>` - stop and ask for the plan to be completed before testing.
2. Confirm the plan's commands run this repository's program. For Java work,
   use Java 25 and run any required build step exactly as the plan specifies.
3. Run each test case in order. Supply the `Inputs` block to the program's
   standard input, capture its console output (including standard error), and
   retain the exact input and output transcript.
4. Compare actual and expected output exactly after normalizing line endings
   (`CRLF` and `LF` are equivalent). Do not trim, reorder, or otherwise ignore
   whitespace.
5. After a passing case, display its aim, command, console input, console
   output, and a `PASS` result. Continue with the next case.
6. On the first failure, immediately stop the session. Display the case aim,
   command, console input, actual output, expected output, and `FAIL`. Do not
   execute later cases.
7. Finish with a concise summary stating either that all executed cases passed
   or that testing stopped at the first failing case.

Do not change application code merely to make a UI test pass unless the user
also asks for a fix.
