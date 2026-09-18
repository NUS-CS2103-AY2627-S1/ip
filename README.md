# RemBot

RemBot is a desktop task manager with a chat-style interface. Its assistant, Rem, can track todos,
deadlines, events, and notes using short text commands.

![RemBot graphical interface](docs/Ui.png)

## Features

- Create todos, deadlines, and events.
- Mark, unmark, and delete tasks.
- Attach notes to tasks.
- Search by keyword or view scheduled tasks by date.
- Recall commands with the Up and Down arrow keys.
- Save tasks automatically between sessions.
- Recover from malformed saved data without overwriting the original file.

See the [User Guide](docs/README.md) for the full command reference and examples.

## Running RemBot

RemBot requires Java 25.

Download `rembot.jar` from the [latest release](https://github.com/dygrik/ip/releases/latest), open a
terminal in the download folder, and run:

```text
java -jar rembot.jar
```

The bundled JavaFX libraries support Windows, macOS, and Linux on x64 systems. Other architectures
need matching JavaFX native libraries.

## Quick start

Enter commands in the text box and press Enter or select **Send**. For example:

```text
todo read book
deadline submit report /by 2026-10-01 1800
event project meeting /from 2026-10-02 1400 /to 2026-10-02 1500
list
mark 1
find report
on 2026-10-02
bye
```

The main commands are:

| Command | Purpose |
| --- | --- |
| `todo DESCRIPTION` | Add a todo |
| `deadline DESCRIPTION /by DATE [TIME]` | Add a deadline |
| `event DESCRIPTION /from DATE [TIME] /to DATE [TIME]` | Add an event |
| `list` | Show all tasks |
| `mark NUMBER` / `unmark NUMBER` | Change a task's completion status |
| `delete NUMBER` | Delete a task |
| `note NUMBER NOTE` / `deletenote NUMBER` | Add, replace, or remove a note |
| `find KEYWORD` | Search task descriptions and notes |
| `on DATE` | Show deadlines and events scheduled on a date |
| `bye` | Close RemBot |

Add `/note NOTE` to a `todo`, `deadline`, or `event` command to create the task with a note.

## Saved data

RemBot stores tasks in `data/rem.txt`, relative to the folder from which it is launched. If that file
contains malformed data, RemBot offers to start with an empty list or exit. Choosing **Start Fresh**
first copies the damaged data to a timestamped backup in the same folder.

## Running from source

Clone the repository and open its root directory in a terminal. On Windows, use:

```powershell
.\gradlew.bat run
```

On macOS or Linux, use:

```bash
./gradlew run
```

To run RemBot from IntelliJ IDEA, import the project as a Gradle project, select JDK 25, refresh
Gradle, and run `rem.Launcher`.

## Building and testing

Run all automated tests and code-style checks:

```powershell
.\gradlew.bat check
```

Build the runnable JAR:

```powershell
.\gradlew.bat shadowJar
```

The JAR is written to `build/libs/rembot.jar`. Use `./gradlew` instead of `./gradlew.bat` on macOS
or Linux.

The console interface is also available for development and scripted testing:

```powershell
.\gradlew.bat runConsole
```
