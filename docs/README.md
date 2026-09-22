# Friday User Guide

Friday is a task-management chatbot written in Java. It keeps track of todos,
deadlines, and events through a graphical chat interface or a console interface.

## Current features

- Add todos, deadlines, and events.
- List all tasks and their completion status.
- Mark tasks as done or not done.
- Delete tasks.
- Find tasks by text in their descriptions.
- Find deadlines due on a specific date.
- Sort tasks alphabetically or group them by type.
- Save tasks automatically and load them again the next time Friday starts.

## Requirements

- JDK 25
- IntelliJ IDEA (recommended) or a terminal that can run Gradle

## Running Friday

### IntelliJ IDEA

1. Open this project directory in IntelliJ IDEA.
2. Configure the project SDK as **JDK 25** and leave the project language level
   as **SDK default**. See IntelliJ's
   [JDK setup guide](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk) if
   needed.
3. Open `src/main/java/friday/Launcher.java`, right-click the file, and run
   `Launcher.main()` to open the chat window. To use the console interface,
   run `Friday.main()` in `src/main/java/friday/Friday.java` instead.

### Terminal

On macOS with SDKMAN, select the required Java version first:

```shell
sdk use java 25.0.3.fx-zulu
./gradlew run
```

This opens the chat window. Type one command into the input box and press
**Enter** or click **Send**. In the console interface, type a command and press
**Enter**. Enter `bye` to exit.

### Try your first task

1. Enter `todo read chapter 3` to add a task.
2. Enter `list` to see its task number and completion status.
3. Enter `mark NUMBER`, replacing `NUMBER` with that task's number, to complete it.
4. Enter `list` again: the task now has an `[X]` marker.
5. Enter `bye` to close Friday.

## Commands

Use lowercase command words. Replace uppercase placeholders such as
`DESCRIPTION` and `NUMBER` with your own values; do not type the placeholders.
Descriptions may contain spaces and must not be empty.

| Command | Description | Example |
| --- | --- | --- |
| `todo DESCRIPTION` | Add a todo | `todo read chapter 3` |
| `deadline DESCRIPTION /by YYYY-MM-DD` | Add a deadline | `deadline submit report /by 2026-08-30` |
| `event DESCRIPTION /from START /to END` | Add an event | `event project meeting /from 2pm /to 3pm` |
| `list` | Show every task | `list` |
| `sort` | Sort tasks alphabetically (A-Z) | `sort` |
| `sort reverse` | Sort tasks in reverse alphabetical order (Z-A) | `sort reverse` |
| `sort type` | Group tasks by task type (Deadline, Event, Todo) | `sort type` |
| `mark NUMBER` | Mark a task as done | `mark 1` |
| `unmark NUMBER` | Mark a task as not done | `unmark 1` |
| `delete NUMBER` | Delete a task | `delete 1` |
| `find KEYWORD` | Find tasks whose descriptions contain the supplied text (case-sensitive) | `find report` |
| `on YYYY-MM-DD` | Show deadlines due on a date | `on 2026-08-30` |
| `bye` | Exit (also saves tasks in console mode) | `bye` |

### Reading and updating tasks

Task numbers start at 1. Use the numbers from `list` with `mark`, `unmark`, and
`delete`. Run `list` again after sorting or deleting, because task numbers can
change. `find` numbers its matches separately, so use `list` to check the task's
number before updating it. `on` retains the full list's task numbers.

Task markers are `[T]` for todos, `[D]` for deadlines, and `[E]` for events.
`[ ]` means incomplete and `[X]` means complete. For example,
`1.[T][X] read chapter 3` is a completed todo. `unmark 1` makes it incomplete
again; `delete 1` removes it from the list.

### Dates, event times, searching, and sorting

- Deadline dates and `on` dates must be valid dates in `YYYY-MM-DD` format.
  `on` searches deadlines only, including completed deadlines.
- Events require `/from` followed by `/to`, each exactly once. Use matching
  date/time formats for start and end, with the end later than the start:
  `14:00` and `15:00`, `2pm` and `3pm`, `2026-09-14` and `2026-09-15`, or
  `2026-09-14T14:00` and `2026-09-14T15:00`. Free-text labels such as
  `Monday morning` and `Monday afternoon` are also accepted, but Friday cannot
  check their chronological order.
- `find` searches descriptions for the exact text supplied, including phrases.
  For example, `find read chapter` matches `read chapter 3`, while `find Read`
  does not. An empty search is rejected.
- `sort` and `sort reverse` sort descriptions without regard to letter case.
  `sort type` groups deadlines, events, and todos in that order. Sorting changes
  the stored task order; enter `list` to see the result.
- Duplicate tasks with the same type and details are rejected, even if the
  existing task is complete. If Friday reports invalid input, correct the
  command using the formats above and submit it again.

## Saving your tasks

In the graphical interface, Friday saves after each command other than `bye`.
In the console interface, enter `bye` to save before exiting.
Tasks load automatically when Friday starts.

When running from the project directory, the default save file is
`data/tasks.txt`. The packaged macOS app uses `~/.friday/tasks.txt` by default.
If Friday reports that it could not save, your latest changes may not persist.
If it reports malformed or duplicate saved records, saving is disabled to
protect the original file; back up and repair that file before restarting.

## Testing and building

Run the automated tests with Java 25:

```shell
sdk use java 25.0.3.fx-zulu
./gradlew test
```

Create the executable JAR with:

```shell
./gradlew shadowJar
java -jar build/libs/friday.jar
```

The project uses Gradle, JUnit 5, and the Shadow plugin. Java source files remain
under `src/main/java`, while tests are under `src/test/java`.
