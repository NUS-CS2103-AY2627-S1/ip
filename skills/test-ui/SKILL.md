---
name: test-ui
description: Run scripted command-line UI test cases for this Java project, compare actual output with expected output, and preserve a console transcript. Use when asked to test Murphy interactively or execute a list of commands with expected outputs.
---

# Test UI

Run end-to-end, console-level test cases against the project application. The test cases are supplied as lists of commands and expected outputs, and must be recorded in `test/ui-test-plan.md` before execution.

## Test-plan format

For every test case, record all three required details:

```markdown
## Test Case: <short name>

**Aim:** <what behavior this verifies>

**Inputs:**

```text
<one command/input per line>
```

**Expected output:**

```text
<the exact complete output expected from the program>
```
```

Include the program command, working directory, Java version, and any environment assumptions in the plan. Preserve output exactly, including capitalization, punctuation, whitespace, and line breaks. If the caller supplies test cases in another list format, translate them into this format before testing.

## Execute the session

1. Read `test/ui-test-plan.md` and use the cases in file order.
2. Ensure Java 25 is active before building or running Java code. On macOS, use `sdk use java 25.0.3.fx-zulu` when needed.
3. Start a fresh program process for each test case unless the plan explicitly says that cases share a process.
4. Send the listed inputs exactly as written, capture stdout and stderr, and compare the captured output with the expected output. Do not silently normalize whitespace or omit prompts, banners, errors, or exit text.
5. Record the session in `_temp/ui-test-session.log`, showing each test case, the console input, the actual console output, and PASS/FAIL.
6. Stop immediately at the first failure. Do not run later test cases. Report the failing case and show both the complete expected output and the complete actual output.
7. If all cases pass, report the number of cases and the absolute path to the console transcript.

The transcript should use this structure:

```text
=== Test Case: <name> ===
--- INPUT ---
<console input>
--- OUTPUT ---
<actual stdout/stderr>
--- RESULT: PASS ---
```

For failures, use `RESULT: FAIL` and include an explicit `EXPECTED` section as well as `OUTPUT`.

## Safety and reproducibility

Run only the commands specified by the user or recorded in the test plan. Do not continue after a failed comparison, and do not report a pass based on a partial or visually similar output. Keep generated transcripts under `_temp/`, which should not be committed.
