# CharlieK User Guide

CharlieK is a lightweight chat-based task manager for keeping track of what you need to do, what is due, and what is happening. It saves every successful change automatically, so your tasks are ready the next time you start it.

![CharlieK in use](Ui.png)

## Start here

1. Check that Java 25 or later is installed:

   ```text
   java -version
   ```

2. From the project folder, run CharlieK:

   macOS or Linux:

   ```text
   ./gradlew run
   ```

   Windows PowerShell:

   ```text
   .\gradlew.bat run
   ```

3. Type a command in the input box and press **Enter** or **Send**. Use `help` if you need a reminder.

On the first run, CharlieK creates `data/charliek.csv` with a small set of sample tasks. Enter `bye` to end the session; the GUI input controls then become disabled.

## What you can do

### Create tasks

CharlieK supports three task types. Replace the values in angle brackets with your own text; do not type the brackets.

#### To-do without a date

```text
todo <description>
```

Example:

```text
todo read the project brief
```

#### Deadline-based task

```text
deadline <description> /by <date/time>
```

Examples:

```text
deadline submit report /by 2026-06-07
deadline review report /by 2/12/2019 1800
```

#### Event with a time span

```text
event <description> /from <date/time> /to <date/time>
```

Example:

```text
event project meeting /from 2026-08-06 14:00 /to 2026-08-06 16:00
```

Both `/from` and `/to` are required. An event that ends before it starts is rejected.

### Browse your task list with `list`

```text
list
```

Shows tasks in the order they were added. Each task has a one-based number that can be used with `mark`, `unmark`, and `delete`.

### Sort by schedule with `list time`

```text
list time
```

Shows deadlines by due date and events by start date. Undated to-dos appear after dated tasks.

### Track completion with `mark` and `unmark`

```text
mark <number>
unmark <number>
```

Examples:

```text
mark 1
unmark 1
```

`mark` changes a task to complete; `unmark` changes it back to incomplete.

### Remove a task with `delete`

```text
delete <number>
```

Example:

```text
delete 2
```

The task is removed and the remaining tasks are renumbered.

### Search descriptions with `find`

```text
find <keyword>
```

Example:

```text
find report
```

The search is case-insensitive and matches any part of a description. Search results are for viewing; run `list` before marking or deleting a result so that you use the task number from the full list.

### Need a reminder? Use `help`

```text
help
help <command>
```

Use `help` to see every command, or for example `help deadline` to see one command's format, description, and example.

### Finish the session with `bye`

```text
bye
```

Your saved tasks remain available the next time CharlieK starts.

## Entering dates and times

CharlieK accepts common date formats, including:

```text
2/12/2019
2019-12-2
2 Dec 2019
```

Times can be written in 24-hour or 12-hour form:

```text
1800
18:00
6pm
6:30 pm
```

A date and time may be separated by a space, a comma, or `T`. Dates are displayed in a readable form such as `7 Jun 2026`; date-times include the time, such as `7 Jun 2026, 18:00`.

## A typical session

```text
todo buy milk
deadline submit report /by 2/12/2019
list time
mark 1
find report
bye
```

## Quick reference

| Goal | Type this | Example |
| --- | --- | --- |
| Add a to-do | `todo <description>` | `todo buy milk` |
| Add a deadline | `deadline <description> /by <date/time>` | `deadline submit report /by 2/12/2019` |
| Add an event | `event <description> /from <date/time> /to <date/time>` | `event meeting /from 2026-08-06 2pm /to 2026-08-06 4pm` |
| List tasks | `list` | `list` |
| Sort by time | `list time` | `list time` |
| Mark complete | `mark <number>` | `mark 1` |
| Mark incomplete | `unmark <number>` | `unmark 1` |
| Delete a task | `delete <number>` | `delete 1` |
| Search descriptions | `find <keyword>` | `find report` |
| Show help | `help [command]` | `help deadline` |
| Exit | `bye` | `bye` |

## Helpful details

- `[T]`, `[D]`, and `[E]` identify to-dos, deadlines, and events.
- `[ ]` means incomplete and `[X]` means complete.
- Commands are lowercase and use one ordinary space between words. Extra spaces, tabs, and line breaks are rejected rather than guessed.
- Descriptions can contain punctuation, including commas and quotation marks.
- There is no save command. Successful additions, status changes, and deletions are saved automatically.
- The data file is `data/charliek.csv`, relative to the folder from which you start CharlieK.
- A missing data file is created with the starter tasks. An existing empty file stays empty.

## When a command fails

| What you see | What to do |
| --- | --- |
| An unknown-command or invalid-format message | Check the command spelling and spacing, then use `help`. |
| A date/time error | Use a supported form such as `2026-06-07` or `2026-06-07 18:00`. |
| A task-number error | Run `list` and use a number from the full task list. |
| A duplicate-task message | Change the description or date; the same task is not added twice. |
| A malformed saved row | CharlieK skips the damaged row and loads valid rows where possible. |
| A file read or save error | Check that `data/charliek.csv` is a regular, readable file and that its folder is writable. |

CharlieK reports an error and continues the session whenever it can. Correct the command or enter `help` and try again.

For build instructions and the end-to-end test plan, see the [project README](../README.md) and [UI test plan](../test/ui-test-plan.md).
