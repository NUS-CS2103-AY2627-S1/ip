# Duchess UI Test Plan

Run the tests from the repository root. The runner compiles the Java sources
into a temporary directory before starting each test case in a fresh process.

Run command: `java -cp {classes} duchess.Duchess`

## Manual GUI check: Readability and safe input

Aim: Verify the graphical behavior reported in forum issue 286.

Follow `test/manual-test-plan.md`, including the blank-input and wrapping cases.
Confirm the navy-and-gold precinct header, 48-pixel shield badges, squad
greeting, and shortcuts for all eleven commands. Text should grow modestly as the
window widens, with conversation width capped for comfortable reading.

## Test case 1: Create and mark multiple task types

Aim: Verify that `todo`, `deadline`, and `event` commands create different
subtypes stored together, and that `mark N` works polymorphically.

Inputs:
```text
todo read book
deadline return book /by 2019-12-02
event buy bread /at Saturday
mark 2
list
bye
```

Expected output:
```text
____________________________________________________________
+------------------------+
|        Duchess         |
+------------------------+
Hello! I'm Duchess.
What can I do for you?
____________________________________________________________
____________________________________________________________
added: [T][ ] read book
____________________________________________________________
____________________________________________________________
added: [D][ ] return book (by: Dec 02 2019)
____________________________________________________________
____________________________________________________________
added: [E][ ] buy bread (at: Saturday)
____________________________________________________________
____________________________________________________________
Nice! I've marked this task as done:
  [D][X] return book (by: Dec 02 2019)
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] read book
2.[D][X] return book (by: Dec 02 2019)
3.[E][ ] buy bread (at: Saturday)
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test case 2: Delete a task and preserve list ordering

Aim: Verify that `delete N` removes the selected polymorphic task, reports the
new count, and reindexes the remaining tasks correctly.

Inputs:
```text
todo read book
deadline return book /by 2019-06-06
event project meeting /at Aug 6th 2pm to 4pm
todo join sports club
list
delete 3
list
mark 3
list
bye
```

Expected output:
```text
____________________________________________________________
+------------------------+
|        Duchess         |
+------------------------+
Hello! I'm Duchess.
What can I do for you?
____________________________________________________________
____________________________________________________________
added: [T][ ] read book
____________________________________________________________
____________________________________________________________
added: [D][ ] return book (by: Jun 06 2019)
____________________________________________________________
____________________________________________________________
added: [E][ ] project meeting (at: Aug 6th 2pm to 4pm)
____________________________________________________________
____________________________________________________________
added: [T][ ] join sports club
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] read book
2.[D][ ] return book (by: Jun 06 2019)
3.[E][ ] project meeting (at: Aug 6th 2pm to 4pm)
4.[T][ ] join sports club
____________________________________________________________
____________________________________________________________
Noted. I've removed this task:
  [E][ ] project meeting (at: Aug 6th 2pm to 4pm)
Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] read book
2.[D][ ] return book (by: Jun 06 2019)
3.[T][ ] join sports club
____________________________________________________________
____________________________________________________________
Nice! I've marked this task as done:
  [T][X] join sports club
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] read book
2.[D][ ] return book (by: Jun 06 2019)
3.[T][X] join sports club
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test case 3: Reject invalid delete commands without changing state

Aim: Verify that invalid delete indexes and missing indexes do not remove the
existing task and that the session continues normally.

Inputs:
```text
todo read book
delete 0
delete 2
list
delete
list
bye
```

Expected output:
```text
____________________________________________________________
+------------------------+
|        Duchess         |
+------------------------+
Hello! I'm Duchess.
What can I do for you?
____________________________________________________________
____________________________________________________________
added: [T][ ] read book
____________________________________________________________
____________________________________________________________
OOPS!!! Please provide a valid task number between 1 and 1.
____________________________________________________________
____________________________________________________________
OOPS!!! Please provide a valid task number between 1 and 1.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] read book
____________________________________________________________
____________________________________________________________
OOPS!!! Please use 'delete <task number>', for example: delete 1.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] read book
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test case 4: Preserve state after invalid commands

Aim: Verify that invalid commands do not add or modify tasks, and that valid
commands still work afterward.

Inputs:
```text
todo read book
todo
blah
deadline report
mark 2
list
event meeting /at Monday
list
bye
```

Expected output:
```text
____________________________________________________________
+------------------------+
|        Duchess         |
+------------------------+
Hello! I'm Duchess.
What can I do for you?
____________________________________________________________
____________________________________________________________
added: [T][ ] read book
____________________________________________________________
____________________________________________________________
OOPS!!! The description of a todo cannot be empty.
____________________________________________________________
____________________________________________________________
OOPS!!! I'm sorry, but I don't know what that means :-(
Try todo, deadline, event, list, find, mark, unmark, delete, or bye.
____________________________________________________________
____________________________________________________________
OOPS!!! A deadline must include a non-empty /by value. Example: deadline task description /by time.
____________________________________________________________
____________________________________________________________
OOPS!!! Please provide a valid task number between 1 and 1.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] read book
____________________________________________________________
____________________________________________________________
added: [E][ ] meeting (at: Monday)
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] read book
2.[E][ ] meeting (at: Monday)
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test case 5: Accept case-insensitive typed commands

Aim: Verify that task types and control commands work regardless of letter
case and still produce the correct polymorphic state.

Inputs:
```text
TODO   read book
DEADLINE report /BY 2019-06-07
EVENT meeting /AT Monday
MARK 2
LIST
BYE
```

Expected output:
```text
____________________________________________________________
+------------------------+
|        Duchess         |
+------------------------+
Hello! I'm Duchess.
What can I do for you?
____________________________________________________________
____________________________________________________________
added: [T][ ] read book
____________________________________________________________
____________________________________________________________
added: [D][ ] report (by: Jun 07 2019)
____________________________________________________________
____________________________________________________________
added: [E][ ] meeting (at: Monday)
____________________________________________________________
____________________________________________________________
Nice! I've marked this task as done:
  [D][X] report (by: Jun 07 2019)
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] read book
2.[D][X] report (by: Jun 07 2019)
3.[E][ ] meeting (at: Monday)
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test case 6: Reject malformed details and task numbers

Aim: Verify that malformed deadline/event commands and invalid mark commands
leave the task list empty and the session usable.

Inputs:
```text
deadline project
event meeting
mark 1
unmark 0
mark
unmark
list
bye
```

Expected output:
```text
____________________________________________________________
+------------------------+
|        Duchess         |
+------------------------+
Hello! I'm Duchess.
What can I do for you?
____________________________________________________________
____________________________________________________________
OOPS!!! A deadline must include a non-empty /by value. Example: deadline task description /by time.
____________________________________________________________
____________________________________________________________
OOPS!!! An event must include a non-empty /at value. Example: event task description /at time.
____________________________________________________________
____________________________________________________________
OOPS!!! Your task list is empty, so there is no task to update.
____________________________________________________________
____________________________________________________________
OOPS!!! Your task list is empty, so there is no task to update.
____________________________________________________________
____________________________________________________________
OOPS!!! Please use 'mark <task number>', for example: mark 1.
____________________________________________________________
____________________________________________________________
OOPS!!! Please use 'unmark <task number>', for example: unmark 1.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test case 7: Mark and unmark a task

Aim: Verify that `unmark N` changes a completed task back to unfinished.

Inputs:
```text
todo read book
mark 1
unmark 1
list
bye
```

Expected output:
```text
____________________________________________________________
+------------------------+
|        Duchess         |
+------------------------+
Hello! I'm Duchess.
What can I do for you?
____________________________________________________________
____________________________________________________________
added: [T][ ] read book
____________________________________________________________
____________________________________________________________
Nice! I've marked this task as done:
  [T][X] read book
____________________________________________________________
____________________________________________________________
Okay, I've marked this task as not done yet:
  [T][ ] read book
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] read book
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test case 8: Recover from blank input and whitespace-only tasks

Aim: Verify that blank input and whitespace-only descriptions are rejected,
then a valid task can still be added and listed.

Inputs:
```text

todo   
todo write code
list
bye
```

Expected output:
```text
____________________________________________________________
+------------------------+
|        Duchess         |
+------------------------+
Hello! I'm Duchess.
What can I do for you?
____________________________________________________________
____________________________________________________________
OOPS!!! A command cannot be empty. Try todo, deadline, event, list, find, mark, unmark, delete, or bye.
____________________________________________________________
____________________________________________________________
OOPS!!! The description of a todo cannot be empty.
____________________________________________________________
____________________________________________________________
added: [T][ ] write code
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] write code
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test case 9: Delete the first, last, and only task

Aim: Verify that deleting tasks at different positions reindexes the remaining
tasks and that deleting the final task leaves an empty list.

Inputs:
```text
todo first task
deadline middle task /by 2019-06-07
event last task /at Monday
mark 2
delete 1
delete 2
list
delete 1
list
bye
```

Expected output:
```text
____________________________________________________________
+------------------------+
|        Duchess         |
+------------------------+
Hello! I'm Duchess.
What can I do for you?
____________________________________________________________
____________________________________________________________
added: [T][ ] first task
____________________________________________________________
____________________________________________________________
added: [D][ ] middle task (by: Jun 07 2019)
____________________________________________________________
____________________________________________________________
added: [E][ ] last task (at: Monday)
____________________________________________________________
____________________________________________________________
Nice! I've marked this task as done:
  [D][X] middle task (by: Jun 07 2019)
____________________________________________________________
____________________________________________________________
Noted. I've removed this task:
  [T][ ] first task
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Noted. I've removed this task:
  [E][ ] last task (at: Monday)
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[D][X] middle task (by: Jun 07 2019)
____________________________________________________________
____________________________________________________________
Noted. I've removed this task:
  [D][X] middle task (by: Jun 07 2019)
Now you have 0 tasks in the list.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test case 10: Reject malformed delete indexes

Aim: Verify that alphabetic, decimal, and negative delete indexes are rejected,
while a whitespace-padded valid index still works.

Inputs:
```text
todo read book
delete abc
delete 1.5
delete -1
delete   1
list
bye
```

Expected output:
```text
____________________________________________________________
+------------------------+
|        Duchess         |
+------------------------+
Hello! I'm Duchess.
What can I do for you?
____________________________________________________________
____________________________________________________________
added: [T][ ] read book
____________________________________________________________
____________________________________________________________
OOPS!!! Please provide a valid task number between 1 and 1.
____________________________________________________________
____________________________________________________________
OOPS!!! Please provide a valid task number between 1 and 1.
____________________________________________________________
____________________________________________________________
OOPS!!! Please provide a valid task number between 1 and 1.
____________________________________________________________
____________________________________________________________
Noted. I've removed this task:
  [T][ ] read book
Now you have 0 tasks in the list.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test case 11: Reject empty deadline and event descriptions

Aim: Verify that deadline and event commands with empty descriptions do not
change the list and that a valid task can still be added afterward.

Inputs:
```text
deadline /by 2019-06-07
event /at Monday
list
todo valid task
list
bye
```

Expected output:
```text
____________________________________________________________
+------------------------+
|        Duchess         |
+------------------------+
Hello! I'm Duchess.
What can I do for you?
____________________________________________________________
____________________________________________________________
OOPS!!! The description of a deadline cannot be empty.
____________________________________________________________
____________________________________________________________
OOPS!!! The description of an event cannot be empty.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
____________________________________________________________
____________________________________________________________
added: [T][ ] valid task
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] valid task
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test case 12: Reject duplicate tasks

Aim: Verify that an equivalent task is rejected without changing the existing
task or preventing later commands.

Inputs:
```text
todo read book
todo   READ BOOK
mark 1
list
bye
```

Expected output:
```text
____________________________________________________________
+------------------------+
|        Duchess         |
+------------------------+
Hello! I'm Duchess.
What can I do for you?
____________________________________________________________
____________________________________________________________
added: [T][ ] read book
____________________________________________________________
____________________________________________________________
OOPS!!! That task already exists in your list.
____________________________________________________________
____________________________________________________________
Nice! I've marked this task as done:
  [T][X] read book
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][X] read book
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test case 13: Load saved tasks and skip corrupted records

Aim: Verify that Duchess loads todo, deadline, and event records at startup,
restores completion state, warns about invalid records, and blocks destructive saves.

Initial data file:
```text
T|1|cmVhZCBib29r
D|0|cmV0dXJuIGJvb2s=|MjAxOS0xMi0wMg==
E|0|YnV5IGJyZWFk|U2F0dXJkYXk=
not-a-valid-record
```

Inputs:
```text
list
todo temporary task
bye
```

Expected output:
```text
____________________________________________________________
+------------------------+
|        Duchess         |
+------------------------+
Hello! I'm Duchess.
What can I do for you?
____________________________________________________________
OOPS!!! I couldn't fully load your saved tasks from data/duchess.txt.
Saving is disabled to protect that file. Back it up, repair the file or its permissions, then restart Duchess.
Changes made in this session stay in memory and will be lost on exit.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][X] read book
2.[D][ ] return book (by: Dec 02 2019)
3.[E][ ] buy bread (at: Saturday)
____________________________________________________________
____________________________________________________________
OOPS!!! I couldn't save your task list to disk.
added: [T][ ] temporary task
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test case 14: Parse and display typed deadline dates

Aim: Verify that an ISO deadline date is stored as a LocalDate and displayed
in a different, readable format.

Inputs:
```text
deadline return book /by 2019-12-02
deadline submit report /by 2019-10-15
list
bye
```

Expected output:
```text
____________________________________________________________
+------------------------+
|        Duchess         |
+------------------------+
Hello! I'm Duchess.
What can I do for you?
____________________________________________________________
____________________________________________________________
added: [D][ ] return book (by: Dec 02 2019)
____________________________________________________________
____________________________________________________________
added: [D][ ] submit report (by: Oct 15 2019)
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[D][ ] return book (by: Dec 02 2019)
2.[D][ ] submit report (by: Oct 15 2019)
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test case 15: Find tasks by a case-insensitive keyword

Aim: Verify that `find` displays matching task types in list order, accepts
different keyword casing, handles no matches, and rejects an empty keyword.

Inputs:
```text
todo read book
deadline return book /by 2019-12-02
event project meeting /at Monday
todo write code
find book
find MEETING
find notebook
find
bye
```

Expected output:
```text
____________________________________________________________
+------------------------+
|        Duchess         |
+------------------------+
Hello! I'm Duchess.
What can I do for you?
____________________________________________________________
____________________________________________________________
added: [T][ ] read book
____________________________________________________________
____________________________________________________________
added: [D][ ] return book (by: Dec 02 2019)
____________________________________________________________
____________________________________________________________
added: [E][ ] project meeting (at: Monday)
____________________________________________________________
____________________________________________________________
added: [T][ ] write code
____________________________________________________________
____________________________________________________________
Here are the matching tasks in your list:
1.[T][ ] read book
2.[D][ ] return book (by: Dec 02 2019)
____________________________________________________________
____________________________________________________________
Here are the matching tasks in your list:
3.[E][ ] project meeting (at: Monday)
____________________________________________________________
____________________________________________________________
No matching tasks found. Try a different keyword.
____________________________________________________________
____________________________________________________________
OOPS!!! Please use 'find <keyword>', for example: find book.
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test case 16: Display task statistics and reject arguments

Aim: Verify that `stats` reports task totals, recent completions, and the
completion rate, and that arguments are rejected.

Inputs:
```text
todo recent task
todo pending task
mark 1
stats
stats today
bye
```

Expected output:
```text
____________________________________________________________
+------------------------+
|        Duchess         |
+------------------------+
Hello! I'm Duchess.
What can I do for you?
____________________________________________________________
____________________________________________________________
added: [T][ ] recent task
____________________________________________________________
____________________________________________________________
added: [T][ ] pending task
____________________________________________________________
____________________________________________________________
Nice! I've marked this task as done:
  [T][X] recent task
____________________________________________________________
____________________________________________________________
Task statistics:
Total tasks: 2
Completed tasks: 1
Incomplete tasks: 1
Completed in the past 7 days: 1
Completion rate: 50%
____________________________________________________________
____________________________________________________________
OOPS!!! Please use 'stats' without arguments.
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test case 17: Load legacy completed task statistics

Aim: Verify that a completed task from the legacy storage format remains
loadable and is counted as completed but not as completed in the past seven
days.

Initial data file:
```text
T|1|Y3MyMTAw
```

Inputs:
```text
stats
bye
```

Expected output:
```text
____________________________________________________________
+------------------------+
|        Duchess         |
+------------------------+
Hello! I'm Duchess.
What can I do for you?
____________________________________________________________
____________________________________________________________
Task statistics:
Total tasks: 1
Completed tasks: 1
Incomplete tasks: 0
Completed in the past 7 days: 0
Completion rate: 100%
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test case 18: Use Duchess's squad-room voice

Aim: Verify that the help response introduces Duchess's consistent squad-room
personality before displaying the command guide.

Inputs:
```text
help
bye
```

Expected output:
```text
____________________________________________________________
+------------------------+
|        Duchess         |
+------------------------+
Hello! I'm Duchess.
What can I do for you?
____________________________________________________________
____________________________________________________________
The squad desk is open.
I'll keep the banter light and your case file organized.

Squad playbook:
• todo <description> — Add a todo task.
  case <description> — Squad alias for todo.
• deadline <description> /by <date> — Add a deadline task.
  timer <description> /by <date> — Squad alias for deadline.
• event <description> /from <date> /to <date> — Add an event.
  briefing <description> /from <date> /to <date> — Squad alias for event.
  Dates use yyyy-MM-dd. Legacy /at <time> is also supported.
• list / rollcall — Show all tasks.
• stats / report — Show task statistics.
• find <keyword> / intel <keyword> — Find tasks by keyword.
• mark <task number> / close <task number> — Mark a task done.
• unmark <task number> / reopen <task number> — Reopen a task.
• delete <task number> / archive <task number> — Delete a task.
• help / brief — Show this playbook.
• bye / signoff — Exit Duchess.

Commands are not case-sensitive.

____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test case 19: Accept squad-room aliases

Aim: Verify that squad-room aliases execute the same behavior as the original
task commands.

Inputs:
```text
case read book
rollcall
signoff
```

Expected output:
```text
____________________________________________________________
+------------------------+
|        Duchess         |
+------------------------+
Hello! I'm Duchess.
What can I do for you?
____________________________________________________________
____________________________________________________________
added: [T][ ] read book
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] read book
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test case 20: Event date ranges and rejected additions

Aim: Verify range display and completion, and ensure invalid event ranges do not alter the list.

Inputs:
```text
event orientation /from 2026-09-15 /to 2026-09-17
event invalid /from 2026-02-30 /to 2026-03-01
event reversed /from 2026-09-17 /to 2026-09-15
event incomplete /from 2026-09-15
list
mark 1
list
bye
```

Expected output:
```text
____________________________________________________________
+------------------------+
|        Duchess         |
+------------------------+
Hello! I'm Duchess.
What can I do for you?
____________________________________________________________
____________________________________________________________
added: [E][ ] orientation (from: Sep 15 2026 to: Sep 17 2026)
____________________________________________________________
____________________________________________________________
OOPS!!! The event dates are invalid. Use yyyy-MM-dd.
____________________________________________________________
____________________________________________________________
OOPS!!! The event end date must be later than its start date.
____________________________________________________________
____________________________________________________________
OOPS!!! An event must contain exactly one /from and one /to parameter.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[E][ ] orientation (from: Sep 15 2026 to: Sep 17 2026)
____________________________________________________________
____________________________________________________________
Nice! I've marked this task as done:
  [E][X] orientation (from: Sep 15 2026 to: Sep 17 2026)
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[E][X] orientation (from: Sep 15 2026 to: Sep 17 2026)
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test case 21: Normalize whitespace and reject repeated parameters

Aim: Verify that harmless leading and repeated whitespace is accepted while
duplicate parameters and same-day event ranges are rejected without changing
the task list.

Inputs:
```text
   todo     read book
deadline report /by 2026-09-20 /by 2026-09-21
event meeting /from 2026-09-20 /to 2026-09-20
   list
bye now
bye
```

Expected output:
```text
____________________________________________________________
+------------------------+
|        Duchess         |
+------------------------+
Hello! I'm Duchess.
What can I do for you?
____________________________________________________________
____________________________________________________________
added: [T][ ] read book
____________________________________________________________
____________________________________________________________
OOPS!!! A deadline must contain exactly one /by parameter.
____________________________________________________________
____________________________________________________________
OOPS!!! The event end date must be later than its start date.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] read book
____________________________________________________________
____________________________________________________________
OOPS!!! Please use 'bye' without arguments.
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

## Test case 22: Use search result numbers to update tasks

Aim: Verify that search retains full-list numbers so mark updates the matching task.

Inputs:
```text
todo first task
todo target task
find target
mark 2
list
bye
```

Expected output:
```text
____________________________________________________________
+------------------------+
|        Duchess         |
+------------------------+
Hello! I'm Duchess.
What can I do for you?
____________________________________________________________
____________________________________________________________
added: [T][ ] first task
____________________________________________________________
____________________________________________________________
added: [T][ ] target task
____________________________________________________________
____________________________________________________________
Here are the matching tasks in your list:
2.[T][ ] target task
____________________________________________________________
____________________________________________________________
Nice! I've marked this task as done:
  [T][X] target task
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] first task
2.[T][X] target task
____________________________________________________________
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```
