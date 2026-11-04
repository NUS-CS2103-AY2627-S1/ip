# UI Test Plan

## Configuration

- Build command: `javac -d _temp/ui-test-classes src/main/java/soar/Soar.java src/main/java/soar/command/*.java src/main/java/soar/exception/*.java src/main/java/soar/parser/*.java src/main/java/soar/storage/*.java src/main/java/soar/task/*.java src/main/java/soar/ui/*.java test/StorageTest.java test/TaskListTest.java test/CommandTest.java && java -ea -cp _temp/ui-test-classes StorageTest && java -ea -cp _temp/ui-test-classes TaskListTest && java -ea -cp _temp/ui-test-classes CommandTest`
- Run command: `powershell -NoProfile -ExecutionPolicy Bypass -File test/run-ui-test.ps1`
- Working directory: `.`
- Timeout seconds: `60`
- Comparison: `exact after normalizing CRLF and CR line endings to LF`

## Test cases

### TC-001 — Polymorphic task types

- Aim: Verify todo, deadline, and event formatting through the shared task list.

#### Inputs

```text
todo borrow book
deadline return book /by 2/12/2019 1800
event project meeting /from Mon 2pm /to 4pm
deadline do homework /by 2019-10-15
mark 2
list
bye
```

#### Expected output

```text
____________________________________________________________
 ____                    
/ ___|  ___   __ _ _ __  
\___ \ / _ \ / _` | '__| 
 ___) | (_) | (_| | |    
|____/ \___/ \__,_|_|    
Hey there! I'm Soar, your upbeat little sidekick!
What exciting thing can I help you tackle today?
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] borrow book
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [D][ ] return book (by: Dec 02 2019, 6:00PM)
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [E][ ] project meeting (from: Mon 2pm to: 4pm)
Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [D][ ] do homework (by: Oct 15 2019)
Now you have 4 tasks in the list.
____________________________________________________________
____________________________________________________________
Nice! I've marked this task as done:
  [D][X] return book (by: Dec 02 2019, 6:00PM)
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] borrow book
2.[D][X] return book (by: Dec 02 2019, 6:00PM)
3.[E][ ] project meeting (from: Mon 2pm to: 4pm)
4.[D][ ] do homework (by: Oct 15 2019)
____________________________________________________________
____________________________________________________________
Bye! Always soar towards your goals!
____________________________________________________________
```



### TC-002 — Informative invalid-input messages

- Aim: Verify that invalid task details, task numbers, and commands identify the problem and let the user continue.

#### Inputs

```text
todo
deadline submit report
event team meeting /from noon
mark two
mark 1
blah
bye
```

#### Expected output

```text
____________________________________________________________
 ____                    
/ ___|  ___   __ _ _ __  
\___ \ / _ \ / _` | '__| 
 ___) | (_) | (_| | |    
|____/ \___/ \__,_|_|    
Hey there! I'm Soar, your upbeat little sidekick!
What exciting thing can I help you tackle today?
____________________________________________________________
____________________________________________________________
The todo description is empty. Give it a few words, and it will be ready to soar!
____________________________________________________________
____________________________________________________________
This deadline has no '/by' date. Add one so it has a clear path through the sky!
____________________________________________________________
____________________________________________________________
This event needs both '/from' and '/to' times to map its flight across the sky!
____________________________________________________________
____________________________________________________________
'two' is not a whole task number. Choose a number from your list to keep flying high!
____________________________________________________________
____________________________________________________________
Your task list is an open sky right now. Add a task before using 'mark'!
____________________________________________________________
____________________________________________________________
That command is on an unfamiliar flight path. Try list, find, date, todo, deadline, event, mark, unmark, delete, edit, or bye to keep flying high!
____________________________________________________________
____________________________________________________________
Bye! Always soar towards your goals!
____________________________________________________________
```

### TC-003 — Invalid indices preserve completion state

- Aim: Verify that out-of-range mark and unmark commands do not change a valid task's completion state.

#### Inputs

```text
todo read book
mark 2
list
mark 1
unmark 0
list
unmark 1
list
bye
```

#### Expected output

```text
____________________________________________________________
 ____                    
/ ___|  ___   __ _ _ __  
\___ \ / _ \ / _` | '__| 
 ___) | (_) | (_| | |    
|____/ \___/ \__,_|_|    
Hey there! I'm Soar, your upbeat little sidekick!
What exciting thing can I help you tackle today?
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] read book
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Task 2 is outside your list. Choose a number from 1 to 1 and we'll stay on course!
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] read book
____________________________________________________________
____________________________________________________________
Nice! I've marked this task as done:
  [T][X] read book
____________________________________________________________
____________________________________________________________
Task 0 is outside your list. Choose a number from 1 to 1 and we'll stay on course!
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][X] read book
____________________________________________________________
____________________________________________________________
OK, I've marked this task as not done yet:
  [T][ ] read book
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] read book
____________________________________________________________
____________________________________________________________
Bye! Always soar towards your goals!
____________________________________________________________
```

### TC-004 — Malformed tasks do not enter the list

- Aim: Verify that invalid deadline and event inputs do not change task count, ordering, or existing task data.

#### Inputs

```text
todo anchor task
deadline missing date /by
deadline impossible date /by 2019-02-29
list
deadline submit report /by 2019-12-06
event missing end /from noon /to
list
event team sync /from noon /to 1pm
event /from 2pm /to 3pm
event backwards /from 2026-09-20 17:00 /to 2026-09-20 16:00
list
bye
```

#### Expected output

```text
____________________________________________________________
 ____                    
/ ___|  ___   __ _ _ __  
\___ \ / _ \ / _` | '__| 
 ___) | (_) | (_| | |    
|____/ \___/ \__,_|_|    
Hey there! I'm Soar, your upbeat little sidekick!
What exciting thing can I help you tackle today?
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] anchor task
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
The deadline's '/by' date is empty. Add a date or time so it can fly on schedule!
____________________________________________________________
____________________________________________________________
I couldn't understand the deadline '2019-02-29'. Use yyyy-MM-dd, d/M/yyyy, d/M/yyyy HHmm, yyyy-MM-dd HH:mm, d MMM yyyy h:mm a, or an ISO date-time!
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] anchor task
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [D][ ] submit report (by: Dec 06 2019)
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
The event's flight times are incomplete. Fill in both '/from' and '/to' values!
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] anchor task
2.[D][ ] submit report (by: Dec 06 2019)
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [E][ ] team sync (from: noon to: 1pm)
Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
The event description is empty. Give it a few words, and it will be ready to soar!
____________________________________________________________
____________________________________________________________
The event ends before it starts. Make its '/to' date or time the same as or after '/from'.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] anchor task
2.[D][ ] submit report (by: Dec 06 2019)
3.[E][ ] team sync (from: noon to: 1pm)
____________________________________________________________
____________________________________________________________
Bye! Always soar towards your goals!
____________________________________________________________
```

### TC-005 — Invalid task-number formats preserve state

- Aim: Verify that missing, negative, decimal, and oversized task numbers do not change a task before or after valid mark and unmark commands.

#### Inputs

```text
todo keep state
mark
unmark
list
mark -1
list
mark 1.5
list
mark 1
unmark 999999999999999999999
list
bye
```

#### Expected output

```text
____________________________________________________________
 ____                    
/ ___|  ___   __ _ _ __  
\___ \ / _ \ / _` | '__| 
 ___) | (_) | (_| | |    
|____/ \___/ \__,_|_|    
Hey there! I'm Soar, your upbeat little sidekick!
What exciting thing can I help you tackle today?
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] keep state
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Add a task number after 'mark' so I know which task has completed its flight!
____________________________________________________________
____________________________________________________________
Add a task number after 'unmark' so I know which task should return to the flight path!
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] keep state
____________________________________________________________
____________________________________________________________
Task -1 is outside your list. Choose a number from 1 to 1 and we'll stay on course!
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] keep state
____________________________________________________________
____________________________________________________________
'1.5' is not a whole task number. Choose a number from your list to keep flying high!
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] keep state
____________________________________________________________
____________________________________________________________
Nice! I've marked this task as done:
  [T][X] keep state
____________________________________________________________
____________________________________________________________
'999999999999999999999' is not a whole task number. Choose a number from your list to keep flying high!
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][X] keep state
____________________________________________________________
____________________________________________________________
Bye! Always soar towards your goals!
____________________________________________________________
```

### TC-006 — Command lookalikes preserve list state

- Aim: Verify that command lookalikes and arguments after exact commands are rejected without changing list state.

#### Inputs

```text
todo real task
todoist fake task
list extra
list
mark 1
mark1
list
unmark1
list
unmark 1
list
bye now
bye
```

#### Expected output

```text
____________________________________________________________
 ____                    
/ ___|  ___   __ _ _ __  
\___ \ / _ \ / _` | '__| 
 ___) | (_) | (_| | |    
|____/ \___/ \__,_|_|    
Hey there! I'm Soar, your upbeat little sidekick!
What exciting thing can I help you tackle today?
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] real task
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
That command is on an unfamiliar flight path. Try list, find, date, todo, deadline, event, mark, unmark, delete, edit, or bye to keep flying high!
____________________________________________________________
____________________________________________________________
That command is on an unfamiliar flight path. Try list, find, date, todo, deadline, event, mark, unmark, delete, edit, or bye to keep flying high!
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] real task
____________________________________________________________
____________________________________________________________
Nice! I've marked this task as done:
  [T][X] real task
____________________________________________________________
____________________________________________________________
That command is on an unfamiliar flight path. Try list, find, date, todo, deadline, event, mark, unmark, delete, edit, or bye to keep flying high!
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][X] real task
____________________________________________________________
____________________________________________________________
That command is on an unfamiliar flight path. Try list, find, date, todo, deadline, event, mark, unmark, delete, edit, or bye to keep flying high!
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][X] real task
____________________________________________________________
____________________________________________________________
OK, I've marked this task as not done yet:
  [T][ ] real task
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] real task
____________________________________________________________
____________________________________________________________
That command is on an unfamiliar flight path. Try list, find, date, todo, deadline, event, mark, unmark, delete, edit, or bye to keep flying high!
____________________________________________________________
____________________________________________________________
Bye! Always soar towards your goals!
____________________________________________________________
```


### TC-007 — Task list grows beyond the former array limit

- Aim: Verify that task tracking can store more than 100 tasks by adding a 101st task successfully.

#### Inputs

```text
todo task 1
todo task 2
todo task 3
todo task 4
todo task 5
todo task 6
todo task 7
todo task 8
todo task 9
todo task 10
todo task 11
todo task 12
todo task 13
todo task 14
todo task 15
todo task 16
todo task 17
todo task 18
todo task 19
todo task 20
todo task 21
todo task 22
todo task 23
todo task 24
todo task 25
todo task 26
todo task 27
todo task 28
todo task 29
todo task 30
todo task 31
todo task 32
todo task 33
todo task 34
todo task 35
todo task 36
todo task 37
todo task 38
todo task 39
todo task 40
todo task 41
todo task 42
todo task 43
todo task 44
todo task 45
todo task 46
todo task 47
todo task 48
todo task 49
todo task 50
todo task 51
todo task 52
todo task 53
todo task 54
todo task 55
todo task 56
todo task 57
todo task 58
todo task 59
todo task 60
todo task 61
todo task 62
todo task 63
todo task 64
todo task 65
todo task 66
todo task 67
todo task 68
todo task 69
todo task 70
todo task 71
todo task 72
todo task 73
todo task 74
todo task 75
todo task 76
todo task 77
todo task 78
todo task 79
todo task 80
todo task 81
todo task 82
todo task 83
todo task 84
todo task 85
todo task 86
todo task 87
todo task 88
todo task 89
todo task 90
todo task 91
todo task 92
todo task 93
todo task 94
todo task 95
todo task 96
todo task 97
todo task 98
todo task 99
todo task 100
todo task 101
bye
```

#### Expected output

```text
____________________________________________________________
 ____                    
/ ___|  ___   __ _ _ __  
\___ \ / _ \ / _` | '__| 
 ___) | (_) | (_| | |    
|____/ \___/ \__,_|_|    
Hey there! I'm Soar, your upbeat little sidekick!
What exciting thing can I help you tackle today?
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] task 1
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] task 2
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] task 3
Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] task 4
Now you have 4 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] task 5
Now you have 5 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] task 6
Now you have 6 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] task 7
Now you have 7 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] task 8
Now you have 8 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] task 9
Now you have 9 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] task 10
Now you have 10 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] task 11
Now you have 11 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] task 12
Now you have 12 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] task 13
Now you have 13 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] task 14
Now you have 14 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] task 15
Now you have 15 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] task 16
Now you have 16 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] task 17
Now you have 17 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] task 18
Now you have 18 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] task 19
Now you have 19 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] task 20
Now you have 20 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] task 21
Now you have 21 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] task 22
Now you have 22 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] task 23
Now you have 23 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] task 24
Now you have 24 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] task 25
Now you have 25 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] task 26
Now you have 26 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] task 27
Now you have 27 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] task 28
Now you have 28 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] task 29
Now you have 29 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] task 30
Now you have 30 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] task 31
Now you have 31 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] task 32
Now you have 32 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] task 33
Now you have 33 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] task 34
Now you have 34 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] task 35
Now you have 35 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] task 36
Now you have 36 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] task 37
Now you have 37 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] task 38
Now you have 38 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] task 39
Now you have 39 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] task 40
Now you have 40 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] task 41
Now you have 41 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] task 42
Now you have 42 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] task 43
Now you have 43 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] task 44
Now you have 44 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] task 45
Now you have 45 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] task 46
Now you have 46 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] task 47
Now you have 47 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] task 48
Now you have 48 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] task 49
Now you have 49 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] task 50
Now you have 50 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] task 51
Now you have 51 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] task 52
Now you have 52 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] task 53
Now you have 53 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] task 54
Now you have 54 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] task 55
Now you have 55 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] task 56
Now you have 56 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] task 57
Now you have 57 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] task 58
Now you have 58 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] task 59
Now you have 59 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] task 60
Now you have 60 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] task 61
Now you have 61 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] task 62
Now you have 62 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] task 63
Now you have 63 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] task 64
Now you have 64 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] task 65
Now you have 65 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] task 66
Now you have 66 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] task 67
Now you have 67 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] task 68
Now you have 68 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] task 69
Now you have 69 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] task 70
Now you have 70 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] task 71
Now you have 71 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] task 72
Now you have 72 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] task 73
Now you have 73 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] task 74
Now you have 74 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] task 75
Now you have 75 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] task 76
Now you have 76 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] task 77
Now you have 77 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] task 78
Now you have 78 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] task 79
Now you have 79 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] task 80
Now you have 80 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] task 81
Now you have 81 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] task 82
Now you have 82 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] task 83
Now you have 83 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] task 84
Now you have 84 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] task 85
Now you have 85 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] task 86
Now you have 86 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] task 87
Now you have 87 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] task 88
Now you have 88 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] task 89
Now you have 89 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] task 90
Now you have 90 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] task 91
Now you have 91 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] task 92
Now you have 92 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] task 93
Now you have 93 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] task 94
Now you have 94 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] task 95
Now you have 95 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] task 96
Now you have 96 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] task 97
Now you have 97 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] task 98
Now you have 98 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] task 99
Now you have 99 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] task 100
Now you have 100 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] task 101
Now you have 101 tasks in the list.
____________________________________________________________
____________________________________________________________
Bye! Always soar towards your goals!
____________________________________________________________
```

### TC-008 — Delete a task and renumber the list

- Aim: Verify that deleting a task reports the removed task, decreases the count, and closes the numbering gap.

#### Inputs

```text
todo read book
deadline return book /by 2019-06-06
event project meeting /from Aug 6th 2pm /to 4pm
todo join sports club
todo borrow book
mark 1
mark 2
mark 4
list
delete 3
list
bye
```

#### Expected output

```text
____________________________________________________________
 ____                    
/ ___|  ___   __ _ _ __  
\___ \ / _ \ / _` | '__| 
 ___) | (_) | (_| | |    
|____/ \___/ \__,_|_|    
Hey there! I'm Soar, your upbeat little sidekick!
What exciting thing can I help you tackle today?
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] read book
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [D][ ] return book (by: Jun 06 2019)
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [E][ ] project meeting (from: Aug 6th 2pm to: 4pm)
Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] join sports club
Now you have 4 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] borrow book
Now you have 5 tasks in the list.
____________________________________________________________
____________________________________________________________
Nice! I've marked this task as done:
  [T][X] read book
____________________________________________________________
____________________________________________________________
Nice! I've marked this task as done:
  [D][X] return book (by: Jun 06 2019)
____________________________________________________________
____________________________________________________________
Nice! I've marked this task as done:
  [T][X] join sports club
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][X] read book
2.[D][X] return book (by: Jun 06 2019)
3.[E][ ] project meeting (from: Aug 6th 2pm to: 4pm)
4.[T][X] join sports club
5.[T][ ] borrow book
____________________________________________________________
____________________________________________________________
Noted. I've removed this task:
  [E][ ] project meeting (from: Aug 6th 2pm to: 4pm)
Now you have 4 tasks in the list.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][X] read book
2.[D][X] return book (by: Jun 06 2019)
3.[T][X] join sports club
4.[T][ ] borrow book
____________________________________________________________
____________________________________________________________
Bye! Always soar towards your goals!
____________________________________________________________
```

### TC-009 — Invalid delete commands preserve the list

- Aim: Verify that missing, nonnumeric, empty-list, and out-of-range delete targets are rejected without changing task data.

#### Inputs

```text
delete
delete two
delete 1
todo anchor task
delete 0
delete 2
list
bye
```

#### Expected output

```text
____________________________________________________________
 ____                    
/ ___|  ___   __ _ _ __  
\___ \ / _ \ / _` | '__| 
 ___) | (_) | (_| | |    
|____/ \___/ \__,_|_|    
Hey there! I'm Soar, your upbeat little sidekick!
What exciting thing can I help you tackle today?
____________________________________________________________
____________________________________________________________
Add a task number after 'delete' so I know which task should be shot down!
____________________________________________________________
____________________________________________________________
'two' is not a whole task number. Choose a number from your list to keep flying high!
____________________________________________________________
____________________________________________________________
Your task list is an open sky right now. Add a task before using 'delete'!
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] anchor task
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Task 0 is outside your list. Choose a number from 1 to 1 and we'll stay on course!
____________________________________________________________
____________________________________________________________
Task 2 is outside your list. Choose a number from 1 to 1 and we'll stay on course!
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] anchor task
____________________________________________________________
____________________________________________________________
Bye! Always soar towards your goals!
____________________________________________________________
```

### TC-010 — Persist after every task-list change

- Aim: Exercise add, mark, unmark, and delete changes whose final state must be saved to the configured data file.

#### Inputs

```text
todo read book
deadline return book /by 2019-06-06
event project meeting /from Aug 6th 2pm /to 4pm
mark 1
unmark 1
delete 2
list
bye
```

#### Expected output

```text
____________________________________________________________
 ____                    
/ ___|  ___   __ _ _ __  
\___ \ / _ \ / _` | '__| 
 ___) | (_) | (_| | |    
|____/ \___/ \__,_|_|    
Hey there! I'm Soar, your upbeat little sidekick!
What exciting thing can I help you tackle today?
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] read book
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [D][ ] return book (by: Jun 06 2019)
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [E][ ] project meeting (from: Aug 6th 2pm to: 4pm)
Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
Nice! I've marked this task as done:
  [T][X] read book
____________________________________________________________
____________________________________________________________
OK, I've marked this task as not done yet:
  [T][ ] read book
____________________________________________________________
____________________________________________________________
Noted. I've removed this task:
  [D][ ] return book (by: Jun 06 2019)
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] read book
2.[E][ ] project meeting (from: Aug 6th 2pm to: 4pm)
____________________________________________________________
____________________________________________________________
Bye! Always soar towards your goals!
____________________________________________________________
```

### TC-011 — Additional deadline date-time formats

- Aim: Verify that day-first dates, ISO-style date-times, textual dates, and ISO date-times are parsed and displayed consistently.

#### Inputs

```text
deadline day-first date /by 15/10/2019
deadline spaced ISO time /by 2019-10-15 18:00
deadline textual time /by 15 Oct 2019 6:00 PM
deadline ISO date-time /by 2019-10-15T18:00
list
bye
```

#### Expected output

```text
____________________________________________________________
 ____                    
/ ___|  ___   __ _ _ __  
\___ \ / _ \ / _` | '__| 
 ___) | (_) | (_| | |    
|____/ \___/ \__,_|_|    
Hey there! I'm Soar, your upbeat little sidekick!
What exciting thing can I help you tackle today?
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [D][ ] day-first date (by: Oct 15 2019)
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [D][ ] spaced ISO time (by: Oct 15 2019, 6:00PM)
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [D][ ] textual time (by: Oct 15 2019, 6:00PM)
Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [D][ ] ISO date-time (by: Oct 15 2019, 6:00PM)
Now you have 4 tasks in the list.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[D][ ] day-first date (by: Oct 15 2019)
2.[D][ ] spaced ISO time (by: Oct 15 2019, 6:00PM)
3.[D][ ] textual time (by: Oct 15 2019, 6:00PM)
4.[D][ ] ISO date-time (by: Oct 15 2019, 6:00PM)
____________________________________________________________
____________________________________________________________
Bye! Always soar towards your goals!
____________________________________________________________
```

### TC-012 — Query deadlines and events by date

- Aim: Verify that the date command accepts supported formats, keeps original task numbers, includes dated event ranges, and explains empty or invalid queries.

#### Inputs

```text
todo background task
deadline submit report /by 2019-10-15
event workshop /from 2019-10-15 14:00 /to 2019-10-15 16:00
event conference /from 2019-10-14T09:00 /to 2019-10-16T17:00
event undated lunch /from noon /to 1pm
date 15 Oct 2019 6:00 PM
date 17/10/2019
date
date 2019-02-29
bye
```

#### Expected output

```text
____________________________________________________________
 ____                    
/ ___|  ___   __ _ _ __  
\___ \ / _ \ / _` | '__| 
 ___) | (_) | (_| | |    
|____/ \___/ \__,_|_|    
Hey there! I'm Soar, your upbeat little sidekick!
What exciting thing can I help you tackle today?
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] background task
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [D][ ] submit report (by: Oct 15 2019)
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [E][ ] workshop (from: 2019-10-15 14:00 to: 2019-10-15 16:00)
Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [E][ ] conference (from: 2019-10-14T09:00 to: 2019-10-16T17:00)
Now you have 4 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [E][ ] undated lunch (from: noon to: 1pm)
Now you have 5 tasks in the list.
____________________________________________________________
____________________________________________________________
Here are the deadlines and events on Oct 15 2019:
2.[D][ ] submit report (by: Oct 15 2019)
3.[E][ ] workshop (from: 2019-10-15 14:00 to: 2019-10-15 16:00)
4.[E][ ] conference (from: 2019-10-14T09:00 to: 2019-10-16T17:00)
____________________________________________________________
____________________________________________________________
There are no deadlines or events on Oct 17 2019.
____________________________________________________________
____________________________________________________________
Add a date after 'date' so I know which day's flight plan to show!
____________________________________________________________
____________________________________________________________
I couldn't understand the date '2019-02-29'. Use one of the supported deadline date or date-time formats!
____________________________________________________________
____________________________________________________________
Bye! Always soar towards your goals!
____________________________________________________________
```

### TC-013 — Find tasks by description keyword

- Aim: Verify that find returns matching task descriptions in list order and handles no matches and a missing keyword.

#### Inputs

```text
todo read book
deadline return book /by 2019-06-06
todo buy groceries
mark 1
mark 2
find book
find pencil
find
bye
```

#### Expected output

```text
____________________________________________________________
 ____                    
/ ___|  ___   __ _ _ __  
\___ \ / _ \ / _` | '__| 
 ___) | (_) | (_| | |    
|____/ \___/ \__,_|_|    
Hey there! I'm Soar, your upbeat little sidekick!
What exciting thing can I help you tackle today?
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] read book
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [D][ ] return book (by: Jun 06 2019)
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] buy groceries
Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
Nice! I've marked this task as done:
  [T][X] read book
____________________________________________________________
____________________________________________________________
Nice! I've marked this task as done:
  [D][X] return book (by: Jun 06 2019)
____________________________________________________________
____________________________________________________________
Here are the matching tasks in your list:
1.[T][X] read book
2.[D][X] return book (by: Jun 06 2019)
____________________________________________________________
____________________________________________________________
Here are the matching tasks in your list:
____________________________________________________________
____________________________________________________________
Add a keyword after 'find' so I know which tasks to look for!
____________________________________________________________
____________________________________________________________
Bye! Always soar towards your goals!
____________________________________________________________
```

### TC-014 — Edit multiple task details atomically

- Aim: Verify supported edits preserve type, completion, numbering, unspecified fields, persistence,
  and dated-event matching while showing exact before-and-after output.

#### Inputs

```text
todo read book
deadline submit draft /by 2026-09-18
event meeting /from 2pm /to 4pm
mark 2
mark 3
edit 1 /description read two chapters
edit 2 /description submit final report /by 20 Sep 2026 6:00 PM
edit 3 /to 2026-09-20 17:00 /description workshop
date 2026-09-20
edit 3 /description workshop
list
bye
```

#### Expected output

```text
____________________________________________________________
 ____                    
/ ___|  ___   __ _ _ __  
\___ \ / _ \ / _` | '__| 
 ___) | (_) | (_| | |    
|____/ \___/ \__,_|_|    
Hey there! I'm Soar, your upbeat little sidekick!
What exciting thing can I help you tackle today?
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] read book
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [D][ ] submit draft (by: Sep 18 2026)
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [E][ ] meeting (from: 2pm to: 4pm)
Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
Nice! I've marked this task as done:
  [D][X] submit draft (by: Sep 18 2026)
____________________________________________________________
____________________________________________________________
Nice! I've marked this task as done:
  [E][X] meeting (from: 2pm to: 4pm)
____________________________________________________________
____________________________________________________________
I've updated this task:
  Before: [T][ ] read book
  After:  [T][ ] read two chapters
____________________________________________________________
____________________________________________________________
I've updated this task:
  Before: [D][X] submit draft (by: Sep 18 2026)
  After:  [D][X] submit final report (by: Sep 20 2026, 6:00PM)
____________________________________________________________
____________________________________________________________
I've updated this task:
  Before: [E][X] meeting (from: 2pm to: 4pm)
  After:  [E][X] workshop (from: 2pm to: 2026-09-20 17:00)
____________________________________________________________
____________________________________________________________
Here are the deadlines and events on Sep 20 2026:
2.[D][X] submit final report (by: Sep 20 2026, 6:00PM)
3.[E][X] workshop (from: 2pm to: 2026-09-20 17:00)
____________________________________________________________
____________________________________________________________
That task already has those details:
  [E][X] workshop (from: 2pm to: 2026-09-20 17:00)
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] read two chapters
2.[D][X] submit final report (by: Sep 20 2026, 6:00PM)
3.[E][X] workshop (from: 2pm to: 2026-09-20 17:00)
____________________________________________________________
____________________________________________________________
Bye! Always soar towards your goals!
____________________________________________________________
```

### TC-015 — Invalid edits preserve every task

- Aim: Verify edit syntax, task numbers, field applicability, duplicate markers, empty values, and
  deadline validation fail without applying any partial change.

#### Inputs

```text
todo anchor
deadline report /by 2026-09-20
event meeting /from 2pm /to 4pm
edit
edit two /description revised
edit 4 /description revised
edit 1
edit 1 /when tomorrow
edit 1 /description revised /description again
edit 1 /to 5pm
edit 2 /description changed /by 2026-02-29
edit 3 /from /to 5pm
list
bye
```

#### Expected output

```text
____________________________________________________________
 ____                    
/ ___|  ___   __ _ _ __  
\___ \ / _ \ / _` | '__| 
 ___) | (_) | (_| | |    
|____/ \___/ \__,_|_|    
Hey there! I'm Soar, your upbeat little sidekick!
What exciting thing can I help you tackle today?
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] anchor
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [D][ ] report (by: Sep 20 2026)
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [E][ ] meeting (from: 2pm to: 4pm)
Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
Add a task number after 'edit' so I know which task should be updated!
____________________________________________________________
____________________________________________________________
'two' is not a whole task number. Choose a number from your list to keep flying high!
____________________________________________________________
____________________________________________________________
Task 4 is outside your list. Choose a number from 1 to 3 and we'll stay on course!
____________________________________________________________
____________________________________________________________
Add at least one editable field after the task number.
____________________________________________________________
____________________________________________________________
I don't recognize the edit field '/when'. Use /description, /by, /from, or /to.
____________________________________________________________
____________________________________________________________
The edit field '/description' appears more than once. Provide each field only once.
____________________________________________________________
____________________________________________________________
A todo can only edit /description.
____________________________________________________________
____________________________________________________________
I couldn't understand the deadline '2026-02-29'. Use yyyy-MM-dd, d/M/yyyy, d/M/yyyy HHmm, yyyy-MM-dd HH:mm, d MMM yyyy h:mm a, or an ISO date-time!
____________________________________________________________
____________________________________________________________
The '/from' value is empty. Add a value after it.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] anchor
2.[D][ ] report (by: Sep 20 2026)
3.[E][ ] meeting (from: 2pm to: 4pm)
____________________________________________________________
____________________________________________________________
Bye! Always soar towards your goals!
____________________________________________________________
```
