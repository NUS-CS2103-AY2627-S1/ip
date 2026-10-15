# Orion User Guide

Orion is a desktop application for managing tasks, optimized for use through a Command Line Interface while retaining the benefits of a Graphical User Interface.

![Ui Screenshot](Ui.png)

---

## Quick start

1. Ensure that Java 25 or later is installed on your computer.
2. Download the latest `orion.jar` file from [here](https://github.com/Ciltan/ip/releases).
3. Copy the file to the folder you want to use as the home folder for Orion.
4. Open a terminal, `cd` to the folder containing the JAR file, and run `java -jar orion.jar`.
5. A GUI similar to the one above should appear in a few seconds.
6. Type a command in the command box and press Enter to execute it. For example, type `help` and press Enter to open the help window.
7. Refer to the Features section below for details of each command.

---

## Features

> **Notes about the command format:**
> * Words in parentheses are the parameters to be supplied by the user.
>   * For example, in `todo (description)`, replace `(description)` with a value such as `Clean room`.
> * Extraneous parameters for commands that take no parameters, such as `help`, `list`, `undo`, and `bye`, are ignored.
>   * For example, `help 123` is interpreted as `help`.

### Viewing help: `help`
Shows a message listing all available commands.

**Format:** `help`

### Adding a Todo task: `todo`
Adds a Todo task to the task list.

**Format:** `todo (description)`

**Example:** `todo Clean room`

### Adding a Deadline task: `deadline`
Adds a Deadline task that needs to be done before a specific date/time.

**Format:** `deadline (description) /by (date)`

**Example:** `deadline Submit report /by 2026-09-20 2359`

### Adding an Event task: `event`
Adds an Event task that starts and ends at specific times.

**Format:** `event (description) /from (start date) /to (end date)`

**Example:** `event Project meeting /from 2026-09-15 1400 /to 2026-09-15 1600`

### Listing all tasks: `list`
Shows a list of all tasks in the list.

**Format:** `list`

### Marking a task as done: `mark`
Marks an existing task in the list as completed.

**Format:** `mark (index)`
* Note: The index refers to the index number shown in the displayed task list. The index must be a positive integer (1, 2, 3, ...).

**Example:** `mark 1`

### Unmarking a task: `unmark`
Marks a completed task as not done yet.

**Format:** `unmark (index)`

**Example:** `unmark 2`

### Deleting a task: `delete`
Deletes the specified task from the list.

**Format:** `delete (index)`
* Note: The index refers to the index number shown in the displayed task list. The index must be a positive integer (1, 2, 3, ...).

**Example:** `delete 3`

### Searching for tasks: `find`
Finds tasks whose descriptions contain the given keyword.

**Format:** `find (keyword)`

**Example:** `find team`

### Undoing a command: `undo`
Reverts your most recent add, delete, mark or unmark command.

**Format:** `undo`

### Exiting the program: `bye`
Exits the program.

**Format:** `bye`

---

## Saving the data
Orion automatically saves all your data after every command. You do not need to save manually.

---

## Command summary

Action | Format | Examples
-------|--------|---------
**Add Todo** | `todo (description)` | `todo Clean room`
**Add Deadline** | `deadline (description) /by (date)` | `deadline Submit report /by 2026-09-20 2359`
**Add Event** | `event (description) /from (start) /to (end)` | `event Project meeting /from 2026-09-15 1400 /to 2026-09-15 1600`
**List** | `list` | `list`
**Mark** | `mark (index)` | `mark 2`
**Unmark** | `unmark (index)` | `unmark 2`
**Delete** | `delete (index)` | `delete 3`
**Find** | `find (keyword)` | `find team`
**Undo** | `undo` | `undo`
**Help** | `help` | `help`
**Exit** | `bye` | `bye`
