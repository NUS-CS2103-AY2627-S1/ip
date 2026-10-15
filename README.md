# Gepit

Gepit is a friendly desktop task manager that remembers todos, deadlines, and events between sessions.

![Gepit product screenshot](docs/Ui.png)

## Quick start

1. Install Java 25.
2. Download `Gepit.jar` from the latest GitHub release.
3. Open a terminal in the download folder and run `java -jar Gepit.jar`.

For commands and examples, see the [Gepit User Guide](docs/README.md).

## Development

Run the automated checks with `./gradlew clean test checkstyleMain checkstyleTest`.

Build the cross-platform fat JAR with `./gradlew clean shadowJar`. The resulting file is `build/libs/Gepit.jar`.
