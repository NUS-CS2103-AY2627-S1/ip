---
name: test-ui
description: Use when validating a project's user interface or interactive behavior from a recorded command-and-output test plan, especially after code updates or when a UI smoke test is requested.
---

# Test UI

Run the project-specific console-driven UI test plan and report evidence.

## Source of truth

Read `test/ui-test-plan.md` from the repository root before running anything. Each test case must state:

- Aim
- Command
- Input, or `none`
- Expected output
- Match mode: `exact` or `contains`; default to `exact`
- Optional timeout in seconds; default to 60

Run only the commands listed in the plan. If a command is destructive, changes external state, or needs credentials, stop and ask for approval before running it.

## Execution

Run cases in document order from the repository root. Supply the recorded input through standard input when present. Capture standard output, standard error, and the exit code. Treat a missing command, timeout, nonzero exit code, or output mismatch as a failure.

For `exact`, normalize line endings only, then compare the complete output. For `contains`, require the expected text to appear verbatim in the captured output. Do not silently trim or rewrite output.

After every case, show this console record:

```text
$ <command>
Input:
<input or none>
Expected output:
<expected output>
Actual output:
<captured output>
Exit code: <code>
Result: PASS or FAIL
```

If a case fails, stop immediately. Report the actual and expected output and state that later cases were not run. Do not edit source files or the test plan to make a failure pass. Only report the full session as passing after every case passes.

## Common mistakes

- Running a different command than the one recorded in the plan
- Treating a zero exit code as sufficient when output is wrong
- Omitting standard error from the record
- Continuing after a failed case
- Claiming a visual UI check from console output alone
