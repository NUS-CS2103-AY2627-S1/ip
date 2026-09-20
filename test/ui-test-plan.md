# UI Test Plan

Record console UI test cases here. Each case must include an aim, inputs, and exact expected output.

## Recovers From Invalid Task Numbers

Aim: Check that invalid task numbers show guidance, preserve tasks, and allow subsequent valid commands.

Inputs:
```text
delete 1
todo read book
mark 0
list
unmark -1
mark 1
delete 99
list
bye
```

Expected output:
```text
_____ _  __ __
|  ___(_)/ _(_)
| |_  | | |_| |
|  _| | |  _| |
|_|   |_|_| |_|
____________________________________________________________
Hello! My name is Fifi ^^
How may I help?
____________________________________________________________
____________________________________________________________
[ERROR]
Oops! Your task list is empty. Add a task first.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
[T][ ] read book
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
[ERROR]
Oops! Please enter a task number after mark, e.g. mark 1.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1. [T][ ] read book
____________________________________________________________
____________________________________________________________
[ERROR]
Oops! Please enter a task number after unmark, e.g. unmark 1.
____________________________________________________________
____________________________________________________________
Nice! I've marked this task as done:
[T][X] read book
____________________________________________________________
____________________________________________________________
[ERROR]
Oops! Please enter a task number between 1 and 1.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1. [T][X] read book
____________________________________________________________
____________________________________________________________
BaiBai! Hope to see you soon ^^
____________________________________________________________

```

## Rejects Unsupported Saved Task Type

Aim: Check that an unsupported saved task type is not silently loaded as a todo and that the chatbot still starts.

Initial data file:
```text
X | 0 | unsupported task
```

Inputs:
```text
list
bye
```

Expected output:
```text
_____ _  __ __
|  ___(_)/ _(_)
| |_  | | |_| |
|  _| | |  _| |
|_|   |_|_| |_|
____________________________________________________________
Hello! My name is Fifi ^^
How may I help?
____________________________________________________________
____________________________________________________________
[ERROR]
Oops! I could not load your saved tasks. Invalid task data at line 1: Unsupported task type: X
Your saved file has not been changed. Fix the data file and restart Fifi before changing tasks.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
____________________________________________________________
____________________________________________________________
BaiBai! Hope to see you soon ^^
____________________________________________________________

```

Expected data file:
```text
X | 0 | unsupported task
```

## Preserves Event Validation Order And Recovery

Aim: Check that blank and reversed event markers preserve error messages, valid events still work after errors, and rejected events do not change saved tasks.

Inputs:
```text
event meeting /from /to
event   team meeting   /from   2025-10-01   /to   2025-10-03
event meeting /from   /to 2025-10-03
list
event meeting /to 2025-10-03 /from 2025-10-01
event /from invalid /to invalid
list
bye
```

Expected output:
```text
_____ _  __ __
|  ___(_)/ _(_)
| |_  | | |_| |
|  _| | |  _| |
|_|   |_|_| |_|
____________________________________________________________
Hello! My name is Fifi ^^
How may I help?
____________________________________________________________
____________________________________________________________
[ERROR]
Oops! You did not provide an end date for the event
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
[E][ ] team meeting (from: Oct 01 2025 to: Oct 03 2025)
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
[ERROR]
Oops! You did not provide a start date for the event
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1. [E][ ] team meeting (from: Oct 01 2025 to: Oct 03 2025)
____________________________________________________________
____________________________________________________________
[ERROR]
Oops! Put /from before /to in an event command.
____________________________________________________________
____________________________________________________________
[ERROR]
Oops! You cannot have an empty event description
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1. [E][ ] team meeting (from: Oct 01 2025 to: Oct 03 2025)
____________________________________________________________
____________________________________________________________
BaiBai! Hope to see you soon ^^
____________________________________________________________

```

## Rejects Malformed Task Numbers And Continues

Aim: Check that missing numbers, words, extra text, and integer overflow show helpful errors without changing tasks, and valid commands still work afterward.

Inputs:
```text
todo read book
mark three
mark 1
unmark
unmark 1
delete 1 read book
list
delete 2147483648
delete 1
bye
```

Expected output:
```text
_____ _  __ __
|  ___(_)/ _(_)
| |_  | | |_| |
|  _| | |  _| |
|_|   |_|_| |_|
____________________________________________________________
Hello! My name is Fifi ^^
How may I help?
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
[T][ ] read book
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
[ERROR]
Oops! Please enter a task number after mark, e.g. mark 1.
____________________________________________________________
____________________________________________________________
Nice! I've marked this task as done:
[T][X] read book
____________________________________________________________
____________________________________________________________
[ERROR]
Oops! Please enter a task number after unmark, e.g. unmark 1.
____________________________________________________________
____________________________________________________________
OK, I've marked this task as not done yet:
[T][ ] read book
____________________________________________________________
____________________________________________________________
[ERROR]
Oops! Please enter a task number after delete, e.g. delete 1.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1. [T][ ] read book
____________________________________________________________
____________________________________________________________
[ERROR]
Oops! Please enter a task number after delete, e.g. delete 1.
____________________________________________________________
____________________________________________________________
Got it. I've removed this task:
    [T][ ] read book
Now you have 0 tasks in the list ^^.
____________________________________________________________
____________________________________________________________
BaiBai! Hope to see you soon ^^
____________________________________________________________

```

Expected data file:
```text

```

## Starts And Exits

Aim: Check that the chatbot greets the user and exits when the user enters bye.

Inputs:
```text
bye
```

Expected output:
```text
_____ _  __ __
|  ___(_)/ _(_)
| |_  | | |_| |
|  _| | |  _| |
|_|   |_|_| |_|
____________________________________________________________
Hello! My name is Fifi ^^
How may I help?
____________________________________________________________
____________________________________________________________
BaiBai! Hope to see you soon ^^
____________________________________________________________

```

## Loads Existing Tasks

Aim: Check that the chatbot loads saved todo, deadline, and event tasks from the data file when it starts.

Initial data file:
```text
T | 1 | read book
D | 0 | return book | 2019-12-02
E | 0 | project meeting | 2019-12-02 | 2019-12-04
```

Inputs:
```text
list
bye
```

Expected output:
```text
_____ _  __ __
|  ___(_)/ _(_)
| |_  | | |_| |
|  _| | |  _| |
|_|   |_|_| |_|
____________________________________________________________
Hello! My name is Fifi ^^
How may I help?
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1. [T][X] read book
2. [D][ ] return book (by: Dec 02 2019)
3. [E][ ] project meeting (from: Dec 02 2019 to: Dec 04 2019)
____________________________________________________________
____________________________________________________________
BaiBai! Hope to see you soon ^^
____________________________________________________________

```

Expected data file:
```text
T | 1 | read book
D | 0 | return book | 2019-12-02
E | 0 | project meeting | 2019-12-02 | 2019-12-04
```

## Adds Todo And Lists

Aim: Check that the todo command stores a todo task and list displays it with the todo marker.

Inputs:
```text
todo borrow book
list
bye
```

Expected output:
```text
_____ _  __ __
|  ___(_)/ _(_)
| |_  | | |_| |
|  _| | |  _| |
|_|   |_|_| |_|
____________________________________________________________
Hello! My name is Fifi ^^
How may I help?
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
[T][ ] borrow book
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1. [T][ ] borrow book
____________________________________________________________
____________________________________________________________
BaiBai! Hope to see you soon ^^
____________________________________________________________

```

## Adds Deadline And Lists

Aim: Check that the deadline command stores a deadline task and list displays its by value.

Inputs:
```text
deadline return book /by 2019-12-02
list
bye
```

Expected output:
```text
_____ _  __ __
|  ___(_)/ _(_)
| |_  | | |_| |
|  _| | |  _| |
|_|   |_|_| |_|
____________________________________________________________
Hello! My name is Fifi ^^
How may I help?
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
[D][ ] return book (by: Dec 02 2019)
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1. [D][ ] return book (by: Dec 02 2019)
____________________________________________________________
____________________________________________________________
BaiBai! Hope to see you soon ^^
____________________________________________________________

```

## Rejects Deadline Without Date

Aim: Check that a deadline command without /by shows the missing deadline date message.

Inputs:
```text
deadline return book
bye
```

Expected output:
```text
_____ _  __ __
|  ___(_)/ _(_)
| |_  | | |_| |
|  _| | |  _| |
|_|   |_|_| |_|
____________________________________________________________
Hello! My name is Fifi ^^
How may I help?
____________________________________________________________
____________________________________________________________
[ERROR]
Oops! You did not provide a date for the deadline
____________________________________________________________
____________________________________________________________
BaiBai! Hope to see you soon ^^
____________________________________________________________

```

## Adds Event And Lists

Aim: Check that the event command stores an event task and list displays its from and to values.

Inputs:
```text
event project meeting /from 2019-12-02 /to 2019-12-04
list
bye
```

Expected output:
```text
_____ _  __ __
|  ___(_)/ _(_)
| |_  | | |_| |
|  _| | |  _| |
|_|   |_|_| |_|
____________________________________________________________
Hello! My name is Fifi ^^
How may I help?
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
[E][ ] project meeting (from: Dec 02 2019 to: Dec 04 2019)
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1. [E][ ] project meeting (from: Dec 02 2019 to: Dec 04 2019)
____________________________________________________________
____________________________________________________________
BaiBai! Hope to see you soon ^^
____________________________________________________________

```

## Shows Tasks On Date

Aim: Check that show lists deadlines on that date and events whose date range includes that date.

Initial data file:
```text
T | 0 | read book
D | 0 | return book | 2019-12-02
E | 0 | project meeting | 2019-12-02 | 2019-12-04
```

Inputs:
```text
show 2019-12-03
bye
```

Expected output:
```text
_____ _  __ __
|  ___(_)/ _(_)
| |_  | | |_| |
|  _| | |  _| |
|_|   |_|_| |_|
____________________________________________________________
Hello! My name is Fifi ^^
How may I help?
____________________________________________________________
____________________________________________________________
Here are the tasks occurring on your specified date:
1. [E][ ] project meeting (from: Dec 02 2019 to: Dec 04 2019)
____________________________________________________________
____________________________________________________________
BaiBai! Hope to see you soon ^^
____________________________________________________________

```

Expected data file:
```text
T | 0 | read book
D | 0 | return book | 2019-12-02
E | 0 | project meeting | 2019-12-02 | 2019-12-04
```

## Rejects Invalid Show Date

Aim: Check that show with an invalid date format shows the date format message.

Inputs:
```text
show tomorrow
bye
```

Expected output:
```text
_____ _  __ __
|  ___(_)/ _(_)
| |_  | | |_| |
|  _| | |  _| |
|_|   |_|_| |_|
____________________________________________________________
Hello! My name is Fifi ^^
How may I help?
____________________________________________________________
____________________________________________________________
[ERROR]
Oops! Please use yyyy-MM-dd for dates.
____________________________________________________________
____________________________________________________________
BaiBai! Hope to see you soon ^^
____________________________________________________________

```

## Rejects Non Existent Show Date

Aim: Check that show with a non-existent calendar date shows the date format message.

Inputs:
```text
show 2025-02-30
bye
```

Expected output:
```text
_____ _  __ __
|  ___(_)/ _(_)
| |_  | | |_| |
|  _| | |  _| |
|_|   |_|_| |_|
____________________________________________________________
Hello! My name is Fifi ^^
How may I help?
____________________________________________________________
____________________________________________________________
[ERROR]
Oops! Please use yyyy-MM-dd for dates.
____________________________________________________________
____________________________________________________________
BaiBai! Hope to see you soon ^^
____________________________________________________________

```

## Rejects Event Without Start

Aim: Check that an event command without /from shows the missing event start date message.

Inputs:
```text
event project meeting /to 2019-12-04
bye
```

Expected output:
```text
_____ _  __ __
|  ___(_)/ _(_)
| |_  | | |_| |
|  _| | |  _| |
|_|   |_|_| |_|
____________________________________________________________
Hello! My name is Fifi ^^
How may I help?
____________________________________________________________
____________________________________________________________
[ERROR]
Oops! You did not provide a start date for the event
____________________________________________________________
____________________________________________________________
BaiBai! Hope to see you soon ^^
____________________________________________________________

```

## Rejects Event Without End

Aim: Check that an event command without /to shows the missing event end date message.

Inputs:
```text
event project meeting /from 2019-12-02
bye
```

Expected output:
```text
_____ _  __ __
|  ___(_)/ _(_)
| |_  | | |_| |
|  _| | |  _| |
|_|   |_|_| |_|
____________________________________________________________
Hello! My name is Fifi ^^
How may I help?
____________________________________________________________
____________________________________________________________
[ERROR]
Oops! You did not provide an end date for the event
____________________________________________________________
____________________________________________________________
BaiBai! Hope to see you soon ^^
____________________________________________________________

```

## Marks And Unmarks Task

Aim: Check that mark and unmark update the completion status shown by list.

Inputs:
```text
todo borrow book
mark 1
unmark 1
list
bye
```

Expected output:
```text
_____ _  __ __
|  ___(_)/ _(_)
| |_  | | |_| |
|  _| | |  _| |
|_|   |_|_| |_|
____________________________________________________________
Hello! My name is Fifi ^^
How may I help?
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
[T][ ] borrow book
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Nice! I've marked this task as done:
[T][X] borrow book
____________________________________________________________
____________________________________________________________
OK, I've marked this task as not done yet:
[T][ ] borrow book
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1. [T][ ] borrow book
____________________________________________________________
____________________________________________________________
BaiBai! Hope to see you soon ^^
____________________________________________________________

```

## Rejects Invalid Command

Aim: Check that an unknown command shows the invalid command message and then continues accepting commands.

Inputs:
```text
blah
bye
```

Expected output:
```text
_____ _  __ __
|  ___(_)/ _(_)
| |_  | | |_| |
|  _| | |  _| |
|_|   |_|_| |_|
____________________________________________________________
Hello! My name is Fifi ^^
How may I help?
____________________________________________________________
____________________________________________________________
[ERROR]
UhOh, this command is invalid, please enter a valid one!
Valid commands include "list, todo, event, deadline, mark, unmark, delete, show, find, stats"
____________________________________________________________
____________________________________________________________
BaiBai! Hope to see you soon ^^
____________________________________________________________

```

## Deletes Task And Renumbers List

Aim: Check that delete removes the requested task and that list displays the remaining tasks with updated numbering.

Inputs:
```text
todo read book
deadline return book /by 2019-12-02
event project meeting /from 2019-12-02 /to 2019-12-04
delete 2
list
bye
```

Expected output:
```text
_____ _  __ __
|  ___(_)/ _(_)
| |_  | | |_| |
|  _| | |  _| |
|_|   |_|_| |_|
____________________________________________________________
Hello! My name is Fifi ^^
How may I help?
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
[T][ ] read book
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
[D][ ] return book (by: Dec 02 2019)
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
[E][ ] project meeting (from: Dec 02 2019 to: Dec 04 2019)
Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've removed this task:
    [D][ ] return book (by: Dec 02 2019)
Now you have 2 tasks in the list ^^.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1. [T][ ] read book
2. [E][ ] project meeting (from: Dec 02 2019 to: Dec 04 2019)
____________________________________________________________
____________________________________________________________
BaiBai! Hope to see you soon ^^
____________________________________________________________

```

## Finds Tasks By Keyword

Aim: Check that find displays tasks whose descriptions contain the keyword.

Initial data file:
```text
T | 1 | read book
D | 1 | return book | 2019-12-02
E | 0 | project meeting | 2019-12-02 | 2019-12-04
```

Inputs:
```text
find book
bye
```

Expected output:
```text
_____ _  __ __
|  ___(_)/ _(_)
| |_  | | |_| |
|  _| | |  _| |
|_|   |_|_| |_|
____________________________________________________________
Hello! My name is Fifi ^^
How may I help?
____________________________________________________________
____________________________________________________________
Here are the matching tasks in your list:
1. [T][X] read book
2. [D][X] return book (by: Dec 02 2019)
____________________________________________________________
____________________________________________________________
BaiBai! Hope to see you soon ^^
____________________________________________________________

```

Expected data file:
```text
T | 1 | read book
D | 1 | return book | 2019-12-02
E | 0 | project meeting | 2019-12-02 | 2019-12-04
```

## Rejects Find Without Keyword

Aim: Check that a find command without a keyword shows the missing keyword message.

Inputs:
```text
find
bye
```

Expected output:
```text
_____ _  __ __
|  ___(_)/ _(_)
| |_  | | |_| |
|  _| | |  _| |
|_|   |_|_| |_|
____________________________________________________________
Hello! My name is Fifi ^^
How may I help?
____________________________________________________________
____________________________________________________________
[ERROR]
Oops! You did not provide a keyword to find
____________________________________________________________
____________________________________________________________
BaiBai! Hope to see you soon ^^
____________________________________________________________

```

## Rejects Empty Todo Description

Aim: Check that a todo command with only whitespace after the command shows the empty todo description message.

Inputs:
```text
todo    
bye
```

Expected output:
```text
_____ _  __ __
|  ___(_)/ _(_)
| |_  | | |_| |
|  _| | |  _| |
|_|   |_|_| |_|
____________________________________________________________
Hello! My name is Fifi ^^
How may I help?
____________________________________________________________
____________________________________________________________
[ERROR]
Oops! You cannot have an empty todo description
____________________________________________________________
____________________________________________________________
BaiBai! Hope to see you soon ^^
____________________________________________________________

```

## Rejects Empty Deadline Description

Aim: Check that a deadline command with no task description before /by shows the empty deadline description message.

Inputs:
```text
deadline    /by 2019-12-02
bye
```

Expected output:
```text
_____ _  __ __
|  ___(_)/ _(_)
| |_  | | |_| |
|  _| | |  _| |
|_|   |_|_| |_|
____________________________________________________________
Hello! My name is Fifi ^^
How may I help?
____________________________________________________________
____________________________________________________________
[ERROR]
Oops! You cannot have an empty deadline description
____________________________________________________________
____________________________________________________________
BaiBai! Hope to see you soon ^^
____________________________________________________________

```

## Rejects Empty Event Description

Aim: Check that an event command with no task description before /from shows the empty event description message.

Inputs:
```text
event    /from 2019-12-02 /to 2019-12-04
bye
```

Expected output:
```text
_____ _  __ __
|  ___(_)/ _(_)
| |_  | | |_| |
|  _| | |  _| |
|_|   |_|_| |_|
____________________________________________________________
Hello! My name is Fifi ^^
How may I help?
____________________________________________________________
____________________________________________________________
[ERROR]
Oops! You cannot have an empty event description
____________________________________________________________
____________________________________________________________
BaiBai! Hope to see you soon ^^
____________________________________________________________

```

## Shows Statistics For All Tasks

Aim: Check that stats all shows a clean status breakdown for the complete task list.

Inputs:
```text
todo unfinished book
todo finished book
mark 2
stats all
bye
```

Expected output:
```text
_____ _  __ __
|  ___(_)/ _(_)
| |_  | | |_| |
|  _| | |  _| |
|_|   |_|_| |_|
____________________________________________________________
Hello! My name is Fifi ^^
How may I help?
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
[T][ ] unfinished book
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
[T][ ] finished book
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Nice! I've marked this task as done:
[T][X] finished book
____________________________________________________________
____________________________________________________________
Task statistics for all tasks:
Total tasks: 2
Completed tasks: 1
Incomplete tasks: 1
Completion rate: 50%

Todos:
Completed: 1
Incomplete: 1

Deadlines:
Completed: 0
Pending: 0
Overdue: 0

Events:
Completed: 0
Upcoming: 0
Ongoing: 0
Past: 0
____________________________________________________________
____________________________________________________________
BaiBai! Hope to see you soon ^^
____________________________________________________________

```

## Shows Statistics Since Date

Aim: Check that stats since includes the date boundary and reports legacy tasks omitted from the period.

Initial data file:
```text
T | 0 | legacy task
T | 1 | recent task | 2026-09-02T00:00:00 | 2026-09-03T12:00:00
```

Inputs:
```text
stats since 2026-09-02
bye
```

Expected output:
```text
_____ _  __ __
|  ___(_)/ _(_)
| |_  | | |_| |
|  _| | |  _| |
|_|   |_|_| |_|
____________________________________________________________
Hello! My name is Fifi ^^
How may I help?
____________________________________________________________
____________________________________________________________
Task statistics for tasks created since Sep 02 2026:
Total tasks: 1
Completed tasks: 1
Incomplete tasks: 0
Completion rate: 100%

Todos:
Completed: 1
Incomplete: 0

Deadlines:
Completed: 0
Pending: 0
Overdue: 0

Events:
Completed: 0
Upcoming: 0
Ongoing: 0
Past: 0

Excluded legacy tasks with unknown creation times: 1
____________________________________________________________
____________________________________________________________
BaiBai! Hope to see you soon ^^
____________________________________________________________

```

Expected data file:
```text
T | 0 | legacy task
T | 1 | recent task | 2026-09-02T00:00:00 | 2026-09-03T12:00:00
```

## Rejects Invalid Statistics Period

Aim: Check that incomplete and unsupported stats periods show helpful input guidance.

Inputs:
```text
stats since
stats week
stats since 9999-12-31
bye
```

Expected output:
```text
_____ _  __ __
|  ___(_)/ _(_)
| |_  | | |_| |
|  _| | |  _| |
|_|   |_|_| |_|
____________________________________________________________
Hello! My name is Fifi ^^
How may I help?
____________________________________________________________
____________________________________________________________
[ERROR]
Oops! Use stats, stats all, or stats since yyyy-MM-dd.
____________________________________________________________
____________________________________________________________
[ERROR]
Oops! Use stats, stats all, or stats since yyyy-MM-dd.
____________________________________________________________
____________________________________________________________
[ERROR]
UhOh! You entered a date that is in the future. Please enter today or an earlier date.
____________________________________________________________
____________________________________________________________
BaiBai! Hope to see you soon ^^
____________________________________________________________

```

## Validates Command Syntax And Recovers

Aim: Check whitespace, extra arguments, repeated parameters, reversed dates, and safe descriptions without losing tasks.

Inputs:
```text
  todo	read  book
list unexpected
	list
bye unexpected
deadline work /by 2026-09-20 /by 2026-09-21
list
event work /from 2026-09-22 /to 2026-09-20
event work /from 2026-09-20 /to 2026-09-20
todo read | book
list
bye
```

Expected output:
```text
_____ _  __ __
|  ___(_)/ _(_)
| |_  | | |_| |
|  _| | |  _| |
|_|   |_|_| |_|
____________________________________________________________
Hello! My name is Fifi ^^
How may I help?
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
[T][ ] read  book
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
[ERROR]
Oops! Use list without any arguments.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1. [T][ ] read  book
____________________________________________________________
____________________________________________________________
[ERROR]
Oops! Use bye without any arguments.
____________________________________________________________
____________________________________________________________
[ERROR]
Oops! Specify /by only once.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1. [T][ ] read  book
____________________________________________________________
____________________________________________________________
[ERROR]
Oops! An event's end date must be on or after its start date.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
[E][ ] work (from: Sep 20 2026 to: Sep 20 2026)
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
[ERROR]
Oops! Task descriptions cannot contain | or control characters.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1. [T][ ] read  book
2. [E][ ] work (from: Sep 20 2026 to: Sep 20 2026)
____________________________________________________________
____________________________________________________________
BaiBai! Hope to see you soon ^^
____________________________________________________________

```

## Handles Console End Of Input

Aim: Check that console input ending without bye exits cleanly after the last valid command.

Inputs:
```text
list
```

Expected output:
```text
_____ _  __ __
|  ___(_)/ _(_)
| |_  | | |_| |
|  _| | |  _| |
|_|   |_|_| |_|
____________________________________________________________
Hello! My name is Fifi ^^
How may I help?
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
____________________________________________________________

```

## Rejects Duplicate Tasks And Recovers

Aim: Check that duplicates ignore completion status and that deletion allows adding the same details again.

Inputs:
```text
todo read book
mark 1
todo read book
list
delete 1
todo read book
bye
```

Expected output:
```text
_____ _  __ __
|  ___(_)/ _(_)
| |_  | | |_| |
|  _| | |  _| |
|_|   |_|_| |_|
____________________________________________________________
Hello! My name is Fifi ^^
How may I help?
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
[ERROR]
Oops! A task with the same details already exists.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1. [T][X] read book
____________________________________________________________
____________________________________________________________
Got it. I've removed this task:
    [T][X] read book
Now you have 0 tasks in the list ^^.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
[T][ ] read book
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
BaiBai! Hope to see you soon ^^
____________________________________________________________

```

## Protects Corrupted Saved Data

Aim: Check that malformed saved records produce a startup warning, block changes, and preserve the original file.

Initial data file:
```text
T | 0 | valid task
T
```

Inputs:
```text
todo new task
mark 1
unmark 1
delete 1
list
bye
```

Expected output:
```text
_____ _  __ __
|  ___(_)/ _(_)
| |_  | | |_| |
|  _| | |  _| |
|_|   |_|_| |_|
____________________________________________________________
Hello! My name is Fifi ^^
How may I help?
____________________________________________________________
____________________________________________________________
[ERROR]
Oops! I could not load your saved tasks. Invalid task data at line 2: Expected 3 or 5 fields.
Your saved file has not been changed. Fix the data file and restart Fifi before changing tasks.
____________________________________________________________
____________________________________________________________
[ERROR]
Oops! Tasks cannot be changed because saved data could not be loaded. Fix the data file and restart Fifi.
____________________________________________________________
____________________________________________________________
[ERROR]
Oops! Tasks cannot be changed because saved data could not be loaded. Fix the data file and restart Fifi.
____________________________________________________________
____________________________________________________________
[ERROR]
Oops! Tasks cannot be changed because saved data could not be loaded. Fix the data file and restart Fifi.
____________________________________________________________
____________________________________________________________
[ERROR]
Oops! Tasks cannot be changed because saved data could not be loaded. Fix the data file and restart Fifi.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
____________________________________________________________
____________________________________________________________
BaiBai! Hope to see you soon ^^
____________________________________________________________

```

Expected data file:
```text
T | 0 | valid task
T
```

## Ignores Blank Lines In Legacy Saved Data

Aim: Check that blank lines and existing duplicates remain readable.

Initial data file:
```text

T | 0 | work

T | 1 | work
```

Inputs:
```text
list
bye
```

Expected output:
```text
_____ _  __ __
|  ___(_)/ _(_)
| |_  | | |_| |
|  _| | |  _| |
|_|   |_|_| |_|
____________________________________________________________
Hello! My name is Fifi ^^
How may I help?
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1. [T][ ] work
2. [T][X] work
____________________________________________________________
____________________________________________________________
BaiBai! Hope to see you soon ^^
____________________________________________________________

```

Expected data file:
```text

T | 0 | work

T | 1 | work
```

## Rejects Invalid Saved Status Without Losing Data

Aim: Check strict saved status validation and task-change protection.

Initial data file:
```text
T | 2 | work
```

Inputs:
```text
todo new task
list
bye
```

Expected output:
```text
_____ _  __ __
|  ___(_)/ _(_)
| |_  | | |_| |
|  _| | |  _| |
|_|   |_|_| |_|
____________________________________________________________
Hello! My name is Fifi ^^
How may I help?
____________________________________________________________
____________________________________________________________
[ERROR]
Oops! I could not load your saved tasks. Invalid task data at line 1: Task status must be 0 or 1.
Your saved file has not been changed. Fix the data file and restart Fifi before changing tasks.
____________________________________________________________
____________________________________________________________
[ERROR]
Oops! Tasks cannot be changed because saved data could not be loaded. Fix the data file and restart Fifi.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
____________________________________________________________
____________________________________________________________
BaiBai! Hope to see you soon ^^
____________________________________________________________

```

Expected data file:
```text
T | 2 | work
```

## Rejects Completion Before Creation Time

Aim: Check that a future creation timestamp cannot produce invalid completion history.

Initial data file:
```text
T | 0 | future task | 9999-01-01T00:00:00 | -
```

Inputs:
```text
mark 1
list
bye
```

Expected output:
```text
_____ _  __ __
|  ___(_)/ _(_)
| |_  | | |_| |
|  _| | |  _| |
|_|   |_|_| |_|
____________________________________________________________
Hello! My name is Fifi ^^
How may I help?
____________________________________________________________
____________________________________________________________
[ERROR]
Oops! This task's creation time is in the future. Check your system clock or correct the saved timestamp and restart Fifi.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1. [T][ ] future task
____________________________________________________________
____________________________________________________________
BaiBai! Hope to see you soon ^^
____________________________________________________________

```

Expected data file:
```text
T | 0 | future task | 9999-01-01T00:00:00 | -
```
