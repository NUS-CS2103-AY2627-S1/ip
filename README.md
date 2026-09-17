# Lion

Lion is a JavaFX desktop chatbot for tracking todos, deadlines, and events via a chat window. See the [User Guide](https://glory-lion.github.io/ip/) for how to use it.

This repository is a Gradle project; JavaFX and its dependencies are declared in `build.gradle`, so no separate JavaFX SDK setup is needed.

## Setting up in IntelliJ

Prerequisites: JDK 25, IntelliJ updated to the most recent version.

1. Open IntelliJ (if you are not in the welcome screen, click `File` > `Close Project` to close the existing project first).
2. Open the project into IntelliJ as follows:
   1. Click `Open`.
   2. Select the project directory, and click `OK`.
   3. If prompted, choose to open/import it as a **Gradle** project and accept the defaults — this is what lets IntelliJ resolve the JavaFX dependencies declared in `build.gradle`.
3. Configure the project to use **JDK 25** (not other versions), as explained [here](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk). In the same dialog, set the **Project language level** field to the `SDK default` option.
4. Locate `src/main/java/lion/Launcher.java`, right-click it, and choose `Run Launcher.main()` (if the code editor is showing compile errors, try restarting the IDE, or re-syncing Gradle). If the setup is correct, a chat window titled "Lion" should appear.

**Warning:** Keep `src/main/java` as the root folder for Java files (i.e., don't rename those folders or move Java files outside of this folder path), as this is the default location some tools (e.g., Gradle) expect to find Java files.
