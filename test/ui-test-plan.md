# UI test plan

Run these cases from the repository root. The plan covers the console-facing UI behavior and JavaFX resource availability that can be checked without opening a window.

## Case 1: UI output behavior

- Aim: Verify the UI output test passes and preserves message ordering.
- Command:

  ```powershell
  .\gradlew.bat test --tests esther.ui.UiTest
  ```

- Input: `none`
- Expected output: `BUILD SUCCESSFUL`
- Match mode: `contains`
- Timeout: `120`

## Case 2: JavaFX resource availability

- Aim: Verify the UI layout, stylesheet, and image resources remain available on the classpath.
- Command:

  ```powershell
  .\gradlew.bat test --tests esther.gui.GuiResourceTest
  ```

- Input: `none`
- Expected output: `BUILD SUCCESSFUL`
- Match mode: `contains`
- Timeout: `120`

## Manual GUI smoke check

- Launch the JavaFX application and enter `bye`.
- Expected behavior: the farewell message `Byeee! Hope to see you soooon!`
  appears in the conversation, remains visible briefly, and then the window
  closes without an error.
