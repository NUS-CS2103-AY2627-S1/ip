# Pebby User Guide

**Pebby** is a simple task-planning chatbot for students. Use it to remember homework, assignment deadlines, and school events in one place. Type a command in the message box, then press <kbd>Enter</kbd> or click **Send**.

## Quick start

1. Install Java 25 or later. To check whether it is installed, open Terminal (macOS) or Command Prompt (Windows), type `java -version`, and press <kbd>Enter</kbd>. You should see a version number beginning with `25` or later.
2. Download `pebby.jar` from the [latest Pebby release](https://github.com/0218hy/ip/releases/latest).
3. Save the file in a folder you can find again, such as `Documents/Pebby`.
4. Double-click `pebby.jar`. Pebby's chat window should open.

If double-clicking does not open Pebby, open Terminal (macOS) or Command Prompt (Windows), go to the folder where you saved the file, and run:

```text
java -jar pebby.jar
```

Try these commands after Pebby opens:

```text
todo finish tutorial
deadline submit assignment /by 25 September 2026
list
```

## Features

### Command format

- Text in `UPPER_CASE` is information you replace. For example, in `todo TASK`, replace `TASK` with something like `finish tutorial`.
- Type one space between each part of a command. Pebby does not accept extra spaces or tabs.
- Dates may be written as `yyyy-MM-dd` (for example, `2026-09-25`) or `d MMMM yyyy` (for example, `25 September 2026`).
- A **task number** is the number shown by `list`, starting at 1. Use it with `mark`, `unmark`, and `delete`.

### Viewing the command list: `help`

Shows a quick reminder of all available commands.

Format: `help`

### Adding a task: `todo`

Adds a task without a date. This is useful for everyday work you simply want to remember.

Format: `todo TASK`

Example: `todo revise lecture notes`

### Adding a deadline: `deadline`

Adds a task that must be completed by a particular date.

Format: `deadline TASK /by DATE`

Example: `deadline submit lab report /by 25 September 2026`

### Adding an event: `event`

Adds an event that lasts from one date through another. The start date must be earlier than the end date.

Format: `event EVENT /from START_DATE /to END_DATE`

Example: `event study break /from 14 September 2026 /to 18 September 2026`

### Viewing all tasks: `list`

Shows every task and its task number. A task with `[X]` is complete; `[ ]` means it is still to do.

Format: `list`

### Marking a task as done: `mark`

Marks the numbered task as complete.

Format: `mark TASK_NUMBER`

Example: `mark 2`

Tip: Run `list` first if you need to check a task's number.

### Marking a task as not done: `unmark`

Changes a completed task back to incomplete.

Format: `unmark TASK_NUMBER`

Example: `unmark 2`

### Finding a task: `find`

Shows tasks whose descriptions contain the keyword you provide. Matching is case-sensitive: `Math` and `math` are different.

Format: `find KEYWORD`

Example: `find assignment`

### Viewing a day's plan: `schedule`

Shows deadlines due on a date and events occurring on that date. Events that span several days appear on every date they cover. Incomplete tasks appear before completed ones.

Format: `schedule DATE`

Example: `schedule 25 September 2026`

### Deleting a task: `delete`

Permanently removes the numbered task from Pebby.

Format: `delete TASK_NUMBER`

Example: `delete 3`

Tip: Use `list` first to make sure you delete the intended task.

### Saving your tasks

Pebby saves your changes automatically after you add, mark, unmark, or delete a task. You do not need a save command. Keep Pebby's folder if you move to another computer so you can take your saved tasks with you.

### Closing Pebby: `bye`

Closes the chatbot.

Format: `bye`

## Command summary

| Action            | Command                                       |
| ----------------- | --------------------------------------------- |
| Show help         | `help`                                      |
| Add a task        | `todo TASK`                                 |
| Add a deadline    | `deadline TASK /by DATE`                    |
| Add an event      | `event EVENT /from START_DATE /to END_DATE` |
| View all tasks    | `list`                                      |
| Complete a task   | `mark TASK_NUMBER`                          |
| Reopen a task     | `unmark TASK_NUMBER`                        |
| Find tasks        | `find KEYWORD`                              |
| View a day's plan | `schedule DATE`                             |
| Delete a task     | `delete TASK_NUMBER`                        |
| Close Pebby       | `bye`                                       |
