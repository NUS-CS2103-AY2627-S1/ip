# Console UI Test Plan

The expected-output blocks contain program stdout only. Terminal input echo is
shown separately by the test runner as part of the test-session record.

## Deletion preserves remaining task state
Aim: Verify invalid deletion leaves tasks unchanged, valid deletion removes the selected task, and remaining tasks are renumbered.
### Input
```text
todo
todo read book
blah
deadline return book
deadline return book /by Sunday
deadline return book /by 2019-02-30
deadline return book /by 2019-10-15
event project meeting /from Mon 2pm
event project meeting /from Mon 2pm /to 4pm
delete 4
delete 3
mark two
mark 2
unmark 1
list
find BOOK
find unicorn
find
bye
```
### Expected output
```text
____________________________________________________________
 ____  _____    _    _   _ __     __ _    ____  ____  
/ ___||_   _|  / \  | \ | |\ \   / / / \  |  _ \|  _ \ 
\___ \  | |   / _ \ |  \| | \ \ / / / _ \ | |_) | | | |
 ___) | | |  / ___ \| |\  |  \ V / / ___ \|  _ <| |_| |
|____/  |_| /_/   \_\_| \_|   \_/ /_/   \_\_| \_\____/ 
Hello! I'm StanVard, your friendly task companion.
What shall we tackle today?
____________________________________________________________
____________________________________________________________
OOPS!!! The description of a todo cannot be empty.
____________________________________________________________
____________________________________________________________
Nice! I've added this task:
  [T][ ] read book
You're up to 1 tasks now — nicely organized.
____________________________________________________________
____________________________________________________________
OOPS!!! I'm sorry, but I don't know what that means :-(
____________________________________________________________
____________________________________________________________
OOPS!!! A deadline must include /by followed by a date.
____________________________________________________________
____________________________________________________________
OOPS!!! The deadline date must be in yyyy-MM-dd format.
____________________________________________________________
____________________________________________________________
OOPS!!! The deadline date must be in yyyy-MM-dd format.
____________________________________________________________
____________________________________________________________
Nice! I've added this task:
  [D][ ] return book (by: Oct 15 2019)
You're up to 2 tasks now — nicely organized.
____________________________________________________________
____________________________________________________________
OOPS!!! An event must include /from and /to times.
____________________________________________________________
____________________________________________________________
Nice! I've added this task:
  [E][ ] project meeting (from: Mon 2pm to: 4pm)
You're up to 3 tasks now — nicely organized.
____________________________________________________________
____________________________________________________________
OOPS!!! The task number is out of range.
____________________________________________________________
____________________________________________________________
Poof! I've removed this task:
  [E][ ] project meeting (from: Mon 2pm to: 4pm)
You're down to 2 tasks now.
____________________________________________________________
____________________________________________________________
OOPS!!! The task number must be a positive integer.
____________________________________________________________
____________________________________________________________
All set! I've marked this task as done:
  [D][X] return book (by: Oct 15 2019)
____________________________________________________________
____________________________________________________________
No problem! I've marked this task as not done yet:
  [T][ ] read book
____________________________________________________________
____________________________________________________________
Here are your tasks:
1.[T][ ] read book
2.[D][X] return book (by: Oct 15 2019)
____________________________________________________________
____________________________________________________________
Here are the tasks matching your search:
1.[T][ ] read book
2.[D][X] return book (by: Oct 15 2019)
____________________________________________________________
____________________________________________________________
Here are the tasks matching your search:
No matching tasks found.
____________________________________________________________
____________________________________________________________
OOPS!!! The keyword to find cannot be empty.
____________________________________________________________
Take care! Bye. Hope to see you again soon!
____________________________________________________________
```

## Saved tasks are loaded at startup
Aim: Verify tasks and their completion state persist into a new application session.
### Input
```text
list
bye
```
### Expected output
```text
____________________________________________________________
 ____  _____    _    _   _ __     __ _    ____  ____  
/ ___||_   _|  / \  | \ | |\ \   / / / \  |  _ \|  _ \ 
\___ \  | |   / _ \ |  \| | \ \ / / / _ \ | |_) | | | |
 ___) | | |  / ___ \| |\  |  \ V / / ___ \|  _ <| |_| |
|____/  |_| /_/   \_\_| \_|   \_/ /_/   \_\_| \_\____/ 
Hello! I'm StanVard, your friendly task companion.
What shall we tackle today?
____________________________________________________________
____________________________________________________________
Here are your tasks:
1.[T][ ] read book
2.[D][X] return book (by: Oct 15 2019)
____________________________________________________________
Take care! Bye. Hope to see you again soon!
____________________________________________________________
```
