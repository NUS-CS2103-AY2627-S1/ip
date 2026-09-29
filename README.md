# Xue project template

This is a project template for a greenfield Java project. It's named after the Java mascot _Xue_. Given below are instructions on how to use it.

## Setting up in Intellij

Prerequisites: JDK 25, update Intellij to the most recent version.

1. Open Intellij (if you are not in the welcome screen, click `File` > `Close Project` to close the existing project first)
1. Open the project into Intellij as follows:
   1. Click `Open`.
   1. Select the project directory, and click `OK`.
   1. If there are any further prompts, accept the defaults.
1. Configure the project to use **JDK 25** (not other versions) as explained in [here](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk).<br>
   In the same dialog, set the **Project language level** field to the `SDK default` option.
1. After that, locate the `src/main/java/xue/Xue.java` file, right-click it, and choose `Run Xue.main()` (if the code editor is showing compile errors, try restarting the IDE). If the setup is correct, Xue will start and display its welcome message:
   ```
   Hello, I'm Xue — your reluctantly competent task assistant.
   Give me a command and I will pretend it was my idea.
   ```

**Warning:** Keep the `src\main\java` folder as the root folder for Java files (i.e., don't rename those folders or move Java files to another folder outside of this folder path), as this is the default location some tools (e.g., Gradle) expect to find Java files.

## Creating and running the fat JAR

This project uses the [ShadowJar](https://github.com/GradleUp/shadow) Gradle plugin to package Xue and its runtime dependencies into one executable (fat) JAR file. Ensure that Java 25 is installed, then run the following command from the project root:

On Windows:

```powershell
.\gradlew.bat shadowJar
```

On macOS or Linux:

```bash
./gradlew shadowJar
```

The generated file is `build/libs/duke.jar`. Run it from the project root so that Xue can find its `data/duke.txt` file:

```bash
java -jar build/libs/duke.jar
```

On Windows PowerShell, the same command is:

```powershell
java -jar .\build\libs\duke.jar
```

To rebuild the JAR after making code changes, run `shadowJar` again. The `jar` task is disabled because only the bundled fat JAR is needed.
