# UI Test Plan

This file is the source of truth for command-line UI regression tests run by the project-specific `test-ui` skill. Inputs are sent in order to a fresh program process for each test case. Expected outputs exclude the startup banner and separator lines.

## TC01: Add, mark, and list all task types

**Aim:** Verify that todos, deadlines, and events retain their type-specific data and completion status.

### Inputs

```text
todo borrow book
deadline return book /by 2026-08-30
event project meeting /from 2026-09-01 /to 2026-09-02
mark 1
list
bye
```

### Expected outputs

#### Output 1

```text
nomnomnom ball! I've fetched this task:
  [T][ ] borrow book
Now you have 1 task in the list.
```

#### Output 2

```text
nomnomnom ball! I've fetched this task:
  [D][ ] return book (by: Aug 30 2026)
Now you have 2 tasks in the list.
```

#### Output 3

```text
nomnomnom ball! I've fetched this task:
  [E][ ] project meeting (from: Sep 1 2026 to: Sep 2 2026)
Now you have 3 tasks in the list.
```

#### Output 4

```text
Nice! I've marked this task as done:
  [T][X] borrow book
```

#### Output 5

```text
Here are the tasks in your list:
1.[T][X] borrow book
2.[D][ ] return book (by: Aug 30 2026)
3.[E][ ] project meeting (from: Sep 1 2026 to: Sep 2 2026)
```

#### Output 6

```text
i miss my owner, hope hes back soon, SIKE!
```

## TC02: Format a deadline date and unmark a task

**Aim:** Verify that an ISO deadline date is displayed clearly and a completed task can be marked incomplete again.

### Inputs

```text
deadline do homework /by 2026-10-05
mark 1
unmark 1
list
bye
```

### Expected outputs

#### Output 1

```text
nomnomnom ball! I've fetched this task:
  [D][ ] do homework (by: Oct 5 2026)
Now you have 1 task in the list.
```

#### Output 2

```text
Nice! I've marked this task as done:
  [D][X] do homework (by: Oct 5 2026)
```

#### Output 3

```text
OK, I've marked this task as not done yet:
  [D][ ] do homework (by: Oct 5 2026)
```

#### Output 4

```text
Here are the tasks in your list:
1.[D][ ] do homework (by: Oct 5 2026)
```

#### Output 5

```text
i miss my owner, hope hes back soon, SIKE!
```

## TC03: Explain invalid commands and arguments

**Aim:** Verify that every current command reports malformed input through clear chatbot-specific exception messages and continues running.

### Inputs

```text
todo
deadline
deadline /by Sunday
deadline submit /by
event
event meeting /from Monday
event /from Monday /to Friday
event meeting /from /to Friday
event meeting /from Monday /to
mark
unmark 1
todo valid task
mark abc
mark 2
unmark 0
blah
bye
```

### Expected outputs

#### Output 1

```text
i pooped on myself... The description of a todo cannot be empty. Try: todo <description>
```

#### Output 2

```text
i pooped on myself... A deadline needs '/by <date or time>'. Try: deadline <description> /by <date or time>
```

#### Output 3

```text
i pooped on myself... The description of a deadline cannot be empty. Try: deadline <description>
```

#### Output 4

```text
i pooped on myself... Please enter a date after /by.
```

#### Output 5

```text
i pooped on myself... An event needs '/from <start>'. Try: event <description> /from <start> /to <end>
```

#### Output 6

```text
i pooped on myself... An event needs '/to <end>'. Try: event <description> /from <start> /to <end>
```

#### Output 7

```text
i pooped on myself... The description of an event cannot be empty. Try: event <description>
```

#### Output 8

```text
i pooped on myself... Please enter a start date after /from.
```

#### Output 9

```text
i pooped on myself... Please enter an end date after /to.
```

#### Output 10

```text
i pooped on myself... Please enter a task number after mark.
```

#### Output 11

```text
i pooped on myself... Your task list is empty, so there is nothing to unmark.
```

#### Output 12

```text
nomnomnom ball! I've fetched this task:
  [T][ ] valid task
Now you have 1 task in the list.
```

#### Output 13

```text
i pooped on myself... 'abc' is not a valid task number. Try: mark <task number>
```

#### Output 14

```text
i pooped on myself... Task 2 does not exist. Choose a number from 1 to 1.
```

#### Output 15

```text
i pooped on myself... Task 0 does not exist. Choose a number from 1 to 1.
```

#### Output 16

```text
i pooped on myself... I don't know what 'blah' means. Try todo, deadline, event, list, find, mark, unmark, delete, or bye.
```

#### Output 17

```text
i miss my owner, hope hes back soon, SIKE!
```

## TC04: Delete tasks and close array gaps

**Aim:** Verify that deleting middle, first, and last tasks preserves list order and that deleting from an empty list is handled safely.

### Inputs

```text
todo first task
todo middle task
todo last task
delete 2
list
delete 1
delete 1
list
delete 1
bye
```

### Expected outputs

#### Output 1

```text
nomnomnom ball! I've fetched this task:
  [T][ ] first task
Now you have 1 task in the list.
```

#### Output 2

```text
nomnomnom ball! I've fetched this task:
  [T][ ] middle task
Now you have 2 tasks in the list.
```

#### Output 3

```text
nomnomnom ball! I've fetched this task:
  [T][ ] last task
Now you have 3 tasks in the list.
```

#### Output 4

```text
DELETED. I've removed this task:
  [T][ ] middle task
Now you have 2 tasks in the list.
```

#### Output 5

```text
Here are the tasks in your list:
1.[T][ ] first task
2.[T][ ] last task
```

#### Output 6

```text
DELETED. I've removed this task:
  [T][ ] first task
Now you have 1 task in the list.
```

#### Output 7

```text
DELETED. I've removed this task:
  [T][ ] last task
Now you have 0 tasks in the list.
```

#### Output 8

```text
Here are the tasks in your list:
```

#### Output 9

```text
i pooped on myself... Your task list is empty, so there is nothing to delete.
```

#### Output 10

```text
i miss my owner, hope hes back soon, SIKE!
```

## TC05: Parse and validate task dates

**Aim:** Verify that valid ISO dates are reformatted and invalid calendar dates are rejected clearly.

### Inputs

```text
deadline leap-day task /by 2024-02-29
deadline impossible date /by 2023-02-29
deadline wrong format /by 29-02-2024
event holiday /from 2026-12-24 /to 2026-12-26
event bad start /from tomorrow /to 2026-12-26
event bad end /from 2026-12-24 /to next-week
bye
```

### Expected outputs

#### Output 1

```text
nomnomnom ball! I've fetched this task:
  [D][ ] leap-day task (by: Feb 29 2024)
Now you have 1 task in the list.
```

#### Output 2

```text
i pooped on myself... Please enter the date after /by as yyyy-MM-dd, for example 2019-10-15.
```

#### Output 3

```text
i pooped on myself... Please enter the date after /by as yyyy-MM-dd, for example 2019-10-15.
```

#### Output 4

```text
nomnomnom ball! I've fetched this task:
  [E][ ] holiday (from: Dec 24 2026 to: Dec 26 2026)
Now you have 2 tasks in the list.
```

#### Output 5

```text
i pooped on myself... Please enter the date after /from as yyyy-MM-dd, for example 2019-10-15.
```

#### Output 6

```text
i pooped on myself... Please enter the date after /to as yyyy-MM-dd, for example 2019-10-15.
```

#### Output 7

```text
i miss my owner, hope hes back soon, SIKE!
```

## TC06: Find tasks by description keyword

**Aim:** Verify that find returns matching tasks in their original order and handles missing matches and keywords.

### Inputs

```text
todo read book
deadline return book /by 2026-09-01
todo buy groceries
find book
find bok
find magazine
find
bye
```

### Expected outputs

#### Output 1

```text
nomnomnom ball! I've fetched this task:
  [T][ ] read book
Now you have 1 task in the list.
```

#### Output 2

```text
nomnomnom ball! I've fetched this task:
  [D][ ] return book (by: Sep 1 2026)
Now you have 2 tasks in the list.
```

#### Output 3

```text
nomnomnom ball! I've fetched this task:
  [T][ ] buy groceries
Now you have 3 tasks in the list.
```

#### Output 4

```text
Here are the matching tasks in your list:
1.[T][ ] read book
2.[D][ ] return book (by: Sep 1 2026)
```

#### Output 5

```text
I couldn't find an exact match. Here are the most similar tasks:
1.[T][ ] read book
2.[D][ ] return book (by: Sep 1 2026)
```

#### Output 6

```text
I couldn't find any tasks matching 'magazine'.
```

#### Output 7

```text
i pooped on myself... Please enter a keyword after find. Try: find <keyword>
```

#### Output 8

```text
i miss my owner, hope hes back soon, SIKE!
```

## TC07: Recover from malformed input and inconsistent data

**Aim:** Verify that whitespace is normalized, meaningful punctuation remains valid, duplicate details and parameters are rejected, and event ranges are chronological.

### Inputs

```text
  todo   call @vet!
todo call @vet!
deadline report /by 2026-09-20 /by 2026-09-21
event trip /from 2026-09-20 /from 2026-09-21 /to 2026-09-22
event trip /from 2026-09-20 /to 2026-09-21 /to 2026-09-22
event trip /to 2026-09-21 /from 2026-09-20
event holiday /from 2026-09-20 /to 2026-09-19
event workshop /from 2026-09-20 /to 2026-09-20
bye
```

### Expected outputs

#### Output 1

```text
nomnomnom ball! I've fetched this task:
  [T][ ] call @vet!
Now you have 1 task in the list.
```

#### Output 2

```text
i pooped on myself... That task is already in your list.
```

#### Output 3

```text
i pooped on myself... Use /by only once in a deadline command.
```

#### Output 4

```text
i pooped on myself... Use /from only once in an event command.
```

#### Output 5

```text
i pooped on myself... Use /to only once in an event command.
```

#### Output 6

```text
i pooped on myself... Place /from before /to in an event command.
```

#### Output 7

```text
i pooped on myself... The event end date cannot be before its start date.
```

#### Output 8

```text
nomnomnom ball! I've fetched this task:
  [E][ ] workshop (from: Sep 20 2026 to: Sep 20 2026)
Now you have 2 tasks in the list.
```

#### Output 9

```text
i miss my owner, hope hes back soon, SIKE!
```
