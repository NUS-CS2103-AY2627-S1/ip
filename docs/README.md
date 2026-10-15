# Erina User Guide

![Erina's window, showing a task list, a task marked as done, and a highlighted error](Ui.png)

**Erina** is a desktop chatbot that keeps your to-dos, deadlines and events in impeccable order. You type short commands into a chat window, and Erina, a composed and slightly exacting assistant, replies in the conversation. Your tasks are saved automatically, so they are still there the next time you open her.

* [Quick start](#quick-start)
* [Features](#features)
  * [Viewing help: `help`](#viewing-help-help)
  * [Adding a to-do: `todo`](#adding-a-to-do-todo)
  * [Adding a deadline: `deadline`](#adding-a-deadline-deadline)
  * [Adding an event: `event`](#adding-an-event-event)
  * [Listing all tasks: `list`](#listing-all-tasks-list)
  * [Marking a task as done: `mark`](#marking-a-task-as-done-mark)
  * [Marking a task as not done: `unmark`](#marking-a-task-as-not-done-unmark)
  * [Finding tasks: `find`](#finding-tasks-find)
  * [Deleting a task: `delete`](#deleting-a-task-delete)
  * [Exiting: `bye`](#exiting-bye)
  * [When something goes wrong](#when-something-goes-wrong)
  * [Saving your tasks](#saving-your-tasks)
* [FAQ](#faq)
* [Command summary](#command-summary)
* [Acknowledgements](#acknowledgements)

## Quick start

1. Make sure **Java 25** is installed. To check, open a terminal and run `java -version`.
2. Download the latest `erina.jar` from the [releases page](https://github.com/tommyquak/ip/releases).
3. Put `erina.jar` in an empty folder. Erina keeps your tasks in a `data` folder next to it.
4. Open a terminal in that folder and run:
   ```
   java -jar erina.jar
   ```
   Erina's window opens and greets you.
5. Type a command into the box at the bottom and press **Enter** (or click **Send**). Try these:
   * `todo read book` adds a to-do.
   * `list` shows all your tasks.
   * `help` shows every command.
   * `bye` closes Erina.

## Features

**About the command format**

* Words in angle brackets are for you to fill in. In `todo <description>`, `<description>` could be `read book`.
* Dates are written as `yyyy-mm-dd`, for example `2026-09-18`.
* Task numbers are the numbers shown by `list`, counting from 1.
* Command words are not case-sensitive, and extra spaces are ignored: `LIST` and `  list  ` both work.
* Commands that take nothing after them (`list`, `help` and `bye`) reject extra text, so `list all` is pointed out rather than silently ignored.
* The `|` character cannot be used in a task, because Erina uses it to organise the save file.

### Viewing help: `help`

Shows every command, how to type it and what it does.

Format: `help`

### Adding a to-do: `todo`

Adds a task that has no date.

Format: `todo <description>`

Example: `todo read book`

```
Noted. I've put this in order for you:
  [T][ ] read book
Your list now holds 1 task.
```

`[T]` marks a to-do, and `[ ]` shows that it is not done yet.

### Adding a deadline: `deadline`

Adds a task that must be done by a certain date.

Format: `deadline <description> /by <yyyy-mm-dd>`

Example: `deadline return book /by 2026-09-18`

```
Noted. I've put this in order for you:
  [D][ ] return book (by: Sep 18 2026)
Your list now holds 2 tasks.
```

The date must be a real date: `2026-02-30` is refused.

### Adding an event: `event`

Adds a task that starts and ends at certain times.

Format: `event <description> /from <start> /to <end>`

The start and end can be any text, such as `Mon 2pm`. If both are dates in `yyyy-mm-dd` form, the end cannot be earlier than the start.

Example: `event project meeting /from Mon 2pm /to 4pm`

```
Noted. I've put this in order for you:
  [E][ ] project meeting (from: Mon 2pm to: 4pm)
Your list now holds 3 tasks.
```

Erina will not add a task that is already in your list. Tasks count as the same if they are the same kind, with the same description (ignoring capital letters) and the same dates.

### Listing all tasks: `list`

Shows every task, numbered, with whether it is done.

Format: `list`

```
Here is your list, in impeccable order:
1.[T][ ] read book
2.[D][ ] return book (by: Sep 18 2026)
3.[E][ ] project meeting (from: Mon 2pm to: 4pm)
```

### Marking a task as done: `mark`

Marks the task with the given number as done.

Format: `mark <task number>`

Example: `mark 1`

```
Splendid. I've marked this as done:
  [T][X] read book
```

`[X]` shows that the task is done. If the task is already done, Erina says so and changes nothing.

### Marking a task as not done: `unmark`

Marks the task with the given number as not done yet.

Format: `unmark <task number>`

Example: `unmark 1`

```
Very well, I've reopened this task:
  [T][ ] read book
```

### Finding tasks: `find`

Shows the tasks whose descriptions contain the given word or phrase. Capital letters are ignored, so `find book` also finds `Book club`.

Format: `find <keyword>`

Example: `find book`

```
These are the tasks that match:
1.[T][ ] read book
2.[D][ ] return book (by: Sep 18 2026)
```

The numbers here count the matches only. Use `list` to see each task's number for `mark`, `unmark` and `delete`.

### Deleting a task: `delete`

Removes the task with the given number from the list.

Format: `delete <task number>`

Example: `delete 2`

```
Very well. I've struck this from your list:
  [D][ ] return book (by: Sep 18 2026)
Your list now holds 2 tasks.
```

### Exiting: `bye`

Says goodbye and closes the window.

Format: `bye`

### When something goes wrong

If Erina cannot carry out a command, her reply is highlighted in red and explains what to fix. For example, `mark 9` when you have two tasks gives:

```
Pardon me. There is no task 9. Your list only goes up to 2.
```

### Saving your tasks

Erina saves your tasks after every change, to `data/erina.txt` in the folder you ran her from. There is no need to save manually.

If that file has been edited and Erina cannot understand it, she tells you when she starts, keeps a copy of it as `data/erina.txt.bak`, and starts you on a fresh list. You can repair the copy and rename it back to `erina.txt` to recover your tasks.

## FAQ

**Q: How do I move my tasks to another computer?**<br>
A: Copy the `data` folder to the folder holding `erina.jar` on the other computer.

**Q: Nothing happens when I double-click `erina.jar`.**<br>
A: Run it from a terminal with `java -jar erina.jar` instead, and check that `java -version` reports Java 25.

## Command summary

| Action | Format and example |
|---|---|
| Help | `help` |
| Add to-do | `todo <description>`<br>e.g. `todo read book` |
| Add deadline | `deadline <description> /by <yyyy-mm-dd>`<br>e.g. `deadline return book /by 2026-09-18` |
| Add event | `event <description> /from <start> /to <end>`<br>e.g. `event project meeting /from Mon 2pm /to 4pm` |
| List | `list` |
| Mark as done | `mark <task number>`<br>e.g. `mark 1` |
| Mark as not done | `unmark <task number>`<br>e.g. `unmark 1` |
| Find | `find <keyword>`<br>e.g. `find book` |
| Delete | `delete <task number>`<br>e.g. `delete 2` |
| Exit | `bye` |

## Acknowledgements

* The GUI is adapted from the [JavaFX tutorial](https://se-education.org/guides/tutorials/javaFx.html) by se-education.org.
* The project started from the [CS2103 iP template](https://github.com/NUS-CS2103-AY2627-S1/ip).
* AI assistance (Claude) was used for the week 6 increments, including the error handling, personality, GUI polish, tests and this guide. All output was reviewed and tested.
