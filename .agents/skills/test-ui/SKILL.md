---
name: test-ui
description: Run fail-fast console UI tests for this Java project from command and expected-output lists, maintain test/ui-test-plan.md, and report a transcript. Use when testing the application's command-line interaction or checking example command/output sessions.
---

# Test UI

Run console tests without changing application code.

## Test-case source

1. Read `test/ui-test-plan.md` from the repository root.
2. If the user supplies test cases, record or update them in that file before testing. Each case must have a name, aim, input block, and expected-output block.
3. Preserve input and output literally, including blank lines and spaces that affect the UI. Resolve an unclear expected output with the user instead of inventing it.

## Execution

1. Confirm that Java 25 is used. In this project, prefer the JDK configured by `.idea/misc.xml`; locate its executables when Java is not on `PATH`.
2. Compile `src/main/java/*.java` into a temporary directory so generated class files do not alter the repository.
3. Run each test case in the order recorded. Start a fresh program process for every case and send its complete input block to standard input.
4. Capture standard output exactly. Compare it with the case's expected output after normalizing only platform line endings (`CRLF` versus `LF`). Do not ignore whitespace, banners, separators, or extra lines.
5. Stop immediately on the first failed case. Do not run any later cases.

Compilation failure is a failed test session. Report the compiler output and do not start any case.

## Report

For every case that ran, show a console transcript containing both the entered commands and actual program output. Clearly distinguish input from output without altering either.

For a passing session, report the number of cases passed. For a failure, identify the failed case and show the complete expected and actual output in separate fenced blocks. State that later cases were not run because testing is fail-fast.

Do not modify production code to make a test pass unless the user separately gives explicit permission.
