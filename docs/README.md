# Sage User Guide

![Sage graphical interface](Ui.png)

Sage is a friendly command-line task manager for todos, deadlines, and events.
It helps you keep track of tasks, mark them as completed, search for them, and
continue where you left off because your data is saved automatically.

## Table of Contents

- [Quick start](#quick-start)
- [Features](#features)
- [Saving the data](#saving-the-data)
- [FAQ](#faq)
- [Known issues](#known-issues)
- [Command summary](#command-summary)

## Quick start

Ensure that Java 25 or later is installed on your computer.

Download the latest `sage.jar` file from the [Sage Releases page](https://github.com/Sia-Pathania/ip/releases).

Open a terminal or command prompt and change to the folder where you downloaded
the JAR file.

On macOS or Linux:

```bash
cd ~/Downloads
java -jar sage.jar
```

On Windows PowerShell:

```powershell
cd $HOME\Downloads
java -jar sage.jar
```

If the JAR file has a different name, replace `sage.jar` in the command with
the actual filename. You can also run Sage from IntelliJ or with the project's
Gradle run task.

Sage will start and wait for a command. Type a command and press Enter.

Some example commands you can try:

```text
todo return library books
deadline submit assignment /by 25/12/2025 2359
event team meeting /from 25/12/2025 1400 /to 25/12/2025 1530
list
mark 1
bye
```

Refer to the [Features](#features) section below for details of each command.

## Features

### Notes about the command format

- Words in `UPPER_CASE` are parameters supplied by the user.
- `DATE` can be written as `d/M/yyyy` or `yyyy-MM-dd`.
- A date and time can be written as `d/M/yyyy HHmm` or `yyyy-MM-dd HHmm`.
- `HHmm` uses the 24-hour format and cannot be entered without a date.
- Task numbers are positive integers starting from `1`, referring to the order
  shown by `list`.
- Commands are case-sensitive and should be entered in lowercase.

### Adding a todo: `todo`

Adds a task without a date or time.

**Format:** `todo DESCRIPTION`

**Example:** `todo return library books`

### Adding a deadline: `deadline`

Adds a task that must be completed by a specified date or date and time.

**Format:** `deadline DESCRIPTION /by DATE [HHmm]`

**Examples:**

```text
deadline submit assignment /by 25/12/2025
deadline submit assignment /by 25/12/2025 2359
```

A deadline must contain exactly one `/by` field. A date-only deadline is due at
the end of that day.

### Adding an event: `event`

Adds a task that occurs between a start and an end date or date and time.

**Format:** `event DESCRIPTION /from DATE [HHmm] /to DATE [HHmm]`

**Example:** `event team meeting /from 25/12/2025 1400 /to 25/12/2025 1530`

An event must contain exactly one `/from` field and one `/to` field. The end
must be later than the start. Date-only boundaries cover the full day.

### Listing all tasks: `list`

Shows all tasks in their current order.

**Format:** `list`

### Finding tasks: `find`

Shows tasks whose descriptions contain the given keyword.

**Format:** `find KEYWORD`

The search is case-sensitive, so `find Books` and `find books` can return
different results.

### Finding tasks on a date: `on`

Shows deadlines and events that occur on a specified date.

**Format:** `on DATE`

An event is shown for every date between its start and end dates, inclusive.

### Finding duplicate tasks: `duplicates`

Reports groups of duplicate tasks.

**Format:** `duplicates`

Duplicate detection ignores case and extra whitespace in descriptions. For
deadlines and events, the date or time range must also match.

### Marking a task as done: `mark`

Marks the specified task as completed.

**Format:** `mark TASK_NUMBER`

**Example:** `mark 1`

### Unmarking a task: `unmark`

Changes a completed task back to not completed.

**Format:** `unmark TASK_NUMBER`

**Example:** `unmark 1`

### Deleting a task: `delete`

Permanently removes the specified task.

**Format:** `delete TASK_NUMBER`

**Example:** `delete 1`

### Exiting the program: `bye`

Exits Sage.

**Format:** `bye`

## Saving the data

Sage automatically saves each successfully added, updated, or deleted task. You
do not need to save manually.

## FAQ

**Q: How do I know which task number to use?**
**A:** Enter `list` first and use the number shown beside the task.

**Q: Can I use a time without a date?**
**A:** No. A time must always be entered together with a date.

**Q: Can I use `25-12-2025` as a date?**
**A:** No. Use `25/12/2025` or `2025-12-25` instead.

## Known issues

- Commands are case-sensitive and must be typed in lowercase.
- A deadline must contain exactly one `/by` field.
- An event must contain exactly one `/from` field and one `/to` field.
- Invalid dates, times, empty descriptions, and incomplete date-time fields are
  rejected.

## Command summary

| Action | Format | Example |
| --- | --- | --- |
| Add todo | `todo DESCRIPTION` | `todo return library books` |
| Add deadline | `deadline DESCRIPTION /by DATE [HHmm]` | `deadline submit assignment /by 25/12/2025 2359` |
| Add event | `event DESCRIPTION /from DATE [HHmm] /to DATE [HHmm]` | `event team meeting /from 25/12/2025 1400 /to 25/12/2025 1530` |
| List | `list` | `list` |
| Find | `find KEYWORD` | `find books` |
| Find by date | `on DATE` | `on 25/12/2025` |
| Find duplicates | `duplicates` | `duplicates` |
| Mark done | `mark TASK_NUMBER` | `mark 1` |
| Unmark | `unmark TASK_NUMBER` | `unmark 1` |
| Delete | `delete TASK_NUMBER` | `delete 1` |
| Exit | `bye` | `bye` |
