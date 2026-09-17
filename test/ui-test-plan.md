# Console UI test plan

## Execution information

- Program: `NotMarth`
- Source directory: `src/main/java`
- Runtime: Java 25
- Build command: `javac -d <temporary-output-directory> $(find src/main/java -name '*.java')`
- Run command: `java -cp <temporary-output-directory> notmarth.NotMarth`
- Each test case runs in a fresh process.
- The test runner records the complete console input and output for each case.
- A nonzero exit status or unexpected standard-error output fails the current test case.

## Test case 1: Add a ToDo

### Aim

Verify that a task without a date or time is stored and displayed as a ToDo.

### Comparison

`contains`

### Input

```text
todo borrow book
```

### Expected output

```text
Order received. I've added it to the battle plan:
[T][ ] borrow book
Now you have 1 task in the list.
```

## Test case 2: Add a Deadline

### Aim

Verify that a deadline parses a day/month date and compact time into a typed date-time, then displays it in a readable format.

### Comparison

`contains`

### Input

```text
deadline return book /by 2/12/2019 1800
```

### Expected output

```text
Order received. I've added it to the battle plan:
[D][ ] return book (by: Dec 02 2019 6:00 PM)
Now you have 1 task in the list.
```

## Test case 3: Add an Event

### Aim

Verify that an event parses typed start and end date-time values and displays the event marker and formatted range.

### Comparison

`contains`

### Input

```text
event project meeting /from 2019-10-15 1400 /to 2019-10-15 1600
```

### Expected output

```text
Order received. I've added it to the battle plan:
[E][ ] project meeting (from: Oct 15 2019 2:00 PM to: Oct 15 2019 4:00 PM)
Now you have 1 task in the list.
```

## Test case 4: List all task types polymorphically

### Aim

Verify that ToDos, Deadlines, and Events can be stored together and listed through the common `Task` type.

### Comparison

`contains`

### Input

```text
todo borrow book
deadline return book /by 2019-10-15
event project meeting /from 2019-10-15 1400 /to 2019-10-15 1600
list
```

### Expected output

```text
1.[T][ ] borrow book
2.[D][ ] return book (by: Oct 15 2019)
3.[E][ ] project meeting (from: Oct 15 2019 2:00 PM to: Oct 15 2019 4:00 PM)
```

## Test case 5: Reject an empty ToDo without changing state

### Aim

Verify that an empty ToDo is rejected and does not create an extra task between two valid ToDos.

### Comparison

`contains`

### Input

```text
todo buy milk
todo
todo read book
list
```

### Expected output

```text
Order received. I've added it to the battle plan:
I couldn't process that, Divine One: A todo needs a description. Try: todo <description>
Order received. I've added it to the battle plan:
1.[T][ ] buy milk
2.[T][ ] read book
```

## Test case 6: Reject incomplete Deadlines between valid Deadlines

### Aim

Verify that a Deadline without its description, `/by` marker, or due-time value is rejected and does not affect the numbering of later valid tasks.

### Comparison

`contains`

### Input

```text
deadline submit report /by 2019-10-10
deadline missing due date
deadline /by 2019-10-12
deadline missing due value /by
deadline prepare presentation /by 2019-10-15
list
```

### Expected output

```text
Order received. I've added it to the battle plan:
I couldn't process that, Divine One: A deadline needs a description and a due time. Try: deadline <description> /by <date or time>
I couldn't process that, Divine One: A deadline needs a description and a due time. Try: deadline <description> /by <date or time>
I couldn't process that, Divine One: A deadline needs a description and a due time. Try: deadline <description> /by <date or time>
Order received. I've added it to the battle plan:
1.[D][ ] submit report (by: Oct 10 2019)
2.[D][ ] prepare presentation (by: Oct 15 2019)
```

## Test case 7: Reject incomplete Events without affecting later tasks

### Aim

Verify that Events missing a description, start time, or end time are rejected while valid tasks remain in the list.

### Comparison

`contains`

### Input

```text
event team meeting /from 2019-10-15 1000 /to 2019-10-15 1100
event missing end time /from 2pm
event /from 2pm /to 3pm
event missing start time /from /to 3pm
todo pack presentation materials
list
```

### Expected output

```text
Order received. I've added it to the battle plan:
I couldn't process that, Divine One: An event needs a description, start time, and end time. Try: event <description> /from <start> /to <end>
I couldn't process that, Divine One: An event needs a description, start time, and end time. Try: event <description> /from <start> /to <end>
I couldn't process that, Divine One: An event needs a description, start time, and end time. Try: event <description> /from <start> /to <end>
Order received. I've added it to the battle plan:
1.[E][ ] team meeting (from: Oct 15 2019 10:00 AM to: Oct 15 2019 11:00 AM)
2.[T][ ] pack presentation materials
```

## Test case 8: Ignore an unknown command between valid tasks

### Aim

Verify that an unknown command is rejected rather than stored as a generic task, preserving the numbering of valid tasks entered afterward.

### Comparison

`contains`

### Input

```text
todo first task
blah
deadline second task /by 2019-10-16
list
```

### Expected output

```text
Order received. I've added it to the battle plan:
I couldn't process that, Divine One: I don't recognize that command. Try todo, deadline, event, list, find, on, mark, unmark, delete, contact, listcontacts, findcontact, deletecontact, bye.
Order received. I've added it to the battle plan:
1.[T][ ] first task
2.[D][ ] second task (by: Oct 16 2019)
```

## Test case 9: Preserve completion state after invalid mark commands

### Aim

Verify that invalid mark and unmark commands do not change a task's completion state, while valid mark and unmark commands still work.

### Comparison

`contains`

### Input

```text
todo finish assignment
mark nope
mark 2
mark 1
unmark 0
unmark 1
list
```

### Expected output

```text
Order received. I've added it to the battle plan:
I couldn't process that, Divine One: Mark needs a task number, for example: mark 1
I couldn't process that, Divine One: That task number is not in your list. Use a number from 1 to 1.
Well fought! This order is complete:
Together, we can accomplish this. Engage!
[T][X] finish assignment
This order is back on the map:
[T][ ] finish assignment
1.[T][ ] finish assignment
```

## Test case 10: Reject blank input between valid tasks

### Aim

Verify that a blank line is rejected and does not create an empty task or change the numbering of valid tasks.

### Comparison

`contains`

### Input

```text
todo first task

todo second task
list
```

### Expected output

```text
Order received. I've added it to the battle plan:
I couldn't process that, Divine One: Please enter a command. Try todo, deadline, event, list, find, on, mark, unmark, delete, contact, listcontacts, findcontact, deletecontact, bye.
Order received. I've added it to the battle plan:
1.[T][ ] first task
2.[T][ ] second task
```

## Test case 11: Handle missing mark and unmark numbers

### Aim

Verify that mark and unmark commands without valid numeric arguments are rejected and leave the existing task unchanged.

### Comparison

`contains`

### Input

```text
todo keep task unchanged
mark
unmark abc
mark 999999999999999999999
list
```

### Expected output

```text
Order received. I've added it to the battle plan:
I couldn't process that, Divine One: Mark needs a task number, for example: mark 1
I couldn't process that, Divine One: Unmark needs a task number, for example: unmark 1
I couldn't process that, Divine One: Mark needs a task number, for example: mark 1
1.[T][ ] keep task unchanged
```

## Test case 12: Reject a task after reaching capacity

### Aim

Verify that the 101st task is rejected and that the first, middle, and last entries among the 100 stored tasks remain available.

### Comparison

`contains`

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
todo task 101
list
```

### Expected output

```text
Now you have 100 tasks in the list.
I couldn't process that, Divine One: Your task list is full. Remove a task before adding another one.
1.[T][ ] task 1
50.[T][ ] task 50
100.[T][ ] task 100
```

## Test case 13: Delete a task and renumber the remaining tasks

### Aim

Verify that deleting a task removes it from the list and shifts later tasks so their numbering remains consecutive.

### Comparison

`contains`

### Input

```text
todo read book
deadline return book /by 2019-06-06
event project meeting /from 2019-08-06 1400 /to 2019-08-06 1600
todo join sports club
todo borrow book
delete 3
list
```

### Expected output

```text
This order has been withdrawn:
[E][ ] project meeting (from: Aug 06 2019 2:00 PM to: Aug 06 2019 4:00 PM)
Now you have 4 tasks in the list.
1.[T][ ] read book
2.[D][ ] return book (by: Jun 06 2019)
3.[T][ ] join sports club
4.[T][ ] borrow book
```

## Test case 14: Reject invalid delete commands without changing state

### Aim

Verify that delete requires a task number and does not change the list when the number is missing or out of range.

### Comparison

`contains`

### Input

```text
todo keep this task
delete nope
delete 2
list
```

### Expected output

```text
I couldn't process that, Divine One: Delete needs a task number, for example: delete 1
I couldn't process that, Divine One: That task number is not in your list. Use a number from 1 to 1.
1.[T][ ] keep this task
```

## Test case 15: Reject task operations on an empty list

### Aim

Verify that mark, unmark, and delete commands report clear errors instead of failing when no tasks exist.

### Comparison

`contains`

### Input

```text
mark 1
unmark 1
delete 1
list
```

### Expected output

```text
I couldn't process that, Divine One: There are no tasks yet. Add a task before marking it.
I couldn't process that, Divine One: There are no tasks yet. Add a task before unmarking it.
I couldn't process that, Divine One: There are no tasks yet. Add a task before deleting it.
You currently have no mission orders, Divine One.
```

## Test case 16: Exit the chatbot

### Aim

Verify that the `bye` command prints the farewell message and terminates normally.

### Comparison

`contains`

### Input

```text
bye
```

### Expected output

```text
Until we meet again. Stay strong, Divine One!
```

## Test case 17: Discover the Sommie Easter egg

### Aim

Verify that the hidden `sommie` command displays Sommie’s companion message without changing task functionality.

### Comparison

`contains`

### Input

```text
sommie
```

### Expected output

```text
Sommie appears with a cheerful wag. Your battle plan has a loyal companion!
```

## Test case 18: Load a saved battle plan at startup

### Aim

Verify that NotMarth loads task types, descriptions, date/time fields, and completion state from its archive when it starts. Before running this case, create `data/notmarth.txt` relative to the project root with the following contents:

```text
# NotMarth battle plan v1
todo|done|review \| plan
deadline|open|return book|2019-06-06
event|done|planning|2019-10-15T14:00|2019-10-15T16:00
```

### Comparison

`contains`

### Input

```text
list
```

### Expected output

```text
Here are your current mission orders:
1.[T][X] review | plan
2.[D][ ] return book (by: Jun 06 2019)
3.[E][X] planning (from: Oct 15 2019 2:00 PM to: Oct 15 2019 4:00 PM)
```

## Test case 19: Stop when the archive is corrupted

### Aim

Verify that malformed saved data shows a clear warning and stops NotMarth before it accepts commands or overwrites the file. There is no input; the programme should exit with the error message immediately after starting up. Before running this case, create `data/notmarth.txt` relative to the project root with the following contents:

```text
# NotMarth battle plan v1
event|open|missing end time|only one field
```

### Comparison

`contains`

### Input

```text
```

### Expected output

```text

I couldn't process that, Divine One: The saved battle plan is corrupted. Repair or remove the file before starting NotMarth again.
```

## Test case 20: Create the storage folder automatically

### Aim

Verify that a first launch can save a task when the `data/` folder and archive do not exist. Before running this case, remove `data/` relative to the project root.

### Comparison

`contains`

### Input

```text
todo first launch task
```

### Expected output

```text
Order received. I've added it to the battle plan:
[T][ ] first launch task
Now you have 1 task in the list.
```

## Test case 21: Reject impossible deadline dates

### Aim

Verify that impossible calendar dates are rejected instead of being stored as text or normalized silently. Before running this case, remove `data/` relative to the project root.

### Comparison

`contains`

### Input

```text
deadline submit report /by 2019-02-30
list
```

### Expected output

```text
I couldn't process that, Divine One: That deadline date or time is not valid. Try yyyy-mm-dd or dd/MM/yyyy HHmm, for example: 2019-10-15 or 02/12/2019 1800
You currently have no mission orders, Divine One.
```

## Test case 22: Reject impossible event dates

### Aim

Verify that an event with an impossible start date is rejected instead of being stored as text or normalized silently. Before running this case, remove `data/` relative to the project root.

### Comparison

`contains`

### Input

```text
event planning /from 2019-02-30 1400 /to 2019-02-30 1600
list
```

### Expected output

```text
I couldn't process that, Divine One: That event date or time is not valid. Try yyyy-mm-dd or dd/MM/yyyy HHmm, for example: 2019-10-15 or 02/12/2019 1800
You currently have no mission orders, Divine One.
```

## Test case 23: Reject non-increasing event ranges

### Aim

Verify that an event ending before its start is rejected and does not enter the task list. Before running this case, remove `data/` relative to the project root.

### Comparison

`contains`

### Input

```text
event backwards /from 2019-02-05 /to 2019-01-04
list
```

### Expected output

```text
I couldn't process that, Divine One: An event must end after it starts. Check the /from and /to values.
You currently have no mission orders, Divine One.
```

## Test case 31: Reject malformed command spacing and duplicate tasks

### Aim

Verify that leading or repeated spaces, repeated parameters, and duplicate task details are rejected.

### Comparison
`contains`

### Input

```text
todo first task
 todo first task
todo  second task
deadline report /by 2019-10-15 /by 2019-10-16
todo first task
list
```

### Expected output

```text
Order received. I've added it to the battle plan:
I couldn't process that, Divine One: Use one space between command parts, without leading or trailing spaces.
I couldn't process that, Divine One: Use one space between command parts, without leading or trailing spaces.
I couldn't process that, Divine One: A deadline needs a description and a due time. Try: deadline <description> /by <date or time>
I couldn't process that, Divine One: That task is already in your battle plan.
1.[T][ ] first task
```

## Test case 32: Reject equal event endpoints

### Aim

Verify that an event must have a strictly later end date or time.

### Comparison
`contains`

### Input

```text
event same time /from 2019-02-05 1400 /to 2019-02-05 1400
list
```

### Expected output

```text
I couldn't process that, Divine One: An event must end after it starts. Check the /from and /to values.
You currently have no mission orders, Divine One.
```

## Test case 24: Find deadlines and events on a date

### Aim

Verify that `on <date>` lists deadlines due on the date and events spanning the date, while excluding ToDos and tasks from other dates.

### Comparison

`contains`

### Input

```text
todo read book
deadline return book /by 2019-10-15
event project meeting /from 2019-10-14 1400 /to 2019-10-16 1600
deadline submit report /by 2019-10-20
on 2019-10-15
```

### Expected output

```text
Here are the deadlines and events for Oct 15 2019:
2.[D][ ] return book (by: Oct 15 2019)
3.[E][ ] project meeting (from: Oct 14 2019 2:00 PM to: Oct 16 2019 4:00 PM)
```

## Test case 25: Show when no deadlines or events match

### Aim

Verify that a valid date with no matching deadline or event produces a clear message. This also checks the `dd/MM/yyyy` date input form.

### Comparison

`contains`

### Input

```text
on 15/10/2020
```

### Expected output

```text
No deadlines or events are scheduled for Oct 15 2020.
```

## Test case 26: Reject invalid date queries

### Aim

Verify that an impossible query date and a missing query date are rejected without changing the task list.

### Comparison

`contains`

### Input

```text
on 2019-02-30
on
```

### Expected output

```text
I couldn't process that, Divine One: That date is not valid. Try yyyy-mm-dd or dd/MM/yyyy, for example: 2019-10-15 or 15/10/2019
I couldn't process that, Divine One: The on command needs a date. Try: on <date>
```

## Test case 27: Find tasks by description keyword

### Aim

Verify that `find <keyword>` displays every task whose description contains the keyword,
ignoring letter case, while preserving the original task numbers.

### Comparison

`contains`

### Input

```text
todo read book
todo plan mission
deadline return BOOK /by 2019-06-06
find Book
```

### Expected output

```text
Here are the matching tasks in your list:
1.[T][ ] read book
3.[D][ ] return BOOK (by: Jun 06 2019)
```

## Test case 28: Handle unsuccessful task searches

### Aim

Verify that a search with no matches reports the keyword and that a missing keyword is rejected
without changing the task list.

### Comparison

`contains`

### Input

```text
todo plan mission
find dragon
find
list
```

### Expected output

```text
No tasks in your list match "dragon".
I couldn't process that, Divine One: The find command needs a keyword. Try: find <keyword>
1.[T][ ] plan mission
```

## Test case 29: Add, find, list, and delete contacts

### Aim

Verify that contacts can be added, searched by name, listed, and deleted using numbers separate
from task numbers.

### Comparison

`contains`

### Input

```text
contact Mrs Tan /phone 81234567 /address 12 Engage Road #04-05
findcontact tan
listcontacts
deletecontact 1
listcontacts
```

### Expected output

```text
Contact added to the battle plan:
Mrs Tan (phone: 81234567, address: 12 Engage Road #04-05)
Now you have 1 contact in the list.
Here are the matching contacts in your list:
1.Mrs Tan (phone: 81234567, address: 12 Engage Road #04-05)
Here are your current contacts:
1.Mrs Tan (phone: 81234567, address: 12 Engage Road #04-05)
This contact has been withdrawn:
Mrs Tan (phone: 81234567, address: 12 Engage Road #04-05)
Now you have 0 contacts in the list.
You currently have no contacts, Divine One.
```

## Test case 30: Reject incomplete contact commands

### Aim

Verify that a contact requires a name, phone number, and address.

### Comparison

`contains`

### Input

```text
contact Mrs Tan /phone 81234567
findcontact
deletecontact 1
```

### Expected output

```text
I couldn't process that, Divine One: A contact needs a name, phone number, and address. Try: contact <name> /phone <number> /address <address>
I couldn't process that, Divine One: Findcontact needs a name or keyword. Try: findcontact <keyword>
I couldn't process that, Divine One: There are no contacts yet. Add a contact before deleting one.
```

## Test case 33: Accept documented phone punctuation

### Aim

Verify that a phone number beginning with parentheses is accepted and preserved in the contact list.

### Comparison

`contains`

### Input

```text
contact Vander /phone (65)-8765-4321 /address Somniel
listcontacts
```

### Expected output

```text
Contact added to the battle plan:
Vander (phone: (65)-8765-4321, address: Somniel)
Now you have 1 contact in the list.
Here are your current contacts:
1.Vander (phone: (65)-8765-4321, address: Somniel)
```

## Test case 34: Reject phone numbers without digits or with unsupported characters

### Aim

Verify that relaxing phone punctuation does not accept letters or punctuation-only numbers.

### Comparison

`contains`

### Input

```text
contact Vander /phone ()+- /address Somniel
contact Vander /phone 8765abcd /address Somniel
listcontacts
```

### Expected output

```text
I couldn't process that, Divine One: A phone number must contain a digit and may contain only digits, spaces, +, -, and parentheses.
I couldn't process that, Divine One: A phone number must contain a digit and may contain only digits, spaces, +, -, and parentheses.
You currently have no contacts, Divine One.
```

## Test case 35: List empty tasks and contacts

### Aim

Verify that a fresh battle plan clearly reports both empty lists.

### Comparison

`contains`

### Input

```text
list
listcontacts
```

### Expected output

```text
You currently have no mission orders, Divine One.
You currently have no contacts, Divine One.
```

## Test case 36: List tasks after deleting the last mission order

### Aim

Verify that removing the final task restores the empty-list message while populated lists retain their heading.

### Comparison

`contains`

### Input

```text
todo prepare supplies
list
delete 1
list
```

### Expected output

```text
Here are your current mission orders:
1.[T][ ] prepare supplies
This order has been withdrawn:
[T][ ] prepare supplies
Now you have 0 tasks in the list.
You currently have no mission orders, Divine One.
```
