# Arav (II)

Arav (II) is a Java 25 task manager with a JavaFX GUI and a text interface.
It supports todos, deadlines, events, search, completion tracking, deletion,
and alphabetical sorting. Both interfaces use the same command implementation.

## Build and run

Install **JDK 25**. On macOS with SDKMAN and the course's JavaFX distribution:

```bash
sdk use java 25.0.3.fx-zulu
./gradlew clean check jar macArmJar javadoc
./gradlew run
```

The Gradle wrapper downloads the pinned Gradle version on its first run;
no separate Gradle installation is needed. On Windows, use `gradlew.bat`.
Import the project as a Gradle project in VS Code or IntelliJ and select
JDK 25. Keep Java sources under `src/main/java`.

For the text interface:

```bash
./gradlew --console=plain runCli
```

The CLI retains the ASCII Arav (II) banner. Enter `help` in either interface
for the command reference. Enter `bye` to exit.

## Run a packaged JAR

Build both artifacts with `./gradlew jar macArmJar`, or download one from
[GitHub Releases](https://github.com/arav31/ip/releases). Use Java 25:

| Platform | Artifact | Command |
| --- | --- | --- |
| Apple Silicon Mac (M1 or newer) | `aravii-mac-aarch64.jar` | `java -jar aravii-mac-aarch64.jar` |
| Intel Mac, Windows x64, Linux x64 | `aravii.jar` | `java -jar aravii.jar` |

These artifacts bundle JavaFX. ARM and Intel Mac native libraries have the
same filenames, so they must not be combined into one duplicate-excluding JAR.
The cross-platform x64 JAR follows the course tutorial's JavaFX dependencies.
Linux ARM and Windows ARM release artifacts are not supplied.

For CLI-only execution, use `java -cp <jar-file> aravii.AravII`.
Run from the folder where you want to keep your task data.

## Commands

Commands and date/time markers are case-sensitive. Task numbers are the
one-based numbers shown by `list`. Search results retain those same numbers.

| Command | Example / behavior |
| --- | --- |
| `todo <description>` | `todo read notes` |
| `deadline <description> /by <YYYY-MM-DD>` | `deadline submit report /by 2026-09-20` |
| `event <description> /from <YYYY-MM-DD HH:MM> /to <YYYY-MM-DD HH:MM>` | `event meeting /from 2026-09-21 14:00 /to 2026-09-21 15:00` |
| `list` | Show all tasks. |
| `mark <number>` | `mark 1` marks a task complete. |
| `unmark <number>` | `unmark 1` marks it incomplete. |
| `delete <number>` | `delete 1` removes a task and renumbers the list. |
| `find <keyword>` | `find report` searches descriptions and date details, ignoring case. |
| `sort` | Sort alphabetically by description, ignoring case; equal descriptions retain their order. |
| `help` | Show every available command. |
| `bye` | Finish saving and exit. |

Dates are stored as Java date/time objects. For example, input `2026-09-20`
is displayed as `Sep 20 2026`. Event times use a 24-hour clock, and an event
cannot end before it starts. Invalid calendar dates are rejected rather
than silently corrected. Descriptions cannot be empty or contain tabs,
newlines, or other control characters.

## Saving and recovery

Changes are automatically saved to `data/aravii.txt`, relative to the
working directory. Missing folders are created on the first successful
change. Existing valid TSV saves remain compatible; dates are still saved
in ISO form even though their display format has changed.

A save writes a temporary file and atomically replaces the original.
If loading fails, the original is left untouched and task commands are
blocked; `help` and `bye` still work. Back up the file, repair the reported
record (or move the backed-up file aside to start fresh), then restart.

If saving fails, both interfaces show the error. Changes remain in memory.
Repair the path or permissions and enter `bye` to retry. The GUI also
prevents normal window closing while changes remain unsaved. Force-quitting
or losing power can still lose unsaved in-memory changes. A filesystem
without atomic replacement reports a save error instead of risking the
existing file. Avoid running two instances against the same save file.

## Structure and verification

- `Task`, `Todo`, `Deadline`, `Event`: task inheritance and typed dates.
- `TaskList`: collection, search, stable sort, and stream-based formatting.
- `Parser`: input validation; `CommandHandler`: command execution.
- `Storage`: TSV loading and safe file replacement.
- `ChatSession`: shared autosave/error lifecycle.
- `Ui` and `AravIIApplication`: console and JavaFX presentation.

`./gradlew check` runs JUnit and Checkstyle for both main and test sources.
Tests use temporary files and do not touch your actual save.
`./gradlew javadoc` generates API documentation under `build/docs/javadoc`.
GitHub Actions runs the checks and packages both JARs using Java 25.

Follow the course's [Java coding standard](https://se-education.org/guides/conventions/java/intermediate.html)
and [Git commit conventions](https://se-education.org/guides/conventions/git.html):
imperative, capitalized subjects; meaningful rationale bodies wrapped at
72 characters.
