# Augustus User Guide

Greetings, citizen.

Augustus is a chatbot that helps you organise todos, deadlines, and events in one place.

Use simple commands to manage your tasks, track important deadlines, and keep your schedule organised.


## Quick Start

1. Make sure Java `25` is installed on your computer.
2. Place `Augustus.jar` in a folder of your choice.
3. Open a new terminal window in that folder.
4. Run:

```bash
java -jar Augustus.jar
```

5. The Augustus GUI should open.
6. Enter a command in the command box and press **Enter** or click **Send**.
7. Refer to the command summary and features below for the available commands.

Here are some commands you can try:

- `todo Review lecture notes`
- `deadline Submit report /by 2026-09-25`
- `event Team meeting /from 2pm /to 4pm`
- `list`

---
---

## Command Summary

| Command | Format | Purpose |
| --- | --- | --- |
| `todo` | `todo DESCRIPTION` | Adds a task without a date or time |
| `deadline` | `deadline DESCRIPTION /by DATE` | Adds a task that must be completed by a date |
| `event` | `event DESCRIPTION /from START /to END` | Adds an event with a start and end time |
| `list` | `list` | Shows all tasks currently stored |
| `find` | `find KEYWORD` | Searches for tasks containing a keyword |
| `mark` | `mark NUMBER` | Marks a task as completed |
| `unmark` | `unmark NUMBER` | Marks a completed task as incomplete |
| `delete` | `delete NUMBER` | Removes a task |
| `tag` | `tag NUMBER /t TAG` | Assigns a tag to a task |
| `bye` | `bye` | Closes Augustus |


## Features

### Command format

Some commands require you to provide extra information.

- `DESCRIPTION` is the description of the task.
- `NUMBER` is the task number shown when you use `list`.
- `KEYWORD` is the word you want to search for.
- Deadline dates should be written in `yyyy-MM-dd` format, such as `2026-09-25`.

### Adding a todo: `todo`

Adds a task without a date or time.

Format: `todo DESCRIPTION`

Example:

`todo Review lecture notes`

Expected outcome:

```text
By my decree, this task shall be recorded:
[T][ ] Review lecture notes
```

### Adding a deadline: `deadline`

Adds a task that must be completed by a specified date.

Format: `deadline DESCRIPTION /by DATE`

The date must use the `yyyy-MM-dd` format.

Example:

`deadline Submit report /by 2026-09-25`

Expected outcome:

```text
By my decree, this task shall be recorded:
[D][ ] Submit report (by: Sep 25 2026)
```

### Adding an event: `event`

Adds an event with a start and end time.

Format: `event DESCRIPTION /from START /to END`

Example:

`event Team meeting /from 2pm /to 4pm`

Expected outcome:

```text
By my decree, this task shall be recorded:
[E][ ] Team meeting (From: 2pm to: 4pm)
```

### Listing all tasks: `list`

Shows all tasks recorded in Augustus.

Format: `list`

Example:

`list`

Example output:

```text
Behold the tasks recorded under my rule:
1. [T][ ] Review lecture notes
2. [E][ ] Team meeting (From: 2pm to: 4pm)
3. [D][ ] Submit report (by: Sep 25 2026)
```

### Finding tasks: `find`

Finds tasks whose descriptions contain the given keyword.

Format: `find KEYWORD`

Example:

`find report`

Augustus displays tasks that match the keyword.

### Marking a task as completed: `mark`

Marks the specified task as completed.

Format: `mark NUMBER`

- `NUMBER` refers to the task number shown by `list`.
- The task number must be valid.

Example:

`mark 1`

Expected outcome:

```text
Well done. I declare this task complete:
[T][X] Review lecture notes
```

### Marking a task as not completed: `unmark`

Marks a completed task as incomplete.

Format: `unmark NUMBER`

Example:

`unmark 1`

Expected outcome:

```text
So be it. This task returns to active duty:
[T][ ] Review lecture notes
```

### Deleting a task: `delete`

Removes the specified task from Augustus.

Format: `delete NUMBER`

- `NUMBER` refers to the task number shown by `list`.
- Deleting a task does not affect the other tasks.

Example:

`delete 1`

Expected outcome:

```text
By my order, this task has been removed from the records:
[T][ ] Review lecture notes
```

### Tagging a task: `tag`

Assigns a tag to a task to help organise it.

Format: `tag NUMBER /t TAG`

Example:

`tag 1 /t university`

Expected outcome:

```text
This task shall bear the following mark:
[T][ ] Review lecture notes [#university]
```

A task can have one tag at a time. Assigning another tag replaces its
existing tag.

### Exiting Augustus: `bye`

Closes the application.

Format: `bye`

Expected outcome:

```text
You are dismissed.
May your duties be completed with honour.
```

---

## Saving Data

Augustus automatically saves your tasks to the file
`data/augustus.txt` whenever the task list is updated.