---
name: test-gui
description: Run the JavaFX GUI regression tests and review documented GUI behavior after changes to the graphical interface.
---

# Test GUI

Use this skill after JavaFX production-code changes or changes to GUI-facing
resources. The project uses TestFX with JUnit 5 for deterministic interaction
tests and keeps broader acceptance checks in the GUI test plan.

## Workflow

1. Read `test/gui-test-plan.md` and identify the GUI behaviors affected by the
   change.
2. Run the deterministic GUI regression suite with Java 25:

   ```bash
   ./gradlew guiTest
   ```

3. Record the test class or method, simulated input, and observed assertions
   for each automated case.
4. Perform any additional acceptance or visual checks listed in the plan when
   a desktop automation capability is available. If it is unavailable, report
   those checks as not run rather than claiming that they passed.
5. Stop at the first automated or acceptance-test failure, reporting the
   failing test and its complete output.
6. Run the existing `test-ui` skill as well when the change can affect shared
   chatbot behavior or CLI output.

## Scope

GUI tests should verify observable behavior: window startup, dialogue content,
speaker alignment, labels, user input handling, and exit behavior. Do not
duplicate the complete command or date-time matrix already covered by the
backend JUnit tests and `test/ui-test-plan.md`.

The `guiTest` Gradle task is the repeatable automated baseline. Broader
acceptance checks may cover multi-command conversations, goodbye timing,
scrolling, resizing, and persistence without making the normal Gradle test
task depend on a desktop display.

Use stable FXML IDs and CSS classes for TestFX lookups. Avoid coordinate-based
assertions and exact pixel comparisons unless a visual-layout regression is
the explicit purpose of the test.

## Test-plan maintenance

Update `test/gui-test-plan.md` when a GUI behavior, control, visible message,
or interaction flow changes. Keep each case's aim, simulated actions, and
expected observable result explicit.
