---
name: test-ui
description: Run Nova console UI test cases from test/ui-test-plan.md, compare complete console output exactly, stop at the first failure, and report the test transcript. Use when asked to test, verify, or regression-test this project's command-line UI.
---

# Test the console UI

Use this skill from the repository root.

## Test cases

Read `test/ui-test-plan.md` before testing. When the user supplies commands and expected outputs, add or update cases in that file before running them. Every case must contain:

- a unique `##` heading;
- an `Aim` section explaining the behavior under test;
- an `Input` fenced `text` block containing commands in entry order; and
- an `Expected output` fenced `text` block containing the complete expected standard output from a fresh program run.

Keep expected output intentional. Do not replace it with actual output merely to make a failing test pass.

## Run the tests

Run:

```shell
python3 .agents/skills/test-ui/scripts/run_ui_tests.py
```

The runner prefers the project's required SDKMAN Java installation at `~/.sdkman/candidates/java/25.0.3.fx-zulu`. Pass `--java-home <path>` only when Java 25 is installed elsewhere. It compiles all Java source files into a temporary directory, starts a fresh `Nova` process for each case, and compares stdout with the case's expected output. It normalizes CRLF to LF and disregards only trailing newline characters.

The runner prints the console input and actual console output for every executed case. Preserve this transcript in the response so the user can see the test session.

If a case fails, stop immediately. Report its aim, input, expected output, actual output, and any runtime error. Do not run later cases. If all cases pass, report the number of passing cases.

