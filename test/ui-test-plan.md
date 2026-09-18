# UI Test Plan

Record console UI test cases here. Each test case must include an aim, an `input` block, and an `expected` block.

## Test Case: Add todo and list

Aim: Verify that a todo task is stored, listed, and followed by the bye message.

```input
todo borrow book
list
bye
```

```expected
____________________________________________________________
 ____        _     _           
| __ )  ___ | |__ | |__  _   _ 
|  _ \ / _ \| '_ \| '_ \| | | |
| |_) | (_) | |_) | |_) | |_| |
|____/ \___/|_.__/|_.__/ \__, |
                         |___/ 
Greetings, young one.
I am Bobby.
Share your task, and we shall bring order to the day.
____________________________________________________________
____________________________________________________________
The seed has been planted:
  [T][ ] borrow book
Now 1 tasks grow in the grove.
____________________________________________________________
____________________________________________________________
These are the stones upon your path:
1.[T][ ] borrow book
____________________________________________________________
____________________________________________________________
The pond grows still. Until our paths meet again...
____________________________________________________________
```

## Test Case: Invalid additions do not change task list

Aim: Verify that invalid deadline, event, and mark commands do not add tasks or change valid task numbering.

```input
todo read book
deadline /by 2019-12-08
deadline return book /by 2019-12-08
event project meeting /from /to 2019-12-02 1600
event project meeting /from 2019-12-02 1400 /to 2019-12-02 1600
mark 5
mark 2
list
bye
```

```expected
____________________________________________________________
 ____        _     _           
| __ )  ___ | |__ | |__  _   _ 
|  _ \ / _ \| '_ \| '_ \| | | |
| |_) | (_) | |_) | |_) | |_| |
|____/ \___/|_.__/|_.__/ \__, |
                         |___/ 
Greetings, young one.
I am Bobby.
Share your task, and we shall bring order to the day.
____________________________________________________________
____________________________________________________________
The seed has been planted:
  [T][ ] read book
Now 1 tasks grow in the grove.
____________________________________________________________
____________________________________________________________
The path is misty: The description of a deadline cannot be empty.
____________________________________________________________
____________________________________________________________
The seed has been planted:
  [D][ ] return book (by: Dec 8 2019)
Now 2 tasks grow in the grove.
____________________________________________________________
____________________________________________________________
The path is misty: The /from part of an event cannot be empty.
____________________________________________________________
____________________________________________________________
The seed has been planted:
  [E][ ] project meeting (from: Dec 2 2019, 2:00pm to: Dec 2 2019, 4:00pm)
Now 3 tasks grow in the grove.
____________________________________________________________
____________________________________________________________
The path is misty: I couldn't find that task number.
____________________________________________________________
____________________________________________________________
Peace. This task now rests complete:
  [D][X] return book (by: Dec 8 2019)
____________________________________________________________
____________________________________________________________
These are the stones upon your path:
1.[T][ ] read book
2.[D][X] return book (by: Dec 8 2019)
3.[E][ ] project meeting (from: Dec 2 2019, 2:00pm to: Dec 2 2019, 4:00pm)
____________________________________________________________
____________________________________________________________
The pond grows still. Until our paths meet again...
____________________________________________________________
```

## Test Case: Find tasks by keyword

Aim: Verify that find lists matching task descriptions, handles no matches, and rejects a missing keyword.

```input
todo read book
deadline return book /by 2019-12-08
todo buy milk
mark 1
mark 2
find book
find chocolate
find
bye
```

```expected
____________________________________________________________
 ____        _     _           
| __ )  ___ | |__ | |__  _   _ 
|  _ \ / _ \| '_ \| '_ \| | | |
| |_) | (_) | |_) | |_) | |_| |
|____/ \___/|_.__/|_.__/ \__, |
                         |___/ 
Greetings, young one.
I am Bobby.
Share your task, and we shall bring order to the day.
____________________________________________________________
____________________________________________________________
The seed has been planted:
  [T][ ] read book
Now 1 tasks grow in the grove.
____________________________________________________________
____________________________________________________________
The seed has been planted:
  [D][ ] return book (by: Dec 8 2019)
Now 2 tasks grow in the grove.
____________________________________________________________
____________________________________________________________
The seed has been planted:
  [T][ ] buy milk
Now 3 tasks grow in the grove.
____________________________________________________________
____________________________________________________________
Peace. This task now rests complete:
  [T][X] read book
____________________________________________________________
____________________________________________________________
Peace. This task now rests complete:
  [D][X] return book (by: Dec 8 2019)
____________________________________________________________
____________________________________________________________
The pond reflects these matching ripples:
1.[T][X] read book
2.[D][X] return book (by: Dec 8 2019)
____________________________________________________________
____________________________________________________________
The pond reflects these matching ripples:
____________________________________________________________
____________________________________________________________
The path is misty: Please provide a keyword after find.
____________________________________________________________
____________________________________________________________
The pond grows still. Until our paths meet again...
____________________________________________________________
```

## Test Case: Tag and find tasks by tag

Aim: Verify that tagging a task displays the tag, allows finding by tag, and rejects invalid tag text.

```input
todo read book
todo buy milk
tag 1 #reading
tag 2 errand
list
find #reading
bye
```

```expected
____________________________________________________________
 ____        _     _           
| __ )  ___ | |__ | |__  _   _ 
|  _ \ / _ \| '_ \| '_ \| | | |
| |_) | (_) | |_) | |_) | |_| |
|____/ \___/|_.__/|_.__/ \__, |
                         |___/ 
Greetings, young one.
I am Bobby.
Share your task, and we shall bring order to the day.
____________________________________________________________
____________________________________________________________
The seed has been planted:
  [T][ ] read book
Now 1 tasks grow in the grove.
____________________________________________________________
____________________________________________________________
The seed has been planted:
  [T][ ] buy milk
Now 2 tasks grow in the grove.
____________________________________________________________
____________________________________________________________
A small mark of meaning is tied to this task:
  [T][ ] read book #reading
____________________________________________________________
____________________________________________________________
The path is misty: Tags should start with # and use only letters, numbers, underscores, or hyphens.
____________________________________________________________
____________________________________________________________
These are the stones upon your path:
1.[T][ ] read book #reading
2.[T][ ] buy milk
____________________________________________________________
____________________________________________________________
The pond reflects these matching ripples:
1.[T][ ] read book #reading
____________________________________________________________
____________________________________________________________
The pond grows still. Until our paths meet again...
____________________________________________________________
```

## Test Case: Invalid fields do not affect status updates

Aim: Verify that empty date fields and invalid unmark numbers do not change existing task statuses.

```input
deadline write report /by 2019-12-06
deadline submit report /by
event workshop /from 2019-12-02 0900 /to
todo buy milk
mark 1
unmark two
unmark 1
list
bye
```

```expected
____________________________________________________________
 ____        _     _           
| __ )  ___ | |__ | |__  _   _ 
|  _ \ / _ \| '_ \| '_ \| | | |
| |_) | (_) | |_) | |_) | |_| |
|____/ \___/|_.__/|_.__/ \__, |
                         |___/ 
Greetings, young one.
I am Bobby.
Share your task, and we shall bring order to the day.
____________________________________________________________
____________________________________________________________
The seed has been planted:
  [D][ ] write report (by: Dec 6 2019)
Now 1 tasks grow in the grove.
____________________________________________________________
____________________________________________________________
The path is misty: The /by part of a deadline cannot be empty.
____________________________________________________________
____________________________________________________________
The path is misty: The /to part of an event cannot be empty.
____________________________________________________________
____________________________________________________________
The seed has been planted:
  [T][ ] buy milk
Now 2 tasks grow in the grove.
____________________________________________________________
____________________________________________________________
Peace. This task now rests complete:
  [D][X] write report (by: Dec 6 2019)
____________________________________________________________
____________________________________________________________
The path is misty: Task numbers should be whole numbers.
____________________________________________________________
____________________________________________________________
Patience. This task returns to the path:
  [D][ ] write report (by: Dec 6 2019)
____________________________________________________________
____________________________________________________________
These are the stones upon your path:
1.[D][ ] write report (by: Dec 6 2019)
2.[T][ ] buy milk
____________________________________________________________
____________________________________________________________
The pond grows still. Until our paths meet again...
____________________________________________________________
```

## Test Case: Reject invalid commands

Aim: Verify that invalid commands produce clear error messages and the chatbot keeps running.

```input
todo
blah
deadline /by 2019-12-08
deadline return book
event project meeting /from 2019-12-02 1400
mark
mark abc
mark 1
bye
```

```expected
____________________________________________________________
 ____        _     _           
| __ )  ___ | |__ | |__  _   _ 
|  _ \ / _ \| '_ \| '_ \| | | |
| |_) | (_) | |_) | |_) | |_| |
|____/ \___/|_.__/|_.__/ \__, |
                         |___/ 
Greetings, young one.
I am Bobby.
Share your task, and we shall bring order to the day.
____________________________________________________________
____________________________________________________________
The path is misty: The description of a todo cannot be empty.
____________________________________________________________
____________________________________________________________
The path is misty: I don't know what that means yet.
____________________________________________________________
____________________________________________________________
The path is misty: The description of a deadline cannot be empty.
____________________________________________________________
____________________________________________________________
The path is misty: Please tell me the deadline using /by.
____________________________________________________________
____________________________________________________________
The path is misty: Please tell me the event time using /from and /to.
____________________________________________________________
____________________________________________________________
The path is misty: Please provide a task number after mark.
____________________________________________________________
____________________________________________________________
The path is misty: Task numbers should be whole numbers.
____________________________________________________________
____________________________________________________________
The path is misty: I couldn't find that task number.
____________________________________________________________
____________________________________________________________
The pond grows still. Until our paths meet again...
____________________________________________________________
```

## Test Case: Delete task and renumber list

Aim: Verify that deleting a task removes it, renumbers later tasks, and rejects invalid delete numbers.

```input
todo read book
deadline return book /by 2019-12-08
event project meeting /from 2019-12-02 1400 /to 2019-12-02 1600
delete 2
list
delete 5
delete abc
bye
```

```expected
____________________________________________________________
 ____        _     _           
| __ )  ___ | |__ | |__  _   _ 
|  _ \ / _ \| '_ \| '_ \| | | |
| |_) | (_) | |_) | |_) | |_| |
|____/ \___/|_.__/|_.__/ \__, |
                         |___/ 
Greetings, young one.
I am Bobby.
Share your task, and we shall bring order to the day.
____________________________________________________________
____________________________________________________________
The seed has been planted:
  [T][ ] read book
Now 1 tasks grow in the grove.
____________________________________________________________
____________________________________________________________
The seed has been planted:
  [D][ ] return book (by: Dec 8 2019)
Now 2 tasks grow in the grove.
____________________________________________________________
____________________________________________________________
The seed has been planted:
  [E][ ] project meeting (from: Dec 2 2019, 2:00pm to: Dec 2 2019, 4:00pm)
Now 3 tasks grow in the grove.
____________________________________________________________
____________________________________________________________
The leaf is released:
  [D][ ] return book (by: Dec 8 2019)
Now 2 tasks remain on the branch.
____________________________________________________________
____________________________________________________________
These are the stones upon your path:
1.[T][ ] read book
2.[E][ ] project meeting (from: Dec 2 2019, 2:00pm to: Dec 2 2019, 4:00pm)
____________________________________________________________
____________________________________________________________
The path is misty: I couldn't find that task number.
____________________________________________________________
____________________________________________________________
The path is misty: Task numbers should be whole numbers.
____________________________________________________________
____________________________________________________________
The pond grows still. Until our paths meet again...
____________________________________________________________
```

## Test Case: Reject malformed and duplicate details

Aim: Verify that Bobby normalizes spacing and rejects duplicate tasks, invalid times, unsafe descriptions, and extra task numbers.

```input
todo   read    book
todo read book
deadline report /by 2019-02-30
deadline report /by 2019-12-02 /by 2019-12-03
event meeting /from 2019-12-02 1600 /to 2019-12-02 1400
event sync /from 2019-12-02 1400 /to 2019-12-02 1600
tag 2 #team
tag 2 #team
tag 2 #bad!
todo pipes | break
deadline pipes | break /by 2019-12-02
event pipes | break /from 2019-12-02 1400 /to 2019-12-02 1600
mark 1 2
list
bye
```

```expected
____________________________________________________________
 ____        _     _           
| __ )  ___ | |__ | |__  _   _ 
|  _ \ / _ \| '_ \| '_ \| | | |
| |_) | (_) | |_) | |_) | |_| |
|____/ \___/|_.__/|_.__/ \__, |
                         |___/ 
Greetings, young one.
I am Bobby.
Share your task, and we shall bring order to the day.
____________________________________________________________
____________________________________________________________
The seed has been planted:
  [T][ ] read book
Now 1 tasks grow in the grove.
____________________________________________________________
____________________________________________________________
The path is misty: That task is already in your list.
____________________________________________________________
____________________________________________________________
The path is misty: Please use a date format like 2019-12-02 or 2/12/2019 1800.
____________________________________________________________
____________________________________________________________
The path is misty: Please include /by only once.
____________________________________________________________
____________________________________________________________
The path is misty: The event start time should be before the end time.
____________________________________________________________
____________________________________________________________
The seed has been planted:
  [E][ ] sync (from: Dec 2 2019, 2:00pm to: Dec 2 2019, 4:00pm)
Now 2 tasks grow in the grove.
____________________________________________________________
____________________________________________________________
A small mark of meaning is tied to this task:
  [E][ ] sync (from: Dec 2 2019, 2:00pm to: Dec 2 2019, 4:00pm) #team
____________________________________________________________
____________________________________________________________
The path is misty: That task already has this tag.
____________________________________________________________
____________________________________________________________
The path is misty: Tags should start with # and use only letters, numbers, underscores, or hyphens.
____________________________________________________________
____________________________________________________________
The path is misty: Descriptions cannot contain | or control characters.
____________________________________________________________
____________________________________________________________
The path is misty: Descriptions cannot contain | or control characters.
____________________________________________________________
____________________________________________________________
The path is misty: Descriptions cannot contain | or control characters.
____________________________________________________________
____________________________________________________________
The path is misty: Please provide only one task number after mark.
____________________________________________________________
____________________________________________________________
These are the stones upon your path:
1.[T][ ] read book
2.[E][ ] sync (from: Dec 2 2019, 2:00pm to: Dec 2 2019, 4:00pm) #team
____________________________________________________________
____________________________________________________________
The pond grows still. Until our paths meet again...
____________________________________________________________
```

## Test Case: Load saved tasks

Aim: Verify that tasks saved in data/bobby.txt are loaded when the chatbot starts.

```data
T | 1 | read book
D | 0 | return book | 2019-12-08T00:00
E | 0 | project meeting | 2019-12-02T14:00 | 2019-12-02T16:00
```

```input
list
bye
```

```expected
____________________________________________________________
 ____        _     _           
| __ )  ___ | |__ | |__  _   _ 
|  _ \ / _ \| '_ \| '_ \| | | |
| |_) | (_) | |_) | |_) | |_| |
|____/ \___/|_.__/|_.__/ \__, |
                         |___/ 
Greetings, young one.
I am Bobby.
Share your task, and we shall bring order to the day.
____________________________________________________________
____________________________________________________________
These are the stones upon your path:
1.[T][X] read book
2.[D][ ] return book (by: Dec 8 2019)
3.[E][ ] project meeting (from: Dec 2 2019, 2:00pm to: Dec 2 2019, 4:00pm)
____________________________________________________________
____________________________________________________________
The pond grows still. Until our paths meet again...
____________________________________________________________
```

## Test Case: Save task changes

Aim: Verify that adding, marking, and deleting tasks update data/bobby.txt.

```input
todo alpha
deadline beta /by 2019-12-06
event gamma /from 2019-12-02 1300 /to 2019-12-02 1400
mark 2
tag 2 #urgent
delete 1
bye
```

```expected
____________________________________________________________
 ____        _     _           
| __ )  ___ | |__ | |__  _   _ 
|  _ \ / _ \| '_ \| '_ \| | | |
| |_) | (_) | |_) | |_) | |_| |
|____/ \___/|_.__/|_.__/ \__, |
                         |___/ 
Greetings, young one.
I am Bobby.
Share your task, and we shall bring order to the day.
____________________________________________________________
____________________________________________________________
The seed has been planted:
  [T][ ] alpha
Now 1 tasks grow in the grove.
____________________________________________________________
____________________________________________________________
The seed has been planted:
  [D][ ] beta (by: Dec 6 2019)
Now 2 tasks grow in the grove.
____________________________________________________________
____________________________________________________________
The seed has been planted:
  [E][ ] gamma (from: Dec 2 2019, 1:00pm to: Dec 2 2019, 2:00pm)
Now 3 tasks grow in the grove.
____________________________________________________________
____________________________________________________________
Peace. This task now rests complete:
  [D][X] beta (by: Dec 6 2019)
____________________________________________________________
____________________________________________________________
A small mark of meaning is tied to this task:
  [D][X] beta (by: Dec 6 2019) #urgent
____________________________________________________________
____________________________________________________________
The leaf is released:
  [T][ ] alpha
Now 2 tasks remain on the branch.
____________________________________________________________
____________________________________________________________
The pond grows still. Until our paths meet again...
____________________________________________________________
```

```saved
D | 1 | beta | 2019-12-06T00:00 | #urgent
E | 0 | gamma | 2019-12-02T13:00 | 2019-12-02T14:00
```

## Test Case: Parse deadline date and time

Aim: Verify that slash-format date-time input is parsed, displayed clearly, and saved in ISO format.

```input
deadline return book /by 2/12/2019 1800
deadline bad date /by no idea
list
bye
```

```expected
____________________________________________________________
 ____        _     _           
| __ )  ___ | |__ | |__  _   _ 
|  _ \ / _ \| '_ \| '_ \| | | |
| |_) | (_) | |_) | |_) | |_| |
|____/ \___/|_.__/|_.__/ \__, |
                         |___/ 
Greetings, young one.
I am Bobby.
Share your task, and we shall bring order to the day.
____________________________________________________________
____________________________________________________________
The seed has been planted:
  [D][ ] return book (by: Dec 2 2019, 6:00pm)
Now 1 tasks grow in the grove.
____________________________________________________________
____________________________________________________________
The path is misty: Please use a date format like 2019-12-02 or 2/12/2019 1800.
____________________________________________________________
____________________________________________________________
These are the stones upon your path:
1.[D][ ] return book (by: Dec 2 2019, 6:00pm)
____________________________________________________________
____________________________________________________________
The pond grows still. Until our paths meet again...
____________________________________________________________
```

```saved
D | 0 | return book | 2019-12-02T18:00
```
