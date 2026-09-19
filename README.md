# Leo

Leo is a friendly desktop chatbot that helps you track todos, deadlines, and events through simple typed commands or a JavaFX GUI, keeping your list saved between sessions. See the [User Guide](docs/README.md) for how to use it.

This project was built from a greenfield Java project template (originally named after the Java mascot _Duke_). Given below are instructions on how to set it up.

## Setting up in Intellij

Prerequisites: JDK 25, update Intellij to the most recent version. (Note: this is only for development - end users running the packaged jar just need Java 17+, per the User Guide.)

1. Open Intellij (if you are not in the welcome screen, click `File` > `Close Project` to close the existing project first)
1. Open the project into Intellij as follows:
   1. Click `Open`.
   1. Select the project directory, and click `OK`.
   1. If there are any further prompts, accept the defaults.
1. Configure the project to use **JDK 25** (not other versions) as explained in [here](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk).<br>
   In the same dialog, set the **Project language level** field to the `SDK default` option.
1. After that, locate the `src/main/java/leo/Leo.java` file, right-click it, and choose `Run Leo.main()` (if the code editor is showing compile errors, try restarting the IDE). If the setup is correct, you should see Leo's welcome banner printed to the console.

**Warning:** Keep the `src\main\java` folder as the root folder for Java files (i.e., don't rename those folders or move Java files to another folder outside of this folder path), as this is the default location some tools (e.g., Gradle) expect to find Java files.

## Acknowledgements

This project was built with assistance from Claude (Anthropic), used during development to help implement, test, and debug features. No third-party code was copied from other individuals or projects.
