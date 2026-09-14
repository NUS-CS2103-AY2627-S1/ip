# Penguin

Penguin is a desktop task manager featuring Pip, a cheerful productivity
penguin. It combines fast command-based task management with a JavaFX chat
interface.

![Penguin GUI](docs/Ui.png)

## Features

- Add to-dos, deadlines, and events.
- Mark, unmark, and delete tasks.
- Find tasks by description or date.
- Undo up to five recent changes.
- Save tasks automatically between sessions.
- Use commands and searches without matching capitalization.

## User guide

See the [Penguin User Guide](https://brianlim35.github.io/ip/) for installation
instructions and the complete command reference.

## Running Penguin

### Using the JAR

1. Install JDK 25.
2. Download `penguin.jar` from the
   [latest release](https://github.com/BrianLim35/ip/releases).
3. Open a terminal in the JAR's folder.
4. Run:

   ```shell
   java -jar penguin.jar
   ```

### Using IntelliJ IDEA

1. Open this project in IntelliJ IDEA.
2. Configure the project to use JDK 25.
3. Run `penguin.Launcher` to start the JavaFX application.

## Building and testing

Run the following command from the project root:

```shell
./gradlew clean test checkstyleMain checkstyleTest shadowJar
```

The distributable JAR will be created at:

```text
build/libs/penguin.jar
```

## Data storage

Penguin stores tasks automatically in:

```text
data/penguin.txt
```

The location is relative to the folder from which Penguin is started.
