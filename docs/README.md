# MEKA User Guide

MEKA is a desktop task chatbot that helps you keep track of todos, deadlines,
and events. Type commands to organize your tasks, find them, mark them done,
and archive the active list when you want a clean slate. Your tasks are saved
automatically when storage is available.

## Quick start

1. Install **Java 25**. Run `java -version` in a terminal to check your version.
2. Download the MEKA JAR from the project's
   [Releases page](https://github.com/isaactay9856/ip/releases) and place it in a
   folder where you have write permission.
3. Open a terminal in that folder and run the following command. If your JAR has
   a different filename, substitute that filename.

   ```shell
   java -jar meka.jar
   ```

4. In the MEKA window, type `todo read book` and press **Enter** or click **Send**.
5. Type `list` to see your task. Try `mark 1` to complete it.
6. Type `archive all` to move the active list into the archive, or type `bye`
   to exit without archiving.

Always launch MEKA from the same folder so it can find your saved tasks.

## Command basics

- Enter one command at a time. Command words and parameters are lowercase:
  use `todo`, not `TODO`.
- Replace uppercase placeholders such as `DESCRIPTION` and `NUMBER` with your
  own values; do not type the placeholders themselves.
- Leading and trailing spaces are ignored. Repeated spaces and tabs are
  converted to a single space, including inside descriptions.
- Dates use **day/month/year** and four-digit **24-hour time**:
  `2/12/2026 1800` means 2 December 2026 at 6:00 PM.
- Task numbers start at **1**. Run `list` before marking, unmarking, or deleting
  a task to check its current number.

## Features

### Add a todo

Use `todo DESCRIPTION` for a task without a date or time.

```text
todo read book
```

Starting with an empty list, MEKA replies:

```text
Got it. I've added this task:
  [T][ ] read book
Now you have 1 tasks in the list.
```

### Add a deadline

Use `deadline DESCRIPTION /by DATE_TIME` for a task with a due date.

```text
deadline submit report /by 2/12/2026 1800
```

MEKA adds a deadline displayed as:

```text
[D][ ] submit report (by: Dec 02 2026, 6:00 PM)
```

### Add an event

Use `event DESCRIPTION /from START_DATE_TIME /to END_DATE_TIME` for an activity
with a start and end. Put `/from` before `/to`; the end must be after the start.

```text
event project meeting /from 3/12/2026 0900 /to 3/12/2026 1100
```

MEKA adds an event displayed as:

```text
[E][ ] project meeting (from: Dec 03 2026, 9:00 AM to: Dec 03 2026, 11:00 AM)
```

Supply each date parameter only once. Each task needs a description, and each
deadline or event date needs both the date and time. Identical tasks are allowed.

### List tasks

Use `list` to show all tasks in the order they were added. After adding the
three examples above, the list is:

```text
1. [T][ ] read book
2. [D][ ] submit report (by: Dec 02 2026, 6:00 PM)
3. [E][ ] project meeting (from: Dec 03 2026, 9:00 AM to: Dec 03 2026, 11:00 AM)
```

`[T]` means todo, `[D]` means deadline, and `[E]` means event.
`[ ]` means incomplete and `[X]` means complete. An empty list currently produces
an empty reply.

### Mark or unmark a task

Use `mark NUMBER` to mark a task complete:

```text
mark 1
```

For the example list, MEKA replies:

```text
Nice! I've marked this task as done:
  [T][X] read book
```

Use `unmark 1` to make that task incomplete again. Marking an already completed
task or unmarking an incomplete task leaves its status unchanged.

### Find tasks

Use `find KEYWORD` to search descriptions. Matching ignores letter case and
includes partial words: `find BOOK` matches both `read book` and `buy notebook`.
You can also search a phrase, such as `find project meeting`.

```text
find BOOK
```

For the example list, MEKA replies:

```text
Here are the matching tasks in your list:
1. [T][ ] read book
```

If nothing matches, only the heading is shown. Dates and completion status are
not searched.

> Search results have their own numbering. Run `list` and use the full-list
> number for `mark`, `unmark`, and `delete`, even immediately after a search.

### Delete a task

Use `delete NUMBER` to remove a task permanently:

```text
delete 2
```

For the example list, this removes `submit report` and leaves two tasks.
Later tasks move up a number, so the meeting becomes task 2. There is no undo;
you can add a deleted task again if needed.

### Archive all tasks

Use `archive all` to move every active task into `data/meka-archive.txt`
and start again with an empty active task list. This archives incomplete and
completed tasks alike.

Example:

```text
archive all
```

MEKA confirms how many tasks were archived:

```text
Noted. I've archived all 3 tasks.
Now you have 0 tasks in the list.
```

If the active task list is empty, MEKA responds:

```text
There are no tasks to archive.
```

MEKA appends tasks in their current list order and preserves each task's type,
description, date-time details, and completion state. Existing archive entries
are kept, so running `archive all` again adds another batch below them.

Archived tasks remain in `data/meka-archive.txt` as a permanent record. They no
longer appear in `list` or `find`, and MEKA does not support viewing, restoring,
or deleting archive entries from within the app. You can inspect or back up the
archive file using your file manager or a text editor.

### Exit

Use `bye` to display a farewell and close MEKA after a short delay. You do not
need a separate save command.

## Saving and recovering tasks

MEKA uses the following files relative to the folder from which you launch it:

| File | Purpose |
| --- | --- |
| `data/meka.txt` | Current active tasks; loaded whenever MEKA starts |
| `data/meka-archive.txt` | Tasks moved by `archive all`; not loaded into the app |

A missing active-task file starts a new empty list. The `data` folder and the
required file are created on the next successful save. The archive file is
created the first time a non-empty list is archived.

To back up your tasks, close MEKA and copy both `data/meka.txt` and
`data/meka-archive.txt` (if present) somewhere safe. Keep
the `data` folder with your working folder when moving to another location.

If the file is unreadable or contains invalid data, MEKA starts with an empty
in-memory list and prevents saving over the original file. The console shows
a startup warning; the GUI currently does not show that warning.

If you see **"I could not save the task list. Your changes are available only
for this session."**, MEKA has disabled storage for the rest of that session.
An add, delete, mark, or unmark change remains in the open app but will not
survive a restart. A failed `archive all` leaves the active tasks in the app. If
the archive was written but the active file could not be cleared, MEKA attempts
to restore the archive file to its previous contents.

Record any tasks you need before closing MEKA, back up the existing data files,
check that the paths are writable files rather than directories, and restore a
known-good backup if needed. Restart MEKA after resolving the issue.

## Troubleshooting

| Problem | What to do |
| --- | --- |
| Command is not understood | Check the lowercase command word and syntax below. `list` and `bye` take no arguments. |
| Description or date/time is required | Supply all placeholders shown in the command format. |
| A number is required, or the task number does not exist | Run `list` and enter a whole number from that list, starting at 1. |
| Date/time is rejected | Use a real date and `d/M/yyyy HHmm`, for example `2/12/2026 1800`, rather than `6pm`. |
| A parameter must be specified only once | Remove the repeated `/by`, `/from`, or `/to` parameter. |
| Event end must be after its start | Enter an end date/time strictly later than the start. |
| Task details contain a reserved delimiter | Remove any pipe character surrounded by spaces from the text. That sequence is reserved for saved data. |
| Tasks seem to be missing | Check that you launched from the usual folder and that its `data/meka.txt` is intact. See recovery guidance above. |
| Archived tasks are not shown by `list` | This is expected. Open `data/meka-archive.txt` directly; archived tasks cannot currently be restored inside MEKA. |
| MEKA does not launch | Check `java -version` reports Java 25 and that the JAR filename and terminal folder are correct. |

Command validation errors leave your existing tasks unchanged. Correct the
command and send it again; GUI errors appear in a red highlighted reply.

## Command summary

| Action | Format |
| --- | --- |
| Add a todo | `todo DESCRIPTION` |
| Add a deadline | `deadline DESCRIPTION /by DATE_TIME` |
| Add an event | `event DESCRIPTION /from START_DATE_TIME /to END_DATE_TIME` |
| List all tasks | `list` |
| Mark complete | `mark NUMBER` |
| Mark incomplete | `unmark NUMBER` |
| Find by description | `find KEYWORD` |
| Delete a task | `delete NUMBER` |
| Archive all active tasks | `archive all` |
| Exit | `bye` |
