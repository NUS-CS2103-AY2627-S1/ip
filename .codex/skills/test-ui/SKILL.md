---
name: test-ui
description: Run fail-fast console UI tests for this Java chatbot from test cases stored in test/ui-test-plan.md. Use when asked to test command sequences, compare console output with expected output, maintain the UI test plan, or show a UI test transcript.
---

# Test UI

Test the chatbot through its console interface and leave a readable record of
each test session.

## Maintain the test plan

Record every test case in `test/ui-test-plan.md`. When the user supplies new
commands and expected outputs, add or update the relevant cases before running
them. When application behavior changes without supplied cases, derive suitable
inputs and expected outputs from the intended requirements and changed behavior.
Do not copy actual output into the plan merely to make a failure pass. Preserve
unrelated existing cases.

Each case must use this structure:

````markdown
## Test Case: Descriptive name

### Aim

Explain the behavior being checked.

### Inputs

```text
first command
second command
bye
```

### Initial Data

```text
Optional contents to place in data/yuno.txt before Yuno starts
```

### Expected Output

```text
Complete program output for the session
```
````

Put one console command on each line in `Inputs`. Include every line printed by
the program in `Expected Output`, but do not include the input commands there.
End each ordinary session with `bye` so the program terminates normally.
Omit `Initial Data` unless the case needs a preloaded `data/yuno.txt` file.

## Run the tests

From the repository root, run:

```bash
python .codex/skills/test-ui/scripts/run_ui_tests.py
```

The runner:

- requires a Java 25 compiler and runtime;
- compiles the console application files under `src/main/java` into a temporary directory,
  excluding JavaFX-only classes that the console entry point does not use;
- starts each test case in a fresh temporary working directory so saved task
  data cannot leak between cases or affect the repository;
- sends that case's commands to standard input in their listed order;
- compares standard output with the case's expected output;
- prints the aim, console input, and console output for every attempted case;
- stops immediately at the first failure and prints expected and actual output.

Comparison treats CRLF and LF as equivalent, ignores trailing spaces on output
lines, and ignores final blank lines. All other text and line ordering must
match.

Do not continue with later cases after a failure. Report the failing case and
the runner's actual-versus-expected record to the user. If all cases pass,
report the number of passed cases and retain the complete transcript in the
response.
