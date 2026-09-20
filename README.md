# Bkxss

Bkxss is a JavaFX task assistant for managing todos, deadlines, and events. It
supports persistent storage, task search, and finding free time between scheduled
events. See the [user guide](docs/README.md) for the complete command reference.

## Setting up in Intellij

Prerequisites: JDK 25, update Intellij to the most recent version.

1. Open Intellij (if you are not in the welcome screen, click `File` > `Close Project` to close the existing project first)
1. Open the project into Intellij as follows:
   1. Click `Open`.
   1. Select the project directory, and click `OK`.
   1. If there are any further prompts, accept the defaults.
1. Configure the project to use **JDK 25** (not other versions) as explained in [here](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk).<br>
   In the same dialog, set the **Project language level** field to the `SDK default` option.
1. After that, locate `src/main/java/bkxss/Launcher.java`, right-click it, and
   choose `Run Launcher.main()`. If the code editor shows compile errors, try
   restarting the IDE.

The application opens a JavaFX chat window. Try `todo borrow book`, then `list`.

**Warning:** Keep the `src\main\java` folder as the root folder for Java files (i.e., don't rename those folders or move Java files to another folder outside of this folder path), as this is the default location some tools (e.g., Gradle) expect to find Java files.

## Creating and running a fat JAR

This project uses the Shadow Gradle plugin to package the application and its runtime dependencies into one executable (fat) JAR. The configured output file is `bkxss.jar`.

From the project root, make sure Java 25 is active, then run:

```sh
sdk use java 25.0.3.fx-zulu
./gradlew shadowJar
```

On Windows PowerShell, use:

```powershell
.\gradlew.bat shadowJar
```

The JAR is created at `build/libs/bkxss.jar`. It includes JavaFX runtime files
for x86-64 Windows, macOS, and Linux, so the same JAR can be run on those
platforms with Java 25:

```sh
java -jar build/libs/bkxss.jar
```

Type commands such as `todo borrow book`, and type `bye` to exit. The application reads and writes task data at `data/bkxss.txt`, so run the JAR from the project root if you want to use that file.

To create a fresh artifact after changing source code, run `./gradlew clean shadowJar`. You can inspect the generated files with `ls -lh build/libs/`.
