---
name: test-ui
description: Run and verify this project's console UI test plan, including fail-fast expected-output checks and a recorded input/output transcript.
---

# Console UI testing

Use this skill when asked to run, add, or update console UI tests for this project.

The source of truth for UI scenarios is [test/ui-test-plan.md](../../../test/ui-test-plan.md). Each test case must state its aim, its complete ordered inputs, and the expected console output.

## Running the plan

1. Read the full test plan and use its `Run command` to compile and start the application with Java 25.
2. Preserve any existing `data/Yawned.txt` file. Before each test case, set that file to the stated `Initial storage` value; `None` means ensure the file does not exist. Restore the original file when testing ends.
3. Run each test case as a separate program session, supplying its `Inputs` in the listed order. Normalize only line endings before comparing output; do not otherwise loosen expected-output checks.
4. Compare the complete captured console output with that case's `Expected output`.
5. On the first failing test case, stop: do not begin another case. Report the case name plus the actual and expected outputs.
6. After a successful run, show a `Console session record` with the inputs and captured output for every executed test case, so the session is reviewable.

## Maintaining the plan

Keep test cases independent: every case starts a fresh application session and ends with `bye`. When behaviour changes, update the expected output in the plan in the same change as the application code. Add a case for success behaviour and relevant invalid input where that is part of the command's contract. Every case must declare `Initial storage` so persistence does not affect another case.

Use this structure for every test case:

````markdown
## Test case: <short name>

**Aim:** <what behaviour is being checked>

**Initial storage:** None

**Inputs:**
```text
<one console command per line>
```

**Expected output:**
```text
<complete console output, including startup and exit messages>
```
````
