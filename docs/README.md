# Walnut User Guide

Walnut is a friendly desktop chatbot for managing your tasks. You can add different types of tasks, view and search your task list, update task status, and remove tasks through the chat window.

![Walnut user interface](docs/Ui.png)

## Setting up and running in IntelliJ

Ensure you are using **JDK 25**. **Mac users need to use** [**this precise distribution of JDK 25**](https://se-education.org/guides/tutorials/javaInstallationMac.html), which comes bundled with JavaFX support.

Prerequisites: JDK 25 and the latest version of IntelliJ IDEA.

1. Open IntelliJ IDEA.
2. Click **Open** and select the project directory.
3. Configure the project to use **JDK 25** and set the project language level to **SDK default**.
4. Locate `src/main/java/walnut/Main.java`.
5. Right-click the file and select **Run `Main.main()`**.

Walnut will open in a desktop window. Type commands into the input box and press **Send** or **Enter**. Commands are not case-sensitive.

Type `/help` at any time to open Walnut's command reference window.

## Adding tasks

### To-do tasks

Use `todo` for a task without a date or time.

```text
todo Read chapter 3
```

Walnut adds the task to the list and assigns it a number.

### Deadline tasks

Use `deadline` with `/by` and a date/time in the format `YYYY-MM-DD HHmm`.

```text
deadline Submit assignment /by 2026-10-05 2359
```

### Event tasks

Use `event` with `/from`, `/to`, and a start and end date/time. The end time must be later than the start time.

```text
event Team meeting /from 2026-09-20 1400 /to 2026-09-20 1500
```

## Viewing and searching tasks

Use `list` to display every task in the task list:

```text
list
```

Use `find` followed by a keyword to display tasks whose descriptions contain that keyword:

```text
find assignment
```

Tasks are numbered in the order in which they appear. Use these numbers with the commands below.

## Updating tasks

Mark a task as completed with `mark`:

```text
mark 1
```

Mark a completed task as incomplete with `unmark`:

```text
unmark 1
```

Remove a task with `remove`:

```text
remove 1
```

Walnut saves changes automatically, so your task list is available the next time you start the application.

## Other commands

| Command | What it does |
| --- | --- |
| `/help` | Opens the command reference window. |
| `bye` | Closes Walnut after ending the session. |

If a command is invalid or a required argument is missing, Walnut displays a message explaining the expected format. Date/time values must use a valid calendar date and the 24-hour `HHmm` format.

**Warning:** Keep the `src/main/java` folder as the root folder for Java files, as this is the default location expected by tools such as Gradle.
