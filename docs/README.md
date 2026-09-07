# Bibi User Guide

Bibi is a task chatbot for ToDos, deadlines, and events. It runs either as a
window or in the console, and remembers your tasks between sessions.

## Starting Bibi

Double-click `bibi.jar` to open the window, or run it from a terminal:

```text
java -jar bibi.jar              # the GUI
java -cp bibi.jar bibi.Bibi     # the text-based interface
```

Either way, tasks are saved to `data/bibi.txt` beside wherever you started it,
so running Bibi in an empty folder gives you a fresh list.

## Adding tasks

```text
todo borrow book
deadline return book /by 2019-10-15
event project meeting /from 2019-08-06 1400 /to 2019-08-06 1600
```

A ToDo is just a description. A deadline has one date, and an event has a start
and an end.

### Writing dates

Dates are real dates, not free text, so Bibi can compare them:

| Form | Example |
|------|---------|
| `yyyy-MM-dd` | `2019-10-15` |
| `d/M/yyyy` | `2/12/2019` |
| either, plus a 24-hour time | `2/12/2019 1800` |

A date without a time means the whole day. A date that does not exist, such as
`2019-02-30`, is rejected rather than quietly moved. An event may not end before
it starts.

## Seeing your tasks

```text
list
```

Tasks show as `[T]`, `[D]` or `[E]` for the three types, and `[ ]` or `[X]` for
incomplete or complete:

```text
1. [T][X] borrow book
2. [D][ ] return book (by: Oct 15 2019)
```

## Sorting

```text
sort
```

Puts the list in order, earliest first. Tasks with no date go last, since a ToDo
has no particular moment to be ready for. The new order is saved, so it survives
into your next session, and the numbers you type afterwards refer to the order
you are looking at.

## Finding tasks

```text
find book
on 2019-10-15
```

`find` searches descriptions, ignoring case. `on` shows the deadlines due on a
date and the events running across it.

Both keep each task's number from the full list, so you can act on a result
straight away without listing everything first.

## Changing and removing tasks

```text
mark 2
unmark 2
remove 2
```

Task numbers come from the most recent listing.

## Getting help, and leaving

```text
help
bye
```

`help` lists every command. `bye` closes Bibi, from the window as well as the
console.
