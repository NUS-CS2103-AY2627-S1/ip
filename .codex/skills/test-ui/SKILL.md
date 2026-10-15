---
name: test-ui
description: Run Nova console UI regression tests defined in test/ui-test-plan.md. Use when asked to test the command-line UI, compare commands with expected console output, execute the UI test plan, or show a test-session transcript.
---

# Test UI

1. Read `test/ui-test-plan.md`; do not alter expectations while testing.
2. Ensure every test case has an aim, input, and complete expected output.
3. From the repository root, run `python .codex/skills/test-ui/scripts/run_ui_tests.py`.
4. Show the runner's console transcript, including input and actual output.
5. Stop on the first failure and report the complete expected and actual outputs. Do not run later cases.

The runner requires Java 25, compiles Java files recursively from `src/main/java` into a temporary directory, and starts a fresh Nova process for each test case.

## Plan format

Define cases as second-level headings followed by `Aim:`, `### Input`, and `### Expected output`. Put input and output in `text` fenced blocks. Comparison is exact except for line-ending normalization and final newline characters. Include `bye` in the input when Nova should exit normally.
