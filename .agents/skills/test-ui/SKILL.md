---
name: test-ui
description: Run and maintain project-specific end-to-end console UI tests for Koji's Pawn after Java behavior changes or when asked to validate command and output flows.
---

# Console UI testing

Use `test/ui-test-plan.md` as the source of truth for test cases. Each case must state its aim, command sequence, and exact expected output.

After a code update that can affect console behavior:

1. Read the test plan and add or update cases for the changed behavior.
2. From the repository root, run:

   ```text
   python .agents/skills/test-ui/scripts/run_ui_tests.py
   ```

   If `python` is not on `PATH` in Codex desktop, load the bundled workspace dependencies and invoke the reported Python executable with the same script path.

3. Report the recorded command input and actual console output for every executed case.
4. Stop at the first failure. Report the case aim, commands, expected output, and actual output before changing code or running further cases.

The runner compiles every Java source file with Java 25 into the ignored `out/test-ui` directory. Do not stage or commit generated `.class` files.

Keep state-dependent commands from one test case in one command sequence so they run in the same program process. Use separate cases when state should start empty.
