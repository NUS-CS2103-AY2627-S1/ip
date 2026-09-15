# Penny User Guide

Penny is a task-tracking chatbot. Keep to-dos, deadlines, and events in one
place, find what you need, and mark tasks off as you finish them.

## Getting started

1. Install **Java 25** and open a terminal in the project folder.
2. Launch Penny with `./gradlew runGui` (macOS/Linux) or
   `.\gradlew.bat runGui` (Windows PowerShell). The first run needs internet
   access to download dependencies.
3. Type `todo read book` in the input box and press **Enter** or click **Send**.
4. Type `list` to see your tasks.

Prefer a terminal? Replace `runGui` with `runCli`; the same commands work there.

## Command basics

- Replace uppercase placeholders such as `DESCRIPTION` with your own text.
  Do not type the placeholder names or the optional square brackets.
- Enter one command at a time. Command names ignore capitalization; surrounding
  spaces and extra spaces between parameters are accepted.
- Descriptions must be non-empty and unique across **all task types**.
  Changing only a task's dates does not make it unique. Description comparisons
  are case-sensitive: `Read book` and `read book` are different.
- Write dates as `ddMMyyyy`, optionally followed by a 24-hour time `HHmm`:
  `15092026` means 15 September 2026; `15092026 1800` means 6 pm that day.
  An omitted time means midnight. Dates must exist, so `30022026` is invalid.
- Use lowercase `/by`, `/from`, and `/to` as separate tokens, exactly once and
  in the order shown. For dated tasks, tokens starting with `/` are reserved
  for parameters; URLs and slashes within words are fine in descriptions.

## Features

### Add a to-do: `todo`

Create a task without a date.

**Format:** `todo DESCRIPTION`

**Example:** `todo read book`

Penny replies: `Added: [T][ ] read book`

### Add a deadline: `deadline`

Create a task with a due date and optional time.

**Format:** `deadline DESCRIPTION /by DATE [TIME]`

**Example:** `deadline submit report /by 15092026 1800`

Penny replies: `Added: [D][ ] submit report (by: 15 09 2026 1800)`

### Add an event: `event`

Create a task with a start and end. The end must be **later than** the start.

**Format:** `event DESCRIPTION /from DATE [TIME] /to DATE [TIME]`

**Example:** `event team meeting /from 15092026 1400 /to 15092026 1500`

Penny adds the event and shows both times. Events may span multiple days.

### View all tasks: `list`

Type `list` with no arguments to see your numbered tasks, including completed
ones. Starting with an empty list, adding the three examples above gives:

```text
1. [T][ ] read book
2. [D][ ] submit report (by: 15 09 2026 1800)
3. [E][ ] team meeting (from: 15 09 2026 1400 | to: 15 09 2026 1500)
```

`T`, `D`, and `E` mean to-do, deadline, and event. `[ ]` means unfinished;
`[X]` means done. Penny tells you if the list is empty.

### Mark or unmark a task: `mark`, `unmark`

**Formats:** `mark NUMBER` and `unmark NUMBER`

Use the task's displayed number, starting from 1. For example, `mark 1` changes
the first task to `[T][X] read book`; `unmark 1` makes it unfinished again.
Penny reports an error if it already has the requested status.

### Find tasks: `find`

**Format:** `find TEXT`

**Example:** `find report` shows tasks containing `report`.

Search is case-sensitive and matches a continuous piece of the displayed task,
including its description, dates, and status. Multiple words form one search
phrase. Penny tells you when nothing matches.

### Find tasks on a date: `due`

**Format:** `due DATE [TIME]`

**Example:** `due 15092026` shows deadlines due that day and events **starting**
that day, including completed tasks. To-dos and events starting on other days
are excluded. Any supplied time is ignored when matching the calendar date.

Both `find` and `due` keep the original task numbers, so you can use the numbers
shown in their results with `mark`, `unmark`, or `delete`.

### Delete a task: `delete`

**Format:** `delete NUMBER`

**Example:** `delete 2` removes task 2 immediately. There is no undo command.
Run `list` afterwards to check the remaining tasks' new numbers.

### Exit: `bye`

Type `bye` with no arguments. Penny says goodbye and closes the window shortly
afterwards, or ends the terminal session.

## Saving and troubleshooting

Penny automatically saves successful task changes in `commands.txt` in the
folder you launch it from and reloads them next time. Use the same folder each
time. Keep a backup of this file; normally, you do not need to edit it.

| Problem | What to do |
| --- | --- |
| Invalid command or missing parameter | Check the formats above and enter the command again. |
| Task number is out of bounds | Run `list` and use a number that appears there. |
| Duplicate task | Choose a different description or use the existing task. |
| Invalid date or event range | Check the date format and ensure the event ends after it starts. |
| Tasks cannot be saved | Check folder/file permissions and free disk space, then retry. The failed change is not applied. |
| Startup warning about saved data | Back up `commands.txt`, fix the reported file or line problem, and restart Penny. |

If saved data cannot be read, or a saved command is invalid, Penny disables task
changes to protect the file. Only tasks before the invalid line are loaded;
queries and `bye` remain available. Missing save files are created automatically.
