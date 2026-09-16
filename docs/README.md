# Nova User Guide

<img src="Ui.png" alt="Nova GUI" width="700">

**Nova** is a desktop task management chatbot for managing todos, deadlines, and events through simple text commands.

Type a command into the input box and press **Enter**, or click **Send**, to get started.

---

## Quick Start

1. Ensure you have **Java 25 or above** installed on your computer.

   Verify your Java version by running:

   ```bash
   java -version
   ```

2. Download the latest Nova JAR file.

3. Move the JAR file into a folder where you would like Nova and its task data to be stored.

4. Open your terminal and navigate to that folder.

5. Launch Nova:

   ```bash
   java -jar nova.jar
   ```

> [!NOTE]
> The exact JAR filename may differ depending on the downloaded version.

Nova automatically saves your tasks locally and restores them the next time you launch the application.

---

## Command Overview

| Action | Command | Example |
| --- | --- | --- |
| Add a todo | `todo DESCRIPTION` | `todo Read book` |
| Add a deadline | `deadline DESCRIPTION /by DATE TIME` | `deadline Submit report /by 18/9/2026 2359` |
| Add an event | `event DESCRIPTION /from DATE TIME /to DATE TIME` | `event Meeting /from 19/9/2026 1400 /to 19/9/2026 1600` |
| View all tasks | `list` | `list` |
| Mark a task | `mark NUMBER` | `mark 1` |
| Unmark a task | `unmark NUMBER` | `unmark 1` |
| Find tasks | `find KEYWORD` | `find book` |
| Delete a task | `delete NUMBER` | `delete 2` |
| Exit Nova | `bye` | `bye` |

### Date and time format

Commands involving dates and times use:

```text
d/M/yyyy HHmm
```

For example:

```text
18/9/2026 2359
```

represents **18 September 2026 at 11:59 PM**.

---

## Adding a todo: `todo`

Adds a task that does not have a specific date or time.

**Format**

```text
todo DESCRIPTION
```

**Example**

```text
todo Read CS2103T textbook
```

Nova responds with:

```text
Got it. I've added this task:
[T][ ] Read CS2103T textbook
```

---

## Adding a deadline: `deadline`

Adds a task that must be completed by a specific date and time.

**Format**

```text
deadline DESCRIPTION /by DATE TIME
```

**Example**

```text
deadline Submit assignment /by 18/9/2026 2359
```

Nova responds with:

```text
Got it. I've added this task:
[D][ ] Submit assignment (by: Sep 18 2026, 11:59 PM)
```

---

## Adding an event: `event`

Adds an event with a specified start and end time.

**Format**

```text
event DESCRIPTION /from DATE TIME /to DATE TIME
```

**Example**

```text
event Project meeting /from 19/9/2026 1400 /to 19/9/2026 1600
```

Nova responds with:

```text
Got it. I've added this task:
[E][ ] Project meeting
(from: Sep 19 2026, 2:00 PM to: Sep 19 2026, 4:00 PM)
```

### Detecting event clashes

Nova automatically warns you when a newly added event overlaps with an existing event.

For example, suppose you already have:

```text
event Project meeting /from 19/9/2026 1400 /to 19/9/2026 1600
```

and attempt to add:

```text
event Consultation /from 19/9/2026 1500 /to 19/9/2026 1700
```

Nova will warn you that the two events clash.

---

## Viewing your tasks: `list`

Displays all tasks currently stored in Nova.

**Format**

```text
list
```

**Example output**

```text
Here are the tasks in your list:
1.[T][ ] Read CS2103T textbook
2.[D][ ] Submit assignment (by: Sep 18 2026, 11:59 PM)
3.[E][ ] Project meeting
```

The number beside each task is its **task number**. Use this number with commands such as `mark`, `unmark`, and `delete`.

---

## Marking a task as done: `mark`

Marks an existing task as completed.

**Format**

```text
mark NUMBER
```

**Example**

```text
mark 1
```

Nova responds with:

```text
Nice! I've marked this task as done:
[T][X] Read CS2103T textbook
```

---

## Marking a task as not done: `unmark`

Changes a completed task back to incomplete.

**Format**

```text
unmark NUMBER
```

**Example**

```text
unmark 1
```

Nova responds with:

```text
OK, I've marked this task as not done yet:
[T][ ] Read CS2103T textbook
```

---

## Finding tasks: `find`

Searches for tasks containing a given keyword in their descriptions.

The search is case-insensitive. For example, `find book` can match tasks containing `Book` or `BOOK`.

**Format**

```text
find KEYWORD
```

**Example**

```text
find assignment
```

Nova responds with:

```text
Here are the matching tasks:
1.[D][ ] Submit assignment (by: Sep 18 2026, 11:59 PM)
```

---

## Deleting a task: `delete`

Permanently removes a task using its task number.

**Format**

```text
delete NUMBER
```

**Example**

```text
delete 2
```

Nova responds with:

```text
Noted. I've removed this task:
[D][ ] Submit assignment (by: Sep 18 2026, 11:59 PM)
```

> [!TIP]
> Use `list` first if you are unsure of a task's number.

---

## Exiting Nova: `bye`

Exits the application.

**Format**

```text
bye
```

Nova responds with:

```text
Bye. Hope to see you again soon!
```

Your tasks are saved automatically before you leave.

---

## Saving your tasks

Nova automatically stores your tasks locally.

You do not need to manually save after adding, deleting, marking, or unmarking a task. Your saved tasks will be loaded automatically the next time Nova starts.

> [!WARNING]
> Avoid manually editing Nova's data file while the application is running, as invalid data may prevent tasks from loading correctly. you start Nova, so you do not need to manually save your task list.s