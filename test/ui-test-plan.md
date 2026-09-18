# UI test plan

## Notes

- Task lists display a space after each task number, for example `1. [T][ ] groceries`.

- Run this plan from the repository root with Java 25 selected: `sdk use java 25.0.3.fx-zulu`, then `python3 .codex/skills/test-ui/scripts/run_ui_tests.py`.
- If `sdk` is unavailable in Bash on macOS, run the plan through Zsh instead: `zsh -lic 'sdk use java 25.0.3.fx-zulu && cd /Users/tat/NUS/ip && python3 .codex/skills/test-ui/scripts/run_ui_tests.py'`.
- Each test command compiles the current Java source before running it, so the test checks the version currently in the working tree.
- Output comparisons are exact, including spaces and blank lines.
- The task status, task type, and command classification enums must preserve the existing task markers and user-facing messages.
- Successful task additions, deletions, marks, and unmarks write the current list to `data/oreo.txt`; startup loads valid records and ignores malformed records.
- Persistence failures are reported on stderr while the chatbot continues running, so UI output remains stable.
- The existing end-to-end cases cover the greeting and goodbye messages now provided by the Ui class.
- GUI-specific checks are performed manually because this plan validates the command-line interface.
- The packaged JAR launches the JavaFX GUI directly; the CLI tests continue to invoke `oreo.Oreo` explicitly.

## Test cases

### Test: Manage merchandise details

**Aim:** Confirm that merchandise can be added, listed separately, searched, edited, and deleted without changing normal task listing or search behaviour.

**Command:**
```sh
rm -f data/oreo.txt && javac -d /tmp/oreo-ui-test-classes $(find src/main/java -name '*.java') && java -cp /tmp/oreo-ui-test-classes oreo.Oreo
```

**Input:**
```text
todo Sell house
merchandise 1 4-room flat at Bishan
list
listall
find house
find-merchandise bishan
edit-merchandise 1 Updated property details
delete-merchandise 1
bye
```

**Expected output:**
```text
____________________________________________ 
  OOO   RRRR   EEEEE  OOO  
 O   O  R   R  E     O   O 
 O   O  RRRR   EEEE  O   O 
 O   O  R R    E     O   O 
  OOO   R  RR  EEEEE  OOO  

Hello! I'm Oreo. 
Let's get started shall we? 
____________________________________________
____________________________________________
Got it. I've added this task:
[T][ ] Sell house
Now you have 1 tasks in the list.
____________________________________________
____________________________________________
Got it. I've added merchandise to this task:
[T][ ] Sell house
   Merchandise: 4-room flat at Bishan
____________________________________________
____________________________________________
Here are the tasks in your list:
1. [T][ ] Sell house
____________________________________________
____________________________________________
Here are all tasks and their merchandise:
1. [T][ ] Sell house
   Merchandise: 4-room flat at Bishan
____________________________________________
____________________________________________
Here are the matching tasks in your list:
1. [T][ ] Sell house
____________________________________________
____________________________________________
Here are the matching merchandise in your list:
1. [T][ ] Sell house
   Merchandise: 4-room flat at Bishan
____________________________________________
____________________________________________
Noted. I've updated merchandise for this task:
[T][ ] Sell house
   Merchandise: Updated property details
____________________________________________
____________________________________________
Noted. I've removed merchandise from this task:
[T][ ] Sell house
   Merchandise: 
____________________________________________
____________________________________________ 
Good work. See you next time! 
____________________________________________ 

```

### Test: Display command help

**Aim:** Confirm that `help` lists all supported commands and their usage.

**Command:**
```sh
rm -f data/oreo.txt && javac -d /tmp/oreo-ui-test-classes $(find src/main/java -name '*.java') && java -cp /tmp/oreo-ui-test-classes oreo.Oreo
```

**Input:**
```text
help
bye
```

**Expected output:**
```text
____________________________________________ 
  OOO   RRRR   EEEEE  OOO  
 O   O  R   R  E     O   O 
 O   O  RRRR   EEEE  O   O 
 O   O  R R    E     O   O 
  OOO   R  RR  EEEEE  OOO  

Hello! I'm Oreo. 
Let's get started shall we? 
____________________________________________
____________________________________________
Here are the commands you can use:
- help: Shows this help message.
- list: Lists all tasks.
- todo DESCRIPTION: Adds a to-do task.
- deadline DESCRIPTION /by DATE: Adds a task with a deadline.
- event DESCRIPTION /from START /to END: Adds an event.
- find KEYWORD: Finds tasks by description.
- on DATE: Lists tasks occurring on a date.
- listall: Lists all tasks together with their merchandise.
- merchandise TASK_NUMBER DETAILS: Adds merchandise to a task.
- edit-merchandise TASK_NUMBER DETAILS: Edits merchandise for a task.
- delete-merchandise TASK_NUMBER: Removes merchandise from a task.
- find-merchandise KEYWORD: Finds merchandise by its details.
- mark TASK_NUMBER: Marks a task as done.
- unmark TASK_NUMBER: Marks a task as not done.
- delete TASK_NUMBER: Deletes a task.
- bye: Exits Oreo.
____________________________________________
____________________________________________ 
Good work. See you next time! 
____________________________________________ 

```

### Test: Find tasks by description keyword

**Aim:** Confirm that `find KEYWORD` displays matching tasks in original order, ignores letter case, and reports no matches.

**Command:**
```sh
rm -f data/oreo.txt && javac -d /tmp/oreo-ui-test-classes $(find src/main/java -name '*.java') && java -cp /tmp/oreo-ui-test-classes oreo.Oreo
```

**Input:**
```text
todo read book
deadline return book /by 2019-06-06
find BOOK
find spaceship
find
bye
```

**Expected output:**
```text
____________________________________________ 
  OOO   RRRR   EEEEE  OOO  
 O   O  R   R  E     O   O 
 O   O  RRRR   EEEE  O   O 
 O   O  R R    E     O   O 
  OOO   R  RR  EEEEE  OOO  

Hello! I'm Oreo. 
Let's get started shall we? 
____________________________________________
____________________________________________
Got it. I've added this task:
[T][ ] read book
Now you have 1 tasks in the list.
____________________________________________
____________________________________________
Got it. I've added this task:
[D][ ] return book (by: Jun 06 2019)
Now you have 2 tasks in the list.
____________________________________________
____________________________________________
Here are the matching tasks in your list:
1. [T][ ] read book
2. [D][ ] return book (by: Jun 06 2019)
____________________________________________
____________________________________________
No matching tasks found.
____________________________________________
____________________________________________
  Oh My God! Use: find KEYWORD
____________________________________________
____________________________________________ 
Good work. See you next time! 
____________________________________________ 

```

### Test: Query tasks by date

**Aim:** Confirm that `on YYYY-MM-DD` finds deadlines and events on a date and handles an invalid date.

**Command:**
```sh
rm -f data/oreo.txt && javac -d /tmp/oreo-ui-test-classes $(find src/main/java -name '*.java') && java -cp /tmp/oreo-ui-test-classes oreo.Oreo
```

**Input:**
```text
deadline return book /by 2019-10-15
event project meeting /from 2019-10-15 /to 2019-10-16
on 2019-10-15
on 2019-02-30
bye
```

**Expected output:**
```text
____________________________________________ 
  OOO   RRRR   EEEEE  OOO  
 O   O  R   R  E     O   O 
 O   O  RRRR   EEEE  O   O 
 O   O  R R    E     O   O 
  OOO   R  RR  EEEEE  OOO  

Hello! I'm Oreo. 
Let's get started shall we? 
____________________________________________
____________________________________________
Got it. I've added this task:
[D][ ] return book (by: Oct 15 2019)
Now you have 1 tasks in the list.
____________________________________________
____________________________________________
Got it. I've added this task:
[E][ ] project meeting (from: Oct 15 2019 to: Oct 16 2019)
Now you have 2 tasks in the list.
____________________________________________
____________________________________________
Tasks occurring on Oct 15 2019:
1. [D][ ] return book (by: Oct 15 2019)
2. [E][ ] project meeting (from: Oct 15 2019 to: Oct 16 2019)
____________________________________________
____________________________________________
  Oh My God! Use a date in yyyy-MM-dd format.
____________________________________________
____________________________________________ 
Good work. See you next time! 
____________________________________________ 

```

### Test: Reject an inverted event range

**Aim:** Confirm that an event whose start is not before its end is rejected without being added.

**Command:**
```sh
rm -f data/oreo.txt && javac -d /tmp/oreo-ui-test-classes $(find src/main/java -name '*.java') && java -cp /tmp/oreo-ui-test-classes oreo.Oreo
```

**Input:**
```text
event meeting /from 2019-10-16 /to 2019-10-15
bye
```

**Expected output:**
```text
____________________________________________ 
  OOO   RRRR   EEEEE  OOO  
 O   O  R   R  E     O   O 
 O   O  RRRR   EEEE  O   O 
 O   O  R R    E     O   O 
  OOO   R  RR  EEEEE  OOO  

Hello! I'm Oreo. 
Let's get started shall we? 
____________________________________________
____________________________________________
  Oh My God! The event start must be before its end.
____________________________________________
____________________________________________ 
Good work. See you next time! 
____________________________________________ 

```

### Test: Parse and format calendar dates and times

**Aim:** Confirm that supported date inputs are stored as calendar values and displayed in a different human-readable format.

**Command:**
```sh
rm -f data/oreo.txt && javac -d /tmp/oreo-ui-test-classes $(find src/main/java -name '*.java') && java -cp /tmp/oreo-ui-test-classes oreo.Oreo
```

**Input:**
```text
deadline return book /by 2/12/2019 1800
deadline submit report /by 2019-10-15
list
bye
```

**Expected output:**
```text
____________________________________________ 
  OOO   RRRR   EEEEE  OOO  
 O   O  R   R  E     O   O 
 O   O  RRRR   EEEE  O   O 
 O   O  R R    E     O   O 
  OOO   R  RR  EEEEE  OOO  

Hello! I'm Oreo. 
Let's get started shall we? 
____________________________________________
____________________________________________
Got it. I've added this task:
[D][ ] return book (by: Dec 02 2019 6:00PM)
Now you have 1 tasks in the list.
____________________________________________
____________________________________________
Got it. I've added this task:
[D][ ] submit report (by: Oct 15 2019)
Now you have 2 tasks in the list.
____________________________________________
____________________________________________
Here are the tasks in your list:
1. [D][ ] return book (by: Dec 02 2019 6:00PM)
2. [D][ ] submit report (by: Oct 15 2019)
____________________________________________
____________________________________________ 
Good work. See you next time! 
____________________________________________ 

```

### Test: Greeting and graceful exit

**Aim:** Confirm that Oreo displays its greeting and exits with its goodbye message when the user enters `bye`.

**Command:**
```sh
rm -f data/oreo.txt && javac -d /tmp/oreo-ui-test-classes $(find src/main/java -name '*.java') && java -cp /tmp/oreo-ui-test-classes oreo.Oreo
```

**Input:**
```text
bye
```

**Expected output:**
```text
____________________________________________ 
  OOO   RRRR   EEEEE  OOO  
 O   O  R   R  E     O   O 
 O   O  RRRR   EEEE  O   O 
 O   O  R R    E     O   O 
  OOO   R  RR  EEEEE  OOO  

Hello! I'm Oreo. 
Let's get started shall we? 
____________________________________________
____________________________________________ 
Good work. See you next time! 
____________________________________________ 

```

### Test: Recover from invalid commands without changing state

**Aim:** Confirm that an empty to-do description and an unknown command produce exception-based error messages, while valid tasks remain intact.

**Command:**
```sh
rm -f data/oreo.txt && javac -d /tmp/oreo-ui-test-classes $(find src/main/java -name '*.java') && java -cp /tmp/oreo-ui-test-classes oreo.Oreo
```

**Input:**
```text
todo
todo buy milk
blah
list
bye
```

**Expected output:**
```text
____________________________________________ 
  OOO   RRRR   EEEEE  OOO  
 O   O  R   R  E     O   O 
 O   O  RRRR   EEEE  O   O 
 O   O  R R    E     O   O 
  OOO   R  RR  EEEEE  OOO  

Hello! I'm Oreo. 
Let's get started shall we? 
____________________________________________
____________________________________________
  Oh My God! To do what task exactly?.
____________________________________________
____________________________________________
Got it. I've added this task:
[T][ ] buy milk
Now you have 1 tasks in the list.
____________________________________________
____________________________________________
  Oh My God! I cannot comprehend your English.
____________________________________________
____________________________________________
Here are the tasks in your list:
1. [T][ ] buy milk
____________________________________________
____________________________________________ 
Good work. See you next time! 
____________________________________________ 

```

### Test: Reject invalid task numbers without changing task state

**Aim:** Confirm that invalid task numbers, including an out-of-range delete command, are handled safely and leave the task list unchanged.

**Command:**
```sh
rm -f data/oreo.txt && javac -d /tmp/oreo-ui-test-classes $(find src/main/java -name '*.java') && java -cp /tmp/oreo-ui-test-classes oreo.Oreo
```

**Input:**
```text
todo buy milk
mark abc
delete 2
list
bye
```

**Expected output:**
```text
____________________________________________ 
  OOO   RRRR   EEEEE  OOO  
 O   O  R   R  E     O   O 
 O   O  RRRR   EEEE  O   O 
 O   O  R R    E     O   O 
  OOO   R  RR  EEEEE  OOO  

Hello! I'm Oreo. 
Let's get started shall we? 
____________________________________________
____________________________________________
Got it. I've added this task:
[T][ ] buy milk
Now you have 1 tasks in the list.
____________________________________________
____________________________________________
  Oh My God! That is not a valid task number.
____________________________________________
____________________________________________
  Oh My God! I can't find that task number.
____________________________________________
____________________________________________
Here are the tasks in your list:
1. [T][ ] buy milk
____________________________________________
____________________________________________ 
Good work. See you next time! 
____________________________________________ 

```

### Test: Add and list every task subtype

**Aim:** Confirm that the `Todo`, `Deadline`, and `Event` subclasses retain their type-specific details and that the `TaskType` enum preserves their existing display markers through the shared `Task` list.

**Command:**
```sh
rm -f data/oreo.txt && javac -d /tmp/oreo-ui-test-classes $(find src/main/java -name '*.java') && java -cp /tmp/oreo-ui-test-classes oreo.Oreo
```

**Input:**
```text
todo read book
deadline return book /by Sunday
event project meeting /from Mon 2pm /to 4pm
list
bye
```

**Expected output:**
```text
____________________________________________ 
  OOO   RRRR   EEEEE  OOO  
 O   O  R   R  E     O   O 
 O   O  RRRR   EEEE  O   O 
 O   O  R R    E     O   O 
  OOO   R  RR  EEEEE  OOO  

Hello! I'm Oreo. 
Let's get started shall we? 
____________________________________________
____________________________________________
Got it. I've added this task:
[T][ ] read book
Now you have 1 tasks in the list.
____________________________________________
____________________________________________
Got it. I've added this task:
[D][ ] return book (by: Sunday)
Now you have 2 tasks in the list.
____________________________________________
____________________________________________
Got it. I've added this task:
[E][ ] project meeting (from: Mon 2pm to: 4pm)
Now you have 3 tasks in the list.
____________________________________________
____________________________________________
Here are the tasks in your list:
1. [T][ ] read book
2. [D][ ] return book (by: Sunday)
3. [E][ ] project meeting (from: Mon 2pm to: 4pm)
____________________________________________
____________________________________________ 
Good work. See you next time! 
____________________________________________ 

```

### Test: Mark and unmark a task

**Aim:** Confirm that marking a task as done and then unmarking it updates its status without changing the task description or list position.

**Command:**
```sh
rm -f data/oreo.txt && javac -d /tmp/oreo-ui-test-classes $(find src/main/java -name '*.java') && java -cp /tmp/oreo-ui-test-classes oreo.Oreo
```

**Input:**
```text
todo buy milk
mark 1
unmark 1
list
bye
```

**Expected output:**
```text
____________________________________________ 
  OOO   RRRR   EEEEE  OOO  
 O   O  R   R  E     O   O 
 O   O  RRRR   EEEE  O   O 
 O   O  R R    E     O   O 
  OOO   R  RR  EEEEE  OOO  

Hello! I'm Oreo. 
Let's get started shall we? 
____________________________________________
____________________________________________
Got it. I've added this task:
[T][ ] buy milk
Now you have 1 tasks in the list.
____________________________________________
____________________________________________
Nice! I've marked this task as done:
  [T][X] buy milk
____________________________________________
____________________________________________
OK, I've marked this task as not done yet:
  [T][ ] buy milk
____________________________________________
____________________________________________
Here are the tasks in your list:
1. [T][ ] buy milk
____________________________________________
____________________________________________ 
Good work. See you next time! 
____________________________________________ 

```

### Test: Load tasks saved by a previous run

**Aim:** Confirm that a task saved in one run is loaded and listed when Oreo starts again.

**Command:**
```sh
rm -rf data && javac -d /tmp/oreo-ui-test-classes $(find src/main/java -name '*.java') && printf 'todo buy milk\nbye\n' | java -cp /tmp/oreo-ui-test-classes oreo.Oreo >/tmp/oreo-first-run.txt && printf 'list\nbye\n' | java -cp /tmp/oreo-ui-test-classes oreo.Oreo
```

**Input:**
```text
list
bye
```

**Expected output:**
```text
____________________________________________ 
  OOO   RRRR   EEEEE  OOO  
 O   O  R   R  E     O   O 
 O   O  RRRR   EEEE  O   O 
 O   O  R R    E     O   O 
  OOO   R  RR  EEEEE  OOO  

Hello! I'm Oreo. 
Let's get started shall we? 
____________________________________________
____________________________________________
Here are the tasks in your list:
1. [T][ ] buy milk
____________________________________________
____________________________________________ 
Good work. See you next time! 
____________________________________________ 

```

### Test: Delete tasks and reject invalid delete numbers

**Aim:** Confirm that deletion removes the selected task and re-numbers the list, while missing, zero, out-of-range, and empty-list delete commands leave the list unchanged.

**Command:**
```sh
rm -f data/oreo.txt && javac -d /tmp/oreo-ui-test-classes $(find src/main/java -name '*.java') && java -cp /tmp/oreo-ui-test-classes oreo.Oreo
```

**Input:**
```text
todo read book
todo return book
delete 1
list
delete
delete 0
delete 2
delete 1
list
delete 1
bye
```

**Expected output:**
```text
____________________________________________ 
  OOO   RRRR   EEEEE  OOO  
 O   O  R   R  E     O   O 
 O   O  RRRR   EEEE  O   O 
 O   O  R R    E     O   O 
  OOO   R  RR  EEEEE  OOO  

Hello! I'm Oreo. 
Let's get started shall we? 
____________________________________________
____________________________________________
Got it. I've added this task:
[T][ ] read book
Now you have 1 tasks in the list.
____________________________________________
____________________________________________
Got it. I've added this task:
[T][ ] return book
Now you have 2 tasks in the list.
____________________________________________
____________________________________________
Noted. I've removed this task:
  [T][ ] read book
Now you have 1 tasks in the list.
____________________________________________
____________________________________________
Here are the tasks in your list:
1. [T][ ] return book
____________________________________________
____________________________________________
  Oh My God! Sooo which task is it?
____________________________________________
____________________________________________
  Oh My God! I can't find that task number.
____________________________________________
____________________________________________
  Oh My God! I can't find that task number.
____________________________________________
____________________________________________
Noted. I've removed this task:
  [T][ ] return book
Now you have 0 tasks in the list.
____________________________________________
____________________________________________
No tasks in the list.
____________________________________________
____________________________________________
  Oh My God! I can't find that task number.
____________________________________________
____________________________________________ 
Good work. See you next time! 
____________________________________________ 

```
