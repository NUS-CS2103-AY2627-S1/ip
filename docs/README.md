# Prism User Guide

Prism is a friendly task-management chatbot. Use the command box in the GUI to add, find, organize, and reschedule your tasks. Prism saves your task list automatically after changes.

![Prism GUI](Ui.png)

## Quick start

1. Start Prism using your IDE or the packaged application.
2. Type a command into the command box.
3. Press **Enter** or click **Send**.
4. Read Prism's response in the conversation area.

Your tasks are stored in `./data/prism.txt`, relative to the folder from which Prism is run. If the file does not exist, Prism creates it automatically.

## Features

### Adding a todo

Adds a task without a date.

**Format:** `todo DESCRIPTION`

**Example:**

```text
todo read chapter 3
```

### Adding a deadline

Adds a task due at a specific date and time.

**Format:** `deadline DESCRIPTION /by DATE_TIME`

**Example:**

```text
deadline submit report /by 2025-12-02 1800
```

Use `yyyy-MM-dd HHmm` or `d/M/yyyy HHmm`. A date without a time is also accepted for deadlines and uses the end of that day.

### Adding an event

Adds a task with a start and end date and time.

**Format:** `event DESCRIPTION /from DATE_TIME /to DATE_TIME`

**Example:** `event project meeting /from 2025-12-03 1400 /to 2025-12-03 1530`

### Viewing all tasks

Displays every task in the order it was added.

**Format:** `list`

### Marking tasks

Use `mark INDEX` to mark a task as completed, or `unmark INDEX` to mark it incomplete again.

**Examples:** `mark 2`, `unmark 2`

Task numbers are the numbers shown by `list`.

### Deleting a task

Permanently removes a task.

**Format:** `delete INDEX`

**Example:** `delete 2`

### Finding tasks

Searches task descriptions without distinguishing uppercase and lowercase letters.

**Format:** `find KEYWORD`

**Example:** `find report`

### Finding tasks on a date

Shows deadlines and events occurring on a date. Date queries accept `yyyy-MM-dd` or `d/M/yyyy`.

**Format:** `date DATE`

**Examples:**

```text
date 2025-12-02
date 2/12/2025
```

### Snoozing a deadline or event

Moves a deadline to a new date and time. For an event, its duration is preserved.

**Format:** `snooze INDEX /to DATE_TIME`

**Example:** `snooze 2 /to 2025-12-05 0900`

Only deadlines and events can be snoozed.

### Exiting Prism

Closes the application.

**Format:** `bye`

## Command summary

| Action | Format |
| --- | --- |
| Add todo | `todo DESCRIPTION` |
| Add deadline | `deadline DESCRIPTION /by DATE_TIME` |
| Add event | `event DESCRIPTION /from DATE_TIME /to DATE_TIME` |
| List tasks | `list` |
| Mark done | `mark INDEX` |
| Mark not done | `unmark INDEX` |
| Delete task | `delete INDEX` |
| Find tasks | `find KEYWORD` |
| Find by date | `date DATE` |
| Snooze task | `snooze INDEX /to DATE_TIME` |
| Exit | `bye` |

## Errors and troubleshooting

- Unknown or malformed commands display an error and leave Prism ready for the next command.
- Task indexes must be positive numbers in the current task list.
- If the data file is missing, Prism creates an empty one when it starts.
- Corrupted data-file entries are skipped while valid entries continue loading.
- Prism saves changes automatically; no separate save command is needed.

