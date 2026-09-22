# UI Test Plan

This plan tests the Stewie console application. Tests are run as separate sessions so each case starts with an empty task list.

JUnit coverage and the OS, language, resolution, and scaling matrix are documented in
[the testing guide](README.md). The exact console expectations below use an English JVM locale.
Under a Burmese JVM locale, task-count confirmations use Burmese digits (for example, `၁`);
task dates still use English month names. `CliTest` checks both locale behaviors separately.

## Universal release startup checks

Prepare with Java 25: `./gradlew shadowJar` (PowerShell: `.\gradlew.bat shadowJar`).
The output `build/libs/stewie.jar` contains shared files and five isolated platform overlays, totaling at most 12,000,000 bytes. Run
`python3 test/check-universal-jar.py` to check every bundled runtime and native CPU type.

- Copy only `stewie.jar` to a fresh writable directory, including a directory
  whose name contains spaces. Run `java -jar stewie.jar` with standard Java 25,
  without JavaFX installed or a network connection. Expect the Stewie window
  and greeting, with no missing runtime, native-library, or architecture error.
- Repeat the same JAR on Windows x64 (including Windows 11 Pro N), macOS Intel
  and Apple Silicon, and glibc-based Linux/Ubuntu x64 and ARM64 with a desktop.
  The JVM architecture determines which runtime is selected.
- Add `todo universal smoke test`, close the window, and check exit status 0.
  Relaunch from the same directory and send `list`: the task must persist in
  `data/stewie.txt` in that directory, rather than in the temporary runtime folder.
- Verify the temporary `.stewie-runtime-*` directory is created inside the launch directory
  and removed after normal exit.
- On an unsupported OS or JVM architecture, expect a clear unsupported-platform
  message and exit status 1, rather than attempting to load incompatible native code.
- Run the existing GUI navigation, persistence, input, and scaling checks with
  the universal JAR on each available platform. Archive checks alone are not GUI tests.

## Windows release startup checks

Prepare with Java 25: `./gradlew windowsJars` (PowerShell: `.\gradlew.bat windowsJars`).
Run `python3 test/check-windows-jar.py` (Windows: `py -3 test/check-windows-jar.py`)
to check the manifest, Windows JavaFX classes, resources, icon services, and x64
PE headers of every DLL. The archive check works on macOS too, but does not prove
that the GUI opens on Windows.

- On Windows 11 x64, including the reported Pro N edition, use a standard x64
  Java 25 JDK without JavaFX. Copy only `build/libs/stewie-windows-x64.jar` into
  a fresh writable directory, open PowerShell there, and run
  `java -jar .\stewie-windows-x64.jar`. Expect the "Stewie — your task studio"
  window and greeting, with no missing JavaFX runtime or native-library error.
- In Chat, send `todo windows smoke test`, then `list`. Expect one unfinished
  task. Close the window; expect `$LASTEXITCODE` to be `0`. Relaunch from the
  same directory, send `list`, and confirm the task was restored. Send `mark 1`,
  then `list`, and confirm the task is completed. Close and relaunch once more
  to verify the completion state persists. Closing must again exit with `0`.
- Repeat with the JAR built on macOS and with the JAR renamed to `stewie.jar`,
  using `java -jar .\stewie.jar`, to cover the original smoke-test command.
- Run the GUI navigation and display-scaling checks below on Windows after
  startup succeeds. Record the exact JDK, OS edition, artifact, and results.
- Build with `./gradlew build` and confirm the Windows and both Linux release
  JARs are created. Run both archive checkers to protect existing Linux packaging.

## Linux release startup checks

Prepare with Java 25: `./gradlew linuxJars`, then run
`python3 test/check-linux-jars.py` to verify both archives and their native CPU types.
These checks supplement the ordered
console sessions; they need a real Linux desktop or an Xvfb display.

- On Ubuntu 22.04 x86-64 with a standard Java 25 JDK (without JavaFX), launch
  `java -jar stewie-linux-x64.jar` from a fresh temporary directory. Expect the
  "Stewie — your task studio" window and greeting, with no missing JavaFX runtime,
  missing native library, or incompatible architecture error. Close the window:
  expect exit status 0. Repeat on another glibc-based Linux distribution.
- On Linux ARM64 with ARM64 Java 25, repeat using `java -jar stewie-linux-aarch64.jar`.
- Build the Linux JARs on macOS as well as Linux. Repeat the relevant startup check
  with the macOS-built artifact, proving packaging does not depend on the build host.
- For each Linux JAR, verify the manifest uses `stewie.ui.gui.Launcher`, JavaFX
  classes and Linux `.so` files are bundled, and no macOS `.dylib` or Windows `.dll`
  files are present. Verify native ELF machine types match the JAR's architecture.
- After successful startup, run the GUI navigation checks below, including adding
  a task, closing the window, and relaunching from the same directory to check persistence.
- On macOS, build with `./gradlew shadowJar` and launch `java -jar build/libs/stewie.jar`
  to check that the shared launcher still opens the existing desktop interface.

## Additional GUI navigation checks

- Launch the GUI from a terminal and add, update, mark, unmark, and delete tasks.
  Show replies in the GUI without printing CLI task confirmations or numbered-command guidance
  to the launching terminal. Running the CLI must retain the exact console output below.

- Add two tasks, enter `list`, then delete the first task. Click a checkbox or delete button
  in the old chat list: show a refreshed list and an out-of-date notice; leave the surviving task unchanged.
  Repeat after an update or a status change. Controls in the refreshed list must work normally.
- Launch a test build without the portrait or stylesheet resources: show a text logo or default JavaFX
  styling and keep command entry working. A corrupt portrait must also fall back to the text logo.


- Start with a malformed or unreadable task file: show the startup warning in Chat.
  Attempt additions, updates, deletion, and checkbox actions: show an error without a success reply
  or a changed task. My List checkbox failures must show a visible error and restore the checkbox.
- Make the file unwritable while the GUI is running and retry a command or card action.
  Restore permissions and retry: save successfully and retain the original data after the failed attempt.


- Verify Chat opens with "Ah, there you are. I'm Stewie." and the supervision greeting.
  Send todo, deadline, event, list, mark, unmark, delete, update, find, help, and bye commands.
  Confirm replies use dry, theatrical phrasing while task cards retain their descriptions, dates, and statuses.
  Completion should say "Completed. Rather well done, actually. Let us not make a scene."
  Bye should say "Very well. Do come back. I mean, someone must supervise your progress."
- Send an unknown command, invalid task number, malformed event/deadline, and invalid date.
  Confirm errors retain usable command guidance; invalid dates begin "A slight flaw in your plan:".
  Search for an absent keyword: show "Nothing matches. Even my brilliance needs a clue. Try another keyword or `list`."
- Verify Help in the sidebar and Chat includes the theatrical introduction and the unchanged command formats.


These manual checks supplement the console cases below. Launch the GUI with Java 25 using `./gradlew run`.

- Verify the sidebar brand and chat header show the Stewie resource image instead of the S logo.
  The whole image should be visible with its original proportions, including after resizing the window.

- Click each suggested command in Chat (`todo plan my week`, `list`, and `help`): immediately send the
  selected command once, show its response, clear the input field, and smoothly scroll to the newest content.
  No additional Enter key or Send click is required. Return keyboard focus to the input field.

- Click Help from Chat and My List: show "Help — Command formats" with formats for todo, deadline,
  event, list, find, mark, unmark, delete, update, help, and bye. Show optional update fields,
  the by/ alias, task numbering, and date examples. Scroll to reach the final example.
- Click Help repeatedly: show one reference and only one active navigation highlight.
  Return to Chat: preserve conversation and unsent input. Enter help: show the same command reference.
- Switch from My List to Help during a completion delay, then return to My List: show the current
  task state without a stale timer changing the displayed cards.
- Click My List with no saved tasks: show the My List heading and
  "No unfinished tasks. Remarkable. Add a task in Chat when ambition returns."
- Add todo, event, and deadline tasks in Chat, then click My List: show unfinished tasks in order,
  including their types, dates, original task numbers, and a circular checkbox on the right.
- Click a completion checkbox: mark that task done and update the sidebar count immediately; dim its card,
  disable its checkbox, and move the card to Completed after three seconds while the GUI remains responsive.
  Complete another task after the first disappears: mark the correct original task, despite the filtered list.
- Complete two tasks one second apart: each card stays dimmed for its own three seconds before disappearing.
  Clicking My List again while already selected must not shorten either delay.
- Switch to Chat during a delay and reopen My List: show the current unfinished tasks; an old timer must not
  remove any newly displayed card.
- Reopen My List and restart the app: completed tasks remain in Completed with checked circular checkboxes. Chat's list command still shows them as done.
- Complete all tasks: show the unfinished empty-state message and all tasks under Completed. Unmark a task in Chat: it reappears in My List.
- Tab to a completion checkbox and press Space: complete the task just as with a mouse click.
- Switch back to Chat: preserve the conversation and any unsent input.
- Change, mark, or delete a task in Chat and reopen My List: show the latest state without duplicate cards.
- Click the selected tab repeatedly: retain one active highlight and one copy of each task.
- With enough tasks to exceed the window height, scroll to reach the last task.
- In Chat, enter `list` with enough conversation history to require scrolling, then check a task:
  append the updated list and smoothly scroll to the bottom over approximately 450 milliseconds,
  easing into and out of the motion so its newest cards are visible without an abrupt jump.
  Uncheck a task in the latest list and verify the same behavior. Repeat after manually scrolling upward.
- Trigger another chat update during scrolling: replace the previous animation and settle at the newest bottom.

- In Completed, uncheck a task: save it as undone, move it immediately to Unfinished, and update the sidebar count.
  Restart the app and verify the task remains unfinished.
- Reopen a completed task while other tasks are dimmed: their individual three-second delays remain intact.
- With no completed tasks, show "No completed tasks yet. I await your first triumph." beneath the Completed heading.

## Shared setup and launch

From the repository root, use Java 25:

```sh
mkdir -p out/ui-test
javac -d out/ui-test $(find src/main/java -name '*.java' ! -path '*/stewie/ui/gui/*')
```

Launch each test case with:

```sh
repo_root="$PWD"
case_dir=$(mktemp -d)
cd "$case_dir"
java -ea -cp "$repo_root/out/ui-test" stewie.Stewie
```

Each case runs in a fresh temporary directory, preserving the real task file.
Run `python3 test/run-ui-tests.py <record-path>` to compile with Java 25 and execute the cases in order.
Optional case setup commands run in that temporary directory before launching the application.
Assertions are enabled with `-ea` to check internal invariants during every session.

The expected output below uses `LF` line endings and includes the final newline produced by the program. The skill may normalize `CRLF` to `LF` and one final trailing newline only.

## Test Case 3: Handle all valid task commands and status changes

### Aim

Verify that todo, event, deadline, list, mark, and unmark commands work together in one session.

### Inputs

```text
todo buy milk
event team meeting /from 10/08/2026 /to 11/08/2026
deadline submit report /by 12/08/2026
list
mark 2
list
unmark 2
list
bye
```

### Expected output

```text
███████╗ ████████╗ ███████╗ ██╗    ██╗ ██╗ ███████╗
██╔════╝ ╚══██╔══╝ ██╔════╝ ██║    ██║ ██║ ██╔════╝
███████╗    ██║    █████╗   ██║ █╗ ██║ ██║ █████╗
╚════██║    ██║    ██╔══╝   ██║███╗██║ ██║ ██╔══╝
███████║    ██║    ███████╗ ╚███╔███╔╝ ██║ ███████╗
╚══════╝    ╚═╝    ╚══════╝  ╚══╝╚══╝  ╚═╝ ╚══════╝

Ah, there you are. I'm Stewie.
Tell me your tasks. Clearly, this operation requires supervision.
Consider it recorded. A small triumph for competent administration.
[T] [ ] buy milk
Your agenda now contains 1 task. Do try to keep up.
Consider it recorded. A small triumph for competent administration.
[E] [ ] team meeting (from: 10 Aug 2026 to: 11 Aug 2026)
Your agenda now contains 2 tasks. Do try to keep up.
Consider it recorded. A small triumph for competent administration.
[D] [ ] submit report (by: 12 Aug 2026)
Your agenda now contains 3 tasks. Do try to keep up.
Behold, your agenda. Let us examine the scale of this undertaking.
1. [T] [ ] buy milk
2. [E] [ ] team meeting (from: 10 Aug 2026 to: 11 Aug 2026)
3. [D] [ ] submit report (by: 12 Aug 2026)
Behold, your agenda. Let us examine the scale of this undertaking.
1. [T] [ ] buy milk
2. [E] [X] team meeting (from: 10 Aug 2026 to: 11 Aug 2026)
3. [D] [ ] submit report (by: 12 Aug 2026)
Behold, your agenda. Let us examine the scale of this undertaking.
1. [T] [ ] buy milk
2. [E] [ ] team meeting (from: 10 Aug 2026 to: 11 Aug 2026)
3. [D] [ ] submit report (by: 12 Aug 2026)
Very well. Do come back. I mean, someone must supervise your progress.
```

## Test Case 4: Handle malformed and invalid commands

### Aim

Verify that malformed task commands and invalid task numbers produce clear errors without crashing or corrupting the session.

### Inputs

```text
please
todo
event team meeting
deadline submit report
mark 9
unmark 0
list
bye
```

### Expected output

```text
███████╗ ████████╗ ███████╗ ██╗    ██╗ ██╗ ███████╗
██╔════╝ ╚══██╔══╝ ██╔════╝ ██║    ██║ ██║ ██╔════╝
███████╗    ██║    █████╗   ██║ █╗ ██║ ██║ █████╗
╚════██║    ██║    ██╔══╝   ██║███╗██║ ██║ ██╔══╝
███████║    ██║    ███████╗ ╚███╔███╔╝ ██║ ███████╗
╚══════╝    ╚═╝    ╚══════╝  ╚══╝╚══╝  ╚═╝ ╚══════╝

Ah, there you are. I'm Stewie.
Tell me your tasks. Clearly, this operation requires supervision.
What precisely is the plan? Use a command: todo, event, deadline, mark, unmark, delete, find, update, list, bye + description!
A slight flaw in your plan: Use: todo <description>.
A slight flaw in your plan: Use: event <description> /from <date> /to <date>. Supply each field once in this order.
A slight flaw in your plan: Use: deadline <description> /by <date>. Supply each field once.
That task exists only in your imagination. Use a listed number: mark <number>
Numbers, please. Use: unmark <number>.
Behold, your agenda. Let us examine the scale of this undertaking.
No tasks to show. How suspiciously serene. Try adding a task or checking your search.
Very well. Do come back. I mean, someone must supervise your progress.
```

## Test Case 6: Delete a task and handle invalid delete numbers

### Aim

Verify that `delete` removes the requested task, reindexes the remaining list, and handles invalid task numbers without ending the session.

### Inputs

```text
todo first
todo second
delete 1
list
delete 9
delete 0
delete two
bye
```

### Expected output

```text
███████╗ ████████╗ ███████╗ ██╗    ██╗ ██╗ ███████╗
██╔════╝ ╚══██╔══╝ ██╔════╝ ██║    ██║ ██║ ██╔════╝
███████╗    ██║    █████╗   ██║ █╗ ██║ ██║ █████╗
╚════██║    ██║    ██╔══╝   ██║███╗██║ ██║ ██╔══╝
███████║    ██║    ███████╗ ╚███╔███╔╝ ██║ ███████╗
╚══════╝    ╚═╝    ╚══════╝  ╚══╝╚══╝  ╚═╝ ╚══════╝

Ah, there you are. I'm Stewie.
Tell me your tasks. Clearly, this operation requires supervision.
Consider it recorded. A small triumph for competent administration.
[T] [ ] first
Your agenda now contains 1 task. Do try to keep up.
Consider it recorded. A small triumph for competent administration.
[T] [ ] second
Your agenda now contains 2 tasks. Do try to keep up.
Behold, your agenda. Let us examine the scale of this undertaking.
1. [T] [ ] second
That task exists only in your imagination. Use a listed number: delete <number>
Numbers, please. Use: delete <number>.
Numbers, please. Use: delete <number>.
Very well. Do come back. I mean, someone must supervise your progress.
```

## Test Case 7: Update every task type

### Aim

Verify that `update` changes the description of todo, event, and deadline tasks while preserving their type,
date/time details, and completion status. Reject reversed dates without changing the existing event.

### Inputs

```text
todo buy milk
event team meeting /from 10/08/2026 /to 11/08/2026
deadline submit report /by 12/08/2026
mark 2
update 1 buy bread
update 2 planning meeting from/15/08/2026 to/16/08/2026
update 3 file report d/20/08/2026
update 2 from/17/08/2026
list
update 9 d/20/08/2026
update two invalid task
update 1
bye
```

### Expected output

```text
███████╗ ████████╗ ███████╗ ██╗    ██╗ ██╗ ███████╗
██╔════╝ ╚══██╔══╝ ██╔════╝ ██║    ██║ ██║ ██╔════╝
███████╗    ██║    █████╗   ██║ █╗ ██║ ██║ █████╗
╚════██║    ██║    ██╔══╝   ██║███╗██║ ██║ ██╔══╝
███████║    ██║    ███████╗ ╚███╔███╔╝ ██║ ███████╗
╚══════╝    ╚═╝    ╚══════╝  ╚══╝╚══╝  ╚═╝ ╚══════╝

Ah, there you are. I'm Stewie.
Tell me your tasks. Clearly, this operation requires supervision.
Consider it recorded. A small triumph for competent administration.
[T] [ ] buy milk
Your agenda now contains 1 task. Do try to keep up.
Consider it recorded. A small triumph for competent administration.
[E] [ ] team meeting (from: 10 Aug 2026 to: 11 Aug 2026)
Your agenda now contains 2 tasks. Do try to keep up.
Consider it recorded. A small triumph for competent administration.
[D] [ ] submit report (by: 12 Aug 2026)
Your agenda now contains 3 tasks. Do try to keep up.
Revised to your specifications. Yes, even that detail.
[T] [ ] buy bread
Revised to your specifications. Yes, even that detail.
[E] [X] planning meeting (from: 15 Aug 2026 to: 16 Aug 2026)
Revised to your specifications. Yes, even that detail.
[D] [ ] file report (by: 20 Aug 2026)
A slight flaw in your plan: Event start date must be before its end date.
Behold, your agenda. Let us examine the scale of this undertaking.
1. [T] [ ] buy bread
2. [E] [X] planning meeting (from: 15 Aug 2026 to: 16 Aug 2026)
3. [D] [ ] file report (by: 20 Aug 2026)
That task exists only in your imagination. Use a listed number: update <number>
A revision needs details. Use: update <number> [description] [d/<deadline>] [from/<from>] [to/<to>]
A revision needs details. Use: update <number> [description] [d/<deadline>] [from/<from>] [to/<to>]
Very well. Do come back. I mean, someone must supervise your progress.
```

## Test Case 5: Handle case and surrounding whitespace

### Aim

Verify that commands remain usable when entered with different letter casing and extra spaces around the command.

### Inputs

```text
  TODO   Read Book  
  LIST  
BYE
```

### Expected output

```text
███████╗ ████████╗ ███████╗ ██╗    ██╗ ██╗ ███████╗
██╔════╝ ╚══██╔══╝ ██╔════╝ ██║    ██║ ██║ ██╔════╝
███████╗    ██║    █████╗   ██║ █╗ ██║ ██║ █████╗
╚════██║    ██║    ██╔══╝   ██║███╗██║ ██║ ██╔══╝
███████║    ██║    ███████╗ ╚███╔███╔╝ ██║ ███████╗
╚══════╝    ╚═╝    ╚══════╝  ╚══╝╚══╝  ╚═╝ ╚══════╝

Ah, there you are. I'm Stewie.
Tell me your tasks. Clearly, this operation requires supervision.
Consider it recorded. A small triumph for competent administration.
[T] [ ] read book
Your agenda now contains 1 task. Do try to keep up.
Behold, your agenda. Let us examine the scale of this undertaking.
1. [T] [ ] read book
Very well. Do come back. I mean, someone must supervise your progress.
```

## Test Case 1: Start and exit

### Aim

Verify that the application displays its greeting and exits when the user enters `bye`.

### Inputs

```text
bye
```

### Expected output

```text
███████╗ ████████╗ ███████╗ ██╗    ██╗ ██╗ ███████╗
██╔════╝ ╚══██╔══╝ ██╔════╝ ██║    ██║ ██║ ██╔════╝
███████╗    ██║    █████╗   ██║ █╗ ██║ ██║ █████╗
╚════██║    ██║    ██╔══╝   ██║███╗██║ ██║ ██╔══╝
███████║    ██║    ███████╗ ╚███╔███╔╝ ██║ ███████╗
╚══════╝    ╚═╝    ╚══════╝  ╚══╝╚══╝  ╚═╝ ╚══════╝

Ah, there you are. I'm Stewie.
Tell me your tasks. Clearly, this operation requires supervision.
Very well. Do come back. I mean, someone must supervise your progress.
```

## Test Case 2: Add and list a todo

### Aim

Verify that a todo is added and then displayed in the task list.

### Inputs

```text
todo read book
list
bye
```

### Expected output

```text
███████╗ ████████╗ ███████╗ ██╗    ██╗ ██╗ ███████╗
██╔════╝ ╚══██╔══╝ ██╔════╝ ██║    ██║ ██║ ██╔════╝
███████╗    ██║    █████╗   ██║ █╗ ██║ ██║ █████╗
╚════██║    ██║    ██╔══╝   ██║███╗██║ ██║ ██╔══╝
███████║    ██║    ███████╗ ╚███╔███╔╝ ██║ ███████╗
╚══════╝    ╚═╝    ╚══════╝  ╚══╝╚══╝  ╚═╝ ╚══════╝

Ah, there you are. I'm Stewie.
Tell me your tasks. Clearly, this operation requires supervision.
Consider it recorded. A small triumph for competent administration.
[T] [ ] read book
Your agenda now contains 1 task. Do try to keep up.
Behold, your agenda. Let us examine the scale of this undertaking.
1. [T] [ ] read book
Very well. Do come back. I mean, someone must supervise your progress.
```

## Test Case 8: Find tasks and recover from invalid dates

### Aim

Verify searches keep their results and personality, and a date error explains the problem without adding a task.

### Inputs

```text
todo buy milk
find milk
find absent
deadline report /by impossible
list
bye
```

### Expected output

```text
███████╗ ████████╗ ███████╗ ██╗    ██╗ ██╗ ███████╗
██╔════╝ ╚══██╔══╝ ██╔════╝ ██║    ██║ ██║ ██╔════╝
███████╗    ██║    █████╗   ██║ █╗ ██║ ██║ █████╗
╚════██║    ██║    ██╔══╝   ██║███╗██║ ██║ ██╔══╝
███████║    ██║    ███████╗ ╚███╔███╔╝ ██║ ███████╗
╚══════╝    ╚═╝    ╚══════╝  ╚══╝╚══╝  ╚═╝ ╚══════╝

Ah, there you are. I'm Stewie.
Tell me your tasks. Clearly, this operation requires supervision.
Consider it recorded. A small triumph for competent administration.
[T] [ ] buy milk
Your agenda now contains 1 task. Do try to keep up.
Behold, your agenda. Let us examine the scale of this undertaking.
1. [T] [ ] buy milk
Behold, your agenda. Let us examine the scale of this undertaking.
No tasks to show. How suspiciously serene. Try adding a task or checking your search.
A slight flaw in your plan: Use a real calendar date, such as 2026-08-28 or 28/8/2026 (dates only).
Behold, your agenda. Let us examine the scale of this undertaking.
1. [T] [ ] buy milk
Very well. Do come back. I mean, someone must supervise your progress.
```

## Test Case 9: Reject ambiguous command fields

### Aim

Reject missing, repeated, misplaced, and unknown fields; accept tabs and command words inside descriptions.

### Inputs

```text
deadline
event
find
mark
mark -2147483648
delete +1
unmark 999999999999999
deadline report /by 1/1/2026 /by 2/1/2026
deadline report /by
event trip /to 2/1/2026 /from 1/1/2026
event trip /from 1/1/2026 /to 2/1/2026 /to 3/1/2026
update 1 d/1/1/2026 by/2/1/2026
update 1 from/
update 1 bogus/value
list extra
bye extra
  todo	  plan the event and deadline
list
bye
```

### Expected output

```text
███████╗ ████████╗ ███████╗ ██╗    ██╗ ██╗ ███████╗
██╔════╝ ╚══██╔══╝ ██╔════╝ ██║    ██║ ██║ ██╔════╝
███████╗    ██║    █████╗   ██║ █╗ ██║ ██║ █████╗
╚════██║    ██║    ██╔══╝   ██║███╗██║ ██║ ██╔══╝
███████║    ██║    ███████╗ ╚███╔███╔╝ ██║ ███████╗
╚══════╝    ╚═╝    ╚══════╝  ╚══╝╚══╝  ╚═╝ ╚══════╝

Ah, there you are. I'm Stewie.
Tell me your tasks. Clearly, this operation requires supervision.
A slight flaw in your plan: Use: deadline <description> /by <date>. Supply each field once.
A slight flaw in your plan: Use: event <description> /from <date> /to <date>. Supply each field once in this order.
A slight flaw in your plan: Use: find <keyword> [more keywords].
Numbers, please. Use: mark <number>.
Numbers, please. Use: mark <number>.
Numbers, please. Use: delete <number>.
Numbers, please. Use: unmark <number>.
A slight flaw in your plan: Use: deadline <description> /by <date>. Supply each field once.
A slight flaw in your plan: Use: deadline <description> /by <date>. Supply each field once.
A slight flaw in your plan: Use: event <description> /from <date> /to <date>. Supply each field once in this order.
A slight flaw in your plan: Use: event <description> /from <date> /to <date>. Supply each field once in this order.
A slight flaw in your plan: Supply each update field only once; d/ and by/ are aliases.
A slight flaw in your plan: Update fields cannot be empty.
A slight flaw in your plan: Use update fields: d/ (or by/), from/, to/.
What precisely is the plan? Use a command: todo, event, deadline, mark, unmark, delete, find, update, list, bye + description!
What precisely is the plan? Use a command: todo, event, deadline, mark, unmark, delete, find, update, list, bye + description!
Consider it recorded. A small triumph for competent administration.
[T] [ ] plan the event and deadline
Your agenda now contains 1 task. Do try to keep up.
Behold, your agenda. Let us examine the scale of this undertaking.
1. [T] [ ] plan the event and deadline
Very well. Do come back. I mean, someone must supervise your progress.
```

## Test Case 10: Preserve valid task data

### Aim

Reject duplicate additions and updates, unsafe descriptions, impossible dates, and invalid event ranges.
Completion status does not permit duplicate details; invalid updates preserve the original task.

### Inputs

```text
todo read book
mark 1
todo read   book
todo other
update 2 read book
update 1 read book
todo bad|description
event trip /from 2026-01-02 /to 2026-01-01
event trip /from 2026-01-01 /to 2026-01-01
deadline report /by 2026-02-30
deadline report /by 2025-02-29
deadline report /by 0000-01-01
event trip /from 2026-01-01 /to 2026-01-02
event trip /from 1/1/2026 /to 2/1/2026
update 3 to/2026-01-01
update 2 d/2026-01-01
list
bye
```

### Expected output

```text
███████╗ ████████╗ ███████╗ ██╗    ██╗ ██╗ ███████╗
██╔════╝ ╚══██╔══╝ ██╔════╝ ██║    ██║ ██║ ██╔════╝
███████╗    ██║    █████╗   ██║ █╗ ██║ ██║ █████╗
╚════██║    ██║    ██╔══╝   ██║███╗██║ ██║ ██╔══╝
███████║    ██║    ███████╗ ╚███╔███╔╝ ██║ ███████╗
╚══════╝    ╚═╝    ╚══════╝  ╚══╝╚══╝  ╚═╝ ╚══════╝

Ah, there you are. I'm Stewie.
Tell me your tasks. Clearly, this operation requires supervision.
Consider it recorded. A small triumph for competent administration.
[T] [ ] read book
Your agenda now contains 1 task. Do try to keep up.
A slight flaw in your plan: A task with these details already exists (task 1).
Consider it recorded. A small triumph for competent administration.
[T] [ ] other
Your agenda now contains 2 tasks. Do try to keep up.
A slight flaw in your plan: A task with these details already exists (task 1).
Revised to your specifications. Yes, even that detail.
[T] [X] read book
A slight flaw in your plan: Descriptions cannot contain | or control characters.
A slight flaw in your plan: Event start date must be before its end date.
A slight flaw in your plan: Event start date must be before its end date.
A slight flaw in your plan: Use a real calendar date, such as 2026-08-28 or 28/8/2026 (dates only).
A slight flaw in your plan: Use a real calendar date, such as 2026-08-28 or 28/8/2026 (dates only).
A slight flaw in your plan: Use a year between 0001 and 9999.
Consider it recorded. A small triumph for competent administration.
[E] [ ] trip (from: 01 Jan 2026 to: 02 Jan 2026)
Your agenda now contains 3 tasks. Do try to keep up.
A slight flaw in your plan: A task with these details already exists (task 3).
A slight flaw in your plan: Event start date must be before its end date.
A slight flaw in your plan: Todo tasks only support descriptions.
Behold, your agenda. Let us examine the scale of this undertaking.
1. [T] [X] read book
2. [T] [ ] other
3. [E] [ ] trip (from: 01 Jan 2026 to: 02 Jan 2026)
Very well. Do come back. I mean, someone must supervise your progress.
```

## Test Case 11: Recover valid records from damaged storage

### Aim

Report all damaged records, retain later valid tasks, and block changes to protect the original file.

### Setup

```sh
mkdir -p data
cat > data/stewie.txt <<'EOF'
T | 0 | first
D | 0 | invalid | 2026-02-30
T | maybe | status
X | 0 | unknown
T | 0 | first
E | 0 | reverse | 2026-01-02 | 2026-01-01
T | 0 |
T | 0 | extra | field
T | 1 | last
EOF
```

### Inputs

```text
list
todo new
mark 1
unmark 2
delete 1
update 1 revised
list
bye
```

### Expected output

```text
███████╗ ████████╗ ███████╗ ██╗    ██╗ ██╗ ███████╗
██╔════╝ ╚══██╔══╝ ██╔════╝ ██║    ██║ ██║ ██╔════╝
███████╗    ██║    █████╗   ██║ █╗ ██║ ██║ █████╗
╚════██║    ██║    ██╔══╝   ██║███╗██║ ██║ ██╔══╝
███████║    ██║    ███████╗ ╚███╔███╔╝ ██║ ███████╗
╚══════╝    ╚═╝    ╚══════╝  ╚══╝╚══╝  ╚═╝ ╚══════╝

Ah, there you are. I'm Stewie.
Tell me your tasks. Clearly, this operation requires supervision.
Storage warning: Invalid or duplicate records at lines 2, 3, 4, 5, 6, 7, 8. Valid tasks are available read-only. Back up and repair the task file, then restart.
Behold, your agenda. Let us examine the scale of this undertaking.
1. [T] [ ] first
2. [T] [X] last
Tasks are read-only. Back up and repair the task file, then restart.
Tasks are read-only. Back up and repair the task file, then restart.
Tasks are read-only. Back up and repair the task file, then restart.
Tasks are read-only. Back up and repair the task file, then restart.
Tasks are read-only. Back up and repair the task file, then restart.
Behold, your agenda. Let us examine the scale of this undertaking.
1. [T] [ ] first
2. [T] [X] last
Very well. Do come back. I mean, someone must supervise your progress.
```

## Test Case 12: Handle a directory in place of the task file

### Aim

Explain invalid storage paths without crashing or replacing the directory.

### Setup

```sh
mkdir -p data/stewie.txt
```

### Inputs

```text
todo new
list
bye
```

### Expected output

```text
███████╗ ████████╗ ███████╗ ██╗    ██╗ ██╗ ███████╗
██╔════╝ ╚══██╔══╝ ██╔════╝ ██║    ██║ ██║ ██╔════╝
███████╗    ██║    █████╗   ██║ █╗ ██║ ██║ █████╗
╚════██║    ██║    ██╔══╝   ██║███╗██║ ██║ ██╔══╝
███████║    ██║    ███████╗ ╚███╔███╔╝ ██║ ███████╗
╚══════╝    ╚═╝    ╚══════╝  ╚══╝╚══╝  ╚═╝ ╚══════╝

Ah, there you are. I'm Stewie.
Tell me your tasks. Clearly, this operation requires supervision.
Storage warning: Unable to read the task file. Tasks are read-only. Check the file path, permissions, and UTF-8 content, then restart.
Tasks are read-only. Back up and repair the task file, then restart.
Behold, your agenda. Let us examine the scale of this undertaking.
No tasks to show. How suspiciously serene. Try adding a task or checking your search.
Very well. Do come back. I mean, someone must supervise your progress.
```

## Test Case 13: Keep task state when saving is denied

### Aim

Reject all mutations without false success messages when an existing file is read-only.

### Setup

```sh
mkdir -p data
printf 'T | 0 | first\nT | 1 | second\n' > data/stewie.txt
chmod 444 data/stewie.txt
```

### Inputs

```text
list
todo new
mark 1
unmark 2
delete 1
update 1 revised
list
bye
```

### Expected output

```text
███████╗ ████████╗ ███████╗ ██╗    ██╗ ██╗ ███████╗
██╔════╝ ╚══██╔══╝ ██╔════╝ ██║    ██║ ██║ ██╔════╝
███████╗    ██║    █████╗   ██║ █╗ ██║ ██║ █████╗
╚════██║    ██║    ██╔══╝   ██║███╗██║ ██║ ██╔══╝
███████║    ██║    ███████╗ ╚███╔███╔╝ ██║ ███████╗
╚══════╝    ╚═╝    ╚══════╝  ╚══╝╚══╝  ╚═╝ ╚══════╝

Ah, there you are. I'm Stewie.
Tell me your tasks. Clearly, this operation requires supervision.
Behold, your agenda. Let us examine the scale of this undertaking.
1. [T] [ ] first
2. [T] [X] second
Unable to save tasks. No changes were applied. Check the task file, permissions, free space, and support for atomic file replacement.
Unable to save tasks. No changes were applied. Check the task file, permissions, free space, and support for atomic file replacement.
Unable to save tasks. No changes were applied. Check the task file, permissions, free space, and support for atomic file replacement.
Unable to save tasks. No changes were applied. Check the task file, permissions, free space, and support for atomic file replacement.
Unable to save tasks. No changes were applied. Check the task file, permissions, free space, and support for atomic file replacement.
Behold, your agenda. Let us examine the scale of this undertaking.
1. [T] [ ] first
2. [T] [X] second
Very well. Do come back. I mean, someone must supervise your progress.
```

## Test Case 14: Handle denied reads

### Aim

Do not treat unreadable storage as an empty writable task file.

### Setup

```sh
mkdir -p data
printf 'T | 0 | secret\n' > data/stewie.txt
chmod 000 data/stewie.txt
```

### Inputs

```text
todo new
list
bye
```

### Expected output

```text
███████╗ ████████╗ ███████╗ ██╗    ██╗ ██╗ ███████╗
██╔════╝ ╚══██╔══╝ ██╔════╝ ██║    ██║ ██║ ██╔════╝
███████╗    ██║    █████╗   ██║ █╗ ██║ ██║ █████╗
╚════██║    ██║    ██╔══╝   ██║███╗██║ ██║ ██╔══╝
███████║    ██║    ███████╗ ╚███╔███╔╝ ██║ ███████╗
╚══════╝    ╚═╝    ╚══════╝  ╚══╝╚══╝  ╚═╝ ╚══════╝

Ah, there you are. I'm Stewie.
Tell me your tasks. Clearly, this operation requires supervision.
Storage warning: Unable to read the task file. Tasks are read-only. Check the file path, permissions, and UTF-8 content, then restart.
Tasks are read-only. Back up and repair the task file, then restart.
Behold, your agenda. Let us examine the scale of this undertaking.
No tasks to show. How suspiciously serene. Try adding a task or checking your search.
Very well. Do come back. I mean, someone must supervise your progress.
```

## Test Case 15: Handle invalid UTF-8

### Aim

Reject undecodable file content and protect the original bytes.

### Setup

```sh
mkdir -p data
printf '\377' > data/stewie.txt
```

### Inputs

```text
todo new
list
bye
```

### Expected output

```text
███████╗ ████████╗ ███████╗ ██╗    ██╗ ██╗ ███████╗
██╔════╝ ╚══██╔══╝ ██╔════╝ ██║    ██║ ██║ ██╔════╝
███████╗    ██║    █████╗   ██║ █╗ ██║ ██║ █████╗
╚════██║    ██║    ██╔══╝   ██║███╗██║ ██║ ██╔══╝
███████║    ██║    ███████╗ ╚███╔███╔╝ ██║ ███████╗
╚══════╝    ╚═╝    ╚══════╝  ╚══╝╚══╝  ╚═╝ ╚══════╝

Ah, there you are. I'm Stewie.
Tell me your tasks. Clearly, this operation requires supervision.
Storage warning: Unable to read the task file. Tasks are read-only. Check the file path, permissions, and UTF-8 content, then restart.
Tasks are read-only. Back up and repair the task file, then restart.
Behold, your agenda. Let us examine the scale of this undertaking.
No tasks to show. How suspiciously serene. Try adding a task or checking your search.
Very well. Do come back. I mean, someone must supervise your progress.
```
