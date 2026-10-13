# Console UI Test Plan

The `test-ui` skill runs each test case in a fresh Ruby process. Each `### Input` block contains one command, and the following `### Expected output` block contains its exact response. Keep the headings and fenced blocks unchanged so the test runner can parse them.

Optional persistence blocks: `### Data file` seeds `data/ruby.txt` before the process starts, and `### Saved data` asserts the exact contents of `data/ruby.txt` after the process exits.

## Greeting and exit

Aim: Verify that Ruby prints its banner and greeting on startup, then exits with the required farewell.

### Startup output

```text
____________________________________________________________
 /$$$$$$$            /$$
 | $$__  $$          | $$
 | $$  \ $$ /$$   /$$| $$$$$$$  /$$   /$$
 | $$$$$$$/| $$  | $$| $$__  $$| $$  | $$
 | $$__  $$| $$  | $$| $$  \ $$| $$  | $$
 | $$  \ $$| $$  | $$| $$  | $$| $$  | $$
 | $$  | $$|  $$$$$$/| $$$$$$$/|  $$$$$$$
 |__/  |__/ \______/ |_______/  \____  $$
                                /$$  | $$
                               |  $$$$$$/
                                \______/
 Hi, I'm Ruby — the gem your task list has been missing.
 What are we polishing today?
____________________________________________________________
```

### Input

```text
bye
```

### Expected output

```text
____________________________________________________________
 Bye. Your tasks are in precious hands.
____________________________________________________________
```

## Duplicate tasks in saved data

Aim: Verify that tasks saved by an earlier version are restored even when the file contains duplicates.

### Startup output

```text
____________________________________________________________
 /$$$$$$$            /$$
 | $$__  $$          | $$
 | $$  \ $$ /$$   /$$| $$$$$$$  /$$   /$$
 | $$$$$$$/| $$  | $$| $$__  $$| $$  | $$
 | $$__  $$| $$  | $$| $$  \ $$| $$  | $$
 | $$  \ $$| $$  | $$| $$  | $$| $$  | $$
 | $$  | $$|  $$$$$$/| $$$$$$$/|  $$$$$$$
 |__/  |__/ \______/ |_______/  \____  $$
                                /$$  | $$
                               |  $$$$$$/
                                \______/
 Hi, I'm Ruby — the gem your task list has been missing.
 What are we polishing today?
____________________________________________________________
```

### Data file

```text
T | 0 | read book
T | 0 | read book
```

### Input

```text
list
```

### Expected output

```text
____________________________________________________________
 Here's everything on your plate:
 1. [T][ ] read book
 2. [T][ ] read book
____________________________________________________________
```

## Level 4 task workflow

Aim: Verify creation, listing, marking, and unmarking for todos, deadlines, and events.

### Startup output

```text
____________________________________________________________
 /$$$$$$$            /$$
 | $$__  $$          | $$
 | $$  \ $$ /$$   /$$| $$$$$$$  /$$   /$$
 | $$$$$$$/| $$  | $$| $$__  $$| $$  | $$
 | $$__  $$| $$  | $$| $$  \ $$| $$  | $$
 | $$  \ $$| $$  | $$| $$  | $$| $$  | $$
 | $$  | $$|  $$$$$$/| $$$$$$$/|  $$$$$$$
 |__/  |__/ \______/ |_______/  \____  $$
                                /$$  | $$
                               |  $$$$$$/
                                \______/
 Hi, I'm Ruby — the gem your task list has been missing.
 What are we polishing today?
____________________________________________________________
```

### Input

```text
todo borrow book
```

### Expected output

```text
____________________________________________________________
 Added to the collection:
   [T][ ] borrow book
 That's 1 task on your plate.
____________________________________________________________
```

### Input

```text
deadline return book /by 2019-06-06
```

### Expected output

```text
____________________________________________________________
 Added to the collection:
   [D][ ] return book (by: Jun 06 2019)
 That's 2 tasks on your plate.
____________________________________________________________
```

### Input

```text
event project meeting /from 2019-08-06 1400 /to 2019-08-06 1600
```

### Expected output

```text
____________________________________________________________
 Added to the collection:
   [E][ ] project meeting (from: Aug 06 2019, 14:00 to: Aug 06 2019, 16:00)
 That's 3 tasks on your plate.
____________________________________________________________
```

### Input

```text
list
```

### Expected output

```text
____________________________________________________________
 Here's everything on your plate:
 1. [T][ ] borrow book
 2. [D][ ] return book (by: Jun 06 2019)
 3. [E][ ] project meeting (from: Aug 06 2019, 14:00 to: Aug 06 2019, 16:00)
____________________________________________________________
```

### Input

```text
mark 2
```

### Expected output

```text
____________________________________________________________
 Done — consider it polished:
   [D][X] return book (by: Jun 06 2019)
____________________________________________________________
```

### Input

```text
unmark 2
```

### Expected output

```text
____________________________________________________________
 Undone — brilliance takes time:
   [D][ ] return book (by: Jun 06 2019)
____________________________________________________________
```

### Input

```text
bye
```

### Expected output

```text
____________________________________________________________
 Bye. Your tasks are in precious hands.
____________________________________________________________
```

## Level 5 error recovery

Aim: Verify that malformed and unknown commands report specific errors without changing valid task state.

### Startup output

```text
____________________________________________________________
 /$$$$$$$            /$$
 | $$__  $$          | $$
 | $$  \ $$ /$$   /$$| $$$$$$$  /$$   /$$
 | $$$$$$$/| $$  | $$| $$__  $$| $$  | $$
 | $$__  $$| $$  | $$| $$  \ $$| $$  | $$
 | $$  \ $$| $$  | $$| $$  | $$| $$  | $$
 | $$  | $$|  $$$$$$/| $$$$$$$/|  $$$$$$$
 |__/  |__/ \______/ |_______/  \____  $$
                                /$$  | $$
                               |  $$$$$$/
                                \______/
 Hi, I'm Ruby — the gem your task list has been missing.
 What are we polishing today?
____________________________________________________________
```

### Input

```text

```

### Expected output

```text
____________________________________________________________
 Hold on — I can't work with silence. Type a command.
____________________________________________________________
```

### Input

```text
todo read book
```

### Expected output

```text
____________________________________________________________
 Added to the collection:
   [T][ ] read book
 That's 1 task on your plate.
____________________________________________________________
```

### Input

```text
todo
```

### Expected output

```text
____________________________________________________________
 Hold on — A todo with no description? Give me something to work with after todo.
____________________________________________________________
```

### Input

```text
blah
```

### Expected output

```text
____________________________________________________________
 Hold on — That's not in my repertoire. Try a command I know.
____________________________________________________________
```

### Input

```text
event meeting /from 2026-08-28 1800 /to 2026-08-28 1800
```

### Expected output

```text
____________________________________________________________
 Hold on — An event must end after it starts.
____________________________________________________________
```

### Input

```text
todo read book
```

### Expected output

```text
____________________________________________________________
 Hold on — That task is already in your collection.
____________________________________________________________
```

### Input

```text
todoish read book
```

### Expected output

```text
____________________________________________________________
 Hold on — That's not in my repertoire. Try a command I know.
____________________________________________________________
```

### Input

```text
deadline /by Sunday
```

### Expected output

```text
____________________________________________________________
 Hold on — A deadline with no description? Give me something to call it.
____________________________________________________________
```

### Input

```text
deadline submit report
```

### Expected output

```text
____________________________________________________________
 Hold on — That's not how deadlines work. Use: deadline DESCRIPTION /by DATE_OR_TIME.
____________________________________________________________
```

### Input

```text
deadline submit report /bypass Sunday
```

### Expected output

```text
____________________________________________________________
 Hold on — That's not how deadlines work. Use: deadline DESCRIPTION /by DATE_OR_TIME.
____________________________________________________________
```

### Input

```text
deadline submit report /by
```

### Expected output

```text
____________________________________________________________
 Hold on — A deadline needs a date or time after /by — I can't read minds.
____________________________________________________________
```

### Input

```text
deadline submit report /by Sunday
```

### Expected output

```text
____________________________________________________________
 Hold on — That date is a mystery even to me. Use yyyy-mm-dd (e.g. 2019-10-15) or yyyy-mm-dd HHmm (e.g. 2026-10-15 1800).
____________________________________________________________
```

### Input

```text
deadline submit report /by 2019-06-06
```

### Expected output

```text
____________________________________________________________
 Added to the collection:
   [D][ ] submit report (by: Jun 06 2019)
 That's 2 tasks on your plate.
____________________________________________________________
```

### Input

```text
event /from Mon /to Tue
```

### Expected output

```text
____________________________________________________________
 Hold on — An event with no description? Give me something to call it.
____________________________________________________________
```

### Input

```text
event meeting /to Tue
```

### Expected output

```text
____________________________________________________________
 Hold on — That's not how events work. Use: event DESCRIPTION /from START /to END.
____________________________________________________________
```

### Input

```text
event meeting /fromage Mon /to Tue
```

### Expected output

```text
____________________________________________________________
 Hold on — That's not how events work. Use: event DESCRIPTION /from START /to END.
____________________________________________________________
```

### Input

```text
event meeting /from Mon
```

### Expected output

```text
____________________________________________________________
 Hold on — An event needs an end after /to — don't leave me guessing.
____________________________________________________________
```

### Input

```text
event meeting /from /to Tue
```

### Expected output

```text
____________________________________________________________
 Hold on — An event needs a start after /from — don't leave me guessing.
____________________________________________________________
```

### Input

```text
event meeting /from Mon /to
```

### Expected output

```text
____________________________________________________________
 Hold on — An event needs an end after /to — don't leave me guessing.
____________________________________________________________
```

### Input

```text
event meeting /from 2019-08-06 1400 /to 2019-08-06 1600
```

### Expected output

```text
____________________________________________________________
 Added to the collection:
   [E][ ] meeting (from: Aug 06 2019, 14:00 to: Aug 06 2019, 16:00)
 That's 3 tasks on your plate.
____________________________________________________________
```

### Input

```text
mark
```

### Expected output

```text
____________________________________________________________
 Hold on — You forgot the number. Give me a task number after mark.
____________________________________________________________
```

### Input

```text
mark banana
```

### Expected output

```text
____________________________________________________________
 Hold on — That's not a number. Give me a whole number after mark.
____________________________________________________________
```

### Input

```text
mark 7
```

### Expected output

```text
____________________________________________________________
 Hold on — Task 7? You only have 3. Pick a number from 1 to 3.
____________________________________________________________
```

### Input

```text
mark 2
```

### Expected output

```text
____________________________________________________________
 Done — consider it polished:
   [D][X] submit report (by: Jun 06 2019)
____________________________________________________________
```

### Input

```text
unmark
```

### Expected output

```text
____________________________________________________________
 Hold on — You forgot the number. Give me a task number after unmark.
____________________________________________________________
```

### Input

```text
list
```

### Expected output

```text
____________________________________________________________
 Here's everything on your plate:
 1. [T][ ] read book
 2. [D][X] submit report (by: Jun 06 2019)
 3. [E][ ] meeting (from: Aug 06 2019, 14:00 to: Aug 06 2019, 16:00)
____________________________________________________________
```

### Input

```text
bye
```

### Expected output

```text
____________________________________________________________
 Bye. Your tasks are in precious hands.
____________________________________________________________
```

## Level 6 deletion and index shifting

Aim: Verify collection-backed deletion, invalid delete handling, task counts, and indexes after removals.

### Startup output

```text
____________________________________________________________
 /$$$$$$$            /$$
 | $$__  $$          | $$
 | $$  \ $$ /$$   /$$| $$$$$$$  /$$   /$$
 | $$$$$$$/| $$  | $$| $$__  $$| $$  | $$
 | $$__  $$| $$  | $$| $$  \ $$| $$  | $$
 | $$  \ $$| $$  | $$| $$  | $$| $$  | $$
 | $$  | $$|  $$$$$$/| $$$$$$$/|  $$$$$$$
 |__/  |__/ \______/ |_______/  \____  $$
                                /$$  | $$
                               |  $$$$$$/
                                \______/
 Hi, I'm Ruby — the gem your task list has been missing.
 What are we polishing today?
____________________________________________________________
```

### Input

```text
delete
```

### Expected output

```text
____________________________________________________________
 Hold on — You forgot the number. Give me a task number after delete.
____________________________________________________________
```

### Input

```text
delete 1
```

### Expected output

```text
____________________________________________________________
 Hold on — You have no tasks to delete — add one first.
____________________________________________________________
```

### Input

```text
todo read book
```

### Expected output

```text
____________________________________________________________
 Added to the collection:
   [T][ ] read book
 That's 1 task on your plate.
____________________________________________________________
```

### Input

```text
deadline return book /by 2019-06-06
```

### Expected output

```text
____________________________________________________________
 Added to the collection:
   [D][ ] return book (by: Jun 06 2019)
 That's 2 tasks on your plate.
____________________________________________________________
```

### Input

```text
event project meeting /from 2019-08-06 1400 /to 2019-08-06 1600
```

### Expected output

```text
____________________________________________________________
 Added to the collection:
   [E][ ] project meeting (from: Aug 06 2019, 14:00 to: Aug 06 2019, 16:00)
 That's 3 tasks on your plate.
____________________________________________________________
```

### Input

```text
delete banana
```

### Expected output

```text
____________________________________________________________
 Hold on — That's not a number. Give me a whole number after delete.
____________________________________________________________
```

### Input

```text
delete 0
```

### Expected output

```text
____________________________________________________________
 Hold on — Since when is 0 a valid task number? Give me a positive whole number after delete.
____________________________________________________________
```

### Input

```text
delete 4
```

### Expected output

```text
____________________________________________________________
 Hold on — Task 4? You only have 3. Pick a number from 1 to 3.
____________________________________________________________
```

### Input

```text
delete 2
```

### Expected output

```text
____________________________________________________________
 Removed — gone without a trace:
   [D][ ] return book (by: Jun 06 2019)
 That leaves 2 tasks on your plate.
____________________________________________________________
```

### Input

```text
list
```

### Expected output

```text
____________________________________________________________
 Here's everything on your plate:
 1. [T][ ] read book
 2. [E][ ] project meeting (from: Aug 06 2019, 14:00 to: Aug 06 2019, 16:00)
____________________________________________________________
```

### Input

```text
mark 2
```

### Expected output

```text
____________________________________________________________
 Done — consider it polished:
   [E][X] project meeting (from: Aug 06 2019, 14:00 to: Aug 06 2019, 16:00)
____________________________________________________________
```

### Input

```text
delete 2
```

### Expected output

```text
____________________________________________________________
 Removed — gone without a trace:
   [E][X] project meeting (from: Aug 06 2019, 14:00 to: Aug 06 2019, 16:00)
 That leaves 1 task on your plate.
____________________________________________________________
```

### Input

```text
list
```

### Expected output

```text
____________________________________________________________
 Here's everything on your plate:
 1. [T][ ] read book
____________________________________________________________
```

### Input

```text
bye
```

### Expected output

```text
____________________________________________________________
 Bye. Your tasks are in precious hands.
____________________________________________________________
```

## Level 7 tasks are saved to disk

Aim: Verify that adding, marking, and deleting tasks writes the data file in the expected format.

### Startup output

```text
____________________________________________________________
 /$$$$$$$            /$$
 | $$__  $$          | $$
 | $$  \ $$ /$$   /$$| $$$$$$$  /$$   /$$
 | $$$$$$$/| $$  | $$| $$__  $$| $$  | $$
 | $$__  $$| $$  | $$| $$  \ $$| $$  | $$
 | $$  \ $$| $$  | $$| $$  | $$| $$  | $$
 | $$  | $$|  $$$$$$/| $$$$$$$/|  $$$$$$$
 |__/  |__/ \______/ |_______/  \____  $$
                                /$$  | $$
                               |  $$$$$$/
                                \______/
 Hi, I'm Ruby — the gem your task list has been missing.
 What are we polishing today?
____________________________________________________________
```

### Input

```text
todo read book
```

### Expected output

```text
____________________________________________________________
 Added to the collection:
   [T][ ] read book
 That's 1 task on your plate.
____________________________________________________________
```

### Input

```text
deadline submit report /by 2019-06-06
```

### Expected output

```text
____________________________________________________________
 Added to the collection:
   [D][ ] submit report (by: Jun 06 2019)
 That's 2 tasks on your plate.
____________________________________________________________
```

### Input

```text
event project meeting /from 2019-08-06 1400 /to 2019-08-06 1600
```

### Expected output

```text
____________________________________________________________
 Added to the collection:
   [E][ ] project meeting (from: Aug 06 2019, 14:00 to: Aug 06 2019, 16:00)
 That's 3 tasks on your plate.
____________________________________________________________
```

### Input

```text
mark 2
```

### Expected output

```text
____________________________________________________________
 Done — consider it polished:
   [D][X] submit report (by: Jun 06 2019)
____________________________________________________________
```

### Input

```text
delete 1
```

### Expected output

```text
____________________________________________________________
 Removed — gone without a trace:
   [T][ ] read book
 That leaves 2 tasks on your plate.
____________________________________________________________
```

### Input

```text
bye
```

### Expected output

```text
____________________________________________________________
 Bye. Your tasks are in precious hands.
____________________________________________________________
```

### Saved data

```text
D | 1 | submit report | 2019-06-06T00:00
E | 0 | project meeting | 2019-08-06T14:00 | 2019-08-06T16:00
```

## Level 7 saved tasks are loaded at startup

Aim: Verify that tasks saved in the data file are restored on startup with their type and done status.

### Data file

```text
T | 1 | read book
D | 0 | return book | 2019-06-06T00:00
E | 1 | project meeting | 2019-08-06T14:00 | 2019-08-06T16:00
```

### Startup output

```text
____________________________________________________________
 /$$$$$$$            /$$
 | $$__  $$          | $$
 | $$  \ $$ /$$   /$$| $$$$$$$  /$$   /$$
 | $$$$$$$/| $$  | $$| $$__  $$| $$  | $$
 | $$__  $$| $$  | $$| $$  \ $$| $$  | $$
 | $$  \ $$| $$  | $$| $$  | $$| $$  | $$
 | $$  | $$|  $$$$$$/| $$$$$$$/|  $$$$$$$
 |__/  |__/ \______/ |_______/  \____  $$
                                /$$  | $$
                               |  $$$$$$/
                                \______/
 Hi, I'm Ruby — the gem your task list has been missing.
 What are we polishing today?
____________________________________________________________
```

### Input

```text
list
```

### Expected output

```text
____________________________________________________________
 Here's everything on your plate:
 1. [T][X] read book
 2. [D][ ] return book (by: Jun 06 2019)
 3. [E][X] project meeting (from: Aug 06 2019, 14:00 to: Aug 06 2019, 16:00)
____________________________________________________________
```

### Input

```text
bye
```

### Expected output

```text
____________________________________________________________
 Bye. Your tasks are in precious hands.
____________________________________________________________
```

## Level 9 find

Aim: Verify that find lists matching tasks, matches case-insensitively, reports no matches, and leaves the data file unchanged.

### Startup output

```text
____________________________________________________________
 /$$$$$$$            /$$
 | $$__  $$          | $$
 | $$  \ $$ /$$   /$$| $$$$$$$  /$$   /$$
 | $$$$$$$/| $$  | $$| $$__  $$| $$  | $$
 | $$__  $$| $$  | $$| $$  \ $$| $$  | $$
 | $$  \ $$| $$  | $$| $$  | $$| $$  | $$
 | $$  | $$|  $$$$$$/| $$$$$$$/|  $$$$$$$
 |__/  |__/ \______/ |_______/  \____  $$
                                /$$  | $$
                               |  $$$$$$/
                                \______/
 Hi, I'm Ruby — the gem your task list has been missing.
 What are we polishing today?
____________________________________________________________
```

### Input

```text
todo read book
```

### Expected output

```text
____________________________________________________________
 Added to the collection:
   [T][ ] read book
 That's 1 task on your plate.
____________________________________________________________
```

### Input

```text
deadline return book /by 2019-06-06
```

### Expected output

```text
____________________________________________________________
 Added to the collection:
   [D][ ] return book (by: Jun 06 2019)
 That's 2 tasks on your plate.
____________________________________________________________
```

### Input

```text
event project meeting /from 2019-08-06 1400 /to 2019-08-06 1600
```

### Expected output

```text
____________________________________________________________
 Added to the collection:
   [E][ ] project meeting (from: Aug 06 2019, 14:00 to: Aug 06 2019, 16:00)
 That's 3 tasks on your plate.
____________________________________________________________
```

### Input

```text
find book
```

### Expected output

```text
____________________________________________________________
 Found them — I never miss:
 1. [T][ ] read book
 2. [D][ ] return book (by: Jun 06 2019)
____________________________________________________________
```

### Input

```text
find BOOK
```

### Expected output

```text
____________________________________________________________
 Found them — I never miss:
 1. [T][ ] read book
 2. [D][ ] return book (by: Jun 06 2019)
____________________________________________________________
```

### Input

```text
find dance
```

### Expected output

```text
____________________________________________________________
 Nothing. Even I can't find what isn't there.
____________________________________________________________
```

### Input

```text
find
```

### Expected output

```text
____________________________________________________________
 Hold on — Find what? Give me a keyword after find.
____________________________________________________________
```

### Input

```text
bye
```

### Expected output

```text
____________________________________________________________
 Bye. Your tasks are in precious hands.
____________________________________________________________
```

### Saved data

```text
T | 0 | read book
D | 0 | return book | 2019-06-06T00:00
E | 0 | project meeting | 2019-08-06T14:00 | 2019-08-06T16:00
```

## Contacts

Aim: Verify adding, listing, and deleting contacts, including validation errors.

### Startup output

```text
____________________________________________________________
 /$$$$$$$            /$$
 | $$__  $$          | $$
 | $$  \ $$ /$$   /$$| $$$$$$$  /$$   /$$
 | $$$$$$$/| $$  | $$| $$__  $$| $$  | $$
 | $$__  $$| $$  | $$| $$  \ $$| $$  | $$
 | $$  \ $$| $$  | $$| $$  | $$| $$  | $$
 | $$  | $$|  $$$$$$/| $$$$$$$/|  $$$$$$$
 |__/  |__/ \______/ |_______/  \____  $$
                                /$$  | $$
                               |  $$$$$$/
                                \______/
 Hi, I'm Ruby — the gem your task list has been missing.
 What are we polishing today?
____________________________________________________________
```

### Input

```text
contact list
```

### Expected output

```text
____________________________________________________________
 No contacts yet — a fresh, unpolished page.
____________________________________________________________
```

### Input

```text
contact add John Doe /phone 91234567 /email john@example.com /address 123 Street
```

### Expected output

```text
____________________________________________________________
 Saved. I never forget a name:
   John Doe | 91234567 | john@example.com | 123 Street
 That's 1 contact in your circle.
____________________________________________________________
```

### Input

```text
contact add Jane
```

### Expected output

```text
____________________________________________________________
 Saved. I never forget a name:
   Jane
 That's 2 contacts in your circle.
____________________________________________________________
```

### Input

```text
contact list
```

### Expected output

```text
____________________________________________________________
 Your circle, as requested:
 1. John Doe | 91234567 | john@example.com | 123 Street
 2. Jane
____________________________________________________________
```

### Input

```text
contact add John /phone abc
```

### Expected output

```text
____________________________________________________________
 Hold on — What's a phone number without digits? Use a plus sign and at least 3 digits, e.g. +123456789.
____________________________________________________________
```

### Input

```text
contact add John /phone 91234567 /phone 98765432
```

### Expected output

```text
____________________________________________________________
 Hold on — Once is enough — use /phone only once.
____________________________________________________________
```

### Input

```text
contact
```

### Expected output

```text
____________________________________________________________
 Hold on — Here's how contacts work: contact add NAME [/phone PHONE] [/email EMAIL] [/address ADDRESS], contact list, or contact delete INDEX.
____________________________________________________________
```

### Input

```text
contact frobnicate
```

### Expected output

```text
____________________________________________________________
 Hold on — That's not a contact command. Use contact add, contact list, or contact delete.
____________________________________________________________
```

### Input

```text
contact delete 1
```

### Expected output

```text
____________________________________________________________
 Removed from your circle:
   John Doe | 91234567 | john@example.com | 123 Street
 That leaves 1 contact in your circle.
____________________________________________________________
```

### Input

```text
contact delete 5
```

### Expected output

```text
____________________________________________________________
 Hold on — Contact 5? You only have 1. Pick a number from 1 to 1.
____________________________________________________________
```

### Input

```text
contact delete 0
```

### Expected output

```text
____________________________________________________________
 Hold on — Since when is 0 a valid contact number? Give me a positive whole number after contact delete.
____________________________________________________________
```

### Input

```text
bye
```

### Expected output

```text
____________________________________________________________
 Bye. Your tasks are in precious hands.
____________________________________________________________
```

## Exit command validation

Aim: Verify rejected exit variants leave the session usable and only exact lowercase bye exits.

### Startup output

```text
____________________________________________________________
 /$$$$$$$            /$$
 | $$__  $$          | $$
 | $$  \ $$ /$$   /$$| $$$$$$$  /$$   /$$
 | $$$$$$$/| $$  | $$| $$__  $$| $$  | $$
 | $$__  $$| $$  | $$| $$  \ $$| $$  | $$
 | $$  \ $$| $$  | $$| $$  | $$| $$  | $$
 | $$  | $$|  $$$$$$/| $$$$$$$/|  $$$$$$$
 |__/  |__/ \______/ |_______/  \____  $$
                                /$$  | $$
                               |  $$$$$$/
                                \______/
 Hi, I'm Ruby — the gem your task list has been missing.
 What are we polishing today?
____________________________________________________________
```

### Input

```text
BYE
```

### Expected output

```text
____________________________________________________________
 Hold on — That's not in my repertoire. Try a command I know.
____________________________________________________________
```

### Input

```text
 bye
```

### Expected output

```text
____________________________________________________________
 Hold on — Use one space between command parts, with no leading or trailing spaces.
____________________________________________________________
```

### Input

```text
bye 
```

### Expected output

```text
____________________________________________________________
 Hold on — Use one space between command parts, with no leading or trailing spaces.
____________________________________________________________
```

### Input

```text
list
```

### Expected output

```text
____________________________________________________________
 Here's everything on your plate:
____________________________________________________________
```

### Input

```text
bye
```

### Expected output

```text
____________________________________________________________
 Bye. Your tasks are in precious hands.
____________________________________________________________
```
