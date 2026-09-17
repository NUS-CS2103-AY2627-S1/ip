# Nubish User Guide

**Nubish** is a friendly task chatbot that helps you track todos, deadlines, and events from a simple command box.

## Quick Start

Type a command and press <kbd>Enter</kbd>. Nubish saves your tasks automatically, so they will still be there the next time you open the app.

Use dates and times in this format:

```text
dd/MM/yyyy HHmm
```

Example: `12/10/2026 1800` means 12 October 2026, 6:00 PM.

## Command Summary

| Action | Command format | Example |
| --- | --- | --- |
| Add a todo | `todo DESCRIPTION` | `todo read lecture notes` |
| Add a deadline | `deadline DESCRIPTION /by DATE_TIME` | `deadline submit iP /by 12/10/2026 1800` |
| Add an event | `event DESCRIPTION /from START_DATE_TIME /to END_DATE_TIME` | `event project meeting /from 12/10/2026 1800 /to 12/10/2026 1900` |
| List tasks | `list` | `list` |
| Mark a task done | `mark TASK_NUMBER` | `mark 2` |
| Mark a task not done | `unmark TASK_NUMBER` | `unmark 2` |
| Delete a task | `delete TASK_NUMBER` | `delete 3` |
| Find tasks | `find KEYWORD` | `find project` |
| Exit Nubish | `bye` | `bye` |

## Adding Tasks

### Todos

Use a todo for a task with no date or time.

```text
todo DESCRIPTION
```

Example:

```text
todo read lecture notes
```

### Deadlines

Use a deadline for a task that must be done by a specific date and time.

```text
deadline DESCRIPTION /by DATE_TIME
```

Example:

```text
deadline submit iP /by 12/10/2026 1800
```

### Events

Use an event for something that happens over a time range.

```text
event DESCRIPTION /from START_DATE_TIME /to END_DATE_TIME
```

Example:

```text
event project meeting /from 12/10/2026 1800 /to 12/10/2026 1900
```

Requirements:

- `/from` must come before `/to`.
- The `/from` date and time must be earlier than the `/to` date and time.
- Both date-times must use `dd/MM/yyyy HHmm`.

## Managing Tasks

Nubish numbers tasks in the order shown by `list`. Use those task numbers with `mark`, `unmark`, and `delete`.

```text
list
mark 1
unmark 1
delete 1
```

## Finding Tasks

Search task descriptions with:

```text
find KEYWORD
```

Example:

```text
find project
```

Nubish shows tasks whose descriptions contain the keyword.

## Input Tips

- Task descriptions cannot be empty.
- Task descriptions cannot contain `|`.
- Duplicate task descriptions are not allowed, even if the letter case is different.
- Avoid extra spaces at the start, end, or between command words.
