# Tasque User Guide

Tasque is a lightweight personal task manager for keeping Todos, Deadlines, and
Events in one place. Type short commands to add tasks, track progress, and find
what needs doing.

![Tasque GUI](Ui.png)

The Tasque icon was generated specifically for this project using OpenAI/ChatGPT.

## Quick start

Install Java 25 using the
[installation guide for your operating system](https://se-education.org/guides/tutorials/javaInstallation.html).
Download `tasque.jar` from this repository's
[GitHub Releases page](https://github.com/HIIMHEY/ip/releases) and place it in a
folder of your choice. Open a terminal in that folder and run
`java -jar tasque.jar`.

1. Type a command in the input field, then press **Enter** or click **Send**.
2. Try `todo Review CS2103T Week 6 notes`, then `list`.
3. Use the task numbers from `list` with `mark`, `unmark`, and `delete`.

Commands and aliases are case-sensitive: use `list`, not `List`. Surrounding
whitespace is accepted. Separate commands and arguments with spaces, not tabs.
Replace placeholders such as `DESCRIPTION` and `TASK_NUMBER` with your own values.

## Commands

### Add Todo

Add a task without a date.

Format: `todo DESCRIPTION` · Alias: `t`

Example: `todo Review CS2103T Week 6 notes`

Tasque adds an incomplete Todo and shows the new task count.

### Add Deadline

Add a task with a due date.

Format: `deadline DESCRIPTION /by yyyy-MM-dd` · Alias: `d`

Example: `deadline Submit CS2103T iP /by 2026-09-18`

Tasque adds an incomplete Deadline due on 18 September 2026. Dates must be valid
calendar dates in `yyyy-MM-dd` format; times and words such as `tomorrow` are not
accepted. Reserve `/by` for the due-date marker.

### Add Event

Add a task with start and end dates.

Format: `event DESCRIPTION /from yyyy-MM-dd /to yyyy-MM-dd` · Alias: `e`

Example: `event Team project discussion /from 2026-09-17 /to 2026-09-17`

Tasque adds an incomplete Event with the specified dates. Use valid dates in
`yyyy-MM-dd` format. The end cannot be before the start; same-day Events are
allowed. Reserve `/from` and `/to` for the date markers, in that order.

### List tasks

Show all tasks, including completed ones, in the order they were added.

Format and example: `list` · Alias: `l`

Numbers start at 1. `[T]`, `[D]`, and `[E]` mean Todo, Deadline, and Event.
`[ ]` means incomplete; `[X]` means completed. For example:

```text
1.[T][X] Review CS2103T Week 6 notes
```

### Mark a task complete

Format: `mark TASK_NUMBER` · Alias: `m`

Example: `mark 1`

Marks task 1 from the full `list` as completed (`[X]`). The task stays in the list.

### Reopen a task

Format: `unmark TASK_NUMBER` · Alias: `u`

Example: `unmark 1`

Marks task 1 from the full `list` as incomplete (`[ ]`) again.

### Delete a task

Format: `delete TASK_NUMBER` · Alias: `del`

Example: `delete 1`

Removes task 1 from the full `list`. Later tasks are renumbered, so run `list`
again before choosing another task number.

### Find tasks

Format: `find KEYWORD` · Alias: `f`

Example: `find CS2103T`

Shows tasks whose descriptions contain `CS2103T`. Matching is case-sensitive and
uses the whole keyword or phrase as a substring: `book` matches `notebook`, but
not `Book`. It searches descriptions only, not dates or task/status markers.
Both completed and incomplete matches appear in their original list order.
If nothing matches, Tasque shows just `Matches:` with no tasks below it.

**Find results are numbered separately from 1.** Run `list` to get the correct
task number before using `mark`, `unmark`, or `delete` on a match.

### Exit

Format and example: `bye` · Alias: `b`

Shows a farewell and closes Tasque. Successful task changes are already saved.

## Important behavior

### Invalid input

Descriptions and search keywords must not be empty. Task numbers must be positive
whole numbers that exist in the full `list`. `list` and `bye` take no arguments.
If Tasque rejects a command, read its error message, correct the input, and try
again; the rejected command does not change your tasks.

### Duplicate tasks

Tasque rejects an exact duplicate of an existing incomplete task with
`Already got that one.` Tasks match only when their type, description (including
capitalization and internal spaces), and any date fields are the same.
Different types, descriptions, or dates make tasks distinct.

Once the matching task is completed, you can add it again, provided no other
incomplete copy exists.

### Data and recovery

Tasque saves automatically after adding, marking, reopening, or deleting a task.
It loads saved tasks when you start it. Data is stored in `data/tasque.txt`
relative to the folder you launch Tasque from; keep launching from that same
folder to use the same list. If no data file exists, Tasque starts with an empty
list.

If an existing file cannot be loaded safely, Tasque explains the problem and
blocks normal commands without overwriting it. Exit with `bye` or `b`, correct
the file or access problem described in the message, and restart Tasque. A
restart is required even after the problem is fixed.

If Tasque reports `I couldn't save your tasks.`, the attempted task change is
undone. Resolve the file-access problem and retry the command.

## Command summary

| Action | Command | Alias |
| --- | --- | --- |
| Add Todo | `todo DESCRIPTION` | `t` |
| Add Deadline | `deadline DESCRIPTION /by yyyy-MM-dd` | `d` |
| Add Event | `event DESCRIPTION /from yyyy-MM-dd /to yyyy-MM-dd` | `e` |
| List tasks | `list` | `l` |
| Complete task | `mark TASK_NUMBER` | `m` |
| Reopen task | `unmark TASK_NUMBER` | `u` |
| Delete task | `delete TASK_NUMBER` | `del` |
| Find tasks | `find KEYWORD` | `f` |
| Exit | `bye` | `b` |
