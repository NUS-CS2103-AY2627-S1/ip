# Console UI test plan

Each test case runs in a fresh Lumi process and an isolated temporary working folder. Expected response blocks correspond to the input commands in order and are separated by a line containing only `---`. The test runner checks the surrounding divider lines separately. A case can provide `Initial data` to prepare its data file and `Expected data` to check the final saved records.

## TC-01: Mixed task types and status changes

Aim: Verify that to-dos, deadlines, and events are added polymorphically and retain their type-specific display when marked and unmarked.

### Inputs

```text
todo borrow book
deadline return book /by 2019-10-15
event project meeting /from 2019-10-16 1400 /to 2019-10-16 1600
mark 1
list
unmark 1
list
bye
```

### Expected outputs

```text
 It's on the map. I've added this task:
   [T][ ] borrow book
 Your map now holds 1 task.
---
 It's on the map. I've added this task:
   [D][ ] return book (by: Oct 15 2019)
 Your map now holds 2 tasks.
---
 It's on the map. I've added this task:
   [E][ ] project meeting (from: Oct 16 2019, 2:00PM to: Oct 16 2019, 4:00PM)
 Your map now holds 3 tasks.
---
 A little brighter. This task is complete:
   [T][X] borrow book
---
 Here's your current constellation:
 1.[T][X] borrow book
 2.[D][ ] return book (by: Oct 15 2019)
 3.[E][ ] project meeting (from: Oct 16 2019, 2:00PM to: Oct 16 2019, 4:00PM)
---
 Back in orbit. This task is active again:
   [T][ ] borrow book
---
 Here's your current constellation:
 1.[T][ ] borrow book
 2.[D][ ] return book (by: Oct 15 2019)
 3.[E][ ] project meeting (from: Oct 16 2019, 2:00PM to: Oct 16 2019, 4:00PM)
---
 Until next time. Your tasks are safe here.
```

## TC-02: Parsed deadline dates and times

Aim: Verify strict calendar validation, ISO and day/month/year inputs, friendly display formatting, state preservation after invalid dates, and canonical date-time storage.

### Inputs

```text
deadline impossible date /by 2019-02-29
deadline wrong separator /by 2019/10/15
deadline invalid time /by 2019-10-15 2400
deadline do homework /by 2019-10-15
deadline return book /by 2/12/2019 1800
list
bye
```

### Expected outputs

```text
 Signal unclear: use a date like 2019-10-15 or 2/12/2019, optionally followed by a 24-hour time such as 1800.
---
 Signal unclear: use a date like 2019-10-15 or 2/12/2019, optionally followed by a 24-hour time such as 1800.
---
 Signal unclear: use a date like 2019-10-15 or 2/12/2019, optionally followed by a 24-hour time such as 1800.
---
 It's on the map. I've added this task:
   [D][ ] do homework (by: Oct 15 2019)
 Your map now holds 1 task.
---
 It's on the map. I've added this task:
   [D][ ] return book (by: Dec 02 2019, 6:00PM)
 Your map now holds 2 tasks.
---
 Here's your current constellation:
 1.[D][ ] do homework (by: Oct 15 2019)
 2.[D][ ] return book (by: Dec 02 2019, 6:00PM)
---
 Until next time. Your tasks are safe here.
```

### Expected data

```text
D | 0 | do homework | 2019-10-15T00:00:00
D | 0 | return book | 2019-12-02T18:00:00
```

## TC-03: Invalid task creation commands preserve state

Aim: Verify specific explanations for malformed or unknown inputs and confirm that rejected task commands do not add tasks.

### Inputs

```text
<EMPTY>
todo
blah
list later
bye now
deadline return book
deadline /by Sunday
deadline return book /by
deadline return book /bypass
event project meeting
event project meeting /from Mon /today
event /from Mon /to Tue
event project meeting /from /to Tue
event project meeting /from Mon /to
deadline impossible date /by 2019-02-29
todo valid task
event bad event /from 2019-10-15 /to tomorrow
list
bye
```

### Expected outputs

```text
 Signal unclear: please enter a command.
---
 Signal unclear: a todo needs a description. Try: todo <description>
---
 Signal unclear: I don't recognize that command. Try todo, deadline, event, list, find, mark, unmark, delete, snooze, or bye.
---
 Signal unclear: I don't recognize that command. Try todo, deadline, event, list, find, mark, unmark, delete, snooze, or bye.
---
 Signal unclear: I don't recognize that command. Try todo, deadline, event, list, find, mark, unmark, delete, snooze, or bye.
---
 Signal unclear: a deadline needs a due date. Try: deadline <description> /by <when>
---
 Signal unclear: a deadline needs a description before /by.
---
 Signal unclear: the /by value cannot be empty.
---
 Signal unclear: a deadline needs a due date. Try: deadline <description> /by <when>
---
 Signal unclear: an event needs start and end details. Try: event <description> /from <start> /to <end>
---
 Signal unclear: an event needs start and end details. Try: event <description> /from <start> /to <end>
---
 Signal unclear: an event needs a description before /from.
---
 Signal unclear: the /from value cannot be empty.
---
 Signal unclear: the /to value cannot be empty.
---
 Signal unclear: use a date like 2019-10-15 or 2/12/2019, optionally followed by a 24-hour time such as 1800.
---
 It's on the map. I've added this task:
   [T][ ] valid task
 Your map now holds 1 task.
---
 Signal unclear: use a date like 2019-10-15 or 2/12/2019, optionally followed by a 24-hour time such as 1800.
---
 Here's your current constellation:
 1.[T][ ] valid task
---
 Until next time. Your tasks are safe here.
```

## TC-04: Delete tasks and renumber the list

Aim: Verify deleting a task returns the removed task, decreases the count, and shifts later task numbers without changing their details or statuses.

### Inputs

```text
todo read book
deadline return book /by 2019-06-06
event project meeting /from 2019-08-06 1400 /to 2019-08-06 1600
mark 1
mark 2
list
delete 3
list
delete 1
list
bye
```

### Expected outputs

```text
 It's on the map. I've added this task:
   [T][ ] read book
 Your map now holds 1 task.
---
 It's on the map. I've added this task:
   [D][ ] return book (by: Jun 06 2019)
 Your map now holds 2 tasks.
---
 It's on the map. I've added this task:
   [E][ ] project meeting (from: Aug 06 2019, 2:00PM to: Aug 06 2019, 4:00PM)
 Your map now holds 3 tasks.
---
 A little brighter. This task is complete:
   [T][X] read book
---
 A little brighter. This task is complete:
   [D][X] return book (by: Jun 06 2019)
---
 Here's your current constellation:
 1.[T][X] read book
 2.[D][X] return book (by: Jun 06 2019)
 3.[E][ ] project meeting (from: Aug 06 2019, 2:00PM to: Aug 06 2019, 4:00PM)
---
 Cleared from the map. I've removed this task:
   [E][ ] project meeting (from: Aug 06 2019, 2:00PM to: Aug 06 2019, 4:00PM)
 Your map now holds 2 tasks.
---
 Here's your current constellation:
 1.[T][X] read book
 2.[D][X] return book (by: Jun 06 2019)
---
 Cleared from the map. I've removed this task:
   [T][X] read book
 Your map now holds 1 task.
---
 Here's your current constellation:
 1.[D][X] return book (by: Jun 06 2019)
---
 Until next time. Your tasks are safe here.
```

## TC-05: Invalid delete commands preserve the list

Aim: Verify empty-list, missing, non-numeric, and out-of-range delete commands are rejected without removing or modifying tasks.

### Inputs

```text
delete 1
todo keep this task
deadline remove this task /by 2019-10-16
delete
delete first
delete 0
delete 3
list
delete 2
delete 2
list
delete 1
list
bye
```

### Expected outputs

```text
 Signal unclear: there are no tasks to delete yet.
---
 It's on the map. I've added this task:
   [T][ ] keep this task
 Your map now holds 1 task.
---
 It's on the map. I've added this task:
   [D][ ] remove this task (by: Oct 16 2019)
 Your map now holds 2 tasks.
---
 Signal unclear: tell me which task to delete. Try: delete <task number>
---
 Signal unclear: the task number must be a whole number.
---
 Signal unclear: choose a task number from 1 to 2.
---
 Signal unclear: choose a task number from 1 to 2.
---
 Here's your current constellation:
 1.[T][ ] keep this task
 2.[D][ ] remove this task (by: Oct 16 2019)
---
 Cleared from the map. I've removed this task:
   [D][ ] remove this task (by: Oct 16 2019)
 Your map now holds 1 task.
---
 Signal unclear: choose a task number from 1 to 1.
---
 Here's your current constellation:
 1.[T][ ] keep this task
---
 Cleared from the map. I've removed this task:
   [T][ ] keep this task
 Your map now holds 0 tasks.
---
 Here's your current constellation:
---
 Until next time. Your tasks are safe here.
```

## TC-06: Invalid mark commands preserve status

Aim: Verify missing, non-numeric, and out-of-range task numbers are handled without changing the selected task's status.

### Inputs

```text
mark 1
todo read book
mark
mark first
mark 2
list
mark 1
unmark 0
unmark
list
unmark 1
list
bye
```

### Expected outputs

```text
 Signal unclear: there are no tasks to mark yet.
---
 It's on the map. I've added this task:
   [T][ ] read book
 Your map now holds 1 task.
---
 Signal unclear: tell me which task to mark. Try: mark <task number>
---
 Signal unclear: the task number must be a whole number.
---
 Signal unclear: choose a task number from 1 to 1.
---
 Here's your current constellation:
 1.[T][ ] read book
---
 A little brighter. This task is complete:
   [T][X] read book
---
 Signal unclear: choose a task number from 1 to 1.
---
 Signal unclear: tell me which task to unmark. Try: unmark <task number>
---
 Here's your current constellation:
 1.[T][X] read book
---
 Back in orbit. This task is active again:
   [T][ ] read book
---
 Here's your current constellation:
 1.[T][ ] read book
---
 Until next time. Your tasks are safe here.
```

## TC-07: Load and automatically save tasks

Aim: Verify valid saved tasks and statuses load at startup, a malformed line is skipped, and each kind of successful list mutation is reflected in the final data file.

### Initial data

```text
T | 1 | read book
D | 0 | return book | 2019-06-06T00:00:00
this line is corrupted
E | 0 | project meeting | 2019-08-06T14:00:00 | 2019-08-06T16:00:00
```

### Inputs

```text
list
unmark 1
mark 2
delete 3
todo join sports club
list
bye
```

### Expected outputs

```text
 Here's your current constellation:
 1.[T][X] read book
 2.[D][ ] return book (by: Jun 06 2019)
 3.[E][ ] project meeting (from: Aug 06 2019, 2:00PM to: Aug 06 2019, 4:00PM)
---
 Back in orbit. This task is active again:
   [T][ ] read book
---
 A little brighter. This task is complete:
   [D][X] return book (by: Jun 06 2019)
---
 Cleared from the map. I've removed this task:
   [E][ ] project meeting (from: Aug 06 2019, 2:00PM to: Aug 06 2019, 4:00PM)
 Your map now holds 2 tasks.
---
 It's on the map. I've added this task:
   [T][ ] join sports club
 Your map now holds 3 tasks.
---
 Here's your current constellation:
 1.[T][ ] read book
 2.[D][X] return book (by: Jun 06 2019)
 3.[T][ ] join sports club
---
 Until next time. Your tasks are safe here.
```

### Expected data

```text
T | 0 | read book
D | 1 | return book | 2019-06-06T00:00:00
T | 0 | join sports club
```

## TC-08: Find tasks by description

Aim: Verify that find requires a separate non-empty keyword, matches description substrings case-insensitively across task types, renumbers matches, shows no results cleanly, and does not change task state.

### Inputs

```text
find
findbook
todo read book
deadline return BOOK /by 2019-06-06
event book club /from 2019-08-06 1400 /to 2019-08-06 1600
todo write notes
mark 1
mark 2
find bOoK
find CLUB
find 2019
find missing
list
bye
```

### Expected outputs

```text
 Signal unclear: tell me what to find. Try: find <keyword>
---
 Signal unclear: I don't recognize that command. Try todo, deadline, event, list, find, mark, unmark, delete, snooze, or bye.
---
 It's on the map. I've added this task:
   [T][ ] read book
 Your map now holds 1 task.
---
 It's on the map. I've added this task:
   [D][ ] return BOOK (by: Jun 06 2019)
 Your map now holds 2 tasks.
---
 It's on the map. I've added this task:
   [E][ ] book club (from: Aug 06 2019, 2:00PM to: Aug 06 2019, 4:00PM)
 Your map now holds 3 tasks.
---
 It's on the map. I've added this task:
   [T][ ] write notes
 Your map now holds 4 tasks.
---
 A little brighter. This task is complete:
   [T][X] read book
---
 A little brighter. This task is complete:
   [D][X] return BOOK (by: Jun 06 2019)
---
 These tasks match your signal:
 1.[T][X] read book
 2.[D][X] return BOOK (by: Jun 06 2019)
 3.[E][ ] book club (from: Aug 06 2019, 2:00PM to: Aug 06 2019, 4:00PM)
---
 These tasks match your signal:
 1.[E][ ] book club (from: Aug 06 2019, 2:00PM to: Aug 06 2019, 4:00PM)
---
 These tasks match your signal:
---
 These tasks match your signal:
---
 Here's your current constellation:
 1.[T][X] read book
 2.[D][X] return BOOK (by: Jun 06 2019)
 3.[E][ ] book club (from: Aug 06 2019, 2:00PM to: Aug 06 2019, 4:00PM)
 4.[T][ ] write notes
---
 Until next time. Your tasks are safe here.
```

## TC-09: Snooze deadlines and events

Aim: Verify that snooze reschedules deadlines, moves events without changing their duration, persists the new schedule, and rejects malformed or unsupported requests without changing task state.

### Inputs

```text
snooze 1 /to 2019-10-20
todo read book
deadline submit report /by 2019-10-15
event project meeting /from 2019-10-16 1400 /to 2019-10-16 1600
snooze
snooze first /to 2019-10-20
snooze 0 /to 2019-10-20
snooze 4 /to 2019-10-20
snooze 1 /to 2019-10-20
snooze 2
snooze 2 /to
snooze 2 /towards 2019-10-20
snooze 2 /to 2019-02-29
list
snooze 2 /to 2019-10-20 1800
snooze 3 /to 2019-10-21 1500
list
bye
```

### Expected outputs

```text
 Signal unclear: there are no tasks to snooze yet.
---
 It's on the map. I've added this task:
   [T][ ] read book
 Your map now holds 1 task.
---
 It's on the map. I've added this task:
   [D][ ] submit report (by: Oct 15 2019)
 Your map now holds 2 tasks.
---
 It's on the map. I've added this task:
   [E][ ] project meeting (from: Oct 16 2019, 2:00PM to: Oct 16 2019, 4:00PM)
 Your map now holds 3 tasks.
---
 Signal unclear: tell me which task to snooze. Try: snooze <task number> /to <when>
---
 Signal unclear: the task number must be a whole number.
---
 Signal unclear: choose a task number from 1 to 3.
---
 Signal unclear: choose a task number from 1 to 3.
---
 Signal unclear: only deadlines and events can be snoozed.
---
 Signal unclear: tell me when to snooze the task until. Try: snooze <task number> /to <when>
---
 Signal unclear: the /to value cannot be empty.
---
 Signal unclear: tell me when to snooze the task until. Try: snooze <task number> /to <when>
---
 Signal unclear: use a date like 2019-10-15 or 2/12/2019, optionally followed by a 24-hour time such as 1800.
---
 Here's your current constellation:
 1.[T][ ] read book
 2.[D][ ] submit report (by: Oct 15 2019)
 3.[E][ ] project meeting (from: Oct 16 2019, 2:00PM to: Oct 16 2019, 4:00PM)
---
 Orbit adjusted. I've rescheduled this task:
   [D][ ] submit report (by: Oct 20 2019, 6:00PM)
---
 Orbit adjusted. I've rescheduled this task:
   [E][ ] project meeting (from: Oct 21 2019, 3:00PM to: Oct 21 2019, 5:00PM)
---
 Here's your current constellation:
 1.[T][ ] read book
 2.[D][ ] submit report (by: Oct 20 2019, 6:00PM)
 3.[E][ ] project meeting (from: Oct 21 2019, 3:00PM to: Oct 21 2019, 5:00PM)
---
 Until next time. Your tasks are safe here.
```

### Expected data

```text
T | 0 | read book
D | 0 | submit report | 2019-10-20T18:00:00
E | 0 | project meeting | 2019-10-21T15:00:00 | 2019-10-21T17:00:00
```

## TC-10: Invalid parameters and event ranges preserve state

Aim: Verify that duplicate parameters, misordered event parameters, and invalid event ranges are rejected without
adding or rescheduling tasks.

### Inputs

```text
event backwards /from 2019-10-16 1600 /to 2019-10-16 1400
event no duration /from 2019-10-16 1400 /to 2019-10-16 1400
deadline duplicate date /by 2019-10-15 /by 2019-10-16
event repeated start /from 2019-10-15 /from 2019-10-16 /to 2019-10-17
event repeated end /from 2019-10-15 /to 2019-10-16 /to 2019-10-17
event wrong order /to 2019-10-16 /from 2019-10-15
deadline valid deadline /by 2019-10-20
snooze 1 /to 2019-10-21 /to 2019-10-22
list
bye
```

### Expected outputs

```text
 Signal unclear: an event's end must be later than its start.
---
 Signal unclear: an event's end must be later than its start.
---
 Signal unclear: the /by parameter can only be used once.
---
 Signal unclear: the /from parameter can only be used once.
---
 Signal unclear: the /to parameter can only be used once.
---
 Signal unclear: put /from before /to in an event.
---
 It's on the map. I've added this task:
   [D][ ] valid deadline (by: Oct 20 2019)
 Your map now holds 1 task.
---
 Signal unclear: the /to parameter can only be used once.
---
 Here's your current constellation:
 1.[D][ ] valid deadline (by: Oct 20 2019)
---
 Until next time. Your tasks are safe here.
```
