# PiplupBot

PiplupBot is a task manager with a Piplup personality. To learn how to use it, see the [User Guide](docs/README.md). Given below are instructions on how to set up the project (for developers).

## Setting up in IntelliJ

Prerequisites: JDK 25, update IntelliJ to the most recent version.

1. Open IntelliJ (if you are not in the welcome screen, click `File` > `Close Project` to close the existing project first)
1. Open the project into IntelliJ as follows:
   1. Click `Open`.
   1. Select the project directory, and click `OK`.
   1. If there are any further prompts, accept the defaults.
1. Configure the project to use **JDK 25** (not other versions) as explained [here](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk).<br>
   In the same dialog, set the **Project language level** field to the `SDK default` option.
1. After that, locate the `src/main/java/piplupbot/Launcher.java` file, right-click it, and choose `Run Launcher.main()` (if the code editor is showing compile errors, try restarting the IDE). If the setup is correct, a window titled **PiplupBot** opens and greets you.<br>
   To use the console instead, run `./gradlew run --args=--cli`. It starts with this banner:
   ```
    ____  _       _             ____        _   
   |  _ \(_)_ __ | |_   _ _ __ | __ )  ___ | |_ 
   | |_) | | '_ \| | | | | '_ \|  _ \ / _ \| __|
   |  __/| | |_) | | |_| | |_) | |_) | (_) | |_ 
   |_|   |_| .__/|_|\__,_| .__/|____/ \___/ \__|
           |_|           |_|                    
   ```

**Warning:** Keep the `src/main/java` folder as the root folder for Java files (i.e., don't rename those folders or move Java files to another folder outside of this folder path), as this is the default location some tools (e.g., Gradle) expect to find Java files.

## Acknowledgements

The code in this project was written with the help of Claude.
