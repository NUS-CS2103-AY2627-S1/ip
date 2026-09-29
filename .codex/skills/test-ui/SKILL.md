---
name: test-ui
description: Run command-line UI test cases recorded in a project's test/ui-test-plan.md, compare actual and expected output, and stop immediately on the first failure.
---

# Test UI

Use this skill for deterministic, command-line user-interface testing of the project.

Invoke this skill after every code update. Before running it, review `test/ui-test-plan.md` and update the plan if the code change affects any documented command, input, behavior, or expected output.

## Workflow

1. Read `test/ui-test-plan.md` from the repository root. Treat its test cases and setup instructions as the source of truth.
2. Confirm the required Java version from the project instructions before running Java commands. Use Java 25 for application or build tasks.
3. Run each test case in the order listed. Supply the documented inputs exactly as console input, and capture both stdout and stderr in the session record.
4. Compare the captured output with the documented expected output. Ignore only differences explicitly permitted by the plan; otherwise compare content and ordering exactly.
5. After each case, show a record containing the command, console input, actual console output, and pass/fail result.
6. If a case fails, stop immediately. Report the case's aim, actual output, and expected output, and do not run later cases.
7. If all cases pass, report the complete session record and an overall pass result.

## Test-plan format

Keep all cases in `test/ui-test-plan.md`. Each case must specify:

- Aim
- Command
- Inputs
- Expected output

Include any working-directory, build, timeout, environment, or output-normalization instructions near the top of the plan. Prefer commands that terminate on their own; apply a bounded timeout when a command could hang. Do not change source files or the plan merely to make a test pass.

## Output discipline

Preserve the console input and output visibly in the final report, using fenced text blocks where helpful. On failure, distinguish expected output from actual output and identify the first failing case clearly.
