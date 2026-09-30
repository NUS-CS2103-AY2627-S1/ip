---
name: test-ui
description: Run and verify planned console UI test cases for this project. Use after changes to command-line behaviour, prompts, task output, or other user-visible program output.
---

# Test UI

Use the test cases in `test/ui-test-plan.md` to validate the console interface.
Each test case must state its aim, input commands, and the complete expected program output.

## Run the tests

1. Update `test/ui-test-plan.md` when a change introduces or alters observable
   behaviour. Keep existing cases that still apply.
2. Run the suite from the repository root. Ensure Java 25 is active first, as
   required by this project.

   ```bash
   python3 .codex/skills/test-ui/scripts/run-ui-tests.py
   ```

3. The runner compiles the Java sources into a temporary directory, runs every
   case in an isolated temporary working directory, and prints the console
   input and output for each case.
4. It stops at the first failed case and shows the expected and actual outputs.
   Fix the issue before continuing with later tests.

## Test-plan format

Use this exact structure for every test case:

````markdown
## Test Case: Short name

**Aim:** What user-visible behaviour this test covers.

### Input
```text
command one
bye
```

### Expected Output
```text
complete console output here
```
````

The expected-output block must include all prompts, dividers, and spacing that
the user can see. Do not include the input commands themselves; the runner
records those separately.

For a case that writes a file, append an optional block immediately after the
expected output. It checks `data/results.txt` relative to the program's working
directory:

````markdown
### Expected Saved Tasks
```text
T | 0 | read book
```
````

To test loading, put an optional initial-file block before the input. The
runner seeds `data/results.txt` with its contents before starting the program:

````markdown
### Initial Saved Tasks
```text
T | 1 | read book
```
````
