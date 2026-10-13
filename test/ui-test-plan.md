# Console UI Test Plan

This file is the source of truth for the project's scripted console UI tests and the record of the latest test session.

## Test configuration

- Working directory: repository root
- Java version: 25
- Java assertions: enabled with `-ea`
- Command wrapper: `python test/run_gradle_ui_test.py` builds the Shadow JAR with Gradle and runs `baymax.Baymax` from isolated temporary storage
- Output comparison: exact after CRLF-to-LF normalization and removal of one final newline
- Captured output: stdout and stderr combined
- Timeout per test (seconds): 30

## Test cases

### TC-001: Exit immediately

- Aim: Verify that Baymax starts correctly and exits with the `bye` command.
- Command: `python test/run_gradle_ui_test.py`
- Inputs:
```text
bye
```
- Expected output:
```text
____________________________________________________________
BBBB   aaa   y   y  m     m   aaa   x   x
B   B a   a  y   y  mm   mm  a   a  x   x
B   B a   a   y y   m m m m  a   a   x x
BBBB  aaaaa    y    m  m  m  aaaaa    x
B   B a   a    y    m     m  a   a   x x
B   B a   a    y    m     m  a   a  x   x
BBBB  a   a    y    m     m  a   a  x   x
Hello. I am Baymax, your personal task companion.
I am here to keep your tasks healthy and organized.
How may I assist you?
____________________________________________________________
____________________________________________________________
 I am satisfied with my care. Until next time.
____________________________________________________________
```

### TC-002: Manage a todo task

- Aim: Verify adding a todo, listing it, marking it done, unmarking it, and listing the updated status.
- Command: `python test/run_gradle_ui_test.py`
- Inputs:
```text
todo buy milk
list
mark 1
unmark 1
list
bye
```
- Expected output:
```text
____________________________________________________________
BBBB   aaa   y   y  m     m   aaa   x   x
B   B a   a  y   y  mm   mm  a   a  x   x
B   B a   a   y y   m m m m  a   a   x x
BBBB  aaaaa    y    m  m  m  aaaaa    x
B   B a   a    y    m     m  a   a   x x
B   B a   a    y    m     m  a   a  x   x
BBBB  a   a    y    m     m  a   a  x   x
Hello. I am Baymax, your personal task companion.
I am here to keep your tasks healthy and organized.
How may I assist you?
____________________________________________________________
____________________________________________________________
 I have added this task to your care plan:
   [T][ ] buy milk
 You now have 1 task under my care.
____________________________________________________________
____________________________________________________________
 Here is your current care plan:
 1.[T][ ] buy milk
____________________________________________________________
____________________________________________________________
 Excellent. This task is complete:
   [T][X] buy milk
____________________________________________________________
____________________________________________________________
 Understood. This task still requires care:
   [T][ ] buy milk
____________________________________________________________
____________________________________________________________
 Here is your current care plan:
 1.[T][ ] buy milk
____________________________________________________________
____________________________________________________________
 I am satisfied with my care. Until next time.
____________________________________________________________
```

### TC-003: Add deadline and event tasks

- Aim: Verify typed task creation and list formatting for deadlines and events.
- Command: `python test/run_gradle_ui_test.py`
- Inputs:
```text
deadline submit report /by 2019-12-02
event team meeting /from 2019-12-02 /to 2019-12-04
list
bye
```
- Expected output:
```text
____________________________________________________________
BBBB   aaa   y   y  m     m   aaa   x   x
B   B a   a  y   y  mm   mm  a   a  x   x
B   B a   a   y y   m m m m  a   a   x x
BBBB  aaaaa    y    m  m  m  aaaaa    x
B   B a   a    y    m     m  a   a   x x
B   B a   a    y    m     m  a   a  x   x
BBBB  a   a    y    m     m  a   a  x   x
Hello. I am Baymax, your personal task companion.
I am here to keep your tasks healthy and organized.
How may I assist you?
____________________________________________________________
____________________________________________________________
 I have added this task to your care plan:
   [D][ ] submit report (by: Dec 02 2019)
 You now have 1 task under my care.
____________________________________________________________
____________________________________________________________
 I have added this task to your care plan:
   [E][ ] team meeting (from: Dec 02 2019 to: Dec 04 2019)
 You now have 2 tasks under my care.
____________________________________________________________
____________________________________________________________
 Here is your current care plan:
 1.[D][ ] submit report (by: Dec 02 2019)
 2.[E][ ] team meeting (from: Dec 02 2019 to: Dec 04 2019)
____________________________________________________________
____________________________________________________________
 I am satisfied with my care. Until next time.
____________________________________________________________
```

### TC-004: Reject invalid task references and malformed typed tasks

- Aim: Verify that malformed deadline/event commands and invalid mark/unmark arguments produce helpful errors without terminating the session.
- Command: `python test/run_gradle_ui_test.py`
- Inputs:
```text
deadline report
event meeting /from 10am
mark abc
unmark 2
bye
```
- Expected output:
```text
____________________________________________________________
BBBB   aaa   y   y  m     m   aaa   x   x
B   B a   a  y   y  mm   mm  a   a  x   x
B   B a   a   y y   m m m m  a   a   x x
BBBB  aaaaa    y    m  m  m  aaaaa    x
B   B a   a    y    m     m  a   a   x x
B   B a   a    y    m     m  a   a  x   x
BBBB  a   a    y    m     m  a   a  x   x
Hello. I am Baymax, your personal task companion.
I am here to keep your tasks healthy and organized.
How may I assist you?
____________________________________________________________
____________________________________________________________
 I have some concerns.
 Sorry, a deadline needs a due date.
____________________________________________________________
____________________________________________________________
 I have some concerns.
 Sorry, an event needs an end time.
____________________________________________________________
____________________________________________________________
 I have some concerns.
 Sorry, please provide a valid task number.
____________________________________________________________
____________________________________________________________
 I have some concerns.
 Sorry, that task is not in your care plan.
____________________________________________________________
____________________________________________________________
 I am satisfied with my care. Until next time.
____________________________________________________________
```

### TC-005: Delete a task

- Aim: Verify deleting a task removes it and updates the remaining task numbering and count.
- Command: `python test/run_gradle_ui_test.py`
- Inputs:
```text
todo buy milk
todo submit report
delete 1
list
bye
```
- Expected output:
```text
____________________________________________________________
BBBB   aaa   y   y  m     m   aaa   x   x
B   B a   a  y   y  mm   mm  a   a  x   x
B   B a   a   y y   m m m m  a   a   x x
BBBB  aaaaa    y    m  m  m  aaaaa    x
B   B a   a    y    m     m  a   a   x x
B   B a   a    y    m     m  a   a  x   x
BBBB  a   a    y    m     m  a   a  x   x
Hello. I am Baymax, your personal task companion.
I am here to keep your tasks healthy and organized.
How may I assist you?
____________________________________________________________
____________________________________________________________
 I have added this task to your care plan:
   [T][ ] buy milk
 You now have 1 task under my care.
____________________________________________________________
____________________________________________________________
 I have added this task to your care plan:
   [T][ ] submit report
 You now have 2 tasks under my care.
____________________________________________________________
____________________________________________________________
 This task is no longer under my care:
   [T][ ] buy milk
 You now have 1 task under my care.
____________________________________________________________
____________________________________________________________
 Here is your current care plan:
 1.[T][ ] submit report
____________________________________________________________
____________________________________________________________
 I am satisfied with my care. Until next time.
____________________________________________________________
```

### TC-006: Find tasks by partial keywords

- Aim: Verify that every partial keyword must occur in a task description for the task to be listed.
- Command: `python test/run_gradle_ui_test.py`
- Inputs:
```text
todo read book
deadline return book /by 2019-06-06
todo buy milk
find ret boo
bye
```
- Expected output:
```text
____________________________________________________________
BBBB   aaa   y   y  m     m   aaa   x   x
B   B a   a  y   y  mm   mm  a   a  x   x
B   B a   a   y y   m m m m  a   a   x x
BBBB  aaaaa    y    m  m  m  aaaaa    x
B   B a   a    y    m     m  a   a   x x
B   B a   a    y    m     m  a   a  x   x
BBBB  a   a    y    m     m  a   a  x   x
Hello. I am Baymax, your personal task companion.
I am here to keep your tasks healthy and organized.
How may I assist you?
____________________________________________________________
____________________________________________________________
 I have added this task to your care plan:
   [T][ ] read book
 You now have 1 task under my care.
____________________________________________________________
____________________________________________________________
 I have added this task to your care plan:
   [D][ ] return book (by: Jun 06 2019)
 You now have 2 tasks under my care.
____________________________________________________________
____________________________________________________________
 I have added this task to your care plan:
   [T][ ] buy milk
 You now have 3 tasks under my care.
____________________________________________________________
____________________________________________________________
 I found these tasks in your care plan:
 1.[D][ ] return book (by: Jun 06 2019)
____________________________________________________________
____________________________________________________________
 I am satisfied with my care. Until next time.
____________________________________________________________
```

### TC-007: Recover from invalid input and reject duplicate tasks

- Aim: Verify whitespace normalization, missing arguments, invalid dates and ranges, repeated parameters, unsafe descriptions, and duplicates while keeping the task list usable.
- Command: `python test/run_gradle_ui_test.py`
- Inputs:
```text
  todo   buy   milk
todo buy milk
mark
todo
deadline report /by 2024-02-30
deadline report /by 2024-01-01 /by 2024-01-02
event meeting /from 2024-01-02 /to 2024-01-02
todo unsafe|record
  list
bye
```
- Expected output:
```text
____________________________________________________________
BBBB   aaa   y   y  m     m   aaa   x   x
B   B a   a  y   y  mm   mm  a   a  x   x
B   B a   a   y y   m m m m  a   a   x x
BBBB  aaaaa    y    m  m  m  aaaaa    x
B   B a   a    y    m     m  a   a   x x
B   B a   a    y    m     m  a   a  x   x
BBBB  a   a    y    m     m  a   a  x   x
Hello. I am Baymax, your personal task companion.
I am here to keep your tasks healthy and organized.
How may I assist you?
____________________________________________________________
____________________________________________________________
 I have added this task to your care plan:
   [T][ ] buy milk
 You now have 1 task under my care.
____________________________________________________________
____________________________________________________________
 I have some concerns.
 Sorry, this task is already in your care plan.
____________________________________________________________
____________________________________________________________
 I have some concerns.
 Sorry, please provide a valid task number.
____________________________________________________________
____________________________________________________________
 I have some concerns.
     OOPS!!! The description of a todo cannot be empty.
____________________________________________________________
____________________________________________________________
 I have some concerns.
 Sorry, dates must use the format yyyy-MM-dd.
____________________________________________________________
____________________________________________________________
 I have some concerns.
 Sorry, use each date parameter once, in order: /by.
____________________________________________________________
____________________________________________________________
 I have some concerns.
 Sorry, an event must end after its start date.
____________________________________________________________
____________________________________________________________
 I have some concerns.
 Sorry, task descriptions cannot contain | or control characters.
____________________________________________________________
____________________________________________________________
 Here is your current care plan:
 1.[T][ ] buy milk
____________________________________________________________
____________________________________________________________
 I am satisfied with my care. Until next time.
____________________________________________________________
```

### TC-008: Load whitespace-only storage without a warning

- Aim: Verify a whitespace-only data file starts normally and permits adding and saving a task without a storage warning.
- Command: `python test/run_gradle_ui_test.py --blank-storage`
- Inputs:
```text
todo new task
bye
```
- Expected output:
```text
____________________________________________________________
BBBB   aaa   y   y  m     m   aaa   x   x
B   B a   a  y   y  mm   mm  a   a  x   x
B   B a   a   y y   m m m m  a   a   x x
BBBB  aaaaa    y    m  m  m  aaaaa    x
B   B a   a    y    m     m  a   a   x x
B   B a   a    y    m     m  a   a  x   x
BBBB  a   a    y    m     m  a   a  x   x
Hello. I am Baymax, your personal task companion.
I am here to keep your tasks healthy and organized.
How may I assist you?
____________________________________________________________
____________________________________________________________
 I have added this task to your care plan:
   [T][ ] new task
 You now have 1 task under my care.
____________________________________________________________
____________________________________________________________
 I am satisfied with my care. Until next time.
____________________________________________________________
```

### TC-009: Accept pasted non-breaking spaces

- Aim: Verify a todo command surrounded by actual U+00A0 non-breaking spaces succeeds with assertions enabled. The first input line contains U+00A0 before todo and after milk.
- Command: `python test/run_gradle_ui_test.py`
- Inputs:
```text
 todo buy milk 
bye
```
- Expected output:
```text
____________________________________________________________
BBBB   aaa   y   y  m     m   aaa   x   x
B   B a   a  y   y  mm   mm  a   a  x   x
B   B a   a   y y   m m m m  a   a   x x
BBBB  aaaaa    y    m  m  m  aaaaa    x
B   B a   a    y    m     m  a   a   x x
B   B a   a    y    m     m  a   a  x   x
BBBB  a   a    y    m     m  a   a  x   x
Hello. I am Baymax, your personal task companion.
I am here to keep your tasks healthy and organized.
How may I assist you?
____________________________________________________________
____________________________________________________________
 I have added this task to your care plan:
   [T][ ] buy milk
 You now have 1 task under my care.
____________________________________________________________
____________________________________________________________
 I am satisfied with my care. Until next time.
____________________________________________________________
```

### TC-010: Block unsavable changes after a corrupt load

- Aim: Verify a corrupt file permits reading and exiting but rejects task changes without a false success message.
- Command: `python test/run_gradle_ui_test.py --corrupt-storage`
- Inputs:
```text
todo new task
list
bye
```
- Expected output:
```text
____________________________________________________________
BBBB   aaa   y   y  m     m   aaa   x   x
B   B a   a  y   y  mm   mm  a   a  x   x
B   B a   a   y y   m m m m  a   a   x x
BBBB  aaaaa    y    m  m  m  aaaaa    x
B   B a   a    y    m     m  a   a   x x
B   B a   a    y    m     m  a   a  x   x
BBBB  a   a    y    m     m  a   a  x   x
Hello. I am Baymax, your personal task companion.
I am here to keep your tasks healthy and organized.
How may I assist you?
____________________________________________________________
I have some concerns. Skipped 1 invalid or duplicate record(s). Repair the data file and restart. Saving is disabled to protect it.
____________________________________________________________
 I have some concerns.
 Sorry, your care plan is read-only because loading failed. Repair the data file and restart Baymax before making changes.
____________________________________________________________
____________________________________________________________
 Here is your current care plan:
 1.[T][ ] existing
____________________________________________________________
____________________________________________________________
 I am satisfied with my care. Until next time.
____________________________________________________________
```

### TC-011: Reject unsupported date years

- Aim: Verify year zero, negative years, and years beyond 9999 are rejected without adding tasks.
- Command: `python test/run_gradle_ui_test.py`
- Inputs:
```text
deadline zero /by 0000-01-01
deadline negative /by -0001-01-01
event distant /from 2026-01-01 /to +10000-01-01
list
bye
```
- Expected output:
```text
____________________________________________________________
BBBB   aaa   y   y  m     m   aaa   x   x
B   B a   a  y   y  mm   mm  a   a  x   x
B   B a   a   y y   m m m m  a   a   x x
BBBB  aaaaa    y    m  m  m  aaaaa    x
B   B a   a    y    m     m  a   a   x x
B   B a   a    y    m     m  a   a  x   x
BBBB  a   a    y    m     m  a   a  x   x
Hello. I am Baymax, your personal task companion.
I am here to keep your tasks healthy and organized.
How may I assist you?
____________________________________________________________
____________________________________________________________
 I have some concerns.
 Sorry, date years must be between 0001 and 9999.
____________________________________________________________
____________________________________________________________
 I have some concerns.
 Sorry, date years must be between 0001 and 9999.
____________________________________________________________
____________________________________________________________
 I have some concerns.
 Sorry, date years must be between 0001 and 9999.
____________________________________________________________
____________________________________________________________
 Here is your current care plan:
____________________________________________________________
____________________________________________________________
 I am satisfied with my care. Until next time.
____________________________________________________________
```

### TC-012: Load a UTF-8 file with an initial BOM

- Aim: Verify the encoding marker does not reject the first task or disable saving.
- Command: `python test/run_gradle_ui_test.py --bom-storage`
- Inputs:
```text
list
bye
```
- Expected output:
```text
____________________________________________________________
BBBB   aaa   y   y  m     m   aaa   x   x
B   B a   a  y   y  mm   mm  a   a  x   x
B   B a   a   y y   m m m m  a   a   x x
BBBB  aaaaa    y    m  m  m  aaaaa    x
B   B a   a    y    m     m  a   a   x x
B   B a   a    y    m     m  a   a  x   x
BBBB  a   a    y    m     m  a   a  x   x
Hello. I am Baymax, your personal task companion.
I am here to keep your tasks healthy and organized.
How may I assist you?
____________________________________________________________
____________________________________________________________
 Here is your current care plan:
 1.[T][ ] existing
____________________________________________________________
____________________________________________________________
 I am satisfied with my care. Until next time.
____________________________________________________________
```

## Latest test session

- Recorded: 2026-09-17T11:53:56+08:00
- Result: PASS (12 passed, 0 failed, 0 skipped; java version "25.0.4.1" 2026-08-18 LTS)

````text
=== TC-001: Exit immediately ===
Command: python test/run_gradle_ui_test.py
Console input:
bye

Console output:
____________________________________________________________
BBBB   aaa   y   y  m     m   aaa   x   x
B   B a   a  y   y  mm   mm  a   a  x   x
B   B a   a   y y   m m m m  a   a   x x
BBBB  aaaaa    y    m  m  m  aaaaa    x
B   B a   a    y    m     m  a   a   x x
B   B a   a    y    m     m  a   a  x   x
BBBB  a   a    y    m     m  a   a  x   x
Hello. I am Baymax, your personal task companion.
I am here to keep your tasks healthy and organized.
How may I assist you?
____________________________________________________________
____________________________________________________________
 I am satisfied with my care. Until next time.
____________________________________________________________

Status: PASS

=== TC-002: Manage a todo task ===
Command: python test/run_gradle_ui_test.py
Console input:
todo buy milk
list
mark 1
unmark 1
list
bye

Console output:
____________________________________________________________
BBBB   aaa   y   y  m     m   aaa   x   x
B   B a   a  y   y  mm   mm  a   a  x   x
B   B a   a   y y   m m m m  a   a   x x
BBBB  aaaaa    y    m  m  m  aaaaa    x
B   B a   a    y    m     m  a   a   x x
B   B a   a    y    m     m  a   a  x   x
BBBB  a   a    y    m     m  a   a  x   x
Hello. I am Baymax, your personal task companion.
I am here to keep your tasks healthy and organized.
How may I assist you?
____________________________________________________________
____________________________________________________________
 I have added this task to your care plan:
   [T][ ] buy milk
 You now have 1 task under my care.
____________________________________________________________
____________________________________________________________
 Here is your current care plan:
 1.[T][ ] buy milk
____________________________________________________________
____________________________________________________________
 Excellent. This task is complete:
   [T][X] buy milk
____________________________________________________________
____________________________________________________________
 Understood. This task still requires care:
   [T][ ] buy milk
____________________________________________________________
____________________________________________________________
 Here is your current care plan:
 1.[T][ ] buy milk
____________________________________________________________
____________________________________________________________
 I am satisfied with my care. Until next time.
____________________________________________________________

Status: PASS

=== TC-003: Add deadline and event tasks ===
Command: python test/run_gradle_ui_test.py
Console input:
deadline submit report /by 2019-12-02
event team meeting /from 2019-12-02 /to 2019-12-04
list
bye

Console output:
____________________________________________________________
BBBB   aaa   y   y  m     m   aaa   x   x
B   B a   a  y   y  mm   mm  a   a  x   x
B   B a   a   y y   m m m m  a   a   x x
BBBB  aaaaa    y    m  m  m  aaaaa    x
B   B a   a    y    m     m  a   a   x x
B   B a   a    y    m     m  a   a  x   x
BBBB  a   a    y    m     m  a   a  x   x
Hello. I am Baymax, your personal task companion.
I am here to keep your tasks healthy and organized.
How may I assist you?
____________________________________________________________
____________________________________________________________
 I have added this task to your care plan:
   [D][ ] submit report (by: Dec 02 2019)
 You now have 1 task under my care.
____________________________________________________________
____________________________________________________________
 I have added this task to your care plan:
   [E][ ] team meeting (from: Dec 02 2019 to: Dec 04 2019)
 You now have 2 tasks under my care.
____________________________________________________________
____________________________________________________________
 Here is your current care plan:
 1.[D][ ] submit report (by: Dec 02 2019)
 2.[E][ ] team meeting (from: Dec 02 2019 to: Dec 04 2019)
____________________________________________________________
____________________________________________________________
 I am satisfied with my care. Until next time.
____________________________________________________________

Status: PASS

=== TC-004: Reject invalid task references and malformed typed tasks ===
Command: python test/run_gradle_ui_test.py
Console input:
deadline report
event meeting /from 10am
mark abc
unmark 2
bye

Console output:
____________________________________________________________
BBBB   aaa   y   y  m     m   aaa   x   x
B   B a   a  y   y  mm   mm  a   a  x   x
B   B a   a   y y   m m m m  a   a   x x
BBBB  aaaaa    y    m  m  m  aaaaa    x
B   B a   a    y    m     m  a   a   x x
B   B a   a    y    m     m  a   a  x   x
BBBB  a   a    y    m     m  a   a  x   x
Hello. I am Baymax, your personal task companion.
I am here to keep your tasks healthy and organized.
How may I assist you?
____________________________________________________________
____________________________________________________________
 I have some concerns.
 Sorry, a deadline needs a due date.
____________________________________________________________
____________________________________________________________
 I have some concerns.
 Sorry, an event needs an end time.
____________________________________________________________
____________________________________________________________
 I have some concerns.
 Sorry, please provide a valid task number.
____________________________________________________________
____________________________________________________________
 I have some concerns.
 Sorry, that task is not in your care plan.
____________________________________________________________
____________________________________________________________
 I am satisfied with my care. Until next time.
____________________________________________________________

Status: PASS

=== TC-005: Delete a task ===
Command: python test/run_gradle_ui_test.py
Console input:
todo buy milk
todo submit report
delete 1
list
bye

Console output:
____________________________________________________________
BBBB   aaa   y   y  m     m   aaa   x   x
B   B a   a  y   y  mm   mm  a   a  x   x
B   B a   a   y y   m m m m  a   a   x x
BBBB  aaaaa    y    m  m  m  aaaaa    x
B   B a   a    y    m     m  a   a   x x
B   B a   a    y    m     m  a   a  x   x
BBBB  a   a    y    m     m  a   a  x   x
Hello. I am Baymax, your personal task companion.
I am here to keep your tasks healthy and organized.
How may I assist you?
____________________________________________________________
____________________________________________________________
 I have added this task to your care plan:
   [T][ ] buy milk
 You now have 1 task under my care.
____________________________________________________________
____________________________________________________________
 I have added this task to your care plan:
   [T][ ] submit report
 You now have 2 tasks under my care.
____________________________________________________________
____________________________________________________________
 This task is no longer under my care:
   [T][ ] buy milk
 You now have 1 task under my care.
____________________________________________________________
____________________________________________________________
 Here is your current care plan:
 1.[T][ ] submit report
____________________________________________________________
____________________________________________________________
 I am satisfied with my care. Until next time.
____________________________________________________________

Status: PASS

=== TC-006: Find tasks by partial keywords ===
Command: python test/run_gradle_ui_test.py
Console input:
todo read book
deadline return book /by 2019-06-06
todo buy milk
find ret boo
bye

Console output:
____________________________________________________________
BBBB   aaa   y   y  m     m   aaa   x   x
B   B a   a  y   y  mm   mm  a   a  x   x
B   B a   a   y y   m m m m  a   a   x x
BBBB  aaaaa    y    m  m  m  aaaaa    x
B   B a   a    y    m     m  a   a   x x
B   B a   a    y    m     m  a   a  x   x
BBBB  a   a    y    m     m  a   a  x   x
Hello. I am Baymax, your personal task companion.
I am here to keep your tasks healthy and organized.
How may I assist you?
____________________________________________________________
____________________________________________________________
 I have added this task to your care plan:
   [T][ ] read book
 You now have 1 task under my care.
____________________________________________________________
____________________________________________________________
 I have added this task to your care plan:
   [D][ ] return book (by: Jun 06 2019)
 You now have 2 tasks under my care.
____________________________________________________________
____________________________________________________________
 I have added this task to your care plan:
   [T][ ] buy milk
 You now have 3 tasks under my care.
____________________________________________________________
____________________________________________________________
 I found these tasks in your care plan:
 1.[D][ ] return book (by: Jun 06 2019)
____________________________________________________________
____________________________________________________________
 I am satisfied with my care. Until next time.
____________________________________________________________

Status: PASS

=== TC-007: Recover from invalid input and reject duplicate tasks ===
Command: python test/run_gradle_ui_test.py
Console input:
  todo   buy   milk
todo buy milk
mark
todo
deadline report /by 2024-02-30
deadline report /by 2024-01-01 /by 2024-01-02
event meeting /from 2024-01-02 /to 2024-01-02
todo unsafe|record
  list
bye

Console output:
____________________________________________________________
BBBB   aaa   y   y  m     m   aaa   x   x
B   B a   a  y   y  mm   mm  a   a  x   x
B   B a   a   y y   m m m m  a   a   x x
BBBB  aaaaa    y    m  m  m  aaaaa    x
B   B a   a    y    m     m  a   a   x x
B   B a   a    y    m     m  a   a  x   x
BBBB  a   a    y    m     m  a   a  x   x
Hello. I am Baymax, your personal task companion.
I am here to keep your tasks healthy and organized.
How may I assist you?
____________________________________________________________
____________________________________________________________
 I have added this task to your care plan:
   [T][ ] buy milk
 You now have 1 task under my care.
____________________________________________________________
____________________________________________________________
 I have some concerns.
 Sorry, this task is already in your care plan.
____________________________________________________________
____________________________________________________________
 I have some concerns.
 Sorry, please provide a valid task number.
____________________________________________________________
____________________________________________________________
 I have some concerns.
     OOPS!!! The description of a todo cannot be empty.
____________________________________________________________
____________________________________________________________
 I have some concerns.
 Sorry, dates must use the format yyyy-MM-dd.
____________________________________________________________
____________________________________________________________
 I have some concerns.
 Sorry, use each date parameter once, in order: /by.
____________________________________________________________
____________________________________________________________
 I have some concerns.
 Sorry, an event must end after its start date.
____________________________________________________________
____________________________________________________________
 I have some concerns.
 Sorry, task descriptions cannot contain | or control characters.
____________________________________________________________
____________________________________________________________
 Here is your current care plan:
 1.[T][ ] buy milk
____________________________________________________________
____________________________________________________________
 I am satisfied with my care. Until next time.
____________________________________________________________

Status: PASS

=== TC-008: Load whitespace-only storage without a warning ===
Command: python test/run_gradle_ui_test.py --blank-storage
Console input:
todo new task
bye

Console output:
____________________________________________________________
BBBB   aaa   y   y  m     m   aaa   x   x
B   B a   a  y   y  mm   mm  a   a  x   x
B   B a   a   y y   m m m m  a   a   x x
BBBB  aaaaa    y    m  m  m  aaaaa    x
B   B a   a    y    m     m  a   a   x x
B   B a   a    y    m     m  a   a  x   x
BBBB  a   a    y    m     m  a   a  x   x
Hello. I am Baymax, your personal task companion.
I am here to keep your tasks healthy and organized.
How may I assist you?
____________________________________________________________
____________________________________________________________
 I have added this task to your care plan:
   [T][ ] new task
 You now have 1 task under my care.
____________________________________________________________
____________________________________________________________
 I am satisfied with my care. Until next time.
____________________________________________________________

Status: PASS

=== TC-009: Accept pasted non-breaking spaces ===
Command: python test/run_gradle_ui_test.py
Console input:
 todo buy milk 
bye

Console output:
____________________________________________________________
BBBB   aaa   y   y  m     m   aaa   x   x
B   B a   a  y   y  mm   mm  a   a  x   x
B   B a   a   y y   m m m m  a   a   x x
BBBB  aaaaa    y    m  m  m  aaaaa    x
B   B a   a    y    m     m  a   a   x x
B   B a   a    y    m     m  a   a  x   x
BBBB  a   a    y    m     m  a   a  x   x
Hello. I am Baymax, your personal task companion.
I am here to keep your tasks healthy and organized.
How may I assist you?
____________________________________________________________
____________________________________________________________
 I have added this task to your care plan:
   [T][ ] buy milk
 You now have 1 task under my care.
____________________________________________________________
____________________________________________________________
 I am satisfied with my care. Until next time.
____________________________________________________________

Status: PASS

=== TC-010: Block unsavable changes after a corrupt load ===
Command: python test/run_gradle_ui_test.py --corrupt-storage
Console input:
todo new task
list
bye

Console output:
____________________________________________________________
BBBB   aaa   y   y  m     m   aaa   x   x
B   B a   a  y   y  mm   mm  a   a  x   x
B   B a   a   y y   m m m m  a   a   x x
BBBB  aaaaa    y    m  m  m  aaaaa    x
B   B a   a    y    m     m  a   a   x x
B   B a   a    y    m     m  a   a  x   x
BBBB  a   a    y    m     m  a   a  x   x
Hello. I am Baymax, your personal task companion.
I am here to keep your tasks healthy and organized.
How may I assist you?
____________________________________________________________
I have some concerns. Skipped 1 invalid or duplicate record(s). Repair the data file and restart. Saving is disabled to protect it.
____________________________________________________________
 I have some concerns.
 Sorry, your care plan is read-only because loading failed. Repair the data file and restart Baymax before making changes.
____________________________________________________________
____________________________________________________________
 Here is your current care plan:
 1.[T][ ] existing
____________________________________________________________
____________________________________________________________
 I am satisfied with my care. Until next time.
____________________________________________________________

Status: PASS

=== TC-011: Reject unsupported date years ===
Command: python test/run_gradle_ui_test.py
Console input:
deadline zero /by 0000-01-01
deadline negative /by -0001-01-01
event distant /from 2026-01-01 /to +10000-01-01
list
bye

Console output:
____________________________________________________________
BBBB   aaa   y   y  m     m   aaa   x   x
B   B a   a  y   y  mm   mm  a   a  x   x
B   B a   a   y y   m m m m  a   a   x x
BBBB  aaaaa    y    m  m  m  aaaaa    x
B   B a   a    y    m     m  a   a   x x
B   B a   a    y    m     m  a   a  x   x
BBBB  a   a    y    m     m  a   a  x   x
Hello. I am Baymax, your personal task companion.
I am here to keep your tasks healthy and organized.
How may I assist you?
____________________________________________________________
____________________________________________________________
 I have some concerns.
 Sorry, date years must be between 0001 and 9999.
____________________________________________________________
____________________________________________________________
 I have some concerns.
 Sorry, date years must be between 0001 and 9999.
____________________________________________________________
____________________________________________________________
 I have some concerns.
 Sorry, date years must be between 0001 and 9999.
____________________________________________________________
____________________________________________________________
 Here is your current care plan:
____________________________________________________________
____________________________________________________________
 I am satisfied with my care. Until next time.
____________________________________________________________

Status: PASS

=== TC-012: Load a UTF-8 file with an initial BOM ===
Command: python test/run_gradle_ui_test.py --bom-storage
Console input:
list
bye

Console output:
____________________________________________________________
BBBB   aaa   y   y  m     m   aaa   x   x
B   B a   a  y   y  mm   mm  a   a  x   x
B   B a   a   y y   m m m m  a   a   x x
BBBB  aaaaa    y    m  m  m  aaaaa    x
B   B a   a    y    m     m  a   a   x x
B   B a   a    y    m     m  a   a  x   x
BBBB  a   a    y    m     m  a   a  x   x
Hello. I am Baymax, your personal task companion.
I am here to keep your tasks healthy and organized.
How may I assist you?
____________________________________________________________
____________________________________________________________
 Here is your current care plan:
 1.[T][ ] existing
____________________________________________________________
____________________________________________________________
 I am satisfied with my care. Until next time.
____________________________________________________________

Status: PASS
````
