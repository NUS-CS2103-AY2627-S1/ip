---
name: test-ui
description: Run Duchess console UI test cases from test/ui-test-plan.md and compare each session with its expected output.
---

# Test UI

Use this skill when the user asks to test Duchess through its console interface or
provides commands with expected output for UI testing.

## Test plan format

Keep the test cases and their supporting information in
`test/ui-test-plan.md`. Each test case must include:

- an aim;
- an `Inputs` fenced block containing the commands sent to Duchess, one command
  per line; and
- an `Expected output` fenced block containing the complete expected console
  output, including the welcome message, separators, and goodbye message when
  those commands produce them.

The plan may specify the executable command in a line such as:

```text
Run command: `java -cp {classes} Duchess`
```

`{classes}` is replaced by the runner with a temporary compilation directory.
If omitted, the same command is used.

When the user supplies new commands and expected output, record them as a new
test case in the plan before running the tests.

## Running tests

Run the bundled standard-library-only runner from the repository root:

```bash
python3 .codex/skills/test-ui/scripts/run_ui_tests.py
```

The runner compiles every Java source in `src/main/java` into a temporary
directory, starts a fresh Duchess process for each test case, sends the listed
commands as standard input, and compares standard output exactly after
normalizing line endings and the final newline. Use Java 25 for compilation and
execution; on macOS, switch with `sdk use java 25.0.3.fx-zulu` first when
SDKMAN is available.

After each case, the runner prints a console record containing the input and
output. If a case fails, it immediately prints the actual and expected output,
exits with a non-zero status, and does not run later cases. Report that failure
without continuing the test session.
