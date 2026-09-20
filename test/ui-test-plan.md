# UI test plan

The `test-ui` skill runs each case in a fresh Bkxss process. Inputs are sent in
order, followed by `bye`. Expected output includes only the responses to the
listed inputs, not the greeting or farewell. Each case uses a temporary working directory so existing task data stays intact.

## GUI checks: A-BetterGui

Run the GUI from a temporary working directory with a fresh data file.

1. At startup, confirm that the command field has focus and Send is disabled.
   Spaces alone must keep Send disabled; Enter must not create empty messages.
   The subtitle should read `Your task housekeeper, at your service!`, and the title,
   controls, focus state, and user messages should use the blue visual theme.
2. Enter `todo borrow book`, `mark 2`, `mark 1`, `list`, `todo`, then `list`.
   Commands should be compact and right-aligned; reply cards should fit their text
   rather than stretching across the row.
   Each command should show the circular cat avatar on the right. Each Bkxss reply
   should show the circular dog avatar on the left.
   Only `mark 2` and `todo` should have a red card headed CHECK YOUR COMMAND.
   Both lists must contain exactly one completed task.
3. Add `todo OhNo!! ERROR :( --> investigate`. Its successful reply must use normal styling.
4. Add a task with a long description and run `list`. Resize the window to its
   minimum size and then to 900 × 700. Text should wrap without truncation or
   horizontal scrolling, and the input and Send button should remain usable.
5. Send enough commands to overflow the conversation. Each new reply should scroll
   into view; scrolling upward should still let you read earlier replies.
   The pale-blue `chatbot_background` world map should fill the conversation viewport
   without tiling, while every message remains easy to read.
6. Use both Enter and Send. Focus should return to the input after sending.
7. Paste `bye` with Unicode spacing around it, such as no-break spaces. The farewell
   should remain visible and both input and Send should be disabled. Relaunch and
   confirm the saved task state is preserved.

## Storage checks: A-MoreErrorHandling

`StorageTest` and `ErrorHandlingTest` automate these fixture-based checks.
Use temporary working directories for manual console verification as well.

1. Start with no data file, add and mark a task, then restart and list it.
   The task must still be completed.
2. Put valid records before and after a malformed record in `data/bkxss.txt`.
   Startup must report the bad line; `list` must contain both valid records.
   Attempt `todo new`, `mark 1`, or `delete 1`: expect an error asking for
   repair/restart, with the list and original file unchanged.
3. Repeat with invalid completion flags, impossible stored deadline/event
   dates, equal/reversed event boundaries, duplicate tasks, and invalid UTF-8.
   Never silently replace an unreadable or partially loaded file.
4. Make the data file read-only, or use a regular file where its parent
   directory should be. Attempt task changes: expect `I couldn't save your
   tasks. No changes were applied.` with guidance about permissions/disk space.
   No success message may appear, and list contents/statuses must be unchanged.
5. Repair permissions or the blocked parent after a save failure and retry.
   Saving must work again. After a failed load, repair the data and restart.
6. Load an older event with `Mon 2pm to 4pm`. Listing/deletion must work;
   `findfree` must ask for dated boundaries. New free-text events must be rejected.

## Automated coverage and manual portability checks: A-MoreTesting

See `test/README.md` for the Java 25 JUnit and coverage commands. CI runs JUnit,
fat-JAR packaging, and this console plan on Windows, macOS, and Linux. JUnit
automates console startup, EOF, farewell, and restart persistence in temporary
directories, in addition to command, model, schedule, and storage tests.

The following manual checks remain for JavaFX presentation. Record the OS,
display resolution/scaling, language, and outcome when running them; this list
does not imply that every environment has already been tested.

1. Repeat the GUI checks above on each available OS (macOS, Windows, Linux).
2. Repeat at 1280 × 720 and 1920 × 1080, including 100% and 200% scaling where
   available. Confirm readable text, usable controls, wrapping, and scrolling.
3. Repeat with English and Chinese OS language settings. Add `todo 买书`,
   search for `买书`, mark it, and restart. Confirm fonts and input methods work
   and the completed task survives. Dates should keep the documented format.

## Test case: Create todo task

Aim: Verify that a todo command creates and displays a Todo task.

### Inputs

```text
todo borrow book
```

### Expected output

```text
Got it. I've added this task:
[T][ ] borrow book
Now you have 1 tasks in the list.
```

## Test case: Mark and unmark a task

Aim: Verify that mark and unmark update a task's status and reject duplicate operations.

### Inputs

```text
todo borrow book
mark 1
mark 1
unmark 1
unmark 1
```

### Expected output

```text
Got it. I've added this task:
[T][ ] borrow book
Now you have 1 tasks in the list.
Nice! I've marked this task as done:
  [T][X] borrow book
OhNo!! ERROR :( --> this task is already marked as done!
OK, I've marked this task as not done yet:
  [T][ ] borrow book
OhNo!! ERROR :( --> this task is already unmarked!
```

## Test case: Interleaved valid and invalid mark operations

Aim: Verify that invalid mark/unmark commands do not corrupt task state.

### Inputs

```text
todo borrow book
mark 2
mark 1
mark abc
list
unmark 1
unmark 99
list
```

### Expected output

```text
Got it. I've added this task:
[T][ ] borrow book
Now you have 1 tasks in the list.
OhNo!! ERROR :( --> there is no task numbered 2.
Nice! I've marked this task as done:
  [T][X] borrow book
OhNo!! ERROR :( --> please provide a task number. Use: mark/unmark/delete NUMBER
Here are the tasks in your list:
1.[T][X] borrow book
OK, I've marked this task as not done yet:
  [T][ ] borrow book
OhNo!! ERROR :( --> there is no task numbered 99.
Here are the tasks in your list:
1.[T][ ] borrow book
```

## Test case: Invalid commands

Aim: Verify that missing todo descriptions, harmless trailing spaces after list, and unknown commands show helpful errors without ending the program.

### Inputs

```text
todo
list 
blah
```

### Expected output

```text
OhNo!! ERROR :( --> The description of a todo cannot be empty.
Here are the tasks in your list:
OhNo!! ERROR :( --> I'm sorry, but I don't know what that means :-(
```

## Test case: Exit with Unicode whitespace

Aim: Verify that Unicode spacing around bye is accepted and later commands are not processed.

### Inputs

```text
 bye 
todo should not run
```

### Expected output

```text

```

## Test case: Find tasks by description keyword

Aim: Verify that find returns matching tasks case-insensitively, preserves task display formats, and rejects an empty keyword without changing the task list.

### Inputs

```text
todo read book
deadline return book /by 2019-12-02 1800
event project meeting /from 2026-09-12 1400 /to 2026-09-12 1600
find BOOK
find
list
```

### Expected output

```text
Got it. I've added this task:
[T][ ] read book
Now you have 1 tasks in the list.
Got it. I've added this task:
[D][ ] return book (by: Dec 02 2019 18:00)
Now you have 2 tasks in the list.
Got it. I've added this task:
[E][ ] project meeting (from: 2026-09-12 1400 to: 2026-09-12 1600)
Now you have 3 tasks in the list.
Here are the matching tasks in your list:
1.[T][ ] read book
2.[D][ ] return book (by: Dec 02 2019 18:00)
OhNo!! ERROR :( --> please provide a keyword to search for. Use: find KEYWORD
Here are the tasks in your list:
1.[T][ ] read book
2.[D][ ] return book (by: Dec 02 2019 18:00)
3.[E][ ] project meeting (from: 2026-09-12 1400 to: 2026-09-12 1600)
```

## Test case: Create deadline task

Aim: Verify that a deadline command preserves its description and due date.

### Inputs

```text
deadline return book /by 2019-12-02 1800
```

### Expected output

```text
Got it. I've added this task:
[D][ ] return book (by: Dec 02 2019 18:00)
Now you have 1 tasks in the list.
```

## Test case: Create event task

Aim: Verify that an event command preserves its description, start, and end times.

### Inputs

```text
event project meeting /from 2026-09-12 1400 /to 2026-09-12 1600
```

### Expected output

```text
Got it. I've added this task:
[E][ ] project meeting (from: 2026-09-12 1400 to: 2026-09-12 1600)
Now you have 1 tasks in the list.
```

## Test case: List polymorphic tasks

Aim: Verify that the Task array lists Todo, Deadline, and Event objects using their own output formats.

### Inputs

```text
todo borrow book
deadline return book /by 2019-12-02 1800
event project meeting /from 2026-09-12 1400 /to 2026-09-12 1600
list
```

### Expected output

```text
Got it. I've added this task:
[T][ ] borrow book
Now you have 1 tasks in the list.
Got it. I've added this task:
[D][ ] return book (by: Dec 02 2019 18:00)
Now you have 2 tasks in the list.
Got it. I've added this task:
[E][ ] project meeting (from: 2026-09-12 1400 to: 2026-09-12 1600)
Now you have 3 tasks in the list.
Here are the tasks in your list:
1.[T][ ] borrow book
2.[D][ ] return book (by: Dec 02 2019 18:00)
3.[E][ ] project meeting (from: 2026-09-12 1400 to: 2026-09-12 1600)
```

## Test case: Delete a task

Aim: Verify that delete removes the correct task and renumbers the remaining list.

### Inputs

```text
todo borrow book
deadline return book /by 2019-12-02 1800
event project meeting /from 2026-09-12 1400 /to 2026-09-12 1600
delete 2
list
delete 5
```

### Expected output

```text
Got it. I've added this task:
[T][ ] borrow book
Now you have 1 tasks in the list.
Got it. I've added this task:
[D][ ] return book (by: Dec 02 2019 18:00)
Now you have 2 tasks in the list.
Got it. I've added this task:
[E][ ] project meeting (from: 2026-09-12 1400 to: 2026-09-12 1600)
Now you have 3 tasks in the list.
Noted. I've removed this task:
  [D][ ] return book (by: Dec 02 2019 18:00)
Now you have 2 tasks in the list.
Here are the tasks in your list:
1.[T][ ] borrow book
2.[E][ ] project meeting (from: 2026-09-12 1400 to: 2026-09-12 1600)
OhNo!! ERROR :( --> there is no task numbered 5.
```

## Test case: Save and reload tasks

Aim: Verify that changed tasks are saved and loaded by a later chatbot startup.

### Inputs

```text
todo remember persistence
mark 1
```

### Expected output

```text
Got it. I've added this task:
[T][ ] remember persistence
Now you have 1 tasks in the list.
Nice! I've marked this task as done:
  [T][X] remember persistence
```

## Test case: Reject invalid deadline without changing task state

Aim: Verify that an invalid date is rejected and does not add a corrupted deadline task.

### Inputs

```text
deadline return book /by 2019-02-30 1800
deadline return book /by 2019-02-28 1800
list
```

### Expected output

```text
OhNo!! ERROR :( --> please provide a valid deadline in yyyy-MM-dd HHmm format, e.g. 2019-12-02 1800
Got it. I've added this task:
[D][ ] return book (by: Feb 28 2019 18:00)
Now you have 1 tasks in the list.
Here are the tasks in your list:
1.[D][ ] return book (by: Feb 28 2019 18:00)
```

## Test case: Find the earliest free time

Aim: Verify that findfree finds an exact-sized gap and reports when no gap is long enough.

### Inputs

```text
event lecture /from 2026-09-12 0900 /to 2026-09-12 1000
event workshop /from 2026-09-12 1200 /to 2026-09-12 1500
findfree 2 /from 2026-09-12 0900 /to 2026-09-12 1700
findfree 3 /from 2026-09-12 0900 /to 2026-09-12 1700
```

### Expected output

```text
Got it. I've added this task:
[E][ ] lecture (from: 2026-09-12 0900 to: 2026-09-12 1000)
Now you have 1 tasks in the list.
Got it. I've added this task:
[E][ ] workshop (from: 2026-09-12 1200 to: 2026-09-12 1500)
Now you have 2 tasks in the list.
The earliest 2-hour free slot is:
  Sep 12 2026 10:00 to Sep 12 2026 12:00
I couldn't find a 3-hour free slot between Sep 12 2026 09:00 and Sep 12 2026 17:00.
```

## Test case: Reject invalid free-time inputs without changing tasks

Aim: Verify that invalid searches and event ranges do not corrupt state, and new events require valid calendar dates.

### Inputs

```text
event lecture /from 2026-09-12 0900 /to 2026-09-12 1000
findfree 0 /from 2026-09-12 0900 /to 2026-09-12 1700
findfree 2 /from 2026-02-30 0900 /to 2026-09-12 1700
event impossible /from 2026-09-12 1200 /to 2026-09-12 1100
findfree 1 /from 2026-09-12 1700 /to 2026-09-12 0900
event vague meeting /from Mon 2pm /to 4pm
delete 2
findfree 2 /from 2026-09-12 0900 /to 2026-09-12 1700
list
```

### Expected output

```text
Got it. I've added this task:
[E][ ] lecture (from: 2026-09-12 0900 to: 2026-09-12 1000)
Now you have 1 tasks in the list.
OhNo!! ERROR :( --> please provide the duration as a positive whole number of hours.
OhNo!! ERROR :( --> please provide valid search dates in yyyy-MM-dd HHmm format, e.g. 2026-09-12 0900
OhNo!! ERROR :( --> an event's start must be before its end.
OhNo!! ERROR :( --> the free-time search start must be before its end.
OhNo!! ERROR :( --> please provide valid event dates in yyyy-MM-dd HHmm format, e.g. 2026-09-12 0900
OhNo!! ERROR :( --> there is no task numbered 2.
The earliest 2-hour free slot is:
  Sep 12 2026 10:00 to Sep 12 2026 12:00
Here are the tasks in your list:
1.[E][ ] lecture (from: 2026-09-12 0900 to: 2026-09-12 1000)
```


## Test case: GUI command sequence preserves state after errors

Aim: Verify the GUI smoke-test command sequence also preserves task state in the console, including task descriptions containing error text.

### Inputs

```text
todo borrow book
mark 2
mark 1
list
todo
list
todo OhNo!! ERROR :( --> investigate
list
```

### Expected output

```text
Got it. I've added this task:
[T][ ] borrow book
Now you have 1 tasks in the list.
OhNo!! ERROR :( --> there is no task numbered 2.
Nice! I've marked this task as done:
  [T][X] borrow book
Here are the tasks in your list:
1.[T][X] borrow book
OhNo!! ERROR :( --> The description of a todo cannot be empty.
Here are the tasks in your list:
1.[T][X] borrow book
Got it. I've added this task:
[T][ ] OhNo!! ERROR :( --> investigate
Now you have 2 tasks in the list.
Here are the tasks in your list:
1.[T][X] borrow book
2.[T][ ] OhNo!! ERROR :( --> investigate
```

## Test case: Normalize spacing and reject duplicate tasks

Aim: Verify harmless spacing is accepted and duplicates remain invalid after marking, without changing the list.

### Inputs

```text
   todo   read   book
mark 1
todo read book
list extra
   list
unmark 1
todo bad | record
list
```

### Expected output

```text
Got it. I've added this task:
[T][ ] read book
Now you have 1 tasks in the list.
Nice! I've marked this task as done:
  [T][X] read book
OhNo!! ERROR :( --> this task already exists in your list.
OhNo!! ERROR :( --> list and bye do not accept extra arguments.
Here are the tasks in your list:
1.[T][X] read book
OK, I've marked this task as not done yet:
  [T][ ] read book
OhNo!! ERROR :( --> task descriptions cannot contain | or control characters.
Here are the tasks in your list:
1.[T][ ] read book
```

## Test case: Validate parameter order and calendar dates

Aim: Interleave valid additions with missing, repeated, reversed, and unknown parameters, impossible dates, and equal event boundaries.

### Inputs

```text
deadline return book /by 2028-02-29 1800
deadline return book /by 2028-02-29 1800
deadline missing /by
deadline repeated /by 2028-02-29 1800 /by 2028-03-01 1800
event reversed /to 2028-02-29 0900 /from 2028-02-29 1000
event meeting /from 2028-02-29 0900 /to 2028-02-29 1000
event repeat /from 2028-02-29 0900 /from 2028-02-29 1000
event missing /from /to 2028-02-29 1000
event invalid /from 2028-02-30 0900 /to 2028-03-01 1000
event equal /from 2028-02-29 0900 /to 2028-02-29 0900
findfree 1 /from 2028-02-29 0900 /to 2028-02-29 1200 /by 2028-02-29 1300
findfree 1 /from 2028-02-29 0900 /to 2028-02-29 1200
list
```

### Expected output

```text
Got it. I've added this task:
[D][ ] return book (by: Feb 29 2028 18:00)
Now you have 1 tasks in the list.
OhNo!! ERROR :( --> this task already exists in your list.
OhNo!! ERROR :( --> invalid or repeated parameters. Use: deadline DESCRIPTION /by DATE
OhNo!! ERROR :( --> invalid or repeated parameters. Use: deadline DESCRIPTION /by DATE
OhNo!! ERROR :( --> invalid or repeated parameters. Use: event DESCRIPTION /from START /to END
Got it. I've added this task:
[E][ ] meeting (from: 2028-02-29 0900 to: 2028-02-29 1000)
Now you have 2 tasks in the list.
OhNo!! ERROR :( --> invalid or repeated parameters. Use: event DESCRIPTION /from START /to END
OhNo!! ERROR :( --> invalid or repeated parameters. Use: event DESCRIPTION /from START /to END
OhNo!! ERROR :( --> please provide valid event dates in yyyy-MM-dd HHmm format, e.g. 2026-09-12 0900
OhNo!! ERROR :( --> an event's start must be before its end.
OhNo!! ERROR :( --> invalid or repeated parameters. Use: findfree HOURS /from START /to END
The earliest 1-hour free slot is:
  Feb 29 2028 10:00 to Feb 29 2028 11:00
Here are the tasks in your list:
1.[D][ ] return book (by: Feb 29 2028 18:00)
2.[E][ ] meeting (from: 2028-02-29 0900 to: 2028-02-29 1000)
```

## Test case: Reject malformed task numbers and exit arguments

Aim: Verify missing, overflowing, signed, and multi-value numbers cannot change state or terminate the session prematurely.

### Inputs

```text
todo keep me
delete 999999999999999999999
mark 1 2
mark +1
mark 0
mark 1
delete -1
unmark
bye extra
list
delete 1
list
```

### Expected output

```text
Got it. I've added this task:
[T][ ] keep me
Now you have 1 tasks in the list.
OhNo!! ERROR :( --> please provide a task number. Use: mark/unmark/delete NUMBER
OhNo!! ERROR :( --> please provide a task number. Use: mark/unmark/delete NUMBER
OhNo!! ERROR :( --> please provide a task number. Use: mark/unmark/delete NUMBER
OhNo!! ERROR :( --> there is no task numbered 0.
Nice! I've marked this task as done:
  [T][X] keep me
OhNo!! ERROR :( --> there is no task numbered -1.
OhNo!! ERROR :( --> please provide a task number. Use: mark/unmark/delete NUMBER
OhNo!! ERROR :( --> list and bye do not accept extra arguments.
Here are the tasks in your list:
1.[T][X] keep me
Noted. I've removed this task:
  [T][X] keep me
Now you have 0 tasks in the list.
Here are the tasks in your list:
```

## Test case: Find renumbers matches and recovers after deletion errors

Aim: Verify matches use consecutive result numbers and invalid deletion leaves the remaining task order intact.

### Inputs

```text
todo unrelated
todo read book
todo return BOOK
find book
find
mark 3
delete 2
delete 3
find BOOK
list
```

### Expected output

```text
Got it. I've added this task:
[T][ ] unrelated
Now you have 1 tasks in the list.
Got it. I've added this task:
[T][ ] read book
Now you have 2 tasks in the list.
Got it. I've added this task:
[T][ ] return BOOK
Now you have 3 tasks in the list.
Here are the matching tasks in your list:
1.[T][ ] read book
2.[T][ ] return BOOK
OhNo!! ERROR :( --> please provide a keyword to search for. Use: find KEYWORD
Nice! I've marked this task as done:
  [T][X] return BOOK
Noted. I've removed this task:
  [T][ ] read book
Now you have 2 tasks in the list.
OhNo!! ERROR :( --> there is no task numbered 3.
Here are the matching tasks in your list:
1.[T][X] return BOOK
Here are the tasks in your list:
1.[T][ ] unrelated
2.[T][X] return BOOK
```

## Test case: Find free time across midnight after an invalid search

Aim: Verify completed events still occupy time, exact boundary gaps work across midnight, and invalid searches preserve state.

### Inputs

```text
event 讨论 /from 2028-02-29 2300 /to 2028-03-01 0030
mark 1
findfree -1 /from 2028-02-29 2330 /to 2028-03-01 0130
findfree 1 /from 2028-02-29 2330 /to 2028-03-01 0130
findfree 2 /from 2028-02-29 2330 /to 2028-03-01 0130
list
```

### Expected output

```text
Got it. I've added this task:
[E][ ] 讨论 (from: 2028-02-29 2300 to: 2028-03-01 0030)
Now you have 1 tasks in the list.
Nice! I've marked this task as done:
  [E][X] 讨论 (from: 2028-02-29 2300 to: 2028-03-01 0030)
OhNo!! ERROR :( --> please provide the duration as a positive whole number of hours.
The earliest 1-hour free slot is:
  Mar 01 2028 00:30 to Mar 01 2028 01:30
I couldn't find a 2-hour free slot between Feb 29 2028 23:30 and Mar 01 2028 01:30.
Here are the tasks in your list:
1.[E][X] 讨论 (from: 2028-02-29 2300 to: 2028-03-01 0030)
```
