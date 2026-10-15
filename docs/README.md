# Nova

Nova is a friendly chatbot for managing todos, deadlines, events, and tags.

## Quick Start

1. Download the latest [`nova.jar`](https://github.com/rahulg1507/ip/releases) from the project's GitHub releases.
2. Make sure [JDK 25](https://www.oracle.com/java/technologies/downloads/) is installed.
3. Open a terminal in the folder containing the JAR and run:

   ```text
   java -jar nova.jar
   ```

Nova saves your tasks locally and remembers them the next time you run it.

## Features

Task indexes are one-based: the first task is task `1`.

### Add a todo

Add a task without a date.

```text
todo DESCRIPTION
```

Example: `todo buy groceries`

```text
Got it. I've added this task:
  [T][ ] buy groceries
Now you have 1 tasks in the list.
```

### Add a deadline

Add a task that must be completed by a date. Use `yyyy-mm-dd` dates.

```text
deadline DESCRIPTION /by DATE
```

Example: `deadline submit report /by 2026-10-15`

```text
Got it. I've added this task:
  [D][ ] submit report (by: Oct 15 2026)
Now you have 1 tasks in the list.
```

### Add an event

Add an event with a start and end date-time. Use `yyyy-MM-dd HH:mm` values.

```text
event DESCRIPTION /from START /to END
```

Example: `event team meeting /from 2026-10-15 09:00 /to 2026-10-15 10:00`

```text
Got it. I've added this task:
  [E][ ] team meeting (from: 2026-10-15 09:00 to: 2026-10-15 10:00)
Now you have 1 tasks in the list.
```

### List tasks

Display every task in its current order.

```text
list
```

Example output:

```text
Here are the tasks in your list:
1.[T][ ] buy groceries
2.[D][ ] submit report (by: Oct 15 2026)
```

### Mark a task as done

Mark a task complete using its index.

```text
mark INDEX
```

Example: `mark 1`

```text
Nice! I've marked this task as done:
   [T][X] buy groceries
```

### Unmark a task

Change a completed task back to not done.

```text
unmark INDEX
```

Example: `unmark 1`

```text
OK, I've marked this task as not done yet:
   [T][ ] buy groceries
```

### Delete a task

Remove a task from the list.

```text
delete INDEX
```

Example: `delete 1`

```text
Noted. I've removed this task:
   [T][ ] buy groceries
Now you have 0 tasks in the list.
```

### Find tasks by keyword

Search task descriptions without worrying about letter case.

```text
find KEYWORD
```

Example: `find book`

```text
Here are the matching tasks in your list:
1.[T][ ] read book
2.[D][ ] return book (by: Jun 6 2026)
```

### Add a tag

Attach a tag to a task. Tags start with `#` and cannot contain spaces.

```text
tag INDEX #label
```

Example: `tag 1 #urgent`

```text
Noted. I've added a tag to this task:
   [T][ ] buy groceries #urgent
```

Adding a tag that is already present is ignored.

### Remove a tag

Remove an existing tag from a task.

```text
untag INDEX #label
```

Example: `untag 1 #urgent`

```text
Noted. I've removed the tag from this task:
   [T][ ] buy groceries
```

### Find tasks on a date

Show deadlines and events occurring on a specific date. Use `yyyy-mm-dd`.

```text
on DATE
```

Example: `on 2026-10-15`

```text
Tasks on 2026-10-15:
[D][ ] submit report (by: Oct 15 2026)
[E][ ] team meeting (from: 2026-10-15 09:00 to: 2026-10-15 10:00)
```

### Exit Nova

End the current session.

```text
bye
```

```text
Bye. Hope to see you again soon!
```
