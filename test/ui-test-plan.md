# UI Test Plan

Run this plan with Java 25 active:

```sh
sdk use java 25.0.3.fx-zulu
python3 .codex/skills/test-ui/scripts/run_ui_tests.py test/ui-test-plan.md
```

The command in each case compiles the console sources into a temporary, ignored directory, then starts a fresh
Lizzy session. JavaFX classes are excluded because these cases verify the console interface without a JavaFX
module path. Expected output is exact except for line-ending normalization.

The invalid-input cases below define the expected validation behaviour: Lizzy should explain the problem, continue accepting commands, and leave the existing task list unchanged.

## Test cases

### Exit politely
- Aim: Verify that Lizzy starts, recognises `bye`, and prints its farewell.
- Command:
  ```sh
  mkdir -p _temp/ui-test-data && rm -f _temp/ui-test-data/current.txt && javac -d _temp/ui-test-classes $(find src/main/java -name '*.java' ! -path '*/gui/*') && java -Dlizzy.data.path=_temp/ui-test-data/current.txt -cp _temp/ui-test-classes lizzy.Lizzy
  ```
- Inputs:
  ```text
  bye
  ```
- Expected output:
  ```text
  ____________________________________________________________
      __    _
     / /   (_)_______  __  __
    / /   / /_  /_  / / / / /
   / /___/ / / /_/ /_/ /_/ /
  /_____/_/ /___/___/\__, /
                    /____/
  Hello! I'm Lizzy.
  What brings you here today?
  Type help whenever you would like a quick command guide.
  ____________________________________________________________
  ____________________________________________________________
  Goodbye! May your plans prosper—and leave you a little leisure.
  ____________________________________________________________
  ```

### Show command help
- Aim: Verify that help gives a concise, in-character summary of every command's format and purpose.
- Command:
  ```sh
  mkdir -p _temp/ui-test-data && rm -f _temp/ui-test-data/current.txt && javac -d _temp/ui-test-classes $(find src/main/java -name '*.java' ! -path '*/gui/*') && java -Dlizzy.data.path=_temp/ui-test-data/current.txt -cp _temp/ui-test-classes lizzy.Lizzy
  ```
- Inputs:
  ```text
  help
  bye
  ```
- Expected output:
  ```text
  ____________________________________________________________
      __    _
     / /   (_)_______  __  __
    / /   / /_  /_  / / / / /
   / /___/ / / /_/ /_/ /_/ /
  /_____/_/ /___/___/\__, /
                    /____/
  Hello! I'm Lizzy.
  What brings you here today?
  Type help whenever you would like a quick command guide.
  ____________________________________________________________
  ____________________________________________________________
  A brief guide, should memory prove uncooperative:
  Add tasks:
    • todo <description> — undated task
    • deadline <description> /by <yyyy-MM-dd> — task due on a date
    • event <description> /on <yyyy-MM-dd> — one-day event
    • event <description> /on <yyyy-MM-dd> /from <HH:mm> /to <HH:mm> — timed event
    • event <description> /from <yyyy-MM-dd> /to <yyyy-MM-dd> — date-range event
    • within <description> /from <yyyy-MM-dd> /to <yyyy-MM-dd> — flexible-period task

  View tasks:
    • list — show every task
    • find <keyword> — partially search descriptions
    • on <yyyy-MM-dd> — show one day's schedule

  Manage tasks:
    • mark <number> — complete a task
    • unmark <number> — reopen a task
    • delete <number> — remove a task
    • bye — close Lizzy
  Dates use yyyy-MM-dd (for example, 2026-09-10); times use HH:mm (for example, 14:30).
  A little precision saves much puzzlement.
  ____________________________________________________________
  ____________________________________________________________
  Goodbye! May your plans prosper—and leave you a little leisure.
  ____________________________________________________________
  ```

### Reject duplicate and ambiguous command input
- Aim: Verify that duplicate tasks, repeated date markers, and decorated task numbers are rejected without changing the valid task list.
- Command:
  ```sh
  mkdir -p _temp/ui-test-data && rm -f _temp/ui-test-data/current.txt && javac -d _temp/ui-test-classes $(find src/main/java -name '*.java' ! -path '*/gui/*') && java -Dlizzy.data.path=_temp/ui-test-data/current.txt -cp _temp/ui-test-classes lizzy.Lizzy
  ```
- Inputs:
  ```text
  todo read chapter
  todo read chapter
  deadline report /by 2026-09-20 /by 2026-09-21
  event meeting /from 2026-09-20 /to 2026-09-21 /to 2026-09-22
  mark +1
  mark 01
  list
  bye
  ```
- Expected output:
  ```text
  ____________________________________________________________
      __    _
     / /   (_)_______  __  __
    / /   / /_  /_  / / / / /
   / /___/ / / /_/ /_/ /_/ /
  /_____/_/ /___/___/\__, /
                    /____/
  Hello! I'm Lizzy.
  What brings you here today?
  Type help whenever you would like a quick command guide.
  ____________________________________________________________
  ____________________________________________________________
  Here comes another matter to keep track of:
    [T][ ] read chapter
  That makes 1 task awaiting your attention.
  ____________________________________________________________
  ____________________________________________________________
  This task is already keeping its place in your list.
  Even a worthy matter need not be introduced twice.
  ____________________________________________________________
  ____________________________________________________________
  A deadline is easier to keep when its due date is stated clearly.
  Write it as: deadline <description> /by <yyyy-MM-dd>.
  ____________________________________________________________
  ____________________________________________________________
  An engagement may be mysterious, but not about when.
  Use /on <yyyy-MM-dd>, or write the full range with both /from and /to.
  ____________________________________________________________
  ____________________________________________________________
  I'm afraid that will not quite do; I need a proper task number.
  Be precise: mark <task number>.
  ____________________________________________________________
  ____________________________________________________________
  I'm afraid that will not quite do; I need a proper task number.
  Be precise: mark <task number>.
  ____________________________________________________________
  ____________________________________________________________
  Let us see what has found its way onto your list:
  1.[T][ ] read chapter
  ____________________________________________________________
  ____________________________________________________________
  Goodbye! May your plans prosper—and leave you a little leisure.
  ____________________________________________________________
  ```

### Add and list a todo
- Aim: Verify that a todo receives the T type marker and can be listed as incomplete.
- Command:
  ```sh
  mkdir -p _temp/ui-test-data && rm -f _temp/ui-test-data/current.txt && javac -d _temp/ui-test-classes $(find src/main/java -name '*.java' ! -path '*/gui/*') && java -Dlizzy.data.path=_temp/ui-test-data/current.txt -cp _temp/ui-test-classes lizzy.Lizzy
  ```
- Inputs:
  ```text
  todo read book
  list
  bye
  ```
- Expected output:
  ```text
  ____________________________________________________________
      __    _
     / /   (_)_______  __  __
    / /   / /_  /_  / / / / /
   / /___/ / / /_/ /_/ /_/ /
  /_____/_/ /___/___/\__, /
                    /____/
  Hello! I'm Lizzy.
  What brings you here today?
  Type help whenever you would like a quick command guide.
  ____________________________________________________________
  ____________________________________________________________
  Here comes another matter to keep track of:
    [T][ ] read book
  That makes 1 task awaiting your attention.
  ____________________________________________________________
  ____________________________________________________________
  Let us see what has found its way onto your list:
  1.[T][ ] read book
  ____________________________________________________________
  ____________________________________________________________
  Goodbye! May your plans prosper—and leave you a little leisure.
  ____________________________________________________________
  ```

### Add, update, and list dated tasks
- Aim: Verify that deadlines and events retain their date/time text and task type markers after status updates.
- Command:
  ```sh
  mkdir -p _temp/ui-test-data && rm -f _temp/ui-test-data/current.txt && javac -d _temp/ui-test-classes $(find src/main/java -name '*.java' ! -path '*/gui/*') && java -Dlizzy.data.path=_temp/ui-test-data/current.txt -cp _temp/ui-test-classes lizzy.Lizzy
  ```
- Inputs:
  ```text
  deadline submit report /by 2026-08-30
  event project meeting /from 2026-08-31 /to 2026-09-01
  mark 1
  unmark 1
  list
  bye
  ```
- Expected output:
  ```text
  ____________________________________________________________
      __    _
     / /   (_)_______  __  __
    / /   / /_  /_  / / / / /
   / /___/ / / /_/ /_/ /_/ /
  /_____/_/ /___/___/\__, /
                    /____/
  Hello! I'm Lizzy.
  What brings you here today?
  Type help whenever you would like a quick command guide.
  ____________________________________________________________
  ____________________________________________________________
  A deadline, then. We'd better not keep it waiting.
    [D][ ] submit report (by: Aug 30 2026)
  That makes 1 task awaiting your attention.
  ____________________________________________________________
  ____________________________________________________________
  An engagement! I've added it to your list:
    [E][ ] project meeting (from: Aug 31 2026 to: Sep 1 2026)
  That makes 2 tasks awaiting your attention.
  ____________________________________________________________
  ____________________________________________________________
  Very good! That is one matter settled:
    [D][X] submit report (by: Aug 30 2026)
  ____________________________________________________________
  ____________________________________________________________
  Ah, it seems this matter is not quite settled:
    [D][ ] submit report (by: Aug 30 2026)
  ____________________________________________________________
  ____________________________________________________________
  Let us see what has found its way onto your list:
  1.[D][ ] submit report (by: Aug 30 2026)
  2.[E][ ] project meeting (from: Aug 31 2026 to: Sep 1 2026)
  ____________________________________________________________
  ____________________________________________________________
  Goodbye! May your plans prosper—and leave you a little leisure.
  ____________________________________________________________
  ```

### List an empty task list
- Aim: Verify that listing before any task is added is safe and does not display phantom tasks.
- Command:
  ```sh
  mkdir -p _temp/ui-test-data && rm -f _temp/ui-test-data/current.txt && javac -d _temp/ui-test-classes $(find src/main/java -name '*.java' ! -path '*/gui/*') && java -Dlizzy.data.path=_temp/ui-test-data/current.txt -cp _temp/ui-test-classes lizzy.Lizzy
  ```
- Inputs:
  ```text
  list
  bye
  ```
- Expected output:
  ```text
  ____________________________________________________________
      __    _
     / /   (_)_______  __  __
    / /   / /_  /_  / / / / /
   / /___/ / / /_/ /_/ /_/ /
  /_____/_/ /___/___/\__, /
                    /____/
  Hello! I'm Lizzy.
  What brings you here today?
  Type help whenever you would like a quick command guide.
  ____________________________________________________________
  ____________________________________________________________
  Your list is blissfully free of obligations.
  ____________________________________________________________
  ____________________________________________________________
  Goodbye! May your plans prosper—and leave you a little leisure.
  ____________________________________________________________
  ```

### Preserve task state after an invalid task number
- Aim: Verify that a valid status update is retained while an intervening out-of-range task number is rejected without changing the task list.
- Command:
  ```sh
  mkdir -p _temp/ui-test-data && rm -f _temp/ui-test-data/current.txt && javac -d _temp/ui-test-classes $(find src/main/java -name '*.java' ! -path '*/gui/*') && java -Dlizzy.data.path=_temp/ui-test-data/current.txt -cp _temp/ui-test-classes lizzy.Lizzy
  ```
- Inputs:
  ```text
  todo revise notes
  mark 1
  mark 2
  unmark 1
  list
  bye
  ```
- Expected output:
  ```text
  ____________________________________________________________
      __    _
     / /   (_)_______  __  __
    / /   / /_  /_  / / / / /
   / /___/ / / /_/ /_/ /_/ /
  /_____/_/ /___/___/\__, /
                    /____/
  Hello! I'm Lizzy.
  What brings you here today?
  Type help whenever you would like a quick command guide.
  ____________________________________________________________
  ____________________________________________________________
  Here comes another matter to keep track of:
    [T][ ] revise notes
  That makes 1 task awaiting your attention.
  ____________________________________________________________
  ____________________________________________________________
  Very good! That is one matter settled:
    [T][X] revise notes
  ____________________________________________________________
  ____________________________________________________________
  An imaginative choice—but that task is not on the present list.
  Choose a task number from 1 to 1.
  ____________________________________________________________
  ____________________________________________________________
  Ah, it seems this matter is not quite settled:
    [T][ ] revise notes
  ____________________________________________________________
  ____________________________________________________________
  Let us see what has found its way onto your list:
  1.[T][ ] revise notes
  ____________________________________________________________
  ____________________________________________________________
  Goodbye! May your plans prosper—and leave you a little leisure.
  ____________________________________________________________
  ```

### Preserve valid tasks around malformed dated commands
- Aim: Verify that missing descriptions, missing or unmarked schedule markers, and incomplete event ranges are rejected without consuming task-list positions between valid commands.
- Command:
  ```sh
  mkdir -p _temp/ui-test-data && rm -f _temp/ui-test-data/current.txt && javac -d _temp/ui-test-classes $(find src/main/java -name '*.java' ! -path '*/gui/*') && java -Dlizzy.data.path=_temp/ui-test-data/current.txt -cp _temp/ui-test-classes lizzy.Lizzy
  ```
- Inputs:
  ```text
  todo review lecture
  deadline submit assignment
  deadline submit assignment by 2026-09-10
  deadline /by 2026-09-10
  todo prepare slides
  event consultation /from Tue 2pm
  event consultation on 2026-09-10
  event /on 2026-09-10
  event meeting /on 2026-09-10 /from 14:00
  event meeting /on 2026-09-10 14:00 to 16:00
  deadline submit assignment /by 2026-08-28
  list
  bye
  ```
- Expected output:
  ```text
  ____________________________________________________________
      __    _
     / /   (_)_______  __  __
    / /   / /_  /_  / / / / /
   / /___/ / / /_/ /_/ /_/ /
  /_____/_/ /___/___/\__, /
                    /____/
  Hello! I'm Lizzy.
  What brings you here today?
  Type help whenever you would like a quick command guide.
  ____________________________________________________________
  ____________________________________________________________
  Here comes another matter to keep track of:
    [T][ ] review lecture
  That makes 1 task awaiting your attention.
  ____________________________________________________________
  ____________________________________________________________
  A deadline is easier to keep when its due date is stated clearly.
  Write it as: deadline <description> /by <yyyy-MM-dd>.
  ____________________________________________________________
  ____________________________________________________________
  A deadline is easier to keep when its due date is stated clearly.
  Write it as: deadline <description> /by <yyyy-MM-dd>.
  ____________________________________________________________
  ____________________________________________________________
  A deadline with nothing to accomplish would be all suspense and no substance.
  Give it a duty: deadline <description> /by <yyyy-MM-dd>.
  ____________________________________________________________
  ____________________________________________________________
  Here comes another matter to keep track of:
    [T][ ] prepare slides
  That makes 2 tasks awaiting your attention.
  ____________________________________________________________
  ____________________________________________________________
  An engagement may be mysterious, but not about when.
  Use /on <yyyy-MM-dd>, or write the full range with both /from and /to.
  ____________________________________________________________
  ____________________________________________________________
  An engagement may be mysterious, but not about when.
  Use /on <yyyy-MM-dd>, or write the full range with both /from and /to.
  ____________________________________________________________
  ____________________________________________________________
  An engagement without a description leaves rather too much to guess.
  Tell me what it is: event <description> /on <yyyy-MM-dd>.
  ____________________________________________________________
  ____________________________________________________________
  An engagement may be mysterious, but not about when.
  Use /on <yyyy-MM-dd>, or write the full range with both /from and /to.
  ____________________________________________________________
  ____________________________________________________________
  An engagement may be mysterious, but not about when.
  Use /on <yyyy-MM-dd>, or write the full range with both /from and /to.
  ____________________________________________________________
  ____________________________________________________________
  A deadline, then. We'd better not keep it waiting.
    [D][ ] submit assignment (by: Aug 28 2026)
  That makes 3 tasks awaiting your attention.
  ____________________________________________________________
  ____________________________________________________________
  Let us see what has found its way onto your list:
  1.[T][ ] review lecture
  2.[T][ ] prepare slides
  3.[D][ ] submit assignment (by: Aug 28 2026)
  ____________________________________________________________
  ____________________________________________________________
  Goodbye! May your plans prosper—and leave you a little leisure.
  ____________________________________________________________
  ```

### Reject malformed commands while retaining valid task state
- Aim: Verify that blank, unknown, malformed task-creation, malformed status, and extra-argument commands are all rejected without changing valid tasks added before or after them.
- Command:
  ```sh
  mkdir -p _temp/ui-test-data && rm -f _temp/ui-test-data/current.txt && javac -d _temp/ui-test-classes $(find src/main/java -name '*.java' ! -path '*/gui/*') && java -Dlizzy.data.path=_temp/ui-test-data/current.txt -cp _temp/ui-test-classes lizzy.Lizzy
  ```
- Inputs:
  ```text

  todo read book
  blah
  todo
  deadline submit report
  deadline submit report /by 2026-08-28
  event meeting /from Monday
  event meeting /from 2026-08-29 /to 2026-08-30
  mark first
  mark 4
  unmark 2
  list later
  bye later
  list
  bye
  ```
- Expected output:
  ```text
  ____________________________________________________________
      __    _
     / /   (_)_______  __  __
    / /   / /_  /_  / / / / /
   / /___/ / / /_/ /_/ /_/ /
  /_____/_/ /___/___/\__, /
                    /____/
  Hello! I'm Lizzy.
  What brings you here today?
  Type help whenever you would like a quick command guide.
  ____________________________________________________________
  ____________________________________________________________
  Silence may be elegant, but it gives me very little to work with.
  A little direction will do: todo <description>, list, or another command.
  ____________________________________________________________
  ____________________________________________________________
  Here comes another matter to keep track of:
    [T][ ] read book
  That makes 1 task awaiting your attention.
  ____________________________________________________________
  ____________________________________________________________
  I'm afraid "blah" is quite beyond my acquaintance.
  You may try: todo, deadline, event, within, list, find, on, mark, unmark, delete, help, or bye.
  ____________________________________________________________
  ____________________________________________________________
  A task with nothing to do is hardly a task at all.
  Give it some substance: todo <description>.
  ____________________________________________________________
  ____________________________________________________________
  A deadline is easier to keep when its due date is stated clearly.
  Write it as: deadline <description> /by <yyyy-MM-dd>.
  ____________________________________________________________
  ____________________________________________________________
  A deadline, then. We'd better not keep it waiting.
    [D][ ] submit report (by: Aug 28 2026)
  That makes 2 tasks awaiting your attention.
  ____________________________________________________________
  ____________________________________________________________
  An engagement may be mysterious, but not about when.
  Use /on <yyyy-MM-dd>, or write the full range with both /from and /to.
  ____________________________________________________________
  ____________________________________________________________
  An engagement! I've added it to your list:
    [E][ ] meeting (from: Aug 29 2026 to: Aug 30 2026)
  That makes 3 tasks awaiting your attention.
  ____________________________________________________________
  ____________________________________________________________
  I'm afraid that will not quite do; I need a proper task number.
  Be precise: mark <task number>.
  ____________________________________________________________
  ____________________________________________________________
  An imaginative choice—but that task is not on the present list.
  Choose a task number from 1 to 3.
  ____________________________________________________________
  ____________________________________________________________
  That matter is already waiting to be done.
  There is nothing to reopen just yet.
  ____________________________________________________________
  ____________________________________________________________
  A list requires no further instruction.
  A simple "list" will do.
  ____________________________________________________________
  ____________________________________________________________
  One farewell at a time, if you please.
  A simple "bye" will do.
  ____________________________________________________________
  ____________________________________________________________
  Let us see what has found its way onto your list:
  1.[T][ ] read book
  2.[D][ ] submit report (by: Aug 28 2026)
  3.[E][ ] meeting (from: Aug 29 2026 to: Aug 30 2026)
  ____________________________________________________________
  ____________________________________________________________
  Goodbye! May your plans prosper—and leave you a little leisure.
  ____________________________________________________________
  ```

### Reject status updates on an empty list
- Aim: Verify that missing task numbers, empty-list updates, and out-of-range updates do not prevent a later valid status update.
- Command:
  ```sh
  mkdir -p _temp/ui-test-data && rm -f _temp/ui-test-data/current.txt && javac -d _temp/ui-test-classes $(find src/main/java -name '*.java' ! -path '*/gui/*') && java -Dlizzy.data.path=_temp/ui-test-data/current.txt -cp _temp/ui-test-classes lizzy.Lizzy
  ```
- Inputs:
  ```text
  mark 1
  unmark 1
  todo write report
  unmark
  unmark 0
  mark 1
  list
  bye
  ```
- Expected output:
  ```text
  ____________________________________________________________
      __    _
     / /   (_)_______  __  __
    / /   / /_  /_  / / / / /
   / /___/ / / /_/ /_/ /_/ /
  /_____/_/ /___/___/\__, /
                    /____/
  Hello! I'm Lizzy.
  What brings you here today?
  Type help whenever you would like a quick command guide.
  ____________________________________________________________
  ____________________________________________________________
  Ambitious—but there is nothing to mark while your list is empty.
  Add a task first, and then we shall have something to mark.
  ____________________________________________________________
  ____________________________________________________________
  Ambitious—but there is nothing to unmark while your list is empty.
  Add a task first, and then we shall have something to unmark.
  ____________________________________________________________
  ____________________________________________________________
  Here comes another matter to keep track of:
    [T][ ] write report
  That makes 1 task awaiting your attention.
  ____________________________________________________________
  ____________________________________________________________
  I'm afraid that will not quite do; I need a proper task number.
  Be precise: unmark <task number>.
  ____________________________________________________________
  ____________________________________________________________
  I'm afraid that will not quite do; I need a proper task number.
  Be precise: unmark <task number>.
  ____________________________________________________________
  ____________________________________________________________
  Very good! That is one matter settled:
    [T][X] write report
  ____________________________________________________________
  ____________________________________________________________
  Let us see what has found its way onto your list:
  1.[T][X] write report
  ____________________________________________________________
  ____________________________________________________________
  Goodbye! May your plans prosper—and leave you a little leisure.
  ____________________________________________________________
  ```

### Reject redundant task status updates
- Aim: Verify that marking a complete task or unmarking an incomplete task is rejected without changing its state.
- Command:
  ```sh
  mkdir -p _temp/ui-test-data && rm -f _temp/ui-test-data/current.txt && javac -d _temp/ui-test-classes $(find src/main/java -name '*.java' ! -path '*/gui/*') && java -Dlizzy.data.path=_temp/ui-test-data/current.txt -cp _temp/ui-test-classes lizzy.Lizzy
  ```
- Inputs:
  ```text
  todo read chapter
  unmark 1
  mark 1
  mark 1
  unmark 1
  unmark 1
  list
  bye
  ```
- Expected output:
  ```text
  ____________________________________________________________
      __    _
     / /   (_)_______  __  __
    / /   / /_  /_  / / / / /
   / /___/ / / /_/ /_/ /_/ /
  /_____/_/ /___/___/\__, /
                    /____/
  Hello! I'm Lizzy.
  What brings you here today?
  Type help whenever you would like a quick command guide.
  ____________________________________________________________
  ____________________________________________________________
  Here comes another matter to keep track of:
    [T][ ] read chapter
  That makes 1 task awaiting your attention.
  ____________________________________________________________
  ____________________________________________________________
  That matter is already waiting to be done.
  There is nothing to reopen just yet.
  ____________________________________________________________
  ____________________________________________________________
  Very good! That is one matter settled:
    [T][X] read chapter
  ____________________________________________________________
  ____________________________________________________________
  That matter is already settled.
  Even diligence need not do the same work twice.
  ____________________________________________________________
  ____________________________________________________________
  Ah, it seems this matter is not quite settled:
    [T][ ] read chapter
  ____________________________________________________________
  ____________________________________________________________
  That matter is already waiting to be done.
  There is nothing to reopen just yet.
  ____________________________________________________________
  ____________________________________________________________
  Let us see what has found its way onto your list:
  1.[T][ ] read chapter
  ____________________________________________________________
  ____________________________________________________________
  Goodbye! May your plans prosper—and leave you a little leisure.
  ____________________________________________________________
  ```

### Delete tasks and retain the reindexed list
- Aim: Verify that delete removes the requested task, preserves the remaining task state, reindexes later tasks, and rejects invalid deletion requests.
- Command:
  ```sh
  mkdir -p _temp/ui-test-data && rm -f _temp/ui-test-data/current.txt && javac -d _temp/ui-test-classes $(find src/main/java -name '*.java' ! -path '*/gui/*') && java -Dlizzy.data.path=_temp/ui-test-data/current.txt -cp _temp/ui-test-classes lizzy.Lizzy
  ```
- Inputs:
  ```text
  delete 1
  todo read book
  deadline submit report /by 2026-08-28
  event project meeting /on 2026-08-06
  mark 3
  delete 2
  list
  delete 3
  delete first
  delete 1
  list
  bye
  ```
- Expected output:
  ```text
  ____________________________________________________________
      __    _
     / /   (_)_______  __  __
    / /   / /_  /_  / / / / /
   / /___/ / / /_/ /_/ /_/ /
  /_____/_/ /___/___/\__, /
                    /____/
  Hello! I'm Lizzy.
  What brings you here today?
  Type help whenever you would like a quick command guide.
  ____________________________________________________________
  ____________________________________________________________
  Ambitious—but there is nothing to delete while your list is empty.
  Add a task first, and then we shall have something to delete.
  ____________________________________________________________
  ____________________________________________________________
  Here comes another matter to keep track of:
    [T][ ] read book
  That makes 1 task awaiting your attention.
  ____________________________________________________________
  ____________________________________________________________
  A deadline, then. We'd better not keep it waiting.
    [D][ ] submit report (by: Aug 28 2026)
  That makes 2 tasks awaiting your attention.
  ____________________________________________________________
  ____________________________________________________________
  An engagement! I've added it to your list:
    [E][ ] project meeting (on: Aug 6 2026)
  That makes 3 tasks awaiting your attention.
  ____________________________________________________________
  ____________________________________________________________
  Very good! That is one matter settled:
    [E][X] project meeting (on: Aug 6 2026)
  ____________________________________________________________
  ____________________________________________________________
  And away it goes—one less matter on the list:
    [D][ ] submit report (by: Aug 28 2026)
  That makes 2 tasks awaiting your attention.
  ____________________________________________________________
  ____________________________________________________________
  Let us see what has found its way onto your list:
  1.[T][ ] read book
  2.[E][X] project meeting (on: Aug 6 2026)
  ____________________________________________________________
  ____________________________________________________________
  An imaginative choice—but that task is not on the present list.
  Choose a task number from 1 to 2.
  ____________________________________________________________
  ____________________________________________________________
  I'm afraid that will not quite do; I need a proper task number.
  Be precise: delete <task number>.
  ____________________________________________________________
  ____________________________________________________________
  And away it goes—one less matter on the list:
    [T][ ] read book
  That makes 1 task awaiting your attention.
  ____________________________________________________________
  ____________________________________________________________
  Let us see what has found its way onto your list:
  1.[E][X] project meeting (on: Aug 6 2026)
  ____________________________________________________________
  ____________________________________________________________
  Goodbye! May your plans prosper—and leave you a little leisure.
  ____________________________________________________________
  ```

### Accept flexible whitespace in dated commands
- Aim: Verify that extra spaces around command arguments and date markers are accepted, while deletion still leaves a correctly reindexed list.
- Command:
  ```sh
  mkdir -p _temp/ui-test-data && rm -f _temp/ui-test-data/current.txt && javac -d _temp/ui-test-classes $(find src/main/java -name '*.java' ! -path '*/gui/*') && java -Dlizzy.data.path=_temp/ui-test-data/current.txt -cp _temp/ui-test-classes lizzy.Lizzy
  ```
- Inputs:
  ```text
  todo   read book
  deadline   submit report    /by    2026-08-28
  event   project meeting   /from    2026-08-31   /to    2026-09-01
  delete 2
  list
  bye
  ```
- Expected output:
  ```text
  ____________________________________________________________
      __    _
     / /   (_)_______  __  __
    / /   / /_  /_  / / / / /
   / /___/ / / /_/ /_/ /_/ /
  /_____/_/ /___/___/\__, /
                    /____/
  Hello! I'm Lizzy.
  What brings you here today?
  Type help whenever you would like a quick command guide.
  ____________________________________________________________
  ____________________________________________________________
  Here comes another matter to keep track of:
    [T][ ] read book
  That makes 1 task awaiting your attention.
  ____________________________________________________________
  ____________________________________________________________
  A deadline, then. We'd better not keep it waiting.
    [D][ ] submit report (by: Aug 28 2026)
  That makes 2 tasks awaiting your attention.
  ____________________________________________________________
  ____________________________________________________________
  An engagement! I've added it to your list:
    [E][ ] project meeting (from: Aug 31 2026 to: Sep 1 2026)
  That makes 3 tasks awaiting your attention.
  ____________________________________________________________
  ____________________________________________________________
  And away it goes—one less matter on the list:
    [D][ ] submit report (by: Aug 28 2026)
  That makes 2 tasks awaiting your attention.
  ____________________________________________________________
  ____________________________________________________________
  Let us see what has found its way onto your list:
  1.[T][ ] read book
  2.[E][ ] project meeting (from: Aug 31 2026 to: Sep 1 2026)
  ____________________________________________________________
  ____________________________________________________________
  Goodbye! May your plans prosper—and leave you a little leisure.
  ____________________________________________________________
  ```

### Save task changes to disk
- Aim: Verify that adding and marking a task rewrites the data file with its latest state.
- Command:
  ```sh
  mkdir -p _temp/ui-test-data && rm -f _temp/ui-test-data/current.txt && javac -d _temp/ui-test-classes $(find src/main/java -name '*.java' ! -path '*/gui/*') && java -Dlizzy.data.path=_temp/ui-test-data/current.txt -cp _temp/ui-test-classes lizzy.Lizzy && sed -n '1,$p' _temp/ui-test-data/current.txt
  ```
- Inputs:
  ```text
  todo read book
  mark 1
  bye
  ```
- Expected output:
  ```text
  ____________________________________________________________
      __    _
     / /   (_)_______  __  __
    / /   / /_  /_  / / / / /
   / /___/ / / /_/ /_/ /_/ /
  /_____/_/ /___/___/\__, /
                    /____/
  Hello! I'm Lizzy.
  What brings you here today?
  Type help whenever you would like a quick command guide.
  ____________________________________________________________
  ____________________________________________________________
  Here comes another matter to keep track of:
    [T][ ] read book
  That makes 1 task awaiting your attention.
  ____________________________________________________________
  ____________________________________________________________
  Very good! That is one matter settled:
    [T][X] read book
  ____________________________________________________________
  ____________________________________________________________
  Goodbye! May your plans prosper—and leave you a little leisure.
  ____________________________________________________________
  T | 1 | cmVhZCBib29r
  ```

### Load saved tasks after restarting
- Aim: Verify that todos, deadlines, events, and completion state survive a restart.
- Command:
  ```sh
  mkdir -p _temp/ui-test-data && rm -f _temp/ui-test-data/current.txt && javac -d _temp/ui-test-classes $(find src/main/java -name '*.java' ! -path '*/gui/*') && printf 'todo read book\ndeadline return book /by 2026-06-06\nevent project meeting /on 2026-08-06\nmark 1\nbye\n' | java -Dlizzy.data.path=_temp/ui-test-data/current.txt -cp _temp/ui-test-classes lizzy.Lizzy >/dev/null && java -Dlizzy.data.path=_temp/ui-test-data/current.txt -cp _temp/ui-test-classes lizzy.Lizzy
  ```
- Inputs:
  ```text
  list
  bye
  ```
- Expected output:
  ```text
  ____________________________________________________________
      __    _
     / /   (_)_______  __  __
    / /   / /_  /_  / / / / /
   / /___/ / / /_/ /_/ /_/ /
  /_____/_/ /___/___/\__, /
                    /____/
  Hello! I'm Lizzy.
  What brings you here today?
  Type help whenever you would like a quick command guide.
  ____________________________________________________________
  ____________________________________________________________
  Let us see what has found its way onto your list:
  1.[T][X] read book
  2.[D][ ] return book (by: Jun 6 2026)
  3.[E][ ] project meeting (on: Aug 6 2026)
  ____________________________________________________________
  ____________________________________________________________
  Goodbye! May your plans prosper—and leave you a little leisure.
  ____________________________________________________________
  ```

### Add single-day and timed events
- Aim: Verify that same-day date ranges become one-day events, valid times are displayed clearly, and reversed or zero-length time ranges are rejected.
- Command:
  ```sh
  mkdir -p _temp/ui-test-data && rm -f _temp/ui-test-data/current.txt && javac -d _temp/ui-test-classes $(find src/main/java -name '*.java' ! -path '*/gui/*') && java -Dlizzy.data.path=_temp/ui-test-data/current.txt -cp _temp/ui-test-classes lizzy.Lizzy
  ```
- Inputs:
  ```text
  event team meeting /on 2026-09-17
  event study group /on 2026-09-18 /from 14:00 /to 16:30
  event invalid meeting /on 2026-09-18 /from 16:30 /to 14:00
  event zero-length meeting /on 2026-09-18 /from 14:00 /to 14:00
  event same-day range /from 2026-09-18 /to 2026-09-18
  event impossible date /on 2026-02-30
  event mysterious time /on 2026-09-18 /from 2pm /to 4pm
  event invalid time /on 2026-09-18 /from 25:00 /to 26:00
  on 2026-09-18
  list
  bye
  ```
- Expected output:
  ```text
  ____________________________________________________________
      __    _
     / /   (_)_______  __  __
    / /   / /_  /_  / / / / /
   / /___/ / / /_/ /_/ /_/ /
  /_____/_/ /___/___/\__, /
                    /____/
  Hello! I'm Lizzy.
  What brings you here today?
  Type help whenever you would like a quick command guide.
  ____________________________________________________________
  ____________________________________________________________
  An engagement! I've added it to your list:
    [E][ ] team meeting (on: Sep 17 2026)
  That makes 1 task awaiting your attention.
  ____________________________________________________________
  ____________________________________________________________
  An engagement! I've added it to your list:
    [E][ ] study group (on: Sep 18 2026, 14:00 to 16:30)
  That makes 2 tasks awaiting your attention.
  ____________________________________________________________
  ____________________________________________________________
  That event ends before it begins—a trick even time will not oblige.
  Choose an end time later than the start time.
  ____________________________________________________________
  ____________________________________________________________
  An event that begins and ends at the same moment scarcely has time to occur.
  Choose an end time later than the start time.
  ____________________________________________________________
  ____________________________________________________________
  An engagement! I've added it to your list:
    [E][ ] same-day range (on: Sep 18 2026)
  That makes 3 tasks awaiting your attention.
  ____________________________________________________________
  ____________________________________________________________
  That date is admirably imaginative, but the calendar refuses to acknowledge it.
  Choose a date that actually exists.
  ____________________________________________________________
  ____________________________________________________________
  That time format is a little too mysterious for me to parse.
  Please use HH:mm—for example, 14:30.
  ____________________________________________________________
  ____________________________________________________________
  That time asks rather more of the clock than it can provide.
  Choose a time from 00:00 to 23:59.
  ____________________________________________________________
  ____________________________________________________________
  On Sep 18 2026, your schedule has the following in store:
  2.[E][ ] study group (on: Sep 18 2026, 14:00 to 16:30)
  3.[E][ ] same-day range (on: Sep 18 2026)
  ____________________________________________________________
  ____________________________________________________________
  Let us see what has found its way onto your list:
  1.[E][ ] team meeting (on: Sep 17 2026)
  2.[E][ ] study group (on: Sep 18 2026, 14:00 to 16:30)
  3.[E][ ] same-day range (on: Sep 18 2026)
  ____________________________________________________________
  ____________________________________________________________
  Goodbye! May your plans prosper—and leave you a little leisure.
  ____________________________________________________________
  ```

### Create a missing data folder
- Aim: Verify that the first task change creates both a missing parent folder and its data file.
- Command:
  ```sh
  rm -rf _temp/ui-test-data/missing-parent && javac -d _temp/ui-test-classes $(find src/main/java -name '*.java' ! -path '*/gui/*') && java -Dlizzy.data.path=_temp/ui-test-data/missing-parent/nested/lizzy.txt -cp _temp/ui-test-classes lizzy.Lizzy && test -f _temp/ui-test-data/missing-parent/nested/lizzy.txt && printf 'Data file created.\n'
  ```
- Inputs:
  ```text
  todo first run
  bye
  ```
- Expected output:
  ```text
  ____________________________________________________________
      __    _
     / /   (_)_______  __  __
    / /   / /_  /_  / / / / /
   / /___/ / / /_/ /_/ /_/ /
  /_____/_/ /___/___/\__, /
                    /____/
  Hello! I'm Lizzy.
  What brings you here today?
  Type help whenever you would like a quick command guide.
  ____________________________________________________________
  ____________________________________________________________
  Here comes another matter to keep track of:
    [T][ ] first run
  That makes 1 task awaiting your attention.
  ____________________________________________________________
  ____________________________________________________________
  Goodbye! May your plans prosper—and leave you a little leisure.
  ____________________________________________________________
  Data file created.
  ```

### Recover from an invalid date in saved data
- Aim: Verify that Lizzy explains a non-existent saved calendar date and continues with an empty task list.
- Command:
  ```sh
  mkdir -p _temp/ui-test-data && printf 'D | 0 | c3VibWl0IHJlcG9ydA== | MjAyNi0wMi0zMA==\n' > _temp/ui-test-data/current.txt && javac -d _temp/ui-test-classes $(find src/main/java -name '*.java' ! -path '*/gui/*') && java -Dlizzy.data.path=_temp/ui-test-data/current.txt -cp _temp/ui-test-classes lizzy.Lizzy
  ```
- Inputs:
  ```text
  list
  bye
  ```
- Expected output:
  ```text
  ____________________________________________________________
      __    _
     / /   (_)_______  __  __
    / /   / /_  /_  / / / / /
   / /___/ / / /_/ /_/ /_/ /
  /_____/_/ /___/___/\__, /
                    /____/
  Hello! I'm Lizzy.
  What brings you here today?
  Type help whenever you would like a quick command guide.
  ____________________________________________________________
  I couldn't understand the saved task data at line 1.
  Starting with an empty task list for this session.
  ____________________________________________________________
  ____________________________________________________________
  Your list is blissfully free of obligations.
  ____________________________________________________________
  ____________________________________________________________
  Goodbye! May your plans prosper—and leave you a little leisure.
  ____________________________________________________________
  ```

### Continue after a save failure
- Aim: Verify that a file-system error is explained without ending the session or losing the in-memory task.
- Command:
  ```sh
  mkdir -p _temp/ui-test-data && rm -f _temp/ui-test-data/not-a-folder && printf 'block' > _temp/ui-test-data/not-a-folder && javac -d _temp/ui-test-classes $(find src/main/java -name '*.java' ! -path '*/gui/*') && java -Dlizzy.data.path=_temp/ui-test-data/not-a-folder/lizzy.txt -cp _temp/ui-test-classes lizzy.Lizzy
  ```
- Inputs:
  ```text
  todo keep working
  list
  bye
  ```
- Expected output:
  ```text
  ____________________________________________________________
      __    _
     / /   (_)_______  __  __
    / /   / /_  /_  / / / / /
   / /___/ / / /_/ /_/ /_/ /
  /_____/_/ /___/___/\__, /
                    /____/
  Hello! I'm Lizzy.
  What brings you here today?
  Type help whenever you would like a quick command guide.
  ____________________________________________________________
  ____________________________________________________________
  I updated your task list, but couldn't save it to _temp/ui-test-data/not-a-folder/lizzy.txt.
  ____________________________________________________________
  ____________________________________________________________
  Let us see what has found its way onto your list:
  1.[T][ ] keep working
  ____________________________________________________________
  ____________________________________________________________
  Goodbye! May your plans prosper—and leave you a little leisure.
  ____________________________________________________________
  ```

### Parse and format calendar dates
- Aim: Verify strict ISO date parsing, calendar validation, event ordering, and readable output formatting.
- Command:
  ```sh
  mkdir -p _temp/ui-test-data && rm -f _temp/ui-test-data/current.txt && javac -d _temp/ui-test-classes $(find src/main/java -name '*.java' ! -path '*/gui/*') && java -Dlizzy.data.path=_temp/ui-test-data/current.txt -cp _temp/ui-test-classes lizzy.Lizzy
  ```
- Inputs:
  ```text
  deadline return book /by 2/12/2019
  deadline leap mistake /by 2019-02-29
  event backwards /from 2019-12-03 /to 2019-12-02
  deadline return book /by 2019-12-02
  event workshop /from 2019-12-02 /to 2019-12-03
  list
  bye
  ```
- Expected output:
  ```text
  ____________________________________________________________
      __    _
     / /   (_)_______  __  __
    / /   / /_  /_  / / / / /
   / /___/ / / /_/ /_/ /_/ /
  /_____/_/ /___/___/\__, /
                    /____/
  Hello! I'm Lizzy.
  What brings you here today?
  Type help whenever you would like a quick command guide.
  ____________________________________________________________
  ____________________________________________________________
  That date format is a little too mysterious for me to parse.
  Please use yyyy-MM-dd—for example, 2026-09-10.
  ____________________________________________________________
  ____________________________________________________________
  That date is admirably imaginative, but the calendar refuses to acknowledge it.
  Choose a date that actually exists.
  ____________________________________________________________
  ____________________________________________________________
  An event cannot end before it begins.
  Let time keep its proper order: choose an end date after the start date.
  ____________________________________________________________
  ____________________________________________________________
  A deadline, then. We'd better not keep it waiting.
    [D][ ] return book (by: Dec 2 2019)
  That makes 1 task awaiting your attention.
  ____________________________________________________________
  ____________________________________________________________
  An engagement! I've added it to your list:
    [E][ ] workshop (from: Dec 2 2019 to: Dec 3 2019)
  That makes 2 tasks awaiting your attention.
  ____________________________________________________________
  ____________________________________________________________
  Let us see what has found its way onto your list:
  1.[D][ ] return book (by: Dec 2 2019)
  2.[E][ ] workshop (from: Dec 2 2019 to: Dec 3 2019)
  ____________________________________________________________
  ____________________________________________________________
  Goodbye! May your plans prosper—and leave you a little leisure.
  ____________________________________________________________
  ```

### Find dated tasks on a date
- Aim: Verify date-based lookup for deadlines and inclusive event ranges, while excluding todos and retaining original task numbers.
- Command:
  ```sh
  mkdir -p _temp/ui-test-data && rm -f _temp/ui-test-data/current.txt && javac -d _temp/ui-test-classes $(find src/main/java -name '*.java' ! -path '*/gui/*') && java -Dlizzy.data.path=_temp/ui-test-data/current.txt -cp _temp/ui-test-classes lizzy.Lizzy
  ```
- Inputs:
  ```text
  todo buy milk
  deadline submit report /by 2026-09-01
  event conference /from 2026-09-01 /to 2026-09-03
  deadline pay bill /by 2026-09-04
  on
  on 09/01/2026
  on 2026-09-01
  on 2026-09-02
  on 2026-09-05
  bye
  ```
- Expected output:
  ```text
  ____________________________________________________________
      __    _
     / /   (_)_______  __  __
    / /   / /_  /_  / / / / /
   / /___/ / / /_/ /_/ /_/ /
  /_____/_/ /___/___/\__, /
                    /____/
  Hello! I'm Lizzy.
  What brings you here today?
  Type help whenever you would like a quick command guide.
  ____________________________________________________________
  ____________________________________________________________
  Here comes another matter to keep track of:
    [T][ ] buy milk
  That makes 1 task awaiting your attention.
  ____________________________________________________________
  ____________________________________________________________
  A deadline, then. We'd better not keep it waiting.
    [D][ ] submit report (by: Sep 1 2026)
  That makes 2 tasks awaiting your attention.
  ____________________________________________________________
  ____________________________________________________________
  An engagement! I've added it to your list:
    [E][ ] conference (from: Sep 1 2026 to: Sep 3 2026)
  That makes 3 tasks awaiting your attention.
  ____________________________________________________________
  ____________________________________________________________
  A deadline, then. We'd better not keep it waiting.
    [D][ ] pay bill (by: Sep 4 2026)
  That makes 4 tasks awaiting your attention.
  ____________________________________________________________
  ____________________________________________________________
  A date is needed to consult the schedule.
  Name the day like this: on <yyyy-MM-dd>.
  ____________________________________________________________
  ____________________________________________________________
  That date format is a little too mysterious for me to parse.
  Please use yyyy-MM-dd—for example, 2026-09-10.
  ____________________________________________________________
  ____________________________________________________________
  On Sep 1 2026, your schedule has the following in store:
  2.[D][ ] submit report (by: Sep 1 2026)
  3.[E][ ] conference (from: Sep 1 2026 to: Sep 3 2026)
  ____________________________________________________________
  ____________________________________________________________
  On Sep 2 2026, your schedule has the following in store:
  3.[E][ ] conference (from: Sep 1 2026 to: Sep 3 2026)
  ____________________________________________________________
  ____________________________________________________________
  Sep 5 2026 appears to make no demands upon you.
  ____________________________________________________________
  ____________________________________________________________
  Goodbye! May your plans prosper—and leave you a little leisure.
  ____________________________________________________________
  ```

### Add and view a task within a date period
- Aim: Verify that a period task accepts inclusive dates, appears on each date in its period, is saved in the list, and rejects a reversed period.
- Command:
  ```sh
  mkdir -p _temp/ui-test-data && rm -f _temp/ui-test-data/current.txt && javac -d _temp/ui-test-classes $(find src/main/java -name '*.java' ! -path '*/gui/*') && java -Dlizzy.data.path=_temp/ui-test-data/current.txt -cp _temp/ui-test-classes lizzy.Lizzy
  ```
- Inputs:
  ```text
  within collect certificate /from 2026-09-10 /to 2026-09-12
  within invalid period /from 2026-09-12 /to 2026-09-10
  on 2026-09-10
  on 2026-09-11
  on 2026-09-13
  list
  bye
  ```
- Expected output:
  ```text
  ____________________________________________________________
      __    _
     / /   (_)_______  __  __
    / /   / /_  /_  / / / / /
   / /___/ / / /_/ /_/ /_/ /
  /_____/_/ /___/___/\__, /
                    /____/
  Hello! I'm Lizzy.
  What brings you here today?
  Type help whenever you would like a quick command guide.
  ____________________________________________________________
  ____________________________________________________________
  A period to work within—I've added it to your list:
    [W][ ] collect certificate (within: Sep 10 2026 to: Sep 12 2026)
  That makes 1 task awaiting your attention.
  ____________________________________________________________
  ____________________________________________________________
  A completion period cannot end before it begins.
  Keep the interval sensible: choose an end date on or after the start date.
  ____________________________________________________________
  ____________________________________________________________
  On Sep 10 2026, your schedule has the following in store:
  1.[W][ ] collect certificate (within: Sep 10 2026 to: Sep 12 2026)
  ____________________________________________________________
  ____________________________________________________________
  On Sep 11 2026, your schedule has the following in store:
  1.[W][ ] collect certificate (within: Sep 10 2026 to: Sep 12 2026)
  ____________________________________________________________
  ____________________________________________________________
  Sep 13 2026 appears to make no demands upon you.
  ____________________________________________________________
  ____________________________________________________________
  Let us see what has found its way onto your list:
  1.[W][ ] collect certificate (within: Sep 10 2026 to: Sep 12 2026)
  ____________________________________________________________
  ____________________________________________________________
  Goodbye! May your plans prosper—and leave you a little leisure.
  ____________________________________________________________
  ```

### Find tasks by keyword
- Aim: Verify that partial, case-insensitive description search finds matching task types in original list order, retains task numbers and completion state, and handles no or missing keywords.
- Command:
  ```sh
  mkdir -p _temp/ui-test-data && rm -f _temp/ui-test-data/current.txt && javac -d _temp/ui-test-classes $(find src/main/java -name '*.java' ! -path '*/gui/*') && java -Dlizzy.data.path=_temp/ui-test-data/current.txt -cp _temp/ui-test-classes lizzy.Lizzy
  ```
- Inputs:
  ```text
  todo read book
  deadline return book /by 2026-09-06
  todo buy milk
  mark 1
  mark 2
  find OOK
  find pen
  find
  bye
  ```
- Expected output:
  ```text
  ____________________________________________________________
      __    _
     / /   (_)_______  __  __
    / /   / /_  /_  / / / / /
   / /___/ / / /_/ /_/ /_/ /
  /_____/_/ /___/___/\__, /
                    /____/
  Hello! I'm Lizzy.
  What brings you here today?
  Type help whenever you would like a quick command guide.
  ____________________________________________________________
  ____________________________________________________________
  Here comes another matter to keep track of:
    [T][ ] read book
  That makes 1 task awaiting your attention.
  ____________________________________________________________
  ____________________________________________________________
  A deadline, then. We'd better not keep it waiting.
    [D][ ] return book (by: Sep 6 2026)
  That makes 2 tasks awaiting your attention.
  ____________________________________________________________
  ____________________________________________________________
  Here comes another matter to keep track of:
    [T][ ] buy milk
  That makes 3 tasks awaiting your attention.
  ____________________________________________________________
  ____________________________________________________________
  Very good! That is one matter settled:
    [T][X] read book
  ____________________________________________________________
  ____________________________________________________________
  Very good! That is one matter settled:
    [D][X] return book (by: Sep 6 2026)
  ____________________________________________________________
  ____________________________________________________________
  These tasks answer to your search:
  1.[T][X] read book
  2.[D][X] return book (by: Sep 6 2026)
  ____________________________________________________________
  ____________________________________________________________
  Not a single task answers to that description.
  ____________________________________________________________
  ____________________________________________________________
  A keyword is needed to find a task.
  Give me something to seek: find <keyword>.
  ____________________________________________________________
  ____________________________________________________________
  Goodbye! May your plans prosper—and leave you a little leisure.
  ____________________________________________________________
  ```
