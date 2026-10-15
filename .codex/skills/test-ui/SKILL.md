---
name: test-ui
description: Run Trackie's text-based UI regression tests after Java behavior changes, update the UI test plan when behavior changes, and report complete input/output sessions or the first mismatch.
---

# Test UI

Use `test/ui-test-plan.md` as the source of truth for Trackie's text UI tests.

## Workflow

1. Review `test/ui-test-plan.md` and update its cases and referenced input/expected files when user-visible behavior changes.
2. From the repository root, run:

   ```bash
   python3 .codex/skills/test-ui/scripts/run-ui-tests.py .
   ```

3. Stop at the first failure. Report the case name and its actual and expected output.
4. When all cases pass, report the complete input/output session printed by the runner.

The runner compiles all files under `src/main/java` with Java 25 into a temporary directory, so no `.class` files enter the repository.
