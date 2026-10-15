---
name: test-ui
description: Run Pip console UI regression tests recorded in test/ui-test-plan.md after Java code changes that affect commands or displayed output.
---

# Test UI

Use this skill after updating Pip's command parsing, task behaviour, or console messages.

1. Update `test/ui-test-plan.md` whenever the supported command syntax or expected console output changes. Each test must have an aim, an `Inputs` block, and an `Expected output` block.
2. Run `powershell -NoProfile -ExecutionPolicy Bypass -File .codex/skills/test-ui/scripts/run-ui-tests.ps1` from the repository root. The per-process execution-policy bypass lets the project runner execute without changing the user's PowerShell settings. It compiles all source files with Java 25, runs each test session, prints a console transcript, and compares the complete program output to the plan.
3. Stop after the first failure. Report the test name along with its expected and actual output. Do not update the expected output merely to hide an unintended regression.
4. When all tests pass, state which test cases were run. If a code update cannot be covered by an existing test, add a focused case before running the suite.

Each test case is a single program session, so its input commands can build on tasks created by earlier commands in that same case. The last input should normally be `bye`.
