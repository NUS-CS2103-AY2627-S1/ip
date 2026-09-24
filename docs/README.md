# Dobby User Guide

Dobby is a friendly desktop chatbot that helps you keep track of todos, deadlines, and events. Type a command in the message box and press **Enter** to send it.

## Getting started

Try these commands:

1. Add a task with `todo read book`.
2. View your tasks with `list`.
3. Complete it with `mark 1`, where `1` is its number in the list.

Type `help` at any time to see Dobby's command summary.

## Features

### Add tasks

| Task type | Format | Example |
| --- | --- | --- |
| Todo | `todo DESCRIPTION` | `todo read book` |
| Deadline | `deadline DESCRIPTION /by DATE [TIME]` | `deadline return book /by 2019-12-02 1800` |
| Event | `event DESCRIPTION /from DATE [TIME] /to DATE [TIME]` | `event project meeting /from 2/12/2019 14:00 /to 2/12/2019 1600` |

Use `yyyy-MM-dd` or `d/M/yyyy` for dates. Times are optional and use the 24-hour `HHmm` or `HH:mm` format.
An event's end must be at or after its start.

### View and find tasks

- `list` shows all tasks and their numbers.
- `find SEARCH_TEXT` shows tasks whose descriptions contain the given text. The search is case-insensitive; for example, `find book` matches `Read Book`.

### Update or delete tasks

Run `list` to check a task's number, then use:

- `mark TASK_NUMBER` to mark it as done, e.g. `mark 2`.
- `unmark TASK_NUMBER` to mark it as not done, e.g. `unmark 2`.
- `delete TASK_NUMBER` to remove it, e.g. `delete 2`.

Task numbers can change after a task is deleted, so run `list` again before your next update.

### Save and exit

Dobby saves your tasks automatically to `data/dobby.txt` after every change and loads them the next time it
starts. Existing tasks in the old `data/duke.txt` location are imported automatically. Enter `bye` to close Dobby.

## Command summary

| Command | Purpose |
| --- | --- |
| `todo DESCRIPTION` | Add a todo |
| `deadline DESCRIPTION /by DATE [TIME]` | Add a deadline |
| `event DESCRIPTION /from DATE [TIME] /to DATE [TIME]` | Add an event |
| `list` | Show all tasks |
| `find SEARCH_TEXT` | Find tasks by description |
| `mark TASK_NUMBER` | Mark a task as done |
| `unmark TASK_NUMBER` | Mark a task as not done |
| `delete TASK_NUMBER` | Delete a task |
| `help` | Show help in Dobby |
| `bye` | Exit Dobby |
