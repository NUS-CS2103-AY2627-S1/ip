# Manual GUI Test Plan

## Purpose

Use this plan for visual and operating-system behavior that is difficult or unreliable to verify
with JUnit. Record the application version or commit, tester, date, operating system, Java version,
display resolution, display scaling, and OS language for every run.

Build the distributable JAR with Java 25 by running `gradlew.bat clean shadowJar` on Windows or
`./gradlew clean shadowJar` on macOS and Linux. Create a disposable working folder, create an empty
`data/info.txt` below it, change into that working folder, and launch the JAR using its
absolute path, for example `java -jar C:\path\to\project\build\libs\panda.jar`. Panda resolves its
data file from the working folder, so this setup protects the normal project data while exercising
the same GUI and storage behavior. Recreate the disposable folder when a test requires a clean
start.

For every test case, record `Pass`, `Fail`, or `Blocked`. For a failure, attach a screenshot and note
the exact command, visible result, expected result, and environment.

## Core window and startup

### Test Case: First launch

Aim: Verify that the application opens cleanly and presents the expected initial state.

Steps:

1. Launch Panda with an empty data file.
2. Do not interact with the window for five seconds.
3. Inspect the title bar, welcome dialog, input field, Send button, background, and taskbar or dock.

Expected result:

- Exactly one Panda window opens with the title `Panda`.
- The welcome dialog contains the short greeting: `Ready to tackle the bamboo pile? (^_^)`.
- A 40 px circular Panda avatar appears beside replies; user command chips have no avatar.
- The input field shows `Tell Panda what to do...`; the Send button is visible and enabled.
- The bamboo artwork appears in the header; the conversation uses an off-white reading surface.
- Panda replies use white and green bubbles; user commands use compact dark-green chips.
- No console, exception dialog, blank scene, missing-image icon, or rendering corruption appears.

### Test Case: Minimum window size

Aim: Verify that the configured minimum size remains usable.

Steps:

1. Resize the window as small as the operating system allows.
2. Enter `todo read book` and then `list`.
3. Inspect and scroll through every dialog.

Expected result:

- The window does not become smaller than approximately 360 by 320 logical pixels.
- The input field and Send button remain fully visible and do not overlap.
- Dialog text wraps within the conversation area without being cut off horizontally.
- A vertical scrollbar makes all earlier messages reachable.

### Test Case: Resize and maximize

Aim: Verify layout behavior across common window sizes.

Steps:

1. Check the initial size, a medium resized window, and a maximized window.
2. Repeat on 1366×768 and 1920×1080 displays when available.
3. Add several tasks and resize the window while dialogs are visible.

Expected result:

- The off-white conversation surface fills the window at every size.
- Check 360 by 320, 460 by 620, and 800 by 700; long text wraps without horizontal clipping.
- Bamboo tiles retain their proportions in the compact header.
- The input field expands while the Send button remains anchored at the lower right.
- The conversation area stays above the input controls.
- Background tiling is visually continuous; dialog text and images remain sharp and aligned.

## Input and command interaction

### Test Case: Keyboard submission and focus

Aim: Verify efficient keyboard-only command entry.

Steps:

1. Click the input field, type `todo keyboard task`, and press Enter.
2. Immediately type `list` without clicking the field again and press Enter.
3. Press Tab between the input field and Send control; activate Send with the keyboard.

Expected result:

- Enter submits each command exactly once.
- The input field clears after success; errors retain the command for editing.
- Tab focus is visibly identifiable and reaches the input field and Send button in a sensible order.
- Activating Send from the keyboard produces the same result as pressing Enter.

### Test Case: Mouse submission and button states

Aim: Verify Send button interaction and its visual feedback.

Steps:

1. Type `todo mouse task` and hover over Send.
2. Press and hold the primary mouse button, then release it.
3. Repeat once using a touchpad if available.

Expected result:

- The button changes appearance for hover and pressed states.
- One click produces exactly one user dialog and one Panda response.
- The input field clears after success, and no duplicate task is created.

### Test Case: Empty and invalid commands

Aim: Verify that error responses fit and remain readable in the GUI.

Steps:

1. Submit an empty input.
2. Submit `find`, `mark bamboo`, `delete 99`, and `unknown command`.
3. Submit a valid `todo recovery task` command afterward.

Expected result:

- Every command creates a user dialog and one readable Panda response.
- Each invalid command displays a pale-red card, dark-red text, a strong left accent, and `(>_<)`.
- The original error message remains readable, including for a task-saving failure.
- Long error text wraps without overlapping the avatar or window edge.
- The valid command succeeds after the errors.

## Dialog layout and scrolling

### Test Case: Speaker alignment and styling

Aim: Verify that user and Panda messages are visually distinguishable.

Steps:

1. Submit `todo visual task`, `mark 1`, and `list`.
2. Compare each user dialog with its Panda response.

Expected result:

- User commands are right aligned without an avatar.
- Panda messages are left aligned with Panda's image on the left.
- Panda response labels use the white-and-green reply styling and readable text.
- The first response line ends with `(^_^)`, `(>_<)` for errors, or `(^_^)/` for farewell.
- Completion markers, task type markers, indentation, and line breaks align consistently.

### Test Case: Long conversation and automatic scrolling

Aim: Verify that new responses remain visible while old responses remain accessible.

Steps:

1. Add at least 30 short tasks one at a time.
2. Confirm after each submission that the newest response is visible.
3. Scroll to the first welcome message, then submit another command.
4. Drag the scrollbar thumb and use mouse-wheel or touchpad scrolling.

Expected result:

- The view follows newly appended dialogs to the bottom.
- Scrolling stays responsive and does not flicker or jump unexpectedly.
- The welcome message and every earlier command remain reachable.
- The scrollbar is visible when needed and its thumb can be operated normally.

### Test Case: Long and multilingual text

Aim: Verify wrapping and Unicode rendering.

Steps:

1. Add a task with at least 250 characters, including spaces and punctuation.
2. Add `todo 阅读报告 🐼` and `todo café résumé`.
3. Run `list`, close Panda, reopen it, and run `list` again.

Expected result:

- Long text wraps inside its bubble without horizontal scrolling or clipping.
- Chinese characters, accented Latin characters, and the emoji render or fall back cleanly.
- Text remains identical after restarting, including all Unicode characters.
- Avatar placement and bubble widths remain correct for wrapped messages.

## Display scaling and operating-system settings

### Test Case: Display scaling

Aim: Verify visual usability at common high-DPI settings.

Steps:

1. Run the startup, resize, and long-text cases at 100%, 125%, 150%, and 200% scaling where the
   operating system supports those values.
2. Sign out or restart the application after changing scaling if required by the OS.

Expected result:

- Text, controls, borders, and images remain sharp and proportionate.
- No control is clipped at the minimum window size.
- The Send label, prompt text, and scrollbar remain legible and operable.

### Test Case: English and Chinese OS settings

Aim: Verify that OS locale and input settings do not alter Panda's formats or break text entry.

Steps:

1. Run Panda with the OS display language and regional format set to English.
2. Repeat with Simplified or Traditional Chinese settings.
3. In each setting, add a dated task and enter Chinese text with an input method editor.

Expected result:

- Panda's English labels and messages remain complete and readable.
- Dates continue to display with English month abbreviations such as `Sep`.
- Chinese input composition works normally; Enter confirms the intended command only once.
- Stored and reloaded text is unchanged.

## Persistence and lifecycle

### Test Case: Data path portability

Aim: Verify storage under paths that commonly expose platform issues.

Steps:

1. Run Panda using a disposable data location whose path contains spaces.
2. Repeat using a path containing Chinese characters.
3. Add, mark, update, and delete tasks; restart after each kind of change.

Expected result:

- Panda starts without a path-related error.
- Every change is present after restart, and deleted tasks stay deleted.
- No incorrectly named extra data file is created.

### Test Case: Graceful exit

Aim: Verify farewell rendering and shutdown behavior.

Steps:

1. Enter `bye` with both keyboard and mouse submission in separate launches.
2. Watch the controls and farewell dialog closely.
3. Attempt to click Send or type during the short farewell delay.

Expected result:

- The farewell dialog appears before the window closes.
- The input field and Send button become disabled immediately.
- The window closes after approximately 0.75 seconds without hanging.
- The application process exits and leaves no duplicate window or background process.

### Test Case: Window-manager close

Aim: Verify closing through the operating system rather than the `bye` command.

Steps:

1. Add a task and wait for its confirmation.
2. Close the window using the title-bar close control or the OS keyboard shortcut.
3. Reopen Panda and list tasks.

Expected result:

- The application closes without an exception or hung process.
- The task saved before closing is present after restart.

## Cross-platform execution matrix

Run at least the first-launch, resize, keyboard, mouse, long-conversation, Unicode, persistence, and
exit cases on each supported platform:

| Platform | Minimum environment | Extra observation |
| --- | --- | --- |
| Windows | Current supported Windows, Java 25 | Test 125% and 150% scaling and a path containing spaces. |
| macOS | Current supported macOS, Java 25 | Test Retina scaling, Return key submission, and Command-Q/window close. |
| Linux | Ubuntu runner equivalent and one desktop session, Java 25 | Test at 100% and 200% scaling and verify font fallback. |

CI proves that automated checks execute on all three systems. This manual matrix verifies native
window decoration, fonts, pointer behavior, display scaling, and actual visual rendering.



## Mobile-style task presentation

- Header: Panda icon, title, and companion subtitle inside one solid white rounded bubble. No menu.
- Enter commands directly; there are no Add task, Today, or Find buttons. Press Enter or the upward-arrow Send button.
- Run an empty list: show the clear-bamboo-patch message. Enter `todo first task` to create the first task.
- Add a to-do, deadline, and event with long names. Run list and find: each result has its original
  number, type, Open/Done status, prominent title, and separate timing for dated tasks.
- To-dos are soft green, deadlines amber, and events lavender; their text labels identify the type too.
- Add and delete each type: Task added / Task removed headings identify the action, the affected
  task has its own card, and the task count is a separate caption. Removed cards have a rose tint.
- Invalid commands stay in the composer for editing; a red hint appears beside it and clears after
  a successful submission. Error cards retain the full error message.
- Check 360 by 320 and maximized windows: header, composer, and task text remain usable.
- Keyboard focus, English/Chinese text, and 125–200% scaling follow the matrix above.
- Confirm a brief reply fade does not block typing, scrolling, or bye shutdown.


### Completion cards

Enter `todo read book`, `mark 1`, and `unmark 1` in a fresh session.
Mark shows a green Task completed card with Done status and the title read book.
Unmark shows a blue Task reopened card with Open status and the same title.
Neither title contains the console marker or leading indentation. Repeat with long task names,
deadlines, and events; the name should wrap normally. At minimum window size and high DPI,
the complete header should remain readable inside one white bubble, with bamboo only around its edges.
