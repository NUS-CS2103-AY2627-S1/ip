# Automated testing

Use Java 25. The suite targets nearly all core production code and full coverage of the GUI,
superseding the earlier approximately 50% testing target for this branch. No application behavior
is changed by this testing work.

## Commands

```bash
./gradlew check
./gradlew testChineseLocale
```

`check` runs JUnit, both Checkstyle tasks, generates the coverage report, and enforces
at least 99% line coverage and 97% branch coverage for non-GUI production classes.
Each of `SevenSixGui`, `DialogBox`, and `Launcher` must have 100% line and branch
coverage; classes with no branches have no branch obligations.
`testChineseLocale` runs the same suite in a fresh JVM with `zh-CN` defaults.
For just JUnit and the report, run `./gradlew test jacocoTestReport`.
Use `gradlew.bat` instead of `./gradlew` on Windows.

Generated reports (do not commit these):

- `build/reports/tests/test/index.html`: default-locale JUnit results.
- `build/reports/tests/testChineseLocale/index.html`: Chinese-locale results.
- `build/reports/jacoco/test/html/index.html`: line and branch coverage.
- `build/reports/jacoco/test/jacocoTestReport.xml`: machine-readable coverage.

JaCoCo 0.8.14 is pinned because its
[release notes](https://www.jacoco.org/jacoco/trunk/doc/changes.html) specify Java 25 support.
All production classes are included in the report. `verifyCoreCoverage` retains
the core-only thresholds, and `jacocoTestCoverageVerification` enforces the
per-class GUI thresholds. The console entry point remains in scope.

## JavaFX test environment

GUI tests create real windows and require a graphical session. On Linux without
an active display, run `xvfb-run --auto-servernum ./gradlew check` (install Xvfb
first if needed). CI uses this command on Linux and the normal Gradle command on
macOS and Windows. The locale task needs the same display setup.

The toolkit is shared within each test worker. UI actions run on the JavaFX thread
with bounded waits. Test storage is temporary, and every test window is closed.
`LauncherTest` uses a separate JVM to exercise the production launcher and the
GUI's `bye` action without shutting down the other tests. A bounded startup
observer finds the window; assertions verify its greeting and farewell before
exit. Child coverage is included in the default-locale report.

Automated GUI assertions cover message text, styling, avatars, resize bindings,
startup warnings, both submission handlers, blank input, errors, persistence,
and scrolling. They do not replace the manual visual and physical-input checks.

## VS Code coverage

Use **Java: Configure Java Runtime** to confirm that the project uses Java 25
(the Zulu JavaFX JDK for this project's setup). The workspace test configuration
in `.vscode/settings.json` enables assertions and native access for
`javafx.graphics`. These options belong in `java.test.config`, because the Java
test extension launches JUnit directly rather than running Gradle's `test` task.

After importing the Gradle project, open the Testing view and choose **Run Tests
with Coverage** for the project. Run the whole suite to measure complete coverage.
`LauncherTest` falls back to the runner's classpath when Gradle's custom property
is absent and passes native access to the child JVM. Keep `coverage.appendResult`
enabled so parent and child JVM coverage records are retained. Appended coverage
can include previous runs; use `./gradlew clean check` for a fresh enforced report.

## Coverage and test design

The tests exercise these behaviors through public APIs, without reflecting into
private methods or changing production code for coverage:

- Every task constructor, completion transition, type marker, and display format.
- List ordering, removal, replacement, collection ownership, Unicode searches,
  duplicate identity, and changes to each event endpoint field.
- Every supported date/time format, leap-day validation, midnight/noon boundaries,
  malformed commands, and repeated or missing parameters.
- Exact storage records, invalid field counts, escaping, corrupt input, file
  permissions, atomic replacement, empty files, and blocked writes.
- Successful command workflows, persistence across restart, failed-command rollback,
  copied undo histories, and independent snapshots of all task types.
- Exact console separators, greeting, startup warnings, EOF, CRLF input, and stopping
  at `bye` without processing later lines.
- Real child-JVM startup with missing/blank configuration, default paths, Unicode
  working directories, configured filenames containing spaces, and an obstructed
  data directory. Unicode configuration values are also tested inside Java.
- English, Chinese, and Turkish locale behavior for dates and case-insensitive search.
- Assertion contracts for invalid internal model, parser, and list operations.

Filesystem fixtures use JUnit temporary directories. Real console processes have
isolated working directories and timeouts. Their JaCoCo execution data is appended
to the parent test report using an absolute destination. Tests restore process-wide
streams, properties, and locale settings after use. POSIX permission and symlink
tests use assumptions on unsupported platforms; inspect skipped results on each OS.

### Windows launcher limitation

The [OpenJDK 25 Windows launcher](https://github.com/openjdk/jdk/blob/jdk-25-ga/src/java.base/share/native/launcher/main.c#L74)
converts command-line arguments through the system ANSI code page before starting
Java. Characters that cannot be represented there can be replaced, so passing
`-Dsevensix.data.file=资料/task list.txt` is not portable across Windows settings.
Setting `file.encoding`, `stdout.encoding`, or the JVM language does not repair a
path that was already changed by the native launcher.

The subprocess test therefore starts in a real Unicode working directory via
`ProcessBuilder.directory` and passes an ASCII relative filename containing spaces.
It still verifies Unicode task content on disk and reloading in a fresh JVM.
`ConsoleUiTest` separately sets a Unicode configuration value using
`System.setProperty` and verifies saving and reloading through `SevenSix.main`.
This preserves application-level coverage without claiming that arbitrary Unicode
launcher arguments work on every Windows code page. Production code is unchanged.
The subprocess streams explicitly use UTF-8 to match the test's input and output.

## GUI coverage result

On 2026-10-14, using macOS and Zulu JavaFX JDK 25.0.3:

| Check | Result |
| --- | --- |
| Default-locale JUnit | 244 passed, none skipped |
| Chinese-locale JUnit | 244 passed, none skipped |
| `DialogBox` line coverage | 28/28 (100%) |
| `SevenSixGui` line coverage | 121/121 (100%) |
| `SevenSixGui` branch coverage | 8/8 (100%) |
| `Launcher` line coverage | 2/2 (100%) |
| Checkstyle and both coverage gates | Passed |
| Recorded console UI cases | 21 passed |

All three GUI classes also have 100% instruction and method coverage.
`DialogBox` and `Launcher` have no branches. Cross-platform execution of the new
GUI tests remains to be confirmed by CI; these results are from macOS.

## Earlier recorded result

On 2026-09-14, macOS 26.5.1 with Zulu JavaFX JDK 25.0.3:

| Check | Result |
| --- | --- |
| Default-locale JUnit | 163 passed, none skipped |
| Chinese-locale JUnit | 163 passed, none skipped |
| Non-GUI line coverage | 511/512 (99.8%) |
| Non-GUI branch coverage | 264/267 (98.9%) |
| Non-GUI method coverage | 152/152 (100%) |
| Checkstyle and coverage gates | Passed |
| Recorded console UI cases | 16 passed |

These local results include the revised Unicode-path tests. Confirmation of the
revised tests on the Windows CI runner is still pending.

The previous suite had 63 tests, 85.9% line coverage, and 77.5% branch coverage with
the same GUI exclusions. Coverage measures execution, not proof of correctness.

The remaining uncovered line is the defensive catch for failure to delete an
abandoned temporary file. Reproducing that requires filesystem fault injection or
a timing-dependent permission change during save. Three remaining branches in
`TaskValidation` cover blank descriptions already rejected by constructors and
mismatched model subtypes with identical type markers. These are not reached by
valid application-created tasks. They remain visible in the report rather than
being excluded to inflate coverage.

See [the manual GUI plan](gui-test-plan.md) for rendering checks and the pending
cross-platform, physical input, display-scaling, and OS-language matrix. JVM locale
tests do not establish compatibility with a different OS or input method.
