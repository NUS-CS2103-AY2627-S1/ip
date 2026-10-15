# Remy User Guide

Remy is a desktop chatbot task manager that helps you track todos, deadlines, and events — with a personality that's just a little too attached to you. Type commands, get things done, and Remy will make sure you never forget a task (whether you like it or not).

## Table of Contents
- [Quick Start](#quick-start)
- [Features](#features)
  - [Adding a todo: `todo`](#adding-a-todo-todo)
  - [Adding a deadline: `deadline`](#adding-a-deadline-deadline)
  - [Adding an event: `event`](#adding-an-event-event)
  - [Listing all tasks: `list`](#listing-all-tasks-list)
  - [Marking a task as done: `mark`](#marking-a-task-as-done-mark)
  - [Unmarking a task: `unmark`](#unmarking-a-task-unmark)
  - [Deleting a task: `delete`](#deleting-a-task-delete)
  - [Finding tasks: `find`](#finding-tasks-find)
  - [Exiting the app: `bye`](#exiting-the-app-bye)
- [Date and Time Format](#date-and-time-format)
- [Saving the Data](#saving-the-data)
- [Command Summary](#command-summary)

---

## Quick Start

1. Ensure you have Java 17 or above installed.
2. Download the latest `remy.jar` release.
3. Run the app from a terminal in the folder containing the jar:
   ```
   java -jar remy.jar
   ```
4. A chat window will appear. Type a command into the input box and press **Enter** or click **Send**.
5. Refer to [Features](#features) below for the full list of commands.

---

## Features

> **Notes on command format**
> - Words in `UPPER_CASE` are parameters supplied by you.
>   e.g. in `todo DESCRIPTION`, `DESCRIPTION` is a parameter, and `todo read book` is a valid command.
> - Extraneous parameters for commands that don't take any (e.g. `bye`) will be ignored.

### Adding a todo: `todo`

Adds a simple task with no date attached.

**Format:** `todo DESCRIPTION`

You can optionally attach a place using `/at`:

**Format:** `todo DESCRIPTION /at PLACE`

**Examples:**
```
todo read book
todo buy groceries /at NTUC
```

---

### Adding a deadline: `deadline`

Adds a task that needs to be completed by a specific date or date-time.

**Format:** `deadline DESCRIPTION /by DATE`

**Examples:**
```
deadline return book /by 2026-08-28
deadline submit report /by 2026-08-28 1430
```

See [Date and Time Format](#date-and-time-format) for accepted date formats.

---

### Adding an event: `event`

Adds a task that spans a start and end date/time.

**Format:** `event DESCRIPTION /from DATE /to DATE`

The `/from` and `/to` markers may appear in either order.

**Examples:**
```
event project meeting /from 2026-08-28 /to 2026-08-29
event conference /from 2026-09-01 1000 /to 2026-09-01 1700
```

---

### Listing all tasks: `list`

Shows every task currently in your list, numbered in order.

**Format:** `list`

---

### Marking a task as done: `mark`

Marks the task at the given index as complete.

**Format:** `mark INDEX`

`INDEX` refers to the number shown in the `list` output. It must be a valid, positive index within range.

**Example:**
```
mark 2
```

---

### Unmarking a task: `unmark`

Marks a previously completed task as not done.

**Format:** `unmark INDEX`

**Example:**
```
unmark 2
```

---

### Deleting a task: `delete`

Removes the task at the given index from the list permanently.

**Format:** `delete INDEX`

**Example:**
```
delete 3
```

---

### Finding tasks: `find`

Searches all tasks for a keyword and displays matching results.

**Format:** `find KEYWORD`

**Example:**
```
find book
```

---

### Exiting the app: `bye`

Saves your task list and closes Remy.

**Format:** `bye`

---

## Date and Time Format

Dates can be entered with or without a time component:

| Format | Example | Meaning |
|---|---|---|
| `yyyy-MM-dd` | `2026-08-28` | Date only |
| `yyyy-MM-dd HHmm` | `2026-08-28 1430` | Date with time (24-hour clock) |

Dates are validated strictly — for example, `2026-02-30` will be rejected as invalid since February never has 30 days.

---

## Saving the Data

Remy automatically saves your task list to disk after every command that changes it, and loads it back the next time you open the app. There's no need to save manually.

---

## Command Summary

| Action | Format | Example |
|---|---|---|
| Add todo | `todo DESCRIPTION [/at PLACE]` | `todo read book /at library` |
| Add deadline | `deadline DESCRIPTION /by DATE` | `deadline return book /by 2026-08-28 1430` |
| Add event | `event DESCRIPTION /from DATE /to DATE` | `event meeting /from 2026-08-28 /to 2026-08-29` |
| List tasks | `list` | `list` |
| Mark done | `mark INDEX` | `mark 2` |
| Unmark | `unmark INDEX` | `unmark 2` |
| Delete | `delete INDEX` | `delete 3` |
| Find | `find KEYWORD` | `find book` |
| Exit | `bye` | `bye` |
