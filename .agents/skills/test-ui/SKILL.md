---
name: test-ui
description: Run project console UI regression tests after every source-code update, or when asked to test the command-line interface or compare console output with expected text. Uses test/ui-test-plan.md and records prompt-provided command sessions there. Do not use for unit tests or browser-based GUI testing.
---

# Test the console UI

Use `test/ui-test-plan.md` as the source of truth for configuration and test cases.

## After a code update

Always invoke this skill after source code changes. First review the plan against the changed behavior and update its aims, inputs, or expected output when needed. Run the complete plan even when no plan changes are necessary.

## Prepare the plan

- If the user supplies test cases, record them in `test/ui-test-plan.md` before testing. Preserve existing cases unless the user asks to replace them.
- Give every case a unique heading plus `Aim`, `Inputs`, and `Expected output` sections in the format already used by the plan.
- Put the commands for one test case, one per line, in its `Inputs` block. Put the exact expected standard output for that complete console session in its `Expected output` block.
- Keep dependent commands in the same test case so they run in one program session. Each test case starts with a fresh task list.
- Compare output line-for-line. Only CRLF/LF differences and one final newline are ignored.
- If an input list or expected output is missing, do not run that test case; obtain or derive the missing expectation from an authoritative project requirement first.

## Run the tests

From the repository root, run:

```bash
python3 .agents/skills/test-ui/scripts/run_ui_tests.py
```

Pass `--plan PATH` only when the user explicitly chooses a different plan file. The runner:

1. Checks the configured Java major version and compiles the application once.
2. Starts a fresh program for each test case and sends all commands in that case to the same process.
3. Compares captured standard output with the expected output.
4. Stops immediately at the first compilation, execution, timeout, standard-error, or output-comparison failure.

Do not continue with later cases or retry automatically after a failure.

## Report the session

- Show the runner's complete test-session record, including every executed case's aim, console input, and actual console output.
- On success, state how many cases passed.
- On failure, clearly show the failing case, actual output, expected output, and comparison diff. Report that later cases were not run.
- Do not replace the transcript with a summary, because the transcript is the evidence of the UI behavior.
