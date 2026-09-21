# UI Test Plan

Each case is run in a fresh Chris process. Expected output is compared exactly, including separators, spaces, and line breaks.

## TC-1 Task lifecycle

**Aim:** Verify creation, listing, marking, unmarking, formatted dates, and exit behavior.

### Input

```text
todo read book
deadline return book /by 2026-12-02 1800
event project meeting /from 2026-12-03 1400 /to 2026-12-03 1600
mark 2
unmark 2
list
bye
```

### Expected output

```text
____________________________________________________________
 Hello! I'm Chris
 What can I do for you?
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] read book
 Now you have 1 task in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [D][ ] return book (by: Dec 02 2026, 6:00pm)
 Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [E][ ] project meeting (from: Dec 03 2026, 2:00pm to: Dec 03 2026, 4:00pm)
 Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
 Nice! I've marked this task as done:
   [D][X] return book (by: Dec 02 2026, 6:00pm)
____________________________________________________________
____________________________________________________________
 OK, I've marked this task as not done yet:
   [D][ ] return book (by: Dec 02 2026, 6:00pm)
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] read book
 2.[D][ ] return book (by: Dec 02 2026, 6:00pm)
 3.[E][ ] project meeting (from: Dec 03 2026, 2:00pm to: Dec 03 2026, 4:00pm)
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

## TC-2 Invalid inputs preserve state

**Aim:** Verify malformed commands are explained and do not add or modify tasks.

### Input

```text
todo
blah
todo valid task
deadline missing date
event meeting /from 2pm
mark two
mark 9
list
bye
```

### Expected output

```text
____________________________________________________________
 Hello! I'm Chris
 What can I do for you?
____________________________________________________________
____________________________________________________________
 OOPS!!! A todo needs a description, e.g., todo read book.
____________________________________________________________
____________________________________________________________
 OOPS!!! I don't recognise that command. Try todo, deadline, event, list, find, mark, unmark, delete, help, or bye.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] valid task
 Now you have 1 task in the list.
____________________________________________________________
____________________________________________________________
 OOPS!!! A deadline needs '/by', e.g., deadline return book /by yyyy-MM-dd HHmm.
____________________________________________________________
____________________________________________________________
 OOPS!!! An event needs '/from' and '/to' times.
____________________________________________________________
____________________________________________________________
 OOPS!!! The task number must be a whole number.
____________________________________________________________
____________________________________________________________
 OOPS!!! Task 9 is not in the list.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] valid task
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

## TC-3 Delete tasks

**Aim:** Verify deletion removes the selected task, shifts numbering, reports the new count, and rejects invalid task numbers.

### Input

```text
todo first task
deadline second task /by 2026-12-02 1800
event third task /from 2026-12-03 1400 /to 2026-12-03 1500
delete 2
list
delete two
delete 9
bye
```

### Expected output

```text
____________________________________________________________
 Hello! I'm Chris
 What can I do for you?
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] first task
 Now you have 1 task in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [D][ ] second task (by: Dec 02 2026, 6:00pm)
 Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [E][ ] third task (from: Dec 03 2026, 2:00pm to: Dec 03 2026, 3:00pm)
 Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
 Noted. I've removed this task:
   [D][ ] second task (by: Dec 02 2026, 6:00pm)
 Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] first task
 2.[E][ ] third task (from: Dec 03 2026, 2:00pm to: Dec 03 2026, 3:00pm)
____________________________________________________________
____________________________________________________________
 OOPS!!! The task number must be a whole number.
____________________________________________________________
____________________________________________________________
 OOPS!!! Task 9 is not in the list.
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```
# Persistence checks

- Start with no `data/chris.txt`; add tasks and verify the directory and file are created.
- Restart Chris and verify saved todo, deadline, event, and completion status are restored.
- Mark, unmark, and delete tasks; restart and verify each change was saved.

# Date and time checks

- Add a deadline using `deadline return book /by 2026-12-02 1800`; verify it displays as `Dec 02 2026, 6:00pm`.
- Add an event with `/from` and `/to` values in `yyyy-MM-dd HHmm`; verify both are reformatted.
- Enter a malformed date and verify Chris explains the expected format without exiting.

JUnit covers Parser task-number validation and Storage save/load behavior; run it with `./gradlew test`.
The TaskList test also confirms that Java assertions are enabled during the test run.

# Find checks

- Add tasks containing `book` in different letter cases and one unrelated task.
- Run `find book`; verify only matching tasks are shown and numbered from 1.
- Run `find` without a keyword; verify Chris explains that a keyword is required.

# JavaFX GUI checks

- Run Chris with `./gradlew run`; verify the window opens with Chris's greeting.
- Verify the title bar says `Chris - Task Companion` and the header, local-tasks badge, and command hint remain visible above and below the conversation.
- Check that small header and command-hint text remains legible at the default window size.
- Verify the command field is focused at startup and remains ready for the next command after pressing Enter or clicking Send.
- Enter `todo read book` by pressing Enter; verify the user command and Chris response appear on opposite sides.
- Enter `list` by clicking Send; verify the saved task appears and the input field is cleared.
- Type an unfinished command, then click **List tasks** and **Help**; verify each command runs, its wider monospaced result card is readable, and the unfinished command remains in the input field.
- Enter `help` and create several long deadline and event descriptions; verify replies wrap inside the window without horizontal scrolling or clipped text.
- Enter an invalid date; verify the error uses a distinct warm colour and remains readable.
- Resize the window down to its minimum width and up to a wide window; verify the header, messages, input and Send button do not overlap or disappear.
- Add enough commands to fill the window; verify the conversation automatically scrolls to the newest message.
- Enter a blank command; verify no dialog boxes are added.
- Enter `bye`; verify the farewell appears and the window closes after a short delay.
- After `bye`, verify the quick-action buttons are disabled during the closing delay.

# Help checks

- Enter `help`; verify Chris displays every supported command and the required date-time syntax.

# Week 6 error-handling checks

- Add a deadline with `2026-02-30 1800`; verify an `OOPS!!!` message explains that the date must exist and `list` remains unchanged.
- Add an event with its `/to` earlier than or equal to `/from`; verify it is rejected without changing the task list.
- Enter `todo bad | separator` and `deadline duplicate /by 2026-12-02 1800 /by 2026-12-03 1800`; verify both are rejected before saving.
- Enter `list all` and `bye now`; verify each is rejected rather than listing/exiting. Enter a command with leading/trailing spaces and verify it still works.
- Start with a hand-edited `data/chris.txt` containing an invalid record. Verify Chris identifies the line, announces read-only mode, refuses additions, and does not overwrite the file. Restore a valid file and restart to recover.
- Start in a writable folder and add two tasks; verify `data/chris.txt` is updated and no temporary file remains. Restart and verify both tasks load.

# Week 6 GUI checks

- Enter an invalid date; verify the reply is visually distinct in a red error bubble, while valid replies use a neutral bubble and user commands use blue bubbles.
- Resize the window horizontally and vertically. Verify text wraps within the chat area, the command box and Send button remain visible, and the newest message can be reached by scrolling.
- Add several tasks and verify the narrow layout does not require horizontal scrolling or obscure the task text.
- Capture a genuine screenshot of the full window with the Chris title and representative task/response before adding it as `docs/Ui.png`.
