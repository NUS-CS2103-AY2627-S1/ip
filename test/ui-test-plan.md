# Console UI test plan

This file is the source of truth for Tung Tung's console UI tests. Each case runs in a fresh temporary directory; include `bye` in every input block so the chatbot exits cleanly.

## Program setup

- Java version: 25
- Source directory: `src/main/java`
- Main class: `tungtung.TungTung`

Cases start without a saved file unless a `Saved tasks` block supplies its contents.
The runner never reads or modifies the project's real task data.

## Test case: load-saved-tasks

### Aim

Verify that a new chatbot session loads the tasks saved in `data/tungtung.txt`.

### Saved tasks

```text
T | 0 | borrow book
E | 0 | project meeting | 2019-10-15 | 2019-10-16
```

### Input

```text
list
bye
```

### Expected output

```text
_____________________________________________________________
  _____          _    _ ______ _____  ______ 
 / ____|   /\   | |  | |  ____|  __ \|  ____|
| (___    /  \  | |__| | |__  | |__) | |__   
 \___ \  / /\ \ |  __  |  __| |  _  /|  __|  
 ____) |/ ____ \| |  | | |____| | \ \| |____ 
|_____//_/    \_\_|  |_|______|_|  \_\______|

Hello! Tung Tung Sahere!
How can I assist?
_____________________________________________________________
_____________________________________________________________
Here are the tasks in your list:
1.[T][ ] borrow book
2.[E][ ] project meeting (from: Oct 15 2019 - to: Oct 16 2019)
_____________________________________________________________
_____________________________________________________________
Bye! Tung Tung Sagone!
_____________________________________________________________
```

## Test case: find-task-by-keyword

### Aim

Verify that `find` displays matching tasks in their original order and preserves their full-list task numbers.

### Input

```text
todo read book
deadline return book /by 2019-10-15
find book
bye
```

### Expected output

```text
_____________________________________________________________
  _____          _    _ ______ _____  ______ 
 / ____|   /\   | |  | |  ____|  __ \|  ____|
| (___    /  \  | |__| | |__  | |__) | |__   
 \___ \  / /\ \ |  __  |  __| |  _  /|  __|  
 ____) |/ ____ \| |  | | |____| | \ \| |____ 
|_____//_/    \_\_|  |_|______|_|  \_\______|

Hello! Tung Tung Sahere!
How can I assist?
_____________________________________________________________
_____________________________________________________________
Got it. I've added this task:
  [T][ ] read book
Now you have 1 tasks in the list.
_____________________________________________________________
_____________________________________________________________
Got it. I've added this task:
  [D][ ] return book (by: Oct 15 2019)
Now you have 2 tasks in the list.
_____________________________________________________________
_____________________________________________________________
Here are the matching tasks in your list:
1.[T][ ] read book
2.[D][ ] return book (by: Oct 15 2019)
_____________________________________________________________
_____________________________________________________________
Bye! Tung Tung Sagone!
_____________________________________________________________
```

## Test case: sort-tasks-by-deadline

### Aim

Verify that `sort by deadline` orders deadlines and events chronologically,
places todos at the end, and preserves the sorted order for `list`.

### Input

```text
todo buy groceries
deadline submit report /by 2026-09-12
event project meeting /from 2026-09-10 /to 2026-09-11
deadline return book /by 2026-09-10
sort by deadline
list
bye
```

### Expected output

```text
_____________________________________________________________
  _____          _    _ ______ _____  ______ 
 / ____|   /\   | |  | |  ____|  __ \|  ____|
| (___    /  \  | |__| | |__  | |__) | |__   
 \___ \  / /\ \ |  __  |  __| |  _  /|  __|  
 ____) |/ ____ \| |  | | |____| | \ \| |____ 
|_____//_/    \_\_|  |_|______|_|  \_\______|

Hello! Tung Tung Sahere!
How can I assist?
_____________________________________________________________
_____________________________________________________________
Got it. I've added this task:
  [T][ ] buy groceries
Now you have 1 tasks in the list.
_____________________________________________________________
_____________________________________________________________
Got it. I've added this task:
  [D][ ] submit report (by: Sep 12 2026)
Now you have 2 tasks in the list.
_____________________________________________________________
_____________________________________________________________
Got it. I've added this task:
  [E][ ] project meeting (from: Sep 10 2026 - to: Sep 11 2026)
Now you have 3 tasks in the list.
_____________________________________________________________
_____________________________________________________________
Got it. I've added this task:
  [D][ ] return book (by: Sep 10 2026)
Now you have 4 tasks in the list.
_____________________________________________________________
_____________________________________________________________
Here are your tasks sorted by deadline:
1.[E][ ] project meeting (from: Sep 10 2026 - to: Sep 11 2026)
2.[D][ ] return book (by: Sep 10 2026)
3.[D][ ] submit report (by: Sep 12 2026)
4.[T][ ] buy groceries
_____________________________________________________________
_____________________________________________________________
Here are the tasks in your list:
1.[E][ ] project meeting (from: Sep 10 2026 - to: Sep 11 2026)
2.[D][ ] return book (by: Sep 10 2026)
3.[D][ ] submit report (by: Sep 12 2026)
4.[T][ ] buy groceries
_____________________________________________________________
_____________________________________________________________
Bye! Tung Tung Sagone!
_____________________________________________________________
```

## Test case: add-and-list-todo

### Aim

Verify that a `todo` command creates an incomplete todo task and that `list` displays it with task number 1.

### Input

```text
todo borrow book
list
bye
```

### Expected output

```text
_____________________________________________________________
  _____          _    _ ______ _____  ______ 
 / ____|   /\   | |  | |  ____|  __ \|  ____|
| (___    /  \  | |__| | |__  | |__) | |__   
 \___ \  / /\ \ |  __  |  __| |  _  /|  __|  
 ____) |/ ____ \| |  | | |____| | \ \| |____ 
|_____//_/    \_\_|  |_|______|_|  \_\______|

Hello! Tung Tung Sahere!
How can I assist?
_____________________________________________________________
_____________________________________________________________
Got it. I've added this task:
  [T][ ] borrow book
Now you have 1 tasks in the list.
_____________________________________________________________
_____________________________________________________________
Here are the tasks in your list:
1.[T][ ] borrow book
_____________________________________________________________
_____________________________________________________________
Bye! Tung Tung Sagone!
_____________________________________________________________
```

## Test case: reject-invalid-task-commands

### Aim

Verify that commands with missing, non-numeric, or incomplete details show an error and the chatbot continues accepting commands.

### Input

```text
mark
todo read book
delete two
deadline return book
event project /from Monday
deadline return book /by tomorrow
event backwards /from 2019-10-16 /to 2019-10-15
todo buy | sell
bye
```

### Expected output

```text
_____________________________________________________________
  _____          _    _ ______ _____  ______ 
 / ____|   /\   | |  | |  ____|  __ \|  ____|
| (___    /  \  | |__| | |__  | |__) | |__   
 \___ \  / /\ \ |  __  |  __| |  _  /|  __|  
 ____) |/ ____ \| |  | | |____| | \ \| |____ 
|_____//_/    \_\_|  |_|______|_|  \_\______|

Hello! Tung Tung Sahere!
How can I assist?
_____________________________________________________________
_____________________________________________________________
OOPS!!! Please provide a valid task number.
_____________________________________________________________
_____________________________________________________________
Got it. I've added this task:
  [T][ ] read book
Now you have 1 tasks in the list.
_____________________________________________________________
_____________________________________________________________
OOPS!!! Please provide a valid task number.
_____________________________________________________________
_____________________________________________________________
OOPS!!! Use: deadline DESCRIPTION /by yyyy-MM-dd.
_____________________________________________________________
_____________________________________________________________
OOPS!!! Use: event DESCRIPTION /from yyyy-MM-dd /to yyyy-MM-dd.
_____________________________________________________________
_____________________________________________________________
OOPS!!! Dates must use yyyy-MM-dd, for example 2019-10-15.
_____________________________________________________________
_____________________________________________________________
OOPS!!! An event's end date cannot be before its start date.
_____________________________________________________________
_____________________________________________________________
OOPS!!! Task details cannot contain " | ".
_____________________________________________________________
_____________________________________________________________
Bye! Tung Tung Sagone!
_____________________________________________________________
```

## Test case: delete-task

### Aim

Verify that `delete` removes the requested task, reports it to the user, and renumbers the remaining tasks in `list`.

### Input

```text
todo read book
todo borrow book
delete 1
list
bye
```

### Expected output

```text
_____________________________________________________________
  _____          _    _ ______ _____  ______ 
 / ____|   /\   | |  | |  ____|  __ \|  ____|
| (___    /  \  | |__| | |__  | |__) | |__   
 \___ \  / /\ \ |  __  |  __| |  _  /|  __|  
 ____) |/ ____ \| |  | | |____| | \ \| |____ 
|_____//_/    \_\_|  |_|______|_|  \_\______|

Hello! Tung Tung Sahere!
How can I assist?
_____________________________________________________________
_____________________________________________________________
Got it. I've added this task:
  [T][ ] read book
Now you have 1 tasks in the list.
_____________________________________________________________
_____________________________________________________________
Got it. I've added this task:
  [T][ ] borrow book
Now you have 2 tasks in the list.
_____________________________________________________________
_____________________________________________________________
Noted. I've removed this task:
  [T][ ] read book
Now you have 1 tasks in the list.
_____________________________________________________________
_____________________________________________________________
Here are the tasks in your list:
1.[T][ ] borrow book
_____________________________________________________________
_____________________________________________________________
Bye! Tung Tung Sagone!
_____________________________________________________________
```

## Test case: reject-empty-todo

### Aim

Verify that `todo` without a description reports the required error and continues accepting commands.

### Input

```text
todo
bye
```

### Expected output

```text
_____________________________________________________________
  _____          _    _ ______ _____  ______ 
 / ____|   /\   | |  | |  ____|  __ \|  ____|
| (___    /  \  | |__| | |__  | |__) | |__   
 \___ \  / /\ \ |  __  |  __| |  _  /|  __|  
 ____) |/ ____ \| |  | | |____| | \ \| |____ 
|_____//_/    \_\_|  |_|______|_|  \_\______|

Hello! Tung Tung Sahere!
How can I assist?
_____________________________________________________________
_____________________________________________________________
OOPS!!! There is nothing TODO.
_____________________________________________________________
_____________________________________________________________
Bye! Tung Tung Sagone!
_____________________________________________________________
```

## Test case: reject-unknown-command

### Aim

Verify that an unrecognised command reports the required error and does not add a task.

### Input

```text
blah
bye
```

### Expected output

```text
_____________________________________________________________
  _____          _    _ ______ _____  ______ 
 / ____|   /\   | |  | |  ____|  __ \|  ____|
| (___    /  \  | |__| | |__  | |__) | |__   
 \___ \  / /\ \ |  __  |  __| |  _  /|  __|  
 ____) |/ ____ \| |  | | |____| | \ \| |____ 
|_____//_/    \_\_|  |_|______|_|  \_\______|

Hello! Tung Tung Sahere!
How can I assist?
_____________________________________________________________
_____________________________________________________________
OOPS!!! IDK what u are on about :-()
_____________________________________________________________
_____________________________________________________________
Bye! Tung Tung Sagone!
_____________________________________________________________
```

## Test case: add-deadline

### Aim

Verify that a `deadline` command separates the description and `/by` value and displays the deadline task correctly.

### Input

```text
deadline return book /by 2019-10-15
bye
```

### Expected output

```text
_____________________________________________________________
  _____          _    _ ______ _____  ______ 
 / ____|   /\   | |  | |  ____|  __ \|  ____|
| (___    /  \  | |__| | |__  | |__) | |__   
 \___ \  / /\ \ |  __  |  __| |  _  /|  __|  
 ____) |/ ____ \| |  | | |____| | \ \| |____ 
|_____//_/    \_\_|  |_|______|_|  \_\______|

Hello! Tung Tung Sahere!
How can I assist?
_____________________________________________________________
_____________________________________________________________
Got it. I've added this task:
  [D][ ] return book (by: Oct 15 2019)
Now you have 1 tasks in the list.
_____________________________________________________________
_____________________________________________________________
Bye! Tung Tung Sagone!
_____________________________________________________________
```

## Test case: add-event

### Aim

Verify that an `event` command separates its description, `/from` value, and `/to` value.

### Input

```text
event project meeting /from 2019-10-15 /to 2019-10-16
bye
```

### Expected output

```text
_____________________________________________________________
  _____          _    _ ______ _____  ______ 
 / ____|   /\   | |  | |  ____|  __ \|  ____|
| (___    /  \  | |__| | |__  | |__) | |__   
 \___ \  / /\ \ |  __  |  __| |  _  /|  __|  
 ____) |/ ____ \| |  | | |____| | \ \| |____ 
|_____//_/    \_\_|  |_|______|_|  \_\______|

Hello! Tung Tung Sahere!
How can I assist?
_____________________________________________________________
_____________________________________________________________
Got it. I've added this task:
  [E][ ] project meeting (from: Oct 15 2019 - to: Oct 16 2019)
Now you have 1 tasks in the list.
_____________________________________________________________
_____________________________________________________________
Bye! Tung Tung Sagone!
_____________________________________________________________
```

## Test case: mark-and-unmark-task

### Aim

Verify that `mark` and `unmark` update a task's completion status while continuing to accept commands.

### Input

```text
todo borrow book
mark 1
unmark 1
bye
```

### Expected output

```text
_____________________________________________________________
  _____          _    _ ______ _____  ______ 
 / ____|   /\   | |  | |  ____|  __ \|  ____|
| (___    /  \  | |__| | |__  | |__) | |__   
 \___ \  / /\ \ |  __  |  __| |  _  /|  __|  
 ____) |/ ____ \| |  | | |____| | \ \| |____ 
|_____//_/    \_\_|  |_|______|_|  \_\______|

Hello! Tung Tung Sahere!
How can I assist?
_____________________________________________________________
_____________________________________________________________
Got it. I've added this task:
  [T][ ] borrow book
Now you have 1 tasks in the list.
_____________________________________________________________
_____________________________________________________________
Nice! I've marked this task as done:
  [T][X] borrow book
_____________________________________________________________
_____________________________________________________________
OK, I've marked this task as not done yet:
  [T][ ] borrow book
_____________________________________________________________
_____________________________________________________________
Bye! Tung Tung Sagone!
_____________________________________________________________
```

## Test case: find-and-modify-by-displayed-number

### Aim

Verify that a search match keeps its full-list number and mark/delete affect that task.

### Saved tasks

```text
T | 0 | groceries
T | 0 | book
```

### Input

```text
find book
mark 2
delete 2
list
bye
```

### Expected output

```text
_____________________________________________________________
  _____          _    _ ______ _____  ______ 
 / ____|   /\   | |  | |  ____|  __ \|  ____|
| (___    /  \  | |__| | |__  | |__) | |__   
 \___ \  / /\ \ |  __  |  __| |  _  /|  __|  
 ____) |/ ____ \| |  | | |____| | \ \| |____ 
|_____//_/    \_\_|  |_|______|_|  \_\______|

Hello! Tung Tung Sahere!
How can I assist?
_____________________________________________________________
_____________________________________________________________
Here are the matching tasks in your list:
2.[T][ ] book
_____________________________________________________________
_____________________________________________________________
Nice! I've marked this task as done:
  [T][X] book
_____________________________________________________________
_____________________________________________________________
Noted. I've removed this task:
  [T][X] book
Now you have 1 tasks in the list.
_____________________________________________________________
_____________________________________________________________
Here are the tasks in your list:
1.[T][ ] groceries
_____________________________________________________________
_____________________________________________________________
Bye! Tung Tung Sagone!
_____________________________________________________________
```

## Test case: preserve-corrupt-save

### Aim

Verify that an invalid saved line stops startup before any replacement task can be added.

### Saved tasks

```text
T | 0 | precious task
broken line
```

### Input

```text
todo replacement
bye
```

### Expected output

```text
_____________________________________________________________
  _____          _    _ ______ _____  ______ 
 / ____|   /\   | |  | |  ____|  __ \|  ____|
| (___    /  \  | |__| | |__  | |__) | |__   
 \___ \  / /\ \ |  __  |  __| |  _  /|  __|  
 ____) |/ ____ \| |  | | |____| | \ \| |____ 
|_____//_/    \_\_|  |_|______|_|  \_\______|

Hello! Tung Tung Sahere!
How can I assist?
_____________________________________________________________
_____________________________________________________________
OOPS!!! I could not load your saved tasks. Exiting without changing the saved file.
_____________________________________________________________
```

## Test case: reject-storage-boundary-characters

### Aim

Reject trailing storage delimiters before saving, then continue using the valid task list.

### Input

```text
todo original
deadline report | /by 2026-09-18
event meeting | /from 2026-09-18 /to 2026-09-19
list
bye
```

### Expected output

```text
_____________________________________________________________
  _____          _    _ ______ _____  ______ 
 / ____|   /\   | |  | |  ____|  __ \|  ____|
| (___    /  \  | |__| | |__  | |__) | |__   
 \___ \  / /\ \ |  __  |  __| |  _  /|  __|  
 ____) |/ ____ \| |  | | |____| | \ \| |____ 
|_____//_/    \_\_|  |_|______|_|  \_\______|

Hello! Tung Tung Sahere!
How can I assist?
_____________________________________________________________
_____________________________________________________________
Got it. I've added this task:
  [T][ ] original
Now you have 1 tasks in the list.
_____________________________________________________________
_____________________________________________________________
OOPS!!! Task details cannot contain " | ".
_____________________________________________________________
_____________________________________________________________
OOPS!!! Task details cannot contain " | ".
_____________________________________________________________
_____________________________________________________________
Here are the tasks in your list:
1.[T][ ] original
_____________________________________________________________
_____________________________________________________________
Bye! Tung Tung Sagone!
_____________________________________________________________
```

## Test case: reject-wrong-event-labels

### Aim

Reject reversed and repeated event labels rather than silently changing their meaning.

### Input

```text
event meeting /to 2026-09-17 /from 2026-09-18
event meeting /from 2026-09-17 /from 2026-09-18
event meeting /to 2026-09-17 /to 2026-09-18
event meeting /from 2026-09-17 /to 2026-09-18 /to 2026-09-19
event meeting /from /to 2026-09-18
list
bye
```

### Expected output

```text
_____________________________________________________________
  _____          _    _ ______ _____  ______ 
 / ____|   /\   | |  | |  ____|  __ \|  ____|
| (___    /  \  | |__| | |__  | |__) | |__   
 \___ \  / /\ \ |  __  |  __| |  _  /|  __|  
 ____) |/ ____ \| |  | | |____| | \ \| |____ 
|_____//_/    \_\_|  |_|______|_|  \_\______|

Hello! Tung Tung Sahere!
How can I assist?
_____________________________________________________________
_____________________________________________________________
OOPS!!! Use: event DESCRIPTION /from yyyy-MM-dd /to yyyy-MM-dd.
_____________________________________________________________
_____________________________________________________________
OOPS!!! Use: event DESCRIPTION /from yyyy-MM-dd /to yyyy-MM-dd.
_____________________________________________________________
_____________________________________________________________
OOPS!!! Use: event DESCRIPTION /from yyyy-MM-dd /to yyyy-MM-dd.
_____________________________________________________________
_____________________________________________________________
OOPS!!! Use: event DESCRIPTION /from yyyy-MM-dd /to yyyy-MM-dd.
_____________________________________________________________
_____________________________________________________________
OOPS!!! Use: event DESCRIPTION /from yyyy-MM-dd /to yyyy-MM-dd.
_____________________________________________________________
_____________________________________________________________
Here are the tasks in your list:
_____________________________________________________________
_____________________________________________________________
Bye! Tung Tung Sagone!
_____________________________________________________________
```

## Test case: reject-out-of-range-task-numbers

### Aim

Reject zero, negative, overflowing, and extra task numbers without changing the saved task.

### Input

```text
todo original
mark 0
delete -2147483648
mark 9999999999999999999999
delete 2
unmark 1 2
list
bye
```

### Expected output

```text
_____________________________________________________________
  _____          _    _ ______ _____  ______ 
 / ____|   /\   | |  | |  ____|  __ \|  ____|
| (___    /  \  | |__| | |__  | |__) | |__   
 \___ \  / /\ \ |  __  |  __| |  _  /|  __|  
 ____) |/ ____ \| |  | | |____| | \ \| |____ 
|_____//_/    \_\_|  |_|______|_|  \_\______|

Hello! Tung Tung Sahere!
How can I assist?
_____________________________________________________________
_____________________________________________________________
Got it. I've added this task:
  [T][ ] original
Now you have 1 tasks in the list.
_____________________________________________________________
_____________________________________________________________
OOPS!!! Please provide a valid task number.
_____________________________________________________________
_____________________________________________________________
OOPS!!! Please provide a valid task number.
_____________________________________________________________
_____________________________________________________________
OOPS!!! Please provide a valid task number.
_____________________________________________________________
_____________________________________________________________
OOPS!!! Please provide a valid task number.
_____________________________________________________________
_____________________________________________________________
OOPS!!! Please provide a valid task number.
_____________________________________________________________
_____________________________________________________________
Here are the tasks in your list:
1.[T][ ] original
_____________________________________________________________
_____________________________________________________________
Bye! Tung Tung Sagone!
_____________________________________________________________
```
