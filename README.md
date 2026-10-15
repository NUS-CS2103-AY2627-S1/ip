# Orbit task chatbot

Orbit is a calm, mission-control-themed task chatbot with command-line and JavaFX interfaces.

See the [Orbit User Guide](docs/README.md) for the complete command reference.

## Setting up in Intellij

Prerequisites: JDK 25, update Intellij to the most recent version.

1. Open Intellij (if you are not in the welcome screen, click `File` > `Close Project` to close the existing project first)
1. Open the project into Intellij as follows:
   1. Click `Open`.
   1. Select the project directory, and click `OK`.
   1. If there are any further prompts, accept the defaults.
1. Configure the project to use **JDK 25** (not other versions) as explained in [here](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk).<br>
   In the same dialog, set the **Project language level** field to the `SDK default` option.
1. After that, locate `src/main/java/stephen/Launcher.java`, right-click it, and choose
   `Run Launcher.main()` to start the GUI. If the code editor shows compile errors, reload
   the Gradle project or restart the IDE.

## Running the application

Use JDK 25 for every command below. The Gradle wrapper downloads the required JavaFX modules
for Windows, macOS, and Linux.

Launch the JavaFX GUI:

```powershell
.\gradlew.bat run
```

The input box supports Enter to send and Shift+Enter to insert a new line.

Run the command-line interface:

```powershell
.\gradlew.bat runCli
```

If the setup is correct, the CLI starts with:

```text
Orbit online.
Ready to plan your next move?
```

The standalone JavaFX learning/setup check is retained separately and can be launched with:

```powershell
.\gradlew.bat runHelloWorld
```

**Warning:** Keep the `src\main\java` folder as the root folder for Java files (i.e., don't rename those folders or move Java files to another folder outside of this folder path), as this is the default location some tools (e.g., Gradle) expect to find Java files.

## Checking code style

Run Checkstyle on both the application and test code from the project folder:

```powershell
.\gradlew.bat checkstyleMain checkstyleTest
```

The rules in `config/checkstyle/checkstyle.xml` enforce the mechanically checkable
parts of the [SE-EDU Java coding standard](https://se-education.org/guides/conventions/java/intermediate.html).
Fix reported violations before submitting code.

## Running tests

Run all automated tests and code-style checks:

```powershell
.\gradlew.bat check
```

Run the exact-output command-line UI tests after compiling:

```powershell
.\gradlew.bat classes
.\scripts\run_ui_tests.ps1 -JavaExecutable (Get-Command java).Source `
    -ClassPath build/classes/java/main -MainClass stephen.Stephen `
    -CasesFile test/ui-test-cases.json
```

## Finding tasks

Enter `find KEYWORD` to display tasks whose descriptions contain the keyword or phrase.
Matching is case-insensitive. For example, `find read book` finds a task described as
`Read Book`.

## Acknowledgements

- The project author used **OpenAI Codex** throughout the project for brainstorming,
  implementation assistance, refactoring, test generation, documentation, and code review.
  All generated suggestions were reviewed and adapted before being included.
- The project is based on the SE-EDU individual-project starter and course materials.
- [OpenJFX](https://openjfx.io/) provides the JavaFX graphical interface.
- [JUnit 5](https://junit.org/junit5/) provides the automated testing framework.
- The Gradle Shadow plugin packages the executable JAR, while Checkstyle enforces the
  project's Java coding standard.
