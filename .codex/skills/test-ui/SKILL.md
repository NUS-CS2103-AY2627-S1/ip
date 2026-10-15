---
name: test-ui
description: Run command-line UI test cases for this Java project, compare actual output with expected output, and show the complete console transcript.
---

# Test UI

Use `test/ui-test-plan.md` as the default source of UI test cases. The main
regression case is stored in `text-ui-test/input.txt` and
`text-ui-test/EXPECTED.TXT`.

## Run the regression test

Run the project test script:

```powershell
.codex/skills/test-ui/scripts/run-ui-test.ps1
```

The helper compiles the project with Java 25, redirects `input.txt` into
`SlotBot`, writes the output to `ACTUAL.TXT`, compares it with `EXPECTED.TXT`,
and prints the complete input/output transcript.

If the output differs, stop and report the actual and expected files. When a
behavior change is intentional, update `EXPECTED.TXT` only after reviewing
the difference. Update the test plan and input/expected files whenever
user-visible behavior changes.
