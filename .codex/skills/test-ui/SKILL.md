---
name: test-ui
description: Run fail-fast console UI regression tests from test/ui-test-plan.md, or add user-supplied lists of console commands and expected outputs to that plan before running them. Use after every code update, whenever console behavior changes, or when asked to test the application's command-line interface.
---

# Test UI

Test the console application against the exact expected output recorded in
`test/ui-test-plan.md`. Keep successful test execution concise and reveal the
input/output transcript only when a failure needs diagnosis.

## Workflow

1. Read `test/ui-test-plan.md` from the repository root.
2. If the user supplied commands and expected outputs, add or update test cases in
   the plan before running the tests. Do not replace expected output with output
   copied from the current program.
3. Review whether the code change requires new or revised cases. Each case must
   have a unique `TC-...` heading, an aim, inputs, and exact expected output.
4. Ensure Java 25 is active. On macOS, run:

   ```bash
   sdk use java 25.0.3.fx-zulu
   ```

5. From the repository root, run:

   ```bash
   python3 .codex/skills/test-ui/scripts/run_ui_tests.py --quiet test/ui-test-plan.md
   ```

6. Report only the total number of passing UI test cases when the suite succeeds.
   Do not include successful cases' console inputs or outputs in commentary or the
   final response.

## Test plan contract

Keep the plan in this parseable form:

````markdown
# UI Test Plan

## Test environment

- Setup command: `command that compiles the program`
- Run command: `command that starts the program`
- Timeout seconds: `10`

## TC-001: Short name

- Aim: Behavior being checked.

### Inputs

```text
one console command per line
```

### Expected output

```text
exact complete standard output
```
````

Treat every line in `Inputs` as a console command. Run each test case in a fresh
program process so cases cannot leak state into one another. Keep prompts,
spacing, dividers, and final newlines in expected output exactly as displayed.

## Failure policy

The runner stops immediately at the first setup error, timeout, nonzero process
exit, standard-error output, or output mismatch. On a mismatch, preserve the
recorded console input, actual output, complete expected output, and unified diff
in the response. Do not run later cases after a failure.

## Resource

Use `scripts/run_ui_tests.py` for parsing, execution, exact comparison,
fail-fast behavior, and failure diagnostics. Its `--quiet` option suppresses
successful test transcripts while retaining complete failure details. It uses
only Python's standard library.
