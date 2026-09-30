# UI Test Plan

These test cases check the Kong chatbot through console input and output.

For each test case, the expected output lists the important lines that must appear in order. The banner and separator lines do not need to be repeated unless they are the behavior being tested.

## TC1 Empty todo description

**Aim:** Check that `todo` without a description is rejected.

### Input
```text
todo
bye
```

### Expected Output
```text
Invalid command. A todo command needs to be in the following format: todo <description>
Cheerio! Until our next scholarly consultation.
```

## TC2 Unknown command

**Aim:** Check that an unrecognized command is rejected.

### Input
```text
blah
bye
```

### Expected Output
```text
Confound it! I do not recognise that command in my lexicon.
Cheerio! Until our next scholarly consultation.
```

## TC3 Empty deadline description

**Aim:** Check that a deadline with no description is rejected.

### Input
```text
deadline /by tomorrow
bye
```

### Expected Output
```text
Invalid command. A deadline command needs to be in the following format: deadline <description> /by <date>
Cheerio! Until our next scholarly consultation.
```

## TC4 Empty event from value

**Aim:** Check that an event with no `/from` value is rejected.

### Input
```text
event party /from /to 5pm
bye
```

### Expected Output
```text
Invalid command. An event command needs to be in the following format: event <description> /from <date> /to <date>
Cheerio! Until our next scholarly consultation.
```

## TC5 Invalid mark number

**Aim:** Check that marking a task outside the list gives an error.

### Input
```text
mark 1
bye
```

### Expected Output
```text
This task number is invalid. You currently have 0 tasks in your list.
Cheerio! Until our next scholarly consultation.
```


## TC6 Task changes are saved

**Aim:** Check that successful task list changes are written to `data/duke.txt`.

### Input
```text
todo read book
deadline return book /by 2019-10-15
event project meeting /from 2019-10-15 /to 2019-10-16
mark 3
delete 1
bye
```

### Expected Output
```text
Splendid addition! I have inscribed this task into your archives:
[T][ ] read book
Splendid addition! I have inscribed this task into your archives:
[D][ ] return book (by: Oct 15 2019)
Splendid addition! I have inscribed this task into your archives:
[E][ ] project meeting (from: Oct 15 2019 to: Oct 16 2019)
Capital progress! I have marked this task as completed:
[T][X] read book
Expunged from the archives! The following task has been removed:
[D][ ] return book (by: Oct 15 2019)
Cheerio! Until our next scholarly consultation.
```

### Expected Saved File
```text
E | 0 | project meeting | 2019-10-15 | 2019-10-16
T | 1 | read book
```


## TC7 Saved tasks are loaded at startup

**Aim:** Check that tasks in `data/duke.txt` are loaded when Kong starts.

### Initial Saved File
```text
T | 1 | read book
D | 0 | return book | 2019-10-15
E | 0 | project meeting | 2019-10-15 | 2019-10-16
```

### Input
```text
list
bye
```

### Expected Output
```text
Here are the tasks currently recorded in your archives:
1. [D][ ] return book (by: Oct 15 2019)
2. [E][ ] project meeting (from: Oct 15 2019 to: Oct 16 2019)
3. [T][X] read book
Cheerio! Until our next scholarly consultation.
```


## TC8 Missing data folder starts empty

**Aim:** Check that Kong starts with an empty list when `data/duke.txt` and the `data` folder do not exist.

### Input
```text
list
bye
```

### Expected Output
```text
Your archive is pristine. There are currently no tasks in your list.
Cheerio! Until our next scholarly consultation.
```


## TC9 Invalid saved lines are skipped

**Aim:** Check that malformed saved lines are ignored while valid saved tasks are still loaded.

### Initial Saved File
```text

T | 1 | read book
X | 0 | mystery task
D | maybe | return book | 2019-10-15
D | 0 |
E | 0 | project meeting | 2019-10-15 | 2019-10-16
E | 0 | missing end | 2019-10-15
T | 0 | too | many
```

### Input
```text
list
bye
```

### Expected Output
```text
Here are the tasks currently recorded in your archives:
1. [E][ ] project meeting (from: Oct 15 2019 to: Oct 16 2019)
2. [T][X] read book
Cheerio! Until our next scholarly consultation.
```


## TC10 Invalid deadline date

**Aim:** Check that deadline dates must use `yyyy-MM-dd`.

### Input
```text
deadline return book /by tomorrow
bye
```

### Expected Output
```text
Invalid date. Please use the format yyyy-MM-dd, for example 2019-10-15.
Cheerio! Until our next scholarly consultation.
```

## TC11 Invalid event date

**Aim:** Check that event dates must use `yyyy-MM-dd`.

### Input
```text
event project meeting /from 2019-10-15 /to tomorrow
bye
```

### Expected Output
```text
Invalid date. Please use the format yyyy-MM-dd, for example 2019-10-15.
Cheerio! Until our next scholarly consultation.
```


## TC12 Tasks on date

**Aim:** Check that `on <date>` lists deadlines due on that date and events occurring on that date.

### Input
```text
todo read book
deadline return book /by 2019-10-15
event conference /from 2019-10-14 /to 2019-10-16
event holiday /from 2019-10-20 /to 2019-10-21
on 2019-10-15
bye
```

### Expected Output
```text
Consulting the ledger. Here are the deadlines and events on this date:
1. [E][ ] conference (from: Oct 14 2019 to: Oct 16 2019)
2. [D][ ] return book (by: Oct 15 2019)
Cheerio! Until our next scholarly consultation.
```

## TC13 No tasks on date

**Aim:** Check that `on <date>` gives a clear message when no deadlines or events occur on that date.

### Input
```text
deadline return book /by 2019-10-15
on 2019-10-16
bye
```

### Expected Output
```text
The ledger is clear. There are no deadlines or events on this date.
Cheerio! Until our next scholarly consultation.
```

## TC14 Invalid on date

**Aim:** Check that `on <date>` requires the `yyyy-MM-dd` date format.

### Input
```text
on tomorrow
bye
```

### Expected Output
```text
Invalid date. Please use the format yyyy-MM-dd, for example 2019-10-15.
Cheerio! Until our next scholarly consultation.
```

## TC15 Empty on date

**Aim:** Check that `on` without a date is rejected.

### Input
```text
on
bye
```

### Expected Output
```text
Invalid command. An on command needs to be in the following format: on <date>
Cheerio! Until our next scholarly consultation.
```

## TC16 Divider after exit

**Aim:** Check that Kong prints the divider line after the exit command finishes.

### Input
```text
bye
```

### Expected Output
```text
Cheerio! Until our next scholarly consultation.
________________________________________
```

## TC17 Find matching tasks

**Aim:** Check that `find <keyword>` lists matching task descriptions in list order, regardless of case.

### Input
```text
todo read book
deadline return book /by 2019-10-15
event project meeting /from 2019-10-15 /to 2019-10-16
find BOOK
bye
```

### Expected Output
```text
Eureka! Here are the matching tasks found in your archives:
1. [D][ ] return book (by: Oct 15 2019)
2. [T][ ] read book
Cheerio! Until our next scholarly consultation.
```

## TC18 No matching tasks

**Aim:** Check that `find <keyword>` gives a clear message when no task descriptions match.

### Input
```text
todo read book
find movie
bye
```

### Expected Output
```text
Search complete. There are no matching tasks in your list.
Cheerio! Until our next scholarly consultation.
```

## TC19 Empty find keyword

**Aim:** Check that `find` without a keyword is rejected.

### Input
```text
find
bye
```

### Expected Output
```text
Invalid command. A find command needs to be in the following format: find <keyword>
Cheerio! Until our next scholarly consultation.
```

## TC20 Insert tasks chronologically

**Aim:** Check that tasks are automatically inserted by date, with todos last, and saved in their displayed order.

### Input
```text
todo read book
deadline submit report /by 2019-10-20
event conference /from 2019-10-14 /to 2019-10-16
deadline return book /by 2019-10-14
list
bye
```

### Expected Output
```text
Here are the tasks currently recorded in your archives:
1. [E][ ] conference (from: Oct 14 2019 to: Oct 16 2019)
2. [D][ ] return book (by: Oct 14 2019)
3. [D][ ] submit report (by: Oct 20 2019)
4. [T][ ] read book
Cheerio! Until our next scholarly consultation.
```

### Expected Saved File
```text
E | 0 | conference | 2019-10-14 | 2019-10-16
D | 0 | return book | 2019-10-14
D | 0 | submit report | 2019-10-20
T | 0 | read book
```

## TC21 Invalid delete number

**Aim:** Check that `delete` with a non-numeric task number identifies the correct command in its error message.

### Input
```text
delete abc
bye
```

### Expected Output
```text
Invalid command. A delete command needs to be followed by a number.
Cheerio! Until our next scholarly consultation.
```

## TC22 Duplicate task rejection

**Aim:** Check that adding a task with the same details as an existing task is rejected.

### Input
```text
todo read book
todo read book
bye
```

### Expected Output
```text
Splendid addition! I have inscribed this task into your archives:
[T][ ] read book
This task already exists in your list.
Cheerio! Until our next scholarly consultation.
```

## TC23 Non-existent calendar date

**Aim:** Check that a date that does not exist on the calendar is rejected with a specific explanation.

### Input
```text
deadline return book /by 2019-02-30
bye
```

### Expected Output
```text
Invalid date. The date '2019-02-30' does not exist on the calendar.
Cheerio! Until our next scholarly consultation.
```

## TC24 Event start date after end date

**Aim:** Check that an event whose start date is later than its end date is rejected.

### Input
```text
event party /from 2019-10-20 /to 2019-10-15
bye
```

### Expected Output
```text
Invalid command. The event start date cannot be after the end date.
Cheerio! Until our next scholarly consultation.
```

## TC25 Duplicate parameter in command

**Aim:** Check that specifying a parameter multiple times is rejected.

### Input
```text
deadline return book /by 2019-10-15 /by 2019-10-16
bye
```

### Expected Output
```text
Invalid command. The /by parameter cannot be specified multiple times.
Cheerio! Until our next scholarly consultation.
```

## TC26 Forbidden pipe character in description

**Aim:** Check that task descriptions containing the storage delimiter '|' are rejected.

### Input
```text
todo buy milk | bread
bye
```

### Expected Output
```text
Task description cannot contain the '|' character.
Cheerio! Until our next scholarly consultation.
```

## TC27 Extraneous arguments in no-argument commands

**Aim:** Check that commands taking no arguments reject extraneous input.

### Input
```text
list all
bye
```

### Expected Output
```text
Invalid command. The list command does not take any arguments.
Cheerio! Until our next scholarly consultation.
```

## TC28 Blank command input

**Aim:** Check that an empty or whitespace-only command prompts the user to enter a command.

### Input
```text
   
bye
```

### Expected Output
```text
Please enter a command.
Cheerio! Until our next scholarly consultation.
```

## TC29 Invalid unmark number

**Aim:** Check that `unmark` with a non-numeric task number identifies the unmark command in its error message.

### Input
```text
unmark abc
bye
```

### Expected Output
```text
Invalid command. A unmark command needs to be followed by a number.
Cheerio! Until our next scholarly consultation.
```

## TC30 Zero task number

**Aim:** Check that an invalid task number such as 0 is rejected with a message showing current task count.

### Input
```text
mark 0
bye
```

### Expected Output
```text
This task number is invalid. You currently have 0 tasks in your list.
Cheerio! Until our next scholarly consultation.
```


