# Jaylen

Jaylen is a JavaFX task assistant developed for the CS2103/T individual project.
See the [User Guide](docs/README.md) for the supported commands.

## Setting up in Intellij

Prerequisites: JDK 25, update Intellij to the most recent version.

1. Open Intellij (if you are not in the welcome screen, click `File` > `Close Project` to close the existing project first)
1. Open the project into Intellij as follows:
   1. Click `Open`.
   1. Select the project directory, and click `OK`.
   1. If there are any further prompts, accept the defaults.
1. Configure the project to use **JDK 25** (not other versions) as explained in [here](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk).<br>
   In the same dialog, set the **Project language level** field to the `SDK default` option.
1. After that, locate `src/main/java/jaylen/Launcher.java`, right-click it, and choose
   `Run Launcher.main()` (if the code editor is showing compile errors, try restarting
   the IDE). If the setup is correct, the Jaylen chat window will open.

**Warning:** Keep the `src\main\java` folder as the root folder for Java files (i.e., don't rename those folders or move Java files to another folder outside of this folder path), as this is the default location some tools (e.g., Gradle) expect to find Java files.

## Building and running with Gradle

The project includes the Gradle Wrapper, so no separate Gradle installation is
needed. Use JDK 25, then run these commands from the project root:

```bash
./gradlew build
./gradlew run
```

The `build` command compiles the project and runs its automated checks. The
`run` command starts the Jaylen JavaFX interface. To use a different save file, pass
it as a system property, for example:

```bash
./gradlew run -Djaylen.storage=./data/jaylen.txt
```

The original console interface remains available for automated testing:

```bash
./gradlew runConsole
```

To run the JUnit test suite, use:

```bash
./gradlew test
```

To check the main and test code against the SE-EDU Java coding standard, use:

```bash
./gradlew checkstyleMain checkstyleTest
```

The `build` command also runs both Checkstyle tasks automatically.

To generate the HTML API documentation, use:

```bash
./gradlew javadoc
```

The generated documentation is written to `build/docs/javadoc/`.

To build the cross-platform executable JAR with its JavaFX dependencies, use:

```bash
./gradlew clean shadowJar
java -jar build/libs/jaylen.jar
```

## Finding tasks

Use `find <terms>` to display tasks whose descriptions contain every search
term. Matching is case-insensitive, accepts partial terms, and does not depend
on the order of the terms. Results keep their original task numbers.

```text
find book
find BOO urn
```

The second example matches a task such as `return book`: `BOO` partially
matches `book`, while `urn` partially matches `return` even though the search
terms are entered in a different order.

## Acknowledgements

Jaylen was developed from the NUS CS2103/T individual-project starter template.
OpenAI Codex assisted with the Week 6 GUI, error-handling, testing, and
documentation improvements; the generated changes were reviewed and tested by
the project author.
