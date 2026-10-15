---
name: test-ui
description: Run scripted console UI test cases from test/ui-test-plan.md, compare each actual output with its expected output, and print the test-session transcript.
---

# Console UI testing

Use this project-specific skill when testing the program through its console user interface.

## Test plan

Read `test/ui-test-plan.md` before running tests. Each test case must document:

- its aim;
- the commands or console inputs to provide to the program; and
- the expected output for those inputs.

If the plan is missing, incomplete, or ambiguous, report that instead of inventing test expectations. Keep the test cases in the order written in the plan.

## Execution rules

1. Determine the project’s normal run command from the repository files or the plan. Use Java 25, switching to `java 25.0.3.fx-zulu` with SDKMAN on macOS when necessary.
2. Execute each test case in order, supplying its listed inputs to a fresh program process unless the plan explicitly requires a shared session.
3. Compare the captured actual output with the expected output. Use exact comparison after normalizing only the line-ending convention; do not ignore prompts, whitespace, or extra lines unless the plan explicitly says to do so.
4. Before moving to the next case, print a readable record containing the test-case name, console input, expected output, and actual output.
5. If a test case fails, stop immediately. Report the first failing case and show both the actual and expected outputs; do not run later cases.
6. If all cases pass, report that every executed case passed and retain the complete console input/output record in the response.

Do not modify application code or the test plan while executing tests. If the program cannot be launched, report the launch command, error output, and the test case that could not be executed.
