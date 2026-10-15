# UI Test Plan

This file records console UI test cases for `LenZaBot`.

Focus:
- invalid input handling
- command parsing regressions
- a small number of happy-path checks around task creation and status changes

Each test case below includes:
- an aim
- the commands to enter
- the expected output for each command
- a JSON block used by `.opencode/skills/test-ui/run-ui-tests.ps1`

## TC-INVALID-DESCRIPTION-TRAILING-PIPE

Aim: Reject deadline and event descriptions that end with a save-file separator pipe.

Commands:
```text
deadline trailing | /by 2026-09-30
event trailing | /from 2026-09-20 /to 2026-09-21
bye
```

Expected output:
```text
Oops: task descriptions cannot contain ` | `.
Oops: task descriptions cannot contain ` | `.
Bye! See ya later.
```

```json
{
  "id": "TC-INVALID-DESCRIPTION-TRAILING-PIPE",
  "aim": "Reject deadline and event descriptions that end with a save-file separator pipe.",
  "commands": [
    "deadline trailing | /by 2026-09-30",
    "event trailing | /from 2026-09-20 /to 2026-09-21",
    "bye"
  ],
  "expectedOutputs": [
    ["Oops: task descriptions cannot contain ` | `."],
    ["Oops: task descriptions cannot contain ` | `."],
    ["Bye! See ya later."]
  ]
}
```

## TC-INVALID-TODO-EMPTY

Aim: Reject a `todo` command with no description.

Commands:
```text
todo
bye
```

Expected output:
```text
Oops: the description for `todo` cannot be empty.
Bye! See ya later.
```

```json
{
  "id": "TC-INVALID-TODO-EMPTY",
  "aim": "Reject a `todo` command with no description.",
  "commands": [
    "todo",
    "bye"
  ],
  "expectedOutputs": [
    ["Oops: the description for `todo` cannot be empty."],
    ["Bye! See ya later."]
  ]
}
```

## TC-VALID-COMMAND-ALIASES

Aim: Confirm common command aliases behave like their full command names.

Commands:
```text
t read book
d return book /by 6/6/2020 1700
e project meeting /from 6/8/2020 1400 /to 6/8/2020 1600
ls
bye
```

Expected output:
```text
Filed on your task desk: [T][ ] read book
Filed on your task desk: [D][ ] return book (by: Jun 6 2020, 5:00 PM)
Filed on your task desk: [E][ ] project meeting (from: Aug 6 2020, 2:00 PM to: Aug 6 2020, 4:00 PM)
1. [T][ ] read book
2. [D][ ] return book (by: Jun 6 2020, 5:00 PM)
3. [E][ ] project meeting (from: Aug 6 2020, 2:00 PM to: Aug 6 2020, 4:00 PM)
Bye! See ya later.
```

```json
{
  "id": "TC-VALID-COMMAND-ALIASES",
  "aim": "Confirm common command aliases behave like their full command names.",
  "commands": [
    "t read book",
    "d return book /by 6/6/2020 1700",
    "e project meeting /from 6/8/2020 1400 /to 6/8/2020 1600",
    "ls",
    "bye"
  ],
  "expectedOutputs": [
    ["Filed on your task desk: [T][ ] read book"],
    ["Filed on your task desk: [D][ ] return book (by: Jun 6 2020, 5:00 PM)"],
    ["Filed on your task desk: [E][ ] project meeting (from: Aug 6 2020, 2:00 PM to: Aug 6 2020, 4:00 PM)"],
    [
      "1. [T][ ] read book",
      "2. [D][ ] return book (by: Jun 6 2020, 5:00 PM)",
      "3. [E][ ] project meeting (from: Aug 6 2020, 2:00 PM to: Aug 6 2020, 4:00 PM)"
    ],
    ["Bye! See ya later."]
  ]
}
```

## TC-INVALID-UNKNOWN-COMMAND

Aim: Reject an unknown command instead of treating it as a task.

Commands:
```text
blah
bye
```

Expected output:
```text
Oops: I don't understand the command "blah". Try `list` or add a task.
Bye! See ya later.
```

```json
{
  "id": "TC-INVALID-UNKNOWN-COMMAND",
  "aim": "Reject an unknown command instead of treating it as a task.",
  "commands": [
    "blah",
    "bye"
  ],
  "expectedOutputs": [
    ["Oops: I don't understand the command \"blah\". Try `list` or add a task."],
    ["Bye! See ya later."]
  ]
}
```

## TC-INVALID-FIND-EMPTY

Aim: Reject a `find` command without a search keyword.

Commands:
```text
find
bye
```

Expected output:
```text
Oops: the keyword for `find` cannot be empty.
Bye! See ya later.
```

```json
{
  "id": "TC-INVALID-FIND-EMPTY",
  "aim": "Reject a `find` command without a search keyword.",
  "commands": [
    "find",
    "bye"
  ],
  "expectedOutputs": [
    ["Oops: the keyword for `find` cannot be empty."],
    ["Bye! See ya later."]
  ]
}
```

## TC-INVALID-DEADLINE-MISSING-BY

Aim: Reject a `deadline` command that is missing `/by`.

Commands:
```text
deadline return book
bye
```

Expected output:
```text
Oops: use `deadline <description> /by <time>`.
Bye! See ya later.
```

```json
{
  "id": "TC-INVALID-DEADLINE-MISSING-BY",
  "aim": "Reject a `deadline` command that is missing `/by`.",
  "commands": [
    "deadline return book",
    "bye"
  ],
  "expectedOutputs": [
    ["Oops: use `deadline <description> /by <time>`."],
    ["Bye! See ya later."]
  ]
}
```

## TC-INVALID-DEADLINE-BAD-DATE

Aim: Reject a `deadline` command whose `/by` value is not a valid calendar date.

Commands:
```text
deadline return book /by 31/2/2020 1800
bye
```

Expected output:
```text
Oops: `/by`, `/from`, and `/to` must be a date/time like `2/12/2019 1800` or `2019-12-02 1800`.
Bye! See ya later.
```

```json
{
  "id": "TC-INVALID-DEADLINE-BAD-DATE",
  "aim": "Reject a `deadline` command whose `/by` value is not a valid calendar date.",
  "commands": [
    "deadline return book /by 31/2/2020 1800",
    "bye"
  ],
  "expectedOutputs": [
    ["Oops: `/by`, `/from`, and `/to` must be a date/time like `2/12/2019 1800` or `2019-12-02 1800`."],
    ["Bye! See ya later."]
  ]
}
```

## TC-INVALID-EVENT-MISSING-TO

Aim: Reject an `event` command that does not include `/to`.

Commands:
```text
event project meeting /from Mon 2pm
bye
```

Expected output:
```text
Oops: use `event <description> /from <start> /to <end>`.
Bye! See ya later.
```

```json
{
  "id": "TC-INVALID-EVENT-MISSING-TO",
  "aim": "Reject an `event` command that does not include `/to`.",
  "commands": [
    "event project meeting /from Mon 2pm",
    "bye"
  ],
  "expectedOutputs": [
    ["Oops: use `event <description> /from <start> /to <end>`."],
    ["Bye! See ya later."]
  ]
}
```

## TC-INVALID-MARK-NONNUMERIC

Aim: Reject `mark` when the task number is not numeric.

Commands:
```text
mark x
bye
```

Expected output:
```text
Oops: `mark` needs a valid task number.
Bye! See ya later.
```

```json
{
  "id": "TC-INVALID-MARK-NONNUMERIC",
  "aim": "Reject `mark` when the task number is not numeric.",
  "commands": [
    "mark x",
    "bye"
  ],
  "expectedOutputs": [
    ["Oops: `mark` needs a valid task number."],
    ["Bye! See ya later."]
  ]
}
```

## TC-INVALID-MARK-EMPTY-LIST

Aim: Reject `mark` when there are no tasks in the list.

Commands:
```text
mark 1
bye
```

Expected output:
```text
Oops: there are no tasks in the list yet.
Bye! See ya later.
```

```json
{
  "id": "TC-INVALID-MARK-EMPTY-LIST",
  "aim": "Reject `mark` when there are no tasks in the list.",
  "commands": [
    "mark 1",
    "bye"
  ],
  "expectedOutputs": [
    ["Oops: there are no tasks in the list yet."],
    ["Bye! See ya later."]
  ]
}
```

## TC-VALID-BASIC-FLOW

Aim: Confirm a basic happy path across `todo`, `deadline`, `event`, `list`, `mark`, and `unmark`.

Commands:
```text
todo borrow book
deadline return book /by 2/12/2019 1800
event project meeting /from 3/12/2019 1400 /to 4/12/2019 1600
list
mark 2
unmark 2
bye
```

Expected output:
```text
Filed on your task desk: [T][ ] borrow book
Filed on your task desk: [D][ ] return book (by: Dec 2 2019, 6:00 PM)
Filed on your task desk: [E][ ] project meeting (from: Dec 3 2019, 2:00 PM to: Dec 4 2019, 4:00 PM)
1. [T][ ] borrow book
2. [D][ ] return book (by: Dec 2 2019, 6:00 PM)
3. [E][ ] project meeting (from: Dec 3 2019, 2:00 PM to: Dec 4 2019, 4:00 PM)
Good job, marked the following task as completed: [D][X] return book (by: Dec 2 2019, 6:00 PM)
Okay, returned this task to the active desk: [D][ ] return book (by: Dec 2 2019, 6:00 PM)
Bye! See ya later.
```

```json
{
  "id": "TC-VALID-BASIC-FLOW",
  "aim": "Confirm a basic happy path across `todo`, `deadline`, `event`, `list`, `mark`, and `unmark`.",
  "commands": [
    "todo borrow book",
    "deadline return book /by 2/12/2019 1800",
    "event project meeting /from 3/12/2019 1400 /to 4/12/2019 1600",
    "list",
    "mark 2",
    "unmark 2",
    "bye"
  ],
  "expectedOutputs": [
    ["Filed on your task desk: [T][ ] borrow book"],
    ["Filed on your task desk: [D][ ] return book (by: Dec 2 2019, 6:00 PM)"],
    ["Filed on your task desk: [E][ ] project meeting (from: Dec 3 2019, 2:00 PM to: Dec 4 2019, 4:00 PM)"],
    [
      "1. [T][ ] borrow book",
      "2. [D][ ] return book (by: Dec 2 2019, 6:00 PM)",
      "3. [E][ ] project meeting (from: Dec 3 2019, 2:00 PM to: Dec 4 2019, 4:00 PM)"
    ],
    ["Good job, marked the following task as completed: [D][X] return book (by: Dec 2 2019, 6:00 PM)"],
    ["Okay, returned this task to the active desk: [D][ ] return book (by: Dec 2 2019, 6:00 PM)"],
    ["Bye! See ya later."]
  ]
}
```

## TC-VALID-FIND-TASKS

Aim: Find tasks by a case-insensitive keyword in their descriptions.

Commands:
```text
todo read book
deadline return book /by 2/12/2019 1800
todo buy milk
find BOOK
find missing
bye
```

Expected output:
```text
Filed on your task desk: [T][ ] read book
Filed on your task desk: [D][ ] return book (by: Dec 2 2019, 6:00 PM)
Filed on your task desk: [T][ ] buy milk
Here are the matching tasks in your list:
1. [T][ ] read book
2. [D][ ] return book (by: Dec 2 2019, 6:00 PM)
No tasks on the desk match "missing".
Bye! See ya later.
```

```json
{
  "id": "TC-VALID-FIND-TASKS",
  "aim": "Find tasks by a case-insensitive keyword in their descriptions.",
  "commands": [
    "todo read book",
    "deadline return book /by 2/12/2019 1800",
    "todo buy milk",
    "find BOOK",
    "find missing",
    "bye"
  ],
  "expectedOutputs": [
    ["Filed on your task desk: [T][ ] read book"],
    ["Filed on your task desk: [D][ ] return book (by: Dec 2 2019, 6:00 PM)"],
    ["Filed on your task desk: [T][ ] buy milk"],
    [
      "Here are the matching tasks in your list:",
      "1. [T][ ] read book",
      "2. [D][ ] return book (by: Dec 2 2019, 6:00 PM)"
    ],
    ["No tasks on the desk match \"missing\"."],
    ["Bye! See ya later."]
  ]
}
```

## TC-VALID-DELETE-TASK

Aim: Confirm `delete` removes the selected task and updates the remaining list.

Commands:
```text
todo read book
deadline return book /by 6/6/2020 1700
event project meeting /from 6/8/2020 1400 /to 6/8/2020 1600
todo join sports club
list
delete 3
list
bye
```

Expected output:
```text
Filed on your task desk: [T][ ] read book
Filed on your task desk: [D][ ] return book (by: Jun 6 2020, 5:00 PM)
Filed on your task desk: [E][ ] project meeting (from: Aug 6 2020, 2:00 PM to: Aug 6 2020, 4:00 PM)
Filed on your task desk: [T][ ] join sports club
1. [T][ ] read book
2. [D][ ] return book (by: Jun 6 2020, 5:00 PM)
3. [E][ ] project meeting (from: Aug 6 2020, 2:00 PM to: Aug 6 2020, 4:00 PM)
4. [T][ ] join sports club
Noted. I've removed this task:
  [E][ ] project meeting (from: Aug 6 2020, 2:00 PM to: Aug 6 2020, 4:00 PM)
Now you have 3 tasks in the list.
1. [T][ ] read book
2. [D][ ] return book (by: Jun 6 2020, 5:00 PM)
3. [T][ ] join sports club
Bye! See ya later.
```

```json
{
  "id": "TC-VALID-DELETE-TASK",
  "aim": "Confirm `delete` removes the selected task and updates the remaining list.",
  "commands": [
    "todo read book",
    "deadline return book /by 6/6/2020 1700",
    "event project meeting /from 6/8/2020 1400 /to 6/8/2020 1600",
    "todo join sports club",
    "list",
    "delete 3",
    "list",
    "bye"
  ],
  "expectedOutputs": [
    ["Filed on your task desk: [T][ ] read book"],
    ["Filed on your task desk: [D][ ] return book (by: Jun 6 2020, 5:00 PM)"],
    ["Filed on your task desk: [E][ ] project meeting (from: Aug 6 2020, 2:00 PM to: Aug 6 2020, 4:00 PM)"],
    ["Filed on your task desk: [T][ ] join sports club"],
    [
      "1. [T][ ] read book",
      "2. [D][ ] return book (by: Jun 6 2020, 5:00 PM)",
      "3. [E][ ] project meeting (from: Aug 6 2020, 2:00 PM to: Aug 6 2020, 4:00 PM)",
      "4. [T][ ] join sports club"
    ],
    [
      "Noted. I've removed this task:",
      "  [E][ ] project meeting (from: Aug 6 2020, 2:00 PM to: Aug 6 2020, 4:00 PM)",
      "Now you have 3 tasks in the list."
    ],
    [
      "1. [T][ ] read book",
      "2. [D][ ] return book (by: Jun 6 2020, 5:00 PM)",
      "3. [T][ ] join sports club"
    ],
    ["Bye! See ya later."]
  ]
}
```

## TC-VALID-FLEXIBLE-WHITESPACE

Aim: Accept mixed command case, tabs, and repeated spaces without changing task text.

Commands:
```text
ToDo    read   book
LIST
bye
```

Expected output:
```text
Filed on your task desk: [T][ ] read book
1. [T][ ] read book
Bye! See ya later.
```

```json
{
  "id": "TC-VALID-FLEXIBLE-WHITESPACE",
  "aim": "Accept mixed command case and repeated spaces.",
  "commands": [
    "ToDo    read   book",
    "LIST",
    "bye"
  ],
  "expectedOutputs": [
    ["Filed on your task desk: [T][ ] read book"],
    ["1. [T][ ] read book"],
    ["Bye! See ya later."]
  ]
}
```

## TC-INVALID-REPEATED-MARKER

Aim: Reject a deadline with more than one `/by` marker.

Commands:
```text
deadline return book /by 2/12/2019 /by 3/12/2019
bye
```

Expected output:
```text
Oops: `deadline` accepts exactly one `/by` value.
Bye! See ya later.
```

```json
{
  "id": "TC-INVALID-REPEATED-MARKER",
  "aim": "Reject a deadline with more than one /by marker.",
  "commands": [
    "deadline return book /by 2/12/2019 /by 3/12/2019",
    "bye"
  ],
  "expectedOutputs": [
    ["Oops: `deadline` accepts exactly one `/by` value."],
    ["Bye! See ya later."]
  ]
}
```

## TC-INVALID-EVENT-RANGE

Aim: Reject an event that does not end after it starts.

Commands:
```text
event meeting /from 2/12/2019 1600 /to 2/12/2019 1500
bye
```

Expected output:
```text
Oops: an event must end after it starts.
Bye! See ya later.
```

```json
{
  "id": "TC-INVALID-EVENT-RANGE",
  "aim": "Reject an event that does not end after it starts.",
  "commands": [
    "event meeting /from 2/12/2019 1600 /to 2/12/2019 1500",
    "bye"
  ],
  "expectedOutputs": [
    ["Oops: an event must end after it starts."],
    ["Bye! See ya later."]
  ]
}
```
