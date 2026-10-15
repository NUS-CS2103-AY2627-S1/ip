---
name: test-ui
description: Run and maintain this Java chatbot's text UI tests. Use after any application code update or when checking command inputs and expected console outputs.
---

# Test the text UI

Use `test/ui-test-plan.md` as the source of truth for UI test cases.

## Workflow

1. Read the code change and `test/ui-test-plan.md`.
2. Update the plan when commands, messages, formatting, or other observable behavior changed. Every test case must have an aim, an `Input` block, and an `Expected output` block.
3. From the repository root, run:

   ```bash
   python3 -B .agents/skills/test-ui/scripts/run_ui_tests.py
   ```

4. Preserve the runner's console transcript in the testing record shown to the user. It lists the input and actual output for every executed test case.
5. If a test fails, stop immediately. Report the failed case and show both its expected and actual output. Do not run later cases.

The runner compares output line-for-line after normalizing Windows and Unix line endings. Input is supplied on standard input and is shown separately from program output in the test-session transcript.

## Test plan format

Write each case in this form:

````markdown
## TC-N: Short name

**Aim:** What behavior this case verifies.

### Input

```text
first command
second command
```

### Expected output

```text
Exact program output
```
````

Keep commands in execution order. Expected output contains only text written by the program; do not duplicate the input commands there.

When a case needs tasks to exist before Zeus starts, add an optional `### Initial data file` fenced text block containing the exact starting contents of `data/zeus.txt`. The runner creates that file before launching Zeus.

When a test also verifies persistence, add an optional `### Expected data file` fenced text block containing the exact expected contents of `data/zeus.txt`. The runner executes every case in an isolated temporary working directory, so tests never overwrite the user's real data file.
