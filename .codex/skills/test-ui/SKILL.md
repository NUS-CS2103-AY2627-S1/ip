---
name: test-ui
description: Run scripted text-interface tests for the Sheppy Java chatbot and report the console session.
---

# Test UI

Use this skill after changing Sheppy's command-line behavior.

1. Read the cases in `test/ui-test-cases.json` and the rationale in
   `test/ui-test-plan.md`.
2. Compile all Java files under `src/main/java` with Java 25.
3. Run each case using its listed input.
4. Check that every expected output line appears in the actual output.
5. Stop at the first failure and report the input, expected lines, and actual output.
6. Report the complete console input/output session when all cases pass.

Run the bundled script from the repository root:

```bash
python3 .codex/skills/test-ui/scripts/run_ui_tests.py test/ui-test-cases.json
```

The test plan and case file are the source of truth. Update them when the UI
requirements change.
