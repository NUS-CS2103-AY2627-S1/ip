---
name: test-ui
description: Run scripted command-line UI tests from test/ui-test-plan.md, compare each console output exactly, and record the session. Use when testing an interactive program with command inputs and expected output.
---

# Test UI

Use this skill to test a command-line program using cases defined in
`test/ui-test-plan.md`. Each case must state an aim, console input, and the
complete expected console output.

## Plan format

Read `test/ui-test-plan.md` before testing. It contains the command that starts
the program and Markdown test cases in this form:

````markdown
## Test command

```sh
cd src/main/java && javac *.java && java Kdb
```

## Test cases

### Lists stored tasks

**Aim:** Confirm that the list command displays every task and its status.

**Input:**

```text
list
bye
```

**Expected output:**

```text
...complete console output...
```
````

Use literal expected output, including spaces and blank lines. The runner
normalizes only line endings, so UI formatting regressions are detected.

## Run and report

From the project root, run:

```bash
python3 .codex/skills/test-ui/scripts/run_ui_tests.py
```

The runner writes `test/ui-test-session.md`, containing each case's aim,
console input, console output, and result. It stops immediately at the first
failed case; that record includes the expected and actual output. Report the
result and link to the session record. Do not continue testing after a failure.
