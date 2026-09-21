# Twizzy UI Test Plan

Run these cases with the project-specific `$test-ui` skill. Each case starts a fresh Twizzy process, uses Java 25, and compares standard output exactly. `{{STARTUP}}` expands to the banner and greeting, while `{{DIVIDER}}` expands to the 60-character separator.

Within a case, `{{RESTART}}` stops Twizzy and starts it again in the same temporary directory so saved data can be checked.

For GUI-only visual behavior, manually verify that the application uses a dark background; bot bubbles are a distinct dark blue-grey, user bubbles are deep blue with legible bold white text, and a rejected command begins with “Nah, you gotta lock in, gang.” in an amber alert bubble with a left accent. Also verify that bot text uses PT Mono at 16px while user text uses Avenir Next at 13px, and that widening the window widens long message bubbles. The console cases below continue to verify the corresponding command responses.

## TC1: Exit the chatbot

**Aim:** Verify that `bye` prints the farewell and terminates normally.

**Inputs:**

```text
bye
```

**Expected output:**

```text
{{STARTUP}}
{{DIVIDER}}
Catch you later, broski. Don't ghost your tasks.
{{DIVIDER}}
```

## TC12: Find matching tasks

**Aim:** Verify that find returns case-insensitive description matches in task-list order and rejects a missing keyword.

**Inputs:**

```text
todo read book
deadline return BOOK /by 2026-09-20
event club meeting /from 2026-09-21 /to 2026-09-22
find book
find
find absent
bye
```

**Expected output:**

```text
{{STARTUP}}
{{DIVIDER}}
Locked in, gang. I added:
  ◷ read book
You're juggling 1 task now, twin.
{{DIVIDER}}
{{DIVIDER}}
Locked in, gang. I added:
  ◷ return BOOK
  ↳ Due: 20 Sep 2026
You're juggling 2 tasks now, twin.
{{DIVIDER}}
{{DIVIDER}}
Locked in, gang. I added:
  ◷ club meeting
  ↳ Schedule: 21 Sep 2026 → 22 Sep 2026
You're juggling 3 tasks now, twin.
{{DIVIDER}}
{{DIVIDER}}
Here are the matching tasks in your list:
Todos
1. ◷ read book

Deadlines
2. ◷ return BOOK
  ↳ Due: 20 Sep 2026
{{DIVIDER}}
{{DIVIDER}}
Nah, you gotta lock in, gang. Please provide a keyword. Try: find <keyword>
{{DIVIDER}}
{{DIVIDER}}
Here are the matching tasks in your list:
No tasks matched that, twin. Try another keyword.
{{DIVIDER}}
{{DIVIDER}}
Catch you later, broski. Don't ghost your tasks.
{{DIVIDER}}
```

## TC2: Add and list tasks

**Aim:** Verify that task text is stored and listed in insertion order with pending status icons.

**Inputs:**

```text
todo read book
todo return book
list
bye
```

**Expected output:**

```text
{{STARTUP}}
{{DIVIDER}}
Locked in, gang. I added:
  ◷ read book
You're juggling 1 task now, twin.
{{DIVIDER}}
{{DIVIDER}}
Locked in, gang. I added:
  ◷ return book
You're juggling 2 tasks now, twin.
{{DIVIDER}}
{{DIVIDER}}
Your active tasks, gang:
Todos
1. ◷ read book
2. ◷ return book
{{DIVIDER}}
{{DIVIDER}}
Catch you later, broski. Don't ghost your tasks.
{{DIVIDER}}
```

## TC3: Mark and unmark a task

**Aim:** Verify that marking and unmarking update the selected task and persist in subsequent lists.

**Inputs:**

```text
todo read book
todo return book
mark 2
list
unmark 2
list
bye
```

**Expected output:**

```text
{{STARTUP}}
{{DIVIDER}}
Locked in, gang. I added:
  ◷ read book
You're juggling 1 task now, twin.
{{DIVIDER}}
{{DIVIDER}}
Locked in, gang. I added:
  ◷ return book
You're juggling 2 tasks now, twin.
{{DIVIDER}}
{{DIVIDER}}
Marked done, twin:
  ★ return book
{{DIVIDER}}
{{DIVIDER}}
Your active tasks, gang:
Todos
1. ◷ read book
2. ★ return book
{{DIVIDER}}
{{DIVIDER}}
Marked pending, gang:
  ◷ return book
{{DIVIDER}}
{{DIVIDER}}
Your active tasks, gang:
Todos
1. ◷ read book
2. ◷ return book
{{DIVIDER}}
{{DIVIDER}}
Catch you later, broski. Don't ghost your tasks.
{{DIVIDER}}
```

## TC4: Add deadlines and events

**Aim:** Verify valid deadline and event dates are parsed and displayed in a friendlier format.

**Inputs:**

```text
deadline do homework /by 2026-09-18
event project meeting /from 2026-09-21 /to 2026-09-22
list
bye
```

**Expected output:**

```text
{{STARTUP}}
{{DIVIDER}}
Locked in, gang. I added:
  ◷ do homework
  ↳ Due: 18 Sep 2026
You're juggling 1 task now, twin.
{{DIVIDER}}
{{DIVIDER}}
Locked in, gang. I added:
  ◷ project meeting
  ↳ Schedule: 21 Sep 2026 → 22 Sep 2026
You're juggling 2 tasks now, twin.
{{DIVIDER}}
{{DIVIDER}}
Your active tasks, gang:
Deadlines
1. ◷ do homework
  ↳ Due: 18 Sep 2026

Events
2. ◷ project meeting
  ↳ Schedule: 21 Sep 2026 → 22 Sep 2026
{{DIVIDER}}
{{DIVIDER}}
Catch you later, broski. Don't ghost your tasks.
{{DIVIDER}}
```

## TC5: Reject an empty todo and unknown command

**Aim:** Verify that invalid commands show helpful errors and do not change tasks added before or after them.

**Inputs:**

```text
todo first task
todo
blah
todo second task
list
bye
```

**Expected output:**

```text
{{STARTUP}}
{{DIVIDER}}
Locked in, gang. I added:
  ◷ first task
You're juggling 1 task now, twin.
{{DIVIDER}}
{{DIVIDER}}
Nah, you gotta lock in, gang. A todo needs a description. Try: todo <description>
{{DIVIDER}}
{{DIVIDER}}
Nah, you gotta lock in, gang. I don't recognize that command. Try todo, deadline, event, find, list, mark, unmark, delete, snooze, unsnooze, or bye.
{{DIVIDER}}
{{DIVIDER}}
Locked in, gang. I added:
  ◷ second task
You're juggling 2 tasks now, twin.
{{DIVIDER}}
{{DIVIDER}}
Your active tasks, gang:
Todos
1. ◷ first task
2. ◷ second task
{{DIVIDER}}
{{DIVIDER}}
Catch you later, broski. Don't ghost your tasks.
{{DIVIDER}}
```

## TC6: Reject malformed deadlines and events

**Aim:** Verify specific errors for every missing deadline or event field and confirm later valid timed tasks are stored correctly.

**Inputs:**

```text
deadline
deadline /by Sunday
deadline report /by
deadline report /bypass
event
event /from Mon /to Tue
event meeting /from Mon
event meeting /from /to Tue
event meeting /from Mon /to
event lunch /fromage /toffee
event lunch /from Mon /today
deadline impossible /by 2025-02-29
event meeting /from Monday /to 2026-09-22
event meeting /from 2026-09-21 /to tomorrow
deadline report /by 2024-02-29
event meeting /from 2026-09-21 /to 2026-09-22
list
bye
```

**Expected output:**

```text
{{STARTUP}}
{{DIVIDER}}
Nah, you gotta lock in, gang. A deadline needs /by followed by a date.
{{DIVIDER}}
{{DIVIDER}}
Nah, you gotta lock in, gang. A deadline needs a description. Try: deadline <description> /by <date>
{{DIVIDER}}
{{DIVIDER}}
Nah, you gotta lock in, gang. The deadline date cannot be empty after /by.
{{DIVIDER}}
{{DIVIDER}}
Nah, you gotta lock in, gang. A deadline needs /by followed by a date.
{{DIVIDER}}
{{DIVIDER}}
Nah, you gotta lock in, gang. An event needs /from followed by a start date.
{{DIVIDER}}
{{DIVIDER}}
Nah, you gotta lock in, gang. An event needs a description. Try: event <description> /from <start-date> /to <end-date>
{{DIVIDER}}
{{DIVIDER}}
Nah, you gotta lock in, gang. An event needs /to followed by an end date.
{{DIVIDER}}
{{DIVIDER}}
Nah, you gotta lock in, gang. The event start date cannot be empty after /from.
{{DIVIDER}}
{{DIVIDER}}
Nah, you gotta lock in, gang. The event end date cannot be empty after /to.
{{DIVIDER}}
{{DIVIDER}}
Nah, you gotta lock in, gang. An event needs /from followed by a start date.
{{DIVIDER}}
{{DIVIDER}}
Nah, you gotta lock in, gang. An event needs /to followed by an end date.
{{DIVIDER}}
{{DIVIDER}}
Nah, you gotta lock in, gang. The deadline date must be a valid date in yyyy-MM-dd format.
{{DIVIDER}}
{{DIVIDER}}
Nah, you gotta lock in, gang. The event start date must be a valid date in yyyy-MM-dd format.
{{DIVIDER}}
{{DIVIDER}}
Nah, you gotta lock in, gang. The event end date must be a valid date in yyyy-MM-dd format.
{{DIVIDER}}
{{DIVIDER}}
Locked in, gang. I added:
  ◷ report
  ↳ Due: 29 Feb 2024
You're juggling 1 task now, twin.
{{DIVIDER}}
{{DIVIDER}}
Locked in, gang. I added:
  ◷ meeting
  ↳ Schedule: 21 Sep 2026 → 22 Sep 2026
You're juggling 2 tasks now, twin.
{{DIVIDER}}
{{DIVIDER}}
Your active tasks, gang:
Deadlines
1. ◷ report
  ↳ Due: 29 Feb 2024

Events
2. ◷ meeting
  ↳ Schedule: 21 Sep 2026 → 22 Sep 2026
{{DIVIDER}}
{{DIVIDER}}
Catch you later, broski. Don't ghost your tasks.
{{DIVIDER}}
```

## TC7: Reject invalid task selections

**Aim:** Verify empty input and invalid mark or unmark numbers are handled without changing valid task state.

**Inputs:**

```text

mark
unmark 1
todo stable task
mark two
mark 0
mark 2
unmark
unmark 1 extra
unmark 2
mark 1
list
bye
```

**Expected output:**

```text
{{STARTUP}}
{{DIVIDER}}
Nah, you gotta lock in, gang. Please enter a command.
{{DIVIDER}}
{{DIVIDER}}
Nah, you gotta lock in, gang. Please provide a task number. Try: mark <number>
{{DIVIDER}}
{{DIVIDER}}
Nah, you gotta lock in, gang. There are no tasks to unmark.
{{DIVIDER}}
{{DIVIDER}}
Locked in, gang. I added:
  ◷ stable task
You're juggling 1 task now, twin.
{{DIVIDER}}
{{DIVIDER}}
Nah, you gotta lock in, gang. The task number must be a whole number.
{{DIVIDER}}
{{DIVIDER}}
Nah, you gotta lock in, gang. Choose a task number from 1 to 1.
{{DIVIDER}}
{{DIVIDER}}
Nah, you gotta lock in, gang. Choose a task number from 1 to 1.
{{DIVIDER}}
{{DIVIDER}}
Nah, you gotta lock in, gang. Please provide a task number. Try: unmark <number>
{{DIVIDER}}
{{DIVIDER}}
Nah, you gotta lock in, gang. The task number must be a whole number.
{{DIVIDER}}
{{DIVIDER}}
Nah, you gotta lock in, gang. Choose a task number from 1 to 1.
{{DIVIDER}}
{{DIVIDER}}
Marked done, twin:
  ★ stable task
{{DIVIDER}}
{{DIVIDER}}
Your active tasks, gang:
Todos
1. ★ stable task
{{DIVIDER}}
{{DIVIDER}}
Catch you later, broski. Don't ghost your tasks.
{{DIVIDER}}
```

## TC8: Delete and renumber tasks

**Aim:** Verify deletion of middle, first, and last tasks, including the removed-task message, count, status, and list renumbering.

**Inputs:**

```text
todo read book
deadline return book /by 2026-09-20
event project meeting /from 2026-09-21 /to 2026-09-22
todo join club
mark 3
delete 3
list
delete 1
delete 2
list
bye
```

**Expected output:**

```text
{{STARTUP}}
{{DIVIDER}}
Locked in, gang. I added:
  ◷ read book
You're juggling 1 task now, twin.
{{DIVIDER}}
{{DIVIDER}}
Locked in, gang. I added:
  ◷ return book
  ↳ Due: 20 Sep 2026
You're juggling 2 tasks now, twin.
{{DIVIDER}}
{{DIVIDER}}
Locked in, gang. I added:
  ◷ project meeting
  ↳ Schedule: 21 Sep 2026 → 22 Sep 2026
You're juggling 3 tasks now, twin.
{{DIVIDER}}
{{DIVIDER}}
Locked in, gang. I added:
  ◷ join club
You're juggling 4 tasks now, twin.
{{DIVIDER}}
{{DIVIDER}}
Marked done, twin:
  ★ return book
  ↳ Due: 20 Sep 2026
{{DIVIDER}}
{{DIVIDER}}
Deleted, broski:
  ★ return book
  ↳ Due: 20 Sep 2026
You're juggling 3 tasks now, twin.
{{DIVIDER}}
{{DIVIDER}}
Your active tasks, gang:
Todos
1. ◷ read book
2. ◷ join club

Events
3. ◷ project meeting
  ↳ Schedule: 21 Sep 2026 → 22 Sep 2026
{{DIVIDER}}
{{DIVIDER}}
Deleted, broski:
  ◷ read book
You're juggling 2 tasks now, twin.
{{DIVIDER}}
{{DIVIDER}}
Deleted, broski:
  ◷ project meeting
  ↳ Schedule: 21 Sep 2026 → 22 Sep 2026
You're juggling 1 task now, twin.
{{DIVIDER}}
{{DIVIDER}}
Your active tasks, gang:
Todos
1. ◷ join club
{{DIVIDER}}
{{DIVIDER}}
Catch you later, broski. Don't ghost your tasks.
{{DIVIDER}}
```

## TC9: Reject invalid deletions

**Aim:** Verify missing, malformed, and out-of-range delete numbers do not remove tasks and deletion from an empty list is handled.

**Inputs:**

```text
delete
delete 1
todo safe task
delete zero
delete 0
delete 2
delete 1 extra
list
delete 1
list
delete 1
bye
```

**Expected output:**

```text
{{STARTUP}}
{{DIVIDER}}
Nah, you gotta lock in, gang. Please provide a task number. Try: delete <number>
{{DIVIDER}}
{{DIVIDER}}
Nah, you gotta lock in, gang. There are no tasks to delete.
{{DIVIDER}}
{{DIVIDER}}
Locked in, gang. I added:
  ◷ safe task
You're juggling 1 task now, twin.
{{DIVIDER}}
{{DIVIDER}}
Nah, you gotta lock in, gang. The task number must be a whole number.
{{DIVIDER}}
{{DIVIDER}}
Nah, you gotta lock in, gang. Choose a task number from 1 to 1.
{{DIVIDER}}
{{DIVIDER}}
Nah, you gotta lock in, gang. Choose a task number from 1 to 1.
{{DIVIDER}}
{{DIVIDER}}
Nah, you gotta lock in, gang. The task number must be a whole number.
{{DIVIDER}}
{{DIVIDER}}
Your active tasks, gang:
Todos
1. ◷ safe task
{{DIVIDER}}
{{DIVIDER}}
Deleted, broski:
  ◷ safe task
You're juggling 0 tasks now, twin.
{{DIVIDER}}
{{DIVIDER}}
Your active tasks, gang:
No active tasks yet, twin. Add one with todo <description>.
{{DIVIDER}}
{{DIVIDER}}
Nah, you gotta lock in, gang. There are no tasks to delete.
{{DIVIDER}}
{{DIVIDER}}
Catch you later, broski. Don't ghost your tasks.
{{DIVIDER}}
```

## TC10: Reject command keyword prefixes

**Aim:** Verify command-like prefixes are not mistaken for supported enum command types and do not affect later valid input.

**Inputs:**

```text
todoing read book
listing
marking 1
deleting 1
list extra
bye now
todo valid task
list
bye
```

**Expected output:**

```text
{{STARTUP}}
{{DIVIDER}}
Nah, you gotta lock in, gang. I don't recognize that command. Try todo, deadline, event, find, list, mark, unmark, delete, snooze, unsnooze, or bye.
{{DIVIDER}}
{{DIVIDER}}
Nah, you gotta lock in, gang. I don't recognize that command. Try todo, deadline, event, find, list, mark, unmark, delete, snooze, unsnooze, or bye.
{{DIVIDER}}
{{DIVIDER}}
Nah, you gotta lock in, gang. I don't recognize that command. Try todo, deadline, event, find, list, mark, unmark, delete, snooze, unsnooze, or bye.
{{DIVIDER}}
{{DIVIDER}}
Nah, you gotta lock in, gang. I don't recognize that command. Try todo, deadline, event, find, list, mark, unmark, delete, snooze, unsnooze, or bye.
{{DIVIDER}}
{{DIVIDER}}
Nah, you gotta lock in, gang. I don't recognize that command. Try todo, deadline, event, find, list, mark, unmark, delete, snooze, unsnooze, or bye.
{{DIVIDER}}
{{DIVIDER}}
Nah, you gotta lock in, gang. I don't recognize that command. Try todo, deadline, event, find, list, mark, unmark, delete, snooze, unsnooze, or bye.
{{DIVIDER}}
{{DIVIDER}}
Locked in, gang. I added:
  ◷ valid task
You're juggling 1 task now, twin.
{{DIVIDER}}
{{DIVIDER}}
Your active tasks, gang:
Todos
1. ◷ valid task
{{DIVIDER}}
{{DIVIDER}}
Catch you later, broski. Don't ghost your tasks.
{{DIVIDER}}
```

## TC11: Save and reload tasks

**Aim:** Verify todos, deadlines, events, and completion state survive a restart, and later changes remain persistent.

**Inputs:**

```text
todo read A | B
deadline submit work /by 2026-09-18
event consultation /from 2026-09-21 /to 2026-09-22
mark 2
bye
{{RESTART}}
list
delete 1
unmark 1
bye
{{RESTART}}
list
bye
```

**Expected output:**

```text
{{STARTUP}}
{{DIVIDER}}
Locked in, gang. I added:
  ◷ read A | B
You're juggling 1 task now, twin.
{{DIVIDER}}
{{DIVIDER}}
Locked in, gang. I added:
  ◷ submit work
  ↳ Due: 18 Sep 2026
You're juggling 2 tasks now, twin.
{{DIVIDER}}
{{DIVIDER}}
Locked in, gang. I added:
  ◷ consultation
  ↳ Schedule: 21 Sep 2026 → 22 Sep 2026
You're juggling 3 tasks now, twin.
{{DIVIDER}}
{{DIVIDER}}
Marked done, twin:
  ★ submit work
  ↳ Due: 18 Sep 2026
{{DIVIDER}}
{{DIVIDER}}
Catch you later, broski. Don't ghost your tasks.
{{DIVIDER}}
{{STARTUP}}
{{DIVIDER}}
Your active tasks, gang:
Todos
1. ◷ read A | B

Deadlines
2. ★ submit work
  ↳ Due: 18 Sep 2026

Events
3. ◷ consultation
  ↳ Schedule: 21 Sep 2026 → 22 Sep 2026
{{DIVIDER}}
{{DIVIDER}}
Deleted, broski:
  ◷ read A | B
You're juggling 2 tasks now, twin.
{{DIVIDER}}
{{DIVIDER}}
Marked pending, gang:
  ◷ submit work
  ↳ Due: 18 Sep 2026
{{DIVIDER}}
{{DIVIDER}}
Catch you later, broski. Don't ghost your tasks.
{{DIVIDER}}
{{STARTUP}}
{{DIVIDER}}
Your active tasks, gang:
Deadlines
1. ◷ submit work
  ↳ Due: 18 Sep 2026

Events
2. ◷ consultation
  ↳ Schedule: 21 Sep 2026 → 22 Sep 2026
{{DIVIDER}}
{{DIVIDER}}
Catch you later, broski. Don't ghost your tasks.
{{DIVIDER}}
```

## TC13: Snooze tasks until a future date

**Aim:** Verify snoozed tasks are hidden from active commands, remain viewable separately, reject invalid dates, and persist across restarts.

**Inputs:**

```text
todo active task
todo postponed task
snooze 2 /until 2099-12-31
list
list snoozed
find postponed
mark 2
snooze 1 /until
snooze 1 /until 2020-01-01
{{RESTART}}
list
list snoozed
bye
```

**Expected output:**

```text
{{STARTUP}}
{{DIVIDER}}
Locked in, gang. I added:
  ◷ active task
You're juggling 1 task now, twin.
{{DIVIDER}}
{{DIVIDER}}
Locked in, gang. I added:
  ◷ postponed task
You're juggling 2 tasks now, twin.
{{DIVIDER}}
{{DIVIDER}}
Snoozed, gang:
  ◷ postponed task
  ↳ Returns: 31 Dec 2099
{{DIVIDER}}
{{DIVIDER}}
Your active tasks, gang:
Todos
1. ◷ active task
{{DIVIDER}}
{{DIVIDER}}
Here are the snoozed tasks:
Todos
1. ◷ postponed task
  ↳ Returns: 31 Dec 2099
{{DIVIDER}}
{{DIVIDER}}
Here are the matching tasks in your list:
No tasks matched that, twin. Try another keyword.
{{DIVIDER}}
{{DIVIDER}}
Nah, you gotta lock in, gang. Choose a task number from 1 to 1.
{{DIVIDER}}
{{DIVIDER}}
Nah, you gotta lock in, gang. The snooze date cannot be empty after /until.
{{DIVIDER}}
{{DIVIDER}}
Nah, you gotta lock in, gang. The snooze date must be after today.
{{DIVIDER}}
{{STARTUP}}
{{DIVIDER}}
Your active tasks, gang:
Todos
1. ◷ active task
{{DIVIDER}}
{{DIVIDER}}
Here are the snoozed tasks:
Todos
1. ◷ postponed task
  ↳ Returns: 31 Dec 2099
{{DIVIDER}}
{{DIVIDER}}
Catch you later, broski. Don't ghost your tasks.
{{DIVIDER}}
```

## TC14: Show command help

**Aim:** Verify that a new user can display the supported command syntax from the application.

**Inputs:**

```text
help
bye
```

**Expected output:**

```text
{{STARTUP}}
{{DIVIDER}}
Commands, gang:
  todo <description>
  deadline <description> /by <yyyy-MM-dd>
  event <description> /from <yyyy-MM-dd> /to <yyyy-MM-dd>
  list | list snoozed | find <keyword>
  mark <number> | unmark <number> | delete <number>
  snooze <number> /until <yyyy-MM-dd> | unsnooze <number>
  bye
{{DIVIDER}}
{{DIVIDER}}
Catch you later, broski. Don't ghost your tasks.
{{DIVIDER}}
```

## TC15: Reject repeated task status changes and invalid event ranges

**Aim:** Verify that repeated mark or unmark commands explain that the task already has that status, and events cannot end on or before their start date.

**Inputs:**

```text
todo prepare slides
mark 1
mark 1
unmark 1
unmark 1
event workshop /from 2026-09-22 /to 2026-09-22
event retrospective /from 2026-09-22 /to 2026-09-21
list
bye
```

**Expected output:**

```text
{{STARTUP}}
{{DIVIDER}}
Locked in, gang. I added:
  ◷ prepare slides
You're juggling 1 task now, twin.
{{DIVIDER}}
{{DIVIDER}}
Marked done, twin:
  ★ prepare slides
{{DIVIDER}}
{{DIVIDER}}
Nah, you gotta lock in, gang. That task is already marked as done.
{{DIVIDER}}
{{DIVIDER}}
Marked pending, gang:
  ◷ prepare slides
{{DIVIDER}}
{{DIVIDER}}
Nah, you gotta lock in, gang. That task is already marked as not done.
{{DIVIDER}}
{{DIVIDER}}
Nah, you gotta lock in, gang. An event must end after it starts.
{{DIVIDER}}
{{DIVIDER}}
Nah, you gotta lock in, gang. An event must end after it starts.
{{DIVIDER}}
{{DIVIDER}}
Your active tasks, gang:
Todos
1. ◷ prepare slides
{{DIVIDER}}
{{DIVIDER}}
Catch you later, broski. Don't ghost your tasks.
{{DIVIDER}}
```

## TC16: Reject pending duplicates but allow completed tasks to be re-added

**Aim:** Verify that a pending duplicate is rejected but a completed task can be added again for future work.

**Inputs:**

```text
todo prepare slides
todo PREPARE SLIDES
mark 1
todo PREPARE SLIDES
list
bye
```

**Expected output:**

```text
{{STARTUP}}
{{DIVIDER}}
Locked in, gang. I added:
  ◷ prepare slides
You're juggling 1 task now, twin.
{{DIVIDER}}
{{DIVIDER}}
Nah, you gotta lock in, gang. That task is already on your list, gang. Use list to find it, or complete it before adding it again.
{{DIVIDER}}
{{DIVIDER}}
Marked done, twin:
  ★ prepare slides
{{DIVIDER}}
{{DIVIDER}}
Locked in, gang. I added:
  ◷ PREPARE SLIDES
You're juggling 2 tasks now, twin.
{{DIVIDER}}
{{DIVIDER}}
Your active tasks, gang:
Todos
1. ★ prepare slides
2. ◷ PREPARE SLIDES
{{DIVIDER}}
{{DIVIDER}}
Catch you later, broski. Don't ghost your tasks.
{{DIVIDER}}
```

## TC17: Unsnooze a task

**Aim:** Verify that `unsnooze` uses snoozed-task numbering, returns the selected task to the active list, persists the change, and rejects unavailable selections.

**Inputs:**

```text
todo active task
todo paused task
snooze 2 /until 2099-12-31
list snoozed
unsnooze 1
list
list snoozed
unsnooze 1
unsnooze
bye
```

**Expected output:**

```text
{{STARTUP}}
{{DIVIDER}}
Locked in, gang. I added:
  ◷ active task
You're juggling 1 task now, twin.
{{DIVIDER}}
{{DIVIDER}}
Locked in, gang. I added:
  ◷ paused task
You're juggling 2 tasks now, twin.
{{DIVIDER}}
{{DIVIDER}}
Snoozed, gang:
  ◷ paused task
  ↳ Returns: 31 Dec 2099
{{DIVIDER}}
{{DIVIDER}}
Here are the snoozed tasks:
Todos
1. ◷ paused task
  ↳ Returns: 31 Dec 2099
{{DIVIDER}}
{{DIVIDER}}
Unsnoozed, twin:
  ◷ paused task
{{DIVIDER}}
{{DIVIDER}}
Your active tasks, gang:
Todos
1. ◷ active task
2. ◷ paused task
{{DIVIDER}}
{{DIVIDER}}
Here are the snoozed tasks:
No snoozed tasks right now, gang.
{{DIVIDER}}
{{DIVIDER}}
Nah, you gotta lock in, gang. There are no tasks to unsnooze.
{{DIVIDER}}
{{DIVIDER}}
Nah, you gotta lock in, gang. Please provide a task number. Try: unsnooze <number>
{{DIVIDER}}
{{DIVIDER}}
Catch you later, broski. Don't ghost your tasks.
{{DIVIDER}}
```
