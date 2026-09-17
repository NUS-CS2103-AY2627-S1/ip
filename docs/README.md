# Kia User Guide

![Kia GUI](Ui.png)

Kia is a friendly JavaFX task assistant with a dog-themed personality. It
helps you create, organize, search, update, and complete tasks. Tasks are
saved automatically and restored the next time Kia starts.

## Features and commands

### Quick reference

| Command | Purpose | Example |
| --- | --- | --- |
| `todo` | Add a task without a date | `todo buy milk` |
| `deadline` | Add a task with a due date | `deadline submit report /by 2026-10-15` |
| `event` | Add a task with a time range | `event meeting /from Mon 2pm /to 4pm` |
| `list` | Show all tasks | `list` |
| `find` | Search descriptions | `find report` |
| `mark` / `unmark` | Change completion status | `mark 2` |
| `delete` | Remove a task | `delete 3` |
| `update` | Edit task fields | `update 1 /description buy tea` |
| `bye` | Exit Kia | `bye` |

### Start Kia

From the project root, run:

```bash
./gradlew run
```

Enter commands in the text box and press **Enter** or **Send**. The **Edit
task** button opens a graphical editor.

### Add a todo

```text
todo borrow book
```

Adds a task without a date or time.

### Add a deadline

```text
deadline submit report /by 2026-10-15
```

Adds a task that must be completed by the specified date. Dates use
`yyyy-MM-dd`.

### Add an event

```text
event project meeting /from Mon 2pm /to 4pm
```

Adds a task with start and end values. Event date and time values are kept as
text.

### List tasks

```text
list
```

Displays every task with its one-based number, type, and completion status.

For example:

```text
Here ya go! These are the tasks in your list:
1.[T][ ] buy milk
2.[D][X] submit report (by: Oct 15 2026)
```

### Find tasks

```text
find book
```

Displays tasks whose descriptions contain the keyword and keeps their original
list numbers.

### Mark or unmark a task

```text
mark 2
unmark 2
```

Marks the selected task as done or not done. Task numbers are one-based.

### Delete a task

```text
delete 3
```

Removes the selected task from the list.

### Update a task

```text
update 1 /description read reference book
update 2 /by 2026-10-15
update 3 /description project meeting /from Mon 2pm /to 4pm
```

Updates one or more fields without changing the task's position or done
status. The **Edit task** form generates the same command format.

Use `/description` for any task, `/by` for deadlines, and `/from` plus `/to`
for events. Multiple fields can be supplied in one command. Extra spaces around
date markers are accepted.

### Exit Kia

```text
bye
```

Displays a goodbye message and exits the application.

## GUI tips

Kia shows user messages on the right and responses on the left. Long responses
can be read using the conversation scroll area. Errors are highlighted in red.
The **Edit task** form disables fields that do not apply to the selected task
type and saves through the same validation as a typed `update` command.

## Validation and persistence

Blank values, invalid task numbers, duplicate update fields, incompatible
fields, missing event markers, and invalid deadline dates are rejected without
changing the task list. Tasks are saved after additions, edits, deletions, and
status changes in `data/kia.txt`, then loaded at startup.
