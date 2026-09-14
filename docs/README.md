# Penguin User Guide

Penguin is a desktop task manager featuring Pip, a cheerful productivity
penguin. It combines a command-line style input box with a graphical chat
interface, allowing you to capture and manage tasks quickly without navigating
through menus.

- [Quick start](#quick-start)
- [Features](#features)
  - [Adding a to-do: `todo`](#adding-a-to-do-todo)
  - [Adding a deadline: `deadline`](#adding-a-deadline-deadline)
  - [Adding an event: `event`](#adding-an-event-event)
  - [Listing all tasks: `list`](#listing-all-tasks-list)
  - [Marking a task as complete: `mark`](#marking-a-task-as-complete-mark)
  - [Marking a task as incomplete: `unmark`](#marking-a-task-as-incomplete-unmark)
  - [Deleting a task: `delete`](#deleting-a-task-delete)
  - [Finding tasks by description: `find`](#finding-tasks-by-description-find)
  - [Finding tasks on a date: `on`](#finding-tasks-on-a-date-on)
  - [Undoing a change: `undo`](#undoing-a-change-undo)
  - [Exiting Penguin: `bye`](#exiting-penguin-bye)
  - [Saving data](#saving-data)
- [FAQ](#faq)
- [Known limitations](#known-limitations)
- [Command summary](#command-summary)

## Quick start

1. Ensure that Java 25 or later is installed on your computer.
2. Download `penguin.jar` from the latest release on the
   [Penguin repository](https://github.com/BrianLim35/ip/releases).
3. Place the JAR file in the folder you want Penguin to use as its home folder.
4. Open a terminal in that folder and run:

   ```shell
   java -jar penguin.jar
   ```

5. The Penguin GUI should appear:

   ![Penguin GUI](Ui.png)

6. Type a command in the message box and press <kbd>Enter</kbd> or select
   **Waddle!** to run it.

Some commands to try:

- `todo Read chapter 3`
- `deadline Submit report /by 2099-12-01 2359`
- `event Project meeting /from 2099-12-02 1400 /to 2099-12-02 1600`
- `list`

The suggestion buttons above the message box can also start common commands.
**What's on my iceberg?** runs `list` immediately; the other buttons insert a
command template that you can complete before submitting.

## Features

### Command format

The following conventions are used throughout this guide:

- Words in `UPPER_CASE` are values that you supply.
- An `INDEX` is the positive task number displayed by `list`.
- Dates use `yyyy-MM-dd`, such as `2099-12-01`.
- Times use the 24-hour `HHmm` format, such as `0900` or `1730`.
- Commands and `find` searches are case-insensitive.
- Leading, trailing, and repeated spaces are accepted and normalized.
- Task descriptions cannot contain the `|` character because Penguin reserves
  it for data storage.
- Commands such as `list`, `undo`, and `bye` do not accept extra arguments.

> **Note:** Examples use future dates so that they remain valid when entered
> before those dates. Penguin rejects deadlines whose dates have passed and
> events whose end dates have passed.

### Adding a to-do: `todo`

Adds a task that has no associated date or time.

Format: `todo DESCRIPTION`

Example: `todo Read chapter 3`

Penguin adds the task to the bottom of the list. A to-do is displayed with the
`[T]` type marker.

### Adding a deadline: `deadline`

Adds a task that must be completed by a specific date and time.

Format: `deadline DESCRIPTION /by DATE TIME`

Example: `deadline Submit report /by 2099-12-01 2359`

- Exactly one `/by` separator is required.
- Both a description and a date-time value are required.
- The deadline date cannot be before the current date.
- A deadline is displayed with the `[D]` type marker.

### Adding an event: `event`

Adds a task that occurs over a period of time.

Format: `event DESCRIPTION /from START_DATE START_TIME /to END_DATE END_TIME`

Example:
`event Project meeting /from 2099-12-02 1400 /to 2099-12-02 1600`

- Exactly one `/from` and one `/to` separator are required, in that order.
- A description, start date-time, and end date-time are required.
- The start must be before the end.
- The event's end date cannot be before the current date.
- An event is displayed with the `[E]` type marker.

### Listing all tasks: `list`

Displays every task in insertion order with its current task number.

Format: `list`

The status marker `[ ]` indicates an incomplete task, while `[X]` indicates a
completed task.

### Marking a task as complete: `mark`

Marks the task at the specified index as complete.

Format: `mark INDEX`

Example: `mark 2`

Use `list` first if you are unsure of the task's current index.

### Marking a task as incomplete: `unmark`

Marks the task at the specified index as incomplete.

Format: `unmark INDEX`

Example: `unmark 2`

### Deleting a task: `delete`

Permanently removes the task at the specified index from the current list.

Format: `delete INDEX`

Example: `delete 3`

The remaining tasks are renumbered after deletion.

### Finding tasks by description: `find`

Displays tasks whose descriptions contain the supplied keyword or phrase.

Format: `find KEYWORD`

Examples:

- `find report`
- `find project meeting`

The match is a case-insensitive substring match. For example, `find report`
matches both `Submit report` and `Submit Report`.

The number beside each result is its index in the complete task list. You can
use that number directly with `mark`, `unmark`, or `delete`.

### Finding tasks on a date: `on`

Displays deadlines on the specified date and events that span that date.
To-dos are not included because they have no date.

Format: `on DATE`

Example: `on 2099-12-02`

The date must be a real calendar date in `yyyy-MM-dd` format.

As with `find`, each result retains its index from the complete task list, so
the displayed number can be used with an index-based command.

### Undoing a change: `undo`

Reverses the most recent `todo`, `deadline`, `event`, `mark`, `unmark`, or
`delete` operation.

Format: `undo`

- Up to five changes can be undone during the current session.
- `list`, `find`, and `on` do not create undoable changes.
- Undo history is not restored after Penguin is restarted.

### Exiting Penguin: `bye`

Closes the application.

Format: `bye`

Any successful changes made before exiting have already been saved.

### Saving data

Penguin automatically saves after every successful command that changes the
task list. No manual save command is required.

Data is stored at `data/penguin.txt`, relative to the folder from which the JAR
is run. Penguin creates the folder and file when necessary. To transfer your
tasks to another computer, close Penguin and copy this file into the other
installation's `data` folder.

> **Warning:** Editing `penguin.txt` manually can corrupt individual records.
> Back up the file before editing it. When Penguin encounters a malformed
> record, it displays a warning, skips that record, and continues loading other
> valid records. A skipped record is not kept in memory. The next successful
> command that changes the task list rewrites the file using only the valid
> loaded records, permanently removing any skipped records from the file.

## FAQ

**Q: Why does Penguin say that my task index is invalid?**

A: Run `list` and use one of the positive task numbers currently displayed.
Indexes can change after a task is deleted.

**Q: Why is my deadline or event date rejected?**

A: Check that the date-time follows `yyyy-MM-dd HHmm`, represents a real date
and time, and is not disallowed for being in the past.

**Q: Why does `find` not return a task that appears to match?**

A: Search is case-insensitive, but the complete keyword or phrase must appear
within the task description. Try a shorter search term if necessary.

**Q: How do I transfer my tasks to another computer?**

A: Close Penguin, copy `data/penguin.txt`, and place it in the `data` folder
beside the JAR on the other computer.

## Known limitations

1. Undo history lasts only for the current session and stores at most five
   changes.
2. Two tasks with identical details can currently be added.

## Command summary

| Action | Format | Example |
|---|---|---|
| Add a to-do | `todo DESCRIPTION` | `todo Read chapter 3` |
| Add a deadline | `deadline DESCRIPTION /by DATE TIME` | `deadline Submit report /by 2099-12-01 2359` |
| Add an event | `event DESCRIPTION /from START_DATE START_TIME /to END_DATE END_TIME` | `event Meeting /from 2099-12-02 1400 /to 2099-12-02 1600` |
| List tasks | `list` | `list` |
| Mark complete | `mark INDEX` | `mark 2` |
| Mark incomplete | `unmark INDEX` | `unmark 2` |
| Delete | `delete INDEX` | `delete 3` |
| Find by description | `find KEYWORD` | `find report` |
| Find by date | `on DATE` | `on 2099-12-02` |
| Undo | `undo` | `undo` |
| Exit | `bye` | `bye` |
