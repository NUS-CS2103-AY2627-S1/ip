# UI Test Plan

Record UI regression checks for the chatbot here. Each test case describes one fresh program run with the exact console input and expected console output.

## Farewell smoke test
Aim: Verify that the chatbot starts correctly and exits cleanly when the user enters bye.

### Inputs
```text
bye
```

### Expected Output
```text
____________________________________________________________
 ____            _            
|  _ \ _   _  __| |_ __ __ _ 
| |_) | | | |/ _` | '__/ _` |
|  _ <| |_| | (_| | | | (_| |
|_| \_\\__,_|\__,_|_|  \__,_|

Yo! Rudra's online.
What are we getting done today?
____________________________________________________________
Catch you on the flip side! Rudra signing off.
____________________________________________________________
```

## Add and list a todo
Aim: Verify that a valid todo is added and later shown in the task list.

### Inputs
```text
todo borrow book
list
bye
```

### Expected Output
```text
____________________________________________________________
 ____            _            
|  _ \ _   _  __| |_ __ __ _ 
| |_) | | | |/ _` | '__/ _` |
|  _ <| |_| | (_| | | | (_| |
|_| \_\\__,_|\__,_|_|  \__,_|

Yo! Rudra's online.
What are we getting done today?
____________________________________________________________
You got it, homie - task locked in!
[T][ ] borrow book
Your radar now has 1 task.
____________________________________________________________
Here's what's on your radar:
1.[T][ ] borrow book
____________________________________________________________
Catch you on the flip side! Rudra signing off.
____________________________________________________________
```

## Find matching tasks by keyword
Aim: Verify that `find` shows only tasks whose descriptions contain the given keyword.

### Inputs
```text
todo read book
deadline return book /by 2026-06-06
todo submit assignment
mark 1
find book
bye
```

### Expected Output
```text
____________________________________________________________
 ____            _            
|  _ \ _   _  __| |_ __ __ _ 
| |_) | | | |/ _` | '__/ _` |
|  _ <| |_| | (_| | | | (_| |
|_| \_\\__,_|\__,_|_|  \__,_|

Yo! Rudra's online.
What are we getting done today?
____________________________________________________________
You got it, homie - task locked in!
[T][ ] read book
Your radar now has 1 task.
____________________________________________________________
You got it, homie - task locked in!
[D][ ] return book (by: Jun 6 2026)
Your radar now has 2 tasks.
____________________________________________________________
You got it, homie - task locked in!
[T][ ] submit assignment
Your radar now has 3 tasks.
____________________________________________________________
Let's gooo! Another one handled:
[T][X] read book
____________________________________________________________
Here are the matches I found:
1.[T][X] read book
2.[D][ ] return book (by: Jun 6 2026)
____________________________________________________________
Catch you on the flip side! Rudra signing off.
____________________________________________________________
```

## Find with no matching tasks
Aim: Verify that `find` reports when no task descriptions contain the keyword.

### Inputs
```text
todo read book
find laundry
bye
```

### Expected Output
```text
____________________________________________________________
 ____            _            
|  _ \ _   _  __| |_ __ __ _ 
| |_) | | | |/ _` | '__/ _` |
|  _ <| |_| | (_| | | | (_| |
|_| \_\\__,_|\__,_|_|  \__,_|

Yo! Rudra's online.
What are we getting done today?
____________________________________________________________
You got it, homie - task locked in!
[T][ ] read book
Your radar now has 1 task.
____________________________________________________________
No matches on the radar, homie.
____________________________________________________________
Catch you on the flip side! Rudra signing off.
____________________________________________________________
```

## Reject an empty todo
Aim: Verify that a todo without a description is rejected with a clear error message.

### Inputs
```text
todo
bye
```

### Expected Output
```text
____________________________________________________________
 ____            _            
|  _ \ _   _  __| |_ __ __ _ 
| |_) | | | |/ _` | '__/ _` |
|  _ <| |_| | (_| | | | (_| |
|_| \_\\__,_|\__,_|_|  \__,_|

Yo! Rudra's online.
What are we getting done today?
____________________________________________________________
Whoa! The description of a todo cannot be empty.
____________________________________________________________
Catch you on the flip side! Rudra signing off.
____________________________________________________________
```

## Reject an unknown command
Aim: Verify that an unrecognized command is rejected instead of being treated as a task.

### Inputs
```text
blah
bye
```

### Expected Output
```text
____________________________________________________________
 ____            _            
|  _ \ _   _  __| |_ __ __ _ 
| |_) | | | |/ _` | '__/ _` |
|  _ <| |_| | (_| | | | (_| |
|_| \_\\__,_|\__,_|_|  \__,_|

Yo! Rudra's online.
What are we getting done today?
____________________________________________________________
Whoa! I don't recognize that command yet. Try todo, deadline, event, list, mark, unmark, delete, find, or sort.
____________________________________________________________
Catch you on the flip side! Rudra signing off.
____________________________________________________________
```

## Preserve task state after a mark error
Aim: Verify that an invalid mark command reports the error and does not accidentally modify the existing task list.

### Inputs
```text
todo borrow book
mark two
list
bye
```

### Expected Output
```text
____________________________________________________________
 ____            _            
|  _ \ _   _  __| |_ __ __ _ 
| |_) | | | |/ _` | '__/ _` |
|  _ <| |_| | (_| | | | (_| |
|_| \_\\__,_|\__,_|_|  \__,_|

Yo! Rudra's online.
What are we getting done today?
____________________________________________________________
You got it, homie - task locked in!
[T][ ] borrow book
Your radar now has 1 task.
____________________________________________________________
Whoa! Task numbers should be whole numbers.
____________________________________________________________
Here's what's on your radar:
1.[T][ ] borrow book
____________________________________________________________
Catch you on the flip side! Rudra signing off.
____________________________________________________________
```

## Mark and unmark a task
Aim: Verify that successful task status updates still show the correct task list after autosave is triggered.

### Inputs
```text
todo borrow book
mark 1
unmark 1
list
bye
```

### Expected Output
```text
____________________________________________________________
 ____            _            
|  _ \ _   _  __| |_ __ __ _ 
| |_) | | | |/ _` | '__/ _` |
|  _ <| |_| | (_| | | | (_| |
|_| \_\\__,_|\__,_|_|  \__,_|

Yo! Rudra's online.
What are we getting done today?
____________________________________________________________
You got it, homie - task locked in!
[T][ ] borrow book
Your radar now has 1 task.
____________________________________________________________
Let's gooo! Another one handled:
[T][X] borrow book
____________________________________________________________
No stress - this task is back in play:
[T][ ] borrow book
____________________________________________________________
Here's what's on your radar:
1.[T][ ] borrow book
____________________________________________________________
Catch you on the flip side! Rudra signing off.
____________________________________________________________
```

## Load saved tasks on startup
Aim: Verify that previously saved tasks are loaded from the save file when the chatbot starts.

### Preloaded Save File
```text
T | 1 | read book
D | 0 | return book | 2026-06-06
E | 0 | project meeting | 2026-08-06 1400 | 2026-08-06 1600
```

### Inputs
```text
list
bye
```

### Expected Output
```text
____________________________________________________________
 ____            _            
|  _ \ _   _  __| |_ __ __ _ 
| |_) | | | |/ _` | '__/ _` |
|  _ <| |_| | (_| | | | (_| |
|_| \_\\__,_|\__,_|_|  \__,_|

Yo! Rudra's online.
What are we getting done today?
____________________________________________________________
Here's what's on your radar:
1.[T][X] read book
2.[D][ ] return book (by: Jun 6 2026)
3.[E][ ] project meeting (from: Aug 6 2026 2:00pm to: Aug 6 2026 4:00pm)
____________________________________________________________
Catch you on the flip side! Rudra signing off.
____________________________________________________________
```

## Load tasks with escaped separators
Aim: Verify that saved task details containing the storage separator are restored correctly.

### Preloaded Save File
```text
T | 0 | revise \| review notes
D | 1 | return \| renew book | 2026-06-06
E | 0 | project \| sync | 2026-08-06 1400 | 2026-08-06 1600
```

### Inputs
```text
list
bye
```

### Expected Output
```text
____________________________________________________________
 ____            _            
|  _ \ _   _  __| |_ __ __ _ 
| |_) | | | |/ _` | '__/ _` |
|  _ <| |_| | (_| | | | (_| |
|_| \_\\__,_|\__,_|_|  \__,_|

Yo! Rudra's online.
What are we getting done today?
____________________________________________________________
Here's what's on your radar:
1.[T][ ] revise | review notes
2.[D][X] return | renew book (by: Jun 6 2026)
3.[E][ ] project | sync (from: Aug 6 2026 2:00pm to: Aug 6 2026 4:00pm)
____________________________________________________________
Catch you on the flip side! Rudra signing off.
____________________________________________________________
```

## Skip corrupted saved tasks
Aim: Verify that corrupted saved lines are ignored while valid saved tasks still load.

### Preloaded Save File
```text
T | 0 | keep me
Z | 1 | unknown task type
D | 2 | invalid status | tomorrow
E | 0 | missing end time | Aug 6th 2pm
```

### Inputs
```text
list
bye
```

### Expected Output
```text
____________________________________________________________
 ____            _            
|  _ \ _   _  __| |_ __ __ _ 
| |_) | | | |/ _` | '__/ _` |
|  _ <| |_| | (_| | | | (_| |
|_| \_\\__,_|\__,_|_|  \__,_|

Yo! Rudra's online.
What are we getting done today?
____________________________________________________________
Heads up! I skipped 3 corrupted saved task(s).
____________________________________________________________
Here's what's on your radar:
1.[T][ ] keep me
____________________________________________________________
Catch you on the flip side! Rudra signing off.
____________________________________________________________
```

## Keep list unchanged when saving fails
Aim: Verify that a save failure reports the problem and rolls back the in-memory change.

### Preloaded Save File
```text
T | 0 | keep me
```

Save Path Mode: directory

### Inputs
```text
todo new task
list
bye
```

### Expected Output
```text
____________________________________________________________
 ____            _            
|  _ \ _   _  __| |_ __ __ _ 
| |_) | | | |/ _` | '__/ _` |
|  _ <| |_| | (_| | | | (_| |
|_| \_\\__,_|\__,_|_|  \__,_|

Yo! Rudra's online.
What are we getting done today?
____________________________________________________________
I couldn't read the saved tasks from data/rudra.txt.
No stress - I'm starting with an empty task list instead.
____________________________________________________________
Whoa! I couldn't save your tasks to data/rudra.txt. Your task list was left unchanged.
____________________________________________________________
Here's what's on your radar:
____________________________________________________________
Catch you on the flip side! Rudra signing off.
____________________________________________________________
```

## Reject malformed deadline input
Aim: Verify that a deadline missing the /by section is rejected with format guidance.

### Inputs
```text
deadline return book
bye
```

### Expected Output
```text
____________________________________________________________
 ____            _            
|  _ \ _   _  __| |_ __ __ _ 
| |_) | | | |/ _` | '__/ _` |
|  _ <| |_| | (_| | | | (_| |
|_| \_\\__,_|\__,_|_|  \__,_|

Yo! Rudra's online.
What are we getting done today?
____________________________________________________________
Whoa! Please use: deadline DESCRIPTION /by WHEN
____________________________________________________________
Catch you on the flip side! Rudra signing off.
____________________________________________________________
```

## Format parsed deadline and event dates
Aim: Verify that valid date and date-time inputs are parsed and shown in the friendly output format.

### Inputs
```text
deadline return book /by 2026-12-02
event project meeting /from 2026-12-02 1400 /to 2026-12-02 1600
list
bye
```

### Expected Output
```text
____________________________________________________________
 ____            _            
|  _ \ _   _  __| |_ __ __ _ 
| |_) | | | |/ _` | '__/ _` |
|  _ <| |_| | (_| | | | (_| |
|_| \_\\__,_|\__,_|_|  \__,_|

Yo! Rudra's online.
What are we getting done today?
____________________________________________________________
You got it, homie - task locked in!
[D][ ] return book (by: Dec 2 2026)
Your radar now has 1 task.
____________________________________________________________
You got it, homie - task locked in!
[E][ ] project meeting (from: Dec 2 2026 2:00pm to: Dec 2 2026 4:00pm)
Your radar now has 2 tasks.
____________________________________________________________
Here's what's on your radar:
1.[D][ ] return book (by: Dec 2 2026)
2.[E][ ] project meeting (from: Dec 2 2026 2:00pm to: Dec 2 2026 4:00pm)
____________________________________________________________
Catch you on the flip side! Rudra signing off.
____________________________________________________________
```

## Reject invalid deadline date
Aim: Verify that an invalid deadline date format is rejected with a helpful message.

### Inputs
```text
deadline return book /by June 6th
bye
```

### Expected Output
```text
____________________________________________________________
 ____            _            
|  _ \ _   _  __| |_ __ __ _ 
| |_) | | | |/ _` | '__/ _` |
|  _ <| |_| | (_| | | | (_| |
|_| \_\\__,_|\__,_|_|  \__,_|

Yo! Rudra's online.
What are we getting done today?
____________________________________________________________
Whoa! Please enter dates as yyyy-mm-dd or yyyy-mm-dd HHmm.
____________________________________________________________
Catch you on the flip side! Rudra signing off.
____________________________________________________________
```

## Delete a task from the middle of the list
Aim: Verify that deleting a task removes the correct item and updates the task count.

### Inputs
```text
todo read book
deadline return book /by 2026-06-06
event project meeting /from 2026-08-06 1400 /to 2026-08-06 1600
delete 2
list
bye
```

### Expected Output
```text
____________________________________________________________
 ____            _            
|  _ \ _   _  __| |_ __ __ _ 
| |_) | | | |/ _` | '__/ _` |
|  _ <| |_| | (_| | | | (_| |
|_| \_\\__,_|\__,_|_|  \__,_|

Yo! Rudra's online.
What are we getting done today?
____________________________________________________________
You got it, homie - task locked in!
[T][ ] read book
Your radar now has 1 task.
____________________________________________________________
You got it, homie - task locked in!
[D][ ] return book (by: Jun 6 2026)
Your radar now has 2 tasks.
____________________________________________________________
You got it, homie - task locked in!
[E][ ] project meeting (from: Aug 6 2026 2:00pm to: Aug 6 2026 4:00pm)
Your radar now has 3 tasks.
____________________________________________________________
Poof! This task is outta here:
[D][ ] return book (by: Jun 6 2026)
Your radar now has 2 tasks.
____________________________________________________________
Here's what's on your radar:
1.[T][ ] read book
2.[E][ ] project meeting (from: Aug 6 2026 2:00pm to: Aug 6 2026 4:00pm)
____________________________________________________________
Catch you on the flip side! Rudra signing off.
____________________________________________________________
```

## Preserve task state after an invalid delete
Aim: Verify that a malformed delete command reports the error and does not remove any tasks.

### Inputs
```text
todo borrow book
delete two
list
bye
```

### Expected Output
```text
____________________________________________________________
 ____            _            
|  _ \ _   _  __| |_ __ __ _ 
| |_) | | | |/ _` | '__/ _` |
|  _ <| |_| | (_| | | | (_| |
|_| \_\\__,_|\__,_|_|  \__,_|

Yo! Rudra's online.
What are we getting done today?
____________________________________________________________
You got it, homie - task locked in!
[T][ ] borrow book
Your radar now has 1 task.
____________________________________________________________
Whoa! Task numbers should be whole numbers.
____________________________________________________________
Here's what's on your radar:
1.[T][ ] borrow book
____________________________________________________________
Catch you on the flip side! Rudra signing off.
____________________________________________________________
```

## Sort deadlines chronologically
Aim: Verify that sorting a mixed task list moves deadlines first in chronological order and updates task numbering.

### Inputs
```text
todo buy milk
deadline submit report /by 2026-10-20
event team sync /from 2026-09-15 1400 /to 2026-09-15 1500
deadline renew license /by 2026-09-10
todo call Alice
sort deadline
mark 1
bye
```

### Expected Output
```text
____________________________________________________________
 ____            _            
|  _ \ _   _  __| |_ __ __ _ 
| |_) | | | |/ _` | '__/ _` |
|  _ <| |_| | (_| | | | (_| |
|_| \_\\__,_|\__,_|_|  \__,_|

Yo! Rudra's online.
What are we getting done today?
____________________________________________________________
You got it, homie - task locked in!
[T][ ] buy milk
Your radar now has 1 task.
____________________________________________________________
You got it, homie - task locked in!
[D][ ] submit report (by: Oct 20 2026)
Your radar now has 2 tasks.
____________________________________________________________
You got it, homie - task locked in!
[E][ ] team sync (from: Sep 15 2026 2:00pm to: Sep 15 2026 3:00pm)
Your radar now has 3 tasks.
____________________________________________________________
You got it, homie - task locked in!
[D][ ] renew license (by: Sep 10 2026)
Your radar now has 4 tasks.
____________________________________________________________
You got it, homie - task locked in!
[T][ ] call Alice
Your radar now has 5 tasks.
____________________________________________________________
All set - your deadlines now run from earliest to latest.
Here's what's on your radar:
1.[D][ ] renew license (by: Sep 10 2026)
2.[D][ ] submit report (by: Oct 20 2026)
3.[T][ ] buy milk
4.[E][ ] team sync (from: Sep 15 2026 2:00pm to: Sep 15 2026 3:00pm)
5.[T][ ] call Alice
____________________________________________________________
Let's gooo! Another one handled:
[D][X] renew license (by: Sep 10 2026)
____________________________________________________________
Catch you on the flip side! Rudra signing off.
____________________________________________________________
```
