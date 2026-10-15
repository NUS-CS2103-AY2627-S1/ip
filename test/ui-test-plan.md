# Amy UI test plan

> Run the automated cases from a temporary directory with `test/run-amy-isolated.sh`.
> The packaged entry point is `amy.Amy` under the `src/main/java` source root.
> The launcher gives every case a fresh save file; the manual check below verifies loading.

## Test case 1: Exit immediately

**Aim:** Confirm that Amy prints the farewell and exits when the user enters `bye`.

**Inputs:**
```text
bye
```

**Expected output:**
```text
________________________________________________________________________________
  A     m   m  y   y
 A A    mm mm   y y
AAAAA   m m m    y
A   A   m   m    y
A   A   m   m    y
Hello! I'm Amy.
What can I do for you?
________________________________________________________________________________
________________________________________________________________________________
Lovely to see you! Until next time.
________________________________________________________________________________
```

## Test case 2: Unknown command

**Aim:** Confirm Amy reports an error when a command is not recognized.

**Inputs:**
```text
visit theme park
bye
```

**Expected output:**
```text
________________________________________________________________________________
  A     m   m  y   y
 A A    mm mm   y y
AAAAA   m m m    y
A   A   m   m    y
A   A   m   m    y
Hello! I'm Amy.
What can I do for you?
________________________________________________________________________________
________________________________________________________________________________
I'm sorry, but I don't know what that means.
________________________________________________________________________________
________________________________________________________________________________
Lovely to see you! Until next time.
________________________________________________________________________________
```

## Test case 3: Prompt for missing deadline date

**Aim:** Confirm Amy rejects a deadline without `/by` text instead of displaying an empty deadline.

**Inputs:**
```text
deadline return books
bye
```

**Expected output:**
```text
________________________________________________________________________________
  A     m   m  y   y
 A A    mm mm   y y
AAAAA   m m m    y
A   A   m   m    y
A   A   m   m    y
Hello! I'm Amy.
What can I do for you?
________________________________________________________________________________
________________________________________________________________________________
Please specify a deadline in the format: deadline <description> /by <date/time>.
________________________________________________________________________________
________________________________________________________________________________
Lovely to see you! Until next time.
________________________________________________________________________________
```
## Test case 4: Mark a task done

**Aim:** Confirm Amy marks an existing task as done and shows `[X]` in `list`.

**Inputs:**
```text
todo buy milk
mark 1
list
bye
```

**Expected output:**
```text
________________________________________________________________________________
  A     m   m  y   y
 A A    mm mm   y y
AAAAA   m m m    y
A   A   m   m    y
A   A   m   m    y
Hello! I'm Amy.
What can I do for you?
________________________________________________________________________________
________________________________________________________________________________
On it! I've added this task:
  [T][ ] buy milk
Now you have 1 tasks in the list.
________________________________________________________________________________
________________________________________________________________________________
Great job! One more task conquered:
  [T][X] buy milk
________________________________________________________________________________
________________________________________________________________________________
Amy's got your list right here:
1.[T][X] buy milk
________________________________________________________________________________
________________________________________________________________________________
Lovely to see you! Until next time.
________________________________________________________________________________
```

## Test case 5: Unmark a task

**Aim:** Confirm Amy reverses a completed task back to not done.

**Inputs:**
```text
todo buy milk
mark 1
unmark 1
list
bye
```

**Expected output:**
```text
________________________________________________________________________________
  A     m   m  y   y
 A A    mm mm   y y
AAAAA   m m m    y
A   A   m   m    y
A   A   m   m    y
Hello! I'm Amy.
What can I do for you?
________________________________________________________________________________
________________________________________________________________________________
On it! I've added this task:
  [T][ ] buy milk
Now you have 1 tasks in the list.
________________________________________________________________________________
________________________________________________________________________________
Great job! One more task conquered:
  [T][X] buy milk
________________________________________________________________________________
________________________________________________________________________________
No worries, plans change. I've marked it as incomplete again:
  [T][ ] buy milk
________________________________________________________________________________
________________________________________________________________________________
Amy's got your list right here:
1.[T][ ] buy milk
________________________________________________________________________________
________________________________________________________________________________
Lovely to see you! Until next time.
________________________________________________________________________________
```

## Test case 6: Mark invalid index

**Aim:** Confirm Amy handles marking a non-existent task gracefully.

**Inputs:**
```text
todo buy milk
mark 5
bye
```

**Expected output:**
```text
________________________________________________________________________________
  A     m   m  y   y
 A A    mm mm   y y
AAAAA   m m m    y
A   A   m   m    y
A   A   m   m    y
Hello! I'm Amy.
What can I do for you?
________________________________________________________________________________
________________________________________________________________________________
On it! I've added this task:
  [T][ ] buy milk
Now you have 1 tasks in the list.
________________________________________________________________________________
________________________________________________________________________________
That task does not exist.
________________________________________________________________________________
________________________________________________________________________________
Lovely to see you! Until next time.
________________________________________________________________________________
```

## Test case 7: Prompt for incomplete event

**Aim:** Confirm Amy requires both `/from` and `/to` values for an event.

**Inputs:**
```text
event project meeting /from Mon 2pm
bye
```

**Expected output:**
```text
________________________________________________________________________________
  A     m   m  y   y
 A A    mm mm   y y
AAAAA   m m m    y
A   A   m   m    y
A   A   m   m    y
Hello! I'm Amy.
What can I do for you?
________________________________________________________________________________
________________________________________________________________________________
Please specify an event in the format: event <description> /from <start> /to <end>.
________________________________________________________________________________
________________________________________________________________________________
Lovely to see you! Until next time.
________________________________________________________________________________
```

## Test case 8: Accept spaced deadline marker

**Aim:** Confirm Amy accepts a deadline marker written as `/ by` and preserves the time text.

**Inputs:**
```text
deadline water plants /by 3/9/2026 1500
bye
```

**Expected output:**
```text
________________________________________________________________________________
  A     m   m  y   y
 A A    mm mm   y y
AAAAA   m m m    y
A   A   m   m    y
A   A   m   m    y
Hello! I'm Amy.
What can I do for you?
________________________________________________________________________________
________________________________________________________________________________
On it! I've added this task:
  [D][ ] water plants (by: Sept 3 2026, 3:00pm)
Now you have 1 tasks in the list.
________________________________________________________________________________
________________________________________________________________________________
Lovely to see you! Until next time.
________________________________________________________________________________
```

## Test case 9: Add typed tasks

**Aim:** Confirm Amy stores todos, deadlines, and events with their type and date/time text.

**Inputs:**
```text
todo borrow book
deadline return book /by 24/04/2025 1420
event project meeting /from Mon 2pm /to 4pm
list
bye
```

**Expected output:**
```text
________________________________________________________________________________
  A     m   m  y   y
 A A    mm mm   y y
AAAAA   m m m    y
A   A   m   m    y
A   A   m   m    y
Hello! I'm Amy.
What can I do for you?
________________________________________________________________________________
________________________________________________________________________________
On it! I've added this task:
  [T][ ] borrow book
Now you have 1 tasks in the list.
________________________________________________________________________________
________________________________________________________________________________
On it! I've added this task:
  [D][ ] return book (by: Apr 24 2025, 2:20pm)
Now you have 2 tasks in the list.
________________________________________________________________________________
________________________________________________________________________________
On it! I've added this task:
  [E][ ] project meeting (from: Mon 2pm to: 4pm)
Now you have 3 tasks in the list.
________________________________________________________________________________
________________________________________________________________________________
Amy's got your list right here:
1.[T][ ] borrow book
2.[D][ ] return book (by: Apr 24 2025, 2:20pm)
3.[E][ ] project meeting (from: Mon 2pm to: 4pm)
________________________________________________________________________________
________________________________________________________________________________
Lovely to see you! Until next time.
________________________________________________________________________________
```

## Test case 10: Accept shorthand event times

**Aim:** Confirm Amy accepts an event with two slash-separated times without `/from` and `/to` labels.

**Inputs:**
```text
event pauls birthday/ 7pm/ 10pm
bye
```

**Expected output:**
```text
________________________________________________________________________________
  A     m   m  y   y
 A A    mm mm   y y
AAAAA   m m m    y
A   A   m   m    y
A   A   m   m    y
Hello! I'm Amy.
What can I do for you?
________________________________________________________________________________
________________________________________________________________________________
On it! I've added this task:
  [E][ ] pauls birthday (from: 7pm to: 10pm)
Now you have 1 tasks in the list.
________________________________________________________________________________
________________________________________________________________________________
Lovely to see you! Until next time.
________________________________________________________________________________
```

## Test case 11: Valid todo survives invalid deadline

**Aim:** Confirm an invalid deadline does not add a task or alter the existing todo.

**Inputs:**
```text
todo water plants
deadline return books
list
bye
```

**Expected output:**
```text
________________________________________________________________________________
  A     m   m  y   y
 A A    mm mm   y y
AAAAA   m m m    y
A   A   m   m    y
A   A   m   m    y
Hello! I'm Amy.
What can I do for you?
________________________________________________________________________________
________________________________________________________________________________
On it! I've added this task:
  [T][ ] water plants
Now you have 1 tasks in the list.
________________________________________________________________________________
________________________________________________________________________________
Please specify a deadline in the format: deadline <description> /by <date/time>.
________________________________________________________________________________
________________________________________________________________________________
Amy's got your list right here:
1.[T][ ] water plants
________________________________________________________________________________
________________________________________________________________________________
Lovely to see you! Until next time.
________________________________________________________________________________
```

## Test case 12: Valid deadline survives invalid event

**Aim:** Confirm an invalid event does not add a task or alter the existing deadline.

**Inputs:**
```text
deadline return books /by 24/04/2025 1700
event project meeting /from Mon 2pm
list
bye
```

**Expected output:**
```text
________________________________________________________________________________
  A     m   m  y   y
 A A    mm mm   y y
AAAAA   m m m    y
A   A   m   m    y
A   A   m   m    y
Hello! I'm Amy.
What can I do for you?
________________________________________________________________________________
________________________________________________________________________________
On it! I've added this task:
  [D][ ] return books (by: Apr 24 2025, 5:00pm)
Now you have 1 tasks in the list.
________________________________________________________________________________
________________________________________________________________________________
Please specify an event in the format: event <description> /from <start> /to <end>.
________________________________________________________________________________
________________________________________________________________________________
Amy's got your list right here:
1.[D][ ] return books (by: Apr 24 2025, 5:00pm)
________________________________________________________________________________
________________________________________________________________________________
Lovely to see you! Until next time.
________________________________________________________________________________
```

## Test case 13: Invalid mark preserves status

**Aim:** Confirm a malformed mark command does not change a task's done status.

**Inputs:**
```text
todo read book
mark nope
list
bye
```

**Expected output:**
```text
________________________________________________________________________________
  A     m   m  y   y
 A A    mm mm   y y
AAAAA   m m m    y
A   A   m   m    y
A   A   m   m    y
Hello! I'm Amy.
What can I do for you?
________________________________________________________________________________
________________________________________________________________________________
On it! I've added this task:
  [T][ ] read book
Now you have 1 tasks in the list.
________________________________________________________________________________
________________________________________________________________________________
Please specify a valid task number.
________________________________________________________________________________
________________________________________________________________________________
Amy's got your list right here:
1.[T][ ] read book
________________________________________________________________________________
________________________________________________________________________________
Lovely to see you! Until next time.
________________________________________________________________________________
```

 ## Test case 14: Optional deadline marker

**Aim:** Confirm a deadline accepts the shorthand slash form without the `by` label.

**Inputs:**
```text
deadline water plants/ 4/9/2026 0900
bye
```

**Expected output:**
```text
________________________________________________________________________________
  A     m   m  y   y
 A A    mm mm   y y
AAAAA   m m m    y
A   A   m   m    y
A   A   m   m    y
Hello! I'm Amy.
What can I do for you?
________________________________________________________________________________
________________________________________________________________________________
On it! I've added this task:
  [D][ ] water plants (by: Sept 4 2026, 9:00am)
Now you have 1 tasks in the list.
________________________________________________________________________________
________________________________________________________________________________
Lovely to see you! Until next time.
________________________________________________________________________________
```  

## Test case 15: Empty todo description

**Aim:** Confirm Amy reports an error when a todo has no description.

**Inputs:**
```text
todo
bye
```

**Expected output:**
```text
________________________________________________________________________________
  A     m   m  y   y
 A A    mm mm   y y
AAAAA   m m m    y
A   A   m   m    y
A   A   m   m    y
Hello! I'm Amy.
What can I do for you?
________________________________________________________________________________
________________________________________________________________________________
A todo description cannot be empty.
________________________________________________________________________________
________________________________________________________________________________
Lovely to see you! Until next time.
________________________________________________________________________________
```

## Test case 16: Unknown command

**Aim:** Confirm Amy reports an error for an unsupported command without changing the list.

**Inputs:**
```text
blah
bye
```

**Expected output:**
```text
________________________________________________________________________________
  A     m   m  y   y
 A A    mm mm   y y
AAAAA   m m m    y
A   A   m   m    y
A   A   m   m    y
Hello! I'm Amy.
What can I do for you?
________________________________________________________________________________
________________________________________________________________________________
I'm sorry, but I don't know what that means.
________________________________________________________________________________
________________________________________________________________________________
Lovely to see you! Until next time.
________________________________________________________________________________
```

## Test case 17: Empty deadline command

**Aim:** Confirm an empty deadline command reports a format error without crashing.

**Inputs:**
```text
deadline
bye
```

**Expected output:**
```text
________________________________________________________________________________
  A     m   m  y   y
 A A    mm mm   y y
AAAAA   m m m    y
A   A   m   m    y
A   A   m   m    y
Hello! I'm Amy.
What can I do for you?
________________________________________________________________________________
________________________________________________________________________________
Please specify a deadline in the format: deadline <description> /by <date/time>.
________________________________________________________________________________
________________________________________________________________________________
Lovely to see you! Until next time.
________________________________________________________________________________
```

## Test case 18: Empty event command

**Aim:** Confirm an empty event command reports a format error without crashing.

**Inputs:**
```text
event
bye
```

**Expected output:**
```text
________________________________________________________________________________
  A     m   m  y   y
 A A    mm mm   y y
AAAAA   m m m    y
A   A   m   m    y
A   A   m   m    y
Hello! I'm Amy.
What can I do for you?
________________________________________________________________________________
________________________________________________________________________________
Please specify an event in the format: event <description> /from <start> /to <end>.
________________________________________________________________________________
________________________________________________________________________________
Lovely to see you! Until next time.
________________________________________________________________________________
```

## Test case 19: Delete a task and shift the list

**Aim:** Confirm deleting a valid task removes it and shifts later tasks.

**Inputs:**
```text
todo read book
deadline return book /by 24/04/2025 1700
event project meeting/ 2pm/ 4pm
delete 2
list
bye
```

**Expected output:**
```text
________________________________________________________________________________
  A     m   m  y   y
 A A    mm mm   y y
AAAAA   m m m    y
A   A   m   m    y
A   A   m   m    y
Hello! I'm Amy.
What can I do for you?
________________________________________________________________________________
________________________________________________________________________________
On it! I've added this task:
  [T][ ] read book
Now you have 1 tasks in the list.
________________________________________________________________________________
________________________________________________________________________________
On it! I've added this task:
  [D][ ] return book (by: Apr 24 2025, 5:00pm)
Now you have 2 tasks in the list.
________________________________________________________________________________
________________________________________________________________________________
On it! I've added this task:
  [E][ ] project meeting (from: 2pm to: 4pm)
Now you have 3 tasks in the list.
________________________________________________________________________________
________________________________________________________________________________
Gone for good! I've removed this task:
  [D][ ] return book (by: Apr 24 2025, 5:00pm)
Now you have 2 tasks in the list.
________________________________________________________________________________
________________________________________________________________________________
Amy's got your list right here:
1.[T][ ] read book
2.[E][ ] project meeting (from: 2pm to: 4pm)
________________________________________________________________________________
________________________________________________________________________________
Lovely to see you! Until next time.
________________________________________________________________________________
```

## Test case 20: Delete invalid index

**Aim:** Confirm an invalid delete reports an error and leaves the list unchanged.

**Inputs:**
```text
todo read book
delete 5
list
bye
```

**Expected output:**
```text
________________________________________________________________________________
  A     m   m  y   y
 A A    mm mm   y y
AAAAA   m m m    y
A   A   m   m    y
A   A   m   m    y
Hello! I'm Amy.
What can I do for you?
________________________________________________________________________________
________________________________________________________________________________
On it! I've added this task:
  [T][ ] read book
Now you have 1 tasks in the list.
________________________________________________________________________________
________________________________________________________________________________
That task does not exist.
________________________________________________________________________________
________________________________________________________________________________
Amy's got your list right here:
1.[T][ ] read book
________________________________________________________________________________
________________________________________________________________________________
Lovely to see you! Until next time.
________________________________________________________________________________
```

## Test case 21: Missing task numbers

**Aim:** Confirm mark, unmark, and delete prompt for a number when omitted.

**Inputs:**
```text
todo read books
mark
delete
unmark
list
bye
```

**Expected output:**
```text
________________________________________________________________________________
  A     m   m  y   y
 A A    mm mm   y y
AAAAA   m m m    y
A   A   m   m    y
A   A   m   m    y
Hello! I'm Amy.
What can I do for you?
________________________________________________________________________________
________________________________________________________________________________
On it! I've added this task:
  [T][ ] read books
Now you have 1 tasks in the list.
________________________________________________________________________________
________________________________________________________________________________
Please provide a task number.
________________________________________________________________________________
________________________________________________________________________________
Please provide a task number.
________________________________________________________________________________
________________________________________________________________________________
Please provide a task number.
________________________________________________________________________________
________________________________________________________________________________
Amy's got your list right here:
1.[T][ ] read books
________________________________________________________________________________
________________________________________________________________________________
Lovely to see you! Until next time.
________________________________________________________________________________
```

## Test case 22: Unmark invalid index

**Aim:** Confirm Amy handles unmarking a non-existent task gracefully.

**Inputs:**
```text
todo buy milk
unmark 5
bye
```

**Expected output:**
```text
________________________________________________________________________________
  A     m   m  y   y
 A A    mm mm   y y
AAAAA   m m m    y
A   A   m   m    y
A   A   m   m    y
Hello! I'm Amy.
What can I do for you?
________________________________________________________________________________
________________________________________________________________________________
On it! I've added this task:
  [T][ ] buy milk
Now you have 1 tasks in the list.
________________________________________________________________________________
________________________________________________________________________________
That task does not exist.
________________________________________________________________________________
________________________________________________________________________________
Lovely to see you! Until next time.
________________________________________________________________________________
```

## Test case 23: Delete a task

**Aim:** Confirm Amy removes the selected task, leaving the remaining task in the list.

**Inputs:**
```text
todo buy milk
todo read book
delete 1
list
bye
```

**Expected output:**
```text
________________________________________________________________________________
  A     m   m  y   y
 A A    mm mm   y y
AAAAA   m m m    y
A   A   m   m    y
A   A   m   m    y
Hello! I'm Amy.
What can I do for you?
________________________________________________________________________________
________________________________________________________________________________
On it! I've added this task:
  [T][ ] buy milk
Now you have 1 tasks in the list.
________________________________________________________________________________
________________________________________________________________________________
On it! I've added this task:
  [T][ ] read book
Now you have 2 tasks in the list.
________________________________________________________________________________
________________________________________________________________________________
Gone for good! I've removed this task:
  [T][ ] buy milk
Now you have 1 tasks in the list.
________________________________________________________________________________
________________________________________________________________________________
Amy's got your list right here:
1.[T][ ] read book
________________________________________________________________________________
________________________________________________________________________________
Lovely to see you! Until next time.
________________________________________________________________________________
```

## Manual check: Load saved tasks at startup

**Aim:** Confirm Amy restores saved todo, deadline, and event tasks with their completion status.

**Setup:** Create `data/amy.txt` with the following content, then start Amy in the same project directory:

```text
T | 1 | read book
D | 0 | return book | Sunday
E | 1 | project meeting | Mon 2pm | 4pm
```

**Manual commands:**
```text
list
bye
```

**Expected list:**
```text
Amy's got your list right here:
1.[T][X] read book
2.[D][ ] return book (by: Sunday)
3.[E][X] project meeting (from: Mon 2pm to: 4pm)
```

## Manual check: Corrupt records and delimiter characters

**Aim:** Confirm unreadable records do not prevent valid records from loading, and task text
containing ` | ` is preserved after a restart.

**Steps:** Start Amy with a save file containing blank lines, invalid status values, unknown task
types, and incomplete deadline/event records alongside a valid task. Confirm `list` shows only the
valid task. Then add a task whose description contains ` | `, restart Amy, and confirm `list`
shows the original description unchanged.

## Test case 24: List with no tasks

**Aim:** Confirm Amy explains that the list is empty on a first run without a data file.

**Inputs:**
```text
list
bye
```

**Expected output:**
```text
________________________________________________________________________________
  A     m   m  y   y
 A A    mm mm   y y
AAAAA   m m m    y
A   A   m   m    y
A   A   m   m    y
Hello! I'm Amy.
What can I do for you?
________________________________________________________________________________
________________________________________________________________________________
Your list is empty for now!
________________________________________________________________________________
________________________________________________________________________________
Lovely to see you! Until next time.
________________________________________________________________________________
```
## Test case 25: Add deadline with valid date and time

**Aim:** Confirm Amy parses `d/M/yyyy HHmm` input and displays it in the formatted output.

**Inputs:**
```text
deadline return book /by 2/12/2019 1800
list
bye
```

**Expected output:**
```text
________________________________________________________________________________
  A     m   m  y   y
 A A    mm mm   y y
AAAAA   m m m    y
A   A   m   m    y
A   A   m   m    y
Hello! I'm Amy.
What can I do for you?
________________________________________________________________________________
________________________________________________________________________________
On it! I've added this task:
  [D][ ] return book (by: Dec 2 2019, 6:00pm)
Now you have 1 tasks in the list.
________________________________________________________________________________
________________________________________________________________________________
Amy's got your list right here:
1.[D][ ] return book (by: Dec 2 2019, 6:00pm)
________________________________________________________________________________
________________________________________________________________________________
Lovely to see you! Until next time.
________________________________________________________________________________
```

## Test case 26: Add deadline with invalid date format

**Aim:** Confirm Amy rejects an unparseable date/time with a helpful error message instead of crashing.

**Inputs:**
```text
deadline return book /by friday 6pm
bye
```

**Expected output:**
```text
________________________________________________________________________________
  A     m   m  y   y
 A A    mm mm   y y
AAAAA   m m m    y
A   A   m   m    y
A   A   m   m    y
Hello! I'm Amy.
What can I do for you?
________________________________________________________________________________
________________________________________________________________________________
Please use the date format: d/M/yyyy HHmm, e.g. 2/12/2019 1800
________________________________________________________________________________
________________________________________________________________________________
Lovely to see you! Until next time.
________________________________________________________________________________
```

## Test case 27: Blank command

**Aim:** Confirm Amy treats an empty input line as an unknown command and continues running.

**Inputs:**
```text

bye
```

**Expected output:**
```text
________________________________________________________________________________
  A     m   m  y   y
 A A    mm mm   y y
AAAAA   m m m    y
A   A   m   m    y
A   A   m   m    y
Hello! I'm Amy.
What can I do for you?
________________________________________________________________________________
________________________________________________________________________________
I'm sorry, but I don't know what that means.
________________________________________________________________________________
________________________________________________________________________________
Lovely to see you! Until next time.
________________________________________________________________________________
```

## Test case 28: Whitespace around exit command

**Aim:** Confirm Amy trims user input before recognizing the `bye` command.

**Inputs:**
```text
  bye
```

**Expected output:**
```text
________________________________________________________________________________
  A     m   m  y   y
 A A    mm mm   y y
AAAAA   m m m    y
A   A   m   m    y
A   A   m   m    y
Hello! I'm Amy.
What can I do for you?
________________________________________________________________________________
________________________________________________________________________________
Lovely to see you! Until next time.
________________________________________________________________________________
```

## Test case 29: Find matching tasks

**Aim:** Confirm `find` displays every task whose description contains the keyword and preserves its list number.

**Inputs:**
```text
todo read book
deadline return book /by 2/12/2019 1800
find BOOK
bye
```

**Expected output:**
```text
________________________________________________________________________________
  A     m   m  y   y
 A A    mm mm   y y
AAAAA   m m m    y
A   A   m   m    y
A   A   m   m    y
Hello! I'm Amy.
What can I do for you?
________________________________________________________________________________
________________________________________________________________________________
On it! I've added this task:
  [T][ ] read book
Now you have 1 tasks in the list.
________________________________________________________________________________
________________________________________________________________________________
On it! I've added this task:
  [D][ ] return book (by: Dec 2 2019, 6:00pm)
Now you have 2 tasks in the list.
________________________________________________________________________________
________________________________________________________________________________
Here are the matching tasks in your list:
1.[T][ ] read book
2.[D][ ] return book (by: Dec 2 2019, 6:00pm)
________________________________________________________________________________
________________________________________________________________________________
Lovely to see you! Until next time.
________________________________________________________________________________
```

## Test case 30: Find with no matches

**Aim:** Confirm `find` reports when no task description contains the keyword.

**Inputs:**
```text
todo read book
find meeting
bye
```

**Expected output:**
```text
________________________________________________________________________________
  A     m   m  y   y
 A A    mm mm   y y
AAAAA   m m m    y
A   A   m   m    y
A   A   m   m    y
Hello! I'm Amy.
What can I do for you?
________________________________________________________________________________
________________________________________________________________________________
On it! I've added this task:
  [T][ ] read book
Now you have 1 tasks in the list.
________________________________________________________________________________
________________________________________________________________________________
I couldn't find anything matching that. Maybe try again with a different keyword!
________________________________________________________________________________
________________________________________________________________________________
Lovely to see you! Until next time.
________________________________________________________________________________
```

## Test case 31: Undo an added task

**Aim:** Confirm a successful task addition can be undone after confirmation.

**Inputs:**
```text
todo buy milk
undo
yes
list
bye
```

**Expected output:**
```text
________________________________________________________________________________
  A     m   m  y   y
 A A    mm mm   y y
AAAAA   m m m    y
A   A   m   m    y
A   A   m   m    y
Hello! I'm Amy.
What can I do for you?
________________________________________________________________________________
________________________________________________________________________________
On it! I've added this task:
  [T][ ] buy milk
Now you have 1 tasks in the list.
________________________________________________________________________________
________________________________________________________________________________
Confirm undo: remove the task "[T][ ] buy milk"? [yes/no]
________________________________________________________________________________
________________________________________________________________________________
Undone. I've restored the previous task list.
________________________________________________________________________________
________________________________________________________________________________
Your list is empty for now!
________________________________________________________________________________
________________________________________________________________________________
Lovely to see you! Until next time.
________________________________________________________________________________
```
