# Ollie Mission Control

Ollie is a calm personal mission controller that keeps tasks on course. Given below are instructions on how
to use it.

## Setting up in Intellij

Prerequisites: JDK 25, update Intellij to the most recent version.

1. Open Intellij (if you are not in the welcome screen, click `File` > `Close Project` to close the existing project first)
1. Open the project into Intellij as follows:
   1. Click `Open`.
   1. Select the project directory, and click `OK`.
   1. If there are any further prompts, accept the defaults.
1. Configure the project to use **JDK 25** (not other versions) as explained in [here](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk).<br>
   In the same dialog, set the **Project language level** field to the `SDK default` option.
1. After that, locate the `src/main/java/ollie/Ollie.java` file, right-click it, and choose `Run Ollie.main()` (if the code editor is showing compile errors, try restarting the IDE). If the setup is correct, you should see something like the below as the output:
   ```
   OLLIE // MISSION CONTROL
   Systems online. Let's put your day into orbit.
   Type help to open the flight manual.
   Mission complete. Safe travels—Ollie signing off.
   ```

**Warning:** Keep the `src\main\java` folder as the root folder for Java files (i.e., don't rename those folders or move Java files to another folder outside of this folder path), as this is the default location some tools (e.g., Gradle) expect to find Java files.

## Building and running the JAR

Build the executable JAR from the project root:

```powershell
.\gradlew.bat clean shadowJar
```

The generated file is located at `build/libs/ollie.jar`. Copy it into the folder where you want to use
Ollie, open a command window in that folder, and run:

```bash
java -jar "ollie.jar"
```
