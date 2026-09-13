---
name: test-ui
description: Runs Alfred console UI tests defined in test/ui-test-plan.md, compares expected output exactly, and reports the test session. Use after changing Alfred commands, task output, or console interaction.
---

# Test UI

Use this skill after every source-code update, as required by `AGENTS.md`.

## Test plan

Keep test cases in `test/ui-test-plan.md`. Each case must use this format:

````markdown
## Test name

**Aim:** What this test verifies.

### Input
```text
command one
command two
```

### Expected output
```text
exact program output
```
````

Before running tests, review the plan and update it whenever behavior changes or a new command is added.

## Coverage checklist

The complete plan must cover:

- startup, an empty list, and clean exit;
- successful `todo`, `deadline`, and `event` creation, including arbitrary date/time text;
- successful `list`, `mark`, `unmark`, and `delete` operations;
- list numbering and task status after state-changing commands;
- missing, non-numeric, zero, negative, and out-of-range task numbers for numbered commands;
- missing descriptions and missing deadline/event markers or values;
- unknown commands;
- state preservation after every rejected command category.

Prefer interleaving valid and invalid commands in the same case so the final `list` output detects unintended state changes. Do not duplicate a case when an existing case already proves the same behavior.

## Run tests

From the project root, run:

```bash
python3 .cursor/skills/test-ui/scripts/run_ui_tests.py
```

The runner compiles all files in `src/main/java`, runs every test case in plan order, and compares program output exactly with the expected output. Run the entire plan, not only newly added cases.

## Results

For each case, show its aim, console input, and console output. Stop immediately at the first failure and report both expected and actual output. Do not continue to later cases after a failure.
