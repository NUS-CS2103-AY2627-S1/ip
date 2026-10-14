# Baby User Guide

Baby is a desktop chatbot that helps you keep track of your todos, deadlines, and events —
all through typed commands, with the speed of a keyboard-driven workflow and the friendliness
of a simple chat window.

![Baby's Winter Court screenshot](Ui.png)

## Quick start

1. Ensure you have Java 25 installed on your computer.
2. Download the latest `Baby.jar` from this repository's **Releases** page.
3. Copy the file to the folder you want to use as the home folder for Baby.
4. Open a terminal, `cd` into that folder, and run:
   ```bash
   java -jar Baby.jar
   ```
5. Type a command in the input box and press Enter (or click **Send**) to run it. Some example commands:
   * `todo read book` — adds a todo
   * `list` — shows all tasks
   * `bye` — exits the app

## Features

> **Notes on the command format**
> * Words in `UPPER_CASE` are parameters you supply, e.g. in `todo DESCRIPTION`, `DESCRIPTION` is a
>   parameter you can supply as `todo read book`.
> * Dates and times must be entered in the format `yyyy-MM-dd HHmm`, e.g. `2025-12-02 1800` for
>   2 Dec 2025, 6:00 PM.
> * Extra spaces are ignored; commands are case-sensitive.

### Adding a todo: `todo`

Adds a task without any date or time attached.

Format: `todo DESCRIPTION`

Example: `todo read book`

```text
Consider it done, Your Highness!
I've added: [T][ ] read book
You now have 1 task in your list.
```

### Adding a deadline: `deadline`

Adds a task that needs to be done by a specific date and time.

Format: `deadline DESCRIPTION /by yyyy-MM-dd HHmm`

Example: `deadline return book /by 2025-12-02 1800`

```text
Consider it done, Your Highness!
I've added: [D][ ] return book (by: Dec 02 2025, 6:00 PM)
You now have 2 tasks in your list.
```

### Adding an event: `event`

Adds a task that spans a start and end date/time.

Format: `event DESCRIPTION /from yyyy-MM-dd HHmm /to yyyy-MM-dd HHmm`

Example: `event team meeting /from 2025-12-03 1400 /to 2025-12-03 1600`

```text
Consider it done, Your Highness!
I've added: [E][ ] team meeting (from: Dec 03 2025, 2:00 PM to: Dec 03 2025, 4:00 PM)
You now have 3 tasks in your list.
```

### Listing all tasks: `list`

Shows every task currently stored, in the order they were added.

Format: `list`

```text
Your Highness, here are the tasks in your list:
1.[T][ ] read book
2.[D][ ] return book (by: Dec 02 2025, 6:00 PM)
3.[E][ ] team meeting (from: Dec 03 2025, 2:00 PM to: Dec 03 2025, 4:00 PM)
```

### Marking a task as done: `mark`

Marks the task at the given index as completed.

Format: `mark INDEX`

Example: `mark 1`

```text
Splendid! Task 1 is now marked as done:
 [T][X] read book
```

### Marking a task as not done: `unmark`

Marks the task at the given index as not yet completed.

Format: `unmark INDEX`

Example: `unmark 1`

```text
Certainly, Your Highness. Task 1 is marked as not done again:
 [T][ ] read book
```

### Deleting a task: `delete`

Removes the task at the given index from the list.

Format: `delete INDEX`

Example: `delete 2`

```text
It's been removed, Your Highness.
Deleted: [D][ ] return book (by: Dec 02 2025, 6:00 PM)
You now have 2 tasks in your list.
```

### Finding tasks: `find`

Finds tasks whose descriptions contain the given keyword. Search is case-insensitive and
matches partial words.

Format: `find KEYWORD`

Examples: `find book`, `find BOOK`, `find boo`

```text
Here are the matching tasks, Your Highness:
1.[T][ ] read book
```

### Exiting the program: `bye`

Says goodbye and closes the app.

Format: `bye`

## Saving the data

Baby automatically saves your tasks to a data file (`data/baby.txt`, inside the folder you
run the app from) after every command that changes the list. There is no need to save manually.

## Editing the data file

Advanced users may edit the data file directly. If a line in the file is invalid or corrupted,
Baby will skip it and warn you, rather than crashing — the rest of your tasks will still load
normally. To avoid data loss, make a backup of the file before editing it directly.

## Handling errors

Baby is designed not to crash on bad input. If a command is mistyped, missing a parameter, or
uses the wrong date format, Baby will explain what went wrong and how to fix it instead of
exiting or corrupting your task list.

## Command summary

| Action | Format | Example |
|---|---|---|
| Todo | `todo DESCRIPTION` | `todo read book` |
| Deadline | `deadline DESCRIPTION /by yyyy-MM-dd HHmm` | `deadline return book /by 2025-12-02 1800` |
| Event | `event DESCRIPTION /from yyyy-MM-dd HHmm /to yyyy-MM-dd HHmm` | `event meeting /from 2025-12-03 1400 /to 2025-12-03 1600` |
| List | `list` | `list` |
| Mark | `mark INDEX` | `mark 1` |
| Unmark | `unmark INDEX` | `unmark 1` |
| Delete | `delete INDEX` | `delete 2` |
| Find | `find KEYWORD` | `find book` |
| Exit | `bye` | `bye` |

## Acknowledgements

- The JavaFX GUI structure, including the `MainWindow` and `DialogBox` approach, was adapted from
  the CS2103T JavaFX tutorial.
- The Gradle project setup, Checkstyle configuration, and starter project structure were adapted
  from the CS2103T iP template and course materials.
- The GUI background and avatar images were generated using ChatGPT for this project.
