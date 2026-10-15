# Thomas text-UI test plan

Test cases for the chatbot's text user interface. Each case is run by the
`test-ui` skill, which feeds the case's input lines to the program on standard
input and compares the console output against the expected output.

## How a case is run

* Every case runs the program **from scratch** in its own process, in its own
  throwaway working directory. A case never sees tasks added by an earlier case,
  whether through memory or through the save file the chatbot writes to
  `./data`, so cases can be reordered or run one at a time without their results
  changing. A case that needs existing tasks adds them itself, as part of its
  own input.
* A case may give **more than one `**Input:**` block**. Each block is a separate
  run of the chatbot, and the runs share that one working directory, so a later
  run starts from the save file the earlier one left behind. The expected output
  is the runs' output end to end, greeting and farewell included for each. This
  is the only way to test saving and loading: a reload only happens when the
  program is started again.
* The comparison is line by line over the program's whole console output,
  including the greeting and the farewell. Indentation is compared; trailing
  spaces at the end of a line and blank lines at the end of the output are not.
* Anything the program writes to standard error (such as a stack trace) counts
  as part of the output, and a non-zero exit status fails the case.
* The session stops at the first failing case.

## How to add a case

Copy an existing case. A case is a `###` heading under `## Test cases` with
three parts: `**Aim:**`, an `**Input:**` code block, and an
`**Expected output:**` code block. In the expected output, a line reading
`{{GREETING}}` or `{{FAREWELL}}` expands to the corresponding block below, so
the banner does not have to be repeated in every case.

A case that needs the chatbot restarted gives a second `**Input:**` block after
the first; its expected output then covers both runs in order, each with its own
greeting and farewell.

## Shared output blocks

### GREETING

```text
    ____________________________________________________________
       ________
      /_  __/ /_  ____  ____ ___  ____ ______
       / / / __ \/ __ \/ __ `__ \/ __ `/ ___/
      / / / / / / /_/ / / / / / / /_/ (__  )
     /_/ /_/ /_/\____/_/ /_/ /_/\__,_/____/
     Peep peep! Thomas the Tank Engine, reporting for duty!
     What shall we haul today?
    ____________________________________________________________
```

### FAREWELL

```text
    ____________________________________________________________
     Off to the sheds! Peep peep, see you down the line!
    ____________________________________________________________
```

## Test cases

### TC1: Greet and exit

**Aim:** The chatbot shows its banner on startup and says goodbye when the user
types `bye`, with nothing in between.

**Input:**

```text
bye
```

**Expected output:**

```text
{{GREETING}}
{{FAREWELL}}
```

### TC2: Added tasks are listed in the order they were added

**Aim:** `list` prints the stored tasks numbered from 1, in the order they were
added, each not done. Guards the numbering loop.

**Input:**

```text
todo read book
todo return book
list
bye
```

**Expected output:**

```text
{{GREETING}}
    ____________________________________________________________
     Coupled up! This wagon is on the train now:
        [T][ ] read book
     That's 1 wagon(s) behind me now.
    ____________________________________________________________
    ____________________________________________________________
     Coupled up! This wagon is on the train now:
        [T][ ] return book
     That's 2 wagon(s) behind me now.
    ____________________________________________________________
    ____________________________________________________________
     Here is every wagon on my train:
     1. [T][ ] read book
     2. [T][ ] return book
    ____________________________________________________________
{{FAREWELL}}
```

### TC3: List with no tasks stored

**Aim:** `list` before anything has been added prints the header and nothing
else, rather than failing. Guards the numbering loop against an empty task list,
and the header slot against an array sized for zero tasks.

**Input:**

```text
list
bye
```

**Expected output:**

```text
{{GREETING}}
    ____________________________________________________________
     Here is every wagon on my train:
    ____________________________________________________________
{{FAREWELL}}
```

### TC4: Mark a task as done

**Aim:** `mark <n>` sets task n's status to done, echoes the task back, and the
change is visible in a later `list`. Only the named task changes.

**Input:**

```text
todo read book
todo return book
mark 2
list
bye
```

**Expected output:**

```text
{{GREETING}}
    ____________________________________________________________
     Coupled up! This wagon is on the train now:
        [T][ ] read book
     That's 1 wagon(s) behind me now.
    ____________________________________________________________
    ____________________________________________________________
     Coupled up! This wagon is on the train now:
        [T][ ] return book
     That's 2 wagon(s) behind me now.
    ____________________________________________________________
    ____________________________________________________________
     Delivered, right on time! This wagon is done:
        [T][X] return book
    ____________________________________________________________
    ____________________________________________________________
     Here is every wagon on my train:
     1. [T][ ] read book
     2. [T][X] return book
    ____________________________________________________________
{{FAREWELL}}
```

### TC5: Unmark a task that was done

**Aim:** `unmark <n>` returns a task that was marked done to not done, and the
change is visible in a later `list`.

**Input:**

```text
todo read book
mark 1
unmark 1
list
bye
```

**Expected output:**

```text
{{GREETING}}
    ____________________________________________________________
     Coupled up! This wagon is on the train now:
        [T][ ] read book
     That's 1 wagon(s) behind me now.
    ____________________________________________________________
    ____________________________________________________________
     Delivered, right on time! This wagon is done:
        [T][X] read book
    ____________________________________________________________
    ____________________________________________________________
     Back on the train it goes! This wagon is not done yet:
        [T][ ] read book
    ____________________________________________________________
    ____________________________________________________________
     Here is every wagon on my train:
     1. [T][ ] read book
    ____________________________________________________________
{{FAREWELL}}
```

### TC6: Marking a task twice leaves it done

**Aim:** `mark` is not a toggle -- marking an already-done task keeps it done.
This is the behaviour that makes `unmark` the only way back.

**Input:**

```text
todo read book
mark 1
mark 1
list
bye
```

**Expected output:**

```text
{{GREETING}}
    ____________________________________________________________
     Coupled up! This wagon is on the train now:
        [T][ ] read book
     That's 1 wagon(s) behind me now.
    ____________________________________________________________
    ____________________________________________________________
     Delivered, right on time! This wagon is done:
        [T][X] read book
    ____________________________________________________________
    ____________________________________________________________
     Delivered, right on time! This wagon is done:
        [T][X] read book
    ____________________________________________________________
    ____________________________________________________________
     Here is every wagon on my train:
     1. [T][X] read book
    ____________________________________________________________
{{FAREWELL}}
```

### TC7: Input ends without a `bye`

**Aim:** The chatbot still says goodbye when standard input runs out rather than
ending on a `bye`. Guards the `hasNextLine()` check that ends the read loop.

**Input:**

```text
todo read book
```

**Expected output:**

```text
{{GREETING}}
    ____________________________________________________________
     Coupled up! This wagon is on the train now:
        [T][ ] read book
     That's 1 wagon(s) behind me now.
    ____________________________________________________________
{{FAREWELL}}
```

### TC8: Add one task of each type and list them

**Aim:** `todo`, `deadline` and `event` each store a task of the matching type,
acknowledge it with the running task count, and show their own tag and date
details in a later `list`. The dates are typed as `yyyy-mm-dd HHmm` on a 24-hour
clock and shown back as `MMM dd yyyy, h:mm a` on a 12-hour one, so this also
guards the input and display formats being deliberately different ones.

**Input:**

```text
todo borrow book
deadline return book /by 2019-12-02 1800
event project meeting /from 2019-12-02 1400 /to 2019-12-04 1600
list
bye
```

**Expected output:**

```text
{{GREETING}}
    ____________________________________________________________
     Coupled up! This wagon is on the train now:
        [T][ ] borrow book
     That's 1 wagon(s) behind me now.
    ____________________________________________________________
    ____________________________________________________________
     Coupled up! This wagon is on the train now:
        [D][ ] return book (by: Dec 02 2019, 6:00 PM)
     That's 2 wagon(s) behind me now.
    ____________________________________________________________
    ____________________________________________________________
     Coupled up! This wagon is on the train now:
        [E][ ] project meeting (from: Dec 02 2019, 2:00 PM to: Dec 04 2019, 4:00 PM)
     That's 3 wagon(s) behind me now.
    ____________________________________________________________
    ____________________________________________________________
     Here is every wagon on my train:
     1. [T][ ] borrow book
     2. [D][ ] return book (by: Dec 02 2019, 6:00 PM)
     3. [E][ ] project meeting (from: Dec 02 2019, 2:00 PM to: Dec 04 2019, 4:00 PM)
    ____________________________________________________________
{{FAREWELL}}
```

### TC9: A `/by` that is not a date is rejected

**Aim:** A deadline is now a real date and time, not free text, so text that is
neither is reported rather than stored. Guards the `DateTimeParseException` from
`LocalDateTime.parse` being rethrown as a `ThomasException` instead of reaching
the user as a stack trace, and the message quoting back what was rejected.

The date is the last thing checked, so the whole `/by` text is passed to the
parser: this is also what shows that splitting on `/by` kept the spaces and
punctuation rather than trimming the argument down to its first word.

**Input:**

```text
deadline do homework /by no idea :-p
bye
```

**Expected output:**

```text
{{GREETING}}
    ____________________________________________________________
     I can't read 'no idea :-p' as a deadline date! My timetable wants a date and a 24-hour time, like 2019-12-02 1800.
    ____________________________________________________________
{{FAREWELL}}
```

### TC9a: A date given without a time is rejected

**Aim:** The time is required, not optional. A bare `yyyy-mm-dd` is refused
rather than being quietly taken to mean midnight, so a task never claims a time
the user did not choose. This is the case that separates "the date must parse"
from "the date and the time must both be there" -- a parser accepting the date
half alone would let this through and store `12:00 AM`.

**Input:**

```text
deadline return book /by 2019-12-02
bye
```

**Expected output:**

```text
{{GREETING}}
    ____________________________________________________________
     I can't read '2019-12-02' as a deadline date! My timetable wants a date and a 24-hour time, like 2019-12-02 1800.
    ____________________________________________________________
{{FAREWELL}}
```

### TC9b: A near-miss date is rejected as firmly as nonsense

**Aim:** Only the one accepted form gets through -- a date written the other way
round, or naming a month that does not exist, is refused rather than guessed at.
`2/12/2019 1800` is the shape the user is most likely to reach for, and
`2019-13-01 1800` matches the pattern exactly while naming a thirteenth month,
which a check on shape alone would let through. The two earn different
answers: the first is shown the format, the second has already matched it and
is told the moment does not exist instead.

**Input:**

```text
deadline return book /by 2/12/2019 1800
deadline return book /by 2019-13-01 1800
list
bye
```

**Expected output:**

```text
{{GREETING}}
    ____________________________________________________________
     I can't read '2/12/2019 1800' as a deadline date! My timetable wants a date and a 24-hour time, like 2019-12-02 1800.
    ____________________________________________________________
    ____________________________________________________________
     There's no such moment as '2019-13-01 1800' for a deadline date! Check the day is on the calendar and the time is on the 24-hour clock, 0000 to 2359.
    ____________________________________________________________
    ____________________________________________________________
     Here is every wagon on my train:
    ____________________________________________________________
{{FAREWELL}}
```

### TC9c: A time outside the 24-hour clock is rejected

**Aim:** The time is read on a 24-hour clock, so `2500` is not a time and
`1860` is not a minute count. Guards the `HH` and `mm` halves of the pattern
against a value of the right width but the wrong range, which is what a check
counting only digits would miss.

**Input:**

```text
deadline return book /by 2019-12-02 2500
deadline return book /by 2019-12-02 1860
bye
```

**Expected output:**

```text
{{GREETING}}
    ____________________________________________________________
     There's no such moment as '2019-12-02 2500' for a deadline date! Check the day is on the calendar and the time is on the 24-hour clock, 0000 to 2359.
    ____________________________________________________________
    ____________________________________________________________
     There's no such moment as '2019-12-02 1860' for a deadline date! Check the day is on the calendar and the time is on the 24-hour clock, 0000 to 2359.
    ____________________________________________________________
{{FAREWELL}}
```

### TC9d: An event's start and end are reported separately when unreadable

**Aim:** The two dates of an event are named individually in the error, so the
user is told which one to fix. Guards the `field` argument passed to the date
parser at each of its call sites; one shared wording would make these two
messages identical.

**Input:**

```text
event project meeting /from Mon 2pm /to 2019-12-04 1600
event project meeting /from 2019-12-02 1400 /to 4pm
bye
```

**Expected output:**

```text
{{GREETING}}
    ____________________________________________________________
     I can't read 'Mon 2pm' as a start date! My timetable wants a date and a 24-hour time, like 2019-12-02 1800.
    ____________________________________________________________
    ____________________________________________________________
     I can't read '4pm' as an end date! My timetable wants a date and a 24-hour time, like 2019-12-02 1800.
    ____________________________________________________________
{{FAREWELL}}
```

### TC9e: Midnight and noon are shown the right way round

**Aim:** `0000` and `1200` are the two values a 24-hour to 12-hour conversion
gets wrong most easily -- midnight is `12:00 AM`, not `0:00 AM`, and noon is
`12:00 PM`, not `12:00 AM`. Guards the `h` and `a` halves of the display format
at the only two points where they disagree with the input.

**Input:**

```text
deadline sleep /by 2019-12-02 0000
deadline lunch /by 2019-12-02 1200
list
bye
```

**Expected output:**

```text
{{GREETING}}
    ____________________________________________________________
     Coupled up! This wagon is on the train now:
        [D][ ] sleep (by: Dec 02 2019, 12:00 AM)
     That's 1 wagon(s) behind me now.
    ____________________________________________________________
    ____________________________________________________________
     Coupled up! This wagon is on the train now:
        [D][ ] lunch (by: Dec 02 2019, 12:00 PM)
     That's 2 wagon(s) behind me now.
    ____________________________________________________________
    ____________________________________________________________
     Here is every wagon on my train:
     1. [D][ ] sleep (by: Dec 02 2019, 12:00 AM)
     2. [D][ ] lunch (by: Dec 02 2019, 12:00 PM)
    ____________________________________________________________
{{FAREWELL}}
```

### TC10: Marking a typed task keeps its type

**Aim:** `mark` works the same on the new task types, and the type tag survives
the status change -- the tag comes from the subclass, the `[X]` from `Task`.

**Input:**

```text
todo join sports club
mark 1
list
bye
```

**Expected output:**

```text
{{GREETING}}
    ____________________________________________________________
     Coupled up! This wagon is on the train now:
        [T][ ] join sports club
     That's 1 wagon(s) behind me now.
    ____________________________________________________________
    ____________________________________________________________
     Delivered, right on time! This wagon is done:
        [T][X] join sports club
    ____________________________________________________________
    ____________________________________________________________
     Here is every wagon on my train:
     1. [T][X] join sports club
    ____________________________________________________________
{{FAREWELL}}
```

### TC11: An unknown command is rejected

**Aim:** A command that is not a known keyword is reported as unknown rather
than stored as a task. Guards the `else` at the end of the command chain.

**Input:**

```text
blah
bye
```

**Expected output:**

```text
{{GREETING}}
    ____________________________________________________________
     Cinders and ashes! I don't know that signal. What does it mean?
    ____________________________________________________________
{{FAREWELL}}
```

### TC12: A todo with no description is rejected

**Aim:** `todo` typed on its own is reported instead of crashing on the missing
argument. Guards the `parts.length < 2` check before `parts[1]` is read.

**Input:**

```text
todo
bye
```

**Expected output:**

```text
{{GREETING}}
    ____________________________________________________________
     Bust my buffers! A todo needs a description before I can couple it up.
    ____________________________________________________________
{{FAREWELL}}
```

### TC13: A todo whose description is only spaces is rejected

**Aim:** A description of nothing but spaces is treated as empty. This is the
case the length check alone misses -- `todo    ` splits into two parts, so only
the `isBlank()` half of the guard catches it.

**Input:**

```text
todo    
bye
```

**Expected output:**

```text
{{GREETING}}
    ____________________________________________________________
     Bust my buffers! A todo needs a description before I can couple it up.
    ____________________________________________________________
{{FAREWELL}}
```

### TC14: A deadline with no `/by` is rejected

**Aim:** A deadline missing its due date is reported rather than crashing on
the second half of the split. Guards `details.length < 2`.

**Input:**

```text
deadline return book
bye
```

**Expected output:**

```text
{{GREETING}}
    ____________________________________________________________
     When is it due? A deadline needs a /by before I can pull it.
    ____________________________________________________________
{{FAREWELL}}
```

### TC15: An event with neither marker is rejected

**Aim:** `event` with no `/from` at all is reported. This is the case an
equality check on the split length misses: with no marker the split yields one
part, not two, so the guard has to be `<` rather than `==`.

**Input:**

```text
event project meeting
bye
```

**Expected output:**

```text
{{GREETING}}
    ____________________________________________________________
     When does it set off? An event needs a /from.
    ____________________________________________________________
{{FAREWELL}}
```

### TC16: An event with `/from` but no `/to` is rejected

**Aim:** A missing end date is reported specifically, rather than being lumped
in with a missing start date. Guards the second of the two splits.

The start given here, `Mon 2pm`, is not a valid date either, so this also pins
down the order of the checks: the missing `/to` is reported first because the
dates are only parsed once both markers have been found. Reordering those would
show the "I can't read 'Mon 2pm'" message instead and hide the real mistake.

**Input:**

```text
event project meeting /from Mon 2pm
bye
```

**Expected output:**

```text
{{GREETING}}
    ____________________________________________________________
     When does it arrive? An event needs a /to after its /from.
    ____________________________________________________________
{{FAREWELL}}
```

### TC17: `mark` with a non-numeric argument is rejected

**Aim:** A task number that is not a number is reported. Guards the
`NumberFormatException` from `Integer.parseInt` being rethrown as a
`ThomasException` instead of reaching the user as a stack trace.

**Input:**

```text
mark abc
bye
```

**Expected output:**

```text
{{GREETING}}
    ____________________________________________________________
     Bust my buffers! That's not a number. My wagons are numbered 1, 2, 3...
    ____________________________________________________________
{{FAREWELL}}
```

### TC18: `mark` past the end of the list is rejected

**Aim:** A task number higher than the number of stored tasks is reported. The
range is checked against the tasks that exist, not the array length -- task 9 is
a valid index into the 100-slot array but holds nothing, so a check against the
array would fail later with a `NullPointerException`.

**Input:**

```text
todo read book
mark 9
bye
```

**Expected output:**

```text
{{GREETING}}
    ____________________________________________________________
     Coupled up! This wagon is on the train now:
        [T][ ] read book
     That's 1 wagon(s) behind me now.
    ____________________________________________________________
    ____________________________________________________________
     There's no wagon 9 on my train! I'm only pulling 1 wagon(s).
    ____________________________________________________________
{{FAREWELL}}
```

### TC19: `mark 0` is rejected

**Aim:** Task numbers start at 1, so 0 is out of range. Guards the lower half
of the range check, which a check for "too high" alone would let through into a
negative array index.

**Input:**

```text
todo read book
mark 0
bye
```

**Expected output:**

```text
{{GREETING}}
    ____________________________________________________________
     Coupled up! This wagon is on the train now:
        [T][ ] read book
     That's 1 wagon(s) behind me now.
    ____________________________________________________________
    ____________________________________________________________
     There's no wagon 0 on my train! I'm only pulling 1 wagon(s).
    ____________________________________________________________
{{FAREWELL}}
```

### TC20: The chatbot keeps going after an error

**Aim:** An error ends only the command that caused it. The next command is
read and carried out normally, which is the point of catching the exception
inside the read loop rather than outside it.

**Input:**

```text
blah
todo read book
list
bye
```

**Expected output:**

```text
{{GREETING}}
    ____________________________________________________________
     Cinders and ashes! I don't know that signal. What does it mean?
    ____________________________________________________________
    ____________________________________________________________
     Coupled up! This wagon is on the train now:
        [T][ ] read book
     That's 1 wagon(s) behind me now.
    ____________________________________________________________
    ____________________________________________________________
     Here is every wagon on my train:
     1. [T][ ] read book
    ____________________________________________________________
{{FAREWELL}}
```

### TC21: A rejected command does not change the task list

**Aim:** A command that throws stores nothing and leaves the count alone --
`taskCount++` sits after the branch that can throw, so a later `list` shows
only the tasks that were accepted.

**Input:**

```text
todo read book
deadline return book
event project meeting
todo
list
bye
```

**Expected output:**

```text
{{GREETING}}
    ____________________________________________________________
     Coupled up! This wagon is on the train now:
        [T][ ] read book
     That's 1 wagon(s) behind me now.
    ____________________________________________________________
    ____________________________________________________________
     When is it due? A deadline needs a /by before I can pull it.
    ____________________________________________________________
    ____________________________________________________________
     When does it set off? An event needs a /from.
    ____________________________________________________________
    ____________________________________________________________
     Bust my buffers! A todo needs a description before I can couple it up.
    ____________________________________________________________
    ____________________________________________________________
     Here is every wagon on my train:
     1. [T][ ] read book
    ____________________________________________________________
{{FAREWELL}}
```

### TC22: A rejected add does not disturb the numbering of later tasks

**Aim:** A failed add between two successful ones leaves no gap. The second
accepted task becomes task 2, not task 3 -- `taskCount` is only advanced by an
add that completed.

**Input:**

```text
todo read book
deadline return book
todo join sports club
list
bye
```

**Expected output:**

```text
{{GREETING}}
    ____________________________________________________________
     Coupled up! This wagon is on the train now:
        [T][ ] read book
     That's 1 wagon(s) behind me now.
    ____________________________________________________________
    ____________________________________________________________
     When is it due? A deadline needs a /by before I can pull it.
    ____________________________________________________________
    ____________________________________________________________
     Coupled up! This wagon is on the train now:
        [T][ ] join sports club
     That's 2 wagon(s) behind me now.
    ____________________________________________________________
    ____________________________________________________________
     Here is every wagon on my train:
     1. [T][ ] read book
     2. [T][ ] join sports club
    ____________________________________________________________
{{FAREWELL}}
```

### TC23: A rejected `mark` leaves every task's status alone

**Aim:** A successful `mark` followed by two failing ones changes only the task
that was named. Guards against a rejected command touching a task's `isDone` on
its way to being rejected.

**Input:**

```text
todo read book
todo return book
mark 1
mark 5
mark abc
list
bye
```

**Expected output:**

```text
{{GREETING}}
    ____________________________________________________________
     Coupled up! This wagon is on the train now:
        [T][ ] read book
     That's 1 wagon(s) behind me now.
    ____________________________________________________________
    ____________________________________________________________
     Coupled up! This wagon is on the train now:
        [T][ ] return book
     That's 2 wagon(s) behind me now.
    ____________________________________________________________
    ____________________________________________________________
     Delivered, right on time! This wagon is done:
        [T][X] read book
    ____________________________________________________________
    ____________________________________________________________
     There's no wagon 5 on my train! I'm only pulling 2 wagon(s).
    ____________________________________________________________
    ____________________________________________________________
     Bust my buffers! That's not a number. My wagons are numbered 1, 2, 3...
    ____________________________________________________________
    ____________________________________________________________
     Here is every wagon on my train:
     1. [T][X] read book
     2. [T][ ] return book
    ____________________________________________________________
{{FAREWELL}}
```

### TC24: A rejected `unmark` leaves a done task done

**Aim:** The mirror of TC23 for `unmark` -- a task marked done stays done when a
later `unmark` is rejected, whether the number is out of range or missing.

**Input:**

```text
todo read book
mark 1
unmark 9
unmark
list
bye
```

**Expected output:**

```text
{{GREETING}}
    ____________________________________________________________
     Coupled up! This wagon is on the train now:
        [T][ ] read book
     That's 1 wagon(s) behind me now.
    ____________________________________________________________
    ____________________________________________________________
     Delivered, right on time! This wagon is done:
        [T][X] read book
    ____________________________________________________________
    ____________________________________________________________
     There's no wagon 9 on my train! I'm only pulling 1 wagon(s).
    ____________________________________________________________
    ____________________________________________________________
     Which wagon do you want me to unmark? Give me its number.
    ____________________________________________________________
    ____________________________________________________________
     Here is every wagon on my train:
     1. [T][X] read book
    ____________________________________________________________
{{FAREWELL}}
```

### TC25: A task number rejected as out of range is accepted once the task exists

**Aim:** The range check follows the real task count rather than a stale one.
The same `mark 2` fails with one task stored and succeeds after a second is
added, and the message reports the count at the time it was refused.

**Input:**

```text
todo read book
mark 2
todo return book
mark 2
list
bye
```

**Expected output:**

```text
{{GREETING}}
    ____________________________________________________________
     Coupled up! This wagon is on the train now:
        [T][ ] read book
     That's 1 wagon(s) behind me now.
    ____________________________________________________________
    ____________________________________________________________
     There's no wagon 2 on my train! I'm only pulling 1 wagon(s).
    ____________________________________________________________
    ____________________________________________________________
     Coupled up! This wagon is on the train now:
        [T][ ] return book
     That's 2 wagon(s) behind me now.
    ____________________________________________________________
    ____________________________________________________________
     Delivered, right on time! This wagon is done:
        [T][X] return book
    ____________________________________________________________
    ____________________________________________________________
     Here is every wagon on my train:
     1. [T][ ] read book
     2. [T][X] return book
    ____________________________________________________________
{{FAREWELL}}
```

### TC26: Marker words inside a description are left alone

**Aim:** A description may contain `/by`, and a word may contain the letters
"by", without either being mistaken for a marker. Guards the choice to split on
`" /by "` with its surrounding spaces rather than on `"by"` or `"/by"`.

**Input:**

```text
todo read book /by tomorrow
deadline standby report /by 2019-12-06 0900
list
bye
```

**Expected output:**

```text
{{GREETING}}
    ____________________________________________________________
     Coupled up! This wagon is on the train now:
        [T][ ] read book /by tomorrow
     That's 1 wagon(s) behind me now.
    ____________________________________________________________
    ____________________________________________________________
     Coupled up! This wagon is on the train now:
        [D][ ] standby report (by: Dec 06 2019, 9:00 AM)
     That's 2 wagon(s) behind me now.
    ____________________________________________________________
    ____________________________________________________________
     Here is every wagon on my train:
     1. [T][ ] read book /by tomorrow
     2. [D][ ] standby report (by: Dec 06 2019, 9:00 AM)
    ____________________________________________________________
{{FAREWELL}}
```

### TC27: Extra spaces around arguments are accepted

**Aim:** Spare spaces after a keyword are not an error -- they are trimmed off.
Guards the `trim()` before `Integer.parseInt`, which rejects a number with
spaces around it, and the `trim()` that keeps a description from being stored
with a leading space.

**Input:**

```text
todo    read book
mark  1
list
bye
```

**Expected output:**

```text
{{GREETING}}
    ____________________________________________________________
     Coupled up! This wagon is on the train now:
        [T][ ] read book
     That's 1 wagon(s) behind me now.
    ____________________________________________________________
    ____________________________________________________________
     Delivered, right on time! This wagon is done:
        [T][X] read book
    ____________________________________________________________
    ____________________________________________________________
     Here is every wagon on my train:
     1. [T][X] read book
    ____________________________________________________________
{{FAREWELL}}
```

### TC28: A long session of mixed valid and invalid commands ends in the right state

**Aim:** Errors of every kind, interleaved with commands that succeed, leave
the task list exactly as the successful commands built it. This is the case
that would catch an error path corrupting state in a way the single-command
cases miss.

**Input:**

```text
todo read book
event project meeting /from Mon 2pm
deadline return book /by 2019-12-02 1800
blah
mark 2
todo
unmark 2
mark 9
list
bye
```

**Expected output:**

```text
{{GREETING}}
    ____________________________________________________________
     Coupled up! This wagon is on the train now:
        [T][ ] read book
     That's 1 wagon(s) behind me now.
    ____________________________________________________________
    ____________________________________________________________
     When does it arrive? An event needs a /to after its /from.
    ____________________________________________________________
    ____________________________________________________________
     Coupled up! This wagon is on the train now:
        [D][ ] return book (by: Dec 02 2019, 6:00 PM)
     That's 2 wagon(s) behind me now.
    ____________________________________________________________
    ____________________________________________________________
     Cinders and ashes! I don't know that signal. What does it mean?
    ____________________________________________________________
    ____________________________________________________________
     Delivered, right on time! This wagon is done:
        [D][X] return book (by: Dec 02 2019, 6:00 PM)
    ____________________________________________________________
    ____________________________________________________________
     Bust my buffers! A todo needs a description before I can couple it up.
    ____________________________________________________________
    ____________________________________________________________
     Back on the train it goes! This wagon is not done yet:
        [D][ ] return book (by: Dec 02 2019, 6:00 PM)
    ____________________________________________________________
    ____________________________________________________________
     There's no wagon 9 on my train! I'm only pulling 2 wagon(s).
    ____________________________________________________________
    ____________________________________________________________
     Here is every wagon on my train:
     1. [T][ ] read book
     2. [D][ ] return book (by: Dec 02 2019, 6:00 PM)
    ____________________________________________________________
{{FAREWELL}}
```

### TC29: A trailing space does not stop `bye` from exiting

**Aim:** `bye ` ends the session like `bye`. Guards the match being made on the
command keyword rather than on the whole input line: a trailing space is
invisible to the user, so matching the whole line rejected a command they had
typed correctly. A command after the `bye ` proves the loop really ended.

**Input:**

```text
bye 
todo this must not be added
```

**Expected output:**

```text
{{GREETING}}
{{FAREWELL}}
```

### TC30: A trailing space does not stop `list` from listing

**Aim:** The same for `list `, which was the other command matched on the whole
line. It lists rather than being reported as unknown.

**Input:**

```text
todo read book
list 
bye
```

**Expected output:**

```text
{{GREETING}}
    ____________________________________________________________
     Coupled up! This wagon is on the train now:
        [T][ ] read book
     That's 1 wagon(s) behind me now.
    ____________________________________________________________
    ____________________________________________________________
     Here is every wagon on my train:
     1. [T][ ] read book
    ____________________________________________________________
{{FAREWELL}}
```

### TC31: Delete a task from the middle of the list

**Aim:** `delete <n>` removes task n, echoes back the task it removed, and
reports the new size. The tasks after it close up, so a later `list` numbers
them contiguously with no gap where the removed task was.

**Input:**

```text
todo read book
deadline return book /by 2019-12-02 1800
event project meeting /from 2019-12-02 1400 /to 2019-12-04 1600
todo join sports club
delete 3
list
bye
```

**Expected output:**

```text
{{GREETING}}
    ____________________________________________________________
     Coupled up! This wagon is on the train now:
        [T][ ] read book
     That's 1 wagon(s) behind me now.
    ____________________________________________________________
    ____________________________________________________________
     Coupled up! This wagon is on the train now:
        [D][ ] return book (by: Dec 02 2019, 6:00 PM)
     That's 2 wagon(s) behind me now.
    ____________________________________________________________
    ____________________________________________________________
     Coupled up! This wagon is on the train now:
        [E][ ] project meeting (from: Dec 02 2019, 2:00 PM to: Dec 04 2019, 4:00 PM)
     That's 3 wagon(s) behind me now.
    ____________________________________________________________
    ____________________________________________________________
     Coupled up! This wagon is on the train now:
        [T][ ] join sports club
     That's 4 wagon(s) behind me now.
    ____________________________________________________________
    ____________________________________________________________
     Uncoupled! I've left this wagon in the siding:
        [E][ ] project meeting (from: Dec 02 2019, 2:00 PM to: Dec 04 2019, 4:00 PM)
     That's 3 wagon(s) behind me now.
    ____________________________________________________________
    ____________________________________________________________
     Here is every wagon on my train:
     1. [T][ ] read book
     2. [D][ ] return book (by: Dec 02 2019, 6:00 PM)
     3. [T][ ] join sports club
    ____________________________________________________________
{{FAREWELL}}
```

### TC32: Deleting renumbers the tasks that follow

**Aim:** After a delete, the numbers shift down, so `mark 2` names what used to
be task 3. This is what would break if a delete left a hole rather than closing
the list up.

**Input:**

```text
todo read book
todo return book
todo join sports club
delete 1
mark 2
list
bye
```

**Expected output:**

```text
{{GREETING}}
    ____________________________________________________________
     Coupled up! This wagon is on the train now:
        [T][ ] read book
     That's 1 wagon(s) behind me now.
    ____________________________________________________________
    ____________________________________________________________
     Coupled up! This wagon is on the train now:
        [T][ ] return book
     That's 2 wagon(s) behind me now.
    ____________________________________________________________
    ____________________________________________________________
     Coupled up! This wagon is on the train now:
        [T][ ] join sports club
     That's 3 wagon(s) behind me now.
    ____________________________________________________________
    ____________________________________________________________
     Uncoupled! I've left this wagon in the siding:
        [T][ ] read book
     That's 2 wagon(s) behind me now.
    ____________________________________________________________
    ____________________________________________________________
     Delivered, right on time! This wagon is done:
        [T][X] join sports club
    ____________________________________________________________
    ____________________________________________________________
     Here is every wagon on my train:
     1. [T][ ] return book
     2. [T][X] join sports club
    ____________________________________________________________
{{FAREWELL}}
```

### TC33: Delete the only task, leaving the list empty

**Aim:** Deleting down to nothing leaves a list that still works rather than a
list in a broken state. Guards the boundary where the count reaches zero.

**Input:**

```text
todo read book
delete 1
list
bye
```

**Expected output:**

```text
{{GREETING}}
    ____________________________________________________________
     Coupled up! This wagon is on the train now:
        [T][ ] read book
     That's 1 wagon(s) behind me now.
    ____________________________________________________________
    ____________________________________________________________
     Uncoupled! I've left this wagon in the siding:
        [T][ ] read book
     That's 0 wagon(s) behind me now.
    ____________________________________________________________
    ____________________________________________________________
     Here is every wagon on my train:
    ____________________________________________________________
{{FAREWELL}}
```

### TC34: `delete` with a bad task number is rejected

**Aim:** `delete` validates its argument the same way `mark` and `unmark` do --
missing, non-numeric, and out of range are each reported, and none of them
removes anything. The closing `list` shows the task still there.

**Input:**

```text
todo read book
delete
delete abc
delete 9
delete 0
list
bye
```

**Expected output:**

```text
{{GREETING}}
    ____________________________________________________________
     Coupled up! This wagon is on the train now:
        [T][ ] read book
     That's 1 wagon(s) behind me now.
    ____________________________________________________________
    ____________________________________________________________
     Which wagon do you want me to delete? Give me its number.
    ____________________________________________________________
    ____________________________________________________________
     Bust my buffers! That's not a number. My wagons are numbered 1, 2, 3...
    ____________________________________________________________
    ____________________________________________________________
     There's no wagon 9 on my train! I'm only pulling 1 wagon(s).
    ____________________________________________________________
    ____________________________________________________________
     There's no wagon 0 on my train! I'm only pulling 1 wagon(s).
    ____________________________________________________________
    ____________________________________________________________
     Here is every wagon on my train:
     1. [T][ ] read book
    ____________________________________________________________
{{FAREWELL}}
```

### TC35: Deleting from an empty list is rejected

**Aim:** `delete 1` with nothing stored is reported rather than removing from an
empty list. Guards the range check when the count is zero, where every task
number is out of range.

**Input:**

```text
delete 1
bye
```

**Expected output:**

```text
{{GREETING}}
    ____________________________________________________________
     There's no wagon 1 on my train! I'm only pulling 0 wagon(s).
    ____________________________________________________________
{{FAREWELL}}
```

### TC36: Adding after a delete continues from the new count

**Aim:** A task added after a delete is numbered from the shortened list, not
from a count that only ever grows. Interleaves adds and deletes so a count kept
separately from the list would drift out of step.

**Input:**

```text
todo read book
todo return book
delete 1
todo join sports club
list
bye
```

**Expected output:**

```text
{{GREETING}}
    ____________________________________________________________
     Coupled up! This wagon is on the train now:
        [T][ ] read book
     That's 1 wagon(s) behind me now.
    ____________________________________________________________
    ____________________________________________________________
     Coupled up! This wagon is on the train now:
        [T][ ] return book
     That's 2 wagon(s) behind me now.
    ____________________________________________________________
    ____________________________________________________________
     Uncoupled! I've left this wagon in the siding:
        [T][ ] read book
     That's 1 wagon(s) behind me now.
    ____________________________________________________________
    ____________________________________________________________
     Coupled up! This wagon is on the train now:
        [T][ ] join sports club
     That's 2 wagon(s) behind me now.
    ____________________________________________________________
    ____________________________________________________________
     Here is every wagon on my train:
     1. [T][ ] return book
     2. [T][ ] join sports club
    ____________________________________________________________
{{FAREWELL}}
```

### TC37: A deleted task's done state does not follow its number

**Aim:** Deleting a task shifts the ones after it without disturbing their
completion state. Task 2 is marked done, task 1 is removed, and the task that
becomes 1 is still the one that was marked.

**Input:**

```text
todo read book
todo return book
mark 2
delete 1
list
bye
```

**Expected output:**

```text
{{GREETING}}
    ____________________________________________________________
     Coupled up! This wagon is on the train now:
        [T][ ] read book
     That's 1 wagon(s) behind me now.
    ____________________________________________________________
    ____________________________________________________________
     Coupled up! This wagon is on the train now:
        [T][ ] return book
     That's 2 wagon(s) behind me now.
    ____________________________________________________________
    ____________________________________________________________
     Delivered, right on time! This wagon is done:
        [T][X] return book
    ____________________________________________________________
    ____________________________________________________________
     Uncoupled! I've left this wagon in the siding:
        [T][ ] read book
     That's 1 wagon(s) behind me now.
    ____________________________________________________________
    ____________________________________________________________
     Here is every wagon on my train:
     1. [T][X] return book
    ____________________________________________________________
{{FAREWELL}}
```

### TC38: Tasks are still there after the chatbot is restarted

**Aim:** Tasks added in one run are saved and loaded back by the next, so the
list survives the program exiting. This is the whole point of the save file, and
it needs two runs to show at all.

**Input:**

```text
todo read book
deadline return book /by 2019-12-02 1800
bye
```

**Input:**

```text
list
bye
```

**Expected output:**

```text
{{GREETING}}
    ____________________________________________________________
     Coupled up! This wagon is on the train now:
        [T][ ] read book
     That's 1 wagon(s) behind me now.
    ____________________________________________________________
    ____________________________________________________________
     Coupled up! This wagon is on the train now:
        [D][ ] return book (by: Dec 02 2019, 6:00 PM)
     That's 2 wagon(s) behind me now.
    ____________________________________________________________
{{FAREWELL}}
{{GREETING}}
    ____________________________________________________________
     Here is every wagon on my train:
     1. [T][ ] read book
     2. [D][ ] return book (by: Dec 02 2019, 6:00 PM)
    ____________________________________________________________
{{FAREWELL}}
```

### TC39: A task's done state survives a restart

**Aim:** `mark` is saved as well as the task itself, so a task marked done in
one run comes back done. Guards the done flag being written to the save file and
read back, rather than every loaded task starting out not done.

**Input:**

```text
todo read book
todo return book
mark 2
bye
```

**Input:**

```text
list
bye
```

**Expected output:**

```text
{{GREETING}}
    ____________________________________________________________
     Coupled up! This wagon is on the train now:
        [T][ ] read book
     That's 1 wagon(s) behind me now.
    ____________________________________________________________
    ____________________________________________________________
     Coupled up! This wagon is on the train now:
        [T][ ] return book
     That's 2 wagon(s) behind me now.
    ____________________________________________________________
    ____________________________________________________________
     Delivered, right on time! This wagon is done:
        [T][X] return book
    ____________________________________________________________
{{FAREWELL}}
{{GREETING}}
    ____________________________________________________________
     Here is every wagon on my train:
     1. [T][ ] read book
     2. [T][X] return book
    ____________________________________________________________
{{FAREWELL}}
```

### TC40: A deleted task does not come back after a restart

**Aim:** `delete` saves the shortened list, so the removed task stays removed.
Guards the save being a fresh write of the whole list rather than an append,
which would leave the deleted task in the file.

**Input:**

```text
todo read book
todo return book
delete 1
bye
```

**Input:**

```text
list
bye
```

**Expected output:**

```text
{{GREETING}}
    ____________________________________________________________
     Coupled up! This wagon is on the train now:
        [T][ ] read book
     That's 1 wagon(s) behind me now.
    ____________________________________________________________
    ____________________________________________________________
     Coupled up! This wagon is on the train now:
        [T][ ] return book
     That's 2 wagon(s) behind me now.
    ____________________________________________________________
    ____________________________________________________________
     Uncoupled! I've left this wagon in the siding:
        [T][ ] read book
     That's 1 wagon(s) behind me now.
    ____________________________________________________________
{{FAREWELL}}
{{GREETING}}
    ____________________________________________________________
     Here is every wagon on my train:
     1. [T][ ] return book
    ____________________________________________________________
{{FAREWELL}}
```

### TC41: An event's start and end both survive a restart

**Aim:** A task type with more than one extra field round-trips completely.
Guards the save format's fourth and fifth fields being written and read back in
the right order -- swapping them would still load, and only the displayed times
would give it away.

**Input:**

```text
event project meeting /from 2019-12-02 1400 /to 2019-12-04 1600
bye
```

**Input:**

```text
list
bye
```

**Expected output:**

```text
{{GREETING}}
    ____________________________________________________________
     Coupled up! This wagon is on the train now:
        [E][ ] project meeting (from: Dec 02 2019, 2:00 PM to: Dec 04 2019, 4:00 PM)
     That's 1 wagon(s) behind me now.
    ____________________________________________________________
{{FAREWELL}}
{{GREETING}}
    ____________________________________________________________
     Here is every wagon on my train:
     1. [E][ ] project meeting (from: Dec 02 2019, 2:00 PM to: Dec 04 2019, 4:00 PM)
    ____________________________________________________________
{{FAREWELL}}
```

### TC42: A task is saved even when the session ends without a `bye`

**Aim:** Saving happens when the task is added, not when the program exits, so a
session that ends by input running out has still saved its work. Guards against
the save being attached to the `bye` path alone.

**Input:**

```text
todo read book
```

**Input:**

```text
list
bye
```

**Expected output:**

```text
{{GREETING}}
    ____________________________________________________________
     Coupled up! This wagon is on the train now:
        [T][ ] read book
     That's 1 wagon(s) behind me now.
    ____________________________________________________________
{{FAREWELL}}
{{GREETING}}
    ____________________________________________________________
     Here is every wagon on my train:
     1. [T][ ] read book
    ____________________________________________________________
{{FAREWELL}}
```

### TC43: A description containing the save file's field separator is rejected

**Aim:** A description holding `" | "` is refused as it is typed, by all three
add commands. Such a description cannot survive being saved -- it writes a line
with an extra field, which the loader refuses -- so accepting it would mean
confirming the task, listing it, and then losing it on the next start-up, with a
complaint about a save file line the user never knew existed. Refusing it while
the user is still looking at it turns a silent loss into something they can act
on.

The second run is what shows the loss is really prevented: the tasks that were
accepted all come back, and no skipped-line complaint appears, so nothing
unreadable was written.

Previously this case pinned down the opposite behaviour -- `todo a | b` was
accepted and skipped on reload -- and it was the only way to produce an
unreadable save file line through the UI. That path is now closed, so the
loader's skip-and-report behaviour is no longer reachable from the UI; it is
covered by `StorageTest` instead, and noted under "Not yet covered" below.

**Input:**

```text
todo a | b
deadline a | b /by 2019-12-02 1800
event a | b /from 2019-12-02 1400 /to 2019-12-04 1600
todo read book
bye
```

**Input:**

```text
list
bye
```

**Expected output:**

```text
{{GREETING}}
    ____________________________________________________________
     Bust my buffers! A description can't contain ' | ' -- that's how I keep my wagons apart in the save file.
    ____________________________________________________________
    ____________________________________________________________
     Bust my buffers! A description can't contain ' | ' -- that's how I keep my wagons apart in the save file.
    ____________________________________________________________
    ____________________________________________________________
     Bust my buffers! A description can't contain ' | ' -- that's how I keep my wagons apart in the save file.
    ____________________________________________________________
    ____________________________________________________________
     Coupled up! This wagon is on the train now:
        [T][ ] read book
     That's 1 wagon(s) behind me now.
    ____________________________________________________________
{{FAREWELL}}
{{GREETING}}
    ____________________________________________________________
     Here is every wagon on my train:
     1. [T][ ] read book
    ____________________________________________________________
{{FAREWELL}}
```

### TC43a: A pipe that is not the separator is still allowed

**Aim:** The boundary of TC43. Only `" | "` exactly, spaces and all, breaks the
save format; a bare pipe or one spaced on a single side splits into no extra
field and survives a restart unharmed. Guards the check being `contains(" | ")`
rather than something looser that would take away a character the format handles
perfectly well.

**Input:**

```text
todo a|b
todo c |d
todo e| f
bye
```

**Input:**

```text
list
bye
```

**Expected output:**

```text
{{GREETING}}
    ____________________________________________________________
     Coupled up! This wagon is on the train now:
        [T][ ] a|b
     That's 1 wagon(s) behind me now.
    ____________________________________________________________
    ____________________________________________________________
     Coupled up! This wagon is on the train now:
        [T][ ] c |d
     That's 2 wagon(s) behind me now.
    ____________________________________________________________
    ____________________________________________________________
     Coupled up! This wagon is on the train now:
        [T][ ] e| f
     That's 3 wagon(s) behind me now.
    ____________________________________________________________
{{FAREWELL}}
{{GREETING}}
    ____________________________________________________________
     Here is every wagon on my train:
     1. [T][ ] a|b
     2. [T][ ] c |d
     3. [T][ ] e| f
    ____________________________________________________________
{{FAREWELL}}
```

### TC44: `on` shows the deadlines and events falling on a day

**Aim:** `on <date>` lists the dated tasks that fall on that day and leaves out
the ones that do not. The numbers shown are the tasks' positions in the whole
list, not their positions among the matches, so the todos in between leave gaps:
task 2 and task 4 are numbered 2 and 4, not 1 and 2.

**Input:**

```text
todo borrow book
deadline return book /by 2019-12-02 1800
todo read book
event project meeting /from 2019-12-02 1400 /to 2019-12-04 1600
on 2019-12-02
bye
```

**Expected output:**

```text
{{GREETING}}
    ____________________________________________________________
     Coupled up! This wagon is on the train now:
        [T][ ] borrow book
     That's 1 wagon(s) behind me now.
    ____________________________________________________________
    ____________________________________________________________
     Coupled up! This wagon is on the train now:
        [D][ ] return book (by: Dec 02 2019, 6:00 PM)
     That's 2 wagon(s) behind me now.
    ____________________________________________________________
    ____________________________________________________________
     Coupled up! This wagon is on the train now:
        [T][ ] read book
     That's 3 wagon(s) behind me now.
    ____________________________________________________________
    ____________________________________________________________
     Coupled up! This wagon is on the train now:
        [E][ ] project meeting (from: Dec 02 2019, 2:00 PM to: Dec 04 2019, 4:00 PM)
     That's 4 wagon(s) behind me now.
    ____________________________________________________________
    ____________________________________________________________
     Here is my timetable for Dec 02 2019:
     2. [D][ ] return book (by: Dec 02 2019, 6:00 PM)
     4. [E][ ] project meeting (from: Dec 02 2019, 2:00 PM to: Dec 04 2019, 4:00 PM)
    ____________________________________________________________
{{FAREWELL}}
```

### TC45: An event counts as happening on every day it spans

**Aim:** An event covers its whole range, both ends included. The five `on`
commands walk the day before, the first day, a day in the middle, the last day,
and the day after, so the event is found on exactly three of them.

This is the case that catches the range test being written as
`day.isAfter(start) && day.isBefore(end)`: that reading drops the first and last
day, which a case asking only about the middle day would still pass.

**Input:**

```text
event project meeting /from 2019-12-02 1400 /to 2019-12-04 1600
on 2019-12-01
on 2019-12-02
on 2019-12-03
on 2019-12-04
on 2019-12-05
bye
```

**Expected output:**

```text
{{GREETING}}
    ____________________________________________________________
     Coupled up! This wagon is on the train now:
        [E][ ] project meeting (from: Dec 02 2019, 2:00 PM to: Dec 04 2019, 4:00 PM)
     That's 1 wagon(s) behind me now.
    ____________________________________________________________
    ____________________________________________________________
     Here is my timetable for Dec 01 2019:
    ____________________________________________________________
    ____________________________________________________________
     Here is my timetable for Dec 02 2019:
     1. [E][ ] project meeting (from: Dec 02 2019, 2:00 PM to: Dec 04 2019, 4:00 PM)
    ____________________________________________________________
    ____________________________________________________________
     Here is my timetable for Dec 03 2019:
     1. [E][ ] project meeting (from: Dec 02 2019, 2:00 PM to: Dec 04 2019, 4:00 PM)
    ____________________________________________________________
    ____________________________________________________________
     Here is my timetable for Dec 04 2019:
     1. [E][ ] project meeting (from: Dec 02 2019, 2:00 PM to: Dec 04 2019, 4:00 PM)
    ____________________________________________________________
    ____________________________________________________________
     Here is my timetable for Dec 05 2019:
    ____________________________________________________________
{{FAREWELL}}
```

### TC46: A todo never falls on a day, whatever its description says

**Aim:** A todo carries no date, so it is never matched even when its
description is the date being asked about. Guards the base `occursOn` returning
false rather than the filter comparing text, and shows a day with no matches
printing its header and nothing else, as an empty `list` does.

The deadline is set one day later to pin the comparison down to the exact day:
a check for "on or after" would wrongly match it.

**Input:**

```text
todo 2019-12-02
deadline return book /by 2019-12-03 1800
on 2019-12-02
bye
```

**Expected output:**

```text
{{GREETING}}
    ____________________________________________________________
     Coupled up! This wagon is on the train now:
        [T][ ] 2019-12-02
     That's 1 wagon(s) behind me now.
    ____________________________________________________________
    ____________________________________________________________
     Coupled up! This wagon is on the train now:
        [D][ ] return book (by: Dec 03 2019, 6:00 PM)
     That's 2 wagon(s) behind me now.
    ____________________________________________________________
    ____________________________________________________________
     Here is my timetable for Dec 02 2019:
    ____________________________________________________________
{{FAREWELL}}
```

### TC47: The number `on` shows is the number `mark` takes

**Aim:** The whole reason `on` numbers by list position rather than by match
position. A task shown as 2 by `on` is task 2 to `mark`, so the user can act on
what they see without counting the list again. Numbering the matches from 1
would show this same task as 1 and send `mark 1` to the wrong task.

**Input:**

```text
todo borrow book
deadline return book /by 2019-12-02 1800
on 2019-12-02
mark 2
list
bye
```

**Expected output:**

```text
{{GREETING}}
    ____________________________________________________________
     Coupled up! This wagon is on the train now:
        [T][ ] borrow book
     That's 1 wagon(s) behind me now.
    ____________________________________________________________
    ____________________________________________________________
     Coupled up! This wagon is on the train now:
        [D][ ] return book (by: Dec 02 2019, 6:00 PM)
     That's 2 wagon(s) behind me now.
    ____________________________________________________________
    ____________________________________________________________
     Here is my timetable for Dec 02 2019:
     2. [D][ ] return book (by: Dec 02 2019, 6:00 PM)
    ____________________________________________________________
    ____________________________________________________________
     Delivered, right on time! This wagon is done:
        [D][X] return book (by: Dec 02 2019, 6:00 PM)
    ____________________________________________________________
    ____________________________________________________________
     Here is every wagon on my train:
     1. [T][ ] borrow book
     2. [D][X] return book (by: Dec 02 2019, 6:00 PM)
    ____________________________________________________________
{{FAREWELL}}
```

### TC48: `on` with a missing or unreadable day is rejected

**Aim:** `on` validates its argument the way the add commands validate theirs.
A day is a whole day, so a date with a time on it is refused rather than having
the time ignored -- the opposite of `deadline`, where the time is required.
That difference is the point of `parseDay` being separate from `parseDate`.

**Input:**

```text
on
on tomorrow
on 2019-12-02 1800
bye
```

**Expected output:**

```text
{{GREETING}}
    ____________________________________________________________
     Which day's timetable do you want to see?
    ____________________________________________________________
    ____________________________________________________________
     I can't find 'tomorrow' on my timetable! Write the day as 2019-12-02.
    ____________________________________________________________
    ____________________________________________________________
     I can't find '2019-12-02 1800' on my timetable! Write the day as 2019-12-02.
    ____________________________________________________________
{{FAREWELL}}
```

### TC49: `on` with nothing stored prints only its header

**Aim:** `on` before anything has been added prints the header and nothing else,
rather than failing. Guards the match loop against an empty task list, the same
boundary TC3 guards for `list`.

**Input:**

```text
on 2019-12-02
bye
```

**Expected output:**

```text
{{GREETING}}
    ____________________________________________________________
     Here is my timetable for Dec 02 2019:
    ____________________________________________________________
{{FAREWELL}}
```

### TC50: A marker given with no description in front of it names the right mistake

**Aim:** `deadline /by ...` and `event /from ... /to ...` are missing their
description, not their marker, and are told so. The marker is present, so
complaining about the marker sends the user looking in the wrong place.

This is the case the separator's leading space creates. `" /by "` is what stops
"standby" being read as a marker, but the argument has already been trimmed, so
a line that is nothing but `/by ...` has no space in front of the marker for the
separator to match -- the split finds no marker and, without the check, blames
the one thing that is actually there.

**Input:**

```text
deadline /by 2019-12-02 1800
event /from 2019-12-02 1400 /to 2019-12-04 1600
bye
```

**Expected output:**

```text
{{GREETING}}
    ____________________________________________________________
     Bust my buffers! A deadline needs a description before I can couple it up.
    ____________________________________________________________
    ____________________________________________________________
     Bust my buffers! An event needs a description before I can couple it up.
    ____________________________________________________________
{{FAREWELL}}
```

### TC51: A genuinely missing marker still reports the marker

**Aim:** The other half of TC50. Text with no marker at all is still told the
marker is missing, so the new check narrows the message rather than replacing
it. Guards `startsWith` being tested against the marker rather than something
looser that would catch these too.

**Input:**

```text
deadline return book
event project meeting
event project meeting /from 2019-12-02 1400
bye
```

**Expected output:**

```text
{{GREETING}}
    ____________________________________________________________
     When is it due? A deadline needs a /by before I can pull it.
    ____________________________________________________________
    ____________________________________________________________
     When does it set off? An event needs a /from.
    ____________________________________________________________
    ____________________________________________________________
     When does it arrive? An event needs a /to after its /from.
    ____________________________________________________________
{{FAREWELL}}
```

### TC52: An event that ends before it starts is rejected

**Aim:** An event cannot run backwards. The times are checked against each
other, not just parsed, and the rejected event is not stored -- the closing
`list` shows nothing was added.

The two dates differ only in their time of day, so this also shows the
comparison is made on the whole point in time rather than on the date alone,
which would see the two as equal and let it through.

**Input:**

```text
event project meeting /from 2019-12-01 1600 /to 2019-12-01 1400
event conference /from 2019-12-05 0900 /to 2019-12-01 0900
list
bye
```

**Expected output:**

```text
{{GREETING}}
    ____________________________________________________________
     Bust my buffers! Your event arrives before it sets off! Check its /from and /to.
    ____________________________________________________________
    ____________________________________________________________
     Bust my buffers! Your event arrives before it sets off! Check its /from and /to.
    ____________________________________________________________
    ____________________________________________________________
     Here is every wagon on my train:
    ____________________________________________________________
{{FAREWELL}}
```

### TC53: An event that starts and ends at the same moment is accepted

**Aim:** The boundary of TC52. Equal times are allowed -- an event lasting no
time says nothing false, while one ending before it begins cannot be true.
Guards the check being `isAfter` rather than a comparison that also rejects
equality.

**Input:**

```text
event standup /from 2019-12-02 0900 /to 2019-12-02 0900
list
bye
```

**Expected output:**

```text
{{GREETING}}
    ____________________________________________________________
     Coupled up! This wagon is on the train now:
        [E][ ] standup (from: Dec 02 2019, 9:00 AM to: Dec 02 2019, 9:00 AM)
     That's 1 wagon(s) behind me now.
    ____________________________________________________________
    ____________________________________________________________
     Here is every wagon on my train:
     1. [E][ ] standup (from: Dec 02 2019, 9:00 AM to: Dec 02 2019, 9:00 AM)
    ____________________________________________________________
{{FAREWELL}}
```

### TC54: `find` shows the tasks whose description contains the keyword

**Aim:** `find <keyword>` lists the tasks whose description contains that text
and leaves out the ones that do not. Every task type is searched, so a matching
deadline and event appear beside a matching todo.

The numbers shown are the tasks' positions in the whole list, not their
positions among the matches, as for `on`: TC56 is what pins that down.

**Input:**

```text
todo read book
todo buy milk
deadline return book /by 2019-12-02 1800
event book club /from 2019-12-03 1400 /to 2019-12-03 1600
find book
bye
```

**Expected output:**

```text
{{GREETING}}
    ____________________________________________________________
     Coupled up! This wagon is on the train now:
        [T][ ] read book
     That's 1 wagon(s) behind me now.
    ____________________________________________________________
    ____________________________________________________________
     Coupled up! This wagon is on the train now:
        [T][ ] buy milk
     That's 2 wagon(s) behind me now.
    ____________________________________________________________
    ____________________________________________________________
     Coupled up! This wagon is on the train now:
        [D][ ] return book (by: Dec 02 2019, 6:00 PM)
     That's 3 wagon(s) behind me now.
    ____________________________________________________________
    ____________________________________________________________
     Coupled up! This wagon is on the train now:
        [E][ ] book club (from: Dec 03 2019, 2:00 PM to: Dec 03 2019, 4:00 PM)
     That's 4 wagon(s) behind me now.
    ____________________________________________________________
    ____________________________________________________________
     I searched the yard and found these wagons:
     1. [T][ ] read book
     3. [D][ ] return book (by: Dec 02 2019, 6:00 PM)
     4. [E][ ] book club (from: Dec 03 2019, 2:00 PM to: Dec 03 2019, 4:00 PM)
    ____________________________________________________________
{{FAREWELL}}
```

### TC55: `find` with no match, and with no keyword

**Aim:** A search that matches nothing prints the header and nothing else,
rather than failing or staying silent, the same boundary TC49 guards for `on`.
A search with no keyword at all is a different thing and is rejected.

**Input:**

```text
todo read book
find homework
find
bye
```

**Expected output:**

```text
{{GREETING}}
    ____________________________________________________________
     Coupled up! This wagon is on the train now:
        [T][ ] read book
     That's 1 wagon(s) behind me now.
    ____________________________________________________________
    ____________________________________________________________
     I searched the yard and found these wagons:
    ____________________________________________________________
    ____________________________________________________________
     What am I looking for? Give me a word to search the yard for.
    ____________________________________________________________
{{FAREWELL}}
```

### TC56: The number `find` shows is the number `mark` takes

**Aim:** The numbers beside the matches are list positions, so marking the
number `find` printed marks the task that was shown. The matches here are tasks
2 and 4, so numbering them 1 and 2 would send `mark 2` to the wrong task -- the
closing `list` is what shows which task was really marked.

This is the `find` counterpart of TC47, which pins the same rule for `on`.

**Input:**

```text
todo buy milk
todo read book
todo call mum
todo return book
find book
mark 2
list
bye
```

**Expected output:**

```text
{{GREETING}}
    ____________________________________________________________
     Coupled up! This wagon is on the train now:
        [T][ ] buy milk
     That's 1 wagon(s) behind me now.
    ____________________________________________________________
    ____________________________________________________________
     Coupled up! This wagon is on the train now:
        [T][ ] read book
     That's 2 wagon(s) behind me now.
    ____________________________________________________________
    ____________________________________________________________
     Coupled up! This wagon is on the train now:
        [T][ ] call mum
     That's 3 wagon(s) behind me now.
    ____________________________________________________________
    ____________________________________________________________
     Coupled up! This wagon is on the train now:
        [T][ ] return book
     That's 4 wagon(s) behind me now.
    ____________________________________________________________
    ____________________________________________________________
     I searched the yard and found these wagons:
     2. [T][ ] read book
     4. [T][ ] return book
    ____________________________________________________________
    ____________________________________________________________
     Delivered, right on time! This wagon is done:
        [T][X] read book
    ____________________________________________________________
    ____________________________________________________________
     Here is every wagon on my train:
     1. [T][ ] buy milk
     2. [T][X] read book
     3. [T][ ] call mum
     4. [T][ ] return book
    ____________________________________________________________
{{FAREWELL}}
```

### TC57: `find` matches on the description only, and is case sensitive

**Aim:** The search reads the description, never the displayed line, so the
type tag and the formatted date are not searchable: `find [T]` matches nothing
even though every todo shows `[T]`. Matching is case sensitive, as command
keywords are, so `find Book` does not find "read book".

**Input:**

```text
todo read book
deadline pay fine /by 2019-12-02 1800
find [T]
find Dec
find Book
bye
```

**Expected output:**

```text
{{GREETING}}
    ____________________________________________________________
     Coupled up! This wagon is on the train now:
        [T][ ] read book
     That's 1 wagon(s) behind me now.
    ____________________________________________________________
    ____________________________________________________________
     Coupled up! This wagon is on the train now:
        [D][ ] pay fine (by: Dec 02 2019, 6:00 PM)
     That's 2 wagon(s) behind me now.
    ____________________________________________________________
    ____________________________________________________________
     I searched the yard and found these wagons:
    ____________________________________________________________
    ____________________________________________________________
     I searched the yard and found these wagons:
    ____________________________________________________________
    ____________________________________________________________
     I searched the yard and found these wagons:
    ____________________________________________________________
{{FAREWELL}}
```

### TC58: `find` searches for the whole argument, spaces included

**Aim:** The keyword is the whole argument, so `find read book` looks for that
phrase rather than for either word. Guards against splitting the argument on
spaces, which would silently widen the search: "buy book" would match a search
for "read book" if either word were enough.

**Input:**

```text
todo read book
todo buy book
find read book
bye
```

**Expected output:**

```text
{{GREETING}}
    ____________________________________________________________
     Coupled up! This wagon is on the train now:
        [T][ ] read book
     That's 1 wagon(s) behind me now.
    ____________________________________________________________
    ____________________________________________________________
     Coupled up! This wagon is on the train now:
        [T][ ] buy book
     That's 2 wagon(s) behind me now.
    ____________________________________________________________
    ____________________________________________________________
     I searched the yard and found these wagons:
     1. [T][ ] read book
    ____________________________________________________________
{{FAREWELL}}
```

### TC59: Undo an add, with the read-only commands in between

**Aim:** `undo` takes back the most recent change and says which line it is
taking back. Every command that reads the list without changing it -- `list`,
`find` and `on` -- must be transparent to undo: none consumes the step, and none
is quoted back in its place.

**Input:**

```text
todo read book
list
find read
on 2019-12-02
undo
list
bye
```

**Expected output:**

```text
{{GREETING}}
    ____________________________________________________________
     Coupled up! This wagon is on the train now:
        [T][ ] read book
     That's 1 wagon(s) behind me now.
    ____________________________________________________________
    ____________________________________________________________
     Here is every wagon on my train:
     1. [T][ ] read book
    ____________________________________________________________
    ____________________________________________________________
     I searched the yard and found these wagons:
     1. [T][ ] read book
    ____________________________________________________________
    ____________________________________________________________
     Here is my timetable for Dec 02 2019:
    ____________________________________________________________
    ____________________________________________________________
     Reversing! I've backed out of 'todo read book'.
     That's 0 wagon(s) behind me now.
    ____________________________________________________________
    ____________________________________________________________
     Here is every wagon on my train:
    ____________________________________________________________
{{FAREWELL}}
```

### TC60: Undo a delete: the task comes back at its own number

**Aim:** Undoing a delete puts the task back where it was rather than on the
end, so the numbering closes around it exactly as deleting it opened them.
Guards against restoring by appending, which would renumber two tasks and send
a later `mark` to the wrong one.

**Input:**

```text
todo read book
todo buy milk
todo pay fine
delete 2
undo
list
bye
```

**Expected output:**

```text
{{GREETING}}
    ____________________________________________________________
     Coupled up! This wagon is on the train now:
        [T][ ] read book
     That's 1 wagon(s) behind me now.
    ____________________________________________________________
    ____________________________________________________________
     Coupled up! This wagon is on the train now:
        [T][ ] buy milk
     That's 2 wagon(s) behind me now.
    ____________________________________________________________
    ____________________________________________________________
     Coupled up! This wagon is on the train now:
        [T][ ] pay fine
     That's 3 wagon(s) behind me now.
    ____________________________________________________________
    ____________________________________________________________
     Uncoupled! I've left this wagon in the siding:
        [T][ ] buy milk
     That's 2 wagon(s) behind me now.
    ____________________________________________________________
    ____________________________________________________________
     Reversing! I've backed out of 'delete 2'.
     That's 3 wagon(s) behind me now.
    ____________________________________________________________
    ____________________________________________________________
     Here is every wagon on my train:
     1. [T][ ] read book
     2. [T][ ] buy milk
     3. [T][ ] pay fine
    ____________________________________________________________
{{FAREWELL}}
```

### TC61: Undoing a `mark` that changed nothing leaves the task done

**Aim:** Marking a task that is already done changes nothing, so undoing that
second `mark` must leave the tick alone. Guards against reversing a mark by
simply unmarking, which would take away a tick the undone command never set.

**Input:**

```text
todo read book
mark 1
mark 1
undo
list
bye
```

**Expected output:**

```text
{{GREETING}}
    ____________________________________________________________
     Coupled up! This wagon is on the train now:
        [T][ ] read book
     That's 1 wagon(s) behind me now.
    ____________________________________________________________
    ____________________________________________________________
     Delivered, right on time! This wagon is done:
        [T][X] read book
    ____________________________________________________________
    ____________________________________________________________
     Delivered, right on time! This wagon is done:
        [T][X] read book
    ____________________________________________________________
    ____________________________________________________________
     Reversing! I've backed out of 'mark 1'.
     That's 1 wagon(s) behind me now.
    ____________________________________________________________
    ____________________________________________________________
     Here is every wagon on my train:
     1. [T][X] read book
    ____________________________________________________________
{{FAREWELL}}
```

### TC62: A rejected command does not consume an undo

**Aim:** A command that is refused changes nothing, so it records nothing and
the next `undo` reaches past it to the last real change. Guards against noting
the undo point before running the command, which would let a typo silently
spend the user's one chance to take back what they did before it.

**Input:**

```text
todo read book
delete 99
undo
list
bye
```

**Expected output:**

```text
{{GREETING}}
    ____________________________________________________________
     Coupled up! This wagon is on the train now:
        [T][ ] read book
     That's 1 wagon(s) behind me now.
    ____________________________________________________________
    ____________________________________________________________
     There's no wagon 99 on my train! I'm only pulling 1 wagon(s).
    ____________________________________________________________
    ____________________________________________________________
     Reversing! I've backed out of 'todo read book'.
     That's 0 wagon(s) behind me now.
    ____________________________________________________________
    ____________________________________________________________
     Here is every wagon on my train:
    ____________________________________________________________
{{FAREWELL}}
```

### TC63: `undo` with nothing to undo is refused

**Aim:** A session that has changed nothing has nothing to take back, and says
so rather than doing nothing quietly. The second `undo` here has already used
up the one change there was.

**Input:**

```text
undo
todo read book
undo
undo
bye
```

**Expected output:**

```text
{{GREETING}}
    ____________________________________________________________
     I can't reverse any further! There's nothing to undo.
    ____________________________________________________________
    ____________________________________________________________
     Coupled up! This wagon is on the train now:
        [T][ ] read book
     That's 1 wagon(s) behind me now.
    ____________________________________________________________
    ____________________________________________________________
     Reversing! I've backed out of 'todo read book'.
     That's 0 wagon(s) behind me now.
    ____________________________________________________________
    ____________________________________________________________
     I can't reverse any further! There's nothing to undo.
    ____________________________________________________________
{{FAREWELL}}
```

### TC64: Undos are saved, but the history does not survive a restart

**Aim:** Two undos walk back two changes, most recent first. The restored list
is written to the save file, so the second run opens on it -- but the history
itself is only ever in memory, so the `undo` in the second run is refused even
though a task is there to see.

**Input:**

```text
todo read book
todo buy milk
undo
list
bye
```

**Input:**

```text
list
undo
list
bye
```

**Expected output:**

```text
{{GREETING}}
    ____________________________________________________________
     Coupled up! This wagon is on the train now:
        [T][ ] read book
     That's 1 wagon(s) behind me now.
    ____________________________________________________________
    ____________________________________________________________
     Coupled up! This wagon is on the train now:
        [T][ ] buy milk
     That's 2 wagon(s) behind me now.
    ____________________________________________________________
    ____________________________________________________________
     Reversing! I've backed out of 'todo buy milk'.
     That's 1 wagon(s) behind me now.
    ____________________________________________________________
    ____________________________________________________________
     Here is every wagon on my train:
     1. [T][ ] read book
    ____________________________________________________________
{{FAREWELL}}
{{GREETING}}
    ____________________________________________________________
     Here is every wagon on my train:
     1. [T][ ] read book
    ____________________________________________________________
    ____________________________________________________________
     I can't reverse any further! There's nothing to undo.
    ____________________________________________________________
    ____________________________________________________________
     Here is every wagon on my train:
     1. [T][ ] read book
    ____________________________________________________________
{{FAREWELL}}
```
### TC65: Repeated `undo` walks back one change at a time, most recent first

**Aim:** Undo is not limited to the last change: asked twice it reverses two,
in the reverse of the order they were made. Guards against a history that only
remembers one change, and against replaying steps oldest first -- which would
reverse the wrong task, since the number a step carries only describes the list
as it stood when that step was recorded.

**Input:**

```text
todo read book
todo buy milk
todo pay fine
undo
undo
list
bye
```

**Expected output:**

```text
{{GREETING}}
    ____________________________________________________________
     Coupled up! This wagon is on the train now:
        [T][ ] read book
     That's 1 wagon(s) behind me now.
    ____________________________________________________________
    ____________________________________________________________
     Coupled up! This wagon is on the train now:
        [T][ ] buy milk
     That's 2 wagon(s) behind me now.
    ____________________________________________________________
    ____________________________________________________________
     Coupled up! This wagon is on the train now:
        [T][ ] pay fine
     That's 3 wagon(s) behind me now.
    ____________________________________________________________
    ____________________________________________________________
     Reversing! I've backed out of 'todo pay fine'.
     That's 2 wagon(s) behind me now.
    ____________________________________________________________
    ____________________________________________________________
     Reversing! I've backed out of 'todo buy milk'.
     That's 1 wagon(s) behind me now.
    ____________________________________________________________
    ____________________________________________________________
     Here is every wagon on my train:
     1. [T][ ] read book
    ____________________________________________________________
{{FAREWELL}}
```

### TC66: Undoing a `mark` leaves the task not done again

**Aim:** Undoing a `mark` puts the completion flag back to what it was before
that command, which for a task that was not done is not done. The count is
reported even though marking never changed it, so that the confirmation reads
the same way whatever was undone.

**Input:**

```text
todo read book
mark 1
undo
list
bye
```

**Expected output:**

```text
{{GREETING}}
    ____________________________________________________________
     Coupled up! This wagon is on the train now:
        [T][ ] read book
     That's 1 wagon(s) behind me now.
    ____________________________________________________________
    ____________________________________________________________
     Delivered, right on time! This wagon is done:
        [T][X] read book
    ____________________________________________________________
    ____________________________________________________________
     Reversing! I've backed out of 'mark 1'.
     That's 1 wagon(s) behind me now.
    ____________________________________________________________
    ____________________________________________________________
     Here is every wagon on my train:
     1. [T][ ] read book
    ____________________________________________________________
{{FAREWELL}}
```

### TC67: Undoing an `unmark` leaves the task done again

**Aim:** The mirror of TC66, and the case a blind inverse would get right by
luck rather than by rule: undoing an `unmark` restores the flag the task
carried before it, which here is done. Together with TC61 this covers all
three ways the flag can stand when a mark or unmark is taken back.

**Input:**

```text
todo read book
mark 1
unmark 1
undo
list
bye
```

**Expected output:**

```text
{{GREETING}}
    ____________________________________________________________
     Coupled up! This wagon is on the train now:
        [T][ ] read book
     That's 1 wagon(s) behind me now.
    ____________________________________________________________
    ____________________________________________________________
     Delivered, right on time! This wagon is done:
        [T][X] read book
    ____________________________________________________________
    ____________________________________________________________
     Back on the train it goes! This wagon is not done yet:
        [T][ ] read book
    ____________________________________________________________
    ____________________________________________________________
     Reversing! I've backed out of 'unmark 1'.
     That's 1 wagon(s) behind me now.
    ____________________________________________________________
    ____________________________________________________________
     Here is every wagon on my train:
     1. [T][X] read book
    ____________________________________________________________
{{FAREWELL}}
```

### TC68: Stray spaces before the keyword and inside the arguments are tidied

**Aim:** A command is found under a leading space, and a doubled space between
the day and the time of a date does not make the date unreadable. Guards the
tidying done once in the parser's constructor, on which every later marker
match with a space either side depends.

**Input:**

```text
   todo read book
deadline return book  /by  2019-12-02   1800
list
bye
```

**Expected output:**

```text
{{GREETING}}
    ____________________________________________________________
     Coupled up! This wagon is on the train now:
        [T][ ] read book
     That's 1 wagon(s) behind me now.
    ____________________________________________________________
    ____________________________________________________________
     Coupled up! This wagon is on the train now:
        [D][ ] return book (by: Dec 02 2019, 6:00 PM)
     That's 2 wagon(s) behind me now.
    ____________________________________________________________
    ____________________________________________________________
     Here is every wagon on my train:
     1. [T][ ] read book
     2. [D][ ] return book (by: Dec 02 2019, 6:00 PM)
    ____________________________________________________________
{{FAREWELL}}
```

### TC69: A keyword that takes no argument refuses one

**Aim:** `list all` and `bye now` are refused with the text that was not
understood quoted back, rather than being carried out with it ignored. The
chatbot is still running after `bye now`, which is what the real `bye` at the
end shows.

**Input:**

```text
list all
undo 3
bye now
bye
```

**Expected output:**

```text
{{GREETING}}
    ____________________________________________________________
     Bust my buffers! 'list' is a signal on its own -- I don't know what to do with 'all'.
    ____________________________________________________________
    ____________________________________________________________
     Bust my buffers! 'undo' is a signal on its own -- I don't know what to do with '3'.
    ____________________________________________________________
    ____________________________________________________________
     Bust my buffers! 'bye' is a signal on its own -- I don't know what to do with 'now'.
    ____________________________________________________________
{{FAREWELL}}
```

### TC70: A marker given twice is refused by name

**Aim:** A second `/by`, `/from` or `/to` is reported as the repeat it is,
not as an unreadable date with a marker inside it. Guards the whole-word
marker check made on the text after each split.

**Input:**

```text
deadline return book /by 2019-12-02 1800 /by 2019-12-03 1800
event meeting /from 2019-12-02 1400 /from 2019-12-02 1500 /to 2019-12-02 1600
event meeting /from 2019-12-02 1400 /to 2019-12-02 1600 /to 2019-12-02 1700
list
bye
```

**Expected output:**

```text
{{GREETING}}
    ____________________________________________________________
     Bust my buffers! You've given /by more than once. Once is all I need.
    ____________________________________________________________
    ____________________________________________________________
     Bust my buffers! You've given /from more than once. Once is all I need.
    ____________________________________________________________
    ____________________________________________________________
     Bust my buffers! You've given /to more than once. Once is all I need.
    ____________________________________________________________
    ____________________________________________________________
     Here is every wagon on my train:
    ____________________________________________________________
{{FAREWELL}}
```

### TC71: The other command's marker is refused by name

**Aim:** A `/from` on a deadline, or a `/by` on an event, means the two
commands have been mixed up, and that is the mistake named -- not a missing
`/by` or `/from`, which the user can see they did not mean to type. Guards the
marker check made before the line is split.

**Input:**

```text
deadline return book /from 2019-12-02 1800
event meeting /by 2019-12-02 1800
bye
```

**Expected output:**

```text
{{GREETING}}
    ____________________________________________________________
     Bust my buffers! A deadline takes just a /by -- there's no /from or /to on it.
    ____________________________________________________________
    ____________________________________________________________
     Bust my buffers! An event takes a /from and a /to -- there's no /by on it.
    ____________________________________________________________
{{FAREWELL}}
```

### TC72: An event's markers the wrong way round are named as such

**Aim:** `/to` before `/from` is reported as the wrong order, not as a
missing `/to` the user can see they typed. Guards the check on the text in
front of the `/from`, made before the `/to` is looked for after it.

**Input:**

```text
event meeting /to 2019-12-02 1600 /from 2019-12-02 1400
bye
```

**Expected output:**

```text
{{GREETING}}
    ____________________________________________________________
     Bust my buffers! Your event's /to came before its /from. Set off first, then arrive.
    ____________________________________________________________
{{FAREWELL}}
```

### TC73: A day that is not on the calendar is refused rather than repaired

**Aim:** `2019-02-30` matches the date format exactly and names a day that
does not exist. It is refused, and told so; the date formatter would otherwise
resolve it to the 28th and store a moment the user never chose. `2400` is the
same mistake on the clock side, which would roll to the next day's midnight.
The leap day is accepted in a leap year and refused in a common one, so the
rule being applied is the calendar's and not a fixed table of month lengths.

**Input:**

```text
deadline return book /by 2019-02-30 1800
event meeting /from 2019-12-02 1400 /to 2019-12-02 2400
deadline leap /by 2020-02-29 1200
deadline leap /by 2019-02-29 1200
list
bye
```

**Expected output:**

```text
{{GREETING}}
    ____________________________________________________________
     There's no such moment as '2019-02-30 1800' for a deadline date! Check the day is on the calendar and the time is on the 24-hour clock, 0000 to 2359.
    ____________________________________________________________
    ____________________________________________________________
     There's no such moment as '2019-12-02 2400' for an end date! Check the day is on the calendar and the time is on the 24-hour clock, 0000 to 2359.
    ____________________________________________________________
    ____________________________________________________________
     Coupled up! This wagon is on the train now:
        [D][ ] leap (by: Feb 29 2020, 12:00 PM)
     That's 1 wagon(s) behind me now.
    ____________________________________________________________
    ____________________________________________________________
     There's no such moment as '2019-02-29 1200' for a deadline date! Check the day is on the calendar and the time is on the 24-hour clock, 0000 to 2359.
    ____________________________________________________________
    ____________________________________________________________
     Here is every wagon on my train:
     1. [D][ ] leap (by: Feb 29 2020, 12:00 PM)
    ____________________________________________________________
{{FAREWELL}}
```

### TC74: Adding a task that is already on the list is refused

**Aim:** The same task typed a second time is refused, with the number of the
one already there, and the count does not move. Marking the first does not make
the second a different task. A deadline with the same text is a different task
and is accepted, and once the first is deleted the same text is accepted again.

**Input:**

```text
todo read book
todo read book
mark 1
todo read book
deadline read book /by 2019-12-02 1800
delete 1
todo read book
list
bye
```

**Expected output:**

```text
{{GREETING}}
    ____________________________________________________________
     Coupled up! This wagon is on the train now:
        [T][ ] read book
     That's 1 wagon(s) behind me now.
    ____________________________________________________________
    ____________________________________________________________
     Bust my buffers! That wagon is already on my train, at number 1:
        [T][ ] read book
    ____________________________________________________________
    ____________________________________________________________
     Delivered, right on time! This wagon is done:
        [T][X] read book
    ____________________________________________________________
    ____________________________________________________________
     Bust my buffers! That wagon is already on my train, at number 1:
        [T][X] read book
    ____________________________________________________________
    ____________________________________________________________
     Coupled up! This wagon is on the train now:
        [D][ ] read book (by: Dec 02 2019, 6:00 PM)
     That's 2 wagon(s) behind me now.
    ____________________________________________________________
    ____________________________________________________________
     Uncoupled! I've left this wagon in the siding:
        [T][X] read book
     That's 1 wagon(s) behind me now.
    ____________________________________________________________
    ____________________________________________________________
     Coupled up! This wagon is on the train now:
        [T][ ] read book
     That's 2 wagon(s) behind me now.
    ____________________________________________________________
    ____________________________________________________________
     Here is every wagon on my train:
     1. [D][ ] read book (by: Dec 02 2019, 6:00 PM)
     2. [T][ ] read book
    ____________________________________________________________
{{FAREWELL}}
```

### TC75: A refused duplicate leaves nothing to undo

**Aim:** The duplicate is refused by the list before the history is told
anything, so `undo` reaches the change before it -- exactly as TC62 shows for
a line the parser refused.

**Input:**

```text
todo read book
todo read book
undo
list
bye
```

**Expected output:**

```text
{{GREETING}}
    ____________________________________________________________
     Coupled up! This wagon is on the train now:
        [T][ ] read book
     That's 1 wagon(s) behind me now.
    ____________________________________________________________
    ____________________________________________________________
     Bust my buffers! That wagon is already on my train, at number 1:
        [T][ ] read book
    ____________________________________________________________
    ____________________________________________________________
     Reversing! I've backed out of 'todo read book'.
     That's 0 wagon(s) behind me now.
    ____________________________________________________________
    ____________________________________________________________
     Here is every wagon on my train:
    ____________________________________________________________
{{FAREWELL}}
```

### TC76: `mark` with two numbers, or a number no list could reach, is refused

**Aim:** `mark 1 2` is told one wagon at a time, rather than that `1 2` is not
a number; a number too big for an `int` is all digits and so is a number, and
gets the range answer. Guards the two checks around `Integer.parseInt`.

**Input:**

```text
todo read book
mark 1 2
mark 99999999999
list
bye
```

**Expected output:**

```text
{{GREETING}}
    ____________________________________________________________
     Coupled up! This wagon is on the train now:
        [T][ ] read book
     That's 1 wagon(s) behind me now.
    ____________________________________________________________
    ____________________________________________________________
     One wagon at a time! Give me a single number to mark.
    ____________________________________________________________
    ____________________________________________________________
     There's no wagon 99999999999 on my train! No train is that long.
    ____________________________________________________________
    ____________________________________________________________
     Here is every wagon on my train:
     1. [T][ ] read book
    ____________________________________________________________
{{FAREWELL}}
```

## Not yet covered

Behaviour that is out of scope for the current increment, listed so it is not
mistaken for an oversight. Add cases here as the chatbot grows:

* **`bye` leaving the undo history alone.** `bye` changes nothing and so
  records nothing, exactly as `list`, `find` and `on` do in TC59. There is no
  case for it because a session cannot be asked to undo anything after it has
  ended, and the next run starts with an empty history whatever `bye` did.
  `ExitCommand` never names the history at all, which is where that is visible.
* **A redo.** `undo` records nothing itself, so undoing twice reaches two
  earlier changes rather than putting the first one back. Adding a redo would
  need a second stack and a rule for when reaching backwards discards what was
  ahead, and neither is asked for yet.
* **Undo surviving a restart.** The history is held in memory and dies with the
  program, which TC64 pins down. Saving it would mean a second file format to
  keep correct in the one class where a mistake silently loses tasks, for a
  convenience only ever wanted inside the session that earned it.
* **Undoing a save that failed.** A failed save leaves the change standing in
  memory with a warning above the confirmation, and an undo of it behaves the
  same way. Reaching it needs an unwritable `./data`, which the test script does
  not set up -- the same reason the save failure itself has no case. Both are
  covered by JUnit instead: each `*CommandTest` in `thomas.command` puts a
  folder where the save file should be and checks the warning sits above the
  confirmation, and `ThomasTest` runs a whole console session that way.
* **The GUI window.** The plan drives `thomas.Thomas`, the console entry point,
  and says nothing about what the window looks like or does. The window types a
  line into `Thomas.getResponse` and paints whatever comes back, so the wording
  every case here pins down is the same wording a dialog bubble shows;
  `ThomasTest` covers that method, including the reply an error earns and the
  command type each bubble is colored by. What is left uncovered is the JavaFX
  layer itself -- the layout, the styling, and closing the window after `bye` --
  which needs a running window rather than typed input. Those checks, along
  with running the app on other operating systems, display scalings and OS
  language settings, are listed in `manual-test-plan.md` beside this file.
* **A keyword typed in the wrong case.** `Bye`, `TODO x` and `List` are all
  reported as unknown commands, because the match is made with `equals`. That is
  deliberate rather than accidental, and noted in `Keyword.of`, but no case pins
  it down, so nothing would notice if the matching were loosened.
* **A blank input line.** Pressing enter on its own is answered with its own
  message, asking for a command, rather than being reported as an unknown one;
  `ParserTest` pins the wording. A blank line inside a fenced input block is
  too easy to mistake for formatting, so there is no case for it here.
* **A tab typed in place of a space.** Tidied exactly as a run of spaces is,
  which `ParserTest` covers. A tab inside a fenced input block is invisible on
  the page, so the case would not be readable and is left to JUnit.
* **A duplicate returning through `undo`.** Deleting a task, adding the same
  one again and then undoing the delete puts the original back beside its
  twin, because putting a task back is not checked -- it only ever replays a
  delete that really happened, and refusing it would leave the undo half done.
  Rare enough to note rather than to fix.
* **A duplicate line in the save file.** The chatbot never writes one, since
  the duplicate is refused as it is typed; one can only be edited in, and it is
  then reported and skipped with the first copy kept, which `StorageTest`
  covers as it does every other kind of damaged line.
* **A ceiling on the number of tasks.** There is no longer one to test: the
  tasks are held in an `ArrayList`, which grows as tasks are added, so the
  refusal message that `MAX_TASKS` used to produce is gone.
* **A save file line that cannot be read.** No longer reachable through the UI.
  A description containing `" | "` used to produce one, and TC43 used to test it
  that way; that description is now refused as it is typed, and every other line
  the chatbot writes it wrote itself, in the format it reads. So the loader's
  skip-and-report path -- one damaged line costing only its own task, the rest
  still loading -- has no UI case any more. It is covered by `StorageTest`
  instead, which writes damaged files directly and checks both halves: the
  complaint each kind of damage earns, and that the surrounding lines still
  loaded.
* **Keeping a description that contains the separator.** Refusing it is the
  simple option, and it costs the user a character sequence they will rarely
  want. Accepting it would mean escaping the separator when writing and
  unescaping when reading, or putting the description last so the rest of the
  line can be split off before it -- either puts an encoder and a decoder into
  the one class where a bug costs data, for a case that is close to never. Worth
  revisiting only if descriptions with pipes turn out to matter.
* **Losing the save file mid-session.** The save is written after every change,
  so a chatbot killed outright should lose nothing. Confirming that needs the
  process to be killed rather than fed end-of-input, which the test script has no
  way to do -- TC42 covers the nearest testable case, input simply running out.
* **A saved date that cannot be read.** A date in the save file is parsed
  through the same helper as one the user types, so it is reported and the line
  skipped rather than killing start-up, exactly as TC43 shows for a bad field
  count. There is no case for it because there is no way to produce one through
  the UI: every date the chatbot writes it wrote from a `LocalDateTime`, in the
  same format it reads, so it can always read it back. Reaching this needs the
  file edited by hand, which the test script does not do.
* **A case-insensitive `find`.** `find Book` does not match "read book", since
  the search is a plain substring test and case sensitive, as keyword matching
  is everywhere else in the chatbot. TC57 pins that behavior down rather than
  leaving it unstated. Ignoring case would be friendlier and is the obvious next
  change, but it is a change in behavior rather than an oversight.
* **Searching for several keywords at once.** `find read book` looks for the
  phrase, not for either word, which TC58 pins down. Matching any of several
  words needs a rule for how they combine, and that rule is not asked for yet.
* **The order `on` lists its matches in.** They come out in list order, not in
  order of the time they happen at, so a 6 PM deadline can be shown above a
  2 PM event. Sorting them would read better but would break the numbering
  `on` exists to provide, since the numbers are list positions.
* **An event in the save file that runs backwards.** The check lives in
  `EventTask`'s constructor, which the save file is loaded through too, so such
  a line is reported and skipped exactly as TC43 shows for a bad field count.
  There is no case for it because there is no way to produce one through the UI
  -- TC52 refuses to create it in the first place, so reaching this needs the
  file edited by hand, which the test script does not do.
* **A start and end that disagree about having a time.** Not reachable: a time
  is required on every date, so an event cannot have one end with a time and the
  other without. It would need testing if the time were ever made optional.
* **An unreadable or unwritable save file.** The messages for a save file that
  exists but cannot be read, or a `./data` folder that cannot be created, are
  reachable only by changing file permissions, which the test script does not
  set up. The two shapes of the same trouble that need no permissions -- a
  plain file sitting where `./data` should be, and a folder sitting where the
  save file should be -- are named in words by `Storage` and covered by
  `StorageTest`; the script works in an empty directory and does not set those
  up either.
