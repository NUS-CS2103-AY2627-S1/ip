# Harold User Guide

Harold is a French bulldog-themed task manager that helps you record, find,
complete, and remove tasks through a simple chat interface. Your tasks are
saved automatically, so they will still be available the next time Harold
starts.

![Harold's graphical user interface](Ui.png)

## Quick start

1. Ensure that [Java 25](https://nus-cs2103-ay2627-s1.github.io/website/admin/programmingLanguages.html)
   is installed.
2. Place `harold.jar` in an empty folder.
3. Open a terminal in that folder and run:

   ```shell
   java -jar harold.jar
   ```

4. Type a command into the field at the bottom of the window.
5. Press <kbd>Enter</kbd> or select **Send**.

When running Harold from its source code instead, use `./gradlew run` on macOS
or Linux, or `gradlew.bat run` on Windows.

> **Tip:** Text inside angle brackets, such as `<description>`, is a placeholder.
> Replace it with your own information and do not type the angle brackets.

## Command summary

| Action | Command |
|---|---|
| Add a todo | `todo <description>` |
| Add a deadline | `deadline <description> /by <yyyy-MM-dd>` |
| Add an event | `event <description> /from <yyyy-MM-dd> /to <yyyy-MM-dd>` |
| View all tasks | `list` |
| Mark a task complete | `mark <task number>` |
| Mark a task incomplete | `unmark <task number>` |
| Delete a task | `delete <task number>` |
| Find tasks | `find <keyword>` |
| Exit Harold | `bye` |

## Features

### Adding a todo

Use a todo for a task without a specific date.

**Format:** `todo <description>`

**Example:**

```text
todo buy dog treats
```

### Adding a deadline

Use a deadline for a task that must be completed by a particular date. Enter
the date in `yyyy-MM-dd` format.

**Format:** `deadline <description> /by <yyyy-MM-dd>`

**Example:**

```text
deadline submit report /by 2026-09-30
```

Harold displays the date in a friendlier format, such as `Sep 30 2026`.

### Adding an event

Use an event for an activity with a start date and an end date.

**Format:** `event <description> /from <yyyy-MM-dd> /to <yyyy-MM-dd>`

**Example:**

```text
event reading week /from 2026-09-26 /to 2026-10-04
```

### Viewing tasks

Use `list` to display every task and its number.

```text
list
```

The symbols beside each task show its type and status:

- `[T]` — todo
- `[D]` — deadline
- `[E]` — event
- `[X]` — completed
- `[ ]` — incomplete

### Marking and unmarking tasks

Use the number shown by `list` to update a task's completion status.

```text
mark 2
unmark 2
```

### Deleting a task

Use the number shown by `list` to remove a task.

**Format:** `delete <task number>`

**Example:**

```text
delete 2
```

Task numbers can change after a deletion, so use `list` again before making
another numbered change.

### Finding tasks

Use `find` to search task descriptions for a keyword.

**Format:** `find <keyword>`

**Example:**

```text
find book
```

Harold lists exact matches first. If there is no exact match, Harold can suggest
up to three tasks with similar text, which is useful for small spelling errors.

### Exiting

Use `bye` to end the conversation and close Harold.

```text
bye
```

## Saving data

Harold saves the task list automatically after an add, mark, unmark, or delete
command. The data is stored in `data/harold.txt`, relative to the folder from
which Harold was launched.

Avoid editing this file manually. Deleting it resets Harold to an empty task
list the next time the application starts.

## Troubleshooting

- **Harold does not start:** Run `java -version` and confirm that Java 25 is
  active. Start the application from a terminal so any error message remains
  visible.
- **A date is rejected:** Use a real calendar date in `yyyy-MM-dd` format, such
  as `2026-09-30`.
- **A numbered command fails:** Run `list` and use one of the task numbers shown.
- **A command is not recognized:** Check the spelling and follow the formats in
  the [command summary](#command-summary).
