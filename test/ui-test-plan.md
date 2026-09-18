# Furina UI test plan

The runner compares program output exactly. User-entered command lines are recorded as console input and are not expected in the program-output blocks because a terminal echoes them.

## Test case 1: Greeting and exit

Aim: Verify that Furina greets the user and exits after `bye`.

Input:

```text
bye
```

Expected output:

```text
____________________________________________________________
    F U R I N A
Hello! I'm Furina.
What can I do for you?
____________________________________________________________
____________________________________________________________
    Bye. Hope to see you again soon!
____________________________________________________________
```

## Test case 5: Invalid commands and empty descriptions

Aim: Verify that malformed task commands and unknown commands produce specific errors without crashing or adding invalid tasks.

Input:

```text
todo

blah
deadline return book
event project meeting /from Monday
mark nope
unmark 9
bye
```

Expected output:

```text
____________________________________________________________
    F U R I N A
Hello! I'm Furina.
What can I do for you?
____________________________________________________________
____________________________________________________________
    OOPS!!! A todo task needs a description.
____________________________________________________________
____________________________________________________________
    OOPS!!! I don't recognize that command.
____________________________________________________________
____________________________________________________________
    OOPS!!! A deadline needs a description and a date after /by.
____________________________________________________________
____________________________________________________________
    OOPS!!! An event needs a description, a start time after /from, and an end time after /to.
____________________________________________________________
____________________________________________________________
    OOPS!!! Task numbers must be positive whole numbers.
____________________________________________________________
____________________________________________________________
    OOPS!!! There is no task with that number.
____________________________________________________________
____________________________________________________________
    Bye. Hope to see you again soon!
____________________________________________________________
```

## Test case 2: Task types and listing

Aim: Verify todo, deadline, and event tasks retain their details and display correctly in a list.

Input:

```text
todo borrow book
deadline return book /by Sunday
event project meeting /from Mon 2pm /to 4pm
list
bye
```

Expected output:

```text
____________________________________________________________
    F U R I N A
Hello! I'm Furina.
What can I do for you?
____________________________________________________________
____________________________________________________________
    Got it. I've added this task:
      [T][ ] borrow book
    Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
    Got it. I've added this task:
      [D][ ] return book (by: Sunday)
    Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
    Got it. I've added this task:
      [E][ ] project meeting (from: Mon 2pm to: 4pm)
    Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
    Here are the tasks in your list:
    1.[T][ ] borrow book
    2.[D][ ] return book (by: Sunday)
    3.[E][ ] project meeting (from: Mon 2pm to: 4pm)
____________________________________________________________
____________________________________________________________
    Bye. Hope to see you again soon!
____________________________________________________________
```

## Test case 3: Mark and unmark

Aim: Verify that task completion can be set and reversed without changing the task description.

Input:

```text
todo read book
todo return book
mark 1
unmark 1
list
bye
```

Expected output:

```text
____________________________________________________________
    F U R I N A
Hello! I'm Furina.
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
    Nice! I've marked this task as done:
      [T][X] read book
____________________________________________________________
____________________________________________________________
    OK, I've marked this task as not done yet:
      [T][ ] read book
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

## Test case 6: Delete and reindex tasks

Aim: Verify that `delete N` removes the requested task, updates the count, and shifts later tasks down.

Input:

```text
todo read book
deadline return book /by June 6th
event project meeting /from Aug 6th 2pm /to 4pm
delete 2
list
delete 9
bye
```

Expected output:

```text
____________________________________________________________
    F U R I N A
Hello! I'm Furina.
What can I do for you?
____________________________________________________________
____________________________________________________________
    Got it. I've added this task:
      [T][ ] read book
    Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
    Got it. I've added this task:
      [D][ ] return book (by: June 6th)
    Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
    Got it. I've added this task:
      [E][ ] project meeting (from: Aug 6th 2pm to: 4pm)
    Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
    Noted. I've removed this task:
      [D][ ] return book (by: June 6th)
    Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
    Here are the tasks in your list:
    1.[T][ ] read book
    2.[E][ ] project meeting (from: Aug 6th 2pm to: 4pm)
____________________________________________________________
____________________________________________________________
    OOPS!!! There is no task with that number.
____________________________________________________________
____________________________________________________________
    Bye. Hope to see you again soon!
____________________________________________________________
```

## Test case 4: Blank input and invalid task number

Aim: Verify blank lines are ignored and invalid status commands do not crash the program.

Input:

```text

   
todo read book
mark nope
unmark 99
list
bye
```

Expected output:

```text
____________________________________________________________
    F U R I N A
Hello! I'm Furina.
What can I do for you?
____________________________________________________________
____________________________________________________________
    Got it. I've added this task:
      [T][ ] read book
    Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
    OOPS!!! Task numbers must be positive whole numbers.
____________________________________________________________
____________________________________________________________
    OOPS!!! There is no task with that number.
____________________________________________________________
____________________________________________________________
    Here are the tasks in your list:
    1.[T][ ] read book
____________________________________________________________
____________________________________________________________
    Bye. Hope to see you again soon!
____________________________________________________________
```

## Test case 7: Date-aware deadlines

Aim: Verify ISO and day/month deadlines are parsed and displayed as readable dates and times.

Input:

```text
deadline submit report /by 2019-10-15
deadline return book /by 2/12/2019 1800
list
bye
```

Expected output:

```text
____________________________________________________________
    F U R I N A
Hello! I'm Furina.
What can I do for you?
____________________________________________________________
____________________________________________________________
    Got it. I've added this task:
      [D][ ] submit report (by: Oct 15 2019)
    Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
    Got it. I've added this task:
      [D][ ] return book (by: Dec 2 2019 6:00 PM)
    Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
    Here are the tasks in your list:
    1.[D][ ] submit report (by: Oct 15 2019)
    2.[D][ ] return book (by: Dec 2 2019 6:00 PM)
____________________________________________________________
____________________________________________________________
    Bye. Hope to see you again soon!
____________________________________________________________
```

## Test case 8: Find tasks by keyword

Aim: Verify that `find` performs a case-insensitive search in task descriptions and preserves task list numbers.

Input:

```text
todo read book
deadline return book /by Sunday
event project meeting /from Mon 2pm /to 4pm
find BOOK
find dentist
find
bye
```

Expected output:

```text
____________________________________________________________
    F U R I N A
Hello! I'm Furina.
What can I do for you?
____________________________________________________________
____________________________________________________________
    Got it. I've added this task:
      [T][ ] read book
    Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
    Got it. I've added this task:
      [D][ ] return book (by: Sunday)
    Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
    Got it. I've added this task:
      [E][ ] project meeting (from: Mon 2pm to: 4pm)
    Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
    Here are the matching tasks in your list:
    1.[T][ ] read book
    2.[D][ ] return book (by: Sunday)
____________________________________________________________
____________________________________________________________
    Here are the matching tasks in your list:
    No matching tasks found.
____________________________________________________________
____________________________________________________________
    OOPS!!! Please provide a keyword to search for.
____________________________________________________________
____________________________________________________________
    Bye. Hope to see you again soon!
____________________________________________________________
```

## Test case 9: Sort tasks by deadline

Aim: Verify that `sort` orders dated deadlines chronologically and keeps tasks without dates after them.

Input:

```text
deadline later task /by 2026-09-20
todo no deadline
deadline earlier task /by 2026-09-10
sort
bye
```

Expected output:

```text
____________________________________________________________
    F U R I N A
Hello! I'm Furina.
What can I do for you?
____________________________________________________________
____________________________________________________________
    Got it. I've added this task:
      [D][ ] later task (by: Sep 20 2026)
    Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
    Got it. I've added this task:
      [T][ ] no deadline
    Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
    Got it. I've added this task:
      [D][ ] earlier task (by: Sep 10 2026)
    Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
    Sorted tasks by deadline:
    Here are the tasks in your list:
    1.[D][ ] earlier task (by: Sep 10 2026)
    2.[D][ ] later task (by: Sep 20 2026)
    3.[T][ ] no deadline
____________________________________________________________
____________________________________________________________
    Bye. Hope to see you again soon!
____________________________________________________________
```

## Test case 10: Invalid date and duplicate task handling

Aim: Verify that invalid date-like input and duplicate tasks are rejected without
crashing or adding a second copy.

Input:

```text
deadline submit report /by 2019-02-30
todo read book
todo read book
list
bye
```

Expected output:

```text
____________________________________________________________
    F U R I N A
Hello! I'm Furina.
What can I do for you?
____________________________________________________________
____________________________________________________________
    OOPS!!! Please provide a valid deadline date.
____________________________________________________________
____________________________________________________________
    Got it. I've added this task:
      [T][ ] read book
    Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
    OOPS!!! That task is already in your list.
____________________________________________________________
____________________________________________________________
    Here are the tasks in your list:
    1.[T][ ] read book
____________________________________________________________
____________________________________________________________
    Bye. Hope to see you again soon!
____________________________________________________________
```
