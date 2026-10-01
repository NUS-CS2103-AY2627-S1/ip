# Bany User Guide

**Bany** is a simple task manager that you control by typing commands. Enter a command in the box at the bottom of the window, then press <kbd>Enter</kbd> or select **Send**. Bany saves changes automatically.

## Getting started

Try these commands first:

```text
todo buy groceries
deadline submit report /by 25-09-2026 23:59
list
```

Commands and tag names are not case-sensitive. Dates and times must use the 24-hour format `dd-MM-yyyy HH:mm`, for example `05-10-2026 09:30`.

Use real calendar dates: `31-02-2026 09:30` is invalid. Years must have four digits and be between `0001` and `9999`.

Tag names must not contain `command` or `description`, ignoring case. For example, `/command`, `/subcommand`, and `/taskDescription` are rejected. These words are allowed in task descriptions and tag values.

## Manage tasks

### Add a to-do

Use `todo DESCRIPTION` for a task without a date.

```text
todo call Mum
```

### Add a deadline

Use `deadline DESCRIPTION /by DATE TIME` for a task due at a particular time.

```text
deadline pay rent /by 01-10-2026 18:00
```

### Add an event

Use `event DESCRIPTION /from DATE TIME /to DATE TIME` for something with a start and end time.

```text
event project meeting /from 03-10-2026 14:00 /to 03-10-2026 15:00
```

The event must end after it starts. Keep `/from` before `/to` to avoid a warning.

### View, find, and complete tasks

Use `list` to see every task. Each task has a number starting from 1; use that number when marking, rescheduling, or deleting it.

```text
list
```

Use `find WORDS` to show tasks whose descriptions contain that text. Searches are case-sensitive. For example, `find report` matches `submit report` but not `submit Report`. Multiple words are matched as one continuous phrase, so `find project meeting` searches for that exact phrase within each description.

Search results keep the numbers from the full task list. Use those numbers to mark, reschedule, or delete a match. Numbers can change after deleting a task, so run `list` or `find` again to see the current numbers.

```text
find report
```

Use `mark NUMBER` when a task is done, and `unmark NUMBER` if it needs to become active again. `m` and `unm` are shorter alternatives.

```text
mark 2
unmark 2
```

### Change or remove a task

Use `reschedule NUMBER` followed by the tag you want to change. For a deadline, use `/by`; for an event, use `/from`, `/to`, or both. `resched` is a shorter alternative.

```text
reschedule 2 /by 02-10-2026 18:00
reschedule 3 /from 04-10-2026 14:00 /to 04-10-2026 15:00
```

Use `delete NUMBER` to permanently remove a task. `del` is a shorter alternative.

```text
delete 4
```

## Finish

Use `bye` to close Bany.

```text
bye
```

Your tasks are stored automatically in `data/bany.txt`, relative to the directory from which Bany runs. Start Bany from the same directory each time to use the same task file. If Bany does not understand a command, check its spelling and format, then try again.

## Command summary

Command | What it does | Example
--- | --- | ---
`todo DESCRIPTION` | Adds a to-do | `todo read chapter 3`
`deadline DESCRIPTION /by DATE TIME` | Adds a deadline | `deadline submit work /by 10-10-2026 17:00`
`event DESCRIPTION /from DATE TIME /to DATE TIME` | Adds an event | `event tutorial /from 11-10-2026 10:00 /to 11-10-2026 11:00`
`list` | Shows all tasks | `list`
`find WORDS` | Searches task descriptions | `find tutorial`
`mark NUMBER` / `unmark NUMBER` | Marks a task done or active | `mark 1`
`reschedule NUMBER /TAG VALUE` | Changes a task's date or tag | `reschedule 1 /by 12-10-2026 17:00`
`delete NUMBER` | Removes a task | `delete 1`
`bye` | Closes Bany | `bye`
