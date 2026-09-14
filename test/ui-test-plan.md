# Console UI Test Plan

JavaFX-specific layout and environment checks are maintained separately in
[`gui-test-plan.md`](gui-test-plan.md).

Run these tests from the repository root with Java 25. Compile the current source before running the plan:

```text
javac -encoding UTF-8 -d out src/main/java/cookie/Cookie.java src/main/java/cookie/command/*.java src/main/java/cookie/task/*.java src/main/java/cookie/storage/*.java src/main/java/cookie/ui/ConsoleOutput.java src/main/java/cookie/ui/Output.java src/main/java/cookie/ui/ReplyCollector.java src/main/java/cookie/ui/Ui.java
```

Each test case starts a fresh program process. The command is the program-launch command; the fenced input block is sent to standard input exactly as written.

## Coverage guide

| Area | Successful behavior | Validation and state preservation |
| --- | --- | --- |
| Application flow | 1 | 9, 12--14 |
| Todo and list | 2--3 | 8, 13, 26, 30, 47 |
| Completion state | 4--5, 25 | 11, 15--17, 22 |
| Deadline and event | 6--7, 35--37, 40--41 | 10, 18, 23--24, 30--31, 38--39 |
| Find | 41 | 42 |
| Sorting | 43--44, 46 | 45 |
| Deletion | 19 | 20--22 |
| Persistence | 2, 4--7, 19, 25, 27, 34--36, 43 | 28--33 |
| Startup and file errors | 28, 33--34 | 29, 32 |

The expected output in every case is compared exactly, including spaces and separators.

## Test case 1: Exit immediately

**Aim:** Verify that Cookie displays its greeting and exits cleanly when the user enters `bye`.

**Command:** `java "-Dfile.encoding=UTF-8" "-Dstdout.encoding=UTF-8" -cp out cookie.Cookie`

**Inputs:**
```text
bye
```

**Expected output:**
```text
____________________________________________________________
 ██████╗ ██████╗  ██████╗ ██╗  ██╗██╗███████╗
██╔════╝██╔═══██╗██╔═══██╗██║ ██╔╝██║██╔════╝
██║     ██║   ██║██║   ██║█████╔╝ ██║█████╗  
██║     ██║   ██║██║   ██║██╔═██╗ ██║██╔══╝  
╚██████╗╚██████╔╝╚██████╔╝██║  ██╗██║███████╗
 ╚═════╝ ╚═════╝  ╚═════╝ ╚═╝  ╚═╝╚═╝╚══════╝
Hello! I'm your favourite chatbot Cookie.
What do you need today?
____________________________________________________________
____________________________________________________________
Bye. I'm going to sleep.
____________________________________________________________
```

## Test case 2: Add a todo task

**Aim:** Verify that a `todo` command creates an incomplete todo task and confirms the addition.

**Command:** `java "-Dfile.encoding=UTF-8" "-Dstdout.encoding=UTF-8" -cp out cookie.Cookie`

**Inputs:**
```text
todo buy milk
bye
```

**Expected output:**
```text
____________________________________________________________
 ██████╗ ██████╗  ██████╗ ██╗  ██╗██╗███████╗
██╔════╝██╔═══██╗██╔═══██╗██║ ██╔╝██║██╔════╝
██║     ██║   ██║██║   ██║█████╔╝ ██║█████╗  
██║     ██║   ██║██║   ██║██╔═██╗ ██║██╔══╝  
╚██████╗╚██████╔╝╚██████╔╝██║  ██╗██║███████╗
 ╚═════╝ ╚═════╝  ╚═════╝ ╚═╝  ╚═╝╚═╝╚══════╝
Hello! I'm your favourite chatbot Cookie.
What do you need today?
____________________________________________________________
____________________________________________________________
Ok. I've added this task:
   [T][ ] buy milk
You have 1 task(s) now. Better start working.
____________________________________________________________
____________________________________________________________
Bye. I'm going to sleep.
____________________________________________________________
```

## Test case 3: List tasks

**Aim:** Verify that the `list` command displays all tasks with their numbering and incomplete status.

**Command:** `java "-Dfile.encoding=UTF-8" "-Dstdout.encoding=UTF-8" -cp out cookie.Cookie`

**Inputs:**
```text
todo buy milk
list
bye
```

**Expected output:**
```text
____________________________________________________________
 ██████╗ ██████╗  ██████╗ ██╗  ██╗██╗███████╗
██╔════╝██╔═══██╗██╔═══██╗██║ ██╔╝██║██╔════╝
██║     ██║   ██║██║   ██║█████╔╝ ██║█████╗  
██║     ██║   ██║██║   ██║██╔═██╗ ██║██╔══╝  
╚██████╗╚██████╔╝╚██████╔╝██║  ██╗██║███████╗
 ╚═════╝ ╚═════╝  ╚═════╝ ╚═╝  ╚═╝╚═╝╚══════╝
Hello! I'm your favourite chatbot Cookie.
What do you need today?
____________________________________________________________
____________________________________________________________
Ok. I've added this task:
   [T][ ] buy milk
You have 1 task(s) now. Better start working.
____________________________________________________________
____________________________________________________________
Here are the task(s) in your list:
1. [T][ ] buy milk
____________________________________________________________
____________________________________________________________
Bye. I'm going to sleep.
____________________________________________________________
```

## Test case 4: Mark a task

**Aim:** Verify that `mark 1` marks the first task as complete and displays the completed status.

**Command:** `java "-Dfile.encoding=UTF-8" "-Dstdout.encoding=UTF-8" -cp out cookie.Cookie`

**Inputs:**
```text
todo finish report
mark 1
list
bye
```

**Expected output:**
```text
____________________________________________________________
 ██████╗ ██████╗  ██████╗ ██╗  ██╗██╗███████╗
██╔════╝██╔═══██╗██╔═══██╗██║ ██╔╝██║██╔════╝
██║     ██║   ██║██║   ██║█████╔╝ ██║█████╗  
██║     ██║   ██║██║   ██║██╔═██╗ ██║██╔══╝  
╚██████╗╚██████╔╝╚██████╔╝██║  ██╗██║███████╗
 ╚═════╝ ╚═════╝  ╚═════╝ ╚═╝  ╚═╝╚═╝╚══════╝
Hello! I'm your favourite chatbot Cookie.
What do you need today?
____________________________________________________________
____________________________________________________________
Ok. I've added this task:
   [T][ ] finish report
You have 1 task(s) now. Better start working.
____________________________________________________________
____________________________________________________________
Wow you actually got work done...
   [T][X] finish report
____________________________________________________________
____________________________________________________________
Here are the task(s) in your list:
1. [T][X] finish report
____________________________________________________________
____________________________________________________________
Bye. I'm going to sleep.
____________________________________________________________
```

## Test case 5: Unmark a task

**Aim:** Verify that `unmark 1` changes a completed task back to incomplete and displays the updated status.

**Command:** `java "-Dfile.encoding=UTF-8" "-Dstdout.encoding=UTF-8" -cp out cookie.Cookie`

**Inputs:**
```text
todo finish report
mark 1
unmark 1
list
bye
```

**Expected output:**
```text
____________________________________________________________
 ██████╗ ██████╗  ██████╗ ██╗  ██╗██╗███████╗
██╔════╝██╔═══██╗██╔═══██╗██║ ██╔╝██║██╔════╝
██║     ██║   ██║██║   ██║█████╔╝ ██║█████╗  
██║     ██║   ██║██║   ██║██╔═██╗ ██║██╔══╝  
╚██████╗╚██████╔╝╚██████╔╝██║  ██╗██║███████╗
 ╚═════╝ ╚═════╝  ╚═════╝ ╚═╝  ╚═╝╚═╝╚══════╝
Hello! I'm your favourite chatbot Cookie.
What do you need today?
____________________________________________________________
____________________________________________________________
Ok. I've added this task:
   [T][ ] finish report
You have 1 task(s) now. Better start working.
____________________________________________________________
____________________________________________________________
Wow you actually got work done...
   [T][X] finish report
____________________________________________________________
____________________________________________________________
I can't believe you lied to me...
   [T][ ] finish report
____________________________________________________________
____________________________________________________________
Here are the task(s) in your list:
1. [T][ ] finish report
____________________________________________________________
____________________________________________________________
Bye. I'm going to sleep.
____________________________________________________________
```

## Test case 6: Add a deadline task

**Aim:** Verify that a `deadline` command accepts a slash-formatted date and time, then displays both values in a readable format.

**Command:** `java "-Dfile.encoding=UTF-8" "-Dstdout.encoding=UTF-8" -cp out cookie.Cookie`

**Inputs:**
```text
deadline return book /by 2/12/2019 1800
bye
```

**Expected output:**
```text
____________________________________________________________
 ██████╗ ██████╗  ██████╗ ██╗  ██╗██╗███████╗
██╔════╝██╔═══██╗██╔═══██╗██║ ██╔╝██║██╔════╝
██║     ██║   ██║██║   ██║█████╔╝ ██║█████╗  
██║     ██║   ██║██║   ██║██╔═██╗ ██║██╔══╝  
╚██████╗╚██████╔╝╚██████╔╝██║  ██╗██║███████╗
 ╚═════╝ ╚═════╝  ╚═════╝ ╚═╝  ╚═╝╚═╝╚══════╝
Hello! I'm your favourite chatbot Cookie.
What do you need today?
____________________________________________________________
____________________________________________________________
Ok. I've added this task:
   [D][ ] return book (by: Dec 02 2019, 6:00 PM)
You have 1 task(s) now. Better start working.
____________________________________________________________
____________________________________________________________
Bye. I'm going to sleep.
____________________________________________________________
```

## Test case 7: Add an event task

**Aim:** Verify that an `event` command accepts date-times after its `/from` and `/to` markers and displays all three values.

**Command:** `java "-Dfile.encoding=UTF-8" "-Dstdout.encoding=UTF-8" -cp out cookie.Cookie`

**Inputs:**
```text
event project meeting /from 2/12/2019 1400 /to 2/12/2019 1600
bye
```

**Expected output:**
```text
____________________________________________________________
 ██████╗ ██████╗  ██████╗ ██╗  ██╗██╗███████╗
██╔════╝██╔═══██╗██╔═══██╗██║ ██╔╝██║██╔════╝
██║     ██║   ██║██║   ██║█████╔╝ ██║█████╗  
██║     ██║   ██║██║   ██║██╔═██╗ ██║██╔══╝  
╚██████╗╚██████╔╝╚██████╔╝██║  ██╗██║███████╗
 ╚═════╝ ╚═════╝  ╚═════╝ ╚═╝  ╚═╝╚═╝╚══════╝
Hello! I'm your favourite chatbot Cookie.
What do you need today?
____________________________________________________________
____________________________________________________________
Ok. I've added this task:
   [E][ ] project meeting (from: Dec 02 2019, 2:00 PM to: Dec 02 2019, 4:00 PM)
You have 1 task(s) now. Better start working.
____________________________________________________________
____________________________________________________________
Bye. I'm going to sleep.
____________________________________________________________
```

## Test case 8: Reject a todo without a description

**Aim:** Verify that an empty `todo` description displays an error and allows the user to continue.

**Command:** `java "-Dfile.encoding=UTF-8" "-Dstdout.encoding=UTF-8" -cp out cookie.Cookie`

**Inputs:**
```text
todo
bye
```

**Expected output:**
```text
____________________________________________________________
 ██████╗ ██████╗  ██████╗ ██╗  ██╗██╗███████╗
██╔════╝██╔═══██╗██╔═══██╗██║ ██╔╝██║██╔════╝
██║     ██║   ██║██║   ██║█████╔╝ ██║█████╗  
██║     ██║   ██║██║   ██║██╔═██╗ ██║██╔══╝  
╚██████╗╚██████╔╝╚██████╔╝██║  ██╗██║███████╗
 ╚═════╝ ╚═════╝  ╚═════╝ ╚═╝  ╚═╝╚═╝╚══════╝
Hello! I'm your favourite chatbot Cookie.
What do you need today?
____________________________________________________________
____________________________________________________________
Bruh... A todo task needs a description.
____________________________________________________________
____________________________________________________________
Bye. I'm going to sleep.
____________________________________________________________
```

## Test case 9: Reject an unknown command

**Aim:** Verify that an unrecognized command displays an error and does not terminate Cookie.

**Command:** `java "-Dfile.encoding=UTF-8" "-Dstdout.encoding=UTF-8" -cp out cookie.Cookie`

**Inputs:**
```text
blah
bye
```

**Expected output:**
```text
____________________________________________________________
 ██████╗ ██████╗  ██████╗ ██╗  ██╗██╗███████╗
██╔════╝██╔═══██╗██╔═══██╗██║ ██╔╝██║██╔════╝
██║     ██║   ██║██║   ██║█████╔╝ ██║█████╗  
██║     ██║   ██║██║   ██║██╔═██╗ ██║██╔══╝  
╚██████╗╚██████╔╝╚██████╔╝██║  ██╗██║███████╗
 ╚═════╝ ╚═════╝  ╚═════╝ ╚═╝  ╚═╝╚═╝╚══════╝
Hello! I'm your favourite chatbot Cookie.
What do you need today?
____________________________________________________________
____________________________________________________________
Bruh... What is that command!?
____________________________________________________________
____________________________________________________________
Bye. I'm going to sleep.
____________________________________________________________
```

## Test case 10: Reject an incomplete deadline

**Aim:** Verify that a `deadline` without a `/by` date and time displays an error and does not add a task.

**Command:** `java "-Dfile.encoding=UTF-8" "-Dstdout.encoding=UTF-8" -cp out cookie.Cookie`

**Inputs:**
```text
deadline return book
list
bye
```

**Expected output:**
```text
____________________________________________________________
 ██████╗ ██████╗  ██████╗ ██╗  ██╗██╗███████╗
██╔════╝██╔═══██╗██╔═══██╗██║ ██╔╝██║██╔════╝
██║     ██║   ██║██║   ██║█████╔╝ ██║█████╗  
██║     ██║   ██║██║   ██║██╔═██╗ ██║██╔══╝  
╚██████╗╚██████╔╝╚██████╔╝██║  ██╗██║███████╗
 ╚═════╝ ╚═════╝  ╚═════╝ ╚═╝  ╚═╝╚═╝╚══════╝
Hello! I'm your favourite chatbot Cookie.
What do you need today?
____________________________________________________________
____________________________________________________________
Bruh... A deadline needs a description and a date and time after /by.
____________________________________________________________
____________________________________________________________
Here are the task(s) in your list:
____________________________________________________________
____________________________________________________________
Bye. I'm going to sleep.
____________________________________________________________
```

## Test case 11: Reject an invalid task number

**Aim:** Verify that marking a task number that is not in the list displays an error and does not terminate Cookie.

**Command:** `java "-Dfile.encoding=UTF-8" "-Dstdout.encoding=UTF-8" -cp out cookie.Cookie`

**Inputs:**
```text
mark 1
bye
```

**Expected output:**
```text
____________________________________________________________
 ██████╗ ██████╗  ██████╗ ██╗  ██╗██╗███████╗
██╔════╝██╔═══██╗██╔═══██╗██║ ██╔╝██║██╔════╝
██║     ██║   ██║██║   ██║█████╔╝ ██║█████╗  
██║     ██║   ██║██║   ██║██╔═██╗ ██║██╔══╝  
╚██████╗╚██████╔╝╚██████╔╝██║  ██╗██║███████╗
 ╚═════╝ ╚═════╝  ╚═════╝ ╚═╝  ╚═╝╚═╝╚══════╝
Hello! I'm your favourite chatbot Cookie.
What do you need today?
____________________________________________________________
____________________________________________________________
Bruh... There is no task numbered 1.
____________________________________________________________
____________________________________________________________
Bye. I'm going to sleep.
____________________________________________________________
```

## Test case 12: Reject an empty command

**Aim:** Verify that a blank input line displays an error and allows the user to continue.

**Command:** `java "-Dfile.encoding=UTF-8" "-Dstdout.encoding=UTF-8" -cp out cookie.Cookie`

**Inputs:**
```text

bye
```

**Expected output:**
```text
____________________________________________________________
 ██████╗ ██████╗  ██████╗ ██╗  ██╗██╗███████╗
██╔════╝██╔═══██╗██╔═══██╗██║ ██╔╝██║██╔════╝
██║     ██║   ██║██║   ██║█████╔╝ ██║█████╗  
██║     ██║   ██║██║   ██║██╔═██╗ ██║██╔══╝  
╚██████╗╚██████╔╝╚██████╔╝██║  ██╗██║███████╗
 ╚═════╝ ╚═════╝  ╚═════╝ ╚═╝  ╚═╝╚═╝╚══════╝
Hello! I'm your favourite chatbot Cookie.
What do you need today?
____________________________________________________________
____________________________________________________________
Bruh... I couldn't understand an empty command.
____________________________________________________________
____________________________________________________________
Bye. I'm going to sleep.
____________________________________________________________
```

## Test case 13: Reject extra arguments for list

**Aim:** Verify that the `list` command rejects unexpected arguments.

**Command:** `java "-Dfile.encoding=UTF-8" "-Dstdout.encoding=UTF-8" -cp out cookie.Cookie`

**Inputs:**
```text
list now
bye
```

**Expected output:**
```text
____________________________________________________________
 ██████╗ ██████╗  ██████╗ ██╗  ██╗██╗███████╗
██╔════╝██╔═══██╗██╔═══██╗██║ ██╔╝██║██╔════╝
██║     ██║   ██║██║   ██║█████╔╝ ██║█████╗  
██║     ██║   ██║██║   ██║██╔═██╗ ██║██╔══╝  
╚██████╗╚██████╔╝╚██████╔╝██║  ██╗██║███████╗
 ╚═════╝ ╚═════╝  ╚═════╝ ╚═╝  ╚═╝╚═╝╚══════╝
Hello! I'm your favourite chatbot Cookie.
What do you need today?
____________________________________________________________
____________________________________________________________
Bruh... The list command does not take any arguments.
____________________________________________________________
____________________________________________________________
Bye. I'm going to sleep.
____________________________________________________________
```

## Test case 14: Reject extra arguments for bye

**Aim:** Verify that the `bye` command rejects unexpected arguments and remains available afterward.

**Command:** `java "-Dfile.encoding=UTF-8" "-Dstdout.encoding=UTF-8" -cp out cookie.Cookie`

**Inputs:**
```text
bye now
bye
```

**Expected output:**
```text
____________________________________________________________
 ██████╗ ██████╗  ██████╗ ██╗  ██╗██╗███████╗
██╔════╝██╔═══██╗██╔═══██╗██║ ██╔╝██║██╔════╝
██║     ██║   ██║██║   ██║█████╔╝ ██║█████╗  
██║     ██║   ██║██║   ██║██╔═██╗ ██║██╔══╝  
╚██████╗╚██████╔╝╚██████╔╝██║  ██╗██║███████╗
 ╚═════╝ ╚═════╝  ╚═════╝ ╚═╝  ╚═╝╚═╝╚══════╝
Hello! I'm your favourite chatbot Cookie.
What do you need today?
____________________________________________________________
____________________________________________________________
Bruh... The bye command does not take any arguments.
____________________________________________________________
____________________________________________________________
Bye. I'm going to sleep.
____________________________________________________________
```

## Test case 15: Reject a missing mark number

**Aim:** Verify that `mark` requires a task number.

**Command:** `java "-Dfile.encoding=UTF-8" "-Dstdout.encoding=UTF-8" -cp out cookie.Cookie`

**Inputs:**
```text
mark
bye
```

**Expected output:**
```text
____________________________________________________________
 ██████╗ ██████╗  ██████╗ ██╗  ██╗██╗███████╗
██╔════╝██╔═══██╗██╔═══██╗██║ ██╔╝██║██╔════╝
██║     ██║   ██║██║   ██║█████╔╝ ██║█████╗  
██║     ██║   ██║██║   ██║██╔═██╗ ██║██╔══╝  
╚██████╗╚██████╔╝╚██████╔╝██║  ██╗██║███████╗
 ╚═════╝ ╚═════╝  ╚═════╝ ╚═╝  ╚═╝╚═╝╚══════╝
Hello! I'm your favourite chatbot Cookie.
What do you need today?
____________________________________________________________
____________________________________________________________
Bruh... Usage: mark <task number>.
____________________________________________________________
____________________________________________________________
Bye. I'm going to sleep.
____________________________________________________________
```

## Test case 16: Reject a missing unmark number

**Aim:** Verify that `unmark` requires a task number.

**Command:** `java "-Dfile.encoding=UTF-8" "-Dstdout.encoding=UTF-8" -cp out cookie.Cookie`

**Inputs:**
```text
unmark
bye
```

**Expected output:**
```text
____________________________________________________________
 ██████╗ ██████╗  ██████╗ ██╗  ██╗██╗███████╗
██╔════╝██╔═══██╗██╔═══██╗██║ ██╔╝██║██╔════╝
██║     ██║   ██║██║   ██║█████╔╝ ██║█████╗  
██║     ██║   ██║██║   ██║██╔═██╗ ██║██╔══╝  
╚██████╗╚██████╔╝╚██████╔╝██║  ██╗██║███████╗
 ╚═════╝ ╚═════╝  ╚═════╝ ╚═╝  ╚═╝╚═╝╚══════╝
Hello! I'm your favourite chatbot Cookie.
What do you need today?
____________________________________________________________
____________________________________________________________
Bruh... Usage: unmark <task number>.
____________________________________________________________
____________________________________________________________
Bye. I'm going to sleep.
____________________________________________________________
```

## Test case 17: Reject a non-numeric task number

**Aim:** Verify that a task number must be a positive whole number.

**Command:** `java "-Dfile.encoding=UTF-8" "-Dstdout.encoding=UTF-8" -cp out cookie.Cookie`

**Inputs:**
```text
mark first
bye
```

**Expected output:**
```text
____________________________________________________________
 ██████╗ ██████╗  ██████╗ ██╗  ██╗██╗███████╗
██╔════╝██╔═══██╗██╔═══██╗██║ ██╔╝██║██╔════╝
██║     ██║   ██║██║   ██║█████╔╝ ██║█████╗  
██║     ██║   ██║██║   ██║██╔═██╗ ██║██╔══╝  
╚██████╗╚██████╔╝╚██████╔╝██║  ██╗██║███████╗
 ╚═════╝ ╚═════╝  ╚═════╝ ╚═╝  ╚═╝╚═╝╚══════╝
Hello! I'm your favourite chatbot Cookie.
What do you need today?
____________________________________________________________
____________________________________________________________
Bruh... The task number must be a positive whole number.
____________________________________________________________
____________________________________________________________
Bye. I'm going to sleep.
____________________________________________________________
```

## Test case 18: Reject an incomplete event

**Aim:** Verify that an `event` requires a description, a `/from` start time, and a `/to` end time.

**Command:** `java "-Dfile.encoding=UTF-8" "-Dstdout.encoding=UTF-8" -cp out cookie.Cookie`

**Inputs:**
```text
event project meeting /from Mon 2pm
bye
```


**Expected output:**
```text
____________________________________________________________
 ██████╗ ██████╗  ██████╗ ██╗  ██╗██╗███████╗
██╔════╝██╔═══██╗██╔═══██╗██║ ██╔╝██║██╔════╝
██║     ██║   ██║██║   ██║█████╔╝ ██║█████╗  
██║     ██║   ██║██║   ██║██╔═██╗ ██║██╔══╝  
╚██████╗╚██████╔╝╚██████╔╝██║  ██╗██║███████╗
 ╚═════╝ ╚═════╝  ╚═════╝ ╚═╝  ╚═╝╚═╝╚══════╝
Hello! I'm your favourite chatbot Cookie.
What do you need today?
____________________________________________________________
____________________________________________________________
Bruh... An event needs a description, a start time after /from, and an end time after /to.
____________________________________________________________
____________________________________________________________
Bye. I'm going to sleep.
____________________________________________________________
```

## Test case 19: Delete a task

**Aim:** Verify that `delete` removes the selected task, reports the removed task and remaining count, and renumbers the remaining tasks.

**Command:** `java "-Dfile.encoding=UTF-8" "-Dstdout.encoding=UTF-8" -cp out cookie.Cookie`

**Inputs:**
```text
todo buy groceries
deadline submit report /by 2019-12-06 1700
event team meeting /from 2019-12-06 1400 /to 2019-12-06 1500
delete 2
list
bye
```

**Expected output:**
```text
____________________________________________________________
 ██████╗ ██████╗  ██████╗ ██╗  ██╗██╗███████╗
██╔════╝██╔═══██╗██╔═══██╗██║ ██╔╝██║██╔════╝
██║     ██║   ██║██║   ██║█████╔╝ ██║█████╗  
██║     ██║   ██║██║   ██║██╔═██╗ ██║██╔══╝  
╚██████╗╚██████╔╝╚██████╔╝██║  ██╗██║███████╗
 ╚═════╝ ╚═════╝  ╚═════╝ ╚═╝  ╚═╝╚═╝╚══════╝
Hello! I'm your favourite chatbot Cookie.
What do you need today?
____________________________________________________________
____________________________________________________________
Ok. I've added this task:
   [T][ ] buy groceries
You have 1 task(s) now. Better start working.
____________________________________________________________
____________________________________________________________
Ok. I've added this task:
   [D][ ] submit report (by: Dec 06 2019, 5:00 PM)
You have 2 task(s) now. Better start working.
____________________________________________________________
____________________________________________________________
Ok. I've added this task:
   [E][ ] team meeting (from: Dec 06 2019, 2:00 PM to: Dec 06 2019, 3:00 PM)
You have 3 task(s) now. Better start working.
____________________________________________________________
____________________________________________________________
You're welcome. I've gotten rid of this task for you:
   [D][ ] submit report (by: Dec 06 2019, 5:00 PM)
Now you have 2 task(s) in the list.
____________________________________________________________
____________________________________________________________
Here are the task(s) in your list:
1. [T][ ] buy groceries
2. [E][ ] team meeting (from: Dec 06 2019, 2:00 PM to: Dec 06 2019, 3:00 PM)
____________________________________________________________
____________________________________________________________
Bye. I'm going to sleep.
____________________________________________________________
```

## Test case 20: Reject an invalid delete number

**Aim:** Verify that `delete` rejects a task number that is not in the list and keeps Cookie running.

**Command:** `java "-Dfile.encoding=UTF-8" "-Dstdout.encoding=UTF-8" -cp out cookie.Cookie`

**Inputs:**
```text
delete 1
bye
```

**Expected output:**
```text
____________________________________________________________
 ██████╗ ██████╗  ██████╗ ██╗  ██╗██╗███████╗
██╔════╝██╔═══██╗██╔═══██╗██║ ██╔╝██║██╔════╝
██║     ██║   ██║██║   ██║█████╔╝ ██║█████╗  
██║     ██║   ██║██║   ██║██╔═██╗ ██║██╔══╝  
╚██████╗╚██████╔╝╚██████╔╝██║  ██╗██║███████╗
 ╚═════╝ ╚═════╝  ╚═════╝ ╚═╝  ╚═╝╚═╝╚══════╝
Hello! I'm your favourite chatbot Cookie.
What do you need today?
____________________________________________________________
____________________________________________________________
Bruh... There is no task numbered 1.
____________________________________________________________
____________________________________________________________
Bye. I'm going to sleep.
____________________________________________________________
```

## Test case 21: Reject a missing delete number

**Aim:** Verify that `delete` requires a task number.

**Command:** `java "-Dfile.encoding=UTF-8" "-Dstdout.encoding=UTF-8" -cp out cookie.Cookie`

**Inputs:**
```text
delete
bye
```

**Expected output:**
```text
____________________________________________________________
 ██████╗ ██████╗  ██████╗ ██╗  ██╗██╗███████╗
██╔════╝██╔═══██╗██╔═══██╗██║ ██╔╝██║██╔════╝
██║     ██║   ██║██║   ██║█████╔╝ ██║█████╗  
██║     ██║   ██║██║   ██║██╔═██╗ ██║██╔══╝  
╚██████╗╚██████╔╝╚██████╔╝██║  ██╗██║███████╗
 ╚═════╝ ╚═════╝  ╚═════╝ ╚═╝  ╚═╝╚═╝╚══════╝
Hello! I'm your favourite chatbot Cookie.
What do you need today?
____________________________________________________________
____________________________________________________________
Bruh... Usage: delete <task number>.
____________________________________________________________
____________________________________________________________
Bye. I'm going to sleep.
____________________________________________________________
```

## Test case 22: Reject boundary task numbers without changing the list

**Aim:** Verify that zero, negative, out-of-range, and extra task-number arguments are rejected while the existing task remains unchanged.

**Command:** `java "-Dfile.encoding=UTF-8" "-Dstdout.encoding=UTF-8" -cp out cookie.Cookie`

**Inputs:**
```text
todo keep me
mark 0
unmark -1
delete 2
mark 1 extra
list
bye
```

**Expected output:**
```text
____________________________________________________________
 ██████╗ ██████╗  ██████╗ ██╗  ██╗██╗███████╗
██╔════╝██╔═══██╗██╔═══██╗██║ ██╔╝██║██╔════╝
██║     ██║   ██║██║   ██║█████╔╝ ██║█████╗  
██║     ██║   ██║██║   ██║██╔═██╗ ██║██╔══╝  
╚██████╗╚██████╔╝╚██████╔╝██║  ██╗██║███████╗
 ╚═════╝ ╚═════╝  ╚═════╝ ╚═╝  ╚═╝╚═╝╚══════╝
Hello! I'm your favourite chatbot Cookie.
What do you need today?
____________________________________________________________
____________________________________________________________
Ok. I've added this task:
   [T][ ] keep me
You have 1 task(s) now. Better start working.
____________________________________________________________
____________________________________________________________
Bruh... There is no task numbered 0.
____________________________________________________________
____________________________________________________________
Bruh... There is no task numbered -1.
____________________________________________________________
____________________________________________________________
Bruh... There is no task numbered 2.
____________________________________________________________
____________________________________________________________
Bruh... Usage: mark <task number>.
____________________________________________________________
____________________________________________________________
Here are the task(s) in your list:
1. [T][ ] keep me
____________________________________________________________
____________________________________________________________
Bye. I'm going to sleep.
____________________________________________________________
```

## Test case 23: Reject malformed deadline fields

**Aim:** Verify that a deadline requires both a description and a date-time after the `/by` marker, and that invalid input does not add a task.

**Command:** `java "-Dfile.encoding=UTF-8" "-Dstdout.encoding=UTF-8" -cp out cookie.Cookie`

**Inputs:**
```text
deadline /by 6/12/2019 1700
deadline submit report /by
deadline submit report by 6/12/2019 1700
list
bye
```

**Expected output:**
```text
____________________________________________________________
 ██████╗ ██████╗  ██████╗ ██╗  ██╗██╗███████╗
██╔════╝██╔═══██╗██╔═══██╗██║ ██╔╝██║██╔════╝
██║     ██║   ██║██║   ██║█████╔╝ ██║█████╗  
██║     ██║   ██║██║   ██║██╔═██╗ ██║██╔══╝  
╚██████╗╚██████╔╝╚██████╔╝██║  ██╗██║███████╗
 ╚═════╝ ╚═════╝  ╚═════╝ ╚═╝  ╚═╝╚═╝╚══════╝
Hello! I'm your favourite chatbot Cookie.
What do you need today?
____________________________________________________________
____________________________________________________________
Bruh... A deadline needs a description and a date and time after /by.
____________________________________________________________
____________________________________________________________
Bruh... A deadline needs a description and a date and time after /by.
____________________________________________________________
____________________________________________________________
Bruh... A deadline needs a description and a date and time after /by.
____________________________________________________________
____________________________________________________________
Here are the task(s) in your list:
____________________________________________________________
____________________________________________________________
Bye. I'm going to sleep.
____________________________________________________________
```

## Test case 24: Reject malformed event fields

**Aim:** Verify that an event requires a description, a non-empty `/from` time, a non-empty `/to` time, and correctly ordered markers without adding a task on error.

**Command:** `java "-Dfile.encoding=UTF-8" "-Dstdout.encoding=UTF-8" -cp out cookie.Cookie`

**Inputs:**
```text
event /from Mon 2pm /to 3pm
event team meeting /from /to 3pm
event team meeting /from Mon 2pm /to
event team meeting /to 3pm /from Mon 2pm
list
bye
```

**Expected output:**
```text
____________________________________________________________
 ██████╗ ██████╗  ██████╗ ██╗  ██╗██╗███████╗
██╔════╝██╔═══██╗██╔═══██╗██║ ██╔╝██║██╔════╝
██║     ██║   ██║██║   ██║█████╔╝ ██║█████╗  
██║     ██║   ██║██║   ██║██╔═██╗ ██║██╔══╝  
╚██████╗╚██████╔╝╚██████╔╝██║  ██╗██║███████╗
 ╚═════╝ ╚═════╝  ╚═════╝ ╚═╝  ╚═╝╚═╝╚══════╝
Hello! I'm your favourite chatbot Cookie.
What do you need today?
____________________________________________________________
____________________________________________________________
Bruh... An event needs a description, a start time after /from, and an end time after /to.
____________________________________________________________
____________________________________________________________
Bruh... An event needs a description, a start time after /from, and an end time after /to.
____________________________________________________________
____________________________________________________________
Bruh... An event needs a description, a start time after /from, and an end time after /to.
____________________________________________________________
____________________________________________________________
Bruh... An event needs a description, a start time after /from, and an end time after /to.
____________________________________________________________
____________________________________________________________
Here are the task(s) in your list:
____________________________________________________________
____________________________________________________________
Bye. I'm going to sleep.
____________________________________________________________
```

## Test case 25: Change the completion state of deadline and event tasks

**Aim:** Verify that `mark` and `unmark` preserve the type-specific details of deadline and event tasks.

**Command:** `java "-Dfile.encoding=UTF-8" "-Dstdout.encoding=UTF-8" -cp out cookie.Cookie`

**Inputs:**
```text
deadline submit report /by 2019-12-06 1700
event team meeting /from 2019-12-06 1400 /to 2019-12-06 1500
mark 1
mark 2
unmark 2
list
bye
```

**Expected output:**
```text
____________________________________________________________
 ██████╗ ██████╗  ██████╗ ██╗  ██╗██╗███████╗
██╔════╝██╔═══██╗██╔═══██╗██║ ██╔╝██║██╔════╝
██║     ██║   ██║██║   ██║█████╔╝ ██║█████╗  
██║     ██║   ██║██║   ██║██╔═██╗ ██║██╔══╝  
╚██████╗╚██████╔╝╚██████╔╝██║  ██╗██║███████╗
 ╚═════╝ ╚═════╝  ╚═════╝ ╚═╝  ╚═╝╚═╝╚══════╝
Hello! I'm your favourite chatbot Cookie.
What do you need today?
____________________________________________________________
____________________________________________________________
Ok. I've added this task:
   [D][ ] submit report (by: Dec 06 2019, 5:00 PM)
You have 1 task(s) now. Better start working.
____________________________________________________________
____________________________________________________________
Ok. I've added this task:
   [E][ ] team meeting (from: Dec 06 2019, 2:00 PM to: Dec 06 2019, 3:00 PM)
You have 2 task(s) now. Better start working.
____________________________________________________________
____________________________________________________________
Wow you actually got work done...
   [D][X] submit report (by: Dec 06 2019, 5:00 PM)
____________________________________________________________
____________________________________________________________
Wow you actually got work done...
   [E][X] team meeting (from: Dec 06 2019, 2:00 PM to: Dec 06 2019, 3:00 PM)
____________________________________________________________
____________________________________________________________
I can't believe you lied to me...
   [E][ ] team meeting (from: Dec 06 2019, 2:00 PM to: Dec 06 2019, 3:00 PM)
____________________________________________________________
____________________________________________________________
Here are the task(s) in your list:
1. [D][X] submit report (by: Dec 06 2019, 5:00 PM)
2. [E][ ] team meeting (from: Dec 06 2019, 2:00 PM to: Dec 06 2019, 3:00 PM)
____________________________________________________________
____________________________________________________________
Bye. I'm going to sleep.
____________________________________________________________
```

## Test case 26: Ignore surrounding and repeated whitespace

**Aim:** Verify that leading, trailing, and repeated whitespace around commands does not change the command or stored task description.

**Command:** `java "-Dfile.encoding=UTF-8" "-Dstdout.encoding=UTF-8" -cp out cookie.Cookie`

**Inputs:**
```text
  todo   buy milk  
  list  
bye
```

**Expected output:**
```text
____________________________________________________________
 ██████╗ ██████╗  ██████╗ ██╗  ██╗██╗███████╗
██╔════╝██╔═══██╗██╔═══██╗██║ ██╔╝██║██╔════╝
██║     ██║   ██║██║   ██║█████╔╝ ██║█████╗  
██║     ██║   ██║██║   ██║██╔═██╗ ██║██╔══╝  
╚██████╗╚██████╔╝╚██████╔╝██║  ██╗██║███████╗
 ╚═════╝ ╚═════╝  ╚═════╝ ╚═╝  ╚═╝╚═╝╚══════╝
Hello! I'm your favourite chatbot Cookie.
What do you need today?
____________________________________________________________
____________________________________________________________
Ok. I've added this task:
   [T][ ] buy milk
You have 1 task(s) now. Better start working.
____________________________________________________________
____________________________________________________________
Here are the task(s) in your list:
1. [T][ ] buy milk
____________________________________________________________
____________________________________________________________
Bye. I'm going to sleep.
____________________________________________________________
```

## Test case 27: Load tasks from disk

**Aim:** Verify that Cookie loads todo, deadline, and event tasks from the saved file, including their completion states and task-specific details.

**Saved data:**
```text
T | Done | finish report
D | Not Done | return book | 2019-12-02T18:00
E | Not Done | project meeting | 2019-12-02T14:00 to 2019-12-02T16:00
```

**Command:** `java "-Dfile.encoding=UTF-8" "-Dstdout.encoding=UTF-8" -cp out cookie.Cookie`

**Inputs:**
```text
list
bye
```

**Expected output:**
```text
____________________________________________________________
 ██████╗ ██████╗  ██████╗ ██╗  ██╗██╗███████╗
██╔════╝██╔═══██╗██╔═══██╗██║ ██╔╝██║██╔════╝
██║     ██║   ██║██║   ██║█████╔╝ ██║█████╗  
██║     ██║   ██║██║   ██║██╔═██╗ ██║██╔══╝  
╚██████╗╚██████╔╝╚██████╔╝██║  ██╗██║███████╗
 ╚═════╝ ╚═════╝  ╚═════╝ ╚═╝  ╚═╝╚═╝╚══════╝
Hello! I'm your favourite chatbot Cookie.
What do you need today?
____________________________________________________________
____________________________________________________________
Here are the task(s) in your list:
1. [T][X] finish report
2. [D][ ] return book (by: Dec 02 2019, 6:00 PM)
3. [E][ ] project meeting (from: Dec 02 2019, 2:00 PM to: Dec 02 2019, 4:00 PM)
____________________________________________________________
____________________________________________________________
Bye. I'm going to sleep.
____________________________________________________________
```

## Test case 28: Start without a data file or folder

**Aim:** Verify that Cookie starts with an empty task list when the data file and its parent folder do not exist, then creates them when a task is added.

**Command:** `java "-Dfile.encoding=UTF-8" "-Dstdout.encoding=UTF-8" -cp out cookie.Cookie`

**Inputs:**
```text
list
todo first task
bye
```

**Expected output:**
```text
____________________________________________________________
 ██████╗ ██████╗  ██████╗ ██╗  ██╗██╗███████╗
██╔════╝██╔═══██╗██╔═══██╗██║ ██╔╝██║██╔════╝
██║     ██║   ██║██║   ██║█████╔╝ ██║█████╗  
██║     ██║   ██║██║   ██║██╔═██╗ ██║██╔══╝  
╚██████╗╚██████╔╝╚██████╔╝██║  ██╗██║███████╗
 ╚═════╝ ╚═════╝  ╚═════╝ ╚═╝  ╚═╝╚═╝╚══════╝
Hello! I'm your favourite chatbot Cookie.
What do you need today?
____________________________________________________________
____________________________________________________________
Here are the task(s) in your list:
____________________________________________________________
____________________________________________________________
Ok. I've added this task:
   [T][ ] first task
You have 1 task(s) now. Better start working.
____________________________________________________________
____________________________________________________________
Bye. I'm going to sleep.
____________________________________________________________
```

## Test case 29: Recover valid records and warn about malformed records

**Aim:** Verify that malformed records are reported by line number while valid records continue loading.

**Saved data:**
```text
  T | Done | valid task  
invalid record
D | Maybe | bad | date
E | Not Done | meeting | 2019-12-02T14:00 to 2019-12-02T15:00
T | Not Done
D | Not Done | return book | 2019-12-02T18:00
T | Not Done | valid task
E | Not Done | backwards | 11:00 to 10:00
```

**Command:** `java "-Dfile.encoding=UTF-8" "-Dstdout.encoding=UTF-8" -cp out cookie.Cookie`

**Inputs:**
```text
list
bye
```

**Expected output:**
```text
____________________________________________________________
 ██████╗ ██████╗  ██████╗ ██╗  ██╗██╗███████╗
██╔════╝██╔═══██╗██╔═══██╗██║ ██╔╝██║██╔════╝
██║     ██║   ██║██║   ██║█████╔╝ ██║█████╗  
██║     ██║   ██║██║   ██║██╔═██╗ ██║██╔══╝  
╚██████╗╚██████╔╝╚██████╔╝██║  ██╗██║███████╗
 ╚═════╝ ╚═════╝  ╚═════╝ ╚═╝  ╚═╝╚═╝╚══════╝
Hello! I'm your favourite chatbot Cookie.
What do you need today?
____________________________________________________________
____________________________________________________________
Heads up! I skipped malformed saved task records on lines 2, 3, 5, 7, 8. Your valid tasks were still loaded.
____________________________________________________________
____________________________________________________________
Here are the task(s) in your list:
1. [T][X] valid task
2. [E][ ] meeting (from: Dec 02 2019, 2:00 PM to: Dec 02 2019, 3:00 PM)
3. [D][ ] return book (by: Dec 02 2019, 6:00 PM)
____________________________________________________________
____________________________________________________________
Bye. I'm going to sleep.
____________________________________________________________
```

## Test case 30: Reject duplicate tasks and invalid event periods

**Aim:** Verify that duplicate task details, non-increasing event periods, and mismatched temporal values are rejected without changing the task list.

**Command:** `java "-Dfile.encoding=UTF-8" "-Dstdout.encoding=UTF-8" -cp out cookie.Cookie`

**Inputs:**
```text
todo buy milk
todo buy milk
event same time /from 1000 /to 1000
event backwards /from 1100 /to 1000
event mixed values /from 2019-12-07 /to 1000
list
bye
```

**Expected output:**
```text
____________________________________________________________
 ██████╗ ██████╗  ██████╗ ██╗  ██╗██╗███████╗
██╔════╝██╔═══██╗██╔═══██╗██║ ██╔╝██║██╔════╝
██║     ██║   ██║██║   ██║█████╔╝ ██║█████╗  
██║     ██║   ██║██║   ██║██╔═██╗ ██║██╔══╝  
╚██████╗╚██████╔╝╚██████╔╝██║  ██╗██║███████╗
 ╚═════╝ ╚═════╝  ╚═════╝ ╚═╝  ╚═╝╚═╝╚══════╝
Hello! I'm your favourite chatbot Cookie.
What do you need today?
____________________________________________________________
____________________________________________________________
Ok. I've added this task:
   [T][ ] buy milk
You have 1 task(s) now. Better start working.
____________________________________________________________
____________________________________________________________
Bruh... That task already exists.
____________________________________________________________
____________________________________________________________
Bruh... An event's end must be later than its start.
____________________________________________________________
____________________________________________________________
Bruh... An event's end must be later than its start.
____________________________________________________________
____________________________________________________________
Bruh... An event's start and end must both be dates, both be times, or both be dates and times.
____________________________________________________________
____________________________________________________________
Here are the task(s) in your list:
1. [T][ ] buy milk
____________________________________________________________
____________________________________________________________
Bye. I'm going to sleep.
____________________________________________________________
```

## Test case 31: Reject repeated command parameters

**Aim:** Verify that task commands reject repeated `/by`, `/from`, and `/to` parameters without adding tasks.

**Command:** `java "-Dfile.encoding=UTF-8" "-Dstdout.encoding=UTF-8" -cp out cookie.Cookie`

**Inputs:**
```text
deadline report /by 2019-12-07 /by 2019-12-08
event meeting /from 0900 /from 0930 /to 1000
event meeting /from 0900 /to 1000 /to 1100
list
bye
```

**Expected output:**
```text
____________________________________________________________
 ██████╗ ██████╗  ██████╗ ██╗  ██╗██╗███████╗
██╔════╝██╔═══██╗██╔═══██╗██║ ██╔╝██║██╔════╝
██║     ██║   ██║██║   ██║█████╔╝ ██║█████╗  
██║     ██║   ██║██║   ██║██╔═██╗ ██║██╔══╝  
╚██████╗╚██████╔╝╚██████╔╝██║  ██╗██║███████╗
 ╚═════╝ ╚═════╝  ╚═════╝ ╚═╝  ╚═╝╚═╝╚══════╝
Hello! I'm your favourite chatbot Cookie.
What do you need today?
____________________________________________________________
____________________________________________________________
Bruh... A deadline must contain exactly one /by parameter.
____________________________________________________________
____________________________________________________________
Bruh... An event must contain exactly one /from and one /to parameter.
____________________________________________________________
____________________________________________________________
Bruh... An event must contain exactly one /from and one /to parameter.
____________________________________________________________
____________________________________________________________
Here are the task(s) in your list:
____________________________________________________________
____________________________________________________________
Bye. I'm going to sleep.
____________________________________________________________
```

## Test case 32: Reject the file delimiter in task details

**Aim:** Verify that task details containing the file delimiter are rejected instead of creating records that cannot be loaded reliably.

**Command:** `java "-Dfile.encoding=UTF-8" "-Dstdout.encoding=UTF-8" -cp out cookie.Cookie`

**Inputs:**
```text
todo task | with delimiter
deadline return book /by 2/12/2019 1800 | night
event team meeting /from 2019-12-02 1400 | Tue 2pm /to 2019-12-02 1600
list
bye
```

**Expected output:**
```text
____________________________________________________________
 ██████╗ ██████╗  ██████╗ ██╗  ██╗██╗███████╗
██╔════╝██╔═══██╗██╔═══██╗██║ ██╔╝██║██╔════╝
██║     ██║   ██║██║   ██║█████╔╝ ██║█████╗  
██║     ██║   ██║██║   ██║██╔═██╗ ██║██╔══╝  
╚██████╗╚██████╔╝╚██████╔╝██║  ██╗██║███████╗
 ╚═════╝ ╚═════╝  ╚═════╝ ╚═╝  ╚═╝╚═╝╚══════╝
Hello! I'm your favourite chatbot Cookie.
What do you need today?
____________________________________________________________
____________________________________________________________
Bruh... Task details cannot contain '|'.
____________________________________________________________
____________________________________________________________
Bruh... Task details cannot contain '|'.
____________________________________________________________
____________________________________________________________
Bruh... Task details cannot contain '|'.
____________________________________________________________
____________________________________________________________
Here are the task(s) in your list:
____________________________________________________________
____________________________________________________________
Bye. I'm going to sleep.
____________________________________________________________
```

## Test case 33: Load an empty file with blank lines

**Aim:** Verify that an empty or whitespace-only saved record is ignored while valid records can still be loaded.

**Saved data:**
```text

   

T | Not Done | keep task

```

**Command:** `java "-Dfile.encoding=UTF-8" "-Dstdout.encoding=UTF-8" -cp out cookie.Cookie`

**Inputs:**
```text
list
bye
```

**Expected output:**
```text
____________________________________________________________
 ██████╗ ██████╗  ██████╗ ██╗  ██╗██╗███████╗
██╔════╝██╔═══██╗██╔═══██╗██║ ██╔╝██║██╔════╝
██║     ██║   ██║██║   ██║█████╔╝ ██║█████╗  
██║     ██║   ██║██║   ██║██╔═██╗ ██║██╔══╝  
╚██████╗╚██████╔╝╚██████╔╝██║  ██╗██║███████╗
 ╚═════╝ ╚═════╝  ╚═════╝ ╚═╝  ╚═╝╚═╝╚══════╝
Hello! I'm your favourite chatbot Cookie.
What do you need today?
____________________________________________________________
____________________________________________________________
Here are the task(s) in your list:
1. [T][ ] keep task
____________________________________________________________
____________________________________________________________
Bye. I'm going to sleep.
____________________________________________________________
```

## Test case 34: Save and load tasks across a restart

**Aim:** Verify that a task added and marked in one Cookie process is restored with its completion state after restarting Cookie.

**Command:** `java "-Dfile.encoding=UTF-8" "-Dstdout.encoding=UTF-8" -cp out cookie.Cookie`

**Inputs:**
```text
todo round trip task
mark 1
bye
```

**Expected output:**
```text
____________________________________________________________
 ██████╗ ██████╗  ██████╗ ██╗  ██╗██╗███████╗
██╔════╝██╔═══██╗██╔═══██╗██║ ██╔╝██║██╔════╝
██║     ██║   ██║██║   ██║█████╔╝ ██║█████╗  
██║     ██║   ██║██║   ██║██╔═██╗ ██║██╔══╝  
╚██████╗╚██████╔╝╚██████╔╝██║  ██╗██║███████╗
 ╚═════╝ ╚═════╝  ╚═════╝ ╚═╝  ╚═╝╚═╝╚══════╝
Hello! I'm your favourite chatbot Cookie.
What do you need today?
____________________________________________________________
____________________________________________________________
Ok. I've added this task:
   [T][ ] round trip task
You have 1 task(s) now. Better start working.
____________________________________________________________
____________________________________________________________
Wow you actually got work done...
   [T][X] round trip task
____________________________________________________________
____________________________________________________________
Bye. I'm going to sleep.
____________________________________________________________
```

**Restart inputs:**
```text
list
bye
```

**Expected restart output:**
```text
____________________________________________________________
 ██████╗ ██████╗  ██████╗ ██╗  ██╗██╗███████╗
██╔════╝██╔═══██╗██╔═══██╗██║ ██╔╝██║██╔════╝
██║     ██║   ██║██║   ██║█████╔╝ ██║█████╗  
██║     ██║   ██║██║   ██║██╔═██╗ ██║██╔══╝  
╚██████╗╚██████╔╝╚██████╔╝██║  ██╗██║███████╗
 ╚═════╝ ╚═════╝  ╚═════╝ ╚═╝  ╚═╝╚═╝╚══════╝
Hello! I'm your favourite chatbot Cookie.
What do you need today?
____________________________________________________________
____________________________________________________________
Here are the task(s) in your list:
1. [T][X] round trip task
____________________________________________________________
____________________________________________________________
Bye. I'm going to sleep.
____________________________________________________________
```

## Test case 35: Save and load a deadline date-time

**Aim:** Verify that a deadline date-time is saved in a canonical format and restored with the same readable display after restarting Cookie.

**Command:** `java "-Dfile.encoding=UTF-8" "-Dstdout.encoding=UTF-8" -cp out cookie.Cookie`

**Inputs:**
```text
deadline submit report /by 2019-12-06 1700
bye
```

**Expected output:**
```text
____________________________________________________________
 ██████╗ ██████╗  ██████╗ ██╗  ██╗██╗███████╗
██╔════╝██╔═══██╗██╔═══██╗██║ ██╔╝██║██╔════╝
██║     ██║   ██║██║   ██║█████╔╝ ██║█████╗  
██║     ██║   ██║██║   ██║██╔═██╗ ██║██╔══╝  
╚██████╗╚██████╔╝╚██████╔╝██║  ██╗██║███████╗
 ╚═════╝ ╚═════╝  ╚═════╝ ╚═╝  ╚═╝╚═╝╚══════╝
Hello! I'm your favourite chatbot Cookie.
What do you need today?
____________________________________________________________
____________________________________________________________
Ok. I've added this task:
   [D][ ] submit report (by: Dec 06 2019, 5:00 PM)
You have 1 task(s) now. Better start working.
____________________________________________________________
____________________________________________________________
Bye. I'm going to sleep.
____________________________________________________________
```

## Test case 36: Save and load an event date-time

**Aim:** Verify that an event's date-time interval is saved and restored with the same readable display after restarting Cookie.

**Command:** `java "-Dfile.encoding=UTF-8" "-Dstdout.encoding=UTF-8" -cp out cookie.Cookie`

**Inputs:**
```text
event project meeting /from 2019-12-02 1400 /to 2019-12-02 1600
bye
```

**Expected output:**
```text
____________________________________________________________
 ██████╗ ██████╗  ██████╗ ██╗  ██╗██╗███████╗
██╔════╝██╔═══██╗██╔═══██╗██║ ██╔╝██║██╔════╝
██║     ██║   ██║██║   ██║█████╔╝ ██║█████╗  
██║     ██║   ██║██║   ██║██╔═██╗ ██║██╔══╝  
╚██████╗╚██████╔╝╚██████╔╝██║  ██╗██║███████╗
 ╚═════╝ ╚═════╝  ╚═════╝ ╚═╝  ╚═╝╚═╝╚══════╝
Hello! I'm your favourite chatbot Cookie.
What do you need today?
____________________________________________________________
____________________________________________________________
Ok. I've added this task:
   [E][ ] project meeting (from: Dec 02 2019, 2:00 PM to: Dec 02 2019, 4:00 PM)
You have 1 task(s) now. Better start working.
____________________________________________________________
____________________________________________________________
Bye. I'm going to sleep.
____________________________________________________________
```

**Restart inputs:**
```text
list
bye
```

**Expected restart output:**
```text
____________________________________________________________
 ██████╗ ██████╗  ██████╗ ██╗  ██╗██╗███████╗
██╔════╝██╔═══██╗██╔═══██╗██║ ██╔╝██║██╔════╝
██║     ██║   ██║██║   ██║█████╔╝ ██║█████╗  
██║     ██║   ██║██║   ██║██╔═██╗ ██║██╔══╝  
╚██████╗╚██████╔╝╚██████╔╝██║  ██╗██║███████╗
 ╚═════╝ ╚═════╝  ╚═════╝ ╚═╝  ╚═╝╚═╝╚══════╝
Hello! I'm your favourite chatbot Cookie.
What do you need today?
____________________________________________________________
____________________________________________________________
Here are the task(s) in your list:
1. [E][ ] project meeting (from: Dec 02 2019, 2:00 PM to: Dec 02 2019, 4:00 PM)
____________________________________________________________
____________________________________________________________
Bye. I'm going to sleep.
____________________________________________________________
```

## Test case 37: List tasks occurring on a date

**Aim:** Verify that `on` lists deadlines on the requested date and events whose intervals overlap that date, while preserving original task numbers.

**Command:** `java "-Dfile.encoding=UTF-8" "-Dstdout.encoding=UTF-8" -cp out cookie.Cookie`

**Inputs:**
```text
deadline submit report /by 2019-12-06 1700
deadline next report /by 2019-12-07 0900
event team meeting /from 2019-12-06 1400 /to 2019-12-06 1600
event overnight deployment /from 2019-12-05 2300 /to 2019-12-06 0100
on 2019-12-06
on 2019-12-08
bye
```

**Expected output:**
```text
____________________________________________________________
 ██████╗ ██████╗  ██████╗ ██╗  ██╗██╗███████╗
██╔════╝██╔═══██╗██╔═══██╗██║ ██╔╝██║██╔════╝
██║     ██║   ██║██║   ██║█████╔╝ ██║█████╗  
██║     ██║   ██║██║   ██║██╔═██╗ ██║██╔══╝  
╚██████╗╚██████╔╝╚██████╔╝██║  ██╗██║███████╗
 ╚═════╝ ╚═════╝  ╚═════╝ ╚═╝  ╚═╝╚═╝╚══════╝
Hello! I'm your favourite chatbot Cookie.
What do you need today?
____________________________________________________________
____________________________________________________________
Ok. I've added this task:
   [D][ ] submit report (by: Dec 06 2019, 5:00 PM)
You have 1 task(s) now. Better start working.
____________________________________________________________
____________________________________________________________
Ok. I've added this task:
   [D][ ] next report (by: Dec 07 2019, 9:00 AM)
You have 2 task(s) now. Better start working.
____________________________________________________________
____________________________________________________________
Ok. I've added this task:
   [E][ ] team meeting (from: Dec 06 2019, 2:00 PM to: Dec 06 2019, 4:00 PM)
You have 3 task(s) now. Better start working.
____________________________________________________________
____________________________________________________________
Ok. I've added this task:
   [E][ ] overnight deployment (from: Dec 05 2019, 11:00 PM to: Dec 06 2019, 1:00 AM)
You have 4 task(s) now. Better start working.
____________________________________________________________
____________________________________________________________
Here are the task(s) on Dec 06 2019:
1. [D][ ] submit report (by: Dec 06 2019, 5:00 PM)
3. [E][ ] team meeting (from: Dec 06 2019, 2:00 PM to: Dec 06 2019, 4:00 PM)
4. [E][ ] overnight deployment (from: Dec 05 2019, 11:00 PM to: Dec 06 2019, 1:00 AM)
____________________________________________________________
____________________________________________________________
Here are the task(s) on Dec 08 2019:
____________________________________________________________
____________________________________________________________
Bye. I'm going to sleep.
____________________________________________________________
```

## Test case 38: Reject invalid event and date-query values

**Aim:** Verify that malformed event date-times and invalid `on` dates are rejected without changing the task list.

**Command:** `java "-Dfile.encoding=UTF-8" "-Dstdout.encoding=UTF-8" -cp out cookie.Cookie`

**Inputs:**
```text
event invalid meeting /from 2019-02-30 1400 /to 2019-02-30 1600
on 2019-02-30
on
list
bye
```

**Expected output:**
```text
____________________________________________________________
 ██████╗ ██████╗  ██████╗ ██╗  ██╗██╗███████╗
██╔════╝██╔═══██╗██╔═══██╗██║ ██╔╝██║██╔════╝
██║     ██║   ██║██║   ██║█████╔╝ ██║█████╗  
██║     ██║   ██║██║   ██║██╔═██╗ ██║██╔══╝  
╚██████╗╚██████╔╝╚██████╔╝██║  ██╗██║███████╗
 ╚═════╝ ╚═════╝  ╚═════╝ ╚═╝  ╚═╝╚═╝╚══════╝
Hello! I'm your favourite chatbot Cookie.
What do you need today?
____________________________________________________________
____________________________________________________________
Bruh... An event's start and end values must use yyyy-MM-dd, d/M/yyyy, HHmm, yyyy-MM-dd HHmm, or d/M/yyyy HHmm.
____________________________________________________________
____________________________________________________________
Bruh... A date must use yyyy-MM-dd or d/M/yyyy.
____________________________________________________________
____________________________________________________________
Bruh... Usage: on <date>.
____________________________________________________________
____________________________________________________________
Here are the task(s) in your list:
____________________________________________________________
____________________________________________________________
Bye. I'm going to sleep.
____________________________________________________________
```

## Test case 39: Reject invalid deadline date-times

**Aim:** Verify that invalid calendar dates, invalid times, and unsupported date separators are rejected without adding tasks.

**Command:** `java "-Dfile.encoding=UTF-8" "-Dstdout.encoding=UTF-8" -cp out cookie.Cookie`

**Inputs:**
```text
deadline invalid date /by 2019-02-30 1800
deadline invalid time /by 2019-12-02 2460
deadline invalid format /by 2019/12/02 1800
list
bye
```

**Expected output:**
```text
____________________________________________________________
 ██████╗ ██████╗  ██████╗ ██╗  ██╗██╗███████╗
██╔════╝██╔═══██╗██╔═══██╗██║ ██╔╝██║██╔════╝
██║     ██║   ██║██║   ██║█████╔╝ ██║█████╗  
██║     ██║   ██║██║   ██║██╔═██╗ ██║██╔══╝  
╚██████╗╚██████╔╝╚██████╔╝██║  ██╗██║███████╗
 ╚═════╝ ╚═════╝  ╚═════╝ ╚═╝  ╚═╝╚═╝╚══════╝
Hello! I'm your favourite chatbot Cookie.
What do you need today?
____________________________________________________________
____________________________________________________________
Bruh... A deadline date, time, or date and time must use yyyy-MM-dd, d/M/yyyy, HHmm, yyyy-MM-dd HHmm, or d/M/yyyy HHmm.
____________________________________________________________
____________________________________________________________
Bruh... A deadline date, time, or date and time must use yyyy-MM-dd, d/M/yyyy, HHmm, yyyy-MM-dd HHmm, or d/M/yyyy HHmm.
____________________________________________________________
____________________________________________________________
Bruh... A deadline date, time, or date and time must use yyyy-MM-dd, d/M/yyyy, HHmm, yyyy-MM-dd HHmm, or d/M/yyyy HHmm.
____________________________________________________________
____________________________________________________________
Here are the task(s) in your list:
____________________________________________________________
____________________________________________________________
Bye. I'm going to sleep.
____________________________________________________________
```

## Test case 40: Accept optional date and time components

**Aim:** Verify that deadlines and events accept date-only, time-only, and combined date-time values.

**Command:** `java "-Dfile.encoding=UTF-8" "-Dstdout.encoding=UTF-8" -cp out cookie.Cookie`

**Inputs:**
```text
deadline submit report /by 2019-12-06
deadline submit invoice /by 1800
event project days /from 2019-12-07 /to 2019-12-08
event project hour /from 0900 /to 1000
list
bye
```

**Expected output:**
```text
____________________________________________________________
 ██████╗ ██████╗  ██████╗ ██╗  ██╗██╗███████╗
██╔════╝██╔═══██╗██╔═══██╗██║ ██╔╝██║██╔════╝
██║     ██║   ██║██║   ██║█████╔╝ ██║█████╗  
██║     ██║   ██║██║   ██║██╔═██╗ ██║██╔══╝  
╚██████╗╚██████╔╝╚██████╔╝██║  ██╗██║███████╗
 ╚═════╝ ╚═════╝  ╚═════╝ ╚═╝  ╚═╝╚═╝╚══════╝
Hello! I'm your favourite chatbot Cookie.
What do you need today?
____________________________________________________________
____________________________________________________________
Ok. I've added this task:
   [D][ ] submit report (by: Dec 06 2019)
You have 1 task(s) now. Better start working.
____________________________________________________________
____________________________________________________________
Ok. I've added this task:
   [D][ ] submit invoice (by: 6:00 PM)
You have 2 task(s) now. Better start working.
____________________________________________________________
____________________________________________________________
Ok. I've added this task:
   [E][ ] project days (from: Dec 07 2019 to: Dec 08 2019)
You have 3 task(s) now. Better start working.
____________________________________________________________
____________________________________________________________
Ok. I've added this task:
   [E][ ] project hour (from: 9:00 AM to: 10:00 AM)
You have 4 task(s) now. Better start working.
____________________________________________________________
____________________________________________________________
Here are the task(s) in your list:
1. [D][ ] submit report (by: Dec 06 2019)
2. [D][ ] submit invoice (by: 6:00 PM)
3. [E][ ] project days (from: Dec 07 2019 to: Dec 08 2019)
4. [E][ ] project hour (from: 9:00 AM to: 10:00 AM)
____________________________________________________________
____________________________________________________________
Bye. I'm going to sleep.
____________________________________________________________
```

## Test case 41: Find tasks by keyword

**Aim:** Verify that `find` displays tasks whose descriptions contain the keyword, preserves their original task numbers, and includes matching tasks of different types.

**Command:** `java "-Dfile.encoding=UTF-8" "-Dstdout.encoding=UTF-8" -cp out cookie.Cookie`

**Inputs:**
```text
todo read book
deadline return book /by 2019-06-06
todo buy groceries
find book
bye
```

**Expected output:**
```text
____________________________________________________________
 ██████╗ ██████╗  ██████╗ ██╗  ██╗██╗███████╗
██╔════╝██╔═══██╗██╔═══██╗██║ ██╔╝██║██╔════╝
██║     ██║   ██║██║   ██║█████╔╝ ██║█████╗  
██║     ██║   ██║██║   ██║██╔═██╗ ██║██╔══╝  
╚██████╗╚██████╔╝╚██████╔╝██║  ██╗██║███████╗
 ╚═════╝ ╚═════╝  ╚═════╝ ╚═╝  ╚═╝╚═╝╚══════╝
Hello! I'm your favourite chatbot Cookie.
What do you need today?
____________________________________________________________
____________________________________________________________
Ok. I've added this task:
   [T][ ] read book
You have 1 task(s) now. Better start working.
____________________________________________________________
____________________________________________________________
Ok. I've added this task:
   [D][ ] return book (by: Jun 06 2019)
You have 2 task(s) now. Better start working.
____________________________________________________________
____________________________________________________________
Ok. I've added this task:
   [T][ ] buy groceries
You have 3 task(s) now. Better start working.
____________________________________________________________
____________________________________________________________
Here are the matching tasks in your list:
1. [T][ ] read book
2. [D][ ] return book (by: Jun 06 2019)
____________________________________________________________
____________________________________________________________
Bye. I'm going to sleep.
____________________________________________________________
```

## Test case 42: Reject invalid find arguments

**Aim:** Verify that `find` requires exactly one keyword and that invalid commands do not change the task list.

**Command:** `java "-Dfile.encoding=UTF-8" "-Dstdout.encoding=UTF-8" -cp out cookie.Cookie`

**Inputs:**
```text
find
find book now
list
bye
```

**Expected output:**
```text
____________________________________________________________
 ██████╗ ██████╗  ██████╗ ██╗  ██╗██╗███████╗
██╔════╝██╔═══██╗██╔═══██╗██║ ██╔╝██║██╔════╝
██║     ██║   ██║██║   ██║█████╔╝ ██║█████╗  
██║     ██║   ██║██║   ██║██╔═██╗ ██║██╔══╝  
╚██████╗╚██████╔╝╚██████╔╝██║  ██╗██║███████╗
 ╚═════╝ ╚═════╝  ╚═════╝ ╚═╝  ╚═╝╚═╝╚══════╝
Hello! I'm your favourite chatbot Cookie.
What do you need today?
____________________________________________________________
____________________________________________________________
Bruh... Usage: find <keyword>.
____________________________________________________________
____________________________________________________________
Bruh... Usage: find <keyword>.
____________________________________________________________
____________________________________________________________
Here are the task(s) in your list:
____________________________________________________________
____________________________________________________________
Bye. I'm going to sleep.
____________________________________________________________
```

## Test case 43: Sort descriptions without changing stored order

**Aim:** Verify that description sorting is case-insensitive, preserves original task numbers, allows a displayed task number to be marked, and does not persist the sorted order.

**Saved data:**
```text
T | Not Done | zebra
T | Not Done | apple
T | Not Done | Banana
```

**Command:** `java "-Dfile.encoding=UTF-8" "-Dstdout.encoding=UTF-8" -cp out cookie.Cookie`

**Inputs:**
```text
sort description
mark 2
list
bye
```

**Expected output:**
```text
____________________________________________________________
 ██████╗ ██████╗  ██████╗ ██╗  ██╗██╗███████╗
██╔════╝██╔═══██╗██╔═══██╗██║ ██╔╝██║██╔════╝
██║     ██║   ██║██║   ██║█████╔╝ ██║█████╗  
██║     ██║   ██║██║   ██║██╔═██╗ ██║██╔══╝  
╚██████╗╚██████╔╝╚██████╔╝██║  ██╗██║███████╗
 ╚═════╝ ╚═════╝  ╚═════╝ ╚═╝  ╚═╝╚═╝╚══════╝
Hello! I'm your favourite chatbot Cookie.
What do you need today?
____________________________________________________________
____________________________________________________________
Here are the task(s) sorted by description (ascending):
2. [T][ ] apple
3. [T][ ] Banana
1. [T][ ] zebra
____________________________________________________________
____________________________________________________________
Wow you actually got work done...
   [T][X] apple
____________________________________________________________
____________________________________________________________
Here are the task(s) in your list:
1. [T][ ] zebra
2. [T][X] apple
3. [T][ ] Banana
____________________________________________________________
____________________________________________________________
Bye. I'm going to sleep.
____________________________________________________________
```

**Restart inputs:**
```text
list
bye
```

**Expected restart output:**
```text
____________________________________________________________
 ██████╗ ██████╗  ██████╗ ██╗  ██╗██╗███████╗
██╔════╝██╔═══██╗██╔═══██╗██║ ██╔╝██║██╔════╝
██║     ██║   ██║██║   ██║█████╔╝ ██║█████╗  
██║     ██║   ██║██║   ██║██╔═██╗ ██║██╔══╝  
╚██████╗╚██████╔╝╚██████╔╝██║  ██╗██║███████╗
 ╚═════╝ ╚═════╝  ╚═════╝ ╚═╝  ╚═╝╚═╝╚══════╝
Hello! I'm your favourite chatbot Cookie.
What do you need today?
____________________________________________________________
____________________________________________________________
Here are the task(s) in your list:
1. [T][ ] zebra
2. [T][X] apple
3. [T][ ] Banana
____________________________________________________________
____________________________________________________________
Bye. I'm going to sleep.
____________________________________________________________
```

## Test case 44: Sort mixed temporal values in both directions

**Aim:** Verify that date sorting compares deadline due values and event start values, groups dated, time-only, and undated tasks, and keeps original task numbers.

**Saved data:**
```text
T | Not Done | undated
D | Not Done | earlier deadline | 2019-12-06
E | Not Done | later event | 2019-12-07T09:00 to 2019-12-07T10:00
D | Not Done | evening deadline | 18:00
E | Not Done | morning event | 09:00 to 10:00
```

**Command:** `java "-Dfile.encoding=UTF-8" "-Dstdout.encoding=UTF-8" -cp out cookie.Cookie`

**Inputs:**
```text
sort date
sort date desc
bye
```

**Expected output:**
```text
____________________________________________________________
 ██████╗ ██████╗  ██████╗ ██╗  ██╗██╗███████╗
██╔════╝██╔═══██╗██╔═══██╗██║ ██╔╝██║██╔════╝
██║     ██║   ██║██║   ██║█████╔╝ ██║█████╗  
██║     ██║   ██║██║   ██║██╔═██╗ ██║██╔══╝  
╚██████╗╚██████╔╝╚██████╔╝██║  ██╗██║███████╗
 ╚═════╝ ╚═════╝  ╚═════╝ ╚═╝  ╚═╝╚═╝╚══════╝
Hello! I'm your favourite chatbot Cookie.
What do you need today?
____________________________________________________________
____________________________________________________________
Here are the task(s) sorted by date (ascending):
2. [D][ ] earlier deadline (by: Dec 06 2019)
3. [E][ ] later event (from: Dec 07 2019, 9:00 AM to: Dec 07 2019, 10:00 AM)
5. [E][ ] morning event (from: 9:00 AM to: 10:00 AM)
4. [D][ ] evening deadline (by: 6:00 PM)
1. [T][ ] undated
____________________________________________________________
____________________________________________________________
Here are the task(s) sorted by date (descending):
3. [E][ ] later event (from: Dec 07 2019, 9:00 AM to: Dec 07 2019, 10:00 AM)
2. [D][ ] earlier deadline (by: Dec 06 2019)
4. [D][ ] evening deadline (by: 6:00 PM)
5. [E][ ] morning event (from: 9:00 AM to: 10:00 AM)
1. [T][ ] undated
____________________________________________________________
____________________________________________________________
Bye. I'm going to sleep.
____________________________________________________________
```

## Test case 45: Reject invalid sort arguments

**Aim:** Verify that missing, unsupported, and extra sort arguments report the command usage without changing the stored task list.

**Saved data:**
```text
T | Not Done | keep task
```

**Command:** `java "-Dfile.encoding=UTF-8" "-Dstdout.encoding=UTF-8" -cp out cookie.Cookie`

**Inputs:**
```text
sort
sort status
sort date descending
sort description asc extra
list
bye
```

**Expected output:**
```text
____________________________________________________________
 ██████╗ ██████╗  ██████╗ ██╗  ██╗██╗███████╗
██╔════╝██╔═══██╗██╔═══██╗██║ ██╔╝██║██╔════╝
██║     ██║   ██║██║   ██║█████╔╝ ██║█████╗  
██║     ██║   ██║██║   ██║██╔═██╗ ██║██╔══╝  
╚██████╗╚██████╔╝╚██████╔╝██║  ██╗██║███████╗
 ╚═════╝ ╚═════╝  ╚═════╝ ╚═╝  ╚═╝╚═╝╚══════╝
Hello! I'm your favourite chatbot Cookie.
What do you need today?
____________________________________________________________
____________________________________________________________
Bruh... Usage: sort <description|date> [asc|desc].
____________________________________________________________
____________________________________________________________
Bruh... Usage: sort <description|date> [asc|desc].
____________________________________________________________
____________________________________________________________
Bruh... Usage: sort <description|date> [asc|desc].
____________________________________________________________
____________________________________________________________
Bruh... Usage: sort <description|date> [asc|desc].
____________________________________________________________
____________________________________________________________
Here are the task(s) in your list:
1. [T][ ] keep task
____________________________________________________________
____________________________________________________________
Bye. I'm going to sleep.
____________________________________________________________
```

## Test case 46: Sort empty and single-item lists

**Aim:** Verify that sorting an empty list and a single-item list succeeds, including case-insensitive criterion and direction arguments.

**Command:** `java "-Dfile.encoding=UTF-8" "-Dstdout.encoding=UTF-8" -cp out cookie.Cookie`

**Inputs:**
```text
sort description
todo only task
SoRt DeScRiPtIoN DeSc
bye
```

**Expected output:**
```text
____________________________________________________________
 ██████╗ ██████╗  ██████╗ ██╗  ██╗██╗███████╗
██╔════╝██╔═══██╗██╔═══██╗██║ ██╔╝██║██╔════╝
██║     ██║   ██║██║   ██║█████╔╝ ██║█████╗  
██║     ██║   ██║██║   ██║██╔═██╗ ██║██╔══╝  
╚██████╗╚██████╔╝╚██████╔╝██║  ██╗██║███████╗
 ╚═════╝ ╚═════╝  ╚═════╝ ╚═╝  ╚═╝╚═╝╚══════╝
Hello! I'm your favourite chatbot Cookie.
What do you need today?
____________________________________________________________
____________________________________________________________
Here are the task(s) sorted by description (ascending):
____________________________________________________________
____________________________________________________________
Ok. I've added this task:
   [T][ ] only task
You have 1 task(s) now. Better start working.
____________________________________________________________
____________________________________________________________
Here are the task(s) sorted by description (descending):
1. [T][ ] only task
____________________________________________________________
____________________________________________________________
Bye. I'm going to sleep.
____________________________________________________________
```

## Test case 47: Normalize repeated whitespace inside task details

**Aim:** Verify that repeated whitespace inside a task description is normalized and does not create visually distinct duplicate details.

**Command:** `java "-Dfile.encoding=UTF-8" "-Dstdout.encoding=UTF-8" -cp out cookie.Cookie`

**Inputs:**
```text
todo buy    milk
list
bye
```

**Expected output:**
```text
____________________________________________________________
 ██████╗ ██████╗  ██████╗ ██╗  ██╗██╗███████╗
██╔════╝██╔═══██╗██╔═══██╗██║ ██╔╝██║██╔════╝
██║     ██║   ██║██║   ██║█████╔╝ ██║█████╗  
██║     ██║   ██║██║   ██║██╔═██╗ ██║██╔══╝  
╚██████╗╚██████╔╝╚██████╔╝██║  ██╗██║███████╗
 ╚═════╝ ╚═════╝  ╚═════╝ ╚═╝  ╚═╝╚═╝╚══════╝
Hello! I'm your favourite chatbot Cookie.
What do you need today?
____________________________________________________________
____________________________________________________________
Ok. I've added this task:
   [T][ ] buy milk
You have 1 task(s) now. Better start working.
____________________________________________________________
____________________________________________________________
Here are the task(s) in your list:
1. [T][ ] buy milk
____________________________________________________________
____________________________________________________________
Bye. I'm going to sleep.
____________________________________________________________
```
