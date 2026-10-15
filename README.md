# Orbit

Orbit is a calm mission-control chatbot that keeps tasks, deadlines, and events on course through a JavaFX interface.

## Running Orbit

Use JDK 25, then launch the GUI from the project root:

```bash
./gradlew run
```

Type a command in the field at the bottom of the window and press Enter or click **Transmit**. For example:

```text
todo read book
deadline return book /by 2026-09-10
event project meeting /from 2026-09-11 /to 2026-09-12
list
mark 1
unmark 1
delete 1
find book
bye
```

See the [Orbit User Guide](docs/README.md) for command formats, examples, storage details, and known limitations.

To build and run the executable JAR:

```bash
./gradlew shadowJar
java -jar build/libs/orbit.jar
```

## Setting up in Intellij

Prerequisites: JDK 25, update Intellij to the most recent version.

1. Open Intellij (if you are not in the welcome screen, click `File` > `Close Project` to close the existing project first)
1. Open the project into Intellij as follows:
   1. Click `Open`.
   1. Select the project directory, and click `OK`.
   1. If there are any further prompts, accept the defaults.
1. Configure the project to use **JDK 25** (not other versions) as explained in [here](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk).<br>
   In the same dialog, set the **Project language level** field to the `SDK default` option.
1. After that, locate `src/main/java/orbit/Launcher.java`, right-click it, and choose `Run Launcher.main()`.

**Warning:** Keep the `src\main\java` folder as the root folder for Java files (i.e., don't rename those folders or move Java files to another folder outside of this folder path), as this is the default location some tools (e.g., Gradle) expect to find Java files.
