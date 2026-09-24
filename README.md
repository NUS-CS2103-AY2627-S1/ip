# Sage project

This is a greenfield Java project for Sage, a personal task-management assistant. Given below are instructions on how to use it.

## Setting up in Intellij

Prerequisites: JDK 25, update Intellij to the most recent version.

1. Open Intellij (if you are not in the welcome screen, click `File` > `Close Project` to close the existing project first)
1. Open the project into Intellij as follows:
   1. Click `Open`.
   1. Select the project directory, and click `OK`.
   1. If there are any further prompts, accept the defaults.
1. Configure the project to use **JDK 25** (not other versions) as explained in [here](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk).<br>
   In the same dialog, set the **Project language level** field to the `SDK default` option.
1. After that, locate the `src/main/java/Sage.java` file, right-click it, and choose `Run Sage.main()` (if the code editor is showing compile errors, try restarting the IDE). If the setup is correct, you should see something like the below as the output:
   ```
    ____
   / ___|  __ _  __ _  ___
   \___ \ / _` |/ _` |/ _ \
    ___) | (_| | (_| |  __/
   |____/ \__,_|\__, |\___|
                |___/
   ```

The JavaFX graphical interface can be launched by running
`src/main/java/sage/Launcher.java` from IntelliJ. Deadlines and events accept
dates in `d/M/yyyy` or `yyyy-MM-dd` format, with an optional 24-hour `HHmm`
time. Date-only deadlines end at 23:59, while date-only events cover the full
day at their boundaries. See the [Sage User Guide](docs/README.md) for details.

**Warning:** Keep the `src\main\java` folder as the root folder for Java files (i.e., don't rename those folders or move Java files to another folder outside of this folder path), as this is the default location some tools (e.g., Gradle) expect to find Java files.
