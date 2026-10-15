# Cheecken

Cheecken is a JavaFX chatbot for managing to-dos, deadlines, and events. Type a command,
press **Enter** or click **Send**, and read the reply in the conversation.

## Run the app

Use **JDK 25**. On macOS, use the JavaFX-bundled distribution recommended by the
[SE-EDU tutorial](https://se-education.org/guides/tutorials/javaFxPart1.html#setting-up-java-fx).
For this project, the SDKMAN installation is `25.0.3.fx-zulu`:

```bash
sdk use java 25.0.3.fx-zulu
./gradlew run
```

In IntelliJ IDEA, open the project as a Gradle project, select JDK 25 for both the project
and Gradle JVM, reload Gradle, and run `cheecken.Launcher`.
On Windows, replace `./gradlew` with `gradlew.bat`.

To build and launch a standalone JAR:

```bash
./gradlew shadowJar
java -jar build/libs/cheecken-1.0-SNAPSHOT-all.jar
```

The Gradle configuration follows the tutorial's JavaFX **17.0.7** dependencies for
Windows, macOS, and Linux, including base, controls, FXML, and graphics. The GUI uses
Java layout code rather than FXML so event handling and layout are easy to trace.
The separate `Launcher` is the entry point recommended by the tutorial.
Desktop verification was performed on macOS with the specified Java 25 distribution.

## Using Cheecken

Open **Command guide** in the window for examples. Commands include:

```text
todo read a book
deadline submit report /by 15/10/2026 1800
event meeting /from 15/10/2026 0900 /to 15/10/2026 1000
deadline read chapter /by tomorrow
deadline send email /by today 1800
event study group /from Mon 0900 /to Monday 1000
deadline start now /by now
list
find book
mark 1
unmark 1
delete 1
bye
```

Task numbers start at 1; use `list` to see them. Dates accept `d/M/yyyy` with optional
24-hour `HHmm` time, or ISO dates/date-times such as `2026-10-15` and `2026-10-15T18:00`.
Natural dates accept `today`, `tomorrow`, `now`, and all English weekday names:
`Mon`–`Sun` or `Monday`–`Sunday`. Date words are case-insensitive; flags stay lowercase.
You can append `HHmm` to `today`, `tomorrow`, or a weekday, but not to `now`.

A weekday always means its **strictly next occurrence**: `Mon` entered on Monday
means seven days later. Dates use your computer's local timezone at submission.
Both event endpoints use the same clock reading and resolve independently;
past times and end dates before start dates remain allowed. `now` uses the current
date and time. Date-only inputs display without a time; supplied times display in
12-hour format. Additional phrases such as `next Monday`, `yesterday`, and `6pm`
are not supported. See the [user guide](docs/README.md) for examples and details.

Blank messages are ignored. Replies wrap to fit the window and
scroll into view. `bye` closes the window and exits the app. Closing the window directly is also safe: task changes are saved after each command.

Tasks are stored in `data/cheecken.txt`, relative to the directory from which you launch
the application. Reopen from the same directory to load the same tasks. Storage errors
appear in the conversation. The conversation history itself is not persisted.
Natural dates are resolved once and saved as absolute values, so they never move
when you reopen the app. New date-only deadlines retain their date-only display.
Existing stored midnight timestamps remain timed values. Manually stored natural
date expressions are malformed records and are skipped, not reinterpreted.

## Understanding the code

- `Launcher`: starts JavaFX.
- `Main`: builds the window and forwards input to `Cheecken.getResponse`.
- `Cheecken`: handles commands and maintains one chatbot session's state.
- `Ui`: formats responses and sends them to the supplied output destination.
- `Storage`: loads and saves tasks and reports file errors to the same destination.
- `src/main/resources/styles/chat.css`: controls colours, spacing, and fonts.

The console interface remains available for debugging:

```bash
./gradlew runCli
```

## Tests and coding style

```bash
./gradlew check
./gradlew jacocoTestReport
./gradlew guiTest
```

`check` runs the normal automated tests and Checkstyle for application and test code.
`jacocoTestReport` runs the JUnit suite and generates coverage at
`build/reports/jacoco/test/html/index.html` (and an XML report alongside the HTML folder).
Coverage excludes only the JavaFX window and launcher (`Main` and `Launcher`).
Tests cover parsing, command validation and recovery, task types and lists, dates,
response formatting, console sessions, and storage round trips and failures.
File tests use temporary directories; date-sensitive tests use fixed clocks.
The report does not include coverage from child JVMs used by process-based tests.
Defensive assertion failures and redundant guards are not forced through reflection.
`guiTest` is an additional opt-in JavaFX acceptance test requiring a graphical desktop.
It uses temporary task data to exercise Enter, Send, blank input, error recovery, command
help, scrolling, resizing, exit behaviour, and persistence after reopening.
See `test/ui-test-plan.md` for the acceptance plan and the legacy console cases.
Natural-date tests use fixed clocks, including timezone and midnight boundaries.
The console acceptance fixture runs at Monday, 7 September 2026, 15:30 UTC;
the normal application always uses the computer's local clock.

Checkstyle **14.1.0** uses the [SE-EDU Java coding standard](https://se-education.org/guides/conventions/java/intermediate.html)
configuration from [AddressBook Level 3](https://github.com/se-edu/addressbook-level3/tree/master/config/checkstyle).
Run only the style checks with:

```bash
./gradlew checkstyleMain checkstyleTest --continue
```

Reports are in `build/reports/checkstyle/` and `build/reports/tests/`.
For editor feedback, install Checkstyle-IDEA, select version **14.1.0**, activate
`config/checkstyle/checkstyle.xml`, and include test sources in the scan scope.
