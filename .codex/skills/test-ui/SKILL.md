---
name: test-ui
description: Run exact console UI and save/load regression tests for this Alzara Java project. Use when code changes can affect commands, console messages, task display, parsing, program flow, or the data/alzara.txt save file, and when adding or updating test cases in test/ui-test-plan.md.
---

# Test UI

Maintain [`test/ui-test-plan.md`](../../../test/ui-test-plan.md) before testing. Add or revise a test case whenever a code change affects observable console behaviour or the save file format.

Each test case is a `## Test case:` heading, an `**Aim:**` line, and one of the two input/output shapes below. Both shapes may add an optional `### Save file before` and/or `### Save file after` block.

## Single-session test case

Use this for anything that fits in one program run.

````markdown
## Test case: descriptive name

**Aim:** What behaviour this test proves.

### Inputs
```text
commands sent to the program
```

### Expected output
```text
complete expected console output
```
````

## Multi-session test case

Use this to prove behaviour that only shows up across a restart, e.g. that tasks saved in one run are loaded correctly in the next. Each session is a separate `java` process, but all sessions in one test case share the same isolated `data/` folder, so it behaves exactly like a user closing and reopening the program.

````markdown
## Test case: descriptive name

**Aim:** What behaviour this test proves.

### Session 1 inputs
```text
commands for the first run
```

### Session 1 expected output
```text
complete expected console output for the first run
```

### Session 2 inputs
```text
commands for the second run
```

### Session 2 expected output
```text
complete expected console output for the second run
```
````

Add further `### Session N inputs` / `### Session N expected output` pairs for more restarts.

## Save file fixtures and assertions (optional, either shape)

- `### Save file before` seeds `data/alzara.txt` with exact file content before the first session starts. Use this to test loading existing or corrupted save files without needing a prior session to create them.
- `### Save file after` asserts the exact content of `data/alzara.txt` once every session has finished. Use this to prove a command wrote (or correctly avoided writing) the expected save format.

````markdown
### Save file before
```text
T | N | read book
```

### Save file after
```text
T | Y | read book
```
````

## Running the plan

Run with Java 25:

```powershell
& .\.codex\skills\test-ui\scripts\run-ui-tests.ps1
```

The runner compiles the program once, then for each test case creates a fresh, isolated temporary working directory — the real project's `data/alzara.txt` is never read or written by the tests. It runs one `java` process per session (fresh per session, sharing that test case's working directory), compares console output exactly apart from the final newline and one optional leading space on each line, and compares the save file exactly (apart from the final newline) when a `Save file after` block is present. It prints each session's input and output, and stops at the first failure. Do not continue testing after a failure; report its expected and actual output.
