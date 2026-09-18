# UI Test Plan

Test cases for `Lune`. Each test case is run as its own fresh `java Lune`
process; the **Input** lines are piped to stdin one per line, and the
program's full console output (stdout) is compared against **Expected
output** verbatim. Run with the `test-ui` skill.

## Test Case 1: Greet and exit

**Aim:** Verify the startup banner/greeting print, and that `bye` exits with the farewell message.

**Input:**
```input
bye
```

**Expected output:**
```expected
 _                     
| |   _   _ _ __   ___ 
| |  | | | | '_ \ / _ \
| |__| |_| | | | |  __/
|_____\__,_|_| |_|\___|

    ____________________________________________________________
     Oh, hey — I'm Lune.
     What's rattling around in that head of yours?
    ____________________________________________________________

    ____________________________________________________________
     Bye! Go forth and be marginally more organized.
    ____________________________________________________________
```

## Test Case 2: Add a todo and list it

**Aim:** Verify `todo` adds a task tagged `[T]` and `list` displays it.

**Input:**
```input
todo read book
list
bye
```

**Expected output:**
```expected
 _                     
| |   _   _ _ __   ___ 
| |  | | | | '_ \ / _ \
| |__| |_| | | | |  __/
|_____\__,_|_| |_|\___|

    ____________________________________________________________
     Oh, hey — I'm Lune.
     What's rattling around in that head of yours?
    ____________________________________________________________

    ____________________________________________________________
     Added. One more thing to think about:
       [T][ ] read book
     That's 1 task(s) on the board now.
    ____________________________________________________________

    ____________________________________________________________
     Here's what you've got going on:
     1.[T][ ] read book
    ____________________________________________________________

    ____________________________________________________________
     Bye! Go forth and be marginally more organized.
    ____________________________________________________________
```

## Test Case 3: Add a deadline and list it

**Aim:** Verify `deadline ... /by ...` adds a task tagged `[D]`, parsing the `yyyy-mm-dd` date (also accepts `d/m/yyyy HHmm` with a time) and displaying it as `MMM dd yyyy`.

**Input:**
```input
deadline return book /by 2019-10-15
list
bye
```

**Expected output:**
```expected
 _                     
| |   _   _ _ __   ___ 
| |  | | | | '_ \ / _ \
| |__| |_| | | | |  __/
|_____\__,_|_| |_|\___|

    ____________________________________________________________
     Oh, hey — I'm Lune.
     What's rattling around in that head of yours?
    ____________________________________________________________

    ____________________________________________________________
     Added. One more thing to think about:
       [D][ ] return book (by: Oct 15 2019)
     That's 1 task(s) on the board now.
    ____________________________________________________________

    ____________________________________________________________
     Here's what you've got going on:
     1.[D][ ] return book (by: Oct 15 2019)
    ____________________________________________________________

    ____________________________________________________________
     Bye! Go forth and be marginally more organized.
    ____________________________________________________________
```

## Test Case 4: Add an event and list it

**Aim:** Verify `event ... /from ... /to ...` adds a task tagged `[E]`, parsing both dates (`yyyy-mm-dd` or `d/m/yyyy HHmm`) and displaying them as `MMM dd yyyy`.

**Input:**
```input
event project meeting /from 2019-10-16 /to 2019-10-18
list
bye
```

**Expected output:**
```expected
 _                     
| |   _   _ _ __   ___ 
| |  | | | | '_ \ / _ \
| |__| |_| | | | |  __/
|_____\__,_|_| |_|\___|

    ____________________________________________________________
     Oh, hey — I'm Lune.
     What's rattling around in that head of yours?
    ____________________________________________________________

    ____________________________________________________________
     Added. One more thing to think about:
       [E][ ] project meeting (from: Oct 16 2019 to: Oct 18 2019)
     That's 1 task(s) on the board now.
    ____________________________________________________________

    ____________________________________________________________
     Here's what you've got going on:
     1.[E][ ] project meeting (from: Oct 16 2019 to: Oct 18 2019)
    ____________________________________________________________

    ____________________________________________________________
     Bye! Go forth and be marginally more organized.
    ____________________________________________________________
```

## Test Case 5: Mark and unmark a task

**Aim:** Verify `mark` and `unmark` toggle a task's done status, reflected in both their confirmation message and later `list` output.

**Input:**
```input
todo read book
mark 1
list
unmark 1
list
bye
```

**Expected output:**
```expected
 _                     
| |   _   _ _ __   ___ 
| |  | | | | '_ \ / _ \
| |__| |_| | | | |  __/
|_____\__,_|_| |_|\___|

    ____________________________________________________________
     Oh, hey — I'm Lune.
     What's rattling around in that head of yours?
    ____________________________________________________________

    ____________________________________________________________
     Added. One more thing to think about:
       [T][ ] read book
     That's 1 task(s) on the board now.
    ____________________________________________________________

    ____________________________________________________________
     Oh, satisfying. Marked as done:
       [T][X] read book
    ____________________________________________________________

    ____________________________________________________________
     Here's what you've got going on:
     1.[T][X] read book
    ____________________________________________________________

    ____________________________________________________________
     Fair enough, back to not-done:
       [T][ ] read book
    ____________________________________________________________

    ____________________________________________________________
     Here's what you've got going on:
     1.[T][ ] read book
    ____________________________________________________________

    ____________________________________________________________
     Bye! Go forth and be marginally more organized.
    ____________________________________________________________
```

## Test Case 6: Mixed task types end to end

**Aim:** Verify todos, deadlines, and events can be mixed in one session, marked done, and listed together with correct numbering and running task counts.

**Input:**
```input
todo read book
deadline return book /by 2019-06-06
event project meeting /from 2019-08-06 /to 2019-08-08
todo join sports club
mark 1
mark 4
todo borrow book
list
bye
```

**Expected output:**
```expected
 _                     
| |   _   _ _ __   ___ 
| |  | | | | '_ \ / _ \
| |__| |_| | | | |  __/
|_____\__,_|_| |_|\___|

    ____________________________________________________________
     Oh, hey — I'm Lune.
     What's rattling around in that head of yours?
    ____________________________________________________________

    ____________________________________________________________
     Added. One more thing to think about:
       [T][ ] read book
     That's 1 task(s) on the board now.
    ____________________________________________________________

    ____________________________________________________________
     Added. One more thing to think about:
       [D][ ] return book (by: Jun 06 2019)
     That's 2 task(s) on the board now.
    ____________________________________________________________

    ____________________________________________________________
     Added. One more thing to think about:
       [E][ ] project meeting (from: Aug 06 2019 to: Aug 08 2019)
     That's 3 task(s) on the board now.
    ____________________________________________________________

    ____________________________________________________________
     Added. One more thing to think about:
       [T][ ] join sports club
     That's 4 task(s) on the board now.
    ____________________________________________________________

    ____________________________________________________________
     Oh, satisfying. Marked as done:
       [T][X] read book
    ____________________________________________________________

    ____________________________________________________________
     Oh, satisfying. Marked as done:
       [T][X] join sports club
    ____________________________________________________________

    ____________________________________________________________
     Added. One more thing to think about:
       [T][ ] borrow book
     That's 5 task(s) on the board now.
    ____________________________________________________________

    ____________________________________________________________
     Here's what you've got going on:
     1.[T][X] read book
     2.[D][ ] return book (by: Jun 06 2019)
     3.[E][ ] project meeting (from: Aug 06 2019 to: Aug 08 2019)
     4.[T][X] join sports club
     5.[T][ ] borrow book
    ____________________________________________________________

    ____________________________________________________________
     Bye! Go forth and be marginally more organized.
    ____________________________________________________________
```

## Test Case 7: Consecutive errors leave the task list untouched

**Aim:** Verify that back-to-back invalid commands (empty todo/deadline/event) are each rejected without adding a phantom task, leaving the list exactly as it was before them.

**Input:**
```input
todo read book
todo
deadline
event
list
bye
```

**Expected output:**
```expected
 _                     
| |   _   _ _ __   ___ 
| |  | | | | '_ \ / _ \
| |__| |_| | | | |  __/
|_____\__,_|_| |_|\___|

    ____________________________________________________________
     Oh, hey — I'm Lune.
     What's rattling around in that head of yours?
    ____________________________________________________________

    ____________________________________________________________
     Added. One more thing to think about:
       [T][ ] read book
     That's 1 task(s) on the board now.
    ____________________________________________________________

    ____________________________________________________________
     Ugh, a todo needs a description... try: todo <what to do>
    ____________________________________________________________

    ____________________________________________________________
     Ugh, a deadline needs a description... try: deadline <what to do> /by <date>
    ____________________________________________________________

    ____________________________________________________________
     Ugh, an event needs a description... try: event <what to do> /from <date> /to <date>
    ____________________________________________________________

    ____________________________________________________________
     Here's what you've got going on:
     1.[T][ ] read book
    ____________________________________________________________

    ____________________________________________________________
     Bye! Go forth and be marginally more organized.
    ____________________________________________________________
```

## Test Case 8: A malformed deadline does not consume a task slot

**Aim:** Verify a deadline missing '/by' is rejected and does not get numbered as a task, so the next successful add still gets the correct index.

**Input:**
```input
todo read book
deadline return book
deadline return book /by 2019-06-06
list
bye
```

**Expected output:**
```expected
 _                     
| |   _   _ _ __   ___ 
| |  | | | | '_ \ / _ \
| |__| |_| | | | |  __/
|_____\__,_|_| |_|\___|

    ____________________________________________________________
     Oh, hey — I'm Lune.
     What's rattling around in that head of yours?
    ____________________________________________________________

    ____________________________________________________________
     Added. One more thing to think about:
       [T][ ] read book
     That's 1 task(s) on the board now.
    ____________________________________________________________

    ____________________________________________________________
     *sigh* — a deadline needs a /by date... try: deadline return book /by <date>
    ____________________________________________________________

    ____________________________________________________________
     Added. One more thing to think about:
       [D][ ] return book (by: Jun 06 2019)
     That's 2 task(s) on the board now.
    ____________________________________________________________

    ____________________________________________________________
     Here's what you've got going on:
     1.[T][ ] read book
     2.[D][ ] return book (by: Jun 06 2019)
    ____________________________________________________________

    ____________________________________________________________
     Bye! Go forth and be marginally more organized.
    ____________________________________________________________
```

## Test Case 9: A malformed event does not consume a task slot

**Aim:** Verify an event missing '/to' is rejected and does not get numbered as a task, so the next successful add still gets task number 1.

**Input:**
```input
event meeting /from 2019-06-06
event meeting /from 2019-06-06 /to 2019-06-08
list
bye
```

**Expected output:**
```expected
 _                     
| |   _   _ _ __   ___ 
| |  | | | | '_ \ / _ \
| |__| |_| | | | |  __/
|_____\__,_|_| |_|\___|

    ____________________________________________________________
     Oh, hey — I'm Lune.
     What's rattling around in that head of yours?
    ____________________________________________________________

    ____________________________________________________________
     Mm, an event needs a /to date after /from... try: event meeting /from <date> /to <date>
    ____________________________________________________________

    ____________________________________________________________
     Added. One more thing to think about:
       [E][ ] meeting (from: Jun 06 2019 to: Jun 08 2019)
     That's 1 task(s) on the board now.
    ____________________________________________________________

    ____________________________________________________________
     Here's what you've got going on:
     1.[E][ ] meeting (from: Jun 06 2019 to: Jun 08 2019)
    ____________________________________________________________

    ____________________________________________________________
     Bye! Go forth and be marginally more organized.
    ____________________________________________________________
```

## Test Case 10: mark/unmark errors never change any task's done status or count

**Aim:** Verify marking on an empty list, task number 0, an out-of-range number, and a non-numeric argument are all rejected without side effects, and a valid mark afterward still works correctly.

**Input:**
```input
mark 1
todo read book
mark 0
mark 5
mark abc
mark 1
list
bye
```

**Expected output:**
```expected
 _                     
| |   _   _ _ __   ___ 
| |  | | | | '_ \ / _ \
| |__| |_| | | | |  __/
|_____\__,_|_| |_|\___|

    ____________________________________________________________
     Oh, hey — I'm Lune.
     What's rattling around in that head of yours?
    ____________________________________________________________

    ____________________________________________________________
     Ugh, task 1 doesn't exist... you currently have 0 task(s).
    ____________________________________________________________

    ____________________________________________________________
     Added. One more thing to think about:
       [T][ ] read book
     That's 1 task(s) on the board now.
    ____________________________________________________________

    ____________________________________________________________
     Ugh, task 0 doesn't exist... you currently have 1 task(s).
    ____________________________________________________________

    ____________________________________________________________
     Ugh, task 5 doesn't exist... you currently have 1 task(s).
    ____________________________________________________________

    ____________________________________________________________
     Mm, "abc" doesn't look like a task number.
    ____________________________________________________________

    ____________________________________________________________
     Oh, satisfying. Marked as done:
       [T][X] read book
    ____________________________________________________________

    ____________________________________________________________
     Here's what you've got going on:
     1.[T][X] read book
    ____________________________________________________________

    ____________________________________________________________
     Bye! Go forth and be marginally more organized.
    ____________________________________________________________
```

## Test Case 11: Unknown commands interleaved with valid ones don't disturb the list

**Aim:** Verify unrecognized commands are rejected without altering existing tasks or task numbering, interleaved between valid adds.

**Input:**
```input
todo read book
blah
list
foobar
todo return book
list
bye
```

**Expected output:**
```expected
 _                     
| |   _   _ _ __   ___ 
| |  | | | | '_ \ / _ \
| |__| |_| | | | |  __/
|_____\__,_|_| |_|\___|

    ____________________________________________________________
     Oh, hey — I'm Lune.
     What's rattling around in that head of yours?
    ____________________________________________________________

    ____________________________________________________________
     Added. One more thing to think about:
       [T][ ] read book
     That's 1 task(s) on the board now.
    ____________________________________________________________

    ____________________________________________________________
     *sigh* I don't recognize that command... try todo, deadline, event, list, mark, unmark, delete, on, find, archive, or bye.
    ____________________________________________________________

    ____________________________________________________________
     Here's what you've got going on:
     1.[T][ ] read book
    ____________________________________________________________

    ____________________________________________________________
     *sigh* I don't recognize that command... try todo, deadline, event, list, mark, unmark, delete, on, find, archive, or bye.
    ____________________________________________________________

    ____________________________________________________________
     Added. One more thing to think about:
       [T][ ] return book
     That's 2 task(s) on the board now.
    ____________________________________________________________

    ____________________________________________________________
     Here's what you've got going on:
     1.[T][ ] read book
     2.[T][ ] return book
    ____________________________________________________________

    ____________________________________________________________
     Bye! Go forth and be marginally more organized.
    ____________________________________________________________
```

## Test Case 12: The task list grows past the old fixed-array size

**Aim:** Verify tasks use an `ArrayList` with no fixed capacity (per the `A-Collections` extension) by adding well beyond the previous 100-task array limit, then confirm `delete` still correctly shrinks the list and updates what indices are valid.

**Input:**
```input
todo task1
todo task2
todo task3
todo task4
todo task5
todo task6
todo task7
todo task8
todo task9
todo task10
todo task11
todo task12
todo task13
todo task14
todo task15
todo task16
todo task17
todo task18
todo task19
todo task20
todo task21
todo task22
todo task23
todo task24
todo task25
todo task26
todo task27
todo task28
todo task29
todo task30
todo task31
todo task32
todo task33
todo task34
todo task35
todo task36
todo task37
todo task38
todo task39
todo task40
todo task41
todo task42
todo task43
todo task44
todo task45
todo task46
todo task47
todo task48
todo task49
todo task50
todo task51
todo task52
todo task53
todo task54
todo task55
todo task56
todo task57
todo task58
todo task59
todo task60
todo task61
todo task62
todo task63
todo task64
todo task65
todo task66
todo task67
todo task68
todo task69
todo task70
todo task71
todo task72
todo task73
todo task74
todo task75
todo task76
todo task77
todo task78
todo task79
todo task80
todo task81
todo task82
todo task83
todo task84
todo task85
todo task86
todo task87
todo task88
todo task89
todo task90
todo task91
todo task92
todo task93
todo task94
todo task95
todo task96
todo task97
todo task98
todo task99
todo task100
todo task101
todo task102
todo task103
todo task104
todo task105
mark 105
delete 105
mark 105
bye
```

**Expected output:**
```expected
 _                     
| |   _   _ _ __   ___ 
| |  | | | | '_ \ / _ \
| |__| |_| | | | |  __/
|_____\__,_|_| |_|\___|

    ____________________________________________________________
     Oh, hey — I'm Lune.
     What's rattling around in that head of yours?
    ____________________________________________________________

    ____________________________________________________________
     Added. One more thing to think about:
       [T][ ] task1
     That's 1 task(s) on the board now.
    ____________________________________________________________

    ____________________________________________________________
     Added. One more thing to think about:
       [T][ ] task2
     That's 2 task(s) on the board now.
    ____________________________________________________________

    ____________________________________________________________
     Added. One more thing to think about:
       [T][ ] task3
     That's 3 task(s) on the board now.
    ____________________________________________________________

    ____________________________________________________________
     Added. One more thing to think about:
       [T][ ] task4
     That's 4 task(s) on the board now.
    ____________________________________________________________

    ____________________________________________________________
     Added. One more thing to think about:
       [T][ ] task5
     That's 5 task(s) on the board now.
    ____________________________________________________________

    ____________________________________________________________
     Added. One more thing to think about:
       [T][ ] task6
     That's 6 task(s) on the board now.
    ____________________________________________________________

    ____________________________________________________________
     Added. One more thing to think about:
       [T][ ] task7
     That's 7 task(s) on the board now.
    ____________________________________________________________

    ____________________________________________________________
     Added. One more thing to think about:
       [T][ ] task8
     That's 8 task(s) on the board now.
    ____________________________________________________________

    ____________________________________________________________
     Added. One more thing to think about:
       [T][ ] task9
     That's 9 task(s) on the board now.
    ____________________________________________________________

    ____________________________________________________________
     Added. One more thing to think about:
       [T][ ] task10
     That's 10 task(s) on the board now.
    ____________________________________________________________

    ____________________________________________________________
     Added. One more thing to think about:
       [T][ ] task11
     That's 11 task(s) on the board now.
    ____________________________________________________________

    ____________________________________________________________
     Added. One more thing to think about:
       [T][ ] task12
     That's 12 task(s) on the board now.
    ____________________________________________________________

    ____________________________________________________________
     Added. One more thing to think about:
       [T][ ] task13
     That's 13 task(s) on the board now.
    ____________________________________________________________

    ____________________________________________________________
     Added. One more thing to think about:
       [T][ ] task14
     That's 14 task(s) on the board now.
    ____________________________________________________________

    ____________________________________________________________
     Added. One more thing to think about:
       [T][ ] task15
     That's 15 task(s) on the board now.
    ____________________________________________________________

    ____________________________________________________________
     Added. One more thing to think about:
       [T][ ] task16
     That's 16 task(s) on the board now.
    ____________________________________________________________

    ____________________________________________________________
     Added. One more thing to think about:
       [T][ ] task17
     That's 17 task(s) on the board now.
    ____________________________________________________________

    ____________________________________________________________
     Added. One more thing to think about:
       [T][ ] task18
     That's 18 task(s) on the board now.
    ____________________________________________________________

    ____________________________________________________________
     Added. One more thing to think about:
       [T][ ] task19
     That's 19 task(s) on the board now.
    ____________________________________________________________

    ____________________________________________________________
     Added. One more thing to think about:
       [T][ ] task20
     That's 20 task(s) on the board now.
    ____________________________________________________________

    ____________________________________________________________
     Added. One more thing to think about:
       [T][ ] task21
     That's 21 task(s) on the board now.
    ____________________________________________________________

    ____________________________________________________________
     Added. One more thing to think about:
       [T][ ] task22
     That's 22 task(s) on the board now.
    ____________________________________________________________

    ____________________________________________________________
     Added. One more thing to think about:
       [T][ ] task23
     That's 23 task(s) on the board now.
    ____________________________________________________________

    ____________________________________________________________
     Added. One more thing to think about:
       [T][ ] task24
     That's 24 task(s) on the board now.
    ____________________________________________________________

    ____________________________________________________________
     Added. One more thing to think about:
       [T][ ] task25
     That's 25 task(s) on the board now.
    ____________________________________________________________

    ____________________________________________________________
     Added. One more thing to think about:
       [T][ ] task26
     That's 26 task(s) on the board now.
    ____________________________________________________________

    ____________________________________________________________
     Added. One more thing to think about:
       [T][ ] task27
     That's 27 task(s) on the board now.
    ____________________________________________________________

    ____________________________________________________________
     Added. One more thing to think about:
       [T][ ] task28
     That's 28 task(s) on the board now.
    ____________________________________________________________

    ____________________________________________________________
     Added. One more thing to think about:
       [T][ ] task29
     That's 29 task(s) on the board now.
    ____________________________________________________________

    ____________________________________________________________
     Added. One more thing to think about:
       [T][ ] task30
     That's 30 task(s) on the board now.
    ____________________________________________________________

    ____________________________________________________________
     Added. One more thing to think about:
       [T][ ] task31
     That's 31 task(s) on the board now.
    ____________________________________________________________

    ____________________________________________________________
     Added. One more thing to think about:
       [T][ ] task32
     That's 32 task(s) on the board now.
    ____________________________________________________________

    ____________________________________________________________
     Added. One more thing to think about:
       [T][ ] task33
     That's 33 task(s) on the board now.
    ____________________________________________________________

    ____________________________________________________________
     Added. One more thing to think about:
       [T][ ] task34
     That's 34 task(s) on the board now.
    ____________________________________________________________

    ____________________________________________________________
     Added. One more thing to think about:
       [T][ ] task35
     That's 35 task(s) on the board now.
    ____________________________________________________________

    ____________________________________________________________
     Added. One more thing to think about:
       [T][ ] task36
     That's 36 task(s) on the board now.
    ____________________________________________________________

    ____________________________________________________________
     Added. One more thing to think about:
       [T][ ] task37
     That's 37 task(s) on the board now.
    ____________________________________________________________

    ____________________________________________________________
     Added. One more thing to think about:
       [T][ ] task38
     That's 38 task(s) on the board now.
    ____________________________________________________________

    ____________________________________________________________
     Added. One more thing to think about:
       [T][ ] task39
     That's 39 task(s) on the board now.
    ____________________________________________________________

    ____________________________________________________________
     Added. One more thing to think about:
       [T][ ] task40
     That's 40 task(s) on the board now.
    ____________________________________________________________

    ____________________________________________________________
     Added. One more thing to think about:
       [T][ ] task41
     That's 41 task(s) on the board now.
    ____________________________________________________________

    ____________________________________________________________
     Added. One more thing to think about:
       [T][ ] task42
     That's 42 task(s) on the board now.
    ____________________________________________________________

    ____________________________________________________________
     Added. One more thing to think about:
       [T][ ] task43
     That's 43 task(s) on the board now.
    ____________________________________________________________

    ____________________________________________________________
     Added. One more thing to think about:
       [T][ ] task44
     That's 44 task(s) on the board now.
    ____________________________________________________________

    ____________________________________________________________
     Added. One more thing to think about:
       [T][ ] task45
     That's 45 task(s) on the board now.
    ____________________________________________________________

    ____________________________________________________________
     Added. One more thing to think about:
       [T][ ] task46
     That's 46 task(s) on the board now.
    ____________________________________________________________

    ____________________________________________________________
     Added. One more thing to think about:
       [T][ ] task47
     That's 47 task(s) on the board now.
    ____________________________________________________________

    ____________________________________________________________
     Added. One more thing to think about:
       [T][ ] task48
     That's 48 task(s) on the board now.
    ____________________________________________________________

    ____________________________________________________________
     Added. One more thing to think about:
       [T][ ] task49
     That's 49 task(s) on the board now.
    ____________________________________________________________

    ____________________________________________________________
     Added. One more thing to think about:
       [T][ ] task50
     That's 50 task(s) on the board now.
    ____________________________________________________________

    ____________________________________________________________
     Added. One more thing to think about:
       [T][ ] task51
     That's 51 task(s) on the board now.
    ____________________________________________________________

    ____________________________________________________________
     Added. One more thing to think about:
       [T][ ] task52
     That's 52 task(s) on the board now.
    ____________________________________________________________

    ____________________________________________________________
     Added. One more thing to think about:
       [T][ ] task53
     That's 53 task(s) on the board now.
    ____________________________________________________________

    ____________________________________________________________
     Added. One more thing to think about:
       [T][ ] task54
     That's 54 task(s) on the board now.
    ____________________________________________________________

    ____________________________________________________________
     Added. One more thing to think about:
       [T][ ] task55
     That's 55 task(s) on the board now.
    ____________________________________________________________

    ____________________________________________________________
     Added. One more thing to think about:
       [T][ ] task56
     That's 56 task(s) on the board now.
    ____________________________________________________________

    ____________________________________________________________
     Added. One more thing to think about:
       [T][ ] task57
     That's 57 task(s) on the board now.
    ____________________________________________________________

    ____________________________________________________________
     Added. One more thing to think about:
       [T][ ] task58
     That's 58 task(s) on the board now.
    ____________________________________________________________

    ____________________________________________________________
     Added. One more thing to think about:
       [T][ ] task59
     That's 59 task(s) on the board now.
    ____________________________________________________________

    ____________________________________________________________
     Added. One more thing to think about:
       [T][ ] task60
     That's 60 task(s) on the board now.
    ____________________________________________________________

    ____________________________________________________________
     Added. One more thing to think about:
       [T][ ] task61
     That's 61 task(s) on the board now.
    ____________________________________________________________

    ____________________________________________________________
     Added. One more thing to think about:
       [T][ ] task62
     That's 62 task(s) on the board now.
    ____________________________________________________________

    ____________________________________________________________
     Added. One more thing to think about:
       [T][ ] task63
     That's 63 task(s) on the board now.
    ____________________________________________________________

    ____________________________________________________________
     Added. One more thing to think about:
       [T][ ] task64
     That's 64 task(s) on the board now.
    ____________________________________________________________

    ____________________________________________________________
     Added. One more thing to think about:
       [T][ ] task65
     That's 65 task(s) on the board now.
    ____________________________________________________________

    ____________________________________________________________
     Added. One more thing to think about:
       [T][ ] task66
     That's 66 task(s) on the board now.
    ____________________________________________________________

    ____________________________________________________________
     Added. One more thing to think about:
       [T][ ] task67
     That's 67 task(s) on the board now.
    ____________________________________________________________

    ____________________________________________________________
     Added. One more thing to think about:
       [T][ ] task68
     That's 68 task(s) on the board now.
    ____________________________________________________________

    ____________________________________________________________
     Added. One more thing to think about:
       [T][ ] task69
     That's 69 task(s) on the board now.
    ____________________________________________________________

    ____________________________________________________________
     Added. One more thing to think about:
       [T][ ] task70
     That's 70 task(s) on the board now.
    ____________________________________________________________

    ____________________________________________________________
     Added. One more thing to think about:
       [T][ ] task71
     That's 71 task(s) on the board now.
    ____________________________________________________________

    ____________________________________________________________
     Added. One more thing to think about:
       [T][ ] task72
     That's 72 task(s) on the board now.
    ____________________________________________________________

    ____________________________________________________________
     Added. One more thing to think about:
       [T][ ] task73
     That's 73 task(s) on the board now.
    ____________________________________________________________

    ____________________________________________________________
     Added. One more thing to think about:
       [T][ ] task74
     That's 74 task(s) on the board now.
    ____________________________________________________________

    ____________________________________________________________
     Added. One more thing to think about:
       [T][ ] task75
     That's 75 task(s) on the board now.
    ____________________________________________________________

    ____________________________________________________________
     Added. One more thing to think about:
       [T][ ] task76
     That's 76 task(s) on the board now.
    ____________________________________________________________

    ____________________________________________________________
     Added. One more thing to think about:
       [T][ ] task77
     That's 77 task(s) on the board now.
    ____________________________________________________________

    ____________________________________________________________
     Added. One more thing to think about:
       [T][ ] task78
     That's 78 task(s) on the board now.
    ____________________________________________________________

    ____________________________________________________________
     Added. One more thing to think about:
       [T][ ] task79
     That's 79 task(s) on the board now.
    ____________________________________________________________

    ____________________________________________________________
     Added. One more thing to think about:
       [T][ ] task80
     That's 80 task(s) on the board now.
    ____________________________________________________________

    ____________________________________________________________
     Added. One more thing to think about:
       [T][ ] task81
     That's 81 task(s) on the board now.
    ____________________________________________________________

    ____________________________________________________________
     Added. One more thing to think about:
       [T][ ] task82
     That's 82 task(s) on the board now.
    ____________________________________________________________

    ____________________________________________________________
     Added. One more thing to think about:
       [T][ ] task83
     That's 83 task(s) on the board now.
    ____________________________________________________________

    ____________________________________________________________
     Added. One more thing to think about:
       [T][ ] task84
     That's 84 task(s) on the board now.
    ____________________________________________________________

    ____________________________________________________________
     Added. One more thing to think about:
       [T][ ] task85
     That's 85 task(s) on the board now.
    ____________________________________________________________

    ____________________________________________________________
     Added. One more thing to think about:
       [T][ ] task86
     That's 86 task(s) on the board now.
    ____________________________________________________________

    ____________________________________________________________
     Added. One more thing to think about:
       [T][ ] task87
     That's 87 task(s) on the board now.
    ____________________________________________________________

    ____________________________________________________________
     Added. One more thing to think about:
       [T][ ] task88
     That's 88 task(s) on the board now.
    ____________________________________________________________

    ____________________________________________________________
     Added. One more thing to think about:
       [T][ ] task89
     That's 89 task(s) on the board now.
    ____________________________________________________________

    ____________________________________________________________
     Added. One more thing to think about:
       [T][ ] task90
     That's 90 task(s) on the board now.
    ____________________________________________________________

    ____________________________________________________________
     Added. One more thing to think about:
       [T][ ] task91
     That's 91 task(s) on the board now.
    ____________________________________________________________

    ____________________________________________________________
     Added. One more thing to think about:
       [T][ ] task92
     That's 92 task(s) on the board now.
    ____________________________________________________________

    ____________________________________________________________
     Added. One more thing to think about:
       [T][ ] task93
     That's 93 task(s) on the board now.
    ____________________________________________________________

    ____________________________________________________________
     Added. One more thing to think about:
       [T][ ] task94
     That's 94 task(s) on the board now.
    ____________________________________________________________

    ____________________________________________________________
     Added. One more thing to think about:
       [T][ ] task95
     That's 95 task(s) on the board now.
    ____________________________________________________________

    ____________________________________________________________
     Added. One more thing to think about:
       [T][ ] task96
     That's 96 task(s) on the board now.
    ____________________________________________________________

    ____________________________________________________________
     Added. One more thing to think about:
       [T][ ] task97
     That's 97 task(s) on the board now.
    ____________________________________________________________

    ____________________________________________________________
     Added. One more thing to think about:
       [T][ ] task98
     That's 98 task(s) on the board now.
    ____________________________________________________________

    ____________________________________________________________
     Added. One more thing to think about:
       [T][ ] task99
     That's 99 task(s) on the board now.
    ____________________________________________________________

    ____________________________________________________________
     Added. One more thing to think about:
       [T][ ] task100
     That's 100 task(s) on the board now.
    ____________________________________________________________

    ____________________________________________________________
     Added. One more thing to think about:
       [T][ ] task101
     That's 101 task(s) on the board now.
    ____________________________________________________________

    ____________________________________________________________
     Added. One more thing to think about:
       [T][ ] task102
     That's 102 task(s) on the board now.
    ____________________________________________________________

    ____________________________________________________________
     Added. One more thing to think about:
       [T][ ] task103
     That's 103 task(s) on the board now.
    ____________________________________________________________

    ____________________________________________________________
     Added. One more thing to think about:
       [T][ ] task104
     That's 104 task(s) on the board now.
    ____________________________________________________________

    ____________________________________________________________
     Added. One more thing to think about:
       [T][ ] task105
     That's 105 task(s) on the board now.
    ____________________________________________________________

    ____________________________________________________________
     Oh, satisfying. Marked as done:
       [T][X] task105
    ____________________________________________________________

    ____________________________________________________________
     Poof. Gone:
       [T][X] task105
     That's 104 task(s) on the board now.
    ____________________________________________________________

    ____________________________________________________________
     Ugh, task 105 doesn't exist... you currently have 104 task(s).
    ____________________________________________________________

    ____________________________________________________________
     Bye! Go forth and be marginally more organized.
    ____________________________________________________________
```

## Test Case 13: unmark errors never change any task's done status or count

**Aim:** Verify unmarking on an empty list, task number 0, an out-of-range number, and a non-numeric argument are all rejected without side effects, and a valid unmark afterward still works correctly. (Added after this exact class of bug — `unmark` skipping its bounds check — was injected and slipped past every other test case undetected, since only `mark` errors were previously covered.)

**Input:**
```input
unmark 1
todo read book
mark 1
unmark 0
unmark 5
unmark abc
unmark 1
list
bye
```

**Expected output:**
```expected
 _                     
| |   _   _ _ __   ___ 
| |  | | | | '_ \ / _ \
| |__| |_| | | | |  __/
|_____\__,_|_| |_|\___|

    ____________________________________________________________
     Oh, hey — I'm Lune.
     What's rattling around in that head of yours?
    ____________________________________________________________

    ____________________________________________________________
     Ugh, task 1 doesn't exist... you currently have 0 task(s).
    ____________________________________________________________

    ____________________________________________________________
     Added. One more thing to think about:
       [T][ ] read book
     That's 1 task(s) on the board now.
    ____________________________________________________________

    ____________________________________________________________
     Oh, satisfying. Marked as done:
       [T][X] read book
    ____________________________________________________________

    ____________________________________________________________
     Ugh, task 0 doesn't exist... you currently have 1 task(s).
    ____________________________________________________________

    ____________________________________________________________
     Ugh, task 5 doesn't exist... you currently have 1 task(s).
    ____________________________________________________________

    ____________________________________________________________
     Mm, "abc" doesn't look like a task number.
    ____________________________________________________________

    ____________________________________________________________
     Fair enough, back to not-done:
       [T][ ] read book
    ____________________________________________________________

    ____________________________________________________________
     Here's what you've got going on:
     1.[T][ ] read book
    ____________________________________________________________

    ____________________________________________________________
     Bye! Go forth and be marginally more organized.
    ____________________________________________________________
```

## Test Case 14: delete errors never corrupt the list, and deleting reindexes correctly

**Aim:** Verify deleting on an empty list, task number 0, an out-of-range number, and a non-numeric argument are all rejected without side effects; verify a successful delete correctly renumbers the remaining tasks (not just leaves a gap); and verify `list` on an empty list still prints cleanly.

**Input:**
```input
delete 1
todo read book
todo return book
delete 0
delete 5
delete abc
delete 1
list
delete 1
list
bye
```

**Expected output:**
```expected
 _                     
| |   _   _ _ __   ___ 
| |  | | | | '_ \ / _ \
| |__| |_| | | | |  __/
|_____\__,_|_| |_|\___|

    ____________________________________________________________
     Oh, hey — I'm Lune.
     What's rattling around in that head of yours?
    ____________________________________________________________

    ____________________________________________________________
     Ugh, task 1 doesn't exist... you currently have 0 task(s).
    ____________________________________________________________

    ____________________________________________________________
     Added. One more thing to think about:
       [T][ ] read book
     That's 1 task(s) on the board now.
    ____________________________________________________________

    ____________________________________________________________
     Added. One more thing to think about:
       [T][ ] return book
     That's 2 task(s) on the board now.
    ____________________________________________________________

    ____________________________________________________________
     Ugh, task 0 doesn't exist... you currently have 2 task(s).
    ____________________________________________________________

    ____________________________________________________________
     Ugh, task 5 doesn't exist... you currently have 2 task(s).
    ____________________________________________________________

    ____________________________________________________________
     Mm, "abc" doesn't look like a task number.
    ____________________________________________________________

    ____________________________________________________________
     Poof. Gone:
       [T][ ] read book
     That's 1 task(s) on the board now.
    ____________________________________________________________

    ____________________________________________________________
     Here's what you've got going on:
     1.[T][ ] return book
    ____________________________________________________________

    ____________________________________________________________
     Poof. Gone:
       [T][ ] return book
     That's 0 task(s) on the board now.
    ____________________________________________________________

    ____________________________________________________________
     Here's what you've got going on:
    ____________________________________________________________

    ____________________________________________________________
     Bye! Go forth and be marginally more organized.
    ____________________________________________________________
```

## Test Case 15: Tasks are saved to disk after every change

**Aim:** Verify that adding, marking, and deleting tasks each write the current task list to `data/lune.txt` in the pipe-separated save format (dates/times stored via `LocalDateTime`'s ISO format), and that a deleted task is actually removed from the file (not just left as a stale line).

**Input:**
```input
todo read book
deadline return book /by 2019-06-06
event project meeting /from 2019-08-06 /to 2019-08-08
todo join sports club
mark 1
mark 4
delete 2
bye
```

**Expected output:**
```expected
 _                     
| |   _   _ _ __   ___ 
| |  | | | | '_ \ / _ \
| |__| |_| | | | |  __/
|_____\__,_|_| |_|\___|

    ____________________________________________________________
     Oh, hey — I'm Lune.
     What's rattling around in that head of yours?
    ____________________________________________________________

    ____________________________________________________________
     Added. One more thing to think about:
       [T][ ] read book
     That's 1 task(s) on the board now.
    ____________________________________________________________

    ____________________________________________________________
     Added. One more thing to think about:
       [D][ ] return book (by: Jun 06 2019)
     That's 2 task(s) on the board now.
    ____________________________________________________________

    ____________________________________________________________
     Added. One more thing to think about:
       [E][ ] project meeting (from: Aug 06 2019 to: Aug 08 2019)
     That's 3 task(s) on the board now.
    ____________________________________________________________

    ____________________________________________________________
     Added. One more thing to think about:
       [T][ ] join sports club
     That's 4 task(s) on the board now.
    ____________________________________________________________

    ____________________________________________________________
     Oh, satisfying. Marked as done:
       [T][X] read book
    ____________________________________________________________

    ____________________________________________________________
     Oh, satisfying. Marked as done:
       [T][X] join sports club
    ____________________________________________________________

    ____________________________________________________________
     Poof. Gone:
       [D][ ] return book (by: Jun 06 2019)
     That's 3 task(s) on the board now.
    ____________________________________________________________

    ____________________________________________________________
     Bye! Go forth and be marginally more organized.
    ____________________________________________________________
```

**Expected file (`data/lune.txt`):**
```file:data/lune.txt
T | 1 | read book
E | 0 | project meeting | 2019-08-06T00:00 | 2019-08-08T00:00
T | 1 | join sports club
```

## Test Case 16: Tasks are loaded from an existing save file on startup

**Aim:** Verify that on startup, an existing `data/lune.txt` (in the format `saveTasks()` writes) is correctly parsed back into todos/deadlines/events — including parsing saved date/times back into `LocalDateTime` and displaying them as `MMM dd yyyy` — with the right done status, and that subsequent commands operate correctly on the loaded tasks.

**Given file:**
```given-file:data/lune.txt
T | 1 | read book
D | 0 | return book | 2019-06-06T00:00
E | 0 | project meeting | 2019-08-06T00:00 | 2019-08-08T00:00
T | 1 | join sports club
```

**Input:**
```input
list
unmark 1
list
bye
```

**Expected output:**
```expected
 _                     
| |   _   _ _ __   ___ 
| |  | | | | '_ \ / _ \
| |__| |_| | | | |  __/
|_____\__,_|_| |_|\___|

    ____________________________________________________________
     Oh, hey — I'm Lune.
     What's rattling around in that head of yours?
    ____________________________________________________________

    ____________________________________________________________
     Here's what you've got going on:
     1.[T][X] read book
     2.[D][ ] return book (by: Jun 06 2019)
     3.[E][ ] project meeting (from: Aug 06 2019 to: Aug 08 2019)
     4.[T][X] join sports club
    ____________________________________________________________

    ____________________________________________________________
     Fair enough, back to not-done:
       [T][ ] read book
    ____________________________________________________________

    ____________________________________________________________
     Here's what you've got going on:
     1.[T][ ] read book
     2.[D][ ] return book (by: Jun 06 2019)
     3.[E][ ] project meeting (from: Aug 06 2019 to: Aug 08 2019)
     4.[T][X] join sports club
    ____________________________________________________________

    ____________________________________________________________
     Bye! Go forth and be marginally more organized.
    ____________________________________________________________
```

## Test Case 17: A corrupted save file doesn't crash startup

**Aim:** Verify a save file that doesn't match the expected format is handled gracefully — the program reports the specific unreadable line instead of crashing, and continues (here, with an empty list since the only line present is bad).

**Given file:**
```given-file:data/lune.txt
this is not a valid line at all
```

**Input:**
```input
list
bye
```

**Expected output:**
```expected
 _                     
| |   _   _ _ __   ___ 
| |  | | | | '_ \ / _ \
| |__| |_| | | | |  __/
|_____\__,_|_| |_|\___|

    ____________________________________________________________
     Oh, hey — I'm Lune.
     What's rattling around in that head of yours?
    ____________________________________________________________

    ____________________________________________________________
     Ugh, skipping unreadable line 1 in data/lune.txt: expected at least 3 fields (type | done | description), found 1
    ____________________________________________________________

    ____________________________________________________________
     Here's what you've got going on:
    ____________________________________________________________

    ____________________________________________________________
     Bye! Go forth and be marginally more organized.
    ____________________________________________________________
```

## Test Case 18: Individually corrupted save-file lines are skipped, not the whole file

**Aim:** Verify that when a save file has a mix of valid and invalid lines, only the invalid ones are skipped (each reported with a specific reason and line number), while every valid line still loads correctly — a single bad line must not wipe out the rest of a person's saved tasks.

**Given file:**
```given-file:data/lune.txt
T | 1 | read book
this is garbage
D | 0 | return book | 2019-06-06T00:00
X | 0 | unknown type
E | 0 | project meeting | 2019-08-06T00:00
T | 2 | bad done flag

T | 1 | join sports club
```

**Input:**
```input
list
bye
```

**Expected output:**
```expected
 _                     
| |   _   _ _ __   ___ 
| |  | | | | '_ \ / _ \
| |__| |_| | | | |  __/
|_____\__,_|_| |_|\___|

    ____________________________________________________________
     Oh, hey — I'm Lune.
     What's rattling around in that head of yours?
    ____________________________________________________________

    ____________________________________________________________
     Ugh, skipping unreadable line 2 in data/lune.txt: expected at least 3 fields (type | done | description), found 1
    ____________________________________________________________

    ____________________________________________________________
     Ugh, skipping unreadable line 4 in data/lune.txt: unknown task type "X"
    ____________________________________________________________

    ____________________________________________________________
     Ugh, skipping unreadable line 5 in data/lune.txt: an event (E) line needs exactly 5 fields with non-empty /from and /to, found 4
    ____________________________________________________________

    ____________________________________________________________
     Ugh, skipping unreadable line 6 in data/lune.txt: done flag must be "0" or "1", found "2"
    ____________________________________________________________

    ____________________________________________________________
     Here's what you've got going on:
     1.[T][X] read book
     2.[D][ ] return book (by: Jun 06 2019)
     3.[T][X] join sports club
    ____________________________________________________________

    ____________________________________________________________
     Bye! Go forth and be marginally more organized.
    ____________________________________________________________
```

## Test Case 19: Input ending without "bye" exits gracefully instead of crashing

**Aim:** Verify that stdin running out without an explicit `bye` (e.g. a piped file with no trailing `bye`, or Ctrl+D) exits cleanly with the normal farewell message, rather than crashing with an uncaught `NoSuchElementException` from `Scanner.nextLine()`.

**Input:**
```input
todo read book
list
```

**Expected output:**
```expected
 _                     
| |   _   _ _ __   ___ 
| |  | | | | '_ \ / _ \
| |__| |_| | | | |  __/
|_____\__,_|_| |_|\___|

    ____________________________________________________________
     Oh, hey — I'm Lune.
     What's rattling around in that head of yours?
    ____________________________________________________________

    ____________________________________________________________
     Added. One more thing to think about:
       [T][ ] read book
     That's 1 task(s) on the board now.
    ____________________________________________________________

    ____________________________________________________________
     Here's what you've got going on:
     1.[T][ ] read book
    ____________________________________________________________

    ____________________________________________________________
     Bye! Go forth and be marginally more organized.
    ____________________________________________________________
```

## Test Case 20: Invalid dates are rejected when adding a deadline/event

**Aim:** Verify `deadline`/`event` reject a `/by`, `/from`, or `/to` value that matches neither accepted format (`yyyy-mm-dd` or `d/m/yyyy HHmm`) — including a syntactically-plausible-but-invalid one like month 13 — without adding a task or corrupting the list, and that a valid date afterward still works.

**Input:**
```input
deadline pay rent /by not-a-date
event trip /from 2019-13-45 /to 2019-10-20
event trip /from 2019-10-20 /to nonsense
deadline pay rent /by 2019-10-20
list
bye
```

**Expected output:**
```expected
 _                     
| |   _   _ _ __   ___ 
| |  | | | | '_ \ / _ \
| |__| |_| | | | |  __/
|_____\__,_|_| |_|\___|

    ____________________________________________________________
     Oh, hey — I'm Lune.
     What's rattling around in that head of yours?
    ____________________________________________________________

    ____________________________________________________________
     Ugh, "not-a-date" isn't a valid /by date/time... use yyyy-mm-dd (e.g. 2019-10-15) or d/m/yyyy HHmm (e.g. 2/12/2019 1800).
    ____________________________________________________________

    ____________________________________________________________
     Ugh, "2019-13-45" isn't a valid /from date/time... use yyyy-mm-dd (e.g. 2019-10-15) or d/m/yyyy HHmm (e.g. 2/12/2019 1800).
    ____________________________________________________________

    ____________________________________________________________
     Ugh, "nonsense" isn't a valid /to date/time... use yyyy-mm-dd (e.g. 2019-10-15) or d/m/yyyy HHmm (e.g. 2/12/2019 1800).
    ____________________________________________________________

    ____________________________________________________________
     Added. One more thing to think about:
       [D][ ] pay rent (by: Oct 20 2019)
     That's 1 task(s) on the board now.
    ____________________________________________________________

    ____________________________________________________________
     Here's what you've got going on:
     1.[D][ ] pay rent (by: Oct 20 2019)
    ____________________________________________________________

    ____________________________________________________________
     Bye! Go forth and be marginally more organized.
    ____________________________________________________________
```

## Test Case 21: An invalid date in the save file is skipped like any other corrupted line

**Aim:** Verify a save-file line whose date/time isn't valid (e.g. hand-edited) is skipped individually with a clear reason, consistent with how other corrupted lines are handled, rather than crashing or discarding the rest of the file.

**Given file:**
```given-file:data/lune.txt
T | 1 | read book
D | 0 | return book | not-a-date
T | 1 | join sports club
```

**Input:**
```input
list
bye
```

**Expected output:**
```expected
 _                     
| |   _   _ _ __   ___ 
| |  | | | | '_ \ / _ \
| |__| |_| | | | |  __/
|_____\__,_|_| |_|\___|

    ____________________________________________________________
     Oh, hey — I'm Lune.
     What's rattling around in that head of yours?
    ____________________________________________________________

    ____________________________________________________________
     Ugh, skipping unreadable line 2 in data/lune.txt: "not-a-date" isn't a valid saved date/time
    ____________________________________________________________

    ____________________________________________________________
     Here's what you've got going on:
     1.[T][X] read book
     2.[T][X] join sports club
    ____________________________________________________________

    ____________________________________________________________
     Bye! Go forth and be marginally more organized.
    ____________________________________________________________
```

## Test Case 22: deadline/event accept d/m/yyyy HHmm as well as yyyy-mm-dd

**Aim:** Verify `/by`, `/from`, and `/to` accept a date with a time attached in `d/m/yyyy HHmm` form (e.g. `2/12/2019 1800` for 6pm on 2 Dec 2019), storing and displaying the time alongside the date rather than treating the whole thing as an opaque string.

**Input:**
```input
deadline return book /by 2/12/2019 1800
event team meeting /from 2/12/2019 0900 /to 2/12/2019 1030
list
bye
```

**Expected output:**
```expected
 _                     
| |   _   _ _ __   ___ 
| |  | | | | '_ \ / _ \
| |__| |_| | | | |  __/
|_____\__,_|_| |_|\___|

    ____________________________________________________________
     Oh, hey — I'm Lune.
     What's rattling around in that head of yours?
    ____________________________________________________________

    ____________________________________________________________
     Added. One more thing to think about:
       [D][ ] return book (by: Dec 02 2019, 6:00 pm)
     That's 1 task(s) on the board now.
    ____________________________________________________________

    ____________________________________________________________
     Added. One more thing to think about:
       [E][ ] team meeting (from: Dec 02 2019, 9:00 am to: Dec 02 2019, 10:30 am)
     That's 2 task(s) on the board now.
    ____________________________________________________________

    ____________________________________________________________
     Here's what you've got going on:
     1.[D][ ] return book (by: Dec 02 2019, 6:00 pm)
     2.[E][ ] team meeting (from: Dec 02 2019, 9:00 am to: Dec 02 2019, 10:30 am)
    ____________________________________________________________

    ____________________________________________________________
     Bye! Go forth and be marginally more organized.
    ____________________________________________________________
```

## Test Case 23: "on <date>" lists deadlines/events occurring on that date

**Aim:** Verify `on <date>` (the date/time stretch goal) lists only deadlines/events associated with the given date — a deadline matches by exact date, an event matches if the date falls within its from/to range inclusive — excluding todos entirely, preserving each task's real list-position number (not a renumbered filtered index), and showing an empty (but non-crashing) result when nothing matches.

**Input:**
```input
todo pack bags
deadline pay rent /by 2019-12-02
event conference /from 2019-12-01 /to 2019-12-05
deadline submit report /by 2019-12-10
on 2019-12-02
on 2019-12-11
bye
```

**Expected output:**
```expected
 _                     
| |   _   _ _ __   ___ 
| |  | | | | '_ \ / _ \
| |__| |_| | | | |  __/
|_____\__,_|_| |_|\___|

    ____________________________________________________________
     Oh, hey — I'm Lune.
     What's rattling around in that head of yours?
    ____________________________________________________________

    ____________________________________________________________
     Added. One more thing to think about:
       [T][ ] pack bags
     That's 1 task(s) on the board now.
    ____________________________________________________________

    ____________________________________________________________
     Added. One more thing to think about:
       [D][ ] pay rent (by: Dec 02 2019)
     That's 2 task(s) on the board now.
    ____________________________________________________________

    ____________________________________________________________
     Added. One more thing to think about:
       [E][ ] conference (from: Dec 01 2019 to: Dec 05 2019)
     That's 3 task(s) on the board now.
    ____________________________________________________________

    ____________________________________________________________
     Added. One more thing to think about:
       [D][ ] submit report (by: Dec 10 2019)
     That's 4 task(s) on the board now.
    ____________________________________________________________

    ____________________________________________________________
     Here's what's happening on Dec 02 2019:
     2.[D][ ] pay rent (by: Dec 02 2019)
     3.[E][ ] conference (from: Dec 01 2019 to: Dec 05 2019)
    ____________________________________________________________

    ____________________________________________________________
     Here's what's happening on Dec 11 2019:
    ____________________________________________________________

    ____________________________________________________________
     Bye! Go forth and be marginally more organized.
    ____________________________________________________________
```

## Test Case 24: "on" rejects a missing or invalid date

**Aim:** Verify `on` with no argument and `on` with an unparseable date are both rejected with a clear message rather than crashing, and that `on` still works normally afterward.

**Input:**
```input
on
on not-a-date
todo pack bags
on 2019-12-02
bye
```

**Expected output:**
```expected
 _                     
| |   _   _ _ __   ___ 
| |  | | | | '_ \ / _ \
| |__| |_| | | | |  __/
|_____\__,_|_| |_|\___|

    ____________________________________________________________
     Oh, hey — I'm Lune.
     What's rattling around in that head of yours?
    ____________________________________________________________

    ____________________________________________________________
     Ugh, tell me which date... try: on <date>
    ____________________________________________________________

    ____________________________________________________________
     Ugh, "not-a-date" isn't a valid on date/time... use yyyy-mm-dd (e.g. 2019-10-15) or d/m/yyyy HHmm (e.g. 2/12/2019 1800).
    ____________________________________________________________

    ____________________________________________________________
     Added. One more thing to think about:
       [T][ ] pack bags
     That's 1 task(s) on the board now.
    ____________________________________________________________

    ____________________________________________________________
     Here's what's happening on Dec 02 2019:
    ____________________________________________________________

    ____________________________________________________________
     Bye! Go forth and be marginally more organized.
    ____________________________________________________________
```

## Test Case 25: "find <keyword>" locates tasks by description

**Aim:** Verify `find` lists only tasks whose description contains the keyword (case-insensitively), preserving each task's real list-position number, excluding non-matching tasks, matching the requirement's own example (this exercises the exact scenario given: two matching tasks of different types both shown, "buy milk" excluded), showing an empty result when nothing matches, and rejecting a missing keyword.

**Input:**
```input
todo read book
mark 1
deadline return book /by 2019-06-06
mark 2
todo buy milk
find book
find BOOK
find nothing
find
bye
```

**Expected output:**
```expected
 _                     
| |   _   _ _ __   ___ 
| |  | | | | '_ \ / _ \
| |__| |_| | | | |  __/
|_____\__,_|_| |_|\___|

    ____________________________________________________________
     Oh, hey — I'm Lune.
     What's rattling around in that head of yours?
    ____________________________________________________________

    ____________________________________________________________
     Added. One more thing to think about:
       [T][ ] read book
     That's 1 task(s) on the board now.
    ____________________________________________________________

    ____________________________________________________________
     Oh, satisfying. Marked as done:
       [T][X] read book
    ____________________________________________________________

    ____________________________________________________________
     Added. One more thing to think about:
       [D][ ] return book (by: Jun 06 2019)
     That's 2 task(s) on the board now.
    ____________________________________________________________

    ____________________________________________________________
     Oh, satisfying. Marked as done:
       [D][X] return book (by: Jun 06 2019)
    ____________________________________________________________

    ____________________________________________________________
     Added. One more thing to think about:
       [T][ ] buy milk
     That's 3 task(s) on the board now.
    ____________________________________________________________

    ____________________________________________________________
     Here's what matched your search:
     1.[T][X] read book
     2.[D][X] return book (by: Jun 06 2019)
    ____________________________________________________________

    ____________________________________________________________
     Here's what matched your search:
     1.[T][X] read book
     2.[D][X] return book (by: Jun 06 2019)
    ____________________________________________________________

    ____________________________________________________________
     Here's what matched your search:
    ____________________________________________________________

    ____________________________________________________________
     Ugh, tell me what to search for... try: find <keyword>
    ____________________________________________________________

    ____________________________________________________________
     Bye! Go forth and be marginally more organized.
    ____________________________________________________________
```

## Test Case 26: "archive" records all tasks to a file and clears the list

**Aim:** Verify "archive" writes every current task to `data/archive.txt` as a human-readable record, then clears the active list — confirmed by both the confirmation message and a subsequent "list" showing nothing, and by `data/lune.txt` being empty afterward.

**Given file:**
```given-file:data/lune.txt
T | 1 | read book
D | 0 | return book | 2019-06-06T00:00
```

**Input:**
```input
archive
list
bye
```

**Expected output:**
```expected
 _                     
| |   _   _ _ __   ___ 
| |  | | | | '_ \ / _ \
| |__| |_| | | | |  __/
|_____\__,_|_| |_|\___|

    ____________________________________________________________
     Oh, hey — I'm Lune.
     What's rattling around in that head of yours?
    ____________________________________________________________

    ____________________________________________________________
     Archived 2 task(s) to data/archive.txt.
     Ah, a blank slate. Delightful.
    ____________________________________________________________

    ____________________________________________________________
     Here's what you've got going on:
    ____________________________________________________________

    ____________________________________________________________
     Bye! Go forth and be marginally more organized.
    ____________________________________________________________
```

**Expected file (`data/lune.txt`):**
```file:data/lune.txt
```

Note: `data/archive.txt`'s exact content isn't asserted here — it includes a "Archived on <current date/time>" timestamp header, which isn't reproducible run-to-run.

## Test Case 27: "archive" with an empty list writes no archive file

**Aim:** Verify "archive" on an empty list succeeds gracefully (no error) and reports 0 tasks archived, and — since there's nothing meaningful to record — doesn't create `data/archive.txt` at all.

**Input:**
```input
archive
bye
```

**Expected output:**
```expected
 _                     
| |   _   _ _ __   ___ 
| |  | | | | '_ \ / _ \
| |__| |_| | | | |  __/
|_____\__,_|_| |_|\___|

    ____________________________________________________________
     Oh, hey — I'm Lune.
     What's rattling around in that head of yours?
    ____________________________________________________________

    ____________________________________________________________
     Archived 0 task(s) to data/archive.txt.
     Ah, a blank slate. Delightful.
    ____________________________________________________________

    ____________________________________________________________
     Bye! Go forth and be marginally more organized.
    ____________________________________________________________
```

**Expected file (`data/archive.txt`):**
```file:data/archive.txt
(file not found)
```

## Test Case 28: Leading/trailing/extra whitespace is tolerated

**Aim:** Verify a command works the same whether or not it's surrounded by extra whitespace, or has extra internal whitespace between words in its description — including `bye` itself.

**Input:**
```input
  todo read   book  
   list
bye  
```

**Expected output:**
```expected
 _                     
| |   _   _ _ __   ___ 
| |  | | | | '_ \ / _ \
| |__| |_| | | | |  __/
|_____\__,_|_| |_|\___|

    ____________________________________________________________
     Oh, hey — I'm Lune.
     What's rattling around in that head of yours?
    ____________________________________________________________

    ____________________________________________________________
     Added. One more thing to think about:
       [T][ ] read book
     That's 1 task(s) on the board now.
    ____________________________________________________________

    ____________________________________________________________
     Here's what you've got going on:
     1.[T][ ] read book
    ____________________________________________________________

    ____________________________________________________________
     Bye! Go forth and be marginally more organized.
    ____________________________________________________________
```

## Test Case 29: A repeated /by or /from is rejected with a specific error

**Aim:** Verify specifying the same parameter twice (e.g. two `/by` clauses) produces a clear "specified more than once" error, instead of the confusing "invalid date" error that would result from the second occurrence being swallowed into the date text.

**Input:**
```input
deadline return book /by 2019-01-01 /by 2019-02-01
event meeting /from 2019-01-01 /from 2019-01-02 /to 2019-01-03
list
bye
```

**Expected output:**
```expected
 _                     
| |   _   _ _ __   ___ 
| |  | | | | '_ \ / _ \
| |__| |_| | | | |  __/
|_____\__,_|_| |_|\___|

    ____________________________________________________________
     Oh, hey — I'm Lune.
     What's rattling around in that head of yours?
    ____________________________________________________________

    ____________________________________________________________
     Ugh, you specified /by more than once... try: deadline <what to do> /by <date>
    ____________________________________________________________

    ____________________________________________________________
     Ugh, you specified /from more than once... try: event <what to do> /from <date> /to <date>
    ____________________________________________________________

    ____________________________________________________________
     Here's what you've got going on:
    ____________________________________________________________

    ____________________________________________________________
     Bye! Go forth and be marginally more organized.
    ____________________________________________________________
```

## Test Case 30: An event's /to can't be before /from, but can equal it

**Aim:** Verify an event whose /to is chronologically before /from is rejected, while an event whose /to equals /from (a single-day event) is still accepted — this is a regression guard for that existing, separately-tested feature.

**Input:**
```input
event meeting /from 2019-10-15 /to 2019-10-10
event standup /from 2019-10-15 /to 2019-10-15
list
bye
```

**Expected output:**
```expected
 _                     
| |   _   _ _ __   ___ 
| |  | | | | '_ \ / _ \
| |__| |_| | | | |  __/
|_____\__,_|_| |_|\___|

    ____________________________________________________________
     Oh, hey — I'm Lune.
     What's rattling around in that head of yours?
    ____________________________________________________________

    ____________________________________________________________
     Ugh, an event's /to can't be before its /from...
    ____________________________________________________________

    ____________________________________________________________
     Added. One more thing to think about:
       [E][ ] standup (from: Oct 15 2019 to: Oct 15 2019)
     That's 1 task(s) on the board now.
    ____________________________________________________________

    ____________________________________________________________
     Here's what you've got going on:
     1.[E][ ] standup (from: Oct 15 2019 to: Oct 15 2019)
    ____________________________________________________________

    ____________________________________________________________
     Bye! Go forth and be marginally more organized.
    ____________________________________________________________
```

## Test Case 31: Adding a duplicate task is rejected, scoped by type

**Aim:** Verify adding a second todo with the same description (any case) is rejected, but a deadline with that same description is not considered a duplicate of it, since duplicate detection is scoped per task type.

**Input:**
```input
todo read book
todo Read Book
deadline read book /by 2019-10-15
list
bye
```

**Expected output:**
```expected
 _                     
| |   _   _ _ __   ___ 
| |  | | | | '_ \ / _ \
| |__| |_| | | | |  __/
|_____\__,_|_| |_|\___|

    ____________________________________________________________
     Oh, hey — I'm Lune.
     What's rattling around in that head of yours?
    ____________________________________________________________

    ____________________________________________________________
     Added. One more thing to think about:
       [T][ ] read book
     That's 1 task(s) on the board now.
    ____________________________________________________________

    ____________________________________________________________
     Ugh, you already have a todo like that: Read Book
    ____________________________________________________________

    ____________________________________________________________
     Added. One more thing to think about:
       [D][ ] read book (by: Oct 15 2019)
     That's 2 task(s) on the board now.
    ____________________________________________________________

    ____________________________________________________________
     Here's what you've got going on:
     1.[T][ ] read book
     2.[D][ ] read book (by: Oct 15 2019)
    ____________________________________________________________

    ____________________________________________________________
     Bye! Go forth and be marginally more organized.
    ____________________________________________________________
```

## Test Case 32: A description containing the save-file delimiter is rejected

**Aim:** Verify a description containing the literal `" | "` save-file delimiter is rejected up front, rather than being silently accepted and corrupting the save file (the previous, documented behavior).

**Input:**
```input
todo read | book
list
bye
```

**Expected output:**
```expected
 _                     
| |   _   _ _ __   ___ 
| |  | | | | '_ \ / _ \
| |__| |_| | | | |  __/
|_____\__,_|_| |_|\___|

    ____________________________________________________________
     Oh, hey — I'm Lune.
     What's rattling around in that head of yours?
    ____________________________________________________________

    ____________________________________________________________
     Ugh, a description can't contain " | "... try phrasing it differently.
    ____________________________________________________________

    ____________________________________________________________
     Here's what you've got going on:
    ____________________________________________________________

    ____________________________________________________________
     Bye! Go forth and be marginally more organized.
    ____________________________________________________________
```

## Test Case 33: Non-existent dates are rejected in both accepted formats

**Aim:** Verify a non-existent calendar date (Feb 30) is rejected whether given in `yyyy-mm-dd` or `d/m/yyyy HHmm` form, rather than being silently normalized to a nearby valid date.

**Input:**
```input
deadline return book /by 2019-02-30
deadline return book /by 30/2/2019 1800
list
bye
```

**Expected output:**
```expected
 _                     
| |   _   _ _ __   ___ 
| |  | | | | '_ \ / _ \
| |__| |_| | | | |  __/
|_____\__,_|_| |_|\___|

    ____________________________________________________________
     Oh, hey — I'm Lune.
     What's rattling around in that head of yours?
    ____________________________________________________________

    ____________________________________________________________
     Ugh, "2019-02-30" isn't a valid /by date/time... use yyyy-mm-dd (e.g. 2019-10-15) or d/m/yyyy HHmm (e.g. 2/12/2019 1800).
    ____________________________________________________________

    ____________________________________________________________
     Ugh, "30/2/2019 1800" isn't a valid /by date/time... use yyyy-mm-dd (e.g. 2019-10-15) or d/m/yyyy HHmm (e.g. 2/12/2019 1800).
    ____________________________________________________________

    ____________________________________________________________
     Here's what you've got going on:
    ____________________________________________________________

    ____________________________________________________________
     Bye! Go forth and be marginally more organized.
    ____________________________________________________________
```
