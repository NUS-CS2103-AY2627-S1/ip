---
name: test-ui
description: Run scripted console UI tests for this project from test/ui-test-plan.md, comparing command-session output with expected output and reporting the first failure.
---

# Test UI

Use this skill when asked to run or add console UI tests for this project.

## Test plan

Keep every UI test case in `test/ui-test-plan.md`. Each case must contain an aim,
an `Input` code block, and an `Expected output` code block. The expected output
is a complete transcript produced by the application, excluding terminal colour
codes. Add a final `bye` command to each case so the program terminates.

Use this structure:

````markdown
## Test case: Short descriptive name
Aim: What behaviour this case verifies.

Input:
```text
command 1
bye
```

Expected output:
```text
Complete program output here.
```
````

## Run tests

From the repository root, run:

```powershell
py .codex/skills/test-ui/scripts/run_ui_tests.py
```

The runner compiles all `src/main/java` source files, runs one fresh program
session per test case, strips ANSI colour codes before comparison, and prints a
record of the console input and output. It stops at the first failure and
prints both the expected and actual outputs.

The runner requires Java 25 and reports a clear error if it cannot find it.
Set `JAVA25_HOME` to the Java 25 installation directory if automatic discovery
does not find it. The runner reports compilation failures instead of continuing
with stale class files.
