---
name: test-ui
description: Run scripted command-line UI test cases for this Java project, compare each session with its expected output, and stop at the first failure.
---

# Test UI

Use this skill for black-box tests of the project's console UI. The test plan is the source of truth and must be kept at `test/ui-test-plan.md`.

## Test case format

Each test case in the plan must include all of the following:

- `Aim`: what behavior the case verifies.
- `Command`: the command that starts the program for this case.
- `Inputs`: the exact lines sent to standard input, in order. Use an empty block when no input is required.
- `Expected output`: the complete expected standard output, including meaningful whitespace and prompts.

When the user supplies test cases inline as lists of commands and expected outputs, first record them in `test/ui-test-plan.md` using this format. Preserve the supplied order. Add only the project-specific setup information needed to run them, such as the Java entry point and compilation command.

For example, accept a request shaped like this and convert it into the plan's Markdown test cases:

```yaml
test_cases:
  - name: Exit immediately
    aim: Verify the exit response.
    command: java -cp _temp/test-ui/classes Shai
    inputs: [bye]
    expected_output: |
      <complete stdout, including the greeting>
```

## Run the tests

1. Read `test/ui-test-plan.md` completely and identify the test cases in order.
2. Confirm that Java 25 is selected. Compile the application once using the plan's setup command, if one is specified.
3. For each test case, start a fresh process using its `Command`, send its `Inputs` exactly, and capture standard output. Do not reuse application state between cases.
4. Compare actual output with expected output exactly after normalizing only CRLF/LF line endings. Do not trim spaces, tabs, blank lines, or trailing newlines.
5. Stop immediately on the first failed test case. Report its aim, command, inputs, expected output, and actual output. Do not run later cases or label the session as passed.
6. If all cases pass, report the number of cases passed.

## Test-session record

During the run, keep a transcript containing each test case's console input and actual console output. Show the transcript in the final response after testing, including the first failure when the session stops early. Also save the transcript as a temporary artifact under `_temp/test-ui/` when practical; do not replace the test plan with generated output.

Use a readable transcript format such as:

```text
=== Test Case: <name> ===
--- INPUT ---
<console input>
--- OUTPUT ---
<actual console output>
=== RESULT: PASS ===
```

For a failure, use `=== RESULT: FAIL ===` and include a separate expected-output section. Keep the transcript faithful to the captured session; do not rewrite output to make it match the plan.

## Safety and reporting

Run only commands needed by the test plan in the project workspace. Do not commit or push changes. If a command cannot be run, report the setup error and the command that failed. Distinguish setup errors from test failures.
