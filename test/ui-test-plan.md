# UI Test Plan

This plan is the source of truth for fail-fast console UI testing. Each test case runs in a fresh application process, and its expected output is compared exactly with stdout.

Each case uses an isolated working directory so saved tasks cannot leak into later cases. File writing, loading, and storage failures are checked separately by `test/test_storage.py`, because this plan compares one session's console output only.

## Configuration

- Source directory: `src/main/java`
- Classes directory: `out/production/ip`
- Main class: `goat.Goat`

## Test case: Exit immediately

### Aim

Verify that the application displays its greeting and exits cleanly when the user enters `bye`.

### Commands

```text
bye
```

### Expected output

```text
____________________________________________________________
  ____             _
 / ___| ___   __ _| |_
| |  _ / _ \ / _` | __|
| |_| | (_) | (_| | |_
 \____|\___/ \__,_|\__|
Hello! I'm Goat, your sure-footed task buddy.
Let's climb that task list together!
____________________________________________________________
____________________________________________________________
Time to hoof it. See you on the next climb!
____________________________________________________________
```

## Test case: Manage todos, deadlines, and events

### Aim

Verify all three task types, parsed date values, readable date formatting, polymorphic listing, and inherited completion status.

### Commands

```text
todo borrow book
deadline return book /by 2026-08-30
event project meeting /from 2026-09-01 /to 2026-09-02
deadline do homework /by 2026-10-15
mark 2
list
unmark 2
list
bye
```

### Expected output

```text
____________________________________________________________
  ____             _
 / ___| ___   __ _| |_
| |  _ / _ \ / _` | __|
| |_| | (_) | (_| | |_
 \____|\___/ \__,_|\__|
Hello! I'm Goat, your sure-footed task buddy.
Let's climb that task list together!
____________________________________________________________
____________________________________________________________
Hoof-tastic! I've added this task:
  [T][ ] borrow book
Now you have 1 task in the list.
____________________________________________________________
____________________________________________________________
Hoof-tastic! I've added this task:
  [D][ ] return book (by: Aug 30 2026)
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Hoof-tastic! I've added this task:
  [E][ ] project meeting (from: Sep 01 2026 to: Sep 02 2026)
Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
Hoof-tastic! I've added this task:
  [D][ ] do homework (by: Oct 15 2026)
Now you have 4 tasks in the list.
____________________________________________________________
____________________________________________________________
Baa-rilliant! I've marked this task as done:
  [D][X] return book (by: Aug 30 2026)
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] borrow book
2.[D][X] return book (by: Aug 30 2026)
3.[E][ ] project meeting (from: Sep 01 2026 to: Sep 02 2026)
4.[D][ ] do homework (by: Oct 15 2026)
____________________________________________________________
____________________________________________________________
Back on the trail! I've marked this task as not done yet:
  [D][ ] return book (by: Aug 30 2026)
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] borrow book
2.[D][ ] return book (by: Aug 30 2026)
3.[E][ ] project meeting (from: Sep 01 2026 to: Sep 02 2026)
4.[D][ ] do homework (by: Oct 15 2026)
____________________________________________________________
____________________________________________________________
Time to hoof it. See you on the next climb!
____________________________________________________________
```

## Test case: Delete tasks and preserve list state

### Aim

Verify deletion of middle, first, last, and sole tasks, including renumbering, relative order, and preservation of completion state.

### Commands

```text
todo first task
deadline second task /by 2026-09-04
event third task /from 2026-09-07 /to 2026-09-08
todo fourth task
mark 2
delete 3
list
delete 1
list
delete 2
delete 1
list
bye
```

### Expected output

```text
____________________________________________________________
  ____             _
 / ___| ___   __ _| |_
| |  _ / _ \ / _` | __|
| |_| | (_) | (_| | |_
 \____|\___/ \__,_|\__|
Hello! I'm Goat, your sure-footed task buddy.
Let's climb that task list together!
____________________________________________________________
____________________________________________________________
Hoof-tastic! I've added this task:
  [T][ ] first task
Now you have 1 task in the list.
____________________________________________________________
____________________________________________________________
Hoof-tastic! I've added this task:
  [D][ ] second task (by: Sep 04 2026)
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Hoof-tastic! I've added this task:
  [E][ ] third task (from: Sep 07 2026 to: Sep 08 2026)
Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
Hoof-tastic! I've added this task:
  [T][ ] fourth task
Now you have 4 tasks in the list.
____________________________________________________________
____________________________________________________________
Baa-rilliant! I've marked this task as done:
  [D][X] second task (by: Sep 04 2026)
____________________________________________________________
____________________________________________________________
One less climb! I've removed this task:
  [E][ ] third task (from: Sep 07 2026 to: Sep 08 2026)
Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] first task
2.[D][X] second task (by: Sep 04 2026)
3.[T][ ] fourth task
____________________________________________________________
____________________________________________________________
One less climb! I've removed this task:
  [T][ ] first task
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[D][X] second task (by: Sep 04 2026)
2.[T][ ] fourth task
____________________________________________________________
____________________________________________________________
One less climb! I've removed this task:
  [T][ ] fourth task
Now you have 1 task in the list.
____________________________________________________________
____________________________________________________________
One less climb! I've removed this task:
  [D][X] second task (by: Sep 04 2026)
Now you have 0 tasks in the list.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
____________________________________________________________
____________________________________________________________
Time to hoof it. See you on the next climb!
____________________________________________________________
```

## Test case: Find tasks by keyword

### Aim

Verify that `find` returns tasks whose descriptions contain a keyword, preserves match order and completion state, and handles no matches.

### Commands

```text
todo read book
deadline return book /by 2026-09-05
event book club /from 2026-09-06 /to 2026-09-07
todo finish homework
mark 1
find book
find homework
find missing
bye
```

### Expected output

```text
____________________________________________________________
  ____             _
 / ___| ___   __ _| |_
| |  _ / _ \ / _` | __|
| |_| | (_) | (_| | |_
 \____|\___/ \__,_|\__|
Hello! I'm Goat, your sure-footed task buddy.
Let's climb that task list together!
____________________________________________________________
____________________________________________________________
Hoof-tastic! I've added this task:
  [T][ ] read book
Now you have 1 task in the list.
____________________________________________________________
____________________________________________________________
Hoof-tastic! I've added this task:
  [D][ ] return book (by: Sep 05 2026)
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Hoof-tastic! I've added this task:
  [E][ ] book club (from: Sep 06 2026 to: Sep 07 2026)
Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
Hoof-tastic! I've added this task:
  [T][ ] finish homework
Now you have 4 tasks in the list.
____________________________________________________________
____________________________________________________________
Baa-rilliant! I've marked this task as done:
  [T][X] read book
____________________________________________________________
____________________________________________________________
Here are the matching tasks in your list:
1.[T][X] read book
2.[D][ ] return book (by: Sep 05 2026)
3.[E][ ] book club (from: Sep 06 2026 to: Sep 07 2026)
____________________________________________________________
____________________________________________________________
Here are the matching tasks in your list:
1.[T][ ] finish homework
____________________________________________________________
____________________________________________________________
Here are the matching tasks in your list:
____________________________________________________________
____________________________________________________________
Time to hoof it. See you on the next climb!
____________________________________________________________
```

## Test case: Sort tasks chronologically

### Aim

Verify that `sort` orders dated tasks chronologically, uses descriptions to break date ties, places todos last, and preserves completion state.

### Commands

```text
todo pack bag
deadline submit report /by 2026-10-15
event conference /from 2026-09-20 /to 2026-09-22
deadline apply /by 2026-09-20
mark 1
sort
bye
```

### Expected output

```text
____________________________________________________________
  ____             _
 / ___| ___   __ _| |_
| |  _ / _ \ / _` | __|
| |_| | (_) | (_| | |_
 \____|\___/ \__,_|\__|
Hello! I'm Goat, your sure-footed task buddy.
Let's climb that task list together!
____________________________________________________________
____________________________________________________________
Hoof-tastic! I've added this task:
  [T][ ] pack bag
Now you have 1 task in the list.
____________________________________________________________
____________________________________________________________
Hoof-tastic! I've added this task:
  [D][ ] submit report (by: Oct 15 2026)
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Hoof-tastic! I've added this task:
  [E][ ] conference (from: Sep 20 2026 to: Sep 22 2026)
Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
Hoof-tastic! I've added this task:
  [D][ ] apply (by: Sep 20 2026)
Now you have 4 tasks in the list.
____________________________________________________________
____________________________________________________________
Baa-rilliant! I've marked this task as done:
  [T][X] pack bag
____________________________________________________________
____________________________________________________________
All lined up! Dated tasks are chronological, followed by undated tasks:
1.[D][ ] apply (by: Sep 20 2026)
2.[E][ ] conference (from: Sep 20 2026 to: Sep 22 2026)
3.[D][ ] submit report (by: Oct 15 2026)
4.[T][X] pack bag
____________________________________________________________
____________________________________________________________
Time to hoof it. See you on the next climb!
____________________________________________________________
```

## Test case: Recover from incorrect inputs

### Aim

Verify that empty and unknown commands, incomplete or invalid task details, and invalid task numbers produce specific guidance without terminating the application or corrupting its task list.

### Commands

```text

mark 1
delete 1
todo
todo read book
deadline submit assignment
deadline /by 2026-09-05
deadline submit assignment /by
deadline submit assignment /by Friday
deadline submit assignment /by 2026-09-05 /by 2026-09-06
deadline submit assignment /by 2026-09-05
event meeting
event /from 2026-09-06 /to 2026-09-07
event meeting /from /to 2026-09-07
event meeting /from 2026-09-06 /to
event meeting /from Monday /to 2026-09-07
event meeting /from 2026-09-06 /to not-a-date
event meeting /from 2026-09-07 /to 2026-09-07
event meeting /from 2026-09-08 /to 2026-09-07
event meeting /from 2026-09-06 /from 2026-09-07 /to 2026-09-08
event meeting /from 2026-09-06 /to 2026-09-07
mark
mark two
mark 0
mark 4
delete
delete two
delete 0
delete 4
mark 1
unmark 1
find
blah
list
bye
```

### Expected output

```text
____________________________________________________________
  ____             _
 / ___| ___   __ _| |_
| |  _ / _ \ / _` | __|
| |_| | (_) | (_| | |_
 \____|\___/ \__,_|\__|
Hello! I'm Goat, your sure-footed task buddy.
Let's climb that task list together!
____________________________________________________________
____________________________________________________________
OOPS!!! Please enter a command.
____________________________________________________________
____________________________________________________________
OOPS!!! There are no tasks in the list yet.
____________________________________________________________
____________________________________________________________
OOPS!!! There are no tasks in the list yet.
____________________________________________________________
____________________________________________________________
OOPS!!! A todo needs a description. Try: todo <description>
____________________________________________________________
____________________________________________________________
Hoof-tastic! I've added this task:
  [T][ ] read book
Now you have 1 task in the list.
____________________________________________________________
____________________________________________________________
OOPS!!! Use exactly one /by: deadline <description> /by <yyyy-MM-dd>
____________________________________________________________
____________________________________________________________
OOPS!!! A deadline needs a description before /by.
____________________________________________________________
____________________________________________________________
OOPS!!! A deadline needs a date after /by.
____________________________________________________________
____________________________________________________________
OOPS!!! The deadline date must be a valid date in yyyy-MM-dd format.
____________________________________________________________
____________________________________________________________
OOPS!!! Use exactly one /by: deadline <description> /by <yyyy-MM-dd>
____________________________________________________________
____________________________________________________________
Hoof-tastic! I've added this task:
  [D][ ] submit assignment (by: Sep 05 2026)
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
OOPS!!! Use exactly one /from and one /to: event <description> /from <start date> /to <end date>
____________________________________________________________
____________________________________________________________
OOPS!!! An event needs a description before /from.
____________________________________________________________
____________________________________________________________
OOPS!!! An event needs a start date after /from.
____________________________________________________________
____________________________________________________________
OOPS!!! An event needs an end date after /to.
____________________________________________________________
____________________________________________________________
OOPS!!! The event start date must be a valid date in yyyy-MM-dd format.
____________________________________________________________
____________________________________________________________
OOPS!!! The event end date must be a valid date in yyyy-MM-dd format.
____________________________________________________________
____________________________________________________________
OOPS!!! An event must start before it ends.
____________________________________________________________
____________________________________________________________
OOPS!!! An event must start before it ends.
____________________________________________________________
____________________________________________________________
OOPS!!! Use exactly one /from and one /to: event <description> /from <start date> /to <end date>
____________________________________________________________
____________________________________________________________
Hoof-tastic! I've added this task:
  [E][ ] meeting (from: Sep 06 2026 to: Sep 07 2026)
Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
OOPS!!! Please specify a task number. Try: mark <task number>
____________________________________________________________
____________________________________________________________
OOPS!!! The task number must be a positive whole number.
____________________________________________________________
____________________________________________________________
OOPS!!! Task 0 does not exist. Choose a number from 1 to 3.
____________________________________________________________
____________________________________________________________
OOPS!!! Task 4 does not exist. Choose a number from 1 to 3.
____________________________________________________________
____________________________________________________________
OOPS!!! Please specify a task number. Try: delete <task number>
____________________________________________________________
____________________________________________________________
OOPS!!! The task number must be a positive whole number.
____________________________________________________________
____________________________________________________________
OOPS!!! Task 0 does not exist. Choose a number from 1 to 3.
____________________________________________________________
____________________________________________________________
OOPS!!! Task 4 does not exist. Choose a number from 1 to 3.
____________________________________________________________
____________________________________________________________
Baa-rilliant! I've marked this task as done:
  [T][X] read book
____________________________________________________________
____________________________________________________________
Back on the trail! I've marked this task as not done yet:
  [T][ ] read book
____________________________________________________________
____________________________________________________________
OOPS!!! Please specify a keyword. Try: find <keyword>
____________________________________________________________
____________________________________________________________
OOPS!!! I don't recognise that command. Try todo, deadline, event, list, find, sort, mark, unmark, delete, or bye.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] read book
2.[D][ ] submit assignment (by: Sep 05 2026)
3.[E][ ] meeting (from: Sep 06 2026 to: Sep 07 2026)
____________________________________________________________
____________________________________________________________
Time to hoof it. See you on the next climb!
____________________________________________________________
```
