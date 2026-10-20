---
name: test-ui
description: Run fail-fast console UI tests for this Java project from command and expected-output lists, maintain test/ui-test-plan.md, and show a complete input/output transcript. Use when asked to test Yapper's command-line interface or execute, add, or update UI test cases.
---

# Test UI

Test the Yapper console interface against the cases in `test/ui-test-plan.md`.

## Prepare the plan

1. Read `test/ui-test-plan.md` before testing.
2. When the user supplies test cases, record them in that file before running them. Preserve unrelated existing cases.
3. Give every test case a unique ID and specify:
   - its aim;
   - its ordered commands;
   - the exact expected output after each command.
4. Keep commands that depend on shared application state in the same test case. Run every test case in a fresh process unless the plan explicitly says otherwise.
5. If information needed to determine the expected output is missing, ask the user instead of inventing it.

## Run the tests

1. Confirm that `java` and `javac` report Java 25. Stop and report the mismatch if they do not.
2. Compile the sources before opening the test session. Use a generated build directory, not `src/main/java`, for class files.
3. For each test case, launch a fresh `Yapper` process and wait for its startup output.
4. Send commands one at a time in the recorded order. Capture the command and all output caused by it before sending the next command.
5. Compare actual and expected output exactly after normalizing only `CRLF` and `CR` to `LF`. Do not ignore whitespace, separators, punctuation, or blank lines unless the plan explicitly defines another rule.
6. On the first mismatch, terminate the running process immediately and do not execute later commands or test cases. Report the failing case and command together with clearly labeled expected and actual output.
7. If a process hangs or does not produce a complete response within the timeout recorded in the plan, treat that command as failed and terminate the process.

Use an interactive terminal session so input and output remain attributable to individual commands. Never send the next command before checking the current command's output.

## Report the session

Show the complete console transcript, including startup output, each input command, each corresponding output, and process termination. Mark each checked command as `PASS` or the first mismatch as `FAIL`. Preserve output verbatim in fenced code blocks.

Finish with the number of passed commands and test cases. For a failure, also state that the remaining tests were not run.
