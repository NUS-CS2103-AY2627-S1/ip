# WOOF WOOF! I'm **Dog**! 🐶

Dog is a task management application that helps you organize your tasks with a friendly interface.
Whether you have deadlines to meet, events to plan, or simple todos to track, Dog is here to help! WOOF!

## Table of Contents

- [Getting Started](#getting-started)
- [Adding Tasks](#adding-tasks)
    - [Todo](#todo)
    - [Deadline](#deadline)
    - [Event](#event)
- [Viewing Tasks](#viewing-tasks)
- [Marking Tasks as Done](#marking-tasks-as-done)
- [Deleting Tasks](#deleting-tasks)
- [Finding Tasks](#finding-tasks)
- [File Storage](#file-storage)
- [Tips](#tips)
- [Command Reference](#command-reference)

## Getting Started

When you launch Dog, you'll see a friendly welcome message:

```
WOOF WOOF! How can I help? WOOF
```

Just type your commands in the input box at the bottom of the window and press Enter, and Dog will obey them like a
_gooooood booooyyy_.

## Adding Tasks

Dog supports three types of tasks: Todo, Deadline, and Event.

### Todo

A todo is a task without a specific deadline.

**Command:** `todo <description>`

**Aliases:** `t`, `task`, `add`

**Example:**

```
todo buy groceries
```

**Response:**

```
WOOF! I've added a new task: 
[T][ ] buy groceries
```

### Deadline

A deadline is a task that must be completed by a specific date.

**Command:** `deadline <description> /by <date>`

**Aliases:** `d`

**Date format:** `yyyy-MM-dd` (e.g., 2026-12-31)

**Example:**

```
deadline submit assignment /by 2026-09-18
```

**Response:**

```
WOOF! I've added a new task: 
[D][ ] submit assignment (by: Sept 18 2026)
```

### Event

An event is a task that occurs within a specific time period.

**Command:** `event <description> /from <start date> /to <end date>`

**Aliases:** `e`

**Date format:** `yyyy-MM-dd` (e.g., 2026-12-31)

**Example:**

```
event party /from 2026-09-18 /to 2026-09-20
```

**Response:**

```
WOOF! I've added a new task: 
[E][ ] party (from: Sept 18 2026 to: Sept 20 2026)
```

## Viewing Tasks

To see all your tasks, use the `list` command.

**Command:** `list`

**Aliases:** `l`, `ls`

**Example:**

```
list
```

**Response:**

```
WOOF! I've fetched the tasks in your list:
1. [T][ ] buy groceries
2. [D][ ] submit assignment (by: Sept 18 2026)
3. [E][ ] party (from: Sept 18 2026 to: Sept 20 2026)
```

## Marking Tasks as Done

Mark a task as completed when you finish it.

**Command:** `mark <task number>`

**Aliases:** `m`, `done`, `complete`

**Example:**

```
mark 1
```

**Response:**

```
WOOF! I've marked this task as done:
[T][X] buy groceries
```

💡 **Tip:** You can view the task numbers using the `list` command.

## Deleting Tasks

Remove tasks you no longer need.

**Command:** `delete <task number>`

**Aliases:** `del`, `remove`, `rm`

**Example:**

```
delete 2
```

**Response:**

```
WOOF! I've deleted this task:
[D][ ] submit assignment (by: Sept 18 2026)
You have 2 tasks left in your list! WOOF!
```

## Finding Tasks

Find tasks that match a keyword.

**Command:** `find <keyword>`

**Aliases:** `f`, `search`

**Example:**

```
find assignment
```

**Response:**

```
WOOF! I've fetched these matching tasks from your list:
1. [D][ ] submit assignment (by: Sept 18 2026)
```

💡 **Tip:** Search is case-insensitive. "assignment", "Assignment", and "ASSIGNMENT" will all find the same tasks.

## Exiting the Application

When you're done managing your tasks, you can exit the application using the `bye` command.

**Command:** `bye`

**Aliases:** `exit`, `q`

**Example:**

```
bye
```

All your tasks are automatically saved when you exit, so you won't lose any of your progress.

## File Storage

Dog automatically saves your tasks to a file so your data persists between sessions.

- **Save location:** `./data/dog.txt` (in the project directory)
- **Format:** Each task is saved on a separate line with the format:
    - Todo: `T | [X or space] | description`
    - Deadline: `D | [X or space] | date | description`
    - Event: `E | [X or space] | from date | to date | description`

You don't need to manually save your tasks. Dog saves each time you update your task list.

## Tips

- **Keep your descriptions clear** - Use descriptive task names so you know what each task is about
- **Use deadlines wisely** - Set realistic deadlines to avoid last-minute stress
- **Search frequently** - Use `find` to quickly locate specific tasks in your list
- **Mark tasks promptly** - Mark tasks as done right after completing them to keep your list accurate
- **Need motivation?** - Try typing `woof` for a surprise!

## Command Reference

| Command                                       | Description              | Aliases                 | Example                                       |
|-----------------------------------------------|--------------------------|-------------------------|-----------------------------------------------|
| `todo <description>`                          | Add a todo task          | `t`, `task`, `add`      | `todo buy groceries`                          |
| `deadline <description> /by <date>`           | Add a deadline task      | `d`                     | `deadline submit assignment /by 2026-09-18`   |
| `event <description> /from <date> /to <date>` | Add an event task        | `e`                     | `event party /from 2026-09-18 /to 2026-09-20` |
| `list`                                        | View all tasks           | `l`, `ls`               | `list`                                        |
| `mark <number>`                               | Mark a task as done      | `m`, `done`, `complete` | `mark 1`                                      |
| `delete <number>`                             | Delete a task            | `del`, `remove`, `rm`   | `delete 2`                                    |
| `find <keyword>`                              | Search for tasks         | `f`, `search`           | `find assignment`                             |
| `woof`                                        | Get a surprise response! | `arf`, `bark`           | `woof`                                        |
| `bye`                                         | Exit the application     | `exit`, `q`             | `bye`                                         |

**Happy task managing! WOOF! 🐶**
