# Della User Guide

Della is a desktop chatbot that helps you keep track of tasks. You can add to-do items, deadlines, and events, then view, search, complete, or delete them using simple commands.

![Della user interface](Ui.png)

## Quick start

1. Ensure that Java 25 is installed on your computer.
2. Download the latest `della.jar` from the project's GitHub release.
3. Open a terminal in the folder containing the JAR.
4. Start Della with:

   ```powershell
   java -jar della.jar
   ```
5. Type a command in the input box and press **Enter**.

The first message from Della is:

```text
Hi! I'm Della :))
How can I help you?
```

## Command format

- `DESCRIPTION` is the text describing your task.
- `INDEX` is the number shown beside a task. It starts at `1`.
- Dates use `dd/MM/yyyy HHmm` and 24-hour time. For example, `31/12/2026 1830` means 31 December 2026 at 6:30 PM.
- Keywords such as `/by`, `/from`, and `/to` must be typed exactly as shown.

## Features

### Adding a to-do: `todo`

Adds a task without a date or time.

Format: `todo DESCRIPTION`

Example: `todo buy groceries`

### Adding a deadline: `deadline`

Adds a task that must be completed by a specified date and time. The deadline must not be in the past.

Format: `deadline DESCRIPTION /by DATE_TIME`

Example: `deadline submit report /by 31/12/2026 1830`

### Adding an event: `event`

Adds a task between a start and end date and time. Both times must not be in the past, and the end time must not be earlier than the start time.

Format: `event DESCRIPTION /from START_DATE_TIME /to END_DATE_TIME`

Example: `event project meeting /from 20/12/2026 1400 /to 20/12/2026 1530`

### Listing all tasks: `list`

Shows all tasks in the order they were added. Each task has a number used by `mark`, `unmark`, and `delete`.

Format: `list`

Example output:

```text
1. [T][ ] buy groceries
2. [D][ ] submit report (by Dec 31 2026 6:30pm)
```

<!-- Add a screenshot of a populated task list here. -->

### Finding tasks: `find`

Searches task descriptions for one exact keyword. The keyword must match a whole word, including its capitalisation.

Format: `find KEYWORD`

Example: `find report`

Matching tasks are displayed as a numbered list. The numbers shown by `find`
are for viewing the results; use the corresponding number from the full `list`
when running `delete`, `mark`, or `unmark`.

### Marking a task as done: `mark`

Marks the task at the specified index as completed, changing `[ ]` to `[X]`.

Format: `mark INDEX`

Example: `mark 1`

### Marking a task as not done: `unmark`

Marks a completed task as incomplete.

Format: `unmark INDEX`

Example: `unmark 1`

### Deleting a task: `delete`

Permanently removes the task at the specified index. The remaining tasks are renumbered.

Format: `delete INDEX`

Example: `delete 2`

### Exiting Della: `bye`

Closes the application.

Format: `bye`

Della responds with `Byee! Rest well!`.

## Data storage

Della saves tasks automatically after adding, marking, unmarking, or deleting them. Task data is stored in `data/della.txt` relative to the application's working directory and is loaded the next time Della starts.

## Troubleshooting

- Use Java 25 to build and run Della.
- Write commands in lowercase.
- Use positive task numbers shown by `list` or `find`.
- Follow the `dd/MM/yyyy HHmm` date-time format.
- A deadline or event cannot use a date-time in the past.
- If Della displays an error, correct the command and try again.

## Command summary

| Action | Format | Example |
| --- | --- | --- |
| Add to-do | `todo DESCRIPTION` | `todo buy groceries` |
| Add deadline | `deadline DESCRIPTION /by DATE_TIME` | `deadline submit report /by 31/12/2026 1830` |
| Add event | `event DESCRIPTION /from START /to END` | `event meeting /from 20/12/2026 1400 /to 20/12/2026 1530` |
| List tasks | `list` | `list` |
| Find tasks | `find KEYWORD` | `find report` |
| Mark done | `mark INDEX` | `mark 1` |
| Mark not done | `unmark INDEX` | `unmark 1` |
| Delete task | `delete INDEX` | `delete 2` |
| Exit | `bye` | `bye` |
