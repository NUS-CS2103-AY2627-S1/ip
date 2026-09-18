# Kachow UI Test Plan

## Test setup

- Runtime: Java 25. See [automated-tests.md](automated-tests.md) for JUnit coverage and
  [gui-test-plan.md](gui-test-plan.md) for manual JavaFX checks.
- GUI window titles and closing on `bye` are covered by the GUI plan. Console exit behavior and
  expected output remain unchanged.
- Personality: expect Kachow's Radiator Springs pit-crew voice in both console and GUI replies.
  The cases below cover greetings, every task operation, empty results, and recoverable errors.
  Task counts describe all stored tasks, including completed tasks; responses use fixed wording.
  The GUI error-presentation flag must not alter console output; the same cases verify this after styling changes.
- Compile the console application before testing with `javac -d _temp/ui-test-console-classes $(find src/main/java
  -name '*.java' ! -path '*/ui/fx/*' ! -name 'FxMain.java' ! -name 'Launcher.java' ! -name 'FxPrinter.java'
  -print)`. This deliberately excludes all JavaFX components from the console UI test classpath.
- Make the test launcher executable with `chmod +x test/run-kachow-isolated.sh`.
- Run every case from the repository root in a fresh JVM. The launcher uses a fresh working directory for each case, so generated task data cannot leak between cases. A case may name a fixture directory, which the launcher copies into its disposable directory.
  The optional `--read-only` argument removes write permission from the copied data file.
  Run permission cases as a regular user whose file permissions are enforced, not as root.
- Compare combined console output exactly after normalizing CRLF line endings to LF.

## Test Case: UI-01 Start and exit cleanly

### Aim

Verify that Kachow welcomes the user as a Radiator Springs pit-crew pal, offers a valid starter command,
and signs off with a rest stop at Flo's when the user enters `bye`.

### Command

```json
["test/run-kachow-isolated.sh"]
```

### Inputs

```text
bye
```

### Expected output

```text
    ____________________________________________________________
     _  __          _                    
    | |/ /__ _  ___| |__   _____      __
    | ' // _` |/ __| '_ \ / _ \ \ /\ / /
    | . \ (_| | (__| | | | (_) \ V  V / 
    |_|\_\__,_|\___|_| |_|\___/ \_/\_/  
    Ka-chow! I'm Kachow, your Radiator Springs pit-crew pal.
    You bring the big dreams; I'll keep the tasks tuned up. Try list, or todo win the Piston Cup.
    ____________________________________________________________
    ____________________________________________________________
    Time to refuel at Flo's. Rest those tires, buddy. Ka-chow!
    ____________________________________________________________
```

## Test Case: UI-15 Edit task details atomically

### Aim

Verify that `edit` accepts one or several fields in any order; applies multiple changes atomically; accepts a
time-only value while retaining that detail's date; preserves completion status and list position; and rejects
invalid task numbers, duplicate or unsupported fields, invalid dates, and reversed event ranges without mutation.

### Command

```json
["test/run-kachow-isolated.sh"]
```

### Inputs

```text
todo read book
deadline submit report /by 2026-08-06 1400
event project meeting /from 2026-08-06 1400 /to 1600
mark 3
edit 1 /description read novel
edit 2 /by 1800 /description submit final report
edit 3 /from 1300 /to 1700 /description planning meeting
edit
edit zero /description name
edit 4 /description name
edit 1 /description should not stick /by 2026-08-07
edit 3 /from 1800 /to 1700 /description should not stick
edit 3 /to tomorrow /description should not stick
edit 3 /to 1800 /to 1900
list
bye
```

### Expected output

```text
    ____________________________________________________________
     _  __          _                    
    | |/ /__ _  ___| |__   _____      __
    | ' // _` |/ __| '_ \ / _ \ \ /\ / /
    | . \ (_| | (__| | | | (_) \ V  V / 
    |_|\_\__,_|\___|_| |_|\___/ \_/\_/  
    Ka-chow! I'm Kachow, your Radiator Springs pit-crew pal.
    You bring the big dreams; I'll keep the tasks tuned up. Try list, or todo win the Piston Cup.
    ____________________________________________________________
    ____________________________________________________________
    Green light, buddy! I've rolled this task onto the starting grid:
      [T][ ] read book
    Your garage now holds 1 task.
    ____________________________________________________________
    ____________________________________________________________
    Green light, buddy! I've rolled this task onto the starting grid:
      [D][ ] submit report (by: Aug 06 2026, 2:00 PM)
    Your garage now holds 2 tasks.
    ____________________________________________________________
    ____________________________________________________________
    Green light, buddy! I've rolled this task onto the starting grid:
      [E][ ] project meeting (from: Aug 06 2026, 2:00 PM to: Aug 06 2026, 4:00 PM)
    Your garage now holds 3 tasks.
    ____________________________________________________________
    ____________________________________________________________
    Ka-chow! That's Piston Cup spirit! This task is marked done:
      [E][X] project meeting (from: Aug 06 2026, 2:00 PM to: Aug 06 2026, 4:00 PM)
    Doc Hudson would be proud. One task at a time, one lap closer.
    ____________________________________________________________
    ____________________________________________________________
    Pit stop complete! Guido's updated this task's details:
      [T][ ] read novel
    ____________________________________________________________
    ____________________________________________________________
    Pit stop complete! Guido's updated this task's details:
      [D][ ] submit final report (by: Aug 06 2026, 6:00 PM)
    ____________________________________________________________
    ____________________________________________________________
    Pit stop complete! Guido's updated this task's details:
      [E][X] planning meeting (from: Aug 06 2026, 1:00 PM to: Aug 06 2026, 5:00 PM)
    ____________________________________________________________
    ____________________________________________________________
    Pit stop, buddy! Let's get you rolling. Tell me which racer to edit. Use: edit TASK_NUMBER FIELD VALUE [FIELD VALUE]...
    ____________________________________________________________
    ____________________________________________________________
    Pit stop, buddy! Let's get you rolling. That racer number isn't a whole positive number. Use: edit TASK_NUMBER
    ____________________________________________________________
    ____________________________________________________________
    Pit stop, buddy! Let's get you rolling. Racer 4 isn't on the grid. Use list to check the task numbers.
    ____________________________________________________________
    ____________________________________________________________
    Pit stop, buddy! Let's get you rolling. This racer does not have a /by detail.
    ____________________________________________________________
    ____________________________________________________________
    Pit stop, buddy! Let's get you rolling. That event must end after it starts. Use a full date when moving it across midnight.
    ____________________________________________________________
    ____________________________________________________________
    Pit stop, buddy! Let's get you rolling. That event end date or time is invalid. Use yyyy-MM-dd, yyyy/M/d, d/M/yyyy, or padded MM/dd/yyyy (US), optionally followed by HHmm, HH:mm, or an AM/PM time.
    ____________________________________________________________
    ____________________________________________________________
    Pit stop, buddy! Let's get you rolling. That edit repeats /to. Specify each detail once.
    ____________________________________________________________
    ____________________________________________________________
    Crew chief's clipboard! Here are all your tasks, from first lap to finish line:
    1.[T][ ] read novel
    2.[D][ ] submit final report (by: Aug 06 2026, 6:00 PM)
    3.[E][X] planning meeting (from: Aug 06 2026, 1:00 PM to: Aug 06 2026, 5:00 PM)
    ____________________________________________________________
    ____________________________________________________________
    Time to refuel at Flo's. Rest those tires, buddy. Ka-chow!
    ____________________________________________________________
```

## Test Case: UI-10 Load persisted tasks on startup

### Aim

Verify that Kachow restores every task type and its completion status from `data/kachow.txt` when a new chatbot process starts.

### Command

```json
["test/run-kachow-isolated.sh", "test/fixtures/persisted-tasks"]
```

### Inputs

```text
list
bye
```

### Expected output

```text
    ____________________________________________________________
     _  __          _                    
    | |/ /__ _  ___| |__   _____      __
    | ' // _` |/ __| '_ \ / _ \ \ /\ / /
    | . \ (_| | (__| | | | (_) \ V  V / 
    |_|\_\__,_|\___|_| |_|\___/ \_/\_/  
    Ka-chow! I'm Kachow, your Radiator Springs pit-crew pal.
    You bring the big dreams; I'll keep the tasks tuned up. Try list, or todo win the Piston Cup.
    ____________________________________________________________
    ____________________________________________________________
    Crew chief's clipboard! Here are all your tasks, from first lap to finish line:
    1.[T][X] read book
    2.[D][ ] return book (by: Jun 06 2019, 2:00 PM)
    3.[E][ ] project meeting (from: Aug 06 2019, 2:00 PM to: Aug 06 2019, 4:00 PM)
    4.[T][X] join sports club
    ____________________________________________________________
    ____________________________________________________________
    Time to refuel at Flo's. Rest those tires, buddy. Ka-chow!
    ____________________________________________________________
```

## Test Case: UI-12 Reject an invalid persisted event range

### Aim

Verify that loading an event whose end precedes its start fails safely instead of creating an invalid task object, and explains how to re-enable saving.

### Command

```json
["test/run-kachow-isolated.sh", "test/fixtures/invalid-event-range"]
```

### Inputs

```text
```

### Expected output

```text
    ____________________________________________________________
     _  __          _                    
    | |/ /__ _  ___| |__   _____      __
    | ' // _` |/ __| '_ \ / _ \ \ /\ / /
    | . \ (_| | (__| | | | (_) \ V  V / 
    |_|\_\__,_|\___|_| |_|\___/ \_/\_/  
    Ka-chow! I'm Kachow, your Radiator Springs pit-crew pal.
    You bring the big dreams; I'll keep the tasks tuned up. Try list, or todo win the Piston Cup.
    ____________________________________________________________
    Pit stop, buddy! Let's get you rolling. Task data on line 1 of ./data/kachow.txt is invalid.
    Saving is disabled. Repair the task file or restore a backup, then restart Kachow.
```

## Test Case: UI-11 Parse and format task dates and times

### Aim

Verify shared parsing across deadline and event parameters, including compact and spaced AM/PM input, repeated whitespace, leap dates, time-only event ends, explicit overnight ranges, and rejection of impossible or reversed values.

### Command

```json
["test/run-kachow-isolated.sh"]
```

### Inputs

```text
deadline return book /by 2/12/2019 1800
deadline submit report /by 2019-10-15
deadline morning call /by 2020/02/29 09:30
deadline midnight /by 2024-01-01 12am
deadline afternoon /by 2024-01-01 6 PM
deadline spaced input /by 2024-01-01    1800
event sprint planning /from 3/12/2019 0900 /to 10:30
event overnight /from 2024/01/02 2300 /to 2024/01/03 0100
deadline invalid date /by 31/02/2019
deadline invalid time /by 2019-10-15 2460
event bad start /from 31/02/2019 0900 /to 1000
event bad end /from 3/12/2019 0900 /to tomorrow
event backwards /from 2024-01-02 1800 /to 1700
list
bye
```

### Expected output

```text
    ____________________________________________________________
     _  __          _                    
    | |/ /__ _  ___| |__   _____      __
    | ' // _` |/ __| '_ \ / _ \ \ /\ / /
    | . \ (_| | (__| | | | (_) \ V  V / 
    |_|\_\__,_|\___|_| |_|\___/ \_/\_/  
    Ka-chow! I'm Kachow, your Radiator Springs pit-crew pal.
    You bring the big dreams; I'll keep the tasks tuned up. Try list, or todo win the Piston Cup.
    ____________________________________________________________
    ____________________________________________________________
    Green light, buddy! I've rolled this task onto the starting grid:
      [D][ ] return book (by: Dec 02 2019, 6:00 PM)
    Your garage now holds 1 task.
    ____________________________________________________________
    ____________________________________________________________
    Green light, buddy! I've rolled this task onto the starting grid:
      [D][ ] submit report (by: Oct 15 2019)
    Your garage now holds 2 tasks.
    ____________________________________________________________
    ____________________________________________________________
    Green light, buddy! I've rolled this task onto the starting grid:
      [D][ ] morning call (by: Feb 29 2020, 9:30 AM)
    Your garage now holds 3 tasks.
    ____________________________________________________________
    ____________________________________________________________
    Green light, buddy! I've rolled this task onto the starting grid:
      [D][ ] midnight (by: Jan 01 2024, 12:00 AM)
    Your garage now holds 4 tasks.
    ____________________________________________________________
    ____________________________________________________________
    Green light, buddy! I've rolled this task onto the starting grid:
      [D][ ] afternoon (by: Jan 01 2024, 6:00 PM)
    Your garage now holds 5 tasks.
    ____________________________________________________________
    ____________________________________________________________
    Green light, buddy! I've rolled this task onto the starting grid:
      [D][ ] spaced input (by: Jan 01 2024, 6:00 PM)
    Your garage now holds 6 tasks.
    ____________________________________________________________
    ____________________________________________________________
    Green light, buddy! I've rolled this task onto the starting grid:
      [E][ ] sprint planning (from: Dec 03 2019, 9:00 AM to: Dec 03 2019, 10:30 AM)
    Your garage now holds 7 tasks.
    ____________________________________________________________
    ____________________________________________________________
    Green light, buddy! I've rolled this task onto the starting grid:
      [E][ ] overnight (from: Jan 02 2024, 11:00 PM to: Jan 03 2024, 1:00 AM)
    Your garage now holds 8 tasks.
    ____________________________________________________________
    ____________________________________________________________
    Pit stop, buddy! Let's get you rolling. That deadline date or time is invalid. Use yyyy-MM-dd, yyyy/M/d, d/M/yyyy, or padded MM/dd/yyyy (US), optionally followed by HHmm, HH:mm, or an AM/PM time.
    ____________________________________________________________
    ____________________________________________________________
    Pit stop, buddy! Let's get you rolling. That deadline date or time is invalid. Use yyyy-MM-dd, yyyy/M/d, d/M/yyyy, or padded MM/dd/yyyy (US), optionally followed by HHmm, HH:mm, or an AM/PM time.
    ____________________________________________________________
    ____________________________________________________________
    Pit stop, buddy! Let's get you rolling. That event start date or time is invalid. Use yyyy-MM-dd, yyyy/M/d, d/M/yyyy, or padded MM/dd/yyyy (US), optionally followed by HHmm, HH:mm, or an AM/PM time.
    ____________________________________________________________
    ____________________________________________________________
    Pit stop, buddy! Let's get you rolling. That event end date or time is invalid. Use yyyy-MM-dd, yyyy/M/d, d/M/yyyy, or padded MM/dd/yyyy (US), optionally followed by HHmm, HH:mm, or an AM/PM time.
    ____________________________________________________________
    ____________________________________________________________
    Pit stop, buddy! Let's get you rolling. That event must end after it starts. Use a full /to date for an overnight event.
    ____________________________________________________________
    ____________________________________________________________
    Crew chief's clipboard! Here are all your tasks, from first lap to finish line:
    1.[D][ ] return book (by: Dec 02 2019, 6:00 PM)
    2.[D][ ] submit report (by: Oct 15 2019)
    3.[D][ ] morning call (by: Feb 29 2020, 9:30 AM)
    4.[D][ ] midnight (by: Jan 01 2024, 12:00 AM)
    5.[D][ ] afternoon (by: Jan 01 2024, 6:00 PM)
    6.[D][ ] spaced input (by: Jan 01 2024, 6:00 PM)
    7.[E][ ] sprint planning (from: Dec 03 2019, 9:00 AM to: Dec 03 2019, 10:30 AM)
    8.[E][ ] overnight (from: Jan 02 2024, 11:00 PM to: Jan 03 2024, 1:00 AM)
    ____________________________________________________________
    ____________________________________________________________
    Time to refuel at Flo's. Rest those tires, buddy. Ka-chow!
    ____________________________________________________________
```

## Test Case: UI-13 Find deadlines and events by date

### Aim

Verify that `on DATE` keeps original task numbers, excludes todos, matches deadlines by due date, matches every date spanned by an event, supports padded US dates, and validates its argument.

### Command

```json
["test/run-kachow-isolated.sh"]
```

### Inputs

```text
todo wash car
deadline return book /by 2/12/2019 1800
deadline submit report /by 12/03/2019 0900
event conference /from 2019/12/03 2300 /to 2019/12/04 0100
on 2019-12-02
on 12/03/2019
on 12/04/2019
on 2019-12-05
on
on 2019-12-03 0900
on tomorrow
bye
```

### Expected output

```text
    ____________________________________________________________
     _  __          _                    
    | |/ /__ _  ___| |__   _____      __
    | ' // _` |/ __| '_ \ / _ \ \ /\ / /
    | . \ (_| | (__| | | | (_) \ V  V / 
    |_|\_\__,_|\___|_| |_|\___/ \_/\_/  
    Ka-chow! I'm Kachow, your Radiator Springs pit-crew pal.
    You bring the big dreams; I'll keep the tasks tuned up. Try list, or todo win the Piston Cup.
    ____________________________________________________________
    ____________________________________________________________
    Green light, buddy! I've rolled this task onto the starting grid:
      [T][ ] wash car
    Your garage now holds 1 task.
    ____________________________________________________________
    ____________________________________________________________
    Green light, buddy! I've rolled this task onto the starting grid:
      [D][ ] return book (by: Dec 02 2019, 6:00 PM)
    Your garage now holds 2 tasks.
    ____________________________________________________________
    ____________________________________________________________
    Green light, buddy! I've rolled this task onto the starting grid:
      [D][ ] submit report (by: Dec 03 2019, 9:00 AM)
    Your garage now holds 3 tasks.
    ____________________________________________________________
    ____________________________________________________________
    Green light, buddy! I've rolled this task onto the starting grid:
      [E][ ] conference (from: Dec 03 2019, 11:00 PM to: Dec 04 2019, 1:00 AM)
    Your garage now holds 4 tasks.
    ____________________________________________________________
    ____________________________________________________________
    Sally's road map! Here are the deadlines and events on Dec 02 2019:
    2.[D][ ] return book (by: Dec 02 2019, 6:00 PM)
    ____________________________________________________________
    ____________________________________________________________
    Sally's road map! Here are the deadlines and events on Dec 03 2019:
    3.[D][ ] submit report (by: Dec 03 2019, 9:00 AM)
    4.[E][ ] conference (from: Dec 03 2019, 11:00 PM to: Dec 04 2019, 1:00 AM)
    ____________________________________________________________
    ____________________________________________________________
    Sally's road map! Here are the deadlines and events on Dec 04 2019:
    4.[E][ ] conference (from: Dec 03 2019, 11:00 PM to: Dec 04 2019, 1:00 AM)
    ____________________________________________________________
    ____________________________________________________________
    Cruise through Radiator Springs! No deadlines or events on Dec 05 2019.
    ____________________________________________________________
    ____________________________________________________________
    Pit stop, buddy! Let's get you rolling. Tell me which race date to check. Use: on DATE
    ____________________________________________________________
    ____________________________________________________________
    Pit stop, buddy! Let's get you rolling. The on command needs a date without a time. Use: on DATE
    ____________________________________________________________
    ____________________________________________________________
    Pit stop, buddy! Let's get you rolling. That date is invalid. Use yyyy-MM-dd, yyyy/M/d, d/M/yyyy, or padded MM/dd/yyyy (US).
    ____________________________________________________________
    ____________________________________________________________
    Time to refuel at Flo's. Rest those tires, buddy. Ka-chow!
    ____________________________________________________________
```

## Test Case: UI-02 Manage todos, deadlines, and events

### Aim

Verify that all three task types can be added and listed, and that marking and unmarking a task updates its displayed status without changing the other tasks.

### Command

```json
["test/run-kachow-isolated.sh"]
```

### Inputs

```text
todo read book
deadline return book /by 2019-12-02
event project meeting /from 2019-08-06 1400 /to 1600
list
mark 2
list
unmark 2
list
bye
```

### Expected output

```text
    ____________________________________________________________
     _  __          _                    
    | |/ /__ _  ___| |__   _____      __
    | ' // _` |/ __| '_ \ / _ \ \ /\ / /
    | . \ (_| | (__| | | | (_) \ V  V / 
    |_|\_\__,_|\___|_| |_|\___/ \_/\_/  
    Ka-chow! I'm Kachow, your Radiator Springs pit-crew pal.
    You bring the big dreams; I'll keep the tasks tuned up. Try list, or todo win the Piston Cup.
    ____________________________________________________________
    ____________________________________________________________
    Green light, buddy! I've rolled this task onto the starting grid:
      [T][ ] read book
    Your garage now holds 1 task.
    ____________________________________________________________
    ____________________________________________________________
    Green light, buddy! I've rolled this task onto the starting grid:
      [D][ ] return book (by: Dec 02 2019)
    Your garage now holds 2 tasks.
    ____________________________________________________________
    ____________________________________________________________
    Green light, buddy! I've rolled this task onto the starting grid:
      [E][ ] project meeting (from: Aug 06 2019, 2:00 PM to: Aug 06 2019, 4:00 PM)
    Your garage now holds 3 tasks.
    ____________________________________________________________
    ____________________________________________________________
    Crew chief's clipboard! Here are all your tasks, from first lap to finish line:
    1.[T][ ] read book
    2.[D][ ] return book (by: Dec 02 2019)
    3.[E][ ] project meeting (from: Aug 06 2019, 2:00 PM to: Aug 06 2019, 4:00 PM)
    ____________________________________________________________
    ____________________________________________________________
    Ka-chow! That's Piston Cup spirit! This task is marked done:
      [D][X] return book (by: Dec 02 2019)
    Doc Hudson would be proud. One task at a time, one lap closer.
    ____________________________________________________________
    ____________________________________________________________
    Crew chief's clipboard! Here are all your tasks, from first lap to finish line:
    1.[T][ ] read book
    2.[D][X] return book (by: Dec 02 2019)
    3.[E][ ] project meeting (from: Aug 06 2019, 2:00 PM to: Aug 06 2019, 4:00 PM)
    ____________________________________________________________
    ____________________________________________________________
    Another practice lap! Even Lightning needs those. This task is marked not done:
      [D][ ] return book (by: Dec 02 2019)
    ____________________________________________________________
    ____________________________________________________________
    Crew chief's clipboard! Here are all your tasks, from first lap to finish line:
    1.[T][ ] read book
    2.[D][ ] return book (by: Dec 02 2019)
    3.[E][ ] project meeting (from: Aug 06 2019, 2:00 PM to: Aug 06 2019, 4:00 PM)
    ____________________________________________________________
    ____________________________________________________________
    Time to refuel at Flo's. Rest those tires, buddy. Ka-chow!
    ____________________________________________________________
```

## Test Case: UI-03 Reject malformed and unknown commands

### Aim

Verify that missing task details and unknown instructions produce specific, themed guidance while leaving the application running for the next command.

### Command

```json
["test/run-kachow-isolated.sh"]
```

### Inputs

```text
todo
deadline return book
event project meeting /from Mon 2pm
dance
bye
```

### Expected output

```text
    ____________________________________________________________
     _  __          _                    
    | |/ /__ _  ___| |__   _____      __
    | ' // _` |/ __| '_ \ / _ \ \ /\ / /
    | . \ (_| | (__| | | | (_) \ V  V / 
    |_|\_\__,_|\___|_| |_|\___/ \_/\_/  
    Ka-chow! I'm Kachow, your Radiator Springs pit-crew pal.
    You bring the big dreams; I'll keep the tasks tuned up. Try list, or todo win the Piston Cup.
    ____________________________________________________________
    ____________________________________________________________
    Pit stop, buddy! Let's get you rolling. This racer needs a name. Use: todo DESCRIPTION
    ____________________________________________________________
    ____________________________________________________________
    Pit stop, buddy! Let's get you rolling. That deadline is missing its /by checkpoint. Use: deadline DESCRIPTION /by DATE_OR_TIME
    ____________________________________________________________
    ____________________________________________________________
    Pit stop, buddy! Let's get you rolling. That event is missing its /to finish line. Use: event DESCRIPTION /from START /to END
    ____________________________________________________________
    ____________________________________________________________
    Pit stop, buddy! Let's get you rolling. That command took a wrong turn. Try todo, deadline, event, list, find, on, mark, unmark, edit, delete, or bye.
    ____________________________________________________________
    ____________________________________________________________
    Time to refuel at Flo's. Rest those tires, buddy. Ka-chow!
    ____________________________________________________________
```

## Test Case: UI-14 Find tasks by description keyword

### Aim

Verify that `find KEYWORD` searches only task descriptions without regard to letter case, keeps original task numbers and statuses across task types, reports no matches, and rejects a missing keyword.

### Command

```json
["test/run-kachow-isolated.sh", "test/fixtures/persisted-tasks"]
```

### Inputs

```text
find BOOK
find PROJECT
find tires
find
bye
```

### Expected output

```text
    ____________________________________________________________
     _  __          _                    
    | |/ /__ _  ___| |__   _____      __
    | ' // _` |/ __| '_ \ / _ \ \ /\ / /
    | . \ (_| | (__| | | | (_) \ V  V / 
    |_|\_\__,_|\___|_| |_|\___/ \_/\_/  
    Ka-chow! I'm Kachow, your Radiator Springs pit-crew pal.
    You bring the big dreams; I'll keep the tasks tuned up. Try list, or todo win the Piston Cup.
    ____________________________________________________________
    ____________________________________________________________
    Mater found 'em! Here are the tasks that match your search:
    1.[T][X] read book
    2.[D][ ] return book (by: Jun 06 2019, 2:00 PM)
    ____________________________________________________________
    ____________________________________________________________
    Mater found 'em! Here are the tasks that match your search:
    3.[E][ ] project meeting (from: Aug 06 2019, 2:00 PM to: Aug 06 2019, 4:00 PM)
    ____________________________________________________________
    ____________________________________________________________
    Mater checked every back road: no tasks matched "tires". Try another keyword, buddy.
    ____________________________________________________________
    ____________________________________________________________
    Pit stop, buddy! Let's get you rolling. Tell me which racer to search for. Use: find KEYWORD
    ____________________________________________________________
    ____________________________________________________________
    Time to refuel at Flo's. Rest those tires, buddy. Ka-chow!
    ____________________________________________________________
```

## Test Case: UI-07 Delete a task and renumber the list

### Aim

Verify that deleting a task shows the removed task and updated task count, and that the remaining tasks are renumbered without otherwise changing their details or completion statuses.

### Command

```json
["test/run-kachow-isolated.sh"]
```

### Inputs

```text
todo read book
deadline return book /by 2026-06-06
event project meeting /from 2026-08-06 1400 /to 1600
todo join sports club
todo borrow book
mark 1
mark 2
mark 4
list
delete 3
list
bye
```

### Expected output

```text
    ____________________________________________________________
     _  __          _                    
    | |/ /__ _  ___| |__   _____      __
    | ' // _` |/ __| '_ \ / _ \ \ /\ / /
    | . \ (_| | (__| | | | (_) \ V  V / 
    |_|\_\__,_|\___|_| |_|\___/ \_/\_/  
    Ka-chow! I'm Kachow, your Radiator Springs pit-crew pal.
    You bring the big dreams; I'll keep the tasks tuned up. Try list, or todo win the Piston Cup.
    ____________________________________________________________
    ____________________________________________________________
    Green light, buddy! I've rolled this task onto the starting grid:
      [T][ ] read book
    Your garage now holds 1 task.
    ____________________________________________________________
    ____________________________________________________________
    Green light, buddy! I've rolled this task onto the starting grid:
      [D][ ] return book (by: Jun 06 2026)
    Your garage now holds 2 tasks.
    ____________________________________________________________
    ____________________________________________________________
    Green light, buddy! I've rolled this task onto the starting grid:
      [E][ ] project meeting (from: Aug 06 2026, 2:00 PM to: Aug 06 2026, 4:00 PM)
    Your garage now holds 3 tasks.
    ____________________________________________________________
    ____________________________________________________________
    Green light, buddy! I've rolled this task onto the starting grid:
      [T][ ] join sports club
    Your garage now holds 4 tasks.
    ____________________________________________________________
    ____________________________________________________________
    Green light, buddy! I've rolled this task onto the starting grid:
      [T][ ] borrow book
    Your garage now holds 5 tasks.
    ____________________________________________________________
    ____________________________________________________________
    Ka-chow! That's Piston Cup spirit! This task is marked done:
      [T][X] read book
    Doc Hudson would be proud. One task at a time, one lap closer.
    ____________________________________________________________
    ____________________________________________________________
    Ka-chow! That's Piston Cup spirit! This task is marked done:
      [D][X] return book (by: Jun 06 2026)
    Doc Hudson would be proud. One task at a time, one lap closer.
    ____________________________________________________________
    ____________________________________________________________
    Ka-chow! That's Piston Cup spirit! This task is marked done:
      [T][X] join sports club
    Doc Hudson would be proud. One task at a time, one lap closer.
    ____________________________________________________________
    ____________________________________________________________
    Crew chief's clipboard! Here are all your tasks, from first lap to finish line:
    1.[T][X] read book
    2.[D][X] return book (by: Jun 06 2026)
    3.[E][ ] project meeting (from: Aug 06 2026, 2:00 PM to: Aug 06 2026, 4:00 PM)
    4.[T][X] join sports club
    5.[T][ ] borrow book
    ____________________________________________________________
    ____________________________________________________________
    Mater's towing this one off the roster. Task deleted:
      [E][ ] project meeting (from: Aug 06 2026, 2:00 PM to: Aug 06 2026, 4:00 PM)
    Your garage now holds 4 tasks.
    ____________________________________________________________
    ____________________________________________________________
    Crew chief's clipboard! Here are all your tasks, from first lap to finish line:
    1.[T][X] read book
    2.[D][X] return book (by: Jun 06 2026)
    3.[T][X] join sports club
    4.[T][ ] borrow book
    ____________________________________________________________
    ____________________________________________________________
    Time to refuel at Flo's. Rest those tires, buddy. Ka-chow!
    ____________________________________________________________
```

## Test Case: UI-04 Validate task numbers

### Aim

Verify that nonnumeric and out-of-range task numbers are rejected, while valid mark and unmark commands still update the selected task.

### Command

```json
["test/run-kachow-isolated.sh"]
```

### Inputs

```text
mark 1
todo pit stop
mark two
mark 2
mark 1
unmark 0
unmark 1
bye
```

### Expected output

```text
    ____________________________________________________________
     _  __          _                    
    | |/ /__ _  ___| |__   _____      __
    | ' // _` |/ __| '_ \ / _ \ \ /\ / /
    | . \ (_| | (__| | | | (_) \ V  V / 
    |_|\_\__,_|\___|_| |_|\___/ \_/\_/  
    Ka-chow! I'm Kachow, your Radiator Springs pit-crew pal.
    You bring the big dreams; I'll keep the tasks tuned up. Try list, or todo win the Piston Cup.
    ____________________________________________________________
    ____________________________________________________________
    Pit stop, buddy! Let's get you rolling. Racer 1 isn't on the grid. Use list to check the task numbers.
    ____________________________________________________________
    ____________________________________________________________
    Green light, buddy! I've rolled this task onto the starting grid:
      [T][ ] pit stop
    Your garage now holds 1 task.
    ____________________________________________________________
    ____________________________________________________________
    Pit stop, buddy! Let's get you rolling. That racer number isn't a whole positive number. Use: mark TASK_NUMBER
    ____________________________________________________________
    ____________________________________________________________
    Pit stop, buddy! Let's get you rolling. Racer 2 isn't on the grid. Use list to check the task numbers.
    ____________________________________________________________
    ____________________________________________________________
    Ka-chow! That's Piston Cup spirit! This task is marked done:
      [T][X] pit stop
    Doc Hudson would be proud. One task at a time, one lap closer.
    ____________________________________________________________
    ____________________________________________________________
    Pit stop, buddy! Let's get you rolling. That racer number isn't a whole positive number. Use: unmark TASK_NUMBER
    ____________________________________________________________
    ____________________________________________________________
    Another practice lap! Even Lightning needs those. This task is marked not done:
      [T][ ] pit stop
    ____________________________________________________________
    ____________________________________________________________
    Time to refuel at Flo's. Rest those tires, buddy. Ka-chow!
    ____________________________________________________________
```

## Test Case: UI-05 Reject empty commands and unexpected arguments

### Aim

Verify that an empty command and extra arguments for argument-free commands receive specific correction guidance, and that listing an empty task collection remains safe.

### Command

```json
["test/run-kachow-isolated.sh"]
```

### Inputs

```text

list
list turbo
bye now
bye
```

### Expected output

```text
    ____________________________________________________________
     _  __          _                    
    | |/ /__ _  ___| |__   _____      __
    | ' // _` |/ __| '_ \ / _ \ \ /\ / /
    | . \ (_| | (__| | | | (_) \ V  V / 
    |_|\_\__,_|\___|_| |_|\___/ \_/\_/  
    Ka-chow! I'm Kachow, your Radiator Springs pit-crew pal.
    You bring the big dreams; I'll keep the tasks tuned up. Try list, or todo win the Piston Cup.
    ____________________________________________________________
    ____________________________________________________________
    Pit stop, buddy! Let's get you rolling. That command stalled on the starting line. Enter a command to keep racing.
    ____________________________________________________________
    ____________________________________________________________
    Quiet as Radiator Springs before sunrise! Add a task with todo, deadline, or event.
    ____________________________________________________________
    ____________________________________________________________
    Pit stop, buddy! Let's get you rolling. The list command has extra cargo. Use: list
    ____________________________________________________________
    ____________________________________________________________
    Pit stop, buddy! Let's get you rolling. The bye command has extra cargo. Use: bye
    ____________________________________________________________
    ____________________________________________________________
    Time to refuel at Flo's. Rest those tires, buddy. Ka-chow!
    ____________________________________________________________
```

## Test Case: UI-06 Explain every malformed command component

### Aim

Verify that each missing, duplicated, misplaced, or invalid command component receives guidance that identifies the exact correction needed.

### Command

```json
["test/run-kachow-isolated.sh"]
```

### Inputs

```text
deadline
deadline /by Sunday
deadline service /by
deadline service /by Friday /by Monday
event
event /from Mon /to Tue
event meeting /to Tue /from Mon
event meeting /from Mon
event meeting /from /to Tue
event meeting /from Mon /to
event meeting /from Mon /to Tue /to Wed
mark
unmark 999999999999999999999
bye
```

### Expected output

```text
    ____________________________________________________________
     _  __          _                    
    | |/ /__ _  ___| |__   _____      __
    | ' // _` |/ __| '_ \ / _ \ \ /\ / /
    | . \ (_| | (__| | | | (_) \ V  V / 
    |_|\_\__,_|\___|_| |_|\___/ \_/\_/  
    Ka-chow! I'm Kachow, your Radiator Springs pit-crew pal.
    You bring the big dreams; I'll keep the tasks tuned up. Try list, or todo win the Piston Cup.
    ____________________________________________________________
    ____________________________________________________________
    Pit stop, buddy! Let's get you rolling. This deadline racer needs a task description. Use: deadline DESCRIPTION /by DATE_OR_TIME
    ____________________________________________________________
    ____________________________________________________________
    Pit stop, buddy! Let's get you rolling. This deadline racer needs a task description. Use: deadline DESCRIPTION /by DATE_OR_TIME
    ____________________________________________________________
    ____________________________________________________________
    Pit stop, buddy! Let's get you rolling. That deadline needs a date or time after /by. Use: deadline DESCRIPTION /by DATE_OR_TIME
    ____________________________________________________________
    ____________________________________________________________
    Pit stop, buddy! Let's get you rolling. That deadline has too many /by checkpoints. Use exactly one: deadline DESCRIPTION /by DATE_OR_TIME
    ____________________________________________________________
    ____________________________________________________________
    Pit stop, buddy! Let's get you rolling. This event racer needs a description. Use: event DESCRIPTION /from START /to END
    ____________________________________________________________
    ____________________________________________________________
    Pit stop, buddy! Let's get you rolling. This event racer needs a description. Use: event DESCRIPTION /from START /to END
    ____________________________________________________________
    ____________________________________________________________
    Pit stop, buddy! Let's get you rolling. That event's /from must come before /to. Use: event DESCRIPTION /from START /to END
    ____________________________________________________________
    ____________________________________________________________
    Pit stop, buddy! Let's get you rolling. That event is missing its /to finish line. Use: event DESCRIPTION /from START /to END
    ____________________________________________________________
    ____________________________________________________________
    Pit stop, buddy! Let's get you rolling. That event needs a start after /from. Use: event DESCRIPTION /from START /to END
    ____________________________________________________________
    ____________________________________________________________
    Pit stop, buddy! Let's get you rolling. That event needs an end after /to. Use: event DESCRIPTION /from START /to END
    ____________________________________________________________
    ____________________________________________________________
    Pit stop, buddy! Let's get you rolling. That event has extra route markers. Use one /from and one /to: event DESCRIPTION /from START /to END
    ____________________________________________________________
    ____________________________________________________________
    Pit stop, buddy! Let's get you rolling. Tell me which racer to mark. Use: mark TASK_NUMBER
    ____________________________________________________________
    ____________________________________________________________
    Pit stop, buddy! Let's get you rolling. That racer number isn't a whole positive number. Use: unmark TASK_NUMBER
    ____________________________________________________________
    ____________________________________________________________
    Time to refuel at Flo's. Rest those tires, buddy. Ka-chow!
    ____________________________________________________________
```

## Test Case: UI-08 Preserve state after invalid delete commands

### Aim

Verify that missing, nonnumeric, nonpositive, extra, and out-of-range delete arguments are rejected without changing task data or completion state, while valid commands still work between the rejected commands.

### Command

```json
["test/run-kachow-isolated.sh"]
```

### Inputs

```text
delete 1
todo tire change
delete
list
delete zero
mark 1
delete -1
list
delete 1 turbo
unmark 1
delete 2
list
delete 1
list
delete 1
bye
```

### Expected output

```text
    ____________________________________________________________
     _  __          _                    
    | |/ /__ _  ___| |__   _____      __
    | ' // _` |/ __| '_ \ / _ \ \ /\ / /
    | . \ (_| | (__| | | | (_) \ V  V / 
    |_|\_\__,_|\___|_| |_|\___/ \_/\_/  
    Ka-chow! I'm Kachow, your Radiator Springs pit-crew pal.
    You bring the big dreams; I'll keep the tasks tuned up. Try list, or todo win the Piston Cup.
    ____________________________________________________________
    ____________________________________________________________
    Pit stop, buddy! Let's get you rolling. Racer 1 isn't on the grid. Use list to check the task numbers.
    ____________________________________________________________
    ____________________________________________________________
    Green light, buddy! I've rolled this task onto the starting grid:
      [T][ ] tire change
    Your garage now holds 1 task.
    ____________________________________________________________
    ____________________________________________________________
    Pit stop, buddy! Let's get you rolling. Tell me which racer to delete. Use: delete TASK_NUMBER
    ____________________________________________________________
    ____________________________________________________________
    Crew chief's clipboard! Here are all your tasks, from first lap to finish line:
    1.[T][ ] tire change
    ____________________________________________________________
    ____________________________________________________________
    Pit stop, buddy! Let's get you rolling. That racer number isn't a whole positive number. Use: delete TASK_NUMBER
    ____________________________________________________________
    ____________________________________________________________
    Ka-chow! That's Piston Cup spirit! This task is marked done:
      [T][X] tire change
    Doc Hudson would be proud. One task at a time, one lap closer.
    ____________________________________________________________
    ____________________________________________________________
    Pit stop, buddy! Let's get you rolling. That racer number isn't a whole positive number. Use: delete TASK_NUMBER
    ____________________________________________________________
    ____________________________________________________________
    Crew chief's clipboard! Here are all your tasks, from first lap to finish line:
    1.[T][X] tire change
    ____________________________________________________________
    ____________________________________________________________
    Pit stop, buddy! Let's get you rolling. That racer number isn't a whole positive number. Use: delete TASK_NUMBER
    ____________________________________________________________
    ____________________________________________________________
    Another practice lap! Even Lightning needs those. This task is marked not done:
      [T][ ] tire change
    ____________________________________________________________
    ____________________________________________________________
    Pit stop, buddy! Let's get you rolling. Racer 2 isn't on the grid. Use list to check the task numbers.
    ____________________________________________________________
    ____________________________________________________________
    Crew chief's clipboard! Here are all your tasks, from first lap to finish line:
    1.[T][ ] tire change
    ____________________________________________________________
    ____________________________________________________________
    Mater's towing this one off the roster. Task deleted:
      [T][ ] tire change
    Your garage now holds 0 tasks.
    ____________________________________________________________
    ____________________________________________________________
    Quiet as Radiator Springs before sunrise! Add a task with todo, deadline, or event.
    ____________________________________________________________
    ____________________________________________________________
    Pit stop, buddy! Let's get you rolling. Racer 1 isn't on the grid. Use list to check the task numbers.
    ____________________________________________________________
    ____________________________________________________________
    Time to refuel at Flo's. Rest those tires, buddy. Ka-chow!
    ____________________________________________________________
```

## Test Case: UI-09 Delete boundary tasks and continue using the list

### Aim

Verify that deleting the first, last, and only tasks preserves task details and completion state through renumbering, invalid deletion does not mutate the list, and new tasks can be added after the list becomes empty.

### Command

```json
["test/run-kachow-isolated.sh"]
```

### Inputs

```text
todo pole position
deadline refuel /by 2026-08-20
event sponsor event /from 2026-08-21 1500 /to 1600
todo victory lap
mark 2
delete 1
delete 4
list
unmark 1
delete 3
list
delete 1
delete 1
list
todo new race
list
bye
```

### Expected output

```text
    ____________________________________________________________
     _  __          _                    
    | |/ /__ _  ___| |__   _____      __
    | ' // _` |/ __| '_ \ / _ \ \ /\ / /
    | . \ (_| | (__| | | | (_) \ V  V / 
    |_|\_\__,_|\___|_| |_|\___/ \_/\_/  
    Ka-chow! I'm Kachow, your Radiator Springs pit-crew pal.
    You bring the big dreams; I'll keep the tasks tuned up. Try list, or todo win the Piston Cup.
    ____________________________________________________________
    ____________________________________________________________
    Green light, buddy! I've rolled this task onto the starting grid:
      [T][ ] pole position
    Your garage now holds 1 task.
    ____________________________________________________________
    ____________________________________________________________
    Green light, buddy! I've rolled this task onto the starting grid:
      [D][ ] refuel (by: Aug 20 2026)
    Your garage now holds 2 tasks.
    ____________________________________________________________
    ____________________________________________________________
    Green light, buddy! I've rolled this task onto the starting grid:
      [E][ ] sponsor event (from: Aug 21 2026, 3:00 PM to: Aug 21 2026, 4:00 PM)
    Your garage now holds 3 tasks.
    ____________________________________________________________
    ____________________________________________________________
    Green light, buddy! I've rolled this task onto the starting grid:
      [T][ ] victory lap
    Your garage now holds 4 tasks.
    ____________________________________________________________
    ____________________________________________________________
    Ka-chow! That's Piston Cup spirit! This task is marked done:
      [D][X] refuel (by: Aug 20 2026)
    Doc Hudson would be proud. One task at a time, one lap closer.
    ____________________________________________________________
    ____________________________________________________________
    Mater's towing this one off the roster. Task deleted:
      [T][ ] pole position
    Your garage now holds 3 tasks.
    ____________________________________________________________
    ____________________________________________________________
    Pit stop, buddy! Let's get you rolling. Racer 4 isn't on the grid. Use list to check the task numbers.
    ____________________________________________________________
    ____________________________________________________________
    Crew chief's clipboard! Here are all your tasks, from first lap to finish line:
    1.[D][X] refuel (by: Aug 20 2026)
    2.[E][ ] sponsor event (from: Aug 21 2026, 3:00 PM to: Aug 21 2026, 4:00 PM)
    3.[T][ ] victory lap
    ____________________________________________________________
    ____________________________________________________________
    Another practice lap! Even Lightning needs those. This task is marked not done:
      [D][ ] refuel (by: Aug 20 2026)
    ____________________________________________________________
    ____________________________________________________________
    Mater's towing this one off the roster. Task deleted:
      [T][ ] victory lap
    Your garage now holds 2 tasks.
    ____________________________________________________________
    ____________________________________________________________
    Crew chief's clipboard! Here are all your tasks, from first lap to finish line:
    1.[D][ ] refuel (by: Aug 20 2026)
    2.[E][ ] sponsor event (from: Aug 21 2026, 3:00 PM to: Aug 21 2026, 4:00 PM)
    ____________________________________________________________
    ____________________________________________________________
    Mater's towing this one off the roster. Task deleted:
      [D][ ] refuel (by: Aug 20 2026)
    Your garage now holds 1 task.
    ____________________________________________________________
    ____________________________________________________________
    Mater's towing this one off the roster. Task deleted:
      [E][ ] sponsor event (from: Aug 21 2026, 3:00 PM to: Aug 21 2026, 4:00 PM)
    Your garage now holds 0 tasks.
    ____________________________________________________________
    ____________________________________________________________
    Quiet as Radiator Springs before sunrise! Add a task with todo, deadline, or event.
    ____________________________________________________________
    ____________________________________________________________
    Green light, buddy! I've rolled this task onto the starting grid:
      [T][ ] new race
    Your garage now holds 1 task.
    ____________________________________________________________
    ____________________________________________________________
    Crew chief's clipboard! Here are all your tasks, from first lap to finish line:
    1.[T][ ] new race
    ____________________________________________________________
    ____________________________________________________________
    Time to refuel at Flo's. Rest those tires, buddy. Ka-chow!
    ____________________________________________________________
```

## Test Case: UI-16 Reject unsafe and duplicate task details

### Aim

Verify whitespace normalization, duplicate add/edit rejection regardless of completion and case,
reserved characters, unsupported fields, signed and overflowing numbers, impossible dates, and equal
event boundaries. Rejected commands must preserve descriptions, completion states, and list positions.

### Command

```json
["test/run-kachow-isolated.sh"]
```

### Inputs

```text
  todo   read	book  
mark 1
todo READ BOOK
todo write book
edit 2 /description read book
todo read | book
todo read /by tomorrow
deadline read /oops x /by 2026-09-10
mark +1
delete 999999999999999999
event zero /from 2026-09-10 /to 2026-09-10
event zero /from 2026-09-10 1000 /to 1000
event meeting /from 2026-09-10 1000 /to 1100
event MEETING /from 2026-09-10 10:00 /to 11:00
edit 3 /to 1000 /description should not stick
edit 2 /description unsafe|name
deadline impossible /by 2026-02-30
list
bye
```

### Expected output

```text
    ____________________________________________________________
     _  __          _                    
    | |/ /__ _  ___| |__   _____      __
    | ' // _` |/ __| '_ \ / _ \ \ /\ / /
    | . \ (_| | (__| | | | (_) \ V  V / 
    |_|\_\__,_|\___|_| |_|\___/ \_/\_/  
    Ka-chow! I'm Kachow, your Radiator Springs pit-crew pal.
    You bring the big dreams; I'll keep the tasks tuned up. Try list, or todo win the Piston Cup.
    ____________________________________________________________
    ____________________________________________________________
    Green light, buddy! I've rolled this task onto the starting grid:
      [T][ ] read book
    Your garage now holds 1 task.
    ____________________________________________________________
    ____________________________________________________________
    Ka-chow! That's Piston Cup spirit! This task is marked done:
      [T][X] read book
    Doc Hudson would be proud. One task at a time, one lap closer.
    ____________________________________________________________
    ____________________________________________________________
    Pit stop, buddy! Let's get you rolling. That racer is already on the grid as task 1. Use list to check its details.
    ____________________________________________________________
    ____________________________________________________________
    Green light, buddy! I've rolled this task onto the starting grid:
      [T][ ] write book
    Your garage now holds 2 tasks.
    ____________________________________________________________
    ____________________________________________________________
    Pit stop, buddy! Let's get you rolling. That racer is already on the grid as task 1. Use list to check its details.
    ____________________________________________________________
    ____________________________________________________________
    Pit stop, buddy! Let's get you rolling. Descriptions cannot contain |, line breaks, or control characters.
    ____________________________________________________________
    ____________________________________________________________
    Pit stop, buddy! Let's get you rolling. Descriptions cannot contain slash-prefixed fields. Check the command's parameters.
    ____________________________________________________________
    ____________________________________________________________
    Pit stop, buddy! Let's get you rolling. Descriptions cannot contain slash-prefixed fields. Check the command's parameters.
    ____________________________________________________________
    ____________________________________________________________
    Pit stop, buddy! Let's get you rolling. That racer number isn't a whole positive number. Use: mark TASK_NUMBER
    ____________________________________________________________
    ____________________________________________________________
    Pit stop, buddy! Let's get you rolling. That racer number isn't a whole positive number. Use: delete TASK_NUMBER
    ____________________________________________________________
    ____________________________________________________________
    Pit stop, buddy! Let's get you rolling. That event must end after it starts. Use a full /to date for an overnight event.
    ____________________________________________________________
    ____________________________________________________________
    Pit stop, buddy! Let's get you rolling. That event must end after it starts. Use a full /to date for an overnight event.
    ____________________________________________________________
    ____________________________________________________________
    Green light, buddy! I've rolled this task onto the starting grid:
      [E][ ] meeting (from: Sep 10 2026, 10:00 AM to: Sep 10 2026, 11:00 AM)
    Your garage now holds 3 tasks.
    ____________________________________________________________
    ____________________________________________________________
    Pit stop, buddy! Let's get you rolling. That racer is already on the grid as task 3. Use list to check its details.
    ____________________________________________________________
    ____________________________________________________________
    Pit stop, buddy! Let's get you rolling. That event must end after it starts. Use a full date when moving it across midnight.
    ____________________________________________________________
    ____________________________________________________________
    Pit stop, buddy! Let's get you rolling. Descriptions cannot contain |, line breaks, or control characters.
    ____________________________________________________________
    ____________________________________________________________
    Pit stop, buddy! Let's get you rolling. That deadline date or time is invalid. Use yyyy-MM-dd, yyyy/M/d, d/M/yyyy, or padded MM/dd/yyyy (US), optionally followed by HHmm, HH:mm, or an AM/PM time.
    ____________________________________________________________
    ____________________________________________________________
    Crew chief's clipboard! Here are all your tasks, from first lap to finish line:
    1.[T][X] read book
    2.[T][ ] write book
    3.[E][ ] meeting (from: Sep 10 2026, 10:00 AM to: Sep 10 2026, 11:00 AM)
    ____________________________________________________________
    ____________________________________________________________
    Time to refuel at Flo's. Rest those tires, buddy. Ka-chow!
    ____________________________________________________________
```

## Test Case: UI-17 Protect invalid stored data from subsequent commands

### Aim

Verify that a failed load disables saving and a rejected add leaves the in-memory list empty.
The original fixture is protected by copying it; storage regression tests also verify byte-for-byte preservation.

### Command

```json
["test/run-kachow-isolated.sh", "test/fixtures/invalid-event-range"]
```

### Inputs

```text
todo replacement
list
bye
```

### Expected output

```text
    ____________________________________________________________
     _  __          _                    
    | |/ /__ _  ___| |__   _____      __
    | ' // _` |/ __| '_ \ / _ \ \ /\ / /
    | . \ (_| | (__| | | | (_) \ V  V / 
    |_|\_\__,_|\___|_| |_|\___/ \_/\_/  
    Ka-chow! I'm Kachow, your Radiator Springs pit-crew pal.
    You bring the big dreams; I'll keep the tasks tuned up. Try list, or todo win the Piston Cup.
    ____________________________________________________________
    Pit stop, buddy! Let's get you rolling. Task data on line 1 of ./data/kachow.txt is invalid.
    Saving is disabled. Repair the task file or restore a backup, then restart Kachow.
    ____________________________________________________________
    Pit stop, buddy! Let's get you rolling. Saving is disabled because task data could not be loaded. Repair the file or restore a backup, then restart Kachow. Your file has not been changed.
    ____________________________________________________________
    ____________________________________________________________
    Quiet as Radiator Springs before sunrise! Add a task with todo, deadline, or event.
    ____________________________________________________________
    ____________________________________________________________
    Time to refuel at Flo's. Rest those tires, buddy. Ka-chow!
    ____________________________________________________________
```

## Test Case: UI-18 Preserve all task state when saving is denied

### Aim

Verify that a readable but non-writable data file rejects add, mark, unmark, edit, and delete,
while list and bye remain usable. Each failed save must preserve task order, details, and status.
The fixture is copied and only the disposable copy has its permissions changed.

### Command

```json
["test/run-kachow-isolated.sh", "test/fixtures/persisted-tasks", "--read-only"]
```

### Inputs

```text
todo replacement
mark 2
unmark 1
edit 1 /description replacement
delete 1
list
bye
```

### Expected output

```text
    ____________________________________________________________
     _  __          _                    
    | |/ /__ _  ___| |__   _____      __
    | ' // _` |/ __| '_ \ / _ \ \ /\ / /
    | . \ (_| | (__| | | | (_) \ V  V / 
    |_|\_\__,_|\___|_| |_|\___/ \_/\_/  
    Ka-chow! I'm Kachow, your Radiator Springs pit-crew pal.
    You bring the big dreams; I'll keep the tasks tuned up. Try list, or todo win the Piston Cup.
    ____________________________________________________________
    ____________________________________________________________
    Pit stop, buddy! Let's get you rolling. I couldn't save task data to ./data/kachow.txt. No changes were applied. Check file permissions, available disk space, and support for atomic file replacement, then try again.
    ____________________________________________________________
    ____________________________________________________________
    Pit stop, buddy! Let's get you rolling. I couldn't save task data to ./data/kachow.txt. No changes were applied. Check file permissions, available disk space, and support for atomic file replacement, then try again.
    ____________________________________________________________
    ____________________________________________________________
    Pit stop, buddy! Let's get you rolling. I couldn't save task data to ./data/kachow.txt. No changes were applied. Check file permissions, available disk space, and support for atomic file replacement, then try again.
    ____________________________________________________________
    ____________________________________________________________
    Pit stop, buddy! Let's get you rolling. I couldn't save task data to ./data/kachow.txt. No changes were applied. Check file permissions, available disk space, and support for atomic file replacement, then try again.
    ____________________________________________________________
    ____________________________________________________________
    Pit stop, buddy! Let's get you rolling. I couldn't save task data to ./data/kachow.txt. No changes were applied. Check file permissions, available disk space, and support for atomic file replacement, then try again.
    ____________________________________________________________
    ____________________________________________________________
    Crew chief's clipboard! Here are all your tasks, from first lap to finish line:
    1.[T][X] read book
    2.[D][ ] return book (by: Jun 06 2019, 2:00 PM)
    3.[E][ ] project meeting (from: Aug 06 2019, 2:00 PM to: Aug 06 2019, 4:00 PM)
    4.[T][X] join sports club
    ____________________________________________________________
    ____________________________________________________________
    Time to refuel at Flo's. Rest those tires, buddy. Ka-chow!
    ____________________________________________________________
```

## Test Case: UI-19 Exit cleanly when standard input is already closed

### Aim

Verify that an empty input stream prints the complete greeting and exits successfully,
without a spurious command error or farewell. JUnit also checks that no data file is created.

### Command

```json
["test/run-kachow-isolated.sh"]
```

### Inputs

```text
```

### Expected output

```text
    ____________________________________________________________
     _  __          _                    
    | |/ /__ _  ___| |__   _____      __
    | ' // _` |/ __| '_ \ / _ \ \ /\ / /
    | . \ (_| | (__| | | | (_) \ V  V / 
    |_|\_\__,_|\___|_| |_|\___/ \_/\_/  
    Ka-chow! I'm Kachow, your Radiator Springs pit-crew pal.
    You bring the big dreams; I'll keep the tasks tuned up. Try list, or todo win the Piston Cup.
    ____________________________________________________________
```
