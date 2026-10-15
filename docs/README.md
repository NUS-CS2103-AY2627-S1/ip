# Lily User Guide

Lily is a chat-style task manager for keeping track of todos, deadlines, and
events. Type a command in the input box and press **Enter** or click **Send**.
Lily replies in the chat and saves changes automatically.

![Lily chatbot interface](Ui.png)

## Table of Contents

- [Starting Lily](#starting-lily)
- [Quick Start](#quick-start)
- [Features](#features)
- [Saving and Closing](#saving-and-closing)
- [Troubleshooting](#troubleshooting)
- [Command Summary](#command-summary)

## Starting Lily

1. Ensure you have Java 25 installed. Check by running `java -version` in a terminal.
2. Download the latest version of `lily.jar` from the [releases page](https://github.com/lilosy/ip/releases).
3. Place the JAR file in an empty folder where you want to store your tasks.
4. Open the terminal in that folder and run:

   ```text
   java -jar lily.jar
   ```
5. Lily should open in a window with a welcome message.

On its first start, Lily creates a `data` folder next to where it is run and
stores your tasks in `data/lily.txt`. Keep this file if you want to keep your
tasks. Always run Lily from the same folder so it can find and load the same
`data/lily.txt` file.

## Quick start

Try these commands in order:

```text
todo read chapter 1
deadline submit assignment /by 2026-09-25 2359
event study session /from 2026-09-20 1400 /to 2026-09-20 1600
list
mark 1
find assignment
schedule 2026-09-20
delete 1
```

Task numbers shown by `list` are used by commands such as `mark`, `unmark`, and
`delete`. This example adds three tasks, completes the first, searches for the
deadline, views the event on its scheduled date, and deletes the first task.

## Features

> **ℹ️ Notes about command format**
>
> - Command words are case-insensitive, so `TODO` and `todo` work alike. Task
>   descriptions preserve the capitalization and repeated spaces you type.
> - Words in `<angle brackets>` are required values that you must supply. For
>   example, replace `<description>` in `todo <description>` with text such as
>   `buy groceries`.
> - Words in `[square brackets]` are optional. For example, `schedule [date]`
>   can be entered as either `schedule` or `schedule tomorrow`.
> - Use single spaces between command arguments. Repeated spaces inside task
>   descriptions are allowed and are preserved.
> - Dates for deadlines and events can use `yyyy-MM-dd` or `d/M/yyyy`. Add a
>   time in the 24-hour `HHmm` format when needed; for example, `0930` means
>   9:30 AM and `1800` means 6:00 PM.
> - A date without a time is displayed as a date-only task. Dates supplied to
>   `schedule` must not include a time.
> - Impossible dates and times, such as `2026-02-30` or `2500`, are rejected.

### Add a todo

Adds a task that does not have a date or time.

**Format**: `todo <description>`

Example:

```text
todo buy groceries
```

### Add a deadline

Adds a task that must be completed by a specific date, with an optional time.

**Format**: `deadline <description> /by <date/time>`

Examples:

```text
deadline return library book /by 2026-10-01
deadline submit report /by 1/10/2026 1800
```

### Add an event

Adds a task that takes place between a specified start and end.

**Format**: `event <description> /from <start date/time> /to <end date/time>`

Examples:

```text
event team meeting /from 2026-10-02 0930 /to 2026-10-02 1030
event holiday /from 2/10/2026 /to 5/10/2026
```

The end of an event cannot be before its start.

### List all tasks

Shows all the tasks in your list and their completion status.

**Format**: `list`

Each task has a type marker and status:

- `[ ]` means not completed; `[X]` means completed
- `[D]` identifies a deadline
- `[E]` identifies an event
- `[T]` identifies a todo

For example:

```text
1. [T][ ] buy groceries
2. [D][X] submit report (by: Oct 01 2026, 6:00PM)
```

### Mark or unmark a task

Marks a task as completed or changes it back to not completed.

**Format**:

- `mark <task number>`
- `unmark <task number>`

Examples:

```text
mark 2
unmark 2
```

Task numbers are positive whole numbers from `list` or `schedule`.

### Delete a task

Permanently removes the specified task from your list.

**Format**: `delete <task number>`

Example:

```text
delete 3
```

Use the number shown beside the task in `list` or `schedule`.

Deleting is permanent from Lily's list, so check the task number before sending
the command.

### Find tasks

Shows tasks whose descriptions contain the specified keyword.

**Format**: `find <keyword>`

Example:

```text
find report
```

Search matches part of a task description and ignores letter case. `find` accepts
one keyword at a time.

### View a daily schedule

Shows deadlines due and events taking place on the selected date.

**Format**: `schedule [date]`

Examples:

```text
schedule
schedule today
schedule tomorrow
schedule 2026-09-16
schedule 16/9/2026
```

With no date, Lily shows today's schedule. The schedule includes deadlines due on
that date and events that span that date; todos are not included. Multi-day
events appear on every day from their start through their end. Incomplete and
completed tasks are shown separately, and the **original task numbers** are retained.

Entries without a specified time appear first, followed by timed entries in time
order. Viewing a schedule does not change your saved tasks.

### Say goodbye

Shows Lily's farewell message, disables further input, and closes the application
after a brief pause.

**Format**: `bye`

Example:

```text
bye
```

## Saving and closing

Lily saves after you add, mark, unmark, or delete a task. Close the window
normally when you want to exit, or type the following to show Lily's farewell
message and close the application automatically:

```text
bye
```

You do not need to run a separate save command. To back up your tasks, close Lily
and copy the entire `data` folder to a safe location. Avoid editing
`data/lily.txt` manually while Lily is running because a later command may
overwrite those edits.

## Troubleshooting

Lily explains most command problems in the chat. The following table covers
common issues and the steps you can take to resolve them.

| Issue | What to do |
| --- | --- |
| Running `java -version` reports that `java` is not recognised or cannot be found. | Install Java 25, ensure Java is added to your system's `PATH`, then close and reopen the terminal before trying again. |
| Lily does not open, or Java reports an incompatible class version. | Confirm that `java -version` reports Java 25. If another version appears, configure your computer to use Java 25 and run Lily again. |
| The terminal reports `Unable to access jarfile lily.jar`. | Open the terminal in the folder containing `lily.jar`. Check that the filename is exactly `lily.jar`, then run `java -jar lily.jar` again. |
| Lily does not recognise a command. | Check its spelling and compare it with the [Command Summary](#command-summary). Use single spaces between command arguments; repeated spaces are supported only inside task descriptions. |
| Lily rejects a date or time. | Use one of the supported date formats and write times using four-digit, 24-hour `HHmm` notation. Check that the date exists and, for an event, that its end is not before its start. |
| Lily cannot find the specified task number. | Run `list` again and use the current positive whole number shown beside the task. Task numbers can change after a task is deleted. |
| Previously saved tasks are missing. | Close Lily without making further changes, then check that you started it from the same folder as before. The expected save file is `data/lily.txt` relative to that folder. Check any other folder from which Lily may have been run for another `data` folder. |
| Lily reports that it cannot create or write to the `data` folder. | Move `lily.jar` to a folder where you have permission to create and edit files. Also check that an ordinary file named `data` is not blocking Lily from creating the folder. |
| Lily displays a startup warning about malformed task records. | Close Lily before changing any tasks and make a backup copy of the `data` folder. Lily loads the valid records and identifies the invalid lines in the warning. Restore a known-good backup if one is available; otherwise, keep the backup and recreate the affected tasks in Lily. |
| Lily reports that the save file is not valid UTF-8. | Lily attempts to preserve the unreadable file as a timestamped `.corrupted-...` backup and starts with an empty list. Keep that backup in case its contents can be recovered, then recreate or restore your tasks from a separate backup. |

If the problem remains, record the command you entered, Lily's complete response,
the output of `java -version`, and the steps needed to reproduce the problem.
Provide those details to the developer, together with a screenshot if useful. Do
not share `data/lily.txt` unless you have checked that its task descriptions
contain no private information.

## Command Summary

| Action | Command | Example |
| --- | --- | --- |
| Add a todo | `todo <description>` | `todo buy groceries` |
| Add a deadline | `deadline <description> /by <date/time>` | `deadline submit report /by 2026-10-01 1800` |
| Add an event | `event <description> /from <start date/time> /to <end date/time>` | `event team meeting /from 2026-10-02 0930 /to 2026-10-02 1030` |
| View all tasks | `list` | `list` |
| Mark a task as completed | `mark <task number>` | `mark 2` |
| Mark a task as not completed | `unmark <task number>` | `unmark 2` |
| Delete a task | `delete <task number>` | `delete 3` |
| Find tasks by keyword | `find <keyword>` | `find report` |
| View a daily schedule | `schedule [date]` | `schedule tomorrow` |
| Show Lily's farewell message and close the application | `bye` | `bye` |
