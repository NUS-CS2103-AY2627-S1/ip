# HABI User Guide

HABI is a steady habit-building companion for keeping small commitments visible.
Use it to record tasks, deadlines, events, and quick notes in one place.

![HABI application window](Ui.png)

## Getting started

Install Java 25, download `habi.jar`, and run it from a terminal:

```bash
java -jar habi.jar
```

HABI stores your tasks and notes in `data/habi.txt` beside the JAR. Item numbers
shown by `list` and `notes` start at 1. Dates use the `yyyy-MM-dd` format.

## Commands

### Add a todo

`todo DESCRIPTION` adds a task without a date.

Example: `todo read one chapter`

### Add a deadline

`deadline DESCRIPTION /by yyyy-MM-dd` adds a task due on a date.

Example: `deadline submit reflection /by 2026-09-25`

### Add an event

`event DESCRIPTION /from START /to END` adds an event with start and end text.

Example: `event project meeting /from Mon 2pm /to 4pm`

### Manage tasks

- `list` shows all tasks.
- `find KEYWORD` shows tasks whose descriptions contain the keyword.
- `mark NUMBER` marks a task as complete.
- `unmark NUMBER` marks a task as incomplete.
- `delete NUMBER` removes a task.

Examples: `find project`, `mark 2`, `delete 3`

### Manage notes

- `note TEXT` saves a short note.
- `notes` shows all notes.
- `delete-note NUMBER` removes a note.

Examples: `note Ask about milestone`, `delete-note 1`

### Exit HABI

`bye` closes the application after HABI says goodbye.

## Input tips

Descriptions and notes cannot be empty. `list`, `notes`, and `bye` do not take
extra arguments. Do not enter tab characters in text: HABI uses tabs internally
to separate fields in its data file. If the data file is unreadable, HABI shows
an error and protects it from accidental overwrites; fix the file before adding
or changing tasks.
