# Henry

**Henry** is a friendly desktop chatbot that helps you create, track, search, and complete tasks
using simple text commands.

## User Guide

### Quick start

1. Ensure that Java 25 is installed.
2. Download `henry.jar` from the
   [latest release](https://github.com/rondth/ip/releases/latest) into an empty folder.
3. Open a terminal in that folder.
4. Start Henry:

   ```shell
   java -jar henry.jar
   ```

5. Type a command into the message box.
6. Press <kbd>Enter</kbd> or click **Send**.

> [!IMPORTANT]
> Commands and their aliases must be entered in lowercase.

### Understanding your task list

Henry supports three types of tasks:

| Symbol | Task type | Meaning |
| --- | --- | --- |
| `[T]` | Todo | A task without a date or time |
| `[D]` | Deadline | A task that must be completed by a certain date or time |
| `[E]` | Event | A task that takes place between a start and end time |

The completion symbol shows whether a task is finished:

- `[ ]` means the task is not completed.
- `[X]` means the task is completed.

## Features

### Adding a todo: `todo`

Adds a task without a date or time.

**Format:** `todo DESCRIPTION`

**Example:**

```text
todo borrow a book
```

Henry adds the following task:

```text
[T][ ] borrow a book
```

Short form: `t borrow a book`

### Adding a deadline: `deadline`

Adds a task that must be completed by a particular date or time.

**Format:** `deadline DESCRIPTION /by DATE`

Henry accepts these date formats:

- `d/M/yyyy HHmm`, such as `2/12/2019 1800`
- `yyyy-MM-dd`, such as `2019-12-02`

A deadline without a time is set to the start of that day.

**Example:**

```text
deadline submit report /by 2/12/2019 1800
```

Henry displays:

```text
[D][ ] submit report (by: Dec 2 2019 6:00 PM)
```

Short form: `d submit report /by 2/12/2019 1800`

### Adding an event: `event`

Adds a task with a start and end time.

**Format:** `event DESCRIPTION /from START_DATE /to END_DATE`

Start and end dates use the same formats as deadlines. The ending date and time must be after the
starting date and time.

**Example:**

```text
event project meeting /from 2/12/2019 1400 /to 2/12/2019 1600
```

Henry displays:

```text
[E][ ] project meeting (from: Dec 2 2019 2:00 PM to: Dec 2 2019 4:00 PM)
```

Short form: `e project meeting /from 2/12/2019 1400 /to 2/12/2019 1600`

### Viewing all tasks: `list`

Shows every task and its current task number.

**Format:** `list`

**Example:**

```text
list
```

Henry displays:

```text
1. [T][ ] borrow a book
2. [D][X] submit report (by: Dec 2 2019 6:00 PM)
```

Short form: `l`

### Finding tasks: `find`

Finds tasks whose descriptions contain the given keyword.

**Format:** `find KEYWORD`

**Example:**

```text
find book
```

Short form: `f book`

> [!NOTE]
> Searching is case-sensitive. Search results are numbered for display, so use `list` to check a
> task's current number before marking, unmarking, or deleting it.

### Marking a task as completed: `mark`

Marks a task as completed using its number from `list`.

**Format:** `mark TASK_NUMBER`

**Example:**

```text
mark 2
```

Short form: `m 2`

### Marking a task as not completed: `unmark`

Returns a completed task to the not-completed state.

**Format:** `unmark TASK_NUMBER`

**Example:**

```text
unmark 2
```

Short form: `u 2`

### Deleting a task: `delete`

Permanently removes a task using its number from `list`.

**Format:** `delete TASK_NUMBER`

**Example:**

```text
delete 1
```

The remaining tasks are renumbered automatically.

Short form: `del 1`

### Viewing available commands: `help`

Shows a summary of every command Henry supports.

**Format:** `help`

### Saying goodbye: `bye`

Displays Henry's farewell message.

**Format:** `bye`

Short form: `b`

When using the desktop interface, close the application window after you are finished.

### Saving data

Henry automatically saves your task list after you add, mark, unmark, or delete a task.

Saved tasks are loaded again the next time Henry starts. By default, they are stored in:

```text
data/henry.txt
```

No manual save command is required.

## Command summary

| Action | Command format | Short form |
| --- | --- | --- |
| Add a todo | `todo DESCRIPTION` | `t DESCRIPTION` |
| Add a deadline | `deadline DESCRIPTION /by DATE` | `d DESCRIPTION /by DATE` |
| Add an event | `event DESCRIPTION /from START_DATE /to END_DATE` | `e DESCRIPTION /from START_DATE /to END_DATE` |
| View all tasks | `list` | `l` |
| Find tasks | `find KEYWORD` | `f KEYWORD` |
| Mark a task | `mark TASK_NUMBER` | `m TASK_NUMBER` |
| Unmark a task | `unmark TASK_NUMBER` | `u TASK_NUMBER` |
| Delete a task | `delete TASK_NUMBER` | `del TASK_NUMBER` |
| View available commands | `help` | None |
| Say goodbye | `bye` | `b` |

## Setting up Henry in IntelliJ IDEA

Prerequisites: JDK 25 and a recent version of IntelliJ IDEA.

1. Open IntelliJ IDEA.
2. Select **Open** and choose the project directory.
3. Configure the project to use **JDK 25**.
4. Set the **Project language level** to **SDK default**.
5. Open `src/main/java/henry/gui/Launcher.java`.
6. Right-click the file and select **Run Launcher.main()**.

Keep `src/main/java` as the root folder for Java source files.

## Creating and running the JAR as a developer

Create the executable JAR from the project root:

```shell
./gradlew shadowJar
```

On Windows, use:

```shell
gradlew.bat shadowJar
```

The generated file is located at:

```text
build/libs/henry.jar
```

Run it using Java 25:

```shell
java -jar build/libs/henry.jar
```
