# Tabby User Guide

Tabby is a lightweight task manager for todos, deadlines, and events. It is available as a command-line application and a JavaFX graphical interface.

## Features

- Add todos, deadlines, and events
- View all tasks or search by keyword
- Mark tasks done, undo completion, and delete tasks
- Save tasks automatically between sessions
- Use a responsive GUI with quick-action buttons

## Quick start

From the project directory, run:

```bash
./gradlew run
```

The GUI opens with a command field. Type a command and press **Enter** or click **Run**. Select a task to use the quick-action buttons.

For the command-line interface, run the `ip.Tabby` application from your IDE or build configuration.

## Commands

| Command | What it does |
| --- | --- |
| `list` | Shows all tasks |
| `find KEYWORD` | Shows tasks whose descriptions contain the keyword |
| `todo DESCRIPTION` | Adds a todo |
| `deadline DESCRIPTION /by DATE` | Adds a deadline |
| `event DESCRIPTION /from START /to END` | Adds an event |
| `mark NUMBER` | Marks a task as done |
| `unmark NUMBER` | Marks a task as not done |
| `delete NUMBER` | Removes a task |
| `help` | Shows the available commands |
| `bye` | Exits Tabby |

Task numbers are shown by `list` and beside each task in the GUI. For example:

```text
todo read lecture notes
deadline submit report /by 2026-09-30
event project meeting /from 2026-10-01 0900 /to 2026-10-01 1000
mark 1
```

## Dates and times

Tabby accepts dates in `yyyy-MM-dd` or `d/M/yyyy` format. Date-times can use `yyyy-MM-dd HHmm`, `yyyy-MM-dd HH:mm`, `d/M/yyyy HHmm`, or `d/M/yyyy HH:mm`.

An event must start before it ends. Invalid dates such as February 30, missing values, and duplicate `/by`, `/from`, or `/to` clauses are rejected with an explanatory error.

## Errors and saved data

If a command is incomplete or has an invalid task number, Tabby keeps existing tasks and shows an error. Tasks are saved in `data/tabby.txt` after changes. If the file does not exist, Tabby starts with an empty list and creates the file when you add your first task.

## Tips

- Use `help` whenever you forget a command format.
- Use `list` to refresh the complete task list after a search.
- In the GUI, use **Mark done**, **Unmark**, and **Delete** after selecting a task.
