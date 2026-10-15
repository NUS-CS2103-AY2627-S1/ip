# UI Test Plan

This file drives the `test-ui` skill. All test cases below run against a
**single continuous session** of the program (state carries over between
cases — a task added in an earlier case is still there for a later `list`
or `mark` case). Test cases must stay in the order they should be run in.

Each test case has:

- **Aim** — what the case is checking.
- **Input** — the line typed at this step, as a fenced code block. Empty
  for the startup case, which checks the program's output before any input
  is given.
- **Expected Output** — the exact lines the program must print in response,
  as a fenced code block. Do not include divider lines (`____...`) or
  leading/trailing blank lines — the runner adds that framing itself and
  checks it separately from the content.

Negative (error) cases are interleaved with the positive cases that change
state, and are followed by a `list` check where useful, so that a negative
case which incorrectly mutates state (e.g. adds a task it should have
rejected) shows up immediately as a wrong task count or wrong task line,
rather than being masked by a later positive case.

## Preconditions

The program loads its saved tasks from `data/ff15.txt` and its saved contacts
from `data/contacts.txt` on startup, so the plan assumes both files are
**empty or absent** before the run — otherwise the first `list` and
`contact list` cases would show leftover entries. Delete them before running:

```bash
rm -f data/ff15.txt data/contacts.txt
```

A complete run ends with every task and every contact deleted, so both files
are left empty and the next run starts clean. A run that stops early (at a
failing case) leaves entries behind, so delete the files again before
re-running.

If either file has a line that cannot be understood (an unknown task type,
or a line missing fields), that line is skipped and the startup block reports
it after the greeting — `No. GOD. NO. I couldn't read 1 line in data/ff15.txt, so I skipped it:`,
the line number and reason, then `The rest loaded fine. Call the IT guy, what's his name?` —
and every other line is loaded as normal. If a file cannot be read at all
(e.g. it is a folder), the block instead prints `Couldn't read your saved
tasks: ...` and starts that list empty. The two files are read independently,
so a damaged one does not cost the user the other. Those cases can't be
covered here, since the runner starts the program itself and each plan runs as
one session; check them by hand by writing a bad line into `data/ff15.txt` or
`data/contacts.txt` and starting the program.

## Test Case: Startup
**Aim:** The program prints its banner and greeting before any input is given.
**Input:**
```
```
**Expected Output:**
```
 _____ _____ _  ____  
|  ___|  ___/ |/ ___| 
| |_  | |_  | |\___ \ 
|  _| |  _| | | ___) |
|_|   |_|   |_||____/ 

     Hi. I'm FF15. Assistant Regional Manager. ...Assistant TO the Regional Manager. Which is you.
     What can I do for you, boss?
```

## Test Case: Unknown command
**Aim:** Input that isn't a recognised command word reports an error instead of being silently added as a task.
**Input:**
```
blah
```
**Expected Output:**
```
     No. GOD. NO. I don't know what that means. Is this a Jim thing? Is Jim doing a thing?
```

## Test Case: List with an unexpected argument
**Aim:** `list` only matches the bare word — `list now` isn't recognised, since the code checks `input.equals("list")` with no `startsWith` fallback.
**Input:**
```
list now
```
**Expected Output:**
```
     No. GOD. NO. I don't know what that means. Is this a Jim thing? Is Jim doing a thing?
```

## Test Case: List on an empty list
**Aim:** `list` before any task has been added prints only the header line, confirming the two negative cases above didn't add anything.
**Input:**
```
list
```
**Expected Output:**
```
     Nothing on the list. Just like Toby's contribution to this office.
```

## Test Case: Todo with no description
**Aim:** `todo` with nothing after it reports an empty-description error.
**Input:**
```
todo
```
**Expected Output:**
```
     No. GOD. NO. A todo with nothing in it. That's what she-- no. Tell me what to do.
```

## Test Case: Word that merely starts with "todo"
**Aim:** `todox ...` must not be treated as `todo` — `startsWith("todo ")` requires the trailing space, so this should fall through to the unknown-command error.
**Input:**
```
todox hello
```
**Expected Output:**
```
     No. GOD. NO. I don't know what that means. Is this a Jim thing? Is Jim doing a thing?
```

## Test Case: Add a todo
**Aim:** `todo <description>` adds a Todo and confirms it with the task count.
**Input:**
```
todo read book
```
**Expected Output:**
```
     That's what she said. Also, added:
       [T][ ] read book
     Now you have 1 tasks in the list.
```

## Test Case: Todo containing the save file's separator

**Aim:** `|` is the one character a description may not contain, since the save file uses it to separate fields; it is refused at input rather than silently losing the text after it on reload.
**Input:**
```
todo read | book
```
**Expected Output:**
```
     No. GOD. NO. A description can't contain '|'. It's the one character I use to save things.
```

## Test Case: Todo with runs of spaces inside

**Aim:** Extra spaces inside a description are collapsed to one, so the task is stored the way it reads.
**Input:**
```
todo   buy    milk
```
**Expected Output:**
```
     That's what she said. Also, added:
       [T][ ] buy milk
     Now you have 2 tasks in the list.
```

## Test Case: Delete the spaced todo

**Aim:** Removes it again so the later cases see the list they expect.
**Input:**
```
delete 2
```
**Expected Output:**
```
     Gone. Like Toby, if I had my way. Removed:
       [T][ ] buy milk
     Now you have 1 tasks in the list.
```

## Test Case: Adding the same todo again

**Aim:** A task identical to one already in the list is refused, and the message says which task it duplicates.
**Input:**
```
todo read book
```
**Expected Output:**
```
     No. GOD. NO. You already have that one. It's task 1. I remember everything.
```

## Test Case: List after adding one todo
**Aim:** `list` shows exactly the one todo, confirming none of the preceding negative cases left a stray task behind.
**Input:**
```
list
```
**Expected Output:**
```
     Here's what we're working with, people:
     1.[T][ ] read book
```

## Test Case: Deadline with no /by
**Aim:** `deadline <description>` without a `/by` reports an error explaining the required format.
**Input:**
```
deadline return book
```
**Expected Output:**
```
     No. GOD. NO. When? Deadlines need a /by, e.g.: deadline return book /by 2019-12-02 1800
```

## Test Case: Deadline with /by given twice

**Aim:** A marker given twice is refused and named, rather than the second one being read as part of the date.
**Input:**
```
deadline x /by 2019-12-02 /by 2019-12-03
```
**Expected Output:**
```
     No. GOD. NO. You gave /by twice. Once is plenty.
```

## Test Case: Deadline with no description at all
**Aim:** `deadline /by Sunday` (description omitted, `/by` immediately after the command) is reported as a missing description, which is what is actually wrong, rather than as a missing `/by`.
**Input:**
```
deadline /by Sunday
```
**Expected Output:**
```
     No. GOD. NO. A deadline needs a description. I'm a manager, not a mind reader. Which I also am.
```

## Test Case: Deadline with empty /by
**Aim:** `deadline <description> /by` with nothing after `/by` reports an empty-by error.
**Input:**
```
deadline return book /by
```
**Expected Output:**
```
     No. GOD. NO. A /by with nothing after it. When is it due? Use your words.
```

## Test Case: Word that merely starts with "deadline"
**Aim:** `deadlinex ...` must not be treated as `deadline` — `startsWith("deadline ")` requires the trailing space, so this should fall through to the unknown-command error.
**Input:**
```
deadlinex return book /by 2019-12-02
```
**Expected Output:**
```
     No. GOD. NO. I don't know what that means. Is this a Jim thing? Is Jim doing a thing?
```

## Test Case: Deadline with a /by that isn't a date
**Aim:** A `/by` in the old free-text style is now rejected, since the date is parsed into a `LocalDate` instead of being stored as a String.
**Input:**
```
deadline return book /by Sunday
```
**Expected Output:**
```
     No. GOD. NO. 'Sunday' isn't a date. I know dates. I've been on a lot of dates. Write it as yyyy-mm-dd, or yyyy-mm-dd HHmm to add a time, e.g. 2019-12-02 or 2019-12-02 1800
```

## Test Case: Deadline with a well-formed but impossible date
**Aim:** `2019-13-45` matches the yyyy-mm-dd shape but has no such month or day, so the parser rejects it too — confirming the check is a real date parse, not just a pattern match.
**Input:**
```
deadline return book /by 2019-13-45
```
**Expected Output:**
```
     No. GOD. NO. '2019-13-45' isn't a date. I know dates. I've been on a lot of dates. Write it as yyyy-mm-dd, or yyyy-mm-dd HHmm to add a time, e.g. 2019-12-02 or 2019-12-02 1800
```

## Test Case: Add a deadline
**Aim:** `deadline <description> /by <yyyy-mm-dd>` adds a Deadline, tagged `[D]`, with the date parsed into a `LocalDate` and printed back in `MMM dd yyyy` form rather than as the text that was typed.
**Input:**
```
deadline return book /by 2019-12-02
```
**Expected Output:**
```
     Got it. Added:
       [D][ ] return book (by: Dec 02 2019)
     Now you have 2 tasks in the list.
```

## Test Case: List after adding a deadline
**Aim:** `list` shows both tasks in order, confirming the four negative deadline cases above didn't add or corrupt anything.
**Input:**
```
list
```
**Expected Output:**
```
     Here's what we're working with, people:
     1.[T][ ] read book
     2.[D][ ] return book (by: Dec 02 2019)
```

## Test Case: Event with no /from or /to
**Aim:** `event <description>` without `/from`/`/to` reports an error explaining the required format.
**Input:**
```
event project meeting
```
**Expected Output:**
```
     No. GOD. NO. An event needs a /from and a /to. Otherwise how do I know when to show up? e.g.: event project meeting /from 2019-12-05 1400 /to 2019-12-05 1600
```

## Test Case: Event with /to before /from
**Aim:** `/to` appearing before `/from` is rejected, since the code requires `toIndex` to come after `fromIndex`.
**Input:**
```
event project meeting /to 2019-12-06 /from 2019-12-05
```
**Expected Output:**
```
     No. GOD. NO. An event needs a /from and a /to. Otherwise how do I know when to show up? e.g.: event project meeting /from 2019-12-05 1400 /to 2019-12-05 1600
```

## Test Case: Event ending the moment it starts

**Aim:** An event with no duration is refused; only one that ends after it starts is an event.
**Input:**
```
event blink /from 2019-12-05 1400 /to 2019-12-05 1400
```
**Expected Output:**
```
     No. GOD. NO. It ends when it starts? That's not an event. That's a moment.
```

## Test Case: Event with /from but no /to
**Aim:** Supplying only `/from` still reports the combined needs-both error.
**Input:**
```
event project meeting /from 2019-12-05
```
**Expected Output:**
```
     No. GOD. NO. An event needs a /from and a /to. Otherwise how do I know when to show up? e.g.: event project meeting /from 2019-12-05 1400 /to 2019-12-05 1600
```

## Test Case: Event with /to but no /from
**Aim:** Supplying only `/to` still reports the combined needs-both error.
**Input:**
```
event project meeting /to 2019-12-06
```
**Expected Output:**
```
     No. GOD. NO. An event needs a /from and a /to. Otherwise how do I know when to show up? e.g.: event project meeting /from 2019-12-05 1400 /to 2019-12-05 1600
```

## Test Case: Event with empty /from
**Aim:** `event <description> /from /to <to>` with nothing between `/from` and `/to` reports the empty date/time error.
**Input:**
```
event project meeting /from /to 2019-12-06
```
**Expected Output:**
```
     No. GOD. NO. A /from or /to with nothing after it. When do I show up?
```

## Test Case: Event with empty /to
**Aim:** `event <description> /from <from> /to` with nothing after `/to` reports the empty date/time error.
**Input:**
```
event project meeting /from 2019-12-05 /to
```
**Expected Output:**
```
     No. GOD. NO. A /from or /to with nothing after it. When do I show up?
```

## Test Case: Word that merely starts with "event"
**Aim:** `eventx ...` must not be treated as `event` — `startsWith("event ")` requires the trailing space, so this should fall through to the unknown-command error.
**Input:**
```
eventx project meeting /from 2019-12-05 /to 2019-12-07
```
**Expected Output:**
```
     No. GOD. NO. I don't know what that means. Is this a Jim thing? Is Jim doing a thing?
```

## Test Case: Event that ends before it starts
**Aim:** A `/to` date earlier than the `/from` date is rejected, since an event can't finish before it begins.
**Input:**
```
event project meeting /from 2019-12-09 /to 2019-12-08
```
**Expected Output:**
```
     No. GOD. NO. It ends before it starts? That's not an event. That's a Ryan.
```

## Test Case: Event with a /from that isn't a date
**Aim:** Event dates go through the same parser as deadline dates, so free text is rejected the same way.
**Input:**
```
event project meeting /from Monday /to 2019-12-06
```
**Expected Output:**
```
     No. GOD. NO. 'Monday' isn't a date. I know dates. I've been on a lot of dates. Write it as yyyy-mm-dd, or yyyy-mm-dd HHmm to add a time, e.g. 2019-12-02 or 2019-12-02 1800
```

## Test Case: Add an event
**Aim:** `event <description> /from <yyyy-mm-dd> /to <yyyy-mm-dd>` adds an Event, tagged `[E]`, with both dates parsed into `LocalDate`s and printed back in `MMM dd yyyy` form. It spans three days so that the `on` cases below can check a query for a day in the middle of an event.
**Input:**
```
event project meeting /from 2019-12-05 /to 2019-12-07
```
**Expected Output:**
```
     Am I invited? ...I'm invited. Added:
       [E][ ] project meeting (from: Dec 05 2019 to: Dec 07 2019)
     Now you have 3 tasks in the list.
```

## Test Case: List after adding all three types
**Aim:** `list` shows the todo, deadline, and event with their distinct type tags and suffixes, confirming the eight negative event cases above didn't add or corrupt anything.
**Input:**
```
list
```
**Expected Output:**
```
     Here's what we're working with, people:
     1.[T][ ] read book
     2.[D][ ] return book (by: Dec 02 2019)
     3.[E][ ] project meeting (from: Dec 05 2019 to: Dec 07 2019)
```

## Test Case: Query a day with one deadline on it
**Aim:** `on <yyyy-mm-dd>` lists the tasks falling on that single day — here only the deadline, since the event hasn't started and the todo has no date at all.
**Input:**
```
on 2019-12-02
```
**Expected Output:**
```
     On Dec 02 2019 we've got:
     1.[D][ ] return book (by: Dec 02 2019)
```

## Test Case: Query a day in the middle of an event
**Aim:** Dec 06 is neither the event's start nor its end, but the event is still running that day, so it matches — confirming the check is an overlap, not an equality test on the two end dates.
**Input:**
```
on 2019-12-06
```
**Expected Output:**
```
     On Dec 06 2019 we've got:
     1.[E][ ] project meeting (from: Dec 05 2019 to: Dec 07 2019)
```

## Test Case: Query a whole month
**Aim:** `on <yyyy-mm>` widens the span to the whole month, so both dated tasks match. The todo is still excluded, having no date.
**Input:**
```
on 2019-12
```
**Expected Output:**
```
     On Dec 2019 we've got:
     1.[D][ ] return book (by: Dec 02 2019)
     2.[E][ ] project meeting (from: Dec 05 2019 to: Dec 07 2019)
```

## Test Case: Query a whole year
**Aim:** `on <yyyy>` widens the span again to the whole year, with the results kept in list order.
**Input:**
```
on 2019
```
**Expected Output:**
```
     On 2019 we've got:
     1.[D][ ] return book (by: Dec 02 2019)
     2.[E][ ] project meeting (from: Dec 05 2019 to: Dec 07 2019)
```

## Test Case: Query a date with nothing on it
**Aim:** A day no task falls on reports that plainly, rather than printing a bare header that would look like a bug.
**Input:**
```
on 2020-01-01
```
**Expected Output:**
```
     Nothing on Jan 01 2020. Conference room is free. I'm calling a meeting.
```

## Test Case: Query with no date at all
**Aim:** `on` with nothing after it asks for one, listing all three accepted forms.
**Input:**
```
on
```
**Expected Output:**
```
     No. GOD. NO. When? Tell me when. e.g.: on 2019-12-02, on 2019-12, or on 2019
```

## Test Case: Query that isn't a date at all
**Aim:** Free text after `on` is rejected with the same three-forms hint.
**Input:**
```
on nonsense
```
**Expected Output:**
```
     No. GOD. NO. 'nonsense' isn't a date, month, or year. I know dates. Try: on 2019-12-02, on 2019-12, or on 2019
```

## Test Case: Query with an impossible month
**Aim:** `2019-13` has the yyyy-mm shape but no 13th month, so the parse fails rather than matching on shape alone.
**Input:**
```
on 2019-13
```
**Expected Output:**
```
     No. GOD. NO. '2019-13' isn't a date, month, or year. I know dates. Try: on 2019-12-02, on 2019-12, or on 2019
```

## Test Case: Query with too many date parts
**Aim:** Four dash-separated parts match none of the day/month/year shapes, so the query is rejected instead of being silently truncated to a day.
**Input:**
```
on 2019-12-02-05
```
**Expected Output:**
```
     No. GOD. NO. '2019-12-02-05' isn't a date, month, or year. I know dates. Try: on 2019-12-02, on 2019-12, or on 2019
```

## Test Case: Word that merely starts with "on"
**Aim:** `once 2019` must not be treated as `on` — `startsWith("on ")` requires the trailing space, so this should fall through to the unknown-command error.
**Input:**
```
once 2019
```
**Expected Output:**
```
     No. GOD. NO. I don't know what that means. Is this a Jim thing? Is Jim doing a thing?
```

## Test Case: Mark with no task number
**Aim:** `mark` with nothing after it reports an error instead of crashing.
**Input:**
```
mark
```
**Expected Output:**
```
     No. GOD. NO. Which one? Use your words. Like, a number. e.g. mark 2
```

## Test Case: Mark with a non-numeric task number
**Aim:** `mark <non-number>` reports an error naming the bad argument.
**Input:**
```
mark abc
```
**Expected Output:**
```
     No. GOD. NO. 'abc' is not a number. I know numbers. I run a branch.
```

## Test Case: Mark with a decimal task number
**Aim:** `mark 1.5` isn't a valid integer, so it hits the same non-numeric error path as letters.
**Input:**
```
mark 1.5
```
**Expected Output:**
```
     No. GOD. NO. '1.5' is not a number. I know numbers. I run a branch.
```

## Test Case: Mark with task number zero
**Aim:** `mark 0` parses fine as an integer but fails the range check, reporting the out-of-range error rather than crashing on `list.get(-1)`.
**Input:**
```
mark 0
```
**Expected Output:**
```
     No. GOD. NO. Task 0? There are 3. I'm not a magician. Well, I'm a bit of a magician.
```

## Test Case: Mark with a negative task number
**Aim:** `mark -1` parses fine (`Integer.parseInt` accepts a leading minus) but fails the range check.
**Input:**
```
mark -1
```
**Expected Output:**
```
     No. GOD. NO. Task -1? There are 3. I'm not a magician. Well, I'm a bit of a magician.
```

## Test Case: Mark with an out-of-range task number
**Aim:** `mark <n>` beyond the list size reports an error stating how many tasks exist.
**Input:**
```
mark 9
```
**Expected Output:**
```
     No. GOD. NO. Task 9? There are 3. I'm not a magician. Well, I'm a bit of a magician.
```

## Test Case: Word that merely starts with "mark"
**Aim:** `marking 1` must not be treated as `mark` — `startsWith("mark ")` requires the trailing space, so this should fall through to the unknown-command error.
**Input:**
```
marking 1
```
**Expected Output:**
```
     No. GOD. NO. I don't know what that means. Is this a Jim thing? Is Jim doing a thing?
```

## Test Case: Mark task 1 as done
**Aim:** `mark <n>` marks the n-th task done and echoes it.
**Input:**
```
mark 1
```
**Expected Output:**
```
     Boom. Done. That's a Dundie right there:
       [T][X] read book
```

## Test Case: List after marking task 1
**Aim:** `list` shows only task 1 as done, confirming the six negative mark cases above didn't mark or otherwise change task 2 or 3.
**Input:**
```
list
```
**Expected Output:**
```
     Here's what we're working with, people:
     1.[T][X] read book
     2.[D][ ] return book (by: Dec 02 2019)
     3.[E][ ] project meeting (from: Dec 05 2019 to: Dec 07 2019)
```

## Test Case: Unmark with no task number
**Aim:** `unmark` with nothing after it reports an error instead of crashing.
**Input:**
```
unmark
```
**Expected Output:**
```
     No. GOD. NO. Which one? Use your words. Like, a number. e.g. unmark 2
```

## Test Case: Unmark with a non-numeric task number
**Aim:** `unmark <non-number>` reports an error naming the bad argument, reusing the same parser as `mark`.
**Input:**
```
unmark xyz
```
**Expected Output:**
```
     No. GOD. NO. 'xyz' is not a number. I know numbers. I run a branch.
```

## Test Case: Unmark with an out-of-range task number
**Aim:** `unmark <n>` beyond the list size reports an error instead of crashing.
**Input:**
```
unmark 9
```
**Expected Output:**
```
     No. GOD. NO. Task 9? There are 3. I'm not a magician. Well, I'm a bit of a magician.
```

## Test Case: Word that merely starts with "unmark"
**Aim:** `unmarking 1` must not be treated as `unmark` — `startsWith("unmark ")` requires the trailing space, so this should fall through to the unknown-command error.
**Input:**
```
unmarking 1
```
**Expected Output:**
```
     No. GOD. NO. I don't know what that means. Is this a Jim thing? Is Jim doing a thing?
```

## Test Case: Unmark task 1
**Aim:** `unmark <n>` reverses the done status and echoes it.
**Input:**
```
unmark 1
```
**Expected Output:**
```
     Un-done. Like me and Jan. Anyway:
       [T][ ] read book
```

## Test Case: List after mark/unmark round-trip
**Aim:** `list` shows task 1 back to not-done, confirming the round trip — and the three negative unmark cases above — left no side effects on the other tasks.
**Input:**
```
list
```
**Expected Output:**
```
     Here's what we're working with, people:
     1.[T][ ] read book
     2.[D][ ] return book (by: Dec 02 2019)
     3.[E][ ] project meeting (from: Dec 05 2019 to: Dec 07 2019)
```

## Test Case: Delete with no task number
**Aim:** `delete` with nothing after it reports an error instead of crashing.
**Input:**
```
delete
```
**Expected Output:**
```
     No. GOD. NO. Which one? Use your words. Like, a number. e.g. delete 2
```

## Test Case: Delete with a non-numeric task number
**Aim:** `delete <non-number>` reports an error naming the bad argument, reusing the same parser as `mark`/`unmark`.
**Input:**
```
delete abc
```
**Expected Output:**
```
     No. GOD. NO. 'abc' is not a number. I know numbers. I run a branch.
```

## Test Case: Delete with an out-of-range task number
**Aim:** `delete <n>` beyond the list size reports an error instead of crashing.
**Input:**
```
delete 9
```
**Expected Output:**
```
     No. GOD. NO. Task 9? There are 3. I'm not a magician. Well, I'm a bit of a magician.
```

## Test Case: Word that merely starts with "delete"
**Aim:** `deletex 2` must not be treated as `delete` — `startsWith("delete ")` requires the trailing space, so this should fall through to the unknown-command error.
**Input:**
```
deletex 2
```
**Expected Output:**
```
     No. GOD. NO. I don't know what that means. Is this a Jim thing? Is Jim doing a thing?
```

## Test Case: List before deleting
**Aim:** `list` still shows all three tasks, confirming the four negative delete cases above didn't remove anything.
**Input:**
```
list
```
**Expected Output:**
```
     Here's what we're working with, people:
     1.[T][ ] read book
     2.[D][ ] return book (by: Dec 02 2019)
     3.[E][ ] project meeting (from: Dec 05 2019 to: Dec 07 2019)
```

## Test Case: Delete task 2
**Aim:** `delete <n>` removes the n-th task and echoes it along with the new task count.
**Input:**
```
delete 2
```
**Expected Output:**
```
     Gone. Like Toby, if I had my way. Removed:
       [D][ ] return book (by: Dec 02 2019)
     Now you have 2 tasks in the list.
```

## Test Case: List after deleting task 2
**Aim:** `list` shows the remaining two tasks, renumbered, confirming the deadline was removed and the other tasks shifted down correctly.
**Input:**
```
list
```
**Expected Output:**
```
     Here's what we're working with, people:
     1.[T][ ] read book
     2.[E][ ] project meeting (from: Dec 05 2019 to: Dec 07 2019)
```

## Test Case: Delete task 1
**Aim:** Deleting down further shrinks the list by one and renumbers the remaining task, continuing to drain the list toward the empty-list edge case below.
**Input:**
```
delete 1
```
**Expected Output:**
```
     Gone. Like Toby, if I had my way. Removed:
       [T][ ] read book
     Now you have 1 tasks in the list.
```

## Test Case: List with one task left
**Aim:** `list` shows the sole remaining task renumbered to 1, confirming the previous delete removed the right one.
**Input:**
```
list
```
**Expected Output:**
```
     Here's what we're working with, people:
     1.[E][ ] project meeting (from: Dec 05 2019 to: Dec 07 2019)
```

## Test Case: Delete the last task
**Aim:** Deleting the final task empties the list, reporting a count of 0.
**Input:**
```
delete 1
```
**Expected Output:**
```
     Gone. Like Toby, if I had my way. Removed:
       [E][ ] project meeting (from: Dec 05 2019 to: Dec 07 2019)
     Now you have 0 tasks in the list.
```

## Test Case: List on an empty list after deletions
**Aim:** `list` after deleting every task prints only the header line, same as the very first empty-list case, confirming deletion doesn't leave stray entries behind.
**Input:**
```
list
```
**Expected Output:**
```
     Nothing on the list. Just like Toby's contribution to this office.
```

## Test Case: Mark on an empty list
**Aim:** `mark 1` on an empty list reports the out-of-range error with a count of 0 task(s), rather than crashing on `list.get(0)`.
**Input:**
```
mark 1
```
**Expected Output:**
```
     No. GOD. NO. Task 1? There are 0. I'm not a magician. Well, I'm a bit of a magician.
```

## Test Case: Delete on an empty list
**Aim:** `delete 1` on an empty list reports the same out-of-range error with a count of 0 task(s), rather than crashing.
**Input:**
```
delete 1
```
**Expected Output:**
```
     No. GOD. NO. Task 1? There are 0. I'm not a magician. Well, I'm a bit of a magician.
```

## Test Case: Add a deadline with a time
**Aim:** `/by <yyyy-mm-dd> <HHmm>` pins the deadline to a time of day, shown after the date. The list is empty again at this point, so the count restarts at 1.
**Input:**
```
deadline return book /by 2019-12-02 1800
```
**Expected Output:**
```
     Got it. Added:
       [D][ ] return book (by: Dec 02 2019, 6:00pm)
     Now you have 1 tasks in the list.
```

## Test Case: Deadline with an impossible time
**Aim:** `1860` has the HHmm shape but there is no 60th minute, so it is rejected — the same shape-plus-value check the date half gets.
**Input:**
```
deadline return book /by 2019-12-02 1860
```
**Expected Output:**
```
     No. GOD. NO. '2019-12-02 1860' isn't a date. I know dates. I've been on a lot of dates. Write it as yyyy-mm-dd, or yyyy-mm-dd HHmm to add a time, e.g. 2019-12-02 or 2019-12-02 1800
```

## Test Case: Deadline with a time in the wrong format
**Aim:** A time must be typed as HHmm, so `6pm` is rejected even though the date beside it is valid.
**Input:**
```
deadline return book /by 2019-12-02 6pm
```
**Expected Output:**
```
     No. GOD. NO. '2019-12-02 6pm' isn't a date. I know dates. I've been on a lot of dates. Write it as yyyy-mm-dd, or yyyy-mm-dd HHmm to add a time, e.g. 2019-12-02 or 2019-12-02 1800
```

## Test Case: Add an event with times
**Aim:** An event can carry a time at each end, letting it start and finish on the same day.
**Input:**
```
event project meeting /from 2019-12-05 1400 /to 2019-12-05 1600
```
**Expected Output:**
```
     Am I invited? ...I'm invited. Added:
       [E][ ] project meeting (from: Dec 05 2019, 2:00pm to: Dec 05 2019, 4:00pm)
     Now you have 2 tasks in the list.
```

## Test Case: Event that ends earlier the same day
**Aim:** Both ends fall on the same date, so only the times distinguish them — this is rejected, confirming the ordering check compares the time as well as the date.
**Input:**
```
event project meeting /from 2019-12-05 1600 /to 2019-12-05 1400
```
**Expected Output:**
```
     No. GOD. NO. It ends before it starts? That's not an event. That's a Ryan.
```

## Test Case: List tasks that carry times
**Aim:** `list` shows both timed tasks, confirming the two negative cases above added nothing.
**Input:**
```
list
```
**Expected Output:**
```
     Here's what we're working with, people:
     1.[D][ ] return book (by: Dec 02 2019, 6:00pm)
     2.[E][ ] project meeting (from: Dec 05 2019, 2:00pm to: Dec 05 2019, 4:00pm)
```

## Test Case: Query a day for a task that carries a time
**Aim:** `on` still matches by day, ignoring the time of day, so a timed event is found by a plain date query.
**Input:**
```
on 2019-12-05
```
**Expected Output:**
```
     On Dec 05 2019 we've got:
     1.[E][ ] project meeting (from: Dec 05 2019, 2:00pm to: Dec 05 2019, 4:00pm)
```

## Test Case: Add a todo that shares an event's description
**Aim:** A todo is compared against the event already in the list before it is added. They share a description but not a kind, so it is a different task and goes in, rather than being refused or crashing on the comparison.
**Input:**
```
todo project meeting
```
**Expected Output:**
```
     That's what she said. Also, added:
       [T][ ] project meeting
     Now you have 3 tasks in the list.
```

## Test Case: Delete the todo namesake
**Aim:** Removes the todo just added, so the two timed tasks are back to being the whole list for the deletions that follow.
**Input:**
```
delete 3
```
**Expected Output:**
```
     Gone. Like Toby, if I had my way. Removed:
       [T][ ] project meeting
     Now you have 2 tasks in the list.
```

## Test Case: Delete the timed deadline
**Aim:** Removes the timed deadline, draining the list back toward empty so the plan leaves an empty save file behind.
**Input:**
```
delete 1
```
**Expected Output:**
```
     Gone. Like Toby, if I had my way. Removed:
       [D][ ] return book (by: Dec 02 2019, 6:00pm)
     Now you have 1 tasks in the list.
```

## Test Case: Delete the timed event
**Aim:** Removes the last task, leaving the list — and so the save file — empty at the end of the run.
**Input:**
```
delete 1
```
**Expected Output:**
```
     Gone. Like Toby, if I had my way. Removed:
       [E][ ] project meeting (from: Dec 05 2019, 2:00pm to: Dec 05 2019, 4:00pm)
     Now you have 0 tasks in the list.
```

## Test Case: Add a todo to search for

**Aim:** Refills the empty list with a task whose description contains "book", setting up the `find` cases that follow.
**Input:**
```
todo read book
```
**Expected Output:**
```
     That's what she said. Also, added:
       [T][ ] read book
     Now you have 1 tasks in the list.
```

## Test Case: Add a deadline to search for

**Aim:** Adds a second task also containing "book", so `find book` has more than one match to report.
**Input:**
```
deadline return book /by 2019-06-06
```
**Expected Output:**
```
     Got it. Added:
       [D][ ] return book (by: Jun 06 2019)
     Now you have 2 tasks in the list.
```

## Test Case: Mark the todo before searching

**Aim:** Marks the todo done so the `find` results show the real status icon rather than always a blank one.
**Input:**
```
mark 1
```
**Expected Output:**
```
     Boom. Done. That's a Dundie right there:
       [T][X] read book
```

## Test Case: Mark the deadline before searching

**Aim:** Marks the deadline done as well, so both matches come back with an `[X]`.
**Input:**
```
mark 2
```
**Expected Output:**
```
     Boom. Done. That's a Dundie right there:
       [D][X] return book (by: Jun 06 2019)
```

## Test Case: Find tasks by keyword

**Aim:** `find book` reports every task whose description contains the keyword, numbered from 1 in list order, across both task types.
**Input:**
```
find book
```
**Expected Output:**
```
     Found them. I'm basically a detective. Michael Scarn:
     1.[T][X] read book
     2.[D][X] return book (by: Jun 06 2019)
```

## Test Case: Find ignores capitalisation

**Aim:** `find BOOK` returns the same matches as `find book`, since the keyword search ignores the difference between upper and lower case.
**Input:**
```
find BOOK
```
**Expected Output:**
```
     Found them. I'm basically a detective. Michael Scarn:
     1.[T][X] read book
     2.[D][X] return book (by: Jun 06 2019)
```

## Test Case: Find with no matches

**Aim:** A keyword matching nothing says so, rather than printing an empty list header.
**Input:**
```
find milk
```
**Expected Output:**
```
     Nothing matching 'milk'. I looked. I looked so hard.
```

## Test Case: Find without a keyword

**Aim:** `find` with nothing after it is rejected with a message telling the user what to type.
**Input:**
```
find
```
**Expected Output:**
```
     No. GOD. NO. Look for what? Give me a word. e.g.: find book
```

## Test Case: Delete the searched todo

**Aim:** Starts draining the list again so the plan still leaves an empty save file behind.
**Input:**
```
delete 1
```
**Expected Output:**
```
     Gone. Like Toby, if I had my way. Removed:
       [T][X] read book
     Now you have 1 tasks in the list.
```

## Test Case: Delete the searched deadline

**Aim:** Removes the last remaining task, returning the list to empty before the final cases.
**Input:**
```
delete 1
```
**Expected Output:**
```
     Gone. Like Toby, if I had my way. Removed:
       [D][X] return book (by: Jun 06 2019)
     Now you have 0 tasks in the list.
```

## Test Case: Command word is case-sensitive
**Aim:** `Todo ...` (capital T) doesn't match the lowercase `todo` command, so it should be rejected as unknown rather than silently adding a task.
**Input:**
```
Todo not a command
```
**Expected Output:**
```
     No. GOD. NO. I don't know what that means. Is this a Jim thing? Is Jim doing a thing?
```

## Test Case: Contact list on an empty contact list

**Aim:** `contact list` on a fresh contact list prints its heading and nothing under it, confirming no contact survived from an earlier run.
**Input:**
```
contact list
```
**Expected Output:**
```
     Here are the contacts in your list:
```

## Test Case: Contact with no sub-command

**Aim:** `contact` on its own says which four things can follow it.
**Input:**
```
contact
```
**Expected Output:**
```
     No. GOD. NO. Tell me what to do with your contacts. Try: contact add, contact list, contact delete, or contact find
```

## Test Case: Contact with an unknown sub-command

**Aim:** A word that isn't one of the four is rejected, and the message names the valid ones.
**Input:**
```
contact mark 1
```
**Expected Output:**
```
     No. GOD. NO. I can't 'mark' a contact. Nobody can. Try: contact add, contact list, contact delete, or contact find
```

## Test Case: Word that merely starts with "contact"

**Aim:** `contacts` isn't `contact` followed by a space, so it should be unknown rather than a contact command with a strange sub-command.
**Input:**
```
contacts
```
**Expected Output:**
```
     No. GOD. NO. I don't know what that means. Is this a Jim thing? Is Jim doing a thing?
```

## Test Case: Contact add with no name

**Aim:** `contact add` with nothing after it is rejected, since a name is the one required field.
**Input:**
```
contact add
```
**Expected Output:**
```
     No. GOD. NO. A contact needs a name. Everyone has a name. Even Toby. e.g.: contact add John /phone 91234567
```

## Test Case: Contact add with a marker but no name

**Aim:** `contact add /phone ...` is a missing name, not a contact named `/phone ...`, because a marker is recognised even when it opens the line.
**Input:**
```
contact add /phone 91234567
```
**Expected Output:**
```
     No. GOD. NO. A contact needs a name. Everyone has a name. Even Toby. e.g.: contact add John /phone 91234567
```

## Test Case: Contact add with an empty /phone

**Aim:** A `/phone` given with nothing after it is rejected rather than stored as blank.
**Input:**
```
contact add John /phone
```
**Expected Output:**
```
     No. GOD. NO. A /phone with nothing after it. What's the number?
```

## Test Case: Contact add with an unusable phone

**Aim:** A phone number containing letters is rejected, and the message says which characters are allowed.
**Input:**
```
contact add John /phone hello
```
**Expected Output:**
```
     No. GOD. NO. 'hello' is not a phone number. I know phones. I have a Blackberry. Digits, spaces, +, -, and brackets only.
```

## Test Case: Contact name containing the save file's separator

**Aim:** The same reserved-character rule applies to contact fields.
**Input:**
```
contact add John | Smith
```
**Expected Output:**
```
     No. GOD. NO. A contact name can't contain '|'. It's the one character I use to save things.
```

## Test Case: Contact add with an unusable email

**Aim:** An email without an `@` is rejected.
**Input:**
```
contact add John /email nope
```
**Expected Output:**
```
     No. GOD. NO. 'nope' is not an email. I've sent emails. Reply-all emails. It needs one @ with something on both sides.
```

## Test Case: List after the rejected contacts

**Aim:** `contact list` still shows nothing, confirming none of the rejected commands left a stray contact behind.
**Input:**
```
contact list
```
**Expected Output:**
```
     Here are the contacts in your list:
```

## Test Case: Add a contact with every field

**Aim:** `contact add <name> /phone <phone> /email <email>` stores all three and confirms with the contact count.
**Input:**
```
contact add John /phone 91234567 /email john@example.com
```
**Expected Output:**
```
     New friend. I'm friends with everyone. Added:
       John (phone: 91234567, email: john@example.com)
     1 contacts. I know everyone. Everyone knows me.
```

## Test Case: Adding the same contact again

**Aim:** A contact identical in every field to one already in the list is refused, and the message says which contact it repeats. Nothing is added.
**Input:**
```
contact add John /phone 91234567 /email john@example.com
```
**Expected Output:**
```
     No. GOD. NO. I already know them. That's contact 1. I know everyone, remember?
```

## Test Case: Adding a contact who only shares a name

**Aim:** Two people can share a name, so a contact with the same name but different details is a new contact, not a repeat.
**Input:**
```
contact add John /phone 98765432
```
**Expected Output:**
```
     New friend. I'm friends with everyone. Added:
       John (phone: 98765432)
     2 contacts. I know everyone. Everyone knows me.
```

## Test Case: Delete the namesake

**Aim:** Removes the second John, so the contacts that follow keep the numbers the rest of the plan expects.
**Input:**
```
contact delete 2
```
**Expected Output:**
```
     Dead to me. Removed:
       John (phone: 98765432)
     1 contacts. I know everyone. Everyone knows me.
```

## Test Case: Add a contact with the markers reversed

**Aim:** `/email` before `/phone` reads the same way, since the markers are order-independent.
**Input:**
```
contact add Mary /email mary@example.com /phone 98765432
```
**Expected Output:**
```
     New friend. I'm friends with everyone. Added:
       Mary (phone: 98765432, email: mary@example.com)
     2 contacts. I know everyone. Everyone knows me.
```

## Test Case: Add a contact with a name only

**Aim:** Both optional fields may be left out, and the display then shows just the name, with no empty brackets.
**Input:**
```
contact add Alex Tan
```
**Expected Output:**
```
     New friend. I'm friends with everyone. Added:
       Alex Tan
     3 contacts. I know everyone. Everyone knows me.
```

## Test Case: Contact list after adding three

**Aim:** `contact list` numbers the contacts from 1 and shows each with only the fields it has.
**Input:**
```
contact list
```
**Expected Output:**
```
     Here are the contacts in your list:
     1.John (phone: 91234567, email: john@example.com)
     2.Mary (phone: 98765432, email: mary@example.com)
     3.Alex Tan
```

## Test Case: Contact list with an argument

**Aim:** `contact list` takes nothing after it, so an argument is rejected rather than ignored.
**Input:**
```
contact list everything
```
**Expected Output:**
```
     No. GOD. NO. 'contact list' doesn't need anything after it. Just 'contact list'. Simple.
```

## Test Case: Contact find by name

**Aim:** `contact find <keyword>` matches names regardless of capitalisation.
**Input:**
```
contact find JOHN
```
**Expected Output:**
```
     Found them. This is my office, I know everyone:
     1.John (phone: 91234567, email: john@example.com)
```

## Test Case: Contact find never matches a phone or email

**Aim:** Searching for part of a stored phone number finds nothing, documenting that only the name is searched.
**Input:**
```
contact find 9123
```
**Expected Output:**
```
     No '9123' here. Is this someone from corporate?
```

## Test Case: Contact find without a keyword

**Aim:** `contact find` with nothing to search for is rejected.
**Input:**
```
contact find
```
**Expected Output:**
```
     No. GOD. NO. Look for who? Give me a name. e.g.: contact find john
```

## Test Case: Task find never matches a contact

**Aim:** `find john` searches only task descriptions, so it finds nothing even though a contact named John exists.
**Input:**
```
find john
```
**Expected Output:**
```
     Nothing matching 'john'. I looked. I looked so hard.
```

## Test Case: Contacts never appear in the task list

**Aim:** `list` shows the empty task list, confirming the two collections are kept apart.
**Input:**
```
list
```
**Expected Output:**
```
     Nothing on the list. Just like Toby's contribution to this office.
```

## Test Case: Contact delete with a number past the end

**Aim:** A contact number nobody has is rejected, and the message says how many there are.
**Input:**
```
contact delete 9
```
**Expected Output:**
```
     No. GOD. NO. Contact 9? There are 3. And I know all of them personally.
```

## Test Case: Contact delete with something that isn't a number

**Aim:** A word where a contact number belongs is rejected.
**Input:**
```
contact delete two
```
**Expected Output:**
```
     No. GOD. NO. 'two' is not a number. I know numbers. I run a branch.
```

## Test Case: Contact delete

**Aim:** `contact delete <number>` removes the contact the user numbered and reports how many are left.
**Input:**
```
contact delete 2
```
**Expected Output:**
```
     Dead to me. Removed:
       Mary (phone: 98765432, email: mary@example.com)
     2 contacts. I know everyone. Everyone knows me.
```

## Test Case: Contact list after deleting

**Aim:** The remaining contacts are renumbered from 1, so the deleted contact's number is reused.
**Input:**
```
contact list
```
**Expected Output:**
```
     Here are the contacts in your list:
     1.John (phone: 91234567, email: john@example.com)
     2.Alex Tan
```

## Test Case: Delete the first remaining contact

**Aim:** Starts draining the contact list so the plan still leaves an empty contacts file behind.
**Input:**
```
contact delete 1
```
**Expected Output:**
```
     Dead to me. Removed:
       John (phone: 91234567, email: john@example.com)
     1 contacts. I know everyone. Everyone knows me.
```

## Test Case: Delete the last remaining contact

**Aim:** Removes the final contact, returning the contact list to empty before the closing cases.
**Input:**
```
contact delete 1
```
**Expected Output:**
```
     Dead to me. Removed:
       Alex Tan
     0 contacts. I know everyone. Everyone knows me.
```

## Test Case: Leading whitespace on a command
**Aim:** ` list` (a leading space before an otherwise valid command) is the same as `list`, since the line is trimmed before the command word is matched.
**Input:**
```
 list
```
**Expected Output:**
```
     Nothing on the list. Just like Toby's contribution to this office.
```

## Test Case: Trailing whitespace on a command
**Aim:** `list ` (a trailing space) is likewise the same as `list`.
**Input:**
```
list
```
**Expected Output:**
```
     Nothing on the list. Just like Toby's contribution to this office.
```

## Test Case: AI question with nothing to ask
**Aim:** `@ai` on its own asks for a question and shows an example, without reaching for the AI.
**Input:**
```
@ai
```
**Expected Output:**
```
     No. GOD. NO. Ask me what? e.g.: @ai how do I add a deadline?
```

## Test Case: AI question in the console session
**Aim:** The console session has no AI (only the window version wires one in), so `@ai` reports that instead of crashing.
**Input:**
```
@ai how do I add a task?
```
**Expected Output:**
```
     No. GOD. NO. There's no AI here. It lives in the window version of me.
```

## Test Case: AI request with nothing to do
**Aim:** `@do` on its own asks for a request and shows an example, without reaching for the AI.
**Input:**
```
@do
```
**Expected Output:**
```
     No. GOD. NO. Do what? Say it like you'd say it to me. e.g.: @do remind me to call Pam
```

## Test Case: AI request in the console session
**Aim:** `@do` likewise reports that there is no AI, and adds nothing.
**Input:**
```
@do remind me to read a book
```
**Expected Output:**
```
     No. GOD. NO. There's no AI here. It lives in the window version of me.
```

## Test Case: List after an AI request
**Aim:** The failed `@do` left the task list untouched.
**Input:**
```
list
```
**Expected Output:**
```
     Nothing on the list. Just like Toby's contribution to this office.
```

## Test Case: AI command word run into its text
**Aim:** `@aihello` is not `@ai hello`; with no space after it, the word is not recognised.
**Input:**
```
@aihello
```
**Expected Output:**
```
     No. GOD. NO. I don't know what that means. Is this a Jim thing? Is Jim doing a thing?
```

## Test Case: Exit
**Aim:** `bye` prints the farewell message and ends the session.
**Input:**
```
bye
```
**Expected Output:**
```
     See ya tomorrow, boss.
```
