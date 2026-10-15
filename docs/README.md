# Computa User Guide

Computa is a simple task manager. Use the command box to add tasks, view your tasks, search for tasks, and keep track of what you have completed.

## Task types

Computa supports four types of tasks:

- **Todo**: A task with a description and no date or time.
- **Deadline**: A task that must be completed by a specific date and time.
- **Event**: An activity with a start date and time and an end date and time.
- **Recurring task**: A task that repeats every week on a chosen day.

## Adding tasks

### Add a todo

```text
todo <description>
```

Example: `todo Buy groceries`

### Add a deadline

Use the date and time format `yyyy-MM-dd HHmm`.

```text
deadline <description> / <date and time>
```

Example: `deadline Submit assignment / 2026-10-05 2359`

### Add an event

Provide the start and end date and time using the same format, `yyyy-MM-dd HHmm`.

```text
event <description> / <start> / <end>
```

Example: `event Project meeting / 2026-09-21 1400 / 2026-09-21 1530`

### Add a recurring task

Use a weekday from Monday to Sunday. The weekday is not case-sensitive.

```text
recurring <description> / <weekday>
```

Example: `recurring Plan the week / Monday`

## Viewing and searching tasks

### List all tasks

```text
list
```

Computa displays the tasks currently visible in your list, each with a number.

### Find a task

```text
find <search text>
```

Example: `find assignment`

Computa displays tasks whose descriptions contain the search text.

## Updating tasks

First use `list` to find the task number.

### Mark a task as done

```text
mark <task number>
```

Example: `mark 2`

### Mark a task as undone

```text
unmark <task number>
```

Example: `unmark 2`

### Delete a task

```text
delete <task number>
```

Example: `delete 2`

## Exiting Computa

```text
bye
```

Use this command when you want to close the application.

## Recurring tasks

When you create a recurring task, Computa keeps the weekly rule in your task list. For example, `recurring Submit report / Friday` creates a task that repeats every Friday.

On the relevant day, Computa creates an ordinary task instance for that week. You can mark that instance as done, just like any other task. The recurring rule remains, so a new instance can be created in a later week.

Recurring rules are shown in the task list together with the current generated instance. Task numbers refer only to tasks currently shown in the list. Run `list` before using `mark`, `unmark`, or `delete` if you are unsure of a task's number.

Deleting a generated recurring-task instance removes that week's task. The recurring rule continues to repeat.

## Common errors

- **A task description is required**: Add text after the task command.
- **Invalid deadline format** or **Invalid event date/time**: Use `yyyy-MM-dd HHmm`, such as `2026-10-05 2359`.
- **Weekday must be Monday to Sunday**: Check that the recurring task uses a valid weekday.
- **Missing task number**: Add a task number after `mark`, `unmark`, or `delete`.
- **Unknown command**: Check the spelling and use one of the commands in this guide.

Your tasks are saved while you use Computa, so you can continue working with them when you open the application again.
