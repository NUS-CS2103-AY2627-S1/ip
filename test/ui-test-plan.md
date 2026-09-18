# UI Test Plan

Sophon uses a JavaFX graphical interface. Its GUI behavior is tested with TestFX and JUnit, while visual appearance
and operating-system differences are checked manually.

- Automated command: `.\gradlew.bat test --tests "sophon.ui.*Test"`

## Automated GUI Tests

Run all automated tests from the project root with Java 25:

```text
.\gradlew.bat test
```

Run only the JavaFX GUI tests:

```text
.\gradlew.bat test --tests "sophon.ui.*Test"
```

The automated GUI tests are in `src/test/java/sophon/ui/MainWindowTest.java`.

### GUI-01: Display the greeting

Aim: Verify that opening the main window displays Sophon's greeting.

Steps performed automatically:

1. Open the JavaFX main window.
2. Inspect the dialog container.
3. Verify that exactly one initial dialog is displayed.
4. Verify that the dialog contains `Hi. I'm Sophon.`.
5. Verify that the complete greeting remains visible instead of being shortened with an ellipsis.

### GUI-02: Send a todo

Aim: Verify that entering a todo through the GUI displays both the user message and Sophon's response.

Steps performed automatically:

1. Enter `todo read book` in the input field.
2. Click the Send button.
3. Verify that the user message is displayed.
4. Verify that Sophon displays the successful add response.

### GUI-03: Ignore blank input

Aim: Verify that whitespace-only input does not create new dialogs.

Steps performed automatically:

1. Enter three spaces in the input field.
2. Click the Send button.
3. Verify that only the initial greeting dialog remains.

### GUI-04: Submit with Enter

Aim: Verify that pressing Enter in the input field submits the message.

### GUI-05: Disable controls after bye

Aim: Verify that entering `bye` displays the complete two-line farewell and immediately disables the input field and
Send button.

The delayed window closing remains covered by MANUAL-03 so the automated suite does not pause for three seconds.

### GUI-06: Display dialog identities

Aim: Verify that user and Sophon dialogs have the correct sender labels, style classes, avatars, and left/right order.

### GUI-07: Configure the application window

Aim: Verify that the application window has the expected title, is resizable, and enforces its minimum dimensions.

## Automated Logic and Persistence Tests

The remaining JUnit tests cover parsing, task manipulation, search, response generation, saving, loading, malformed
save files, and non-JavaFX message formatting. They run as part of `gradlew test`.

## Manual Visual and Environment Tests

Record the date, operating system, display resolution, scaling, result, and any observations for each test session.

### MANUAL-01: Layout and readability

1. Start Sophon with `gradlew run`.
2. Verify that the greeting, input field, Send button, scrollbar, and avatars are visible and do not overlap.
3. Send short and long messages and verify that text wraps without being cut off.

### MANUAL-02: Scrolling

1. Add enough tasks or messages to exceed the visible window height.
2. Verify that the conversation scrolls to the latest message.
3. Verify that earlier messages remain reachable with the scrollbar.

### MANUAL-03: Exit behavior

1. Enter `bye`.
2. Verify that the farewell message appears.
3. Verify that the window closes after approximately three seconds.

### MANUAL-04: Display settings

Repeat MANUAL-01 using the available display resolutions and scaling settings, especially 100%, 125%, and 150%.
Record only the configurations that were actually tested.

### MANUAL-05: Operating-system and language settings

Run the application on each available operating system and, where practical, with English and Chinese system
language settings. Verify that Sophon starts, accepts input, saves tasks, and displays text correctly. Do not record an
environment as passed unless it was actually tested.
