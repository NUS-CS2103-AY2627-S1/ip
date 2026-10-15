# UI Test Plan

## Project configuration

- Main class: `peanutbuttercat.PeanutButterCat`
- Source directory: `src/main/java`
- Required Java major version: `25`
- Capacity: The task list grows dynamically and has no fixed 100-task limit.
- Isolation: Each test case starts a fresh application process with no `data/peanutbuttercat.txt` file.
- Comparison: Output must match exactly after normalizing CRLF/LF line endings and ignoring one final line terminator.

## Test case: Exit the application
**Aim:** Verify that `bye` prints the farewell and exits cleanly.
### Inputs
```text
bye
```
### Expected output
```text
____________________________________________________________
 /\_/\
( o.o )  PeanutButterCat
 > u <
Hello! I'm PeanutButterCat, your cozy, snack-powered task keeper.
Tell me what's on your plate, and I'll tuck it into the task jar.
____________________________________________________________
____________________________________________________________
The task jar is safe with me. Stay smooth, and see you soon!
____________________________________________________________
```

## Test case: Find tasks by description keyword
**Aim:** Verify that `find` returns tasks whose descriptions contain a case-insensitive keyword.
### Inputs
```text
todo read book
deadline return book /by 06/06/2026 1800
todo buy groceries
find BOOK
find travel
bye
```
### Expected output
```text
____________________________________________________________
 /\_/\
( o.o )  PeanutButterCat
 > u <
Hello! I'm PeanutButterCat, your cozy, snack-powered task keeper.
Tell me what's on your plate, and I'll tuck it into the task jar.
____________________________________________________________
____________________________________________________________
Spread the word - this task is in the jar:
[T][ ] read book
The task jar now holds 1 task.
____________________________________________________________
____________________________________________________________
Spread the word - this task is in the jar:
[D][ ] return book (by: Jun 06 2026 6:00 pm)
The task jar now holds 2 tasks.
____________________________________________________________
____________________________________________________________
Spread the word - this task is in the jar:
[T][ ] buy groceries
The task jar now holds 3 tasks.
____________________________________________________________
____________________________________________________________
I sniffed out these matching tasks:
1.[T][ ] read book
2.[D][ ] return book (by: Jun 06 2026 6:00 pm)
____________________________________________________________
____________________________________________________________
I sniffed out these matching tasks:
No matching crumbs found.
____________________________________________________________
____________________________________________________________
The task jar is safe with me. Stay smooth, and see you soon!
____________________________________________________________
```

## Test case: Parse, format, save, and query dates
**Aim:** Verify that slash and ISO date/time inputs become typed calendar values, display readably, persist, and can be queried by date.
### Inputs
```text
deadline return book /by 2/12/2019 1800
event project meeting /from 2019-12-02 1900 /to 2019-12-02 2000
on 2019-12-02
list
bye
```
### Expected output
```text
____________________________________________________________
 /\_/\
( o.o )  PeanutButterCat
 > u <
Hello! I'm PeanutButterCat, your cozy, snack-powered task keeper.
Tell me what's on your plate, and I'll tuck it into the task jar.
____________________________________________________________
____________________________________________________________
Spread the word - this task is in the jar:
[D][ ] return book (by: Dec 02 2019 6:00 pm)
The task jar now holds 1 task.
____________________________________________________________
____________________________________________________________
Spread the word - this task is in the jar:
[E][ ] project meeting (from: Dec 02 2019 7:00 pm to: Dec 02 2019 8:00 pm)
The task jar now holds 2 tasks.
____________________________________________________________
____________________________________________________________
Here's what's on the plate for Dec 02 2019:
1.[D][ ] return book (by: Dec 02 2019 6:00 pm)
2.[E][ ] project meeting (from: Dec 02 2019 7:00 pm to: Dec 02 2019 8:00 pm)
____________________________________________________________
____________________________________________________________
Here's what's tucked in the task jar:
1.[D][ ] return book (by: Dec 02 2019 6:00 pm)
2.[E][ ] project meeting (from: Dec 02 2019 7:00 pm to: Dec 02 2019 8:00 pm)
____________________________________________________________
____________________________________________________________
The task jar is safe with me. Stay smooth, and see you soon!
____________________________________________________________
```

## Test case: Reject invalid dates and continue
**Aim:** Verify that malformed dates are rejected without adding tasks.
### Inputs
```text
deadline invalid /by no idea
event invalid /from 2019-02-30 /to 2019-03-01
todo recover gracefully
list
bye
```
### Expected output
```text
____________________________________________________________
 /\_/\
( o.o )  PeanutButterCat
 > u <
Hello! I'm PeanutButterCat, your cozy, snack-powered task keeper.
Tell me what's on your plate, and I'll tuck it into the task jar.
____________________________________________________________
____________________________________________________________
I couldn't understand that date. Use yyyy-MM-dd or d/M/yyyy HHmm, purr-lease!
____________________________________________________________
____________________________________________________________
I couldn't understand that date. Use yyyy-MM-dd or d/M/yyyy HHmm, purr-lease!
____________________________________________________________
____________________________________________________________
Spread the word - this task is in the jar:
[T][ ] recover gracefully
The task jar now holds 1 task.
____________________________________________________________
____________________________________________________________
Here's what's tucked in the task jar:
1.[T][ ] recover gracefully
____________________________________________________________
____________________________________________________________
The task jar is safe with me. Stay smooth, and see you soon!
____________________________________________________________
```

## Test case: Explain common command mistakes and continue
**Aim:** Verify that missing details, invalid task numbers, reversed event times, and unknown commands are explained without terminating the application.
### Inputs
```text
todo
mark two
event evening class /from 2026-09-17 2000 /to 2026-09-17 1900
typo
todo recover gracefully
list
bye
```
### Expected output
```text
____________________________________________________________
 /\_/\
( o.o )  PeanutButterCat
 > u <
Hello! I'm PeanutButterCat, your cozy, snack-powered task keeper.
Tell me what's on your plate, and I'll tuck it into the task jar.
____________________________________________________________
____________________________________________________________
Oops, this kitty needs a description for your todo! Please add one after 'todo'.
____________________________________________________________
____________________________________________________________
My paws can only count whole task numbers. Try 'mark 1', for example!
____________________________________________________________
____________________________________________________________
An event must end after it starts. Please check the '/from' and '/to' times!
____________________________________________________________
____________________________________________________________
My whiskers can't sort that command yet. Try another scoop, purr-lease!
____________________________________________________________
____________________________________________________________
Spread the word - this task is in the jar:
[T][ ] recover gracefully
The task jar now holds 1 task.
____________________________________________________________
____________________________________________________________
Here's what's tucked in the task jar:
1.[T][ ] recover gracefully
____________________________________________________________
____________________________________________________________
The task jar is safe with me. Stay smooth, and see you soon!
____________________________________________________________
```

## Test case: Reject ambiguous and duplicate data
**Aim:** Verify that extra spacing is accepted while duplicate tasks, parameters, status updates, invalid query formats, and zero-length events are explained.
### Inputs
```text
   todo   read   book
todo READ book
deadline submit report /by draft /by 2026-10-01
event instant meeting /from 2026-10-01 1200 /to 2026-10-01 1200
on 2026-10-01 1200
unmark 1
mark 1
mark 1
list
bye
```
### Expected output
```text
____________________________________________________________
 /\_/\
( o.o )  PeanutButterCat
 > u <
Hello! I'm PeanutButterCat, your cozy, snack-powered task keeper.
Tell me what's on your plate, and I'll tuck it into the task jar.
____________________________________________________________
____________________________________________________________
Spread the word - this task is in the jar:
[T][ ] read book
The task jar now holds 1 task.
____________________________________________________________
____________________________________________________________
That task is already in the jar. Try adding something different!
____________________________________________________________
____________________________________________________________
Use '/by' only once in each command, purr-lease!
____________________________________________________________
____________________________________________________________
An event must end after it starts. Please check the '/from' and '/to' times!
____________________________________________________________
____________________________________________________________
I couldn't understand that date. Use yyyy-MM-dd, purr-lease!
____________________________________________________________
____________________________________________________________
That task is already active. No change needed!
____________________________________________________________
____________________________________________________________
Paw-some! That's one smooth finish:
  [T][X] read book
____________________________________________________________
____________________________________________________________
That task is already marked as done. No change needed!
____________________________________________________________
____________________________________________________________
Here's what's tucked in the task jar:
1.[T][X] read book
____________________________________________________________
____________________________________________________________
The task jar is safe with me. Stay smooth, and see you soon!
____________________________________________________________
```

## Test case: Show recent completion statistics
**Aim:** Verify that `stats` counts current completions, reflects unmarking, and rejects arguments.
### Inputs
```text
todo submit report
mark 1
stats
unmark 1
stats
stats week
bye
```
### Expected output
```text
____________________________________________________________
 /\_/\
( o.o )  PeanutButterCat
 > u <
Hello! I'm PeanutButterCat, your cozy, snack-powered task keeper.
Tell me what's on your plate, and I'll tuck it into the task jar.
____________________________________________________________
____________________________________________________________
Spread the word - this task is in the jar:
[T][ ] submit report
The task jar now holds 1 task.
____________________________________________________________
____________________________________________________________
Paw-some! That's one smooth finish:
  [T][X] submit report
____________________________________________________________
____________________________________________________________
In the last 7 calendar days, you finished 1 task. Nice spread!
____________________________________________________________
____________________________________________________________
Back on the plate! This task is active again:
  [T][ ] submit report
____________________________________________________________
____________________________________________________________
In the last 7 calendar days, you finished 0 tasks. Nice spread!
____________________________________________________________
____________________________________________________________
No extra toppings needed for statistics! Use: stats
____________________________________________________________
____________________________________________________________
The task jar is safe with me. Stay smooth, and see you soon!
____________________________________________________________
```
