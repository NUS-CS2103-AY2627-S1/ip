# Alfred UI test plan

## Start with no saved task file

**Aim:** Verify that Alfred starts with an empty task list when its relative data directory and
file do not exist yet.

### Input
```text
list
bye
```

### Expected output
```text
____________________________________________________________
      *==/          |     |            \==*
     /XX/           |\__\/|             \XX\
   /XXXX\           |XXXXX|             /XXXX\
 |XXXXXX\_         *XXXXXXX*         \_/XXXXXX|
XXXXXXXXXXXxxxxxxxXXXXXXXXXXXxxxxxxxXXXXXXXXXXX
|XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX|
XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
|XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX|
 XXXXXX/^^^^"\XXXXXXXXXXXXXXXXXXXXX/^^^^^\XXXXXX
 |XXX|       \XXX/^^\XXXXX/^^\XXX/       |XXX|
   \XX\       \X/    \XXX/    \X/       /XX/
      "\       "      \X/      "       /

    _    _  __             _
   / \  | |/ _|_ __ ___  __| |
  / _ \ | | |_| '__/ _ \/ _` |
 / ___ \| |  _| | |  __/ (_| |
/_/   \_\_|_| |_|  \___|\__,_|

Good day, sir. Alfred is at your service in the cave. What shall we accomplish?
____________________________________________________________
____________________________________________________________
Here is your cave ledger:
____________________________________________________________
____________________________________________________________
The cave is secure. Until our next mission, sir.
____________________________________________________________
```

## Load saved task state after a restart

**Aim:** Verify that add, mark, unmark, and delete changes are saved, then that every task type,
date/time field, and completion status is restored by a fresh Alfred session.

### Setup input
```text
todo keep this task
todo remove this task
deadline return book /by 2019-12-02
event project meeting /from 2/12/2019 1800 /to 2019-12-03
mark 3
unmark 3
mark 4
delete 2
bye
```

### Input
```text
list
bye
```

### Expected output
```text
____________________________________________________________
      *==/          |     |            \==*
     /XX/           |\__\/|             \XX\
   /XXXX\           |XXXXX|             /XXXX\
 |XXXXXX\_         *XXXXXXX*         \_/XXXXXX|
XXXXXXXXXXXxxxxxxxXXXXXXXXXXXxxxxxxxXXXXXXXXXXX
|XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX|
XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
|XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX|
 XXXXXX/^^^^"\XXXXXXXXXXXXXXXXXXXXX/^^^^^\XXXXXX
 |XXX|       \XXX/^^\XXXXX/^^\XXX/       |XXX|
   \XX\       \X/    \XXX/    \X/       /XX/
      "\       "      \X/      "       /

    _    _  __             _
   / \  | |/ _|_ __ ___  __| |
  / _ \ | | |_| '__/ _ \/ _` |
 / ___ \| |  _| | |  __/ (_| |
/_/   \_\_|_| |_|  \___|\__,_|

Good day, sir. Alfred is at your service in the cave. What shall we accomplish?
____________________________________________________________
____________________________________________________________
Here is your cave ledger:
1.[T][ ] keep this task
2.[D][ ] return book (by: Dec 02 2019)
3.[E][X] project meeting (from: Dec 02 2019 18:00 to: Dec 03 2019)
____________________________________________________________
____________________________________________________________
The cave is secure. Until our next mission, sir.
____________________________________________________________
```

## Add, update, and list all task types

**Aim:** Verify that ToDos, Deadlines, and Events are stored, formatted, marked, unmarked, and
listed correctly.

### Input
```text
todo borrow book
deadline return book /by 2019-10-15
event project meeting /from 2/12/2019 1800 /to 2019-12-03
mark 2
unmark 2
list
bye
```

### Expected output
```text
____________________________________________________________
      *==/          |     |            \==*
     /XX/           |\__\/|             \XX\
   /XXXX\           |XXXXX|             /XXXX\
 |XXXXXX\_         *XXXXXXX*         \_/XXXXXX|
XXXXXXXXXXXxxxxxxxXXXXXXXXXXXxxxxxxxXXXXXXXXXXX
|XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX|
XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
|XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX|
 XXXXXX/^^^^"\XXXXXXXXXXXXXXXXXXXXX/^^^^^\XXXXXX
 |XXX|       \XXX/^^\XXXXX/^^\XXX/       |XXX|
   \XX\       \X/    \XXX/    \X/       /XX/
      "\       "      \X/      "       /

    _    _  __             _
   / \  | |/ _|_ __ ___  __| |
  / _ \ | | |_| '__/ _ \/ _` |
 / ___ \| |  _| | |  __/ (_| |
/_/   \_\_|_| |_|  \___|\__,_|

Good day, sir. Alfred is at your service in the cave. What shall we accomplish?
____________________________________________________________
____________________________________________________________
Consider it noted in the cave ledger:
  [T][ ] borrow book
Your cave ledger now holds 1 task.
____________________________________________________________
____________________________________________________________
Consider it noted in the cave ledger:
  [D][ ] return book (by: Oct 15 2019)
Your cave ledger now holds 2 tasks.
____________________________________________________________
____________________________________________________________
Consider it noted in the cave ledger:
  [E][ ] project meeting (from: Dec 02 2019 18:00 to: Dec 03 2019)
Your cave ledger now holds 3 tasks.
____________________________________________________________
____________________________________________________________
Mission accomplished. I've marked this task as done:
  [D][X] return book (by: Oct 15 2019)
____________________________________________________________
____________________________________________________________
I've reopened this mission:
  [D][ ] return book (by: Oct 15 2019)
____________________________________________________________
____________________________________________________________
Here is your cave ledger:
1.[T][ ] borrow book
2.[D][ ] return book (by: Oct 15 2019)
3.[E][ ] project meeting (from: Dec 02 2019 18:00 to: Dec 03 2019)
____________________________________________________________
____________________________________________________________
The cave is secure. Until our next mission, sir.
____________________________________________________________
```

## Delete a task and renumber the remaining list

**Aim:** Verify that deletion removes the requested task, preserves the other task types and
statuses, and rejects an invalid task number.

### Input
```text
todo read book
deadline return book /by 2019-10-15
event project meeting /from 2/12/2019 1800 /to 2019-12-03
mark 1
delete 5
delete 2
list
bye
```

### Expected output
```text
____________________________________________________________
      *==/          |     |            \==*
     /XX/           |\__\/|             \XX\
   /XXXX\           |XXXXX|             /XXXX\
 |XXXXXX\_         *XXXXXXX*         \_/XXXXXX|
XXXXXXXXXXXxxxxxxxXXXXXXXXXXXxxxxxxxXXXXXXXXXXX
|XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX|
XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
|XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX|
 XXXXXX/^^^^"\XXXXXXXXXXXXXXXXXXXXX/^^^^^\XXXXXX
 |XXX|       \XXX/^^\XXXXX/^^\XXX/       |XXX|
   \XX\       \X/    \XXX/    \X/       /XX/
      "\       "      \X/      "       /

    _    _  __             _
   / \  | |/ _|_ __ ___  __| |
  / _ \ | | |_| '__/ _ \/ _` |
 / ___ \| |  _| | |  __/ (_| |
/_/   \_\_|_| |_|  \___|\__,_|

Good day, sir. Alfred is at your service in the cave. What shall we accomplish?
____________________________________________________________
____________________________________________________________
Consider it noted in the cave ledger:
  [T][ ] read book
Your cave ledger now holds 1 task.
____________________________________________________________
____________________________________________________________
Consider it noted in the cave ledger:
  [D][ ] return book (by: Oct 15 2019)
Your cave ledger now holds 2 tasks.
____________________________________________________________
____________________________________________________________
Consider it noted in the cave ledger:
  [E][ ] project meeting (from: Dec 02 2019 18:00 to: Dec 03 2019)
Your cave ledger now holds 3 tasks.
____________________________________________________________
____________________________________________________________
Mission accomplished. I've marked this task as done:
  [T][X] read book
____________________________________________________________
____________________________________________________________
Apologies, sir: Alfred cannot find task 5. Choose a number from 1 to 3.
____________________________________________________________
____________________________________________________________
The following mission has left the cave ledger:
  [D][ ] return book (by: Oct 15 2019)
Your cave ledger now holds 2 tasks.
____________________________________________________________
____________________________________________________________
Here is your cave ledger:
1.[T][X] read book
2.[E][ ] project meeting (from: Dec 02 2019 18:00 to: Dec 03 2019)
____________________________________________________________
____________________________________________________________
The cave is secure. Until our next mission, sir.
____________________________________________________________
```

## Reject malformed events without changing tasks

**Aim:** Verify that malformed events show specific errors and leave a valid event unchanged.

### Input
```text
event team meeting /from 2/12/2019 1800 /to 2019-12-03
event team meeting
event team meeting /from Mon 2pm
event  /from 2/12/2019 1800 /to 2019-12-03
event team meeting /from  /to 2019-12-03
event team meeting /from Mon 2pm /to
list
bye
```

### Expected output
```text
____________________________________________________________
      *==/          |     |            \==*
     /XX/           |\__\/|             \XX\
   /XXXX\           |XXXXX|             /XXXX\
 |XXXXXX\_         *XXXXXXX*         \_/XXXXXX|
XXXXXXXXXXXxxxxxxxXXXXXXXXXXXxxxxxxxXXXXXXXXXXX
|XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX|
XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
|XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX|
 XXXXXX/^^^^"\XXXXXXXXXXXXXXXXXXXXX/^^^^^\XXXXXX
 |XXX|       \XXX/^^\XXXXX/^^\XXX/       |XXX|
   \XX\       \X/    \XXX/    \X/       /XX/
      "\       "      \X/      "       /

    _    _  __             _
   / \  | |/ _|_ __ ___  __| |
  / _ \ | | |_| '__/ _ \/ _` |
 / ___ \| |  _| | |  __/ (_| |
/_/   \_\_|_| |_|  \___|\__,_|

Good day, sir. Alfred is at your service in the cave. What shall we accomplish?
____________________________________________________________
____________________________________________________________
Consider it noted in the cave ledger:
  [E][ ] team meeting (from: Dec 02 2019 18:00 to: Dec 03 2019)
Your cave ledger now holds 1 task.
____________________________________________________________
____________________________________________________________
Apologies, sir: Alfred needs `/from` followed by a start time for an event.
____________________________________________________________
____________________________________________________________
Apologies, sir: Alfred needs `/to` followed by an end time for an event.
____________________________________________________________
____________________________________________________________
Apologies, sir: Alfred needs an event description before `/from`.
____________________________________________________________
____________________________________________________________
Apologies, sir: Alfred needs a start time after `/from`.
____________________________________________________________
____________________________________________________________
Apologies, sir: Alfred needs an end time after `/to`.
____________________________________________________________
____________________________________________________________
Here is your cave ledger:
1.[E][ ] team meeting (from: Dec 02 2019 18:00 to: Dec 03 2019)
____________________________________________________________
____________________________________________________________
The cave is secure. Until our next mission, sir.
____________________________________________________________
```

## Reject invalid task numbers without changing task status

**Aim:** Verify that invalid mark and unmark commands show specific errors and do not change the task status.

### Input
```text
mark 1
todo read book
mark
mark two
mark 0
mark 2
mark 1
unmark
unmark two
unmark 4
unmark -1
unmark 1
list
bye
```

### Expected output
```text
____________________________________________________________
      *==/          |     |            \==*
     /XX/           |\__\/|             \XX\
   /XXXX\           |XXXXX|             /XXXX\
 |XXXXXX\_         *XXXXXXX*         \_/XXXXXX|
XXXXXXXXXXXxxxxxxxXXXXXXXXXXXxxxxxxxXXXXXXXXXXX
|XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX|
XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
|XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX|
 XXXXXX/^^^^"\XXXXXXXXXXXXXXXXXXXXX/^^^^^\XXXXXX
 |XXX|       \XXX/^^\XXXXX/^^\XXX/       |XXX|
   \XX\       \X/    \XXX/    \X/       /XX/
      "\       "      \X/      "       /

    _    _  __             _
   / \  | |/ _|_ __ ___  __| |
  / _ \ | | |_| '__/ _ \/ _` |
 / ___ \| |  _| | |  __/ (_| |
/_/   \_\_|_| |_|  \___|\__,_|

Good day, sir. Alfred is at your service in the cave. What shall we accomplish?
____________________________________________________________
____________________________________________________________
Apologies, sir: Alfred's task list is empty, so there is nothing to mark.
____________________________________________________________
____________________________________________________________
Consider it noted in the cave ledger:
  [T][ ] read book
Your cave ledger now holds 1 task.
____________________________________________________________
____________________________________________________________
Apologies, sir: Alfred needs a task number after `mark`.
____________________________________________________________
____________________________________________________________
Apologies, sir: Alfred needs a whole-number task number after `mark`.
____________________________________________________________
____________________________________________________________
Apologies, sir: Alfred cannot find task 0. Choose a number from 1 to 1.
____________________________________________________________
____________________________________________________________
Apologies, sir: Alfred cannot find task 2. Choose a number from 1 to 1.
____________________________________________________________
____________________________________________________________
Mission accomplished. I've marked this task as done:
  [T][X] read book
____________________________________________________________
____________________________________________________________
Apologies, sir: Alfred needs a task number after `unmark`.
____________________________________________________________
____________________________________________________________
Apologies, sir: Alfred needs a whole-number task number after `unmark`.
____________________________________________________________
____________________________________________________________
Apologies, sir: Alfred cannot find task 4. Choose a number from 1 to 1.
____________________________________________________________
____________________________________________________________
Apologies, sir: Alfred cannot find task -1. Choose a number from 1 to 1.
____________________________________________________________
____________________________________________________________
I've reopened this mission:
  [T][ ] read book
____________________________________________________________
____________________________________________________________
Here is your cave ledger:
1.[T][ ] read book
____________________________________________________________
____________________________________________________________
The cave is secure. Until our next mission, sir.
____________________________________________________________
```

## Reject malformed deadlines without changing tasks

**Aim:** Verify that malformed deadlines show specific errors and leave a valid deadline unchanged.

### Input
```text
deadline submit report /by 2019-10-15
deadline submit report
deadline  /by 2019-10-15
deadline submit report /by
list
bye
```

### Expected output
```text
____________________________________________________________
      *==/          |     |            \==*
     /XX/           |\__\/|             \XX\
   /XXXX\           |XXXXX|             /XXXX\
 |XXXXXX\_         *XXXXXXX*         \_/XXXXXX|
XXXXXXXXXXXxxxxxxxXXXXXXXXXXXxxxxxxxXXXXXXXXXXX
|XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX|
XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
|XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX|
 XXXXXX/^^^^"\XXXXXXXXXXXXXXXXXXXXX/^^^^^\XXXXXX
 |XXX|       \XXX/^^\XXXXX/^^\XXX/       |XXX|
   \XX\       \X/    \XXX/    \X/       /XX/
      "\       "      \X/      "       /

    _    _  __             _
   / \  | |/ _|_ __ ___  __| |
  / _ \ | | |_| '__/ _ \/ _` |
 / ___ \| |  _| | |  __/ (_| |
/_/   \_\_|_| |_|  \___|\__,_|

Good day, sir. Alfred is at your service in the cave. What shall we accomplish?
____________________________________________________________
____________________________________________________________
Consider it noted in the cave ledger:
  [D][ ] submit report (by: Oct 15 2019)
Your cave ledger now holds 1 task.
____________________________________________________________
____________________________________________________________
Apologies, sir: Alfred needs `/by` followed by a due time for a deadline.
____________________________________________________________
____________________________________________________________
Apologies, sir: Alfred needs a deadline description before `/by`.
____________________________________________________________
____________________________________________________________
Apologies, sir: Alfred needs a due time after `/by`.
____________________________________________________________
____________________________________________________________
Here is your cave ledger:
1.[D][ ] submit report (by: Oct 15 2019)
____________________________________________________________
____________________________________________________________
The cave is secure. Until our next mission, sir.
____________________________________________________________
```

## Reject empty and unknown commands without changing tasks

**Aim:** Verify that invalid commands show Alfred-themed errors and do not change the existing task list.

### Input
```text
todo read book
todo
blah
list
bye
```

### Expected output
```text
____________________________________________________________
      *==/          |     |            \==*
     /XX/           |\__\/|             \XX\
   /XXXX\           |XXXXX|             /XXXX\
 |XXXXXX\_         *XXXXXXX*         \_/XXXXXX|
XXXXXXXXXXXxxxxxxxXXXXXXXXXXXxxxxxxxXXXXXXXXXXX
|XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX|
XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
|XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX|
 XXXXXX/^^^^"\XXXXXXXXXXXXXXXXXXXXX/^^^^^\XXXXXX
 |XXX|       \XXX/^^\XXXXX/^^\XXX/       |XXX|
   \XX\       \X/    \XXX/    \X/       /XX/
      "\       "      \X/      "       /

    _    _  __             _
   / \  | |/ _|_ __ ___  __| |
  / _ \ | | |_| '__/ _ \/ _` |
 / ___ \| |  _| | |  __/ (_| |
/_/   \_\_|_| |_|  \___|\__,_|

Good day, sir. Alfred is at your service in the cave. What shall we accomplish?
____________________________________________________________
____________________________________________________________
Consider it noted in the cave ledger:
  [T][ ] read book
Your cave ledger now holds 1 task.
____________________________________________________________
____________________________________________________________
Apologies, sir: Alfred cannot add a to-do without a mission description.
____________________________________________________________
____________________________________________________________
Apologies, sir: I do not recognise that command. Come again.
____________________________________________________________
____________________________________________________________
Here is your cave ledger:
1.[T][ ] read book
____________________________________________________________
____________________________________________________________
The cave is secure. Until our next mission, sir.
____________________________________________________________
```

## Validate delete inputs and the empty-list lifecycle

**Aim:** Verify every delete-number error category, deletion of the final task, and listing an
empty task collection.

### Input
```text
delete 1
todo keep me
delete
delete two
delete 0
delete -1
delete 2
list
delete 1
list
bye
```

### Expected output
```text
____________________________________________________________
      *==/          |     |            \==*
     /XX/           |\__\/|             \XX\
   /XXXX\           |XXXXX|             /XXXX\
 |XXXXXX\_         *XXXXXXX*         \_/XXXXXX|
XXXXXXXXXXXxxxxxxxXXXXXXXXXXXxxxxxxxXXXXXXXXXXX
|XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX|
XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
|XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX|
 XXXXXX/^^^^"\XXXXXXXXXXXXXXXXXXXXX/^^^^^\XXXXXX
 |XXX|       \XXX/^^\XXXXX/^^\XXX/       |XXX|
   \XX\       \X/    \XXX/    \X/       /XX/
      "\       "      \X/      "       /

    _    _  __             _
   / \  | |/ _|_ __ ___  __| |
  / _ \ | | |_| '__/ _ \/ _` |
 / ___ \| |  _| | |  __/ (_| |
/_/   \_\_|_| |_|  \___|\__,_|

Good day, sir. Alfred is at your service in the cave. What shall we accomplish?
____________________________________________________________
____________________________________________________________
Apologies, sir: Alfred's task list is empty, so there is nothing to delete.
____________________________________________________________
____________________________________________________________
Consider it noted in the cave ledger:
  [T][ ] keep me
Your cave ledger now holds 1 task.
____________________________________________________________
____________________________________________________________
Apologies, sir: Alfred needs a task number after `delete`.
____________________________________________________________
____________________________________________________________
Apologies, sir: Alfred needs a whole-number task number after `delete`.
____________________________________________________________
____________________________________________________________
Apologies, sir: Alfred cannot find task 0. Choose a number from 1 to 1.
____________________________________________________________
____________________________________________________________
Apologies, sir: Alfred cannot find task -1. Choose a number from 1 to 1.
____________________________________________________________
____________________________________________________________
Apologies, sir: Alfred cannot find task 2. Choose a number from 1 to 1.
____________________________________________________________
____________________________________________________________
Here is your cave ledger:
1.[T][ ] keep me
____________________________________________________________
____________________________________________________________
The following mission has left the cave ledger:
  [T][ ] keep me
Your cave ledger now holds 0 tasks.
____________________________________________________________
____________________________________________________________
Here is your cave ledger:
____________________________________________________________
____________________________________________________________
The cave is secure. Until our next mission, sir.
____________________________________________________________
```

## Parse, format, and reject task dates

**Aim:** Verify both supported date formats are displayed consistently and invalid dates do not change tasks.

### Input
```text
deadline do homework /by 2019-10-15
event orientation /from 2/12/2019 1800 /to 2019-12-03
deadline invalid deadline /by no idea :-p
event invalid event /from sometime soon /to 2019-12-03
list
bye
```

### Expected output
```text
____________________________________________________________
      *==/          |     |            \==*
     /XX/           |\__\/|             \XX\
   /XXXX\           |XXXXX|             /XXXX\
 |XXXXXX\_         *XXXXXXX*         \_/XXXXXX|
XXXXXXXXXXXxxxxxxxXXXXXXXXXXXxxxxxxxXXXXXXXXXXX
|XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX|
XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
|XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX|
 XXXXXX/^^^^"\XXXXXXXXXXXXXXXXXXXXX/^^^^^\XXXXXX
 |XXX|       \XXX/^^\XXXXX/^^\XXX/       |XXX|
   \XX\       \X/    \XXX/    \X/       /XX/
      "\       "      \X/      "       /

    _    _  __             _
   / \  | |/ _|_ __ ___  __| |
  / _ \ | | |_| '__/ _ \/ _` |
 / ___ \| |  _| | |  __/ (_| |
/_/   \_\_|_| |_|  \___|\__,_|

Good day, sir. Alfred is at your service in the cave. What shall we accomplish?
____________________________________________________________
____________________________________________________________
Consider it noted in the cave ledger:
  [D][ ] do homework (by: Oct 15 2019)
Your cave ledger now holds 1 task.
____________________________________________________________
____________________________________________________________
Consider it noted in the cave ledger:
  [E][ ] orientation (from: Dec 02 2019 18:00 to: Dec 03 2019)
Your cave ledger now holds 2 tasks.
____________________________________________________________
____________________________________________________________
Apologies, sir: Alfred needs a valid date in yyyy-MM-dd or d/M/yyyy HHmm format.
____________________________________________________________
____________________________________________________________
Apologies, sir: Alfred needs a valid date in yyyy-MM-dd or d/M/yyyy HHmm format.
____________________________________________________________
____________________________________________________________
Here is your cave ledger:
1.[D][ ] do homework (by: Oct 15 2019)
2.[E][ ] orientation (from: Dec 02 2019 18:00 to: Dec 03 2019)
____________________________________________________________
____________________________________________________________
The cave is secure. Until our next mission, sir.
____________________________________________________________
```

## Find matching task descriptions

**Aim:** Verify that find matches descriptions case-insensitively, reports no matches, rejects a missing keyword, and preserves task state.

### Input
```text
todo read book
deadline return BOOK /by 2019-06-06
event project meeting /from 2019-06-01 /to 2019-06-02
mark 1
find book
find report
find
list
bye
```

### Expected output
```text
____________________________________________________________
      *==/          |     |            \==*
     /XX/           |\__\/|             \XX\
   /XXXX\           |XXXXX|             /XXXX\
 |XXXXXX\_         *XXXXXXX*         \_/XXXXXX|
XXXXXXXXXXXxxxxxxxXXXXXXXXXXXxxxxxxxXXXXXXXXXXX
|XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX|
XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
|XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX|
 XXXXXX/^^^^"\XXXXXXXXXXXXXXXXXXXXX/^^^^^\XXXXXX
 |XXX|       \XXX/^^\XXXXX/^^\XXX/       |XXX|
   \XX\       \X/    \XXX/    \X/       /XX/
      "\       "      \X/      "       /

    _    _  __             _
   / \  | |/ _|_ __ ___  __| |
  / _ \ | | |_| '__/ _ \/ _` |
 / ___ \| |  _| | |  __/ (_| |
/_/   \_\_|_| |_|  \___|\__,_|

Good day, sir. Alfred is at your service in the cave. What shall we accomplish?
____________________________________________________________
____________________________________________________________
Consider it noted in the cave ledger:
  [T][ ] read book
Your cave ledger now holds 1 task.
____________________________________________________________
____________________________________________________________
Consider it noted in the cave ledger:
  [D][ ] return BOOK (by: Jun 06 2019)
Your cave ledger now holds 2 tasks.
____________________________________________________________
____________________________________________________________
Consider it noted in the cave ledger:
  [E][ ] project meeting (from: Jun 01 2019 to: Jun 02 2019)
Your cave ledger now holds 3 tasks.
____________________________________________________________
____________________________________________________________
Mission accomplished. I've marked this task as done:
  [T][X] read book
____________________________________________________________
____________________________________________________________
Here are the matching missions in your cave ledger:
1.[T][X] read book
2.[D][ ] return BOOK (by: Jun 06 2019)
____________________________________________________________
____________________________________________________________
Here are the matching missions in your cave ledger:
____________________________________________________________
____________________________________________________________
Apologies, sir: Alfred needs a keyword after `find`.
____________________________________________________________
____________________________________________________________
Here is your cave ledger:
1.[T][X] read book
2.[D][ ] return BOOK (by: Jun 06 2019)
3.[E][ ] project meeting (from: Jun 01 2019 to: Jun 02 2019)
____________________________________________________________
____________________________________________________________
The cave is secure. Until our next mission, sir.
____________________________________________________________
```

## Sort tasks without changing stored order

**Aim:** Verify ascending and descending chronological views, stable equal-date ordering, to-do placement,
rejected sort syntax, and preservation of the original task order.

### Input
```text
todo first todo
deadline later deadline /by 2019-12-03
event same time event /from 2/12/2019 1800 /to 2/12/2019 1900
deadline same time deadline /by 2/12/2019 1800
todo second todo
event early event /from 2019-12-01 /to 2019-12-02
sort
sort asc
sort desc
sort ascending
SORT asc
list
bye
```

### Expected output
```text
____________________________________________________________
      *==/          |     |            \==*
     /XX/           |\__\/|             \XX\
   /XXXX\           |XXXXX|             /XXXX\
 |XXXXXX\_         *XXXXXXX*         \_/XXXXXX|
XXXXXXXXXXXxxxxxxxXXXXXXXXXXXxxxxxxxXXXXXXXXXXX
|XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX|
XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
|XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX|
 XXXXXX/^^^^"\XXXXXXXXXXXXXXXXXXXXX/^^^^^\XXXXXX
 |XXX|       \XXX/^^\XXXXX/^^\XXX/       |XXX|
   \XX\       \X/    \XXX/    \X/       /XX/
      "\       "      \X/      "       /

    _    _  __             _
   / \  | |/ _|_ __ ___  __| |
  / _ \ | | |_| '__/ _ \/ _` |
 / ___ \| |  _| | |  __/ (_| |
/_/   \_\_|_| |_|  \___|\__,_|

Good day, sir. Alfred is at your service in the cave. What shall we accomplish?
____________________________________________________________
____________________________________________________________
Consider it noted in the cave ledger:
  [T][ ] first todo
Your cave ledger now holds 1 task.
____________________________________________________________
____________________________________________________________
Consider it noted in the cave ledger:
  [D][ ] later deadline (by: Dec 03 2019)
Your cave ledger now holds 2 tasks.
____________________________________________________________
____________________________________________________________
Consider it noted in the cave ledger:
  [E][ ] same time event (from: Dec 02 2019 18:00 to: Dec 02 2019 19:00)
Your cave ledger now holds 3 tasks.
____________________________________________________________
____________________________________________________________
Consider it noted in the cave ledger:
  [D][ ] same time deadline (by: Dec 02 2019 18:00)
Your cave ledger now holds 4 tasks.
____________________________________________________________
____________________________________________________________
Consider it noted in the cave ledger:
  [T][ ] second todo
Your cave ledger now holds 5 tasks.
____________________________________________________________
____________________________________________________________
Consider it noted in the cave ledger:
  [E][ ] early event (from: Dec 01 2019 to: Dec 02 2019)
Your cave ledger now holds 6 tasks.
____________________________________________________________
____________________________________________________________
Here is your cave ledger in ascending chronological order:
1.[E][ ] early event (from: Dec 01 2019 to: Dec 02 2019)
2.[E][ ] same time event (from: Dec 02 2019 18:00 to: Dec 02 2019 19:00)
3.[D][ ] same time deadline (by: Dec 02 2019 18:00)
4.[D][ ] later deadline (by: Dec 03 2019)
5.[T][ ] first todo
6.[T][ ] second todo
____________________________________________________________
____________________________________________________________
Here is your cave ledger in ascending chronological order:
1.[E][ ] early event (from: Dec 01 2019 to: Dec 02 2019)
2.[E][ ] same time event (from: Dec 02 2019 18:00 to: Dec 02 2019 19:00)
3.[D][ ] same time deadline (by: Dec 02 2019 18:00)
4.[D][ ] later deadline (by: Dec 03 2019)
5.[T][ ] first todo
6.[T][ ] second todo
____________________________________________________________
____________________________________________________________
Here is your cave ledger in descending chronological order:
1.[T][ ] first todo
2.[T][ ] second todo
3.[D][ ] later deadline (by: Dec 03 2019)
4.[E][ ] same time event (from: Dec 02 2019 18:00 to: Dec 02 2019 19:00)
5.[D][ ] same time deadline (by: Dec 02 2019 18:00)
6.[E][ ] early event (from: Dec 01 2019 to: Dec 02 2019)
____________________________________________________________
____________________________________________________________
Apologies, sir: Alfred only understands `sort`, `sort asc`, or `sort desc`.
____________________________________________________________
____________________________________________________________
Apologies, sir: Alfred only understands `sort`, `sort asc`, or `sort desc`.
____________________________________________________________
____________________________________________________________
Here is your cave ledger:
1.[T][ ] first todo
2.[D][ ] later deadline (by: Dec 03 2019)
3.[E][ ] same time event (from: Dec 02 2019 18:00 to: Dec 02 2019 19:00)
4.[D][ ] same time deadline (by: Dec 02 2019 18:00)
5.[T][ ] second todo
6.[E][ ] early event (from: Dec 01 2019 to: Dec 02 2019)
____________________________________________________________
____________________________________________________________
The cave is secure. Until our next mission, sir.
____________________________________________________________
```

## Sort an empty task list

**Aim:** Verify that sorting an empty task list displays an empty chronological view without an error.

### Input
```text
sort
bye
```

### Expected output
```text
____________________________________________________________
      *==/          |     |            \==*
     /XX/           |\__\/|             \XX\
   /XXXX\           |XXXXX|             /XXXX\
 |XXXXXX\_         *XXXXXXX*         \_/XXXXXX|
XXXXXXXXXXXxxxxxxxXXXXXXXXXXXxxxxxxxXXXXXXXXXXX
|XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX|
XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
|XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX|
 XXXXXX/^^^^"\XXXXXXXXXXXXXXXXXXXXX/^^^^^\XXXXXX
 |XXX|       \XXX/^^\XXXXX/^^\XXX/       |XXX|
   \XX\       \X/    \XXX/    \X/       /XX/
      "\       "      \X/      "       /

    _    _  __             _
   / \  | |/ _|_ __ ___  __| |
  / _ \ | | |_| '__/ _ \/ _` |
 / ___ \| |  _| | |  __/ (_| |
/_/   \_\_|_| |_|  \___|\__,_|

Good day, sir. Alfred is at your service in the cave. What shall we accomplish?
____________________________________________________________
____________________________________________________________
Here is your cave ledger in ascending chronological order:
____________________________________________________________
____________________________________________________________
The cave is secure. Until our next mission, sir.
____________________________________________________________
```

## Reject duplicate tasks and strict command syntax

**Aim:** Verify that duplicate missions, repeated markers, invalid event ranges, and extra
arguments are rejected without changing the valid task list.

### Input
```text
todo keep record
todo keep record
deadline submit report /by 2019-12-02
deadline resubmit /by 2019-12-02 /by 2019-12-03
event team meeting /from 2019-12-02 /to 2019-12-03
event same instant /from 2019-12-02 /to 2019-12-02
event backwards /from 2019-12-03 /to 2019-12-02
event repeated start /from 2019-12-02 /from 2019-12-03 /to 2019-12-04
event repeated end /from 2019-12-02 /to 2019-12-03 /to 2019-12-04
list extra
bye later
list
bye
```

### Expected output
```text
____________________________________________________________
      *==/          |     |            \==*
     /XX/           |\__\/|             \XX\
   /XXXX\           |XXXXX|             /XXXX\
 |XXXXXX\_         *XXXXXXX*         \_/XXXXXX|
XXXXXXXXXXXxxxxxxxXXXXXXXXXXXxxxxxxxXXXXXXXXXXX
|XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX|
XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
|XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX|
 XXXXXX/^^^^"\XXXXXXXXXXXXXXXXXXXXX/^^^^^\XXXXXX
 |XXX|       \XXX/^^\XXXXX/^^\XXX/       |XXX|
   \XX\       \X/    \XXX/    \X/       /XX/
      "\       "      \X/      "       /

    _    _  __             _
   / \  | |/ _|_ __ ___  __| |
  / _ \ | | |_| '__/ _ \/ _` |
 / ___ \| |  _| | |  __/ (_| |
/_/   \_\_|_| |_|  \___|\__,_|

Good day, sir. Alfred is at your service in the cave. What shall we accomplish?
____________________________________________________________
____________________________________________________________
Consider it noted in the cave ledger:
  [T][ ] keep record
Your cave ledger now holds 1 task.
____________________________________________________________
____________________________________________________________
Apologies, sir: Alfred already has an identical mission in the cave ledger.
____________________________________________________________
____________________________________________________________
Consider it noted in the cave ledger:
  [D][ ] submit report (by: Dec 02 2019)
Your cave ledger now holds 2 tasks.
____________________________________________________________
____________________________________________________________
Apologies, sir: Alfred accepts only one `/by` marker for a deadline.
____________________________________________________________
____________________________________________________________
Consider it noted in the cave ledger:
  [E][ ] team meeting (from: Dec 02 2019 to: Dec 03 2019)
Your cave ledger now holds 3 tasks.
____________________________________________________________
____________________________________________________________
Apologies, sir: Alfred needs an event end time later than its start time.
____________________________________________________________
____________________________________________________________
Apologies, sir: Alfred needs an event end time later than its start time.
____________________________________________________________
____________________________________________________________
Apologies, sir: Alfred accepts only one `/from` marker for an event.
____________________________________________________________
____________________________________________________________
Apologies, sir: Alfred accepts only one `/to` marker for an event.
____________________________________________________________
____________________________________________________________
Apologies, sir: Alfred's `list` command does not take additional arguments.
____________________________________________________________
____________________________________________________________
Apologies, sir: Alfred's `bye` command does not take additional arguments.
____________________________________________________________
____________________________________________________________
Here is your cave ledger:
1.[T][ ] keep record
2.[D][ ] submit report (by: Dec 02 2019)
3.[E][ ] team meeting (from: Dec 02 2019 to: Dec 03 2019)
____________________________________________________________
____________________________________________________________
The cave is secure. Until our next mission, sir.
____________________________________________________________
```

## Manual JavaFX verification

Run `./gradlew run`. The resizable Alfred window should open with a scrollable dialog area, a
text field, and a Send button. It must show Alfred's `Good day, sir. Alfred is at your service in the cave. What shall we accomplish?` welcome
message before any user input. Enter `todo read book` and press Enter; the response must confirm
that the task was added. Then enter `list` and click Send; the response must include the saved
task. Enter `todo` and verify the error response does not remove the task shown by a final `list`.
Enter `sort desc` and verify that it displays a descending view without changing the order shown by
a following `list`. Enter enough commands to verify that the scroll pane follows the newest dialog. Resize the window
to verify that the scroll area and text field grow with it while Send remains at the bottom-right.
Finally, enter `bye`; Alfred must show the farewell response and then close the window. Enter an
invalid command before `bye` to verify that only a valid exit command closes the application.
This check is manual because the console UI test runner has no access to a desktop window.
