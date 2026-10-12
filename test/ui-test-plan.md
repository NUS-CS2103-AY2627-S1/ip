# UI Test Plan

## Test environment

- Java version: 25
- Compile command: `javac -d _temp/ui-test-classes src/main/java/bogos/Bogos.java src/main/java/bogos/BogosException.java src/main/java/bogos/Deadline.java src/main/java/bogos/Event.java src/main/java/bogos/Parser.java src/main/java/bogos/Storage.java src/main/java/bogos/Task.java src/main/java/bogos/TaskList.java src/main/java/bogos/TaskType.java src/main/java/bogos/Todo.java src/main/java/bogos/Ui.java`
- Run command: `java -ea -cp _temp/ui-test-classes bogos.Bogos`

Run the planned sessions from the repository root with:

```powershell
python .codex/skills/test-ui/scripts/run_ui_tests.py
```

Before running the full plan, delete `data/bogos.txt` so the first session starts
with no saved tasks. The test cases run in the listed order because later sessions
verify tasks saved by earlier sessions.

## Test case: Show command help

### Aim

Verify that `help` displays every supported command and the documentation link.

### Inputs

```text
help
bye
```

### Expected output

```text
      ___             __ _
     | _ )    ___    / _` |   ___     ___
     | _ \   / _ \   \__, |  / _ \   (_-<
     |___/   \___/   |___/   \___/   /__/_
   _|"""""|_|"""""|_|"""""|_|"""""|_|"""""|
   "`-0-0-'"`-0-0-'"`-0-0-'"`-0-0-'"`-0-0-'
____________________________________________________________
Blessings! Bogos beckons. Bring Bogos business? :]
____________________________________________________________
         Bogos' basic business:

           todo DESCRIPTION [#TAG]...
           deadline DESCRIPTION /by YYYY-MM-DD [#TAG]...
           event DESCRIPTION /from YYYY-MM-DD /to YYYY-MM-DD [#TAG]...

           list
           find KEYWORD
           mark NUMBER
           unmark NUMBER
           delete NUMBER
           help
           bye

         Browse beyond basics: https://oofky.github.io/ip/
____________________________________________________________
____________________________________________________________
         Bye bye! :]
____________________________________________________________
```

## Test case: List an empty task list

### Aim

Verify that listing before any task is added gives the expected empty-list error.

### Inputs

```text
list
bye
```

### Expected output

```text
      ___             __ _
     | _ )    ___    / _` |   ___     ___
     | _ \   / _ \   \__, |  / _ \   (_-<
     |___/   \___/   |___/   \___/   /__/_
   _|"""""|_|"""""|_|"""""|_|"""""|_|"""""|
   "`-0-0-'"`-0-0-'"`-0-0-'"`-0-0-'"`-0-0-'
____________________________________________________________
Blessings! Bogos beckons. Bring Bogos business? :]
____________________________________________________________
         But board be blank...
____________________________________________________________
____________________________________________________________
         Bye bye! :]
____________________________________________________________
```

## Test case: Add and list task types

### Aim

Verify that whitespace is normalized for task creation, all task types are added
with their type-specific details, and duplicate tasks are rejected.

### Inputs

```text
  todo   borrow   book   #reading   #Fun
deadline return book /by 2026-09-05
event project meeting /from 2026-09-05 /to 2026-09-06
list
find book
find coffee
todo borrow book #Fun #reading
bye
```

### Expected output

```text
      ___             __ _
     | _ )    ___    / _` |   ___     ___
     | _ \   / _ \   \__, |  / _ \   (_-<
     |___/   \___/   |___/   \___/   /__/_
   _|"""""|_|"""""|_|"""""|_|"""""|_|"""""|
   "`-0-0-'"`-0-0-'"`-0-0-'"`-0-0-'"`-0-0-'
____________________________________________________________
Blessings! Bogos beckons. Bring Bogos business? :]
____________________________________________________________
         Boom! Bullet born: 
           [T][ ] borrow book #reading #Fun
         1 bullet(s) being.
____________________________________________________________
____________________________________________________________
         Boom! Bullet born: 
           [D][ ] return book (by: Sep 05 2026)
         2 bullet(s) being.
____________________________________________________________
____________________________________________________________
         Boom! Bullet born: 
           [E][ ] project meeting (from: Sep 05 2026 to: Sep 06 2026)
         3 bullet(s) being.
____________________________________________________________
____________________________________________________________
         Behold bulleted board:
         1.[T][ ] borrow book #reading #Fun
         2.[D][ ] return book (by: Sep 05 2026)
         3.[E][ ] project meeting (from: Sep 05 2026 to: Sep 06 2026)
____________________________________________________________
____________________________________________________________
         Bogos brings befitting bullets:
         1.[T][ ] borrow book #reading #Fun
         2.[D][ ] return book (by: Sep 05 2026)
____________________________________________________________
____________________________________________________________
         Bogos' beams bounced back blank. :[
____________________________________________________________
____________________________________________________________
         Bummer, buplicate bullet. :[
____________________________________________________________
____________________________________________________________
         Bye bye! :]
____________________________________________________________
```

## Test case: Reject invalid task tags

### Aim

Verify that blank, duplicate, description-only, and non-final tag commands are
rejected without creating tasks.

### Inputs

```text
todo watch movie #
todo watch movie #fun #fun
todo #fun #weekend
todo spacetag #two words
bye
```

### Expected output

```text
      ___             __ _
     | _ )    ___    / _` |   ___     ___
     | _ \   / _ \   \__, |  / _ \   (_-<
     |___/   \___/   |___/   \___/   /__/_
   _|"""""|_|"""""|_|"""""|_|"""""|_|"""""|
   "`-0-0-'"`-0-0-'"`-0-0-'"`-0-0-'"`-0-0-'
____________________________________________________________
Blessings! Bogos beckons. Bring Bogos business? :]
____________________________________________________________
         Bogus blank #badge. :[
____________________________________________________________
____________________________________________________________
         Bummer, buplicate #badge. :[
____________________________________________________________
____________________________________________________________
         Bwhere body? Be: todo DESCRIPTION [#TAG]...
____________________________________________________________
____________________________________________________________
         Bah! Unidentified Foreign Object: words
____________________________________________________________
____________________________________________________________
         Bye bye! :]
____________________________________________________________
```

## Test case: Mark, unmark, and delete tasks

### Aim

Verify that a task can be marked, unmarked, and deleted, and that the saved task list reflects each change.

### Inputs

```text
mark 1
unmark 1
delete 2
list
bye
```

### Expected output

```text
      ___             __ _
     | _ )    ___    / _` |   ___     ___
     | _ \   / _ \   \__, |  / _ \   (_-<
     |___/   \___/   |___/   \___/   /__/_
   _|"""""|_|"""""|_|"""""|_|"""""|_|"""""|
   "`-0-0-'"`-0-0-'"`-0-0-'"`-0-0-'"`-0-0-'
____________________________________________________________
Blessings! Bogos beckons. Bring Bogos business? :]
____________________________________________________________
         Bravo! Bogos boxed bullet:
           [T][X] borrow book #reading #Fun
____________________________________________________________
____________________________________________________________
         Bet! Bogos blanked box:
           [T][ ] borrow book #reading #Fun
____________________________________________________________
____________________________________________________________
         Brilliant! Bye bye bullet:
           [D][ ] return book (by: Sep 05 2026)
         2 bullet(s) being.
____________________________________________________________
____________________________________________________________
         Behold bulleted board:
         1.[T][ ] borrow book #reading #Fun
         2.[E][ ] project meeting (from: Sep 05 2026 to: Sep 06 2026)
____________________________________________________________
____________________________________________________________
         Bye bye! :]
____________________________________________________________
```

## Test case: Exit immediately

### Aim

Verify that the application exits cleanly when given the `bye` command.

### Inputs

```text
bye
```

### Expected output

```text
      ___             __ _
     | _ )    ___    / _` |   ___     ___
     | _ \   / _ \   \__, |  / _ \   (_-<
     |___/   \___/   |___/   \___/   /__/_
   _|"""""|_|"""""|_|"""""|_|"""""|_|"""""|
   "`-0-0-'"`-0-0-'"`-0-0-'"`-0-0-'"`-0-0-'
____________________________________________________________
Blessings! Bogos beckons. Bring Bogos business? :]
____________________________________________________________
         Bye bye! :]
____________________________________________________________
```

## Test case: Find with no matching tasks

### Aim

Verify that an unsuccessful search reports the dedicated no-results message.

### Inputs

```text
find zxqvnoresult
bye
```

### Expected output

```text
      ___             __ _
     | _ )    ___    / _` |   ___     ___
     | _ \   / _ \   \__, |  / _ \   (_-<
     |___/   \___/   |___/   \___/   /__/_
   _|"""""|_|"""""|_|"""""|_|"""""|_|"""""|
   "`-0-0-'"`-0-0-'"`-0-0-'"`-0-0-'"`-0-0-'
____________________________________________________________
Blessings! Bogos beckons. Bring Bogos business? :]
____________________________________________________________
         Bogos' beams bounced back blank. :[
____________________________________________________________
____________________________________________________________
         Bye bye! :]
____________________________________________________________
```

## Test case: Explain invalid commands

### Aim

Verify that unknown commands and bare commands provide the help command or the
required command format.

### Inputs

```text
yeet
todo
find
mark
unmark
delete
deadline
event
bye
```

### Expected output

```text
      ___             __ _
     | _ )    ___    / _` |   ___     ___
     | _ \   / _ \   \__, |  / _ \   (_-<
     |___/   \___/   |___/   \___/   /__/_
   _|"""""|_|"""""|_|"""""|_|"""""|_|"""""|
   "`-0-0-'"`-0-0-'"`-0-0-'"`-0-0-'"`-0-0-'
____________________________________________________________
Blessings! Bogos beckons. Bring Bogos business? :]
____________________________________________________________
         Bwhat? Best browse: help
____________________________________________________________
____________________________________________________________
         Bwhere body? Be: todo DESCRIPTION [#TAG]...
____________________________________________________________
____________________________________________________________
         Bwhere buzzword? Be: find KEYWORD
____________________________________________________________
____________________________________________________________
         Bwhere base-ten? Be: mark NUMBER
____________________________________________________________
____________________________________________________________
         Bwhere base-ten? Be: unmark NUMBER
____________________________________________________________
____________________________________________________________
         Bwhere base-ten? Be: delete NUMBER
____________________________________________________________
____________________________________________________________
         Bwhere body? Be: deadline DESCRIPTION /by YYYY-MM-DD [#TAG]...
____________________________________________________________
____________________________________________________________
         Bwhere body? Be: event DESCRIPTION /from YYYY-MM-DD /to YYYY-MM-DD [#TAG]...
____________________________________________________________
____________________________________________________________
         Bye bye! :]
____________________________________________________________
```

## Test case: Reject repeated date parameters

### Aim

Verify that commands with repeated date parameters and reversed event dates are
rejected, while same-day events are accepted.

### Inputs

```text
deadline submit report /by 2026-09-15 /by 2026-09-16
event project meeting /from 2026-09-15 /from 2026-09-16 /to 2026-09-17
event project meeting /from 2026-09-15 /to 2026-09-16 /to 2026-09-17
event same day /from 2026-09-17 /to 2026-09-17
event backwards /from 2026-09-18 /to 2026-09-17
bye
```

### Expected output

```text
      ___             __ _
     | _ )    ___    / _` |   ___     ___
     | _ \   / _ \   \__, |  / _ \   (_-<
     |___/   \___/   |___/   \___/   /__/_
   _|"""""|_|"""""|_|"""""|_|"""""|_|"""""|
   "`-0-0-'"`-0-0-'"`-0-0-'"`-0-0-'"`-0-0-'
____________________________________________________________
Blessings! Bogos beckons. Bring Bogos business? :]
____________________________________________________________
         Bummer, buplicate /by. :[
____________________________________________________________
____________________________________________________________
         Bummer, buplicate /from. :[
____________________________________________________________
____________________________________________________________
         Bummer, buplicate /to. :[
____________________________________________________________
____________________________________________________________
         Boom! Bullet born: 
           [E][ ] same day (from: Sep 17 2026 to: Sep 17 2026)
         3 bullet(s) being.
____________________________________________________________
____________________________________________________________
         Bro be breathing backwards??
____________________________________________________________
____________________________________________________________
         Bye bye! :]
____________________________________________________________
```

## Test case: Explain malformed task details

### Aim

Verify that missing task descriptions take priority and that missing and blank
parameters, impossible calendar dates, and trailing text receive specific guidance.

### Inputs

```text
deadline /by 2026-09-15
deadline /by nonsense
event /from 2026-05-12 /to 2026-05-13
deadline submit report
event project meeting /to 2026-09-16
event project meeting /from 2026-09-15
deadline submit report /by
event project meeting /from /to 2026-09-16
event project meeting /from 2026-09-15 /to
deadline invalid /by 2026-02-30
event hike /from 2026-05-12/to 2026-04-10
deadline bridge /by 2026-02-02 bridge
bye
```

### Expected output

```text
      ___             __ _
     | _ )    ___    / _` |   ___     ___
     | _ \   / _ \   \__, |  / _ \   (_-<
     |___/   \___/   |___/   \___/   /__/_
   _|"""""|_|"""""|_|"""""|_|"""""|_|"""""|
   "`-0-0-'"`-0-0-'"`-0-0-'"`-0-0-'"`-0-0-'
____________________________________________________________
Blessings! Bogos beckons. Bring Bogos business? :]
____________________________________________________________
         Bwhere body? Be: deadline DESCRIPTION /by YYYY-MM-DD [#TAG]...
____________________________________________________________
____________________________________________________________
         Bwhere body? Be: deadline DESCRIPTION /by YYYY-MM-DD [#TAG]...
____________________________________________________________
____________________________________________________________
         Bwhere body? Be: event DESCRIPTION /from YYYY-MM-DD /to YYYY-MM-DD [#TAG]...
____________________________________________________________
____________________________________________________________
         Bwhere /by? Be: deadline DESCRIPTION /by YYYY-MM-DD [#TAG]...
____________________________________________________________
____________________________________________________________
         Bwhere /from? Be: event DESCRIPTION /from YYYY-MM-DD /to YYYY-MM-DD [#TAG]...
____________________________________________________________
____________________________________________________________
         Bwhere /to? Be: event DESCRIPTION /from YYYY-MM-DD /to YYYY-MM-DD [#TAG]...
____________________________________________________________
____________________________________________________________
         Bogus blank /by. :[
____________________________________________________________
____________________________________________________________
         Bogus blank /from. :[
____________________________________________________________
____________________________________________________________
         Bogus blank /to. :[
____________________________________________________________
____________________________________________________________
         Bogus. Bring Bogos bona-fide YYYY-MM-DD. :[
____________________________________________________________
____________________________________________________________
         Bogus. Bring Bogos bona-fide YYYY-MM-DD. :[
____________________________________________________________
____________________________________________________________
         Bah! Unidentified Foreign Object: bridge
____________________________________________________________
____________________________________________________________
         Bye bye! :]
____________________________________________________________
```

## Test case: Explain misplaced and reordered date parameters

### Aim

Verify that event date markers can be reordered, reversed dates are still
reported accurately, and task-type-specific date markers receive clear errors.

### Inputs

```text
event reordered markers /to 2026-11-02 /from 2026-11-01
event backwards markers /to 2026-11-01 /from 2026-11-02
deadline submit report /by 2026-11-01 /from 2026-11-02
event submit report /from 2026-11-01 /by 2026-11-02 /to 2026-11-03
bye
```

### Expected output

```text
      ___             __ _
     | _ )    ___    / _` |   ___     ___
     | _ \   / _ \   \__, |  / _ \   (_-<
     |___/   \___/   |___/   \___/   /__/_
   _|"""""|_|"""""|_|"""""|_|"""""|_|"""""|
   "`-0-0-'"`-0-0-'"`-0-0-'"`-0-0-'"`-0-0-'
____________________________________________________________
Blessings! Bogos beckons. Bring Bogos business? :]
____________________________________________________________
         Boom! Bullet born: 
           [E][ ] reordered markers (from: Nov 01 2026 to: Nov 02 2026)
         4 bullet(s) being.
____________________________________________________________
____________________________________________________________
         Bro be breathing backwards??
____________________________________________________________
____________________________________________________________
         Bwhere /by? Be: deadline DESCRIPTION /by YYYY-MM-DD [#TAG]...
____________________________________________________________
____________________________________________________________
         Bwhere /from? Be: event DESCRIPTION /from YYYY-MM-DD /to YYYY-MM-DD [#TAG]...
____________________________________________________________
____________________________________________________________
         Bye bye! :]
____________________________________________________________
```
