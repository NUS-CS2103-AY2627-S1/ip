---
name: test-ui
description: Run and verify command-line UI acceptance tests recorded in test/ui-test-plan.md. Use after every code update in this repository, and when asked to test the project's text UI, compare console output with expected output, or execute the UI test plan.
---

# Test the command-line UI

Use `test/ui-test-plan.md` as the source of truth for the launch command, comparison rules, and ordered test cases. Accept new or revised lists of commands and expected outputs from the user by recording them in that file before testing.

## After a code update

Before running tests, compare the completed code change with `test/ui-test-plan.md`:

- Update the plan if commands, expected console output, build or launch steps, setup, or covered behavior changed.
- Leave the plan unchanged if it still describes the relevant behavior accurately, and report that decision.
- Derive expected output only from explicit requirements and established project behavior. Do not change expected output merely to make a faulty implementation pass.
- Run the applicable test cases after the final code edit. If a test-driven fix requires another code edit, run the applicable cases again.

## Maintain the test plan

For every test case, record:

- a unique ID and short name;
- the aim;
- the exact console input, in order; and
- the exact expected program output.

Also record the working directory, launch command, timeout, setup or reset steps, output-normalization rules, and expected exit behavior when they matter. Do not invent missing expected output. If information required to run or judge a test cannot be discovered from the repository, ask for it before starting the test session.

## Run the tests

1. Read the entire test plan and validate that every selected case contains an aim, input, and expected output.
2. Run any build, setup, or reset command specified by the plan. Do not silently add state-changing preparation that the plan does not authorize.
3. Run test cases in their recorded order. Unless the plan explicitly defines a shared session, start a fresh program process for each case so state cannot leak between cases.
4. Send the case's input lines to the program exactly as recorded. Capture stdout and stderr in their observed order. Enforce the recorded timeout and ensure the child process is terminated when the case ends.
5. Compare the program output with the expected output using the plan's rules. Default to exact text equality after normalizing only CRLF to LF and ignoring one final newline. Do not ignore prompts, whitespace, timestamps, paths, or other varying text unless the plan explicitly permits it.
6. Stop immediately on the first mismatch, unexpected exit status, timeout, or launch error. Do not run later cases.

## Report the session

Always show a console transcript for every case that ran. Make tester-entered input unambiguous without altering the captured program output, for example:

```text
$ <launch command>
> <tester input>
<program output>
```

For a successful run, report the number and IDs of cases that passed and include the transcript.

For a failure, identify the failed case and failure type, state that the session stopped, and show:

- the console transcript up to the failure;
- the full expected output;
- the full actual output; and
- a focused diff or the first differing line when useful.

Never claim that an unexecuted case passed. Keep remaining cases explicitly marked as not run.
