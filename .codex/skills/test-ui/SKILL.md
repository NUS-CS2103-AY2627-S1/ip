---
name: test-ui
description: Run plan-driven console UI tests from test/ui-test-plan.md, compare each case with its expected output, and stop at the first failure while showing the full session transcript.
---

# Test UI

Run this skill from the repository root for command-line applications whose input is a sequence of console commands.

## Test plan

Read [test/ui-test-plan.md](../../../test/ui-test-plan.md) before testing. It must contain:

- Configuration entries for `Program`, optional `Working directory`, and optional `Setup`.
- One `## Test Case <id>: <title>` section per case.
- An `### Aim` section explaining the case.
- An `### Inputs` fenced text block containing the commands sent to the program.
- An `### Expected output` fenced text block containing required output lines in order.

Expected output lines are matched as non-empty substrings in order. This keeps tests useful when the program has harmless formatting or randomized flavour text while still checking the important response.

## Run tests

Use the bundled standard-library Python runner:

```text
python .codex/skills/test-ui/scripts/run-ui-tests.py --plan test/ui-test-plan.md
```

The runner performs any configured setup command once, starts a fresh program process for each test case, sends that case's inputs, and checks its output. It prints the console input and output for every case. If setup or a test case fails, it prints the actual and expected output and terminates immediately without running later cases.

For this Java project, use Java 25 when executing the plan's setup and program commands. Do not alter the application to make a test pass; report the failure with the transcript instead.
