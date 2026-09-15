# Tianyi User Guide

Tianyi is a friendly desktop chatbot that helps you keep track of todos, deadlines, and events using simple typed
commands.

![Tianyi product screenshot](Ui.png)

## Quick start

1. Ensure that Java 25 is installed on your computer.
2. Download `Tianyi.jar` from the [latest release](https://github.com/HXY070103/ip/releases).
3. Place the JAR file in the folder where you want Tianyi to store its data.
4. Open a terminal in that folder and run:

   ```bash
   java -jar Tianyi.jar
   ```

5. Type a command in the input box and press Enter, or click **Send**.

You can enter `help` at any time to see the commands available in Tianyi.

## Command format

- Words in `UPPER_CASE` are values that you need to provide.
- Items in square brackets are optional. For example, `[TIME]` means that a time may be omitted.
- Dates use the `d-M-yyyy` format, such as `2-12-2019`.
- Times use the 24-hour `HH:mm` format, such as `18:00`.
- Command words are not case-sensitive, but search keywords are case-sensitive.
- Task descriptions cannot contain the `|` character.

Tasks are displayed with the following symbols:

| Symbol | Meaning |
|---|---|
| `[T]` | Todo |
| `[D]` | Deadline |
| `[E]` | Event |
| `[ ]` | Not completed |
| `[X]` | Completed |

## Features

### Viewing help: `help`

Shows every command together with a short description and an example.

Example: `help`

### Adding a todo: `todo`

Adds a task that does not have a date or time.

Format: `todo DESCRIPTION`

Example: `todo borrow book`

Tianyi adds `borrow book` as an incomplete todo.

### Adding a deadline: `deadline`

Adds a task that must be completed by a particular date. The time is optional.

Format: `deadline DESCRIPTION /by DATE [TIME]`

Examples:

- `deadline return book /by 2-12-2019`
- `deadline submit report /by 2-12-2019 18:00`

### Adding an event: `event`

Adds an event with a start and an end. Each time is optional when the start and end dates are different.

Format: `event DESCRIPTION /from DATE [TIME] /to DATE [TIME]`

Examples:

- `event holiday /from 2-12-2019 /to 3-12-2019`
- `event meeting /from 2-12-2019 14:00 /to 2-12-2019 16:00`

The end must be later than the start. For an event that starts and ends on the same day, include both times.

### Listing tasks: `list`

Shows the tasks and their current task numbers.

- Enter `list` to show every task.
- Enter `list DATE` to show deadlines that are still due on that date and events taking place on that date. Todos are
  not included in a date-filtered list.

Examples:

- `list`
- `list 2-12-2019`

Use the displayed task numbers with `mark`, `unmark`, and `delete`.

### Finding tasks: `find`

Finds tasks whose descriptions contain the given keyword. Completed and incomplete tasks are both included.

Format: `find KEYWORD`

Example: `find book`

The search is case-sensitive. For example, `find book` does not match a description containing only `Book`.

### Marking a task as completed: `mark`

Marks the task with the given task number as completed.

Format: `mark TASK_NUMBER`

Example: `mark 1`

### Marking a task as incomplete: `unmark`

Marks the task with the given task number as not completed.

Format: `unmark TASK_NUMBER`

Example: `unmark 1`

### Deleting a task: `delete`

Permanently removes the task with the given task number. The remaining tasks are renumbered automatically.

Format: `delete TASK_NUMBER`

Example: `delete 1`

### Exiting Tianyi: `bye`

Closes Tianyi after displaying a farewell message.

Example: `bye`

## Saving data

Tianyi saves your tasks automatically whenever you add, mark, unmark, or delete a task. The data is stored in
`Data/tianyi.txt`, relative to the folder from which you run Tianyi, and is loaded the next time Tianyi starts.

Avoid editing the data file manually, as invalid data may prevent saved tasks from loading correctly.

## Command summary

| Action | Format | Example |
|---|---|---|
| View help | `help` | `help` |
| Add a todo | `todo DESCRIPTION` | `todo borrow book` |
| Add a deadline | `deadline DESCRIPTION /by DATE [TIME]` | `deadline return book /by 2-12-2019 18:00` |
| Add an event | `event DESCRIPTION /from DATE [TIME] /to DATE [TIME]` | `event meeting /from 2-12-2019 14:00 /to 2-12-2019 16:00` |
| List all tasks | `list` | `list` |
| List dated tasks | `list DATE` | `list 2-12-2019` |
| Find tasks | `find KEYWORD` | `find book` |
| Mark a task | `mark TASK_NUMBER` | `mark 1` |
| Unmark a task | `unmark TASK_NUMBER` | `unmark 1` |
| Delete a task | `delete TASK_NUMBER` | `delete 1` |
| Exit Tianyi | `bye` | `bye` |
