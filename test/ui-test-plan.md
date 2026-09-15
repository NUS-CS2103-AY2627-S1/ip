# LuigiBot UI Test Plan

Run these cases with the project-specific `test-ui` skill. Each case starts LuigiBot with an empty task list and compares the complete console output exactly.

## Test case: Add and list a Todo

**Aim:** Verify that LuigiBot adds a Todo and displays it in the task list.

### Input

```text
todo borrow book
list
bye
```

### Expected output

```text
____________________________________________________________
.____          .__       .____________        __   
|    |    __ __|__| ____ |__\______   \ _____/  |_
|    |   |  |  \  |/ ___\|  ||    |  _//  _ \   __\
|    |___|  |  /  / /_/  >  ||    |   (  <_> )  | 
|_______ \____/|__\___  /|__||______  /\____/|__|
        \/       /_____/            \/             
____________________________________________________________
Its a-me,LuigiBot!
What can I do for you?
____________________________________________________________
____________________________________________________________
Okie-dokie! Luigi added this task:
  [T][ ] borrow book
You've-a got 1 tasks now!
____________________________________________________________
____________________________________________________________
Let's-a see what Luigi has on the list:
1.[T][ ] borrow book
____________________________________________________________
Mama mia! Leaving already? Cya soon!
____________________________________________________________
```

## Test case: Handle outer whitespace and unsafe descriptions

**Aim:** Verify that harmless outer whitespace is accepted while descriptions that would corrupt storage are rejected without changing the task list.

### Input

```text
  todo read book
todo read | book
list
bye
```

### Expected output

```text
____________________________________________________________
.____          .__       .____________        __   
|    |    __ __|__| ____ |__\______   \ _____/  |_
|    |   |  |  \  |/ ___\|  ||    |  _//  _ \   __\
|    |___|  |  /  / /_/  >  ||    |   (  <_> )  | 
|_______ \____/|__\___  /|__||______  /\____/|__|
        \/       /_____/            \/             
____________________________________________________________
Its a-me,LuigiBot!
What can I do for you?
____________________________________________________________
____________________________________________________________
Okie-dokie! Luigi added this task:
  [T][ ] read book
You've-a got 1 tasks now!
____________________________________________________________
____________________________________________________________
Mamma mia! Task descriptions can't-a contain the | symbol.
____________________________________________________________
____________________________________________________________
Let's-a see what Luigi has on the list:
1.[T][ ] read book
____________________________________________________________
Mama mia! Leaving already? Cya soon!
____________________________________________________________
```

## Test case: Find tasks by description keyword

**Aim:** Verify that `find` performs a case-insensitive description search, preserves task numbering, and reports when there are no matches.

### Input

```text
todo read book
todo return book
find BOOK
find pizza
bye
```

### Expected output

```text
____________________________________________________________
.____          .__       .____________        __   
|    |    __ __|__| ____ |__\______   \ _____/  |_
|    |   |  |  \  |/ ___\|  ||    |  _//  _ \   __\
|    |___|  |  /  / /_/  >  ||    |   (  <_> )  | 
|_______ \____/|__\___  /|__||______  /\____/|__|
        \/       /_____/            \/             
____________________________________________________________
Its a-me,LuigiBot!
What can I do for you?
____________________________________________________________
____________________________________________________________
Okie-dokie! Luigi added this task:
  [T][ ] read book
You've-a got 1 tasks now!
____________________________________________________________
____________________________________________________________
Okie-dokie! Luigi added this task:
  [T][ ] return book
You've-a got 2 tasks now!
____________________________________________________________
____________________________________________________________
Here are the matching tasks in Luigi's list:
1.[T][ ] read book
2.[T][ ] return book
____________________________________________________________
____________________________________________________________
Mamma mia! Luigi found-a no tasks matching "pizza".
____________________________________________________________
Mama mia! Leaving already? Cya soon!
____________________________________________________________
```

## Test case: Skip malformed saved tasks

**Aim:** Verify that malformed save lines are reported and skipped without affecting valid loaded tasks.

### Initial saved data

```text
T | 1 | read book
Z | 0 | mystery task
D | 0 | return book | 2019-12-02 1800
T | 2 | invalid status
E | 1 | project meeting | 2019-12-02 1400 | 2019-12-02 1600
D | 0 | missing deadline
T | 0 |
E | 0 | extra event | Monday | Tuesday | extra field
```

### Input

```text
list
todo borrow book
bye
```

### Expected output

```text
____________________________________________________________
.____          .__       .____________        __   
|    |    __ __|__| ____ |__\______   \ _____/  |_
|    |   |  |  \  |/ ___\|  ||    |  _//  _ \   __\
|    |___|  |  /  / /_/  >  ||    |   (  <_> )  | 
|_______ \____/|__\___  /|__||______  /\____/|__|
        \/       /_____/            \/             
____________________________________________________________
Its a-me,LuigiBot!
What can I do for you?
____________________________________________________________
____________________________________________________________
Mamma mia! Luigi skipped-a an invalid saved task.
____________________________________________________________
____________________________________________________________
Mamma mia! Luigi skipped-a an invalid saved task.
____________________________________________________________
____________________________________________________________
Mamma mia! Luigi skipped-a an invalid saved task.
____________________________________________________________
____________________________________________________________
Mamma mia! Luigi skipped-a an invalid saved task.
____________________________________________________________
____________________________________________________________
Mamma mia! Luigi skipped-a an invalid saved task.
____________________________________________________________
____________________________________________________________
Let's-a see what Luigi has on the list:
1.[T][X] read book
2.[D][ ] return book (by: Dec 02 2019, 6:00 PM)
3.[E][X] project meeting (from: Dec 02 2019, 2:00 PM to: Dec 02 2019, 4:00 PM)
____________________________________________________________
____________________________________________________________
Okie-dokie! Luigi added this task:
  [T][ ] borrow book
You've-a got 4 tasks now!
____________________________________________________________
Mama mia! Leaving already? Cya soon!
____________________________________________________________
```

### Expected saved data

```text
T | 1 | read book
D | 0 | return book | 2019-12-02 1800
E | 1 | project meeting | 2019-12-02 1400 | 2019-12-02 1600
T | 0 | borrow book
```

## Test case: Add and list a Deadline

**Aim:** Verify that LuigiBot parses, stores, and formats a Deadline date and time.

### Input

```text
deadline return book /by 2019-12-02 1800
list
bye
```

### Expected output

```text
____________________________________________________________
.____          .__       .____________        __   
|    |    __ __|__| ____ |__\______   \ _____/  |_
|    |   |  |  \  |/ ___\|  ||    |  _//  _ \   __\
|    |___|  |  /  / /_/  >  ||    |   (  <_> )  | 
|_______ \____/|__\___  /|__||______  /\____/|__|
        \/       /_____/            \/             
____________________________________________________________
Its a-me,LuigiBot!
What can I do for you?
____________________________________________________________
____________________________________________________________
Okie-dokie! Luigi added this task:
  [D][ ] return book (by: Dec 02 2019, 6:00 PM)
You've-a got 1 tasks now!
____________________________________________________________
____________________________________________________________
Let's-a see what Luigi has on the list:
1.[D][ ] return book (by: Dec 02 2019, 6:00 PM)
____________________________________________________________
Mama mia! Leaving already? Cya soon!
____________________________________________________________
```

## Test case: Reject invalid Deadline dates and times

**Aim:** Verify that invalid Deadline date-times are rejected without changing the task list.

### Input

```text
deadline impossible date /by 2019-02-30 1800
deadline return book /by 2019-12-02 1800
deadline impossible time /by 2019-12-02 2500
deadline missing time /by 2019-12-02
deadline wrong format /by tomorrow
list
bye
```

### Expected output

```text
____________________________________________________________
.____          .__       .____________        __   
|    |    __ __|__| ____ |__\______   \ _____/  |_
|    |   |  |  \  |/ ___\|  ||    |  _//  _ \   __\
|    |___|  |  /  / /_/  >  ||    |   (  <_> )  | 
|_______ \____/|__\___  /|__||______  /\____/|__|
        \/       /_____/            \/             
____________________________________________________________
Its a-me,LuigiBot!
What can I do for you?
____________________________________________________________
____________________________________________________________
Mamma mia! Use-a yyyy-MM-dd HHmm for the deadline date and time.
____________________________________________________________
____________________________________________________________
Okie-dokie! Luigi added this task:
  [D][ ] return book (by: Dec 02 2019, 6:00 PM)
You've-a got 1 tasks now!
____________________________________________________________
____________________________________________________________
Mamma mia! Use-a yyyy-MM-dd HHmm for the deadline date and time.
____________________________________________________________
____________________________________________________________
Mamma mia! Use-a yyyy-MM-dd HHmm for the deadline date and time.
____________________________________________________________
____________________________________________________________
Mamma mia! Use-a yyyy-MM-dd HHmm for the deadline date and time.
____________________________________________________________
____________________________________________________________
Let's-a see what Luigi has on the list:
1.[D][ ] return book (by: Dec 02 2019, 6:00 PM)
____________________________________________________________
Mama mia! Leaving already? Cya soon!
____________________________________________________________
```

## Test case: Mark and unmark an Event

**Aim:** Verify that LuigiBot parses and formats Event date-times while preserving status updates.

### Input

```text
event project meeting /from 2019-12-02 1400 /to 2019-12-02 1600
mark 1
list
unmark 1
bye
```

### Expected output

```text
____________________________________________________________
.____          .__       .____________        __   
|    |    __ __|__| ____ |__\______   \ _____/  |_
|    |   |  |  \  |/ ___\|  ||    |  _//  _ \   __\
|    |___|  |  /  / /_/  >  ||    |   (  <_> )  | 
|_______ \____/|__\___  /|__||______  /\____/|__|
        \/       /_____/            \/             
____________________________________________________________
Its a-me,LuigiBot!
What can I do for you?
____________________________________________________________
____________________________________________________________
Okie-dokie! Luigi added this task:
  [E][ ] project meeting (from: Dec 02 2019, 2:00 PM to: Dec 02 2019, 4:00 PM)
You've-a got 1 tasks now!
____________________________________________________________
____________________________________________________________
Nice-a! Luigi marked this task as done:
  [E][X] project meeting (from: Dec 02 2019, 2:00 PM to: Dec 02 2019, 4:00 PM)
____________________________________________________________
____________________________________________________________
Let's-a see what Luigi has on the list:
1.[E][X] project meeting (from: Dec 02 2019, 2:00 PM to: Dec 02 2019, 4:00 PM)
____________________________________________________________
____________________________________________________________
No problem! Luigi marked this task as not done:
  [E][ ] project meeting (from: Dec 02 2019, 2:00 PM to: Dec 02 2019, 4:00 PM)
____________________________________________________________
Mama mia! Leaving already? Cya soon!
____________________________________________________________
```

## Test case: Reject unknown command

**Aim:** Verify that LuigiBot reports an unknown command without storing it as a task.

### Input

```text
read book
list
bye
```

### Expected output

```text
____________________________________________________________
.____          .__       .____________        __   
|    |    __ __|__| ____ |__\______   \ _____/  |_
|    |   |  |  \  |/ ___\|  ||    |  _//  _ \   __\
|    |___|  |  /  / /_/  >  ||    |   (  <_> )  | 
|_______ \____/|__\___  /|__||______  /\____/|__|
        \/       /_____/            \/             
____________________________________________________________
Its a-me,LuigiBot!
What can I do for you?
____________________________________________________________
____________________________________________________________
Oh no! Luigi doesn't-a recognize that command.
____________________________________________________________
____________________________________________________________
Let's-a see what Luigi has on the list:
____________________________________________________________
Mama mia! Leaving already? Cya soon!
____________________________________________________________
```

## Test case: Reject empty command

**Aim:** Verify that LuigiBot reports an empty command and continues accepting input.

### Input

```text

bye
```

### Expected output

```text
____________________________________________________________
.____          .__       .____________        __   
|    |    __ __|__| ____ |__\______   \ _____/  |_
|    |   |  |  \  |/ ___\|  ||    |  _//  _ \   __\
|    |___|  |  /  / /_/  >  ||    |   (  <_> )  | 
|_______ \____/|__\___  /|__||______  /\____/|__|
        \/       /_____/            \/             
____________________________________________________________
Its a-me,LuigiBot!
What can I do for you?
____________________________________________________________
____________________________________________________________
Mamma mia! You didn't-a enter a command.
____________________________________________________________
Mama mia! Leaving already? Cya soon!
____________________________________________________________
```

## Test case: Reject malformed mark and unmark numbers

**Aim:** Verify that mark and unmark require a whole-number task index.

### Input

```text
mark one
unmark
bye
```

### Expected output

```text
____________________________________________________________
.____          .__       .____________        __   
|    |    __ __|__| ____ |__\______   \ _____/  |_
|    |   |  |  \  |/ ___\|  ||    |  _//  _ \   __\
|    |___|  |  /  / /_/  >  ||    |   (  <_> )  | 
|_______ \____/|__\___  /|__||______  /\____/|__|
        \/       /_____/            \/             
____________________________________________________________
Its a-me,LuigiBot!
What can I do for you?
____________________________________________________________
____________________________________________________________
Mamma mia! Please-a enter a whole task number.
____________________________________________________________
____________________________________________________________
Mamma mia! Please-a enter a whole task number.
____________________________________________________________
Mama mia! Leaving already? Cya soon!
____________________________________________________________
```

## Test case: Reject out-of-range mark and unmark numbers

**Aim:** Verify that mark and unmark reject task numbers outside the stored task list.

### Input

```text
todo read book
mark 0
unmark 2
list
bye
```

### Expected output

```text
____________________________________________________________
.____          .__       .____________        __   
|    |    __ __|__| ____ |__\______   \ _____/  |_
|    |   |  |  \  |/ ___\|  ||    |  _//  _ \   __\
|    |___|  |  /  / /_/  >  ||    |   (  <_> )  | 
|_______ \____/|__\___  /|__||______  /\____/|__|
        \/       /_____/            \/             
____________________________________________________________
Its a-me,LuigiBot!
What can I do for you?
____________________________________________________________
____________________________________________________________
Okie-dokie! Luigi added this task:
  [T][ ] read book
You've-a got 1 tasks now!
____________________________________________________________
____________________________________________________________
Oh no! Luigi can't-a find that task number.
____________________________________________________________
____________________________________________________________
Oh no! Luigi can't-a find that task number.
____________________________________________________________
____________________________________________________________
Let's-a see what Luigi has on the list:
1.[T][ ] read book
____________________________________________________________
Mama mia! Leaving already? Cya soon!
____________________________________________________________
```

## Test case: Reject empty task descriptions

**Aim:** Verify that Todo and Deadline commands require a task description.

### Input

```text
todo
deadline /by 2019-12-02 1800
bye
```

### Expected output

```text
____________________________________________________________
.____          .__       .____________        __   
|    |    __ __|__| ____ |__\______   \ _____/  |_
|    |   |  |  \  |/ ___\|  ||    |  _//  _ \   __\
|    |___|  |  /  / /_/  >  ||    |   (  <_> )  | 
|_______ \____/|__\___  /|__||______  /\____/|__|
        \/       /_____/            \/             
____________________________________________________________
Its a-me,LuigiBot!
What can I do for you?
____________________________________________________________
____________________________________________________________
Mamma mia! The task description can't-a be empty.
____________________________________________________________
____________________________________________________________
Mamma mia! The task description can't-a be empty.
____________________________________________________________
Mama mia! Leaving already? Cya soon!
____________________________________________________________
```

## Test case: Reject missing Deadline details

**Aim:** Verify that a Deadline requires `/by` followed by a non-empty value.

### Input

```text
deadline return book
deadline return book /by
bye
```

### Expected output

```text
____________________________________________________________
.____          .__       .____________        __   
|    |    __ __|__| ____ |__\______   \ _____/  |_
|    |   |  |  \  |/ ___\|  ||    |  _//  _ \   __\
|    |___|  |  /  / /_/  >  ||    |   (  <_> )  | 
|_______ \____/|__\___  /|__||______  /\____/|__|
        \/       /_____/            \/             
____________________________________________________________
Its a-me,LuigiBot!
What can I do for you?
____________________________________________________________
____________________________________________________________
Oh no! Luigi needs-a know the deadline! Use /by.
____________________________________________________________
____________________________________________________________
Oh no! Luigi needs-a know the deadline! Use /by.
____________________________________________________________
Mama mia! Leaving already? Cya soon!
____________________________________________________________
```

## Test case: Reject empty Event description

**Aim:** Verify that an Event requires a task description.

### Input

```text
event /from 2019-12-02 1400 /to 2019-12-02 1600
bye
```

### Expected output

```text
____________________________________________________________
.____          .__       .____________        __   
|    |    __ __|__| ____ |__\______   \ _____/  |_
|    |   |  |  \  |/ ___\|  ||    |  _//  _ \   __\
|    |___|  |  /  / /_/  >  ||    |   (  <_> )  | 
|_______ \____/|__\___  /|__||______  /\____/|__|
        \/       /_____/            \/             
____________________________________________________________
Its a-me,LuigiBot!
What can I do for you?
____________________________________________________________
____________________________________________________________
Mamma mia! The task description can't-a be empty.
____________________________________________________________
Mama mia! Leaving already? Cya soon!
____________________________________________________________
```

## Test case: Reject malformed Event details

**Aim:** Verify that an Event requires non-empty `/from` and `/to` values in the correct order.

### Input

```text
event project meeting
event project meeting /from Mon 2pm
event project meeting /to 4pm
event project meeting /from /to 4pm
event project meeting /from Mon 2pm /to
event project meeting /to 4pm /from Mon 2pm
bye
```

### Expected output

```text
____________________________________________________________
.____          .__       .____________        __   
|    |    __ __|__| ____ |__\______   \ _____/  |_
|    |   |  |  \  |/ ___\|  ||    |  _//  _ \   __\
|    |___|  |  /  / /_/  >  ||    |   (  <_> )  | 
|_______ \____/|__\___  /|__||______  /\____/|__|
        \/       /_____/            \/             
____________________________________________________________
Its a-me,LuigiBot!
What can I do for you?
____________________________________________________________
____________________________________________________________
Mamma mia! Use: event DESCRIPTION /from START /to END.
____________________________________________________________
____________________________________________________________
Mamma mia! Use: event DESCRIPTION /from START /to END.
____________________________________________________________
____________________________________________________________
Mamma mia! Use: event DESCRIPTION /from START /to END.
____________________________________________________________
____________________________________________________________
Mamma mia! Use: event DESCRIPTION /from START /to END.
____________________________________________________________
____________________________________________________________
Mamma mia! Use: event DESCRIPTION /from START /to END.
____________________________________________________________
____________________________________________________________
Mamma mia! Use: event DESCRIPTION /from START /to END.
____________________________________________________________
Mama mia! Leaving already? Cya soon!
____________________________________________________________
```

## Test case: Reject invalid Event date-times and ranges

**Aim:** Verify that invalid Event date-times and non-positive time ranges are rejected without changing the task list.

### Input

```text
event impossible date /from 2019-02-29 1400 /to 2019-03-01 1600
event project meeting /from 2019-12-02 1400 /to 2019-12-02 1600
event invalid hour /from 2019-12-03 2500 /to 2019-12-03 2600
event reverse meeting /from 2019-12-04 1600 /to 2019-12-04 1400
event zero meeting /from 2019-12-05 1400 /to 2019-12-05 1400
list
bye
```

### Expected output

```text
____________________________________________________________
.____          .__       .____________        __   
|    |    __ __|__| ____ |__\______   \ _____/  |_
|    |   |  |  \  |/ ___\|  ||    |  _//  _ \   __\
|    |___|  |  /  / /_/  >  ||    |   (  <_> )  | 
|_______ \____/|__\___  /|__||______  /\____/|__|
        \/       /_____/            \/             
____________________________________________________________
Its a-me,LuigiBot!
What can I do for you?
____________________________________________________________
____________________________________________________________
Mamma mia! Use-a yyyy-MM-dd HHmm for both Event times.
____________________________________________________________
____________________________________________________________
Okie-dokie! Luigi added this task:
  [E][ ] project meeting (from: Dec 02 2019, 2:00 PM to: Dec 02 2019, 4:00 PM)
You've-a got 1 tasks now!
____________________________________________________________
____________________________________________________________
Mamma mia! Use-a yyyy-MM-dd HHmm for both Event times.
____________________________________________________________
____________________________________________________________
Mamma mia! The Event must-a end after it starts.
____________________________________________________________
____________________________________________________________
Mamma mia! The Event must-a end after it starts.
____________________________________________________________
____________________________________________________________
Let's-a see what Luigi has on the list:
1.[E][ ] project meeting (from: Dec 02 2019, 2:00 PM to: Dec 02 2019, 4:00 PM)
____________________________________________________________
Mama mia! Leaving already? Cya soon!
____________________________________________________________
```

## Test case: Reject a clashing Event

**Aim:** Verify that LuigiBot rejects an overlapping Event without changing the task list.

### Input

```text
event team meeting /from 2019-12-02 1400 /to 2019-12-02 1600
event project meeting /from 2019-12-02 1500 /to 2019-12-02 1700
list
bye
```

### Expected output

```text
____________________________________________________________
.____          .__       .____________        __   
|    |    __ __|__| ____ |__\______   \ _____/  |_
|    |   |  |  \  |/ ___\|  ||    |  _//  _ \   __\
|    |___|  |  /  / /_/  >  ||    |   (  <_> )  | 
|_______ \____/|__\___  /|__||______  /\____/|__|
        \/       /_____/            \/             
____________________________________________________________
Its a-me,LuigiBot!
What can I do for you?
____________________________________________________________
____________________________________________________________
Okie-dokie! Luigi added this task:
  [E][ ] team meeting (from: Dec 02 2019, 2:00 PM to: Dec 02 2019, 4:00 PM)
You've-a got 1 tasks now!
____________________________________________________________
____________________________________________________________
Mamma mia! Luigi can't-a add that Event because it clashes with:
1.[E][ ] team meeting (from: Dec 02 2019, 2:00 PM to: Dec 02 2019, 4:00 PM)
____________________________________________________________
____________________________________________________________
Let's-a see what Luigi has on the list:
1.[E][ ] team meeting (from: Dec 02 2019, 2:00 PM to: Dec 02 2019, 4:00 PM)
____________________________________________________________
Mama mia! Leaving already? Cya soon!
____________________________________________________________
```

## Test case: Find dated tasks on a specific date

**Aim:** Verify that the `on` command finds Deadlines and Events occurring on a date while excluding Todos.

### Input

```text
todo buy snacks
deadline submit report /by 2019-12-02 1800
event overnight meeting /from 2019-12-01 2300 /to 2019-12-02 0100
on
on 2019-12-02
on 2019-12-03
on 2019-02-29
bye
```

### Expected output

```text
____________________________________________________________
.____          .__       .____________        __   
|    |    __ __|__| ____ |__\______   \ _____/  |_
|    |   |  |  \  |/ ___\|  ||    |  _//  _ \   __\
|    |___|  |  /  / /_/  >  ||    |   (  <_> )  | 
|_______ \____/|__\___  /|__||______  /\____/|__|
        \/       /_____/            \/             
____________________________________________________________
Its a-me,LuigiBot!
What can I do for you?
____________________________________________________________
____________________________________________________________
Okie-dokie! Luigi added this task:
  [T][ ] buy snacks
You've-a got 1 tasks now!
____________________________________________________________
____________________________________________________________
Okie-dokie! Luigi added this task:
  [D][ ] submit report (by: Dec 02 2019, 6:00 PM)
You've-a got 2 tasks now!
____________________________________________________________
____________________________________________________________
Okie-dokie! Luigi added this task:
  [E][ ] overnight meeting (from: Dec 01 2019, 11:00 PM to: Dec 02 2019, 1:00 AM)
You've-a got 3 tasks now!
____________________________________________________________
____________________________________________________________
Mamma mia! Luigi needs-a date. Use: on yyyy-MM-dd.
____________________________________________________________
____________________________________________________________
Luigi found-a these tasks on Dec 02 2019:
2.[D][ ] submit report (by: Dec 02 2019, 6:00 PM)
3.[E][ ] overnight meeting (from: Dec 01 2019, 11:00 PM to: Dec 02 2019, 1:00 AM)
____________________________________________________________
____________________________________________________________
Mamma mia! Luigi found-a no tasks on Dec 03 2019.
____________________________________________________________
____________________________________________________________
Mamma mia! Use-a yyyy-MM-dd for the date.
____________________________________________________________
Mama mia! Leaving already? Cya soon!
____________________________________________________________
```

## Test case: Invalid task creation preserves numbering

**Aim:** Verify that rejected task commands do not change the task count or occupy list positions.

### Input

```text
todo read book
deadline return book
deadline return book /by 2019-12-06 1800
event meeting /from 2pm
event meeting /from 2019-12-02 1400 /to 2019-12-02 1600
todo
list
bye
```

### Expected output

```text
____________________________________________________________
.____          .__       .____________        __   
|    |    __ __|__| ____ |__\______   \ _____/  |_
|    |   |  |  \  |/ ___\|  ||    |  _//  _ \   __\
|    |___|  |  /  / /_/  >  ||    |   (  <_> )  | 
|_______ \____/|__\___  /|__||______  /\____/|__|
        \/       /_____/            \/             
____________________________________________________________
Its a-me,LuigiBot!
What can I do for you?
____________________________________________________________
____________________________________________________________
Okie-dokie! Luigi added this task:
  [T][ ] read book
You've-a got 1 tasks now!
____________________________________________________________
____________________________________________________________
Oh no! Luigi needs-a know the deadline! Use /by.
____________________________________________________________
____________________________________________________________
Okie-dokie! Luigi added this task:
  [D][ ] return book (by: Dec 06 2019, 6:00 PM)
You've-a got 2 tasks now!
____________________________________________________________
____________________________________________________________
Mamma mia! Use: event DESCRIPTION /from START /to END.
____________________________________________________________
____________________________________________________________
Okie-dokie! Luigi added this task:
  [E][ ] meeting (from: Dec 02 2019, 2:00 PM to: Dec 02 2019, 4:00 PM)
You've-a got 3 tasks now!
____________________________________________________________
____________________________________________________________
Mamma mia! The task description can't-a be empty.
____________________________________________________________
____________________________________________________________
Let's-a see what Luigi has on the list:
1.[T][ ] read book
2.[D][ ] return book (by: Dec 06 2019, 6:00 PM)
3.[E][ ] meeting (from: Dec 02 2019, 2:00 PM to: Dec 02 2019, 4:00 PM)
____________________________________________________________
Mama mia! Leaving already? Cya soon!
____________________________________________________________
```

## Test case: Invalid status commands preserve task states

**Aim:** Verify that rejected mark and unmark commands do not change existing task statuses.

### Input

```text
mark 1
todo alpha
mark 1
mark 2
todo beta
unmark 1
mark two
mark 2
unmark 3
list
bye
```

### Expected output

```text
____________________________________________________________
.____          .__       .____________        __   
|    |    __ __|__| ____ |__\______   \ _____/  |_
|    |   |  |  \  |/ ___\|  ||    |  _//  _ \   __\
|    |___|  |  /  / /_/  >  ||    |   (  <_> )  | 
|_______ \____/|__\___  /|__||______  /\____/|__|
        \/       /_____/            \/             
____________________________________________________________
Its a-me,LuigiBot!
What can I do for you?
____________________________________________________________
____________________________________________________________
Oh no! Luigi can't-a find that task number.
____________________________________________________________
____________________________________________________________
Okie-dokie! Luigi added this task:
  [T][ ] alpha
You've-a got 1 tasks now!
____________________________________________________________
____________________________________________________________
Nice-a! Luigi marked this task as done:
  [T][X] alpha
____________________________________________________________
____________________________________________________________
Oh no! Luigi can't-a find that task number.
____________________________________________________________
____________________________________________________________
Okie-dokie! Luigi added this task:
  [T][ ] beta
You've-a got 2 tasks now!
____________________________________________________________
____________________________________________________________
No problem! Luigi marked this task as not done:
  [T][ ] alpha
____________________________________________________________
____________________________________________________________
Mamma mia! Please-a enter a whole task number.
____________________________________________________________
____________________________________________________________
Nice-a! Luigi marked this task as done:
  [T][X] beta
____________________________________________________________
____________________________________________________________
Oh no! Luigi can't-a find that task number.
____________________________________________________________
____________________________________________________________
Let's-a see what Luigi has on the list:
1.[T][ ] alpha
2.[T][X] beta
____________________________________________________________
Mama mia! Leaving already? Cya soon!
____________________________________________________________
```

## Test case: Unknown commands preserve existing state

**Aim:** Verify that unknown, blank, and incorrectly capitalized commands do not alter a valid task.

### Input

```text
todo keep state
hello

mark 1
LIST
list later
bye now
list
bye
```

### Expected output

```text
____________________________________________________________
.____          .__       .____________        __   
|    |    __ __|__| ____ |__\______   \ _____/  |_
|    |   |  |  \  |/ ___\|  ||    |  _//  _ \   __\
|    |___|  |  /  / /_/  >  ||    |   (  <_> )  | 
|_______ \____/|__\___  /|__||______  /\____/|__|
        \/       /_____/            \/             
____________________________________________________________
Its a-me,LuigiBot!
What can I do for you?
____________________________________________________________
____________________________________________________________
Okie-dokie! Luigi added this task:
  [T][ ] keep state
You've-a got 1 tasks now!
____________________________________________________________
____________________________________________________________
Oh no! Luigi doesn't-a recognize that command.
____________________________________________________________
____________________________________________________________
Mamma mia! You didn't-a enter a command.
____________________________________________________________
____________________________________________________________
Nice-a! Luigi marked this task as done:
  [T][X] keep state
____________________________________________________________
____________________________________________________________
Oh no! Luigi doesn't-a recognize that command.
____________________________________________________________
____________________________________________________________
Oh no! Luigi doesn't-a recognize that command.
____________________________________________________________
____________________________________________________________
Oh no! Luigi doesn't-a recognize that command.
____________________________________________________________
____________________________________________________________
Let's-a see what Luigi has on the list:
1.[T][X] keep state
____________________________________________________________
Mama mia! Leaving already? Cya soon!
____________________________________________________________
```


## Test case: Delete tasks and preserve list state

**Aim:** Verify that valid deletions remove and renumber tasks while invalid delete commands leave the list unchanged.

### Input

```text
todo read book
deadline return book /by 2019-12-02 1800
event project meeting /from 2019-12-02 1400 /to 2019-12-02 1600
delete 2
delete
delete abc
delete 0
list
delete 2
list
bye
```

### Expected output

```text
____________________________________________________________
.____          .__       .____________        __   
|    |    __ __|__| ____ |__\______   \ _____/  |_
|    |   |  |  \  |/ ___\|  ||    |  _//  _ \   __\
|    |___|  |  /  / /_/  >  ||    |   (  <_> )  | 
|_______ \____/|__\___  /|__||______  /\____/|__|
        \/       /_____/            \/             
____________________________________________________________
Its a-me,LuigiBot!
What can I do for you?
____________________________________________________________
____________________________________________________________
Okie-dokie! Luigi added this task:
  [T][ ] read book
You've-a got 1 tasks now!
____________________________________________________________
____________________________________________________________
Okie-dokie! Luigi added this task:
  [D][ ] return book (by: Dec 02 2019, 6:00 PM)
You've-a got 2 tasks now!
____________________________________________________________
____________________________________________________________
Okie-dokie! Luigi added this task:
  [E][ ] project meeting (from: Dec 02 2019, 2:00 PM to: Dec 02 2019, 4:00 PM)
You've-a got 3 tasks now!
____________________________________________________________
____________________________________________________________
Okie-dokie! Luigi removed this task:
  [D][ ] return book (by: Dec 02 2019, 6:00 PM)
You've-a got 2 tasks now!
____________________________________________________________
____________________________________________________________
Oh no! Luigi can't-a find that task number.
____________________________________________________________
____________________________________________________________
Mamma mia! Please-a enter a whole task number.
____________________________________________________________
____________________________________________________________
Oh no! Luigi can't-a find that task number.
____________________________________________________________
____________________________________________________________
Let's-a see what Luigi has on the list:
1.[T][ ] read book
2.[E][ ] project meeting (from: Dec 02 2019, 2:00 PM to: Dec 02 2019, 4:00 PM)
____________________________________________________________
____________________________________________________________
Okie-dokie! Luigi removed this task:
  [E][ ] project meeting (from: Dec 02 2019, 2:00 PM to: Dec 02 2019, 4:00 PM)
You've-a got 1 tasks now!
____________________________________________________________
____________________________________________________________
Let's-a see what Luigi has on the list:
1.[T][ ] read book
____________________________________________________________
Mama mia! Leaving already? Cya soon!
____________________________________________________________
```

## Test case: Allow more than 100 tasks

**Aim:** Verify that collection-backed task storage can add and update task 101.

### Input

```text
todo task 1
todo task 2
todo task 3
todo task 4
todo task 5
todo task 6
todo task 7
todo task 8
todo task 9
todo task 10
todo task 11
todo task 12
todo task 13
todo task 14
todo task 15
todo task 16
todo task 17
todo task 18
todo task 19
todo task 20
todo task 21
todo task 22
todo task 23
todo task 24
todo task 25
todo task 26
todo task 27
todo task 28
todo task 29
todo task 30
todo task 31
todo task 32
todo task 33
todo task 34
todo task 35
todo task 36
todo task 37
todo task 38
todo task 39
todo task 40
todo task 41
todo task 42
todo task 43
todo task 44
todo task 45
todo task 46
todo task 47
todo task 48
todo task 49
todo task 50
todo task 51
todo task 52
todo task 53
todo task 54
todo task 55
todo task 56
todo task 57
todo task 58
todo task 59
todo task 60
todo task 61
todo task 62
todo task 63
todo task 64
todo task 65
todo task 66
todo task 67
todo task 68
todo task 69
todo task 70
todo task 71
todo task 72
todo task 73
todo task 74
todo task 75
todo task 76
todo task 77
todo task 78
todo task 79
todo task 80
todo task 81
todo task 82
todo task 83
todo task 84
todo task 85
todo task 86
todo task 87
todo task 88
todo task 89
todo task 90
todo task 91
todo task 92
todo task 93
todo task 94
todo task 95
todo task 96
todo task 97
todo task 98
todo task 99
todo task 100
todo overflow
mark 101
bye
```

### Expected output

```text
____________________________________________________________
.____          .__       .____________        __   
|    |    __ __|__| ____ |__\______   \ _____/  |_
|    |   |  |  \  |/ ___\|  ||    |  _//  _ \   __\
|    |___|  |  /  / /_/  >  ||    |   (  <_> )  | 
|_______ \____/|__\___  /|__||______  /\____/|__|
        \/       /_____/            \/             
____________________________________________________________
Its a-me,LuigiBot!
What can I do for you?
____________________________________________________________
____________________________________________________________
Okie-dokie! Luigi added this task:
  [T][ ] task 1
You've-a got 1 tasks now!
____________________________________________________________
____________________________________________________________
Okie-dokie! Luigi added this task:
  [T][ ] task 2
You've-a got 2 tasks now!
____________________________________________________________
____________________________________________________________
Okie-dokie! Luigi added this task:
  [T][ ] task 3
You've-a got 3 tasks now!
____________________________________________________________
____________________________________________________________
Okie-dokie! Luigi added this task:
  [T][ ] task 4
You've-a got 4 tasks now!
____________________________________________________________
____________________________________________________________
Okie-dokie! Luigi added this task:
  [T][ ] task 5
You've-a got 5 tasks now!
____________________________________________________________
____________________________________________________________
Okie-dokie! Luigi added this task:
  [T][ ] task 6
You've-a got 6 tasks now!
____________________________________________________________
____________________________________________________________
Okie-dokie! Luigi added this task:
  [T][ ] task 7
You've-a got 7 tasks now!
____________________________________________________________
____________________________________________________________
Okie-dokie! Luigi added this task:
  [T][ ] task 8
You've-a got 8 tasks now!
____________________________________________________________
____________________________________________________________
Okie-dokie! Luigi added this task:
  [T][ ] task 9
You've-a got 9 tasks now!
____________________________________________________________
____________________________________________________________
Okie-dokie! Luigi added this task:
  [T][ ] task 10
You've-a got 10 tasks now!
____________________________________________________________
____________________________________________________________
Okie-dokie! Luigi added this task:
  [T][ ] task 11
You've-a got 11 tasks now!
____________________________________________________________
____________________________________________________________
Okie-dokie! Luigi added this task:
  [T][ ] task 12
You've-a got 12 tasks now!
____________________________________________________________
____________________________________________________________
Okie-dokie! Luigi added this task:
  [T][ ] task 13
You've-a got 13 tasks now!
____________________________________________________________
____________________________________________________________
Okie-dokie! Luigi added this task:
  [T][ ] task 14
You've-a got 14 tasks now!
____________________________________________________________
____________________________________________________________
Okie-dokie! Luigi added this task:
  [T][ ] task 15
You've-a got 15 tasks now!
____________________________________________________________
____________________________________________________________
Okie-dokie! Luigi added this task:
  [T][ ] task 16
You've-a got 16 tasks now!
____________________________________________________________
____________________________________________________________
Okie-dokie! Luigi added this task:
  [T][ ] task 17
You've-a got 17 tasks now!
____________________________________________________________
____________________________________________________________
Okie-dokie! Luigi added this task:
  [T][ ] task 18
You've-a got 18 tasks now!
____________________________________________________________
____________________________________________________________
Okie-dokie! Luigi added this task:
  [T][ ] task 19
You've-a got 19 tasks now!
____________________________________________________________
____________________________________________________________
Okie-dokie! Luigi added this task:
  [T][ ] task 20
You've-a got 20 tasks now!
____________________________________________________________
____________________________________________________________
Okie-dokie! Luigi added this task:
  [T][ ] task 21
You've-a got 21 tasks now!
____________________________________________________________
____________________________________________________________
Okie-dokie! Luigi added this task:
  [T][ ] task 22
You've-a got 22 tasks now!
____________________________________________________________
____________________________________________________________
Okie-dokie! Luigi added this task:
  [T][ ] task 23
You've-a got 23 tasks now!
____________________________________________________________
____________________________________________________________
Okie-dokie! Luigi added this task:
  [T][ ] task 24
You've-a got 24 tasks now!
____________________________________________________________
____________________________________________________________
Okie-dokie! Luigi added this task:
  [T][ ] task 25
You've-a got 25 tasks now!
____________________________________________________________
____________________________________________________________
Okie-dokie! Luigi added this task:
  [T][ ] task 26
You've-a got 26 tasks now!
____________________________________________________________
____________________________________________________________
Okie-dokie! Luigi added this task:
  [T][ ] task 27
You've-a got 27 tasks now!
____________________________________________________________
____________________________________________________________
Okie-dokie! Luigi added this task:
  [T][ ] task 28
You've-a got 28 tasks now!
____________________________________________________________
____________________________________________________________
Okie-dokie! Luigi added this task:
  [T][ ] task 29
You've-a got 29 tasks now!
____________________________________________________________
____________________________________________________________
Okie-dokie! Luigi added this task:
  [T][ ] task 30
You've-a got 30 tasks now!
____________________________________________________________
____________________________________________________________
Okie-dokie! Luigi added this task:
  [T][ ] task 31
You've-a got 31 tasks now!
____________________________________________________________
____________________________________________________________
Okie-dokie! Luigi added this task:
  [T][ ] task 32
You've-a got 32 tasks now!
____________________________________________________________
____________________________________________________________
Okie-dokie! Luigi added this task:
  [T][ ] task 33
You've-a got 33 tasks now!
____________________________________________________________
____________________________________________________________
Okie-dokie! Luigi added this task:
  [T][ ] task 34
You've-a got 34 tasks now!
____________________________________________________________
____________________________________________________________
Okie-dokie! Luigi added this task:
  [T][ ] task 35
You've-a got 35 tasks now!
____________________________________________________________
____________________________________________________________
Okie-dokie! Luigi added this task:
  [T][ ] task 36
You've-a got 36 tasks now!
____________________________________________________________
____________________________________________________________
Okie-dokie! Luigi added this task:
  [T][ ] task 37
You've-a got 37 tasks now!
____________________________________________________________
____________________________________________________________
Okie-dokie! Luigi added this task:
  [T][ ] task 38
You've-a got 38 tasks now!
____________________________________________________________
____________________________________________________________
Okie-dokie! Luigi added this task:
  [T][ ] task 39
You've-a got 39 tasks now!
____________________________________________________________
____________________________________________________________
Okie-dokie! Luigi added this task:
  [T][ ] task 40
You've-a got 40 tasks now!
____________________________________________________________
____________________________________________________________
Okie-dokie! Luigi added this task:
  [T][ ] task 41
You've-a got 41 tasks now!
____________________________________________________________
____________________________________________________________
Okie-dokie! Luigi added this task:
  [T][ ] task 42
You've-a got 42 tasks now!
____________________________________________________________
____________________________________________________________
Okie-dokie! Luigi added this task:
  [T][ ] task 43
You've-a got 43 tasks now!
____________________________________________________________
____________________________________________________________
Okie-dokie! Luigi added this task:
  [T][ ] task 44
You've-a got 44 tasks now!
____________________________________________________________
____________________________________________________________
Okie-dokie! Luigi added this task:
  [T][ ] task 45
You've-a got 45 tasks now!
____________________________________________________________
____________________________________________________________
Okie-dokie! Luigi added this task:
  [T][ ] task 46
You've-a got 46 tasks now!
____________________________________________________________
____________________________________________________________
Okie-dokie! Luigi added this task:
  [T][ ] task 47
You've-a got 47 tasks now!
____________________________________________________________
____________________________________________________________
Okie-dokie! Luigi added this task:
  [T][ ] task 48
You've-a got 48 tasks now!
____________________________________________________________
____________________________________________________________
Okie-dokie! Luigi added this task:
  [T][ ] task 49
You've-a got 49 tasks now!
____________________________________________________________
____________________________________________________________
Okie-dokie! Luigi added this task:
  [T][ ] task 50
You've-a got 50 tasks now!
____________________________________________________________
____________________________________________________________
Okie-dokie! Luigi added this task:
  [T][ ] task 51
You've-a got 51 tasks now!
____________________________________________________________
____________________________________________________________
Okie-dokie! Luigi added this task:
  [T][ ] task 52
You've-a got 52 tasks now!
____________________________________________________________
____________________________________________________________
Okie-dokie! Luigi added this task:
  [T][ ] task 53
You've-a got 53 tasks now!
____________________________________________________________
____________________________________________________________
Okie-dokie! Luigi added this task:
  [T][ ] task 54
You've-a got 54 tasks now!
____________________________________________________________
____________________________________________________________
Okie-dokie! Luigi added this task:
  [T][ ] task 55
You've-a got 55 tasks now!
____________________________________________________________
____________________________________________________________
Okie-dokie! Luigi added this task:
  [T][ ] task 56
You've-a got 56 tasks now!
____________________________________________________________
____________________________________________________________
Okie-dokie! Luigi added this task:
  [T][ ] task 57
You've-a got 57 tasks now!
____________________________________________________________
____________________________________________________________
Okie-dokie! Luigi added this task:
  [T][ ] task 58
You've-a got 58 tasks now!
____________________________________________________________
____________________________________________________________
Okie-dokie! Luigi added this task:
  [T][ ] task 59
You've-a got 59 tasks now!
____________________________________________________________
____________________________________________________________
Okie-dokie! Luigi added this task:
  [T][ ] task 60
You've-a got 60 tasks now!
____________________________________________________________
____________________________________________________________
Okie-dokie! Luigi added this task:
  [T][ ] task 61
You've-a got 61 tasks now!
____________________________________________________________
____________________________________________________________
Okie-dokie! Luigi added this task:
  [T][ ] task 62
You've-a got 62 tasks now!
____________________________________________________________
____________________________________________________________
Okie-dokie! Luigi added this task:
  [T][ ] task 63
You've-a got 63 tasks now!
____________________________________________________________
____________________________________________________________
Okie-dokie! Luigi added this task:
  [T][ ] task 64
You've-a got 64 tasks now!
____________________________________________________________
____________________________________________________________
Okie-dokie! Luigi added this task:
  [T][ ] task 65
You've-a got 65 tasks now!
____________________________________________________________
____________________________________________________________
Okie-dokie! Luigi added this task:
  [T][ ] task 66
You've-a got 66 tasks now!
____________________________________________________________
____________________________________________________________
Okie-dokie! Luigi added this task:
  [T][ ] task 67
You've-a got 67 tasks now!
____________________________________________________________
____________________________________________________________
Okie-dokie! Luigi added this task:
  [T][ ] task 68
You've-a got 68 tasks now!
____________________________________________________________
____________________________________________________________
Okie-dokie! Luigi added this task:
  [T][ ] task 69
You've-a got 69 tasks now!
____________________________________________________________
____________________________________________________________
Okie-dokie! Luigi added this task:
  [T][ ] task 70
You've-a got 70 tasks now!
____________________________________________________________
____________________________________________________________
Okie-dokie! Luigi added this task:
  [T][ ] task 71
You've-a got 71 tasks now!
____________________________________________________________
____________________________________________________________
Okie-dokie! Luigi added this task:
  [T][ ] task 72
You've-a got 72 tasks now!
____________________________________________________________
____________________________________________________________
Okie-dokie! Luigi added this task:
  [T][ ] task 73
You've-a got 73 tasks now!
____________________________________________________________
____________________________________________________________
Okie-dokie! Luigi added this task:
  [T][ ] task 74
You've-a got 74 tasks now!
____________________________________________________________
____________________________________________________________
Okie-dokie! Luigi added this task:
  [T][ ] task 75
You've-a got 75 tasks now!
____________________________________________________________
____________________________________________________________
Okie-dokie! Luigi added this task:
  [T][ ] task 76
You've-a got 76 tasks now!
____________________________________________________________
____________________________________________________________
Okie-dokie! Luigi added this task:
  [T][ ] task 77
You've-a got 77 tasks now!
____________________________________________________________
____________________________________________________________
Okie-dokie! Luigi added this task:
  [T][ ] task 78
You've-a got 78 tasks now!
____________________________________________________________
____________________________________________________________
Okie-dokie! Luigi added this task:
  [T][ ] task 79
You've-a got 79 tasks now!
____________________________________________________________
____________________________________________________________
Okie-dokie! Luigi added this task:
  [T][ ] task 80
You've-a got 80 tasks now!
____________________________________________________________
____________________________________________________________
Okie-dokie! Luigi added this task:
  [T][ ] task 81
You've-a got 81 tasks now!
____________________________________________________________
____________________________________________________________
Okie-dokie! Luigi added this task:
  [T][ ] task 82
You've-a got 82 tasks now!
____________________________________________________________
____________________________________________________________
Okie-dokie! Luigi added this task:
  [T][ ] task 83
You've-a got 83 tasks now!
____________________________________________________________
____________________________________________________________
Okie-dokie! Luigi added this task:
  [T][ ] task 84
You've-a got 84 tasks now!
____________________________________________________________
____________________________________________________________
Okie-dokie! Luigi added this task:
  [T][ ] task 85
You've-a got 85 tasks now!
____________________________________________________________
____________________________________________________________
Okie-dokie! Luigi added this task:
  [T][ ] task 86
You've-a got 86 tasks now!
____________________________________________________________
____________________________________________________________
Okie-dokie! Luigi added this task:
  [T][ ] task 87
You've-a got 87 tasks now!
____________________________________________________________
____________________________________________________________
Okie-dokie! Luigi added this task:
  [T][ ] task 88
You've-a got 88 tasks now!
____________________________________________________________
____________________________________________________________
Okie-dokie! Luigi added this task:
  [T][ ] task 89
You've-a got 89 tasks now!
____________________________________________________________
____________________________________________________________
Okie-dokie! Luigi added this task:
  [T][ ] task 90
You've-a got 90 tasks now!
____________________________________________________________
____________________________________________________________
Okie-dokie! Luigi added this task:
  [T][ ] task 91
You've-a got 91 tasks now!
____________________________________________________________
____________________________________________________________
Okie-dokie! Luigi added this task:
  [T][ ] task 92
You've-a got 92 tasks now!
____________________________________________________________
____________________________________________________________
Okie-dokie! Luigi added this task:
  [T][ ] task 93
You've-a got 93 tasks now!
____________________________________________________________
____________________________________________________________
Okie-dokie! Luigi added this task:
  [T][ ] task 94
You've-a got 94 tasks now!
____________________________________________________________
____________________________________________________________
Okie-dokie! Luigi added this task:
  [T][ ] task 95
You've-a got 95 tasks now!
____________________________________________________________
____________________________________________________________
Okie-dokie! Luigi added this task:
  [T][ ] task 96
You've-a got 96 tasks now!
____________________________________________________________
____________________________________________________________
Okie-dokie! Luigi added this task:
  [T][ ] task 97
You've-a got 97 tasks now!
____________________________________________________________
____________________________________________________________
Okie-dokie! Luigi added this task:
  [T][ ] task 98
You've-a got 98 tasks now!
____________________________________________________________
____________________________________________________________
Okie-dokie! Luigi added this task:
  [T][ ] task 99
You've-a got 99 tasks now!
____________________________________________________________
____________________________________________________________
Okie-dokie! Luigi added this task:
  [T][ ] task 100
You've-a got 100 tasks now!
____________________________________________________________
____________________________________________________________
Okie-dokie! Luigi added this task:
  [T][ ] overflow
You've-a got 101 tasks now!
____________________________________________________________
____________________________________________________________
Nice-a! Luigi marked this task as done:
  [T][X] overflow
____________________________________________________________
Mama mia! Leaving already? Cya soon!
____________________________________________________________
```

## Test case: Save changed task list

**Aim:** Verify that task additions, status changes, and deletion are saved in the expected text format.

### Input

```text
todo read book
deadline return book /by 2019-12-02 1800
event project meeting /from 2019-12-02 1400 /to 2019-12-02 1600
mark 1
delete 2
bye
```

### Expected output

```text
____________________________________________________________
.____          .__       .____________        __   
|    |    __ __|__| ____ |__\______   \ _____/  |_
|    |   |  |  \  |/ ___\|  ||    |  _//  _ \   __\
|    |___|  |  /  / /_/  >  ||    |   (  <_> )  | 
|_______ \____/|__\___  /|__||______  /\____/|__|
        \/       /_____/            \/             
____________________________________________________________
Its a-me,LuigiBot!
What can I do for you?
____________________________________________________________
____________________________________________________________
Okie-dokie! Luigi added this task:
  [T][ ] read book
You've-a got 1 tasks now!
____________________________________________________________
____________________________________________________________
Okie-dokie! Luigi added this task:
  [D][ ] return book (by: Dec 02 2019, 6:00 PM)
You've-a got 2 tasks now!
____________________________________________________________
____________________________________________________________
Okie-dokie! Luigi added this task:
  [E][ ] project meeting (from: Dec 02 2019, 2:00 PM to: Dec 02 2019, 4:00 PM)
You've-a got 3 tasks now!
____________________________________________________________
____________________________________________________________
Nice-a! Luigi marked this task as done:
  [T][X] read book
____________________________________________________________
____________________________________________________________
Okie-dokie! Luigi removed this task:
  [D][ ] return book (by: Dec 02 2019, 6:00 PM)
You've-a got 2 tasks now!
____________________________________________________________
Mama mia! Leaving already? Cya soon!
____________________________________________________________
```

### Expected saved data

```text
T | 1 | read book
E | 0 | project meeting | 2019-12-02 1400 | 2019-12-02 1600
```

## Test case: Load and update saved tasks

**Aim:** Verify that saved task types and statuses load correctly and remain writable after startup.

### Initial saved data

```text
T | 1 | read book
D | 0 | return book | 2019-12-02 1800
E | 1 | project meeting | 2019-12-02 1400 | 2019-12-02 1600
```

### Input

```text
list
unmark 3
delete 2
todo borrow book
bye
```

### Expected output

```text
____________________________________________________________
.____          .__       .____________        __   
|    |    __ __|__| ____ |__\______   \ _____/  |_
|    |   |  |  \  |/ ___\|  ||    |  _//  _ \   __\
|    |___|  |  /  / /_/  >  ||    |   (  <_> )  | 
|_______ \____/|__\___  /|__||______  /\____/|__|
        \/       /_____/            \/             
____________________________________________________________
Its a-me,LuigiBot!
What can I do for you?
____________________________________________________________
____________________________________________________________
Let's-a see what Luigi has on the list:
1.[T][X] read book
2.[D][ ] return book (by: Dec 02 2019, 6:00 PM)
3.[E][X] project meeting (from: Dec 02 2019, 2:00 PM to: Dec 02 2019, 4:00 PM)
____________________________________________________________
____________________________________________________________
No problem! Luigi marked this task as not done:
  [E][ ] project meeting (from: Dec 02 2019, 2:00 PM to: Dec 02 2019, 4:00 PM)
____________________________________________________________
____________________________________________________________
Okie-dokie! Luigi removed this task:
  [D][ ] return book (by: Dec 02 2019, 6:00 PM)
You've-a got 2 tasks now!
____________________________________________________________
____________________________________________________________
Okie-dokie! Luigi added this task:
  [T][ ] borrow book
You've-a got 3 tasks now!
____________________________________________________________
Mama mia! Leaving already? Cya soon!
____________________________________________________________
```

### Expected saved data

```text
T | 1 | read book
E | 0 | project meeting | 2019-12-02 1400 | 2019-12-02 1600
T | 0 | borrow book
```

## Test case: Handle unavailable task file

**Aim:** Verify that task-file read and write failures are reported without terminating LuigiBot.

### Initial save path

```text
directory
```

### Input

```text
todo read book
list
bye
```

### Expected output

```text
____________________________________________________________
.____          .__       .____________        __   
|    |    __ __|__| ____ |__\______   \ _____/  |_
|    |   |  |  \  |/ ___\|  ||    |  _//  _ \   __\
|    |___|  |  /  / /_/  >  ||    |   (  <_> )  | 
|_______ \____/|__\___  /|__||______  /\____/|__|
        \/       /_____/            \/             
____________________________________________________________
Its a-me,LuigiBot!
What can I do for you?
____________________________________________________________
____________________________________________________________
Mamma mia! Luigi couldn't-a read the task file.
____________________________________________________________
____________________________________________________________
Mamma mia! Luigi couldn't-a save your tasks.
____________________________________________________________
____________________________________________________________
Okie-dokie! Luigi added this task:
  [T][ ] read book
You've-a got 1 tasks now!
____________________________________________________________
____________________________________________________________
Let's-a see what Luigi has on the list:
1.[T][ ] read book
____________________________________________________________
Mama mia! Leaving already? Cya soon!
____________________________________________________________
```
