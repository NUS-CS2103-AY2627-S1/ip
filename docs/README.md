# Myriad User Guide

**Myriad** is a chatbot that keeps track of your tasks. You type short commands into a chat window, and Myriad
remembers your to-dos, deadlines and events for you, even after you close it.

Myriad is a *task painter*. Your task list is its canvas, and every task you add is a fresh stroke of paint on it.

If you can type quickly, Myriad lets you add and find tasks faster than clicking through a to-do app.

![Myriad chat window, showing a painted list of to-dos, deadlines and events](Ui.png)

- [Quick start](#quick-start)
- [Features](#features)
  - [Adding a to-do: `todo`](#adding-a-to-do-todo)
  - [Adding a deadline: `deadline`](#adding-a-deadline-deadline)
  - [Adding an event: `event`](#adding-an-event-event)
  - [Listing all tasks: `list`](#listing-all-tasks-list)
  - [Marking a task as done: `mark` / `unmark`](#marking-a-task-as-done-mark--unmark)
  - [Deleting a task: `delete`](#deleting-a-task-delete)
  - [Finding tasks by keyword: `find`](#finding-tasks-by-keyword-find)
  - [Showing tasks on a date: `show`](#showing-tasks-on-a-date-show)
  - [Viewing statistics: `stats`](#viewing-statistics-stats)
  - [Recalling earlier commands](#recalling-earlier-commands)
  - [Exiting: `bye`](#exiting-bye)
  - [Saving your data](#saving-your-data)
- [FAQ](#faq)
- [Command summary](#command-summary)

---

## Quick start

1. Make sure you have **Java 25** installed. You can check by running `java -version` in a terminal.
2. Download the latest `myriad.jar` from the project's Releases page.
3. Put `myriad.jar` in the folder where you want Myriad to keep your tasks.
4. Open a terminal in that folder and run:
   ```
   java -jar myriad.jar
   ```
   A chat window opens and Myriad greets you, ready to paint.
5. Type a command in the box at the bottom and press **Enter** (or click **Paint**). Try these:
   - `todo read book` adds a to-do.
   - `list` shows all your tasks.
   - `mark 1` marks the first task as done.
   - `bye` closes Myriad.
6. See [Features](#features) below for every command.

---

## Features

**Notes about the command format:**

- Words in `UPPER_CASE` are what you fill in.<br>
  e.g. in `todo DESCRIPTION`, `DESCRIPTION` is what you type, as in `todo read book`.
- Commands and the `/by`, `/from` and `/to` markers are not case-sensitive.<br>
  e.g. `TODO read book` and `deadline essay /BY 2019-12-02` both work.
- `list`, `stats` and `bye` take nothing after them.<br>
  e.g. `list all` is not understood.
- `TASK_NUMBER` is the number shown next to a task in `list`. It starts at 1.
- A task description cannot contain the `|` character, because Myriad uses it in its save file.

**Accepted date formats:**

Wherever a command asks for a `DATE`, you can type a date on its own, or a date with a time (24-hour clock):

| Form          | Examples                                   |
|---------------|--------------------------------------------|
| Date only     | `2019-12-02`, `2/12/2019`                  |
| Date and time | `2019-12-02 1800`, `2019-12-02 18:00`, `2/12/2019 1800` |

Myriad shows dates back to you like `Dec 02 2019` or `Dec 02 2019 1800`.
Dates that don't exist, such as `2019-02-30`, are rejected.

**Reading a task:**

Each task is shown like `[D][X] return book (by: Dec 02 2019)`.

- The first box is the task type: `T` for to-do, `D` for deadline, `E` for event.
- The second box is `X` if the task is done, or empty if it isn't.

### Adding a to-do: `todo`

Adds a task with no date.

Format: `todo DESCRIPTION`

Example: `todo read book`

```
A fresh stroke on the canvas:
[T][ ] read book
Your canvas now holds 1 task.
```

### Adding a deadline: `deadline`

Adds a task that must be done by a certain date.

Format: `deadline DESCRIPTION /by DATE`

Example: `deadline return book /by 2019-12-02`

```
A fresh stroke on the canvas:
[D][ ] return book (by: Dec 02 2019)
Your canvas now holds 2 tasks.
```

### Adding an event: `event`

Adds a task that starts and ends at certain times.

Format: `event DESCRIPTION /from DATE /to DATE`

- The end must be after the start.

Example: `event project meeting /from 2019-12-02 1400 /to 2019-12-02 1600`

```
A fresh stroke on the canvas:
[E][ ] project meeting (from: Dec 02 2019 1400 to: Dec 02 2019 1600)
Your canvas now holds 3 tasks.
```

### Listing all tasks: `list`

Shows every task, numbered.

Format: `list`

```
Here's your canvas so far:
1.[T][ ] read book
2.[D][ ] return book (by: Dec 02 2019)
3.[E][ ] project meeting (from: Dec 02 2019 1400 to: Dec 02 2019 1600)
```

### Marking a task as done: `mark` / `unmark`

`mark` marks a task as done. `unmark` marks it as not done again.

Format: `mark TASK_NUMBER`, `unmark TASK_NUMBER`

Example: `mark 1`

```
Beautiful! That one's painted in:
  [T][X] read book
```

Example: `unmark 1`

```
Back to a sketch. Marked as not done:
  [T][ ] read book
```

### Deleting a task: `delete`

Removes a task for good. The tasks after it move up one number.

Format: `delete TASK_NUMBER`

Example: `delete 1`

```
Painted over. I've removed this task:
  [T][ ] read book
Your canvas now holds 2 tasks.
```

### Finding tasks by keyword: `find`

Shows every task whose description contains the keyword.

Format: `find KEYWORD`

- The search is not case-sensitive. e.g. `find BOOK` matches `return book`.
- Part of a word also matches. e.g. `find boo` matches `return book`.
- The numbers shown are for this result only. Use `list` to get the number for `mark`, `unmark` or `delete`.

Example: `find book`

```
Strokes matching your search:
1.[D][ ] return book (by: Dec 02 2019)
```

### Showing tasks on a date: `show`

Shows the deadlines and events that fall on a date, or at a particular time.

Format: `show DATE`

- With a date only, you get everything happening at any time that day.
- With a date and time, you get the deadlines due then and the events running at that moment.
  A deadline or event given without a time counts as lasting all day.
- To-dos have no date, so they never appear here.

Example: `show 2019-12-02`

```
Painted on Dec 02 2019:
1.[D][ ] return book (by: Dec 02 2019)
2.[E][ ] project meeting (from: Dec 02 2019 1400 to: Dec 02 2019 1600)
```

### Viewing statistics: `stats`

Gives a quick overview of your tasks:

- how many tasks you have of each type,
- the deadlines and events that fall between today and 7 days from now,
- your 5 oldest tasks that are not done yet.

Format: `stats`

```
Stepping back to admire your canvas:
Total tasks: 2 (ToDo: 0, Deadline: 1, Event: 1)
Due in the next 7 days: none
Oldest not done:
1.[D][ ] return book (by: Dec 02 2019)
2.[E][ ] project meeting (from: Dec 02 2019 1400 to: Dec 02 2019 1600)
```

### Recalling earlier commands

In the input box, press **↑** to bring back the commands you sent earlier, one at a time.
Press **↓** to move forward again, back to an empty line.

### Exiting: `bye`

Says goodbye and closes Myriad.

Format: `bye`

```
Putting the brushes away. Come paint again soon!
```

### Saving your data

Myriad saves your tasks automatically after every change, so there is nothing to do yourself.

Tasks are saved in `data/myriad.txt`, inside the folder you ran Myriad from.

> **Caution:** You can edit this file by hand, but be careful. If a line can't be read, Myriad skips that task
> and warns you when it starts. The skipped task is lost the next time Myriad saves, so back up the file first.

---

## FAQ

**Q: How do I move my tasks to another computer?**<br>
A: Copy the `data` folder next to `myriad.jar` on the other computer.

**Q: Myriad says "Smudge!". What went wrong?**<br>
A: "Smudge!" means Myriad could not carry out your command, and the rest of the message says why.
If it says it doesn't recognize the command, check the spelling of the command word and that `list`, `stats` and
`bye` have nothing after them. That message lists every command Myriad knows.

---

## Command summary

| Action             | Format and example                                                                         |
|--------------------|--------------------------------------------------------------------------------------------|
| **Add to-do**      | `todo DESCRIPTION`<br>e.g. `todo read book`                                                |
| **Add deadline**   | `deadline DESCRIPTION /by DATE`<br>e.g. `deadline return book /by 2019-12-02`              |
| **Add event**      | `event DESCRIPTION /from DATE /to DATE`<br>e.g. `event meeting /from 2019-12-02 1400 /to 2019-12-02 1600` |
| **List**           | `list`                                                                                     |
| **Mark / Unmark**  | `mark TASK_NUMBER`, `unmark TASK_NUMBER`<br>e.g. `mark 2`                                  |
| **Delete**         | `delete TASK_NUMBER`<br>e.g. `delete 3`                                                    |
| **Find**           | `find KEYWORD`<br>e.g. `find book`                                                         |
| **Show**           | `show DATE`<br>e.g. `show 2019-12-02 1800`                                                 |
| **Stats**          | `stats`                                                                                    |
| **Exit**           | `bye`                                                                                      |
