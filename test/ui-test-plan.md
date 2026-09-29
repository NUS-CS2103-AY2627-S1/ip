# UI Test Plan

This file is the source of truth for the `test-ui` skill.

## Execution notes

- Build from the repository root with `.\gradlew.bat --gradle-user-home .gradle-home build`.
- Run each console case in a fresh temporary working directory; use the absolute path to `build/classes/java/main` in the command. This preserves the real task file.
- Use `java -Dstdout.encoding=UTF-8 -Dstderr.encoding=UTF-8 -Duser.language=en -Duser.country=US -ea -cp <repo>/build/classes/java/main xue.Xue` for console cases, with a 15-second timeout.
- For expectations introduced by "The output must contain, in order", match the listed text in order; welcome art, separators, and intervening response lines may be omitted. Capture stdout and stderr in the session report.
- Case 10 is a separate manual GUI check; report it as not run when native GUI control is unavailable, then continue the console cases.
- Use Java 25 for Java application or build commands.
- Feed each case's inputs through standard input exactly as written.
- Compare output exactly unless a case documents an allowed normalization.
- Run cases in listed order and stop immediately after the first failure.
- For test cases 1–6, start with no `data/duke.txt` file so saved state from another case does not affect the result.
- For GUI checks, run `gradlew run` from the repository root and use test case 10.

## Test cases

<!-- Add cases using this structure. Keep the command and expected output reproducible. -->

### Test case 1: specific input errors

**Aim:** Verify that empty todo descriptions, unknown commands, and invalid task numbers produce specific errors and do not terminate Xue.

**Command:**

```text
java -Dstdout.encoding=UTF-8 -Dstderr.encoding=UTF-8 -Duser.language=en -Duser.country=US -ea -cp <repo>/build/classes/java/main xue.Xue
```

**Inputs:**

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

**Expected output:**

```text
The output must contain, in order:
OOPS!!! The description of a todo cannot be empty. I cannot read your mind!
OOPS!!! I don't know what that means. Use a proper command next time.
OOPS!!! That is not a valid task number. Numbers are not that complicated.
OOPS!!! Tell me which task to mark. I am not a mind reader.
OOPS!!! That task number does not exist. Did you just invent it?
Got it. I've added this task: [T][ ] buy milk
Fine, I've marked this task as done. Happy now?
There. I've undone it. Try to make up your mind next time:
1.[T][ ] buy milk
Finally, you're leaving. Bye. Don't make me miss you.
```

### Test case 2: invalid commands do not corrupt task state

**Aim:** Verify that invalid additions and task-number commands leave existing tasks unchanged while valid commands still work afterward.

**Command:**

```text
java -Dstdout.encoding=UTF-8 -Dstderr.encoding=UTF-8 -Duser.language=en -Duser.country=US -ea -cp <repo>/build/classes/java/main xue.Xue
```

**Inputs:**

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

**Expected output:**

```text
The output must contain, in order:
Got it. I've added this task: [T][ ] finish report
OOPS!!! The description of a todo cannot be empty. I cannot read your mind!
1.[T][ ] finish report
OOPS!!! That task number does not exist. Did you just invent it?
1.[T][ ] finish report
Fine, I've marked this task as done. Happy now?
OOPS!!! That is not a valid task number. Numbers are not that complicated.
1.[T][X] finish report
There. I've undone it. Try to make up your mind next time:
1.[T][ ] finish report
Finally, you're leaving. Bye. Don't make me miss you.
```

### Test case 3: whitespace and malformed command edge cases

**Aim:** Verify that whitespace-only descriptions, malformed command names, and malformed task numbers are rejected without creating tasks.

**Command:**

```text
java -Dstdout.encoding=UTF-8 -Dstderr.encoding=UTF-8 -Duser.language=en -Duser.country=US -ea -cp <repo>/build/classes/java/main xue.Xue
```

**Inputs:**

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

**Expected output:**

```text
The output must contain, in order:
OOPS!!! I don't know what that means. Use a proper command next time.
OOPS!!! The description of a todo cannot be empty. I cannot read your mind!
OOPS!!! I don't know what that means. Use a proper command next time.
Got it. I've added this task: [T][ ] actual task
OOPS!!! That is not a valid task number. Numbers are not that complicated.
OOPS!!! That is not a valid task number. Numbers are not that complicated.
1.[T][ ] actual task
OOPS!!! I don't know what that means. Use a proper command next time.
Finally, you're leaving. Bye. Don't make me miss you.
```

### Test case 4: multiple tasks and boundary indices

**Aim:** Verify that task numbering remains correct with multiple tasks and that only valid boundary indices change state.

**Command:**

```text
java -Dstdout.encoding=UTF-8 -Dstderr.encoding=UTF-8 -Duser.language=en -Duser.country=US -ea -cp <repo>/build/classes/java/main xue.Xue
```

**Inputs:**

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

**Expected output:**

```text
The output must contain, in order:
Got it. I've added this task: [T][ ] first task
Got it. I've added this task: [T][ ] second task
Got it. I've added this task: [T][ ] third task
Fine, I've marked this task as done. Happy now?
1.[T][ ] first task
2.[T][ ] second task
3.[T][X] third task
There. I've undone it. Try to make up your mind next time:
OOPS!!! That task number does not exist. Did you just invent it?
OOPS!!! That task number does not exist. Did you just invent it?
1.[T][ ] first task
2.[T][ ] second task
3.[T][X] third task
Finally, you're leaving. Bye. Don't make me miss you.
```

### Test case 5: task types and date/time text

**Aim:** Verify that todos, deadlines, and events are accepted, while supported numeric dates are parsed and displayed in a readable format.

**Command:**

```text
java -Dstdout.encoding=UTF-8 -Dstderr.encoding=UTF-8 -Duser.language=en -Duser.country=US -ea -cp <repo>/build/classes/java/main xue.Xue
```

**Inputs:**

```text
todo borrow book
deadline return book /by Sunday
event project meeting /from Mon 2pm /to 4pm
deadline submit form /by 2019-10-15
event conference /from 2019-10-15 0900 /to 2019-10-15 1800
list
bye
```

**Expected output:**

```text
The output must contain, in order:
Got it. I've added this task: [T][ ] borrow book
Got it. I've added this task: [D][ ] return book (by: Sunday)
Got it. I've added this task: [E][ ] project meeting (from: Mon 2pm to: 4pm)
Got it. I've added this task: [D][ ] submit form (by: Oct 15 2019)
Got it. I've added this task: [E][ ] conference (from: Oct 15 2019 09:00 AM to: Oct 15 2019 06:00 PM)
1.[T][ ] borrow book
2.[D][ ] return book (by: Sunday)
3.[E][ ] project meeting (from: Mon 2pm to: 4pm)
4.[D][ ] submit form (by: Oct 15 2019)
5.[E][ ] conference (from: Oct 15 2019 09:00 AM to: Oct 15 2019 06:00 PM)
Finally, you're leaving. Bye. Don't make me miss you.
```

### Test case 6: deleting tasks and preserving list numbering

**Aim:** Verify that a valid delete removes the selected task, shifts later tasks, and rejects invalid task numbers.

**Command:**

```text
java -Dstdout.encoding=UTF-8 -Dstderr.encoding=UTF-8 -Duser.language=en -Duser.country=US -ea -cp <repo>/build/classes/java/main xue.Xue
```

**Inputs:**

```text
todo first task
deadline second task /by Friday
event third task /from Mon 2pm /to 4pm
delete 2
delete 9
list
bye
```

**Expected output:**

```text
The output must contain, in order:
Got it. I've added this task: [T][ ] first task
Got it. I've added this task: [D][ ] second task (by: Friday)
Got it. I've added this task: [E][ ] third task (from: Mon 2pm to: 4pm)
Fine, I've removed this task:
  [D][ ] second task (by: Friday)
Now you have 2 tasks in the list.
OOPS!!! That task number does not exist. Did you just invent it?
1.[T][ ] first task
2.[E][ ] third task (from: Mon 2pm to: 4pm)
Finally, you're leaving. Bye. Don't make me miss you.
```

### Test case 7: loading saved tasks

**Aim:** Verify that valid todo, deadline, and event records are loaded with their completion status and date/time details.

**Setup:** Create `data/duke.txt` containing:

```text
T | 1 | read book
D | 0 | return book | June 6th
E | 0 | project meeting | Aug 6th 2pm | Aug 6th 4pm
```

**Inputs:**

```text
list
bye
```

**Command:** Same console command as case 1.

**Expected output:**

```text
The output must contain, in order:
1.[T][X] read book
2.[D][ ] return book (by: June 6th)
3.[E][ ] project meeting (from: Aug 6th 2pm to: Aug 6th 4pm)
Finally, you're leaving. Bye. Don't make me miss you.
```

### Test case 8: malformed saved records

**Aim:** Verify that malformed records are ignored while valid records still load.

**Command:** Same console command as case 1.

**Setup:** Create `data/duke.txt` containing:

```text
X | 0 | unknown
T | 2 | invalid status
D | 0 | missing date
T | 0 |
T | 1 | retained
```

**Inputs:**

```text
list
bye
```

**Expected output:**

```text
The output must contain, in order:
OOPS!!! Some saved tasks were invalid and were skipped.
Here are your tasks. Yes, I did all the work for you:
1.[T][X] retained
Finally, you're leaving. Bye. Don't make me miss you.
```

Only the retained todo may appear in the list.

### Test case 9: finding tasks by description keyword

**Aim:** Verify that `find` returns matching tasks in their original order, searches case-insensitively, and preserves task details.

**Command:**

```text
java -Dstdout.encoding=UTF-8 -Dstderr.encoding=UTF-8 -Duser.language=en -Duser.country=US -ea -cp <repo>/build/classes/java/main xue.Xue
```

**Inputs:**

```text
todo read book
deadline return book /by June 6th
todo buy milk
find BOOK
bye
```

**Expected output:**

```text
The output must contain, in order:
Got it. I've added this task: [T][ ] read book
Got it. I've added this task: [D][ ] return book (by: June 6th)
Got it. I've added this task: [T][ ] buy milk
Here are the matching tasks in your list:
1.[T][ ] read book
2.[D][ ] return book (by: June 6th)
Finally, you're leaving. Bye. Don't make me miss you.
```

### Test case 10: JavaFX GUI interactions

**Aim:** Verify the GUI welcome message, command submission, error handling, existing commands, and safe closing.

**Command:**

```text
gradlew run
```

**Inputs and checks:**

1. Confirm the welcome message is visible when the window opens.
2. Confirm the bottom input area and **Send** button are visible.
3. Enter `todo read book` and click **Send**; confirm both the `You:` message and `Xue:` response appear.
4. Enter `todo buy milk` and press **Enter**; confirm both command and response appear.
5. Click **Send** with empty input; confirm no blank exchange is added.
6. Enter `not a command`; confirm an `OOPS!!!` response appears.
7. Try `list`, `find book`, `mark 1`, `unmark 1`, and `delete 1`.
8. Add enough messages to exceed the window height; confirm the conversation area scrolls while the input area remains visible at the bottom.
9. Close the window with its close button and confirm it exits without an error.

### Test case 11: undo and redo

**Aim:** Verify that undo reverses the latest successful mutation, redo reapplies it, and a new mutation clears redo history.

**Command:**

```text
java -Dstdout.encoding=UTF-8 -Dstderr.encoding=UTF-8 -Duser.language=en -Duser.country=US -ea -cp <repo>/build/classes/java/main xue.Xue
```

**Inputs:**

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

**Expected output:**

The output must contain, in order:

```text
Undo completed. Be alert next time.
Redo completed.
Undo completed. Be alert next time.
OOPS!!! There is nothing to redo.
Undo completed. Be alert next time.
1.[T][ ] first task
Finally, you're leaving. Bye. Don't make me miss you.
```

### Test case 12: undo and redo validation

**Aim:** Verify that undo and redo reject arguments and report empty history correctly.

**Command:**

```text
java -Dstdout.encoding=UTF-8 -Dstderr.encoding=UTF-8 -Duser.language=en -Duser.country=US -ea -cp <repo>/build/classes/java/main xue.Xue
```

**Inputs:**

```text
undo
redo
undo 1
redo 1
bye
```

**Expected output:**

The output must contain, in order:

```text
OOPS!!! Hey you need to DO before you can undo!
OOPS!!! There is nothing to redo.
OOPS!!! The undo command does not accept any arguments.
OOPS!!! The redo command does not accept any arguments.
Finally, you're leaving. Bye. Don't make me miss you.
```


### Test case 13: invalid dates and backwards events

**Aim:** Reject invalid calendar dates, malformed numeric dates, invalid times, missing dates, and events ending before they start without changing tasks or undo/redo history.

**Command:** Same console command as case 1.

**Inputs:**

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

**Expected output:**

```text
The output must contain, in order:
Undo completed. Be alert next time.
OOPS!!! That date is invalid. Use yyyy-MM-dd or yyyy-MM-dd HHmm.
OOPS!!! That date is invalid. Use yyyy-MM-dd or yyyy-MM-dd HHmm.
OOPS!!! That date is invalid. Use yyyy-MM-dd or yyyy-MM-dd HHmm.
OOPS!!! An event cannot end before it starts.
OOPS!!! That date is invalid. Use yyyy-MM-dd or yyyy-MM-dd HHmm.
OOPS!!! That date is invalid. Use yyyy-MM-dd or yyyy-MM-dd HHmm.
OOPS!!! That date is invalid. Use yyyy-MM-dd or yyyy-MM-dd HHmm.
Here are your tasks. Yes, I did all the work for you:
1.[T][ ] retained
Redo completed.
1.[T][ ] retained
2.[T][ ] redo me
Finally, you're leaving. Bye. Don't make me miss you.
```

The saved file must contain only `T | 0 | retained` and `T | 0 | redo me`.

### Test case 14: invalid saved dates and valid boundary dates

**Aim:** Warn about invalid saved dates and backwards events, retain valid tasks, and leave the file unchanged on loading.

**Command:** Same console command as case 1.

**Setup:** Create `data/duke.txt` containing:

```text
T | 1 | retained
D | 0 | impossible | 2026-09-31 1200
D | 0 | malformed | 2026/09/24 1200
E | 0 | impossible event | 2026-09-31 1400 | 2026-10-01 1600
E | 0 | backwards | 2026-09-26 1600 | 2026-09-25 1400
D | 0 | leap day | 2028-02-29 0000
E | 1 | overnight | 2026-09-30 2359 | 2026-10-01 0000
```

**Inputs:**

```text
list
bye
```

**Expected output:**

```text
The output must contain, in order:
OOPS!!! Some saved tasks were invalid and were skipped.
Here are your tasks. Yes, I did all the work for you:
1.[T][X] retained
2.[D][ ] leap day (by: Feb 29 2028 12:00 AM)
3.[E][X] overnight (from: Sep 30 2026 11:59 PM to: Oct 01 2026 12:00 AM)
Finally, you're leaving. Bye. Don't make me miss you.
```

Only these three tasks may appear in the list. The saved file must remain byte-for-byte unchanged.

### Test case 15: valid dates persist and reload

**Aim:** Verify that leap days, midnight, equal event endpoints, and overnight events survive saving and reloading.

**Command:** Same console command as case 1, run twice in the same temporary directory.

**Inputs:**

```text
deadline leap day /by 2028-02-29 0000
event instant /from 2026-09-30 2359 /to 2026-09-30 2359
event overnight /from 2026-09-30 2359 /to 2026-10-01 0000
bye
```

**Expected output:**

```text
The output must contain, in order:
Got it. I've added this task: [D][ ] leap day (by: Feb 29 2028 12:00 AM)
Got it. I've added this task: [E][ ] instant (from: Sep 30 2026 11:59 PM to: Sep 30 2026 11:59 PM)
Got it. I've added this task: [E][ ] overnight (from: Sep 30 2026 11:59 PM to: Oct 01 2026 12:00 AM)
Finally, you're leaving. Bye. Don't make me miss you.
```

**Reload inputs:**

```text
list
bye
```

**Reload expected output:**

```text
The output must contain, in order:
1.[D][ ] leap day (by: Feb 29 2028 12:00 AM)
2.[E][ ] instant (from: Sep 30 2026 11:59 PM to: Sep 30 2026 11:59 PM)
3.[E][ ] overnight (from: Sep 30 2026 11:59 PM to: Oct 01 2026 12:00 AM)
Finally, you're leaving. Bye. Don't make me miss you.
```

No storage warning may appear on reload.
