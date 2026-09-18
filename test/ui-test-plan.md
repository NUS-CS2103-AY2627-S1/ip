# HABI UI Test Plan

## Test case: manage typed tasks

**Aim:** Verify creation, listing, marking, and unmarking of todo, deadline, and event tasks.

**Input**
```text
todo read book
deadline return book /by 2026-09-15
event project meeting /from Mon 2pm /to 4pm
mark 2
list
unmark 2
list
bye
```

**Expected output**
```text
____________________________________________________________
 _   _    _     ____   ___
| | | |  / \   | __ )   |  |
| |_| | / _ \  |  _ \  |  |
|  _  |/ ___ \ | |_) | |  |
|_| |_|_/   \_\|____/  _|_
Hello! I'm HABI, your steady habit-building companion.
What small step can we plan today?
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] read book
Now you have 1 task in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [D][ ] return book (by: Sep 15 2026)
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [E][ ] project meeting (from: Mon 2pm to: 4pm)
Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
Nice! I've marked this task as done:
  [D][X] return book (by: Sep 15 2026)
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] read book
2.[D][X] return book (by: Sep 15 2026)
3.[E][ ] project meeting (from: Mon 2pm to: 4pm)
____________________________________________________________
____________________________________________________________
OK, I've marked this task as not done yet:
  [D][ ] return book (by: Sep 15 2026)
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] read book
2.[D][ ] return book (by: Sep 15 2026)
3.[E][ ] project meeting (from: Mon 2pm to: 4pm)
____________________________________________________________
____________________________________________________________
Bye for now. Small steps build better days—see you soon!
____________________________________________________________
```

## Test case: manage notes

**Aim:** Verify notes can be added, listed, and deleted independently of tasks.

**Input**
```text
note buy milk
note call Mum
notes
delete-note 1
notes
bye
```

**Expected output**
```text
____________________________________________________________
 _   _    _     ____   ___
| | | |  / \   | __ )   |  |
| |_| | / _ \  |  _ \  |  |
|  _  |/ ___ \ | |_) | |  |
|_| |_|_/   \_\|____/  _|_
Hello! I'm HABI, your steady habit-building companion.
What small step can we plan today?
____________________________________________________________
____________________________________________________________
Got it. I've added this note:
  [N] buy milk
Now you have 1 note in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this note:
  [N] call Mum
Now you have 2 notes in the list.
____________________________________________________________
____________________________________________________________
Here are the notes in your list:
1.[N] buy milk
2.[N] call Mum
____________________________________________________________
____________________________________________________________
Noted. I've removed this note:
  [N] buy milk
Now you have 1 note in the list.
____________________________________________________________
____________________________________________________________
Here are the notes in your list:
1.[N] call Mum
____________________________________________________________
____________________________________________________________
Bye for now. Small steps build better days—see you soon!
____________________________________________________________
```

## Test case: find matching tasks

**Aim:** Verify `find` lists only tasks whose descriptions contain the keyword.

**Input**
```text
todo read book
deadline return book /by 2026-09-15
event project meeting /from Mon /to Tue
find book
bye
```

**Expected output**
```text
____________________________________________________________
 _   _    _     ____   ___
| | | |  / \   | __ )   |  |
| |_| | / _ \  |  _ \  |  |
|  _  |/ ___ \ | |_) | |  |
|_| |_|_/   \_\|____/  _|_
Hello! I'm HABI, your steady habit-building companion.
What small step can we plan today?
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] read book
Now you have 1 task in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [D][ ] return book (by: Sep 15 2026)
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [E][ ] project meeting (from: Mon to: Tue)
Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
Here are the matching tasks in your list:
1.[T][ ] read book
2.[D][ ] return book (by: Sep 15 2026)
____________________________________________________________
____________________________________________________________
Bye for now. Small steps build better days—see you soon!
____________________________________________________________
```

## Test case: save a changed task list

**Aim:** Verify commands that change tasks retain their normal console responses while persistence runs.

**Input**
```text
todo read book
mark 1
bye
```

**Expected output**
```text
____________________________________________________________
 _   _    _     ____   ___
| | | |  / \   | __ )   |  |
| |_| | / _ \  |  _ \  |  |
|  _  |/ ___ \ | |_) | |  |
|_| |_|_/   \_\|____/  _|_
Hello! I'm HABI, your steady habit-building companion.
What small step can we plan today?
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] read book
Now you have 1 task in the list.
____________________________________________________________
____________________________________________________________
Nice! I've marked this task as done:
  [T][X] read book
____________________________________________________________
____________________________________________________________
Bye for now. Small steps build better days—see you soon!
____________________________________________________________
```

## Test case: recover from invalid commands

**Aim:** Verify invalid commands report useful errors, preserve the task list, and allow the session to continue.

**Input**
```text

todo
deadline return book
deadline return book /by 15-09-2026
event project meeting /from Mon 2pm
mark two
mark 1
todo read book
mark 2
unmark 0
blah
list
bye
```

**Expected output**
```text
____________________________________________________________
 _   _    _     ____   ___
| | | |  / \   | __ )   |  |
| |_| | / _ \  |  _ \  |  |
|  _  |/ ___ \ | |_) | |  |
|_| |_|_/   \_\|____/  _|_
Hello! I'm HABI, your steady habit-building companion.
What small step can we plan today?
____________________________________________________________
____________________________________________________________
OOPS! Please enter a command.
____________________________________________________________
____________________________________________________________
OOPS! The todo description cannot be empty.
____________________________________________________________
____________________________________________________________
OOPS! Use: deadline DESCRIPTION /by yyyy-MM-dd
____________________________________________________________
____________________________________________________________
OOPS! Use: deadline DESCRIPTION /by yyyy-MM-dd
____________________________________________________________
____________________________________________________________
OOPS! Use: event DESCRIPTION /from START /to END
____________________________________________________________
____________________________________________________________
OOPS! The task number must be a whole number.
____________________________________________________________
____________________________________________________________
OOPS! Task number 1 is out of range.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] read book
Now you have 1 task in the list.
____________________________________________________________
____________________________________________________________
OOPS! Task number 2 is out of range.
____________________________________________________________
____________________________________________________________
OOPS! Task number 0 is out of range.
____________________________________________________________
____________________________________________________________
OOPS! I don't know what "blah" means.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] read book
____________________________________________________________
____________________________________________________________
Bye for now. Small steps build better days—see you soon!
____________________________________________________________
```

## Test case: reject arguments for parameterless commands

**Aim:** Verify `list`, `notes`, and `bye` reject extra arguments while a bare `bye` still ends the session.

**Input**
```text
list now
notes now
bye later
bye
```

**Expected output**
```text
____________________________________________________________
 _   _    _     ____   ___
| | | |  / \   | __ )   |  |
| |_| | / _ \  |  _ \  |  |
|  _  |/ ___ \ | |_) | |  |
|_| |_|_/   \_\|____/  _|_
Hello! I'm HABI, your steady habit-building companion.
What small step can we plan today?
____________________________________________________________
____________________________________________________________
OOPS! list does not take any arguments.
____________________________________________________________
____________________________________________________________
OOPS! notes does not take any arguments.
____________________________________________________________
____________________________________________________________
OOPS! bye does not take any arguments.
____________________________________________________________
____________________________________________________________
Bye for now. Small steps build better days—see you soon!
____________________________________________________________
```

## Test case: delete a task safely

**Aim:** Verify deletion removes the selected task, renumbers the list, and rejects an out-of-range task number.

**Input**
```text
todo read book
deadline return book /by 2026-09-15
event project meeting /from Mon /to Tue
delete 2
delete 5
list
bye
```

**Expected output**
```text
____________________________________________________________
 _   _    _     ____   ___
| | | |  / \   | __ )   |  |
| |_| | / _ \  |  _ \  |  |
|  _  |/ ___ \ | |_) | |  |
|_| |_|_/   \_\|____/  _|_
Hello! I'm HABI, your steady habit-building companion.
What small step can we plan today?
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] read book
Now you have 1 task in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [D][ ] return book (by: Sep 15 2026)
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [E][ ] project meeting (from: Mon to: Tue)
Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
Noted. I've removed this task:
  [D][ ] return book (by: Sep 15 2026)
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
OOPS! Task number 5 is out of range.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] read book
2.[E][ ] project meeting (from: Mon to: Tue)
____________________________________________________________
____________________________________________________________
Bye for now. Small steps build better days—see you soon!
____________________________________________________________
```

### Manual test case: protect corrupt startup data

**Aim:** Verify HABI reports an unreadable data file at startup and does not overwrite it when a user attempts a mutation.

**Setup**
```text
Create `data/habi.txt` containing exactly: `T\t2\tread book\n`.
```

**Input**
```text
todo write report
bye
```

**Expected output**
```text
____________________________________________________________
 _   _    _     ____   ___
| | | |  / \   | __ )   |  |
| |_| | / _ \  |  _ \  |  |
|  _  |/ ___ \ | |_) | |  |
|_| |_|_/   \_\|____/  _|_
Hello! I'm HABI, your steady habit-building companion.
What small step can we plan today?
____________________________________________________________
____________________________________________________________
OOPS! I could not load tasks from the data file.
____________________________________________________________
____________________________________________________________
OOPS! I could not load tasks from the data file. Fix the file before making changes.
____________________________________________________________
____________________________________________________________
Bye for now. Small steps build better days—see you soon!
____________________________________________________________
```

After the session, verify `data/habi.txt` is byte-for-byte unchanged.
