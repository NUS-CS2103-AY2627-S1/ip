# Minty A-Personality UI Test Plan

Reminder date boundaries, ordering, persistence, lifecycle changes, and console startup are tested
with a fixed clock in JUnit. TC23 checks exact console syntax and empty output.

### GUI6: Startup reminders

Using an isolated data file, save an incomplete deadline due today, then restart Minty.
Expected: one separate reminder bubble follows the greeting, showing the deadline's original list number.
Enter `remind` and `reminders`; both show the same text. Mark the task done, then restart:
no reminder bubble appears. Existing conversation bubbles remain unchanged after marking.

These tests run with Java 25. Each test starts a fresh instance of Minty's command-line backend and compares the
complete console output exactly. The JavaFX launcher and chat controls are verified separately with a startup smoke
test and computer-use test because rendered windows cannot be compared as console text.

## Coverage summary

| Behavior | Happy path | Error and boundary coverage |
| --- | --- | --- |
| `todo` | TC1, TC8–TC10 | TC4 |
| `deadline` | TC1, TC5, TC6, TC9, TC10 | TC6 |
| `event` | TC1, TC5, TC7, TC9, TC10 | TC7 |
| `list` | TC1, TC2, TC3, TC5–TC8, TC10 | TC3 checks an empty list |
| `mark` and `unmark` | TC1, TC2, TC8, TC9 | TC8 |
| `delete` | TC2, TC9 | TC3 |
| Unknown or empty command | — | TC4 |
| Save after task-list changes | TC9 | — |
| Missing data file and folder | TC9 | — |
| Load task types and statuses | TC10 | — |
| Blank lines and escaped delimiters | TC11 | — |
| Invalid saved task status | — | TC12 |
| Unknown saved task type | — | TC13 |
| Missing saved task fields | — | TC14 |
| Empty saved task details | — | TC15 |
| Invalid saved escape sequence | — | TC16 |
| Unsupported saved escape sequence | — | TC17 |
| Parse and format ISO dates | TC1, TC5, TC10 | TC18 |
| Invalid dates in saved data | — | TC19, TC20 |
| `on` date query | TC21 | TC21 |
| `find` keyword search | TC22 | TC22 |
| Startup and `bye` | TC1–TC22 | — |

The use of `ArrayList<Task>` is an implementation detail and is verified by code review rather than console output. The UI tests verify its observable add, lookup, renumbering, and deletion behavior.

## JavaFX GUI checks

These checks are performed through the running desktop window rather than by the console test script.

### GUI1: Send a command with the button

1. Enter `todo read book` in the command field.
2. Click **Send**.

Expected: The conversation shows the command and Minty's confirmation, and the command field is cleared.

### GUI2: Send a command with Enter

1. Enter `list` in the command field.
2. Press Enter.

Expected: Minty shows the stored task and the conversation scrolls to the newest response.

### GUI3: Recover from invalid input

1. Enter `todo` in the command field.
2. Click **Send**.

Expected: Minty says "Let's give that task a name! Try todo read a book.", and the window remains usable.

### GUI4: Exit the conversation

1. Enter `bye` in the command field.
2. Press Enter.

Expected: Minty's window closes and the application process exits normally.
Repeat after restarting, submitting `  bye  ` with Send instead of Enter; the app also closes.
Before exiting, submit `bye now` and then `list`: the invalid command must leave the window usable.

### GUI5: Display themed images

1. Start Minty and send any valid command.
2. Inspect the welcome message and both sides of the new exchange.

Expected: The botanical mint background fills the window. Minty's leaf mascot appears to the left of its messages,
and the user avatar appears to the right of user messages without obscuring the message text.

### GUI7: Identity bar and themed bubbles

1. Start Minty and inspect the header and greeting.
2. Send `list`, then an invalid command, and scroll through the conversation.
3. Resize the window down to its minimum size and then enlarge it.

Expected: A fixed cream header shows the mascot, green Minty title, and
"Your fresh little task buddy" subtitle. The conversation scrolls below it.
Minty's cream bubbles have green outlines and small tails pointing toward the
mascot; user bubbles are pale mint. Text wraps without clipping, and the header
and input remain separate from the conversation at every supported size.

### GUI8: Expressive mascot avatars

1. Add `todo read book`, then enter `mark 1`.
2. Enter `mark 99`, then `list`.
3. Inspect the avatars alongside each response.

Expected: The add and list responses use the original mascot. Successful marking
uses the celebrating mascot. The invalid task number uses the curious mascot.
Earlier bubbles keep their
original expressions. All avatars have transparent backgrounds and fit the
existing avatar space; the identity bar keeps the original mascot.

### GUI9: Friendly input and spacing

1. Focus the empty input field and inspect its hint and green outline.
2. Add a task with Enter and another with Send, then enter `list`.
3. Resize to the minimum window size, navigate with Tab, and enter `bye`.

Expected: The hint reads "What’s next? Try todo read a book". Focus has a
clear green outline; Send has hover, pressed, and keyboard-focus feedback.
Messages have comfortable padding and line spacing; task numbers are followed
by a space. The input and button fit without overlapping the conversation.
Both submission methods clear the field, and goodbye closes the application.

### GUI10: Bundled Nunito typography

1. Start Minty without installing Nunito as a system font.
2. Inspect the header, subtitle, messages, input, and Send button.
3. Add a long task and list tasks at the minimum window width.

Expected: Nunito is used throughout: ExtraBold 23px for the title, Regular
15px for messages, Regular 14px for input, Bold 14px for Send, and Regular
12px for the subtitle. Text wraps without clipping and the input hint fits.

### GUI11: Message dividers

1. Send `list`, add a task, and send `list` again.
2. Inspect both user and Minty messages at the minimum window width.

Expected: A thin muted-green horizontal divider appears below each message,
inset from the window edges, with space between the line and the bubble or
avatar. Lines span the conversation width regardless of bubble length.

### GUI12: Response colors and error emphasis

1. Add a task, mark it, and enter `list`.
2. Enter an invalid command and an invalid task number.
3. Enter `reminders`, then another valid command.

Expected: Successful task changes use pale green bubbles; list/search and
ordinary conversation use cream; reminders use pale blue. Errors use soft red
with dark-red text and a "Needs attention" heading. Speech tails match each
bubble. Subsequent responses use their own colors, and old bubbles stay unchanged.
Startup reminders also use pale blue. Save failures take priority over success.

### GUI13: Softened conversation background

1. Start Minty and compare the center of the conversation with its edges.
2. Send enough messages to scroll and resize the window.

Expected: A cream wash softens leaves through the middle of the conversation,
fading smoothly to richer foliage at both edges. The wash stays fixed behind
the scrolling messages and adapts to the viewport width. Avatars, bubbles,
header, input controls, and text retain their normal colors and opacity.

### GUI15: Existing duplicate tasks

Start Minty with a saved file containing duplicate task details, including
completed copies. Enter `list` and add a different task.

Expected: Every saved task loads in its original order and status, saving
continues normally, and no corruption warning appears. Adding another copy
of an existing task still reports the duplicate-input error.

### GUI14: Recover from input and storage errors

1. Enter a command with extra spaces or tabs and verify it is accepted.
2. Add the same task twice and repeat /by in a deadline command.
3. With an isolated malformed data file, restart the GUI and add a task.

Expected: Input errors use red bubbles and explain recovery. A startup warning
appears in a red bubble when loading fails. New tasks stay in the session, but
saving is blocked so the original malformed file is preserved. Repair or move
the file and restart to resume saving. Missing files start an empty list normally.

## TC1: Add, mark, and list all task types

Aim: Verify that todos, deadlines, and events are created with the correct details, retain their types when marked, and appear correctly in the task list.

### Input

```text
todo borrow book
deadline do homework /by 2019-12-02
event project meeting /from 2019-12-03 /to 2019-12-04
mark 2
list
bye
```

### Expected output

```text
____________________________________________________________
███╗   ███╗██╗███╗   ██╗████████╗██╗   ██╗
████╗ ████║██║████╗  ██║╚══██╔══╝╚██╗ ██╔╝
██╔████╔██║██║██╔██╗ ██║   ██║    ╚████╔╝
██║╚██╔╝██║██║██║╚██╗██║   ██║     ╚██╔╝
██║ ╚═╝ ██║██║██║ ╚████║   ██║      ██║
╚═╝     ╚═╝╚═╝╚═╝  ╚═══╝   ╚═╝      ╚═╝
  Heyyy! I'm Minty, your fresh little task buddy!
  Let's get things moving. What's on your list today?
____________________________________________________________
____________________________________________________________
  Fresh task coming right up! I've added:
    [T][ ] borrow book
  Your list now has 1 task.
____________________________________________________________
____________________________________________________________
  Fresh task coming right up! I've added:
    [D][ ] do homework (by: Dec 02 2019)
  Your list now has 2 tasks.
____________________________________________________________
____________________________________________________________
  Fresh task coming right up! I've added:
    [E][ ] project meeting (from: Dec 03 2019 to: Dec 04 2019)
  Your list now has 3 tasks.
____________________________________________________________
____________________________________________________________
  Woohoo! Marked as done:
    [D][X] do homework (by: Dec 02 2019)
____________________________________________________________
____________________________________________________________
  Let's check your lineup! Here are your tasks:
  1. [T][ ] borrow book
  2. [D][X] do homework (by: Dec 02 2019)
  3. [E][ ] project meeting (from: Dec 03 2019 to: Dec 04 2019)
____________________________________________________________
____________________________________________________________
  Stay fresh! Catch you next time!
____________________________________________________________
```

## TC2: Delete completed and renumbered tasks

Aim: Verify that deleting a completed middle task preserves its status in the confirmation, shifts later tasks to new indices, and can reduce the list to zero tasks.

### Input

```text
todo first
deadline second /by 2019-12-06
event third /from 2019-12-09 /to 2019-12-10
mark 2
delete 2
mark 2
list
delete 1
delete 1
list
bye
```

### Expected output

```text
____________________________________________________________
███╗   ███╗██╗███╗   ██╗████████╗██╗   ██╗
████╗ ████║██║████╗  ██║╚══██╔══╝╚██╗ ██╔╝
██╔████╔██║██║██╔██╗ ██║   ██║    ╚████╔╝
██║╚██╔╝██║██║██║╚██╗██║   ██║     ╚██╔╝
██║ ╚═╝ ██║██║██║ ╚████║   ██║      ██║
╚═╝     ╚═╝╚═╝╚═╝  ╚═══╝   ╚═╝      ╚═╝
  Heyyy! I'm Minty, your fresh little task buddy!
  Let's get things moving. What's on your list today?
____________________________________________________________
____________________________________________________________
  Fresh task coming right up! I've added:
    [T][ ] first
  Your list now has 1 task.
____________________________________________________________
____________________________________________________________
  Fresh task coming right up! I've added:
    [D][ ] second (by: Dec 06 2019)
  Your list now has 2 tasks.
____________________________________________________________
____________________________________________________________
  Fresh task coming right up! I've added:
    [E][ ] third (from: Dec 09 2019 to: Dec 10 2019)
  Your list now has 3 tasks.
____________________________________________________________
____________________________________________________________
  Woohoo! Marked as done:
    [D][X] second (by: Dec 06 2019)
____________________________________________________________
____________________________________________________________
  Making room! I've removed:
    [D][X] second (by: Dec 06 2019)
  Your list now has 2 tasks.
____________________________________________________________
____________________________________________________________
  Woohoo! Marked as done:
    [E][X] third (from: Dec 09 2019 to: Dec 10 2019)
____________________________________________________________
____________________________________________________________
  Let's check your lineup! Here are your tasks:
  1. [T][ ] first
  2. [E][X] third (from: Dec 09 2019 to: Dec 10 2019)
____________________________________________________________
____________________________________________________________
  Making room! I've removed:
    [T][ ] first
  Your list now has 1 task.
____________________________________________________________
____________________________________________________________
  Making room! I've removed:
    [E][X] third (from: Dec 09 2019 to: Dec 10 2019)
  Your list now has 0 tasks.
____________________________________________________________
____________________________________________________________
  A fresh start! Your list is empty. Try todo read a book to get going.
____________________________________________________________
____________________________________________________________
  Stay fresh! Catch you next time!
____________________________________________________________
```

## TC3: Handle an empty list and invalid delete arguments

Aim: Verify that listing an empty task collection is safe and that missing, non-numeric, non-positive, and out-of-range delete arguments do not change it.

### Input

```text
list
delete 1
delete
delete two
delete 0
list
bye
```

### Expected output

```text
____________________________________________________________
███╗   ███╗██╗███╗   ██╗████████╗██╗   ██╗
████╗ ████║██║████╗  ██║╚══██╔══╝╚██╗ ██╔╝
██╔████╔██║██║██╔██╗ ██║   ██║    ╚████╔╝
██║╚██╔╝██║██║██║╚██╗██║   ██║     ╚██╔╝
██║ ╚═╝ ██║██║██║ ╚████║   ██║      ██║
╚═╝     ╚═╝╚═╝╚═╝  ╚═══╝   ╚═╝      ╚═╝
  Heyyy! I'm Minty, your fresh little task buddy!
  Let's get things moving. What's on your list today?
____________________________________________________________
____________________________________________________________
  A fresh start! Your list is empty. Try todo read a book to get going.
____________________________________________________________
____________________________________________________________
  I can't spot that task number! Type list to check your task numbers.
____________________________________________________________
____________________________________________________________
  Which task? Add a number after delete, like delete 1.
____________________________________________________________
____________________________________________________________
  Whoops! Use a whole number for the task, like delete 1.
____________________________________________________________
____________________________________________________________
  I can't spot that task number! Type list to check your task numbers.
____________________________________________________________
____________________________________________________________
  A fresh start! Your list is empty. Try todo read a book to get going.
____________________________________________________________
____________________________________________________________
  Stay fresh! Catch you next time!
____________________________________________________________
```

## TC4: Reject an empty todo and an unknown command

Aim: Verify that Minty explains an empty todo description and an unrecognized command without adding a task or terminating the session.

### Input

```text
todo
blah
bye
```

### Expected output

```text
____________________________________________________________
███╗   ███╗██╗███╗   ██╗████████╗██╗   ██╗
████╗ ████║██║████╗  ██║╚══██╔══╝╚██╗ ██╔╝
██╔████╔██║██║██╔██╗ ██║   ██║    ╚████╔╝
██║╚██╔╝██║██║██║╚██╗██║   ██║     ╚██╔╝
██║ ╚═╝ ██║██║██║ ╚████║   ██║      ██║
╚═╝     ╚═╝╚═╝╚═╝  ╚═══╝   ╚═╝      ╚═╝
  Heyyy! I'm Minty, your fresh little task buddy!
  Let's get things moving. What's on your list today?
____________________________________________________________
____________________________________________________________
  Let's give that task a name! Try todo read a book.
____________________________________________________________
____________________________________________________________
  Whoops! I don't recognize that command. Try list to see your tasks or todo read a book to add one.
____________________________________________________________
____________________________________________________________
  Stay fresh! Catch you next time!
____________________________________________________________
```

## TC5: Parse and format ISO dates

Aim: Verify that deadline and event dates entered as `yyyy-MM-dd` are stored as dates and displayed as `MMM dd yyyy`.

### Input

```text
deadline submit report /by 2019-10-15
event orientation week /from 2019-10-04 /to 2019-10-11
list
bye
```

### Expected output

```text
____________________________________________________________
███╗   ███╗██╗███╗   ██╗████████╗██╗   ██╗
████╗ ████║██║████╗  ██║╚══██╔══╝╚██╗ ██╔╝
██╔████╔██║██║██╔██╗ ██║   ██║    ╚████╔╝
██║╚██╔╝██║██║██║╚██╗██║   ██║     ╚██╔╝
██║ ╚═╝ ██║██║██║ ╚████║   ██║      ██║
╚═╝     ╚═╝╚═╝╚═╝  ╚═══╝   ╚═╝      ╚═╝
  Heyyy! I'm Minty, your fresh little task buddy!
  Let's get things moving. What's on your list today?
____________________________________________________________
____________________________________________________________
  Fresh task coming right up! I've added:
    [D][ ] submit report (by: Oct 15 2019)
  Your list now has 1 task.
____________________________________________________________
____________________________________________________________
  Fresh task coming right up! I've added:
    [E][ ] orientation week (from: Oct 04 2019 to: Oct 11 2019)
  Your list now has 2 tasks.
____________________________________________________________
____________________________________________________________
  Let's check your lineup! Here are your tasks:
  1. [D][ ] submit report (by: Oct 15 2019)
  2. [E][ ] orientation week (from: Oct 04 2019 to: Oct 11 2019)
____________________________________________________________
____________________________________________________________
  Stay fresh! Catch you next time!
____________________________________________________________
```

## TC6: Validate deadlines and recover afterward

Aim: Verify each missing deadline component produces the correct error, does not add a task, and does not prevent a later valid deadline from being stored.

### Input

```text
deadline
deadline /by 2019-12-06
deadline submit report /by
deadline valid report /by 2019-12-09
list
bye
```

### Expected output

```text
____________________________________________________________
███╗   ███╗██╗███╗   ██╗████████╗██╗   ██╗
████╗ ████║██║████╗  ██║╚══██╔══╝╚██╗ ██╔╝
██╔████╔██║██║██╔██╗ ██║   ██║    ╚████╔╝
██║╚██╔╝██║██║██║╚██╗██║   ██║     ╚██╔╝
██║ ╚═╝ ██║██║██║ ╚████║   ██║      ██║
╚═╝     ╚═╝╚═╝╚═╝  ╚═══╝   ╚═╝      ╚═╝
  Heyyy! I'm Minty, your fresh little task buddy!
  Let's get things moving. What's on your list today?
____________________________________________________________
____________________________________________________________
  Let's give that deadline a date! Try deadline submit report /by 2026-09-18.
____________________________________________________________
____________________________________________________________
  What's due? Add a description before /by.
____________________________________________________________
____________________________________________________________
  When's it due? Add a date after /by, like 2026-09-18.
____________________________________________________________
____________________________________________________________
  Fresh task coming right up! I've added:
    [D][ ] valid report (by: Dec 09 2019)
  Your list now has 1 task.
____________________________________________________________
____________________________________________________________
  Let's check your lineup! Here are your tasks:
  1. [D][ ] valid report (by: Dec 09 2019)
____________________________________________________________
____________________________________________________________
  Stay fresh! Catch you next time!
____________________________________________________________
```

## TC7: Validate events and recover afterward

Aim: Verify every malformed event arrangement produces the correct error, does not add a task, and does not prevent a later valid event from being stored.

### Input

```text
event
event meeting /to 4pm /from 2pm
event meeting /from 2pm
event /from 2019-12-09 /to 2019-12-10
event meeting /from /to 4pm
event meeting /from 2pm /to
event valid meeting /from 2019-12-09 /to 2019-12-10
list
bye
```

### Expected output

```text
____________________________________________________________
███╗   ███╗██╗███╗   ██╗████████╗██╗   ██╗
████╗ ████║██║████╗  ██║╚══██╔══╝╚██╗ ██╔╝
██╔████╔██║██║██╔██╗ ██║   ██║    ╚████╔╝
██║╚██╔╝██║██║██║╚██╗██║   ██║     ╚██╔╝
██║ ╚═╝ ██║██║██║ ╚████║   ██║      ██║
╚═╝     ╚═╝╚═╝╚═╝  ╚═══╝   ╚═╝      ╚═╝
  Heyyy! I'm Minty, your fresh little task buddy!
  Let's get things moving. What's on your list today?
____________________________________________________________
____________________________________________________________
  When does it start? Add /from followed by a date, like /from 2026-09-18.
____________________________________________________________
____________________________________________________________
  Start first, finish second! Put /from before /to.
____________________________________________________________
____________________________________________________________
  When does it wrap up? Add /to followed by a date, like /to 2026-09-19.
____________________________________________________________
____________________________________________________________
  What's the occasion? Add a description before /from.
____________________________________________________________
____________________________________________________________
  Let's set the start! Add a date after /from, like 2026-09-18.
____________________________________________________________
____________________________________________________________
  Let's set the finish! Add a date after /to, like 2026-09-19.
____________________________________________________________
____________________________________________________________
  Fresh task coming right up! I've added:
    [E][ ] valid meeting (from: Dec 09 2019 to: Dec 10 2019)
  Your list now has 1 task.
____________________________________________________________
____________________________________________________________
  Let's check your lineup! Here are your tasks:
  1. [E][ ] valid meeting (from: Dec 09 2019 to: Dec 10 2019)
____________________________________________________________
____________________________________________________________
  Stay fresh! Catch you next time!
____________________________________________________________
```

## TC8: Handle numeric boundaries and surrounding whitespace

Aim: Verify that descriptions are trimmed, valid signed or zero-padded task numbers work, and missing, non-numeric, non-positive, overflowing, or out-of-range task numbers leave state unchanged.

### Input

```text
todo       trimmed task
mark +1
unmark 01
mark
mark two
mark -1
mark 0
mark 2147483648
unmark
unmark bananas
unmark 2
deadline report /by 2019-11-01
event trip /from 2019-11-02 /to 2019-11-03
list
bye
```

### Expected output

```text
____________________________________________________________
███╗   ███╗██╗███╗   ██╗████████╗██╗   ██╗
████╗ ████║██║████╗  ██║╚══██╔══╝╚██╗ ██╔╝
██╔████╔██║██║██╔██╗ ██║   ██║    ╚████╔╝
██║╚██╔╝██║██║██║╚██╗██║   ██║     ╚██╔╝
██║ ╚═╝ ██║██║██║ ╚████║   ██║      ██║
╚═╝     ╚═╝╚═╝╚═╝  ╚═══╝   ╚═╝      ╚═╝
  Heyyy! I'm Minty, your fresh little task buddy!
  Let's get things moving. What's on your list today?
____________________________________________________________
____________________________________________________________
  Fresh task coming right up! I've added:
    [T][ ] trimmed task
  Your list now has 1 task.
____________________________________________________________
____________________________________________________________
  Woohoo! Marked as done:
    [T][X] trimmed task
____________________________________________________________
____________________________________________________________
  Ready for another round! Marked as not done:
    [T][ ] trimmed task
____________________________________________________________
____________________________________________________________
  Which task? Add a number after mark, like mark 1.
____________________________________________________________
____________________________________________________________
  Whoops! Use a whole number for the task, like mark 1.
____________________________________________________________
____________________________________________________________
  I can't spot that task number! Type list to check your task numbers.
____________________________________________________________
____________________________________________________________
  I can't spot that task number! Type list to check your task numbers.
____________________________________________________________
____________________________________________________________
  Whoops! Use a whole number for the task, like mark 1.
____________________________________________________________
____________________________________________________________
  Which task? Add a number after unmark, like unmark 1.
____________________________________________________________
____________________________________________________________
  Whoops! Use a whole number for the task, like unmark 1.
____________________________________________________________
____________________________________________________________
  I can't spot that task number! Type list to check your task numbers.
____________________________________________________________
____________________________________________________________
  Fresh task coming right up! I've added:
    [D][ ] report (by: Nov 01 2019)
  Your list now has 2 tasks.
____________________________________________________________
____________________________________________________________
  Fresh task coming right up! I've added:
    [E][ ] trip (from: Nov 02 2019 to: Nov 03 2019)
  Your list now has 3 tasks.
____________________________________________________________
____________________________________________________________
  Let's check your lineup! Here are your tasks:
  1. [T][ ] trimmed task
  2. [D][ ] report (by: Nov 01 2019)
  3. [E][ ] trip (from: Nov 02 2019 to: Nov 03 2019)
____________________________________________________________
____________________________________________________________
  Stay fresh! Catch you next time!
____________________________________________________________
```

## TC9: Save after every task-list change

Aim: Verify that Minty starts without an existing data file or folder, creates both automatically, and writes after each successful add, mark, delete, and unmark operation.

### Input

```text
todo read book
deadline return book /by 2019-06-06
event project meeting /from 2019-08-06 /to 2019-08-07
mark 1
delete 2
unmark 1
bye
```

### Expected output

```text
____________________________________________________________
███╗   ███╗██╗███╗   ██╗████████╗██╗   ██╗
████╗ ████║██║████╗  ██║╚══██╔══╝╚██╗ ██╔╝
██╔████╔██║██║██╔██╗ ██║   ██║    ╚████╔╝
██║╚██╔╝██║██║██║╚██╗██║   ██║     ╚██╔╝
██║ ╚═╝ ██║██║██║ ╚████║   ██║      ██║
╚═╝     ╚═╝╚═╝╚═╝  ╚═══╝   ╚═╝      ╚═╝
  Heyyy! I'm Minty, your fresh little task buddy!
  Let's get things moving. What's on your list today?
____________________________________________________________
____________________________________________________________
  Fresh task coming right up! I've added:
    [T][ ] read book
  Your list now has 1 task.
____________________________________________________________
____________________________________________________________
  Fresh task coming right up! I've added:
    [D][ ] return book (by: Jun 06 2019)
  Your list now has 2 tasks.
____________________________________________________________
____________________________________________________________
  Fresh task coming right up! I've added:
    [E][ ] project meeting (from: Aug 06 2019 to: Aug 07 2019)
  Your list now has 3 tasks.
____________________________________________________________
____________________________________________________________
  Woohoo! Marked as done:
    [T][X] read book
____________________________________________________________
____________________________________________________________
  Making room! I've removed:
    [D][ ] return book (by: Jun 06 2019)
  Your list now has 2 tasks.
____________________________________________________________
____________________________________________________________
  Ready for another round! Marked as not done:
    [T][ ] read book
____________________________________________________________
____________________________________________________________
  Stay fresh! Catch you next time!
____________________________________________________________
```

## TC10: Load saved task types and statuses

Aim: Verify that Minty loads the Todo, Deadline, and Event in `test/data/TC10.txt`, including a completed task, before processing the first command.

### Input

```text
list
bye
```

### Expected output

```text
____________________________________________________________
███╗   ███╗██╗███╗   ██╗████████╗██╗   ██╗
████╗ ████║██║████╗  ██║╚══██╔══╝╚██╗ ██╔╝
██╔████╔██║██║██╔██╗ ██║   ██║    ╚████╔╝
██║╚██╔╝██║██║██║╚██╗██║   ██║     ╚██╔╝
██║ ╚═╝ ██║██║██║ ╚████║   ██║      ██║
╚═╝     ╚═╝╚═╝╚═╝  ╚═══╝   ╚═╝      ╚═╝
  Heyyy! I'm Minty, your fresh little task buddy!
  Let's get things moving. What's on your list today?
____________________________________________________________
____________________________________________________________
  Let's check your lineup! Here are your tasks:
  1. [T][X] read book
  2. [D][ ] return book (by: Jun 06 2019)
  3. [E][ ] project meeting (from: Aug 06 2019 to: Aug 07 2019)
____________________________________________________________
____________________________________________________________
  Stay fresh! Catch you next time!
____________________________________________________________
```

## TC11: Load blank lines and escaped delimiters

Aim: Verify that blank save-file lines are ignored and escaped pipe and backslash characters are restored as ordinary task text.

### Input

```text
list
bye
```

### Expected output

```text
____________________________________________________________
███╗   ███╗██╗███╗   ██╗████████╗██╗   ██╗
████╗ ████║██║████╗  ██║╚══██╔══╝╚██╗ ██╔╝
██╔████╔██║██║██╔██╗ ██║   ██║    ╚████╔╝
██║╚██╔╝██║██║██║╚██╗██║   ██║     ╚██╔╝
██║ ╚═╝ ██║██║██║ ╚████║   ██║      ██║
╚═╝     ╚═╝╚═╝╚═╝  ╚═══╝   ╚═╝      ╚═╝
  Heyyy! I'm Minty, your fresh little task buddy!
  Let's get things moving. What's on your list today?
____________________________________________________________
____________________________________________________________
  Let's check your lineup! Here are your tasks:
  1. [T][X] compare | alternatives
  2. [D][ ] use C:\temp (by: Dec 06 2019)
____________________________________________________________
____________________________________________________________
  Stay fresh! Catch you next time!
____________________________________________________________
```

## TC12: Reject an invalid saved status

Aim: Verify that a completion status other than `0` or `1` produces a clear error and starts Minty with an empty list instead of crashing.

### Input

```text
bye
```

### Expected output

```text
  I've hit a snag loading your saved tasks. Starting with an empty list for this session. Details: I couldn't read the saved task on line 1: status must be 0 or 1. Saving is paused to protect your data. Repair or move the data file, then restart Minty.
____________________________________________________________
███╗   ███╗██╗███╗   ██╗████████╗██╗   ██╗
████╗ ████║██║████╗  ██║╚══██╔══╝╚██╗ ██╔╝
██╔████╔██║██║██╔██╗ ██║   ██║    ╚████╔╝
██║╚██╔╝██║██║██║╚██╗██║   ██║     ╚██╔╝
██║ ╚═╝ ██║██║██║ ╚████║   ██║      ██║
╚═╝     ╚═╝╚═╝╚═╝  ╚═══╝   ╚═╝      ╚═╝
  Heyyy! I'm Minty, your fresh little task buddy!
  Let's get things moving. What's on your list today?
____________________________________________________________
____________________________________________________________
  Stay fresh! Catch you next time!
____________________________________________________________
```

## TC13: Reject an unknown saved task type

Aim: Verify that an unknown task type produces a line-specific error and starts Minty safely.

### Input

```text
bye
```

### Expected output

```text
  I've hit a snag loading your saved tasks. Starting with an empty list for this session. Details: I couldn't read the saved task on line 1: unknown task type 'N'. Saving is paused to protect your data. Repair or move the data file, then restart Minty.
____________________________________________________________
███╗   ███╗██╗███╗   ██╗████████╗██╗   ██╗
████╗ ████║██║████╗  ██║╚══██╔══╝╚██╗ ██╔╝
██╔████╔██║██║██╔██╗ ██║   ██║    ╚████╔╝
██║╚██╔╝██║██║██║╚██╗██║   ██║     ╚██╔╝
██║ ╚═╝ ██║██║██║ ╚████║   ██║      ██║
╚═╝     ╚═╝╚═╝╚═╝  ╚═══╝   ╚═╝      ╚═╝
  Heyyy! I'm Minty, your fresh little task buddy!
  Let's get things moving. What's on your list today?
____________________________________________________________
____________________________________________________________
  Stay fresh! Catch you next time!
____________________________________________________________
```

## TC14: Reject missing saved task fields

Aim: Verify that a task with too few fields produces a line-specific error and starts Minty safely.

### Input

```text
bye
```

### Expected output

```text
  I've hit a snag loading your saved tasks. Starting with an empty list for this session. Details: I couldn't read the saved task on line 1: expected 4 fields but found 3. Saving is paused to protect your data. Repair or move the data file, then restart Minty.
____________________________________________________________
███╗   ███╗██╗███╗   ██╗████████╗██╗   ██╗
████╗ ████║██║████╗  ██║╚══██╔══╝╚██╗ ██╔╝
██╔████╔██║██║██╔██╗ ██║   ██║    ╚████╔╝
██║╚██╔╝██║██║██║╚██╗██║   ██║     ╚██╔╝
██║ ╚═╝ ██║██║██║ ╚████║   ██║      ██║
╚═╝     ╚═╝╚═╝╚═╝  ╚═══╝   ╚═╝      ╚═╝
  Heyyy! I'm Minty, your fresh little task buddy!
  Let's get things moving. What's on your list today?
____________________________________________________________
____________________________________________________________
  Stay fresh! Catch you next time!
____________________________________________________________
```

## TC15: Reject empty saved task details

Aim: Verify that an empty required task field produces a line-specific error and starts Minty safely.

### Input

```text
bye
```

### Expected output

```text
  I've hit a snag loading your saved tasks. Starting with an empty list for this session. Details: I couldn't read the saved task on line 1: task details cannot be empty. Saving is paused to protect your data. Repair or move the data file, then restart Minty.
____________________________________________________________
███╗   ███╗██╗███╗   ██╗████████╗██╗   ██╗
████╗ ████║██║████╗  ██║╚══██╔══╝╚██╗ ██╔╝
██╔████╔██║██║██╔██╗ ██║   ██║    ╚████╔╝
██║╚██╔╝██║██║██║╚██╗██║   ██║     ╚██╔╝
██║ ╚═╝ ██║██║██║ ╚████║   ██║      ██║
╚═╝     ╚═╝╚═╝╚═╝  ╚═══╝   ╚═╝      ╚═╝
  Heyyy! I'm Minty, your fresh little task buddy!
  Let's get things moving. What's on your list today?
____________________________________________________________
____________________________________________________________
  Stay fresh! Catch you next time!
____________________________________________________________
```

## TC16: Reject an unfinished saved escape sequence

Aim: Verify that a trailing escape character produces a line-specific error and starts Minty safely.

### Input

```text
bye
```

### Expected output

```text
  I've hit a snag loading your saved tasks. Starting with an empty list for this session. Details: I couldn't read the saved task on line 1: unfinished escape character. Saving is paused to protect your data. Repair or move the data file, then restart Minty.
____________________________________________________________
███╗   ███╗██╗███╗   ██╗████████╗██╗   ██╗
████╗ ████║██║████╗  ██║╚══██╔══╝╚██╗ ██╔╝
██╔████╔██║██║██╔██╗ ██║   ██║    ╚████╔╝
██║╚██╔╝██║██║██║╚██╗██║   ██║     ╚██╔╝
██║ ╚═╝ ██║██║██║ ╚████║   ██║      ██║
╚═╝     ╚═╝╚═╝╚═╝  ╚═══╝   ╚═╝      ╚═╝
  Heyyy! I'm Minty, your fresh little task buddy!
  Let's get things moving. What's on your list today?
____________________________________________________________
____________________________________________________________
  Stay fresh! Catch you next time!
____________________________________________________________
```

## TC17: Reject an unsupported saved escape sequence

Aim: Verify that an escape character before an unsupported character produces a line-specific error and starts Minty safely.

### Input

```text
bye
```

### Expected output

```text
  I've hit a snag loading your saved tasks. Starting with an empty list for this session. Details: I couldn't read the saved task on line 1: unsupported escape sequence '\q'. Saving is paused to protect your data. Repair or move the data file, then restart Minty.
____________________________________________________________
███╗   ███╗██╗███╗   ██╗████████╗██╗   ██╗
████╗ ████║██║████╗  ██║╚══██╔══╝╚██╗ ██╔╝
██╔████╔██║██║██╔██╗ ██║   ██║    ╚████╔╝
██║╚██╔╝██║██║██║╚██╗██║   ██║     ╚██╔╝
██║ ╚═╝ ██║██║██║ ╚████║   ██║      ██║
╚═╝     ╚═╝╚═╝╚═╝  ╚═══╝   ╚═╝      ╚═╝
  Heyyy! I'm Minty, your fresh little task buddy!
  Let's get things moving. What's on your list today?
____________________________________________________________
____________________________________________________________
  Stay fresh! Catch you next time!
____________________________________________________________
```

## TC18: Reject invalid dates and reversed event ranges

Aim: Verify that malformed and impossible ISO dates and an event ending before it starts are rejected without preventing a later valid leap-day deadline.

### Input

```text
deadline impossible /by 2019-02-29
deadline wrong format /by 15-10-2019
event impossible /from 2019-02-29 /to 2019-03-01
event reversed /from 2019-03-02 /to 2019-03-01
event invalid end /from 2019-03-01 /to tomorrow
deadline leap day /by 2020-02-29
list
bye
```

### Expected output

```text
____________________________________________________________
███╗   ███╗██╗███╗   ██╗████████╗██╗   ██╗
████╗ ████║██║████╗  ██║╚══██╔══╝╚██╗ ██╔╝
██╔████╔██║██║██╔██╗ ██║   ██║    ╚████╔╝
██║╚██╔╝██║██║██║╚██╗██║   ██║     ╚██╔╝
██║ ╚═╝ ██║██║██║ ╚████║   ██║      ██║
╚═╝     ╚═╝╚═╝╚═╝  ╚═══╝   ╚═╝      ╚═╝
  Heyyy! I'm Minty, your fresh little task buddy!
  Let's get things moving. What's on your list today?
____________________________________________________________
____________________________________________________________
  That deadline date doesn't look right! Use a valid date in yyyy-MM-dd format, like 2026-09-18.
____________________________________________________________
____________________________________________________________
  That deadline date doesn't look right! Use a valid date in yyyy-MM-dd format, like 2026-09-18.
____________________________________________________________
____________________________________________________________
  That event start date doesn't look right! Use a valid date in yyyy-MM-dd format, like 2026-09-18.
____________________________________________________________
____________________________________________________________
  Whoops! The event ends before it starts. Set the end date to the start date or later.
____________________________________________________________
____________________________________________________________
  That event end date doesn't look right! Use a valid date in yyyy-MM-dd format, like 2026-09-18.
____________________________________________________________
____________________________________________________________
  Fresh task coming right up! I've added:
    [D][ ] leap day (by: Feb 29 2020)
  Your list now has 1 task.
____________________________________________________________
____________________________________________________________
  Let's check your lineup! Here are your tasks:
  1. [D][ ] leap day (by: Feb 29 2020)
____________________________________________________________
____________________________________________________________
  Stay fresh! Catch you next time!
____________________________________________________________
```

## TC19: Reject an invalid saved date

Aim: Verify that an impossible date in saved deadline data produces a line-specific error and starts Minty safely.

### Input

```text
bye
```

### Expected output

```text
  I've hit a snag loading your saved tasks. Starting with an empty list for this session. Details: I couldn't read the saved task on line 1: date must use yyyy-MM-dd. Saving is paused to protect your data. Repair or move the data file, then restart Minty.
____________________________________________________________
███╗   ███╗██╗███╗   ██╗████████╗██╗   ██╗
████╗ ████║██║████╗  ██║╚══██╔══╝╚██╗ ██╔╝
██╔████╔██║██║██╔██╗ ██║   ██║    ╚████╔╝
██║╚██╔╝██║██║██║╚██╗██║   ██║     ╚██╔╝
██║ ╚═╝ ██║██║██║ ╚████║   ██║      ██║
╚═╝     ╚═╝╚═╝╚═╝  ╚═══╝   ╚═╝      ╚═╝
  Heyyy! I'm Minty, your fresh little task buddy!
  Let's get things moving. What's on your list today?
____________________________________________________________
____________________________________________________________
  Stay fresh! Catch you next time!
____________________________________________________________
```

## TC20: Reject a reversed saved event range

Aim: Verify that a saved event ending before it starts produces a line-specific error and starts Minty safely.

### Input

```text
bye
```

### Expected output

```text
  I've hit a snag loading your saved tasks. Starting with an empty list for this session. Details: I couldn't read the saved task on line 1: event end date is before its start date. Saving is paused to protect your data. Repair or move the data file, then restart Minty.
____________________________________________________________
███╗   ███╗██╗███╗   ██╗████████╗██╗   ██╗
████╗ ████║██║████╗  ██║╚══██╔══╝╚██╗ ██╔╝
██╔████╔██║██║██╔██╗ ██║   ██║    ╚████╔╝
██║╚██╔╝██║██║██║╚██╗██║   ██║     ╚██╔╝
██║ ╚═╝ ██║██║██║ ╚████║   ██║      ██║
╚═╝     ╚═╝╚═╝╚═╝  ╚═══╝   ╚═╝      ╚═╝
  Heyyy! I'm Minty, your fresh little task buddy!
  Let's get things moving. What's on your list today?
____________________________________________________________
____________________________________________________________
  Stay fresh! Catch you next time!
____________________________________________________________
```

## TC21: Find tasks occurring on a date

Aim: Verify that `on` finds deadlines due on the requested date and events across inclusive date ranges, excludes todos, reports no matches, and validates missing or invalid dates.

### Input

```text
on 2019-10-15
on 2019-10-14
on 2019-10-16
on 2019-10-17
on 2019-10-18
on
on 2019-02-29
bye
```

### Expected output

```text
____________________________________________________________
███╗   ███╗██╗███╗   ██╗████████╗██╗   ██╗
████╗ ████║██║████╗  ██║╚══██╔══╝╚██╗ ██╔╝
██╔████╔██║██║██╔██╗ ██║   ██║    ╚████╔╝
██║╚██╔╝██║██║██║╚██╗██║   ██║     ╚██╔╝
██║ ╚═╝ ██║██║██║ ╚████║   ██║      ██║
╚═╝     ╚═╝╚═╝╚═╝  ╚═══╝   ╚═╝      ╚═╝
  Heyyy! I'm Minty, your fresh little task buddy!
  Let's get things moving. What's on your list today?
____________________________________________________________
____________________________________________________________
  Here's your lineup for Oct 15 2019:
  1. [D][ ] due task (by: Oct 15 2019)
  2. [E][ ] conference (from: Oct 14 2019 to: Oct 16 2019)
____________________________________________________________
____________________________________________________________
  Here's your lineup for Oct 14 2019:
  1. [E][ ] conference (from: Oct 14 2019 to: Oct 16 2019)
____________________________________________________________
____________________________________________________________
  Here's your lineup for Oct 16 2019:
  1. [E][ ] conference (from: Oct 14 2019 to: Oct 16 2019)
____________________________________________________________
____________________________________________________________
  Here's your lineup for Oct 17 2019:
  1. [D][X] later task (by: Oct 17 2019)
____________________________________________________________
____________________________________________________________
  Here's your lineup for Oct 18 2019:
  No deadlines or events on this date. A little breathing room!
____________________________________________________________
____________________________________________________________
  Which day are we checking? Try on 2026-09-18.
____________________________________________________________
____________________________________________________________
  That requested date doesn't look right! Use a valid date in yyyy-MM-dd format, like 2026-09-18.
____________________________________________________________
____________________________________________________________
  Stay fresh! Catch you next time!
____________________________________________________________
```

## TC22: Find tasks by description keyword

Aim: Verify that `find` returns matching task descriptions in list order, preserves their displayed types and statuses, excludes metadata-only matches, handles no matches, and validates a missing keyword.

### Input

```text
todo read book
deadline return book /by 2019-12-02
event book club /from 2019-12-03 /to 2019-12-04
mark 1
find book
find magazine
find 2019
find
bye
```

### Expected output

```text
____________________________________________________________
███╗   ███╗██╗███╗   ██╗████████╗██╗   ██╗
████╗ ████║██║████╗  ██║╚══██╔══╝╚██╗ ██╔╝
██╔████╔██║██║██╔██╗ ██║   ██║    ╚████╔╝
██║╚██╔╝██║██║██║╚██╗██║   ██║     ╚██╔╝
██║ ╚═╝ ██║██║██║ ╚████║   ██║      ██║
╚═╝     ╚═╝╚═╝╚═╝  ╚═══╝   ╚═╝      ╚═╝
  Heyyy! I'm Minty, your fresh little task buddy!
  Let's get things moving. What's on your list today?
____________________________________________________________
____________________________________________________________
  Fresh task coming right up! I've added:
    [T][ ] read book
  Your list now has 1 task.
____________________________________________________________
____________________________________________________________
  Fresh task coming right up! I've added:
    [D][ ] return book (by: Dec 02 2019)
  Your list now has 2 tasks.
____________________________________________________________
____________________________________________________________
  Fresh task coming right up! I've added:
    [E][ ] book club (from: Dec 03 2019 to: Dec 04 2019)
  Your list now has 3 tasks.
____________________________________________________________
____________________________________________________________
  Woohoo! Marked as done:
    [T][X] read book
____________________________________________________________
____________________________________________________________
  Found some fresh matches! Here's what matches your search:
  1. [T][X] read book
  2. [D][ ] return book (by: Dec 02 2019)
  3. [E][ ] book club (from: Dec 03 2019 to: Dec 04 2019)
____________________________________________________________
____________________________________________________________
  No matches this time! Give another keyword a go.
____________________________________________________________
____________________________________________________________
  No matches this time! Give another keyword a go.
____________________________________________________________
____________________________________________________________
  What are we looking for? Add a keyword, like find book.
____________________________________________________________
____________________________________________________________
  Stay fresh! Catch you next time!
____________________________________________________________
```

## TC23: Query empty reminders and reject arguments

Aim: Verify both reminder aliases, empty output, and rejection of arguments without depending on the current date.

### Input

```text
reminders
remind
reminders 14
remind 2
bye
```

### Expected output

```text
____________________________________________________________
███╗   ███╗██╗███╗   ██╗████████╗██╗   ██╗
████╗ ████║██║████╗  ██║╚══██╔══╝╚██╗ ██╔╝
██╔████╔██║██║██╔██╗ ██║   ██║    ╚████╔╝
██║╚██╔╝██║██║██║╚██╗██║   ██║     ╚██╔╝
██║ ╚═╝ ██║██║██║ ╚████║   ██║      ██║
╚═╝     ╚═╝╚═╝╚═╝  ╚═══╝   ╚═╝      ╚═╝
  Heyyy! I'm Minty, your fresh little task buddy!
  Let's get things moving. What's on your list today?
____________________________________________________________
____________________________________________________________
  Heads up! Here are your reminders for today and the next 6 days:
  No incomplete deadlines or events in this period. Stay fresh!
____________________________________________________________
____________________________________________________________
  Heads up! Here are your reminders for today and the next 6 days:
  No incomplete deadlines or events in this period. Stay fresh!
____________________________________________________________
____________________________________________________________
  Whoops! I don't recognize that command. Try list to see your tasks or todo read a book to add one.
____________________________________________________________
____________________________________________________________
  Whoops! I don't recognize that command. Try list to see your tasks or todo read a book to add one.
____________________________________________________________
____________________________________________________________
  Stay fresh! Catch you next time!
____________________________________________________________
```

## TC24: Normalize whitespace and reject duplicates

Aim: Verify leading/trailing spaces and tabs are accepted, duplicate details do not create tasks, and the session recovers.

### Input

```text
  todo	read   book  
 todo read book 
 mark	1 
todo read book
 list 
 bye 
```

### Expected output

```text
____________________________________________________________
███╗   ███╗██╗███╗   ██╗████████╗██╗   ██╗
████╗ ████║██║████╗  ██║╚══██╔══╝╚██╗ ██╔╝
██╔████╔██║██║██╔██╗ ██║   ██║    ╚████╔╝
██║╚██╔╝██║██║██║╚██╗██║   ██║     ╚██╔╝
██║ ╚═╝ ██║██║██║ ╚████║   ██║      ██║
╚═╝     ╚═╝╚═╝╚═╝  ╚═══╝   ╚═╝      ╚═╝
  Heyyy! I'm Minty, your fresh little task buddy!
  Let's get things moving. What's on your list today?
____________________________________________________________
____________________________________________________________
  Fresh task coming right up! I've added:
    [T][ ] read book
  Your list now has 1 task.
____________________________________________________________
____________________________________________________________
  That task is already on your list! Use list to find it.
____________________________________________________________
____________________________________________________________
  Woohoo! Marked as done:
    [T][X] read book
____________________________________________________________
____________________________________________________________
  That task is already on your list! Use list to find it.
____________________________________________________________
____________________________________________________________
  Let's check your lineup! Here are your tasks:
  1. [T][X] read book
____________________________________________________________
____________________________________________________________
  Stay fresh! Catch you next time!
____________________________________________________________
```

## TC25: Reject repeated parameters and recover

Aim: Verify repeated date markers and invalid dates are rejected while a same-day event remains valid.

### Input

```text
deadline work /by 2019-01-01 /by 2019-01-02
event work /from 2019-01-01 /from 2019-01-02 /to 2019-01-03
event work /from 2019-01-01 /to 2019-01-02 /to 2019-01-03
deadline work /by 2019-02-30
event day trip /from 2019-01-01 /to 2019-01-01
bye
```

### Expected output

```text
____________________________________________________________
███╗   ███╗██╗███╗   ██╗████████╗██╗   ██╗
████╗ ████║██║████╗  ██║╚══██╔══╝╚██╗ ██╔╝
██╔████╔██║██║██╔██╗ ██║   ██║    ╚████╔╝
██║╚██╔╝██║██║██║╚██╗██║   ██║     ╚██╔╝
██║ ╚═╝ ██║██║██║ ╚████║   ██║      ██║
╚═╝     ╚═╝╚═╝╚═╝  ╚═══╝   ╚═╝      ╚═╝
  Heyyy! I'm Minty, your fresh little task buddy!
  Let's get things moving. What's on your list today?
____________________________________________________________
____________________________________________________________
  Whoops! Use /by only once.
____________________________________________________________
____________________________________________________________
  Whoops! Use /from only once.
____________________________________________________________
____________________________________________________________
  Whoops! Use /to only once.
____________________________________________________________
____________________________________________________________
  That deadline date doesn't look right! Use a valid date in yyyy-MM-dd format, like 2026-09-18.
____________________________________________________________
____________________________________________________________
  Fresh task coming right up! I've added:
    [E][ ] day trip (from: Jan 01 2019 to: Jan 01 2019)
  Your list now has 1 task.
____________________________________________________________
____________________________________________________________
  Stay fresh! Catch you next time!
____________________________________________________________
```

## TC26: Reject blank input and malformed markers

Aim: Verify blank input, missing spaces around date markers, and multiple task numbers produce errors without stopping later commands.

### Input

```text

deadline work /by2019-01-01
mark 1 2
list
bye
```

### Expected output

```text
____________________________________________________________
███╗   ███╗██╗███╗   ██╗████████╗██╗   ██╗
████╗ ████║██║████╗  ██║╚══██╔══╝╚██╗ ██╔╝
██╔████╔██║██║██╔██╗ ██║   ██║    ╚████╔╝
██║╚██╔╝██║██║██║╚██╗██║   ██║     ╚██╔╝
██║ ╚═╝ ██║██║██║ ╚████║   ██║      ██║
╚═╝     ╚═╝╚═╝╚═╝  ╚═══╝   ╚═╝      ╚═╝
  Heyyy! I'm Minty, your fresh little task buddy!
  Let's get things moving. What's on your list today?
____________________________________________________________
____________________________________________________________
  What shall we do? Try list or todo read a book.
____________________________________________________________
____________________________________________________________
  Let's give that deadline a date! Try deadline submit report /by 2026-09-18.
____________________________________________________________
____________________________________________________________
  Whoops! Use a whole number for the task, like mark 1.
____________________________________________________________
____________________________________________________________
  A fresh start! Your list is empty. Try todo read a book to get going.
____________________________________________________________
____________________________________________________________
  Stay fresh! Catch you next time!
____________________________________________________________
```
