# UI Test Plan

The executable JUnit tests are in `src/test/java/UiTest.java`. The cases alternate between valid and invalid flows. Each invalid flow follows or precedes a valid action and then runs `list`, which verifies that invalid input does not add or mutate a task.

## Test case: Reject a blank command and continue

**Aim:** Verify that an empty command receives a helpful response and does not stop the application.

### Input
```text

list
bye
```

### Expected output
```text
Hello! I'm TardT.
What can I do for you?
____________________________________________________________

____________________________________________________________
Please enter a command.
____________________________________________________________
____________________________________________________________
Your task list is empty!
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test case: Reject an invalid priority without adding a task

**Aim:** Verify that a malformed priority flag does not become part of a task description or create a task.

### Input
```text
todo /priority urgent
list
bye
```

### Expected output
```text
Hello! I'm TardT.
What can I do for you?
____________________________________________________________

____________________________________________________________
Invalid priority. Priority is optional; when included, use /priority low, /priority medium, or /priority high.
____________________________________________________________
____________________________________________________________
Your task list is empty!
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test case: Reject repeated priority flags without adding a task

**Aim:** Verify that repeated priority flags are rejected instead of one being included in the task description.

### Input
```text
todo read book /priority low /priority high
list
bye
```

### Expected output
```text
Hello! I'm TardT.
What can I do for you?
____________________________________________________________

____________________________________________________________
Invalid priority. Priority is optional; when included, use /priority low, /priority medium, or /priority high.
____________________________________________________________
____________________________________________________________
Your task list is empty!
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test case: Reject an event whose end is before its start

**Aim:** Verify that an impossible event time range does not create a task.

### Input
```text
event workshop /from 2026-10-20T16:00 /to 2026-10-20T15:00
list
bye
```

### Expected output
```text
Hello! I'm TardT.
What can I do for you?
____________________________________________________________

____________________________________________________________
Event end time must be after its start time.
____________________________________________________________
____________________________________________________________
Your task list is empty!
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test case: Exit the application

**Aim:** Verify that the application shows its welcome message and exits politely when the user enters `bye`.

### Input
```text
bye
```

### Expected output
```text
Hello! I'm tardt.tardt. 
What can I do for you? 
________________________________

________________________________
Bye. Hope to see you again soon!
________________________________
```

## Test case: Save a newly added task

**Aim:** Verify that a successful task addition writes the current task list to `data/tardt.txt`.

### Input
```text
todo read book
bye
```

### Expected output
```text
Hello! I'm tardt.tardt. 
What can I do for you? 
________________________________

________________________________
Got it. I've added this task:
  [T][ ] read book
Now you have 1 tasks in the list.
________________________________
________________________________
Bye. Hope to see you again soon!
________________________________
```

### Expected saved data
```text
T | 0 | read book
```

## Test case: Load saved tasks at startup

**Aim:** Verify that tasks from `data/tardt.txt` are reconstructed and listed when the application starts.

### Initial saved data
```text
T | 1 | read book
D | 0 | return book | June 6th
E | 0 | project meeting | Aug 6th 2pm | 4pm
```

### Input
```text
list
bye
```

### Expected output
```text
Hello! I'm tardt.tardt. 
What can I do for you? 
________________________________

________________________________
1. [T][X] read book
2. [D][ ] return book (by: June 6th)
3. [E][ ] project meeting (from: Aug 6th 2pm to: 4pm)
________________________________
________________________________
Bye. Hope to see you again soon!
________________________________
```

## Test case: Reject an unknown command without changing tasks

**Aim:** Verify that an invalid command is rejected and that the following valid command creates the first task.

### Input
```text
nonsense
todo read book
list
bye
```

### Expected output
```text
Hello! I'm tardt.tardt. 
What can I do for you? 
________________________________

________________________________
'nonsense' is not a valid input.
Valid input formats: 
bye -> exits the interface
list -> lists all the tasks and their status
mark [task number] -> marks the task and show their status
unmark [task number] -> unmarks the task and show their status
Priority is optional; omit it to use low priority.
todo [task name] (optional: /priority low|medium|high) -> adds a todo task to taskList
deadline [task name] /by [deadline] (optional: /priority low|medium|high) -> adds a deadline task to taskList
event [task name] /from [start time] /to [end time] (optional: /priority low|medium|high) -> adds an event task to taskList
delete [task number] -> deletes a task from taskList
________________________________
________________________________
Got it. I've added this task:
  [T][ ] read book
Now you have 1 tasks in the list.
________________________________
________________________________
1. [T][ ] read book
________________________________
________________________________
Bye. Hope to see you again soon!
________________________________
```

## Test case: Delete a task and list the remaining task

**Aim:** Verify that `delete` removes the specified task and `list` renumbers the remaining task.

### Input
```text
todo read book
deadline submit assignment /by Friday
delete 1
list
bye
```

### Expected output
```text
Hello! I'm tardt.tardt. 
What can I do for you? 
________________________________

________________________________
Got it. I've added this task:
  [T][ ] read book
Now you have 1 tasks in the list.
________________________________
________________________________
    Got it. I've added this task:
      [D][ ] submit assignment (by: Friday)
    Now you have 2 tasks in the list.
________________________________
________________________________
Noted, I've removed this task: 
  [T][ ] read book
    Now you have 1 tasks in the list.
________________________________
________________________________
1. [D][ ] submit assignment (by: Friday)
________________________________
________________________________
Bye. Hope to see you again soon!
________________________________
```

## Test case: Reject a non-numeric delete without changing tasks

**Aim:** Verify that an invalid delete number is rejected and the existing task remains in the list.

### Input
```text
todo read book
delete one
list
bye
```

### Expected output
```text
Hello! I'm tardt.tardt. 
What can I do for you? 
________________________________

________________________________
Got it. I've added this task:
  [T][ ] read book
Now you have 1 tasks in the list.
________________________________
________________________________
    'one' is not a valid integer.
________________________________
________________________________
1. [T][ ] read book
________________________________
________________________________
Bye. Hope to see you again soon!
________________________________
```

## Test case: Add and list a deadline

**Aim:** Verify that a valid deadline is stored and displayed with its deadline value.

### Input
```text
deadline submit assignment /by Friday
list
bye
```

### Expected output
```text
Hello! I'm tardt.tardt. 
What can I do for you? 
________________________________

________________________________
    Got it. I've added this task:
      [D][ ] submit assignment (by: Friday)
    Now you have 1 tasks in the list.
________________________________
________________________________
1. [D][ ] submit assignment (by: Friday)
________________________________
________________________________
Bye. Hope to see you again soon!
________________________________
```

## Test case: Reject a malformed deadline without changing tasks

**Aim:** Verify that a malformed deadline is rejected and does not add a second task.

### Input
```text
todo read book
deadline submit assignment
list
bye
```

### Expected output
```text
Hello! I'm tardt.tardt. 
What can I do for you? 
________________________________

________________________________
Got it. I've added this task:
  [T][ ] read book
Now you have 1 tasks in the list.
________________________________
________________________________
    Invalid format. Use: deadline [task name] /by [deadline] (optional: /priority low|medium|high).
________________________________
________________________________
1. [T][ ] read book
________________________________
________________________________
Bye. Hope to see you again soon!
________________________________
```

## Test case: Add and list an event

**Aim:** Verify that a valid event is stored and displayed with its start and end times.

### Input
```text
event lecture /from 2pm /to 4pm
list
bye
```

### Expected output
```text
Hello! I'm tardt.tardt. 
What can I do for you? 
________________________________

________________________________
    Got it. I've added this task:
      [E][ ] lecture (from: 2pm to: 4pm)
    Now you have 1 tasks in the list.
________________________________
________________________________
1. [E][ ] lecture (from: 2pm to: 4pm)
________________________________
________________________________
Bye. Hope to see you again soon!
________________________________
```

## Test case: Reject a non-numeric mark without changing task status

**Aim:** Verify that an invalid mark number is rejected and the existing task remains unmarked.

### Input
```text
todo read book
mark one
list
bye
```

### Expected output
```text
Hello! I'm tardt.tardt. 
What can I do for you? 
________________________________

________________________________
Got it. I've added this task:
  [T][ ] read book
Now you have 1 tasks in the list.
________________________________
________________________________
'mark one' is not a valid integer.
________________________________
________________________________
________________________________
1. [T][ ] read book
________________________________
________________________________
Bye. Hope to see you again soon!
________________________________
```
