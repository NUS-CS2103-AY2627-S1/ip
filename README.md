# Otaku

Otaku is an anime-inspired task-tracking chatbot that turns everyday work into a quest log. It supports todos,
deadlines, events, searching, task completion, deletion, chronological sorting, and persistent local storage.

The JavaFX interface presents Otaku as a questkeeper, highlights invalid commands, and adapts message widths when
the window is resized.

## Requirements

- Java 25 with JavaFX support
- macOS, Windows, or Linux

## Running Otaku

From the project root, start the graphical interface with:

```shell
./gradlew run
```

On Windows, use `gradlew.bat run` instead.

To create a standalone JAR and run it:

```shell
./gradlew shadowJar
java -jar build/libs/otaku.jar
```

Otaku saves tasks to `data/otaku.txt`, creating the file and its directory when needed.

## Commands

| Command | Example | Purpose |
| --- | --- | --- |
| `todo` | `todo read book` | Adds a task without a date. |
| `deadline` | `deadline submit report /by 2026-09-30` | Adds a task with a due date. |
| `event` | `event workshop /from 2026-10-01 /to 2026-10-02` | Adds an event with start and end dates. |
| `list` | `list` | Shows the full quest log. |
| `find` | `find book` | Finds tasks containing a keyword. |
| `mark` | `mark 2` | Marks a numbered task as complete. |
| `unmark` | `unmark 2` | Marks a numbered task as incomplete. |
| `delete` | `delete 2` | Removes a numbered task. |
| `sort` | `sort` | Sorts dated tasks chronologically. |
| `bye` | `bye` | Ends the session. |

Dates use the `yyyy-MM-dd` format. For more details, see the [user guide](docs/README.md).

## Testing

Run the complete JUnit suite with:

```shell
./gradlew test
```

Generate the JaCoCo coverage report with:

```shell
./gradlew jacocoTestReport
```

The HTML report is generated at `build/reports/jacoco/test/html/index.html`. Additional command-line and manual GUI
test plans are available in the [`test`](test) directory.

## Acknowledgements

- This individual project was developed from the Duke project materials provided by the
  [SE-EDU teaching team](https://se-education.org/).
- Yuxiang Liu used OpenAI Codex extensively to assist with implementation, refactoring, GUI styling, error handling,
  automated tests, test planning, and documentation. All AI-assisted changes were reviewed and tested before use.
- [JavaFX](https://openjfx.io/) is used for the graphical interface.
- [JUnit 5](https://junit.org/junit5/) is used for automated testing.
- [JaCoCo](https://www.jacoco.org/jacoco/) is used for code-coverage reporting.
- The [Gradle Shadow plugin](https://gradleup.com/shadow/) is used to package the executable JAR with its runtime
  dependencies.
- No third-party images, audio, or copied external code snippets are included in this project.
