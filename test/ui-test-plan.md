# UI Test Plan

These tests exercise Quackie's interactive command-line behavior. Each test case starts a fresh process and sends the commands in its `Inputs` block in order.

- Compile command: `javac -d /tmp/ui-test-classes $(find src/main/java -name '*.java' ! -path '*/gui/*' ! -name 'Launcher.java' -print)`
- Program command: `rm -f data/quackie.txt && java -cp /tmp/ui-test-classes quackie.Quackie`

## Test Case 1: Add a ToDo and list it

- Aim: Verify that `todo` creates a ToDo task and that `list` displays its type and not-done status.
- Inputs:
  ```text
  todo read book
  list
  bye
  ```
- Expected output:
  ```text
____________________________________________________________
                           _      _      
  __ _  _   _   __ _  ___| | __ (_)  ___ 
 / _` || | | | / _` |/ __| |/ / | | / _ \ 
| (_| || |_| || (_| | (__|   <  | ||  __/
 \__, | \__,_| \__,_|\___|_|\_\ |_|\___|
    |_|                                  
Hello! I'm Quackie.
What can I do for you?
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] read book
 Now you have 1 task in the list.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] read book
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
  ```

## Test Case 2: Accept irregular command whitespace

* Aim: Verify that leading, trailing, and repeated whitespace does not prevent a valid command from being parsed.
* Inputs:
  ```text
    todo   read   book  
  list
  bye
  ```
* Expected output:
  ```text
____________________________________________________________
                           _      _      
  __ _  _   _   __ _  ___| | __ (_)  ___ 
 / _` || | | | / _` |/ __| |/ / | | / _ \ 
| (_| || |_| || (_| | (__|   <  | ||  __/
 \__, | \__,_| \__,_|\___|_|\_\ |_|\___|
    |_|                                  
Hello! I'm Quackie.
What can I do for you?
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] read book
 Now you have 1 task in the list.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] read book
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
  ```

## Test Case 3: Reject ambiguous and inconsistent task data

* Aim: Verify that duplicate tasks, repeated markers, and reversed structured event times are rejected.
* Inputs:
  ```text
  todo read book
  todo READ BOOK
  deadline report /by Friday /by Saturday
  event meeting /from 2pm /from 3pm /to 4pm
  event meeting /from 2/12/2019 1600 /to 2/12/2019 1400
  list
  bye
  ```
* Expected output:
  ```text
____________________________________________________________
                           _      _      
  __ _  _   _   __ _  ___| | __ (_)  ___ 
 / _` || | | | / _` |/ __| |/ / | | / _ \ 
| (_| || |_| || (_| | (__|   <  | ||  __/
 \__, | \__,_| \__,_|\___|_|\_\ |_|\___|
    |_|                                  
Hello! I'm Quackie.
What can I do for you?
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] read book
 Now you have 1 task in the list.
____________________________________________________________
____________________________________________________________
 OOPS!!! That task is already in your list.
____________________________________________________________
____________________________________________________________
 OOPS!!! A deadline needs exactly one /by date or time.
____________________________________________________________
____________________________________________________________
 OOPS!!! An event needs exactly one /from time and one /to time.
____________________________________________________________
____________________________________________________________
 OOPS!!! The event end time must be after its start time.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] read book
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
  ```

## Test Case 4: Reject invalid delete task numbers

- Aim: Verify that missing, zero, out-of-range, and non-numeric delete arguments do not change the task list.
- Inputs:
  ```text
  todo read book
  delete
  delete 0
  delete 99
  delete nope
  list
  bye
  ```
- Expected output:
  ```text
____________________________________________________________
                           _      _      
  __ _  _   _   __ _  ___| | __ (_)  ___ 
 / _` || | | | / _` |/ __| |/ / | | / _ \ 
| (_| || |_| || (_| | (__|   <  | ||  __/
 \__, | \__,_| \__,_|\___|_|\_\ |_|\___|
    |_|                                  
Hello! I'm Quackie.
What can I do for you?
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] read book
 Now you have 1 task in the list.
____________________________________________________________
____________________________________________________________
 OOPS!!! Please provide a valid task number.
____________________________________________________________
____________________________________________________________
 OOPS!!! Please provide a valid task number.
____________________________________________________________
____________________________________________________________
 OOPS!!! Please provide a valid task number.
____________________________________________________________
____________________________________________________________
 OOPS!!! Please provide a valid task number.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] read book
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
  ```

## Test Case 5: Delete the only task

- Aim: Verify that deleting the only task leaves an empty list and reports zero tasks.
- Inputs:
  ```text
  todo only task
  delete 1
  list
  bye
  ```
- Expected output:
  ```text
____________________________________________________________
                           _      _      
  __ _  _   _   __ _  ___| | __ (_)  ___ 
 / _` || | | | / _` |/ __| |/ / | | / _ \ 
| (_| || |_| || (_| | (__|   <  | ||  __/
 \__, | \__,_| \__,_|\___|_|\_\ |_|\___|
    |_|                                  
Hello! I'm Quackie.
What can I do for you?
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] only task
 Now you have 1 task in the list.
____________________________________________________________
____________________________________________________________
 Noted. I've removed this task:
   [T][ ] only task
 Now you have 0 tasks in the list.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
  ```

## Test Case 6: Compact the list after deleting first and last tasks

- Aim: Verify that deleting tasks at both ends shifts the remaining task into the correct list position.
- Inputs:
  ```text
  todo first
  todo second
  todo third
  delete 1
  delete 2
  list
  bye
  ```
- Expected output:
  ```text
____________________________________________________________
                           _      _      
  __ _  _   _   __ _  ___| | __ (_)  ___ 
 / _` || | | | / _` |/ __| |/ / | | / _ \ 
| (_| || |_| || (_| | (__|   <  | ||  __/
 \__, | \__,_| \__,_|\___|_|\_\ |_|\___|
    |_|                                  
Hello! I'm Quackie.
What can I do for you?
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] first
 Now you have 1 task in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] second
 Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] third
 Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
 Noted. I've removed this task:
   [T][ ] first
 Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 Noted. I've removed this task:
   [T][ ] third
 Now you have 1 task in the list.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] second
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
  ```

## Test Case 7: Delete a completed task from mixed task types

- Aim: Verify that delete removes the selected task while preserving the remaining task types and statuses.
- Inputs:
  ```text
  todo read book
  deadline return book /by Sunday
  event project meeting /from Mon 2pm /to 4pm
  mark 2
  delete 2
  list
  bye
  ```
- Expected output:
  ```text
____________________________________________________________
                           _      _      
  __ _  _   _   __ _  ___| | __ (_)  ___ 
 / _` || | | | / _` |/ __| |/ / | | / _ \ 
| (_| || |_| || (_| | (__|   <  | ||  __/
 \__, | \__,_| \__,_|\___|_|\_\ |_|\___|
    |_|                                  
Hello! I'm Quackie.
What can I do for you?
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] read book
 Now you have 1 task in the list.
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
 Nice! I've marked this task as done:
   [D][X] return book (by: Sunday)
____________________________________________________________
____________________________________________________________
 Noted. I've removed this task:
   [D][X] return book (by: Sunday)
 Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] read book
 2.[E][ ] project meeting (from: Mon 2pm to: 4pm)
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
  ```

## Test Case 8: Mark a ToDo as done

- Aim: Verify that `mark N` changes a ToDo's status to done and that `list` shows `[X]`.
- Inputs:
  ```text
  todo read book
  todo return book
  mark 2
  list
  bye
  ```
- Expected output:
  ```text
____________________________________________________________
                           _      _      
  __ _  _   _   __ _  ___| | __ (_)  ___ 
 / _` || | | | / _` |/ __| |/ / | | / _ \ 
| (_| || |_| || (_| | (__|   <  | ||  __/
 \__, | \__,_| \__,_|\___|_|\_\ |_|\___|
    |_|                                  
Hello! I'm Quackie.
What can I do for you?
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] read book
 Now you have 1 task in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] return book
 Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 Nice! I've marked this task as done:
   [T][X] return book
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] read book
 2.[T][X] return book
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
  ```

## Test Case 9: Reverse a ToDo's done status

- Aim: Verify that `unmark N` changes a completed ToDo back to not done.
- Inputs:
  ```text
  todo read book
  todo return book
  mark 2
  unmark 2
  list
  bye
  ```
- Expected output:
  ```text
____________________________________________________________
                           _      _      
  __ _  _   _   __ _  ___| | __ (_)  ___ 
 / _` || | | | / _` |/ __| |/ / | | / _ \ 
| (_| || |_| || (_| | (__|   <  | ||  __/
 \__, | \__,_| \__,_|\___|_|\_\ |_|\___|
    |_|                                  
Hello! I'm Quackie.
What can I do for you?
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] read book
 Now you have 1 task in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] return book
 Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 Nice! I've marked this task as done:
   [T][X] return book
____________________________________________________________
____________________________________________________________
 OK, I've marked this task as not done yet:
   [T][ ] return book
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] read book
 2.[T][ ] return book
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
  ```

## Test Case 10: Add a Deadline

- Aim: Verify that `deadline` stores the task description and its `/by` date or time.
- Inputs:
  ```text
  deadline return book /by Sunday
  list
  bye
  ```
- Expected output:
  ```text
____________________________________________________________
                           _      _      
  __ _  _   _   __ _  ___| | __ (_)  ___ 
 / _` || | | | / _` |/ __| |/ / | | / _ \ 
| (_| || |_| || (_| | (__|   <  | ||  __/
 \__, | \__,_| \__,_|\___|_|\_\ |_|\___|
    |_|                                  
Hello! I'm Quackie.
What can I do for you?
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [D][ ] return book (by: Sunday)
 Now you have 1 task in the list.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[D][ ] return book (by: Sunday)
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
  ```

## Test Case 11: Add an Event

- Aim: Verify that `event` stores the description, start time, and end time.
- Inputs:
  ```text
  event project meeting /from Mon 2pm /to 4pm
  list
  bye
  ```
- Expected output:
  ```text
____________________________________________________________
                           _      _      
  __ _  _   _   __ _  ___| | __ (_)  ___ 
 / _` || | | | / _` |/ __| |/ / | | / _ \ 
| (_| || |_| || (_| | (__|   <  | ||  __/
 \__, | \__,_| \__,_|\___|_|\_\ |_|\___|
    |_|                                  
Hello! I'm Quackie.
What can I do for you?
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [E][ ] project meeting (from: Mon 2pm to: 4pm)
 Now you have 1 task in the list.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[E][ ] project meeting (from: Mon 2pm to: 4pm)
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
  ```

## Test Case 12: Reject an empty ToDo

- Aim: Verify that an empty `todo` is rejected without adding a task.
- Inputs:
  ```text
  todo
  todo read book
  list
  bye
  ```
- Expected output:
  ```text
____________________________________________________________
                           _      _      
  __ _  _   _   __ _  ___| | __ (_)  ___ 
 / _` || | | | / _` |/ __| |/ / | | / _ \ 
| (_| || |_| || (_| | (__|   <  | ||  __/
 \__, | \__,_| \__,_|\___|_|\_\ |_|\___|
    |_|                                  
Hello! I'm Quackie.
What can I do for you?
____________________________________________________________
____________________________________________________________
 OOPS!!! A ToDo needs a description.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] read book
 Now you have 1 task in the list.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] read book
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
  ```

## Test Case 13: Reject an unknown command

- Aim: Verify that an unrecognized command is rejected without changing the task list.
- Inputs:
  ```text
  blah
  todo read book
  list
  bye
  ```
- Expected output:
  ```text
____________________________________________________________
                           _      _      
  __ _  _   _   __ _  ___| | __ (_)  ___ 
 / _` || | | | / _` |/ __| |/ / | | / _ \ 
| (_| || |_| || (_| | (__|   <  | ||  __/
 \__, | \__,_| \__,_|\___|_|\_\ |_|\___|
    |_|                                  
Hello! I'm Quackie.
What can I do for you?
____________________________________________________________
____________________________________________________________
 OOPS!!! I don't recognize that command.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] read book
 Now you have 1 task in the list.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] read book
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
  ```

## Test Case 14: Reject a blank command

- Aim: Verify that blank input is rejected without changing the task list.
- Inputs:
  ```text

  bye
  ```
- Expected output:
  ```text
____________________________________________________________
                           _      _      
  __ _  _   _   __ _  ___| | __ (_)  ___ 
 / _` || | | | / _` |/ __| |/ / | | / _ \ 
| (_| || |_| || (_| | (__|   <  | ||  __/
 \__, | \__,_| \__,_|\___|_|\_\ |_|\___|
    |_|                                  
Hello! I'm Quackie.
What can I do for you?
____________________________________________________________
____________________________________________________________
 OOPS!!! Please enter a command.
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
  ```

## Test Case 15: Reject invalid mark and unmark task numbers

- Aim: Verify that missing, out-of-range, and non-numeric task numbers are rejected without changing task status.
- Inputs:
  ```text
  todo read book
  mark
  mark 99
  unmark nope
  list
  bye
  ```
- Expected output:
  ```text
____________________________________________________________
                           _      _      
  __ _  _   _   __ _  ___| | __ (_)  ___ 
 / _` || | | | / _` |/ __| |/ / | | / _ \ 
| (_| || |_| || (_| | (__|   <  | ||  __/
 \__, | \__,_| \__,_|\___|_|\_\ |_|\___|
    |_|                                  
Hello! I'm Quackie.
What can I do for you?
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] read book
 Now you have 1 task in the list.
____________________________________________________________
____________________________________________________________
 OOPS!!! Please provide a valid task number.
____________________________________________________________
____________________________________________________________
 OOPS!!! Please provide a valid task number.
____________________________________________________________
____________________________________________________________
 OOPS!!! Please provide a valid task number.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] read book
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
  ```

## Test Case 16: Reject a malformed deadline

- Aim: Verify that a deadline without a description or `/by` value is rejected without adding a task.
- Inputs:
  ```text
  deadline return book
  bye
  ```
- Expected output:
  ```text
____________________________________________________________
                           _      _      
  __ _  _   _   __ _  ___| | __ (_)  ___ 
 / _` || | | | / _` |/ __| |/ / | | / _ \ 
| (_| || |_| || (_| | (__|   <  | ||  __/
 \__, | \__,_| \__,_|\___|_|\_\ |_|\___|
    |_|                                  
Hello! I'm Quackie.
What can I do for you?
____________________________________________________________
____________________________________________________________
 OOPS!!! A deadline needs a description and a /by date or time.
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
  ```

## Test Case 17: Reject a malformed event

- Aim: Verify that an event without a description, `/from`, or `/to` value is rejected without adding a task.
- Inputs:
  ```text
  event project meeting
  bye
  ```
- Expected output:
  ```text
____________________________________________________________
                           _      _      
  __ _  _   _   __ _  ___| | __ (_)  ___ 
 / _` || | | | / _` |/ __| |/ / | | / _ \ 
| (_| || |_| || (_| | (__|   <  | ||  __/
 \__, | \__,_| \__,_|\___|_|\_\ |_|\___|
    |_|                                  
Hello! I'm Quackie.
What can I do for you?
____________________________________________________________
____________________________________________________________
 OOPS!!! An event needs a description, /from time, and /to time.
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
  ```
## Test Case 18: Save tasks before restart

* Aim: Verify that adding tasks writes their descriptions, types, and statuses to the data file.
Program command: ```rm -f data/quackie.txt; rmdir data 2>/dev/null || true; java -cp /tmp/ui-test-classes quackie.Quackie```
* Inputs:
  ```text
  todo read book
  deadline return book /by Sunday
  mark 2
  bye
  ```
* Expected output:
  ```text
____________________________________________________________
                           _      _      
  __ _  _   _   __ _  ___| | __ (_)  ___ 
 / _` || | | | / _` |/ __| |/ / | | / _ \ 
| (_| || |_| || (_| | (__|   <  | ||  __/
 \__, | \__,_| \__,_|\___|_|\_\ |_|\___|
    |_|                                  
Hello! I'm Quackie.
What can I do for you?
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] read book
 Now you have 1 task in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [D][ ] return book (by: Sunday)
 Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 Nice! I've marked this task as done:
   [D][X] return book (by: Sunday)
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
  ```

## Test Case 19: Load tasks after restart

* Aim: Verify that tasks saved by a previous session are loaded when Quackie starts.
Program command: ```java -cp /tmp/ui-test-classes quackie.Quackie```
* Inputs:
  ```text
  list
  bye
  ```
* Expected output:
  ```text
____________________________________________________________
                           _      _      
  __ _  _   _   __ _  ___| | __ (_)  ___ 
 / _` || | | | / _` |/ __| |/ / | | / _ \ 
| (_| || |_| || (_| | (__|   <  | ||  __/
 \__, | \__,_| \__,_|\___|_|\_\ |_|\___|
    |_|                                  
Hello! I'm Quackie.
What can I do for you?
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] read book
 2.[D][X] return book (by: Sunday)
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
  ```

## Test Case 20: Start without a data file

* Aim: Verify that Quackie starts with an empty list when no data file exists.
Program command: ```rm -f data/quackie.txt && java -cp /tmp/ui-test-classes quackie.Quackie```
* Inputs:
  ```text
  list
  bye
  ```
* Expected output:
  ```text
____________________________________________________________
                           _      _      
  __ _  _   _   __ _  ___| | __ (_)  ___ 
 / _` || | | | / _` |/ __| |/ / | | / _ \ 
| (_| || |_| || (_| | (__|   <  | ||  __/
 \__, | \__,_| \__,_|\___|_|\_\ |_|\___|
    |_|                                  
Hello! I'm Quackie.
What can I do for you?
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
  ```

## Test Case 21: Handle a malformed data file

* Aim: Verify that malformed saved data is reported and does not prevent a fresh session.
Program command: ```rm -f data/quackie.txt && mkdir -p data && printf 'corrupt-record\n' > data/quackie.txt && java -cp /tmp/ui-test-classes quackie.Quackie```
* Inputs:
  ```text
  list
  bye
  ```
* Expected output:
  ```text
____________________________________________________________
                           _      _      
  __ _  _   _   __ _  ___| | __ (_)  ___ 
 / _` || | | | / _` |/ __| |/ / | | / _ \ 
| (_| || |_| || (_| | (__|   <  | ||  __/
 \__, | \__,_| \__,_|\___|_|\_\ |_|\___|
    |_|                                  
Hello! I'm Quackie.
What can I do for you?
____________________________________________________________
 OOPS!!! I couldn't load saved tasks. Starting with an empty list.
____________________________________________________________
 Here are the tasks in your list:
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
  ```

## Test Case 22: Format an ISO deadline date

- Aim: Verify that an ISO deadline date is stored as a date and displayed in a human-readable format.
- Inputs:
  ```text
  deadline return book /by 2019-10-15
  list
  bye
  ```
- Expected output:
  ```text
____________________________________________________________
                           _      _      
  __ _  _   _   __ _  ___| | __ (_)  ___ 
 / _` || | | | / _` |/ __| |/ / | | / _ \ 
| (_| || |_| || (_| | (__|   <  | ||  __/
 \__, | \__,_| \__,_|\___|_|\_\ |_|\___|
    |_|                                  
Hello! I'm Quackie.
What can I do for you?
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [D][ ] return book (by: Oct 15 2019)
 Now you have 1 task in the list.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[D][ ] return book (by: Oct 15 2019)
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
  ```

## Test Case 23: Format a deadline date and time

- Aim: Verify that a day-month-year deadline with a 24-hour time is stored as a date and time and displayed clearly.
- Inputs:
  ```text
  deadline return book /by 2/12/2019 1800
  list
  bye
  ```
- Expected output:
  ```text
____________________________________________________________
                           _      _      
  __ _  _   _   __ _  ___| | __ (_)  ___ 
 / _` || | | | / _` |/ __| |/ / | | / _ \ 
| (_| || |_| || (_| | (__|   <  | ||  __/
 \__, | \__,_| \__,_|\___|_|\_\ |_|\___|
    |_|                                  
Hello! I'm Quackie.
What can I do for you?
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [D][ ] return book (by: Dec 02 2019, 6:00 PM)
 Now you have 1 task in the list.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[D][ ] return book (by: Dec 02 2019, 6:00 PM)
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
  ```

## Test Case 24: Reject an invalid structured deadline

- Aim: Verify that an invalid date is rejected without adding a deadline task.
- Inputs:
  ```text
  deadline return book /by 2019-02-30
  list
  bye
  ```
- Expected output:
  ```text
____________________________________________________________
                           _      _      
  __ _  _   _   __ _  ___| | __ (_)  ___ 
 / _` || | | | / _` |/ __| |/ / | | / _ \ 
| (_| || |_| || (_| | (__|   <  | ||  __/
 \__, | \__,_| \__,_|\___|_|\_\ |_|\___|
    |_|                                  
Hello! I'm Quackie.
What can I do for you?
____________________________________________________________
____________________________________________________________
 OOPS!!! Please enter a valid deadline date (yyyy-MM-dd) or date and time (d/M/yyyy HHmm).
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
  ```

## Test Case 25: Save a structured deadline before restart

* Aim: Verify that a structured deadline is written to local storage without losing its original date value.
Program command: ```rm -f data/quackie.txt; rmdir data 2>/dev/null || true; java -cp /tmp/ui-test-classes quackie.Quackie```
* Inputs:
  ```text
  deadline submit report /by 2019-10-15
  bye
  ```
* Expected output:
  ```text
____________________________________________________________
                           _      _      
  __ _  _   _   __ _  ___| | __ (_)  ___ 
 / _` || | | | / _` |/ __| |/ / | | / _ \ 
| (_| || |_| || (_| | (__|   <  | ||  __/
 \__, | \__,_| \__,_|\___|_|\_\ |_|\___|
    |_|                                  
Hello! I'm Quackie.
What can I do for you?
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [D][ ] submit report (by: Oct 15 2019)
 Now you have 1 task in the list.
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
  ```

## Test Case 26: Load a structured deadline after restart

* Aim: Verify that a saved structured deadline is reconstructed with its human-readable date display.
Program command: `java -cp /tmp/ui-test-classes quackie.Quackie`
* Inputs:
  ```text
  list
  bye
  ```
* Expected output:
  ```text
____________________________________________________________
                           _      _      
  __ _  _   _   __ _  ___| | __ (_)  ___ 
 / _` || | | | / _` |/ __| |/ / | | / _ \ 
| (_| || |_| || (_| | (__|   <  | ||  __/
 \__, | \__,_| \__,_|\___|_|\_\ |_|\___|
    |_|                                  
Hello! I'm Quackie.
What can I do for you?
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[D][ ] submit report (by: Oct 15 2019)
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
  ```

## Test Case 27: Save a structured date-time before restart

* Aim: Verify that a structured deadline date and time are written to local storage without losing the original value.
Program command: ```rm -f data/quackie.txt; rmdir data 2>/dev/null || true; java -cp /tmp/ui-test-classes quackie.Quackie```
* Inputs:
  ```text
  deadline attend meeting /by 2/12/2019 1800
  bye
  ```
* Expected output:
  ```text
____________________________________________________________
                           _      _      
  __ _  _   _   __ _  ___| | __ (_)  ___ 
 / _` || | | | / _` |/ __| |/ / | | / _ \ 
| (_| || |_| || (_| | (__|   <  | ||  __/
 \__, | \__,_| \__,_|\___|_|\_\ |_|\___|
    |_|                                  
Hello! I'm Quackie.
What can I do for you?
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [D][ ] attend meeting (by: Dec 02 2019, 6:00 PM)
 Now you have 1 task in the list.
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
  ```

## Test Case 28: Load a structured date-time after restart

* Aim: Verify that a saved structured deadline date and time are reconstructed with their human-readable display.
Program command: `java -cp /tmp/ui-test-classes quackie.Quackie`
* Inputs:
  ```text
  list
  bye
  ```
* Expected output:
  ```text
____________________________________________________________
                           _      _      
  __ _  _   _   __ _  ___| | __ (_)  ___ 
 / _` || | | | / _` |/ __| |/ / | | / _ \ 
| (_| || |_| || (_| | (__|   <  | ||  __/
 \__, | \__,_| \__,_|\___|_|\_\ |_|\___|
    |_|                                  
Hello! I'm Quackie.
What can I do for you?
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[D][ ] attend meeting (by: Dec 02 2019, 6:00 PM)
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
  ```

## Test Case 29: Reject an invalid structured time

* Aim: Verify that an invalid 24-hour time is rejected without adding a deadline task.
* Inputs:
  ```text
  deadline return book /by 2/12/2019 2500
  list
  bye
  ```
* Expected output:
  ```text
____________________________________________________________
                           _      _      
  __ _  _   _   __ _  ___| | __ (_)  ___ 
 / _` || | | | / _` |/ __| |/ / | | / _ \ 
| (_| || |_| || (_| | (__|   <  | ||  __/
 \__, | \__,_| \__,_|\___|_|\_\ |_|\___|
    |_|                                  
Hello! I'm Quackie.
What can I do for you?
____________________________________________________________
____________________________________________________________
 OOPS!!! Please enter a valid deadline date (yyyy-MM-dd) or date and time (d/M/yyyy HHmm).
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
  ```

## Test Case 30: Find multiple matching tasks

* Aim: Verify that `find` returns every task whose description contains the keyword while preserving task order.
* Inputs:
  ```text
  todo read book
  deadline return book /by Sunday
  event project meeting /from Mon 2pm /to 4pm
  find book
  bye
  ```
* Expected output:
  ```text
____________________________________________________________
                           _      _      
  __ _  _   _   __ _  ___| | __ (_)  ___ 
 / _` || | | | / _` |/ __| |/ / | | / _ \ 
| (_| || |_| || (_| | (__|   <  | ||  __/
 \__, | \__,_| \__,_|\___|_|\_\ |_|\___|
    |_|                                  
Hello! I'm Quackie.
What can I do for you?
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] read book
 Now you have 1 task in the list.
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
 Here are the matching tasks in your list:
 1.[T][ ] read book
 2.[D][ ] return book (by: Sunday)
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
  ```

## Test Case 31: Find tasks without case sensitivity

* Aim: Verify that `find` matches task descriptions regardless of letter case.
* Inputs:
  ```text
  todo Read Book
  find read
  bye
  ```
* Expected output:
  ```text
____________________________________________________________
                           _      _      
  __ _  _   _   __ _  ___| | __ (_)  ___ 
 / _` || | | | / _` |/ __| |/ / | | / _ \ 
| (_| || |_| || (_| | (__|   <  | ||  __/
 \__, | \__,_| \__,_|\___|_|\_\ |_|\___|
    |_|                                  
Hello! I'm Quackie.
What can I do for you?
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] Read Book
 Now you have 1 task in the list.
____________________________________________________________
____________________________________________________________
 Here are the matching tasks in your list:
 1.[T][ ] Read Book
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
  ```

## Test Case 32: Report no matching tasks

* Aim: Verify that `find` displays an empty matching-task list when no descriptions contain the keyword.
* Inputs:
  ```text
  todo read book
  find groceries
  bye
  ```
* Expected output:
  ```text
____________________________________________________________
                           _      _      
  __ _  _   _   __ _  ___| | __ (_)  ___ 
 / _` || | | | / _` |/ __| |/ / | | / _ \ 
| (_| || |_| || (_| | (__|   <  | ||  __/
 \__, | \__,_| \__,_|\___|_|\_\ |_|\___|
    |_|                                  
Hello! I'm Quackie.
What can I do for you?
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] read book
 Now you have 1 task in the list.
____________________________________________________________
____________________________________________________________
 Here are the matching tasks in your list:
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
  ```

## Test Case 33: Reject an empty find keyword

* Aim: Verify that `find` without a keyword is rejected without changing the task list.
* Inputs:
  ```text
  find
  list
  bye
  ```
* Expected output:
  ```text
____________________________________________________________
                           _      _      
  __ _  _   _   __ _  ___| | __ (_)  ___ 
 / _` || | | | / _` |/ __| |/ / | | / _ \ 
| (_| || |_| || (_| | (__|   <  | ||  __/
 \__, | \__,_| \__,_|\___|_|\_\ |_|\___|
    |_|                                  
Hello! I'm Quackie.
What can I do for you?
____________________________________________________________
____________________________________________________________
 OOPS!!! Please provide a keyword to find.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
  ```

## Test Case 34: Update a completed task and preserve its state

* Aim: Verify that `update` replaces a task in place, can change its type, and preserves its done status.
* Inputs:
  ```text
  todo read book
  mark 1
  update 1 deadline read novel /by Friday
  list
  bye
  ```
* Expected output:
  ```text
____________________________________________________________
                           _      _      
  __ _  _   _   __ _  ___| | __ (_)  ___ 
 / _` || | | | / _` |/ __| |/ / | | / _ \ 
| (_| || |_| || (_| | (__|   <  | ||  __/
 \__, | \__,_| \__,_|\___|_|\_\ |_|\___|
    |_|                                  
Hello! I'm Quackie.
What can I do for you?
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] read book
 Now you have 1 task in the list.
____________________________________________________________
____________________________________________________________
 Nice! I've marked this task as done:
   [T][X] read book
____________________________________________________________
____________________________________________________________
 Updated this task:
   [D][X] read novel (by: Friday)
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[D][X] read novel (by: Friday)
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
  ```

## Test Case 35: Load an updated task after restart

* Aim: Verify that an updated task and its preserved done status are saved to disk.
Program command: `java -cp /tmp/ui-test-classes quackie.Quackie`
* Inputs:
  ```text
  list
  bye
  ```
* Expected output:
  ```text
____________________________________________________________
                           _      _      
  __ _  _   _   __ _  ___| | __ (_)  ___ 
 / _` || | | | / _` |/ __| |/ / | | / _ \ 
| (_| || |_| || (_| | (__|   <  | ||  __/
 \__, | \__,_| \__,_|\___|_|\_\ |_|\___|
    |_|                                  
Hello! I'm Quackie.
What can I do for you?
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[D][X] read novel (by: Friday)
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
  ```

## Test Case 36: Reject invalid update commands

* Aim: Verify that invalid task numbers and replacement commands are rejected without changing the original task.
* Inputs:
  ```text
  todo original task
  update
  update nope todo replacement
  update 0 todo replacement
  update 2 todo replacement
  update 1 list
  update 1 todo
  list
  bye
  ```
* Expected output:
  ```text
____________________________________________________________
                           _      _      
  __ _  _   _   __ _  ___| | __ (_)  ___ 
 / _` || | | | / _` |/ __| |/ / | | / _ \ 
| (_| || |_| || (_| | (__|   <  | ||  __/
 \__, | \__,_| \__,_|\___|_|\_\ |_|\___|
    |_|                                  
Hello! I'm Quackie.
What can I do for you?
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] original task
 Now you have 1 task in the list.
____________________________________________________________
____________________________________________________________
 OOPS!!! An update needs a task number and replacement todo, deadline, or event command.
____________________________________________________________
____________________________________________________________
 OOPS!!! Please provide a valid task number.
____________________________________________________________
____________________________________________________________
 OOPS!!! Please provide a valid task number.
____________________________________________________________
____________________________________________________________
 OOPS!!! Please provide a valid task number.
____________________________________________________________
____________________________________________________________
 OOPS!!! An update needs a replacement todo, deadline, or event command.
____________________________________________________________
____________________________________________________________
 OOPS!!! A ToDo needs a description.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] original task
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
  ```

## Test Case 37: Display structured event times in a friendly format

- Aim: Verify that an event with structured `d/M/yyyy HHmm` times is displayed in the same friendly date-time style as deadlines.
- Inputs:
  ```text
  event project meeting /from 24/9/2026 1400 /to 24/9/2026 1600
  list
  bye
  ```
- Expected output:
  ```text
____________________________________________________________
                           _      _      
  __ _  _   _   __ _  ___| | __ (_)  ___ 
 / _` || | | | / _` |/ __| |/ / | | / _ \ 
| (_| || |_| || (_| | (__|   <  | ||  __/
 \__, | \__,_| \__,_|\___|_|\_\ |_|\___|
    |_|                                  
Hello! I'm Quackie.
What can I do for you?
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [E][ ] project meeting (from: Sep 24 2026, 2:00 PM to: Sep 24 2026, 4:00 PM)
 Now you have 1 task in the list.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[E][ ] project meeting (from: Sep 24 2026, 2:00 PM to: Sep 24 2026, 4:00 PM)
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
  ```
