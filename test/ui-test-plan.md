# Koara UI Test Plan

## Test environment

- Entry point: `src/main/java/koara/Koara.java`
- Runtime and compiler: Java 25
- Isolation: each test case starts a new Koara process
- Comparison: exact standard output after normalizing line endings; spaces, blank lines, punctuation, letter case, and the final newline remain significant
- Failure policy: stop immediately after the first failed case
- State safety: positive and negative cases are interleaved, and invalid commands are followed by valid commands or `list` in the same session

## TC-01: Add a todo

### Aim

Verify the exact confirmation, task icon, indentation, task count, and farewell when adding a valid todo.

### Inputs

```text
todo borrow book
bye
```

### Expected output

```text
    ____________________________________________________________
      _  __  ___      _      ____       _
     | |/ / / _ \    / \    |  _ \     / \
     | ' / | | | |  / _ \   | |_) |   / _ \
     | . \ | |_| | / ___ \  |  _ <   / ___ \
     |_|\_\ \___/ /_/   \_\ |_| \_\ /_/   \_\
     Hello! I'm Koara.
     What can I do for you?
    ____________________________________________________________
    ____________________________________________________________
     Got it. I've added this task:
       [T][ ] borrow book
     Now you have 1 tasks in the list.
    ____________________________________________________________
    ____________________________________________________________
     Bye. Koara hopes to see you again soon!
    ____________________________________________________________
```

## TC-02: Reject empty and unknown commands without changing the list

### Aim

Verify the exact sample error messages and confirm that empty or unknown commands do not add tasks or stop later valid commands.

### Inputs

```text
todo

todo keep state
blah
list
bye
```

### Expected output

```text
    ____________________________________________________________
      _  __  ___      _      ____       _
     | |/ / / _ \    / \    |  _ \     / \
     | ' / | | | |  / _ \   | |_) |   / _ \
     | . \ | |_| | / ___ \  |  _ <   / ___ \
     |_|\_\ \___/ /_/   \_\ |_| \_\ /_/   \_\
     Hello! I'm Koara.
     What can I do for you?
    ____________________________________________________________
    ____________________________________________________________
     Unlucky!!! The description of a todo task cannot be empty. Try typing something more.
    ____________________________________________________________
    ____________________________________________________________
     What is that bro!!! Sorry ah, I don't know what that means :-(
    ____________________________________________________________
    ____________________________________________________________
     Got it. I've added this task:
       [T][ ] keep state
     Now you have 1 tasks in the list.
    ____________________________________________________________
    ____________________________________________________________
     What is that bro!!! Sorry ah, I don't know what that means :-(
    ____________________________________________________________
    ____________________________________________________________
     Here are the tasks in your list:
     1.[T][ ] keep state
    ____________________________________________________________
    ____________________________________________________________
     Bye. Koara hopes to see you again soon!
    ____________________________________________________________
```

## TC-03: Mark and unmark a typed task

### Aim

Verify that a formatted deadline and its type icon are retained when a valid task number is marked and unmarked.

### Inputs

```text
deadline do homework /by 2026-08-28
mark 1
unmark 1
bye
```

### Expected output

```text
    ____________________________________________________________
      _  __  ___      _      ____       _
     | |/ / / _ \    / \    |  _ \     / \
     | ' / | | | |  / _ \   | |_) |   / _ \
     | . \ | |_| | / ___ \  |  _ <   / ___ \
     |_|\_\ \___/ /_/   \_\ |_| \_\ /_/   \_\
     Hello! I'm Koara.
     What can I do for you?
    ____________________________________________________________
    ____________________________________________________________
     Got it. I've added this task:
       [D][ ] do homework (by: Aug 28 2026)
     Now you have 1 tasks in the list.
    ____________________________________________________________
    ____________________________________________________________
     Nice! I've marked this task as done:
       [D][X] do homework (by: Aug 28 2026)
    ____________________________________________________________
    ____________________________________________________________
     OK, I've marked this task as not done yet:
       [D][ ] do homework (by: Aug 28 2026)
    ____________________________________________________________
    ____________________________________________________________
     Bye. Koara hopes to see you again soon!
    ____________________________________________________________
```

## TC-04: Reject malformed typed tasks without changing the list

### Aim

Verify specific deadline and event errors while interleaving valid additions, then confirm only valid tasks appear in the correct order.

### Inputs

```text
todo first valid
deadline /by Sunday
deadline missing timing
deadline empty time /by
deadline invalid date /by Friday
deadline second valid /by 2026-09-05
event /from 2pm /to 3pm
event missing end /from 2pm
event empty start /from  /to 3pm
event empty end /from 2pm /to
event invalid date /from Saturday /to 2026-09-06
event third valid /from 2026-09-05 /to 2026-09-06
list
bye
```

### Expected output

```text
    ____________________________________________________________
      _  __  ___      _      ____       _
     | |/ / / _ \    / \    |  _ \     / \
     | ' / | | | |  / _ \   | |_) |   / _ \
     | . \ | |_| | / ___ \  |  _ <   / ___ \
     |_|\_\ \___/ /_/   \_\ |_| \_\ /_/   \_\
     Hello! I'm Koara.
     What can I do for you?
    ____________________________________________________________
    ____________________________________________________________
     Got it. I've added this task:
       [T][ ] first valid
     Now you have 1 tasks in the list.
    ____________________________________________________________
    ____________________________________________________________
     Error error!!! The description of a deadline task cannot be empty. Try typing something more.
    ____________________________________________________________
    ____________________________________________________________
     Sorry, this cannot!!! A deadline task needs a /by date or time.
    ____________________________________________________________
    ____________________________________________________________
     Sorry, this cannot!!! A deadline task needs a /by date or time.
    ____________________________________________________________
    ____________________________________________________________
     Wrong date format!!! Please use yyyy-MM-dd.
    ____________________________________________________________
    ____________________________________________________________
     Got it. I've added this task:
       [D][ ] second valid (by: Sep 05 2026)
     Now you have 2 tasks in the list.
    ____________________________________________________________
    ____________________________________________________________
     Your bad!!! The description of an event task cannot be empty. Try typing something more.
    ____________________________________________________________
    ____________________________________________________________
     Retry retry!!! An event task needs /from and /to dates or times.
    ____________________________________________________________
    ____________________________________________________________
     Wrong input!!! An event task needs /from and /to dates or times.
    ____________________________________________________________
    ____________________________________________________________
     Retry retry!!! An event task needs /from and /to dates or times.
    ____________________________________________________________
    ____________________________________________________________
     Wrong date format!!! Please use yyyy-MM-dd.
    ____________________________________________________________
    ____________________________________________________________
     Got it. I've added this task:
       [E][ ] third valid (from: Sep 05 2026 to: Sep 06 2026)
     Now you have 3 tasks in the list.
    ____________________________________________________________
    ____________________________________________________________
     Here are the tasks in your list:
     1.[T][ ] first valid
     2.[D][ ] second valid (by: Sep 05 2026)
     3.[E][ ] third valid (from: Sep 05 2026 to: Sep 06 2026)
    ____________________________________________________________
    ____________________________________________________________
     Bye. Koara hopes to see you again soon!
    ____________________________________________________________
```

## TC-05: Add and list all task types

### Aim

Verify task parsing, date conversion and display, task counts, and numbered list formatting.

### Inputs

```text
todo read book
deadline return book /by 2019-10-15
event project meeting /from 2019-12-02 /to 2019-12-03
list
bye
```

### Expected output

```text
    ____________________________________________________________
      _  __  ___      _      ____       _
     | |/ / / _ \    / \    |  _ \     / \
     | ' / | | | |  / _ \   | |_) |   / _ \
     | . \ | |_| | / ___ \  |  _ <   / ___ \
     |_|\_\ \___/ /_/   \_\ |_| \_\ /_/   \_\
     Hello! I'm Koara.
     What can I do for you?
    ____________________________________________________________
    ____________________________________________________________
     Got it. I've added this task:
       [T][ ] read book
     Now you have 1 tasks in the list.
    ____________________________________________________________
    ____________________________________________________________
     Got it. I've added this task:
       [D][ ] return book (by: Oct 15 2019)
     Now you have 2 tasks in the list.
    ____________________________________________________________
    ____________________________________________________________
     Got it. I've added this task:
       [E][ ] project meeting (from: Dec 02 2019 to: Dec 03 2019)
     Now you have 3 tasks in the list.
    ____________________________________________________________
    ____________________________________________________________
     Here are the tasks in your list:
     1.[T][ ] read book
     2.[D][ ] return book (by: Oct 15 2019)
     3.[E][ ] project meeting (from: Dec 02 2019 to: Dec 03 2019)
    ____________________________________________________________
    ____________________________________________________________
     Bye. Koara hopes to see you again soon!
    ____________________________________________________________
```

## TC-06: Reject invalid task numbers without changing statuses

### Aim

Verify missing, non-numeric, zero, negative, and out-of-range task numbers, then confirm only the valid mark command changes task status.

### Inputs

```text
todo first
todo second
mark
mark abc
mark 0
mark 3
mark 2
unmark
unmark 1.5
unmark -1
unmark 3
list
bye
```

### Expected output

```text
    ____________________________________________________________
      _  __  ___      _      ____       _
     | |/ / / _ \    / \    |  _ \     / \
     | ' / | | | |  / _ \   | |_) |   / _ \
     | . \ | |_| | / ___ \  |  _ <   / ___ \
     |_|\_\ \___/ /_/   \_\ |_| \_\ /_/   \_\
     Hello! I'm Koara.
     What can I do for you?
    ____________________________________________________________
    ____________________________________________________________
     Got it. I've added this task:
       [T][ ] first
     Now you have 1 tasks in the list.
    ____________________________________________________________
    ____________________________________________________________
     Got it. I've added this task:
       [T][ ] second
     Now you have 2 tasks in the list.
    ____________________________________________________________
    ____________________________________________________________
     Sorry for the inconvenience!!! Please specify a task number to mark.
    ____________________________________________________________
    ____________________________________________________________
     OOPS!!! The task number must be a whole number.
    ____________________________________________________________
    ____________________________________________________________
     Are you crazy!!! That task number does not exist.
    ____________________________________________________________
    ____________________________________________________________
     Are you crazy!!! That task number does not exist.
    ____________________________________________________________
    ____________________________________________________________
     Nice! I've marked this task as done:
       [T][X] second
    ____________________________________________________________
    ____________________________________________________________
     Sorry for the inconvenience!!! Please specify a task number to unmark.
    ____________________________________________________________
    ____________________________________________________________
     OOPS!!! The task number must be a whole number.
    ____________________________________________________________
    ____________________________________________________________
     Are you crazy!!! That task number does not exist.
    ____________________________________________________________
    ____________________________________________________________
     Are you crazy!!! That task number does not exist.
    ____________________________________________________________
    ____________________________________________________________
     Here are the tasks in your list:
     1.[T][ ] first
     2.[T][X] second
    ____________________________________________________________
    ____________________________________________________________
     Bye. Koara hopes to see you again soon!
    ____________________________________________________________
```

## TC-07: Delete a task from the middle of the list

### Aim

Verify the exact Level 6 deletion confirmation, updated task count, and renumbering of tasks after the deleted task.

### Inputs

```text
todo read book
deadline return book /by 2026-06-06
event project meeting /from 2026-08-06 /to 2026-08-07
todo join sports club
todo borrow book
list
delete 3
list
bye
```

### Expected output

```text
    ____________________________________________________________
      _  __  ___      _      ____       _
     | |/ / / _ \    / \    |  _ \     / \
     | ' / | | | |  / _ \   | |_) |   / _ \
     | . \ | |_| | / ___ \  |  _ <   / ___ \
     |_|\_\ \___/ /_/   \_\ |_| \_\ /_/   \_\
     Hello! I'm Koara.
     What can I do for you?
    ____________________________________________________________
    ____________________________________________________________
     Got it. I've added this task:
       [T][ ] read book
     Now you have 1 tasks in the list.
    ____________________________________________________________
    ____________________________________________________________
     Got it. I've added this task:
       [D][ ] return book (by: Jun 06 2026)
     Now you have 2 tasks in the list.
    ____________________________________________________________
    ____________________________________________________________
     Got it. I've added this task:
       [E][ ] project meeting (from: Aug 06 2026 to: Aug 07 2026)
     Now you have 3 tasks in the list.
    ____________________________________________________________
    ____________________________________________________________
     Got it. I've added this task:
       [T][ ] join sports club
     Now you have 4 tasks in the list.
    ____________________________________________________________
    ____________________________________________________________
     Got it. I've added this task:
       [T][ ] borrow book
     Now you have 5 tasks in the list.
    ____________________________________________________________
    ____________________________________________________________
     Here are the tasks in your list:
     1.[T][ ] read book
     2.[D][ ] return book (by: Jun 06 2026)
     3.[E][ ] project meeting (from: Aug 06 2026 to: Aug 07 2026)
     4.[T][ ] join sports club
     5.[T][ ] borrow book
    ____________________________________________________________
    ____________________________________________________________
     Noted. I've removed this task:
       [E][ ] project meeting (from: Aug 06 2026 to: Aug 07 2026)
     Now you have 4 tasks in the list.
    ____________________________________________________________
    ____________________________________________________________
     Here are the tasks in your list:
     1.[T][ ] read book
     2.[D][ ] return book (by: Jun 06 2026)
     3.[T][ ] join sports club
     4.[T][ ] borrow book
    ____________________________________________________________
    ____________________________________________________________
     Bye. Koara hopes to see you again soon!
    ____________________________________________________________
```

## TC-08: Reject invalid delete task numbers

### Aim

Verify that missing, non-numeric, zero, and out-of-range delete task numbers display errors and leave the task list unchanged.

### Inputs

```text
todo keep this task
delete
delete abc
delete 0
delete 2
list
bye
```

### Expected output

```text
    ____________________________________________________________
      _  __  ___      _      ____       _
     | |/ / / _ \    / \    |  _ \     / \
     | ' / | | | |  / _ \   | |_) |   / _ \
     | . \ | |_| | / ___ \  |  _ <   / ___ \
     |_|\_\ \___/ /_/   \_\ |_| \_\ /_/   \_\
     Hello! I'm Koara.
     What can I do for you?
    ____________________________________________________________
    ____________________________________________________________
     Got it. I've added this task:
       [T][ ] keep this task
     Now you have 1 tasks in the list.
    ____________________________________________________________
    ____________________________________________________________
     Sorry for the inconvenience!!! Please specify a task number to delete.
    ____________________________________________________________
    ____________________________________________________________
     OOPS!!! The task number must be a whole number.
    ____________________________________________________________
    ____________________________________________________________
     Are you crazy!!! That task number does not exist.
    ____________________________________________________________
    ____________________________________________________________
     Are you crazy!!! That task number does not exist.
    ____________________________________________________________
    ____________________________________________________________
     Here are the tasks in your list:
     1.[T][ ] keep this task
    ____________________________________________________________
    ____________________________________________________________
     Bye. Koara hopes to see you again soon!
    ____________________________________________________________
```
