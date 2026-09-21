# Janet UI test plan

The runner executes each test case in order. Expected output is compared exactly.
Each case runs in a fresh temporary folder. Cases may provide an initial data file or assert the saved file.

The JavaFX cases below are manual because the command-line runner has no graphical display. Run Janet with
`./gradlew run`, perform each case in order, and close the window before starting the next case.

## Manual GUI test: Start and add a task

Enter `todo read book`, followed by `list`. Verify that the window opens at a usable size, shows the welcome bubble,
accepts input by Enter and the Send button, displays distinct user and Janet bubbles, and lists the new task. Verify
that the header, composer, badges, and bubbles use a warm pastel-purple palette with readable contrast.

## Manual GUI test: Reject malformed input without changing state

After adding `read book`, enter `todo`, followed by `list`. Verify that Janet shows the missing-description error and
that the list still contains exactly the original task. Verify that the error uses the pink error bubble and `!` badge,
while the valid list reply uses Janet's cream bubble and `J` badge.

## Manual GUI test: Show and enforce a saved-data warning

Place one valid row followed by one malformed row in `data/janet.txt`, then start Janet. Verify that a warning bubble
identifies line 2, `list` shows the valid task, and an add command is rejected without overwriting the original file.

## Manual GUI test: Update an existing task

After adding one task, enter `mark 1`, followed by `list`. Verify that Janet confirms the change and displays `[X]`.

## Manual GUI test: Reject an invalid task number

After marking task 1, enter `delete 0`, followed by `list`. Verify that Janet reports the invalid number and the marked
task remains in the list.

## Manual GUI test: Display long conversations and messages

Resize the window to its minimum size, enter a description longer than the visible input field, and then add enough
tasks to fill the conversation. Verify that bubbles wrap without clipping and the view scrolls to the latest reply.

## Manual GUI test: Exit using the bye command

Enter `  bye  `. Verify that Janet displays its goodbye response, disables further input, and closes after a short
delay even though the command contains surrounding spaces.

## Test case: Find tasks by keyword

Aim: Verify that Janet displays tasks whose descriptions contain a keyword and rejects an empty keyword without changing the list.

### Inputs

```text
todo read book
deadline return book /by 2099-12-02
event project meeting /from 2099-12-02 14:00 /to 2099-12-02 16:00
find book
find
find meeting
find missing
list
bye
```

### Expected output

```text
____________________________________________________________
     _                  _
    | |                | |
    | | __ _ _ __   ___| |_
 _  | |/ _` | '_ \ / _ \ __|
| |_| | (_| | | | |  __/ |_
 \___/ \__,_|_| |_|\___|\__|
____________________________________________________________
Hi there! I'm Janet, your cheerful task assistant.
Fun fact: not a robot. What can I help you organize?
____________________________________________________________
 Done and done! I added this task:
   [T][ ] read book
 You now have 1 task.
____________________________________________________________
 Done and done! I added this task:
   [D][ ] return book (by: Dec 02 2099)
 You now have 2 tasks.
____________________________________________________________
 Done and done! I added this task:
   [E][ ] project meeting (from: 2099-12-02 14:00 to: 2099-12-02 16:00)
 You now have 3 tasks.
____________________________________________________________
 I found these matching tasks:
 1.[T][ ] read book
 2.[D][ ] return book (by: Dec 02 2099)
____________________________________________________________
 OOPS!!! A find command needs a keyword.
____________________________________________________________
 I found these matching tasks:
 1.[E][ ] project meeting (from: 2099-12-02 14:00 to: 2099-12-02 16:00)
____________________________________________________________
 I checked the whole list. No matching tasks found.
____________________________________________________________
 Absolutely! Here is everything on your list:
 1.[T][ ] read book
 2.[D][ ] return book (by: Dec 02 2099)
 3.[E][ ] project meeting (from: 2099-12-02 14:00 to: 2099-12-02 16:00)
____________________________________________________________
____________________________________________________________
 All set! I'll be right here if you need me. Bye!
____________________________________________________________
```

## Test case: Delete multiple tasks atomically

Aim: Verify that Janet removes several selected tasks in one command, preserves their requested order in the
confirmation, and rejects duplicate, malformed, or out-of-range selections without changing the task list.

### Inputs

```text
todo alpha
todo bravo
deadline charlie /by 2099-12-02
event delta /from 2099-12-02 14:00 /to 2099-12-02 16:00
todo echo
delete 2 2
delete 2 word
delete 2 6
list
delete 4 2
list
bye
```

### Expected output

```text
____________________________________________________________
     _                  _
    | |                | |
    | | __ _ _ __   ___| |_
 _  | |/ _` | '_ \ / _ \ __|
| |_| | (_| | | | |  __/ |_
 \___/ \__,_|_| |_|\___|\__|
____________________________________________________________
Hi there! I'm Janet, your cheerful task assistant.
Fun fact: not a robot. What can I help you organize?
____________________________________________________________
 Done and done! I added this task:
   [T][ ] alpha
 You now have 1 task.
____________________________________________________________
 Done and done! I added this task:
   [T][ ] bravo
 You now have 2 tasks.
____________________________________________________________
 Done and done! I added this task:
   [D][ ] charlie (by: Dec 02 2099)
 You now have 3 tasks.
____________________________________________________________
 Done and done! I added this task:
   [E][ ] delta (from: 2099-12-02 14:00 to: 2099-12-02 16:00)
 You now have 4 tasks.
____________________________________________________________
 Done and done! I added this task:
   [T][ ] echo
 You now have 5 tasks.
____________________________________________________________
 Sorry, please provide each task number only once.
____________________________________________________________
 Sorry, please provide valid task numbers separated by spaces.
____________________________________________________________
 Sorry, that task number does not exist.
____________________________________________________________
 Absolutely! Here is everything on your list:
 1.[T][ ] alpha
 2.[T][ ] bravo
 3.[D][ ] charlie (by: Dec 02 2099)
 4.[E][ ] delta (from: 2099-12-02 14:00 to: 2099-12-02 16:00)
 5.[T][ ] echo
____________________________________________________________
 Done! I removed these tasks:
   [E][ ] delta (from: 2099-12-02 14:00 to: 2099-12-02 16:00)
   [T][ ] bravo
 You now have 3 tasks.
____________________________________________________________
 Absolutely! Here is everything on your list:
 1.[T][ ] alpha
 2.[D][ ] charlie (by: Dec 02 2099)
 3.[T][ ] echo
____________________________________________________________
____________________________________________________________
 All set! I'll be right here if you need me. Bye!
____________________________________________________________
```

### Expected saved data

```text
T	0	alpha
D	0	charlie	2099-12-02
T	0	echo
```

## Test case: Save task changes automatically

Aim: Verify that adding and marking a task writes its current state to Janet's relative data file.

### Inputs

```text
todo read book
mark 1
bye
```

### Expected output

```text
____________________________________________________________
     _                  _
    | |                | |
    | | __ _ _ __   ___| |_
 _  | |/ _` | '_ \ / _ \ __|
| |_| | (_| | | | |  __/ |_
 \___/ \__,_|_| |_|\___|\__|
____________________________________________________________
Hi there! I'm Janet, your cheerful task assistant.
Fun fact: not a robot. What can I help you organize?
____________________________________________________________
 Done and done! I added this task:
   [T][ ] read book
 You now have 1 task.
____________________________________________________________
 Excellent! This task is now complete:
   [T][X] read book
____________________________________________________________
____________________________________________________________
 All set! I'll be right here if you need me. Bye!
____________________________________________________________
```

### Expected saved data

```text
T	1	read book
```

## Test case: Load saved tasks at startup

Aim: Verify that Janet recreates each stored task and its completion status when it starts.

### Inputs

```text
list
bye
```

### Expected output

```text
____________________________________________________________
     _                  _
    | |                | |
    | | __ _ _ __   ___| |_
 _  | |/ _` | '_ \ / _ \ __|
| |_| | (_| | | | |  __/ |_
 \___/ \__,_|_| |_|\___|\__|
____________________________________________________________
Hi there! I'm Janet, your cheerful task assistant.
Fun fact: not a robot. What can I help you organize?
____________________________________________________________
 Absolutely! Here is everything on your list:
 1.[T][X] read book
 2.[D][ ] return book (by: Dec 02 2099)
 3.[E][X] project meeting (from: 2099-12-02 14:00 to: 2099-12-02 16:00)
____________________________________________________________
____________________________________________________________
 All set! I'll be right here if you need me. Bye!
____________________________________________________________
```

### Initial saved data

```text
T	1	read book
D	0	return book	2099-12-02
E	1	project meeting	2099-12-02 14:00	2099-12-02 16:00
```

## Test case: Add, list, mark, and unmark a task

Aim: Verify that Janet stores a task, displays its status, marks it done, and reverses the status.

### Inputs

```text
todo read book
list
mark 1
unmark 1
list
bye
```

### Expected output

```text
____________________________________________________________
     _                  _
    | |                | |
    | | __ _ _ __   ___| |_
 _  | |/ _` | '_ \ / _ \ __|
| |_| | (_| | | | |  __/ |_
 \___/ \__,_|_| |_|\___|\__|
____________________________________________________________
Hi there! I'm Janet, your cheerful task assistant.
Fun fact: not a robot. What can I help you organize?
____________________________________________________________
 Done and done! I added this task:
   [T][ ] read book
 You now have 1 task.
____________________________________________________________
 Absolutely! Here is everything on your list:
 1.[T][ ] read book
____________________________________________________________
 Excellent! This task is now complete:
   [T][X] read book
____________________________________________________________
 No problem! This task is back in progress:
   [T][ ] read book
____________________________________________________________
 Absolutely! Here is everything on your list:
 1.[T][ ] read book
____________________________________________________________
____________________________________________________________
 All set! I'll be right here if you need me. Bye!
____________________________________________________________
```

## Test case: Delete a task

Aim: Verify that Janet removes the selected task and reports the updated task count.

### Inputs

```text
todo read book
deadline return book /by 2099-12-02
event project meeting /from 2099-12-02 14:00 /to 2099-12-02 16:00
delete 3
list
bye
```

### Expected output

```text
____________________________________________________________
     _                  _
    | |                | |
    | | __ _ _ __   ___| |_
 _  | |/ _` | '_ \ / _ \ __|
| |_| | (_| | | | |  __/ |_
 \___/ \__,_|_| |_|\___|\__|
____________________________________________________________
Hi there! I'm Janet, your cheerful task assistant.
Fun fact: not a robot. What can I help you organize?
____________________________________________________________
 Done and done! I added this task:
   [T][ ] read book
 You now have 1 task.
____________________________________________________________
 Done and done! I added this task:
   [D][ ] return book (by: Dec 02 2099)
 You now have 2 tasks.
____________________________________________________________
 Done and done! I added this task:
   [E][ ] project meeting (from: 2099-12-02 14:00 to: 2099-12-02 16:00)
 You now have 3 tasks.
____________________________________________________________
 Done! I removed this task:
   [E][ ] project meeting (from: 2099-12-02 14:00 to: 2099-12-02 16:00)
 You now have 2 tasks.
____________________________________________________________
 Absolutely! Here is everything on your list:
 1.[T][ ] read book
 2.[D][ ] return book (by: Dec 02 2099)
____________________________________________________________
____________________________________________________________
 All set! I'll be right here if you need me. Bye!
____________________________________________________________
```

## Test case: Add and complete an event

Aim: Verify that Janet creates an event, displays its start and end times, and preserves them when marking it done.

### Inputs

```text
event project meeting /from 2099-12-02 14:00 /to 2099-12-02 16:00
list
mark 1
list
bye
```

### Expected output

```text
____________________________________________________________
     _                  _
    | |                | |
    | | __ _ _ __   ___| |_
 _  | |/ _` | '_ \ / _ \ __|
| |_| | (_| | | | |  __/ |_
 \___/ \__,_|_| |_|\___|\__|
____________________________________________________________
Hi there! I'm Janet, your cheerful task assistant.
Fun fact: not a robot. What can I help you organize?
____________________________________________________________
 Done and done! I added this task:
   [E][ ] project meeting (from: 2099-12-02 14:00 to: 2099-12-02 16:00)
 You now have 1 task.
____________________________________________________________
 Absolutely! Here is everything on your list:
 1.[E][ ] project meeting (from: 2099-12-02 14:00 to: 2099-12-02 16:00)
____________________________________________________________
 Excellent! This task is now complete:
   [E][X] project meeting (from: 2099-12-02 14:00 to: 2099-12-02 16:00)
____________________________________________________________
 Absolutely! Here is everything on your list:
 1.[E][X] project meeting (from: 2099-12-02 14:00 to: 2099-12-02 16:00)
____________________________________________________________
____________________________________________________________
 All set! I'll be right here if you need me. Bye!
____________________________________________________________
```

## Test case: Add and complete a deadline

Aim: Verify that Janet creates a deadline, displays its due date, and preserves that information when marking it done.

### Inputs

```text
deadline return book /by 2099-12-02
list
mark 1
list
bye
```

### Expected output

```text
____________________________________________________________
     _                  _
    | |                | |
    | | __ _ _ __   ___| |_
 _  | |/ _` | '_ \ / _ \ __|
| |_| | (_| | | | |  __/ |_
 \___/ \__,_|_| |_|\___|\__|
____________________________________________________________
Hi there! I'm Janet, your cheerful task assistant.
Fun fact: not a robot. What can I help you organize?
____________________________________________________________
 Done and done! I added this task:
   [D][ ] return book (by: Dec 02 2099)
 You now have 1 task.
____________________________________________________________
 Absolutely! Here is everything on your list:
 1.[D][ ] return book (by: Dec 02 2099)
____________________________________________________________
 Excellent! This task is now complete:
   [D][X] return book (by: Dec 02 2099)
____________________________________________________________
 Absolutely! Here is everything on your list:
 1.[D][X] return book (by: Dec 02 2099)
____________________________________________________________
____________________________________________________________
 All set! I'll be right here if you need me. Bye!
____________________________________________________________
```

### Expected saved data

```text
D	1	return book	2099-12-02
```

## Test case: Add and complete a to-do

Aim: Verify that Janet creates a to-do, displays its type icon, and preserves that type when marking it done.

### Inputs

```text
todo borrow book
list
mark 1
list
bye
```

### Expected output

```text
____________________________________________________________
     _                  _
    | |                | |
    | | __ _ _ __   ___| |_
 _  | |/ _` | '_ \ / _ \ __|
| |_| | (_| | | | |  __/ |_
 \___/ \__,_|_| |_|\___|\__|
____________________________________________________________
Hi there! I'm Janet, your cheerful task assistant.
Fun fact: not a robot. What can I help you organize?
____________________________________________________________
 Done and done! I added this task:
   [T][ ] borrow book
 You now have 1 task.
____________________________________________________________
 Absolutely! Here is everything on your list:
 1.[T][ ] borrow book
____________________________________________________________
 Excellent! This task is now complete:
   [T][X] borrow book
____________________________________________________________
 Absolutely! Here is everything on your list:
 1.[T][X] borrow book
____________________________________________________________
____________________________________________________________
 All set! I'll be right here if you need me. Bye!
____________________________________________________________
```

## Test case: Reject empty and unknown commands without changing tasks

Aim: Verify that an empty to-do and an unknown command show helpful errors, while an existing task remains intact.

### Inputs

```text
list
todo
todo read book
what
list
bye
```

### Expected output

```text
____________________________________________________________
     _                  _
    | |                | |
    | | __ _ _ __   ___| |_
 _  | |/ _` | '_ \ / _ \ __|
| |_| | (_| | | | |  __/ |_
 \___/ \__,_|_| |_|\___|\__|
____________________________________________________________
Hi there! I'm Janet, your cheerful task assistant.
Fun fact: not a robot. What can I help you organize?
____________________________________________________________
 Your task list is empty. Everything is wonderfully under control.
____________________________________________________________
 OOPS!!! A todo needs a description.
____________________________________________________________
 Done and done! I added this task:
   [T][ ] read book
 You now have 1 task.
____________________________________________________________
 OOPS!!! I don't recognize that command.
____________________________________________________________
 Absolutely! Here is everything on your list:
 1.[T][ ] read book
____________________________________________________________
____________________________________________________________
 All set! I'll be right here if you need me. Bye!
____________________________________________________________
```

## Test case: Reject malformed deadline and event commands without changing tasks

Aim: Verify that malformed structured commands are rejected and do not add tasks; valid deadlines and events continue to work.

### Inputs

```text
deadline
deadline submit report /by
deadline submit report /by Friday
deadline submit report /by 2099-12-06
event meeting /from 2099-12-02 14:00
event meeting /from 2099-12-02 14:00 /to
event meeting /from /to 2099-12-02 16:00
event meeting /from 2099-12-02 14:00 /to 2099-12-02 16:00
list
bye
```

### Expected output

```text
____________________________________________________________
     _                  _
    | |                | |
    | | __ _ _ __   ___| |_
 _  | |/ _` | '_ \ / _ \ __|
| |_| | (_| | | | |  __/ |_
 \___/ \__,_|_| |_|\___|\__|
____________________________________________________________
Hi there! I'm Janet, your cheerful task assistant.
Fun fact: not a robot. What can I help you organize?
____________________________________________________________
 Sorry, please use: deadline <task> /by <date or time>.
____________________________________________________________
 Sorry, please use: deadline <task> /by <date or time>.
____________________________________________________________
 Sorry, please provide a deadline date in yyyy-MM-dd format.
____________________________________________________________
 Done and done! I added this task:
   [D][ ] submit report (by: Dec 06 2099)
 You now have 1 task.
____________________________________________________________
 Sorry, please use: event <task> /from <start> /to <end>.
____________________________________________________________
 Sorry, please use: event <task> /from <start> /to <end>.
____________________________________________________________
 OOPS!!! An event needs a description, start, and end.
____________________________________________________________
 Done and done! I added this task:
   [E][ ] meeting (from: 2099-12-02 14:00 to: 2099-12-02 16:00)
 You now have 2 tasks.
____________________________________________________________
 Absolutely! Here is everything on your list:
 1.[D][ ] submit report (by: Dec 06 2099)
 2.[E][ ] meeting (from: 2099-12-02 14:00 to: 2099-12-02 16:00)
____________________________________________________________
____________________________________________________________
 All set! I'll be right here if you need me. Bye!
____________________________________________________________
```

## Test case: Reject invalid task numbers without changing task state

Aim: Verify that mark, unmark, and delete reject missing, non-numeric, zero, and out-of-range task numbers, while valid operations still change only the selected task.

### Inputs

```text
todo alpha
mark
mark 0
mark 2
mark 1
unmark
unmark zero
unmark 2
unmark 1
delete
delete zero
delete 2
delete 1
list
bye
```

### Expected output

```text
____________________________________________________________
     _                  _
    | |                | |
    | | __ _ _ __   ___| |_
 _  | |/ _` | '_ \ / _ \ __|
| |_| | (_| | | | |  __/ |_
 \___/ \__,_|_| |_|\___|\__|
____________________________________________________________
Hi there! I'm Janet, your cheerful task assistant.
Fun fact: not a robot. What can I help you organize?
____________________________________________________________
 Done and done! I added this task:
   [T][ ] alpha
 You now have 1 task.
____________________________________________________________
 Sorry, please provide a valid task number.
____________________________________________________________
 Sorry, that task number does not exist.
____________________________________________________________
 Sorry, that task number does not exist.
____________________________________________________________
 Excellent! This task is now complete:
   [T][X] alpha
____________________________________________________________
 Sorry, please provide a valid task number.
____________________________________________________________
 Sorry, please provide a valid task number.
____________________________________________________________
 Sorry, that task number does not exist.
____________________________________________________________
 No problem! This task is back in progress:
   [T][ ] alpha
____________________________________________________________
 Sorry, please provide a valid task number.
____________________________________________________________
 Sorry, please provide a valid task number.
____________________________________________________________
 Sorry, that task number does not exist.
____________________________________________________________
 Done! I removed this task:
   [T][ ] alpha
 You now have 0 tasks.
____________________________________________________________
 Your task list is empty. Everything is wonderfully under control.
____________________________________________________________
____________________________________________________________
 All set! I'll be right here if you need me. Bye!
____________________________________________________________
```

## Test case: Handle whitespace and unexpected arguments

Aim: Verify that Janet reports blank input, accepts harmless surrounding spaces, and rejects extra arguments without changing tasks.

### Inputs

```text

   todo   spaced task
list extra
list
bye now
bye
```

### Expected output

```text
____________________________________________________________
     _                  _
    | |                | |
    | | __ _ _ __   ___| |_
 _  | |/ _` | '_ \ / _ \ __|
| |_| | (_| | | | |  __/ |_
 \___/ \__,_|_| |_|\___|\__|
____________________________________________________________
Hi there! I'm Janet, your cheerful task assistant.
Fun fact: not a robot. What can I help you organize?
____________________________________________________________
 Please enter a command.
____________________________________________________________
 Done and done! I added this task:
   [T][ ] spaced task
 You now have 1 task.
____________________________________________________________
 Sorry, list does not take any arguments.
____________________________________________________________
 Absolutely! Here is everything on your list:
 1.[T][ ] spaced task
____________________________________________________________
 Sorry, bye does not take any arguments.
____________________________________________________________
____________________________________________________________
 All set! I'll be right here if you need me. Bye!
____________________________________________________________
```

## Test case: Protect malformed saved data

Aim: Verify that Janet reports malformed saved rows, keeps valid rows available, and refuses changes that would overwrite the original file.

### Inputs

```text
list
todo another task
list
bye
```

### Expected output

```text
____________________________________________________________
     _                  _
    | |                | |
    | | __ _ _ __   ___| |_
 _  | |/ _` | '_ \ / _ \ __|
| |_| | (_| | | | |  __/ |_
 \___/ \__,_|_| |_|\___|\__|
____________________________________________________________
Hi there! I'm Janet, your cheerful task assistant.
Fun fact: not a robot. What can I help you organize?
____________________________________________________________
 I ignored malformed saved-task line 2. Saving is disabled to protect the data file. Fix the file and restart Janet.
____________________________________________________________
 Absolutely! Here is everything on your list:
 1.[T][ ] valid task
____________________________________________________________
 I can't save changes while the saved-data warning is unresolved. Fix the data file and restart Janet.
____________________________________________________________
 Absolutely! Here is everything on your list:
 1.[T][ ] valid task
____________________________________________________________
____________________________________________________________
 All set! I'll be right here if you need me. Bye!
____________________________________________________________
```

### Initial saved data

```text
T	0	valid task
invalid row
```

### Expected saved data

```text
T	0	valid task
invalid row
```

## Test case: Validate task dates, duplicates, warnings, and find order

Aim: Verify that Janet rejects exact duplicates, impossible or mismatched date/time values, and reversed ranges;
accepts past values with warnings; and keeps matching tasks in their original list order.

### Inputs

```text
todo project alpha
todo middle
deadline project deadline /by 2099-12-01
event project event /from 2099-12-02 14:00 /to 2099-12-02 16:00
event project event /from 2099-12-02 14:00 /to 2099-12-02 16:00
deadline impossible /by 2026-02-30
event invalid time /from 10:99 /to 12:00
event reversed /from 18:00 /to 17:00
event mixed /from 2099-12-02 /to 16:00
deadline archived /by 2000-01-01
event old meeting /from 2000-01-01 /to 2000-01-02
find project
list
bye
```

### Expected output

```text
____________________________________________________________
     _                  _
    | |                | |
    | | __ _ _ __   ___| |_
 _  | |/ _` | '_ \ / _ \ __|
| |_| | (_| | | | |  __/ |_
 \___/ \__,_|_| |_|\___|\__|
____________________________________________________________
Hi there! I'm Janet, your cheerful task assistant.
Fun fact: not a robot. What can I help you organize?
____________________________________________________________
 Done and done! I added this task:
   [T][ ] project alpha
 You now have 1 task.
____________________________________________________________
 Done and done! I added this task:
   [T][ ] middle
 You now have 2 tasks.
____________________________________________________________
 Done and done! I added this task:
   [D][ ] project deadline (by: Dec 01 2099)
 You now have 3 tasks.
____________________________________________________________
 Done and done! I added this task:
   [E][ ] project event (from: 2099-12-02 14:00 to: 2099-12-02 16:00)
 You now have 4 tasks.
____________________________________________________________
 Sorry, that exact task is already in your list.
____________________________________________________________
 Sorry, please provide a deadline date in yyyy-MM-dd format.
____________________________________________________________
 Sorry, use yyyy-MM-dd, HH:mm, or yyyy-MM-dd HH:mm.
____________________________________________________________
 Sorry, an event's end must be equal to or later than its start.
____________________________________________________________
 Sorry, please use the same date/time format for an event's range.
____________________________________________________________
 Done and done! I added this task:
   [D][ ] archived (by: Jan 01 2000)
 You now have 5 tasks.
 Warning: this task contains a date or time that has already passed.
____________________________________________________________
 Done and done! I added this task:
   [E][ ] old meeting (from: 2000-01-01 to: 2000-01-02)
 You now have 6 tasks.
 Warning: this task contains a date or time that has already passed.
____________________________________________________________
 I found these matching tasks:
 1.[T][ ] project alpha
 2.[D][ ] project deadline (by: Dec 01 2099)
 3.[E][ ] project event (from: 2099-12-02 14:00 to: 2099-12-02 16:00)
____________________________________________________________
 Absolutely! Here is everything on your list:
 1.[T][ ] project alpha
 2.[T][ ] middle
 3.[D][ ] project deadline (by: Dec 01 2099)
 4.[E][ ] project event (from: 2099-12-02 14:00 to: 2099-12-02 16:00)
 5.[D][ ] archived (by: Jan 01 2000)
 6.[E][ ] old meeting (from: 2000-01-01 to: 2000-01-02)
____________________________________________________________
____________________________________________________________
 All set! I'll be right here if you need me. Bye!
____________________________________________________________
```
