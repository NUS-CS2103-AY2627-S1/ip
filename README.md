# Tony project template

This is a project template for a greenfield Java project. Given below are instructions on how to use it.

## Setting up in Intellij

Prerequisites: JDK 25, update Intellij to the most recent version.

1. Open Intellij (if you are not in the welcome screen, click `File` > `Close Project` to close the existing project first)
1. Open the project into Intellij as follows:
   1. Click `Open`.
   1. Select the project directory, and click `OK`.
   1. If there are any further prompts, accept the defaults.
1. Configure the project to use **JDK 25** (not other versions) as explained in [here](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk).<br>
   In the same dialog, set the **Project language level** field to the `SDK default` option.
1. Run the `Launcher` class to open Tony's graphical chat interface. Enter a command in the text field and press
   <kbd>Enter</kbd> or select **Send**. The command hint at the top of the window lists the supported commands.

**Warning:** Keep the `src\main\java` folder as the root folder for Java files (i.e., don't rename those folders or move Java files to another folder outside of this folder path), as this is the default location some tools (e.g., Gradle) expect to find Java files.

## Building and running the fat JAR

A fat JAR contains the application and all of its runtime dependencies, so it can be run without assembling a separate classpath.

1. In a terminal opened at the project root, select Java 25:
   ```shell
   sdk use java 25.0.3.fx-zulu
   ```
1. Build the fat JAR with the Gradle wrapper:
   ```shell
   ./gradlew shadowJar
   ```
1. Find the generated JAR at `build/libs/tony.jar`.
1. From the project root, run it with Java 25:
   ```shell
   java -jar build/libs/tony.jar
   ```

The application opens a chat window. Tony keeps the conversation visible, scrolls to new replies automatically,
and saves task changes to `data/tony.txt`. Enter `bye` to end the conversation.

To use the original console interface instead, run `tony.Tony` from the IDE.

On Windows, use `gradlew.bat shadowJar` for the build command. Run the JAR from the project root so Tony reads and writes its task data under the project's `data` directory.
