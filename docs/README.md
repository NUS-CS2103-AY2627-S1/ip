# Alfred User Guide

Alfred is a cave-themed task butler. Use the commands below to keep a concise
ledger of to-dos, deadlines, and events. Commands are lowercase; surrounding
spaces are accepted.

![Alfred's full application window](Ui.png)

## Add a task

Add a to-do with `todo DESCRIPTION`.

```text
todo inspect lanterns
```

Add a deadline with `deadline DESCRIPTION /by DATE`. Add an event with
`event DESCRIPTION /from START /to END`. Dates use either `yyyy-MM-dd` or
`d/M/yyyy HHmm`. An event must end after it begins.

```text
deadline send report /by 2026-09-18
event patrol cave /from 18/9/2026 0900 /to 18/9/2026 1030
```

Alfred rejects duplicate tasks with identical details and rejects repeated
`/by`, `/from`, or `/to` markers.

## View and manage tasks

Use `list` to view all tasks. Use `find KEYWORD` to find task descriptions,
and `sort`, `sort asc`, or `sort desc` for a chronological view that does not
change the saved task order.

Use `mark NUMBER`, `unmark NUMBER`, and `delete NUMBER` to update the
one-based task number shown by `list`.

```text
list
find lantern
mark 1
unmark 1
delete 1
```

## Exit Alfred

Use `bye` without additional arguments. Alfred confirms the farewell before
closing the GUI.

## Sorting tasks

Use `sort` to view tasks in ascending chronological order without changing their saved or listed
order. Deadlines are ordered by due time, events by start time, and to-dos appear after dated tasks.

Use `sort desc` to view tasks in descending chronological order. In this view, to-dos appear before
dated tasks. `sort asc` is an explicit form of the default ascending order.

Examples:

```text
sort
sort asc
sort desc
```

Only these lowercase forms are accepted. A sorted view does not change task numbers for `list`,
`mark`, `unmark`, or `delete`.
