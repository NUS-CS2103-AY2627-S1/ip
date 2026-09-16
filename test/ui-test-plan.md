# UI test plan

Run this plan with:

```text
python .codex/skills/test-ui/scripts/run_ui_tests.py
```

Each expected-output block contains the complete console output for its input.

## Test Case 1: Mark and unmark a to-do task

### Aim

Verify that a new to-do task is shown as incomplete, can be marked done, and
can be changed back to incomplete.

### Input

```text
todo read book
mark 1
unmark 1
list
bye
```

### Expected Output

```text
____________________________________________________________
 ____   ____  ____  ____  __   __
| __ ) / __ \| __ )| __ ) \ \ / /
|  _ \| |  | |  _ \|  _ \  \ V /
| |_) | |__| | |_) | |_) |   | |
|____/ \____/|____/|____/    |_|
Hiya! I'm Bobby, your cheerfully goofy task buddy.
Throw me a task and I'll keep it from wandering off!
____________________________________________________________
____________________________________________________________
Boop! I've tucked this task into the list:
[T][ ] read book
Bobby's task count is now 1.
____________________________________________________________
____________________________________________________________
Woohoo! This task is officially done:
  [T][X] read book
____________________________________________________________
____________________________________________________________
Plot twist! This task is back in action:
  [T][ ] read book
____________________________________________________________
____________________________________________________________
Ta-da! Here's your task parade:
1.[T][ ] read book
____________________________________________________________
____________________________________________________________
Toodles! Bobby is off to recharge the silly batteries.
____________________________________________________________
```

## Test Case 2: Add and list a to-do task

### Aim

Verify that the `todo` command creates a task without a date or time and that
the task is displayed with the `[T]` type marker.

### Input

```text
todo borrow book
list
bye
```

### Expected Output

```text
____________________________________________________________
 ____   ____  ____  ____  __   __
| __ ) / __ \| __ )| __ ) \ \ / /
|  _ \| |  | |  _ \|  _ \  \ V /
| |_) | |__| | |_) | |_) |   | |
|____/ \____/|____/|____/    |_|
Hiya! I'm Bobby, your cheerfully goofy task buddy.
Throw me a task and I'll keep it from wandering off!
____________________________________________________________
____________________________________________________________
Boop! I've tucked this task into the list:
[T][ ] borrow book
Bobby's task count is now 1.
____________________________________________________________
____________________________________________________________
Ta-da! Here's your task parade:
1.[T][ ] borrow book
____________________________________________________________
____________________________________________________________
Toodles! Bobby is off to recharge the silly batteries.
____________________________________________________________
```

## Test Case 3: Add and list a deadline task

### Aim

Verify that the `deadline` command parses an ISO date, stores it as a typed
date, and displays it in a human-friendly format.

### Input

```text
deadline return book /by 2019-10-15
list
bye
```

### Expected Output

```text
____________________________________________________________
 ____   ____  ____  ____  __   __
| __ ) / __ \| __ )| __ ) \ \ / /
|  _ \| |  | |  _ \|  _ \  \ V /
| |_) | |__| | |_) | |_) |   | |
|____/ \____/|____/|____/    |_|
Hiya! I'm Bobby, your cheerfully goofy task buddy.
Throw me a task and I'll keep it from wandering off!
____________________________________________________________
____________________________________________________________
Boop! I've tucked this task into the list:
[D][ ] return book (by: Oct 15 2019)
Bobby's task count is now 1.
____________________________________________________________
____________________________________________________________
Ta-da! Here's your task parade:
1.[D][ ] return book (by: Oct 15 2019)
____________________________________________________________
____________________________________________________________
Toodles! Bobby is off to recharge the silly batteries.
____________________________________________________________
```

## Test Case 4: Add and list an event task

### Aim

Verify that the `event` command parses start and end ISO dates and displays the
event with the `[E]` type marker.

### Input

```text
event project meeting /from 2019-10-15 /to 2019-10-16
list
bye
```

### Expected Output

```text
____________________________________________________________
 ____   ____  ____  ____  __   __
| __ ) / __ \| __ )| __ ) \ \ / /
|  _ \| |  | |  _ \|  _ \  \ V /
| |_) | |__| | |_) | |_) |   | |
|____/ \____/|____/|____/    |_|
Hiya! I'm Bobby, your cheerfully goofy task buddy.
Throw me a task and I'll keep it from wandering off!
____________________________________________________________
____________________________________________________________
Boop! I've tucked this task into the list:
[E][ ] project meeting (from: Oct 15 2019 to: Oct 16 2019)
Bobby's task count is now 1.
____________________________________________________________
____________________________________________________________
Ta-da! Here's your task parade:
1.[E][ ] project meeting (from: Oct 15 2019 to: Oct 16 2019)
____________________________________________________________
____________________________________________________________
Toodles! Bobby is off to recharge the silly batteries.
____________________________________________________________
```

## Test Case 5: Reject an unknown command

### Aim

Verify that input that does not begin with a supported command is rejected
with a helpful error message and is not added as a task.

### Input

```text
blah
bye
```

### Expected Output

```text
____________________________________________________________
 ____   ____  ____  ____  __   __
| __ ) / __ \| __ )| __ ) \ \ / /
|  _ \| |  | |  _ \|  _ \  \ V /
| |_) | |__| | |_) | |_) |   | |
|____/ \____/|____/|____/    |_|
Hiya! I'm Bobby, your cheerfully goofy task buddy.
Throw me a task and I'll keep it from wandering off!
____________________________________________________________
____________________________________________________________
No such task type available. Try again!
____________________________________________________________
____________________________________________________________
Toodles! Bobby is off to recharge the silly batteries.
____________________________________________________________
```

## Test Case 6: Reject a to-do without a description

### Aim

Verify that the `todo` task type is accepted but an empty description is
rejected with a specific error message.

### Input

```text
todo
bye
```

### Expected Output

```text
____________________________________________________________
 ____   ____  ____  ____  __   __
| __ ) / __ \| __ )| __ ) \ \ / /
|  _ \| |  | |  _ \|  _ \  \ V /
| |_) | |__| | |_) | |_) |   | |
|____/ \____/|____/|____/    |_|
Hiya! I'm Bobby, your cheerfully goofy task buddy.
Throw me a task and I'll keep it from wandering off!
____________________________________________________________
____________________________________________________________
Error! The description of a todo cannot be empty!
____________________________________________________________
____________________________________________________________
Toodles! Bobby is off to recharge the silly batteries.
____________________________________________________________
```

## Test Case 7: Reject a deadline without a description

### Aim

Verify that a deadline with no description is rejected without causing the
program to crash.

### Input

```text
deadline /by Sunday
bye
```

### Expected Output

```text
____________________________________________________________
 ____   ____  ____  ____  __   __
| __ ) / __ \| __ )| __ ) \ \ / /
|  _ \| |  | |  _ \|  _ \  \ V /
| |_) | |__| | |_) | |_) |   | |
|____/ \____/|____/|____/    |_|
Hiya! I'm Bobby, your cheerfully goofy task buddy.
Throw me a task and I'll keep it from wandering off!
____________________________________________________________
____________________________________________________________
Error! The description of a deadline cannot be empty!
____________________________________________________________
____________________________________________________________
Toodles! Bobby is off to recharge the silly batteries.
____________________________________________________________
```

## Test Case 8: Reject a deadline without a date

### Aim

Verify that a deadline with no date after `/by` is rejected without causing
the program to crash.

### Input

```text
deadline return book /by
bye
```

### Expected Output

```text
____________________________________________________________
 ____   ____  ____  ____  __   __
| __ ) / __ \| __ )| __ ) \ \ / /
|  _ \| |  | |  _ \|  _ \  \ V /
| |_) | |__| | |_) | |_) |   | |
|____/ \____/|____/|____/    |_|
Hiya! I'm Bobby, your cheerfully goofy task buddy.
Throw me a task and I'll keep it from wandering off!
____________________________________________________________
____________________________________________________________
Error! The date of a deadline cannot be empty!
____________________________________________________________
____________________________________________________________
Toodles! Bobby is off to recharge the silly batteries.
____________________________________________________________
```

## Test Case 9: Reject an event without a description

### Aim

Verify that an event with no description is rejected without causing the
program to crash.

### Input

```text
event /from 2019-10-15 /to 2019-10-16
bye
```

### Expected Output

```text
____________________________________________________________
 ____   ____  ____  ____  __   __
| __ ) / __ \| __ )| __ ) \ \ / /
|  _ \| |  | |  _ \|  _ \  \ V /
| |_) | |__| | |_) | |_) |   | |
|____/ \____/|____/|____/    |_|
Hiya! I'm Bobby, your cheerfully goofy task buddy.
Throw me a task and I'll keep it from wandering off!
____________________________________________________________
____________________________________________________________
Error! The description of an event cannot be empty!
____________________________________________________________
____________________________________________________________
Toodles! Bobby is off to recharge the silly batteries.
____________________________________________________________
```

## Test Case 10: Reject an event without a start date

### Aim

Verify that an event with no `/from` start date is rejected without causing
the program to crash.

### Input

```text
event project meeting /from /to 2019-10-16
bye
```

### Expected Output

```text
____________________________________________________________
 ____   ____  ____  ____  __   __
| __ ) / __ \| __ )| __ ) \ \ / /
|  _ \| |  | |  _ \|  _ \  \ V /
| |_) | |__| | |_) | |_) |   | |
|____/ \____/|____/|____/    |_|
Hiya! I'm Bobby, your cheerfully goofy task buddy.
Throw me a task and I'll keep it from wandering off!
____________________________________________________________
____________________________________________________________
Error! Start date of an event cannot be empty. Try again!
____________________________________________________________
____________________________________________________________
Toodles! Bobby is off to recharge the silly batteries.
____________________________________________________________
```

## Test Case 11: Reject an event without an end date

### Aim

Verify that an event with no `/to` end date is rejected without causing the
program to crash.

### Input

```text
event project meeting /from 2019-10-15 /to
bye
```

### Expected Output

```text
____________________________________________________________
 ____   ____  ____  ____  __   __
| __ ) / __ \| __ )| __ ) \ \ / /
|  _ \| |  | |  _ \|  _ \  \ V /
| |_) | |__| | |_) | |_) |   | |
|____/ \____/|____/|____/    |_|
Hiya! I'm Bobby, your cheerfully goofy task buddy.
Throw me a task and I'll keep it from wandering off!
____________________________________________________________
____________________________________________________________
Error! End date of an event cannot be empty. Try again!
____________________________________________________________
____________________________________________________________
Toodles! Bobby is off to recharge the silly batteries.
____________________________________________________________
```

## Test Case 12: Preserve state after an invalid to-do

### Aim

Verify that an invalid empty `todo` does not add a task or change the task
count, and that a later valid task is stored correctly.

### Input

```text
todo write report
todo
list
todo submit report
list
bye
```

### Expected Output

```text
____________________________________________________________
 ____   ____  ____  ____  __   __
| __ ) / __ \| __ )| __ ) \ \ / /
|  _ \| |  | |  _ \|  _ \  \ V /
| |_) | |__| | |_) | |_) |   | |
|____/ \____/|____/|____/    |_|
Hiya! I'm Bobby, your cheerfully goofy task buddy.
Throw me a task and I'll keep it from wandering off!
____________________________________________________________
____________________________________________________________
Boop! I've tucked this task into the list:
[T][ ] write report
Bobby's task count is now 1.
____________________________________________________________
____________________________________________________________
Error! The description of a todo cannot be empty!
____________________________________________________________
____________________________________________________________
Ta-da! Here's your task parade:
1.[T][ ] write report
____________________________________________________________
____________________________________________________________
Boop! I've tucked this task into the list:
[T][ ] submit report
Bobby's task count is now 2.
____________________________________________________________
____________________________________________________________
Ta-da! Here's your task parade:
1.[T][ ] write report
2.[T][ ] submit report
____________________________________________________________
____________________________________________________________
Toodles! Bobby is off to recharge the silly batteries.
____________________________________________________________
```

## Test Case 13: Preserve state after an invalid deadline

### Aim

Verify that a deadline with no date does not add a task or change the task
count, and that a later valid deadline is stored correctly.

### Input

```text
deadline submit report /by 2019-11-29
deadline submit slides /by
list
deadline submit slides /by 2019-12-02
list
bye
```

### Expected Output

```text
____________________________________________________________
 ____   ____  ____  ____  __   __
| __ ) / __ \| __ )| __ ) \ \ / /
|  _ \| |  | |  _ \|  _ \  \ V /
| |_) | |__| | |_) | |_) |   | |
|____/ \____/|____/|____/    |_|
Hiya! I'm Bobby, your cheerfully goofy task buddy.
Throw me a task and I'll keep it from wandering off!
____________________________________________________________
____________________________________________________________
Boop! I've tucked this task into the list:
[D][ ] submit report (by: Nov 29 2019)
Bobby's task count is now 1.
____________________________________________________________
____________________________________________________________
Error! The date of a deadline cannot be empty!
____________________________________________________________
____________________________________________________________
Ta-da! Here's your task parade:
1.[D][ ] submit report (by: Nov 29 2019)
____________________________________________________________
____________________________________________________________
Boop! I've tucked this task into the list:
[D][ ] submit slides (by: Dec 02 2019)
Bobby's task count is now 2.
____________________________________________________________
____________________________________________________________
Ta-da! Here's your task parade:
1.[D][ ] submit report (by: Nov 29 2019)
2.[D][ ] submit slides (by: Dec 02 2019)
____________________________________________________________
____________________________________________________________
Toodles! Bobby is off to recharge the silly batteries.
____________________________________________________________
```

## Test Case 14: Preserve state after an invalid event

### Aim

Verify that an event with no end date does not add a task or change the task
count, and that a later valid event is stored correctly.

### Input

```text
event team meeting /from 2019-10-15 /to 2019-10-16
event planning /from 2019-11-01 /to
list
event planning /from 2019-11-01 /to 2019-11-02
list
bye
```


### Expected Output

```text
____________________________________________________________
 ____   ____  ____  ____  __   __
| __ ) / __ \| __ )| __ ) \ \ / /
|  _ \| |  | |  _ \|  _ \  \ V /
| |_) | |__| | |_) | |_) |   | |
|____/ \____/|____/|____/    |_|
Hiya! I'm Bobby, your cheerfully goofy task buddy.
Throw me a task and I'll keep it from wandering off!
____________________________________________________________
____________________________________________________________
Boop! I've tucked this task into the list:
[E][ ] team meeting (from: Oct 15 2019 to: Oct 16 2019)
Bobby's task count is now 1.
____________________________________________________________
____________________________________________________________
Error! End date of an event cannot be empty. Try again!
____________________________________________________________
____________________________________________________________
Ta-da! Here's your task parade:
1.[E][ ] team meeting (from: Oct 15 2019 to: Oct 16 2019)
____________________________________________________________
____________________________________________________________
Boop! I've tucked this task into the list:
[E][ ] planning (from: Nov 01 2019 to: Nov 02 2019)
Bobby's task count is now 2.
____________________________________________________________
____________________________________________________________
Ta-da! Here's your task parade:
1.[E][ ] team meeting (from: Oct 15 2019 to: Oct 16 2019)
2.[E][ ] planning (from: Nov 01 2019 to: Nov 02 2019)
____________________________________________________________
____________________________________________________________
Toodles! Bobby is off to recharge the silly batteries.
____________________________________________________________
```

## Test Case 15: Delete a task from the list

### Aim

Verify that the `delete` command removes the selected task, displays the
removed task, updates the task count, and renumbers the remaining tasks.

### Input

```text
todo read book
deadline return book /by 2019-06-06
event project meeting /from 2019-08-06 /to 2019-08-07
todo join sports club
todo borrow book
mark 1
mark 2
mark 4
list
delete 3
list
bye
```

### Expected Output

```text
____________________________________________________________
 ____   ____  ____  ____  __   __
| __ ) / __ \| __ )| __ ) \ \ / /
|  _ \| |  | |  _ \|  _ \  \ V /
| |_) | |__| | |_) | |_) |   | |
|____/ \____/|____/|____/    |_|
Hiya! I'm Bobby, your cheerfully goofy task buddy.
Throw me a task and I'll keep it from wandering off!
____________________________________________________________
____________________________________________________________
Boop! I've tucked this task into the list:
[T][ ] read book
Bobby's task count is now 1.
____________________________________________________________
____________________________________________________________
Boop! I've tucked this task into the list:
[D][ ] return book (by: Jun 06 2019)
Bobby's task count is now 2.
____________________________________________________________
____________________________________________________________
Boop! I've tucked this task into the list:
[E][ ] project meeting (from: Aug 06 2019 to: Aug 07 2019)
Bobby's task count is now 3.
____________________________________________________________
____________________________________________________________
Boop! I've tucked this task into the list:
[T][ ] join sports club
Bobby's task count is now 4.
____________________________________________________________
____________________________________________________________
Boop! I've tucked this task into the list:
[T][ ] borrow book
Bobby's task count is now 5.
____________________________________________________________
____________________________________________________________
Woohoo! This task is officially done:
  [T][X] read book
____________________________________________________________
____________________________________________________________
Woohoo! This task is officially done:
  [D][X] return book (by: Jun 06 2019)
____________________________________________________________
____________________________________________________________
Woohoo! This task is officially done:
  [T][X] join sports club
____________________________________________________________
____________________________________________________________
Ta-da! Here's your task parade:
1.[T][X] read book
2.[D][X] return book (by: Jun 06 2019)
3.[E][ ] project meeting (from: Aug 06 2019 to: Aug 07 2019)
4.[T][X] join sports club
5.[T][ ] borrow book
____________________________________________________________
____________________________________________________________
Poof! This task has left the building:
[E][ ] project meeting (from: Aug 06 2019 to: Aug 07 2019)
Bobby's task count is now 4.
____________________________________________________________
____________________________________________________________
Ta-da! Here's your task parade:
1.[T][X] read book
2.[D][X] return book (by: Jun 06 2019)
3.[T][X] join sports club
4.[T][ ] borrow book
____________________________________________________________
____________________________________________________________
Toodles! Bobby is off to recharge the silly batteries.
____________________________________________________________
```

## Test Case 16: Reject delete without a task number

### Aim

Verify that `delete` without a task number shows an error and leaves the task
list unchanged.

### Input

```text
todo read book
delete
bye
```

### Expected Output

```text
____________________________________________________________
 ____   ____  ____  ____  __   __
| __ ) / __ \| __ )| __ ) \ \ / /
|  _ \| |  | |  _ \|  _ \  \ V /
| |_) | |__| | |_) | |_) |   | |
|____/ \____/|____/|____/    |_|
Hiya! I'm Bobby, your cheerfully goofy task buddy.
Throw me a task and I'll keep it from wandering off!
____________________________________________________________
____________________________________________________________
Boop! I've tucked this task into the list:
[T][ ] read book
Bobby's task count is now 1.
____________________________________________________________
____________________________________________________________
Error! The task number cannot be empty!
____________________________________________________________
____________________________________________________________
Toodles! Bobby is off to recharge the silly batteries.
____________________________________________________________
```

## Test Case 17: Reject invalid delete task numbers

### Aim

Verify that non-numeric and out-of-range task numbers show errors without
changing the task list.

### Input

```text
todo read book
delete 2
delete 0
delete abc
list
bye
```

### Expected Output

```text
____________________________________________________________
 ____   ____  ____  ____  __   __
| __ ) / __ \| __ )| __ ) \ \ / /
|  _ \| |  | |  _ \|  _ \  \ V /
| |_) | |__| | |_) | |_) |   | |
|____/ \____/|____/|____/    |_|
Hiya! I'm Bobby, your cheerfully goofy task buddy.
Throw me a task and I'll keep it from wandering off!
____________________________________________________________
____________________________________________________________
Boop! I've tucked this task into the list:
[T][ ] read book
Bobby's task count is now 1.
____________________________________________________________
____________________________________________________________
Error! The task number must be between 1 and 1.
____________________________________________________________
____________________________________________________________
Error! The task number must be between 1 and 1.
____________________________________________________________
____________________________________________________________
Error! The task number must be a valid integer.
____________________________________________________________
____________________________________________________________
Ta-da! Here's your task parade:
1.[T][ ] read book
____________________________________________________________
____________________________________________________________
Toodles! Bobby is off to recharge the silly batteries.
____________________________________________________________
```

## Test Case 18: Reject delete when no tasks are available

### Aim

Verify that attempting to delete from an empty list shows that no tasks are
available to delete.

### Input

```text
delete 1
bye
```

### Expected Output

```text
____________________________________________________________
 ____   ____  ____  ____  __   __
| __ ) / __ \| __ )| __ ) \ \ / /
|  _ \| |  | |  _ \|  _ \  \ V /
| |_) | |__| | |_) | |_) |   | |
|____/ \____/|____/|____/    |_|
Hiya! I'm Bobby, your cheerfully goofy task buddy.
Throw me a task and I'll keep it from wandering off!
____________________________________________________________
____________________________________________________________
No tasks available to delete.
____________________________________________________________
____________________________________________________________
Toodles! Bobby is off to recharge the silly batteries.
____________________________________________________________
```

## Test Case 19: Save task changes to disk
<!-- data-scope: persistence -->

### Aim

Verify that adding, marking, unmarking, and deleting tasks complete normally
while exercising the automatic task-list save path.

### Input

```text
todo read book
deadline return book /by 2019-06-06
event project meeting /from 2019-08-06 /to 2019-08-07
mark 1
unmark 1
delete 2
list
bye
```

### Expected Output

```text
____________________________________________________________
 ____   ____  ____  ____  __   __
| __ ) / __ \| __ )| __ ) \ \ / /
|  _ \| |  | |  _ \|  _ \  \ V /
| |_) | |__| | |_) | |_) |   | |
|____/ \____/|____/|____/    |_|
Hiya! I'm Bobby, your cheerfully goofy task buddy.
Throw me a task and I'll keep it from wandering off!
____________________________________________________________
____________________________________________________________
Boop! I've tucked this task into the list:
[T][ ] read book
Bobby's task count is now 1.
____________________________________________________________
____________________________________________________________
Boop! I've tucked this task into the list:
[D][ ] return book (by: Jun 06 2019)
Bobby's task count is now 2.
____________________________________________________________
____________________________________________________________
Boop! I've tucked this task into the list:
[E][ ] project meeting (from: Aug 06 2019 to: Aug 07 2019)
Bobby's task count is now 3.
____________________________________________________________
____________________________________________________________
Woohoo! This task is officially done:
  [T][X] read book
____________________________________________________________
____________________________________________________________
Plot twist! This task is back in action:
  [T][ ] read book
____________________________________________________________
____________________________________________________________
Poof! This task has left the building:
[D][ ] return book (by: Jun 06 2019)
Bobby's task count is now 2.
____________________________________________________________
____________________________________________________________
Ta-da! Here's your task parade:
1.[T][ ] read book
2.[E][ ] project meeting (from: Aug 06 2019 to: Aug 07 2019)
____________________________________________________________
____________________________________________________________
Toodles! Bobby is off to recharge the silly batteries.
____________________________________________________________
```

## Test Case 20: Load saved tasks at startup
<!-- data-scope: persistence -->

### Aim

Verify that tasks saved by a previous chatbot session are loaded when Bobby
starts and are available to the `list` command.

### Input

```text
list
bye
```

### Expected Output

```text
____________________________________________________________
 ____   ____  ____  ____  __   __
| __ ) / __ \| __ )| __ ) \ \ / /
|  _ \| |  | |  _ \|  _ \  \ V /
| |_) | |__| | |_) | |_) |   | |
|____/ \____/|____/|____/    |_|
Hiya! I'm Bobby, your cheerfully goofy task buddy.
Throw me a task and I'll keep it from wandering off!
____________________________________________________________
____________________________________________________________
Ta-da! Here's your task parade:
1.[T][ ] read book
2.[E][ ] project meeting (from: Aug 06 2019 to: Aug 07 2019)
____________________________________________________________
____________________________________________________________
Toodles! Bobby is off to recharge the silly batteries.
____________________________________________________________
```

## Test Case 21: Reject invalid mark and unmark task numbers

### Aim

Verify that missing, non-numeric, zero, and out-of-range task numbers are
rejected without crashing or changing the task list.

### Input

```text
todo read book
mark
mark abc
mark 0
mark 2
unmark
unmark abc
unmark 0
unmark 2
list
bye
```

### Expected Output

```text
____________________________________________________________
 ____   ____  ____  ____  __   __
| __ ) / __ \| __ )| __ ) \ \ / /
|  _ \| |  | |  _ \|  _ \  \ V /
| |_) | |__| | |_) | |_) |   | |
|____/ \____/|____/|____/    |_|
Hiya! I'm Bobby, your cheerfully goofy task buddy.
Throw me a task and I'll keep it from wandering off!
____________________________________________________________
____________________________________________________________
Boop! I've tucked this task into the list:
[T][ ] read book
Bobby's task count is now 1.
____________________________________________________________
____________________________________________________________
Error! The task number cannot be empty!
____________________________________________________________
____________________________________________________________
Error! The task number must be a valid integer.
____________________________________________________________
____________________________________________________________
Error! The task number must be between 1 and 1.
____________________________________________________________
____________________________________________________________
Error! The task number must be between 1 and 1.
____________________________________________________________
____________________________________________________________
Error! The task number cannot be empty!
____________________________________________________________
____________________________________________________________
Error! The task number must be a valid integer.
____________________________________________________________
____________________________________________________________
Error! The task number must be between 1 and 1.
____________________________________________________________
____________________________________________________________
Error! The task number must be between 1 and 1.
____________________________________________________________
____________________________________________________________
Ta-da! Here's your task parade:
1.[T][ ] read book
____________________________________________________________
____________________________________________________________
Toodles! Bobby is off to recharge the silly batteries.
____________________________________________________________
```

## Test Case 22: Reject an empty command

### Aim

Verify that an empty input line shows a helpful error instead of being treated
as an unknown command.

### Input

```text

bye
```

### Expected Output

```text
____________________________________________________________
 ____   ____  ____  ____  __   __
| __ ) / __ \| __ )| __ ) \ \ / /
|  _ \| |  | |  _ \|  _ \  \ V /
| |_) | |__| | |_) | |_) |   | |
|____/ \____/|____/|____/    |_|
Hiya! I'm Bobby, your cheerfully goofy task buddy.
Throw me a task and I'll keep it from wandering off!
____________________________________________________________
____________________________________________________________
Error! The command cannot be empty!
____________________________________________________________
____________________________________________________________
Toodles! Bobby is off to recharge the silly batteries.
____________________________________________________________
```

## Test Case 23: Reject unsafe task storage characters

### Aim

Verify that a task containing the storage separator is rejected instead of
creating a line that cannot be loaded reliably later.

### Input

```text
todo read | book
bye
```

### Expected Output

```text
____________________________________________________________
 ____   ____  ____  ____  __   __
| __ ) / __ \| __ )| __ ) \ \ / /
|  _ \| |  | |  _ \|  _ \  \ V /
| |_) | |__| | |_) | |_) |   | |
|____/ \____/|____/|____/    |_|
Hiya! I'm Bobby, your cheerfully goofy task buddy.
Throw me a task and I'll keep it from wandering off!
____________________________________________________________
____________________________________________________________
Error! Task details cannot contain the '|' character!
____________________________________________________________
____________________________________________________________
Toodles! Bobby is off to recharge the silly batteries.
____________________________________________________________
```

## Test Case 24: Exit cleanly at end of input

### Aim

Verify that Bobby exits cleanly when input ends without a `bye` command.

### Input

```text
todo read book
```

### Expected Output

```text
____________________________________________________________
 ____   ____  ____  ____  __   __
| __ ) / __ \| __ )| __ ) \ \ / /
|  _ \| |  | |  _ \|  _ \  \ V /
| |_) | |__| | |_) | |_) |   | |
|____/ \____/|____/|____/    |_|
Hiya! I'm Bobby, your cheerfully goofy task buddy.
Throw me a task and I'll keep it from wandering off!
____________________________________________________________
____________________________________________________________
Boop! I've tucked this task into the list:
[T][ ] read book
Bobby's task count is now 1.
____________________________________________________________
____________________________________________________________
Toodles! Bobby is off to recharge the silly batteries.
____________________________________________________________
```

## Test Case 25: Parse a deadline date and time

### Aim

Verify that a deadline in `yyyy-MM-dd HHmm` format is parsed as a real date and
time, then displayed as `MMM dd yyyy h:mm a`.

### Input

```text
deadline return book /by 2019-12-02 1800
list
bye
```

### Expected Output

```text
____________________________________________________________
 ____   ____  ____  ____  __   __
| __ ) / __ \| __ )| __ ) \ \ / /
|  _ \| |  | |  _ \|  _ \  \ V /
| |_) | |__| | |_) | |_) |   | |
|____/ \____/|____/|____/    |_|
Hiya! I'm Bobby, your cheerfully goofy task buddy.
Throw me a task and I'll keep it from wandering off!
____________________________________________________________
____________________________________________________________
Boop! I've tucked this task into the list:
[D][ ] return book (by: Dec 02 2019 6:00 PM)
Bobby's task count is now 1.
____________________________________________________________
____________________________________________________________
Ta-da! Here's your task parade:
1.[D][ ] return book (by: Dec 02 2019 6:00 PM)
____________________________________________________________
____________________________________________________________
Toodles! Bobby is off to recharge the silly batteries.
____________________________________________________________
```

## Test Case 26: Find tasks by a keyword in their descriptions

### Aim

Verify that `find` displays all tasks whose descriptions contain the keyword,
ignoring letter case and preserving the order of the matching tasks.

### Input

```text
todo read book
deadline return book /by 2019-06-06
todo join sports club
find BOOK
bye
```

### Expected Output

```text
____________________________________________________________
 ____   ____  ____  ____  __   __
| __ ) / __ \| __ )| __ ) \ \ / /
|  _ \| |  | |  _ \|  _ \  \ V /
| |_) | |__| | |_) | |_) |   | |
|____/ \____/|____/|____/    |_|
Hiya! I'm Bobby, your cheerfully goofy task buddy.
Throw me a task and I'll keep it from wandering off!
____________________________________________________________
____________________________________________________________
Boop! I've tucked this task into the list:
[T][ ] read book
Bobby's task count is now 1.
____________________________________________________________
____________________________________________________________
Boop! I've tucked this task into the list:
[D][ ] return book (by: Jun 06 2019)
Bobby's task count is now 2.
____________________________________________________________
____________________________________________________________
Boop! I've tucked this task into the list:
[T][ ] join sports club
Bobby's task count is now 3.
____________________________________________________________
____________________________________________________________
Detective Bobby found these matching tasks:
1.[T][ ] read book
2.[D][ ] return book (by: Jun 06 2019)
____________________________________________________________
____________________________________________________________
Toodles! Bobby is off to recharge the silly batteries.
____________________________________________________________
```

## Test Case 27: Reject an empty find keyword

### Aim

Verify that `find` without a keyword shows an error and does not change the
task list.

### Input

```text
todo read book
find
bye
```

### Expected Output

```text
____________________________________________________________
 ____   ____  ____  ____  __   __
| __ ) / __ \| __ )| __ ) \ \ / /
|  _ \| |  | |  _ \|  _ \  \ V /
| |_) | |__| | |_) | |_) |   | |
|____/ \____/|____/|____/    |_|
Hiya! I'm Bobby, your cheerfully goofy task buddy.
Throw me a task and I'll keep it from wandering off!
____________________________________________________________
____________________________________________________________
Boop! I've tucked this task into the list:
[T][ ] read book
Bobby's task count is now 1.
____________________________________________________________
____________________________________________________________
Error! The search keyword cannot be empty!
____________________________________________________________
____________________________________________________________
Toodles! Bobby is off to recharge the silly batteries.
____________________________________________________________
```

## Test Case 28: Report when no tasks match a keyword

### Aim

Verify that a valid keyword with no matching task descriptions displays a
clear no-match message.

### Input

```text
todo read book
find movie
bye
```

### Expected Output

```text
____________________________________________________________
 ____   ____  ____  ____  __   __
| __ ) / __ \| __ )| __ ) \ \ / /
|  _ \| |  | |  _ \|  _ \  \ V /
| |_) | |__| | |_) | |_) |   | |
|____/ \____/|____/|____/    |_|
Hiya! I'm Bobby, your cheerfully goofy task buddy.
Throw me a task and I'll keep it from wandering off!
____________________________________________________________
____________________________________________________________
Boop! I've tucked this task into the list:
[T][ ] read book
Bobby's task count is now 1.
____________________________________________________________
____________________________________________________________
Detective Bobby found these matching tasks:
I checked under the couch. No matching tasks found!
____________________________________________________________
____________________________________________________________
Toodles! Bobby is off to recharge the silly batteries.
____________________________________________________________
```

## Test Case 29: Assign and change a task priority

<!-- data-scope: priority-persistence -->

### Aim

Verify that a task priority can be assigned using a numeric alias and changed
using a case-insensitive name without reordering the task list.

### Input

```text
todo first task
todo second task
priority 2 1
priority 2 MEDIUM
list
bye
```

### Expected Output

```text
____________________________________________________________
 ____   ____  ____  ____  __   __
| __ ) / __ \| __ )| __ ) \ \ / /
|  _ \| |  | |  _ \|  _ \  \ V /
| |_) | |__| | |_) | |_) |   | |
|____/ \____/|____/|____/    |_|
Hiya! I'm Bobby, your cheerfully goofy task buddy.
Throw me a task and I'll keep it from wandering off!
____________________________________________________________
____________________________________________________________
Boop! I've tucked this task into the list:
[T][ ] first task
Bobby's task count is now 1.
____________________________________________________________
____________________________________________________________
Boop! I've tucked this task into the list:
[T][ ] second task
Bobby's task count is now 2.
____________________________________________________________
____________________________________________________________
Beep boop! I've updated this task's priority:
  [T][ ][P: HIGH] second task
____________________________________________________________
____________________________________________________________
Beep boop! I've updated this task's priority:
  [T][ ][P: MEDIUM] second task
____________________________________________________________
____________________________________________________________
Ta-da! Here's your task parade:
1.[T][ ] first task
2.[T][ ][P: MEDIUM] second task
____________________________________________________________
____________________________________________________________
Toodles! Bobby is off to recharge the silly batteries.
____________________________________________________________
```

## Test Case 30: Reload and clear a task priority

<!-- data-scope: priority-persistence -->

### Aim

Verify that a saved priority loads in a new session and can be cleared without
changing the task's position.

### Input

```text
list
priority 2 none
list
bye
```

### Expected Output

```text
____________________________________________________________
 ____   ____  ____  ____  __   __
| __ ) / __ \| __ )| __ ) \ \ / /
|  _ \| |  | |  _ \|  _ \  \ V /
| |_) | |__| | |_) | |_) |   | |
|____/ \____/|____/|____/    |_|
Hiya! I'm Bobby, your cheerfully goofy task buddy.
Throw me a task and I'll keep it from wandering off!
____________________________________________________________
____________________________________________________________
Ta-da! Here's your task parade:
1.[T][ ] first task
2.[T][ ][P: MEDIUM] second task
____________________________________________________________
____________________________________________________________
Beep boop! I've updated this task's priority:
  [T][ ] second task
____________________________________________________________
____________________________________________________________
Ta-da! Here's your task parade:
1.[T][ ] first task
2.[T][ ] second task
____________________________________________________________
____________________________________________________________
Toodles! Bobby is off to recharge the silly batteries.
____________________________________________________________
```

## Test Case 31: Reject invalid priority commands

### Aim

Verify that missing, malformed, out-of-range, and unsupported priority
arguments are rejected without changing the task.

### Input

```text
todo read book
priority
priority 1
priority abc high
priority 2 high
priority 1 urgent
priority 1 high extra
list
bye
```

### Expected Output

```text
____________________________________________________________
 ____   ____  ____  ____  __   __
| __ ) / __ \| __ )| __ ) \ \ / /
|  _ \| |  | |  _ \|  _ \  \ V /
| |_) | |__| | |_) | |_) |   | |
|____/ \____/|____/|____/    |_|
Hiya! I'm Bobby, your cheerfully goofy task buddy.
Throw me a task and I'll keep it from wandering off!
____________________________________________________________
____________________________________________________________
Boop! I've tucked this task into the list:
[T][ ] read book
Bobby's task count is now 1.
____________________________________________________________
____________________________________________________________
Error! The task number cannot be empty!
____________________________________________________________
____________________________________________________________
Error! The priority level cannot be empty!
____________________________________________________________
____________________________________________________________
Error! The task number must be a valid integer.
____________________________________________________________
____________________________________________________________
Error! The task number must be between 1 and 1.
____________________________________________________________
____________________________________________________________
Error! The priority level must be high, medium, low, none, 1, 2, or 3.
____________________________________________________________
____________________________________________________________
Error! The priority command must follow: priority TASK_NUMBER LEVEL.
____________________________________________________________
____________________________________________________________
Ta-da! Here's your task parade:
1.[T][ ] read book
____________________________________________________________
____________________________________________________________
Toodles! Bobby is off to recharge the silly batteries.
____________________________________________________________
```

## Test Case 32: Handle malformed task input safely

### Aim

Verify that irregular whitespace is normalized while duplicate tasks,
non-existent dates, invalid event ranges, repeated parameters, and unexpected
arguments are rejected without changing the task list.

### Input

```text
   todo    Read   Book
todo read book
deadline return book    /by   2026-02-30
event meeting /from 2026-10-03 /to 2026-10-02
event meeting /from 2026-10-01 /from 2026-10-02 /to 2026-10-03
list all
list
bye
```

### Expected Output

```text
____________________________________________________________
 ____   ____  ____  ____  __   __
| __ ) / __ \| __ )| __ ) \ \ / /
|  _ \| |  | |  _ \|  _ \  \ V /
| |_) | |__| | |_) | |_) |   | |
|____/ \____/|____/|____/    |_|
Hiya! I'm Bobby, your cheerfully goofy task buddy.
Throw me a task and I'll keep it from wandering off!
____________________________________________________________
____________________________________________________________
Boop! I've tucked this task into the list:
[T][ ] Read Book
Bobby's task count is now 1.
____________________________________________________________
____________________________________________________________
Error! This task already exists in the list.
____________________________________________________________
____________________________________________________________
Error! The deadline must be a valid date. Use yyyy-MM-dd or yyyy-MM-dd HHmm.
____________________________________________________________
____________________________________________________________
Error! An event cannot end before it starts.
____________________________________________________________
____________________________________________________________
Error! The /from parameter can only be specified once!
____________________________________________________________
____________________________________________________________
Error! The list command does not accept arguments.
____________________________________________________________
____________________________________________________________
Ta-da! Here's your task parade:
1.[T][ ] Read Book
____________________________________________________________
____________________________________________________________
Toodles! Bobby is off to recharge the silly batteries.
____________________________________________________________
```

## Test Case 33: Add a same-day event

### Aim

Verify that an event whose start and end dates are the same is accepted,
displayed, and stored like any other event.

### Input

```text
event workshop /from 2026-10-02 /to 2026-10-02
list
bye
```

### Expected Output

```text
____________________________________________________________
 ____   ____  ____  ____  __   __
| __ ) / __ \| __ )| __ ) \ \ / /
|  _ \| |  | |  _ \|  _ \  \ V /
| |_) | |__| | |_) | |_) |   | |
|____/ \____/|____/|____/    |_|
Hiya! I'm Bobby, your cheerfully goofy task buddy.
Throw me a task and I'll keep it from wandering off!
____________________________________________________________
____________________________________________________________
Boop! I've tucked this task into the list:
[E][ ] workshop (from: Oct 02 2026 to: Oct 02 2026)
Bobby's task count is now 1.
____________________________________________________________
____________________________________________________________
Ta-da! Here's your task parade:
1.[E][ ] workshop (from: Oct 02 2026 to: Oct 02 2026)
____________________________________________________________
____________________________________________________________
Toodles! Bobby is off to recharge the silly batteries.
____________________________________________________________
```
