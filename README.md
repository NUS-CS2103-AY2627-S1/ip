# Yachiyo

Yachiyo is a JavaFX desktop task manager for tracking to-dos, deadlines, and events through a
chat-style interface. Tasks are stored locally and restored the next time the application starts.

## Features

- Add to-dos, deadlines, and events.
- Edit individual task descriptions and date-time details.
- Mark tasks as complete or incomplete.
- Find tasks by description or date.
- Delete tasks and list the current lineup.
- Track total and remaining tasks from the pinned summary.
- Distinguish correctable input, invalid operations, and storage failures visually.
- Show time-aware greetings and celebratory feedback when every task is complete.
- Save every change automatically to `data/yachiyo.txt`.

See the [user guide](docs/README.md) for command formats and examples.

## Prerequisites

- JDK 25
- IntelliJ IDEA (optional)

The repository includes the Gradle wrapper, so a separate Gradle installation is not required.

## Running with Gradle

On macOS or Linux:

```shell
./gradlew run
```

On Windows:

```bat
gradlew.bat run
```

## Setting up in IntelliJ IDEA

1. Open the project directory and import it as a Gradle project.
2. Configure the project and Gradle JVMs to use **JDK 25**. Keep the project language level set to
   `SDK default`.
3. Open `src/main/java/yachiyo/Launcher.java`.
4. Run `Launcher.main()` to start the JavaFX interface.

Keep `src/main/java` as the source root because Gradle and IntelliJ IDEA expect Java source files in
that directory.

For the terminal interface, run `Yachiyo.main()` from
`src/main/java/yachiyo/Yachiyo.java` instead.

## Data storage

Yachiyo creates `data/yachiyo.txt` relative to the directory from which it is launched. Tasks are
loaded into memory when the application starts and every successful change is saved automatically.
External changes to the file are not reloaded after initialization; restart Yachiyo to load them.

If the file cannot be read or contains malformed task data, Yachiyo reports a storage error and does
not run task-related commands. Correct or restore the file, then retry a command to load it again.
The `bye` command remains available so the application can always be closed safely.

## Building the application

Create an executable JAR containing the application and its dependencies:

```shell
./gradlew shadowJar
```

Run the generated JAR:

```shell
java -ea -jar build/libs/yachiyo.jar
```

## Running tests and code checks

Run the test suite and Checkstyle checks together:

```shell
./gradlew check
```

Run only Checkstyle:

```shell
./gradlew checkstyleMain checkstyleTest
```

Visual and native window behavior is covered by the
[manual GUI testing checklist](docs/ManualTesting.md).

## Project structure

```text
src/main/java/yachiyo/       Application source code
src/main/resources/          JavaFX views, styles, and images
src/test/java/yachiyo/       Automated tests
docs/README.md               User guide
docs/ManualTesting.md         Manual GUI testing checklist
data/yachiyo.txt             Local task data created at runtime
```
