# Minty

Minty is a chatbot developed as a greenfield Java project.

Use `reminders` (or `remind`) to view incomplete deadlines due and events starting
today through six days later. Qualifying reminders also appear at startup.
See the [user guide](docs/README.md#reminders) for details.

## Setting up in Intellij

Prerequisites: JDK 25, update Intellij to the most recent version.

1. Open Intellij (if you are not in the welcome screen, click `File` > `Close Project` to close the existing project first)
1. Open the project into Intellij as follows:
   1. Click `Open`.
   1. Select the project directory, and click `OK`.
   1. If there are any further prompts, accept the defaults.
1. Configure the project to use **JDK 25** (not other versions) as explained in [here](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk).<br>
   In the same dialog, set the **Project language level** field to the `SDK default` option.
1. Reload the Gradle project so IntelliJ downloads the JavaFX dependencies.
1. Run `./gradlew run` in the terminal. If the setup is correct, Minty's chat window will appear.

**Warning:** Keep the `src\main\java` folder as the root folder for Java files (i.e., don't rename those folders or move Java files to another folder outside of this folder path), as this is the default location some tools (e.g., Gradle) expect to find Java files.
