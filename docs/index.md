# FlorkOfCows User Guide

AI-assisted development note: This project was developed with the assistance of GitHub Copilot. The tool was used to help draft code, improve validation/error handling, and refine automated tests.

FlorkOfCows is a lightweight task manager for tracking todos, deadlines, and events. It supports adding tasks, marking them complete, searching by keyword, viewing tasks on a specific date, and attaching tags.

## Getting started

Start the app using the Java launcher. Once running, enter commands in the chat box or terminal.

Example:

```
list
```

This shows all current tasks in your list.

## Add a todo

Use `todo` to add a simple task with no date.

Example:

```
todo read book
```

Expected outcome:

```
Okayyy added!
   [T][ ] read book
You now have 1 tasks. Jiayous!
```

## Add a deadline

Use `deadline` with `/by` to create a task that has a due date or deadline.

Example:

```
deadline submit report /by 2026-09-30
```

Expected outcome:

```
Okayyy added!
   [D][ ] submit report (by: Sep 30 2026)
You now have 2 tasks. Jiayous!
```

## Add an event

Use `event` with `/from` and `/to` to schedule an event.

Example:

```
event team meeting /from 2026-09-20 0900 /to 2026-09-20 1000
```

Expected outcome:

```
Okayyy added!
   [E][ ] team meeting (from: Sep 20 2026, 9:00am to: Sep 20 2026, 10:00am)
You now have 3 tasks. Jiayous!
```

## List tasks

Use `list` to view all tasks.

Example:

```
list
```

Expected outcome:

```
 Shag sia.
 1.[T][ ] read book
 2.[D][ ] submit report (by: Sep 30 2026)
 3.[E][ ] team meeting (from: Sep 20 2026, 9:00am to: Sep 20 2026, 10:00am)
```

## Mark or unmark tasks

Use `mark <index>` to complete a task and `unmark <index>` to reverse it.

Example:

```
mark 1
```

Expected outcome:

```
Marked it!
   [T][X] read book
```

## Delete a task

Use `delete <index>` to remove a task.

Example:

```
delete 2
```

Expected outcome:

```
Cans. Deleted!
   [D][ ] submit report (by: Sep 30 2026)
Shiok, you have 2 tasks left. Jiayous!
```

## Find tasks by keyword

Use `find <keyword>` to search task descriptions.

Example:

```
find book
```

Expected outcome:

```
Nah here:
 1.[T][X] read book
```

## View tasks on a date

Use `on <yyyy-MM-dd>` to list tasks scheduled or due on that date.

Example:

```
on 2026-09-20
```

Expected outcome:

```
Here's what's on 2026-09-20:
 1.[E][ ] team meeting (from: Sep 20 2026, 9:00am to: Sep 20 2026, 10:00am)
```

## Tag tasks

Use `tag <index> <tag>` to add a tag to a task.

Example:

```
tag 1 #fun
```

Expected outcome:

```
Tag added!
   [T][X] read book #fun
```

## Exit the app

Use `bye` to exit the program.

Example:

```
bye
```

Expected outcome:

```
See ya!
```

## Error handling

FlorkOfCows validates malformed or incomplete commands and shows a clear error message instead of crashing. Examples include:

- missing task descriptions
- missing `/by`, `/from`, or `/to` values
- invalid task numbers
- malformed dates
- impossible event ranges where the end is before the start

Example:

```
todo
```

Expected outcome:

```
 Eh lock in!!! No todo description sia.
```

## Image Credits

1. FlorkOfCows User image: "https://in.pinterest.com/pin/florkofcows-icon--5136987067416450/"

1. FlorkOfCows image: "https://merch.kawaentertainment.com/en-ca/collections/florkofcows?srsltid=AU7gw4VWBu3-Ch"
