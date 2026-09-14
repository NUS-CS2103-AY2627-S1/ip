# UI test plan

Use one level-two section per test case. The `test-ui` skill executes the command in each case from the project root and compares the complete console output with the expected-output block.

## TC01: Exit immediately

**Aim:** Verify that the application displays its greeting and a farewell when the user exits.

**Command:**
```powershell
powershell -NoProfile -Command "Remove-Item data\arrodes.txt -ErrorAction SilentlyContinue; java -jar build\libs\arrodes.jar --cli"
```

**Input:**
```text
bye
```

**Expected output:**
```text
____________________________________________________________
    _                       _
   / \   _ __ _ __ ___   __| | ___  ___
  / _ \ | '__| '__/ _ \ / _` |/ _ \/ __|
 / ___ \| |  | | | (_) | (_| |  __/\__ \
/_/   \_\_|  |_|  \___/ \__,_|\___||___/
Eyes that watch All living Beings
The Stigmata from the Primordial Land
The Great Arrodes is before you!
State your request!
____________________________________________________________
____________________________________________________________
I shall await your next request...
____________________________________________________________
```

## TC04: Recover from a corrupted data file

**Aim:** Verify that a corrupted data file is reported and the chatbot continues with an empty task list.

**Command:**
```powershell
powershell -NoProfile -Command "Set-Content data\arrodes.txt 'X | 0 | corrupt'; java -jar build\libs\arrodes.jar --cli"
```

**Input:**
```text
list
bye
```

**Expected output:**
```text
Arrodes could not load your requests.
____________________________________________________________
    _                       _
   / \   _ __ _ __ ___   __| | ___  ___
  / _ \ | '__| '__/ _ \ / _` |/ _ \/ __|
 / ___ \| |  | | | (_) | (_| |  __/\__ \
/_/   \_\_|  |_|  \___/ \__,_|\___||___/
Eyes that watch All living Beings
The Stigmata from the Primordial Land
The Great Arrodes is before you!
State your request!
____________________________________________________________
____________________________________________________________
Arrodes recalls your requests:
____________________________________________________________
____________________________________________________________
I shall await your next request...
____________________________________________________________
```

## TC02: Add and list every task type

**Aim:** Verify that todo, deadline, event, tag, and list commands display their stored tasks.

**Command:**
```powershell
powershell -NoProfile -Command "Remove-Item data\arrodes.txt -ErrorAction SilentlyContinue; java -jar build\libs\arrodes.jar --cli"
```

**Input:**
```text
todo buy milk
deadline submit report /by 2026-08-30
event team meeting /from 2026-08-30T14:00 /to 2026-08-30T15:00
tag 3 /with work
list
bye
```

**Expected output:**
```text
____________________________________________________________
    _                       _
   / \   _ __ _ __ ___   __| | ___  ___
  / _ \ | '__| '__/ _ \ / _` |/ _ \/ __|
 / ___ \| |  | | | (_) | (_| |  __/\__ \
/_/   \_\_|  |_|  \___/ \__,_|\___||___/
Eyes that watch All living Beings
The Stigmata from the Primordial Land
The Great Arrodes is before you!
State your request!
____________________________________________________________
____________________________________________________________
Inscribing request: 
   [T][ ] buy milk
1 tasks are being tracked
____________________________________________________________
____________________________________________________________
Inscribing request: 
   [D][ ] submit report (by: Aug 30 2026)
2 tasks are being tracked
____________________________________________________________
____________________________________________________________
Inscribing request: 
   [E][ ] team meeting (from: Aug 30 2026 14:00 to Aug 30 2026 15:00)
3 tasks are being tracked
____________________________________________________________
____________________________________________________________
Arrodes shall bestow the task with work tag
  [E][ ] team meeting (from: Aug 30 2026 14:00 to Aug 30 2026 15:00) #work
____________________________________________________________
____________________________________________________________
Arrodes recalls your requests:
1.[T][ ] buy milk
2.[D][ ] submit report (by: Aug 30 2026)
3.[E][ ] team meeting (from: Aug 30 2026 14:00 to Aug 30 2026 15:00) #work
____________________________________________________________
____________________________________________________________
I shall await your next request...
____________________________________________________________
```

## TC03: Mark, unmark, and delete tasks

**Aim:** Verify that task status changes and deletion update the task list.

**Command:**
```powershell
powershell -NoProfile -Command "Remove-Item data\arrodes.txt -ErrorAction SilentlyContinue; java -jar build\libs\arrodes.jar --cli"
```

**Input:**
```text
todo buy milk
event team meeting /from 2026-08-30T14:00 /to 2026-08-30T15:00
mark 1
unmark 1
delete 2
list
bye
```

**Expected output:**
```text
____________________________________________________________
    _                       _
   / \   _ __ _ __ ___   __| | ___  ___
  / _ \ | '__| '__/ _ \ / _` |/ _ \/ __|
 / ___ \| |  | | | (_) | (_| |  __/\__ \
/_/   \_\_|  |_|  \___/ \__,_|\___||___/
Eyes that watch All living Beings
The Stigmata from the Primordial Land
The Great Arrodes is before you!
State your request!
____________________________________________________________
____________________________________________________________
Inscribing request: 
   [T][ ] buy milk
1 tasks are being tracked
____________________________________________________________
____________________________________________________________
Inscribing request: 
   [E][ ] team meeting (from: Aug 30 2026 14:00 to Aug 30 2026 15:00)
2 tasks are being tracked
____________________________________________________________
____________________________________________________________
A worthy task! Arrodes has marked it as done:
  [T][X] buy milk
____________________________________________________________
____________________________________________________________
As you decree, Arrodes has marked this task as not done yet:
  [T][ ] buy milk
____________________________________________________________
____________________________________________________________
Erasing records of the task:

[E][ ] team meeting (from: Aug 30 2026 14:00 to Aug 30 2026 15:00)

1 tasks remaining are being tracked
____________________________________________________________
____________________________________________________________
Arrodes recalls your requests:
1.[T][ ] buy milk
____________________________________________________________
____________________________________________________________
I shall await your next request...
____________________________________________________________
```

## TC05: Reject impossible calendar dates

**Aim:** Verify that a correctly shaped but impossible date, such as February 31, is rejected clearly.

**Command:**
```powershell
powershell -NoProfile -Command "Remove-Item data\arrodes.txt -ErrorAction SilentlyContinue; java -jar build\libs\arrodes.jar --cli"
```

**Input:**
```text
deadline submit report /by 2026-02-31
bye
```

**Expected output:**
```text
____________________________________________________________
    _                       _
   / \   _ __ _ __ ___   __| | ___  ___
  / _ \ | '__| '__/ _ \ / _` |/ _ \/ __|
 / ___ \| |  | | | (_) | (_| |  __/\__ \
/_/   \_\_|  |_|  \___/ \__,_|\___||___/
Eyes that watch All living Beings
The Stigmata from the Primordial Land
The Great Arrodes is before you!
State your request!
____________________________________________________________
____________________________________________________________
That date is invalid because the specified day or time does not exist.
____________________________________________________________
____________________________________________________________
I shall await your next request...
____________________________________________________________
```

## TC07: Find upcoming tasks on a date

**Aim:** Verify that upcoming lists matching deadlines and events for date-only and date-time queries.

**Command:**
```powershell
powershell -NoProfile -Command "Remove-Item data\arrodes.txt -ErrorAction SilentlyContinue; java -jar build\libs\arrodes.jar --cli"
```

**Input:**
```text
deadline submit report /by 2026-10-15T17:00
event all day /from 2026-10-15 /to 2026-10-15
event meeting /from 2026-10-14T23:00 /to 2026-10-15T01:00
todo buy milk
upcoming /on 2026-10-15
upcoming /on 2026-10-15T00:30
bye
```

**Expected output:**
```text
____________________________________________________________
    _                       _
   / \   _ __ _ __ ___   __| | ___  ___
  / _ \ | '__| '__/ _ \ / _` |/ _ \/ __|
 / ___ \| |  | | | (_) | (_| |  __/\__ \
/_/   \_\_|  |_|  \___/ \__,_|\___||___/
Eyes that watch All living Beings
The Stigmata from the Primordial Land
The Great Arrodes is before you!
State your request!
____________________________________________________________
____________________________________________________________
Inscribing request: 
   [D][ ] submit report (by: Oct 15 2026 17:00)
1 tasks are being tracked
____________________________________________________________
____________________________________________________________
Inscribing request: 
   [E][ ] all day (from: Oct 15 2026 to Oct 15 2026)
2 tasks are being tracked
____________________________________________________________
____________________________________________________________
Inscribing request: 
   [E][ ] meeting (from: Oct 14 2026 23:00 to Oct 15 2026 01:00)
3 tasks are being tracked
____________________________________________________________
____________________________________________________________
Inscribing request: 
   [T][ ] buy milk
4 tasks are being tracked
____________________________________________________________
____________________________________________________________
Arrodes recalls requests for Oct 15 2026:
1.[D][ ] submit report (by: Oct 15 2026 17:00)
2.[E][ ] all day (from: Oct 15 2026 to Oct 15 2026)
3.[E][ ] meeting (from: Oct 14 2026 23:00 to Oct 15 2026 01:00)
____________________________________________________________
____________________________________________________________
Arrodes recalls requests for Oct 15 2026 00:30:
3.[E][ ] meeting (from: Oct 14 2026 23:00 to Oct 15 2026 01:00)
____________________________________________________________
____________________________________________________________
I shall await your next request...
____________________________________________________________
```

## TC08: Find tasks by description

**Aim:** Verify that find displays matching tasks with their original list numbers and reports when there are no matches.

**Command:**
```powershell
powershell -NoProfile -Command "Set-Content data\arrodes.txt 'T | 1 | read book','D | 1 | return book | 2026-06-06','T | 1 | wash dishes'; java -jar build\libs\arrodes.jar --cli"
```

**Input:**
```text
find book
find laptop
bye
```

**Expected output:**
```text
____________________________________________________________
    _                       _
   / \   _ __ _ __ ___   __| | ___  ___
  / _ \ | '__| '__/ _ \ / _` |/ _ \/ __|
 / ___ \| |  | | | (_) | (_| |  __/\__ \
/_/   \_\_|  |_|  \___/ \__,_|\___||___/
Eyes that watch All living Beings
The Stigmata from the Primordial Land
The Great Arrodes is before you!
State your request!
____________________________________________________________
____________________________________________________________
Here are the matching tasks in your list:
1.[T][X] read book
2.[D][X] return book (by: Jun 06 2026)
____________________________________________________________
____________________________________________________________
Here are the matching tasks in your list:
Arrodes found no matching tasks.
____________________________________________________________
____________________________________________________________
I shall await your next request...
____________________________________________________________
```

## TC06: Validate event time ordering

**Aim:** Verify that same-day date-only events are accepted and reversed event times produce a warning.

**Command:**
```powershell
powershell -NoProfile -Command "Remove-Item data\arrodes.txt -ErrorAction SilentlyContinue; java -jar build\libs\arrodes.jar --cli"
```

**Input:**
```text
event same day /from 2026-10-15 /to 2026-10-15
event reversed /from 2026-10-15T10:00 /to 2026-10-15T09:00
bye
```

**Expected output:**
```text
____________________________________________________________
    _                       _
   / \   _ __ _ __ ___   __| | ___  ___
  / _ \ | '__| '__/ _ \ / _` |/ _ \/ __|
 / ___ \| |  | | | (_) | (_| |  __/\__ \
/_/   \_\_|  |_|  \___/ \__,_|\___||___/
Eyes that watch All living Beings
The Stigmata from the Primordial Land
The Great Arrodes is before you!
State your request!
____________________________________________________________
____________________________________________________________
Inscribing request: 
   [E][ ] same day (from: Oct 15 2026 to Oct 15 2026)
1 tasks are being tracked
____________________________________________________________
____________________________________________________________
Event end time must not be earlier than its start time.
____________________________________________________________
____________________________________________________________
I shall await your next request...
____________________________________________________________
```

**Input:**
```text
bye
```

**Expected output:**
```text
____________________________________________________________
    _                       _
   / \   _ __ _ __ ___   __| | ___  ___
  / _ \ | '__| '__/ _ \ / _` |/ _ \/ __|
 / ___ \| |  | | | (_) | (_| |  __/\__ \
/_/   \_\_|  |_|  \___/ \__,_|\___||___/
Eyes that watch All living Beings
The Stigmata from the Primordial Land
The Great Arrodes is before you!
State your request!
____________________________________________________________
____________________________________________________________
I shall await your next request...
____________________________________________________________
```
