---
name: test-ui
description: Run and document fail-fast console UI tests for this Java project using command sequences and exact expected output. Use when testing Koara's interactive terminal behavior or updating its UI test plan.
---

# Test UI

Use `test/ui-test-plan.md` as the source of truth for console UI test cases.

## Prepare the plan

When the user supplies commands and expected outputs, record them in `test/ui-test-plan.md` before running tests. Each test case must contain:

- a unique ID and descriptive name;
- an aim explaining the behavior being checked;
- an `Inputs` fenced text block containing commands in entry order; and
- an `Expected output` fenced text block containing exact program stdout.

Preserve user-provided expected output. Never replace it with output observed from the program. Normalize line endings only; spaces, blank lines, punctuation, letter case, and the final newline are significant.

Each test case represents one independent program run. Send its input commands to that process in order so state can accumulate within the case, while no state leaks between cases.

## Run the tests

From the repository root, run:

```text
python .agents/skills/test-ui/scripts/run_ui_tests.py
```

The runner verifies Java 25, compiles `src/main/java/Koara.java` in a temporary directory, and executes the cases in plan order. Do not continue manually with later cases after the runner reports a failure.

## Report the session

After testing, show the runner's console record for every executed case so the user can see both the entered commands and program output.

- If all cases pass, report the number of passed cases.
- If a case fails, terminate immediately and identify the failed case. Show its console input, actual output, and expected output exactly. State that remaining cases were not run.
- If compilation or environment validation fails, report that error and do not claim any test case passed.

