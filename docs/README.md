# Meowmeow User Guide

<img src="Ui.png" alt="Meowmeow UI" width="600">

Meowmeow is a desktop app for tracking your tasks - todos, deadlines, and
events - through a chat-style window. It's built for typing: if you can
type fast, Meowmeow can manage your tasks faster than any point-and-click
task app. It also has opinions about your to-do list, whether you asked
for them or not.

## Quick start

1. Ensure you have Java 25 installed on your computer.
2. Download the latest `meowmeow.jar`.
3. Run it with `java -jar meowmeow.jar`, or double-click it.
4. A chat window opens with a greeting. Type a command into the box at
   the bottom and press Enter (or click Send) to try it.
5. Some commands to get started:
   * `todo borrow book` - add a task
   * `list` - see everything you've added
   * `mark 1` - mark task 1 as done
   * `bye` - exit

See [Features](#features) below for everything Meowmeow can do.

## Notes about the command format

* Words in `<angle brackets>` are values you supply, e.g. in
  `todo <description>`, `<description>` might be `borrow book`.
* Commands and markers (`todo`, `/by`, `/from`, `/to`, `/p`) are all
  case-insensitive - `LIST`, `/BY`, `/P` work just as well.
* The `/p <level>` (or `/priority <level>`) flag is optional and can be
  placed anywhere in a `todo`, `deadline`, or `event` command. Only one
  `/p`/`/priority` flag is allowed per command.
* Task descriptions can't contain `|` - it's the character Meowmeow uses
  internally to save your tasks to disk.
* Blank input is silently ignored - nothing bad happens if you hit Enter
  on an empty box.
* Meowmeow has a bit of an attitude: most replies are picked at random
  from a handful of variations, so you won't always see the exact wording
  shown in the examples below - only the task details themselves are
  fixed.
* Each reply appears in a chat bubble with a small icon showing what
  happened - ✅ added, 🗑 removed, ✔ marked, ⭐ priority changed, 📋 list,
  🔍 find, ❌ error, ⚠ warning.

## Features

### Adding a todo: `todo`

Adds a task with no date attached.

Format: `todo <description>`

Example: `todo borrow book`

```
✅ Fine. Added. Happy now?
[T][ ] borrow book
Now you have 1 task in the list, meow!
```

You can also set a priority right away with a `/p` (or `/priority`) flag,
anywhere in the command - see [Setting a priority](#setting-a-priority-priority).

Example: `todo borrow book /p high`

### Adding a deadline: `deadline`

Adds a task due by a specific date/time.

Format: `deadline <description> /by <when>`

Example: `deadline return book /by 2/12/2019 1800`

Accepted date formats for `<when>` (also used by `/from`, `/to`, and
`list <date>`):

| Format | Example |
|---|---|
| `d/M/uuuu HHmm` | `2/12/2019 1800` |
| `uuuu-M-d HHmm` | `2019-12-02 1800` |
| `d/M/uuuu` | `2/12/2019` |
| `uuuu-M-d` | `2019-12-02` |

Dates are displayed as `MMM d uuuu`, and times (if given) as `h:mm a` -
e.g. `2/12/2019 1800` is shown as `Dec 2 2019, 6:00 pm`.

```
✅ There. Added. You're welcome.
[D][ ] return book (by: Dec 2 2019, 6:00 pm)
Now you have 1 task in the list, meow!
```

A `/p` (or `/priority`) flag also works here, anywhere in the command -
see [Setting a priority](#setting-a-priority-priority).

Example: `deadline return book /by 2/12/2019 1800 /p high`

### Adding an event: `event`

Adds a task spanning a start and an end.

Format: `event <description> /from <start> /to <end>`

Example: `event project meeting /from 2/12/2019 1400 /to 2/12/2019 1600`

The end must not be before the start. `<start>` and `<end>` accept the
same date formats as `deadline`'s `/by`.

```
✅ Ugh, fine, it's on the list.
[E][ ] project meeting (from: Dec 2 2019, 2:00 pm to: Dec 2 2019, 4:00 pm)
Now you have 1 task in the list, meow!
```

A `/p` (or `/priority`) flag also works here, anywhere in the command -
see [Setting a priority](#setting-a-priority-priority).

### Listing all tasks: `list`

Shows every task, numbered from 1.

Example: `list`

```
📋 Here's everything you're avoiding:
1.[T][ ] borrow book
2.[D][ ] return book (by: Dec 2 2019, 6:00 pm)
```

### Listing tasks on a date: `list <date>`

Shows only the deadlines due, and events spanning, the given date. Todos
never show up here, since they have no date. `<date>` accepts the same
formats as `deadline`'s `/by`.

Example: `list 2/12/2019`

```
📋 Your Dec 2 2019 agenda, such as it is:
1.[D][ ] return book (by: Dec 2 2019, 6:00 pm)
```

> **Note:** the numbers shown by `list <date>` (and by `find`, below)
> restart at 1 for that filtered view - they are **not** the task's
> number in your full list. Use plain `list` first if you need the real
> number for `mark`, `unmark`, `delete`, or `priority`.

### Finding tasks: `find`

Shows the tasks whose description contains a keyword. The search is
case-insensitive and matches anywhere in the description.

Example: `find book`

```
🔍 Here's what matched:
1.[T][ ] borrow book
2.[D][ ] return book (by: Dec 2 2019, 6:00 pm)
```

Numbering here also restarts at 1 - see the note under
[`list <date>`](#listing-tasks-on-a-date-list-date) above.

### Marking a task as done: `mark`

Format: `mark <task number>`

Example: `mark 1`

```
✔ Look at you, finishing things.
[T][X] borrow book
```

### Marking a task as not done: `unmark`

Format: `unmark <task number>`

Example: `unmark 1`

```
✔ Back on the list. Typical.
[T][ ] borrow book
```

### Deleting a task: `delete`

Removes a task from the list permanently.

Format: `delete <task number>`

Example: `delete 1`

```
🗑 Gone. Like it never existed.
[T][ ] borrow book
Now you have 0 tasks in the list, meow!
```

### Setting a priority: `priority`

Sets or clears a task's priority: `high`, `medium`, or `low`. A task's
priority appears as a trailing `(priority: ...)` after its other details,
and is not shown at all once cleared. Levels can also be given as a single
letter (`h`, `m`, `l`), and either is case-insensitive.

Format: `priority <task number> <level>`

Example: `priority 2 high`

```
⭐ Fine, that one matters more now.
[D][ ] return book (by: Dec 2 2019, 6:00 pm) (priority: HIGH)
```

Clear a priority back to none with the `none` (or `n`) level.

Example: `priority 2 none`

A priority can also be set right away when adding a task - see the `/p`
flag on [`todo`](#adding-a-todo-todo), [`deadline`](#adding-a-deadline-deadline)
and [`event`](#adding-an-event-event) above.

### Exiting: `bye`

Says goodbye and closes the window a moment later, so you have time to
read the farewell. Closing the window with its close button instead loses
nothing - every command that changes your tasks is saved immediately.

Example: `bye`

```
👋 Bye. Try not to need me again so soon.
```

## When Meowmeow says no

Every rejected command leaves your task list and saved data untouched -
nothing is lost, you just need to try again. Some errors you're likely to
run into:

| Situation | What Meowmeow says |
|---|---|
| Unrecognised command | `I don't know what that means.` + the full list of valid commands |
| `mark`/`unmark`/`delete` with no task number | `Tell me which task number to <command>.` |
| `priority` with no task number/level | `Use "priority <task number> <level>", e.g. "priority 2 high".` |
| Task number that doesn't exist | `Task <n> doesn't exist in your list.` |
| `deadline` missing `/by`, or `event` missing `/from`/`/to` | The correct format, with an example |
| A date Meowmeow can't parse (e.g. `30/2/2019`, `tomorrow`) | `I don't understand that date.` + the accepted formats |
| An event ending before it starts | `An event can't end before it starts.` |
| A description containing `\|` | `Sorry, task descriptions can't contain "\|".` |
| More than one `/p`/`/priority` flag in one command | `One priority per task, please.` |

## Saving the data

Meowmeow saves your tasks to `./data/meowmeow.txt` automatically after
every command that adds, removes, or updates a task - there's no separate
save command, and nothing is lost if you close the window instead of
typing `bye`. Each task is stored as one line, with fields separated by
`|` (which is why `|` can't appear in a description).

If a line in the save file becomes unreadable (e.g. from manual editing),
Meowmeow skips just that line with a warning and still loads the rest of
your tasks.

## FAQ

**How do I move my data to another computer?** Install Meowmeow on the
other computer, then copy over the `data/meowmeow.txt` file created by
your old installation, replacing the new one.

**Is there a `help` command?** Not a dedicated one - type anything
Meowmeow doesn't recognise and it replies with the full list of commands.

**What if my save file gets corrupted?** Meowmeow drops only the
unreadable lines and keeps everything else; see
[Saving the data](#saving-the-data).

## Command summary

| Action                | Format                                          | Example                                         |
|-----------------------|--------------------------------------------------|--------------------------------------------------|
| Add a todo            | `todo <description>`                            | `todo borrow book`                              |
| Add a deadline        | `deadline <description> /by <when>`             | `deadline return book /by 2/12/2019 1800`       |
| Add an event          | `event <description> /from <start> /to <end>`   | `event meeting /from 2/12/2019 /to 3/12/2019`   |
| List all tasks        | `list`                                          | `list`                                          |
| List tasks on a date  | `list <date>`                                   | `list 2/12/2019`                                |
| Find tasks            | `find <keyword>`                                | `find book`                                     |
| Mark a task done      | `mark <task number>`                            | `mark 1`                                        |
| Mark a task not done  | `unmark <task number>`                          | `unmark 1`                                      |
| Delete a task         | `delete <task number>`                          | `delete 1`                                      |
| Set a task's priority | `priority <task number> <level>`                | `priority 2 high`                               |
| Exit                  | `bye`                                           | `bye`                                           |
