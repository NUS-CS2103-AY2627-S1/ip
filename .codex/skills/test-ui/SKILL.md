---
name: test-ui
description: Run command-line UI test cases defined in test/ui-test-plan.md, compare actual and expected console output, and stop at the first failure.
---

# Test UI

Use this skill when the user asks to execute or verify the project's interactive command-line UI using the test plan in `test/ui-test-plan.md`.

## Workflow

1. Read `test/ui-test-plan.md`. Each test case must state its aim, command or program invocation, inputs, and expected output.
2. If the plan is missing or has no test cases, report that it cannot be run and explain what the plan must contain.
3. Run the cases in listed order using `scripts/run_ui_tests.py`. The runner accepts a plan path and optionally a working directory; commands are intentionally run by the shell so they can include normal build/run syntax.
4. Before running Java commands, ensure Java 25 is active. On macOS, use `sdk use java 25.0.3.fx-zulu` in the execution environment when needed.
5. Compare each case's complete captured stdout and stderr with its expected output. Treat differences in whitespace, line endings, or missing/extra text as failures unless the plan explicitly defines a different comparison mode.
6. Print a console-session record containing each command, its input, actual output, and pass/fail result. If a case fails, stop immediately and report both the actual and expected output; do not run later cases.

## Test-plan format

Use this structure in `test/ui-test-plan.md`:

```markdown
# UI Test Plan

## Test case: <short name>

**Aim:** <what behavior is verified>
**Command:** `<command to run>`
**Inputs:**
```text
<stdin, exactly as supplied; omit the block or write “none” when there is no input>
```
**Expected output:**
```text
<complete expected combined console output>
```
```

Keep expected output deterministic. If prompts and responses are both printed, include both. Do not silently repair a failing expected output: report the mismatch so the student can decide whether the program or plan is wrong.
