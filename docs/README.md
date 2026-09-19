# Xerxes User Guide

Xerxes is a desktop task manager for creating, viewing, searching, completing,
and deleting tasks. It provides a graphical user interface (GUI) while keeping
the same command-based interaction available through the command line
interface (CLI).

## Table of contents

* [Quick start](#quick-start)
    * [Prerequisites](#prerequisites)
    * [Running Xerxes](#running-xerxes)
* [Command format](#command-format)
* [Features](#features)
    * [Adding a to-do task](#adding-a-to-do-task-todo)
    * [Adding a deadline](#adding-a-deadline-deadline)
    * [Adding an event](#adding-an-event-event)
    * [Listing all tasks](#listing-all-tasks-list)
    * [Finding tasks](#finding-tasks-find)
    * [Marking a task as completed](#marking-a-task-as-completed-mark)
    * [Marking a task as incomplete](#marking-a-task-as-incomplete-unmark)
    * [Deleting a task](#deleting-a-task-delete)
    * [Saving tasks](#saving-tasks-save)
    * [Exiting Xerxes](#exiting-xerxes-bye)
* [Command aliases](#command-aliases)
* [Task display format](#task-display-format)
* [Data storage](#data-storage)
    * [Editing the save file](#editing-the-save-file)
* [FAQ](#faq)
* [Command summary](#command-summary)


## Quick start

### Prerequisites

Ensure that the following are installed:

* Java Development Kit (JDK) 25.
### Running Xerxes

1. Download the latest .jar file from [here](https://github.com/ZYS-37/ip/releases).

2. Copy the file to the folder you want to use as the home folder for your AddressBook.

3. Open a terminal, cd to the folder containing the JAR file, and run ```java -jar xerxes.jar.```
   A GUI similar to the one below should appear in a few seconds.
    - The GUI opens a window titled **Xerxes Task Manager**.
    - To run the CLI instead, run ```java -cp xerxes.jar xerxes.Xerxes```
4. Type a command into the text field and press **Enter** or click **Send**. here are some commands that you can try:
    - ```todo quickstart```: Adds a ToDo task "quickstart"
    - ```list```: Lists all contacts
    - ```delete 1```: Deletes the first task in the current list
    - ```bye```: Exits the app
5. Refer to the features section below for details of each command

## Command format

* Commands are case-insensitive. For example, `LIST` and `list` are equivalent.
* Leading and trailing spaces are ignored.
* Task descriptions may contain spaces.
* `INDEX` refers to the displayed task number and must be a positive integer.
* Dates use the format `d/M/yyyy`, such as `2/3/2026` or `24/8/2026`.
* An event may start and end on the same date, but it cannot end before it starts.
* A task with the same type, description, and date details cannot be added twice.

## Features

### Adding a to-do task: `todo`

Adds a task without a date.

Format:``` todo DESCRIPTION```

Examples:
- ```todo Complete the project documentation```
- ```todo Buy groceries```

The description must not be empty. Duplicate to-do tasks are rejected,
regardless of differences in capitalisation.

### Adding a deadline: `deadline`

Adds a task that must be completed by a specified date.

Format:```deadline DESCRIPTION /by DATE```

Examples:
- ```deadline Submit report /by 20/9/2026```
- ```deadline Renew passport /by 2/3/2027```

The `/by` parameter must appear exactly once. The date must be valid; for
example, `31/2/2026` is rejected.

### Adding an event: `event`

Adds a task that takes place over a start and end date.

Format:```event DESCRIPTION /from START_DATE /to END_DATE```

Examples:
- ```event Software demo /from 20/9/2026 /to 21/9/2026```
- ```event Doctor appointment /from 2/3/2027 /to 2/3/2027```

The `/from` and `/to` parameters must each appear exactly once. Both dates
must be valid, and the end date cannot be earlier than the start date.

### Listing all tasks: `list`

Displays all tasks in the order they were added.

Format: ```list```

Each task is shown with its task type, completion status, and list index.

### Finding tasks: `find`

Finds tasks whose descriptions contain the given keyword.

Format:```find KEYWORD```

Examples:
- ```find report```
- ```find project```

Search is case-insensitive. A missing keyword is rejected.

### Marking a task as completed: `mark`

Marks the task at the given index as completed.

Format:```mark INDEX```

Example:
- ```mark 2```

### Marking a task as incomplete: `unmark`

Marks the task at the given index as incomplete.

Format:```unmark INDEX```

Example:
- ```unmark 2```

The index must identify an existing task. Task numbers must be positive, and
marking commands accept exactly one task number.

### Deleting a task: `delete`

Deletes the task at the given index.

Format:```delete INDEX```

Example:
- ```delete 3```

The index must identify an existing task. Deleting from an empty task list is
rejected with an explanatory error message.

### Saving tasks: `save`

Writes the current task list to the save file.

Format:```save```

The `save` command does not accept additional arguments.

### Exiting Xerxes: `bye`

Exits the application. In the GUI, the Xerxes window closes. In the CLI, the
Xerxes command loop ends.

Format:```bye```

The `bye` command does not accept additional arguments.

## Command aliases

The following shorter aliases are available:

| Full command | Alias |
| --- | --- |
| `bye` | `b` |
| `list` | `l` |
| `save` | `s` |
| `find` | `f` |
| `todo` | `t` |
| `deadline` | `d` |
| `event` | `e` |
| `delete` | `del` |
| `mark` | `m` |
| `unmark` | `um` |

For example, the following commands are equivalent:
- ```todo Read chapter 1```
- ```t Read chapter 1```

## Task display format

Xerxes identifies tasks using the following prefixes:

| Task type | Prefix | Example |
| --- | --- | --- |
| To-do | `[T]` | `[T][ ] Buy groceries` |
| Deadline | `[D]` | `[D][ ] Submit report (by: Sep 20 2026)` |
| Event | `[E]` | `[E][X] Software demo (from: Sep 20 2026 to: Sep 21 2026 )` |

`[ ]` means incomplete and `[X]` means completed.

## Data storage

Tasks are stored in:

```text
data/Xerxes.txt
```

The directory and file are created automatically when Xerxes starts if they
do not already exist. Use the `save` command after making changes that should
be written to disk.

### Editing the save file

The save file is a plain-text file intended for Xerxes's internal format. If
you edit it manually, keep a backup first. Invalid task records, unsupported
task types, invalid dates, and malformed escape sequences may cause the file
to be treated as corrupted when Xerxes starts.



## FAQ

**How do I see the available commands?**

There is currently no `help` command. Refer to the command summary below or
this guide.

**Why does the GUI not send an empty message?**

The GUI ignores empty or whitespace-only submissions so that blank chat
bubbles are not added to the conversation.

**Why did my task not appear after restarting Xerxes?**

Run `save` before exiting and check that `data/Xerxes.txt` is present and
accessible.

**Can I use the CLI and GUI with the same tasks?**

Yes, provided both are run from the project directory and use the same
`data/Xerxes.txt` save file.

## Command summary

| Action | Format | Example |
| --- | --- | --- |
| Add to-do | `todo DESCRIPTION` | `todo Buy groceries` |
| Add deadline | `deadline DESCRIPTION /by DATE` | `deadline Submit report /by 20/9/2026` |
| Add event | `event DESCRIPTION /from START /to END` | `event Demo /from 20/9/2026 /to 21/9/2026` |
| List tasks | `list` | `list` |
| Find tasks | `find KEYWORD` | `find report` |
| Mark complete | `mark INDEX` | `mark 1` |
| Mark incomplete | `unmark INDEX` | `unmark 1` |
| Delete task | `delete INDEX` | `delete 1` |
| Save tasks | `save` | `save` |
| Exit | `bye` | `bye` |
