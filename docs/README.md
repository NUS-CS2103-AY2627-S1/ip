# Bean User Guide

Bean is a friendly taskbot for managing to-do tasks, deadlines, and events through a simple chat-style interface.
Type a command in the input box and press **Enter** or click **Send**.

## Quick start

Try these commands:

```text
todo read chapter 3
list
mark 1
```

Bean saves your tasks automatically in `memory.txt` when the application closes.

## Command format

- Words in `UPPER_CASE` are values you provide.
- Text in square brackets is optional.
- Task names and search terms may contain spaces.
- Priorities are `HIGH`, `MEDIUM`, or `LOW`.
- Task numbers refer to the numbered tasks shown by Bean.

## Features

### Add a to-do task: `todo` or `td`

Adds a task without a date.

Format:

```text
todo TASK [/priority PRIORITY]
```

Examples:

```text
todo buy groceries
todo finish project report /priority HIGH
```

### Add a deadline: `deadline` or `dln`

Adds a task that must be completed by a date. Use the format `yyyy-mm-dd`.

Format:

```text
deadline TASK /by DATE [/priority PRIORITY]
```

Example:

```text
deadline submit report /by 2026-10-31 /priority HIGH
```

### Add an event: `event` or `evt`

Adds a task that takes place between two dates.

Format:

```text
event TASK /from START_DATE /to END_DATE [/priority PRIORITY]
```

Example:

```text
event team meeting /from 2026-10-05 /to 2026-10-05 /priority MEDIUM
```

### List tasks: `list` or `ls`

Displays all tasks. Tasks are shown with their type, completion status, dates, and priority.

```text
list
```

### Find a task: `find`, `findtask`, or `f`

Searches task names for a keyword. The search is case-insensitive and can include spaces.

```text
find KEYWORD
findtask "KEYWORD PHRASE"
```

Examples:

```text
find report
findtask "buy groceries"
```

### Mark a task as complete: `mark`

Marks the task with the given number as complete. Completed tasks are displayed with a strikethrough.

```text
mark TASK_NUMBER
```

Example:

```text
mark 2
```

### Mark a task as incomplete: `unmark`

Restores a completed task to its incomplete state.

```text
unmark TASK_NUMBER
```

### Delete a task: `delete` or `del`

Deletes the task with the given number and displays the remaining tasks.

```text
delete TASK_NUMBER
```

Example:

```text
del 3
```

### Exit Bean: `exit` or `q`

Closes Bean. Your tasks are saved automatically.

```text
exit
```

## Priority colours

Bean uses colour to make priorities easy to scan:

- **High** — red
- **Medium** — yellow
- **Low** — green

Invalid commands or values are shown as red error messages with additional guidance.

## Command summary

| Action | Format | Aliases |
| --- | --- | --- |
| Add to-do | `todo TASK [/priority PRIORITY]` | `td` |
| Add deadline | `deadline TASK /by DATE [/priority PRIORITY]` | `dln` |
| Add event | `event TASK /from START_DATE /to END_DATE [/priority PRIORITY]` | `evt` |
| List tasks | `list` | `ls` |
| Find tasks | `find KEYWORD` | `findtask`, `f` |
| Mark complete | `mark TASK_NUMBER` | — |
| Mark incomplete | `unmark TASK_NUMBER` | — |
| Delete task | `delete TASK_NUMBER` | `del` |
| Exit | `exit` | `q` |
