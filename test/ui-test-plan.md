# Nova UI Test Plan

Run the cases with the repository-local `test-ui` skill. Each case starts a fresh Nova process. The expected output is the program's complete standard output; comparisons normalize CRLF to LF and ignore trailing newline characters only.

The program is compiled recursively from `src/main/java`, runs with main class `nova.Nova`, and requires Java 25. The test runner stops immediately after the first compilation, runtime, or output-comparison failure.

## TC-01: Add and list all task types

### Aim

Verify that todos, deadlines, and events are stored polymorphically, displayed in order with their type icons, and format typed dates and times readably.

### Input

```text
todo read book
deadline return book /by 2/12/2019 1800
event project meeting /from 2019-12-03 1400 /to 2019-12-03 1600
deadline do homework /by 2019-12-04
list
bye
```

### Expected output

```text
____________________________________________________________
 _   _                 
| \ | | _____   ____ _ 
|  \| |/ _ \ \ / / _` |
| |\  | (_) \ V / (_| |
|_| \_|\___/ \_/ \__,_|
Nova online! Your mission navigator is ready.
What shall we launch today?
____________________________________________________________
____________________________________________________________
 Mission logged and ready for launch:
   [T][ ] read book
 You now have 1 mission in orbit.
____________________________________________________________
____________________________________________________________
 Mission logged and ready for launch:
   [D][ ] return book (by: Dec 02 2019, 6:00 PM)
 You now have 2 missions in orbit.
____________________________________________________________
____________________________________________________________
 Mission logged and ready for launch:
   [E][ ] project meeting (from: Dec 03 2019, 2:00 PM to: Dec 03 2019, 4:00 PM)
 You now have 3 missions in orbit.
____________________________________________________________
____________________________________________________________
 Mission logged and ready for launch:
   [D][ ] do homework (by: Dec 04 2019)
 You now have 4 missions in orbit.
____________________________________________________________
____________________________________________________________
 Here's your mission log:
 1.[T][ ] read book
 2.[D][ ] return book (by: Dec 02 2019, 6:00 PM)
 3.[E][ ] project meeting (from: Dec 03 2019, 2:00 PM to: Dec 03 2019, 4:00 PM)
 4.[D][ ] do homework (by: Dec 04 2019)
____________________________________________________________
____________________________________________________________
 Returning to base. Until our next mission!
____________________________________________________________
```

## TC-02: Mark a deadline as done

### Aim

Verify that `mark` works through the shared `Task` type and preserves deadline-specific display details.

### Input

```text
todo read book
deadline return book /by 2019-12-02
mark 2
list
bye
```

### Expected output

```text
____________________________________________________________
 _   _                 
| \ | | _____   ____ _ 
|  \| |/ _ \ \ / / _` |
| |\  | (_) \ V / (_| |
|_| \_|\___/ \_/ \__,_|
Nova online! Your mission navigator is ready.
What shall we launch today?
____________________________________________________________
____________________________________________________________
 Mission logged and ready for launch:
   [T][ ] read book
 You now have 1 mission in orbit.
____________________________________________________________
____________________________________________________________
 Mission logged and ready for launch:
   [D][ ] return book (by: Dec 02 2019)
 You now have 2 missions in orbit.
____________________________________________________________
____________________________________________________________
 Mission accomplished! Stellar work:
   [D][X] return book (by: Dec 02 2019)
____________________________________________________________
____________________________________________________________
 Here's your mission log:
 1.[T][ ] read book
 2.[D][X] return book (by: Dec 02 2019)
____________________________________________________________
____________________________________________________________
 Returning to base. Until our next mission!
____________________________________________________________
```

## TC-03: Reverse an event's done status

### Aim

Verify that `unmark` changes a completed event back to not done while preserving its time range.

### Input

```text
event orientation week /from 4/10/2019 /to 11/10/2019
mark 1
unmark 1
list
bye
```

### Expected output

```text
____________________________________________________________
 _   _                 
| \ | | _____   ____ _ 
|  \| |/ _ \ \ / / _` |
| |\  | (_) \ V / (_| |
|_| \_|\___/ \_/ \__,_|
Nova online! Your mission navigator is ready.
What shall we launch today?
____________________________________________________________
____________________________________________________________
 Mission logged and ready for launch:
   [E][ ] orientation week (from: Oct 04 2019 to: Oct 11 2019)
 You now have 1 mission in orbit.
____________________________________________________________
____________________________________________________________
 Mission accomplished! Stellar work:
   [E][X] orientation week (from: Oct 04 2019 to: Oct 11 2019)
____________________________________________________________
____________________________________________________________
 Mission reopened and back on course:
   [E][ ] orientation week (from: Oct 04 2019 to: Oct 11 2019)
____________________________________________________________
____________________________________________________________
 Here's your mission log:
 1.[E][ ] orientation week (from: Oct 04 2019 to: Oct 11 2019)
____________________________________________________________
____________________________________________________________
 Returning to base. Until our next mission!
____________________________________________________________
```

## TC-04: Reject empty and unknown commands

### Aim

Verify that Nova uses specific exception messages for an empty todo, an unknown command, and a blank input while continuing to accept later commands.

### Input

```text
todo
blah

bye
```

### Expected output

```text
____________________________________________________________
 _   _                 
| \ | | _____   ____ _ 
|  \| |/ _ \ \ / / _` |
| |\  | (_) \ V / (_| |
|_| \_|\___/ \_/ \__,_|
Nova online! Your mission navigator is ready.
What shall we launch today?
____________________________________________________________
____________________________________________________________
 NAVIGATION ALERT: A todo needs a description. Try: todo <description>.
____________________________________________________________
____________________________________________________________
 NAVIGATION ALERT: I don't recognize that command. Start with todo, deadline, event, find, on, list, mark, unmark, delete, edit, or bye.
____________________________________________________________
____________________________________________________________
 NAVIGATION ALERT: You entered a blank command. Try todo, deadline, event, find, on, list, mark, unmark, delete, edit, or bye.
____________________________________________________________
____________________________________________________________
 Returning to base. Until our next mission!
____________________________________________________________
```

## TC-07: Delete a task and renumber the list

### Aim

Verify that `delete` removes the selected task, reports it, updates the task count, and shifts later task numbers down.

### Input

```text
todo read book
deadline return book /by 2019-06-06
event project meeting /from 2019-08-06 1400 /to 2019-08-06 1600
todo join sports club
todo borrow book
mark 1
mark 2
mark 4
delete 3
list
bye
```

### Expected output

```text
____________________________________________________________
 _   _                 
| \ | | _____   ____ _ 
|  \| |/ _ \ \ / / _` |
| |\  | (_) \ V / (_| |
|_| \_|\___/ \_/ \__,_|
Nova online! Your mission navigator is ready.
What shall we launch today?
____________________________________________________________
____________________________________________________________
 Mission logged and ready for launch:
   [T][ ] read book
 You now have 1 mission in orbit.
____________________________________________________________
____________________________________________________________
 Mission logged and ready for launch:
   [D][ ] return book (by: Jun 06 2019)
 You now have 2 missions in orbit.
____________________________________________________________
____________________________________________________________
 Mission logged and ready for launch:
   [E][ ] project meeting (from: Aug 06 2019, 2:00 PM to: Aug 06 2019, 4:00 PM)
 You now have 3 missions in orbit.
____________________________________________________________
____________________________________________________________
 Mission logged and ready for launch:
   [T][ ] join sports club
 You now have 4 missions in orbit.
____________________________________________________________
____________________________________________________________
 Mission logged and ready for launch:
   [T][ ] borrow book
 You now have 5 missions in orbit.
____________________________________________________________
____________________________________________________________
 Mission accomplished! Stellar work:
   [T][X] read book
____________________________________________________________
____________________________________________________________
 Mission accomplished! Stellar work:
   [D][X] return book (by: Jun 06 2019)
____________________________________________________________
____________________________________________________________
 Mission accomplished! Stellar work:
   [T][X] join sports club
____________________________________________________________
____________________________________________________________
 Mission removed from the flight plan:
   [E][ ] project meeting (from: Aug 06 2019, 2:00 PM to: Aug 06 2019, 4:00 PM)
 You now have 4 missions in orbit.
____________________________________________________________
____________________________________________________________
 Here's your mission log:
 1.[T][X] read book
 2.[D][X] return book (by: Jun 06 2019)
 3.[T][X] join sports club
 4.[T][ ] borrow book
____________________________________________________________
____________________________________________________________
 Returning to base. Until our next mission!
____________________________________________________________
```

## TC-08: Explain invalid delete commands

### Aim

Verify that missing, non-numeric, empty-list, and out-of-range delete task numbers produce actionable guidance without changing the list.

### Input

```text
delete
delete first
delete 1
todo read book
delete 0
delete 2
list
bye
```

### Expected output

```text
____________________________________________________________
 _   _                 
| \ | | _____   ____ _ 
|  \| |/ _ \ \ / / _` |
| |\  | (_) \ V / (_| |
|_| \_|\___/ \_/ \__,_|
Nova online! Your mission navigator is ready.
What shall we launch today?
____________________________________________________________
____________________________________________________________
 NAVIGATION ALERT: Tell me which task to delete. Try: delete <task number>.
____________________________________________________________
____________________________________________________________
 NAVIGATION ALERT: The task number after delete must be a whole number, for example: delete 1.
____________________________________________________________
____________________________________________________________
 NAVIGATION ALERT: There are no tasks to delete yet. Add a task first.
____________________________________________________________
____________________________________________________________
 Mission logged and ready for launch:
   [T][ ] read book
 You now have 1 mission in orbit.
____________________________________________________________
____________________________________________________________
 NAVIGATION ALERT: Task 0 does not exist. Choose a number from 1 to 1.
____________________________________________________________
____________________________________________________________
 NAVIGATION ALERT: Task 2 does not exist. Choose a number from 1 to 1.
____________________________________________________________
____________________________________________________________
 Here's your mission log:
 1.[T][ ] read book
____________________________________________________________
____________________________________________________________
 Returning to base. Until our next mission!
____________________________________________________________
```

## TC-05: Explain malformed deadlines and events

### Aim

Verify that every missing or misplaced deadline and event field produces guidance specific to that field.

### Input

```text
deadline
deadline return book
deadline /by Sunday
deadline return book /by
event
event meeting /to 4pm
event meeting /from 2pm
event meeting /to 4pm /from 2pm
event /from 2pm /to 4pm
event meeting /from /to 4pm
event meeting /from 2pm /to
bye
```

### Expected output

```text
____________________________________________________________
 _   _                 
| \ | | _____   ____ _ 
|  \| |/ _ \ \ / / _` |
| |\  | (_) \ V / (_| |
|_| \_|\___/ \_/ \__,_|
Nova online! Your mission navigator is ready.
What shall we launch today?
____________________________________________________________
____________________________________________________________
 NAVIGATION ALERT: A deadline needs a description. Try: deadline <description> /by <date or time>.
____________________________________________________________
____________________________________________________________
 NAVIGATION ALERT: A deadline needs a /by date or time. Try: deadline return book /by <date or time>.
____________________________________________________________
____________________________________________________________
 NAVIGATION ALERT: A deadline needs a description before /by.
____________________________________________________________
____________________________________________________________
 NAVIGATION ALERT: The /by field cannot be empty. Add a date or time after /by.
____________________________________________________________
____________________________________________________________
 NAVIGATION ALERT: An event needs a description and a time range. Try: event <description> /from <start> /to <end>.
____________________________________________________________
____________________________________________________________
 NAVIGATION ALERT: An event needs a /from start date or time.
____________________________________________________________
____________________________________________________________
 NAVIGATION ALERT: An event needs a /to end date or time.
____________________________________________________________
____________________________________________________________
 NAVIGATION ALERT: Put /from before /to. Try: event <description> /from <start> /to <end>.
____________________________________________________________
____________________________________________________________
 NAVIGATION ALERT: An event needs a description before /from.
____________________________________________________________
____________________________________________________________
 NAVIGATION ALERT: The /from field cannot be empty. Add a start date or time after /from.
____________________________________________________________
____________________________________________________________
 NAVIGATION ALERT: The /to field cannot be empty. Add an end date or time after /to.
____________________________________________________________
____________________________________________________________
 Returning to base. Until our next mission!
____________________________________________________________
```

## TC-06: Explain invalid mark and unmark commands

### Aim

Verify that missing, non-numeric, empty-list, and out-of-range task numbers each produce actionable guidance.

### Input

```text
mark
unmark
mark first
unmark 1
todo read book
mark 0
mark 2
unmark two
bye
```

### Expected output

```text
____________________________________________________________
 _   _                 
| \ | | _____   ____ _ 
|  \| |/ _ \ \ / / _` |
| |\  | (_) \ V / (_| |
|_| \_|\___/ \_/ \__,_|
Nova online! Your mission navigator is ready.
What shall we launch today?
____________________________________________________________
____________________________________________________________
 NAVIGATION ALERT: Tell me which task to mark. Try: mark <task number>.
____________________________________________________________
____________________________________________________________
 NAVIGATION ALERT: Tell me which task to unmark. Try: unmark <task number>.
____________________________________________________________
____________________________________________________________
 NAVIGATION ALERT: The task number after mark must be a whole number, for example: mark 1.
____________________________________________________________
____________________________________________________________
 NAVIGATION ALERT: There are no tasks to unmark yet. Add a task first.
____________________________________________________________
____________________________________________________________
 Mission logged and ready for launch:
   [T][ ] read book
 You now have 1 mission in orbit.
____________________________________________________________
____________________________________________________________
 NAVIGATION ALERT: Task 0 does not exist. Choose a number from 1 to 1.
____________________________________________________________
____________________________________________________________
 NAVIGATION ALERT: Task 2 does not exist. Choose a number from 1 to 1.
____________________________________________________________
____________________________________________________________
 NAVIGATION ALERT: The task number after unmark must be a whole number, for example: unmark 1.
____________________________________________________________
____________________________________________________________
 Returning to base. Until our next mission!
____________________________________________________________
```

## TC-09: Save the task list after every change

### Aim

Verify that Nova creates a missing data folder and file, then rewrites the file after adding, marking, and deleting tasks.

### Input

```text
todo read book
deadline return book /by 2019-06-06
mark 2
delete 1
bye
```

### Expected output

```text
____________________________________________________________
 _   _                 
| \ | | _____   ____ _ 
|  \| |/ _ \ \ / / _` |
| |\  | (_) \ V / (_| |
|_| \_|\___/ \_/ \__,_|
Nova online! Your mission navigator is ready.
What shall we launch today?
____________________________________________________________
____________________________________________________________
 Mission logged and ready for launch:
   [T][ ] read book
 You now have 1 mission in orbit.
____________________________________________________________
____________________________________________________________
 Mission logged and ready for launch:
   [D][ ] return book (by: Jun 06 2019)
 You now have 2 missions in orbit.
____________________________________________________________
____________________________________________________________
 Mission accomplished! Stellar work:
   [D][X] return book (by: Jun 06 2019)
____________________________________________________________
____________________________________________________________
 Mission removed from the flight plan:
   [T][ ] read book
 You now have 1 mission in orbit.
____________________________________________________________
____________________________________________________________
 Returning to base. Until our next mission!
____________________________________________________________
```

### Expected data file

```text
D | 1 | return book | 2019-06-06
```

## TC-10: Load saved tasks when Nova starts

### Aim

Verify that Nova loads saved todos, deadlines, and events in order and restores each task's completion state.

### Initial data file

```text
T | 1 | read book
D | 0 | return book | 2019-06-06
E | 1 | project meeting | 2019-08-06 1400 | 2019-08-06 1600
```

### Input

```text
list
bye
```

### Expected output

```text
____________________________________________________________
 _   _                 
| \ | | _____   ____ _ 
|  \| |/ _ \ \ / / _` |
| |\  | (_) \ V / (_| |
|_| \_|\___/ \_/ \__,_|
Nova online! Your mission navigator is ready.
What shall we launch today?
____________________________________________________________
____________________________________________________________
 Here's your mission log:
 1.[T][X] read book
 2.[D][ ] return book (by: Jun 06 2019)
 3.[E][X] project meeting (from: Aug 06 2019, 2:00 PM to: Aug 06 2019, 4:00 PM)
____________________________________________________________
____________________________________________________________
 Returning to base. Until our next mission!
____________________________________________________________
```

## TC-11: Recover from corrupted saved data

### Aim

Verify that an invalid completion state produces an actionable startup warning and Nova continues with an empty task list.

### Initial data file

```text
T | yes | read book
```

### Input

```text
list
bye
```

### Expected output

```text
____________________________________________________________
 _   _                 
| \ | | _____   ____ _ 
|  \| |/ _ \ \ / / _` |
| |\  | (_) \ V / (_| |
|_| \_|\___/ \_/ \__,_|
Nova online! Your mission navigator is ready.
What shall we launch today?
____________________________________________________________
 NAVIGATION ALERT: I couldn't load your saved tasks because line 1 is invalid: the completion state must be 0 or 1. Starting with an empty task list.
____________________________________________________________
____________________________________________________________
 Here's your mission log:
____________________________________________________________
____________________________________________________________
 Returning to base. Until our next mission!
____________________________________________________________
```

## TC-12: Recover when the data file cannot be written

### Aim

Verify that read and write failures are explained without crashing and a failed addition is rolled back in memory.

### Initial data path

```text
directory
```

### Input

```text
todo read book
list
bye
```

### Expected output

```text
____________________________________________________________
 _   _                 
| \ | | _____   ____ _ 
|  \| |/ _ \ \ / / _` |
| |\  | (_) \ V / (_| |
|_| \_|\___/ \_/ \__,_|
Nova online! Your mission navigator is ready.
What shall we launch today?
____________________________________________________________
 NAVIGATION ALERT: I couldn't read the task data file. Starting with an empty task list.
____________________________________________________________
____________________________________________________________
 NAVIGATION ALERT: I couldn't save the task data file. Your latest change was not kept.
____________________________________________________________
____________________________________________________________
 Here's your mission log:
____________________________________________________________
____________________________________________________________
 Returning to base. Until our next mission!
____________________________________________________________
```

## TC-13: Preserve storage separator characters in task text

### Aim

Verify that escaped pipes and backslashes in task descriptions load correctly and remain escaped when the updated task list is saved again.

### Initial data file

```text
D | 0 | discuss \| review \\ notes | 2019-08-09 1700
```

### Input

```text
mark 1
list
bye
```

### Expected output

```text
____________________________________________________________
 _   _                 
| \ | | _____   ____ _ 
|  \| |/ _ \ \ / / _` |
| |\  | (_) \ V / (_| |
|_| \_|\___/ \_/ \__,_|
Nova online! Your mission navigator is ready.
What shall we launch today?
____________________________________________________________
____________________________________________________________
 Mission accomplished! Stellar work:
   [D][X] discuss | review \ notes (by: Aug 09 2019, 5:00 PM)
____________________________________________________________
____________________________________________________________
 Here's your mission log:
 1.[D][X] discuss | review \ notes (by: Aug 09 2019, 5:00 PM)
____________________________________________________________
____________________________________________________________
 Returning to base. Until our next mission!
____________________________________________________________
```

### Expected data file

```text
D | 1 | discuss \| review \\ notes | 2019-08-09 1700
```

## TC-14: Roll back every task mutation when saving fails

### Aim

Verify that failed mark, unmark, delete, and edit saves restore the original in-memory task states and list order.

### Initial data file

```text
T | 0 | read book
D | 1 | return book | 2019-12-02
```

### Initial data path

```text
read-only-directory
```

### Input

```text
mark 1
unmark 2
delete 1
edit 1 /description read novel
list
bye
```

### Expected output

```text
____________________________________________________________
 _   _                 
| \ | | _____   ____ _ 
|  \| |/ _ \ \ / / _` |
| |\  | (_) \ V / (_| |
|_| \_|\___/ \_/ \__,_|
Nova online! Your mission navigator is ready.
What shall we launch today?
____________________________________________________________
____________________________________________________________
 NAVIGATION ALERT: I couldn't save the task data file. Your latest change was not kept.
____________________________________________________________
____________________________________________________________
 NAVIGATION ALERT: I couldn't save the task data file. Your latest change was not kept.
____________________________________________________________
____________________________________________________________
 NAVIGATION ALERT: I couldn't save the task data file. Your latest change was not kept.
____________________________________________________________
____________________________________________________________
 NAVIGATION ALERT: I couldn't save the task data file. Your latest change was not kept.
____________________________________________________________
____________________________________________________________
 Here's your mission log:
 1.[T][ ] read book
 2.[D][X] return book (by: Dec 02 2019)
____________________________________________________________
____________________________________________________________
 Returning to base. Until our next mission!
____________________________________________________________
```

## TC-17: Edit one task detail while preserving all others

### Aim

Verify that description, date-only, and time-only edits preserve task types, completion status,
list positions, and unspecified values, and that edited values are persisted.

### Input

```text
todo read book
deadline return book /by 2019-06-06 1800
event project meeting /from 2019-08-06 1400 /to 2019-08-06 1600
mark 2
edit 1 /description read novel
edit 2 /by 2019-06-07
edit 3 /to 1700
list
bye
```

### Expected output

```text
____________________________________________________________
 _   _                 
| \ | | _____   ____ _ 
|  \| |/ _ \ \ / / _` |
| |\  | (_) \ V / (_| |
|_| \_|\___/ \_/ \__,_|
Nova online! Your mission navigator is ready.
What shall we launch today?
____________________________________________________________
____________________________________________________________
 Mission logged and ready for launch:
   [T][ ] read book
 You now have 1 mission in orbit.
____________________________________________________________
____________________________________________________________
 Mission logged and ready for launch:
   [D][ ] return book (by: Jun 06 2019, 6:00 PM)
 You now have 2 missions in orbit.
____________________________________________________________
____________________________________________________________
 Mission logged and ready for launch:
   [E][ ] project meeting (from: Aug 06 2019, 2:00 PM to: Aug 06 2019, 4:00 PM)
 You now have 3 missions in orbit.
____________________________________________________________
____________________________________________________________
 Mission accomplished! Stellar work:
   [D][X] return book (by: Jun 06 2019, 6:00 PM)
____________________________________________________________
____________________________________________________________
 Flight plan updated:
   Before: [T][ ] read book
   After:  [T][ ] read novel
____________________________________________________________
____________________________________________________________
 Flight plan updated:
   Before: [D][X] return book (by: Jun 06 2019, 6:00 PM)
   After:  [D][X] return book (by: Jun 07 2019)
____________________________________________________________
____________________________________________________________
 Flight plan updated:
   Before: [E][ ] project meeting (from: Aug 06 2019, 2:00 PM to: Aug 06 2019, 4:00 PM)
   After:  [E][ ] project meeting (from: Aug 06 2019, 2:00 PM to: Aug 06 2019, 5:00 PM)
____________________________________________________________
____________________________________________________________
 Here's your mission log:
 1.[T][ ] read novel
 2.[D][X] return book (by: Jun 07 2019)
 3.[E][ ] project meeting (from: Aug 06 2019, 2:00 PM to: Aug 06 2019, 5:00 PM)
____________________________________________________________
____________________________________________________________
 Returning to base. Until our next mission!
____________________________________________________________
```

### Expected data file

```text
T | 0 | read novel
D | 1 | return book | 2019-06-07
E | 0 | project meeting | 2019-08-06 1400 | 2019-08-06 1700
```

## TC-18: Explain invalid edit commands

### Aim

Verify that malformed edits, unsupported fields, invalid times, and invalid event ranges produce
actionable errors without changing any tasks.

### Input

```text
todo read book
deadline return book /by 2019-06-06
event meeting /from 2019-08-06 1400 /to 2019-08-06 1600
edit
edit first /description read novel
edit 1
edit 4 /description missing task
edit 1 /to 1700
edit 2 /from 2019-06-07
edit 3 /from 1700
edit 3 /to 2500
edit 3 /from 2019-08-06 1300 /to 2019-08-06 1700
list
bye
```

### Expected output

```text
____________________________________________________________
 _   _                 
| \ | | _____   ____ _ 
|  \| |/ _ \ \ / / _` |
| |\  | (_) \ V / (_| |
|_| \_|\___/ \_/ \__,_|
Nova online! Your mission navigator is ready.
What shall we launch today?
____________________________________________________________
____________________________________________________________
 Mission logged and ready for launch:
   [T][ ] read book
 You now have 1 mission in orbit.
____________________________________________________________
____________________________________________________________
 Mission logged and ready for launch:
   [D][ ] return book (by: Jun 06 2019)
 You now have 2 missions in orbit.
____________________________________________________________
____________________________________________________________
 Mission logged and ready for launch:
   [E][ ] meeting (from: Aug 06 2019, 2:00 PM to: Aug 06 2019, 4:00 PM)
 You now have 3 missions in orbit.
____________________________________________________________
____________________________________________________________
 NAVIGATION ALERT: Tell me which task to edit. Try: edit <task number> <field> <new value>.
____________________________________________________________
____________________________________________________________
 NAVIGATION ALERT: The task number after edit must be a whole number, for example: edit 1.
____________________________________________________________
____________________________________________________________
 NAVIGATION ALERT: Tell me which field to edit in task 1. Use /description, /by, /from, or /to.
____________________________________________________________
____________________________________________________________
 NAVIGATION ALERT: Task 4 does not exist. Choose a number from 1 to 3.
____________________________________________________________
____________________________________________________________
 NAVIGATION ALERT: A todo can only edit /description.
____________________________________________________________
____________________________________________________________
 NAVIGATION ALERT: A deadline can only edit /description or /by.
____________________________________________________________
____________________________________________________________
 NAVIGATION ALERT: An event's /to date/time cannot be before its /from date/time.
____________________________________________________________
____________________________________________________________
 NAVIGATION ALERT: The /to time must be a real time in HHmm format, for example: 1700.
____________________________________________________________
____________________________________________________________
 NAVIGATION ALERT: Edit one field at a time. Use a separate edit command for each field.
____________________________________________________________
____________________________________________________________
 Here's your mission log:
 1.[T][ ] read book
 2.[D][ ] return book (by: Jun 06 2019)
 3.[E][ ] meeting (from: Aug 06 2019, 2:00 PM to: Aug 06 2019, 4:00 PM)
____________________________________________________________
____________________________________________________________
 Returning to base. Until our next mission!
____________________________________________________________
```

## TC-15: Find scheduled tasks by date

### Aim

Verify that Nova parses real dates and times into typed values, finds deadlines and multi-day events on a requested date, excludes todos, reports an empty result, and explains invalid dates or ranges.

### Input

```text
deadline return book /by 2/12/2019 1800
event conference /from 1/12/2019 /to 3/12/2019
todo undated
on 2/12/2019
on 4/12/2019
deadline impossible /by 31/2/2019
event backwards /from 2019-12-03 /to 2019-12-02
on
on 2019-13-01
bye
```

### Expected output

```text
____________________________________________________________
 _   _                 
| \ | | _____   ____ _ 
|  \| |/ _ \ \ / / _` |
| |\  | (_) \ V / (_| |
|_| \_|\___/ \_/ \__,_|
Nova online! Your mission navigator is ready.
What shall we launch today?
____________________________________________________________
____________________________________________________________
 Mission logged and ready for launch:
   [D][ ] return book (by: Dec 02 2019, 6:00 PM)
 You now have 1 mission in orbit.
____________________________________________________________
____________________________________________________________
 Mission logged and ready for launch:
   [E][ ] conference (from: Dec 01 2019 to: Dec 03 2019)
 You now have 2 missions in orbit.
____________________________________________________________
____________________________________________________________
 Mission logged and ready for launch:
   [T][ ] undated
 You now have 3 missions in orbit.
____________________________________________________________
____________________________________________________________
 Missions scheduled for Dec 02 2019:
 1.[D][ ] return book (by: Dec 02 2019, 6:00 PM)
 2.[E][ ] conference (from: Dec 01 2019 to: Dec 03 2019)
____________________________________________________________
____________________________________________________________
 Missions scheduled for Dec 04 2019:
 No timed missions are in orbit for this date.
____________________________________________________________
____________________________________________________________
 NAVIGATION ALERT: The /by date/time must be a real date in yyyy-MM-dd or d/M/yyyy format, optionally followed by HHmm, for example: 2019-12-02 1800.
____________________________________________________________
____________________________________________________________
 NAVIGATION ALERT: An event's /to date/time cannot be before its /from date/time.
____________________________________________________________
____________________________________________________________
 NAVIGATION ALERT: Tell me which date to search. Try: on 2019-12-02.
____________________________________________________________
____________________________________________________________
 NAVIGATION ALERT: The date after on must be a real date in yyyy-MM-dd or d/M/yyyy format, for example: on 2019-12-02.
____________________________________________________________
____________________________________________________________
 Returning to base. Until our next mission!
____________________________________________________________
```

## TC-16: Find tasks by description keyword

### Aim

Verify that `find` searches only task descriptions without regard to letter case, retains original task numbers,
reports no matches, and explains a missing keyword.

### Input

```text
todo read book
deadline return BOOK /by 2019-06-06
event project meeting /from 2019-08-06 1400 /to 2019-08-06 1600
mark 1
mark 2
find BoOk
find 2019
find
bye
```

### Expected output

```text
____________________________________________________________
 _   _                 
| \ | | _____   ____ _ 
|  \| |/ _ \ \ / / _` |
| |\  | (_) \ V / (_| |
|_| \_|\___/ \_/ \__,_|
Nova online! Your mission navigator is ready.
What shall we launch today?
____________________________________________________________
____________________________________________________________
 Mission logged and ready for launch:
   [T][ ] read book
 You now have 1 mission in orbit.
____________________________________________________________
____________________________________________________________
 Mission logged and ready for launch:
   [D][ ] return BOOK (by: Jun 06 2019)
 You now have 2 missions in orbit.
____________________________________________________________
____________________________________________________________
 Mission logged and ready for launch:
   [E][ ] project meeting (from: Aug 06 2019, 2:00 PM to: Aug 06 2019, 4:00 PM)
 You now have 3 missions in orbit.
____________________________________________________________
____________________________________________________________
 Mission accomplished! Stellar work:
   [T][X] read book
____________________________________________________________
____________________________________________________________
 Mission accomplished! Stellar work:
   [D][X] return BOOK (by: Jun 06 2019)
____________________________________________________________
____________________________________________________________
 Scan complete. Here are the matching missions:
 1.[T][X] read book
 2.[D][X] return BOOK (by: Jun 06 2019)
____________________________________________________________
____________________________________________________________
 Scan complete. Here are the matching missions:
 No matching missions detected.
____________________________________________________________
____________________________________________________________
 NAVIGATION ALERT: Tell me what to find. Try: find <keyword>.
____________________________________________________________
____________________________________________________________
 Returning to base. Until our next mission!
____________________________________________________________
```
