# Jason project template

Jason is a command-line task manager with an optional JavaFX graphical interface. The text-based interface remains the default because it is convenient for automated testing.

## Setting up in Intellij

Prerequisites: JDK 25, update Intellij to the most recent version.

1. Open IntelliJ (if you are not in the welcome screen, click `File` > `Close Project` to close the existing project first)
1. Open the project into Intellij as follows:
   1. Click `Open`.
   1. Select the project directory, and click `OK`.
   1. If there are any further prompts, accept the defaults.
1. Configure the project to use **JDK 25** (not other versions) as explained in [here](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk).<br>
   In the same dialog, set the **Project language level** field to the `SDK default` option.
1. After that, locate `src/main/java/jason/Main.java`, right-click it, and choose **Run `Main.main()`** to start the text-based application.

## Running the application

From the project root, use Gradle:

```bash
./gradlew run
```

The GUI is optional and can be launched with:

```bash
./gradlew runGui
```

The GUI supports the same Jason commands as the CLI. Enter `bye` to close the GUI window. The GUI-specific code is in the `jason.gui` package, while the existing CLI remains the default application entry point.

## Building and testing

Run the complete verification suite, including unit tests and Checkstyle, with:

```bash
./gradlew check
```

Run the documented command-line UI tests with:

```bash
python3 .codex/skills/test-ui/scripts/run_ui_tests.py test/ui-test-plan.md
```

Application data is stored in `data/jason.txt`. The directory is created automatically when Jason saves tasks.

## Project structure

- `src/main/java/jason` — command-line application and core task logic.
- `src/main/java/jason/gui` — optional JavaFX GUI and controllers.
- `src/main/resources/view` — FXML layout files.
- `src/main/resources/css` — JavaFX stylesheets.
- `src/main/resources/images` — GUI avatar images.
- `src/test/java` — JUnit tests.
- `test/ui-test-plan.md` — command-line UI test cases.

**Warning:** Keep the `src\main\java` folder as the root folder for Java files (i.e., don't rename those folders or move Java files to another folder outside of this folder path), as this is the default location some tools (e.g., Gradle) expect to find Java files.
