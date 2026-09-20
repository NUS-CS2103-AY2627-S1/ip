# AXIOM User Guide

<img src="images/axiom-logo.png" width="96" alt="AXIOM logo">

**AXIOM** is a desktop chatbot that helps you keep track of todos, deadlines, and events.
Type a command, and AXIOM replies in a chat window (or in the terminal, if you run the text interface).

This guide explains how to start AXIOM and how to use every command.

## Table of contents

* [Quick start](#quick-start)
* [Command format](#command-format)
* [Features](#features)
  * [Adding a todo: `todo`](#adding-a-todo-todo)
  * [Adding a deadline: `deadline`](#adding-a-deadline-deadline)
  * [Adding an event: `event`](#adding-an-event-event)
  * [Listing tasks: `list`](#listing-tasks-list)
  * [Finding tasks: `find`](#finding-tasks-find)
  * [Sorting tasks: `sort`](#sorting-tasks-sort)
  * [Marking a task as done: `mark`](#marking-a-task-as-done-mark)
  * [Marking a task as not done: `unmark`](#marking-a-task-as-not-done-unmark)
  * [Deleting a task: `delete`](#deleting-a-task-delete)
  * [Exiting: `bye`](#exiting-bye)
* [Dates and times](#dates-and-times)
* [Saving your tasks](#saving-your-tasks)
* [Using the window](#using-the-window)
* [Common errors](#common-errors)
* [Command summary](#command-summary)

## Quick start

1. Ensure you have **Java 25** installed.
   * On macOS with SDKMAN: `sdk use java 25.0.3.fx-zulu`
2. Open a terminal in the project folder.
3. Start AXIOM in one of these ways:

   **From source (GUI)**

   ```bash
   ./gradlew run
   ```

   **From the packaged JAR (GUI)**

   ```bash
   ./gradlew shadowJar
   java -jar build/libs/axiom.jar
   ```

4. When the window opens, AXIOM greets you:

   ```text
   Hello! I'm AXIOM.
   What can I do for you?
   ```

5. Type a command in the box at the bottom and press <kbd>Enter</kbd> (or click **Send**).
6. Type `bye` when you are done. AXIOM says goodbye and closes the window.

> **Tip:** Your tasks are stored in `data/axiom.txt`. The next time you start AXIOM, it loads that file automatically.

## Command format

> **Notes about the command format:**
>
> * Words in `UPPER_CASE` are values you type in, e.g. in `todo DESCRIPTION`, `DESCRIPTION` can be `read book`.
> * Items after a `/flag` are part of that flag, e.g. `/by DATE` or `/from START /to END`.
> * Extra spaces before, after, or between words are ignored.
> * Commands are case-sensitive: use `list`, not `List` or `LIST`.
> * Task numbers shown by `list` and `find` are **1-based** (the first task is `1`).

## Features

### Adding a todo: `todo`

Adds a task with no date attached.

**Format:** `todo DESCRIPTION`

* `DESCRIPTION` cannot be empty.
* `DESCRIPTION` cannot contain `|` (that character is reserved for the save file).

**Example:**

```text
todo read book
```

**Expected outcome:**

```text
Got it. I've added this task:
   [T][ ] read book
Now you have 1 tasks in the list.
```

`[T]` means todo. `[ ]` means not done yet.

### Adding a deadline: `deadline`

Adds a task that must be done by a date or date-and-time.

**Format:** `deadline DESCRIPTION /by DATE`

* You must include exactly one `/by`.
* See [Dates and times](#dates-and-times) for accepted formats.

**Examples:**

```text
deadline return book /by 2019-10-15
deadline return book /by 2/12/2019 1800
```

**Expected outcome:**

```text
Got it. I've added this task:
   [D][ ] return book (by: Oct 15 2019)
Now you have 1 tasks in the list.
```

`[D]` means deadline. A date with no time is stored as the start of that day.

### Adding an event: `event`

Adds a task with a start and an end.

**Format:** `event DESCRIPTION /from START /to END`

* You must include `/from` before `/to`.
* `START` must be **earlier** than `END` (the same instant is not allowed).
* Each flag may appear only once.

**Example:**

```text
event project meeting /from 2019-08-06 1400 /to 2019-08-06 1600
```

**Expected outcome:**

```text
Got it. I've added this task:
   [E][ ] project meeting (from: Aug 06 2019, 2:00 PM to: Aug 06 2019, 4:00 PM)
Now you have 1 tasks in the list.
```

`[E]` means event.

### Listing tasks: `list`

Shows every task in the current order, numbered from 1.

**Format:** `list`

**Example:**

```text
list
```

**Expected outcome:**

```text
Here are the tasks in your list:
 1.[T][ ] read book
 2.[D][ ] return book (by: Oct 15 2019)
 3.[E][ ] project meeting (from: Aug 06 2019, 2:00 PM to: Aug 06 2019, 4:00 PM)
```

Use these numbers with `mark`, `unmark`, and `delete`.

### Finding tasks: `find`

Shows tasks whose description contains a keyword (case-insensitive). Numbers in the result are the tasks’ original list numbers.

**Format:** `find KEYWORD`

**Example:** after adding `todo read book` and `deadline return book /by 2019-06-06`:

```text
find book
```

**Expected outcome:**

```text
Here are the matching tasks in your list:
 1.[T][ ] read book
 2.[D][ ] return book (by: Jun 06 2019)
```

### Sorting tasks: `sort`

Reorders the list by date and saves the new order.

**Format:** `sort`

* Deadlines are ordered by their `/by` time.
* Events are ordered by their `/from` time.
* Todos (no date) appear **after** dated tasks, keeping their relative order.
* Tasks with the same date keep their relative order.

**Example:**

```text
sort
```

**Expected outcome:**

```text
OK, I've sorted your tasks chronologically:
 1.[D][ ] homework (by: Jun 06 2019)
 2.[E][ ] project meeting (from: Aug 06 2019, 2:00 PM to: Aug 06 2019, 4:00 PM)
 3.[D][ ] return book (by: Oct 15 2019)
 4.[T][ ] read book
```

`list` after `sort` shows the same order.

### Marking a task as done: `mark`

Marks the task at the given list number as done. The status icon becomes `[X]`.

**Format:** `mark INDEX`

**Example:**

```text
mark 2
```

**Expected outcome:**

```text
Nice! I've marked this task as done:
   [D][X] return book (by: Oct 15 2019)
```

### Marking a task as not done: `unmark`

Marks the task at the given list number as not done. The status icon becomes `[ ]`.

**Format:** `unmark INDEX`

**Example:**

```text
unmark 2
```

**Expected outcome:**

```text
OK, I've marked this task as not done yet:
   [D][ ] return book (by: Oct 15 2019)
```

### Deleting a task: `delete`

Removes the task at the given list number. Later tasks are renumbered.

**Format:** `delete INDEX`

**Example:**

```text
delete 1
```

**Expected outcome:**

```text
Noted. I've removed this task:
   [T][ ] read book
Now you have 2 tasks in the list.
```

### Exiting: `bye`

Saves nothing extra (tasks are already saved after each change) and closes AXIOM.

**Format:** `bye`

**Expected outcome:**

```text
Bye. Hope to see you again soon!
```

In the GUI, the window closes shortly after this message.

## Dates and times

You can type dates in any of these forms:

| What you type | Meaning |
| --- | --- |
| `2019-10-15` | 15 Oct 2019, start of day |
| `2/12/2019` | 2 Dec 2019, start of day |
| `2/12/2019 1800` | 2 Dec 2019, 6:00 PM |
| `6/8/2019 14:00` | 6 Aug 2019, 2:00 PM |
| `2019-08-06 1400` | 6 Aug 2019, 2:00 PM |
| `2019-08-06T14:00` | 6 Aug 2019, 2:00 PM |

AXIOM checks that the calendar date is real. `2019-02-30` is rejected; it is not turned into 28 Feb.

Times use 24-hour clock (`1800` or `18:00`). AXIOM displays them in a readable form such as `6:00 PM`.

## Saving your tasks

* After every successful add, mark, unmark, delete, or sort, AXIOM writes `data/axiom.txt`.
* If the `data` folder does not exist yet, AXIOM creates it on the first save.
* On startup, AXIOM loads that file. If the file is missing, you start with an empty list.
* If the file cannot be read (wrong format, duplicate tasks, reversed event times, or no permission), AXIOM shows an error and starts with an empty list. You can still add new tasks.

> **Warning:** Do not edit `data/axiom.txt` by hand unless you know the format. A damaged line will prevent the whole file from loading.

## Using the window

* Your commands appear on the **right** as compact chips. AXIOM’s replies appear on the **left** as wider cards.
* Errors (unknown commands, bad dates, duplicates, and so on) use a **red-accented** card so they stand out.
* You can **resize** the window. Task lists wrap to the new width.
* Press <kbd>Enter</kbd> in the input box to send. Click **Send** if you prefer the mouse.

## Common errors

| Situation | What AXIOM tells you (summary) |
| --- | --- |
| Empty input | Please enter a command. |
| Unknown command | Sorry, I don't understand that command. |
| `todo` with no description | A todo needs a description. |
| Missing `/by`, `/from`, or `/to` | Usage hint for that command. |
| `/by` (or `/from` / `/to`) twice | The flag is specified more than once. |
| Event start not before end | The event `/from` time must be earlier than the `/to` time. |
| Impossible date such as 30 Feb | `'…'` is not a valid date or time. |
| Same task added again | That task is already in your list. |
| `mark 99` when you have fewer tasks | That task number isn't in your list. |
| `list extra` (or extra words after `bye` / `sort`) | That command does not take any arguments. |
| Description contains `\|` | Task descriptions cannot contain `'\|'`. |

A duplicate is the same **type**, **description**, and **dates**. Marking a task done does not make a second copy allowed.

## Command summary

| Command | Format | What it does |
| --- | --- | --- |
| Todo | `todo DESCRIPTION` | Adds a todo |
| Deadline | `deadline DESCRIPTION /by DATE` | Adds a deadline |
| Event | `event DESCRIPTION /from START /to END` | Adds an event |
| List | `list` | Shows all tasks |
| Find | `find KEYWORD` | Shows matching tasks |
| Sort | `sort` | Orders tasks by date |
| Mark | `mark INDEX` | Marks a task done |
| Unmark | `unmark INDEX` | Marks a task not done |
| Delete | `delete INDEX` | Removes a task |
| Bye | `bye` | Exits AXIOM |
