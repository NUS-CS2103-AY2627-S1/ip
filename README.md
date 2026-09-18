# Rene

**Rene** is a personal task chatbot with a no-nonsense, corporate register. It keeps your todos, deadlines, and events so they remain available the next time the application starts. Given below are instructions on how to set up and run the project.

## Setting up in Intellij

Prerequisites: JDK 25, update Intellij to the most recent version.

1. Open Intellij (if you are not in the welcome screen, click `File` > `Close Project` to close the existing project first)
1. Open the project into Intellij as follows:
   1. Click `Open`.
   1. Select the project directory, and click `OK`.
   1. If there are any further prompts, accept the defaults.
1. Configure the project to use **JDK 25** (not other versions) as explained in [here](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk).<br>
   In the same dialog, set the **Project language level** field to the `SDK default` option.
1. After that, locate `src/main/java/rene/gui/Launcher.java`, right-click it, and choose `Run Launcher.main()` (if the code editor is showing compile errors, try restarting the IDE). If the setup is correct, the Rene window should open.

**Warning:** Keep the `src\main\java` folder as the root folder for Java files (i.e., don't rename those folders or move Java files to another folder outside of this folder path), as this is the default location some tools (e.g., Gradle) expect to find Java files.

## Running the app

- **GUI:** run `./gradlew run` from the project root, or press the play icon next to the `main` method of `src/main/java/rene/gui/Launcher.java` in IntelliJ.
- **Command line:** run `./gradlew run --args="path/to/rene.txt"` if you prefer the text UI (the path defaults to `data/rene.txt` next to the working directory).

The app saves and reloads tasks from `data/rene.txt` automatically.

## Building the JAR

Run `./gradlew clean shadowJar` to produce the cross-platform fat JAR at
`build/libs/rene.jar`. Distribute that single file; it bundles the JavaFX
libraries needed by Windows, macOS, and Linux and requires Java 25.
