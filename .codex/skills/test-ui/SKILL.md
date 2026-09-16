---
name: test-ui
description: Run and verify planned terminal UI tests for this Java project. Use when asked to test Bambolino's console interface, run UI regression tests, compare commands with expected output, or show a console test-session record.
---

# UI Test Runner

Run the test plan at `test/ui-test-plan.md` with:

```bash
python3 .codex/skills/test-ui/scripts/run_ui_tests.py
```

Each test case must use this exact structure. `Expected output` starts after Bambolino's startup greeting; the full startup greeting is still included in the generated transcript.

````markdown
## Test: short name

**Aim:** What behaviour this test verifies.

**Inputs:**
```text
command one
bye
```

**Expected output:**
```text
output after command one
Bye. Hope to see you again soon!
```
````

Add tests in the order they should run. The runner compiles all files in `src/main/java` using Java 25, runs each test in a fresh application process, and compares its output exactly. It ends immediately at the first failing test and reports both expected and actual output.

After every run, inspect `test/ui-test-session.md` for the console input and output of every test that ran. Do not edit that generated record manually.
