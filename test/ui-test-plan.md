# Text UI test plan

Compile with Java 25 and run the console entry point `bot67.Bot67` (the packaged JAR starts the JavaFX GUI).
Run each numbered test in a separate, empty working directory, applying that test's setup before sending commands.
This prevents saved tasks from an earlier test changing task numbers or expected counts. Each test ends with `bye`
so the session terminates cleanly. Check the expected output checkpoints in order; English month names assume an
English locale. Use `-Duser.language=en -Duser.country=US` when running the console tests for consistent date output.

The pass notes below record earlier runs, not a guarantee that the current checkout passes. Record new run results
separately. The graphical checks require the JavaFX application and are not covered by console checkpoints alone.

## Test 1: Add and list all Level-4 task types

Aim: Verify that ToDos, Deadlines, and Events are added and listed with the correct type and date information.

Input commands:

```text
todo borrow book
deadline return book /by Sunday
event project meeting /from Mon 2pm /to 4pm
list
bye
```

Expected output checkpoints, in order:

```text
1.[T][ ] borrow book
2.[D][ ] return book (by: Sunday)
3.[E][ ] project meeting (from: Mon 2pm to: 4pm)
```

Console record: passed. The three add confirmations and the final `list` output matched the checkpoints above.

## Test 2: Mark and unmark typed tasks

Aim: Verify that marking and unmarking work for typed tasks without losing their type or date information.

Input commands:

```text
todo borrow book
deadline return book /by Sunday
event project meeting /from Mon 2pm /to 4pm
mark 2
unmark 2
list
bye
```

Expected output checkpoints, in order:

```text
1.[T][ ] borrow book
2.[D][ ] return book (by: Sunday)
3.[E][ ] project meeting (from: Mon 2pm to: 4pm)
```

Console record: passed. The `mark 2` and `unmark 2` confirmations appeared, and the final `list` showed task 2 as `[D][ ]` with its deadline preserved.

## Test 3: Required Level-5 errors

Aim: Verify that empty todos and unknown commands produce errors without terminating the application.

Input commands:

```text
todo
blah
todo valid task
list
bye
```

Expected output checkpoints, in order:

```text
SIX SEVEN! A todo description cannot be empty.
SIX SEVEN! I do not recognize that command.
1.[T][ ] valid task
```

## Test 4: Malformed commands and invalid task numbers

Aim: Verify that malformed Level-4 commands and invalid task numbers are handled safely.

Input commands:

```text
deadline
event meeting
mark abc
unmark 0
todo valid task
list
bye
```

Expected output checkpoints, in order:

```text
SIX SEVEN! Use: deadline <description> /by <date or time>.
SIX SEVEN! Use: event <description> /from <start> /to <end>.
SIX SEVEN! Task number must be a whole number.
SIX SEVEN! Task number must be a positive whole number.
1.[T][ ] valid task
```

## Test 5: Delete a task and renumber the list

Aim: Verify that deleting a task removes the correct typed task and shifts later tasks forward.

Input commands:

```text
todo borrow book
deadline return book /by Sunday
event project meeting /from Mon 2pm /to 4pm
delete 2
list
bye
```

Expected output checkpoints, in order:

```text
Six seven. Making room! I've removed this task:
  [D][ ] return book (by: Sunday)
Now you have 2 tasks in the list.
1.[T][ ] borrow book
2.[E][ ] project meeting (from: Mon 2pm to: 4pm)
```

## Test 6: Invalid delete commands

Aim: Verify that malformed and out-of-range delete commands do not terminate the application.

Input commands:

```text
delete
delete abc
delete 0
todo valid task
delete 2
list
bye
```

Expected output checkpoints, in order:

```text
SIX SEVEN! Use: delete <task number>.
SIX SEVEN! Task number must be a whole number.
SIX SEVEN! Task number must be a positive whole number.
SIX SEVEN! Task number is out of range.
1.[T][ ] valid task
```

## Test 7: Delete a completed task

Aim: Verify that deleting a completed typed task reports its completed status and leaves the other task unchanged.

Input commands:

```text
todo read book
event project meeting /from Mon 2pm /to 4pm
mark 2
delete 2
list
bye
```

Expected output checkpoints, in order:

```text
Six seven. Making room! I've removed this task:
  [E][X] project meeting (from: Mon 2pm to: 4pm)
Now you have 1 task in the list.
1.[T][ ] read book
```

## Test 8: Save tasks after a change

Aim: Verify that adding a task still completes normally while the task is written to `data/bot67.txt`.

Input commands:

```text
todo read book
bye
```

Expected output checkpoints, in order:

```text
Six seven! On it. I've added this task:
  [T][ ] read book
You have 1 task in the list. 67!
```

Console record: passed. The application created `data/bot67.txt` with the saved task.

## Test 9: Parse and display Level-8 dates and times

Aim: Verify that ISO dates and date-times are parsed into date/time values, displayed in a user-friendly format, and saved in a reloadable format.

Input commands:

```text
deadline submit report /by 2026-10-15
event project meeting /from 2026-10-15T14:00 /to 2026-10-15T16:30
list
bye
```

Expected output checkpoints, in order:

```text
1.[D][ ] submit report (by: Oct 15 2026)
2.[E][ ] project meeting (from: Oct 15 2026 14:00 to: Oct 15 2026 16:30)
```

## Test 10: Find tasks by keyword

Aim: Verify that `find` displays matching tasks with their original list numbers and reports no matching tasks without crashing.

Input commands:

```text
todo read book
deadline return book /by Sunday
event project meeting /from Mon 2pm /to 4pm
find book
find dinner
bye
```

Expected output checkpoints, in order:

```text
Six seven! Found them. Here are the matching tasks in your list:
1.[T][ ] read book
2.[D][ ] return book (by: Sunday)
Six seven... no matching tasks this time. Try another keyword!
```

## Test 11: Sort tasks alphabetically

Aim: Verify that `sort` orders mixed task types by description without losing their status or date information.
The sort heading must be followed immediately by the task rows, without the `list` introduction.

Input commands:

```text
todo zebra task
deadline Apple task /by 2026-10-15
event banana task /from Mon 2pm /to 4pm
mark 3
sort
bye
```

Expected output checkpoints, in order:

```text
Six seven! I've sorted your tasks alphabetically. Even 67 likes a little order:
1.[D][ ] Apple task (by: Oct 15 2026)
2.[E][X] banana task (from: Mon 2pm to: 4pm)
3.[T][ ] zebra task
```

## Test 12: Friendly empty lists and recovery

Aim: Verify the 67 personality in the greeting, empty states, errors, task feedback, and farewell.

Input commands:

```text
list
sort
find book
todo
todo read book
mark 1
unmark 1
delete 1
list
bye
```

Expected output checkpoints, in order:

```text
Hello! I'm Bot67. Six seven! Your tasks, my favourite topic after 67.
Six seven! No tasks in the list yet. Let's start small: todo read a book
Six seven! I've sorted your tasks alphabetically. Even 67 likes a little order:
Six seven... no matching tasks this time. Try another keyword!
SIX SEVEN! A todo description cannot be empty.
No worries. Give it another go - I've got you. 67!
Six seven! On it. I've added this task:
  [T][ ] read book
You have 1 task in the list. 67!
Six seven! One task down! I've marked this task as done:
  [X] read book
Six seven! Another round? I've marked this task as not done yet:
  [ ] read book
Six seven. Making room! I've removed this task:
  [T][ ] read book
Now you have 0 tasks in the list.
Six seven! No tasks in the list yet. Let's start small: todo read a book
Bye. Hope to see you again soon. Six Seven!
```

## A-BetterGui: graphical checks

The console syntax is unchanged; personality replies use the checkpoints above. GUI-only behavior:

- Launch with Java 25: the window shows Bot67, a welcome message, and a collapsed command guide.
- Empty or whitespace-only input disables Send. Enter or Send submits a nonempty command and returns focus to input.
- In a fresh session, `list` displays `Six seven! No tasks in the list yet. Let's start small: todo read a book`.
- `todo prepare project demo` adds a user bubble and bot confirmation.
- `deadline` displays the normal error text without an added heading, using a contrasting error style.
- Open the command guide: examples are visible above the input; close it to recover conversation space.
- Resize to 420 by 500 and then enlarge: the composer remains usable and messages wrap.
- After enough messages to scroll, new replies become visible; older replies remain reachable by scrolling.
- `bye` exits the application.

Automated JavaFX smoke check passed for FXML loading, disabled empty Send, sending a task, displaying an error,
and keeping the input usable at 420 by 500. The 640 by 720 scene snapshot was visually inspected.

Revision checks: the expanded guide uses 15px left-aligned text and scrolls within a bounded height. At 420 by 500, the input remains visible with the guide expanded. The header uses the bot picture at 48px; chat avatars are 44px with rounded corners. The conversation uses the original hearts background under a pale overlay and rose accents.

The welcome message displays the original ASCII BANNER in a 12px monospace font, followed by PERSONALITY_ART as drawn dots and the welcome text. The guide lists all ten commands separately using angle-bracket placeholders, explains that brackets are omitted, and includes date formats and spacing guidance. Verify both artworks fit without wrapping or clipping at the minimum window width and scroll the guide to read its final lines.

App icon: launching the JavaFX application loads images/Bot67.png as the stage icon, matching the bot profile picture.

## Test 13: Whitespace and argument recovery

Aim: Accept extra whitespace and show specific guidance without losing the task list.

Input commands:

```text
  todo   read   book
mark
unmark
find
list extra
sort extra
bye extra
mark 2
unmark 9999999999999999
list
bye
```

Expected output checkpoints, in order:

```text
  [T][ ] read book
SIX SEVEN! Use: mark <task number>.
SIX SEVEN! Use: unmark <task number>.
SIX SEVEN! Use: find <keyword>.
SIX SEVEN! Use: list (no arguments).
SIX SEVEN! Use: sort (no arguments).
SIX SEVEN! Use: bye (no arguments).
SIX SEVEN! Task number is out of range.
SIX SEVEN! Task number must be a whole number.
1.[T][ ] read book
```

## Test 14: Malformed parameters and unsafe text

Aim: Reject missing descriptions, repeated or reordered parameters, and save-file delimiters.

Input commands:

```text
deadline   /by Sunday
deadline task /by Sunday /by Monday
event meeting /to Tue /from Mon
event meeting /from Mon /to Tue /to Wed
event meeting /from Mon /to
todo a | b
list
bye
```

Expected output checkpoints, in order:

```text
SIX SEVEN! Use: deadline <description> /by <date or time>.
SIX SEVEN! Use: deadline <description> /by <date or time>.
SIX SEVEN! Use: event <description> /from <start> /to <end>.
SIX SEVEN! Use: event <description> /from <start> /to <end>.
SIX SEVEN! Use: event <description> /from <start> /to <end>.
SIX SEVEN! Commands cannot contain | or control characters other than tabs.
Six seven! No tasks in the list yet. Let's start small: todo read a book
```

## Test 15: Strict dates and event ordering

Aim: Reject impossible dates and non-increasing ISO event ranges, then accept valid dates.

Input commands:

```text
deadline task /by 2026-02-30 12:00
deadline task /by 2026-02-30T12:00
deadline task /by 2026-10-15 24:00
event meeting /from 2026-10-15 /to 2026-10-15
event meeting /from 2026-10-16T14:00 /to 2026-10-15T14:00
deadline leap day /by 2028-02-29
list
bye
```

Expected output checkpoints, in order:

```text
SIX SEVEN! Invalid deadline date/time. Use yyyy-MM-dd or yyyy-MM-ddTHH:mm.
SIX SEVEN! Invalid deadline date/time. Use yyyy-MM-dd or yyyy-MM-ddTHH:mm.
SIX SEVEN! Invalid deadline date/time. Use yyyy-MM-dd or yyyy-MM-ddTHH:mm.
SIX SEVEN! Event start must be before its end.
SIX SEVEN! Event start must be before its end.
1.[D][ ] leap day (by: Feb 29 2028)
```

## Test 16: Damaged save file is protected

Aim: Warn on startup, identify the damaged line, and refuse to overwrite the original file.
Setup: Create `data/bot67.txt` with the following exact content before starting Bot67:

```text
T | 0 | keep this task
D | 0 | impossible date | 2026-02-30
```

Input commands:

```text
todo replacement
sort
bye
```

Expected output checkpoints, in order:

```text
SIX SEVEN! Could not load saved tasks. No tasks were loaded; changes are disabled to protect your file.
Check data/bot67.txt and restart Bot67.
Invalid saved task at line 2.
SIX SEVEN! Changes are disabled because saved tasks could not be loaded. Fix data/bot67.txt and restart Bot67.
SIX SEVEN! Changes are disabled because saved tasks could not be loaded. Fix data/bot67.txt and restart Bot67.
Bye. Hope to see you again soon. Six Seven!
```

Also verify that the save file's contents are unchanged. On Windows, accept `data\bot67.txt` in path messages.

## Test 17: Unreadable save path is protected

Aim: Treat a directory at the save-file path as an error, not an empty first session.
Setup: Create a directory at `data/bot67.txt` before starting Bot67.

Input commands:

```text
todo replacement
bye
```

Expected output checkpoints, in order:

```text
SIX SEVEN! Could not load saved tasks. No tasks were loaded; changes are disabled to protect your file.
Check data/bot67.txt and restart Bot67.
SIX SEVEN! Changes are disabled because saved tasks could not be loaded. Fix data/bot67.txt and restart Bot67.
Bye. Hope to see you again soon. Six Seven!
```

Also verify that `data/bot67.txt` remains a directory. On Windows, accept `data\bot67.txt` in path messages.

## Test 18: Save failure leaves the list unchanged

Aim: A failed first save reports a storage error and rolls back the new task.
Setup: Start Bot67 with no save file. After the welcome message, create a directory at `data/bot67.txt`.

Input commands:

```text
todo unsaved task
list
bye
```

Expected output checkpoints, in order:

```text
SIX SEVEN! Could not save tasks. Your change was not applied. Check data/bot67.txt and its folder permissions, then try again.
Six seven! No tasks in the list yet. Let's start small: todo read a book
Bye. Hope to see you again soon. Six Seven!
```

Also verify that `data/bot67.txt` remains a directory. On Windows, accept `data\bot67.txt` in path messages.

### A-MoreErrorHandling: graphical checks

- A corrupt or unreadable save file shows the startup warning text without an added heading.
- Storage failures and command errors use the same contrasting error style.
- Correcting invalid input lets the next valid response return to normal styling.
- A save failure reports that the change was not applied; `list` still shows the previous tasks.

Automated JavaFX smoke check passed for loading the actual FXML, showing the startup storage warning,
highlighting a reversed event-range error, and clearing the error style and input after a valid command.

Banner regression check passed: the GUI displays the exact original BANNER in monospace, with its horizontal
bounds inside both 420px and 640px windows. Snapshots at 420 by 500 and 640 by 720 were visually inspected;
the smaller window scrolls to reveal the complete welcome bubble.
