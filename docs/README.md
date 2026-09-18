# Hamtaro User Guide

Hamtaro is a small task-management chatbot. Use the text box at the bottom of the window to enter a command, then click **Squeak!** or press **Enter**. Hamtaro displays the result in the conversation area.

Your tasks are saved automatically in `data/hamtaro.txt`, so they remain available the next time you start the application.

## Adding tasks

### To-do tasks

Use `todo` for a task without a date:

```text
todo Read chapter 3
```

### Deadlines

Use `deadline`, followed by a description and `/by` with the due date:

```text
deadline Submit assignment /by 30/09/2026
```

Accepted date formats are `dd/MM/yyyy`, `dd-MM-yyyy`, and `yyyy-MM-dd`.

### Events

Use `event`, followed by a description, `/from`, and `/to`:

```text
event Software engineering lecture /from 01/10/2026 0900 /to 01/10/2026 1100
```

Accepted date and time formats are `dd/MM/uuuu HHmm`, `dd-MM-uuuu HHmm`, `uuuu/MM/dd HHmm`, and `uuuu-MM-dd HHmm`. Use 24-hour time, such as `0900` or `1430`.

## Viewing and finding tasks

### List all tasks

Use:

```text
list
```

This shows every task and its task number. Tasks are displayed with `[ ]` when incomplete and `[X]` when complete. The type is shown as `[T]` for to-dos, `[D]` for deadlines, and `[E]` for events.

### Find tasks by description

Use `find` followed by a keyword:

```text
find assignment
```

Hamtaro searches task descriptions without distinguishing between upper- and lower-case letters. The keyword may be part of a longer description.

### Use tags

Add a tag to a task with:

```text
tag 1 #school
```

Remove a tag with:

```text
untag 1 #school
```

Tags may contain letters, numbers, hyphens (`-`), and underscores (`_`). They are case-insensitive; Hamtaro stores and displays them in lower-case. The `#` is required in the command.

To list tasks with a particular tag, use:

```text
list #school
```

## Managing tasks

Mark a task as complete:

```text
mark 1
```

Mark it as incomplete again:

```text
unmark 1
```

Delete a task permanently from the task list:

```text
delete 1
```

Task numbers refer to the full list shown by `list`. If you have searched or filtered the tasks, run `list` again before using `mark`, `unmark`, `tag`, `untag`, or `delete` to confirm the correct number.

## Closing Hamtaro

Use the following command to end the chatbot session:

```text
bye
```

Hamtaro saves changes as they are made. If the task data file cannot be read or written, Hamtaro displays an error message.

## Command summary

| Command | Purpose |
| --- | --- |
| `todo [description]` | Add a to-do task |
| `deadline [description] /by [date]` | Add a deadline |
| `event [description] /from [date time] /to [date time]` | Add an event |
| `list` | Show all tasks |
| `list #[tag]` | Show tasks with a tag |
| `find [keyword]` | Find tasks by description |
| `tag [task number] #[tag]` | Add a tag |
| `untag [task number] #[tag]` | Remove a tag |
| `mark [task number]` | Mark a task complete |
| `unmark [task number]` | Mark a task incomplete |
| `delete [task number]` | Delete a task |
| `bye` | Exit Hamtaro |
