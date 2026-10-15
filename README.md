# Quackers project template

This is a project template for a greenfield Java project. It's named _Quackers_. Given below are instructions on how to use it.

## Setting up in Intellij

Prerequisites: JDK 25, update Intellij to the most recent version.

1. Open Intellij (if you are not in the welcome screen, click `File` > `Close Project` to close the existing project first)
1. Open the project into Intellij as follows:
   1. Click `Open`.
   1. Select the project directory, and click `OK`.
   1. If there are any further prompts, accept the defaults.
1. Configure the project to use **JDK 25** (not other versions) as explained in [here](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk).<br>
   In the same dialog, set the **Project language level** field to the `SDK default` option.
1. After that, locate the `src/main/java/quackers/Launcher.java` file, right-click it, and choose `Run Launcher.main()` (if the code editor is showing compile errors, try restarting the IDE). If the setup is correct, a chat window titled _Quackers_ opens and greets you.

## Running Quackers

Quackers has two user interfaces, both driven by the same task logic.

- **GUI (default).** Run `./gradlew run`, or run `Launcher.main()` from the IDE. `Launcher` rather
  than `Main` is the entry point because a main class that extends `Application` makes the JVM
  demand a modular JavaFX runtime, which this project does not use.
- **Console.** Run `Quackers.main()` from the IDE. This is handy for checking the core logic
  against `test/ui-test-plan.md` without clicking through the GUI.

To build a runnable JAR, run `./gradlew shadowJar`; the result is `build/libs/quackers.jar`, which
bundles JavaFX and can be launched with `java -jar quackers.jar`.

Tasks are saved to `data/quackers.txt`, relative to the working directory.

**Warning:** Keep the `src\main\java` folder as the root folder for Java files (i.e., don't rename those folders or move Java files to another folder outside of this folder path), as this is the default location some tools (e.g., Gradle) expect to find Java files.

## Acknowledgements

- The Quackers avatar (`src/main/resources/images/duck.png`) is the duck from *The Duck Song* by Bryant Oden,
  taken from a Google Images thumbnail, with its white background made transparent.
