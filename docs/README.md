# Sine User Guide

Sine is a task-management chatbot. Keep track of things to do, deadlines,
and events by typing commands into its chat window.

![Sine chat window showing a deadline being added, tasks being listed, and a task being marked as done](Ui.png)

## Getting started

You need **Java 25** to run Sine.

If you have `sine.jar`, open a terminal in its folder and run:

```bash
java -jar sine.jar
```

If you have the source project, open a terminal in the project folder and run
`./gradlew run` on macOS/Linux or `.\gradlew.bat run` on Windows. In IntelliJ,
you can also run `sine.Launcher.main()` for the GUI or `sine.Sine.main()` for
the console version.

Type a command into the field at the bottom, then press **Enter** or click
**Send**. Try these commands one at a time:

```text
todo read book
deadline submit report /by 2026-09-20
event team meeting /from Sep 20 2pm /to Sep 20 4pm
list
```

Sine replies after each command. Scroll up to see earlier replies, or resize
the window to give longer messages more room.

## Command rules

- Use lowercase command words, such as `todo` and `list`. `undo` also accepts `UNDO`.
- Replace uppercase placeholders below with your own text; do not type the placeholders.
- Put spaces around `/by`, `/from`, and `/to`. Each parameter may appear only once.
- Descriptions and event times can contain spaces. Quotation marks are unnecessary.
- `list`, `bye`, and `undo` take no arguments.

## Adding tasks

### Todo: a task without a date

Format: `todo DESCRIPTION`

```text
todo borrow a book
```

This adds an incomplete task displayed as `[T][ ] borrow a book`.

### Deadline: a task due on a date

Format: `deadline DESCRIPTION /by YYYY-MM-DD`

```text
deadline submit report /by 2026-09-20
```

This means the report is due on **20 September 2026**. Enter the year first,
then the two-digit month and day, separated by hyphens. Sine displays the date
as `Sep 20 2026`.

Use a real calendar date: `2026-02-30` is invalid. Inputs such as `tomorrow`,
`20/09/2026`, or a date with a time are not supported for deadlines.

### Event: a task with a start and an end

Format: `event DESCRIPTION /from START /to END`

```text
event team meeting /from Sep 20 2pm /to Sep 20 4pm
```

For an event spanning several days:

```text
event study camp /from Sep 20 9am /to Sep 22 5pm
```

Both fields are required, and `/from` must come before `/to`.
**Event dates and times are free text:** Sine stores them as entered. It does
not validate dates or check that the end is later than the start, so include
clear dates and times yourself.

## Viewing and finding tasks

### List all tasks

```text
list
```

Each task has a number, a type (`[T]` todo, `[D]` deadline, or `[E]` event),
and a status (`[ ]` incomplete or `[X]` done). For example:

```text
1.[T][ ] read book
2.[D][X] submit report (by: Sep 20 2026)
```

### Find tasks by description

Format: `find KEYWORD`

```text
find book
```

This finds descriptions containing `book`, including `borrow book` and
`bookshop`. Matching is **case-sensitive**: `Book` and `book` differ. A phrase
such as `find team meeting` must appear together in the description. Search
does not inspect deadline dates or event time fields.

**Run `list` before marking or deleting a task.** Search results are numbered
separately; commands that change tasks always use the full list's numbers.

## Marking and deleting tasks

Use the number shown by the latest `list` response:

| Action | Format | Example |
| --- | --- | --- |
| Mark as done | `mark TASK_NUMBER` | `mark 2` |
| Mark as incomplete | `unmark TASK_NUMBER` | `unmark 2` |
| Delete a task | `delete TASK_NUMBER` | `delete 2` |

Numbers start at **1**. Deleting a task changes the numbers of later tasks,
so run `list` again before your next change. Use `undo` if you delete the
wrong task.

## Undoing task changes

Type `undo` or `UNDO` to reverse the most recent task change in this session.
It supports adding, deleting, marking, and unmarking tasks. A deleted task
returns to its original position with its previous completion status.

For example, after `todo read book`, entering `undo` gives:

```text
Understood. I undid the last task change.
I removed this task:
  [T][ ] read book
```

Repeat `undo` to reverse earlier changes. Listing, searching, invalid commands,
and marking an already completed task do not consume undo history.

There is **no redo**. After an accidental undo, make the change again manually.
History lasts only until the application closes; tasks loaded from a previous
session cannot be undone. There is no configured history limit.

Use exactly `undo` or `UNDO`, without arguments. `Undo` and `undo 2` are invalid.
The console also rejects surrounding spaces for this command; the GUI removes
them before processing input. If no change remains, Sine replies:

```text
Error :( There is nothing to undo.
```

## Saving and closing

Sine automatically saves task changes to `data/sine.txt` relative to the
folder you launched it from, and loads that file when you start again.
Always launch from the same folder to use the same task list. A missing file
starts a new empty list; Sine creates the file when you make a change.

Type `bye` to exit the console version. In the GUI, `bye` displays a farewell
but keeps the window and session open. Use the window's close button to quit.

Use one Sine instance per data file. Back up `data/sine.txt` before editing it
manually, and close Sine first: it does not detect external edits during a session.

## Troubleshooting

| Problem | What to do |
| --- | --- |
| Command is not recognized | Check its spelling and lowercase command word against the examples above. |
| A description, date, or time is missing | Supply every field shown in the command's format. |
| Deadline date is rejected | Use a valid date such as `2026-09-20`, with no time attached. |
| Repeated or reversed parameters | Use `/by` once, or `/from` once followed by `/to` once. |
| Task number is invalid | Run `list` and choose an existing positive whole number. |
| Saved tasks cannot be loaded | Check the launch folder, file permissions, and file contents. Back up the existing file before making changes: Sine starts empty after a load failure, and a later task change can overwrite that file. |
| A change cannot be saved | Check that the data folder is writable. The change remains available only in this session unless a later save succeeds. |

Undo also attempts to save. If saving fails, it still reverses the change in
memory and consumes that history entry. A failed write does not guarantee
that the previous file contents remain intact.
