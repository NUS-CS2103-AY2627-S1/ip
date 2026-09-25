# Friedberg

Friedberg is a Java task-management chatbot with a JavaFX graphical interface and a command-line interface. It is designed for users who prefer managing tasks through concise text commands.

**[Read the Friedberg User Guide](https://chuanhao01.github.io/ip/)** for command formats, examples, and screenshots.

![Friedberg JavaFX interface](docs/Ui.png)

## Features

Friedberg supports:

- to-do tasks;
- deadlines with validated dates;
- events with start and end dates;
- marking and unmarking tasks;
- finding active tasks by description;
- deleting tasks;
- archiving and restoring tasks;
- automatic local data persistence;
- strict command-format validation;
- an in-application `help` command.

## Requirements

- Java Development Kit (JDK) 25
- No separate Gradle installation is required; the project includes the Gradle Wrapper.

## Running Friedberg

### JavaFX GUI

From the project root, run:

```bash
./gradlew run
```

On Windows, use:

```bat
gradlew.bat run
```

### Command-line interface

```bash
./gradlew runCli
```

## Building the application

Create the executable Shadow JAR with:

```bash
./gradlew shadowJar
```

The generated application is located at:

```text
build/libs/Friedberg.jar
```

Run it with:

```bash
java -jar build/libs/Friedberg.jar
```

## Running tests and checks

Run the automated test suite:

```bash
./gradlew test
```

Run tests and Checkstyle together:

```bash
./gradlew check
```

## Data storage

Friedberg automatically creates and uses these files relative to the working directory:

```text
data/friedberg_data
data/friedberg_archive
```

- `friedberg_data` stores active tasks.
- `friedberg_archive` stores archived tasks.

Do not edit these files while Friedberg is running.

## Command overview

Enter `help` in Friedberg to display all supported commands.

| Action | Command format |
| --- | --- |
| Show help | `help` |
| Add a to-do | `todo DESCRIPTION` |
| Add a deadline | `deadline DESCRIPTION /by DATE` |
| Add an event | `event DESCRIPTION /from FROM_DATE /to TO_DATE` |
| List active tasks | `list` |
| Find active tasks | `find QUERY` |
| Mark a task | `mark TASK_INDEX` |
| Unmark a task | `unmark TASK_INDEX` |
| Delete a task | `delete TASK_INDEX` |
| Archive a task | `archive TASK_INDEX` |
| List archived tasks | `alist` |
| Restore an archived task | `unarchive TASK_INDEX` |
| Exit | `bye` |

Dates use the `yyyy-MM-dd` format, for example `2026-09-10`.

## Project structure

```text
src/main/java/        Java source code
src/main/resources/   FXML, CSS, and image resources
src/test/java/        Automated tests
docs/                 User guide and screenshots
data/                 Runtime task data
```

## Acknowledgements

Friedberg was developed as an individual project for the National University of Singapore (NUS) module **CS2103 Software Engineering**. It is based on the Duke project used in the SE-EDU introductory software engineering curriculum.
