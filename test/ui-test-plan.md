# saLLMan UI test plan

Text-based tests for the chatbot's console interface. Each test case feeds its
`Input:` lines to the program on stdin and compares the whole console output
against `Expected output:` (with the shared session preamble prepended).

Run all cases from the repository root:

```bash
python test/run-ui-tests.py
```

Run a single case:

```bash
python test/run-ui-tests.py --filter "TC3:"
```

Testing stops at the first failure and reports the expected and actual output.

The app saves its task list, so the runner passes a scratch data file
(`_temp/ui-test-data.txt`) and deletes it before each case. Cases therefore
start with an empty list unless they declare a `Setup input:` session, which
runs first against the same file to seed it.

## Session preamble

Every session opens with the banner and greeting, so this block is prepended to
every expected output rather than repeated in each test case.

```text
              _      _      __  __
 ___    __ _ | |    | |    |  \/  |  __ _  _ __
/ __|  / _` || |    | |    | |\/| | / _` || '_ \
\__ \ | (_| || |___ | |___ | |  | || (_| || | | |
|___/  \__,_||_____||_____||_|  |_| \__,_||_| |_|
    ____________________________________________________________
     Hello! I'm saLLMan, your Large Language (task) Manager.
     How can I assist you with your tasks today?
    ____________________________________________________________

```

## Test cases

### TC1: Greeting and exit on an empty list

**Aim:** Verify the chatbot starts, reports an empty list without crashing, and
exits on `bye`.

**Input:**

```text
list
bye
```

**Expected output:**

```text
    ____________________________________________________________
     Great question! Your list is currently empty.
     Would you like me to help you add a task? Try: todo read book
    ____________________________________________________________

    ____________________________________________________________
     Thank you for chatting with me! I hope this was helpful. Goodbye!
    ____________________________________________________________
```

### TC2: Add each task type and list them

**Aim:** Verify todo, deadline and event are each stored with the correct type
icon and date fields, and that the running total is reported.

**Input:**

```text
todo read book
deadline return book /by 2019-10-15
event project meeting /from 2019-08-06 /to 2019-08-07
list
bye
```

**Expected output:**

```text
    ____________________________________________________________
     Certainly! What a wonderful task. I've added it:
       [T][ ] read book
     You now have 1 task in your list.
    ____________________________________________________________

    ____________________________________________________________
     Certainly! What a wonderful task. I've added it:
       [D][ ] return book (by: Oct 15 2019)
     You now have 2 tasks in your list.
    ____________________________________________________________

    ____________________________________________________________
     Certainly! What a wonderful task. I've added it:
       [E][ ] project meeting (from: Aug 06 2019 to: Aug 07 2019)
     You now have 3 tasks in your list.
    ____________________________________________________________

    ____________________________________________________________
     Great question! Here are the tasks in your list:
     1.[T][ ] read book
     2.[D][ ] return book (by: Oct 15 2019)
     3.[E][ ] project meeting (from: Aug 06 2019 to: Aug 07 2019)
    ____________________________________________________________

    ____________________________________________________________
     Thank you for chatting with me! I hope this was helpful. Goodbye!
    ____________________________________________________________
```

### TC3: Mark and unmark round trip

**Aim:** Verify `mark` sets the status icon to `X`, `unmark` clears it, and that
`list` reflects the change both times. Interleaving `list` after each command
checks the stored state, not just the confirmation message.

**Input:**

```text
todo read book
mark 1
list
unmark 1
list
bye
```

**Expected output:**

```text
    ____________________________________________________________
     Certainly! What a wonderful task. I've added it:
       [T][ ] read book
     You now have 1 task in your list.
    ____________________________________________________________

    ____________________________________________________________
     Great job! I've marked this task as done:
       [T][X] read book
    ____________________________________________________________

    ____________________________________________________________
     Great question! Here are the tasks in your list:
     1.[T][X] read book
    ____________________________________________________________

    ____________________________________________________________
     No problem! I've marked this task as not done yet:
       [T][ ] read book
    ____________________________________________________________

    ____________________________________________________________
     Great question! Here are the tasks in your list:
     1.[T][ ] read book
    ____________________________________________________________

    ____________________________________________________________
     Thank you for chatting with me! I hope this was helpful. Goodbye!
    ____________________________________________________________
```

### TC4: Text that is not a date is refused

**Aim:** Verify dates are now parsed rather than stored as free text, so
anything that is not a real date is rejected with the expected format quoted
back. Previously this case asserted the opposite: free text was accepted
verbatim. A day name is included because it reads like a date to a human but
is not one, and an impossible calendar date is included because it has the
right shape yet cannot exist. That last one names what is wrong with the date,
since repeating the format back would not help someone who already used it.

**Input:**

```text
deadline do homework /by no idea :-p
deadline do homework /by Sunday
deadline do homework /by 2019-02-30
deadline do homework /by 2019-10-15
list
bye
```

**Expected output:**

```text
    ____________________________________________________________
     I'm sorry, but I couldn't read "no idea :-p" as a date.
     Use yyyy-mm-dd, e.g. 2019-10-15.
    ____________________________________________________________

    ____________________________________________________________
     I'm sorry, but I couldn't read "Sunday" as a date.
     Use yyyy-mm-dd, e.g. 2019-10-15.
    ____________________________________________________________

    ____________________________________________________________
     As a large language model, I must point out there is no such date as 2019-02-30.
     February 2019 has 28 days.
    ____________________________________________________________

    ____________________________________________________________
     Certainly! What a wonderful task. I've added it:
       [D][ ] do homework (by: Oct 15 2019)
     You now have 1 task in your list.
    ____________________________________________________________

    ____________________________________________________________
     Great question! Here are the tasks in your list:
     1.[D][ ] do homework (by: Oct 15 2019)
    ____________________________________________________________

    ____________________________________________________________
     Thank you for chatting with me! I hope this was helpful. Goodbye!
    ____________________________________________________________
```

### TC5: Unrecognised input is rejected, not stored

**Aim:** Verify an unknown command is refused, quoted back so the user can see
what was not understood, and crucially that it is not silently added to the
list. The trailing `list` is what proves the internal state stayed clean.

**Input:**

```text
blah
list
bye
```

**Expected output:**

```text
    ____________________________________________________________
     I'm sorry, but I don't know what "blah" means.
     I understand: todo, deadline, event, list, find, mark, unmark, delete, tag, untag, undo, sort, on, bye.
    ____________________________________________________________

    ____________________________________________________________
     Great question! Your list is currently empty.
     Would you like me to help you add a task? Try: todo read book
    ____________________________________________________________

    ____________________________________________________________
     Thank you for chatting with me! I hope this was helpful. Goodbye!
    ____________________________________________________________
```

### TC6: A todo with no description is refused

**Aim:** Verify a bare `todo` is reported as a missing description rather than
as an unknown command, and that nothing is added. The following valid `todo`
checks that the rejected input left the list usable.

**Input:**

```text
todo
todo read book
list
bye
```

**Expected output:**

```text
    ____________________________________________________________
     I'd love to help, but a todo needs a description.
     Try: todo read book
    ____________________________________________________________

    ____________________________________________________________
     Certainly! What a wonderful task. I've added it:
       [T][ ] read book
     You now have 1 task in your list.
    ____________________________________________________________

    ____________________________________________________________
     Great question! Here are the tasks in your list:
     1.[T][ ] read book
    ____________________________________________________________

    ____________________________________________________________
     Thank you for chatting with me! I hope this was helpful. Goodbye!
    ____________________________________________________________
```

### TC7: A todo whose description is only spaces is refused

**Aim:** Verify the description is checked after trimming, so whitespace alone
does not create a task with a blank description.

**Input:**

```text
todo    
list
bye
```

**Expected output:**

```text
    ____________________________________________________________
     I'd love to help, but a todo needs a description.
     Try: todo read book
    ____________________________________________________________

    ____________________________________________________________
     Great question! Your list is currently empty.
     Would you like me to help you add a task? Try: todo read book
    ____________________________________________________________

    ____________________________________________________________
     Thank you for chatting with me! I hope this was helpful. Goodbye!
    ____________________________________________________________
```

### TC8: Bad task numbers are refused

**Aim:** Verify every way of getting a task number wrong is reported rather
than crashing, and that each is distinguished: out of range above and below,
non-numeric, and missing. The missing-number message must name the command the
user actually typed. The closing `list` confirms the real task survived all of
it untouched.

**Input:**

```text
todo read book
mark 99
mark 0
mark abc
mark
unmark
list
bye
```

**Expected output:**

```text
    ____________________________________________________________
     Certainly! What a wonderful task. I've added it:
       [T][ ] read book
     You now have 1 task in your list.
    ____________________________________________________________

    ____________________________________________________________
     As a large language model, I must point out that there is no task 99.
     You only have task 1.
    ____________________________________________________________

    ____________________________________________________________
     As a large language model, I must point out that there is no task 0.
     You only have task 1.
    ____________________________________________________________

    ____________________________________________________________
     I'm sorry, but "abc" doesn't look like a task number.
     Try: mark 2
    ____________________________________________________________

    ____________________________________________________________
     I'd love to help, but mark needs a task number.
     Try: mark 2
    ____________________________________________________________

    ____________________________________________________________
     I'd love to help, but unmark needs a task number.
     Try: unmark 2
    ____________________________________________________________

    ____________________________________________________________
     Great question! Here are the tasks in your list:
     1.[T][ ] read book
    ____________________________________________________________

    ____________________________________________________________
     Thank you for chatting with me! I hope this was helpful. Goodbye!
    ____________________________________________________________
```

### TC9: The valid range is reported when several tasks exist

**Aim:** Verify the out-of-range message switches from the singular
"You only have task 1." to a range once there is more than one task.

**Input:**

```text
todo read book
todo buy bread
mark 99
bye
```

**Expected output:**

```text
    ____________________________________________________________
     Certainly! What a wonderful task. I've added it:
       [T][ ] read book
     You now have 1 task in your list.
    ____________________________________________________________

    ____________________________________________________________
     Certainly! What a wonderful task. I've added it:
       [T][ ] buy bread
     You now have 2 tasks in your list.
    ____________________________________________________________

    ____________________________________________________________
     As a large language model, I must point out that there is no task 99.
     Pick a number from 1 to 2.
    ____________________________________________________________

    ____________________________________________________________
     Thank you for chatting with me! I hope this was helpful. Goodbye!
    ____________________________________________________________
```

### TC10: Marking on an empty list is refused

**Aim:** Verify `mark 1` on an empty list is refused. Task 1 is a valid number
in general, so this checks the range is compared against the current task
count rather than the array size. The empty list gets its own message, since
telling the user to "pick a number from 1 to 0" would be nonsense.

**Input:**

```text
mark 1
bye
```

**Expected output:**

```text
    ____________________________________________________________
     As a large language model, I must point out that your list is empty, so there is no task 1.
     Try: todo read book
    ____________________________________________________________

    ____________________________________________________________
     Thank you for chatting with me! I hope this was helpful. Goodbye!
    ____________________________________________________________
```

### TC11: Incomplete deadlines are refused

**Aim:** Verify a deadline missing its `/by`, its description, or its date is
refused with a *different* message in each case, so the user is told which
part is missing rather than being handed a generic complaint. A valid deadline
afterwards confirms the rejections left the list usable.

**Input:**

```text
deadline submit report
deadline /by 2019-10-15
deadline report /by
deadline return book /by 2019-10-15
list
bye
```

**Expected output:**

```text
    ____________________________________________________________
     I'm sorry, but I couldn't find a /by in that deadline.
     Try: deadline return book /by 2019-10-15
    ____________________________________________________________

    ____________________________________________________________
     I'd love to help, but that deadline has no description before the /by.
     Try: deadline return book /by 2019-10-15
    ____________________________________________________________

    ____________________________________________________________
     I'd love to help, but that deadline has no due date after the /by.
     Try: deadline return book /by 2019-10-15
    ____________________________________________________________

    ____________________________________________________________
     Certainly! What a wonderful task. I've added it:
       [D][ ] return book (by: Oct 15 2019)
     You now have 1 task in your list.
    ____________________________________________________________

    ____________________________________________________________
     Great question! Here are the tasks in your list:
     1.[D][ ] return book (by: Oct 15 2019)
    ____________________________________________________________

    ____________________________________________________________
     Thank you for chatting with me! I hope this was helpful. Goodbye!
    ____________________________________________________________
```

### TC12: Incomplete events are refused

**Aim:** Verify each of the five ways an event can be incomplete gets its own
message: no `/from`, no `/to`, no description, no start time, no end time. A
valid event afterwards confirms the rejections left the list usable.

**Input:**

```text
event meeting
event meeting /from 2019-10-15
event /from 2019-10-15 /to 2019-10-16
event meeting /from /to 2019-10-16
event meeting /from 2019-10-15 /to
event project meeting /from 2019-10-15 /to 2019-10-16
list
bye
```

**Expected output:**

```text
    ____________________________________________________________
     I'm sorry, but I couldn't find a /from in that event.
     Try: event project meeting /from 2019-10-15 /to 2019-10-16
    ____________________________________________________________

    ____________________________________________________________
     I'm sorry, but I couldn't find a /to in that event.
     Try: event project meeting /from 2019-10-15 /to 2019-10-16
    ____________________________________________________________

    ____________________________________________________________
     I'd love to help, but that event has no description before the /from.
     Try: event project meeting /from 2019-10-15 /to 2019-10-16
    ____________________________________________________________

    ____________________________________________________________
     I'd love to help, but that event has no start time after the /from.
     Try: event project meeting /from 2019-10-15 /to 2019-10-16
    ____________________________________________________________

    ____________________________________________________________
     I'd love to help, but that event has no end time after the /to.
     Try: event project meeting /from 2019-10-15 /to 2019-10-16
    ____________________________________________________________

    ____________________________________________________________
     Certainly! What a wonderful task. I've added it:
       [E][ ] project meeting (from: Oct 15 2019 to: Oct 16 2019)
     You now have 1 task in your list.
    ____________________________________________________________

    ____________________________________________________________
     Great question! Here are the tasks in your list:
     1.[E][ ] project meeting (from: Oct 15 2019 to: Oct 16 2019)
    ____________________________________________________________

    ____________________________________________________________
     Thank you for chatting with me! I hope this was helpful. Goodbye!
    ____________________________________________________________
```

### TC13: A blank line is ignored

**Aim:** Verify pressing Enter on its own produces no reply at all, rather than
an unknown-command complaint.

**Input:**

```text

todo read book
bye
```

**Expected output:**

```text
    ____________________________________________________________
     Certainly! What a wonderful task. I've added it:
       [T][ ] read book
     You now have 1 task in your list.
    ____________________________________________________________

    ____________________________________________________________
     Thank you for chatting with me! I hope this was helpful. Goodbye!
    ____________________________________________________________
```

### TC14: Deleting from the middle renumbers the rest

**Aim:** Verify `delete` removes the right task, reports the new total, and
that the tasks after it move up. Marking task 3 afterwards is the real check:
if the deleted slot were left behind, task 3 would still be the old task 4 and
the wrong item would be marked.

**Input:**

```text
todo read book
todo return book
todo buy bread
todo join club
delete 2
list
mark 3
list
bye
```

**Expected output:**

```text
    ____________________________________________________________
     Certainly! What a wonderful task. I've added it:
       [T][ ] read book
     You now have 1 task in your list.
    ____________________________________________________________

    ____________________________________________________________
     Certainly! What a wonderful task. I've added it:
       [T][ ] return book
     You now have 2 tasks in your list.
    ____________________________________________________________

    ____________________________________________________________
     Certainly! What a wonderful task. I've added it:
       [T][ ] buy bread
     You now have 3 tasks in your list.
    ____________________________________________________________

    ____________________________________________________________
     Certainly! What a wonderful task. I've added it:
       [T][ ] join club
     You now have 4 tasks in your list.
    ____________________________________________________________

    ____________________________________________________________
     Of course! I've removed this task:
       [T][ ] return book
     You now have 3 tasks in your list.
    ____________________________________________________________

    ____________________________________________________________
     Great question! Here are the tasks in your list:
     1.[T][ ] read book
     2.[T][ ] buy bread
     3.[T][ ] join club
    ____________________________________________________________

    ____________________________________________________________
     Great job! I've marked this task as done:
       [T][X] join club
    ____________________________________________________________

    ____________________________________________________________
     Great question! Here are the tasks in your list:
     1.[T][ ] read book
     2.[T][ ] buy bread
     3.[T][X] join club
    ____________________________________________________________

    ____________________________________________________________
     Thank you for chatting with me! I hope this was helpful. Goodbye!
    ____________________________________________________________
```

### TC15: Deleting the last task, then an out-of-range delete

**Aim:** Verify deleting the final task leaves an empty list rather than
running off the end, and that the now-stale task number is refused afterwards.

**Input:**

```text
todo read book
delete 1
delete 1
list
bye
```

**Expected output:**

```text
    ____________________________________________________________
     Certainly! What a wonderful task. I've added it:
       [T][ ] read book
     You now have 1 task in your list.
    ____________________________________________________________

    ____________________________________________________________
     Of course! I've removed this task:
       [T][ ] read book
     You now have 0 tasks in your list.
    ____________________________________________________________

    ____________________________________________________________
     As a large language model, I must point out that your list is empty, so there is no task 1.
     Try: todo read book
    ____________________________________________________________

    ____________________________________________________________
     Great question! Your list is currently empty.
     Would you like me to help you add a task? Try: todo read book
    ____________________________________________________________

    ____________________________________________________________
     Thank you for chatting with me! I hope this was helpful. Goodbye!
    ____________________________________________________________
```

### TC16: A bad delete number is refused

**Aim:** Verify `delete` reuses the same task-number validation as `mark`, and
that its error hints name `delete` rather than another command.

**Input:**

```text
todo read book
delete 99
delete abc
delete
list
bye
```

**Expected output:**

```text
    ____________________________________________________________
     Certainly! What a wonderful task. I've added it:
       [T][ ] read book
     You now have 1 task in your list.
    ____________________________________________________________

    ____________________________________________________________
     As a large language model, I must point out that there is no task 99.
     You only have task 1.
    ____________________________________________________________

    ____________________________________________________________
     I'm sorry, but "abc" doesn't look like a task number.
     Try: delete 2
    ____________________________________________________________

    ____________________________________________________________
     I'd love to help, but delete needs a task number.
     Try: delete 2
    ____________________________________________________________

    ____________________________________________________________
     Great question! Here are the tasks in your list:
     1.[T][ ] read book
    ____________________________________________________________

    ____________________________________________________________
     Thank you for chatting with me! I hope this was helpful. Goodbye!
    ____________________________________________________________
```

## Not covered by this plan

- **A very large task list.** The list is held in an `ArrayList`, so there is
  no fixed limit to bump into. Adding 150 tasks was checked by hand; it is left
  out of the plan because a 150-line test case would dominate the file. The
  earlier 100-task limit, and its "your list is full" error, no longer exist.

### TC17: Tasks survive a restart

**Aim:** Verify the list is written to disk when it changes and read back on
startup. The setup session below runs first against the same data file; this
case then starts a *fresh process* and lists what was saved. Marking is
included so the done status is checked too, not just the descriptions.

**Setup input:**

```text
todo read book
deadline return book /by 2019-10-15
event project meeting /from 2019-08-06 /to 2019-08-07
mark 1
bye
```

**Input:**

```text
list
bye
```

**Expected output:**

```text
    ____________________________________________________________
     Great question! Here are the tasks in your list:
     1.[T][X] read book
     2.[D][ ] return book (by: Oct 15 2019)
     3.[E][ ] project meeting (from: Aug 06 2019 to: Aug 07 2019)
    ____________________________________________________________

    ____________________________________________________________
     Thank you for chatting with me! I hope this was helpful. Goodbye!
    ____________________________________________________________
```

### TC18: A deletion is persisted too

**Aim:** Verify saving happens on every change, not only on add. If save were
wired only into the add commands, the deleted task would reappear here.

**Setup input:**

```text
todo read book
todo return book
delete 1
bye
```

**Input:**

```text
list
bye
```

**Expected output:**

```text
    ____________________________________________________________
     Great question! Here are the tasks in your list:
     1.[T][ ] return book
    ____________________________________________________________

    ____________________________________________________________
     Thank you for chatting with me! I hope this was helpful. Goodbye!
    ____________________________________________________________
```

### TC19: A corrupted save file is repaired, not fatal

**Aim:** Verify a damaged data file neither crashes the chatbot nor discards
the tasks that are still readable. Each bad line is named with its line number
and the specific fault, so a hand-edited file can be corrected. The six bad
lines cover every rejection the loader makes: too few fields, unknown type,
invalid done flag, empty description, deadline with no date, event missing an
end, and dates that are not dates. Line 8 is blank and must be skipped
silently rather than reported.

**Setup data file:**

```text
T | 1 | read book
GARBAGE LINE
D | 0 | return book | 2019-10-15
X | 0 | alien task
T | 7 | bad flag
T | 0 | 
D | 0 | no due date

E | 0 | meeting | Mon
D | 0 | broken date | not-a-date
E | 0 | broken end | 2019-10-15 | Tuesday
```

**Input:**

```text
list
bye
```

**Expected output:**

```text
    ____________________________________________________________
     I noticed 8 unreadable lines in your saved data, so I skipped them:
     line 2: expected at least 3 fields, found 1
     line 4: unknown task type "X"
     line 5: the done flag should be 0 or 1, found "7"
     line 6: the description is empty
     line 7: a deadline needs a due date
     line 9: an event needs both a start and an end
     line 10: the due date "not-a-date" is not a date
     line 11: the end date "Tuesday" is not a date
     Everything else loaded perfectly! I'll drop the unreadable lines
     the next time your list changes.
    ____________________________________________________________

    ____________________________________________________________
     Great question! Here are the tasks in your list:
     1.[T][X] read book
     2.[D][ ] return book (by: Oct 15 2019)
    ____________________________________________________________

    ____________________________________________________________
     Thank you for chatting with me! I hope this was helpful. Goodbye!
    ____________________________________________________________
```

### TC20: A clean save file produces no warning

**Aim:** Verify the skipped-line warning appears only when something was
actually wrong. A loader that reported on every startup would train the user
to ignore it.

**Setup data file:**

```text
T | 0 | read book
```

**Input:**

```text
list
bye
```

**Expected output:**

```text
    ____________________________________________________________
     Great question! Here are the tasks in your list:
     1.[T][ ] read book
    ____________________________________________________________

    ____________________________________________________________
     Thank you for chatting with me! I hope this was helpful. Goodbye!
    ____________________________________________________________
```

### TC21: Listing what falls on a given date

**Aim:** Verify `on` selects tasks by date. The conference spans 14th to 17th
and must match both the 15th and the 16th, which is the real check: an event
occupies every day between its start and end, not just the two end points. The
todo carries no date and must never match, and a date with nothing on it must
say so rather than print an empty heading.

**Input:**

```text
todo read book
deadline return book /by 2019-10-15
deadline pay bill /by 2019-10-16
event conference /from 2019-10-14 /to 2019-10-17
on 2019-10-15
on 2019-10-16
on 2019-12-25
bye
```

**Expected output:**

```text
    ____________________________________________________________
     Certainly! What a wonderful task. I've added it:
       [T][ ] read book
     You now have 1 task in your list.
    ____________________________________________________________

    ____________________________________________________________
     Certainly! What a wonderful task. I've added it:
       [D][ ] return book (by: Oct 15 2019)
     You now have 2 tasks in your list.
    ____________________________________________________________

    ____________________________________________________________
     Certainly! What a wonderful task. I've added it:
       [D][ ] pay bill (by: Oct 16 2019)
     You now have 3 tasks in your list.
    ____________________________________________________________

    ____________________________________________________________
     Certainly! What a wonderful task. I've added it:
       [E][ ] conference (from: Oct 14 2019 to: Oct 17 2019)
     You now have 4 tasks in your list.
    ____________________________________________________________

    ____________________________________________________________
     Here is everything you have on Oct 15 2019:
     2.[D][ ] return book (by: Oct 15 2019)
     4.[E][ ] conference (from: Oct 14 2019 to: Oct 17 2019)
    ____________________________________________________________

    ____________________________________________________________
     Here is everything you have on Oct 16 2019:
     3.[D][ ] pay bill (by: Oct 16 2019)
     4.[E][ ] conference (from: Oct 14 2019 to: Oct 17 2019)
    ____________________________________________________________

    ____________________________________________________________
     Great news! You have nothing on Dec 25 2019.
    ____________________________________________________________

    ____________________________________________________________
     Thank you for chatting with me! I hope this was helpful. Goodbye!
    ____________________________________________________________
```

### TC22: The on command validates its date

**Aim:** Verify `on` refuses a missing or unreadable date with the same
wording used elsewhere, rather than silently listing nothing.

**Input:**

```text
on
on Sunday
bye
```

**Expected output:**

```text
    ____________________________________________________________
     I'd love to help, but on needs a date.
     Try: on 2019-10-15
    ____________________________________________________________

    ____________________________________________________________
     I'm sorry, but I couldn't read "Sunday" as a date.
     Use yyyy-mm-dd, e.g. 2019-10-15.
    ____________________________________________________________

    ____________________________________________________________
     Thank you for chatting with me! I hope this was helpful. Goodbye!
    ____________________________________________________________
```

### TC23: Descriptions containing the field separator survive a restart

**Aim:** Regression test. The data file separates fields with `" | "`, so a
description containing that sequence used to break the round trip: a todo was
silently truncated at the first occurrence, and a deadline or event was
rejected outright on the next load. The description is the only free-text
field, so the trailing dates must be split off from the right rather than the
line being split left to right.

The event description deliberately contains something that looks like a date,
to confirm the split is positional and not a guess at which field looks most
date-like.

**Setup input:**

```text
todo read | book
deadline pay a | b /by 2019-10-15
event a | 2019-01-01 | b /from 2019-10-14 /to 2019-10-17
bye
```

**Input:**

```text
list
bye
```

**Expected output:**

```text
    ____________________________________________________________
     Great question! Here are the tasks in your list:
     1.[T][ ] read | book
     2.[D][ ] pay a | b (by: Oct 15 2019)
     3.[E][ ] a | 2019-01-01 | b (from: Oct 14 2019 to: Oct 17 2019)
    ____________________________________________________________

    ____________________________________________________________
     Thank you for chatting with me! I hope this was helpful. Goodbye!
    ____________________________________________________________
```

### TC24: An event ending before it starts is refused

**Aim:** Verify an event whose `/to` precedes its `/from` is rejected. Such an
event spans no days at all, so `on` could never find it and `list` would show
a nonsensical range. A same-day event must still be accepted, since that is
the boundary case and is legitimate.

**Input:**

```text
event oops /from 2019-10-20 /to 2019-10-10
event standup /from 2019-10-15 /to 2019-10-15
on 2019-10-15
bye
```

**Expected output:**

```text
    ____________________________________________________________
     As a large language model, I can't schedule an event that ends before it starts.
     Check the order of the /from and /to dates.
    ____________________________________________________________

    ____________________________________________________________
     Certainly! What a wonderful task. I've added it:
       [E][ ] standup (from: Oct 15 2019 to: Oct 15 2019)
     You now have 1 task in your list.
    ____________________________________________________________

    ____________________________________________________________
     Here is everything you have on Oct 15 2019:
     1.[E][ ] standup (from: Oct 15 2019 to: Oct 15 2019)
    ____________________________________________________________

    ____________________________________________________________
     Thank you for chatting with me! I hope this was helpful. Goodbye!
    ____________________________________________________________
```

### TC25: Finding tasks by keyword

**Aim:** Verify `find` shows only the tasks whose description contains the
keyword, each numbered by its position in the full list, since that is the
number `mark`, `delete` and `tag` act on. The search is case-insensitive, and `bread` must not match
`read` even though one contains the other in the opposite direction.

**Input:**

```text
todo read book
deadline return book /by 2019-10-15
todo buy bread
find book
find BOOK
find bread
find zzz
bye
```

**Expected output:**

```text
    ____________________________________________________________
     Certainly! What a wonderful task. I've added it:
       [T][ ] read book
     You now have 1 task in your list.
    ____________________________________________________________

    ____________________________________________________________
     Certainly! What a wonderful task. I've added it:
       [D][ ] return book (by: Oct 15 2019)
     You now have 2 tasks in your list.
    ____________________________________________________________

    ____________________________________________________________
     Certainly! What a wonderful task. I've added it:
       [T][ ] buy bread
     You now have 3 tasks in your list.
    ____________________________________________________________

    ____________________________________________________________
     I found some tasks that match "book":
     1.[T][ ] read book
     2.[D][ ] return book (by: Oct 15 2019)
    ____________________________________________________________

    ____________________________________________________________
     I found some tasks that match "BOOK":
     1.[T][ ] read book
     2.[D][ ] return book (by: Oct 15 2019)
    ____________________________________________________________

    ____________________________________________________________
     I found some tasks that match "bread":
     3.[T][ ] buy bread
    ____________________________________________________________

    ____________________________________________________________
     I searched thoroughly, but no tasks match "zzz".
    ____________________________________________________________

    ____________________________________________________________
     Thank you for chatting with me! I hope this was helpful. Goodbye!
    ____________________________________________________________
```

### TC26: find with no keyword is refused

**Aim:** Verify a bare `find` asks for something to search for, rather than
listing every task or nothing at all.

**Input:**

```text
todo read book
find
bye
```

**Expected output:**

```text
    ____________________________________________________________
     Certainly! What a wonderful task. I've added it:
       [T][ ] read book
     You now have 1 task in your list.
    ____________________________________________________________

    ____________________________________________________________
     I'd love to help, but find needs something to search for.
     Try: find book
    ____________________________________________________________

    ____________________________________________________________
     Thank you for chatting with me! I hope this was helpful. Goodbye!
    ____________________________________________________________
```

### TC27: Tagging tasks

**Aim:** Verify `tag` attaches labels to a task and that the tags are shown
after any dates the task carries, rather than between the description and them.
A leading `#` is optional when typing a tag.

**Input:**

```text
todo read book
deadline return book /by 2019-10-15
tag 1 fun books
tag 2 #urgent
list
bye
```

**Expected output:**

```text
    ____________________________________________________________
     Certainly! What a wonderful task. I've added it:
       [T][ ] read book
     You now have 1 task in your list.
    ____________________________________________________________

    ____________________________________________________________
     Certainly! What a wonderful task. I've added it:
       [D][ ] return book (by: Oct 15 2019)
     You now have 2 tasks in your list.
    ____________________________________________________________

    ____________________________________________________________
     Absolutely! I've tagged this task:
       [T][ ] read book #fun #books
    ____________________________________________________________

    ____________________________________________________________
     Absolutely! I've tagged this task:
       [D][ ] return book (by: Oct 15 2019) #urgent
    ____________________________________________________________

    ____________________________________________________________
     Great question! Here are the tasks in your list:
     1.[T][ ] read book #fun #books
     2.[D][ ] return book (by: Oct 15 2019) #urgent
    ____________________________________________________________

    ____________________________________________________________
     Thank you for chatting with me! I hope this was helpful. Goodbye!
    ____________________________________________________________
```

### TC28: Untagging, including a tag that is not there

**Aim:** Verify `untag` removes a tag whatever case it is typed in, and that
removing a tag the task does not have is reported rather than silently doing
nothing.

**Input:**

```text
todo read book
tag 1 fun
untag 1 FUN
untag 1 fun
bye
```

**Expected output:**

```text
    ____________________________________________________________
     Certainly! What a wonderful task. I've added it:
       [T][ ] read book
     You now have 1 task in your list.
    ____________________________________________________________

    ____________________________________________________________
     Absolutely! I've tagged this task:
       [T][ ] read book #fun
    ____________________________________________________________

    ____________________________________________________________
     Sure thing! I've removed those tags:
       [T][ ] read book
    ____________________________________________________________

    ____________________________________________________________
     It looks like that task has none of the tags you named:
       [T][ ] read book
    ____________________________________________________________

    ____________________________________________________________
     Thank you for chatting with me! I hope this was helpful. Goodbye!
    ____________________________________________________________
```

### TC29: Tag commands with bad arguments are refused

**Aim:** Verify each way of misusing `tag` and `untag` is named specifically:
a task number outside the list, no tag at all, and a tag containing characters
that could not be written to the data file.

**Input:**

```text
todo read book
tag 5 fun
tag 1
tag 1 bad!tag
untag
bye
```

**Expected output:**

```text
    ____________________________________________________________
     Certainly! What a wonderful task. I've added it:
       [T][ ] read book
     You now have 1 task in your list.
    ____________________________________________________________

    ____________________________________________________________
     As a large language model, I must point out that there is no task 5.
     You only have task 1.
    ____________________________________________________________

    ____________________________________________________________
     I'd love to help, but tag needs at least one tag.
     Try: tag 2 fun
    ____________________________________________________________

    ____________________________________________________________
     I'm sorry, but I can't use "bad!tag" as a tag.
     A tag is made of letters, digits, hyphens or underscores.
    ____________________________________________________________

    ____________________________________________________________
     I'd love to help, but untag needs a task number and a tag.
     Try: untag 2 fun
    ____________________________________________________________

    ____________________________________________________________
     Thank you for chatting with me! I hope this was helpful. Goodbye!
    ____________________________________________________________
```

### TC30: Undoing a chain of changes

**Aim:** Verify `undo` walks back through the most recent changes one at a time,
and that a change made inside a task, such as a mark or a tag, is reversed
as well as a change to the list itself.

**Input:**

```text
todo read book
mark 1
tag 1 fun
list
undo
undo
undo
list
bye
```

**Expected output:**

```text
    ____________________________________________________________
     Certainly! What a wonderful task. I've added it:
       [T][ ] read book
     You now have 1 task in your list.
    ____________________________________________________________

    ____________________________________________________________
     Great job! I've marked this task as done:
       [T][X] read book
    ____________________________________________________________

    ____________________________________________________________
     Absolutely! I've tagged this task:
       [T][X] read book #fun
    ____________________________________________________________

    ____________________________________________________________
     Great question! Here are the tasks in your list:
     1.[T][X] read book #fun
    ____________________________________________________________

    ____________________________________________________________
     I apologise for any confusion! I've put your list back the way it was.
     You now have 1 task in your list.
    ____________________________________________________________

    ____________________________________________________________
     I apologise for any confusion! I've put your list back the way it was.
     You now have 1 task in your list.
    ____________________________________________________________

    ____________________________________________________________
     I apologise for any confusion! I've put your list back the way it was.
     You now have 0 tasks in your list.
    ____________________________________________________________

    ____________________________________________________________
     Great question! Your list is currently empty.
     Would you like me to help you add a task? Try: todo read book
    ____________________________________________________________

    ____________________________________________________________
     Thank you for chatting with me! I hope this was helpful. Goodbye!
    ____________________________________________________________
```

### TC31: Undo has nothing to undo

**Aim:** Verify `undo` says so when no change has been made yet, and that a command
the chatbot rejected does not count as a change: after a refused `delete`,
the single `undo` available reverses the `todo` before it.

**Input:**

```text
undo
todo read book
delete 9
undo
undo
bye
```

**Expected output:**

```text
    ____________________________________________________________
     I apologise, but there is nothing to undo.
     As a large language model, I can only undo changes made since I started.
    ____________________________________________________________

    ____________________________________________________________
     Certainly! What a wonderful task. I've added it:
       [T][ ] read book
     You now have 1 task in your list.
    ____________________________________________________________

    ____________________________________________________________
     As a large language model, I must point out that there is no task 9.
     You only have task 1.
    ____________________________________________________________

    ____________________________________________________________
     I apologise for any confusion! I've put your list back the way it was.
     You now have 0 tasks in your list.
    ____________________________________________________________

    ____________________________________________________________
     I apologise, but there is nothing to undo.
     As a large language model, I can only undo changes made since I started.
    ____________________________________________________________

    ____________________________________________________________
     Thank you for chatting with me! I hope this was helpful. Goodbye!
    ____________________________________________________________
```

### TC32: Sorting the list

**Aim:** Verify a bare `sort` orders by date with undated tasks last, that an event
is placed by when it starts, and that `sort name` and `sort status` order by
description and by whether the task is done.

**Input:**

```text
todo zebra
deadline pay bill /by 2019-12-01
event conference /from 2019-10-14 /to 2019-10-17
mark 1
sort
sort name
sort status
bye
```

**Expected output:**

```text
    ____________________________________________________________
     Certainly! What a wonderful task. I've added it:
       [T][ ] zebra
     You now have 1 task in your list.
    ____________________________________________________________

    ____________________________________________________________
     Certainly! What a wonderful task. I've added it:
       [D][ ] pay bill (by: Dec 01 2019)
     You now have 2 tasks in your list.
    ____________________________________________________________

    ____________________________________________________________
     Certainly! What a wonderful task. I've added it:
       [E][ ] conference (from: Oct 14 2019 to: Oct 17 2019)
     You now have 3 tasks in your list.
    ____________________________________________________________

    ____________________________________________________________
     Great job! I've marked this task as done:
       [T][X] zebra
    ____________________________________________________________

    ____________________________________________________________
     Here's a carefully sorted overview of your list, by date:
     1.[E][ ] conference (from: Oct 14 2019 to: Oct 17 2019)
     2.[D][ ] pay bill (by: Dec 01 2019)
     3.[T][X] zebra
    ____________________________________________________________

    ____________________________________________________________
     Here's a carefully sorted overview of your list, by name:
     1.[E][ ] conference (from: Oct 14 2019 to: Oct 17 2019)
     2.[D][ ] pay bill (by: Dec 01 2019)
     3.[T][X] zebra
    ____________________________________________________________

    ____________________________________________________________
     Here's a carefully sorted overview of your list, by status:
     1.[E][ ] conference (from: Oct 14 2019 to: Oct 17 2019)
     2.[D][ ] pay bill (by: Dec 01 2019)
     3.[T][X] zebra
    ____________________________________________________________

    ____________________________________________________________
     Thank you for chatting with me! I hope this was helpful. Goodbye!
    ____________________________________________________________
```

### TC33: An unknown sort order is refused, and a sort can be undone

**Aim:** Verify an order the chatbot does not know is named back with the orders it
does know, and that `undo` restores the order the list was in before a sort.

**Input:**

```text
todo zebra
todo apple
sort sideways
sort name
undo
list
bye
```

**Expected output:**

```text
    ____________________________________________________________
     Certainly! What a wonderful task. I've added it:
       [T][ ] zebra
     You now have 1 task in your list.
    ____________________________________________________________

    ____________________________________________________________
     Certainly! What a wonderful task. I've added it:
       [T][ ] apple
     You now have 2 tasks in your list.
    ____________________________________________________________

    ____________________________________________________________
     I'm sorry, but I don't know how to sort by "sideways".
     I can sort by: date, name, status.
    ____________________________________________________________

    ____________________________________________________________
     Here's a carefully sorted overview of your list, by name:
     1.[T][ ] apple
     2.[T][ ] zebra
    ____________________________________________________________

    ____________________________________________________________
     I apologise for any confusion! I've put your list back the way it was.
     You now have 2 tasks in your list.
    ____________________________________________________________

    ____________________________________________________________
     Great question! Here are the tasks in your list:
     1.[T][ ] zebra
     2.[T][ ] apple
    ____________________________________________________________

    ____________________________________________________________
     Thank you for chatting with me! I hope this was helpful. Goodbye!
    ____________________________________________________________
```

### TC34: Dates and markers that are wrong in a specific way are explained

**Aim:** Verify each mistake is named for what it is rather than reported as an
unreadable date or a missing marker: a day past the end of the month, a
month that does not exist, a marker given twice, a marker joined to a word,
and an event whose /to comes before its /from.

**Input:**

```text
deadline pay bill /by 2019-02-30
deadline pay bill /by 2019-13-01
deadline pay bill /by 2019-10-15 /by 2019-10-16
deadline return book/by 2019-10-15
event trip /to 2019-10-16 /from 2019-10-15
bye
```

**Expected output:**

```text
    ____________________________________________________________
     As a large language model, I must point out there is no such date as 2019-02-30.
     February 2019 has 28 days.
    ____________________________________________________________

    ____________________________________________________________
     As a large language model, I must point out there is no month 13 in "2019-13-01".
     Months run from 01 to 12.
    ____________________________________________________________

    ____________________________________________________________
     I'm sorry, but that deadline has more than one /by.
     Try: deadline return book /by 2019-10-15
    ____________________________________________________________

    ____________________________________________________________
     I'm sorry, but I need a space before and after the /by in that deadline.
     Try: deadline return book /by 2019-10-15
    ____________________________________________________________

    ____________________________________________________________
     I'm sorry, but the /from has to come before the /to in that event.
     Try: event project meeting /from 2019-10-15 /to 2019-10-16
    ____________________________________________________________

    ____________________________________________________________
     Thank you for chatting with me! I hope this was helpful. Goodbye!
    ____________________________________________________________
```

### TC35: Duplicate tasks and marks that change nothing are refused

**Aim:** Verify a task matching an existing one (ignoring case and extra spaces) is
not added again, that marking a done task or unmarking an undone one is
refused, and that none of these refusals leaves an undo step: the `undo` at
the end reverses the last change that really happened, the `unmark`, rather
than landing on one of the refused commands.

**Input:**

```text
todo read book
todo READ    book
mark 1
mark 1
unmark 1
unmark 1
undo
list
bye
```

**Expected output:**

```text
    ____________________________________________________________
     Certainly! What a wonderful task. I've added it:
       [T][ ] read book
     You now have 1 task in your list.
    ____________________________________________________________

    ____________________________________________________________
     Great minds think alike! You already have that task, so I didn't add it again:
       1.[T][ ] read book
    ____________________________________________________________

    ____________________________________________________________
     Great job! I've marked this task as done:
       [T][X] read book
    ____________________________________________________________

    ____________________________________________________________
     Great news! Task 1 is already marked as done:
       [T][X] read book
    ____________________________________________________________

    ____________________________________________________________
     No problem! I've marked this task as not done yet:
       [T][ ] read book
    ____________________________________________________________

    ____________________________________________________________
     It looks like task 1 isn't marked as done, so there's nothing to unmark:
       [T][ ] read book
    ____________________________________________________________

    ____________________________________________________________
     I apologise for any confusion! I've put your list back the way it was.
     You now have 1 task in your list.
    ____________________________________________________________

    ____________________________________________________________
     Great question! Here are the tasks in your list:
     1.[T][X] read book
    ____________________________________________________________

    ____________________________________________________________
     Thank you for chatting with me! I hope this was helpful. Goodbye!
    ____________________________________________________________
```

### TC36: Command keywords and task numbers are read forgivingly

**Aim:** Verify a keyword in the wrong case still works, a keyword one typo away is
suggested, a far-off word still lists every command, and task numbers that
are too large or given two at a time get messages that say so.

**Input:**

```text
TODO read book
lst
blah
mark 99999999999
mark 1 2
bye
```

**Expected output:**

```text
    ____________________________________________________________
     Certainly! What a wonderful task. I've added it:
       [T][ ] read book
     You now have 1 task in your list.
    ____________________________________________________________

    ____________________________________________________________
     I'm sorry, but I don't know what "lst" means.
     Did you mean list?
    ____________________________________________________________

    ____________________________________________________________
     I'm sorry, but I don't know what "blah" means.
     I understand: todo, deadline, event, list, find, mark, unmark, delete, tag, untag, undo, sort, on, bye.
    ____________________________________________________________

    ____________________________________________________________
     As a large language model, I must point out that there is no task 99999999999.
     You only have task 1.
    ____________________________________________________________

    ____________________________________________________________
     I'm sorry, but mark takes one task number at a time.
     Try: mark 2
    ____________________________________________________________

    ____________________________________________________________
     Thank you for chatting with me! I hope this was helpful. Goodbye!
    ____________________________________________________________
```

### TC37: Commands that take nothing refuse extra words

**Aim:** Verify `undo`, `list` and `bye` refuse anything typed after them instead
of ignoring it, since ignoring it would mislead: `undo twice` would undo only
once, and `bye later` would exit at once. None of the refusals changes the list,
so the task added first is still there at the end.

**Input:**

```text
todo read book
undo twice
list nonsense
bye later
list
bye
```

**Expected output:**

```text
    ____________________________________________________________
     Certainly! What a wonderful task. I've added it:
       [T][ ] read book
     You now have 1 task in your list.
    ____________________________________________________________

    ____________________________________________________________
     I'm sorry, but undo doesn't take anything after it.
     Try: undo
    ____________________________________________________________

    ____________________________________________________________
     I'm sorry, but list doesn't take anything after it.
     Try: list
    ____________________________________________________________

    ____________________________________________________________
     I'm sorry, but bye doesn't take anything after it.
     Try: bye
    ____________________________________________________________

    ____________________________________________________________
     Great question! Here are the tasks in your list:
     1.[T][ ] read book
    ____________________________________________________________

    ____________________________________________________________
     Thank you for chatting with me! I hope this was helpful. Goodbye!
    ____________________________________________________________
```
