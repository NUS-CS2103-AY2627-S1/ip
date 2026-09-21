# Caitlyn UI test plan

The `test-ui` skill runs each case in a fresh Caitlyn process. Expected output lines are required substrings and must appear in the listed order; the banner and separators are intentionally not repeated here.

The state-sensitive cases below deliberately interleave valid and invalid commands. Their later `list` and status assertions verify that rejected inputs do not corrupt the task list.

The cases below exercise the command-line entry point. For the JavaFX entry point, launch with Java 25 using `./gradlew run`. Test task changes in a disposable copy of the project, or launch the packaged application from a temporary working directory. Perform these graphical smoke checks:

- Confirm the metal background appears behind the conversation, the nerd portrait identifies user messages, and the robot identifies Caitlyn. Avatars should be small, circular, and cropped around the faces. Text should remain legible over the background.
- Enter `todo buy milk` with Enter, then `list` with **Send**. Commands should be compact right-aligned bubbles; each complete reply should appear in one wider left-aligned card. The task count should change from `0 tasks saved` to `1 task saved`. There should be no ASCII banner or console separator lines.
- Submit `blah`, `todo`, and `mark 999`. Each error should have a red accent and the explicit label `CAITLYN · ERROR`. Invalid input should stay selected for correction. Correct it to `list` and verify that a normal reply is shown and the input clears.
- Open **Commands** and check that each entry shows a labelled syntax template and example, including `list` and `bye` without arguments. Long templates and examples must wrap within the menu. Choose `within`: the input should contain `within collect certificate /from 2027-01-15 /to 2027-01-25` without submitting, with only `collect certificate` selected for replacement. Type a new description and verify that the command name and date clauses remain intact. Repeat with `find`, a task-number command, and `list`; commands without arguments should place the caret at the end. Check mouse and keyboard menu selection. Right-click a reply and use **Copy message** to verify its complete text can be copied.
- Add a task with a long description, list several tasks, and resize between the minimum 380 × 360 window and a larger window. Replies should wrap without horizontal scrolling or truncated task text. Input and Send should remain visible. Scroll up to review history, then submit a command and verify the newest reply scrolls into view.
- Blank input should do nothing. Enter `bye`: the farewell remains visible, the status reads `Session ended · Close the window to exit`, and input, Send, and Commands are disabled. Resize and scroll the history after ending the session.
- Start with malformed saved data as in case 27. The startup error must use the same error card format, and the footer must show `Saved tasks unavailable · Changes disabled`. Rejected task changes must also be error cards.
- Run the reversed-event commands in case 28 and verify the error cards and unchanged task count. Run case 29 and verify that seconds and fractional seconds are visibly distinct. Mark the short event, enter `bye`, close the window, and restart from the same folder: `list` must retain the exact times and completed status.

For within-period tasks, launch the GUI from a temporary working directory with no saved data. Enter `within collect certificate /from 2027-01-15 /to 2027-01-25` using Enter, then `list` using Send. Verify the exact task text from case 22, `1 task saved`, and the visible `within` hint. Submit a reversed window and confirm the range error and unchanged count; use mark/unmark and then bye. In a second temporary directory, put the invalid data from case 27 in `data/duke.txt`, relaunch, and verify the startup protection error. Try `within` using Enter and `todo blocked` using Send; both must show the protection message. Check that the saved bytes are unchanged. Repair the file, verify that the running session still rejects changes, then restart and verify normal operation.

## Manual environment matrix

These checks complement JUnit's JVM-locale and line-ending tests. Record the OS,
Java version, display resolution/scaling, OS language, outcome, and any screenshot
or failure details for each run. All matrix rows below are pending manual execution;
a passing CLI test does not count as a GUI or OS compatibility result.

| Environment | Display and language combinations | Checks |
| --- | --- | --- |
| macOS | Native Retina scaling and scaled display; English and Chinese | Run the GUI checks above, including resizing to 380 × 360 and a larger window. |
| Windows | 1366 × 768 at 100%, 1920 × 1080 at 150%; English and Chinese | Check wrapping, keyboard/menu focus, scrolling, and persistence after restart. |
| Linux desktop | 1366 × 768 and 1920 × 1080; English and Chinese | Check fonts, clipboard, focus, window resizing, and persistence after restart. |

On each environment, enter `todo 阅读 📚 café` using the keyboard and, for Chinese,
the OS input method. Confirm the composed text is not sent prematurely. Add a dated
task, then use `list`, `find 阅读`, mark/unmark, and restart. The description and
completion state must survive; dates should retain their English display format.
Try a long mixed English/Chinese description at the minimum window size and at
larger sizes. Ensure task text wraps, controls remain usable, and no text is clipped.
Use a temporary working directory for each run to keep personal task data separate.

## Test case 1: Add and list a ToDo

Aim: Verify that a date-free task is stored, displayed with the `T` type marker, and included in the task count.

Input:

```text
todo borrow book
list
bye
```

Expected output:

```text
[T][ ] borrow book
Now you have 1 tasks in the list.
1.[T][ ] borrow book
```

## Test case 2: Parse and format a Deadline date and time

Aim: Verify that a deadline parses the example day/month/year and compact 24-hour time into a date/time value, displays it in a different format, and saves it canonically.

Input:

```text
deadline return book /by 2/12/2019 1800
list
bye
```

Expected output:

```text
[D][ ] return book (by: Dec 2 2019 6:00 PM)
1.[D][ ] return book (by: Dec 2 2019 6:00 PM)
```

Expected `data/duke.txt` contents after the case:

```text
D | 0 | return book | 2019-12-02T18:00
```

## Test case 3: Parse and format an Event with date-only values

Aim: Verify that an event stores typed start and end dates and displays them in the requested readable format.

Input:

```text
event orientation week /from 2019-10-04 /to 2019-10-11
list
bye
```

Expected output:

```text
[E][ ] orientation week (from: Oct 4 2019 to: Oct 11 2019)
1.[E][ ] orientation week (from: Oct 4 2019 to: Oct 11 2019)
```

## Test case 4: Use polymorphic task storage and status changes

Aim: Verify that ToDo, Deadline, and Event objects coexist in one task list and can all be marked and unmarked through the common `Task` interface.

Input:

```text
todo read book
deadline return book /by 2019-10-20
event project meeting /from 2019-10-21 1400 /to 2019-10-21 1600
mark 1
unmark 1
list
bye
```

Expected output:

```text
[T][X] read book
[T][ ] read book
1.[T][ ] read book
2.[D][ ] return book (by: Oct 20 2019)
3.[E][ ] project meeting (from: Oct 21 2019 2:00 PM to: Oct 21 2019 4:00 PM)
```

## Test case 5: Reject an incomplete Deadline command

Aim: Verify that a malformed Deadline does not add a task and that the application remains usable.

Input:

```text
deadline missing deadline
list
bye
```

Expected output:

```text
Please provide a deadline in the format: deadline task /by date
Here are the tasks in your list:
```

## Test case 6: Reject an empty ToDo description

Aim: Verify that an empty ToDo is rejected with a Caitlyn exception and that no empty task is added.

Input:

```text
todo
list
bye
```

Expected output:

```text
I beg your pardon, master. I cannot prepare a task without a description.
Here are the tasks in your list:
```

## Test case 7: Reject an unknown command

Aim: Verify that an unrecognized command is rejected and that Caitlyn continues accepting commands.

Input:

```text
blah
list
bye
```

Expected output:

```text
I humbly beg your pardon, master. I do not know how to carry out that command.
Here are the tasks in your list:
```

## Test case 8: Preserve ToDo state after an empty description

Aim: Verify that an empty ToDo between two valid ToDos is rejected without creating an empty task or changing the task count.

Input:

```text
todo buy milk
todo
list
todo read book
list
bye
```

Expected output:

```text
[T][ ] buy milk
I beg your pardon, master. I cannot prepare a task without a description.
Here are the tasks in your list:
1.[T][ ] buy milk
[T][ ] read book
Now you have 2 tasks in the list.
1.[T][ ] buy milk
2.[T][ ] read book
```

## Test case 9: Preserve mixed task state after malformed task commands

Aim: Verify that malformed Deadline and Event commands do not add partial tasks between valid tasks.

Input:

```text
deadline submit report /by 2019-10-18
deadline missing deadline
event team meeting /from 2019-10-21 /to 2019-10-21 1500
event /from Tuesday /to 4pm
list
bye
```

Expected output:

```text
[D][ ] submit report (by: Oct 18 2019)
I beg your pardon, master. Please provide a deadline in the format: deadline task /by date.
[E][ ] team meeting (from: Oct 21 2019 to: Oct 21 2019 3:00 PM)
I beg your pardon, master. Please provide a description, start time, and end time for the event.
Here are the tasks in your list:
1.[D][ ] submit report (by: Oct 18 2019)
2.[E][ ] team meeting (from: Oct 21 2019 to: Oct 21 2019 3:00 PM)
```

## Test case 10: Preserve task status after invalid mark commands

Aim: Verify that an out-of-range or non-numeric status command does not change existing task statuses.

Input:

```text
todo finish project
deadline review report /by 2019-10-20
mark 1
mark 3
list
unmark two
unmark 1
list
bye
```

Expected output:

```text
[T][X] finish project
I beg your pardon, master, but I could not find task 3.
1.[T][X] finish project
2.[D][ ] review report (by: Oct 20 2019)
I beg your pardon, master. Please provide a valid task number, for example: unmark 2.
[T][ ] finish project
1.[T][ ] finish project
2.[D][ ] review report (by: Oct 20 2019)
```

## Test case 11: Delete a task and renumber the remaining list

Aim: Verify that a task can be removed by its displayed number, that the removed task is confirmed, and that the remaining tasks are renumbered.

Input:

```text
todo read book
deadline return book /by 2019-06-06
event project meeting /from 2019-08-06 1400 /to 2019-08-06 1600
todo join sports club
delete 3
list
bye
```

Expected output:

```text
Noted. I've removed this task:
[E][ ] project meeting (from: Aug 6 2019 2:00 PM to: Aug 6 2019 4:00 PM)
Now you have 3 tasks in the list.
1.[T][ ] read book
2.[D][ ] return book (by: Jun 6 2019)
3.[T][ ] join sports club
```

## Test case 12: Reject an invalid delete command

Aim: Verify that an invalid task number does not delete an existing task or change the task count.

Input:

```text
todo keep this task
delete 2
list
bye
```

Expected output:

```text
I beg your pardon, master, but I could not find task 2.
1.[T][ ] keep this task
```

## Test case 13: Save task changes automatically

Aim: Verify that adding, completing, and deleting tasks all use the automatic save path. After this case, `data/duke.txt` should contain the final task list in storage format.

Input:

```text
todo read book
deadline return book /by 2019-06-06
mark 1
delete 2
bye
```

Expected output:

```text
[T][ ] read book
[D][ ] return book (by: Jun 6 2019)
[T][X] read book
Now you have 1 tasks in the list.
```

Expected `data/duke.txt` contents after the case:

```text
T | 1 | read book
```

## Test case 14: Load saved tasks at startup

Aim: Verify that ToDos, deadlines, events, and their saved completion statuses are restored when Caitlyn starts.

Initial data/duke.txt contents:

```text
T | 1 | read book
D | 0 | return book | 2019-10-20
E | 0 | project meeting | 2019-10-21 | 2019-10-21T16:00
T | 1 | path \\backup \| notes
```

Input:

```text
list
bye
```

Expected output:

```text
Here are the tasks in your list:
1.[T][X] read book
2.[D][ ] return book (by: Oct 20 2019)
3.[E][ ] project meeting (from: Oct 21 2019 to: Oct 21 2019 4:00 PM)
4.[T][X] path \backup | notes
```

## Test case 15: Protect malformed saved data

Aim: Verify that an invalid legacy record starts an empty protected session without overwriting the original data.

Initial data/duke.txt contents:

```text
T | 2 | invalid status
```

Input:

```text
list
bye
```

Expected output:

```text
I could not read data/duke.txt. Task changes are disabled to protect your saved data. Repair the file or its access permissions, then restart Caitlyn.
Here are the tasks in your list:
```

Expected `data/duke.txt` contents after the case:

```text
T | 2 | invalid status
```

## Test case 16: Preserve special characters during saving

Aim: Verify that pipes and backslashes in task fields are escaped when saved instead of being mistaken for storage separators.

Input:

```text
todo plan | review \backup
deadline send | mail /by 2019-10-15
event call | backup /from 2019-10-15 /to 2019-10-16
bye
```

Expected output:

```text
[T][ ] plan | review \backup
[D][ ] send | mail (by: Oct 15 2019)
[E][ ] call | backup (from: Oct 15 2019 to: Oct 16 2019)
```

Expected `data/duke.txt` contents after the case:

```text
T | 0 | plan \| review \\backup
D | 0 | send \| mail | 2019-10-15
E | 0 | call \| backup | 2019-10-15 | 2019-10-16
```

## Test case 17: Start without an existing data file

Aim: Verify that a first run works when both the `data` directory and `duke.txt` file are absent, then creates them when the first task is added.

Input:

```text
list
todo first-run task
bye
```

Expected output:

```text
Here are the tasks in your list:
[T][ ] first-run task
Now you have 1 tasks in the list.
```

Expected `data/duke.txt` contents after the case:

```text
T | 0 | first-run task
```

## Test case 18: Reject an invalid date

Aim: Verify that an impossible calendar date is rejected without crashing Caitlyn or adding a task.

Input:

```text
deadline invalid date /by 2019-02-30
list
bye
```

Expected output:

```text
Please use a valid date such as 2019-10-15 or 2/12/2019 1800.
Here are the tasks in your list:
```

## Test case 19: Exit immediately after the bye command

Aim: Verify that Caitlyn stops without waiting for another input line after the user enters `bye`.

Input:

```text
bye
```

Expected output:

```text
Farewell, master. It has been my pleasure to serve you.
```

## Test case 20: Find tasks by keyword

Aim: Verify that a keyword search is case-insensitive, returns only matching descriptions, preserves the original task numbers, and displays all task types in their normal format.

Input:

```text
todo read book
deadline return book /by 2019-06-06
todo buy milk
find BOOK
bye
```

Expected output:

```text
Here are the matching tasks in your list:
1.[T][ ] read book
2.[D][ ] return book (by: Jun 6 2019)
```

## Test case 21: Reject a find command without a keyword

Aim: Verify that a find command without a keyword is rejected and does not affect the task list.

Input:

```text
todo keep this task
find
list
bye
```

Expected output:

```text
Please provide a keyword, for example: find book.
1.[T][ ] keep this task
```

## Test case 22: Add and manage a within-period task

Aim: Verify the new type's exact task display, search, completion changes, deletion, and automatic saving.

Input:

```text
within collect certificate /from 2027-01-15 /to 2027-01-25
list
find CERTIFICATE
mark 1
unmark 1
delete 1
list
bye
```

Expected output:

```text
Got it. I've added this task:
[W][ ] collect certificate (within: Jan 15 2027 to: Jan 25 2027)
Now you have 1 tasks in the list.
Here are the tasks in your list:
1.[W][ ] collect certificate (within: Jan 15 2027 to: Jan 25 2027)
Here are the matching tasks in your list:
1.[W][ ] collect certificate (within: Jan 15 2027 to: Jan 25 2027)
As you wish, master. I have marked this task as done:
[W][X] collect certificate (within: Jan 15 2027 to: Jan 25 2027)
Of course, master. I have marked this task as not done yet:
[W][ ] collect certificate (within: Jan 15 2027 to: Jan 25 2027)
Noted. I've removed this task:
[W][ ] collect certificate (within: Jan 15 2027 to: Jan 25 2027)
Now you have 0 tasks in the list.
Here are the tasks in your list:
```

Expected `data/duke.txt` contents after the case:

```text

```

## Test case 23: Preserve timed and whole-day boundaries

Aim: Verify accepted input formats, equal endpoints, and a timed start ending on a date-only day.

Input:

```text
within collect certificate /from 15/1/2027 0900 /to 25/1/2027 17:00
within submit form /from 2027-01-15 0900 /to 2027-01-15
within one day /from 2027-01-15 /to 2027-01-15
within press button /from 2027-01-15T09:00:00 /to 2027-01-15T09:00
bye
```

Expected output:

```text
[W][ ] collect certificate (within: Jan 15 2027 9:00 AM to: Jan 25 2027 5:00 PM)
[W][ ] submit form (within: Jan 15 2027 9:00 AM to: Jan 15 2027)
[W][ ] one day (within: Jan 15 2027 to: Jan 15 2027)
[W][ ] press button (within: Jan 15 2027 9:00 AM to: Jan 15 2027 9:00 AM)
```

Expected `data/duke.txt` contents after the case:

```text
W | 0 | collect certificate | 2027-01-15T09:00 | 2027-01-25T17:00
W | 0 | submit form | 2027-01-15T09:00 | 2027-01-15
W | 0 | one day | 2027-01-15 | 2027-01-15
W | 0 | press button | 2027-01-15T09:00 | 2027-01-15T09:00
```

## Test case 24: Reject malformed within commands without changing data

Aim: Verify structure, date, precision, range, and command-case errors while retaining an existing task.

Input:

```text
todo keep
within task /from 2027-01-15
within task /from 2027-02-30 /to 2027-03-05
within task /from 2027-01-15 /to tomorrow
within task /from 2027-01-15T09:00:01 /to 2027-01-25
within task /from 2027-01-15 /to 2027-01-25T17:00:00.001
within task /from 2027-01-26 /to 2027-01-25
WITHIN task /from 2027-01-15 /to 2027-01-25
list
bye
```

Expected output:

```text
I beg your pardon, master. Please use: within task /from start /to end. Provide a description and both boundaries, with /from followed by /to exactly once.
I beg your pardon, master. Please provide a valid start date, for example: 2027-01-15 or 15/1/2027 0900.
I beg your pardon, master. Please provide a valid end date, for example: 2027-01-25 or 25/1/2027 1700.
I beg your pardon, master. The start time must use minute precision; seconds and fractional seconds must be zero.
I beg your pardon, master. The end time must use minute precision; seconds and fractional seconds must be zero.
I beg your pardon, master. The start of the window must not be after its end.
I humbly beg your pardon, master. I do not know how to carry out that command.
Here are the tasks in your list:
1.[T][ ] keep
```

Expected `data/duke.txt` contents after the case:

```text
T | 0 | keep
```

## Test case 25: Load within records alongside legacy tasks

Aim: Verify task order, completion, mixed boundary precision, escaping, and valid legacy events.

Initial data/duke.txt contents:

```text
T | 0 | keep
E | 0 | meeting | 2027-01-15 | 2027-01-25
W | 1 | collect \| file \\backup | 2027-01-15T09:00 | 2027-01-25
D | 0 | return book | 2027-01-25
```

Input:

```text
list
find COLLECT
bye
```

Expected output:

```text
1.[T][ ] keep
2.[E][ ] meeting (from: Jan 15 2027 to: Jan 25 2027)
3.[W][X] collect | file \backup (within: Jan 15 2027 9:00 AM to: Jan 25 2027)
4.[D][ ] return book (by: Jan 25 2027)
Here are the matching tasks in your list:
3.[W][X] collect | file \backup (within: Jan 15 2027 9:00 AM to: Jan 25 2027)
```

Expected `data/duke.txt` contents after the case:

```text
T | 0 | keep
E | 0 | meeting | 2027-01-15 | 2027-01-25
W | 1 | collect \| file \\backup | 2027-01-15T09:00 | 2027-01-25
D | 0 | return book | 2027-01-25
```

## Test case 26: Save duplicate within tasks with special characters

Aim: Verify that identical windows are accepted and special description text is escaped on saving.

Input:

```text
within collect | file \backup /from 2027-01-15 /to 2027-01-25
within collect | file \backup /from 2027-01-15 /to 2027-01-25
find backup
bye
```

Expected output:

```text
Now you have 2 tasks in the list.
Here are the matching tasks in your list:
1.[W][ ] collect | file \backup (within: Jan 15 2027 to: Jan 25 2027)
2.[W][ ] collect | file \backup (within: Jan 15 2027 to: Jan 25 2027)
```

Expected `data/duke.txt` contents after the case:

```text
W | 0 | collect \| file \\backup | 2027-01-15 | 2027-01-25
W | 0 | collect \| file \\backup | 2027-01-15 | 2027-01-25
```

## Test case 27: Block all task changes after invalid within data

Aim: Verify the shared protection rule, precedence over argument errors, read-only commands, and unchanged saved records.

Initial data/duke.txt contents:

```text
T | 1 | preserve this task
W | 0 | invalid | 2027-01-26 | 2027-01-25
```

Input:

```text
todo blocked
deadline blocked /by 2027-01-15
event blocked /from 2027-01-15 /to 2027-01-25
within
mark 999
unmark 999
delete 999
list
find preserve
unknown
bye
```

Expected output:

```text
I could not read data/duke.txt. Task changes are disabled to protect your saved data. Repair the file or its access permissions, then restart Caitlyn.
I beg your pardon, master. Task changes are disabled because saved tasks could not be loaded. Repair data/duke.txt or its access permissions, then restart Caitlyn.
I beg your pardon, master. Task changes are disabled because saved tasks could not be loaded. Repair data/duke.txt or its access permissions, then restart Caitlyn.
I beg your pardon, master. Task changes are disabled because saved tasks could not be loaded. Repair data/duke.txt or its access permissions, then restart Caitlyn.
I beg your pardon, master. Task changes are disabled because saved tasks could not be loaded. Repair data/duke.txt or its access permissions, then restart Caitlyn.
I beg your pardon, master. Task changes are disabled because saved tasks could not be loaded. Repair data/duke.txt or its access permissions, then restart Caitlyn.
I beg your pardon, master. Task changes are disabled because saved tasks could not be loaded. Repair data/duke.txt or its access permissions, then restart Caitlyn.
I beg your pardon, master. Task changes are disabled because saved tasks could not be loaded. Repair data/duke.txt or its access permissions, then restart Caitlyn.
Here are the tasks in your list:
Here are the matching tasks in your list:
I humbly beg your pardon, master. I do not know how to carry out that command.
Farewell, master. It has been my pleasure to serve you.
```

Expected `data/duke.txt` contents after the case:

```text
T | 1 | preserve this task
W | 0 | invalid | 2027-01-26 | 2027-01-25
```

## Test case 28: Reject reversed event boundaries without changing saved tasks

Aim: Reject reversed dates, times, and sub-minute ranges while retaining existing data.

Input:

```text
todo keep
event reversed /from 2026-12-03 1600 /to 2026-12-03 1500
event reversed dates /from 2026-12-04 /to 2026-12-03
event reversed seconds /from 2026-09-21T18:00:45 /to 2026-09-21T18:00:30
list
bye
```

Expected output:

```text
[T][ ] keep
I beg your pardon, master. The event's start must not be after its end.
I beg your pardon, master. The event's start must not be after its end.
I beg your pardon, master. The event's start must not be after its end.
Here are the tasks in your list:
1.[T][ ] keep
```

Expected `data/duke.txt` contents after the case:

```text
T | 0 | keep
```

## Test case 29: Display event and deadline seconds without truncation

Aim: Preserve seconds and nanoseconds in displays and storage, and allow equal or date-only end boundaries.

Input:

```text
event short /from 2026-09-21T18:00:30 /to 2026-09-21T18:00:45
deadline precise /by 2026-09-21T18:00:00.000000001
event equal /from 2026-09-21 1800 /to 2026-09-21 1800
event whole day end /from 2026-09-21 1800 /to 2026-09-21
list
bye
```

Expected output:

```text
[E][ ] short (from: Sep 21 2026 6:00:30 PM to: Sep 21 2026 6:00:45 PM)
[D][ ] precise (by: Sep 21 2026 6:00:00.000000001 PM)
[E][ ] equal (from: Sep 21 2026 6:00 PM to: Sep 21 2026 6:00 PM)
[E][ ] whole day end (from: Sep 21 2026 6:00 PM to: Sep 21 2026)
1.[E][ ] short (from: Sep 21 2026 6:00:30 PM to: Sep 21 2026 6:00:45 PM)
2.[D][ ] precise (by: Sep 21 2026 6:00:00.000000001 PM)
```

Expected `data/duke.txt` contents after the case:

```text
E | 0 | short | 2026-09-21T18:00:30 | 2026-09-21T18:00:45
D | 0 | precise | 2026-09-21T18:00:00.000000001
E | 0 | equal | 2026-09-21T18:00 | 2026-09-21T18:00
E | 0 | whole day end | 2026-09-21T18:00 | 2026-09-21
```

## Test case 30: Require standalone date markers and preserve description text

Aim: Reject markers glued to values or descriptions and duplicate markers, while accepting marker-like words and extra spaces.

Input:

```text
deadline read /bytecode /by 2026-09-21  1800
event inspect /fromage and /tools /from 2026-09-21  1800 /to 2026-09-21 1900
deadline bad /by2026-09-21
event bad /from2026-09-21 /to2026-09-22
event bad /from 2026-09-21 /to 2026-09-22 /to 2026-09-23
list
bye
```

Expected output:

```text
[D][ ] read /bytecode (by: Sep 21 2026 6:00 PM)
[E][ ] inspect /fromage and /tools (from: Sep 21 2026 6:00 PM to: Sep 21 2026 7:00 PM)
Please provide a deadline in the format: deadline task /by date.
Please provide an event in the format: event task /from start /to end.
Please provide an event in the format: event task /from start /to end.
1.[D][ ] read /bytecode (by: Sep 21 2026 6:00 PM)
2.[E][ ] inspect /fromage and /tools (from: Sep 21 2026 6:00 PM to: Sep 21 2026 7:00 PM)
```

Expected `data/duke.txt` contents after the case:

```text
D | 0 | read /bytecode | 2026-09-21T18:00
E | 0 | inspect /fromage and /tools | 2026-09-21T18:00 | 2026-09-21T19:00
```

## Test case 31: Protect saved files containing reversed events

Aim: Reject invalid old event data at startup without rewriting or deleting it.

Initial data/duke.txt contents:

```text
T | 1 | keep
E | 0 | reversed | 2026-12-03T16:00 | 2026-12-03T15:00
```

Input:

```text
todo blocked
delete 1
list
bye
```

Expected output:

```text
I could not read data/duke.txt. Task changes are disabled to protect your saved data.
Task changes are disabled because saved tasks could not be loaded.
Task changes are disabled because saved tasks could not be loaded.
Here are the tasks in your list:
Farewell, master.
```

Expected `data/duke.txt` contents after the case:

```text
T | 1 | keep
E | 0 | reversed | 2026-12-03T16:00 | 2026-12-03T15:00
```
