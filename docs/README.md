# Sal User Guide

![Ui](Ui.png)

Sal is a desktop chatbot that helps you keep track of todos, deadlines, and events. You type commands in the chat box, and Sal replies in the same window. Tasks are saved automatically, so they are still there the next time you open the app.

## Quick start

1. Ensure that **Java 25** is installed on your computer.
   **Mac users:** Use a JDK 25 distribution that includes JavaFX, such as the one prescribed in the [SE-EDU Java installation guide](https://se-education.org/guides/tutorials/javaInstallationMac.html).
1. Download the latest `sal.jar` from the [Releases](https://github.com/HeYue474/ip/releases) page.
1. Copy the file to the folder you want to use as Sal's home folder. Sal will save your tasks in a `data` folder inside this location.
1. Open a terminal, `cd` to that folder, and run:

   ```
   java -jar sal.jar
   ```

   A window titled **Sal** should appear, with a greeting:

   ```
   Hello! I'm Sal.
   What can I do for you?
   ```

1. Type a command in the box at the bottom and press Enter, or click **Send**. Some commands you can try:

   * `todo read book` — adds a todo
   * `list` — shows all tasks
   * `bye` — ends the session

1. Refer to the feature sections below for the full command list.

**Notes about the command format:**

* Words in `UPPER_CASE` are parameters you replace with your own values.
* The command word (the first word) is case-sensitive. Use `todo`, not `Todo`.
* Task numbers are the numbers shown by `list`, starting from `1`.

## Adding todos

Adds a task that has a description only (no date or time).

Format: `todo DESCRIPTION`

Example: `todo read book`

Sal confirms the new task and the updated list size:

```
Got it. I've added this task:
  [T][ ] read book
Now you have 1 tasks in the list.
```

If the description is missing, Sal replies: `Correct format: todo <task name>`

## Adding deadlines

Adds a task that must be done by a specific date or date-time.

Format: `deadline DESCRIPTION /by DATE`

`DATE` can be a date only, or a date with a time:

* `yyyy-mm-dd` — e.g. `2019-10-15`
* `d/M/yyyy` — e.g. `2/12/2019`
* `yyyy-mm-dd HHmm` — e.g. `2019-10-15 1800`
* `d/M/yyyy HHmm` — e.g. `2/12/2019 1800`

Times use 24-hour `HHmm` (for example, `1800` is 6:00 pm). Dates without a time are shown as a date only.

Example: `deadline return book /by 2019-10-15`

```
Got it. I've added this task:
  [D][ ] return book (by: Oct 15 2019)
Now you have 1 tasks in the list.
```

Example: `deadline return book /by 2/12/2019 1800`

```
Got it. I've added this task:
  [D][ ] return book (by: Dec 02 2019, 6:00 pm)
Now you have 1 tasks in the list.
```

If the description or `/by` date is missing, Sal replies: `Correct format: deadline <task name> /by <date/time>`

If the date/time cannot be parsed, Sal replies: `Invalid date/time format. Use yyyy-mm-dd (e.g., 2019-10-15) or d/M/yyyy HHmm (e.g., 2/12/2019 1800).`

## Adding events

Adds a task that spans a start and end date or date-time. The same date formats as deadlines are accepted for both `/from` and `/to`.

Format: `event DESCRIPTION /from START /to END`

Example: `event project meeting /from 2019-10-15 /to 2019-10-16`

```
Got it. I've added this task:
  [E][ ] project meeting (from: Oct 15 2019 to: Oct 16 2019)
Now you have 1 tasks in the list.
```

Example: `event project meeting /from 2/12/2019 1400 /to 2/12/2019 1600`

```
Got it. I've added this task:
  [E][ ] project meeting (from: Dec 02 2019, 2:00 pm to: Dec 02 2019, 4:00 pm)
Now you have 1 tasks in the list.
```

If the description, `/from`, or `/to` is missing, Sal replies: `Correct format: event <task name> /from <start> /to <end>`

## Listing tasks

Shows every task in the order they were added. Each line starts with a 1-based number, a type icon (`T` todo, `D` deadline, `E` event), and a status icon (`X` if done, a space if not). Tags, if any, appear at the end of the line.

Format: `list`

Example: `list`

```
Here are the tasks in your list:
1.[T][ ] read book
2.[D][ ] return book (by: Oct 15 2019)
3.[E][ ] project meeting (from: Oct 15 2019 to: Oct 16 2019)
```

## Marking a task as done

Marks the task at the given list number as completed.

Format: `mark TASK_NUMBER`

Example: `mark 2`

```
Nice! I've marked this task as done:
  [D][X] return book (by: Oct 15 2019)
```

If the number is missing or not an integer, Sal replies: `Correct format: mark <task number>`

If the number does not match a task in the list, Sal shows an error and the list is unchanged.

## Marking a task as not done

Marks a completed task as not done yet.

Format: `unmark TASK_NUMBER`

Example: `unmark 2`

```
OK, I've marked this task as not done yet:
  [D][ ] return book (by: Oct 15 2019)
```

If the number is missing or not an integer, Sal replies: `Correct format: unmark <task number>`

## Deleting a task

Removes the task at the given list number. Remaining tasks are renumbered.

Format: `delete TASK_NUMBER`

Example: `delete 1`

```
Noted. I've removed this task:
  [T][ ] read book
Now you have 2 tasks in the list.
```

If the number is missing or not an integer, Sal replies: `Correct format: delete <task number>`

## Finding tasks

Shows tasks whose description contains the given keyword. Matching is a **case-sensitive substring** search (so `book` matches `read book`, but `Book` does not). Only descriptions are searched, not tags or dates. Matches are numbered from 1 in the order they appear in the full list.

Format: `find KEYWORD`

Example: `find book`

```
Here are the matching tasks in your list:
1.[T][ ] read book
2.[D][ ] return book (by: Oct 15 2019)
```

If no tasks match, Sal still shows the heading, with no tasks listed underneath.

If the keyword is missing, Sal replies: `Correct format: find <keyword>`

## Tagging a task

Adds a tag to an existing task. Tags are shown with a `#` in the task list. A task can have several tags; they appear in the order they were added.

Format: `tag TASK_NUMBER TAG`

`TAG` may be written with or without a leading `#`. After that, it must be a single word of letters, digits, hyphens, or underscores (for example `fun`, `#urgent`, `cs-2103_t`).

Example: `tag 1 fun`

```
OK, I've tagged this task:
  [T][ ] read book #fun
```

Example: `tag 1 #urgent`

```
OK, I've tagged this task:
  [T][ ] read book #fun #urgent
```

If the same tag is added twice, Sal replies: `This task already has the tag #fun` (using that tag's name) and does not change the task.

If the format is wrong or the tag name is invalid, Sal replies: `Correct format: tag <task number> <tag>`

## Exiting

Ends the session. Sal replies with a goodbye message and disables the input box and **Send** button. You can then close the window.

Format: `bye`

Example: `bye`

```
Bye. Hope to see you again soon!
```

## Saving data

Sal saves the task list to `data/sal.txt` in the home folder after every change (add, mark, unmark, delete, or tag). You do not need a save command.

If the saved file cannot be read at startup, Sal starts with an empty list and shows: `Could not load saved tasks. Starting with an empty list.`

If a change cannot be written to disk, Sal shows: `Could not save tasks to disk.`

## Unrecognised commands

If Sal does not recognise the command word (including an empty input), it replies: `Command not recognised.`

## Command summary

| Action | Format | Example |
| --- | --- | --- |
| Add todo | `todo DESCRIPTION` | `todo read book` |
| Add deadline | `deadline DESCRIPTION /by DATE` | `deadline return book /by 2019-10-15` |
| Add event | `event DESCRIPTION /from START /to END` | `event meeting /from 2019-10-15 /to 2019-10-16` |
| List tasks | `list` | `list` |
| Mark done | `mark TASK_NUMBER` | `mark 2` |
| Mark not done | `unmark TASK_NUMBER` | `unmark 2` |
| Delete | `delete TASK_NUMBER` | `delete 1` |
| Find | `find KEYWORD` | `find book` |
| Tag | `tag TASK_NUMBER TAG` | `tag 1 fun` |
| Exit | `bye` | `bye` |
