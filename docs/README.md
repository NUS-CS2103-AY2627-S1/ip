# Alzara User Guide

Alzara is a desktop chatbot that keeps track of your to-dos, deadlines,
and events. Type a command and
press Enter (or click Send in the GUI). Alzara replies immediately and
saves your task list automatically after every change.

## Adding a to-do: `todo`

Adds a task with just a description, no date attached.

Example: `todo read book`

## Adding a deadline: `deadline`

Adds a task that is due by a specific date.

Example: `deadline return book /by 2019-10-15`

The date must be in `yyyy-mm-dd` format.

## Adding an event: `event`

Adds a task that runs from a start date to an end date (inclusive). The
start date cannot be after the end date.

Example: `event trip /from 2019-10-14 /to 2019-10-16`

## Listing all tasks: `list`

Shows every task currently tracked, numbered from 1 in the order they
were added.

Example: `list`

## Marking a task as done: `mark`

Marks the task at the given list number as done. Use `list` first to
find the number.

Example: `mark 1`

## Marking a task as not done: `unmark`

Marks the task at the given list number as not done.

Example: `unmark 1`

## Deleting a task: `delete`

Removes the task at the given list number.

Example: `delete 2`

## Finding tasks by keyword: `find`

Shows every task whose description contains all the given keywords,
matched case-insensitively. Multiple keywords must all match.

Example: `find book`

## Viewing your schedule: `view`

Shows every deadline due, and every event happening, on a given date. Deadlines match that exact date, events match any date within their start-to-end range (inclusive of both ends).

Example: `view yyyy-mm-dd`

Matches are numbered from 1 and sorted chronologically by their own date (a deadline by its own date, an event by its start date) and not by the order they were added.

## Exiting the program: `bye`

Ends the session.

Example: `bye`

## Things to keep in mind

* Alzara won't add a `todo`, `deadline`, or `event` that's an exact match
  (same type, same description, same dates) of a task you already have -
  you'll get "This task has already been recorded." instead.
* A task description can't contain the `|` character, since Alzara uses
  it internally to separate fields in the save file.

## Saving and loading data

Alzara automatically saves your task list to `data/alzara.txt` after
every command that changes it, and loads it back the next time you
start the program. There is no need to save manually. If a line in the
save file is corrupted, Alzara reports it and discards only that entry,
keeping the rest of your tasks intact.

## Command summary

| Action | Format | Example |
|---|---|---|
| Add a to-do | `todo DESCRIPTION` | `todo read book` |
| Add a deadline | `deadline DESCRIPTION /by yyyy-mm-dd` | `deadline return book /by 2019-10-15` |
| Add an event | `event DESCRIPTION /from yyyy-mm-dd /to yyyy-mm-dd` | `event trip /from 2019-10-14 /to 2019-10-16` |
| List all tasks | `list` | `list` |
| Mark a task done | `mark TASK_NUMBER` | `mark 1` |
| Mark a task not done | `unmark TASK_NUMBER` | `unmark 1` |
| Delete a task | `delete TASK_NUMBER` | `delete 2` |
| Find tasks | `find KEYWORD [MORE_KEYWORDS...]` | `find book report` |
| View schedule for a date | `view yyyy-mm-dd` | `view 2019-10-15` |
| Exit | `bye` | `bye` |
