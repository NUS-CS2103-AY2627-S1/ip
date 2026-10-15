---
name: test-ui
description: Run Minty's command-line UI test cases from test/ui-test-plan.md, compare complete console output exactly, stop at the first failure, and preserve a test-session transcript.
---

# Test Minty's UI

Record every test case in `test/ui-test-plan.md` before running it. Each case must use this structure:

````markdown
## TC1: Short name

Aim: What behavior this checks.

### Input

```text
command one
command two
bye
```

### Expected output

```text
complete expected console output
```
````

Keep commands in the order they should be sent. Include `bye` when a normal exit is expected. Expected output includes the full program output, including the greeting, dividers, responses, and goodbye message.

Run `.codex/skills/test-ui/scripts/run_ui_tests.py`. It compiles the sources with Java 25, starts a fresh Minty process for each case, compares stdout exactly, and writes `test/ui-test-session.txt`.

Stop at the first failing case. Report its actual and expected output; do not run later cases. After a successful run, show or summarize the saved console transcript so the user can inspect the session.
