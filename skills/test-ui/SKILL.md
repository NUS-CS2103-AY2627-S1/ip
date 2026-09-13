---
name: test-ui
description: Run and maintain fail-fast console UI tests for this Java project. Use when adding, changing, or verifying interactive command-line behavior, expected console output, or the UI test plan.
---

# UI Test Runner

Keep test cases in [test/ui-test-plan.md](../../../test/ui-test-plan.md). Each case needs a name, aim, input block, and expected-output block.

Run from the repository root:

```powershell
python skills/test-ui/scripts/run_ui_tests.py
```

The runner compiles all Java sources with Java 25, prints the input/output transcript for each passing case, and stops at the first mismatch. It reports that case's input, expected output, and actual output. Preserve spaces, blank lines, and punctuation in expected-output blocks; comparison is exact except for a final newline.
