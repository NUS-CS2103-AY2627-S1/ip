---
name: test-ui
description: Run console UI test cases from test/ui-test-plan.md and compare actual output with expected output, stopping at the first failure.
---

Read `test/ui-test-plan.md`. For each `## Test case`, run the commands in its fenced `Input` block, compare the complete transcript with the fenced `Expected output` block, and print the input/output record. Stop immediately on a mismatch and report actual versus expected output.

Run from the repository root after compiling with Java 25:

```powershell
javac -d build src/main/java/*.java
python .codex/skills/test-ui/scripts/run_ui_tests.py
```
