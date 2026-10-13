# Automated testing and remaining manual checks

Run with Java 25 from the repository root:

```text
./gradlew test jacocoTestReport checkstyleTest
python .codex/skills/test-ui/scripts/run_ui_tests.py --plan test/ui-test-plan.md --cwd .
```

On Windows, use `gradlew.bat`. Open `build/reports/jacoco/test/html/index.html`
for coverage and `build/reports/tests/test/index.html` for JUnit results.
The coverage report includes all production classes, including GUI classes;
console subprocess coverage is merged into the report. Each console test uses
its own temporary working directory and a 15-second process watchdog.

## Verified results (Windows, Java 25.0.4.1)

- JUnit: 98 tests passed (14 added).
- Checkstyle for tests: passed.
- Non-GUI line coverage: 507/512 (99.02%).
- Non-GUI branch coverage: 272/317 (85.80%), including internal assertions.
- Console acceptance: 12 cases passed; see the recorded session.
- Non-GUI percentages omit only the four JavaFX classes listed below.

## Coverage scope

The suite covers normal and invalid commands, date boundaries, duplicate identity,
completion transitions, locale-independent searches, Unicode persistence,
malformed UTF-8, corrupt records, repair/reload, save conflicts, and console
shutdown on both `bye` and end of input. Console save failures are checked for
both retry/continued interaction and end of input. Temporary files isolate all
new persistence tests from user data.

JavaFX classes `Main`, `Launcher`, `MainWindow`, and `DialogBox` require manual
rendering and interaction checks. Their lines remain visible in the full report.
`UiAssets` and `CloseGuard` remain covered by automated tests.

Five storage lines remain uncovered: an external-process lock returning null
(the overlapping-lock exception and retry are tested), the filesystem-specific
atomic-move fallback, and an unreachable missing-file catch around an in-memory
reader. Assertion failure branches for internal programming contracts are not
exhaustively exercised. These limitations are not hidden by coverage exclusions.

## Manual test matrix (not executed in this change)

- Windows, macOS, and Linux with Java 25: launch the packaged JAR, add each task
  type, mark/unmark, find, delete, exit, and reopen to verify saved state.
- 1366x768 and 1920x1080 screens; 100%, 150%, and 200% scaling: resize the window,
  enter long descriptions, scroll, and verify readable text and accessible input.
- English and Chinese OS language settings: enter Chinese text and emoji, restart,
  and check preserved descriptions, fonts, date display, and pasted whitespace.
- GUI close handling: force a save conflict using another instance and verify
  retry, cancel, and discard actions, including closing using the window button.
- Record OS, Java version, display scaling, language, steps, and observed result
  for each manual run. Do not mark an unexecuted platform check as passed.

The exact console acceptance transcript is recorded in `ui-test-plan.md` under
`Latest test session`. Existing expected output is unchanged because this change
adds tests and reporting without changing application behavior.

