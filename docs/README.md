# Zeus User Guide

Zeus is a task-tracking chatbot that helps you manage your tasks using simple text commands.

You can add tasks, set deadlines and events, mark tasks as completed, search for tasks, and view your full task list through the graphical interface.

![Zeus GUI](Ui.png)

## Getting Started

### Running Zeus

1. Download `Zeus.jar` from the latest GitHub release.
2. Ensure that Java 25 is installed on your computer.
3. Open a terminal in the folder containing `Zeus.jar`.
4. Run:

```text
java -jar Zeus.jar
```

The Zeus graphical interface should open.

### Using Zeus

Enter a command into the text box at the bottom of the window and press **Enter**.

For example:

```text
todo buy groceries
```

Zeus will respond to confirm that the task has been added.

You can enter:

```text
help
```

at any time to view the available commands.

---

## Adding a Todo

Use `todo` for a task that does not have a specific date.

### Format

```text
todo DESCRIPTION
```

### Example

```text
todo buy groceries
```

---

## Adding a Deadline

Use `deadline` for a task that needs to be completed by a particular date.

### Format

```text
deadline DESCRIPTION /by yyyy-MM-dd
```

### Example

```text
deadline submit CS2103 project /by 2026-09-18
```

Dates must follow the `yyyy-MM-dd` format.

---

## Adding an Event

Use `event` for something that takes place between a start date and an end date.

### Format

```text
event DESCRIPTION /from yyyy-MM-dd /to yyyy-MM-dd
```

### Example

```text
event overseas trip /from 2026-12-01 /to 2026-12-05
```

---

## Viewing Your Tasks

Use `list` to display all tasks currently stored in Zeus.

### Format

```text
list
```

Each task is displayed with a number that can be used with commands such as `mark`, `unmark`, and `delete`.

---

## Marking a Task as Completed

Use `mark` followed by the task number.

### Format

```text
mark NUMBER
```

### Example

```text
mark 1
```

This marks task 1 as completed.

---

## Marking a Task as Incomplete

Use `unmark` to change a completed task back to incomplete.

### Format

```text
unmark NUMBER
```

### Example

```text
unmark 1
```

---

## Deleting a Task

Use `delete` followed by the task number.

### Format

```text
delete NUMBER
```

### Example

```text
delete 2
```

This removes task 2 from your task list.

---

## Finding Tasks

Use `find` to search for tasks containing a particular keyword.

### Format

```text
find KEYWORD
```

### Example

```text
find project
```

Zeus will display tasks whose descriptions contain the keyword.

---

## Viewing Help

Use the `help` command to display the commands supported by Zeus.

### Format

```text
help
```

The command should be entered by itself.

For example:

```text
help
```

is valid, while:

```text
help todo
```

is not.

Commands are case-sensitive, so:

```text
HELP
```

is treated as an unknown command.

---

## Exiting Zeus

Use:

```text
bye
```

to exit the application.

---

## Date Format

Dates used in deadlines and events must follow:

```text
yyyy-MM-dd
```

For example:

```text
2026-09-18
```

---

## Command Summary

| Command | Purpose |
| --- | --- |
| `todo DESCRIPTION` | Add a task without a date |
| `deadline DESCRIPTION /by DATE` | Add a task with a deadline |
| `event DESCRIPTION /from DATE /to DATE` | Add an event |
| `list` | View all tasks |
| `find KEYWORD` | Search for tasks |
| `mark NUMBER` | Mark a task as completed |
| `unmark NUMBER` | Mark a task as incomplete |
| `delete NUMBER` | Delete a task |
| `help` | View available commands |
| `bye` | Exit Zeus |

---

## Acknowledgements

OpenAI Codex was used as a development assistant for debugging, code refinement, testing, and GUI improvements. All suggestions were reviewed and adapted by the project author.