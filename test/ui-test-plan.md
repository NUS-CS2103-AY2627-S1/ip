# UI Test Plan

The tutorial change launches the JavaFX graphical interface by default rather than
the command-line chatbot. The existing command-line test cases remain applicable
when `topaz.Topaz` is run directly. This plan cannot automate the current GUI
iteration; manually verify that submitting text with Enter or Send adds a
right-aligned user dialog and a left-aligned response dialog. Verify that `todo`,
`deadline`, `event`, `list`, `find`, `mark`, `unmark`, and `delete` produce the
same responses and save task changes as their command-line equivalents. Verify
that a welcome dialog appears on launch, user commands appear as compact forest-green bubbles without avatars and Topaz
responses appear as full-width cards labeled GRONK!, and `bye` disables both the input field and Send button after displaying
the farewell message.

Record every requested command-line UI test case here before running the test session.
The expected output must match stdout exactly, including prompts, separators, spaces,
and line breaks.

## Test case format

```markdown
### Test case: <name>

Aim: <what this verifies>

Input:
```text
<commands, one per line>
```

Expected output:
```text
<exact stdout>
```
```

The `test-ui` skill runs each case in a fresh process and stops immediately at the
first failure, showing the actual and expected output.

Automated UI tests use a temporary save-file path so that they do not overwrite a
user's `data/Topaz.txt` file.

## Code quality increment

The code-quality refactorings preserve command-line behavior. The existing UI
cases remain applicable without revision.

## Assertion increment

The assertions added in this increment validate internal model and command
invariants only. They do not change command-line input or output, so the existing
UI cases remain applicable without revision.

## Persistence integration check

Aim: Verify that a fresh chatbot process loads saved tasks with their type, completion status, and time fields intact.

Setup: Create `build/reload-ui-data.txt` with the following contents and launch with
`-Dtopaz.dataFile=build/reload-ui-data.txt`. Never overwrite the real save file:

```text
D | 1 | return book | 2026-12-07
E | 0 | project meeting | 2026-12-08T14:00 | 2026-12-08T16:00
```

Input:

```text
list
bye
```

Expected output after startup:

```text
GRONK!
 Gronk guards your task pile:
 1.[D][X] return book (by: Dec 07 2026)
 2.[E][ ] project meeting (from: Dec 08 2026 1400 to: Dec 08 2026 1600)
```

### Test case: Interleaved task creation and invalid commands

Aim: Verify that valid tasks are stored while invalid task commands do not change the list.

Input:
```text
todo read book
todo
list
deadline return book /by 2026-12-07
deadline return book
list
event project meeting /from 8/12/2026 1400 /to 8/12/2026 1600
event project meeting /from 8/12/2026 1400
list
bye
```

Expected output:
```text
____________________________________________________________
GRONK!
I'm Gronk, your mighty task keeper.
Give Gronk a task. We crush it together!
____________________________________________________________
GRONK!
 Gronk grabbed a new task:
   [T][ ] read book
 Now you have 1 tasks in the list.
____________________________________________________________
GRONK!
 Gronk hit a snag. The description of a todo cannot be empty.
____________________________________________________________
GRONK!
 Gronk guards your task pile:
 1.[T][ ] read book
____________________________________________________________
GRONK!
 Gronk grabbed a new task:
   [D][ ] return book (by: Dec 07 2026)
 Now you have 2 tasks in the list.
____________________________________________________________
GRONK!
 Gronk hit a snag. Use: deadline <description> /by <time>.
____________________________________________________________
GRONK!
 Gronk guards your task pile:
 1.[T][ ] read book
 2.[D][ ] return book (by: Dec 07 2026)
____________________________________________________________
GRONK!
 Gronk grabbed a new task:
   [E][ ] project meeting (from: Dec 08 2026 1400 to: Dec 08 2026 1600)
 Now you have 3 tasks in the list.
____________________________________________________________
GRONK!
 Gronk hit a snag. Use: event <description> /from <time> /to <time>.
____________________________________________________________
GRONK!
 Gronk guards your task pile:
 1.[T][ ] read book
 2.[D][ ] return book (by: Dec 07 2026)
 3.[E][ ] project meeting (from: Dec 08 2026 1400 to: Dec 08 2026 1600)
____________________________________________________________
GRONK!
 Gronk rests now. Come back strong!
____________________________________________________________
```

### Test case: Find tasks by keyword

Aim: Verify that find returns matching tasks in list order and rejects a missing keyword.

Input:
```text
todo read book
deadline return book /by 2026-12-07
todo buy milk
find book
find
bye
```

Expected output:
```text
____________________________________________________________
GRONK!
I'm Gronk, your mighty task keeper.
Give Gronk a task. We crush it together!
____________________________________________________________
GRONK!
 Gronk grabbed a new task:
   [T][ ] read book
 Now you have 1 tasks in the list.
____________________________________________________________
GRONK!
 Gronk grabbed a new task:
   [D][ ] return book (by: Dec 07 2026)
 Now you have 2 tasks in the list.
____________________________________________________________
GRONK!
 Gronk grabbed a new task:
   [T][ ] buy milk
 Now you have 3 tasks in the list.
____________________________________________________________
GRONK!
 Gronk sniffed out these tasks:
 1.[T][ ] read book
 2.[D][ ] return book (by: Dec 07 2026)
____________________________________________________________
GRONK!
 Gronk hit a snag. Please provide a keyword after find.
____________________________________________________________
GRONK!
 Gronk rests now. Come back strong!
____________________________________________________________
```

### Test case: Reserved save-file delimiter

Aim: Verify that task details containing `|` are rejected and do not change the task list, because `|` separates fields in the save file.

Input:
```text
todo read | book
list
deadline return book /by Sun|day
list
event meeting /from 2|pm /to 4pm
list
bye
```

Expected output:
```text
____________________________________________________________
GRONK!
I'm Gronk, your mighty task keeper.
Give Gronk a task. We crush it together!
____________________________________________________________
GRONK!
 Gronk hit a snag. Task details cannot contain the | character.
____________________________________________________________
GRONK!
 Gronk guards your task pile:
 No tasks yet. Try: todo read book
____________________________________________________________
GRONK!
 Gronk hit a snag. Task details cannot contain the | character.
____________________________________________________________
GRONK!
 Gronk guards your task pile:
 No tasks yet. Try: todo read book
____________________________________________________________
GRONK!
 Gronk hit a snag. Task details cannot contain the | character.
____________________________________________________________
GRONK!
 Gronk guards your task pile:
 No tasks yet. Try: todo read book
____________________________________________________________
GRONK!
 Gronk rests now. Come back strong!
____________________________________________________________
```

### Test case: Save changed task list

Aim: Verify that adding, marking, and deleting tasks saves the remaining task list to `data/Topaz.txt`.

Input:
```text
todo read book
mark 1
deadline return book /by 2026-12-07
event project meeting /from 8/12/2026 1400 /to 8/12/2026 1600
delete 1
bye
```

Expected output:
```text
____________________________________________________________
GRONK!
I'm Gronk, your mighty task keeper.
Give Gronk a task. We crush it together!
____________________________________________________________
GRONK!
 Gronk grabbed a new task:
   [T][ ] read book
 Now you have 1 tasks in the list.
____________________________________________________________
GRONK!
 Task crushed! Gronk marks it done:
   [T][X] read book
____________________________________________________________
GRONK!
 Gronk grabbed a new task:
   [D][ ] return book (by: Dec 07 2026)
 Now you have 2 tasks in the list.
____________________________________________________________
GRONK!
 Gronk grabbed a new task:
   [E][ ] project meeting (from: Dec 08 2026 1400 to: Dec 08 2026 1600)
 Now you have 3 tasks in the list.
____________________________________________________________
GRONK!
 Gronk tossed this task out:
   [T][X] read book
 Now you have 2 tasks in the list.
____________________________________________________________
GRONK!
 Gronk rests now. Come back strong!
____________________________________________________________
```

Expected `data/Topaz.txt` after this case:

```text
D | 0 | return book | 2026-12-07
E | 0 | project meeting | 2026-12-08T14:00 | 2026-12-08T16:00
```

### Test case: Interleaved mark, unmark, and invalid task numbers

Aim: Verify that invalid mark and unmark commands do not change completion status.

Input:
```text
todo read book
mark 1
mark zero
list
unmark 1
unmark 2
list
unknown command
list
bye
```

Expected output:
```text
____________________________________________________________
GRONK!
I'm Gronk, your mighty task keeper.
Give Gronk a task. We crush it together!
____________________________________________________________
GRONK!
 Gronk grabbed a new task:
   [T][ ] read book
 Now you have 1 tasks in the list.
____________________________________________________________
GRONK!
 Task crushed! Gronk marks it done:
   [T][X] read book
____________________________________________________________
GRONK!
 Gronk hit a snag. The task number must be an integer.
____________________________________________________________
GRONK!
 Gronk guards your task pile:
 1.[T][X] read book
____________________________________________________________
GRONK!
 Back to the pile! Gronk marks it not done:
   [T][ ] read book
____________________________________________________________
GRONK!
 Gronk hit a snag. That task number is not in your list.
____________________________________________________________
GRONK!
 Gronk guards your task pile:
 1.[T][ ] read book
____________________________________________________________
GRONK!
 Gronk hit a snag. I'm sorry, but I don't know what that means.
____________________________________________________________
GRONK!
 Gronk guards your task pile:
 1.[T][ ] read book
____________________________________________________________
GRONK!
 Gronk rests now. Come back strong!
____________________________________________________________
```

Both cases intentionally interleave valid and invalid commands so that state changes
caused by rejected inputs are visible in later `list` output.

### Test case: Delete tasks and preserve list order

Aim: Verify that deleting a task removes the correct item, shifts later tasks up, and rejects a missing task number without changing state.

Input:
```text
todo first
deadline second /by 2026-12-09
event third /from 10/12/2026 1400 /to 10/12/2026 1600
delete 2
list
delete
list
bye
```

Expected output:
```text
____________________________________________________________
GRONK!
I'm Gronk, your mighty task keeper.
Give Gronk a task. We crush it together!
____________________________________________________________
GRONK!
 Gronk grabbed a new task:
   [T][ ] first
 Now you have 1 tasks in the list.
____________________________________________________________
GRONK!
 Gronk grabbed a new task:
   [D][ ] second (by: Dec 09 2026)
 Now you have 2 tasks in the list.
____________________________________________________________
GRONK!
 Gronk grabbed a new task:
   [E][ ] third (from: Dec 10 2026 1400 to: Dec 10 2026 1600)
 Now you have 3 tasks in the list.
____________________________________________________________
GRONK!
 Gronk tossed this task out:
   [D][ ] second (by: Dec 09 2026)
 Now you have 2 tasks in the list.
____________________________________________________________
GRONK!
 Gronk guards your task pile:
 1.[T][ ] first
 2.[E][ ] third (from: Dec 10 2026 1400 to: Dec 10 2026 1600)
____________________________________________________________
GRONK!
 Gronk hit a snag. Please provide a task number after delete.
____________________________________________________________
GRONK!
 Gronk guards your task pile:
 1.[T][ ] first
 2.[E][ ] third (from: Dec 10 2026 1400 to: Dec 10 2026 1600)
____________________________________________________________
GRONK!
 Gronk rests now. Come back strong!
____________________________________________________________
```

### Test case: Invalid task numbers do not change state

Aim: Verify that out-of-range and non-numeric mark commands do not alter or add tasks.

Input:
```text
todo alpha
mark 2
list
todo beta
unmark abc
list
blah
list
bye
```

Expected output:
```text
____________________________________________________________
GRONK!
I'm Gronk, your mighty task keeper.
Give Gronk a task. We crush it together!
____________________________________________________________
GRONK!
 Gronk grabbed a new task:
   [T][ ] alpha
 Now you have 1 tasks in the list.
____________________________________________________________
GRONK!
 Gronk hit a snag. That task number is not in your list.
____________________________________________________________
GRONK!
 Gronk guards your task pile:
 1.[T][ ] alpha
____________________________________________________________
GRONK!
 Gronk grabbed a new task:
   [T][ ] beta
 Now you have 2 tasks in the list.
____________________________________________________________
GRONK!
 Gronk hit a snag. The task number must be an integer.
____________________________________________________________
GRONK!
 Gronk guards your task pile:
 1.[T][ ] alpha
 2.[T][ ] beta
____________________________________________________________
GRONK!
 Gronk hit a snag. I'm sorry, but I don't know what that means.
____________________________________________________________
GRONK!
 Gronk guards your task pile:
 1.[T][ ] alpha
 2.[T][ ] beta
____________________________________________________________
GRONK!
 Gronk rests now. Come back strong!
____________________________________________________________
```

### Test case: Empty event fields are rejected

Aim: Verify that an event with an empty `from` value is rejected and does not corrupt the task list.

Input:
```text
event meeting /from /to 4pm
list
bye
```

Expected output:
```text
____________________________________________________________
GRONK!
I'm Gronk, your mighty task keeper.
Give Gronk a task. We crush it together!
____________________________________________________________
GRONK!
 Gronk hit a snag. The event start time cannot be empty.
____________________________________________________________
GRONK!
 Gronk guards your task pile:
 No tasks yet. Try: todo read book
____________________________________________________________
GRONK!
 Gronk rests now. Come back strong!
____________________________________________________________
```

### Test case: First run without a save file

Aim: Verify that Topaz starts with an empty task list and creates its save file when no saved data exists.

Input:
```text
list
todo first task
list
bye
```

Expected output:
```text
____________________________________________________________
GRONK!
I'm Gronk, your mighty task keeper.
Give Gronk a task. We crush it together!
____________________________________________________________
GRONK!
 Gronk guards your task pile:
 No tasks yet. Try: todo read book
____________________________________________________________
GRONK!
 Gronk grabbed a new task:
   [T][ ] first task
 Now you have 1 tasks in the list.
____________________________________________________________
GRONK!
 Gronk guards your task pile:
 1.[T][ ] first task
____________________________________________________________
GRONK!
 Gronk rests now. Come back strong!
____________________________________________________________
```

### Test case: Parse and display dates and times

Aim: Verify that valid dates are stored as date/time values, displayed in a readable format, and that invalid dates do not change the list.

Input:
```text
deadline return book /by 2/12/2019 1800
list
deadline review notes /by 2019-10-15
list
deadline invalid date /by 31/02/2019 1800
list
event project meeting /from 15/10/2019 1400 /to 15/10/2019 1600
list
event invalid event /from 15/10/2019 1400 /to 2019-02-29
list
bye
```

Expected output:
```text
____________________________________________________________
GRONK!
I'm Gronk, your mighty task keeper.
Give Gronk a task. We crush it together!
____________________________________________________________
GRONK!
 Gronk grabbed a new task:
   [D][ ] return book (by: Dec 02 2019 1800)
 Now you have 1 tasks in the list.
____________________________________________________________
GRONK!
 Gronk guards your task pile:
 1.[D][ ] return book (by: Dec 02 2019 1800)
____________________________________________________________
GRONK!
 Gronk grabbed a new task:
   [D][ ] review notes (by: Oct 15 2019)
 Now you have 2 tasks in the list.
____________________________________________________________
GRONK!
 Gronk guards your task pile:
 1.[D][ ] return book (by: Dec 02 2019 1800)
 2.[D][ ] review notes (by: Oct 15 2019)
____________________________________________________________
GRONK!
 Gronk hit a snag. Use a date as yyyy-MM-dd or d/M/yyyy HHmm.
____________________________________________________________
GRONK!
 Gronk guards your task pile:
 1.[D][ ] return book (by: Dec 02 2019 1800)
 2.[D][ ] review notes (by: Oct 15 2019)
____________________________________________________________
GRONK!
 Gronk grabbed a new task:
   [E][ ] project meeting (from: Oct 15 2019 1400 to: Oct 15 2019 1600)
 Now you have 3 tasks in the list.
____________________________________________________________
GRONK!
 Gronk guards your task pile:
 1.[D][ ] return book (by: Dec 02 2019 1800)
 2.[D][ ] review notes (by: Oct 15 2019)
 3.[E][ ] project meeting (from: Oct 15 2019 1400 to: Oct 15 2019 1600)
____________________________________________________________
GRONK!
 Gronk hit a snag. Use a date as yyyy-MM-dd or d/M/yyyy HHmm.
____________________________________________________________
GRONK!
 Gronk guards your task pile:
 1.[D][ ] return book (by: Dec 02 2019 1800)
 2.[D][ ] review notes (by: Oct 15 2019)
 3.[E][ ] project meeting (from: Oct 15 2019 1400 to: Oct 15 2019 1600)
____________________________________________________________
GRONK!
 Gronk rests now. Come back strong!
____________________________________________________________
```

### Test case: Display date-only event times

Aim: Verify that date-only event values are stored as dates and displayed without an artificial midnight time.

Input:
```text
event conference /from 2019-10-15 /to 2019-10-16
list
bye
```

Expected output:
```text
____________________________________________________________
GRONK!
I'm Gronk, your mighty task keeper.
Give Gronk a task. We crush it together!
____________________________________________________________
GRONK!
 Gronk grabbed a new task:
   [E][ ] conference (from: Oct 15 2019 to: Oct 16 2019)
 Now you have 1 tasks in the list.
____________________________________________________________
GRONK!
 Gronk guards your task pile:
 1.[E][ ] conference (from: Oct 15 2019 to: Oct 16 2019)
____________________________________________________________
GRONK!
 Gronk rests now. Come back strong!
____________________________________________________________
```

### Test case: Bare numbered commands and empty time fields

Aim: Verify that missing task numbers and empty deadline or event time fields are rejected without changing existing tasks.

Input:
```text
todo read
mark
list
unmark
list
deadline return /by
list
event meeting /from /to 4pm
list
event meeting /from 2pm /to
list
bye
```

Expected output:
```text
____________________________________________________________
GRONK!
I'm Gronk, your mighty task keeper.
Give Gronk a task. We crush it together!
____________________________________________________________
GRONK!
 Gronk grabbed a new task:
   [T][ ] read
 Now you have 1 tasks in the list.
____________________________________________________________
GRONK!
 Gronk hit a snag. Please provide a task number after mark.
____________________________________________________________
GRONK!
 Gronk guards your task pile:
 1.[T][ ] read
____________________________________________________________
GRONK!
 Gronk hit a snag. Please provide a task number after unmark.
____________________________________________________________
GRONK!
 Gronk guards your task pile:
 1.[T][ ] read
____________________________________________________________
GRONK!
 Gronk hit a snag. The deadline time cannot be empty.
____________________________________________________________
GRONK!
 Gronk guards your task pile:
 1.[T][ ] read
____________________________________________________________
GRONK!
 Gronk hit a snag. The event start time cannot be empty.
____________________________________________________________
GRONK!
 Gronk guards your task pile:
 1.[T][ ] read
____________________________________________________________
GRONK!
 Gronk hit a snag. The event end time cannot be empty.
____________________________________________________________
GRONK!
 Gronk guards your task pile:
 1.[T][ ] read
____________________________________________________________
GRONK!
 Gronk rests now. Come back strong!
____________________________________________________________
```

### Test case: Fixed-duration task management and validation

Aim: Verify that duration tasks display their fixed duration and work with existing task commands, while invalid duration inputs do not change the list.

Input:
```text
duration read the sales report /for 2h
duration take a break /for 90m
list
mark 1
find hours
duration invalid duration /for 0h
duration missing duration /for
duration /for 2h
duration duplicate duration /for 1h /for 2h
bye
```

Expected output:
```text
____________________________________________________________
GRONK!
I'm Gronk, your mighty task keeper.
Give Gronk a task. We crush it together!
____________________________________________________________
GRONK!
 Gronk grabbed a new task:
   [F][ ] read the sales report (for: 2 hours)
 Now you have 1 tasks in the list.
____________________________________________________________
GRONK!
 Gronk grabbed a new task:
   [F][ ] take a break (for: 90 minutes)
 Now you have 2 tasks in the list.
____________________________________________________________
GRONK!
 Gronk guards your task pile:
 1.[F][ ] read the sales report (for: 2 hours)
 2.[F][ ] take a break (for: 90 minutes)
____________________________________________________________
GRONK!
 Task crushed! Gronk marks it done:
   [F][X] read the sales report (for: 2 hours)
____________________________________________________________
GRONK!
 Gronk sniffed out these tasks:
 1.[F][X] read the sales report (for: 2 hours)
____________________________________________________________
GRONK!
 Gronk hit a snag. Use a duration as a positive whole number followed by h or m.
____________________________________________________________
GRONK!
 Gronk hit a snag. The duration cannot be empty.
____________________________________________________________
GRONK!
 Gronk hit a snag. The description of a duration task cannot be empty.
____________________________________________________________
GRONK!
 Gronk hit a snag. Use: duration <description> /for <duration>.
____________________________________________________________
GRONK!
 Gronk rests now. Come back strong!
____________________________________________________________
```

### Test case: Fixed-duration task persistence

Aim: Verify that a marked duration task is saved using the fixed-duration record format.

Input:
```text
duration read the sales report /for 2h
mark 1
bye
```

Expected output:
```text
____________________________________________________________
GRONK!
I'm Gronk, your mighty task keeper.
Give Gronk a task. We crush it together!
____________________________________________________________
GRONK!
 Gronk grabbed a new task:
   [F][ ] read the sales report (for: 2 hours)
 Now you have 1 tasks in the list.
____________________________________________________________
GRONK!
 Task crushed! Gronk marks it done:
   [F][X] read the sales report (for: 2 hours)
____________________________________________________________
GRONK!
 Gronk rests now. Come back strong!
____________________________________________________________
```

Expected `data/ui-test.txt` after this case:

```text
F | 1 | read the sales report | 120
```

## Asymmetric GUI presentation

CLI behavior is unchanged; existing cases remain applicable. The CLI runner
cannot verify JavaFX styling. Manually check the welcome card, Enter and Send,
a long command, a multi-line list, and window resizing: commands must wrap in
right-aligned forest-green bubbles, while responses fill the width with a GRONK! heading
and left accent. No avatars or clipped text should appear.

## Error highlighting

CLI messages are unchanged; run the existing invalid-command cases. JavaFX
JUnit tests check the error heading, text color, and isolation from normal
cards. Manually submit `unknown`, `todo`, `mark 99`, and an invalid deadline:
each response should have a GRONK! heading and separate red ERROR label, pale red background, dark
red text, and red left border. Then submit `list`: the new card should use
normal styling while previous error cards remain highlighted. Verify long
errors wrap and Enter and Send both retain focus/scroll behavior.

## Gronk personality

All expected CLI replies now include GRONK! and Gronk's task-keeper voice.
The existing cases cover greetings, all task commands, errors, and farewell.
Manually check the window title is Gronk, each app response has a bold 26px
green GRONK! heading, and user text has no added heading. Errors retain their
red card and separate ERROR label while GRONK! stays green. Resize the window
and submit with Enter and Send; the cry should appear once per response.
The data file and command syntax remain compatible with existing tasks.

### Test case: Whitespace and ambiguous commands

Aim: Accept harmless spacing and reject empty or ambiguous commands without changing tasks or exiting.

The input block intentionally contains trailing spaces and a whitespace-only line.

Input:
```text
  todo   read book  
list extra
bye extra
deadline /by 2026-12-07
deadline return /by 2026-12-07 /by 2026-12-08
event meet /from 2026-12-07 /to 2026-12-08 /to
mark +1
   
 list 
 bye 
```

Expected output:
```text
____________________________________________________________
GRONK!
I'm Gronk, your mighty task keeper.
Give Gronk a task. We crush it together!
____________________________________________________________
GRONK!
 Gronk grabbed a new task:
   [T][ ] read book
 Now you have 1 tasks in the list.
____________________________________________________________
GRONK!
 Gronk hit a snag. Use: list (without extra arguments).
____________________________________________________________
GRONK!
 Gronk hit a snag. Use: bye (without extra arguments).
____________________________________________________________
GRONK!
 Gronk hit a snag. The description of a deadline cannot be empty.
____________________________________________________________
GRONK!
 Gronk hit a snag. Use: deadline <description> /by <time>.
____________________________________________________________
GRONK!
 Gronk hit a snag. Use: event <description> /from <time> /to <time>.
____________________________________________________________
GRONK!
 Gronk hit a snag. The task number must be an integer.
____________________________________________________________
GRONK!
 Gronk hit a snag. Please enter a command, such as list or todo <description>.
____________________________________________________________
GRONK!
 Gronk guards your task pile:
 1.[T][ ] read book
____________________________________________________________
GRONK!
 Gronk rests now. Come back strong!
____________________________________________________________
```

### Test case: Reject invalid dates and duplicate tasks

Aim: Reject zero-length/reversed events, impossible dates, and duplicates while preserving valid tasks.

Input:
```text
todo read book
mark 1
todo READ BOOK
event meeting /from 2026-12-07 /to 2026-12-07
event meeting /from 2026-12-08 /to 2026-12-07
deadline impossible /by 2026-02-30
duration stretch /for 1h
duration STRETCH /for 60m
list
bye
```

Expected output:
```text
____________________________________________________________
GRONK!
I'm Gronk, your mighty task keeper.
Give Gronk a task. We crush it together!
____________________________________________________________
GRONK!
 Gronk grabbed a new task:
   [T][ ] read book
 Now you have 1 tasks in the list.
____________________________________________________________
GRONK!
 Task crushed! Gronk marks it done:
   [T][X] read book
____________________________________________________________
GRONK!
 Gronk hit a snag. That task already exists. Use list to find it.
____________________________________________________________
GRONK!
 Gronk hit a snag. The event end must be after its start.
____________________________________________________________
GRONK!
 Gronk hit a snag. The event end must be after its start.
____________________________________________________________
GRONK!
 Gronk hit a snag. Use a date as yyyy-MM-dd or d/M/yyyy HHmm.
____________________________________________________________
GRONK!
 Gronk grabbed a new task:
   [F][ ] stretch (for: 1 hour)
 Now you have 2 tasks in the list.
____________________________________________________________
GRONK!
 Gronk hit a snag. That task already exists. Use list to find it.
____________________________________________________________
GRONK!
 Gronk guards your task pile:
 1.[T][X] read book
 2.[F][ ] stretch (for: 1 hour)
____________________________________________________________
GRONK!
 Gronk rests now. Come back strong!
____________________________________________________________
```

## Storage environment increment

Normal command output remains unchanged; rerun all CLI sessions. JUnit covers
missing files, blocked parent paths, directory targets, read-only save files,
invalid configured paths, malformed UTF-8, a byte-order mark, corrupt line
numbers, save replacement, encoding failure during a write, recovery after repair, and mutation rollback.
The read-only test skips only if the host account overrides file permissions.
Disk-full and abrupt-power-loss scenarios are not simulated by this CLI runner.

For a GUI check, use a disposable `topaz.dataFile` containing an invalid record,
submit `list`, and verify a red error card reports the bad line. Repair that
file and submit `list` again; it should recover without loading partial data.
Do not run this check against the user's real saved tasks.

The separate `test/storage-ui-test-plan.md` checks startup output for corrupt
saved data using its own disposable fixture.


## Finalization checks

Search results retain full-list numbers. The GUI controller is also covered by
JUnit using real FXML: Enter, Send, error recovery, persistence, and disabled
controls after bye. Window resizing and the packaged JAR are smoke-tested separately.

### Test case: Search numbers identify the task to change

Aim: Search a subset, then mark and delete using the displayed full-list numbers.

Input:
```text
todo buy milk
todo read book
find BOOK
mark 2
find book
delete 2
find book
list
bye
```

Expected output:
```text
____________________________________________________________
GRONK!
I'm Gronk, your mighty task keeper.
Give Gronk a task. We crush it together!
____________________________________________________________
GRONK!
 Gronk grabbed a new task:
   [T][ ] buy milk
 Now you have 1 tasks in the list.
____________________________________________________________
GRONK!
 Gronk grabbed a new task:
   [T][ ] read book
 Now you have 2 tasks in the list.
____________________________________________________________
GRONK!
 Gronk sniffed out these tasks:
 2.[T][ ] read book
____________________________________________________________
GRONK!
 Task crushed! Gronk marks it done:
   [T][X] read book
____________________________________________________________
GRONK!
 Gronk sniffed out these tasks:
 2.[T][X] read book
____________________________________________________________
GRONK!
 Gronk tossed this task out:
   [T][X] read book
 Now you have 1 tasks in the list.
____________________________________________________________
GRONK!
 Gronk sniffed out these tasks:
 No matching tasks. Try another keyword or use list.
____________________________________________________________
GRONK!
 Gronk guards your task pile:
 1.[T][ ] buy milk
____________________________________________________________
GRONK!
 Gronk rests now. Come back strong!
____________________________________________________________
```
