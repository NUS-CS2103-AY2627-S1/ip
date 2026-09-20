# Atom User Guide

Atom is a chatbot designed to help keep track of tasks.

![Atom user interface](Ui.png)

## Getting Started

1. Ensure that you have Java 25 installed. You can check this by running `java -version` in the terminal.
2. Download `Atom.jar` from the [latest release](https://github.com/LucasNgui/ip/releases/download/A-Release/Atom.jar).
3. Move the file into a new folder.
4. Run `java -jar "Atom.jar"`.

## Adding todos

Add a task without a start or end time.

Example: `todo borrow book`

```
Alright! I've added this task:
[D][ ] borrow book
You now have 1 tasks in the list.
```

## Adding deadlines

Add a task with only an end time.

Times use the 24-hour `yyyy-MM-dd HH:mm` format.

Example: `deadline return book /by 2026-04-30 18:00`

```
Alright! I've added this task:
[D][ ] return book (by: Apr 30 2026, 6:00 PM)
You now have 2 tasks in the list.
```

## Adding events

Add a task with both a start time and an end time.

Example: `event project meeting /from 2026-04-14 14:00 /to 2026-04-14 16:00`

```
Alright! I've added this task:
[E][ ] project meeting (from: Apr 14 2026, 2:00 PM to: Apr 14 2026, 4:00 PM)
You now have 3 tasks in the list.
```

## Listing tasks

List all tasks that have been added so far.

Example: `list`

```
Here are the tasks in your list:
1. [T][ ] borrow book
2. [D][ ] return book (by: Apr 30 2026, 6:00 PM)
3. [E][ ] project meeting (from: Apr 14 2026, 2:00 PM to: Apr 14 2026, 4:00 PM)
``` 

## Viewing a schedule

View all deadlines and events occurring on a date.

Example: `schedule 2026-04-14`

```
Tasks scheduled for Apr 14 2026:
1. [E][ ] project meeting (from: Apr 14 2026, 2:00 PM to: Apr 14 2026, 4:00 PM)
```

Events spanning multiple days appear on each date they cover.

## Marking and unmarking tasks

Mark a task as done or undone.

Example: `mark 1`

```
Awesome! Marking this task as done:
[T][X] borrow book
``` 

`unmark 1`

```
Ok. Marking this task as undone:
[T][ ] borrow book
```

## Deleting tasks

Delete a task from the list.

Example: `delete 2`

```
Alright! I've removed this task:
[D][ ] return book (by: Apr 30 2026, 6:00 PM)
You now have 2 tasks in the list.
``` 

## Finding tasks

Find a task using a keyword.

Example: `find book`

```
I've found these matching tasks in your list:
1. [T][ ] borrow book
```

## Command summary

| Command | Format | Description |
| --- | --- | --- |
| Add todo | `todo <description>` | Adds a task without a date or time. |
| Add deadline | `deadline <description> /by <yyyy-MM-dd HH:mm>` | Adds a task with an end time. |
| Add event | `event <description> /from <yyyy-MM-dd HH:mm> /to <yyyy-MM-dd HH:mm>` | Adds a task with a start and end time. |
| List tasks | `list` | Shows all tasks. |
| View schedule | `schedule <yyyy-MM-dd>` | Shows deadlines and events occurring on a date. |
| Mark task | `mark <index>` | Marks a task as done. |
| Unmark task | `unmark <index>` | Marks a task as undone. |
| Delete task | `delete <index>` | Deletes a task. |
| Find tasks | `find <keyword>` | Shows tasks whose descriptions contain the keyword. |
| Exit Atom | `bye` | Closes the application. |
