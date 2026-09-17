# Botavius UI test plan

Each test starts a fresh application session.

## Test case: add and list a task

Aim: Verify that a normal command is added as a task and that `list` displays it.

Input:

```text
borrow book
list
bye
```

Expected output:

```text
Got it. I've added this task:
[T][ ] borrow book
Now you have 1 task in the list.
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] borrow book
____________________________________________________________
Bye. Hope to see you again soon!
```

## Test case: find a task

Aim: Verify that `find` displays only tasks containing the supplied text.

Input:

```text
borrow book
buy milk
find book
bye
```

Expected output:

```text
Here are the matching tasks in your list:
1: [ ] borrow book
```

## Test case: add a deadline task

Aim: Verify that a deadline command stores and displays its deadline.

Input:

```text
deadline return book /by 31-12-2026 23:59
bye
```

Expected output:

```text
Got it. I've added this task:
[D][ ] return book (by: Dec 31 2026 11:59PM)
Now you have 1 task in the list.
____________________________________________________________
Bye. Hope to see you again soon!
```

## Test case: add an event task

Aim: Verify that an event command stores and displays its start and end times.

Input:

```text
event project meeting /from 31-12-2026 14:00 /to 31-12-2026 16:00
bye
```

## Test case: add a do-after task

Aim: Verify that a do-after command stores and displays the earliest time at
which the task can be done.

Input:

```text
doafter call client /after 31-12-2026 14:00
bye
```

Expected output:

```text
Got it. I've added this task:
[A][ ] call client (after: 31-Dec-26 02:00 pm)
Now you have 1 tasks in the list.
____________________________________________________________
Bye. Hope to see you again soon!
```

## Test case: reject an unknown GUI command

Aim: Verify that an unrecognized command entered in the GUI command bar is
reported as an error and is not added as a to-do task.

Input in the GUI command bar:

```text
buy milk
```

Expected status:

```text
ERROR // bad command issued.
```

The task list remains unchanged.

Expected output:

```text
Got it. I've added this task:
[E][ ] project meeting (from: Dec 31 2026 02:00PM to: Dec 31 2026 04:00PM)
Now you have 1 task in the list.
____________________________________________________________
Bye. Hope to see you again soon!
```
