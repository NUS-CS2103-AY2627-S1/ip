# Console UI Test Plan

## Test setup

- Java version: 25
- Compile command (from the project root): `.\gradlew.bat classes`
- Launch command: `java -cp <project-root>\build\classes\java\main yappa.Yappa`
- Working directory: a new empty temporary directory for each test case, so saved tasks cannot leak between cases.
- Comparison: exact output, including whitespace and line breaks. Use `{{TIME_OF_DAY}}` only for the application's time-dependent greeting.

## Test cases

Each case starts in a new application process. This makes the `list` command the explicit state check within that case.

### TC-01: Empty todo does not create a task

**Aim:** Verify that an empty todo is rejected, leaves the task list empty, and does not prevent a subsequent valid todo from being added.

**Input:**
```text
todo
list
todo revise notes
list
bye
```

**Expected output:**
```text
____________________________________________________________
__   __                    
\ \ / /_ _ _ __  _ __  __ _ 
 \ V / _` | '_ \| '_ \/ _` |
  | | (_| | |_) | |_) | (_| |
  |_|\__,_| .__/| .__/ \__,_|
          |_|   |_|          

Good {{TIME_OF_DAY}}! I'm Yappa. Ready to yap and get stuff done!
What are we tackling today? Let's do this!
____________________________________________________________
____________________________________________________________
Todo description must not be empty :(. Yappa cannot add task.
____________________________________________________________
____________________________________________________________
Here are your current tasks: 
No tasks
____________________________________________________________
____________________________________________________________
Ok! I have added the task:
	[T] [ ] revise notes
Now you have 1 task in the list
____________________________________________________________
____________________________________________________________
Here are your current tasks: 
	1.[T] [ ] revise notes
____________________________________________________________
____________________________________________________________
	 Catch you later :)!
____________________________________________________________
```

### TC-02: Delete preserves completion state and handles the final task

**Aim:** Verify that deleting a completed task displays its completed status, then deleting the remaining final task leaves an empty list.

**Input:**
```text
todo read book
todo borrow book
mark 1
delete 1
list
delete 1
list
bye
```

**Expected output:**
```text
____________________________________________________________
__   __                    
\ \ / /_ _ _ __  _ __  __ _ 
 \ V / _` | '_ \| '_ \/ _` |
  | | (_| | |_) | |_) | (_| |
  |_|\__,_| .__/| .__/ \__,_|
          |_|   |_|          

Good {{TIME_OF_DAY}}! I'm Yappa. Ready to yap and get stuff done!
What are we tackling today? Let's do this!
____________________________________________________________
____________________________________________________________
Ok! I have added the task:
	[T] [ ] read book
Now you have 1 task in the list
____________________________________________________________
____________________________________________________________
Ok! I have added the task:
	[T] [ ] borrow book
Now you have 2 tasks in the list
____________________________________________________________
____________________________________________________________
Ok! I've marked this task as completed: 
	[X] read book
____________________________________________________________
____________________________________________________________
Ok! I will remove this task:
	[T] [X] read book
Now you have 1 task in the list
____________________________________________________________
____________________________________________________________
Here are your current tasks: 
	1.[T] [ ] borrow book
____________________________________________________________
____________________________________________________________
Ok! I will remove this task:
	[T] [ ] borrow book
Now you have 0 tasks in the list
____________________________________________________________
____________________________________________________________
Here are your current tasks: 
No tasks
____________________________________________________________
____________________________________________________________
	 Catch you later :)!
____________________________________________________________
```

### TC-03: Deadline and event commands add tasks; malformed commands are rejected without adding partial tasks

**Aim:** Verify that valid deadline and event commands add tasks, while malformed commands between them are rejected without adding partial tasks.

**Input:**
```text
deadline submit report /by Friday
deadline submit report
list
event tutorial /from 14:00 /to 16:00
event tutorial /from 14:00
list
bye
```

**Expected output:**
```text
____________________________________________________________
__   __                    
\ \ / /_ _ _ __  _ __  __ _ 
 \ V / _` | '_ \| '_ \/ _` |
  | | (_| | |_) | |_) | (_| |
  |_|\__,_| .__/| .__/ \__,_|
          |_|   |_|          

Good {{TIME_OF_DAY}}! I'm Yappa. Ready to yap and get stuff done!
What are we tackling today? Let's do this!
____________________________________________________________
____________________________________________________________
Ok! I have added the task:
	[D] [ ] submit report (by: Friday)
Now you have 1 task in the list
____________________________________________________________
____________________________________________________________
Invalid Deadline task. Please re-enter in this format: deadline <task> /by <date/time>
____________________________________________________________
____________________________________________________________
Here are your current tasks: 
	1.[D] [ ] submit report (by: Friday)
____________________________________________________________
____________________________________________________________
Ok! I have added the task:
	[E] [ ] tutorial (from: 14:00 to: 16:00)
Now you have 2 tasks in the list
____________________________________________________________
____________________________________________________________
Invalid Event task. Please re-enter in this format: event <task> /from <start> /to <end>
____________________________________________________________
____________________________________________________________
Here are your current tasks: 
	1.[D] [ ] submit report (by: Friday)
	2.[E] [ ] tutorial (from: 14:00 to: 16:00)
____________________________________________________________
____________________________________________________________
	 Catch you later :)!
____________________________________________________________
```

### TC-04: Invalid task numbers do not change completion state

**Aim:** Verify that malformed, zero, and out-of-range task numbers are rejected and that only a valid `mark` command changes the task's completion state.

**Input:**
```text
todo pay bill
mark
list
mark 0
mark 2
mark abc
mark 1
list
unmark 3
list
bye
```

**Expected output:**
```text
____________________________________________________________
__   __                    
\ \ / /_ _ _ __  _ __  __ _ 
 \ V / _` | '_ \| '_ \/ _` |
  | | (_| | |_) | |_) | (_| |
  |_|\__,_| .__/| .__/ \__,_|
          |_|   |_|          

Good {{TIME_OF_DAY}}! I'm Yappa. Ready to yap and get stuff done!
What are we tackling today? Let's do this!
____________________________________________________________
____________________________________________________________
Ok! I have added the task:
	[T] [ ] pay bill
Now you have 1 task in the list
____________________________________________________________
____________________________________________________________
Please specify a task number!
____________________________________________________________
____________________________________________________________
Here are your current tasks: 
	1.[T] [ ] pay bill
____________________________________________________________
____________________________________________________________
Task number 0 does not exist!
____________________________________________________________
____________________________________________________________
Task number 2 does not exist!
____________________________________________________________
____________________________________________________________
Please give me a valid task number!
____________________________________________________________
____________________________________________________________
Ok! I've marked this task as completed: 
	[X] pay bill
____________________________________________________________
____________________________________________________________
Here are your current tasks: 
	1.[T] [X] pay bill
____________________________________________________________
____________________________________________________________
Task number 3 does not exist!
____________________________________________________________
____________________________________________________________
Here are your current tasks: 
	1.[T] [X] pay bill
____________________________________________________________
____________________________________________________________
	 Catch you later :)!
____________________________________________________________
```

### TC-05: Unknown command does not end the session or add a task

**Aim:** Verify that an unrecognised command is rejected without modifying the empty list, and that the next valid command is still processed.

**Input:**
```text
remind me later
list
todo buy milk
list
bye
```

**Expected output:**
```text
____________________________________________________________
__   __                    
\ \ / /_ _ _ __  _ __  __ _ 
 \ V / _` | '_ \| '_ \/ _` |
  | | (_| | |_) | |_) | (_| |
  |_|\__,_| .__/| .__/ \__,_|
          |_|   |_|          

Good {{TIME_OF_DAY}}! I'm Yappa. Ready to yap and get stuff done!
What are we tackling today? Let's do this!
____________________________________________________________
____________________________________________________________
Oh no...sorry, I am not sure what you mean :(
____________________________________________________________
____________________________________________________________
Here are your current tasks: 
No tasks
____________________________________________________________
____________________________________________________________
Ok! I have added the task:
	[T] [ ] buy milk
Now you have 1 task in the list
____________________________________________________________
____________________________________________________________
Here are your current tasks: 
	1.[T] [ ] buy milk
____________________________________________________________
____________________________________________________________
	 Catch you later :)!
____________________________________________________________
```

### TC-06: Event with adjacent /from and /to markers does not crash

**Aim:** Verify that a malformed event where /from and /to appear with no value between them (an edge case in the split-delimiter logic) is rejected gracefully rather than crashing the program.

**Input:**
```text
event task /from /to end
list
bye
```

**Expected output:**
```text
____________________________________________________________
__   __                    
\ \ / /_ _ _ __  _ __  __ _ 
 \ V / _` | '_ \| '_ \/ _` |
  | | (_| | |_) | |_) | (_| |
  |_|\__,_| .__/| .__/ \__,_|
          |_|   |_|          

Good {{TIME_OF_DAY}}! I'm Yappa. Ready to yap and get stuff done!
What are we tackling today? Let's do this!
____________________________________________________________
____________________________________________________________
Event description, /from, and /to fields must not be empty :(. Yappa cannot add task.
____________________________________________________________
____________________________________________________________
Here are your current tasks: 
No tasks
____________________________________________________________
____________________________________________________________
	 Catch you later :)!
____________________________________________________________
```

### TC-07: Delete removes the correct task and shifts indices

**Aim:** Verify that `delete` removes the specified task, reports it correctly, and that subsequent `list` reflects the updated (renumbered) task list.

**Input:**
```text
todo read book
deadline return book /by Friday
event project meeting /from 14:00 /to 15:00
list
delete 2
list
bye
```

**Expected output:**
```text
____________________________________________________________
__   __                    
\ \ / /_ _ _ __  _ __  __ _ 
 \ V / _` | '_ \| '_ \/ _` |
  | | (_| | |_) | |_) | (_| |
  |_|\__,_| .__/| .__/ \__,_|
          |_|   |_|          

Good {{TIME_OF_DAY}}! I'm Yappa. Ready to yap and get stuff done!
What are we tackling today? Let's do this!
____________________________________________________________
____________________________________________________________
Ok! I have added the task:
	[T] [ ] read book
Now you have 1 task in the list
____________________________________________________________
____________________________________________________________
Ok! I have added the task:
	[D] [ ] return book (by: Friday)
Now you have 2 tasks in the list
____________________________________________________________
____________________________________________________________
Ok! I have added the task:
	[E] [ ] project meeting (from: 14:00 to: 15:00)
Now you have 3 tasks in the list
____________________________________________________________
____________________________________________________________
Here are your current tasks: 
	1.[T] [ ] read book
	2.[D] [ ] return book (by: Friday)
	3.[E] [ ] project meeting (from: 14:00 to: 15:00)
____________________________________________________________
____________________________________________________________
Ok! I will remove this task:
	[D] [ ] return book (by: Friday)
Now you have 2 tasks in the list
____________________________________________________________
____________________________________________________________
Here are your current tasks: 
	1.[T] [ ] read book
	2.[E] [ ] project meeting (from: 14:00 to: 15:00)
____________________________________________________________
____________________________________________________________
	 Catch you later :)!
____________________________________________________________
```

### TC-08: Invalid delete numbers do not change the list

**Aim:** Verify that malformed, zero, and out-of-range delete arguments are rejected without removing any task.

**Input:**
```text
todo pay bill
delete
delete 0
delete 2
delete abc
list
delete 1
list
bye
```

**Expected output:**
```text
____________________________________________________________
__   __                    
\ \ / /_ _ _ __  _ __  __ _ 
 \ V / _` | '_ \| '_ \/ _` |
  | | (_| | |_) | |_) | (_| |
  |_|\__,_| .__/| .__/ \__,_|
          |_|   |_|          

Good {{TIME_OF_DAY}}! I'm Yappa. Ready to yap and get stuff done!
What are we tackling today? Let's do this!
____________________________________________________________
____________________________________________________________
Ok! I have added the task:
	[T] [ ] pay bill
Now you have 1 task in the list
____________________________________________________________
____________________________________________________________
Please specify a task number!
____________________________________________________________
____________________________________________________________
Task number 0 does not exist!
____________________________________________________________
____________________________________________________________
Task number 2 does not exist!
____________________________________________________________
____________________________________________________________
Please give me a valid task number!
____________________________________________________________
____________________________________________________________
Here are your current tasks: 
	1.[T] [ ] pay bill
____________________________________________________________
____________________________________________________________
Ok! I will remove this task:
	[T] [ ] pay bill
Now you have 0 tasks in the list
____________________________________________________________
____________________________________________________________
Here are your current tasks: 
No tasks
____________________________________________________________
____________________________________________________________
	 Catch you later :)!
____________________________________________________________
```
