# Zabud

Zabud is a JavaFX chatbot for managing to-dos, deadlines, and events. Tasks and command history are saved automatically between sessions.

## Requirements

- JDK 25

Confirm that Java 25 is active:

```bash
java -version
javac -version
```

## Build and run

From the project root, build and start the JavaFX application:

```bash
./gradlew run
```

To create and run the cross-platform executable JAR instead:

```bash
./gradlew clean shadowJar
java -jar build/libs/zabud.jar
```

Enter `help` at any time to display the available commands and input requirements.

## Test

Run the JUnit suite and both course Checkstyle tasks:

```bash
./gradlew test checkstyleMain checkstyleTest
```

## Commands

| Command | Description |
| --- | --- |
| `todo DESCRIPTION` | Adds a task without a date or time. |
| `deadline DESCRIPTION /by DATE_OR_TIME` | Adds a task with a deadline. |
| `event DESCRIPTION /from DATE_OR_TIME /to DATE_OR_TIME` | Adds an event with a start and end. |
| `list` | Displays all tasks. |
| `find KEYWORD` | Finds tasks by case-insensitive partial description. |
| `mark TASK_NUMBER` | Marks an existing task as completed. |
| `unmark TASK_NUMBER` | Marks an existing task as incomplete. |
| `delete TASK_NUMBER` | Deletes an existing task. |
| `help` | Displays command syntax and input requirements. |
| `bye` | Exits Zabud. |

`DESCRIPTION` must contain at least one non-space character. `TASK_NUMBER` is the number shown beside an existing task in the task list.
For events, `/to` must be later than `/from`; both endpoints must either include dates or both be time-only.

## Dates and times

`DATE_OR_TIME` accepts a date, a 24-hour time, or both separated by a space:

| Input | Meaning |
| --- | --- |
| `2/12/2019` | 2 December 2019 |
| `1800` | 6:00 PM |
| `2/12/2019 1800` | 2 December 2019 at 6:00 PM |

Dates use day/month/year order. Times use the 24-hour clock without a colon.

Examples:

```text
deadline return book /by 2/12/2019 1800
event project meeting /from 3/12/2019 1400 /to 3/12/2019 1600
```

## Command parsing

`Parser` converts each input line into an ordered list of immutable `ParsedToken` values. The first
word is stored as `command`, text before the first slash-prefixed token is stored as `default`, and
each `/name` introduces another named value. For example:

```text
deadline test /by 1200
```

is parsed as:

```text
(command, deadline), (default, test), (by, 1200)
```

Every type in `commands.impl` implements `Validatable` and `Buildable`. The parser uses the
`command` value to select the corresponding command handler, then removes the command entry. It
calls `isValid()` on that handler before construction. Valid input is passed to `build()` and the
returned command is executed; invalid input prints the same handler's `hint()` without calling
`build()`. Plain values such as `default` are handled directly, while typed values such as `/by`,
`/from`, and `/to` are checked by `DateTimeToken`.

## Command history

Zabud retains the latest 1000 non-empty commands:

- Press Up to recall an older command.
- Press Down to move toward a newer command.
- Press Escape to clear the current input and reset history navigation. The next Up press recalls the latest command.

Command history is restored the next time Zabud starts.

## Persistent data

Tasks, completion status, dates, times, and command history are saved automatically to `data/zabud.txt`. The directory and file are created when session data is first saved.

A missing or unreadable data file is treated as an empty session. The storage file is managed by Zabud and should not normally be edited manually.
