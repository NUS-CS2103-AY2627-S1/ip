# UI test plan

Each command is run in a fresh process. Expected output is compared exactly.

## Test case: list an empty task list

Aim: Verify that `list` gives useful feedback when there are no saved tasks.

### Command
```text
list
bye
```

### Expected output
```text
____________________________________________________________
 __  __              _
|  \/  | ___  _ __  | | _____ _   _
| |\/| |/ _ \| '_ \ | |/ / _ \ | | |
| |  | | (_) | | | ||   <  __/ |_| |
|_|  |_|\___/|_| |_||_|\_\___|\__, |
                              |___/

Hello! I'm Monkey, your cheeky little assistant.
What can I do for you today?
____________________________________________________________
____________________________________________________________
There are no tasks in your list.
____________________________________________________________
____________________________________________________________
Bye! Keep swinging, and I hope to see you again soon!
____________________________________________________________
```

## Test case: find tasks by keyword

Aim: Verify that `find` displays matching tasks in their original order.

### Command
```text
todo read book
todo buy milk
todo return book
find book
bye
```

### Expected output
```text
____________________________________________________________
 __  __              _
|  \/  | ___  _ __  | | _____ _   _
| |\/| |/ _ \| '_ \ | |/ / _ \ | | |
| |  | | (_) | | | ||   <  __/ |_| |
|_|  |_|\___/|_| |_||_|\_\___|\__, |
                              |___/

Hello! I'm Monkey, your cheeky little assistant.
What can I do for you today?
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] read book
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] buy milk
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] return book
Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
Here are the matching tasks in your list:
1.[T][ ] read book
2.[T][ ] return book
____________________________________________________________
____________________________________________________________
Bye! Keep swinging, and I hope to see you again soon!
____________________________________________________________
```

## Test case: exit immediately

Aim: Verify that the program starts and exits when the user enters `bye`.

### Command
```text
bye
```

### Expected output
```text
____________________________________________________________
 __  __              _
|  \/  | ___  _ __  | | _____ _   _
| |\/| |/ _ \| '_ \ | |/ / _ \ | | |
| |  | | (_) | | | ||   <  __/ |_| |
|_|  |_|\___/|_| |_||_|\_\___|\__, |
                              |___/

Hello! I'm Monkey, your cheeky little assistant.
What can I do for you today?
____________________________________________________________
____________________________________________________________
Bye! Keep swinging, and I hope to see you again soon!
____________________________________________________________
```

## Test case: delete a task and renumber remaining tasks

Aim: Verify that a task can be deleted and that the remaining tasks are renumbered.

### Command
```text
todo first
todo second
delete 1
list
bye
```

### Expected output
```text
____________________________________________________________
 __  __              _
|  \/  | ___  _ __  | | _____ _   _
| |\/| |/ _ \| '_ \ | |/ / _ \ | | |
| |  | | (_) | | | ||   <  __/ |_| |
|_|  |_|\___/|_| |_||_|\_\___|\__, |
                              |___/

Hello! I'm Monkey, your cheeky little assistant.
What can I do for you today?
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] first
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] second
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Noted. I've removed this task:
  [T][ ] first
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] second
____________________________________________________________
____________________________________________________________
Bye! Keep swinging, and I hope to see you again soon!
____________________________________________________________
```

## Test case: reject non-numeric mark and preserve task state

Aim: Verify that an invalid `mark` argument is rejected without changing a valid task.

### Command
```text
todo buy milk
mark abc
list
bye
```

### Expected output
```text
____________________________________________________________
 __  __              _
|  \/  | ___  _ __  | | _____ _   _
| |\/| |/ _ \| '_ \ | |/ / _ \ | | |
| |  | | (_) | | | ||   <  __/ |_| |
|_|  |_|\___/|_| |_||_|\_\___|\__, |
                              |___/

Hello! I'm Monkey, your cheeky little assistant.
What can I do for you today?
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] buy milk
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
OOPS! Monkey says: That banana-shaped task number does not look right. Use a number after 'mark'.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] buy milk
____________________________________________________________
____________________________________________________________
Bye! Keep swinging, and I hope to see you again soon!
____________________________________________________________
```

## Test case: reject invalid task indexes

Aim: Verify that zero and out-of-range task numbers do not alter the task list.

### Command
```text
todo submit
mark 0
mark 2
list
bye
```

### Expected output
```text
____________________________________________________________
 __  __              _
|  \/  | ___  _ __  | | _____ _   _
| |\/| |/ _ \| '_ \ | |/ / _ \ | | |
| |  | | (_) | | | ||   <  __/ |_| |
|_|  |_|\___/|_| |_||_|\_\___|\__, |
                              |___/

Hello! I'm Monkey, your cheeky little assistant.
What can I do for you today?
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] submit
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
That task number does not exist.
____________________________________________________________
____________________________________________________________
That task number does not exist.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] submit
____________________________________________________________
____________________________________________________________
Bye! Keep swinging, and I hope to see you again soon!
____________________________________________________________
```

## Test case: interleave mark, invalid unmark, and unmark

Aim: Verify that a valid completion persists through an invalid command and can then be reversed by a valid command.

### Command
```text
todo read
mark 1
unmark nope
list
unmark 1
list
bye
```

### Expected output
```text
____________________________________________________________
 __  __              _
|  \/  | ___  _ __  | | _____ _   _
| |\/| |/ _ \| '_ \ | |/ / _ \ | | |
| |  | | (_) | | | ||   <  __/ |_| |
|_|  |_|\___/|_| |_||_|\_\___|\__, |
                              |___/

Hello! I'm Monkey, your cheeky little assistant.
What can I do for you today?
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] read
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Nice! I've marked this task as done:
  [T][X] read
____________________________________________________________
____________________________________________________________
OOPS! Monkey says: This monkey needs a valid task number after 'unmark'.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][X] read
____________________________________________________________
____________________________________________________________
OK, I've marked this task as not done yet:
  [T][ ] read
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] read
____________________________________________________________
____________________________________________________________
Bye! Keep swinging, and I hope to see you again soon!
____________________________________________________________
```

## Test case: add a deadline

Aim: Verify that a deadline command stores both its description and due date/time.

### Command
```text
deadline submit report /by 11/10/2019 1700
list
bye
```

### Expected output
```text
____________________________________________________________
 __  __              _
|  \/  | ___  _ __  | | _____ _   _
| |\/| |/ _ \| '_ \ | |/ / _ \ | | |
| |  | | (_) | | | ||   <  __/ |_| |
|_|  |_|\___/|_| |_||_|\_\___|\__, |
                              |___/

Hello! I'm Monkey, your cheeky little assistant.
What can I do for you today?
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [D][ ] submit report (by: Oct 11 2019, 5:00 PM)
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[D][ ] submit report (by: Oct 11 2019, 5:00 PM)
____________________________________________________________
____________________________________________________________
Bye! Keep swinging, and I hope to see you again soon!
____________________________________________________________
```

## Test case: add and list a todo

Aim: Verify that a todo command creates a typed todo and that it is displayed in the list.

### Command
```text
todo borrow book
list
bye
```

### Expected output
```text
____________________________________________________________
 __  __              _
|  \/  | ___  _ __  | | _____ _   _
| |\/| |/ _ \| '_ \ | |/ / _ \ | | |
| |  | | (_) | | | ||   <  __/ |_| |
|_|  |_|\___/|_| |_||_|\_\___|\__, |
                              |___/

Hello! I'm Monkey, your cheeky little assistant.
What can I do for you today?
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] borrow book
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] borrow book
____________________________________________________________
____________________________________________________________
Bye! Keep swinging, and I hope to see you again soon!
____________________________________________________________
```

## Test case: add an event

Aim: Verify that an event command stores its description, start, and end date/time values.

### Command
```text
event project meeting /from Mon 2pm /to 4pm
list
bye
```

### Expected output
```text
____________________________________________________________
 __  __              _
|  \/  | ___  _ __  | | _____ _   _
| |\/| |/ _ \| '_ \ | |/ / _ \ | | |
| |  | | (_) | | | ||   <  __/ |_| |
|_|  |_|\___/|_| |_||_|\_\___|\__, |
                              |___/

Hello! I'm Monkey, your cheeky little assistant.
What can I do for you today?
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [E][ ] project meeting (from: Mon 2pm to: 4pm)
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[E][ ] project meeting (from: Mon 2pm to: 4pm)
____________________________________________________________
____________________________________________________________
Bye! Keep swinging, and I hope to see you again soon!
____________________________________________________________
```

## Test case: snooze an incomplete deadline

Aim: Verify that `snooze` updates a deadline and keeps the task in its
original position.

### Command
```text
deadline submit report /by 2099-01-01
todo buy milk
snooze 1 2099-02-01
list
bye
```

### Expected output
```text
____________________________________________________________
 __  __              _
|  \/  | ___  _ __  | | _____ _   _
| |\/| |/ _ \| '_ \ | |/ / _ \ | | |
| |  | | (_) | | | ||   <  __/ |_| |
|_|  |_|\___/|_| |_||_|\_\___|\__, |
                              |___/

Hello! I'm Monkey, your cheeky little assistant.
What can I do for you today?
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [D][ ] submit report (by: Jan 01 2099)
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] buy milk
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Snoozed this task to Feb 01 2099:
  [D][ ] submit report (by: Feb 01 2099)
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[D][ ] submit report (by: Feb 01 2099)
2.[T][ ] buy milk
____________________________________________________________
____________________________________________________________
Bye! Keep swinging, and I hope to see you again soon!
____________________________________________________________
```

The subsequent list must show the snoozed deadline as task 1 and the todo as
task 2.

## Test case: reject invalid snooze requests

Aim: Verify that snoozing todos and invalid task numbers does not change task
state.

### Command
```text
todo buy milk
snooze 1 2099-02-01
snooze abc 2099-02-01
bye
```

### Expected output
```text
____________________________________________________________
 __  __              _
|  \/  | ___  _ __  | | _____ _   _
| |\/| |/ _ \| '_ \ | |/ / _ \ | | |
| |  | | (_) | | | ||   <  __/ |_| |
|_|  |_|\___/|_| |_||_|\_\___|\__, |
                              |___/

Hello! I'm Monkey, your cheeky little assistant.
What can I do for you today?
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] buy milk
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Only deadline tasks can be snoozed.
____________________________________________________________
____________________________________________________________
OOPS! Monkey says: Please provide a valid task number after 'snooze'.
____________________________________________________________
____________________________________________________________
Bye! Keep swinging, and I hope to see you again soon!
____________________________________________________________
```

## Test case: normalize whitespace and reject duplicate tasks

Aim: Verify that extra whitespace is normalized while equivalent tasks and
unexpected command arguments are rejected.

### Command
```text
   todo    buy   milk
todo buy milk
list extra
bye
```

### Expected output
```text
____________________________________________________________
 __  __              _
|  \/  | ___  _ __  | | _____ _   _
| |\/| |/ _ \| '_ \ | |/ / _ \ | | |
| |  | | (_) | | | ||   <  __/ |_| |
|_|  |_|\___/|_| |_||_|\_\___|\__, |
                              |___/

Hello! I'm Monkey, your cheeky little assistant.
What can I do for you today?
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] buy milk
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
OOPS! Monkey says: That task is already in your list.
____________________________________________________________
____________________________________________________________
OOPS! Monkey says: The 'list' command does not accept arguments.
____________________________________________________________
____________________________________________________________
Bye! Keep swinging, and I hope to see you again soon!
____________________________________________________________
```

## Test case: reject invalid temporal data and command markers

Aim: Verify that impossible and non-chronological event dates and repeated
deadline markers are rejected without adding tasks.

### Command
```text
event meeting /from 2026-02-30 1400 /to 2026-03-01 1400
event meeting /from 2026-09-20 /to 2026-09-20
deadline submit /by 2026-09-20 /by 2026-09-21
bye
```

### Expected output
```text
____________________________________________________________
 __  __              _
|  \/  | ___  _ __  | | _____ _   _
| |\/| |/ _ \| '_ \ | |/ / _ \ | | |
| |  | | (_) | | | ||   <  __/ |_| |
|_|  |_|\___/|_| |_||_|\_\___|\__, |
                              |___/

Hello! I'm Monkey, your cheeky little assistant.
What can I do for you today?
____________________________________________________________
____________________________________________________________
Use event times like yyyy-mm-dd HHmm, d/M/yyyy HHmm, or h:mm am/pm.
____________________________________________________________
____________________________________________________________
An event must end after it starts.
____________________________________________________________
____________________________________________________________
OOPS! Monkey says: Use exactly one /by marker for a deadline.
____________________________________________________________
____________________________________________________________
Bye! Keep swinging, and I hope to see you again soon!
____________________________________________________________
```
