# LuckyNoSlacky GUI test plan

## Test execution information

- Framework: TestFX with JUnit 5
- Java version: Java 25
- Automated test command: `./gradlew guiTest`
- Comparison: assertions on observable JavaFX state and displayed controls
- Failure policy: stop at the first failed GUI test

## Test Case: DialogueBox displays user messages

- Aim: Verify that user messages use a compact right-aligned bubble and
  circular user avatar.
- Test: `DialogueBoxTest.dialogueBox_userMessage_displaysCircularAvatarOnRight`
- Expected result: The row is right-aligned, displays only the raw message
  text, applies the user dialogue CSS style, displays the existing user image
  inside a 48-pixel circular avatar frame, and exposes the accessible text
  `You: <message>` without making the avatar keyboard-focusable.

## Test Case: DialogueBox displays chatbot messages

- Aim: Verify that application messages use a wider left-aligned bubble.
- Test: `DialogueBoxTest.dialogueBox_chatbotMessage_displaysBubbleOnLeft`
- Expected result: The row is left-aligned, preserves the application message
  text, applies the chatbot dialogue CSS style, displays the existing chatbot
  image inside a circular avatar frame with the green accent, and exposes the
  accessible text `LuckyNoSlacky: <message>`.

## Test Case: DialogueBox displays warning messages

- Aim: Verify that application warnings use a distinct visual style.
- Test: `DialogueBoxTest.dialogueBox_warningMessage_appliesWarningStyle`
- Expected result: The warning is left-aligned and applies the warning
  dialogue CSS style, exposes the drafted warning accessible text, and shows a
  visible warning marker.

## Test Case: DialogueBox displays successful responses

- Aim: Verify that successful task changes use a clover-green chatbot bubble.
- Test: `DialogueBoxTest.dialogueBox_successMessage_displaysCloverMarker`
- Expected result: The response remains left-aligned, applies the success
  dialogue CSS style, shows the green personality treatment, and shows a
  bundled success-marker image without changing the response text.

## Test Case: DialogueBox displays information responses

- Aim: Verify that task lists and search results use the information tone.
- Test: `DialogueBoxTest.dialogueBox_infoMessage_appliesInfoStyle`
- Expected result: The response remains left-aligned, applies the information
  dialogue CSS style, and does not add a tone prefix or marker to the message.

## Test Case: DialogueBox displays system errors

- Aim: Verify that storage failures use a stronger non-warning error style.
- Test: `DialogueBoxTest.dialogueBox_systemErrorMessage_displaysErrorMarker`
- Expected result: The response remains left-aligned, applies the system-error
  dialogue CSS style, and shows the bundled system-error marker image without
  changing the response text.

## Test Case: Main window exposes accessible labels

- Aim: Verify that the conversation history, command field, and Send button
  expose the drafted labels from the accessibility specification.
- Test: `MainWindowInteractionTest.mainWindow_controls_exposeAccessibleLabels`
- Expected result: Each control exposes its configured accessible text and the
  command field and Send button expose their keyboard-use help text.

## Test Case: Main window exposes visual integration regions

- Aim: Verify that future personality visuals can be inserted without
  replacing the functional conversation or command regions.
- Test: `MainWindowLayoutTest.mainWindow_layoutRegions_exposeStableIntegrationHooks`
- Expected result: The production layout exposes a visible managed brand
  header slot, a conversation region, and a command row with stable IDs and
  CSS classes. The integrated header slot contains the LuckyNoSlacky title,
  tagline, existing chatbot image, and bundled clover accent image; the
  conversation region also exposes the personality background layer.

## Test Case: Main window focuses the command field on startup

- Aim: Verify that keyboard users can start typing immediately.
- Test: `MainWindowInteractionTest.mainWindow_startApplication_focusesCommandInput`
- Expected result: The command field has focus after the window opens.

## Test Case: Main window supports forward keyboard traversal

- Aim: Verify that Tab follows the specified focus order.
- Test: `MainWindowInteractionTest.mainWindow_tabTraversal_movesForwardThroughControls`
- Expected result: Focus moves from command input to Send, then conversation
  history, then back to command input.

## Test Case: Main window supports reverse keyboard traversal

- Aim: Verify that Shift+Tab reverses the specified focus order.
- Test: `MainWindowInteractionTest.mainWindow_tabTraversal_movesBackwardThroughControls`
- Expected result: Focus moves from conversation history to Send, then back to
  command input.

## Test Case: Send-button submission restores focus

- Aim: Verify that keyboard users can continue typing after using Send.
- Test: `MainWindowInteractionTest.mainWindow_sendButtonSubmission_returnsFocusToCommandInput`
- Expected result: Focus returns to the command field after a Send-button
  submission.

## Test Case: Main window displays a conversation

- Aim: Verify that entering an unrecognised command creates a user row and a
  warning response row.
- Test: `MainWindowInteractionTest.mainWindow_unknownCommand_displaysUserAndWarningMessages`
- Actions: Enter `unknown` in the command field and submit it.
- Expected result: The conversation contains the greeting, a right-aligned
  user row containing `unknown`, and a left-aligned warning row containing the
  actual unknown-command response. The user row does not contain `You said:`.

## Test Case: Main window assigns response tones

- Aim: Verify that the GUI receives semantic response tones from the chatbot.
- Test: `MainWindowInteractionTest.mainWindow_unknownCommand_displaysUserAndWarningMessages`
- Expected result: Invalid input uses the warning style while task changes,
  lists, and searches use their corresponding success or information styles.

## Test Case: Main window remains usable after a normal command

- Aim: Verify that a non-exit command clears the input field without disabling
  it.
- Test: `MainWindowInteractionTest.mainWindow_unknownCommand_keepsInputEnabled`
- Actions: Enter `unknown` in the command field and submit it.
- Expected result: The input field is empty and remains enabled.

## Test Case: Main window submits with the Send button

- Aim: Verify that clicking the Send button submits a command through the same
  handler as pressing Enter.
- Test: `MainWindowInteractionTest.mainWindow_sendButton_submitsCommand`
- Actions: Enter `unknown` and click `Send`.
- Expected result: A user dialogue row and chatbot response row are added.

## Test Case: Main window scrolls to the latest dialogue

- Aim: Verify that a long conversation automatically reveals its newest
  dialogue.
- Test: `MainWindowLayoutTest.mainWindow_manyMessages_scrollsToLatestDialogue`
- Actions: Submit 20 deterministic commands through the input field.
- Expected result: All dialogue rows are retained and the scroll pane is at
  its maximum vertical position.

## Test Case: Main window ignores blank input

- Aim: Verify that submitting an empty command does not add dialogue rows.
- Test: `MainWindowInteractionTest.mainWindow_blankInput_doesNotAddDialogue`
- Actions: Click `Send` without entering a command.
- Expected result: The greeting remains the only dialogue row.

## Test Case: Main window remains usable after a load failure

- Aim: Verify that the GUI enters degraded empty-list mode when task data
  cannot be loaded.
- Test: `MainWindowInteractionTest.mainWindow_loadFailure_remainsInteractive`
- Actions: Launch the main window with a chatbot reporting a startup load
  failure, then enter `list` and submit it.
- Expected result: The greeting and loading-error message are displayed once,
  the input field and Send button remain enabled, and the `list` command adds
  the normal user and chatbot dialogue rows.

## Test Case: Main window remains usable after a capacity error

- Aim: Verify that rejecting a task because the task list is full does not
  disable the GUI.
- Test: `MainWindowInteractionTest.mainWindow_taskLimit_keepsControlsEnabled`
- Actions: Launch the main window with a full-list chatbot, then submit
  `todo overflow`.
- Expected result: The approved task-limit message is displayed, the rejected
  task is not added, and the input field and Send button remain enabled.

## Test Case: Main window displays goodbye before delayed exit

- Aim: Verify that the goodbye message remains visible while the application
  waits before exiting.
- Test: `MainWindowInteractionTest.mainWindow_byeCommand_displaysGoodbyeBeforeDelayedExit`
- Actions: Enter `bye` in the command field and submit it.
- Expected result: The goodbye message is displayed, both the input field and
  Send button are disabled, the window remains visible, and the exit callback
  runs only after the configured 1.5-second delay.

## Test Case: JavaFX application starts correctly

- Aim: Verify that the production JavaFX application creates the expected
  LuckyNoSlacky window.
- Test: `LuckyNoGuiTest.luckyNoGui_startApplication_createsConfiguredWindow`
- Actions: Launch the JavaFX application through its production startup code.
- Expected result: The window title is `LuckyNoSlacky`, its scene is 400 by
  600 pixels, and its root node is present.

## Test Case: GUI resources are packaged

- Aim: Verify that the FXML layout, CSS stylesheets, avatars, and task-card
  icons are available from the application classpath.
- Test: `LuckyNoGuiTest.luckyNoGui_resourcePaths_areAvailable`
- Expected result: The layout, both CSS stylesheets, chatbot avatar, user
  avatar, task-card icons, response-marker icons, branded header, clover
  pattern, and bundled fonts are all found, and the two personality font
  families are registered before FXML styling.

## Test Case: Cross-platform release JAR is packaged

- Aim: Verify that the release JAR starts the JavaFX launcher and contains the
  native JavaFX back ends needed by the supported operating systems.
- Test: Run `./gradlew clean shadowJar`, inspect the manifest and JAR contents,
  then launch `java -jar build/libs/luckyNoSlacky.jar` from an empty directory
  using Java 25.
- Expected result: `build/libs/luckyNoSlacky.jar` has
  `luckynoslacky.luckyui.gui.Launcher` as its main class and contains the
  Windows, Linux, and universal macOS JavaFX native libraries. The macOS
  libraries report both `x86_64` and `arm64` slices when inspected with
  `lipo -archs`. It opens the GUI, renders bundled task-card and response-tone
  icons at fixed sizes, closes after `bye`, and writes no `hs_err_pid` crash
  log.

## Test Case: DialogueBox displays structured task cards

- Aim: Verify that every formatted task line becomes a typed, status-aware
  card inside one response bubble.
- Test: `DialogueBoxTest.dialogueBox_taskContent_displaysTypedCardsAndAccessibleStatus`
- Expected result: TODO, deadline, and event lines are displayed in their
  original order with `TODO`, `DEADLINE`, and `EVENT` labels and their matching
  bundled task-type image assets.
  Numbered task lines from `list` and `find` display their existing task
  number as a `#<number>` badge in the top-right corner of the card.
  Incomplete and completed tasks show their matching bundled status image
  assets, and the card colours are pale blue, pale orange, and pale purple
  respectively. Todo cards omit a schedule row. Deadline cards show one `by`
  row, while event cards show separate `from` and `to` rows. The outer response
  bubble keeps its semantic tone colour. Unnumbered confirmation lines remain
  unnumbered.

## Test Case: DialogueBox provides accessible task descriptions

- Aim: Verify that decorative task images and internal markers are not read by
  a screen reader.
- Test: `DialogueBoxTest.dialogueBox_taskContent_displaysTypedCardsAndAccessibleStatus`
- Expected result: The outer response is exposed with descriptions such as
  `TODO, task 1, incomplete, <task name>`, `DEADLINE, task 2, incomplete,
  <task name>, by <date>`, and `EVENT, task 3, completed, <task name>, from
  <start>, to <end>`. The
  accessible text contains none of the CLI task emojis (`📌`, `⏳`, `📆`,
  `✅`, and `❗`), and the inner card nodes are not keyboard-focusable.
  Confirmation lines without task numbers do not receive an invented number.

## Test Case: DialogueBox leaves confirmations unnumbered

- Aim: Verify that task numbers are limited to numbered `list` and `find`
  output during this increment.
- Test: `DialogueBoxTest.dialogueBox_taskConfirmation_omitsTaskNumber`
- Expected result: A confirmation line such as `[📌][❗] buy groceries` keeps
  its task card but has no `#1` badge and no task number in its accessible
  description.

## Test Case: Main window preserves usable minimum dimensions

- Aim: Verify that the application prevents the window from becoming too small
  for the conversation and input controls.
- Test: `LuckyNoGuiTest.luckyNoGui_startApplication_createsConfiguredWindow`
- Actions: Inspect the primary stage minimum width and height.
- Expected result: The minimum dimensions are 320 by 480 pixels.

## Test Case: Main window provides responsive input controls

- Aim: Verify that the input field and Send button are placed in a responsive
  bottom control row.
- Test: Manual acceptance check
- Actions: Resize the window horizontally and vertically.
- Expected result: The input field expands with the window, the Send button
  remains at the bottom-right with a stable width, and message bubbles wrap
  without clipping or horizontal scrolling. A muted, low-contrast clover
  pattern fills the complete visible conversation viewport, including empty
  space below the newest message, and continues to do so after each resize.

## Test Case: Sample task conversation uses actual chatbot responses

- Aim: Verify that the GUI presents real LuckyNoSlacky task-management
  responses rather than invented assistant copy.
- Test: Manual acceptance check
- Actions: Enter the following commands in order:

  ```text
  todo buy groceries
  deadline submit report /by 20 Sep 2026 12pm
  list
  snooze 1 /by 2 days
  ```

- Expected result: The GUI displays the actual task confirmations, task list,
  and the warning `Eh mr blur sotong this task don't even have time for you to
  snooze la`. It does not display `Got it — I'll help you stay on track` or
  `You said:`.

## Integrated visual resources

The production GUI loads `personality.css` after `main.css`, loads Patrick Hand
and Roboto Mono before FXML construction, includes `BrandHeader.fxml` in the
visible header slot, and places the clover pattern behind the conversation.
These resources are visual dependencies only: command behavior, response
tones, accessibility text, focus traversal, and minimum window dimensions
remain owned and tested by BetterGUI.
- Test: `MainWindowLayoutTest.mainWindow_widenedWindow_expandsInputAndPreservesButtonWidth`
- Actions: Resize the window from 400 by 600 to 800 by 600.
- Expected result: The input field expands with the window, while the Send
  button remains at the bottom-right with a stable width.

## Test Case: Main window keeps controls usable at minimum size

- Aim: Verify that the input controls remain inside the scene at the minimum
  supported dimensions.
- Test: `MainWindowLayoutTest.mainWindow_minimumWindow_keepsControlsWithinScene`
- Actions: Resize the window to 320 by 480.
- Expected result: The input field and Send button remain visible, usable, and
  inside the scene bounds.

## Test Case: Main window preserves tasks across relaunch

- Aim: Verify that a task added through the GUI is restored after closing and
  relaunching the application.
- Test: `GuiPersistenceTest.luckyNoGui_taskSavedThenRelaunched_restoresTask`
- Actions: Add `todo persisted gui task`, close the stage, relaunch the GUI
  with the same isolated data file, then submit `list`.
- Expected result: The task appears in the relaunched conversation.

## Acceptance checks beyond `guiTest`

These optional checks supplement the deterministic Gradle task with visual
and end-to-end observations. They are non-blocking unless a failure reveals a
serious functional regression.

- Enter `bye` and verify that the goodbye message is visible before the window
  closes after the configured delay.
- Resize the window and verify that message text and avatars remain readable.
- Verify that both existing avatar assets are displayed as circular images.
- Add a task, close the GUI, relaunch it, and verify that the task persists.
- Add TODO, deadline, and event tasks in both incomplete and completed states;
  use `list` and `find` to verify the card colour, type label, completion icon,
  task number badge, and schedule rows. Confirm that command confirmations do
  not display invented task numbers.
- Use a screen reader to confirm that the drafted labels and message roles are
  announced meaningfully, without reading decorative task emojis.
- Enter `bye` and verify that both command controls become unavailable while
  the farewell remains visible before the delayed exit.
### Test environment

- Use Java 25 and the packaged GUI build.
- Run the checks from a disposable copy of the project.
- Use an isolated task-data file and do not modify normal application data.
- Record the tested commit or build version.
- Record screenshots only for failed or questionable checks.

### Manual check: Conversation scrolling

- Launch the GUI at 400 by 600 pixels.
- Submit at least 20 short commands, such as `unknown`.
- Confirm that the newest response remains visible after each submission.
- Scroll upward after the final response.
- Expected result: New content is shown automatically, earlier dialogue rows
  remain available, and no message, avatar, or horizontal layout is clipped.

### Manual check: Responsive sizing and readability

- Inspect the GUI at 400 by 600, 320 by 480, and 800 by 600 pixels.
- Inspect a shorter-than-default height when practical.
- Expected result: The input field and Send button remain visible and usable;
  the input expands horizontally; the Send button stays at the bottom-right;
  long messages wrap; and labels, text, and avatars remain readable without
  overlap or clipping.

### Manual check: Persistence after relaunch

- Start with the isolated task-data file empty.
- Add `todo manual persistence check`.
- Close the GUI using the window close control.
- Relaunch the GUI from the same disposable copy and data file.
- Submit `list`.
- Expected result: The task is restored once with the same description and
  type, and the relaunched GUI remains usable without unexpected errors.

## Test Case: GUI goodbye is visible before delayed exit

- Aim: Verify that the goodbye response and disabled-input state are observable
  before the GUI process exits.
- Test: `LuckyNoGuiSubprocessTest.guiProcess_byeCommand_delaysExitAfterShowingGoodbye`
- Actions: Launch an isolated GUI subprocess and submit `bye`.
- Expected result: The exact goodbye message is shown, the input is empty and
  disabled, the process remains alive for the configured delay, and then exits
  successfully.

## Test Case: GUI task persists across relaunches

- Aim: Verify persistence through two complete GUI application launches.
- Test: `LuckyNoGuiSubprocessTest.guiProcess_taskAcrossRelaunches_loadsPersistedTask`
- Actions: Launch the GUI in an isolated directory, create a task, terminate
  it, relaunch it in the same directory, and submit `list`.
- Expected result: The second process reports no startup load error and lists
  the task created by the first process.

### Manual check reporting

- Record a pass or fail result, window sizes, commands entered, and any
  scrolling, readability, persistence, or error observations.
- Capture screenshots only for failed or questionable checks.
- Document confirmed defects as follow-up increments.
- Verify the automated persistence scenario against a desktop build when
  visual confirmation is available.
