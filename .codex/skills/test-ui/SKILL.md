---
name: test-ui
description: Run this project's console UI regression tests from test/ui-test-plan.md after code changes or when command/output behavior needs verification.
---

# Test UI

Use `test/ui-test-plan.md` as the source of truth for console test cases. Each case records its aim, startup output, command inputs, and exact expected responses.

## Workflow

1. Review the current code change and update `test/ui-test-plan.md` when commands or expected output have changed, or when the change needs a new regression case.
2. Run from the repository root:

   ```bash
   python3 .codex/skills/test-ui/scripts/run_ui_tests.py
   ```

3. Show the test transcript produced by the runner and report the result. The transcript includes every command and the program output.
4. If a test fails, stop. Report the failed case and command together with the expected and actual output. Do not continue to later commands or test cases.

When the user supplies a new list of commands and expected outputs, record them as a new or updated case in `test/ui-test-plan.md` before running the test. Preserve the plan's documented Markdown structure so the runner can parse it.

The runner requires Java 25, compiles sources into a temporary directory, and does not place `.class` files in the repository.
