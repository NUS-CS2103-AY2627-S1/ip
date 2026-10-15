# Bot project template

This is a project template for a greenfield Java project. It's named after the Java mascot _Bot_. Given below are instructions on how to use it.

## Setting up in Intellij

Prerequisites: JDK 25, update Intellij to the most recent version.

1. Open Intellij (if you are not in the welcome screen, click `File` > `Close Project` to close the existing project first)
1. Open the project into Intellij as follows:
   1. Click `Open`.
   1. Select the project directory, and click `OK`.
   1. If there are any further prompts, accept the defaults.
1. Configure the project to use **JDK 25** (not other versions) as explained in [here](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk).<br>
   In the same dialog, set the **Project language level** field to the `SDK default` option.
1. After that, locate the `src/main/java/bot/Launcher.java` file, right-click it, and choose `Run Launcher.main()`
   (if the code editor is showing compile errors, try restarting the IDE). If the setup is correct, a window titled
   `Bot` should appear with a chat area, a command field, and a send button.

The JavaFX window provides a scrollable chat area, a command field, and a send button. Pressing Enter or clicking
Send executes the command, displays the user's message and Bot's response, then scrolls to the newest dialog
automatically. The GUI supports the same task commands, validation, and file storage as the console interface.
The responsive layout is defined in `src/main/resources/view` using FXML, while Java controller classes handle
interaction. Styles in `src/main/resources/css` provide distinct message bubbles and interactive button feedback.

**Warning:** Keep the `src\main\java` folder as the root folder for Java files (i.e., don't rename those folders or move Java files to another folder outside of this folder path), as this is the default location some tools (e.g., Gradle) expect to find Java files.

## Creating and running the executable JAR

Ensure that Java 25 is active, then run the following command from the project root:

```shell
./gradlew clean shadowJar
```

On Windows, use `gradlew.bat clean shadowJar` instead. The Shadow plugin packages the application and its runtime
dependencies into `build/libs/Bot.jar`.

To distribute and run the application:

1. Copy `build/libs/Bot.jar` into an empty folder.
2. Open a terminal in that folder.
3. Run `java -jar "Bot.jar"`.

The current JAR launches the JavaFX chat layout. After the chatbot logic is connected to the GUI, saved tasks will
continue to use a `data` folder relative to the folder from which the JAR is run.

Do not commit `Bot.jar` to Git because it is a generated binary. The `build` directory is ignored by this
repository. To distribute a version through GitHub, create a GitHub release and attach `Bot.jar` to the release.
