# Nova User Guide

![Nova](Ui.png)

Nova is a task tracker you talk to. You type a command, it does the thing and
answers in one line. It keeps three kinds of task — **todos**, **deadlines**
and **events** — saves your list the moment it changes, and never asks you to
click anything.

## Quick start

1. Install **Java 25**. On macOS, use the Azul Zulu 25 build with JavaFX.
2. Download `nova.jar` from the [latest release](https://github.com/dbs15329/ip/releases).
3. Put it in an empty folder and run it from a terminal in that folder:

   ```
   java -jar "nova.jar"
   ```

4. Type a command into the box at the bottom and press **Enter**.

Not sure where to start? Type `help`.

## Features

### Adding a todo — `todo`

A task with no date attached.

Format: `todo DESCRIPTION`

Example: `todo read book`

```
Added. Now 1st in your list.
  [T][ ] read book
```

### Adding a deadline — `deadline`

A task that has to be finished by a particular moment.

Format: `deadline DESCRIPTION /by DATE`

Example: `deadline return book /by 2019-12-02 1800`

```
Added. Now 2nd in your list.
  [D][ ] return book (by: Dec 02 2019, 6:00pm)
```

### Adding an event — `event`

A task that runs from one moment to another. The two can be on different days.

Format: `event DESCRIPTION /from DATE /to DATE`

Example: `event trip /from 2019-08-06 /to 2019-08-08`

```
Added. Now 3rd in your list.
  [E][ ] trip (from: Aug 06 2019 to: Aug 08 2019)
```

Nova will not accept an event that ends before it starts.

### Listing everything — `list`

Format: `list`

```
2 tasks in your list.
  1. [T][ ] read book
  2. [D][ ] return book (by: Dec 02 2019, 6:00pm)
```

The number at the front is what `mark`, `unmark` and `delete` expect.

### Marking a task done — `mark` / `unmark`

Format: `mark NUMBER` or `unmark NUMBER`

Example: `mark 1`

```
Marked done.
  [T][X] read book
```

### Deleting a task — `delete`

Format: `delete NUMBER`

Example: `delete 1`

```
Removed. 1 task left.
  [T][X] read book
```

### Searching — `find`

Shows the tasks whose description contains the keyword. Case does not matter,
and it matches inside words, so `book` also finds `Bookkeeping`.

Format: `find KEYWORD`

Example: `find book`

```
1 task matching "book".
  1. [D][ ] return book (by: Dec 02 2019, 6:00pm)
```

### Seeing one day — `on`

Shows everything falling on a date: deadlines due that day, and events running
across it.

Format: `on DATE`

Example: `on 2019-12-02`

```
1 task on Dec 02 2019.
  1. [D][ ] return book (by: Dec 02 2019, 6:00pm)
```

### Sorting — `sort`

Reorders your list so the soonest thing is first. Dated tasks come first in
time order, then undated todos alphabetically. The new order is saved, so it
sticks.

Format: `sort`

```
Sorted by date, soonest first.
  1. [D][ ] return book (by: Dec 02 2019, 6:00pm)
  2. [T][ ] read book
```

### Getting help — `help`

Lists every command with its format.

Format: `help`

### Exiting — `bye`

Saves and closes the window.

Format: `bye`

## Writing dates

Anywhere Nova asks for a `DATE`, you can write:

- `2019-12-02` — year first
- `2/12/2019` — day first

Either can be followed by a time on the 24-hour clock:

- `2019-12-02 1800`
- `2019-12-02 18:00`

Leave the time out and Nova shows just the date. Dates that do not exist, such
as `2019-02-30`, are rejected rather than quietly shifted.

## When something goes wrong

Nova answers in red and tells you what to type instead:

```
"tomorrow" didn't parse as a date. Try 2019-12-02, or 2019-12-02 1800 for a time.
```

It also refuses to add the same task twice, and points at the one you already
have.

## Saving your data

Your list is written to `data/nova.txt` next to the JAR every time it changes,
and read back on startup. There is no save command and nothing to remember.

The file is plain text, so you can read or edit it. If a line gets damaged,
Nova skips that line, tells you how many it skipped, and loads the rest rather
than losing everything.

## Command summary

- `todo DESCRIPTION`
- `deadline DESCRIPTION /by DATE`
- `event DESCRIPTION /from DATE /to DATE`
- `list`
- `mark NUMBER` / `unmark NUMBER`
- `delete NUMBER`
- `find KEYWORD`
- `on DATE`
- `sort`
- `help`
- `bye`
