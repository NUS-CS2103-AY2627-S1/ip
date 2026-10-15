# Command-Line UI Test Plan

Run these tests after each application code change using the project-local `test-ui` skill. Expected values are meaningful output fragments checked in order; startup animation frames are intentionally excluded because they do not affect command behavior.

## Coverage

- Create and list all three task types with parsed, consistently formatted dates.
- Preserve `LocalDate` details while marking and unmarking through `Task` polymorphism.
- Find task descriptions by case-insensitive substring while preserving their original task numbers.
- Accept valid ISO, day/month/year, and English text dates, including leap days, while rejecting invalid dates.
- Reject events that end before they start while allowing same-day events.
- Find deadlines and in-progress events on a date while preserving their original task numbers.
- Edit descriptions and type-specific dates while preserving task status and untouched details.
- Reject incompatible edit fields and invalid replacement dates without changing stored tasks.
- Reject empty and unknown commands with actionable feedback.
- Accept harmless whitespace and command capitalization while rejecting repeated or reordered parameters.
- Reject exact duplicate tasks even when the existing task has a different completion status.
- Preserve malformed data files rather than overwriting them after a failed load.
- Display concise guidance for every supported command without changing saved tasks.
- Reject incomplete task commands and invalid task numbers without changing stored tasks.
- Delete tasks from collection storage and renumber the remaining list.
- Reject invalid delete requests without changing stored tasks.
- Create missing storage automatically, save canonical dates on every mutation, and restore formatted dates and statuses.
- Replace saved data atomically without leaving temporary files after success or failure.
- Run each automated case in isolated temporary storage so saved tasks cannot leak between cases.

## JavaFX GUI checks

Run `./gradlew run` and verify the graphical interface manually:

1. The welcome dialog appears with Green Chonk's transparent-background mascot in a resizable window, and the
   command field receives keyboard focus.
2. The welcome, task confirmations, help, errors, and farewell consistently use Green Chonk's bear-and-pear
   identity without referring to the placeholder name `Duke`.
3. Pressing Enter and clicking **CHOMP** both add the user command and Green Chonk's response.
4. A blank or invalid command appears as a visually distinct error response, and the header status changes to
   `NEEDS A RETRY` without crashing the app.
5. A successful command restores the header status to `READY`; `bye` changes it to `ROLLING OFF` before exit.
6. A multi-line response keeps each fixed-size avatar aligned with the bottom edge of its message bubble.
7. Resizing the window changes the available bubble width without stretching avatars or clipping text.
8. Repeated commands keep the newest dialog visible by scrolling the conversation automatically.
9. `bye` displays the farewell response and then closes the window.
10. Restarting the GUI restores tasks created in the previous session.
11. Starting the GUI with `./gradlew run` does not print a restricted native-access warning.

The automated cases below continue through the command-line interface because they exercise the same parser,
commands, task list, and storage without depending on a display server.

## Persistence check

In a temporary working directory, run Green Chonk twice against the same `data/greenchonk.txt`:

1. Add a todo, deadline, and event; mark the deadline; delete the todo; then exit.
2. Start Green Chonk again and run `list`.

The second session must restore the marked deadline and event, including their parsed dates, while the deleted todo must remain absent. The data file must contain ISO dates even though the UI displays friendly dates. The test directory and data file must not exist before the first session; this verifies first-run creation as well as loading. No temporary save file should remain beside `greenchonk.txt`.

Also seed the data file with an event whose ending date is before its starting date. On startup, Green Chonk must identify that line as invalid rather than loading the impossible event.

## Automated cases

```json
{
  "main_class": "greenchonk.GreenChonk",
  "cases": [
    {
      "name": "reject-duplicate-edits",
      "aim": "Reject an edit that duplicates another task, even with a different status, but allow unchanged details.",
      "inputs": ["todo read book", "todo buy milk", "mark 2", "edit 1 /description buy milk", "edit 1 /description read book", "list", "bye"],
      "expected": [
        "Green Chonk is already carrying a task with those details.",
        "Green Chonk updated this task:",
        "1.[T][ ] read book\n2.[T][X] buy milk"
      ]
    },
    {
      "name": "show-command-help",
      "aim": "Verify help lists every command and the required date format without changing the task list.",
      "inputs": [
        "help",
        "list",
        "bye"
      ],
      "expected": [
        "Here are the commands Green Chonk understands:\n  todo DESCRIPTION\n  deadline DESCRIPTION /by DATE\n  event DESCRIPTION /from START_DATE /to END_DATE\n  list\n  find KEYWORD\n  schedule DATE\n  edit TASK_NUMBER /description DESCRIPTION\n  edit TASK_NUMBER /by DATE\n  edit TASK_NUMBER /from START_DATE\n  edit TASK_NUMBER /to END_DATE\n  mark TASK_NUMBER\n  unmark TASK_NUMBER\n  delete TASK_NUMBER\n  help\n  bye\nDates: yyyy-MM-dd, d/M/yyyy, or d MMM yyyy (e.g., 2026-08-28, 28/8/2026, 28 Aug 2026).",
        "Green Chonk is not carrying any tasks yet."
      ]
    },
    {
      "name": "handle-command-edge-cases",
      "aim": "Accept harmless spacing and capitalization while rejecting duplicates and ambiguous scheduled-task parameters.",
      "inputs": [
        "  ToDo\t  spaced task  ",
        "DEADLINE review/bylaws   /by   2026-08-28",
        "mark 1",
        "todo spaced task",
        "deadline report /by 2026-08-28 /by 2026-08-29",
        "event meeting /to 2026-08-29 /from 2026-08-28",
        "event meeting /from 2026-08-28 /to 2026-08-29 /to 2026-08-30",
        "list",
        "bye"
      ],
      "expected": [
        "Chomped and packed this task:\n  [T][ ] spaced task\nGreen Chonk is now carrying 1 task.",
        "Chomped and packed this task:\n  [D][ ] review/bylaws (by: Aug 28 2026)\nGreen Chonk is now carrying 2 tasks.",
        "High paw! Green Chonk marked this task as done:\n  [T][X] spaced task",
        "Green Chonk is already carrying a task with those details.",
        "A deadline accepts /by only once.",
        "Put /from before /to in an event command.",
        "An event accepts /to only once.",
        "Here are the tasks Green Chonk is carrying:\n1.[T][X] spaced task\n2.[D][ ] review/bylaws (by: Aug 28 2026)"
      ]
    },
    {
      "name": "edit-task-details",
      "aim": "Verify each supported edit changes only one detail while preserving the task type, completion status, and other details.",
      "inputs": [
        "todo buy milk",
        "deadline submit draft /by 2026-08-28",
        "event conference /from 2026-08-29 /to 2026-08-30",
        "mark 2",
        "edit 1 /description buy oat milk",
        "edit 2 /description submit final report",
        "edit 2 /by 30/8/2026",
        "edit 3 /from 28 Aug 2026",
        "edit 3 /to 2026-09-01",
        "list",
        "bye"
      ],
      "expected": [
        "Green Chonk updated this task:\n  Before: [T][ ] buy milk\n  After:  [T][ ] buy oat milk",
        "Green Chonk updated this task:\n  Before: [D][X] submit draft (by: Aug 28 2026)\n  After:  [D][X] submit final report (by: Aug 28 2026)",
        "Green Chonk updated this task:\n  Before: [D][X] submit final report (by: Aug 28 2026)\n  After:  [D][X] submit final report (by: Aug 30 2026)",
        "Green Chonk updated this task:\n  Before: [E][ ] conference (from: Aug 29 2026 to: Aug 30 2026)\n  After:  [E][ ] conference (from: Aug 28 2026 to: Aug 30 2026)",
        "Green Chonk updated this task:\n  Before: [E][ ] conference (from: Aug 28 2026 to: Aug 30 2026)\n  After:  [E][ ] conference (from: Aug 28 2026 to: Sep 01 2026)",
        "Here are the tasks Green Chonk is carrying:\n1.[T][ ] buy oat milk\n2.[D][X] submit final report (by: Aug 30 2026)\n3.[E][ ] conference (from: Aug 28 2026 to: Sep 01 2026)"
      ]
    },
    {
      "name": "reject-invalid-edits",
      "aim": "Verify malformed edits, incompatible fields, invalid dates, and impossible event ranges leave the task list unchanged.",
      "inputs": [
        "todo unchanged todo",
        "deadline unchanged deadline /by 2026-08-28",
        "event unchanged event /from 2026-08-29 /to 2026-08-30",
        "edit",
        "edit one /description replacement",
        "edit 1",
        "edit 1 /description",
        "edit 1 /when tomorrow",
        "edit 1 /by 2026-08-30",
        "edit 2 /to 2026-08-30",
        "edit 3 /from tomorrow",
        "edit 3 /from 2026-08-31",
        "edit 3 /to 2026-08-28",
        "edit 4 /description missing",
        "list",
        "bye"
      ],
      "expected": [
        "An edit command needs a task number and field. Try: edit 1 /description buy milk",
        "\"one\" is not a valid task number. Use a whole number such as 1.",
        "Please choose a field to edit. Use /description, /by, /from, or /to.",
        "Please provide a new description. Try: edit 1 /description buy milk",
        "I don't recognize the edit field \"/when\". Use /description, /by, /from, or /to.",
        "Task 1 is not a deadline. Use /by only with a deadline.",
        "Task 2 is not an event. Use /from and /to only with an event.",
        "The new event start date must use yyyy-MM-dd, d/M/yyyy, or d MMM yyyy and be valid. Try: 2026-08-28",
        "An event's start date cannot be after its end date. Try /from 2026-08-30 or earlier.",
        "An event's end date cannot be before its start date. Try /to 2026-08-29 or later.",
        "Task 4 does not exist. Choose a number from 1 to 3.",
        "Here are the tasks Green Chonk is carrying:\n1.[T][ ] unchanged todo\n2.[D][ ] unchanged deadline (by: Aug 28 2026)\n3.[E][ ] unchanged event (from: Aug 29 2026 to: Aug 30 2026)"
      ]
    },
    {
      "name": "add-and-list-task-types",
      "aim": "Verify todos, deadlines, and events are constructed and displayed with the correct type icons and details.",
      "inputs": [
        "todo borrow book",
        "deadline return book /by 2026-08-30",
        "event project meeting /from 2026-08-31 /to 2026-09-01",
        "list",
        "bye"
      ],
      "expected": [
        "Chomped and packed this task:\n  [T][ ] borrow book\nGreen Chonk is now carrying 1 task.",
        "Chomped and packed this task:\n  [D][ ] return book (by: Aug 30 2026)\nGreen Chonk is now carrying 2 tasks.",
        "Chomped and packed this task:\n  [E][ ] project meeting (from: Aug 31 2026 to: Sep 01 2026)\nGreen Chonk is now carrying 3 tasks.",
        "Here are the tasks Green Chonk is carrying:\n1.[T][ ] borrow book\n2.[D][ ] return book (by: Aug 30 2026)\n3.[E][ ] project meeting (from: Aug 31 2026 to: Sep 01 2026)"
      ]
    },
    {
      "name": "mark-and-unmark-subtype",
      "aim": "Verify completion changes operate polymorphically without losing deadline details.",
      "inputs": [
        "deadline submit report /by 2026-08-28",
        "mark 1",
        "list",
        "unmark 1",
        "list",
        "bye"
      ],
      "expected": [
        "[D][ ] submit report (by: Aug 28 2026)",
        "High paw! Green Chonk marked this task as done:\n  [D][X] submit report (by: Aug 28 2026)",
        "Here are the tasks Green Chonk is carrying:\n1.[D][X] submit report (by: Aug 28 2026)",
        "OK, Green Chonk marked this task as not done yet:\n  [D][ ] submit report (by: Aug 28 2026)",
        "Here are the tasks Green Chonk is carrying:\n1.[D][ ] submit report (by: Aug 28 2026)"
      ]
    },
    {
      "name": "find-tasks-by-keyword",
      "aim": "Verify find performs case-insensitive substring matching, preserves original task numbers, reports no matches, and rejects an empty keyword.",
      "inputs": [
        "todo buy milk",
        "todo Read Book",
        "deadline return book /by 2026-08-30",
        "todo update handbook",
        "find BOOK",
        "find hand",
        "find missing",
        "find",
        "bye"
      ],
      "expected": [
        "Here are the matching tasks in your list:\n2.[T][ ] Read Book\n3.[D][ ] return book (by: Aug 30 2026)\n4.[T][ ] update handbook",
        "Here are the matching tasks in your list:\n4.[T][ ] update handbook",
        "Green Chonk found no matching tasks.",
        "A find command needs a keyword. Try: find book"
      ]
    },
    {
      "name": "parse-and-format-calendar-dates",
      "aim": "Verify each supported date format, including a leap day and mixed-case month, produces a friendly display.",
      "inputs": [
        "deadline do homework /by 29/2/2028",
        "event orientation /from 31 dEc 2026 /to 1 Jan 2027",
        "schedule 31/12/2026",
        "list",
        "bye"
      ],
      "expected": [
        "[D][ ] do homework (by: Feb 29 2028)",
        "[E][ ] orientation (from: Dec 31 2026 to: Jan 01 2027)",
        "Here are the tasks scheduled for 2026-12-31:\n2.[E][ ] orientation (from: Dec 31 2026 to: Jan 01 2027)",
        "Here are the tasks Green Chonk is carrying:\n1.[D][ ] do homework (by: Feb 29 2028)\n2.[E][ ] orientation (from: Dec 31 2026 to: Jan 01 2027)"
      ]
    },
    {
      "name": "reject-empty-and-unknown-commands",
      "aim": "Verify empty and unknown inputs report specific errors and leave the task list unchanged.",
      "inputs": [
        "",
        "todo",
        "roll away",
        "list",
        "bye"
      ],
      "expected": [
        "Oops! Green Chonk couldn't chomp that:\n  Please enter a command. Try: todo buy milk",
        "Oops! Green Chonk couldn't chomp that:\n  A todo needs a description. Try: todo buy milk",
        "Oops! Green Chonk couldn't chomp that:\n  I don't recognize \"roll away\". Try todo, deadline, event, list, find, schedule, edit, mark, unmark, delete, help, or bye.",
        "Green Chonk is not carrying any tasks yet."
      ]
    },
    {
      "name": "reject-incomplete-scheduled-tasks",
      "aim": "Verify each missing deadline or event field is explained and invalid tasks are not stored.",
      "inputs": [
        "todo valid task",
        "deadline submit report",
        "deadline /by 2026-08-28",
        "deadline submit report /by",
        "event meeting /to 2026-08-29",
        "event /from 2026-08-28 /to 2026-08-29",
        "event meeting /from 2026-08-28",
        "event meeting /from /to 2026-08-29",
        "event meeting /from 2026-08-28 /to",
        "list",
        "bye"
      ],
      "expected": [
        "[T][ ] valid task",
        "A deadline needs /by followed by a date. Try: deadline submit report /by 2026-08-28",
        "A deadline needs a description before /by.",
        "A deadline needs a date after /by.",
        "An event needs /from and /to. Try: event meeting /from 2026-08-28 /to 2026-08-29",
        "An event needs a description before /from.",
        "An event needs /to followed by an ending date.",
        "An event needs a starting date after /from.",
        "An event needs an ending date after /to.",
        "Here are the tasks Green Chonk is carrying:\n1.[T][ ] valid task"
      ]
    },
    {
      "name": "reject-invalid-calendar-dates",
      "aim": "Verify invalid or wrongly formatted deadline and event dates are rejected without changing the task list.",
      "inputs": [
        "todo valid task",
        "deadline impossible date /by 2026-02-29",
        "deadline wrong format /by 29-02-2028",
        "event bad start /from 2026-13-01 /to 2026-08-29",
        "event bad end /from 2026-08-28 /to tomorrow",
        "list",
        "bye"
      ],
      "expected": [
        "[T][ ] valid task",
        "The deadline date must use yyyy-MM-dd, d/M/yyyy, or d MMM yyyy and be valid. Try: 2026-08-28",
        "The deadline date must use yyyy-MM-dd, d/M/yyyy, or d MMM yyyy and be valid. Try: 2026-08-28",
        "The event start date must use yyyy-MM-dd, d/M/yyyy, or d MMM yyyy and be valid. Try: 2026-08-28",
        "The event end date must use yyyy-MM-dd, d/M/yyyy, or d MMM yyyy and be valid. Try: 2026-08-29",
        "Here are the tasks Green Chonk is carrying:\n1.[T][ ] valid task"
      ]
    },
    {
      "name": "validate-event-date-range",
      "aim": "Verify an event cannot end before it starts while equal dates remain valid for a same-day event.",
      "inputs": [
        "event backwards /from 2026-12-29 /to 2023-03-24",
        "event same-day /from 2026-08-28 /to 2026-08-28",
        "list",
        "bye"
      ],
      "expected": [
        "An event's end date cannot be before its start date. Try /to 2026-12-29 or later.",
        "Chomped and packed this task:\n  [E][ ] same-day (from: Aug 28 2026 to: Aug 28 2026)\nGreen Chonk is now carrying 1 task.",
        "Here are the tasks Green Chonk is carrying:\n1.[E][ ] same-day (from: Aug 28 2026 to: Aug 28 2026)"
      ]
    },
    {
      "name": "show-tasks-scheduled-on-date",
      "aim": "Verify schedule finds matching deadlines and inclusive event dates, excludes todos, preserves task numbers, and handles empty or invalid queries.",
      "inputs": [
        "todo undated task",
        "deadline submit report /by 2026-08-28",
        "event conference /from 2026-08-27 /to 2026-08-29",
        "schedule 2026-08-28",
        "schedule 2026-08-30",
        "schedule",
        "schedule next Friday",
        "bye"
      ],
      "expected": [
        "Here are the tasks scheduled for 2026-08-28:\n2.[D][ ] submit report (by: Aug 28 2026)\n3.[E][ ] conference (from: Aug 27 2026 to: Aug 29 2026)",
        "Green Chonk has no deadlines or events scheduled for 2026-08-30.",
        "Please provide a schedule date. Try: schedule 2026-08-28",
        "The schedule date must use yyyy-MM-dd, d/M/yyyy, or d MMM yyyy and be valid. Try: 2026-08-28"
      ]
    },
    {
      "name": "reject-invalid-task-numbers",
      "aim": "Verify mark and unmark reject missing, non-numeric, and out-of-range task numbers without changing task state.",
      "inputs": [
        "mark 1",
        "unmark",
        "todo keep me incomplete",
        "mark zero",
        "mark 0",
        "mark 2",
        "unmark 2",
        "list",
        "bye"
      ],
      "expected": [
        "There are no tasks to mark yet.",
        "Please provide a task number. Try: unmark 1",
        "[T][ ] keep me incomplete",
        "\"zero\" is not a valid task number. Use a whole number such as 1.",
        "Task 0 does not exist. Choose a number from 1 to 1.",
        "Task 2 does not exist. Choose a number from 1 to 1.",
        "Task 2 does not exist. Choose a number from 1 to 1.",
        "Here are the tasks Green Chonk is carrying:\n1.[T][ ] keep me incomplete"
      ]
    },
    {
      "name": "delete-and-renumber-tasks",
      "aim": "Verify deleting middle, first, and last tasks removes the correct object and keeps numbering contiguous.",
      "inputs": [
        "todo first task",
        "deadline second task /by 2026-08-28",
        "event third task /from 2026-08-29 /to 2026-08-30",
        "mark 2",
        "delete 2",
        "list",
        "delete 1",
        "delete 1",
        "list",
        "bye"
      ],
      "expected": [
        "Noted. Green Chonk removed this task:\n  [D][X] second task (by: Aug 28 2026)\nGreen Chonk is now carrying 2 tasks.",
        "Here are the tasks Green Chonk is carrying:\n1.[T][ ] first task\n2.[E][ ] third task (from: Aug 29 2026 to: Aug 30 2026)",
        "Noted. Green Chonk removed this task:\n  [T][ ] first task\nGreen Chonk is now carrying 1 task.",
        "Noted. Green Chonk removed this task:\n  [E][ ] third task (from: Aug 29 2026 to: Aug 30 2026)\nGreen Chonk is now carrying 0 tasks.",
        "Green Chonk is not carrying any tasks yet."
      ]
    },
    {
      "name": "reject-invalid-delete-requests",
      "aim": "Verify delete rejects missing, non-numeric, and out-of-range task numbers without removing a task.",
      "inputs": [
        "delete 1",
        "delete",
        "todo keep this task",
        "delete zero",
        "delete 0",
        "delete -1",
        "delete 2",
        "list",
        "bye"
      ],
      "expected": [
        "There are no tasks to delete yet.",
        "Please provide a task number. Try: delete 1",
        "[T][ ] keep this task",
        "\"zero\" is not a valid task number. Use a whole number such as 1.",
        "Task 0 does not exist. Choose a number from 1 to 1.",
        "Task -1 does not exist. Choose a number from 1 to 1.",
        "Task 2 does not exist. Choose a number from 1 to 1.",
        "Here are the tasks Green Chonk is carrying:\n1.[T][ ] keep this task"
      ]
    }
  ]
}
```
