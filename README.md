# Sine

Sine is a chatbot application written in Java. Given below are instructions on how to use it.

## Setting up in Intellij

Prerequisites: JDK 25, update Intellij to the most recent version.

1. Open Intellij (if you are not in the welcome screen, click `File` > `Close Project` to close the existing project first)
1. Open the project into Intellij as follows:
   1. Click `Open`.
   1. Select the project directory, and click `OK`.
   1. If there are any further prompts, accept the defaults.
1. Configure the project to use **JDK 25** (not other versions) as explained in [here](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk).<br>
   In the same dialog, set the **Project language level** field to the `SDK default` option.
1. After that, locate the `src/main/java/sine/Sine.java` file, right-click it, and choose `Run Sine.main()` (if the code editor is showing compile errors, try restarting the IDE). The console opens with the command help.

Try the following commands:

```text
todo read book
undo
bye
```

The undo response is:

```text
____________________________________________________________
Understood. I undid the last task change.
I removed this task:
  [T][ ] read book
____________________________________________________________
```

See the [user guide](docs/README.md) for GUI startup instructions, all commands,
date and time examples, saving, and troubleshooting.

**Warning:** Keep the `src\main\java` folder as the root folder for Java files (i.e., don't rename those folders or move Java files to another folder outside of this folder path), as this is the default location some tools (e.g., Gradle) expect to find Java files.

## Creating the fat JAR

The Shadow plugin packages Sine and its runtime dependencies into one executable fat JAR.

On macOS or Linux, open a terminal in the project root and run:

```bash
./gradlew shadowJar
```

On Windows, run:

```bat
gradlew.bat shadowJar
```

After a successful build, the fat JAR is located at:

```text
build/libs/sine.jar
```

Run it from the project root using Java 25:

```bash
java -jar build/libs/sine.jar
```

The application uses a relative path for `data/sine.txt`, so running it from the project root keeps the data folder in the expected location.

## Publishing the JAR through a GitHub release

Do not commit `sine.jar` to Git. The generated `build/` directory is excluded by `.gitignore`.

To distribute a version of Sine:

1. Push the source-code commit and an appropriate version tag, such as `v0.1`, to your GitHub fork.
2. Open the fork on GitHub and go to **Releases**.
3. Select **Draft a new release** and choose or create the version tag.
4. Enter a release title and briefly describe the changes in that version.
5. Attach `build/libs/sine.jar` where GitHub says **Attach binaries by dropping them here or selecting them**.
6. Publish the release.

## Verification

Use Java 25 to run `./gradlew test checkstyleMain checkstyleTest`.
Checkstyle enforces the mechanical subset in `config/checkstyle/checkstyle.xml`;
review naming intent and documentation against the SE-EDU standard manually.
The CLI acceptance cases are in `test/ui-test-plan.md`.
