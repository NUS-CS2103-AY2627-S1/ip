# Level-6 UI test plan

These tests cover creating the three supported task types, event-order validation, listing tasks,
marking and unmarking a task, detecting repeated status changes, and saving and loading task-list changes
through `data/dobby.txt`.
They also cover in-app command guidance and discovery after an unknown command.

## Create, list, mark, and unmark dated tasks

**Aim:** Confirm that todo, deadline, and event commands create correctly typed tasks; dates are reformatted for
display; `mark` and `unmark` change a task's status; and repeated status changes are reported without changing it.
Each successful change also saves the task list to `data/dobby.txt` without changing the console output.

**Command:**
```text
del /q data\dobby.txt data\duke.txt 2>nul & javac -d build\ui-test src\main\java\dobby\Dobby.java src\main\java\dobby\command\*.java src\main\java\dobby\exception\*.java src\main\java\dobby\logic\*.java src\main\java\dobby\parser\*.java src\main\java\dobby\storage\*.java src\main\java\dobby\task\*.java src\main\java\dobby\ui\cli\*.java src\main\java\dobby\util\*.java && java -cp build\ui-test dobby.Dobby
```

**Input:**
```text
todo read book
deadline return book /by 2/12/2019 1800
event project meeting /from 2019-12-03 0900 /to 2019-12-03 1100
list
find book
mark 2
mark 2
unmark 2
unmark 2
list
bye
```

**Expected output:**
```text
____________________________________________________________
       *       .       *       .       *       
   .      ____        _     _              .   
     *   |  _ \  ___ | |__ | |__  _   _     * 
   .     | | | |/ _ \| '_ \| '_ \| | | |   . 
     *   | |_| | (_) | |_) | |_) | |_| |     * 
   .     |____/ \___/|_.__/|_.__/ \__, |   . 
                                  |___/        
       *       .       *       .       *       

> Dobby says hi!
> Dobby is ready to take orders.
____________________________________________________________
Tell Dobby: ____________________________________________________________
> Dobby noted a new Todo: read book
____________________________________________________________
Tell Dobby: ____________________________________________________________
> Dobby noted a new Deadline: return book by Dec 02 2019, 18:00
____________________________________________________________
Tell Dobby: ____________________________________________________________
> Dobby noted a new Event: project meeting from Dec 03 2019, 09:00 to Dec 03 2019, 11:00
____________________________________________________________
Tell Dobby: ____________________________________________________________
> Dobby show 3 tasks:
1. [T][ ] read book
2. [D][ ] return book (by: Dec 02 2019, 18:00)
3. [E][ ] project meeting (from: Dec 03 2019, 09:00 to: Dec 03 2019, 11:00)

____________________________________________________________
Tell Dobby: ____________________________________________________________
> Here are the matching tasks in your list:
1. [T][ ] read book
2. [D][ ] return book (by: Dec 02 2019, 18:00)

____________________________________________________________
Tell Dobby: ____________________________________________________________
> Dobby will mark this as done!
   [D][X] return book (by: Dec 02 2019, 18:00)
____________________________________________________________
Tell Dobby: ____________________________________________________________
> Dobby sees that the task is already marked.
____________________________________________________________
Tell Dobby: ____________________________________________________________
> Dobby will mark this as not done!
   [D][ ] return book (by: Dec 02 2019, 18:00)
____________________________________________________________
Tell Dobby: ____________________________________________________________
> Dobby sees that the task is already unmarked.
____________________________________________________________
Tell Dobby: ____________________________________________________________
> Dobby show 3 tasks:
1. [T][ ] read book
2. [D][ ] return book (by: Dec 02 2019, 18:00)
3. [E][ ] project meeting (from: Dec 03 2019, 09:00 to: Dec 03 2019, 11:00)

____________________________________________________________
Tell Dobby: ____________________________________________________________
> Dobby says goodbye to master!
____________________________________________________________
```

## Reject an event whose end precedes its start

**Aim:** Confirm that a reversed event is rejected without adding a task.

**Command:**
```text
del /q data\dobby.txt data\duke.txt 2>nul & javac -d build\ui-test src\main\java\dobby\Dobby.java src\main\java\dobby\command\*.java src\main\java\dobby\exception\*.java src\main\java\dobby\logic\*.java src\main\java\dobby\parser\*.java src\main\java\dobby\storage\*.java src\main\java\dobby\task\*.java src\main\java\dobby\ui\cli\*.java src\main\java\dobby\util\*.java && java -cp build\ui-test dobby.Dobby
```

**Input:**
```text
event reversed /from 2026-09-22 1600 /to 2026-09-22 1400
list
bye
```

**Expected output:**
```text
____________________________________________________________
       *       .       *       .       *       
   .      ____        _     _              .   
     *   |  _ \  ___ | |__ | |__  _   _     * 
   .     | | | |/ _ \| '_ \| '_ \| | | |   . 
     *   | |_| | (_) | |_) | |_) | |_| |     * 
   .     |____/ \___/|_.__/|_.__/ \__, |   . 
                                  |___/        
       *       .       *       .       *       

> Dobby says hi!
> Dobby is ready to take orders.
____________________________________________________________
Tell Dobby: ____________________________________________________________
> Dobby needs the event end to be at or after its start.
____________________________________________________________
Tell Dobby: ____________________________________________________________
> Dobby show 0 tasks:

____________________________________________________________
Tell Dobby: ____________________________________________________________
> Dobby says goodbye to master!
____________________________________________________________
```

## Show help and suggest it after an unknown command

**Aim:** Confirm that `help` lists every supported command and date format without changing task state, and that an unknown command directs the user to help.

**Command:**
```text
del /q data\dobby.txt data\duke.txt 2>nul & javac -d build\ui-test src\main\java\dobby\Dobby.java src\main\java\dobby\command\*.java src\main\java\dobby\exception\*.java src\main\java\dobby\logic\*.java src\main\java\dobby\parser\*.java src\main\java\dobby\storage\*.java src\main\java\dobby\task\*.java src\main\java\dobby\ui\cli\*.java src\main\java\dobby\util\*.java && java -cp build\ui-test dobby.Dobby
```

**Input:**
```text
todo read book
help
remind me
list
bye
```

**Expected output:**
```text
____________________________________________________________
       *       .       *       .       *       
   .      ____        _     _              .   
     *   |  _ \  ___ | |__ | |__  _   _     * 
   .     | | | |/ _ \| '_ \| '_ \| | | |   . 
     *   | |_| | (_) | |_) | |_) | |_| |     * 
   .     |____/ \___/|_.__/|_.__/ \__, |   . 
                                  |___/        
       *       .       *       .       *       

> Dobby says hi!
> Dobby is ready to take orders.
____________________________________________________________
Tell Dobby: ____________________________________________________________
> Dobby noted a new Todo: read book
____________________________________________________________
Tell Dobby: ____________________________________________________________
> Dobby can help with these commands:
Create tasks:
  todo <description> - Add a todo task.
  deadline <description> /by <date/time> - Add a task with a deadline.
  event <description> /from <date/time> /to <date/time> - Add an event.
View tasks:
  list - Show all tasks.
  find <search text> - Show tasks matching text.
Update tasks:
  mark <task number> - Mark a task as done.
  unmark <task number> - Mark a task as not done.
  delete <task number> - Delete a task.
Other:
  help - Show this help page.
  bye - Exit Dobby.
Dates: use yyyy-MM-dd or d/M/yyyy. Times are optional; use HHmm or HH:mm.
Example: deadline return book /by 2019-12-02 1800
____________________________________________________________
Tell Dobby: ____________________________________________________________
> Dobby asks is this a Todo, Deadline, or Event?
> Dobby recommend whisper 'help' so that Dobby can help you!
____________________________________________________________
Tell Dobby: ____________________________________________________________
> Dobby show 1 tasks:
1. [T][ ] read book

____________________________________________________________
Tell Dobby: ____________________________________________________________
> Dobby says goodbye to master!
____________________________________________________________
```

## Skip invalid saved tasks without losing valid tasks

**Aim:** Confirm that an invalid saved record is reported and ignored while valid saved tasks still load.

**Command:**
```text
(echo T ^| 0 ^| recovered task&echo invalid saved task)>data\dobby.txt & javac -d build\ui-test src\main\java\dobby\Dobby.java src\main\java\dobby\command\*.java src\main\java\dobby\exception\*.java src\main\java\dobby\logic\*.java src\main\java\dobby\parser\*.java src\main\java\dobby\storage\*.java src\main\java\dobby\task\*.java src\main\java\dobby\ui\cli\*.java src\main\java\dobby\util\*.java && java -cp build\ui-test dobby.Dobby
```

**Input:**
```text
list
bye
```

**Expected output:**
```text
> Dobby skipped 1 invalid saved task(s).
____________________________________________________________
       *       .       *       .       *       
   .      ____        _     _              .   
     *   |  _ \  ___ | |__ | |__  _   _     * 
   .     | | | |/ _ \| '_ \| '_ \| | | |   . 
     *   | |_| | (_) | |_) | |_) | |_| |     * 
   .     |____/ \___/|_.__/|_.__/ \__, |   . 
                                  |___/        
       *       .       *       .       *       

> Dobby says hi!
> Dobby is ready to take orders.
____________________________________________________________
Tell Dobby: ____________________________________________________________
> Dobby show 1 tasks:
1. [T][ ] recovered task

____________________________________________________________
Tell Dobby: ____________________________________________________________
> Dobby says goodbye to master!
____________________________________________________________
```

## Reject malformed task dates without adding tasks

**Aim:** Confirm that missing markers and invalid calendar dates are rejected while a valid todo remains the only task in the list.

**Command:**
```text
del /q data\dobby.txt data\duke.txt 2>nul & javac -d build\ui-test src\main\java\dobby\Dobby.java src\main\java\dobby\command\*.java src\main\java\dobby\exception\*.java src\main\java\dobby\logic\*.java src\main\java\dobby\parser\*.java src\main\java\dobby\storage\*.java src\main\java\dobby\task\*.java src\main\java\dobby\ui\cli\*.java src\main\java\dobby\util\*.java && java -cp build\ui-test dobby.Dobby
```

**Input:**
```text
todo keep me
deadline missing date
list
event workshop /from 2019-02-30 /to 2019-03-01
list
bye
```

**Expected output:**
```text
____________________________________________________________
       *       .       *       .       *       
   .      ____        _     _              .   
     *   |  _ \  ___ | |__ | |__  _   _     * 
   .     | | | |/ _ \| '_ \| '_ \| | | |   . 
     *   | |_| | (_) | |_) | |_) | |_| |     * 
   .     |____/ \___/|_.__/|_.__/ \__, |   . 
                                  |___/        
       *       .       *       .       *       

> Dobby says hi!
> Dobby is ready to take orders.
____________________________________________________________
Tell Dobby: ____________________________________________________________
> Dobby noted a new Todo: keep me
____________________________________________________________
Tell Dobby: ____________________________________________________________
> Dobby is confused. Dobby think you meant 'deadline <description> /by <date/time>'
____________________________________________________________
Tell Dobby: ____________________________________________________________
> Dobby show 1 tasks:
1. [T][ ] keep me

____________________________________________________________
Tell Dobby: ____________________________________________________________
> Dobby needs valid dates: yyyy-MM-dd, optionally followed by HHmm.
____________________________________________________________
Tell Dobby: ____________________________________________________________
> Dobby show 1 tasks:
1. [T][ ] keep me

____________________________________________________________
Tell Dobby: ____________________________________________________________
> Dobby says goodbye to master!
____________________________________________________________
```

## Load saved tasks when Dobby starts

**Aim:** Confirm that a saved todo, completed deadline, and event with ISO dates are restored and reformatted when Dobby starts.

**Command:**
```text
(echo T ^| 0 ^| read book&echo D ^| 1 ^| return book ^| 2019-12-02 1800&echo E ^| 0 ^| project meeting ^| 2019-12-03 0900 ^| 2019-12-03 1100)>data\dobby.txt & javac -d build\ui-test src\main\java\dobby\Dobby.java src\main\java\dobby\command\*.java src\main\java\dobby\exception\*.java src\main\java\dobby\logic\*.java src\main\java\dobby\parser\*.java src\main\java\dobby\storage\*.java src\main\java\dobby\task\*.java src\main\java\dobby\ui\cli\*.java src\main\java\dobby\util\*.java && java -cp build\ui-test dobby.Dobby
```

**Input:**
```text
list
bye
```

**Expected output:**
```text
____________________________________________________________
       *       .       *       .       *       
   .      ____        _     _              .   
     *   |  _ \  ___ | |__ | |__  _   _     * 
   .     | | | |/ _ \| '_ \| '_ \| | | |   . 
     *   | |_| | (_) | |_) | |_) | |_| |     * 
   .     |____/ \___/|_.__/|_.__/ \__, |   . 
                                  |___/        
       *       .       *       .       *       

> Dobby says hi!
> Dobby is ready to take orders.
____________________________________________________________
Tell Dobby: ____________________________________________________________
> Dobby show 3 tasks:
1. [T][ ] read book
2. [D][X] return book (by: Dec 02 2019, 18:00)
3. [E][ ] project meeting (from: Dec 03 2019, 09:00 to: Dec 03 2019, 11:00)

____________________________________________________________
Tell Dobby: ____________________________________________________________
> Dobby says goodbye to master!
____________________________________________________________
```
