# Pulbot

Pulbot is a desktop task manager with a chat-style interface. You enter short
commands to create todos, deadlines, and events, and Pulbot saves your tasks
between sessions.

## Features

- Create todos, deadlines, and events.
- Mark tasks as complete or incomplete.
- Delete tasks by their displayed number.
- Find tasks by a case-insensitive keyword.
- View deadlines and events that occur on a specific date.
- Reject duplicate tasks before they are saved.
- Store task data automatically in `data/pulbot.txt`.

## Requirements

- Java Development Kit (JDK) 25
- A desktop environment that supports JavaFX

The Gradle wrapper downloads the required build tools and JavaFX dependencies.
You don't need to install Gradle separately.

## Run Pulbot

1. Clone the repository and open its directory:

   ```shell
   git clone https://github.com/pulas2345/ip.git
   cd ip
   ```

2. Confirm that your terminal uses JDK 25:

   ```shell
   java -version
   ```

   On macOS with SDKMAN, you can select the required JDK with this command:

   ```shell
   sdk use java 25.0.3.fx-zulu
   ```

3. Start the application:

   ```shell
   ./gradlew run
   ```

   On Windows, run `gradlew.bat run` instead.

## Use commands

Enter commands in the text field at the bottom of the Pulbot window.

| Command | Description | Example |
| --- | --- | --- |
| `todo DESCRIPTION` | Add a task without a date. | `todo read chapter 3` |
| `deadline DESCRIPTION /by DATE_TIME` | Add a task with a deadline. | `deadline submit report /by 18/9/2026 2359` |
| `event DESCRIPTION /from DATE_TIME /to DATE_TIME` | Add an event with a start and end time. | `event tutorial /from 18/9/2026 1000 /to 18/9/2026 1100` |
| `list` | Display every task. | `list` |
| `find KEYWORD` | Find tasks whose descriptions contain a keyword. | `find report` |
| `on DATE` | Display deadlines and events occurring on a date. | `on 18/9/2026` |
| `mark NUMBER` | Mark a task as complete. | `mark 2` |
| `unmark NUMBER` | Mark a task as incomplete. | `unmark 2` |
| `delete NUMBER` | Delete a task. | `delete 2` |
| `bye` | Close Pulbot. | `bye` |

Use `d/M/yyyy HHmm` for date-times and `d/M/yyyy` for dates. Pulbot interprets
times using the computer's local time zone.

### Duplicate tasks

Pulbot rejects a new task when an existing task has the same type, description,
and date-time details. Description comparison ignores capitalization and
surrounding spaces. Completion status doesn't make a task unique.

For example, if `todo read book` already exists, Pulbot rejects
`todo READ BOOK` with this message:

```text
This task already exists in your list.
```

## Build a runnable JAR

Create a JAR that includes the JavaFX dependencies:

```shell
./gradlew shadowJar
```

Gradle creates `build/libs/Pulbot.jar`. Run it with JDK 25:

```shell
java -jar build/libs/Pulbot.jar
```

## Run development checks

Run the automated tests and coding-standard checks before submitting a change:

```shell
./gradlew test checkstyleMain checkstyleTest
```

Run every verification task and rebuild the application with this command:

```shell
./gradlew build shadowJar
```

## Project structure

```text
src/main/java/pulbot/
├── command/    Command parsing and execution
├── storage/    File loading and saving
├── task/       Task types and task-list operations
├── ui/         Console presentation
└── *.java      Application and JavaFX controllers
```

Tests follow the same package structure under `src/test/java/pulbot/`.

## Data storage

Pulbot creates `data/pulbot.txt` when it first saves a task. The application
manages this file automatically. Closing and reopening Pulbot reloads the saved
tasks.

If saved data cannot be loaded, Pulbot reports a warning and blocks task changes to protect the original file. Back up and repair or move `data/pulbot.txt`, then restart Pulbot. If a save fails, the command reports an error and restores the in-memory task list.

## Acknowledgments

* This project is based on the [NUS CS2103/T iP starter template](https://github.com/NUS-CS2103-AY2627-S1/ip).
* The JavaFX launcher, FXML controllers, and dialog layout adapt the [SE-EDU JavaFX tutorial](https://se-education.org/guides/tutorials/javaFx.html).
