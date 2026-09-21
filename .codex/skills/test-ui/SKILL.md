---
name: test-ui
description: Run deterministic command-line UI regression tests for this Java chatbot. Use after application code changes, when updating or invoking test/ui-test-plan.md, or when asked to test commands and expected console output.
---

# Test UI

Run the chatbot against every case in `test/ui-test-plan.md` and compare its complete console output with the expected output.

## Workflow

1. Update `test/ui-test-plan.md` when application behavior changes or a new edge case needs coverage. Give every case an aim, input, and exact expected output.
2. Switch to Java 25 on macOS:

   ```bash
   source "$HOME/.sdkman/bin/sdkman-init.sh" && sdk use java 25.0.3.fx-zulu
   ```

3. From the repository root, run:

   ```bash
   python3 .codex/skills/test-ui/scripts/run-ui-tests.py . test/ui-test-plan.md
   ```

4. Stop at the first failed case. Report its input and exact expected and actual output.
5. When all cases pass, report the console input/output transcript printed by the runner and the total number of passing cases.

Do not weaken an expected result merely to make a failing test pass. Decide whether the application or the documented expectation is wrong, then explain the correction.
