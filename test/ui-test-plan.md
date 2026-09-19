# UI Test Plan

Release packaging check: run `python test/run_jar_smoke.py build/libs/gary.jar`
with a plain Java 25.0.3 JDK on Windows x64, Intel macOS, and Apple Silicon macOS.
The test launches the actual JAR in fresh working folders, adds and lists a task,
checks persistence, and verifies that `bye` and `BYE` close the window.
Expected markers: `SMOKE_GUI_PASS`, `SMOKE_PROCESS_EXITED_SUCCESSFULLY`, and
`All release JAR smoke tests passed`. Stop and report any failed launch or timeout.

The entry point is `gary.Gary`. Each test case runs in a fresh process using Java 25. `Inputs` and `Expected outputs` are parallel JSON lists with one expected response per command. An expected response can be a string or a list of output lines. Test cases are isolated unless they specify the same `Storage group`, in which case their processes share one temporary working directory.

The runner compares the response printed between the two standard divider lines after each command. Line endings and surrounding blank lines are normalized; response text and internal whitespace are otherwise compared exactly. Startup output and all divider lines remain visible in the console-session record.

The JavaFX interface uses the same command-response engine as the terminal interface. After GUI changes, also launch `gary.gui.Launcher`, enter `help`, `todo read book`, `list`, and `blah`, and verify that each command appears as a compact right-aligned command chip without a user avatar. Verify that the supplied Gary picture appears in the header and beside Gary's left-aligned responses, the supplied jellyfish scene fills the conversation background beneath a readable tint, the subtitle reads `Your sea-snail task companion`, and Gary uses `Meow` only in his greeting, missing-input guidance, unknown-command response, and farewell. Verify that routine successful commands do not contain `Meow`, the help guide has a dash-prefixed command list and monospaced text, the unknown command uses the coral warning treatment, and the farewell uses its distinct treatment. For contact changes, repeat the UI-05A commands through the GUI and verify the title identifies Gary as a task and contact assistant. Verify both the Send button and Enter key submit commands and the conversation scrolls to the latest dialog. Keep the input field focused and use the mouse wheel over the input bar; verify the conversation scrolls without requiring a click in the conversation. Resize the window to its minimum size and then wider than its initial size; verify messages wrap without horizontal scrolling, the background continues to cover the conversation area, the input field expands, the Send button remains usable, and the compact header and input bar remain visible. Finally, enter `bye` and verify the controls become disabled, the farewell appears briefly, and the window closes automatically. Relaunch the GUI, enter uppercase `BYE`, and verify it follows the same shutdown behavior.

## UI-01: Task lifecycle

**Aim:** Verify typed tasks can be added, marked, listed, deleted, and listed again while preserving their details and status.

### Inputs

```json
[
  "todo read book",
  "deadline return book /by 2019-12-02",
  "event project meeting /from 2019-08-06 /to 2019-08-07",
  "mark 2",
  "list",
  "delete 1",
  "list",
  "bye"
]
```

### Expected outputs

```json
[
  [
    "Got it. I've added this task:",
    "  [T][ ] read book",
    "Now you have 1 tasks in the list."
  ],
  [
    "Got it. I've added this task:",
    "  [D][ ] return book (by: 2019-12-02)",
    "Now you have 2 tasks in the list."
  ],
  [
    "Got it. I've added this task:",
    "  [E][ ] project meeting (from: 2019-08-06 to: 2019-08-07)",
    "Now you have 3 tasks in the list."
  ],
  [
    "Nice! I've marked this task as done:",
    "  [D][X] return book (by: 2019-12-02)"
  ],
  [
    "Here are the tasks in your list:",
    "1.[T][ ] read book",
    "2.[D][X] return book (by: 2019-12-02)",
    "3.[E][ ] project meeting (from: 2019-08-06 to: 2019-08-07)"
  ],
  [
    "Noted. I've removed this task:",
    "  [T][ ] read book",
    "Now you have 2 tasks in the list."
  ],
  [
    "Here are the tasks in your list:",
    "1.[D][X] return book (by: 2019-12-02)",
    "2.[E][ ] project meeting (from: 2019-08-06 to: 2019-08-07)"
  ],
  "Meow! Sea you again soon!"
]
```

## UI-02: Invalid input

**Aim:** Verify incomplete, out-of-range, and unknown commands report errors without terminating the session.

### Inputs

```json
[
  "todo",
  "deadline submit report /by tomorrow",
  "event meeting /from 2019-12-02 /to Tuesday",
  "delete 1",
  "blah",
  "bye"
]
```

### Expected outputs

```json
[
  "Meow? The description of a todo cannot be empty.",
  "Error: The deadline date must be in YYYY-MM-DD format",
  "Error: The event dates must be in YYYY-MM-DD format",
  "Error: The task number is invalid",
  "Meow... I don't recognize that command. Type help to see the available commands.",
  "Meow! Sea you again soon!"
]
```

## UI-03A: Save tasks

**Aim:** Verify todo, deadline, and event tasks and their completion status are saved before the chatbot exits.

**Storage group:** level-7-persistence

### Inputs

```json
[
  "todo read book",
  "deadline return book /by 2019-12-02",
  "event project meeting /from 2019-08-06 /to 2019-08-07",
  "mark 2",
  "bye"
]
```

### Expected outputs

```json
[
  [
    "Got it. I've added this task:",
    "  [T][ ] read book",
    "Now you have 1 tasks in the list."
  ],
  [
    "Got it. I've added this task:",
    "  [D][ ] return book (by: 2019-12-02)",
    "Now you have 2 tasks in the list."
  ],
  [
    "Got it. I've added this task:",
    "  [E][ ] project meeting (from: 2019-08-06 to: 2019-08-07)",
    "Now you have 3 tasks in the list."
  ],
  [
    "Nice! I've marked this task as done:",
    "  [D][X] return book (by: 2019-12-02)"
  ],
  "Meow! Sea you again soon!"
]
```

## UI-03B: Load tasks

**Aim:** Verify a new chatbot process reloads the previously saved task types, details, and completion status.

**Storage group:** level-7-persistence

### Inputs

```json
[
  "list",
  "delete 1",
  "bye"
]
```

### Expected outputs

```json
[
  [
    "Here are the tasks in your list:",
    "1.[T][ ] read book",
    "2.[D][X] return book (by: 2019-12-02)",
    "3.[E][ ] project meeting (from: 2019-08-06 to: 2019-08-07)"
  ],
  [
    "Noted. I've removed this task:",
    "  [T][ ] read book",
    "Now you have 2 tasks in the list."
  ],
  "Meow! Sea you again soon!"
]
```

## UI-04: Find tasks

**Aim:** Verify find returns only descriptions containing the keyword, ignores case, preserves task numbers, and rejects an empty keyword.

### Inputs

```json
[
  "todo read book",
  "deadline return book /by 2019-12-02",
  "todo buy bread",
  "find BOOK",
  "find",
  "bye"
]
```

### Expected outputs

```json
[
  [
    "Got it. I've added this task:",
    "  [T][ ] read book",
    "Now you have 1 tasks in the list."
  ],
  [
    "Got it. I've added this task:",
    "  [D][ ] return book (by: 2019-12-02)",
    "Now you have 2 tasks in the list."
  ],
  [
    "Got it. I've added this task:",
    "  [T][ ] buy bread",
    "Now you have 3 tasks in the list."
  ],
  [
    "Here are the matching tasks in your list:",
    "1.[T][ ] read book",
    "2.[D][ ] return book (by: 2019-12-02)"
  ],
  "Meow? The keyword for a find cannot be empty.",
  "Meow! Sea you again soon!"
]
```

## UI-05A: Manage contacts

**Aim:** Verify contacts can be added, listed, searched, deleted, and saved with all details intact.

**Storage group:** contact-persistence

### Inputs

```json
[
  "contact add Alice Tan /phone 91234567 /email alice@example.com",
  "contact add Bob Lim /phone 87654321 /email bob@example.com",
  "contact list",
  "contact find ALICE",
  "contact delete 2",
  "contact add Alice /phone /email alice@example.com",
  "contact find",
  "contact delete 2",
  "contact rename Alice",
  "bye"
]
```

### Expected outputs

```json
[
  [
    "Got it. I've added this contact:",
    "  Alice Tan (Phone: 91234567, Email: alice@example.com)",
    "Now you have 1 contact."
  ],
  [
    "Got it. I've added this contact:",
    "  Bob Lim (Phone: 87654321, Email: bob@example.com)",
    "Now you have 2 contacts."
  ],
  [
    "Here are your contacts:",
    "1. Alice Tan (Phone: 91234567, Email: alice@example.com)",
    "2. Bob Lim (Phone: 87654321, Email: bob@example.com)"
  ],
  [
    "Here are the matching contacts:",
    "1. Alice Tan (Phone: 91234567, Email: alice@example.com)"
  ],
  [
    "Noted. I've removed this contact:",
    "  Bob Lim (Phone: 87654321, Email: bob@example.com)",
    "Now you have 1 contact."
  ],
  "Meow? Use: contact add NAME /phone PHONE /email EMAIL.",
  "Meow? The contact keyword cannot be empty.",
  "Error: The contact number is invalid",
  "Meow... I don't recognize that contact command. Type help to see the available commands.",
  "Meow! Sea you again soon!"
]
```

## UI-05B: Reload contacts

**Aim:** Verify contact changes are loaded by a later chatbot process.

**Storage group:** contact-persistence

### Inputs

```json
[
  "contact list",
  "bye"
]
```

### Expected outputs

```json
[
  [
    "Here are your contacts:",
    "1. Alice Tan (Phone: 91234567, Email: alice@example.com)"
  ],
  "Meow! Sea you again soon!"
]
```

## UI-06: Help command

**Aim:** Verify help lists every supported task, contact, and exit command, and rejects unexpected arguments.

### Inputs

```json
[
  "help",
  "help extra",
  "bye"
]
```

### Expected outputs

```json
[
  [
    "Here are the available commands:",
    "  - list : Views all tasks",
    "  - todo <description> : Adds a todo task",
    "  - deadline <description> /by <date> : Adds a deadline task",
    "  - event <description> /from <start date> /to <end date> : Adds an event task",
    "  - mark <index> : Marks a task as completed",
    "  - unmark <index> : Marks a task as not completed",
    "  - delete <index> : Deletes a task from the list",
    "  - find <keyword> : Finds tasks by description",
    "  - contact add <name> /phone <phone> /email <email> : Adds a contact",
    "  - contact list : Views all contacts",
    "  - contact find <keyword> : Finds contacts by name, phone, or email",
    "  - contact delete <index> : Deletes a contact",
    "  - help : Shows this command guide",
    "  - bye : Ends the conversation",
    "  - Date format: YYYY-MM-DD (e.g. 2026-09-30)"
  ],
  "Meow... I don't recognize that command. Type help to see the available commands.",
  "Meow! Sea you again soon!"
]
```

## UI-07: Common input mistakes and missing files

**Aim:** Verify Gary starts with missing data files, accepts harmless whitespace and capitalization differences, and explains malformed commands without terminating.

### Inputs

```json
[
  "   LIST   ",
  "ToDo   read instructions",
  "DEADLINE submit report /BY 2026-02-31",
  "todo compare red | blue",
  "deadline submit report 2026-12-31",
  "event trip /FROM 2027-01-05 /TO 2027-01-04",
  "mark one",
  "contact   LIST",
  "contact ADD Alice Tan /PHONE 91234567 /EMAIL alice@example.com",
  "contact list extra",
  "bye extra",
  "bye"
]
```

### Expected outputs

```json
[
  "Your task list is empty. Add one with todo, deadline, or event.",
  [
    "Got it. I've added this task:",
    "  [T][ ] read instructions",
    "Now you have 1 tasks in the list."
  ],
  "Error: The deadline date must be in YYYY-MM-DD format",
  "Meow? Task descriptions cannot contain \" | \".",
  "Meow? Use: deadline DESCRIPTION /by YYYY-MM-DD.",
  "Error: The event end date cannot be before the start date",
  "Error: The task number is invalid",
  "Your contact list is empty. Add one with contact add.",
  [
    "Got it. I've added this contact:",
    "  Alice Tan (Phone: 91234567, Email: alice@example.com)",
    "Now you have 1 contact."
  ],
  "Meow... I don't recognize that contact command. Type help to see the available commands.",
  "Meow... I don't recognize that command. Type help to see the available commands.",
  "Meow! Sea you again soon!"
]
```
