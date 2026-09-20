---
name: test-ui
description: Run and maintain transcript-based command-line UI tests for this Java project. Use when testing Harold commands against expected console output or when adding UI regression cases.
---

# Test UI

Use [`test/ui-test-plan.md`](../../../test/ui-test-plan.md) as the source of truth for UI test cases. Each case must contain an aim, an ordered input list, and one expected-output block per input.

## Workflow

1. Read the complete test plan before testing.
2. When the user supplies new commands and expected outputs, add or update cases in the test plan before running them. Preserve the documented Markdown structure because the runner parses it.
3. Run `python3 .agents/skills/test-ui/scripts/run_ui_tests.py` from the project root.
4. Show the runner's console-session record to the user.
5. Stop immediately on the first failing command. Report its test case and command, followed by the actual and expected outputs. Do not run later cases after a failure.

The runner compiles the CLI and shared Java sources into a temporary directory,
excluding JavaFX-only GUI classes whose dependencies are managed by Gradle. It
starts a fresh Harold process for each test case, sends that case's commands in
order, and compares each command response exactly after normalizing line endings
and outer blank lines. It excludes the startup banner and separator lines from
comparisons.

## Test-plan format

Use this exact shape for every case:

````markdown
## TC01: Short name

**Aim:** What behavior this case verifies.

### Inputs

```text
first command
bye
```

### Expected outputs

#### Output 1

```text
response to first command
```

#### Output 2

```text
response to bye
```
````

Keep inputs one command per line. The number and order of output blocks must match the input lines. End each case with a command that terminates the program, normally `bye`.
