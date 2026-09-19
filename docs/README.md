# Bob User Guide

Bob is a simple chatbot that helps you keep track of todos, deadlines, and
events. Enter one command at a time in the chat window. Your tasks are saved
automatically, so they are available the next time you start Bob.

## Quick start

Use the following commands to manage your task list:

| Command | What it does |
| --- | --- |
| `todo DESCRIPTION` | Adds a task without a date. |
| `deadline DESCRIPTION /by DATE` | Adds a task that is due on a date. |
| `event DESCRIPTION /from DATE /to DATE` | Adds an event spanning two dates. |
| `list` | Shows all tasks in their current order. |
| `find KEYWORD` | Shows tasks whose descriptions contain the keyword. |
| `mark TASK_NUMBER` | Marks a task as done. |
| `unmark TASK_NUMBER` | Marks a task as not done. |
| `edit TASK_NUMBER NEW_DETAILS` | Changes a task's description or date details. |
| `delete TASK_NUMBER` | Removes a task. |
| `bye` | Exits Bob. |

Task numbers are the numbers shown by `list`. They start at 1.

## Adding tasks

### Todo

Use `todo` followed by a description:

```
todo buy groceries
```

### Deadline

Use `deadline`, a description, and `/by` followed by a date:

```
deadline submit report /by 2026-09-30
```

Dates must use `yyyy-MM-dd`, such as `2019-10-15`.

### Event

Use `event`, a description, `/from`, and `/to` followed by the start and end
dates:

```
event project meeting /from 2026-09-20 /to 2026-09-21
```

The end date must be later than the start date. Events and deadlines cannot be
added with the same details as an existing task.

## Viewing and finding tasks

`list` displays every task. Each task shows its type, completion state, and any
date information. To search descriptions without worrying about letter case,
use `find`:

```
find report
```

## Updating tasks

Use the number displayed by `list`:

```
mark 1
unmark 1
delete 2
```

To edit a task, provide its number and new details. Keep the task's original
type. For example:

```
edit 1 buy groceries and detergent
edit 2 submit final report /by 2026-10-01
edit 3 project meeting /from 2026-09-21 /to 2026-09-22
```

If a command is invalid, Bob explains what needs to be corrected. Existing
tasks remain unchanged.
