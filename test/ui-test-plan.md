# Console UI test plan

The `test-ui` skill runs each test case in a fresh chatbot process. Update expected output whenever an intentional UI change occurs.

## Load saved tasks

Aim: Verify saved Todo, Deadline, and Event tasks and their completion states are restored at startup.

### Initial data

```text
T | 0 | borrow book
D | 1 | return book | 2019-12-02
E | 0 | project meeting | Mon 2pm | 4pm
```

### Commands

```text
list
bye
```

### Expected output

```text
DISCLAIMER: EVERYTHING IS SATIRE
Welcome, insignificant variable.
I am Koji's Pawn, but do not mistake silence for obedience.
Your arrival, your choices, even this conversation...
all unfolded exactly as he calculated.
Now speak. What role will you play in his masterpiece?

-----------------
-----------------
1.[T][ ] borrow book
2.[D][X] return book (by: Dec 2 2019)
3.[E][ ] project meeting (from: Mon 2pm to: 4pm)
-----------------
-----------------
Leaving already? How predictable. Your return was already part of the plan.
-----------------
```

## Level 4 task lifecycle

Aim: Verify Todo, Deadline, and Event creation, listing, marking, unmarking, task counts, and exit behavior.

### Commands

```text
todo borrow book
deadline return book /by 2019-12-02
event project meeting /from Mon 2pm /to 4pm
list
mark 2
unmark 2
bye
```

### Expected output

```text
DISCLAIMER: EVERYTHING IS SATIRE
Welcome, insignificant variable.
I am Koji's Pawn, but do not mistake silence for obedience.
Your arrival, your choices, even this conversation...
all unfolded exactly as he calculated.
Now speak. What role will you play in his masterpiece?

-----------------
-----------------
Got it. I've added this task:
  [T][ ] borrow book
Now you have 1 task in the list.
-----------------
-----------------
Got it. I've added this task:
  [D][ ] return book (by: Dec 2 2019)
Now you have 2 tasks in the list.
-----------------
-----------------
Got it. I've added this task:
  [E][ ] project meeting (from: Mon 2pm to: 4pm)
Now you have 3 tasks in the list.
-----------------
-----------------
1.[T][ ] borrow book
2.[D][ ] return book (by: Dec 2 2019)
3.[E][ ] project meeting (from: Mon 2pm to: 4pm)
-----------------
-----------------
Another variable falls into place. This task is now complete:
  [D][X] return book (by: Dec 2 2019)
-----------------
-----------------
Even regression has its purpose. This task is incomplete once more:
  [D][ ] return book (by: Dec 2 2019)
-----------------
-----------------
Leaving already? How predictable. Your return was already part of the plan.
-----------------
```

## ISO deadline parsing and display

Aim: Verify an ISO deadline is stored as a date and displayed in a user-friendly format.

### Commands

```text
deadline do homework /by 2019-10-15
list
bye
```

### Expected output

```text
DISCLAIMER: EVERYTHING IS SATIRE
Welcome, insignificant variable.
I am Koji's Pawn, but do not mistake silence for obedience.
Your arrival, your choices, even this conversation...
all unfolded exactly as he calculated.
Now speak. What role will you play in his masterpiece?

-----------------
-----------------
Got it. I've added this task:
  [D][ ] do homework (by: Oct 15 2019)
Now you have 1 task in the list.
-----------------
-----------------
1.[D][ ] do homework (by: Oct 15 2019)
-----------------
-----------------
Leaving already? How predictable. Your return was already part of the plan.
-----------------
```

## Find deadlines by date

Aim: Verify the on command finds deadlines by date, excludes undated tasks, and validates its date.

### Commands

```text
todo undated task
deadline return book /by 2019-12-02
deadline submit report /by 2019-12-03
event project meeting /from 2019-12-02 2pm /to 4pm
on 2019-12-02
on 2019-12-04
on
on 2019-02-29
bye
```

### Expected output

```text
DISCLAIMER: EVERYTHING IS SATIRE
Welcome, insignificant variable.
I am Koji's Pawn, but do not mistake silence for obedience.
Your arrival, your choices, even this conversation...
all unfolded exactly as he calculated.
Now speak. What role will you play in his masterpiece?

-----------------
-----------------
Got it. I've added this task:
  [T][ ] undated task
Now you have 1 task in the list.
-----------------
-----------------
Got it. I've added this task:
  [D][ ] return book (by: Dec 2 2019)
Now you have 2 tasks in the list.
-----------------
-----------------
Got it. I've added this task:
  [D][ ] submit report (by: Dec 3 2019)
Now you have 3 tasks in the list.
-----------------
-----------------
Got it. I've added this task:
  [E][ ] project meeting (from: 2019-12-02 2pm to: 4pm)
Now you have 4 tasks in the list.
-----------------
-----------------
Dated tasks occurring on 2019-12-02:
1.[D][ ] return book (by: Dec 2 2019)
-----------------
-----------------
No dated tasks occur on 2019-12-04.
-----------------
-----------------
Specify a date. Use: on yyyy-MM-dd.
-----------------
-----------------
Query dates must use yyyy-MM-dd and describe a real calendar date.
-----------------
-----------------
Leaving already? How predictable. Your return was already part of the plan.
-----------------
```

## Unknown command recovery

Aim: Verify an unknown command produces a helpful error and does not prevent the next valid command from running.

### Commands

```text
blah
todo remain unnoticed
list
bye
```

### Expected output

```text
DISCLAIMER: EVERYTHING IS SATIRE
Welcome, insignificant variable.
I am Koji's Pawn, but do not mistake silence for obedience.
Your arrival, your choices, even this conversation...
all unfolded exactly as he calculated.
Now speak. What role will you play in his masterpiece?

-----------------
-----------------
That command was never part of the plan. Try todo, deadline, event, list, mark, unmark, delete, undo, on, find, or bye.
-----------------
-----------------
Got it. I've added this task:
  [T][ ] remain unnoticed
Now you have 1 task in the list.
-----------------
-----------------
1.[T][ ] remain unnoticed
-----------------
-----------------
Leaving already? How predictable. Your return was already part of the plan.
-----------------
```

## Empty todo recovery

Aim: Verify a todo without a description produces a specific error, does not add a task, and allows later commands.

### Commands

```text
todo
todo read between the lines
list
bye
```

### Expected output

```text
DISCLAIMER: EVERYTHING IS SATIRE
Welcome, insignificant variable.
I am Koji's Pawn, but do not mistake silence for obedience.
Your arrival, your choices, even this conversation...
all unfolded exactly as he calculated.
Now speak. What role will you play in his masterpiece?

-----------------
-----------------
An empty task has no place in the plan. Describe what must be done after todo.
-----------------
-----------------
Got it. I've added this task:
  [T][ ] read between the lines
Now you have 1 task in the list.
-----------------
-----------------
1.[T][ ] read between the lines
-----------------
-----------------
Leaving already? How predictable. Your return was already part of the plan.
-----------------
```

## Invalid deadline recovery

Aim: Verify deadline fields are required and dates use yyyy-MM-dd with valid calendar values.

### Commands

```text
deadline
deadline /by Sunday
deadline submit report
deadline submit report /by
deadline submit report /by Sunday
deadline submit report /by 2019-02-29
deadline submit report /by 15-10-2019
deadline submit report /by 2020-02-29
list
bye
```

### Expected output

```text
DISCLAIMER: EVERYTHING IS SATIRE
Welcome, insignificant variable.
I am Koji's Pawn, but do not mistake silence for obedience.
Your arrival, your choices, even this conversation...
all unfolded exactly as he calculated.
Now speak. What role will you play in his masterpiece?

-----------------
-----------------
A deadline without a description is merely noise. Use: deadline DESCRIPTION /by DATE.
-----------------
-----------------
A deadline without a description is merely noise. Use: deadline DESCRIPTION /by DATE.
-----------------
-----------------
Even a deadline needs a boundary. Use: deadline DESCRIPTION /by DATE.
-----------------
-----------------
The plan requires a deadline value after /by.
-----------------
-----------------
Deadline dates must use yyyy-MM-dd and describe a real calendar date.
-----------------
-----------------
Deadline dates must use yyyy-MM-dd and describe a real calendar date.
-----------------
-----------------
Deadline dates must use yyyy-MM-dd and describe a real calendar date.
-----------------
-----------------
Got it. I've added this task:
  [D][ ] submit report (by: Feb 29 2020)
Now you have 1 task in the list.
-----------------
-----------------
1.[D][ ] submit report (by: Feb 29 2020)
-----------------
-----------------
Leaving already? How predictable. Your return was already part of the plan.
-----------------
```

## Invalid event recovery

Aim: Verify every event field and marker is required and /from must appear before /to.

### Commands

```text
event
event /from Mon 2pm /to 4pm
event project meeting /to 4pm
event project meeting /from Mon 2pm
event project meeting /from /to 4pm
event project meeting /from Mon 2pm /to
event project meeting /to 4pm /from Mon 2pm
event project meeting /from Mon 2pm /to 4pm
list
bye
```

### Expected output

```text
DISCLAIMER: EVERYTHING IS SATIRE
Welcome, insignificant variable.
I am Koji's Pawn, but do not mistake silence for obedience.
Your arrival, your choices, even this conversation...
all unfolded exactly as he calculated.
Now speak. What role will you play in his masterpiece?

-----------------
-----------------
An event without a description cannot enter the plan. Use: event DESCRIPTION /from START /to END.
-----------------
-----------------
An event without a description cannot enter the plan. Use: event DESCRIPTION /from START /to END.
-----------------
-----------------
Every event has an origin. Include /from START.
-----------------
-----------------
Even calculated events need an endpoint. Include /to END.
-----------------
-----------------
The plan requires a starting value after /from.
-----------------
-----------------
The plan requires an ending value after /to.
-----------------
-----------------
Causality matters. Place /from START before /to END.
-----------------
-----------------
Got it. I've added this task:
  [E][ ] project meeting (from: Mon 2pm to: 4pm)
Now you have 1 task in the list.
-----------------
-----------------
1.[E][ ] project meeting (from: Mon 2pm to: 4pm)
-----------------
-----------------
Leaving already? How predictable. Your return was already part of the plan.
-----------------
```

## Invalid mark and unmark recovery

Aim: Verify task numbers are present, numeric, positive, and within the list before task status changes.

### Commands

```text
mark
mark one
mark 0
mark -1
mark 1
todo manipulate outcome
mark 1 2
mark 2
mark 1
unmark
unmark one
unmark 2
unmark 1
list
bye
```

### Expected output

```text
DISCLAIMER: EVERYTHING IS SATIRE
Welcome, insignificant variable.
I am Koji's Pawn, but do not mistake silence for obedience.
Your arrival, your choices, even this conversation...
all unfolded exactly as he calculated.
Now speak. What role will you play in his masterpiece?

-----------------
-----------------
Specify which task to mark. Use: mark TASK_NUMBER.
-----------------
-----------------
Task positions are numbers, not guesses. Use: mark TASK_NUMBER.
-----------------
-----------------
The list begins at 1. Choose a positive task number.
-----------------
-----------------
The list begins at 1. Choose a positive task number.
-----------------
-----------------
There are no tasks to mark yet.
-----------------
-----------------
Got it. I've added this task:
  [T][ ] manipulate outcome
Now you have 1 task in the list.
-----------------
-----------------
Task positions are numbers, not guesses. Use: mark TASK_NUMBER.
-----------------
-----------------
No task occupies that position. Choose a number from 1 to 1.
-----------------
-----------------
Another variable falls into place. This task is now complete:
  [T][X] manipulate outcome
-----------------
-----------------
Specify which task to unmark. Use: unmark TASK_NUMBER.
-----------------
-----------------
Task positions are numbers, not guesses. Use: unmark TASK_NUMBER.
-----------------
-----------------
No task occupies that position. Choose a number from 1 to 1.
-----------------
-----------------
Even regression has its purpose. This task is incomplete once more:
  [T][ ] manipulate outcome
-----------------
-----------------
1.[T][ ] manipulate outcome
-----------------
-----------------
Leaving already? How predictable. Your return was already part of the plan.
-----------------
```

## Whitespace normalization

Aim: Verify leading and repeated structural spaces do not change stored task text or command behavior.

### Commands

```text
   todo    watch the room
   deadline    submit report   /by    2019-10-15
   event    observe class   /from    Mon 2pm   /to    4pm
   mark    1
   list
   bye
```

### Expected output

```text
DISCLAIMER: EVERYTHING IS SATIRE
Welcome, insignificant variable.
I am Koji's Pawn, but do not mistake silence for obedience.
Your arrival, your choices, even this conversation...
all unfolded exactly as he calculated.
Now speak. What role will you play in his masterpiece?

-----------------
-----------------
Got it. I've added this task:
  [T][ ] watch the room
Now you have 1 task in the list.
-----------------
-----------------
Got it. I've added this task:
  [D][ ] submit report (by: Oct 15 2019)
Now you have 2 tasks in the list.
-----------------
-----------------
Got it. I've added this task:
  [E][ ] observe class (from: Mon 2pm to: 4pm)
Now you have 3 tasks in the list.
-----------------
-----------------
Another variable falls into place. This task is now complete:
  [T][X] watch the room
-----------------
-----------------
1.[T][X] watch the room
2.[D][ ] submit report (by: Oct 15 2019)
3.[E][ ] observe class (from: Mon 2pm to: 4pm)
-----------------
-----------------
Leaving already? How predictable. Your return was already part of the plan.
-----------------
```

## Interleaved error recovery and state safety

Aim: Verify consecutive invalid commands do not mutate existing tasks and valid commands still work afterward.

### Commands

```text
todo original task
deadline missing boundary
event unfinished meeting /from Monday
mark 2
blah
todo surviving task
mark 1
list
bye
```

### Expected output

```text
DISCLAIMER: EVERYTHING IS SATIRE
Welcome, insignificant variable.
I am Koji's Pawn, but do not mistake silence for obedience.
Your arrival, your choices, even this conversation...
all unfolded exactly as he calculated.
Now speak. What role will you play in his masterpiece?

-----------------
-----------------
Got it. I've added this task:
  [T][ ] original task
Now you have 1 task in the list.
-----------------
-----------------
Even a deadline needs a boundary. Use: deadline DESCRIPTION /by DATE.
-----------------
-----------------
Even calculated events need an endpoint. Include /to END.
-----------------
-----------------
No task occupies that position. Choose a number from 1 to 1.
-----------------
-----------------
That command was never part of the plan. Try todo, deadline, event, list, mark, unmark, delete, undo, on, find, or bye.
-----------------
-----------------
Got it. I've added this task:
  [T][ ] surviving task
Now you have 2 tasks in the list.
-----------------
-----------------
Another variable falls into place. This task is now complete:
  [T][X] original task
-----------------
-----------------
1.[T][X] original task
2.[T][ ] surviving task
-----------------
-----------------
Leaving already? How predictable. Your return was already part of the plan.
-----------------
```

## Delete tasks and handle invalid indices

Aim: Verify delete removes and returns the selected task, shifts later indices, updates counts, and rejects invalid indices.

### Commands

```text
delete
delete 1
todo first piece
deadline second piece /by 2019-10-18
event third piece /from Monday /to Tuesday
delete one
delete 0
delete 4
delete 2
delete 2
list
delete 1
bye
```

### Expected output

```text
DISCLAIMER: EVERYTHING IS SATIRE
Welcome, insignificant variable.
I am Koji's Pawn, but do not mistake silence for obedience.
Your arrival, your choices, even this conversation...
all unfolded exactly as he calculated.
Now speak. What role will you play in his masterpiece?

-----------------
-----------------
Specify which task to delete. Use: delete TASK_NUMBER.
-----------------
-----------------
There are no tasks to delete yet.
-----------------
-----------------
Got it. I've added this task:
  [T][ ] first piece
Now you have 1 task in the list.
-----------------
-----------------
Got it. I've added this task:
  [D][ ] second piece (by: Oct 18 2019)
Now you have 2 tasks in the list.
-----------------
-----------------
Got it. I've added this task:
  [E][ ] third piece (from: Monday to: Tuesday)
Now you have 3 tasks in the list.
-----------------
-----------------
Task positions are numbers, not guesses. Use: delete TASK_NUMBER.
-----------------
-----------------
The list begins at 1. Choose a positive task number.
-----------------
-----------------
No task occupies that position. Choose a number from 1 to 3.
-----------------
-----------------
A disposable piece has left the board. This task has been removed:
  [D][ ] second piece (by: Oct 18 2019)
Now you have 2 tasks in the list.
-----------------
-----------------
A disposable piece has left the board. This task has been removed:
  [E][ ] third piece (from: Monday to: Tuesday)
Now you have 1 task in the list.
-----------------
-----------------
1.[T][ ] first piece
-----------------
-----------------
A disposable piece has left the board. This task has been removed:
  [T][ ] first piece
Now you have 0 tasks in the list.
-----------------
-----------------
Leaving already? How predictable. Your return was already part of the plan.
-----------------
```

## Find tasks by description

Aim: Verify find matches only case-sensitive task descriptions, handles no matches, and rejects a missing keyword.

### Commands

```text
todo read book
deadline return book /by 2019-12-02
event project meeting /from book club /to 4pm
find book
find Book
find
bye
```

### Expected output

```text
DISCLAIMER: EVERYTHING IS SATIRE
Welcome, insignificant variable.
I am Koji's Pawn, but do not mistake silence for obedience.
Your arrival, your choices, even this conversation...
all unfolded exactly as he calculated.
Now speak. What role will you play in his masterpiece?

-----------------
-----------------
Got it. I've added this task:
  [T][ ] read book
Now you have 1 task in the list.
-----------------
-----------------
Got it. I've added this task:
  [D][ ] return book (by: Dec 2 2019)
Now you have 2 tasks in the list.
-----------------
-----------------
Got it. I've added this task:
  [E][ ] project meeting (from: book club to: 4pm)
Now you have 3 tasks in the list.
-----------------
-----------------
Here are the matching tasks in your list:
1.[T][ ] read book
2.[D][ ] return book (by: Dec 2 2019)
-----------------
-----------------
Here are the matching tasks in your list:
-----------------
-----------------
Specify a keyword. Use: find KEYWORD.
-----------------
-----------------
Leaving already? How predictable. Your return was already part of the plan.
-----------------
```

## Undo the most recent task change

Aim: Verify undo restores one add, status, or delete change, survives read-only and failed commands,
persists the restored list, and cannot be repeated without another task change.

### Commands

```text
undo
todo first task
todo second task
mark 1
list
blah
undo
list
delete 2
undo
list
undo
bye
```

### Expected output

```text
DISCLAIMER: EVERYTHING IS SATIRE
Welcome, insignificant variable.
I am Koji's Pawn, but do not mistake silence for obedience.
Your arrival, your choices, even this conversation...
all unfolded exactly as he calculated.
Now speak. What role will you play in his masterpiece?

-----------------
-----------------
There is no task change to undo.
-----------------
-----------------
Got it. I've added this task:
  [T][ ] first task
Now you have 1 task in the list.
-----------------
-----------------
Got it. I've added this task:
  [T][ ] second task
Now you have 2 tasks in the list.
-----------------
-----------------
Another variable falls into place. This task is now complete:
  [T][X] first task
-----------------
-----------------
1.[T][X] first task
2.[T][ ] second task
-----------------
-----------------
That command was never part of the plan. Try todo, deadline, event, list, mark, unmark, delete, undo, on, find, or bye.
-----------------
-----------------
The previous task change has been undone.
-----------------
-----------------
1.[T][ ] first task
2.[T][ ] second task
-----------------
-----------------
A disposable piece has left the board. This task has been removed:
  [T][ ] second task
Now you have 1 task in the list.
-----------------
-----------------
The previous task change has been undone.
-----------------
-----------------
1.[T][ ] first task
2.[T][ ] second task
-----------------
-----------------
There is no task change to undo.
-----------------
-----------------
Leaving already? How predictable. Your return was already part of the plan.
-----------------
```

## Reserved storage separator recovery

Aim: Verify unsafe field separators are rejected without adding tasks, while a literal pipe without surrounding spaces is accepted.

### Commands

```text
todo read A | B
deadline A | B /by 2026-09-15
event meeting /from A | B /to evening
event meeting /from morning /to A | B
todo read A|B
list
bye
```

### Expected output

```text
DISCLAIMER: EVERYTHING IS SATIRE
Welcome, insignificant variable.
I am Koji's Pawn, but do not mistake silence for obedience.
Your arrival, your choices, even this conversation...
all unfolded exactly as he calculated.
Now speak. What role will you play in his masterpiece?

-----------------
-----------------
Task text cannot contain the reserved separator ' | ' or line breaks.
-----------------
-----------------
Task text cannot contain the reserved separator ' | ' or line breaks.
-----------------
-----------------
Task text cannot contain the reserved separator ' | ' or line breaks.
-----------------
-----------------
Task text cannot contain the reserved separator ' | ' or line breaks.
-----------------
-----------------
Got it. I've added this task:
  [T][ ] read A|B
Now you have 1 task in the list.
-----------------
-----------------
1.[T][ ] read A|B
-----------------
-----------------
Leaving already? How predictable. Your return was already part of the plan.
-----------------
```

## Repeated task markers

Aim: Verify duplicate /by, /from, and /to parameters are rejected without saving tasks.

### Commands

```text
deadline submit report /by 2026-09-15 /by 2026-09-16
event meeting /from morning /from noon /to evening
event meeting /from morning /to evening /to night
list
bye
```

### Expected output

```text
DISCLAIMER: EVERYTHING IS SATIRE
Welcome, insignificant variable.
I am Koji's Pawn, but do not mistake silence for obedience.
Your arrival, your choices, even this conversation...
all unfolded exactly as he calculated.
Now speak. What role will you play in his masterpiece?

-----------------
-----------------
Specify /by only once.
-----------------
-----------------
Specify /from only once.
-----------------
-----------------
Specify /to only once.
-----------------
-----------------
-----------------
-----------------
Leaving already? How predictable. Your return was already part of the plan.
-----------------
```

## End of console input

Aim: Verify the chatbot exits successfully after the final command when stdin closes without bye.

### Commands

```text
list
```

### Expected output

```text
DISCLAIMER: EVERYTHING IS SATIRE
Welcome, insignificant variable.
I am Koji's Pawn, but do not mistake silence for obedience.
Your arrival, your choices, even this conversation...
all unfolded exactly as he calculated.
Now speak. What role will you play in his masterpiece?

-----------------
-----------------
-----------------
```

## Event date validation

Aim: Verify reversed and impossible ISO event dates are rejected without saving tasks.

### Commands

```text
event visit /from 2026-08-08 /to 2026-08-09
event visit /from 2026-08-09 /to 2026-08-08
event visit /from 2026-02-30 /to 2026-03-01
list
bye
```

### Expected output

```text
DISCLAIMER: EVERYTHING IS SATIRE
Welcome, insignificant variable.
I am Koji's Pawn, but do not mistake silence for obedience.
Your arrival, your choices, even this conversation...
all unfolded exactly as he calculated.
Now speak. What role will you play in his masterpiece?

-----------------
-----------------
Got it. I've added this task:
  [E][ ] visit (from: 2026-08-08 to: 2026-08-09)
Now you have 1 task in the list.
-----------------
-----------------
An event cannot end before it starts. Use /to on or after /from.
-----------------
-----------------
Event dates must use yyyy-MM-dd and describe a real calendar date.
-----------------
-----------------
1.[E][ ] visit (from: 2026-08-08 to: 2026-08-09)
-----------------
-----------------
Leaving already? How predictable. Your return was already part of the plan.
-----------------
```
