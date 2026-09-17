# Griddel UI Test Plan

The expected output blocks contain only the chatbot's output. The test runner separately records the console input used for each session.

## Test: Level 4 task types and status changes

### Aim
Confirm that todo, deadline, and event tasks are stored, displayed, marked, and unmarked correctly.

### Input
```text
todo read book
deadline return book /by 2026-09-07
event project meeting /from 2026-08-31 /to 2026-09-01
list
mark 2
unmark 2
list
bye
```

### Expected output
```text
     _             _   _
    | | ___  _ __ | |_| |__   __ _ _ __
 _  | |/ _ \| '_ \| __| '_ \ / _` | '_ \
| |_| | (_) | | | | |_| | | | (_| | | | |
 \___/ \___/|_| |_|\__|_| |_|\__,_|_| |_|

____________________________________________________________
Hello! I'm Griddel.
What can I do for you?
____________________________________________________________
____________________________________________________________
Got it! I added this task:
  [T][ ] read book
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it! I added this task:
  [D][ ] return book (by: Sept 7 2026)
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it! I added this task:
  [E][ ] project meeting (from: Aug 31 2026 to: Sept 1 2026)
Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] read book
2.[D][ ] return book (by: Sept 7 2026)
3.[E][ ] project meeting (from: Aug 31 2026 to: Sept 1 2026)
____________________________________________________________
____________________________________________________________
Nice! I've marked this task as done:
[D][X] return book (by: Sept 7 2026)
____________________________________________________________
____________________________________________________________
OK, I've marked this task as not done yet:
[D][ ] return book (by: Sept 7 2026)
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] read book
2.[D][ ] return book (by: Sept 7 2026)
3.[E][ ] project meeting (from: Aug 31 2026 to: Sept 1 2026)
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

### Expected saved data
```text
T | 0 | read book
D | 0 | return book | Sept 7 2026
E | 0 | project meeting | Aug 31 2026 | Sept 1 2026
```

## Test: Invalid commands

### Aim
Confirm that malformed and unrecognised commands show specific errors and do not terminate the chatbot.

### Input
```text
todo
blah
deadline submit report
event meeting /from 2pm
mark
unmark 5
bye
```

### Expected output
```text
     _             _   _
    | | ___  _ __ | |_| |__   __ _ _ __
 _  | |/ _ \| '_ \| __| '_ \ / _` | '_ \
| |_| | (_) | | | | |_| | | | (_| | | | |
 \___/ \___/|_| |_|\__|_| |_|\__,_|_| |_|

____________________________________________________________
Hello! I'm Griddel.
What can I do for you?
____________________________________________________________
____________________________________________________________
Error: A todo needs a description after `todo`.
____________________________________________________________
____________________________________________________________
Error: I don't recognize that command. Try todo, deadline, event, list, mark, unmark, or bye.
____________________________________________________________
____________________________________________________________
Error: A deadline needs a description and a `/by` time.
____________________________________________________________
____________________________________________________________
Error: An event needs a description, a `/from` time, and a `/to` time.
____________________________________________________________
____________________________________________________________
Error: `mark` needs a task number.
____________________________________________________________
____________________________________________________________
Error: There are no tasks to modify.
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test: Todo task regression

### Aim
Confirm that a todo task can be added, marked, listed, and exited.

### Input
```text
todo read book
mark 1
list
bye
```

### Expected output
```text
     _             _   _
    | | ___  _ __ | |_| |__   __ _ _ __
 _  | |/ _ \| '_ \| __| '_ \ / _` | '_ \
| |_| | (_) | | | | |_| | | | (_| | | | |
 \___/ \___/|_| |_|\__|_| |_|\__,_|_| |_|

____________________________________________________________
Hello! I'm Griddel.
What can I do for you?
____________________________________________________________
____________________________________________________________
Got it! I added this task:
  [T][ ] read book
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Nice! I've marked this task as done:
[T][X] read book
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][X] read book
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```
