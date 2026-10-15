# Duchess User Guide

Duchess is a desktop task manager for organising your obligations. She supports
to-dos, deadlines, events, recurring tasks, searching, completion tracking,
and automatic data storage.

## Quick start

1. Ensure that Java 25 or later is installed.
2. Open the project in IntelliJ.
3. Run the application using the Gradle run task or the
   duchess.ui.gui.controller.Launcher class.
4. Enter a command in the command box and press Enter or click Send.

For example:

    todo buy groceries

## Features

### Viewing the agenda: list

Shows all tasks currently on the agenda.

    list

### Adding a to-do: todo

Format:

    todo DESCRIPTION

Example:

    todo buy groceries

### Adding a deadline: deadline

Format:

    deadline DESCRIPTION /by yyyy-MM-dd

Example:

    deadline submit report /by 2026-12-10

### Adding an event: event

Format:

    event DESCRIPTION /from yyyy-MM-dd /to yyyy-MM-dd

The start date must come before the end date.

### Adding a recurring task: recurring

Format:

    recurring DESCRIPTION /by yyyy-MM-dd /repeat N days

When a recurring task is completed, Duchess automatically adds its next
occurrence.

### Managing tasks

    mark INDEX
    unmark INDEX
    delete INDEX

Task numbers are shown by the list command.

### Finding tasks: find

Finds tasks containing all supplied keywords.

    find KEYWORDS

Example:

    find grocery shopping

### Finding an exact phrase: find -e

Finds tasks containing the supplied phrase as one continuous string.

    find -e PHRASE

Example:

    find -e grocery shopping

### Exiting Duchess: bye

    bye

## Saving the data

Duchess automatically saves changes to data/duchess.txt and restores them
when the application is launched again. Duplicate tasks are not added.

## Input rules

- Dates must use the yyyy-MM-dd format.
- Task numbers must be valid integers.
- Task descriptions cannot be empty.
- Recurrences must be specified in days.
- The pipe character (|) is not allowed in commands.

## FAQ

### How do I find the number of a task?

Use the list command. Each task is displayed with an index.

### What happens when I complete a recurring task?

Duchess marks the current occurrence as complete and adds the next occurrence.

## Command summary

| Action | Format | Example |
| --- | --- | --- |
| View agenda | list | list |
| Add to-do | todo DESCRIPTION | todo buy groceries |
| Add deadline | deadline DESCRIPTION /by DATE | deadline submit report /by 2026-12-10 |
| Add event | event DESCRIPTION /from DATE /to DATE | event attend conference /from 2026-12-10 /to 2026-12-12 |
| Add recurring task | recurring DESCRIPTION /by DATE /repeat N days | recurring exercise /by 2026-12-10 /repeat 2 days |
| Complete task | mark INDEX | mark 1 |
| Unmark task | unmark INDEX | unmark 1 |
| Delete task | delete INDEX | delete 1 |
| Find tasks | find KEYWORDS | find grocery shopping |
| Find exact phrase | find -e PHRASE | find -e grocery shopping |
| Exit | bye | bye |

## Acknowledgements

### Images

Duchess Image: Ernest Khalimov, as depicted in Sleek'N'Tears by Krista Sudmalis

User Image: Markiplier Soyjak, as depicted in
[Wikipedia](https://en.wikipedia.org/wiki/File:Soyjak_examples.png)

### Code Completion

Done in collaboration with Codex, using GPT-5.6 Terra and GPT-5.6 Luna.

### GUI

Heavily inspired by the
[SE Education JavaFX Tutorial](https://se-education.org/guides/tutorials/javaFx.html)
