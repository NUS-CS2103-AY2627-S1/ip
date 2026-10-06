# Moon

Moon is a task-management chatbot with a chill, Gen-Z-inspired personality. It is available through both a console interface and a JavaFX GUI.

## Setting up in Intellij

Prerequisites: JDK 25, update Intellij to the most recent version.

1. Open Intellij (if you are not in the welcome screen, click `File` > `Close Project` to close the existing project first)
1. Open the project into Intellij as follows:
   1. Click `Open`.
   1. Select the project directory, and click `OK`.
   1. If there are any further prompts, accept the defaults.
1. Configure the project to use **JDK 25** (not other versions) as explained in [here](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk).<br>
   In the same dialog, set the **Project language level** field to the `SDK default` option.
1. After that, locate the `src/main/java/moon/Moon.java` file, right-click it, and choose `Run Moon.main()` (if the code editor is showing compile errors, try restarting the IDE). If the setup is correct, you should see Moon's welcome message.

**Warning:** Keep the `src\main\java` folder as the root folder for Java files (i.e., don't rename those folders or move Java files to another folder outside of this folder path), as this is the default location some tools (e.g., Gradle) expect to find Java files.

## Building the executable JAR

Use Java 25 when running Gradle. From the project root, run:

```powershell
java -version
.\gradlew.bat clean shadowJar
```

Gradle creates the executable fat JAR at `build/libs/moon.jar`. The Shadow JAR
contains Moon's JavaFX dependencies, so JavaFX does not need to be installed
separately on the machine running the JAR. The generated file should not be
committed to Git.

To distribute the application, copy `moon.jar` into an empty folder and run:

```powershell
java -jar "moon.jar"
```

## Acknowledgements

- The project began from the [SE-EDU iP starter template](https://github.com/se-edu/ip).
- The GUI uses the [OpenJFX JavaFX library](https://openjfx.io/), and the executable JAR is packaged with the [Shadow Gradle plugin](https://github.com/GradleUp/shadow).
- `src/main/resources/images/moon-night.png` and `src/main/resources/images/moon-avatar.png` were generated for this project with OpenAI Codex's built-in image-generation tool. No third-party images were reused.
- OpenAI Codex was used as an AI-assisted development tool across the implementation, refactoring, testing, and documentation work. The generated changes were reviewed and verified with the project's Java 25 Gradle checks.
