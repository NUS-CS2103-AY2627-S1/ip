# Bob User Guide

Bob is a friendly task manager for todos, deadlines, and events. Enter a
command in the chat window and Bob will add, update, search, or organize your
tasks for you.

## Getting started

1. Ensure that JDK 25 is installed.
2. Open a terminal in the project folder.
3. Start Bob with:

   ```text
   gradlew.bat run
   ```

   On macOS or Linux, use `./gradlew run` instead.

Bob automatically loads saved tasks when it starts. Changes are saved in
`data/Bob.txt` in the folder where Bob is run.

## Creating tasks

### Todo

Use a todo for a task without a date or time.

```text
todo <description>
```

Example:

```text
todo buy groceries
```

### Deadline

Use a deadline for something that must be completed by a particular date or
time. The `/by` detail is required.

```text
deadline <description> /by <date or date-time>
```

Examples:

```text
deadline submit report /by 2026-10-15
deadline call bank /by 2026-10-15 18:00
```

### Event

Use an event for something with a start and end date or time. Both `/from` and
`/to` details are required.

```text
event <description> /from <start> /to <end>
```

Example:

```text
event team meeting /from 2026-10-16 09:00 /to 2026-10-16 10:00
```

Dates use `yyyy-MM-dd`. You may optionally include a time in `HH:mm` or
`HH:mm:ss` format.

## Viewing and finding tasks

| Command | What it does |
| --- | --- |
| `list` | Shows every task in the current list. |
| `upcoming` | Shows deadlines and events starting within the next seven days. |
| `upcoming <number of days>` | Shows deadlines and events starting within the specified number of days. |
| `on <yyyy-MM-dd>` | Shows deadlines and events scheduled on the date. Events are included for every date they span. |
| `overdue` | Shows incomplete deadlines whose due date or time has passed. |
| `find <keyword>` | Finds tasks whose descriptions contain the keyword, ignoring letter case. |

Examples:

```text
upcoming 14
on 2026-10-16
overdue
find report
```

Todos do not appear in date-based searches because they have no date or time.

## Updating and removing tasks

Bob numbers tasks starting from `1`. Use the number shown by `list` or another
task-list command.

```text
mark <task number>
unmark <task number>
delete <task number>
```

For example:

```text
list
mark 1
delete 2
```

`mark` marks a task as complete, `unmark` marks it as incomplete, and `delete`
removes it from the list. After a deletion, the remaining tasks are numbered
again.

## Undoing a change

Use `undo` to reverse the most recent successful change. It can undo adding,
marking, unmarking, or deleting a task.

```text
todo read a book
undo
```

Only one undo is available at a time. Viewing or searching tasks does not
replace the undo action, but Bob clears the undo history when it restarts.
`undo` does not accept a task number or any other argument.

## Ending a session

In the command-line version, use `bye` to exit Bob:

```text
bye
```

If a command is invalid, Bob explains the expected format and continues
running so that you can try again.
