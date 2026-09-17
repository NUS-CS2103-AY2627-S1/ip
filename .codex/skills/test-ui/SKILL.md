---
name: test-ui
description: Run scripted command-line UI tests for this Java project by comparing each command's actual output with its expected output and stopping at the first failure.
---

# Test the command-line UI

Use this skill when a user provides command/expected-output pairs or asks to test the Botavius interactive console.

## Workflow

1. Read `test/ui-test-plan.md`. Each test case must state its aim, input commands, and expected output.
2. Compile the Java sources with Java 25 before testing.
3. Run each test case as a fresh process, supplying its commands through standard input.
4. Compare captured output with expected output, preserving whitespace and line breaks. Normalize only platform line endings if needed.
5. Stop immediately at the first failed test. Report the test case, complete console input, actual output, and expected output.
6. If all cases pass, report a record of every console input and output.

Keep test cases in `test/ui-test-plan.md`. Each case must include an aim, an `Input` block, and an `Expected output` block. Expected output should include the banner, prompts, separators, responses, and goodbye message printed by the program.
