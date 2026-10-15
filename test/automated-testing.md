# Automated test coverage

Run with Java 25 (`sdk use java 25.0.3.fx-zulu` on macOS if needed):

```sh
./gradlew test jacocoTestReport checkstyleTest
python3 .codex/skills/test-ui/scripts/run_ui_tests.py
```

JUnit uses temporary directories for persistence tests. Console tests replace and
restore standard streams; the locale test restores the original default locale.
Keep tests sequential because these JVM settings are shared. The symbolic-link
test is skipped with an explanation on systems that cannot create symbolic links.

The suite covers task validation and formatting, every menu alias, duplicate retention,
sorting and lookup boundaries, Unicode and locale-independent searching, chat state
transitions and retries, console workflows, persistence, recovery warnings, and
failed-write cleanup. Chat response logic is tested without launching JavaFX.

Open `build/reports/tests/test/index.html` for JUnit results and
`build/reports/jacoco/test/html/index.html` for per-class coverage. JaCoCo excludes
only `Main`, `MainWindow`, `DialogBox`, and `Launcher`, which handle JavaFX rendering
and application startup. `Doe` and `UserInterface` remain included.

The expanded suite has 60 tests. The Java 25 macOS run measured 510/521 lines (97.89%)
and 252/267 branches (94.38%) in the included classes. Coverage measures execution;
the assertions also check responses, persisted records, and recovery behavior.

Remaining uncovered lines include defensive switch defaults, delimiter guards
already rejected by description validation, unused construction of the parser
utility class, the console main entry point, and filesystem-dependent atomic-move
fallback/cleanup failures. The separate console UI runner exercises the main entry
point but does not contribute to JUnit coverage. Filesystem fallback failures are
not forced by changing production code solely for tests.

All seven exact-output console sessions are documented in [ui-test-plan.md](ui-test-plan.md).
Graphical rendering, IME input, screen scaling, and real OS/language combinations
remain in the [manual GUI plan](gui-resize-test-plan.md); those platform runs are pending.
