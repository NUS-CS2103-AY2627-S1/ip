# PotatoBot UI Test Plan

This plan is consumed by `skills/test-ui/scripts/run_ui_tests.py`. Each test case starts with an empty task list. Steps within a test case share state and are checked in order.

The runner requires Java 25, builds before testing, compares each PotatoBot reply exactly after normalizing line endings and surrounding whitespace, prints the console input/output record, and stops at the first failure. A test case can provide its starting save-file content through `initial_save_file`.

```json
{
  "build_command": ["./gradlew", "test"],
  "run_command": ["java", "-cp", "build/classes/java/main", "potatobot.ui.cli.CliLauncher"],
  "test_cases": [
    {
      "id": "UI-001",
      "aim": "Add each supported typed task, mark one task, and display the stored details.",
      "steps": [
        {
          "input": "todo read book",
          "expected_output": "PotatoBot:\n  Added: read book (Todo)"
        },
        {
          "input": "deadline return book /by 2019-12-02",
          "expected_output": "PotatoBot:\n  Added: return book (Deadline, by: Dec 02 2019)"
        },
        {
          "input": "event project meeting /from 2019-12-02 /to 2019-12-03",
          "expected_output": "PotatoBot:\n  Added: project meeting (Event, from: Dec 02 2019 to: Dec 03 2019)"
        },
        {
          "input": "mark 1",
          "expected_output": "PotatoBot:\n  Task completed: read book (Todo)\n  Keep going!"
        },
        {
          "input": "list",
          "expected_output": "PotatoBot:\n  Here are the tasks in your potato sack:\n    1.[X] read book (Todo)\n    2.[ ] return book (Deadline, by: Dec 02 2019)\n    3.[ ] project meeting (Event, from: Dec 02 2019 to: Dec 03 2019)"
        }
      ]
    },
    {
      "id": "UI-002",
      "aim": "Reject a non-integer task number without terminating PotatoBot.",
      "steps": [
        {
          "input": "add buy potatoes",
          "expected_output": "PotatoBot:\n  Added: buy potatoes"
        },
        {
          "input": "mark 24.2F",
          "expected_output": "PotatoBot:\n  Do you hear yourself?? Task number must be a whole number."
        },
        {
          "input": "list",
          "expected_output": "PotatoBot:\n  Here are the tasks in your potato sack:\n    1.[ ] buy potatoes"
        }
      ]
    },
    {
      "id": "UI-003",
      "aim": "Delete a task by number and remove it from the displayed list.",
      "steps": [
        {
          "input": "add buy potatoes",
          "expected_output": "PotatoBot:\n  Added: buy potatoes"
        },
        {
          "input": "todo read book",
          "expected_output": "PotatoBot:\n  Added: read book (Todo)"
        },
        {
          "input": "delete 1",
          "expected_output": "PotatoBot:\n  Task deleted: buy potatoes\n  Keep it going!"
        },
        {
          "input": "list",
          "expected_output": "PotatoBot:\n  Here are the tasks in your potato sack:\n    1.[ ] read book (Todo)"
        }
      ]
    },
    {
      "id": "UI-004",
      "aim": "Save the current task list before exiting PotatoBot.",
      "steps": [
        {
          "input": "add buy potatoes",
          "expected_output": "PotatoBot:\n  Added: buy potatoes"
        },
        {
          "input": "bye",
          "expected_output": "PotatoBot:\n  Bye. I'm rolling back to the potato patch.\n\n================================================================================\nBye. I'm rolling back to the potato patch. Hope to see you again soon!\n================================================================================"
        }
      ]
    },
    {
      "id": "UI-005",
      "aim": "Load saved tasks and restore their types and completion states on startup.",
      "initial_save_file": "[X] read book (Todo)\n[ ] return book (Deadline, by: Dec 02 2019)\n[ ] project meeting (Event, from: Dec 02 2019 to: Dec 03 2019)",
      "steps": [
        {
          "input": "list",
          "expected_output": "PotatoBot:\n  Here are the tasks in your potato sack:\n    1.[X] read book (Todo)\n    2.[ ] return book (Deadline, by: Dec 02 2019)\n    3.[ ] project meeting (Event, from: Dec 02 2019 to: Dec 03 2019)"
        }
      ]
    },
    {
      "id": "UI-006",
      "aim": "Reject invalid event dates and an end date before the start date.",
      "steps": [
        {
          "input": "event project meeting /from 2019-13-02 /to 2019-12-03",
          "expected_output": "PotatoBot:\n  Use the date format YYYY-MM-DD, for example 2019-12-02."
        },
        {
          "input": "event project meeting /from 2019-12-03 /to 2019-12-02",
          "expected_output": "PotatoBot:\n  The event end date cannot be before its start date."
        }
      ]
    },
    {
      "id": "UI-007",
      "aim": "Reject an invalid deadline date.",
      "steps": [
        {
          "input": "deadline return book /by 2019-13-02",
          "expected_output": "PotatoBot:\n  Use the date format YYYY-MM-DD, for example 2019-12-02."
        }
      ]
    },
    {
      "id": "UI-008",
      "aim": "Undo five state-changing commands in reverse order and reject a sixth undo.",
      "steps": [
        {
          "input": "add first task",
          "expected_output": "PotatoBot:\n  Added: first task"
        },
        {
          "input": "todo second task",
          "expected_output": "PotatoBot:\n  Added: second task (Todo)"
        },
        {
          "input": "mark 1",
          "expected_output": "PotatoBot:\n  Task completed: first task\n  Keep going!"
        },
        {
          "input": "unmark 1",
          "expected_output": "PotatoBot:\n  Task Reset: first task\n  Keep going!"
        },
        {
          "input": "delete 2",
          "expected_output": "PotatoBot:\n  Task deleted: second task (Todo)\n  Keep it going!"
        },
        {
          "input": "undo",
          "expected_output": "PotatoBot:\n  PototaBot has travelled back in time: delete 2 undone"
        },
        {
          "input": "undo",
          "expected_output": "PotatoBot:\n  PototaBot has travelled back in time: unmark 1 undone"
        },
        {
          "input": "undo",
          "expected_output": "PotatoBot:\n  PototaBot has travelled back in time: mark 1 undone"
        },
        {
          "input": "undo",
          "expected_output": "PotatoBot:\n  PototaBot has travelled back in time: todo second task undone"
        },
        {
          "input": "undo",
          "expected_output": "PotatoBot:\n  PototaBot has travelled back in time: add first task undone"
        },
        {
          "input": "undo",
          "expected_output": "PotatoBot:\n  PotatoBot can't travel THAT far back in time sorry :("
        },
        {
          "input": "list",
          "expected_output": "PotatoBot:\n  Nothing to see here..."
        }
      ]
    }
  ]
}
```
