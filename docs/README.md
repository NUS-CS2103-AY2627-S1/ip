# AnswerMe User Guide

![AnswerMe application interface](Ui.png)

**AnswerMe** is an interactive task management application. It frees your mind from having to remember countless things that you need to get done.

## Table of Contents

- [Getting Started](#getting-started)
- [Command Summary](#command-summary)
- [Adding Tasks](#adding-tasks)
  - [Add To-dos with `todo`](#add-to-dos-with-todo)
  - [Add Deadlines with `deadline`](#add-deadlines-with-deadline)
  - [Add Events with `event`](#add-events-with-event)
  - [Accepted date and datetime formats](#accepted-date-and-datetime-formats)
- [Viewing tasks with `list`](#viewing-tasks-with-list)
- [Removing a task with `delete`](#removing-a-task-with-delete)
- [Search for tasks with `find`](#search-for-tasks-with-find)
- [Managing Task Statuses](#managing-task-statuses)
  - [Indicate task completion with `mark`](#indicate-task-completion-with-mark)
  - [Indicate an incomplete task with `unmark`](#indicate-an-incomplete-task-with-unmark)
- [Remove duplicate tasks using `dedup`](#remove-duplicate-tasks-using-dedup)
- [Exiting AnswerMe with `bye`](#exiting-answerme-with-bye)

## Getting Started
1. Ensure that you have Java 25 installed on your computer based on [these installation guides](https://se-education.org/guides/tutorials/javaInstallation.html) (choose your OS accordingly).
2. Download the [latest release](https://github.com/RyanNgCT/ip/releases) of AnswerMe as a `.jar` file.
3. Place the `.jar` file in a directory of your choosing.
4. Open a new terminal window in that folder and run:
    ```
    java -jar answerme.jar
    ```
5. This should open the graphical window shown above, where you can enter commands.
6. Hit the submit button (i.e., the plane icon) or press `Enter` to send a command to AnswerMe.

## Command Summary

| Command | Command Syntax | Purpose |
| --- | --- | --- |
| `todo` | `todo <task-description>` | Adds a to-do task. |
| `deadline` | `deadline <task-description> /by <when>` | Adds a task with a due date and optionally a due time. |
| `event` | `event <task-description> /from <when> /to <when>` | Adds an event with start and end dates, each with an optional time. |
| `list` | `list` | Displays all saved tasks. |
| `delete` | `delete <task-index>` | Removes a task from the list. |
| `find` | `find <search-term>` | Searches for tasks matching a search term. |
| `mark` | `mark <task-index>` | Marks a task as completed. |
| `unmark` | `unmark <task-index>` | Marks a task as incomplete. |
| `dedup` | `dedup` | Removes saved duplicate tasks. |
| `bye` | `bye` | Exits the application. |

> ⚠️**Important Notes**
> 
> Here are some pointers worth noting before starting:
> - Replace the placeholder arguments surrounded in angle brackets with actual values that you wish to supply (i.e., `<task-description>`).
> - Your tasks are saved in `data/tasks.txt` to ensure persistence of data across sessions. To transfer your data, simply copy `tasks.txt` over to another computer and ensure that it can be found at `data/tasks.txt`, relative to where AnswerMe is on the new machine.
> - The application considers tasks with the same type and same description as _duplicates_. You will not be able to add tasks that are duplicates!
> - You are not allowed to use pipe characters (`|`) in task descriptions.
> - Refer to the [list of accepted date and datetime formats](#accepted-date-and-datetime-formats) when specifying dates/times.
> - Below is a list of shorthand symbols for tasks and what they represent on the user interface. Each task will have a type (i.e., `ToDo`, `Deadline` or `Event`) and a corresponding status (i.e., Completed/Incomplete).
> 
> | Symbol  | Description  |
> |---|---|
> | `[T]`  | To-do task  |
> | `[D]`  | Deadline task |
> | `[E]`  | Event task  |
> | `[X]`  | Completed task  |
> | `[ ]`  | Incomplete task  |

## Adding Tasks
### Add To-dos with `todo`
Adds a new to-do, a task without a deadline.

#### Usage: `todo <task-description>`

#### Examples
- `todo update resume`
- `todo clean room`

#### Expected Output
```
Got it. I have added this task:
[T][ ] update resume
You now have 1 task(s) in the list.
```

### Add Deadlines with `deadline`

Adds a new deadline with its associated due date and optionally a due time.

#### Usage: `deadline <task-description> /by <when>`

#### Examples
- `deadline submit iP /by 18/9/2026 23:59`
- `deadline complete Week 6 tutorial /by 17/9/2026`
- `deadline submit quiz answers /by 20/9/2026 12:00`

#### Expected Output
```
Got it. I have added this task:
[D][ ] submit iP (by: Sep 18 2026 23:59)
You now have 2 task(s) in the list.
```

### Add Events with `event`
Adds a new event with start and end dates, each with an optional time.

#### Usage: `event <task-description> /from <when> /to <when>`
> ⚠️ **Note:** The `/from` value cannot be later than the `/to` value.

#### Examples
- `event CS2103 tutorial /from 17/9/2026 13:00 /to 17/9/2026 14:00`
- `event 9/11 /from 11/9/2026 /to 11/9/2026`

#### Expected Output
```
Got it. I have added this task:
[E][ ] 9/11 (from: Sep 11 2026 to: Sep 11 2026)
You now have 3 task(s) in the list.
```

### Accepted date and datetime formats
The following formats can be used for populating `<when>` in the deadline and event commands. When a time is omitted, the time defaults to midnight (`00:00`). Midnight is omitted when only dates are displayed.

| Acceptable date format | Acceptable datetime format |
| --- | --- |
| `yyyy-M-d` (e.g., `2026-9-18`) | `yyyy-M-d HH:mm` (e.g., `2026-9-18 23:59`) |
| `yyyy/M/d` (e.g., `2026/9/18`) | `yyyy/M/d HH:mm` (e.g., `2026/9/18 23:59`) |
| `d-M-yyyy` (e.g., `18-9-2026`) | `d-M-yyyy HHmm` or `d-M-yyyy HH:mm` (e.g., `18-9-2026 2359` or `18-9-2026 23:59`) |
| `d/M/yyyy` (e.g., `18/9/2026`) | `d/M/yyyy HHmm` or `d/M/yyyy HH:mm` (e.g., `18/9/2026 2359` or `18/9/2026 23:59`) |
| `MMM d yyyy` (e.g., `Sep 18 2026`) | `MMM d yyyy HH:mm` (e.g., `Sep 18 2026 23:59`) |


## Viewing tasks with `list`
Displays the current list of tasks.

### Usage: `list`

### Expected Output
**Case 1:** The task list contains one or more tasks.
```
Here are the tasks in your list:
    1. [T][ ] update resume
    2. [D][ ] submit iP (by: Sep 18 2026 23:59)
    3. [E][ ] 9/11 (from: Sep 11 2026 to: Sep 11 2026)
```

**Case 2:** The current list does not contain any tasks.
```
Task list is empty!
```

## Removing a task with `delete`
Removes the task based on its index shown by the `list` command.

### Usage: `delete <task-index>`
> ⚠️ Note that indexing starts from one, following the numbering shown by the `list` command.

### Examples
- `delete 2`
- `delete 3`

### Expected Output
**Case 1:** The task at the specified index exists and is removed.
```
Noted. I will remove this task:
[E][ ] 9/11 (from: Sep 11 2026 to: Sep 11 2026)
You now have 2 task(s) in the list.
```

**Case 2:** The task at the specified index does not exist (i.e., no removal is performed).
```
Oh no! The task does not exist in the list.
```

## Search for tasks with `find`
Displays tasks whose descriptions contain the given search term. The search term is **case-insensitive**.

### Usage: `find <search-term>`

### Examples
- `find submit`
- `find iP`

### Expected Output
**Case 1:** Some tasks matching the search term were found.
```
Here are the matching tasks in your list:
    1. [D][ ] submit iP (by: Sep 18 2026 23:59)
    2. [D][ ] submit quiz answers (by: Sep 20 2026 12:00)
```

**Case 2:** No matching tasks were found for the given search term.
```
No tasks matching "abc" were found
```

## Managing Task Statuses
### Indicate task completion with `mark`
Marks a task as completed, based on its index shown by the `list` command.

#### Usage: `mark <task-index>`

#### Examples
- `mark 1`
- `mark 2`

#### Expected Output
```
Nice! I have marked this task as done:
[D][X] submit iP (by: Sep 18 2026 23:59)
```

### Indicate an incomplete task with `unmark`
Marks a task as not complete, based on its index shown by the `list` command.

#### Usage: `unmark <task-index>`

#### Examples
- `unmark 1`
- `unmark 2`

#### Expected Output
```
OK, I've marked this task as not done yet
[T][ ] update resume
```

## Remove duplicate tasks using `dedup`
Removes duplicate saved tasks from the task list. Tasks are considered duplicates when they have the same task type and description, ignoring differences in case.

The implementation preserves the first occurrence in the current task list, regardless of its date, time, or completion status.

This command is useful for removing duplicates already present in saved data.

### Usage: `dedup`

### Expected Output
**Case 1:** Duplicate entries are found and removed.
```
Removed the following duplicate task(s):
    1. [T][X] update RESUME
You now have 3 task(s) in the list.
```

**Case 2:** No duplicate entries are found, so no action is taken.
```
No duplicate tasks found.
```

## Exiting AnswerMe with `bye`
Terminates the chatbot session.

### Usage: `bye`

### Expected Output
```
Bye. Hope to see you again soon!
```

The application then exits.
