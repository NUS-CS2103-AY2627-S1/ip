# UI Test Plan

## Program

Compile:
```sh
source "$HOME/.sdkman/bin/sdkman-init.sh" && sdk use java 25.0.3.fx-zulu >/dev/null && javac -d /private/tmp/edith-ui-test-classes src/main/java/edith/*.java src/main/java/edith/task/*.java src/main/java/edith/command/*.java
```

Run:
```sh
source "$HOME/.sdkman/bin/sdkman-init.sh" && sdk use java 25.0.3.fx-zulu >/dev/null && rm -f /private/tmp/edith-ui-test-data.txt && java -Dedith.taskFile=/private/tmp/edith-ui-test-data.txt -cp /private/tmp/edith-ui-test-classes edith.Edith
```

Each case starts a fresh Edith session. The expected output includes the entire
session, from Edith's greeting through the `bye` response.

The Gradle application entry point launches the JavaFX GUI; these tests invoke
the console entry point directly to verify Edith's command behavior.

Manual GUI checks after visual changes:

- The title bar uses the mock-up's #2d67c8-to-#1e488f blue gradient and displays the bundled logo from
  `src/main/resources/edith/images/logo.png` as a circle in both the operating system and app title bars, followed by
  the E.D.I.T.H. name, subtitle, and green Online indicator.
- The opening message addresses Peter and tells the user to send `help` for command syntax; no separate
  assistant bar or navigation sidebar is present.
- User commands appear in right-aligned deep-blue bubbles, while Edith's responses are left-aligned slate
  bubbles with the circular cropped logo beside them. Both bubble styles use the mock-up's asymmetric pointed
  corner.
- The conversation, bubbles, input area, and scrollbar use the mock-up's dark-navy scheme rather than light
  surfaces.
- Invalid commands appear in a pale-red response bubble identified by a `COMMAND ERROR` label.
- The GUI help card groups commands and shows required task fields, including `/by` for deadlines and
  `/from` and `/to` for events.
- The command field and guide preserve parser syntax, including `deadline <description> /by <date>`.
- Resizing the window keeps the input bar visible and expands or contracts message widths with the conversation.
- Pressing Enter or Send submits a command and scrolls to the newest message without manual scrolling.
- Entering `bye` changes the title-bar indicator to red Offline and disables both input controls after Edith
  replies.

### Test case: add and list all task types

Aim: Verify that to-dos, deadlines, and events are stored with their type-specific details and displayed in insertion order.

Input:
```text
todo borrow book
deadline return book /by 2019-12-02
event project meeting /from 2/12/2019 1800 /to 2/12/2019 2000
list
bye
```

Expected output:
```text
_______________________________________________________________________________
 _____    _ _ _   _     
| ____|__| (_) |_| |__  
|  _| / _` | | __| '_ \ 
| |__| (_| | | |_| | | |
|_____\__,_|_|\__|_| |_|

	EDITH online. I'll keep the chaos organised.
	What are we pretending is urgent today?
_______________________________________________________________________________
_______________________________________________________________________________
	Logged. Organisation suits you:
	  [T][ ] borrow book
	Active tasks: 1.
_______________________________________________________________________________
_______________________________________________________________________________
	Logged. Organisation suits you:
	  [D][ ] return book (by: Dec 2 2019)
	Active tasks: 2.
_______________________________________________________________________________
_______________________________________________________________________________
	Logged. Organisation suits you:
	  [E][ ] project meeting (from: Dec 2 2019 6:00pm to: Dec 2 2019 8:00pm)
	Active tasks: 3.
_______________________________________________________________________________
_______________________________________________________________________________
	Your task situation. Do try to keep up:
	1.[T][ ] borrow book
	2.[D][ ] return book (by: Dec 2 2019)
	3.[E][ ] project meeting (from: Dec 2 2019 6:00pm to: Dec 2 2019 8:00pm)
_______________________________________________________________________________
_______________________________________________________________________________
	EDITH signing off. Try not to create chaos without me.
_______________________________________________________________________________
```

### Test case: find tasks by keyword

Aim: Verify that find matches keywords case-insensitively, preserves task numbering, and displays an empty result when there are no matches.

Input:
```text
todo read book
deadline return book /by 2019-12-02
event project meeting /from 2/12/2019 1800 /to 2/12/2019 2000
find BOOK
find exercise
bye
```

Expected output:
```text
_______________________________________________________________________________
 _____    _ _ _   _     
| ____|__| (_) |_| |__  
|  _| / _` | | __| '_ \ 
| |__| (_| | | |_| | | |
|_____\__,_|_|\__|_| |_|

	EDITH online. I'll keep the chaos organised.
	What are we pretending is urgent today?
_______________________________________________________________________________
_______________________________________________________________________________
	Logged. Organisation suits you:
	  [T][ ] read book
	Active tasks: 1.
_______________________________________________________________________________
_______________________________________________________________________________
	Logged. Organisation suits you:
	  [D][ ] return book (by: Dec 2 2019)
	Active tasks: 2.
_______________________________________________________________________________
_______________________________________________________________________________
	Logged. Organisation suits you:
	  [E][ ] project meeting (from: Dec 2 2019 6:00pm to: Dec 2 2019 8:00pm)
	Active tasks: 3.
_______________________________________________________________________________
_______________________________________________________________________________
	Search complete. These survived the filter:
	1.[T][ ] read book
	2.[D][ ] return book (by: Dec 2 2019)
_______________________________________________________________________________
_______________________________________________________________________________
	Search complete. These survived the filter:
_______________________________________________________________________________
_______________________________________________________________________________
	EDITH signing off. Try not to create chaos without me.
_______________________________________________________________________________
```

### Test case: mark and unmark a typed task

Aim: Verify that to-do status changes retain the task type and description.

Input:
```text
todo read book
mark 1
unmark 1
list
bye
```

Expected output:
```text
_______________________________________________________________________________
 _____    _ _ _   _     
| ____|__| (_) |_| |__  
|  _| / _` | | __| '_ \ 
| |__| (_| | | |_| | | |
|_____\__,_|_|\__|_| |_|

	EDITH online. I'll keep the chaos organised.
	What are we pretending is urgent today?
_______________________________________________________________________________
_______________________________________________________________________________
	Logged. Organisation suits you:
	  [T][ ] read book
	Active tasks: 1.
_______________________________________________________________________________
_______________________________________________________________________________
	Marked complete. Miracles do happen:
	  [T][X] read book
_______________________________________________________________________________
_______________________________________________________________________________
	Marked incomplete. Back to the grind:
	  [T][ ] read book
_______________________________________________________________________________
_______________________________________________________________________________
	Your task situation. Do try to keep up:
	1.[T][ ] read book
_______________________________________________________________________________
_______________________________________________________________________________
	EDITH signing off. Try not to create chaos without me.
_______________________________________________________________________________
```

### Test case: reject invalid commands and task data

Aim: Verify that malformed command spacing, missing parameters, invalid dates, invalid task numbers, duplicate tasks,
and invalid event time ranges are reported without ending the session.

Input:
```text
 todo buy milk
todo  buy milk
deadline pay bill
deadline pay bill /by 2026-02-30
event meeting /from 2026-06-06T16:00 /to 4pm
mark 0
todo buy milk
todo buy milk
delete 2
bye
```

Expected output:
```text
_______________________________________________________________________________
 _____    _ _ _   _     
| ____|__| (_) |_| |__  
|  _| / _` | | __| '_ \ 
| |__| (_| | | |_| | | |
|_____\__,_|_|\__|_| |_|

	EDITH online. I'll keep the chaos organised.
	What are we pretending is urgent today?
_______________________________________________________________________________
_______________________________________________________________________________
	A minor complication:
	OOPS!!! Commands must not begin or end with spaces.
_______________________________________________________________________________
_______________________________________________________________________________
	A minor complication:
	OOPS!!! Use exactly one space between command parts.
_______________________________________________________________________________
_______________________________________________________________________________
	A minor complication:
	OOPS!!! Invalid deadline command. Enter help to see its format.
_______________________________________________________________________________
_______________________________________________________________________________
	A minor complication:
	OOPS!!! Please enter a valid deadline.
_______________________________________________________________________________
_______________________________________________________________________________
	A minor complication:
	OOPS!!! An event's end date/time must be after its start date/time.
_______________________________________________________________________________
_______________________________________________________________________________
	A minor complication:
	OOPS!!! The mark command needs a positive task number.
_______________________________________________________________________________
_______________________________________________________________________________
	Logged. Organisation suits you:
	  [T][ ] buy milk
	Active tasks: 1.
_______________________________________________________________________________
_______________________________________________________________________________
	A minor complication:
	OOPS!!! An identical task is already in your list.
_______________________________________________________________________________
_______________________________________________________________________________
	A minor complication:
	OOPS!!! That task number does not exist.
_______________________________________________________________________________
_______________________________________________________________________________
	EDITH signing off. Try not to create chaos without me.
_______________________________________________________________________________
```

### Test case: delete a task from the middle of the list

Aim: Verify that deleting a task removes the selected task, updates the count, and renumbers later tasks.

Input:
```text
todo read book
deadline return book /by June 6th
event project meeting /from Aug 6th 2pm /to 4pm
todo borrow book
delete 3
list
bye
```

Expected output:
```text
_______________________________________________________________________________
 _____    _ _ _   _     
| ____|__| (_) |_| |__  
|  _| / _` | | __| '_ \ 
| |__| (_| | | |_| | | |
|_____\__,_|_|\__|_| |_|

	EDITH online. I'll keep the chaos organised.
	What are we pretending is urgent today?
_______________________________________________________________________________
_______________________________________________________________________________
	Logged. Organisation suits you:
	  [T][ ] read book
	Active tasks: 1.
_______________________________________________________________________________
_______________________________________________________________________________
	Logged. Organisation suits you:
	  [D][ ] return book (by: June 6th)
	Active tasks: 2.
_______________________________________________________________________________
_______________________________________________________________________________
	Logged. Organisation suits you:
	  [E][ ] project meeting (from: Aug 6th 2pm to: 4pm)
	Active tasks: 3.
_______________________________________________________________________________
_______________________________________________________________________________
	Logged. Organisation suits you:
	  [T][ ] borrow book
	Active tasks: 4.
_______________________________________________________________________________
_______________________________________________________________________________
	Removed. One less thing to avoid:
	  [E][ ] project meeting (from: Aug 6th 2pm to: 4pm)
	Active tasks: 3.
_______________________________________________________________________________
_______________________________________________________________________________
	Your task situation. Do try to keep up:
	1.[T][ ] read book
	2.[D][ ] return book (by: June 6th)
	3.[T][ ] borrow book
_______________________________________________________________________________
_______________________________________________________________________________
	EDITH signing off. Try not to create chaos without me.
_______________________________________________________________________________
```

### Test case: sort tasks in supported orders

Aim: Verify alphabetical, date, status, and insertion-order sorting in each supported direction.

Input:
```text
todo write report
deadline submit report /by June 6th
event project meeting /from June 5th 3pm /to 6pm
todo Buy groceries
mark 3
sort alph
sort alph desc
sort date
sort date desc
sort status
sort added
sort added desc
bye
```

Expected output:
```text
_______________________________________________________________________________
 _____    _ _ _   _     
| ____|__| (_) |_| |__  
|  _| / _` | | __| '_ \ 
| |__| (_| | | |_| | | |
|_____\__,_|_|\__|_| |_|

	EDITH online. I'll keep the chaos organised.
	What are we pretending is urgent today?
_______________________________________________________________________________
_______________________________________________________________________________
	Logged. Organisation suits you:
	  [T][ ] write report
	Active tasks: 1.
_______________________________________________________________________________
_______________________________________________________________________________
	Logged. Organisation suits you:
	  [D][ ] submit report (by: June 6th)
	Active tasks: 2.
_______________________________________________________________________________
_______________________________________________________________________________
	Logged. Organisation suits you:
	  [E][ ] project meeting (from: June 5th 3pm to: 6pm)
	Active tasks: 3.
_______________________________________________________________________________
_______________________________________________________________________________
	Logged. Organisation suits you:
	  [T][ ] Buy groceries
	Active tasks: 4.
_______________________________________________________________________________
_______________________________________________________________________________
	Marked complete. Miracles do happen:
	  [E][X] project meeting (from: June 5th 3pm to: 6pm)
_______________________________________________________________________________
_______________________________________________________________________________
	Your task situation. Do try to keep up:
	1.[T][ ] Buy groceries
	2.[E][X] project meeting (from: June 5th 3pm to: 6pm)
	3.[D][ ] submit report (by: June 6th)
	4.[T][ ] write report
_______________________________________________________________________________
_______________________________________________________________________________
	Your task situation. Do try to keep up:
	1.[T][ ] write report
	2.[D][ ] submit report (by: June 6th)
	3.[E][X] project meeting (from: June 5th 3pm to: 6pm)
	4.[T][ ] Buy groceries
_______________________________________________________________________________
_______________________________________________________________________________
	Your task situation. Do try to keep up:
	1.[E][X] project meeting (from: June 5th 3pm to: 6pm)
	2.[D][ ] submit report (by: June 6th)
	3.[T][ ] write report
	4.[T][ ] Buy groceries
_______________________________________________________________________________
_______________________________________________________________________________
	Your task situation. Do try to keep up:
	1.[D][ ] submit report (by: June 6th)
	2.[E][X] project meeting (from: June 5th 3pm to: 6pm)
	3.[T][ ] write report
	4.[T][ ] Buy groceries
_______________________________________________________________________________
_______________________________________________________________________________
	Your task situation. Do try to keep up:
	1.[D][ ] submit report (by: June 6th)
	2.[T][ ] write report
	3.[T][ ] Buy groceries
	4.[E][X] project meeting (from: June 5th 3pm to: 6pm)
_______________________________________________________________________________
_______________________________________________________________________________
	Your task situation. Do try to keep up:
	1.[T][ ] write report
	2.[D][ ] submit report (by: June 6th)
	3.[E][X] project meeting (from: June 5th 3pm to: 6pm)
	4.[T][ ] Buy groceries
_______________________________________________________________________________
_______________________________________________________________________________
	Your task situation. Do try to keep up:
	1.[T][ ] Buy groceries
	2.[E][X] project meeting (from: June 5th 3pm to: 6pm)
	3.[D][ ] submit report (by: June 6th)
	4.[T][ ] write report
_______________________________________________________________________________
_______________________________________________________________________________
	EDITH signing off. Try not to create chaos without me.
_______________________________________________________________________________
```

### Test case: show command help

Aim: Verify that help displays the syntax of every available command.

Input:
```text
help
bye
```

Expected output:
```text
_______________________________________________________________________________
 _____    _ _ _   _     
| ____|__| (_) |_| |__  
|  _| / _` | | __| '_ \ 
| |__| (_| | | |_| | | |
|_____\__,_|_|\__|_| |_|

	EDITH online. I'll keep the chaos organised.
	What are we pretending is urgent today?
_______________________________________________________________________________
_______________________________________________________________________________
	Since you asked, here is the briefing:
	Available commands:
	  todo <description>
	  deadline <description> /by <date>
	  event <description> /from <start> /to <end>
	  list
	  find <keyword>
	  mark <number>
	  unmark <number>
	  delete <number>
	  sort alph
	  sort alph desc
	  sort date
	  sort date desc
	  sort status
	  sort added
	  sort added desc
	  help
	  bye
_______________________________________________________________________________
_______________________________________________________________________________
	EDITH signing off. Try not to create chaos without me.
_______________________________________________________________________________
```
