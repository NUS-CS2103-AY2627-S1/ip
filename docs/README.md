# Mochi User Guide

Mochi is a friendly desktop task manager that helps you keep track of todos, deadlines, and events through
simple text commands.

![Mochi GUI](Ui.png)

## Quick start

1. Install Java 25.
2. Download `mochi.jar` and place it in a folder of your choice.
3. Open a terminal in that folder and run:

   ```shell
   java -jar mochi.jar
   ```

4. Enter a command in the text box and press <kbd>Enter</kbd> or select **Send**.

Mochi saves your tasks automatically in a `data` folder inside the folder from which you launch the app. Your
tasks are restored the next time Mochi starts.

## Command reference

Commands and aliases are lowercase. Replace words in `UPPER_CASE` with your own values, without including the
underscores or surrounding labels.

### Add a todo — `todo`

Adds a task without a date or time.

- Format: `todo DESCRIPTION`
- Example: `todo Read CS2103T notes`
- Alias: `t Read CS2103T notes`
- The description cannot be empty.

### Add a deadline — `deadline`

Adds a task that must be completed by a specific date and time.

- Format: `deadline DESCRIPTION /by DATE_TIME`
- Example: `deadline Submit assignment /by 2026-09-20 2359`
- Alias: `d Submit assignment /by 2026-09-20 2359`
- The description and date/time are required.

### Add an event — `event`

Adds a task that takes place between two date-times.

- Format: `event DESCRIPTION /from START_DATE_TIME /to END_DATE_TIME`
- Example: `event Project meeting /from 2026-09-21 1400 /to 2026-09-21 1600`
- Alias: `e Project meeting /from 2026-09-21 1400 /to 2026-09-21 1600`
- The description, start, and end are required. The event must end after it starts.

### Date and time format

Use `yyyy-MM-dd HHmm`, where the time uses the 24-hour clock without a colon.

- Valid: `2026-09-20 2359`
- Invalid: `20/09/2026 11:59 PM`
- Dates and times must exist; for example, `2026-02-30 1200` is rejected.

### List tasks — `list`

Shows every saved task and its current task number.

- Format: `list`
- Example: `list`
- Alias: `l`

### Mark a task — `mark`

Marks a task as complete.

- Format: `mark TASK_NUMBER`
- Example: `mark 1`
- Use a whole number currently shown by `list`.

### Unmark a task — `unmark`

Marks a completed task as incomplete.

- Format: `unmark TASK_NUMBER`
- Example: `unmark 1`
- Use a whole number currently shown by `list`.

### Delete a task — `delete`

Permanently removes a task.

- Format: `delete TASK_NUMBER`
- Example: `delete 2`
- Use a whole number currently shown by `list`. Task numbers may change after deletion.

### Find tasks — `find`

Shows tasks whose descriptions contain a keyword or phrase.

- Format: `find KEYWORD`
- Example: `find assignment`
- Alias: `f assignment`
- Matching is case-sensitive and searches task descriptions only.

### Exit Mochi — `bye`

Closes the application.

- Format: `bye`
- Example: `bye`

## Errors and saved data

Mochi shows an error when a command, task number, or date/time is invalid. If a saved-data line is malformed,
Mochi skips that line, loads the remaining valid tasks, and displays a warning.

## Acknowledgements

- The JavaFX and FXML GUI structure references the
  [SE-EDU JavaFX tutorial](https://se-education.org/guides/tutorials/javaFx.html).
- OpenAI Codex assisted with generating and reviewing automated tests, auditing error handling, and refining this
  User Guide.
