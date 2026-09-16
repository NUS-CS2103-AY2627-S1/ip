---
name: test-ui
description: Run fail-fast console UI tests from lists of commands and expected outputs, recording the test plan and complete session transcript. Use when asked to test this project's command-line interface or verify console interactions.
---

# Test UI

Turn supplied console commands and expected outputs into repeatable UI tests. Run them in order and stop at the first failure.

## Record the plan

Create or update `test/ui-test-plan.md` before running tests. Preserve the user's commands, ordering, casing, and expected whitespace. Record all information needed to repeat the session, including:

- the build and program launch commands;
- the working directory, Java version, and timeout when relevant;
- every test case's aim, inputs, and complete expected output.

Use this structure for each case:

````markdown
## Test case: TC01 - Short descriptive name

### Aim

Explain the behavior this case verifies.

### Inputs

```text
first command
second command
```

### Expected output

```text
Complete expected console output for this program run.
```
````

Each test case must have a meaningful aim, one or more input commands, and the complete expected output. Use a separate test case when the program should restart. Inputs within one test case are sent to the same process in their listed order. Do not include input echoes in expected output unless the program itself prints them.

If the launch command or expected-output boundaries are unavailable, ask the user for the missing information before testing. Do not invent expected behavior.

## Run the session

1. Build and run from the repository root unless the plan specifies another working directory. Use Java 25 for this project.
2. Run one fresh program process for each test case and send its inputs in order.
3. Capture combined console output and compare it with the case's expected output. Normalize only platform line endings and the presence of one final newline; preserve all other whitespace.
4. Append a readable record to `test/ui-test-session.txt` containing the case name and aim, program command, console input, console output, and PASS or FAIL result.
5. Show the complete session record to the user when testing finishes.

## Fail fast

On the first timeout, nonzero exit, or output mismatch:

- stop the test session immediately;
- do not run or retry any later case;
- identify the failed case;
- show the actual and expected outputs verbatim;
- show the session record produced up to the failure.

On success, report how many cases passed and show the complete session record. Do not change application code to make a test pass unless the user separately asks for a fix.
