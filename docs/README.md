# Mira User Guide

Mira is a small desktop task companion. Type short commands to remember todos,
deadlines and events, find a task, or mark something done. Your tasks stay on
your computer and are saved after every successful change.

![Mira showing todos, deadlines and events](Ui.png)

## Quick start

1. Install **Java 25**. For CS2103/T, use the distribution specified in the
   [course Java setup instructions](https://nus-cs2103-ay2627-s1.github.io/website/admin/programmingLanguages.html).
   macOS users should use the prescribed **Azul Zulu JDK+FX** build for their
   processor. Mira's bundled macOS native libraries target Apple silicon;
   Intel Mac compatibility has not been verified.
2. Download **mira.jar** from the [latest release](https://github.com/sonlexuan3000/ip/releases/latest).
3. Put it in a folder you can write to, then open a terminal in that folder.
4. Run `java -version` and check it reports Java 25. Then run:

   ```shell
   java -jar mira.jar
   ```

5. Type `todo read a book` into the command box and press **Enter** or **Send**.
   Type `list` to see your tasks.

Keep launching from the same folder: Mira uses `data/mira.txt` relative to
your terminal's current directory. Changing folders gives you a different
task list. No account or network connection is needed after downloading.

## Command basics

- Type **one command per line**. Commands and delimiters ignore letter case:
  `TODO` and `todo` work alike.
- Replace UPPERCASE placeholders below with your own text; do not type them.
  Leading and trailing spaces are ignored.
- Descriptions can contain multiple words and Unicode characters.
  Duplicate tasks are allowed and occupy separate numbered entries.
- Task numbers start at **1**. Use the number shown by `list` or `find` for
  `mark`, `unmark`, and `delete`. `find` keeps the full-list numbers.
  Deleting shifts later numbers down; use `list` again when unsure.
- Invalid commands produce a red-tinted message beginning with `OOPS!!!`.
  Correct the input and try again. Invalid commands do not change saved tasks.

## Add a todo

**Format:** `todo DESCRIPTION` (short form: `t DESCRIPTION`)

**Example:** `todo read a book`

```text
Got it. I've added this task:
  [T][ ] read a book
Now you have 1 task in the list.
```

The description is required. Todos have no date or time.

## Add a deadline

**Format:** `deadline DESCRIPTION /by YYYY-MM-DD`

**Example:** `deadline submit report /by 2026-09-18`

```text
Got it. I've added this task:
  [D][ ] submit report (by: Sep 18 2026)
Now you have 2 tasks in the list.
```

Use an ISO calendar date, with a four-digit year and two-digit month/day.
Mira rejects impossible dates such as `2026-02-29`, and text such as `Friday`.
Past dates are allowed. Supply exactly one `/by` delimiter and no time.
The date is displayed in a readable English format.

## Add an event

**Format:** `event DESCRIPTION /from START /to END`

**Example:** `event project meeting /from Fri 2pm /to Fri 3pm`

```text
Got it. I've added this task:
  [E][ ] project meeting (from: Fri 2pm to: Fri 3pm)
Now you have 3 tasks in the list.
```

Description, start, and end must be non-empty. Supply one `/from` and one
`/to`, in that order. **START and END are text labels**: Mira stores what
you type after trimming outer spaces. It does not validate calendar dates,
compare start/end times, detect overlaps, or notify you. Include a date
when the event is not today. Delimiter tokens cannot also be used as text
inside the event's fields.

## List all tasks

**Format:** `list`

```text
Here are the tasks in your list:
1. [T][ ] read a book
2. [D][ ] submit report (by: Sep 18 2026)
3. [E][ ] project meeting (from: Fri 2pm to: Fri 3pm)
```

`[T]`, `[D]` and `[E]` identify todos, deadlines and events. `[ ]` means
not done; `[X]` means done. Tasks remain in insertion order. An empty list
shows `Your task list is empty.` The command takes no extra arguments.

## Find tasks

**Format:** `find KEYWORD OR PHRASE`

**Example:** `find REPORT`

```text
Here are the matching tasks in your list:
2. [D][ ] submit report (by: Sep 18 2026)
```

Search matches a literal substring in the **description**, ignoring case.
A multiword query is one phrase, not a set of alternatives. Date/time fields
and completion status are not searched. The query is required. No matches
gives `No matching tasks found.` The result above is still task **2**, so
`mark 2` acts on the report. Search does not change the list.

## Mark or unmark a task

**Formats:** `mark NUMBER` and `unmark NUMBER`

**Example:** `mark 2`

```text
Nice! I've marked this task as done:
  [D][X] submit report (by: Sep 18 2026)
```

`unmark 2` changes `[X]` back to `[ ]`. Marking an already completed task,
or unmarking an incomplete one, leaves it in the requested state. The number
must be a positive whole number within the list; zero, negative numbers,
decimals, missing numbers and extra arguments are rejected.

## Delete a task

**Format:** `delete NUMBER`

**Example:** `delete 1`

```text
Noted. I've removed this task:
  [T][ ] read a book
Now you have 2 tasks in the list.
```

Deletion is immediate and there is **no undo**. Later numbers shift down.
Check `list` or the full-list number shown by `find` before deleting.
Deleting does not remove other tasks with similar descriptions.

## Exit

**Format:** `bye`

Mira displays `Bye. Hope to see you again soon!` and closes after a short
pause. Closing the window also exits. Every successful add, mark, unmark,
or delete was already saved, so the next launch restores that state.
The conversation is not saved; type `list` after restarting.

## Your data and troubleshooting

- **Backup:** while Mira is closed, copy `data/mira.txt` somewhere safe.
  Restore by replacing that file while Mira is closed. Keep one running
  instance per data folder; concurrent instances are not supported.
- **Format:** each line contains a task type, completion status and
  Base64-encoded text fields separated by ` | `. Do not edit encoded fields
  as plain descriptions. Prefer commands or restoring a backup.
- **Cannot save:** Mira reports that nothing changed. Check folder write
  permission and free space, then retry. A failed save retains the previous
  in-memory list; temporary-file replacement protects the existing file
  against a failed write.
- **Invalid/unreadable file at startup:** Mira shows an error and preserves
  the file. Keep a backup before repairing or moving it aside. If moved
  aside, restarting creates a new empty list; the original tasks remain
  only in your backup until you restore a valid file.
- **Different/empty list after moving the JAR:** check the terminal's working
  folder. The original data does not move with the JAR automatically.
- **Java version error:** run `java -version` in the same terminal and select
  Java 25. Do not double-click the JAR for course testing.
- **JavaFX/native-library error:** check the OS/processor and Java build
  against the course instructions. Include the terminal error, Java version
  and OS when reporting a problem.
- **Input error:** follow the syntax above. Unknown commands, missing fields,
  malformed delimiters, invalid dates and nonexistent indexes are rejected
  with a message; you can continue using Mira.

## Command summary

| Action | Command |
| --- | --- |
| Todo | `todo DESCRIPTION` or `t DESCRIPTION` |
| Deadline | `deadline DESCRIPTION /by YYYY-MM-DD` |
| Event | `event DESCRIPTION /from START /to END` |
| List | `list` |
| Find | `find KEYWORD OR PHRASE` |
| Complete | `mark NUMBER` |
| Reopen | `unmark NUMBER` |
| Delete | `delete NUMBER` |
| Exit | `bye` |

[Source code and issue tracker](https://github.com/sonlexuan3000/ip)
