---
name: test-ui
description: Run command-line UI test cases defined in this project's test/ui-test-plan.md, compare actual output with expected output, print the console session, and stop at the first failure.
---

# Test UI

Use this skill for scripted console/UI testing of the Java application.

## Test-plan contract

Read `test/ui-test-plan.md`. Each test case must use this structure:

```markdown
### Test Case: Short name
Aim: What behavior this verifies.

Command:
```text
command to run
```

Input:
```text
input lines sent to stdin
```

Expected output:
```text
exact stdout, excluding the trailing newline
```
```

The `Input` block may be empty. Commands run from the repository root. Keep expected output exact, including line breaks and punctuation. Add any setup/build commands as test cases with an empty expected output when appropriate.

## Run the session

Run the bundled runner from the repository root:

```powershell
py .codex/skills/test-ui/scripts/run-ui-tests.py test/ui-test-plan.md
```

Use Java 25 for Java compilation or execution, as required by this repository's `AGENTS.md`. If the plan specifies another plan path, pass it instead.

The runner must print each console input and output, followed by the expected output and PASS/FAIL status. Execute cases in document order. If a command exits nonzero or stdout differs from the expected output, stop immediately and report the test name, exit code, actual output, and expected output. Do not continue to later cases after a failure.
