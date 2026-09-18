# UI Test Plan

## Test case: Show command guide and accept shortcuts

Aim: Present every command in a readable guide and verify that the displayed shortcuts work.

### Input

```input
h
t read book
l
q
```

### Expected output

```expected
____________________________________________________________
Phin
I'm Phin. Apparently I have to deal with this.
What do you want?
____________________________________________________________
    PHIN COMMAND GUIDE
    Add tasks
      todo (t) DESCRIPTION
      deadline (dl) DESCRIPTION /by yyyy-MM-dd
      event (e) DESCRIPTION /from yyyy-MM-dd /to yyyy-MM-dd
    Manage tasks
      list (l)
      mark (m) NUMBER
      unmark (um) NUMBER
      delete (del) NUMBER
      update (u) NUMBER /description TEXT [/by DATE | /from DATE /to DATE]
      find (f) KEYWORD
    Other
      help (h)    Show this guide
      bye (q)     Exit Phin
    Text in parentheses is the shortcut. Replace CAPITALIZED words with your details.
____________________________________________________________
    Fine. I've added this task:
      [T][ ] read book
    Now you have 1 tasks in the list.
____________________________________________________________
    Here are the tasks in your list:
    1.[T][ ] read book
____________________________________________________________
    Finally. Bye.
____________________________________________________________
```

## Test case: Accept harmless whitespace and reject duplicate tasks

Aim: Accept leading, trailing, repeated, and tab whitespace while ensuring duplicate input does not change the list.

### Input

```input
   todo   read book
todo read book
deadline submit /by 2024-03-01
deadline submit /by 2024-03-01
mark   1
   list
bye
```

### Expected output

```expected
____________________________________________________________
Phin
I'm Phin. Apparently I have to deal with this.
What do you want?
____________________________________________________________
    Fine. I've added this task:
      [T][ ] read book
    Now you have 1 tasks in the list.
____________________________________________________________
    Seriously? That task is already in the list.
____________________________________________________________
    Fine. I've added this task:
      [D][ ] submit (by: Mar 01 2024)
    Now you have 2 tasks in the list.
____________________________________________________________
    Seriously? That task is already in the list.
____________________________________________________________
    Fine. I've marked this task as done:
      [T][X] read book
____________________________________________________________
    Here are the tasks in your list:
    1.[T][X] read book
    2.[D][ ] submit (by: Mar 01 2024)
____________________________________________________________
    Finally. Bye.
____________________________________________________________
```

## GUI appearance checks

Launch `phin.Launcher` on Java 25. Verify the clean neutral background, supplied
suited dolphin (upper half in both header and chat) and King avatars,
the header contains only the product name "Phin", and the input hint is visible. Send a command with
Enter and another with Send; check both avatars appear on the appropriate side.
Run an invalid command and check that its response uses the red error style.
Resize to the minimum window width and check long messages wrap without hiding
avatars or input controls. CLI command text below remains unchanged.

The cases below are executed by the project-specific `test-ui` skill. Output comparisons are exact.

The runner compiles sources recursively from `src/main/java` and launches
`phin.Phin`. Packaging does not change the command inputs or expected output below.

To verify the text interface after adding JavaFX, build `cliJar` and pass
`-JarPath build/libs/phin-cli.jar` to
the same runner after building it. The runner copies only the JAR into each
case directory (plus any specified saved-data fixture) and runs the CLI JAR
from that directory. Restart cases reuse that directory to verify persistence.

## Test case: Add and manage all task types

Aim: Verify that todos, deadlines, and events are stored polymorphically and retain their type-specific details when listed or marked.

### Input

```input
todo borrow book
deadline return book /by 2019-10-20
event project meeting /from 2019-10-15 /to 2019-10-16
mark 2
list
bye
```

### Expected output

```expected
____________________________________________________________
Phin
I'm Phin. Apparently I have to deal with this.
What do you want?
____________________________________________________________
    Fine. I've added this task:
      [T][ ] borrow book
    Now you have 1 tasks in the list.
____________________________________________________________
    Fine. I've added this task:
      [D][ ] return book (by: Oct 20 2019)
    Now you have 2 tasks in the list.
____________________________________________________________
    Fine. I've added this task:
      [E][ ] project meeting (from: Oct 15 2019 to: Oct 16 2019)
    Now you have 3 tasks in the list.
____________________________________________________________
    Fine. I've marked this task as done:
      [D][X] return book (by: Oct 20 2019)
____________________________________________________________
    Here are the tasks in your list:
    1.[T][ ] borrow book
    2.[D][X] return book (by: Oct 20 2019)
    3.[E][ ] project meeting (from: Oct 15 2019 to: Oct 16 2019)
____________________________________________________________
    Finally. Bye.
____________________________________________________________
```

## Test case: Reject invalid mark and unmark commands

Aim: Verify that missing, nonnumeric, and out-of-range task numbers produce specific guidance without changing task state.

### Input

```input
todo test Phin
mark
mark two
mark 2
mark 1
unmark 0
unmark 1
list
bye
```

### Expected output

```expected
____________________________________________________________
Phin
I'm Phin. Apparently I have to deal with this.
What do you want?
____________________________________________________________
    Fine. I've added this task:
      [T][ ] test Phin
    Now you have 1 tasks in the list.
____________________________________________________________
    Seriously? Tell me which task to mark. Try: mark NUMBER
____________________________________________________________
    Seriously? Task numbers are, inconveniently, numbers. Try: mark NUMBER
____________________________________________________________
    Seriously? Task 2 isn't in the list. Pick a number from 1 to 1.
____________________________________________________________
    Fine. I've marked this task as done:
      [T][X] test Phin
____________________________________________________________
    Seriously? Task 0 isn't in the list. Pick a number from 1 to 1.
____________________________________________________________
    Fine. I've marked this task as not done:
      [T][ ] test Phin
____________________________________________________________
    Here are the tasks in your list:
    1.[T][ ] test Phin
____________________________________________________________
    Finally. Bye.
____________________________________________________________
```

## Test case: Reject free-form deadline text

Aim: Verify that ambiguous free-form dates are rejected instead of stored.

### Input

```input
deadline do homework /by no idea :-p
bye
```

### Expected output

```expected
____________________________________________________________
Phin
I'm Phin. Apparently I have to deal with this.
What do you want?
____________________________________________________________
    Seriously? Use a valid date in yyyy-MM-dd format (e.g., 2019-10-15).
____________________________________________________________
    Finally. Bye.
____________________________________________________________
```

## Test case: Reject malformed task commands without changing the list

Aim: Verify that missing descriptions and scheduling details produce specific guidance, while valid commands before and after each error still work.

### Input

```input
todo read book
todo
deadline return book
deadline submit report /by 2019-10-18
event meeting /from 2019-10-15
event demo /from 2019-10-15 /to 2019-10-16
list
bye
```

### Expected output

```expected
____________________________________________________________
Phin
I'm Phin. Apparently I have to deal with this.
What do you want?
____________________________________________________________
    Fine. I've added this task:
      [T][ ] read book
    Now you have 1 tasks in the list.
____________________________________________________________
    Seriously? A todo without a description? Give me something to work with.
____________________________________________________________
    Seriously? Deadlines need a description and a time. Try: deadline TASK /by TIME
____________________________________________________________
    Fine. I've added this task:
      [D][ ] submit report (by: Oct 18 2019)
    Now you have 2 tasks in the list.
____________________________________________________________
    Seriously? Events need all their details. Try: event TASK /from START /to END
____________________________________________________________
    Fine. I've added this task:
      [E][ ] demo (from: Oct 15 2019 to: Oct 16 2019)
    Now you have 3 tasks in the list.
____________________________________________________________
    Here are the tasks in your list:
    1.[T][ ] read book
    2.[D][ ] submit report (by: Oct 18 2019)
    3.[E][ ] demo (from: Oct 15 2019 to: Oct 16 2019)
____________________________________________________________
    Finally. Bye.
____________________________________________________________
```

## Test case: Reject an unknown command

Aim: Verify that an unrecognised command produces guidance instead of being silently ignored.

### Input

```input
blah
bye
```

### Expected output

```expected
____________________________________________________________
Phin
I'm Phin. Apparently I have to deal with this.
What do you want?
____________________________________________________________
    Seriously? That command means nothing to me. Type help to see every command.
____________________________________________________________
    Finally. Bye.
____________________________________________________________
```

## Test case: Delete a task and renumber the remaining list

Aim: Verify that deleting a task reports the removed task and that later tasks move up to close the gap.

### Input

```input
todo read book
deadline return book /by 2019-06-06
event project meeting /from 2019-08-06 /to 2019-10-16
todo join sports club
delete 3
list
bye
```

### Expected output

```expected
____________________________________________________________
Phin
I'm Phin. Apparently I have to deal with this.
What do you want?
____________________________________________________________
    Fine. I've added this task:
      [T][ ] read book
    Now you have 1 tasks in the list.
____________________________________________________________
    Fine. I've added this task:
      [D][ ] return book (by: Jun 06 2019)
    Now you have 2 tasks in the list.
____________________________________________________________
    Fine. I've added this task:
      [E][ ] project meeting (from: Aug 06 2019 to: Oct 16 2019)
    Now you have 3 tasks in the list.
____________________________________________________________
    Fine. I've added this task:
      [T][ ] join sports club
    Now you have 4 tasks in the list.
____________________________________________________________
    Noted. I've removed this task:
      [E][ ] project meeting (from: Aug 06 2019 to: Oct 16 2019)
    Now you have 3 tasks in the list.
____________________________________________________________
    Here are the tasks in your list:
    1.[T][ ] read book
    2.[D][ ] return book (by: Jun 06 2019)
    3.[T][ ] join sports club
____________________________________________________________
    Finally. Bye.
____________________________________________________________
```

## Test case: Reject invalid delete commands

Aim: Verify that missing, nonnumeric, and out-of-range task numbers do not remove tasks, while a later valid deletion still works.

### Input

```input
todo keep this
delete
todo remove this
delete two
delete 3
delete 2
list
bye
```

### Expected output

```expected
____________________________________________________________
Phin
I'm Phin. Apparently I have to deal with this.
What do you want?
____________________________________________________________
    Fine. I've added this task:
      [T][ ] keep this
    Now you have 1 tasks in the list.
____________________________________________________________
    Seriously? Tell me which task to delete. Try: delete NUMBER
____________________________________________________________
    Fine. I've added this task:
      [T][ ] remove this
    Now you have 2 tasks in the list.
____________________________________________________________
    Seriously? Task numbers are, inconveniently, numbers. Try: delete NUMBER
____________________________________________________________
    Seriously? Task 3 isn't in the list. Pick a number from 1 to 2.
____________________________________________________________
    Noted. I've removed this task:
      [T][ ] remove this
    Now you have 1 tasks in the list.
____________________________________________________________
    Here are the tasks in your list:
    1.[T][ ] keep this
____________________________________________________________
    Finally. Bye.
____________________________________________________________
```

## Test case: Load saved task types and persist changes across restarts

Aim: Load every task type, interleave rejected and valid changes, and verify unmark and deletion survive a restart.

```saved
T|1|read+book
D|0|return+book|2019-10-20
E|0|meeting|2019-10-15|2019-10-16
```

### Input

```input
unmark 1
delete 9
mark 2
delete 3
list
bye
# restart
list
bye
```

### Expected output

```expected
____________________________________________________________
Phin
I'm Phin. Apparently I have to deal with this.
What do you want?
____________________________________________________________
    Fine. I've marked this task as not done:
      [T][ ] read book
____________________________________________________________
    Seriously? Task 9 isn't in the list. Pick a number from 1 to 3.
____________________________________________________________
    Fine. I've marked this task as done:
      [D][X] return book (by: Oct 20 2019)
____________________________________________________________
    Noted. I've removed this task:
      [E][ ] meeting (from: Oct 15 2019 to: Oct 16 2019)
    Now you have 2 tasks in the list.
____________________________________________________________
    Here are the tasks in your list:
    1.[T][ ] read book
    2.[D][X] return book (by: Oct 20 2019)
____________________________________________________________
    Finally. Bye.
____________________________________________________________
____________________________________________________________
Phin
I'm Phin. Apparently I have to deal with this.
What do you want?
____________________________________________________________
    Here are the tasks in your list:
    1.[T][ ] read book
    2.[D][X] return book (by: Oct 20 2019)
____________________________________________________________
    Finally. Bye.
____________________________________________________________
```


## Test case: Create missing storage and round-trip separators

Aim: Create a missing data folder, preserve pipe and percent characters, and persist an empty list after deleting the last task.

### Input

```input
todo a | b %
bye
# restart
list
delete 1
bye
# restart
list
bye
```

### Expected output

```expected
____________________________________________________________
Phin
I'm Phin. Apparently I have to deal with this.
What do you want?
____________________________________________________________
    Fine. I've added this task:
      [T][ ] a | b %
    Now you have 1 tasks in the list.
____________________________________________________________
    Finally. Bye.
____________________________________________________________
____________________________________________________________
Phin
I'm Phin. Apparently I have to deal with this.
What do you want?
____________________________________________________________
    Here are the tasks in your list:
    1.[T][ ] a | b %
____________________________________________________________
    Noted. I've removed this task:
      [T][ ] a | b %
    Now you have 0 tasks in the list.
____________________________________________________________
    Finally. Bye.
____________________________________________________________
____________________________________________________________
Phin
I'm Phin. Apparently I have to deal with this.
What do you want?
____________________________________________________________
    Here are the tasks in your list:
____________________________________________________________
    Finally. Bye.
____________________________________________________________
```


## Test case: Reject corrupt storage without overwriting it

Aim: Stop safely on an invalid record, including on a second startup.

```saved
T|2|invalid
```

### Input

```input
todo cannot overwrite
bye
# restart
bye
```

### Expected output

```expected
____________________________________________________________
Phin
I'm Phin. Apparently I have to deal with this.
What do you want?
____________________________________________________________
    Couldn't load data/phin.txt. Check the file before restarting; it has not been changed.
____________________________________________________________
____________________________________________________________
Phin
I'm Phin. Apparently I have to deal with this.
What do you want?
____________________________________________________________
    Couldn't load data/phin.txt. Check the file before restarting; it has not been changed.
____________________________________________________________
```

## Test case: Validate calendar dates and persist typed dates

Aim: Interleave valid dates with impossible dates, wrong formats, invalid endpoints, and reversed ranges; verify the list and every date after restart.

### Input

```input
deadline leap day /by 2024-02-29
deadline invalid /by 2023-02-29
event holiday /from 2024-12-31 /to 2025-01-01
deadline invalid /by 2024-04-31
event reversed /from 2024-03-02 /to 2024-03-01
mark 1
event invalid /from 2024-03-01 /to 2024-13-01
deadline invalid /by 2/12/2019 1800
event same day /from 2024-03-01 /to 2024-03-01
deadline invalid /by 2024-2-03
list
bye
# restart
list
bye
```

### Expected output

```expected
____________________________________________________________
Phin
I'm Phin. Apparently I have to deal with this.
What do you want?
____________________________________________________________
    Fine. I've added this task:
      [D][ ] leap day (by: Feb 29 2024)
    Now you have 1 tasks in the list.
____________________________________________________________
    Seriously? Use a valid date in yyyy-MM-dd format (e.g., 2019-10-15).
____________________________________________________________
    Fine. I've added this task:
      [E][ ] holiday (from: Dec 31 2024 to: Jan 01 2025)
    Now you have 2 tasks in the list.
____________________________________________________________
    Seriously? Use a valid date in yyyy-MM-dd format (e.g., 2019-10-15).
____________________________________________________________
    Seriously? An event's end date cannot be before its start date.
____________________________________________________________
    Fine. I've marked this task as done:
      [D][X] leap day (by: Feb 29 2024)
____________________________________________________________
    Seriously? Use a valid date in yyyy-MM-dd format (e.g., 2019-10-15).
____________________________________________________________
    Seriously? Use a valid date in yyyy-MM-dd format (e.g., 2019-10-15).
____________________________________________________________
    Fine. I've added this task:
      [E][ ] same day (from: Mar 01 2024 to: Mar 01 2024)
    Now you have 3 tasks in the list.
____________________________________________________________
    Seriously? Use a valid date in yyyy-MM-dd format (e.g., 2019-10-15).
____________________________________________________________
    Here are the tasks in your list:
    1.[D][X] leap day (by: Feb 29 2024)
    2.[E][ ] holiday (from: Dec 31 2024 to: Jan 01 2025)
    3.[E][ ] same day (from: Mar 01 2024 to: Mar 01 2024)
____________________________________________________________
    Finally. Bye.
____________________________________________________________
____________________________________________________________
Phin
I'm Phin. Apparently I have to deal with this.
What do you want?
____________________________________________________________
    Here are the tasks in your list:
    1.[D][X] leap day (by: Feb 29 2024)
    2.[E][ ] holiday (from: Dec 31 2024 to: Jan 01 2025)
    3.[E][ ] same day (from: Mar 01 2024 to: Mar 01 2024)
____________________________________________________________
    Finally. Bye.
____________________________________________________________
```


## Test case: Preserve legacy dates on failed startup

Aim: Reject an old free-form deadline without overwriting the file, verified by a second startup.

```saved
D|0|return+book|Sunday
```

### Input

```input
todo ignored
bye
# restart
bye
```

### Expected output

```expected
____________________________________________________________
Phin
I'm Phin. Apparently I have to deal with this.
What do you want?
____________________________________________________________
    Couldn't load data/phin.txt. Check the file before restarting; it has not been changed.
____________________________________________________________
____________________________________________________________
Phin
I'm Phin. Apparently I have to deal with this.
What do you want?
____________________________________________________________
    Couldn't load data/phin.txt. Check the file before restarting; it has not been changed.
____________________________________________________________
```


## Test case: Reject impossible saved dates

Aim: Reject invalid calendar dates loaded from disk without crashing.

```saved
E|0|invalid|2024-02-30|2024-03-01
```

### Input

```input
bye
```

### Expected output

```expected
____________________________________________________________
Phin
I'm Phin. Apparently I have to deal with this.
What do you want?
____________________________________________________________
    Couldn't load data/phin.txt. Check the file before restarting; it has not been changed.
____________________________________________________________
```

## Test case: Preserve command boundaries and reject invalid numbers

Aim: Interleave valid commands with command-prefix lookalikes, unexpected arguments, missing numbers, overflow, and extra numeric arguments; list tasks to verify rejected input leaves state unchanged.

### Input

```input
todo keep
todoist reject
mark 1
list extra
unmark
unmark 1
mark 2147483648
bye extra
todo second
mark 1 2
list
bye
```

### Expected output

```expected
____________________________________________________________
Phin
I'm Phin. Apparently I have to deal with this.
What do you want?
____________________________________________________________
    Fine. I've added this task:
      [T][ ] keep
    Now you have 1 tasks in the list.
____________________________________________________________
    Seriously? That command means nothing to me. Type help to see every command.
____________________________________________________________
    Fine. I've marked this task as done:
      [T][X] keep
____________________________________________________________
    Seriously? That command means nothing to me. Type help to see every command.
____________________________________________________________
    Seriously? Tell me which task to unmark. Try: unmark NUMBER
____________________________________________________________
    Fine. I've marked this task as not done:
      [T][ ] keep
____________________________________________________________
    Seriously? Task numbers are, inconveniently, numbers. Try: mark NUMBER
____________________________________________________________
    Seriously? That command means nothing to me. Type help to see every command.
____________________________________________________________
    Fine. I've added this task:
      [T][ ] second
    Now you have 2 tasks in the list.
____________________________________________________________
    Seriously? Task numbers are, inconveniently, numbers. Try: mark NUMBER
____________________________________________________________
    Here are the tasks in your list:
    1.[T][ ] keep
    2.[T][ ] second
____________________________________________________________
    Finally. Bye.
____________________________________________________________
```


## Test case: Find descriptions without changing saved tasks

Aim: Interleave valid and invalid searches, empty lists, case-sensitive substrings, phrases, and dates; list and restart to verify ordering, types, completion status, and persistent state.

### Input

```input
find book
find
todo read book
find book
find
deadline return book /by 2024-03-01
mark 2
find book
findbook
event bookshelf /from 2024-03-01 /to 2024-03-02
find Book
find read book
find Mar
find book
list
bye
# restart
find book
list
bye
```

### Expected output

```expected
____________________________________________________________
Phin
I'm Phin. Apparently I have to deal with this.
What do you want?
____________________________________________________________
    Here are the matching tasks in your list:
____________________________________________________________
    Seriously? Tell me what to find. Try: find KEYWORD
____________________________________________________________
    Fine. I've added this task:
      [T][ ] read book
    Now you have 1 tasks in the list.
____________________________________________________________
    Here are the matching tasks in your list:
    1.[T][ ] read book
____________________________________________________________
    Seriously? Tell me what to find. Try: find KEYWORD
____________________________________________________________
    Fine. I've added this task:
      [D][ ] return book (by: Mar 01 2024)
    Now you have 2 tasks in the list.
____________________________________________________________
    Fine. I've marked this task as done:
      [D][X] return book (by: Mar 01 2024)
____________________________________________________________
    Here are the matching tasks in your list:
    1.[T][ ] read book
    2.[D][X] return book (by: Mar 01 2024)
____________________________________________________________
    Seriously? That command means nothing to me. Type help to see every command.
____________________________________________________________
    Fine. I've added this task:
      [E][ ] bookshelf (from: Mar 01 2024 to: Mar 02 2024)
    Now you have 3 tasks in the list.
____________________________________________________________
    Here are the matching tasks in your list:
____________________________________________________________
    Here are the matching tasks in your list:
    1.[T][ ] read book
____________________________________________________________
    Here are the matching tasks in your list:
____________________________________________________________
    Here are the matching tasks in your list:
    1.[T][ ] read book
    2.[D][X] return book (by: Mar 01 2024)
    3.[E][ ] bookshelf (from: Mar 01 2024 to: Mar 02 2024)
____________________________________________________________
    Here are the tasks in your list:
    1.[T][ ] read book
    2.[D][X] return book (by: Mar 01 2024)
    3.[E][ ] bookshelf (from: Mar 01 2024 to: Mar 02 2024)
____________________________________________________________
    Finally. Bye.
____________________________________________________________
____________________________________________________________
Phin
I'm Phin. Apparently I have to deal with this.
What do you want?
____________________________________________________________
    Here are the matching tasks in your list:
    1.[T][ ] read book
    2.[D][X] return book (by: Mar 01 2024)
    3.[E][ ] bookshelf (from: Mar 01 2024 to: Mar 02 2024)
____________________________________________________________
    Here are the tasks in your list:
    1.[T][ ] read book
    2.[D][X] return book (by: Mar 01 2024)
    3.[E][ ] bookshelf (from: Mar 01 2024 to: Mar 02 2024)
____________________________________________________________
    Finally. Bye.
____________________________________________________________
```

## Test case: Update task details and preserve unchanged state

Aim: Update each supported task type, interleave invalid updates, and restart to verify valid changes persist while rejected input changes nothing.

### Input

```input
todo read book
deadline submit draft /by 2024-03-02
event project meeting /from 2024-03-01 /to 2024-03-03
mark 3
update 1 /description read two books
update 2 /description submit final report /by 2024-03-04
update 3 /to 2024-03-05
update 1 /by 2024-03-06
update 3 /from 2024-03-06
update 2 /by 2024-02-30
update 2 /description
update 9 /description missing
list
bye
# restart
list
bye
```

### Expected output

```expected
____________________________________________________________
Phin
I'm Phin. Apparently I have to deal with this.
What do you want?
____________________________________________________________
    Fine. I've added this task:
      [T][ ] read book
    Now you have 1 tasks in the list.
____________________________________________________________
    Fine. I've added this task:
      [D][ ] submit draft (by: Mar 02 2024)
    Now you have 2 tasks in the list.
____________________________________________________________
    Fine. I've added this task:
      [E][ ] project meeting (from: Mar 01 2024 to: Mar 03 2024)
    Now you have 3 tasks in the list.
____________________________________________________________
    Fine. I've marked this task as done:
      [E][X] project meeting (from: Mar 01 2024 to: Mar 03 2024)
____________________________________________________________
    Fine. I've updated this task:
      [T][ ] read two books
____________________________________________________________
    Fine. I've updated this task:
      [D][ ] submit final report (by: Mar 04 2024)
____________________________________________________________
    Fine. I've updated this task:
      [E][X] project meeting (from: Mar 01 2024 to: Mar 05 2024)
____________________________________________________________
    Seriously? Those fields do not apply to this task type.
____________________________________________________________
    Seriously? An event's end date cannot be before its start date.
____________________________________________________________
    Seriously? Use a valid date in yyyy-MM-dd format (e.g., 2019-10-15).
____________________________________________________________
    Seriously? Update fields cannot be blank.
____________________________________________________________
    Seriously? Task 9 isn't in the list. Pick a number from 1 to 3.
____________________________________________________________
    Here are the tasks in your list:
    1.[T][ ] read two books
    2.[D][ ] submit final report (by: Mar 04 2024)
    3.[E][X] project meeting (from: Mar 01 2024 to: Mar 05 2024)
____________________________________________________________
    Finally. Bye.
____________________________________________________________
____________________________________________________________
Phin
I'm Phin. Apparently I have to deal with this.
What do you want?
____________________________________________________________
    Here are the tasks in your list:
    1.[T][ ] read two books
    2.[D][ ] submit final report (by: Mar 04 2024)
    3.[E][X] project meeting (from: Mar 01 2024 to: Mar 05 2024)
____________________________________________________________
    Finally. Bye.
____________________________________________________________
```
