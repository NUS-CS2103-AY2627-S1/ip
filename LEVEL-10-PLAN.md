# Level 10 GUI Plan

## Current state

- `build.gradle` already uses the JavaFX dependencies and `clara.gui.Launcher` entry point
  prescribed by the SE-EDU JavaFX tutorial.
- `clara.gui.Launcher` already follows the tutorial's JavaFX classpath workaround, but its
  `Main` target has not been created yet.
- `clara.Clara#main` currently owns the terminal input loop and directly prints through
  `clara.ui.Ui`. A JavaFX GUI needs a synchronous command-response method instead.

## Implementation steps

1. [Done] Extract Clara's command-processing and startup-loading logic into instance state and a
   public `getResponse(String)` method. This method will preserve the existing commands,
   persistence, task handling, and user-facing messages, but return the response for the GUI
   instead of printing it. `main` will be retained as the console entry point for now.
2. [Done] Add `clara.gui.Main`, extending `Application`. Following tutorial part 4, it will load
   `/view/MainWindow.fxml`, create the `Scene`, inject a `Clara` instance into the controller,
   and show the stage.
3. [Done] Add `clara.gui.MainWindow` as the FXML controller. It will bind the scroll position to the
   dialog container, pass user input to `Clara#getResponse`, and append paired user/Clara
   dialog boxes.
4. [Done] Add `clara.gui.DialogBox`, an `HBox` custom control. Following the tutorial, it will load
   its own FXML using the `fx:root` technique and flip Clara replies to the left.
5. [Done] Add tutorial-structured resources:
   `src/main/resources/view/MainWindow.fxml`, `DialogBox.fxml`, `css/main.css`, and
   `css/dialog-box.css`. The layout will use anchors, a `ScrollPane`, a dialog `VBox`, and
   Enter/Send actions. CSS will provide basic, readable message-bubble styling.
6. [Done] Add two local avatar images under `src/main/resources/images/`. These are original
   simple assets; no library or downloaded runtime asset is needed.
7. You will run `./gradlew test`, `./gradlew checkstyleMain`, and `./gradlew run` manually to
   verify compilation, style, and GUI startup. I will address any output you provide.

## Scope boundaries

- Preserve the existing command syntax and task-storage format.
- Do not add libraries or change the tutorial JavaFX/Gradle approach.
- Do not change build configuration unless a concrete incompatibility requires it; if so, stop
  and request approval first.

## Bye-command correction plan

1. [Done] Add a `shouldExit` state flag to `Clara`, initially `false`, and a public query method for the
   GUI and console entry points. This preserves the tutorial-compatible `getResponse(String)`
   return type while communicating the non-text outcome.
2. [Done] In the `bye` branch of `getResponse`, call `Parser.requireNoArguments` first. Set the flag only
   after that validation succeeds, then return the standard farewell message. Therefore,
   `bye anything` remains an error and never requests termination.
3. [Done] Update `Clara.main` to process every input through `getResponse` before checking the exit flag;
   it will print the reply and terminate only when the validated command requested it.
4. [Done] Update `MainWindow` to append the farewell dialog and then close the JavaFX application when
   Clara reports that the validated `bye` command requested exit.

No Gradle verification commands will be run; you will perform those manually as agreed.
