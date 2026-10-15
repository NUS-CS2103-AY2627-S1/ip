---
name: test-ui
description: Run the Java text-user-interface test cases recorded in test/ui-test-plan.md and stop at the first mismatch.
---

# Test the text UI

Use this skill after changes to the Java text-user-interface application.

## Workflow

1. Read `test/ui-test-plan.md` and use its test cases as the source of truth.
2. Ensure Java 25 is being used.
3. Compile the application into a temporary directory without placing `.class` files in the source tree.
4. For each test case, send its listed commands to `Bot67` through standard input.
5. Check the output for the expected output listed by that test case.
6. Stop immediately when a test fails. Report the test name, complete console input, expected output, and actual output.
7. If all tests pass, show a concise record of every console input and output and state that all tests passed.

Tests should be deterministic and should normally end with `bye`. Update the test plan when command syntax or expected output changes. Run the skill again after every code update.

## Test case format

Each test case in `test/ui-test-plan.md` must contain an aim, exact input commands, and expected output checkpoints. When checking output, ignore the startup banner and separator lines unless a test explicitly tests them. Preserve the order of the remaining expected checkpoints.
