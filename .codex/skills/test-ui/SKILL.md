---
name: test-ui
description: Run and verify command-line UI test cases defined in this project’s test/ui-test-plan.md. Use when adding, updating, or executing console interaction tests that need exact expected-output comparison and a saved input/output session record.
---

# Console UI testing

Keep every UI test case in `test/ui-test-plan.md`. Use this format exactly:

````markdown
## TC01: Short name

**Aim:** What behaviour this case verifies.

**Command:**
```powershell
java -cp out\production\ip Arrodes
```

**Input:**
```text
bye
```

**Expected output:**
```text
... exact program output ...
```
````

Give each case a distinct `##` heading, a one-line aim, one command, input (use an empty fenced block when there is none), and expected output. Keep the command self-contained; it is run from the project root. Record only stable console output in the expected-output block.

Before executing tests, compile or otherwise prepare the program if its command requires it. Use Java 25 for Java build and run commands.

Run the complete plan with:

```powershell
& 'C:\Users\foong\.cache\codex-runtimes\codex-primary-runtime\dependencies\python\python.exe' .codex\skills\test-ui\scripts\run_ui_tests.py
```

The runner compares output exactly apart from Windows versus Unix line endings, prints each command, supplied input, expected output, and actual output, and writes the same transcript to `test/ui-test-session.md`. It stops immediately at the first failed test and reports both the expected and actual output. Do not continue with later cases after a failure.