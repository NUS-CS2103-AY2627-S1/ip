---
layout: page
title: CLI User Guide
---

## Table of Contents
{:.no_toc}

- ToC
{:toc}

---

Marquee CLI uses commands to create, manipulate and filter tasks.
The following lists the available commands in Marquee.

### 1. `help` command

Shows the user manual for the specified command, or all commands if not specified.

- Syntax: `help <command name>`

### 2. `bye` command

Exits the program.

- Syntax: `bye`

### 3. `load` command

Loads the last checklist you saved.

- Syntax: `load`

### 4. `save` command

Save the checklist to load later.

- Syntax: `save`

### 5. `list` command

List all the tasks in your current checklist.

- Syntax: `list`

### 6. `todo` command

Creates a task with no start or ending time, called a to-do task.

- Syntax: `todo [<description>] [/completed] [/incomplete]`
  - `<description>`: the description of this task.
  - `/completed`: mark this task as completed after creating it. Takes priority over `/incomplete`.
  - `/incomplete`: mark this task as incomplete after creating it. Is set by default.

To-do tasks are tagged with the tag `T`. 

Without a starting or ending time, to-do tasks cannot be found with the `find` command if flag `/from` or `/to` is set.

### 7. `deadline` command

Creates a task with an ending time, called a deadline task.

- Syntax: `deadline [<description>] [/by <end>] [/completed] [/incomplete]`
  - `<description>`: the description of this task.
  - `/by <end>`: the ending time of this task.
  - `/completed`: mark this task as completed after creating it. Takes priority over `/incomplete`.
  - `/incomplete`: mark this task as incomplete after creating it. Is set by default.

Deadline tasks are tagged with the tag `D`. The ending time of a `Deadline` task is called the deadline for convenience.

Without a starting time, this task type cannot be found with the `find` command if flag `/from` is set.

### 8. `event` command

Creates a task with both a starting and ending time, called an event task.

- Syntax: `event [<description>] [/from <start>] [/to <end>] [/completed] [/incomplete]`
  - `<description>`: the description of this task.
  - `/from <start>`: the starting time of this task.
  - `/to <end>`: the ending time of this task.
  - `/completed`: mark this task as completed after creating it. Takes priority over `/incomplete`.
  - `/incomplete`: mark this task as incomplete after creating it. Is set by default.

Event tasks are tagged with the tag `E`.

### 9. `find` command

Finds the tasks matching the following conditions.

- Syntax: `find [<description>] [/tags <tag>...] [/from <start>] [/to <end>] [/completed] [/incomplete] [/chain]`
  - `<description>`: the string to search for in task descriptions.
  - `/tags <tag>...`: a list of the names of the tags to search for.
  - `/from <start>`: the earliest time to find task.
  - `/to <end>`: the latest time to find task.
  - `/completed`: set this flag to search for completed tasks only. Takes priority over `/incomplete`.
  - `/incomplete`: set this flag to search for incomplete tasks only. Is set by default.
  - `/chain`: set this flag to search only from the last command's result

If no argument is given, the command returns nothing.

The result of this command can be operated on by other tasks by setting flag `/chain`.

### 10. `edit` command

Changes the properties of the tasks at the given indices.

- Syntax: `edit <index>... [/description <description>] [/from <start>] [/to <end>] [/completed] [/incomplete] [/chain]`
  - `<index>...`: a list of indices of the tasks to edit
  - `/description <description>`: the new description of the task
  - `/from <start>`: the new starting time of the task
  - `/to <end>`: the new ending time of the task
  - `/completed`: mark the tasks as completed. Takes priority over `/incomplete`
  - `/incomplete`: mark the tasks as incomplete
  - `/chain`: set this flag to edit tasks from the last command's result

This command returns the modified tasks as a result set that can be operated on by other tasks by setting flag `/chain`.

Even if there is no actual change in value, the command still treats the task as modified.

### 11. `edit-all` command

Changes the properties of all tasks.

- Syntax: `edit-all [/description <description>] [/from <start>] [/to <end>] [/completed] [/incomplete] [/chain]`
  - `/description <description>`: the new description of the task
  - `/from <start>`: the new starting time of the task
  - `/to <end>`: the new ending time of the task
  - `/completed`: mark the tasks as completed. Takes priority over `/incomplete`
  - `/incomplete`: mark the tasks as incomplete
  - `/chain`: set this flag to edit only tasks from the last command's result

### 12. `delete` command

Deletes the tasks at the given indices.

- Syntax: `delete <index>... [/chain]`
    - `<index>...`: a list of indices of the tasks to delete
    - `/chain`: set this flag to delete only tasks from the last command's result

This command returns the deleted tasks as a result set that can be operated on by other tasks by setting flag `/chain`.

### 13. `delete-all` command

Deletes all tasks.

- Syntax: `delete-all [/chain]`
    - `/chain`: set this flag to delete only tasks from the last command's result

Be careful when using this command without `/chain` - there is no undo feature, yet.

This command returns the deleted tasks as a result set that can be operated on by other tasks by setting flag `/chain`.

### 14. `delete-matching` command

Deletes all tasks matching the following conditions.

- Syntax: `delete-matching [<description>] [/tags <tag>...] [/from <start>] [/to <end>] [/completed] [/incomplete] [/chain]`
    - `<description>`: the string to search for in task descriptions.
    - `/tags <tag>...`: a list of the names of the tags to search for.
    - `/from <start>`: the earliest time to find task.
    - `/to <end>`: the latest time to find task.
    - `/completed`: set this flag to search for completed tasks only. Takes priority over `/incomplete`.
    - `/incomplete`: set this flag to search for incomplete tasks only. Is set by default.
    - `/chain`: set this flag to delete only tasks from the last command's result

Equivalent to running `find...` with the above arguments, then running `delete-all`.

This command returns the deleted tasks as a result set that can be operated on by other tasks by setting flag `/chain`.

### 15. `mark` command

Marks the tasks at the given indices as completed.

- Syntax: `mark <index>... [/chain]`
    - `<index>...`: a list of indices of the tasks to delete
    - `/chain`: set this flag to mark only tasks from the last command's result

This command returns the marked tasks as a result set that can be operated on by other tasks by setting flag `/chain`.

### 16. `mark-all` command

Marks all tasks as completed.

- Syntax: `mark-all [/chain]`
    - `/chain`: set this flag to mark only tasks from the last command's result

Be careful when using this command without `/chain` - there is no undo feature, yet.

This command returns the marked tasks as a result set that can be operated on by other tasks by setting flag `/chain`.

### 17. `mark-matching` command

Marks all tasks matching the following conditions as completed.

- Syntax: `mark-matching [<description>] [/tags <tag>...] [/from <start>] [/to <end>] [/completed] [/incomplete] [/chain]`
    - `<description>`: the string to search for in task descriptions.
    - `/tags <tag>...`: a list of the names of the tags to search for.
    - `/from <start>`: the earliest time to find task.
    - `/to <end>`: the latest time to find task.
    - `/completed`: set this flag to search for completed tasks only. Takes priority over `/incomplete`.
    - `/incomplete`: set this flag to search for incomplete tasks only. Is set by default.
    - `/chain`: set this flag to mark only tasks from the last command's result

Equivalent to running `find...` with the above arguments, then running `mark-all`.

This command returns the marked tasks as a result set that can be operated on by other tasks by setting flag `/chain`.

### 18. `unmark` command

Marks the tasks at the given indices as incomplete.

- Syntax: `unmark <index>... [/chain]`
    - `<index>...`: a list of indices of the tasks to delete
    - `/chain`: set this flag to mark only tasks from the last command's result

This command returns the unmarked tasks as a result set that can be operated on by other tasks by setting flag `/chain`.

### 19. `unmark-all` command

Marks all tasks as incomplete.

- Syntax: `unmark-all [/chain]`
    - `/chain`: set this flag to mark only tasks from the last command's result

Be careful when using this command without `/chain` - there is no undo feature, yet.

This command returns the unmarked tasks as a result set that can be operated on by other tasks by setting flag `/chain`.

### 20. `unmark-matching` command

Marks all tasks matching the following conditions as incomplete.

- Syntax: `unmark-matching [<description>] [/tags <tag>...] [/from <start>] [/to <end>] [/completed] [/incomplete] [/chain]`
    - `<description>`: the string to search for in task descriptions.
    - `/tags <tag>...`: a list of the names of the tags to search for.
    - `/from <start>`: the earliest time to find task.
    - `/to <end>`: the latest time to find task.
    - `/completed`: set this flag to search for completed tasks only. Takes priority over `/incomplete`.
    - `/incomplete`: set this flag to search for incomplete tasks only. Is set by default.
    - `/chain`: set this flag to mark only tasks from the last command's result

Equivalent to running `find...` with the above arguments, then running `unmark-all`.

This command returns the unmarked tasks as a result set that can be operated on by other tasks by setting flag `/chain`.
