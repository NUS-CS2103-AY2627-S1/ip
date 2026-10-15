---
name: test-ui
description: Run project-specific command-line UI test cases from test/ui-test-plan.md, compare exact console output, and stop with a transcript at the first failure.
---

# Test UI

Use this skill when the user asks to test the interactive console UI of this Java project.

## Test workflow

1. Read `test/ui-test-plan.md` before running anything. Each test case must contain an aim, an `Inputs` fenced block containing one command per line, and an `Expected output` fenced block containing the complete expected stdout for that session.
2. Run the bundled `scripts/run_ui_tests.py` from the repository root. It compiles the configured Java application with Java 25, launches one fresh process per test case, feeds the listed commands, and compares normalized stdout exactly.
3. Preserve the runner's console transcript in the response. The transcript must show each test case's input and output, in order.
4. If a test fails, do not run later cases. Report the first failing case plus both the actual and expected outputs, then stop.

Use this command from the project root:

```text
python .codex/skills/test-ui/scripts/run_ui_tests.py
```

If the system `python` command is unavailable, use the bundled workspace Python runtime. Do not modify `src/main` or `docs/README.md` merely to make a UI test pass. Update the test plan when the intended UI behavior changes, and explain any expected-output change.
