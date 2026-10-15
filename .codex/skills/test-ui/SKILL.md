---
name: test-ui
description: Run the project's scripted console UI tests from test/ui-test-plan.md, comparing each session's output exactly and stopping at the first failure.
---

# Console UI testing

Use this skill after every code update to the Java console application, and whenever its interactive behaviour needs to be checked against documented test cases. Before running it, review the plan and update it when the code change adds, removes, or alters user-visible console behaviour.

The source of truth is [test/ui-test-plan.md](../../../test/ui-test-plan.md). Keep every test case there, including its aim, newline-separated console inputs, and its complete expected standard output. Do not put test cases only in chat or in the runner.

## Test-plan format

The plan contains one `## Program command` shell block and one or more `## Test case: <name>` sections. Each test case must have these fields:

- `### Aim` — what user-facing behaviour the session verifies.
- `### Inputs` — a fenced text block; each line is entered into the console.
- `### Expected output` — a fenced text block containing the complete stdout, including prompts and blank lines.

Use a fresh program process for every test case. A test session must include a command that exits the program; otherwise the runner will time out.

## Run the tests

From the repository root, run:

```sh
python3 .codex/skills/test-ui/scripts/run_ui_tests.py
```

The runner requires Java 25, executes the documented program command for each case, and compares stdout exactly after normalising Windows line endings. It prints a console-input and console-output transcript for every session. On the first failure it immediately stops, displays the expected and actual outputs (plus stderr or a non-zero exit status when applicable), and returns a non-zero exit code.

When changing UI behaviour, update the affected expected-output blocks deliberately, then rerun the complete plan. Do not alter expected output merely to conceal an unintended regression. Report the result when handing off the code update.
