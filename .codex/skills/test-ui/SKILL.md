---
name: test-ui
description: Run and verify the chatbot's console UI against command-and-output test cases recorded in test/ui-test-plan.md. Use after changes that affect user-visible chatbot behaviour.
---

# Test UI

Use this skill to run the project's console UI test plan and present an auditable test session.

## Test plan

Maintain [`test/ui-test-plan.md`](../../../test/ui-test-plan.md). Each test case must include:

- a `##` heading and an `Aim:` line;
- an `Input` `text` code block listing commands entered in one program session; and
- an `Expected output` `text` code block containing the exact chatbot output, excluding terminal echo of the input.

Update the plan whenever a user-visible command, response, or format changes. Keep existing cases that cover unchanged behaviour.

## Run tests

From the repository root, run:

```powershell
py -3 .codex/skills/test-ui/scripts/run-ui-tests.py
```

The runner compiles all Java files under `src/main/java` with the configured JDK, executes every test case in order, and prints the console input and output for each case.

If a test fails, it stops immediately and reports the test aim together with the expected and actual outputs. Do not continue to later test cases until the mismatch is resolved.

## Verify

Confirm that the runner reports every planned case as passing. When a command's expected response changes intentionally, update the corresponding test plan before rerunning it.
