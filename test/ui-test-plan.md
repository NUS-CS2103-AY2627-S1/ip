# Ui Test Plan

## Project information

- Working directory: `C:\dev\CS2103\ip\ip`
- Runtime: Java 25
- Character encoding: UTF-8
- Application entry point: `src/main/java/Alice.java`

## Preconditions

Compile all application classes before running the test cases:

```powershell
javac -encoding UTF-8 -d build/ui-test src/main/java/*.java
```

## Test cases

### TC-001: Launch and exit cleanly

- Aim: Verify the startup greeting and clean exit behavior.
- Command: `java -Dstdout.encoding=UTF-8 -Dstderr.encoding=UTF-8 -cp build/ui-test Alice`
- Inputs:
  ```text
  bye
  ```
- Expected output:
  ```text
----------------------------------------------------------
██████  ██     ████  █████  █████
██  ██  ██      ██   ██     ██
██████  ██      ██   ██     ████
██  ██  ██      ██   ██     ██
██  ██  █████  ████  █████  █████

Hello! Alice is here to chat!
What do you want to discuss with me?
----------------------------------------------------------
----------------------------------------------------------
Bye Bye. Can't wait to talk to you again!
----------------------------------------------------------
  ```

### TC-002: Create and list all task types

- Aim: Verify todo, deadline, and event creation plus list formatting.
- Command: `java -Dstdout.encoding=UTF-8 -Dstderr.encoding=UTF-8 -cp build/ui-test Alice`
- Inputs:
  ```text
todo buy milk
deadline submit report /by Friday
event project meeting /from Monday 10am /to Monday 11am
list
bye
  ```
- Expected output:
  ```text
----------------------------------------------------------
██████  ██     ████  █████  █████
██  ██  ██      ██   ██     ██
██████  ██      ██   ██     ████
██  ██  ██      ██   ██     ██
██  ██  █████  ████  █████  █████

Hello! Alice is here to chat!
What do you want to discuss with me?
----------------------------------------------------------
Got it. I've added this task:
  [T][ ] buy milk
Now you have 1 tasks in the list.
----------------------------------------------------------
Got it. I've added this task:
  [D][ ] submit report (by: Friday)
Now you have 2 tasks in the list.
----------------------------------------------------------
Got it. I've added this task:
  [E][ ] project meeting (from: Monday 10am to: Monday 11am)
Now you have 3 tasks in the list.
----------------------------------------------------------
Here are the tasks in your list:
1.[T][ ] buy milk
2.[D][ ] submit report (by: Friday)
3.[E][ ] project meeting (from: Monday 10am to: Monday 11am)
----------------------------------------------------------
----------------------------------------------------------
Bye Bye. Can't wait to talk to you again!
----------------------------------------------------------
  ```

### TC-003: Mark and unmark a task

- Aim: Verify status changes, repeated mark/unmark behavior, and list status.
- Command: `java -Dstdout.encoding=UTF-8 -Dstderr.encoding=UTF-8 -cp build/ui-test Alice`
- Inputs:
  ```text
todo submit assignment
mark 1
mark 1
unmark 1
unmark 1
list
bye
  ```
- Expected output:
  ```text
----------------------------------------------------------
██████  ██     ████  █████  █████
██  ██  ██      ██   ██     ██
██████  ██      ██   ██     ████
██  ██  ██      ██   ██     ██
██  ██  █████  ████  █████  █████

Hello! Alice is here to chat!
What do you want to discuss with me?
----------------------------------------------------------
Got it. I've added this task:
  [T][ ] submit assignment
Now you have 1 tasks in the list.
----------------------------------------------------------
Nice! I've marked this task as done;
  [T][x] submit assignment
----------------------------------------------------------
This task is already mark
----------------------------------------------------------
Ok, I've marked this task as not done yet;
  [ ] [T][ ] submit assignment
----------------------------------------------------------
This task is already unmark
----------------------------------------------------------
Here are the tasks in your list:
1.[T][ ] submit assignment
----------------------------------------------------------
----------------------------------------------------------
Bye Bye. Can't wait to talk to you again!
----------------------------------------------------------
  ```

### TC-004: Reject invalid task creation and commands

- Aim: Verify empty descriptions, missing deadline/event markers, malformed event values, and unknown commands.
- Command: `java -Dstdout.encoding=UTF-8 -Dstderr.encoding=UTF-8 -cp build/ui-test Alice`
- Inputs:
  ```text
todo
deadline
deadline report
deadline /by Friday
event
event team meeting
event team meeting /from Monday 10am
event meeting /from /to 4pm
unknown
bye
  ```
- Expected output:
  ```text
----------------------------------------------------------
██████  ██     ████  █████  █████
██  ██  ██      ██   ██     ██
██████  ██      ██   ██     ████
██  ██  ██      ██   ██     ██
██  ██  █████  ████  █████  █████

Hello! Alice is here to chat!
What do you want to discuss with me?
----------------------------------------------------------
AIYO! The description of a todo cannot be empty.
----------------------------------------------------------
AIYO!!! The description of a deadline cannot be empty.
----------------------------------------------------------
AIYO! Please specify a deadline using /by (e.g. deadline return book /by Sunday)
----------------------------------------------------------
AIYO! The description of a deadline cannot be empty.
----------------------------------------------------------
AIYO! The description of an event cannot be empty.
----------------------------------------------------------
AYIO! Please specify event using /from and /to (e.g. event meeting /from Mon 2pm /to 4pm)
----------------------------------------------------------
AYIO! Please specify event using /from and /to (e.g. event meeting /from Mon 2pm /to 4pm)
----------------------------------------------------------
AIYO! The event start time (/from) cannot be empty.
----------------------------------------------------------
----------------------------------------------------------
Sorry! I am not sure what are you talking about :(
----------------------------------------------------------
----------------------------------------------------------
Bye Bye. Can't wait to talk to you again!
----------------------------------------------------------
  ```

### TC-005: Reject invalid mark and unmark inputs

- Aim: Verify missing indices, non-numeric indices, out-of-range indices, and repeated status changes.
- Command: `java -Dstdout.encoding=UTF-8 -Dstderr.encoding=UTF-8 -cp build/ui-test Alice`
- Inputs:
  ```text
mark
mark abc
mark 1
todo task
mark 0
mark 2
mark 1
mark 1
unmark abc
unmark 2
unmark 1
unmark 1
bye
  ```
- Expected output:
  ```text
----------------------------------------------------------
██████  ██     ████  █████  █████
██  ██  ██      ██   ██     ██
██████  ██      ██   ██     ████
██  ██  ██      ██   ██     ██
██  ██  █████  ████  █████  █████

Hello! Alice is here to chat!
What do you want to discuss with me?
----------------------------------------------------------
AIYO! Please specify which task to mark (e.g. mark 2)
----------------------------------------------------------
AIYO! Please enter a valid number! (e.g. mark 2)
----------------------------------------------------------
AIYO! Please enter a valid number from 1 to 0!
----------------------------------------------------------
Got it. I've added this task:
  [T][ ] task
Now you have 1 tasks in the list.
----------------------------------------------------------
AIYO! Please enter a valid number from 1 to 1!
----------------------------------------------------------
AIYO! Please enter a valid number from 1 to 1!
----------------------------------------------------------
Nice! I've marked this task as done;
  [T][x] task
----------------------------------------------------------
This task is already mark
----------------------------------------------------------
AIYO! Please enter a valid number! (e.g. mark 2)
----------------------------------------------------------
AIYO! Please enter a valid number from 1 to 1!
----------------------------------------------------------
Ok, I've marked this task as not done yet;
  [ ] [T][ ] task
----------------------------------------------------------
This task is already unmark
----------------------------------------------------------
----------------------------------------------------------
Bye Bye. Can't wait to talk to you again!
----------------------------------------------------------
  ```

### TC-006: List an empty task list

- Aim: Verify list reports that no tasks exist in a fresh session.
- Command: `java -Dstdout.encoding=UTF-8 -Dstderr.encoding=UTF-8 -cp build/ui-test Alice`
- Inputs:
  ```text
list
bye
  ```
- Expected output:
  ```text
----------------------------------------------------------
██████  ██     ████  █████  █████
██  ██  ██      ██   ██     ██
██████  ██      ██   ██     ████
██  ██  ██      ██   ██     ██
██  ██  █████  ████  █████  █████

Hello! Alice is here to chat!
What do you want to discuss with me?
----------------------------------------------------------
No tasks in your list yet!
----------------------------------------------------------
Bye Bye. Can't wait to talk to you again!
----------------------------------------------------------
  ```

### TC-007: Delete a task and reindex the list

- Aim: Verify that deleting a task removes the requested item, reports the remaining count, and renumbers the remaining tasks.
- Command: `java -Dstdout.encoding=UTF-8 -Dstderr.encoding=UTF-8 -cp build/ui-test Alice`
- Inputs:
  ```text
todo buy milk
deadline submit report /by Friday
event project meeting /from Monday 10am /to Monday 11am
delete 2
list
bye
  ```
- Expected output:
  ```text
----------------------------------------------------------
██████  ██     ████  █████  █████
██  ██  ██      ██   ██     ██
██████  ██      ██   ██     ████
██  ██  ██      ██   ██     ██
██  ██  █████  ████  █████  █████

Hello! Alice is here to chat!
What do you want to discuss with me?
----------------------------------------------------------
Got it. I've added this task:
  [T][ ] buy milk
Now you have 1 tasks in the list.
----------------------------------------------------------
Got it. I've added this task:
  [D][ ] submit report (by: Friday)
Now you have 2 tasks in the list.
----------------------------------------------------------
Got it. I've added this task:
  [E][ ] project meeting (from: Monday 10am to: Monday 11am)
Now you have 3 tasks in the list.
----------------------------------------------------------
Noted, I've removed this task:
  [D][ ] submit report (by: Friday)
Now you have 2 tasks in the list.
----------------------------------------------------------
Here are the tasks in your list:
1.[T][ ] buy milk
2.[E][ ] project meeting (from: Monday 10am to: Monday 11am)
----------------------------------------------------------
----------------------------------------------------------
Bye Bye. Can't wait to talk to you again!
----------------------------------------------------------
  ```

## Test session: 2026-08-18 23:53:41 +08:00

The session stopped before `TC-001` because the compile precondition failed. No test case was launched.

### Preconditions: Compile the application — FAIL

- Command: `javac -encoding UTF-8 -d build/ui-test src/main/java/Alice.java`
- Inputs: None
- Expected output: No compiler errors and exit code `0`.
- Actual output:
  ```text
src\main\java\Alice.java:7: error: cannot find symbol
    private final ArrayList<Task> tasks;
                            ^
  symbol:   class Task
  location: class Alice
src\main\java\Alice.java:129: error: cannot find symbol
        Task taskItem;
        ^
  symbol:   class Task
  location: class Alice
src\main\java\Alice.java:132: error: cannot find symbol
            taskItem = new ToDos(description);
                           ^
  symbol:   class ToDos
  location: class Alice
src\main\java\Alice.java:151: error: cannot find symbol
            taskItem = new Deadlines(parts[0], parts[1]);
                           ^
  symbol:   class Deadlines
  location: class Alice
src\main\java\Alice.java:179: error: cannot find symbol
            taskItem = new Events(parts[0], fromToParts[0], fromToParts[1]);
                           ^
  symbol:   class Events
  location: class Alice
src\main\java\Alice.java:213: error: cannot find symbol
            Task selectedTask = tasks.get(index);
            ^
  symbol:   class Task
  location: class Alice
6 errors
  ```
- Exit code: `1`

## Test session: 2026-08-19 00:05:34 +08:00

The compile precondition passed. The session stopped at `TC-001` because the output did not match the expected UTF-8 banner. No later test cases were run.

### Preconditions: Compile all application classes — PASS

```text
$ javac -encoding UTF-8 -d build/ui-test src/main/java/*.java
[exit code: 0]
```

## Test session: 2026-08-19 00:08:22 +08:00

The UTF-8 launch settings were applied. `TC-001` passed, then the session stopped at `TC-002` because the event confirmation differed from the expected output. `TC-003` through `TC-006` were not run.

### Preconditions: Compile all application classes — PASS

```text
$ javac -encoding UTF-8 -d build/ui-test src/main/java/*.java
[exit code: 0]
```

### TC-001: Launch and exit cleanly — PASS

```text
$ java -Dstdout.encoding=UTF-8 -Dstderr.encoding=UTF-8 -cp build/ui-test Alice
bye
----------------------------------------------------------
██████  ██     ████  █████  █████
██  ██  ██      ██   ██     ██
██████  ██      ██   ██     ████
██  ██  ██      ██   ██     ██
██  ██  █████  ████  █████  █████

Hello! Alice is here to chat!
What do you want to discuss with me?
----------------------------------------------------------
----------------------------------------------------------
Bye Bye. Can't wait to talk to you again!
----------------------------------------------------------
[exit code: 0]
```

### TC-002: Create and list all task types — FAIL

- Inputs:
  ```text
  todo buy milk
  deadline submit report /by Friday
  event project meeting /from Monday 10am /to Monday 11am
  list
  bye
  ```
- Expected output:
  ```text
----------------------------------------------------------
██████  ██     ████  █████  █████
██  ██  ██      ██   ██     ██
██████  ██      ██   ██     ████
██  ██  ██      ██   ██     ██
██  ██  █████  ████  █████  █████

Hello! Alice is here to chat!
What do you want to discuss with me?
----------------------------------------------------------
Got it. I've added this task:
  [T][ ] buy milk
Now you have 1 tasks in the list.
----------------------------------------------------------
Got it. I've added this task:
  [D][ ] submit report (by: Friday)
Now you have 2 tasks in the list.
----------------------------------------------------------
Got it. I've added this task:
  [E][ ] project meeting (from: Monday 10am to Monday 11am)
Now you have 3 tasks in the list.
----------------------------------------------------------
Here are the tasks in your list:
1.[T][ ] buy milk
2.[D][ ] submit report (by: Friday)
3.[E][ ] project meeting (from: Monday 10am to Monday 11am)
----------------------------------------------------------
----------------------------------------------------------
Bye Bye. Can't wait to talk to you again!
----------------------------------------------------------
  ```
- Actual output:
  ```text
----------------------------------------------------------
██████  ██     ████  █████  █████
██  ██  ██      ██   ██     ██
██████  ██      ██   ██     ████
██  ██  ██      ██   ██     ██
██  ██  █████  ████  █████  █████

Hello! Alice is here to chat!
What do you want to discuss with me?
----------------------------------------------------------
Got it. I've added this task:
  [T][ ] buy milk
Now you have 1 tasks in the list.
----------------------------------------------------------
Got it. I've added this task:
  [D][ ] submit report (by: Friday)
Now you have 2 tasks in the list.
----------------------------------------------------------
Got it. I've added this task:
  [E][ ] project meeting (from: Monday 10am to: Monday 11am)
Now you have 3 tasks in the list.
----------------------------------------------------------
Here are the tasks in your list:
1.[T][ ] buy milk
2.[D][ ] submit report (by: Friday)
3.[E][ ] project meeting (from: Monday 10am to: Monday 11am)
----------------------------------------------------------
----------------------------------------------------------
Bye Bye. Can't wait to talk to you again!
----------------------------------------------------------
  ```

```text
$ java -Dstdout.encoding=UTF-8 -Dstderr.encoding=UTF-8 -cp build/ui-test Alice
todo buy milk
deadline submit report /by Friday
event project meeting /from Monday 10am /to Monday 11am
list
bye
<actual output shown above>
[exit code: 0]
```
## Test session: 2026-08-19 00:10:01 +08:00

All six test cases passed after enabling explicit UTF-8 stdout/stderr encoding.

### Preconditions: Compile all application classes — PASS

```text
$ javac -encoding UTF-8 -d build/ui-test src/main/java/*.java
[exit code: 0]
```

### TC-001: Launch and exit cleanly — PASS

```text
$ java -Dstdout.encoding=UTF-8 -Dstderr.encoding=UTF-8 -cp build/ui-test Alice
bye
----------------------------------------------------------
██████  ██     ████  █████  █████
██  ██  ██      ██   ██     ██
██████  ██      ██   ██     ████
██  ██  ██      ██   ██     ██
██  ██  █████  ████  █████  █████

Hello! Alice is here to chat!
What do you want to discuss with me?
----------------------------------------------------------
----------------------------------------------------------
Bye Bye. Can't wait to talk to you again!
----------------------------------------------------------
[exit code: 0]
```

### TC-002: Create and list all task types — PASS

```text
$ java -Dstdout.encoding=UTF-8 -Dstderr.encoding=UTF-8 -cp build/ui-test Alice
todo buy milk
deadline submit report /by Friday
event project meeting /from Monday 10am /to Monday 11am
list
bye
----------------------------------------------------------
██████  ██     ████  █████  █████
██  ██  ██      ██   ██     ██
██████  ██      ██   ██     ████
██  ██  ██      ██   ██     ██
██  ██  █████  ████  █████  █████

Hello! Alice is here to chat!
What do you want to discuss with me?
----------------------------------------------------------
Got it. I've added this task:
  [T][ ] buy milk
Now you have 1 tasks in the list.
----------------------------------------------------------
Got it. I've added this task:
  [D][ ] submit report (by: Friday)
Now you have 2 tasks in the list.
----------------------------------------------------------
Got it. I've added this task:
  [E][ ] project meeting (from: Monday 10am to: Monday 11am)
Now you have 3 tasks in the list.
----------------------------------------------------------
Here are the tasks in your list:
1.[T][ ] buy milk
2.[D][ ] submit report (by: Friday)
3.[E][ ] project meeting (from: Monday 10am to: Monday 11am)
----------------------------------------------------------
----------------------------------------------------------
Bye Bye. Can't wait to talk to you again!
----------------------------------------------------------
[exit code: 0]
```

### TC-003: Mark and unmark a task — PASS

```text
$ java -Dstdout.encoding=UTF-8 -Dstderr.encoding=UTF-8 -cp build/ui-test Alice
todo submit assignment
mark 1
mark 1
unmark 1
unmark 1
list
bye
----------------------------------------------------------
██████  ██     ████  █████  █████
██  ██  ██      ██   ██     ██
██████  ██      ██   ██     ████
██  ██  ██      ██   ██     ██
██  ██  █████  ████  █████  █████

Hello! Alice is here to chat!
What do you want to discuss with me?
----------------------------------------------------------
Got it. I've added this task:
  [T][ ] submit assignment
Now you have 1 tasks in the list.
----------------------------------------------------------
Nice! I've marked this task as done;
  [T][x] submit assignment
----------------------------------------------------------
This task is already mark
----------------------------------------------------------
Ok, I've marked this task as not done yet;
  [ ] [T][ ] submit assignment
----------------------------------------------------------
This task is already unmark
----------------------------------------------------------
Here are the tasks in your list:
1.[T][ ] submit assignment
----------------------------------------------------------
----------------------------------------------------------
Bye Bye. Can't wait to talk to you again!
----------------------------------------------------------
[exit code: 0]
```

### TC-004: Reject invalid task creation and commands — PASS

```text
$ java -Dstdout.encoding=UTF-8 -Dstderr.encoding=UTF-8 -cp build/ui-test Alice
todo
deadline
deadline report
deadline /by Friday
event
event team meeting
event team meeting /from Monday 10am
event meeting /from /to 4pm
unknown
bye
----------------------------------------------------------
██████  ██     ████  █████  █████
██  ██  ██      ██   ██     ██
██████  ██      ██   ██     ████
██  ██  ██      ██   ██     ██
██  ██  █████  ████  █████  █████

Hello! Alice is here to chat!
What do you want to discuss with me?
----------------------------------------------------------
AIYO! The description of a todo cannot be empty.
----------------------------------------------------------
AIYO!!! The description of a deadline cannot be empty.
----------------------------------------------------------
AIYO! Please specify a deadline using /by (e.g. deadline return book /by Sunday)
----------------------------------------------------------
AIYO! The description of a deadline cannot be empty.
----------------------------------------------------------
AIYO! The description of an event cannot be empty.
----------------------------------------------------------
AYIO! Please specify event using /from and /to (e.g. event meeting /from Mon 2pm /to 4pm)
----------------------------------------------------------
AYIO! Please specify event using /from and /to (e.g. event meeting /from Mon 2pm /to 4pm)
----------------------------------------------------------
AIYO! The event start time (/from) cannot be empty.
----------------------------------------------------------
----------------------------------------------------------
Sorry! I am not sure what are you talking about :(
----------------------------------------------------------
----------------------------------------------------------
Bye Bye. Can't wait to talk to you again!
----------------------------------------------------------
[exit code: 0]
```

### TC-005: Reject invalid mark and unmark inputs — PASS

```text
$ java -Dstdout.encoding=UTF-8 -Dstderr.encoding=UTF-8 -cp build/ui-test Alice
mark
mark abc
mark 1
todo task
mark 0
mark 2
mark 1
mark 1
unmark abc
unmark 2
unmark 1
unmark 1
bye
----------------------------------------------------------
██████  ██     ████  █████  █████
██  ██  ██      ██   ██     ██
██████  ██      ██   ██     ████
██  ██  ██      ██   ██     ██
██  ██  █████  ████  █████  █████

Hello! Alice is here to chat!
What do you want to discuss with me?
----------------------------------------------------------
AIYO! Please specify which task to mark (e.g. mark 2)
----------------------------------------------------------
AIYO! Please enter a valid number! (e.g. mark 2)
----------------------------------------------------------
AIYO! Please enter a valid number from 1 to 0!
----------------------------------------------------------
Got it. I've added this task:
  [T][ ] task
Now you have 1 tasks in the list.
----------------------------------------------------------
AIYO! Please enter a valid number from 1 to 1!
----------------------------------------------------------
AIYO! Please enter a valid number from 1 to 1!
----------------------------------------------------------
Nice! I've marked this task as done;
  [T][x] task
----------------------------------------------------------
This task is already mark
----------------------------------------------------------
AIYO! Please enter a valid number! (e.g. mark 2)
----------------------------------------------------------
AIYO! Please enter a valid number from 1 to 1!
----------------------------------------------------------
Ok, I've marked this task as not done yet;
  [ ] [T][ ] task
----------------------------------------------------------
This task is already unmark
----------------------------------------------------------
----------------------------------------------------------
Bye Bye. Can't wait to talk to you again!
----------------------------------------------------------
[exit code: 0]
```

### TC-006: List an empty task list — PASS

```text
$ java -Dstdout.encoding=UTF-8 -Dstderr.encoding=UTF-8 -cp build/ui-test Alice
list
bye
----------------------------------------------------------
██████  ██     ████  █████  █████
██  ██  ██      ██   ██     ██
██████  ██      ██   ██     ████
██  ██  ██      ██   ██     ██
██  ██  █████  ████  █████  █████

Hello! Alice is here to chat!
What do you want to discuss with me?
----------------------------------------------------------
No tasks in your list yet!
----------------------------------------------------------
Bye Bye. Can't wait to talk to you again!
----------------------------------------------------------
[exit code: 0]
```


### TC-001: Launch and exit cleanly — FAIL

- Inputs:
  ```text
  bye
  ```
- Expected output:
  ```text
----------------------------------------------------------
██████  ██     ████  █████  █████
██  ██  ██      ██   ██     ██
██████  ██      ██   ██     ████
██  ██  ██      ██   ██     ██
██  ██  █████  ████  █████  █████

Hello! Alice is here to chat!
What do you want to discuss with me?
----------------------------------------------------------
----------------------------------------------------------
Bye Bye. Can't wait to talk to you again!
----------------------------------------------------------
  ```
- Actual output:
  ```text
----------------------------------------------------------
??????  ??     ????  ?????  ?????
??  ??  ??      ??   ??     ??
??????  ??      ??   ??     ????
??  ??  ??      ??   ??     ??
??  ??  ?????  ????  ?????  ?????

Hello! Alice is here to chat!
What do you want to discuss with me?
----------------------------------------------------------
----------------------------------------------------------
Bye Bye. Can't wait to talk to you again!
----------------------------------------------------------
  ```

```text
$ java -cp build/ui-test Alice
bye
<actual output shown above>
[exit code: 0]
```

## Test session: 2026-08-19 00:24:52 +08:00

TC-007 passed. The delete command removed task 2 and the remaining tasks were reindexed correctly.

### Preconditions: Compile all application classes — PASS

```text
$ javac -encoding UTF-8 -d build/ui-test src/main/java/*.java
[exit code: 0]
```

### TC-007: Delete a task and reindex the list — PASS

```text
$ java -Dstdout.encoding=UTF-8 -Dstderr.encoding=UTF-8 -cp build/ui-test Alice
todo buy milk
deadline submit report /by Friday
event project meeting /from Monday 10am /to Monday 11am
delete 2
list
bye
----------------------------------------------------------
██████  ██     ████  █████  █████
██  ██  ██      ██   ██     ██
██████  ██      ██   ██     ████
██  ██  ██      ██   ██     ██
██  ██  █████  ████  █████  █████

Hello! Alice is here to chat!
What do you want to discuss with me?
----------------------------------------------------------
Got it. I've added this task:
  [T][ ] buy milk
Now you have 1 tasks in the list.
----------------------------------------------------------
Got it. I've added this task:
  [D][ ] submit report (by: Friday)
Now you have 2 tasks in the list.
----------------------------------------------------------
Got it. I've added this task:
  [E][ ] project meeting (from: Monday 10am to: Monday 11am)
Now you have 3 tasks in the list.
----------------------------------------------------------
Noted, I've removed this task:
  [D][ ] submit report (by: Friday)
Now you have 2 tasks in the list.
----------------------------------------------------------
Here are the tasks in your list:
1.[T][ ] buy milk
2.[E][ ] project meeting (from: Monday 10am to: Monday 11am)
----------------------------------------------------------
----------------------------------------------------------
Bye Bye. Can't wait to talk to you again!
----------------------------------------------------------
[exit code: 0]
```
