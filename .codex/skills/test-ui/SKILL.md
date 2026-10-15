---
name: test-ui
description: Run Chocolate's planned console UI tests and compare actual output with expected output. Use after chatbot behavior changes or when asked to verify console commands and responses.
---

# Test UI

Run the test cases recorded in `test/ui-test-plan.md`. Each case must state its aim, console input, and expected output using the format already present in that file.

## Run tests

From the repository root, run:

```bash
python3 .codex/skills/test-ui/scripts/run_ui_tests.py
```

The runner compiles every Java file in `src/main/java` into a temporary directory, starts `Chocolate`, and supplies each test case's input. It prints the console input and output for every completed case.

Stop at the first failed test. Report the test aim together with the expected output, actual output, and test transcript. Do not change application code merely to make an expected-output comparison pass; first determine whether the implementation or the plan is wrong.

## Maintain the plan

When a code update changes console behavior, add or update a relevant test case before running this skill. Keep expected output exact, including task status icons and task-type prefixes.
