---
name: test-ui
description: Record and run fail-fast console UI tests from test/ui-test-plan.md. Use when the user supplies console commands and expected outputs, asks to test the command-line UI, wants a reproducible UI test plan, or needs an input/output transcript and exact mismatch report.
---

# Test UI

Record console test cases in `test/ui-test-plan.md`, execute them with the bundled runner, and report the resulting console sessions.

## Workflow

1. Work from the repository root.
2. Read the user's commands and expected outputs. If either is genuinely ambiguous, ask only for the missing information.
3. Inspect the program's normal build and launch commands. For this repository, use Java 25, `mvn -q compile`, and `java -cp target/classes Larry` unless the project configuration has changed.
4. Create or update `test/ui-test-plan.md` using the format below. Preserve unrelated existing test cases.
5. Give every test case a specific name and record its aim, ordered input commands, and complete expected standard output.
6. Run:

   ```bash
   python3 .agents/skills/test-ui/scripts/run_ui_tests.py --plan test/ui-test-plan.md
   ```

7. Show the runner's console transcript to the user. If a case fails, stop immediately and report the input, actual output, and expected output. Do not run later cases.

## Test Plan Format

Use this exact heading structure:

````markdown
# UI Test Plan

## Configuration

Program command: `java -cp target/classes Larry`
Build command: `mvn -q compile`
Output starts after line: `What can I do for you?`

## Test Case: Add and list a task

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
<complete expected standard output, including prompts and spacing>
```
````

Treat each test case as one console session. Send all lines under `Inputs` to the same process in order so state created by an earlier command is available to later commands. Start a fresh process for the next test case.

The build command is optional. When present, run it once before the test cases. Commands are tokenized without a shell, so do not use pipes, redirection, command substitution, or shell operators.

## Comparison Rules

- Compare the complete standard output exactly, including spaces and blank lines.
- Normalize Windows and Unix line endings and ignore only trailing newline characters at the end of the entire output.
- If `Output starts after line` is configured, ignore earlier startup output through the first matching line before comparing.
- Treat a timeout or nonzero program exit as a failure.
- Display standard error separately when the program writes to it.
- Never rewrite expected output merely to make a failing test pass. Change it only when the user changes the specification or confirms the expected behavior was wrong.

## Runner Options

Use `--timeout SECONDS` to change the per-case timeout from 10 seconds. Use `--repo PATH` only when invoking the runner outside the repository root.
