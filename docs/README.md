# Bob User Guide

Bob is a desktop chatbot that helps you keep track of to-dos, deadlines, and events.
Type commands in the chat window to add tasks, find them, track their completion,
and remove tasks you no longer need. Bob saves your task list automatically.

![Bob chat window](docs/Ui.png)

## Getting started

You need **JDK 25** to run Bob. Check your Java version with `java -version`.

From the project folder, launch the chatbot using the Gradle wrapper:

**Windows (PowerShell):**

```powershell
.\gradlew.bat run
```

**macOS or Linux:**

```sh
./gradlew run
```

The first launch needs an internet connection to download build dependencies.
A window titled **Bob** opens with a greeting. Type a command in the text box at
the bottom, then press **Enter** or click **Send**. Send one command at a time.
Scroll through the conversation to review earlier replies.

Try these commands in order:

```text
todo read book
deadline return book /by 2/12/2026 1800
list
mark 1
find book
```

Starting with an empty task list, this adds two tasks, displays both, marks
`read book` as done, and finds both tasks by their descriptions.

## Command format

- Command names and aliases are **lowercase**: use `list`, not `List`.
- Replace placeholders such as `<description>` with your own text; do not type
  the angle brackets.
- Descriptions may contain spaces and must not be empty.
- Keep spaces around `/by`, `/from`, and `/to`, as shown in the examples.
- Task numbers start at **1**. Use the latest `list` output when choosing a task
  to mark, unmark, or delete.

### Dates and times

Use `dd/MM/yyyy` for a date, or `dd/MM/yyyy HHmm` for a date with a time.
Single-digit days and months are accepted. Times use four digits on a 24-hour clock.

| Input | Meaning |
| --- | --- |
| `2/12/2026` | 2 December 2026, without a time |
| `02/12/2026 0900` | 2 December 2026 at 9:00 am |
| `02/12/2026 1800` | 2 December 2026 at 6:00 pm |

Dates such as `31/02/2026`, times such as `2400`, and natural-language dates such
as `tomorrow` are not accepted. Bob displays dates as `Dec 02 2026` and times
as `1800hrs`.

## Adding tasks

### Add a to-do: `todo`

Use a to-do for a task without a date or time.

**Format:** `todo <description>`

**Example:**

```text
todo read book
```

For an initially empty list, Bob replies:

```text
Got it. I've added this task:
[T][ ] read book
Now you have 1 tasks in the list.
```

### Add a deadline: `deadline`

Use a deadline for a task that needs to be completed by a date or time.

**Format:** `deadline <description> /by <date or date-time>`

**Examples:**

```text
deadline return book /by 2/12/2026
deadline submit report /by 3/12/2026 1800
```

Bob adds the task, shows its deadline, and reports the updated task count.
The second example appears as:

```text
[D][ ] submit report (by: Dec 03 2026 1800hrs)
```

### Add an event: `event`

Use an event for an activity with a start and an end.

**Format:** `event <description> /from <date or date-time> /to <date or date-time>`

**Example:**

```text
event project meeting /from 3/12/2026 1400 /to 3/12/2026 1600
```

Bob adds the event and displays:

```text
[E][ ] project meeting (from: Dec 03 2026 1400hrs to: Dec 03 2026 1600hrs)
```

Both endpoints are required. Date-only endpoints are also accepted, for example
`event holiday /from 4/12/2026 /to 6/12/2026`. Check the order yourself: Bob
validates each date and time but does not reject an end earlier than the start.

## Viewing tasks: `list`

**Format:** `list`

Bob displays all tasks in their saved order, including completed tasks. For example:

```text
Here are the tasks in your list:
1.[T][X] read book
2.[D][ ] return book (by: Dec 02 2026 1800hrs)
```

`[T]` means to-do, `[D]` means deadline, and `[E]` means event.
`[ ]` means incomplete and `[X]` means complete. An empty list produces `No tasks yet!`.

## Finding tasks: `find`

**Format:** `find <keyword or phrase>`

**Example:** `find BOOK`

Bob finds descriptions containing the supplied text, ignoring capitalization.
Partial words work too: `find meet` matches `project meeting`. A phrase such as
`find read book` searches for that complete phrase. Results include completed
tasks and keep their original relative order. With no matches, Bob displays
`Here are the matching tasks in your list:` with no tasks beneath it.

**Search results are numbered separately from the full list.** Run `list` before
using `mark`, `unmark`, or `delete`; those commands always use full-list numbers.

## Marking and unmarking tasks

**Formats:** `mark <task_number>` and `unmark <task_number>`

For example, `mark 1` marks the first task in the full list as complete:

```text
I marked this task as done:
[T][X] read book
```

Use `unmark 1` to make it incomplete again. Marking a task does not remove it
or change its number. Both commands save the updated completion status.

## Deleting tasks: `delete`

**Format:** `delete <task_number>`

**Example:** `delete 2`

Bob immediately removes the second task from the full list, displays the removed
task, and reports how many tasks remain. Later tasks are renumbered: the former
third task becomes task 2. Run `list` again before choosing another task.

There is no undo command. To restore a deleted task, add it again.

## Ending a session: `bye`

**Format:** `bye`

Enter `bye` or its alias `b` to close the chat window and exit Bob.
Restart Bob to begin another session; your saved tasks are loaded again.
In console mode, Bob prints `Bye. Hope to see you again soon!` before exiting.

## Command aliases

Aliases accept exactly the same arguments as their full command names.

| Command | Alias | Example |
| --- | --- | --- |
| `todo` | `t` | `t read book` |
| `deadline` | `d` | `d return book /by 2/12/2026` |
| `event` | `e` | `e meeting /from 2/12/2026 1400 /to 2/12/2026 1600` |
| `list` | `l` | `l` |
| `find` | `f` | `f book` |
| `mark` | `m` | `m 1` |
| `unmark` | `u` | `u 1` |
| `delete` | `del` | `del 1` |
| `bye` | `b` | `b` |

## Saving and loading tasks

Bob automatically saves after adding, deleting, marking, or unmarking a task.
When launched from the project folder, tasks are stored in `data/bob.txt`.
Bob creates this file and its folder when needed, and reads it on the next launch.
Chat history is not saved.

Launch from the same folder each time so Bob uses the same data file. To back up
your tasks, close Bob and copy `data/bob.txt` somewhere safe. Avoid manually editing
this file or using ` | ` in task descriptions, because that sequence separates
fields in the saved data.

## Fixing input and storage errors

Error replies appear in italics in the chat window. Correct the command and send
it again.

| Message | What to do |
| --- | --- |
| `Invalid command format. Use: ...` | Follow the format shown, including the description and required separators. |
| `I'm sorry, I don't understand that command.` | Use a lowercase command or alias from the table. `list` and `bye` take no arguments. |
| `Invalid task number.` | Run `list` and choose a number from 1 to the number of tasks shown. |
| `Invalid task number format.` | Enter a whole number, such as `mark 1`. |
| `Invalid date or time. Use ...` | Use a real calendar date and, if needed, a four-digit time from `0000` to `2359`. |
| `Warning: could not save tasks to the data file.` | Check that the data folder is writable and `bob.txt` is a file, not a folder. |

If saving fails, changes may remain visible during the session but will not survive
a restart unless a later save succeeds. After fixing the storage location, make a
task change to save the list again. Malformed saved records are skipped during
loading, so keep a backup if you need to recover missing tasks.
