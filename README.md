# Aster

Aster is a JavaFX task manager for recording to-dos, deadlines, events, tags, and
searches. See the [user guide](docs/README.md) for the complete command reference.

## Setting up in Intellij

Prerequisites: JDK 25, update Intellij to the most recent version.

1. Open Intellij (if you are not in the welcome screen, click `File` > `Close Project` to close the existing project first)
1. Open the project into Intellij as follows:
   1. Click `Open`.
   1. Select the project directory, and click `OK`.
   1. If there are any further prompts, accept the defaults.
1. Configure the project to use **JDK 25** (not other versions) as explained in [here](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk).<br>
   In the same dialog, set the **Project language level** field to the `SDK default` option.
1. After that, locate the `src/main/java/Aster.java` file, right-click it, and choose `Run Aster.main()` (if the code editor is showing compile errors, try restarting the IDE). If the setup is correct, you should see something like the below as the output:
   ```
       _    ____ _____ _____ ____
      / \  / ___|_   _| ____|  _ \
     / _ \ \___ \ | | |  _| | |_) |
    / ___ \ ___) || | | |___|  _ <
   /_/   \_\____/ |_| |_____|_| \_\
   ```

**Warning:** Keep the `src\main\java` folder as the root folder for Java files (i.e., don't rename those folders or move Java files to another folder outside of this folder path), as this is the default location some tools (e.g., Gradle) expect to find Java files.
