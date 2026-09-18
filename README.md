# Toothless

Toothless is a warm, friendly task-management chatbot with a JavaFX chat interface. It remembers todos,
deadlines, and events between sessions.

## Requirements

- JDK 25
- No separate JavaFX installation is needed; Gradle supplies the platform dependencies.

## Running the GUI

From the project directory, use Java 25 and run:

```shell
./gradlew run
```

The window supports both the Send button and the Enter key. Select **Help** to reveal clickable command examples.
Select **Edit task** to reveal the description, deadline, and event edit choices; selecting one places its example
in the input field so it can be adjusted before sending.

See the [Toothless User Guide](docs/README.md) for all commands.

## Verifying the project

Run the complete automated suite and code-style checks with:

```shell
./gradlew clean test checkstyleMain checkstyleTest
```

## IntelliJ setup

1. Open the project directory in a recent IntelliJ version.
2. Configure the project SDK as JDK 25 and keep the language level at **SDK default**.
3. Import the Gradle project when prompted.
4. Run the Gradle `run` task to launch the JavaFX interface.

Keep `src/main/java` as the source root and `src/main/resources` as the resources root so Gradle can package the
Java classes, FXML layouts, CSS, and images correctly.
