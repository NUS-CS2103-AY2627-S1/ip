---
name: test-ui
description: Run planned command-line UI tests from test/ui-test-plan.md, checking exact console output and recording an input/output transcript. Use when testing a command-line application's user interface.
---

# Test UI

Use this skill to run the project's command-line UI tests. The test plan is the source of truth: update `test/ui-test-plan.md` before running tests so every test case has an aim, a command, console input, and expected console output.

## Test-plan format

Keep the test cases under `## Test cases`. Each test is a `###` heading with these four fields, each followed by a fenced block. The command is run from the repository root; its standard input is the `Input` block.

````markdown
### Test: Short descriptive name

**Aim:** What behaviour this test demonstrates.

**Command:**
```sh
COMMAND TO RUN THE PROGRAM
```

**Input:**
```text
LINES TYPED INTO THE PROGRAM
```

**Expected output:**
```text
THE COMPLETE EXPECTED STANDARD OUTPUT
```
````

Use exact output, including blank lines, whitespace, and prompts. Keep setup or compilation in the test command when it is needed to run the program. Put shared assumptions and how to run the plan in the plan's `## Notes` section.

## Run tests

From the repository root, first select this project's required Java version, then run:

```bash
sdk use java 25.0.3.fx-zulu
python3 .codex/skills/test-ui/scripts/run_ui_tests.py
```

The runner checks each case in order and writes `test/ui-test-session.md`, which records the command, console input, expected output, and actual console output for all attempted tests. It prints the same record location and summary to the console.

Stop immediately if a test fails. Report its name and the actual versus expected output from the runner's diff; do not run later cases. A test also fails if its command has a non-zero exit status. Do not edit application code merely to make an existing test pass unless the user asks for a fix.
