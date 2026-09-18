# Bobby project

This is a project template for a greenfield Java project named Bobby. Given below are instructions on how to use it.

## Creating and running the executable JAR

The project uses Gradle's Shadow plugin to build a **fat JAR**: one file containing Bobby and all of its runtime dependencies. The application entry point is configured as `bobby.Bobby`, so the JAR can be started directly with `java -jar`.

1. From the project root, run:

   ```powershell
   .\gradlew.bat shadowJar
   ```

   On macOS or Linux, run `./gradlew shadowJar` instead. Use Java 25 to run the build.

1. Find the generated file at `build/libs/bobby.jar`.
1. Copy `bobby.jar` into an empty folder, open a command window in that folder, and run:

   ```powershell
   java -jar "bobby.jar"
   ```

   The application stores its task data in `data/bobby.txt` relative to the folder containing the JAR. This keeps the JAR portable: copying only the JAR to a new empty folder starts a separate task list there.

## Setting up in Intellij

Prerequisites: JDK 25, update Intellij to the most recent version.

1. Open Intellij (if you are not in the welcome screen, click `File` > `Close Project` to close the existing project first)
1. Open the project into Intellij as follows:
   1. Click `Open`.
   1. Select the project directory, and click `OK`.
   1. If there are any further prompts, accept the defaults.
1. Configure the project to use **JDK 25** (not other versions) as explained in [here](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk).<br>
   In the same dialog, set the **Project language level** field to the `SDK default` option.
1. After that, locate the `src/main/java/Bobby.java` file, right-click it, and choose `Run Bobby.main()` (if the code editor is showing compile errors, try restarting the IDE). If the setup is correct, you should see the following output:
   ```
    ____        _        
   |  _ \ _   _| | _____ 
   | | | | | | | |/ / _ \
   | |_| | |_| |   <  __/
   |____/ \__,_|_|\_\___|
   ```

**Warning:** Keep the `src\main\java` folder as the root folder for Java files (i.e., don't rename those folders or move Java files to another folder outside of this folder path), as this is the default location some tools (e.g., Gradle) expect to find Java files.

## Credits

AI was used in the generation of this project. Code was generated using Codex, primarily using models GPT-5.6 Sol and Luna.
