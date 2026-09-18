# Bibi User Guide

Bibi keeps track of what needs doing, with a dry sense of humor and a dependable memory.
Tell it what you need done, and it keeps track between sessions. Doing it, however, is still your job ;)

![Bibi in use](Ui.png)

## Getting started

1. Make sure you have **Java 25** installed.
2. Download `bibi.jar` from the [latest release](https://github.com/joshmode/ip/releases).
3. Put it in a folder of its own, and double click on the file. Or run it:

```
java -jar bibi.jar
```

Bibi saves your tasks to `data/bibi.txt` beside wherever you started it, so a fresh
folder gives you a fresh list.

> **On an Intel Mac**, the window will not open: the bundled graphics libraries are
> the Apple Silicon ones, since that is what current Macs run. Use the text interface
> instead, which needs no graphics libraries: `java -cp bibi.jar bibi.Bibi`. Apple
> Silicon Macs, Windows and 64-bit Linux need nothing extra.

> **Tip:** type in the box at the bottom and press <kbd>Enter</kbd>, or click **Send**.
> Everything is a short typed command, with keyboard shortcuts to keep things moving.

### Using the keyboard and transcript

- **Enter** submits the input; focus returns to the input after submission.
- **Up / Down** in the input recalls this session's commands without running them.
  Your unfinished draft returns when you move Down past the newest command.
- A rejected command stays in the input so you can correct it. Blank input is ignored.
- Select text in any previous command or reply, then use **Ctrl+C** (or **Cmd+C** on macOS)
  to copy it. Selection shortcuts act on the focused text field, not on the task list.
- New output follows the transcript when you are near the bottom. If you scroll up to
  read, your position stays put; scroll back down when you are ready.

Commands and `/by`, `/from`, `/to` markers ignore capitalization. Extra separator
whitespace is fine; descriptions keep their capitalization, with whitespace runs
collapsed to single spaces. For dates starting with a digit, a space after a marker
is optional: `deadline return book /by21/12/2026` also works.

## Adding things to do

Bibi keeps three kinds of tasks.

| Kind | What it is | Command |
|------|------------|---------|
| To Do | something with no particular date | `todo <description>` |
| Deadline | something due by a date | `deadline <description> /by <when>` |
| Event | something that runs from one time to another | `event <description> /from <start> /to <end>` |

For example:

```
todo buy a birthday present
deadline submit CS2103T iP /by 18/9/2026 2359
event tutorial /from 15/9/2026 14:00 /to 15/9/2026 15:00
```

Bibi validates each one and tells you how many tasks you have:

```
Added. Remembering it is my job. Doing it is still yours:
  [D][ ] submit CS2103T iP (by: 18 Sep 2026 11:59PM)
Your list holds 1 task.
```

Each task type gets its own confirmation, so you can tell at a glance which one you
just added — a ToDo waits on you to stop procrastinating, a deadline runs the clock down,
and an event happens whether or not you are ready for it.

### Writing dates

Dates are parsed as real dates, not free text, which is what lets Bibi sort them and search
them by day.

| Form | Example |
|------|---------|
| `d/M/yyyy` | `21/12/2026`, `1/2/2026` |
| `d-M-yyyy` | `21-12-2026` |
| `d.M.yyyy` | `21.12.2026` |
| `d MMM yyyy` or `d MMMM yyyy` | `21 Dec 2026`, `21 December 2026` |
| `d/M/yy` | `21/12/26` |
| existing ISO form, `yyyy-MM-dd` | `2026-12-21` |

Numeric dates with the day first always mean **day/month/year**: `03/04/2026` is
3 April, never March 4. Two-digit years `00`–`99` mean **2000–2099**.

Any date may have a time: `1800`, `18:00`, `6 pm`, or `6:00 pm`. Month names and
AM/PM are case-insensitive, and extra spacing between date/time parts is accepted.
`12 am` means midnight; `12 pm` means noon.

Bibi displays dates as `21 Dec 2026` and timed dates as `21 Dec 2026 6:00PM`.
A date without a time stays date-only. Past dates are accepted. Impossible dates
such as `31/04/2026`, invalid times, and events ending before they start are rejected.
Leap days must belong to leap years. Phrases such as `next Tuesday` are not supported.

Existing save files still load. Saved dates keep their ISO `yyyy-MM-dd` form, with
`HHmm` only when a time was supplied.

## Seeing your list

```
list
```

Each task shows its kind and whether it is done:

```
1. [D][X] submit CS2103T iP (by: 18 Sep 2026 11:59PM)
2. [T][ ] buy a birthday present
```

`[T]`, `[D]` and `[E]` are ToDo, deadline and event. `[X]` means done, `[ ]` means not yet.

## Putting the list in order

```
sort
```

Sorts everything with a date into chronological order, earliest first, and puts undated
ToDos at the end — a ToDo has no particular deadline to be ready for.

The new order is **kept**, not just displayed. That matters because the numbers you type
into `mark`, `unmark` and `remove` come from the list, so an order that vanished when you
looked away would leave those numbers pointing at the wrong tasks.

## Finding things

```
find book
on 18/9/2026
```

- `find` searches descriptions and ignores case, so `find BOOK` finds "read book".
- `on` shows deadlines falling on a date, and events running across it.

Both keep each task's number **from the full list**, so you can act on a result straight
away without running `list` first.

## Marking things done, and removing them

```
mark 2
unmark 2
remove 2
delete 2
```

Numbers come from the most recent listing. `mark` ticks a task off, `unmark` reopens
it, and `remove` takes it off the list for good. `delete` is the same command under
another name, so use whichever comes to mind. Marking a task that is already done,
or reopening one that was never done, says so rather than reporting a change that
did not happen.

Sorting or removing a task can make numbers in older replies stale. Use `list`
again before acting on an old entry. Confirmations show the affected task so you
can check what changed; filtered results retain the full-list numbers.

### Undoing one change

```
undo
```

`undo` restores the list before the most recent change: adding, removing, marking,
unmarking, or sorting. It restores completion states and task order, including the
numbers, and saves the restored list. There is **one undo step, for this session
only**, and no redo. A second undo is rejected until you make another change.

Viewing, searching, greetings, rejected commands, and operations that change nothing
do not replace your undo step. A change that failed to save is still applied in memory
and can be undone. Closing Bibi discards undo history; it is not written to the save file.

## Help, and leaving

```
help
help --examples
bye
```

`help` lists every command, grouped by what it is for, with a one-line
description each — enough to find the command word you have forgotten without
reading past it.

`help --examples` prints that same list and then adds the reference detail:
worked examples, every accepted date format, how task numbers behave, and the
keyboard shortcuts. Anything else after `help` is rejected, so a mistyped flag
is reported rather than quietly ignored.

`bye` closes Bibi. Changes are saved after each task-changing command; watch for
a save warning if a write could not finish.

### A quick hello

`hi`, `hello`, `hey`, and `thanks` get short replies. Capitalization and harmless
punctuation such as `HELLO!` are fine. These work only as complete standalone inputs;
`todo say hello` still adds a task. Bibi does not collect missing arguments through
follow-up questions.

## When something goes wrong

Bibi tries to say what is actually wrong rather than just refusing. Mistakes are shown
in amber, with a rule down the left edge, so you can spot them when scrolling back:

- **An unknown command** names the word it could not place.
- **A missing part** shows the shape of the command, with an example.
- **A parameter used twice** — `deadline report /by Mon /by Tue` — says so, rather than
  complaining about an unreadable date.
- **A task you already have** is pointed at by number, and nothing is changed.
- **A damaged save file** is reported line by line; the lines Bibi *could* read are kept.
- **An invalid task number** explains the current range, or that the list is empty.
- **A failed save** warns that the command already changed the list in memory. Do not
  repeat the operation: check the file location and permissions before another save.
  Unsaved changes can be lost when Bibi closes. In the GUI, an applied command clears
  the input even if saving failed; a validation rejection stays available for editing.

## Command summary

| Command | Does | Example |
|---------|------|---------|
| `todo` | adds an undated task | `todo buy a present` |
| `deadline` | adds a task due by a date | `deadline report /by 18/9/2026` |
| `event` | adds a task with a start and end | `event camp /from 15/9/2026 /to 17/9/2026` |
| `list` | shows everything | `list` |
| `sort` | orders by date, undated last | `sort` |
| `find` | searches descriptions | `find book` |
| `on` | shows what falls on a date | `on 18/9/2026` |
| `mark` | ticks a task off | `mark 2` |
| `unmark` | reopens a task | `unmark 2` |
| `remove` or `delete` | deletes a task | `delete 2` |
| `undo` | restores the most recent change once, in this session | `undo` |
| `help` | lists the commands; `--examples` adds examples and date formats | `help --examples` |
| `bye` | closes Bibi | `bye` |
| `hi`, `hello`, `hey`, `thanks` | replies briefly without changing tasks | `hello!` |

## Running without the window, for the old-fashioned

Bibi also has a text-only interface, which is what its automated tests drive:

```
java -cp bibi.jar bibi.Bibi
```

Same chatbot, same save file, but on a command line interface, the good old-fashioned way.
