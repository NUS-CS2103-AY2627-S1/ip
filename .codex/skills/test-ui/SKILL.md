---
name: test-ui
description: Run and maintain fail-fast console user-interface tests for this Java project. Use when asked to test the program interactively, add or update command-and-output test cases, verify console output, or show a UI test session transcript.
---

# Console UI testing

Keep the project test specification in `test/ui-test-plan.md`. Each case must have a name, aim, inputs, and expected output. Use one fresh program process for each case so cases are independent.

## Test-plan format

Set the program launch command under `## Program command`, optionally set `## Timeout seconds`, and use this case structure. Commands run from the repository root.

````markdown
## Test case: Add and list a task

### Aim

Verify that a task can be added and displayed.

### Inputs

```text
read book
list
bye
```

### Expected output

```text
...complete console output for this session...
```
````

Do not omit startup text, separators, prompts, or termination text from expected output. Update the plan whenever intended UI behaviour changes.

## Run tests

Run the deterministic runner from the repository root:

```powershell
& 'C:\Users\ic552\.cache\codex-runtimes\codex-primary-runtime\dependencies\python\python.exe' .codex/skills/test-ui/scripts/run_ui_tests.py
```

The runner prints the input and actual output for every completed test. It compares output exactly after normalizing Windows/Linux line endings and ignoring only final trailing line endings. On the first failure, stop: report that test's expected and actual outputs, return a non-zero status, and do not run later cases.

Before running, compile the Java program with Java 25 when the configured program command requires current class files. Report the full transcript and pass/fail outcome to the user.

