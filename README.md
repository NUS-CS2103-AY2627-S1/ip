# Yappy

This is a greenfield Java project for a chatbot named _Yappy_. Given below are instructions on how to use it.

For available commands and examples, see the [Yappy User Guide](docs/README.md).

## Setting up in Intellij

Prerequisites: JDK 25, update Intellij to the most recent version.

1. Open Intellij (if you are not in the welcome screen, click `File` > `Close Project` to close the existing project first)
1. Open the project into Intellij as follows:
   1. Click `Open`.
   1. Select the project directory, and click `OK`.
   1. If there are any further prompts, accept the defaults.
1. Configure the project to use **JDK 25** (not other versions) as explained in [here](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk).<br>
   In the same dialog, set the **Project language level** field to the `SDK default` option.
1. After that, locate `src/main/java/yappy/Launcher.java`, right-click it, and choose
   `Run Launcher.main()` (if the code editor is showing compile errors, try restarting the IDE).
   If the setup is correct, the Yappy window should open.

**Warning:** Keep the `src\main\java` folder as the root folder for Java files (i.e., don't rename those folders or move Java files to another folder outside of this folder path), as this is the default location some tools (e.g., Gradle) expect to find Java files.

## Running with Gradle

The included Gradle wrapper uses the project's tested Gradle version, so a system-wide Gradle installation is not needed.

On Windows, run:

```powershell
.\gradlew.bat run
```

On macOS or Linux, run:

```bash
./gradlew run
```

Run `.\gradlew.bat build` on Windows or `./gradlew build` on macOS and Linux to compile the project and execute its automated tests.

## Creating an Executable JAR

Run the following command on Windows:

```powershell
.\gradlew.bat shadowJar
```

On macOS or Linux, run `./gradlew shadowJar` instead. The executable file is created at `build/libs/Yappy.jar`.

To distribute Yappy, copy `Yappy.jar` into an empty folder. Open a terminal in that folder and run:

```text
java -jar "Yappy.jar"
```

Yappy creates its `data/yappy.txt` file relative to the folder from which the JAR is run. The generated JAR and runtime data are excluded from Git.
