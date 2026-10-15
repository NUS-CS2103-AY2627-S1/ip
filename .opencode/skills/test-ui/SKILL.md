---
name: test-ui
description: UI test cases, invalid input, and test/ui-test-plan.md. Use ONLY when testing LenZaBot console interactions from lists of commands and expected outputs, or when updating/running the UI test plan.
---

# Test UI

Use this skill to record and run console UI tests for `LenZaBot`.

## When to use

Use this skill when the user:
- provides commands and expected outputs for UI testing
- asks to test invalid input handling
- wants to update or run `test/ui-test-plan.md`
- changes LenZaBot console behavior in a way that affects command output, parsing, or error handling

## Maintenance rule

If the code change affects any console-visible behavior, update `test/ui-test-plan.md` in the same task.

This includes changes to:
- command names
- command parsing rules
- success output
- error output
- task rendering in `list`
- startup or goodbye text when those are part of the tested flow

Do not leave the plan stale after changing behavior.

## Test plan location

Record all UI test cases in:

`test/ui-test-plan.md`

Each test case must include:
- aim
- commands
- expected output for each command

## Test case format

Use one section per test case, with a human-readable summary and one JSON block for automation.

Example:

````markdown
## TC-EXAMPLE: Empty todo

Aim: Reject a `todo` command with no description.

Commands:
```text
todo
bye
```

Expected output:
```text
Oops: the description for `todo` cannot be empty.
Bye! See ya later.
```

```json
{
  "id": "TC-EXAMPLE",
  "aim": "Reject a `todo` command with no description.",
  "commands": [
    "todo",
    "bye"
  ],
  "expectedOutputs": [
    ["Oops: the description for `todo` cannot be empty."],
    ["Bye! See ya later."]
  ]
}
```
````

Rules:
- `commands[i]` maps to `expectedOutputs[i]`
- each `expectedOutputs[i]` is a list of output lines for that command
- keep outputs exact enough to detect regressions
- prefer adding invalid-input cases first when the user is focused on error handling

## How to run

Run the helper script:

```powershell
powershell -ExecutionPolicy Bypass -File ".opencode/skills/test-ui/run-ui-tests.ps1" -PlanPath "test/ui-test-plan.md"
```

## Expected behavior of the test runner

- compiles the Java program
- runs one UI session per test case
- checks each command's actual output against the expected output for that command
- prints a readable console transcript showing inputs and outputs
- stops immediately on the first failed test case
- reports the failed case id, aim, expected output, and actual output

## Reporting back to the user

When using this skill:
- mention which test cases were added or changed in `test/ui-test-plan.md`
- show the recorded transcript from the runner
- if a test fails, report the first failure only and stop
- if program behavior changed, update the affected expected outputs before rerunning tests
