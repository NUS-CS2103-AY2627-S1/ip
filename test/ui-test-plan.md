# SlotBot UI test plan

Run the regression case with the project-local `test-ui` skill. The complete
expected output is stored in `text-ui-test/EXPECTED.TXT` and the commands are
stored in `text-ui-test/input.txt`.

## Case 1: Add each task type and handle basic errors

Aim: Verify that invalid todo, deadline, event, and unknown commands show
specific errors without adding tasks. The unknown-command guidance should list
every supported command, including `find`, `reminders`, and `delete`, so users
can recover without consulting another source. Valid task commands should create the
correct task types, show the task count, handle invalid delete inputs safely,
delete a task and renumber the list, and share task-number validation across
mark, unmark, and delete. Verify that leading or trailing whitespace around
`list` and `bye` is handled the same as the plain command. Verify that `find`
displays matching task descriptions in their original order, preserves their
numbers from the full list, and names the search keyword in a clear message
when there are no matches.

Deadline inputs use the Level 8 `yyyy-MM-dd` format and display as `MMM dd yyyy`.
Event inputs use `yyyy-MM-dd HH:mm` and display as `MMM dd yyyy HH:mm`.
Invalid deadline and event date-times should be rejected with a clear invalid
date or time message and without adding tasks.
Extra whitespace around `/by`, `/from`, and `/to` should be accepted. Repeated
separators, task descriptions containing the storage separator `|`, and events
whose start time is equal to or later than their end time should be rejected
without adding tasks.

The regression runner uses a fresh temporary working directory, so the case
also verifies that a missing save file does not prevent first launch and that
the relative `data/slotbot.txt` file is created without touching user data.

Inputs:

```text
todo
blah
deadline
deadline return book
event
event project meeting
event project meeting /from Mon 2pm
todo borrow book
deadline return book /by 2019-10-15
deadline repeated /by 2019-10-16 /by 2019-10-17
todo unsafe | description
event project meeting /from 2019-10-15 14:00 /to 2019-10-15 16:00
event same time /from 2019-10-15 14:00 /to 2019-10-15 14:00
event reversed /from 2019-10-15 16:00 /to 2019-10-15 14:00
todo join sports club
find book
find club
find missing
list
delete
delete abc
delete 99
delete 2
list
mark
unmark
mark 2
unmark 2
mark abc
unmark abc
mark 99
deadline spaced deadline   /by   2019-10-16
bye
```

Expected output:

See `text-ui-test/EXPECTED.TXT`.

## Case 2: Handle save-file errors

Aim: Verify that SlotBot starts with an empty list when it cannot read the
save file, and skips malformed task records while loading other valid records.

Expected warnings:

```text
Warning: Unable to load saved tasks.
Starting with an empty list.
```

```text
Warning: Ignoring invalid task data on line NUMBER.
```

An event record whose start time is equal to or later than its end time is
invalid. Expect SlotBot to warn about that record, skip it, and continue loading
the other valid records.

## Case 3: JavaFX GUI (Level-10)

Launch with `./gradlew.bat run`, or build with `./gradlew.bat shadowJar` and
run `java -jar build/libs/slotbot.jar`. The GUI uses the same relative
`data/slotbot.txt` file as the console. For isolated manual testing, run the
absolute JAR path from a fresh temporary directory.

1. Confirm the welcome message, command input, and Send button appear.
   Confirm SlotBot messages are left-aligned, user messages are right-aligned,
   and each bubble is visually distinct without occupying unnecessary width.
2. Submit `todo read book` with Enter. Expect one user message and one reply,
   an empty input field, and focus ready for the next command.
3. Submit `deadline return book /by 2026-09-10` using Send, then
   `event study /from 2026-09-10 14:00 /to 2026-09-10 15:00`.
   Expect the correct types, dates, and task counts.
4. Run `list`, `find book`, `find missing`, `mark 1`, `unmark 1`, and `delete 3`.
   Expect the existing console semantics and updated task status/counts.
5. Submit empty input and spaces: expect no message or task. Submit `todo`,
   `blah`, `mark 0`, `mark 99`, and an impossible date: expect a specific error
   identifying the invalid date or time, no crash, and no unwanted task changes.
   Confirm error replies use the distinct error colors while valid replies
   retain the normal bot style.
6. Repeat `list` until the conversation scrolls. Confirm new replies are visible
   and older messages remain reachable. While at the bottom, resize the window
   from wide to narrow so existing descriptions wrap onto more lines. The latest
   reply must remain fully visible. Manually scroll up and resize again; the
   conversation must preserve that manual position. After Enter or Send, the
   final line must be visible without further scrolling, including when
   submitting from a scrolled-up position or a narrower window.
   Confirm bubbles remain within the viewport, wrap cleanly, and preserve their
   left/right alignment at narrow and wide window sizes.
7. Close the window and reopen from the same directory. `list` must show the
   saved tasks. Enter `bye extra`: expect an error, not exit. Enter ` bye `:
   expect a goodbye and the window to close after a short delay.
8. Run the text UI regression suite. Its expected transcript must stay unchanged.
9. With an isolated save file containing `broken` followed by `T | 0 | kept`,
   reopen: expect a visible invalid-record warning and `list` to include `kept`.

The retained console entry point is `slotbot.SlotBot`; Gradle also provides
`runCli`. Blank-input suppression is specific to the GUI.

## Case 4: Show upcoming deadline reminders

Aim: Verify that `reminders` reports unfinished deadlines in the seven-day
window, orders them by due date, and shows a clear message when none are due.
Completed deadlines, overdue deadlines, and non-deadline tasks must not appear.

The command uses the current system date. The automated unit tests use a fixed
clock so that the date-window boundaries remain deterministic.

## Maintaining the regression test

1. Edit the Java code.
2. Update `text-ui-test/input.txt` only when adding or changing a scenario.
3. Run `text-ui-test/runtest.bat`.
4. Inspect `text-ui-test/ACTUAL.TXT`.
5. Fix the code and rerun if the output is unexpected.
6. Copy `ACTUAL.TXT` to `EXPECTED.TXT` only after confirming the output is
   correct and intentional.
7. Rerun the test and confirm that it passes.

Do not replace `EXPECTED.TXT` without reviewing `ACTUAL.TXT` first.
