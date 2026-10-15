# Thomas project template

This is a project template for a greenfield Java project for Thomas. Given below are instructions on how to use it.

## Setting up in Intellij

Prerequisites: JDK 25, update Intellij to the most recent version.

1. Open Intellij (if you are not in the welcome screen, click `File` > `Close Project` to close the existing project first)
1. Open the project into Intellij as follows:
   1. Click `Open`.
   1. Select the project directory, and click `OK`.
   1. If there are any further prompts, accept the defaults.
1. Configure the project to use **JDK 25** (not other versions) as explained in [here](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk).<br>
   In the same dialog, set the **Project language level** field to the `SDK default` option.
1. After that, locate the `src/main/java/thomas/Thomas.java` file, right-click it, and choose `Run Thomas.main()` (if the code editor is showing compile errors, try restarting the IDE). If the setup is correct, you should see something like the below as the output:
   ```
   ▀▀█▀▀ █  █ █▀▀█ █▀▄▀█ █▀▀█ █▀▀
     █   █▀▀█ █  █ █ ▀ █ █▄▄█ ▀▀█
     ▀   ▀  ▀ ▀▀▀▀ ▀   ▀ ▀  ▀▀▀
   ```

**Warning:** Keep the `src\main\java` folder as the root folder for Java files (i.e., don't rename those folders or move Java files to another folder outside of this folder path), as this is the default location some tools (e.g., Gradle) expect to find Java files.

## Building a fat JAR with Shadow

This project is configured with the Shadow plugin to produce a runnable fat JAR (dependencies included).

1. Open a terminal in the project root.
1. Build the shadow JAR:
   ```powershell
   .\gradlew clean shadowJar
   ```
1. Locate the generated JAR at:
   ```text
   build\libs\thomas.jar
   ```
1. Run the JAR:
   ```powershell
   java -jar build\libs\thomas.jar
   ```

You can also run `./gradlew build` (or `.\gradlew build` on Windows); it is configured to produce the same fat JAR as part of the build.

## Acknowledgements

Thomas started from the [SE-EDU Duke project template and tutorials](https://se-education.org/guides/tutorials/duke.html).
The initial Gradle structure, JavaFX/FXML chat interface, and related starter resources were adapted from that
material. The original template contributors are listed in [CONTRIBUTORS.md](CONTRIBUTORS.md).
