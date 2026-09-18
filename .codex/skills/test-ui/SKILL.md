---
name: test-ui
description: Run project UI tests for this Java chatbot from test/ui-test-plan.md, comparing command lists against expected console output and stopping immediately on the first mismatch.
---

# test-ui

Use this skill when asked to run, add, review, or update UI tests for this project.

## Test Plan

Keep UI test cases in `test/ui-test-plan.md`.

Each test case must include:
- aim of the test
- console input commands
- expected program output

Each test case may also include:
- initial contents of a file before the program starts
- expected contents of a file after the program exits

Use this format:

````markdown
## Test Case: Short Name

Aim: What this test verifies.

Inputs:
```text
command one
command two
bye
```

Expected output:
```text
full expected stdout
```

Initial file `path/to/file.txt`:
```text
full initial file contents
```

Expected file `path/to/file.txt`:
```text
full expected file contents
```
````

## Running Tests

Run:

```bash
python3 .codex/skills/test-ui/scripts/run_ui_tests.py --plan test/ui-test-plan.md
```

The runner:
- compiles all `src/main/java/**/*.java` files into `/tmp/cs2103-ip-ui-tests`
- runs `java -cp /tmp/cs2103-ip-ui-tests computah.Computah` once per test case
- optionally creates files recorded as `Initial file `path/to/file.txt`:`
- sends the listed inputs through standard input
- compares actual output against expected output after normalizing line endings and trimming trailing whitespace on each line
- optionally compares files recorded as `Expected file `path/to/file.txt`:`
- prints a console input/output record for each completed test
- stops immediately on the first failed test and prints actual versus expected output

If the user provides new commands and expected outputs, update `test/ui-test-plan.md` first, then run the script.
