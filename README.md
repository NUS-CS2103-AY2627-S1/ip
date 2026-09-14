# Oreo

Oreo is a JavaFX task-management chat app. For commands and examples, see the
[Oreo User Guide](docs/README.md).

## Running Oreo

From the project folder, run:

```powershell
.\gradlew.bat run
```

Oreo opens as a chat window. Enter commands such as `todo buy milk`, `list`,
`mark 1`, `find milk`, and `bye` in the input box; press Enter or select **Send**.
Tasks are saved immediately to `data/Oreo.txt` after each change.

## Setting up in IntelliJ IDEA

Prerequisites: JDK 25, update Intellij to the most recent version.

1. Open IntelliJ IDEA (if you are not in the welcome screen, click `File` > `Close Project` to close the existing project first)
1. Open the project into Intellij as follows:
   1. Click `Open`.
   1. Select the project directory, and click `OK`.
   1. If there are any further prompts, accept the defaults.
1. Configure the project to use **JDK 25** (not other versions) as explained [here](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk).<br>
   In the same dialog, set the **Project language level** field to the `SDK default` option.
1. To run the app from IntelliJ, open [Launcher.java](src/main/java/oreo/Launcher.java), right-click it, and select `Run 'Launcher.main()'`.

**Warning:** Keep the `src\main\java` folder as the root folder for Java files (i.e., don't rename those folders or move Java files to another folder outside of this folder path), as this is the default location some tools (e.g., Gradle) expect to find Java files.
