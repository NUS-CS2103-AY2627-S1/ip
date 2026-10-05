# Gatsby UI test plan

The tests below exercise Gatsby through standard input. Each expected-output line must appear in the program output in the listed order. The test runner ignores the banner, separators, and indentation, while still checking the meaningful output lines.

Level-8 date/time tests use input in the format `yyyy-MM-dd HHmm` and display
values in the format `MMM dd yyyy HH:mm:ss`. Saved date/time values use ISO
format, such as `2019-12-02T18:00`.

## Test 1: Create and list all Level-4 task types

Aim: Verify that ToDos, deadlines, and events are instantiated with the correct descriptions and date/time strings.

Input:
```text
todo borrow book
deadline return book /by 2019-12-02 1800
event project meeting /from 2019-12-02 1400 /to 2019-12-02 1600
list
bye
```

Expected output:
```text
[T][ ] borrow book
[D][ ] return book (by: Dec 02 2019 18:00:00)
[E][ ] project meeting (from: Dec 02 2019 14:00:00 to: Dec 02 2019 16:00:00)
```

## Test 2: Preserve multi-word descriptions

Aim: Verify that the description can contain more than two words and is not truncated by command parsing.

Input:
```text
todo borrow a really thick book
deadline submit the final software engineering report /by 2019-12-03 0900
event team project discussion /from 2019-12-03 1400 /to 2019-12-03 1600
list
bye
```

Expected output:
```text
[T][ ] borrow a really thick book
[D][ ] submit the final software engineering report (by: Dec 03 2019 09:00:00)
[E][ ] team project discussion (from: Dec 03 2019 14:00:00 to: Dec 03 2019 16:00:00)
```

## Test 3: Mark and unmark typed tasks

Aim: Verify that inherited mark and unmark behavior works for a deadline and an event.

Input:
```text
todo read book
deadline return book /by 2019-12-02 1800
event project meeting /from 2019-12-02 1400 /to 2019-12-02 1600
mark 2
unmark 2
mark 3
list
bye
```

Expected output:
```text
[D][X] return book (by: Dec 02 2019 18:00:00)
[E][X] project meeting (from: Dec 02 2019 14:00:00 to: Dec 02 2019 16:00:00)
```

## Test 4: Saving to disk does not change the console UI

Aim: Verify that automatically saving the task list after every change (add, mark, delete) is silent, so the normal add/mark/delete/list output is unchanged and no error message about saving appears.

Input:
```text
todo read book
deadline return book /by 2019-06-06 0000
event project meeting /from 2019-08-06 1400 /to 2019-08-06 1600
mark 1
delete 2
list
bye
```

Expected output:
```text
Got it. I've added this task:
[T][ ] read book
Now you have 1 task in the list.
Nice! I've marked this task as done:
[T][X] read book
Noted. I've removed this task:
[D][ ] return book (by: Jun 06 2019 00:00:00)
Now you have 2 tasks in the list.
Here are the tasks in your list:
1. [T][X] read book
2. [E][ ] project meeting (from: Aug 06 2019 14:00:00 to: Aug 06 2019 16:00:00)
```

## Test 5: Start with no save file

Aim: Verify the first-run state, as on a freshly cloned copy of the project: neither the `data` folder nor `data/gatsby.txt` exists. The list must start empty with no error or warning, and the first added task must create the folder and the file. The test runner deletes the whole `data` folder before each case, so every case starts from this state.

Input:
```text
list
todo read book
list
bye
```

Expected output:
```text
Here are the tasks in your list:
There's nothing here yet! Go ahead and add any tasks you'd like! :)
Got it. I've added this task:
[T][ ] read book
Here are the tasks in your list:
1. [T][ ] read book
```

## Test 6: Reject empty and malformed task details

Aim: Verify that commands missing their description, date, or keyword are rejected with a specific message, and that the chatbot keeps running afterwards.

Input:
```text
todo
todo    
deadline return book
deadline /by 2019-12-02 1800
deadline return book /by
event project meeting
event project meeting /from 2019-12-02 1400
list
bye
```

Expected output:
```text
son the description of a todo cannot be empty -_-!
son the description of a todo cannot be empty -_-!
son there's no name or deadline for this deadline -_-!
son this deadline has no description -_-!
son this deadline has no date after /by -_-!
son there's no event name/timing for this event -_-!
son this event has no end time, it's infinite! -_-!
There's nothing here yet! Go ahead and add any tasks you'd like! :)
```

## Test 7: Reject bad task numbers

Aim: Verify that mark, unmark, and delete report a missing number, a non-numeric number, an out-of-range number, and an empty list, each with its own message, without ending the session.

Input:
```text
mark 1
mark
todo read book
mark abc
mark 0
mark 5
unmark
delete
delete 99
list
bye
```

Expected output:
```text
OOPS! Your list is empty, so there's no task 1!
OOPS! We can't be marking nothing as done!
Got it. I've added this task:
OOPS! "abc" isn't a task number! :(
OOPS! There's no task 0! Pick a number from 1 to 1.
OOPS! There's no task 5! Pick a number from 1 to 1.
OOPS! We can't be marking nothing as undone!
OOPS! How do I even delete nothing??
OOPS! There's no task 99! Pick a number from 1 to 1.
1. [T][ ] read book
```

## Test 8: Handle blank input, unknown commands, and repeated marking

Aim: Verify that an unrecognised command is answered helpfully, and that marking an already-done task says so instead of pretending it just changed. (Blank input is covered by manual check C, because the runner drops empty lines from the input block.)

Input:
```text
blah blah
todo read book
mark 1
mark 1
unmark 1
unmark 1
bye
```

Expected output:
```text
Wait I don't recognise that yet :(
I know: todo, deadline, event, list, find, mark, unmark, delete, bye.
Nice! I've marked this task as done:
That one was already done, but sure:
OK, I've marked this task as not done yet:
That one wasn't done yet, but sure:
```

## Test 9: Reject the field separator in descriptions

Aim: Verify that a description containing the save file's field separator is rejected, so a saved line can never become ambiguous.

Input:
```text
todo read book | and nap
list
bye
```

Expected output:
```text
OOPS! Please leave out the "|" character; I use it to separate fields in my save file.
There's nothing here yet! Go ahead and add any tasks you'd like! :)
```

## Test 10: Find tasks by flexible description search

Aim: Verify that `find` is case-insensitive, supports partial and multi-word matching, prioritizes exact word matches, displays original task numbers, searches only descriptions, reports no matches, and rejects an empty query.

Input:
```text
todo notebook shopping
todo buy book
event book club /from 2019-12-03 1400 /to 2019-12-03 1600
todo book
find BOOK
find club book
find 2019
find missing
find b
find
bye
```

Expected output:
```text
Here are the matching tasks in your list (numbers refer to your full task list):
2. [T][ ] buy book
3. [E][ ] book club (from: Dec 03 2019 14:00:00 to: Dec 03 2019 16:00:00)
4. [T][ ] book
1. [T][ ] notebook shopping
Here are the matching tasks in your list (numbers refer to your full task list):
3. [E][ ] book club (from: Dec 03 2019 14:00:00 to: Dec 03 2019 16:00:00)
No matching tasks found :(
No matching tasks found :(
Here are the matching tasks in your list (numbers refer to your full task list):
1. [T][ ] notebook shopping
2. [T][ ] buy book
3. [E][ ] book club (from: Dec 03 2019 14:00:00 to: Dec 03 2019 16:00:00)
4. [T][ ] book
OOPS! How do I even find nothing??
```

## Test 11: Display the command reference

Aim: Verify that users can request help directly with either the `help` command or its `?` alias.

Input:
```text
help
?
bye
```

Expected output:
```text
Here are the commands I know:
todo <description> - add a task
deadline <description> /by <yyyy-MM-dd HHmm> - add a deadline
event <description> /from <yyyy-MM-dd HHmm> /to <yyyy-MM-dd HHmm> - add an event
list - show all tasks
find <keyword> - find matching tasks
mark <number> / unmark <number> - update a task's status
delete <number> - remove a task
help or ? - show this help
bye - exit Gatsby
Here are the commands I know:
```

## Test 12: Reject invalid dates, duplicate parameters, and duplicate tasks

Aim: Verify that malformed dates, impossible event ranges, repeated date parameters, and duplicate task details are rejected while the session remains usable.

Input:
```text
   todo   read book
todo read book
deadline submit report /by 2019-02-30 1800
deadline return book /by 2019-12-02 1800 /by 2019-12-03 1800
event meeting /from 2019-12-02 1600 /to 2019-12-02 1600
list
bye
```

Expected output:
```text
Got it. I've added this task:
OOPS! You already have a task with the same details.
OOPS! Please enter a valid deadline date and time in the format yyyy-MM-dd HHmm (for example, 2019-12-02 1800).
OOPS! The "/by" parameter can only be specified once.
OOPS! An event's end time must be later than its start time.
1. [T][ ] read book
```

# Manual checks

These are not run by the automated runner, because the runner deletes `data/gatsby.txt` before every case to keep cases independent.

### Manual check A: Reload the saved tasks in a new run

Aim: Verify that tasks saved by an earlier run are read back at startup, including done status and date/time fields.

Setup: put this in `data/gatsby.txt`, then start Gatsby and enter `list`.

```text
T | 1 | read book
D | 0 | return book | 2019-06-06T00:00
E | 0 | project meeting | 2019-08-06T14:00 | 2019-08-06T16:00
```

Expected output:
```text
1. [T][X] read book
2. [D][ ] return book (by: Jun 06 2019 00:00:00)
3. [E][ ] project meeting (from: Aug 06 2019 14:00:00 to: Aug 06 2019 16:00:00)
```

### Manual check B: Skip a corrupted line

Aim: Verify that one unreadable line does not discard the rest of the saved list.

Setup: put this in `data/gatsby.txt`, then start Gatsby and enter `list`.

```text
T | 1 | read book
X | this line is broken
D | 0 | return book | 2019-06-06T00:00
```

Expected output:
```text
OOPS! I skipped a line I couldn't read in my save file: X | this line is broken
1. [T][X] read book
2. [D][ ] return book (by: Jun 06 2019 00:00:00)
```

### Manual check C: Blank input

Aim: Verify that pressing Enter on an empty line is answered helpfully instead of being treated as an unknown command. Run Gatsby and press Enter twice, once on an empty line and once after typing only spaces.

Expected output (once per blank line):
```text
You didn't type anything! Try "todo read book" or "list".
```

### Manual check D: The data folder is blocked

Aim: Verify that Gatsby explains itself instead of crashing when it cannot create its data folder.

Setup: from the project root, remove the folder and put a plain file in its place, then start Gatsby and add a task.

```bash
rm -rf data && touch data
```

Expected output:
```text
OOPS! "data" already exists as a file, so I have nowhere to save. Rename or remove it and I'll save again.
Got it. I've added this task:
```

The task is still added to the in-memory list, so the session stays usable; only saving is skipped. Remember to `rm data` afterwards.
