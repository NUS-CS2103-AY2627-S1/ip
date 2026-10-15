---
name: test-ui
description: "Run Clover console UI cases recorded in test/ui-test-plan.md and compare their complete output exactly. Use after command, output, or persistence changes; do not use for JUnit-only checks."
---

# Test Ui

Run the project console UI checks after reviewing `test/ui-test-plan.md`.

1. Confirm Java 25 is active. On macOS, use `sdk use java 25.0.3.fx-zulu` when needed.
2. Ensure each automated case in `test/ui-test-plan.md` has an **Aim**, **Input**, and complete **Expected output** block. Mark graphical checks with `**Type:** Manual`; the runner skips those cases.
3. Run `python3 .codex/skills/test-ui/scripts/run_ui_tests.py`.
4. Report the console input/output record emitted by the runner. If a case fails, report its expected and actual output and do not continue to later cases.
