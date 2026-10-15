---
name: test-ui
description: Run the project's command-line UI test plan, compare each command's output with its expected output, and stop at the first failure.
---

# Test the Jelly command-line UI

Use this skill after code changes that can affect the interactive Java program.

1. Read `test/ui-test-plan.md`. Each test case must contain an aim, a command/input block, and an expected output block.
2. Run the supplied test runner from the repository root. It compiles the Java sources, launches the program once per case, sends the listed input, and compares normalized output exactly (line endings and trailing whitespace are ignored; content and line order are not).
3. Do not alter the expected output to make a failing test pass. If a case fails, stop immediately and report the case, complete console input, actual output, and expected output.
4. If all cases pass, show a session record containing every console input and output, followed by a concise pass summary.

The canonical runner is `.codex/skills/test-ui/scripts/run_ui_tests.sh`; invoke it with:

```bash
bash .codex/skills/test-ui/scripts/run_ui_tests.sh
```

Keep the plan focused on observable user behavior. Add or update a case whenever a code change changes the UI contract.
