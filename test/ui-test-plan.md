# UI Test Plan

## Application and execution

- Main class: `chudgpt.ChudGpt`; Java requirement: Java 25.
- Run from the repository root: `python .codex/skills/test-ui/scripts/run_ui_tests.py`.
- Each case starts a fresh process in an isolated temporary working directory.
- Initial save records are written to that case's `data/tasks.txt`.
- Compare exact stdout after normalizing line endings only. Spaces and the final newline are significant.
- Print each case's input/output transcript and stop at the first failure.
- The runner compiles the console entry point and its Java dependencies, without JavaFX.

The original six scenarios have been reconciled with current implementation behavior: ISO dates,
actual messages, a divider before every command, and explicit NONE priority labels.
Blank ToDos are rejected, and commands consistently validate missing or unexpected arguments.
Malformed save records are reported by line while valid records continue to load.

## JUnit acceptance coverage

Run `./gradlew test checkstyleMain checkstyleTest` with Java 25 (`.\gradlew.bat` on Windows).

| Concern | Main test coverage |
| --- | --- |
| Five keywords, case folding, exact seven-character padding | `PriorityTest` |
| Default NONE, priority changes preserve task fields, null rejection, serialization | `TaskTest` |
| Creation syntax, command shape, description safety, date ordering, index syntax | `ParserTest` |
| Duplicate rejection, stable sort, completion independence, empty/single/tied lists | `TaskListTest` |
| Description-only search with priority labels | `TaskListTest` |
| New-format round trips, mixed records, legacy migration only on save, malformed priorities | `StorageTest` |
| Line-level recovery, duplicate records, backups, invalid dates, save failures | `StorageTest` |
| Priority update/repeat/clear, persistence and rejected commands without file changes | `ChangeTaskPriorityCommandTest` |
| Sorted view uses no storage and preserves normal-list numbering | `ListCommandTest` |
| Add, delete, status, find, greeting, save, and exit command behavior and persistence | `CommandTest` |
| All console message variants, logo loading, input reading, and output display | `UiTest` |
| Missing and invalid storage paths, immutable load results, and invalid constructor inputs | `StorageTest` |
| Task completion toggles and duplicate-detail comparison branches | `TaskTest`, `TaskListTest` |
| Domain exception messages and chained causes | `ChudExceptionTest` |
| Sorted output followed by mark/priority/delete, restart, errors, invalid list arguments | `ChudGptTest` |

## Test Case 1: Greet and exit

Aim: Preserve greeting, goodbye, and console dividers.

Inputs:

```text
hi
bye
```

Expected output:

```text
____________________________________________________________
  ____ _               _  ____ ____ _____ 
 / ___| |__  _   _  __| |/ ___|  _ \_   _|
| |   | '_ \| | | |/ _` | |  _| |_) || |  
| |___| | | | |_| | (_| | |_| |  __/ | |  
 \____|_| |_|\__,_|\__,_|\____|_|    |_|  

Hello! I'm ChudGPT.
What can I do for you?
____________________________________________________________
____________________________________________________________
Hi! I'm ChudGPT. How can I help you?
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test Case 2: Default priority and completion

Aim: Show NONE by default and retain the existing mark and unmark responses.

Inputs:

```text
todo read book
list
mark 1
unmark 1
bye
```

Expected output:

```text
____________________________________________________________
  ____ _               _  ____ ____ _____ 
 / ___| |__  _   _  __| |/ ___|  _ \_   _|
| |   | '_ \| | | |/ _` | |  _| |_) || |  
| |___| | | | |_| | (_| | |_| |  __/ | |  
 \____|_| |_|\__,_|\__,_|\____|_|    |_|  

Hello! I'm ChudGPT.
What can I do for you?
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ][P:NONE   ] read book
Now you have 1 tasks in your list.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1. [T][ ][P:NONE   ] read book
____________________________________________________________
____________________________________________________________
Nice! I've marked this task as completed!
  [T][X][P:NONE   ] read book
____________________________________________________________
____________________________________________________________
OK, I've marked this task as incomplete.
  [T][ ][P:NONE   ] read book
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test Case 3: Prioritized deadlines and events

Aim: Parse optional priorities after ISO dates and preserve dates in displayed tasks.

Inputs:

```text
deadline return book /by 2026-10-01 /priority HIGH
event meeting /from 2026-10-01 /to 2026-10-02 /priority low
list
bye
```

Expected output:

```text
____________________________________________________________
  ____ _               _  ____ ____ _____ 
 / ___| |__  _   _  __| |/ ___|  _ \_   _|
| |   | '_ \| | | |/ _` | |  _| |_) || |  
| |___| | | | |_| | (_| | |_| |  __/ | |  
 \____|_| |_|\__,_|\__,_|\____|_|    |_|  

Hello! I'm ChudGPT.
What can I do for you?
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [D][ ][P:HIGH   ] return book (by: 2026-10-01)
Now you have 1 tasks in your list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [E][ ][P:LOW    ] meeting (from: 2026-10-01 to: 2026-10-02)
Now you have 2 tasks in your list.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1. [D][ ][P:HIGH   ] return book (by: 2026-10-01)
2. [E][ ][P:LOW    ] meeting (from: 2026-10-01 to: 2026-10-02)
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test Case 4: Invalid task numbers

Aim: Reject invalid mark and priority indices without adding any tasks.

Inputs:

```text
mark 1
priority 1 high
list
bye
```

Expected output:

```text
____________________________________________________________
  ____ _               _  ____ ____ _____ 
 / ___| |__  _   _  __| |/ ___|  _ \_   _|
| |   | '_ \| | | |/ _` | |  _| |_) || |  
| |___| | | | |_| | (_| | |_| |  __/ | |  
 \____|_| |_|\__,_|\__,_|\____|_|    |_|  

Hello! I'm ChudGPT.
What can I do for you?
____________________________________________________________
____________________________________________________________
OOPS!!! I've run into an error :( I'm such a chud...
Details:
  Task number 1 does not exist.
____________________________________________________________
____________________________________________________________
OOPS!!! I've run into an error :( I'm such a chud...
Details:
  Task number 1 does not exist.
____________________________________________________________
____________________________________________________________
You have no tasks in your list! Try adding some
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test Case 5: Unsupported and blank commands

Aim: Preserve error messages and both empty-list views; the blank input line is significant.

Inputs:

```text
buy groceries

list
list /sort priority
bye
```

Expected output:

```text
____________________________________________________________
  ____ _               _  ____ ____ _____ 
 / ___| |__  _   _  __| |/ ___|  _ \_   _|
| |   | '_ \| | | |/ _` | |  _| |_) || |  
| |___| | | | |_| | (_| | |_| |  __/ | |  
 \____|_| |_|\__,_|\__,_|\____|_|    |_|  

Hello! I'm ChudGPT.
What can I do for you?
____________________________________________________________
____________________________________________________________
OOPS!!! I've run into an error :( I'm such a chud...
Details:
  Invalid command.
____________________________________________________________
____________________________________________________________
OOPS!!! I've run into an error :( I'm such a chud...
Details:
  Please enter a command.
____________________________________________________________
____________________________________________________________
You have no tasks in your list! Try adding some
____________________________________________________________
____________________________________________________________
You have no tasks in your list! Try adding some
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test Case 6: Load legacy tasks

Aim: Load all three legacy task types with NONE from data/tasks.txt.

Initial save file:

```text
T | 1 | read book
D | 0 | return book | 2026-10-01
E | 0 | meeting | 2026-10-01 | 2026-10-02
```

Inputs:

```text
list
bye
```

Expected output:

```text
____________________________________________________________
  ____ _               _  ____ ____ _____ 
 / ___| |__  _   _  __| |/ ___|  _ \_   _|
| |   | '_ \| | | |/ _` | |  _| |_) || |  
| |___| | | | |_| | (_| | |_| |  __/ | |  
 \____|_| |_|\__,_|\__,_|\____|_|    |_|  

Hello! I'm ChudGPT.
What can I do for you?
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1. [T][X][P:NONE   ] read book
2. [D][ ][P:NONE   ] return book (by: 2026-10-01)
3. [E][ ][P:NONE   ] meeting (from: 2026-10-01 to: 2026-10-02)
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test Case 7: Set repeat clear find and delete

Aim: Use one priority confirmation pattern and display padding consistently in find and delete responses.

Inputs:

```text
todo read book /priority low
priority 1 ExTrEmE
priority 1 extreme
priority 1 none
find book
delete 1
bye
```

Expected output:

```text
____________________________________________________________
  ____ _               _  ____ ____ _____ 
 / ___| |__  _   _  __| |/ ___|  _ \_   _|
| |   | '_ \| | | |/ _` | |  _| |_) || |  
| |___| | | | |_| | (_| | |_| |  __/ | |  
 \____|_| |_|\__,_|\__,_|\____|_|    |_|  

Hello! I'm ChudGPT.
What can I do for you?
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ][P:LOW    ] read book
Now you have 1 tasks in your list.
____________________________________________________________
____________________________________________________________
Got it. I've set this task's priority to EXTREME:
  [T][ ][P:EXTREME] read book
____________________________________________________________
____________________________________________________________
Got it. I've set this task's priority to EXTREME:
  [T][ ][P:EXTREME] read book
____________________________________________________________
____________________________________________________________
Got it. I've removed the priority from this task:
  [T][ ][P:NONE   ] read book
____________________________________________________________
____________________________________________________________
Here are the matching tasks in your list:
1. [T][ ][P:NONE   ] read book
____________________________________________________________
____________________________________________________________
Got it. Noted. I've removed this task:
  [T][ ][P:NONE   ] read book
Now you have 0 tasks in your list.
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test Case 8: Priority validation

Aim: Reject missing, duplicate, misplaced, numeric, and unknown priorities; every failed creation leaves the list empty.

Inputs:

```text
todo book /priority
todo book /priority high /priority low
deadline book /priority high /by 2026-10-01
todo book /priority 1
priority 1 urgent
priority 1
priority one high
list
bye
```

Expected output:

```text
____________________________________________________________
  ____ _               _  ____ ____ _____ 
 / ___| |__  _   _  __| |/ ___|  _ \_   _|
| |   | '_ \| | | |/ _` | |  _| |_) || |  
| |___| | | | |_| | (_| | |_| |  __/ | |  
 \____|_| |_|\__,_|\__,_|\____|_|    |_|  

Hello! I'm ChudGPT.
What can I do for you?
____________________________________________________________
____________________________________________________________
OOPS!!! I've run into an error :( I'm such a chud...
Details:
  The /priority argument requires a priority value.
____________________________________________________________
____________________________________________________________
OOPS!!! I've run into an error :( I'm such a chud...
Details:
  The /priority argument can only be specified once.
____________________________________________________________
____________________________________________________________
OOPS!!! I've run into an error :( I'm such a chud...
Details:
  The /priority argument must be the final argument.
____________________________________________________________
____________________________________________________________
OOPS!!! I've run into an error :( I'm such a chud...
Details:
  Priority must be one of: EXTREME, HIGH, MEDIUM, LOW, NONE.
____________________________________________________________
____________________________________________________________
OOPS!!! I've run into an error :( I'm such a chud...
Details:
  Priority must be one of: EXTREME, HIGH, MEDIUM, LOW, NONE.
____________________________________________________________
____________________________________________________________
OOPS!!! I've run into an error :( I'm such a chud...
Details:
  Usage: priority <task number> <priority>.
____________________________________________________________
____________________________________________________________
OOPS!!! I've run into an error :( I'm such a chud...
Details:
  Task number must be a positive whole number.
____________________________________________________________
____________________________________________________________
You have no tasks in your list! Try adding some
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test Case 9: Stable sorted view and usable task numbers

Aim: Sort all five levels, retain ties and completed tasks, and update using the displayed underlying number.

Initial save file:

```text
T | 0 | MEDIUM | write report
T | 1 | EXTREME | urgent first
T | 0 | HIGH | review code
T | 0 | EXTREME | urgent second
T | 0 | NONE | buy milk
T | 0 | LOW | read book
```

Inputs:

```text
LIST /SORT PRIORITY
priority 4 none
list
bye
```

Expected output:

```text
____________________________________________________________
  ____ _               _  ____ ____ _____ 
 / ___| |__  _   _  __| |/ ___|  _ \_   _|
| |   | '_ \| | | |/ _` | |  _| |_) || |  
| |___| | | | |_| | (_| | |_| |  __/ | |  
 \____|_| |_|\__,_|\__,_|\____|_|    |_|  

Hello! I'm ChudGPT.
What can I do for you?
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
2. [T][X][P:EXTREME] urgent first
4. [T][ ][P:EXTREME] urgent second
3. [T][ ][P:HIGH   ] review code
1. [T][ ][P:MEDIUM ] write report
6. [T][ ][P:LOW    ] read book
5. [T][ ][P:NONE   ] buy milk
____________________________________________________________
____________________________________________________________
Got it. I've removed the priority from this task:
  [T][ ][P:NONE   ] urgent second
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1. [T][ ][P:MEDIUM ] write report
2. [T][X][P:EXTREME] urgent first
3. [T][ ][P:HIGH   ] review code
4. [T][ ][P:NONE   ] urgent second
5. [T][ ][P:NONE   ] buy milk
6. [T][ ][P:LOW    ] read book
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test Case 10: Mixed save formats and invalid explicit priorities

Aim: Accept uppercase saved priorities and legacy priority-like descriptions; skip malformed new records.

Initial save file:

```text
T | 0 | HIGH
T | 0 | HIGH | new task
T | 0 | high | invalid lowercase priority
D | 0 | | invalid priority | 2026-10-01
E | 0 | URGENT | invalid priority | 2026-10-01 | 2026-10-02
D | 1 | MEDIUM | return book | 2026-10-01
E | 0 | LOW | meeting | 2026-10-01 | 2026-10-02
```

Inputs:

```text
list
bye
```

Expected output:

```text
____________________________________________________________
  ____ _               _  ____ ____ _____ 
 / ___| |__  _   _  __| |/ ___|  _ \_   _|
| |   | '_ \| | | |/ _` | |  _| |_) || |  
| |___| | | | |_| | (_| | |_| |  __/ | |  
 \____|_| |_|\__,_|\__,_|\____|_|    |_|  

Hello! I'm ChudGPT.
What can I do for you?
____________________________________________________________
WARNING: Some saved tasks could not be loaded.
Details:
  Line 3 was skipped: The priority value is invalid.
  Line 4 was skipped: The priority value is invalid.
  Line 5 was skipped: The priority value is invalid.
A backup will be created before the recovered task list is saved.
____________________________________________________________
Here are the tasks in your list:
1. [T][ ][P:NONE   ] HIGH
2. [T][ ][P:HIGH   ] new task
3. [D][X][P:MEDIUM ] return book (by: 2026-10-01)
4. [E][ ][P:LOW    ] meeting (from: 2026-10-01 to: 2026-10-02)
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test Case 11: Expanded input validation

Aim: Reject empty descriptions, malformed command shapes, unsafe descriptions, invalid task numbers,
non-increasing event dates, and normalized duplicate tasks without changing the valid task list.

Inputs:

```text
todo
hi extra
delete
delete 0
delete one
event meeting /from 2026-10-01 /to 2026-10-01
todo unsafe | description
todo Read   Book
todo read book /priority high
list
bye
```

Expected output:

```text
____________________________________________________________
  ____ _               _  ____ ____ _____ 
 / ___| |__  _   _  __| |/ ___|  _ \_   _|
| |   | '_ \| | | |/ _` | |  _| |_) || |  
| |___| | | | |_| | (_| | |_| |  __/ | |  
 \____|_| |_|\__,_|\__,_|\____|_|    |_|  

Hello! I'm ChudGPT.
What can I do for you?
____________________________________________________________
____________________________________________________________
OOPS!!! I've run into an error :( I'm such a chud...
Details:
  Task description cannot be empty.
____________________________________________________________
____________________________________________________________
OOPS!!! I've run into an error :( I'm such a chud...
Details:
  Usage: hi.
____________________________________________________________
____________________________________________________________
OOPS!!! I've run into an error :( I'm such a chud...
Details:
  Usage: delete <task number>.
____________________________________________________________
____________________________________________________________
OOPS!!! I've run into an error :( I'm such a chud...
Details:
  Task numbers start from 1.
____________________________________________________________
____________________________________________________________
OOPS!!! I've run into an error :( I'm such a chud...
Details:
  Task number must be a positive whole number.
____________________________________________________________
____________________________________________________________
OOPS!!! I've run into an error :( I'm such a chud...
Details:
  Event end date must be later than its start date.
____________________________________________________________
____________________________________________________________
OOPS!!! I've run into an error :( I'm such a chud...
Details:
  Task description cannot contain '|' or control characters.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ][P:NONE   ] read   book
Now you have 1 tasks in your list.
____________________________________________________________
____________________________________________________________
OOPS!!! I've run into an error :( I'm such a chud...
Details:
  This task duplicates task 1.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1. [T][ ][P:NONE   ] read   book
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```
