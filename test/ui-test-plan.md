# Bibi UI Test Plan

Each expected-output block contains ordered output fragments. The test-ui skill checks that each fragment appears in the console output after the previous fragment.

These cases drive the text-based interface (`./gradlew runCli`). They cover the GUI too, because both interfaces hand the same typed line to the same parser and commands; only the last step, printing versus putting the words in a dialog box, differs. What they do not cover is the window itself, which has to be checked by opening it.

## Test 1: Add and list a ToDo

Aim: Confirm that a ToDo is stored without date or time details.

### Input

```text
todo borrow book
list
bye
```

### Expected output

```text
Bibi: Added. Do or do not. There is no try:
[T][ ] borrow book
Your list holds 1 task.
Bibi: Here is everything on your list:
1. [T][ ] borrow book
Bibi: See you.
```

## Test 2: Add and list a deadline

Aim: Confirm that text after /by is retained and shown with a deadline, under the
confirmation wording that belongs to a deadline.

### Input

```text
deadline return book /by 2019-10-15
list
bye
```

### Expected output

```text
Bibi: Added. Remembering it is my job. Doing it is still yours:
[D][ ] return book (by: 15 Oct 2019)
Your list holds 1 task.
1. [D][ ] return book (by: 15 Oct 2019)
```

## Test 3: Add and list an event

Aim: Confirm that text after /from and /to is retained and shown with an event,
under the confirmation wording that belongs to an event.

### Input

```text
event project meeting /from 2019-08-06 1400 /to 2019-08-06 1600
list
bye
```

### Expected output

```text
Bibi: Added. It starts on time. Be there or be square:
[E][ ] project meeting (from: 06 Aug 2019 2:00PM to: 06 Aug 2019 4:00PM)
Your list holds 1 task.
1. [E][ ] project meeting (from: 06 Aug 2019 2:00PM to: 06 Aug 2019 4:00PM)
```

## Test 4: Mark and unmark a task

Aim: Confirm that completion state changes are displayed for a Task subclass,
and that each reply names the task it changed.

### Input

```text
todo join sports club
mark 1
list
unmark 1
list
bye
```

### Expected output

```text
Bibi: Task 1 done. Look at us getting things done.
  [T][X] join sports club
1. [T][X] join sports club
Bibi: We're not making progress, huh? Task 1 is open again.
  [T][ ] join sports club
1. [T][ ] join sports club
```

## Test 5: Reject incomplete deadline and event commands

Aim: Confirm that missing date/time markers produce clear guidance instead of invalid tasks.

### Input

```text
deadline return book
event project meeting /from Mon 2pm
bye
```

### Expected output

```text
Bibi: I need a due date marked with /by. Use deadline <description> /by <time>, for example:
Bibi: I need both /from and /to dates for an event. Use event <description> /from <start> /to <end>, for example:
```

## Test 6: Reject empty, unknown, and incomplete commands

Aim: Confirm that invalid input is reported without ending the program.

### Input

```text

remind me
todo
mark two
bye
```

### Expected output

```text
Bibi: Please enter a command.
Bibi: I don't recognize 'remind'. Check the command word, or type help for examples.
Bibi: A task needs a description. Use todo <description>, for example: todo read book.
Bibi: 'two' is not a task number.
```

## Test 7: Keep tasks between sessions

Aim: Confirm that tasks added in one session are saved and restored in the next.

### Input

```text
todo read book
deadline return book /by 2019-06-06
event project meeting /from 2019-08-06 1400 /to 2019-08-06 1600
mark 1
bye
<<restart>>
list
bye
```

### Expected output

```text
Bibi: Task 1 done. Look at us getting things done.
Bibi: See you.
Bibi: Picked up where we left off: 3 tasks restored.
Bibi: Here is everything on your list:
1. [T][X] read book
2. [D][ ] return book (by: 06 Jun 2019)
3. [E][ ] project meeting (from: 06 Aug 2019 2:00PM to: 06 Aug 2019 4:00PM)
```

## Test 8: Start with no save file

Aim: Confirm that a first run without a save file starts with an empty list.

### Input

```text
list
bye
```

### Expected output

```text
Bibi: Nothing on the list. I'll assume that's good news.
Bibi: See you.
```

## Test 9: Restore a task list written earlier

Aim: Confirm that an existing save file is read in the documented format.

### Saved data

```text
T | 1 | read book
D | 0 | return book | 2019-06-06
E | 0 | project meeting | 2019-08-06 1400 | 2019-08-06 1600
T | 1 | join sports club
```

### Input

```text
list
bye
```

### Expected output

```text
Bibi: Picked up where we left off: 4 tasks restored.
1. [T][X] read book
2. [D][ ] return book (by: 06 Jun 2019)
3. [E][ ] project meeting (from: 06 Aug 2019 2:00PM to: 06 Aug 2019 4:00PM)
4. [T][X] join sports club
```

## Test 10: Recover from a corrupted save file

Aim: Confirm that damaged lines are reported and skipped while valid tasks still load.

### Saved data

```text
T | 1 | read book
this line is not a task
X | 0 | unknown type
D | 2 | return book | June 6th
E | 0 | missing the end time

T | 0 | join sports club
```

### Input

```text
list
bye
```

### Expected output

```text
Bibi: Picked up where we left off: 2 tasks restored.
Bibi: I couldn't read these lines in the save file:
  Line 2: expected at least type, status, and description separated by '|'.
  Line 3: unknown task type 'X'.
  Line 4: status '2' should be 1 (done) or 0 (not done).
  Line 5: expected 5 fields but found 3, for example: E | 0 | project meeting | 2019-08-06 1400 | 2019-08-06 1600
Bibi: I've skipped those lines. They will leave the file the next time your list changes.
Bibi: Here is everything on your list:
1. [T][X] read book
2. [T][ ] join sports club
```

## Test 11: Reject task text containing the save-file separator

Aim: Confirm that a description that would corrupt the save file is refused.

### Input

```text
todo read | book
list
bye
```

### Expected output

```text
Bibi: Task text cannot contain '|' because that character separates the fields in the save file.
Bibi: Nothing on the list. I'll assume that's good news.
```

## Test 12: Understand dates and times in several formats

Aim: Confirm that dates are parsed and shown in the display format, with the
time kept only when one was given.

### Input

```text
deadline return book /by 2/12/2019 1800
deadline submit essay /by 2019-10-15
event camp /from 2019-08-10 /to 2019-08-12
list
bye
```

### Expected output

```text
[D][ ] return book (by: 02 Dec 2019 6:00PM)
[D][ ] submit essay (by: 15 Oct 2019)
[E][ ] camp (from: 10 Aug 2019 to: 12 Aug 2019)
Bibi: Here is everything on your list:
1. [D][ ] return book (by: 02 Dec 2019 6:00PM)
2. [D][ ] submit essay (by: 15 Oct 2019)
3. [E][ ] camp (from: 10 Aug 2019 to: 12 Aug 2019)
```

## Test 13: Reject dates that cannot be understood

Aim: Confirm that unreadable dates and impossible event ranges are refused with
guidance instead of being stored as text.

### Input

```text
deadline return book /by next Tuesday
deadline return book /by 2019-13-45
event camp /from 2019-08-12 /to 2019-08-10
list
bye
```

### Expected output

```text
Bibi: I could not read the date 'next Tuesday'. Use yyyy-MM-dd, d/M/yyyy, d-M-yyyy, d.M.yyyy, d MMM yyyy, d MMMM yyyy or d/M/yy (00-99 means 2000-2099). Add an optional time such as 1800, 18:00, 6 pm or 6:00 pm; for example 15/10/2019 or 2/12/2019 1800.
Bibi: I could not read the date '2019-13-45'.
Bibi: An event cannot end before it starts.
Bibi: Nothing on the list. I'll assume that's good news.
```

## Test 14: Report saved tasks whose dates predate this format

Aim: Confirm that a save file written before dates were understood is reported
rather than silently accepted.

### Saved data

```text
T | 0 | read book
D | 0 | return book | Sunday
```

### Input

```text
list
bye
```

### Expected output

```text
Bibi: Picked up where we left off: 1 task restored.
Bibi: I couldn't read these lines in the save file:
  Line 2: I could not read the date 'Sunday'.
Bibi: Here is everything on your list:
1. [T][ ] read book
```

## Test 15: List what is happening on one date

Aim: Confirm that the on command finds deadlines due that day and events running
that day, keeps each task's number from the full list, and ignores ToDos.

### Input

```text
todo read book
deadline return book /by 2019-08-11
event camp /from 2019-08-10 /to 2019-08-12
event party /from 2019-08-11 1900 /to 2019-08-11 2300
on 2019-08-11
on 2019-12-25
on someday
bye
```

### Expected output

```text
Bibi: Here is what you have on 11 Aug 2019:
2. [D][ ] return book (by: 11 Aug 2019)
3. [E][ ] camp (from: 10 Aug 2019 to: 12 Aug 2019)
4. [E][ ] party (from: 11 Aug 2019 7:00PM to: 11 Aug 2019 11:00PM)
Bibi: Nothing on 25 Dec 2019. Enjoy the quiet.
Bibi: I could not read the date 'someday'.
```

## Test 16: Show the list of commands

Aim: Confirm that help is recognised on its own, lists every command grouped by
what it is for, and points at the examples rather than printing them.

### Input

```text
help
bye
```

### Expected output

```text
Bibi: Usage: <command> [arguments]
  Add
    todo <description>
    deadline <description> /by <time>
    event <description> /from <start> /to <end>
  View
    list
    sort
    find <keyword>
    on <date>
  Update
    mark <number>
    unmark <number>
    remove|delete <number>
    undo
  Session
    help [--examples]
    bye
    hi, hello, hey, thanks
Bibi: Run help --examples for examples, date formats and keyboard shortcuts.
Bibi: See you.
```

## Test 16a: Show the examples behind the help flag

Aim: Confirm that `help --examples` keeps the command list and adds the worked
examples, date formats, numbering notes and keyboard shortcuts after it, and
that any other argument to help is rejected rather than ignored.

### Input

```text
help --examples
help me
bye
```

### Expected output

```text
Bibi: Usage: <command> [arguments]
    todo <description>
    help [--examples]
Bibi: Run help --examples for examples, date formats and keyboard shortcuts.
Bibi: Examples:
  todo read book
  deadline return book /by 21/12/2026 1800
  event study group /from 21/12/2026 1800 /to 21/12/2026 2000
  find book
  on 21/12/2026
  mark 2
Bibi: Dates:
  d/M/yyyy, d-M-yyyy, d.M.yyyy, d MMM yyyy or d MMMM yyyy.
  d/M/yy uses 00-99 for 2000-2099. Numeric dates are always day first.
  Existing yyyy-MM-dd input still works. Examples: 1/2/2026, 21 December 2026, 21/12/26.
  Optional time: 1800, 18:00, 6 pm or 6:00 pm. Display: 21 Dec 2026 6:00PM.
Bibi: Task numbers:
  Numbers come from the full list, including find/on results. Sort and remove/delete change them;
  use list for current numbers before acting on an older reply.
  undo restores the last task change once this session; there is no redo.
Bibi: Keyboard, in the window:
  Enter sends; Up/Down recall commands without sending. Down past the newest restores your draft.
  Rejected commands stay for editing. Select transcript text and use Ctrl+C (Cmd+C on macOS) to copy.
  New replies scroll into view when you're near the bottom; scroll up to keep your reading position.
Bibi: help takes nothing or --examples after it, but I found 'me'.
Bibi: See you.
```

## Test 17: Find tasks by keyword

Aim: Confirm that find matches descriptions case-insensitively, keeps each
task's number from the full list, and reports when nothing matches.

### Input

```text
todo read book
deadline return Book /by 2019-06-06
todo join sports club
mark 1
find book
find sports
find zzz
find
bye
```

### Expected output

```text
Bibi: Here is what matches:
1. [T][X] read book
2. [D][ ] return Book (by: 06 Jun 2019)
Bibi: Here is what matches:
3. [T][ ] join sports club
Bibi: Nothing matches 'zzz'.
Bibi: Use find followed by a keyword, for example: find book
```

## Test 18: Sort tasks by date

Aim: Confirm that sort puts dated tasks in chronological order, places undated
tasks last, and that the new order is saved rather than shown once.

### Input

```text
todo borrow book
deadline submit report /by 2019-12-01
event orientation /from 2019-08-06 1400 /to 2019-08-06 1600
deadline pay fees /by 2019-10-15
sort
<<restart>>
list
bye
```

### Expected output

```text
Bibi: Sorted it, but not your life. Earliest first:
1. [E][ ] orientation (from: 06 Aug 2019 2:00PM to: 06 Aug 2019 4:00PM)
2. [D][ ] pay fees (by: 15 Oct 2019)
3. [D][ ] submit report (by: 01 Dec 2019)
4. [T][ ] borrow book
Bibi: Here is everything on your list:
1. [E][ ] orientation (from: 06 Aug 2019 2:00PM to: 06 Aug 2019 4:00PM)
2. [D][ ] pay fees (by: 15 Oct 2019)
3. [D][ ] submit report (by: 01 Dec 2019)
4. [T][ ] borrow book
Bibi: See you.
```

## Test 19: Sort an empty list

Aim: Confirm that sorting with nothing stored says so instead of showing an
empty list.

### Input

```text
sort
bye
```

### Expected output

```text
Bibi: Nothing to sort yet, your list is empty.
Bibi: See you.
```

## Test 20: Reject a parameter given twice

Aim: Confirm that a repeated /by or /to is reported as such, rather than being
half-read and failing later as an unreadable date.

### Input

```text
deadline submit report /by 2019-10-15 /by 2019-11-11
event camp /from 2019-08-06 /to 2019-08-07 /to 2019-08-08
list
bye
```

### Expected output

```text
Bibi: You used /by 2 times, but it belongs exactly once.
Bibi: You used /to 2 times, but it belongs exactly once.
Bibi: Nothing on the list. I'll assume that's good news.
```

## Test 21: Reject stray text after a command that takes none

Aim: Confirm that extra words are reported rather than silently ignored, so a
user who expected them to filter the result is not misled.

### Input

```text
list extra
sort now
bye
```

### Expected output

```text
Bibi: list does not take anything after it, but I found 'extra'.
Bibi: sort does not take anything after it, but I found 'now'.
Bibi: See you.
```

## Test 22: Refuse to add the same task twice

Aim: Confirm that re-adding an identical task points at the existing one instead
of quietly creating a second copy.

### Input

```text
todo read book
todo    read    book
deadline pay fees /by 2019-10-15
deadline pay fees /by 2019-10-15
list
bye
```

### Expected output

```text
Bibi: Added. Do or do not. There is no try:
Bibi: You already have that one, as task 1: [T][ ] read book.
Bibi: Added. Remembering it is my job. Doing it is still yours:
Bibi: You already have that one, as task 2: [D][ ] pay fees (by: 15 Oct 2019).
Bibi: Here is everything on your list:
1. [T][ ] read book
2. [D][ ] pay fees (by: 15 Oct 2019)
```

## Test 23: Remove a task

Aim: Confirm that remove names the task it took away, since every later task
moves up a number once it is gone, and that the rest are renumbered.

### Input

```text
todo read book
todo join sports club
remove 1
list
remove 5
bye
```

### Expected output

```text
Bibi: Eeeeesh, another one bites the dust. Task 1 is off the list:
  [T][ ] read book
Your list holds 1 task.
Bibi: Here is everything on your list:
1. [T][ ] join sports club
Bibi: That task number does not exist: use a number from 1 to 1.
```

## Test 24: Read parameters typed in capitals

Aim: Confirm that /by, /from and /to are recognized in any case, as the command
words already are.

### Input

```text
DEADLINE return book /BY 2019-10-15
event camp /From 2019-08-10 /TO 2019-08-12
list
bye
```

### Expected output

```text
Bibi: Here is everything on your list:
1. [D][ ] return book (by: 15 Oct 2019)
2. [E][ ] camp (from: 10 Aug 2019 to: 12 Aug 2019)
```

## Test 25: Accept standalone greetings without changing task text

Aim: Confirm that social inputs ignore case and punctuation only when the whole
input is a greeting or thanks, and that ordinary descriptions keep their meaning.

### Input

```text
HI!
hello
Hey?
THANKS.
todo say hello and thanks
hello read book
list
bye
```

### Expected output

```text
Bibi: Hey. I'm here. Type help if you need the rundown.
Bibi: Hey. I'm here. Type help if you need the rundown.
Bibi: Hey. I'm here. Type help if you need the rundown.
Bibi: You're welcome. You handle the doing; I'll handle the remembering.
[T][ ] say hello and thanks
Bibi: I don't recognize 'hello'. Check the command word, or type help for examples.
1. [T][ ] say hello and thanks
```

## Test 26: Accept the additional day-first formats

Aim: Confirm all new date forms, day/month interpretation and two-digit years,
with date-only values still shown without a time.

### Input

```text
deadline slash /by 21/12/2026
deadline short /by 1/2/2026
deadline hyphen /by 21-12-2026
deadline dotted /by 21.12.2026
deadline named /by 21 Dec 2026
deadline full month /by 21 December 2026
deadline short year /by 21/12/26
deadline ambiguous /by 03/04/2026
deadline year zero /by 1/1/00
deadline year ninety nine /by 31/12/99
list
bye
```

### Expected output

```text
1. [D][ ] slash (by: 21 Dec 2026)
2. [D][ ] short (by: 01 Feb 2026)
3. [D][ ] hyphen (by: 21 Dec 2026)
4. [D][ ] dotted (by: 21 Dec 2026)
5. [D][ ] named (by: 21 Dec 2026)
6. [D][ ] full month (by: 21 Dec 2026)
7. [D][ ] short year (by: 21 Dec 2026)
8. [D][ ] ambiguous (by: 03 Apr 2026)
9. [D][ ] year zero (by: 01 Jan 2000)
10. [D][ ] year ninety nine (by: 31 Dec 2099)
```

## Test 27: Accept timed dates and an overnight event

Aim: Confirm optional time forms, capitalization, whitespace, leap days,
midnight/noon, and inclusive date filtering for an event crossing midnight.

### Input

```text
deadline four digits /by 21/12/2026 1800
deadline colon /by 21-12-2026 18:00
deadline hour /by 21.12.2026 6 pm
deadline named /by 21   dEcEmBeR   2026  6:00   PM
deadline midnight /by 29/2/24 12 am
deadline noon /by 29/2/2024 12:00 pm
event night shift /from 21 Dec 2026 11:30 pm /to 22/12/26 12:30 am
list
on 22/12/26
bye
```

### Expected output

```text
1. [D][ ] four digits (by: 21 Dec 2026 6:00PM)
2. [D][ ] colon (by: 21 Dec 2026 6:00PM)
3. [D][ ] hour (by: 21 Dec 2026 6:00PM)
4. [D][ ] named (by: 21 Dec 2026 6:00PM)
5. [D][ ] midnight (by: 29 Feb 2024 12:00AM)
6. [D][ ] noon (by: 29 Feb 2024 12:00PM)
7. [E][ ] night shift (from: 21 Dec 2026 11:30PM to: 22 Dec 2026 12:30AM)
Bibi: Here is what you have on 22 Dec 2026:
7. [E][ ] night shift (from: 21 Dec 2026 11:30PM to: 22 Dec 2026 12:30AM)
```

## Test 28: Reject impossible dates, invalid times, and month-first input

Aim: Confirm that more accepted formats never normalize invalid values or
introduce numeric month-first fallback.

### Input

```text
deadline impossible /by 31/04/2026
deadline leap /by 29/2/2023
deadline century /by 29 February 2100
deadline month first /by 12/21/2026
deadline hour /by 21/12/2026 24:00
deadline minute /by 21 Dec 2026 18:60
deadline am /by 21 Dec 2026 0 am
deadline pm /by 21 Dec 2026 13 pm
event backwards /from 22/12/2026 1 am /to 21/12/2026 11 pm
list
bye
```

### Expected output

```text
Bibi: I could not read the date '31/04/2026'.
Bibi: I could not read the date '29/2/2023'.
Bibi: I could not read the date '29 February 2100'.
Bibi: I could not read the date '12/21/2026'.
Bibi: I could not read the date '21/12/2026 24:00'.
Bibi: I could not read the date '21 Dec 2026 18:60'.
Bibi: I could not read the date '21 Dec 2026 0 am'.
Bibi: I could not read the date '21 Dec 2026 13 pm'.
Bibi: An event cannot end before it starts.
Bibi: Nothing on the list. I'll assume that's good news.
```

## Test 29: Accept compact markers without weakening validation

Aim: Confirm compact date markers, preserved description capitalization and
normalization, duplicate rejection, and repeated or missing marker guidance.

### Input

```text
DEADLINE   Read   Book /BY21/12/2026 6 pm
deadline read book /by 21 December 2026 1800
event Trip /FROM21/12/2026 /TO22/12/2026
deadline repeat /by21/12/2026 /by22/12/2026
event repeat /from21/12/2026 /to22/12/2026 /to23/12/2026
deadline missing /by
deadline /by21/12/2026
event missing /from /to22/12/2026
event missing /from21/12/2026 /to
list
bye
```

### Expected output

```text
[D][ ] Read Book (by: 21 Dec 2026 6:00PM)
Bibi: You already have that one, as task 1: [D][ ] Read Book (by: 21 Dec 2026 6:00PM).
[E][ ] Trip (from: 21 Dec 2026 to: 22 Dec 2026)
Bibi: You used /by 2 times, but it belongs exactly once.
Bibi: You used /to 2 times, but it belongs exactly once.
Bibi: I need a due date after /by. Use deadline <description> /by <time>, for example:
Bibi: A task needs a description. Use deadline <description> /by <time>, for example:
Bibi: I need a date after /from. Use event <description> /from <start> /to <end>, for example:
Bibi: I need a date after /to. Use event <description> /from <start> /to <end>, for example:
1. [D][ ] Read Book (by: 21 Dec 2026 6:00PM)
2. [E][ ] Trip (from: 21 Dec 2026 to: 22 Dec 2026)
```

## Test 30: Keep task targeting after filtering, sorting and removal

Aim: Confirm new date forms leave the full-list numbering convention unchanged.

### Input

```text
todo Read Book
deadline Return Book /by 21/12/26
deadline Pay Fees /by 1.2.2026
find BOOK
mark 2
sort
on 21 December 2026
remove 1
find book
unmark 1
bye
```

### Expected output

```text
1. [T][ ] Read Book
2. [D][ ] Return Book (by: 21 Dec 2026)
Bibi: Task 2 done. Look at us getting things done.
  [D][X] Return Book (by: 21 Dec 2026)
Bibi: Sorted it, but not your life. Earliest first:
1. [D][ ] Pay Fees (by: 01 Feb 2026)
2. [D][X] Return Book (by: 21 Dec 2026)
3. [T][ ] Read Book
Bibi: Here is what you have on 21 Dec 2026:
2. [D][X] Return Book (by: 21 Dec 2026)
Bibi: Eeeeesh, another one bites the dust. Task 1 is off the list:
  [D][ ] Pay Fees (by: 01 Feb 2026)
Your list holds 2 tasks.
1. [D][X] Return Book (by: 21 Dec 2026)
2. [T][ ] Read Book
Bibi: We're not making progress, huh? Task 1 is open again.
  [D][ ] Return Book (by: 21 Dec 2026)
```

## Test 31: Undo removal once and save the restored list

Aim: Confirm undo restores contents, completion, order and numbers, persists the
result, and never supplies a second undo or carries history across sessions.

### Saved data

```text
T | 0 | first
D | 1 | return book | 2026-12-21 1800
T | 0 | last
```

### Input

```text
remove 2
undo
undo
bye
<<restart>>
undo
list
bye
```

### Expected output

```text
Bibi: Eeeeesh, another one bites the dust. Task 2 is off the list:
  [D][X] return book (by: 21 Dec 2026 6:00PM)
Your list holds 2 tasks.
Bibi: Fickle, aren't ya? Last change undone. Your list holds 3 tasks.
1. [T][ ] first
2. [D][X] return book (by: 21 Dec 2026 6:00PM)
3. [T][ ] last
Bibi: There is no change to undo in this session.
Bibi: See you.
Bibi: Picked up where we left off: 3 tasks restored.
Bibi: There is no change to undo in this session.
1. [T][ ] first
2. [D][X] return book (by: 21 Dec 2026 6:00PM)
3. [T][ ] last
```

## Test 32: Undo adding, marking, unmarking and sorting

Aim: Confirm the single step restores each kind of mutation, including the
completion flags and numbers that were visible before sorting.

### Input

```text
todo first
undo
todo second
mark 1
undo
mark 1
unmark 1
undo
deadline pay fees /by 1/2/26
sort
undo
bye
```

### Expected output

```text
Bibi: Fickle, aren't ya? Last change undone. Your list holds 0 tasks.
[T][ ] second
Bibi: Task 1 done. Look at us getting things done.
Bibi: Fickle, aren't ya? Last change undone. Your list holds 1 task.
1. [T][ ] second
Bibi: Task 1 done. Look at us getting things done.
Bibi: We're not making progress, huh? Task 1 is open again.
Bibi: Fickle, aren't ya? Last change undone. Your list holds 1 task.
1. [T][X] second
[D][ ] pay fees (by: 01 Feb 2026)
Bibi: Sorted it, but not your life. Earliest first:
1. [D][ ] pay fees (by: 01 Feb 2026)
2. [T][X] second
Bibi: Fickle, aren't ya? Last change undone. Your list holds 2 tasks.
1. [T][X] second
2. [D][ ] pay fees (by: 01 Feb 2026)
```

## Test 33: Keep undo through rejection, viewing and no-op commands

Aim: Confirm only an actual mutation replaces the single undo step, with
strict argument validation and no redo after it is consumed.

### Input

```text
todo Read Book
todo   read   book
mark 99
list
find BOOK
hello!
sort
unmark 1
undo
list
undo extra
undo
bye
```

### Expected output

```text
Bibi: You already have that one, as task 1: [T][ ] Read Book.
Bibi: That task number does not exist: use a number from 1 to 1.
1. [T][ ] Read Book
Bibi: Here is what matches:
1. [T][ ] Read Book
Bibi: Hey. I'm here. Type help if you need the rundown.
Bibi: Sorted it, but not your life. Earliest first:
1. [T][ ] Read Book
Bibi: Task 1 was never done, so it stays open.
Bibi: Fickle, aren't ya? Last change undone. Your list holds 0 tasks.
Bibi: Nothing on the list. I'll assume that's good news.
Bibi: undo does not take anything after it, but I found 'extra'.
Bibi: There is no change to undo in this session.
```

## Test 34: Ask for the missing argument of a lookup command

Aim: Confirm that `on` and `find` without an argument name the command form and an
example, rather than explaining only what a date or a keyword is.

### Input

```text
deadline return book /by 21/12/2026
on
find
on 21/12/2026
bye
```

### Expected output

```text
Bibi: I need a date to look up. Use on <date>, for example: on 21/12/2026.
Bibi: Use find followed by a keyword, for example: find book
Bibi: Here is what you have on 21 Dec 2026:
1. [D][ ] return book (by: 21 Dec 2026)
```

## Test 35: Report a completion command that changed nothing

Aim: Confirm that marking a task that is already done, or reopening one that was
never done, says so instead of reporting a change that did not happen, and that
the task itself is still shown.

### Input

```text
todo read book
mark 1
mark 1
unmark 1
unmark 1
bye
```

### Expected output

```text
Bibi: Task 1 done. Look at us getting things done.
[T][X] read book
Bibi: Task 1 was already done. Doing it twice won't count twice.
[T][X] read book
Bibi: We're not making progress, huh? Task 1 is open again.
[T][ ] read book
Bibi: Task 1 was never done, so it stays open.
[T][ ] read book
```

## Test 36: Remove a task with the delete alias

Aim: Confirm that `delete` does the same work as `remove`, in any capitalization,
and that when the number is missing or unreadable the reply names the word that
was actually typed rather than the other spelling of the same command.

### Input

```text
todo read book
todo join sports club
delete 1
list
DELETE 1
delete
delete two
bye
```

### Expected output

```text
Bibi: Eeeeesh, another one bites the dust. Task 1 is off the list:
  [T][ ] read book
Your list holds 1 task.
Bibi: Here is everything on your list:
1. [T][ ] join sports club
Bibi: Eeeeesh, another one bites the dust. Task 1 is off the list:
  [T][ ] join sports club
Bibi: Use delete followed by a task number, for example: delete 2
Bibi: 'two' is not a task number. Use delete followed by one number, for example: delete 2
```

## GUI checks

These checks supplement the scripted console cases. Use a fresh save location.

1. Open at the default 450 by 600 size, resize down to the minimum and wider, then
   repeat with enlarged display scaling. Check input/Send visibility, text contrast,
   avatar size, keyboard focus, and wrapping of long commands and errors.
2. Submit with Enter and Send. Confirm focus returns to the input; blank input adds
   nothing. Submit an invalid deadline and edit the retained text to a valid one.
3. Submit several commands. Type an unfinished draft, press Up and Down, and confirm
   the commands are recalled without executing and the draft returns at the newest
   position. Select all input, then press Up: the selected command must not be replaced
   by history. Shift+arrows and copy shortcuts must keep normal text-selection behavior.
4. Select and copy a previous command, task description, and error example. Verify
   text is unchanged and copying does not execute a command or navigate history.
5. Build a long transcript. Near the bottom, a new reply should follow into view;
   while scrolled up, it should preserve the text being read. Repeat after resizing.
   Scroll with the pointer over a command and over a reply; both must scroll the
   outer transcript without a separate scrollbar or clipped text inside a message.
6. Make the save location unwritable in a disposable folder and submit a valid task.
   Confirm the warning states that the change happened in memory, the input clears,
   and a subsequent list contains the task once. A rejected command must instead
   remain editable and leave the tasks unchanged.
7. Submit `undo` through the input after a task change. Confirm the restored list
   appears with its numbers, the input clears, and a second undo remains editable
   with an error. History recall of `undo` must not execute it until Enter is pressed.
