# UI Test Plan

This plan defines console tests for the Lumine application. Commands are run from the repository root with Java 25.

## Manual GUI regression cases

These cases require the JavaFX window and are not run by the console test runner.

### Manual GUI case: Display farewell before closing
Aim: Verify that `bye` displays the farewell, disables both input controls, and closes after a short delay.

Setup: Launch the GUI with Java 25 using `gradlew.bat run`.

Inputs:
1. Enter `bye` and submit it with the SEND button.
2. During the delay, attempt to type another command or click SEND.
3. Relaunch and repeat using Enter to submit `bye`.

Expected output and behavior:
- The conversation displays `bye` and `Bye. Hope to see you again soon!`, scrolling to the farewell.
- The input field and SEND button are disabled immediately after submission; further commands cannot be submitted.
- The window stays responsive with the farewell visible, then closes automatically after approximately one second.
- Both SEND and Enter produce the same exit behavior.

## Console regression cases

### Test Case: Compile application with Java 25
Aim: Verify that all current Java sources compile successfully using the required Java 25 compiler.

Command:
```text
gradlew.bat --console plain classes > NUL && echo BUILD_OK
```

Input:
```text
<empty>
```

Expected output:
```text
BUILD_OK
```

### Test Case: Application greeting, empty list, and exit
Aim: Verify the greeting, the empty-list message from `list`, and the farewell when the user enters `bye`.

Command:
```text
del /q data\lumine.txt 2>NUL & java -cp build\classes\java\main lumine.Lumine
```

Input:
```text
list
bye
```

Expected output:
```text
____________________________________________________________
 ___      __   __  __   __  ___   __    _  _______ 
|   |    |  | |  ||  |_|  ||   | |  |  | ||       |
|   |    |  | |  ||       ||   | |   |_| ||    ___|
|   |    |  |_|  ||       ||   | |       ||   |___ 
|   |___ |       ||       ||   | |  _    ||    ___|
|       ||       || ||_|| ||   | | | |   ||   |___ 
|_______||_______||_|   |_||___| |_|  |__||_______|
Hello, I'm Lumine!
What can I do for you today?
____________________________________________________________
____________________________________________________________
Nice, there are no task on your list!
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

### Test Case: Delete a task and renumber the remaining tasks
Aim: Verify that `delete <number>` removes the selected task, reports the removed task and new count, and renumbers the remaining tasks when listed.

Command:
```text
del /q data\lumine.txt 2>NUL & java -cp build\classes\java\main lumine.Lumine
```

Input:
```text
todo first task
todo second task
todo third task
list
delete 2
list
bye
```

Expected output:
```text
____________________________________________________________
 ___      __   __  __   __  ___   __    _  _______ 
|   |    |  | |  ||  |_|  ||   | |  |  | ||       |
|   |    |  | |  ||       ||   | |   |_| ||    ___|
|   |    |  |_|  ||       ||   | |       ||   |___ 
|   |___ |       ||       ||   | |  _    ||    ___|
|       ||       || ||_|| ||   | | | |   ||   |___ 
|_______||_______||_|   |_||___| |_|  |__||_______|
Hello, I'm Lumine!
What can I do for you today?
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] first task
Now, you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] second task
Now, you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] third task
Now, you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] first task
2.[T][ ] second task
3.[T][ ] third task
____________________________________________________________
____________________________________________________________
Noted. I've removed this task:
[T][ ] second task
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] first task
2.[T][ ] third task
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

### Test Case: Handle empty todo and unknown command
Aim: Verify that an empty todo and an unrecognised command produce the required error messages and that the application continues running.

Command:
```text
del /q data\lumine.txt 2>NUL & java -cp build\classes\java\main lumine.Lumine
```

Input:
```text
todo
blah
bye
```

Expected output:
```text
____________________________________________________________
 ___      __   __  __   __  ___   __    _  _______ 
|   |    |  | |  ||  |_|  ||   | |  |  | ||       |
|   |    |  | |  ||       ||   | |   |_| ||    ___|
|   |    |  |_|  ||       ||   | |       ||   |___ 
|   |___ |       ||       ||   | |  _    ||    ___|
|       ||       || ||_|| ||   | | | |   ||   |___ 
|_______||_______||_|   |_||___| |_|  |__||_______|
Hello, I'm Lumine!
What can I do for you today?
____________________________________________________________
____________________________________________________________
Sorry, todo task cannot be empty. :C
____________________________________________________________
____________________________________________________________
Hmmmm, I can't understand what that means. ;-;
Try entering a command instead.
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

### Test Case: Handle empty deadline and event
Aim: Verify that deadline and event commands without the required details produce helpful error messages and do not terminate the application.

Command:
```text
del /q data\lumine.txt 2>NUL & java -cp build\classes\java\main lumine.Lumine
```

Input:
```text
deadline
event
bye
```

Expected output:
```text
____________________________________________________________
 ___      __   __  __   __  ___   __    _  _______ 
|   |    |  | |  ||  |_|  ||   | |  |  | ||       |
|   |    |  | |  ||       ||   | |   |_| ||    ___|
|   |    |  |_|  ||       ||   | |       ||   |___ 
|   |___ |       ||       ||   | |  _    ||    ___|
|       ||       || ||_|| ||   | | | |   ||   |___ 
|_______||_______||_|   |_||___| |_|  |__||_______|
Hello, I'm Lumine!
What can I do for you today?
____________________________________________________________
____________________________________________________________
Sorry, I can't read your deadline task. :C
It needs a description and a /by time.
e.g. deadline test /by Mon 2pm
____________________________________________________________
____________________________________________________________
Sorry, I can't read your event task. :C
It needs a description, /from time, and /to time.
e.g. event test /from Mon 2pm /to 4pm
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

### Test Case: Handle invalid task number
Aim: Verify that marking a task outside the current list shows a helpful message and the application continues running.

Command:
```text
del /q data\lumine.txt 2>NUL & java -cp build\classes\java\main lumine.Lumine
```

Input:
```text
mark 1
bye
```

Expected output:
```text
____________________________________________________________
 ___      __   __  __   __  ___   __    _  _______ 
|   |    |  | |  ||  |_|  ||   | |  |  | ||       |
|   |    |  | |  ||       ||   | |   |_| ||    ___|
|   |    |  |_|  ||       ||   | |       ||   |___ 
|   |___ |       ||       ||   | |  _    ||    ___|
|       ||       || ||_|| ||   | | | |   ||   |___ 
|_______||_______||_|   |_||___| |_|  |__||_______|
Hello, I'm Lumine!
What can I do for you today?
____________________________________________________________
____________________________________________________________
Task not found :<.
Please enter a valid task number.
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

### Test Case: Empty list, blank command, and invalid task numbers
Aim: Verify that an empty list can be displayed, a blank command is rejected, and invalid mark, unmark, and delete inputs do not modify existing tasks.

Command:
```text
del /q data\lumine.txt 2>NUL & java -cp build\classes\java\main lumine.Lumine
```

Input:
```text
list

todo keep task
mark
unmark abc
delete 0
delete 2
list
bye
```

Expected output:
```text
____________________________________________________________
 ___      __   __  __   __  ___   __    _  _______ 
|   |    |  | |  ||  |_|  ||   | |  |  | ||       |
|   |    |  | |  ||       ||   | |   |_| ||    ___|
|   |    |  |_|  ||       ||   | |       ||   |___ 
|   |___ |       ||       ||   | |  _    ||    ___|
|       ||       || ||_|| ||   | | | |   ||   |___ 
|_______||_______||_|   |_||___| |_|  |__||_______|
Hello, I'm Lumine!
What can I do for you today?
____________________________________________________________
____________________________________________________________
Nice, there are no task on your list!
____________________________________________________________
____________________________________________________________
Hmmmm, I can't understand what that means. ;-;
Try entering a command instead.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] keep task
Now, you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Task not found :<.
Please enter a valid task number.
____________________________________________________________
____________________________________________________________
Task not found :<.
Please enter a valid task number.
____________________________________________________________
____________________________________________________________
Task not found :<.
Please enter a valid task number.
____________________________________________________________
____________________________________________________________
Task not found :<.
Please enter a valid task number.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] keep task
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

### Test Case: Malformed deadline and event commands preserve existing tasks
Aim: Verify that deadline and event commands with missing descriptions, invalid ranges, mixed date types, or missing times show errors and do not alter tasks that were already added.

Command:
```text
del /q data\lumine.txt 2>NUL & java -cp build\classes\java\main lumine.Lumine
```

Input:
```text
todo keep task
deadline prepare slides
deadline /by Friday
deadline prepare slides /by
event meeting /from 2pm
event meeting /to 4pm
event /from 2pm /to 4pm
deadline submit /by 2026 02 30
event meeting /from 2026 02 30 1400 /to 2026 03 01 1600
event meeting /from 2026 02 28 1400 /to 2026 02 30 1600
event meeting /from 2026 09 20 1600 /to 2026 09 20 1400
event meeting /from 2026 09 20 1400 /to 2026 09 20 1400
event meeting /from 2026 09 21 1400 /to 2026 09 20 1600
event meeting /from 2026 09 20 1400 /to 4pm
event meeting /from 2pm /to 2026 09 20 1600
event meeting /from 2026 09 20 /to 2026 09 21 1600
event meeting /from 2026 09 20 1400 /to 2026 09 21
list
bye
```

Expected output:
```text
____________________________________________________________
 ___      __   __  __   __  ___   __    _  _______ 
|   |    |  | |  ||  |_|  ||   | |  |  | ||       |
|   |    |  | |  ||       ||   | |   |_| ||    ___|
|   |    |  |_|  ||       ||   | |       ||   |___ 
|   |___ |       ||       ||   | |  _    ||    ___|
|       ||       || ||_|| ||   | | | |   ||   |___ 
|_______||_______||_|   |_||___| |_|  |__||_______|
Hello, I'm Lumine!
What can I do for you today?
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] keep task
Now, you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Sorry, I can't read your deadline task. :C
It needs a description and a /by time.
e.g. deadline test /by Mon 2pm
____________________________________________________________
____________________________________________________________
Sorry, I can't read your deadline task. :C
It needs a description and a /by time.
e.g. deadline test /by Mon 2pm
____________________________________________________________
____________________________________________________________
Sorry, I can't read your deadline task. :C
It needs a description and a /by time.
e.g. deadline test /by Mon 2pm
____________________________________________________________
____________________________________________________________
Sorry, I can't read your event task. :C
It needs a description, /from time, and /to time.
e.g. event test /from Mon 2pm /to 4pm
____________________________________________________________
____________________________________________________________
Sorry, I can't read your event task. :C
It needs a description, /from time, and /to time.
e.g. event test /from Mon 2pm /to 4pm
____________________________________________________________
____________________________________________________________
Sorry, I can't read your event task. :C
It needs a description, /from time, and /to time.
e.g. event test /from Mon 2pm /to 4pm
____________________________________________________________
____________________________________________________________
Date not found :<.
Please enter a valid calendar date or time.
____________________________________________________________
____________________________________________________________
Date not found :<.
Please enter a valid calendar date or time.
____________________________________________________________
____________________________________________________________
Date not found :<.
Please enter a valid calendar date or time.
____________________________________________________________
____________________________________________________________
Hmmmm, the event end date and start date isn't valid, try again with a valid range instead
____________________________________________________________
____________________________________________________________
Hmmmm, the event end date and start date isn't valid, try again with a valid range instead
____________________________________________________________
____________________________________________________________
Hmmmm, the event end date and start date isn't valid, try again with a valid range instead
____________________________________________________________
____________________________________________________________
Sorry, you need to enter date in format yyyy MM dd HHmm for both from and to date.
____________________________________________________________
____________________________________________________________
Sorry, you need to enter date in format yyyy MM dd HHmm for both from and to date.
____________________________________________________________
____________________________________________________________
Sorry, you need to enter date in format yyyy MM dd HHmm for both from and to date.
____________________________________________________________
____________________________________________________________
Sorry, you need to enter date in format yyyy MM dd HHmm for both from and to date.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] keep task
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

### Test Case: Create, list, mark, and unmark task types
Aim: Verify that todo, deadline, and event commands create the correct task subtypes, that date filtering shows pending tasks and the no-tasks-due message, and that marking and unmarking preserve each task's subtype information.

Command:
```text
del /q data\lumine.txt 2>NUL & java -cp build\classes\java\main lumine.Lumine
```

Input:
```text
todo borrow book
deadline return book /by 2019 10 15
event project meeting /from 2019 10 14 1400 /to 2019 10 15 1600
list
date 2019 10 15
date 2019 10 16
mark 2
list
unmark 2
list
bye
```

Expected output:
```text
____________________________________________________________
 ___      __   __  __   __  ___   __    _  _______ 
|   |    |  | |  ||  |_|  ||   | |  |  | ||       |
|   |    |  | |  ||       ||   | |   |_| ||    ___|
|   |    |  |_|  ||       ||   | |       ||   |___ 
|   |___ |       ||       ||   | |  _    ||    ___|
|       ||       || ||_|| ||   | | | |   ||   |___ 
|_______||_______||_|   |_||___| |_|  |__||_______|
Hello, I'm Lumine!
What can I do for you today?
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] borrow book
Now, you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [D][ ] return book (by: Oct 15 2019)
Now, you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [E][ ] project meeting (from: Oct 14 2019 14:00 to: Oct 15 2019 16:00)
Now, you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] borrow book
2.[D][ ] return book (by: Oct 15 2019)
3.[E][ ] project meeting (from: Oct 14 2019 14:00 to: Oct 15 2019 16:00)
____________________________________________________________
____________________________________________________________
Here is your list of pending task due on 2019 10 15:
1.[D][ ] return book (by: Oct 15 2019)
2.[E][ ] project meeting (from: Oct 14 2019 14:00 to: Oct 15 2019 16:00)
____________________________________________________________
____________________________________________________________
Your path is clear on 2019 10 16 — no tasks due.
____________________________________________________________
____________________________________________________________
A step forward! Marked done:
[D][X] return book (by: Oct 15 2019)
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] borrow book
2.[D][X] return book (by: Oct 15 2019)
3.[E][ ] project meeting (from: Oct 14 2019 14:00 to: Oct 15 2019 16:00)
____________________________________________________________
____________________________________________________________
OK, I've marked this task as not done yet:
[D][ ] return book (by: Oct 15 2019)
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] borrow book
2.[D][ ] return book (by: Oct 15 2019)
3.[E][ ] project meeting (from: Oct 14 2019 14:00 to: Oct 15 2019 16:00)
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

### Test Case: Save task changes to disk
Aim: Verify that adding, marking, unmarking, and deleting tasks updates `data\\lumine.txt` with the current task list.

Command:
```text
del /q data\lumine.txt 2>NUL & java -cp build\classes\java\main lumine.Lumine > NUL & type data\lumine.txt & del /q data\lumine.txt
```

Input:
```text
todo write report
deadline submit report /by 2019 10 15 2359
event team meeting /from 2pm /to 3pm
mark 2
unmark 2
delete 1
bye
```

Expected output:
```text
D | 0 | submit report | 2019 10 15 2359
E | 0 | team meeting | 2pm | 3pm
```

### Test Case: Load saved tasks on startup
Aim: Verify that saved todo, deadline, and event tasks are loaded when the application starts, including their completion state.

Command:
```text
powershell -NoProfile -Command "[System.IO.Directory]::CreateDirectory('data') | Out-Null; [System.IO.File]::WriteAllLines('data\lumine.txt', @('T | 1 | recovered todo','D | 0 | recovered deadline | Friday','E | 1 | recovered event | 2pm | 3pm'))" & java -cp build\classes\java\main lumine.Lumine & del /q data\lumine.txt
```

Input:
```text
list
bye
```

Expected output:
```text
____________________________________________________________
 ___      __   __  __   __  ___   __    _  _______ 
|   |    |  | |  ||  |_|  ||   | |  |  | ||       |
|   |    |  | |  ||       ||   | |   |_| ||    ___|
|   |    |  |_|  ||       ||   | |       ||   |___ 
|   |___ |       ||       ||   | |  _    ||    ___|
|       ||       || ||_|| ||   | | | |   ||   |___ 
|_______||_______||_|   |_||___| |_|  |__||_______|
Hello, I'm Lumine!
What can I do for you today?
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][X] recovered todo
2.[D][ ] recovered deadline (by: Friday)
3.[E][X] recovered event (from: 2pm to: 3pm)
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

### Test Case: Recover from a malformed save file
Aim: Verify that an invalid saved record reports its line number, starts with an empty list, and keeps the chatbot running.

Command:
```text
powershell -NoProfile -Command "[System.IO.Directory]::CreateDirectory('data') | Out-Null; [System.IO.File]::WriteAllText('data\lumine.txt', 'X | 0 | invalid task')" & java -cp build\classes\java\main lumine.Lumine & del /q data\lumine.txt
```

Input:
```text
list
bye
```

Expected output:
```text
____________________________________________________________
 ___      __   __  __   __  ___   __    _  _______ 
|   |    |  | |  ||  |_|  ||   | |  |  | ||       |
|   |    |  | |  ||       ||   | |   |_| ||    ___|
|   |    |  |_|  ||       ||   | |       ||   |___ 
|   |___ |       ||       ||   | |  _    ||    ___|
|       ||       || ||_|| ||   | | | |   ||   |___ 
|_______||_______||_|   |_||___| |_|  |__||_______|
Hello, I'm Lumine!
What can I do for you today?
____________________________________________________________
____________________________________________________________
Sorry, I couldn't load your tasks. :C
Invalid saved task on line 1.
____________________________________________________________
____________________________________________________________
Nice, there are no task on your list!
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

### Test Case: Report the saved record line for an invalid date
Aim: Verify that an impossible saved deadline date reports its physical line number, including blank lines, and starts with an empty list.

Command:
```text
powershell -NoProfile -Command "[System.IO.Directory]::CreateDirectory('data') | Out-Null; [System.IO.File]::WriteAllLines('data\lumine.txt', @('T | 0 | valid', '', 'D | 0 | deadline | 2026 02 30'))" & java -cp build\classes\java\main lumine.Lumine & del /q data\lumine.txt
```

Input:
```text
list
bye
```

Expected output:
```text
____________________________________________________________
 ___      __   __  __   __  ___   __    _  _______ 
|   |    |  | |  ||  |_|  ||   | |  |  | ||       |
|   |    |  | |  ||       ||   | |   |_| ||    ___|
|   |    |  |_|  ||       ||   | |       ||   |___ 
|   |___ |       ||       ||   | |  _    ||    ___|
|       ||       || ||_|| ||   | | | |   ||   |___ 
|_______||_______||_|   |_||___| |_|  |__||_______|
Hello, I'm Lumine!
What can I do for you today?
____________________________________________________________
____________________________________________________________
Sorry, I couldn't load your tasks. :C
Invalid saved task on line 3.
____________________________________________________________
____________________________________________________________
Nice, there are no task on your list!
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

### Test Case: Preserve special characters in saved tasks
Aim: Verify that pipes and backslashes in task descriptions are escaped when saved instead of corrupting the task format.

Command:
```text
del /q data\lumine.txt 2>NUL & java -cp build\classes\java\main lumine.Lumine > NUL & type data\lumine.txt & del /q data\lumine.txt
```

Input:
```text
todo pipe | slash \
bye
```

Expected output:
```text
T | 0 | pipe \| slash \\
```

### Test Case: Handle save destination errors
Aim: Verify that an unwritable save destination reports an error, rolls back the attempted addition, and keeps the chatbot running.

Command:
```text
powershell -NoProfile -Command "[System.IO.Directory]::CreateDirectory('data\\lumine.txt') | Out-Null" & java -cp build\classes\java\main lumine.Lumine & powershell -NoProfile -Command "[System.IO.Directory]::Delete('data\\lumine.txt')"
```

Input:
```text
todo cannot save
list
bye
```

Expected output:
```text
____________________________________________________________
 ___      __   __  __   __  ___   __    _  _______ 
|   |    |  | |  ||  |_|  ||   | |  |  | ||       |
|   |    |  | |  ||       ||   | |   |_| ||    ___|
|   |    |  |_|  ||       ||   | |       ||   |___ 
|   |___ |       ||       ||   | |  _    ||    ___|
|       ||       || ||_|| ||   | | | |   ||   |___ 
|_______||_______||_|   |_||___| |_|  |__||_______|
Hello, I'm Lumine!
What can I do for you today?
____________________________________________________________
____________________________________________________________
Sorry, I couldn't load your tasks. :C
____________________________________________________________
____________________________________________________________
Sorry, I couldn't save your tasks. :C
____________________________________________________________
____________________________________________________________
Nice, there are no task on your list!
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

### Test Case: Find matching tasks
Aim: Verify that the find command returns tasks containing the given keyword and reports an error on empty keywords.

Command:
```text
del /q data\lumine.txt 2>NUL & java -cp build\classes\java\main lumine.Lumine
```

Input:
```text
todo return book
todo read book
todo borrow notes
find book
find notes
find magazine
find
bye
```

Expected output:
```text
____________________________________________________________
 ___      __   __  __   __  ___   __    _  _______ 
|   |    |  | |  ||  |_|  ||   | |  |  | ||       |
|   |    |  | |  ||       ||   | |   |_| ||    ___|
|   |    |  |_|  ||       ||   | |       ||   |___ 
|   |___ |       ||       ||   | |  _    ||    ___|
|       ||       || ||_|| ||   | | | |   ||   |___ 
|_______||_______||_|   |_||___| |_|  |__||_______|
Hello, I'm Lumine!
What can I do for you today?
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] return book
Now, you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] read book
Now, you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] borrow notes
Now, you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
Here is the list of matching tasks:
1.[T][ ] return book
2.[T][ ] read book
____________________________________________________________
____________________________________________________________
Here is the list of matching tasks:
3.[T][ ] borrow notes
____________________________________________________________
____________________________________________________________
I couldn't find a task containing 'magazine'.
____________________________________________________________
____________________________________________________________
Sorry, the search keyword cannot be empty. :C
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

### Test Case: Undo the most recent task-list change
Aim: Verify that `undo` names and reverses the most recent successful editing command, ignores read-only commands and failed edits, persists the reverted list, and reports when no further change can be undone.

Command:
```text
del /q data\lumine.txt 2>NUL & java -cp build\classes\java\main lumine.Lumine
```

Input:
```text
todo temporary task
list
delete 99
undo
list
undo
bye
```

Expected output:
```text
____________________________________________________________
 ___      __   __  __   __  ___   __    _  _______ 
|   |    |  | |  ||  |_|  ||   | |  |  | ||       |
|   |    |  | |  ||       ||   | |   |_| ||    ___|
|   |    |  |_|  ||       ||   | |       ||   |___ 
|   |___ |       ||       ||   | |  _    ||    ___|
|       ||       || ||_|| ||   | | | |   ||   |___ 
|_______||_______||_|   |_||___| |_|  |__||_______|
Hello, I'm Lumine!
What can I do for you today?
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] temporary task
Now, you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] temporary task
____________________________________________________________
____________________________________________________________
Task not found :<.
Please enter a valid task number.
____________________________________________________________
____________________________________________________________
Done! I had undo the latest command. :D (todo temporary task)
____________________________________________________________
____________________________________________________________
Nice, there are no task on your list!
____________________________________________________________
____________________________________________________________
Sorry, there is no command to undo. :C
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```
