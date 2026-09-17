---
name: test-ui
description: Run and verify console UI test cases defined in test/ui-test-plan.md. Use when testing an interactive command-line program against planned inputs and exact expected outputs.
---

# Test UI

Use this skill to execute the console UI tests specified in `test/ui-test-plan.md`. The test plan is the source of truth for the commands, input, expected output, and any setup or comparison rules.

## Before testing

1. Read `test/ui-test-plan.md` in full. If it is missing, malformed, or has no test cases, stop and explain what needs to be added; do not invent test cases.
2. Ensure every test case states its aim, command, inputs, and expected output. Record any required setup, working directory, and output-normalization rule in the plan as well.
3. Use Java 25 for Java build and run commands. Follow the command in each case exactly, supplying the listed inputs in order.

## Run the session

1. Execute test cases in their listed order, one at a time.
2. Capture the console transcript for every case: the command, each provided input, standard output, standard error, and exit status.
3. Compare the actual output to the expected output using the comparison rule in the plan. Use exact text comparison unless the plan explicitly permits a normalization such as platform line-ending differences.
4. On the first failure, immediately stop the session. Clearly report the test case, its aim, expected output, actual output, and exit status. Do not execute later test cases.

## Report results

After a completed or stopped session, show a readable transcript for every test case that ran. Label console input separately from program output so the interaction can be reviewed. State whether each executed case passed or failed. For a failure, include both the expected and actual output verbatim in fenced text blocks.

Do not change application code just because a test fails. Update `test/ui-test-plan.md` only when the user asks to change the planned cases or when essential test-plan information is missing and the user supplies it.
