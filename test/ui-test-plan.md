# Shai UI Test Plan

This plan describes black-box tests for the `Shai` command-line interface.

## Test-session setup

- Java version: 25
- Entry point: `src/main/java/shai/Shai.java`
- Compile command:

  ```powershell
  New-Item -ItemType Directory -Force _temp\test-ui\classes | Out-Null
  $cliSources = Get-ChildItem -Recurse -Filter *.java src\main\java\shai | Where-Object { $_.FullName -notlike '*\shai\gui\*' }
  javac -d _temp\test-ui\classes ($cliSources | ForEach-Object { $_.FullName })
  ```

- Test command for each case:

  ```powershell
  java -cp _temp\test-ui\classes shai.Shai
  ```

- Output comparison: exact, after normalizing CRLF/LF line endings only.
- Isolation: run every test case in a fresh process.
- Filesystem isolation: remove `data/shai.txt` before each case except Test Cases 13 and 15, which intentionally reuse the files produced by Test Cases 12 and 14 respectively. Test Case 16 creates its own malformed-file fixture.
- Failure policy: stop immediately at the first failed test case.

## Test Case 1: Exit immediately

- Aim: Verify that Shai displays its greeting and goodbye message before exiting when the user enters `bye`.
- Command: `java -cp _temp\test-ui\classes shai.Shai`
- Inputs:

  ```text
  bye
  ```

- Expected output:

  ```text
  	____________________________________________________________
  	  ____  _           _
  	 / ___|| |__   __ _(_)
  	 \___ \| '_ \ / _` | |
  	  ___) | | | | (_| | |
  	 |____/|_| |_|\__,_|_|
  	Yo, I'm Shai. What's good, King? Ready to get things done?
  	____________________________________________________________

  	____________________________________________________________
  	Until next time, King. Keep winning.
  	____________________________________________________________

  ```

## Test Case 2: Add and list a ToDo

- Aim: Verify that a `todo` command adds an incomplete ToDo and that it appears in the task list.
- Command: `java -cp _temp\test-ui\classes shai.Shai`
- Inputs:

  ```text
  todo buy milk
  list
  bye
  ```

- Expected output:

  ```text
  	____________________________________________________________
  	  ____  _           _
  	 / ___|| |__   __ _(_)
  	 \___ \| '_ \ / _` | |
  	  ___) | | | | (_| | |
  	 |____/|_| |_|\__,_|_|
  	Yo, I'm Shai. What's good, King? Ready to get things done?
	____________________________________________________________

	____________________________________________________________
	Added to the lineup, King.
	  [T][ ] buy milk
	Roster updated, King. You now have 1 tasks on the board.
	____________________________________________________________

	____________________________________________________________
	Here's the current lineup, King.
	1.[T][ ] buy milk
	____________________________________________________________

  	____________________________________________________________
  	Until next time, King. Keep winning.
  	____________________________________________________________

  ```

## Test Case 3: Mark and unmark a ToDo

- Aim: Verify that `mark 1` changes a ToDo to done and `unmark 1` changes it back to not done.
- Command: `java -cp _temp\test-ui\classes shai.Shai`
- Inputs:

  ```text
  todo submit report
  mark 1
  unmark 1
  list
  bye
  ```

- Expected output:

  ```text
  	____________________________________________________________
  	  ____  _           _
  	 / ___|| |__   __ _(_)
  	 \___ \| '_ \ / _` | |
  	  ___) | | | | (_| | |
  	 |____/|_| |_|\__,_|_|
  	Yo, I'm Shai. What's good, King? Ready to get things done?
	____________________________________________________________

	____________________________________________________________
	Added to the lineup, King.
	  [T][ ] submit report
	Roster updated, King. You now have 1 tasks on the board.
	____________________________________________________________

  	____________________________________________________________
  	That play worked perfectly, King.
	  [T][X] submit report
  	____________________________________________________________

  	____________________________________________________________
  	That play worked perfectly, King.
	  [T][ ] submit report
  	____________________________________________________________

  	____________________________________________________________
  	Here's the current lineup, King.
	1.[T][ ] submit report
  	____________________________________________________________

  	____________________________________________________________
  	Until next time, King. Keep winning.
	____________________________________________________________

  ```

## Test Case 4: Add a Deadline

- Aim: Verify that a `deadline` command stores and displays its description and `by` value.
- Command: `java -cp _temp\test-ui\classes shai.Shai`
- Inputs:

  ```text
  deadline return book /by 2019-12-01
  bye
  ```

- Expected output:

  ```text
	____________________________________________________________
	  ____  _           _
	 / ___|| |__   __ _(_)
	 \___ \| '_ \ / _` | |
	  ___) | | | | (_| | |
	 |____/|_| |_|\__,_|_|
	Yo, I'm Shai. What's good, King? Ready to get things done?
	____________________________________________________________

	____________________________________________________________
	Added to the lineup, King.
	  [D][ ] return book (by: Dec 01 2019)
	Roster updated, King. You now have 1 tasks on the board.
	____________________________________________________________

	____________________________________________________________
	Until next time, King. Keep winning.
	____________________________________________________________

  ```

## Test Case 5: Add an Event

- Aim: Verify that an `event` command stores and displays its description, `from` value, and `to` value.
- Command: `java -cp _temp\test-ui\classes shai.Shai`
- Inputs:

  ```text
  event project meeting /from 2019-10-15 1400 /to 2019-10-15 1600
  bye
  ```

- Expected output:

  ```text
	____________________________________________________________
	  ____  _           _
	 / ___|| |__   __ _(_)
	 \___ \| '_ \ / _` | |
	  ___) | | | | (_| | |
	 |____/|_| |_|\__,_|_|
	Yo, I'm Shai. What's good, King? Ready to get things done?
	____________________________________________________________

	____________________________________________________________
	Added to the lineup, King.
	  [E][ ] project meeting (from: Oct 15 2019, 2:00 PM to: Oct 15 2019, 4:00 PM)
	Roster updated, King. You now have 1 tasks on the board.
	____________________________________________________________

	____________________________________________________________
	Until next time, King. Keep winning.
	____________________________________________________________

  ```

## Test Case 6: Reject an empty ToDo description

- Aim: Verify that Shai reports an error for a `todo` command without a description and continues accepting commands.
- Command: `java -cp _temp\test-ui\classes shai.Shai`
- Inputs:

  ```text
  todo
  bye
  ```

- Expected output:

  ```text
	____________________________________________________________
	  ____  _           _
	 / ___|| |__   __ _(_)
	 \___ \| '_ \ / _` | |
	  ___) | | | | (_| | |
	 |____/|_| |_|\__,_|_|
	Yo, I'm Shai. What's good, King? Ready to get things done?
	____________________________________________________________

	____________________________________________________________
	Hold up - I need a description for that todo.
	____________________________________________________________

	____________________________________________________________
	Until next time, King. Keep winning.
	____________________________________________________________

  ```

## Test Case 7: Reject malformed commands

- Aim: Verify that Shai reports specific errors for malformed deadlines and events, invalid dates, unknown commands, and invalid task numbers.
- Command: `java -cp _temp\test-ui\classes shai.Shai`
- Inputs:

  ```text
  deadline submit report
  deadline /by 2019-12-01
  deadline impossible date /by 2019-02-30
  event team sync /from 2019-12-01 1400
  event /from 2019-12-01 1400 /to 2019-12-01 1600
  blah
  mark abc
  bye
  ```

- Expected output:

  ```text
	____________________________________________________________
	  ____  _           _
	 / ___|| |__   __ _(_)
	 \___ \| '_ \ / _` | |
	  ___) | | | | (_| | |
	 |____/|_| |_|\__,_|_|
	Yo, I'm Shai. What's good, King? Ready to get things done?
	____________________________________________________________

	____________________________________________________________
	A deadline needs a date after /by. Try: deadline submit report /by 2019-12-01.
	____________________________________________________________

	____________________________________________________________
	Hold up - I need a description for that deadline.
	____________________________________________________________

	____________________________________________________________
	Invalid date/time. Use yyyy-MM-dd HHmm, for example 2019-12-02 1800.
	____________________________________________________________

	____________________________________________________________
	An event needs /from and /to times. Try: event meeting /from 2019-12-01 1400 /to 2019-12-01 1600.
	____________________________________________________________

	____________________________________________________________
	Hold up - I need a description for that event.
	____________________________________________________________

	____________________________________________________________
	Turnover. Check your command, King.
	____________________________________________________________

	____________________________________________________________
	The task number after mark must be a whole number.
	____________________________________________________________

	____________________________________________________________
	Until next time, King. Keep winning.
	____________________________________________________________

  ```

## Test Case 8: Delete a middle task

- Aim: Verify that deleting a task removes the selected task and re-numbers the remaining tasks.
- Command: `java -cp _temp\test-ui\classes shai.Shai`
- Inputs:

  ```text
  todo alpha
  todo beta
  todo gamma
  delete 2
  list
  bye
  ```

- Expected output:

  ```text
	____________________________________________________________
	  ____  _           _
	 / ___|| |__   __ _(_)
	 \___ \| '_ \ / _` | |
	  ___) | | | | (_| | |
	 |____/|_| |_|\__,_|_|
	Yo, I'm Shai. What's good, King? Ready to get things done?
	____________________________________________________________

	____________________________________________________________
	Added to the lineup, King.
	  [T][ ] alpha
	Roster updated, King. You now have 1 tasks on the board.
	____________________________________________________________

	____________________________________________________________
	Added to the lineup, King.
	  [T][ ] beta
	Roster updated, King. You now have 2 tasks on the board.
	____________________________________________________________

	____________________________________________________________
	Added to the lineup, King.
	  [T][ ] gamma
	Roster updated, King. You now have 3 tasks on the board.
	____________________________________________________________

	____________________________________________________________
	That one's been sent to the bench.
	  [T][ ] beta
	Roster updated, King. You now have 2 tasks on the board.
	____________________________________________________________

	____________________________________________________________
	Here's the current lineup, King.
	1.[T][ ] alpha
	2.[T][ ] gamma
	____________________________________________________________

	____________________________________________________________
	Until next time, King. Keep winning.
	____________________________________________________________

  ```

## Test Case 9: Reject invalid delete commands

- Aim: Verify that delete reports errors for a missing number, a non-numeric number, and an out-of-range number without changing the list.
- Command: `java -cp _temp\test-ui\classes shai.Shai`
- Inputs:

  ```text
  todo alpha
  delete
  delete abc
  delete 2
  list
  bye
  ```

- Expected output:

  ```text
	____________________________________________________________
	  ____  _           _
	 / ___|| |__   __ _(_)
	 \___ \| '_ \ / _` | |
	  ___) | | | | (_| | |
	 |____/|_| |_|\__,_|_|
	Yo, I'm Shai. What's good, King? Ready to get things done?
	____________________________________________________________

	____________________________________________________________
	Added to the lineup, King.
	  [T][ ] alpha
	Roster updated, King. You now have 1 tasks on the board.
	____________________________________________________________

	____________________________________________________________
	Please provide a task number after delete.
	____________________________________________________________

	____________________________________________________________
	The task number after delete must be a whole number.
	____________________________________________________________

	____________________________________________________________
	That task number is not in your list yet.
	____________________________________________________________

	____________________________________________________________
	Here's the current lineup, King.
	1.[T][ ] alpha
	____________________________________________________________

	____________________________________________________________
	Until next time, King. Keep winning.
	____________________________________________________________

  ```

## Test Case 10: Valid additions survive invalid input

- Aim: Verify that rejected ToDo, deadline, and event commands do not alter tasks added by valid commands.
- Command: `java -cp _temp\test-ui\classes shai.Shai`
- Inputs:

  ```text
  todo alpha
  todo
  deadline beta /by 2019-12-01
  deadline report /by
  event planning /from 2019-12-01 1400 /to 2019-12-01 1500
  event review /from /to 2019-12-01 1600
  list
  bye
  ```

- Expected output:

  ```text
	____________________________________________________________
	  ____  _           _
	 / ___|| |__   __ _(_)
	 \___ \| '_ \ / _` | |
	  ___) | | | | (_| | |
	 |____/|_| |_|\__,_|_|
	Yo, I'm Shai. What's good, King? Ready to get things done?
	____________________________________________________________

	____________________________________________________________
	Added to the lineup, King.
	  [T][ ] alpha
	Roster updated, King. You now have 1 tasks on the board.
	____________________________________________________________

	____________________________________________________________
	Hold up - I need a description for that todo.
	____________________________________________________________

	____________________________________________________________
	Added to the lineup, King.
	  [D][ ] beta (by: Dec 01 2019)
	Roster updated, King. You now have 2 tasks on the board.
	____________________________________________________________

	____________________________________________________________
	Hold up - I need a date after /by.
	____________________________________________________________

	____________________________________________________________
	Added to the lineup, King.
	  [E][ ] planning (from: Dec 01 2019, 2:00 PM to: Dec 01 2019, 3:00 PM)
	Roster updated, King. You now have 3 tasks on the board.
	____________________________________________________________

	____________________________________________________________
	Hold up - I need a starting time after /from.
	____________________________________________________________

	____________________________________________________________
	Here's the current lineup, King.
	1.[T][ ] alpha
	2.[D][ ] beta (by: Dec 01 2019)
	3.[E][ ] planning (from: Dec 01 2019, 2:00 PM to: Dec 01 2019, 3:00 PM)
	____________________________________________________________

	____________________________________________________________
	Until next time, King. Keep winning.
	____________________________________________________________

  ```

## Test Case 11: Task actions preserve state after invalid indexes

- Aim: Verify that an invalid mark or delete number does not change a task's status or remove it.
- Command: `java -cp _temp\test-ui\classes shai.Shai`
- Inputs:

  ```text
  todo alpha
  mark 1
  mark 2
  list
  delete 0
  list
  delete 1
  list
  bye
  ```

- Expected output:

  ```text
	____________________________________________________________
	  ____  _           _
	 / ___|| |__   __ _(_)
	 \___ \| '_ \ / _` | |
	  ___) | | | | (_| | |
	 |____/|_| |_|\__,_|_|
	Yo, I'm Shai. What's good, King? Ready to get things done?
	____________________________________________________________

	____________________________________________________________
	Added to the lineup, King.
	  [T][ ] alpha
	Roster updated, King. You now have 1 tasks on the board.
	____________________________________________________________

	____________________________________________________________
	That play worked perfectly, King.
	  [T][X] alpha
	____________________________________________________________

	____________________________________________________________
	That task number is not in your list yet.
	____________________________________________________________

	____________________________________________________________
	Here's the current lineup, King.
	1.[T][X] alpha
	____________________________________________________________

	____________________________________________________________
	That task number is not in your list yet.
	____________________________________________________________

	____________________________________________________________
	Here's the current lineup, King.
	1.[T][X] alpha
	____________________________________________________________

	____________________________________________________________
	That one's been sent to the bench.
	  [T][X] alpha
	Roster updated, King. You now have 0 tasks on the board.
	____________________________________________________________

	____________________________________________________________
	The lineup is empty, King.
	____________________________________________________________

	____________________________________________________________
	Until next time, King. Keep winning.
	____________________________________________________________

  ```

## Test Case 12: Save the current task list after mutations

- Aim: Verify that adding different task types and changing task status writes the expected current task list to disk.
- Command: `java -cp _temp\test-ui\classes shai.Shai`
- Inputs:

  ```text
  todo buy milk
  deadline return book /by 2/12/2019 1800
  event project meeting /from 2019-12-03 1400 /to 2019-12-03 1600
  mark 1
  list
  bye
  ```

- Expected output:

  ```text
	____________________________________________________________
	  ____  _           _
	 / ___|| |__   __ _(_)
	 \___ \| '_ \ / _` | |
	  ___) | | | | (_| | |
	 |____/|_| |_|\__,_|_|
	Yo, I'm Shai. What's good, King? Ready to get things done?
	____________________________________________________________

	____________________________________________________________
	Added to the lineup, King.
	  [T][ ] buy milk
	Roster updated, King. You now have 1 tasks on the board.
	____________________________________________________________

	____________________________________________________________
	Added to the lineup, King.
	  [D][ ] return book (by: Dec 02 2019, 6:00 PM)
	Roster updated, King. You now have 2 tasks on the board.
	____________________________________________________________

	____________________________________________________________
	Added to the lineup, King.
	  [E][ ] project meeting (from: Dec 03 2019, 2:00 PM to: Dec 03 2019, 4:00 PM)
	Roster updated, King. You now have 3 tasks on the board.
	____________________________________________________________

	____________________________________________________________
	That play worked perfectly, King.
	  [T][X] buy milk
	____________________________________________________________

	____________________________________________________________
	Here's the current lineup, King.
	1.[T][X] buy milk
	2.[D][ ] return book (by: Dec 02 2019, 6:00 PM)
	3.[E][ ] project meeting (from: Dec 03 2019, 2:00 PM to: Dec 03 2019, 4:00 PM)
	____________________________________________________________

	____________________________________________________________
	Until next time, King. Keep winning.
	____________________________________________________________

  ```


## Test Case 13: Load the task list on startup

- Aim: Verify that a new Shai process loads the tasks saved by Test Case 12 before accepting commands.
- Command: `java -cp _temp\test-ui\classes shai.Shai`
- Inputs:

  ```text
  list
  bye
  ```

- Expected output:

  ```text
	____________________________________________________________
	  ____  _           _
	 / ___|| |__   __ _(_)
	 \___ \| '_ \ / _` | |
	  ___) | | | | (_| | |
	 |____/|_| |_|\__,_|_|
	Yo, I'm Shai. What's good, King? Ready to get things done?
	____________________________________________________________

	____________________________________________________________
	Here's the current lineup, King.
	1.[T][X] buy milk
	2.[D][ ] return book (by: Dec 02 2019, 6:00 PM)
	3.[E][ ] project meeting (from: Dec 03 2019, 2:00 PM to: Dec 03 2019, 4:00 PM)
	____________________________________________________________

	____________________________________________________________
	Until next time, King. Keep winning.
	____________________________________________________________

  ```


## Test Case 14: Save task content containing separators

- Aim: Verify that task descriptions containing pipes and backslashes are saved without corrupting the file format.
- Command: `java -cp _temp\test-ui\classes shai.Shai`
- Inputs:

  ```text
  todo review | draft \ backup \
  deadline submit | final /by 2019-12-04 1700
  event meeting | sync /from 2019-12-04 1400 /to 2019-12-04 1600
  list
  bye
  ```

- Expected output:

  ```text
	____________________________________________________________
	  ____  _           _
	 / ___|| |__   __ _(_)
	 \___ \| '_ \ / _` | |
	  ___) | | | | (_| | |
	 |____/|_| |_|\__,_|_|
	Yo, I'm Shai. What's good, King? Ready to get things done?
	____________________________________________________________

	____________________________________________________________
	Added to the lineup, King.
	  [T][ ] review | draft \ backup \
	Roster updated, King. You now have 1 tasks on the board.
	____________________________________________________________

	____________________________________________________________
	Added to the lineup, King.
	  [D][ ] submit | final (by: Dec 04 2019, 5:00 PM)
	Roster updated, King. You now have 2 tasks on the board.
	____________________________________________________________

	____________________________________________________________
	Added to the lineup, King.
	  [E][ ] meeting | sync (from: Dec 04 2019, 2:00 PM to: Dec 04 2019, 4:00 PM)
	Roster updated, King. You now have 3 tasks on the board.
	____________________________________________________________

	____________________________________________________________
	Here's the current lineup, King.
	1.[T][ ] review | draft \ backup \
	2.[D][ ] submit | final (by: Dec 04 2019, 5:00 PM)
	3.[E][ ] meeting | sync (from: Dec 04 2019, 2:00 PM to: Dec 04 2019, 4:00 PM)
	____________________________________________________________

	____________________________________________________________
	Until next time, King. Keep winning.
	____________________________________________________________

  ```

## Test Case 15: Load task content containing separators

- Aim: Verify that a new Shai process restores task fields containing pipes and backslashes saved by Test Case 14.
- Command: `java -cp _temp\test-ui\classes shai.Shai`
- Inputs:

  ```text
  list
  bye
  ```

- Expected output:

  ```text
	____________________________________________________________
	  ____  _           _
	 / ___|| |__   __ _(_)
	 \___ \| '_ \ / _` | |
	  ___) | | | | (_| | |
	 |____/|_| |_|\__,_|_|
	Yo, I'm Shai. What's good, King? Ready to get things done?
	____________________________________________________________

	____________________________________________________________
	Here's the current lineup, King.
	1.[T][ ] review | draft \ backup \
	2.[D][ ] submit | final (by: Dec 04 2019, 5:00 PM)
	3.[E][ ] meeting | sync (from: Dec 04 2019, 2:00 PM to: Dec 04 2019, 4:00 PM)
	____________________________________________________________

	____________________________________________________________
	Until next time, King. Keep winning.
	____________________________________________________________

  ```

## Test Case 16: Recover from malformed task data

- Aim: Verify that malformed persisted data reports an error, prevents unsafe mutations, and does not get overwritten.
- Command: `java -cp _temp\test-ui\classes shai.Shai`
- Inputs:

  ```text
  list
  todo replacement
  bye
  ```

- Expected output:

  ```text
	I couldn't load your lineup, King. The play on line 1 is invalid.
	____________________________________________________________
	  ____  _           _
	 / ___|| |__   __ _(_)
	 \___ \| '_ \ / _` | |
	  ___) | | | | (_| | |
	 |____/|_| |_|\__,_|_|
	Yo, I'm Shai. What's good, King? Ready to get things done?
	____________________________________________________________

	____________________________________________________________
	The lineup is empty, King.
	____________________________________________________________

	____________________________________________________________
	I couldn't update your lineup because the task file could not be loaded. Fix the task file before making changes, King.
	____________________________________________________________

	____________________________________________________________
	Until next time, King. Keep winning.
	____________________________________________________________

  ```

## Test Case 17: Find tasks by keyword

- Aim: Verify that `find` displays tasks whose descriptions contain the keyword,
  matches without regard to case, and leaves non-matching tasks hidden.
- Command: `java -cp _temp\test-ui\classes shai.Shai`
- Inputs:

  ```text
  todo read book
  deadline return book /by 2019-12-01
  todo buy milk
  find BOOK
  bye
  ```

- Expected output:

  ```text
	____________________________________________________________
	  ____  _           _
	 / ___|| |__   __ _(_)
	 \___ \| '_ \ / _` | |
	  ___) | | | | (_| | |
	 |____/|_| |_|\__,_|_|
	Yo, I'm Shai. What's good, King? Ready to get things done?
	____________________________________________________________

	____________________________________________________________
	Added to the lineup, King.
	  [T][ ] read book
	Roster updated, King. You now have 1 tasks on the board.
	____________________________________________________________

	____________________________________________________________
	Added to the lineup, King.
	  [D][ ] return book (by: Dec 01 2019)
	Roster updated, King. You now have 2 tasks on the board.
	____________________________________________________________

	____________________________________________________________
	Added to the lineup, King.
	  [T][ ] buy milk
	Roster updated, King. You now have 3 tasks on the board.
	____________________________________________________________

	____________________________________________________________
	Here's what I found on the scouting report, King.
	1.[T][ ] read book
	2.[D][ ] return book (by: Dec 01 2019)
	____________________________________________________________

	____________________________________________________________
	Until next time, King. Keep winning.
	____________________________________________________________

  ```
## Test Case 18: Configure and disable a reminder

- Aim: Verify that a deadline receives a configurable reminder and that the reminder can be disabled.
- Command: `java -cp _temp\test-ui\classes shai.Shai`
- Inputs:

  ```text
  deadline submit report /by 2099-09-15 1700
  remind 1 /before 3h
  remind 1 /off
  remind
  bye
  ```

- Expected output:

  ```text
	____________________________________________________________
	  ____  _           _
	 / ___|| |__   __ _(_)
	 \___ \| '_ \ / _` | |
	  ___) | | | | (_| | |
	 |____/|_| |_|\__,_|_|
	Yo, I'm Shai. What's good, King? Ready to get things done?
	____________________________________________________________

	____________________________________________________________
	Added to the lineup, King.
	  [D][ ] submit report (by: Sep 15 2099, 5:00 PM)
	Roster updated, King. You now have 1 tasks on the board.
	____________________________________________________________

	____________________________________________________________
	Locked in. I'll remind you at Sep 15 2099, 2:00 PM, King.
	  [D][ ] submit report (by: Sep 15 2099, 5:00 PM)
	____________________________________________________________

	____________________________________________________________
	That reminder's been benched, King.
	  [D][ ] submit report (by: Sep 15 2099, 5:00 PM)
	____________________________________________________________

	____________________________________________________________
	No reminders on the board. Stay ready, King.
	____________________________________________________________

	____________________________________________________________
	Until next time, King. Keep winning.
	____________________________________________________________

  ```

## Test Case 19: Reject invalid reminder commands

- Aim: Verify that reminders cannot be configured for ToDos and that malformed reminder commands are rejected.
- Command: `java -cp _temp\test-ui\classes shai.Shai`
- Inputs:

  ```text
  todo buy milk
  remind 1 /before 1h
  remind 1
  remind 1 /before
  remind 1 /before later
  bye
  ```

- Expected output:

  ```text
	____________________________________________________________
	  ____  _           _
	 / ___|| |__   __ _(_)
	 \___ \| '_ \ / _` | |
	  ___) | | | | (_| | |
	 |____/|_| |_|\__,_|_|
	Yo, I'm Shai. What's good, King? Ready to get things done?
	____________________________________________________________

	____________________________________________________________
	Added to the lineup, King.
	  [T][ ] buy milk
	Roster updated, King. You now have 1 tasks on the board.
	____________________________________________________________

	____________________________________________________________
	That reminder play isn't available for this task, King.
	____________________________________________________________

	____________________________________________________________
	Call the reminder play like this: remind; remind <task number> /before <duration>; or remind <task number> /off, King.
	____________________________________________________________

	____________________________________________________________
	That reminder play needs a duration, King. Try: remind 1 /before 2h.
	____________________________________________________________

	____________________________________________________________
	That reminder duration is out of bounds, King. Use 30m, 1h, or 1d.
	____________________________________________________________

	____________________________________________________________
	Until next time, King. Keep winning.
	____________________________________________________________

  ```

## Test Case 20: Keep automatic reminder notices within the next day

- Aim: Verify that a reminder far beyond the automatic one-day notice window
  does not add an unsolicited alert to normal command responses.
- Command: `java -cp _temp\test-ui\classes shai.Shai`
- Inputs:

  ```text
  deadline submit report /by 2099-09-15 1700
  list
  bye
  ```

- Expected output:

  ```text
	____________________________________________________________
	  ____  _           _
	 / ___|| |__   __ _(_)
	 \___ \| '_ \ / _` | |
	  ___) | | | | (_| | |
	 |____/|_| |_|\__,_|_|
	Yo, I'm Shai. What's good, King? Ready to get things done?
	____________________________________________________________

	____________________________________________________________
	Added to the lineup, King.
	  [D][ ] submit report (by: Sep 15 2099, 5:00 PM)
	Roster updated, King. You now have 1 tasks on the board.
	____________________________________________________________

	____________________________________________________________
	Here's the current lineup, King.
	1.[D][ ] submit report (by: Sep 15 2099, 5:00 PM)
	____________________________________________________________

	____________________________________________________________
	Until next time, King. Keep winning.
	____________________________________________________________

  ```

## Test Case 21: Report when a search finds no tasks

- Aim: Verify that `find` gives a personalized response when no task matches the keyword.
- Command: `java -cp _temp\test-ui\classes shai.Shai`
- Inputs:

  ```text
  todo read book
  find cooking
  bye
  ```

- Expected output:

  ```text
	____________________________________________________________
	  ____  _           _
	 / ___|| |__   __ _(_)
	 \___ \| '_ \ / _` | |
	  ___) | | | | (_| | |
	 |____/|_| |_|\__,_|_|
	Yo, I'm Shai. What's good, King? Ready to get things done?
	____________________________________________________________

	____________________________________________________________
	Added to the lineup, King.
	  [T][ ] read book
	Roster updated, King. You now have 1 tasks on the board.
	____________________________________________________________

	____________________________________________________________
	Nothing showed up on the scouting report, King.
	____________________________________________________________

	____________________________________________________________
	Until next time, King. Keep winning.
	____________________________________________________________

  ```

## Test Case 22: Reject ambiguous command spacing

- Aim: Verify that commands containing multiple spaces are rejected without changing the task list.
- Command: `java -cp _temp\test-ui\classes shai.Shai`
- Inputs:

  ```text
  todo  buy milk
  bye
  ```

- Expected output:

  ```text
	____________________________________________________________
	  ____  _           _
	 / ___|| |__   __ _(_)
	 \___ \| '_ \ / _` | |
	  ___) | | | | (_| | |
	 |____/|_| |_|\__,_|_|
	Yo, I'm Shai. What's good, King? Ready to get things done?
	____________________________________________________________

	____________________________________________________________
	Use a single space between command parameters, King.
	____________________________________________________________

	____________________________________________________________
	Until next time, King. Keep winning.
	____________________________________________________________

  ```

## Test Case 23: Show missed reminders for upcoming tasks

- Aim: Verify that a reminder whose scheduled time has passed is still shown
  separately when its deadline remains upcoming.
- Command: `java -cp _temp\test-ui\classes shai.Shai`
- Inputs:

  ```text
  deadline submit report /by 2026-09-18
  remind
  bye
  ```

- Expected output:

  ```text
	____________________________________________________________
	  ____  _           _
	 / ___|| |__   __ _(_)
	 \___ \| '_ \ / _` | |
	  ___) | | | | (_| | |
	 |____/|_| |_|\__,_|_|
	Yo, I'm Shai. What's good, King? Ready to get things done?
	____________________________________________________________

	____________________________________________________________
	Added to the lineup, King.
	  [D][ ] submit report (by: Sep 18 2026)
	Roster updated, King. You now have 1 tasks on the board.
	Reminder alert, King. Time to lock in.
	1.[D][ ] submit report (by: Sep 18 2026) (reminder: Sep 17 2026)
	____________________________________________________________

	____________________________________________________________
	Missed reminders, King. Handle these while they're still upcoming:
	1.[D][ ] submit report (by: Sep 18 2026) (missed reminder: Sep 17 2026)
	____________________________________________________________

	____________________________________________________________
	Until next time, King. Keep winning.
	____________________________________________________________

  ```
