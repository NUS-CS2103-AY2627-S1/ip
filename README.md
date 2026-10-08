# Rudra

Rudra is a Y2K-inspired task chatbot with a JavaFX interface. Given below are instructions on how to use it.

## Setting up in Intellij

Prerequisites: JDK 25, update Intellij to the most recent version.

1. Open Intellij (if you are not in the welcome screen, click `File` > `Close Project` to close the existing project first)
1. Open the project into Intellij as follows:
   1. Click `Open`.
   1. Select the project directory, and click `OK`.
   1. If there are any further prompts, accept the defaults.
1. Configure the project to use **JDK 25** (not other versions) as explained in [here](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk).<br>
   In the same dialog, set the **Project language level** field to the `SDK default` option.
1. Run `./gradlew run`, or locate `src/main/java/rudra/Launcher.java` and choose `Run Launcher.main()`. This opens Rudra's JavaFX chat window.
1. Enter the existing commands in the message field, for example `todo borrow book`, `list`, or `deadline submit report /by 2026-09-10`.

The GUI is launched through `Launcher`, which starts the separate JavaFX `Application` class. `rudra.Rudra` remains the console entry point used by the scripted console tests.

To build the cross-platform application JAR, run `./gradlew shadowJar`. The resulting file is `build/libs/rudra.jar`.

## Error handling

Rudra keeps running after invalid or incomplete commands and explains how to correct them. If the save file is absent,
Rudra starts with an empty task list and creates the file when a task is first saved. Malformed saved records are
skipped with a warning, while a read or write failure is reported without silently changing the in-memory task list.

## Acknowledgements

Rudra was developed from the [SE-EDU Duke starter project](https://github.com/se-edu/duke). Credit for the starter
code and project setup belongs to its original contributors, who are listed in [CONTRIBUTORS.md](CONTRIBUTORS.md).
The application uses [OpenJFX](https://openjfx.io/) and the Gradle Wrapper; their generated and library code retain
their respective notices and licenses.

**Warning:** Keep the `src\main\java` folder as the root folder for Java files (i.e., don't rename those folders or move Java files to another folder outside of this folder path), as this is the default location some tools (e.g., Gradle) expect to find Java files.
