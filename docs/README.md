# Labubu User Guide

Labubu is a friendly task-tracking chatbot. Use the chat window to add, find, update, and delete tasks. Your tasks are saved automatically in `data/labubu.txt`.

![Labubu task tracker](Ui.png)

## Getting started

### Prerequisites

- JDK 25

### Launch the application

From the project root, run:

```powershell
java -jar "labubu.jar"
```

Type a command in the input field and press **Enter** or click **Send**.

## Commands

### Add tasks

| Command | What it does | Example |
| --- | --- | --- |
| `todo [task title]` | Adds a task without a date. | `todo read book` |
| `deadline [task title] /by [date and time]` | Adds a task with a deadline. | `deadline return book /by 06/07/2026` |
| `event [task title] /from [start] /to [end]` | Adds a task that takes place during a period. | `event meeting /from 06/07/2026 14:00 /to 06/07/2026 15:30` |

Dates may use either format:

```text
dd/MM/yyyy
dd/MM/yyyy HH:mm
```

An event must start before it ends. Dates such as `31/02/2026` are rejected.

### Manage tasks

| Command | What it does | Example |
| --- | --- | --- |
| `list` | Displays all tasks. | `list` |
| `find [keyword]` | Displays tasks whose titles contain the keyword. | `find book` |
| `mark [task number]` | Marks a task as completed. | `mark 1` |
| `unmark [task number]` | Marks a task as incomplete. | `unmark 1` |
| `delete [task number]` | Deletes a task. | `delete 1` |

Task numbers correspond to their position in the list.

### Other commands

| Command | What it does |
| --- | --- |
| `help` | Opens or closes the in-app help guide. |
| `bye`, `exit`, `quit` | Saves your tasks and closes Labubu. |

## Task markers

- `[T]` — To-do
- `[D]` — Deadline
- `[E]` — Event
- `[ ]` — Incomplete
- `[X]` — Completed

## If something goes wrong

Labubu reports invalid commands, missing details, invalid task numbers, invalid dates, and invalid event ranges without creating the invalid task. Error responses are highlighted with a warning icon in the GUI.

If the save file is missing, Labubu starts with an empty task list and creates the file when tasks are saved. If the file is corrupted, Labubu reports the problem and starts with a fresh task list.

## Quick example

```text
todo read book
deadline return book /by 06/07/2026
list
mark 1
find book
```
