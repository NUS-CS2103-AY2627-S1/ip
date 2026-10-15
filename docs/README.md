# Bob User Guide

Bob is a desktop chatbot that helps you keep track of todos, deadlines, and events using short text commands.

## Quick start

1. Launch Bob.
2. Type a command in the input box.
3. Press **Enter** or click **Run**.

The conversation expands with the window when it is resized.

Bob saves your tasks automatically. When Bob starts, it shows the tasks loaded from storage and the number of
invalid stored tasks that were skipped.

> **Date format:** Use `dd/MM/yy HH:mm` in 24-hour time. For example, `21/09/26 18:30` means
> 21 September 2026 at 6:30 PM. Dates are checked strictly, so nonexistent dates such as `31/09/26 10:00`
> are rejected.

Command words and flags are case-insensitive. Task descriptions keep their original capitalization and may include
the `|` character.

## Command summary

| Action | Command |
| --- | --- |
| Add a todo | `todo DESCRIPTION` |
| Add a deadline | `deadline DESCRIPTION /by DATE_TIME` |
| Add an event | `event DESCRIPTION /from DATE_TIME /to DATE_TIME` |
| Show all tasks | `list` |
| Find tasks | `find KEYWORD` |
| Mark a task as done | `mark INDEX` |
| Mark a task as not done | `unmark INDEX` |
| Delete a task | `delete INDEX` |
| Update a task | `update INDEX FLAG VALUE [FLAG VALUE]...` |
| Exit Bob | `bye` |

## Adding tasks

### Todo

Use a todo for a task without a date or time.

```text
todo read chapter 3
```

### Deadline

Use a deadline for a task that must be completed by a specific time.

```text
deadline submit report /by 21/09/26 18:30
```

### Event

Use an event for an activity with a start and end time. The end must not be earlier than the start.

```text
event project meeting /from 22/09/26 14:00 /to 22/09/26 15:30
```

## Viewing and finding tasks

Use `list` to display every task and its index:

```text
list
```

Use `find` to show tasks whose descriptions contain a keyword. Matching is case-insensitive.

```text
find report
```

## Completing, reopening, and deleting tasks

Use the index shown by `list` or `find`:

```text
mark 2
unmark 2
delete 2
```

Task indices must be positive integers. They can change after a deletion, so run `list` again when unsure.

## Updating tasks

Use one or more update flags in any order:

| Flag | Updates |
| --- | --- |
| `/desc DESCRIPTION` | Task description |
| `/by DATE_TIME` | Deadline time |
| `/from DATE_TIME` | Event start time |
| `/to DATE_TIME` | Event end time |

Examples:

```text
update 1 /desc read chapters 3 and 4
update 2 /by 23/09/26 20:00 /desc submit final report
update 3 /from 24/09/26 09:30 /to 24/09/26 11:00
```

Only flags that apply to the task type are accepted:

- Todos support `/desc`.
- Deadlines support `/desc` and `/by`.
- Events support `/desc`, `/from`, and `/to`.

## Data storage

Bob stores tasks in `data/tasks.txt`. Fields use the `0x01` control character as an internal delimiter, allowing task
descriptions to contain `|` without data loss. Pipe-delimited files created by older versions are not supported and
are reported as invalid stored tasks.

## Exiting

Use the following command to close Bob safely:

```text
bye
```
