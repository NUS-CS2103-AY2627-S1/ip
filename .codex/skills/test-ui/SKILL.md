---
name: test-ui
description: Run console UI tests from lists of commands and expected outputs for this Java chatbot project. Use when asked to create, update, run, or report UI tests; verify command-line chatbot behavior; compare console output with expected output; or maintain test/ui-test-plan.md.
---

# Test UI

Run repeatable console UI tests for the Java chatbot.

## Workflow

1. Treat this repository as the target project unless the user identifies another repository.
2. Record every test case in `test/ui-test-plan.md`.
3. For each supplied test case, include:
   - Aim: what behavior the case verifies.
   - Input: the exact console commands to send to the program.
   - Expected: the exact console output expected from the program.
   - Data, if needed: initial contents of `data/bobby.txt` before the run.
   - Saved, if needed: expected contents of `data/bobby.txt` after the run.
4. Use the bundled runner from the repository root:

   ```bash
   python3 .codex/skills/test-ui/scripts/run-ui-tests.py
   ```

   Pass `--plan <path>` only if the test plan is not `test/ui-test-plan.md`.

5. If any test case fails, stop the test session immediately and report:
   - the failed test case name
   - the console input
   - the expected output
   - the actual output
6. After testing, show the console input/output transcript for each completed test case.

## Test Plan Format

Use this Markdown format in `test/ui-test-plan.md`:

````markdown
## Test Case: Add todo and list

Aim: Verify that a todo task is stored and shown in the list.

```input
todo borrow book
list
bye
```

```expected
____________________________________________________________
...
```
````

Keep expected output exact. The runner normalizes line endings but does not ignore missing, extra, or reordered output lines.
Use optional `data` and `saved` fenced blocks when testing persistence:

````markdown
```data
T | 1 | read book
```

```saved
T | 1 | read book
D | 0 | return book | Sunday
```
````

## Resource

`scripts/run-ui-tests.py` is a standard-library-only runner. It compiles all Java files in `src/main/java` into `/tmp/ip-ui-test-classes`, runs `bobby.Bobby`, compares stdout with expected output, checks saved data when requested, restores any pre-existing `data/bobby.txt`, and stops on the first failure.
