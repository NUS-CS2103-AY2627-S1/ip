# Console UI test plan

This plan is the source of truth for console UI behavior. The runner starts a
fresh application process for each test case, compares output exactly, and
stops as soon as one case fails.

## Test environment

- **Setup command:** `./gradlew classes`
- **Timeout seconds:** `5`
- **Output matching:** Exact, including blank lines and spaces. Line-ending
  differences between Windows and Unix are ignored.
- **Path placeholders:** `{workspace}` expands to the repository's absolute path and
  `{test_dir}` expands to an isolated temporary working directory for that case.
- **Restart inputs:** An optional fenced block runs the same command again in the
  same temporary directory so saved data can be checked after a restart.

## Test case: greeting-and-exit

**Aim:** Verify that the application shows its greeting and exits cleanly.

**Run command:** `java -Duser.dir={test_dir} -cp {workspace}/build/classes/java/main bro.Bro`

**Inputs:**
```text
bye
```

**Expected output:**
```text
+----------------------------+
|       B R O // TASK HQ      |
|   YOUR PRODUCTIVITY WINGMAN |
+----------------------------+

Yo, I'm Bro — your laid-back productivity wingman. What are we getting done today?
Catch you later, bro. Keep crushing that to-do list!
```

## Test case: todo-add-and-list

**Aim:** Verify that a todo command adds a task and list displays it.

**Run command:** `java -Duser.dir={test_dir} -cp {workspace}/build/classes/java/main bro.Bro`

**Inputs:**
```text
todo read book
list
bye
```

**Expected output:**
```text
+----------------------------+
|       B R O // TASK HQ      |
|   YOUR PRODUCTIVITY WINGMAN |
+----------------------------+

Yo, I'm Bro — your laid-back productivity wingman. What are we getting done today?
Nice, bro — I've logged:

[T] [ ] read book
Bro is keeping tabs on 1 task.
1. [T] [ ] read book
Catch you later, bro. Keep crushing that to-do list!
```

## Test case: deadline-add-and-list

**Aim:** Verify that a deadline command stores an ISO date and displays it in a friendly format.

**Run command:** `java -Duser.dir={test_dir} -cp {workspace}/build/classes/java/main bro.Bro`

**Inputs:**
```text
deadline submit report /by 2019-10-15
list
bye
```

**Expected output:**
```text
+----------------------------+
|       B R O // TASK HQ      |
|   YOUR PRODUCTIVITY WINGMAN |
+----------------------------+

Yo, I'm Bro — your laid-back productivity wingman. What are we getting done today?
Nice, bro — I've logged:

[D] [ ] submit report(by: Oct 15 2019)
Bro is keeping tabs on 1 task.
1. [D] [ ] submit report(by: Oct 15 2019)
Catch you later, bro. Keep crushing that to-do list!
```

## Test case: deadline-date-time-add-and-list

**Aim:** Verify that a day/month/year deadline time is parsed and displayed in a friendly format.

**Run command:** `java -Duser.dir={test_dir} -cp {workspace}/build/classes/java/main bro.Bro`

**Inputs:**
```text
deadline return book /by 2/12/2019 1800
list
bye
```

**Expected output:**
```text
+----------------------------+
|       B R O // TASK HQ      |
|   YOUR PRODUCTIVITY WINGMAN |
+----------------------------+

Yo, I'm Bro — your laid-back productivity wingman. What are we getting done today?
Nice, bro — I've logged:

[D] [ ] return book(by: Dec 2 2019 6:00PM)
Bro is keeping tabs on 1 task.
1. [D] [ ] return book(by: Dec 2 2019 6:00PM)
Catch you later, bro. Keep crushing that to-do list!
```

## Test case: deadline-leap-day

**Aim:** Verify that a valid leap-day deadline is accepted and formatted correctly.

**Run command:** `java -Duser.dir={test_dir} -cp {workspace}/build/classes/java/main bro.Bro`

**Inputs:**
```text
deadline submit tax return /by 2020-02-29
list
bye
```

**Expected output:**
```text
+----------------------------+
|       B R O // TASK HQ      |
|   YOUR PRODUCTIVITY WINGMAN |
+----------------------------+

Yo, I'm Bro — your laid-back productivity wingman. What are we getting done today?
Nice, bro — I've logged:

[D] [ ] submit tax return(by: Feb 29 2020)
Bro is keeping tabs on 1 task.
1. [D] [ ] submit tax return(by: Feb 29 2020)
Catch you later, bro. Keep crushing that to-do list!
```

## Test case: deadline-midnight

**Aim:** Verify that 24-hour midnight is formatted as 12:00AM.

**Run command:** `java -Duser.dir={test_dir} -cp {workspace}/build/classes/java/main bro.Bro`

**Inputs:**
```text
deadline reset password /by 2/12/2019 0000
list
bye
```

**Expected output:**
```text
+----------------------------+
|       B R O // TASK HQ      |
|   YOUR PRODUCTIVITY WINGMAN |
+----------------------------+

Yo, I'm Bro — your laid-back productivity wingman. What are we getting done today?
Nice, bro — I've logged:

[D] [ ] reset password(by: Dec 2 2019 12:00AM)
Bro is keeping tabs on 1 task.
1. [D] [ ] reset password(by: Dec 2 2019 12:00AM)
Catch you later, bro. Keep crushing that to-do list!
```

## Test case: deadline-persists-after-restart

**Aim:** Verify that a typed deadline and its completion state survive a chatbot restart.

**Run command:** `java -Duser.dir={test_dir} -cp {workspace}/build/classes/java/main bro.Bro`

**Inputs:**
```text
deadline return book /by 2/12/2019 1800
mark 1
bye
```

**Restart inputs:**
```text
list
bye
```

**Expected output:**
```text
+----------------------------+
|       B R O // TASK HQ      |
|   YOUR PRODUCTIVITY WINGMAN |
+----------------------------+

Yo, I'm Bro — your laid-back productivity wingman. What are we getting done today?
Nice, bro — I've logged:

[D] [ ] return book(by: Dec 2 2019 6:00PM)
Bro is keeping tabs on 1 task.
Let's go, bro — this task is complete!
[X] return book
Catch you later, bro. Keep crushing that to-do list!
+----------------------------+
|       B R O // TASK HQ      |
|   YOUR PRODUCTIVITY WINGMAN |
+----------------------------+

Yo, I'm Bro — your laid-back productivity wingman. What are we getting done today?
1. [D] [X] return book(by: Dec 2 2019 6:00PM)
Catch you later, bro. Keep crushing that to-do list!
```

## Test case: event-add-and-list

**Aim:** Verify that an event command stores and lists both event times.

**Run command:** `java -Duser.dir={test_dir} -cp {workspace}/build/classes/java/main bro.Bro`

**Inputs:**
```text
event project meeting /from 2pm /to 4pm
list
bye
```

**Expected output:**
```text
+----------------------------+
|       B R O // TASK HQ      |
|   YOUR PRODUCTIVITY WINGMAN |
+----------------------------+

Yo, I'm Bro — your laid-back productivity wingman. What are we getting done today?
Nice, bro — I've logged:

[E] [ ] project meeting(from: 2pm to: 4pm)
Bro is keeping tabs on 1 task.
1. [E] [ ] project meeting(from: 2pm to: 4pm)
Catch you later, bro. Keep crushing that to-do list!
```

## Test case: mark-a-todo

**Aim:** Verify that mark changes the selected todo and list shows it as complete.

**Run command:** `java -Duser.dir={test_dir} -cp {workspace}/build/classes/java/main bro.Bro`

**Inputs:**
```text
todo read book
mark 1
list
bye
```

**Expected output:**
```text
+----------------------------+
|       B R O // TASK HQ      |
|   YOUR PRODUCTIVITY WINGMAN |
+----------------------------+

Yo, I'm Bro — your laid-back productivity wingman. What are we getting done today?
Nice, bro — I've logged:

[T] [ ] read book
Bro is keeping tabs on 1 task.
Let's go, bro — this task is complete!
[X] read book
1. [T] [X] read book
Catch you later, bro. Keep crushing that to-do list!
```

## Test case: empty-todo

**Aim:** Verify that an empty todo is rejected and the session continues.

**Run command:** `java -Duser.dir={test_dir} -cp {workspace}/build/classes/java/main bro.Bro`

**Inputs:**
```text
todo
list
bye
```

**Expected output:**
```text
+----------------------------+
|       B R O // TASK HQ      |
|   YOUR PRODUCTIVITY WINGMAN |
+----------------------------+

Yo, I'm Bro — your laid-back productivity wingman. What are we getting done today?
    ============================================================
     Bro says: I need a todo description. Try: todo <description>.
    ============================================================
Catch you later, bro. Keep crushing that to-do list!
```

## Test case: unknown-command

**Aim:** Verify that an unrecognised command is rejected without terminating Bro.

**Run command:** `java -Duser.dir={test_dir} -cp {workspace}/build/classes/java/main bro.Bro`

**Inputs:**
```text
blah
bye
```

**Expected output:**
```text
+----------------------------+
|       B R O // TASK HQ      |
|   YOUR PRODUCTIVITY WINGMAN |
+----------------------------+

Yo, I'm Bro — your laid-back productivity wingman. What are we getting done today?
    ============================================================
     Bro says: I don't recognize that command. Try todo, deadline, event, list, find, mark, unmark, delete, or bye.
    ============================================================
Catch you later, bro. Keep crushing that to-do list!
```

## Test case: find-task-by-keyword

**Aim:** Verify that find displays matching task descriptions in list order and ignores letter case.

**Run command:** `java -Duser.dir={test_dir} -cp {workspace}/build/classes/java/main bro.Bro`

**Inputs:**
```text
todo read book
deadline return book /by 2019-10-15
todo write notes
find BOOK
bye
```

**Expected output:**
```text
+----------------------------+
|       B R O // TASK HQ      |
|   YOUR PRODUCTIVITY WINGMAN |
+----------------------------+

Yo, I'm Bro — your laid-back productivity wingman. What are we getting done today?
Nice, bro — I've logged:

[T] [ ] read book
Bro is keeping tabs on 1 task.
Nice, bro — I've logged:

[D] [ ] return book(by: Oct 15 2019)
Bro is keeping tabs on 2 tasks.
Nice, bro — I've logged:

[T] [ ] write notes
Bro is keeping tabs on 3 tasks.
    ============================================================
     Bro found these matching tasks:
1. [T] [ ] read book
2. [D] [ ] return book(by: Oct 15 2019)
    ============================================================
Catch you later, bro. Keep crushing that to-do list!
```

## Test case: malformed-deadline

**Aim:** Verify that a deadline without a /by date gives a useful usage hint.

**Run command:** `java -Duser.dir={test_dir} -cp {workspace}/build/classes/java/main bro.Bro`

**Inputs:**
```text
deadline submit report
bye
```

**Expected output:**
```text
+----------------------------+
|       B R O // TASK HQ      |
|   YOUR PRODUCTIVITY WINGMAN |
+----------------------------+

Yo, I'm Bro — your laid-back productivity wingman. What are we getting done today?
    ============================================================
     Bro says: My format: deadline <description> /by <yyyy-MM-dd> or <d/M/yyyy HHmm>.
    ============================================================
Catch you later, bro. Keep crushing that to-do list!
```

## Test case: invalid-deadline-date

**Aim:** Verify that an invalid calendar date is rejected without adding a task.

**Run command:** `java -Duser.dir={test_dir} -cp {workspace}/build/classes/java/main bro.Bro`

**Inputs:**
```text
deadline return book /by 31/2/2019 1800
bye
```

**Expected output:**
```text
+----------------------------+
|       B R O // TASK HQ      |
|   YOUR PRODUCTIVITY WINGMAN |
+----------------------------+

Yo, I'm Bro — your laid-back productivity wingman. What are we getting done today?
    ============================================================
     Bro says: My format: deadline <description> /by <yyyy-MM-dd> or <d/M/yyyy HHmm>.
    ============================================================
Catch you later, bro. Keep crushing that to-do list!
```

## Test case: invalid-iso-deadline-date

**Aim:** Verify that a non-leap-year ISO date is rejected.

**Run command:** `java -Duser.dir={test_dir} -cp {workspace}/build/classes/java/main bro.Bro`

**Inputs:**
```text
deadline submit tax return /by 2019-02-29
bye
```

**Expected output:**
```text
+----------------------------+
|       B R O // TASK HQ      |
|   YOUR PRODUCTIVITY WINGMAN |
+----------------------------+

Yo, I'm Bro — your laid-back productivity wingman. What are we getting done today?
    ============================================================
     Bro says: My format: deadline <description> /by <yyyy-MM-dd> or <d/M/yyyy HHmm>.
    ============================================================
Catch you later, bro. Keep crushing that to-do list!
```

## Test case: invalid-deadline-time

**Aim:** Verify that an invalid 24-hour time is rejected.

**Run command:** `java -Duser.dir={test_dir} -cp {workspace}/build/classes/java/main bro.Bro`

**Inputs:**
```text
deadline return book /by 2/12/2019 2460
bye
```

**Expected output:**
```text
+----------------------------+
|       B R O // TASK HQ      |
|   YOUR PRODUCTIVITY WINGMAN |
+----------------------------+

Yo, I'm Bro — your laid-back productivity wingman. What are we getting done today?
    ============================================================
     Bro says: My format: deadline <description> /by <yyyy-MM-dd> or <d/M/yyyy HHmm>.
    ============================================================
Catch you later, bro. Keep crushing that to-do list!
```

## Test case: malformed-event

**Aim:** Verify that an event missing its /to component gives a useful usage hint.

**Run command:** `java -Duser.dir={test_dir} -cp {workspace}/build/classes/java/main bro.Bro`

**Inputs:**
```text
event team sync /from 2pm
bye
```

**Expected output:**
```text
+----------------------------+
|       B R O // TASK HQ      |
|   YOUR PRODUCTIVITY WINGMAN |
+----------------------------+

Yo, I'm Bro — your laid-back productivity wingman. What are we getting done today?
    ============================================================
     Bro says: My format: event <description> /from <start> /to <end>.
    ============================================================
Catch you later, bro. Keep crushing that to-do list!
```

## Test case: invalid-mark-index

**Aim:** Verify that a non-numeric mark index is reported clearly.

**Run command:** `java -Duser.dir={test_dir} -cp {workspace}/build/classes/java/main bro.Bro`

**Inputs:**
```text
todo read book
mark abc
bye
```

**Expected output:**
```text
+----------------------------+
|       B R O // TASK HQ      |
|   YOUR PRODUCTIVITY WINGMAN |
+----------------------------+

Yo, I'm Bro — your laid-back productivity wingman. What are we getting done today?
Nice, bro — I've logged:

[T] [ ] read book
Bro is keeping tabs on 1 task.
    ============================================================
     Bro says: I need a positive whole task number.
    ============================================================
Catch you later, bro. Keep crushing that to-do list!
```

## Test case: out-of-range-mark

**Aim:** Verify that a mark index outside the task list is rejected.

**Run command:** `java -Duser.dir={test_dir} -cp {workspace}/build/classes/java/main bro.Bro`

**Inputs:**
```text
todo read book
mark 2
bye
```

**Expected output:**
```text
+----------------------------+
|       B R O // TASK HQ      |
|   YOUR PRODUCTIVITY WINGMAN |
+----------------------------+

Yo, I'm Bro — your laid-back productivity wingman. What are we getting done today?
Nice, bro — I've logged:

[T] [ ] read book
Bro is keeping tabs on 1 task.
    ============================================================
     Bro says: I can only target tasks 1 through 1.
    ============================================================
Catch you later, bro. Keep crushing that to-do list!
```

## Test case: invalid-unmark-index

**Aim:** Verify that unmark validates its task index too.

**Run command:** `java -Duser.dir={test_dir} -cp {workspace}/build/classes/java/main bro.Bro`

**Inputs:**
```text
todo read book
unmark two
bye
```

**Expected output:**
```text
+----------------------------+
|       B R O // TASK HQ      |
|   YOUR PRODUCTIVITY WINGMAN |
+----------------------------+

Yo, I'm Bro — your laid-back productivity wingman. What are we getting done today?
Nice, bro — I've logged:

[T] [ ] read book
Bro is keeping tabs on 1 task.
    ============================================================
     Bro says: I need a positive whole task number.
    ============================================================
Catch you later, bro. Keep crushing that to-do list!
```

## Test case: blank-command

**Aim:** Verify that a blank line is handled as invalid input rather than a task.

**Run command:** `java -Duser.dir={test_dir} -cp {workspace}/build/classes/java/main bro.Bro`

**Inputs:**
```text

bye
```

**Expected output:**
```text
+----------------------------+
|       B R O // TASK HQ      |
|   YOUR PRODUCTIVITY WINGMAN |
+----------------------------+

Yo, I'm Bro — your laid-back productivity wingman. What are we getting done today?
    ============================================================
     Bro says: I'm ready when you are—enter a command.
    ============================================================
Catch you later, bro. Keep crushing that to-do list!
```

## Test case: delete-middle-task

**Aim:** Verify that delete removes the selected task and renumbers the remaining list.

**Run command:** `java -Duser.dir={test_dir} -cp {workspace}/build/classes/java/main bro.Bro`

**Inputs:**
```text
todo read book
deadline return book /by 2019-06-06
event project meeting /from Aug 6th 2pm /to 4pm
delete 2
list
bye
```

**Expected output:**
```text
+----------------------------+
|       B R O // TASK HQ      |
|   YOUR PRODUCTIVITY WINGMAN |
+----------------------------+

Yo, I'm Bro — your laid-back productivity wingman. What are we getting done today?
Nice, bro — I've logged:

[T] [ ] read book
Bro is keeping tabs on 1 task.
Nice, bro — I've logged:

[D] [ ] return book(by: Jun 6 2019)
Bro is keeping tabs on 2 tasks.
Nice, bro — I've logged:

[E] [ ] project meeting(from: Aug 6th 2pm to: 4pm)
Bro is keeping tabs on 3 tasks.
No worries, bro — I've cleared:
[D] [ ] return book(by: Jun 6 2019)
Bro is keeping tabs on 2 tasks.
1. [T] [ ] read book
2. [E] [ ] project meeting(from: Aug 6th 2pm to: 4pm)
Catch you later, bro. Keep crushing that to-do list!
```

## Test case: delete-missing-index

**Aim:** Verify that delete without a task number gives a usage hint.

**Run command:** `java -Duser.dir={test_dir} -cp {workspace}/build/classes/java/main bro.Bro`

**Inputs:**
```text
todo read book
delete
bye
```

**Expected output:**
```text
+----------------------------+
|       B R O // TASK HQ      |
|   YOUR PRODUCTIVITY WINGMAN |
+----------------------------+

Yo, I'm Bro — your laid-back productivity wingman. What are we getting done today?
Nice, bro — I've logged:

[T] [ ] read book
Bro is keeping tabs on 1 task.
    ============================================================
     Bro says: I need a task number. Use: delete <task number>.
    ============================================================
Catch you later, bro. Keep crushing that to-do list!
```

## Test case: delete-out-of-range

**Aim:** Verify that delete rejects an index beyond the current task list.

**Run command:** `java -Duser.dir={test_dir} -cp {workspace}/build/classes/java/main bro.Bro`

**Inputs:**
```text
todo read book
delete 2
bye
```

**Expected output:**
```text
+----------------------------+
|       B R O // TASK HQ      |
|   YOUR PRODUCTIVITY WINGMAN |
+----------------------------+

Yo, I'm Bro — your laid-back productivity wingman. What are we getting done today?
Nice, bro — I've logged:

[T] [ ] read book
Bro is keeping tabs on 1 task.
    ============================================================
     Bro says: I can only target tasks 1 through 1.
    ============================================================
Catch you later, bro. Keep crushing that to-do list!
```

## Test case: delete-empty-list

**Aim:** Verify that delete on an empty list reports the problem without crashing.

**Run command:** `java -Duser.dir={test_dir} -cp {workspace}/build/classes/java/main bro.Bro`

**Inputs:**
```text
delete 1
bye
```

**Expected output:**
```text
+----------------------------+
|       B R O // TASK HQ      |
|   YOUR PRODUCTIVITY WINGMAN |
+----------------------------+

Yo, I'm Bro — your laid-back productivity wingman. What are we getting done today?
    ============================================================
     Bro says: I have no tasks to delete yet.
    ============================================================
Catch you later, bro. Keep crushing that to-do list!
```
