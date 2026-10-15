# Michael — User Guide

Michael is a friendly space-themed task chatbot. Type a command in the chat box and press Enter to manage your tasks. Tasks are saved automatically and are available the next time you start Michael.

## Features

### Add tasks

Add a task without a date using `todo`:

```
todo Read chapter 3
```

Add a deadline using `deadline` and `/by`. Use the format `yyyy-MM-dd HHmm`:

```
deadline Submit assignment /by 2026-10-05 2359
```

Add an event with a start and end time using `/from` and `/to`:

```
event Project meeting /from Monday 1400 /to Monday 1500
```

### View your tasks

Use `list` to see all tasks. Each task has a number for use with other commands.

### Mark tasks as done

Mark a task complete with its number:

```
mark 2
```

Reopen it with `unmark`:

```
unmark 2
```

### Delete tasks

Delete a task by number:

```
delete 2
```

### Find tasks

Search for a keyword with `find`:

```
find assignment
```

### Undo the last change

Use `undo` to reverse the most recent successful add, mark, unmark, or delete command. Michael supports one undo at a time.

### Exit Michael

Use `bye` to close the chatbot:

```
bye
```

Task numbers must be positive whole numbers. If a command is incomplete or incorrectly formatted, Michael will explain what needs to be corrected.
