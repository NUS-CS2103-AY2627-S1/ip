# WALLE

WALLE is a desktop chatbot for tracking tasks and notes, with both a command-line interface and a JavaFX GUI.

## Setting up in Intellij

Prerequisites: JDK 25, update Intellij to the most recent version.

1. Open Intellij (if you are not in the welcome screen, click `File` > `Close Project` to close the existing project first)
1. Open the project into Intellij as follows:
   1. Click `Open`.
   1. Select the project directory, and click `OK`.
   1. If there are any further prompts, accept the defaults.
1. Configure the project to use **JDK 25** (not other versions) as explained in [here](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk).<br>
   In the same dialog, set the **Project language level** field to the `SDK default` option.
1. This project uses Gradle. Run the GUI via the `run` Gradle task (or `./gradlew run` from a terminal), or run `src/main/java/walle/WALLE.java`'s `main` method directly for the command-line interface.

**Warning:** Keep the `src\main\java` folder as the root folder for Java files (i.e., don't rename those folders or move Java files to another folder outside of this folder path), as this is the default location some tools (e.g., Gradle) expect to find Java files.

## Acknowledgements

This project made extensive use of [Claude Code](https://claude.com/claude-code) (Claude Sonnet 5, Anthropic) throughout development — including planning and implementing features (e.g. GUI styling, error handling), writing commit messages, and git/GitHub workflow. All AI-assisted changes were reviewed and verified (build, tests, manual testing) by the author before being committed.
