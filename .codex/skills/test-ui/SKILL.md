---
name: test-ui
description: Tests the Mochi chatbot by feeding it recorded input lists and comparing its output line by line against the expected outputs in the UI test plan. Use after every code update.
---

# test-ui

Use this skill to verify that Mochi's text output still matches the expected
outputs recorded in the UI test plan.

## Test plan

The test cases are documented in `test/ui-test-plan.md` (project root). Each
case records:

- **Aim**: what behavior is being checked.
- **Inputs**: the commands fed to Mochi, one per line.
- **Expected output**: the full output Mochi should produce for those inputs.

Machine-readable versions live in `test/cases/` as `NN-name-input.txt` and
`NN-name-expected.txt` file pairs (one pair per test case). Keep the files in
`test/cases/` in sync with `test/ui-test-plan.md` whenever the plan changes.

## How to run

Run the runner script:

```powershell
.codex/skills/test-ui/test-ui.ps1
```

The script:

1. Compiles the latest code from `src/main/java` into `out/`.
2. For each case in `test/cases/`, feeds the input lines to `java -cp out Mochi`.
3. Prints a transcript of the inputs and the actual output produced.
4. Compares the actual output to the expected output line by line.

## Failure handling

If any test case fails, stop the session immediately (do not run remaining
cases) and report:

- the name of the failed case,
- the actual output, and
- the expected output,

so the mismatch can be investigated.

## After running

- If a change intentionally altered user-visible behavior, update the affected
  case's expected output in both `test/cases/` and `test/ui-test-plan.md`, then
  re-run the suite.
- Otherwise, fix the code and re-run until all cases pass.
