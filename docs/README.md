# Blud User Guide

**Blud** is a friendly chatbot for keeping track of tasks. Enter one command
at a time in the chat window and press **Enter**. Your tasks are saved
automatically, so they are available the next time you start Blud.

## Quick start

### Set up and start Blud

Before starting, ensure that Java 25 is installed and configured on your
computer. Then:

1. Download the latest release of `blud.jar` into a local folder.
2. Open a terminal.
3. Navigate to the folder containing `blud.jar`.
4. Start Blud with:

   ```bash
   java -jar blud.jar
   ```

5. Follow the rest of this guide for detailed command instructions.

### Sample conversation

Here is an example of what a conversation with Blud looks like:

![Blud chatbot interface](Ui.png)

### Command overview

| Command | Usage | Description |
| --- | --- | --- |
| Add to-do | `todo <description>` | Adds a task without a date. |
| Add deadline | `deadline <description> /by <date time>` | Adds a task that must be completed by a date and time. |
| Add event | `event <description> /from <start> /to <end>` | Adds a task that takes place between two date-times. |
| List | `list` | Shows all tasks in their current order. |
| Find | `find <keyword>` | Finds tasks whose descriptions contain the keyword. |
| Mark | `mark <task number>` | Marks a task as completed. |
| Unmark | `unmark <task number>` | Marks a completed task as not done. |
| Delete | `delete <task number>` | Removes a task from the list. |
| Sort | `sort <criterion> [direction]` | Sorts tasks by deadline, event start date, or status. |
| Exit | `bye` | Closes Blud. |

Blud supports three kinds of tasks:

| Task type | Syntax | Example |
| --- | --- | --- |
| To-do | `todo <description>` | `todo buy groceries` |
| Deadline | `deadline <description> /by <date time>` | `deadline submit report /by 2/12/2026 1800` |
| Event | `event <description> /from <start> /to <end>` | `event team meeting /from 3/12/2026 1400 /to 3/12/2026 1600` |

Dates use `d/M/yyyy HHmm`, a 24-hour format. For example, `2/12/2026 1800`
means 2 December 2026 at 6:00 pm. An event's end must be after its start.

## Commands

### Add a task

Use one of the three task commands above. The task description can contain
spaces.

```text
todo buy groceries
```

Expected response:

```text
added: [T][ ] buy groceries
Now you have 1 tasks in the list
```

```text
deadline submit report /by 2/12/2026 1800
```

Expected response:

```text
added: [D][ ] submit report (by: Dec 02 2026, 6:00 pm)
Now you have 2 tasks in the list
```

```text
event team meeting /from 3/12/2026 1400 /to 3/12/2026 1600
```

Expected response:

```text
added: [E][ ] team meeting (from: Dec 03 2026, 2:00 pm to: Dec 03 2026, 4:00 pm)
Now you have 3 tasks in the list
```

### View all tasks

Use `list`. Tasks are shown with one-based numbers; use these numbers with
`mark`, `unmark`, and `delete`.

```text
list
```

Expected response:

```text
Here are the tasks in your list:
1. [T][ ] buy groceries
2. [D][ ] submit report (by: Dec 02 2026, 6:00 pm)
3. [E][ ] team meeting (from: Dec 03 2026, 2:00 pm to: Dec 03 2026, 4:00 pm)
```

### Find tasks

Use `find <keyword>` to search descriptions. The search is case-insensitive
and also matches part of a description.

```text
find report
```

Expected response:

```text
Here are the matching tasks in your list:
1. [D][ ] submit report (by: Dec 02 2026, 6:00 pm)
```

### Mark a task as done

Use `mark <task number>`.

```text
mark 1
```

Expected response:

```text
Nice! I've marked this task as done:
[T][X] buy groceries
```

### Mark a task as not done

Use `unmark <task number>` to restore an incomplete task.

```text
unmark 1
```

Expected response:

```text
OK, I've marked this task as not done yet:
[T][ ] buy groceries
```

### Delete a task

Use `delete <task number>`. The task is removed permanently from your list.

```text
delete 1
```

Expected response:

```text
Task removed successfully:
[T][ ] buy groceries
```

### Sort tasks

Use `sort <criterion> [direction]` to reorder the current list. The direction
is optional and defaults to `asc`.

| Criterion | Sorts by |
| --- | --- |
| `deadline` | Deadline, earliest first |
| `event` | Event start date, earliest first |
| `status` | Completion status, incomplete first |

Use `desc` for the reverse order. For example:

```text
sort deadline desc
```

Expected response:

```text
Tasks sorted by deadline in descending order.
```

Sorting changes the order used by `list`, `find`, `mark`, `unmark`, and
`delete` for the current session. New tasks follow the selected order.

### Exit Blud

Use `bye` when you are finished. The command is case-insensitive, so `BYE`
works too.

```text
bye
```

Expected response:

```text
Thanks for the conversation, see you soon!
```

## Helpful tips

- Enter commands with exactly one space between parameters.
- Task numbers always refer to the current order shown by `list`.
- If a command is invalid, Blud explains what needs to be corrected and
  remains ready for your next command.
