# UI Test Plan

Each test case starts a fresh instance of `Groot`. Begin the suite without a `data` directory so TC1 covers first-run startup and later task commands cover automatic directory creation. Expected-output blocks contain program stdout only; console input is recorded separately.

Run the recorded suite with Java assertions enabled (`JDK_JAVA_OPTIONS=-ea`).
TC2 and TC6 exercise status postconditions; TC3 verifies that invalid user input still produces normal errors
and preserves task state. JUnit separately checks impossible internal states and faulty task subclasses.
The separate `test/storage-ui-test-plan.md` covers startup with an invalid saved date;
run it in a separate temporary repository with its specified fixture.

GUI error highlighting is covered separately: submit `unknown`, then `list`, then `todo`,
then `help`. Each rejected command should have an “Error:” prefix, bold dark red text,
a pale red background, and a red border. Successful replies should retain ordinary styling,
and earlier error bubbles should stay highlighted. Console wording remains unchanged;
the existing invalid-input and state-preservation cases remain applicable.

The JavaFX tests also cover the read-only task panel and chat layout: load saved todos,
deadlines, and events; submit add/mark/unmark/delete commands and check numbers and counts.
Search, sorting, rejected commands, and failed saves must preserve main-list panel state.
Verify G/You badges, the initial welcome card, blank-input handling, Enter/Send submission,
input clearing, wrapping, independent scrolling, and scrolling to the latest reply.
The requested default window is 1000 × 720 and the minimum is 800 × 600. Check the
requested size before showing the window; afterward, allow the window manager to
fit it to the display within those bounds. Visually inspect the full window with
representative tasks before refreshing `docs/Ui.png`.

## TC1: Exit using bye

**Aim:** Verify that Groot starts normally without an existing data folder or file and exits with the farewell message when the user enters `bye`.

### Input

```text
bye
```

### Expected output

```text
____________________________________________________________
       \  |  /
     ___\_|_/___
    /   /   \   \
   /   | o o |    |
  |    |  ^  |    |
  |    \ \_/ /    |
   \    '---'    /
    \  |||||||  /
     | ||||||| |
  ___|_|||||||_|___
 /     |||||||     \
/      |||||||      \
       |||||||
      /||| |||\
     /_||| |||_\
Hello! I'm Groot.
What can I do for you?
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

## TC2: Manage todos, deadlines, and events

**Aim:** Verify that all three task types are stored polymorphically, valid ISO deadline dates are reformatted for display, and task statuses can be marked and unmarked.

### Input

```text
todo borrow book
deadline return book /by 2019-12-02
deadline do homework /by 2020-02-29
event project meeting /from Mon 2pm /to 4pm
mark 1
mark 2
list
unmark 2
list
delete 1
delete 1
delete 1
delete 1
bye
```

### Expected output

```text
____________________________________________________________
       \  |  /
     ___\_|_/___
    /   /   \   \
   /   | o o |    |
  |    |  ^  |    |
  |    \ \_/ /    |
   \    '---'    /
    \  |||||||  /
     | ||||||| |
  ___|_|||||||_|___
 /     |||||||     \
/      |||||||      \
       |||||||
      /||| |||\
     /_||| |||_\
Hello! I'm Groot.
What can I do for you?
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] borrow book
 Now you have 1 task in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [D][ ] return book (by: Dec 02 2019)
 Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [D][ ] do homework (by: Feb 29 2020)
 Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [E][ ] project meeting (from: Mon 2pm to: 4pm)
 Now you have 4 tasks in the list.
____________________________________________________________
____________________________________________________________
 Nice! I've marked this task as done:
   [T][X] borrow book
____________________________________________________________
____________________________________________________________
 Nice! I've marked this task as done:
   [D][X] return book (by: Dec 02 2019)
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][X] borrow book
 2.[D][X] return book (by: Dec 02 2019)
 3.[D][ ] do homework (by: Feb 29 2020)
 4.[E][ ] project meeting (from: Mon 2pm to: 4pm)
____________________________________________________________
____________________________________________________________
 OK, I've marked this task as not done yet:
   [D][ ] return book (by: Dec 02 2019)
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][X] borrow book
 2.[D][ ] return book (by: Dec 02 2019)
 3.[D][ ] do homework (by: Feb 29 2020)
 4.[E][ ] project meeting (from: Mon 2pm to: 4pm)
____________________________________________________________
____________________________________________________________
 Noted. I've removed this task:
   [T][X] borrow book
 Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
 Noted. I've removed this task:
   [D][ ] return book (by: Dec 02 2019)
 Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 Noted. I've removed this task:
   [D][ ] do homework (by: Feb 29 2020)
 Now you have 1 task in the list.
____________________________________________________________
____________________________________________________________
 Noted. I've removed this task:
   [E][ ] project meeting (from: Mon 2pm to: 4pm)
 Now you have 0 tasks in the list.
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

## TC3: Reject invalid commands without corrupting tasks

**Aim:** Verify unknown commands and invalid arguments interleaved with valid mark/unmark commands with assertions enabled. Confirm that rejected indices preserve task state and repeated status commands remain safe.

### Input

```text
todo
blah
list extra
bye now
todo read book
mark
mark two
mark 2
mark 1
mark 0
mark 1
list
unmark
unmark 1
unmark 1
list
delete 1
bye
```

### Expected output

```text
____________________________________________________________
       \  |  /
     ___\_|_/___
    /   /   \   \
   /   | o o |    |
  |    |  ^  |    |
  |    \ \_/ /    |
   \    '---'    /
    \  |||||||  /
     | ||||||| |
  ___|_|||||||_|___
 /     |||||||     \
/      |||||||      \
       |||||||
      /||| |||\
     /_||| |||_\
Hello! I'm Groot.
What can I do for you?
____________________________________________________________
____________________________________________________________
 Oops! A todo needs a description.
____________________________________________________________
____________________________________________________________
 Oops! I don't recognise that command.
____________________________________________________________
____________________________________________________________
 Oops! I don't recognise that command.
____________________________________________________________
____________________________________________________________
 Oops! I don't recognise that command.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] read book
 Now you have 1 task in the list.
____________________________________________________________
____________________________________________________________
 Oops! Tell me which task to mark.
____________________________________________________________
____________________________________________________________
 Oops! The task number must be a whole number.
____________________________________________________________
____________________________________________________________
 Oops! Task 2 is not in the list.
____________________________________________________________
____________________________________________________________
 Nice! I've marked this task as done:
   [T][X] read book
____________________________________________________________
____________________________________________________________
 Oops! Task 0 is not in the list.
____________________________________________________________
____________________________________________________________
 Nice! I've marked this task as done:
   [T][X] read book
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][X] read book
____________________________________________________________
____________________________________________________________
 Oops! Tell me which task to unmark.
____________________________________________________________
____________________________________________________________
 OK, I've marked this task as not done yet:
   [T][ ] read book
____________________________________________________________
____________________________________________________________
 OK, I've marked this task as not done yet:
   [T][ ] read book
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] read book
____________________________________________________________
____________________________________________________________
 Noted. I've removed this task:
   [T][ ] read book
 Now you have 0 tasks in the list.
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

## TC4: Reject malformed deadlines and events

**Aim:** Verify errors for missing deadline/event fields, non-ISO and impossible dates, and reversed event markers. Interleave invalid commands with valid tasks and confirm rejected inputs preserve the list.

### Input

```text
deadline return book
deadline /by Sunday
deadline return book /by
deadline return book /by Sunday
deadline return book /by 2019-02-29
event meeting /from Mon
event /from Mon /to Tue
event meeting /from /to Tue
event meeting /from Mon /to
deadline return book /by 2019-12-01
event meeting /to Tue /from Mon
event meeting /from Mon /to Tue
deadline report /by 2023-02-29
list
delete 1
delete 1
bye
```

### Expected output

```text
____________________________________________________________
       \  |  /
     ___\_|_/___
    /   /   \   \
   /   | o o |    |
  |    |  ^  |    |
  |    \ \_/ /    |
   \    '---'    /
    \  |||||||  /
     | ||||||| |
  ___|_|||||||_|___
 /     |||||||     \
/      |||||||      \
       |||||||
      /||| |||\
     /_||| |||_\
Hello! I'm Groot.
What can I do for you?
____________________________________________________________
____________________________________________________________
 Oops! Use: deadline DESCRIPTION /by DATE
____________________________________________________________
____________________________________________________________
 Oops! A deadline needs a description.
____________________________________________________________
____________________________________________________________
 Oops! A deadline needs a date after /by.
____________________________________________________________
____________________________________________________________
 Oops! Use deadline dates in yyyy-MM-dd format, e.g. 2019-10-15.
____________________________________________________________
____________________________________________________________
 Oops! Use deadline dates in yyyy-MM-dd format, e.g. 2019-10-15.
____________________________________________________________
____________________________________________________________
 Oops! Use: event DESCRIPTION /from START /to END
____________________________________________________________
____________________________________________________________
 Oops! An event needs a description.
____________________________________________________________
____________________________________________________________
 Oops! An event needs a start date or time after /from.
____________________________________________________________
____________________________________________________________
 Oops! An event needs an end date or time after /to.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [D][ ] return book (by: Dec 01 2019)
 Now you have 1 task in the list.
____________________________________________________________
____________________________________________________________
 Oops! Use: event DESCRIPTION /from START /to END
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [E][ ] meeting (from: Mon to: Tue)
 Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 Oops! Use deadline dates in yyyy-MM-dd format, e.g. 2019-10-15.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[D][ ] return book (by: Dec 01 2019)
 2.[E][ ] meeting (from: Mon to: Tue)
____________________________________________________________
____________________________________________________________
 Noted. I've removed this task:
   [D][ ] return book (by: Dec 01 2019)
 Now you have 1 task in the list.
____________________________________________________________
____________________________________________________________
 Noted. I've removed this task:
   [E][ ] meeting (from: Mon to: Tue)
 Now you have 0 tasks in the list.
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

## TC5: Delete tasks and preserve list state

**Aim:** Verify middle, first, and last deletion with ArrayList reindexing, retained task status, invalid delete handling, and deletion from an empty list.

### Input

```text
todo alpha
deadline beta /by 2021-01-01
event gamma /from 2pm /to 3pm
mark 3
delete 2
list
delete
delete two
delete 0
delete 3
list
delete 1
delete 1
list
delete 1
bye
```

### Expected output

```text
____________________________________________________________
       \  |  /
     ___\_|_/___
    /   /   \   \
   /   | o o |    |
  |    |  ^  |    |
  |    \ \_/ /    |
   \    '---'    /
    \  |||||||  /
     | ||||||| |
  ___|_|||||||_|___
 /     |||||||     \
/      |||||||      \
       |||||||
      /||| |||\
     /_||| |||_\
Hello! I'm Groot.
What can I do for you?
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] alpha
 Now you have 1 task in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [D][ ] beta (by: Jan 01 2021)
 Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [E][ ] gamma (from: 2pm to: 3pm)
 Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
 Nice! I've marked this task as done:
   [E][X] gamma (from: 2pm to: 3pm)
____________________________________________________________
____________________________________________________________
 Noted. I've removed this task:
   [D][ ] beta (by: Jan 01 2021)
 Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] alpha
 2.[E][X] gamma (from: 2pm to: 3pm)
____________________________________________________________
____________________________________________________________
 Oops! Tell me which task to delete.
____________________________________________________________
____________________________________________________________
 Oops! The task number must be a whole number.
____________________________________________________________
____________________________________________________________
 Oops! Task 0 is not in the list.
____________________________________________________________
____________________________________________________________
 Oops! Task 3 is not in the list.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] alpha
 2.[E][X] gamma (from: 2pm to: 3pm)
____________________________________________________________
____________________________________________________________
 Noted. I've removed this task:
   [T][ ] alpha
 Now you have 1 task in the list.
____________________________________________________________
____________________________________________________________
 Noted. I've removed this task:
   [E][X] gamma (from: 2pm to: 3pm)
 Now you have 0 tasks in the list.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
____________________________________________________________
____________________________________________________________
 Oops! Task 1 is not in the list.
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

## TC6: Save every task-list change

**Aim:** Verify that add, mark, unmark, and delete commands succeed in sequence and save a deterministic task list for TC7 to load after restart.

### Input

```text
todo keep this
deadline submit report /by 2024-03-08
event team meeting /from 2pm /to 3pm
todo remove this
todo symbols | and \ slash
mark 1
delete 4
unmark 1
mark 1
bye
```

### Expected output

```text
____________________________________________________________
       \  |  /
     ___\_|_/___
    /   /   \   \
   /   | o o |    |
  |    |  ^  |    |
  |    \ \_/ /    |
   \    '---'    /
    \  |||||||  /
     | ||||||| |
  ___|_|||||||_|___
 /     |||||||     \
/      |||||||      \
       |||||||
      /||| |||\
     /_||| |||_\
Hello! I'm Groot.
What can I do for you?
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] keep this
 Now you have 1 task in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [D][ ] submit report (by: Mar 08 2024)
 Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [E][ ] team meeting (from: 2pm to: 3pm)
 Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] remove this
 Now you have 4 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] symbols | and \ slash
 Now you have 5 tasks in the list.
____________________________________________________________
____________________________________________________________
 Nice! I've marked this task as done:
   [T][X] keep this
____________________________________________________________
____________________________________________________________
 Noted. I've removed this task:
   [T][ ] remove this
 Now you have 4 tasks in the list.
____________________________________________________________
____________________________________________________________
 OK, I've marked this task as not done yet:
   [T][ ] keep this
____________________________________________________________
____________________________________________________________
 Nice! I've marked this task as done:
   [T][X] keep this
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

## TC7: Load saved tasks after restart

**Aim:** Verify that a fresh Groot process loads the task types, details, order, and completion states saved by TC6, then cleans up the shared test data.

### Input

```text
list
delete 1
delete 1
delete 1
delete 1
bye
```

### Expected output

```text
____________________________________________________________
       \  |  /
     ___\_|_/___
    /   /   \   \
   /   | o o |    |
  |    |  ^  |    |
  |    \ \_/ /    |
   \    '---'    /
    \  |||||||  /
     | ||||||| |
  ___|_|||||||_|___
 /     |||||||     \
/      |||||||      \
       |||||||
      /||| |||\
     /_||| |||_\
Hello! I'm Groot.
What can I do for you?
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][X] keep this
 2.[D][ ] submit report (by: Mar 08 2024)
 3.[E][ ] team meeting (from: 2pm to: 3pm)
 4.[T][ ] symbols | and \ slash
____________________________________________________________
____________________________________________________________
 Noted. I've removed this task:
   [T][X] keep this
 Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
 Noted. I've removed this task:
   [D][ ] submit report (by: Mar 08 2024)
 Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 Noted. I've removed this task:
   [E][ ] team meeting (from: 2pm to: 3pm)
 Now you have 1 task in the list.
____________________________________________________________
____________________________________________________________
 Noted. I've removed this task:
   [T][ ] symbols | and \ slash
 Now you have 0 tasks in the list.
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

## TC8: Find tasks by description

**Aim:** Verify case-insensitive keyword and phrase searches, ordered result numbering, no-match output, missing-keyword handling, and that searches do not change the task list.

### Input

```text
todo read book
deadline return book /by 2019-12-02
event project team meeting /from 2pm /to 3pm
todo buy groceries
mark 1
mark 2
find book
find BOOK
find team meeting
find library
find
list
delete 1
delete 1
delete 1
delete 1
bye
```

### Expected output

```text
____________________________________________________________
       \  |  /
     ___\_|_/___
    /   /   \   \
   /   | o o |    |
  |    |  ^  |    |
  |    \ \_/ /    |
   \    '---'    /
    \  |||||||  /
     | ||||||| |
  ___|_|||||||_|___
 /     |||||||     \
/      |||||||      \
       |||||||
      /||| |||\
     /_||| |||_\
Hello! I'm Groot.
What can I do for you?
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] read book
 Now you have 1 task in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [D][ ] return book (by: Dec 02 2019)
 Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [E][ ] project team meeting (from: 2pm to: 3pm)
 Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] buy groceries
 Now you have 4 tasks in the list.
____________________________________________________________
____________________________________________________________
 Nice! I've marked this task as done:
   [T][X] read book
____________________________________________________________
____________________________________________________________
 Nice! I've marked this task as done:
   [D][X] return book (by: Dec 02 2019)
____________________________________________________________
____________________________________________________________
 Here are the matching tasks in your list:
 1.[T][X] read book
 2.[D][X] return book (by: Dec 02 2019)
____________________________________________________________
____________________________________________________________
 Here are the matching tasks in your list:
 1.[T][X] read book
 2.[D][X] return book (by: Dec 02 2019)
____________________________________________________________
____________________________________________________________
 Here are the matching tasks in your list:
 1.[E][ ] project team meeting (from: 2pm to: 3pm)
____________________________________________________________
____________________________________________________________
 Here are the matching tasks in your list:
____________________________________________________________
____________________________________________________________
 Oops! Tell me what to find.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][X] read book
 2.[D][X] return book (by: Dec 02 2019)
 3.[E][ ] project team meeting (from: 2pm to: 3pm)
 4.[T][ ] buy groceries
____________________________________________________________
____________________________________________________________
 Noted. I've removed this task:
   [T][X] read book
 Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
 Noted. I've removed this task:
   [D][X] return book (by: Dec 02 2019)
 Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 Noted. I've removed this task:
   [E][ ] project team meeting (from: 2pm to: 3pm)
 Now you have 1 task in the list.
____________________________________________________________
____________________________________________________________
 Noted. I've removed this task:
   [T][ ] buy groceries
 Now you have 0 tasks in the list.
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

## TC9: Display command help

**Aim:** Verify that streamed command lookup accepts the help alias, rejects alias prefixes and trailing arguments, and preserves task state when valid and invalid commands are interleaved.

### Input

```text
todo keep state
help extra
--help
-hello
-h extra
list
delete 1
bye
```

### Expected output

```text
____________________________________________________________
       \  |  /
     ___\_|_/___
    /   /   \   \
   /   | o o |    |
  |    |  ^  |    |
  |    \ \_/ /    |
   \    '---'    /
    \  |||||||  /
     | ||||||| |
  ___|_|||||||_|___
 /     |||||||     \
/      |||||||      \
       |||||||
      /||| |||\
     /_||| |||_\
Hello! I'm Groot.
What can I do for you?
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] keep state
 Now you have 1 task in the list.
____________________________________________________________
____________________________________________________________
 Oops! I don't recognise that command.
____________________________________________________________
____________________________________________________________
 Here are the commands you can use:
   todo DESCRIPTION - Add a todo task.
   deadline DESCRIPTION /by YYYY-MM-DD - Add a deadline task.
   event DESCRIPTION /from START /to END - Add an event task.
   list - Show all tasks.
   sort [-r | --reverse] - Show deadlines by date; reverse shows latest first.
   find KEYWORD - Find tasks by description.
   mark NUMBER - Mark a task as done.
   unmark NUMBER - Mark a task as not done.
   delete NUMBER - Delete a task.
   help, --help, -h - Show this help message.
   bye - Exit Groot.
____________________________________________________________
____________________________________________________________
 Oops! I don't recognise that command.
____________________________________________________________
____________________________________________________________
 Oops! I don't recognise that command.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] keep state
____________________________________________________________
____________________________________________________________
 Noted. I've removed this task:
   [T][ ] keep state
 Now you have 0 tasks in the list.
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```


## TC10: Sort deadlines without changing task order

**Aim:** Verify empty and no-deadline responses, both reverse flags, forgiving casing and a literal tab, date/name ordering, completed deadlines, and original task numbers. Interleave invalid commands with valid sorting and task changes; confirm rejected commands leave the list unchanged and new tasks append normally. Leave five tasks for TC11.

### Input

```text
sort -x
sort
todo groceries
event meeting /from Mon 2pm /to 4pm
sort --reverse
deadline report /by 2026-09-20
deadline beta /by 2026-09-12
deadline Alpha /by 2026-09-12
mark 5
sort
sort reverse
SORT	-R
sort -r -r
sort -r --reverse
list
sort   --REVERSE
sort --reverse extra
sort --reverse=true
sorting
sort-r
sort
mark 4
unmark 5
delete 3
sort
deadline early /by 2025-01-01
list
sort
bye
```

### Expected output

```text
____________________________________________________________
       \  |  /
     ___\_|_/___
    /   /   \   \
   /   | o o |    |
  |    |  ^  |    |
  |    \ \_/ /    |
   \    '---'    /
    \  |||||||  /
     | ||||||| |
  ___|_|||||||_|___
 /     |||||||     \
/      |||||||      \
       |||||||
      /||| |||\
     /_||| |||_\
Hello! I'm Groot.
What can I do for you?
____________________________________________________________
____________________________________________________________
 Oops! Use: sort [-r | --reverse].
____________________________________________________________
____________________________________________________________
 There are no deadlines to sort.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] groceries
 Now you have 1 task in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [E][ ] meeting (from: Mon 2pm to: 4pm)
 Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 There are no deadlines to sort.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [D][ ] report (by: Sep 20 2026)
 Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [D][ ] beta (by: Sep 12 2026)
 Now you have 4 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [D][ ] Alpha (by: Sep 12 2026)
 Now you have 5 tasks in the list.
____________________________________________________________
____________________________________________________________
 Nice! I've marked this task as done:
   [D][X] Alpha (by: Sep 12 2026)
____________________________________________________________
____________________________________________________________
 Here are your deadlines sorted by date (earliest first):
 5.[D][X] Alpha (by: Sep 12 2026)
 4.[D][ ] beta (by: Sep 12 2026)
 3.[D][ ] report (by: Sep 20 2026)
____________________________________________________________
____________________________________________________________
 Oops! Use: sort [-r | --reverse].
____________________________________________________________
____________________________________________________________
 Here are your deadlines sorted by date (latest first):
 3.[D][ ] report (by: Sep 20 2026)
 5.[D][X] Alpha (by: Sep 12 2026)
 4.[D][ ] beta (by: Sep 12 2026)
____________________________________________________________
____________________________________________________________
 Oops! Use: sort [-r | --reverse].
____________________________________________________________
____________________________________________________________
 Oops! Use: sort [-r | --reverse].
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] groceries
 2.[E][ ] meeting (from: Mon 2pm to: 4pm)
 3.[D][ ] report (by: Sep 20 2026)
 4.[D][ ] beta (by: Sep 12 2026)
 5.[D][X] Alpha (by: Sep 12 2026)
____________________________________________________________
____________________________________________________________
 Here are your deadlines sorted by date (latest first):
 3.[D][ ] report (by: Sep 20 2026)
 5.[D][X] Alpha (by: Sep 12 2026)
 4.[D][ ] beta (by: Sep 12 2026)
____________________________________________________________
____________________________________________________________
 Oops! Use: sort [-r | --reverse].
____________________________________________________________
____________________________________________________________
 Oops! Use: sort [-r | --reverse].
____________________________________________________________
____________________________________________________________
 Oops! I don't recognise that command.
____________________________________________________________
____________________________________________________________
 Oops! I don't recognise that command.
____________________________________________________________
____________________________________________________________
 Here are your deadlines sorted by date (earliest first):
 5.[D][X] Alpha (by: Sep 12 2026)
 4.[D][ ] beta (by: Sep 12 2026)
 3.[D][ ] report (by: Sep 20 2026)
____________________________________________________________
____________________________________________________________
 Nice! I've marked this task as done:
   [D][X] beta (by: Sep 12 2026)
____________________________________________________________
____________________________________________________________
 OK, I've marked this task as not done yet:
   [D][ ] Alpha (by: Sep 12 2026)
____________________________________________________________
____________________________________________________________
 Noted. I've removed this task:
   [D][ ] report (by: Sep 20 2026)
 Now you have 4 tasks in the list.
____________________________________________________________
____________________________________________________________
 Here are your deadlines sorted by date (earliest first):
 4.[D][ ] Alpha (by: Sep 12 2026)
 3.[D][X] beta (by: Sep 12 2026)
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [D][ ] early (by: Jan 01 2025)
 Now you have 5 tasks in the list.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] groceries
 2.[E][ ] meeting (from: Mon 2pm to: 4pm)
 3.[D][X] beta (by: Sep 12 2026)
 4.[D][ ] Alpha (by: Sep 12 2026)
 5.[D][ ] early (by: Jan 01 2025)
____________________________________________________________
____________________________________________________________
 Here are your deadlines sorted by date (earliest first):
 5.[D][ ] early (by: Jan 01 2025)
 4.[D][ ] Alpha (by: Sep 12 2026)
 3.[D][X] beta (by: Sep 12 2026)
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

## TC11: Preserve main-list order after sorting and restarting

**Aim:** Load TC10's tasks in their unsorted saved order with unchanged details and statuses. Verify reverse name ties, refreshed task numbers after deletions, a singleton result, invalid-input state preservation, and the common no-deadline response; remove the test tasks afterward.

### Input

```text
list
sort -r
delete 5
delete 4
sort
sort --reverse
sort deadline
list
delete 3
sort
delete 2
delete 1
sort -r
bye
```

### Expected output

```text
____________________________________________________________
       \  |  /
     ___\_|_/___
    /   /   \   \
   /   | o o |    |
  |    |  ^  |    |
  |    \ \_/ /    |
   \    '---'    /
    \  |||||||  /
     | ||||||| |
  ___|_|||||||_|___
 /     |||||||     \
/      |||||||      \
       |||||||
      /||| |||\
     /_||| |||_\
Hello! I'm Groot.
What can I do for you?
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] groceries
 2.[E][ ] meeting (from: Mon 2pm to: 4pm)
 3.[D][X] beta (by: Sep 12 2026)
 4.[D][ ] Alpha (by: Sep 12 2026)
 5.[D][ ] early (by: Jan 01 2025)
____________________________________________________________
____________________________________________________________
 Here are your deadlines sorted by date (latest first):
 4.[D][ ] Alpha (by: Sep 12 2026)
 3.[D][X] beta (by: Sep 12 2026)
 5.[D][ ] early (by: Jan 01 2025)
____________________________________________________________
____________________________________________________________
 Noted. I've removed this task:
   [D][ ] early (by: Jan 01 2025)
 Now you have 4 tasks in the list.
____________________________________________________________
____________________________________________________________
 Noted. I've removed this task:
   [D][ ] Alpha (by: Sep 12 2026)
 Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
 Here are your deadlines sorted by date (earliest first):
 3.[D][X] beta (by: Sep 12 2026)
____________________________________________________________
____________________________________________________________
 Here are your deadlines sorted by date (latest first):
 3.[D][X] beta (by: Sep 12 2026)
____________________________________________________________
____________________________________________________________
 Oops! Use: sort [-r | --reverse].
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] groceries
 2.[E][ ] meeting (from: Mon 2pm to: 4pm)
 3.[D][X] beta (by: Sep 12 2026)
____________________________________________________________
____________________________________________________________
 Noted. I've removed this task:
   [D][X] beta (by: Sep 12 2026)
 Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 There are no deadlines to sort.
____________________________________________________________
____________________________________________________________
 Noted. I've removed this task:
   [E][ ] meeting (from: Mon 2pm to: 4pm)
 Now you have 1 task in the list.
____________________________________________________________
____________________________________________________________
 Noted. I've removed this task:
   [T][ ] groceries
 Now you have 0 tasks in the list.
____________________________________________________________
____________________________________________________________
 There are no deadlines to sort.
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

## TC12: Reject invalid dates and oversized task numbers

**Aim:** Interleave rejected date and number inputs with valid commands and verify that task state remains unchanged.

### Input

```text
todo sentinel
deadline invalid /by 2025-02-29
list
deadline invalid /by nonsense
list
mark 2147483648
list
delete 1
bye
```

### Expected output

```text
____________________________________________________________
       \  |  /
     ___\_|_/___
    /   /   \   \
   /   | o o |    |
  |    |  ^  |    |
  |    \ \_/ /    |
   \    '---'    /
    \  |||||||  /
     | ||||||| |
  ___|_|||||||_|___
 /     |||||||     \
/      |||||||      \
       |||||||
      /||| |||\
     /_||| |||_\
Hello! I'm Groot.
What can I do for you?
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] sentinel
 Now you have 1 task in the list.
____________________________________________________________
____________________________________________________________
 Oops! Use deadline dates in yyyy-MM-dd format, e.g. 2019-10-15.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] sentinel
____________________________________________________________
____________________________________________________________
 Oops! Use deadline dates in yyyy-MM-dd format, e.g. 2019-10-15.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] sentinel
____________________________________________________________
____________________________________________________________
 Oops! The task number must be a whole number.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] sentinel
____________________________________________________________
____________________________________________________________
 Noted. I've removed this task:
   [T][ ] sentinel
 Now you have 0 tasks in the list.
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```
