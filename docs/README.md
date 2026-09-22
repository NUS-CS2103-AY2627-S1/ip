# Yappa - Your No-Nonsense Task Assistant

Got a million things to do and zero motivation? **Yappa** is here to keep
you on track and make sure your deadlines don't sneak up on you like an
unexpected 8 AM lecture.

Yappa is a desktop task-management application optimized for users who
prefer typing commands. Add tasks, track deadlines and events, search your task list, and stop procrasting!

## Table of Contents

- [Quick Start](#quick-start)
- [Features](#features)
  - [Viewing help: `help`](#viewing-help-help)
  - [Adding a todo: `todo`](#adding-a-todo-todo)
  - [Adding a deadline: `deadline`](#adding-a-deadline-deadline)
  - [Adding an event: `event`](#adding-an-event-event)
  - [Listing tasks: `list`](#listing-tasks-list)
  - [Sorting tasks: `sort`](#sorting-tasks-sort)
  - [Finding tasks: `find`](#finding-tasks-find)
  - [Marking a task: `mark`](#marking-a-task-mark)
  - [Unmarking a task: `unmark`](#unmarking-a-task-unmark)
  - [Deleting a task: `delete`](#deleting-a-task-delete)
  - [Clearing all tasks: `clear`](#clearing-all-tasks-clear)
  - [Exiting Yappa: `bye`](#exiting-yappa-bye)
- [Handling Duplicates](#handling-duplicates)
- [Saving Data](#saving-data)
- [Command Summary](#command-summary)

---

## Quick Start

1. Ensure that **Java 25** is installed on your computer.

2. Download `Yappa.jar` from the
   [latest GitHub release](https://github.com/minrui13/ip/releases/latest).

3. Place `Yappa.jar` in an empty folder where you want to run Yappa.

4. Open a terminal in that folder and run:

   ```console
   java -jar Yappa.jar
   ```
5. Yappa's GUI should appear. Type commands into the input box and press
Enter or the Send button to execute them.

6. Try some commands:

```
todo read chapter 5
deadline submit assignment /by 20/09/2026 2359
list
help
```
--- 

## Features
**Notes about command formats**
* Words in UPPER_CASE represent values that you need to provide.
* Task numbers correspond to the numbers shown by the list command.
* Task numbers must be positive integers starting from 1.
* Dates and times use the dd/MM/yyyy HHmm format and a 24-hour clock.
* Leading and trailing whitespace is ignored.
* Consecutive whitespace is treated as a single space.
* Commands that do not accept parameters will reject additional parameters.

For example:

```
deadline submit assignment /by 20/09/2026 2359
```

uses:

* `submit assignment` as the task description.
* `20/09/2026 2359` as the deadline.

--- 

### Viewing help: `help`

Displays all available Yappa commands and their expected syntax.

**Format:**

`help`

**Example:**

`help`

Yappa displays:

```
Here are the commands!

Task commands:
todo <description>
deadline <description> /by <dd/MM/yyyy HHmm>
event <description> /from <dd/MM/yyyy HHmm> /to <dd/MM/yyyy HHmm>
mark <task_number>
unmark <task_number>
delete <task_number>
find <search>

Display and sorting commands:
list
sort [alpha|datetime] [asc|desc]

General commands:
help
clear
bye
```
---

### Adding a todo: `todo`

Adds a task without a specific date or time.

**Format:**

`todo DESCRIPTION`

**Example:**

`todo read chapter 5`

**Expected output:**
```
Ok! I have added the task:
    [T][ ] read chapter 5
Now you have 1 task in the list.
```
--- 

### Adding a deadline: `deadline`

Adds a task that must be completed by a specific date and time.

**Format:**

`deadline DESCRIPTION /by DATE_TIME`

`DATE_TIME` must follow the `dd/MM/yyyy HHmm` format.

**Example:**

`deadline submit assignment /by 20/09/2026 2359`

**Expected output:**
```
Ok! I have added the task:
    [D][ ] submit assignment (by: 20 Sep 2026, 11:59 PM)
Now you have 1 task in the list.
```

---

### Adding an event: `event`


Adds a task that takes place between a start and end date/time.

**Format:**

`event DESCRIPTION /from START_DATE_TIME /to END_DATE_TIME`

Both date-time values must follow the `dd/MM/yyyy HHmm` format.

The event's end time must be later than its start time.

**Example:**

`event team meeting /from 21/09/2026 1400 /to 21/09/2026 1600`

**Expected output:**
```
Ok! I have added the task:
    [E][ ] team meeting (from: 21 Sep 2026, 2:00 PM to: 21 Sep 2026, 4:00 PM)
```
---

### Listing tasks: `list`

Displays all tasks currently stored in Yappa.

**Format:**

`list`

**Example output:**
```
Here are your current tasks:
1. [T][ ] read chapter 5
2. [D][ ] submit assignment (by: 20 Sep 2026, 11:59 PM)
```
---

### Sorting tasks: `sort`

Sorts tasks by description or datetime.

Sorting is case-insensitive when sorting alphabetically.

The default sorting options are:

- Field: alphabetical
- Order: ascending

**Format:**

`sort [FIELD] [ORDER]`

Where:

- `FIELD` is either `alpha` or `datetime`
- `ORDER` is either `asc` or `desc`

**Examples:**

```text
sort
sort alpha asc
sort alpha desc
sort datetime asc
sort datetime desc
```
The following command is equivalent to sort alpha asc:

`sort`

For alphabetical sorting, tasks are ordered by their descriptions:

Before sorting:

```
1. [T][ ] Write report
2. [T][ ] buy milk
3. [T][ ] Attend meeting
```
After sort alpha asc:

```
1. [T][ ] Attend meeting
2. [T][ ] buy milk
3. [T][ ] Write report
```
For date sorting:
* Deadlines are sorted by their deadline.
* Events are sorted by their start time.
* Todos, which do not have a date/time, are placed after dated task

---

### Finding tasks: `find`

Finds tasks whose descriptions contain the specified search text.

**Format:**

`find SEARCH`

**Example:**

`find meeting`

**Example output:**
```
Here are the matching tasks:
1. [E][ ] team meeting (from: 21 Sep 2026, 2:00 PM to: 21 Sep 2026, 4:00 PM)
```
---

### Marking a task: `mark`

Marks a task as completed.

**Format:**

`mark TASK_NUMBER`

`TASK_NUMBER` refers to the number displayed beside the task in the task list and must be a positive integer.

**Example:**

`mark 2`

**Expected output:**
```
Ok! I've marked this task as completed:
    [X] submit assignment
```
---

### Unmarking a task: `unmark`

Marks a completed task as incomplete.

**Format:**

`unmark TASK_NUMBER`

**Example:**

`unmark 2`

**Expected output:**
```
Ok! I've marked this task as not completed:
    [ ] submit assignment
```

---

### Deleting a task: `delete`

Deletes the specified task.

**Format:**

`delete TASK_NUMBER`

**Example:**

`delete 2`

**Expected output:**
```
Ok! I will remove this task:
    [D][ ] submit assignment (by: 20 Sep 2026, 11:59 PM)
Now you have 1 task in the list.
```

### Clearing all tasks: `clear`

Removes all tasks from Yappa.

**Format:**

`clear`

**Expected output:**

`All tasks cleared`

### Exiting Yappa: `bye`

Closes Yappa.

**Format:**

`bye`

**Expected output:**

`Catch you later :)!`

---

### Handling Duplicates

Yappa prevents duplicate tasks with the same description in most cases.

* Todo tasks with the same description are treated as duplicates.
* Deadline tasks with the same description are allowed if their deadline date/time is different.
* Event tasks with the same description are allowed if their start or end date/time is different.
* Tasks with the same description and identical date/time details are treated as duplicates.

For example, these tasks are allowed:

```text
deadline submit report /by 20/09/2026 2359
deadline submit report /by 25/09/2026 2359
```

These events are also allowed:

```text
event team meeting /from 21/09/2026 1400 /to 21/09/2026 1600
event team meeting /from 22/09/2026 1400 /to 22/09/2026 1600
```
However, adding the exact same task twice is not allowed:

```text
todo read chapter 5
todo read chapter 5
```

---

### Saving Data

Yappa automatically saves changes to your task list. You do not need to
manually save your tasks.

Saved tasks are loaded automatically the next time Yappa starts.

---

## Command Summary

| Action | Format | Example |
| --- | --- | --- |
| **Help** | `help` | `help` |
| **List tasks** | `list` | `list` |
| **Sort tasks** | `sort [alpha|datetime] [asc|desc]` | `sort datetime desc` |
| **Add todo** | `todo DESCRIPTION` | `todo read chapter 5` |
| **Add deadline** | `deadline DESCRIPTION /by DATE_TIME` | `deadline submit assignment /by 20/09/2026 2359` |
| **Add event** | `event DESCRIPTION /from START /to END` | `event meeting /from 21/09/2026 1400 /to 21/09/2026 1600` |
| **Mark task** | `mark TASK_NUMBER` | `mark 2` |
| **Unmark task** | `unmark TASK_NUMBER` | `unmark 2` |
| **Delete task** | `delete TASK_NUMBER` | `delete 2` |
| **Find tasks** | `find SEARCH` | `find meeting` |
| **Clear tasks** | `clear` | `clear` |
| **Exit** | `bye` | `bye` |
