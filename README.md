# Ekud

A task chatbot with a JavaFX GUI and a command-line interface, started from the CS2103T Duke project template.

## AI assistance

Parts of this project were written with help from AI coding tools. Suggestions were reviewed and edited before being included.

## Setting up in Intellij

Prerequisites: JDK 25, update Intellij to the most recent version.

1. Open Intellij (if you are not in the welcome screen, click `File` > `Close Project` to close the existing project first)
1. Open the project into Intellij as follows:
   1. Click `Open`.
   1. Select the project directory, and click `OK`.
   1. If there are any further prompts, accept the defaults.
1. Configure the project to use **JDK 25** (not other versions) as explained in [here](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk).<br>
   In the same dialog, set the **Project language level** field to the `SDK default` option.
1. After that, reload the Gradle project so IntelliJ picks up the JavaFX libraries.
1. Start the GUI with Gradle `run` (or right-click `src/main/java/ekud/Launcher.java` and choose `Run 'Launcher.main()'`). If the setup is correct, a chat window titled Ekud should open.
1. To use the text interface instead, right-click `src/main/java/ekud/Ekud.java` and choose `Run 'Ekud.main()'`. You should see the Ekud banner in the console.

**Warning:** Keep the `src\main\java` folder as the root folder for Java files (i.e., don't rename those folders or move Java files to another folder outside of this folder path), as this is the default location some tools (e.g., Gradle) expect to find Java files.

## Creating a fat JAR

This project uses the Gradle Shadow plugin to package Ekud as an executable fat JAR (`ekud.jar`). A fat JAR includes your classes and any runtime libraries, so someone else can run the app with only Java installed.

From the project root, with JDK 25:

```powershell
$env:JAVA_HOME = "C:\Program Files\Java\jdk-25.0.4"
.\gradlew.bat clean shadowJar
```

Or in IntelliJ: Gradle tool window → `Tasks` → `shadow` → `shadowJar`. Use `clean` first if you want Gradle to rebuild even when it thinks the JAR is up to date.

The file is created at `build/libs/ekud.jar`. Do not commit it; `/build/` in `.gitignore` already ignores that folder.

To run the GUI, copy `ekud.jar` into an empty folder, open a terminal in that folder, and run:

```powershell
java -jar ekud.jar
```

The save file is created as `data/ekud.txt` relative to the folder you run from, not relative to the project source tree.

To start the command-line interface instead, run `ekud.Ekud` from IntelliJ as described above.
