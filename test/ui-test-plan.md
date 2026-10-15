# Level 4 UI test plan

## Test case: Delete tasks and preserve list state

### Aim
Verify deletion removes the selected task, updates the task count, and invalid deletion input does not terminate the chatbot.

### Input
```text
todo one
delete 1
list
delete nope
bye
```

### Expected output
```text
 _   _  _____  __  __  _   _  _____
| \ | || ____| \ \/ / | | | ||  ___|
|  \| ||  _|    \  /  | | | || |___ 
| |\  || |___   /  \  | |_| | ___| |
|_| \_||_____| /_/\_\  \___/ |____/ 
Hello! I'm Nexus.
What can I do for you?
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] one
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Noted. I've removed this task:
  [T][ ] one
Now you have 0 tasks in the list.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
____________________________________________________________
____________________________________________________________
Please specify a valid task number.
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test case: Interleave valid and invalid commands

### Aim
Verify invalid commands do not consume task slots or affect task ordering and completion state.

### Input
```text
todo alpha
todo
deadline beta /by tomorrow
deadline broken
event gamma /from 2pm /to 4pm
event bad /from /to
list
mark 2
unmark 2
list
bye
```

### Expected output
```text
 _   _  _____  __  __  _   _  _____
| \ | || ____| \ \/ / | | | ||  ___|
|  \| ||  _|    \  /  | | | || |___ 
| |\  || |___   /  \  | |_| | ___| |
|_| \_||_____| /_/\_\  \___/ |____/ 
Hello! I'm Nexus.
What can I do for you?
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] alpha
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
OOPS!!! A todo needs a description.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [D][ ] beta (by: tomorrow)
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
OOPS!!! A deadline needs a description and a /by date.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [E][ ] gamma (from: 2pm to: 4pm)
Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
OOPS!!! An event needs a description, /from time, and /to time.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] alpha
2.[D][ ] beta (by: tomorrow)
3.[E][ ] gamma (from: 2pm to: 4pm)
____________________________________________________________
____________________________________________________________
Nice! I've marked this task as done:
  [D][X] beta (by: tomorrow)
____________________________________________________________
____________________________________________________________
OK, I've marked this task as not done yet:
  [D][ ] beta (by: tomorrow)
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] alpha
2.[D][ ] beta (by: tomorrow)
3.[E][ ] gamma (from: 2pm to: 4pm)
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test case: Add, list, mark, and unmark task types

### Aim
Verify Level 4 commands create the correct subclasses, preserve details in `list`, and show the full task format when marking and unmarking.

### Input
```text
todo borrow book
deadline return book /by Sunday
event project meeting /from Mon 2pm /to 4pm
list
mark 2
unmark 2
bye
```

### Expected output
```text
 _   _  _____  __  __  _   _  _____
| \ | || ____| \ \/ / | | | ||  ___|
|  \| ||  _|    \  /  | | | || |___ 
| |\  || |___   /  \  | |_| | ___| |
|_| \_||_____| /_/\_\  \___/ |____/ 
Hello! I'm Nexus.
What can I do for you?
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] borrow book
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [D][ ] return book (by: Sunday)
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [E][ ] project meeting (from: Mon 2pm to: 4pm)
Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] borrow book
2.[D][ ] return book (by: Sunday)
3.[E][ ] project meeting (from: Mon 2pm to: 4pm)
____________________________________________________________
____________________________________________________________
Nice! I've marked this task as done:
  [D][X] return book (by: Sunday)
____________________________________________________________
____________________________________________________________
OK, I've marked this task as not done yet:
  [D][ ] return book (by: Sunday)
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test case: Handle invalid task commands

### Aim
Verify empty, unknown, and malformed commands produce friendly errors without terminating Nexus.

### Input
```text
todo
blah
deadline
event
bye
```

### Expected output
```text
 _   _  _____  __  __  _   _  _____
| \ | || ____| \ \/ / | | | ||  ___|
|  \| ||  _|    \  /  | | | || |___ 
| |\  || |___   /  \  | |_| | ___| |
|_| \_||_____| /_/\_\  \___/ |____/ 
Hello! I'm Nexus.
What can I do for you?
____________________________________________________________
____________________________________________________________
OOPS!!! A todo needs a description.
____________________________________________________________
____________________________________________________________
OOPS!!! I'm sorry, but I don't know what that means.
____________________________________________________________
____________________________________________________________
OOPS!!! A deadline needs a description and a /by date.
____________________________________________________________
____________________________________________________________
OOPS!!! An event needs a description, /from time, and /to time.
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```
