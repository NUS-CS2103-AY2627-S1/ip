# Chione User Guide

Chione is a desktop app for keeping track of your tasks. She is named after the
Greek goddess of snow, and she talks like it: tell her what needs doing, and she
keeps it frozen in place until it's done.

You talk to Chione by typing commands, the way you would in a chat. If you can
type fast, you can manage your tasks faster than with a mouse.

![Chione's window, midway through a conversation](Ui.png)

* [Quick start](#quick-start)
* [Features](#features)
  * [Adding a todo: `todo`](#adding-a-todo-todo)
  * [Adding a deadline: `deadline`](#adding-a-deadline-deadline)
  * [Adding an event: `event`](#adding-an-event-event)
  * [Listing every task: `list`](#listing-every-task-list)
  * [Marking a task as done: `mark`](#marking-a-task-as-done-mark)
  * [Marking a task as not done: `unmark`](#marking-a-task-as-not-done-unmark)
  * [Deleting a task: `delete`](#deleting-a-task-delete)
  * [Finding tasks by keyword: `find`](#finding-tasks-by-keyword-find)
  * [Seeing what falls on a day: `on`](#seeing-what-falls-on-a-day-on)
  * [Exiting: `bye`](#exiting-bye)
  * [Saving your tasks](#saving-your-tasks)
  * [Editing the save file](#editing-the-save-file)
  * [When Chione can't do something](#when-chione-cant-do-something)
* [FAQ](#faq)
* [Command summary](#command-summary)

## Quick start

1. Make sure you have **Java 25** installed. In a terminal, `java -version`
   should say `25`.
2. Download the latest `chione.jar` from the
   [releases page](https://github.com/Lew1sWong/ip/releases).
3. Put the file in a folder of its own. Chione keeps your tasks in a `data`
   folder next to it.
4. Open a terminal in that folder and run:

   ```
   java -jar chione.jar
   ```

   A window like the one above appears, with Chione's greeting.
5. Type a command in the box at the bottom and press **Enter** (or click
   **Send**). Try `todo buy ice skates`, then `list`.
6. Read on for what each command does.

## Features

**How to read the command formats below**

* Words in `UPPER_CASE` are the parts you fill in.
  In `todo DESCRIPTION`, `DESCRIPTION` could be `buy ice skates`.
* Dates are written as `YYYY-MM-DD`, or `YYYY-MM-DD HHMM` to include a time
  of day in 24-hour form. `2026-09-18` is a day; `2026-09-18 2359` is one
  minute before midnight on it. Chione shows dates back in a friendlier form,
  e.g. `Sep 18 2026, 11:59pm`.
* Extra spaces don't matter: `todo    buy   skates` is the same as
  `todo buy skates`.
* A description can be anything except the `|` character, which Chione uses
  in her save file.

**How to read the task list**

Every task is shown on one line, like `2.[D][X] submit iP (by: Sep 18 2026, 11:59pm)`:

| Part               | Meaning                                                    |
|--------------------|------------------------------------------------------------|
| `2.`               | Its number in the list. Use this with `mark`, `unmark` and `delete`. |
| `[T]`, `[D]`, `[E]` | A todo, a deadline or an event.                           |
| `[X]` or `[ ]`     | Done, or not done yet.                                     |
| `(by: ...)`, `(from: ... to: ...)` | When it is due, or when it runs.           |

### Adding a todo: `todo`

Adds a task that has no date attached.

Format: `todo DESCRIPTION`

Example: `todo buy ice skates`

Chione replies:

```
Settled, like fresh snowfall. I've added:
  [T][ ] buy ice skates
Your list now holds 1 task.
```

### Adding a deadline: `deadline`

Adds a task that must be done by a certain date, with or without a time.

Format: `deadline DESCRIPTION /by DATE`

Examples:

* `deadline submit iP /by 2026-09-18 2359`
* `deadline return library book /by 2026-10-15`

Chione replies:

```
Settled, like fresh snowfall. I've added:
  [D][ ] submit iP (by: Sep 18 2026, 11:59pm)
Your list now holds 2 tasks.
```

### Adding an event: `event`

Adds a task that runs from one date to another. It can span several days.
The end has to be after the start.

Format: `event DESCRIPTION /from START /to END`

Examples:

* `event ski trip /from 2026-12-20 /to 2026-12-27`
* `event project meeting /from 2026-09-16 1400 /to 2026-09-16 1600`

Chione replies:

```
Settled, like fresh snowfall. I've added:
  [E][ ] ski trip (from: Dec 20 2026 to: Dec 27 2026)
Your list now holds 3 tasks.
```

Chione refuses a task that is already in your list: the same kind of task,
with the same description (ignoring case) and, for a deadline or an event, the
same dates. She tells you so, and leaves the list as it was.

### Listing every task: `list`

Shows all your tasks, numbered.

Format: `list`

Chione replies:

```
Here is everything resting in your list:
1.[T][ ] buy ice skates
2.[D][ ] submit iP (by: Sep 18 2026, 11:59pm)
3.[E][ ] ski trip (from: Dec 20 2026 to: Dec 27 2026)
```

If the list is empty, she says so instead.

### Marking a task as done: `mark`

Ticks off a task, using its number from `list`.

Format: `mark NUMBER`

Example: `mark 2`

Chione replies:

```
Done, and dusted with snow. This one is finished:
  [D][X] submit iP (by: Sep 18 2026, 11:59pm)
```

### Marking a task as not done: `unmark`

Un-ticks a task you marked by mistake, or that turned out not to be finished.

Format: `unmark NUMBER`

Example: `unmark 2`

Chione replies:

```
Thawed out again. This one is not done yet:
  [D][ ] submit iP (by: Sep 18 2026, 11:59pm)
```

### Deleting a task: `delete`

Removes a task from the list for good. The tasks after it move up one number.

Format: `delete NUMBER`

Example: `delete 1`

Chione replies:

```
Melted away. I've removed:
  [T][ ] buy ice skates
Your list now holds 2 tasks.
```

### Finding tasks by keyword: `find`

Shows the tasks whose description contains some text. The search ignores
case, and the text can be part of a word: `find ski` finds `ski trip`, and so
does `find TRIP`. Only descriptions are searched; to search by date, use `on`.

Format: `find TEXT`

Example: `find ski`

Chione replies:

```
Here is what I found for "ski":
1.[E][ ] ski trip (from: Dec 20 2026 to: Dec 27 2026)
```

The numbers here are positions in the search result, not in the full list.
Run `list` before using `mark`, `unmark` or `delete`.

### Seeing what falls on a day: `on`

Shows every deadline due on a day and every event running over it, including
events that started earlier and end later. Todos have no date, so they never
appear here.

Format: `on DATE` (a day only, without a time)

Example: `on 2026-12-22`

Chione replies:

```
Here is what falls on Dec 22 2026:
1.[E][ ] ski trip (from: Dec 20 2026 to: Dec 27 2026)
```

### Exiting: `bye`

Says goodbye and closes the window after a moment.

Format: `bye`

### Saving your tasks

Chione saves your list after every change, so there is nothing you need to do.
The tasks are still there the next time you start her.

They live in `data/chione.txt`, in the folder you ran Chione from. If you
start her from a different folder, she starts a different list there.

### Editing the save file

`data/chione.txt` is plain text, one task per line, so you can open it in any
text editor:

```
T | 1 | buy ice skates
D | 0 | submit iP | 2026-09-18 2359
E | 0 | ski trip | 2026-12-20 0000 | 2026-12-27 0000
```

The columns are the kind of task, `1` for done or `0` for not, the description,
and then the dates. Keep that shape if you edit it. If a line can't be read,
Chione tells you which one when she starts, and begins with an empty list
rather than guessing; saving anything at that point replaces the file, so fix
or copy it first.

### When Chione can't do something

If a command can't be carried out, Chione says why in a red-edged bubble, and
suggests something that would work:

```
Hm, that one slipped on the ice:
I don't know what "blah" means. I understand: todo, deadline, event, list, on,
find, mark, unmark, delete, bye.
```

Nothing is changed when this happens, so you can simply correct the command and
try again. The things she checks for include a missing description or date,
a date that doesn't exist (`2026-02-30`), an event that ends before it starts,
`/by` or `/from` given twice, a task number that isn't in the list, and a task
that repeats one you already have.

## FAQ

**Q: How do I move my tasks to another computer?**
A: Install Chione there, run it once, then replace its `data/chione.txt` with
the one from your old computer.

**Q: Can I type a date like `15/10/2026` or `tomorrow`?**
A: Not yet. Dates have to be written as `2026-10-15`, optionally followed by a
time such as `1800`.

**Q: `mark` marked the wrong task.**
A: Task numbers come from `list`, and they shift when a task is deleted. Run
`list` again to see the current numbers, then `unmark` the wrong one.

**Q: Chione says my save file has a line she doesn't understand.**
A: A line in `data/chione.txt` isn't in the shape shown above. Open the file,
fix or delete that line, and start Chione again.

## Command summary

| Action   | Format                                   | Example                                              |
|----------|------------------------------------------|------------------------------------------------------|
| Todo     | `todo DESCRIPTION`                       | `todo buy ice skates`                                |
| Deadline | `deadline DESCRIPTION /by DATE`          | `deadline submit iP /by 2026-09-18 2359`             |
| Event    | `event DESCRIPTION /from START /to END`  | `event ski trip /from 2026-12-20 /to 2026-12-27`     |
| List     | `list`                                   | `list`                                               |
| Mark     | `mark NUMBER`                            | `mark 2`                                             |
| Unmark   | `unmark NUMBER`                          | `unmark 2`                                           |
| Delete   | `delete NUMBER`                          | `delete 1`                                           |
| Find     | `find TEXT`                              | `find ski`                                           |
| On       | `on DATE`                                | `on 2026-12-22`                                      |
| Exit     | `bye`                                    | `bye`                                                |
