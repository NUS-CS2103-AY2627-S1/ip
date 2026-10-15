# UI Test Plan

Run these cases with the project-specific `test-ui` skill. Each case starts Avon in a fresh JVM and compares the complete console output exactly, apart from line-ending and final-newline normalization.

## TC-001: Add and list a Todo

Aim: Verify that `todo` creates a Todo task and `list` displays its type and completion status.

### Input

```text
todo borrow book
list
bye
```

### Expected output

```text
____________________________________________________________
    ___
   /   |_   ______  ____
  / /| | | / / __ \/ __ \
 / ___ | |/ / /_/ / / / /
/_/  |_|___/\____/_/ /_/
Avon:	Hark! I am Avon who stands before thee.
Avon:	How may my hand or wit now serve thy need?
____________________________________________________________
____________________________________________________________
Avon:	By thy command, I've added this task:
        [T][ ] borrow book
Avon:	Now thou hast 1 task in thy list.
____________________________________________________________
____________________________________________________________
Avon:	Here are the tasks in thy list:
        1.[T][ ] borrow book
____________________________________________________________
____________________________________________________________
Avon:	Fare thee well! Pray heavens our paths cross anon.
____________________________________________________________
```

## TC-011: Normalize command whitespace

Aim: Verify that leading whitespace is ignored while command arguments remain intact.

### Input

```text
   todo rehearse scene
   list
   bye
```

### Expected output

```text
____________________________________________________________
    ___
   /   |_   ______  ____
  / /| | | / / __ \/ __ \
 / ___ | |/ / /_/ / / / /
/_/  |_|___/\____/_/ /_/
Avon:	Hark! I am Avon who stands before thee.
Avon:	How may my hand or wit now serve thy need?
____________________________________________________________
____________________________________________________________
Avon:	By thy command, I've added this task:
        [T][ ] rehearse scene
Avon:	Now thou hast 1 task in thy list.
____________________________________________________________
____________________________________________________________
Avon:	Here are the tasks in thy list:
        1.[T][ ] rehearse scene
____________________________________________________________
____________________________________________________________
Avon:	Fare thee well! Pray heavens our paths cross anon.
____________________________________________________________
```

## TC-013: Reject duplicate tasks

Aim: Verify that Avon rejects tasks with identical details even after the stored task is completed.

### Input

```text
todo read book
todo read book
mark 1
todo read book
list
bye
```

### Expected output

```text
____________________________________________________________
    ___
   /   |_   ______  ____
  / /| | | / / __ \/ __ \
 / ___ | |/ / /_/ / / / /
/_/  |_|___/\____/_/ /_/
Avon:	Hark! I am Avon who stands before thee.
Avon:	How may my hand or wit now serve thy need?
____________________________________________________________
____________________________________________________________
Avon:	By thy command, I've added this task:
        [T][ ] read book
Avon:	Now thou hast 1 task in thy list.
____________________________________________________________
____________________________________________________________
Avon:	Pardon, I beseech thee! That task already dwells within thy list.
____________________________________________________________
____________________________________________________________
Avon:	Tis well! Thy noble task is now fulfilled:
        [T][X] read book
____________________________________________________________
____________________________________________________________
Avon:	Pardon, I beseech thee! That task already dwells within thy list.
____________________________________________________________
____________________________________________________________
Avon:	Here are the tasks in thy list:
        1.[T][X] read book
____________________________________________________________
____________________________________________________________
Avon:	Fare thee well! Pray heavens our paths cross anon.
____________________________________________________________
```

## TC-002: Add timed tasks and reverse completion

Aim: Verify Deadline and Event parsing, typed display, marking, unmarking, and list ordering.

### Input

```text
deadline return book /by 2026-08-23 1800
event project meeting /from 2026-08-23 1400 /to 2026-08-23 1600
mark 1
unmark 1
list
bye
```

### Expected output

```text
____________________________________________________________
    ___
   /   |_   ______  ____
  / /| | | / / __ \/ __ \
 / ___ | |/ / /_/ / / / /
/_/  |_|___/\____/_/ /_/
Avon:	Hark! I am Avon who stands before thee.
Avon:	How may my hand or wit now serve thy need?
____________________________________________________________
____________________________________________________________
Avon:	By thy command, I've added this task:
        [D][ ] return book (by: Aug 23 2026, 6:00PM)
Avon:	Now thou hast 1 task in thy list.
____________________________________________________________
____________________________________________________________
Avon:	By thy command, I've added this task:
        [E][ ] project meeting (from: Aug 23 2026, 2:00PM to: Aug 23 2026, 4:00PM)
Avon:	Now thou hast 2 tasks in thy list.
____________________________________________________________
____________________________________________________________
Avon:	Tis well! Thy noble task is now fulfilled:
        [D][X] return book (by: Aug 23 2026, 6:00PM)
____________________________________________________________
____________________________________________________________
Avon:	Thy noble task is undone once more:
        [D][ ] return book (by: Aug 23 2026, 6:00PM)
____________________________________________________________
____________________________________________________________
Avon:	Here are the tasks in thy list:
        1.[D][ ] return book (by: Aug 23 2026, 6:00PM)
        2.[E][ ] project meeting (from: Aug 23 2026, 2:00PM to: Aug 23 2026, 4:00PM)
____________________________________________________________
____________________________________________________________
Avon:	Fare thee well! Pray heavens our paths cross anon.
____________________________________________________________
```

## TC-003: Reject unknown and incomplete Todo commands

Aim: Verify that Avon rejects an unknown first word and a Todo without a description, then explains valid input.

### Input

```text
blah
todo
todo read Macbeth
bye
```

### Expected output

```text
____________________________________________________________
    ___
   /   |_   ______  ____
  / /| | | / / __ \/ __ \
 / ___ | |/ / /_/ / / / /
/_/  |_|___/\____/_/ /_/
Avon:	Hark! I am Avon who stands before thee.
Avon:	How may my hand or wit now serve thy need?
____________________________________________________________
____________________________________________________________
Avon:	Pardon, I beseech thee! I know not that command.
        Do start with one of: todo, deadline, event, list, find, mark, unmark, delete, help, bye.
____________________________________________________________
____________________________________________________________
Avon:	Pardon, I beseech thee! The todo description cannot be empty.
        Do enter it in this format: todo DESCRIPTION
____________________________________________________________
____________________________________________________________
Avon:	By thy command, I've added this task:
        [T][ ] read Macbeth
Avon:	Now thou hast 1 task in thy list.
____________________________________________________________
____________________________________________________________
Avon:	Fare thee well! Pray heavens our paths cross anon.
____________________________________________________________
```

## TC-004: Explain incomplete Deadline and Event commands

Aim: Verify error-specific guidance for missing descriptions, separators, and date or time details.

### Input

```text
deadline
deadline return book
deadline /by Sunday
deadline return book /by
deadline return book /by Sunday
deadline return book /by 2026-08-23 2500
event
event project meeting /to 4pm
event project meeting /from 2pm
event /from 2pm /to 4pm
event project meeting /from /to 4pm
event project meeting /from 2pm /to
event project meeting /from 2026-08-23 2500 /to 2026-08-23 2600
event project meeting /from 2026-08-23 1600 /to 2026-08-23 1400
event project meeting /from 2026-08-23 1600 /to 2026-08-23 1600
bye
```

### Expected output

```text
____________________________________________________________
    ___
   /   |_   ______  ____
  / /| | | / / __ \/ __ \
 / ___ | |/ / /_/ / / / /
/_/  |_|___/\____/_/ /_/
Avon:	Hark! I am Avon who stands before thee.
Avon:	How may my hand or wit now serve thy need?
____________________________________________________________
____________________________________________________________
Avon:	Pardon, I beseech thee! The deadline description cannot be empty.
        Do enter it in this format: deadline DESCRIPTION /by yyyy-MM-dd [HHmm]
____________________________________________________________
____________________________________________________________
Avon:	Pardon, I beseech thee! This deadline command is ill-formed.
        Include '/by' before the deadline date or time.
        Do enter it in this format: deadline DESCRIPTION /by yyyy-MM-dd [HHmm]
____________________________________________________________
____________________________________________________________
Avon:	Pardon, I beseech thee! The deadline description cannot be empty.
        Do enter it in this format: deadline DESCRIPTION /by yyyy-MM-dd [HHmm]
____________________________________________________________
____________________________________________________________
Avon:	Pardon, I beseech thee! This deadline command is ill-formed.
        Add a date or time after '/by'.
        Do enter it in this format: deadline DESCRIPTION /by yyyy-MM-dd [HHmm]
____________________________________________________________
____________________________________________________________
Avon:	Pardon, I beseech thee! This deadline command is ill-formed.
        Use a real date and optional 24-hour time in yyyy-MM-dd [HHmm] format.
        Do enter it in this format: deadline DESCRIPTION /by yyyy-MM-dd [HHmm]
____________________________________________________________
____________________________________________________________
Avon:	Pardon, I beseech thee! This deadline command is ill-formed.
        Use a real date and optional 24-hour time in yyyy-MM-dd [HHmm] format.
        Do enter it in this format: deadline DESCRIPTION /by yyyy-MM-dd [HHmm]
____________________________________________________________
____________________________________________________________
Avon:	Pardon, I beseech thee! The event description cannot be empty.
        Do enter it in this format: event DESCRIPTION /from yyyy-MM-dd [HHmm] /to yyyy-MM-dd [HHmm]
____________________________________________________________
____________________________________________________________
Avon:	Pardon, I beseech thee! This event command is ill-formed.
        Include '/from' before the start date or time.
        Do enter it in this format: event DESCRIPTION /from yyyy-MM-dd [HHmm] /to yyyy-MM-dd [HHmm]
____________________________________________________________
____________________________________________________________
Avon:	Pardon, I beseech thee! This event command is ill-formed.
        Include '/to' before the end date or time.
        Do enter it in this format: event DESCRIPTION /from yyyy-MM-dd [HHmm] /to yyyy-MM-dd [HHmm]
____________________________________________________________
____________________________________________________________
Avon:	Pardon, I beseech thee! The event description cannot be empty.
        Do enter it in this format: event DESCRIPTION /from yyyy-MM-dd [HHmm] /to yyyy-MM-dd [HHmm]
____________________________________________________________
____________________________________________________________
Avon:	Pardon, I beseech thee! This event command is ill-formed.
        Add a start date or time after '/from'.
        Do enter it in this format: event DESCRIPTION /from yyyy-MM-dd [HHmm] /to yyyy-MM-dd [HHmm]
____________________________________________________________
____________________________________________________________
Avon:	Pardon, I beseech thee! This event command is ill-formed.
        Add an end date or time after '/to'.
        Do enter it in this format: event DESCRIPTION /from yyyy-MM-dd [HHmm] /to yyyy-MM-dd [HHmm]
____________________________________________________________
____________________________________________________________
Avon:	Pardon, I beseech thee! This event command is ill-formed.
        Use real dates and optional 24-hour times in yyyy-MM-dd [HHmm] format.
        Do enter it in this format: event DESCRIPTION /from yyyy-MM-dd [HHmm] /to yyyy-MM-dd [HHmm]
____________________________________________________________
____________________________________________________________
Avon:	Pardon, I beseech thee! This event command is ill-formed.
        Set '/to' to a time later than '/from'.
        Do enter it in this format: event DESCRIPTION /from yyyy-MM-dd [HHmm] /to yyyy-MM-dd [HHmm]
____________________________________________________________
____________________________________________________________
Avon:	Pardon, I beseech thee! This event command is ill-formed.
        Set '/to' to a time later than '/from'.
        Do enter it in this format: event DESCRIPTION /from yyyy-MM-dd [HHmm] /to yyyy-MM-dd [HHmm]
____________________________________________________________
____________________________________________________________
Avon:	Fare thee well! Pray heavens our paths cross anon.
____________________________________________________________
```

## TC-005: Reject invalid Mark and Unmark task numbers

Aim: Verify mark and unmark guidance for empty lists and missing, malformed, non-positive, or out-of-range task numbers.

### Input

```text
mark
mark one
mark 1
unmark 1
todo rehearse scene
mark 0
mark 2
mark 1
unmark
unmark 1
bye
```

### Expected output

```text
____________________________________________________________
    ___
   /   |_   ______  ____
  / /| | | / / __ \/ __ \
 / ___ | |/ / /_/ / / / /
/_/  |_|___/\____/_/ /_/
Avon:	Hark! I am Avon who stands before thee.
Avon:	How may my hand or wit now serve thy need?
____________________________________________________________
____________________________________________________________
Avon:	Pardon, I beseech thee! I cannot mark that task.
        Add a task number after 'mark'.
        Example: mark 1
____________________________________________________________
____________________________________________________________
Avon:	Pardon, I beseech thee! I cannot mark that task.
        Use one whole task number greater than zero.
        Example: mark 1
____________________________________________________________
____________________________________________________________
Avon:	Pardon, I beseech thee! I cannot mark that task.
        Thy task list is empty; add a task first.
____________________________________________________________
____________________________________________________________
Avon:	Pardon, I beseech thee! I cannot unmark that task.
        Thy task list is empty; add a task first.
____________________________________________________________
____________________________________________________________
Avon:	By thy command, I've added this task:
        [T][ ] rehearse scene
Avon:	Now thou hast 1 task in thy list.
____________________________________________________________
____________________________________________________________
Avon:	Pardon, I beseech thee! I cannot mark that task.
        Choose a task number from 1 to 1.
        Example: mark 1
____________________________________________________________
____________________________________________________________
Avon:	Pardon, I beseech thee! I cannot mark that task.
        Choose a task number from 1 to 1.
        Example: mark 1
____________________________________________________________
____________________________________________________________
Avon:	Tis well! Thy noble task is now fulfilled:
        [T][X] rehearse scene
____________________________________________________________
____________________________________________________________
Avon:	Pardon, I beseech thee! I cannot unmark that task.
        Add a task number after 'unmark'.
        Example: unmark 1
____________________________________________________________
____________________________________________________________
Avon:	Thy noble task is undone once more:
        [T][ ] rehearse scene
____________________________________________________________
____________________________________________________________
Avon:	Fare thee well! Pray heavens our paths cross anon.
____________________________________________________________
```

## TC-006: Delete a middle task and renumber later tasks

Aim: Verify that deleting a middle task retains the other tasks in order and renumbers every later task.

### Input

```text
todo read book
deadline return book /by 2026-06-06
event project meeting /from 2026-08-06 1400 /to 2026-08-06 1600
mark 1
mark 2
list
delete 2
list
bye
```

### Expected output

```text
____________________________________________________________
    ___
   /   |_   ______  ____
  / /| | | / / __ \/ __ \
 / ___ | |/ / /_/ / / / /
/_/  |_|___/\____/_/ /_/
Avon:	Hark! I am Avon who stands before thee.
Avon:	How may my hand or wit now serve thy need?
____________________________________________________________
____________________________________________________________
Avon:	By thy command, I've added this task:
        [T][ ] read book
Avon:	Now thou hast 1 task in thy list.
____________________________________________________________
____________________________________________________________
Avon:	By thy command, I've added this task:
        [D][ ] return book (by: Jun 6 2026)
Avon:	Now thou hast 2 tasks in thy list.
____________________________________________________________
____________________________________________________________
Avon:	By thy command, I've added this task:
        [E][ ] project meeting (from: Aug 6 2026, 2:00PM to: Aug 6 2026, 4:00PM)
Avon:	Now thou hast 3 tasks in thy list.
____________________________________________________________
____________________________________________________________
Avon:	Tis well! Thy noble task is now fulfilled:
        [T][X] read book
____________________________________________________________
____________________________________________________________
Avon:	Tis well! Thy noble task is now fulfilled:
        [D][X] return book (by: Jun 6 2026)
____________________________________________________________
____________________________________________________________
Avon:	Here are the tasks in thy list:
        1.[T][X] read book
        2.[D][X] return book (by: Jun 6 2026)
        3.[E][ ] project meeting (from: Aug 6 2026, 2:00PM to: Aug 6 2026, 4:00PM)
____________________________________________________________
____________________________________________________________
Avon:	So be it! I've removed this task:
        [D][X] return book (by: Jun 6 2026)
Avon:	Now thou hast 2 tasks in thy list.
____________________________________________________________
____________________________________________________________
Avon:	Here are the tasks in thy list:
        1.[T][X] read book
        2.[E][ ] project meeting (from: Aug 6 2026, 2:00PM to: Aug 6 2026, 4:00PM)
____________________________________________________________
____________________________________________________________
Avon:	Fare thee well! Pray heavens our paths cross anon.
____________________________________________________________
```

## TC-007: Reject invalid Delete task numbers

Aim: Verify empty-list display and delete guidance for empty, missing, malformed, non-positive, and out-of-range task numbers.

### Input

```text
list
delete 1
delete
delete one
todo rehearse scene
delete 0
delete 2
bye
```

### Expected output

```text
____________________________________________________________
    ___
   /   |_   ______  ____
  / /| | | / / __ \/ __ \
 / ___ | |/ / /_/ / / / /
/_/  |_|___/\____/_/ /_/
Avon:	Hark! I am Avon who stands before thee.
Avon:	How may my hand or wit now serve thy need?
____________________________________________________________
____________________________________________________________
Avon:	Thy task list is empty.
____________________________________________________________
____________________________________________________________
Avon:	Pardon, I beseech thee! I cannot delete that task.
        Thy task list is empty; add a task first.
____________________________________________________________
____________________________________________________________
Avon:	Pardon, I beseech thee! I cannot delete that task.
        Add a task number after 'delete'.
        Example: delete 1
____________________________________________________________
____________________________________________________________
Avon:	Pardon, I beseech thee! I cannot delete that task.
        Use one whole task number greater than zero.
        Example: delete 1
____________________________________________________________
____________________________________________________________
Avon:	By thy command, I've added this task:
        [T][ ] rehearse scene
Avon:	Now thou hast 1 task in thy list.
____________________________________________________________
____________________________________________________________
Avon:	Pardon, I beseech thee! I cannot delete that task.
        Choose a task number from 1 to 1.
        Example: delete 1
____________________________________________________________
____________________________________________________________
Avon:	Pardon, I beseech thee! I cannot delete that task.
        Choose a task number from 1 to 1.
        Example: delete 1
____________________________________________________________
____________________________________________________________
Avon:	Fare thee well! Pray heavens our paths cross anon.
____________________________________________________________
```

## TC-008: Reject ambiguous timed-task separators

Aim: Verify that Deadline and Event commands reject duplicate separators and Event separators in reverse order.

### Input

```text
deadline return book /by Monday /by Tuesday
event meeting /to 4pm /from 2pm
event meeting /from 1pm /from 2pm /to 3pm
event meeting /from 1pm /to 2pm /to 3pm
bye
```

### Expected output

```text
____________________________________________________________
    ___
   /   |_   ______  ____
  / /| | | / / __ \/ __ \
 / ___ | |/ / /_/ / / / /
/_/  |_|___/\____/_/ /_/
Avon:	Hark! I am Avon who stands before thee.
Avon:	How may my hand or wit now serve thy need?
____________________________________________________________
____________________________________________________________
Avon:	Pardon, I beseech thee! This deadline command is ill-formed.
        Use '/by' exactly once.
        Do enter it in this format: deadline DESCRIPTION /by yyyy-MM-dd [HHmm]
____________________________________________________________
____________________________________________________________
Avon:	Pardon, I beseech thee! This event command is ill-formed.
        Place '/from' before '/to'.
        Do enter it in this format: event DESCRIPTION /from yyyy-MM-dd [HHmm] /to yyyy-MM-dd [HHmm]
____________________________________________________________
____________________________________________________________
Avon:	Pardon, I beseech thee! This event command is ill-formed.
        Use '/from' and '/to' exactly once each.
        Do enter it in this format: event DESCRIPTION /from yyyy-MM-dd [HHmm] /to yyyy-MM-dd [HHmm]
____________________________________________________________
____________________________________________________________
Avon:	Pardon, I beseech thee! This event command is ill-formed.
        Use '/from' and '/to' exactly once each.
        Do enter it in this format: event DESCRIPTION /from yyyy-MM-dd [HHmm] /to yyyy-MM-dd [HHmm]
____________________________________________________________
____________________________________________________________
Avon:	Fare thee well! Pray heavens our paths cross anon.
____________________________________________________________
```

## TC-009: Find tasks by description

Aim: Verify that `find` preserves original task numbers, remains case-sensitive, and rejects an empty keyword.

### Input

```text
todo read book
todo write essay
deadline return book /by 2026-08-23
find book
find Book
find
bye
```

### Expected output

```text
____________________________________________________________
    ___
   /   |_   ______  ____
  / /| | | / / __ \/ __ \
 / ___ | |/ / /_/ / / / /
/_/  |_|___/\____/_/ /_/
Avon:	Hark! I am Avon who stands before thee.
Avon:	How may my hand or wit now serve thy need?
____________________________________________________________
____________________________________________________________
Avon:	By thy command, I've added this task:
        [T][ ] read book
Avon:	Now thou hast 1 task in thy list.
____________________________________________________________
____________________________________________________________
Avon:	By thy command, I've added this task:
        [T][ ] write essay
Avon:	Now thou hast 2 tasks in thy list.
____________________________________________________________
____________________________________________________________
Avon:	By thy command, I've added this task:
        [D][ ] return book (by: Aug 23 2026)
Avon:	Now thou hast 3 tasks in thy list.
____________________________________________________________
____________________________________________________________
Avon:	Here are the matching tasks in thy list:
        1.[T][ ] read book
        3.[D][ ] return book (by: Aug 23 2026)
____________________________________________________________
____________________________________________________________
Avon:	No matching tasks were found in thy list.
____________________________________________________________
____________________________________________________________
Avon:	Pardon, I beseech thee! The find description cannot be empty.
        Do enter it in this format: find KEYWORD
____________________________________________________________
____________________________________________________________
Avon:	Fare thee well! Pray heavens our paths cross anon.
____________________________________________________________
```

## TC-010: Match complete command keywords

Aim: Verify that command prefixes and arguments after argument-free commands are rejected, while an exact command remains valid.

### Input

```text
todoist write tests
list extra
bye now
bye
```

### Expected output

```text
____________________________________________________________
    ___
   /   |_   ______  ____
  / /| | | / / __ \/ __ \
 / ___ | |/ / /_/ / / / /
/_/  |_|___/\____/_/ /_/
Avon:	Hark! I am Avon who stands before thee.
Avon:	How may my hand or wit now serve thy need?
____________________________________________________________
____________________________________________________________
Avon:	Pardon, I beseech thee! I know not that command.
        Do start with one of: todo, deadline, event, list, find, mark, unmark, delete, help, bye.
____________________________________________________________
____________________________________________________________
Avon:	Pardon, I beseech thee! I know not that command.
        Do start with one of: todo, deadline, event, list, find, mark, unmark, delete, help, bye.
____________________________________________________________
____________________________________________________________
Avon:	Pardon, I beseech thee! I know not that command.
        Do start with one of: todo, deadline, event, list, find, mark, unmark, delete, help, bye.
____________________________________________________________
____________________________________________________________
Avon:	Fare thee well! Pray heavens our paths cross anon.
____________________________________________________________
```

## TC-014: Display command help

Aim: Verify that `help` displays the syntax of every supported command without changing the task list.

### Input

```text
help
list
bye
```

### Expected output

```text
____________________________________________________________
    ___
   /   |_   ______  ____
  / /| | | / / __ \/ __ \
 / ___ | |/ / /_/ / / / /
/_/  |_|___/\____/_/ /_/
Avon:	Hark! I am Avon who stands before thee.
Avon:	How may my hand or wit now serve thy need?
____________________________________________________________
____________________________________________________________
Avon:	Here are the commands at thy service:
        todo DESCRIPTION
        deadline DESCRIPTION /by yyyy-MM-dd [HHmm]
        event DESCRIPTION /from yyyy-MM-dd [HHmm] /to yyyy-MM-dd [HHmm]
        list
        find KEYWORD
        mark TASK_NUMBER
        unmark TASK_NUMBER
        delete TASK_NUMBER
        help
        bye
____________________________________________________________
____________________________________________________________
Avon:	Thy task list is empty.
____________________________________________________________
____________________________________________________________
Avon:	Fare thee well! Pray heavens our paths cross anon.
____________________________________________________________
```

## TC-012: Preserve delimiter prefixes

Aim: Verify that delimiter prefixes embedded in descriptions are not treated as separator tokens.

### Input

```text
deadline study /byte encoding /by 2026-08-23 1800
event prepare /today notes /from 2026-08-23 1400 /to 2026-08-23 1600
list
bye
```

### Expected output

```text
____________________________________________________________
    ___
   /   |_   ______  ____
  / /| | | / / __ \/ __ \
 / ___ | |/ / /_/ / / / /
/_/  |_|___/\____/_/ /_/
Avon:	Hark! I am Avon who stands before thee.
Avon:	How may my hand or wit now serve thy need?
____________________________________________________________
____________________________________________________________
Avon:	By thy command, I've added this task:
        [D][ ] study /byte encoding (by: Aug 23 2026, 6:00PM)
Avon:	Now thou hast 1 task in thy list.
____________________________________________________________
____________________________________________________________
Avon:	By thy command, I've added this task:
        [E][ ] prepare /today notes (from: Aug 23 2026, 2:00PM to: Aug 23 2026, 4:00PM)
Avon:	Now thou hast 2 tasks in thy list.
____________________________________________________________
____________________________________________________________
Avon:	Here are the tasks in thy list:
        1.[D][ ] study /byte encoding (by: Aug 23 2026, 6:00PM)
        2.[E][ ] prepare /today notes (from: Aug 23 2026, 2:00PM to: Aug 23 2026, 4:00PM)
____________________________________________________________
____________________________________________________________
Avon:	Fare thee well! Pray heavens our paths cross anon.
____________________________________________________________
```
