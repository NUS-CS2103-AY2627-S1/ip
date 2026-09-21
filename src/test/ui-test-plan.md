# Console UI test plan

Run these cases with `python .codex/skills/test-ui/scripts/run_ui_tests.py` from the repository root. Compile the program first with Java 25.

## GUI smoke testing

Use your computer use plugin to test the basic functionality of the chatbot GUI. Let me know if I need to
enable/configure anything to help with that. Check startup, a valid command, an invalid command, Enter-to-send,
the Send button, scrolling, and resizing. If native desktop control is unavailable, report that limitation and use
the manual checklist instead of claiming that the GUI was tested through computer control.

Manual checklist: confirm that messages wrap at minimum, default, and wide window sizes; errors use the coral
presentation; blank input creates no messages; input focus returns after submission; avatars stay 52 px; and the
conversation scrolls to the newest response.

Because Echo restores saved tasks at startup, every case deletes the `data` directory before it starts, so cases are independent of execution order and leftovers.

## Dates and times (Level-8)

Deadline and event times are entered as `yyyy-MM-dd HHmm`, e.g. `2019-12-02 1800`. Echo parses them into `java.time.LocalDateTime` objects and displays them differently, e.g. `Dec 02 2019, 6:00 PM`. Any other shape, including the slash style `2/12/2019 1800` or a date without a time, is rejected with a format error naming the accepted format; see the "date values are validated and formatted" case.

## Side effects: automatic saving and startup loading

Every successful `todo`, `deadline`, `event`, `mark`, `unmark`, or `delete` command silently rewrites `./data/echo.txt`, relative to the working directory. The file stores one pipe-delimited line per task: type letter (`T`/`D`/`E`), an optional completion date-time (blank when incomplete), description, then the deadline's due date or the event's start and end times. Dates are written as ISO-8601 text (`yyyy-MM-ddTHH:mm`). Running the "add and list all task types" case from a clean state must leave:

```text
T |  | borrow book
D |  | return book | 2019-12-02T18:00
E |  | project meeting | 2019-12-02T14:00 | 2019-12-02T16:00
```

On startup Echo reads `data/echo.txt` (if present) and restores those tasks before greeting; a missing file starts an empty list, and any malformed or duplicate line rejects the entire file with a line-numbered storage warning. Task details must not contain `|` because it separates saved fields; inputs that do are rejected outright (see the "pipe characters are rejected" case). The previous `T | 0 | description` completion-flag format is not supported. To verify persistence manually from a known state:

1. Delete `data/echo.txt`, run one session that adds `todo borrow book`, then quit with `bye`.
2. Start a new session entering `list`: `[T][ ] borrow book` must appear.
3. Re-running the same session after `mark 1` must show `[T][X] borrow book`; its save line has a nonblank completion date-time.

Successful saves and loads produce no console output, so the expected transcripts below do not change.
## Test case: greeting and graceful exit

- **Aim:** Verify that Echo presents its greeting and exits cleanly when the user enters `bye`.
- **Command:** `(if exist data rmdir /s /q data) & java -cp build/classes/java/main echo.Echo`
- **Inputs:**
```text
bye
```
- **Expected output:**
```text
 _____     _           
| ____|___| |__   ___  
|  _| / __| '_ \ / _ \ 
| |__| (__| | | | (_) |
|_____\___|_| |_|\___/ 

Signal acquired. Echo here, your feline mission controller.
What are we tackling today?
Echo going off-air. See you on the next mission!
```

## Test case: add and list all task types

- **Aim:** Verify that ToDos, Deadlines, and Events are stored polymorphically, accept `yyyy-MM-dd HHmm` dates, and display them reformatted.
- **Command:** `(if exist data rmdir /s /q data) & java -cp build/classes/java/main echo.Echo`
- **Inputs:**
```text
todo borrow book
deadline return book /by 2019-12-02 1800
event project meeting /from 2019-12-02 1400 /to 2019-12-02 1600
list
bye
```
- **Expected output:**
```text
 _____     _           
| ____|___| |__   ___  
|  _| / __| '_ \ / _ \ 
| |__| (__| | | | (_) |
|_____\___|_| |_|\___/ 

Signal acquired. Echo here, your feline mission controller.
What are we tackling today?
Mission logged:
[T][ ] borrow book
You now have 1 mission on the manifest.
Mission logged:
[D][ ] return book (by: Dec 02 2019, 6:00 PM)
You now have 2 missions on the manifest.
Mission logged:
[E][ ] project meeting (from: Dec 02 2019, 2:00 PM to: Dec 02 2019, 4:00 PM)
You now have 3 missions on the manifest.
Current mission manifest:
1.[T][ ] borrow book
2.[D][ ] return book (by: Dec 02 2019, 6:00 PM)
3.[E][ ] project meeting (from: Dec 02 2019, 2:00 PM to: Dec 02 2019, 4:00 PM)
Echo going off-air. See you on the next mission!
```

## Test case: invalid commands show helpful errors

- **Aim:** Verify that Echo catches invalid commands and malformed task inputs without terminating the session.
- **Command:** `(if exist data rmdir /s /q data) & java -cp build/classes/java/main echo.Echo`
- **Inputs:**
```text
todo
blah
deadline submit report
event project meeting /from Monday
mark one
unmark 1
bye
```
- **Expected output:**
```text
 _____     _           
| ____|___| |__   ___  
|  _| / __| '_ \ / _ \ 
| |__| (__| | | | (_) |
|_____\___|_| |_|\___/ 

Signal acquired. Echo here, your feline mission controller.
What are we tackling today?
Signal scrambled.
I need a mission description. Try: todo <description>
Signal scrambled.
I don't recognise that transmission. Try todo, deadline, event, list, find, mark, unmark, delete, stats, or bye.
Signal scrambled.
I need a description and valid deadline. Try: deadline <description> /by <yyyy-MM-dd HHmm>
Example: deadline return book /by 2019-12-02 1800
Signal scrambled.
I need a description and valid times. Try: event <description> /from <yyyy-MM-dd HHmm> /to <yyyy-MM-dd HHmm>
Example: event party /from 2019-12-02 1800 /to 2019-12-02 2100
Signal scrambled.
That mission number is not on the manifest. Use list to check the available numbers.
Signal scrambled.
That mission number is not on the manifest. Use list to check the available numbers.
Echo going off-air. See you on the next mission!
```

## Test case: errors do not change existing tasks

- **Aim:** Verify that rejected commands, including dates in an invalid format such as month 13, leave the task list unchanged while valid commands before and after them still work.
- **Command:** `(if exist data rmdir /s /q data) & java -cp build/classes/java/main echo.Echo`
- **Inputs:**
```text
todo
todo read book
mark 1
deadline submit report
deadline bad /by 2019-13-45 9999
event team sync /from 2019-08-06 1000 /to 2019-08-06 1100
unmark 3
unmark 1
blah
list
bye
```
- **Expected output:**
```text
 _____     _           
| ____|___| |__   ___  
|  _| / __| '_ \ / _ \ 
| |__| (__| | | | (_) |
|_____\___|_| |_|\___/ 

Signal acquired. Echo here, your feline mission controller.
What are we tackling today?
Signal scrambled.
I need a mission description. Try: todo <description>
Mission logged:
[T][ ] read book
You now have 1 mission on the manifest.
Mission complete. Nicely landed:
[T][X] read book
Signal scrambled.
I need a description and valid deadline. Try: deadline <description> /by <yyyy-MM-dd HHmm>
Example: deadline return book /by 2019-12-02 1800
Signal scrambled.
I need a description and valid deadline. Try: deadline <description> /by <yyyy-MM-dd HHmm>
Example: deadline return book /by 2019-12-02 1800
Mission logged:
[E][ ] team sync (from: Aug 06 2019, 10:00 AM to: Aug 06 2019, 11:00 AM)
You now have 2 missions on the manifest.
Signal scrambled.
That mission number is not on the manifest. Use list to check the available numbers.
Mission reopened. It's back on the radar:
[T][ ] read book
Signal scrambled.
I don't recognise that transmission. Try todo, deadline, event, list, find, mark, unmark, delete, stats, or bye.
Current mission manifest:
1.[T][ ] read book
2.[E][ ] team sync (from: Aug 06 2019, 10:00 AM to: Aug 06 2019, 11:00 AM)
Echo going off-air. See you on the next mission!
```

## Test case: malformed task fields do not consume task numbers

- **Aim:** Verify that unparseable or incomplete deadline and event fields, plus a command with an unrecognised keyword, do not affect later valid tasks or their numbers.
- **Command:** `(if exist data rmdir /s /q data) & java -cp build/classes/java/main echo.Echo`
- **Inputs:**
```text
deadline plan /by someday
todo practice Java
event workshop /from 2019-10-01 0900 /to
deadline pay bill /by 2019-09-06 1200
mark 2
todoist typo
list
bye
```
- **Expected output:**
```text
 _____     _           
| ____|___| |__   ___  
|  _| / __| '_ \ / _ \ 
| |__| (__| | | | (_) |
|_____\___|_| |_|\___/ 

Signal acquired. Echo here, your feline mission controller.
What are we tackling today?
Signal scrambled.
I need a description and valid deadline. Try: deadline <description> /by <yyyy-MM-dd HHmm>
Example: deadline return book /by 2019-12-02 1800
Mission logged:
[T][ ] practice Java
You now have 1 mission on the manifest.
Signal scrambled.
I need a description and valid times. Try: event <description> /from <yyyy-MM-dd HHmm> /to <yyyy-MM-dd HHmm>
Example: event party /from 2019-12-02 1800 /to 2019-12-02 2100
Mission logged:
[D][ ] pay bill (by: Sep 06 2019, 12:00 PM)
You now have 2 missions on the manifest.
Mission complete. Nicely landed:
[D][X] pay bill (by: Sep 06 2019, 12:00 PM)
Signal scrambled.
I don't recognise that transmission. Try todo, deadline, event, list, find, mark, unmark, delete, stats, or bye.
Current mission manifest:
1.[T][ ] practice Java
2.[D][X] pay bill (by: Sep 06 2019, 12:00 PM)
Echo going off-air. See you on the next mission!
```

## Test case: delete a task and renumber the remaining list

- **Aim:** Verify that `delete` removes the specified task, reports the new task count, and leaves the remaining tasks in their correct order with consecutive list numbers.
- **Command:** `(if exist data rmdir /s /q data) & java -cp build/classes/java/main echo.Echo`
- **Inputs:**
```text
todo read book
deadline return book /by 2019-06-06 1800
event project meeting /from 2019-08-06 1400 /to 2019-08-06 1600
todo borrow book
delete 3
list
bye
```
- **Expected output:**
```text
 _____     _           
| ____|___| |__   ___  
|  _| / __| '_ \ / _ \ 
| |__| (__| | | | (_) |
|_____\___|_| |_|\___/ 

Signal acquired. Echo here, your feline mission controller.
What are we tackling today?
Mission logged:
[T][ ] read book
You now have 1 mission on the manifest.
Mission logged:
[D][ ] return book (by: Jun 06 2019, 6:00 PM)
You now have 2 missions on the manifest.
Mission logged:
[E][ ] project meeting (from: Aug 06 2019, 2:00 PM to: Aug 06 2019, 4:00 PM)
You now have 3 missions on the manifest.
Mission logged:
[T][ ] borrow book
You now have 4 missions on the manifest.
Mission removed from the manifest:
[E][ ] project meeting (from: Aug 06 2019, 2:00 PM to: Aug 06 2019, 4:00 PM)
You now have 3 missions on the manifest.
Current mission manifest:
1.[T][ ] read book
2.[D][ ] return book (by: Jun 06 2019, 6:00 PM)
3.[T][ ] borrow book
Echo going off-air. See you on the next mission!
```

## Test case: find filters tasks by description keyword

- **Aim:** Verify that `find` lists only tasks whose description contains the keyword while keeping their full-list numbers usable for mark/unmark/delete, that matching is case-sensitive, and that a missing keyword reports a format error.
- **Command:** `(if exist data rmdir /s /q data) & java -cp build/classes/java/main echo.Echo`
- **Inputs:**
```text
todo read book
deadline return book /by 2019-06-06 1800
event project meeting /from 2019-08-06 1400 /to 2019-08-06 1600
todo borrow newspaper
find book
find BOOK
find
bye
```
- **Expected output:**
```text
 _____     _           
| ____|___| |__   ___  
|  _| / __| '_ \ / _ \ 
| |__| (__| | | | (_) |
|_____\___|_| |_|\___/ 

Signal acquired. Echo here, your feline mission controller.
What are we tackling today?
Mission logged:
[T][ ] read book
You now have 1 mission on the manifest.
Mission logged:
[D][ ] return book (by: Jun 06 2019, 6:00 PM)
You now have 2 missions on the manifest.
Mission logged:
[E][ ] project meeting (from: Aug 06 2019, 2:00 PM to: Aug 06 2019, 4:00 PM)
You now have 3 missions on the manifest.
Mission logged:
[T][ ] borrow newspaper
You now have 4 missions on the manifest.
Radar found these matching missions:
1.[T][ ] read book
2.[D][ ] return book (by: Jun 06 2019, 6:00 PM)
Nothing appeared on the radar for 'BOOK'.
Signal scrambled.
I need a search term. Try: find <keyword>
Echo going off-air. See you on the next mission!
```

## Test case: missing task numbers report format errors

- **Aim:** Verify that mark, unmark, and delete without a task number each report their command-specific format error instead of the generic invalid-number message.
- **Command:** `(if exist data rmdir /s /q data) & java -cp build/classes/java/main echo.Echo`
- **Inputs:**
```text
mark
unmark
delete
bye
```
- **Expected output:**
```text
 _____     _           
| ____|___| |__   ___  
|  _| / __| '_ \ / _ \ 
| |__| (__| | | | (_) |
|_____\___|_| |_|\___/ 

Signal acquired. Echo here, your feline mission controller.
What are we tackling today?
Signal scrambled.
I need a mission number. Try: mark <taskNumber>
Signal scrambled.
I need a mission number. Try: unmark <taskNumber>
Signal scrambled.
I need a mission number. Try: delete <taskNumber>
Echo going off-air. See you on the next mission!
```

## Test case: show statistics and reject arguments

- **Aim:** Verify that `stats` reports global and current-week completion counts without changing tasks, and rejects any arguments.
- **Command:** `(if exist data rmdir /s /q data) & java -cp build/classes/java/main echo.Echo`
- **Inputs:**
```text
todo read book
deadline return book /by 2019-12-02 1800
event project meeting /from 2019-12-02 1400 /to 2019-12-02 1600
mark 1
mark 2
stats
stats today
bye
```
- **Expected output:**
```text
 _____     _           
| ____|___| |__   ___  
|  _| / __| '_ \ / _ \ 
| |__| (__| | | | (_) |
|_____\___|_| |_|\___/ 

Signal acquired. Echo here, your feline mission controller.
What are we tackling today?
Mission logged:
[T][ ] read book
You now have 1 mission on the manifest.
Mission logged:
[D][ ] return book (by: Dec 02 2019, 6:00 PM)
You now have 2 missions on the manifest.
Mission logged:
[E][ ] project meeting (from: Dec 02 2019, 2:00 PM to: Dec 02 2019, 4:00 PM)
You now have 3 missions on the manifest.
Mission complete. Nicely landed:
[T][X] read book
Mission complete. Nicely landed:
[D][X] return book (by: Dec 02 2019, 6:00 PM)
Mission progress report:

All missions:
Total missions: 3
Completed: 2
Active: 1
Todos completed: 1
Events completed: 0
Deadlines completed: 1

This week:
Completed: 2
Todos completed: 1
Events completed: 0
Deadlines completed: 1
Signal scrambled.
The stats command does not take extra details. Try: stats
Echo going off-air. See you on the next mission!
```

## Test case: pipe characters are rejected

- **Aim:** Verify that task details containing the reserved save-file separator '|' are rejected for every task type and leave the list unchanged.
- **Command:** `(if exist data rmdir /s /q data) & java -cp build/classes/java/main echo.Echo`
- **Inputs:**
```text
todo evil | plan
deadline x /by Sun|day
event y /from a|b /to c
list
bye
```
- **Expected output:**
```text
 _____     _           
| ____|___| |__   ___  
|  _| / __| '_ \ / _ \ 
| |__| (__| | | | (_) |
|_____\___|_| |_|\___/ 

Signal acquired. Echo here, your feline mission controller.
What are we tackling today?
Signal scrambled.
The '|' character interferes with mission records, so it cannot be used.
Signal scrambled.
The '|' character interferes with mission records, so it cannot be used.
Signal scrambled.
The '|' character interferes with mission records, so it cannot be used.
The mission manifest is clear.
Echo going off-air. See you on the next mission!
```

## Test case: date values are validated and formatted

- **Aim:** Verify that date handling rejects unsupported formats, impossible dates, and invalid times for both deadlines and events while leaving the list unchanged.
- **Command:** `(if exist data rmdir /s /q data) & java -cp build/classes/java/main echo.Echo`
- **Inputs:**
```text
deadline return book /by 2/12/2019 1800
deadline return book /by banana
deadline return book /by 2019-12-02
deadline impossible /by 2019-02-30 1200
deadline midnight /by 2019-12-31 2400
event party /from 2019-12-02 1800 /to apple
list
bye
```
- **Expected output:**
```text
 _____     _           
| ____|___| |__   ___  
|  _| / __| '_ \ / _ \ 
| |__| (__| | | | (_) |
|_____\___|_| |_|\___/ 

Signal acquired. Echo here, your feline mission controller.
What are we tackling today?
Signal scrambled.
I need a description and valid deadline. Try: deadline <description> /by <yyyy-MM-dd HHmm>
Example: deadline return book /by 2019-12-02 1800
Signal scrambled.
I need a description and valid deadline. Try: deadline <description> /by <yyyy-MM-dd HHmm>
Example: deadline return book /by 2019-12-02 1800
Signal scrambled.
I need a description and valid deadline. Try: deadline <description> /by <yyyy-MM-dd HHmm>
Example: deadline return book /by 2019-12-02 1800
Signal scrambled.
I need a description and valid deadline. Try: deadline <description> /by <yyyy-MM-dd HHmm>
Example: deadline return book /by 2019-12-02 1800
Signal scrambled.
I need a description and valid deadline. Try: deadline <description> /by <yyyy-MM-dd HHmm>
Example: deadline return book /by 2019-12-02 1800
Signal scrambled.
I need a description and valid times. Try: event <description> /from <yyyy-MM-dd HHmm> /to <yyyy-MM-dd HHmm>
Example: event party /from 2019-12-02 1800 /to 2019-12-02 2100
The mission manifest is clear.
Echo going off-air. See you on the next mission!
```

## Test case: whitespace duplicates and parameters are validated

- **Aim:** Verify that whitespace is normalized while duplicate tasks, extra arguments, repeated parameters, invalid event periods, and signed task numbers are rejected.
- **Command:** `(if exist data rmdir /s /q data) & java -cp build/classes/java/main echo.Echo`
- **Inputs:**
```text
  todo   read   book  
todo read book
list now
deadline project /by 2025-01-15 1800 /by 2025-01-16 1800
event sync /from 2025-01-15 1800 /to 2025-01-15 1800
mark +1
list
bye
```
- **Expected output:**
```text
 _____     _           
| ____|___| |__   ___  
|  _| / __| '_ \ / _ \ 
| |__| (__| | | | (_) |
|_____\___|_| |_|\___/ 

Signal acquired. Echo here, your feline mission controller.
What are we tackling today?
Mission logged:
[T][ ] read book
You now have 1 mission on the manifest.
Signal scrambled.
That mission is already on the manifest.
Signal scrambled.
The list command does not take extra details. Try: list
Signal scrambled.
I need a description and valid deadline. Try: deadline <description> /by <yyyy-MM-dd HHmm>
Example: deadline return book /by 2019-12-02 1800
Signal scrambled.
An event must start before it ends.
Signal scrambled.
That mission number is not on the manifest. Use list to check the available numbers.
Current mission manifest:
1.[T][ ] read book
Echo going off-air. See you on the next mission!
```

## Test case: corrupted save lines report storage errors on load

- **Aim:** Verify that a saved line containing an unparsable date is reported at startup as a storage error instead of crashing, after which the session continues with the remaining valid tasks discarded.
- **Command:** `(if exist data rmdir /s /q data) & mkdir data & (echo T ^|  ^| read book>data\echo.txt) & (echo D ^| Sunday ^| return book ^| 2019-12-02T18:00>>data\echo.txt) & java -cp build/classes/java/main echo.Echo`
- **Inputs:**
```text
list
bye
```
- **Expected output:**
```text
 _____     _           
| ____|___| |__   ___  
|  _| / __| '_ \ / _ \ 
| |__| (__| | | | (_) |
|_____\___|_| |_|\___/ 

Signal acquired. Echo here, your feline mission controller.
What are we tackling today?
Signal scrambled.
Mission archive problem: I could not understand line 2 because its fields are invalid: D | Sunday | return book | 2019-12-02T18:00
The mission manifest is clear.
Echo going off-air. See you on the next mission!
```
