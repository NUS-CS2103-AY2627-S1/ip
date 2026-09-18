# MEKA

MEKA is a JavaFX task chatbot written in Java. It keeps track of todos,
deadlines, and events, remembers whether tasks are complete, and restores saved
tasks the next time it starts. It can also archive the entire active task list
when the user wants a clean slate. The original console interface remains
available for automated testing.

### Level 10: Graphical user interface

MEKA provides a chat-style JavaFX interface. Type a command into the input box
and press Enter or select **Send**. Messages scroll automatically, and entering
`bye` displays MEKA's farewell before closing the window.

## Features by level

### Level 1: Chatbot interaction

MEKA greets the user when it starts and ends the conversation when the user
enters `bye`.

```text
bye
```

### Level 2: Todo tasks

Create a task that has no attached date or time:

```text
todo borrow book
```

MEKA displays todos with the `[T]` task-type marker and `[ ]` completion marker.

### Level 3: Task list

MEKA stores multiple tasks during a session. Enter `list` to display every task
with its one-based task number:

```text
list
```

```text
1. [T][ ] borrow book
2. [T][ ] read lecture notes
```

### Level 4: Task status

Mark a task as complete or incomplete by referring to its list number:

```text
mark 1
unmark 1
```

Completed tasks use `[X]`; incomplete tasks use `[ ]`.

### Level 5: Deadlines and events

Create a deadline with `/by`:

```text
deadline return book /by 2/12/2019 1800
```

Create an event with `/from` and `/to`:

```text
event project meeting /from 3/12/2019 0900 /to 3/12/2019 1100
```

Deadlines use `[D]`, while events use `[E]`.

### Level 6: Delete tasks

Delete a task by its list number:

```text
delete 2
```

MEKA confirms the removed task and reports the new number of tasks.

### Level 7: Validation and persistence

MEKA reports friendly errors for unknown commands, missing descriptions,
missing or non-numeric task numbers, invalid task numbers, missing date-times,
and malformed saved data. Invalid commands do not add or alter tasks.

Tasks are saved automatically to `data/meka.txt` after a successful change.
The file records each task's type, completion state, description, and any ISO
date-time values. MEKA loads these tasks automatically on its next run.

Archived tasks are stored separately in `data/meka-archive.txt` and are not
loaded back into the active task list.

### Level 8: Date and time

Deadline and event values are stored as `java.time.LocalDateTime`, not plain
strings. Enter date-times using `d/M/yyyy HHmm`, where `HHmm` is 24-hour time:

```text
2/12/2019 1800
```

MEKA understands this as 2 December 2019 at 6:00 PM and displays it as:

```text
Dec 02 2019, 6:00 PM
```

Invalid values such as `29/2/2019 1800` or `2/12/2019 2500` are rejected.
Saved date-times use the ISO form `2019-12-02T18:00` for reliable parsing.

### Level 9: Find tasks

Find tasks whose descriptions contain a keyword:

```text
find book
```

The search ignores letter case and numbers only the matching tasks:

```text
Here are the matching tasks in your list:
1. [T][X] read book
2. [D][X] return book (by: Jun 06 2019, 6:00 PM)
```

### Extension: Archive all tasks

Move every active task into a separate archive and start with an empty active
list:

```text
archive all
```

MEKA appends the tasks to `data/meka-archive.txt` in their current order,
including each task's completion state. It then clears `data/meka.txt` and
reports how many tasks were archived:

```text
Noted. I've archived all 3 tasks.
Now you have 0 tasks in the list.
```

If there are no active tasks, MEKA reports `There are no tasks to archive.`
Archived entries are retained as records; the app does not currently provide
commands to list, restore, or delete them.

## Command summary

| Command | Purpose |
| --- | --- |
| `todo DESCRIPTION` | Add a todo task |
| `deadline DESCRIPTION /by DATE_TIME` | Add a deadline |
| `event DESCRIPTION /from DATE_TIME /to DATE_TIME` | Add an event |
| `list` | Show all tasks |
| `find KEYWORD` | Show tasks whose descriptions contain a keyword |
| `mark NUMBER` | Mark a task as complete |
| `unmark NUMBER` | Mark a task as incomplete |
| `delete NUMBER` | Delete a task |
| `archive all` | Archive every active task and clear the active list |
| `bye` | Exit MEKA |

For deadline and event commands, replace `DATE_TIME` with a value such as
`2/12/2019 1800`.

## Requirements

- Java Development Kit (JDK) 25
- IntelliJ IDEA, or a terminal that can run Gradle

## Running in IntelliJ IDEA

1. Open this repository as an IntelliJ IDEA project.
2. Configure the Project SDK as JDK 25 and leave the language level as
   `SDK default`.
3. Open `src/main/java/meka/gui/Launcher.java`.
4. Run `Launcher.main()`.

## Running from a terminal

Use Java 25, then run the Gradle wrapper from the repository root. On Windows,
replace `./gradlew` with `./gradlew.bat`.

```shell
./gradlew run --console=plain
./gradlew test
```

The Gradle `run` task starts the JavaFX interface. To run the console interface,
run `meka.Meka` directly from IntelliJ IDEA.

## Building the executable JAR

```shell
./gradlew shadowJar
java -jar build/libs/meka.jar
```

See the [MEKA User Guide](docs/README.md) for complete command examples,
validation rules, archive behavior, and recovery guidance.

## Acknowledgements

Isaac Tay ([@isaactay9856](https://github.com/isaactay9856)) used OpenAI Codex to assist with MEKA's development, which includes code review and test verification.
