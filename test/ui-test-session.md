# UI test session — 2026-09-29

Java 25.0.4.1; assertions enabled; isolated temporary storage for every case.
Matching follows the ordered excerpts permitted by the plan. Full stdout and stderr are recorded below.

Result: 14 console cases PASS; case 10 manual GUI checks NOT RUN.


## 1: specific input errors

**Command:** `java -Dstdout.encoding=UTF-8 -Dstderr.encoding=UTF-8 -Duser.language=en -Duser.country=US -ea -cp C:\NUS\CS2103T\ip\build\classes\java\main xue.Xue`

**Input:**
```text
todo
blah
mark abc
mark
mark 99
todo buy milk
mark 1
unmark 1
list
bye
```

**Actual stdout:**
```text
________________________________________________________________________________
██   ██  ██   ██  ███████
 ██ ██   ██   ██  ██
  ███    ██   ██  █████
 ██ ██   ██   ██  ██
██   ██   █████   ███████
Hello, I'm Xue — your reluctantly competent task assistant.
Give me a command and I will pretend it was my idea.
________________________________________________________________________________
OOPS!!! The description of a todo cannot be empty. I cannot read your mind!
________________________________________________________________________________
________________________________________________________________________________
OOPS!!! I don't know what that means. Use a proper command next time.
________________________________________________________________________________
________________________________________________________________________________
OOPS!!! That is not a valid task number. Numbers are not that complicated.
________________________________________________________________________________
________________________________________________________________________________
OOPS!!! Tell me which task to mark. I am not a mind reader.
________________________________________________________________________________
________________________________________________________________________________
OOPS!!! That task number does not exist. Did you just invent it?
________________________________________________________________________________
________________________________________________________________________________
Got it. I've added this task: [T][ ] buy milk
Now you have 1 tasks in the list.
________________________________________________________________________________
________________________________________________________________________________
Fine, I've marked this task as done. Happy now?
  [X] buy milk
________________________________________________________________________________
________________________________________________________________________________
There. I've undone it. Try to make up your mind next time:
  [ ] buy milk
________________________________________________________________________________
________________________________________________________________________________
Here are your tasks. Yes, I did all the work for you:
1.[T][ ] buy milk
________________________________________________________________________________
________________________________________________________________________________
Finally, you're leaving. Bye. Don't make me miss you.
________________________________________________________________________________
```

**Actual stderr:**
```text
(empty)
```

**PASS**

## 2: invalid commands do not corrupt task state

**Command:** `java -Dstdout.encoding=UTF-8 -Dstderr.encoding=UTF-8 -Duser.language=en -Duser.country=US -ea -cp C:\NUS\CS2103T\ip\build\classes\java\main xue.Xue`

**Input:**
```text
todo finish report
todo
list
mark 0
list
mark 1
mark 1 extra
list
unmark 1
list
bye
```

**Actual stdout:**
```text
________________________________________________________________________________
██   ██  ██   ██  ███████
 ██ ██   ██   ██  ██
  ███    ██   ██  █████
 ██ ██   ██   ██  ██
██   ██   █████   ███████
Hello, I'm Xue — your reluctantly competent task assistant.
Give me a command and I will pretend it was my idea.
________________________________________________________________________________
Got it. I've added this task: [T][ ] finish report
Now you have 1 tasks in the list.
________________________________________________________________________________
________________________________________________________________________________
OOPS!!! The description of a todo cannot be empty. I cannot read your mind!
________________________________________________________________________________
________________________________________________________________________________
Here are your tasks. Yes, I did all the work for you:
1.[T][ ] finish report
________________________________________________________________________________
________________________________________________________________________________
OOPS!!! That task number does not exist. Did you just invent it?
________________________________________________________________________________
________________________________________________________________________________
Here are your tasks. Yes, I did all the work for you:
1.[T][ ] finish report
________________________________________________________________________________
________________________________________________________________________________
Fine, I've marked this task as done. Happy now?
  [X] finish report
________________________________________________________________________________
________________________________________________________________________________
OOPS!!! That is not a valid task number. Numbers are not that complicated.
________________________________________________________________________________
________________________________________________________________________________
Here are your tasks. Yes, I did all the work for you:
1.[T][X] finish report
________________________________________________________________________________
________________________________________________________________________________
There. I've undone it. Try to make up your mind next time:
  [ ] finish report
________________________________________________________________________________
________________________________________________________________________________
Here are your tasks. Yes, I did all the work for you:
1.[T][ ] finish report
________________________________________________________________________________
________________________________________________________________________________
Finally, you're leaving. Bye. Don't make me miss you.
________________________________________________________________________________
```

**Actual stderr:**
```text
(empty)
```

**PASS**

## 3: whitespace and malformed command edge cases

**Command:** `java -Dstdout.encoding=UTF-8 -Dstderr.encoding=UTF-8 -Duser.language=en -Duser.country=US -ea -cp C:\NUS\CS2103T\ip\build\classes\java\main xue.Xue`

**Input:**
```text
   
todo    
Todo valid-looking task
todo   actual task
mark +1
unmark -1
list
bye now
bye
```

**Actual stdout:**
```text
________________________________________________________________________________
██   ██  ██   ██  ███████
 ██ ██   ██   ██  ██
  ███    ██   ██  █████
 ██ ██   ██   ██  ██
██   ██   █████   ███████
Hello, I'm Xue — your reluctantly competent task assistant.
Give me a command and I will pretend it was my idea.
________________________________________________________________________________
OOPS!!! I don't know what that means. Use a proper command next time.
________________________________________________________________________________
________________________________________________________________________________
OOPS!!! The description of a todo cannot be empty. I cannot read your mind!
________________________________________________________________________________
________________________________________________________________________________
OOPS!!! I don't know what that means. Use a proper command next time.
________________________________________________________________________________
________________________________________________________________________________
Got it. I've added this task: [T][ ] actual task
Now you have 1 tasks in the list.
________________________________________________________________________________
________________________________________________________________________________
OOPS!!! That is not a valid task number. Numbers are not that complicated.
________________________________________________________________________________
________________________________________________________________________________
OOPS!!! That is not a valid task number. Numbers are not that complicated.
________________________________________________________________________________
________________________________________________________________________________
Here are your tasks. Yes, I did all the work for you:
1.[T][ ] actual task
________________________________________________________________________________
________________________________________________________________________________
OOPS!!! I don't know what that means. Use a proper command next time.
________________________________________________________________________________
________________________________________________________________________________
Finally, you're leaving. Bye. Don't make me miss you.
________________________________________________________________________________
```

**Actual stderr:**
```text
(empty)
```

**PASS**

## 4: multiple tasks and boundary indices

**Command:** `java -Dstdout.encoding=UTF-8 -Dstderr.encoding=UTF-8 -Duser.language=en -Duser.country=US -ea -cp C:\NUS\CS2103T\ip\build\classes\java\main xue.Xue`

**Input:**
```text
todo first task
todo second task
todo third task
mark 3
list
unmark 2
list
mark 4
unmark 0
list
bye
```

**Actual stdout:**
```text
________________________________________________________________________________
██   ██  ██   ██  ███████
 ██ ██   ██   ██  ██
  ███    ██   ██  █████
 ██ ██   ██   ██  ██
██   ██   █████   ███████
Hello, I'm Xue — your reluctantly competent task assistant.
Give me a command and I will pretend it was my idea.
________________________________________________________________________________
Got it. I've added this task: [T][ ] first task
Now you have 1 tasks in the list.
________________________________________________________________________________
________________________________________________________________________________
Got it. I've added this task: [T][ ] second task
Now you have 2 tasks in the list.
________________________________________________________________________________
________________________________________________________________________________
Got it. I've added this task: [T][ ] third task
Now you have 3 tasks in the list.
________________________________________________________________________________
________________________________________________________________________________
Fine, I've marked this task as done. Happy now?
  [X] third task
________________________________________________________________________________
________________________________________________________________________________
Here are your tasks. Yes, I did all the work for you:
1.[T][ ] first task
2.[T][ ] second task
3.[T][X] third task
________________________________________________________________________________
________________________________________________________________________________
There. I've undone it. Try to make up your mind next time:
  [ ] second task
________________________________________________________________________________
________________________________________________________________________________
Here are your tasks. Yes, I did all the work for you:
1.[T][ ] first task
2.[T][ ] second task
3.[T][X] third task
________________________________________________________________________________
________________________________________________________________________________
OOPS!!! That task number does not exist. Did you just invent it?
________________________________________________________________________________
________________________________________________________________________________
OOPS!!! That task number does not exist. Did you just invent it?
________________________________________________________________________________
________________________________________________________________________________
Here are your tasks. Yes, I did all the work for you:
1.[T][ ] first task
2.[T][ ] second task
3.[T][X] third task
________________________________________________________________________________
________________________________________________________________________________
Finally, you're leaving. Bye. Don't make me miss you.
________________________________________________________________________________
```

**Actual stderr:**
```text
(empty)
```

**PASS**

## 5: task types and date/time text

**Command:** `java -Dstdout.encoding=UTF-8 -Dstderr.encoding=UTF-8 -Duser.language=en -Duser.country=US -ea -cp C:\NUS\CS2103T\ip\build\classes\java\main xue.Xue`

**Input:**
```text
todo borrow book
deadline return book /by Sunday
event project meeting /from Mon 2pm /to 4pm
deadline submit form /by 2019-10-15
event conference /from 2019-10-15 0900 /to 2019-10-15 1800
list
bye
```

**Actual stdout:**
```text
________________________________________________________________________________
██   ██  ██   ██  ███████
 ██ ██   ██   ██  ██
  ███    ██   ██  █████
 ██ ██   ██   ██  ██
██   ██   █████   ███████
Hello, I'm Xue — your reluctantly competent task assistant.
Give me a command and I will pretend it was my idea.
________________________________________________________________________________
Got it. I've added this task: [T][ ] borrow book
Now you have 1 tasks in the list.
________________________________________________________________________________
________________________________________________________________________________
Got it. I've added this task: [D][ ] return book (by: Sunday)
Now you have 2 tasks in the list.
________________________________________________________________________________
________________________________________________________________________________
Got it. I've added this task: [E][ ] project meeting (from: Mon 2pm to: 4pm)
Now you have 3 tasks in the list.
________________________________________________________________________________
________________________________________________________________________________
Got it. I've added this task: [D][ ] submit form (by: Oct 15 2019)
Now you have 4 tasks in the list.
________________________________________________________________________________
________________________________________________________________________________
Got it. I've added this task: [E][ ] conference (from: Oct 15 2019 09:00 AM to: Oct 15 2019 06:00 PM)
Now you have 5 tasks in the list.
________________________________________________________________________________
________________________________________________________________________________
Here are your tasks. Yes, I did all the work for you:
1.[T][ ] borrow book
2.[D][ ] return book (by: Sunday)
3.[E][ ] project meeting (from: Mon 2pm to: 4pm)
4.[D][ ] submit form (by: Oct 15 2019)
5.[E][ ] conference (from: Oct 15 2019 09:00 AM to: Oct 15 2019 06:00 PM)
________________________________________________________________________________
________________________________________________________________________________
Finally, you're leaving. Bye. Don't make me miss you.
________________________________________________________________________________
```

**Actual stderr:**
```text
(empty)
```

**PASS**

## 6: deleting tasks and preserving list numbering

**Command:** `java -Dstdout.encoding=UTF-8 -Dstderr.encoding=UTF-8 -Duser.language=en -Duser.country=US -ea -cp C:\NUS\CS2103T\ip\build\classes\java\main xue.Xue`

**Input:**
```text
todo first task
deadline second task /by Friday
event third task /from Mon 2pm /to 4pm
delete 2
delete 9
list
bye
```

**Actual stdout:**
```text
________________________________________________________________________________
██   ██  ██   ██  ███████
 ██ ██   ██   ██  ██
  ███    ██   ██  █████
 ██ ██   ██   ██  ██
██   ██   █████   ███████
Hello, I'm Xue — your reluctantly competent task assistant.
Give me a command and I will pretend it was my idea.
________________________________________________________________________________
Got it. I've added this task: [T][ ] first task
Now you have 1 tasks in the list.
________________________________________________________________________________
________________________________________________________________________________
Got it. I've added this task: [D][ ] second task (by: Friday)
Now you have 2 tasks in the list.
________________________________________________________________________________
________________________________________________________________________________
Got it. I've added this task: [E][ ] third task (from: Mon 2pm to: 4pm)
Now you have 3 tasks in the list.
________________________________________________________________________________
________________________________________________________________________________
Fine, I've removed this task:
  [D][ ] second task (by: Friday)
Now you have 2 tasks in the list.
________________________________________________________________________________
________________________________________________________________________________
OOPS!!! That task number does not exist. Did you just invent it?
________________________________________________________________________________
________________________________________________________________________________
Here are your tasks. Yes, I did all the work for you:
1.[T][ ] first task
2.[E][ ] third task (from: Mon 2pm to: 4pm)
________________________________________________________________________________
________________________________________________________________________________
Finally, you're leaving. Bye. Don't make me miss you.
________________________________________________________________________________
```

**Actual stderr:**
```text
(empty)
```

**PASS**

## 7: loading saved tasks

**Command:** `java -Dstdout.encoding=UTF-8 -Dstderr.encoding=UTF-8 -Duser.language=en -Duser.country=US -ea -cp C:\NUS\CS2103T\ip\build\classes\java\main xue.Xue`

**Input:**
```text
list
bye
```

**Actual stdout:**
```text
________________________________________________________________________________
██   ██  ██   ██  ███████
 ██ ██   ██   ██  ██
  ███    ██   ██  █████
 ██ ██   ██   ██  ██
██   ██   █████   ███████
Hello, I'm Xue — your reluctantly competent task assistant.
Give me a command and I will pretend it was my idea.
________________________________________________________________________________
Here are your tasks. Yes, I did all the work for you:
1.[T][X] read book
2.[D][ ] return book (by: June 6th)
3.[E][ ] project meeting (from: Aug 6th 2pm to: Aug 6th 4pm)
________________________________________________________________________________
________________________________________________________________________________
Finally, you're leaving. Bye. Don't make me miss you.
________________________________________________________________________________
```

**Actual stderr:**
```text
(empty)
```

**PASS**

## 8: malformed saved records

**Command:** `java -Dstdout.encoding=UTF-8 -Dstderr.encoding=UTF-8 -Duser.language=en -Duser.country=US -ea -cp C:\NUS\CS2103T\ip\build\classes\java\main xue.Xue`

**Input:**
```text
list
bye
```

**Actual stdout:**
```text
________________________________________________________________________________
██   ██  ██   ██  ███████
 ██ ██   ██   ██  ██
  ███    ██   ██  █████
 ██ ██   ██   ██  ██
██   ██   █████   ███████
Hello, I'm Xue — your reluctantly competent task assistant.
Give me a command and I will pretend it was my idea.
OOPS!!! Some saved tasks were invalid and were skipped.
________________________________________________________________________________
Here are your tasks. Yes, I did all the work for you:
1.[T][X] retained
________________________________________________________________________________
________________________________________________________________________________
Finally, you're leaving. Bye. Don't make me miss you.
________________________________________________________________________________
```

**Actual stderr:**
```text
(empty)
```

**PASS**

## 9: finding tasks by description keyword

**Command:** `java -Dstdout.encoding=UTF-8 -Dstderr.encoding=UTF-8 -Duser.language=en -Duser.country=US -ea -cp C:\NUS\CS2103T\ip\build\classes\java\main xue.Xue`

**Input:**
```text
todo read book
deadline return book /by June 6th
todo buy milk
find BOOK
bye
```

**Actual stdout:**
```text
________________________________________________________________________________
██   ██  ██   ██  ███████
 ██ ██   ██   ██  ██
  ███    ██   ██  █████
 ██ ██   ██   ██  ██
██   ██   █████   ███████
Hello, I'm Xue — your reluctantly competent task assistant.
Give me a command and I will pretend it was my idea.
________________________________________________________________________________
Got it. I've added this task: [T][ ] read book
Now you have 1 tasks in the list.
________________________________________________________________________________
________________________________________________________________________________
Got it. I've added this task: [D][ ] return book (by: June 6th)
Now you have 2 tasks in the list.
________________________________________________________________________________
________________________________________________________________________________
Got it. I've added this task: [T][ ] buy milk
Now you have 3 tasks in the list.
________________________________________________________________________________
________________________________________________________________________________
Here are the matching tasks in your list:
1.[T][ ] read book
2.[D][ ] return book (by: June 6th)
________________________________________________________________________________
________________________________________________________________________________
Finally, you're leaving. Bye. Don't make me miss you.
________________________________________________________________________________
```

**Actual stderr:**
```text
(empty)
```

**PASS**

## 10: JavaFX GUI interactions

**NOT RUN:** Native GUI control is unavailable in this session. Manual checks remain outstanding.

## 11: undo and redo

**Command:** `java -Dstdout.encoding=UTF-8 -Dstderr.encoding=UTF-8 -Duser.language=en -Duser.country=US -ea -cp C:\NUS\CS2103T\ip\build\classes\java\main xue.Xue`

**Input:**
```text
todo first task
todo second task
undo
redo
undo
todo replacement task
redo
undo
list
bye
```

**Actual stdout:**
```text
________________________________________________________________________________
██   ██  ██   ██  ███████
 ██ ██   ██   ██  ██
  ███    ██   ██  █████
 ██ ██   ██   ██  ██
██   ██   █████   ███████
Hello, I'm Xue — your reluctantly competent task assistant.
Give me a command and I will pretend it was my idea.
________________________________________________________________________________
Got it. I've added this task: [T][ ] first task
Now you have 1 tasks in the list.
________________________________________________________________________________
________________________________________________________________________________
Got it. I've added this task: [T][ ] second task
Now you have 2 tasks in the list.
________________________________________________________________________________
________________________________________________________________________________
Undo completed. Be alert next time.
Here are your tasks. Yes, I did all the work for you:
1.[T][ ] first task
________________________________________________________________________________
________________________________________________________________________________
Redo completed.
Here are your tasks. Yes, I did all the work for you:
1.[T][ ] first task
2.[T][ ] second task
________________________________________________________________________________
________________________________________________________________________________
Undo completed. Be alert next time.
Here are your tasks. Yes, I did all the work for you:
1.[T][ ] first task
________________________________________________________________________________
________________________________________________________________________________
Got it. I've added this task: [T][ ] replacement task
Now you have 2 tasks in the list.
________________________________________________________________________________
________________________________________________________________________________
OOPS!!! There is nothing to redo.
________________________________________________________________________________
________________________________________________________________________________
Undo completed. Be alert next time.
Here are your tasks. Yes, I did all the work for you:
1.[T][ ] first task
________________________________________________________________________________
________________________________________________________________________________
Here are your tasks. Yes, I did all the work for you:
1.[T][ ] first task
________________________________________________________________________________
________________________________________________________________________________
Finally, you're leaving. Bye. Don't make me miss you.
________________________________________________________________________________
```

**Actual stderr:**
```text
(empty)
```

**PASS**

## 12: undo and redo validation

**Command:** `java -Dstdout.encoding=UTF-8 -Dstderr.encoding=UTF-8 -Duser.language=en -Duser.country=US -ea -cp C:\NUS\CS2103T\ip\build\classes\java\main xue.Xue`

**Input:**
```text
undo
redo
undo 1
redo 1
bye
```

**Actual stdout:**
```text
________________________________________________________________________________
██   ██  ██   ██  ███████
 ██ ██   ██   ██  ██
  ███    ██   ██  █████
 ██ ██   ██   ██  ██
██   ██   █████   ███████
Hello, I'm Xue — your reluctantly competent task assistant.
Give me a command and I will pretend it was my idea.
________________________________________________________________________________
OOPS!!! Hey you need to DO before you can undo!
________________________________________________________________________________
________________________________________________________________________________
OOPS!!! There is nothing to redo.
________________________________________________________________________________
________________________________________________________________________________
OOPS!!! The undo command does not accept any arguments.
________________________________________________________________________________
________________________________________________________________________________
OOPS!!! The redo command does not accept any arguments.
________________________________________________________________________________
________________________________________________________________________________
Finally, you're leaving. Bye. Don't make me miss you.
________________________________________________________________________________
```

**Actual stderr:**
```text
(empty)
```

**PASS**

## 13: invalid dates and backwards events

**Command:** `java -Dstdout.encoding=UTF-8 -Dstderr.encoding=UTF-8 -Duser.language=en -Duser.country=US -ea -cp C:\NUS\CS2103T\ip\build\classes\java\main xue.Xue`

**Input:**
```text
todo retained
todo redo me
undo
deadline impossible /by 2026-09-31 1200
deadline malformed /by 2026/09/24 1200
event impossible /from 2026-09-31 1400 /to 2026-10-01 1600
event backwards /from 2026-09-26 1600 /to 2026-09-25 1400
event invalid end /from 2026-09-01 /to 2026-09-31
deadline invalid time /by 2026-09-24 2400
deadline missing /by
list
redo
bye
```

**Actual stdout:**
```text
________________________________________________________________________________
██   ██  ██   ██  ███████
 ██ ██   ██   ██  ██
  ███    ██   ██  █████
 ██ ██   ██   ██  ██
██   ██   █████   ███████
Hello, I'm Xue — your reluctantly competent task assistant.
Give me a command and I will pretend it was my idea.
________________________________________________________________________________
Got it. I've added this task: [T][ ] retained
Now you have 1 tasks in the list.
________________________________________________________________________________
________________________________________________________________________________
Got it. I've added this task: [T][ ] redo me
Now you have 2 tasks in the list.
________________________________________________________________________________
________________________________________________________________________________
Undo completed. Be alert next time.
Here are your tasks. Yes, I did all the work for you:
1.[T][ ] retained
________________________________________________________________________________
________________________________________________________________________________
OOPS!!! That date is invalid. Use yyyy-MM-dd or yyyy-MM-dd HHmm.
________________________________________________________________________________
________________________________________________________________________________
OOPS!!! That date is invalid. Use yyyy-MM-dd or yyyy-MM-dd HHmm.
________________________________________________________________________________
________________________________________________________________________________
OOPS!!! That date is invalid. Use yyyy-MM-dd or yyyy-MM-dd HHmm.
________________________________________________________________________________
________________________________________________________________________________
OOPS!!! An event cannot end before it starts.
________________________________________________________________________________
________________________________________________________________________________
OOPS!!! That date is invalid. Use yyyy-MM-dd or yyyy-MM-dd HHmm.
________________________________________________________________________________
________________________________________________________________________________
OOPS!!! That date is invalid. Use yyyy-MM-dd or yyyy-MM-dd HHmm.
________________________________________________________________________________
________________________________________________________________________________
OOPS!!! That date is invalid. Use yyyy-MM-dd or yyyy-MM-dd HHmm.
________________________________________________________________________________
________________________________________________________________________________
Here are your tasks. Yes, I did all the work for you:
1.[T][ ] retained
________________________________________________________________________________
________________________________________________________________________________
Redo completed.
Here are your tasks. Yes, I did all the work for you:
1.[T][ ] retained
2.[T][ ] redo me
________________________________________________________________________________
________________________________________________________________________________
Finally, you're leaving. Bye. Don't make me miss you.
________________________________________________________________________________
```

**Actual stderr:**
```text
(empty)
```

**PASS**

Storage assertion: PASS
## 14: invalid saved dates and valid boundary dates

**Command:** `java -Dstdout.encoding=UTF-8 -Dstderr.encoding=UTF-8 -Duser.language=en -Duser.country=US -ea -cp C:\NUS\CS2103T\ip\build\classes\java\main xue.Xue`

**Input:**
```text
list
bye
```

**Actual stdout:**
```text
________________________________________________________________________________
██   ██  ██   ██  ███████
 ██ ██   ██   ██  ██
  ███    ██   ██  █████
 ██ ██   ██   ██  ██
██   ██   █████   ███████
Hello, I'm Xue — your reluctantly competent task assistant.
Give me a command and I will pretend it was my idea.
OOPS!!! Some saved tasks were invalid and were skipped.
________________________________________________________________________________
Here are your tasks. Yes, I did all the work for you:
1.[T][X] retained
2.[D][ ] leap day (by: Feb 29 2028 12:00 AM)
3.[E][X] overnight (from: Sep 30 2026 11:59 PM to: Oct 01 2026 12:00 AM)
________________________________________________________________________________
________________________________________________________________________________
Finally, you're leaving. Bye. Don't make me miss you.
________________________________________________________________________________
```

**Actual stderr:**
```text
(empty)
```

**PASS**

Storage unchanged assertion: PASS
## 15: valid dates persist and reload

**Command:** `java -Dstdout.encoding=UTF-8 -Dstderr.encoding=UTF-8 -Duser.language=en -Duser.country=US -ea -cp C:\NUS\CS2103T\ip\build\classes\java\main xue.Xue`

**Input:**
```text
deadline leap day /by 2028-02-29 0000
event instant /from 2026-09-30 2359 /to 2026-09-30 2359
event overnight /from 2026-09-30 2359 /to 2026-10-01 0000
bye
```

**Actual stdout:**
```text
________________________________________________________________________________
██   ██  ██   ██  ███████
 ██ ██   ██   ██  ██
  ███    ██   ██  █████
 ██ ██   ██   ██  ██
██   ██   █████   ███████
Hello, I'm Xue — your reluctantly competent task assistant.
Give me a command and I will pretend it was my idea.
________________________________________________________________________________
Got it. I've added this task: [D][ ] leap day (by: Feb 29 2028 12:00 AM)
Now you have 1 tasks in the list.
________________________________________________________________________________
________________________________________________________________________________
Got it. I've added this task: [E][ ] instant (from: Sep 30 2026 11:59 PM to: Sep 30 2026 11:59 PM)
Now you have 2 tasks in the list.
________________________________________________________________________________
________________________________________________________________________________
Got it. I've added this task: [E][ ] overnight (from: Sep 30 2026 11:59 PM to: Oct 01 2026 12:00 AM)
Now you have 3 tasks in the list.
________________________________________________________________________________
________________________________________________________________________________
Finally, you're leaving. Bye. Don't make me miss you.
________________________________________________________________________________
```

**Actual stderr:**
```text
(empty)
```

**PASS**

## 15: valid dates persist and reload (reload)

**Command:** `java -Dstdout.encoding=UTF-8 -Dstderr.encoding=UTF-8 -Duser.language=en -Duser.country=US -ea -cp C:\NUS\CS2103T\ip\build\classes\java\main xue.Xue`

**Input:**
```text
list
bye
```

**Actual stdout:**
```text
________________________________________________________________________________
██   ██  ██   ██  ███████
 ██ ██   ██   ██  ██
  ███    ██   ██  █████
 ██ ██   ██   ██  ██
██   ██   █████   ███████
Hello, I'm Xue — your reluctantly competent task assistant.
Give me a command and I will pretend it was my idea.
________________________________________________________________________________
Here are your tasks. Yes, I did all the work for you:
1.[D][ ] leap day (by: Feb 29 2028 12:00 AM)
2.[E][ ] instant (from: Sep 30 2026 11:59 PM to: Sep 30 2026 11:59 PM)
3.[E][ ] overnight (from: Sep 30 2026 11:59 PM to: Oct 01 2026 12:00 AM)
________________________________________________________________________________
________________________________________________________________________________
Finally, you're leaving. Bye. Don't make me miss you.
________________________________________________________________________________
```

**Actual stderr:**
```text
(empty)
```

**PASS**

## Additional JavaFX smoke check

This automated check invokes the actual control handlers. It does not verify physical clicks or visual scrolling.

Command: `java -Dstdout.encoding=UTF-8 -Dstderr.encoding=UTF-8 -ea -cp C:\NUS\CS2103T\ip\_temp\gui-classes;C:\NUS\CS2103T\ip\build\libs\duke.jar xue.smoke.GuiSmoke`

Stdout:
```text
PASS: Startup controls
PASS: Welcome message
PASS: Send handler
PASS: Enter handler
PASS: Empty input ignored
PASS: not a command
PASS: list
PASS: find book
PASS: mark 1
PASS: unmark 1
PASS: delete 1
PASS: deadline impossible /by 2026-09-31 1200
PASS: deadline malformed /by 2026/09/24 1200
PASS: event impossible /from 2026-09-31 1400 /to 2026-10-01 1600
PASS: event backwards /from 2026-09-26 1600 /to 2026-09-25 1400
PASS: Input stays within window
PASS: Stage closes
PASS: JavaFX startup, controls, handlers, errors, layout bounds, and close.
```

Stderr:
```text
Sept 29, 2026 2:20:55 PM com.sun.javafx.application.PlatformImpl startup
WARNING: Unsupported JavaFX configuration: classes were loaded from 'unnamed module @5674cd4d'
WARNING: A restricted method in java.lang.System has been called
WARNING: java.lang.System::load has been called by com.sun.glass.utils.NativeLibLoader in an unnamed module (file:/C:/NUS/CS2103T/ip/build/libs/duke.jar)
WARNING: Use --enable-native-access=ALL-UNNAMED to avoid a warning for callers in this module
WARNING: Restricted methods will be blocked in a future release unless native access is enabled

Can not create cache at C:\.openjfx\cache\17.0.7
Can not create cache at C:\.openjfx\cache\17.0.7
Can not create cache at C:\.openjfx\cache\17.0.7
Can not create cache at C:\.openjfx\cache\17.0.7
Can not create cache at C:\.openjfx\cache\17.0.7
Can not create cache at C:\.openjfx\cache\17.0.7
Can not create cache at C:\.openjfx\cache\17.0.7
Can not create cache at C:\.openjfx\cache\17.0.7
Can not create cache at C:\.openjfx\cache\17.0.7
Can not create cache at C:\.openjfx\cache\17.0.7
Can not create cache at C:\.openjfx\cache\17.0.7
Can not create cache at C:\.openjfx\cache\17.0.7
Can not create cache at C:\.openjfx\cache\17.0.7
Can not create cache at C:\.openjfx\cache\17.0.7
Can not create cache at C:\.openjfx\cache\17.0.7
Can not create cache at C:\.openjfx\cache\17.0.7
Can not create cache at C:\.openjfx\cache\17.0.7
Can not create cache at C:\.openjfx\cache\17.0.7
Can not create cache at C:\.openjfx\cache\17.0.7
Can not create cache at C:\.openjfx\cache\17.0.7
Can not create cache at C:\.openjfx\cache\17.0.7
Can not create cache at C:\.openjfx\cache\17.0.7
Can not create cache at C:\.openjfx\cache\17.0.7
Can not create cache at C:\.openjfx\cache\17.0.7
Can not create cache at C:\.openjfx\cache\17.0.7
Can not create cache at C:\.openjfx\cache\17.0.7
Can not create cache at C:\.openjfx\cache\17.0.7
Can not create cache at C:\.openjfx\cache\17.0.7
Can not create cache at C:\.openjfx\cache\17.0.7
Can not create cache at C:\.openjfx\cache\17.0.7
Can not create cache at C:\.openjfx\cache\17.0.7
Can not create cache at C:\.openjfx\cache\17.0.7
Can not create cache at C:\.openjfx\cache\17.0.7
Can not create cache at C:\.openjfx\cache\17.0.7
Can not create cache at C:\.openjfx\cache\17.0.7
Can not create cache at C:\.openjfx\cache\17.0.7
Can not create cache at C:\.openjfx\cache\17.0.7
Can not create cache at C:\.openjfx\cache\17.0.7
Can not create cache at C:\.openjfx\cache\17.0.7
Can not create cache at C:\.openjfx\cache\17.0.7
Can not create cache at C:\.openjfx\cache\17.0.7
Can not create cache at C:\.openjfx\cache\17.0.7
Can not create cache at C:\.openjfx\cache\17.0.7
Can not create cache at C:\.openjfx\cache\17.0.7
Can not create cache at C:\.openjfx\cache\17.0.7
Can not create cache at C:\.openjfx\cache\17.0.7
Can not create cache at C:\.openjfx\cache\17.0.7
Can not create cache at C:\.openjfx\cache\17.0.7
Can not create cache at C:\.openjfx\cache\17.0.7
Can not create cache at C:\.openjfx\cache\17.0.7
```

Exit code: 0

## Build and review checks

- Java: Oracle JDK 25.0.4.1.
- Command: `.\gradlew.bat --gradle-user-home .gradle-home build --rerun-tasks --console=plain`.
- Final result: BUILD SUCCESSFUL; all 42 JUnit tests passed (15 command processor, 8 task list, 19 task model).
- The initial unit-test run found one locale-dependent display mismatch: expected `Sep 30 2026`, actual `Sept 30 2026`. Month formatting now explicitly uses English, and the full build and console UI suite passed after that fix.
- The initial console capture needed explicit UTF-8 stdout/stderr settings on Windows; the documented commands include these settings.
- `git diff --check -- src test`: passed after removing trailing whitespace from the blank-description fixture.
- JavaFX smoke check: passed with runtime/module and sandbox cache warnings recorded above. Physical click and visual scrolling checks remain unverified.
- Tests used temporary task files; the application's real `data/duke.txt` was not modified.
