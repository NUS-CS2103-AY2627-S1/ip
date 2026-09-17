# UI Test Plan

This file is the source of truth for interactive console tests run with the project-specific `test-ui` skill. Add cases here before testing; keep the readable cases and JSON block synchronized.

## Test cases

### TC-001 — Exit the program

- Aim: Verify that `bye` exits cleanly and prints the farewell.
- Input: `bye`
- Expected startup output:

```text
____________________________________________________________
███   ███ ███████  █████  ██     ██
████ ████ ██      ██   ██ ██     ██
██ ███ ██ █████   ██   ██ ██  █  ██
██     ██ ██      ██   ██ ██ ███ ██
██     ██ ███████  █████   ███ ███

Meow! Welcome back. 
Start yapping, I'm all ears!
____________________________________________________________
```

- Expected output after `bye`:

```text
____________________________________________________________
Marvellous yap session. Let's catch up soon meow!
____________________________________________________________
```

### TC-002 — Add and list a task

- Aim: Verify that a todo can be added, appears in the task list, and the session exits cleanly.
- Inputs, in order: `todo read book`, `list`, `bye`
- Expected startup output: Same as TC-001.
- Expected output after `todo read book`:

```text
____________________________________________________________
Got it. I've added this task:
[T][ ] read book
Now you have 1 tasks in the list.
____________________________________________________________
```

- Expected output after `list`:

```text
____________________________________________________________
Here are the tasks in your list:
1. [T][ ] read book
____________________________________________________________
```

- Expected output after `bye`:

```text
____________________________________________________________
Marvellous yap session. Let's catch up soon meow!
____________________________________________________________
```

## Machine-readable plan

The runner resolves `working_directory` relative to this file. Because this plan is in `test/`, `..` refers to the project root.

```json
{
  "version": 1,
  "working_directory": "..",
  "run_command": "java -cp out/production/ip Meow",
  "timeout_seconds": 10,
  "idle_milliseconds": 250,
  "comparison": "exact",
  "test_cases": [
    {
      "id": "TC-001",
      "aim": "Exit the program",
      "expected_startup_output": "____________________________________________________________\n███   ███ ███████  █████  ██     ██\n████ ████ ██      ██   ██ ██     ██\n██ ███ ██ █████   ██   ██ ██  █  ██\n██     ██ ██      ██   ██ ██ ███ ██\n██     ██ ███████  █████   ███ ███\n\nMeow! Welcome back. \nStart yapping, I'm all ears!\n____________________________________________________________\n",
      "steps": [
        {
          "input": "bye",
          "expected_output": "____________________________________________________________\nMarvellous yap session. Let's catch up soon meow!\n____________________________________________________________\n"
        }
      ]
    },
    {
      "id": "TC-002",
      "aim": "Add and list a task",
      "expected_startup_output": "____________________________________________________________\n███   ███ ███████  █████  ██     ██\n████ ████ ██      ██   ██ ██     ██\n██ ███ ██ █████   ██   ██ ██  █  ██\n██     ██ ██      ██   ██ ██ ███ ██\n██     ██ ███████  █████   ███ ███\n\nMeow! Welcome back. \nStart yapping, I'm all ears!\n____________________________________________________________\n",
      "steps": [
        {
          "input": "todo read book",
          "expected_output": "____________________________________________________________\nGot it. I've added this task:\n[T][ ] read book\nNow you have 1 tasks in the list.\n____________________________________________________________\n"
        },
        {
          "input": "list",
          "expected_output": "____________________________________________________________\nHere are the tasks in your list:\n1. [T][ ] read book\n____________________________________________________________\n"
        },
        {
          "input": "bye",
          "expected_output": "____________________________________________________________\nMarvellous yap session. Let's catch up soon meow!\n____________________________________________________________\n"
        }
      ]
    }
  ]
}
```
