# Daddy User Guide

Daddy is a desktop task manager for people who prefer typing a quick command
over clicking through menus. It keeps todos, deadlines, and events together,
remembers them between sessions, and helps you find free time in your working
week.

![Daddy displaying a realistic task list](Ui.png)

## Quick start

1. Install Java 25.
2. Download `Daddy.jar` from the
   [GitHub releases page](https://github.com/YUJ1N06/ip/releases).
3. Put the JAR file in an empty folder.
4. Open a terminal in that folder and run:

   ```shell
   java -jar "Daddy.jar"
   ```

5. Type a command in the box at the bottom of the window, then press
   <kbd>Enter</kbd> or select **Send**. A good first command is:

   ```text
   todo Read the user guide
   ```

> **Tip:** Daddy gives a specific correction when a command is incomplete or
> invalid. Your task list is not changed when a command fails.

## Understanding command formats

Words in `UPPER_CASE` below are values that you supply:

| Value | Meaning | Example |
| --- | --- | --- |
| `DESCRIPTION` | What the task is about | `return library book` |
| `DATE` | A real date in `dd-MM-yyyy` format | `25-09-2026` |
| `TIME` | A 24-hour time in `HHmm` format | `1800` |
| `INDEX` | The task number shown by `list` | `2` |
| `DURATION` | Whole hours or minutes | `4h` or `90m` |

Daddy accepts extra spaces at the beginning, end, or between words. Task
descriptions cannot contain `|`, which is reserved for the saved-data format.

## Features

### Adding a todo: `todo`

Use a todo for a task without a date or time.

**Format:** `todo DESCRIPTION`

```text
todo Borrow a book
```

### Adding a deadline: `deadline`

Use a deadline for work that must be completed by a date or time.

**Format:** `deadline DESCRIPTION /by DATE [TIME]`

```text
deadline Submit project report /by 25-09-2026 1800
```

The time is optional. If it is omitted, Daddy stores the beginning of that
date (`00:00`). Supply exactly one `/by` value.

### Adding an event: `event`

Use an event for something with a start and an end.

**Format:** `event DESCRIPTION /from DATE TIME /to DATE TIME`

```text
event Team meeting /from 22-09-2026 1400 /to 22-09-2026 1530
```

The end must be later than the start. Put `/from` before `/to` and supply each
parameter exactly once.

### Viewing and finding tasks

| Action | Format | Example |
| --- | --- | --- |
| Show every task | `list` | `list` |
| Show dated tasks | `list on DATE` | `list on 25-09-2026` |
| Search descriptions | `find KEYWORD` | `find report` |

`list on` shows deadlines due on the chosen date and events that start or end
on that date. Todos are not included. `find` performs a case-insensitive,
partial match and does not change the task list.

### Completing, reopening, and deleting tasks

Run `list` first to find the task's current number.

| Action | Format | Example |
| --- | --- | --- |
| Mark completed | `mark INDEX` | `mark 2` |
| Mark not completed | `unmark INDEX` | `unmark 2` |
| Remove permanently | `delete INDEX` | `delete 2` |

Task numbers start at `1`. Deleting a task renumbers the tasks after it, so
run `list` again before using another number.

### Finding free time: `free`

Ask Daddy for the earliest working-hours slot long enough for you.

**Format:** `free DURATION`

```text
free 4h
free 90m
```

The duration must be a positive whole number followed immediately by `h` or
`m`, and cannot exceed 9 hours. Daddy searches using these rules:

- Working hours are **09:00–18:00, Monday–Friday**.
- The search begins at the current local time, rounded up to the next minute.
- Events block time; todos and deadlines do not.
- Completed events still block their scheduled time.
- Overlapping or touching events count as one busy period.

This command only reports a free slot; it does not add or change a task.

### Exiting Daddy: `bye`

Enter `bye` to end the session. The command field and Send button are disabled
after Daddy says goodbye, and you can close the window safely.

## Saving and recovering data

There is no save command. Daddy automatically saves every successful add,
mark, unmark, and delete operation to `data/daddy.txt`, relative to the folder
where you run the JAR.

- A missing data file on the first run is normal; Daddy creates it when needed.
- If part of the file is corrupted, valid tasks still load.
- Daddy copies the original file to `data/daddy.txt.backup` and writes rejected
  records to `data/daddy.txt.corrupt` for inspection.
- If Daddy cannot read or write the file, it explains which path or permission
  needs attention instead of crashing.

> **Important:** Keep `Daddy.jar` and its generated `data` folder together if
> you move the application. The `data` folder contains your saved tasks.

## Common problems

| Problem | What to do |
| --- | --- |
| `java` is not recognized | Install Java 25 and reopen the terminal. |
| A date is rejected | Use a real date in `dd-MM-yyyy` format. |
| A time is rejected | Use 24-hour `HHmm` format, such as `0930` or `1800`. |
| A task number is rejected | Run `list`, then choose a displayed number. |
| Daddy reports a duplicate | Complete or delete the existing task before adding it again. |
| Tasks cannot be saved | Ensure the JAR's folder is writable and check its `data` folder permissions. |

## Command summary

| Command | Purpose |
| --- | --- |
| `todo DESCRIPTION` | Add a todo |
| `deadline DESCRIPTION /by DATE [TIME]` | Add a deadline |
| `event DESCRIPTION /from DATE TIME /to DATE TIME` | Add an event |
| `list` | Show all tasks |
| `list on DATE` | Show deadlines and events on a date |
| `find KEYWORD` | Find tasks by description |
| `free DURATION` | Find the earliest free working-hours slot |
| `mark INDEX` | Mark a task as completed |
| `unmark INDEX` | Mark a task as not completed |
| `delete INDEX` | Delete a task |
| `bye` | End the session |
