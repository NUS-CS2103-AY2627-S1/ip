# UI Test Plan

This plan covers the command-line interaction documented for Turtley. Each test case starts a fresh program session. The persistence cases run in order: test case 1 creates the save file, and test case 2 verifies loading it before clearing the list. The test runner uses an isolated temporary working directory for each full run. The `inputs` and `expected_outputs` arrays in the JSON block are aligned by position: each expected output must appear after the corresponding input's earlier output.

## Test case 1: To-do, deadline, and event workflow

Aim: Verify that ordinary to-dos, deadlines, and events are accepted, displayed with the correct type icons and details, retained in insertion order, counted correctly, and saved after successful list mutations.

Inputs:

```text
todo read book
deadline return book /by 2026-06-06
event project meeting /from 06-08-2026 14:00 /to 06-08-2026 16:00
todo join sports club
mark 1
mark 4
todo borrow book
list
find book
deadline return book /by 07-06-2026
event project meeting /from 10-08-2026 1400 /to 10-08-2026 1600
bye
```

Expected output: The program prints the corresponding confirmation for each command, including the following important results:

```text
[T][ ] borrow book
[D][ ] return book (by: 2026-06-06)
[E][ ] project meeting (from: 2026-08-06 14:00 to: 2026-08-06 16:00)
1.[T][X] read book
2.[D][ ] return book (by: 2026-06-06)
3.[E][ ] project meeting (from: 2026-08-06 14:00 to: 2026-08-06 16:00)
4.[T][X] join sports club
5.[T][ ] borrow book
[D][ ] return book (by: 2026-06-07)
[E][ ] project meeting (from: 2026-08-10 14:00 to: 2026-08-10 16:00)
Here are the matching tasks in your list:
1.[T][X] read book
2.[D][ ] return book (by: 2026-06-06)
5.[T][ ] borrow book
Now you have 7 tasks in the list.
```

The executable expected-output list is kept below so the `test-ui` skill can run this case without guessing which output belongs to each command.

```json test-ui
{
  "test_cases": [
    {
      "name": "To-do, deadline, and event workflow",
      "aim": "Verify creation, formatting, listing, completion status, task counts, and automatic saving for all supported task types.",
      "inputs": [
        "todo read book",
        "deadline return book /by 2026-06-06",
        "event project meeting /from 06-08-2026 14:00 /to 06-08-2026 16:00",
        "todo join sports club",
        "mark 1",
        "mark 4",
        "todo borrow book",
        "list",
        "find book",
        "deadline return book /by 07-06-2026",
        "event project meeting /from 10-08-2026 1400 /to 10-08-2026 1600",
        "bye"
      ],
      "expected_outputs": [
        "Got it. I've added this task:\n  [T][ ] read book\nNow you have 1 tasks in the list.",
        "Got it. I've added this task:\n  [D][ ] return book (by: 2026-06-06)\nNow you have 2 tasks in the list.",
        "Got it. I've added this task:\n  [E][ ] project meeting (from: 2026-08-06 14:00 to: 2026-08-06 16:00)\nNow you have 3 tasks in the list.",
        "Got it. I've added this task:\n  [T][ ] join sports club\nNow you have 4 tasks in the list.",
        "Nice! I've marked this task as done:\n   [X] read book",
        "Nice! I've marked this task as done:\n   [X] join sports club",
        "Got it. I've added this task:\n  [T][ ] borrow book\nNow you have 5 tasks in the list.",
        "Here are the tasks in your list:\n 1.[T][X] read book\n 2.[D][ ] return book (by: 2026-06-06)\n 3.[E][ ] project meeting (from: 2026-08-06 14:00 to: 2026-08-06 16:00)\n 4.[T][X] join sports club\n 5.[T][ ] borrow book",
        "Here are the matching tasks in your list:\n 1.[T][X] read book\n 2.[D][ ] return book (by: 2026-06-06)\n 5.[T][ ] borrow book",
        "Got it. I've added this task:\n  [D][ ] return book (by: 2026-06-07)\nNow you have 6 tasks in the list.",
        "Got it. I've added this task:\n  [E][ ] project meeting (from: 2026-08-10 14:00 to: 2026-08-10 16:00)\nNow you have 7 tasks in the list.",
        "Bye. See you around!"
      ]
    },
    {
      "name": "Load saved tasks and clear list",
      "aim": "Verify that tasks saved by an earlier session are restored on startup, that missing to-do names are rejected, and that the list can be cleared for later cases.",
      "inputs": [
        "list",
        "todo",
        "delete 1",
        "delete 1",
        "delete 1",
        "delete 1",
        "delete 1",
        "delete 1",
        "delete 1",
        "list",
        "bye"
      ],
      "expected_outputs": [
        "Here are the tasks in your list:\n 1.[T][X] read book\n 2.[D][ ] return book (by: 2026-06-06)\n 3.[E][ ] project meeting (from: 2026-08-06 14:00 to: 2026-08-06 16:00)\n 4.[T][X] join sports club\n 5.[T][ ] borrow book\n 6.[D][ ] return book (by: 2026-06-07)\n 7.[E][ ] project meeting (from: 2026-08-10 14:00 to: 2026-08-10 16:00)",
        "Invalid format. Use: todo <description> [/tag #tag1 #tag2 ...] o/T\\>",
        "Noted. I've removed this task:\n   [T][X] read book\n Now you have 6 tasks in the list.",
        "Noted. I've removed this task:\n   [D][ ] return book (by: 2026-06-06)\n Now you have 5 tasks in the list.",
        "Noted. I've removed this task:\n   [E][ ] project meeting (from: 2026-08-06 14:00 to: 2026-08-06 16:00)\n Now you have 4 tasks in the list.",
        "Noted. I've removed this task:\n   [T][X] join sports club\n Now you have 3 tasks in the list.",
        "Noted. I've removed this task:\n   [T][ ] borrow book\n Now you have 2 tasks in the list.",
        "Noted. I've removed this task:\n   [D][ ] return book (by: 2026-06-07)\n Now you have 1 tasks in the list.",
        "Noted. I've removed this task:\n   [E][ ] project meeting (from: 2026-08-10 14:00 to: 2026-08-10 16:00)\n Now you have 0 tasks in the list.",
        "Task list empty. Good job! Here's a cookie. o/T\\>",
        "Bye. See you around!"
      ]
    },
    {
      "name": "Error messages",
      "aim": "Verify that invalid commands, task numbers, structured task formats, and invalid find inputs use the custom Turtley error message suffix.",
      "inputs": [
        "deadline /by tomorrow",
        "event /from 2pm /to 3pm",
        "mark nope",
        "unmark nope",
        "mark 1",
        "unmark 1",
        "",
        "unknown command",
        "find book",
        "find",
        "deadline malformed /by 2026-02-30",
        "event malformed /from 31-02-2026 /to 01-03-2026",
        "event compact time /from 31-12-2026 2300 /to 01-01-2027 0030",
        "delete 1",
        "bye"
      ],
      "expected_outputs": [
        "Invalid format. Use: deadline <description> /by <date> [/tag #tag1 #tag2 ...] o/T\\>",
        "Invalid format. Use: event <description> /from <start> /to <end> [/tag #tag1 #tag2 ...] o/T\\>",
        "Please provide a valid task number. o/T\\>",
        "Please provide a valid task number. o/T\\>",
        "Task number is not in your list. o/T\\>",
        "Task number is not in your list. o/T\\>",
        "Please input something. o/T\\>",
        "Please input something correct. o/T\\>",
        "Here are the matching tasks in your list:\nNone! o/T\\>",
        "Invalid format. Use: find <keyword> o/T\\>",
        "Invalid date/time format. Use yyyy-MM-dd, dd-MM-yyyy, yyyy/MM/dd, dd/MM/yyyy, yyyy-MM-dd HH:mm, dd-MM-yyyy HH:mm, yyyy/MM/dd HH:mm, dd/MM/yyyy HH:mm, yyyy-MM-dd HHmm, dd-MM-yyyy HHmm, yyyy/MM/dd HHmm, or dd/MM/yyyy HHmm. o/T\\>",
        "Invalid date/time format. Use yyyy-MM-dd, dd-MM-yyyy, yyyy/MM/dd, dd/MM/yyyy, yyyy-MM-dd HH:mm, dd-MM-yyyy HH:mm, yyyy/MM/dd HH:mm, dd/MM/yyyy HH:mm, yyyy-MM-dd HHmm, dd-MM-yyyy HHmm, yyyy/MM/dd HHmm, or dd/MM/yyyy HHmm. o/T\\>",
        "Got it. I've added this task:\n  [E][ ] compact time (from: 2026-12-31 23:00 to: 2027-01-01 00:30)\nNow you have 1 tasks in the list.",
        "Noted. I've removed this task:\n   [E][ ] compact time (from: 2026-12-31 23:00 to: 2027-01-01 00:30)\n Now you have 0 tasks in the list.",
        "Bye. See you around!"
      ]
    },
    {
      "name": "Delete task workflow",
      "aim": "Verify that a task can be deleted by its one-based number, that its details are shown, that the count decreases, that later tasks are renumbered, and that the updated list is saved.",
      "inputs": [
        "todo read book",
        "deadline return book /by 2026-06-06",
        "event project meeting /from 06-08-2026 14:00 /to 06-08-2026 16:00",
        "todo join sports club",
        "todo borrow book",
        "mark 1",
        "mark 2",
        "list",
        "delete 3",
        "list",
        "bye"
      ],
      "expected_outputs": [
        "Got it. I've added this task:\n  [T][ ] read book\nNow you have 1 tasks in the list.",
        "Got it. I've added this task:\n  [D][ ] return book (by: 2026-06-06)\nNow you have 2 tasks in the list.",
        "Got it. I've added this task:\n  [E][ ] project meeting (from: 2026-08-06 14:00 to: 2026-08-06 16:00)\nNow you have 3 tasks in the list.",
        "Got it. I've added this task:\n  [T][ ] join sports club\nNow you have 4 tasks in the list.",
        "Got it. I've added this task:\n  [T][ ] borrow book\nNow you have 5 tasks in the list.",
        "Nice! I've marked this task as done:\n   [X] read book",
        "Nice! I've marked this task as done:\n   [X] return book",
        "Here are the tasks in your list:\n 1.[T][X] read book\n 2.[D][X] return book (by: 2026-06-06)\n 3.[E][ ] project meeting (from: 2026-08-06 14:00 to: 2026-08-06 16:00)\n 4.[T][ ] join sports club\n 5.[T][ ] borrow book",
        "Noted. I've removed this task:\n   [E][ ] project meeting (from: 2026-08-06 14:00 to: 2026-08-06 16:00)\n Now you have 4 tasks in the list.",
        "Here are the tasks in your list:\n 1.[T][X] read book\n 2.[D][X] return book (by: 2026-06-06)\n 3.[T][ ] join sports club\n 4.[T][ ] borrow book",
        "Bye. See you around!"
      ]
    },
    {
      "name": "Timecheck workflow",
      "aim": "Verify that timecheck lists only deadlines and events on or before a supplied date/time, handles no matches, rejects invalid input, and does not alter the task list.",
      "inputs": [
        "event planning /from 06/08/2026 09:00 /to 06/08/2026 10:00",
        "timecheck 2026-06-30",
        "timecheck 2026-08-27 12:00",
        "timecheck 2025-01-01",
        "timecheck invalid",
        "delete 5",
        "delete 1",
        "delete 1",
        "delete 1",
        "delete 1",
        "bye"
      ],
      "expected_outputs": [
        "Got it. I've added this task:\n  [E][ ] planning (from: 2026-08-06 09:00 to: 2026-08-06 10:00)\nNow you have 5 tasks in the list.",
        "Here are the deadline and event tasks on or before 2026-06-30:\n 2.[D][X] return book (by: 2026-06-06)",
        "Here are the deadline and event tasks on or before 2026-08-27 12:00:\n 2.[D][X] return book (by: 2026-06-06)\n 5.[E][ ] planning (from: 2026-08-06 09:00 to: 2026-08-06 10:00)",
        "Here are the deadline and event tasks on or before 2025-01-01:\nNone! o/T\\>",
        "Invalid date/time format. Use yyyy-MM-dd, dd-MM-yyyy, yyyy/MM/dd, dd/MM/yyyy, yyyy-MM-dd HH:mm, dd-MM-yyyy HH:mm, yyyy/MM/dd HH:mm, dd/MM/yyyy HH:mm, yyyy-MM-dd HHmm, dd-MM-yyyy HHmm, yyyy/MM/dd HHmm, or dd/MM/yyyy HHmm. o/T\\>",
        "Noted. I've removed this task:\n   [E][ ] planning (from: 2026-08-06 09:00 to: 2026-08-06 10:00)\n Now you have 4 tasks in the list.",
        "Noted. I've removed this task:\n   [T][X] read book\n Now you have 3 tasks in the list.",
        "Noted. I've removed this task:\n   [D][X] return book (by: 2026-06-06)\n Now you have 2 tasks in the list.",
        "Noted. I've removed this task:\n   [T][ ] join sports club\n Now you have 1 tasks in the list.",
        "Noted. I've removed this task:\n   [T][ ] borrow book\n Now you have 0 tasks in the list.",
        "Bye. See you around!"
      ]
    },
    {
      "name": "Tagging workflow",
      "aim": "Verify tagged creation for all task types, tag and untag mutations, tag-aware find, tag filtering, validation, help, and persistence.",
      "inputs": [
        "todo go jogging /tag #health #fun",
        "deadline submit report /by 2026-12-31 /tag #work",
        "event team dinner /from 2026-09-20 19:00 /to 2026-09-20 21:00 /tag #social",
        "mark 1",
        "tag 1 #weekend #fun",
        "find FUN",
        "filter #HE",
        "untag 1 #health",
        "list",
        "todo invalid /tag #",
        "tag 1 fun",
        "help",
        "bye"
      ],
      "expected_outputs": [
        "Got it. I've added this task:\n  [T][ ] go jogging [#fun] [#health]\nNow you have 1 tasks in the list.",
        "Got it. I've added this task:\n  [D][ ] submit report [#work] (by: 2026-12-31)\nNow you have 2 tasks in the list.",
        "Got it. I've added this task:\n  [E][ ] team dinner [#social] (from: 2026-09-20 19:00 to: 2026-09-20 21:00)\nNow you have 3 tasks in the list.",
        "Nice! I've marked this task as done:\n   [X] go jogging [#fun] [#health]",
        "Got it. I've tagged this task:\n   [T][X] go jogging [#fun] [#health] [#weekend]",
        "Here are the matching tasks in your list:\n 1.[T][X] go jogging [#fun] [#health] [#weekend]",
        "Here are the tasks with tags matching #HE:\n 1.[T][X] go jogging [#fun] [#health] [#weekend]",
        "OK, I've untagged this task:\n   [T][X] go jogging [#fun] [#weekend]",
        "Here are the tasks in your list:\n 1.[T][X] go jogging [#fun] [#weekend]\n 2.[D][ ] submit report [#work] (by: 2026-12-31)\n 3.[E][ ] team dinner [#social] (from: 2026-09-20 19:00 to: 2026-09-20 21:00)",
        "Invalid tag. Tags must start with # and contain 1-10 non-whitespace characters. o/T\\>",
        "Invalid tag. Tags must start with # and contain 1-10 non-whitespace characters. o/T\\>",
        "Commands:\ntodo <description> [/tag #tag1 #tag2 ...]\ndeadline <description> /by <date> [/tag #tag1 #tag2 ...]\nevent <description> /from <start> /to <end> [/tag #tag1 #tag2 ...]\ntag <task number> #tag1 [#tag2 ...]\nuntag <task number> #tag1 [#tag2 ...]\nfilter #tag-substring\nlist\nfind <keyword>\nmark <task number>\nunmark <task number>\ndelete <task number>\ntimecheck <date/time>\nhelp\nbye\n\nTags must start with # and contain 1-10 non-whitespace characters.\nEach task can have at most 10 tags.\nTags are case-sensitive, displayed alphabetically, and searched case-insensitively.",
        "Bye. See you around!"
      ]
    },
    {
      "name": "Error-handling edge cases",
      "aim": "Verify that duplicate tasks, repeated modifiers, invalid event ranges, and malformed whitespace are rejected without corrupting the session.",
      "inputs": [
        "todo duplicate",
        "todo duplicate",
        "deadline duplicate /by 2026-08-27 /by 2026-08-28",
        "event invalid range /from 2026-08-27 10:00 /to 2026-08-27 10:00",
        "todo  duplicate",
        " todo spaced",
        "delete 4",
        "bye"
      ],
      "expected_outputs": [
        "Got it. I've added this task:\n  [T][ ] duplicate\nNow you have 4 tasks in the list.",
        "Cannot add a duplicate task. o/T\\>",
        "Invalid format. Use: deadline <description> /by <date> [/tag #tag1 #tag2 ...] o/T\\>",
        "Event start time must be before end time. o/T\\>",
        "Please input something correct. o/T\\>",
        "Please input something correct. o/T\\>",
        "Noted. I've removed this task:\n   [T][ ] duplicate\n Now you have 3 tasks in the list.",
        "Bye. See you around!"
      ]
    }
  ]
}
```

## Test case 6: Tagging workflow

Aim: Verify tagged creation for all task types, tag and untag mutations, tag-aware search, tag filtering, validation, help output, and persistence-related display behavior.

Inputs:

```text
todo go jogging /tag #health #fun
deadline submit report /by 2026-12-31 /tag #work
event team dinner /from 2026-09-20 19:00 /to 2026-09-20 21:00 /tag #social
mark 1
tag 1 #weekend #fun
find FUN
filter #HE
untag 1 #health
list
todo invalid /tag #
tag 1 fun
help
bye
```

Expected output: Tagged tasks show sorted tags in [#tag]-style suffixes. find searches tags, filter performs case-insensitive substring matching on tags, duplicate additions are ignored, and malformed tags are rejected.

## Test case 2: Load saved tasks and clear list

Aim: Verify that tasks saved by the previous session are restored on startup, that a missing to-do name is rejected, and that the list can be cleared for later test cases.

Inputs:

```text
list
todo
delete 1
delete 1
delete 1
delete 1
delete 1
delete 1
delete 1
list
bye
```

Expected output:

```text
Here are the tasks in your list:
 1.[T][X] read book
 2.[D][ ] return book (by: 2026-06-06)
 3.[E][ ] project meeting (from: 2026-08-06 14:00 to: 2026-08-06 16:00)
 4.[T][X] join sports club
 5.[T][ ] borrow book
 6.[D][ ] return book (by: 2026-06-07)
 7.[E][ ] project meeting (from: 2026-08-10 14:00 to: 2026-08-10 16:00)
Invalid format. Use: todo <description> [/tag #tag1 #tag2 ...] o/T\>
Task list empty. Good job! Here's a cookie. o/T\>
```
## Test case 3: Error messages

Aim: Verify that invalid commands, task numbers, structured task formats, and invalid find inputs use the custom Turtley error message suffix.

Inputs:

```text
deadline /by tomorrow
event /from 2pm /to 3pm
mark nope
unmark nope
mark 1
unmark 1

unknown command
find book
find
deadline malformed /by 2026-02-30
event malformed /from 31-02-2026 /to 01-03-2026
event compact time /from 31-12-2026 2300 /to 01-01-2027 0030
delete 1
bye
```

Expected output:

```text
Invalid format. Use: deadline <description> /by <date> [/tag #tag1 #tag2 ...] o/T\>
Invalid format. Use: event <description> /from <start> /to <end> [/tag #tag1 #tag2 ...] o/T\>
Please provide a valid task number. o/T\>
Please provide a valid task number. o/T\>
Task number is not in your list. o/T\>
Task number is not in your list. o/T\>
Please input something. o/T\>
Please input something correct. o/T\>
Here are the matching tasks in your list:
None! o/T\>
Invalid format. Use: find <keyword> o/T\>
Invalid date/time format. Use yyyy-MM-dd, dd-MM-yyyy, yyyy/MM/dd, dd/MM/yyyy, yyyy-MM-dd HH:mm, dd-MM-yyyy HH:mm, yyyy/MM/dd HH:mm, dd/MM/yyyy HH:mm, yyyy-MM-dd HHmm, dd-MM-yyyy HHmm, yyyy/MM/dd HHmm, or dd/MM/yyyy HHmm. o/T\>
Invalid date/time format. Use yyyy-MM-dd, dd-MM-yyyy, yyyy/MM/dd, dd/MM/yyyy, yyyy-MM-dd HH:mm, dd-MM-yyyy HH:mm, yyyy/MM/dd HH:mm, dd/MM/yyyy HH:mm, yyyy-MM-dd HHmm, dd-MM-yyyy HHmm, yyyy/MM/dd HHmm, or dd/MM/yyyy HHmm. o/T\>
Got it. I've added this task:
  [E][ ] compact time (from: 2026-12-31 23:00 to: 2027-01-01 00:30)
Now you have 1 tasks in the list.
 Noted. I've removed this task:
   [E][ ] compact time (from: 2026-12-31 23:00 to: 2027-01-01 00:30)
 Now you have 0 tasks in the list.
Bye. See you around!
```

## Test case 4: Delete task workflow

Aim: Verify that a task is deleted by its one-based number, its details and updated task count are shown, later tasks are renumbered, and the updated list is saved.

Inputs:

```text
todo read book
deadline return book /by 2026-06-06
event project meeting /from 06-08-2026 14:00 /to 06-08-2026 16:00
todo join sports club
todo borrow book
mark 1
mark 2
list
delete 3
list
bye
```

Expected output for the relevant commands:

```text
 Here are the tasks in your list:
 1.[T][X] read book
 2.[D][X] return book (by: 2026-06-06)
 3.[E][ ] project meeting (from: 2026-08-06 14:00 to: 2026-08-06 16:00)
 4.[T][ ] join sports club
 5.[T][ ] borrow book
 Noted. I've removed this task:
   [E][ ] project meeting (from: 2026-08-06 14:00 to: 2026-08-06 16:00)
 Now you have 4 tasks in the list.
 1.[T][X] read book
 2.[D][X] return book (by: 2026-06-06)
 3.[T][ ] join sports club
 4.[T][ ] borrow book
```

## Test case 5: Timecheck workflow

Aim: Verify that timecheck lists only deadlines and events on or before a supplied date/time, handles a date with no matches, rejects invalid input, and leaves the task list unchanged.

Inputs:

```text
event planning /from 06/08/2026 09:00 /to 06/08/2026 10:00
timecheck 2026-06-30
timecheck 2026-08-27 12:00
timecheck 2025-01-01
timecheck invalid
delete 5
delete 1
delete 1
delete 1
delete 1
bye
```

Expected output for the relevant commands:

```text
Got it. I've added this task:
  [E][ ] planning (from: 2026-08-06 09:00 to: 2026-08-06 10:00)
Now you have 5 tasks in the list.
 Here are the deadline and event tasks on or before 2026-06-30:
 2.[D][X] return book (by: 2026-06-06)
 Here are the deadline and event tasks on or before 2026-08-27 12:00:
 2.[D][X] return book (by: 2026-06-06)
 5.[E][ ] planning (from: 2026-08-06 09:00 to: 2026-08-06 10:00)
 Here are the deadline and event tasks on or before 2025-01-01:
None! o/T\>
Invalid date/time format. Use yyyy-MM-dd, dd-MM-yyyy, yyyy/MM/dd, dd/MM/yyyy, yyyy-MM-dd HH:mm, dd-MM-yyyy HH:mm, yyyy/MM/dd HH:mm, dd/MM/yyyy HH:mm, yyyy-MM-dd HHmm, dd-MM-yyyy HHmm, yyyy/MM/dd HHmm, or dd/MM/yyyy HHmm. o/T\>
 Noted. I've removed this task:
   [E][ ] planning (from: 2026-08-06 09:00 to: 2026-08-06 10:00)
 Now you have 4 tasks in the list.
 Noted. I've removed this task:
   [T][X] read book
 Now you have 3 tasks in the list.
 Noted. I've removed this task:
   [D][X] return book (by: 2026-06-06)
 Now you have 2 tasks in the list.
 Noted. I've removed this task:
   [T][ ] join sports club
 Now you have 1 tasks in the list.
 Noted. I've removed this task:
   [T][ ] borrow book
 Now you have 0 tasks in the list.
```

## Test case 7: Error-handling edge cases

Aim: Verify that duplicate tasks, repeated modifiers, invalid event ranges, and malformed whitespace are rejected without corrupting the session.

Inputs:

```text
todo duplicate
todo duplicate
deadline duplicate /by 2026-08-27 /by 2026-08-28
event invalid range /from 2026-08-27 10:00 /to 2026-08-27 10:00
todo  duplicate
 todo spaced
delete 4
bye
```

Expected output: The first task is added; each invalid input produces an error while leaving the task list usable.
