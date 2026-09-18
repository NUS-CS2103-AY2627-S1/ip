# UI Test Plan

## Test Case: task types and status changes

### Aim

Verify that the chatbot can add todos, deadlines, and events; list them with type icons; mark a task as done; and unmark it again.

### Inputs

```text
todo read book
deadline return book /by 25/8/2026 1800
event project meeting /from 25/8/2026 1400 /to 25/8/2026 1600
mark 1
list
unmark 1
list
bye
```

### Expected output

```text
____________________________________________________________
 _   _       _                 ____             _    
| | | | __ _| |__  _ __  _   _|  _ \ _   _  ___| | __
| |_| |/ _` | '_ \| '_ \| | | | | | | | | |/ __| |/ /
|  _  | (_| | |_) | |_) | |_| | |_| | |_| | (__|   < 
|_| |_|\__,_|_.__/| .__/ \__, |____/ \__,_|\___|_|\_\
                  |_|    |___/                       
Hi friend! I'm HabpyDuck.
What can I do for you today?
____________________________________________________________
____________________________________________________________
Got it, friend. I've added this task:
  [T][ ] read book
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it, friend. I've added this task:
  [D][ ] return book (by: 25/8/2026 1800)
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it, friend. I've added this task:
  [E][ ] project meeting (from: 25/8/2026 1400 to: 25/8/2026 1600)
Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
YAY GOOD JOB!!! I've marked this task as done, friend:
  [T][X] read book
____________________________________________________________
____________________________________________________________
Here are the tasks in your list, friend:
1.[T][X] read book
2.[D][ ] return book (by: 25/8/2026 1800)
3.[E][ ] project meeting (from: 25/8/2026 1400 to: 25/8/2026 1600)
____________________________________________________________
____________________________________________________________
OK, friend. I've marked this task as not done yet:
  [T][ ] read book
____________________________________________________________
____________________________________________________________
Here are the tasks in your list, friend:
1.[T][ ] read book
2.[D][ ] return book (by: 25/8/2026 1800)
3.[E][ ] project meeting (from: 25/8/2026 1400 to: 25/8/2026 1600)
____________________________________________________________
____________________________________________________________
Bye friend. Hope to see you again soon!
____________________________________________________________
```

## Test Case: saved tasks load on startup

### Aim

Verify that the chatbot loads todo, deadline, and event tasks from the saved file when it starts, including their done statuses.

### Initial saved file content

```text
T | 1 | read book
D | 0 | return book | 2019-06-06T09:00
E | 1 | project meeting | 2019-08-06T14:00 | 2019-08-06T16:00
```

### Inputs

```text
list
todo join sports club
list
bye
```

### Expected output

```text
____________________________________________________________
 _   _       _                 ____             _    
| | | | __ _| |__  _ __  _   _|  _ \ _   _  ___| | __
| |_| |/ _` | '_ \| '_ \| | | | | | | | | |/ __| |/ /
|  _  | (_| | |_) | |_) | |_| | |_| | |_| | (__|   < 
|_| |_|\__,_|_.__/| .__/ \__, |____/ \__,_|\___|_|\_\
                  |_|    |___/                       
Hi friend! I'm HabpyDuck.
What can I do for you today?
____________________________________________________________
____________________________________________________________
Here are the tasks in your list, friend:
1.[T][X] read book
2.[D][ ] return book (by: 6/6/2019 0900)
3.[E][X] project meeting (from: 6/8/2019 1400 to: 6/8/2019 1600)
____________________________________________________________
____________________________________________________________
Got it, friend. I've added this task:
  [T][ ] join sports club
Now you have 4 tasks in the list.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list, friend:
1.[T][X] read book
2.[D][ ] return book (by: 6/6/2019 0900)
3.[E][X] project meeting (from: 6/8/2019 1400 to: 6/8/2019 1600)
4.[T][ ] join sports club
____________________________________________________________
____________________________________________________________
Bye friend. Hope to see you again soon!
____________________________________________________________
```

### Expected saved file content

```text
T | 1 | read book
D | 0 | return book | 2019-06-06T09:00
E | 1 | project meeting | 2019-08-06T14:00 | 2019-08-06T16:00
T | 0 | join sports club
```

## Test Case: malformed saved tasks are skipped

### Aim

Verify that malformed saved task lines are reported and skipped, while valid saved tasks still load.

### Initial saved file content

```text
T | 1 | read book
D | 2 | bad status | tomorrow
X | 0 | bad type
D | 0 | missing date
D | 0 | invalid date | tomorrow
E | 1 | project meeting | 2026-08-25T14:00 | 2026-08-25T16:00
T | 0 |    
```

### Inputs

```text
list
bye
```

### Expected output

```text
____________________________________________________________
 _   _       _                 ____             _    
| | | | __ _| |__  _ __  _   _|  _ \ _   _  ___| | __
| |_| |/ _` | '_ \| '_ \| | | | | | | | | |/ __| |/ /
|  _  | (_| | |_) | |_) | |_| | |_| | |_| | (__|   < 
|_| |_|\__,_|_.__/| .__/ \__, |____/ \__,_|\___|_|\_\
                  |_|    |___/                       
Hi friend! I'm HabpyDuck.
What can I do for you today?
____________________________________________________________
____________________________________________________________
OH NO!!! I had trouble loading saved task on line 2: status must be 0 or 1
OH NO!!! I had trouble loading saved task on line 3: unknown task type 'X'
OH NO!!! I had trouble loading saved task on line 4: expected 4 or 5 fields but found 3
OH NO!!! I had trouble loading saved task on line 5: saved deadline date and time must use yyyy-MM-ddTHH:mm format
OH NO!!! I had trouble loading saved task on line 7: task details cannot be empty
Here are the tasks in your list, friend:
1.[T][X] read book
2.[E][X] project meeting (from: 25/8/2026 1400 to: 25/8/2026 1600)
____________________________________________________________
____________________________________________________________
Bye friend. Hope to see you again soon!
____________________________________________________________
```

## Test Case: special file characters are escaped when saved

### Aim

Verify that task text containing pipes and backslashes can be saved without breaking the file format.

### Inputs

```text
todo read | book
deadline path \ home /by 8/8/2026 0830
event sync | call /from 8/8/2026 1400 /to 8/8/2026 1700
list
bye
```

### Expected output

```text
____________________________________________________________
 _   _       _                 ____             _    
| | | | __ _| |__  _ __  _   _|  _ \ _   _  ___| | __
| |_| |/ _` | '_ \| '_ \| | | | | | | | | |/ __| |/ /
|  _  | (_| | |_) | |_) | |_| | |_| | |_| | (__|   < 
|_| |_|\__,_|_.__/| .__/ \__, |____/ \__,_|\___|_|\_\
                  |_|    |___/                       
Hi friend! I'm HabpyDuck.
What can I do for you today?
____________________________________________________________
____________________________________________________________
Got it, friend. I've added this task:
  [T][ ] read | book
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it, friend. I've added this task:
  [D][ ] path \ home (by: 8/8/2026 0830)
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it, friend. I've added this task:
  [E][ ] sync | call (from: 8/8/2026 1400 to: 8/8/2026 1700)
Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list, friend:
1.[T][ ] read | book
2.[D][ ] path \ home (by: 8/8/2026 0830)
3.[E][ ] sync | call (from: 8/8/2026 1400 to: 8/8/2026 1700)
____________________________________________________________
____________________________________________________________
Bye friend. Hope to see you again soon!
____________________________________________________________
```

### Expected saved file content

```text
T | 0 | read \| book
D | 0 | path \\ home | 2026-08-08T08:30
E | 0 | sync \| call | 2026-08-08T14:00 | 2026-08-08T17:00
```

## Test Case: empty input exits cleanly

### Aim

Verify that the chatbot does not crash if standard input ends before the user enters bye.

### Inputs

```text

```

### Expected output

```text
____________________________________________________________
 _   _       _                 ____             _    
| | | | __ _| |__  _ __  _   _|  _ \ _   _  ___| | __
| |_| |/ _` | '_ \| '_ \| | | | | | | | | |/ __| |/ /
|  _  | (_| | |_) | |_) | |_| | |_| | |_| | (__|   < 
|_| |_|\__,_|_.__/| .__/ \__, |____/ \__,_|\___|_|\_\
                  |_|    |___/                       
Hi friend! I'm HabpyDuck.
What can I do for you today?
____________________________________________________________
____________________________________________________________
Bye friend. Hope to see you again soon!
____________________________________________________________
```

## Test Case: delete removes a task and preserves remaining state

### Aim

Verify that deleting a middle task removes only that task, renumbers the remaining tasks, keeps their done statuses, and handles invalid delete commands without changing the list.

### Inputs

```text
todo read book
deadline return book /by 6/6/2019 0900
event project meeting /from 6/8/2019 1400 /to 6/8/2019 1600
todo join sports club
mark 1
mark 2
mark 4
list
delete 3
list
delete
delete abc
delete 9
list
bye
```

### Expected output

```text
____________________________________________________________
 _   _       _                 ____             _    
| | | | __ _| |__  _ __  _   _|  _ \ _   _  ___| | __
| |_| |/ _` | '_ \| '_ \| | | | | | | | | |/ __| |/ /
|  _  | (_| | |_) | |_) | |_| | |_| | |_| | (__|   < 
|_| |_|\__,_|_.__/| .__/ \__, |____/ \__,_|\___|_|\_\
                  |_|    |___/                       
Hi friend! I'm HabpyDuck.
What can I do for you today?
____________________________________________________________
____________________________________________________________
Got it, friend. I've added this task:
  [T][ ] read book
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it, friend. I've added this task:
  [D][ ] return book (by: 6/6/2019 0900)
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it, friend. I've added this task:
  [E][ ] project meeting (from: 6/8/2019 1400 to: 6/8/2019 1600)
Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it, friend. I've added this task:
  [T][ ] join sports club
Now you have 4 tasks in the list.
____________________________________________________________
____________________________________________________________
YAY GOOD JOB!!! I've marked this task as done, friend:
  [T][X] read book
____________________________________________________________
____________________________________________________________
YAY GOOD JOB!!! I've marked this task as done, friend:
  [D][X] return book (by: 6/6/2019 0900)
____________________________________________________________
____________________________________________________________
YAY GOOD JOB!!! I've marked this task as done, friend:
  [T][X] join sports club
____________________________________________________________
____________________________________________________________
Here are the tasks in your list, friend:
1.[T][X] read book
2.[D][X] return book (by: 6/6/2019 0900)
3.[E][ ] project meeting (from: 6/8/2019 1400 to: 6/8/2019 1600)
4.[T][X] join sports club
____________________________________________________________
____________________________________________________________
Got it, friend. I've removed this task:
  [E][ ] project meeting (from: 6/8/2019 1400 to: 6/8/2019 1600)
Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list, friend:
1.[T][X] read book
2.[D][X] return book (by: 6/6/2019 0900)
3.[T][X] join sports club
____________________________________________________________
____________________________________________________________
OH NO!!! Please tell me which task to delete, like: delete 2
____________________________________________________________
____________________________________________________________
OH NO!!! Please use a number after delete, like: delete 2
____________________________________________________________
____________________________________________________________
OH NO!!! Task 9 does not exist in your list.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list, friend:
1.[T][X] read book
2.[D][X] return book (by: 6/6/2019 0900)
3.[T][X] join sports club
____________________________________________________________
____________________________________________________________
Bye friend. Hope to see you again soon!
____________________________________________________________
```

### Expected saved file content

```text
T | 1 | read book
D | 1 | return book | 2019-06-06T09:00
T | 1 | join sports club
```

## Test Case: invalid commands do not change task state

### Aim

Verify that invalid commands interleaved with valid commands do not add extra tasks or change the done status of existing tasks.

### Inputs

```text
list
todo alpha
todo
blah
list
deadline beta /by 13/12/2019 2359
event gamma /from 13/12/2019 1300 /to 13/12/2019 1400
event delta /from 3pm
mark 2
mark 99
unmark abc
unmark 2
list
bye
```

### Expected output

```text
____________________________________________________________
 _   _       _                 ____             _    
| | | | __ _| |__  _ __  _   _|  _ \ _   _  ___| | __
| |_| |/ _` | '_ \| '_ \| | | | | | | | | |/ __| |/ /
|  _  | (_| | |_) | |_) | |_| | |_| | |_| | (__|   < 
|_| |_|\__,_|_.__/| .__/ \__, |____/ \__,_|\___|_|\_\
                  |_|    |___/                       
Hi friend! I'm HabpyDuck.
What can I do for you today?
____________________________________________________________
____________________________________________________________
Here are the tasks in your list, friend:
____________________________________________________________
____________________________________________________________
Got it, friend. I've added this task:
  [T][ ] alpha
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
OH NO!!! A todo needs a description, friend. Try something like: todo read book
____________________________________________________________
____________________________________________________________
OH NO!!! I don't understand that command friend :(. Try todo, deadline, event, list, mark, unmark, delete, find, tag, untag, or findtag!
____________________________________________________________
____________________________________________________________
Here are the tasks in your list, friend:
1.[T][ ] alpha
____________________________________________________________
____________________________________________________________
Got it, friend. I've added this task:
  [D][ ] beta (by: 13/12/2019 2359)
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it, friend. I've added this task:
  [E][ ] gamma (from: 13/12/2019 1300 to: 13/12/2019 1400)
Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
OH NO!!! Please use this format for events: event DESCRIPTION /from START /to END :)
____________________________________________________________
____________________________________________________________
YAY GOOD JOB!!! I've marked this task as done, friend:
  [D][X] beta (by: 13/12/2019 2359)
____________________________________________________________
____________________________________________________________
OH NO!!! Task 99 does not exist in your list.
____________________________________________________________
____________________________________________________________
OH NO!!! Please use a number after unmark, like: unmark 2
____________________________________________________________
____________________________________________________________
OK, friend. I've marked this task as not done yet:
  [D][ ] beta (by: 13/12/2019 2359)
____________________________________________________________
____________________________________________________________
Here are the tasks in your list, friend:
1.[T][ ] alpha
2.[D][ ] beta (by: 13/12/2019 2359)
3.[E][ ] gamma (from: 13/12/2019 1300 to: 13/12/2019 1400)
____________________________________________________________
____________________________________________________________
Bye friend. Hope to see you again soon!
____________________________________________________________
```

## Test Case: command boundary cases with valid recovery

### Aim

Verify that blank commands, missing command details, and invalid task number boundaries are handled, and that later valid commands still update the list correctly.

### Inputs

```text

deadline
event
unmark
todo keep notes
unmark 1
mark 0
mark -1
mark 1
deadline do homework /by 4/10/2019 1200
event orientation week /from 4/10/2019 0000 /to 11/10/2019 0000
list
bye
```

### Expected output

```text
____________________________________________________________
 _   _       _                 ____             _    
| | | | __ _| |__  _ __  _   _|  _ \ _   _  ___| | __
| |_| |/ _` | '_ \| '_ \| | | | | | | | | |/ __| |/ /
|  _  | (_| | |_) | |_) | |_| | |_| | |_| | (__|   < 
|_| |_|\__,_|_.__/| .__/ \__, |____/ \__,_|\___|_|\_\
                  |_|    |___/                       
Hi friend! I'm HabpyDuck.
What can I do for you today?
____________________________________________________________
____________________________________________________________
OH NO!!! I didn't catch a command, friend. Please type something for me.
____________________________________________________________
____________________________________________________________
OH NO!!! Please use this format for deadlines: deadline DESCRIPTION /by DD/MM/YYYY or DD/MM/YYYY HHmm :)
____________________________________________________________
____________________________________________________________
OH NO!!! Please use this format for events: event DESCRIPTION /from START /to END :)
____________________________________________________________
____________________________________________________________
OH NO!!! Please tell me which task to unmark, like: unmark 2
____________________________________________________________
____________________________________________________________
Got it, friend. I've added this task:
  [T][ ] keep notes
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
OH NO!!! This task is already unmarked, friend.
____________________________________________________________
____________________________________________________________
OH NO!!! Task 0 does not exist in your list.
____________________________________________________________
____________________________________________________________
OH NO!!! Task -1 does not exist in your list.
____________________________________________________________
____________________________________________________________
YAY GOOD JOB!!! I've marked this task as done, friend:
  [T][X] keep notes
____________________________________________________________
____________________________________________________________
Got it, friend. I've added this task:
  [D][ ] do homework (by: 4/10/2019 1200)
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it, friend. I've added this task:
  [E][ ] orientation week (from: 4/10/2019 0000 to: 11/10/2019 0000)
Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list, friend:
1.[T][X] keep notes
2.[D][ ] do homework (by: 4/10/2019 1200)
3.[E][ ] orientation week (from: 4/10/2019 0000 to: 11/10/2019 0000)
____________________________________________________________
____________________________________________________________
Bye friend. Hope to see you again soon!
____________________________________________________________
```

## Test Case: invalid command handling

### Aim

Verify that the chatbot accepts date-only events and reports user-friendly errors for empty task descriptions, unknown
commands, invalid task numbers, and malformed deadline/event commands.

### Inputs

```text
todo
blah
mark
mark abc
mark 1
deadline return book
deadline /by Sunday
deadline return book /by 2019-10-15
deadline return book /by 25/8/2026 1800 /by 26/8/2026 1800
deadline return book /by 25/8/2026 1800
event meeting /from Mon 2pm
event meeting /to 25/8/2026 1600 /from 25/8/2026 1400
event meeting /from 25/8/2026 1400 /from 25/8/2026 1500 /to 25/8/2026 1600
event meeting /from 18/9/2026 1800 /to 18/9/2026 1600
event meeting /from 18/9/2026 1800 /to 18/9/2026 1800
event meeting /from 25/8/2026 /to 26/8/2026
list
bye
```

### Expected output

```text
____________________________________________________________
 _   _       _                 ____             _    
| | | | __ _| |__  _ __  _   _|  _ \ _   _  ___| | __
| |_| |/ _` | '_ \| '_ \| | | | | | | | | |/ __| |/ /
|  _  | (_| | |_) | |_) | |_| | |_| | |_| | (__|   < 
|_| |_|\__,_|_.__/| .__/ \__, |____/ \__,_|\___|_|\_\
                  |_|    |___/                       
Hi friend! I'm HabpyDuck.
What can I do for you today?
____________________________________________________________
____________________________________________________________
OH NO!!! A todo needs a description, friend. Try something like: todo read book
____________________________________________________________
____________________________________________________________
OH NO!!! I don't understand that command friend :(. Try todo, deadline, event, list, mark, unmark, delete, find, tag, untag, or findtag!
____________________________________________________________
____________________________________________________________
OH NO!!! Please tell me which task to mark, like: mark 2
____________________________________________________________
____________________________________________________________
OH NO!!! Please use a number after mark, like: mark 2
____________________________________________________________
____________________________________________________________
OH NO!!! Task 1 does not exist in your list.
____________________________________________________________
____________________________________________________________
OH NO!!! Please use this format for deadlines: deadline DESCRIPTION /by DD/MM/YYYY or DD/MM/YYYY HHmm :)
____________________________________________________________
____________________________________________________________
OH NO!!! A deadline needs a description, friend. Try again!
____________________________________________________________
____________________________________________________________
OH NO!!! Please enter the deadline date in DD/MM/YYYY format, or include time like: 25/8/2026 1800
____________________________________________________________
____________________________________________________________
OH NO!!! A deadline should have only one /by marker, friend.
____________________________________________________________
____________________________________________________________
Got it, friend. I've added this task:
  [D][ ] return book (by: 25/8/2026 1800)
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
OH NO!!! Please use this format for events: event DESCRIPTION /from START /to END :)
____________________________________________________________
____________________________________________________________
OH NO!!! Please put /from before /to for events, friend.
____________________________________________________________
____________________________________________________________
OH NO!!! An event should have one /from marker and one /to marker, friend.
____________________________________________________________
____________________________________________________________
OH NO!!! An event must end after it starts, friend.
____________________________________________________________
____________________________________________________________
OH NO!!! An event must end after it starts, friend.
____________________________________________________________
____________________________________________________________
Got it, friend. I've added this task:
  [E][ ] meeting (from: 25/8/2026 0000 to: 26/8/2026 0000)
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list, friend:
1.[D][ ] return book (by: 25/8/2026 1800)
2.[E][ ] meeting (from: 25/8/2026 0000 to: 26/8/2026 0000)
____________________________________________________________
____________________________________________________________
Bye friend. Hope to see you again soon!
____________________________________________________________
```

## Test Case: find tasks by keyword

### Aim

Verify that the chatbot can find tasks whose descriptions contain a keyword, including case-insensitive matches, no matches, and missing keyword errors.

### Inputs

```text
todo read book
deadline return book /by 6/6/2019 0900
todo buy milk
mark 1
mark 2
find book
find MILK
find pen
find
bye
```

### Expected output

```text
____________________________________________________________
 _   _       _                 ____             _    
| | | | __ _| |__  _ __  _   _|  _ \ _   _  ___| | __
| |_| |/ _` | '_ \| '_ \| | | | | | | | | |/ __| |/ /
|  _  | (_| | |_) | |_) | |_| | |_| | |_| | (__|   < 
|_| |_|\__,_|_.__/| .__/ \__, |____/ \__,_|\___|_|\_\
                  |_|    |___/                       
Hi friend! I'm HabpyDuck.
What can I do for you today?
____________________________________________________________
____________________________________________________________
Got it, friend. I've added this task:
  [T][ ] read book
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it, friend. I've added this task:
  [D][ ] return book (by: 6/6/2019 0900)
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it, friend. I've added this task:
  [T][ ] buy milk
Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
YAY GOOD JOB!!! I've marked this task as done, friend:
  [T][X] read book
____________________________________________________________
____________________________________________________________
YAY GOOD JOB!!! I've marked this task as done, friend:
  [D][X] return book (by: 6/6/2019 0900)
____________________________________________________________
____________________________________________________________
I found these matching tasks for you, friend:
1.[T][X] read book
2.[D][X] return book (by: 6/6/2019 0900)
____________________________________________________________
____________________________________________________________
I found these matching tasks for you, friend:
1.[T][ ] buy milk
____________________________________________________________
____________________________________________________________
I found these matching tasks for you, friend:
____________________________________________________________
____________________________________________________________
OH NO!!! Please tell me what keyword to find, like: find book
____________________________________________________________
____________________________________________________________
Bye friend. Hope to see you again soon!
____________________________________________________________
```

## Test Case: tag, untag, and find tagged tasks

### Aim

Verify that the chatbot can add multiple tags to a task, normalize tags to lowercase, avoid duplicate tags, remove tags, find tasks by tag, and reject invalid tag formats.

### Inputs

```text
todo read book
deadline return book /by 6/6/2019 0900
tag 1 #Fun #school_work
tag 1 #fun
tag 2 fun
tag abc #fun
find book
findtag #fun
untag 1 #fun
findtag #fun
findtag #school_work
untag 1 #missing
list
bye
```

### Expected output

```text
____________________________________________________________
 _   _       _                 ____             _    
| | | | __ _| |__  _ __  _   _|  _ \ _   _  ___| | __
| |_| |/ _` | '_ \| '_ \| | | | | | | | | |/ __| |/ /
|  _  | (_| | |_) | |_) | |_| | |_| | |_| | (__|   < 
|_| |_|\__,_|_.__/| .__/ \__, |____/ \__,_|\___|_|\_\
                  |_|    |___/                       
Hi friend! I'm HabpyDuck.
What can I do for you today?
____________________________________________________________
____________________________________________________________
Got it, friend. I've added this task:
  [T][ ] read book
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it, friend. I've added this task:
  [D][ ] return book (by: 6/6/2019 0900)
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it, friend. I've tagged this task:
  [T][ ] read book #fun #school_work
____________________________________________________________
____________________________________________________________
OH NO!!! This task already has those tag(s), friend.
____________________________________________________________
____________________________________________________________
OH NO!!! Tags must start with # and use only letters, numbers, hyphens, or underscores. Try something like: tag 2 #fun
____________________________________________________________
____________________________________________________________
OH NO!!! Please use a number after tag, like: tag 2
____________________________________________________________
____________________________________________________________
I found these matching tasks for you, friend:
1.[T][ ] read book #fun #school_work
2.[D][ ] return book (by: 6/6/2019 0900)
____________________________________________________________
____________________________________________________________
I found these tasks with that tag, friend:
1.[T][ ] read book #fun #school_work
____________________________________________________________
____________________________________________________________
Got it, friend. I've removed that tag from this task:
  [T][ ] read book #school_work
____________________________________________________________
____________________________________________________________
I found these tasks with that tag, friend:
____________________________________________________________
____________________________________________________________
I found these tasks with that tag, friend:
1.[T][ ] read book #school_work
____________________________________________________________
____________________________________________________________
OH NO!!! This task does not have that tag, friend.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list, friend:
1.[T][ ] read book #school_work
2.[D][ ] return book (by: 6/6/2019 0900)
____________________________________________________________
____________________________________________________________
Bye friend. Hope to see you again soon!
____________________________________________________________
```

### Expected saved file content

```text
T | 0 | read book | #school_work
D | 0 | return book | 2019-06-06T09:00
```

## Test Case: date-only deadlines and duplicate status changes

### Aim

Verify that deadlines can be added with a date but no time, and that marking or unmarking a task that is already in
that status reports an error without changing the task list.

### Inputs

```text
deadline submit report /by 25/8/2026
mark 1
mark 1
unmark 1
unmark 1
list
bye
```

### Expected output

```text
____________________________________________________________
 _   _       _                 ____             _    
| | | | __ _| |__  _ __  _   _|  _ \ _   _  ___| | __
| |_| |/ _` | '_ \| '_ \| | | | | | | | | |/ __| |/ /
|  _  | (_| | |_) | |_) | |_| | |_| | |_| | (__|   < 
|_| |_|\__,_|_.__/| .__/ \__, |____/ \__,_|\___|_|\_\
                  |_|    |___/                       
Hi friend! I'm HabpyDuck.
What can I do for you today?
____________________________________________________________
____________________________________________________________
Got it, friend. I've added this task:
  [D][ ] submit report (by: 25/8/2026)
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
YAY GOOD JOB!!! I've marked this task as done, friend:
  [D][X] submit report (by: 25/8/2026)
____________________________________________________________
____________________________________________________________
OH NO!!! This task is already marked, friend.
____________________________________________________________
____________________________________________________________
OK, friend. I've marked this task as not done yet:
  [D][ ] submit report (by: 25/8/2026)
____________________________________________________________
____________________________________________________________
OH NO!!! This task is already unmarked, friend.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list, friend:
1.[D][ ] submit report (by: 25/8/2026)
____________________________________________________________
____________________________________________________________
Bye friend. Hope to see you again soon!
____________________________________________________________
```

### Expected saved file content

```text
D | 0 | submit report | 2026-08-25T00:00
```
