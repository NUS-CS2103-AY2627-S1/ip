# Chausistant UI test plan

## Project setup

- Each test runs in its own temporary working directory, so its save data
  cannot affect other tests or the developer's local task file.
- Java version: Java 25
- Compile command: `javac --release 25 -d <temporary-classes> src/main/java/*.java`
- Run command: `java -cp <temporary-classes> Chausistant`
- Each test case starts a fresh program session and sends its inputs in order.
- The program prints a startup banner. These tests use `Match: contains`, so
  each expected-output block must appear verbatim in the complete console
  output while the banner remains visible in the transcript.
- Testing stops at the first failed case.
- A test may optionally include `### Initial file: <relative path>`,
  `### Initial directory: <relative path>`, or `### Expected file: <relative path>`
  blocks to set up or verify files in that test's working directory.

## Test case: Add a todo task

Aim: Verify that a `todo` command creates a todo task and reports the updated task count.

Match: contains

### Inputs

```text
todo borrow book
bye
```

### Expected output

```text
Got it. I've added this task:
[T][ ] borrow book
Now you have 1 tasks in the list.
Bye. Hope to see you again soon!
```

## Test case: Add a deadline with punctuation

Aim: Verify that a deadline parses a date/time while preserving punctuation in its description.

Match: contains

### Inputs

```text
deadline do homework :-p /by 2/12/2019 1800
bye
```

### Expected output

```text
Got it. I've added this task:
[D][ ] do homework :-p (by: Dec 2 2019 1800)
Now you have 1 tasks in the list.
Bye. Hope to see you again soon!
```

## Test case: Add and mark an event

Aim: Verify event parsing, list formatting, inheritance-based type formatting, and marking a task complete.

Match: contains

### Inputs

```text
event project meeting /from 2/12/2019 1400 /to 2/12/2019 1600
list
mark 1
bye
```

### Expected output

```text
Got it. I've added this task:
[E][ ] project meeting (from: Dec 2 2019 1400 to: Dec 2 2019 1600)
Now you have 1 tasks in the list.
Here are the tasks in your list:
1.[E][ ] project meeting (from: Dec 2 2019 1400 to: Dec 2 2019 1600)
[E][X] project meeting (from: Dec 2 2019 1400 to: Dec 2 2019 1600)
Bye. Hope to see you again soon!
```

## Test case: Handle invalid commands with helpful templates

Aim: Verify that invalid user inputs show a command template when details are missing and the program continues accepting commands.

Match: contains

### Inputs

```text
todo
blah
deadline study
event meeting /from Mon
todo submit assignment
mark
mark one
mark 2
unmark
list now
bye later
bye
```

### Expected output

```text
Oops! Use: todo <task>.
Oops! Unknown command: blah
Oops! Use: deadline <task> /by <date> [HHmm].
Oops! Use: event <task> /from <date> [HHmm] /to <date> [HHmm].
Got it. I've added this task:
[T][ ] submit assignment
Now you have 1 tasks in the list.
Oops! Use: mark <task number>.
Oops! A task number must be a whole number.
Oops! There is no task numbered 2.
Oops! Use: unmark <task number>.
Oops! Use: list.
Oops! Use: bye.
Bye. Hope to see you again soon!
```

## Test case: Keep only valid tasks after malformed task commands

Aim: Verify that malformed task commands between valid commands do not add tasks to the list.

Match: contains

### Inputs

```text
todo read chapter
todo
deadline plan trip /by 28/02/2020 0900
deadline write report
event club meeting /from 2/03/2020 1400 /to 02/03/2020 1500
event study session /from 4pm /to
blah
list
bye
```

### Expected output

```text
Got it. I've added this task:
[T][ ] read chapter
Now you have 1 tasks in the list.
Oops! Use: todo <task>.
Got it. I've added this task:
[D][ ] plan trip (by: Feb 28 2020 0900)
Now you have 2 tasks in the list.
Oops! Use: deadline <task> /by <date> [HHmm].
Got it. I've added this task:
[E][ ] club meeting (from: Mar 2 2020 1400 to: Mar 2 2020 1500)
Now you have 3 tasks in the list.
Oops! Use: event <task> /from <date> [HHmm] /to <date> [HHmm].
Oops! Unknown command: blah
Here are the tasks in your list:
1.[T][ ] read chapter
2.[D][ ] plan trip (by: Feb 28 2020 0900)
3.[E][ ] club meeting (from: Mar 2 2020 1400 to: Mar 2 2020 1500)
Bye. Hope to see you again soon!
```

## Test case: Preserve task statuses after invalid mark commands

Aim: Verify that invalid mark and unmark commands do not alter a task's completion status.

Match: contains

### Inputs

```text
todo first task
todo second task
mark 2
mark 0
unmark 3
unmark two
unmark 2
mark 1 trailing
list
bye
```

### Expected output

```text
Got it. I've added this task:
[T][ ] first task
Now you have 1 tasks in the list.
Got it. I've added this task:
[T][ ] second task
Now you have 2 tasks in the list.
[T][X] second task
Oops! There is no task numbered 0.
Oops! There is no task numbered 3.
Oops! A task number must be a whole number.
[T][ ] second task
Oops! A task number must be a whole number.
Here are the tasks in your list:
1.[T][ ] first task
2.[T][ ] second task
Bye. Hope to see you again soon!
```

## Test case: Continue after invalid list and bye commands

Aim: Verify that command matching is case-insensitive and invalid list or bye commands neither change state nor exit the program.

Match: contains

### Inputs

```text
TODO  buy milk
LIST details
BYE now
list
bye
```

### Expected output

```text
Got it. I've added this task:
[T][ ] buy milk
Now you have 1 tasks in the list.
Oops! Use: list.
Oops! Use: bye.
Here are the tasks in your list:
1.[T][ ] buy milk
Bye. Hope to see you again soon!
```

## Test case: Delete a task without corrupting the list

Aim: Verify that deletion removes the requested task, renumbers the remaining tasks, and leaves the list unchanged after invalid delete commands.

Match: contains

### Inputs

```text
todo keep first
deadline remove this /by 01/04/2020 1200
event keep last /from 01/04/2020 1300 /to 01/04/2020 1400
delete 2
delete 0
delete
todo add after error
delete two
delete 4
list
bye
```

### Expected output

```text
Got it. I've added this task:
[T][ ] keep first
Now you have 1 tasks in the list.
Got it. I've added this task:
[D][ ] remove this (by: Apr 1 2020 1200)
Now you have 2 tasks in the list.
Got it. I've added this task:
[E][ ] keep last (from: Apr 1 2020 1300 to: Apr 1 2020 1400)
Now you have 3 tasks in the list.
Noted. I've removed this task:
[D][ ] remove this (by: Apr 1 2020 1200)
Now you have 2 tasks in the list.
Oops! There is no task numbered 0.
Oops! Use: delete <task number>.
Got it. I've added this task:
[T][ ] add after error
Now you have 3 tasks in the list.
Oops! A task number must be a whole number.
Oops! There is no task numbered 4.
Here are the tasks in your list:
1.[T][ ] keep first
2.[E][ ] keep last (from: Apr 1 2020 1300 to: Apr 1 2020 1400)
3.[T][ ] add after error
Bye. Hope to see you again soon!
```

## Test case: Save each task-list change

Aim: Verify that adding, marking, and deleting tasks completes normally while exercising the save path.

Match: contains

### Inputs

```text
todo read book
deadline return book /by 06/06/2020 1200
event project meeting /from 06/08/2020 1400 /to 06/08/2020 1600
mark 1
delete 2
bye
```

### Expected output

```text
Got it. I've added this task:
[T][ ] read book
Now you have 1 tasks in the list.
Got it. I've added this task:
[D][ ] return book (by: Jun 6 2020 1200)
Now you have 2 tasks in the list.
Got it. I've added this task:
[E][ ] project meeting (from: Aug 6 2020 1400 to: Aug 6 2020 1600)
Now you have 3 tasks in the list.
[T][X] read book
Noted. I've removed this task:
[D][ ] return book (by: Jun 6 2020 1200)
Now you have 2 tasks in the list.
Bye. Hope to see you again soon!
```

### Expected file: data/chausistant.txt

```text
T | 1 | read book
E | 0 | project meeting | 06/08/2020 1400 | 06/08/2020 1600
```

## Test case: Load tasks from a previous session

Aim: Verify that valid todo, deadline, and event entries are restored from the save file at startup.

Match: contains

### Initial file: data/chausistant.txt

```text
T | 1 | read book
D | 0 | return book | 06/06/2020 1200
E | 0 | project meeting | 06/08/2020 1400 | 06/08/2020 1600
```

### Inputs

```text
list
bye
```

### Expected output

```text
Here are the tasks in your list:
1.[T][X] read book
2.[D][ ] return book (by: Jun 6 2020 1200)
3.[E][ ] project meeting (from: Aug 6 2020 1400 to: Aug 6 2020 1600)
Bye. Hope to see you again soon!
```

## Test case: Accept date and time boundary values

Aim: Verify that one- and two-digit dates, midnight, the end of the day, and event ranges across dates are accepted.

Match: contains

### Inputs

```text
deadline midnight /by 2/12/2019 0000
deadline end of day /by 02/12/2019 2359
event overnight /from 31/12/2019 2300 /to 01/01/2020 0030
list
bye
```

### Expected output

```text
Got it. I've added this task:
[D][ ] midnight (by: Dec 2 2019 0000)
Now you have 1 tasks in the list.
Got it. I've added this task:
[D][ ] end of day (by: Dec 2 2019 2359)
Now you have 2 tasks in the list.
Got it. I've added this task:
[E][ ] overnight (from: Dec 31 2019 2300 to: Jan 1 2020 0030)
Now you have 3 tasks in the list.
Here are the tasks in your list:
1.[D][ ] midnight (by: Dec 2 2019 0000)
2.[D][ ] end of day (by: Dec 2 2019 2359)
3.[E][ ] overnight (from: Dec 31 2019 2300 to: Jan 1 2020 0030)
Bye. Hope to see you again soon!
```

## Test case: Accept date-only deadlines and events

Aim: Verify that times are optional, date-only values display without an invented time, and the save file preserves that choice.

Match: contains

### Inputs

```text
deadline submit form /by 2/12/2019
event conference /from 3/12/2019 /to 4/12/2019
event briefing /from 4/12/2019 0900 /to 4/12/2019
list
what's on: 4/12/2019
bye
```

### Expected output

```text
Got it. I've added this task:
[D][ ] submit form (by: Dec 2 2019)
Now you have 1 tasks in the list.
Got it. I've added this task:
[E][ ] conference (from: Dec 3 2019 to: Dec 4 2019)
Now you have 2 tasks in the list.
Got it. I've added this task:
[E][ ] briefing (from: Dec 4 2019 0900 to: Dec 4 2019)
Now you have 3 tasks in the list.
Here are the tasks in your list:
1.[D][ ] submit form (by: Dec 2 2019)
2.[E][ ] conference (from: Dec 3 2019 to: Dec 4 2019)
3.[E][ ] briefing (from: Dec 4 2019 0900 to: Dec 4 2019)
Here are the events and deadlines on Dec 4 2019:
Events:
[E][ ] conference (from: Dec 3 2019 to: Dec 4 2019)
[E][ ] briefing (from: Dec 4 2019 0900 to: Dec 4 2019)
--------------------
Deadlines:
No deadlines on this date.
Bye. Hope to see you again soon!
```

### Expected file: data/chausistant.txt

```text
D | 0 | submit form | 02/12/2019
E | 0 | conference | 03/12/2019 | 04/12/2019
E | 0 | briefing | 04/12/2019 0900 | 04/12/2019
```

## Test case: Restore date-only task details from the save file

Aim: Verify that date-only task details are restored without adding a time during a later session.

Match: contains

### Initial file: data/chausistant.txt

```text
D | 0 | submit form | 02/12/2019
E | 0 | conference | 03/12/2019 | 04/12/2019
```

### Inputs

```text
list
what's on: 04/12/2019
bye
```

### Expected output

```text
Here are the tasks in your list:
1.[D][ ] submit form (by: Dec 2 2019)
2.[E][ ] conference (from: Dec 3 2019 to: Dec 4 2019)
Here are the events and deadlines on Dec 4 2019:
Events:
[E][ ] conference (from: Dec 3 2019 to: Dec 4 2019)
--------------------
Deadlines:
No deadlines on this date.
Bye. Hope to see you again soon!
```

## Test case: Reject invalid date and time values

Aim: Verify that impossible dates, `2400`, five-digit times, and invalid event dates are rejected without adding tasks.

Match: contains

### Inputs

```text
deadline impossible date /by 31/02/2019 1200
deadline invalid hour /by 02/12/2019 2400
deadline with seconds /by 02/12/2019 120000
event impossible end /from 01/01/2020 1200 /to 31/02/2020 1300
list
bye
```

### Expected output

```text
Oops! Use date format DD/MM/YYYY, optionally followed by HHmm, with a valid calendar date.
Oops! Use date format DD/MM/YYYY, optionally followed by HHmm, with a valid calendar date.
Oops! Use date format DD/MM/YYYY, optionally followed by HHmm, with a valid calendar date.
Oops! Use date format DD/MM/YYYY, optionally followed by HHmm, with a valid calendar date.
Here are the tasks in your list:
no tasks for now! go doomscroll
Bye. Hope to see you again soon!
```

## Test case: Show events and deadlines for a date in time order

Aim: Verify that `what's on:` filters by date, sorts events and deadlines independently by time, and places events before the divider.

Match: contains

### Inputs

```text
event afternoon meeting /from 02/12/2019 1400 /to 02/12/2019 1600
deadline late report /by 02/12/2019 1800
event morning meeting /from 2/12/2019 0900 /to 2/12/2019 1000
deadline early report /by 2/12/2019 0800
what's on: 02/12/2019
bye
```

### Expected output

```text
Here are the events and deadlines on Dec 2 2019:
Events:
[E][ ] morning meeting (from: Dec 2 2019 0900 to: Dec 2 2019 1000)
[E][ ] afternoon meeting (from: Dec 2 2019 1400 to: Dec 2 2019 1600)
--------------------
Deadlines:
[D][ ] early report (by: Dec 2 2019 0800)
[D][ ] late report (by: Dec 2 2019 1800)
Bye. Hope to see you again soon!
```

## Test case: Include events that overlap the requested date

Aim: Verify that an event crossing midnight is shown on both dates covered by its interval, while an event outside the date is excluded.

Match: contains

### Inputs

```text
event overnight /from 31/12/2019 2300 /to 01/01/2020 0030
event next evening /from 01/01/2020 1800 /to 01/01/2020 1900
what's on: 01/01/2020
bye
```

### Expected output

```text
Here are the events and deadlines on Jan 1 2020:
Events:
[E][ ] overnight (from: Dec 31 2019 2300 to: Jan 1 2020 0030)
[E][ ] next evening (from: Jan 1 2020 1800 to: Jan 1 2020 1900)
--------------------
Deadlines:
No deadlines on this date.
Bye. Hope to see you again soon!
```

## Test case: Handle dates with no matching tasks and invalid query dates

Aim: Verify that an empty result is reported clearly and an invalid query date returns an error without stopping the chatbot.

Match: contains

### Inputs

```text
what's on
what's on:
what's on: 02/12/2019 extra
what's on: 31/02/2019
what's on: 02/12/2019
bye
```

### Expected output

```text
Oops! Use: what's on: <date>.
Oops! Use: what's on: <date>.
Oops! Use date format DD/MM/YYYY with a valid calendar date.
Oops! Use date format DD/MM/YYYY with a valid calendar date.
Here are the events and deadlines on Dec 2 2019:
Events:
No events on this date.
--------------------
Deadlines:
No deadlines on this date.
Bye. Hope to see you again soon!
```

## Test case: Start with an empty save file

Aim: Verify that an empty save file is treated as an empty task list.

Match: contains

### Initial file: data/chausistant.txt

```text

```

### Inputs

```text
list
bye
```

### Expected output

```text
Here are the tasks in your list:
no tasks for now! go doomscroll
Bye. Hope to see you again soon!
```

## Test case: Preserve separator characters in saved tasks

Aim: Verify that task descriptions containing pipe and backslash characters save without corrupting the file format.

Match: contains

### Inputs

```text
todo prepare | review \ archive
bye
```

### Expected output

```text
Got it. I've added this task:
[T][ ] prepare | review \ archive
Now you have 1 tasks in the list.
Bye. Hope to see you again soon!
```

### Expected file: data/chausistant.txt

```text
T | 0 | prepare \| review \\ archive
```

## Test case: Ignore malformed saved tasks

Aim: Verify that blank and malformed save-file lines do not crash the program or hide valid tasks.

Match: contains

### Initial file: data/chausistant.txt

```text
T | 1 | keep this task
T | 2 | invalid status
D | 0 | missing deadline
E | 0 | missing end | 2pm
Z | 0 | unknown task
T | 0 |
```

### Inputs

```text
list
bye
```

### Expected output

```text
Oops! Ignoring malformed task on line 2: the status must be 0 or 1.
Oops! Ignoring malformed task on line 3: the task has an incorrect number of fields.
Oops! Ignoring malformed task on line 4: the task has an incorrect number of fields.
Oops! Ignoring malformed task on line 5: unknown task type 'Z'.
Oops! Ignoring malformed task on line 6: the todo description is missing.
Here are the tasks in your list:
1.[T][X] keep this task
Bye. Hope to see you again soon!
```

## Test case: Recover when the save path is a directory

Aim: Verify that loading and saving report a clear error and retain an empty in-memory list when the save path is a directory.

Match: contains

### Initial directory: data/chausistant.txt

### Inputs

```text
todo task that cannot be saved
list
bye
```

### Expected output

```text
Oops! I could not load your tasks from data/chausistant.txt.
Oops! I could not save your tasks to data/chausistant.txt.
Here are the tasks in your list:
no tasks for now! go doomscroll
Bye. Hope to see you again soon!
```

## Test case: Find tasks by keyword

Aim: Verify that finding is case-insensitive, retains matching task order, and handles no or missing keywords.

Match: contains

### Initial file: data/chausistant.txt

```text
T | 0 | read Book
D | 0 | return book | 06/06/2020 1200
E | 0 | project meeting | 06/08/2020 1400 | 06/08/2020 1600
```

### Inputs

```text
find BOOK
find receipt
find
bye
```

### Expected output

```text
Here are the matching tasks in your list:
1.[T][ ] read Book
2.[D][ ] return book (by: Jun 6 2020 1200)
Here are the matching tasks in your list:
No matching tasks found.
Oops! Use: find <keyword>.
Bye. Hope to see you again soon!
```

## Test case: Show an empty deadline reminder

Aim: Verify that `remind` reports clearly when no incomplete deadline is due in the next seven days.

Match: contains

### Inputs

```text
remind
bye
```

### Expected output

```text
Here are your upcoming deadlines:
No upcoming deadlines in the next 7 days.
Bye. Hope to see you again soon!
```

## Manual verification: View upcoming deadlines

Aim: Verify that `remind` shows only incomplete deadlines due from the current time through the
next seven days, in due-time order.

Match: contains

Before running this test, replace `<tomorrow>`, `<within-seven-days>`, `<yesterday>`, and
`<eight-days-away>` with valid dates relative to the local date when the test is run.
`<within-seven-days>` must be later than `<tomorrow>`. Use a time later than the current time for
the two upcoming deadlines.

### Suggested console inputs

```text
deadline later deadline /by <within-seven-days> 1800
deadline earlier deadline /by <tomorrow> 0900
deadline overdue deadline /by <yesterday> 1200
deadline outside deadline /by <eight-days-away> 1200
event upcoming event /from <tomorrow> 1000 /to <tomorrow> 1100
todo ordinary task
mark 1
remind
bye
```

### Expected reminder section

```text
Here are your upcoming deadlines:
[D][ ] earlier deadline (by: <tomorrow> 0900)
Bye. Hope to see you again soon!
```

The reminder output must not include the completed first deadline, the overdue deadline, the
deadline more than seven days away, the event, or the todo task.
