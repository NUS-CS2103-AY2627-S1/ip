# Ada User Guide

Ada is a desktop task-management chatbot that helps you keep track of todos, deadlines, and events in one organised list.

## Overview

People often have many different responsibilities at the same time. When tasks are scattered across notes, messages, and memory, it is easy to forget an important task or remember the wrong deadline. Ada was designed as a simple, chat-based todo list to reduce this problem.

With Ada, you can:

* record ordinary todos, deadlines, and events;
* mark tasks as completed or reopen them later;
* remove tasks that are no longer needed;
* search for tasks by keyword;
* view and sort your task list; 
* keep your task list between sessions using local file storage.

## Setup

### Prerequisites

* JDK 25
* IntelliJ IDEA (recommended) or another Java-compatible IDE
* A terminal, if you want to run the project with Gradle

### Running the application with Gradle

Open a terminal in the project root directory and run:

```bash
./gradlew run
```

On Windows, use:

```bat
gradlew.bat run
```

The Ada JavaFX window will open. Enter a command in the input box and submit it to receive Ada's response.

### Running the application with IntelliJ IDEA

1. Open the project directory in IntelliJ IDEA.
2. Configure the project SDK to use **JDK 25**.
3. Run the `ada.Launcher` class.

The project uses the Gradle wrapper and JavaFX dependencies supplied by the build configuration. If IntelliJ displays compilation errors after changing the JDK, reload the Gradle project and rebuild it.

### Running the tests

To run the automated tests, use:

```bash
./gradlew test
```

### Task persistence

Ada stores tasks in `data/Ada.txt`. Tasks are loaded when the application starts, and changes made by commands such as `todo`, `deadline`, `event`, `mark`, `unmark`, `delete`, and `sort` are saved automatically.

## Command Overview

| Command    | Purpose                                | Input format                                  |
| ---------- | -------------------------------------- | --------------------------------------------- |
| `todo`     | Add an ordinary task                   | `todo <description>`                          |
| `deadline` | Add a task with a due date or time     | `deadline <description> /by <date/time>`      |
| `event`    | Add an event with a start and end time | `event <description> /from <start> /to <end>` |
| `delete`   | Remove a task                          | `delete <task number>`                        |
| `mark`     | Mark a task as completed               | `mark <task number>`                          |
| `unmark`   | Mark a completed task as unfinished    | `unmark <task number>`                        |
| `find`     | Search for tasks containing a keyword  | `find <keyword>`                              |
| `list`     | Display all tasks                      | `list`                                        |
| `sort`     | Sort the task list                     | `sort`                                        |
| `bye`      | Exit Ada                               | `bye`                                         |

In the formats below, angle brackets indicate a value that you should replace. Do not type the angle brackets themselves. Commands should be entered in lowercase.

## Adding Tasks

Ada supports three task types. Use the task-type command directly; there is no separate `add` prefix.

### Add an ordinary task with `todo`

Use `todo` for a task that does not need a deadline or a time range.

**Format**

```text
todo <description>
```

**Example**

```text
todo finish reading
```

Ada adds the task as an unfinished todo task and displays it with the `[T]` type label.

### Add a deadline with `deadline`

Use `deadline` for a task that must be completed by a particular date or time. The `/by` marker is required.

**Format**

```text
deadline <description> /by <date or date-time>
```

**Examples**

```text
deadline submit project report /by 2026-09-30 1800
deadline pay subscription /by 2026-10-01
```

If only a date is provided, Ada uses `23:59` as the default time for that deadline.

### Add an event with `event`

Use `event` for an activity with a starting time and an ending time. Both `/from` and `/to` markers are required, and they must appear in that order.

**Format**

```text
event <description> /from <start> /to <end>
```

**Examples**

```text
event project meeting /from 2026-09-18 1400 /to 2026-09-18 1530
event lunch with team /from 2026-09-19 1200 /to 1300
```

For the second form, an `HHmm` end time uses the same date as the start time. Choose an end time that is later than the start time.

## Managing Tasks

Task numbers are shown by the `list` command and start from `1`.

### Delete a task with `delete`

Use `delete` to permanently remove a task from the current task list.

**Format**

```text
delete <task number>
```

**Example**

```text
delete 3
```

Ada removes the selected task and saves the updated task list.

### Mark a task as completed with `mark`

Use `mark` when a task has been completed.

**Format**

```text
mark <task number>
```

**Example**

```text
mark 1
```

The task is displayed with an `[X]` completion indicator.

### Reopen a task with `unmark`

Use `unmark` to change a completed task back to unfinished.

**Format**

```text
unmark <task number>
```

**Example**

```text
unmark 1
```

The task is displayed with an empty `[ ]` completion indicator.

## Viewing and Organising Tasks

### Display all tasks with `list`

Use `list` to display every task currently stored by Ada.

**Format**

```text
list
```

The result includes the task number, task type, completion status, and any deadline or event time information.

### Search for tasks with `find`

Use `find` to search task descriptions for a keyword. The search is case-insensitive and matches the keyword anywhere in the description.

**Format**

```text
find <keyword>
```

**Examples**

```text
find report
find project meeting
```

If no task description contains the keyword, Ada reports that no matching tasks were found. Search results are displayed as their own numbered list; use `list` to confirm a task's current list number before using `mark`, `unmark`, or `delete`.

### Sort tasks with `sort`

Use `sort` to organise the entire task list into a consistent order.

**Format**

```text
sort
```

Ada sorts tasks using these rules:

1. Unfinished tasks appear before completed tasks.
2. Within each completion group, task types appear in this order: `deadline`, `event`, then `todo`.
3. Deadlines are ordered by due date and time.
4. Events are ordered by start date and time.
5. Tasks with otherwise equal sorting values are ordered by description, without distinguishing between uppercase and lowercase letters.

Sorting also saves the new order to `data/Ada.txt`.

## Date and Time Formats

Ada accepts the following formats for deadline and event inputs:

| Format            | Example           | Where it can be used                                      |
| ----------------- | ----------------- | --------------------------------------------------------- |
| `yyyy-MM-dd`      | `2026-09-30`      | A deadline date or an event `/from` value                 |
| `yyyy-MM-dd HHmm` | `2026-09-30 1800` | A deadline date-time, event `/from`, or event `/to` value |
| `HHmm`            | `1800`            | An event `/to` value only; uses the `/from` date          |

Times use the 24-hour clock and do not include a colon. For example, `0930` means 9:30 AM and `1800` means 6:00 PM.

When Ada displays a date or time, it uses a more readable format such as `Sep 30 2026 6:00 PM`.

## Exiting Ada

Use `bye` to close the interaction.

**Format**

```text
bye
```

Ada displays a goodbye message and disables further input in the application window.

## Common Input Rules

* Use a valid existing task number for `mark`, `unmark`, and `delete`.
* Include `/by` in every `deadline` command.
* Include both `/from` and `/to` in every `event` command.
* Keep the markers in the required order: `/from` before `/to`.
* Provide a non-empty description for every task.
* Use the date and time formats documented above.
* If a command is not recognised or its format is invalid, Ada displays an error message and keeps the current task list unchanged.
