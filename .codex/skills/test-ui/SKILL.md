---
name: test-ui
description: Run fail-fast console UI tests for this Java chatbot from test/ui-test-plan.md, comparing each scripted command session with its expected output.
---

# Console UI testing

Use this skill when testing the chatbot's terminal interaction. The test plan is the source of truth: do not invent test commands or expected output.

## Workflow

1. Read `test/ui-test-plan.md`. Confirm that every test case has an aim, an input block, and an expected-output block.
2. Run the test runner with a Python 3 interpreter:

   ```powershell
   python .codex/skills/test-ui/scripts/run_ui_tests.py test/ui-test-plan.md
   ```

   If `python` is not on PATH, use the workspace's configured Python runtime instead.
3. Present the runner's result. It records the console input and captured console output for every case that ran.
4. On the first failure, stop. Report that case's aim plus its expected and actual outputs. Do not continue with later test cases.

## Test-plan format

Keep the program settings and test cases in `test/ui-test-plan.md` using this exact structure:

````markdown
## Program setup

- Java version: 25
- Source directory: `src/main/java`
- Main class: `TungTung`

## Test case: descriptive-id

### Aim

What behaviour this case checks.

### Input

```text
todo read book
bye
```

### Expected output

```text
...entire console output, including prompts and separators...
```
````

The input block is sent as one independent program session. The expected-output block must contain the complete output for that session. The runner compares output exactly after normalising Windows and Unix line endings; spaces and blank lines remain significant.
