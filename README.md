# Murphy

This is a project template for a greenfield Java project. Given below are instructions on how to use it.

## Setting up in Intellij

Prerequisites: JDK 25, update Intellij to the most recent version.

1. Open Intellij (if you are not in the welcome screen, click `File` > `Close Project` to close the existing project first)
1. Open the project into Intellij as follows:
   1. Click `Open`.
   1. Select the project directory, and click `OK`.
   1. If there are any further prompts, accept the defaults.
1. Configure the project to use **JDK 25** (not other versions) as explained in [here](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk).<br>
   In the same dialog, set the **Project language level** field to the `SDK default` option.
1. After that, locate `src/main/java/murphy/Launcher.java`, right-click it, and choose `Run 'Launcher.main()'` to launch the JavaFX GUI. For the command-line interface, run `murphy.Murphy.main()`.


**Warning:** Keep the `src\main\java` folder as the root folder for Java files (i.e., don't rename those folders or move Java files to another folder outside of this folder path), as this is the default location some tools (e.g., Gradle) expect to find Java files.

## Creating and running the executable JAR

Use Java 25 and run the Shadow plugin task from the project root:

```bash
sdk use java 25.0.3.fx-zulu
./gradlew shadowJar
```

The executable fat JAR is created at `build/libs/murphy.jar`. It contains Murphy and the project's runtime dependencies, so it can be copied by itself into an empty folder and run with:

```bash
java -jar "murphy.jar"
```

The JAR is a generated build artifact and should not be committed to Git. For distribution, attach it to a GitHub release instead.

## Acknowledgements

- Murphy uses [OpenJFX](https://openjfx.io/) for its JavaFX GUI. It is used under the terms of its applicable open-source license.
- The bundled [Fredoka](https://fonts.google.com/specimen/Fredoka) and [Nunito](https://fonts.google.com/specimen/Nunito) fonts are distributed under the SIL Open Font License. Their license terms should be retained with any redistributed font files.
- Codex helped with parts of the coding process that would have taken too much time to complete manually, including implementation suggestions, refactoring support, and test-case suggestions. All generated changes were reviewed and adapted for this project.
