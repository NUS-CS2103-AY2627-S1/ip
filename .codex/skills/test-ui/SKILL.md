---
name: test-ui
description: Run planned console UI tests for this Java project from test/ui-test-plan.md, comparing each command response and producing a fail-fast transcript.
---

# UI test runner

Use this skill when changing or verifying Quackers' command-line interface.

The test plan is the source of truth and lives at `test/ui-test-plan.md`. Maintain one section per test case with its aim, a command list, and an equally sized list of expected output blocks. Use this exact shape:

````markdown
## Test: Add a to-do

Aim: Confirm that a to-do is stored and displayed correctly.

### Commands

```text
todo borrow book
list
```

### Expected outputs

```text
     Got it. I've added this task:
       [T][ ] borrow book
     Now you have 1 tasks in the list.
```

```text
     Here are the tasks in your list:
     1.[T][ ] borrow book
```
````

Run `python .codex/skills/test-ui/scripts/run_ui_tests.py`. It compiles the Java sources with Java 25, runs each test case in a fresh application session, compares the response after every command exactly (except for Windows/Linux line endings), and prints the console input/output transcript.

Do not continue after a failed command. Report its aim, command, expected output, actual output, and transcript. A command sequence in one test case shares the same in-memory task list; separate test cases do not.
