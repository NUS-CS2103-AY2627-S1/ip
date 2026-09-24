# UI Test Plan

Run every case as a fresh session with Java 25.

For each case, run from a fresh temporary working directory so the relative
`data/sage.txt` file starts empty. This also verifies that the data directory
is created automatically.

Deadlines and event times are parsed as calendar dates and times. Users can enter
date-times in `d/M/yyyy HHmm` format, such as `2/12/2019 1800`, and Sage
displays them as `MMM dd yyyy, hh:mma`.

Launch command:

```text
javac -d build/classes/java/main $(find src/main/java -name '*.java') && java -cp build/classes/java/main sage.Sage
```

The standard startup banner is expected at the beginning of every session:

```text
____________________________________________________________
 ____                   
/ ___|  __ _  __ _  ___ 
\___ \ / _` |/ _` |/ _ \
 ___) | (_| | (_| |  __/
|____/ \__,_|\__, |\___|
             |___/      
Hello! I'm Sage.
I'm here whenever you feel like chatting!
____________________________________________________________
What can I do for you?
____________________________________________________________
```

## Test case 1: Adding a Todo

**Aim:** Verify that Sage adds a Todo and reports its count.

**Input:**
```text
todo read book
bye
```

**Expected output:**
```text
____________________________________________________________
Got it. I've added this task:
  [T][ ] read book
Now you have 1 task in the list.
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test case 2: Adding a Deadline

**Aim:** Verify that Sage adds a Deadline with its due date.

**Input:**
```text
deadline submit report /by Friday
bye
```

**Expected output:**
```text
____________________________________________________________
Got it. I've added this task:
  [D][ ] submit report (by: Friday)
Now you have 1 task in the list.
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test case 3: Adding an Event

**Aim:** Verify that Sage adds an Event with its start and end times.

**Input:**
```text
event team meeting /from Monday 10am /to Monday 11am
bye
```

**Expected output:**
```text
____________________________________________________________
Got it. I've added this task:
  [E][ ] team meeting (from: Monday 10am to: Monday 11am)
Now you have 1 task in the list.
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test case 4: Listing Todo, Deadline, and Event tasks

**Aim:** Verify that `list` displays all three task types in insertion order.

**Input:**
```text
todo read book
deadline submit report /by Friday
event team meeting /from Monday 10am /to Monday 11am
list
bye
```

**Expected output:**
```text
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] read book
2.[D][ ] submit report (by: Friday)
3.[E][ ] team meeting (from: Monday 10am to: Monday 11am)
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test case 5: Marking a task as done

**Aim:** Verify that `mark 1` marks the first task as done.

**Input:**
```text
todo read book
mark 1
bye
```

**Expected output:**
```text
____________________________________________________________
Nice! I've marked this task as done:
  [T][X] read book
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test case 6: Unmarking a task

**Aim:** Verify that `unmark 1` changes a completed task back to not done.

**Input:**
```text
todo read book
mark 1
unmark 1
bye
```

**Expected output:**
```text
____________________________________________________________
OK, I've marked this task as not done yet:
  [T][ ] read book
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test case 7: Exiting with bye

**Aim:** Verify that `bye` ends the session with the farewell message.

**Input:**
```text
bye
```

## Test case 7a: Rejecting a repeated mark command

**Aim:** Verify that marking an already completed task reports that it is already done.

**Input:**
```text
todo read book
mark 1
mark 1
bye
```

**Expected output for the repeated command:**
```text
Task 1 is already marked as done.
```

## Test case 7b: Rejecting a repeated unmark command

**Aim:** Verify that unmarking an already incomplete task reports that it is already not done.

**Input:**
```text
todo read book
unmark 1
bye
```

**Expected output:**
```text
Task 1 is already marked as not done.
```

## Test case 8: Deleting a task persists

**Aim:** Verify that deleting a task is saved to disk.

**Input:**
```text
todo temporary task
delete 1
list
bye
```

**Expected output:**
```text
Here are the tasks in your list:
____________________________________________________________
```

## Test case 9: Loading tasks after restart

**Aim:** Verify that tasks saved by one session are loaded by the next session.

**Input:**
```text
todo remember this
bye
```

Then start Sage again in the same working directory and enter:

```text
list
bye
```

**Expected output in the second session:**
```text
Here are the tasks in your list:
1.[T][ ] remember this
____________________________________________________________
```

## Test case 10: Rejecting invalid dates and event ranges

**Aim:** Verify that invalid calendar dates and non-increasing event times are reported without crashing.

**Input:**
```text
deadline submit report /by 30/2/2025 1200
event planning /from 2/3/2025 1200 /to 2/3/2025 1100
bye
```

**Expected output:**
```text
Please enter a valid date and time in the format d/M/yyyy HHmm.
An event's end time must be later than its start time.
Bye. Hope to see you again soon!
```

## Test case 11: Finding deadlines and events on a date

**Aim:** Verify that `on` displays deadlines and events occurring on a specific date.

**Input:**
```text
deadline submit report /by 2/12/2019 1800
event team meeting /from 2019-12-02 1000 /to 2019-12-02 1100
on 2019-12-02
bye
```

**Expected output:**
```text
  [D][ ] submit report (by: Dec 02 2019, 06:00pm)
  [E][ ] team meeting (from: Dec 02 2019, 10:00am to Dec 02 2019, 11:00am)
Bye. Hope to see you again soon!
```

## Test case 12: Rejecting duplicate tasks

**Aim:** Verify that an identical task is not added twice.

**Input:**
```text
todo read book
todo read book
bye
```

**Expected output:**
```text
This task is already in your list.
Bye. Hope to see you again soon!
```

**Expected output:**
```text
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test case 10: Finding tasks by keyword

**Aim:** Verify that `find` displays matching tasks with their original list numbers.

**Input:**
```text
todo read book
todo return book
todo buy milk
find book
bye
```

**Expected output:**
```text
Here are the matching tasks in your list:
1.[T][ ] read book
2.[T][ ] return book
```

## Test case 11: Finding with no matches

**Aim:** Verify that `find` reports when no task description contains the keyword.

**Input:**
```text
todo read book
find holiday
bye
```

**Expected output:**
```text
There are no tasks matching "holiday".
```

## Test case 12: Allowing punctuation in task descriptions

**Aim:** Verify that task descriptions may contain punctuation.

**Input:**
```text
todo buy milk!
bye
```

**Expected output:**
```text
Got it. I've added this task:
  [T][ ] buy milk!
Now you have 1 task in the list.
Bye. Hope to see you again soon!
```

## Test case 13: Rejecting repeated spaces in task descriptions

**Aim:** Verify that task descriptions require single spaces between words.

**Input:**
```text
todo buy  milk
bye
```

**Expected output:**
```text
Task descriptions may contain only letters, numbers, and single spaces.
```

## Test case 14: Finding without a keyword

**Aim:** Verify that `find` without a keyword reports a helpful error.

**Input:**
```text
find
bye
```

**Expected output:**
```text
Please provide a keyword to find, like `find book`.
```

## Test case 15: Finding duplicate tasks

**Aim:** Verify that `duplicates` reports duplicate Todo, Deadline, and Event tasks, ignoring completion status and normalizing descriptions.

**Input:**
```text
todo read book
todo read book
deadline submit report /by Friday
deadline SUBMIT REPORT /by Friday
event team meeting /from Monday 10am /to Monday 11am
event team meeting /from Monday 10am /to Monday 11am
mark 2
duplicates
bye
```

**Expected output:**
```text
Here are the duplicate task groups in your list:
Group:
1.[T][ ] read book
2.[T][X] read book
Group:
3.[D][ ] submit report (by: Friday)
4.[D][ ] SUBMIT REPORT (by: Friday)
Group:
5.[E][ ] team meeting (from: Monday 10am to Monday 11am)
6.[E][ ] team meeting (from: Monday 10am to Monday 11am)
```

## Test case 16: No duplicate tasks

**Aim:** Verify that tasks with different time ranges are not reported as duplicates.

**Input:**
```text
deadline submit report /by Friday
deadline submit report /by Monday
duplicates
bye
```

**Expected output:**
```text
There are no duplicate tasks.
```

## Test case 17: Duplicate command with arguments

**Aim:** Verify that `duplicates` rejects unexpected arguments.

**Input:**
```text
duplicates extra
bye
```

**Expected output:**
```text
The duplicates command does not accept arguments. Please use `duplicates`.
```

## Test case 18: Lowercase task commands

**Aim:** Verify that Todo, Deadline, and Event commands using uppercase letters explain the lowercase requirement.

**Input:**
```text
Todo read book
Deadline submit report /by Friday
EVENT team meeting /from Monday 10am /to Monday 11am
bye
```

**Expected output:**
```text
The 'Todo' command must be lowercase. Please use 'todo'.
The 'Deadline' command must be lowercase. Please use 'deadline'.
The 'EVENT' command must be lowercase. Please use 'event'.
```
