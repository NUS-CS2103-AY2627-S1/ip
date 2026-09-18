# UI test plan

Each test case records its aim, run command, inputs, and complete expected console output. The runner compares output exactly, apart from platform line-ending differences.

## Test case: Exit politely

### Aim

Verify that the chatbot greets the user and exits with its farewell message.

### Run command

```sh
./gradlew --quiet classes && rm -f _temp/ui-test-data.txt && java -cp build/classes/java/main rene.Rene _temp/ui-test-data.txt
```

### Inputs

```text
bye
```

### Expected output

```text
____________________________________________________________
 ____
|  _ \ ___ _ __   ___
| |_) / _ \ '_ \ / _ \
|  _ <  __/ | | |  __/
|_| \_\___|_| |_|\___|
Good day. Rene here, your personal productivity liaison.
Let's align on your priorities for the day.
____________________________________________________________
____________________________________________________________
Thank you for your time. Standing by — let's touch base again soon.
____________________________________________________________
```

## Test case: Save tasks for the next session

### Aim

Verify that Rene saves todos, deadlines, events, and completion status for a later application session.

### Run command

```sh
./gradlew --quiet classes && rm -f _temp/ui-test-persistence.txt && java -cp build/classes/java/main rene.Rene _temp/ui-test-persistence.txt
```

### Inputs

```text
todo read book
deadline return book /by 2026-06-06
event project meeting /from Aug 6th 2pm /to 4pm
mark 2
unmark 2
mark 3
delete 1
bye
```

### Expected output

```text
____________________________________________________________
 ____
|  _ \ ___ _ __   ___
| |_) / _ \ '_ \ / _ \
|  _ <  __/ | | |  __/
|_| \_\___|_| |_|\___|
Good day. Rene here, your personal productivity liaison.
Let's align on your priorities for the day.
____________________________________________________________
____________________________________________________________
 Noted. I've logged this deliverable on your action items:
   [T][ ] read book
 Your backlog now contains 1 action item.
____________________________________________________________
____________________________________________________________
 Noted. I've logged this deliverable on your action items:
   [D][ ] return book (by: Jun 6 2026)
 Your backlog now contains 2 action items.
____________________________________________________________
____________________________________________________________
 Noted. I've logged this deliverable on your action items:
   [E][ ] project meeting (from: Aug 6th 2pm to: 4pm)
 Your backlog now contains 3 action items.
____________________________________________________________
____________________________________________________________
 Well received. I've marked this item as actioned:
   [D][X] return book (by: Jun 6 2026)
____________________________________________________________
____________________________________________________________
 Understood. This item has been reopened:
   [D][ ] return book (by: Jun 6 2026)
____________________________________________________________
____________________________________________________________
 Well received. I've marked this item as actioned:
   [E][X] project meeting (from: Aug 6th 2pm to: 4pm)
____________________________________________________________
____________________________________________________________
 Noted. This action item has been removed from the pipeline:
   [T][ ] read book
 Your backlog now contains 2 action items.
 The remaining action items have been renumbered.
____________________________________________________________
____________________________________________________________
Thank you for your time. Standing by — let's touch base again soon.
____________________________________________________________
```

## Test case: Load tasks from the previous session

### Aim

Verify that Rene reloads every saved task and its completion status when the application starts again.

### Run command

```sh
./gradlew --quiet classes && java -cp build/classes/java/main rene.Rene _temp/ui-test-persistence.txt; ui_status=$?; rm -f _temp/ui-test-persistence.txt; exit $ui_status
```

### Inputs

```text
list
bye
```

### Expected output

```text
____________________________________________________________
 ____
|  _ \ ___ _ __   ___
| |_) / _ \ '_ \ / _ \
|  _ <  __/ | | |  __/
|_| \_\___|_| |_|\___|
Good day. Rene here, your personal productivity liaison.
Let's align on your priorities for the day.
____________________________________________________________
____________________________________________________________
 Here is your current action-item backlog:
 1.[D][ ] return book (by: Jun 6 2026)
 2.[E][X] project meeting (from: Aug 6th 2pm to: 4pm)
____________________________________________________________
____________________________________________________________
Thank you for your time. Standing by — let's touch base again soon.
____________________________________________________________
```

## Test case: Reject repeated status changes

### Aim

Verify that marking a completed task and unmarking an incomplete task are rejected without changing the task's status.

### Run command

```sh
./gradlew --quiet classes && rm -f _temp/ui-test-data.txt && java -cp build/classes/java/main rene.Rene _temp/ui-test-data.txt
```

### Inputs

```text
todo revise notes
unmark 1
mark 1
mark 1
unmark 1
unmark 1
list
bye
```

### Expected output

```text
____________________________________________________________
 ____
|  _ \ ___ _ __   ___
| |_) / _ \ '_ \ / _ \
|  _ <  __/ | | |  __/
|_| \_\___|_| |_|\___|
Good day. Rene here, your personal productivity liaison.
Let's align on your priorities for the day.
____________________________________________________________
____________________________________________________________
 Noted. I've logged this deliverable on your action items:
   [T][ ] revise notes
 Your backlog now contains 1 action item.
____________________________________________________________
____________________________________________________________
 Apologies — This action item has not yet been completed, so there is nothing to reopen.
____________________________________________________________
____________________________________________________________
 Well received. I've marked this item as actioned:
   [T][X] revise notes
____________________________________________________________
____________________________________________________________
 Apologies — This action item has already been completed — no action required.
____________________________________________________________
____________________________________________________________
 Understood. This item has been reopened:
   [T][ ] revise notes
____________________________________________________________
____________________________________________________________
 Apologies — This action item has not yet been completed, so there is nothing to reopen.
____________________________________________________________
____________________________________________________________
 Here is your current action-item backlog:
 1.[T][ ] revise notes
____________________________________________________________
____________________________________________________________
Thank you for your time. Standing by — let's touch base again soon.
____________________________________________________________
```

## Test case: Add and list all task types

### Aim

Verify that todos, deadlines, and events are stored polymorphically and displayed with their type-specific details.

### Run command

```sh
./gradlew --quiet classes && rm -f _temp/ui-test-data.txt && java -cp build/classes/java/main rene.Rene _temp/ui-test-data.txt
```

### Inputs

```text
todo borrow book
deadline return book /by 2026-08-30
event project meeting /from Mon 2pm /to 4pm
deadline do homework /by 2026-09-01
list
bye
```

### Expected output

```text
____________________________________________________________
 ____
|  _ \ ___ _ __   ___
| |_) / _ \ '_ \ / _ \
|  _ <  __/ | | |  __/
|_| \_\___|_| |_|\___|
Good day. Rene here, your personal productivity liaison.
Let's align on your priorities for the day.
____________________________________________________________
____________________________________________________________
 Noted. I've logged this deliverable on your action items:
   [T][ ] borrow book
 Your backlog now contains 1 action item.
____________________________________________________________
____________________________________________________________
 Noted. I've logged this deliverable on your action items:
   [D][ ] return book (by: Aug 30 2026)
 Your backlog now contains 2 action items.
____________________________________________________________
____________________________________________________________
 Noted. I've logged this deliverable on your action items:
   [E][ ] project meeting (from: Mon 2pm to: 4pm)
 Your backlog now contains 3 action items.
____________________________________________________________
____________________________________________________________
 Noted. I've logged this deliverable on your action items:
   [D][ ] do homework (by: Sep 1 2026)
 Your backlog now contains 4 action items.
____________________________________________________________
____________________________________________________________
 Here is your current action-item backlog:
 1.[T][ ] borrow book
 2.[D][ ] return book (by: Aug 30 2026)
 3.[E][ ] project meeting (from: Mon 2pm to: 4pm)
 4.[D][ ] do homework (by: Sep 1 2026)
____________________________________________________________
____________________________________________________________
Thank you for your time. Standing by — let's touch base again soon.
____________________________________________________________
```

## Test case: Mark and unmark a typed task

### Aim

Verify that completion status changes preserve the todo type marker.

### Run command

```sh
./gradlew --quiet classes && rm -f _temp/ui-test-data.txt && java -cp build/classes/java/main rene.Rene _temp/ui-test-data.txt
```

### Inputs

```text
todo submit assignment
mark 1
unmark 1
bye
```

### Expected output

```text
____________________________________________________________
 ____
|  _ \ ___ _ __   ___
| |_) / _ \ '_ \ / _ \
|  _ <  __/ | | |  __/
|_| \_\___|_| |_|\___|
Good day. Rene here, your personal productivity liaison.
Let's align on your priorities for the day.
____________________________________________________________
____________________________________________________________
 Noted. I've logged this deliverable on your action items:
   [T][ ] submit assignment
 Your backlog now contains 1 action item.
____________________________________________________________
____________________________________________________________
 Well received. I've marked this item as actioned:
   [T][X] submit assignment
____________________________________________________________
____________________________________________________________
 Understood. This item has been reopened:
   [T][ ] submit assignment
____________________________________________________________
____________________________________________________________
Thank you for your time. Standing by — let's touch base again soon.
____________________________________________________________
```

## Test case: Reject malformed commands without changing tasks

### Aim

Verify that invalid commands are reported through ReneException, and that later valid commands still operate on the correct task list.

### Run command

```sh
./gradlew --quiet classes && rm -f _temp/ui-test-data.txt && java -cp build/classes/java/main rene.Rene _temp/ui-test-data.txt
```

### Inputs

```text
todo
todo read chapter 3
blah
deadline return book
deadline return book /by 2026-08-28
event group study /from 2pm /to 4pm
event movie /from 7pm
mark nope
mark 9
mark 1
unmark 1
list
bye
```

### Expected output

```text
____________________________________________________________
 ____
|  _ \ ___ _ __   ___
| |_) / _ \ '_ \ / _ \
|  _ <  __/ | | |  __/
|_| \_\___|_| |_|\___|
Good day. Rene here, your personal productivity liaison.
Let's align on your priorities for the day.
____________________________________________________________
____________________________________________________________
 Apologies — A todo requires a description. Try: todo read chapter 3
____________________________________________________________
____________________________________________________________
 Noted. I've logged this deliverable on your action items:
   [T][ ] read chapter 3
 Your backlog now contains 1 action item.
____________________________________________________________
____________________________________________________________
 Apologies — That falls outside my scope of operations. Standard procedures are: todo, deadline, event, list, mark, unmark, delete, find, help, or bye.
____________________________________________________________
____________________________________________________________
 Apologies — A deadline requires /by. Try: deadline submit report /by 2026-08-31
____________________________________________________________
____________________________________________________________
 Noted. I've logged this deliverable on your action items:
   [D][ ] return book (by: Aug 28 2026)
 Your backlog now contains 2 action items.
____________________________________________________________
____________________________________________________________
 Noted. I've logged this deliverable on your action items:
   [E][ ] group study (from: 2pm to: 4pm)
 Your backlog now contains 3 action items.
____________________________________________________________
____________________________________________________________
 Apologies — An event requires /from and /to. Try: event study group /from 2pm /to 4pm
____________________________________________________________
____________________________________________________________
 Apologies — A task position must be a whole number, e.g.: mark 1
____________________________________________________________
____________________________________________________________
 Apologies — No action item with that position was found.
____________________________________________________________
____________________________________________________________
 Well received. I've marked this item as actioned:
   [T][X] read chapter 3
____________________________________________________________
____________________________________________________________
 Understood. This item has been reopened:
   [T][ ] read chapter 3
____________________________________________________________
____________________________________________________________
 Here is your current action-item backlog:
 1.[T][ ] read chapter 3
 2.[D][ ] return book (by: Aug 28 2026)
 3.[E][ ] group study (from: 2pm to: 4pm)
____________________________________________________________
____________________________________________________________
Thank you for your time. Standing by — let's touch base again soon.
____________________________________________________________
```

## Test case: Show command help

### Aim

Verify that the help command lists the syntax of every supported command without changing the task list.

### Run command

```sh
./gradlew --quiet classes && rm -f _temp/ui-test-help.txt && java -cp build/classes/java/main rene.Rene _temp/ui-test-help.txt
```

### Inputs

```text
todo keep this task
help
list
bye
```

### Expected output

```text
____________________________________________________________
 ____
|  _ \ ___ _ __   ___
| |_) / _ \ '_ \ / _ \
|  _ <  __/ | | |  __/
|_| \_\___|_| |_|\___|
Good day. Rene here, your personal productivity liaison.
Let's align on your priorities for the day.
____________________________________________________________
____________________________________________________________
 Noted. I've logged this deliverable on your action items:
   [T][ ] keep this task
 Your backlog now contains 1 action item.
____________________________________________________________
____________________________________________________________
 Per your request, here is the standard operating procedure for working with Rene:
 todo DESCRIPTION
 deadline DESCRIPTION /by yyyy-MM-dd
 event DESCRIPTION /from START /to END
 list
 mark NUMBER
 unmark NUMBER
 delete NUMBER
 find KEYWORD
 help
 bye
____________________________________________________________
____________________________________________________________
 Here is your current action-item backlog:
 1.[T][ ] keep this task
____________________________________________________________
____________________________________________________________
Thank you for your time. Standing by — let's touch base again soon.
____________________________________________________________
```

## Test case: Find tasks by description

### Aim

Verify that find searches task descriptions case-insensitively, preserves matching task details and status,
reports no matches cleanly, and rejects a missing keyword.

### Run command

```sh
./gradlew --quiet classes && rm -f _temp/ui-test-find.txt && java -cp build/classes/java/main rene.Rene _temp/ui-test-find.txt
```

### Inputs

```text
todo read book
deadline return BOOK /by 2026-06-06
event project meeting /from library /to home
mark 1
find book
find PROJECT
find 2026
find missing
find
bye
```

### Expected output

```text
____________________________________________________________
 ____
|  _ \ ___ _ __   ___
| |_) / _ \ '_ \ / _ \
|  _ <  __/ | | |  __/
|_| \_\___|_| |_|\___|
Good day. Rene here, your personal productivity liaison.
Let's align on your priorities for the day.
____________________________________________________________
____________________________________________________________
 Noted. I've logged this deliverable on your action items:
   [T][ ] read book
 Your backlog now contains 1 action item.
____________________________________________________________
____________________________________________________________
 Noted. I've logged this deliverable on your action items:
   [D][ ] return BOOK (by: Jun 6 2026)
 Your backlog now contains 2 action items.
____________________________________________________________
____________________________________________________________
 Noted. I've logged this deliverable on your action items:
   [E][ ] project meeting (from: library to: home)
 Your backlog now contains 3 action items.
____________________________________________________________
____________________________________________________________
 Well received. I've marked this item as actioned:
   [T][X] read book
____________________________________________________________
____________________________________________________________
 Here are the action items matching your search criteria:
 1.[T][X] read book
 2.[D][ ] return BOOK (by: Jun 6 2026)
____________________________________________________________
____________________________________________________________
 Here are the action items matching your search criteria:
 1.[E][ ] project meeting (from: library to: home)
____________________________________________________________
____________________________________________________________
 Here are the action items matching your search criteria:
____________________________________________________________
____________________________________________________________
 Here are the action items matching your search criteria:
____________________________________________________________
____________________________________________________________
 Apologies — A find directive requires a keyword. Try: find book
____________________________________________________________
____________________________________________________________
Thank you for your time. Standing by — let's touch base again soon.
____________________________________________________________
```

## Test case: Mark after deleting the first task

### Aim

Verify that deleting the first task renumbers the remaining tasks and that `mark 1` marks the new first task rather than the removed task.

### Run command

```sh
./gradlew --quiet classes && rm -f _temp/ui-test-data.txt && java -cp build/classes/java/main rene.Rene _temp/ui-test-data.txt
```

### Inputs

```text
todo read book
deadline return book /by 2026-06-06
event project meeting /from Aug 6th 2pm /to 4pm
delete 1
mark 1
list
bye
```

### Expected output

```text
____________________________________________________________
 ____
|  _ \ ___ _ __   ___
| |_) / _ \ '_ \ / _ \
|  _ <  __/ | | |  __/
|_| \_\___|_| |_|\___|
Good day. Rene here, your personal productivity liaison.
Let's align on your priorities for the day.
____________________________________________________________
____________________________________________________________
 Noted. I've logged this deliverable on your action items:
   [T][ ] read book
 Your backlog now contains 1 action item.
____________________________________________________________
____________________________________________________________
 Noted. I've logged this deliverable on your action items:
   [D][ ] return book (by: Jun 6 2026)
 Your backlog now contains 2 action items.
____________________________________________________________
____________________________________________________________
 Noted. I've logged this deliverable on your action items:
   [E][ ] project meeting (from: Aug 6th 2pm to: 4pm)
 Your backlog now contains 3 action items.
____________________________________________________________
____________________________________________________________
 Noted. This action item has been removed from the pipeline:
   [T][ ] read book
 Your backlog now contains 2 action items.
 The remaining action items have been renumbered.
____________________________________________________________
____________________________________________________________
 Well received. I've marked this item as actioned:
   [D][X] return book (by: Jun 6 2026)
____________________________________________________________
____________________________________________________________
 Here is your current action-item backlog:
 1.[D][X] return book (by: Jun 6 2026)
 2.[E][ ] project meeting (from: Aug 6th 2pm to: 4pm)
____________________________________________________________
____________________________________________________________
Thank you for your time. Standing by — let's touch base again soon.
____________________________________________________________
```

## Test case: Reject commands for a deleted only task

### Aim

Verify that deleting the only task empties the list and prevents later mark, unmark, and delete commands from accessing it.

### Run command

```sh
./gradlew --quiet classes && rm -f _temp/ui-test-delete-only.txt && java -cp build/classes/java/main rene.Rene _temp/ui-test-delete-only.txt
```

### Inputs

```text
todo only task
delete 1
mark 1
unmark 1
delete 1
list
bye
```

### Expected output

```text
____________________________________________________________
 ____
|  _ \ ___ _ __   ___
| |_) / _ \ '_ \ / _ \
|  _ <  __/ | | |  __/
|_| \_\___|_| |_|\___|
Good day. Rene here, your personal productivity liaison.
Let's align on your priorities for the day.
____________________________________________________________
____________________________________________________________
 Noted. I've logged this deliverable on your action items:
   [T][ ] only task
 Your backlog now contains 1 action item.
____________________________________________________________
____________________________________________________________
 Noted. This action item has been removed from the pipeline:
   [T][ ] only task
 Your backlog now contains 0 action items.
____________________________________________________________
____________________________________________________________
 Apologies — No action item with that position was found.
____________________________________________________________
____________________________________________________________
 Apologies — No action item with that position was found.
____________________________________________________________
____________________________________________________________
 Apologies — No action item with that position was found.
____________________________________________________________
____________________________________________________________
 Here is your current action-item backlog:
____________________________________________________________
____________________________________________________________
Thank you for your time. Standing by — let's touch base again soon.
____________________________________________________________
```

## Test case: Reject invalid delete positions

### Aim

Verify that nonnumeric, zero, negative, and out-of-range delete positions are rejected without removing a task.

### Run command

```sh
./gradlew --quiet classes && rm -f _temp/ui-test-invalid-delete.txt && java -cp build/classes/java/main rene.Rene _temp/ui-test-invalid-delete.txt
```

### Inputs

```text
todo keep this task
delete nope
delete 0
delete -1
delete 2
list
bye
```

### Expected output

```text
____________________________________________________________
 ____
|  _ \ ___ _ __   ___
| |_) / _ \ '_ \ / _ \
|  _ <  __/ | | |  __/
|_| \_\___|_| |_|\___|
Good day. Rene here, your personal productivity liaison.
Let's align on your priorities for the day.
____________________________________________________________
____________________________________________________________
 Noted. I've logged this deliverable on your action items:
   [T][ ] keep this task
 Your backlog now contains 1 action item.
____________________________________________________________
____________________________________________________________
 Apologies — A task position must be a whole number, e.g.: delete 1
____________________________________________________________
____________________________________________________________
 Apologies — No action item with that position was found.
____________________________________________________________
____________________________________________________________
 Apologies — No action item with that position was found.
____________________________________________________________
____________________________________________________________
 Apologies — No action item with that position was found.
____________________________________________________________
____________________________________________________________
 Here is your current action-item backlog:
 1.[T][ ] keep this task
____________________________________________________________
____________________________________________________________
Thank you for your time. Standing by — let's touch base again soon.
____________________________________________________________
```

## Test case: Parse and reject deadline dates

### Aim

Verify that deadlines accept real ISO dates, display readable dates, and reject impossible or incorrectly formatted dates.

### Run command

```sh
./gradlew --quiet classes && rm -f _temp/ui-test-deadline-dates.txt && java -cp build/classes/java/main rene.Rene _temp/ui-test-deadline-dates.txt
```

### Inputs

```text
deadline leap day /by 2024-02-29
deadline year end /by 2026-12-31
deadline impossible /by 2026-02-29
deadline invalid month /by 2026-13-01
deadline wrong format /by 31-12-2026
deadline short date /by 2026-2-3
list
bye
```

### Expected output

```text
____________________________________________________________
 ____
|  _ \ ___ _ __   ___
| |_) / _ \ '_ \ / _ \
|  _ <  __/ | | |  __/
|_| \_\___|_| |_|\___|
Good day. Rene here, your personal productivity liaison.
Let's align on your priorities for the day.
____________________________________________________________
____________________________________________________________
 Noted. I've logged this deliverable on your action items:
   [D][ ] leap day (by: Feb 29 2024)
 Your backlog now contains 1 action item.
____________________________________________________________
____________________________________________________________
 Noted. I've logged this deliverable on your action items:
   [D][ ] year end (by: Dec 31 2026)
 Your backlog now contains 2 action items.
____________________________________________________________
____________________________________________________________
 Apologies — The due date must be a valid date in yyyy-MM-dd format. Try: deadline submit report /by 2026-08-31
____________________________________________________________
____________________________________________________________
 Apologies — The due date must be a valid date in yyyy-MM-dd format. Try: deadline submit report /by 2026-08-31
____________________________________________________________
____________________________________________________________
 Apologies — The due date must be a valid date in yyyy-MM-dd format. Try: deadline submit report /by 2026-08-31
____________________________________________________________
____________________________________________________________
 Apologies — The due date must be a valid date in yyyy-MM-dd format. Try: deadline submit report /by 2026-08-31
____________________________________________________________
____________________________________________________________
 Here is your current action-item backlog:
 1.[D][ ] leap day (by: Feb 29 2024)
 2.[D][ ] year end (by: Dec 31 2026)
____________________________________________________________
____________________________________________________________
Thank you for your time. Standing by — let's touch base again soon.
____________________________________________________________
```

## Test case: Tolerate surrounding spaces and blank lines

### Aim

Verify that commands with leading/trailing spaces are still recognized, extra spaces inside a description are collapsed, and blank lines are rejected without changing tasks.

### Run command

```sh
./gradlew --quiet classes && rm -f _temp/ui-test-spaces.txt && java -cp build/classes/java/main rene.Rene _temp/ui-test-spaces.txt
```

### Inputs

```text
   list  
  todo   read    chapter 3  
deadline  submit  report  /by 2026-08-31  
list

bye
```

### Expected output

```text
____________________________________________________________
 ____
|  _ \ ___ _ __   ___
| |_) / _ \ '_ \ / _ \
|  _ <  __/ | | |  __/
|_| \_\___|_| |_|\___|
Good day. Rene here, your personal productivity liaison.
Let's align on your priorities for the day.
____________________________________________________________
____________________________________________________________
 Here is your current action-item backlog:
____________________________________________________________
____________________________________________________________
 Noted. I've logged this deliverable on your action items:
   [T][ ] read chapter 3
 Your backlog now contains 1 action item.
____________________________________________________________
____________________________________________________________
 Noted. I've logged this deliverable on your action items:
   [D][ ] submit report (by: Aug 31 2026)
 Your backlog now contains 2 action items.
____________________________________________________________
____________________________________________________________
 Here is your current action-item backlog:
 1.[T][ ] read chapter 3
 2.[D][ ] submit report (by: Aug 31 2026)
____________________________________________________________
____________________________________________________________
 Apologies — No directive detected. Try: help
____________________________________________________________
____________________________________________________________
Thank you for your time. Standing by — let's touch base again soon.
____________________________________________________________
```

## Test case: Reject duplicated markers and missing task numbers

### Aim

Verify that a marker specified more than once, a marker in the wrong order, a missing task number, and an out-of-range task number are all rejected without changing tasks.

### Run command

```sh
./gradlew --quiet classes && rm -f _temp/ui-test-markers.txt && java -cp build/classes/java/main rene.Rene _temp/ui-test-markers.txt
```

### Inputs

```text
todo first task
deadline report /by 2026-08-31 /by 2026-09-01
event study /from 2pm /from 3pm /to 4pm
event study /to 4pm /from 2pm
mark
mark 99999999999999999999
mark 1
list
bye
```

### Expected output

```text
____________________________________________________________
 ____
|  _ \ ___ _ __   ___
| |_) / _ \ '_ \ / _ \
|  _ <  __/ | | |  __/
|_| \_\___|_| |_|\___|
Good day. Rene here, your personal productivity liaison.
Let's align on your priorities for the day.
____________________________________________________________
____________________________________________________________
 Noted. I've logged this deliverable on your action items:
   [T][ ] first task
 Your backlog now contains 1 action item.
____________________________________________________________
____________________________________________________________
 Apologies — Only one /by is permitted per deadline. Try: deadline submit report /by 2026-08-31
____________________________________________________________
____________________________________________________________
 Apologies — An event permits one /from and one /to each. Try: event study group /from 2pm /to 4pm
____________________________________________________________
____________________________________________________________
 Apologies — An event requires /from before /to. Try: event study group /from 2pm /to 4pm
____________________________________________________________
____________________________________________________________
 Apologies — A mark directive requires a task number. Try: mark 1
____________________________________________________________
____________________________________________________________
 Apologies — That task number exceeds my processing capacity. Try: mark 1
____________________________________________________________
____________________________________________________________
 Well received. I've marked this item as actioned:
   [T][X] first task
____________________________________________________________
____________________________________________________________
 Here is your current action-item backlog:
 1.[T][X] first task
____________________________________________________________
____________________________________________________________
Thank you for your time. Standing by — let's touch base again soon.
____________________________________________________________
```

## Test case: Recover valid tasks from a corrupt data file

### Aim

Verify that Rene reports a malformed stored line, keeps valid tasks available,
and blocks changes that could overwrite the original data file.

### Run command

```sh
./gradlew --quiet classes && mkdir -p _temp && printf 'T | 0 | keep first\nmalformed line\nT | 1 | keep last\n' > _temp/ui-test-corrupt.txt && java -cp build/classes/java/main rene.Rene _temp/ui-test-corrupt.txt; ui_status=$?; rm -f _temp/ui-test-corrupt.txt; exit $ui_status
```

### Inputs

```text
list
todo do not save
list
bye
```

### Expected output

```text
____________________________________________________________
 ____
|  _ \ ___ _ __   ___
| |_) / _ \ '_ \ / _ \
|  _ <  __/ | | |  __/
|_| \_\___|_| |_|\___|
Good day. Rene here, your personal productivity liaison.
Let's align on your priorities for the day.
____________________________________________________________
 Apologies — I couldn't understand line 2 in _temp/ui-test-corrupt.txt.
Changes are disabled to protect the original data file. Fix or move the file, then restart Rene.
____________________________________________________________
____________________________________________________________
 Here is your current action-item backlog:
 1.[T][ ] keep first
 2.[T][X] keep last
____________________________________________________________
____________________________________________________________
 Apologies — Changes are disabled because the data file did not load completely. Fix or move it, then restart Rene.
____________________________________________________________
____________________________________________________________
 Here is your current action-item backlog:
 1.[T][ ] keep first
 2.[T][X] keep last
____________________________________________________________
____________________________________________________________
Thank you for your time. Standing by — let's touch base again soon.
____________________________________________________________
```

## Manual tests (A-MoreErrorHandling)

Cases that cannot be automated in the console runner. Verify by hand before the final release.

### Environment issue: data file path is a directory

Start Rene with a directory as its data file:

```sh
rm -rf _temp/ui-test-dir && mkdir _temp/ui-test-dir && java -cp build/classes/java/main rene.Rene _temp/ui-test-dir
```

Expected: the welcome message shows `Apologies — <path> is a folder, not a file, so I cannot read tasks from it.` Rene continues with an empty list in read-only mode and rejects commands that would change it.

### Environment issue: data file is not readable (macOS/Linux)

```sh
rm -f _temp/ui-test-locked.txt && touch _temp/ui-test-locked.txt && chmod 000 _temp/ui-test-locked.txt && java -cp build/classes/java/main rene.Rene _temp/ui-test-locked.txt
```

Expected: the welcome message shows `Apologies — I do not have permission to read <path>. Check the file's permissions and try again.` Rene opens in read-only mode. (Skip this case when running as a user that bypasses file permissions.)

### GUI: blank submission does nothing

In the GUI, press Enter without typing anything: no dialog is added and the input field keeps focus.

### GUI: resize the window (A-BetterGui)

Open the GUI, then drag the window from its default 520x640 to roughly twice
as wide and then back to the minimum (420x560). Expected: the conversation
area and cards reflow (long responses wrap, no horizontal scroll appears, no
clipping), and the composer stays fully visible.

### GUI: error responses are visually distinct (A-BetterGui)

In the GUI, send a command that fails, e.g. `mark 99`. Expected: the error
reply renders in a warm/red-tinted card (instead of the plain white Rene
card) so it catches the eye, while normal replies keep the white card.

### Cross-OS smoke tests (A-MoreTesting)

The GitHub Actions workflow automates the fat-JAR launch, data-file creation,
and save/reload checks on macOS, Windows, and Linux. Before the final release,
manually complete the visual checks on each available OS too:

1. Run the fat JAR from an empty folder: `java -jar rene.jar` — the window opens, the greeting appears, and `data/rene.txt` is created next to the JAR (not in some other location).
2. Add one todo, exit with `bye`, restart, and verify the todo is back.
3. Verify fonts and window rendering at the minimum (420x560) and a large size (e.g. 1200x800): no clipping, cards wrap.
4. Check a system with a non-English locale: the date format in deadline replies stays `MMM d yyyy` (English month names) and nothing crashes.
