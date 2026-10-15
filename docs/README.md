# Bern User Guide

## Product Introduction
![A screenshot of Bern's user interface.](/Ui.png)
> *"In the process of resolving the Navier-Stokes problem, the agents sent 2.7 million messages and used approximately
> 130 billion output tokens."*
> 
-OpenAI, [*On the Navier-Stokes Millennium Prize Problem*](https://openai.com/index/navier-stokes-solution/)

---
Ever wanted to tokenmaxx but don't want to pay for a premium model? Try Bern Tokens, our newest Artifical
nonIntelligence (AnI) model!

Use Bern Tokens to:
* Keep track of your to-do list, upcoming deadlines and events
* View them in a user-friendly format

Bern Token's primary mode of input is **typed user commands**, so users with fast typing speeds will be able to
efficiently manage their tasks very quickly.

## Bern Command Reference
[Adding Tasks](#adding-tasks)
* [`todo <task-name>`](#to-dos)
* [`deadline <task-name> /by <deadline-date-time>`](#deadlines)
* [`event <task-name> /from <event-start-date-time> /to <event-end-date-time>`](#events)

[Modifying Tasks](#modifying-tasks)
* [`mark <task-number>`](#marking-tasks)
* [`unmark <task-number>`](#unmarking-tasks)
* [`delete <task-number>`](#deleting-tasks)

[Viewing Tasks](#viewing-tasks)
* [`list`](#list-all-tasks)
* [`find <task-keyword>`](#find-tasks-by-keyword)
* [`schedule <date>`](#view-schedule-for-a-date)

[Exiting the Program](#exiting-program)
* [`bye`](#say-goodbye)

## Adding Tasks

### To-Dos
Todos are tasks with no deadline. 
#### Syntax
```
todo <task-name>
```
#### Parameter Values

| Parameter     | Description                         |
|---------------|-------------------------------------|
| `<task-name>` | The display name of the to-do item. |

### Deadlines
Deadlines are tasks that need to be done by a specific date, sometimes at a specific time.
#### Syntax
```
deadline <task-name> /by <deadline-date-time>
```
#### Parameter Values

| Parameter              | Description                                                                                                                |
|------------------------|----------------------------------------------------------------------------------------------------------------------------|
| `<task-name>`          | The display name of the to-do item.                                                                                        |
| `<deadline-date-time>` | The date (and optionally, time) the deadline item is to be done by.<br>To be given in [DateTime format](#datetime-format). |

### Events
Events are tasks that start and end at specific dates, sometimes at specific times.
#### Syntax
```
event <task-name> /from <event-start-date-time> /to <event-end-date-time>
```
#### Parameter Values

| Parameter                 | Description                                                                                                   |
|---------------------------|---------------------------------------------------------------------------------------------------------------|
| `<task-name>`             | The display name of the to-do item.                                                                           |
| `<event-start-date-time>` | The date (and optionally, time) the event item starts.<br>To be given in [DateTime format](#datetime-format). |
| `<event-end-date-time>`   | The date (and optionally, time) the event item ends.<br>To be given in [DateTime format](#datetime-format).   |

## Modifying Tasks
### Marking Tasks
Tasks can be marked as done. This causes the task to be disabled (greyed-out) when displayed.
#### Syntax
```
mark <task-number>
```
#### Parameter Values

| Parameter       | Description                               |
|-----------------|-------------------------------------------|
| `<task-number>` | The task number of the task to be marked. |

### Unmarking Tasks
Tasks can be unmarked, indicating they are not yet done. This re-enables the task when displayed.
#### Syntax
```
unmark <task-number>
```
#### Parameter Values

| Parameter       | Description                                 |
|-----------------|---------------------------------------------|
| `<task-number>` | The task number of the task to be unmarked. |

### Deleting Tasks
Tasks can be deleted. Once deleted, the task cannot be retrieved again.
#### Syntax
```
delete <task-number>
```
#### Parameter Values

| Parameter       | Description                                |
|-----------------|--------------------------------------------|
| `<task-number>` | The task number of the task to be deleted. |

## Viewing Tasks
### List All Tasks
Displays all tasks by date, then type. The display order is:
1. To-dos
2. Tasks, by date
   1. Deadlines due on this date
   2. Event Schedule for this date

#### Syntax
```
list
```
### Find Tasks by Keyword
Find all tasks containing a specific keyword. Only full keyword matches will be displayed, so a task with
display name "task 1" will be shown by `find task` but not `find ask`. Only one keyword can be specified per `find`
command.
#### Syntax
```
find <task-keyword>
```
#### Parameter Values

| Parameter        | Description                              |
|------------------|------------------------------------------|
| `<task-keyword>` | The search keyword used to filter tasks. |

### View Schedule for a Date
View the schedule for a specified date.
#### Syntax
```
schedule <date>
```
#### Parameter Values

| Parameter | Description                                                                                                             |
|-----------|-------------------------------------------------------------------------------------------------------------------------|
| `<date>`  | The date of the schedule to be displayed. To be given in [DateTime format](#datetime-format), excluding the time field. |

## Exiting Program
### Say Goodbye
Closes the Bern Tokens program.
#### Syntax
```
bye
```

## DateTime Format
When providing a DateTime as an argument:
* If only a date is given, the task will not be given a time.
* If only a time is given, the task will be given the current date.
* If both a date and time are given, the date may be written before or after the time.

### Examples
```
17/09/2026
17 Sept 2026 3pm
4.00pm
16:00 2026 Sept 17
```

The following formats are accepted for the date and time:
### Date Formats
#### Numeric Formats
Numeric dates are only accepted in little-endian (`17/09/2026`) and big-endian (`2026/09/17`) formats.
Accepted separators include `/`, `.` and `-`, but only one can be used. Years are assumed to be given in full
```
Accepted
17/09/2026 (/ seperator, little-endian)
17.09.2026 (. separator, little-endian)
2026-09-17 (- separator, big-endian)

Not Accepted
09/17/2026 (middle-endian format is not accepted)
17.09.26 (will register as 17 September 0026)
```
#### Mixed Formats
Mixed format dates are accepted in little-/middle-/big-endian formats. Months may be abbreviated. Years must be given
in full.
```
Accepted
17 September 2026 (fully spelled, little-endian)
17 Sept 2026 (abbreviated month, little-endian)
2026 Sept 17 (big-endian)
Sept 17 2026 (middle-endian)

Not Accepted
17 Sept 26 (will register as 17 September 0026)
```

### Time Formats
Time formats accept both 12-hour and 24-hour formats. When specifying minutes, either `:` or `.` is used as a separator.
```
Accepted
5pm (12 hr, hour only)
5:29pm (12hr, : separator)
5.29pm (12hr, . separator)
17:29 (24hr, : separator)
17.29 (24hr, . separator)

Not Accepted
1729 (no separator given)
```