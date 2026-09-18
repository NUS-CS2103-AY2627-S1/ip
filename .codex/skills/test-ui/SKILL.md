---
name: test-ui
description: Run the project's command-line UI test plan, compare each session with its expected output, and stop at the first failure while showing the console transcript.
---

# Test UI

Use this skill for command-line UI regression testing of this project.

## Workflow

1. Read `test/ui-test-plan.md` from the repository root. Treat its compile and run commands as project-specific configuration.
2. Compile the application using the plan's compile command. If compilation fails, show the compiler output and stop.
3. Run each test case as a separate program session using its listed input commands.
4. For each session, print the console input followed by the complete console output.
5. Compare the expected-output lines with the actual output in order. Expected lines must match complete output lines; the plan may omit startup banners and separators when they are not relevant to the test.
6. Stop immediately after the first failed test case. Report both the complete actual output and the expected output block, then return a failure status.
7. After all cases pass, report the number of passing cases and the test plan used.

## Test plan format

The plan must contain these configuration bullets:

```text
- Compile command: <command>
- Run command: <command>
```

Each test case must contain an aim, an input code block, and an expected-output code block:

```markdown
## Test case: descriptive name

### Aim
What behavior this test covers.

### Inputs
```text
command one
command two
bye
```

### Expected output
```text
line that must appear
another line that must appear later
```
```

The runner executes one session per test case, so multiple commands in one input block can verify state across a conversation. Expected output blocks are ordered exact-line assertions; blank lines are ignored so plans can focus on behavior rather than formatting-only whitespace.

## Runner

Run the bundled standard-library-only runner from the repository root:

```text
python .codex/skills/test-ui/scripts/run_ui_tests.py . test/ui-test-plan.md
```

Do not continue with later test cases after a failure. Do not replace a failed test with a weaker assertion; update the implementation or the plan only when the expected behavior has intentionally changed.
