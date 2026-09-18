# chausistant project template

This is a project template for a greenfield Java project for the `chausistant` chatbot. Given below are instructions on how to use it.

## Setting up in Intellij

Prerequisites: JDK 25, update Intellij to the most recent version.

1. Open Intellij (if you are not in the welcome screen, click `File` > `Close Project` to close the existing project first)
1. Open the project into Intellij as follows:
   1. Click `Open`.
   1. Select the project directory, and click `OK`.
   1. If there are any further prompts, accept the defaults.
1. Configure the project to use **JDK 25** (not other versions) as explained in [here](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk).<br>
   In the same dialog, set the **Project language level** field to the `SDK default` option.
1. After that, locate the `src/main/java/Chausistant.java` file, right-click it, and choose `Run Chausistant.main()` (if the code editor is showing compile errors, try restarting the IDE). If the setup is correct, you should see the following output:
   ```
    ████ █   █  ███  █   █  ████ █████  ████ █████  ███  █   █ █████
   █     █   █ █   █ █   █ █      █    █      █    █   █ ██  █   █
   █     █   █ █   █ █   █ █      █    █      █    █   █ ██  █   █
   █     █████ █████ █   █  ███   █     ███   █    █████ █ █ █   █
   █     █   █ █   █ █   █    █   █       █   █    █   █ █  ██   █
   █     █   █ █   █ █   █    █   █       █   █    █   █ █  ██   █
    ████ █   █ █   █  ███  ████ █████ ████ █████  █   █ █   █   █

                         chausistant
   ```

**Warning:** Keep the `src\main\java` folder as the root folder for Java files (i.e., don't rename those folders or move Java files to another folder outside of this folder path), as this is the default location some tools (e.g., Gradle) expect to find Java files.

## Creating and running the executable JAR

This project uses Gradle's `shadowJar` task to create one executable JAR containing
the application and its runtime dependencies. Use JDK 25 to build and run it.

1. From the project directory, create the JAR:

   ```bash
   sdk use java 25.0.3.fx-zulu
   ./gradlew shadowJar
   ```

1. Locate the generated file at `build/libs/chausistant.jar`.

1. To test it as a user would, copy `chausistant.jar` into an empty folder, open a
   terminal in that folder, and run:

   ```bash
   java -jar "chausistant.jar"
   ```

   The application stores its task data in `data/chausistant.txt`, relative to the
   folder where the JAR is run.

The generated JAR is intentionally excluded by `.gitignore`; do not commit it.
To distribute a version, attach `build/libs/chausistant.jar` to a GitHub Release instead.

## Acknowledgments

ChatGPT assisted with the graphical user-interface layout and styling.
