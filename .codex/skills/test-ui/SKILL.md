---
name: test-ui
description: Run console UI test cases listed in a project's test/ui-test-plan.md, comparing each actual output with its expected output and stopping at the first failure.
metadata:
  short-description: Run fail-fast console UI tests
---

# Test UI

Use this skill when the user asks to run or update the project's console UI tests.

## Test-plan format

Read `test/ui-test-plan.md` before running anything. Each test case must contain:

- an aim;
- the exact console input, represented as one command per line in an `Input` code block;
- the expected output in an `Expected output` code block.

The plan may also state the command used to build or launch the program. If it does not, inspect the project and use the simplest appropriate command. For this Java project, use Java 25 (`sdk use java 25.0.3.fx-zulu` on macOS) before building or running.

## Execution

Run test cases in the order written in the plan. For each case:

1. Run the program with the listed input.
2. Capture the complete console input and output, preserving line breaks.
3. Compare the captured output with the expected output. Ignore only unavoidable terminal prompts or line-ending differences; do not silently normalize meaningful whitespace.
4. Record the result in the final test-session report.

If any case fails, stop immediately. Report the case, actual output, and expected output, then include the console input/output record for the cases that ran. Do not continue to later cases.

If all cases pass, report that every case passed and include the complete console input/output record.

Do not modify application source code to make a test pass. If the plan is malformed or the program cannot be launched, report that as a test-session failure with the relevant command and console output.
