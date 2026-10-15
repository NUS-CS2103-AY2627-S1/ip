# Testing NutriSoy / Soya

## Automated checks

Use Java 25. On the project macOS setup:

```bash
sdk use java 25.0.3.fx-zulu
./gradlew clean build
```

The build runs JUnit and Checkstyle. The test report is at
`build/reports/tests/test/index.html`.

Tests cover command dispatch and execution, input and index boundaries,
duplicate detection, task identity and formatting, tag operations, storage
compatibility and malformed records, failed saves, application restarts,
console interaction, output capture, and GUI response error metadata.

Storage tests use JUnit temporary directories. Tests that change console
streams or the default locale restore them in `finally` blocks. Keep test
execution sequential because these resources are shared by the JVM.

No numerical coverage percentage is claimed. JavaFX layout, launchers,
OS-specific permissions, disk-full conditions, and actual display behavior
are outside the automated suite.

## Manual checklist

These checks are instructions, not a record of tests performed. Use a separate
working directory or a backup of your data file for manual testing.

| Area | Action | Expected result |
| --- | --- | --- |
| Startup | Launch with Java 25 using `./gradlew run` | Soya greeting, input and Do it button appear |
| Resize | Resize from the minimum window size to a large window | Input remains usable and long replies wrap |
| Scroll | Add enough tasks and messages to fill the window | Latest reply is visible and older replies can be read |
| Error display | Enter `deadline report /by 2026-02-30` | Helpful error appears with error styling |
| Recovery | Follow that error with `todo read book` | Success response appears and the input remains usable |
| Persistence | Add all task types, tag and mark them, exit, reopen, then `list` | Task details and state survive restart |
| Platforms | Run on each available supported OS with Java 25 and JavaFX | App launches and text remains readable |
| Language | Run under English and Chinese OS language settings | Commands, dates and saved Unicode descriptions remain usable |
| Permissions | Make a test data directory unwritable and attempt a save | Save failure is reported; record whether recovery works after access is restored |

Record OS, Java version, screen size, language setting, result, and any issue
when performing manual checks. Do not count an unperformed check as passed.

## Submission verification (17 September 2026)

- Java 25.0.3.fx-zulu, macOS ARM64: JUnit, Checkstyle, and packaging checked.
- All 161 JUnit cases passed. The packaged `nutrisoy.jar` also launched from
  an isolated directory with the JDK's bundled JavaFX modules excluded, verifying
  that the JAR contains its own JavaFX dependencies. JavaFX emitted classpath and
  native-access warnings; no FXML loading error occurred.
- Regression tests include damaged-file write protection, safe-save retry,
  exit after save failure, empty console input, UTF-8 descriptions, and uppercase
  commands under a Turkish JVM locale. This is not a full OS-language test.
- An isolated JavaFX smoke harness loaded the packaged FXML/CSS and submitted
  todo, tag, mark, list, and invalid-date commands using temporary task data.
  App snapshots were inspected at normal and small window sizes; replies and
  error text wrapped, and the composer remained visible.
- After replacing the profile PNGs with JavaFX/CSS avatars, the clean build and
  all 161 tests passed again. The GUI harness verified nine text-avatar nodes
  and no ImageView nodes for the sample conversation. Normal and small-window
  snapshots were inspected, and the User Guide screenshot was refreshed.
- Windows/Linux GUI behavior, physical high-DPI displays, and mouse/keyboard
  interaction with the close-confirmation dialog still need manual verification.

## Release v0.2 verification (17 September 2026)

- Java 25 was confirmed before `./gradlew clean check shadowJar`; all 163
  JUnit cases and Checkstyle passed, including native-platform selection tests.
- The fat JAR contains JavaFX classes and separate native directories for
  Windows x64, Linux x64, macOS Intel, and macOS Apple Silicon. This supersedes
  the earlier single-platform packaging limitation.
- The JAR launched using `java -jar nutrisoy.jar` from an isolated folder on
  macOS ARM64. A second launch excluded the JDK's bundled JavaFX modules and
  verified that JavaFX loaded libraries extracted from this JAR.
- The packaged-JavaFX GUI harness passed todo, tag, mark, list, invalid-date,
  and persistence checks. JavaFX classpath/native-access warnings are not
  themselves launch failures.
- Windows, Linux, and Intel Mac runtime smoke tests remain unperformed here.
  Ask a teammate on another supported OS to download the release JAR, launch
  it from an empty folder, add all task types, tag and mark a task, then exit
  and reopen to check persistence. Record the OS, Java version, and outcome.
