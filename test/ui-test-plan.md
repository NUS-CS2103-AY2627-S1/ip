# Console UI test plan

Run this plan with the project-local `test-ui` skill. Expected output is exact except for platform line endings.

Each test starts with an isolated data file. `Setup data` supplies its initial contents; `Expected saved data` is checked after the session.

## Test case: Create, display, find, and save dated tasks

Aim: Verify that deadlines and events parse into dates and times, display in a user-friendly format, are found by date, and are saved without losing 24-hour times.

### Inputs

```text
deadline return book /by 2/12/2019 1800
event project meeting /from 2019-12-02 0900 /to 2019-12-03 1700
date 2019-12-02
list
bye
```

### Expected output

```text
____________________________________________________________
       .       *
     C B T
 *       .       *

Mission control online. I'm CBT.
Where shall we steer your day?
____________________________________________________________
____________________________________________________________
Task locked into orbit:
  [D][ ] return book (by: Dec 02 2019, 6:00pm)
Your flight plan now has 1 task.
____________________________________________________________
____________________________________________________________
Task locked into orbit:
  [E][ ] project meeting (from: Dec 02 2019, 9:00am to: Dec 03 2019, 5:00pm)
Your flight plan now has 2 tasks.
____________________________________________________________
____________________________________________________________
On the radar for Dec 02 2019:
1.[D][ ] return book (by: Dec 02 2019, 6:00pm)
2.[E][ ] project meeting (from: Dec 02 2019, 9:00am to: Dec 03 2019, 5:00pm)
____________________________________________________________
____________________________________________________________
Your current flight plan:
1.[D][ ] return book (by: Dec 02 2019, 6:00pm)
2.[E][ ] project meeting (from: Dec 02 2019, 9:00am to: Dec 03 2019, 5:00pm)
____________________________________________________________
____________________________________________________________
Mission paused. Safe travels!
____________________________________________________________
```

### Expected saved data

```text
DEADLINE | 0 | return book | 02/12/2019 1800
EVENT | 0 | project meeting | 02/12/2019 0900 | 03/12/2019 1700
```

## Test case: Guide an unrecognized command back on course

Aim: Verify that CBT responds to an unknown command with clear, in-character guidance.

### Inputs

```text
dance
bye
```

### Expected output

```text
____________________________________________________________
       .       *
     C B T
 *       .       *

Mission control online. I'm CBT.
Where shall we steer your day?
____________________________________________________________
____________________________________________________________
That command is off course. Try todo, deadline, event, date, list, listdate, find, mark, unmark, delete, sort, or bye.
____________________________________________________________
____________________________________________________________
Mission paused. Safe travels!
____________________________________________________________
```

## Test case: Keep existing task-list actions working

Aim: Verify that date support does not affect creating, marking, unmarking, deleting, and listing todos.

### Inputs

```text
todo first task
todo second task
mark 1
unmark 1
delete 2
list
bye
```

### Expected output

```text
____________________________________________________________
       .       *
     C B T
 *       .       *

Mission control online. I'm CBT.
Where shall we steer your day?
____________________________________________________________
____________________________________________________________
Task locked into orbit:
  [T][ ] first task
Your flight plan now has 1 task.
____________________________________________________________
____________________________________________________________
Task locked into orbit:
  [T][ ] second task
Your flight plan now has 2 tasks.
____________________________________________________________
____________________________________________________________
Mission accomplished. Task complete:
  [T][X] first task
____________________________________________________________
____________________________________________________________
Back on the flight plan:
  [T][ ] first task
____________________________________________________________
____________________________________________________________
Cleared from the flight plan:
  [T][ ] second task
1 task remains in orbit.
____________________________________________________________
____________________________________________________________
Your current flight plan:
1.[T][ ] first task
____________________________________________________________
____________________________________________________________
Mission paused. Safe travels!
____________________________________________________________
```

## Test case: Reject invalid dates and event ranges

Aim: Verify that malformed and impossible dates, missing event fields, and an event whose start is after its end do not add tasks.

### Inputs

```text
deadline invalid leap day /by 2019-02-29 0900
event missing end /from 2019-12-02 0900
event backwards /from 2019-12-03 0900 /to 2019-12-02 0900
date 2019-02-29
date
list
bye
```

### Expected output

```text
____________________________________________________________
       .       *
     C B T
 *       .       *

Mission control online. I'm CBT.
Where shall we steer your day?
____________________________________________________________
____________________________________________________________
Cannot recognize date/time! Example valid formats:
  - 2/12/2019 1800
  - 2-12-2019 1800
  - 2019-12-02 1800
  - 2/12/2019
____________________________________________________________
____________________________________________________________
Use: event DESCRIPTION /from START /to END
____________________________________________________________
____________________________________________________________
The event start date and time must be before its end date and time.
____________________________________________________________
____________________________________________________________
Cannot recognize date! Use a date such as 2019-12-02.
____________________________________________________________
____________________________________________________________
Use: date DATE (for example, 2019-12-02)
____________________________________________________________
____________________________________________________________
Your current flight plan:
____________________________________________________________
____________________________________________________________
Mission paused. Safe travels!
____________________________________________________________
```

## Test case: Load and query persisted dated tasks

Aim: Verify that canonical saved dates reload as date-time objects and that a multi-day event appears on its final date.

### Setup data

```text
TODO | 0 | read book
DEADLINE | 1 | return book | 02/12/2019 1800
EVENT | 0 | conference | 01/12/2019 0900 | 03/12/2019 1700
```

### Inputs

```text
date 2019-12-03
bye
```

### Expected output

```text
____________________________________________________________
       .       *
     C B T
 *       .       *

Mission control online. I'm CBT.
Where shall we steer your day?
____________________________________________________________
____________________________________________________________
On the radar for Dec 03 2019:
3.[E][ ] conference (from: Dec 01 2019, 9:00am to: Dec 03 2019, 5:00pm)
____________________________________________________________
____________________________________________________________
Mission paused. Safe travels!
____________________________________________________________
```

## Test case: Find tasks by description keyword

Aim: Verify that find searches descriptions without regard to letter case, renumbers matching tasks, and rejects a
blank keyword.

### Setup data

```text
TODO | 1 | read book
DEADLINE | 0 | return book | 02/12/2019 1800
TODO | 0 | write report
```

### Inputs

```text
find BOOK
find
bye
```

### Expected output

```text
____________________________________________________________
       .       *
     C B T
 *       .       *

Mission control online. I'm CBT.
Where shall we steer your day?
____________________________________________________________
____________________________________________________________
Matches on the radar:
1.[T][X] read book
2.[D][ ] return book (by: Dec 02 2019, 6:00pm)
____________________________________________________________
____________________________________________________________
Use: find KEYWORD (for example, find book)
____________________________________________________________
____________________________________________________________
Mission paused. Safe travels!
____________________________________________________________
```

## Test case: Sort mixed tasks chronologically and save the new order

Aim: Verify that sorting uses deadline times and event start times, preserves tied task order, places todos last,
renumbers tasks for later commands, and persists the result.

### Setup data

```text
TODO | 0 | undated task
DEADLINE | 0 | later deadline | 03/12/2019 1800
EVENT | 0 | early event | 01/12/2019 0900 | 01/12/2019 1000
DEADLINE | 0 | tied deadline | 01/12/2019 0900
```

### Inputs

```text
sort date
mark 1
list
bye
```

### Expected output

```text
____________________________________________________________
       .       *
     C B T
 *       .       *

Mission control online. I'm CBT.
Where shall we steer your day?
____________________________________________________________
____________________________________________________________
Flight plan aligned by date:
1.[E][ ] early event (from: Dec 01 2019, 9:00am to: Dec 01 2019, 10:00am)
2.[D][ ] tied deadline (by: Dec 01 2019, 9:00am)
3.[D][ ] later deadline (by: Dec 03 2019, 6:00pm)
4.[T][ ] undated task
____________________________________________________________
____________________________________________________________
Mission accomplished. Task complete:
  [E][X] early event (from: Dec 01 2019, 9:00am to: Dec 01 2019, 10:00am)
____________________________________________________________
____________________________________________________________
Your current flight plan:
1.[E][X] early event (from: Dec 01 2019, 9:00am to: Dec 01 2019, 10:00am)
2.[D][ ] tied deadline (by: Dec 01 2019, 9:00am)
3.[D][ ] later deadline (by: Dec 03 2019, 6:00pm)
4.[T][ ] undated task
____________________________________________________________
____________________________________________________________
Mission paused. Safe travels!
____________________________________________________________
```

### Expected saved data

```text
EVENT | 1 | early event | 01/12/2019 0900 | 01/12/2019 1000
DEADLINE | 0 | tied deadline | 01/12/2019 0900
DEADLINE | 0 | later deadline | 03/12/2019 1800
TODO | 0 | undated task
```

## Test case: Validate sort criteria and handle an empty list

Aim: Verify that missing, unsupported, and extra sort arguments are rejected and that an empty list sorts successfully.

### Inputs

```text
sort
sort name
sort date asc
sort date
bye
```

### Expected output

```text
____________________________________________________________
       .       *
     C B T
 *       .       *

Mission control online. I'm CBT.
Where shall we steer your day?
____________________________________________________________
____________________________________________________________
Use: sort date
____________________________________________________________
____________________________________________________________
Use: sort date
____________________________________________________________
____________________________________________________________
Use: sort date
____________________________________________________________
____________________________________________________________
Flight plan aligned by date:
____________________________________________________________
____________________________________________________________
Mission paused. Safe travels!
____________________________________________________________
```

## Test case: Normalize commands and reject ambiguous or duplicate data

Aim: Verify that harmless whitespace is normalized while extra arguments, duplicate parameters, equal event times,
unsafe descriptions, and duplicate tasks are rejected without corrupting the task list.

### Inputs

```text
  todo    read    book
todo READ BOOK
todo unsafe | description
deadline report /by 2/12/2026 /by 3/12/2026
event instant /from 2/12/2026 0900 /to 2/12/2026 0900
list extra
list
bye
```

### Expected output

```text
____________________________________________________________
       .       *
     C B T
 *       .       *

Mission control online. I'm CBT.
Where shall we steer your day?
____________________________________________________________
____________________________________________________________
Task locked into orbit:
  [T][ ] read book
Your flight plan now has 1 task.
____________________________________________________________
____________________________________________________________
That task is already on your flight plan.
____________________________________________________________
____________________________________________________________
A task description cannot contain the reserved '|' character.
____________________________________________________________
____________________________________________________________
Use: deadline DESCRIPTION /by DATE_OR_TIME
____________________________________________________________
____________________________________________________________
The event start date and time must be before its end date and time.
____________________________________________________________
____________________________________________________________
Use: list
____________________________________________________________
____________________________________________________________
Your current flight plan:
1.[T][ ] read book
____________________________________________________________
____________________________________________________________
Mission paused. Safe travels!
____________________________________________________________
```
