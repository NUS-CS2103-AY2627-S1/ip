---
name: test-ui
description: Run repeatable console UI tests from `test/ui-test-plan.md`. Use when asked to test an interactive command-line program against a list of commands and expected output, record a console session, or stop and diagnose the first UI-output mismatch.
---

# Test UI

Maintain all test cases in `test/ui-test-plan.md`. Every test case must state its aim, console input, and expected standard output.

## Plan format

Set a `run-command` and, if compilation or setup is needed, a `setup-command`. Add test cases using exactly this structure:

````markdown
run-command: java -cp _temp/ui-test-classes YourHand
setup-command: javac -d _temp/ui-test-classes src/main/java/Tasks.Task.java src/main/java/ToDo.java src/main/java/Tasks.Deadline.java src/main/java/Tasks.Event.java src/main/java/YourHand.java
working-directory: .
timeout-seconds: 10
before-each-command: optional shell command run before each test

## Test case: List an empty task list

### Aim

Confirm that `list` reports an empty list before any tasks are added.

### Input

```text
list
bye
```

### Expected output

```text
... exact console output here ...
```
````

`working-directory` is relative to the plan file. The runner normalizes Windows and Unix line endings and ignores only final line-ending differences; all other output must match exactly.

To prepare a specific test, add an optional `### Setup` section before `### Input` containing one fenced shell-command block. The runner executes it after `before-each-command` and before launching the chatbot.

## Run and report

1. Update the plan before running tests. Do not keep placeholder test cases active.
2. Run from the repository root:

   ```powershell
   python .codex/skills/test-ui/scripts/run-ui-tests.py test/ui-test-plan.md
   ```

3. Read `test/ui-test-record.md`, which records each test's console input, expected output, actual output, exit code, and result.
4. Stop after the first failed test. Report that test's aim and its expected and actual output; do not continue with later cases.

## Resource

Use `scripts/run-ui-tests.py` as the standard-library-only runner. It executes each test case as a fresh program session so tests do not share in-memory state.
