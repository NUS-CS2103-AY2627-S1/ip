---
name: test-ui
description: Run Avon's console UI regression tests from test/ui-test-plan.md, compare exact output, stop at the first failure, and show the console transcript. Use when asked to test the CLI, verify command output, run UI test cases, or update and execute the project UI test plan.
---

# Test UI

Use `test/ui-test-plan.md` as the source of truth for console UI tests. Each case must contain an aim, an `Input` text block, and an `Expected output` text block.

## Workflow

1. Read `test/ui-test-plan.md` completely.
2. If the user supplies new commands or expected outputs, add or update cases in the plan before testing. Preserve the documented case format.
3. Run from the project root:

   ```bash
   python3 .agents/skills/test-ui/scripts/run_ui_tests.py
   ```

4. Do not continue after a failed case. The runner is fail-fast and prints the console transcript, expected output, actual output, and unified diff.
5. Report the number of passing cases and include the runner's console-session record in the response. Do not claim success if compilation, Java-version validation, plan parsing, or any case fails.

## Test-plan format

Use one section per test case:

````markdown
## TC-001: Short title

Aim: State the behavior being tested.

### Input

```text
command one
command two
```

### Expected output

```text
exact complete console output
```
````

Treat every non-empty line in `Input` as a command sent to one Avon session. Include `bye` when a normal shutdown is part of the expected session. Expected output is exact apart from CRLF/LF normalization and final newline differences.

## Runner behavior

- Require `javac 25` and compile every file under `src/main/java` into a temporary directory.
- Run each test case in a fresh JVM so cases do not share state.
- Merge standard error into the captured console output so unexpected errors fail comparison.
- Print each case's inputs and actual console output as the permanent test-session record.
- Exit nonzero immediately on the first mismatch or infrastructure error.

