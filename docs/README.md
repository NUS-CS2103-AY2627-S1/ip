# Bobby User Guide

Bobby is a cheerfully goofy task manager that helps you keep track of to-dos,
deadlines, and events through simple text commands.

![Bobby's graphical interface](Ui.png)

## Quick start

1. Install Java 25.
2. Download `bobby.jar` from the latest GitHub release.
3. Place the JAR file in a folder where Bobby can store its data.
4. Open a terminal in that folder and run:

   ```text
   java -jar bobby.jar
   ```

5. Enter commands in Bobby's input field and press Enter or click **Boop!**.

Bobby saves successful changes automatically in `data/bobby.txt`. Enter `bye`
when you are finished.

## Understanding the task list

Each task starts with symbols that describe it:

| Symbol | Meaning |
| --- | --- |
| `[T]` | To-do |
| `[D]` | Deadline |
| `[E]` | Event |
| `[ ]` | Not completed |
| `[X]` | Completed |
| `[P: HIGH]` | Assigned priority |

Tasks are numbered in the order they were added. Commands such as `mark`,
`delete`, and `priority` use these task numbers.

## Command summary

| Action | Format |
| --- | --- |
| Add a to-do | `todo DESCRIPTION` |
| Add a deadline | `deadline DESCRIPTION /by DATE` |
| Add an event | `event DESCRIPTION /from DATE /to DATE` |
| View all tasks | `list` |
| Mark a task | `mark TASK_NUMBER` |
| Unmark a task | `unmark TASK_NUMBER` |
| Delete a task | `delete TASK_NUMBER` |
| Find tasks | `find KEYWORD` |
| Set a priority | `priority TASK_NUMBER LEVEL` |
| Exit Bobby | `bye` |

Command names should be entered in lowercase. Replace words in uppercase, such
as `DESCRIPTION`, with your own values. Bobby accepts extra spaces around and
between command details.

## Adding a to-do

Use a to-do for a task without a specific date.

```text
todo DESCRIPTION
```

Example:

```text
todo review lecture notes
```

## Adding a deadline

Use a deadline for a task that must be completed by a date or date and time.

```text
deadline DESCRIPTION /by DATE
```

Use `yyyy-MM-dd` for a date:

```text
deadline submit report /by 2026-09-18
```

Add a 24-hour time in `HHmm` format when needed:

```text
deadline submit report /by 2026-09-18 2359
```

## Adding an event

Use an event for something with a start date and an end date.

```text
event DESCRIPTION /from DATE /to DATE
```

Both dates must use `yyyy-MM-dd`. The end date cannot be earlier than the start
date, but a same-day event is allowed.

Example:

```text
event project consultation /from 2026-09-16 /to 2026-09-16
```

## Viewing tasks

Display every task and its current task number:

```text
list
```

## Marking and unmarking tasks

Mark a task as completed:

```text
mark TASK_NUMBER
```

Example:

```text
mark 2
```

Change a completed task back to incomplete:

```text
unmark TASK_NUMBER
```

Example:

```text
unmark 2
```

## Deleting a task

Delete a task using its current task number:

```text
delete TASK_NUMBER
```

Example:

```text
delete 3
```

The remaining tasks are renumbered after deletion.

## Finding tasks

Search task descriptions for a word or phrase:

```text
find KEYWORD
```

Example:

```text
find report
```

Search is not case-sensitive, so `report` and `REPORT` produce the same
matches.

## Prioritizing tasks

Assign or clear the priority of an existing task:

```text
priority TASK_NUMBER LEVEL
```

Supported levels are:

| Level | Numeric alias |
| --- | --- |
| `high` | `1` |
| `medium` | `2` |
| `low` | `3` |
| `none` | No numeric alias |

Priority names are not case-sensitive. For example, both commands assign high
priority to task 2:

```text
priority 2 high
priority 2 1
```

Clear its priority with:

```text
priority 2 none
```

Changing a priority does not reorder the task list or change the task's
completion status.

## Exiting Bobby

Close Bobby safely with:

```text
bye
```

## Input errors

Bobby explains invalid commands without changing the task list. Common causes
include a missing description, invalid task number, unsupported date, repeated
parameter, or duplicate task. Task descriptions cannot contain the `|`
character because Bobby uses it to save task data.
