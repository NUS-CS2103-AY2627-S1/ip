# UI Test Plan

This file defines automated UI tests for the AXIOM chatbot. Each test case runs a full
interactive session: inputs are piped to the program, and the complete stdout is compared
against the expected output.

**Run command:** `java src/main/java/axiom/Axiom.java`

**Test runner:** `python3 test/run-ui-tests.py`

On macOS, ensure Java 25 is active before running tests:
`sdk use java 25.0.3.fx-zulu`

---

## Test Case 1: Add todo

**Aim:** Verify that `todo` adds a todo task and confirms with the new message format.

**Inputs:**
```
todo read book
bye
```

**Expected output:**
```
     _    __  _____ ___  __  __ 
    / \   \ \/ /_ _/ _ \|  \/  |
   / _ \   \  / | | | | | |\/| |
  / ___ \  /  \ | | |_| | |  | |
 /_/   \_\/_/\_\___\___/|_|  |_|

Hello! I'm AXIOM.
What can I do for you?
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] read book
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________

```

## Test Case 2: List empty tasks

**Aim:** Verify that `list` on an empty task list shows the header with no numbered tasks.

**Inputs:**
```
list
bye
```

**Expected output:**
```
     _    __  _____ ___  __  __ 
    / \   \ \/ /_ _/ _ \|  \/  |
   / _ \   \  / | | | | | |\/| |
  / ___ \  /  \ | | |_| | |  | |
 /_/   \_\/_/\_\___\___/|_|  |_|

Hello! I'm AXIOM.
What can I do for you?
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________

```

## Test Case 3: Add todo, deadline, event and list

**Aim:** Verify that all three task types are stored and listed with correct type prefixes.

**Inputs:**
```
todo read book
deadline return book /by 2019-06-06
event project meeting /from 2019-08-06 1400 /to 2019-08-06 1600
list
bye
```

**Expected output:**
```
     _    __  _____ ___  __  __ 
    / \   \ \/ /_ _/ _ \|  \/  |
   / _ \   \  / | | | | | |\/| |
  / ___ \  /  \ | | |_| | |  | |
 /_/   \_\/_/\_\___\___/|_|  |_|

Hello! I'm AXIOM.
What can I do for you?
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] read book
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [D][ ] return book (by: Jun 06 2019)
 Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [E][ ] project meeting (from: Aug 06 2019, 2:00 PM to: Aug 06 2019, 4:00 PM)
 Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] read book
 2.[D][ ] return book (by: Jun 06 2019)
 3.[E][ ] project meeting (from: Aug 06 2019, 2:00 PM to: Aug 06 2019, 4:00 PM)
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________

```

## Test Case 4: Invalid deadline date

**Aim:** Verify that an unparseable `/by` value is rejected with a helpful error message.

**Inputs:**
```
deadline do homework /by no idea :-p
bye
```

**Expected output:**
```
     _    __  _____ ___  __  __ 
    / \   \ \/ /_ _/ _ \|  \/  |
   / _ \   \  / | | | | | |\/| |
  / ___ \  /  \ | | |_| | |  | |
 /_/   \_\/_/\_\___\___/|_|  |_|

Hello! I'm AXIOM.
What can I do for you?
____________________________________________________________
____________________________________________________________
 Invalid date/time format: 'no idea :-p'. Use yyyy-MM-dd or d/M/yyyy HHmm.
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________

```

## Test Case 5: Mark and unmark todo

**Aim:** Verify that mark and unmark work with todo tasks and update the `[T]` status icon.

**Inputs:**
```
todo read book
todo return book
todo buy bread
mark 2
list
unmark 2
list
bye
```

**Expected output:**
```
     _    __  _____ ___  __  __ 
    / \   \ \/ /_ _/ _ \|  \/  |
   / _ \   \  / | | | | | |\/| |
  / ___ \  /  \ | | |_| | |  | |
 /_/   \_\/_/\_\___\___/|_|  |_|

Hello! I'm AXIOM.
What can I do for you?
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] read book
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] return book
 Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] buy bread
 Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
 Nice! I've marked this task as done:
   [T][X] return book
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] read book
 2.[T][X] return book
 3.[T][ ] buy bread
____________________________________________________________
____________________________________________________________
 OK, I've marked this task as not done yet:
   [T][ ] return book
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] read book
 2.[T][ ] return book
 3.[T][ ] buy bread
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________

```

## Test Case 6: Empty todo description

**Aim:** Verify that `todo` without a description shows an error and does not add a task.

**Inputs:**
```
todo
bye
```

**Expected output:**
```
     _    __  _____ ___  __  __ 
    / \   \ \/ /_ _/ _ \|  \/  |
   / _ \   \  / | | | | | |\/| |
  / ___ \  /  \ | | |_| | |  | |
 /_/   \_\/_/\_\___\___/|_|  |_|

Hello! I'm AXIOM.
What can I do for you?
____________________________________________________________
____________________________________________________________
 A todo needs a description. Usage: todo <description>
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________

```

## Test Case 7: Unknown command

**Aim:** Verify that an unrecognised command shows an error message.

**Inputs:**
```
blah
bye
```

**Expected output:**
```
     _    __  _____ ___  __  __ 
    / \   \ \/ /_ _/ _ \|  \/  |
   / _ \   \  / | | | | | |\/| |
  / ___ \  /  \ | | |_| | |  | |
 /_/   \_\/_/\_\___\___/|_|  |_|

Hello! I'm AXIOM.
What can I do for you?
____________________________________________________________
____________________________________________________________
 Sorry, I don't understand that command.
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________

```

## Test Case 8: Deadline missing /by

**Aim:** Verify that a deadline without `/by` is rejected with a helpful message.

**Inputs:**
```
deadline homework
bye
```

**Expected output:**
```
     _    __  _____ ___  __  __ 
    / \   \ \/ /_ _/ _ \|  \/  |
   / _ \   \  / | | | | | |\/| |
  / ___ \  /  \ | | |_| | |  | |
 /_/   \_\/_/\_\___\___/|_|  |_|

Hello! I'm AXIOM.
What can I do for you?
____________________________________________________________
____________________________________________________________
 A deadline must include /by. Usage: deadline <description> /by <time>
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________

```

## Test Case 9: Invalid mark command

**Aim:** Verify that `mark` without a number and `mark` with an out-of-range number both produce errors.

**Inputs:**
```
mark
mark 99
bye
```

**Expected output:**
```
     _    __  _____ ___  __  __ 
    / \   \ \/ /_ _/ _ \|  \/  |
   / _ \   \  / | | | | | |\/| |
  / ___ \  /  \ | | |_| | |  | |
 /_/   \_\/_/\_\___\___/|_|  |_|

Hello! I'm AXIOM.
What can I do for you?
____________________________________________________________
____________________________________________________________
 Please specify which task to mark. Usage: mark <task number>
____________________________________________________________
____________________________________________________________
 That task number isn't in your list. You currently have 0 task(s).
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________

```

## Test Case 10: Continue after error

**Aim:** Verify that the chatbot keeps running after an error and still accepts valid commands.

**Inputs:**
```
todo read book
blah
todo return book
list
bye
```

**Expected output:**
```
     _    __  _____ ___  __  __ 
    / \   \ \/ /_ _/ _ \|  \/  |
   / _ \   \  / | | | | | |\/| |
  / ___ \  /  \ | | |_| | |  | |
 /_/   \_\/_/\_\___\___/|_|  |_|

Hello! I'm AXIOM.
What can I do for you?
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] read book
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Sorry, I don't understand that command.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] return book
 Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] read book
 2.[T][ ] return book
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________

```

## Test Case 11: Event missing /to

**Aim:** Verify that an event without `/to` is rejected with a helpful message.

**Inputs:**
```
event meeting /from 2pm
bye
```

**Expected output:**
```
     _    __  _____ ___  __  __ 
    / \   \ \/ /_ _/ _ \|  \/  |
   / _ \   \  / | | | | | |\/| |
  / ___ \  /  \ | | |_| | |  | |
 /_/   \_\/_/\_\___\___/|_|  |_|

Hello! I'm AXIOM.
What can I do for you?
____________________________________________________________
____________________________________________________________
 An event must include /from and /to. Usage: event <description> /from <start> /to <end>
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________

```

## Test Case 12: Delete task

**Aim:** Verify that `delete N` removes the specified task and updates the remaining list.

**Inputs:**
```
todo read book
todo return book
event project meeting /from 2019-08-06 1400 /to 2019-08-06 1600
todo borrow book
delete 3
list
bye
```

**Expected output:**
```
     _    __  _____ ___  __  __ 
    / \   \ \/ /_ _/ _ \|  \/  |
   / _ \   \  / | | | | | |\/| |
  / ___ \  /  \ | | |_| | |  | |
 /_/   \_\/_/\_\___\___/|_|  |_|

Hello! I'm AXIOM.
What can I do for you?
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] read book
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] return book
 Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [E][ ] project meeting (from: Aug 06 2019, 2:00 PM to: Aug 06 2019, 4:00 PM)
 Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] borrow book
 Now you have 4 tasks in the list.
____________________________________________________________
____________________________________________________________
 Noted. I've removed this task:
   [E][ ] project meeting (from: Aug 06 2019, 2:00 PM to: Aug 06 2019, 4:00 PM)
 Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] read book
 2.[T][ ] return book
 3.[T][ ] borrow book
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________

```

## Test Case 13: Delete without task number

**Aim:** Verify that `delete` without a number shows an error message.

**Inputs:**
```
delete
bye
```

**Expected output:**
```
     _    __  _____ ___  __  __ 
    / \   \ \/ /_ _/ _ \|  \/  |
   / _ \   \  / | | | | | |\/| |
  / ___ \  /  \ | | |_| | |  | |
 /_/   \_\/_/\_\___\___/|_|  |_|

Hello! I'm AXIOM.
What can I do for you?
____________________________________________________________
____________________________________________________________
 Please specify which task to delete. Usage: delete <task number>
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________

```

## Test Case 14: Save todo to file

**Aim:** Verify that adding and marking a todo saves the task list to `data/axiom.txt`.

**Inputs:**
```
todo read book
mark 1
bye
```

**Expected output:**
```
     _    __  _____ ___  __  __ 
    / \   \ \/ /_ _/ _ \|  \/  |
   / _ \   \  / | | | | | |\/| |
  / ___ \  /  \ | | |_| | |  | |
 /_/   \_\/_/\_\___\___/|_|  |_|

Hello! I'm AXIOM.
What can I do for you?
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] read book
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Nice! I've marked this task as done:
   [T][X] read book
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________

```

**Expected file:** `data/axiom.txt`
```
T | 1 | read book

```

## Test Case 15: Save after add, mark, and delete

**Aim:** Verify that the data file reflects the final task list after multiple changes.

**Inputs:**
```
todo read book
deadline return book /by 2019-06-06
event project meeting /from 2019-08-06 1400 /to 2019-08-06 1600
mark 1
delete 3
bye
```

**Expected output:**
```
     _    __  _____ ___  __  __ 
    / \   \ \/ /_ _/ _ \|  \/  |
   / _ \   \  / | | | | | |\/| |
  / ___ \  /  \ | | |_| | |  | |
 /_/   \_\/_/\_\___\___/|_|  |_|

Hello! I'm AXIOM.
What can I do for you?
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] read book
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [D][ ] return book (by: Jun 06 2019)
 Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [E][ ] project meeting (from: Aug 06 2019, 2:00 PM to: Aug 06 2019, 4:00 PM)
 Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
 Nice! I've marked this task as done:
   [T][X] read book
____________________________________________________________
____________________________________________________________
 Noted. I've removed this task:
   [E][ ] project meeting (from: Aug 06 2019, 2:00 PM to: Aug 06 2019, 4:00 PM)
 Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________

```

**Expected file:** `data/axiom.txt`
```
T | 1 | read book
D | 0 | return book | 2019-06-06T00:00

```

## Test Case 16: Load tasks from file on startup

**Aim:** Verify that tasks are loaded from `data/axiom.txt` when the chatbot starts.

**Initial file:** `data/axiom.txt`
```
T | 1 | read book
D | 0 | return book | 2019-06-06T00:00
E | 0 | project meeting | 2019-08-06T14:00 to 2019-08-06T16:00

```

**Inputs:**
```
list
bye
```

**Expected output:**
```
     _    __  _____ ___  __  __ 
    / \   \ \/ /_ _/ _ \|  \/  |
   / _ \   \  / | | | | | |\/| |
  / ___ \  /  \ | | |_| | |  | |
 /_/   \_\/_/\_\___\___/|_|  |_|

Hello! I'm AXIOM.
What can I do for you?
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][X] read book
 2.[D][ ] return book (by: Jun 06 2019)
 3.[E][ ] project meeting (from: Aug 06 2019, 2:00 PM to: Aug 06 2019, 4:00 PM)
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________

```

## Test Case 17: Load then add task

**Aim:** Verify that a loaded task list can be extended and saved back to disk.

**Initial file:** `data/axiom.txt`
```
T | 0 | read book

```

**Inputs:**
```
todo return book
list
bye
```

**Expected output:**
```
     _    __  _____ ___  __  __ 
    / \   \ \/ /_ _/ _ \|  \/  |
   / _ \   \  / | | | | | |\/| |
  / ___ \  /  \ | | |_| | |  | |
 /_/   \_\/_/\_\___\___/|_|  |_|

Hello! I'm AXIOM.
What can I do for you?
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] return book
 Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] read book
 2.[T][ ] return book
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________

```

**Expected file:** `data/axiom.txt`
```
T | 0 | read book
T | 0 | return book

```

## Test Case 18: First run with no data folder

**Aim:** Verify that the chatbot starts with an empty task list when neither `data/` nor `data/axiom.txt` exists.

**Inputs:**
```
list
bye
```

**Expected output:**
```
     _    __  _____ ___  __  __ 
    / \   \ \/ /_ _/ _ \|  \/  |
   / _ \   \  / | | | | | |\/| |
  / ___ \  /  \ | | |_| | |  | |
 /_/   \_\/_/\_\___\___/|_|  |_|

Hello! I'm AXIOM.
What can I do for you?
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________

```

## Test Case 19: Recover from corrupt storage file

**Aim:** Verify that a corrupt data file shows an error on startup but the chatbot can still accept new tasks.

**Initial file:** `data/axiom.txt`
```
T | 1 | read book
BAD LINE

```

**Inputs:**
```
todo borrow book
list
bye
```

**Expected output:**
```
     _    __  _____ ___  __  __ 
    / \   \ \/ /_ _/ _ \|  \/  |
   / _ \   \  / | | | | | |\/| |
  / ___ \  /  \ | | |_| | |  | |
 /_/   \_\/_/\_\___\___/|_|  |_|

Hello! I'm AXIOM.
What can I do for you?
____________________________________________________________
 Problem in data/axiom.txt at line 2: expected format TYPE | STATUS | DESCRIPTION.
____________________________________________________________
 Got it. I've added this task:
   [T][ ] borrow book
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] borrow book
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________

```

**Expected file:** `data/axiom.txt`
```
T | 0 | borrow book

```

## Test Case 20: Create data folder on first save

**Aim:** Verify that adding the first task creates `data/` and `data/axiom.txt` when they do not exist yet.

**Inputs:**
```
todo read book
bye
```

**Expected output:**
```
     _    __  _____ ___  __  __ 
    / \   \ \/ /_ _/ _ \|  \/  |
   / _ \   \  / | | | | | |\/| |
  / ___ \  /  \ | | |_| | |  | |
 /_/   \_\/_/\_\___\___/|_|  |_|

Hello! I'm AXIOM.
What can I do for you?
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] read book
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________

```

**Expected file:** `data/axiom.txt`
```
T | 0 | read book

```

## Test Case 21: Parse d/M/yyyy HHmm deadline

**Aim:** Verify that `deadline ... /by d/M/yyyy HHmm` is parsed as a date and time and displayed in readable format.

**Inputs:**
```
deadline return book /by 2/12/2019 1800
list
bye
```

**Expected output:**
```
     _    __  _____ ___  __  __ 
    / \   \ \/ /_ _/ _ \|  \/  |
   / _ \   \  / | | | | | |\/| |
  / ___ \  /  \ | | |_| | |  | |
 /_/   \_\/_/\_\___\___/|_|  |_|

Hello! I'm AXIOM.
What can I do for you?
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [D][ ] return book (by: Dec 02 2019, 6:00 PM)
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[D][ ] return book (by: Dec 02 2019, 6:00 PM)
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________

```

**Expected file:** `data/axiom.txt`
```
D | 0 | return book | 2019-12-02T18:00

```

---

## Test Case 22: Find tasks by keyword

**Aim:** Verify that `find` displays matching tasks with their original list numbers.

**Inputs:**
```
todo read book
deadline return book /by 2019-06-06
mark 1
mark 2
find book
bye
```

**Expected output:**
```
     _    __  _____ ___  __  __ 
    / \   \ \/ /_ _/ _ \|  \/  |
   / _ \   \  / | | | | | |\/| |
  / ___ \  /  \ | | |_| | |  | |
 /_/   \_\/_/\_\___\___/|_|  |_|

Hello! I'm AXIOM.
What can I do for you?
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] read book
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [D][ ] return book (by: Jun 06 2019)
 Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 Nice! I've marked this task as done:
   [T][X] read book
____________________________________________________________
____________________________________________________________
 Nice! I've marked this task as done:
   [D][X] return book (by: Jun 06 2019)
____________________________________________________________
____________________________________________________________
 Here are the matching tasks in your list:
 1.[T][X] read book
 2.[D][X] return book (by: Jun 06 2019)
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________

```

---

## Test Case 23: Sort tasks chronologically

**Aim:** Verify that `sort` reorders deadlines and events by date, places todos last, persists the new order, and that `list` then shows the same order.

**Inputs:**
```
todo read book
deadline return book /by 2019-10-15
event project meeting /from 2019-08-06 1400 /to 2019-08-06 1600
deadline homework /by 2019-06-06
sort
list
bye
```

**Expected output:**
```
     _    __  _____ ___  __  __ 
    / \   \ \/ /_ _/ _ \|  \/  |
   / _ \   \  / | | | | | |\/| |
  / ___ \  /  \ | | |_| | |  | |
 /_/   \_\/_/\_\___\___/|_|  |_|

Hello! I'm AXIOM.
What can I do for you?
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] read book
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [D][ ] return book (by: Oct 15 2019)
 Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [E][ ] project meeting (from: Aug 06 2019, 2:00 PM to: Aug 06 2019, 4:00 PM)
 Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [D][ ] homework (by: Jun 06 2019)
 Now you have 4 tasks in the list.
____________________________________________________________
____________________________________________________________
 OK, I've sorted your tasks chronologically:
 1.[D][ ] homework (by: Jun 06 2019)
 2.[E][ ] project meeting (from: Aug 06 2019, 2:00 PM to: Aug 06 2019, 4:00 PM)
 3.[D][ ] return book (by: Oct 15 2019)
 4.[T][ ] read book
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[D][ ] homework (by: Jun 06 2019)
 2.[E][ ] project meeting (from: Aug 06 2019, 2:00 PM to: Aug 06 2019, 4:00 PM)
 3.[D][ ] return book (by: Oct 15 2019)
 4.[T][ ] read book
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________

```

**Expected file:** `data/axiom.txt`
```
D | 0 | homework | 2019-06-06T00:00
E | 0 | project meeting | 2019-08-06T14:00 to 2019-08-06T16:00
D | 0 | return book | 2019-10-15T00:00
T | 0 | read book

```

---

## Test Case 24: Reject invalid commands and data

**Aim:** Verify leading/extra spaces are accepted, while blank input, duplicates, impossible dates, repeated flags, reversed event times, extra arguments, and pipe characters are rejected with clear errors.

**Inputs:**
```

  todo   read book
todo read book
deadline homework /by 2019-02-30
deadline homework /by 2019-06-06 /by 2019-07-01
event meeting /from 2019-08-06 1600 /to 2019-08-06 1400
list extra
todo read | book
list
bye
```

**Expected output:**
```
     _    __  _____ ___  __  __ 
    / \   \ \/ /_ _/ _ \|  \/  |
   / _ \   \  / | | | | | |\/| |
  / ___ \  /  \ | | |_| | |  | |
 /_/   \_\/_/\_\___\___/|_|  |_|

Hello! I'm AXIOM.
What can I do for you?
____________________________________________________________
____________________________________________________________
 Please enter a command.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] read book
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 That task is already in your list.
____________________________________________________________
____________________________________________________________
 '2019-02-30' is not a valid date or time.
____________________________________________________________
____________________________________________________________
 The /by time is specified more than once. Usage: deadline <description> /by <time>
____________________________________________________________
____________________________________________________________
 The event /from time must be earlier than the /to time.
____________________________________________________________
____________________________________________________________
 list does not take any arguments. Usage: list
____________________________________________________________
____________________________________________________________
 Task descriptions cannot contain '|'.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] read book
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________

```

**Expected file:** `data/axiom.txt`
```
T | 0 | read book

```

