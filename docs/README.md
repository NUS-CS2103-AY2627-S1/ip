# Bos User Guide

Bos is a chatbot that helps you keep track of todos, deadlines, and events. It can save your tasks, update their
completion status, and find tasks by description.

## Getting started

1. Launch Bos.
2. Type a command in the text box at the bottom of the window.
3. Press <kbd>Enter</kbd> to submit the command.

Bos automatically loads previously saved tasks when it starts. Changes are saved to `data/tasks.txt`, so they are
available the next time you run the chatbot.

## Command summary

| Action | Command format | Example |
| --- | --- | --- |
| Add a todo | `todo DESCRIPTION` | `todo read chapter 1` |
| Add a deadline | `deadline DESCRIPTION /by DATE` | `deadline submit report /by 2026-09-30 2359` |
| Add an event | `event DESCRIPTION /from START /to END` | `event project meeting /from 2026-09-21 1400 /to 2026-09-21 1600` |
| Show all tasks | `list` | `list` |
| Find tasks | `find KEYWORD` | `find report` |
| Mark a task as done | `mark TASK_NUMBER` | `mark 2` |
| Mark a task as not done | `unmark TASK_NUMBER` | `unmark 2` |
| Delete a task | `delete TASK_NUMBER` | `delete 2` |
| Finish the conversation | `bye` | `bye` |

Words in uppercase, such as `DESCRIPTION`, are values that you replace. Do not type the uppercase placeholder
itself. Enter command words such as `todo` and `list` in lowercase.

## Understanding the task list

Each task has a type and a completion status:

- `[T]` identifies a todo.
- `[D]` identifies a deadline.
- `[E]` identifies an event.
- `[ ]` means that the task is not completed.
- `[X]` means that the task is completed.

For example, `[D][X] submit report (by: Sep 30 2026, 11:59 PM)` is a completed deadline.

Task numbers are the numbers shown by the `list` command. Use these numbers with `mark`, `unmark`, and `delete`.

## Adding a todo

Use a todo for a task without a specific date or time.

Format: `todo DESCRIPTION`

Example:

```text
todo read chapter 1
```

Bos adds the todo to the end of the task list:

```text
Got it. I've added this task:
  [T][ ] read chapter 1
Now you have 1 tasks in the list.
```

## Adding a deadline

Use a deadline for a task that must be completed by a particular date or time.

Format: `deadline DESCRIPTION /by DATE`

Example:

```text
deadline submit report /by 2026-09-30 2359
```

For a consistently formatted date and time, enter it as `yyyy-MM-dd HHmm`, using a 24-hour time. Bos displays a
valid value in a more readable form:

```text
[D][ ] submit report (by: Sep 30 2026, 11:59 PM)
```

You can also use free-form text such as `Friday` or `tomorrow evening`. Bos stores and displays free-form text as
entered.

## Adding an event

Use an event for an activity with a start and end.

Format: `event DESCRIPTION /from START /to END`

Example:

```text
event project meeting /from 2026-09-21 1400 /to 2026-09-21 1600
```

Bos displays the event as:

```text
[E][ ] project meeting (from: Sep 21 2026, 2:00 PM to: Sep 21 2026, 4:00 PM)
```

The start and end accept the same `yyyy-MM-dd HHmm` format or free-form text described for deadlines.

## Listing tasks

Use `list` to display every task and its task number.

```text
list
```

Example output:

```text
Here are the tasks in your list:
1.[T][ ] read chapter 1
2.[D][ ] submit report (by: Sep 30 2026, 11:59 PM)
```

## Finding tasks

Use `find` to show tasks whose descriptions contain a keyword.

Format: `find KEYWORD`

Example:

```text
find report
```

The search is case-insensitive, so `find report` also matches a task containing `Report`. It searches task
descriptions only, not dates or event times.

The numbers in search results identify the order of the matches, not necessarily their numbers in the full task
list. Run `list` before using `mark`, `unmark`, or `delete` on a found task.

## Marking a task as done

Use the task number shown by `list`.

Format: `mark TASK_NUMBER`

```text
mark 2
```

The task's status changes from `[ ]` to `[X]`.

## Marking a task as not done

Use `unmark` to change a completed task back to incomplete.

Format: `unmark TASK_NUMBER`

```text
unmark 2
```

The task's status changes from `[X]` to `[ ]`.

## Deleting a task

Use the task number shown by `list` to permanently remove a task.

Format: `delete TASK_NUMBER`

```text
delete 2
```

Bos removes the task and renumbers the tasks that follow it. Run `list` again to see the updated numbers.

## Finishing the conversation

Enter `bye` when you are finished:

```text
bye
```

Bos saves the current task list and displays a farewell message. When using the graphical version, close the window
afterward to exit the application.

## Notes and input restrictions

- Every todo, deadline, and event must have a description.
- Deadline and event date-time fields cannot be empty.
- Bos rejects a new task if another task has exactly the same description, even when its task type or scheduled
  time is different.
- Descriptions, dates, and times cannot contain the `|` character because Bos uses it to store task data.
- `TASK_NUMBER` must be a whole number that currently appears in the full task list.

## Use of AI

Generative AI tools were used to assist with troubleshooting, build configuration, and documentation. All AI-assisted suggestions were reviewed and verified by the author.
