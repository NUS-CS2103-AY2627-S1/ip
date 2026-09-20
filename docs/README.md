# Fifi User Guide

Fifi is a personal assistant chatbot for keeping track of todos, deadlines, and
events. Use its graphical chat interface or console interface to manage tasks,
find tasks, and view completion statistics.

![Fifi managing realistic tasks through its graphical interface](Ui.png)

## Installation and launch

1. Install Java 25. Open a terminal and run `java -version` to check that it reports
   version 25.
2. Obtain `fifi.jar` by following the repository's
   [build instructions](https://github.com/yyihengg/ip#building-and-running). Place it in the folder
   where you want to keep your task data.
3. Open a terminal in that folder and run:

   ```text
   java -jar fifi.jar
   ```

4. Type a command in the input box and press **Enter** or click **Send**.
5. Enter `bye` to end the conversation. Fifi shows its farewell and closes the
   GUI automatically.

## Features

Replace uppercase placeholders with your own values, and enter each command on
one line. All shown parameters are required; descriptions can include spaces
without quotation marks.

A few helpful rules:

- Use lowercase commands. Leading/trailing spaces, extra spaces, and tabs between
  command parts are welcome.
- Dates must be real dates in `yyyy-MM-dd`, with years 0001–9999. Times are not supported.
- Descriptions cannot be blank or contain `|` or control characters other than tabs.
  Ordinary punctuation and Unicode text are fine.
- Fifi holds up to **100 tasks**. Delete an unwanted task if you need more room.
- New duplicates are rejected: the same task type, trimmed case-sensitive
  description, and dates count as a duplicate, even if completion status differs.
  Different types or dates are allowed; older saved duplicates still load.
- Use numbers from the full **`list`** for task changes. `find` and `show` number
  their own results. Deleting a task changes later numbers, so check `list` again.

### Viewing your tasks: `list`

See all your tasks, including completed ones.

**Format and example:** `list`

```text
Here are the tasks in your list:
1. [T][ ] read book
2. [D][X] return book (by: Sep 20 2026)
```

`[T]`, `[D]`, and `[E]` mean todo, deadline, and event. `[ ]` means incomplete;
`[X]` means complete. An empty list shows just the heading. No extra arguments are allowed.

### Adding a todo: `todo`

Keep track of something you need to do, without a date.

**Format:** `todo DESCRIPTION`

**Example:** `todo read book`

Fifi adds `[T][ ] read book` and tells you the new task count. Todos do not appear
in date searches (`show`). Remember to include a description!

### Adding a deadline: `deadline`

Add a task with a due date.

**Format:** `deadline DESCRIPTION /by DATE`

**Example:** `deadline return book /by 2026-09-20`

Fifi adds `[D][ ] return book (by: Sep 20 2026)` and reports the new task count.
Use `/by` once, separated from its date by whitespace. Past dates are allowed;
incomplete deadlines due before today are overdue, while those due today are pending.

### Adding an event: `event`

Plan an event over one or more days.

**Format:** `event DESCRIPTION /from START_DATE /to END_DATE`

**Example:** `event project meeting /from 2026-09-20 /to 2026-09-22`

Fifi adds the event with its dates and reports the new task count. Both boundary
dates are included, so this event appears in `show` on September 20, 21, and 22.
Same-day events are welcome. Use `/from` before `/to`, once each, separated from
their dates by whitespace. The end date cannot be earlier than the start date.

### Completing a task: `mark`

Done with a task? Mark it complete.

**Format:** `mark NUMBER`

**Example:** `mark 1` changes task 1 from `[ ]` to `[X]`.

The task stays in your list and counts as completed in `stats`. Marking it again
updates its latest completion time; its creation time stays the same.

### Reopening a task: `unmark`

Bring a task back to your to-do list.

**Format:** `unmark NUMBER`

**Example:** `unmark 1` changes task 1 from `[X]` to `[ ]`.

Its creation and last completion times are kept, but `stats` treats it as incomplete.
Unmarking an already incomplete task is fine.

### Removing a task: `delete`

Remove a task you no longer need.

**Format:** `delete NUMBER`

**Example:** Run `list`, then `delete 2` to remove its second task.

Fifi shows the removed task and remaining count. Deleted tasks no longer appear
in statistics. There is **no undo or confirmation**, so check the number first.

For `mark`, `unmark`, and `delete`, use an existing positive task number with no
extra arguments. Invalid numbers leave your tasks unchanged.

### Viewing a date: `show`

See deadlines due on a date and events happening that day.

**Format:** `show DATE`

**Example:** `show 2026-09-20`

The results include completed tasks and both ends of an event's date range.
Todos are excluded. Past and future dates are allowed; if nothing matches,
Fifi shows just the results heading. Use `list` numbers when changing tasks.

### Finding a task: `find`

Search any task's description, including completed tasks.

**Format:** `find KEYWORD`

**Example:** `find book` matches `read book`, `return book`, and `bookcase shopping`.

Matching is case-sensitive: `find Book` does not match `read book`. Multiple words
form one exact phrase, so `find read book` looks for that phrase, including its
internal spaces. Dates and type labels are not searched. No matches means an empty
results list. Use `list` numbers when changing a result.

### Checking your progress: `stats`

Get current totals, completed/incomplete counts, and a completion percentage.

**Formats:** `stats` or `stats all`

**Example:** With two tasks and one complete, `stats` reports a 50% completion rate.

The report also breaks tasks down by type:

| Type | Statuses |
| --- | --- |
| Todos | Completed or incomplete. |
| Deadlines | Completed; pending if due today or later; overdue if due before today. |
| Events | Completed; upcoming before the start; ongoing within the inclusive date range; past after the end. |

Completed tasks always count as completed, regardless of dates. Other statuses
use today's date on your computer. Percentages are rounded to the nearest whole
number; an empty list reports zero counts and 0%. This shows current progress,
not a history of deleted tasks or past statuses.

### Checking recent tasks: `stats since`

Check progress for tasks **created** from a chosen date through now.

**Format:** `stats since DATE`

**Example:** `stats since 2026-08-24` includes tasks created on August 24 or later.

The start date is inclusive and must be today or earlier. This filters by creation
time, not due date or completion time, and reports current statuses. No qualifying
tasks means zero counts and 0%. Older tasks with unknown creation times are excluded
and counted in a note; they still appear in `list` and overall `stats`.

### Saying goodbye: `bye`

**Format and example:** `bye`

Fifi says `BaiBai! Hope to see you soon ^^`. The GUI and console then exit
automatically.
No extra arguments are allowed: `bye extra` gives an error and keeps the conversation open.

### Saving your tasks

Fifi automatically saves successful task changes to `data/fifi.txt` in the folder
where you launch it, and loads them next time. A missing file is normal; Fifi
creates it when needed.

Launch from the same folder each time to use the same data. Back up the file before
editing it manually or if you want a recovery copy before deleting tasks.

## When something goes wrong

Fifi highlights errors in red in the GUI, or with `[ERROR]` in the console.
Read the message, adjust your command, and try again. For example, `show tomorrow`
asks you to use `yyyy-MM-dd` instead.

- **A save fails:** your task list and timestamps stay unchanged. Check file access
  and disk space, then retry. Saving requires support for atomic file replacement;
  if that is unavailable, Fifi reports an error and preserves the old file.
- **Saved data cannot load:** Fifi warns you and blocks changes to protect the file.
  Back it up, fix the reported problem, and restart. Read-only commands remain
  available, but show an empty list until loading succeeds.
- **A task's creation time is in the future:** marking is blocked to keep its history
  valid. Check your system clock or correct the saved timestamp and restart.

## Feature summary

| Command | Example | What it does |
| --- | --- | --- |
| `list` | `list` | Lists all tasks with their task numbers. |
| `todo DESCRIPTION` | `todo read book` | Adds a task without a date. |
| `deadline DESCRIPTION /by DATE` | `deadline return book /by 2026-09-20` | Adds a task with a due date. |
| `event DESCRIPTION /from DATE /to DATE` | `event project meeting /from 2026-09-20 /to 2026-09-21` | Adds an event with inclusive start and end dates. |
| `mark NUMBER` | `mark 1` | Marks a task as completed. |
| `unmark NUMBER` | `unmark 1` | Marks a task as incomplete. |
| `delete NUMBER` | `delete 1` | Deletes a task. |
| `show DATE` | `show 2026-09-20` | Lists deadlines due on that date and events occurring on that date. |
| `find KEYWORD` | `find book` | Finds tasks whose descriptions contain the keyword; matching is case-sensitive. |
| `stats` or `stats all` | `stats` | Shows completion statistics and a status breakdown by task type. |
| `stats since DATE` | `stats since 2026-08-24` | Shows statistics for tasks created from that date through now. |
| `bye` | `bye` | Ends the conversation. |

## Developer resources

See the repository README for [developer setup](https://github.com/yyihengg/ip#developer-setup) and
[testing instructions](https://github.com/yyihengg/ip#testing).

## Acknowledgements

- [SE-EDU JavaFX tutorial Part 4](https://se-education.org/guides/tutorials/javaFxPart4.html)
  provides the FXML layout and controller patterns adapted for Fifi.
- [JavaFX (OpenJFX)](https://openjfx.io/) provides Fifi's graphical interface.
- [JUnit 5](https://junit.org/junit5/) is used for automated unit testing.

# Use of AI-generated work
From course Week 3 onwards, Young Yi Heng (the developer of this project) used OpenAI Codex to support development of this project. Codex was used to plan changes and discuss ideas before generating code based on Young Yi Heng’s plans and implementation decisions.
