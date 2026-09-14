# Cookie User Guide

Cookie is a task manager that accepts text commands through its console or JavaFX chat window.
Commands are case-insensitive, although task descriptions and search keywords retain their case.

## Command summary

| Command | Purpose |
| --- | --- |
| `todo <description>` | Add a task without a date or time. |
| `deadline <description> /by <date/time>` | Add a task with a due value. |
| `event <description> /from <date/time> /to <date/time>` | Add an event with start and end values. |
| `list` | Display every task in its stored order. |
| `sort <description\|date> [asc\|desc]` | Display a temporary sorted view. |
| `find <keyword>` | Display tasks whose descriptions contain the keyword. |
| `on <date>` | Display deadlines and events occurring on the date. |
| `mark <task number>` | Mark a task as complete. |
| `unmark <task number>` | Mark a task as incomplete. |
| `delete <task number>` | Delete a task. |
| `bye` | Exit Cookie. |

## Adding tasks

Add a todo with a description:

```text
todo read book
```

Add a deadline using a date, a time, or both:

```text
deadline submit report /by 2026-09-15 1800
```

Add an event with `/from` and `/to` values:

```text
event project meeting /from 2026-09-15 1400 /to 2026-09-15 1600
```

Dates accept `yyyy-MM-dd` or `d/M/yyyy`. Times use the 24-hour `HHmm` format.
An event's start and end must use the same kind of value (date, time, or date-time),
and its end must be later than its start. Each task must have unique identifying details;
completion status does not make an otherwise identical task unique.

Cookie accepts surrounding or repeated whitespace, but each `/by`, `/from`, and `/to`
parameter may appear only once. The `|` storage delimiter and control characters are not
allowed in task details.

## Viewing and finding tasks

Use `list` to display all tasks in their stored order. Task numbers identify tasks for
`mark`, `unmark`, and `delete`.

Use `find` for an exact, case-sensitive substring search of task descriptions:

```text
find book
```

Use `on` to display deadlines due on a date and events whose date ranges include it:

```text
on 2026-09-15
```

Filtered results preserve the task numbers shown by `list`.

## Sorting tasks temporarily

Use `sort` with `description` or `date`. The optional direction is `asc` or `desc`;
ascending is the default.

```text
sort description
sort description desc
sort date
sort date desc
```

Description sorting is alphabetical and case-insensitive. Equal descriptions retain
their stored relative order.

Date sorting compares deadlines by their due values and events by their start values.
Tasks with calendar dates appear first, followed by time-only tasks and then todos.
Date-only values are treated as the start of their day. These groups stay in that order
for both ascending and descending views.

Sorting does not change or save the stored task order. Each sorted result retains the
original task numbers, so a command such as `mark 3` still targets task 3 even if it
appears first in the sorted view.

## Updating tasks

Use a task's displayed number to update or remove it:

```text
mark 2
unmark 2
delete 2
```

Marking, unmarking, deleting, and adding tasks are saved automatically. Sorting is a
read-only view and is not persisted.

If Cookie cannot read its task file at startup, it reports the problem and blocks changes
for that session. This prevents an unreadable file from being overwritten. Fix the reported
file or permission issue, then restart Cookie.

## Exiting

Enter `bye` to exit Cookie:

```text
bye
```
