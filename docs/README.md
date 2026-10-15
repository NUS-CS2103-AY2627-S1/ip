# Bob User Guide

**Bob** is a minion that keeps track of your tasks. Chat with King Bob, in a small
window: type what you need to do, and Bob remembers it, reminds you what is due, and saves
your list for next time.

Bob tracks three kinds of task:

* a `todo` has no date, such as _`borrow book`_,
* a `deadline` is due by a date, such as _`return book by 2 December`_,
* an `event` runs from one date to another, such as _`project meeting from 2pm to 4pm`_.

## Quick start

1. Make sure your computer has Java 25 installed.
2. Download the latest `bob.jar` from the
   [Releases page](https://github.com/adwaithanoop/ip/releases).
3. Put `bob.jar` in the folder where you want Bob to keep your tasks.
4. Open a terminal in that folder and run `java -jar bob.jar`. A chat window opens.
5. Type a command in the box at the bottom and press Enter. Some to try:
   * `todo borrow book` adds a todo.
   * `list` shows all your tasks.
   * `mark 1` marks the first task as done.
   * `bye` says goodbye and closes Bob.

## Features

**About the command format:**

* Words in angle brackets, such as `<description>`, are placeholders. Replace them with your
  own value and do not type the angle brackets.
* Items in square brackets are optional.
* Commands are lowercase: `list` works, but `LIST` does not.
* `<task number>` is the number shown next to a task by `list`.
* Write a date as `yyyy-mm-dd`, for example `2026-12-02`. Add a 24-hour time after it, such as
  `2026-12-02 1800` for 6pm, when the hour matters. The year is four digits, from `0001`
  to `9999`.
* When you get something wrong, Bob says what went wrong and shows an example of the command
  written correctly.

### Adding a todo: `todo`

Adds a task that has no date.

Format: `todo <description>`

Example: `todo borrow book`

```
Okay! Bob add dis:
  [T][ ] borrow book
Now yu have 1 task in da list.
```

`[T]` shows the task is a todo. The empty `[ ]` next to it shows it is not done yet.

### Adding a deadline: `deadline`

Adds a task that is due by a date.

Format: `deadline <description> /by yyyy-mm-dd`

Examples:

* `deadline return book /by 2026-12-02 1800`
* `deadline submit report /by 2026-11-30`

```
Okay! Bob add dis:
  [D][ ] return book (by: Dec 02 2026 18:00)
Now yu have 2 tasks in da list.
```

### Adding an event: `event`

Adds a task that starts and ends at a given time.

Format: `event <description> /from yyyy-mm-dd /to yyyy-mm-dd`

* `/from` comes before `/to`.
* An event can't end before it starts.
* A day written with no time means the whole of that day: it starts at the
  beginning of the day in `/from`, and ends at the end of it in `/to`. So
  `/from 2026-12-02 1800 /to 2026-12-02` runs from six in the evening until the
  day is over.

Examples:

* `event project meeting /from 2026-12-02 1400 /to 2026-12-02 1600`
* `event orientation week /from 2026-08-03 /to 2026-08-07`

```
Okay! Bob add dis:
  [E][ ] project meeting (from: Dec 02 2026 14:00 to: Dec 02 2026 16:00)
Now yu have 3 tasks in da list.
```

Bob won't add a task you already have. Two tasks count as the same when they are the same
kind, with the same dates, and their descriptions differ only in capital letters or spaces.

### Listing all tasks: `list`

Shows every task, numbered.

Format: `list`

```
Luk at tu! Here da tasks:
1.[T][ ] borrow book
2.[D][ ] return book (by: Dec 02 2026 18:00)
3.[E][ ] project meeting (from: Dec 02 2026 14:00 to: Dec 02 2026 16:00)
4.[E][ ] orientation week (from: Aug 03 2026 to: Aug 07 2026)
```

### Marking a task as done: `mark`, `unmark`

`mark` marks a task as done, and `unmark` marks it as not done again.

Format: `mark <task number>` or `unmark <task number>`

Example: `mark 1`

```
Kanpai! Dis one finish:
  [T][X] borrow book
```

The `X` shows the task is done.

### Editing a task: `edit`

Changes the description or the dates of a task you already have, without deleting
it and adding it again. The task keeps its number in the list, and stays done if it
was done.

Format: `edit <task number> [/desc <description>] [/by yyyy-mm-dd] [/from yyyy-mm-dd] [/to yyyy-mm-dd]`

* `<task number>` is the number shown next to the task by `list`.
* Give at least one of the markers, in any order. Details you leave out stay as
  they are.
* Each kind of task takes only the markers for the details it has:
  * a todo: `/desc`
  * a deadline: `/desc` and `/by`
  * an event: `/desc`, `/from` and `/to`
* Write a date as `yyyy-mm-dd`, followed by a 24-hour time such as `1800` if the
  hour matters. A new date replaces the old one whole, so a date given without a
  time removes the time the task had.
* An event can't be changed so that it ends before it starts.
* What follows a marker runs up to the next marker, so a new description can't
  itself contain `/desc`, `/by`, `/from` or `/to`.

Examples:

* `edit 1 /desc read library book`
* `edit 2 /by 2026-12-05`
* `edit 3 /from 2026-08-07 1000 /to 2026-08-07 1200`

If task 2 is a deadline to return a book by 6pm on 2 December 2026,
`edit 2 /by 2026-12-05` moves it to 5 December and shows the task before and after
the change:

```
Tadaa! Dis was:
  [D][ ] return book (by: Dec 02 2026 18:00)
Now is:
  [D][ ] return book (by: Dec 05 2026)
```

### Deleting a task: `delete`

Removes a task for good. The tasks after it move up one number.

Format: `delete <task number>`

Example: `delete 1`

```
Bee-do! Bob throw away dis:
  [T][X] borrow book
Now yu have 3 tasks in da list.
```

### Seeing the tasks on a day: `on`

Shows the deadlines due on a day, and the events running on it.

Format: `on yyyy-mm-dd`

* Give the day only, with no time.
* An event that runs over several days shows up on each of them.

Example: `on 2026-12-02`

```
Luk! On Dec 02 2026 yu have:
2.[D][ ] return book (by: Dec 02 2026 18:00)
3.[E][ ] project meeting (from: Dec 02 2026 14:00 to: Dec 02 2026 16:00)
```

### Seeing the tasks before or after a day: `before`, `after`

`before` shows the tasks dated earlier than a day, and `after` the tasks dated later.

Format: `before yyyy-mm-dd` or `after yyyy-mm-dd`

* Give the day only, with no time. Tasks on that day itself are not shown; use `on` for those.
* An event counts by the day it starts.
* Todos have no date, so they are never shown.

Example: `after 2026-09-01`

```
Bob look after Sep 01 2026. Yu have:
2.[D][ ] return book (by: Dec 02 2026 18:00)
3.[E][ ] project meeting (from: Dec 02 2026 14:00 to: Dec 02 2026 16:00)
```

### Seeing the most urgent tasks: `next`

Shows the tasks you haven't finished yet that have the earliest dates, earliest first.

Format: `next <count>`

* `<count>` is how many tasks to show, 1 or more.
* Todos and tasks that are done are left out.
* Tasks whose date has already passed are included, and come first.

Example: `next 2`

```
Bee-do bee-do! 2 most urgent, soonest first:
4.[E][ ] orientation week (from: Aug 03 2026 to: Aug 07 2026)
3.[E][ ] project meeting (from: Dec 02 2026 14:00 to: Dec 02 2026 16:00)
```

### Finding tasks: `find`

Shows the tasks whose description contains some text.

Format: `find <keyword>`

* Capital letters don't matter: `find BOOK` finds `borrow book`.
* Part of a word is enough: `find book` also finds `bookshop`.
* The keyword may have spaces in it, such as `find project meeting`.
* Extra spaces between words don't matter: `find project meeting` finds
  `project  meeting` as well.

Example: `find book`

```
Bob found dem:
1.[T][X] borrow book
2.[D][ ] return book (by: Dec 02 2026 18:00)
```

### Exiting: `bye`

Says goodbye and closes Bob.

Format: `bye`

### Saving your tasks

Bob saves your tasks by itself whenever they change, so there is nothing to save by hand.
They are kept in `data/bob.txt`, in the folder you ran Bob from, and are loaded again the next
time you start Bob there.

If that file gets damaged, for example by a mistake while editing it by hand, Bob tells you what
it couldn't read when it starts. It also copies the file as it was to `data/bob.txt.bak`, so
nothing in it is lost.

## Command summary

| Action | Command format |
|--------|----------------|
| Add a todo | `todo <description>` |
| Add a deadline | `deadline <description> /by yyyy-mm-dd` |
| Add an event | `event <description> /from yyyy-mm-dd /to yyyy-mm-dd` |
| Show every task | `list` |
| Mark a task as done | `mark <task number>` |
| Mark a task as not done | `unmark <task number>` |
| Change a task's description | `edit <task number> /desc <description>` |
| Change a deadline date | `edit <task number> /by yyyy-mm-dd` |
| Change an event's dates | `edit <task number> /from yyyy-mm-dd /to yyyy-mm-dd` |
| Delete a task | `delete <task number>` |
| Show tasks on a date | `on yyyy-mm-dd` |
| Show tasks before a date | `before yyyy-mm-dd` |
| Show tasks after a date | `after yyyy-mm-dd` |
| Show the most urgent tasks | `next <count>` |
| Find tasks by description | `find <keyword>` |
| Exit Bob | `bye` |

Words in angle brackets, such as `<description>`, are placeholders. Replace them with your own
value and do not type the angle brackets.

Add a 24-hour time after any date, such as `2026-12-02 1800`, when the hour matters.

Bob ignores leading and trailing whitespace, as well as repeated spaces or tabs around `/desc`,
`/by`, `/from` and `/to`. Whitespace within a task description is kept as part of the
description. The `list` and `bye` commands do not accept arguments.
