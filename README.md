# Nova

Nova is a task list chatbot: add todos, deadlines and events, mark, find, delete and archive them, from a JavaFX window or a text terminal. It started from the SE-EDU greenfield Java project template, and the user guide lives in [docs/README.md](docs/README.md).

Download `nova.jar` from the [releases page](https://github.com/Keerthivasan152/ip/releases) and run it with Java 25 (`java -jar nova.jar`).

## Setting up in Intellij

Prerequisites: JDK 25, update Intellij to the most recent version.

1. Open Intellij (if you are not in the welcome screen, click `File` > `Close Project` to close the existing project first)
1. Open the project into Intellij as follows:
   1. Click `Open`.
   1. Select the project directory, and click `OK`.
   1. If there are any further prompts, accept the defaults.
1. Configure the project to use **JDK 25** (not other versions) as explained in [here](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk).<br>
   In the same dialog, set the **Project language level** field to the `SDK default` option.
1. After that, locate the `src/main/java/nova/Nova.java` file, right-click it, and choose `Run Nova.main()` (if the code editor is showing compile errors, try restarting the IDE). If the setup is correct, you should see something like the below as the output:
   ```
    _   _
   | \ | | _____   ____ _
   |  \| |/ _ \ \ / / _` |
   | |\  | (_) \ V / (_| |
   |_| \_|\___/ \_/ \__,_|

   Nova here. What can I do for you?
   ```

**Warning:** Keep the `src\main\java` folder as the root folder for Java files (i.e., don't rename those folders or move Java files to another folder outside of this folder path), as this is the default location some tools (e.g., Gradle) expect to find Java files.

## Setting up with Gradle

Alternatively, the project is set up to build with Gradle:

1. Run `./gradlew run` (Windows: `gradlew.bat run`) to start the chatbot.
2. Run `./gradlew test` to run the automated tests.
3. Run `./gradlew shadowJar` to build a runnable JAR at `build/libs/nova.jar`.

## Acknowledgements

* Libraries: [JavaFX](https://openjfx.io/) 17.0.7 for the GUI and [JUnit 5](https://junit.org/junit5/) for the automated tests, each under its own open source licence.
* Starting point and coding-standard configuration: [AddressBook-Level3](https://github.com/se-edu/addressbook-level3); the GUI follows the [SE-EDU JavaFX tutorial](https://se-education.org/guides/tutorials/javaFxPart1.html). Both are course materials from SE-EDU.
* AI coding assistants were used to write and reshape parts of this codebase, most heavily for the Week 6 optional increments, which the course asks to be done with AI assistance. Every increment was reviewed, built and smoke tested before it was committed.
