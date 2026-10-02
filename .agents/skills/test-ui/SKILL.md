---
name: test-ui
description: Run all planned console UI test cases from test/ui-test-plan.md, compare actual and expected output, persist failures, and report complete console transcripts. Use after project code changes or when the user asks to test the text UI.
---

# Test the Console UI

Use `test/ui-test-plan.md` as the source of truth for console UI tests.

## Record the test plan

When the user supplies new or revised test cases, update `test/ui-test-plan.md` before running them. Preserve unrelated existing cases. For every test case, record:

- a unique name or identifier;
- the aim of the test;
- any setup or preconditions;
- the ordered console input commands; and
- the complete expected console output, including startup and shutdown output.

Also keep the working directory, compile command, run command, Java version, session-isolation rule, and comparison rule current in the plan. Ask for clarification only when an omitted expected output or command makes the test impossible to evaluate.

## Run the tests

1. Work from the repository root containing `test/ui-test-plan.md`.
2. Read the whole plan and select the cases requested by the user. If no subset is requested, run all recorded cases in document order.
3. Run the inspectable repository script with `python test/run_ui_tests.py`. Pass `--jdk <JDK-home>` only when automatic JDK 25 discovery fails.
4. The script must compile once, start a fresh process per case, capture standard output and error, normalize only line endings, compare all other content exactly, continue after case failures, and replace `test/ui-test-results.md`.
5. If the script itself cannot run, follow the same procedure manually and record the blocker or results. Do not silently replace the documented runner with an unrecorded temporary script.

Do not modify application code merely to make a test pass unless the user separately asks for a fix.

## Report the session

Always show the test cases in execution order and a console transcript for every case that ran. Clearly distinguish entered input from program output while preserving the actual text, for example by prefixing input lines with `> ` and placing the transcript in a fenced text block.

For each passing case, report `PASS`. For every failing case, report `FAIL`, identify the first mismatch when practical, and show both the complete expected output and complete actual output in separate fenced text blocks.

After the run, replace `test/ui-test-results.md` with a durable latest-run report containing the run date and time, Java version, compile result, totals, execution order, and status of every case. Include inputs, actual output, expected output, standard error, and exit code for each failure. Record passing cases concisely. Never remove a recorded failure merely because another case also failed.
