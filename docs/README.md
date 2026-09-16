# tt.TT User Guide

```text
 _______  _______
|__   __||__   __|
   | |      | |
   | |      | |
   |_|      |_|
```

tt.TT is a desktop task manager with a chatbot-style interface. It helps you keep track of todos, deadlines, and
events using short text commands.

## Features

- Add todos, deadlines, and events.
- View, search, and sort tasks.
- Mark tasks as complete or incomplete.
- Delete individual tasks or clear groups of tasks.
- Save tasks automatically between sessions.
- Explain invalid commands without closing the application.

## Before you start

tt.TT requires Java Development Kit (JDK) 25.

To check your installed Java version, open a terminal and enter:

```text
java -version
```

## Starting the application

### Using the executable JAR

1. Place `tt.jar` in a folder where tt.TT can store its task data.
2. Open a terminal in that folder.
3. Run:

```text
java -jar tt.jar
```

### Running from the project with Gradle

Open a terminal in the project folder and run the following command on Windows:

```text
.\gradlew.bat run
```

On macOS or Linux, run:

```text
./gradlew run
```

A separate tt.TT chatbot window will open. The large TT artwork above is the project's text logo; chatbot replies
are identified by a circular **TT** avatar in the application.

## Using the chatbot

Enter a command in the text box at the bottom of the window. Press `Enter` or select **Send** to submit it.

For example:

```text
todo read a book
```

tt.TT responds with:

```text
Got it. I've added this task:
[T][ ] read a book
```

## Understanding task symbols

| Symbol | Meaning |
|---|---|
| `T` | Todo |
| `D` | Deadline |
| `E` | Event |
| `[ ]` | Incomplete task |
| `[X]` | Completed task |

## Command summary

| Action | Format | Example |
|---|---|---|
| Add a todo | `todo DESCRIPTION` | `todo read a book` |
| Add a deadline | `deadline DESCRIPTION /by DATE` | `deadline submit report /by 2026-09-18` |
| Add an event | `event DESCRIPTION /from START /to END` | `event meeting /from 2pm /to 3pm` |
| View all tasks | `list` | `list` |
| Mark a task | `mark NUMBER` | `mark 2` |
| Unmark a task | `unmark NUMBER` | `unmark 2` |
| Delete a task | `delete NUMBER` | `delete 2` |
| Find tasks | `find KEYWORD` | `find report` |
| Sort tasks | `sort` | `sort` |
| Clear tasks | `clear TYPE` | `clear deadline` |
| End the session | `bye` | `bye` |

Words written in uppercase, such as `DESCRIPTION`, are placeholders that you should replace with your own details.

## Adding a todo

Use `todo` followed by a description:

```text
todo buy groceries
```

## Adding a deadline

Use `deadline`, a description, `/by`, and a date in `yyyy-MM-dd` format:

```text
deadline return library book /by 2026-09-18
```

tt.TT displays the saved deadline in a friendlier format:

```text
[D][ ] return library book (by: Sep 18 2026)
```

The date must be valid. For example, `2026-02-30` is rejected because that date does not exist.

## Adding an event

Use `event`, a description, `/from`, and `/to`:

```text
event project meeting /from Monday 2pm /to Monday 3pm
```

Event start and end values can be written as meaningful text.

## Viewing tasks

Use `list` to display every saved task:

```text
list
```

Each task is assigned a number. Use that number with commands such as `mark`, `unmark`, and `delete`.

## Marking a task

Use `mark` followed by a task number:

```text
mark 2
```

The task's status changes from `[ ]` to `[X]`.

## Unmarking a task

Use `unmark` followed by a task number:

```text
unmark 2
```

The task's status changes from `[X]` to `[ ]`.

## Deleting a task

Use `delete` followed by a task number:

```text
delete 2
```

Task numbers can change after deletion. Use `list` again before entering another number-based command.

## Finding tasks

Use `find` followed by a keyword:

```text
find book
```

tt.TT displays tasks whose descriptions contain the keyword. Matching is case-insensitive.

## Sorting tasks

Use `sort` to arrange all tasks alphabetically by description:

```text
sort
```

Capitalization does not affect the order. The sorted order is saved automatically.

## Clearing tasks

Use `clear` followed by the type of task to remove:

```text
clear todo
clear deadline
clear event
clear all
```

`clear all` permanently removes every saved task.

## Ending the session

Use `bye` to finish the current chat session:

```text
bye
```

The input field and Send button are disabled after this command. Close and reopen tt.TT to begin another session.

## Handling invalid commands

Invalid commands appear as red error messages. The message explains what went wrong and usually includes a valid
example. Common causes include:

- leaving a task description empty;
- entering a task number that is not in the list;
- omitting `/by`, `/from`, or `/to`;
- using an invalid deadline date; or
- adding extra text to commands such as `list`, `sort`, or `bye`.

Correct the command and submit it again. The application remains open after an input error.

## Saved task data

tt.TT saves tasks automatically in `data/tt.TT.txt`, relative to the folder from which the application was started.
The file is created after the first task is added. Avoid editing it manually while tt.TT is running.
