# Clsl User Guide

![Clsl graphical user interface](Ui.png)

Clsl is a desktop task manager for managing to-dos, deadlines, and events. It
uses a chat-style graphical user interface while allowing you to manage your
tasks quickly with simple commands.

## Quick start

Ensure that Java 25 or later is installed on your computer.

Place `clsl.jar` in the folder you want to use as Clsl's home folder. Open a
terminal in that folder and run:

```text
java -jar clsl.jar
```

A GUI similar to the one above should appear in a few seconds. If no saved
data exists, Clsl starts with an empty task list.

Type a command in the message box and press **Enter** or select **Send**. For
example, type `list` to display all your tasks.

Some example commands you can try:

```text
todo read book
deadline submit report /by 2026-09-30
event project meeting /from 2026-09-20 /to 2026-09-21
list
mark 1
```

Refer to the Features section below for details of each command.

## Features

- [Adding a to-do: `todo`](#adding-a-to-do-todo)
- [Adding a deadline: `deadline`](#adding-a-deadline-deadline)
- [Adding an event: `event`](#adding-an-event-event)
- [Listing all tasks: `list`](#listing-all-tasks-list)
- [Finding tasks by description: `find`](#finding-tasks-by-description-find)
- [Finding tasks on a date: `on`](#finding-tasks-on-a-date-on)
- [Marking a task as done: `mark`](#marking-a-task-as-done-mark)
- [Marking a task as not done: `unmark`](#marking-a-task-as-not-done-unmark)
- [Deleting a task: `delete`](#deleting-a-task-delete)
- [Exiting the program: `bye`](#exiting-the-program-bye)

### Notes about the command format

- Words in `UPPER_CASE` are parameters to be supplied by the user. For
  example, in `todo DESCRIPTION`, replace `DESCRIPTION` with text such as
  `read book`.
- Descriptions may contain spaces.
- Dates must use the `yyyy-mm-dd` format and must be valid calendar dates.
- Task numbers refer to the numbers shown by `list`, starting from `1`.
- Commands and their parameters must be separated by spaces. The date markers
  `/by`, `/from`, and `/to` are required for their respective commands.

### Adding a to-do: `todo`

Adds a task without a date.

**Format:** `todo DESCRIPTION`

**Examples:**

```text
todo read book
todo Buy groceries for the week
```

### Adding a deadline: `deadline`

Adds a task that is due on a particular date.

**Format:** `deadline DESCRIPTION /by DATE`

**Examples:**

```text
deadline submit report /by 2026-09-30
deadline renew passport /by 2027-01-15
```

### Adding an event: `event`

Adds a task that occurs over a date range. The start date must be earlier than
or equal to the end date.

**Format:** `event DESCRIPTION /from START_DATE /to END_DATE`

**Examples:**

```text
event project meeting /from 2026-09-20 /to 2026-09-20
event vacation /from 2026-12-24 /to 2026-12-31
```

### Listing all tasks: `list`

Shows all tasks in the task list. Each task is displayed with its task number,
type, completion status, and description. Deadlines and events also show their
dates.

**Format:** `list`

### Finding tasks by description: `find`

Finds tasks whose descriptions contain the supplied keyword. The search is
case-insensitive and searches descriptions only.

**Format:** `find KEYWORD`

**Examples:**

```text
find book
find report
```

### Finding tasks on a date: `on`

Shows deadlines due on the specified date and events that include the
specified date. To-do tasks do not occur on a date and are not included.

**Format:** `on DATE`

**Example:**

```text
on 2026-09-20
```

### Marking a task as done: `mark`

Marks the task at the specified task number as complete. The task is displayed
with `[X]` as its completion status.

**Format:** `mark TASK_NUMBER`

**Example:**

```text
mark 1
```

### Marking a task as not done: `unmark`

Marks the task at the specified task number as incomplete.

**Format:** `unmark TASK_NUMBER`

**Example:**

```text
unmark 1
```

### Deleting a task: `delete`

Deletes the task at the specified task number. The remaining tasks are then
kept in their order and are shown with consecutive task numbers the next time
you use `list`.

**Format:** `delete TASK_NUMBER`

**Example:**

```text
delete 3
```

### Exiting the program: `bye`

Displays a goodbye message and exits the application. In the GUI, the window
closes shortly after the goodbye message is shown.

**Format:** `bye`

## Saving the data

Clsl automatically saves data after every command that changes the task list:
`todo`, `deadline`, `event`, `mark`, `unmark`, and `delete`. You do not need to
save manually.

## Editing the data file

Clsl saves task data automatically in `data/csls.txt`, relative to the folder
from which the application is run. Advanced users may edit this file directly.

Each line represents one task. The first field identifies the task type, the
second field stores completion status (`0` for incomplete or `1` for complete),
and the remaining fields store the description and any date information.

```text
T | 0 | read book
D | 1 | submit report | 2026-09-30
E | 0 | project meeting | 2026-09-20 | 2026-09-20
```

**Caution:** Clsl expects every line in the data file to follow the format
shown above. If the file cannot be read or contains an unknown task type, Clsl
displays a loading error and starts with an empty task list. Other malformed
records, such as records with missing fields or invalid dates, may prevent Clsl
from starting. Back up the file before editing it, and edit it only if you are
confident that the format remains valid.

Clsl rejects duplicate tasks of the same type when their descriptions match
after ignoring capitalization and whitespace. Deadlines must also have the
same due date, and events must also have the same start and end dates. When
duplicate entries are found in an existing data file, Clsl keeps the first
occurrence and removes later occurrences.

## FAQ

**Q: How do I transfer my tasks to another computer?**

**A:** Install Clsl on the other computer and place it in the folder you want
to use as its home folder. Create a `data` folder there if it does not already
exist, then copy your previous `data/csls.txt` file into it before starting
Clsl.

**Q: What happens if I enter an invalid command?**

**A:** Clsl displays an error message beginning with `ERROR:` and keeps running.
For example, task numbers must be positive whole numbers that refer to a task
currently in the list.

## Known issues

No known issues are currently documented.

## Command summary

| Action | Format | Example |
| --- | --- | --- |
| Add a to-do | `todo DESCRIPTION` | `todo read book` |
| Add a deadline | `deadline DESCRIPTION /by DATE` | `deadline submit report /by 2026-09-30` |
| Add an event | `event DESCRIPTION /from START_DATE /to END_DATE` | `event meeting /from 2026-09-20 /to 2026-09-20` |
| List tasks | `list` | `list` |
| Find tasks | `find KEYWORD` | `find report` |
| Find tasks on a date | `on DATE` | `on 2026-09-20` |
| Mark as done | `mark TASK_NUMBER` | `mark 1` |
| Mark as not done | `unmark TASK_NUMBER` | `unmark 1` |
| Delete a task | `delete TASK_NUMBER` | `delete 3` |
| Exit | `bye` | `bye` |
