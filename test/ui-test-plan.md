# UI Test Plan

These tests exercise Yappy's finalized console behavior through Week 6. Expected output blocks list fragments that must appear in order; the banner and divider lines may also appear in the actual console output. Each test case starts with an empty data file unless it explicitly restarts Yappy.

## Test Case 1: Add and List the Three Task Types

Aim: Verify that todos, deadlines, and events are added with the correct type icons and displayed by `list`.

Commands:
```text
todo borrow book
deadline return book /by 2026-08-30
event project meeting /from 2026-08-31 /to 2026-09-01
list
bye
```

Expected output fragments:
```text
Got it. I've added this task:
  [T][ ] borrow book
Now you have 1 task in the list.
Got it. I've added this task:
  [D][ ] return book (by: Aug 30 2026)
Now you have 2 tasks in the list.
Got it. I've added this task:
  [E][ ] project meeting (from: Aug 31 2026 to: Sep 01 2026)
Now you have 3 tasks in the list.
Here are the tasks in your list:
1.[T][ ] borrow book
2.[D][ ] return book (by: Aug 30 2026)
3.[E][ ] project meeting (from: Aug 31 2026 to: Sep 01 2026)
Bye. Hope to see you again soon!
```

## Test Case 2: Mark and Unmark Typed Tasks

Aim: Verify that inherited done-status behavior works for todo, deadline, and event subclasses.

Commands:
```text
todo read book
deadline submit report /by 2019-10-11
event orientation week /from 2019-10-04 /to 2019-10-11
mark 2
unmark 2
mark 3
list
bye
```

Expected output fragments:
```text
Nice! I've marked this task as done:
  [D][X] submit report (by: Oct 11 2019)
OK, I've marked this task as not done yet:
  [D][ ] submit report (by: Oct 11 2019)
Nice! I've marked this task as done:
  [E][X] orientation week (from: Oct 04 2019 to: Oct 11 2019)
Here are the tasks in your list:
1.[T][ ] read book
2.[D][ ] submit report (by: Oct 11 2019)
3.[E][X] orientation week (from: Oct 04 2019 to: Oct 11 2019)
Bye. Hope to see you again soon!
```

## Test Case 3: Parse and Format Deadline Dates

Aim: Verify that a valid ISO deadline date is stored as a date and displayed in a friendlier format.

Commands:
```text
deadline do homework /by 2019-12-02
list
bye
```

Expected output fragments:
```text
Got it. I've added this task:
  [D][ ] do homework (by: Dec 02 2019)
Now you have 1 task in the list.
Here are the tasks in your list:
1.[D][ ] do homework (by: Dec 02 2019)
Bye. Hope to see you again soon!
```

## Test Case 4: Reject Unknown and Empty Todo Inputs Without Changing State

Aim: Verify that empty commands, unknown commands, and empty todo descriptions report errors and do not add tasks.

Commands:
```text
todo keep state

todo
blah
list
bye
```

Expected output fragments:
```text
Got it. I've added this task:
  [T][ ] keep state
Now you have 1 task in the list.
OOPS!!! Please type a command.
OOPS!!! The description of a todo cannot be empty.
OOPS!!! I don't know what that means. Try todo, deadline, event, list, find, remind, mark, unmark, or delete.
Here are the tasks in your list:
1.[T][ ] keep state
Bye. Hope to see you again soon!
```

## Test Case 5: Reject Invalid Deadline and Event Inputs Without Changing State

Aim: Verify that missing or empty deadline/event fields report specific errors and only valid typed tasks are stored.

Commands:
```text
deadline /by 2026-08-29
deadline pay bills
deadline pay bills /by
deadline pay bills /by Friday
deadline pay bills /by 2026-02-30
deadline pay bills /by 2026-08-29
event /from 2026-08-30 /to 2026-08-31
event meeting /from /to 2026-08-31
event meeting /from 2026-08-30
event meeting /from 2026-08-30 /to
event meeting /from Monday /to 2026-08-31
event meeting /from 2026-08-30 /to Tuesday
event meeting /from 2026-08-31 /to 2026-08-30
event meeting /from 2026-08-30 /to 2026-08-31
list
bye
```

Expected output fragments:
```text
OOPS!!! The description of a deadline cannot be empty.
OOPS!!! Please use: deadline DESCRIPTION /by WHEN
OOPS!!! The /by value of a deadline cannot be empty.
OOPS!!! Please enter the /by date as yyyy-MM-dd, e.g. 2019-10-15.
OOPS!!! Please enter the /by date as yyyy-MM-dd, e.g. 2019-10-15.
Got it. I've added this task:
  [D][ ] pay bills (by: Aug 29 2026)
Now you have 1 task in the list.
OOPS!!! The description of an event cannot be empty.
OOPS!!! The /from value of an event cannot be empty.
OOPS!!! Please use: event DESCRIPTION /from START /to END
OOPS!!! The /to value of an event cannot be empty.
OOPS!!! Please enter the /from date as yyyy-MM-dd, e.g. 2019-10-15.
OOPS!!! Please enter the /to date as yyyy-MM-dd, e.g. 2019-10-15.
OOPS!!! An event's /to date cannot be before its /from date.
Got it. I've added this task:
  [E][ ] meeting (from: Aug 30 2026 to: Aug 31 2026)
Now you have 2 tasks in the list.
Here are the tasks in your list:
1.[D][ ] pay bills (by: Aug 29 2026)
2.[E][ ] meeting (from: Aug 30 2026 to: Aug 31 2026)
Bye. Hope to see you again soon!
```

## Test Case 6: Reject Invalid Mark and Unmark Inputs Without Changing State

Aim: Verify that missing, non-numeric, and out-of-range task numbers report errors and do not change task status.

Commands:
```text
mark 1
todo alpha
mark
mark two
mark 5
mark 1
unmark
unmark 0
unmark one
unmark 1
list
bye
```

Expected output fragments:
```text
OOPS!!! There are no tasks in the list yet.
Got it. I've added this task:
  [T][ ] alpha
Now you have 1 task in the list.
OOPS!!! Please tell me which task to mark, e.g. mark 1.
OOPS!!! Task numbers must be whole numbers.
OOPS!!! Task number must be between 1 and 1.
Nice! I've marked this task as done:
  [T][X] alpha
OOPS!!! Please tell me which task to unmark, e.g. unmark 1.
OOPS!!! Task number must be between 1 and 1.
OOPS!!! Task numbers must be whole numbers.
OK, I've marked this task as not done yet:
  [T][ ] alpha
Here are the tasks in your list:
1.[T][ ] alpha
Bye. Hope to see you again soon!
```

## Test Case 7: Delete Tasks and Renumber the List

Aim: Verify that deleting a task removes the correct item and the remaining tasks keep their relative order with updated numbering.

Commands:
```text
todo read book
deadline return book /by 2026-08-30
event project meeting /from 2026-08-31 /to 2026-09-01
todo borrow book
delete 3
list
delete 1
list
bye
```

Expected output fragments:
```text
Got it. I've added this task:
  [T][ ] read book
Got it. I've added this task:
  [D][ ] return book (by: Aug 30 2026)
Got it. I've added this task:
  [E][ ] project meeting (from: Aug 31 2026 to: Sep 01 2026)
Got it. I've added this task:
  [T][ ] borrow book
Noted. I've removed this task:
  [E][ ] project meeting (from: Aug 31 2026 to: Sep 01 2026)
Now you have 3 tasks in the list.
Here are the tasks in your list:
1.[T][ ] read book
2.[D][ ] return book (by: Aug 30 2026)
3.[T][ ] borrow book
Noted. I've removed this task:
  [T][ ] read book
Now you have 2 tasks in the list.
Here are the tasks in your list:
1.[D][ ] return book (by: Aug 30 2026)
2.[T][ ] borrow book
Bye. Hope to see you again soon!
```

## Test Case 8: Reject Invalid Delete Inputs Without Changing State

Aim: Verify that missing, non-numeric, empty-list, and out-of-range delete commands report errors and do not remove tasks.

Commands:
```text
delete 1
todo alpha
todo beta
delete
delete two
delete 0
delete 3
list
delete 2
list
bye
```

Expected output fragments:
```text
OOPS!!! There are no tasks in the list yet.
Got it. I've added this task:
  [T][ ] alpha
Got it. I've added this task:
  [T][ ] beta
OOPS!!! Please tell me which task to delete, e.g. delete 1.
OOPS!!! Task numbers must be whole numbers.
OOPS!!! Task number must be between 1 and 2.
OOPS!!! Task number must be between 1 and 2.
Here are the tasks in your list:
1.[T][ ] alpha
2.[T][ ] beta
Noted. I've removed this task:
  [T][ ] beta
Now you have 1 task in the list.
Here are the tasks in your list:
1.[T][ ] alpha
Bye. Hope to see you again soon!
```

## Test Case 9: Save and Reload Tasks

Aim: Verify that tasks and their completion status survive an application restart and remain editable after loading.

Commands:
```text
todo write report
deadline submit report /by 2026-08-29
mark 2
bye
```

Commands after restart:
```text
list
delete 1
bye
```

Expected output fragments:
```text
Got it. I've added this task:
  [T][ ] write report
Got it. I've added this task:
  [D][ ] submit report (by: Aug 29 2026)
Nice! I've marked this task as done:
  [D][X] submit report (by: Aug 29 2026)
Bye. Hope to see you again soon!
Here are the tasks in your list:
1.[T][ ] write report
2.[D][X] submit report (by: Aug 29 2026)
Noted. I've removed this task:
  [T][ ] write report
Now you have 1 task in the list.
Bye. Hope to see you again soon!
```

## Test Case 10: Find Tasks by Keyword

Aim: Verify that find matches descriptions case-insensitively and that a missing keyword reports an error without changing the task list.

Commands:
```text
todo Read Book
deadline return book /by 2026-08-31
todo buy groceries
find BOOK
find groceries
find
list
bye
```

Expected output fragments:
```text
Got it. I've added this task:
  [T][ ] Read Book
Got it. I've added this task:
  [D][ ] return book (by: Aug 31 2026)
Got it. I've added this task:
  [T][ ] buy groceries
Here are the matching tasks in your list:
1.[T][ ] Read Book
2.[D][ ] return book (by: Aug 31 2026)
Here are the matching tasks in your list:
1.[T][ ] buy groceries
OOPS!!! The keyword of a find command cannot be empty.
Here are the tasks in your list:
1.[T][ ] Read Book
2.[D][ ] return book (by: Aug 31 2026)
3.[T][ ] buy groceries
Bye. Hope to see you again soon!
```

## Test Case 11: Show Upcoming Deadline Reminders Without Changing State

Aim: Verify that reminders show only incomplete deadlines, reject invalid periods,
and do not modify the saved task list.

Commands:
```text
todo prepare slides
deadline submit report /by 2099-01-01
deadline completed deadline /by 2099-01-02
mark 3
remind
remind tomorrow
remind -1
remind 99999
list
bye
```

Expected output fragments:
```text
Got it. I've added this task:
  [T][ ] prepare slides
Got it. I've added this task:
  [D][ ] submit report (by: Jan 01 2099)
Got it. I've added this task:
  [D][ ] completed deadline (by: Jan 02 2099)
Nice! I've marked this task as done:
  [D][X] completed deadline (by: Jan 02 2099)
OOPS!!! Please tell me how many days ahead to check, e.g. remind 7.
OOPS!!! Reminder days must be a whole number.
OOPS!!! Reminder days cannot be negative.
Here are your incomplete deadlines due within 99999 day(s):
1.[D][ ] submit report (by: Jan 01 2099)
Here are the tasks in your list:
1.[T][ ] prepare slides
2.[D][ ] submit report (by: Jan 01 2099)
3.[D][X] completed deadline (by: Jan 02 2099)
Bye. Hope to see you again soon!
```

## Test Case 12: Handle Flexible Commands and Reject Ambiguous Formats Without Changing State

Aim: Verify that command words are case-insensitive and tolerate surrounding whitespace, while extra
arguments and duplicated or misordered date markers report specific errors without changing the task list.

Commands:
```text
  TODO    Prepare Demo
list later
deadline duplicate marker /by 2026-09-01 /by 2026-09-02
event duplicate marker /from 2026-09-01 /from 2026-09-02 /to 2026-09-03
event wrong order /to 2026-09-03 /from 2026-09-01
list
bye later
list
bye
```

Expected output fragments:
```text
Got it. I've added this task:
  [T][ ] Prepare Demo
Now you have 1 task in the list.
OOPS!!! The list command does not take extra arguments.
OOPS!!! Please specify /by only once.
OOPS!!! Please specify /from only once.
OOPS!!! Please put /from before /to in an event.
Here are the tasks in your list:
1.[T][ ] Prepare Demo
OOPS!!! The bye command does not take extra arguments.
Here are the tasks in your list:
1.[T][ ] Prepare Demo
Bye. Hope to see you again soon!
```
