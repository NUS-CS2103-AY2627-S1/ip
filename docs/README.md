# Xue User Guide

Xue is a sarcastic but helpful task manager for todos, deadlines, and events.
It remembers your tasks between sessions and can help you search, complete,
delete, undo, and redo them.

## Getting started

Launch Xue from the project folder with:

```powershell
.\gradlew.bat run
```

Type a command into the input box and click **Send**, or press **Enter**.

## Commands

### Add a todo

Use a todo for a task without a date.

```text
todo <description>
```

Example:

```text
todo read lecture notes
```

### Add a deadline

Use a deadline for a task that must be completed by a particular date.

```text
deadline <description> /by <date>
```

Examples:

```text
deadline submit report /by Friday
deadline submit report /by 2026-09-30
```

Dates in `yyyy-MM-dd` format are displayed in a readable form. Other date
text, such as `Friday`, is preserved as entered.

### Add an event

Use an event for something with a start and end time.

```text
event <description> /from <start> /to <end>
```

Example:

```text
event team meeting /from 2026-09-25 1400 /to 2026-09-25 1600
```

### View and search tasks

```text
list
find <keyword>
```

`list` displays all tasks. `find` searches task descriptions without
distinguishing between uppercase and lowercase letters.

Example:

```text
find report
```

### Complete, reopen, and delete tasks

Tasks are identified by the numbers shown by `list`.

```text
mark <task number>
unmark <task number>
delete <task number>
```

Examples:

```text
mark 1
unmark 1
delete 2
```

### Undo and redo

```text
undo
redo
```

`undo` reverses the latest successful task-changing command. `redo` reapplies
an undone command. Undo and redo history lasts for the current session and
stores up to 50 changes. Starting a new task-changing command clears the redo
history.

### Exit Xue

```text
bye
```

## Saving tasks

Xue saves changes automatically and reloads them when it starts again. A
missing save file is treated as an empty task list.

## Error handling

Xue reports invalid commands without terminating. For example:

```text
todo
mark abc
deadline submit report
unknown command
```

The error message explains what went wrong so that you can correct the
command and continue using Xue.
