# Doge

> Much task management. Very organised.

Doge is a lightweight task-management application for organising todos, deadlines,
and events. It combines a friendly JavaFX graphical interface with fast text commands
for users who prefer typing.

## Quick start

### Prerequisites

- Java Development Kit (JDK) 25
- IntelliJ IDEA, if you want to run the project from an IDE

### Download Jar file from latest release

From jar file location run:

```bash
java -jar "doge.jar"
```

The Doge window opens with a command box at the bottom. Enter a command and press
`Enter` or click **Fetch**.

## Getting started

Try these commands in order:

```text
todo Prepare product launch presentation
deadline Submit project proposal /by 20/9/2026 1800
event Product demo rehearsal /from 21/9/2026 1400 /to 21/9/2026 1530
priority 2 1
mark 1
list
```

These commands create different task types, assign a priority, complete a task,
and display the complete task list.

## Features

### Command format

- Words in `UPPER_CASE` are values that you supply.
- `TASK_NUMBER` refers to the number shown by `list`.
- Date and time values use the format `d/M/yyyy HHmm`.
- Text commands are case-sensitive and should be entered in lowercase.

### Adding a todo: `todo`

Adds a task without a date or time.

Format:

```text
todo DESCRIPTION
```

Example:

```text
todo Prepare slides for Monday's meeting
```

### Adding a deadline: `deadline`

Adds a task that must be completed by a specified date and time.

Format:

```text
deadline DESCRIPTION /by DATE TIME
```

Example:

```text
deadline Submit expense report /by 25/9/2026 1700
```

### Adding an event: `event`

Adds a task with a start and end time.

Format:

```text
event DESCRIPTION /from START_DATE START_TIME /to END_DATE END_TIME
```

Example:

```text
event Team planning session /from 28/9/2026 1000 /to 28/9/2026 1130
```

### Listing tasks: `list`

Displays all tasks in the order they were added.

```text
list
```

Tasks are displayed with their type, completion status, priority, and any relevant
date or time information:

```text
1.[T][ ] Prepare slides for Monday's meeting
2.[D][ ] [P1] Submit expense report (by: Sep 25 2026, 5:00 PM)
3.[E][X] Team planning session (from: Sep 28 2026, 10:00 AM to: Sep 28 2026, 11:30 AM)
```

### Marking a task complete: `mark`

Marks a task as completed.

Format:

```text
mark TASK_NUMBER
```

Example:

```text
mark 2
```

### Marking a task incomplete: `unmark`

Marks a previously completed task as incomplete.

```text
unmark TASK_NUMBER
```

### Setting a priority: `priority`

Assigns a positive priority level to a task. If no level is provided, the priority
defaults to level 1.

Formats:

```text
priority TASK_NUMBER
priority TASK_NUMBER LEVEL
```

Examples:

```text
priority 1
priority 3 2
```

### Removing a priority: `unpriority`

Removes the priority from a task.

```text
unpriority TASK_NUMBER
```

### Finding tasks: `find`

Finds tasks whose descriptions contain the specified keyword. Searches are
case-insensitive.

```text
find KEYWORD
```

Example:

```text
find meeting
```

### Deleting a task: `delete`

Deletes the task at the specified number.

```text
delete TASK_NUMBER
```

Example:

```text
delete 4
```

### Exiting the application: `bye`

Closes Doge and saves the current task list.

```text
bye
```

## Command summary

| Action | Format | Example |
| --- | --- | --- |
| Add todo | `todo DESCRIPTION` | `todo Read project brief` |
| Add deadline | `deadline DESCRIPTION /by DATE TIME` | `deadline Send report /by 30/9/2026 1800` |
| Add event | `event DESCRIPTION /from START /to END` | `event Demo /from 1/10/2026 1400 /to 1/10/2026 1500` |
| List tasks | `list` | `list` |
| Mark done | `mark TASK_NUMBER` | `mark 1` |
| Mark undone | `unmark TASK_NUMBER` | `unmark 1` |
| Set priority | `priority TASK_NUMBER [LEVEL]` | `priority 2 1` |
| Remove priority | `unpriority TASK_NUMBER` | `unpriority 2` |
| Find tasks | `find KEYWORD` | `find report` |
| Delete task | `delete TASK_NUMBER` | `delete 3` |
| Exit | `bye` | `bye` |

## Saving your tasks

Doge stores tasks locally in:

```text
data/tasks.txt
```

Tasks are saved when you enter `bye` or close the application window. The data file
is created automatically when the first save occurs.

## Troubleshooting

### The application does not start

Check that Java 25 is selected:

```bash
java -version
```

If you are using IntelliJ IDEA, also check **File > Project Structure > Project SDK**.

### A command is rejected

Check the command format and make sure that:

- the command keyword is lowercase;
- task numbers refer to an existing task;
- deadline and event dates use `d/M/yyyy HHmm`;
- events include both `/from` and `/to`.

## Development

Run the test suite with:

```bash
./gradlew test
```

Build the executable JAR with:

```bash
./gradlew shadowJar
```

The generated JAR is placed in `build/libs/doge.jar`.

## Acknowledgements

The organisation of this guide is inspired by the [AddressBook Level 3 User Guide](https://se-education.org/addressbook-level3/UserGuide.html), adapted for Doge's task-management features and command syntax.
