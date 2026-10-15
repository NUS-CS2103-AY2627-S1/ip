# Alice User Guide

**Alice** is a thoughtful task companion for keeping track of todos, deadlines, events, and recurring tasks.
Type a command in the input box and press <kbd>Enter</kbd> or select **Send**.

## Quick start

1. Open Alice.
2. Add a task:

   ```text
   todo read Alice in Wonderland
   ```

3. View your tasks:

   ```text
   list
   ```

4. When you are finished, enter `bye`.

> [!TIP]
> Command words are lowercase. Task numbers start at `1`, and dates use `yyyy-MM-dd`.

## Features

### Add a todo: `todo`

Adds a task without a date.

```text
todo DESCRIPTION
```

Example:

```text
todo borrow a library book
```

### Add a deadline: `deadline`

Adds a task that is due on a date.

```text
deadline DESCRIPTION /by yyyy-MM-dd
```

Example:

```text
deadline submit report /by 2026-10-01
```

### Add an event: `event`

Adds a task with a start and end date-time. Times use 24-hour `HHmm` format.

```text
event DESCRIPTION /from yyyy-MM-dd HHmm /to yyyy-MM-dd HHmm
```

Example:

```text
event project meeting /from 2026-10-01 1400 /to 2026-10-01 1500
```

### View all tasks: `list`

Shows every task in your list.

```text
list
```

### Mark a task complete: `mark`

Marks an incomplete task as done.

```text
mark TASK_NUMBER
```

Example:

```text
mark 2
```

### Mark a task incomplete: `unmark`

Marks a completed, non-recurring task as not done.

```text
unmark TASK_NUMBER
```

### Delete a task: `delete`

Removes a task from the list.

```text
delete TASK_NUMBER
```

### Find tasks: `find`

Shows tasks whose descriptions contain the keyword. Matching ignores letter case.

```text
find KEYWORD
```

Example:

```text
find book
```

### View tasks on a date: `date`

Shows deadlines and events occurring on the specified date. An event that spans the date is included.

```text
date yyyy-MM-dd
```

Example:

```text
date 2026-10-01
```

### Repeat a deadline or event: `repeat`

Deadlines and events can repeat `daily`, `weekly`, `monthly`, or `yearly`.
You can set a recurrence while creating the task or change one later. Todos cannot recur.

Create a recurring task by adding `/r INTERVAL`:

```text
deadline pay rent /by 2026-10-01 /r monthly
event team meeting /from 2026-10-08 1400 /to 2026-10-08 1500 /r weekly
```

Change a task's recurrence:

```text
repeat TASK_NUMBER INTERVAL
```

Stop a recurrence:

```text
repeat TASK_NUMBER none
```

When you complete a recurring task, Alice keeps the completed occurrence and adds its next occurrence.

### Exit Alice: `bye`

Closes Alice after a brief farewell.

```text
bye
```

## Command summary

| Command | Format |
|---|---|
| Add todo | `todo DESCRIPTION` |
| Add deadline | `deadline DESCRIPTION /by yyyy-MM-dd` |
| Add event | `event DESCRIPTION /from yyyy-MM-dd HHmm /to yyyy-MM-dd HHmm` |
| List tasks | `list` |
| Mark complete | `mark TASK_NUMBER` |
| Mark incomplete | `unmark TASK_NUMBER` |
| Delete task | `delete TASK_NUMBER` |
| Find tasks | `find KEYWORD` |
| View by date | `date yyyy-MM-dd` |
| Set or change recurrence | `repeat TASK_NUMBER daily\|weekly\|monthly\|yearly` |
| Stop recurrence | `repeat TASK_NUMBER none` |
| Exit | `bye` |
