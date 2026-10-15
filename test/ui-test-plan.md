# UI Test Plan

Program command: `java -cp build/classes/java/main:build/resources/main dukey.Dukey`
Compile command: `./gradlew classes`
Data file: `src/main/data/dukey.txt`

## Test Case: Loads tasks with date times from disk

Aim: Verify that the chatbot loads todo, deadline, and event tasks from the data file, and displays deadline/event date-times in the user-facing format.

Initial data:
```text
T | 1 | read book
D | 0 | return book | 2099-12-06 1800
E | 0 | project meeting | 2099-08-06 1400 | 2099-08-06 1600
T | 1 | join sports club
```

Input:
```text
list
bye
```

Expected output:
```text
____________________________________________________________
 ____        _              
|  _ \ _   _| | _____ _   _ 
| | | | | | | |/ / _ \ | | |
| |_| | |_| |   <  __/ |_| |
|____/ \__,_|_|\_\___|\__, |
                       |___/ 

Hello! I'm Dukey, your calm task librarian.
What shall we shelve, sort, or settle today?
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][X] read book
2.[D][ ] return book (by: Dec 06 2099, 6:00pm)
3.[E][ ] project meeting (from: Aug 06 2099, 2:00pm to: Aug 06 2099, 4:00pm)
4.[T][X] join sports club
____________________________________________________________
____________________________________________________________
Bye. The shelves are tidy for now.
____________________________________________________________
```

Expected data:
```text
T | 1 | read book |
D | 0 | return book | 2099-12-06 1800 |
E | 0 | project meeting | 2099-08-06 1400 | 2099-08-06 1600 |
T | 1 | join sports club |
```

## Test Case: Finds tasks by keyword

Aim: Verify that the `find` command lists tasks whose descriptions contain the given keyword.

Initial data:
```text
T | 1 | read book
D | 1 | return book | 2099-12-06 1800
E | 0 | project meeting | 2099-08-06 1400 | 2099-08-06 1600
T | 1 | join sports club
```

Input:
```text
find book
bye
```

Expected output:
```text
____________________________________________________________
 ____        _              
|  _ \ _   _| | _____ _   _ 
| | | | | | | |/ / _ \ | | |
| |_| | |_| |   <  __/ |_| |
|____/ \__,_|_|\_\___|\__, |
                       |___/ 

Hello! I'm Dukey, your calm task librarian.
What shall we shelve, sort, or settle today?
____________________________________________________________
____________________________________________________________
Here are the matching tasks in your list:
1.[T][X] read book
2.[D][X] return book (by: Dec 06 2099, 6:00pm)
____________________________________________________________
____________________________________________________________
Bye. The shelves are tidy for now.
____________________________________________________________
```

Expected data:
```text
T | 1 | read book |
D | 1 | return book | 2099-12-06 1800 |
E | 0 | project meeting | 2099-08-06 1400 | 2099-08-06 1600 |
T | 1 | join sports club |
```

## Test Case: Shows task statistics

Aim: Verify that the `stats` command reports total, completed, pending, current-week, and current-month counts.

Initial data:
```text
T | 1 | read book
D | 1 | return book | 2099-12-06 1800
T | 0 | borrow book
```

Input:
```text
stats
bye
```

Expected output:
```text
____________________________________________________________
 ____        _              
|  _ \ _   _| | _____ _   _ 
| | | | | | | |/ / _ \ | | |
| |_| | |_| |   <  __/ |_| |
|____/ \__,_|_|\_\___|\__, |
                       |___/ 

Hello! I'm Dukey, your calm task librarian.
What shall we shelve, sort, or settle today?
____________________________________________________________
____________________________________________________________
Here are your task statistics:
Total tasks: 3
Completed tasks: 2
Pending tasks: 1
Completed in the current calendar week: 0
Completed in the current calendar month: 0
____________________________________________________________
____________________________________________________________
Bye. The shelves are tidy for now.
____________________________________________________________
```

Expected data:
```text
T | 1 | read book |
D | 1 | return book | 2099-12-06 1800 |
T | 0 | borrow book |
```

## Test Case: Rejects stats arguments

Aim: Verify that the `stats` command rejects extra arguments and does not change saved task data.

Initial data:
```text
T | 1 | read book
```

Input:
```text
stats today
bye
```

Expected output:
```text
____________________________________________________________
 ____        _              
|  _ \ _   _| | _____ _   _ 
| | | | | | | |/ / _ \ | | |
| |_| | |_| |   <  __/ |_| |
|____/ \__,_|_|\_\___|\__, |
                       |___/ 

Hello! I'm Dukey, your calm task librarian.
What shall we shelve, sort, or settle today?
____________________________________________________________
____________________________________________________________
 OOPS!!! The stats command does not take any arguments. Let's keep the shelves neat.
____________________________________________________________
____________________________________________________________
Bye. The shelves are tidy for now.
____________________________________________________________
```

Expected data:
```text
T | 1 | read book |
```

## Test Case: Shows error for invalid saved date time

Aim: Verify that Storage reports malformed saved date-time data when loading tasks from disk.

Initial data:
```text
T | 1 | read book
D | 0 | return book | not-a-date
```

Input:
```text
list
bye
```

Expected output:
```text
____________________________________________________________
 ____        _              
|  _ \ _   _| | _____ _   _ 
| | | | | | | |/ / _ \ | | |
| |_| | |_| |   <  __/ |_| |
|____/ \__,_|_|\_\___|\__, |
                       |___/ 

Hello! I'm Dukey, your calm task librarian.
What shall we shelve, sort, or settle today?
____________________________________________________________
 OOPS!!! Saved date/time must use format: yyyy-MM-dd HHmm Let's keep the shelves neat.
____________________________________________________________
Here are the tasks in your list:
____________________________________________________________
____________________________________________________________
Bye. The shelves are tidy for now.
____________________________________________________________
```

## Test Case: Saves added todo automatically

Aim: Verify that adding a todo updates the data file without needing another task-list command.

Initial data:
```text
T | 1 | read book
D | 0 | return book | 2099-12-06 1800
E | 0 | project meeting | 2099-08-06 1400 | 2099-08-06 1600
T | 1 | join sports club
```

Input:
```text
todo borrow book
bye
```

Expected output:
```text
____________________________________________________________
 ____        _              
|  _ \ _   _| | _____ _   _ 
| | | | | | | |/ / _ \ | | |
| |_| | |_| |   <  __/ |_| |
|____/ \__,_|_|\_\___|\__, |
                       |___/ 

Hello! I'm Dukey, your calm task librarian.
What shall we shelve, sort, or settle today?
____________________________________________________________
____________________________________________________________
Filed neatly. I've added this task:
  [T][ ] borrow book
Now you have 5 tasks in the list.
____________________________________________________________
____________________________________________________________
Bye. The shelves are tidy for now.
____________________________________________________________
```

Expected data:
```text
T | 1 | read book |
D | 0 | return book | 2099-12-06 1800 |
E | 0 | project meeting | 2099-08-06 1400 | 2099-08-06 1600 |
T | 1 | join sports club |
T | 0 | borrow book |
```

## Test Case: Saves added deadline and event automatically

Aim: Verify that added deadline and event tasks are saved in the data file using parseable LocalDateTime values.

Initial data:
```text
T | 1 | read book
```

Input:
```text
deadline return book /by 2099-12-02 1800
event project meeting /from 2099-08-06 1400 /to 2099-08-06 1600
bye
```

Expected output:
```text
____________________________________________________________
 ____        _              
|  _ \ _   _| | _____ _   _ 
| | | | | | | |/ / _ \ | | |
| |_| | |_| |   <  __/ |_| |
|____/ \__,_|_|\_\___|\__, |
                       |___/ 

Hello! I'm Dukey, your calm task librarian.
What shall we shelve, sort, or settle today?
____________________________________________________________
____________________________________________________________
Filed neatly. I've added this task:
  [D][ ] return book (by: Dec 02 2099, 6:00pm)
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Filed neatly. I've added this task:
  [E][ ] project meeting (from: Aug 06 2099, 2:00pm to: Aug 06 2099, 4:00pm)
Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
Bye. The shelves are tidy for now.
____________________________________________________________
```

Expected data:
```text
T | 1 | read book |
D | 0 | return book | 2099-12-02 1800 |
E | 0 | project meeting | 2099-08-06 1400 | 2099-08-06 1600 |
```

## Test Case: Saves mark and unmark automatically

Aim: Verify that marking and unmarking tasks updates the saved done statuses.

Initial data:
```text
T | 1 | read book
D | 0 | return book | 2099-12-06 1800
E | 0 | project meeting | 2099-08-06 1400 | 2099-08-06 1600
T | 1 | join sports club
```

Input:
```text
mark 2
unmark 4
bye
```

Expected output:
```text
____________________________________________________________
 ____        _              
|  _ \ _   _| | _____ _   _ 
| | | | | | | |/ / _ \ | | |
| |_| | |_| |   <  __/ |_| |
|____/ \__,_|_|\_\___|\__, |
                       |___/ 

Hello! I'm Dukey, your calm task librarian.
What shall we shelve, sort, or settle today?
____________________________________________________________
____________________________________________________________
Stamped and settled. I've marked this task as done:
  [D][X] return book (by: Dec 06 2099, 6:00pm)
____________________________________________________________
____________________________________________________________
Back on the shelf. I've marked this task as not done yet:
  [T][ ] join sports club
____________________________________________________________
____________________________________________________________
Bye. The shelves are tidy for now.
____________________________________________________________
```

## Test Case: Saves delete automatically

Aim: Verify that deleting a task removes it from the data file.

Initial data:
```text
T | 1 | read book
D | 0 | return book | 2099-12-06 1800
E | 0 | project meeting | 2099-08-06 1400 | 2099-08-06 1600
T | 1 | join sports club
```

Input:
```text
delete 3
bye
```

Expected output:
```text
____________________________________________________________
 ____        _              
|  _ \ _   _| | _____ _   _ 
| | | | | | | |/ / _ \ | | |
| |_| | |_| |   <  __/ |_| |
|____/ \__,_|_|\_\___|\__, |
                       |___/ 

Hello! I'm Dukey, your calm task librarian.
What shall we shelve, sort, or settle today?
____________________________________________________________
____________________________________________________________
Removed from the shelf. I've removed this task:
  [E][ ] project meeting (from: Aug 06 2099, 2:00pm to: Aug 06 2099, 4:00pm)
Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
Bye. The shelves are tidy for now.
____________________________________________________________
```

Expected data:
```text
T | 1 | read book |
D | 0 | return book | 2099-12-06 1800 |
T | 1 | join sports club |
```

## Test Case: Rejects invalid command inputs

Aim: Verify that command errors do not change the saved task data.

Initial data:
```text
T | 1 | read book
D | 0 | return book | 2099-12-06 1800
E | 0 | project meeting | 2099-08-06 1400 | 2099-08-06 1600
T | 1 | join sports club
```

Input:
```text
todo
deadline return book Sunday
event project meeting /from 2099-08-06 1400 /to
delete 999
blah
bye
```

Expected output:
```text
____________________________________________________________
 ____        _              
|  _ \ _   _| | _____ _   _ 
| | | | | | | |/ / _ \ | | |
| |_| | |_| |   <  __/ |_| |
|____/ \__,_|_|\_\___|\__, |
                       |___/ 

Hello! I'm Dukey, your calm task librarian.
What shall we shelve, sort, or settle today?
____________________________________________________________
____________________________________________________________
 OOPS!!! The description of a todo cannot be empty. Let's keep the shelves neat.
____________________________________________________________
____________________________________________________________
 OOPS!!! Please use: deadline {DESCRIPTION} /by {WHEN} Let's keep the shelves neat.
____________________________________________________________
____________________________________________________________
 OOPS!!! Please provide a event task date/time after /to. Let's keep the shelves neat.
____________________________________________________________
____________________________________________________________
 OOPS!!! Please provide a valid task number. Let's keep the shelves neat.
____________________________________________________________
____________________________________________________________
 OOPS!!! I'm sorry, but I don't know what that means :-( Let's keep the shelves neat.
____________________________________________________________
____________________________________________________________
Bye. The shelves are tidy for now.
____________________________________________________________
```

Expected data:
```text
T | 1 | read book |
D | 0 | return book | 2099-12-06 1800 |
E | 0 | project meeting | 2099-08-06 1400 | 2099-08-06 1600 |
T | 1 | join sports club |
```

## Test Case: Handles extra spaces and duplicate keywords

Aim: Verify that leading/trailing spaces around commands are accepted, while duplicated command keywords are rejected.

Initial data:
```text
T | 1 | read book
D | 0 | return book | 2099-12-06 1800
```

Input:
```text
  list  
deadline return book /by 2099-12-06 1800 /by 2099-12-07 1800
bye
```

Expected output:
```text
____________________________________________________________
 ____        _              
|  _ \ _   _| | _____ _   _ 
| | | | | | | |/ / _ \ | | |
| |_| | |_| |   <  __/ |_| |
|____/ \__,_|_|\_\___|\__, |
                       |___/ 

Hello! I'm Dukey, your calm task librarian.
What shall we shelve, sort, or settle today?
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][X] read book
2.[D][ ] return book (by: Dec 06 2099, 6:00pm)
____________________________________________________________
____________________________________________________________
 OOPS!!! Please use: deadline {DESCRIPTION} /by {WHEN} Let's keep the shelves neat.
____________________________________________________________
____________________________________________________________
Bye. The shelves are tidy for now.
____________________________________________________________
```

Expected data:
```text
T | 1 | read book |
D | 0 | return book | 2099-12-06 1800 |
```

## Test Case: Rejects past deadline

Aim: Verify that a deadline with a past date-time is rejected and not saved.

Initial data:
```text
T | 1 | read book
```

Input:
```text
deadline old task /by 2020-01-01 1200
bye
```

Expected output:
```text
____________________________________________________________
 ____        _              
|  _ \ _   _| | _____ _   _ 
| | | | | | | |/ / _ \ | | |
| |_| | |_| |   <  __/ |_| |
|____/ \__,_|_|\_\___|\__, |
                       |___/ 

Hello! I'm Dukey, your calm task librarian.
What shall we shelve, sort, or settle today?
____________________________________________________________
____________________________________________________________
 OOPS!!! Deadline date/time cannot be in the past. Let's keep the shelves neat.
____________________________________________________________
____________________________________________________________
Bye. The shelves are tidy for now.
____________________________________________________________
```

Expected data:
```text
T | 1 | read book |
```

## Test Case: Lists deadlines and events on a date

Aim: Verify that the `on` command lists deadlines due on the given date and events occurring on the given date.

Initial data:
```text
T | 1 | read book
D | 0 | return book | 2099-12-06 1800
E | 0 | project meeting | 2099-12-06 1400 | 2099-12-07 1600
D | 0 | submit report | 2099-12-08 0900
```

Input:
```text
on 2099-12-06
on 2099-12-07
on 2099-12-09
bye
```

Expected output:
```text
____________________________________________________________
 ____        _              
|  _ \ _   _| | _____ _   _ 
| | | | | | | |/ / _ \ | | |
| |_| | |_| |   <  __/ |_| |
|____/ \__,_|_|\_\___|\__, |
                       |___/ 

Hello! I'm Dukey, your calm task librarian.
What shall we shelve, sort, or settle today?
____________________________________________________________
____________________________________________________________
Here are the deadlines and events on that date:
2.[D][ ] return book (by: Dec 06 2099, 6:00pm)
3.[E][ ] project meeting (from: Dec 06 2099, 2:00pm to: Dec 07 2099, 4:00pm)
____________________________________________________________
____________________________________________________________
Here are the deadlines and events on that date:
3.[E][ ] project meeting (from: Dec 06 2099, 2:00pm to: Dec 07 2099, 4:00pm)
____________________________________________________________
____________________________________________________________
Here are the deadlines and events on that date:
There are no deadlines or events on that date.
____________________________________________________________
____________________________________________________________
Bye. The shelves are tidy for now.
____________________________________________________________
```

Expected data:
```text
T | 1 | read book |
D | 0 | return book | 2099-12-06 1800 |
E | 0 | project meeting | 2099-12-06 1400 | 2099-12-07 1600 |
D | 0 | submit report | 2099-12-08 0900 |
```

## Test Case: Rejects invalid on command dates

Aim: Verify that missing or malformed `on` command dates show errors and do not change saved data.

Initial data:
```text
T | 1 | read book
D | 0 | return book | 2099-12-06 1800
```

Input:
```text
on
on Sunday
bye
```

Expected output:
```text
____________________________________________________________
 ____        _              
|  _ \ _   _| | _____ _   _ 
| | | | | | | |/ / _ \ | | |
| |_| | |_| |   <  __/ |_| |
|____/ \__,_|_|\_\___|\__, |
                       |___/ 

Hello! I'm Dukey, your calm task librarian.
What shall we shelve, sort, or settle today?
____________________________________________________________
____________________________________________________________
 OOPS!!! Please provide a date using format: yyyy-MM-dd Let's keep the shelves neat.
____________________________________________________________
____________________________________________________________
 OOPS!!! Please use date format: yyyy-MM-dd Let's keep the shelves neat.
____________________________________________________________
____________________________________________________________
Bye. The shelves are tidy for now.
____________________________________________________________
```

Expected data:
```text
T | 1 | read book |
D | 0 | return book | 2099-12-06 1800 |
```

## Test Case: Rejects event whose start is after end

Aim: Verify that an event with a start date-time later than its end date-time is rejected and not saved.

Initial data:
```text
T | 1 | read book
```

Input:
```text
event backwards meeting /from 2099-08-06 1600 /to 2099-08-06 1400
bye
```

Expected output:
```text
____________________________________________________________
 ____        _              
|  _ \ _   _| | _____ _   _ 
| | | | | | | |/ / _ \ | | |
| |_| | |_| |   <  __/ |_| |
|____/ \__,_|_|\_\___|\__, |
                       |___/ 

Hello! I'm Dukey, your calm task librarian.
What shall we shelve, sort, or settle today?
____________________________________________________________
____________________________________________________________
 OOPS!!! Event start date/time cannot be later than end date/time. Let's keep the shelves neat.
____________________________________________________________
____________________________________________________________
Bye. The shelves are tidy for now.
____________________________________________________________
```

Expected data:
```text
T | 1 | read book |
```
