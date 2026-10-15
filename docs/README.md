# James the ぱんどろぼう — User Guide

James is a playful bread-thief chatbot that keeps track of your tasks, deadlines, and events.

[Getting started](#getting-started) · [Commands](#commands) · [Saving and undo](#saving-and-undo) · [Command examples](#command-examples)

<img src="Ui.png" alt="James chatbot interface" width="420">

## Getting started

With **JDK 25** installed, open a terminal in the project folder and run `./gradlew run`
(`gradlew.bat run` on Windows). Type a [command](#commands) into the chat box and press
**Enter** or click **Send**.

For IntelliJ IDEA, open the project, select **JDK 25** as the Project SDK and
**SDK default** as the language level, then run
[`Launcher.main()`](../src/main/java/james/gui/Launcher.java).
For the console interface, run [`James.main()`](../src/main/java/james/James.java) instead.

## Commands

**Notes about the command format:**

- Words in `UPPER_CASE` are required values you supply: `todo DESCRIPTION` becomes `todo read book`.
- Type command words and options such as `/by`, `/from`, and `/to` as shown.
  Keep parameters in the shown order; for events, `/from` must precede `/to`.
- Descriptions and search keywords may contain spaces; quotation marks are unnecessary.
- Dates use **`yyyy-mm-dd`**; an event must end after its start date.
- `list`, `undo`, `random_sticker`, and `bye` take no parameters. Extra arguments cause an error.
- No commands below have optional or repeatable parameters. Square brackets in task output
  indicate task type or completion status, as explained [below](#task-numbers).

| Action | Format | Example |
| --- | --- | --- |
| [Add a task](#adding-a-task) | `todo DESCRIPTION` | `todo read book` |
| [Add a deadline](#adding-a-deadline) | `deadline DESCRIPTION /by DATE` | `deadline submit report /by 2026-09-20` |
| [Add an event](#adding-an-event) | `event DESCRIPTION /from START_DATE /to END_DATE` | `event study camp /from 2026-09-21 /to 2026-09-23` |
| [List all tasks](#listing-all-tasks) | `list` | `list` |
| [Search descriptions (case-insensitive, partial matches)](#finding-tasks) | `find KEYWORD` | `find book` |
| [Show deadlines due that day and events spanning it (inclusive)](#listing-tasks-by-date) | `list_by_date DATE` | `list_by_date 2026-09-22` |
| [Mark a task as done](#marking-a-task-as-done) | `mark TASK_NUMBER` | `mark 1` |
| [Mark a task as incomplete](#marking-a-task-as-incomplete) | `unmark TASK_NUMBER` | `unmark 1` |
| [Delete a task](#deleting-a-task) | `delete TASK_NUMBER` | `delete 1` |
| [Undo the last task change](#undoing-a-change) | `undo` | `undo` |
| [Show a random sticker in the chat window](#requesting-a-random-sticker) | `random_sticker` | `random_sticker` |
| [Exit James](#exiting-james) | `bye` | `bye` |

### Task numbers

Use the numbers from the full `list`, starting at **1**, when marking,
unmarking, or deleting. Search and date results have their own numbering; run `list`
first to get the correct number. `[T]`, `[D]`, and `[E]` identify tasks, deadlines, and
events; `[X]` means done and `[ ]` means incomplete. Duplicate tasks are rejected.

## Saving and undo

Successful task changes are saved automatically to `data/james.txt`, relative to the
folder you launch James from, and loaded on the next launch.
Use `undo` repeatedly to reverse up to **20 task changes in the current session**
(add, delete, mark, or unmark). Undo also saves the restored tasks; its history clears
when James closes. There is no redo.

## Command examples

Follow these examples **in order, in one session starting with an empty task list**.
If you already have saved tasks, your task numbers and counts will differ.
Output blocks show James's response text without the console divider lines or GUI stickers.
The sticker and exit commands have interface-specific behavior described below.

### Adding a task

**Format:** `todo DESCRIPTION`

**Use:** Creates an incomplete task without a date. Supply a nonempty description; it may contain spaces, but not `|` or control characters.

**Example:** Add your first task, `read book`.

```text
todo read book
```

**Output:**

```text
Hehe! Tucked this task into my bread basket:
[T][ ] read book
Now you have 1 tasks in the list.
```

The task is saved as task 1. `[T][ ]` identifies an incomplete to-do.

### Adding a deadline

**Format:** `deadline DESCRIPTION /by DATE`

**Use:** Creates an incomplete task with a due date. Put `/by` after the description and supply a valid date in `yyyy-mm-dd` format.

**Example:** Add a report due on 20 September 2026.

```text
deadline submit report /by 2026-09-20
```

**Output:**

```text
Hehe! Tucked this task into my bread basket:
[D][ ] submit report (by: Sep 20 2026)
Now you have 2 tasks in the list.
```

The deadline becomes task 2. James displays dates as `MMM dd yyyy`, even though commands use `yyyy-mm-dd`.

### Adding an event

**Format:** `event DESCRIPTION /from START_DATE /to END_DATE`

**Use:** Creates an incomplete event with start and end dates. Supply `/from` before `/to`; the end date must be later than the start date.

**Example:** Add a study camp running from 21 to 23 September 2026.

```text
event study camp /from 2026-09-21 /to 2026-09-23
```

**Output:**

```text
Hehe! Tucked this task into my bread basket:
[E][ ] study camp (from: Sep 21 2026 to: Sep 23 2026)
Now you have 3 tasks in the list.
```

The event becomes task 3. Same-day events and end dates before the start date are rejected.

### Listing all tasks

**Format:** `list`

**Use:** Shows every task, including completed tasks, in its current list order. This is the source of task numbers for `mark`, `unmark`, and `delete`.

**Example:** View the three tasks added above.

```text
list
```

**Output:**

```text
Let's peek in the basket. Your tasks:
1.[T][ ] read book
2.[D][ ] submit report (by: Sep 20 2026)
3.[E][ ] study camp (from: Sep 21 2026 to: Sep 23 2026)
```

If the list is empty, James displays only the heading. Listing tasks does not change them or consume undo history.

### Finding tasks

**Format:** `find KEYWORD`

**Use:** Finds tasks whose descriptions contain the supplied text, ignoring letter case. A phrase containing spaces is matched as one phrase.

**Example:** Search for `book`; `find BOOK` produces the same result.

```text
find book
```

**Output:**

```text
Sniff sniff... here are the matching tasks:
1.[T][ ] read book
```

Only matching tasks are shown. If nothing matches, only the heading appears. Results are numbered separately; use [list](#listing-all-tasks) before changing a task.

### Listing tasks by date

**Format:** `list_by_date DATE`

**Use:** Shows deadlines due on the given date and events whose date range includes it, including both endpoints. To-dos have no date and are excluded.

**Example:** Check what is scheduled for 22 September 2026.

```text
list_by_date 2026-09-22
```

**Output:**

```text
Tasks on the menu for 2026-09-22:
1.[E][ ] study camp (from: Sep 21 2026 to: Sep 23 2026)
```

The camp matches because it spans that date. Its result number is 1, but its full-list number is 3. With no matches, only the date heading appears.

### Marking a task as done

**Format:** `mark TASK_NUMBER`

**Use:** Marks the task at the supplied full-list number as complete. The number must be a whole number from 1 to the current list size.

**Example:** Complete `read book`, which is task 1 in the full list.

```text
mark 1
```

**Output:**

```text
Yatta! Task done. Time for a bread break:
[T][X] read book
```

`[X]` replaces `[ ]`, and the change is saved. Marking an already completed task gives the same response without adding an undo entry.

### Marking a task as incomplete

**Format:** `unmark TASK_NUMBER`

**Use:** Marks a completed task as incomplete using its full-list number.

**Example:** Reopen `read book` after marking it done above.

```text
unmark 1
```

**Output:**

```text
Back in the oven! This task is not done yet:
[T][ ] read book
```

The task returns to `[ ]`. Unmarking an already incomplete task gives the same response without adding an undo entry.

### Deleting a task

**Format:** `delete TASK_NUMBER`

**Use:** Removes the task at the supplied full-list number and saves the updated list.

**Example:** Delete `read book`, currently task 1.

```text
delete 1
```

**Output:**

```text
Poof! Snatched this task out of the basket:
[T][ ] read book
Now you have 2 tasks in the list.
```

Two tasks remain, and their numbers shift: the report becomes task 1 and the camp task 2. Use [undo](#undoing-a-change) to restore the deleted task.

### Undoing a change

**Format:** `undo`

**Use:** Reverses the latest successful task change. See [Saving and undo](#saving-and-undo) for the session limit and persistence rules.

**Example:** Immediately undo the deletion from the previous example.

```text
undo
```

**Output:**

```text
Tiptoe back! Undid the last change.
Now you have 3 tasks in the list.
```

`read book` returns to task 1 with its incomplete status, and the other tasks return to their original positions. Listing and searching do not add undo entries. If no history remains, James replies:

```text
Not a crumb to retrace. Nothing to undo.
```

### Requesting a random sticker

**Format:** `random_sticker`

**Use:** Requests a randomly selected sticker without changing tasks or undo history.

**Example:** Ask James for a sticker.

```text
random_sticker
```

**Console output:**

```text
Hehe! A little treat from my secret stash!
```

The chat window shows a sticker without reply text; the image can vary and may repeat. The text above is the console fallback.

### Exiting James

**Format:** `bye`

**Use:** Ends the current session. Successful task changes have already been saved automatically.

**Example:** Close James after finishing the walkthrough.

```text
bye
```

**Output:**

```text
Mata ne! Rest up. I smell fresh bread!
```

The GUI closes its window immediately, so the farewell may not remain visible. The console prints the farewell and exits. Your tasks load on the next launch, but undo history does not.
