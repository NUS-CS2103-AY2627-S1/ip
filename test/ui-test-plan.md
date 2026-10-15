# UI Test Plan

This file defines the console UI checks run by `.codex/skills/test-ui/scripts/run-ui-tests.ps1`.
Each interaction runs its PowerShell input as a separate program session. Keep the expected output exact; the runner ignores only a single trailing line ending.

## Test Case: Exit politely

**Aim:** Verify that Lily starts, accepts `bye`, and displays its farewell message.

### Interaction 1

**Input**

```powershell
javac -d _temp\ui-test-out src\main\java\*.java; "bye" | java -cp _temp\ui-test-out Lily
```

**Expected output**

```text
 _     _ _       
| |   (_) |      
| |    _| |_   _ 
| |   | | | | | |
| |___| | | |_| |
\_____/_|\__, |  
          __/ |  
         |___/   

Hey there! I'm Lily.
What would you like to do today?
----------------------------------------------------------

Bye! See you soon :)
```

## Test Case: Save task changes

**Aim:** Verify that successful task changes are written to the data file in a parseable format.

### Interaction 1

**Input**

```powershell
Remove-Item -LiteralPath data\lily.txt -ErrorAction SilentlyContinue; javac -d _temp\ui-test-out src\main\java\*.java; "todo read book`ndeadline return book /by 2019-12-02`nmark 1`nbye" | java -cp _temp\ui-test-out Lily; Get-Content -LiteralPath data\lily.txt; Remove-Item -LiteralPath data\lily.txt
```

**Expected output**

```text
 _     _ _       
| |   (_) |      
| |    _| |_   _ 
| |   | | | | | |
| |___| | | |_| |
\_____/_|\__, |  
          __/ |  
         |___/   

Hey there! I'm Lily.
What would you like to do today?
----------------------------------------------------------
Got it. I've added this task:
	[T][ ] read book
Now you have 1 tasks in the list
----------------------------------------------------------
Got it. I've added this task:
	[D][ ] return book (by: Dec 02 2019)
Now you have 2 tasks in the list
----------------------------------------------------------
Nice! I've marked this task as done:
  [T][X] read book
----------------------------------------------------------

Bye! See you soon :)
T | 1 | read book
D | 0 | return book | 2019-12-02T00:00
```

## Test Case: Load saved tasks

**Aim:** Verify that Lily restores saved todo, deadline, and event tasks when it starts.

### Interaction 1

**Input**

```powershell
New-Item -ItemType Directory -Path data -Force | Out-Null; @("T | 1 | read book", "D | 0 | return book | 2019-12-02T00:00", "E | 1 | project meeting | 2019-12-02T14:00 | 2019-12-02T16:00") | Set-Content -LiteralPath data\lily.txt; javac -d _temp\ui-test-out src\main\java\*.java; "list`nbye" | java -cp _temp\ui-test-out Lily; Remove-Item -LiteralPath data\lily.txt
```

**Expected output**

```text
 _     _ _       
| |   (_) |      
| |    _| |_   _ 
| |   | | | | | |
| |___| | | |_| |
\_____/_|\__, |  
          __/ |  
         |___/   

Hey there! I'm Lily.
What would you like to do today?
----------------------------------------------------------
1. [T][X] read book
2. [D][ ] return book (by: Dec 02 2019)
3. [E][X] project meeting (from: Dec 02 2019, 2:00pm to: Dec 02 2019, 4:00pm)
----------------------------------------------------------

Bye! See you soon :)
```

## Test Case: View a dated schedule

**Aim:** Verify that Lily excludes todos, retains original task numbers, orders
date-only entries before timed entries, and separates completed tasks.

### Interaction 1

**Input**

```powershell
New-Item -ItemType Directory -Path data -Force | Out-Null; @("T | 0 | undated task", "E | 0 | morning meeting | 2026-09-16T09:00 | 2026-09-16T10:00", "D | 0 | submit report | 2026-09-16T00:00", "D | 1 | call client | 2026-09-16T14:00") | Set-Content -LiteralPath data\lily.txt; .\gradlew.bat classes; "schedule 2026-09-16`nbye" | java -cp build\classes\java\main lily.Lily
```

**Expected command response**

```text
Here is your schedule for Sep 16 2026:
Not completed:
3. [D][ ] submit report (by: Sep 16 2026)
2. [E][ ] morning meeting (from: Sep 16 2026, 9:00AM to: Sep 16 2026, 10:00AM)
Completed:
4. [D][X] call client (by: Sep 16 2026, 2:00PM)
```

The normal welcome message, separators, and farewell message surround this command
response as in the preceding UI test cases.
