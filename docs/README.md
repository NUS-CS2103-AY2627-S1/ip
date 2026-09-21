# Steph User Guide

Steph is a small chat-style task manager. You type short commands such as
`todo read book` or `deadline return book /by 2026-10-15 1800`, and Steph
replies, keeps your list, and saves it for next time.

## Product overview

Steph tracks three kinds of tasks:

* **Todos**: things to do, with no date.
* **Deadlines**: things to do by a date (and optionally a time).
* **Events**: things that span a start and an end.

What you get:

* **A chat window or a console.** Both work the same way and share one save file.
* **Automatic saving.** Every change is written to disk straight away.
* **Double-booking protection.** Steph warns you when a new event overlaps one you already have.
* **Quick search.** Find any task by a word in its name.

## Table of contents

1. [Introduction and product overview](#steph-user-guide)
2. [Table of contents](#table-of-contents)
3. [About this guide](#about-this-guide)
4. [Quick start](#quick-start)
5. [Features](#features)
   * [Adding a todo: `todo`](#adding-a-todo-todo)
   * [Adding a deadline: `deadline`](#adding-a-deadline-deadline)
   * [Adding an event: `event`](#adding-an-event-event)
   * [Detecting schedule clashes](#detecting-schedule-clashes)
   * [Overriding a clash with `/force`](#overriding-a-clash-with-force)
   * [Listing all tasks: `list`](#listing-all-tasks-list)
   * [Marking and unmarking tasks: `mark`, `unmark`](#marking-and-unmarking-tasks-mark-unmark)
   * [Deleting a task: `delete`](#deleting-a-task-delete)
   * [Finding tasks: `find`](#finding-tasks-find)
   * [Exiting: `bye`](#exiting-bye)
   * [Saving and loading](#saving-and-loading)
   * [Reply colours in the chat window](#reply-colours-in-the-chat-window)
6. [FAQs and troubleshooting](#faqs-and-troubleshooting)
7. [Command summary](#command-summary)
8. [Other helpful information](#other-helpful-information)
   * [Glossary](#glossary)
   * [Date and time formats](#date-and-time-formats)
   * [Save file format](#save-file-format)
   * [Product specifications](#product-specifications)

## About this guide

* **Command format.** Words in `<angle brackets>` are values you fill in.
  Words in `[square brackets]` are optional. Everything else is typed exactly
  as shown. For example, `deadline <task-name> /by <date>` becomes
  `deadline return book /by 2026-10-15`.
* **Examples.** Each feature shows a command and Steph's reply. The console
  version also prints a line of underscores above and below each reply; they
  are left out here.
* **Notes.** A line starting with **Note** points out something easy to miss.
* **Where to look.** New to Steph? Start with [Quick start](#quick-start).
  Already know the basics? Jump to the [Command summary](#command-summary).
  Stuck on an unfamiliar word? See the [Glossary](#glossary).

## Quick start

1. **Install Java.** Ensure that Java 25 or later is installed on your
   computer.

   **Mac users:** Ensure you have the precise JDK version prescribed in the
   [Java installation guide for Mac](https://se-education.org/guides/tutorials/javaInstallationMac.html).
2. **Download `steph.jar`** from the [v1.0 release](https://github.com/preyjwal/ip/releases/tag/v1.0) (under
   "Assets"). Put it in a folder of your choice. Steph will keep your
   tasks in a `data` folder next to where you start it, so an empty folder of
   its own is a good idea.
3. **Start Steph.** Open a terminal in that folder and run:

   ```
   java -jar steph.jar
   ```

   The chat window opens. (Double-clicking the jar also works on most
   systems, but then the `data` folder is created wherever your system
   decides to start it. Using the terminal keeps this predictable.)

   **Note:** You may see a few `WARNING: ... restricted method ...` lines in
   the terminal. They come from JavaFX, are harmless, and can be ignored.
4. **Try a first session.** Type these one at a time:

   | You type | What happens |
   |---|---|
   | `todo read book` | Adds a todo |
   | `deadline return book /by 2026-10-15 1800` | Adds a deadline |
   | `list` | Shows both tasks, numbered |
   | `mark 1` | Marks task 1 as done |
   | `bye` | Says goodbye and exits |

   Steph's reply to `list` at that point:

   ```
   Here are the tasks in your list:
   1.[T][X] read book
   2.[D][ ] return book (by: Oct 15 2026, 6:00pm)
   ```

5. **Come back later.** Start Steph again and type `list`; your tasks are still there.

## Features

Command words are lower-case and must be typed exactly (`todo`, not `Todo`).
Extra spaces at the start or end of a line are ignored.

### Adding a todo: `todo`

Adds a task with only a name.

Format: `todo <task-name>`

Example: `todo read book`

```
Got it. I've added this task:
  [T][ ] read book
Now you have 1 tasks in the list.
```

### Adding a deadline: `deadline`

Adds a task that is due by a date, optionally with a time.

Format: `deadline <task-name> /by <date>`

`<date>` is `yyyy-mm-dd` or `yyyy-mm-dd HHmm` (24-hour time). See
[Date and time formats](#date-and-time-formats).

Examples:

* `deadline return book /by 2026-10-15 1800`
* `deadline submit report /by 2026-12-01`

```
Got it. I've added this task:
  [D][ ] return book (by: Oct 15 2026, 6:00pm)
Now you have 1 tasks in the list.
```

```
Got it. I've added this task:
  [D][ ] submit report (by: Dec 01 2026)
Now you have 2 tasks in the list.
```

**Note:** A date with no time is shown without a time.

### Adding an event: `event`

Adds a task that runs from a start to an end.

Format: `event <task-name> /from <date> /to <date> [/force]`

`/from` must come before `/to`, and the start must be before the end.
`/force` is explained in [Overriding a clash](#overriding-a-clash-with-force).

Example: `event camp /from 2026-10-15 1400 /to 2026-10-15 1800`

```
Got it. I've added this task:
  [E][ ] camp (from: Oct 15 2026, 2:00pm to: Oct 15 2026, 6:00pm)
Now you have 1 tasks in the list.
```

**Note:** A date with no time counts as the whole day. So
`event holiday /from 2026-10-15 /to 2026-10-15` is a valid one-day event.

### Detecting schedule clashes

When you add an `event`, Steph checks it against your other events. If it
overlaps one, the new event is **not added**, and you are shown which
event(s) it clashes with, so you don't double-book yourself.

Example (with `camp` from above already in the list):
`event trip /from 2026-10-15 1600 /to 2026-10-15 1700`

```
This clashes with an existing event:
  [E][ ] camp (from: Oct 15 2026, 2:00pm to: Oct 15 2026, 6:00pm)
Add '/force' to the command if you want to schedule it anyway.
```

The rules:

* Only events are compared with each other. Todos and deadlines never clash.
* An event you have marked as done is never compared.
* Events clash only if their times really overlap. One event ending exactly
  when another starts is **not** a clash (2pm to 4pm and 4pm to 6pm are fine).
* An event with no time (a date-only `/from` or `/to`) covers that whole day.

### Overriding a clash with `/force`

Add `/force` at the very end of an `event` command to schedule it anyway. It
skips the clash check completely, so it works the first time; you don't have to
be rejected first.

Example: `event trip /from 2026-10-15 1600 /to 2026-10-15 1700 /force`

```
Got it. I've added this task:
  [E][ ] trip (from: Oct 15 2026, 4:00pm to: Oct 15 2026, 5:00pm)
Now you have 2 tasks in the list.
```

**Note:** Steph does not remember a forced clash. The check only runs when a
new event is added, so the two events are not flagged again later.

### Listing all tasks: `list`

Shows every task, numbered from 1. Use these numbers with `mark`, `unmark`
and `delete`.

Format: `list`

```
Here are the tasks in your list:
1.[T][ ] read book
2.[D][ ] return book (by: Oct 15 2026, 6:00pm)
3.[D][ ] submit report (by: Dec 01 2026)
4.[E][ ] camp (from: Oct 15 2026, 2:00pm to: Oct 15 2026, 6:00pm)
```

How to read a line: `[T]`, `[D]` or `[E]` is the type (todo, deadline,
event), and `[ ]` or `[X]` shows whether it is done.

### Marking and unmarking tasks: `mark`, `unmark`

`mark` records a task as done; `unmark` puts it back to not done.

Format: `mark <task-number>` and `unmark <task-number>`

Example: `mark 2`

```
Awesome! I've marked this task as done:
  [D][X] return book (by: Oct 15 2026, 6:00pm)
```

Example: `unmark 2`

```
OK, I've marked this task as not done yet:
  [D][ ] return book (by: Oct 15 2026, 6:00pm)
```

The task number must be one that currently appears in `list`.

### Deleting a task: `delete`

Removes a task from the list.

Format: `delete <task-number>`

Example: `delete 3`

```
Okay! I've removed this task:
  [D][ ] submit report (by: Dec 01 2026)
Now you have 3 tasks in the list.
```

**Note:** Deleting cannot be undone, and the numbers of the tasks after it
shift down by one. Type `list` again before your next `delete`.

### Finding tasks: `find`

Shows the tasks whose name contains a word or phrase. Upper and lower case
are treated the same.

Format: `find <keyword>`

Example: `find BOOK`

```
Here are the matching tasks in your list:
1.[T][ ] read book
2.[D][ ] return book (by: Oct 15 2026, 6:00pm)
```

The numbers in this reply count only the matches. They are **not** the task
numbers to use with `mark` or `delete`; use `list` for those.

If nothing matches (`find zzz`):

```
I couldn't find any tasks matching "zzz".
```

### Exiting: `bye`

Ends the session.

Format: `bye`

```
Goodbye. Hope to see you again soon!
```

In the chat window, the window closes about a second after the goodbye. In the
console, the program simply ends. Nothing needs saving first, because every
change is already saved.

### Saving and loading

Steph saves automatically after every command that changes your list
(`todo`, `deadline`, `event`, `mark`, `unmark`, `delete`). `list` and `find`
only read, so they don't touch the file. There is no save command.

* **Where:** `data/steph.txt`, inside the folder you ran `java -jar steph.jar`
  from. Steph creates the folder and file the first time it needs them.
* **Loading:** the file is read when Steph starts. If it doesn't exist yet, you
  start with an empty list.
* **Editing by hand:** possible, but be careful. See [Save file format](#save-file-format).
  A line Steph can't understand is skipped when loading, and is not written back on the next save.

### Reply colours in the chat window

In the chat window, Steph's reply bubble is tinted to show what just happened:

| Colour | Meaning |
|---|---|
| Yellow | A task was added (`todo`, `deadline`, `event`) |
| Slightly deeper green | A task was marked or unmarked |
| Red-pink | A task was deleted |
| Blue | Search results (`find`) |
| Bold text with a red border | An error: the command was rejected (see [FAQs](#faqs-and-troubleshooting)) |
| Light green (the default) | Anything else (`list`, greetings) |

## FAQs and troubleshooting

**Steph says `I don't understand that command: "..."`.**
The first word isn't a command Steph knows. Commands are `todo`, `deadline`,
`event`, `list`, `mark`, `unmark`, `delete`, `find` and `bye`, all lower-case.

**Steph says `Please type "todo <task-name>"` (or a similar usage line).**
Part of the command is missing. Check that you gave a name, and for
`deadline` and `event` that the `/by`, `/from` and `/to` markers are present,
with something on both sides. For `event`, `/from` must come before `/to`.

**Steph says `I couldn't read "..." as a date`.**
Use `yyyy-mm-dd` or `yyyy-mm-dd HHmm`, for example `2026-10-15` or
`2026-10-15 1800`. The month and day need two digits (`2026-10-05`, not
`2026-10-5`), the date must exist (no `2026-13-01`), and words like `tomorrow`
aren't understood.

**Steph says `An event's start (...) must be before its end (...)`.**
The `/from` time comes at or after the `/to` time. Swap them or fix the
dates.

**Steph says `This clashes with an existing event`.**
The new event overlaps one you haven't marked as done. Change its time, mark
the old event as done, or add `/force` to schedule both anyway.

**Steph says `Please type "mark <task-number>" with a valid task number`
(also for `unmark` and `delete`).**
The number must be a whole number that appears in `list`. `0`, negative
numbers, numbers past the end, words, and a missing number are all rejected.

**`find` works but the number I need is different.**
`find` numbers its matches from 1. Run `list` to get the real task number.

**I made a mistake in a task. How do I change it?**
There is no edit command. `delete` the task and add it again.

**How do I clear all tasks?**
Delete them one by one, or quit Steph and delete `data/steph.txt`.

**How do I move my tasks to another computer?**
Quit Steph and copy `data/steph.txt` into the `data` folder on the other
computer.

**My tasks are missing after starting Steph.**
Steph reads `data/steph.txt` relative to where it is started. Starting it from
a different folder (or double-clicking the jar from a different place) gives
you a different, probably empty, list. Start it from the original folder, or
copy the file over.

**Steph says `Sorry, I couldn't read the save file. Starting with an empty list.`**
The file exists but can't be read (for example, it is locked or you lack
permission). Fix that, then restart. Until you do, Steph works with an empty
list, and the next change would overwrite the file.

**Steph says `Sorry, I couldn't save your tasks: ...`.**
The change was made for this session but not written to disk, usually because
the `data` folder isn't writable or the disk is full. Fix that and make any
change to save again.

**The console prints `Skipping corrupted line: ...`.**
A line in `data/steph.txt` was in the wrong format (perhaps after a hand-edit).
Steph loaded everything else and ignored that line.

**`java -jar steph.jar` says `Unsupported class file major version` or
`UnsupportedClassVersionError`.**
Your Java is older than 25. Run `java -version` to check, and install Java 25 or later.

**`java: command not found`.**
Java isn't installed, or isn't on your PATH. Install Java 25 or later and reopen the terminal.

**The window doesn't open.**
Run the command from a terminal (not by double-clicking) so you can see any
error message. Make sure you are on a desktop session, not a text-only or
remote shell.

**Can I use Steph without the window?**
Yes: `java -cp steph.jar steph.Steph` runs the console version. It uses the
same save file.

## Command summary

| Command | Format | Example |
|---|---|---|
| Add todo | `todo <task-name>` | `todo read book` |
| Add deadline | `deadline <task-name> /by <date>` | `deadline return book /by 2026-10-15 1800` |
| Add event | `event <task-name> /from <date> /to <date> [/force]` | `event camp /from 2026-10-15 1400 /to 2026-10-15 1800` |
| List | `list` | `list` |
| Mark done | `mark <task-number>` | `mark 2` |
| Mark not done | `unmark <task-number>` | `unmark 2` |
| Delete | `delete <task-number>` | `delete 3` |
| Find | `find <keyword>` | `find book` |
| Exit | `bye` | `bye` |

`<date>` is `yyyy-mm-dd` or `yyyy-mm-dd HHmm`.

## Other helpful information

### Glossary

| Term | Meaning |
|---|---|
| Task | One item in your list: a todo, a deadline or an event |
| Todo | A task with a name only |
| Deadline | A task that must be done by a date or date-time |
| Event | A task that spans a start and an end |
| Done / not done | Whether a task is marked `[X]` or `[ ]` |
| Task number | A task's position in `list`, counting from 1 |
| Clash | Two not-done events whose times overlap |
| `/force` | An event flag that skips the clash check |
| Save file | `data/steph.txt`, where your tasks are stored |
| Keyword | The text `find` searches task names for |

### Date and time formats

| You can type | Meaning | Shown as |
|---|---|---|
| `2026-10-15` | Oct 15 2026, no time (midnight) | `Oct 15 2026` |
| `2026-10-15 1800` | Oct 15 2026 at 6:00pm | `Oct 15 2026, 6:00pm` |

Times use the 24-hour clock with no colon (`0930`, `1800`). Steph shows times
in 12-hour form. Depending on your Java setup, `am`/`pm` may appear in
capitals (`6:00PM`).

### Save file format

`data/steph.txt` has one task per line, with fields separated by ` | `:

```
T | 0 | read book
D | 0 | return book | 2026-10-15T18:00
E | 0 | camp | 2026-10-15T14:00 | 2026-10-15T18:00
```

* Field 1: type, `T` (todo), `D` (deadline) or `E` (event).
* Field 2: `1` if done, otherwise `0`.
* Field 3: the task name. **Names must not contain `|`.**
* Deadlines add a date-time; events add a start and an end. They use the
  form `yyyy-mm-ddThh:mm`; a plain `yyyy-mm-dd` is also accepted.

### Product specifications

| | |
|---|---|
| Runs on | Windows, macOS, Linux |
| Requires | Java 25 or later; JavaFX is bundled in `steph.jar` |
| Distribution | A single self-contained file, `steph.jar` |
| Interfaces | Chat window (`java -jar steph.jar`) and console (`java -cp steph.jar steph.Steph`) |
| Storage | One local text file, `data/steph.txt`; no network or account |
| Task numbering | Starts at 1 and follows the order of `list` |
