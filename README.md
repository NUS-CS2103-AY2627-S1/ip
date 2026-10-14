# Milo chatbot

Milo is a chatbot built as a greenfield Java project. Given below are instructions on how to set it up.

## Setting up in Intellij

Prerequisites: JDK 25, update Intellij to the most recent version.

1. Open Intellij (if you are not in the welcome screen, click `File` > `Close Project` to close the existing project first)
1. Open the project into Intellij as follows:
   1. Click `Open`.
   1. Select the project directory, and click `OK`.
   1. If there are any further prompts, accept the defaults.
1. Configure the project to use **JDK 25** (not other versions) as explained in [here](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk).<br>
   In the same dialog, set the **Project language level** field to the `SDK default` option.
1. After that, locate the `src/main/java/milo/Milo.java` file, right-click it, and choose `Run Milo.main()` (if the code editor is showing compile errors, try restarting the IDE). If the setup is correct, you should see something like the below as the output:
   ```
    __  __ _ _
   |  \/  (_) | ___
   | |\/| | | |/ _ \
   | |  | | | | (_) |
   |_|  |_|_|_|\___/
   ```

**Warning:** Keep the `src\main\java` folder as the root folder for Java files (i.e., don't rename those folders or move Java files to another folder outside of this folder path), as this is the default location some tools (e.g., Gradle) expect to find Java files.

## Building and running the fat JAR

The Shadow plugin packages Milo and its runtime dependencies into one executable JAR. Use JDK 25 and run the commands from the project root.

On Windows PowerShell, create the JAR with:

```powershell
.\gradlew.bat clean shadowJar
```

On macOS or Linux, create it with:

```bash
./gradlew clean shadowJar
```

Gradle writes the resulting JAR to `build/libs/duke.jar`. Run it from the project root with:

```powershell
java -jar build/libs/duke.jar
```

Milo reads and writes `data/duke.txt` relative to the directory from which the JAR is run.

## Automated checks

GitHub Actions runs the JUnit tests and builds the executable JAR on Windows and Linux for pushes and pull requests, using Java 25. Run the same Gradle checks locally in PowerShell with:

```powershell
.\gradlew.bat check shadowJar
```

Tests use temporary task files so repeated runs do not change your saved tasks.
