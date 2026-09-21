# Automated testing and coverage

Run the checks with Java 25 (including JavaFX). On macOS:

```sh
source "$HOME/.sdkman/bin/sdkman-init.sh"
sdk use java 25.0.3.fx-zulu
./gradlew check
```

`check` runs JUnit and Checkstyle. The JUnit task also generates a JaCoCo report at
`build/reports/jacoco/test/html/index.html` and machine-readable coverage at
`build/reports/jacoco/test/jacocoTestReport.xml`. To regenerate the report directly,
run `./gradlew jacocoTestReport`. JaCoCo 0.8.14 supports this project's Java 25
class files ([release notes](https://www.jacoco.org/jacoco/trunk/doc/changes.html)).

Tests exercise task models, every command, parsing, date/time validation, storage
escaping and corruption, rollback, protected sessions, and the shared UI adapter.
The CLI integration tests start real Java processes in temporary directories, so
startup, end-of-input, exit, error recovery, and restart persistence are checked
without touching the user's saved tasks. Each subprocess has a 15-second timeout.
When Gradle enables JaCoCo, subprocesses write separate coverage files that are
included in the report; these files are cleared before the next JUnit run.

The CLI tests use English/US, Chinese/China, and Turkish/Turkey JVM locale settings.
They verify Unicode persistence, English date formatting, and locale-independent
case-insensitive searching. Storage tests also exercise LF, CRLF, and CR line
endings. These checks do not simulate an entire OS language setting, font rendering,
or Chinese input method behavior.

## Coverage scope and remaining manual work

Only `CaitlynGui` (including its nested classes) and the JavaFX `Launcher` are
excluded from coverage. The GUI's shared session, commands, output adapter, and
command menu examples remain included. Follow the manual GUI checks and environment
matrix in [ui-test-plan.md](ui-test-plan.md) for layout and interaction coverage.

The uncovered non-GUI paths are the unused `Caitlyn` constructor, defensive guards
that public APIs cannot reach with invalid values, assertion-failure paths, and
the storage fallback for filesystems without atomic moves. Tests intentionally do
not use reflection to reach private guards or alter the production filesystem just
to increase coverage. Coverage is evidence of executed code, not a guarantee that
all combinations or platform failures have been tested.

## Latest local verification

On macOS with Java 25.0.3.fx-zulu, the expanded suite passed 97 JUnit tests and
Checkstyle. Coverage, excluding the two GUI classes, was:

| Metric | Covered / total | Coverage |
| --- | --- | --- |
| Lines | 559 / 566 | 98.8% |
| Branches | 217 / 223 | 97.3% |
| Methods | 138 / 139 | 99.3% |

The 27 CLI regression cases also passed. To run that plan from the repository root:

```sh
JDK_JAVAC_OPTIONS='--add-modules=javafx.controls' \
    python3 .codex/skills/test-ui/scripts/run_ui_tests.py
```

The complete input/output transcript from the local run is in
`build/reports/ui/transcript.txt`. Build reports are generated artifacts and are
removed by `clean`. GUI, Windows, Linux, screen-resolution, and OS-language manual
checks have not been performed in this local run. The existing CI workflow is
configured to run `check` on Windows, Linux, and macOS; its results must be verified
separately after the changes are pushed.
