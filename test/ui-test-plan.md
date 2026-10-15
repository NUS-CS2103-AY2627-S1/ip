# Snoopy UI Test Plan

The tests compare the complete console output exactly, including whitespace and dividers. Each test starts a fresh Snoopy process.

The graphical interface also requires these personality checks because visual presentation is not represented in console transcripts:

- The in-app header, window title, and greeting identify the chatbot as Snoopy.
- Black, white, and Snoopy-red colors give the interface a classic comic-inspired theme.
- Snoopy replies show the circular-cropped black-and-white beagle avatar, while user messages remain text-only.
- Every message bubble has evenly rounded corners, and the action button is labeled "Send".
- Invalid commands use Snoopy's red error dialog style, while valid replies retain the neutral reply style.
- Resizing the window keeps the conversation pane and input controls usable.

## UI-01: Add and list all task types

**Aim:** Verify that todos, deadlines, and events are created and displayed using their type-specific formats.

### Input

```text
todo borrow book
deadline return book /by 2026-08-30
event project meeting /from 2026-09-01 /to 2026-09-02
list
bye
```

### Expected output

```text
____________________________________________________________
  ____
 / ___| _ __   ___   ___  _ __  _   _
 \___ \| '_ \ / _ \ / _ \| '_ \| | | |
  ___) | | | | (_) | (_) | |_) | |_| |
 |____/|_| |_|\___/ \___/| .__/ \__, |
                            |_|    |___/
Hi! I'm Snoopy, your happy little helper.
What can I do for you?
____________________________________________________________
 Got it. I've added this task:
   [T][ ] borrow book
 Now you have 1 tasks in the list.
____________________________________________________________
 Got it. I've added this task:
   [D][ ] return book (by: Aug 30 2026)
 Now you have 2 tasks in the list.
____________________________________________________________
 Got it. I've added this task:
   [E][ ] project meeting (from: Sep 01 2026 to: Sep 02 2026)
 Now you have 3 tasks in the list.
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] borrow book
 2.[D][ ] return book (by: Aug 30 2026)
 3.[E][ ] project meeting (from: Sep 01 2026 to: Sep 02 2026)
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

## UI-17: Update a task description without replacing the task

**Aim:** Verify that updating a description preserves the task's type, date, and completion state, while an invalid
update leaves the edited task unchanged.

### Input

```text
deadline return book /by 2026-08-30
mark 1
update 1 return all library books
update
update 1
update one unchanged
update 2 unchanged
list
bye
```

### Expected output

```text
____________________________________________________________
  ____
 / ___| _ __   ___   ___  _ __  _   _
 \___ \| '_ \ / _ \ / _ \| '_ \| | | |
  ___) | | | | (_) | (_) | |_) | |_| |
 |____/|_| |_|\___/ \___/| .__/ \__, |
                            |_|    |___/
Hi! I'm Snoopy, your happy little helper.
What can I do for you?
____________________________________________________________
 Got it. I've added this task:
   [D][ ] return book (by: Aug 30 2026)
 Now you have 1 tasks in the list.
____________________________________________________________
 Nice! I've marked this task as done:
   [D][X] return book (by: Aug 30 2026)
____________________________________________________________
 Got it. I've updated this task:
   [D][X] return all library books (by: Aug 30 2026)
____________________________________________________________
 OOPS! Please use: update <task number> <new description>.
____________________________________________________________
 OOPS! Please use: update <task number> <new description>.
____________________________________________________________
 OOPS! 'one' is not a valid task number.
____________________________________________________________
 OOPS! Task 2 does not exist. Choose a number from 1 to 1.
____________________________________________________________
 Here are the tasks in your list:
 1.[D][X] return all library books (by: Aug 30 2026)
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

## UI-16: Reject an event ending before it starts

**Aim:** Verify that a reversed event date range is rejected without changing the existing task list.

### Input

```text
todo anchor
event holiday /from 2026-09-10 /to 2026-09-09
list
bye
```

### Expected output

```text
____________________________________________________________
  ____
 / ___| _ __   ___   ___  _ __  _   _
 \___ \| '_ \ / _ \ / _ \| '_ \| | | |
  ___) | | | | (_) | (_) | |_) | |_| |
 |____/|_| |_|\___/ \___/| .__/ \__, |
                            |_|    |___/
Hi! I'm Snoopy, your happy little helper.
What can I do for you?
____________________________________________________________
 Got it. I've added this task:
   [T][ ] anchor
 Now you have 1 tasks in the list.
____________________________________________________________
 OOPS! The event end date cannot be before its start date.
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] anchor
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

## UI-12: Save all mutations and load the final state after restart

**Aim:** Verify that additions, mark, unmark, and deletion are persisted with the correct task types, order, details, and completion states.

### Input

```text
todo remove me
deadline return book /by 2026-08-29
event project meeting /from 2026-09-01 /to 2026-09-02
mark 2
mark 3
unmark 3
delete 1
bye
```

### Expected output

```text
____________________________________________________________
  ____
 / ___| _ __   ___   ___  _ __  _   _
 \___ \| '_ \ / _ \ / _ \| '_ \| | | |
  ___) | | | | (_) | (_) | |_) | |_| |
 |____/|_| |_|\___/ \___/| .__/ \__, |
                            |_|    |___/
Hi! I'm Snoopy, your happy little helper.
What can I do for you?
____________________________________________________________
 Got it. I've added this task:
   [T][ ] remove me
 Now you have 1 tasks in the list.
____________________________________________________________
 Got it. I've added this task:
   [D][ ] return book (by: Aug 29 2026)
 Now you have 2 tasks in the list.
____________________________________________________________
 Got it. I've added this task:
   [E][ ] project meeting (from: Sep 01 2026 to: Sep 02 2026)
 Now you have 3 tasks in the list.
____________________________________________________________
 Nice! I've marked this task as done:
   [D][X] return book (by: Aug 29 2026)
____________________________________________________________
 Nice! I've marked this task as done:
   [E][X] project meeting (from: Sep 01 2026 to: Sep 02 2026)
____________________________________________________________
 OK, I've marked this task as not done yet:
   [E][ ] project meeting (from: Sep 01 2026 to: Sep 02 2026)
____________________________________________________________
 Noted. I've removed this task:
   [T][ ] remove me
 Now you have 2 tasks in the list.
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

### Restart input

```text
list
bye
```

### Expected restart output

```text
____________________________________________________________
  ____
 / ___| _ __   ___   ___  _ __  _   _
 \___ \| '_ \ / _ \ / _ \| '_ \| | | |
  ___) | | | | (_) | (_) | |_) | |_| |
 |____/|_| |_|\___/ \___/| .__/ \__, |
                            |_|    |___/
Hi! I'm Snoopy, your happy little helper.
What can I do for you?
____________________________________________________________
 Here are the tasks in your list:
 1.[D][X] return book (by: Aug 29 2026)
 2.[E][ ] project meeting (from: Sep 01 2026 to: Sep 02 2026)
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

## UI-13: Handle a corrupted data file without crashing

**Aim:** Verify that malformed saved data produces a clear error, starts with a safe empty list, and still accepts subsequent commands.

### Initial data file

```text
D | 0 | impossible saved deadline | 2026-02-30
```

### Input

```text
list
todo replacement
list
bye
```

### Expected output

```text
____________________________________________________________
  ____
 / ___| _ __   ___   ___  _ __  _   _
 \___ \| '_ \ / _ \ / _ \| '_ \| | | |
  ___) | | | | (_) | (_) | |_) | |_| |
 |____/|_| |_|\___/ \___/| .__/ \__, |
                            |_|    |___/
Hi! I'm Snoopy, your happy little helper.
What can I do for you?
____________________________________________________________
 OOPS! The data file is corrupted at line 1.
____________________________________________________________
 Here are the tasks in your list:
____________________________________________________________
 Got it. I've added this task:
   [T][ ] replacement
 Now you have 1 tasks in the list.
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] replacement
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

## UI-14: Parse valid dates and reject invalid dates without changing state

**Aim:** Verify that ISO dates become formatted date objects while malformed and impossible dates are rejected without adding tasks.

### Input

```text
todo anchor
deadline bad format /by Friday
deadline impossible /by 2026-02-30
event bad start /from Monday /to 2026-09-02
event bad end /from 2026-09-01 /to Tuesday
deadline valid deadline /by 2019-10-15
event valid event /from 2019-10-15 /to 2019-10-16
list
bye
```

### Expected output

```text
____________________________________________________________
  ____
 / ___| _ __   ___   ___  _ __  _   _
 \___ \| '_ \ / _ \ / _ \| '_ \| | | |
  ___) | | | | (_) | (_) | |_) | |_| |
 |____/|_| |_|\___/ \___/| .__/ \__, |
                            |_|    |___/
Hi! I'm Snoopy, your happy little helper.
What can I do for you?
____________________________________________________________
 Got it. I've added this task:
   [T][ ] anchor
 Now you have 1 tasks in the list.
____________________________________________________________
 OOPS! Please enter the deadline date as yyyy-MM-dd, for example 2019-10-15.
____________________________________________________________
 OOPS! Please enter the deadline date as yyyy-MM-dd, for example 2019-10-15.
____________________________________________________________
 OOPS! Please enter event dates as yyyy-MM-dd, for example 2019-10-15.
____________________________________________________________
 OOPS! Please enter event dates as yyyy-MM-dd, for example 2019-10-15.
____________________________________________________________
 Got it. I've added this task:
   [D][ ] valid deadline (by: Oct 15 2019)
 Now you have 2 tasks in the list.
____________________________________________________________
 Got it. I've added this task:
   [E][ ] valid event (from: Oct 15 2019 to: Oct 16 2019)
 Now you have 3 tasks in the list.
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] anchor
 2.[D][ ] valid deadline (by: Oct 15 2019)
 3.[E][ ] valid event (from: Oct 15 2019 to: Oct 16 2019)
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

## UI-02: Mark and unmark a task

**Aim:** Verify that a task's done status can be set and then reversed without changing its description or type.

### Input

```text
todo read book
mark 1
unmark 1
list
bye
```

### Expected output

```text
____________________________________________________________
  ____
 / ___| _ __   ___   ___  _ __  _   _
 \___ \| '_ \ / _ \ / _ \| '_ \| | | |
  ___) | | | | (_) | (_) | |_) | |_| |
 |____/|_| |_|\___/ \___/| .__/ \__, |
                            |_|    |___/
Hi! I'm Snoopy, your happy little helper.
What can I do for you?
____________________________________________________________
 Got it. I've added this task:
   [T][ ] read book
 Now you have 1 tasks in the list.
____________________________________________________________
 Nice! I've marked this task as done:
   [T][X] read book
____________________________________________________________
 OK, I've marked this task as not done yet:
   [T][ ] read book
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] read book
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

## UI-03: Reject an empty todo and an unknown command

**Aim:** Verify that the chatbot explains the two minimum required input errors and remains available for the next command.

### Input

```text
todo
blah
bye
```

### Expected output

```text
____________________________________________________________
  ____
 / ___| _ __   ___   ___  _ __  _   _
 \___ \| '_ \ / _ \ / _ \| '_ \| | | |
  ___) | | | | (_) | (_) | |_) | |_| |
 |____/|_| |_|\___/ \___/| .__/ \__, |
                            |_|    |___/
Hi! I'm Snoopy, your happy little helper.
What can I do for you?
____________________________________________________________
 OOPS! Please tell me what to add after 'todo'.
____________________________________________________________
 OOPS! Sorry, I don't recognize that command. Available commands: bye, list, mark, unmark, delete, find, update, todo, deadline, event.
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

## UI-04: Reject malformed task details and task numbers

**Aim:** Verify that malformed deadlines, events, and task numbers produce specific errors without terminating the chatbot.

### Input

```text
deadline return book
deadline /by Sunday
event meeting /from Monday
event meeting /from /to Tuesday
mark
mark abc
mark 1
todo read book
mark 2
bye
```

### Expected output

```text
____________________________________________________________
  ____
 / ___| _ __   ___   ___  _ __  _   _
 \___ \| '_ \ / _ \ / _ \| '_ \| | | |
  ___) | | | | (_) | (_) | |_) | |_| |
 |____/|_| |_|\___/ \___/| .__/ \__, |
                            |_|    |___/
Hi! I'm Snoopy, your happy little helper.
What can I do for you?
____________________________________________________________
 OOPS! Please use: deadline <description> /by <date or time>.
____________________________________________________________
 OOPS! A deadline needs both a description and a '/by' value.
____________________________________________________________
 OOPS! Please use: event <description> /from <start> /to <end>.
____________________________________________________________
 OOPS! An event needs a description, a '/from' value, and a '/to' value.
____________________________________________________________
 OOPS! Please provide a task number, for example 'mark 2'.
____________________________________________________________
 OOPS! 'abc' is not a valid task number.
____________________________________________________________
 OOPS! Your task list is empty, so there is no task to mark.
____________________________________________________________
 Got it. I've added this task:
   [T][ ] read book
 Now you have 1 tasks in the list.
____________________________________________________________
 OOPS! Task 2 does not exist. Choose a number from 1 to 1.
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

## UI-05: Rejected additions preserve task order and count

**Aim:** Verify that invalid todo, deadline, and event commands do not consume task slots or alter the order of valid tasks added before and after them.

### Input

```text
todo alpha
deadline beta /by 2026-08-29
todo
deadline gamma
event delta /from Monday
event meeting /to Tue /from Mon
event meeting /from 2026-09-01 /to 2026-09-02
list
deadline /by Sunday
todo epsilon
list
bye
```

### Expected output

```text
____________________________________________________________
  ____
 / ___| _ __   ___   ___  _ __  _   _
 \___ \| '_ \ / _ \ / _ \| '_ \| | | |
  ___) | | | | (_) | (_) | |_) | |_| |
 |____/|_| |_|\___/ \___/| .__/ \__, |
                            |_|    |___/
Hi! I'm Snoopy, your happy little helper.
What can I do for you?
____________________________________________________________
 Got it. I've added this task:
   [T][ ] alpha
 Now you have 1 tasks in the list.
____________________________________________________________
 Got it. I've added this task:
   [D][ ] beta (by: Aug 29 2026)
 Now you have 2 tasks in the list.
____________________________________________________________
 OOPS! Please tell me what to add after 'todo'.
____________________________________________________________
 OOPS! Please use: deadline <description> /by <date or time>.
____________________________________________________________
 OOPS! Please use: event <description> /from <start> /to <end>.
____________________________________________________________
 OOPS! Please use: event <description> /from <start> /to <end>.
____________________________________________________________
 Got it. I've added this task:
   [E][ ] meeting (from: Sep 01 2026 to: Sep 02 2026)
 Now you have 3 tasks in the list.
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] alpha
 2.[D][ ] beta (by: Aug 29 2026)
 3.[E][ ] meeting (from: Sep 01 2026 to: Sep 02 2026)
____________________________________________________________
 OOPS! A deadline needs both a description and a '/by' value.
____________________________________________________________
 Got it. I've added this task:
   [T][ ] epsilon
 Now you have 4 tasks in the list.
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] alpha
 2.[D][ ] beta (by: Aug 29 2026)
 3.[E][ ] meeting (from: Sep 01 2026 to: Sep 02 2026)
 4.[T][ ] epsilon
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

## UI-06: Rejected status changes preserve completion states

**Aim:** Verify that invalid mark and unmark commands do not change existing task statuses and that later valid status changes still target the correct tasks.

### Input

```text
todo first
todo second
mark 1
mark abc
mark 3
unmark 2
list
unmark -1
unmark 1
mark 2
list
bye
```

### Expected output

```text
____________________________________________________________
  ____
 / ___| _ __   ___   ___  _ __  _   _
 \___ \| '_ \ / _ \ / _ \| '_ \| | | |
  ___) | | | | (_) | (_) | |_) | |_| |
 |____/|_| |_|\___/ \___/| .__/ \__, |
                            |_|    |___/
Hi! I'm Snoopy, your happy little helper.
What can I do for you?
____________________________________________________________
 Got it. I've added this task:
   [T][ ] first
 Now you have 1 tasks in the list.
____________________________________________________________
 Got it. I've added this task:
   [T][ ] second
 Now you have 2 tasks in the list.
____________________________________________________________
 Nice! I've marked this task as done:
   [T][X] first
____________________________________________________________
 OOPS! 'abc' is not a valid task number.
____________________________________________________________
 OOPS! Task 3 does not exist. Choose a number from 1 to 2.
____________________________________________________________
 OK, I've marked this task as not done yet:
   [T][ ] second
____________________________________________________________
 Here are the tasks in your list:
 1.[T][X] first
 2.[T][ ] second
____________________________________________________________
 OOPS! Task -1 does not exist. Choose a number from 1 to 2.
____________________________________________________________
 OK, I've marked this task as not done yet:
   [T][ ] first
____________________________________________________________
 Nice! I've marked this task as done:
   [T][X] second
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] first
 2.[T][X] second
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

## UI-07: Whitespace and incomplete suffixes do not corrupt state

**Aim:** Verify that surrounding whitespace is handled consistently while incomplete command suffixes and case-sensitive unknown commands leave the valid task unchanged.

### Input

```text

   todo spaced task
deadline report /by
event review /from Mon /to
mark 1 extra
LIST
list extra
bye extra
list
bye
```

### Expected output

```text
____________________________________________________________
  ____
 / ___| _ __   ___   ___  _ __  _   _
 \___ \| '_ \ / _ \ / _ \| '_ \| | | |
  ___) | | | | (_) | (_) | |_) | |_| |
 |____/|_| |_|\___/ \___/| .__/ \__, |
                            |_|    |___/
Hi! I'm Snoopy, your happy little helper.
What can I do for you?
____________________________________________________________
 OOPS! Sorry, I don't recognize that command. Available commands: bye, list, mark, unmark, delete, find, update, todo, deadline, event.
____________________________________________________________
 Got it. I've added this task:
   [T][ ] spaced task
 Now you have 1 tasks in the list.
____________________________________________________________
 OOPS! Please use: deadline <description> /by <date or time>.
____________________________________________________________
 OOPS! Please use: event <description> /from <start> /to <end>.
____________________________________________________________
 OOPS! '1 extra' is not a valid task number.
____________________________________________________________
 OOPS! Sorry, I don't recognize that command. Available commands: bye, list, mark, unmark, delete, find, update, todo, deadline, event.
____________________________________________________________
 OOPS! Sorry, I don't recognize that command. Available commands: bye, list, mark, unmark, delete, find, update, todo, deadline, event.
____________________________________________________________
 OOPS! Sorry, I don't recognize that command. Available commands: bye, list, mark, unmark, delete, find, update, todo, deadline, event.
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] spaced task
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

## UI-15: Find tasks without changing the task list

**Aim:** Verify that find matches case-sensitive description substrings across task types, renumbers matches, ignores formatted dates, and rejects an empty keyword without changing task state.

### Input

```text
todo read book
deadline return book /by 2026-08-30
event book club /from 2026-09-01 /to 2026-09-02
todo call Alice
mark 2
find book
find Aug 30
find Book
find
list
bye
```

### Expected output

```text
____________________________________________________________
  ____
 / ___| _ __   ___   ___  _ __  _   _
 \___ \| '_ \ / _ \ / _ \| '_ \| | | |
  ___) | | | | (_) | (_) | |_) | |_| |
 |____/|_| |_|\___/ \___/| .__/ \__, |
                            |_|    |___/
Hi! I'm Snoopy, your happy little helper.
What can I do for you?
____________________________________________________________
 Got it. I've added this task:
   [T][ ] read book
 Now you have 1 tasks in the list.
____________________________________________________________
 Got it. I've added this task:
   [D][ ] return book (by: Aug 30 2026)
 Now you have 2 tasks in the list.
____________________________________________________________
 Got it. I've added this task:
   [E][ ] book club (from: Sep 01 2026 to: Sep 02 2026)
 Now you have 3 tasks in the list.
____________________________________________________________
 Got it. I've added this task:
   [T][ ] call Alice
 Now you have 4 tasks in the list.
____________________________________________________________
 Nice! I've marked this task as done:
   [D][X] return book (by: Aug 30 2026)
____________________________________________________________
 I sniffed out these matching tasks:
 1.[T][ ] read book
 2.[D][X] return book (by: Aug 30 2026)
 3.[E][ ] book club (from: Sep 01 2026 to: Sep 02 2026)
____________________________________________________________
 I sniffed out these matching tasks:
____________________________________________________________
 I sniffed out these matching tasks:
____________________________________________________________
 OOPS! I need a scent to follow. Please provide a keyword to find.
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] read book
 2.[D][X] return book (by: Aug 30 2026)
 3.[E][ ] book club (from: Sep 01 2026 to: Sep 02 2026)
 4.[T][ ] call Alice
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

## UI-08: Delete tasks while preserving shifted objects and statuses

**Aim:** Verify that deleting middle, first, and final tasks shifts indexes correctly while preserving the type and completion status of each remaining object.

### Input

```text
todo first
deadline second /by 2026-08-29
event third /from 2026-09-01 /to 2026-09-02
mark 3
delete 2
delete 3
list
unmark 2
delete 1
list
delete 1
list
bye
```

### Expected output

```text
____________________________________________________________
  ____
 / ___| _ __   ___   ___  _ __  _   _
 \___ \| '_ \ / _ \ / _ \| '_ \| | | |
  ___) | | | | (_) | (_) | |_) | |_| |
 |____/|_| |_|\___/ \___/| .__/ \__, |
                            |_|    |___/
Hi! I'm Snoopy, your happy little helper.
What can I do for you?
____________________________________________________________
 Got it. I've added this task:
   [T][ ] first
 Now you have 1 tasks in the list.
____________________________________________________________
 Got it. I've added this task:
   [D][ ] second (by: Aug 29 2026)
 Now you have 2 tasks in the list.
____________________________________________________________
 Got it. I've added this task:
   [E][ ] third (from: Sep 01 2026 to: Sep 02 2026)
 Now you have 3 tasks in the list.
____________________________________________________________
 Nice! I've marked this task as done:
   [E][X] third (from: Sep 01 2026 to: Sep 02 2026)
____________________________________________________________
 Noted. I've removed this task:
   [D][ ] second (by: Aug 29 2026)
 Now you have 2 tasks in the list.
____________________________________________________________
 OOPS! Task 3 does not exist. Choose a number from 1 to 2.
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] first
 2.[E][X] third (from: Sep 01 2026 to: Sep 02 2026)
____________________________________________________________
 OK, I've marked this task as not done yet:
   [E][ ] third (from: Sep 01 2026 to: Sep 02 2026)
____________________________________________________________
 Noted. I've removed this task:
   [T][ ] first
 Now you have 1 tasks in the list.
____________________________________________________________
 Here are the tasks in your list:
 1.[E][ ] third (from: Sep 01 2026 to: Sep 02 2026)
____________________________________________________________
 Noted. I've removed this task:
   [E][ ] third (from: Sep 01 2026 to: Sep 02 2026)
 Now you have 0 tasks in the list.
____________________________________________________________
 Here are the tasks in your list:
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

## UI-09: Operations remain safe after deletion empties and repopulates the list

**Aim:** Verify that mark, unmark, and delete fail safely after the final task is deleted and that a subsequently added task starts a clean list at index one.

### Input

```text
todo sole
mark 1
delete 1
mark 1
unmark 1
delete 1
todo replacement
list
bye
```

### Expected output

```text
____________________________________________________________
  ____
 / ___| _ __   ___   ___  _ __  _   _
 \___ \| '_ \ / _ \ / _ \| '_ \| | | |
  ___) | | | | (_) | (_) | |_) | |_| |
 |____/|_| |_|\___/ \___/| .__/ \__, |
                            |_|    |___/
Hi! I'm Snoopy, your happy little helper.
What can I do for you?
____________________________________________________________
 Got it. I've added this task:
   [T][ ] sole
 Now you have 1 tasks in the list.
____________________________________________________________
 Nice! I've marked this task as done:
   [T][X] sole
____________________________________________________________
 Noted. I've removed this task:
   [T][X] sole
 Now you have 0 tasks in the list.
____________________________________________________________
 OOPS! Your task list is empty, so there is no task to mark.
____________________________________________________________
 OOPS! Your task list is empty, so there is no task to unmark.
____________________________________________________________
 OOPS! Your task list is empty, so there is no task to delete.
____________________________________________________________
 Got it. I've added this task:
   [T][ ] replacement
 Now you have 1 tasks in the list.
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] replacement
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

## UI-10: Bye terminates processing immediately

**Aim:** Verify that commands already buffered after bye are ignored and cannot mutate or display application state.

### Input

```text
bye
todo must not appear
list
```

### Expected output

```text
____________________________________________________________
  ____
 / ___| _ __   ___   ___  _ __  _   _
 \___ \| '_ \ / _ \ / _ \| '_ \| | | |
  ___) | | | | (_) | (_) | |_) | |_| |
 |____/|_| |_|\___/ \___/| .__/ \__, |
                            |_|    |___/
Hi! I'm Snoopy, your happy little helper.
What can I do for you?
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

## UI-11: Invalid delete commands preserve the list

**Aim:** Verify that missing, non-numeric, zero, and out-of-range delete indexes leave task order and completion states unchanged before a valid deletion.

### Input

```text
delete 1
todo keep
todo remove
mark 1
delete
delete abc
delete 0
delete 3
list
delete 2
list
bye
```

### Expected output

```text
____________________________________________________________
  ____
 / ___| _ __   ___   ___  _ __  _   _
 \___ \| '_ \ / _ \ / _ \| '_ \| | | |
  ___) | | | | (_) | (_) | |_) | |_| |
 |____/|_| |_|\___/ \___/| .__/ \__, |
                            |_|    |___/
Hi! I'm Snoopy, your happy little helper.
What can I do for you?
____________________________________________________________
 OOPS! Your task list is empty, so there is no task to delete.
____________________________________________________________
 Got it. I've added this task:
   [T][ ] keep
 Now you have 1 tasks in the list.
____________________________________________________________
 Got it. I've added this task:
   [T][ ] remove
 Now you have 2 tasks in the list.
____________________________________________________________
 Nice! I've marked this task as done:
   [T][X] keep
____________________________________________________________
 OOPS! Please provide a task number, for example 'delete 2'.
____________________________________________________________
 OOPS! 'abc' is not a valid task number.
____________________________________________________________
 OOPS! Task 0 does not exist. Choose a number from 1 to 2.
____________________________________________________________
 OOPS! Task 3 does not exist. Choose a number from 1 to 2.
____________________________________________________________
 Here are the tasks in your list:
 1.[T][X] keep
 2.[T][ ] remove
____________________________________________________________
 Noted. I've removed this task:
   [T][ ] remove
 Now you have 1 tasks in the list.
____________________________________________________________
 Here are the tasks in your list:
 1.[T][X] keep
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

## UI-18: Reject repeated date delimiters and zero-length events

**Aim:** Verify that ambiguous date delimiters and equal event dates are rejected without changing the existing task list.

### Input

```text
todo anchor
deadline report /by 2026-09-01 /by 2026-09-02
event trip /from 2026-09-01 /from 2026-09-02 /to 2026-09-03
event trip /from 2026-09-01 /to 2026-09-02 /to 2026-09-03
event one-day /from 2026-09-01 /to 2026-09-01
list
bye
```

### Expected output

```text
____________________________________________________________
  ____
 / ___| _ __   ___   ___  _ __  _   _
 \___ \| '_ \ / _ \ / _ \| '_ \| | | |
  ___) | | | | (_) | (_) | |_) | |_| |
 |____/|_| |_|\___/ \___/| .__/ \__, |
                            |_|    |___/
Hi! I'm Snoopy, your happy little helper.
What can I do for you?
____________________________________________________________
 Got it. I've added this task:
   [T][ ] anchor
 Now you have 1 tasks in the list.
____________________________________________________________
 OOPS! A deadline must contain exactly one '/by' separator.
____________________________________________________________
 OOPS! An event must contain exactly one '/from' and one '/to' separator.
____________________________________________________________
 OOPS! An event must contain exactly one '/from' and one '/to' separator.
____________________________________________________________
 OOPS! The event end date must be after its start date.
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] anchor
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```
