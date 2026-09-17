# Kiwi User Guide

Kiwi is a **desktop chatbot for managing to-dos, deadlines, and events**. Type commands in the box and press Enter (or **Send**) — Kiwi keeps your list on disk so it is still there the next time you open the app.

![The Kiwi chat window after listing tasks and adding a to-do](Ui.png)

* [Quick start](#quick-start)
* [Features](#features)
  * [Adding a to-do: `todo`](#adding-a-to-do-todo)
  * [Adding a deadline: `deadline`](#adding-a-deadline-deadline)
  * [Adding an event: `event`](#adding-an-event-event)
  * [Listing all tasks: `list`](#listing-all-tasks-list)
  * [Marking a task as done: `mark`](#marking-a-task-as-done-mark)
  * [Marking a task as not done: `unmark`](#marking-a-task-as-not-done-unmark)
  * [Finding tasks: `find`](#finding-tasks-find)
  * [Viewing tasks on a date: `on`](#viewing-tasks-on-a-date-on)
  * [Finding free time: `free`](#finding-free-time-free)
  * [Deleting a task: `delete`](#deleting-a-task-delete)
  * [Exiting the program: `bye`](#exiting-the-program-bye)
  * [Saving the data](#saving-the-data)
  * [Editing the data file](#editing-the-data-file)
* [Command summary](#command-summary)

---

## Quick start

1. Ensure that [Java 25](https://www.oracle.com/java/technologies/downloads/) or later is installed.
2. Download the latest `kiwi.jar` from [Releases](https://github.com/its-shoutime/ip/releases).
3. Copy the file to the folder you want as Kiwi's home folder (tasks are saved here as `data/kiwi.txt`).
4. Open a terminal, `cd` to that folder, and run:

   ```bash
   java -jar kiwi.jar
   ```

   A window titled **Kiwi** should appear in a few seconds. Chat replies stay in that window — they are not printed in the terminal.
5. Type a command and press Enter. For example, try `list`, then `todo borrow book`.

If you are running from the project source instead of a JAR, use `./gradlew run`.

---

## Features

:information_source: **Notes about the command format:**

* Words in `UPPER_CASE` are parameters you fill in.
  For example, in `todo DESCRIPTION`, replace `DESCRIPTION` with `borrow book`.
* Items in square brackets are optional.
  For example, `free HOURS [/from DATE]` can be `free 4` or `free 4 /from 2019-12-01`.
* Dates must be `yyyy-MM-dd` (for example `2019-12-02`). Kiwi shows them as `MMM dd yyyy` (for example `Dec 02 2019`).
* Extra spaces are fine; Kiwi treats repeated spaces as a single space.
* `list` and `bye` take no extra words. Commands that need a task number (`mark`, `unmark`, `delete`) take exactly one number from the `list`.

Each task has a type icon and a done icon:

* `[T]` to-do, `[D]` deadline, `[E]` event
* `[ ]` not done yet, `[X]` done

### Adding a to-do: `todo`

Adds a to-do (no date).

Format: `todo DESCRIPTION`

Example:

* `todo borrow book`

```text
Got it. I've added this task:
  [T][ ] borrow book
Now you have 1 task in the list.
```

Kiwi will not add a task that already matches one in your list (same type, description, and dates).

### Adding a deadline: `deadline`

Adds a task that is due on a date.

Format: `deadline DESCRIPTION /by DATE`

:bulb: **Tip:** Put spaces around `/by`.

Examples:

* `deadline return book /by 2019-12-02`
* `deadline submit report /by 2019-10-18`

```text
Got it. I've added this task:
  [D][ ] return book (by: Dec 02 2019)
Now you have 2 tasks in the list.
```

### Adding an event: `event`

Adds an event spanning one or more days (start and end dates are inclusive). Same-day events are allowed; the end date cannot be before the start date.

Format: `event DESCRIPTION /from DATE /to DATE`

Examples:

* `event project meeting /from 2019-12-01 /to 2019-12-03`
* `event meeting /from 2019-10-04 /to 2019-10-04`

```text
Got it. I've added this task:
  [E][ ] project meeting (from: Dec 01 2019 to: Dec 03 2019)
Now you have 3 tasks in the list.
```

### Listing all tasks: `list`

Shows every task, numbered from 1. Use these numbers with `mark`, `unmark`, and `delete`.

Format: `list`

```text
Here are the tasks in your list:
1.[T][ ] borrow book
2.[D][ ] return book (by: Dec 02 2019)
3.[E][ ] project meeting (from: Dec 01 2019 to: Dec 03 2019)
```

### Marking a task as done: `mark`

Marks the task at the given list number as done.

Format: `mark INDEX`

* `INDEX` is the number shown by `list`. It must be a positive integer 1, 2, 3, …

Example: `mark 1`

```text
Marked this task as done:
1.[T][X] borrow book
```

### Marking a task as not done: `unmark`

Marks the task at the given list number as not done yet.

Format: `unmark INDEX`

Example: `unmark 1`

```text
Marked this task as not done yet:
1.[T][ ] borrow book
```

### Finding tasks: `find`

Lists tasks whose description contains the given keyword.

Format: `find KEYWORD`

* Matching is a **case-sensitive** substring search (for example `book` matches `return book`, but `Book` does not).
* Matching tasks are numbered from 1 in the result. Those numbers are only for reading — use `list` if you need a number for `mark`, `unmark`, or `delete`.
* If nothing matches, Kiwi prints `None found.`

Examples:

* `find book` returns tasks such as `read book` and `borrow book`
* `find meeting` returns tasks whose description contains `meeting`

```text
Here are the matching tasks in your list:
1.[T][X] read book
2.[D][X] return book (by: Jun 06 2019)
3.[T][ ] borrow book
```

### Viewing tasks on a date: `on`

Shows deadlines due on that date and events whose date range covers it. To-dos are omitted.

Format: `on DATE`

* Numbers shown are the original `list` numbers, so you can `mark` or `delete` from this view.
* If nothing falls on that date, Kiwi prints `None found.`

Example: `on 2019-12-02`

```text
Here are the deadlines/events on Dec 02 2019:
2.[D][ ] return book (by: Dec 02 2019)
3.[E][ ] project meeting (from: Dec 01 2019 to: Dec 03 2019)
```

### Finding free time: `free`

Finds the nearest work-day slot with that many consecutive free hours.

Format: `free HOURS [/from DATE]`

* A work day is `08:00-18:00` (10 hours). `HOURS` must be between 1 and 10.
* Only **events** occupy the calendar — they block the whole work day for each day in their range. Deadlines and to-dos do not.
* Omit `/from` to start the search from today. Kiwi looks up to 365 days ahead.
* The slot starts at `08:00` on the first free day (for example a 4-hour slot is `08:00-12:00`).

Examples:

* `free 4`
* `free 4 /from 2019-12-01`

```text
The nearest 4-hour free slot is on Dec 04 2019, 08:00-12:00.
```

If every day in the search window is taken, Kiwi tells you it could not find a slot.

### Deleting a task: `delete`

Removes the task at the given list number. Later tasks move up.

Format: `delete INDEX`

Example: `list` followed by `delete 3` deletes the 3rd task in the list.

```text
Noted. I've removed this task:
  [E][ ] project meeting (from: Dec 01 2019 to: Dec 03 2019)
Now you have 2 tasks in the list.
```

### Exiting the program: `bye`

Saves nothing extra (data is already saved) and closes Kiwi.

Format: `bye`

The window shows `Bye. Hope to see you again soon!` and then closes.

### Saving the data

Kiwi saves after every change (add, mark, unmark, delete). You do not need to save manually.

Tasks are stored as a text file `[JAR file location]/data/kiwi.txt`.

### Editing the data file

Advanced users can edit `data/kiwi.txt` directly. Each line looks like:

```text
T | 1 | read book
D | 0 | return book | 2019-12-02
E | 0 | project meeting | 2019-10-04 | 2019-10-11
```

The middle field is `1` if done and `0` if not. Descriptions cannot contain ` | `.

:exclamation: **Caution:** If a line is invalid, Kiwi skips it (and warns you) when it next starts. Back up the file before editing it.

---

## Command summary

| Action | Format, examples |
| --- | --- |
| **To-do** | `todo DESCRIPTION` e.g., `todo borrow book` |
| **Deadline** | `deadline DESCRIPTION /by DATE` e.g., `deadline return book /by 2019-12-02` |
| **Event** | `event DESCRIPTION /from DATE /to DATE` e.g., `event meeting /from 2019-10-04 /to 2019-10-11` |
| **List** | `list` |
| **Mark** | `mark INDEX` e.g., `mark 1` |
| **Unmark** | `unmark INDEX` e.g., `unmark 1` |
| **Find** | `find KEYWORD` e.g., `find book` |
| **On** | `on DATE` e.g., `on 2019-12-02` |
| **Free** | `free HOURS [/from DATE]` e.g., `free 4` or `free 4 /from 2019-12-01` |
| **Delete** | `delete INDEX` e.g., `delete 3` |
| **Bye** | `bye` |
