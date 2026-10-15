# UI Test Plan

Each case is run in a fresh Zeus process. Inputs are sent in order through standard input. Expected output contains only program output, so adjacent separators represent the points where console input appears during an interactive session.

## TC-1: Add and list all task types

**Aim:** Verify that `todo`, `deadline`, and `event` create the correct task types and `list` displays their details in insertion order.

### Input

```text
todo borrow book
deadline return book /by 2026-09-06
event project meeting /from 2026-09-07 /to 2026-09-08
list
bye
```

### Expected output

```text
____________________________________________________________
 _____
|__  /___ _   _ ___
  / // _ \ | | / __|
 / /|  __/ |_| \__ \
/____\___|\__,_|___/
Zeus awaits your command. ⚡
Type 'help' to see available commands.
____________________________________________________________
____________________________________________________________
It is decreed. Your task has been added.
  [T][ ] borrow book
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
It is decreed. Your task has been added.
  [D][ ] return book (by: Sep 6 2026)
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
It is decreed. Your task has been added.
  [E][ ] project meeting (from: Sep 7 2026 to: Sep 8 2026)
Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] borrow book
2.[D][ ] return book (by: Sep 6 2026)
3.[E][ ] project meeting (from: Sep 7 2026 to: Sep 8 2026)
____________________________________________________________
____________________________________________________________
Farewell, mortal.
____________________________________________________________
```

## TC-2: Mark typed tasks as done

**Aim:** Verify that `mark N` updates inherited task status while preserving each task's subtype formatting.

### Input

```text
todo read book
deadline return book /by 2026-06-06
mark 1
mark 2
list
bye
```

### Expected output

```text
____________________________________________________________
 _____
|__  /___ _   _ ___
  / // _ \ | | / __|
 / /|  __/ |_| \__ \
/____\___|\__,_|___/
Zeus awaits your command. ⚡
Type 'help' to see available commands.
____________________________________________________________
____________________________________________________________
It is decreed. Your task has been added.
  [T][ ] read book
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
It is decreed. Your task has been added.
  [D][ ] return book (by: Jun 6 2026)
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
A worthy task completed.
  [T][X] read book
____________________________________________________________
____________________________________________________________
A worthy task completed.
  [D][X] return book (by: Jun 6 2026)
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][X] read book
2.[D][X] return book (by: Jun 6 2026)
____________________________________________________________
____________________________________________________________
Farewell, mortal.
____________________________________________________________
```

## TC-3: Unmark a typed task

**Aim:** Verify that `unmark N` reverses inherited task status without losing deadline details.

### Input

```text
todo read book
deadline return book /by 2026-06-06
mark 2
unmark 2
list
bye
```

### Expected output

```text
____________________________________________________________
 _____
|__  /___ _   _ ___
  / // _ \ | | / __|
 / /|  __/ |_| \__ \
/____\___|\__,_|___/
Zeus awaits your command. ⚡
Type 'help' to see available commands.
____________________________________________________________
____________________________________________________________
It is decreed. Your task has been added.
  [T][ ] read book
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
It is decreed. Your task has been added.
  [D][ ] return book (by: Jun 6 2026)
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
A worthy task completed.
  [D][X] return book (by: Jun 6 2026)
____________________________________________________________
____________________________________________________________
OK, I've marked this task as not done yet:
  [D][ ] return book (by: Jun 6 2026)
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] read book
2.[D][ ] return book (by: Jun 6 2026)
____________________________________________________________
____________________________________________________________
Farewell, mortal.
____________________________________________________________
```

## TC-4: Parse and format ISO dates

**Aim:** Verify that valid ISO deadline and event dates are stored as dates and displayed in a friendlier format, including a leap day.

### Input

```text
deadline do homework /by 2028-02-29
event orientation week /from 2019-10-04 /to 2019-10-11
list
bye
```

### Expected output

```text
____________________________________________________________
 _____
|__  /___ _   _ ___
  / // _ \ | | / __|
 / /|  __/ |_| \__ \
/____\___|\__,_|___/
Zeus awaits your command. ⚡
Type 'help' to see available commands.
____________________________________________________________
____________________________________________________________
It is decreed. Your task has been added.
  [D][ ] do homework (by: Feb 29 2028)
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
It is decreed. Your task has been added.
  [E][ ] orientation week (from: Oct 4 2019 to: Oct 11 2019)
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[D][ ] do homework (by: Feb 29 2028)
2.[E][ ] orientation week (from: Oct 4 2019 to: Oct 11 2019)
____________________________________________________________
____________________________________________________________
Farewell, mortal.
____________________________________________________________
```

## TC-5: Reject empty and unknown commands

**Aim:** Verify that empty task commands and unknown commands produce helpful errors without stopping Zeus.

### Input

```text
todo
deadline
event
find
blah
bye
```

### Expected output

```text
____________________________________________________________
 _____
|__  /___ _   _ ___
  / // _ \ | | / __|
 / /|  __/ |_| \__ \
/____\___|\__,_|___/
Zeus awaits your command. ⚡
Type 'help' to see available commands.
____________________________________________________________
____________________________________________________________
OOPS!!! A todo needs a description after 'todo'.
____________________________________________________________
____________________________________________________________
OOPS!!! A deadline needs a description and '/by' date.
____________________________________________________________
____________________________________________________________
OOPS!!! An event needs a description, '/from' start date, and '/to' end date.
____________________________________________________________
____________________________________________________________
OOPS!!! Tell me what to find, for example 'find book'.
____________________________________________________________
____________________________________________________________
OOPS!!! Even Zeus cannot understand that command. Type 'help' to see available commands.
____________________________________________________________
____________________________________________________________
Farewell, mortal.
____________________________________________________________
```

## TC-6: Reject malformed deadline and event details

**Aim:** Verify that missing details, non-ISO dates, impossible dates, and reversed event dates produce field-specific errors.

### Input

```text
deadline submit report
deadline /by 2026-10-10
deadline submit report /by
deadline submit report /by Sunday
deadline impossible date /by 2025-02-29
event meeting
event /from 2026-10-10 /to 2026-10-11
event meeting /from /to 2026-10-11
event meeting /from 2026-10-10
event meeting /from 2026-10-10 /to
event meeting /from Monday /to 2026-10-11
event meeting /from 2026-10-10 /to Tuesday
event trip /from 2026-10-12 /to 2026-10-11
bye
```

### Expected output

```text
____________________________________________________________
 _____
|__  /___ _   _ ___
  / // _ \ | | / __|
 / /|  __/ |_| \__ \
/____\___|\__,_|___/
Zeus awaits your command. ⚡
Type 'help' to see available commands.
____________________________________________________________
____________________________________________________________
OOPS!!! A deadline needs a '/by' date.
____________________________________________________________
____________________________________________________________
OOPS!!! A deadline needs a description before '/by'.
____________________________________________________________
____________________________________________________________
OOPS!!! A deadline needs a date after '/by'.
____________________________________________________________
____________________________________________________________
OOPS!!! The deadline date must use yyyy-MM-dd, for example 2019-10-15.
____________________________________________________________
____________________________________________________________
OOPS!!! The deadline date must use yyyy-MM-dd, for example 2019-10-15.
____________________________________________________________
____________________________________________________________
OOPS!!! An event needs a start date after '/from'.
____________________________________________________________
____________________________________________________________
OOPS!!! An event needs a description before '/from'.
____________________________________________________________
____________________________________________________________
OOPS!!! An event needs a start date after '/from'.
____________________________________________________________
____________________________________________________________
OOPS!!! An event needs an end date after '/to'.
____________________________________________________________
____________________________________________________________
OOPS!!! An event needs an end date after '/to'.
____________________________________________________________
____________________________________________________________
OOPS!!! The event start date must use yyyy-MM-dd, for example 2019-10-15.
____________________________________________________________
____________________________________________________________
OOPS!!! The event end date must use yyyy-MM-dd, for example 2019-10-15.
____________________________________________________________
____________________________________________________________
OOPS!!! The event end date cannot be before its start date.
____________________________________________________________
____________________________________________________________
Farewell, mortal.
____________________________________________________________
```

## TC-7: Reject invalid task numbers

**Aim:** Verify that status commands reject missing, non-numeric, and out-of-range task numbers without changing tasks.

### Input

```text
mark
mark two
mark 1
todo valid task
mark 0
mark 2
unmark
unmark two
unmark 2
list
bye
```

### Expected output

```text
____________________________________________________________
 _____
|__  /___ _   _ ___
  / // _ \ | | / __|
 / /|  __/ |_| \__ \
/____\___|\__,_|___/
Zeus awaits your command. ⚡
Type 'help' to see available commands.
____________________________________________________________
____________________________________________________________
OOPS!!! Tell me which task to mark, for example 'mark 1'.
____________________________________________________________
____________________________________________________________
OOPS!!! The task number must be a whole number.
____________________________________________________________
____________________________________________________________
OOPS!!! Your task list is empty.
____________________________________________________________
____________________________________________________________
It is decreed. Your task has been added.
  [T][ ] valid task
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
OOPS!!! There is no task number 0. Choose a number from 1 to 1.
____________________________________________________________
____________________________________________________________
OOPS!!! There is no task number 2. Choose a number from 1 to 1.
____________________________________________________________
____________________________________________________________
OOPS!!! Tell me which task to unmark, for example 'unmark 1'.
____________________________________________________________
____________________________________________________________
OOPS!!! The task number must be a whole number.
____________________________________________________________
____________________________________________________________
OOPS!!! There is no task number 2. Choose a number from 1 to 1.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] valid task
____________________________________________________________
____________________________________________________________
Farewell, mortal.
____________________________________________________________
```

## TC-8: Delete a task and renumber the list

**Aim:** Verify that `delete N` removes the selected subtype, reports it, and shifts later task numbers down.

### Input

```text
todo read book
deadline return book /by 2026-06-06
event project meeting /from 2026-08-06 /to 2026-08-07
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
 _____
|__  /___ _   _ ___
  / // _ \ | | / __|
 / /|  __/ |_| \__ \
/____\___|\__,_|___/
Zeus awaits your command. ⚡
Type 'help' to see available commands.
____________________________________________________________
____________________________________________________________
It is decreed. Your task has been added.
  [T][ ] read book
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
It is decreed. Your task has been added.
  [D][ ] return book (by: Jun 6 2026)
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
It is decreed. Your task has been added.
  [E][ ] project meeting (from: Aug 6 2026 to: Aug 7 2026)
Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
It is decreed. Your task has been added.
  [T][ ] join sports club
Now you have 4 tasks in the list.
____________________________________________________________
____________________________________________________________
It is decreed. Your task has been added.
  [T][ ] borrow book
Now you have 5 tasks in the list.
____________________________________________________________
____________________________________________________________
A worthy task completed.
  [T][X] read book
____________________________________________________________
____________________________________________________________
A worthy task completed.
  [D][X] return book (by: Jun 6 2026)
____________________________________________________________
____________________________________________________________
A worthy task completed.
  [T][X] join sports club
____________________________________________________________
____________________________________________________________
Noted. I've removed this task:
  [E][ ] project meeting (from: Aug 6 2026 to: Aug 7 2026)
Now you have 4 tasks in the list.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][X] read book
2.[D][X] return book (by: Jun 6 2026)
3.[T][X] join sports club
4.[T][ ] borrow book
____________________________________________________________
____________________________________________________________
Farewell, mortal.
____________________________________________________________
```

## TC-9: Reject invalid deletion numbers

**Aim:** Verify that delete rejects missing, non-numeric, empty-list, and out-of-range task numbers without removing anything.

### Input

```text
delete
delete two
delete 1
todo keep me
delete 0
delete 2
list
bye
```

### Expected output

```text
____________________________________________________________
 _____
|__  /___ _   _ ___
  / // _ \ | | / __|
 / /|  __/ |_| \__ \
/____\___|\__,_|___/
Zeus awaits your command. ⚡
Type 'help' to see available commands.
____________________________________________________________
____________________________________________________________
OOPS!!! Tell me which task to delete, for example 'delete 1'.
____________________________________________________________
____________________________________________________________
OOPS!!! The task number must be a whole number.
____________________________________________________________
____________________________________________________________
OOPS!!! Your task list is empty.
____________________________________________________________
____________________________________________________________
It is decreed. Your task has been added.
  [T][ ] keep me
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
OOPS!!! There is no task number 0. Choose a number from 1 to 1.
____________________________________________________________
____________________________________________________________
OOPS!!! There is no task number 2. Choose a number from 1 to 1.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] keep me
____________________________________________________________
____________________________________________________________
Farewell, mortal.
____________________________________________________________
```

## TC-10: Save task changes to disk

**Aim:** Verify that adding, marking, and deleting tasks writes the latest task list to `data/zeus.txt` in the serialized format.

### Input

```text
todo read book
deadline return book /by 2026-06-06
event project meeting /from 2026-08-06 /to 2026-08-07
mark 1
delete 2
bye
```

### Expected output

```text
____________________________________________________________
 _____
|__  /___ _   _ ___
  / // _ \ | | / __|
 / /|  __/ |_| \__ \
/____\___|\__,_|___/
Zeus awaits your command. ⚡
Type 'help' to see available commands.
____________________________________________________________
____________________________________________________________
It is decreed. Your task has been added.
  [T][ ] read book
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
It is decreed. Your task has been added.
  [D][ ] return book (by: Jun 6 2026)
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
It is decreed. Your task has been added.
  [E][ ] project meeting (from: Aug 6 2026 to: Aug 7 2026)
Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
A worthy task completed.
  [T][X] read book
____________________________________________________________
____________________________________________________________
Noted. I've removed this task:
  [D][ ] return book (by: Jun 6 2026)
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Farewell, mortal.
____________________________________________________________
```

### Expected data file

```text
T | 1 | read book
E | 0 | project meeting | 2026-08-06 | 2026-08-07
```

## TC-11: Load all saved task types

**Aim:** Verify that Zeus loads todos, deadlines, and events from `data/zeus.txt`, including their saved completion status and original order.

### Initial data file

```text
T | 1 | read book
D | 0 | return book | 2026-06-06
E | 1 | project meeting | 2026-08-06 | 2026-08-07
```

### Input

```text
list
bye
```

### Expected output

```text
____________________________________________________________
 _____
|__  /___ _   _ ___
  / // _ \ | | / __|
 / /|  __/ |_| \__ \
/____\___|\__,_|___/
Zeus awaits your command. ⚡
Type 'help' to see available commands.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][X] read book
2.[D][ ] return book (by: Jun 6 2026)
3.[E][X] project meeting (from: Aug 6 2026 to: Aug 7 2026)
____________________________________________________________
____________________________________________________________
Farewell, mortal.
____________________________________________________________
```

### Expected data file

```text
T | 1 | read book
D | 0 | return book | 2026-06-06
E | 1 | project meeting | 2026-08-06 | 2026-08-07
```

## TC-12: Start without a data file

**Aim:** Verify that a missing `data/zeus.txt` is treated as a new empty task list without displaying an error.

### Input

```text
list
bye
```

### Expected output

```text
____________________________________________________________
 _____
|__  /___ _   _ ___
  / // _ \ | | / __|
 / /|  __/ |_| \__ \
/____\___|\__,_|___/
Zeus awaits your command. ⚡
Type 'help' to see available commands.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
____________________________________________________________
____________________________________________________________
Farewell, mortal.
____________________________________________________________
```

## TC-13: Recover from malformed saved records

**Aim:** Verify that blank lines are ignored, every malformed saved record gets a specific warning, valid records still load, and the next task change rewrites only valid tasks.

### Initial data file

```text
T | 1 | valid todo

X | 0 | unknown type
D | 2 | bad status | Monday
D | 0 | | Monday
D | 0 | missing by
D | 0 | no date |
E | 0 | valid event | 2026-09-07 | 2026-09-08
E | 0 | missing end | 2026-09-07 |
D | 0 | impossible deadline | 2025-02-29
E | 0 | backwards | 2026-09-08 | 2026-09-07
T | 0 | bad \q escape
T | 0 | trailing \
garbage
```

### Input

```text
list
todo recovered task
bye
```

### Expected output

```text
____________________________________________________________
 _____
|__  /___ _   _ ___
  / // _ \ | | / __|
 / /|  __/ |_| \__ \
/____\___|\__,_|___/
Zeus awaits your command. ⚡
Type 'help' to see available commands.
____________________________________________________________
OOPS!!! Saved data line 3 was ignored: Unknown task type 'X'.
OOPS!!! Saved data line 4 was ignored: Completion status must be 0 or 1, not '2'.
OOPS!!! Saved data line 5 was ignored: The task description is empty.
OOPS!!! Saved data line 6 was ignored: Task type 'D' needs 4 fields, but this record has 3.
OOPS!!! Saved data line 7 was ignored: The deadline's '/by' value is empty.
OOPS!!! Saved data line 9 was ignored: The event's '/to' value is empty.
OOPS!!! Saved data line 10 was ignored: The deadline date must use yyyy-MM-dd, for example 2019-10-15.
OOPS!!! Saved data line 11 was ignored: The event end date cannot be before its start date.
OOPS!!! Saved data line 12 was ignored: Invalid escape sequence '\q'.
OOPS!!! Saved data line 13 was ignored: The record ends with an incomplete escape sequence.
OOPS!!! Saved data line 14 was ignored: A record needs a task type and completion status.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][X] valid todo
2.[E][ ] valid event (from: Sep 7 2026 to: Sep 8 2026)
____________________________________________________________
____________________________________________________________
It is decreed. Your task has been added.
  [T][ ] recovered task
Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
Farewell, mortal.
____________________________________________________________
```

### Expected data file

```text
T | 1 | valid todo
E | 0 | valid event | 2026-09-07 | 2026-09-08
T | 0 | recovered task
```

## TC-14: Preserve storage separator and escape characters

**Aim:** Verify that escaped pipe and backslash characters load as ordinary task text and are escaped again when Zeus saves the list.

### Initial data file

```text
T | 0 | compare A \| B
T | 1 | use path C:\\Temp
```

### Input

```text
list
todo back up C:\Temp | archive
bye
```

### Expected output

```text
____________________________________________________________
 _____
|__  /___ _   _ ___
  / // _ \ | | / __|
 / /|  __/ |_| \__ \
/____\___|\__,_|___/
Zeus awaits your command. ⚡
Type 'help' to see available commands.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] compare A | B
2.[T][X] use path C:\Temp
____________________________________________________________
____________________________________________________________
It is decreed. Your task has been added.
  [T][ ] back up C:\Temp | archive
Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
Farewell, mortal.
____________________________________________________________
```

### Expected data file

```text
T | 0 | compare A \| B
T | 1 | use path C:\\Temp
T | 0 | back up C:\\Temp \| archive
```

## TC-15: Find tasks by description keyword

**Aim:** Verify that `find KEYWORD` displays only matching tasks in their original order, renumbers each result set, leaves an empty result readable, and does not modify stored tasks.

### Initial data file

```text
T | 1 | read book
D | 0 | return book | 2026-06-06
E | 1 | project meeting | 2026-08-06 | 2026-08-07
```

### Input

```text
find book
find meeting
find missing
bye
```

### Expected output

```text
____________________________________________________________
 _____
|__  /___ _   _ ___
  / // _ \ | | / __|
 / /|  __/ |_| \__ \
/____\___|\__,_|___/
Zeus awaits your command. ⚡
Type 'help' to see available commands.
____________________________________________________________
____________________________________________________________
Here are the matching tasks in your list:
1.[T][X] read book
2.[D][ ] return book (by: Jun 6 2026)
____________________________________________________________
____________________________________________________________
Here are the matching tasks in your list:
1.[E][X] project meeting (from: Aug 6 2026 to: Aug 7 2026)
____________________________________________________________
____________________________________________________________
Here are the matching tasks in your list:
____________________________________________________________
____________________________________________________________
Farewell, mortal.
____________________________________________________________
```

### Expected data file

```text
T | 1 | read book
D | 0 | return book | 2026-06-06
E | 1 | project meeting | 2026-08-06 | 2026-08-07
```

## TC-16: Display command help

**Aim:** Verify that `help` displays all supported commands without changing saved tasks, rejects arguments, and remains case-sensitive.

### Initial data file

```text
T | 0 | read book
```

### Input

```text
help
help todo
HELP
bye
```

### Expected output

```text
____________________________________________________________
 _____
|__  /___ _   _ ___
  / // _ \ | | / __|
 / /|  __/ |_| \__ \
/____\___|\__,_|___/
Zeus awaits your command. ⚡
Type 'help' to see available commands.
____________________________________________________________
____________________________________________________________
Here are the commands you can use:
  todo DESCRIPTION - Add a task without a date.
  deadline DESCRIPTION /by yyyy-MM-dd - Add a task with a due date.
  event DESCRIPTION /from yyyy-MM-dd /to yyyy-MM-dd - Add a task with start and end dates.
  list - Show all tasks.
  find KEYWORD - Find tasks whose descriptions contain the keyword.
  mark NUMBER - Mark the numbered task as done.
  unmark NUMBER - Mark the numbered task as not done.
  delete NUMBER - Delete the numbered task.
  help - Show this help page.
  bye - Exit Zeus.

Dates must use yyyy-MM-dd, for example 2026-09-06.
____________________________________________________________
____________________________________________________________
OOPS!!! The help command does not take any arguments. Type 'help' on its own.
____________________________________________________________
____________________________________________________________
OOPS!!! Even Zeus cannot understand that command. Type 'help' to see available commands.
____________________________________________________________
____________________________________________________________
Farewell, mortal.
____________________________________________________________
```

### Expected data file

```text
T | 0 | read book
```
