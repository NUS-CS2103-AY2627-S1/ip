# Nova UI Test Plan

Run cases in order with Java 25. Each case starts Nova in a fresh process and temporary working directory.

Manual GUI check (A-BetterGui):

- Aim: Verify the 14px GUI font remains readable without clipping at the default window size.
- Input: Launch the GUI, enter `help` using Enter, then `list` using Send.
- Expected output: `help` displays the command list shown in TC8; `list` displays the current tasks.
  Message text, the input field, and Send use the larger font. Long replies wrap inside their bubbles,
  all lines remain accessible by scrolling, and the input field and Send text are not clipped.
  User messages remain blue and right-aligned; Nova replies remain gray and left-aligned.
  This visual check is manual and is not run by the console test runner.

### Failed-load GUI regression (also covered by NovaTest)

Aim: Verify a failed load cannot overwrite existing data or display misleading empty results,
and leaves only help and exit available.
Use a disposable working folder, not your real task data. Before launching Nova, create
`data/nova.txt` containing `T | 0 | important existing task` followed by a newline and the
invalid UTF-8 byte `FF`. Keep a byte-for-byte copy for comparison. This binary fixture is
covered by JUnit; the console Markdown runner below only accepts text fixtures.

Input: Launch Nova. Expected greeting:

```text
Hello! I'm Nova.
Let's take it one task at a time.
 Oops! I could not load your tasks from the data file.
```

Input: Enter each command separately: `todo new task`, `deadline report /by 2026-09-30`,
`event meeting /from 2pm /to 4pm`, `mark 1`, `unmark 1`, `delete 1`, `todo another task`,
`list`, `find task`, then `on 2026-09-30`.
Exact expected Nova response to every command:

```text
 Oops! I could not load your tasks. Please fix the data file and restart Nova. Only help and bye are available this session.
```

Input: `help`. Exact expected Nova response:

```text
 Here are Nova's commands:
 list
 todo DESCRIPTION
 deadline DESCRIPTION /by yyyy-MM-dd
 event DESCRIPTION /from START /to END
 mark TASK_NUMBER
 unmark TASK_NUMBER
 delete TASK_NUMBER
 find KEYWORD
 on yyyy-MM-dd
 help
 bye
```

Input: `bye`. Exact expected Nova response:

```text
 See you soon. Take care!
```

Expected state: No attempted change adds or removes a task. Input remains usable until `bye`,
which disables the GUI input and Send button. The original data file remains byte-for-byte unchanged.

## TC1: Add and list all task types

Aim: Verify todos, dated deadlines, and events display polymorphically, with deadlines reformatted from yyyy-MM-dd.

### Input

```text
todo borrow book
deadline do homework /by 2019-10-15
event project meeting /from Mon 2pm /to 4pm
list
bye
```

### Expected output

```text
 _   _                  
| \ | | _____   ____ _ 
|  \| |/ _ \ \ / / _` |
| |\  | (_) \ V / (_| |
|_| \_|\___/ \_/ \__,_|

Hello! I'm Nova.
Let's take it one task at a time.
____________________________________________________________
____________________________________________________________
 Got it! Added to your list:
  [T][ ] borrow book
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it! Added to your list:
  [D][ ] do homework (by: Oct 15 2019)
 Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it! Added to your list:
  [E][ ] project meeting (from: Mon 2pm to: 4pm)
 Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] borrow book
 2.[D][ ] do homework (by: Oct 15 2019)
 3.[E][ ] project meeting (from: Mon 2pm to: 4pm)
____________________________________________________________
____________________________________________________________
 See you soon. Take care!
____________________________________________________________
```

## TC2: Delete tasks and reject invalid task numbers

Aim: Verify deletion removes the selected task and renumbers the remaining list, while malformed and out-of-range delete commands report errors without changing the list.

### Input

```text
todo read book
deadline return book /by 2019-06-06
event project meeting /from Aug 6th 2pm /to 4pm
todo join sports club
todo borrow book
delete 3
delete one
delete 9
list
bye
```

### Expected output

```text
 _   _                  
| \ | | _____   ____ _ 
|  \| |/ _ \ \ / / _` |
| |\  | (_) \ V / (_| |
|_| \_|\___/ \_/ \__,_|

Hello! I'm Nova.
Let's take it one task at a time.
____________________________________________________________
____________________________________________________________
 Got it! Added to your list:
  [T][ ] read book
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it! Added to your list:
  [D][ ] return book (by: Jun 6 2019)
 Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it! Added to your list:
  [E][ ] project meeting (from: Aug 6th 2pm to: 4pm)
 Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it! Added to your list:
  [T][ ] join sports club
 Now you have 4 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it! Added to your list:
  [T][ ] borrow book
 Now you have 5 tasks in the list.
____________________________________________________________
____________________________________________________________
 All right. Removed from your list:
  [E][ ] project meeting (from: Aug 6th 2pm to: 4pm)
 Now you have 4 tasks in the list.
____________________________________________________________
____________________________________________________________
 Oops! Please enter a task number, for example: delete 1
____________________________________________________________
____________________________________________________________
 Oops! Task 9 does not exist in the list.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] read book
 2.[D][ ] return book (by: Jun 6 2019)
 3.[T][ ] join sports club
 4.[T][ ] borrow book
____________________________________________________________
____________________________________________________________
 See you soon. Take care!
____________________________________________________________
```

## TC3: Handle invalid commands with NovaException

Aim: Verify invalid commands and malformed or impossible deadline dates produce specific errors without ending the chatbot session or adding a task.

### Input

```text
todo
blah
deadline submit report
deadline submit report /by 2019-02-30
deadline submit report /by June 6th
event meeting /from Mon 2pm
mark one
mark 1
list
bye
```

### Expected output

```text
 _   _                  
| \ | | _____   ____ _ 
|  \| |/ _ \ \ / / _` |
| |\  | (_) \ V / (_| |
|_| \_|\___/ \_/ \__,_|

Hello! I'm Nova.
Let's take it one task at a time.
____________________________________________________________
____________________________________________________________
 Oops! The description of a todo cannot be empty.
____________________________________________________________
____________________________________________________________
 Oops! I'm sorry, but I don't know what that means :-(
____________________________________________________________
____________________________________________________________
 Oops! A deadline must follow: deadline DESCRIPTION /by yyyy-MM-dd
____________________________________________________________
____________________________________________________________
 Oops! The deadline date must be a valid date in yyyy-MM-dd format.
____________________________________________________________
____________________________________________________________
 Oops! The deadline date must be a valid date in yyyy-MM-dd format.
____________________________________________________________
____________________________________________________________
 Oops! An event must follow: event DESCRIPTION /from START /to END
____________________________________________________________
____________________________________________________________
 Oops! Please enter a task number, for example: mark 1
____________________________________________________________
____________________________________________________________
 Oops! Task 1 does not exist in the list.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
____________________________________________________________
____________________________________________________________
 See you soon. Take care!
____________________________________________________________
```

## TC4: Save after completion-state changes

Aim: Verify successful mark and unmark commands remain usable when each task-list change is saved to disk.

### Input

```text
todo write tests
mark 1
unmark 1
list
bye
```

### Expected output

```text
 _   _                  
| \ | | _____   ____ _ 
|  \| |/ _ \ \ / / _` |
| |\  | (_) \ V / (_| |
|_| \_|\___/ \_/ \__,_|

Hello! I'm Nova.
Let's take it one task at a time.
____________________________________________________________
____________________________________________________________
 Got it! Added to your list:
  [T][ ] write tests
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Nice work! Marked as done:
  [T][X] write tests
____________________________________________________________
____________________________________________________________
 No problem. Marked as not done yet:
  [T][ ] write tests
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] write tests
____________________________________________________________
____________________________________________________________
 See you soon. Take care!
____________________________________________________________
```

## TC5: Load saved tasks and skip corrupted records

Aim: Verify startup restores encoded and legacy tasks, including delimiter and Unicode text, while safely ignoring malformed records.

### Initial data file

```text
V2 | T | 1 | cmVhZCB8IGNhZsOp
V2 | D | 0 | cmV0dXJuIGJvb2s= | MjAxOS0wNi0wNg==
V2 | E | 0 | cHJvamVjdCBtZWV0aW5n | QXVnIDZ0aCAycG0= | NHBt
T | 0 | legacy task
V2 | T | 2 | aW52YWxpZCBzdGF0dXM=
V2 | T | 0 | not_base64!
V2 | D | 0 | ZGVhZGxpbmU= |
V2 | D | 0 | aW52YWxpZCBkYXRl | MjAxOS0wMi0zMA==
V2 | X | 0 | dW5rbm93biB0eXBl
corrupted task data
```

### Input

```text
list
bye
```

### Expected output

```text
 _   _                  
| \ | | _____   ____ _ 
|  \| |/ _ \ \ / / _` |
| |\  | (_) \ V / (_| |
|_| \_|\___/ \_/ \__,_|

Hello! I'm Nova.
Let's take it one task at a time.
____________________________________________________________
 Oops! I skipped 6 corrupted task record(s) in the data file.
____________________________________________________________
 Here are the tasks in your list:
 1.[T][X] read | café
 2.[D][ ] return book (by: Jun 6 2019)
 3.[E][ ] project meeting (from: Aug 6th 2pm to: 4pm)
 4.[T][ ] legacy task
____________________________________________________________
____________________________________________________________
 See you soon. Take care!
____________________________________________________________
```

## TC6: Find deadlines occurring on a date

Aim: Verify the on command lists only deadlines due on a valid date, reports invalid dates, and leaves the task list unchanged.

### Input

```text
deadline submit report /by 2019-10-15
todo buy stationery
deadline return book /by 2019-10-15
deadline renew membership /by 2019-10-16
on 2019-10-15
on 2019-10-17
on 2019-02-30
on
list
bye
```

### Expected output

```text
 _   _                  
| \ | | _____   ____ _ 
|  \| |/ _ \ \ / / _` |
| |\  | (_) \ V / (_| |
|_| \_|\___/ \_/ \__,_|

Hello! I'm Nova.
Let's take it one task at a time.
____________________________________________________________
____________________________________________________________
 Got it! Added to your list:
  [D][ ] submit report (by: Oct 15 2019)
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it! Added to your list:
  [T][ ] buy stationery
 Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it! Added to your list:
  [D][ ] return book (by: Oct 15 2019)
 Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it! Added to your list:
  [D][ ] renew membership (by: Oct 16 2019)
 Now you have 4 tasks in the list.
____________________________________________________________
____________________________________________________________
 Here are the deadlines on 2019-10-15:
 1.[D][ ] submit report (by: Oct 15 2019)
 3.[D][ ] return book (by: Oct 15 2019)
____________________________________________________________
____________________________________________________________
 Here are the deadlines on 2019-10-17:
____________________________________________________________
____________________________________________________________
 Oops! The date must be a valid date in yyyy-MM-dd format.
____________________________________________________________
____________________________________________________________
 Oops! The date must be a valid date in yyyy-MM-dd format.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[D][ ] submit report (by: Oct 15 2019)
 2.[T][ ] buy stationery
 3.[D][ ] return book (by: Oct 15 2019)
 4.[D][ ] renew membership (by: Oct 16 2019)
____________________________________________________________
____________________________________________________________
 See you soon. Take care!
____________________________________________________________
```

## TC7: Find tasks by description keyword

Aim: Verify the find command lists matching task descriptions with their original task numbers, ignores task
metadata, rejects an empty keyword, and leaves the task list unchanged after searches and errors.

### Input

```text
todo read book
todo write report
deadline return book /by 2019-06-06
event book launch /from 2pm /to 4pm
find book
find 2019
find
list
bye
```

### Expected output

```text
 _   _                  
| \ | | _____   ____ _ 
|  \| |/ _ \ \ / / _` |
| |\  | (_) \ V / (_| |
|_| \_|\___/ \_/ \__,_|

Hello! I'm Nova.
Let's take it one task at a time.
____________________________________________________________
____________________________________________________________
 Got it! Added to your list:
  [T][ ] read book
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it! Added to your list:
  [T][ ] write report
 Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it! Added to your list:
  [D][ ] return book (by: Jun 6 2019)
 Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it! Added to your list:
  [E][ ] book launch (from: 2pm to: 4pm)
 Now you have 4 tasks in the list.
____________________________________________________________
____________________________________________________________
 Here are the matching tasks in your list:
 1.[T][ ] read book
 3.[D][ ] return book (by: Jun 6 2019)
 4.[E][ ] book launch (from: 2pm to: 4pm)
____________________________________________________________
____________________________________________________________
 Here are the matching tasks in your list:
____________________________________________________________
____________________________________________________________
 Oops! The keyword for a find command cannot be empty.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] read book
 2.[T][ ] write report
 3.[D][ ] return book (by: Jun 6 2019)
 4.[E][ ] book launch (from: 2pm to: 4pm)
____________________________________________________________
____________________________________________________________
 See you soon. Take care!
____________________________________________________________
```

## TC8: Display command help and reject trailing arguments

Aim: Verify help displays every supported command, rejects trailing arguments, and leaves Nova usable without
changing the task list.

### Input

```text
todo read book
help
help extra
list
bye
```

### Expected output

```text
 _   _                  
| \ | | _____   ____ _ 
|  \| |/ _ \ \ / / _` |
| |\  | (_) \ V / (_| |
|_| \_|\___/ \_/ \__,_|

Hello! I'm Nova.
Let's take it one task at a time.
____________________________________________________________
____________________________________________________________
 Got it! Added to your list:
  [T][ ] read book
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Here are Nova's commands:
 list
 todo DESCRIPTION
 deadline DESCRIPTION /by yyyy-MM-dd
 event DESCRIPTION /from START /to END
 mark TASK_NUMBER
 unmark TASK_NUMBER
 delete TASK_NUMBER
 find KEYWORD
 on yyyy-MM-dd
 help
 bye
____________________________________________________________
____________________________________________________________
 Oops! I'm sorry, but I don't know what that means :-(
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] read book
____________________________________________________________
____________________________________________________________
 See you soon. Take care!
____________________________________________________________
```
