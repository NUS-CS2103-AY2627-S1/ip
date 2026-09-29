# Emma User Guide

Emma is a friendly chatbot that keeps track of your tasks — things to do, things
due by a date, and things happening over a few days. Tell her what's on your
plate, and she remembers it, including after you close her.

<img src="Ui.png" alt="Emma keeping track of a few tasks" width="400">

## Getting started

1. Install **Java 25** if you don't already have it.
2. Download `emma.jar` and put it in a folder of its own.
3. Double-click it, or run `java -jar emma.jar` from a terminal.

Type a command in the box at the bottom and press <kbd>Enter</kbd>, or click
**Send**.

> **Tip:** Emma saves after every change, so you can close her whenever you like
> without losing anything.

## Adding tasks: `todo`, `deadline`, `event`

Emma tracks three kinds of task. Each one goes to the end of your list and starts
off not done, and Emma repeats back what she understood so you can check the dates
came out the way you meant.

**`todo` — something to do, with no particular date**

```
todo buy milk
```

**`deadline` — something due by a date**

```
deadline submit report /by 2026-09-25
```

**`event` — something running over a stretch of days**

```
event team offsite /from 2026-10-02 /to 2026-10-03
```

> **Note:** Type dates as **`yyyy-mm-dd`**. Emma reads them back to you in words.

## Seeing what you have: `list`

```
list
```

Shows every task you have, numbered from 1. Those are the numbers `mark`,
`unmark` and `delete` expect, so this is usually the command you reach for first.

Each task starts with two brackets: the kind — `T` for a todo, `D` for a
deadline, `E` for an event — and the status, which shows `x` once the task is
done. Deadlines and events show their dates after the description.

## Ticking things off: `mark`, `unmark`, `delete`

Use the number shown by `list`:

| You type | What happens |
|---|---|
| `mark 2` | Task 2 is marked done |
| `unmark 2` | Task 2 goes back to not done |
| `delete 2` | Task 2 is removed for good |

## Finding a task: `find`, `filter`

When the list gets long:

| You type | What you get |
|---|---|
| `find report` | Every task with "report" in its description |
| `filter /type deadline` | Only deadlines — or use `todo` or `event` |
| `filter /type deadline /due-by 2026-09-30` | Deadlines due on or before that date |
| `filter /type event /at 2026-10-02` | Events running on that day |

`find` matches part of a word, and it cares about capital letters — `find Report`
won't match "report".

## Finishing up: `bye`

`bye` closes the window. Everything is already saved.

## All the commands

| Command | What it does | Example |
|---|---|---|
| `todo DESCRIPTION` | Adds a task with no date | `todo buy milk` |
| `deadline DESCRIPTION /by DATE` | Adds a task due by a date | `deadline submit report /by 2026-09-25` |
| `event DESCRIPTION /from START /to END` | Adds a task spanning dates | `event team offsite /from 2026-10-02 /to 2026-10-03` |
| `list` | Shows every task, numbered | `list` |
| `mark NUMBER` | Marks a task done | `mark 2` |
| `unmark NUMBER` | Marks a task not done | `unmark 2` |
| `delete NUMBER` | Removes a task | `delete 2` |
| `find TEXT` | Shows tasks containing that text | `find report` |
| `filter /type TYPE` | Shows one kind of task | `filter /type deadline` |
| `filter /type deadline /due-by DATE` | Deadlines due by a date | `filter /type deadline /due-by 2026-09-30` |
| `filter /type event /at DATE` | Events running on a date | `filter /type event /at 2026-10-02` |
| `bye` | Closes Emma | `bye` |

## Good to know

- **Emma won't add the same task twice.** If a task matches one you already have
  — same kind, same description, same dates — she'll say so and leave your list
  alone.
- **Nothing is lost when something goes wrong.** If Emma can't carry out a
  command, she says why in a red bubble and your tasks stay exactly as they were.
- **`list` and `bye` are typed on their own.** Anything after them is a typo as
  far as Emma is concerned, so she'll ask rather than guess.
- **Your tasks live in `data/emma.json`**, in the folder you ran Emma from. It's
  ordinary text, so you can read it — or back it up — yourself.
