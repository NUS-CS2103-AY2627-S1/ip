# Yokohama User Guide

Yokohama is a desktop chatbot for managing personal tasks, deadlines, and events.
Your tasks are saved locally on your device.

## Getting started

### Requirements

- Java 25
- A desktop environment that supports JavaFX

### Starting Yokohama

From the project directory, run:

```bash
./gradlew run
```

On Windows, use:

```bat
gradlew.bat run
```

The chatbot opens in a resizable window. Type a command in the message box and
press **Enter** or click **Send**.

## Commands

Yokohama supports these commands:

| Command | Purpose |
| --- | --- |
| `todo <description>` | Add a task without a date. |
| `deadline <description> /by <date> [time]` | Add a deadline. |
| `event <description> /from <date and time> /to <date and time>` | Add an event. |
| `list` | List all saved tasks. |
| `find <keyword>` | Find tasks whose descriptions contain a keyword. |
| `schedule <date>` | Show deadlines and events scheduled for a date. |
| `mark <number>` | Mark a task as complete. |
| `unmark <number>` | Mark a task as incomplete. |
| `delete <number>` | Delete a task. |
| `exit` | Save all tasks and close Yokohama. |

Examples:

```text
todo buy milk
deadline submit report /by 9/3/2026 2359
event team meeting /from 9/10/2026 0900 /to 9/10/2026 1000
list
find report
schedule 9/10/2026
mark 1
unmark 1
delete 1
exit
```

### Add a task

Use `todo` for a task without a date:

```text
todo buy milk
```

### Add a deadline

Use `deadline` followed by a description and `/by`. The time is optional.

```text
deadline submit report /by 9/3/2026
deadline submit report /by 9/3/2026 2359
```

If you omit the time, Yokohama uses **11:59 PM** on that date.

### Add an event

Use `/from` and `/to` with a date and time:

```text
event team meeting /from 9/10/2026 0900 /to 9/10/2026 1000
```

The event's start must be earlier than its end.

### View all tasks

```text
list
```

Tasks are shown with their numbers. Use these numbers with `mark`, `unmark`,
and `delete`.

### Find tasks

Search task descriptions with `find`:

```text
find report
```

Search is case-insensitive.

### View a schedule

Show deadlines and events for a date:

```text
schedule 9/10/2026
```

### Mark a task as complete

```text
mark 1
```

### Mark a task as incomplete

```text
unmark 1
```

### Delete a task

```text
delete 1
```

### Exit

Save your tasks and close the chatbot:

```text
exit
```

## Date and time format

Use these formats:

- Date: `M/d/yyyy`, such as `9/3/2026`
- Date and time: `M/d/yyyy HHmm`, such as `9/3/2026 2359`

Dates must be real calendar dates. For example, `2/30/2026` is invalid.
Times use the 24-hour clock, so `1830` means 6:30 PM.

## Input tips

- Enter one command at a time.
- Use exactly one space between command parts.
- Do not add leading or trailing spaces.
- Use each parameter once. For example, an event must contain exactly one
  `/from` and one `/to`.
- Task descriptions cannot contain the `|` character or control characters.
- Do not create the same task more than once.

Invalid commands are shown in a highlighted error message with an explanation.
Your existing tasks remain available after an invalid command.

## Saved tasks and recovery

Yokohama stores tasks in `data/todo_data.txt` relative to the project directory.
The file is updated when tasks are added, edited, deleted, or when the
application exits.

If the file is missing, Yokohama creates it when it first saves a task. If the
file cannot be read or contains invalid data, Yokohama displays a warning so
you can repair or remove the damaged file. If the file cannot be written,
check that the directory exists and that you have permission to modify it.

## Window behavior

You can resize the application window. The task conversation and message
bubbles adjust to the available space. If the window becomes too small, enlarge
it until the message list and command box are comfortable to use.
