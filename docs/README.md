# Oreo User Guide

![Ui Screenshot](./Ui.png)

**Oreo** is a chat-based task manager for keeping track of to-dos, deadlines,
events, and tags. Type a command in the input box and press <kbd>Enter</kbd> or
select **Send**. Your tasks are saved automatically.

## Quick start

Add a task, then view it:

```text
todo buy milk
list
```

Task numbers in the list are used by commands such as `mark`, `delete`, and
`tag`.

## Adding tasks

### To-dos

Use `todo` for tasks without a date.

Example: `todo <description> [#tag]...`

```text
todo buy milk #errands
```

Oreo adds the task to your list. Tags are optional and must begin with `#`.

### Deadlines

Use `deadline` for tasks due on a particular date. Dates use `YYYY-MM-DD`.

Example: `deadline <description> /by <YYYY-MM-DD> [#tag]...`

```text
deadline submit report /by 2026-09-30 #school
```

### Events

Use `event` for activities with a start and end date. The end date must be
after the start date.

Example: `event <description> /from <YYYY-MM-DD> /to <YYYY-MM-DD> [#tag]...`

```text
event project meeting /from 2026-09-15 /to 2026-09-16 #team
```

## Viewing and finding tasks

Use `list` to see all tasks.

```text
list
```

Use `find` to search task descriptions. The search is not case-sensitive.

Example: `find <keyword>`

```text
find report
```

## Updating tasks

Mark a task as complete or incomplete with its number.

```text
mark 2
unmark 2
```

Delete a task permanently with:

```text
delete 2
```

## Using tags

Add tags while creating a task, or update an existing task later.

```text
tag 1 #important #week1
untag 1 #week1
list tags
```

A tag may contain letters, numbers, hyphens, and underscores. `list tags`
groups tasks by their tags.

## Exiting Oreo

Use `bye` to close the session.

```text
bye
```

## Tips

- Commands ignore extra spaces at the beginning, end, or between words.
- Oreo shows a highlighted error message when a command needs correction.
- Identical tasks cannot be added twice.
