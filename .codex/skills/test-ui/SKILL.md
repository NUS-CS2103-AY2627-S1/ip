---
name: test-ui
description: Run and verify planned console UI tests for this project. Use after every code update, when testing the Duke command-line interface, or when adding or changing commands, responses, task formatting, or other console output.
---

# Test UI

Run the complete UI test plan after each code update. Before testing, update `test/ui-test-plan.md` when an input or expected console output has changed.

## Maintain the test plan

Record each test case in `test/ui-test-plan.md` using this structure:

```markdown
## Test Case <number>: <title>

Aim: <behavior verified by this test>

### Inputs

```text
<commands, one per line>
```

### Expected Output

```text
<complete expected console session>
```
```

Keep at least one case for each supported command and add or update a case whenever a UI behavior changes.

## Run the tests

From the project root, run the bundled test runner with the workspace Python runtime:

```powershell
& <bundled-python> .codex/skills/test-ui/scripts/run_ui_tests.py test/ui-test-plan.md
```

The runner compiles all Java files in `src/main/java`, executes each command sequence, compares the full console output with the expected output, and displays the console input/output transcript.

Stop at the first failed case. Report that case's actual and expected outputs; do not continue with later cases. Report success only if every case passes.
