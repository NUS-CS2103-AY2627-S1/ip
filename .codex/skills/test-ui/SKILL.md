---
name: test-ui
description: Run the project's scripted command-line UI test cases from test/ui-test-plan.md, compare actual and expected console output, and stop at the first failure.
---

# Test UI

Use this project-specific skill when the user asks to test the interactive Java command-line UI with scripted inputs and expected outputs.

## Test plan

Treat `test/ui-test-plan.md` as the source of truth. Each test case must contain:

- an `Aim` section describing what behavior is being checked;
- a `Command` fenced block containing the command used to start the program;
- an `Inputs` fenced block containing the lines entered into standard input; and
- an `Expected output` fenced block containing the complete expected console output.

Keep test cases in the order they should run. Add project-specific setup or assumptions to the plan rather than hiding them in the runner.

## Run the tests

Run the bundled standard-library-only runner from the repository root:

```powershell
& "<configured-python-3>" .codex/skills/test-ui/scripts/run_ui_tests.py
```

Replace `<configured-python-3>` with the workspace-configured Python executable or another available Python 3 interpreter. The runner defaults to `test/ui-test-plan.md`; pass `--plan <path>` for a different plan and `--timeout <seconds>` when a test needs a different limit.

The runner executes one test case at a time, merges standard error into the console transcript, and prints the command, input, output, and pass/fail result for every completed case. It must stop immediately after the first failed case. On failure, report the actual output, expected output, exit status, and (when useful) a concise unified diff. Do not continue to later cases after a failure.

Before running the plan, check that its commands are safe and scoped to this repository. Use Java 25 for Java build or execution commands, as required by `AGENTS.md`.

After the run, summarize the test session and include the runner's console transcript in the response. If the runner cannot start a command, times out, or cannot parse the plan, treat that as a failure and report the diagnostic.

The implementation is in [`scripts/run_ui_tests.py`](scripts/run_ui_tests.py).
