# Console UI test record

Java 25.0.3.fx-zulu; exact combined output comparison; isolated process and directory per case.

## TC-01: Start and exit cleanly — PASS

**Aim:** Verify that the application shows its greeting and exit message when the user exits immediately.

**Input:**

```text
bye
```

**Actual output:**

```text
____________________________________________________________
James the ぱんどろぼう
Shh! I'm James, your bread thief.
I'll guard your tasks. The bread? No promises!
____________________________________________________________

____________________________________________________________
Mata ne! Rest up. I smell fresh bread!
____________________________________________________________
```

## TC-13: Find tasks by keyword — PASS

**Aim:** Verify that `find` returns case-insensitive partial matches across task types,
preserves insertion order, and reports no-match searches without changing the task list.

**Input:**

```text
todo read book
deadline return book /by 2019-06-06
find BOOK
find magazine
bye
```

**Actual output:**

```text
____________________________________________________________
James the ぱんどろぼう
Shh! I'm James, your bread thief.
I'll guard your tasks. The bread? No promises!
____________________________________________________________

____________________________________________________________
Hehe! Tucked this task into my bread basket:
[T][ ] read book
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Hehe! Tucked this task into my bread basket:
[D][ ] return book (by: Jun 06 2019)
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Sniff sniff... here are the matching tasks:
1.[T][ ] read book
2.[D][ ] return book (by: Jun 06 2019)
____________________________________________________________
____________________________________________________________
Sniff sniff... here are the matching tasks:
____________________________________________________________
____________________________________________________________
Mata ne! Rest up. I smell fresh bread!
____________________________________________________________
```

## TC-02: List an empty task list — PASS

**Aim:** Verify that listing tasks before any task is added displays the empty-list heading without creating a task.

**Input:**

```text
list
bye
```

**Actual output:**

```text
____________________________________________________________
James the ぱんどろぼう
Shh! I'm James, your bread thief.
I'll guard your tasks. The bread? No promises!
____________________________________________________________

____________________________________________________________
Let's peek in the basket. Your tasks:
____________________________________________________________
____________________________________________________________
Mata ne! Rest up. I smell fresh bread!
____________________________________________________________
```

## TC-03: Reject invalid delete commands without changing the task list — PASS

**Aim:** Verify that deletion rejects a missing, non-numeric, zero, and out-of-range task number; the valid task must remain in the list after every error.

**Input:**

```text
todo protect task
delete
list
delete one
list
delete 0
list
delete 2
list
bye
```

**Actual output:**

```text
____________________________________________________________
James the ぱんどろぼう
Shh! I'm James, your bread thief.
I'll guard your tasks. The bread? No promises!
____________________________________________________________

____________________________________________________________
Hehe! Tucked this task into my bread basket:
[T][ ] protect task
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Gomen! A little flour in the gears.
Which task shall I fetch? Please provide a task number.
Try: delete <task number>
____________________________________________________________
____________________________________________________________
Let's peek in the basket. Your tasks:
1.[T][ ] protect task
____________________________________________________________
____________________________________________________________
Gomen! A little flour in the gears.
No half-slices here! The task number must be a whole number.
Try: delete <task number>
____________________________________________________________
____________________________________________________________
Let's peek in the basket. Your tasks:
1.[T][ ] protect task
____________________________________________________________
____________________________________________________________
Gomen! A little flour in the gears.
I can't find task number 0.
Your list currently has 1 tasks.
____________________________________________________________
____________________________________________________________
Let's peek in the basket. Your tasks:
1.[T][ ] protect task
____________________________________________________________
____________________________________________________________
Gomen! A little flour in the gears.
I can't find task number 2.
Your list currently has 1 tasks.
____________________________________________________________
____________________________________________________________
Let's peek in the basket. Your tasks:
1.[T][ ] protect task
____________________________________________________________
____________________________________________________________
Mata ne! Rest up. I smell fresh bread!
____________________________________________________________
```

## TC-04: Reject invalid mark and unmark commands without changing task status — PASS

**Aim:** Verify that invalid mark and unmark task numbers preserve the task and its incomplete status; valid mark and unmark commands must still work afterwards.

**Input:**

```text
todo persistent task
mark
unmark nope
mark 0
unmark 2
list
mark 1
unmark 1
list
bye
```

**Actual output:**

```text
____________________________________________________________
James the ぱんどろぼう
Shh! I'm James, your bread thief.
I'll guard your tasks. The bread? No promises!
____________________________________________________________

____________________________________________________________
Hehe! Tucked this task into my bread basket:
[T][ ] persistent task
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Gomen! A little flour in the gears.
Which task shall I fetch? Please provide a task number.
Try: mark <task number>
____________________________________________________________
____________________________________________________________
Gomen! A little flour in the gears.
No half-slices here! The task number must be a whole number.
Try: unmark <task number>
____________________________________________________________
____________________________________________________________
Gomen! A little flour in the gears.
I can't find task number 0.
Your list currently has 1 tasks.
____________________________________________________________
____________________________________________________________
Gomen! A little flour in the gears.
I can't find task number 2.
Your list currently has 1 tasks.
____________________________________________________________
____________________________________________________________
Let's peek in the basket. Your tasks:
1.[T][ ] persistent task
____________________________________________________________
____________________________________________________________
Yatta! Task done. Time for a bread break:
[T][X] persistent task
____________________________________________________________
____________________________________________________________
Back in the oven! This task is not done yet:
[T][ ] persistent task
____________________________________________________________
____________________________________________________________
Let's peek in the basket. Your tasks:
1.[T][ ] persistent task
____________________________________________________________
____________________________________________________________
Mata ne! Rest up. I smell fresh bread!
____________________________________________________________
```

## TC-05: Reject malformed deadline and event formats without creating tasks — PASS

**Aim:** Verify that deadlines without a description and events with missing descriptions, `/from`, or `/to` values are rejected; the final list must remain empty.

**Input:**

```text
deadline /by Sunday
event /from Monday /to Tuesday
event meeting
event meeting /from Monday /to
list
bye
```

**Actual output:**

```text
____________________________________________________________
James the ぱんどろぼう
Shh! I'm James, your bread thief.
I'll guard your tasks. The bread? No promises!
____________________________________________________________

____________________________________________________________
Gomen! A little flour in the gears.
A deadline needs a by date.
Try: deadline <description> /by <date>
____________________________________________________________
____________________________________________________________
Gomen! A little flour in the gears.
An event needs a description followed by /from.
Try: event <description> /from <start> /to <end>
____________________________________________________________
____________________________________________________________
Gomen! A little flour in the gears.
An event needs a description followed by /from.
Try: event <description> /from <start> /to <end>
____________________________________________________________
____________________________________________________________
Gomen! A little flour in the gears.
An event needs both a start and end time.
Try: event <description> /from <start> /to <end>
____________________________________________________________
____________________________________________________________
Let's peek in the basket. Your tasks:
____________________________________________________________
____________________________________________________________
Mata ne! Rest up. I smell fresh bread!
____________________________________________________________
```

## TC-06: Delete a task and renumber the remaining list — PASS

**Aim:** Verify that deleting a valid task removes the selected task, reports the updated task count, and preserves the remaining tasks in order.

**Input:**

```text
todo read book
deadline return book /by 2019-06-06
event project meeting /from 2019-08-06 /to 2019-08-07
todo join sports club
mark 1
mark 4
todo borrow book
list
delete 3
list
bye
```

**Actual output:**

```text
____________________________________________________________
James the ぱんどろぼう
Shh! I'm James, your bread thief.
I'll guard your tasks. The bread? No promises!
____________________________________________________________

____________________________________________________________
Hehe! Tucked this task into my bread basket:
[T][ ] read book
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Hehe! Tucked this task into my bread basket:
[D][ ] return book (by: Jun 06 2019)
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Hehe! Tucked this task into my bread basket:
[E][ ] project meeting (from: Aug 06 2019 to: Aug 07 2019)
Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
Hehe! Tucked this task into my bread basket:
[T][ ] join sports club
Now you have 4 tasks in the list.
____________________________________________________________
____________________________________________________________
Yatta! Task done. Time for a bread break:
[T][X] read book
____________________________________________________________
____________________________________________________________
Yatta! Task done. Time for a bread break:
[T][X] join sports club
____________________________________________________________
____________________________________________________________
Hehe! Tucked this task into my bread basket:
[T][ ] borrow book
Now you have 5 tasks in the list.
____________________________________________________________
____________________________________________________________
Let's peek in the basket. Your tasks:
1.[T][X] read book
2.[D][ ] return book (by: Jun 06 2019)
3.[E][ ] project meeting (from: Aug 06 2019 to: Aug 07 2019)
4.[T][X] join sports club
5.[T][ ] borrow book
____________________________________________________________
____________________________________________________________
Poof! Snatched this task out of the basket:
[E][ ] project meeting (from: Aug 06 2019 to: Aug 07 2019)
Now you have 4 tasks in the list.
____________________________________________________________
____________________________________________________________
Let's peek in the basket. Your tasks:
1.[T][X] read book
2.[D][ ] return book (by: Jun 06 2019)
3.[T][X] join sports club
4.[T][ ] borrow book
____________________________________________________________
____________________________________________________________
Mata ne! Rest up. I smell fresh bread!
____________________________________________________________
```

## TC-07: Add tasks, list them, and retain their details — PASS

**Aim:** Verify that tasks can be added, marked, listed, and that deadline and event details are displayed in the expected format.

**Input:**

```text
todo read book
deadline return book /by 2019-06-06
event project meeting /from 2019-08-06 /to 2019-08-07
todo join sports club
mark 1
mark 4
todo borrow book
list
deadline return book /by 2019-12-01
event project meeting /from 2019-08-12 /to 2019-08-13
bye
```

**Actual output:**

```text
____________________________________________________________
James the ぱんどろぼう
Shh! I'm James, your bread thief.
I'll guard your tasks. The bread? No promises!
____________________________________________________________

____________________________________________________________
Hehe! Tucked this task into my bread basket:
[T][ ] read book
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Hehe! Tucked this task into my bread basket:
[D][ ] return book (by: Jun 06 2019)
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Hehe! Tucked this task into my bread basket:
[E][ ] project meeting (from: Aug 06 2019 to: Aug 07 2019)
Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
Hehe! Tucked this task into my bread basket:
[T][ ] join sports club
Now you have 4 tasks in the list.
____________________________________________________________
____________________________________________________________
Yatta! Task done. Time for a bread break:
[T][X] read book
____________________________________________________________
____________________________________________________________
Yatta! Task done. Time for a bread break:
[T][X] join sports club
____________________________________________________________
____________________________________________________________
Hehe! Tucked this task into my bread basket:
[T][ ] borrow book
Now you have 5 tasks in the list.
____________________________________________________________
____________________________________________________________
Let's peek in the basket. Your tasks:
1.[T][X] read book
2.[D][ ] return book (by: Jun 06 2019)
3.[E][ ] project meeting (from: Aug 06 2019 to: Aug 07 2019)
4.[T][X] join sports club
5.[T][ ] borrow book
____________________________________________________________
____________________________________________________________
Hehe! Tucked this task into my bread basket:
[D][ ] return book (by: Dec 01 2019)
Now you have 6 tasks in the list.
____________________________________________________________
____________________________________________________________
Hehe! Tucked this task into my bread basket:
[E][ ] project meeting (from: Aug 12 2019 to: Aug 13 2019)
Now you have 7 tasks in the list.
____________________________________________________________
____________________________________________________________
Mata ne! Rest up. I smell fresh bread!
____________________________________________________________
```

## TC-08: Reject malformed commands without changing the task list — PASS

**Aim:** Verify that invalid deadline, event, and task-number inputs show the current error message, while valid operations before and after them leave the single task in the expected state.

**Input:**

```text
todo keep this task
deadline submit report
list
event team meeting /from Monday 2pm
list
mark two
mark 1
unmark 1
bye
```

**Actual output:**

```text
____________________________________________________________
James the ぱんどろぼう
Shh! I'm James, your bread thief.
I'll guard your tasks. The bread? No promises!
____________________________________________________________

____________________________________________________________
Hehe! Tucked this task into my bread basket:
[T][ ] keep this task
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Gomen! A little flour in the gears.
A deadline needs a by date.
Try: deadline <description> /by <date>
____________________________________________________________
____________________________________________________________
Let's peek in the basket. Your tasks:
1.[T][ ] keep this task
____________________________________________________________
____________________________________________________________
Gomen! A little flour in the gears.
An event needs both a start and end time.
Try: event <description> /from <start> /to <end>
____________________________________________________________
____________________________________________________________
Let's peek in the basket. Your tasks:
1.[T][ ] keep this task
____________________________________________________________
____________________________________________________________
Gomen! A little flour in the gears.
No half-slices here! The task number must be a whole number.
Try: mark <task number>
____________________________________________________________
____________________________________________________________
Yatta! Task done. Time for a bread break:
[T][X] keep this task
____________________________________________________________
____________________________________________________________
Back in the oven! This task is not done yet:
[T][ ] keep this task
____________________________________________________________
____________________________________________________________
Mata ne! Rest up. I smell fresh bread!
____________________________________________________________
```

## TC-09: Handle blank, unknown, and incomplete commands without changing the task list — PASS

**Aim:** Verify that a blank command, an unknown command, and a todo without a description report errors; valid commands interleaved between them must preserve the one valid task.

**Input:**

```text

todo retained task
unknown
list
todo
list
bye
```

**Actual output:**

```text
____________________________________________________________
James the ぱんどろぼう
Shh! I'm James, your bread thief.
I'll guard your tasks. The bread? No promises!
____________________________________________________________

____________________________________________________________
Gomen! A little flour in the gears.
No command specified
Try: <command> <arguments:optional>
____________________________________________________________
____________________________________________________________
Hehe! Tucked this task into my bread basket:
[T][ ] retained task
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Gomen! A little flour in the gears.
I don't know that recipe! Try list or todo <description>.
____________________________________________________________
____________________________________________________________
Let's peek in the basket. Your tasks:
1.[T][ ] retained task
____________________________________________________________
____________________________________________________________
Gomen! A little flour in the gears.
The description of a todo cannot be empty.
Try: todo <description>
____________________________________________________________
____________________________________________________________
Let's peek in the basket. Your tasks:
1.[T][ ] retained task
____________________________________________________________
____________________________________________________________
Mata ne! Rest up. I smell fresh bread!
____________________________________________________________
```

## TC-10: Reject invalid date format without creating tasks — PASS

**Aim:** Verify that deadlines and events with invalid or malformed dates are rejected with formatting guidance; no tasks must be added.

**Input:**

```text
deadline return book /by invalid-date
event team meeting /from 2019-02-30 /to 2019-03-01
list
bye
```

**Actual output:**

```text
____________________________________________________________
James the ぱんどろぼう
Shh! I'm James, your bread thief.
I'll guard your tasks. The bread? No promises!
____________________________________________________________

____________________________________________________________
Gomen! A little flour in the gears.
Formatting of the date is incorrect, try: yyyy-mm-dd
____________________________________________________________
____________________________________________________________
Gomen! A little flour in the gears.
Formatting of the date is incorrect, try: yyyy-mm-dd
____________________________________________________________
____________________________________________________________
Let's peek in the basket. Your tasks:
____________________________________________________________
____________________________________________________________
Mata ne! Rest up. I smell fresh bread!
____________________________________________________________
```

## TC-11: List tasks occurring on a specific date — PASS

**Aim:** Verify that list_by_date retrieves deadlines due on that date and events whose period covers that date (including intermediate dates), and shows an empty list if no tasks match.

**Input:**

```text
todo read book
deadline return book /by 2019-10-15
deadline submit report /by 2019-10-20
event career fair /from 2019-10-14 /to 2019-10-16
list_by_date 2019-10-15
list_by_date 2019-10-20
list_by_date 2019-10-18
bye
```

**Actual output:**

```text
____________________________________________________________
James the ぱんどろぼう
Shh! I'm James, your bread thief.
I'll guard your tasks. The bread? No promises!
____________________________________________________________

____________________________________________________________
Hehe! Tucked this task into my bread basket:
[T][ ] read book
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Hehe! Tucked this task into my bread basket:
[D][ ] return book (by: Oct 15 2019)
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Hehe! Tucked this task into my bread basket:
[D][ ] submit report (by: Oct 20 2019)
Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
Hehe! Tucked this task into my bread basket:
[E][ ] career fair (from: Oct 14 2019 to: Oct 16 2019)
Now you have 4 tasks in the list.
____________________________________________________________
____________________________________________________________
Tasks on the menu for 2019-10-15:
1.[D][ ] return book (by: Oct 15 2019)
2.[E][ ] career fair (from: Oct 14 2019 to: Oct 16 2019)
____________________________________________________________
____________________________________________________________
Tasks on the menu for 2019-10-20:
1.[D][ ] submit report (by: Oct 20 2019)
____________________________________________________________
____________________________________________________________
Tasks on the menu for 2019-10-18:
____________________________________________________________
____________________________________________________________
Mata ne! Rest up. I smell fresh bread!
____________________________________________________________
```

## TC-12: Reject invalid arguments for list_by_date — PASS

**Aim:** Verify that list_by_date rejects missing date arguments and invalid date formats with descriptive error messages.

**Input:**

```text
list_by_date
list_by_date 2019-13-01
list_by_date invalid-date
bye
```

**Actual output:**

```text
____________________________________________________________
James the ぱんどろぼう
Shh! I'm James, your bread thief.
I'll guard your tasks. The bread? No promises!
____________________________________________________________

____________________________________________________________
Gomen! A little flour in the gears.
Please provide a date in the format: yyyy-mm-dd
____________________________________________________________
____________________________________________________________
Gomen! A little flour in the gears.
The date format provided is incorrect! Please use the format: yyyy-mm-dd
____________________________________________________________
____________________________________________________________
Gomen! A little flour in the gears.
The date format provided is incorrect! Please use the format: yyyy-mm-dd
____________________________________________________________
____________________________________________________________
Mata ne! Rest up. I smell fresh bread!
____________________________________________________________
```

## TC-14: Undo changes in reverse order — PASS

**Aim:** Verify empty history, undoing mark/delete/add, restored completion status, and skipping searches and repeated marks.

**Input:**

```text
undo
todo read book
mark 1
mark 1
find book
undo
list
delete 1
undo
list
undo
undo
bye
```

**Actual output:**

```text
____________________________________________________________
James the ぱんどろぼう
Shh! I'm James, your bread thief.
I'll guard your tasks. The bread? No promises!
____________________________________________________________

____________________________________________________________
Not a crumb to retrace. Nothing to undo.
____________________________________________________________
____________________________________________________________
Hehe! Tucked this task into my bread basket:
[T][ ] read book
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Yatta! Task done. Time for a bread break:
[T][X] read book
____________________________________________________________
____________________________________________________________
Yatta! Task done. Time for a bread break:
[T][X] read book
____________________________________________________________
____________________________________________________________
Sniff sniff... here are the matching tasks:
1.[T][X] read book
____________________________________________________________
____________________________________________________________
Tiptoe back! Undid the last change.
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Let's peek in the basket. Your tasks:
1.[T][ ] read book
____________________________________________________________
____________________________________________________________
Poof! Snatched this task out of the basket:
[T][ ] read book
Now you have 0 tasks in the list.
____________________________________________________________
____________________________________________________________
Tiptoe back! Undid the last change.
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Let's peek in the basket. Your tasks:
1.[T][ ] read book
____________________________________________________________
____________________________________________________________
Tiptoe back! Undid the last change.
Now you have 0 tasks in the list.
____________________________________________________________
____________________________________________________________
Not a crumb to retrace. Nothing to undo.
____________________________________________________________
____________________________________________________________
Mata ne! Rest up. I smell fresh bread!
____________________________________________________________
```

## TC-15: Reject invalid data and recover — PASS

**Aim:** Accept extra whitespace and reject duplicates, equal/reversed dates, impossible dates, repeated parameters, reserved characters, and extra arguments without changing tasks or exiting.

**Input:**

```text
  todo   keep  
todo keep
event trip /from 2026-09-16 /to 2026-09-16
event trip /from 2026-09-17 /to 2026-09-16
deadline report /by 2026-02-30
deadline report /by 2026-09-16 /by 2026-09-17
todo bad | record
bye extra
list extra
list
bye
```

**Actual output:**

```text
____________________________________________________________
James the ぱんどろぼう
Shh! I'm James, your bread thief.
I'll guard your tasks. The bread? No promises!
____________________________________________________________

____________________________________________________________
Hehe! Tucked this task into my bread basket:
[T][ ] keep
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Gomen! A little flour in the gears.
Already in the basket! This task exists in your list.
____________________________________________________________
____________________________________________________________
Gomen! A little flour in the gears.
An event must end after its start date.
____________________________________________________________
____________________________________________________________
Gomen! A little flour in the gears.
An event must end after its start date.
____________________________________________________________
____________________________________________________________
Gomen! A little flour in the gears.
Formatting of the date is incorrect, try: yyyy-mm-dd
____________________________________________________________
____________________________________________________________
Gomen! A little flour in the gears.
Date options must appear once and in order: /by
____________________________________________________________
____________________________________________________________
Gomen! A little flour in the gears.
Task descriptions cannot contain | or control characters.
____________________________________________________________
____________________________________________________________
Gomen! A little flour in the gears.
BYE does not take arguments.
____________________________________________________________
____________________________________________________________
Gomen! A little flour in the gears.
LIST does not take arguments.
____________________________________________________________
____________________________________________________________
Let's peek in the basket. Your tasks:
1.[T][ ] keep
____________________________________________________________
____________________________________________________________
Mata ne! Rest up. I smell fresh bread!
____________________________________________________________
```

## TC-16: Protect corrupted storage — PASS

**Aim:** Show a startup warning before any command, explain recovery, and preserve the original file when saved data is malformed.

**Input:**

```text
todo keep
undo
bye
```

**Actual output:**

```text
____________________________________________________________
James the ぱんどろぼう
Shh! I'm James, your bread thief.
I'll guard your tasks. The bread? No promises!
____________________________________________________________

____________________________________________________________
Warning: Skipping invalid saved task at line 1.
Some saved tasks could not be loaded. Changes are disabled.
Repair the saved file or restore read access, then restart James.
____________________________________________________________
____________________________________________________________
Gomen! A little flour in the gears.
Saved tasks could not be fully loaded. No changes were made.
Repair the saved file or restore read access, then restart James.
____________________________________________________________
____________________________________________________________
Not a crumb to retrace. Nothing to undo.
____________________________________________________________
____________________________________________________________
Mata ne! Rest up. I smell fresh bread!
____________________________________________________________
```

## TC-17: Request stickers without changing tasks — PASS

**Aim:** Verify the fixed console fallback, repeated requests, argument rejection, and unchanged empty task list and undo history.

**Input:**

```text
random_sticker
random_sticker
random_sticker extra
list
undo
bye
```

**Actual output:**

```text
____________________________________________________________
James the ぱんどろぼう
Shh! I'm James, your bread thief.
I'll guard your tasks. The bread? No promises!
____________________________________________________________

____________________________________________________________
Hehe! A little treat from my secret stash!
____________________________________________________________
____________________________________________________________
Hehe! A little treat from my secret stash!
____________________________________________________________
____________________________________________________________
Gomen! A little flour in the gears.
RANDOM_STICKER does not take arguments.
____________________________________________________________
____________________________________________________________
Let's peek in the basket. Your tasks:
____________________________________________________________
____________________________________________________________
Not a crumb to retrace. Nothing to undo.
____________________________________________________________
____________________________________________________________
Mata ne! Rest up. I smell fresh bread!
____________________________________________________________
```

## TC-18: Allow paths in descriptions — PASS

**Aim:** Accept slash-prefixed paths in deadline and event descriptions while rejecting repeated date options.

**Input:**

```text
deadline inspect /tmp /by 2026-09-20
event inspect /tmp/files /from 2026-09-20 /to 2026-09-21
deadline inspect /tmp /by 2026-09-20 /by 2026-09-21
list
bye
```

**Actual output:**

```text
____________________________________________________________
James the ぱんどろぼう
Shh! I'm James, your bread thief.
I'll guard your tasks. The bread? No promises!
____________________________________________________________

____________________________________________________________
Hehe! Tucked this task into my bread basket:
[D][ ] inspect /tmp (by: Sep 20 2026)
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Hehe! Tucked this task into my bread basket:
[E][ ] inspect /tmp/files (from: Sep 20 2026 to: Sep 21 2026)
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Gomen! A little flour in the gears.
Date options must appear once and in order: /by
____________________________________________________________
____________________________________________________________
Let's peek in the basket. Your tasks:
1.[D][ ] inspect /tmp (by: Sep 20 2026)
2.[E][ ] inspect /tmp/files (from: Sep 20 2026 to: Sep 21 2026)
____________________________________________________________
____________________________________________________________
Mata ne! Rest up. I smell fresh bread!
____________________________________________________________
```

## TC-19: Reject saved control characters — PASS

**Aim:** Keep legacy pipes, reject control characters without printing them, and show the startup warning.

**Input:**

```text
list
bye
```

**Actual output:**

```text
____________________________________________________________
James the ぱんどろぼう
Shh! I'm James, your bread thief.
I'll guard your tasks. The bread? No promises!
____________________________________________________________

____________________________________________________________
Warning: Skipping invalid saved task at line 2.
Some saved tasks could not be loaded. Changes are disabled.
Repair the saved file or restore read access, then restart James.
____________________________________________________________
____________________________________________________________
Let's peek in the basket. Your tasks:
1.[T][ ] safe | legacy
____________________________________________________________
____________________________________________________________
Mata ne! Rest up. I smell fresh bread!
____________________________________________________________
```

## TC-20: Search independently of the OS language — PASS

**Aim:** Verify English case-insensitive search under a Turkish default locale, including the letter I.

**Input:**

```text
todo TITLE
find title
bye
```

**Actual output:**

```text
____________________________________________________________
James the ぱんどろぼう
Shh! I'm James, your bread thief.
I'll guard your tasks. The bread? No promises!
____________________________________________________________

____________________________________________________________
Hehe! Tucked this task into my bread basket:
[T][ ] TITLE
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Sniff sniff... here are the matching tasks:
1.[T][ ] TITLE
____________________________________________________________
____________________________________________________________
Mata ne! Rest up. I smell fresh bread!
____________________________________________________________
```

## TC-21: Preserve tasks when another writer holds the lock — PASS

**Aim:** Reject a save while another process holds the writer lock, preserve the saved task and undo history, and explain that the user can retry.

**Input:**

```text
todo new task
list
undo
bye
```

**Actual output:**

```text
____________________________________________________________
James the ぱんどろぼう
Shh! I'm James, your bread thief.
I'll guard your tasks. The bread? No promises!
____________________________________________________________

____________________________________________________________
Gomen! A little flour in the gears.
Another instance is saving tasks. No changes were made; try again.
____________________________________________________________
____________________________________________________________
Let's peek in the basket. Your tasks:
1.[T][ ] keep
____________________________________________________________
____________________________________________________________
Not a crumb to retrace. Nothing to undo.
____________________________________________________________
____________________________________________________________
Mata ne! Rest up. I smell fresh bread!
____________________________________________________________
```

## TC-22: Explain a missing deadline option — PASS

**Aim:** Give missing-date guidance for an unrecognized `/until` token, preserve the empty task list, and accept a subsequent valid command.

**Input:**

```text
deadline report /until 2026-09-16
list
deadline report /by 2026-09-16
bye
```

**Actual output:**

```text
____________________________________________________________
James the ぱんどろぼう
Shh! I'm James, your bread thief.
I'll guard your tasks. The bread? No promises!
____________________________________________________________

____________________________________________________________
Gomen! A little flour in the gears.
A deadline needs a by date.
Try: deadline <description> /by <date>
____________________________________________________________
____________________________________________________________
Let's peek in the basket. Your tasks:
____________________________________________________________
____________________________________________________________
Hehe! Tucked this task into my bread basket:
[D][ ] report (by: Sep 16 2026)
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Mata ne! Rest up. I smell fresh bread!
____________________________________________________________
```

All 22 cases passed.
