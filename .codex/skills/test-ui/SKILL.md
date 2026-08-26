---
name: test-ui
description: Run and verify documented console UI tests for this project. Use when testing an interactive command-line program against command lists and exact expected output, recording console input/output, or stopping at the first UI test failure.
---

# Console UI testing

Read `test/ui-test-plan.md` before testing. Keep every test case in the required format:

~~~markdown
## Test N: Short name

**Aim:** State the behavior being checked.

**Run:** `command that starts the program`

**Input:**
~~~text
first command
second command
~~~

**Expected output:**
~~~text
exact program output
~~~
~~~

Compile or otherwise prepare the program before running the plan. Run each test case in a fresh process so state cannot leak between cases:

~~~powershell
pwsh -NoProfile -File .codex/skills/test-ui/scripts/run_ui_tests.ps1 test/ui-test-plan.md
~~~

The runner compares complete output after normalizing line endings and a final line ending. It prints the console input and output for every executed case. If a case fails, do not continue: report its aim, command, input, expected output, and actual output. For a passing session, report the number of cases that passed and include the printed test-session record.

When changing UI behavior, update the plan in the same change so its expected output remains an accurate specification.
