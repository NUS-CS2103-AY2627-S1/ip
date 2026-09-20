---
name: test-ui
description: Run Markdown-defined console UI tests and show a transcript. Use when testing an interactive command-line program against expected input and output.
---

# Test UI

Test the project's interactive console program from `test/ui-test-plan.md`.
Each test case is an independent program session: it supplies a list of commands
and an expected complete console output.

## Test plan

Keep compile and run commands plus all test cases in `test/ui-test-plan.md`.
Use this format exactly:

````markdown
## Program

Compile:
```sh
<compile command>
```

Run:
```sh
<program command>
```

### Test case: <short name>

Aim: <what this case verifies>

Input:
```text
<one command per line>
```

Expected output:
```text
<complete console output>
```
````

Record the exact console output, including spaces, task-status icons, and
separator lines. Add or update cases when a user-facing behavior changes.

## Run the tests

From the repository root, run:

```sh
python3 .codex/skills/test-ui/scripts/run_ui_tests.py
```

The runner compiles once, runs test cases in their recorded order, compares
each full console output, and prints the input/output transcript. It stops at
the first failure and reports both expected and actual output. Do not continue
with later cases after a failure.
