# Console UI test plan

The test runner compiles the program and starts a fresh session for each case. Expected output is exact apart from Windows/Linux line-ending differences.

## Test Case: add, list, and exit

**Aim:** Verify that a todo can be added, listed, and retained until the user exits.

### Input

```text
todo read book
list
bye
```

### Expected output

```text
____________________________________________________________
 _       ___     _
| |     / / |__ (_)_ __ ___  ___ _   _
| | /| / /| '_ \| | '_ ` _ \/ __| | | |
| |/ |/ / | | | | | | | | | \__ \ |_| |
|__/|__/  |_| |_|_|_| |_| |_|___/\__, |
                                  |___/
 ____        _
| __ )  ___ | |_ 
|  _ \ / _ \| __|
| |_) | (_) | |_
|____/ \___/ \__|

Greetings, brave planner!
Which tiny quest shall we tackle today?
I am Whimsy Bot, your cheerful questmaster for everyday tasks.
I can help you capture quests, deadlines, events, and tags.

Try these spells to begin:
  todo DESCRIPTION - add a simple quest
  deadline DESCRIPTION /by DATE - add a time-sensitive quest
  event DESCRIPTION /from START /to END - pin an appointment
  list - view your quest board
  help - open the full spellbook

Type a command below, and I will handle the busywork with a little magic.
____________________________________________________________
____________________________________________________________
A new side quest has been tucked into your satchel:
  [T][ ] read book
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Behold, your current constellation of quests:
1.[T][ ] read book
____________________________________________________________
____________________________________________________________
Farewell, brave planner! May your to-do list remain enchantingly short.
____________________________________________________________
```

## Test Case: display the help page

**Aim:** Verify that the `help` command displays guidance for the available commands and that the session remains usable afterwards.

### Input

```text
help
bye
```

### Expected output

```text
____________________________________________________________
 _       ___     _                         
| |     / / |__ (_)_ __ ___  ___ _   _     
| | /| / /| '_ \| | '_ ` _ \/ __| | | |    
| |/ |/ / | | | | | | | | | \__ \ |_| |    
|__/|__/  |_| |_|_|_| |_| |_|___/\__, |    
                                  |___/     
 ____        _   
| __ )  ___ | |_ 
|  _ \ / _ \| __|
| |_) | (_) | |_ 
|____/ \___/ \__|

Greetings, brave planner!
Which tiny quest shall we tackle today?
I am Whimsy Bot, your cheerful questmaster for everyday tasks.
I can help you capture quests, deadlines, events, and tags.

Try these spells to begin:
  todo DESCRIPTION - add a simple quest
  deadline DESCRIPTION /by DATE - add a time-sensitive quest
  event DESCRIPTION /from START /to END - pin an appointment
  list - view your quest board
  help - open the full spellbook

Type a command below, and I will handle the busywork with a little magic.
____________________________________________________________
____________________________________________________________
The spellbook is open. Here are the available incantations:
  todo DESCRIPTION - add a task without a date
  deadline DESCRIPTION /by DATE - add a task with a deadline
  event DESCRIPTION /from START /to END - add an event
  list - show all tasks
  find KEYWORD - find tasks containing a keyword
  mark NUMBER - mark a task as done
  unmark NUMBER - mark a task as not done
  tag NUMBER TAG - add a tag to a task
  untag NUMBER TAG - remove a tag from a task
  delete NUMBER - remove a task
  help - show this help page
  bye - exit Whimsy Bot
____________________________________________________________
____________________________________________________________
Farewell, brave planner! May your to-do list remain enchantingly short.
____________________________________________________________
```

## Test Case: task types and status changes

**Aim:** Verify deadline and event formatting, plus marking and unmarking a task.

### Input

```text
deadline return book /by Friday
event project meeting /from Monday 2pm /to 4pm
mark 1
unmark 1
list
bye
```

### Expected output

```text
____________________________________________________________
 _       ___     _                         
| |     / / |__ (_)_ __ ___  ___ _   _     
| | /| / /| '_ \| | '_ ` _ \/ __| | | |    
| |/ |/ / | | | | | | | | | \__ \ |_| |    
|__/|__/  |_| |_|_|_| |_| |_|___/\__, |    
                                  |___/     
 ____        _   
| __ )  ___ | |_ 
|  _ \ / _ \| __|
| |_) | (_) | |_ 
|____/ \___/ \__|

Greetings, brave planner!
Which tiny quest shall we tackle today?
I am Whimsy Bot, your cheerful questmaster for everyday tasks.
I can help you capture quests, deadlines, events, and tags.

Try these spells to begin:
  todo DESCRIPTION - add a simple quest
  deadline DESCRIPTION /by DATE - add a time-sensitive quest
  event DESCRIPTION /from START /to END - pin an appointment
  list - view your quest board
  help - open the full spellbook

Type a command below, and I will handle the busywork with a little magic.
____________________________________________________________
____________________________________________________________
A time-sensitive quest has joined the expedition:
  [D][ ] return book (by: Friday)
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
An appointment has been pinned to the adventure map:
  [E][ ] project meeting (from: Monday 2pm to: 4pm)
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Victory! One chore has been banished to the realm of Done:
  [D][X] return book (by: Friday)
____________________________________________________________
____________________________________________________________
The quest has returned from retirement:
  [D][ ] return book (by: Friday)
____________________________________________________________
____________________________________________________________
Behold, your current constellation of quests:
1.[D][ ] return book (by: Friday)
2.[E][ ] project meeting (from: Monday 2pm to: 4pm)
____________________________________________________________
____________________________________________________________
Farewell, brave planner! May your to-do list remain enchantingly short.
____________________________________________________________
```

## Test Case: delete a task

**Aim:** Verify that deleting a task removes it from the list, shifts the remaining tasks up, and reports the new task count.

### Input

```text
todo read book
deadline return book /by Friday
event project meeting /from Monday 2pm /to 4pm
delete 2
list
bye
```

### Expected output

```text
____________________________________________________________
 _       ___     _                         
| |     / / |__ (_)_ __ ___  ___ _   _     
| | /| / /| '_ \| | '_ ` _ \/ __| | | |    
| |/ |/ / | | | | | | | | | \__ \ |_| |    
|__/|__/  |_| |_|_|_| |_| |_|___/\__, |    
                                  |___/     
 ____        _   
| __ )  ___ | |_ 
|  _ \ / _ \| __|
| |_) | (_) | |_ 
|____/ \___/ \__|

Greetings, brave planner!
Which tiny quest shall we tackle today?
I am Whimsy Bot, your cheerful questmaster for everyday tasks.
I can help you capture quests, deadlines, events, and tags.

Try these spells to begin:
  todo DESCRIPTION - add a simple quest
  deadline DESCRIPTION /by DATE - add a time-sensitive quest
  event DESCRIPTION /from START /to END - pin an appointment
  list - view your quest board
  help - open the full spellbook

Type a command below, and I will handle the busywork with a little magic.
____________________________________________________________
____________________________________________________________
A new side quest has been tucked into your satchel:
  [T][ ] read book
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
A time-sensitive quest has joined the expedition:
  [D][ ] return book (by: Friday)
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
An appointment has been pinned to the adventure map:
  [E][ ] project meeting (from: Monday 2pm to: 4pm)
Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
Plucked from the quest board and sent to the archives:
  [D][ ] return book (by: Friday)
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Behold, your current constellation of quests:
1.[T][ ] read book
2.[E][ ] project meeting (from: Monday 2pm to: 4pm)
____________________________________________________________
____________________________________________________________
Farewell, brave planner! May your to-do list remain enchantingly short.
____________________________________________________________
```

## Test Case: error handling for invalid input

**Aim:** Verify that Whimsy Bot reports a clear error instead of crashing on an unrecognised command, a task added with no description, a deadline/event missing its date markers, and an out-of-range or non-numeric mark/delete index — and that it keeps working normally afterwards.

### Input

```text
blah
list now
todo
deadline return book
event project meeting
deadline invalid date /by 2026-02-30
event backwards /from 2026-09-21 /to 2026-09-20
mark 5
mark abc
delete 5
delete abc
todo read book
todo read book
list
bye
```

### Expected output

```text
____________________________________________________________
 _       ___     _                         
| |     / / |__ (_)_ __ ___  ___ _   _     
| | /| / /| '_ \| | '_ ` _ \/ __| | | |    
| |/ |/ / | | | | | | | | | \__ \ |_| |    
|__/|__/  |_| |_|_|_| |_| |_|___/\__, |    
                                  |___/     
 ____        _   
| __ )  ___ | |_ 
|  _ \ / _ \| __|
| |_) | (_) | |_ 
|____/ \___/ \__|

Greetings, brave planner!
Which tiny quest shall we tackle today?
I am Whimsy Bot, your cheerful questmaster for everyday tasks.
I can help you capture quests, deadlines, events, and tags.

Try these spells to begin:
  todo DESCRIPTION - add a simple quest
  deadline DESCRIPTION /by DATE - add a time-sensitive quest
  event DESCRIPTION /from START /to END - pin an appointment
  list - view your quest board
  help - open the full spellbook

Type a command below, and I will handle the busywork with a little magic.
____________________________________________________________
____________________________________________________________
OOPS!!! The command pixie could not decipher that spell.
Type 'help' to open the spellbook and see the commands I understand.
____________________________________________________________
____________________________________________________________
OOPS!!! The list spell does not accept any extra ingredients.
____________________________________________________________
____________________________________________________________
OOPS!!! Even the quest pixie needs a description for your todo.
____________________________________________________________
____________________________________________________________
OOPS!!! The deadline quest needs a date, e.g. 'deadline return book /by Sunday'.
____________________________________________________________
____________________________________________________________
OOPS!!! The event quest needs a start and end, e.g. 'event project meeting /from Monday 2pm /to 4pm'.
____________________________________________________________
____________________________________________________________
OOPS!!! The deadline must be a valid date, such as 2026-09-20.
____________________________________________________________
____________________________________________________________
OOPS!!! An event's start date must be before its end date.
____________________________________________________________
____________________________________________________________
OOPS!!! There is no quest number 5 on your quest board.
____________________________________________________________
____________________________________________________________
OOPS!!! Task numbers must be whole numbers, brave planner.
____________________________________________________________
____________________________________________________________
OOPS!!! There is no quest number 5 on your quest board.
____________________________________________________________
____________________________________________________________
OOPS!!! Task numbers must be whole numbers, brave planner.
____________________________________________________________
____________________________________________________________
A new side quest has been tucked into your satchel:
  [T][ ] read book
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
OOPS!!! That quest is already in your satchel.
____________________________________________________________
____________________________________________________________
Behold, your current constellation of quests:
1.[T][ ] read book
____________________________________________________________
____________________________________________________________
Farewell, brave planner! May your to-do list remain enchantingly short.
____________________________________________________________
```
