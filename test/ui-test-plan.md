# UI Test Plan

This file is the source of truth for console UI test cases run by the project-specific `test-ui` skill.

For each command, `expected_output` lists the exact response lines printed after the `You: ` prompt. Empty strings represent blank lines, and leading spaces are significant. Each test case starts in a fresh data folder and ends with `bye` so the program exits normally. A command with `"new_session": true` starts Nerrad again using the same test data folder, which is used to test saved tasks being loaded. A startup-error case can declare `Initial files`, use an empty command list, and verify `Expected startup output` instead.

## Test case: Add and list a todo

### Aim

Verify that a task without a date or time is stored and displayed with the Todo type icon.

### Commands and expected outputs

```json
[
  {
    "command": "todo borrow book",
    "expected_output": [
      "",
      "  Placed on your path:",
      "    [T][ ] borrow book",
      "  Now you have 1 tasks in the list.",
      "____________________________________________________________"
    ]
  },
  {
    "command": "list",
    "expected_output": [
      "",
      "  Here is the path ahead:",
      "  1.[T][ ] borrow book",
      "____________________________________________________________"
    ]
  },
  {
    "command": "bye",
    "expected_output": [
      "",
      "  Safe travels. May your path stay clear.",
      "____________________________________________________________"
    ]
  }
]
```

## Test case: Save task changes to disk

### Aim

Verify that adding, marking, unmarking, and deleting tasks complete normally so their current state can be written to the save file.

### Commands and expected outputs

```json
[
  {
    "command": "todo save me",
    "expected_output": [
      "",
      "  Placed on your path:",
      "    [T][ ] save me",
      "  Now you have 1 tasks in the list.",
      "____________________________________________________________"
    ]
  },
  {
    "command": "deadline remove me /by 2019-12-03",
    "expected_output": [
      "",
      "  Placed on your path:",
      "    [D][ ] remove me (by: Dec 03 2019)",
      "  Now you have 2 tasks in the list.",
      "____________________________________________________________"
    ]
  },
  {
    "command": "mark 1",
    "expected_output": [
      "",
      "  A bright step forward - this task is complete:",
      "    [T][X] save me",
      "____________________________________________________________"
    ]
  },
  {
    "command": "unmark 1",
    "expected_output": [
      "",
      "  No worries. This task can wait a little longer:",
      "    [T][ ] save me",
      "____________________________________________________________"
    ]
  },
  {
    "command": "delete 2",
    "expected_output": [
      "",
      "  Released from your path:",
      "    [D][ ] remove me (by: Dec 03 2019)",
      "  Now you have 1 tasks in the list.",
      "____________________________________________________________"
    ]
  },
  {
    "command": "bye",
    "expected_output": [
      "",
      "  Safe travels. May your path stay clear.",
      "____________________________________________________________"
    ]
  }
]
```

## Test case: Delete a task and renumber the list

### Aim

Verify that deleting a selected task displays the removed task, decreases the task count, shifts later tasks up, and handles invalid delete inputs.

### Commands and expected outputs

```json
[
  {
    "command": "todo read book",
    "expected_output": [
      "",
      "  Placed on your path:",
      "    [T][ ] read book",
      "  Now you have 1 tasks in the list.",
      "____________________________________________________________"
    ]
  },
  {
    "command": "deadline return book /by 2019-12-02",
    "expected_output": [
      "",
      "  Placed on your path:",
      "    [D][ ] return book (by: Dec 02 2019)",
      "  Now you have 2 tasks in the list.",
      "____________________________________________________________"
    ]
  },
  {
    "command": "event project meeting /from Mon 2pm /to 4pm",
    "expected_output": [
      "",
      "  Placed on your path:",
      "    [E][ ] project meeting (from: Mon 2pm to: 4pm)",
      "  Now you have 3 tasks in the list.",
      "____________________________________________________________"
    ]
  },
  {
    "command": "mark 2",
    "expected_output": [
      "",
      "  A bright step forward - this task is complete:",
      "    [D][X] return book (by: Dec 02 2019)",
      "____________________________________________________________"
    ]
  },
  {
    "command": "delete 2",
    "expected_output": [
      "",
      "  Released from your path:",
      "    [D][X] return book (by: Dec 02 2019)",
      "  Now you have 2 tasks in the list.",
      "____________________________________________________________"
    ]
  },
  {
    "command": "list",
    "expected_output": [
      "",
      "  Here is the path ahead:",
      "  1.[T][ ] read book",
      "  2.[E][ ] project meeting (from: Mon 2pm to: 4pm)",
      "____________________________________________________________"
    ]
  },
  {
    "command": "delete",
    "expected_output": [
      "",
      "  OOPS!!! Please provide a task number to delete.",
      "____________________________________________________________"
    ]
  },
  {
    "command": "delete two",
    "expected_output": [
      "",
      "  OOPS!!! The task number must be a whole number.",
      "____________________________________________________________"
    ]
  },
  {
    "command": "delete 3",
    "expected_output": [
      "",
      "  OOPS!!! There is no task with this number.",
      "____________________________________________________________"
    ]
  },
  {
    "command": "delete 1",
    "expected_output": [
      "",
      "  Released from your path:",
      "    [T][ ] read book",
      "  Now you have 1 tasks in the list.",
      "____________________________________________________________"
    ]
  },
  {
    "command": "list",
    "expected_output": [
      "",
      "  Here is the path ahead:",
      "  1.[E][ ] project meeting (from: Mon 2pm to: 4pm)",
      "____________________________________________________________"
    ]
  },
  {
    "command": "bye",
    "expected_output": [
      "",
      "  Safe travels. May your path stay clear.",
      "____________________________________________________________"
    ]
  }
]
```

## Test case: Add and list a deadline

### Aim

Verify that a deadline stores a valid date and displays it in a readable format.

### Commands and expected outputs

```json
[
  {
    "command": "deadline return book /by 2019-12-02",
    "expected_output": [
      "",
      "  Placed on your path:",
      "    [D][ ] return book (by: Dec 02 2019)",
      "  Now you have 1 tasks in the list.",
      "____________________________________________________________"
    ]
  },
  {
    "command": "list",
    "expected_output": [
      "",
      "  Here is the path ahead:",
      "  1.[D][ ] return book (by: Dec 02 2019)",
      "____________________________________________________________"
    ]
  },
  {
    "command": "bye",
    "expected_output": [
      "",
      "  Safe travels. May your path stay clear.",
      "____________________________________________________________"
    ]
  }
]
```

## Test case: Add and list an event

### Aim

Verify that an event stores and displays both its start and end date/time exactly as entered.

### Commands and expected outputs

```json
[
  {
    "command": "event project meeting /from Mon 2pm /to 4pm",
    "expected_output": [
      "",
      "  Placed on your path:",
      "    [E][ ] project meeting (from: Mon 2pm to: 4pm)",
      "  Now you have 1 tasks in the list.",
      "____________________________________________________________"
    ]
  },
  {
    "command": "list",
    "expected_output": [
      "",
      "  Here is the path ahead:",
      "  1.[E][ ] project meeting (from: Mon 2pm to: 4pm)",
      "____________________________________________________________"
    ]
  },
  {
    "command": "bye",
    "expected_output": [
      "",
      "  Safe travels. May your path stay clear.",
      "____________________________________________________________"
    ]
  }
]
```

## Test case: Store and update mixed task types polymorphically

### Aim

Verify that todos, deadlines, and events can coexist in one Task collection while retaining their type-specific displays and shared done status behavior.

### Commands and expected outputs

```json
[
  {
    "command": "todo read book",
    "expected_output": [
      "",
      "  Placed on your path:",
      "    [T][ ] read book",
      "  Now you have 1 tasks in the list.",
      "____________________________________________________________"
    ]
  },
  {
    "command": "deadline return book /by 2019-06-06",
    "expected_output": [
      "",
      "  Placed on your path:",
      "    [D][ ] return book (by: Jun 06 2019)",
      "  Now you have 2 tasks in the list.",
      "____________________________________________________________"
    ]
  },
  {
    "command": "event project meeting /from Aug 6th 2pm /to 4pm",
    "expected_output": [
      "",
      "  Placed on your path:",
      "    [E][ ] project meeting (from: Aug 6th 2pm to: 4pm)",
      "  Now you have 3 tasks in the list.",
      "____________________________________________________________"
    ]
  },
  {
    "command": "mark 1",
    "expected_output": [
      "",
      "  A bright step forward - this task is complete:",
      "    [T][X] read book",
      "____________________________________________________________"
    ]
  },
  {
    "command": "mark 3",
    "expected_output": [
      "",
      "  A bright step forward - this task is complete:",
      "    [E][X] project meeting (from: Aug 6th 2pm to: 4pm)",
      "____________________________________________________________"
    ]
  },
  {
    "command": "list",
    "expected_output": [
      "",
      "  Here is the path ahead:",
      "  1.[T][X] read book",
      "  2.[D][ ] return book (by: Jun 06 2019)",
      "  3.[E][X] project meeting (from: Aug 6th 2pm to: 4pm)",
      "____________________________________________________________"
    ]
  },
  {
    "command": "unmark 3",
    "expected_output": [
      "",
      "  No worries. This task can wait a little longer:",
      "    [E][ ] project meeting (from: Aug 6th 2pm to: 4pm)",
      "____________________________________________________________"
    ]
  },
  {
    "command": "list",
    "expected_output": [
      "",
      "  Here is the path ahead:",
      "  1.[T][X] read book",
      "  2.[D][ ] return book (by: Jun 06 2019)",
      "  3.[E][ ] project meeting (from: Aug 6th 2pm to: 4pm)",
      "____________________________________________________________"
    ]
  },
  {
    "command": "bye",
    "expected_output": [
      "",
      "  Safe travels. May your path stay clear.",
      "____________________________________________________________"
    ]
  }
]
```

## Test case: Reject missing task details

### Aim

Verify that empty todos and incomplete deadline or event commands show specific errors without ending the chatbot session.

### Commands and expected outputs

```json
[
  {
    "command": "todo",
    "expected_output": [
      "",
      "  OOPS!!! The description of a todo cannot be empty.",
      "____________________________________________________________"
    ]
  },
  {
    "command": "deadline return book",
    "expected_output": [
      "",
      "  OOPS!!! A deadline needs a /by date or time.",
      "____________________________________________________________"
    ]
  },
  {
    "command": "deadline /by Sunday",
    "expected_output": [
      "",
      "  OOPS!!! The description of a deadline cannot be empty.",
      "____________________________________________________________"
    ]
  },
  {
    "command": "deadline return book /by",
    "expected_output": [
      "",
      "  OOPS!!! The /by date or time of a deadline cannot be empty.",
      "____________________________________________________________"
    ]
  },
  {
    "command": "deadline return book /by tomorrow",
    "expected_output": [
      "",
      "  OOPS!!! Please use the date format yyyy-MM-dd.",
      "____________________________________________________________"
    ]
  },
  {
    "command": "event project meeting /from Mon 2pm",
    "expected_output": [
      "",
      "  OOPS!!! An event needs a /to end date or time.",
      "____________________________________________________________"
    ]
  },
  {
    "command": "event project meeting /to 4pm",
    "expected_output": [
      "",
      "  OOPS!!! An event needs a /from start date or time.",
      "____________________________________________________________"
    ]
  },
  {
    "command": "event /from Mon 2pm /to 4pm",
    "expected_output": [
      "",
      "  OOPS!!! The description of an event cannot be empty.",
      "____________________________________________________________"
    ]
  },
  {
    "command": "event project meeting /from /to 4pm",
    "expected_output": [
      "",
      "  OOPS!!! The /from start date or time of an event cannot be empty.",
      "____________________________________________________________"
    ]
  },
  {
    "command": "event project meeting /from Mon 2pm /to",
    "expected_output": [
      "",
      "  OOPS!!! The /to end date or time of an event cannot be empty.",
      "____________________________________________________________"
    ]
  },
  {
    "command": "todo valid task",
    "expected_output": [
      "",
      "  Placed on your path:",
      "    [T][ ] valid task",
      "  Now you have 1 tasks in the list.",
      "____________________________________________________________"
    ]
  },
  {
    "command": "bye",
    "expected_output": [
      "",
      "  Safe travels. May your path stay clear.",
      "____________________________________________________________"
    ]
  }
]
```

## Test case: Reject invalid task numbers

### Aim

Verify that mark and unmark commands reject missing, non-numeric, and out-of-range task numbers while allowing later valid commands.

### Commands and expected outputs

```json
[
  {
    "command": "mark",
    "expected_output": [
      "",
      "  OOPS!!! Please provide a task number to mark.",
      "____________________________________________________________"
    ]
  },
  {
    "command": "unmark zero",
    "expected_output": [
      "",
      "  OOPS!!! The task number must be a whole number.",
      "____________________________________________________________"
    ]
  },
  {
    "command": "todo borrow book",
    "expected_output": [
      "",
      "  Placed on your path:",
      "    [T][ ] borrow book",
      "  Now you have 1 tasks in the list.",
      "____________________________________________________________"
    ]
  },
  {
    "command": "mark 0",
    "expected_output": [
      "",
      "  OOPS!!! There is no task with this number.",
      "____________________________________________________________"
    ]
  },
  {
    "command": "unmark 2",
    "expected_output": [
      "",
      "  OOPS!!! There is no task with this number.",
      "____________________________________________________________"
    ]
  },
  {
    "command": "mark 1",
    "expected_output": [
      "",
      "  A bright step forward - this task is complete:",
      "    [T][X] borrow book",
      "____________________________________________________________"
    ]
  },
  {
    "command": "unmark 1",
    "expected_output": [
      "",
      "  No worries. This task can wait a little longer:",
      "    [T][ ] borrow book",
      "____________________________________________________________"
    ]
  },
  {
    "command": "bye",
    "expected_output": [
      "",
      "  Safe travels. May your path stay clear.",
      "____________________________________________________________"
    ]
  }
]
```

## Test case: Reject an unknown command

### Aim

Verify that unknown commands show a clear error and do not add an unintended task.

### Commands and expected outputs

```json
[
  {
    "command": "blah",
    "expected_output": [
      "",
      "  OOPS!!! I'm sorry, but I don't know what that means :-(",
      "____________________________________________________________"
    ]
  },
  {
    "command": "list",
    "expected_output": [
      "",
      "  Here is the path ahead:",
      "____________________________________________________________"
    ]
  },
  {
    "command": "bye",
    "expected_output": [
      "",
      "  Safe travels. May your path stay clear.",
      "____________________________________________________________"
    ]
  }
]
```

## Test case: Save all task types

### Aim

Verify that Todo, Deadline, and Event tasks can all be saved, including a completed task.

### Commands and expected outputs

```json
[
  {
    "command": "todo saved todo",
    "expected_output": [
      "",
      "  Placed on your path:",
      "    [T][ ] saved todo",
      "  Now you have 1 tasks in the list.",
      "____________________________________________________________"
    ]
  },
  {
    "command": "deadline saved deadline /by 2020-01-10",
    "expected_output": [
      "",
      "  Placed on your path:",
      "    [D][ ] saved deadline (by: Jan 10 2020)",
      "  Now you have 2 tasks in the list.",
      "____________________________________________________________"
    ]
  },
  {
    "command": "event saved event /from Mon 2pm /to 4pm",
    "expected_output": [
      "",
      "  Placed on your path:",
      "    [E][ ] saved event (from: Mon 2pm to: 4pm)",
      "  Now you have 3 tasks in the list.",
      "____________________________________________________________"
    ]
  },
  {
    "command": "mark 1",
    "expected_output": [
      "",
      "  A bright step forward - this task is complete:",
      "    [T][X] saved todo",
      "____________________________________________________________"
    ]
  },
  {
    "command": "bye",
    "expected_output": [
      "",
      "  Safe travels. May your path stay clear.",
      "____________________________________________________________"
    ]
  }
]
```

## Test case: Load saved tasks after restarting

### Aim

Verify that Todo, Deadline, and Event tasks, including their done status, are restored when Nerrad starts again.

### Commands and expected outputs

```json
[
  {
    "command": "todo read book",
    "expected_output": [
      "",
      "  Placed on your path:",
      "    [T][ ] read book",
      "  Now you have 1 tasks in the list.",
      "____________________________________________________________"
    ]
  },
  {
    "command": "deadline return book /by 2019-12-02",
    "expected_output": [
      "",
      "  Placed on your path:",
      "    [D][ ] return book (by: Dec 02 2019)",
      "  Now you have 2 tasks in the list.",
      "____________________________________________________________"
    ]
  },
  {
    "command": "event project meeting /from Mon 2pm /to 4pm",
    "expected_output": [
      "",
      "  Placed on your path:",
      "    [E][ ] project meeting (from: Mon 2pm to: 4pm)",
      "  Now you have 3 tasks in the list.",
      "____________________________________________________________"
    ]
  },
  {
    "command": "mark 2",
    "expected_output": [
      "",
      "  A bright step forward - this task is complete:",
      "    [D][X] return book (by: Dec 02 2019)",
      "____________________________________________________________"
    ]
  },
  {
    "command": "bye",
    "expected_output": [
      "",
      "  Safe travels. May your path stay clear.",
      "____________________________________________________________"
    ]
  },
  {
    "command": "list",
    "new_session": true,
    "expected_output": [
      "",
      "  Here is the path ahead:",
      "  1.[T][ ] read book",
      "  2.[D][X] return book (by: Dec 02 2019)",
      "  3.[E][ ] project meeting (from: Mon 2pm to: 4pm)",
      "____________________________________________________________"
    ]
  },
  {
    "command": "bye",
    "expected_output": [
      "",
      "  Safe travels. May your path stay clear.",
      "____________________________________________________________"
    ]
  }
]
```

## Test case: Reject corrupted save data

### Aim

Verify that Nerrad reports a clear error and stops before changing a malformed save file.

### Initial files

```json
{
  "data/nerrad.txt": "Z | 0 | unknown task"
}
```

### Expected startup output

```json
[
  "  OOPS!!! I could not load your saved tasks.",
  "____________________________________________________________"
]
```

### Commands and expected outputs

```json
[]
```

## Test case: Find tasks by description keyword

### Aim

Verify that the find command returns only tasks with matching descriptions in their list order and rejects a missing keyword.

### Commands and expected outputs

```json
[
  {
    "command": "todo read book",
    "expected_output": [
      "",
      "  Placed on your path:",
      "    [T][ ] read book",
      "  Now you have 1 tasks in the list.",
      "____________________________________________________________"
    ]
  },
  {
    "command": "deadline return book /by 2019-06-06",
    "expected_output": [
      "",
      "  Placed on your path:",
      "    [D][ ] return book (by: Jun 06 2019)",
      "  Now you have 2 tasks in the list.",
      "____________________________________________________________"
    ]
  },
  {
    "command": "todo write report",
    "expected_output": [
      "",
      "  Placed on your path:",
      "    [T][ ] write report",
      "  Now you have 3 tasks in the list.",
      "____________________________________________________________"
    ]
  },
  {
    "command": "find book",
    "expected_output": [
      "",
      "  Here are the matching waypoints:",
      "  1.[T][ ] read book",
      "  2.[D][ ] return book (by: Jun 06 2019)",
      "____________________________________________________________"
    ]
  },
  {
    "command": "find missing",
    "expected_output": [
      "",
      "  Here are the matching waypoints:",
      "____________________________________________________________"
    ]
  },
  {
    "command": "find",
    "expected_output": [
      "",
      "  OOPS!!! Please provide a keyword to find.",
      "____________________________________________________________"
    ]
  },
  {
    "command": "bye",
    "expected_output": [
      "",
      "  Safe travels. May your path stay clear.",
      "____________________________________________________________"
    ]
  }
]
```

## Test case: Record, settle, and reload loans

### Aim

Verify that lent and borrowed money can be recorded separately from tasks, settled, and restored after restarting Nerrad.

### Commands and expected outputs

```json
[
  {
    "command": "loan lend Alex Tan 12.50 /for lunch",
    "expected_output": [
      "",
      "  Kept safely in your ledger:",
      "    [LENT][OUTSTANDING] Alex Tan: S$12.50 (lunch)",
      "  You now have 1 loan records.",
      "____________________________________________________________"
    ]
  },
  {
    "command": "loan borrow Ben 5 /for bus fare",
    "expected_output": [
      "",
      "  Kept safely in your ledger:",
      "    [BORROWED][OUTSTANDING] Ben: S$5.00 (bus fare)",
      "  You now have 2 loan records.",
      "____________________________________________________________"
    ]
  },
  {
    "command": "loans",
    "expected_output": [
      "",
      "  Here is your loan ledger:",
      "  1.[LENT][OUTSTANDING] Alex Tan: S$12.50 (lunch)",
      "  2.[BORROWED][OUTSTANDING] Ben: S$5.00 (bus fare)",
      "____________________________________________________________"
    ]
  },
  {
    "command": "settle-loan 1",
    "expected_output": [
      "",
      "  Your ledger is balanced for this loan:",
      "    [LENT][SETTLED] Alex Tan: S$12.50 (lunch)",
      "____________________________________________________________"
    ]
  },
  {
    "command": "bye",
    "expected_output": [
      "",
      "  Safe travels. May your path stay clear.",
      "____________________________________________________________"
    ]
  },
  {
    "command": "loans",
    "new_session": true,
    "expected_output": [
      "",
      "  Here is your loan ledger:",
      "  1.[LENT][SETTLED] Alex Tan: S$12.50 (lunch)",
      "  2.[BORROWED][OUTSTANDING] Ben: S$5.00 (bus fare)",
      "____________________________________________________________"
    ]
  },
  {
    "command": "bye",
    "expected_output": [
      "",
      "  Safe travels. May your path stay clear.",
      "____________________________________________________________"
    ]
  }
]
```

## Test case: Reject invalid loan commands

### Aim

Verify that loan commands reject missing details, invalid directions, invalid amounts, empty reasons, and invalid loan numbers.

### Commands and expected outputs

```json
[
  {
    "command": "loan",
    "expected_output": [
      "",
      "  OOPS!!! A loan needs a person's name and an amount.",
      "____________________________________________________________"
    ]
  },
  {
    "command": "loan give Alex 10",
    "expected_output": [
      "",
      "  OOPS!!! Use loan lend or loan borrow.",
      "____________________________________________________________"
    ]
  },
  {
    "command": "loan lend Alex 0",
    "expected_output": [
      "",
      "  OOPS!!! The loan amount must be positive with at most two decimal places.",
      "____________________________________________________________"
    ]
  },
  {
    "command": "loan borrow Ben 1.999",
    "expected_output": [
      "",
      "  OOPS!!! The loan amount must be positive with at most two decimal places.",
      "____________________________________________________________"
    ]
  },
  {
    "command": "loan lend Alex 10 /for",
    "expected_output": [
      "",
      "  OOPS!!! The reason for a loan cannot be empty.",
      "____________________________________________________________"
    ]
  },
  {
    "command": "settle-loan 1",
    "expected_output": [
      "",
      "  OOPS!!! There is no loan with this number.",
      "____________________________________________________________"
    ]
  },
  {
    "command": "bye",
    "expected_output": [
      "",
      "  Safe travels. May your path stay clear.",
      "____________________________________________________________"
    ]
  }
]
```
