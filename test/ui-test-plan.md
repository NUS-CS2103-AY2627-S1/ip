 # Clover UI test plan

The `test-ui` skill compiles Clover and launches `clover.Clover` for every command-line case. Expected output is compared exactly, excluding only a final newline. Cases marked **Type:** Manual require a JavaFX visual check and are skipped by the console runner.

Each case uses an isolated working directory. A case can include an optional **Saved data** block to provide the contents of `data/clover.txt` before Clover starts.

## Test case: Launch the JavaFX window

**Type:** Manual

**Aim:** Verify that Gradle starts the JavaFX application through `clover.Launcher`.

**Input:**

1. Run `./gradlew run` with JDK 25 selected.
2. Confirm that an FXML-based JavaFX window appears with **Clover** in both its operating-system title bar and its header, a gentle lavender, sage, and blue gradient, and a welcome card featuring Clover's original forest-sprite avatar.
3. Press Enter with an empty input and with an input containing only spaces. Confirm that neither creates a user message or a Clover reply. Then enter `todo read book` and press Enter. Confirm that the command and reply use small circular avatars aligned with the bottom of their message bubbles, with the user message on the right and Clover's forest-sprite reply on the left. Confirm that Clover says the new study quest has "taken root."
4. Enter `list` and select `Send`. Confirm that Clover says "The grove has gathered these for you," then lists `read book`, showing that chat commands use Clover's task list.
5. Enter an invalid command as the first non-blank command. Confirm that the command and Clover's red error reply have separate, non-overlapping bubbles.
6. Add enough messages to exceed the visible area and confirm that it scrolls to the latest response.
7. Hover over the chat log and use the mouse wheel or trackpad. Confirm that it scrolls without dragging the side scrollbar, that earlier messages remain inside the chat viewport rather than appearing over the header, and that dragging the scrollbar can review earlier messages.
8. Enter an invalid command and confirm that Clover's reply begins with "The forest path is unclear." and is styled as a distinct red error bubble.
9. Enter `mark 1` and confirm that Clover's "The grove celebrates!" reply bubble uses the success style. Enter `delete 1` and confirm its "This trail has been cleared." reply uses the delete-task style.
10. Enter `add-tutoree Alice Tan /address 12 Example Road /fee 50` and confirm that Clover's study-quest reply uses the distinct lavender tutoree-added style.
11. Resize the window. Confirm that the input bar and chat log remain anchored, and message bubbles expand while retaining comfortable reading widths.
12. Confirm that the background has a visible but gentle lavender, sage, and blue gradient and each avatar has a fixed-size circular coloured ring, even beside a long reply.
13. Confirm that button hover/pressed states do not change the layout.
14. Enter `bye` and confirm that Clover displays its farewell, disables further input, then closes the application window after about 700 ms.

**Expected output:** Clover opens an FXML-based JavaFX window with the Part 5 responsive, styled chat layout and exits cleanly when the window is closed.

## Test case: Run the extracted task commands

**Aim:** Verify that the command classes add, update, delete, and list the correct tasks.

**Input:**
```text
todo read book
deadline submit report /by 2026-09-01
mark 2
unmark 2
delete 1
list
bye
```

**Expected output:**
```text
____________________________________________________________
  _____    _         ____    __      __   ______    _____
 / ____|  | |       / __ \   \ \    / /  |  ____|  |  __ \
| |       | |      | |  | |   \ \  / /   | |__     | |__) |
| |       | |      | |  | |    \ \/ /    |  __|    |  _  /
| |____   | |____  | |  | |     \  /     | |____   | | \ \
 \_____|  |______|  \____/       \/      |______|  |_|  \_\

Hello! I'm Clover.
What can I do for you?

____________________________________________________________
____________________________________________________________
A new study quest has taken root: [T] [ ] read book
The grove now holds 1 quest.
____________________________________________________________
____________________________________________________________
A new study quest has taken root: [D] [ ] submit report (by: Sep 1 2026)
The grove now holds 2 quests.
____________________________________________________________
____________________________________________________________
The grove celebrates! This quest is complete: [D] [X] submit report (by: Sep 1 2026)
____________________________________________________________
____________________________________________________________
This quest needs a little more tending: [D] [ ] submit report (by: Sep 1 2026)
____________________________________________________________
____________________________________________________________
This trail has been cleared: [T] [ ] read book
The grove now holds 1 quest.
____________________________________________________________
____________________________________________________________
The grove has gathered your study quests:
1.[D] [ ] submit report (by: Sep 1 2026)
____________________________________________________________
____________________________________________________________
The grove closes for now. Goodbye, and may your path through the grove be gentle.
____________________________________________________________
```

**Expected saved data:**
```text
D | 0 | submit report | 2026-09-01
```

## Test case: Find tasks by keyword

**Aim:** Verify that Clover finds matching task descriptions regardless of letter case and keeps their original order.

**Saved data:**
```text
T | 1 | read book
D | 1 | return book | 2026-06-06
T | 0 | buy groceries
```

**Input:**
```text
find BOOK
find absent
bye
```

**Expected output:**
```text
____________________________________________________________
  _____    _         ____    __      __   ______    _____
 / ____|  | |       / __ \   \ \    / /  |  ____|  |  __ \
| |       | |      | |  | |   \ \  / /   | |__     | |__) |
| |       | |      | |  | |    \ \/ /    |  __|    |  _  /
| |____   | |____  | |  | |     \  /     | |____   | | \ \
 \_____|  |______|  \____/       \/      |______|  |_|  \_\

Hello! I'm Clover.
What can I do for you?

____________________________________________________________
____________________________________________________________
The grove found these matching quests:
1.[T] [X] read book
2.[D] [X] return book (by: Jun 6 2026)
____________________________________________________________
____________________________________________________________
No matching tasks found.
____________________________________________________________
____________________________________________________________
The grove closes for now. Goodbye, and may your path through the grove be gentle.
____________________________________________________________
```

## Test case: Reject unknown parameters

**Aim:** Verify that an unsupported slash parameter is identified directly instead of being treated as task text.

**Input:**
```text
deadline submit report /due Friday
bye
```

**Expected output:**
```text
____________________________________________________________
  _____    _         ____    __      __   ______    _____
 / ____|  | |       / __ \   \ \    / /  |  ____|  |  __ \
| |       | |      | |  | |   \ \  / /   | |__     | |__) |
| |       | |      | |  | |    \ \/ /    |  __|    |  _  /
| |____   | |____  | |  | |     \  /     | |____   | | \ \
 \_____|  |______|  \____/       \/      |______|  |_|  \_\

Hello! I'm Clover.
What can I do for you?

____________________________________________________________
____________________________________________________________
The forest path is unclear. Unknown parameter "/due" for this command.
____________________________________________________________
____________________________________________________________
The grove closes for now. Goodbye, and may your path through the grove be gentle.
____________________________________________________________
```

## Test case: Load saved tasks

**Aim:** Verify that Clover restores saved tasks, including their task type and completion status, when it starts.

**Saved data:**
```text
T | 1 | read \| annotate book
D | 0 | return book | 2019-12-02
E | 0 | project meeting | 2019-08-06 | 2019-08-07
```

**Input:**
```text
list
bye
```

**Expected output:**
```text
____________________________________________________________
  _____    _         ____    __      __   ______    _____
 / ____|  | |       / __ \   \ \    / /  |  ____|  |  __ \
| |       | |      | |  | |   \ \  / /   | |__     | |__) |
| |       | |      | |  | |    \ \/ /    |  __|    |  _  /
| |____   | |____  | |  | |     \  /     | |____   | | \ \
 \_____|  |______|  \____/       \/      |______|  |_|  \_\

Hello! I'm Clover.
What can I do for you?

____________________________________________________________
____________________________________________________________
The grove has gathered your study quests:
1.[T] [X] read | annotate book
2.[D] [ ] return book (by: Dec 2 2019)
3.[E] [ ] project meeting (from: Aug 6 2019 to: Aug 7 2019)
____________________________________________________________
____________________________________________________________
The grove closes for now. Goodbye, and may your path through the grove be gentle.
____________________________________________________________
```

## Test case: Recover from invalid saved data

**Aim:** Verify that malformed saved data does not prevent Clover from starting.

**Saved data:**
```text
Q | 0 | unknown task
```

**Input:**
```text
list
bye
```

**Expected output:**
```text
The forest path is unclear. The saved quest journal could not be opened. A backup was kept. Starting with an empty grove.
____________________________________________________________
  _____    _         ____    __      __   ______    _____
 / ____|  | |       / __ \   \ \    / /  |  ____|  |  __ \
| |       | |      | |  | |   \ \  / /   | |__     | |__) |
| |       | |      | |  | |    \ \/ /    |  __|    |  _  /
| |____   | |____  | |  | |     \  /     | |____   | | \ \
 \_____|  |______|  \____/       \/      |______|  |_|  \_\

Hello! I'm Clover.
What can I do for you?

____________________________________________________________
____________________________________________________________
There are no study quests in the grove yet.
____________________________________________________________
____________________________________________________________
The grove closes for now. Goodbye, and may your path through the grove be gentle.
____________________________________________________________
```

## Test case: Add an event

**Aim:** Verify that Clover accepts a correctly formatted event, displays it, and reports the correct task count.

**Input:**
```text
event tutorial /from 2019-12-02 /to 2019-12-04
bye
```

**Expected output:**
```text
____________________________________________________________
  _____    _         ____    __      __   ______    _____
 / ____|  | |       / __ \   \ \    / /  |  ____|  |  __ \
| |       | |      | |  | |   \ \  / /   | |__     | |__) |
| |       | |      | |  | |    \ \/ /    |  __|    |  _  /
| |____   | |____  | |  | |     \  /     | |____   | | \ \
 \_____|  |______|  \____/       \/      |______|  |_|  \_\

Hello! I'm Clover.
What can I do for you?

____________________________________________________________
____________________________________________________________
A new study quest has taken root: [E] [ ] tutorial (from: Dec 2 2019 to: Dec 4 2019)
The grove now holds 1 quest.
____________________________________________________________
____________________________________________________________
The grove closes for now. Goodbye, and may your path through the grove be gentle.
____________________________________________________________
```

**Expected saved data:**
```text
E | 0 | tutorial | 2019-12-02 | 2019-12-04
```

## Test case: Reject an event without times

**Aim:** Verify that an incomplete event command produces guidance instead of terminating the program.

**Input:**
```text
event tutorial
bye
```

**Expected output:**
```text
____________________________________________________________
  _____    _         ____    __      __   ______    _____
 / ____|  | |       / __ \   \ \    / /  |  ____|  |  __ \
| |       | |      | |  | |   \ \  / /   | |__     | |__) |
| |       | |      | |  | |    \ \/ /    |  __|    |  _  /
| |____   | |____  | |  | |     \  /     | |____   | | \ \
 \_____|  |______|  \____/       \/      |______|  |_|  \_\

Hello! I'm Clover.
What can I do for you?

____________________________________________________________
____________________________________________________________
The forest path is unclear. To schedule a grove event, use: event DESCRIPTION /from START /to END. Optional: add /for TUTOREE NAME.
____________________________________________________________
____________________________________________________________
The grove closes for now. Goodbye, and may your path through the grove be gentle.
____________________________________________________________
```

## Test case: Reject incorrect inputs without crashing

**Aim:** Verify that Clover explains common invalid inputs and remains available for the next command.

**Input:**
```text
todo
mark one
unmark 1
deadline submit
event tutorial /from 2019-12-02
deadline return book /by tomorrow
event tutorial /from 2019-12-02 /to tomorrow
read book

bye
```

**Expected output:**
```text
____________________________________________________________
  _____    _         ____    __      __   ______    _____
 / ____|  | |       / __ \   \ \    / /  |  ____|  |  __ \
| |       | |      | |  | |   \ \  / /   | |__     | |__) |
| |       | |      | |  | |    \ \/ /    |  __|    |  _  /
| |____   | |____  | |  | |     \  /     | |____   | | \ \
 \_____|  |______|  \____/       \/      |______|  |_|  \_\

Hello! I'm Clover.
What can I do for you?

____________________________________________________________
____________________________________________________________
The forest path is unclear. To plant a task, use: todo DESCRIPTION. Optional: add /for TUTOREE NAME.
____________________________________________________________
____________________________________________________________
The forest path is unclear. Choose a valid quest number to complete.
____________________________________________________________
____________________________________________________________
The forest path is unclear. Choose a valid quest number to tend again.
____________________________________________________________
____________________________________________________________
The forest path is unclear. To set a deadline, use: deadline DESCRIPTION /by DUE DATE. Optional: add /for TUTOREE NAME.
____________________________________________________________
____________________________________________________________
The forest path is unclear. To schedule a grove event, use: event DESCRIPTION /from START /to END. Optional: add /for TUTOREE NAME.
____________________________________________________________
____________________________________________________________
The forest path is unclear. Enter a date in yyyy-MM-dd format, for example 2026-02-28.
____________________________________________________________
____________________________________________________________
The forest path is unclear. Enter a date in yyyy-MM-dd format, for example 2026-02-28.
____________________________________________________________
____________________________________________________________
The forest path is unclear. That command is not a forest path I know. Type "help" to view the supported commands.
____________________________________________________________
____________________________________________________________
The forest path is unclear. The grove needs a command or a quest description.
____________________________________________________________
____________________________________________________________
The grove closes for now. Goodbye, and may your path through the grove be gentle.
____________________________________________________________
```

## Test case: Manage tutorees and link tasks

**Aim:** Verify that Clover stores tutoree records and links each supported task type only to an existing tutoree.

**Input:**
```text
add-tutoree Alice Tan /address 12 Example Road /fee 50
todo prepare worksheet /for Alice Tan
deadline collect fee /by 2026-09-30 /for alice tan
event lesson /from 2026-09-20 /to 2026-09-21 /for Alice Tan
list-tutorees
find-tutoree ALI
list
bye
```

**Expected output:**
```text
____________________________________________________________
  _____    _         ____    __      __   ______    _____
 / ____|  | |       / __ \   \ \    / /  |  ____|  |  __ \
| |       | |      | |  | |   \ \  / /   | |__     | |__) |
| |       | |      | |  | |    \ \/ /    |  __|    |  _  /
| |____   | |____  | |  | |     \  /     | |____   | | \ \
 \_____|  |______|  \____/       \/      |______|  |_|  \_\

Hello! I'm Clover.
What can I do for you?

____________________________________________________________
____________________________________________________________
A new learning companion has arrived in the grove:
Alice Tan
Address: 12 Example Road
Fee: 50
The grove now knows 1 learning companion.
____________________________________________________________
____________________________________________________________
A new study quest has taken root: [T] [ ] prepare worksheet (for: Alice Tan)
The grove now holds 1 quest.
____________________________________________________________
____________________________________________________________
A new study quest has taken root: [D] [ ] collect fee (by: Sep 30 2026) (for: Alice Tan)
The grove now holds 2 quests.
____________________________________________________________
____________________________________________________________
A new study quest has taken root: [E] [ ] lesson (from: Sep 20 2026 to: Sep 21 2026) (for: Alice Tan)
The grove now holds 3 quests.
____________________________________________________________
____________________________________________________________
Here are the learning companions in the grove:
1. Alice Tan
   Address: 12 Example Road
   Fee: 50
____________________________________________________________
____________________________________________________________
The grove found these matching learning companions:
1. Alice Tan
   Address: 12 Example Road
   Fee: 50
____________________________________________________________
____________________________________________________________
The grove has gathered your study quests:
1.[T] [ ] prepare worksheet (for: Alice Tan)
2.[D] [ ] collect fee (by: Sep 30 2026) (for: Alice Tan)
3.[E] [ ] lesson (from: Sep 20 2026 to: Sep 21 2026) (for: Alice Tan)
____________________________________________________________
____________________________________________________________
The grove closes for now. Goodbye, and may your path through the grove be gentle.
____________________________________________________________
```

**Expected saved data:**
```text
T | 0 | prepare worksheet | Alice Tan
D | 0 | collect fee | 2026-09-30 | Alice Tan
E | 0 | lesson | 2026-09-20 | 2026-09-21 | Alice Tan
```

**Expected saved tutoree data:**
```text
S | Alice Tan | 12 Example Road | 50
```

## Test case: Reject an unknown linked tutoree

**Aim:** Verify that a task cannot be linked to a student who is absent from the tutoree directory.

**Input:**
```text
todo prepare worksheet /for Alice Tan
bye
```

**Expected output:**
```text
____________________________________________________________
  _____    _         ____    __      __   ______    _____
 / ____|  | |       / __ \   \ \    / /  |  ____|  |  __ \
| |       | |      | |  | |   \ \  / /   | |__     | |__) |
| |       | |      | |  | |    \ \/ /    |  __|    |  _  /
| |____   | |____  | |  | |     \  /     | |____   | | \ \
 \_____|  |______|  \____/       \/      |______|  |_|  \_\

Hello! I'm Clover.
What can I do for you?

____________________________________________________________
____________________________________________________________
The forest path is unclear. No learning companion named "Alice Tan" is in the grove. Add them before linking a quest.
____________________________________________________________
____________________________________________________________
The grove closes for now. Goodbye, and may your path through the grove be gentle.
____________________________________________________________
```

## Test case: Display command help

**Aim:** Verify that Clover lists every supported command and its format.

**Input:**
```text
help
bye
```

**Expected output:**
```text
____________________________________________________________
  _____    _         ____    __      __   ______    _____
 / ____|  | |       / __ \   \ \    / /  |  ____|  |  __ \
| |       | |      | |  | |   \ \  / /   | |__     | |__) |
| |       | |      | |  | |    \ \/ /    |  __|    |  _  /
| |____   | |____  | |  | |     \  /     | |____   | | \ \
 \_____|  |______|  \____/       \/      |______|  |_|  \_\

Hello! I'm Clover.
What can I do for you?

____________________________________________________________
____________________________________________________________
How to use Clover
Type lowercase words exactly. Replace <UPPERCASE> placeholders with your details.
[Square brackets show optional parts; do not type the brackets when writing the command.]

TASKS
• todo <DESCRIPTION> [/for <NAME>]
  Add a task. /for links it to a tutoree.
• deadline <DESCRIPTION> /by <DATE> [/for <NAME>]
  Add a deadline. DATE uses yyyy-MM-dd.
• event <DESCRIPTION> /from <DATE> /to <DATE> [/for <NAME>]
  Add an event. The end date must be after the start date.
• list
  Show all tasks.
• find <KEYWORD>
  Search task descriptions.
• mark <NUMBER>
  Complete a task.
• unmark <NUMBER>
  Reopen a completed task.
• delete <NUMBER>
  Remove a task.

TUTOREE DIRECTORY
• add-tutoree <NAME> /address <ADDRESS> /fee <AMOUNT>[/RATE]
  Add a tutoree. RATE may be /hour, /session, /lesson, or /month.
• list-tutorees
  Show all tutorees.
• find-tutoree <KEYWORD>
  Search tutoree names.

OTHER
• help
  Show this command guide.
• bye
  Close Clover.
____________________________________________________________
____________________________________________________________
The grove closes for now. Goodbye, and may your path through the grove be gentle.
____________________________________________________________
```
