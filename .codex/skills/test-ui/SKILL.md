---
name: test-ui
description: Run scripted console UI tests for this Java project from a recorded test plan, compare actual and expected output, and show the resulting transcript.
---

# Console UI Testing

Use this skill when the user asks to test the console user interface with one or more command-and-output cases. Keep the definitive test plan in `test/ui-test-plan.md`.

## Test-plan format

Record each requested test case before running it. A case must have:

- a descriptive name and its aim;
- the console inputs, one command per line in an `Input` fenced code block; and
- the complete expected console output in an `Expected output` fenced code block.

The plan's **Test setup** section must state the Java version and launch command. Use Java 25. Run each test case in a separate, fresh application process so that in-memory state cannot leak between cases. Add `bye` to a case's input only if it is part of the interaction being tested; closing standard input is otherwise sufficient to end the session.

Expected output comparisons are exact, including whitespace and line breaks. When output legitimately varies by time of day, use `{{TIME_OF_DAY}}` in the expected output; replace it with the value observed when the program starts before comparing. Do not use broad wildcards or omit output merely to make a test pass.

## Execution

1. Read `test/ui-test-plan.md`. If the user supplied a list of tests in the current request, add or update those cases in the plan first. If required aim, input, or expected output information is missing, ask for it rather than inventing expected behaviour.
2. Compile the application with Java 25 when necessary, then launch the stated command once per test case. Send the case's `Input` block to standard input and capture both standard output and standard error.
3. Compare the captured output with the case's resolved `Expected output` block. On the first failure, stop immediately. Report the case name and show the expected output alongside the actual output.
4. On success, continue to the next case.
5. In the final response, show a console-session record for every case that was run, with its input and actual output in fenced code blocks. State whether all cases passed. Do not claim that unrun cases passed.

Do not edit application source code while performing this skill unless the user separately asks for a fix.
