# Console UI test plan

Each test case specifies its aim, commands, and expected output associated with each command.

```json
{
  "cases": [
    {
      "name": "lantern keeper personality and empty trail",
      "aim": "Verify empty-state help, the task lifecycle, matching feedback, and the lantern keeper voice.",
      "commands": ["list", "mark 1", "todo read chapter 1", "mark 1", "unmark 1", "find chapter", "find absent", "delete 1", "bye"],
      "expected": ["A clear path! No tasks yet. Try: todo read chapter 1", "A little fog on the path. There are no tasks yet.", "A new trail marker. I've added this task:", "One more light along the path. Task marked as done:", "Back on the trail. Task marked as not done:", "I've held the lantern up to these matches:", "No matching tasks found. Try another word or #tag.", "Path cleared. I've removed this task:", "Rest well, wanderer. I'll keep the lantern lit."]
    },
    {
      "name": "handle malformed saved tasks",
      "aim": "Verify malformed records are ignored while valid records still load.",
      "saved_file": "2|T|1|cmVhZCBib29r\nnot a valid record\n2|X|0|YmFkIHR5cGU=\n2|D|9|YmFkIHN0YXR1cw==",
      "commands": ["list", "bye"],
      "expected": ["1.[T][X] read book", "Rest well, wanderer. I'll keep the lantern lit."]
    },
    {
      "name": "load task text containing separators",
      "aim": "Verify encoded task fields preserve pipe characters and Unicode text.",
      "saved_file": "2|T|0|cmVhZCB8IGJvb2s=",
      "commands": ["list", "bye"],
      "expected": ["1.[T][ ] read | book", "Rest well, wanderer. I'll keep the lantern lit."]
    },
    {
      "name": "load saved tasks on startup",
      "aim": "Verify tasks are reconstructed from the save file when Anders starts.",
      "saved_file": "T | 1 | read book\n2|D|0|cmV0dXJuIGJvb2s=|MjAxOS0xMi0wMg==\n2|E|0|cHJvamVjdCBtZWV0aW5n|MjAyNS0wMS0wMSAxNDowMA==|MjAyNS0wMS0wMSAxNjowMA==",
      "commands": ["list", "bye"],
      "expected": ["1.[T][X] read book\n     2.[D][ ] return book (by: Dec 02 2019)\n     3.[E][ ] project meeting (from: Jan 01 2025 2.00 pm to: Jan 01 2025 4.00 pm)", "Rest well, wanderer. I'll keep the lantern lit."]
    },
    {
      "name": "save tasks after changes",
      "aim": "Verify adding, marking, and deleting tasks writes the current task list to disk.",
      "commands": ["todo save this task", "mark 1", "delete 1", "bye"],
      "expected": ["Your trail holds 1 task.", "[X] save this task", "I've removed this task", "Rest well, wanderer. I'll keep the lantern lit."]
    },
    {
      "name": "add and list all task types",
      "aim": "Verify todo, deadline, event parsing, and polymorphic list display.",
      "commands": ["todo borrow book", "deadline return book /by 2019-12-02", "event project meeting /from 2025-01-01 14:00 /to 2025-01-01 16:00", "list", "bye"],
      "expected": ["[T] borrow book", "[D] return book (by: Dec 02 2019)", "[E] project meeting (from: Jan 01 2025 2.00 pm to: Jan 01 2025 4.00 pm)", "1.[T][ ] borrow book", "Rest well, wanderer. I'll keep the lantern lit."]
    },
    {
      "name": "parse and format ISO deadline dates",
      "aim": "Verify yyyy-MM-dd deadline input is stored as a date and displayed in a readable format.",
      "commands": ["deadline return book /by 2019-12-02", "list", "bye"],
      "expected": ["[D] return book (by: Dec 02 2019)", "1.[D][ ] return book (by: Dec 02 2019)", "Rest well, wanderer. I'll keep the lantern lit."]
    },
    {
      "name": "add and list ISO deadline times",
      "aim": "Accept compact and colon-separated ISO deadline times with tags and display both correctly.",
      "commands": [
        "deadline compact report /by 2026-10-02 1800 /tags #School",
        "deadline colon report /by 2026-10-02 18:00",
        "list",
        "bye"
      ],
      "expected": [
        "[D] compact report (by: Oct 02 2026 6.00 pm) (tags: #school)",
        "[D] colon report (by: Oct 02 2026 6.00 pm)",
        "1.[D][ ] compact report (by: Oct 02 2026 6.00 pm) (tags: #school)\n     2.[D][ ] colon report (by: Oct 02 2026 6.00 pm)",
        "Rest well, wanderer. I'll keep the lantern lit."
      ]
    },
    {
      "name": "reject invalid ISO deadline times",
      "aim": "Reject impossible dates and times in both new formats without adding tasks.",
      "commands": [
        "deadline report /by 2026-02-30 1800",
        "deadline report /by 2026-02-30 18:00",
        "deadline report /by 2026-10-02 2400",
        "deadline report /by 2026-10-02 18:60",
        "list",
        "bye"
      ],
      "expected": [
        "A deadline needs a real date",
        "A deadline needs a real date",
        "A deadline needs a real date",
        "A deadline needs a real date",
        "A clear path! No tasks yet.",
        "Rest well, wanderer. I'll keep the lantern lit."
      ]
    },
    {
      "name": "load saved ISO deadline times",
      "aim": "Load both ISO deadline time formats from saved records and preserve explicit midnight.",
      "saved_file": "D | 0 | compact report | 2026-10-02 1800\nD | 1 | midnight report | 2026-10-02 00:00",
      "commands": [
        "list",
        "bye"
      ],
      "expected": [
        "1.[D][ ] compact report (by: Oct 02 2026 6.00 pm)\n     2.[D][X] midnight report (by: Oct 02 2026 12.00 am)",
        "Rest well, wanderer. I'll keep the lantern lit."
      ]
    },
    {
      "name": "handle invalid commands",
      "aim": "Verify empty todo, deadline, and event descriptions plus unknown commands produce helpful errors without ending the session.",
      "commands": ["", "todo", "deadline", "event", "mark", "unmark", "blah", "bye"],
      "expected": ["A little fog on the path. I don't know what that means. Please enter a command.", "A little fog on the path. The description of a todo cannot be empty.", "A little fog on the path. A deadline needs a description and a /by value.", "A little fog on the path. An event needs a description, /from value, and /to value.", "A little fog on the path. Mark needs a task number.", "A little fog on the path. Unmark needs a task number.", "A little fog on the path. I don't know what that means.", "Rest well, wanderer. I'll keep the lantern lit."]
    },
    {
      "name": "invalid todo does not alter state",
      "aim": "Verify an empty todo is rejected and does not create a task before a valid todo is listed.",
      "commands": ["todo valid task", "todo", "list", "bye"],
      "expected": ["Your trail holds 1 task.", "A little fog on the path. The description of a todo cannot be empty.", "1.[T][ ] valid task", "Rest well, wanderer. I'll keep the lantern lit."]
    },
    {
      "name": "invalid deadline and event do not alter state",
      "aim": "Verify malformed deadline and event commands do not create phantom tasks between valid tasks.",
      "commands": ["deadline valid deadline /by 2019-12-02", "deadline missing by", "event valid event /from 2025-01-01 14:00 /to 2025-01-01 15:00", "event missing times", "list", "bye"],
      "expected": ["Your trail holds 1 task.", "A little fog on the path. A deadline needs a description and a /by value.", "Your trail holds 2 tasks.", "A little fog on the path. An event needs a description, /from value, and /to value.", "1.[D][ ] valid deadline (by: Dec 02 2019)", "Rest well, wanderer. I'll keep the lantern lit."]
    },
    {
      "name": "invalid mark commands do not alter completion state",
      "aim": "Verify valid marking works and invalid mark or unmark inputs do not change task completion state.",
      "commands": ["todo finish report", "mark 1", "mark abc", "mark 0", "unmark 1", "unmark xyz", "list", "bye"],
      "expected": ["Your trail holds 1 task.", "[X] finish report", "Please provide a valid task number.", "Task number must be between 1 and 1.", "[ ] finish report", "Please provide a valid task number.", "1.[T][ ] finish report", "Rest well, wanderer. I'll keep the lantern lit."]
    },
    {
      "name": "delete task and renumber remaining tasks",
      "aim": "Verify a selected task is removed and later tasks shift down in the list.",
      "commands": ["todo read book", "deadline return book /by 2019-12-02", "event project meeting /from 2025-08-06 14:00 /to 2025-08-06 16:00", "delete 2", "list", "bye"],
      "expected": ["Your trail holds 1 task.", "Your trail holds 2 tasks.", "Your trail holds 3 tasks.", "Your trail holds 2 tasks.", "1.[T][ ] read book\n     2.[E][ ] project meeting (from: Aug 06 2025 2.00 pm to: Aug 06 2025 4.00 pm)", "Rest well, wanderer. I'll keep the lantern lit."]
    },
    {
      "name": "create and reuse tags across tasks",
      "aim": "Verify tags can be supplied during creation, reused by multiple tasks, and searched case-insensitively.",
      "commands": ["todo read book /tags #Fun #school", "todo revise notes /tags #fun", "list", "find #FUN", "bye"],
      "expected": ["[T] read book (tags: #fun #school)", "[T] revise notes (tags: #fun)", "1.[T][ ] read book (tags: #fun #school)", "2.[T][ ] revise notes (tags: #fun)", "Rest well, wanderer. I'll keep the lantern lit."]
    },
    {
      "name": "add and remove tags after creation",
      "aim": "Verify tag and untag commands update one task, prevent duplicates, and preserve the remaining tags.",
      "commands": ["todo read book", "tag 1 #fun #school", "tag 1 #FUN", "untag 1 #school", "list", "bye"],
      "expected": ["Your trail holds 1 task.", "Trail labels attached. I've tagged this task:", "Already signposted. This task already has these tags:", "A little less baggage. I've removed these tags from this task:", "1.[T][ ] read book (tags: #fun)", "Rest well, wanderer. I'll keep the lantern lit."]
    },
    {
      "name": "invalid tags do not alter state",
      "aim": "Verify malformed tags are rejected and do not create or modify task tags.",
      "commands": ["todo valid task /tags #fun", "tag 1 fun", "untag 1 #", "list", "bye"],
      "expected": ["Your trail holds 1 task.", "A little fog on the path. Each tag must start with #", "A little fog on the path. Each tag must start with #", "1.[T][ ] valid task (tags: #fun)", "Rest well, wanderer. I'll keep the lantern lit."]
    },
    {
      "name": "load saved tags on startup",
      "aim": "Verify version-3 saved tags are reconstructed and displayed when Anders starts.",
      "saved_file": "3|T|0|cmVhZCBib29r|I2Z1bg==",
      "commands": ["list", "bye"],
      "expected": ["1.[T][ ] read book (tags: #fun)", "Rest well, wanderer. I'll keep the lantern lit."]
    },
    {
      "name": "find tasks by keyword",
      "aim": "Verify find displays matching task descriptions with their original list numbers.",
      "commands": ["todo read book", "deadline return book /by 2019-12-02", "todo revise notes", "find BOOK", "find NOTHING", "bye"],
      "expected": ["Your trail holds 1 task.", "Your trail holds 2 tasks.", "Your trail holds 3 tasks.", "1.[T][ ] read book\n     2.[D][ ] return book (by: Dec 02 2019)", "No matching tasks found. Try another word or #tag.", "Rest well, wanderer. I'll keep the lantern lit."]
    },
    {
      "name": "reject impossible dates and reversed events",
      "aim": "Reject calendar and time mistakes without changing tasks, and accept a corrected command.",
      "commands": [
        "deadline report /by 31/2/2026",
        "event meeting /from 2/10/2026 1600 /to 2/10/2026 1400",
        "event meeting /from 2/10/2026 2400 /to 3/10/2026",
        "list",
        "todo corrected task",
        "bye"
      ],
      "expected": [
        "A deadline needs a real date",
        "An event must end at or after it starts.",
        "Event dates must be real dates",
        "A clear path! No tasks yet.",
        "Your trail holds 1 task.",
        "Rest well, wanderer. I'll keep the lantern lit."
      ]
    },
    {
      "name": "recover from invalid saved dates",
      "aim": "Show a startup warning, skip bad dates, and retain valid tasks on both sides of damaged records.",
      "saved_file": "T | 0 | first task\nD | 0 | bad date | tomorrow\nE | 0 | reversed event | 2026-10-02 1600 | 2026-10-02 1400\nT | 0 | last task",
      "commands": [
        "list",
        "list",
        "todo next task",
        "bye"
      ],
      "expected": [
        "Skipped 2 invalid saved task record(s)",
        "1.[T][ ] first task\n     2.[T][ ] last task",
        "Your trail holds 3 tasks.",
        "Rest well, wanderer. I'll keep the lantern lit."
      ]
    },
    {
      "name": "start without a data file",
      "aim": "Start with an empty trail and create usable task storage after the first addition.",
      "commands": [
        "list",
        "todo first task",
        "list",
        "bye"
      ],
      "expected": [
        "A clear path! No tasks yet.",
        "Your trail holds 1 task.",
        "1.[T][ ] first task",
        "Rest well, wanderer. I'll keep the lantern lit."
      ]
    }
  ]
}
```

## Packaged JAR checks

Use ordinary JDK 25 without bundled JavaFX. Build the JAR and external GUI test
agent with `.\gradlew.bat check shadowJar smokeAgentJar` on Windows or
`./gradlew check shadowJar smokeAgentJar` on macOS/Linux. Run
`python test/run_jar_smoke.py`; use `xvfb-run --auto-servernum` before each
command on headless Linux.

The script verifies all four native-library folders, the manifest entry point,
the absence of duplicate entries, and that the test agent is not in the release.
It copies only `anders.jar` into a fresh temporary working folder, supplies the
agent from outside that folder, and launches `java -jar`. A fresh user-home folder
also prevents an existing JavaFX cache from masking missing libraries.
The first GUI session adds and tags a task, rejects an invalid date, and marks
the task done. The second session checks that the task, status, and tag reload,
then unmarks and deletes it. The observer waits for each window inspection before
scheduling another. After checking commands, it lets queued reply layouts finish
and closes the window normally, so JavaFX can shut down after its event queue is
idle. Both sessions must print `SMOKE PASSED` after closing the window and exit
with code 0. An uncaught exception or a nonzero exit still fails the test, even
if the success message was printed. Full input/output records and the release
checksum are written to `build/reports/jar-smoke/`. The process times out on hangs.
`SmokeAgentTest` covers a slow GUI without a callback backlog, delayed toolkit
startup, command assertion failures, and a GUI that never runs its callbacks.

GitHub Actions builds `anders.jar` once and downloads that same artifact into
Windows x64, Linux x64, Intel Mac, and Apple Silicon Mac smoke-test jobs. Every
job uses plain JDK 25. Compare their SHA-256 logs to confirm the artifact is
identical. Check all `build` and `jar-smoke` jobs after pushing; a previous green
run does not validate a newer local JAR.

Before publishing, also copy the tested JAR into an empty folder and manually run
`java -jar anders.jar` without the agent. Ask testers on the other operating
systems to repeat the following checks:

| Action | Expected result |
| --- | --- |
| Start the same release JAR with Java 25. | The GUI opens with no separate JavaFX installation. |
| Add each task type, tag a task, mark it, and restart from the same folder. | Tasks, dates, status, and tags are restored. |
| Enter an invalid date and an invalid task number. | Helpful errors appear, and the next valid command works. |
| Resize, scroll, use Tasks and Commands, and submit with Enter and Send. | Controls remain usable and text remains readable. |
| Inspect the original project data file after automated smoke testing. | Its contents are unchanged; the smoke tests use temporary data. |

## GUI checks

Run `gradlew.bat test` (or `./gradlew test`) with Java 25. `MainWindowTest` loads the
real FXML and CSS, checks input submission and history, draft preservation, narrow
layouts, long-word wrapping, and scrolling. It saves actual JavaFX previews under
`build/reports/gui/` at 360, 460, and 720 pixels wide.

For headless Linux CI, run `xvfb-run --auto-servernum ./gradlew check` with
Java 25 and Xvfb installed. This provides a virtual display for the JavaFX tests.
The workflow uses this command only on Linux; macOS and Windows run
`./gradlew check` directly. Each job has a 10-minute timeout so a stalled GUI
toolkit cannot keep CI running indefinitely. After pushing a workflow change,
check the new run: Windows, Linux, Intel Mac, and Apple Silicon Mac build jobs
and all packaged-JAR smoke-test jobs should finish successfully.

For a manual pass, launch `gradlew.bat run` (or `./gradlew run`) and check:

| Action | Expected result |
| --- | --- |
| Launch the app. | Title, header, greeting, and reply labels say Anders. The forest-green header, parchment replies, amber Send button, serif title, lantern bot icons, compass user icons, and window icon render clearly. |
| Start with no data file, then add a task and restart. | Anders starts empty and reloads the newly saved task. |
| Start with malformed saved dates or an unreadable data file. | A visible warning explains the problem; valid records load. Unreadable files are protected from overwrite. |
| Submit an impossible date or an event ending before it starts, then a valid command. | An actionable error appears; the next command still works. |
| Make the data path unwritable after launch and change a task. | The reply explains that changes are only available in this session and how to retry saving. |
| Open the window and resize it down to its minimum size. | Input stays visible; messages wrap; no horizontal scrolling or clipped text. |
| Type spaces only, then press Enter. | Send stays disabled and no message is added. |
| Send `todo read chapter 1` using Enter, then send `list` with Send. | Each command runs once; input clears and regains focus. |
| Draft a command, press Up repeatedly, then Down repeatedly. | Submitted commands are recalled in order; navigation stops at the ends and restores the draft. |
| Click Tasks and Commands while drafting. | Tasks lists saved tasks; Commands shows examples; both preserve the draft. |
| Open Commands, then narrow the window and copy the guide. | The guide explains placeholders, groups commands by purpose, and gives each command a description. Section headings and task-number guidance are bold. Examples stay regular and wrap; copied text has no formatting markers. |
| Read Update a task in Commands. | The guide defines `<number>` as any current task number from 1 to the task count, explains that two tasks allow either 1 or 2, and notes renumbering after deletion. |
| Read the Example lines in Commands. | Each is one complete command accepted by the parser. The date explanation makes day/month order and 24-hour time clear. |
| Request a list longer than the visible conversation area. | The beginning of the new reply is visible; scrolling reveals the remaining lines. |
| Scroll back to older replies. | The scroll position is freely adjustable. |
| Right-click a reply and choose Copy message, then paste into the input. | The complete reply is copied without console indentation. |
| Navigate controls with Tab and Shift+Tab. | Focus is visible; Enter submits from the input; buttons work with the keyboard. |

The console cases above check Anders's new phrases while retaining the existing
command syntax and task formatting. Run the console test skill in an isolated workspace: its runner replaces
`data/anders.txt` as part of its fixtures.
