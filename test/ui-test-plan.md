# UI Test Plan

run-command: java -cp _temp/ui-test-classes yourhand.YourHand
setup-command: javac -d _temp/ui-test-classes src/main/java/yourhand/YourHand.java src/main/java/yourhand/tasks/*.java src/main/java/yourhand/exceptions/*.java src/main/java/yourhand/storage/*.java src/main/java/yourhand/ui/*.java src/main/java/yourhand/commands/*.java
before-each-command: if exist data\yourhand.txt del /q data\yourhand.txt
working-directory: ..
timeout-seconds: 10

## Manual GUI check: YourHand introduction layout

### Aim

Confirm that the JavaFX interface shows `YourHand` as a header, includes the hand image in the introduction/help card, and places the invalid-command reaction image above its error text.

### Input

```text
Launch the JavaFX application, then enter an invalid command such as `hello` and enter `help`.
```

### Expected output

```text
The header shows the hand image and `YourHand`. The opening introduction and the help card show the hand image inside the response card. For `hello`, the reaction image appears above the invalid-command text.
```

## Manual compatibility checks

### Aim

Confirm that the JavaFX presentation remains usable across common environments that are not covered by unit tests.

### Matrix

Run the application on each available operating system and check at least narrow laptop, 1080p, and high-DPI display settings. Repeat the basic command flow (`help`, add task, `list`, invalid command, `bye`) with English and a non-English OS locale.

### Expected output

The window opens without missing-resource crashes, text remains readable without clipping, the input and Send button remain accessible, and dates, task status, and error messages remain understandable regardless of OS language settings.

## Manual GUI check: corrupted startup data

### Input

Temporarily replace `data/yourhand.txt` with a malformed entry such as:

```text
D | 0 | report | 2026-02-30
```

Launch the GUI and enter `list`.

### Expected output

The GUI displays a red error card containing `Man got hacked ggwp`, starts with an empty task list, and remains usable for subsequent commands.

## Manual GUI check: delayed bye message

### Input

Launch the GUI and enter `bye`.

### Expected output

The chat displays `See you never :)` immediately, remains visible for approximately five seconds, and then the GUI closes automatically.

## Test case: Reject an empty to-do and unknown command

### Aim

Confirm that invalid input is handled through a clear user-visible error message and that the chatbot continues running.

### Input

```text
todo
blah
bye
```

### Expected output

```text
__   __                 _   _                 _
\ \ / /__  _   _ _ __  | | | | __ _ _ __   __| |
 \ V / _ \| | | | '__| | |_| |/ _` | '_ \ / _` |
  | | (_) | |_| | |    |  _  | (_| | | | | (_| |
  |_|\___/ \__,_|_|    |_| |_|\__,_|_| |_|\__,_|
____________________________________________________________
 Selamat Datang 早上好! YourHand 为你服务
 你来这干嘛 What are you here for?
____________________________________________________________
____________________________________________________________
 You handed me an empty to-do. Try: todo borrow book
____________________________________________________________
____________________________________________________________
 Hmm, I don't speak that yet. Try help to see what I can do.
____________________________________________________________
____________________________________________________________
 See you never :)
____________________________________________________________
```

## Test case: Reject malformed deadlines and task references

### Aim

Confirm that malformed task commands and references to a missing task produce specific errors.

### Input

```text
deadline report
mark 1
bye
```

### Expected output

```text
__   __                 _   _                 _
\ \ / /__  _   _ _ __  | | | | __ _ _ __   __| |
 \ V / _ \| | | | '__| | |_| |/ _` | '_ \ / _` |
  | | (_) | |_| | |    |  _  | (_| | | | | (_| |
  |_|\___/ \__,_|_|    |_| |_|\__,_|_| |_|\__,_|
____________________________________________________________
 Selamat Datang 早上好! YourHand 为你服务
 你来这干嘛 What are you here for?
____________________________________________________________
____________________________________________________________
 Bro due when please. Try: deadline DESCRIPTION /by DATE_OR_TIME
____________________________________________________________
____________________________________________________________
 Brother I free how delete stuff. Add one before I can even delete.
____________________________________________________________
____________________________________________________________
 See you never :)
____________________________________________________________
```

## Test case: Mark and unmark a to-do

### Aim

Confirm that a valid task can be added, marked done, marked not done, and displayed with its final status.

### Input

```text
todo read book
mark 1
unmark 1
list
bye
```

### Expected output

```text
__   __                 _   _                 _
\ \ / /__  _   _ _ __  | | | | __ _ _ __   __| |
 \ V / _ \| | | | '__| | |_| |/ _` | '_ \ / _` |
  | | (_) | |_| | |    |  _  | (_| | | | | (_| |
  |_|\___/ \__,_|_|    |_| |_|\__,_|_| |_|\__,_|
____________________________________________________________
 Selamat Datang 早上好! YourHand 为你服务
 你来这干嘛 What are you here for?
____________________________________________________________
____________________________________________________________
 Fine, I've written this down:
   [T][ ] read book
 That's 1 task on your plate.
____________________________________________________________
____________________________________________________________
 Good job for surviving. I'll mark this as done:
   [T][X] read book
____________________________________________________________
____________________________________________________________
 Stop scamming me >:(
   [T][ ] read book
____________________________________________________________
____________________________________________________________
 Here's your list of responsibilities:
 1.[T][ ] read book
____________________________________________________________
____________________________________________________________
 See you never :)
____________________________________________________________
```

## Test case: Preserve a valid to-do after rejecting an empty one

### Aim

Confirm that a valid task remains in the list when a later invalid to-do command is rejected.

### Input

```text
todo buy milk
todo
list
bye
```

### Expected output

```text
__   __                 _   _                 _
\ \ / /__  _   _ _ __  | | | | __ _ _ __   __| |
 \ V / _ \| | | | '__| | |_| |/ _` | '_ \ / _` |
  | | (_) | |_| | |    |  _  | (_| | | | | (_| |
  |_|\___/ \__,_|_|    |_| |_|\__,_|_| |_|\__,_|
____________________________________________________________
 Selamat Datang 早上好! YourHand 为你服务
 你来这干嘛 What are you here for?
____________________________________________________________
____________________________________________________________
 Fine, I've written this down:
   [T][ ] buy milk
 That's 1 task on your plate.
____________________________________________________________
____________________________________________________________
 You handed me an empty to-do. Try: todo borrow book
____________________________________________________________
____________________________________________________________
 Here's your list of responsibilities:
 1.[T][ ] buy milk
____________________________________________________________
____________________________________________________________
 See you never :)
____________________________________________________________
```

## Test case: Preserve a deadline after rejecting an incomplete event

### Aim

Confirm that a malformed event does not alter an already stored deadline.

### Input

```text
deadline submit report /by 2026-10-18
event team meeting /from Monday
list
bye
```

### Expected output

```text
__   __                 _   _                 _
\ \ / /__  _   _ _ __  | | | | __ _ _ __   __| |
 \ V / _ \| | | | '__| | |_| |/ _` | '_ \ / _` |
  | | (_) | |_| | |    |  _  | (_| | | | | (_| |
  |_|\___/ \__,_|_|    |_| |_|\__,_|_| |_|\__,_|
____________________________________________________________
 Selamat Datang 早上好! YourHand 为你服务
 你来这干嘛 What are you here for?
____________________________________________________________
____________________________________________________________
 Fine, I've written this down:
   [D][ ] submit report (by: Oct 18 2026)
 That's 1 task on your plate.
____________________________________________________________
____________________________________________________________
 Walao when the even happening. Try: event DESCRIPTION /from START /to END
____________________________________________________________
____________________________________________________________
 Here's your list of responsibilities:
 1.[D][ ] submit report (by: Oct 18 2026)
____________________________________________________________
____________________________________________________________
 See you never :)
____________________________________________________________
```

## Test case: Preserve completion status after invalid task numbers

### Aim

Confirm that non-numeric and out-of-range task numbers do not change a task's completion status.

### Input

```text
todo read book
mark one
list
mark 2
list
bye
```

### Expected output

```text
__   __                 _   _                 _
\ \ / /__  _   _ _ __  | | | | __ _ _ __   __| |
 \ V / _ \| | | | '__| | |_| |/ _` | '_ \ / _` |
  | | (_) | |_| | |    |  _  | (_| | | | | (_| |
  |_|\___/ \__,_|_|    |_| |_|\__,_|_| |_|\__,_|
____________________________________________________________
 Selamat Datang 早上好! YourHand 为你服务
 你来这干嘛 What are you here for?
____________________________________________________________
____________________________________________________________
 Fine, I've written this down:
   [T][ ] read book
 That's 1 task on your plate.
____________________________________________________________
____________________________________________________________
 Task numbers are whole numbers, not creative writing.
____________________________________________________________
____________________________________________________________
 Here's your list of responsibilities:
 1.[T][ ] read book
____________________________________________________________
____________________________________________________________
 Pick a task number from 1 to 1.
____________________________________________________________
____________________________________________________________
 Here's your list of responsibilities:
 1.[T][ ] read book
____________________________________________________________
____________________________________________________________
 See you never :)
____________________________________________________________
```

## Test case: Delete an event and keep the remaining task order

### Aim

Confirm that deleting a task removes only that task, preserves the remaining tasks and their completion status, and closes the numbering gap.

### Input

```text
todo read book
deadline return book /by 2026-06-06
event project meeting /from 2026-08-06 /to 2026-08-07
mark 1
mark 2
delete 3
list
bye
```

### Expected output

```text
__   __                 _   _                 _
\ \ / /__  _   _ _ __  | | | | __ _ _ __   __| |
 \ V / _ \| | | | '__| | |_| |/ _` | '_ \ / _` |
  | | (_) | |_| | |    |  _  | (_| | | | | (_| |
  |_|\___/ \__,_|_|    |_| |_|\__,_|_| |_|\__,_|
____________________________________________________________
 Selamat Datang 早上好! YourHand 为你服务
 你来这干嘛 What are you here for?
____________________________________________________________
____________________________________________________________
 Fine, I've written this down:
   [T][ ] read book
 That's 1 task on your plate.
____________________________________________________________
____________________________________________________________
 Fine, I've written this down:
   [D][ ] return book (by: Jun 06 2026)
 That's 2 tasks on your plate.
____________________________________________________________
____________________________________________________________
 Fine, I've written this down:
   [E][ ] project meeting (from: Aug 06 2026 to: Aug 07 2026)
 That's 3 tasks on your plate.
____________________________________________________________
____________________________________________________________
 Good job for surviving. I'll mark this as done:
   [T][X] read book
____________________________________________________________
____________________________________________________________
 Good job for surviving. I'll mark this as done:
   [D][X] return book (by: Jun 06 2026)
____________________________________________________________
____________________________________________________________
 Poof. I've removed this task:
   [E][ ] project meeting (from: Aug 06 2026 to: Aug 07 2026)
 That's 2 tasks left on your plate.
____________________________________________________________
____________________________________________________________
 Here's your list of responsibilities:
 1.[T][X] read book
 2.[D][X] return book (by: Jun 06 2026)
____________________________________________________________
____________________________________________________________
 See you never :)
____________________________________________________________
```

## Test case: Preserve tasks after invalid delete commands

### Aim

Confirm that out-of-range and non-numeric delete commands leave the task list unchanged, and that deleting the valid task then leaves an empty list.

### Input

```text
todo buy bread
delete 2
list
delete one
list
delete 1
list
bye
```

### Expected output

```text
__   __                 _   _                 _
\ \ / /__  _   _ _ __  | | | | __ _ _ __   __| |
 \ V / _ \| | | | '__| | |_| |/ _` | '_ \ / _` |
  | | (_) | |_| | |    |  _  | (_| | | | | (_| |
  |_|\___/ \__,_|_|    |_| |_|\__,_|_| |_|\__,_|
____________________________________________________________
 Selamat Datang 早上好! YourHand 为你服务
 你来这干嘛 What are you here for?
____________________________________________________________
____________________________________________________________
 Fine, I've written this down:
   [T][ ] buy bread
 That's 1 task on your plate.
____________________________________________________________
____________________________________________________________
 Pick a task number from 1 to 1.
____________________________________________________________
____________________________________________________________
 Here's your list of responsibilities:
 1.[T][ ] buy bread
____________________________________________________________
____________________________________________________________
 Task numbers are whole numbers, not creative writing.
____________________________________________________________
____________________________________________________________
 Here's your list of responsibilities:
 1.[T][ ] buy bread
____________________________________________________________
____________________________________________________________
 Poof. I've removed this task:
   [T][ ] buy bread
 That's 0 tasks left on your plate.
____________________________________________________________
____________________________________________________________
 Your task list is empty.
____________________________________________________________
____________________________________________________________
 See you never :)
____________________________________________________________
```

## Test case: Keep task status correct after repeated marking and unmarking

### Aim

Confirm that repeated mark and unmark commands do not corrupt task status, and that the final list shows the status from the last valid change.

### Input

```text
todo write essay
mark 1
mark 1
unmark 1
unmark 1
mark 1
list
bye
```

### Expected output

```text
__   __                 _   _                 _
\ \ / /__  _   _ _ __  | | | | __ _ _ __   __| |
 \ V / _ \| | | | '__| | |_| |/ _` | '_ \ / _` |
  | | (_) | |_| | |    |  _  | (_| | | | | (_| |
  |_|\___/ \__,_|_|    |_| |_|\__,_|_| |_|\__,_|
____________________________________________________________
 Selamat Datang 早上好! YourHand 为你服务
 你来这干嘛 What are you here for?
____________________________________________________________
____________________________________________________________
 Fine, I've written this down:
   [T][ ] write essay
 That's 1 task on your plate.
____________________________________________________________
____________________________________________________________
 Good job for surviving. I'll mark this as done:
   [T][X] write essay
____________________________________________________________
____________________________________________________________
 That task was already done. Double-checking never hurts:
   [T][X] write essay
____________________________________________________________
____________________________________________________________
 Stop scamming me >:(
   [T][ ] write essay
____________________________________________________________
____________________________________________________________
 Thanks for reminding me of your laziness...
   [T][ ] write essay
____________________________________________________________
____________________________________________________________
 Good job for surviving. I'll mark this as done:
   [T][X] write essay
____________________________________________________________
____________________________________________________________
 Here's your list of responsibilities:
 1.[T][X] write essay
____________________________________________________________
____________________________________________________________
 See you never :)
____________________________________________________________
```

## Test case: Save a new task without changing the console dialogue

### Aim

Confirm that adding a task still produces the usual response while causing the task list to be saved. After this test, `data/yourhand.txt` should contain `T | 0 | save task`.

### Input

```text
todo save task
bye
```

### Expected output

```text
__   __                 _   _                 _
\ \ / /__  _   _ _ __  | | | | __ _ _ __   __| |
 \ V / _ \| | | | '__| | |_| |/ _` | '_ \ / _` |
  | | (_) | |_| | |    |  _  | (_| | | | | (_| |
  |_|\___/ \__,_|_|    |_| |_|\__,_|_| |_|\__,_|
____________________________________________________________
 Selamat Datang 早上好! YourHand 为你服务
 你来这干嘛 What are you here for?
____________________________________________________________
____________________________________________________________
 Fine, I've written this down:
   [T][ ] save task
 That's 1 task on your plate.
____________________________________________________________
____________________________________________________________
 See you never :)
____________________________________________________________
```

## Test case: Load saved tasks when the chatbot starts

### Aim

Confirm that a fresh program session restores the task type, description, date or time fields, and completion status from the saved data file.

### Setup

```cmd
mkdir data 2> nul & copy /Y test\fixtures\valid-saved-tasks.txt data\yourhand.txt > nul
```

### Input

```text
list
bye
```

### Expected output

```text
__   __                 _   _                 _
\ \ / /__  _   _ _ __  | | | | __ _ _ __   __| |
 \ V / _ \| | | | '__| | |_| |/ _` | '_ \ / _` |
  | | (_) | |_| | |    |  _  | (_| | | | | (_| |
  |_|\___/ \__,_|_|    |_| |_|\__,_|_| |_|\__,_|
____________________________________________________________
 Selamat Datang 早上好! YourHand 为你服务
 你来这干嘛 What are you here for?
____________________________________________________________
____________________________________________________________
 Here's your list of responsibilities:
 1.[T][X] read book
 2.[D][ ] return book (by: Dec 02 2019 6:00pm)
 3.[E][X] team meeting (from: Aug 26 2026 6:00pm to: Aug 26 2026 8:00pm)
____________________________________________________________
____________________________________________________________
 See you never :)
____________________________________________________________
```

## Test case: Reject an entire corrupted data file

### Aim

Confirm that one malformed saved-data line prevents every task in that file from loading.

### Setup

```cmd
mkdir data 2> nul & copy /Y test\fixtures\corrupt-saved-tasks.txt data\yourhand.txt > nul
```

### Input

```text
list
bye
```

### Expected output

```text
__   __                 _   _                 _
\ \ / /__  _   _ _ __  | | | | __ _ _ __   __| |
 \ V / _ \| | | | '__| | |_| |/ _` | '_ \ / _` |
  | | (_) | |_| | |    |  _  | (_| | | | | (_| |
  |_|\___/ \__,_|_|    |_| |_|\__,_|_| |_|\__,_|
____________________________________________________________
 Selamat Datang 早上好! YourHand 为你服务
 你来这干嘛 What are you here for?
____________________________________________________________
 Man got hacked ggwp
____________________________________________________________
 Your task list is empty.
____________________________________________________________
____________________________________________________________
 See you never :)
____________________________________________________________
```

## Test case: Reject the storage delimiter in user task text

### Aim

Confirm that a pipe character is rejected because it would corrupt the saved-data format, and that no task is added.

### Input

```text
todo contains | delimiter
list
bye
```

### Expected output

```text
__   __                 _   _                 _
\ \ / /__  _   _ _ __  | | | | __ _ _ __   __| |
 \ V / _ \| | | | '__| | |_| |/ _` | '_ \ / _` |
  | | (_) | |_| | |    |  _  | (_| | | | | (_| |
  |_|\___/ \__,_|_|    |_| |_|\__,_|_| |_|\__,_|
____________________________________________________________
 Selamat Datang 早上好! YourHand 为你服务
 你来这干嘛 What are you here for?
____________________________________________________________
____________________________________________________________
 Please don't use | in a task. I need it to save your data safely.
____________________________________________________________
____________________________________________________________
 Your task list is empty.
____________________________________________________________
____________________________________________________________
 See you never :)
____________________________________________________________
```

## Test case: Remind the user about duplicate task descriptions

### Aim

Confirm that adding a duplicate description displays a reminder while retaining both tasks.

### Input

```text
todo read book
todo read book
list
bye
```

### Expected output

```text
__   __                 _   _                 _
\ \ / /__  _   _ _ __  | | | | __ _ _ __   __| |
 \ V / _ \| | | | '__| | |_| |/ _` | '_ \ / _` |
  | | (_) | |_| | |    |  _  | (_| | | | | (_| |
  |_|\___/ \__,_|_|    |_| |_|\__,_|_| |_|\__,_|
____________________________________________________________
 Selamat Datang 早上好! YourHand 为你服务
 你来这干嘛 What are you here for?
____________________________________________________________
____________________________________________________________
 Fine, I've written this down:
   [T][ ] read book
 That's 1 task on your plate.
____________________________________________________________
____________________________________________________________
 Heads up: task 1 already has that description. I'll add this one too.
 Fine, I've written this down:
   [T][ ] read book
 That's 2 tasks on your plate.
____________________________________________________________
____________________________________________________________
 Here's your list of responsibilities:
 1.[T][ ] read book
 2.[T][ ] read book
____________________________________________________________
____________________________________________________________
 See you never :)
____________________________________________________________
```

## Test case: Accept supported date and time formats

### Aim

Confirm that slash-format and ISO-format 24-hour times are parsed, displayed in a friendly format, and retained in the task list.

### Input

```text
deadline return book /by 2/12/2019 1800
event project meeting /from 2026-08-26 18:00 /to 2026-08-26 2000
list
bye
```

### Expected output

```text
__   __                 _   _                 _
\ \ / /__  _   _ _ __  | | | | __ _ _ __   __| |
 \ V / _ \| | | | '__| | |_| |/ _` | '_ \ / _` |
  | | (_) | |_| | |    |  _  | (_| | | | | (_| |
  |_|\___/ \__,_|_|    |_| |_|\__,_|_| |_|\__,_|
____________________________________________________________
 Selamat Datang 早上好! YourHand 为你服务
 你来这干嘛 What are you here for?
____________________________________________________________
____________________________________________________________
 Fine, I've written this down:
   [D][ ] return book (by: Dec 02 2019 6:00pm)
 That's 1 task on your plate.
____________________________________________________________
____________________________________________________________
 Fine, I've written this down:
   [E][ ] project meeting (from: Aug 26 2026 6:00pm to: Aug 26 2026 8:00pm)
 That's 2 tasks on your plate.
____________________________________________________________
____________________________________________________________
 Here's your list of responsibilities:
 1.[D][ ] return book (by: Dec 02 2019 6:00pm)
 2.[E][ ] project meeting (from: Aug 26 2026 6:00pm to: Aug 26 2026 8:00pm)
____________________________________________________________
____________________________________________________________
 See you never :)
____________________________________________________________
```

## Test case: Reject invalid and out-of-order task dates

### Aim

Confirm that impossible calendar dates and events ending before they start are rejected without changing the task list.

### Input

```text
deadline impossible /by 2026-02-30
event backwards /from 2026-08-27 /to 2026-08-26
list
bye
```

### Expected output

```text
__   __                 _   _                 _
\ \ / /__  _   _ _ __  | | | | __ _ _ __   __| |
 \ V / _ \| | | | '__| | |_| |/ _` | '_ \ / _` |
  | | (_) | |_| | |    |  _  | (_| | | | | (_| |
  |_|\___/ \__,_|_|    |_| |_|\__,_|_| |_|\__,_|
____________________________________________________________
 Selamat Datang 早上好! YourHand 为你服务
 你来这干嘛 What are you here for?
____________________________________________________________
____________________________________________________________
 Date [2026-02-30] is impossible. Brother even leap year don't even have 30 days what nonsense calendar you using? Check your date again >:(
____________________________________________________________
____________________________________________________________
 Your event must end after it starts.
____________________________________________________________
____________________________________________________________
 Your task list is empty.
____________________________________________________________
____________________________________________________________
 See you never :)
____________________________________________________________
```

## Test case: Accept unpadded ISO month and day values

### Aim

Confirm that valid ISO-style dates with one-digit months or days are accepted while still being displayed in a consistent friendly format.

### Input

```text
event please /from 2222-2-22 /to 3333-3-3
list
bye
```

### Expected output

```text
__   __                 _   _                 _
\ \ / /__  _   _ _ __  | | | | __ _ _ __   __| |
 \ V / _ \| | | | '__| | |_| |/ _` | '_ \ / _` |
  | | (_) | |_| | |    |  _  | (_| | | | | (_| |
  |_|\___/ \__,_|_|    |_| |_|\__,_|_| |_|\__,_|
____________________________________________________________
 Selamat Datang 早上好! YourHand 为你服务
 你来这干嘛 What are you here for?
____________________________________________________________
____________________________________________________________
 Fine, I've written this down:
   [E][ ] please (from: Feb 22 2222 to: Mar 03 3333)
 That's 1 task on your plate.
____________________________________________________________
____________________________________________________________
 Here's your list of responsibilities:
 1.[E][ ] please (from: Feb 22 2222 to: Mar 03 3333)
____________________________________________________________
____________________________________________________________
 See you never :)
____________________________________________________________
```

## Test case: View the schedule for a date

### Aim

Confirm that a deadline due on the requested date is displayed by the schedule command and that an undated to-do is not displayed.

### Input

```text
deadline submit report /by 2026-09-10
todo buy milk
view schedule 2026-09-10
bye
```

### Expected output

```text
__   __                 _   _                 _
\ \ / /__  _   _ _ __  | | | | __ _ _ __   __| |
 \ V / _ \| | | | '__| | |_| |/ _` | '_ \ / _` |
  | | (_) | |_| | |    |  _  | (_| | | | | (_| |
  |_|\___/ \__,_|_|    |_| |_|\__,_|_| |_|\__,_|
____________________________________________________________
 Selamat Datang 早上好! YourHand 为你服务
 你来这干嘛 What are you here for?
____________________________________________________________
____________________________________________________________
 Fine, I've written this down:
   [D][ ] submit report (by: Sept 10 2026)
 That's 1 task on your plate.
____________________________________________________________
____________________________________________________________
 Fine, I've written this down:
   [T][ ] buy milk
 That's 2 tasks on your plate.
____________________________________________________________
____________________________________________________________
 Schedule for 2026-09-10:
 1.[D][ ] submit report (by: Sept 10 2026)
____________________________________________________________
____________________________________________________________
 See you never :)
____________________________________________________________
```
