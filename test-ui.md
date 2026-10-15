# Serina UI Test Cases

This file records console-based test cases for Serina after Levels 1-9, A-enums, and storage features. Each test
lists the user input sequence and the key output that should appear.

## How To Use

Compile and run Serina, then enter the inputs shown under each test case.

From the project root, the terminal commands are:

```bash
javac -d out $(find src/main/java -name "*.java")
java -cp out serina.Serina
```

The exact divider lines are omitted below to keep the tests readable. The messages should still appear inside Serina's usual response boxes.

## Level 1 (Echo)

### Test 1.1 - Greet And Exit

Input:

```text
bye
```

Expected key output:

```text
Hello! I'm Serina
What can I do for you?
Type help to see the available commands.
Bye. Hope to see you again soon!
```

Note: The early echo behavior was later replaced by task-specific command parsing. After Level 5, unknown commands are handled as errors instead of being echoed or added.

## Level 2 (Add, List)

### Test 2.1 - Add Todo Tasks And List Them

Input:

```text
todo read book
todo return book
list
bye
```

Expected key output:

```text
Got it. I've added this task:
  [T][ ] read book
Now you have 1 tasks in the list.
Got it. I've added this task:
  [T][ ] return book
Now you have 2 tasks in the list.
Here are the tasks in your list:
1.[T][ ] read book
2.[T][ ] return book
Bye. Hope to see you again soon!
```

### Test 2.2 - List When There Are No Tasks

Input:

```text
list
bye
```

Expected key output:

```text
Here are the tasks in your list:
Bye. Hope to see you again soon!
```

### Test 2.3 - Maximum Task Limit

Input:

```text
todo task 1
todo task 2
...
todo task 100
todo task 101
```

Expected key output:

```text
Got it. I've added this task:
  [T][ ] task 100
Now you have 100 tasks in the list.
You've reached the maximum number of tasks.
Bye. Hope to see you again soon!
```

## Level 3 (Mark as Done)

### Test 3.1 - Mark And Unmark A Task

Input:

```text
todo read book
todo return book
mark 2
unmark 2
list
bye
```

Expected key output:

```text
Nice! I've marked this task as done:
  [T][X] return book
OK, I've marked this task as not done yet:
  [T][ ] return book
Here are the tasks in your list:
1.[T][ ] read book
2.[T][ ] return book
Bye. Hope to see you again soon!
```

### Test 3.2 - Repeated Mark And Unmark

Input:

```text
todo read book
mark 1
mark 1
unmark 1
unmark 1
bye
```

Expected key output:

```text
Nice! I've marked this task as done:
  [T][X] read book
Nice! I've marked this task as done:
  [T][X] read book
OK, I've marked this task as not done yet:
  [T][ ] read book
OK, I've marked this task as not done yet:
  [T][ ] read book
Bye. Hope to see you again soon!
```

## Level 4 (ToDo, Event, DeadLine)

### Test 4.1 - Add All Three Task Types

Input:

```text
todo borrow book
deadline return book /by 2019-10-15
event project meeting /from 2019-10-16 /to 2019-10-17
list
bye
```

Expected key output:

```text
Got it. I've added this task:
  [T][ ] borrow book
Now you have 1 tasks in the list.
Got it. I've added this task:
  [D][ ] return book (by: Oct 15 2019)
Now you have 2 tasks in the list.
Got it. I've added this task:
  [E][ ] project meeting (from: Oct 16 2019 to: Oct 17 2019)
Now you have 3 tasks in the list.
Here are the tasks in your list:
1.[T][ ] borrow book
2.[D][ ] return book (by: Oct 15 2019)
3.[E][ ] project meeting (from: Oct 16 2019 to: Oct 17 2019)
Bye. Hope to see you again soon!
```

### Test 4.2 - Deadline Displays Formatted Date

Input:

```text
deadline do homework /by 2019-12-02
list
bye
```

Expected key output:

```text
Got it. I've added this task:
  [D][ ] do homework (by: Dec 2 2019)
Now you have 1 tasks in the list.
Here are the tasks in your list:
1.[D][ ] do homework (by: Dec 2 2019)
Bye. Hope to see you again soon!
```

### Test 4.3 - Mark Deadline And Event Tasks

Input:

```text
deadline return book /by 2019-10-15
event project meeting /from 2019-10-16 /to 2019-10-17
mark 1
mark 2
list
bye
```

Expected key output:

```text
Nice! I've marked this task as done:
  [D][X] return book (by: Oct 15 2019)
Nice! I've marked this task as done:
  [E][X] project meeting (from: Oct 16 2019 to: Oct 17 2019)
Here are the tasks in your list:
1.[D][X] return book (by: Oct 15 2019)
2.[E][X] project meeting (from: Oct 16 2019 to: Oct 17 2019)
Bye. Hope to see you again soon!
```

## Level 5 (Handle Errors)

### Test 5.1 - Empty Todo Description

Input:

```text
todo
bye
```

Expected key output:

```text
Sorry captain, todo descriptions can't be empty.
Bye. Hope to see you again soon!
```

### Test 5.2 - Unknown Command

Input:

```text
blah
bye
```

Expected key output:

```text
Sorry captain, could you rephrase that for me? Type help to see the available commands.
Bye. Hope to see you again soon!
```

### Test 5.3 - Invalid Deadline Commands

Input:

```text
deadline
deadline return book /by
deadline /by 2019-10-15
bye
```

Expected key output:

```text
Sorry captain, please use: deadline <task> /by <date>
Sorry captain, deadlines need a /by date.
Sorry captain, deadline descriptions can't be empty.
Bye. Hope to see you again soon!
```

### Test 5.4 - Invalid Event Commands

Input:

```text
event
event meeting /from 2019-10-15
event /from 2019-10-15 /to 2019-10-16
event meeting /from /to 2019-10-16
event meeting /from 2019-10-15 /to
bye
```

Expected key output:

```text
Sorry captain, please use: event <task> /from <start date> /to <end date>
Sorry captain, please use: event <task> /from <start date> /to <end date>
Sorry captain, event descriptions can't be empty.
Sorry captain, events need a /from date.
Sorry captain, events need a /to date.
Bye. Hope to see you again soon!
```

### Test 5.5 - Invalid Mark And Unmark Commands

Input:

```text
mark
mark abc
unmark 1
todo read book
mark 2
bye
```

Expected key output:

```text
Sorry captain, please provide a valid task number.
Sorry captain, please provide a valid task number.
Sorry captain, please provide a valid task number.
Got it. I've added this task:
  [T][ ] read book
Now you have 1 tasks in the list.
Sorry captain, please provide a valid task number.
Bye. Hope to see you again soon!
```

## Level 6 (Delete)

### Test 6.1 - Delete A Task From The Middle

Input:

```text
todo read book
deadline return book /by 2019-06-06
event project meeting /from 2019-08-06 /to 2019-08-06
todo join sports club
todo borrow book
mark 1
mark 2
mark 4
delete 3
list
bye
```

Expected key output:

```text
Noted. I've removed this task:
  [E][ ] project meeting (from: Aug 6 2019 to: Aug 6 2019)
Now you have 4 tasks in the list.
Here are the tasks in your list:
1.[T][X] read book
2.[D][X] return book (by: Jun 6 2019)
3.[T][X] join sports club
4.[T][ ] borrow book
Bye. Hope to see you again soon!
```

### Test 6.2 - Delete First And Last Tasks

Input:

```text
todo first task
todo second task
todo third task
delete 1
delete 2
list
bye
```

Expected key output:

```text
Noted. I've removed this task:
  [T][ ] first task
Now you have 2 tasks in the list.
Noted. I've removed this task:
  [T][ ] third task
Now you have 1 tasks in the list.
Here are the tasks in your list:
1.[T][ ] second task
Bye. Hope to see you again soon!
```

### Test 6.3 - Invalid Delete Commands

Input:

```text
delete
delete abc
delete 1
todo read book
delete 2
bye
```

Expected key output:

```text
Sorry captain, please provide a valid task number.
Sorry captain, please provide a valid task number.
Sorry captain, please provide a valid task number.
Got it. I've added this task:
  [T][ ] read book
Now you have 1 tasks in the list.
Sorry captain, please provide a valid task number.
Bye. Hope to see you again soon!
```

## Level A-enums

### Test A.1 - Enum-Backed Task Type And Status Display

Input:

```text
todo enum todo
deadline enum deadline /by 2019-07-05
event enum event /from 2019-07-06 /to 2019-07-07
mark 1
mark 2
mark 3
list
bye
```

Expected key output:

```text
Here are the tasks in your list:
1.[T][X] enum todo
2.[D][X] enum deadline (by: Jul 5 2019)
3.[E][X] enum event (from: Jul 6 2019 to: Jul 7 2019)
Bye. Hope to see you again soon!
```

### Test A.2 - Enum-Backed Error Messages Continue Normally

Input:

```text
todo
blah
todo recovered task
list
bye
```

Expected key output:

```text
Sorry captain, todo descriptions can't be empty.
Sorry captain, could you rephrase that for me? Type help to see the available commands.
Got it. I've added this task:
  [T][ ] recovered task
Now you have 1 tasks in the list.
Here are the tasks in your list:
1.[T][ ] recovered task
Bye. Hope to see you again soon!
```

### Test A.3 - Enum-Backed Exit Error Still Says Bye

Input:

```text
todo task 1
todo task 2
...
todo task 100
todo task 101
```

Expected key output:

```text
Got it. I've added this task:
  [T][ ] task 100
Now you have 100 tasks in the list.
You've reached the maximum number of tasks.
Bye. Hope to see you again soon!
```

## Level 7 (Save)

### Test 7.1 - Save Added Tasks To Disk

Input:

```text
todo read book
deadline return book /by 2019-06-06
event project meeting /from 2019-08-06 /to 2019-08-06
bye
```

Expected key output:

```text
Got it. I've added this task:
  [T][ ] read book
Got it. I've added this task:
  [D][ ] return book (by: Jun 6 2019)
Got it. I've added this task:
  [E][ ] project meeting (from: Aug 6 2019 to: Aug 6 2019)
Bye. Hope to see you again soon!
```

Expected save file at `data/serina.txt`:

```text
T | 0 | read book
D | 0 | return book | 2019-06-06
E | 0 | project meeting | 2019-08-06 | 2019-08-06
```

### Test 7.2 - Save Marked And Deleted Task Changes To Disk

Input:

```text
todo read book
todo return book
mark 1
delete 2
bye
```

Expected key output:

```text
Nice! I've marked this task as done:
  [T][X] read book
Noted. I've removed this task:
  [T][ ] return book
Bye. Hope to see you again soon!
```

Expected save file at `data/serina.txt`:

```text
T | 1 | read book
```

## Storage (Load)

### Test Load.1 - Load Saved Tasks On Startup

Starting save file at `data/serina.txt`:

```text
T | 1 | read book
D | 0 | return book | 2019-06-06
E | 0 | project meeting | 2019-08-06 | 2019-08-06
```

Input:

```text
list
bye
```

Expected key output:

```text
Here are the tasks in your list:
1.[T][X] read book
2.[D][ ] return book (by: Jun 6 2019)
3.[E][ ] project meeting (from: Aug 6 2019 to: Aug 6 2019)
Bye. Hope to see you again soon!
```

### Test Load.2 - Loaded Tasks Can Still Be Updated And Saved

Starting save file at `data/serina.txt`:

```text
T | 0 | read book
D | 0 | return book | 2019-06-06
```

Input:

```text
mark 2
delete 1
bye
```

Expected key output:

```text
Nice! I've marked this task as done:
  [D][X] return book (by: Jun 6 2019)
Noted. I've removed this task:
  [T][ ] read book
Bye. Hope to see you again soon!
```

Expected save file at `data/serina.txt`:

```text
D | 1 | return book | 2019-06-06
```

## Storage (Edge Cases)

### Test Storage.1 - Save And Load Text Containing File Delimiters

Input:

```text
todo read | review notes
deadline submit \ draft /by 2019-05-24
bye
```

Expected key output:

```text
Got it. I've added this task:
  [T][ ] read | review notes
Got it. I've added this task:
  [D][ ] submit \ draft (by: May 24 2019)
Bye. Hope to see you again soon!
```

Expected save file at `data/serina.txt`:

```text
T | 0 | read \| review notes
D | 0 | submit \\ draft | 2019-05-24
```

Follow-up input using the same save file:

```text
list
bye
```

Expected key output:

```text
Here are the tasks in your list:
1.[T][ ] read | review notes
2.[D][ ] submit \ draft (by: May 24 2019)
Bye. Hope to see you again soon!
```

### Test Storage.2 - Ignore Blank Lines In Save File

Starting save file at `data/serina.txt`:

```text

T | 0 | read book

D | 1 | return book | 2019-06-06

```

Input:

```text
list
bye
```

Expected key output:

```text
Here are the tasks in your list:
1.[T][ ] read book
2.[D][X] return book (by: Jun 6 2019)
Bye. Hope to see you again soon!
```

### Test Storage.3 - Handle Malformed Save File Gracefully

Starting save file at `data/serina.txt`:

```text
X | 0 | unknown type task
```

Input:

```text
list
bye
```

Expected key output:

```text
Hello! I'm Serina
What can I do for you?
Type help to see the available commands.
Sorry captain, I couldn't load your saved tasks.
Here are the tasks in your list:
Bye. Hope to see you again soon!
```

### Test Storage.4 - Handle Too Many Saved Tasks Gracefully

Starting save file at `data/serina.txt`:

```text
T | 0 | saved task 1
T | 0 | saved task 2
...
T | 0 | saved task 101
```

Input:

```text
list
bye
```

Expected key output:

```text
Hello! I'm Serina
What can I do for you?
Type help to see the available commands.
Sorry captain, I found more than 100 saved tasks.
Here are the tasks in your list:
Bye. Hope to see you again soon!
```

### Test Storage.5 - Start Normally When Data File And Folder Do Not Exist

Starting state:

```text
No data folder and no data/serina.txt file.
```

Input:

```text
list
todo first run task
bye
```

Expected key output:

```text
Hello! I'm Serina
What can I do for you?
Type help to see the available commands.
Here are the tasks in your list:
Got it. I've added this task:
  [T][ ] first run task
Bye. Hope to see you again soon!
```

Expected save file at `data/serina.txt`:

```text
T | 0 | first run task
```

## Level 8 (Dates and Times)

### Test 8.1 - Store And Display Parsed Dates

Input:

```text
deadline return book /by 2019-12-02
event orientation week /from 2019-10-04 /to 2019-10-11
list
bye
```

Expected key output:

```text
Got it. I've added this task:
  [D][ ] return book (by: Dec 2 2019)
Got it. I've added this task:
  [E][ ] orientation week (from: Oct 4 2019 to: Oct 11 2019)
Here are the tasks in your list:
1.[D][ ] return book (by: Dec 2 2019)
2.[E][ ] orientation week (from: Oct 4 2019 to: Oct 11 2019)
Bye. Hope to see you again soon!
```

Expected save file at `data/serina.txt`:

```text
D | 0 | return book | 2019-12-02
E | 0 | orientation week | 2019-10-04 | 2019-10-11
```

### Test 8.2 - Reject Invalid Date Inputs

Input:

```text
deadline return book /by 2/12/2019 1800
event meeting /from 2019-10-20 /to 2019-10-19
bye
```

Expected key output:

```text
Sorry captain, dates must be in yyyy-MM-dd format.
Sorry captain, event end date can't be before its start date.
Bye. Hope to see you again soon!
```

## Level 9 (Find)

### Test 9.1 - Find Matching Tasks By Keyword

Input:

```text
todo read book
deadline return book /by 2019-12-02
event project meeting /from 2019-12-01 /to 2019-12-03
mark 1
mark 2
find book
bye
```

Expected key output:

```text
Here are the matching tasks in your list:
1.[T][X] read book
2.[D][X] return book (by: Dec 2 2019)
Bye. Hope to see you again soon!
```

### Test 9.2 - Find Is Case-Insensitive And Supports Partial Keywords

Input:

```text
todo Read Textbook
todo buy bread
find BOOK
bye
```

Expected key output:

```text
Here are the matching tasks in your list:
1.[T][ ] Read Textbook
Bye. Hope to see you again soon!
```

### Test 9.3 - Find With No Matches

Input:

```text
todo read book
find exercise
bye
```

Expected key output:

```text
Here are the matching tasks in your list:
Bye. Hope to see you again soon!
```

### Test 9.4 - Reject Empty Find Keyword

Input:

```text
find
bye
```

Expected key output:

```text
Sorry captain, find needs a keyword.
Bye. Hope to see you again soon!
```

## Help Command

### Test Help.1 - Show Available Commands

Input:

```text
help
bye
```

Expected key output:

```text
Here are the commands I can respond to:
help - show this command list
todo <task> - add a todo
deadline <task> /by <yyyy-MM-dd> - add a deadline
event <task> /from <yyyy-MM-dd> /to <yyyy-MM-dd> - add an event
list - show all tasks
mark <number> - mark a task as done
unmark <number> - mark a task as not done
delete <number> - remove a task
find <keyword> - find tasks containing a keyword
bye - exit Serina
Bye. Hope to see you again soon!
```
