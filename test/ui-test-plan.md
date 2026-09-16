# UI Test Plan

The `test-ui` skill maintains and runs this plan. Each test case records its
aim, ordered console inputs, and the exact response expected after each input.
Expected responses omit the surrounding separator lines.

<!-- test-ui-plan -->
```json
{
  "source_directory": "src/main/java",
  "main_class": "walter.Walter",
  "separator": "____________________________________________________________",
  "response_timeout_seconds": 5,
  "test_cases": [
    {
      "id": "find-task-descriptions",
      "aim": "Verify find searches descriptions case-insensitively, preserves order, handles no matches, and rejects a missing keyword.",
      "inputs": [
        "todo Read Book",
        "deadline return book /by 2026-08-30",
        "todo buy milk",
        "find BOOK",
        "find milk",
        "find 2026",
        "find",
        "mark 2",
        "list",
        "bye"
      ],
      "expected_outputs": [
        [
          "Good. That's on the list now:",
          "[T][ ] Read Book",
          "The list now contains 1 task."
        ],
        [
          "Good. That's on the list now:",
          "[D][ ] return book (by: Aug 30 2026)",
          "The list now contains 2 tasks."
        ],
        [
          "Good. That's on the list now:",
          "[T][ ] buy milk",
          "The list now contains 3 tasks."
        ],
        [
          "Here's what I found:",
          "1. [T][ ] Read Book",
          "2. [D][ ] return book (by: Aug 30 2026)"
        ],
        [
          "Here's what I found:",
          "3. [T][ ] buy milk"
        ],
        ["I found no tasks matching that keyword."],
        ["Keyword is required for the find command. Stay sharp, Jesse."],
        [
          "Done. Consider this one handled:",
          "[D][X] return book (by: Aug 30 2026)"
        ],
        [
          "Here are your tasks:",
          "1. [T][ ] Read Book",
          "2. [D][X] return book (by: Aug 30 2026)",
          "3. [T][ ] buy milk"
        ],
        ["All right. The operation is closed. Stay focused, Jesse."]
      ]
    },
    {
      "id": "task-status-lifecycle",
      "aim": "Verify adding Todos, listing, marking, unmarking, and exiting in one stateful session.",
      "inputs": [
        "todo read book",
        "todo return book",
        "todo buy bread",
        "list",
        "mark 2",
        "list",
        "unmark 2",
        "list",
        "bye"
      ],
      "expected_outputs": [
        [
          "Good. That's on the list now:",
          "[T][ ] read book",
          "The list now contains 1 task."
        ],
        [
          "Good. That's on the list now:",
          "[T][ ] return book",
          "The list now contains 2 tasks."
        ],
        [
          "Good. That's on the list now:",
          "[T][ ] buy bread",
          "The list now contains 3 tasks."
        ],
        [
          "Here are your tasks:",
          "1. [T][ ] read book",
          "2. [T][ ] return book",
          "3. [T][ ] buy bread"
        ],
        [
          "Done. Consider this one handled:",
          "[T][X] return book"
        ],
        [
          "Here are your tasks:",
          "1. [T][ ] read book",
          "2. [T][X] return book",
          "3. [T][ ] buy bread"
        ],
        [
          "Understood. This goes back into the mix:",
          "[T][ ] return book"
        ],
        [
          "Here are your tasks:",
          "1. [T][ ] read book",
          "2. [T][ ] return book",
          "3. [T][ ] buy bread"
        ],
        ["All right. The operation is closed. Stay focused, Jesse."]
      ]
    },
    {
      "id": "task-state-machine-rejects-no-ops",
      "aim": "Verify repeated mark, done, and unmark operations are rejected without changing task state.",
      "inputs": [
        "todo test",
        "unmark 1",
        "mark 1",
        "mark 1",
        "done 1",
        "unmark 1",
        "unmark 1",
        "list",
        "bye"
      ],
      "expected_outputs": [
        [
          "Good. That's on the list now:",
          "[T][ ] test",
          "The list now contains 1 task."
        ],
        ["Task is already not done. Stay sharp, Jesse."],
        ["Done. Consider this one handled:", "[T][X] test"],
        ["Task is already marked as done. Stay sharp, Jesse."],
        ["Task is already marked as done. Stay sharp, Jesse."],
        ["Understood. This goes back into the mix:", "[T][ ] test"],
        ["Task is already not done. Stay sharp, Jesse."],
        ["Here are your tasks:", "1. [T][ ] test"],
        ["All right. The operation is closed. Stay focused, Jesse."]
      ]
    },
    {
      "id": "typed-task-lifecycle",
      "aim": "Verify Todo, Deadline, and Event creation, polymorphic listing, and the done command.",
      "inputs": [
        "todo borrow book",
        "deadline return book /by 2026-08-30",
        "event project meeting /at Mon 2-4pm",
        "list",
        "done 2",
        "list",
        "bye"
      ],
      "expected_outputs": [
        [
          "Good. That's on the list now:",
          "[T][ ] borrow book",
          "The list now contains 1 task."
        ],
        [
          "Good. That's on the list now:",
          "[D][ ] return book (by: Aug 30 2026)",
          "The list now contains 2 tasks."
        ],
        [
          "Good. That's on the list now:",
          "[E][ ] project meeting (at: Mon 2-4pm)",
          "The list now contains 3 tasks."
        ],
        [
          "Here are your tasks:",
          "1. [T][ ] borrow book",
          "2. [D][ ] return book (by: Aug 30 2026)",
          "3. [E][ ] project meeting (at: Mon 2-4pm)"
        ],
        [
          "Done. Consider this one handled:",
          "[D][X] return book (by: Aug 30 2026)"
        ],
        [
          "Here are your tasks:",
          "1. [T][ ] borrow book",
          "2. [D][X] return book (by: Aug 30 2026)",
          "3. [E][ ] project meeting (at: Mon 2-4pm)"
        ],
        ["All right. The operation is closed. Stay focused, Jesse."]
      ]
    },
    {
      "id": "event-from-to",
      "aim": "Verify an event with /from and /to is parsed, stored, and listed without crashing.",
      "inputs": [
        "event project meeting /from Mon 2pm /to 4pm",
        "list",
        "bye"
      ],
      "expected_outputs": [
        [
          "Good. That's on the list now:",
          "[E][ ] project meeting (from: Mon 2pm to: 4pm)",
          "The list now contains 1 task."
        ],
        [
          "Here are your tasks:",
          "1. [E][ ] project meeting (from: Mon 2pm to: 4pm)"
        ],
        ["All right. The operation is closed. Stay focused, Jesse."]
      ]
    },
    {
      "id": "unknown-and-blank-commands",
      "aim": "Verify blank input and command-prefix variants are rejected without terminating the session.",
      "inputs": [
        "",
        "   ",
        "blah",
        "hello there",
        "donee 1",
        "todoooo book",
        "listing",
        "byebye",
        "bye hello",
        "list",
        "bye"
      ],
      "expected_outputs": [
        ["Command cannot be blank. Stay sharp, Jesse."],
        ["Command cannot be blank. Stay sharp, Jesse."],
        ["Unknown command. Try: todo DESCRIPTION; deadline DESCRIPTION /by yyyy-MM-dd; event DESCRIPTION /at TIME; list; find KEYWORD; mark INDEX; unmark INDEX; delete INDEX; on yyyy-MM-dd; or bye. Stay sharp, Jesse."],
        ["Unknown command. Try: todo DESCRIPTION; deadline DESCRIPTION /by yyyy-MM-dd; event DESCRIPTION /at TIME; list; find KEYWORD; mark INDEX; unmark INDEX; delete INDEX; on yyyy-MM-dd; or bye. Stay sharp, Jesse."],
        ["Unknown command. Try: todo DESCRIPTION; deadline DESCRIPTION /by yyyy-MM-dd; event DESCRIPTION /at TIME; list; find KEYWORD; mark INDEX; unmark INDEX; delete INDEX; on yyyy-MM-dd; or bye. Stay sharp, Jesse."],
        ["Unknown command. Try: todo DESCRIPTION; deadline DESCRIPTION /by yyyy-MM-dd; event DESCRIPTION /at TIME; list; find KEYWORD; mark INDEX; unmark INDEX; delete INDEX; on yyyy-MM-dd; or bye. Stay sharp, Jesse."],
        ["Unknown command. Try: todo DESCRIPTION; deadline DESCRIPTION /by yyyy-MM-dd; event DESCRIPTION /at TIME; list; find KEYWORD; mark INDEX; unmark INDEX; delete INDEX; on yyyy-MM-dd; or bye. Stay sharp, Jesse."],
        ["Unknown command. Try: todo DESCRIPTION; deadline DESCRIPTION /by yyyy-MM-dd; event DESCRIPTION /at TIME; list; find KEYWORD; mark INDEX; unmark INDEX; delete INDEX; on yyyy-MM-dd; or bye. Stay sharp, Jesse."],
        ["bye does not take arguments. Stay sharp, Jesse."],
        ["The board is clean. Nothing needs doing."],
        ["All right. The operation is closed. Stay focused, Jesse."]
      ]
    },
    {
      "id": "typed-command-errors",
      "aim": "Verify malformed Todo, Deadline, and Event commands produce specific errors and do not add tasks.",
      "inputs": [
        "todo",
        "todo    ",
        "deadline",
        "deadline return book",
        "deadline /by Sunday",
        "deadline return book /by",
        "deadline report /bypass Sunday",
        "deadline report /by 2026-08-30 /by 2026-09-01",
        "event",
        "event meeting",
        "event /at Monday",
        "event meeting /at",
        "event meeting /atlas",
        "event meeting /from Monday",
        "event meeting /to 4pm",
        "event /from Monday /to 4pm",
        "event meeting /from /to 4pm",
        "event meeting /from Monday /to",
        "event meeting /at 2pm /from 2pm /to 4pm",
        "event meeting /from 2pm /to 4pm /to 5pm",
        "event meeting /to 4pm /from 2pm",
        "list",
        "bye"
      ],
      "expected_outputs": [
        ["Todo description cannot be empty. Stay sharp, Jesse."],
        ["Todo description cannot be empty. Stay sharp, Jesse."],
        ["Deadline description cannot be empty. Stay sharp, Jesse."],
        ["Deadline requires /by. Stay sharp, Jesse."],
        ["Deadline description cannot be empty. Stay sharp, Jesse."],
        ["Deadline date/time cannot be empty. Stay sharp, Jesse."],
        ["Deadline requires /by. Stay sharp, Jesse."],
        ["Deadline requires exactly one /by. Stay sharp, Jesse."],
        ["Event description cannot be empty. Stay sharp, Jesse."],
        ["Event requires /at or /from and /to. Stay sharp, Jesse."],
        ["Event description cannot be empty. Stay sharp, Jesse."],
        ["Event date/time cannot be empty. Stay sharp, Jesse."],
        ["Event requires /at or /from and /to. Stay sharp, Jesse."],
        ["Event requires /to command when given /from command. Stay sharp, Jesse."],
        ["Event requires /from command when given /to command. Stay sharp, Jesse."],
        ["Event description cannot be empty. Stay sharp, Jesse."],
        ["Event start cannot be empty. Stay sharp, Jesse."],
        ["Event end cannot be empty. Stay sharp, Jesse."],
        ["Event must use either one /at or one /from and one /to. Stay sharp, Jesse."],
        ["Event requires exactly one /from and one /to. Stay sharp, Jesse."],
        ["Event /from must appear before /to. Stay sharp, Jesse."],
        ["The board is clean. Nothing needs doing."],
        ["All right. The operation is closed. Stay focused, Jesse."]
      ]
    },
    {
      "id": "deadline-date-validation",
      "aim": "Verify ISO Deadline dates are parsed and formatted while textual, malformed, and impossible dates are rejected.",
      "inputs": [
        "deadline submit CS2103 tutorial /by 2026-08-30",
        "deadline christmas /by 2026-12-25",
        "deadline january /by 2027-01-05",
        "deadline leap /by 2028-02-29",
        "deadline report /by Sunday",
        "deadline report /by tomorrow",
        "deadline report /by potato",
        "deadline report /by 30-08-2026",
        "deadline report /by 08/30/2026",
        "deadline report /by 2026-02-30",
        "deadline report /by 2027-02-29",
        "deadline report /by 2026-13-01",
        "deadline report /by 2026-00-01",
        "deadline report /by 2026-1-1",
        "deadline report /by 26-01-01",
        "deadline report /by 0000-01-01",
        "deadline report /by -0001-01-01",
        "list",
        "bye"
      ],
      "expected_outputs": [
        [
          "Good. That's on the list now:",
          "[D][ ] submit CS2103 tutorial (by: Aug 30 2026)",
          "The list now contains 1 task."
        ],
        [
          "Good. That's on the list now:",
          "[D][ ] christmas (by: Dec 25 2026)",
          "The list now contains 2 tasks."
        ],
        [
          "Good. That's on the list now:",
          "[D][ ] january (by: Jan 5 2027)",
          "The list now contains 3 tasks."
        ],
        [
          "Good. That's on the list now:",
          "[D][ ] leap (by: Feb 29 2028)",
          "The list now contains 4 tasks."
        ],
        ["Deadline date must be in yyyy-MM-dd format. Stay sharp, Jesse."],
        ["Deadline date must be in yyyy-MM-dd format. Stay sharp, Jesse."],
        ["Deadline date must be in yyyy-MM-dd format. Stay sharp, Jesse."],
        ["Deadline date must be in yyyy-MM-dd format. Stay sharp, Jesse."],
        ["Deadline date must be in yyyy-MM-dd format. Stay sharp, Jesse."],
        ["Deadline date is not a valid calendar date. Stay sharp, Jesse."],
        ["Deadline date is not a valid calendar date. Stay sharp, Jesse."],
        ["Deadline date is not a valid calendar date. Stay sharp, Jesse."],
        ["Deadline date is not a valid calendar date. Stay sharp, Jesse."],
        ["Deadline date must be in yyyy-MM-dd format. Stay sharp, Jesse."],
        ["Deadline date must be in yyyy-MM-dd format. Stay sharp, Jesse."],
        ["Deadline date must be in yyyy-MM-dd format. Stay sharp, Jesse."],
        ["Deadline date must be in yyyy-MM-dd format. Stay sharp, Jesse."],
        [
          "Here are your tasks:",
          "1. [D][ ] submit CS2103 tutorial (by: Aug 30 2026)",
          "2. [D][ ] christmas (by: Dec 25 2026)",
          "3. [D][ ] january (by: Jan 5 2027)",
          "4. [D][ ] leap (by: Feb 29 2028)"
        ],
        ["All right. The operation is closed. Stay focused, Jesse."]
      ]
    },
    {
      "id": "deadline-date-lookup",
      "aim": "Verify the on command filters Deadlines by LocalDate in list order without changing their status.",
      "inputs": [
        "deadline submit CS2103 /by 2026-08-30",
        "deadline MA2116 homework /by 2026-08-31",
        "deadline project report /by 2026-08-30",
        "todo read book",
        "mark 3",
        "on 2026-08-30",
        "on 2026-08-31",
        "on 2026-12-31",
        "on Sunday",
        "on tomorrow",
        "on 30-08-2026",
        "on 2026-02-30",
        "on 2027-02-29",
        "on 2026-13-01",
        "on 0000-01-01",
        "on -0001-01-01",
        "on 2028-02-29",
        "on",
        "onward 2026-08-30",
        "list",
        "bye"
      ],
      "expected_outputs": [
        [
          "Good. That's on the list now:",
          "[D][ ] submit CS2103 (by: Aug 30 2026)",
          "The list now contains 1 task."
        ],
        [
          "Good. That's on the list now:",
          "[D][ ] MA2116 homework (by: Aug 31 2026)",
          "The list now contains 2 tasks."
        ],
        [
          "Good. That's on the list now:",
          "[D][ ] project report (by: Aug 30 2026)",
          "The list now contains 3 tasks."
        ],
        [
          "Good. That's on the list now:",
          "[T][ ] read book",
          "The list now contains 4 tasks."
        ],
        [
          "Done. Consider this one handled:",
          "[D][X] project report (by: Aug 30 2026)"
        ],
        [
          "Here is the schedule for Aug 30 2026:",
          "1. [D][ ] submit CS2103 (by: Aug 30 2026)",
          "2. [D][X] project report (by: Aug 30 2026)"
        ],
        [
          "Here is the schedule for Aug 31 2026:",
          "1. [D][ ] MA2116 homework (by: Aug 31 2026)"
        ],
        ["No deadlines are scheduled for Dec 31 2026."],
        ["Date must be in yyyy-MM-dd format. Stay sharp, Jesse."],
        ["Date must be in yyyy-MM-dd format. Stay sharp, Jesse."],
        ["Date must be in yyyy-MM-dd format. Stay sharp, Jesse."],
        ["Date is not a valid calendar date. Stay sharp, Jesse."],
        ["Date is not a valid calendar date. Stay sharp, Jesse."],
        ["Date is not a valid calendar date. Stay sharp, Jesse."],
        ["Date must be in yyyy-MM-dd format. Stay sharp, Jesse."],
        ["Date must be in yyyy-MM-dd format. Stay sharp, Jesse."],
        ["No deadlines are scheduled for Feb 29 2028."],
        ["Date is required for the on command. Stay sharp, Jesse."],
        ["Unknown command. Try: todo DESCRIPTION; deadline DESCRIPTION /by yyyy-MM-dd; event DESCRIPTION /at TIME; list; find KEYWORD; mark INDEX; unmark INDEX; delete INDEX; on yyyy-MM-dd; or bye. Stay sharp, Jesse."],
        [
          "Here are your tasks:",
          "1. [D][ ] submit CS2103 (by: Aug 30 2026)",
          "2. [D][ ] MA2116 homework (by: Aug 31 2026)",
          "3. [D][X] project report (by: Aug 30 2026)",
          "4. [T][ ] read book"
        ],
        ["All right. The operation is closed. Stay focused, Jesse."]
      ]
    },
    {
      "id": "task-number-errors",
      "aim": "Verify task-number validation including integer boundaries, whitespace handling, recovery, and state integrity.",
      "inputs": [
        "done 1",
        "todo read book",
        "done",
        "done abc",
        "done two",
        "done 1.5",
        "done !",
        "done 0",
        "done -1",
        "done 999",
        "done 1 2",
        "done 999999999999999999999999999999999999",
        "done 2147483647",
        "done 2147483648",
        "done -2147483648",
        "done -2147483649",
        "mark abc",
        "unmark 2",
        "done\t1",
        "list",
        "bye"
      ],
      "expected_outputs": [
        ["Task number is out of range. Stay sharp, Jesse."],
        [
          "Good. That's on the list now:",
          "[T][ ] read book",
          "The list now contains 1 task."
        ],
        ["Task number is required. Stay sharp, Jesse."],
        ["Task number must be an integer. Stay sharp, Jesse."],
        ["Task number must be an integer. Stay sharp, Jesse."],
        ["Task number must be an integer. Stay sharp, Jesse."],
        ["Task number must be an integer. Stay sharp, Jesse."],
        ["Task number is out of range. Stay sharp, Jesse."],
        ["Task number is out of range. Stay sharp, Jesse."],
        ["Task number is out of range. Stay sharp, Jesse."],
        ["Task number must be an integer. Stay sharp, Jesse."],
        ["Task number must be an integer. Stay sharp, Jesse."],
        ["Task number is out of range. Stay sharp, Jesse."],
        ["Task number must be an integer. Stay sharp, Jesse."],
        ["Task number is out of range. Stay sharp, Jesse."],
        ["Task number must be an integer. Stay sharp, Jesse."],
        ["Task number must be an integer. Stay sharp, Jesse."],
        ["Task number is out of range. Stay sharp, Jesse."],
        [
          "Done. Consider this one handled:",
          "[T][X] read book"
        ],
        [
          "Here are your tasks:",
          "1. [T][X] read book"
        ],
        ["All right. The operation is closed. Stay focused, Jesse."]
      ]
    },
    {
      "id": "recovery-after-errors",
      "aim": "Verify expected errors are caught centrally and later valid commands preserve correct state.",
      "inputs": [
        "todo",
        "todo read book",
        "deadline return book",
        "deadline return book /by 2026-08-30",
        "done abc",
        "done 1",
        "blah",
        "list",
        "bye"
      ],
      "expected_outputs": [
        ["Todo description cannot be empty. Stay sharp, Jesse."],
        [
          "Good. That's on the list now:",
          "[T][ ] read book",
          "The list now contains 1 task."
        ],
        ["Deadline requires /by. Stay sharp, Jesse."],
        [
          "Good. That's on the list now:",
          "[D][ ] return book (by: Aug 30 2026)",
          "The list now contains 2 tasks."
        ],
        ["Task number must be an integer. Stay sharp, Jesse."],
        [
          "Done. Consider this one handled:",
          "[T][X] read book"
        ],
        ["Unknown command. Try: todo DESCRIPTION; deadline DESCRIPTION /by yyyy-MM-dd; event DESCRIPTION /at TIME; list; find KEYWORD; mark INDEX; unmark INDEX; delete INDEX; on yyyy-MM-dd; or bye. Stay sharp, Jesse."],
        [
          "Here are your tasks:",
          "1. [T][X] read book",
          "2. [D][ ] return book (by: Aug 30 2026)"
        ],
        ["All right. The operation is closed. Stay focused, Jesse."]
      ]
    },
    {
      "id": "delete-lifecycle",
      "aim": "Verify deletion removes the selected polymorphic task, shifts numbering, and preserves other task states.",
      "inputs": [
        "todo read book",
        "deadline return book /by 2026-08-30",
        "event project meeting /at Mon 2-4pm",
        "list",
        "done 2",
        "delete 1",
        "list",
        "delete 2",
        "list",
        "bye"
      ],
      "expected_outputs": [
        [
          "Good. That's on the list now:",
          "[T][ ] read book",
          "The list now contains 1 task."
        ],
        [
          "Good. That's on the list now:",
          "[D][ ] return book (by: Aug 30 2026)",
          "The list now contains 2 tasks."
        ],
        [
          "Good. That's on the list now:",
          "[E][ ] project meeting (at: Mon 2-4pm)",
          "The list now contains 3 tasks."
        ],
        [
          "Here are your tasks:",
          "1. [T][ ] read book",
          "2. [D][ ] return book (by: Aug 30 2026)",
          "3. [E][ ] project meeting (at: Mon 2-4pm)"
        ],
        [
          "Done. Consider this one handled:",
          "[D][X] return book (by: Aug 30 2026)"
        ],
        [
          "Done. I've removed this from the list:",
          "[T][ ] read book",
          "The list now contains 2 tasks."
        ],
        [
          "Here are your tasks:",
          "1. [D][X] return book (by: Aug 30 2026)",
          "2. [E][ ] project meeting (at: Mon 2-4pm)"
        ],
        [
          "Done. I've removed this from the list:",
          "[E][ ] project meeting (at: Mon 2-4pm)",
          "The list now contains 1 task."
        ],
        [
          "Here are your tasks:",
          "1. [D][X] return book (by: Aug 30 2026)"
        ],
        ["All right. The operation is closed. Stay focused, Jesse."]
      ]
    },
    {
      "id": "delete-errors-preserve-state",
      "aim": "Verify malformed and out-of-range delete commands never crash or modify the task list.",
      "inputs": [
        "delete 1",
        "list",
        "todo keep me",
        "delete",
        "list",
        "delete      ",
        "list",
        "delete abc",
        "list",
        "delete two",
        "list",
        "delete 1.5",
        "list",
        "delete 0",
        "list",
        "delete -1",
        "list",
        "delete 999",
        "list",
        "delete 1 2",
        "list",
        "delete 999999999999999999999999999",
        "list",
        "deletee 1",
        "list",
        "deleted 1",
        "list",
        "deleteSomething 1",
        "list",
        "bye"
      ],
      "expected_outputs": [
        ["Task number is out of range. Stay sharp, Jesse."],
        ["The board is clean. Nothing needs doing."],
        [
          "Good. That's on the list now:",
          "[T][ ] keep me",
          "The list now contains 1 task."
        ],
        ["Task number is required. Stay sharp, Jesse."],
        ["Here are your tasks:", "1. [T][ ] keep me"],
        ["Task number is required. Stay sharp, Jesse."],
        ["Here are your tasks:", "1. [T][ ] keep me"],
        ["Task number must be an integer. Stay sharp, Jesse."],
        ["Here are your tasks:", "1. [T][ ] keep me"],
        ["Task number must be an integer. Stay sharp, Jesse."],
        ["Here are your tasks:", "1. [T][ ] keep me"],
        ["Task number must be an integer. Stay sharp, Jesse."],
        ["Here are your tasks:", "1. [T][ ] keep me"],
        ["Task number is out of range. Stay sharp, Jesse."],
        ["Here are your tasks:", "1. [T][ ] keep me"],
        ["Task number is out of range. Stay sharp, Jesse."],
        ["Here are your tasks:", "1. [T][ ] keep me"],
        ["Task number is out of range. Stay sharp, Jesse."],
        ["Here are your tasks:", "1. [T][ ] keep me"],
        ["Task number must be an integer. Stay sharp, Jesse."],
        ["Here are your tasks:", "1. [T][ ] keep me"],
        ["Task number must be an integer. Stay sharp, Jesse."],
        ["Here are your tasks:", "1. [T][ ] keep me"],
        ["Unknown command. Try: todo DESCRIPTION; deadline DESCRIPTION /by yyyy-MM-dd; event DESCRIPTION /at TIME; list; find KEYWORD; mark INDEX; unmark INDEX; delete INDEX; on yyyy-MM-dd; or bye. Stay sharp, Jesse."],
        ["Here are your tasks:", "1. [T][ ] keep me"],
        ["Unknown command. Try: todo DESCRIPTION; deadline DESCRIPTION /by yyyy-MM-dd; event DESCRIPTION /at TIME; list; find KEYWORD; mark INDEX; unmark INDEX; delete INDEX; on yyyy-MM-dd; or bye. Stay sharp, Jesse."],
        ["Here are your tasks:", "1. [T][ ] keep me"],
        ["Unknown command. Try: todo DESCRIPTION; deadline DESCRIPTION /by yyyy-MM-dd; event DESCRIPTION /at TIME; list; find KEYWORD; mark INDEX; unmark INDEX; delete INDEX; on yyyy-MM-dd; or bye. Stay sharp, Jesse."],
        ["Here are your tasks:", "1. [T][ ] keep me"],
        ["All right. The operation is closed. Stay focused, Jesse."]
      ]
    },
    {
      "id": "scheduled-range-and-duplicate-validation",
      "aim": "Verify ISO event range ordering and exact scheduled-item duplicate rejection without over-restricting distinct items or Todos.",
      "inputs": [
        "event Workshop /from 2026-12-20 /to 2026-12-10",
        "event Workshop /from 2026-12-01 /to 2026-12-10",
        "event Release /from 2026-12-10 /to 2026-12-10",
        "event Workshop /from 2026-12-01 /to 2026-12-10",
        "event Lab /from 2026-12-01 /to 2026-12-10",
        "event Workshop /from 2026-12-01 /to 2026-12-11",
        "deadline Submit report /by 2026-12-10",
        "deadline Submit report /by 2026-12-10",
        "deadline Submit report /by 2026-12-11",
        "deadline Other report /by 2026-12-10",
        "event Meeting /at 2pm",
        "event Meeting /at 2pm",
        "event Meeting /at 3pm",
        "event Consultation /at 2pm",
        "todo Read book",
        "todo Read book",
        "list",
        "bye"
      ],
      "expected_outputs": [
        ["Event start must not be after its end. Stay sharp, Jesse."],
        ["Good. That's on the list now:", "[E][ ] Workshop (from: 2026-12-01 to: 2026-12-10)", "The list now contains 1 task."],
        ["Good. That's on the list now:", "[E][ ] Release (from: 2026-12-10 to: 2026-12-10)", "The list now contains 2 tasks."],
        ["That task already exists. Stay sharp, Jesse."],
        ["Good. That's on the list now:", "[E][ ] Lab (from: 2026-12-01 to: 2026-12-10)", "The list now contains 3 tasks."],
        ["Good. That's on the list now:", "[E][ ] Workshop (from: 2026-12-01 to: 2026-12-11)", "The list now contains 4 tasks."],
        ["Good. That's on the list now:", "[D][ ] Submit report (by: Dec 10 2026)", "The list now contains 5 tasks."],
        ["That task already exists. Stay sharp, Jesse."],
        ["Good. That's on the list now:", "[D][ ] Submit report (by: Dec 11 2026)", "The list now contains 6 tasks."],
        ["Good. That's on the list now:", "[D][ ] Other report (by: Dec 10 2026)", "The list now contains 7 tasks."],
        ["Good. That's on the list now:", "[E][ ] Meeting (at: 2pm)", "The list now contains 8 tasks."],
        ["That task already exists. Stay sharp, Jesse."],
        ["Good. That's on the list now:", "[E][ ] Meeting (at: 3pm)", "The list now contains 9 tasks."],
        ["Good. That's on the list now:", "[E][ ] Consultation (at: 2pm)", "The list now contains 10 tasks."],
        ["Good. That's on the list now:", "[T][ ] Read book", "The list now contains 11 tasks."],
        ["That task already exists. Stay sharp, Jesse."],
        [
          "Here are your tasks:",
          "1. [E][ ] Workshop (from: 2026-12-01 to: 2026-12-10)",
          "2. [E][ ] Release (from: 2026-12-10 to: 2026-12-10)",
          "3. [E][ ] Lab (from: 2026-12-01 to: 2026-12-10)",
          "4. [E][ ] Workshop (from: 2026-12-01 to: 2026-12-11)",
          "5. [D][ ] Submit report (by: Dec 10 2026)",
          "6. [D][ ] Submit report (by: Dec 11 2026)",
          "7. [D][ ] Other report (by: Dec 10 2026)",
          "8. [E][ ] Meeting (at: 2pm)",
          "9. [E][ ] Meeting (at: 3pm)",
          "10. [E][ ] Consultation (at: 2pm)",
          "11. [T][ ] Read book"
        ],
        ["All right. The operation is closed. Stay focused, Jesse."]
      ]
    },
    {
      "id": "normalized-duplicates-and-dated-time-ranges",
      "aim": "Verify normalized duplicates include completed todos, event formats remain distinct, and explicit dated times enforce chronological order while allowing overnight events.",
      "inputs": [
        "todo Read book",
        "mark 1",
        "todo read   BOOK",
        "deadline Submit report /by 2026-12-10",
        "deadline submit   REPORT /by 2026-12-10",
        "event Meeting /at Monday 2PM",
        "event meeting /at monday   2pm",
        "event Meeting /from Monday 2PM /to Monday 3PM",
        "event late /from 2026-12-10 4pm /to 2026-12-10 2pm",
        "event overnight /from 2026-12-10 11pm /to 2026-12-11 1am",
        "list",
        "bye"
      ],
      "expected_outputs": [
        ["Good. That's on the list now:", "[T][ ] Read book", "The list now contains 1 task."],
        ["Done. Consider this one handled:", "[T][X] Read book"],
        ["That task already exists. Stay sharp, Jesse."],
        ["Good. That's on the list now:", "[D][ ] Submit report (by: Dec 10 2026)", "The list now contains 2 tasks."],
        ["That task already exists. Stay sharp, Jesse."],
        ["Good. That's on the list now:", "[E][ ] Meeting (at: Monday 2PM)", "The list now contains 3 tasks."],
        ["That task already exists. Stay sharp, Jesse."],
        ["Good. That's on the list now:", "[E][ ] Meeting (from: Monday 2PM to: Monday 3PM)", "The list now contains 4 tasks."],
        ["Event start must not be after its end. Stay sharp, Jesse."],
        ["Good. That's on the list now:", "[E][ ] overnight (from: 2026-12-10 11pm to: 2026-12-11 1am)", "The list now contains 5 tasks."],
        [
          "Here are your tasks:",
          "1. [T][X] Read book",
          "2. [D][ ] Submit report (by: Dec 10 2026)",
          "3. [E][ ] Meeting (at: Monday 2PM)",
          "4. [E][ ] Meeting (from: Monday 2PM to: Monday 3PM)",
          "5. [E][ ] overnight (from: 2026-12-10 11pm to: 2026-12-11 1am)"
        ],
        ["All right. The operation is closed. Stay focused, Jesse."]
      ]
    },
    {
      "id": "place-lifecycle-and-errors",
      "aim": "Verify places can be added, listed, deleted, and rejected safely when syntax or indexes are invalid.",
      "inputs": [
        "places",
        "place Alex's home /at 123 Clementi Ave 3",
        "place NUS Central Library /at 12 Computing Drive",
        "places",
        "place",
        "place Alex's home",
        "place /at 123 Clementi Ave 3",
        "place Alex's home /at",
        "place home /at first /at second",
        "deleteplace abc",
        "deleteplace 999",
        "deleteplace 1",
        "places",
        "bye"
      ],
      "expected_outputs": [
        ["The location list is empty."],
        [
          "Good. I've recorded this location:",
          "Alex's home — 123 Clementi Ave 3",
          "The location list now contains 1 place."
        ],
        [
          "Good. I've recorded this location:",
          "NUS Central Library — 12 Computing Drive",
          "The location list now contains 2 places."
        ],
        [
          "Here are the recorded locations:",
          "1. Alex's home — 123 Clementi Ave 3",
          "2. NUS Central Library — 12 Computing Drive"
        ],
        ["Place name cannot be empty. Stay sharp, Jesse."],
        ["Place requires exactly one /at. Stay sharp, Jesse."],
        ["Place name cannot be empty. Stay sharp, Jesse."],
        ["Place address cannot be empty. Stay sharp, Jesse."],
        ["Place requires exactly one /at. Stay sharp, Jesse."],
        ["Place number must be an integer. Stay sharp, Jesse."],
        ["Place number is out of range. Stay sharp, Jesse."],
        [
          "Done. I've removed this location:",
          "Alex's home — 123 Clementi Ave 3",
          "The location list now contains 1 place."
        ],
        [
          "Here are the recorded locations:",
          "1. NUS Central Library — 12 Computing Drive"
        ],
        ["All right. The operation is closed. Stay focused, Jesse."]
      ]
    }
  ]
}
```
