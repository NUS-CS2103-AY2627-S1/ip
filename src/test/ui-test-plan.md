# Console UI test plan

## Personality update expectations

The welcome and command responses should use Judey's playful astronaut-cat personality. In particular, the
welcome output must include `Meowdy! I'm Judey, your astronaut cat assistant.`, successful task creation must
include `Purrfect!`, and goodbye must include `Bye for now, space cadet!`. Existing task data, command syntax,
and validation behavior remain unchanged.

## Test environment

- Compile command: `javac -d out src/main/java/*.java`
- Run command: `java -cp out Judey`
- The `test-ui` skill compiles with Java 25 before each session.

## GUI visual verification

The following checks require launching the JavaFX GUI manually. They are intentionally separate from the console
transcripts because the starfield and dividers are visual layout behavior.

### Starfield and conversation dividers

1. Launch the GUI at its default size and confirm the starfield fills the entire background without stretching.
2. Resize the window to a wide shape, a tall shape, and the minimum supported size. Confirm that the starfield repeats
   horizontally and vertically without blank areas.
3. Confirm that the input controls and all dialog text remain above the starfield and readable.
4. Confirm that the welcome banner has one fixed-length divider line above and below its text.
5. Submit a valid command, an invalid command, `help`, and a command producing a multi-line response. Confirm that
   every Judey response has exactly one fixed-length divider line above and below its text.
6. Confirm that user messages have no divider lines and retain their original bubble styling.

## Event date/time validation

1. Add an event with a non-existent date, such as `30/2/2028`, and confirm that Judey displays:
   `Oopsie! The date/time you entered is invalid. Please use a real date in the format d/M/yyyy HHmm.`
2. Add an event whose start and end are equal and confirm that Judey displays:
   `Oopsie! The event start date/time must be before its end date/time.`
3. Add an event whose end is before its start and confirm the same ordering error is displayed.
4. Add an event whose start is before the current local date/time and confirm that Judey displays:
   `Oopsie! The event start date/time cannot be in the past.`
5. Confirm that rejected events are not displayed in `list` and are not written to storage.
6. Confirm that a previously stored event whose start is in the past still loads, provided its date/time values are
   valid and its start is before its end.

## Task filtering and storage verification

1. Add an event spanning multiple dates, then run `events-on` for its start date, an intermediate date, and its end
   date. Confirm that the event appears on all three dates.
2. Run `events-on` for the date immediately before and after the event range. Confirm that the event is not shown.
3. Add tasks before an event, run `events-on`, and confirm that the displayed task number is its original number in
   the full task list rather than its position in the filtered results.
4. Create a save file containing status values `0`, `1`, and an invalid value such as `2`. Confirm that valid records
   load and the invalid record is skipped with a corruption warning.

## Test case: Exit politely

### Aim

Verify that the application accepts the `bye` command and exits with its farewell message.

### Inputs

```text
bye
```

### Expected output

```text
----------------------------------------
JJJJJJ  uu   uu  dddddd   eeeeeee  yy   yy
   JJ   uu   uu  dd   dd  ee       yy   yy
   JJ   uu   uu  dd   dd  eeeee     yyyyy
JJ JJ   uu   uu  dd   dd  ee         yyy
 JJJ     uuuu u  dddddd   eeeeeee    yyy

Hello! I'm Judey.
What can I do for you?
----------------------------------------
----------------------------------------
Bye. Hope to see you again soon!
----------------------------------------
```

## GUI error highlighting

Manually verify that the original user command remains unchanged in the user bubble and that the GUI displays the
correction in the red error bubble. For `event`, the correction must use `<start date and start time>` and
`<end date and end time>`, with the first missing placeholder highlighted.
For `mark`, `unmark`, and `delete`, missing or invalid task numbers must highlight `<task number>`. For
`events-on`, missing or invalid dates must highlight `<date: d/M/yyyy>`.

Input:

```text
event
```

Expected correction text:

```text
The event is missing a description, start date and start time, and end date and end time.

Try:
event <description> /from <start date and start time: d/M/yyyy HHmm> /to <end date and end time: d/M/yyyy HHmm>
```

Expected correction texts for the newly validated commands:

```text
mark
The mark command is missing a task number.

Try: mark <task number>

unmark nope
The unmark command requires a valid task number.

Try: unmark <task number>

delete 1 extra
The delete command requires a valid task number.

Try: delete <task number>

events-on
The events-on command is missing a date.

Try: events-on <date: d/M/yyyy>

events-on tomorrow
The events-on command requires a valid date.

Try: events-on <date: d/M/yyyy>
```

## Test case: Display help

### Aim

Verify that `help` displays the available commands and examples, then allows the user to continue.

### Inputs

```text
help
bye
```

### Expected output

```text
----------------------------------------
JJJJJJ  uu   uu  dddddd   eeeeeee  yy   yy
   JJ   uu   uu  dd   dd  ee       yy   yy
   JJ   uu   uu  dd   dd  eeeee     yyyyy
JJ JJ   uu   uu  dd   dd  ee         yyy
 JJJ     uuuu u  dddddd   eeeeeee    yyy

Hello! I'm Judey.
What can I do for you?
----------------------------------------
----------------------------------------
----------------------------------------
Judey Help

Creating tasks:
  todo <description>
    Example: todo read book

  deadline <description> /by <date and time>
    Example: deadline submit report /by 2026-12-31 2359

  event <description> /from <date and time> /to <date and time>
    Format: d/M/yyyy HHmm
    Example: event team meeting /from 15/10/2026 1400 /to 15/10/2026 1600

Managing tasks:
  list
    Example: list

  mark <task number>
    Example: mark 1

  unmark <task number>
    Example: unmark 1

  delete <task number>
    Example: delete 1

Searching and filtering:
  events-on <date>
    Example: events-on 2/12/2019

Application:
  help
    Example: help

  bye
    Example: bye
----------------------------------------
----------------------------------------
----------------------------------------
----------------------------------------
Bye. Hope to see you again soon!
----------------------------------------
----------------------------------------
```

## Test case: Reject help arguments

### Aim

Verify that `help` rejects additional arguments.

### Inputs

```text
help extra
bye
```

### Expected output

```text
----------------------------------------
JJJJJJ  uu   uu  dddddd   eeeeeee  yy   yy
   JJ   uu   uu  dd   dd  ee       yy   yy
   JJ   uu   uu  dd   dd  eeeee     yyyyy
JJ JJ   uu   uu  dd   dd  ee         yyy
 JJJ     uuuu u  dddddd   eeeeeee    yyy

Hello! I'm Judey.
What can I do for you?
----------------------------------------
----------------------------------------
----------------------------------------
----------------------------------------
Oopsie! The help command does not take any arguments. Try: help
----------------------------------------
----------------------------------------
----------------------------------------
----------------------------------------
Bye. Hope to see you again soon!
----------------------------------------
----------------------------------------
```

## Test case: Add and list a todo

### Aim

Verify that a `todo` command adds a task and that `list` displays the added task before the session ends.

### Inputs

```text
todo read book
list
bye
```

### Expected output

```text
----------------------------------------
JJJJJJ  uu   uu  dddddd   eeeeeee  yy   yy
   JJ   uu   uu  dd   dd  ee       yy   yy
   JJ   uu   uu  dd   dd  eeeee     yyyyy
JJ JJ   uu   uu  dd   dd  ee         yyy
 JJJ     uuuu u  dddddd   eeeeeee    yyy

Hello! I'm Judey.
What can I do for you?
----------------------------------------
----------------------------------------
Got it. I've added this task 
  [T][ ] read book

Now you have 1 tasks in this list.

----------------------------------------
----------------------------------------
Here are the tasks in your list: 
1.[T][ ] read book

----------------------------------------
----------------------------------------
Bye. Hope to see you again soon!
----------------------------------------
```

## Test case: Find tasks by description

### Aim

Verify that `find` supports case-insensitive multi-word searches, preserves original task numbers, and reports
missing search keywords.

### Inputs

```text
todo Read a book
todo Buy milk
todo Return a book
find READ BOOK
find
bye
```

### Expected output

```text
Here are the matching tasks in your mission log:
1.[T][ ] Read a book
3.[T][ ] Return a book
Oopsie! The find command is missing a search keyword.

Try: find <keyword>
```

## Test case: Reject missing and unknown commands

### Aim

Verify that invalid input is reported through Judey's error handling and that the chatbot continues accepting later commands.

### Inputs

```text
todo
blah
bye
```

### Expected output

```text
----------------------------------------
JJJJJJ  uu   uu  dddddd   eeeeeee  yy   yy
   JJ   uu   uu  dd   dd  ee       yy   yy
   JJ   uu   uu  dd   dd  eeeee     yyyyy
JJ JJ   uu   uu  dd   dd  ee         yyy
 JJJ     uuuu u  dddddd   eeeeeee    yyy

Hello! I'm Judey.
What can I do for you?
----------------------------------------
----------------------------------------
Oopsie! Your todo is missing its mission! Try: todo read book
----------------------------------------
----------------------------------------
Oopsie! Hmm, that command is still a mystery to me. Try todo, deadline, event, list, mark, unmark, delete, or bye.
----------------------------------------
----------------------------------------
Bye. Hope to see you again soon!
----------------------------------------
```

## Test case: Keep task state after invalid status commands

### Aim

Verify that invalid task numbers do not create or alter tasks, while valid status changes still update the intended task.

### Inputs

```text
mark 1
todo study
mark nope
mark 1
unmark 2
list
bye
```

### Expected output

```text
----------------------------------------
JJJJJJ  uu   uu  dddddd   eeeeeee  yy   yy
   JJ   uu   uu  dd   dd  ee       yy   yy
   JJ   uu   uu  dd   dd  eeeee     yyyyy
JJ JJ   uu   uu  dd   dd  ee         yyy
 JJJ     uuuu u  dddddd   eeeeeee    yyy

Hello! I'm Judey.
What can I do for you?
----------------------------------------
----------------------------------------
Oopsie! I checked twice, but that task number is not on the list.
----------------------------------------
----------------------------------------
Got it. I've added this task 
  [T][ ] study

Now you have 1 tasks in this list.

----------------------------------------
----------------------------------------
Oopsie! The mark command requires a valid task number.

Try: mark <task number>
----------------------------------------
----------------------------------------
Nice! I've marked this task as done: 
  [T][X] study

----------------------------------------
----------------------------------------
Oopsie! I checked twice, but that task number is not on the list.
----------------------------------------
----------------------------------------
Here are the tasks in your list: 
1.[T][X] study

----------------------------------------
----------------------------------------
Bye. Hope to see you again soon!
----------------------------------------
```

## Test case: Delete a task

### Aim

Verify that `delete` removes the requested task and renumbers the remaining tasks.

### Inputs

```text
todo read book
deadline return book /by June 6th
event project meeting /from Aug 6th 2pm /to 4pm
delete 3
list
bye
```

### Expected output

```text
----------------------------------------
JJJJJJ  uu   uu  dddddd   eeeeeee  yy   yy
   JJ   uu   uu  dd   dd  ee       yy   yy
   JJ   uu   uu  dd   dd  eeeee     yyyyy
JJ JJ   uu   uu  dd   dd  ee         yyy
 JJJ     uuuu u  dddddd   eeeeeee    yyy

Hello! I'm Judey.
What can I do for you?
----------------------------------------
----------------------------------------
Got it. I've added this task 
  [T][ ] read book

Now you have 1 tasks in this list.

----------------------------------------
----------------------------------------
Got it. I've added this task 
  [D][ ] return book (by: June 6th)

Now you have 2 tasks in this list.

----------------------------------------
----------------------------------------
Got it. I've added this task 
  [E][ ] project meeting (from: Aug 6th 2pm to: 4pm)

Now you have 3 tasks in this list.

----------------------------------------
----------------------------------------
Noted. I've removed this task:
  [E][ ] project meeting (from: Aug 6th 2pm to: 4pm)
Now you have 2 tasks in the list.
----------------------------------------
----------------------------------------
Here are the tasks in your list: 
1.[T][ ] read book
2.[D][ ] return book (by: June 6th)

----------------------------------------
----------------------------------------
Bye. Hope to see you again soon!
----------------------------------------
```

## Test case: Keep task state after malformed deadline and event commands

### Aim

Verify that malformed deadline and event commands add nothing, while the later valid commands create exactly the requested tasks.

### Inputs

```text
deadline report
deadline report /by Friday
event meeting /from 2pm
event meeting /from 2pm /to 3pm
list
bye
```

### Expected output

```text
----------------------------------------
JJJJJJ  uu   uu  dddddd   eeeeeee  yy   yy
   JJ   uu   uu  dd   dd  ee       yy   yy
   JJ   uu   uu  dd   dd  eeeee     yyyyy
JJ JJ   uu   uu  dd   dd  ee         yyy
 JJJ     uuuu u  dddddd   eeeeeee    yyy

Hello! I'm Judey.
What can I do for you?
----------------------------------------
----------------------------------------
Oopsie! That deadline needs a little more sparkle: deadline report /by Friday
----------------------------------------
----------------------------------------
Got it. I've added this task 
  [D][ ] report (by: Friday)

Now you have 1 tasks in this list.

----------------------------------------
----------------------------------------
Oopsie! That event needs a name, /from time, and /to time to get on my calendar.
----------------------------------------
----------------------------------------
Got it. I've added this task 
  [E][ ] meeting (from: 2pm to: 3pm)

Now you have 2 tasks in this list.

----------------------------------------
----------------------------------------
Here are the tasks in your list: 
1.[D][ ] report (by: Friday)
2.[E][ ] meeting (from: 2pm to: 3pm)

----------------------------------------
----------------------------------------
Bye. Hope to see you again soon!
----------------------------------------
```
