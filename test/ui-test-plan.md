# Pip UI test plan

Run this plan with `powershell -NoProfile -ExecutionPolicy Bypass -File .codex/skills/test-ui/scripts/run-ui-tests.ps1`. Expected-output blocks contain the complete program output; the prompt and divider appear on the same line because the application uses `print` for the prompt. A `â ` represents one required trailing space.

## Test: Todo lifecycle

Aim: Verify that a todo can be added, listed, marked, unmarked, and displayed with its completion status.

Inputs:
```text
t borrow book
l
m 1
l
um 1
l
b

```

Expected output:

```text
PIP | Small paws. Big plans.
____________________________________________________________

Hey there! I'm Pip, your chipmunk task buddy.
Small paws. Big plans. Let's chip away at your tasks!
____________________________________________________________

What shall we chip away at? ____________________________________________________________

Acorn secured! I've added this task:
   [T][ ] borrow book
Now you have 1 task(s) in your stash.
____________________________________________________________

What shall we chip away at? ____________________________________________________________

Here is your task stash:
1.[T][ ] borrow book
____________________________________________________________

What shall we chip away at? ____________________________________________________________

Nice nibbling! I've marked this task as done:
[T][X] borrow book
____________________________________________________________

What shall we chip away at? ____________________________________________________________

Here is your task stash:
1.[T][X] borrow book
____________________________________________________________

What shall we chip away at? ____________________________________________________________

Back in the stash! I've marked this task as not done yet:
[T][ ] borrow book
____________________________________________________________

What shall we chip away at? ____________________________________________________________

Here is your task stash:
1.[T][ ] borrow book
____________________________________________________________

What shall we chip away at? ____________________________________________________________

Scampering off! See you next time, task buddy.

```

## Test: Deadline and event details

Aim: Verify that deadline and event date/time text is preserved exactly and each task displays its type-specific details.

Inputs:

```text
deadline do homework /by 2019-10-15
event /to 2019-10-17 project meeting /from 2019-10-16
list
bye

```

Expected output:

```text
PIP | Small paws. Big plans.
____________________________________________________________

Hey there! I'm Pip, your chipmunk task buddy.
Small paws. Big plans. Let's chip away at your tasks!
____________________________________________________________

What shall we chip away at? ____________________________________________________________

Acorn secured! I've added this task:
   [D][ ] do homework (by: Oct 15 2019)
Now you have 1 task(s) in your stash.
____________________________________________________________

What shall we chip away at? ____________________________________________________________

Acorn secured! I've added this task:
   [E][ ] project meeting (from: Oct 16 2019, to: Oct 17 2019)
Now you have 2 task(s) in your stash.
____________________________________________________________

What shall we chip away at? ____________________________________________________________

Here is your task stash:
1.[D][ ] do homework (by: Oct 15 2019)
2.[E][ ] project meeting (from: Oct 16 2019, to: Oct 17 2019)
____________________________________________________________

What shall we chip away at? ____________________________________________________________

Scampering off! See you next time, task buddy.

```

## Test: Invalid command feedback

Aim: Verify that malformed task commands, an invalid task index, and an unknown command show their current guidance without adding tasks.

Inputs:

```text
todo
deadline return book
event project meeting /from Mon 2pm
mark 1
remind me
bye

```

Expected output:

```text
PIP | Small paws. Big plans.
____________________________________________________________

Hey there! I'm Pip, your chipmunk task buddy.
Small paws. Big plans. Let's chip away at your tasks!
____________________________________________________________

What shall we chip away at? ____________________________________________________________

Sorry! Please follow the format: todo DESCRIPTION
____________________________________________________________

What shall we chip away at? ____________________________________________________________

Sorry! Please follow the format: deadline DESCRIPTION /by DATE
____________________________________________________________

What shall we chip away at? ____________________________________________________________

Sorry! Please follow the format: event DESCRIPTION /from START /to END
____________________________________________________________

What shall we chip away at? ____________________________________________________________

Sorry! Invalid task index!
____________________________________________________________

What shall we chip away at? ____________________________________________________________

Sorry! Unknown task type: remind
____________________________________________________________

What shall we chip away at? ____________________________________________________________

Scampering off! See you next time, task buddy.

```

## Test: Delete task lifecycle

Aim: Verify that deleting a task removes it, updates the task count and numbering, and rejects an index beyond the list.

Inputs:

```text
todo first task
todo second task
delete 1
list
delete 5
bye

```

Expected output:

```text
PIP | Small paws. Big plans.
____________________________________________________________

Hey there! I'm Pip, your chipmunk task buddy.
Small paws. Big plans. Let's chip away at your tasks!
____________________________________________________________

What shall we chip away at? ____________________________________________________________

Acorn secured! I've added this task:
   [T][ ] first task
Now you have 1 task(s) in your stash.
____________________________________________________________

What shall we chip away at? ____________________________________________________________

Acorn secured! I've added this task:
   [T][ ] second task
Now you have 2 task(s) in your stash.
____________________________________________________________

What shall we chip away at? ____________________________________________________________

Cleared from the stash! I've removed this task:
   [T][ ] first task
Now you have 1 task(s) in your stash.
____________________________________________________________

What shall we chip away at? ____________________________________________________________

Here is your task stash:
1.[T][ ] second task
____________________________________________________________

What shall we chip away at? ____________________________________________________________

Sorry! Invalid task index!
____________________________________________________________

What shall we chip away at? ____________________________________________________________

Scampering off! See you next time, task buddy.

```

## Test: Load saved tasks

Aim: Verify that Pip loads valid todo, deadline, and event records while safely ignoring malformed saved records.

Saved tasks:

```text
T | 2 | cmVhZCBib29r
X | 0 | cmVhZCBib29r
T | 0 | not-valid-base64!
T | 0 | cmVhZCBib29r
D | 1 | cmV0dXJuIGJvb2s= | MjAxOS0wNi0wNg==
E | 0 | cHJvamVjdCBtZWV0aW5n | MjAxOS0wOC0wNg== | MjAxOS0wOC0wNw==
E | 0 | b3V0IG9mIG9yZGVy | MjAxOS0wOC0wNw== | MjAxOS0wOC0wNg==

```

Inputs:

```text
list
bye

```

Expected output:

```text
PIP | Small paws. Big plans.
____________________________________________________________

Hey there! I'm Pip, your chipmunk task buddy.
Small paws. Big plans. Let's chip away at your tasks!
____________________________________________________________

What shall we chip away at? ____________________________________________________________

Here is your task stash:
1.[T][ ] read book
2.[D][X] return book (by: Jun 06 2019)
3.[E][ ] project meeting (from: Aug 06 2019, to: Aug 07 2019)
____________________________________________________________

What shall we chip away at? ____________________________________________________________

Scampering off! See you next time, task buddy.

```

## Test: Invalid input edge cases

Aim: Verify that blank input, invalid task indexes, incomplete task details, and commands with unexpected arguments show guidance without terminating Pip.

Inputs:

```text

todo one task
mark zero
mark 0
delete -1
deadline /by Friday
deadline study /by 2019-02-29
event meeting /from /to 4pm
event meeting /from 2019-10-16 /to 2019-02-29
event meeting /from 2019-10-17 /to 2019-10-16
list extra
bye later
bye

```

Expected output:

```text
PIP | Small paws. Big plans.
____________________________________________________________

Hey there! I'm Pip, your chipmunk task buddy.
Small paws. Big plans. Let's chip away at your tasks!
____________________________________________________________

What shall we chip away at? ____________________________________________________________

Sorry! Please enter a command.
____________________________________________________________

What shall we chip away at? ____________________________________________________________

Acorn secured! I've added this task:
   [T][ ] one task
Now you have 1 task(s) in your stash.
____________________________________________________________

What shall we chip away at? ____________________________________________________________

Sorry! Invalid task index!
____________________________________________________________

What shall we chip away at? ____________________________________________________________

Sorry! Invalid task index!
____________________________________________________________

What shall we chip away at? ____________________________________________________________

Sorry! Invalid task index!
____________________________________________________________

What shall we chip away at? ____________________________________________________________

Sorry! Please follow the format: deadline DESCRIPTION /by DATE
____________________________________________________________

What shall we chip away at? ____________________________________________________________

Sorry! Please use a valid date in the format yyyy-MM-dd.
____________________________________________________________

What shall we chip away at? ____________________________________________________________

Sorry! Please follow the format: event DESCRIPTION /from START /to END
____________________________________________________________

What shall we chip away at? ____________________________________________________________

Sorry! Please use valid dates in the format yyyy-MM-dd.
____________________________________________________________

What shall we chip away at? ____________________________________________________________

Sorry! The event end date must not be before its start date.
____________________________________________________________

What shall we chip away at? ____________________________________________________________

Sorry! Please follow the format: list
____________________________________________________________

What shall we chip away at? ____________________________________________________________

Sorry! Please follow the format: bye
____________________________________________________________

What shall we chip away at? ____________________________________________________________

Scampering off! See you next time, task buddy.

```

## Test: Save changed task list

Aim: Verify that task-list changes can be completed without changing the existing console feedback.

Inputs:

```text
todo save this task
mark 1
delete 1
bye

```

Expected output:

```text
PIP | Small paws. Big plans.
____________________________________________________________

Hey there! I'm Pip, your chipmunk task buddy.
Small paws. Big plans. Let's chip away at your tasks!
____________________________________________________________

What shall we chip away at? ____________________________________________________________

Acorn secured! I've added this task:
   [T][ ] save this task
Now you have 1 task(s) in your stash.
____________________________________________________________

What shall we chip away at? ____________________________________________________________

Nice nibbling! I've marked this task as done:
[T][X] save this task
____________________________________________________________

What shall we chip away at? ____________________________________________________________

Cleared from the stash! I've removed this task:
   [T][X] save this task
Now you have 0 task(s) in your stash.
____________________________________________________________

What shall we chip away at? ____________________________________________________________

Scampering off! See you next time, task buddy.

```

## GUI appearance checks (manual)

- At 380 × 360 and 520 × 680, and when enlarged, check that long commands and replies wrap without horizontal scrolling and the composer stays visible.
- Send `list`: the user message has a right-aligned neutral bubble; Pip has a 28px circular DaPip.png avatar and a wide, plain reply.
- Send `unknown`: the reply has a red accent, tinted background, and a Command error label. Send `list` again: normal styling returns.
- Check that blank input disables Send, Enter submits a command, and keyboard focus returns to the input after clicking Send.
- Scroll up through a long conversation, then send a command: the newest response becomes visible.
- User avatar: check that DaUser.jpg appears as a 28px circle to the right of each user bubble, without stretching or clipping message text at the minimum window width.
- Avatar consistency: Pip replies and the top bar show DaPip.png with the same 28px circular crop as the user avatar; the top bar no longer shows an F placeholder.

## Test: Woodland command aliases

Aim: Verify Pip's greeting and woodland aliases for adding, listing, finding, and exiting.

Inputs:
```text
gather collect acorns
stash
sniff acorns
scamper
```

Expected output:
```text
PIP | Small paws. Big plans.
____________________________________________________________

Hey there! I'm Pip, your chipmunk task buddy.
Small paws. Big plans. Let's chip away at your tasks!
____________________________________________________________

What shall we chip away at? ____________________________________________________________

Acorn secured! I've added this task:
    [T][ ] collect acorns
Now you have 1 task(s) in your stash.
____________________________________________________________

What shall we chip away at? ____________________________________________________________

Here is your task stash:
1.[T][ ] collect acorns
____________________________________________________________

What shall we chip away at? ____________________________________________________________

Sniffed them out! Here are your matching tasks:
1.[T][ ] collect acorns
____________________________________________________________

What shall we chip away at? ____________________________________________________________

Scampering off! See you next time, task buddy.
```
- Startup error: use an unreadable data/Pip.txt in an isolated working folder; the GUI shows the loading warning after the greeting and remains usable. Missing files start empty without an error.
- Save error: make the data path unwritable; a task change produces a highlighted message explaining that it is in memory only. Restore write access and verify a subsequent task change saves the stash.
