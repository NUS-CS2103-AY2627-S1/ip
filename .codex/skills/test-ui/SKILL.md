---
name: test-ui
description: Compile and run Jaku's exact-output command-line UI regression tests. Use after changing Java code or user-visible chatbot behavior, and when adding or reviewing cases in test/ui-test-plan.md.
---

# Test UI

Use the checked-in test plan and deterministic runner to verify Jaku's complete console output.

1. Work from the repository root and review `test/ui-test-plan.md`.
2. If behavior changed or the user supplied commands and expected output, add or update a test case before running the suite. Each case must include an aim, an input block, and an expected-output block.
3. Run `python3 .codex/skills/test-ui/scripts/run_ui_tests.py`.
4. Show the runner's input/output transcript and final result to the user.

The runner compiles all Java sources with Java 25 and warnings treated as errors. It starts a fresh Jaku process for every case and compares output exactly. Stop at the first failure, report both expected and actual output, and fix the application or test expectation before continuing. Do not weaken an expectation merely to hide a regression.
