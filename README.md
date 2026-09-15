# CHOO

CHOO is a JavaFX chatbot for managing todos, deadlines, and events through
short text commands.

Read the [CHOO User Guide](https://clarencechoo.github.io/ip/) for setup,
commands, and troubleshooting.

## Setting up in Intellij

Prerequisites: JDK 25, update Intellij to the most recent version.

1. Open Intellij (if you are not in the welcome screen, click `File` > `Close Project` to close the existing project first)
1. Open the project into Intellij as follows:
   1. Click `Open`.
   1. Select the project directory, and click `OK`.
   1. If there are any further prompts, accept the defaults.
1. Configure the project to use **JDK 25** (not other versions) as explained in [here](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk).<br>
   In the same dialog, set the **Project language level** field to the `SDK default` option.
1. After that, locate `src/main/java/choo/gui/Launcher.java`, right-click it,
   and choose `Run Launcher.main()` (if the code editor is showing compile
   errors, try restarting the IDE). If setup is correct, the CHOO chat window
   appears.

**Warning:** Keep the `src\main\java` folder as the root folder for Java files (i.e., don't rename those folders or move Java files to another folder outside of this folder path), as this is the default location some tools (e.g., Gradle) expect to find Java files.

## Using Gradle

Run these commands from the project root:

```bash
./gradlew clean build
./gradlew run
```

The first command compiles the project and checks the build. The second opens
the CHOO window; enter `bye` to exit. In IntelliJ, ensure both the Project SDK and Gradle
JVM use JDK 25, then reload the Gradle project using the elephant toolbar.

## Creating the executable JAR

From the project root, run:

```bash
./gradlew clean shadowJar
```

The fat JAR is created at `build/libs/choo.jar`. To test the distributable,
copy only `choo.jar` into an empty folder, open a terminal in that folder, and
run:

```bash
java -jar "choo.jar"
```

The JAR bundles JavaFX for Windows and Linux on x64, and for both Intel and
Apple Silicon Macs. The build combines the two macOS native libraries into
universal binaries; no separate JavaFX installation or macOS build tool is
needed. Java 25 is still required.

`./gradlew check` also inspects the finished JAR with `verifyReleaseJar`.
This check verifies the launcher, essential resources, and both macOS
architectures. Before releasing, also launch the JAR using a Java 25 runtime
without JavaFX: a JDK that includes JavaFX can conceal missing bundled
libraries. Test commands, error feedback, and saving/reloading in the GUI.
If creating a test runtime with `jlink`, include `java.se,jdk.unsupported`:
JavaFX's renderer needs `jdk.unsupported`, even though JavaFX itself must
remain outside the test runtime.

CHOO creates its `data/choo.txt` storage file relative to that folder. The
generated JAR and `build/` directory are ignored by Git and must not be
committed. To distribute CHOO, create a GitHub release with an appropriate
version such as `v0.2` and attach only `build/libs/choo.jar` as the release asset.

## Acknowledgements

- This project builds on the NUS CS2103/T Project Duke course materials and
  Gradle support branch.
- The GUI uses [OpenJFX](https://openjfx.io/). The project also uses JUnit,
  Checkstyle, JaCoCo, and the Gradle Shadow plugin for testing and builds.
- OpenAI Codex was used extensively as a coding collaborator for planning,
  implementation, refactoring, testing, documentation, and Git workflows.
  The generated work was reviewed and tested before inclusion.
- `ChooAvatar.png` and `UserAvatar.png` were generated specifically for this
  project using OpenAI's GPT Image model.
