# Lobby

Lobby is a desktop task-management chatbot for keeping track of to-dos, deadlines, and events through simple text commands.

See the [Lobby User Guide](docs/README.md) for installation and instructions on using every command.

## Developer setup

### Prerequisites

- JDK 25
- A recent version of IntelliJ IDEA

### Opening the project in IntelliJ IDEA

1. Open IntelliJ IDEA. If another project is open, select **File** > **Close Project** first.
2. Select **Open**, choose this project folder, and accept the default options in any further prompts.
3. Configure the project to use **JDK 25** by following IntelliJ's [SDK setup guide](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk). Set the **Project language level** to **SDK default**.
4. Open `src/main/java/lobby/Launcher.java`, right-click in the editor, and select **Run 'Launcher.main()'**.

Keep `src/main/java` as the source folder because Gradle and IntelliJ use this standard project structure to locate the Java files.

## Building and testing

Run all automated tests and code-style checks with Java 25:

```shell
# Windows PowerShell
.\gradlew.bat check

# macOS or Linux
./gradlew check
```

Build the executable application JAR with:

```shell
# Windows PowerShell
.\gradlew.bat shadowJar

# macOS or Linux
./gradlew shadowJar
```

The resulting file is `build/libs/Lobby.jar`.

## Continuous integration

The [Java CI workflow](.github/workflows/gradle.yml) runs automatically for pushes and pull requests. It checks the project on Linux, macOS, and Windows using JDK 25. Each job runs `./gradlew check`, which compiles the code, runs the JUnit tests, and applies Checkstyle.

The workflow is based on the [SE-EDU Duke workflow](https://github.com/se-edu/duke/blob/full-template/.github/workflows/gradle.yml). For more information, see the [SE-EDU GitHub Actions guide](https://se-education.org/guides/tutorials/githubActions.html).

## Acknowledgements

Edrich Yu ([@ed3yuu](https://github.com/ed3yuu)) used OpenAI Codex to assist with Lobby's development, including code review and test verification.
