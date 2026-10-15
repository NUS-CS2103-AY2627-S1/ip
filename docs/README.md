# Apollo User Guide

## About Apollo

**Apollo** is a desktop task-management chatbot that helps you record, organise, search, and review your tasks through simple text commands.

The program is named after Apollo, the Greek god associated with light and prophecy. Like an oracle bringing hidden matters into the light, Apollo reveals your upcoming tasks and helps bring order to your schedule. Its responses reflect this divine personality while still explaining how to correct invalid commands.

## Quick start

Apollo requires **Java 25**.

To run a downloaded copy of the application, place `apollo.jar` in a folder of your choice and run:

```shell
java -jar apollo.jar
```

To run Apollo from the project source on macOS or Linux:

```shell
./gradlew run
```

On Windows, run:

```shell
gradlew.bat run
```

To build the executable JAR from the project source:

```shell
./gradlew shadowJar
```

The generated file can be found at `build/libs/apollo.jar`.

## Command summary

Commands are case-insensitive. Words written in uppercase in the formats below, such as `DESCRIPTION`, represent values that you must supply.

| Commands | Use format | What it does |
| --- | --- | --- |
| [`todo`](#adding-a-todo) | `todo DESCRIPTION` | Adds a task without a date or time. |
| [`deadline`](#adding-a-deadline) | `deadline DESCRIPTION /by DATE_TIME` | Adds a task that must be completed by a deadline. |
| [`event`](#adding-an-event) | `event DESCRIPTION /from DATE_TIME /to DATE_TIME` | Adds an event with a starting and ending time. |
| [`list`](#listing-all-tasks) | `list` | Displays every saved task and its task number. |
| [`mark`](#marking-a-task-as-complete) | `mark TASK_NUMBER` | Marks a task as complete. |
| [`unmark`](#marking-a-task-as-incomplete) | `unmark TASK_NUMBER` | Marks a completed task as incomplete. |
| [`delete`](#deleting-a-task) | `delete TASK_NUMBER` | Removes a task from the list. |
| [`find`](#finding-tasks) | `find SEARCH_TEXT` | Finds tasks whose descriptions contain the search text. |
| [`undo`](#undoing-a-change) | `undo` | Reverses the most recent supported change. |
| [`duetoday`](#viewing-deadlines-due-today) | `duetoday` | Displays deadlines due today. |
| [`duethisdate`](#viewing-deadlines-due-on-a-date) | `duethisdate DATE` | Displays deadlines due on a specified date. |
| [`ongoingnow`](#viewing-ongoing-events) | `ongoingnow` | Displays events taking place now. |
| [`bye`](#exiting-apollo) | `bye` | Ends the current Apollo session. |

## Date and time formats

Apollo accepts dates and times in the following formats:

| Value | Format | Example |
| --- | --- | --- |
| Date | `d/M/yyyy` | `25/8/2026` |
| Date and time | `d/M/yyyy HHmm` | `25/8/2026 1830` |

Times use the 24-hour clock. For example, `0900` means 9:00 AM and `1830` means 6:30 PM.

When a time is omitted:

- A deadline uses `2359` on the specified date.
- An event start uses `0000` on the specified date.
- An event end uses `2359` on the specified date.

Apollo validates calendar dates strictly, so impossible values such as `31/4/2026` are rejected.

## Understanding task displays

Each task begins with indicators showing its type and completion state.

| Indicator | Meaning |
| --- | --- |
| `[T]` | Todo |
| `[D]` | Deadline |
| `[E]` | Event |
| `[ ]` | Incomplete |
| `[X]` | Complete |

For example:

```text
[D] [X] submit report (by: Aug 25 2026, 6:00PM)
```

This represents a completed deadline.

## Adding a todo

Use `todo` for a task that does not need a date or time.

**Format:**

```text
todo DESCRIPTION
```

**Example:**

```text
todo read the software engineering textbook
```

Apollo adds the todo as an incomplete task and saves it automatically. The description cannot be empty.

## Adding a deadline

Use `deadline` for a task that must be completed by a particular date or time.

**Format:**

```text
deadline DESCRIPTION /by DATE_TIME
```

**Examples:**

```text
deadline submit project report /by 25/8/2026 1800
deadline renew library book /by 30/8/2026
```

If no time is given, Apollo sets the deadline to `2359` on that date. Both the description and `/by` date are required.

## Adding an event

Use `event` for an activity that takes place between a starting and ending date or time.

**Format:**

```text
event DESCRIPTION /from DATE_TIME /to DATE_TIME
```

**Examples:**

```text
event project meeting /from 25/8/2026 1400 /to 25/8/2026 1530
event orientation camp /from 25/8/2026 /to 27/8/2026
```

The description, `/from` value, and `/to` value are required. The event cannot end before it starts.

## Listing all tasks

Use `list` to display every task in the order it was added.

**Format:**

```text
list
```

Apollo assigns a number to each listed task. Use these numbers with commands such as `mark`, `unmark`, and `delete`.

> [!IMPORTANT]
> Use task numbers from the full `list` output. Numbers shown by filtered commands such as `find` and `duethisdate` refer only to the displayed results and may not match the task's number in the full list.

## Marking a task as complete

Use `mark` with a task number from `list`.

**Format:**

```text
mark TASK_NUMBER
```

**Example:**

```text
mark 2
```

Apollo changes the task's completion indicator from `[ ]` to `[X]` and saves the change.

## Marking a task as incomplete

Use `unmark` to return a completed task to its incomplete state.

**Format:**

```text
unmark TASK_NUMBER
```

**Example:**

```text
unmark 2
```

Apollo changes the task's completion indicator from `[X]` to `[ ]` and saves the change.

## Deleting a task

Use `delete` with a task number from `list`.

**Format:**

```text
delete TASK_NUMBER
```

**Example:**

```text
delete 3
```

Apollo removes the selected task, displays how many tasks remain, and saves the updated list. The deletion can be reversed with `undo` while it remains in the undo history.

## Finding tasks

Use `find` to search task descriptions.

**Format:**

```text
find SEARCH_TEXT
```

**Example:**

```text
find project
```

The search is case-insensitive and matches text appearing anywhere in a description. It searches todos, deadlines, and events while preserving their original order.

## Undoing a change

Use `undo` to reverse the most recent supported change from the current session.

**Format:**

```text
undo
```

Apollo can undo:

- Adding a task
- Deleting a task
- Marking a task
- Unmarking a task

Apollo remembers only the **five most recent state-changing actions**. Undo history is not restored after the application is closed and reopened. The result of an undo is saved to storage.

## Viewing deadlines due today

Use `duetoday` to display deadlines whose due date matches the current system date.

**Format:**

```text
duetoday
```

Todos, events, and deadlines on other dates are excluded from the results.

## Viewing deadlines due on a date

Use `duethisdate` to display deadlines due on a specified date.

**Format:**

```text
duethisdate DATE
```

**Example:**

```text
duethisdate 25/8/2026
```

The supplied value must contain a date without a time.

## Viewing ongoing events

Use `ongoingnow` to display events whose time range includes the current date and time.

**Format:**

```text
ongoingnow
```

An event is considered ongoing at its exact start and end times. Todos, deadlines, past events, and future events are excluded.

## Exiting Apollo

Use `bye` when you have finished using Apollo.

**Format:**

```text
bye
```

Apollo displays a farewell message. In the GUI, further input is disabled and the application closes after five seconds so that the farewell can be read.

## Data storage

Apollo automatically stores tasks in `data/apollo.txt`. Changes made by adding, marking, unmarking, deleting, or undoing a task are saved immediately.

If Apollo detects malformed task data while starting, it displays an error and resets the storage file so that the application can continue with an empty task list.

> [!WARNING]
> Resetting malformed storage removes the tasks previously contained in that file.

The vertical bar character (`|`) is reserved as Apollo's storage separator and should not be used in task descriptions.

## Example interaction

The following sequence demonstrates a typical session:

```text
todo read chapter 3
deadline submit reflection /by 25/8/2026 1800
event project meeting /from 24/8/2026 1400 /to 24/8/2026 1530
list
mark 1
find project
undo
duethisdate 25/8/2026
bye
```

## Troubleshooting

- If Apollo reports an invalid task number, run `list` and use a number shown there.
- If a date is rejected, check that it follows `d/M/yyyy` or `d/M/yyyy HHmm` and represents a real calendar date.
- If a command is not recognised, compare it with the formats in the [command summary](#command-summary).
- If tasks cannot be saved, check that Apollo has permission to write to its current folder.
