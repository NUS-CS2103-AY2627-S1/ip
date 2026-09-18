# Yun User Guide

<img width="729" height="909" alt="Ui" src="https://github.com/user-attachments/assets/bb5e58ec-46e8-4787-81a3-a70807fa7b95" />

Yun is an interactive and friendly chatbot that helps you manage todos, deadlines, and events!

Tasks are automatically saved to `taskList.txt` when changes are made. 
A new file is created in the current working directory if it is not present.

## Adding todos

Use the following format:

```text
todo <description>
```

Example:

```text
todo read chapter 1
```

Yun will add the todo to your task list.

## Adding deadlines

Use the following format:

```text
deadline <description> /by <yyyy-MM-dd HHmm>
```

Example:

```text
deadline submit report /by 2026-08-31 0135
```

The date and time must use the `yyyy-MM-dd HHmm` format.

## Adding events

Use the following format:

```text
event <description> /from <yyyy-MM-dd HHmm> /to <yyyy-MM-dd HHmm>
```

Example:

```text
event team meeting /from 2026-08-31 1000 /to 2026-08-31 1130
```

The event's end time cannot be earlier than its start time.

## Listing tasks

Use:

```text
list
```

This prints out all tasks in the task list, together with their task numbers and completion statuses.

## Finding tasks

Use the following format:

```text
find <keyword>
```

Example:

```text
find report
```

Yun searches task descriptions for given keywords without considering letter case.

## Marking tasks as complete

Use the following format:

```text
mark <task number>
```

Example:

```text
mark 1
```

This marks task 1 as complete.

## Marking tasks as incomplete

Use the following format:

```text
unmark <task number>
```

Example:

```text
unmark 1
```

This marks task 1 as incomplete.

## Deleting tasks

Use the following format:

```text
delete <task number>
```

Example:

```text
delete 2
```

This removes task 2 from the task list.

## Viewing tasks on a date

Use the following format:

```text
on <yyyy-MM-dd>
```

Example:

```text
on 2026-08-31
```

This displays deadlines and events that occur on the specified date.

## Viewing available commands

Use:

```text
help
```

This displays a list of all commands supported by Yun.

## Exiting Yun

Use:

```text
bye
```

Yun saves the current task list before exiting.

## Task file

Yun stores tasks in `taskList.txt` in the program's current working directory.

If `taskList.txt` does not exist, Yun creates the file when a task is saved (and saves that task too!)

If the task file contains invalid data, Yun displays an error message. Make a backup copy of the file, then rename or remove it before restarting Yun.

## Date and time formats

Deadlines and event times use:

```text
yyyy-MM-dd HHmm
```

For example:

```text
2026-08-31 0135
```

The `on` command uses:

```text
yyyy-MM-dd
```

For example:

```text
2026-08-31
```
