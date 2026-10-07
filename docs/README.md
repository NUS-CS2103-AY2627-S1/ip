# Drax User Guide

Drax is a desktop task manager for organizing todos, deadlines, and events using simple text commands. 
It combines the efficiency of a command-line workflow with the convenience of a graphical chat interface.

<img width="897" height="932" alt="Ui" src="https://github.com/user-attachments/assets/40b369e8-7ed0-489a-82ff-2b31d16235b8" />

## Table of contents

- [Quick start](#quick-start)
- [Features](#features)
  - [Adding a todo: `todo`](#adding-a-todo-todo)
  - [Adding a deadline: `deadline`](#adding-a-deadline-deadline)
  - [Adding an event: `event`](#adding-an-event-event)
  - [Listing all tasks: `list`](#listing-all-tasks-list)
  - [Marking a task as complete: `mark`](#marking-a-task-as-complete-mark)
  - [Marking a task as incomplete: `unmark`](#marking-a-task-as-incomplete-unmark)
  - [Finding tasks: `find`](#finding-tasks-find)
  - [Deleting a task: `delete`](#deleting-a-task-delete)
  - [Undoing a change: `undo`](#undoing-a-change-undo)
  - [Redoing a change: `redo`](#redoing-a-change-redo)
  - [Exiting Drax: `bye`](#exiting-drax-bye)
  - [Saving data](#saving-data)
- [FAQ](#faq)
- [Known limitations](#known-limitations)
- [Command summary](#command-summary)

## Quick start

1. Ensure that Java 25 is installed on your computer.
   - On macOS, use the Zulu FX JDK `25.0.3.fx-zulu`.
1. Download the latest `drax.jar` from the
   [Drax releases page](https://github.com/draxche/ip/releases).
1. Move `drax.jar` into the folder where you want Drax to store its data.
1. Open a terminal in that folder and run:

   ```bash
   java -jar drax.jar
   ```

1. Type a command into the text field at the bottom of the window.
1. Press <kbd>Enter</kbd> or select the send button to run the command.

Here are some commands you can try:

- `todo read book` adds a todo.
- `deadline submit report /by 20/9/2026 1800` adds a deadline.
- `event project meeting /from 21/9/2026 1400 /to 21/9/2026 1500` adds an event.
- `list` displays all tasks and their task numbers.

## Features

### Notes about command formats

- Words in `UPPER_CASE` are parameters that you must replace with your own values. For example, replace
  `DESCRIPTION` in `todo DESCRIPTION` with a description such as `read book`.
- Commands must be typed in lowercase.
- Enter each command on one line.
- `INDEX` is the positive task number shown by the `list` command, such as `1`, `2`, or `3`.
- Drax accepts dates and times in any of these formats:
  - `yyyy-MM-dd`, for example `2026-09-20`.
  - `yyyy-MM-ddTHH:mm`, for example `2026-09-20T18:30`.
  - `d/M/yyyy HHmm`, for example `20/9/2026 1830`.
- A date entered without a time is treated as midnight and is displayed without a time.

Task entries use the following symbols:

| Symbol | Meaning |
| --- | --- |
| `[T]` | Todo |
| `[D]` | Deadline |
| `[E]` | Event |
| `[ ]` | Incomplete task |
| `[X]` | Completed task |



## Command summary

| Action | Format | Example |
| --- | --- | --- |
| Add a todo | `todo DESCRIPTION` | `todo read book` |
| Add a deadline | `deadline DESCRIPTION /by DATE_TIME` | `deadline submit report /by 20/9/2026 1830` |
| Add an event | `event DESCRIPTION /from START_DATE_TIME /to END_DATE_TIME` | `event meeting /from 21/9/2026 1400 /to 21/9/2026 1530` |
| List tasks | `list` | `list` |
| Mark as complete | `mark INDEX` | `mark 2` |
| Mark as incomplete | `unmark INDEX` | `unmark 2` |
| Find tasks | `find KEYWORD` | `find report` |
| Delete a task | `delete INDEX` | `delete 1` |
| Undo a change | `undo` | `undo` |
| Redo a change | `redo` | `redo` |
| Exit Drax | `bye` | `bye` |


### Adding a todo: `todo`

Adds a task without a date or time.

Format: `todo DESCRIPTION`

Example: `todo read book`

Expected result:

```text
I've added this task
[T][ ] read book
Now you have 1 task!
```

### Adding a deadline: `deadline`

Adds a task that must be completed by a specific date or time.

Format: `deadline DESCRIPTION /by DATE_TIME`

Examples:

- `deadline submit report /by 2026-09-20`
- `deadline submit report /by 20/9/2026 1830`

Expected result for the second example:

```text
I've added this task
[D][ ] submit report (by: Sep 20 2026 6:30 PM)
Now you have 1 task!
```

### Adding an event: `event`

Adds a task that takes place between a start and end date or time. The `/from` part must appear before the `/to`
part.

Format: `event DESCRIPTION /from START_DATE_TIME /to END_DATE_TIME`

Example: `event project meeting /from 21/9/2026 1400 /to 21/9/2026 1530`

Expected result:

```text
I've added this task
[E][ ] project meeting (from: Sep 21 2026 2:00 PM to: Sep 21 2026 3:30 PM)
Now you have 1 task!
```

### Listing all tasks: `list`

Displays all tasks in the order in which they are currently stored. Use the displayed task numbers as the 
indices for `mark`,`unmark`, and `delete`.

Format: `list`

Example: `list`

Expected result:

```text
Here are the tasks in your list!
1.[T][ ] read book
2.[D][ ] submit report (by: Sep 20 2026 6:30 PM)
3.[E][ ] project meeting (from: Sep 21 2026 2:00 PM to: Sep 21 2026 3:30 PM)
```

### Marking a task as complete: `mark`

Marks the task at `INDEX` as complete. Run `list` first if you are unsure of the task number.

Format: `mark INDEX`

Example: `mark 2`

Expected result:

```text
I've marked this task as done:
[D][X] submit report (by: Sep 20 2026 6:30 PM)
```

### Marking a task as incomplete: `unmark`

Marks the task at `INDEX` as incomplete.

Format: `unmark INDEX`

Example: `unmark 2`

Expected result:

```text
I've marked this task as not done:
[D][ ] submit report (by: Sep 20 2026 6:30 PM)
```

### Finding tasks: `find`

Displays tasks whose descriptions contain `KEYWORD`.

Format: `find KEYWORD`

- The search is case-insensitive. For example, `find BOOK` matches `read book`.
- Partial words match. For example, `find book` matches both `read book` and `book flight`.
- The whole text after `find` is treated as one keyword or phrase.

Example: `find report`

Expected result:

```text
Here are the matching tasks in your list:
1.[D][ ] submit report (by: Sep 20 2026 6:30 PM)
```

> **❗ Important**
>
> The numbers in search results identify positions within the results only. Run `list` to get the task number to
> use with `mark`, `unmark`, or `delete`.

### Deleting a task: `delete`

Removes the task at `INDEX` from the current task list. Run `list` first to confirm the task number.

Format: `delete INDEX`

Example: `delete 1`

Expected result:

```text
I've deleted this task
[T][ ] read book
Now you have 2 tasks!
```

You can immediately restore the task with `undo` if you deleted it by mistake.

### Undoing a change: `undo`

Restores the task list to its state before the most recent successful `todo`, `deadline`, `event`, `mark`, `unmark`,
or `delete` command.

Format: `undo`

Example: `undo`

Drax displays the restored task list after undoing the change. You can use `undo` repeatedly to move through
earlier changes made during the current session.

### Redoing a change: `redo`

Reapplies the most recently undone change.

Format: `redo`

Example: `redo`

Drax displays the updated task list after redoing the change. Running a new command that changes the task list
after an `undo` clears the redo history.

### Exiting Drax: `bye`

Closes Drax.

Format: `bye`

Expected result:

```text
Godspeed. Hope to see ya again soon!
```

### Saving data

Drax automatically saves the task list after every successful command that changes it. You do not need to save
manually.

Data is stored in `data/drax.txt`, relative to the folder from which Drax was launched. To transfer your tasks to
another computer, copy this file into the `data` folder beside the other copy of `drax.jar`.

> **⚠️ Caution**
>
> Edit `data/drax.txt` only if you understand Drax's storage format. Invalid lines are skipped when Drax starts.
> Back up the file before editing it manually.

## FAQ

### Why does Drax say that my date or time is invalid?

Check that it follows one of the supported formats exactly. For example, use `2026-09-20`, `2026-09-20T18:30`,
or `20/9/2026 1830`. Natural-language dates such as `tomorrow` and `next Friday` are not supported.

### Why does Drax say that a task does not exist?

Task numbers can change after a task is deleted. Run `list`, then use the number shown beside the task you want to
change.

### Can I undo a change after restarting Drax?

No. The undo and redo history applies only to the current session. Your saved task list is still loaded when Drax
starts again.

### How do I transfer my data to another computer?

Exit Drax, copy `data/drax.txt` to the `data` folder used by Drax on the other computer, and then start Drax there.
