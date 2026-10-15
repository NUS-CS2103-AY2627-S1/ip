---
name: test-ui
description: Run fail-fast console UI tests for the Zikiai Java chatbot using test cases recorded in test/ui-test-plan.md. Use when asked to test chatbot commands, compare console output with expected output, verify a UI interaction, add or update console test cases, or show a test-session transcript.
---

# Test UI

Use `test/ui-test-plan.md` as the source of truth for UI test cases. Each case must contain:

- a unique `##` heading;
- an `**Aim:**` statement;
- a fenced `### Input` block containing commands in entry order;
- a fenced `### Expected output` block containing exact program output.

Use `{{SEPARATOR}}` and `{{BANNER}}` in expected output when appropriate. Their exact expansions are defined in the plan.

## Run the test plan

From the repository root, select Java 25 and run:

```bash
source /Users/ziqiai/.sdkman/bin/sdkman-init.sh && \
sdk use java 25.0.3.fx-zulu >/dev/null && \
python3 .agents/skills/test-ui/scripts/run_ui_tests.py
```

The runner must:

1. Compile all Java files recursively below `src/main/java` into a temporary directory.
2. Start a fresh `zikiai.Zikiai` process for every test case.
3. Send the case's input commands to standard input in order.
4. Print a record of the console input and actual output.
5. Compare actual output with the expanded expected output exactly, except for newline style and trailing final newlines.
6. Stop immediately on the first failure.
7. On failure, print the input, expected output, actual output, and unified diff.

Do not continue to later cases after a compile error, nonzero program exit, timeout, or output mismatch.

## Maintain tests

After every project code update, review `test/ui-test-plan.md`. Update or add cases when the change adds or modifies commands, output, error handling, or other console-visible behavior. Record no plan change when existing cases already cover the update. Never change expected output merely to hide an unexplained failure; first confirm that the new behavior matches the user's requirement.

Invoke this skill and run the entire plan after every project code update, whether or not the plan changed. In the final response, report the number of passing cases and identify the first failure when applicable. The runner's transcript is the required console-session record.
