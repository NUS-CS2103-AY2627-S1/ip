# Console UI Test Plan

Run this plan with the project-specific `test-ui` skill. Expected output is
compared exactly, including spaces, separators, and line order.

Cases may include `Initial data:` and `Expected data:` blocks for
`data/choo.txt`. Each case runs in a separate temporary working directory.

## Test case: Load and automatically save tasks

Aim: Verify startup loading and saving after mark, unmark, delete, and add operations.

Initial data:
```text
T | 1 | loaded todo
D | 0 | loaded deadline | 2019-12-06 0000
E | 0 | loaded event | Monday | Tuesday
```

Input:
```text
list
mark 2
unmark 1
delete 2
todo new | task
list
bye
```

Expected output:
```text
____________________________________________________________
##### #   # ##### #####
#     #   # #   # #   #
#     ##### #   # #   #
#     #   # #   # #   #
##### #   # ##### #####
All aboard! I'm CHOO, your task conductor.
Tell me what needs to stay on track.
____________________________________________________________
Here is your task itinerary:
1.[T][X] loaded todo
2.[D][ ] loaded deadline (by: Dec 6 2019, 12:00AM)
3.[E][ ] loaded event (from: Monday to: Tuesday)
____________________________________________________________
On track! I've marked this task as done:
  [D][X] loaded deadline (by: Dec 6 2019, 12:00AM)
____________________________________________________________
Back on the route. I've marked this task as not done:
  [T][ ] loaded todo
____________________________________________________________
Route updated. I've removed this task:
  [D][X] loaded deadline (by: Dec 6 2019, 12:00AM)
Your itinerary now has 2 tasks.
____________________________________________________________
Ticket issued. I've added this task:
  [T][ ] new | task
Your itinerary now has 3 tasks.
____________________________________________________________
Here is your task itinerary:
1.[T][ ] loaded todo
2.[E][ ] loaded event (from: Monday to: Tuesday)
3.[T][ ] new | task
____________________________________________________________
End of the line for now. Safe travels!
____________________________________________________________
```

Expected data:
```text
T | 0 | loaded todo
E | 0 | loaded event | Monday | Tuesday
T | 0 | new \| task
```

## Test case: Sort deadlines chronologically

Aim: Verify sorting orders deadlines, preserves undated task order, and saves the reordered list.

Initial data:
```text
T | 0 | first undated
D | 0 | later deadline | 2026-12-31
E | 0 | second undated | Monday | Tuesday
D | 0 | morning deadline | 2026-01-15 0900
D | 0 | date-only deadline | 2026-01-15
```

Input:
```text
sort
list
bye
```

Expected output:
```text
____________________________________________________________
##### #   # ##### #####
#     #   # #   # #   #
#     ##### #   # #   #
#     #   # #   # #   #
##### #   # ##### #####
All aboard! I'm CHOO, your task conductor.
Tell me what needs to stay on track.
____________________________________________________________
Timetable sorted by deadline:
1.[D][ ] date-only deadline (by: Jan 15 2026)
2.[D][ ] morning deadline (by: Jan 15 2026, 9:00AM)
3.[D][ ] later deadline (by: Dec 31 2026)
4.[T][ ] first undated
5.[E][ ] second undated (from: Monday to: Tuesday)
____________________________________________________________
Here is your task itinerary:
1.[D][ ] date-only deadline (by: Jan 15 2026)
2.[D][ ] morning deadline (by: Jan 15 2026, 9:00AM)
3.[D][ ] later deadline (by: Dec 31 2026)
4.[T][ ] first undated
5.[E][ ] second undated (from: Monday to: Tuesday)
____________________________________________________________
End of the line for now. Safe travels!
____________________________________________________________
```

Expected data:
```text
D | 0 | date-only deadline | 2026-01-15
D | 0 | morning deadline | 2026-01-15 0900
D | 0 | later deadline | 2026-12-31
T | 0 | first undated
E | 0 | second undated | Monday | Tuesday
```

## Test case: Reject malformed command structures

Aim: Verify malformed command structures are rejected without changing task state.

Input:
```text
todo keep this

list now
sort later
bye please
deadline report /by 2019-12-02 /by 2019-12-03
event meeting /from Mon /from Tue /to Wed
event meeting /from Mon /to Tue /to Wed
event meeting /to Tue /from Mon
list
bye
```

Expected output:
```text
____________________________________________________________
##### #   # ##### #####
#     #   # #   # #   #
#     ##### #   # #   #
#     #   # #   # #   #
##### #   # ##### #####
All aboard! I'm CHOO, your task conductor.
Tell me what needs to stay on track.
____________________________________________________________
Ticket issued. I've added this task:
  [T][ ] keep this
Your itinerary now has 1 task.
____________________________________________________________
OOPS!!! Signal problem: A command cannot be empty.
____________________________________________________________
OOPS!!! Signal problem: The list command does not accept extra details.
____________________________________________________________
OOPS!!! Signal problem: The sort command does not accept extra details.
____________________________________________________________
OOPS!!! Signal problem: The bye command does not accept extra details.
____________________________________________________________
OOPS!!! Signal problem: A deadline needs exactly one /by value.
____________________________________________________________
OOPS!!! Signal problem: An event needs exactly one /from followed by exactly one /to value.
____________________________________________________________
OOPS!!! Signal problem: An event needs exactly one /from followed by exactly one /to value.
____________________________________________________________
OOPS!!! Signal problem: An event needs exactly one /from followed by exactly one /to value.
____________________________________________________________
Here is your task itinerary:
1.[T][ ] keep this
____________________________________________________________
End of the line for now. Safe travels!
____________________________________________________________
```

## Test case: Level 4 task types

Aim: Verify task parsing, formatted deadline dates and times, task counts, marking, listing, and exit.

Input:
```text
todo borrow book
deadline return book /by 2019-12-06
deadline do homework /by 2019-12-07 1800
event project meeting /from Mon 2pm /to 4pm
mark 2
list
bye
```

Expected output:
```text
____________________________________________________________
##### #   # ##### #####
#     #   # #   # #   #
#     ##### #   # #   #
#     #   # #   # #   #
##### #   # ##### #####
All aboard! I'm CHOO, your task conductor.
Tell me what needs to stay on track.
____________________________________________________________
Ticket issued. I've added this task:
  [T][ ] borrow book
Your itinerary now has 1 task.
____________________________________________________________
Ticket issued. I've added this task:
  [D][ ] return book (by: Dec 6 2019)
Your itinerary now has 2 tasks.
____________________________________________________________
Ticket issued. I've added this task:
  [D][ ] do homework (by: Dec 7 2019, 6:00PM)
Your itinerary now has 3 tasks.
____________________________________________________________
Ticket issued. I've added this task:
  [E][ ] project meeting (from: Mon 2pm to: 4pm)
Your itinerary now has 4 tasks.
____________________________________________________________
On track! I've marked this task as done:
  [D][X] return book (by: Dec 6 2019)
____________________________________________________________
Here is your task itinerary:
1.[T][ ] borrow book
2.[D][X] return book (by: Dec 6 2019)
3.[D][ ] do homework (by: Dec 7 2019, 6:00PM)
4.[E][ ] project meeting (from: Mon 2pm to: 4pm)
____________________________________________________________
End of the line for now. Safe travels!
____________________________________________________________
```

## Test case: Delete tasks and reject invalid positions

Aim: Verify deletion removes the selected task, renumbers the list, and leaves state unchanged after invalid positions.

Input:
```text
todo first
deadline second /by 2019-12-06
event third /from Monday /to Tuesday
delete
delete two
delete 0
delete -1
delete 4
delete 2
list
delete 1
list
bye
```

Expected output:
```text
____________________________________________________________
##### #   # ##### #####
#     #   # #   # #   #
#     ##### #   # #   #
#     #   # #   # #   #
##### #   # ##### #####
All aboard! I'm CHOO, your task conductor.
Tell me what needs to stay on track.
____________________________________________________________
Ticket issued. I've added this task:
  [T][ ] first
Your itinerary now has 1 task.
____________________________________________________________
Ticket issued. I've added this task:
  [D][ ] second (by: Dec 6 2019)
Your itinerary now has 2 tasks.
____________________________________________________________
Ticket issued. I've added this task:
  [E][ ] third (from: Monday to: Tuesday)
Your itinerary now has 3 tasks.
____________________________________________________________
OOPS!!! Signal problem: Enter a whole-number task position after delete.
____________________________________________________________
OOPS!!! Signal problem: Enter a whole-number task position after delete.
____________________________________________________________
OOPS!!! Signal problem: Task number 0 is outside the list.
____________________________________________________________
OOPS!!! Signal problem: Task number -1 is outside the list.
____________________________________________________________
OOPS!!! Signal problem: Task number 4 is outside the list.
____________________________________________________________
Route updated. I've removed this task:
  [D][ ] second (by: Dec 6 2019)
Your itinerary now has 2 tasks.
____________________________________________________________
Here is your task itinerary:
1.[T][ ] first
2.[E][ ] third (from: Monday to: Tuesday)
____________________________________________________________
Route updated. I've removed this task:
  [T][ ] first
Your itinerary now has 1 task.
____________________________________________________________
Here is your task itinerary:
1.[E][ ] third (from: Monday to: Tuesday)
____________________________________________________________
End of the line for now. Safe travels!
____________________________________________________________
```

## Test case: Invalid task details preserve valid state

Aim: Verify descriptions and schedule delimiters are validated while successful tasks remain unchanged.

Input:
```text
todo
mystery command
todo keep this
deadline report
deadline /by Friday
deadline report /by
deadline report /by Friday
deadline report /by 2019-02-29
deadline report /by 2019-12-06
event /from Monday /to Tuesday
event meeting /from Monday
event meeting /to Tuesday
event meeting /from Monday /to
event meeting /from Monday /to Tuesday
list
bye
```

Expected output:
```text
____________________________________________________________
##### #   # ##### #####
#     #   # #   # #   #
#     ##### #   # #   #
#     #   # #   # #   #
##### #   # ##### #####
All aboard! I'm CHOO, your task conductor.
Tell me what needs to stay on track.
____________________________________________________________
OOPS!!! Signal problem: A todo needs a description.
____________________________________________________________
OOPS!!! Signal problem: I don't recognize that command.
____________________________________________________________
Ticket issued. I've added this task:
  [T][ ] keep this
Your itinerary now has 1 task.
____________________________________________________________
OOPS!!! Signal problem: A deadline needs a /by date or time.
____________________________________________________________
OOPS!!! Signal problem: A deadline needs a description.
____________________________________________________________
OOPS!!! Signal problem: A deadline needs a /by date or time.
____________________________________________________________
OOPS!!! Signal problem: Use yyyy-MM-dd or yyyy-MM-dd HHmm for a deadline date.
____________________________________________________________
OOPS!!! Signal problem: Use yyyy-MM-dd or yyyy-MM-dd HHmm for a deadline date.
____________________________________________________________
Ticket issued. I've added this task:
  [D][ ] report (by: Dec 6 2019)
Your itinerary now has 2 tasks.
____________________________________________________________
OOPS!!! Signal problem: An event needs a description.
____________________________________________________________
OOPS!!! Signal problem: An event needs both /from and /to values.
____________________________________________________________
OOPS!!! Signal problem: An event needs both /from and /to values.
____________________________________________________________
OOPS!!! Signal problem: An event needs both /from and /to values.
____________________________________________________________
Ticket issued. I've added this task:
  [E][ ] meeting (from: Monday to: Tuesday)
Your itinerary now has 3 tasks.
____________________________________________________________
Here is your task itinerary:
1.[T][ ] keep this
2.[D][ ] report (by: Dec 6 2019)
3.[E][ ] meeting (from: Monday to: Tuesday)
____________________________________________________________
End of the line for now. Safe travels!
____________________________________________________________
```

## Test case: Invalid task numbers preserve completion state

Aim: Verify missing, non-numeric, and out-of-range positions are rejected without changing task status.

Input:
```text
todo first
mark
mark two
mark 2
mark 1
unmark
unmark 0
unmark 1
list
bye
```

Expected output:
```text
____________________________________________________________
##### #   # ##### #####
#     #   # #   # #   #
#     ##### #   # #   #
#     #   # #   # #   #
##### #   # ##### #####
All aboard! I'm CHOO, your task conductor.
Tell me what needs to stay on track.
____________________________________________________________
Ticket issued. I've added this task:
  [T][ ] first
Your itinerary now has 1 task.
____________________________________________________________
OOPS!!! Signal problem: Enter a whole-number task position after mark.
____________________________________________________________
OOPS!!! Signal problem: Enter a whole-number task position after mark.
____________________________________________________________
OOPS!!! Signal problem: Task number 2 is outside the list.
____________________________________________________________
On track! I've marked this task as done:
  [T][X] first
____________________________________________________________
OOPS!!! Signal problem: Enter a whole-number task position after unmark.
____________________________________________________________
OOPS!!! Signal problem: Task number 0 is outside the list.
____________________________________________________________
Back on the route. I've marked this task as not done:
  [T][ ] first
____________________________________________________________
Here is your task itinerary:
1.[T][ ] first
____________________________________________________________
End of the line for now. Safe travels!
____________________________________________________________
```

## Test case: Find matching task descriptions

Aim: Verify ordered, case-sensitive, description-only searches, empty results, and invalid find commands.

Input:
```text
todo read book
deadline return book /by 2019-12-06
event book club /from Monday /to Tuesday
todo BOOK notes
find book
find 2019
find BOOK
find
list
bye
```

Expected output:
```text
____________________________________________________________
##### #   # ##### #####
#     #   # #   # #   #
#     ##### #   # #   #
#     #   # #   # #   #
##### #   # ##### #####
All aboard! I'm CHOO, your task conductor.
Tell me what needs to stay on track.
____________________________________________________________
Ticket issued. I've added this task:
  [T][ ] read book
Your itinerary now has 1 task.
____________________________________________________________
Ticket issued. I've added this task:
  [D][ ] return book (by: Dec 6 2019)
Your itinerary now has 2 tasks.
____________________________________________________________
Ticket issued. I've added this task:
  [E][ ] book club (from: Monday to: Tuesday)
Your itinerary now has 3 tasks.
____________________________________________________________
Ticket issued. I've added this task:
  [T][ ] BOOK notes
Your itinerary now has 4 tasks.
____________________________________________________________
Here are the matching stops:
1.[T][ ] read book
2.[D][ ] return book (by: Dec 6 2019)
3.[E][ ] book club (from: Monday to: Tuesday)
____________________________________________________________
Here are the matching stops:
____________________________________________________________
Here are the matching stops:
1.[T][ ] BOOK notes
____________________________________________________________
OOPS!!! Signal problem: A find command needs a keyword.
____________________________________________________________
Here is your task itinerary:
1.[T][ ] read book
2.[D][ ] return book (by: Dec 6 2019)
3.[E][ ] book club (from: Monday to: Tuesday)
4.[T][ ] BOOK notes
____________________________________________________________
End of the line for now. Safe travels!
____________________________________________________________
```
