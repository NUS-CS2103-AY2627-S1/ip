# Nicola User Guide

![Nicola's chat interface with task commands and responses](Ui.png)

> Buongiorno. I'm Nicola Francesca. Allow me to organize your affairs.

**Nicola** is a desktop task-management chatbot with a charming, calculating personality inspired by Nicola Francesca. Organize todos, deadlines, and events, search your tasks, and check upcoming deadlines through simple typed commands.

## Getting started

1. Install **JDK 25**.
2. Clone or download this repository and open a terminal in the project folder.
3. On Windows, run `.\gradlew.bat run`. On macOS or Linux, run `./gradlew run`.
4. Type a command into the input box and press **Enter** or click **Send**.

To build a runnable JAR on Windows:

```powershell
.\gradlew.bat shadowJar
java -jar build/libs/nicola.jar
```

## Command conventions

- Use lowercase command words.
- Replace placeholders such as `<description>` with your own text; omit the angle brackets.
- Use dates in `yyyy-MM-dd` format and 24-hour times in `HHmm` format.
- Keep spaces around `/by`, `/from`, and `/to` as shown below.
- Task numbers start at **1**. Use the latest `list` output before marking or deleting tasks.
- `[T]`, `[D]`, and `[E]` mean todo, deadline, and event. `[ ]` means incomplete; `[X]` means complete.

The examples below form a walkthrough starting with an empty task list. Counts and task numbers differ if you already have saved tasks. Displayed month names may vary with your system's language settings.

## Adding deadlines

Add an incomplete task with a due date and an optional time.

Format: `deadline <description> /by <yyyy-MM-dd>` or `deadline <description> /by <yyyy-MM-dd HHmm>`

Example: `deadline submit report /by 2026-09-20 1800`

Expected output:

```text
A deadline, understood. I'll make certain we don't lose sight of it.
  [D][ ] submit report (by: Sep 20 2026 1800)
We now have 1 matters requiring our attention.
```

## Adding todos

Add a task without a date or time.

Format: `todo <description>`

Example: `todo read chapter 3`

Expected output:

```text
Consider it handled. I've added this task to our plans.
  [T][ ] read chapter 3
We now have 2 matters requiring our attention.
```

## Adding events

Add an event with a start and end date-time. Both values require a date and a time.

Format: `event <description> /from <yyyy-MM-dd HHmm> /to <yyyy-MM-dd HHmm>`

Example: `event project meeting /from 2026-09-21 1400 /to 2026-09-21 1500`

Expected output:

```text
I've reserved a place for this event in our schedule.
  [E][ ] project meeting (from: Sep 21 2026 1400 to: Sep 21 2026 1500)
We now have 3 matters requiring our attention.
```

## Listing tasks

Display all tasks, including completed ones, with their task numbers.

Example: `list`

Expected output after the three additions above:

```text
Here are the matters currently on our agenda:
 1.[D][ ] submit report (by: Sep 20 2026 1800)
 2.[T][ ] read chapter 3
 3.[E][ ] project meeting (from: Sep 21 2026 1400 to: Sep 21 2026 1500)
```

## Marking tasks as complete

Mark an existing task as done. It stays in your list with an `[X]` marker.

Format: `mark <task number>`

Example: `mark 2`

Expected output:

```text
Excellent. Another matter settled exactly as planned.
  [T][X] read chapter 3
```

## Marking tasks as incomplete

Undo a completion mark so that the task displays `[ ]` again.

Format: `unmark <task number>`

Example: `unmark 2`

Expected output:

```text
I see, we acted too soon. I've returned it to our agenda.
  [T][ ] read chapter 3
```

## Finding tasks

Find tasks whose descriptions contain your search text. Matching is **case-insensitive** and includes partial words and completed tasks.

Format: `find <search text>`

Example: `find report`

Expected output:

```text
These are the matters matching your inquiry:
 1.[D][ ] submit report (by: Sep 20 2026 1800)
```

Search results are numbered separately from the full task list. **Use `list` to get the correct task number before using `mark`, `unmark`, or `delete`.**

If there are no matches:

```text
How curious. I found nothing matching that description.
```

## Checking reminders

Display incomplete deadlines due **today through seven days from today**, inclusive, using your computer's local date. The check uses dates only, so a deadline earlier today still appears.

Example: `reminders`

If today is September 18, 2026, the example deadline above produces:

```text
A word of caution, these deadlines are approaching:
 1.[D][ ] submit report (by: Sep 20 2026 1800)
```

Completed deadlines, deadlines before today, todos, and events are excluded. Reminders retain the task numbers from `list`. Run this command when you want a reminder; it does not schedule automatic notifications.

If there are no upcoming deadlines:

```text
A word of caution, these deadlines are approaching:
Everything is under control. No deadlines are approaching this week.
```

## Deleting tasks

Remove a task from your list. The remaining tasks are renumbered, and there is no undo command.

Format: `delete <task number>`

Example: `delete 2`

Expected output:

```text
It's gone. You needn't concern yourself with it again.
  [T][ ] read chapter 3
We now have 2 matters requiring our attention.
```

## Correcting errors

Invalid commands and task details produce an **Error:** message in a pink gradient bubble. Correct the input and submit it again.

Example: `todo`

Expected output:

```text
Error: The todo cannot be empty.
```

Provide a description, for example `todo buy groceries`. For task-number errors, run `list` and select an existing number. For date errors, use the formats shown above.

## Saving tasks

Nicola automatically saves tasks to `data/nicola.txt` and loads them when you next start the app. No save command is needed. The path is relative to the folder from which you launch Nicola, so use the same folder each time to load the same tasks.

The list holds up to **100 tasks**. Delete unwanted tasks to make room; completed tasks still count towards this limit.

## Exiting Nicola

Example: `bye`

Nicola replies and closes the GUI after a short delay:

```text
Arrivederci. I'll miss you.
```

## FAQ

**Q: Does marking a task as complete remove it?**

A: No. The task stays in your list with an `[X]` marker. Use `unmark` to make it incomplete again, or `delete` to remove it.

**Q: How do I save my tasks?**

A: You do not have to do anything manually, Nicola automatically saves your tasks and loads them the next time you open the app from the same folder.

**Q: How many tasks can I store?**

A: Nicola supports up to 100 tasks, including completed ones. Delete unwanted tasks to make room.

**Q: Why is a task missing from my reminders?**

A: The `reminders` command only includes incomplete deadlines dated today through seven days from today. It excludes todos, events, completed deadlines, and deadlines before today or beyond that range. Reminders appear when you enter the command; they are not automatic notifications.

**Q: Why did my tasks disappear after moving the JAR?**

A: Nicola looks for `data/nicola.txt` relative to the folder from which you launch it. Launch from your usual folder, or copy your existing `data` folder to the new launch location while Nicola is closed.

## Command Summary

Replace uppercase placeholders with your own values. Use task numbers from the latest `list` output, not the numbered search results.

| Action | Format and examples |
| --- | --- |
| Todo | `todo DESCRIPTION`<br>Example: `todo read a book` |
| Deadline | `deadline DESCRIPTION /by DATE` or `deadline DESCRIPTION /by DATE TIME`<br>Example: `deadline return book /by 2026-09-20`<br>Example with time: `deadline submit report /by 2026-09-20 1800` |
| Event | `event DESCRIPTION /from DATE TIME /to DATE TIME`<br>Example: `event meeting /from 2026-09-21 1400 /to 2026-09-21 1500` |
| List | `list` |
| Mark | `mark INDEX`<br>Example: `mark 2` |
| Unmark | `unmark INDEX`<br>Example: `unmark 2` |
| Delete | `delete INDEX`<br>Example: `delete 3` |
| Find | `find KEYWORD`<br>Example: `find book` |
| Reminders | `reminders` |
| Bye | `bye` |

Dates use `yyyy-MM-dd`; times use the 24-hour `HHmm` format. Events require both dates and times.
