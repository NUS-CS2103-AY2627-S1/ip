# Martin User Guide

Martin is a desktop task manager for keeping track of todos, deadlines, and
events. Enter commands in the text field at the bottom of the window; Martin
will record and display your engagements in the conversation above it.

## Quick start

1. Ensure that Java 25 is installed.
2. Run the application JAR from a terminal:

   ```sh
   java -jar martin.jar
   ```

3. Type a command and press <kbd>Enter</kbd> or select **Send**.
4. Your tasks are saved automatically in `data/martin.txt`, relative to the
   folder from which you start Martin. Martin creates this file and its parent
   directory when they do not already exist.

## Command syntax

- Commands must begin with a lowercase command word, such as `todo` or
  `list`.
- Text in `UPPER_CASE` is a value you provide. Do not type the uppercase
  placeholder itself.
- Square brackets indicate optional input. For example,
  `todo DESCRIPTION [/p PRIORITY]` can be used with or without `/p PRIORITY`.
- Dates and times should use `yyyy/MM/dd HHmm`, such as `2026/10/01 1400`.
- Task indexes are one-based: the first task shown by `list` has index `1`.
- Valid priorities are `high`, `medium`, and `low`. Tasks without `/p` are
  assigned low priority.

## Features

### Adding a todo: `todo`

Adds a task without a date or time.

Format: `todo DESCRIPTION [/p PRIORITY]`

Examples:

```text
todo read Pride and Prejudice
todo submit report /p high
```

Martin confirms the new task and its priority. The task is saved immediately.

### Adding a deadline: `deadline`

Adds a task to be completed by a specified date and time.

Format: `deadline DESCRIPTION /by DATE_TIME [/p PRIORITY]`

Examples:

```text
deadline return library book /by 2026/10/05 1800
deadline submit report /by 2026/10/01 2359 /p high
```

Both a description and a `/by` value are required. Martin displays the date
and time in a friendlier format after creating the deadline.

### Adding an event: `event`

Adds an engagement with a start and end date/time.

Format: `event DESCRIPTION /from DATE_TIME /to DATE_TIME [/p PRIORITY]`

Examples:

```text
event project meeting /from 2026/10/01 1400 /to 2026/10/01 1530
event CS2103 tutorial /from 2026/10/02 1000 /to 2026/10/02 1200 /p medium
```

The `/from` value must appear before `/to`, and the end date/time cannot be
earlier than the start date/time.

### Listing tasks: `list`

Shows every task in the order it was added.

Format: `list`

Example:

```text
list
```

Each entry begins with its index, followed by its type (`T`, `D`, or `E`),
completion status, and priority. Use the index with `mark`, `unmark`, and
`delete`.

### Finding tasks: `find`

Shows tasks whose descriptions contain the supplied keyword. Matching ignores
letter case.

Format: `find KEYWORD`

Examples:

```text
find report
find MEETING
```

Martin shows all matching tasks, or reports when none match.

### Marking a task complete: `mark`

Marks the task at the specified index as complete.

Format: `mark INDEX`

Example:

```text
mark 2
```

### Marking a task incomplete: `unmark`

Returns a completed task to its unfinished state.

Format: `unmark INDEX`

Example:

```text
unmark 2
```

### Deleting a task: `delete`

Permanently removes the task at the specified index.

Format: `delete INDEX`

Example:

```text
delete 3
```

Run `list` first if you need to check a task's current index. Deletion is
saved immediately.

### Leaving Martin: `bye`

Closes Martin.

Format: `bye`

## Handling errors

Martin does not change your task list when it cannot understand a command or
when required input is missing. It explains common problems, including an
unknown command, an empty description, an invalid priority, a missing date,
an invalid date/time, or a task index that is not in the current list.

If the data file contains a corrupted entry, Martin skips that entry and loads
the remaining valid tasks. If Martin cannot access its data file, it reports
the storage problem when starting or saving.

## Command summary

| Action          | Format                                                          |
| --------------- | --------------------------------------------------------------- |
| Add a todo      | `todo DESCRIPTION [/p PRIORITY]`                                |
| Add a deadline  | `deadline DESCRIPTION /by DATE_TIME [/p PRIORITY]`              |
| Add an event    | `event DESCRIPTION /from DATE_TIME /to DATE_TIME [/p PRIORITY]` |
| List tasks      | `list`                                                          |
| Find tasks      | `find KEYWORD`                                                  |
| Mark complete   | `mark INDEX`                                                    |
| Mark incomplete | `unmark INDEX`                                                  |
| Delete a task   | `delete INDEX`                                                  |
| Exit Martin     | `bye`                                                           |
