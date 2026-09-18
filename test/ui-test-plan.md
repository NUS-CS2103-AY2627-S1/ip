# UI Test Plan

This file is the source of truth for scripted end-to-end tests of Rem's console UI. Run it with the project-specific `test-ui` skill.

## Launch information

- Required JDK: Java 25
- Compile: `javac -d out` followed by every `.java` file under `src/main/java` except the JavaFX-only classes
  `Launcher.java`, `Main.java`, `MainWindow.java`, and `DialogBox.java`
- Run: `java -cp out rem.Rem`
- Comparison: exact standard-output comparison, with CRLF and LF treated as equivalent
- Default timeout: 10 seconds per test case
- Test isolation: remove the `data` folder before each case unless the case specifies saved data
- Persistence check: after a case that changes the task list, inspect `data/rem.txt`
  and verify that it contains the final task list in the documented storage format

## TC-01: Create and list all task types

**Aim:** Verify that Rem creates each task subtype, parses date/time values, reports the task count,
and displays dates in a readable format.

**Inputs:**

```text
todo borrow book
deadline return book /by 2/12/2019 1800
event project meeting /from 2019-12-03 1400 /to 2019-12-03 1600
list
bye
```

**Expected output:**

```text
____________________________________________________________
 ____                      
|  _ \    ___    _ __ ___  
| |_) |  / _ \  | '_ ` _ \ 
|  _ <  |  __/  | | | | | |
|_| \_\  \___|  |_| |_| |_|
Rem: Hi! I'm Rem.
Rem: I can help! Then maybe a nap.
____________________________________________________________
Me: ____________________________________________________________
Rem: Got it! I put it on the list:
Rem: [T][ ] borrow book
Rem: Yay! Our first task!
____________________________________________________________
Me: ____________________________________________________________
Rem: Got it! I put it on the list:
Rem: [D][ ] return book (by: Dec 02 2019, 6:00 PM)
Rem: Now you have 2 tasks in the list.
____________________________________________________________
Me: ____________________________________________________________
Rem: Got it! I put it on the list:
Rem: [E][ ] project meeting (from: Dec 03 2019, 2:00 PM to: Dec 03 2019, 4:00 PM)
Rem: Now you have 3 tasks in the list.
____________________________________________________________
Me: ____________________________________________________________
Rem: Hmm... what to do now?
Rem: 1.[T][ ] borrow book
Rem: 2.[D][ ] return book (by: Dec 02 2019, 6:00 PM)
Rem: 3.[E][ ] project meeting (from: Dec 03 2019, 2:00 PM to: Dec 03 2019, 4:00 PM)
____________________________________________________________
Me: ____________________________________________________________
Rem: [Yawn] Need more sleep. Time for bed...
____________________________________________________________
```

## TC-03: Handle invalid user input without exiting

**Aim:** Verify that Rem reports unknown commands, empty task descriptions, invalid deadline and event formats, and invalid task numbers, then continues accepting commands.

**Inputs:**

```text

blah
todo
deadline return book
deadline return book /by
event meeting
event meeting /from 2pm
event /from 2pm /to 4pm
mark
mark abc
mark 0
todo read book
mark 2
unmark -1
bye
```

**Expected output:**

```text
____________________________________________________________
 ____                      
|  _ \    ___    _ __ ___  
| |_) |  / _ \  | '_ ` _ \ 
|  _ <  |  __/  | | | | | |
|_| \_\  \___|  |_| |_| |_|
Rem: Hi! I'm Rem.
Rem: I can help! Then maybe a nap.
____________________________________________________________
Me: ____________________________________________________________
Rem: Hmm... I don't know what to do with that...
Rem: Try todo read book to add a task, or list to view tasks.
Rem: Commands: todo, deadline, event, list, find, on, mark, unmark, delete, note, deletenote, bye.
____________________________________________________________
Me: ____________________________________________________________
Rem: Hmm... I don't know what to do with that...
Rem: Try todo read book to add a task, or list to view tasks.
Rem: Commands: todo, deadline, event, list, find, on, mark, unmark, delete, note, deletenote, bye.
____________________________________________________________
Me: ____________________________________________________________
Rem: You didn't say what you wanna do...
____________________________________________________________
Me: ____________________________________________________________
Rem: When is this due by again?
Rem: Use: deadline DESCRIPTION /by YYYY-MM-DD [HHmm]
Rem: Example: deadline return book /by 2026-10-01 1800
____________________________________________________________
Me: ____________________________________________________________
Rem: When is this due by again?
Rem: Use: deadline DESCRIPTION /by YYYY-MM-DD [HHmm]
Rem: Example: deadline return book /by 2026-10-01 1800
____________________________________________________________
Me: ____________________________________________________________
Rem: I need to know when it starts and when it ends...
Rem: Use: event DESCRIPTION /from YYYY-MM-DD [HHmm] /to YYYY-MM-DD [HHmm]
Rem: Example: event meeting /from 2026-10-01 1400 /to 2026-10-01 1500
Rem: The end must be after the start.
____________________________________________________________
Me: ____________________________________________________________
Rem: I need to know when it starts and when it ends...
Rem: Use: event DESCRIPTION /from YYYY-MM-DD [HHmm] /to YYYY-MM-DD [HHmm]
Rem: Example: event meeting /from 2026-10-01 1400 /to 2026-10-01 1500
Rem: The end must be after the start.
____________________________________________________________
Me: ____________________________________________________________
Rem: You didn't say what you wanna do...
____________________________________________________________
Me: ____________________________________________________________
Rem: Please give me a task number I can work with...
Rem: Use list to see task numbers, then use a positive number from that list.
Rem: Example: mark 1
____________________________________________________________
Me: ____________________________________________________________
Rem: Please give me a task number I can work with...
Rem: Use list to see task numbers, then use a positive number from that list.
Rem: Example: mark 1
____________________________________________________________
Me: ____________________________________________________________
Rem: Please give me a task number I can work with...
Rem: Use list to see task numbers, then use a positive number from that list.
Rem: Example: mark 1
____________________________________________________________
Me: ____________________________________________________________
Rem: Got it! I put it on the list:
Rem: [T][ ] read book
Rem: Yay! Our first task!
____________________________________________________________
Me: ____________________________________________________________
Rem: Please give me a task number I can work with...
Rem: Use list to see task numbers, then use a positive number from that list.
Rem: Example: mark 1
____________________________________________________________
Me: ____________________________________________________________
Rem: Please give me a task number I can work with...
Rem: Use list to see task numbers, then use a positive number from that list.
Rem: Example: mark 1
____________________________________________________________
Me: ____________________________________________________________
Rem: [Yawn] Need more sleep. Time for bed...
____________________________________________________________
```

## TC-02: Preserve the task type when marking and unmarking

**Aim:** Verify that operations inherited from `Task` update completion status without losing the to-do type marker.

**Inputs:**

```text
todo read book
mark 1
unmark 1
bye
```

**Expected output:**

```text
____________________________________________________________
 ____                      
|  _ \    ___    _ __ ___  
| |_) |  / _ \  | '_ ` _ \ 
|  _ <  |  __/  | | | | | |
|_| \_\  \___|  |_| |_| |_|
Rem: Hi! I'm Rem.
Rem: I can help! Then maybe a nap.
____________________________________________________________
Me: ____________________________________________________________
Rem: Got it! I put it on the list:
Rem: [T][ ] read book
Rem: Yay! Our first task!
____________________________________________________________
Me: ____________________________________________________________
Rem: We did it! This one's done:
Rem: [T][X] read book
____________________________________________________________
Me: ____________________________________________________________
Rem: Oh! We're still doing this one. Marked it not done:
Rem: [T][ ] read book
____________________________________________________________
Me: ____________________________________________________________
Rem: [Yawn] Need more sleep. Time for bed...
____________________________________________________________
```

## TC-05: Delete boundary tasks and handle an empty list

**Aim:** Verify that Rem rejects deletion from an empty list, deletes the first and last remaining tasks, renumbers tasks, and reports a zero task count correctly.

**Inputs:**

```text
delete 1
todo first task
todo second task
delete 1
list
delete 1
list
delete 1
bye
```

**Expected output:**

```text
____________________________________________________________
 ____                      
|  _ \    ___    _ __ ___  
| |_) |  / _ \  | '_ ` _ \ 
|  _ <  |  __/  | | | | | |
|_| \_\  \___|  |_| |_| |_|
Rem: Hi! I'm Rem.
Rem: I can help! Then maybe a nap.
____________________________________________________________
Me: ____________________________________________________________
Rem: Please give me a task number I can work with...
Rem: Use list to see task numbers, then use a positive number from that list.
Rem: Example: mark 1
____________________________________________________________
Me: ____________________________________________________________
Rem: Got it! I put it on the list:
Rem: [T][ ] first task
Rem: Yay! Our first task!
____________________________________________________________
Me: ____________________________________________________________
Rem: Got it! I put it on the list:
Rem: [T][ ] second task
Rem: Now you have 2 tasks in the list.
____________________________________________________________
Me: ____________________________________________________________
Rem: Don't need this one? Okay. Removed:
Rem: [T][ ] first task
Rem: Now we are only left with 1 task in the list.
____________________________________________________________
Me: ____________________________________________________________
Rem: Hmm... what to do now?
Rem: 1.[T][ ] second task
____________________________________________________________
Me: ____________________________________________________________
Rem: Don't need this one? Okay. Removed:
Rem: [T][ ] second task
Rem: Now we are only left with 0 tasks in the list.
____________________________________________________________
Me: ____________________________________________________________
Rem: Hmm... what to do now?
Rem: No tasks yet. Got something for us to do?
____________________________________________________________
Me: ____________________________________________________________
Rem: Please give me a task number I can work with...
Rem: Use list to see task numbers, then use a positive number from that list.
Rem: Example: mark 1
____________________________________________________________
Me: ____________________________________________________________
Rem: [Yawn] Need more sleep. Time for bed...
____________________________________________________________
```

## TC-06: Recognize mixed-case enum commands

**Aim:** Verify that command recognition is case-insensitive and that unsupported first words still produce the unknown-command response.

**Inputs:**

```text
ToDo mixed case task
MaRk 1
UnMaRk 1
LiSt
nonsense
BYE
```

**Expected output:**

```text
____________________________________________________________
 ____                      
|  _ \    ___    _ __ ___  
| |_) |  / _ \  | '_ ` _ \ 
|  _ <  |  __/  | | | | | |
|_| \_\  \___|  |_| |_| |_|
Rem: Hi! I'm Rem.
Rem: I can help! Then maybe a nap.
____________________________________________________________
Me: ____________________________________________________________
Rem: Got it! I put it on the list:
Rem: [T][ ] mixed case task
Rem: Yay! Our first task!
____________________________________________________________
Me: ____________________________________________________________
Rem: We did it! This one's done:
Rem: [T][X] mixed case task
____________________________________________________________
Me: ____________________________________________________________
Rem: Oh! We're still doing this one. Marked it not done:
Rem: [T][ ] mixed case task
____________________________________________________________
Me: ____________________________________________________________
Rem: Hmm... what to do now?
Rem: 1.[T][ ] mixed case task
____________________________________________________________
Me: ____________________________________________________________
Rem: Hmm... I don't know what to do with that...
Rem: Try todo read book to add a task, or list to view tasks.
Rem: Commands: todo, deadline, event, list, find, on, mark, unmark, delete, note, deletenote, bye.
____________________________________________________________
Me: ____________________________________________________________
Rem: [Yawn] Need more sleep. Time for bed...
____________________________________________________________
```

## TC-04: Delete a task from the list

**Aim:** Verify that deleting a task removes the selected task, reduces the task count, and renumbers the remaining tasks.

**Inputs:**

```text
todo read book
deadline return book /by 2019-06-06
event project meeting /from 2019-08-06 1400 /to 2019-08-06 1600
delete 2
list
bye
```

**Expected output:**

```text
____________________________________________________________
 ____                      
|  _ \    ___    _ __ ___  
| |_) |  / _ \  | '_ ` _ \ 
|  _ <  |  __/  | | | | | |
|_| \_\  \___|  |_| |_| |_|
Rem: Hi! I'm Rem.
Rem: I can help! Then maybe a nap.
____________________________________________________________
Me: ____________________________________________________________
Rem: Got it! I put it on the list:
Rem: [T][ ] read book
Rem: Yay! Our first task!
____________________________________________________________
Me: ____________________________________________________________
Rem: Got it! I put it on the list:
Rem: [D][ ] return book (by: Jun 06 2019)
Rem: Now you have 2 tasks in the list.
____________________________________________________________
Me: ____________________________________________________________
Rem: Got it! I put it on the list:
Rem: [E][ ] project meeting (from: Aug 06 2019, 2:00 PM to: Aug 06 2019, 4:00 PM)
Rem: Now you have 3 tasks in the list.
____________________________________________________________
Me: ____________________________________________________________
Rem: Don't need this one? Okay. Removed:
Rem: [D][ ] return book (by: Jun 06 2019)
Rem: Now we are only left with 2 tasks in the list.
____________________________________________________________
Me: ____________________________________________________________
Rem: Hmm... what to do now?
Rem: 1.[T][ ] read book
Rem: 2.[E][ ] project meeting (from: Aug 06 2019, 2:00 PM to: Aug 06 2019, 4:00 PM)
____________________________________________________________
Me: ____________________________________________________________
Rem: [Yawn] Need more sleep. Time for bed...
____________________________________________________________
```

## TC-07: Save the final task list to disk

**Aim:** Verify that adding, marking, and deleting tasks leaves the final task list saved in
`data/rem.txt`. After the process exits, the file must contain `T | 1 | saved task` followed by a
newline.

**Inputs:**

```text
todo saved task
deadline removed task /by 2019-06-06
mark 1
delete 2
bye
```

**Expected output:**

```text
____________________________________________________________
 ____                      
|  _ \    ___    _ __ ___  
| |_) |  / _ \  | '_ ` _ \ 
|  _ <  |  __/  | | | | | |
|_| \_\  \___|  |_| |_| |_|
Rem: Hi! I'm Rem.
Rem: I can help! Then maybe a nap.
____________________________________________________________
Me: ____________________________________________________________
Rem: Got it! I put it on the list:
Rem: [T][ ] saved task
Rem: Yay! Our first task!
____________________________________________________________
Me: ____________________________________________________________
Rem: Got it! I put it on the list:
Rem: [D][ ] removed task (by: Jun 06 2019)
Rem: Now you have 2 tasks in the list.
____________________________________________________________
Me: ____________________________________________________________
Rem: We did it! This one's done:
Rem: [T][X] saved task
____________________________________________________________
Me: ____________________________________________________________
Rem: Don't need this one? Okay. Removed:
Rem: [D][ ] removed task (by: Jun 06 2019)
Rem: Now we are only left with 1 task in the list.
____________________________________________________________
Me: ____________________________________________________________
Rem: [Yawn] Need more sleep. Time for bed...
____________________________________________________________
```

## TC-08: Load saved tasks when starting

**Aim:** Verify that Rem loads every task subtype and its completion status from `data/rem.txt`.
Before starting Rem, create the file with these contents:

```text
T | 1 | read book
D | 0 | return book | 2019-06-06 0000
E | 0 | project meeting | 2019-08-06 1400 | 2019-08-06 1600
```

**Inputs:**

```text
list
bye
```

**Expected output:**

```text
____________________________________________________________
 ____                      
|  _ \    ___    _ __ ___  
| |_) |  / _ \  | '_ ` _ \ 
|  _ <  |  __/  | | | | | |
|_| \_\  \___|  |_| |_| |_|
Rem: Hi! I'm Rem.
Rem: I can help! Then maybe a nap.
____________________________________________________________
Me: ____________________________________________________________
Rem: Hmm... what to do now?
Rem: 1.[T][X] read book
Rem: 2.[D][ ] return book (by: Jun 06 2019)
Rem: 3.[E][ ] project meeting (from: Aug 06 2019, 2:00 PM to: Aug 06 2019, 4:00 PM)
____________________________________________________________
Me: ____________________________________________________________
Rem: [Yawn] Need more sleep. Time for bed...
____________________________________________________________
```

## TC-09: Recover from malformed saved data

**Aim:** Verify that malformed saved data does not crash Rem. Before starting Rem, create
`data/rem.txt` containing `D | maybe | broken task`, which has an invalid status and missing due
value. Attempting to add a task must preserve the original file.

**Inputs:**

```text
todo replacement
list
bye
```

**Expected output:**

```text
____________________________________________________________
 ____                      
|  _ \    ___    _ __ ___  
| |_) |  / _ \  | '_ ` _ \ 
|  _ <  |  __/  | | | | | |
|_| \_\  \___|  |_| |_| |_|
Rem: Hi! I'm Rem.
Rem: I can help! Then maybe a nap.
Rem: Oh. I couldn't load your saved tasks. I'm showing an empty list.
____________________________________________________________
Rem: Invalid task data on line 1.
Me: ____________________________________________________________
Rem: Your changes were not applied or saved. Saved tasks could not be loaded. Repair the data file or its permissions, then restart RemBot. The original file has been preserved.
____________________________________________________________
Me: ____________________________________________________________
Rem: Hmm... what to do now?
Rem: No tasks yet. Got something for us to do?
____________________________________________________________
Me: ____________________________________________________________
Rem: [Yawn] Need more sleep. Time for bed...
____________________________________________________________
```

## TC-10: Continue after a save failure

**Aim:** Verify that an unwritable data-file path does not crash Rem. Before starting Rem, create
`data/rem.txt` as a directory so it cannot be read from or overwritten as a regular file.

**Inputs:**

```text
todo unsaved task
bye
```

**Expected output:**

```text
____________________________________________________________
 ____                      
|  _ \    ___    _ __ ___  
| |_) |  / _ \  | '_ ` _ \ 
|  _ <  |  __/  | | | | | |
|_| \_\  \___|  |_| |_| |_|
Rem: Hi! I'm Rem.
Rem: I can help! Then maybe a nap.
Rem: Oh. I couldn't load your saved tasks. I'm showing an empty list.
____________________________________________________________
Rem: The saved task path is a directory. Choose a regular file and restart RemBot.
Me: ____________________________________________________________
Rem: Your changes were not applied or saved. Saved tasks could not be loaded. Repair the data file or its permissions, then restart RemBot. The original file has been preserved.
____________________________________________________________
Me: ____________________________________________________________
Rem: [Yawn] Need more sleep. Time for bed...
____________________________________________________________
```

## TC-11: Find scheduled tasks by date

**Aim:** Verify that the `on` command lists deadlines due and events overlapping the requested
date, excludes todos, reports dates with no scheduled tasks, and rejects invalid dates.

**Inputs:**

```text
todo buy snacks
deadline submit report /by 2019-12-04 1800
event conference /from 2019-12-03 0900 /to 2019-12-05 1700
on 2019-12-04
on 2019-12-06
on tomorrow
bye
```

**Expected output:**

```text
____________________________________________________________
 ____                      
|  _ \    ___    _ __ ___  
| |_) |  / _ \  | '_ ` _ \ 
|  _ <  |  __/  | | | | | |
|_| \_\  \___|  |_| |_| |_|
Rem: Hi! I'm Rem.
Rem: I can help! Then maybe a nap.
____________________________________________________________
Me: ____________________________________________________________
Rem: Got it! I put it on the list:
Rem: [T][ ] buy snacks
Rem: Yay! Our first task!
____________________________________________________________
Me: ____________________________________________________________
Rem: Got it! I put it on the list:
Rem: [D][ ] submit report (by: Dec 04 2019, 6:00 PM)
Rem: Now you have 2 tasks in the list.
____________________________________________________________
Me: ____________________________________________________________
Rem: Got it! I put it on the list:
Rem: [E][ ] conference (from: Dec 03 2019, 9:00 AM to: Dec 05 2019, 5:00 PM)
Rem: Now you have 3 tasks in the list.
____________________________________________________________
Me: ____________________________________________________________
Rem: Here's what's scheduled on Dec 04 2019:
Rem: 1.[D][ ] submit report (by: Dec 04 2019, 6:00 PM)
Rem: 2.[E][ ] conference (from: Dec 03 2019, 9:00 AM to: Dec 05 2019, 5:00 PM)
____________________________________________________________
Me: ____________________________________________________________
Rem: Nothing scheduled on Dec 06 2019. Maybe nap time?
____________________________________________________________
Me: ____________________________________________________________
Rem: Please use a date like 2019-10-15.
____________________________________________________________
Me: ____________________________________________________________
Rem: [Yawn] Need more sleep. Time for bed...
____________________________________________________________
```

## TC-12: Find tasks by description keyword

**Aim:** Verify that `find` searches task descriptions case-insensitively, preserves matching task
order, excludes non-matches, and rejects a missing keyword.

**Inputs:**

```text
todo Read Book
deadline return book /by 2019-06-06
event library visit /from 2019-06-07 1400 /to 2019-06-07 1600
find BOOK
find
bye
```

**Expected output:**

```text
____________________________________________________________
 ____                      
|  _ \    ___    _ __ ___  
| |_) |  / _ \  | '_ ` _ \ 
|  _ <  |  __/  | | | | | |
|_| \_\  \___|  |_| |_| |_|
Rem: Hi! I'm Rem.
Rem: I can help! Then maybe a nap.
____________________________________________________________
Me: ____________________________________________________________
Rem: Got it! I put it on the list:
Rem: [T][ ] Read Book
Rem: Yay! Our first task!
____________________________________________________________
Me: ____________________________________________________________
Rem: Got it! I put it on the list:
Rem: [D][ ] return book (by: Jun 06 2019)
Rem: Now you have 2 tasks in the list.
____________________________________________________________
Me: ____________________________________________________________
Rem: Got it! I put it on the list:
Rem: [E][ ] library visit (from: Jun 07 2019, 2:00 PM to: Jun 07 2019, 4:00 PM)
Rem: Now you have 3 tasks in the list.
____________________________________________________________
Me: ____________________________________________________________
Rem: Found these!
Rem: 1.[T][ ] Read Book
Rem: 2.[D][ ] return book (by: Jun 06 2019)
____________________________________________________________
Me: ____________________________________________________________
Rem: You didn't say what you wanna do...
____________________________________________________________
Me: ____________________________________________________________
Rem: [Yawn] Need more sleep. Time for bed...
____________________________________________________________
```

## TC-13: Create, replace, find, and delete notes

**Aim:** Verify that notes can be added during task creation, replaced after creation, found by
their text, retained when marking a task, removed, and saved in the backward-compatible format.
After the process exits, `data/rem.txt` must contain these lines followed by a newline:

```text
T | 1 | read book | N:Qm9ycm93IGl0IGZyb20gQWxpY2U=
D | 0 | submit report | 2026-10-01 1800
```

**Inputs:**

```text
todo read book /note Borrow it from Alice
deadline submit report /by 2026-10-01 1800 /note Include appendix
note 2 Include the references
find references
mark 1
deletenote 2
deletenote 2
list
bye
```

**Expected output:**

```text
____________________________________________________________
 ____                      
|  _ \    ___    _ __ ___  
| |_) |  / _ \  | '_ ` _ \ 
|  _ <  |  __/  | | | | | |
|_| \_\  \___|  |_| |_| |_|
Rem: Hi! I'm Rem.
Rem: I can help! Then maybe a nap.
____________________________________________________________
Me: ____________________________________________________________
Rem: Got it! I put it on the list:
Rem: [T][ ] read book
Rem:   Note: Borrow it from Alice
Rem: Yay! Our first task!
____________________________________________________________
Me: ____________________________________________________________
Rem: Got it! I put it on the list:
Rem: [D][ ] submit report (by: Oct 01 2026, 6:00 PM)
Rem:   Note: Include appendix
Rem: Now you have 2 tasks in the list.
____________________________________________________________
Me: ____________________________________________________________
Rem: I'll keep this note with your task:
Rem: [D][ ] submit report (by: Oct 01 2026, 6:00 PM)
Rem:   Note: Include the references
____________________________________________________________
Me: ____________________________________________________________
Rem: Found these!
Rem: 1.[D][ ] submit report (by: Oct 01 2026, 6:00 PM)
Rem:   Note: Include the references
____________________________________________________________
Me: ____________________________________________________________
Rem: We did it! This one's done:
Rem: [T][X] read book
Rem:   Note: Borrow it from Alice
____________________________________________________________
Me: ____________________________________________________________
Rem: Okay. Took the note off this task:
Rem: [D][ ] submit report (by: Oct 01 2026, 6:00 PM)
____________________________________________________________
Me: ____________________________________________________________
Rem: This task doesn't have a note to remove.
____________________________________________________________
Me: ____________________________________________________________
Rem: Hmm... what to do now?
Rem: 1.[T][X] read book
Rem:   Note: Borrow it from Alice
Rem: 2.[D][ ] submit report (by: Oct 01 2026, 6:00 PM)
____________________________________________________________
Me: ____________________________________________________________
Rem: [Yawn] Need more sleep. Time for bed...
____________________________________________________________
```

## TC-14: Reject missing note text

**Aim:** Verify that both creation-time and post-creation note commands reject missing note text
without changing the task.

**Inputs:**

```text
todo read book
note 1
todo another task /note
list
bye
```

**Expected output:**

```text
____________________________________________________________
 ____                      
|  _ \    ___    _ __ ___  
| |_) |  / _ \  | '_ ` _ \ 
|  _ <  |  __/  | | | | | |
|_| \_\  \___|  |_| |_| |_|
Rem: Hi! I'm Rem.
Rem: I can help! Then maybe a nap.
____________________________________________________________
Me: ____________________________________________________________
Rem: Got it! I put it on the list:
Rem: [T][ ] read book
Rem: Yay! Our first task!
____________________________________________________________
Me: ____________________________________________________________
Rem: What should I keep in this note?
____________________________________________________________
Me: ____________________________________________________________
Rem: What should I keep in this note?
____________________________________________________________
Me: ____________________________________________________________
Rem: Hmm... what to do now?
Rem: 1.[T][ ] read book
____________________________________________________________
Me: ____________________________________________________________
Rem: [Yawn] Need more sleep. Time for bed...
____________________________________________________________
```

## TC-15: Explain empty lists and unsuccessful searches

**Aim:** Verify empty-state dialogue before adding and after deleting a task, and distinguish empty searches from matches.

**Inputs:**

```text
list
find pillow
todo wash pillow
find pillow
delete 1
list
bye
```

**Expected output:**

```text
____________________________________________________________
 ____                      
|  _ \    ___    _ __ ___  
| |_) |  / _ \  | '_ ` _ \ 
|  _ <  |  __/  | | | | | |
|_| \_\  \___|  |_| |_| |_|
Rem: Hi! I'm Rem.
Rem: I can help! Then maybe a nap.
____________________________________________________________
Me: ____________________________________________________________
Rem: Hmm... what to do now?
Rem: No tasks yet. Got something for us to do?
____________________________________________________________
Me: ____________________________________________________________
Rem: Didn't find any tasks matching 'pillow'. Try another word?
____________________________________________________________
Me: ____________________________________________________________
Rem: Got it! I put it on the list:
Rem: [T][ ] wash pillow
Rem: Yay! Our first task!
____________________________________________________________
Me: ____________________________________________________________
Rem: Found these!
Rem: 1.[T][ ] wash pillow
____________________________________________________________
Me: ____________________________________________________________
Rem: Don't need this one? Okay. Removed:
Rem: [T][ ] wash pillow
Rem: Now we are only left with 0 tasks in the list.
____________________________________________________________
Me: ____________________________________________________________
Rem: Hmm... what to do now?
Rem: No tasks yet. Got something for us to do?
____________________________________________________________
Me: ____________________________________________________________
Rem: [Yawn] Need more sleep. Time for bed...
____________________________________________________________
```


## TC-16: Validate input and preserve literal descriptions

**Aim:** Accept whitespace, reject duplicates and extra arguments, reject impossible dates, and save literal pipes safely.

**Inputs:**

```text
  todo	read  book  
todo read  book
list extra
bye later
deadline report /by 2026-02-30
todo A | B
list
bye
```

**Expected output:**

```text
____________________________________________________________
 ____                      
|  _ \    ___    _ __ ___  
| |_) |  / _ \  | '_ ` _ \ 
|  _ <  |  __/  | | | | | |
|_| \_\  \___|  |_| |_| |_|
Rem: Hi! I'm Rem.
Rem: I can help! Then maybe a nap.
____________________________________________________________
Me: ____________________________________________________________
Rem: Got it! I put it on the list:
Rem: [T][ ] read  book
Rem: Yay! Our first task!
____________________________________________________________
Me: ____________________________________________________________
Rem: This task already exists as task 1. Use list to see it.
____________________________________________________________
Me: ____________________________________________________________
Rem: list does not take arguments.
____________________________________________________________
Me: ____________________________________________________________
Rem: bye does not take arguments.
____________________________________________________________
Me: ____________________________________________________________
Rem: When is this due by again?
Rem: Use: deadline DESCRIPTION /by YYYY-MM-DD [HHmm]
Rem: Example: deadline return book /by 2026-10-01 1800
____________________________________________________________
Me: ____________________________________________________________
Rem: Got it! I put it on the list:
Rem: [T][ ] A | B
Rem: Now you have 2 tasks in the list.
____________________________________________________________
Me: ____________________________________________________________
Rem: Hmm... what to do now?
Rem: 1.[T][ ] read  book
Rem: 2.[T][ ] A | B
____________________________________________________________
Me: ____________________________________________________________
Rem: [Yawn] Need more sleep. Time for bed...
____________________________________________________________
```

## Graphical interface regression checks

Run `gradlew test` on a desktop with Java 25. `MainWindowTest` loads the actual FXML and CSS
on the JavaFX thread and submits real commands through Send and Enter. It checks:

- Task and note replies, wrapping, and successful input clearing.
- Blank submissions add no messages and disable Send.
- Invalid commands retain the input and display an error heading, accessible error text,
  and a contrasting error background. A subsequent valid command returns to normal styling.
- The single 40 px header portrait loads correctly. After 45 seconds idle it dims with a zZ indicator.
- Typing, key presses, clicks, or scrolling wake the portrait immediately; exit stops the idle timer.
- A vector helper appears only while the task list is empty, including after deleting the last task.
- Short replies use compact bubbles; multiline lists and help retain wide cards.
- Replies and the composer fit narrow and wide windows.
- Submissions scroll to the latest reply; resizing preserves the reader's scroll position.
- Up/Down recall submitted commands without submitting and restore the unfinished draft.
- Vertical wheel scrolling moves 1.5 times the event distance and stops at both bounds.
- Exit disables input and Send.
- Malformed saved task data opens a modal Start Fresh/Exit prompt. Start Fresh preserves the
  original contents in a timestamped `rem-corrupt-*.txt` backup before enabling an empty task list;
  dismissing the prompt or choosing Exit closes RemBot without changing the data file.
- File path and permission failures do not offer Start Fresh.

The console cases above include the new command syntax and recovery hints shared with the GUI.
Launch the GUI with `gradlew run` or `java -jar build/libs/rembot.jar` after `gradlew shadowJar`.
The artwork is anchored at the bottom right behind a lightly dimmed viewport; message cards stay opaque.
Rem's circular portrait appears only in the header; commands have no avatar.
Verify long task lists and notes remain readable at 380 x 400 and 800 x 600.
Errors have a red-tinted card, contrasting border, and a visible warning symbol and Error heading.
`bye` displays the farewell and closes the application after 1.5 seconds.
