# Nova

Nova is a cheerful space-themed mission navigator with both a JavaFX graphical interface and a console interface.

## Setting up in Intellij

Prerequisites: JDK 25, update Intellij to the most recent version.

1. Open Intellij (if you are not in the welcome screen, click `File` > `Close Project` to close the existing project first)
1. Open the project into Intellij as follows:
   1. Click `Open`.
   1. Select the project directory, and click `OK`.
   1. If there are any further prompts, accept the defaults.
1. Configure the project to use **JDK 25** (not other versions) as explained in [here](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk).<br>
   In the same dialog, set the **Project language level** field to the `SDK default` option.
1. To open the graphical interface, locate `src/main/java/nova/Launcher.java`, right-click it, and choose
   `Run Launcher.main()`.
1. To use the text-based interface instead, run `src/main/java/nova/Nova.java`. The console will show output like:
   ```
   ____________________________________________________________
    _   _
   | \ | | _____   ____ _
   |  \| |/ _ \ \ / / _` |
   | |\  | (_) \ V / (_| |
   |_| \_|\___/ \_/ \__,_|
   Nova online! Your mission navigator is ready.
   What shall we launch today?
   ____________________________________________________________
   Returning to base. Until our next mission!
   ____________________________________________________________
   ```

**Warning:** Keep the `src\main\java` folder as the root folder for Java files (i.e., don't rename those folders or move Java files to another folder outside of this folder path), as this is the default location some tools (e.g., Gradle) expect to find Java files.

## Building and running the fat JAR

The Shadow plugin packages Nova and all of its runtime dependencies into one executable JAR file.

1. From the project root, build the fat JAR:

   ```shell
   ./gradlew shadowJar
   ```

   On Windows, use `gradlew.bat shadowJar` instead.

2. Find the generated JAR at `build/libs/nova.jar`.

3. Run it from the project root so Nova can use its relative `data/nova.txt` storage path. The executable JAR opens
   the JavaFX interface:

   ```shell
   java -jar build/libs/nova.jar
   ```

Java 25 is required for both building and running Nova. Rebuilding the JAR replaces the previous `build/libs/nova.jar` file.

To start the text-based interface from Gradle instead, run the `nova.Nova` main class from IntelliJ. Both interfaces
support the same commands, including `bye`.
