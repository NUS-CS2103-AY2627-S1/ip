# Ada User Guide

![Ada User Interface](Ui.png)

Ada is a desktop task-management chatbot that helps users organize tasks, deadlines, and events in one list.

People often have many responsibilities at the same time, making it easy to forget tasks or remember deadlines incorrectly. Ada was designed as a simple chat-based todo list to help users stay organised.

## Adding tasks

Ada supports three types of tasks: todo tasks, deadlines, and events.

### Adding a todo task

Adds an ordinary task without a deadline.

**Format:**

```text
todo <description>
```

**Example:**

```text
todo finish reading
```

### Adding a deadline

Adds a task that must be completed by a particular date or time.

**Format:**

```text
deadline <description> /by <date or date-time>
```

**Example:**

```text
deadline submit project report /by 2026-09-30 1800
```

If only a date is provided, Ada uses `23:59` as the default time.

### Adding an event

Adds an event with a starting time and an ending time.

**Format:**

```text
event <description> /from <start> /to <end>
```

**Example:**

```text
event project meeting /from 2026-09-18 1400 /to 2026-09-18 1530
```

An `HHmm` ending time can be used to represent an event ending on the same date as its starting time.

After adding a task, Ada displays a confirmation such as:

```text
I've added this task:
  [T][ ] finish reading
Now you have <number> tasks in the list.
```

## Managing tasks

Task numbers are displayed by the `list` command and start from `1`.

### Deleting a task

Removes a task from the task list.

```text
delete <task number>
```

Example:

```text
delete 3
```

### Marking a task as done

Marks a task as completed.

```text
mark <task number>
```

Example:

```text
mark 1
```

### Marking a task as unfinished

Changes a completed task back to unfinished.

```text
unmark <task number>
```

Example:

```text
unmark 1
```

## Viewing and organising tasks

### Listing tasks

Displays all tasks in the current task list.

```text
list
```

### Finding tasks

Searches task descriptions using a case-insensitive keyword.

```text
find <keyword>
```

Examples:

```text
find report
find project meeting
```

### Sorting tasks

Sorts tasks in the following order:

1. Unfinished tasks before completed tasks.
2. Deadlines before events.
3. Events before ordinary todo tasks.
4. Earlier deadlines and event starting times first.

```text
sort
```

### Exiting Ada

Ends the interaction with Ada.

```text
bye
```

## Date and time formats

| Format            | Example           | Usage                                |
| ----------------- | ----------------- | ------------------------------------ |
| `yyyy-MM-dd`      | `2026-09-30`      | Deadline date or event `/from` value |
| `yyyy-MM-dd HHmm` | `2026-09-30 1800` | Deadline or event date-time          |
| `HHmm`            | `1800`            | Event `/to` value on the same date   |

Times use the 24-hour clock and do not include a colon.

## Credits

The GitHub Actions workflow in `.github/workflows/gradle.yml` was adapted from an external workflow.

* **Source:** `https://github.com/se-edu/duke.git`
* **Original author or repository:** `damithc
Damith C. Rajapakse`
* **Reused parts:** Multi-platform build matrix, Gradle wrapper validation, Java setup, and Gradle check step
* **Modifications:** Adapted the workflow for the Ada project
* **Date accessed:** 09 September 2026
