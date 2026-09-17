# Baemax project template

This is a project template for a greenfield Java project. It is configured for the Baemax chatbot. Given below are instructions on how to use it.

## Setting up in Intellij

Prerequisites: JDK 25, update Intellij to the most recent version.

1. Open Intellij (if you are not in the welcome screen, click `File` > `Close Project` to close the existing project first)
1. Open the project into Intellij as follows:
   1. Click `Open`.
   1. Select the project directory, and click `OK`.
   1. If there are any further prompts, accept the defaults.
1. Configure the project to use **JDK 25** (not other versions) as explained in [here](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk).<br>
   In the same dialog, set the **Project language level** field to the `SDK default` option.
1. After that, locate the `src/main/java/baemax/Launcher.java` file, right-click it, and choose `Run Launcher.main()` (if the code editor is showing compile errors, try restarting the IDE). If the setup is correct, a Baemax window should open, ready to take commands.

   A console-only version is also available: right-click `src/main/java/baemax/Baemax.java` and choose `Run Baemax.main()` instead. You should see something like the below as the output:
   ```
   ╔════════════════╗
   ║     Baemax     ║
   ╚════════════════╝

   Hello, I am Baemax, your personal task manager companion!
   What can I do for you today?
   ```

**Warning:** Keep the `src\main\java` folder as the root folder for Java files (i.e., don't rename those folders or move Java files to another folder outside of this folder path), as this is the default location some tools (e.g., Gradle) expect to find Java files.

## Running from the command line

```bash
./gradlew run
```

runs the GUI version the same way IntelliJ's `Launcher.main()` does. To build a standalone jar:

```bash
./gradlew shadowJar
java -jar build/libs/baemax.jar
```

## Credits

Parts of this project were adapted from course-provided material rather than written from scratch:

- The GUI's structure (`Launcher` → `Main` → `MainWindow` + `DialogBox`, and using FXML for the layout) follows the [SE-EDU JavaFX tutorial](https://se-education.org/guides/tutorials/javaFxPart1.html).
- The Gradle build setup came from the course's `add-gradle-support` branch of this template repository.
- `config/checkstyle/checkstyle.xml` and `suppressions.xml` are copied from [AddressBook Level 3](https://github.com/se-edu/addressbook-level3), the SE-EDU teaching codebase.
- The fonts bundled in `src/main/resources/fonts/` are [VT323](https://fonts.google.com/specimen/VT323) and [Press Start 2P](https://fonts.google.com/specimen/Press+Start+2P), both by their respective Google Fonts contributors under the SIL Open Font License (license text included alongside each font).
