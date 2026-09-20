# Duchess testing guide

## Automated checks

Run the full build checks with the Gradle wrapper:

```text
./gradlew check
```

This compiles the project, runs the JUnit suite, runs Checkstyle, and generates
the JaCoCo report at `build/reports/jacoco/test/html/index.html`. The coverage
figure excludes `duchess.gui` because those JavaFX controls require a graphical
toolkit; the console and shared command-processing code remain included. The
build requires at least 98% line coverage and 90% branch coverage for that
headless core. The current report records the exact measured values. The
equivalent project-owned wrappers are:

```text
scripts/test.sh       # POSIX systems
scripts\\test.bat     # Windows
```

The project targets Java 25. The wrapper scripts use the Java installation
selected in the environment, so configure Java 25 before running them.

## Test layers

- `ParserTest` checks deterministic command parsing and task-index validation.
- `TaskTest`, `DeadlineTest`, and `EventTest` check domain state and display.
- `TaskListTest` and `TaskStatisticsTest` check mutable state and calculations.
- `StorageTest` checks round trips, malformed records, legacy records, and
  missing files using temporary paths.
- `DuchessTest` checks the response boundary shared by the CLI and GUI.
- `DuchessMainTest` and `UiTest` check the CLI entry point and console streams.
- `GuiResourceTest` checks that every referenced FXML and CSS resource is packaged.
- `test/ui-test-plan.md` checks complete user sessions and exact console output.

Run the UI plan with:

```text
python3 .codex/skills/test-ui/scripts/run_ui_tests.py
```

or use `scripts/test-ui.sh` / `scripts\\test-ui.bat`.

## Test design decisions

Tests prioritize state transitions, persistence boundaries, invalid input, and
derived statistics because failures in these areas affect multiple user-facing
commands. The UI plan remains an end-to-end safety net for exact output and
startup data loading.

## Manual JavaFX checks

JavaFX rendering, focus, resizing, and platform appearance require a graphical
desktop and are intentionally excluded from the headless JaCoCo measurement.
Follow `test/manual-test-plan.md` before a release or a major GUI change.

## Graphical regression check

On a desktop with Java 25, run `./gradlew guiTest` to exercise blank Enter
repeats, whitespace-only submissions, wrapping, pasted line breaks, search
feedback, narrow/wide layouts, and the disabled farewell state. It uses an
isolated temporary data directory. The ordinary headless `test` task skips
this display-dependent test. Inspect `build/reports/gui-preview.png` for the
rendered scene; continue to use the manual plan for platform smoke testing.
