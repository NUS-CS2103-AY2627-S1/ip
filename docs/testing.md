# Automated testing

Use Zulu JDK `25.0.3.fx-zulu` and Python 3.9 or newer. The complete suite needs
a desktop session because it includes twenty-one JavaFX interaction tests.
Tests use temporary data files; the console regression runner also uses a
separate working directory for each case.

## Run the complete checks

From the project root on macOS:

```sh
export JAVA_HOME="$HOME/.sdkman/candidates/java/25.0.3.fx-zulu"
export PATH="$JAVA_HOME/bin:$PATH"
java -version
javac -version
./gradlew clean check jacocoTestReport
python3 test/run_ui_plan.py --plan test/ui-test-plan.md -- \
  "$JAVA_HOME/bin/java" -cp "$PWD/build/classes/java/main" glennon.Glennon
```

Both version commands must report `25.0.3`. The console launcher, shared runner
(`test/run_ui_tests.py`), and specification (`test/ui-test-plan.md`) are all
included in the repository, so the commands also work in a fresh clone without
installing Codex skills or Python packages. The console runner supports macOS
and Linux. It checks complete responses, including whitespace, and rejects
duplicate command sequences. Review the plan before running it and preserve
its expected output unless the intended behavior changes.

Reports:

- JUnit: `build/reports/tests/test/index.html`
- Coverage: `build/reports/jacoco/test/html/index.html`
- Machine-readable coverage: `build/reports/jacoco/test/jacocoTestReport.xml`
- Checkstyle: `build/reports/checkstyle/main.html` and `test.html`

## Tested contracts

| Area | Automated checks |
| --- | --- |
| Parser | Every command family and bound argument; missing/extra arguments; separators and repeated flags; index overflow; strict dates, leap centuries, and midnight/year transitions; Unicode description edges, blank descriptions, and forbidden control characters |
| Tasks | Description validation and Unicode edge normalization; exact type/detail comparisons; completion transitions and idempotence; date overlap; all-day versus timed display; nanosecond precision; English date rendering with a Chinese format locale |
| Task list | Duplicates across the entire list; every indexed operation on empty/populated lists; defensive copying and read-only views; filter snapshots; stable sorting, ties, event start times, and preserved status |
| Storage | Exact serialized records; all task types and completion states; Unicode and precise date-time roundtrips; Windows line endings and missing final newline; corrupt records and duplicates; independent loads; atomic replacement and cleanup failures |
| Commands | Read-only commands never save and preserve task identities/status; exact result numbering and empty results; mutating commands preserve memory and disk after a failed save and support safe retries |
| Sessions | Filtered mission numbers followed by mark/unmark/delete, sorting, failed saves, retry, and restart; Unicode blank and duplicate rejection; GUI-facing responses and error status; console greeting/dividers; EOF; queued input after exit; startup failures preserve data |
| JavaFX | Twenty-one FXML interaction, validation, filtered-numbering, scrolling, focus, and resizing scenarios; see [the GUI plan](gui-test-plan.md) |

The suite contains 249 JUnit tests, including 21 JavaFX interaction tests.
Several tests check tables of related input boundaries within one scenario.
These assertions verify observable results and preserved state, rather than
only invoking methods to increase coverage.

## Coverage scope and remaining gaps

JaCoCo measures all production classes outside `glennon.gui`. GUI tests still
execute; excluding their presentation classes from this report keeps core
coverage separate from visual checks. The project pins
[JaCoCo 0.8.14](https://github.com/jacoco/jacoco/releases/tag/v0.8.14), which
supports Java 25.

The expanded suite covers **541/546 core lines (99.1%)**, **224/226 branches
(99.1%)**, and **126/128 methods (98.4%)**. All task and command classes have
100% line and branch coverage. Treat these numbers as a snapshot; regenerate
the report after further changes.

The remaining gaps are:

- The no-argument `Glennon` constructor and CLI `main` forwarding method are
  outside the JUnit coverage. The full console plan exercises `main` in fresh
  processes, while JUnit tests `run()` with isolated streams and storage.
- Storage's default task-type switch arm is unreachable after field-count
  validation has already rejected unknown type markers. Tests verify that
  rejection at the public storage boundary.
- Load-path validation normally finds an existing directory before reaching
  the end of the ancestor chain. Tests cover missing parent chains and paths
  blocked by a file, including nested paths.

The console test plan includes corrected full-log numbers for filtered results
and regression cases for filter-then-update workflows and Unicode description
spaces. Each changed feature has at least seven distinct positive cases and
three negative cases, with unique complete input sequences.

## Manual portability checks

Automated checks on macOS, including Unicode data, Windows-style line endings,
and Chinese format-locale assertions, do not substitute for running on another
operating system.

### Reported JAR smoke tests

The following results were reported in
[forum issue #329](https://github.com/NUS-CS2103-AY2627-S1/forum/issues/329).
Each tester reported successfully launching the JAR, adding a to-do, deadline,
and event, listing missions, marking a mission, and deleting a mission. After
restart from the same folder, the remaining missions and marked status were
preserved, and the deleted mission did not return.

| Tester | Reported OS | Reported Java version | Smoke-test result |
| --- | --- | --- | --- |
| cacklycrackle | Windows 11, 25H2 | Not supplied | Passed |
| MarcusMa06-code | macOS 27.0 | Not supplied | Passed |
| elsxyss | macOS 26.5 | Zulu OpenJDK 25.0.3 | Passed |

elsxyss also reported successful checks of `unmark`, `find`, `on`, `sort`,
duplicate-mission handling, date-only deadlines, all-day events, leap-year
dates, and Unicode and punctuation in descriptions. Invalid input checks
covered a missing to-do description, an impossible calendar date, an event
ending before it starts, an invalid mission number, and extra arguments after
`list`. The application remained usable after each error. The feedback suggested
minor improvements to search-result spacing and invalid-date guidance.

These reports concern the supplied JAR; its build identity was not provided,
so they do not verify the current rebuilt artifact. Linux remains unverified.
The reports also do not establish coverage of the display, keyboard, or
alternate OS language checks below.

### Checks for future manual runs

Use a temporary folder for each manual run. Follow the
[GUI visual and packaged checks](gui-test-plan.md#visual-and-packaged-checks),
then record the OS, JDK, language, display scale, resolution, and result for:

1. Launching the packaged application, entering English and Chinese mission
   descriptions, and restoring them after restart.
2. Minimum and maximized window sizes, display scaling, and long text wrapping
   without clipped controls or unreadable characters.
3. Keyboard input, focus, scrolling, closing the window, and the `bye` command.
4. Deadline/event date rendering and parsing under English and Chinese OS
   language settings.

Keep manual results separate from the automated pass counts and note any
environment that has not been tested.
